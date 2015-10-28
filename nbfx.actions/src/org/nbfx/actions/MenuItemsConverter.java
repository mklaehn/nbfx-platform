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
package org.nbfx.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.nbfx.util.NBFxUtilities;
import org.openide.util.Lookup;

public interface MenuItemsConverter {

    /**
     * Query method to get the menuitems to be displayed in the ContextMenu of
     * the <code>object</code>. If this provider instance is not responsible for
     * the given {@code object} just return null. A non {@code null} value is
     * considered as a valid response and no more ContextMenuItemsFactory will
     * be queried.
     *
     * @param object
     * @return {@code null} if this ContextMenuItemsFactory is not responsible
     * for the give {@code object}. Otherwise a Collection of at least MenuItem.
     */
    Collection<? extends MenuItem> getMenuItems(final Object object);

    static Collection<? extends MenuItem> convertMenuItems(final Object object) {
        NBFxUtilities.SWING.ensureThread();
        for (final MenuItemsConverter provider : Lookup.getDefault().lookupAll(MenuItemsConverter.class)) {
            try {
                final Collection<? extends MenuItem> menuItems = provider.getMenuItems(object);

                if (null != menuItems) {
                    return menuItems;
                }
            } catch (final Throwable e) {
                Logger.getLogger(MenuItemsConverter.class.getName()).log(Level.WARNING, e.getMessage(), e);
            }
        }

        return Collections.emptyList();
    }

    static Collection<? extends MenuItem> convertMenuItems(final Object[] objects, final Function<Object, Object>... objectSwitchers) {
        NBFxUtilities.SWING.ensureThread();
        if ((null == objects) || (0 == objects.length)) {
            return Collections.emptyList();
        }

        final List<MenuItem> menuItems = new ArrayList<>(objects.length);
        boolean separator = false;
        boolean itemAdded = false;

        for (final Object object : objects) {
            Object o = object;

            for (Function<Object, Object> objectSwitcher : objectSwitchers) {
                o = objectSwitcher.apply(o);
            }

            if (null == o) {
                if (itemAdded) {
                    separator = true;
                }
            } else {
                for (final MenuItem menuItem : MenuElementConverter.convertMenuItem(o)) {
                    if (null == menuItem) {
                        if (itemAdded) {
                            separator = true;
                        }
                    } else {
                        if (separator) {
                            menuItems.add(new SeparatorMenuItem());
                        }
                        separator = false;
                    }

                    menuItems.add(menuItem);
                    itemAdded = true;
                }
            }
        }

        return menuItems;
    }
}
