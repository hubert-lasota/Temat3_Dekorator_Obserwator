import java.util.List;
import java.util.Objects;

public class MinLengthNamesFilter implements Names {

    private final Names names;
    private final int minLength;

    public MinLengthNamesFilter(Names names, int minLength) {
        this.names = Objects.requireNonNull(names, "names cannot be null");
        if (minLength < 0) {
            throw new IllegalArgumentException("length cannot be less than 0");
        }
        this.minLength = minLength;
    }

    @Override
    public List<String> getFilteredNames() {
        return names.getFilteredNames()
                .stream()
                .filter(name -> name.length() >= minLength )
                .toList();
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
