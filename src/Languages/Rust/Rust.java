package Languages.Rust;

import Languages.Code;
import Transpiler.AbstractSyntaxTree;
import Transpiler.NodeType;
import Transpiler.RuleType;

import java.io.StringReader;
import java.util.ArrayList;

public class Rust extends Code {

    public Rust(String code) {
        super(code);
    }

    public Rust(AbstractSyntaxTree ast) {
        super(ast);
    }

    @Override
    public AbstractSyntaxTree parseToAST() {
        /**
         * This function parses the given program code with Rust Parser.
         * @return  Parsed AST (Abstract Syntax Tree) of the given Program.
         */
        RustLexer lexer = new RustLexer(new StringReader(this.code));
        RustParser parser = new RustParser(lexer);
        try {
            Object result = parser.parse().value;
            this.ast = (AbstractSyntaxTree) result;
            return this.ast;
        } catch (Exception e) {
            System.err.println("Parser error: " + e.getMessage());
            this.ast = null;
            return null;
        }
    }

    @Override
    public String generateCode() {
        /**
         * This function reverses the parsing process to generate a string code for the given AST.
         * @return  The generated Rust program code for the given AST.
         */
        return generate(this.ast).toString();
    }

    private StringBuilder generate(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        if (ast == null) {
            return res;
        }

        if (ast.getType().equals(NodeType.PROGRAM)) {
            res.append(program(ast));
        } else if (ast.getType().equals(NodeType.STATEMENTS)) {
            res.append(statements(ast));
        } else if (ast.getType().equals(NodeType.STATEMENT)) {
            res.append(statement(ast));
        } else if (ast.getType().equals(NodeType.DISJUNCTION)) {
            res.append(disjunction(ast));
        } else if (ast.getType().equals(NodeType.CONJUNCTION)) {
            res.append(conjunction(ast));
        } else if (ast.getType().equals(NodeType.INVERSION)) {
            res.append(inversion(ast));
        } else if (ast.getType().equals(NodeType.COMPARISON)) {
            res.append(comparison(ast));
        } else if (ast.getType().equals(NodeType.SUM)) {
            res.append(sum(ast));
        }

        return res;
    }




    private StringBuilder conjunction(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            res.append(conjunction(ast.getChildren().get(0)))
                    .append(" && ")
                    .append(inversion(ast.getChildren().get(1)));
        } else {
            res.append(inversion(ast.getChildren().getFirst()));
        }
        return res;
    }




    private StringBuilder inversion(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        AbstractSyntaxTree child = ast.getChildren().getFirst();
        if (child.getType().equals(NodeType.INVERSION)) {
            res.append("!").append(inversion(child));
        }
        else {
            res.append(comparison(child));
        }

        return res;
    }

    private StringBuilder disjunction(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            AbstractSyntaxTree d = ast.getChildren().get(0),
                    c = ast.getChildren().get(1);

            res.append(disjunction(d)).append(" || ").append(conjunction(c));
        }

        else {
            AbstractSyntaxTree c = ast.getChildren().getFirst();
            res.append(conjunction(c));
        }

        return res;
    }

    private String toString(AbstractSyntaxTree ast) {
        String s;
        if (ast.getSubType() == null) {
            s = ast.getLexeme();
        }
        else {
            s = ast.getSubType().toString();
        }
        return "(" + ast.getType().toString()+ "+" + s +  ")";
    }

    private StringBuilder comparison(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        AbstractSyntaxTree child = ast.getChildren().getFirst();

        if (child.getType().equals(NodeType.SUM)) {
            res.append(sum(child));
        }

        else {
            AbstractSyntaxTree sum1 = child.getChildren().getFirst(),
                    sum2 = child.getChildren().getLast();

            res.append(generate(sum1));
            if (child.getType().equals(NodeType.EQ))
                res.append(" == ");
            else if (child.getType().equals(NodeType.LT))
                res.append(" < ");
            else if (child.getType().equals(NodeType.GT))
                res.append(" > ");

            res.append(generate(sum2));
        }

        return res;
    }

    private StringBuilder sum(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append(term(ast.getChildren().getFirst()));
        }

        else {
            AbstractSyntaxTree s = ast.getChildren().getFirst(),
                    t = ast.getChildren().getLast();

            res.append(sum(s));
            if (ast.getSubType().equals(RuleType.ADD)) {
                res.append(" + ");
            }
            else {
                res.append(" - ");
            }
            res.append(term(t));
        }

        return res;
    }

    private StringBuilder num(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        res.append(ast.getLexeme());
        return res;
    }

    private StringBuilder primary(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        if (ast.getSubType().equals(RuleType.VARIABLE)) {
            res.append(id(ast.getChildren().getFirst()));
        }

        else {
            res.append(num(ast.getChildren().getFirst()));
        }

        return res;
    }

    private StringBuilder factor(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append(primary(ast.getChildren().getFirst()));
        }

        else if (ast.getSubType().equals(RuleType.PAR)) {
            res.append("(");
            res.append(disjunction(ast.getChildren().getFirst()));
            res.append(")");
        }
        else {
            if (ast.getSubType().equals(RuleType.NEGATIVE))
                res.append("-").append(primary(ast.getChildren().getFirst()));
            else
                res.append("+").append(primary(ast.getChildren().getFirst()));
        }

        return res;
    }

    private StringBuilder term(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append(modulo(ast.getChildren().getFirst()));
        }

        else {
            AbstractSyntaxTree t = ast.getChildren().getFirst(),
                    m = ast.getChildren().getLast();

            res.append(term(t));

            if (ast.getSubType().equals(RuleType.TIMES))
                res.append(" * ");
            else
                res.append(" / ");
            res.append(modulo(m));
        }

        return res;
    }

    private StringBuilder modulo(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            res.append(modulo(ast.getChildren().getFirst()));
            res.append(" % ");
            res.append(factor(ast.getChildren().getLast()));
        }

        else {
            res.append(factor(ast.getChildren().getFirst()));
        }

        return res;
    }

    private StringBuilder goThrowChildren(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        for (AbstractSyntaxTree child : ast.getChildren()) {
            res.append(generate(child));
        }

        return res;
    }

    private StringBuilder assignment(AbstractSyntaxTree ast, boolean declared) {
        StringBuilder res = new StringBuilder();

        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.ID)) {
                if (!declared) {
                    res.append("let ");
                }

                res.append(id(child));
                res.append(" = ");
                continue;
            }

            res.append(generate(child));
        }
        res.append(";\n");
        return res;
    }

    private StringBuilder id(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        res.append(ast.getLexeme());
        return res;
    }

    private StringBuilder program(AbstractSyntaxTree ast)  {
        StringBuilder res = new StringBuilder("fn ");
        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.ID)) {
                res.append(child.getLexeme());
                res.append("() {\n");
                continue;
            }

            res.append(generate(child));
        }

        res.append("\n}");
        return res;
    }

    private StringBuilder statements(AbstractSyntaxTree ast) {
        return goThrowChildren(ast);
    }

    private StringBuilder statement(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.DECLARATION)) {
                res.append(declaration(child));
            }

            else if (child.getType().equals(NodeType.ASSIGNMENTS)) {
                res.append(assignments(child, true));
            }

            else if (child.getType().equals(NodeType.IF)) {
                res.append(if_assignment(child));
            }

            else if (child.getType().equals(NodeType.WHILE)) {
                res.append(while_statement(child));
            }

            else if (child.getType().equals(NodeType.SWITCH)) {
                res.append(switchStatement(child));
            }

            else if (child.getType().equals(NodeType.PRINT)) {
                res.append(print(child));
            }

            else if (child.getType().equals(NodeType.ASSIGNMENT)) {
                res.append(assignment(child, true));
            }

            else if (child.getType().equals(NodeType.DISJUNCTION)) {

                if (ast.getSubType().equals(RuleType.PRINT)) {
                    res.append("println!(").append(disjunction(child)).append(");\n");
                }
                else
                    res.append(disjunction(child));
            }

            else {
                System.out.println(child.getType().toString());
                throw new AssertionError();
            }
        }

        if (ast.getChildren().isEmpty()) {
            if (ast.getSubType().equals(RuleType.BREAK)) {
                res.append("break;\n");
            }

            else if (ast.getSubType().equals(RuleType.CONTINUE)) {
                res.append("continue;\n");
            }
        }

        return res;
    }

    private StringBuilder print(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.DISJUNCTION)) {
                res.append("println!(");
                res.append(disjunction(child));
                res.append(");\n");
            }

            else {
                res.append(print(child));
            }
        }

        return res;
    }

    private StringBuilder switchStatement(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        AbstractSyntaxTree i = ast.getChildren().getFirst(),
                c = ast.getChildren().getLast();

        res.append("match ");
        res.append(id(i));
        res.append(" {\n");
        res.append(cases(c));
        res.append("}\n");
        return res;
    }

    private StringBuilder cases(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            AbstractSyntaxTree f = ast.getChildren().getFirst();
            res.append("_ => ");
            if (f.getSubType().equals(RuleType.MULTI)) {
                res.append("{\n");
                res.append(followStatement(f)).append("};\n");
            }
            else {
                res.append(followStatement(f).append("\n"));
            }
            return res;
        }

        AbstractSyntaxTree n = ast.getChildren().get(0),
                f = ast.getChildren().get(1),
                c = ast.getChildren().get(2);

        if (n.getType().equals(NodeType.OPTIONS)) {

            ArrayList<String> op = options(n);
            for (int i = op.size()-1; i > 0; i--) {
                res.append(op.get(i)).append("|");
            }
            res.append(op.getFirst());

            if (f.getSubType().equals(RuleType.MULTI)) {
                res.append(" => {\n");
                res.append(followStatement(f)).append("\n},\n");
            }
            else {
                res.append(" => ").append(followStatement(f)).append(",\n");
            }
            res.append(cases(c));
        }
        else {
            res.append(num(n)).append(" => ");
            if (f.getSubType().equals(RuleType.MULTI)) {
                res.append("{\n");
                res.append(followStatement(f)).append("\n},\n");
            }
            else {
                res.append(followStatement(f)).append(",\n");
            }

            res.append(cases(c));
        }

        return res;
    }

    private ArrayList<String> options(AbstractSyntaxTree ast) {
        ArrayList<String> arr = new ArrayList<>();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            AbstractSyntaxTree o = ast.getChildren().get(0),
                    n = ast.getChildren().get(1);
            arr.add(num(n).toString());
            arr.addAll(options(o));
        }

        else {
            AbstractSyntaxTree n = ast.getChildren().getFirst();
            arr.add(num(n).toString());
        }

        return arr;
    }

    private StringBuilder while_statement(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        AbstractSyntaxTree dis = ast.getChildren().getFirst(),
                following = ast.getChildren().getLast();

        res.append("while( ");
        res.append(disjunction(dis));
        res.append(") {\n");
        res.append(followStatement(following));
        res.append("}\n");

        return res;
    }

    private StringBuilder followStatement(AbstractSyntaxTree ast) {
        return goThrowChildren(ast);
    }

    private StringBuilder if_assignment(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        AbstractSyntaxTree dis = ast.getChildren().get(0),
                follow1 = ast.getChildren().get(1),
                follow2 = ast.getChildren().get(2);

        res.append("if (");
        res.append(disjunction(dis));
        res.append(") {\n");
        res.append(followStatement(follow1));
        res.append("\n} else {\n");
        res.append(followStatement(follow2));
        res.append("}\n");
        return res;
    }

    private StringBuilder declaration(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        AbstractSyntaxTree child = ast.getChildren().getFirst();

        if (child.getType().equals(NodeType.ASSIGNMENTS))
            res.append(assignments(child, false));
        else
            res.append(assignment(child, false));

        return res;
    }

    private StringBuilder assignments(AbstractSyntaxTree ast, boolean declared) {
        StringBuilder res = new StringBuilder();

        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.ASSIGNMENT)) {
                res.append(assignment(child, declared));
            }
            else {
                res.append(assignments(child, declared));
            }
        }

        return res;
    }
}