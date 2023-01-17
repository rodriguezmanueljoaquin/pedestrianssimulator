package CellIndexMethod;

import Agent.Agent;
import Agent.AgentConstants;
import Environment.Wall;
import Utils.Constants;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
/*
las posiciones y radios de
N partículas y los parámetros N, L, M y rc (ver punto 5). Las N partículas deben ser generadas en
forma aleatoria dentro del área de lado L

   ALGORITMO EN PAPEL;
        1.Recibo particulas.
        2.Por cada celda cargo las que entran en esa celda, cada celda es una lista de particulas. Una par
ticula puede estar en mas de una celda.
        3.Hago una martiz de NxN de ceros, donde hay una particula adyacente a otro le pongo un 1. Esta ma
triz no representa la matriz de celdas, es solo
            una celda de ids de particulas.
        4. Recorro la matriz y muestro el output pedido.

OUTPUT: [id de la partícula "i" id's de las partículas cuya distancia borde-borde es menos de rc].ESTO PAR
A CADA PARTICULA.


    RADIUSC ES LA DISTANCIA ENTRE PARTICULAS MAX A CONSIDERAR
 */
public class CellIndexMethod {
    private Cell[][] matrix;
    private Vector bottomLeft, topRight;
    private int matrixCol, matrixRow; // define dynamically for optimum results? TODO: READ PAPER ABOUT CIM
    private double radiusC = 3.0;

    public CellIndexMethod(List<Wall> walls) {
        initMatrix(walls);
    }


    public List<Agent> getAgentNeighbours(Agent agent) {
        double cellLength = (this.topRight.getX() - this.bottomLeft.getX()) / matrixCol;
        double cellHeight = (this.topRight.getY() - this.bottomLeft.getY()) / matrixRow;
        int i = ((Double) ((this.bottomLeft.getX() + agent.getPosition().getX()) / cellLength)).intValue();
        int j = ((Double) ((this.bottomLeft.getY() + agent.getPosition().getY()) / cellHeight)).intValue();

        List<Agent> neighbours = new ArrayList<>();

        // ... TODO ...

        return neighbours;
    }

    public void updateAgentsPosition(List<Agent> agents) {
        for (int i = 0; i < matrixRow; i++) {
            for (int j = 0; j < matrixCol; j++) {
                this.matrix[i][j].clear();
            }
        }

        double cellLength = (this.topRight.getX() - this.bottomLeft.getX()) / matrixCol;
        double cellHeight = (this.topRight.getY() - this.bottomLeft.getY()) / matrixRow;


        //Esto de sumarle el bottomLeft estara bien? ellos ya estan referenciados en el plano
        for (Agent agent : agents) {
            int i = ((Double) ((this.bottomLeft.getX() + agent.getPosition().getX()) / cellLength)).intValue();
            int j = ((Double) ((this.bottomLeft.getY() + agent.getPosition().getY()) / cellHeight)).intValue();
            this.matrix[i][j].addAgent(agent);
        }
    }

    private static void updateMinPoints(Vector currentMin, Vector possibleNewMin) {
        if (currentMin.getX() > possibleNewMin.getX())
            currentMin.setX(possibleNewMin.getX());

        if (currentMin.getY() > possibleNewMin.getY())
            currentMin.setY(possibleNewMin.getY());
    }

    private static void updateMaxPoints(Vector currentMax, Vector possibleNewMax) {
        if (currentMax.getX() < possibleNewMax.getX())
            currentMax.setX(possibleNewMax.getX());

        if (currentMax.getY() < possibleNewMax.getY())
            currentMax.setY(possibleNewMax.getY());
    }


    private void initMatrix(List<Wall> walls) {
        //Tuve que cambiar, el MATRIX_DIM pq ahora tenemos el problema de que las cells no son cuadradas
        //Calculamos una cantidad para las filas y otra para las columnas
        //El paper dice que L/(Rc + 2*maxRadius) > M
        //El primer M que cumpla eso es el optimo


        // generate matrix from a rectangle that has all walls inside

        // find the dots that define this rectangle
        this.bottomLeft = new Vector(Double.MAX_VALUE, Double.MAX_VALUE);
        this.topRight = new Vector(Double.MIN_VALUE, Double.MIN_VALUE);
        for (Wall wall : walls) {
            updateMinPoints(this.bottomLeft, wall.getA());
            updateMinPoints(this.bottomLeft, wall.getB());

            updateMaxPoints(this.topRight, wall.getA());
            updateMaxPoints(this.topRight, wall.getB());
        }

        double matrixLength = this.topRight.getX() - this.bottomLeft.getX();
        double matrixHeight = this.topRight.getY() - this.bottomLeft.getY();

        matrixCol = (int) Math.ceil(matrixLength / (radiusC + 2 * AgentConstants.MAX_RADIUS));
        matrixRow = (int) Math.ceil(matrixHeight / (radiusC + 2 * AgentConstants.MAX_RADIUS));

        matrix = new Cell[matrixRow][matrixCol];
        for (int i = 0; i < matrixRow; i++) {
            for (int j = 0; j < matrixCol; j++) {
                matrix[i][j] = new Cell(null);
            }
        }
    }
}