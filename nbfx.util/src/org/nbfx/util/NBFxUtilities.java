/* 
 * The MIT License
 *
 * Copyright 2015 NBFx.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.nbfx.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

public final class NBFxUtilities {

    private static final Logger LOG = Logger.getLogger(NBFxUtilities.class.getName());
    public static final NBFxThreading SWING = new SwingThreading();
    public static final NBFxThreading FX = new FxThreading();

    static {
        LOG.setLevel(Level.CONFIG);
    }

    private NBFxUtilities() {
    }

    public static abstract class AbstractThreading implements NBFxThreading {

        protected abstract String getThreadingName();

        @Override
        public final void ensureThread() {
            if (!isCurrentThread()) {
                throw new IllegalStateException("Must be called from " + getThreadingName());
            }
        }

        @Override
        public final <T> Future<T> getAsynch(final Callable<T> callable) {
            if (null == callable) {
                return null;
            }

            final RunnableFuture<T> rf = new FutureTask<>(callable);

            runLater(rf);

            return rf;
        }

        @Override
        public final void runLater(final Runnable runnable) {
            if (null != runnable) {
                runLaterImpl(new RunnableExecutor(this, runnable));
            }
        }

        @Override
        public final <T> T get(final Callable<T> callable) {
            T result = null;

            try {
                result = getAsynch(callable).get();
            } catch (final InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

            return result;
        }

        protected abstract void runLaterImpl(final Runnable runnable);
    }

    private static class RunnableExecutor implements Runnable {

        private final AbstractThreading threading;
        private final Runnable runnable;
        private final Throwable throwable = null;

        public RunnableExecutor(final AbstractThreading threading, final Runnable runnable) {
            this.threading = threading;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            final long startTime = System.currentTimeMillis();

            runnable.run();

            final long endTime = System.currentTimeMillis();
            final long diff = endTime - startTime;

            if (diff > 100) {
                LOG.log(Level.CONFIG,
                        threading.getThreadingName() + "-Thread took " + diff + " ms to run " + runnable,
                        (diff < 1000) ? null : throwable);
            }
        }
    }

    private static final class SwingThreading extends AbstractThreading {

        private static final String NAME = "EventDispatchThread";

        @Override
        protected String getThreadingName() {
            return NAME;
        }

        @Override
        public boolean isCurrentThread() {
            return SwingUtilities.isEventDispatchThread();
        }

        @Override
        protected void runLaterImpl(final Runnable runnable) {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private static final class FxThreading extends AbstractThreading {

        private static final String NAME = "FxApplicationThread";
        private final AtomicBoolean isInitiated = new AtomicBoolean(false);

        public FxThreading() {
            runLater(() -> {
            });
        }

        @Override
        protected String getThreadingName() {
            return NAME;
        }

        @Override
        public boolean isCurrentThread() {
            return Platform.isFxApplicationThread();
        }

        @Override
        protected void runLaterImpl(final Runnable runnable) {
            synchronized (this) {
                if (!isInitiated.get()) {
                    LOG.log(Level.FINE, "Initiated Platform Thread with {0}", new JFXPanel());
                    Platform.setImplicitExit(false);
                    isInitiated.set(true);
                }
            }

            Platform.runLater(runnable);
        }
    }
}
