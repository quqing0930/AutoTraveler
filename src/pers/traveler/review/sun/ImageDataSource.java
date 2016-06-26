/*
 * ImageDataSource.java
 *
 * Modified on October 8, 2006, 3:44 PM by Andre' Neto in order to integrate in the Jim2Mov (http://jim2mov.sourceforge.net) project
 *
 * @(#)JpegImagesToMovie.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package pers.traveler.review.sun;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/**
 *
 * @author andre
 */
public class ImageDataSource extends PullBufferDataSource
{
    
    private PullBufferStream streams[];
    
    ImageDataSource(PullBufferStream stream)
    {
        streams = new PullBufferStream[1];
        streams[0] = stream;
    }
    
    public void setLocator(MediaLocator source)
    {
    }
    
    public MediaLocator getLocator()
    {
        return null;
    }
    
    /**
     * Content type is of RAW since we are sending buffers of video
     * frames without a container format.
     */
    public String getContentType()
    {
        return ContentDescriptor.RAW;
    }
    
    public void connect()
    {
    }
    
    public void disconnect()
    {
    }
    
    public void start()
    {
    }
    
    public void stop()
    {
    }
    
    /**
     * Return the ImageSourceStreams.
     */
    public PullBufferStream[] getStreams()
    {
        return streams;
    }
    
    /**
     * We could have derived the duration from the number of
     * frames and frame rate.  But for the purpose of this program,
     * it's not necessary.
     */
    public Time getDuration()
    {
        return DURATION_UNKNOWN;        
    }
    
    public Object[] getControls()
    {
        return new Object[0];
    }
    
    public Object getControl(String type)
    {
        return null;
    }
    
}
