import java.util.List;

public class MinLengthNamesObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("Names has been filtered by min length: %s%n", names);
    }
}
