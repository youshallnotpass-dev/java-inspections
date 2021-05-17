package dev.youshallnotpass.inspections.nomultiplereturn.multiplereturns;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;
import dev.youshallnotpass.javaparser.ParentNodeWithParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cactoos.BiFunc;

public final class MultipleReturnViolations implements BiFunc<CompilationUnit, TypeDeclaration<?>, List<MultipleReturn>> {

    @Override
    public List<MultipleReturn> apply(
        final CompilationUnit unit,
        final TypeDeclaration<?> root
    ) {
        final List<MultipleReturn> multipleReturns = new ArrayList<>();
        final List<MethodDeclaration> methods = unit.findAll(
            MethodDeclaration.class
        );
        for (final MethodDeclaration method : methods) {
            final Map<Node, List<ReturnStmt>> returnStmtMap =
                method.findAll(ReturnStmt.class)
                    .stream()
                    .collect(Collectors.groupingBy((final ReturnStmt returnStmt) -> {
                        return new ParentNodeWithParameters(returnStmt).find();
                    }));

            returnStmtMap.forEach((final Node parent, final List<ReturnStmt> returnStmts) -> {
                if (returnStmts.size() > 1) {
                    multipleReturns.add(new JavaMultipleReturn(parent, root));
                }
            });
        }
        return multipleReturns;
    }
}
