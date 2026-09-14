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
package io.github.gapplex.logax.appender;

import io.github.gapplex.logax.event.LogEvent;

import java.util.concurrent.*;

public class AsyncAppender implements Appender {
    private final Appender delegate;
    private final BlockingQueue<LogEvent> queue;
    private final ExecutorService executor;
    private volatile boolean running = true;

    public AsyncAppender(Appender delegate, int queueSize) {
        this.delegate = delegate;
        this.queue = new LinkedBlockingQueue<>(queueSize);
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Logax-Async-Worker");
            t.setDaemon(true);
            return t;
        });
        this.delegate.start();
        this.executor.submit(this::consume);
    }

    private void consume() {
        while (running) {
            try {
                LogEvent event = queue.poll(1, TimeUnit.SECONDS);
                if (event != null) delegate.append(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) { /* 处理异常 */ }
        }
        while (!queue.isEmpty()) {
            delegate.append(queue.poll());
        }
    }

    @Override
    public void append(LogEvent event) {
        if (!running) return;
        if (!queue.offer(event)) {
            System.err.println("Log queue full, dropping log: " + event.getMessage());
        }
    }

    @Override
    public void start() {}
    @Override
    public void stop() {
        running = false;
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("AsyncAppender shutdown timeout, forcing stop");
            }
        } catch (InterruptedException ignored) {}
        delegate.stop();
    }
}
