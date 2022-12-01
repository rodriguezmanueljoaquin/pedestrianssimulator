from particle import Particle, ParticleState
import os
import copy
import numpy as np


class SimulationResult:
    def __init__(self, walls):
        self.particles_by_frame = list()
        self.walls = walls

    def __str__(self):
        return "SimulationResult: [particles_by_frame={}]".format(
            self.particles_by_frame
        )

    def __repr__(self):
        return self.__str__()

class ParticlesFrame:
    def __init__(self, time):
        self.particles = list()
        self.time = time

def read_input_files(input_file_directory_path):
    
    print('Reading static file...')
    simulation_result = __read_static_input_file(input_file_directory_path+"static.txt")
    print('Static file successfully read')

    print('Reading dynamic file...')
    __read_dynamic_input_file(input_file_directory_path+"dynamic.txt",simulation_result)
    print('Dynamic file successfully read')

    return simulation_result

def __read_static_input_file(static_input_file_path):
    file = open(static_input_file_path , 'r')
    line = file.readline()

    walls_qty = int(line.strip())
    walls = list()
    for i in range(0, walls_qty):
        line = file.readline()
        wall_data = line.split(";")
        walls.append((wall_data[0],wall_data[1],wall_data[2],wall_data[3]))

    line = file.readline()

    if line: raise Exception("Invalid static input file, there are more arguments than expected")
    file.close()

    return SimulationResult(walls)
    
def __read_dynamic_input_file(dynamic_input_file_path, simulation_result):
    read = True
    file = open(dynamic_input_file_path , 'r')
    while read:
        line = file.readline()
        if not line:
            read = False
        else:
            headers = line.split(";")
            time = float(headers[0])
            particles_qty = int(headers[1])
            particles_frame = ParticlesFrame(time)
            for _ in range(0, particles_qty):
                line = file.readline()
                particles_frame.particles.append(__get_particle_data(line))

            simulation_result.particles_by_frame.append(particles_frame)

            if(not file.readline()):
                # there is an enter after each frame
                raise Exception("Invalid dynamic input file, there are more arguments than expected")

    file.close()
    return simulation_result

def __get_particle_data(line):
    data = line.split(";")
    id = int(data[0])
    x = float(data[1])
    y = float(data[2])
    vx = float(data[3])
    vy = float(data[4])
    radius = float(data[5])
    state = ParticleState(int(data[6]))
    return Particle(id,x,y,vx,vy,radius,state)
