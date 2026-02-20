import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class CycleExtractor {
    private final HashMap<String, Object> output = new HashMap<>();
    private int index;
    private LinkedList<String> instructions;

    public CycleExtractor(int index, LinkedList<String> instructions) {
        this.index = index;
        this.instructions = instructions;
    }

    public void extract() {
        String[] header = instructions.get(index).strip().split(" ");
        StringBuilder condition = new StringBuilder();
        for (int i = 1; i < header.length; ++i) {
            switch (header[i]) {
                case "with" -> {
                    String varname = header[i+1];
                    output.put("with", varname);
                    System.out.println("with"+varname);
                    i = i+1;
                }
                case "by" -> {
                    String slope = header[i+1];
                    output.put("by", slope);
                    System.out.println("by: "+slope);
                    i = i+1;
                }
                case "check" -> {
                    String check = header[i+1];
                    output.put("check", check);
                    System.out.println("check: "+check);
                    i = i+1;
                }
                default -> {
                    condition.append(header[i]);
                    System.out.println("default ran");
                }
            }
        }

        ClauseExtractor scope = new ClauseExtractor(index+1, instructions, "{", "}");
        scope.extract();

        output.put("condition", condition.toString());
        output.put("scope", scope.getOutput());
        index = scope.getIndex();
    }

    public HashMap<String, Object> getOutput() {return output;}
    public int getIndex() {return index;}
}
