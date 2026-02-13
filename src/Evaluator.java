import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Evaluator {
    private final boolean logContent;

    private static final String[] operators = {"*", "/", "+", "-", "=", "<", ">", "&", "|", "^"};

    private static final String[][] precedency = {
            {"*", "/"},
            {"+", "-"},
            {"==", "<", ">", "<=", ">=", "!="},
            {"&&", "||", "^^"}
    };

    private final String problem;
    private final LinkedList<String> strippedProblem = new LinkedList<>();

    public Evaluator(String problem) {
        this.problem = problem;
        this.logContent = false;
    }

    public Evaluator(String problem, boolean logContent) {
        this.problem = problem;
        this.logContent = logContent;
    }

    public String eval() throws ParseException {
        if (logContent) {
            System.out.println("Main poblem: "+problem+"\n");
        }
        problemStripper(problem);
        precedencyChecker();
        if (!strippedProblem.isEmpty()) {
            if (logContent) System.out.println("\nstrippedProblem: "+strippedProblem);
            if (returnTypeRecognizer() == Types.STRING) {
                if (logContent) System.out.println("Final result: \""+strippedProblem.getFirst()+"\"");
                return "\""+strippedProblem.getFirst()+"\"";
            } else {
                if (logContent) System.out.println("Final result: "+strippedProblem.getFirst());
                return strippedProblem.getFirst();
            }
        } else {
            if (logContent) {
                System.out.println("\nstrippedProblem: "+strippedProblem);
                System.out.println("Final result: null");
            }
            return null;
        }
    }

    private Types returnTypeRecognizer() throws RuntimeException {
        if (strippedProblem.size() != 1) throw new RuntimeException("Calculation failed!");
        String result = strippedProblem.getFirst();
        if (result.equals("false") || result.equals("true")) {
            if (logContent) System.out.println("@bool result detected!");
            return Types.BOOL;
        } else if (Pattern.matches("^(0|[1-9])[0-9]*(\\.[0-9]*)?$", result)) {
            if (logContent) System.out.println("@double result detected!");
            return Types.DOUBLE;
        } else {
            if (logContent) System.out.println("@string result detected!");
            return Types.STRING;
        }
    }

    private void problemStripper(String problem) throws ParseException {
        String[] iterable = String.join("", problem.split(" ")).split("");
        StringBuilder currentValue = new StringBuilder();
        boolean doesVariableKeyContainNumericOrBooleanValue = false;
        if (logContent) {
            System.out.println("Extracting elements...");
        }
        for (int i = 0; i < iterable.length; i++) {
            if (iterable[i].equals("\"")) {
                if (!currentValue.isEmpty()) {
                    nonMatchedOperatorExtractor(currentValue.toString());
                    currentValue.setLength(0);
                }
                i = typeExtractor(i, iterable, (character) -> (character.equals("\"")), true);

                if (logContent) {
                    System.out.println("String literal detected!\nExtracted value: \""+strippedProblem.getLast()+"\"");
                }
            } else if (iterable[i].equals("(")) {

                if (!currentValue.isEmpty()) {
                    nonMatchedOperatorExtractor(currentValue.toString());
                    currentValue.setLength(0);
                }

                i = nestedExpressionExtractor(i, iterable);
            } else if (Pattern.matches("^[0-9.]$", iterable[i])) {
                if (!currentValue.isEmpty()) {
                    if (isOperator(iterable[i-1])) {
                        doesVariableKeyContainNumericOrBooleanValue = false;
                        nonMatchedOperatorExtractor(currentValue.toString());
                        currentValue.setLength(0);
                    } else {
                        doesVariableKeyContainNumericOrBooleanValue = true;
                        currentValue.append(iterable[i]);
                    }
                }
                if (!doesVariableKeyContainNumericOrBooleanValue) {
                    i = typeExtractor(i, iterable, (character) -> (!Pattern.matches("^[0-9.]$", character)), false);
                }

                if (logContent) {
                    if (!strippedProblem.isEmpty()) {
                        System.out.println("Numeric literal detected!\nExtracted value: "+strippedProblem.getLast());
                    }
                }
            } else if (iterable[i].equals("f") || iterable[i].equals("t")) {
                if (!currentValue.isEmpty()) {
                    if (isOperator(iterable[i-1])) {
                        doesVariableKeyContainNumericOrBooleanValue = false;
                        nonMatchedOperatorExtractor(currentValue.toString());
                        currentValue.setLength(0);
                    } else {
                        doesVariableKeyContainNumericOrBooleanValue = true;
                        currentValue.append(iterable[i]);
                    }
                }
                if (!doesVariableKeyContainNumericOrBooleanValue) {
                    boolean seekTrue = false;
                    if (iterable[i].equals("t")) {
                        seekTrue = true;
                    }
                    StringBuilder boolPattern = new StringBuilder();

                    for (int j = i; j < (seekTrue ? i+4 : i+5); j++) {
                        boolPattern.append(iterable[j]);
                        if (!Inspector.lookAhead(j, iterable).isEmpty()) {
                            if (j == (seekTrue ? i+3 : i+4)
                                    && !isOperator(Inspector.lookAhead(j, iterable))
                                    && !Pattern.matches("^[()\"]$", Inspector.lookAhead(j, iterable))) {
                                System.out.println("this ran");
                                boolPattern.setLength(0);
                            }
                        }

                    }

                    if (boolPattern.toString().equals("true")) {
                        appender(boolPattern.toString());
                        i = i+3;
                        if (logContent) {
                            System.out.println("Boolean literal detected!\nExtracted value: "+boolPattern.toString());
                        }
                    } else if (boolPattern.toString().equals("false")) {
                        appender(boolPattern.toString());
                        i = i+4;
                        if (logContent) {
                            System.out.println("Boolean literal detected!\nExtracted value: "+boolPattern.toString());
                        }
                    } else {
                        if (!iterable[i].equals(" ")) currentValue.append(iterable[i]);
                    }
                }
            } else {
                if (!iterable[i].equals(" ")) currentValue.append(iterable[i]);
            }
        }
        if (!currentValue.isEmpty()) {
            nonMatchedOperatorExtractor(currentValue.toString());
            currentValue.setLength(0);
        }
    }

    private int nestedExpressionExtractor(int index, String[] iterable) throws ParseException, ArithmeticException {
        StringBuilder currentValue = new StringBuilder();
        int unresolvedParenthesesPairs = 0;
        for (int i = index+1; i < iterable.length; i++) {
            if (iterable[i].equals("(")) unresolvedParenthesesPairs++;
            if (iterable[i].equals(")")) {
                if (unresolvedParenthesesPairs == 0) {
                    if (logContent) {
                        System.out.println("Open parenthesis found!\n\nRecursion for parentheses fallback begin...");
                    }
                    Evaluator parenthesesExtractor = new Evaluator(currentValue.toString(), logContent);
                    appender(parenthesesExtractor.eval());
                    if (logContent) {
                        System.out.println("End of recursion\nStepback to: "+problem+"\n");
                    }
                    currentValue.setLength(0);
                    return i;
                } else {
                    unresolvedParenthesesPairs--;
                    currentValue.append(iterable[i]);
                }
            } else {
                currentValue.append(iterable[i]);
            }
        }
        throw new ArithmeticException("Unclosed parentheses found");
    }

    private void nonMatchedOperatorExtractor(String key) throws NullPointerException, ParseException {
        Pattern variableOperatorPassThroughPattern = Pattern.compile("[a-zA-Z0-9_]");
        String[] keySequence = key.split("");
        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < keySequence.length; i++) {
            if (isOperator(keySequence[i])) {
                String variable = "";
                String operator = "";
                if (!keySequence[i].equals("-") && !keySequence[i].equals("+")) {
                    if (!bldr.isEmpty()) {
                        variableExtractor(bldr.toString());
                        bldr.setLength(0);
                    }
                }
                switch (keySequence[i]) {
                    case "=", "<", ">", "!" -> {
                        if (i+1 < keySequence.length && keySequence[i+1].equals("=")) {
                            operator = keySequence[i]+keySequence[i+1];
                            i = i + 1;
                        } else if (keySequence[i].equals("=")) {
                            throw new ParseException("'=' operator not supported in this context!", 0);
                        } else {
                            operator = keySequence[i];
                        }
                    }
                    case "|", "&", "^" -> {
                        if (i+1 < keySequence.length && keySequence[i+1].equals(keySequence[i])) {
                            operator = keySequence[i]+keySequence[i+1];
                            i = i + 1;
                        } else {
                            throw new ParseException("'"+keySequence[i]+"' boolean operator must come in pairs!", 0);
                        }
                    }
                    case "+", "-" -> {
                        if (i+1 < keySequence.length && keySequence[i+1].equals(keySequence[i])) {
                            bldr.append(keySequence[i]);
                            bldr.append(keySequence[i]);
                            i = i + 1;
                        } else {
                            operator = keySequence[i];
                        }
                    }
                    default -> {
                        operator = keySequence[i];

                    }
                }
                if (!bldr.isEmpty() && variableOperatorPassThroughPattern.matcher(bldr.toString()).find()) {
                    variableExtractor(bldr.toString());
                    bldr.setLength(0);
                }
                if (!operator.isEmpty()) {
                    appender(operator);
                    if (logContent) {
                        System.out.println("Operator detected!\nExtracted value: "+operator);
                    }
                }
            } else {
                bldr.append(keySequence[i]);
            }
        }
        if (!bldr.isEmpty()) {
            variableExtractor(bldr.toString());
            bldr.setLength(0);
        }
    }

    private void variableExtractor(String key) throws ParseException {
        String[] allowedOperators = {"+", "-"};
        CompoundAssignmentTypes compoundMode = null;
        boolean eagerWriting = false;

        LinkedList<String> keySequence = new LinkedList<>(Arrays.asList(key.split("")));
        for (String allowedOperator : allowedOperators) {
            if (keySequence.size() > 1) {
                if (keySequence.get(0).equals(allowedOperator) && keySequence.get(1).equals(allowedOperator)) {
                    eagerWriting = true;
                    compoundMode = keySequence.getFirst().equals("+") ? CompoundAssignmentTypes.ADD : CompoundAssignmentTypes.SUBTRACT;
                    keySequence.removeFirst();
                    keySequence.removeFirst();
                } else if (keySequence.getLast().equals(allowedOperator) && keySequence.get(keySequence.size() - 2).equals(allowedOperator)) {
                    compoundMode = keySequence.getLast().equals("+") ? CompoundAssignmentTypes.ADD : CompoundAssignmentTypes.SUBTRACT;
                    keySequence.removeLast();
                    keySequence.removeLast();
                }
            }
        }

        StringBuilder keybldr = new StringBuilder(keySequence.size());
        for (String character : keySequence) {
            keybldr.append(character);
        }
        key = keybldr.toString();

        if (Accessor.getInstance().doesExist(key)) {
            if (compoundMode != null) {
                String value = Accessor.getInstance().compoundModification(
                        key,
                        compoundMode,
                        "1",
                        eagerWriting
                ).toString();
                appender(value);
                if (logContent) {
                    System.out.println("Variable with compound assignment detected!\nExtracted key: "+key+", Extracted value: "+value+" compound mode: "+compoundMode.toString()+" eagerWriting: "+eagerWriting);
                }
            } else {
                String value = Accessor.getInstance().getValue(key).toString();
                appender(value);
                if (logContent) {
                    System.out.println("Variable detected!\nExtracted key: "+key+", Extracted value: "+value);
                }
            }
        } else {
            throw new NullPointerException("Variable "+key+" does not exist!");
        }
    }

    private int typeExtractor(int index, String[] iterable, Predicate<String> condition, boolean stringExtract) {
        StringBuilder currentValue = new StringBuilder();
        for (int i = (stringExtract ? index+1 : index); i < iterable.length; i++) {
            if (condition.test(iterable[i])) {
                appender(currentValue.toString());
                return stringExtract ? i : i-1;
            } else {
                currentValue.append(iterable[i]);
            }
            index = i;
        }
        appender(currentValue.toString());
        return index;
    }

    private void precedencyChecker() {
        if (logContent) {
            System.out.println("\nEvaluator calculation log: ");
            System.out.print("Base problem: ");
            printContents(true);
        }
        for (int p = 0; p < precedency.length; p++) {
            if (logContent) {
                System.out.println("\nLooking for layer "+p+" operators...");
            }
            while (containsOperatorInLayer(strippedProblem, p)) {
                for (int i = 0; i < strippedProblem.size(); i++) {
                    String result;

                    if (isOperatorInLayer(strippedProblem.get(i), p)) {
                        if (p == precedency.length - 1) {
                            if (isOperatorInLayer(strippedProblem.get(i + 1), p)) {
                                String sign = setSignedType(strippedProblem.get(i), strippedProblem.get(i+1));
                                strippedProblem.remove(i);
                                strippedProblem.remove(i);
                                strippedProblem.add(i, sign);
                                if (logContent) {
                                    printContents(false);
                                    System.out.println(" - Prefix signs aligned");
                                }
                                break;
                            }
                        }
                        result = calculate(
                                    (i == 0 ? null : strippedProblem.get(i - 1)),
                                    strippedProblem.get(i + 1),
                                    strippedProblem.get(i)
                                );
                        for (int j = 0; j < (i==0 ? 2 : 3); j++) {
                            strippedProblem.remove((i==0 ? i : i - 1));
                        }
                        strippedProblem.add((i==0 ? i : i - 1),result);
                        break;
                    }
                }
            }
        }
    }

    private boolean isOperator(String value) {
        for (String operator : operators) {
            if (operator.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOperatorInLayer(String value, int layer) {
        for (int i = 0; i < precedency[layer].length; i++) {
            if (precedency[layer][i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOperatorInLayer(LinkedList<String> value, int layer) {
        for (String digit : value) {
            if (isOperatorInLayer(digit, layer)) {
                return true;
            }
        }
        return false;
    }

    private String calculate(String a, String b, String operator) throws ArithmeticException {
        String result;
        String operation;
        switch (operator) {
            case "+":
                try {
                    boolean parseErrorForAValue = false;
                    try {
                        Double.parseDouble(a);
                    } catch (NullPointerException e) {
                        parseErrorForAValue = true;
                    }
                    result = Double.toString((parseErrorForAValue ? 0.0 : Double.parseDouble(a)) + Double.parseDouble(b));
                    operation = "Addition";
                } catch (NumberFormatException e) {
                    result = a+b;
                    operation = "String Chaining";
                }
                break;
            case "-":
                boolean parseErrorForAValue = false;
                try {
                    Double.parseDouble(a);
                } catch (NullPointerException e) {
                    parseErrorForAValue = true;
                }
                result = Double.toString((parseErrorForAValue ? 0.0 : Double.parseDouble(a)) - Double.parseDouble(b));
                operation = "Subtraction";
                break;
            case "*":
                result = Double.toString(Double.parseDouble(a) * Double.parseDouble(b));
                operation = "Multiplication";
                break;
            case "/":
                if (Double.parseDouble(b) == 0) {
                    throw new ArithmeticException("Yo mama(divByZero)");
                }
                result = Double.toString(Double.parseDouble(a) / Double.parseDouble(b));
                operation = "Division";
                break;
            case "==":
                try {
                    result = Boolean.toString(Double.parseDouble(a) == Double.parseDouble(b));
                    operation = "Numeric comparison";
                } catch (NumberFormatException e) {
                    result = Boolean.toString(a.equals(b));
                    operation = "String comparison";
                }

                break;
            case "<":
                result = Boolean.toString(Double.parseDouble(a) < Double.parseDouble(b));
                operation = "Smaller than comparison";
                break;
            case ">":
                result = Boolean.toString(Double.parseDouble(a) > Double.parseDouble(b));
                operation = "Greater than comparison";
                break;
            case ">=":
                result = Boolean.toString(Double.parseDouble(a) >= Double.parseDouble(b));
                operation = "Greater or equals comparison";
                break;
            case "<=":
                result = Boolean.toString(Double.parseDouble(a) <= Double.parseDouble(b));
                operation = "Smaller or equals comparison";
                break;
            case "!=":
                try {
                    result = Boolean.toString(Double.parseDouble(a) != Double.parseDouble(b));
                    operation = "Negating numeric comparison";
                } catch (NumberFormatException e) {
                    result = Boolean.toString(!a.equals(b));
                    operation = "Negating string comparison";
                }

                break;
            case "||":
                result = Boolean.toString(Boolean.parseBoolean(a) || Boolean.parseBoolean(b));
                operation = "Logical OR";
                break;
            case "&&":
                result = Boolean.toString(Boolean.parseBoolean(a) && Boolean.parseBoolean(b));
                operation = "Logical AND";
                break;
            case "^^":
                result = Boolean.toString(Boolean.parseBoolean(a) != Boolean.parseBoolean(b));
                operation = "Logical XOR";
                break;
            default:
                result = null;
                operation = "null";
        }
        if (logContent) {
            printContents(false);
            System.out.println(" - "+operation+" performed");
        }
        return result;
    }

    private String setSignedType(String a, String b) {
        if (a.equals(b)) {
            return "+";
        } else {
            return "-";
        }
    }
    
    private void appender(String value) {
        strippedProblem.add(value);
    }

    private void printContents(boolean lineBreak) {
        System.out.print("[");
        for (String i : strippedProblem) {
            System.out.print(i+", ");
        }
        if (lineBreak) {
            System.out.print("]\n");
        } else {
            System.out.print("]");
        }
    }
}