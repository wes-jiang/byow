package byow.Core;

import java.awt.*;
import java.io.Serializable;

public class Coordinate implements Serializable {
    private int x;
    private int y;

    public Coordinate(int xp, int yp) {
        x = xp;
        y = yp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        Coordinate otherW = (Coordinate) other;
        if (this.x == otherW.getX() && this.y == otherW.getY()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }
}
