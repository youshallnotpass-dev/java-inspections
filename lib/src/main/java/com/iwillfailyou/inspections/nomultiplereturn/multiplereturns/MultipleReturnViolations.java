package com.iwillfailyou.inspections.nomultiplereturn.multiplereturns;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.iwillfailyou.javaparser.NodeCaller;
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
                    .collect(Collectors.groupingBy(returnStmt -> new NodeCaller(returnStmt).caller()));

            returnStmtMap.forEach((parent, returnStmts) -> {
                if (returnStmts.size() > 1) {
                    multipleReturns.add(new JavaMultipleReturn(parent, root));
                } else {
                    final ReturnStmt returnStmt = returnStmts.get(0);
                    if (returnStmt.getChildNodes().size() > 0 && returnStmt.getChildNodes().get(0) instanceof ConditionalExpr) {
                        multipleReturns.add(new JavaMultipleReturn(returnStmt.getChildNodes().get(0), root));
                    }
                }
            });
        }
        return multipleReturns;
    }
}
