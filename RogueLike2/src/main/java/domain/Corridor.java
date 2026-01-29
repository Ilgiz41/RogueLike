package domain;

import java.util.ArrayList;
import java.util.List;

public class Corridor {
    private List<Point> corridorPoints;

    Corridor() {
        this.corridorPoints = new ArrayList<>();
    }

    public Corridor(Coordinate begin, Coordinate end, int
            rotation) {
        corridorPoints = new ArrayList<>();

        int x1 = begin.getX();
        int y1 = begin.getY();
        int x2 = end.getX();
        int y2 = end.getY();

        if (rotation == GameSession.LEFT || rotation == GameSession.RIGHT) {
            int halfX = (x1 + x2) / 2;
            if (x1 < x2) {
                for (int x = x1; x <= halfX; x++) {
                    corridorPoints.add(new Point(new Coordinate(x, y1), false));
                }
            } else {
                for (int x = x1; x >= halfX; x--) {
                    corridorPoints.add(new Point(new Coordinate(x, y1), false));
                }
            }

            if (y1 < y2) {
                for (int y = y1; y <= y2; y++) {
                    corridorPoints.add(new Point(new Coordinate(halfX, y), false));
                }
            } else {
                for (int y = y1; y >= y2; y--) {
                    corridorPoints.add(new Point(new Coordinate(halfX, y), false));
                }
            }

            if (x1 < x2) {
                for (int x = halfX + 1; x <= x2; x++) {
                    corridorPoints.add(new Point(new Coordinate(x, y2), false));
                }
            } else {
                for (int x = halfX - 1; x >= x2; x--) {
                    corridorPoints.add(new Point(new Coordinate(x, y2), false));
                }
            }
        } else if (rotation == GameSession.UP || rotation == GameSession.DOWN) {
            int halfY = (y1 + y2) / 2;
            if (y1 < y2) {
                for (int y = y1; y <= halfY; y++) {
                    corridorPoints.add(new Point(new Coordinate(x1, y), false));
                }
            } else {
                for (int y = y1; y >= halfY; y--) {
                    corridorPoints.add(new Point(new Coordinate(x1, y), false));
                }
            }
            if (x1 < x2) {
                for (int x = x1; x <= x2; x++) {
                    corridorPoints.add(new Point(new Coordinate(x, halfY), false));
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    corridorPoints.add(new Point(new Coordinate(x, halfY), false));
                }
            }

            if (y1 < y2) {
                for (int y = halfY + 1; y <= y2; y++) {
                    corridorPoints.add(new Point(new Coordinate(x2, y), false));
                }
            } else {
                for (int y = halfY - 1; y >= y2; y--) {
                    corridorPoints.add(new Point(new Coordinate(x2, y), false));
                }
            }
        }
    }

    //GET
    public List<Point> getPoint() {
        return corridorPoints;
    }

    //SET
    public void setPoint(List<Point> corridorPoints) {
        this.corridorPoints = corridorPoints;
    }
}