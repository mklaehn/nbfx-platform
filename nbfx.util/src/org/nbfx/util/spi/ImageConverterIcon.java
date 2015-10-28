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
package org.nbfx.util.spi;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.nbfx.util.api.ImageConverter;
import org.nbfx.util.NBFxImageUtilities;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ImageConverter.class)
public class ImageConverterIcon implements ImageConverter {

    private static final JLabel label = new JLabel();

    @Override
    public Image getImage(final Object original) {
        if (original instanceof Icon) {
            return NBFxImageUtilities.getImage(icon2Image((Icon) original));
        }

        return null;
    }

    private static java.awt.Image icon2Image(final Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        } else {
            final BufferedImage bufferedImage = ImageOps.createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
            final Graphics g = bufferedImage.createGraphics();

            icon.paintIcon(label, g, 0, 0);
            g.dispose();
            bufferedImage.flush();

            return bufferedImage;
        }
    }
}
