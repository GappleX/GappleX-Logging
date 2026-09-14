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

class SimpleLogger implements Logger {
    private final String name;
    private final Level level;

    SimpleLogger(String name) {
        this.name = name;
        level = Level.stringToLevel(System.getProperty("gxl.level", "INFO"));
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) Constants.STDOUT().println("[DEBUG] [" + name + "] " + msg);
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) Constants.STDOUT().println("[INFO ] [" + name + "] " + msg);
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) Constants.STDOUT().println("[WARN ] [" + name + "] " + msg);
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) Constants.STDOUT().println("[ERROR] [" + name + "] " + msg);
    }

    @Override
    public void debug(String msg, Throwable t){
        debug(msg);
        if (t != null && isDebugEnabled()) t.printStackTrace(Constants.STDERR());
    }

    @Override
    public void info(String msg, Throwable t){
        info(msg);
        if (t != null && isInfoEnabled()) t.printStackTrace(Constants.STDERR());
    }

    @Override
    public void warn(String msg, Throwable t){
        warn(msg);
        if (t != null && isWarnEnabled()) t.printStackTrace(Constants.STDERR());
    }

    @Override
    public void error(String msg, Throwable t){
        error(msg);
        if (t != null && isErrorEnabled()) t.printStackTrace(Constants.STDERR());
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
}