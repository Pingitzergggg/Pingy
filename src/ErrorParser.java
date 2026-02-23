import java.text.ParseException;

public class ErrorParser {

    public static void handle(Exception exception, int lineIndex) throws RuntimeException {
        String header = "Error at line ";
        StringBuilder bldr = new StringBuilder();
        if (exception instanceof NonexistentVariableException) {
            header = "NonexistentVariableError at line ";
        } else if (exception instanceof CompoundAssignmentException) {
            header = "CompoundAssignmentError at line ";
        } else if (exception instanceof IllegalVariableNameException) {
            header = "IllegalVariableNameError at line ";
        } else if (exception instanceof ParseException) {
            header = "ParseError at line ";
        }
        bldr.append(header);
        bldr.append(lineIndex);
        bldr.append(":\n");
        bldr.append(exception.toString());

        Pool.getInstance().printToErrorStream(bldr.toString());
        throw new RuntimeException(exception.toString());
    }
}
