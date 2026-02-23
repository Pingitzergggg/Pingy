import java.util.LinkedList;

public class ClauseExtractor {
    private final LinkedList<String> output = new LinkedList<>();
    private String openingClause;
    private String closingClause;
    private int index;
    private LinkedList<String> instructions;

    public ClauseExtractor(int index, LinkedList<String> sequence, String openingClause, String closingClause) {
        this.index = index;
        this.instructions = sequence;
        this.openingClause = openingClause;
        this.closingClause = closingClause;
    }

    public void extract() throws UnresolvedClauseException {
        int unresolvedClausePairs = 0;
        for (int i = index+1; i < instructions.size(); i++) {
            if (instructions.get(i).equals(this.openingClause)) {
                unresolvedClausePairs++;
                output.add(instructions.get(i));
            } else if (instructions.get(i).equals(this.closingClause)) {
                if (unresolvedClausePairs > 0) {
                    unresolvedClausePairs--;
                    output.add(instructions.get(i));
                } else {
                    index = i;
                    return;
                }
            } else {
                output.add(instructions.get(i).strip());
            }
        }
        throw new UnresolvedClauseException("Scope missing ending character '"+closingClause+"'!");
    }

    public int getIndex() {return this.index;}
    public LinkedList<String> getOutput() {return this.output;}
}
