package com.harmony.umbrella.graphql.type;

import com.harmony.umbrella.graphql.metadata.GraphqlFieldMetadata;
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
public class FieldType extends AbstractNode<FieldType> implements Type<FieldType> {

    private GraphqlFieldMetadata fieldMetadata;

    public FieldType(GraphqlFieldMetadata fieldMetadata) {
        this.fieldMetadata = fieldMetadata;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>();
    }

    public GraphqlFieldMetadata getFieldMetadata() {
        return fieldMetadata;
    }

    @Override
    public boolean isEqualTo(Node node) {
        return node instanceof FieldType && ((FieldType) node).getFieldMetadata().equals(this.fieldMetadata);
    }

    @Override
    public FieldType deepCopy() {
        return new FieldType(fieldMetadata);
    }

    @Override
    public TraversalControl accept(TraverserContext<Node> context, NodeVisitor visitor) {
        throw new UnsupportedOperationException();
    }
}
