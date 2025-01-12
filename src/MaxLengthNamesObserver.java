import java.util.List;

public class MaxLengthNamesObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("Names has been filtered by max length: %s%n", names);
    }

}
