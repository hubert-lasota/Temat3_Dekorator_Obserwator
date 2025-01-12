import java.util.List;

public class MaxLengthNamesObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("After filter by max length. Names size is %d%n", names.size());
    }

}
