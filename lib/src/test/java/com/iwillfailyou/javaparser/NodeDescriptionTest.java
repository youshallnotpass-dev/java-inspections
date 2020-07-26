package com.iwillfailyou.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public final class NodeDescriptionTest {

    @Test
    public void nullDescription() throws Exception {
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
            new NodeDescription(aNull, aNull, typeDeclaration).asString(),
            IsEqual.equalTo("A(A.java:1) > null")
        );
    }

    @Test
    public void staticDescription() throws Exception {
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
}