import java.util.List;

public class NamesSortAsc implements NamesSort, Names {

    private final Names names;

    public NamesSortAsc(Names names) {
        this.names = names;
    }

    @Override
    public List<String> sort(List<String> names) {
       names.sort(String::compareTo);
       return names;
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
