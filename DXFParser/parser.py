import argparse
import ezdxf
import re as regex

WALLS_LAYER_PREFIX = "WALLS"
EXITS_LAYER_PREFIX = "EXITS"
GENERATORS_LAYER_PREFIX = "GENERATORS"
# TODO: ADD other layers

def get_layer_figures(msp, layer_prefix, expected_types, with_name=False, figures_are_rectangles=False):
    array = []
    for e in msp.query().filter(lambda e: regex.match(r"{}*".format(layer_prefix), e.dxf.layer)):
        new_figures = []

        if e.dxftype() not in expected_types:
            raise ValueError(f'Layer {layer_prefix} contains {e.dxftype()} entities which is not in the expected: {expected_types}.')

        # TODO: ADD other types of entities
        # TODO: should be another function 
        if e.dxftype() == 'LINE':
            new_figures.append([e.dxf.start[0], e.dxf.start[1], e.dxf.start[2],
                      e.dxf.end[0], e.dxf.end[1], e.dxf.end[2]])
        elif e.dxftype() == 'POLYLINE':
            if not figures_are_rectangles:
                lines_qty = len(e)

                if not e.is_closed:
                    lines_qty -= 1 # because the last vertex does not have to connect to the first one

                for i in range(lines_qty):
                    current_vertex_location = e[i].dxf.location
                    next_vertex_location = e[(i+1)%len(e)].dxf.location
                    new_figures.append([current_vertex_location[0], current_vertex_location[1], current_vertex_location[2],
                                next_vertex_location[0], next_vertex_location[1], next_vertex_location[2]])
            else:
                # in a rectangle there are 4 vertices + 1 closing vertex (that is the same as the first one)
                if e[0].dxf.location != e[-1].dxf.location or len(e) != 5: 
                    raise ValueError(f'Layer {layer_prefix} contains {e.dxftype()} entities which is not a rectangle, when it should be because of being in this layer.')

                new_figures.append([e[0].dxf.location[0], e[0].dxf.location[1], e[0].dxf.location[2],
                            e[2].dxf.location[0], e[2].dxf.location[1], e[2].dxf.location[2]])
        else:
            raise ValueError(f'Layer {layer_prefix} contains {e.dxftype()} entities which is not supported.')

        for new_figure in new_figures:
            if with_name:
                array.append([e.dxf.layer.split('_', 1)[1]].extend(new_figure))
            else:
                array.append(new_figure)


    if len(array) == 0:
        raise ValueError(f'Layer {layer_prefix} is empty.')

    return array

def write_to_file(file, array, with_name=False):
    DECIMALS = 6
    # round to 6 decimals to avoid minimal innacuracies from autocad
    if with_name:
        for value in array:
            file.write(f'{value[0]}, {round(value[1],DECIMALS)}, {round(value[2],DECIMALS)}, {round(value[3],DECIMALS)}, {round(value[4],DECIMALS)}, {round(value[5],DECIMALS)}, {round(value[6],DECIMALS)}\n')
    else:
        for value in array:
            file.write(f'{round(value[0],DECIMALS)}, {round(value[1],DECIMALS)}, {round(value[2],DECIMALS)}, {round(value[3],DECIMALS)}, {round(value[4],DECIMALS)}, {round(value[5],DECIMALS)}\n')

def get_layer_figures_and_write_to_file(msp, layer_prefix, expected_types, 
                                        out_file_path, with_name=False, figures_are_rectangles=False):
    array = get_layer_figures(msp, layer_prefix, expected_types, with_name, figures_are_rectangles)
    file = open(out_file_path + layer_prefix + ".csv", "w")
    write_to_file(file, array, with_name)
    file.close()

def parse_dxf(in_file_path, out_path):
    print("Initializing parsing over the dxf file...")
    doc = ezdxf.readfile(in_file_path)
    msp = doc.modelspace()

    print("\tParsing walls...")
    get_layer_figures_and_write_to_file(msp, WALLS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path)
    
    print("\tParsing exits...")
    get_layer_figures_and_write_to_file(msp, EXITS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path)

    print("\tParsing generators...")
    get_layer_figures_and_write_to_file(msp, GENERATORS_LAYER_PREFIX, ['LINE', 'POLYLINE'], out_path, 
                with_name=True, figures_are_rectangles=True)


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
