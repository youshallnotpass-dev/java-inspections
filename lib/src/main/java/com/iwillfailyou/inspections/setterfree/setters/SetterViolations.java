package com.iwillfailyou.inspections.setterfree.setters;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.VoidType;
import com.iwillfailyou.inspections.nomultiplereturn.multiplereturns.JavaMultipleReturn;
import com.iwillfailyou.inspections.nomultiplereturn.multiplereturns.MultipleReturn;
import org.cactoos.BiFunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Applying to methods by pattern:
 * non private, method with only one parameter and only one statement
 * (exclusive return statements) - assign statement, which consists of class
 * field access target and parameter name value.
 */
public final class SetterViolations implements BiFunc<CompilationUnit, TypeDeclaration<?>, List<Setter>> {

    @Override
    public List<Setter> apply(
        final CompilationUnit unit,
        final TypeDeclaration<?> root
    ) {
        final List<Setter> setters = new ArrayList<>();
        final List<ClassOrInterfaceDeclaration> declarations = unit.findAll(ClassOrInterfaceDeclaration.class);
        for (final ClassOrInterfaceDeclaration declaration : declarations) {
            final List<VariableDeclarator> classFields = new ArrayList<>();
            final List<MethodDeclaration> classMethods = new ArrayList<>();

            for (final Node childNode : declaration.getChildNodes()) {
                if (childNode instanceof FieldDeclaration) {
                    final FieldDeclaration field = (FieldDeclaration) childNode;
                    if (!field.isFinal()) {
                        classFields.addAll(field.getVariables());
                    }
                }
                if (childNode instanceof MethodDeclaration) {
                    final MethodDeclaration method = (MethodDeclaration) childNode;
                    classMethods.add(method);
                }
            }

            for (final MethodDeclaration method : classMethods) {
                final boolean isPrivate = method.isPrivate();
                final NodeList<Parameter> parameters = method.getParameters();
                final Optional<AssignExpr> assignExprOpt = method.getBody()
                    .map(BlockStmt::getStatements)
                    .map((final List<Statement> statements) -> {
                        return statements.stream()
                            .filter((final Statement statement) -> !statement.isReturnStmt())
                            .collect(Collectors.toList());
                    })
                    .filter((final List<Statement> statements) -> statements.size() == 1)
                    .map((final List<Statement> statements) -> statements.get(0))
                    .filter(Statement::isExpressionStmt)
                    .map((final Statement statement) -> statement.asExpressionStmt().getExpression())
                    .filter(Expression::isAssignExpr)
                    .map(Expression::asAssignExpr);

                if (!isPrivate && parameters.size() == 1 && assignExprOpt.isPresent()) {
                    final Parameter methodArg = parameters.get(0);
                    final AssignExpr assignExpr = assignExprOpt.get();
                    final Expression target = assignExpr.getTarget();
                    final Expression value = assignExpr.getValue();

                    if (value.isNameExpr() && value.asNameExpr().getNameAsString().equals(methodArg.getNameAsString())) {
                        if (target.isFieldAccessExpr()) {
                            final FieldAccessExpr fieldAccessExpr = target.asFieldAccessExpr();
                            final Expression scope = fieldAccessExpr.getScope();
                            if (scope.isThisExpr()) {
                                final String fieldName = fieldAccessExpr.getNameAsString();
                                for (final VariableDeclarator classField : classFields) {
                                    if (classField.getNameAsString().equals(fieldName)) {
                                        setters.add(new JavaSetter(method, root));
                                    }
                                }
                            }
                        } else if (target.isNameExpr()) {
                            final String fieldName = target.asNameExpr().getNameAsString();
                            for (final VariableDeclarator classField : classFields) {
                                if (classField.getNameAsString().equals(fieldName)) {
                                    setters.add(new JavaSetter(method, root));
                                }
                            }
                        }
                    }
                }
            }
        }

        return setters;
    }
}
