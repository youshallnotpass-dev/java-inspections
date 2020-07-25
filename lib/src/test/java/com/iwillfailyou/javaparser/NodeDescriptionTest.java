package com.iwillfailyou.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public final class NodeDescriptionTest {

    @Test
    public void nullDescription() {
        final JavaParser parser = new JavaParser(
            new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW)
        );
        final ParseResult<CompilationUnit> result = parser.parse(
            "class A { private String a = null; }"
        );
        final CompilationUnit unit = result.getResult().get();
        final Optional<TypeDeclaration> rootOpt = unit.findFirst(
            TypeDeclaration.class
        );
        final TypeDeclaration<?> typeDeclaration = rootOpt.get();
        final Optional<NullLiteralExpr> nullOpt = unit.findFirst(
            NullLiteralExpr.class
        );
        final NullLiteralExpr aNull = nullOpt.get();

        Assert.assertThat(
            new NodeDescription(aNull, typeDeclaration).asString(),
            IsEqual.equalTo("A(A.java:1) > a = null")
        );
    }

    @Test
    public void staticDescription() {
        final JavaParser parser = new JavaParser(
            new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW)
        );
        final ParseResult<CompilationUnit> result = parser.parse(
            "class A { private static String a = null; }"
        );
        final CompilationUnit unit = result.getResult().get();
        final Optional<TypeDeclaration> rootOpt = unit.findFirst(
            TypeDeclaration.class
        );
        final TypeDeclaration<?> typeDeclaration = rootOpt.get();
        final Optional<FieldDeclaration> fieldOpt = unit.findFirst(
            FieldDeclaration.class
        );
        final FieldDeclaration field = fieldOpt.get();

        Assert.assertThat(
            new NodeDescription(field, field, typeDeclaration).asString(),
            IsEqual.equalTo("A(A.java:1) > private static String a = null;")
        );
    }

    @Test
    public void descriptionSkipsSingleAnnotation() {
        final JavaParser parser = new JavaParser(
            new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW)
        );
        final ParseResult<CompilationUnit> result = parser.parse(
            "class A { @Annotation private static void method() {} }"
        );
        final CompilationUnit unit = result.getResult().get();
        final Optional<TypeDeclaration> rootOpt = unit.findFirst(
            TypeDeclaration.class
        );
        final TypeDeclaration<?> typeDeclaration = rootOpt.get();
        final Optional<MethodDeclaration> methodOpt = unit.findFirst(
            MethodDeclaration.class
        );
        final MethodDeclaration method = methodOpt.get();

        Assert.assertThat(
            new NodeDescription(method, method, typeDeclaration).asString(),
            IsEqual.equalTo("A.method(A.java:1) > private static void method() {")
        );
    }

    @Test
    public void descriptionSkipsMultipleAnnotations() {
        final JavaParser parser = new JavaParser(
            new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW)
        );
        final ParseResult<CompilationUnit> result = parser.parse(
            "class A {\n" +
                "   @Annotation1\n" +
                "   @Annotation2\n" +
                "   @Annotation3\n" +
                "   private static String a = null;\n" +
                "}"
        );
        final CompilationUnit unit = result.getResult().get();
        final Optional<TypeDeclaration> rootOpt = unit.findFirst(
            TypeDeclaration.class
        );
        final TypeDeclaration<?> typeDeclaration = rootOpt.get();
        final Optional<FieldDeclaration> fieldOpt = unit.findFirst(
            FieldDeclaration.class
        );
        final FieldDeclaration field = fieldOpt.get();

        Assert.assertThat(
            new NodeDescription(field, field, typeDeclaration).asString(),
            IsEqual.equalTo("A(A.java:5) > private static String a = null;")
        );
    }
}