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

import java.util.concurrent.atomic.AtomicLong;

public class CountingLogger implements Logger {
    private final String name;
    public static final AtomicLong debugCount = new AtomicLong();
    public static final AtomicLong infoCount = new AtomicLong();
    public static final AtomicLong warnCount = new AtomicLong();
    public static final AtomicLong errorCount = new AtomicLong();

    public CountingLogger(String name) {
        this.name = name;
    }

    @Override public void debug(String msg) { debugCount.incrementAndGet(); }
    @Override public void info(String msg)  { infoCount.incrementAndGet(); }
    @Override public void warn(String msg)  { warnCount.incrementAndGet(); }
    @Override public void error(String msg) { errorCount.incrementAndGet(); }

    @Override public boolean isDebugEnabled() { return true; }
    @Override public boolean isInfoEnabled()  { return true; }
    @Override public boolean isWarnEnabled()  { return true; }
    @Override public boolean isErrorEnabled() { return true; }

    // 其他重载方法（带 Throwable 或参数）简单委托，这里省略
    // 可根据需要实现，或直接使用默认接口方法（但默认方法会调用 debug(String) 等，也会增加计数）

    public String getName() { return name; }
}