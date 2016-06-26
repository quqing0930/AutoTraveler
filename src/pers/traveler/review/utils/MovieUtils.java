/*
 * MovieUtils.java
 *
 * Created on October 8, 2006, 3:45 PM
 *
 * <p>Title: Jim2mov</p>
 *
 * <p>Description: Create movies from image files</p>
 *
 * <p>Copyright: (C) Copyright 2005-2006, by Andre' Neto</p>
 *
 * Project Info:  	http://jim2mov.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 * </p>
 *
 * @author Andre' Neto
 * @version 1.0.0
 *
 */

package pers.traveler.review.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.media.MediaLocator;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Utility class to help in the creation of some media classes.
 *
 * @author andre
 */
public abstract class MovieUtils
{
    /**
     * Create a media locator from the given location.
     * @param url The location of the place where the media will be writen.
     * @return A media locator.
     */
    public static MediaLocator createMediaLocator(String url)
    {

        MediaLocator ml;

        if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
            return ml;

        if (url.startsWith(File.separator))
        {
            if ((ml = new MediaLocator("file:" + url)) != null)
                return ml;
        }
        else
        {
            String file = "file:" + System.getProperty("user.dir") + File.separator + url;
            if ((ml = new MediaLocator(file)) != null)
                return ml;
        }

        return null;
    }

    /**
     * Loads a file using the standard Java IO classes
     * @param imageLoc The location of the file
     * @throws java.io.IOException If some error occurs while reading the file
     * @return A byte array with file information
     */
    public static byte[] loadImageFile(File imageLoc) throws IOException
    {
        FileInputStream fis = new FileInputStream(imageLoc);
        byte[] read = new byte[fis.available()];
        fis.read(read);
        fis.close();
        return read;
    }

    /**
     * converts an image to JPEG
     * @param img The location of the image
     * @param quality The quality of the jpeg output
     * @throws java.io.IOException If some error occurs while reading the image
     * @return A byte array with the image as a JPEG
     */
//    public static byte[] convertImageToJPEG(File originalImage, float quality) throws IOException
    public static byte[] convertImageToJPEG(BufferedImage img, float quality) throws IOException
    {
//        return bufferedImageToJPEG(ImageIO.read(originalImage), quality);
        return bufferedImageToJPEG(img, quality);
    }

    /**
     * Tries to load an image as a 32 bit RGB image...
     * @param imageLoc The location of the image
     * @throws java.io.IOException If some error occurs
     * @return The 32 bit image
     */
    public static byte[] loadImageAs32bitRGB(File imageLoc) throws IOException
    {
        BufferedImage img = ImageIO.read(imageLoc);
        return int32RGBBufferedImageToByteArray(img);
    }

    /**
     * Converts a buffered image to a JPEG image
     * @param img The image to convert
     * @param quality The quality of the output
     * @throws java.io.IOException If some IO error occurs
     * @return The JPEG image as a byte array
     */
    public static byte[] bufferedImageToJPEG(BufferedImage img, float quality) throws IOException
    {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext())
            throw new IllegalStateException("No writers for jpeg...");

        ImageWriter writer = (ImageWriter) writers.next();
        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(quality);

        IIOImage iioImage = new IIOImage(img, null, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(img.getWidth() * img.getHeight() * 2);
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(baos);
        writer.setOutput(mcios);
        writer.write(null, iioImage, imageWriteParam);
        baos.flush();
        mcios.close();
        return baos.toByteArray();
    }

    /**
     * Converts a standard 32 bit/pixel RGB image into a byte array
     * @param img The image to convert
     * @return A byte array with the image
     */
    public static byte[] int32RGBBufferedImageToByteArray(BufferedImage img)
    {
        int[] pixels = null;
        DataBuffer db = img.getRaster().getDataBuffer();
        if(db instanceof DataBufferInt)
            pixels = ((DataBufferInt)db).getData();
        else
            throw new RuntimeException("Format not supported...");

        byte[] ret = new byte[pixels.length * 4];
        for(int i=0; i<pixels.length; i++)
        {
            ret[4 * i] = (byte)(pixels[i] & 0xff000000);
            ret[4 * i + 1] = (byte)(pixels[i] & 0x00ff0000);
            ret[4 * i + 2] = (byte)(pixels[i] & 0x0000ff00);
            ret[4 * i + 3] = (byte)(pixels[i] & 0x000000ff);
        }

        return ret;
    }
}
