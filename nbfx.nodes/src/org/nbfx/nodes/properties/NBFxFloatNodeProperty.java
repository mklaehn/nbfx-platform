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

import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.Node;
import org.openide.nodes.Node.Property;

public class NBFxFloatNodeProperty extends SimpleFloatProperty implements NBFxNodeProperty<Number> {

    public NBFxFloatNodeProperty(final Property<Float> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Float getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final Number newValue) {
        if (canWrite() && NBFxNodePropertyUtility.setValue(getNodeProperty(), (null == newValue)
                ? null
                : newValue.floatValue())) {
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

    private Property<Float> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Float> prop = (Property<Float>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        return NBFxNodePropertyUtility.getRenderNodeText(canRead(), canWrite(), Float::valueOf, this);
    }
}
