
import os
ovitoFolderName = "ovito_input"

def exportOvito(simulation_result):
    #Si no existe la carpeta de archivos de Ovito, la creamos
    if(not os.path.exists(ovitoFolderName)):
        os.makedirs(ovitoFolderName)
    print('Generating ovito file. . .')
    exportParticles(simulation_result.particles_by_frame,simulation_result.simulation_deltaT,simulation_result.seconds_to_departure, simulation_result.sun_id, simulation_result.sun_position[0],simulation_result.sun_position[1], simulation_result.sun_radius)
    print('Ovito file successfully generated')


def exportParticles(particles_by_frame):
    file = open("{}/particles.xyz".format(ovitoFolderName), "w")
    for particle_frame in particles_by_frame:
        n = len(particle_frame.particles)
        file.write("{}\ncomment\n".format(n + 1))
        for particle in particle_frame.particles:
            radius = particle.radius

            file.write("{} {} {} {} {} {}\n".format(particle.id, particle.x, particle.y, particle.velx, particle.vely, radius))
    
    file.close()

