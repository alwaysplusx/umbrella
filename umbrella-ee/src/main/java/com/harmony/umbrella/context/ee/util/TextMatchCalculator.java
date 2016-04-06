/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.context.ee.util;

import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public final class TextMatchCalculator {

    public static double matchingRate(String text1, String text2) {
        if (StringUtils.isBlank(text1) || StringUtils.isBlank(text2)) {
            return 0.0;
        }

        String[] words1 = splitWord(text1);
        String[] words2 = splitWord(text2);
        if (words1.length < words2.length) {
            String[] tmp = words2;
            words2 = words1;
            words1 = tmp;
        }

        int matchCount = 0;
        for (String w1 : words1) {
            w1 = w1.toLowerCase();
            for (String w2 : words2) {
                w2 = w2.toLowerCase();
                if (w1.indexOf(w2) != -1 || w2.indexOf(w1) != -1) {
                    matchCount++;
                }
            }
        }

        return (double) matchCount / words1.length;
    }

    public static String[] splitWord(String text) {
        List<String> words = new ArrayList<String>();
        int lastIndex = 0;
        for (int i = 1, max = text.length(); i < max; i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                words.add(text.substring(lastIndex, i));
                lastIndex = i;
            }
        }
        words.add(text.substring(lastIndex, text.length()));
        return words.toArray(new String[words.size()]);
    }

}