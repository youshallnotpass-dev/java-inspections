package dev.youshallnotpass.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithParameters;

import java.util.Optional;

public final class ParentNodeWithParameters implements Search<Node> {

    private final Node node;

    public ParentNodeWithParameters(final Node node) {
        this.node = node;
    }

    @Override
    public Node find() {
        return node.findFirst(
            Node.TreeTraversal.PARENTS,
            (final Node parent) -> {
                return Optional.of(parent)
                    .filter((final Node p) -> p instanceof NodeWithParameters);
            }
        ).orElse(node);
    }
}
