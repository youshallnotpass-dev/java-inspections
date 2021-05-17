package dev.youshallnotpass.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.util.Optional;

public final class NodePath implements Path {

    private final Node node;

    public NodePath(final Node node) {
        this.node = node;
    }

    @Override
    public String asString() {
        final StringBuilder path = new StringBuilder();
        if (node instanceof TypeDeclaration) {
            path.insert(0, ((TypeDeclaration<?>) node).getNameAsString());
            path.insert(0, '$');
        }
        if (node instanceof ObjectCreationExpr) {
            path.insert(0, ((ObjectCreationExpr) node).getType().getNameAsString());
            path.insert(0, '$');
        }
        if (node instanceof MethodDeclaration) {
            path.insert(0, ((MethodDeclaration) node).getNameAsString());
            path.insert(0, '.');
        }
        if (node instanceof CompilationUnit) {
            final Optional<PackageDeclaration> pkg = ((CompilationUnit) node).getPackageDeclaration();
            if (pkg.isPresent()) {
                path.replace(0, 1, ".");
                path.insert(0, pkg.get().getNameAsString());
            }
        }
        node.walk(Node.TreeTraversal.PARENTS, (final Node node) -> {
            if (node instanceof TypeDeclaration) {
                path.insert(0, ((TypeDeclaration<?>) node).getNameAsString());
                path.insert(0, '$');
            }
            if (node instanceof ObjectCreationExpr) {
                path.insert(0, ((ObjectCreationExpr) node).getType().getNameAsString());
                path.insert(0, '$');
            }
            if (node instanceof MethodDeclaration) {
                path.insert(0, ((MethodDeclaration) node).getNameAsString());
                path.insert(0, '.');
            }
            if (node instanceof CompilationUnit) {
                final Optional<PackageDeclaration> pkg = ((CompilationUnit) node).getPackageDeclaration();
                if (pkg.isPresent()) {
                    path.replace(0, 1, ".");
                    path.insert(0, pkg.get().getNameAsString());
                }
            }
        });
        if (path.length() > 0 && path.charAt(0) == '$') {
            path.delete(0, 1);
        }
        return path.toString();
    }
}
