/**
 * Наследник Edge, дополнительно хранит вес связи и ид узлов
 */

public class WeightedEdge extends Edge {
    private int firstVertexId;
    private int secondVertexId;
    private int weight;

    public WeightedEdge(int firstVertexId, int secondVertexId, int weight, Coordinates firstVertexCoordinates, Coordinates secondVertexCoordinates) {
        super(firstVertexCoordinates,secondVertexCoordinates);
        this.firstVertexId = firstVertexId;
        this.secondVertexId = secondVertexId;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public int getFirstVertexId() {
        return firstVertexId;
    }

    public int getSecondVertexId() {
        return secondVertexId;
    }
}
