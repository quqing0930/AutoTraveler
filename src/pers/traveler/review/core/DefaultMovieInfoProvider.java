/*
 * DefaultMovieInfoProvider.java
 *
 * Created on October 12, 2006, 12:10 AM
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

import pers.traveler.review.utils.MovieUtils;

import javax.media.MediaLocator;

/**
 * Default implementation of the MovieInfoProvider with getter and setter methods
 * It also helps creating a media locator for files
 * @author andre
 */
public class DefaultMovieInfoProvider implements MovieInfoProvider
{

    /** Creates a new instance of DefaultMovieInfoProvider */
    public DefaultMovieInfoProvider()
    {
    }

    /**
     * Creates a new instance of DefaultMovieInfoProvider
     * @param fileToSave the file where to save the movie
     */
    public DefaultMovieInfoProvider(String fileToSave)
    {
        this(MovieUtils.createMediaLocator(fileToSave));
    }

    /**
     * Creates a new instance of DefaultMovieInfoProvider
     * @param mediaLocator the locator where to save the movie
     */
    public DefaultMovieInfoProvider(MediaLocator mediaLocator)
    {
        this.mediaLocator = mediaLocator;
    }

    /**
     * Holds value of property FPS.
     */
    private float FPS;

    /**
     * Getter for property FPS.
     * @return Value of property FPS.
     */
    public float getFPS()
    {

        return this.FPS;
    }

    /**
     * Setter for property FPS.
     * @param FPS New value of property FPS.
     */
    public void setFPS(float FPS)
    {

        this.FPS = FPS;
    }

    /**
     * Holds value of property numberOfFrames.
     */
    private int numberOfFrames;

    /**
     * Getter for property numberOfFrames.
     * @return Value of property numberOfFrames.
     */
    public int getNumberOfFrames()
    {

        return this.numberOfFrames;
    }

    /**
     * Setter for property numberOfFrames.
     * @param numberOfFrames New value of property numberOfFrames.
     */
    public void setNumberOfFrames(int numberOfFrames)
    {

        this.numberOfFrames = numberOfFrames;
    }

    /**
     * Holds value of property width.
     */
    private int width;

    /**
     * Getter for property width.
     * @return Value of property width.
     */
    public int getMWidth()
    {

        return this.width;
    }

    /**
     * Setter for property width.
     * @param width New value of property width.
     */
    public void setMWidth(int width)
    {

        this.width = width;
    }

    /**
     * Holds value of property height.
     */
    private int height;

    /**
     * Getter for property height.
     * @return Value of property height.
     */
    public int getMHeight()
    {

        return this.height;
    }

    /**
     * Setter for property height.
     * @param height New value of property height.
     */
    public void setMHeight(int height)
    {

        this.height = height;
    }

    /**
     * Holds value of property mediaLocator.
     */
    private MediaLocator mediaLocator;

    /**
     * Getter for property mediaLocator.
     * @return Value of property mediaLocator.
     */
    public MediaLocator getMediaLocator()
    {

        return this.mediaLocator;
    }

    /**
     * Setter for property mediaLocator.
     * @param mediaLocator New value of property mediaLocator.
     */
    public void setMediaLocator(MediaLocator mediaLocator)
    {

        this.mediaLocator = mediaLocator;
    }

    /**
     * Setter for property saveFile.
     * @param fileLocation the location of the file.
     */
    public void setMediaLocator(String fileLocation)
    {
        setMediaLocator(MovieUtils.createMediaLocator(fileLocation));
    }

}