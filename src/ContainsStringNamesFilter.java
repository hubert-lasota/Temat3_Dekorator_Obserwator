import java.util.List;
import java.util.Objects;

public class ContainsStringNamesFilter implements Names {

    private final Names names;
    private final String filter;

    public ContainsStringNamesFilter(Names names, String filter) {
        this.names = Objects.requireNonNull(names, "names cannot be null");
        Objects.requireNonNull(filter, "filter cannot be null");
        if(filter.isEmpty()) {
            throw new IllegalArgumentException("filter cannot be empty");
        }
        this.filter = filter;
    }

    @Override
    public List<String> getFilteredNames() {
        String filterUpperCase = filter.toUpperCase();
        return names.getFilteredNames()
                .stream()
                .filter(name -> {
                    String nameUpperCase = name.toUpperCase();
                    return nameUpperCase.contains(filterUpperCase);
                })
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
