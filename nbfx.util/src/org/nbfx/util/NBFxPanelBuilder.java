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

import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

public final class NBFxPanelBuilder {

    private final Map<Key, Object> values = new EnumMap<>(Key.class);

    private NBFxPanelBuilder() {
    }

    public static NBFxPanelBuilder create() {
        return new NBFxPanelBuilder();
    }

    public NBFxPanelBuilder fill(final Paint fill) {
        Objects.requireNonNull(fill);
        return setValue(Key.FILL, fill);
    }

    public NBFxPanelBuilder root(final Parent root) {
        Objects.requireNonNull(root);
        return setValue(Key.ROOT, root);
    }

    public NBFxPanelBuilder additionalStyle(final String additionalStyle) {
        Objects.requireNonNull(additionalStyle);
        return setValue(Key.ADDITIONAL_STYLES, additionalStyle);
    }

    private NBFxPanelBuilder setValue(final Key key, final Object value) {
        if (null != value) {
            if (key.isList()) {
                final Object object = values.get(key);
                final List<Object> list;

                if (object instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<Object> l = (List<Object>) object;
                    list = l;
                } else {
                    list = new ArrayList<>();
                    values.put(key, list);
                }

                list.add(value);
            } else {
                values.put(key, value);
            }
        }

        return this;
    }

    public JFXPanel build() {
        NBFxUtilities.SWING.ensureThread();

        final JFXPanel jfxp = new JFXPanel();

        jfxp.setScene(NBFxUtilities.FX.get(() -> {
            final Parent parent = (Parent) Objects.requireNonNull(values.get(Key.ROOT), "A Scene requires a Parent!");
            final Scene scene = new Scene(parent);

            if (values.containsKey(Key.FILL)) {
                final Paint paint = (Paint) values.get(Key.FILL);

                scene.setFill(paint);
            }

            if (values.get(Key.ADDITIONAL_STYLES) instanceof List) {
                @SuppressWarnings("unchecked")
                final List<String> list = (List<String>) values.get(Key.ADDITIONAL_STYLES);

                scene.getStylesheets().addAll(list);
            }

            return scene;
        }
        ));

        return jfxp;
    }

    private static enum Key {

        FILL,
        ROOT,
        ADDITIONAL_STYLES {

            @Override
            public boolean isList() {
                return true;
            }
        };

        public boolean isList() {
            return false;
        }
    }
}
