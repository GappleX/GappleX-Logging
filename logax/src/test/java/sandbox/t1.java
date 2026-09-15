package sandbox;/*
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
import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.LoggerFactory;
import io.github.gapplex.logax.EventField;
import io.github.gapplex.logax.appender.AsyncAppender;
import io.github.gapplex.logax.appender.ConsoleAppender;
import io.github.gapplex.logax.Logax;
import io.github.gapplex.logax.layout.YouDefLayout;

public class t1 {
    static {
        Logax.install(new AsyncAppender(new ConsoleAppender(new YouDefLayout("[{}] [{}] [{}] - {} [Throwable] {}", EventField.TIMESTAMP, EventField.LEVEL, EventField.LOGGER_NAME, EventField.MESSAGE, EventField.THROWABLE)), 1024));
    }
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(t1.class);
        logger.info("hi");
        logger.warn("hello{}", " world");
        logger.error("boom!", new RuntimeException("Boom!"));
    }
}
