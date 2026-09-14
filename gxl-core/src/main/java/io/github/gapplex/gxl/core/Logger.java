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

import io.github.gapplex.gxl.core.utils.FormattingTuple;
import io.github.gapplex.gxl.core.utils.MessageFormatter;

public interface Logger {
    Logger NOOP_LOGGER = new NoopLogger();

    default void debug(String msg, Throwable t){
        debug(msg);
        if (t != null) t.printStackTrace(System.err);
    }
    default void info(String msg, Throwable t){
        info(msg);
        if (t != null) t.printStackTrace(System.err);
    }
    default void warn(String msg, Throwable t){
        warn(msg);
        if (t != null) t.printStackTrace(System.err);
    }
    default void error(String msg, Throwable t){
        error(msg);
        if (t != null) t.printStackTrace(System.err);
    }

    void debug(String msg);
    void info(String msg);
    void warn(String msg);
    void error(String msg);

    default void debug(String msg, Object... args){
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        debug(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }
    default void info(String msg, Object... args){
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        info(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }
    default void warn(String msg, Object... args){
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        warn(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }
    default void error(String msg, Object... args){
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        error(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }
    default boolean isEnabledLevel(Level level){
        switch (level){
            case DEBUG:return isDebugEnabled();
            case INFO:return isInfoEnabled();
            case WARN:return isWarnEnabled();
            case ERROR:return isErrorEnabled();
            default:{
                throw new IllegalArgumentException("Unknown level '" + level + "'");
            }
        }
    }

    boolean isDebugEnabled();
    boolean isInfoEnabled();
    boolean isWarnEnabled();
    boolean isErrorEnabled();

    static Logger simple(String name) {
        return new SimpleLogger(name);
    }

    static Logger noop() {
        return NOOP_LOGGER;
    }
}
