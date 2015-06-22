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

import static com.harmony.umbrella.data.bond.Bond.Link.*;

/**
 * @author wuxii@foxmail.com
 */
public class BooleanBond extends AbstractBond {

    private static final long serialVersionUID = 5657553287762316666L;

    public BooleanBond(boolean negated) {
        super("1", "1", negated ? EQUAL : NOT_EQUAL);
    }

    private BooleanBond(Link link) {
        super("1", "1", link);
    }

    @Override
    public Bond not() {
        return new BooleanBond(link.negated());
    }

    @Override
    public boolean isInline() {
        return true;
    }

}
