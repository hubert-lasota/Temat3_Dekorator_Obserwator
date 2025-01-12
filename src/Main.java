import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Main {

    private static int id = 1;
    private static final Map<Integer, Names> namesDatabase = new HashMap<>();
    private static boolean isJson;
    private static Names names;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        boolean isFinished = false;
        do {
            start(sc);
            System.out.print("\nWant to start program again(y/n)?");
            String result = sc.nextLine();
            if (!result.equals("y")) {
                isFinished = true;
            }
            names = null;
            isJson = false;
        } while (!isFinished);
        sc.close();
    }

    private static void start(Scanner sc) {
        System.out.println("Fetch names by: ");
        boolean isFetchTypeFinished = false;
        do {
            System.out.println("1. JSON");
            System.out.println("2. Manually");
            String result = sc.nextLine();
            try {
                int resultNum = Integer.parseInt(result);
                if(resultNum > 2 || resultNum < 1) {
                    throw new IllegalStateException();
                }
                if(resultNum == 1) {
                    isJson = true;
                }
                isFetchTypeFinished = true;
            } catch (NumberFormatException | IllegalStateException e) {
                System.out.println("Enter a valid option.");
            }
        } while (!isFetchTypeFinished);

        if(isJson) {
            fetchJson(sc);
        } else {
            manually(sc);
        }
    }

    private static void fetchJson(Scanner sc) {
        String jsonDirName = "src/json";
        File jsonDir = new File(jsonDirName);
        File[] jsonFiles = jsonDir.listFiles();
        JsonReader jsonReader = null;
        boolean isValidJsonForm = false;
        do {
            try {
                int index = selectJsonFile(jsonFiles, sc);
                String jsonData = Files.readString(jsonFiles[index].toPath());
                jsonReader = new JsonReader(jsonData);
                isValidJsonForm = true;
            } catch (IOException e) {
                System.out.println("Error reading json file. Select different json");
            }
        } while (!isValidJsonForm);

        List<String> jsonNames = jsonReader.getFirstValueInTree("names", List.class);
        System.out.printf("Selected names: %s%n", jsonNames);
        names = new BaseNames(jsonNames);
        filterForm(sc);
    }

    private static int selectJsonFile(File[] jsonFiles, Scanner sc) {
        boolean isJsonSelected = false;
        int jsonIndex = 0;
        System.out.println("Choose a json file:");
        do {
            try {
                for (int i = 0; i < jsonFiles.length; i++) {
                    System.out.println((i + 1) + ". " + jsonFiles[i].getName());
                }
                int index = sc.nextInt();
                sc.nextLine();
                if(index < 1 || index > jsonFiles.length) {
                    throw new RuntimeException();
                }
                jsonIndex = index - 1;
                isJsonSelected = true;
            } catch (RuntimeException e) {
                System.out.println("Enter a valid option.");
            }
        } while (!isJsonSelected);
        return jsonIndex;
    }

    private static void manually(Scanner sc) {
        List<String> nameList = new ArrayList<>();
        boolean isNameTypingFinished = false;
        System.out.println("Enter names(type 'q' to stop typing)");
        do {
            System.out.print("Name: ");
            String result = sc.nextLine();
            if(result.equals("q")) {
                if(nameList.isEmpty()) {
                    System.out.println("You have to type at least one name.");
                } else {
                    isNameTypingFinished = true;
                }
            } else {
                nameList.add(result);
            }
        } while (!isNameTypingFinished);
        names = new BaseNames(nameList);
        filterForm(sc);
    }

    private static void filterForm(Scanner sc) {
        boolean isSortFinished = false;
        Observer observer = null;
        System.out.println("Enter sort type: ");
        do {
            System.out.println("1. Ascending");
            System.out.println("2. Descending");
            System.out.println("3. Skip sorting");
            String result = sc.nextLine();
            try {
                int resultNum = Integer.parseInt(result);
                if(resultNum > 3 || resultNum < 1) {
                    throw new IllegalStateException();
                }
                if(resultNum == 1) {
                    observer = new NamesSortAscObserver();
                    names = new NamesSortAsc(names);
                    names.addObserver(observer);
                } else if(resultNum == 2) {
                    observer = new NamesSortDescObserver();
                    names = new NamesSortDesc(names);
                    names.addObserver(observer);
                } else {
                    break;
                }
                List<String> filteredNames = names.getFilteredNames();
                names.notifyObservers(filteredNames);
                names.removeObserver(observer);
                isSortFinished = true;
            } catch (NumberFormatException | IllegalStateException e) {
                System.out.println("Enter a valid option.");
            }
        } while (!isSortFinished);

        minMaxForm(sc, true);
        minMaxForm(sc, false);

        System.out.print("Enter search string(press 'q' to skip filter): ");
        String containsString = sc.nextLine();
        if(!containsString.equals("q")) {
            names = new ContainsStringNamesFilter(names, containsString);
            observer = new ContainsStringNamesObserver();
            names.addObserver(observer);
            List<String> filteredNames = names.getFilteredNames();
            names.notifyObservers(filteredNames);
            names.removeObserver(observer);
        }
        List<String> finalResult = names.getFilteredNames();
        if(finalResult.isEmpty()) {
            System.out.println("Final result is empty");
        } else {
            System.out.printf("\nFinal result = %s", finalResult);
            namesDatabase.put(id++, names);
        }

        System.out.println("\nNames list in database");
        namesDatabase.forEach((k, v) ->
            System.out.printf("Id: %d, Names: %s\n", k, v.getFilteredNames())
        );
    }

    private static void minMaxForm(Scanner sc, boolean isMin) {
        Observer observer = null;
        boolean isMinMaxLengthFinished = false;
        String minMaxText = isMin ? "minimum" : "maximum";
        System.out.printf("\nEnter %s length(type 'q' to skip filter): ", minMaxText);
        do {
            String result = sc.nextLine();
            if(result.equals("q")) {
                break;
            }
            try {
                int length = Integer.parseInt(result);
                if(isMin) {
                    names = new MinLengthNamesFilter(names, length);
                    observer = new MinLengthNamesObserver();
                } else {
                    names = new MaxLengthNamesFilter(names, length);
                    observer = new MaxLengthNamesObserver();
                }
                names.addObserver(observer);
                List<String> filteredNames = names.getFilteredNames();
                names.notifyObservers(filteredNames);
                names.removeObserver(observer);
                isMinMaxLengthFinished = true;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        } while (!isMinMaxLengthFinished);

    }
}