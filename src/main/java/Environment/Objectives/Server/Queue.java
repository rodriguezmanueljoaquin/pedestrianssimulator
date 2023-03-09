package Environment.Objectives.Server;

import Utils.Constants;
import Utils.Line;
import Utils.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Queue {
    private final List<QueueLine> queue;

    public Queue(List<Line> lines) {
        this.queue = new ArrayList<>();
        this.queue.addAll(
                lines.stream().map(QueueLine::new).collect(Collectors.toList())
        );
    }

    public Vector getSpotPosition(int spot) {
        if(spot < 0) return null;
        for (QueueLine line : this.queue) {
            if(line.getSegmentsQuantity() < spot) {
                // searched spot is not in this line
                spot -= line.getSegmentsQuantity();
            } else {
                return line.getSegmentPosition(spot);
            }
        }

        // spots capacity reached, return end of queue
        return this.queue.get(this.queue.size()-1).getX2();
    }

    private class QueueLine extends Line {
        private final int segmentsQuantity;

        public QueueLine(Vector x1, Vector x2) {
            super(x1, x2);
            this.segmentsQuantity = (int) (this.getX2().distance(this.getX1()) / Constants.SPACE_BETWEEN_AGENTS_IN_QUEUE);
        }

        public QueueLine(Line line) {
            super(line);
            this.segmentsQuantity = (int) (this.getX2().distance(this.getX1()) / Constants.SPACE_BETWEEN_AGENTS_IN_QUEUE);
        }

        public int getSegmentsQuantity() {
            return this.segmentsQuantity;
        }

        public Vector getSegmentPosition(int segmentIndex) {
            if (segmentIndex > this.segmentsQuantity)
                return this.getX2();
            if (segmentIndex < 0)
                return null;

            return this.getX1().add(this.getX2().substract(this.getX1()).scalarMultiply(1d / this.segmentsQuantity).scalarMultiply((double) segmentIndex));

        }
    }
}
