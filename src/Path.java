import java.util.ArrayList;
import java.util.Collection;


/**
 * Представляет собой путь - список координат всех точек через которые нужно пройти, чтобы добраться от входа к выходу
 */
public class Path {
    private ArrayList<Coordinates> path;
    private int count;

    /**
     * @param steps любая коллекция, которая хранит координаты, желательно последовательно
     */
    public Path(Collection<Coordinates> steps) {
        this.path = new ArrayList<>(steps);
        this.count = 0;
    }

    /**
     * Проверяет остались ли еще не полученый шаги из пути
     * @return true - остались; false - не остались
     */
    public boolean hasNext() {
        return count < path.size();
    }

    /**
     * Возвращает координаты следующего шага и увеличивает счётчик
     * @return следующий шаг
     */
    public Coordinates nextStep() {
        return path.get(count++);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Coordinates step : path) sb.append(step.toString());
        return sb.toString();
    }
}
