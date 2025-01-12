import java.util.List;
import java.util.Objects;

public class MaxLengthNamesFilter implements Names {

    private final Names names;
    private final int maxLength;

    public MaxLengthNamesFilter(Names names, int maxLength) {
        this.names = Objects.requireNonNull(names, "names cannot be null");
        if(maxLength < 0) {
            throw new IllegalArgumentException("maxLength must be at least 0");
        }
        this.maxLength = maxLength;
    }

    @Override
    public List<String> getFilteredNames() {
        return names.getFilteredNames()
                .stream()
                .filter(name -> name.length() <= maxLength)
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
