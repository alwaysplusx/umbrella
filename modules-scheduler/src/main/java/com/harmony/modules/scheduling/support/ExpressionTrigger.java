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
package com.harmony.modules.scheduling.support;

import java.util.StringTokenizer;

import com.harmony.modules.scheduling.Trigger;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionTrigger implements Trigger {

    private String[] expressions = new String[] { "9999", "12", "31", "*", "23", "59", "59", "0" };

    public ExpressionTrigger() {
    }

    public ExpressionTrigger(String expression) {
        StringTokenizer token = new StringTokenizer(expression, " ");
        for (int i = 0, max = expressions.length; i < max && token.hasMoreTokens(); i++) {
            expressions[i] = token.nextToken();
        }
    }

    @Override
    public String getYears() {
        return expressions[0];
    }

    @Override
    public String getMonths() {
        return expressions[1];
    }

    @Override
    public String getDayOfMonth() {
        return expressions[2];
    }

    @Override
    public String getDayOfWeek() {
        return expressions[3];
    }

    @Override
    public String getHours() {
        return expressions[4];
    }

    @Override
    public String getMinutes() {
        return expressions[5];
    }

    @Override
    public String getSeconds() {
        return expressions[6];
    }

    @Override
    public long getDelay() {
        return expressions[7] == null ? 0 : Long.parseLong(expressions[7]);
    }

    @Override
    public String toString() {
        return expressions.toString();
    }

}
