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
package com.harmony.umbrella.mapper.vo.deep;

import com.harmony.umbrella.mapper.vo.VoSupport;

/**
 * @author wuxii@foxmail.com
 */
public class Dest extends VoSupport {

    private static final long serialVersionUID = 1L;
    private SubDest subDest;

    public Dest() {
        super();
    }

    public Dest(SubDest subDest) {
        super();
        this.subDest = subDest;
    }

    public SubDest getSubDest() {
        return subDest;
    }

    public void setSubDest(SubDest subDest) {
        this.subDest = subDest;
    }

    @Override
    public String toString() {
        return getClass().getName() + ":{\"subDest\":\"" + subDest + "\"}";
    }

}
