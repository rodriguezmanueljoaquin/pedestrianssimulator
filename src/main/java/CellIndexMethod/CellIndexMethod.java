package CellIndexMethod;

import Agent.Agent;
import Agent.AgentConstants;
import Environment.Wall;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CellIndexMethod {
    private static final int[][] POSSIBLE_NEIGHBOURS_CELLS =
            {
                    {-1, 1}, {0, 1}, {1, 1}, // top row
                    {-1, 0}, {0, 0}, {1, 0}, // middle row
                    {-1, -1}, {0, -1}, {1, -1}, // bottom row
            };
    private final double neighbourhoodRadius;
    private final double EXTRA_MARGIN_SIZE = 0.25; // Percentage that determines how much bigger the matrix will be from the rectangle defined by the walls
    private Cell[][] matrix;
    private Vector bottomLeft, topRight;
    private int M; // cells in each row and in each column (the matrix must be square)
    private double cellSize;

    public CellIndexMethod(List<Wall> walls, double neighbourhoodRadius, double agentsMaximumMostPossibleRadius) {
        this.neighbourhoodRadius = neighbourhoodRadius;
        initMatrix(walls, agentsMaximumMostPossibleRadius);
    }

    private static void updateMinPoints(Vector currentMin, Vector possibleNewMin) {
        //Added an extra 5
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

    public List<Agent> getAgentNeighbours(Agent agent) {
        int i = ((Double) ((this.bottomLeft.getX() + agent.getPosition().getX()) / this.cellSize)).intValue();
        int j = ((Double) ((this.bottomLeft.getY() + agent.getPosition().getY()) / this.cellSize)).intValue();

        List<Agent> neighbours = new ArrayList<>();

        List<Cell> neighbourhood = new ArrayList<>();
        for (int[] shift : POSSIBLE_NEIGHBOURS_CELLS) {
            int neighbour_i = i + shift[0];
            int neighbour_j = j + shift[1];
            if (!isOutOfBounds(neighbour_i, this.M) && !isOutOfBounds(neighbour_j, this.M)) {
                neighbourhood.add(this.matrix[neighbour_i][neighbour_j]);
            }
        }

        //Get neighbours in cells
        for (Cell cell : neighbourhood) {
            neighbours.addAll(cell.getAgents().stream()
                    .filter(other -> agent.distance(other) < this.neighbourhoodRadius && !agent.equals(other))
                    .collect(Collectors.toSet()));
        }

        return neighbours;
    }

    public void updateAgentsPosition(List<Agent> agents) {
        for (int i = 0; i < this.M; i++) {
            for (int j = 0; j < this.M; j++) {
                this.matrix[i][j].clear();
            }
        }

        for (Agent agent : agents) {
            int i = ((Double) (agent.getPosition().getX() / this.cellSize)).intValue();
            int j = ((Double) (agent.getPosition().getY() / this.cellSize)).intValue();
            if (i < this.M && j < this.M)
                this.matrix[i][j].addAgent(agent);
            else
                System.out.println(agent.getPosition().toString());
        }
    }

    private boolean isOutOfBounds(int position, int max) {
        return position < 0 || position >= max;
    }

    private void initMatrix(List<Wall> walls, double agentsMaximumMostPossibleRadius) {
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

        Vector matrixSize = new Vector(this.topRight.getX() - this.bottomLeft.getX(), this.topRight.getY() - this.bottomLeft.getY());
        // Apply the extra margin to the dots, in order to give some extra space in case agents go outside the rectangle defined by the walls
        this.bottomLeft = this.bottomLeft.substract(matrixSize.scalarMultiply(EXTRA_MARGIN_SIZE / 2));
        this.topRight = this.topRight.add(matrixSize.scalarMultiply(EXTRA_MARGIN_SIZE / 2));
        //recalculate to consider margin
        matrixSize = matrixSize.add(matrixSize.scalarMultiply(EXTRA_MARGIN_SIZE));

        // use the highest dimension to make the matrix square
        if (matrixSize.getY() > matrixSize.getX()) {
            this.M = (int) Math.ceil(matrixSize.getY() / (this.neighbourhoodRadius + 2 * agentsMaximumMostPossibleRadius));
            this.cellSize = matrixSize.getY() / this.M;
        } else {
            this.M = (int) Math.ceil(matrixSize.getX() / (this.neighbourhoodRadius + 2 * agentsMaximumMostPossibleRadius));
            this.cellSize = matrixSize.getX() / this.M;
        }

        this.matrix = new Cell[this.M][this.M];
        for (int i = 0; i < this.M; i++) {
            for (int j = 0; j < this.M; j++) {
                this.matrix[i][j] = new Cell();
            }
        }
    }
}