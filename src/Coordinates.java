import java.util.Objects;

/**
 * Представляет пару координат (y, x)
 */
public class Coordinates {
    private int y;
    private int x;

    public Coordinates(int y, int x) {
        this.y = y;
        this.x = x;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;

        Coordinates otherCoordinates = (Coordinates) otherObject;

        return y == otherCoordinates.y &&
                x == otherCoordinates.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "(" + y + "; " + x + ")";
    }
}
