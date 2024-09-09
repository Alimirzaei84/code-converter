package Transpiler;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class AbstractSyntaxTree {

    private NodeType type;
    private RuleType subType;
    private String lexeme;
    private List<AbstractSyntaxTree> children = new ArrayList<>();
    protected AbstractSyntaxTree clone() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree(this.type, this.lexeme);
        ast.subType = this.subType;
        ast.children = new ArrayList<>();
        for (AbstractSyntaxTree child: this.children)
            ast.children.add(child.clone());
        return ast;
    }

    public AbstractSyntaxTree(NodeType type, RuleType subType, AbstractSyntaxTree... children) {
        this.type = type;
        this.subType = subType;
        this.children.addAll(Arrays.asList(children));
    }

    public AbstractSyntaxTree(NodeType type, String name) {
        this.type = type;
        this.lexeme = name;
    }

    private static boolean programEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  idEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                statementsEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean statementsEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        List<AbstractSyntaxTree> result = matchStatement(ast1.children.get(ast1.children.size()-1), ast2.children.get(ast2.children.size()-1), id1, id2);
        if (result == null) return false;
        if (result.get(0) == null) {
            if (ast1.subType == RuleType.SINGLE) return result.get(1) == null && ast2.subType == RuleType.SINGLE;
            ast1 = ast1.children.get(0);
        } else ast1.children.set(ast1.children.size()-1, result.get(0));
        if (result.get(1) == null) {
            if (ast2.subType == RuleType.SINGLE) return result.get(0) == null && ast1.subType == RuleType.SINGLE;
            ast2 = ast2.children.get(0);
        } else ast2.children.set(ast2.children.size()-1, result.get(1));
        return statementsEq(ast1, ast2, id1, id2);
    }

    private static boolean followStatementsEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType == RuleType.EMPTY ^ ast2.subType == RuleType.EMPTY) return false;
        if (ast1.subType == RuleType.EMPTY) return true;
        AbstractSyntaxTree child1 = (ast1.subType == RuleType.SINGLE) ?
                                    new AbstractSyntaxTree(NodeType.STATEMENTS, RuleType.SINGLE, ast1.children.get(0)) :
                                    ast1.children.get(0);
        AbstractSyntaxTree child2 = (ast2.subType == RuleType.SINGLE) ?
                                    new AbstractSyntaxTree(NodeType.STATEMENTS, RuleType.SINGLE, ast2.children.get(0)) :
                                    ast2.children.get(0);
        return statementsEq(child1, child2, id1, id2);
    }

    private static AbstractSyntaxTree followStatementsToStatements(AbstractSyntaxTree ast) {
        if (ast.type != NodeType.FOLLOW_STATEMENTS) return ast;
        if (ast.subType == RuleType.EMPTY) return null;
        if (ast.subType == RuleType.SINGLE) return new AbstractSyntaxTree(NodeType.STATEMENTS, RuleType.SINGLE, ast.children.get(0));
        return ast.children.get(0);
    }

    private static List<AbstractSyntaxTree> matchStatement(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return null;
        List<AbstractSyntaxTree> result;
        List<AbstractSyntaxTree> returned = new ArrayList<>();
        switch (ast1.subType) {
            case DECLARE:
                result = matchDeclaration(ast1.children.get(0), ast2.children.get(0), id1, id2);
                if (result == null) return null;
                if (result.get(0) == null) ast1 = null;
                else ast1.children.set(0, result.get(0));
                if (result.get(1) == null) ast2 = null;
                else ast2.children.set(0, result.get(1));
                returned.add(ast1);
                returned.add(ast2);
                return returned;
            case ASSIGNMENTS:
                result = matchAssignments(ast1.children.get(0), ast2.children.get(0), id1, id2);
                if (result == null) return null;
                if (result.get(0) == null) ast1 = null;
                else ast1.children.set(0, result.get(0));
                if (result.get(1) == null) ast2 = null;
                else ast2.children.set(0, result.get(1));
                returned.add(ast1);
                returned.add(ast2);
                return returned;
            case BREAK:
            case CONTINUE:
                returned.add(null);
                returned.add(null);
                return returned;
            case PRINT:
                result = matchPrint(ast1.children.get(0), ast2.children.get(0), id1, id2);
                if (result == null) return null;
                if (result.get(0) == null) ast1 = null;
                else ast1.children.set(0, result.get(0));
                if (result.get(1) == null) ast2 = null;
                else ast2.children.set(0, result.get(1));
                returned.add(ast1);
                returned.add(ast2);
                return returned;
            case IF:
                returned.add(null);
                returned.add(null);
                return ifEq(ast1.children.get(0), ast2.children.get(0), id1, id2)? returned: null;
            case SWITCH:
                returned.add(null);
                returned.add(null);
                return switchEq(ast1.children.get(0), ast2.children.get(0), id1, id2)? returned: null;
            case WHILE:
                returned.add(null);
                returned.add(null);
                return whileEq(ast1.children.get(0), ast2.children.get(0), id1, id2)? returned: null;
            default:
                return null;
        }
    }

    private static List<AbstractSyntaxTree> matchPrint(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        AbstractSyntaxTree child1 = (ast1.type == NodeType.DISJUNCTION)? ast1: ast1.children.get(ast1.children.size()-1);
        AbstractSyntaxTree child2 = (ast2.type == NodeType.DISJUNCTION)? ast2: ast2.children.get(ast2.children.size()-1);
        if (!disjunctionEq(child1, child2, id1, id2)) return null;
        if (ast1.type == NodeType.DISJUNCTION || ast1.subType == RuleType.SINGLE) ast1 = null;
        else ast1 = ast1.children.getFirst();
        if (ast2.type == NodeType.DISJUNCTION || ast2.subType == RuleType.SINGLE) ast2 = null;
        else ast2 = ast2.children.getFirst();
        List<AbstractSyntaxTree> result = new ArrayList<>();
        result.add(ast1);
        result.add(ast2);
        return result;
    }

    private static List<AbstractSyntaxTree> matchDeclaration(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        List<AbstractSyntaxTree> result = matchAssignments(ast1.children.getFirst(), ast2.children.getFirst(), id1, id2);
        if (result == null) return null;
        if (result.get(0) == null) ast1 = null;
        else ast1.children.set(0, result.get(0));
        if (result.get(1) == null) ast2 = null;
        else ast2.children.set(0, result.get(1));
        List<AbstractSyntaxTree> returned = new ArrayList<>();
        returned.add(ast1);
        returned.add(ast2);
        return returned;
    }

    private static boolean assignmentEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  idEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                disjunctionEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static List<AbstractSyntaxTree> matchAssignments(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        AbstractSyntaxTree child1 = (ast1.type == NodeType.ASSIGNMENT)? ast1: ast1.children.getLast();
        AbstractSyntaxTree child2 = (ast2.type == NodeType.ASSIGNMENT)? ast2: ast2.children.getLast();
        if (!assignmentEq(child1, child2, id1, id2)) return null;
        if (ast1.type == NodeType.ASSIGNMENT || ast1.subType == RuleType.SINGLE) ast1 = null;
        else ast1 = ast1.children.getFirst();
        if (ast2.type == NodeType.ASSIGNMENT || ast2.subType == RuleType.SINGLE) ast2 = null;
        else ast2 = ast2.children.getFirst();
        List<AbstractSyntaxTree> result = new ArrayList<>();
        result.add(ast1);
        result.add(ast2);
        return result;
    }

    private static boolean ifEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  disjunctionEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                followStatementsEq(ast1.children.get(1), ast2.children.get(1), id1, id2) &&
                followStatementsEq(ast1.children.get(2), ast2.children.get(2), id1, id2);
    }

    private static boolean switchEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  idEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                casesEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private List<AbstractSyntaxTree> getCases() {
        List<AbstractSyntaxTree> result = new ArrayList<>();
        if (this.subType == RuleType.DEFAULT) {
            result.add(new AbstractSyntaxTree(NodeType.CASE, RuleType.DEFAULT, followStatementsToStatements(this.children.get(0))));
            return result;
        }
        result.add(new AbstractSyntaxTree(NodeType.CASE, RuleType.MULTI, this.children.get(0), followStatementsToStatements(this.children.get(1))));
        result.addAll(this.children.get(2).getCases());
        return result;
    }

    private static boolean casesEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        List<AbstractSyntaxTree> cases1 = ast1.getCases(), cases2 = ast2.getCases();
        int i = cases1.size(), j = cases2.size();
        while (i * j > 0) {
            List<AbstractSyntaxTree> result = matchCase(cases1.get(i-1), cases2.get(j-1), id1, id2);
            if (result == null) return false;
            if (result.get(0) == null) i--;
            else cases1.set(i-1, result.get(0));
            if (result.get(1) == null) j--;
            else cases2.set(j-1, result.get(1));
        }
        return i == j;
    }

    private static List<AbstractSyntaxTree> matchCase(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return null;
        List<AbstractSyntaxTree> returned = new ArrayList<>();
        if (ast1.subType == RuleType.DEFAULT) {
            returned.add(null);
            returned.add(null);
            return statementsEq(ast1.children.get(0), ast2.children.get(0), id1, id2)? returned: null;
        }
        List<AbstractSyntaxTree> result = matchOptions(ast1.children.get(0), ast2.children.get(0));
        if (result == null) return null;
        if (result.get(0) == null) ast1 = null;
        else ast1.children.set(0, result.get(0));
        if (result.get(1) == null) ast2 = null;
        else ast2.children.set(0, result.get(1));
        returned.add(ast1);
        returned.add(ast2);
        return returned;
    }

    private static List<AbstractSyntaxTree> matchOptions(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2) {
        AbstractSyntaxTree child1 = (ast1.type == NodeType.NUM)? ast1: ast1.children.getLast();
        AbstractSyntaxTree child2 = (ast2.type == NodeType.NUM)? ast2: ast2.children.getLast();
        if (!numEq(child1, child2)) return null;
        if (ast1.type == NodeType.NUM || ast1.subType == RuleType.SINGLE) ast1 = null;
        else ast1 = ast1.children.getFirst();
        if (ast2.type == NodeType.NUM || ast2.subType == RuleType.SINGLE) ast2 = null;
        else ast2 = ast2.children.getFirst();
        List<AbstractSyntaxTree> result = new ArrayList<>();
        result.add(ast1);
        result.add(ast2);
        return result;
    }

    private static boolean whileEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  disjunctionEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                followStatementsEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean disjunctionEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.SINGLE) return conjunctionEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return  disjunctionEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                conjunctionEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean conjunctionEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.SINGLE) return inversionEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return  conjunctionEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                inversionEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean inversionEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.SINGLE) return comparisonEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return inversionEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
    }

    private static boolean comparisonEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.EQ) return eqEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        if (ast1.subType == RuleType.LT) return ltEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        if (ast1.subType == RuleType.GT) return gtEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return sumEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
    }

    private static boolean eqEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  sumEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                sumEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean ltEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  sumEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                sumEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean gtEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        return  sumEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                sumEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean sumEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.ADD || ast1.subType == RuleType.SUB)
            return  sumEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                    termEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
        return termEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
    }

    private static boolean termEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.TIMES || ast1.subType == RuleType.DIVIDES)
            return  termEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                    moduloEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
        return moduloEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
    }

    private static boolean moduloEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.SINGLE) return factorEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return  moduloEq(ast1.children.get(0), ast2.children.get(0), id1, id2) &&
                factorEq(ast1.children.get(1), ast2.children.get(1), id1, id2);
    }

    private static boolean factorEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.PAR) return disjunctionEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return primaryEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
    }

    private static boolean primaryEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (ast1.subType != ast2.subType) return false;
        if (ast1.subType == RuleType.VARIABLE) return idEq(ast1.children.get(0), ast2.children.get(0), id1, id2);
        return numEq(ast1.children.get(0), ast2.children.get(0));
    }

    private static boolean idEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2, Map<String, Integer> id1, Map<String, Integer> id2) {
        if (id1.get(ast1.lexeme) == null) id1.put(ast1.lexeme, id1.size());
        if (id2.get(ast2.lexeme) == null) id2.put(ast2.lexeme, id2.size());
        return id1.get(ast1.lexeme).equals(id2.get(ast2.lexeme));
    }

    private static boolean numEq(AbstractSyntaxTree ast1, AbstractSyntaxTree ast2) {
        return Integer.parseInt(ast1.lexeme) == Integer.parseInt(ast2.lexeme);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof AbstractSyntaxTree ast)) return false;
        if (this.type != ast.type) return false;
        Map<String, Integer> map1 = new HashMap<>();
        Map<String, Integer> map2 = new HashMap<>();
        return programEq(this.clone(), ast.clone(), map1, map2);
    }

    public List<AbstractSyntaxTree> getChildren() {
        return children;
    }

    public NodeType getType() {
        return type;
    }

    public RuleType getSubType() {
        return subType;
    }

    public String getLexeme() {
        return lexeme;
    }
}
