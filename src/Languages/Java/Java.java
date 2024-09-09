package Languages.Java;

import Languages.Code;
import Transpiler.AbstractSyntaxTree;
import Transpiler.NodeType;
import Transpiler.RuleType;

import java.io.StringReader;
import java.util.ArrayList;

public class Java extends Code {

    public Java(String code) {
        super(code);
    }

    public Java(AbstractSyntaxTree ast) {
        super(ast);
    }

    @Override
    public AbstractSyntaxTree parseToAST() {
        /*
         * This function parses the given program code with Java Parser.
         * @return Parsed AST (Abstract Syntax Tree) of the given Program.
         */
        JavaLexer lexer = new JavaLexer(new StringReader(this.code));
        JavaParser parser = new JavaParser(lexer);
        try {
            Object result = parser.parse().value;
            this.ast = (AbstractSyntaxTree) result;
            return this.ast;
        } catch (Exception e) {
            System.out.println("Parser error: " + e.getMessage());
            this.ast = null;
            return null;
        }
    }

    @Override
    public String generateCode() {
        /*
         * This function reverses the parsing process to generate a string code for the given AST.
         * @return  The generated Java program code for the given AST.
         */
        System.out.println("------------------");
        return generate(this.ast).toString();
    }

    private StringBuilder generate(AbstractSyntaxTree ast) {
        if (ast == null) {
            return null;
        }

        StringBuilder res = new StringBuilder();
        if (ast.getType().equals(NodeType.PROGRAM)) {
            res = program(ast);
        } else if (ast.getType().equals(NodeType.STATEMENTS)) {
            res = statements(ast);
        } else if (ast.getType().equals(NodeType.STATEMENT)) {
            res = statement(ast);
        } else if (ast.getType().equals(NodeType.ASSIGNMENTS)) {
            throw new AssertionError();
        } else if (ast.getType().equals(NodeType.DISJUNCTION)) {
            res.append(disjunction(ast));
        } else if (ast.getType().equals(NodeType.CONJUNCTION)) {
            res.append(goThrowChildren(ast));
        } else if (ast.getType().equals(NodeType.INVERSION)) {
            res.append(inversion(ast));
        } else if (ast.getType().equals(NodeType.COMPARISON)) {
            res.append(comparison(ast));
        } else if (ast.getType().equals(NodeType.SUM)) {
            res.append(sum(ast));
        }

        return res;
    }

    private StringBuilder inversion(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        AbstractSyntaxTree child = ast.getChildren().getFirst();
        if (child.getType().equals(NodeType.INVERSION)) {
            res.append("!").append(inversion(child));
        } else {
            res.append(generate(child));
        }

        return res;
    }

    private StringBuilder conjunction(AbstractSyntaxTree ast) {

        StringBuilder res = new StringBuilder();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            AbstractSyntaxTree c = ast.getChildren().get(0), i = ast.getChildren().get(1);

            res.append(conjunction(c)).append(" && ").append(inversion(i));
        } else {
            AbstractSyntaxTree i = ast.getChildren().getFirst();
            res.append(inversion(i));
        }

        return res;
    }

    private StringBuilder disjunction(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            AbstractSyntaxTree d = ast.getChildren().get(0), c = ast.getChildren().get(1);

            res.append(disjunction(d)).append(" || ").append(conjunction(c));
        } else if (ast.getSubType().equals(RuleType.SINGLE)) {
            AbstractSyntaxTree c = ast.getChildren().getFirst();
//            System.out.println(toString(c));
            res.append(conjunction(c));
        } else {
            throw new AssertionError();
        }

        return res;
    }

    private StringBuilder comparison(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        AbstractSyntaxTree child = ast.getChildren().getFirst();

        if (child.getType().equals(NodeType.SUM)) {
            res.append(sum(child));
        } else {
            AbstractSyntaxTree sum1 = child.getChildren().getFirst(), sum2 = child.getChildren().getLast();

            res.append(generate(sum1));
            if (child.getType().equals(NodeType.EQ)) res.append(" == ");
            else if (child.getType().equals(NodeType.LT)) res.append(" < ");
            else if (child.getType().equals(NodeType.GT)) res.append(" > ");

            res.append(generate(sum2));
        }

        return res;
    }

    private StringBuilder sum(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append(term(ast.getChildren().getFirst()));
        } else {
            AbstractSyntaxTree s = ast.getChildren().getFirst(), t = ast.getChildren().getLast();

            res.append(sum(s));
            if (ast.getSubType().equals(RuleType.ADD)) {
                res.append(" + ");
            } else {
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
        } else {
            res.append(num(ast.getChildren().getFirst()));
        }

        return res;
    }

    private StringBuilder factor(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append(primary(ast.getChildren().getFirst()));
        } else if (ast.getSubType().equals(RuleType.PAR)) {
            res.append("(");
            res.append(disjunction(ast.getChildren().getFirst()));
            res.append(")");
        } else {
            if (ast.getSubType().equals(RuleType.NEGATIVE))
                res.append("-").append(primary(ast.getChildren().getFirst()));
            else res.append("+").append(primary(ast.getChildren().getFirst()));
        }

        return res;
    }

    private StringBuilder term(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append(modulo(ast.getChildren().getFirst()));
        } else {
            AbstractSyntaxTree t = ast.getChildren().getFirst(), m = ast.getChildren().getLast();

            res.append(term(t));

            if (ast.getSubType().equals(RuleType.TIMES)) res.append(" * ");
            else res.append(" / ");
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
        } else {
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
                    res.append("int ");
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

    private StringBuilder program(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder("public static void ");
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
        StringBuilder res = new StringBuilder();
        for (AbstractSyntaxTree child : ast.getChildren()) {
            res.append(generate(child));
        }

        return res;
    }



    private StringBuilder statement(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();
        for (AbstractSyntaxTree child : ast.getChildren()) {
            switch (child.getType()) {
                case NodeType.DECLARATION:
                    res.append(declaration(child));
                    break;
                case NodeType.ASSIGNMENTS:
                    res.append(assignments(child, true));
                    break;
                case NodeType.IF:
                    res.append(if_assignment(child));
                    break;
                case NodeType.WHILE:
                    res.append(while_statement(child));
                    break;
                case NodeType.SWITCH:
                    res.append(switchStatement(child));
                    break;
                case NodeType.PRINT:
                    res.append(print(child));
                    break;
                case NodeType.ASSIGNMENT:
                    res.append(assignment(child, true));
                    break;
                case NodeType.DISJUNCTION:
                    if (ast.getSubType().equals(RuleType.PRINT)) {
                        res.append("System.out.println(").append(disjunction(child)).append(");\n");
                    } else {
                        res.append(disjunction(child));
                    }
                    break;
                default:
                    throw new AssertionError("Unexpected node type: " + child.getType());
            }
        }

        if (ast.getSubType().equals(RuleType.BREAK)) {
            res.append("break;\n");
        } else if (ast.getSubType().equals(RuleType.CONTINUE)) {
            res.append("continue;\n");
        }

        return res;
    }


    private StringBuilder print(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.DISJUNCTION)) {
                res.append("System.out.println(");
                res.append(disjunction(child));
                res.append(");\n");
            } else {
                res.append(print(child));
            }
        }

        return res;
    }

    private StringBuilder switchStatement(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        AbstractSyntaxTree i = ast.getChildren().getFirst(), c = ast.getChildren().getLast();

        res.append("switch(");
        res.append(id(i));
        res.append(") {\n");
        res.append(cases(c));
        res.append("}");
        return res;
    }

    private ArrayList<String> options(AbstractSyntaxTree ast) {
        ArrayList<String> arr = new ArrayList<>();
        if (ast.getSubType().equals(RuleType.MULTI)) {
            AbstractSyntaxTree o = ast.getChildren().get(0), n = ast.getChildren().get(1);
            arr.add(num(n).toString());
            arr.addAll(options(o));
        } else {
            AbstractSyntaxTree n = ast.getChildren().getFirst();
            arr.add(num(n).toString());
        }

        return arr;
    }

    private StringBuilder cases(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        if (ast.getSubType().equals(RuleType.DEFAULT)) {
            res.append("default:\n");
            res.append(statements(ast.getChildren().getFirst()));
            return res;
        }

        AbstractSyntaxTree n = ast.getChildren().get(0), s = ast.getChildren().get(1), c = ast.getChildren().get(2);

        if (n.getType().equals(NodeType.OPTIONS)) {

            ArrayList<String> op = options(n);
            for (int i = op.size() - 1; i >= 0; i--) {
                res.append("case ").append(op.get(i)).append(":\n").append(followStatement(s));
            }

            res.append(cases(c));
        } else {
            res.append("case ").append(num(n)).append(":\n");
            res.append(statements(s));
            res.append(cases(c));
        }
        return res;
    }

    private StringBuilder while_statement(AbstractSyntaxTree ast) {
        StringBuilder res = new StringBuilder();

        AbstractSyntaxTree dis = ast.getChildren().getFirst(), following = ast.getChildren().getLast();

        res.append("while (");
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
        AbstractSyntaxTree dis = ast.getChildren().get(0), follow1 = ast.getChildren().get(1), follow2 = ast.getChildren().get(2);

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

        if (child.getType().equals(NodeType.ASSIGNMENTS)) res.append(assignments(child, false));
        else res.append(assignment(child, false));

        return res;
    }

    private StringBuilder assignments(AbstractSyntaxTree ast, boolean declared) {
        StringBuilder res = new StringBuilder();

        for (AbstractSyntaxTree child : ast.getChildren()) {
            if (child.getType().equals(NodeType.ASSIGNMENT)) {
                res.append(assignment(child, declared));
            } else {
                res.append(assignments(child, declared));
            }
        }

        return res;
    }
}
