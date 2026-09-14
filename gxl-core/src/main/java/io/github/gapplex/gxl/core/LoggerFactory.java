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

import io.github.gapplex.gxl.core.utils.JSONUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class LoggerFactory {
    private static final int MAX_DISPLAYED_LOGGERS =
            Integer.getInteger("gxl.status.maxDisplay", 10);

    private static final Map<String, LoggerProxy> CACHE = new ConcurrentHashMap<>();
    private static final Object LOCK = new Object();
    public static final Function<String, Logger> NOOP_LOGGER_LAMBDA = n -> Logger.noop();

    private static volatile Function<String, Logger> creator = NOOP_LOGGER_LAMBDA;
    private static volatile String creatorName;

    private static volatile boolean set = false;
    private static final AtomicBoolean WARNED = new AtomicBoolean(false);
    private static final boolean suppress = isSuppressed();

    static Function<String, Logger> getCreator(){
        return creator;
    }

    public static boolean isSet(){
        return set;
    }

    public static String getCreatorName(){
        return creatorName;
    }

    static void warnIfUnconfigured() {
        if (!set && WARNED.compareAndSet(false, true)) {
            if (!suppress) Constants.STDERR().println("WARNING: LoggerFactory is unconfigured (defaulting to NOOP). " +
                    "Please call LoggerFactory.setLoggerCreator(yourCustomLogger::new) " +
                    "or use provided installers (SLF4JInstaller/Log4j2Installer) to enable logging.");
        }
    }

    public static void setLoggerCreator(Function<String, Logger> creator, boolean force) {
        synchronized (LOCK) {
            if (set && !force) {
                if (!suppress) Constants.STDERR().println("WARNING: LoggerCreator already set. Ignoring. Use force=true to override.");
                return;
            }

            if (creator == null) {
                if (!suppress) Constants.STDERR().println("WARNING: Creator passed as null, falling back to NOOP.");
                creator = NOOP_LOGGER_LAMBDA;
            }

            Logger testLogger;
            try {
                testLogger = creator.apply("__gxl_validation__");
                if (testLogger == null) {
                    Constants.STDERR().println("FATAL: New LoggerCreator returned null. Refusing to apply.");
                    return;
                }
            } catch (Exception e) {
                Constants.STDERR().println("FATAL: New LoggerCreator threw exception during validation: " + e.getMessage());
                e.printStackTrace(Constants.STDERR());
                return;
            }
            LoggerFactory.creator = creator;
            LoggerFactory.creatorName = testLogger.getClass().getName();

            if (set && force) {
                if (!suppress) Constants.STDERR().println("INFO: Forcibly overriding LoggerCreator. Reloading cached loggers.");
                for (LoggerProxy proxy : CACHE.values()) {
                    proxy.reload();
                }
            }
            set = true;
        }
    }

    public static void setLoggerCreator(Function<String, Logger> creator) {
        setLoggerCreator(creator, false);
    }

    public static void resetToNoop(){
        setLoggerCreator(NOOP_LOGGER_LAMBDA, true);
    }

    public static Logger getLogger(Class<?> clazz) {
        String name = clazz.getName();
        return CACHE.computeIfAbsent(name, LoggerProxy::new);
    }

    public static Logger getLogger(String name){
        return CACHE.computeIfAbsent(name, LoggerProxy::new);
    }

    public static String getStatus(){
        return JSONUtil.objectToJson(getStatusMap());
    }

    public static Map<String, Object> getStatusMap(){
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("set", set);
        if (creatorName == null){
            status.put("creator", "Maybe it hasn't loaded yet?");
        } else {
            status.put("creator", creatorName);
        }
        if (CACHE.size() < MAX_DISPLAYED_LOGGERS){
            status.put("cachedLoggers", CACHE.keySet());
        } else {
            List<String> list = new ArrayList<>(CACHE.keySet()).subList(0, MAX_DISPLAYED_LOGGERS);
            list.add("... (" + (CACHE.size() - MAX_DISPLAYED_LOGGERS) + " more)");
            status.put("cachedLoggers", list);
        }
        status.put("cacheSize", CACHE.size());
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("LoggerFactory", status);

        return root;
    }

    static boolean isSuppressed(){
        String raw = System.getProperty("gxl.suppress", "false");
        return Boolean.parseBoolean(raw);
    }

    public static boolean isSuppress(){
        return suppress;
    }
}
