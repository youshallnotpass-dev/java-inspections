package com.iwillfailyou.javaparser;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.cactoos.scalar.Unchecked;

import java.util.Optional;

public final class NodeDescription implements Description {

    private final Node node;
    private final Unchecked<Optional<Node>> cause;
    private final TypeDeclaration<?> root;

    public NodeDescription(
        final Node node,
        final Node cause,
        final TypeDeclaration<?> root
    ) {
        this(
            node,
            new Unchecked<>(() -> Optional.of(cause)),
            root
        );
    }

    public NodeDescription(
        final Node node,
        final Optional<Node> cause,
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
        final Unchecked<Optional<Node>> cause,
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
        final Optional<Node> causeOpt = cause.value();
        if (range.isPresent()) {
            description.append('(');
            description.append(root.getNameAsString());
            description.append(".java:");
            description.append(
                causeOpt
                    .map((final Node cause) -> {
                        return cause.getRange().orElse(range.get());
                    })
                    .orElse(range.get())
                    .begin
                    .line
            );
            description.append(')');
        }
        if (causeOpt.isPresent()) {
            description.append(" > ");
            final String causeRepr = causeOpt.get().toString();
            final int firstBreakIndex = causeRepr.indexOf('\n');
            if (firstBreakIndex != -1) { // many strings in cause representation
                description.append(causeRepr, 0, firstBreakIndex);
            } else {
                description.append(causeRepr);
            }
        }
        return description.toString();
    }
}
