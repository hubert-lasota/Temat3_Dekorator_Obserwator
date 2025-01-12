import java.util.List;

public class NamesSortDescObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("Names has been sorted descending: %s", names);
    }
}
