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
package io.github.gapplex.gxl.loggers.sysIntcpt;

import io.github.gapplex.gxl.core.Constants;
import io.github.gapplex.gxl.core.Logger;
import io.github.gapplex.gxl.core.LoggerFactory;
import java.io.PrintStream;

public class OutputInterceptor {
    private static final ThreadLocal<Boolean> LOGGING_ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final Logger logger = LoggerFactory.getLogger(OutputInterceptor.class);
    private static final Logger STDOUT = LoggerFactory.getLogger("STDOUT");
    private static final Logger STDERR = LoggerFactory.getLogger("STDERR");

    private static volatile PrintStream originalOut;
    private static volatile PrintStream originalErr;
    private static volatile boolean installed = false;

    public static synchronized void install() {
        if (installed) {
            logger.warn("OutputInterceptor already installed, skipping...");
            return;
        }

        originalOut = System.out;
        originalErr = System.err;

        PrintStream proxyOut = new PrintStream(Constants.STDOUT()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                if (LOGGING_ACTIVE.get()) {
                    Constants.STDOUT().write(buf, off, len);
                    return;
                }

                String message = new String(buf, off, len);
                if (message.trim().isEmpty()) {
                    return;
                }

                try {
                    LOGGING_ACTIVE.set(true);
                    STDOUT.info(message.trim());
                } finally {
                    LOGGING_ACTIVE.set(false);
                }
            }

            @Override
            public void write(int b) {
                if (LOGGING_ACTIVE.get()) {
                    Constants.STDOUT().write(b);
                    return;
                }
                char c = (char) b;
                if (c >= 32 && c < 127) {
                    try {
                        LOGGING_ACTIVE.set(true);
                        STDOUT.info(String.valueOf(c));
                    } finally {
                        LOGGING_ACTIVE.set(false);
                    }
                } else {
                    Constants.STDOUT().write(b);
                }
            }
        };

        PrintStream proxyErr = new PrintStream(Constants.STDERR()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                if (LOGGING_ACTIVE.get()) {
                    Constants.STDERR().write(buf, off, len);
                    return;
                }

                String message = new String(buf, off, len);
                if (message.trim().isEmpty()) {
                    return;
                }

                try {
                    LOGGING_ACTIVE.set(true);
                    STDERR.error(message.trim());
                } finally {
                    LOGGING_ACTIVE.set(false);
                }
            }

            @Override
            public void write(int b) {
                if (LOGGING_ACTIVE.get()) {
                    Constants.STDERR().write(b);
                    return;
                }
                char c = (char) b;
                if (c >= 32 && c < 127) {
                    try {
                        LOGGING_ACTIVE.set(true);
                        STDERR.error(String.valueOf(c));
                    } finally {
                        LOGGING_ACTIVE.set(false);
                    }
                } else {
                    Constants.STDERR().write(b);
                }
            }
        };

        System.setOut(proxyOut);
        System.setErr(proxyErr);

        installed = true;
    }

    public static synchronized void uninstall() {
        if (!installed) {
            logger.warn("OutputInterceptor not installed, nothing to uninstall.");
            return;
        }

        if (originalOut != null) {
            System.setOut(originalOut);
        }
        if (originalErr != null) {
            System.setErr(originalErr);
        }

        installed = false;
        originalOut = null;
        originalErr = null;

        LOGGING_ACTIVE.remove();
    }

    public static boolean isInstalled() {
        return installed;
    }
}