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
package org.nbfx.actions.spi;

import java.util.List;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javax.swing.JCheckBoxMenuItem;
import org.nbfx.actions.MenuElementConverter;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = MenuElementConverter.class, position = Integer.MAX_VALUE - 1)
public final class JCheckBoxMenuItemMenuElementConverter implements MenuElementConverter {

    @Override
    public List<MenuItem> performConversion(final Object menuElement) {
        if (menuElement instanceof JCheckBoxMenuItem) {
            final JCheckBoxMenuItem jmi = (JCheckBoxMenuItem) menuElement;
            final CheckMenuItem menuItem = new CheckMenuItem();

            menuItem.setSelected(jmi.isSelected());

            return JMenuItemUtil.basicConversion(menuItem, jmi);
        } else {
            return null;
        }
    }
}
