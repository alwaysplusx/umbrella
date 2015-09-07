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
package com.harmony.umbrella.base;

import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.io.FileSystemResource;
import com.harmony.umbrella.io.Resource;

/**
 * @author wuxii@foxmail.com
 */
public class SplitWordTest {

    public static void main(String[] args) {
        Class<?> a = Resource.class;
        Class<?> b = FileSystemResource.class;
        String aName = a.getSimpleName();
        String bName = b.getSimpleName();
        System.out.println(matchingRate(bName, aName));
    }

    public static Double matchingRate(String longString, String shortString) {
        String[] w1 = splitWord(longString);
        String[] w2 = splitWord(shortString);
        return w1.length > w2.length ? matchingRate(w1, w2) : matchingRate(w2, w1);
    }

    private static Double matchingRate(String[] w1, String[] w2) {
        if (w1.length == 0) {
            return 0d;
        }
        int count = 0;
        for (String leftWord : w1) {
            for (String rightWord : w2) {
                if (leftWord.equals(rightWord) || leftWord.indexOf(rightWord) > 0) {
                    count++;
                }
            }
        }
        return (double) count / w1.length;
    }

    private static String[] splitWord(String text) {
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
