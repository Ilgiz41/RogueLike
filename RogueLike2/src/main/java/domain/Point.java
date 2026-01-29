package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Point {
    private boolean visibility;
    private Coordinate coordinate;

    @JsonCreator
    public Point(@JsonProperty("coordinate") Coordinate coordinate,
                 @JsonProperty("visibility") boolean visibility) {
        this.coordinate = coordinate;
        setVisibility(visibility);
    }

    public boolean getVisibility() {
        return this.visibility;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}
