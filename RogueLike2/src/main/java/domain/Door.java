package domain;

import java.util.ArrayList;
import java.util.List;

public class Door {
    private List<Point> point = new ArrayList<>();

    Door() {
    }

    public Door(List<Point> point) {
        this.point = point;
    }

    public List<Point> getPoint() {
        return point;
    }

    public void setPoint(List<Point> point) {
        this.point = point;
    }

}
