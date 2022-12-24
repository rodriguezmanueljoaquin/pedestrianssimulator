package Utils;

public class Line {
    private final Vector x1, x2;
    /*
                x2
              /
             /
            /
           /
         x1
     */
    private final int segmentsQuantity;

    public Line(Vector x1, Vector x2) {
        // check dots are as expected, check that one axis is shared and x1 is below x2
        if (x1.getX() > x2.getX() || x1.getY() > x2.getY())
            throw new IllegalArgumentException("Line arguments are not as expected.");

        this.x1 = x1;
        this.x2 = x2;
        this.segmentsQuantity = (int) (this.x2.distance(this.x1)/Constants.SPACE_IN_QUEUE);
    }

    public int getSegmentsQuantity(double separation) {
        return this.segmentsQuantity;
    }

    public Vector getSegmentPosition(int segmentIndex) {
        if(segmentIndex > this.segmentsQuantity)
            return x2;

        return this.x1.scalarMultiply(Constants.SPACE_IN_QUEUE * (this.segmentsQuantity - segmentIndex))
                .add(x2.scalarMultiply(Constants.SPACE_IN_QUEUE * (segmentIndex)));
    }
}
