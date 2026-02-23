import java.util.HashMap;
import java.util.LinkedList;

public class BranchExtractor {
    private final LinkedList<HashMap<String, Object>> output = new LinkedList<>();
    private int index;
    private LinkedList<String> instructions;

    public BranchExtractor(int index, LinkedList<String> instructions) {
        this.index = index;
        this.instructions = instructions;
    }

    public void extract() throws UnresolvedClauseException {
        for (int i = index; i < instructions.size(); i++) {
            ClauseExtractor scope = new ClauseExtractor(i+1, instructions, "{", "}");
            BranchTypes type = null;
            System.out.println("---"+instructions.get(i).strip().split(" ")[0]);
            switch (instructions.get(i).strip().split(" ")[0]) {
                case "if" -> {
                    System.out.println("if detected");
                    if (containsKey(BranchTypes.IF)) return;
                    type = BranchTypes.IF;
                }
                case "elif" -> {
                    System.out.println("elif detected");
                    if (!containsKey(BranchTypes.IF)) throw new RuntimeException("elif clause must go after if clause!");
                    type = BranchTypes.ELIF;
                }
                case "else" -> {
                    System.out.println("else detected");
                    if (!containsKey(BranchTypes.IF)) throw new RuntimeException("elif clause must go after if clause!");
                    type = BranchTypes.ELSE;
                }
                default -> {
                    return;
                }
            }
            System.out.println("--"+instructions.get(i));
            if (Inspector.lookAhead(i, instructions).equals("{")) {
                HashMap<String, Object> branch = new HashMap<>();
                scope.extract();
                System.out.println(scope.getOutput());
                System.out.println("created socket for "+type+" branch");
                branch.put("type", type);
                branch.put("condition", (type == BranchTypes.ELSE ? null : conditionExtractor(i).strip()));
                branch.put("instructions", scope.getOutput());
                output.add(branch);
                i = scope.getIndex();
                this.index = i;
            }
        }
    }

    public boolean containsKey(BranchTypes key) {
        for (HashMap<String, Object> value : output) {
            if (value.containsValue(key)) return true;
        }
        return false;
    }

    public String conditionExtractor(int index) {
        StringBuilder condition = new StringBuilder();
        String[] instruction = instructions.get(index).strip().split(" ");
        for (int i = 1; i < instruction.length; i++) {
            condition.append(instruction[i]);
        }
        return condition.toString();
    }

    public int getIndex() {return this.index;}
    public LinkedList<HashMap<String, Object>> getOutput() {return this.output;}
}
