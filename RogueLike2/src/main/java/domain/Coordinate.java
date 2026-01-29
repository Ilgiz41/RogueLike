package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Coordinate {

    private int x;
    private int y;

    public Coordinate() {
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        Coordinate other = (Coordinate) object;
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        return x == other.x && y == other.y;
    }

    //GET

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //SET

    @JsonIgnore
    public void setNewPlusOld(int x, int y) {
        this.x += x;
        this.y += y;
    }

    @JsonIgnore
    public void setNew(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


}
