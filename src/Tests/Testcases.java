package Tests;

import Languages.C.C;
import Languages.Code;
import Languages.Java.Java;
import Languages.Rust.Rust;
import Transpiler.AbstractSyntaxTree;
import Transpiler.Transpiler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import static org.junit.Assert.*;

public class Testcases {

    @Test
    public void testCTranslation() throws IOException, URISyntaxException {
        String cCode = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test1.c")).toURI())));
        C c = new C(cCode);
        AbstractSyntaxTree tree = c.parseToAST();
        Java java = new Java(new Java(tree).generateCode());
        Rust rust = new Rust(new Rust(tree).generateCode());
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> runnable = transpiler.getSimilarRunnable(c);
        assertEquals(runnable.size(), 3);
    }

    @Test
    public void testJavaTranslation() throws IOException, URISyntaxException {
        String javaCode = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test2.j")).toURI())));
        Java java = new Java(javaCode);
        AbstractSyntaxTree tree = java.parseToAST();
        C c = new C(new C(tree).generateCode());
        Rust rust = new Rust(new Rust(tree).generateCode());
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> runnables = transpiler.getSimilarRunnable(c);
        assertEquals(runnables.size(), 3);
    }

    @Test
    public void testRustTranslation() throws IOException, URISyntaxException {
        String rustCode = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test3.rs")).toURI())));
        Rust rust = new Rust(rustCode);
        AbstractSyntaxTree tree = rust.parseToAST();
        Java java = new Java(new Java(tree).generateCode());
        C c = new C(new C(tree).generateCode());
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> runnables = transpiler.getSimilarRunnable(c);
        assertEquals(runnables.size(), 3);
    }

    @Test
    public void testDifferentCodes() throws IOException, URISyntaxException {
        String cCode = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test4.c")).toURI())));
        String javaCode = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test4.j")).toURI())));
        String rustCode = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test4.rs")).toURI())));
        C c = new C(cCode);
        Java java = new Java(javaCode);
        Rust rust = new Rust(rustCode);
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> uniques = transpiler.getUniqueRunnables();
        assertEquals(c.parseToAST(), java.parseToAST());
        assertEquals(uniques.size(), 1);
    }

    @Test
    public void testManual() throws IOException, URISyntaxException {
        String Code = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource("test2.j")).toURI())));
        Java java = new Java(Code);
        AbstractSyntaxTree tree = java.parseToAST();
        System.out.println(new Java(tree).generateCode());
    }
}