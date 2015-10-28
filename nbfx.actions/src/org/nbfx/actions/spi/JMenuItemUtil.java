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

import java.util.Collections;
import java.util.List;
import javafx.scene.control.MenuItem;
import javax.swing.JMenuItem;
import org.nbfx.util.NBFxUtilities;

class JMenuItemUtil {

    private JMenuItemUtil() {
    }

    public static List<MenuItem> basicConversion(final JMenuItem jmi) {
        return basicConversion(new MenuItem(), jmi);
    }

    public static List<MenuItem> basicConversion(final MenuItem menuItem, final JMenuItem jmi) {
        menuItem.setText(jmi.getText());
        menuItem.setId(jmi.getActionCommand());
        menuItem.setOnAction(ae -> NBFxUtilities.SWING.runLater(() -> jmi.doClick()));
        return Collections.singletonList(menuItem);
    }
}
