package Transpiler;

public enum NodeType {
    PROGRAM("program"),
    STATEMENTS("statements"),
    FOLLOW_STATEMENTS("followStatements"),
    STATEMENT("statement"),
    PRINT("print"),
    DECLARATION("declaration"),
    ASSIGNMENTS("assignments"),
    ASSIGNMENT("assignment"),
    IF("if"),
    SWITCH("switch"),
    CASES("cases"),
    CASE("case"),
    WHILE("while"),
    DISJUNCTION("disjunction"),
    CONJUNCTION("conjunction"),
    INVERSION("inversion"),
    COMPARISON("comparison"),
    EQ("eq"),
    LT("lt"),
    GT("gt"),
    SUM("sum"),
    TERM("term"),
    MODULO("modulo"),
    FACTOR("factor"),
    PRIMARY("primary"),
    ID("id"),
    NUM("num"),
    OPTIONS("options"),
    ;

    public final String label;

    NodeType(String label) {
        this.label = label;
    }
}
