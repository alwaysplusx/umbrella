package com.harmony.umbrella.graphql.type;

import com.harmony.umbrella.graphql.metadata.GraphqlParameterMetadata;
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
public class ParameterType extends AbstractNode<ParameterType> implements Type<ParameterType> {

    private GraphqlParameterMetadata parameterMetadata;

    public ParameterType(GraphqlParameterMetadata parameterMetadata) {
        this.parameterMetadata = parameterMetadata;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>();
    }

    public GraphqlParameterMetadata getParameterMetadata() {
        return parameterMetadata;
    }

    @Override
    public boolean isEqualTo(Node node) {
        return node instanceof ParameterType && ((ParameterType) node).getParameterMetadata().equals(parameterMetadata);
    }

    @Override
    public ParameterType deepCopy() {
        return new ParameterType(parameterMetadata);
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        throw new UnsupportedOperationException();
    }

}
