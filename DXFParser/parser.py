import argparse
import ezdxf
import re as regex
import os

WALLS_LAYER = "WALLS"
EXITS_LAYER = "EXITS"
GENERATORS_LAYER = "GENERATORS"
TARGETS_LAYER = "TARGETS"
SERVERS_LAYER = "SERVERS"


def is_rectangle(entity):
    if entity.dxftype() == 'POLYLINE':
        # In a rectangle of polyline there are 4 vertices + 1 closing vertex (that is the same as the first one)
        print(len(entity))
        return len(entity) == 5 and entity[0].dxf.location == entity[-1].dxf.location
    elif entity.dxftype() == 'LWPOLYLINE':
        return len(entity) == 4 and entity.dxf.flags == ezdxf.const.LWPOLYLINE_CLOSED
    else: return False

def get_rectangle_figure(entity):
    # If we know we are getting a rectangle, we can save just top left and bottom right vertices
    if entity.dxftype() == 'POLYLINE':
        return [entity[0].dxf.location[0], entity[0].dxf.location[1], entity[0].dxf.location[2],
                entity[2].dxf.location[0], entity[2].dxf.location[1], entity[2].dxf.location[2]]
    elif entity.dxftype() == 'LWPOLYLINE':
        first_vertex = None
        third_vertex = None
        for i, vertex in enumerate(entity.vertices()):
            if i == 0:
                first_vertex = [*vertex, 0]
            elif i == 2:
                third_vertex = [*vertex, 0]
        
        return [*first_vertex, *third_vertex]
    else: ValueError(f'Entity type {entity.dxftype()} is not supported as a rectangle.')

def get_figures(entity, layer_prefix):
    figures = []
    if entity.dxftype() == 'LINE':
        figures.append([entity.dxf.start[0], entity.dxf.start[1], entity.dxf.start[2],
                        entity.dxf.end[0], entity.dxf.end[1], entity.dxf.end[2]])
        
    elif entity.dxftype() == 'CIRCLE':
        figures.append([entity.dxf.center[0], entity.dxf.center[1],
                       entity.dxf.center[2], entity.dxf.radius])
        
    elif entity.dxftype() == 'POLYLINE':
        lines_qty = len(entity)
        if not entity.is_closed:
            lines_qty -= 1  # because the last vertex does not have to connect to the first one

        for i in range(lines_qty):
            current_vertex_location = entity[(i) % len(entity)].dxf.location
            next_vertex_location = entity[(i+1) % len(entity)].dxf.location
            figures.append([current_vertex_location[0], current_vertex_location[1], current_vertex_location[2],
                            next_vertex_location[0], next_vertex_location[1], next_vertex_location[2]])
            
    elif entity.dxftype() == 'LWPOLYLINE':
        iterator = entity.vertices()
        first_vertex_location = next(iterator)
        previous_vertex_location = first_vertex_location
        for current_vertex_location in iterator:
            figures.append([previous_vertex_location[0], previous_vertex_location[1], 0,
                            current_vertex_location[0], current_vertex_location[1], 0])
            previous_vertex_location = current_vertex_location
        
        if entity.dxf.flags == ezdxf.const.LWPOLYLINE_CLOSED:
            figures.append([previous_vertex_location[0], previous_vertex_location[1], 0,
                            first_vertex_location[0], first_vertex_location[1], 0])
            
    else:
        raise ValueError(
            f'Layer {layer_prefix} contains {entity.dxftype()} entities which is not supported.')

    return figures


def parse_entities(entities, layer_prefix, expected_types, name=None, figures_can_be_rectangles=False):
    figures = []
    for entity in entities:
        if entity.dxftype() not in expected_types:
            raise ValueError(
                f'Layer {layer_prefix} contains {entity.dxftype()} entities which is not in the expected: {expected_types}.')

        if figures_can_be_rectangles and is_rectangle(entity):
            new_figures = [get_rectangle_figure(entity)]
        else:
            new_figures = get_figures(entity, layer_prefix)

        for new_figure in new_figures:
            if name is not None:
                new_figure.insert(0, name)

            figures.append(new_figure)

    if len(figures) == 0:
        raise ValueError(f'Layer {WALLS_LAYER} is empty.')
    return figures


def write_to_file(file, array):
    for value in array:
        with_name = False
        if type(value[0]) is str:
            with_name = True
            file.write(f'{value[0]}, ')

        # round to 6 decimals to avoid minimal innacuracies from autocad
        DECIMALS = 6
        for i in range(with_name == True, len(value) - 1):
            file.write(f'{round(value[i], DECIMALS)}, ')

        file.write(f'{round(value[len(value)-1], DECIMALS)}\n')


def get_blocks_figures(msp, layer, expected_types, figures_can_be_rectangles=False):
    figures = []
    for block in msp.query('INSERT').filter(lambda block: regex.match(r"{}*".format(layer), block.dxfattribs()['layer'])):
        # TODO: QUE PASA SI EL BLOQUE ESTA EN MAS DE UN LAYER? O SI SU CONTENIDO ESTA EN MAS DE UN LAYER?
        figures += parse_entities(block.virtual_entities(), layer, expected_types,
                                  block.dxf.name, figures_can_be_rectangles)

    return figures


def get_walls(msp, expected_types):
    figures = parse_entities(msp.query().filter(lambda entity: regex.match(r"{}*".format(WALLS_LAYER), entity.dxf.layer)),
                             WALLS_LAYER,
                             expected_types=expected_types,
                             name=None,
                             figures_can_be_rectangles=False)

    return figures


def get_servers(msp):
    servers_map = {}
    # retrieve all blocks in the servers layer, grouping by name
    for server in msp.query('INSERT').filter(lambda block: regex.match(r"{}*".format(SERVERS_LAYER), block.dxfattribs()['layer'])):
        if server.dxf.name not in servers_map:
            servers_map[server.dxf.name] = []

        servers_map[server.dxf.name].append(server)

    figures = []
    # retrieve queue and server for each server group
    # assign an id to each instance so we can identify the server and its queue on the csv
    for key in servers_map:
        id = 0
        for server in servers_map[key]:
            id += 1
            for entity in server.virtual_entities():
                if entity.dxftype() == 'POLYLINE' or entity.dxftype() == 'LWPOLYLINE':
                    if is_rectangle(entity):
                        # its a server
                        figures.append(
                            [f'{key}_{id}_SERVER', *get_rectangle_figure(entity)])
                    else:
                        # its a queue
                        queue_id = 0
                        for line in get_figures(entity, SERVERS_LAYER):
                            figures.append(
                                [f'{key}_{id}_QUEUE{queue_id:03d}', *line])
                            queue_id += 1
                    
                elif entity.dxftype() == 'LINE':
                    # has to be a queue
                    figures.append([f'{key}_{id}_QUEUE000', entity.dxf.start[0], entity.dxf.start[1], entity.dxf.start[2],
                                    entity.dxf.end[0], entity.dxf.end[1], entity.dxf.end[2]])

    return figures


def parse_layer_and_write_to_file(msp, layer, expected_types, out_file_path, figures_can_be_rectangles=False):
    if layer == WALLS_LAYER:
        array = get_walls(msp, expected_types)
    elif layer == SERVERS_LAYER:
        array = get_servers(msp)
    else:
        array = get_blocks_figures(
            msp, layer, expected_types, figures_can_be_rectangles)

    file = open(out_file_path + layer + ".csv", "w")
    write_to_file(file, array)
    file.close()


def parse_dxf(in_file_path, out_path):
    print("Initializing parsing over the dxf file...")
    doc = ezdxf.readfile(in_file_path)
    msp = doc.modelspace()

    print("\tParsing walls...")
    parse_layer_and_write_to_file(
        msp, WALLS_LAYER, ['LINE', 'POLYLINE', 'LWPOLYLINE'], out_path)

    print("\tParsing exits...")
    parse_layer_and_write_to_file(
        msp, EXITS_LAYER, ['LINE', 'POLYLINE', 'LWPOLYLINE'], out_path)

    print("\tParsing generators...")
    parse_layer_and_write_to_file(msp, GENERATORS_LAYER, [
                                  'POLYLINE', 'LWPOLYLINE'], out_path, figures_can_be_rectangles=True)

    print("\tParsing targets...")
    parse_layer_and_write_to_file(
        msp, TARGETS_LAYER, ['POLYLINE', 'LWPOLYLINE', 'CIRCLE'], out_path, figures_can_be_rectangles=True)

    print("\tParsing servers...")
    parse_layer_and_write_to_file(msp, SERVERS_LAYER, [
                                  'LINE', 'POLYLINE', 'LWPOLYLINE'], out_path, figures_can_be_rectangles=True)

    print("Parsing of dxf file finished...")


EXAMPLE_DXF_PATH = "data/Plano prueba simulacion V05.02.dxf"
EXAMPLE2_DXF_PATH = "data/Plano SREC PB simulacion V01.dxf"
EXAMPLE_JSON_PATH = "data/parameters.json"

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Parse a .dxf file to a the .csv files necessary for the program.")

    parser.add_argument("-dxf",
                        help="Path to the .dxf file to be used by the program to define the environment of the simulation. \
This file has to follow the requirements indicated on the README. \
Defaults to: " + EXAMPLE2_DXF_PATH,
                        type=str, default=EXAMPLE2_DXF_PATH, required=False)

    parser.add_argument("-params",
                        help="Path to the .json file to be used by the program to define the behavior of the components of the simulation. \
This file requirements indicated on the README. \
Defaults to: " + EXAMPLE_JSON_PATH,
                        type=str, default=EXAMPLE_JSON_PATH, required=False)

    args = parser.parse_args()
    out_path = "tmp/simulation_input/"
    if not os.path.exists("tmp"):
        os.makedirs("tmp")
    if not os.path.exists(out_path):
        os.makedirs(out_path)
    parse_dxf(args.dxf, out_path)
