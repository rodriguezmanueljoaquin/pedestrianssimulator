import argparse
import ezdxf
import re as regex

WALLS_LAYER_PREFIX = "WALLS"
EXITS_LAYER_PREFIX = "EXITS"
GENERATORS_LAYER_PREFIX = "GENERATORS"
TARGETS_LAYER_PREFIX = "TARGETS"
SERVERS_LAYER_PREFIX = "SERVERS"

def get_rectangle_figure(e, layer_prefix):
    if e.dxftype() != 'POLYLINE' or len(e) != 5 or e[0].dxf.location != e[-1].dxf.location:
        # If we know we are getting a rectangle, we can save just top left and bottom right vertices
        # in a rectangle there are 4 vertices + 1 closing vertex (that is the same as the first one)
        raise ValueError(f'Layer {layer_prefix} contains {e.dxftype()} entities which is not a rectangle, when it should be because of being in this layer.')

    return [e[0].dxf.location[0], e[0].dxf.location[1], e[0].dxf.location[2],
                        e[2].dxf.location[0], e[2].dxf.location[1], e[2].dxf.location[2]]

def get_figures(e, layer_prefix):
    figures = []
    if e.dxftype() == 'LINE':
        figures.append([e.dxf.start[0], e.dxf.start[1], e.dxf.start[2],
                        e.dxf.end[0], e.dxf.end[1], e.dxf.end[2]])
    elif e.dxftype() == 'CIRCLE':
        figures.append([e.dxf.center[0], e.dxf.center[1], e.dxf.center[2], e.dxf.radius])
    elif e.dxftype() == 'POLYLINE':
        lines_qty = len(e)

        if not e.is_closed:
            lines_qty -= 1 # because the last vertex does not have to connect to the first one

        for i in range(lines_qty):
            current_vertex_location = e[i].dxf.location
            next_vertex_location = e[(i+1)%len(e)].dxf.location
            figures.append([current_vertex_location[0], current_vertex_location[1], current_vertex_location[2],
                        next_vertex_location[0], next_vertex_location[1], next_vertex_location[2]])
    else:
        raise ValueError(f'Layer {layer_prefix} contains {e.dxftype()} entities which is not supported.')

    return figures

def get_server_layer_figures(msp):
    figures = []
    for e in msp.query().filter(lambda e: regex.match(r"{}*".format(SERVERS_LAYER_PREFIX), e.dxf.layer)):
        # TODO: ADD SUPPORT FOR POLYLINE QUEUES!
        if e.dxftype() == 'POLYLINE':
            figures.append([e.dxf.layer.split('_', 1)[1] + "_SERVER", *get_rectangle_figure(e, SERVERS_LAYER_PREFIX)])
        elif e.dxftype() == 'LINE':
            figures.append([e.dxf.layer.split('_', 1)[1] + "_QUEUE", e.dxf.start[0], e.dxf.start[1], e.dxf.start[2],
                                                                    e.dxf.end[0], e.dxf.end[1], e.dxf.end[2]])

    return figures
            

def get_layer_figures(msp, layer_prefix, expected_types, with_name=False, figures_are_rectangles=False):
    figures = []
    for e in msp.query().filter(lambda e: regex.match(r"{}*".format(layer_prefix), e.dxf.layer)):

        if e.dxftype() not in expected_types:
            raise ValueError(f'Layer {layer_prefix} contains {e.dxftype()} entities which is not in the expected: {expected_types}.')

        if figures_are_rectangles:
            new_figures = [get_rectangle_figure(e, layer_prefix)]
        else:
            new_figures = get_figures(e, layer_prefix)

        for new_figure in new_figures:
            if with_name:
                new_figure.insert(0, e.dxf.layer.split('_', 1)[1])

            figures.append(new_figure)


    if len(figures) == 0:
        raise ValueError(f'Layer {layer_prefix} is empty.')

    return figures

def write_to_file(file, array, with_name=False):
    for value in array:
        print(value)
        if with_name:
            file.write(f'{value[0]}, ')

        # round to 6 decimals to avoid minimal innacuracies from autocad
        DECIMALS = 6
        for i in range(with_name == True, len(value) -1):
            file.write(f'{round(value[i], DECIMALS)}, ')

        file.write(f'{round(value[len(value)-1], DECIMALS)}\n')

def parse_layer_figures_and_write_to_file(msp, layer_prefix, expected_types, 
                                        out_file_path, with_name=False, figures_are_rectangles=False):
    if layer_prefix == SERVERS_LAYER_PREFIX:
        array = get_server_layer_figures(msp)
    else:
        array = get_layer_figures(msp, layer_prefix, expected_types, with_name, figures_are_rectangles)
    file = open(out_file_path + layer_prefix + ".csv", "w")
    write_to_file(file, array, with_name)
    file.close()

def parse_dxf(in_file_path, out_path):
    print("Initializing parsing over the dxf file...")
    doc = ezdxf.readfile(in_file_path)
    msp = doc.modelspace()

    print("\tParsing walls...")
    parse_layer_figures_and_write_to_file(msp, WALLS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path)
    
    print("\tParsing exits...")
    parse_layer_figures_and_write_to_file(msp, EXITS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path)

    print("\tParsing generators...")
    parse_layer_figures_and_write_to_file(msp, GENERATORS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path, 
                with_name=True, figures_are_rectangles=True)

    print("\tParsing targets...")
    parse_layer_figures_and_write_to_file(msp, TARGETS_LAYER_PREFIX, ['CIRCLE'], out_path, 
                with_name=True)

    print("\tParsing servers...")
    parse_layer_figures_and_write_to_file(msp, SERVERS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path, 
                with_name=True, figures_are_rectangles=True) 

    print("Parsing of dxf file finished...")


EXAMPLE_PATH = "DXFParser/DXFExamples/Plano_prueba_simulacion_V02.dxf"

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Parse a .dxf file to a the .csv files necessary for the program.")
    parser.add_argument("-dxf", 
    help="Path to the .dxf file to be used by the programs. \
This file has to follow the five layers requirements of this program indicated on the README. \
Defaults to: " + EXAMPLE_PATH, 
    type=str, default=EXAMPLE_PATH, required=False)
    args = parser.parse_args()

    parse_dxf(args.dxf, "input/")
