import java.text.ParseException;
import java.util.Arrays;
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

        if (!instruction.contains("#") && !instruction.contains("//")) {
            switch (instructionSet[0]) {
                case "var" -> {
                    try {
                        if (instruction.contains("=")) {
                            Evaluator evaluator = new Evaluator(instruction.split("=", 2)[1].strip(), true);
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
                    } catch (Exception e) {
                        ErrorParser.handle(e, index);
                    }
                }
                case "if" -> {
                    try {
                        Brancher branchExecutor = new Brancher(index, instructions);
                        branchExecutor.execute();
                        this.index = branchExecutor.getIndex();
                    } catch (Exception e) {
                        ErrorParser.handle(e, index);
                    }
                }
                case "loop" -> {
                    try {
                        Cycler cycleExecutor = new Cycler(index, instructions);
                        cycleExecutor.execute();
                        this.index = cycleExecutor.getIndex();
                    } catch (Exception e) {
                        ErrorParser.handle(e, index);
                    }
                }
                case "print" -> {
                    Evaluator evaluator = new Evaluator(instruction.split("print")[1].strip());
                    try {
                        standardPool.printToOutputStream(evaluator.eval().replace("\"", "")+"\n");
                    } catch (Exception e) {
                        ErrorParser.handle(e, index);
                    }
                }
                default -> {
                    if (instruction.contains("=")) {
                        try {
                            String[] parts = instruction.split("=", 2);
                            String varName = parts[0].strip();
                            if (accessor.doesExist(varName)) {
                                try {
                                    Evaluator evaluator = new Evaluator(parts[1].strip());
                                    accessor.overWriteValue(varName, evaluator.eval());
                                } catch (Exception e) {
                                    ErrorParser.handle(e, index);
                                }
                            }
                        } catch (Exception e) {
                            ErrorParser.handle(e, index);
                        }
                    } else {
                        Evaluator evaluator = new Evaluator(instruction);
                        try {
                            evaluator.eval();
                        } catch (Exception e) {
                            ErrorParser.handle(e, index);
                        }
                    }
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
