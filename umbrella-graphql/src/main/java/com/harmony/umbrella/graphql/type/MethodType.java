package com.harmony.umbrella.graphql.type;

import com.harmony.umbrella.graphql.metadata.GraphqlMethodMetadata;
import graphql.language.AbstractNode;
import graphql.language.Node;
import graphql.language.NodeVisitor;
import graphql.language.Type;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxin
 */
public class MethodType extends AbstractNode<MethodType> implements Type<MethodType> {

    private GraphqlMethodMetadata methodMetadata;

    public MethodType(GraphqlMethodMetadata methodMetadata) {
        this.methodMetadata = methodMetadata;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>();
    }

    public GraphqlMethodMetadata getMethodMetadata() {
        return methodMetadata;
    }

    @Override
    public boolean isEqualTo(Node node) {
        return node instanceof MethodType && ((MethodType) node).methodMetadata.equals(this.methodMetadata);
    }

    @Override
    public MethodType deepCopy() {
        return new MethodType(methodMetadata);
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        throw new UnsupportedOperationException();
    }
}
