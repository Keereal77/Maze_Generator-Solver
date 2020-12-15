/**
 * Преставляет собой лабиринт, основу которого представляет двумерный массив
 */
public class Maze {

    private int[][] mazeMatrix;

    final public static int WALL = 1;
    final public static int PASS = 0;
    final public static int PATH = -1;


    /**
     * Генерируется лабиринт с заданными размерами
     * @param rowsQuantity количество рядов
     * @param columnQuantity количество столбцов
     */
    public Maze(int rowsQuantity, int columnQuantity) {
        mazeMatrix = MazeGenerator.createMaze(rowsQuantity, columnQuantity);
    }

    public Maze(int[][] mazeMatrix) {
        this.mazeMatrix = mazeMatrix;
    }

    /**
     * Выводит лабиринт на консоль, наглядно представляя внутреннюю матрицу и обрамляя её стенами вокруг
     */
    public void printMaze() {

        for (int i = 0; i < mazeMatrix[0].length + 2; i++) System.out.print("\u2588\u2588");
        System.out.println();

        for (int i = 0; i < mazeMatrix.length; i++) {

            if (i == 0) System.out.print("  ");
            else System.out.print("\u2588\u2588");

            for (int j = 0; j < mazeMatrix[0].length; j++) {
                int value = mazeMatrix[i][j];
                if (value == PASS) System.out.print("  ");
                else System.out.print("\u2588\u2588");
            }

            if (i == mazeMatrix.length - 1) System.out.print("  \n");
            else System.out.print("\u2588\u2588\n");
        }

        for (int i = 0; i < mazeMatrix[0].length + 2; i++) System.out.print("\u2588\u2588");
        System.out.println();
    }

    /**
     * Выводит матрицу на консоль с отображением переданного пути
     * @param path
     */
    public void printMazeWithPath(Path path) {
        int[][] mazeMatrixWithPath = this.mazeMatrix.clone();

        while (path.hasNext()) {
            Coordinates step = path.nextStep();
            int y = step.getY();
            int x = step.getX();

            if (mazeMatrixWithPath[y][x] != PASS) {
                System.out.println("Incorect path!");
                return;
            }

            mazeMatrixWithPath[y][x] = PATH;
        }

        for (int i = 0; i < mazeMatrix[0].length + 2; i++) System.out.print("\u2588\u2588");
        System.out.println();

        for (int i = 0; i < mazeMatrix.length; i++) {

            if (i == 0) System.out.print("//");
            else System.out.print("\u2588\u2588");

            for (int j = 0; j < mazeMatrix[0].length; j++) {
                int value = mazeMatrix[i][j];
                if (value == PASS) System.out.print("  ");
                else if (value == PATH) System.out.print("//");
                else System.out.print("\u2588\u2588");
            }

            if (i == mazeMatrix.length - 1) System.out.print("//\n");
            else System.out.print("\u2588\u2588\n");
        }

        for (int i = 0; i < mazeMatrix[0].length + 2; i++) System.out.print("\u2588\u2588");
        System.out.println();
    }

    /**
     * Выводит на консоль матрицу лабиринта из 1 и 0
     */
    public void printMazeMatrix() {
        for (int[] row : mazeMatrix) {
            for (int cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }

    /**
     * @return внутренняя матрица
     */
    public int[][] getMazeMatrix() {
        return mazeMatrix;
    }
}
