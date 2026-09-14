/*
 * Copyright 2026 GapplX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.gapplex.gxl.core;

import java.io.PrintStream;

public class Constants {
    //Level
    public static final int DEBUG_INT = 0;
    public static final int INFO_INT = 1;
    public static final int WARN_INT = 2;
    public static final int ERROR_INT = 3;

    public static final String DEBUG_STR = "DEBUG";
    public static final String INFO_STR = "INFO";
    public static final String WARN_STR = "WARN";
    public static final String ERROR_STR = "ERROR";

    //Output
    private static PrintStream STDOUT = System.out;
    private static PrintStream STDERR = System.err;

    public static PrintStream STDOUT(){
        return STDOUT;
    }

    public static PrintStream STDERR(){
        return STDERR;
    }

    static void changeStdout(PrintStream newPrintStream){
        STDOUT = newPrintStream;
    }

    static void changeStderr(PrintStream newPrintStream){
        STDERR = newPrintStream;
    }
}
