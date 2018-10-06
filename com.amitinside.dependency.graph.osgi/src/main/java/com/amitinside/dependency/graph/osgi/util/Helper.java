/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.dependency.graph.osgi.util;

public final class Helper {

    private Helper() {
        throw new IllegalAccessError("Cannot instantiate");
    }

    /**
     * Matches wildcard pattern with support for '?' and '*'.
     * '?' Matches any single character.
     * '*' Matches any sequence of characters (including the empty sequence).
     * The matching should cover the entire input string (not partial).
     * The function prototype should be:
     * bool isMatch(const char *s, const char *p)
     * Some examples:
     * isMatch("aa","a") → false
     * isMatch("aa","aa") → true
     * isMatch("aaa","aa") → false
     * isMatch("aa", "*") → true
     * isMatch("aa", "a*") → true
     * isMatch("ab", "?*") → true
     * isMatch("aab", "c*a*b") → false
     *
     * @param source source string to match
     * @param pattern pattern to match
     */
    public static boolean isMatch(final String source, final String pattern) {
        final int lenS = source.length();
        final int lenP = pattern.length();
        if (lenS == 0 && lenP == 0) {
            return true;
        }
        int i = 0;
        int j = 0;
        // save the last matched index
        int startS = -1;
        int startP = -1;
        while (i < lenS) {
            if (j < lenP && (source.charAt(i) == pattern.charAt(j) || pattern.charAt(j) == '?')) {
                i++;
                j++;
            } else if (j < lenP && pattern.charAt(j) == '*') {
                while (j < lenP && pattern.charAt(j) == '*') {
                    j++;
                }
                if (j == lenP) {
                    return true;
                }
                startP = j;
                startS = i;
            } else if ((j >= lenP || source.charAt(i) != pattern.charAt(j)) && startP > -1) {
                startS++;
                j = startP;
                i = startS;
            } else {
                return false;
            }
        }
        while (j < lenP) {
            if (pattern.charAt(j) != '*') {
                return false;
            }
            j++;
        }
        return true;
    }
}