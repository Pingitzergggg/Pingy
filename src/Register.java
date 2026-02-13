import java.text.ParseException;
import java.util.LinkedList;

interface IRegister {
    void start() throws RuntimeException;
}

public class Register implements IRegister {
    private static final Accessor accessor = Accessor.getInstance();
    private LinkedList<String> instructions = new LinkedList<>();
    private int index = 0;

    public Register(LinkedList<String> instructions) {
        this.instructions = instructions;
    }

    @Override
    public void start() throws RuntimeException {
        for (; index < instructions.size(); ++index) {
            execute(instructions.get(index));
        }
    }

    private void execute(String instruction) throws RuntimeException {
        String[] instructionSet = instruction.strip().split(" ");
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
                case "print" -> {
                    Evaluator evaluator = new Evaluator(instruction.split("print")[1].strip());
                    try {
                        System.out.println(evaluator.eval());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
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
}
