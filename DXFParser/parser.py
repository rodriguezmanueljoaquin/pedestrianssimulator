import ezdxf

GENERATORS_LAYER_NAME = "PEATONES"
WALLS_LAYER_NAME = "PAREDES"
EXITS_LAYER_NAME = "SALIDAS"
# TODO: ADD 4th layer


def verify_required_layers(doc):
    layers_list = [WALLS_LAYER_NAME, GENERATORS_LAYER_NAME, EXITS_LAYER_NAME]

    model_layers = []
    error_layers = []
    for layer in doc.layers:
        if layer.dxf.name != '0':
            model_layers.append(layer.dxf.name)
    for layer in layers_list:
        if not layer in model_layers:
            error_layers.append(layer)
    
    return error_layers


def get_all_layer_lines(msp, layer, min_values):
    array = []
    for e in msp.query('LINE[layer=="{}"]'.format(layer)):
        min_values[0] = min(min(min_values[0], e.dxf.start[0]), e.dxf.end[0])
        min_values[1] = min(min(min_values[1], e.dxf.start[1]), e.dxf.end[1])
        min_values[2] = min(min(min_values[2], e.dxf.start[2]), e.dxf.end[2])
        array.append([e.dxf.start[0], e.dxf.start[1], e.dxf.start[2],
                      e.dxf.end[0], e.dxf.end[1], e.dxf.end[2]])
    return array


def normalize_values(min_values, array):
    min_x_abs = abs(min_values[0])
    min_y_abs = abs(min_values[1])
    min_z_abs = abs(min_values[2])
    for values in array:
        values[0] = round(values[0] + min_x_abs, 2)
        values[1] = round(values[1] + min_y_abs, 2)
        values[2] = round(values[2] + min_z_abs, 2)
        values[3] = round(values[3] + min_x_abs, 2)
        values[4] = round(values[4] + min_y_abs, 2)
        values[5] = round(values[5] + min_z_abs, 2)

def write_to_file(file, array):
    for value in array:
        file.write('{}, {}, {}, {}, {}, {}\n'.format(
            value[0], value[1], value[2], value[3], value[4], value[5]))

def parse_dxf(in_file_path, out_path):
    doc = ezdxf.readfile(in_file_path)
    error_layers = verify_required_layers(doc)

    if len(error_layers) != 0:
        return ValueError("On layers: {}".format(error_layers))

    msp = doc.modelspace()
    min_values = [0, 0, 0]

    walls_array = get_all_layer_lines(msp, WALLS_LAYER_NAME, min_values)
    pedestrians_array = get_all_layer_lines(
        msp, GENERATORS_LAYER_NAME, min_values)
    exits_array = get_all_layer_lines(msp, EXITS_LAYER_NAME, min_values)

    normalize_values(min_values, walls_array)
    normalize_values(min_values, pedestrians_array)
    normalize_values(min_values, exits_array)

    walls_file = open(out_path + WALLS_LAYER_NAME + ".csv", "w")
    write_to_file(walls_file, walls_array)
    walls_file.close()
    
    generators_file = open(out_path + GENERATORS_LAYER_NAME + ".csv", "w")
    write_to_file(generators_file, pedestrians_array)
    generators_file.close()
    
    exits_file = open(out_path + EXITS_LAYER_NAME + ".csv", "w")
    write_to_file(exits_file, exits_array)
    exits_file.close()


if __name__ == '__main__':
    # parse_dxf("DXFParser/DXFExamples/market.dxf", "input/") TODO: NO FUNCIONA! pedir a alguien que maneje dxf
    # parse_dxf("DXFParser/DXFExamples/Plano1.dxf", "input/")
    parse_dxf("DXFParser/DXFExamples/Plano2.dxf", "input/")
