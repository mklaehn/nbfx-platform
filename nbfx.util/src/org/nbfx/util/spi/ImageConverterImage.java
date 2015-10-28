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
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.nbfx.util.api.ImageConverter;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ImageConverter.class)
public class ImageConverterImage implements ImageConverter {

    @Override
    public Image getImage(final Object original) {
        if (original instanceof java.awt.Image) {
            return getImage((java.awt.Image) original);
        }

        return null;
    }

    private static Image getImage(final java.awt.Image image) {
        final RenderedImage ri = toRenderedImage(image);

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ImageIO.write(ri, "png", baos);
            baos.flush();

            return new Image(new ByteArrayInputStream(baos.toByteArray()));
        } catch (final IOException ioe) {
            return null;
        }
    }

    private static RenderedImage toRenderedImage(final java.awt.Image image) {
        if (image instanceof RenderedImage) {
            return (RenderedImage) image;
        } else {
            final BufferedImage bufferedImage = ImageOps.createBufferedImage(image.getWidth(null), image.getHeight(null));
            final Graphics g = bufferedImage.createGraphics();

            g.drawImage(image, 0, 0, null);
            g.dispose();
            image.flush();

            return bufferedImage;
        }
    }
}
