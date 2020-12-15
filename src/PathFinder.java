import java.util.*;

public class PathFinder {

    /**
     * Возвращает путь, полученный с помощью волнового алгоритма
     * @param maze объект готового лабиринта
     * @return объект пути
     */
    public static Path getPathByWaveAlgorithm(Maze maze)  {
        int[][] mazeMatrix = maze.getMazeMatrix();


        // Назначаются координаты старта и финиша
        Coordinates finish = new Coordinates(mazeMatrix.length - 1, mazeMatrix[0].length - 1);
        Coordinates start = new Coordinates(0, 0);

        // Создаёт очередь и добавляет в неё координаты старта
        Queue<Coordinates> vertexesQueue = new ArrayDeque();
        vertexesQueue.add(start);

        // Создаёт карту, где каждой паре координат соответсвует номер их порядка (удалённость от старта)
        HashMap<Coordinates, Integer> vertexCoordinatesAndOrder = new HashMap<>();
        vertexCoordinatesAndOrder.put(start, 0);

        // Получает список связей
        ArrayList<Edge> graph = getGraphForReadyMaze(mazeMatrix);

        // В цикле из очереди достаётся пара координат и высчитывается порядок его соседей, которые ещё не были посещены
        boolean isFound = false;
        while (vertexesQueue.peek() != null) {
            Coordinates currentVertex = vertexesQueue.poll();
            int order = vertexCoordinatesAndOrder.get(currentVertex) + 1;

            // Каждый не посещенный сосед добавляется в очередь и ему присваивается его порядок, и если его координаты
            // соответсвуют финишу цикл прерывается
            for (Edge edge : graph) {
                Coordinates nextOrderVertex;
                if (edge.getFirstVertexCoordinates().equals(currentVertex))
                    nextOrderVertex = edge.getSecondVertexCoordinates();
                else if (edge.getSecondVertexCoordinates().equals(currentVertex))
                    nextOrderVertex = edge.getFirstVertexCoordinates();
                else continue;

                if (!vertexCoordinatesAndOrder.containsKey(nextOrderVertex)) {
                    vertexCoordinatesAndOrder.put(nextOrderVertex, order);
                    if (nextOrderVertex.equals(finish)) {
                        isFound = true;
                        break;
                    }
                    vertexesQueue.add(nextOrderVertex);
                }
            }

            if (isFound) break;
        }

        /*
         Начиная с финиша, в каждой следующей итерации, для каждого следующего узла ищется сосед чей порядок на 1
         меньше, чем порядок текущего и добавляется в коллекцию (вместе с координатами шагов между ними) для создания
         объекта пути, пока не будет достигнута стартовая позиция
         */
        Coordinates controlPoint = finish;
        ArrayList<Coordinates> allSteps = new ArrayList<>();
        allSteps.add(controlPoint);
        int order = vertexCoordinatesAndOrder.get(finish);


        while (order > 0) {
            order--;
            for (Edge edge : graph) {
                Coordinates nextControlPoint = edge.getOtherVertexCoordinatesOrNull(controlPoint);
                if (nextControlPoint != null &&
                        vertexCoordinatesAndOrder.containsKey(nextControlPoint) &&
                        vertexCoordinatesAndOrder.get(nextControlPoint) == order) {

                    addStepsWithinPoints(allSteps, controlPoint, nextControlPoint);
                    allSteps.add(nextControlPoint);
                    controlPoint = nextControlPoint;
                    break;
                }
            }
        }

        // Переворот колекции, так как это путь от финища к старту
        Collections.reverse(allSteps);

        return new Path(allSteps);
    }

    /**
     * Возвращает путь, полученный с помощью поиска в глубину
     * @param maze объект готового лабиринта
     * @return объект пути
     */
    public static Path getPathByDepthFirstSearch(Maze maze) {
        int[][] mazeMatrix = maze.getMazeMatrix();

        // Получается список связей графа лабиринта и задаются координаты финиша
        ArrayList<Edge> graph = getGraphForReadyMaze(mazeMatrix);
        Coordinates finishCoordinates = new Coordinates(mazeMatrix.length - 1, mazeMatrix[0].length - 1);

        // Создаётся стек для хранения пути и в него помещается координаты старта
        Stack<Coordinates> pathStack = new Stack<>();
        pathStack.push(new Coordinates(0,0));

        // В каждой итерации получаются координаты с верхушки стека и, если они соответсвуют финишным, то цикл прерывается
        while (true) {
            Coordinates lastCoordinates = pathStack.peek();
            if (lastCoordinates.equals(finishCoordinates)) break;
            boolean isFound = false;

            // Если для этого узла существует сосед, то он добавляется в стек, а связь удаляется из списка, иначе верхний элемент стека выбрасывается
            for (int i = 0; i < graph.size(); i++) {
                Coordinates nextCoordinates = graph.get(i).getOtherVertexCoordinatesOrNull(lastCoordinates);

                if (nextCoordinates != null) {
                    isFound = true;
                    pathStack.push(nextCoordinates);
                    graph.remove(i);
                    break;
                }
            }

            if (!isFound) {
                pathStack.pop();
            }
        }

        // Стек содержит только значимые пункты, поэтому создается список для хранения полного пути и заполняется
        ArrayList<Coordinates> fullPath = new ArrayList<>();
        fullPath.add(new Coordinates(0, 0));

        for (int i = 1; i < pathStack.size(); i++) {
            Coordinates c1 = pathStack.get(i - 1);
            Coordinates c2 = pathStack.get(i);
            addStepsWithinPoints(fullPath, c1, c2);
            fullPath.add(c2);
        }

        return new Path(fullPath);
    }

    /**
     * Возвращает путь, полученный с помощью рекурсивного алгоритма
     * @param maze объект готового лабиринта
     * @return объект пути
     */
    public static Path getPathByRecursiveAlgorithm(Maze maze) {
        int[][] mazeMatrix = maze.getMazeMatrix();

        // Создается стек для хранения шагов пути, набор для хранения посещенных точек и задаются координаты финиша
        Stack<Coordinates> pathStack = new Stack<>();
        HashSet<Coordinates> visitedPoints = new HashSet<>();
        Coordinates finish = new Coordinates(mazeMatrix.length - 1, mazeMatrix[0].length - 1);;

        // Вызывается метод с координатами финиша
        recursiveMethod(mazeMatrix, visitedPoints, pathStack, finish.getY(), finish.getX());

        return new Path(pathStack);
    }

    /**
     * Рекурсивный метод. Вызывает сам себя, чтобы узнать есть ли путь впереди, проверяя ближайших соседей.
     * Если на каком-то этапе достигается старт, то возвращатся true, и его координаты, как и все предыдущии
     * координаты в цепочке в обратном порядке добавляются в стек. В случае если вызов приводит в стену или в уже
     * посещённую точку, то возвращается false
     * @param mazeMatrix матрица лабиринта
     * @param visitedPoints набор координат посещённых точек
     * @param pathStack стек для хранения пути
     * @param y
     * @param x
     * @return ведёт ли этот шаг к старту
     */
    private static boolean recursiveMethod(int[][] mazeMatrix, HashSet<Coordinates> visitedPoints, Stack<Coordinates> pathStack, int y, int x) {

        Coordinates currentPoint = new Coordinates(y, x);

        if (mazeMatrix[y][x] == 1 || visitedPoints.contains(currentPoint)) return false;
        if (y == 0 && x == 0) {
            pathStack.add(new Coordinates(0, 0));
            return true;
        }

        visitedPoints.add(currentPoint);

        if (x > 0 && recursiveMethod(mazeMatrix, visitedPoints, pathStack, y, x - 1)) {
            pathStack.push(currentPoint);
            return true;
        }

        if (y > 0 && recursiveMethod(mazeMatrix, visitedPoints, pathStack, y - 1, x)) {
            pathStack.push(currentPoint);
            return true;
        }

        if (y < mazeMatrix.length - 1 && recursiveMethod(mazeMatrix, visitedPoints, pathStack, y + 1, x)) {
            pathStack.push(currentPoint);
            return true;
        }

        if (x < mazeMatrix[0].length - 1 && recursiveMethod(mazeMatrix, visitedPoints, pathStack, y, x + 1)) {
            pathStack.push(currentPoint);
            return true;
        }
        
        return false;
    }

    /**
     * Заполняет список координатами точек между двумя ключевыми точками
     * @param allSteps список с координатами точек (шагов)
     * @param firstPoint координаты первого ключевого узла
     * @param secondPoint координаты второго ключевого узла
     */
    private static void addStepsWithinPoints(ArrayList<Coordinates> allSteps, Coordinates firstPoint, Coordinates secondPoint) {
        if (firstPoint.getY() == secondPoint.getY()) {
            int from = firstPoint.getX();
            int to = secondPoint.getX();

            if (from < to) {
                for (int i = from + 1; i < to; i++) {
                    allSteps.add(new Coordinates(firstPoint.getY(), i));
                }
            } else {
                for (int i = from - 1; i > to; i--) {
                    allSteps.add(new Coordinates(firstPoint.getY(), i));
                }
            }

        } else {
            int from = firstPoint.getY();
            int to = secondPoint.getY();

            if (from < to) {
                for (int i = from + 1; i < to; i++) {
                    allSteps.add(new Coordinates(i, firstPoint.getX()));
                }
            } else {
                for (int i = from - 1; i > to; i--) {
                    allSteps.add(new Coordinates(i, firstPoint.getX()));
                }
            }
        }
    }

    /**
     * Возвращает граф для готового лабиринта
     * @param mazeMatrix матрица лабиринта
     * @return список связей в этом лабиринте
     */
    private static ArrayList<Edge> getGraphForReadyMaze(int[][] mazeMatrix) {
        ArrayList<Edge> graph = new ArrayList<>();
        horizontalEdges(mazeMatrix, graph);
        verticalEdges(mazeMatrix, graph);
        return graph;
    }

    /**
     * Проходится по каждому ряду и объедияет ключевые узлы
     * @param mazeMatrix матрица лабиринта
     * @param graph список для заполнения
     */
    private static void horizontalEdges(int[][] mazeMatrix, ArrayList<Edge> graph) {
        // Устанавливается флаг, что предыдущего узла в этом ряду еще нет
        boolean havePrev = false;
        Coordinates previousVertex = null;

        // Для каждого ряда
        for (int j = 0; j < mazeMatrix.length; j++) {
            havePrev = false;

            // Проходится по всем ячейкам в ряду
            for (int i = 0; i < mazeMatrix[0].length; i++) {

                //Если текущая ячейка является проходом
                if (mazeMatrix[j][i] == 0) {

                    //Если ячейка не имеет предшественника в этом ряду (самое начало или перед этой ячейкой была стена),
                    // то она устанавливатся как предшествующая и цикл переходит к след. итерации
                    if (!havePrev) {
                        havePrev = true;
                        previousVertex = new Coordinates(j, i);
                        continue;
                    }

                    // Если справа стена или конец ряда... Иначе если снизу или сверху от текущей есть проход
                    // (узел не находится просто на прямой)
                    if ((i < mazeMatrix[0].length - 1 && mazeMatrix[j][i + 1] == 1) || i == mazeMatrix[0].length - 1) {
                        graph.add(new Edge(previousVertex, new Coordinates(j, i)));
                        havePrev = false;

                    } else if ((j > 0 && mazeMatrix[j - 1][i] == 0) ||
                            (j < mazeMatrix.length - 1 && mazeMatrix[j + 1][i] == 0)) {

                        graph.add(new Edge(previousVertex, new Coordinates(j, i)));
                        previousVertex = new Coordinates(j, i);
                    }
                } else {
                    if (havePrev) {
                        havePrev = false;
                    }
                }
            }
        }
    }

    /**
     * Проходится по каждой колонке и объедияет ключевые узлы
     * @param mazeMatrix матрица лабиринта
     * @param graph список для заполнения
     */
    private static void verticalEdges(int[][] mazeMatrix, ArrayList<Edge> graph) {
        // Устанавливается флаг, что предыдущего узла в этом ряду еще нет
        boolean havePrev = false;
        Coordinates previousVertex = null;

        // Для каждой колонки
        for (int j = 0; j < mazeMatrix[0].length; j++) {
            havePrev = false;

            // По каждой ячейке
            for (int i = 0; i < mazeMatrix.length; i++) {

                //Если текущая ячейка является проходом
                if (mazeMatrix[i][j] == 0) {

                    //Если ячейка не имеет предшественника в этой колонке (самый верх или перед этой ячейкой была стена),
                    // то она устанавливатся как предшествующая и цикл переходит к след. итерации
                    if (!havePrev) {
                        havePrev = true;
                        previousVertex = new Coordinates(i, j);
                        continue;
                    }

                    // Если снизу стена или конец колонки... Иначе если справа или слева от текущей есть проход
                    // (узел не находится просто на прямой)
                    if ((i < mazeMatrix.length - 1 && mazeMatrix[i + 1][j] == 1) || i == mazeMatrix.length - 1) {
                        graph.add(new Edge(previousVertex, new Coordinates(i, j)));
                        havePrev = false;

                    } else if ((j > 0 && mazeMatrix[i][j - 1] == 0) ||
                            (j < mazeMatrix[0].length - 1 && mazeMatrix[i][j + 1] == 0)) {

                        graph.add(new Edge(previousVertex, new Coordinates(i, j)));
                        previousVertex = new Coordinates(i, j);
                    }
                } else {
                    if (havePrev) {
                        havePrev = false;
                    }
                }
            }
        }
    }


}