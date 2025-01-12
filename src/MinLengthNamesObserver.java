import java.util.List;

public class MinLengthNamesObserver implements Observer {

    @Override
    public void update(List<String> names) {
        System.out.printf("After filter by min length. Names size is %d%n",names.size());
    }
}
