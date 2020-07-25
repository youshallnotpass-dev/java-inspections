package com.iwillfailyou.inspections.allpublic.nonpublics;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.cactoos.BiFunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NonpublicViolations implements BiFunc<CompilationUnit,
    TypeDeclaration<?>, List<Nonpublic>> {

    @Override
    public List<Nonpublic> apply(
        final CompilationUnit unit,
        final TypeDeclaration<?> root
    ) {
        final List<Nonpublic> nonpublics = new ArrayList<>();
        final List<MethodDeclaration> methods = unit.findAll(
            MethodDeclaration.class
        );
        for (final MethodDeclaration method : methods) {
            if (!method.isPublic()) {
                final Optional<ClassOrInterfaceDeclaration> parentType = method.findFirst(
                    Node.TreeTraversal.PARENTS,
                    (final Node node) -> {
                        final Optional<ClassOrInterfaceDeclaration> result;
                        if (node instanceof ClassOrInterfaceDeclaration) {
                            result = Optional.of((ClassOrInterfaceDeclaration) node);
                        } else {
                            result = Optional.empty();
                        }
                        return result;
                    }
                );
                final boolean interfaceMethod = parentType.isPresent() && parentType.get().isInterface();
                if (!interfaceMethod) {
                    nonpublics.add(new JavaNonpublic(method, root));
                }
            }
        }
        return nonpublics;
    }
}
