import java.util.List;

public class NamesSortAscObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("Names has been sorted ascending: %s", names);
    }

}
