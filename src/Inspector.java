package pingy;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Inspector {

    public static String lookAhead(int index, String[] sequence) {
        try {
            return seek(index+1, sequence);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String lookAhead(int index, LinkedList<String> sequence) {
        try {
            return seek(index+1, sequence);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String lookAheadThrow(int index, String[] sequence) throws IndexOutOfBoundsException {
        try {
            return seek(index+1, sequence);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index "+(index+1)+" is out of bounds for this sequence!");
        }
    }

    public static String lookAheadThrow(int index, LinkedList<String> sequence) throws IndexOutOfBoundsException {
        try {
            return seek(index+1, sequence);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index "+(index+1)+" is out of bounds for this sequence!");
        }
    }

    public static String lookBehind(int index, String[] sequence) {
        try {
            return seek(index-1, sequence);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String lookBehind(int index, LinkedList<String> sequence) {
        try {
            return seek(index-1, sequence);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public static String lookBehindThrow(int index, String[] sequence) throws IndexOutOfBoundsException {
        try {
            return seek(index-1, sequence);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index "+(index-1)+" is out of bounds for this sequence!");
        }
    }

    public static String lookBehindThrow(int index, LinkedList<String> sequence) throws IndexOutOfBoundsException {
        try {
            return seek(index-1, sequence);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Index "+(index-1)+" is out of bounds for this sequence!");
        }
    }

    public static String seek(int index, String[] sequence) throws IndexOutOfBoundsException {
        try {
            return sequence[index];
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(e.toString());
        }
    }

    public static String seek(int index, LinkedList<String> sequence) throws IndexOutOfBoundsException {
        try {
            return sequence.get(index);
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(e.toString());
        }
    }
}
