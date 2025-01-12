import java.util.List;

public class ContainsStringNamesObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("Names has been filtered by search string: %s\n", names);
    }

}
