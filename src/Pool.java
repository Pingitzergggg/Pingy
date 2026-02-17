import java.util.LinkedList;

public class Pool {
    private final LinkedList<String> output = new LinkedList<>();
    private final LinkedList<String> error = new LinkedList<>();
    private final LinkedList<String> debug = new LinkedList<>();

    private final static Pool instance = new Pool();

    private Pool() {
        printAll("Pingy interpreter running on version 1.1 (Java build)\n");
    }

    public static Pool getInstance() {return instance;}

    public LinkedList<String> getOutputStream() {return this.output;}
    public LinkedList<String> getErrorStream() {return this.error;}
    public LinkedList<String> getDebugStream() {return this.debug;}

    public void printToOutputStream(Object value) {
        output.add(value.toString());
    }

    public void printToErrorStream(Object value) {
        error.add(value.toString());
    }

    public void printToDebugStream(Object value) {
        debug.add(value.toString());
    }

    public void printAll(Object value) {
        output.add(value.toString());
        error.add(value.toString());
        debug.add(value.toString());
    }

    public void showOutputStream() {
        for (Object value : output) {
            System.out.println(value);
        }
    }

    public void showErrorStream() {
        for (Object value : error) {
            System.out.println(value);
        }
    }

    public void showDebugStream() {
        for (Object value : debug) {
            System.out.println(value);
        }
    }
}
