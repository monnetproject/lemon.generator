/**********************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/
package eu.monnetproject.lemon.generator.lela;
import java.util.*;

/**
 * Provides a number of Perl like functions for string manipulation
 *
 * @author John McCrae
 */
public class Strings {
    /** The end of record of token 
     * @see #chomp(String)
     */
    public static char inputRecordSeperator = '\n';
    
    /** Convert the first character to lower case */
    public static String toLCFirst(String s) {
        if(s.length() == 0)
            return s;
        return s.substring(0,1).toLowerCase() + s.substring(1);
    }
    
    /** Convert the first character to upper case */
    public static String toUCFirst(String s) {
        if(s.length() == 0)
            return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
    
    /** Remove the final character if it is inputRecordSeperator */
    public static String chomp(String s) {
        if(s.length() == 0)
            return s;
        if(s.charAt(s.length() - 1) == inputRecordSeperator)
            return s.substring(0,s.length() - 1);
        else
            return s;
    }
    
    /** Remove the final character */
    public static String chop(String s) {
        return s.substring(0,s.length() - 1);
    }
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, String[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i];
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, Object[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i].toString();
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, char[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i];
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, int[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i];
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, long[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i];
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
    
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, float[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i];
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
    
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, double[] strings) {
        String rval = "";
        for(int i = 0; i < strings.length; i++) {
            rval = rval + strings[i];
            if(i < strings.length - 1)
                rval = rval + seperator;
        }
        return rval;
    }
   
    /** Join a set of strings together. Example join(", ", {"Hello","World!}) = "Hello, World!" */
    public static String join(String seperator, Collection<?> strings) {
        String rval = "";
        Iterator<?> iter = strings.iterator();
        while(iter.hasNext()) {
            rval = rval + iter.next().toString();
            if(iter.hasNext())
                rval = rval + seperator;
        }
        return rval;
    }
    
    /** Escape all regex meta-characters */
    public static String quoteMeta(String s) {
        return s.replaceAll("([\\.\\[\\]\\^\\$\\|\\?\\(\\)\\\\\\+\\{\\}\\*])","\\\\$1");
    }
    
    /** Create a new string by repeating s n-times */
    public static String repString(String s, int n) {
        String rval = "";
        for(int i = 0; i < n; i++) {
            rval = rval + s;
        }
        return rval;
    }
}
