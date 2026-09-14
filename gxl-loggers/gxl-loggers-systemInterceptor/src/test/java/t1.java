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
import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.LoggerFactory;
import io.github.gapplex.gxl.loggers.sysIntcpt.OutputInterceptor;

public class t1 {
    public static void main(String[] args) {
        System.setProperty("gxl.suppress", "false");

        LoggerFactory.setLoggerCreator(LoggerFactory.NOOP_LOGGER_LAMBDA);

        OutputInterceptor.install();

        LoggerFactory.getLogger("OUTPUT").info("hi");
        System.out.println("这是一条普通日志");
        System.err.println("这是一条错误");

        LoggerFactory.setLoggerCreator(Logger::simple, true);
        Logger logger = LoggerFactory.getLogger("1");
        logger.info("1");
        System.out.println("这是一条普通日志");
        System.err.println("这是一条错误");
    }
}
