package dev.youshallnotpass.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import org.cactoos.func.UncheckedBiFunc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class NodeItem implements Item {

    private final Node node;
    private final Description description;
    private final UncheckedBiFunc<Node, String, Boolean> isSuppressed;

    public NodeItem(
        final Node origin,
        final Description description
    ) {
        this(
            origin,
            description,
            new UncheckedBiFunc<>(
                (final Node node, final String name) -> {
                    final List<Boolean> result = Arrays.asList(false);
                    if (node instanceof NodeWithAnnotations) {
                        final NodeWithAnnotations<?> declaration = (NodeWithAnnotations<?>) node;

                        if (declaration.isAnnotationPresent("SuppressWarnings")) {
                            final Optional<AnnotationExpr> suppressAnnotation = node.findFirst(
                                AnnotationExpr.class,
                                (final AnnotationExpr expr) -> {
                                    return "SuppressWarnings".equals(
                                        expr.getNameAsString()
                                    );
                                }
                            );
                            if (suppressAnnotation.isPresent()) {
                                final List<StringLiteralExpr> values = suppressAnnotation.get().findAll(
                                    StringLiteralExpr.class
                                );
                                for (final StringLiteralExpr value : values) {
                                    if (name.equals(value.asString())) {
                                        result.set(0, true);
                                    }
                                }
                            }
                        }
                    }
                    return result.get(0);
                }
            )
        );
    }

    public NodeItem(
        final Node origin,
        final Description description,
        final UncheckedBiFunc<Node, String, Boolean> isSuppressed
    ) {
        this.node = origin;
        this.description = description;
        this.isSuppressed = isSuppressed;
    }

    @Override
    public String description() {
        return description.asString();
    }

    @Override
    public boolean isSuppressed(final String name) {
        final List<Boolean> result = Arrays.asList(false);

        final boolean currentSuppressed = this.isSuppressed.apply(node, name);
        if (currentSuppressed) {
            result.set(0, true);
        } else {
            node.walk(Node.TreeTraversal.PARENTS, (final Node node) -> {
                final boolean suppressed = this.isSuppressed.apply(node, name);
                if (suppressed) {
                    result.set(0, true);
                }
            });
        }

        return result.get(0);
    }
}
