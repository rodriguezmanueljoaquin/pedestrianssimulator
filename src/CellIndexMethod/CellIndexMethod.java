package CellIndexMethod;

import Agent.Agent;
import Environment.Wall;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;

public class CellIndexMethod {
    private List<List<List<Agent>>> matrix;
    private Vector bottomLeft, topRight;
    private static final double MATRIX_DIM = 5; // define dynamically for optimum results? TODO: READ PAPER ABOUT CIM

    public CellIndexMethod(List<Wall> walls) {
        initMatrix(walls);
    }

    public List<Agent> getAgentNeighbours(Agent agent) {
        double cellLength = (this.topRight.getX() - this.bottomLeft.getX())/MATRIX_DIM;
        double cellHeight = (this.topRight.getY() - this.bottomLeft.getY())/MATRIX_DIM;
        int i = ((Double) ((this.bottomLeft.getX() + agent.getPosition().getX())/cellLength)).intValue();
        int j = ((Double) ((this.bottomLeft.getY() + agent.getPosition().getY())/cellHeight)).intValue();

        List<Agent> neighbours = new ArrayList<>();

        // ... TODO ...

        return neighbours;
    }

    public void updateAgentsPosition(List<Agent> agents) {
        for (int i = 0; i < this.matrix.size(); i++) {
            for (int j = 0; j < this.matrix.get(i).size(); j++) {
                this.matrix.get(i).get(j).clear();
            }
        }

        double cellLength = (this.topRight.getX() - this.bottomLeft.getX())/MATRIX_DIM;
        double cellHeight = (this.topRight.getY() - this.bottomLeft.getY())/MATRIX_DIM;
        for (Agent agent : agents) {
            int i = ((Double) ((this.bottomLeft.getX() + agent.getPosition().getX())/cellLength)).intValue();
            int j = ((Double) ((this.bottomLeft.getY() + agent.getPosition().getY())/cellHeight)).intValue();
            this.matrix.get(i).get(j).add(agent);
        }
    }

    private static void updateMinPoints(Vector currentMin, Vector possibleNewMin) {
        if(currentMin.getX() > possibleNewMin.getX())
            currentMin.setX(possibleNewMin.getX());

        if(currentMin.getY() > possibleNewMin.getY())
            currentMin.setY(possibleNewMin.getY());
    }

    private static void updateMaxPoints(Vector currentMax, Vector possibleNewMax) {
        if(currentMax.getX() < possibleNewMax.getX())
            currentMax.setX(possibleNewMax.getX());

        if(currentMax.getY() < possibleNewMax.getY())
            currentMax.setY(possibleNewMax.getY());
    }

    private void initMatrix(List<Wall> walls) {
        // generate matrix from a rectangle that has all walls inside

        // find the dots that define this rectangle
        this.bottomLeft = new Vector(Double.MAX_VALUE,Double.MAX_VALUE);
        this.topRight = new Vector(Double.MIN_VALUE,Double.MIN_VALUE);
        for (Wall wall : walls) {
            updateMinPoints(this.bottomLeft, wall.getA());
            updateMinPoints(this.bottomLeft, wall.getB());

            updateMaxPoints(this.topRight, wall.getA());
            updateMaxPoints(this.topRight, wall.getB());
        }

        double matrixLength = this.topRight.getX() - this.bottomLeft.getX();
        double matrixHeight = this.topRight.getY() - this.bottomLeft.getY();


        for (double i = this.bottomLeft.getX(); i < this.topRight.getX(); i += matrixLength/ MATRIX_DIM) {
            List<List<Agent>> column = new ArrayList<>();

            for (double j = this.bottomLeft.getY(); j < this.topRight.getY(); j += matrixHeight/ MATRIX_DIM) {
                List<Agent> cell = new ArrayList<>(); // i,j cell
                column.add(cell);
            }
            matrix.add(column);
        }
    }

}
