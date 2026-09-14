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
package io.github.gapplex.gxl.core.utils;

import java.util.ArrayList;
import java.util.List;

public class MessageFormatter {
    public static FormattingTuple format(String msg, Object... rawArgs){
        Throwable throwable = null;

        Object[] args = rawArgs;
        if (rawArgs == null) {
            args = new Object[]{};
        } else if (rawArgs.length > 0 && rawArgs[rawArgs.length - 1] instanceof Throwable) {
            throwable = (Throwable) rawArgs[rawArgs.length - 1];
            args = new Object[rawArgs.length - 1];
            System.arraycopy(rawArgs, 0, args, 0, rawArgs.length - 1);
        }

        if (msg == null) {
            return new FormattingTuple("null", throwable);
        }

        StringBuilder result = new StringBuilder(msg.length() + (args.length * 3));

        int i = 0;
        int argIndex = 0;
        while (i < msg.length()){
            char currentChar = msg.charAt(i);

            if (currentChar == '\\'){
                if (i + 1 < msg.length()){
                    char next = msg.charAt(i + 1);
                    if (next == '\\'){
                        result.append('\\');
                    } else if (next == '{'){
                        result.append(next);
                    } else {
                        result.append('\\').append(next);
                    }
                    i++;
                } else {
                    result.append('\\');
                }
            } else if (currentChar == '{'){
                if (i + 1 < msg.length()){
                    char next = msg.charAt(i + 1);
                    if (next == '}'){
                        if (args.length <= argIndex){
                            result.append("null");
                        } else {
                            result.append(args[argIndex]);
                        }
                        argIndex++;
                    } else {
                        result.append('{').append(next);
                    }
                    i++;
                } else {
                    result.append('{');
                }
            } else {
                result.append(currentChar);
            }

            i++;
        }

        return new FormattingTuple(result.toString(), throwable);
    }

    public static String formatWithMessage(String msg, Object... arg){
        return format(msg, arg).getMessage();
    }

    public static List<Object> formatWithArray(String msg, Object... rawArgs){
        List<Object> resultList = new ArrayList<>();
        StringBuilder result = new StringBuilder(msg.length() + (rawArgs.length * 3));

        int i = 0;
        int argIndex = 0;
        while (i < msg.length()){
            char currentChar = msg.charAt(i);

            if (currentChar == '\\'){
                if (i + 1 < msg.length()){
                    char next = msg.charAt(i + 1);
                    if (next == '\\'){
                        result.append('\\');
                    } else if (next == '{'){
                        result.append(next);
                    } else {
                        result.append('\\').append(next);
                    }
                    i++;
                } else {
                    result.append('\\');
                }
            } else if (currentChar == '{'){
                if (i + 1 < msg.length()){
                    char next = msg.charAt(i + 1);
                    if (next == '}'){
                        if (result.length() > 0){
                            resultList.add(result.toString());
                            result.setLength(0);
                        }
                        if (rawArgs.length <= argIndex){
                            resultList.add("null");
                        } else {
                            resultList.add(rawArgs[argIndex]);
                        }
                        argIndex++;
                    } else {
                        result.append('{').append(next);
                    }
                    i++;
                } else {
                    result.append('{');
                }
            } else {
                result.append(currentChar);
            }

            i++;
        }

        if (result.length() > 0){
            resultList.add(result.toString());
            result.setLength(0);
        }

        return resultList;
    }
}
