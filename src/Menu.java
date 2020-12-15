
import java.util.Scanner;

public class Menu {

    public static void main(String[] args) {

        Maze maze;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Vai jus gribat, lai labirints un cels tiktu paraditi parskatami? (y - yes, citadi izvade pec parauga)");
        boolean visualMode = scanner.next().equalsIgnoreCase("y");

        System.out.print("Rindu skaits:");
        int rowQuantity = scanner.nextInt();
        System.out.print("Kolonu skaits:");
        int columnQuantity = scanner.nextInt();

        System.out.println("Auto fill maze (y - yes | n - no)?");
        String answer = scanner.next();
        if (answer.equalsIgnoreCase("n")) {
            int[][] mazeMatrix = new int[rowQuantity][columnQuantity];
            for (int i = 0; i < rowQuantity; i++)
                for (int j = 0; j < columnQuantity; j++)
                    mazeMatrix[i][j] = scanner.nextInt();
            maze = new Maze(mazeMatrix);
            System.out.println();
        } else if (answer.equalsIgnoreCase("y")) {
            maze = new Maze(rowQuantity, columnQuantity);
        } else {
            System.out.println("Incorrect option!");
            return;
        }

        if (visualMode) {
            maze.printMaze();
        } else {
            maze.printMazeMatrix();
        }

        System.out.println("\nKuru algoritmu jus gribat izmantot, lai atrast celu? \n" +
                "1 - vilnu algorims\n" +
                "2 - dziluma meklesanas algoritms\n" +
                "3 - rekursivs algoritms\n");
        System.out.print("Algoritma numurs: ");
        int algorithmNum = scanner.nextInt();

        Path path;
        switch (algorithmNum) {
            case 1:
                path = PathFinder.getPathByWaveAlgorithm(maze);
                break;
            case 2:
                path = PathFinder.getPathByDepthFirstSearch(maze);
                break;
            case 3:
                path = PathFinder.getPathByRecursiveAlgorithm(maze);
                break;
            default:
                System.out.println("Incorrect option!");
                return;
        }

        scanner.close();

        if (visualMode) {
            maze.printMazeWithPath(path);
        } else {
            System.out.println(path);
        }
    }
}
