package Environment.Server;

import Utils.Rectangle;
import Utils.Vector;

import java.util.HashMap;
import java.util.Map;

//Antes era una inner clas, pero como el enum y el queuue tambien podian serlo, saque todas a esto
//De todas formas el default hace que solo sea accesibility en el paquete
class ServerPositionHandler {
    //Por ahora esta clase quedo con gusto a poco, pero mi idea es que aca hagamos la grilla para generar nuevas pos.
    //Que le permitamos construir posiciones estatica o dinamicamente
    //Y la totalidad de lo relacionado a las posiciones dentro del server, entonces.
    //El server solo deberia encargarse de cosas tipo, termino 1, empezo 1, terminaron N, etc.
    //No de como estan organizados internamente.
    //Todo: Hace falta un refactor, podemos mandar esta logica a Server o hacer el ServerHandler
    //dejando en Server la administracion entre server y queue
    //en retro tal vez me tiro por la segunda porque tal vez mas adelante tenemos que hacer cosas mas complejas
    //con la posicion, tipo crear una grilla y poner las posiciones ahi o cosas asi.
    private final Rectangle zone;
    private final Map<Integer, Vector> occupiedPositions;

    public ServerPositionHandler(Rectangle zone) {
        this.zone = zone;
        occupiedPositions = new HashMap<>();
    }

    public Vector setNewPosition(int id) {
        Vector newPosition;
        do {
            newPosition = zone.getRandomPointInside();
        } while (occupiedPositions.containsValue(newPosition));

        occupiedPositions.put(id, newPosition);
        return newPosition;
    }

    public Vector getOccupiedPosition(int id) {
        return occupiedPositions.get(id);
    }

    public Boolean isInServer(int id) {
        return occupiedPositions.get(id) != null;
    }

    public void removeAgent(int id) {
        occupiedPositions.remove(id);
    }
}