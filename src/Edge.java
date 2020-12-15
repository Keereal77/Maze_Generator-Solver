/**
 * Представляет собой связь двух узлов, внутри хранит их координаты
 */
public class Edge {

    private final Coordinates firstVertexCoordinates;
    private final Coordinates secondVertexCoordinates;

    public Edge(Coordinates firstVertexCoordinates, Coordinates secondVertexCoordinates) {
        this.firstVertexCoordinates = firstVertexCoordinates;
        this.secondVertexCoordinates = secondVertexCoordinates;
    }

    public Coordinates getFirstVertexCoordinates() {
        return firstVertexCoordinates;
    }

    public Coordinates getSecondVertexCoordinates() {
        return secondVertexCoordinates;
    }

    public boolean contains(Coordinates coordinates) {
        return firstVertexCoordinates.equals(coordinates) || secondVertexCoordinates.equals(coordinates);
    }

    /**
     * Принимает на вход координаты узла и возвращает координаты второго или null, если не содержит координаты переданного
     * @param coordinates координаты одного из узлов
     * @return координаты другого или null
     */
    public Coordinates getOtherVertexCoordinatesOrNull(Coordinates coordinates) {
        return firstVertexCoordinates.equals(coordinates) ? secondVertexCoordinates :
                secondVertexCoordinates.equals(coordinates) ? firstVertexCoordinates : null;
    }

    @Override
    public String toString() {
        return "Edge{" +
                firstVertexCoordinates + " " + secondVertexCoordinates +
                '}';
    }
}
