package dev.youshallnotpass.inspections.inheritancefree.inheritances;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.cactoos.BiFunc;

import java.util.List;
import java.util.stream.Collectors;

public final class InheritanceViolations implements BiFunc<CompilationUnit, TypeDeclaration<?>, List<Inheritance>> {

    @Override
    public List<Inheritance> apply(
        final CompilationUnit unit,
        final TypeDeclaration<?> root
    ) {
        return unit.findAll(ClassOrInterfaceDeclaration.class)
            .stream()
            .filter((final ClassOrInterfaceDeclaration type) -> !type.isInterface() && type.getExtendedTypes().isNonEmpty())
            .map((final ClassOrInterfaceDeclaration type) -> new JavaInheritance(type, root))
            .collect(Collectors.toList());
    }
}
