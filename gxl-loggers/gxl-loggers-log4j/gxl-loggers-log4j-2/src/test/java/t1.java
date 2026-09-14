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
import io.github.gapplex.gxl.loggers.log4j.two.Log4j2Installer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.util.ArrayList;
import java.util.List;

public class t1 {
    static {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.WARN);

        builder.add(builder.newAppender("File", "FILE")
                .addAttribute("fileName", "test.log") // 写入项目根目录
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n")));
        builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("File")));

        Configurator.initialize(builder.build());

        Log4j2Installer.install();
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(t1.class);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 16; i++){
            threads.add(new Thread(() -> {
                int in = 0;
                while (in < 1000000){
                    logger.info(String.valueOf(in));
                    in++;
                }
            }));
        }

        for (Thread thread : threads){
            thread.start();
        }
    }
}