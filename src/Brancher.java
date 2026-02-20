import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;

public class Brancher {
    private final LinkedList<HashMap<String, Object>> branch;
    private final int index;
    private boolean executionToken = false;

    public Brancher(int index, LinkedList<String> instructions) {
        BranchExtractor extractor = new BranchExtractor(index, instructions);
        extractor.extract();
        System.out.println(extractor.getOutput());
        this.branch = extractor.getOutput();
        this.index = extractor.getIndex();
    }

    public void printBranchTable() {
        for (HashMap<String, Object> subBranch : branch) {
            System.out.println(subBranch);
        }
    }

    public void execute() throws RuntimeException, InterruptedException {
        for (HashMap<String, Object> subBranch : branch) {
            if (subBranch.get("condition") != null) {
                Evaluator condition = new Evaluator(subBranch.get("condition").toString());
                try {
                    if (Boolean.parseBoolean(condition.eval())) {
                        executionToken = true;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                executionToken = true;
            }
            if (executionToken) {
                Register engine = new Register((LinkedList<String>) subBranch.get("instructions"));
                engine.start();
                return;
            }
        }
    }

    public int getIndex() {return this.index;}
}
