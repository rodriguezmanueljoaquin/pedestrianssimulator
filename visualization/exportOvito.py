import math
import os
import numpy as np

ovitoFolderName = "ovito_input"

def exportOvito(simulation_result):
    #Si no existe la carpeta de archivos de Ovito, la creamos
    if(not os.path.exists(ovitoFolderName)):
        os.makedirs(ovitoFolderName)
    print('Generating ovito file for walls...')
    exportWalls(simulation_result.walls)
    print('Generating ovito file for particles...')
    exportParticles(simulation_result.particles_by_frame)
    print('Ovito files successfully generated')

def exportWalls(walls):
    file = open("{}/walls.xyz".format(ovitoFolderName), "w")
    walls_str = ""
    wall_particles_qty = 0
    PRECISION = 0.001
    for wall in walls:
        for i in np.arange(0, 1, PRECISION):
            #https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
            x = (1-i)*float(wall[0]) + i*float(wall[2])
            y = (1-i)*float(wall[1]) + i*float(wall[3])
            walls_str += "{} {}\n".format(x, y)
            wall_particles_qty += 1
    
    file.write("{}\ncomment\n".format(wall_particles_qty))
    file.write(walls_str)
    
    file.close()

def exportParticles(particles_by_frame):
    file = open("{}/particles.xyz".format(ovitoFolderName), "w")
    firstEmptyLinesAvoided = False
    for particle_frame in particles_by_frame:
        if(not firstEmptyLinesAvoided):
            if(len(particle_frame.particles) != 0):
                firstEmptyLinesAvoided = True
            else:
                continue
            
        file.write("{}\ncomment\n".format(len(particle_frame.particles)))
        for particle in particle_frame.particles:
            file.write("{} {} {} {} {} {} {}\n"
                .format(particle.id, particle.x, particle.y, particle.velx, particle.vely, particle.radius, particle.state.value))
    
    file.close()

