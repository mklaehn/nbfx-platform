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
package org.nbfx.nodes.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public final class NBFxNodePropertyUtility {

    private static final Logger LOG = Logger.getLogger(NBFxNodePropertyUtility.class.getName());

    private NBFxNodePropertyUtility() {
    }

    public static <T> T getValue(final org.openide.nodes.Node.Property<T> nodeProperty) {
        Objects.requireNonNull(nodeProperty, "nodeProperty must not be null");

        if (nodeProperty.canRead()) {
            try {
                return nodeProperty.getValue();
            } catch (final IllegalAccessException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return null;
    }

    public static <T> boolean setValue(final org.openide.nodes.Node.Property<T> nodeProperty, final T v) {
        Objects.requireNonNull(nodeProperty, "nodeProperty must not be null");

        if (nodeProperty.canWrite()) {
            try {
                nodeProperty.setValue(v);
                return true;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> NBFxNodeProperty<?> createNBFxNodeProperty(final org.openide.nodes.Node.Property<T> nodeProperty) {
        Objects.requireNonNull(nodeProperty, "nodeProperty must not be null");
        final Class<T> valueType = nodeProperty.getValueType();

        if (Boolean.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Boolean> prop = (org.openide.nodes.Node.Property<Boolean>) nodeProperty;
            return new NBFxBooleanNodeProperty(prop);
        } else if (Enum.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Enum<?>> prop = (org.openide.nodes.Node.Property<Enum<?>>) nodeProperty;
            @SuppressWarnings("rawtypes")
            final NBFxEnumNodeProperty nbFxEnumNodeProperty = new NBFxEnumNodeProperty(prop);
            return nbFxEnumNodeProperty;
        } else if (Short.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Short> prop = (org.openide.nodes.Node.Property<Short>) nodeProperty;
            return new NBFxShortNodeProperty(prop);
        } else if (Integer.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Integer> prop = (org.openide.nodes.Node.Property<Integer>) nodeProperty;
            return new NBFxIntegerNodeProperty(prop);
        } else if (Long.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Long> prop = (org.openide.nodes.Node.Property<Long>) nodeProperty;
            return new NBFxLongNodeProperty(prop);
        } else if (Float.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Float> prop = (org.openide.nodes.Node.Property<Float>) nodeProperty;
            return new NBFxFloatNodeProperty(prop);
        } else if (Double.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Double> prop = (org.openide.nodes.Node.Property<Double>) nodeProperty;
            return new NBFxDoubleNodeProperty(prop);
        } else if (String.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<String> prop = (org.openide.nodes.Node.Property<String>) nodeProperty;
            return new NBFxStringNodeProperty(prop);
        } else {
            return new NBFxObjectNodeProperty<>(nodeProperty);
        }
    }

    static <T, X extends WritableValue<T> & ObservableValue<T>> Node getRenderNodeText(final boolean canRead, final boolean canWrite, final Function<String, T> textConverter, final X obersableWritableValue) {
        if (canRead || canWrite) {
            final Object value = obersableWritableValue.getValue();
            final TextField textField = new TextField(null == value ? "" : value.toString());

            textField.setEditable(canWrite);

            if (canWrite) {
                textField.textProperty().addListener((ov, oldValue, newValue) -> {
                    try {
                        obersableWritableValue.setValue(textConverter.apply(newValue));
                    } catch (final Throwable e) {
                    }
                });
            }

            obersableWritableValue.addListener((ov, oldValue, newValue) -> textField.setText(null == newValue ? "" : newValue.toString()));

            return textField;
        } else {
            return null;
        }
    }
}
