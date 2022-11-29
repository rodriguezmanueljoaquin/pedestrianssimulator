import exportOvito
from simulationResults import read_input_files, SimulationResult


if __name__ == "__main__":
    input_files_directory_path = "../results/"
    simulations_results = read_input_files(input_files_directory_path)

    exportOvito.exportOvito(simulations_results)
