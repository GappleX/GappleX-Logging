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

import io.github.gapplex.logax.event.LogEvent;
import io.github.gapplex.logax.utils.TimeUtils;

import java.util.Arrays;

public enum EventField {
    TIMESTAMP {
        @Override
        public String format(LogEvent event) {
            return TimeUtils.formatTime(event.getTimestamp());
        }
    }, LOGGER_NAME {
        @Override
        public String format(LogEvent event) {
            return event.getLoggerName();
        }
    }, LEVEL {
        @Override
        public String format(LogEvent event) {
            return event.getLevel().toString();
        }
    }, MESSAGE {
        @Override
        public String format(LogEvent event) {
            return event.getMessage();
        }
    }, ARGS {
        @Override
        public String format(LogEvent event) {
            Object[] args = event.getArgs();
            if (args == null){
                return "";
            }
            return Arrays.deepToString(args);
        }
    }, THROWABLE {
        @Override
        public String format(LogEvent event) {
            Throwable throwable = event.getThrowable();
            if (throwable == null){
                return "";
            }
            return throwable.toString();
        }
    };

    public abstract String format(LogEvent event);
}
