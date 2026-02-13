import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Accessor {
    public static final Pattern namingRestrictionPattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private final HashMap<Types, HashMap<String, Object>> stored = new HashMap<>();
    private static final Accessor instance = new Accessor();

    private Accessor() {
        for (Types type : Types.values()) {
            stored.put(type, new HashMap<>());
        }
    }

    public static Accessor getInstance() {
        return instance;
    }

    public Object getValue(String key) throws NullPointerException {
        for (HashMap<String, Object> values: stored.values()) {
            if (values.get(key) != null) {
                return values.get(key);
            }
        }
        throw new NullPointerException("Variable "+key+" does not exist!");
    }

    public Types getType(String key) {
        Types currentType = null;
        for (Types keys: stored.keySet()) {
            currentType = keys;
           if (stored.get(keys).get(key) != null) {
               return currentType;
           }
        }
        throw new NullPointerException("Variable "+key+" does not exist!");
    }

    public boolean doesExist(String key) {
        try {
            getValue(key);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void declare(String type, String key) throws ParseException {
        String defaultValue = null;
        switch (type) {
            case "@byte", "@short", "@int", "@long" -> defaultValue = "0";
            case "@float", "@double" -> defaultValue = "0.0";
            case "@string" -> defaultValue = "";
            case "@bool" -> defaultValue = "false";
            default -> throw new ParseException("Nonexisting type declaration "+type+"!", 0);
        }
        storeValue(type, key, defaultValue);
    }

    public void storeValue(String type, String key, String value) throws ParseException, IllegalArgumentException {
        if (!namingRestrictionPattern.matcher(key).matches()) throw new IllegalArgumentException("Variable name "+key+" is illegal");
        try {
            getValue(key);
            throw new IllegalArgumentException("Variable "+key+" already exists!");
        } catch (NullPointerException error) {
            switch (type) {
                case "@bool":
                    if (value.equals("false") || value.equals("true")) {
                        stored.get(Types.BOOL).put(key, Boolean.parseBoolean(value));
                    } else {
                        throw new ParseException("@bool type can only have values 'true' and 'false'!", 0);
                    }
                    break;
                case "@string":
                    String[] charSequence = value.split("");
                    if (charSequence[charSequence.length - 1].equals("\"") && charSequence[0].equals("\"")) {
                        stored.get(Types.STRING).put(key, value);
                    } else {
                        throw new ParseException("@string type must be literal between \" characters!", 0);
                    }
                    break;
                default:
                    try {
                        switch (type) {
                            case "@byte" -> stored.get(Types.BYTE).put(key, Byte.parseByte(value));
                            case "@short" -> stored.get(Types.SHORT).put(key, Short.parseShort(value));
                            case "@int" -> stored.get(Types.INT).put(key, Integer.parseInt(value));
                            case "@long" -> stored.get(Types.LONG).put(key, Long.parseLong(value));
                            case "@float" -> stored.get(Types.FLOAT).put(key, Float.parseFloat(value));
                            case "@double" -> stored.get(Types.DOUBLE).put(key, Double.parseDouble(value));
                            default -> throw new ParseException("Nonexisting type declaration "+type+"!", 0);
                        }
                    } catch (NumberFormatException e) {
                        throw new ParseException(e.toString(), 0);
                    }
                    break;
            }
        }
    }

    public Object overWriteValue(String key, String value) throws ParseException {
        try {
            switch (getType(key)) {
                case BYTE -> Byte.parseByte(value);
                case SHORT -> Short.parseShort(value);
                case INT -> Integer.parseInt(value);
                case LONG -> Long.parseLong(value);
                case FLOAT -> Float.parseFloat(value);
                case DOUBLE -> Double.parseDouble(value);
                case BOOL -> {if(!(value.equals("false") || value.equals("true"))) throw new NumberFormatException("Boolean format exception!");}
            }
            Types type = getType(key);
            stored.get(getType(key)).remove(key);
            stored.get(type).put(key, value);
            return getValue(key);
        } catch (NumberFormatException e) {
            throw new ParseException("Variable "+key+" can only store "+getType(key)+" types!", 0);
        }
    }

    public Object compoundModification(String key, CompoundAssignmentTypes type, String quantity, boolean eagerWriting) throws ParseException, ArithmeticException {
        double original;
        double modified;
        switch(getType(key)) {
            case BYTE:
                original = (byte) getValue(key);
                modified = (byte) assignNumericValue(original, Byte.parseByte(quantity), type);
                overWriteValue(key, Byte.toString((byte) modified));
                return eagerWriting ? getValue(key) : original;
            case SHORT:
                original = (short) getValue(key);
                modified = (short) assignNumericValue(original, Short.parseShort(quantity), type);
                overWriteValue(key, Short.toString((short) modified));
                return eagerWriting ? getValue(key) : original;
            case INT:
                original = (int) getValue(key);
                modified = (int) assignNumericValue(original, Integer.parseInt(quantity), type);
                overWriteValue(key, Integer.toString((int) modified));
                return eagerWriting ? getValue(key) : original;
            case LONG:
                original = (long) getValue(key);
                modified = (long) assignNumericValue(original, Long.parseLong(quantity), type);
                overWriteValue(key, Long.toString((long) modified));
                return eagerWriting ? getValue(key) : original;
            case FLOAT:
                original = (float) getValue(key);
                modified = (float) assignNumericValue(original, Float.parseFloat(quantity), type);
                overWriteValue(key, Float.toString((float) modified));
                return eagerWriting ? getValue(key) : original;
            case DOUBLE:
                original = (double) getValue(key);
                modified = (double) assignNumericValue(original, Double.parseDouble(quantity), type);
                overWriteValue(key, Double.toString((double) modified));
                return eagerWriting ? getValue(key) : original;
            case STRING:
                overWriteValue(
                        key,
                        assignStringValue(getValue(key).toString(), quantity, type)
                );
                return getValue(key);
            case BOOL: throw new IllegalArgumentException("Cannot perform CompoundAssignment on @bool type!");
            default: throw new IllegalArgumentException("Nonexistent CompoundAssignment chosen!");
        }
    }

    private double assignNumericValue(double value, double quantity, CompoundAssignmentTypes type) throws IllegalArgumentException {
        switch (type) {
            case ADD -> { return value + quantity; }
            case SUBTRACT -> { return value - quantity; }
            case MULTIPLY -> { return value * quantity; }
            case DIVIDE -> { return value / quantity; }
            case MODULO -> { return value % quantity; }
            default -> throw new IllegalArgumentException("Nonexistent CompoundAssignment chosen!");
        }
    }

    private String assignStringValue(String value, String assignable, CompoundAssignmentTypes type) throws IllegalArgumentException {
        StringBuilder bldr = new StringBuilder();
        switch (type) {
            case ADD -> {
                bldr.append(value);
                bldr.append(assignable);
                return bldr.toString();
            }
            case SUBTRACT, MULTIPLY, DIVIDE, MODULO -> {
                throw new IllegalArgumentException("Type @string cannot implement CompoundAssignment: "+type);
            }
            default -> throw new IllegalArgumentException("Nonexistent CompoundAssignment chosen!");
        }
    }

    public void printVariableTable() {
        for (Types type : stored.keySet()) {
            printVariablesForType(type);
        }
    }

    private void printVariablesForType(Types type) {
        LinkedList<String> rows = new LinkedList<>();
        for (String key : stored.get(type).keySet()) {
            StringBuilder row = new StringBuilder();
            row.append("| ");
            row.append(key);
            row.append(" -> ");
            row.append(stored.get(type).get(key));
            row.append(" |");
            rows.add(row.toString());
        }
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < 49-type.toString().length(); ++i) separator.append("-");
        System.out.println("@"+type.toString().toLowerCase()+separator);
        for (String row : rows) {
            System.out.println("\t"+row);
        }
    }
}
