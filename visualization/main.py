import exportOvito
from simulationResults import read_input_files, SimulationResult
input_files_directory_path = "../results/"

def visualize_simulation_results():
    simulations_results = read_input_files(input_files_directory_path)
    exportOvito.exportOvito(simulations_results)

def visualize_graph_results():
    import matplotlib.pyplot as plt
    import networkx as nx

    G = nx.Graph()
    file = open(input_files_directory_path + "graph.txt" , 'r')
    line = file.readline()

    pos = dict()
    while line:
        data = line.split(";")[:-1]
        id = int(data[0])
        pos[id] = (float(data[1]),float(data[2]))
        for neighbor in list(map(int, data[3:])):
            G.add_edge(id, neighbor)

        line = file.readline()
    
    options = {
        "font_size": 10,
        "node_size": 200,
        "node_color": "white",
        "edgecolors": "black",
        "linewidths": 5,
        "width": 5,
    }
    nx.draw_networkx(G, pos, **options)

    # Set margins for the axes so that nodes aren't clipped
    ax = plt.gca()
    ax.margins(0.20)
    plt.axis("off")
    plt.show()

if __name__ == "__main__":
    # visualize_simulation_results()
    visualize_graph_results()
