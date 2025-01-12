import java.util.Comparator;
import java.util.List;

public class NamesSortDesc implements NamesSort, Names {

    private final Names names;

    public NamesSortDesc(Names names) {
        this.names = names;
    }

    @Override
    public List<String> sort(List<String> names) {
        return names.stream()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    @Override
    public List<String> getFilteredNames() {
        return sort(names.getFilteredNames());
    }

    @Override
    public void addObserver(Observer observer) {
        names.addObserver(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        names.removeObserver(observer);
    }

    @Override
    public void notifyObservers(List<String> names) {
        this.names.notifyObservers(names);
    }

}
