import java.util.*;

public class BaseNames implements Names {
    private final Set<Observer> observers;
    private final List<String> filteredNames;

    public BaseNames(List<String> names) {

        this.filteredNames = Objects.requireNonNull(names, "names cannot be null");
        observers = new HashSet<>();
    }

    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(List<String> names) {
        observers.forEach(observer -> observer.update(names));
    }

    @Override
    public List<String> getFilteredNames() {
        return filteredNames;
    }

}
