package com.iwillfailyou.inspections.allpublic.nonpublics;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.cactoos.BiFunc;

import java.util.ArrayList;
import java.util.List;

public final class NonpublicViolations implements BiFunc<CompilationUnit,
    TypeDeclaration<?>, List<Nonpublic>> {

    @Override
    public List<Nonpublic> apply(
        final CompilationUnit unit,
        final TypeDeclaration<?> root
    ) {
        final List<Nonpublic> result = new ArrayList<>();
        final List<MethodDeclaration> methods = unit.findAll(
            MethodDeclaration.class
        );
        for (final MethodDeclaration method : methods) {
            if (!method.isPublic()) {
                result.add(new JavaNonpublic(method, root));
            }
        }
        return result;
    }
}
