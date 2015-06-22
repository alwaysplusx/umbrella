/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.data.bond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class CompoundBond extends AbstractBond {

    private static final long serialVersionUID = -2291422684625628610L;

    protected final Operator operator;

    private final List<Bond> bonds = new ArrayList<Bond>();

    public CompoundBond(Operator operator, Bond... bonds) {
        super(null, null);
        this.operator = operator;
        appylBonds(bonds);
    }

    public CompoundBond(Operator operator, List<Bond> bonds) {
        super(null, null);
        this.operator = operator;
        appylBonds(bonds);
    }

    private void appylBonds(List<Bond> bonds) {
        this.bonds.clear();
        this.bonds.addAll(bonds);
    }

    private void appylBonds(Bond... bonds) {
        appylBonds(Arrays.asList(bonds));
    }

    @Override
    public Bond not() {
        List<Bond> list = new ArrayList<Bond>(bonds.size());
        for (Bond bond : bonds) {
            list.add(bond.not());
        }
        return new CompoundBond(operator.negated(), list);
    }

    public enum Operator {

        AND("and") {
            @Override
            protected Operator negated() {
                return OR;
            }
        },
        OR("or") {
            @Override
            protected Operator negated() {
                return AND;
            }
        };

        private String desc;

        private Operator(String desc) {
            this.desc = desc;
        }

        public String desc() {
            return desc;
        }

        protected abstract Operator negated();

    }

    @Override
    public String toString() {
        if (bonds.isEmpty()) {
            return "(1 = 1)";
        }
        if (bonds.size() == 1) {
            return bonds.get(0).toString();
        }

        StringBuilder sb = new StringBuilder("(");

        String opStr = operator == null ? "unknow" : operator.desc();
        Iterator<Bond> iterator = bonds.iterator();

        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(" ").append(opStr).append(" ");
            }
        }
        return sb.append(")").toString();
    }

}
