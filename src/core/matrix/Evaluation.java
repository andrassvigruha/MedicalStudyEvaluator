package core.matrix;

public enum Evaluation {

    PASSED {
        @Override public String displayString() { return "Passed"; }
    },
    FAILED {
        @Override public String displayString() { return "Failed"; }
    },
    UNDECIDABLE {
        @Override public String displayString() { return "Undecidable"; }
    },
    NONE {
        @Override public String displayString() { return "<empty>"; }
    };
    
    public abstract String displayString();
}
