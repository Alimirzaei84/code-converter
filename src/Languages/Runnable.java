package Languages;

import Transpiler.AbstractSyntaxTree;

public interface Runnable {
    AbstractSyntaxTree parseToAST();
    String generateCode();
}
