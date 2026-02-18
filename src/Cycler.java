import com.sun.nio.sctp.IllegalReceiveException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Cycler {
    private static final Accessor accessor = Accessor.getInstance();

    private final int index;
    private final HashMap<String, Object> output;
    private boolean executionToken = true;
    
    private Object cycleVariable = 0;
    private boolean variableBinding = false;
    private boolean afterTesting = false;
    private String condition;
    private double slope;
    private LinkedList<String> scope;

    public Cycler(int index, LinkedList<String> instructions) throws ParseException {
        CycleExtractor extractor = new CycleExtractor(index, instructions);
        extractor.extract();
        System.out.println(extractor.getOutput());
        this.index = extractor.getIndex();
        this.output = extractor.getOutput();
        parseDetails();
        parseEnvVar();
    }
    
    public void execute() throws ParseException {
        while (true) {
            System.out.println("Iterating...\nCondition: "+condition+"\nWith: "+cycleVariable+" bound: "+variableBinding+"\nBy: "+slope+"\nScope: "+scope+"\nCheck later: "+afterTesting);
            if (executionToken) {
                parseCondition();
                if (afterTesting) {
                    iterateFirst();
                } else {
                    iterate();
                }
            } else {
                if (!variableBinding && accessor.doesExist(cycleVariable.toString())) accessor.removeValue(cycleVariable.toString());
                return;
            }
        }
    }

    private void parseCondition() throws ParseException {
        Object value = output.get("condition");
        if (value == null || value.equals("")) {
            condition = "true";
        } else if (Pattern.matches("^[0-9]\\.\\.=?[0-9]$", value.toString().strip())) {
            String[] interval = value.toString().strip().split("\\.\\.");
            StringBuilder conditionBuilder = new StringBuilder();
            if (interval[1].split("")[0].equals("=")) {
                conditionBuilder.append(cycleVariable);
                conditionBuilder.append("<=");
                for (int i = 1; i < interval[1].split("").length; ++i) conditionBuilder.append(interval[1].split("")[i]);
                condition = conditionBuilder.toString();
            } else {
                conditionBuilder.append(cycleVariable);
                conditionBuilder.append("<");
                conditionBuilder.append(interval[1]);
                condition = conditionBuilder.toString();
            }
        } else {
            Evaluator evaluator = new Evaluator(value.toString());
            String result = evaluator.eval();
            if (result.equals("true") || result.equals("false")) {
                condition = value.toString();
            } else {
                StringBuilder conditionBuilder = new StringBuilder();
                conditionBuilder.append(cycleVariable);
                conditionBuilder.append("<");
                conditionBuilder.append(value);
                condition = conditionBuilder.toString();
            }
        }
    }

    private void parseEnvVar() throws ParseException {
        Object value = output.get("with");
        if (value != null) {
            if (!accessor.doesExist(value.toString())) {
                accessor.declare("@double", value.toString());
            } else {
                variableBinding = true;
            }
            cycleVariable = value.toString();
        } else {
            cycleVariable = 0;
        }
        if (output.get("condition") != null && Pattern.matches("^[0-9]\\.\\.=?[0-9]$", output.get("condition").toString().strip())) {
            String[] interval = output.get("condition").toString().strip().split("\\.\\.");
            if (accessor.doesExist(cycleVariable.toString())) {
                accessor.overWriteValue(cycleVariable.toString(), Integer.parseInt(interval[0]));
            } else {
                cycleVariable = Double.parseDouble(interval[0]);
            }
        }
    }

    private void parseDetails() throws ParseException {
        this.slope = output.get("by") == null ? 1 : Double.parseDouble(output.get("by").toString());
        this.scope = (LinkedList<String>) output.get("scope");
        if (output.get("check") == null) {
            this.afterTesting = false;
        } else {
            if (output.get("check").equals("after")) {
                this.afterTesting = true;
            }
        }
    }

    private void iterateFirst() throws ParseException {
        Register engine = new Register(scope);
        engine.start();
        System.out.println("Iterating");
        if (accessor.doesExist(cycleVariable.toString())) {
            accessor.compoundModification(cycleVariable.toString(), CompoundAssignmentTypes.ADD, String.valueOf(slope), false);
        } else {
            System.out.println("increasing value");
            cycleVariable = ((Number) cycleVariable).doubleValue() + slope;
        }
        Evaluator evaluator = new Evaluator(condition);
        String result = evaluator.eval();
        if (result.equals("false")) {
            executionToken = false;
        }
    }

    private void iterate() throws ParseException {
        Evaluator evaluator = new Evaluator(condition);
        String result = evaluator.eval();
        if (result.equals("false")) {
            executionToken = false;
        } else if (result.equals("true")) {
            Register engine = new Register(scope);
            engine.start();
            System.out.println("Iterating");
            if (accessor.doesExist(cycleVariable.toString())) {
                accessor.compoundModification(cycleVariable.toString(), CompoundAssignmentTypes.ADD, String.valueOf(slope), false);
            } else {
                System.out.println("increasing value");
                cycleVariable = ((Number) cycleVariable).doubleValue() + slope;
            }
        } else {
            throw new IllegalReceiveException("Condition must have @bool return value!");
        }
    }

    public int getIndex() {return index;}
}
