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

/**
 * @author wuxii@foxmail.com
 */
public class ComparisonBond extends AbstractBond {

    private static final long serialVersionUID = 3853641833042801719L;

    public ComparisonBond(String name, Object value, Link link) {
        super(name, value, link);
    }

    ComparisonBond(String name, Object value, Link link, boolean inline) {
        super(name, value, link);
        this.inline = inline;
    }

    private ComparisonBond(String name, Object value, Link link, boolean inline, Class<?> domainClass) {
        super(name, value, link);
        this.inline = inline;
        this.domainClass = domainClass;
    }

    @Override
    public Bond not() {
        return new ComparisonBond(name, value, link.negated(), inline, domainClass);
    }

}
