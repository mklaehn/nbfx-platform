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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.openide.nodes.Node.Property;

public class NBFxBooleanNodeProperty extends SimpleBooleanProperty implements NBFxNodeProperty<Boolean> {

    public NBFxBooleanNodeProperty(final Property<Boolean> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Boolean getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final Boolean newValue) {
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

    private Property<Boolean> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Boolean> prop = (Property<Boolean>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final CheckBox checkBox = new CheckBox();

            checkBox.setDisable(!canWrite());
            checkBox.setSelected((null == getValue()) ? false : getValue());

            if (canWrite()) {
                checkBox.selectedProperty().addListener((ov, oldValue, newValue) -> setValue(newValue));
            }

            addListener((ov, oldValue, newValue) -> checkBox.setSelected(Boolean.TRUE.equals(newValue)));

            return checkBox;
        } else {
            return null;
        }
    }
}
