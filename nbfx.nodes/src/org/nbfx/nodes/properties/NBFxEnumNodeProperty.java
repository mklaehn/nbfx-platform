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

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import org.openide.nodes.Node.Property;

public class NBFxEnumNodeProperty<E extends Enum<?>> extends SimpleObjectProperty<E> implements NBFxNodeProperty<E> {

    public NBFxEnumNodeProperty(final Property<E> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public E getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final E newValue) {
        if (canWrite() && NBFxNodePropertyUtility.setValue(getNodeProperty(), newValue)) {
            fireValueChangedEvent();
        }
    }

    @Override
    public final boolean canRead() {
        return getNodeProperty().canRead();
    }

    @Override
    public final boolean canWrite() {
        return getNodeProperty().canWrite();
    }

    private Property<E> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<E> prop = (Property<E>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final ObservableList<E> enums;

            if (null != getValue()) {
                @SuppressWarnings("unchecked")
                final Class<E> enumClass = (Class<E>) getValue().getClass();
                enums = FXCollections.observableArrayList(enumClass.getEnumConstants());
            } else if (getNodeProperty().getValueType().isEnum()) {
                enums = FXCollections.observableArrayList(getNodeProperty().getValueType().getEnumConstants());
            } else {
                return null;
            }

            final ChoiceBox<E> choiceBox = new ChoiceBox<>(enums);

            choiceBox.setDisable(!canWrite());

            if (canWrite()) {
                choiceBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> setValue(newValue));
            }

            addListener((ov, oldValue, newValue) -> choiceBox.getSelectionModel().select(newValue));

            return choiceBox;
        } else {
            return null;
        }
    }
}
