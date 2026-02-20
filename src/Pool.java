package pingy;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Pool {
    private final ConcurrentLinkedQueue<String> output = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> error = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> debug = new ConcurrentLinkedQueue<>();

    private final static Pool instance = new Pool();

    private Pool() {
        clearPools();
    }

    public static Pool getInstance() {return instance;}

    public ConcurrentLinkedQueue<String> getOutputStream() {return this.output;}
    public ConcurrentLinkedQueue<String> getErrorStream() {return this.error;}
    public ConcurrentLinkedQueue<String> getDebugStream() {return this.debug;}

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

    public void clearPools() {
        output.clear();
        error.clear();
        debug.clear();
        printAll("Pingy interpreter running on version 1.1 (Java build)\n");
    }
}
