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

import io.github.gapplex.gxl.core.*;
import io.github.gapplex.gxl.core.utils.FormattingTuple;
import io.github.gapplex.gxl.core.utils.MessageFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

public class LoggerFunctionalTest {
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

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private Method changeStdoutMethod;
    private Method changeStderrMethod;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("gxl.suppress", "true");

        originalOut = System.out;
        originalErr = System.err;
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        changeStdoutMethod = Constants.class.getDeclaredMethod("changeStdout", PrintStream.class);
        changeStdoutMethod.setAccessible(true);
        changeStderrMethod = Constants.class.getDeclaredMethod("changeStderr", PrintStream.class);
        changeStderrMethod.setAccessible(true);

        changeStdoutMethod.invoke(null, new PrintStream(outContent));
        changeStderrMethod.invoke(null, new PrintStream(errContent));

        LoggerFactory.resetToNoop();
        Field cacheField = LoggerFactory.class.getDeclaredField("CACHE");
        cacheField.setAccessible(true);
        Map<String, LoggerProxy> cache = (Map<String, LoggerProxy>) cacheField.get(null);
        cache.clear();

        System.clearProperty("gxl.level");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setOut(originalOut);
        System.setErr(originalErr);
        changeStdoutMethod.invoke(null, originalOut);
        changeStderrMethod.invoke(null, originalErr);

        LoggerFactory.resetToNoop();
        System.clearProperty("gxl.level");
        System.clearProperty("gxl.suppress");
    }

    // ======================== Level 枚举测试 ========================
    @Test
    void testLevelConversions() {
        assertThat(Level.DEBUG.toInt()).isEqualTo(0);
        assertThat(Level.INFO.toInt()).isEqualTo(1);
        assertThat(Level.WARN.toInt()).isEqualTo(2);
        assertThat(Level.ERROR.toInt()).isEqualTo(3);

        assertThat(Level.DEBUG.toString()).isEqualTo("DEBUG");
        assertThat(Level.INFO.toString()).isEqualTo("INFO");
        assertThat(Level.WARN.toString()).isEqualTo("WARN");
        assertThat(Level.ERROR.toString()).isEqualTo("ERROR");

        assertThat(Level.intToLevel(0)).isSameAs(Level.DEBUG);
        assertThat(Level.intToLevel(1)).isSameAs(Level.INFO);
        assertThat(Level.intToLevel(2)).isSameAs(Level.WARN);
        assertThat(Level.intToLevel(3)).isSameAs(Level.ERROR);

        assertThatThrownBy(() -> Level.intToLevel(4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown level int:4");

        assertThat(Level.stringToLevel("DEBUG")).isSameAs(Level.DEBUG);
        assertThat(Level.stringToLevel("  info ")).isSameAs(Level.INFO);
        assertThat(Level.stringToLevel("WARN")).isSameAs(Level.WARN);
        assertThat(Level.stringToLevel("ERROR")).isSameAs(Level.ERROR);
        assertThatThrownBy(() -> Level.stringToLevel("TRACE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown level str:TRACE");
        assertThatThrownBy(() -> Level.stringToLevel(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Level string cannot be null");
    }

    // ======================== SimpleLogger 级别逻辑 ========================
    @Test
    void testSimpleLoggerLevels() {
        Logger logger = Logger.simple("test");
        assertThat(logger.isDebugEnabled()).isFalse();
        assertThat(logger.isInfoEnabled()).isTrue();
        assertThat(logger.isWarnEnabled()).isTrue();
        assertThat(logger.isErrorEnabled()).isTrue();

        System.setProperty("gxl.level", "DEBUG");
        logger = Logger.simple("test");
        assertThat(logger.isDebugEnabled()).isTrue();
        assertThat(logger.isInfoEnabled()).isTrue();

        System.setProperty("gxl.level", "ERROR");
        logger = Logger.simple("test");
        assertThat(logger.isDebugEnabled()).isFalse();
        assertThat(logger.isInfoEnabled()).isFalse();
        assertThat(logger.isWarnEnabled()).isFalse();
        assertThat(logger.isErrorEnabled()).isTrue();
    }

    // ======================== NoopLogger 测试 ========================
    @Test
    void testNoopLogger() {
        Logger logger = Logger.noop();
        assertThat(logger.isDebugEnabled()).isFalse();
        assertThat(logger.isInfoEnabled()).isFalse();
        assertThat(logger.isWarnEnabled()).isFalse();
        assertThat(logger.isErrorEnabled()).isFalse();

        logger.debug("should not appear");
        logger.info("should not appear");
        logger.warn("should not appear");
        logger.error("should not appear");
        logger.debug("with exception", new RuntimeException());
        logger.info("with args", "arg1", "arg2");
        assertThat(outContent.toString()).isEmpty();
        assertThat(errContent.toString()).isEmpty();
    }

    // ======================== 输出验证（使用真正的 SimpleLogger） ========================
    @Test
    void testOutputFormatting() {
        System.setProperty("gxl.level", "DEBUG");
        Logger logger = Logger.simple("MyApp");

        logger.debug("debug msg");
        logger.info("info msg");
        logger.warn("warn msg");
        logger.error("error msg");

        String out = outContent.toString();
        assertThat(out).contains("[DEBUG] [MyApp] debug msg");
        assertThat(out).contains("[INFO ] [MyApp] info msg");
        assertThat(out).contains("[WARN ] [MyApp] warn msg");
        assertThat(out).contains("[ERROR] [MyApp] error msg");

        Exception ex = new RuntimeException("test exception");
        logger.debug("debug with exception", ex);
        String err = errContent.toString();
        assertThat(err).contains("java.lang.RuntimeException: test exception");
        assertThat(err).contains("at test.LoggerFunctionalTest");
    }

    // ======================== LoggerFactory 功能测试 ========================
    @Test
    void testLoggerFactorySetAndGet() {
        Logger logger = LoggerFactory.getLogger("test");
        assertThat(logger).isInstanceOf(LoggerProxy.class);
        logger.debug("dummy");
        Logger delegate = unwrap(logger);
        assertThat(delegate.getClass().getSimpleName()).isEqualTo("NoopLogger");
        assertThat(outContent.toString()).isEmpty();

        Function<String, Logger> creator = Logger::simple;
        LoggerFactory.setLoggerCreator(creator, true);
        assertThat(LoggerFactory.isSet()).isTrue();
        assertThat(LoggerFactory.getCreatorName()).contains("SimpleLogger");

        Logger newLogger = LoggerFactory.getLogger("test-new");
        assertThat(newLogger).isInstanceOf(LoggerProxy.class);
        newLogger.info("hello");
        assertThat(outContent.toString()).contains("[INFO ] [test-new] hello");

        LoggerFactory.resetToNoop();
        assertThat(LoggerFactory.isSet()).isTrue();
        Logger noopLogger = LoggerFactory.getLogger("test-noop");
        assertThat(noopLogger).isInstanceOf(LoggerProxy.class);

        noopLogger.debug("dummy");
        delegate = unwrap(noopLogger);
        assertThat(delegate.getClass().getSimpleName()).isEqualTo("NoopLogger");
        noopLogger.info("should not print");
        assertThat(outContent.toString()).doesNotContain("should not print");
    }

    @Test
    void testLoggerFactoryCache() {
        Function<String, Logger> creator = Logger::simple;
        LoggerFactory.setLoggerCreator(creator, true);

        Logger logger1 = LoggerFactory.getLogger("foo");
        Logger logger2 = LoggerFactory.getLogger("foo");
        assertThat(logger1).isSameAs(logger2);

        Logger logger3 = LoggerFactory.getLogger("bar");
        assertThat(logger1).isNotSameAs(logger3);
    }

    @Test
    void testLoggerFactorySetForce() {
        Function<String, Logger> creator1 = Logger::simple;
        LoggerFactory.setLoggerCreator(creator1, true);
        Logger proxy = LoggerFactory.getLogger("test");
        proxy.info("first creator");
        assertThat(outContent.toString()).contains("first creator");

        Function<String, Logger> creator2 = name -> Logger.noop();
        LoggerFactory.setLoggerCreator(creator2, false);
        proxy.info("second attempt");
        assertThat(outContent.toString()).contains("second attempt");

        LoggerFactory.setLoggerCreator(creator2, true);
        proxy.info("third attempt");
        assertThat(outContent.toString()).doesNotContain("third attempt");
    }

    @Test
    void testLoggerFactoryStatus() {
        Function<String, Logger> creator = Logger::simple;
        LoggerFactory.setLoggerCreator(creator, true);
        LoggerFactory.getLogger("a");
        LoggerFactory.getLogger("b");

        String statusJson = LoggerFactory.getStatus();
        assertThat(statusJson).contains("LoggerFactory");
        assertThat(statusJson).contains("\"set\":true");
        assertThat(statusJson).contains("SimpleLogger");
        assertThat(statusJson).contains("\"cachedLoggers\"");
        assertThat(statusJson).contains("\"a\"");
        assertThat(statusJson).contains("\"b\"");
    }

    // ======================== LoggerProxy 懒加载 ========================
    @Test
    void testProxyLazyLoading() {
        final int[] createCount = {0};
        Function<String, Logger> creator = name -> {
            createCount[0]++;
            return Logger.simple(name);
        };
        LoggerFactory.setLoggerCreator(creator, true);
        createCount[0] = 0;

        Logger proxy = LoggerFactory.getLogger("lazy");
        assertThat(createCount[0]).isEqualTo(0);

        proxy.debug("trigger lazy");
        assertThat(createCount[0]).isEqualTo(1);
        proxy.debug("again");
        assertThat(createCount[0]).isEqualTo(1);
    }

    @Test
    void testProxyReload() {
        Function<String, Logger> creator1 = Logger::simple;
        LoggerFactory.setLoggerCreator(creator1, true);
        Logger proxy = LoggerFactory.getLogger("reload-test");
        proxy.info("before reload");
        assertThat(outContent.toString()).contains("before reload");

        Function<String, Logger> creator2 = name -> Logger.noop();
        LoggerFactory.setLoggerCreator(creator2, true);
        proxy.info("after reload");
        assertThat(outContent.toString()).doesNotContain("after reload");
        assertThat(outContent.toString()).contains("before reload");
    }

    // ======================== 异常降级测试（修正版） ========================
    @Test
    void testProxyCreatorThrowsException() {
        final boolean[] shouldThrow = {false};
        Function<String, Logger> creator = name -> {
            if (shouldThrow[0]) {
                throw new RuntimeException("simulated failure");
            }
            return Logger.simple(name);
        };
        LoggerFactory.setLoggerCreator(creator, true);
        shouldThrow[0] = true;

        Logger proxy = LoggerFactory.getLogger("bad");
        proxy.info("should be noop");
        assertThat(outContent.toString()).isEmpty();

        String err = errContent.toString();
        assertThat(err).contains("ERROR: Failed to create logger for 'bad'");
        assertThat(err).contains("simulated failure");
    }

    @Test
    void testProxyCreatorReturnsNull() {
        final boolean[] shouldReturnNull = {false};
        Function<String, Logger> creator = name -> {
            if (shouldReturnNull[0]) {
                return null;
            }
            return Logger.simple(name);
        };
        LoggerFactory.setLoggerCreator(creator, true);
        shouldReturnNull[0] = true;

        Logger proxy = LoggerFactory.getLogger("null");
        proxy.info("should be noop");
        assertThat(outContent.toString()).isEmpty();

        String err = errContent.toString();
        assertThat(err).contains("ERROR: Failed to create logger for 'null'");
        assertThat(err).contains("Unexpected null for 'null'");
    }

    // ======================== 消息格式化 ========================
    @Test
    void testMessageFormatting() {
        System.setProperty("gxl.level", "DEBUG");
        Function<String, Logger> creator = Logger::simple;
        LoggerFactory.setLoggerCreator(creator, true);
        Logger logger = LoggerFactory.getLogger("fmt");

        logger.debug("Hello {} and {}", "Alice", "Bob");
        assertThat(outContent.toString()).contains("Hello Alice and Bob");

        Exception ex = new IllegalArgumentException("bad arg");
        logger.error("Error: {}", ex.getMessage());
        assertThat(outContent.toString()).contains("Error: bad arg");

        FormattingTuple tuple = MessageFormatter.format("Value is {}", 42);
        assertThat(tuple.getMessage()).isEqualTo("Value is 42");
        assertThat(tuple.getThrowable()).isNull();

        tuple = MessageFormatter.format("Error", new RuntimeException("oops"));
        assertThat(tuple.getMessage()).isEqualTo("Error");
        assertThat(tuple.getThrowable()).isInstanceOf(RuntimeException.class);
    }

    // ======================== 带 Throwable 的日志方法 ========================
    @Test
    void testLogWithThrowable() {
        System.setProperty("gxl.level", "DEBUG");
        Function<String, Logger> creator = Logger::simple;
        LoggerFactory.setLoggerCreator(creator, true);
        Logger logger = LoggerFactory.getLogger("ex");
        Exception ex = new NullPointerException("npe");

        logger.debug("debug with ex", ex);
        logger.info("info with ex", ex);
        logger.warn("warn with ex", ex);
        logger.error("error with ex", ex);

        String out = outContent.toString();
        String err = errContent.toString();
        assertThat(out).contains("debug with ex", "info with ex", "warn with ex", "error with ex");

        String exceptionMarker = "java.lang.NullPointerException: npe";
        int count = err.split(exceptionMarker, -1).length - 1;
        assertThat(count).isEqualTo(4);
    }

    // ======================== 接口默认方法 ========================
    @Test
    void testLoggerDefaultMethods() {
        System.setProperty("gxl.level", "DEBUG");
        Function<String, Logger> creator = Logger::simple;
        LoggerFactory.setLoggerCreator(creator, true);
        Logger logger = LoggerFactory.getLogger("defaults");

        logger.debug("Debug with {} and {}", "arg1", "arg2");
        logger.info("Info with {}", 123);
        logger.warn("Warn with exception", new IllegalStateException("state"));
        logger.error("Error with multiple {} {} {}", "a", "b", "c");

        String out = outContent.toString();
        assertThat(out).contains("Debug with arg1 and arg2");
        assertThat(out).contains("Info with 123");
        assertThat(out).contains("Warn with exception");
        assertThat(out).contains("Error with multiple a b c");

        String err = errContent.toString();
        assertThat(err).contains("java.lang.IllegalStateException: state");
    }
}