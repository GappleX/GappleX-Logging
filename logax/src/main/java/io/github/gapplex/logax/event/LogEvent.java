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
package io.github.gapplex.logax.event;

import io.github.gapplex.gxl.core.Level;

public class LogEvent {
    private final long timestamp;
    private final String loggerName;
    private final Level level;
    private final String message;
    private final Object[] args;
    private final Throwable throwable;

    public LogEvent(long timestamp, String loggerName, Level level, String message, Object[] args, Throwable t){
        this.timestamp = timestamp;
        this.loggerName = loggerName;
        this.level = level;
        this.message = message;
        this.args = args;
        throwable = t;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public String getLoggerName(){
        return loggerName;
    }

    public Level getLevel(){
        return level;
    }

    public String getMessage(){
        return message;
    }

    public Object[] getArgs(){
        return args;
    }

    public Throwable getThrowable(){
        return throwable;
    }
}
