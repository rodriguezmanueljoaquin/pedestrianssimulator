package Utils;

public class Line {
    private final Vector x1, x2;
    // x1 is the start
    private final int segmentsQuantity;

    public Line(Vector x1, Vector x2) {
        this.x1 = x1;
        this.x2 = x2;
        this.segmentsQuantity = (int) (this.x2.distance(this.x1)/Constants.SPACE_BETWEEN_AGENTS_IN_QUEUE);
    }

    public int getSegmentsQuantity() {
        return this.segmentsQuantity;
    }

    public Vector getSegmentPosition(int segmentIndex) {
        if(segmentIndex > this.segmentsQuantity)
            return x2;
        if(segmentIndex < 0)
            return x1;

        return this.x1.add(this.x2.substract(this.x1).scalarMultiply(1d/this.segmentsQuantity).scalarMultiply((double) segmentIndex));
    }
}
