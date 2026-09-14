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
package test;

import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.LoggerFactory;
import io.github.gapplex.gxl.core.LoggerProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerConcurrencyTest {

    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        LoggerFactory.resetToNoop();
        // 重置全局计数器
        CountingLogger.debugCount.set(0);
        CountingLogger.infoCount.set(0);
        CountingLogger.warnCount.set(0);
        CountingLogger.errorCount.set(0);
        executor = Executors.newCachedThreadPool();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        LoggerFactory.resetToNoop();
    }

    // 工具：解包 LoggerProxy 获取内部委托
    private static Logger unwrap(Logger proxy) {
        if (proxy instanceof LoggerProxy) {
            try {
                Field field = LoggerProxy.class.getDeclaredField("logger");
                field.setAccessible(true);
                return (Logger) field.get(proxy);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return proxy;
    }

    // === 测试1：纯并发调用，不重载 ===
    @Test
    void concurrentLoggingWithoutSwitch() throws Exception {
        Function<String, Logger> creator = CountingLogger::new;
        LoggerFactory.setLoggerCreator(creator, true);

        int threadCount = 20;
        int iterationsPerThread = 500;
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    Logger logger = LoggerFactory.getLogger("test");
                    for (int j = 0; j < iterationsPerThread; j++) {
                        logger.debug("msg");
                        logger.info("msg");
                        logger.warn("msg");
                        logger.error("msg");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        startLatch.countDown();
        for (Future<?> f : futures) f.get();

        long expected = threadCount * iterationsPerThread;
        assertThat(CountingLogger.debugCount.get()).isEqualTo(expected);
        assertThat(CountingLogger.infoCount.get()).isEqualTo(expected);
        assertThat(CountingLogger.warnCount.get()).isEqualTo(expected);
        assertThat(CountingLogger.errorCount.get()).isEqualTo(expected);
    }

    // === 测试2：并发调用 + 一次强制重载（重载后继续调用） ===
    @Test
    void concurrentLoggingWithOneForceReload() throws Exception {
        // 初始 creator
        Function<String, Logger> creator1 = CountingLogger::new;
        LoggerFactory.setLoggerCreator(creator1, true);

        int threadCount = 20;
        int iterationsBefore = 300;
        int iterationsAfter = 200;
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        // 日志线程
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    Logger logger = LoggerFactory.getLogger("test");
                    // 重载前的调用
                    for (int j = 0; j < iterationsBefore; j++) {
                        logger.debug("before");
                    }
                    // 等待重载发生（由另一个线程触发）
                    Thread.sleep(100);
                    // 重载后的调用
                    for (int j = 0; j < iterationsAfter; j++) {
                        logger.debug("after");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        // 重载线程：在中间时刻强制重载
        futures.add(executor.submit(() -> {
            try {
                startLatch.await();
                Thread.sleep(50); // 确保部分 before 调用已完成
                Function<String, Logger> creator2 = name -> new CountingLogger(name + "-reloaded");
                LoggerFactory.setLoggerCreator(creator2, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        startLatch.countDown();
        for (Future<?> f : futures) f.get();

        // 总调用次数 = 所有线程 * (before + after)
        long expectedCalls = threadCount * (iterationsBefore + iterationsAfter);
        assertThat(CountingLogger.debugCount.get()).isEqualTo(expectedCalls);

        // 验证重载生效：获取代理，解包，检查内部 logger 的 name 是否包含 "-reloaded"
        Logger proxy = LoggerFactory.getLogger("test");
        Logger delegate = unwrap(proxy);
        assertThat(delegate).isInstanceOf(CountingLogger.class);
        CountingLogger cl = (CountingLogger) delegate;
        assertThat(cl.getName()).endsWith("-reloaded");
    }

    // === 测试3：并发设置 LoggerCreator（竞态） ===
    @RepeatedTest(10) // 重复执行暴露问题
    void concurrentSetCreator() throws Exception {
        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    // 每个线程尝试设置不同的 creator，有的 force=true，有的 force=false
                    Function<String, Logger> creator = name -> new CountingLogger(name + "-" + index);
                    if (index % 2 == 0) {
                        LoggerFactory.setLoggerCreator(creator, true);
                    } else {
                        LoggerFactory.setLoggerCreator(creator, false);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        startLatch.countDown();
        for (Future<?> f : futures) f.get();

        // 验证最终状态：至少有一个 creator 被设置，且无异常
        assertThat(LoggerFactory.isSet()).isTrue();
        // 可以进一步验证最终 creator 是否有效（通过获取一个 logger 并调用）
        Logger logger = LoggerFactory.getLogger("test");
        logger.debug("final check");
        // 若未抛异常，表示一切正常
    }
}