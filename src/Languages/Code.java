package Languages;

import Transpiler.AbstractSyntaxTree;

abstract public class Code implements Runnable {
    protected String code;
    protected AbstractSyntaxTree ast;

    protected Code(String code) {
        this.code = code;
    }

    protected Code(AbstractSyntaxTree ast) {
        this.ast = ast;
    }


}
