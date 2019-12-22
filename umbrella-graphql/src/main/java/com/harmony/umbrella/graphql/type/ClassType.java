package com.harmony.umbrella.graphql.type;

import com.harmony.umbrella.graphql.metadata.GraphqlMetadata;
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
public class ClassType extends AbstractNode<ClassType> implements Type<ClassType> {

    private GraphqlMetadata graphqlMetadata;

    public ClassType(GraphqlMetadata metadata) {
        this.graphqlMetadata = metadata;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>();
    }

    public GraphqlMetadata getGraphqlMetadata() {
        return graphqlMetadata;
    }

    @Override
    public boolean isEqualTo(Node node) {
        return node instanceof ClassType
                && ((ClassType) node).getGraphqlMetadata().equals(this.graphqlMetadata);
    }

    @Override
    public ClassType deepCopy() {
        return new ClassType(graphqlMetadata);
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        throw new UnsupportedOperationException();
    }
}

