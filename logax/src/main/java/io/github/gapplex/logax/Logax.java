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
import io.github.gapplex.gxl.core.LoggerFactory;
import io.github.gapplex.logax.appender.Appender;
import io.github.gapplex.logax.appender.ConsoleAppender;
import io.github.gapplex.logax.layout.SimpleLayout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Logax {
    private static volatile Level rootLevel = Level.INFO;
    private static final List<Appender> rootAppenders = new CopyOnWriteArrayList<>();
    private static final Map<String, LogaxLogger> loggerCache = new ConcurrentHashMap<>();
    private static volatile boolean installed = false;

    private Logax() {}

    static Level getRootLevel(){
        return rootLevel;
    }

    public static synchronized void install(Appender appender) {
        if (installed) {
            return;
        }
        LoggerFactory.setLoggerCreator(Logax::getLogger, true);
        if (rootAppenders.isEmpty()) {
            if (appender != null){
                addRootAppender(appender);
            } else {
                addRootAppender(new ConsoleAppender(new SimpleLayout()));
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread(Logax::shutdown));
        installed = true;
    }

    static Logger getLogger(String name) {
        return loggerCache.computeIfAbsent(name, (key) -> {
            LogaxLogger logger = new LogaxLogger(key);
            logger.setLevel(rootLevel);
            for (Appender app : rootAppenders) {
                logger.addAppender(app);
            }
            return logger;
        });
    }

    public static void setRootLevel(Level level) {
        if (level == null) return;
        rootLevel = level;
        for (LogaxLogger logger : loggerCache.values()){
            logger.setLevel(rootLevel);
        }
    }

    public static void addRootAppender(Appender appender) {
        if (appender == null) return;
        rootAppenders.add(appender);
        for (LogaxLogger logger : loggerCache.values()) {
            logger.addAppender(appender);
        }
    }

    public static void removeRootAppender(Appender appender) {
        if (appender == null) return;
        rootAppenders.remove(appender);
        for (LogaxLogger logger : loggerCache.values()) {
            logger.removeAppender(appender);
        }
    }

    private static void shutdown() {
        for (LogaxLogger logger : loggerCache.values()) {
            logger.stopAllAppenders();
        }
        for (Appender app : rootAppenders) {
            try { app.stop(); } catch (Exception ignored) {}
        }
    }
}
