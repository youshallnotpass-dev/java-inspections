package com.iwillfailyou.javaparser;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.cactoos.scalar.Unchecked;

import java.util.Optional;

public final class NodeDescription implements Description {

    private final Node node;
    private final Unchecked<Node> cause;
    private final TypeDeclaration<?> root;

    public NodeDescription(
        final Node node,
        final TypeDeclaration<?> root
    ) {
        this(
            node,
            new Unchecked<>(() -> {
                final Optional<Node> parentNode = node.getParentNode();
                final Node cause;
                if (parentNode.isPresent()) {
                    cause = parentNode.get();
                } else {
                    cause = node;
                }
                return cause;
            }),
            root
        );
    }

    public NodeDescription(
        final Node node,
        final Node cause,
        final TypeDeclaration<?> root
    ) {
        this(
            node,
            new Unchecked<>(() -> cause),
            root
        );
    }

    public NodeDescription(
        final Node node,
        final Unchecked<Node> cause,
        final TypeDeclaration<?> root
    ) {
        this.node = node;
        this.cause = cause;
        this.root = root;
    }

    @Override
    public String asString() {
        final StringBuilder description = new StringBuilder();
        description.append(new NodePath(node).asString());
        final Optional<Range> range = node.getRange();
        if (range.isPresent()) {
            description.append('(');
            description.append(root.getNameAsString());
            description.append(".java:");
            description.append(range.get().begin.line);
            description.append(')');
        }
        description.append(" > ");
        final String causeRepr = cause.value().toString();
        final int firstBreakIndex = causeRepr.indexOf('\n');
        if (firstBreakIndex != -1) { // many strings in cause representation
            description.append(causeRepr, 0, firstBreakIndex);
        } else {
            description.append(causeRepr);
        }
        return description.toString();
    }
}
