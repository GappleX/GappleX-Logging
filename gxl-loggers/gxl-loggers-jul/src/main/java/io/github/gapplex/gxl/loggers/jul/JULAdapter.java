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
package io.github.gapplex.gxl.loggers.jul;

import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.utils.FormattingTuple;
import io.github.gapplex.gxl.core.utils.MessageFormatter;

import java.util.logging.Level;

public class JULAdapter implements Logger {
    private final java.util.logging.Logger logger;
    JULAdapter(String name){
        logger = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.log(Level.FINE, msg, t);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.log(Level.INFO, msg, t);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.log(Level.WARNING, msg, t);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.log(Level.SEVERE, msg, t);
    }

    @Override
    public void debug(String msg) {
        logger.fine(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warning(msg);
    }

    @Override
    public void error(String msg) {
        logger.severe(msg);
    }

    @Override
    public void debug(String msg, Object... args) {
        if (!isDebugEnabled()){
            return;
        }
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        debug(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public void info(String msg, Object... args) {
        if (!isInfoEnabled()){
            return;
        }
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        info(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public void warn(String msg, Object... args) {
        if (!isWarnEnabled()){
            return;
        }
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        warn(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public void error(String msg, Object... args) {
        if (!isErrorEnabled()){
            return;
        }
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        error(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }
}
