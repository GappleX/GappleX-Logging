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
package io.github.gapplex.gxl.loggers.log4j.one;

import io.github.gapplex.gxl.core.utils.FormattingTuple;
import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.utils.MessageFormatter;
import org.apache.log4j.Level;

public class Log4j1Adapter implements Logger {
    private final org.apache.log4j.Logger logger;

    Log4j1Adapter(String name){
        logger = org.apache.log4j.LogManager.getLogger(name);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (t != null) {
            logger.debug(msg, t);
        } else {
            logger.debug(msg);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (t != null) {
            logger.info(msg, t);
        } else {
            logger.info(msg);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (t != null) {
            logger.warn(msg, t);
        } else {
            logger.warn(msg);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (t != null) {
            logger.error(msg, t);
        } else {
            logger.error(msg);
        }
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void debug(String msg, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        debug(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public void info(String msg, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        info(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public void warn(String msg, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        warn(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public void error(String msg, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.format(msg, args);
        error(formattingTuple.getMessage(), formattingTuple.getThrowable());
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Level.ERROR);
    }
}
