import java.util.*;

/**
 * Класс ответственный за генерацию лабиринтов. Для этого использует граф узлы которого расположены в чётных рядах
 * и колонках (Примеры: (0;0), (2;2), (8;8), (10;2)).
 * Как условие рассматривается, что из любой части лабиринта можно попасть в любую часть и, что он не содержит
 * изолированных коридоров в которые невозможно попасть или выбраться. Для этого получается остовное дерево графа,
 * ветви которого образуют коридоры.
 */
public class MazeGenerator {

    /**
     * Главный метод класса, который собирает логику воедино. Принимает на вход количество рядов и колонок в требуемом
     * лабиринте и возвращает двумерный массив из 1 (означает стену) и 0 (означает проход).
     * @param rowsQuantity количество рядов
     * @param columnQuantity количество колонок
     * @return матрица лабиринта
     */
    public static int[][] createMaze(int rowsQuantity, int columnQuantity) {
        // Создаётся матрица лабиринта с заданными размерами и заполняется везде, исключая места рассположения узлов
        int[][] mazeMatrix = new int[rowsQuantity][columnQuantity];
        fillMaze(mazeMatrix);

        // Рассчитывается количество узлов в ряду и колонке (формула зависит от чётности) и общее их количество в матрице
        int vertexesInRow = (rowsQuantity & 1) == 0 ? rowsQuantity / 2 : rowsQuantity / 2 + 1;
        int vertexesInColumn = (columnQuantity & 1) == 0 ? columnQuantity / 2 : columnQuantity / 2 + 1;
        int vertexesQuantity = vertexesInColumn * vertexesInRow;

        // Создается список для хранения связей между узлами
        ArrayList<WeightedEdge> edges = new ArrayList<>();

        // Создаются связи между узлами
        connectVectors(vertexesQuantity, vertexesInRow, vertexesInColumn, edges);

        // Список в котором содержаться связи сортируются по весу по возрастанию
        Collections.sort(edges, Comparator.comparingInt(WeightedEdge::getWeight));

        // Получается набор связей входящих в остовное дерево
        HashSet<WeightedEdge> spanningTree = getSpanningTree(vertexesQuantity, edges);

        // На месте каждой связи "прогрызается" стена
        for (WeightedEdge edge : spanningTree) {

            Coordinates firstVertexCoordinates = edge.getFirstVertexCoordinates();
            Coordinates secondVertexCoordinates = edge.getSecondVertexCoordinates();

            int jointsRow = (firstVertexCoordinates.getY() + secondVertexCoordinates.getY()) / 2;
            int jointsColumn = (firstVertexCoordinates.getX() + secondVertexCoordinates.getX()) / 2;

            mazeMatrix[jointsRow][jointsColumn] = Maze.PASS;
        }

        // Так как в случае чётного количества рядов и колонок образуются двойные стены может потребоваться
        // прогрызать дополнительный коридор
        additionalCorridor(mazeMatrix, rowsQuantity, columnQuantity);

        return mazeMatrix;
    }

    /**
     * Так как в случае чётного количества рядов и колонок образуются двойные стены может потребоваться
     * прогрызать дополнительный коридор
     * @param mazeMatrix матрица лабиринта
     * @param rowsQuantity количество рядов
     * @param columnQuantity количество колонок
     */
    private static void additionalCorridor(int[][] mazeMatrix, int rowsQuantity, int columnQuantity) {
        int lastColumn = (columnQuantity & 1) == 0 ? columnQuantity - 2 : columnQuantity - 1;
        if ((rowsQuantity & 1) == 0) mazeMatrix[rowsQuantity - 1][lastColumn] = 0;
        if ((columnQuantity & 1) == 0) mazeMatrix[rowsQuantity - 1][lastColumn + 1] = 0;
    }

    /**
     * На основе ид узла рассчитывает его координаты <номер ряда, номер колонки> внутри матрицы лабиринта
     * @param id ид узла
     * @param vertexesInColumn количество рядов в колонке
     * @return координаты узла
     */
    private static Coordinates getCoordinateByVectorId(int id, int vertexesInColumn) {
        int rowNum = (id / vertexesInColumn) * 2;
        int columnNum = (id % vertexesInColumn) * 2;
        return new Coordinates(rowNum, columnNum);
    }

    /**
     * Объединяет все узлы из матрицы графа в условную сетку, соединяя каждый узел с его правым и нижним соседов
     * (если такие есть), и задает связям случайное значение, занося его в список связей
     * @param vertexesInRow количество узлов в ряду
     * @param vertexesInColumn количество узлов в колонке
     * @param edges список связей для заполнения
     */
    private static void connectVectors(int vertexesQuantity, int vertexesInRow, int vertexesInColumn, ArrayList<WeightedEdge> edges) {

        Random random = new Random();

        for (int i = 0; i < vertexesQuantity; i++) {
            //Создает связь с соседом справа, если такой есть
            if ((i + 1) % vertexesInColumn != 0) {
                Coordinates firstVertexCoordinates = getCoordinateByVectorId(i, vertexesInColumn);
                Coordinates secondVertexCoordinates = getCoordinateByVectorId(i + 1, vertexesInColumn);
                int randomValue = random.nextInt();
                edges.add(new WeightedEdge(i, i + 1, randomValue, firstVertexCoordinates, secondVertexCoordinates));
            }
            //Создает связь с соседом снизу, если такой есть
            if ((i / vertexesInColumn) + 1 != vertexesInRow) {
                Coordinates firstVertexCoordinates = getCoordinateByVectorId(i, vertexesInColumn);
                Coordinates secondVertexCoordinates = getCoordinateByVectorId(i + vertexesInColumn, vertexesInColumn);
                int randomValue = random.nextInt();
                edges.add(new WeightedEdge(i, i + vertexesInColumn, randomValue, firstVertexCoordinates, secondVertexCoordinates));
            }
        }
    }


    /**
     * Получает остовное дерево на основе отсортированного списка имеющихся связей. Создается список ид,
     * которые уже присутствуют в дереве и в него заносится 0 (вход). После в каждой итерации цикла в списке ищется
     * самая лёгкая связь один из узлов которой еще не входит в дерево, после нахождения ид узла отмечается как
     * использованный, а связь добавляется в дерево и удаляется из списка
     * @param vertexesQuantity количество узлов в матрице лабиринта
     * @param edges список всех связей
     * @return набор связей из остовного дерева
     */
    private static HashSet<WeightedEdge> getSpanningTree(int vertexesQuantity, ArrayList<WeightedEdge> edges) {
        HashSet<Integer> usedVertexesId = new HashSet<>();
        usedVertexesId.add(0);
        HashSet<WeightedEdge> spanningTree = new HashSet<>();

        while (spanningTree.size() < vertexesQuantity - 1) {
            for (int i = 0; i < edges.size(); i++) {
                WeightedEdge WeightedEdge = edges.get(i);
                int firstVertex = WeightedEdge.getFirstVertexId();
                int secondVertex = WeightedEdge.getSecondVertexId();

                if ((usedVertexesId.contains(firstVertex) && !usedVertexesId.contains(secondVertex)) ||
                        (!usedVertexesId.contains(firstVertex) && usedVertexesId.contains(secondVertex))) {
                    usedVertexesId.add(firstVertex);
                    usedVertexesId.add(secondVertex);
                    spanningTree.add(WeightedEdge);
                    edges.remove(i);
                    break;
                }
            }
        }

        return spanningTree;
    }

    /**
     * Заливает помещение лабиринта бетоном(сплошные стены), оставляя пустыми только места нахождения узлов
     * @param mazeMatrix матрица лабиринта
     */
    private static void fillMaze(int[][] mazeMatrix) {
        for (int i = 0; i < mazeMatrix.length; i++) {
            for (int j = 0; j < mazeMatrix[0].length; j++) {
                if ((i & 1) != 0 || (j & 1) != 0) mazeMatrix[i][j] = Maze.WALL;
            }
        }
    }
}
