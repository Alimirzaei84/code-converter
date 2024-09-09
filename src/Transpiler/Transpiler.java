package Transpiler;

import Languages.Runnable;

import java.util.ArrayList;
import java.util.List;

public class Transpiler <T extends Runnable> {

    List<T> runnable = new ArrayList<>();

    public void addCode(T code) {
        runnable.add(code);
    }

    public List<AbstractSyntaxTree> getAbstractSyntaxTrees() {
        /*
         * This function gathers each runnable object's abstract tree and returns them.
         * @return  list of each runnable object's abstract tree.
         */
        ArrayList<AbstractSyntaxTree> abstractSyntaxTrees = new ArrayList<>();
        for (T runnable : runnable) {
            abstractSyntaxTrees.add(runnable.parseToAST());
        }
        return abstractSyntaxTrees;
    }

    public List<String> getCodes() {
        /*
         * This function gathers each runnable object's string code and returns them.
         * @return  list of each runnable object's string code.
         */
        ArrayList<String> codes = new ArrayList<>();
        for (T runnable : runnable) {
            codes.add(runnable.generateCode());
        }
        return codes;
    }

    public List<T> getSimilarRunnable(T runnable) {
        /*
         * This function finds the runnable objects that are similar to the given runnable object
         * and returns them. Two runnable objects are considered similar only if their
         * Abstract Syntax Tree (AST) would be equal.
         * @param runnable: a runnable object (a specific programming language code)
         * @return          the runnable objects similar to the input.
         */
        AbstractSyntaxTree ast = runnable.parseToAST();
        ArrayList<T> list = new ArrayList<>();
        for (T r: this.runnable) {
            if (ast.equals(r.parseToAST())) {
                list.add(r);
            }
        }
        return list;
    }

    public List<T> getUniqueRunnables() {
        /*
         * This function finds the runnable objects that are unique (non-similar) and returns them.
         * @return  list of unique runnable objects.
         */
        List<T> uniqueRunnables = new ArrayList<>();
        for (T runnable : runnable) {
            boolean unique = true;
            for (int i = uniqueRunnables.size()-1; i >= 0; i--) {
                if (runnable.parseToAST().equals(uniqueRunnables.get(i).parseToAST())) {
                    uniqueRunnables.remove(i);
                    unique = false;
                }
            }

            if (unique) {
                uniqueRunnables.add(runnable);
            }
        }
        return uniqueRunnables;
    }
}