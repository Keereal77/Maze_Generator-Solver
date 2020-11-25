public class MazeGenerator {

    //TODO: Maze createMaze(int rowsCount, int columnCount)

    //TODO: Pair<Integer, Integer> getCoordinateByVectorId(int id)

    //TODO: int[][] connectVectors(int[][] mazeMatrix)

    //TODO: int[][] getSpanningTree(int[][] graphMatrix)

    public static void fillMaze(int[][] mazeMatrix) {
        for (int i = 0; i < mazeMatrix.length; i++) {
            for (int j = 0; j < mazeMatrix[0].length; j++) {
                mazeMatrix[i][j] = 1;
            }
        }
    }
}
