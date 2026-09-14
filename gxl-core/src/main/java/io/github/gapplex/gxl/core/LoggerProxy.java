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

import java.util.function.Function;

public class LoggerProxy implements Logger {
    private volatile Logger logger;
    private final String name;

    LoggerProxy(String name) {
        this.name = name;
    }

    @Override
    public void debug(String msg) {
        lazyLoad();
        logger.debug(msg);
    }

    @Override
    public void info(String msg) {
        lazyLoad();
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        lazyLoad();
        logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        lazyLoad();
        logger.error(msg);
    }

    @Override
    public void debug(String msg, Throwable t) {
        lazyLoad();
        logger.debug(msg, t);
    }

    @Override
    public void info(String msg, Throwable t) {
        lazyLoad();
        logger.info(msg, t);
    }

    @Override
    public void warn(String msg, Throwable t) {
        lazyLoad();
        logger.warn(msg, t);
    }

    @Override
    public void error(String msg, Throwable t) {
        lazyLoad();
        logger.error(msg, t);
    }

    @Override
    public void debug(String msg, Object... args) {
        lazyLoad();
        logger.debug(msg, args);
    }

    @Override
    public void info(String msg, Object... args) {
        lazyLoad();
        logger.info(msg, args);
    }

    @Override
    public void warn(String msg, Object... args) {
        lazyLoad();
        logger.warn(msg, args);
    }

    @Override
    public void error(String msg, Object... args) {
        lazyLoad();
        logger.error(msg, args);
    }

    @Override
    public boolean isDebugEnabled() {
        lazyLoad();
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        lazyLoad();
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        lazyLoad();
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        lazyLoad();
        return logger.isErrorEnabled();
    }

    private void lazyLoad() {
        if (logger == null) {
            synchronized (this) {
                if (logger == null) {
                    Function<String, Logger> creator = LoggerFactory.getCreator();
                    if (creator == LoggerFactory.NOOP_LOGGER_LAMBDA) {
                        LoggerFactory.warnIfUnconfigured();
                        logger = Logger.noop();
                        return;
                    }
                    try {
                        Logger created = creator.apply(name);
                        if (created == null) {
                            throw new IllegalArgumentException("Unexpected null for '" + name + "'.");
                        }
                        logger = created;
                    } catch (Exception e){
                        Constants.STDERR().println("ERROR: Failed to create logger for '" + name + "'. Falling back to NOOP. Cause: " + e.getMessage());
                        logger = Logger.noop();
                    }
                }
            }
        }
    }

    void reload(){
        synchronized (this){
            Function<String, Logger> creator = LoggerFactory.getCreator();
            if (creator == LoggerFactory.NOOP_LOGGER_LAMBDA) {
                LoggerFactory.warnIfUnconfigured();
                logger = Logger.noop();
                return;
            }
            try {
                Logger created = creator.apply(name);
                if (created == null) {
                    throw new IllegalArgumentException("Unexpected null for '" + name + ".");
                }
                logger = created;
            } catch (Exception e){
                Constants.STDERR().println("ERROR: Failed to create logger for '" + name + "'. Falling back to NOOP. Cause: " + e.getMessage());
                logger = Logger.noop();
            }
        }
    }

    Logger getDelegate() {
        return logger;
    }
}