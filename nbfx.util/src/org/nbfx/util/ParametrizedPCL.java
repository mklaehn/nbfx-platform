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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ParametrizedPCL implements PropertyChangeListener {

    private Map<String, Collection<PropertyChangeListener>> map = null;
//    private Map<String, Collection<PropertyChangeListener>> map = null;

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (null == map) {
            return;
        }

        map.getOrDefault(pce.getPropertyName(), Collections.emptyList())
                .forEach(pcl -> pcl.propertyChange(pce));
    }

    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener propertyChangeListener) {
        addPropertyChangeListener(propertyName, null, propertyChangeListener);
    }

    public void addPropertyChangeListener(final String propertyName, final NBFxThreading nbft, final PropertyChangeListener propertyChangeListener) {
        if (null == propertyChangeListener) {
            return;
        } else if (null == map) {
            map = new HashMap<>();
        }

        map.computeIfAbsent(propertyName, s -> new ArrayList<>()).add(new ThreadedPCL(nbft, propertyChangeListener));
    }

    private final class ThreadedPCL implements PropertyChangeListener {

        private final NBFxThreading threading;
        private final PropertyChangeListener pcl;

        public ThreadedPCL(final NBFxThreading threading, final PropertyChangeListener pcl) {
            this.threading = threading;
            this.pcl = pcl;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (null == threading) {
                pcl.propertyChange(evt);
            } else {
                threading.runLater(() -> pcl.propertyChange(evt));
            }
        }
    }
}
