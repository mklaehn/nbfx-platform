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
package org.nbfx.util;

import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.image.Image;
import org.nbfx.util.api.ImageConverter;
import org.openide.util.Lookup;

public class NBFxImageUtilities {

    private static final Collection<ImageConverter> IMAGE_CONVERTERS = new ArrayList<>(Lookup.getDefault().lookupAll(ImageConverter.class));

    private NBFxImageUtilities() {
    }

    public static Image getImage(final Object object) {
        if (object instanceof Image) {
            return (Image) object;
        } else {
            for (final ImageConverter imageConverter : IMAGE_CONVERTERS) {
                final Image image = imageConverter.getImage(object);

                if (null != image) {
                    return image;
                }
            }

            throw new IllegalArgumentException("Illegal parameter. cannot get Image from " + object);
        }
    }
}
