package pingy;

import java.text.ParseException;
import java.util.LinkedList;
import java.lang.reflect.Field;
import java.util.HashMap;

interface IRegister {
    void start() throws RuntimeException, InterruptedException;
}

public class Register implements IRegister {
    private static final Accessor accessor = Accessor.getInstance();
    private static final Pool standardPool = Pool.getInstance();
    private LinkedList<String> instructions = new LinkedList<>();
    private int index = 0;

    public Register(LinkedList<String> instructions) {
        this.instructions = instructions;
    }

    @Override
    public void start() throws RuntimeException, InterruptedException {
        if (Thread.currentThread().isInterrupted()) throw new InterruptedException("Interpreter terminated!");
        for (; index < instructions.size(); ++index) {
            execute(instructions.get(index));
        }
    }

    private void execute(String instruction) throws RuntimeException, InterruptedException {
        String[] instructionSet = instruction.strip().split(" ");
        if (instruction.contains("=") && !instruction.startsWith("var") && !instruction.contains("==") && !instruction.contains("!=") && !instruction.contains("<=") && !instruction.contains(">=")) {
            String[] parts = instruction.split("=", 2);
            String varName = parts[0].strip();
            if (accessor.doesExist(varName)) {
                try {
                    Evaluator evaluator = new Evaluator(parts[1].strip());
                    accessor.overWriteValue(varName, evaluator.eval());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        
            switch (instructionSet[0]) {
                case "var" -> {
                    try {
                        if (instruction.contains("=")) {
                            Evaluator evaluator = new Evaluator(instruction.split("=")[1].strip());
                            accessor.storeValue(
                                    instructionSet[1],
                                    instructionSet[2],
                                    evaluator.eval()
                            );
                        } else {
                            accessor.declare(
                                    instructionSet[1],
                                    instructionSet[2]
                            );
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e.toString());
                    }
                }
                case "if" -> {
                    Brancher branchExecutor = new Brancher(index, instructions);
                    branchExecutor.execute();
                    this.index = branchExecutor.getIndex();
                }
                case "loop" -> {
                    try {
                        Cycler cycleExecutor = new Cycler(index, instructions);
                        cycleExecutor.execute();
                        this.index = cycleExecutor.getIndex();
                    } catch (ParseException e) {
                        throw new RuntimeException(e.toString());
                    }
                }
                case "print" -> {
                    Evaluator evaluator = new Evaluator(instruction.split("print")[1].strip());
                    try {
                        standardPool.printToOutputStream(evaluator.eval().replace("\"", "")+"\n");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "while" -> {
                    if (!Inspector.lookAhead(index, instructions).equals("{")) {
                        throw new RuntimeException("while clause must be followed by '{'");
                    }
                    ClauseExtractor scope = new ClauseExtractor(index + 1, instructions, "{", "}");
                    scope.extract();
                    String condition = extractCondition(instruction);
                    try {
                        while (true) {
                            Evaluator conditionEval = new Evaluator(condition);
                            if (!Boolean.parseBoolean(conditionEval.eval())) {
                                break;
                            }
                            Register engine = new Register(scope.getOutput());
                            engine.start();
                            for (Types type : Types.values()) {
                                if (type == Types.STRING || type == Types.BOOL) continue;
                                try {
                                    Field storedField = Accessor.class.getDeclaredField("stored");
                                    storedField.setAccessible(true);
                                    HashMap<Types, HashMap<String, Object>> stored = (HashMap<Types, HashMap<String, Object>>) storedField.get(accessor);
                                    HashMap<String, Object> typeMap = stored.get(type);
                                    for (String key : typeMap.keySet()) {
                                        Object val = typeMap.get(key);
                                        if (val instanceof String) {
                                            String s = (String) val;
                                            Object parsed = switch (type) {
                                                case BYTE -> Byte.parseByte(s);
                                                case SHORT -> Short.parseShort(s);
                                                case INT -> Integer.parseInt(s);
                                                case LONG -> Long.parseLong(s);
                                                case FLOAT -> Float.parseFloat(s);
                                                case DOUBLE -> Double.parseDouble(s);
                                                default -> val;
                                            };
                                            typeMap.put(key, parsed);
                                        }
                                    }
                                } catch (ReflectiveOperationException | NumberFormatException ignored) {}
                            }
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    this.index = scope.getIndex();
                }
                default -> {
                    Evaluator evaluator = new Evaluator(instruction);
                    try {
                        evaluator.eval();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
    }

    private String extractCondition(String instruction) {
        StringBuilder condition = new StringBuilder();
        String[] segments = instruction.strip().split(" ");
        for (int i = 1; i < segments.length; i++) {
            condition.append(segments[i]);
        }
        return condition.toString();
    }
}
