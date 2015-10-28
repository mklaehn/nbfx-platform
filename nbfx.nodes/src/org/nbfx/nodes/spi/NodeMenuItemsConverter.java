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
package org.nbfx.nodes.spi;

import java.util.Collection;
import javafx.scene.control.MenuItem;
import javax.swing.Action;
import org.nbfx.actions.MenuItemsConverter;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = MenuItemsConverter.class)
public class NodeMenuItemsConverter implements MenuItemsConverter {

    @Override
    public Collection<? extends MenuItem> getMenuItems(final Object object) {
        if (object instanceof Node) {
            final Node node = (Node) object;
            final Action[] actions = node.getActions(true);

            return convertMenuItems(node.getLookup(), actions);
        }

        return null;
    }

    private Collection<? extends MenuItem> convertMenuItems(final Lookup lookup, final Object[] objects) {
        if (null != lookup) {
            return MenuItemsConverter.convertMenuItems(objects, o -> o instanceof ContextAwareAction
                    ? ((ContextAwareAction) o).createContextAwareInstance(lookup)
                    : o);
        } else {
            return MenuItemsConverter.convertMenuItems(objects);
        }
    }
}
