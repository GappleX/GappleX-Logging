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
package io.github.gapplex.logax;

import io.github.gapplex.gxl.core.Level;
import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.utils.FormattingTuple;
import io.github.gapplex.gxl.core.utils.MessageFormatter;
import io.github.gapplex.logax.appender.Appender;
import io.github.gapplex.logax.event.LogEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogaxLogger implements Logger {
    private final String name;
    private volatile Level level = Logax.getRootLevel();
    private volatile List<Appender> appenders = new CopyOnWriteArrayList<>();

    LogaxLogger(String name){
        this.name = name;
    }

    @Override
    public void debug(String msg, Throwable t) {
        log(Level.DEBUG, msg, t);
    }

    @Override
    public void info(String msg, Throwable t) {
        log(Level.INFO, msg, t);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(Level.WARN, msg, t);
    }

    @Override
    public void error(String msg, Throwable t) {
        log(Level.ERROR, msg, t);
    }

    @Override
    public void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    @Override
    public void info(String msg) {
        log(Level.INFO, msg);
    }

    @Override
    public void warn(String msg) {
        log(Level.WARN, msg);
    }

    @Override
    public void error(String msg) {
        log(Level.ERROR, msg);
    }

    @Override
    public void debug(String msg, Object... args) {
        log(Level.DEBUG, msg, args);
    }

    @Override
    public void info(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }

    @Override
    public void warn(String msg, Object... args) {
        log(Level.WARN, msg, args);
    }

    @Override
    public void error(String msg, Object... args) {
        log(Level.ERROR, msg, args);
    }

    @Override
    public boolean isDebugEnabled() {
        return level.toInt() <= Level.DEBUG.toInt();
    }

    @Override
    public boolean isInfoEnabled() {
        return level.toInt() <= Level.INFO.toInt();
    }

    @Override
    public boolean isWarnEnabled() {
        return level.toInt() <= Level.WARN.toInt();
    }

    @Override
    public boolean isErrorEnabled() {
        return level.toInt() <= Level.ERROR.toInt();
    }

    private void log(Level level, String msg, Object... args) {
        if (!isEnabledLevel(level)) return;

        FormattingTuple formattedMsg = MessageFormatter.format(msg, args);

        LogEvent event = new LogEvent(
                System.currentTimeMillis(),
                name,
                level,
                formattedMsg.getMessage(),
                args,
                formattedMsg.getThrowable()
        );

        for (Appender app : appenders) {
            try {
                app.append(event);
            } catch (Exception e) {
                System.err.println("[Logax] Appender error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void setLevel(Level level) {
        this.level = level;
    }

    void addAppender(Appender appender) {
        if (appender != null) {
            appenders.add(appender);
            appender.start();
        }
    }

    void removeAppender(Appender appender) {
        if (appender != null) {
            appenders.remove(appender);
            appender.stop();
        }
    }

    void stopAllAppenders() {
        for (Appender app : appenders) {
            try { app.stop(); } catch (Exception ignored) {}
        }
        appenders.clear();
    }
}
