/*
 * MovieInfoProvider.java
 *
 * Created on October 8, 2006, 3:55 PM
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
package pers.traveler.review.core;

import javax.media.MediaLocator;

/**
 * This interface is used to provide informations about the movie
 * @author andre
 */
public interface MovieInfoProvider
{
    /**
     * Quicktime / JPEG movie
     */
    public int TYPE_QUICKTIME_JPEG = 0;
    /**
     * MSVideo / MJPEG movie
     */
    public int TYPE_AVI_MJPEG = 1;
    /**
     * MSVideo / Raw movie
     */
    public int TYPE_AVI_RAW = 2;
    
    /**
     * The number of frames per second with which the movie will be saved
     * @return The number of frames per second
     */
    public float getFPS();
    /**
     * The total number of frames in the movie
     * @return The number of frames
     */
    public int getNumberOfFrames();
    /**
     * Returns the width of the movie frames
     * @return width of the movie frames
     */
    public int getMWidth();
    /**
     * Returns the height of the movie frames
     * @return height of the movie frames
     */
    public int getMHeight();
    /**
     * Returns the location of the media where to write the movie
     * @return the location of the media where to write the movie
     */
    public MediaLocator getMediaLocator();
}
