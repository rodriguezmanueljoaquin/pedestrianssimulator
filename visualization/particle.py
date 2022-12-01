from enum import Enum

# enum
class ParticleState(Enum):
    MOVING = 0,
    ATTENDING = 1,
    WAITING = 2

class Particle:    
    def __init__(self,id,x,y,velx,vely,radius,state):
        self.id = id
        self.x = x
        self.y = y
        self.velx = velx
        self.vely = vely
        self.radius = radius
        self.state = state

    def __eq__(self, other):
        return self.id == other.id

    def __str__(self):
        return "{Id="+str(self.id)+";position=("+str(self.x)+";"+str(self.y)+");velocity=("+str(self.velx)+";"+str(self.vely)+") }"

    def __repr__(self):
        return self.__str__()
    
    def __hash__(self):
        return self.id

    def getPosition(self):
        return (self.x,self.y)

    def getVelocity(self):
        return (self.velx,self.vely)
