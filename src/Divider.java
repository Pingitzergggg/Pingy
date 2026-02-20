import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Divider {
    private String fileContent;
    private final LinkedList<String> codeBase = new LinkedList<>();

    public Divider(String filePath) {
        StringBuilder input = new StringBuilder();
        try {
            Scanner scn = new Scanner(new File(filePath));
            while (scn.hasNextLine()) {
                input.append(scn.nextLine());
                input.append("\n");
            }
            fileContent = input.toString();
            divide();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void divide() {
        StringBuilder expression = new StringBuilder();
        for (String character : fileContent.split("")) {
            if (Pattern.matches("^[{}]$", character)) {
                codeBase.add(expression.toString());
                expression.setLength(0);
                codeBase.add(character);
            } else if (character.equals(";") || character.equals("\n")) {
                codeBase.add(expression.toString());
                expression.setLength(0);
            } else {
                expression.append(character);
            }
        }
        codeBase.add(expression.toString());
    }

    public String getFileContent() {
        return fileContent;
    }

    public LinkedList<String> getCodeBase() {
        return codeBase;
    }
}
