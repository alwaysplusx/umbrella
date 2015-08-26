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

import java.util.Arrays;

import com.harmony.umbrella.data.Bond;

/**
 * Bond辅助工具类
 * 
 * @author wuxii@foxmail.com
 */
public class Bonds {
    /**
     * 将传入的{@linkplain Bond}用{@linkplain Operator#AND}关联起来
     * 
     * @param bonds
     *            待关联的{@linkplain Bond}
     * @return and条件的Bond
     */
    public static JunctionBond and(Bond... bonds) {
        return new DisjunctionBond(Arrays.asList(bonds));
    }

    /**
     * 将传入的{@linkplain Bond}用{@linkplain Operator#OR}关联起来
     * 
     * @param bonds
     *            待关联的{@linkplain Bond}
     * @return or条件的Bond
     */
    public static JunctionBond or(Bond... bonds) {
        return new ConjunctionBond(Arrays.asList(bonds));
    }

}
