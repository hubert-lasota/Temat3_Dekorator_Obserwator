import java.util.List;

public class ContainsStringNamesObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("After search filter. Names size is %d\n", names.size());
    }

}
