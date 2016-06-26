/*
 * ImagesToMovie.java
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

import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullBufferStream;
import java.io.IOException;

/**
 *
 * @author andre
 */
public class ImagesToMovie implements ControllerListener, DataSinkListener
{
    private PullBufferStream stream = null;
    private ImageDataSource ids = null;
    
    public ImagesToMovie(PullBufferStream stream)
    {
        this.stream = stream;
        this.ids = new ImageDataSource(stream);
    }
    
    public void saveMovie(MediaLocator outML, String descriptor, VideoFormat formatDesired) throws IOException, NoProcessorException, NotRealizedError, NoDataSinkException
    {        
        Processor p = Manager.createProcessor(ids);
        
        p.addControllerListener(this);
        
        p.configure();
        if (!waitForState(p, p.Configured))
        {
            throw new RuntimeException("Failed to configure the processor.");
        }
        
        p.setContentDescriptor(new ContentDescriptor(descriptor));
        
        // Query for the processor for supported formats.
        // Then set it on the processor.
        TrackControl tcs[] = p.getTrackControls();
        
        for(int i = 0; i < tcs.length; i++)
        {
            TrackControl c = tcs[i];
            Format f = c.getFormat();
            if (f instanceof VideoFormat)
            {
                Format[] fmt = c.getSupportedFormats();
                if ((fmt != null) && (fmt.length > 0))
                {
                    for(int j = 0; j < fmt.length; j++)
                    {
                        if(fmt[j] instanceof VideoFormat)
                        {
                            if(videoMatch((VideoFormat)fmt[j], formatDesired))
                            {
                                c.setFormat(fmt[j]);
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        p.realize();
        if (!waitForState(p, p.Realized))
        {
            throw new RuntimeException("Failed to realize the processor.");
        }
        
        // Now, we'll need to create a DataSink.
        DataSink dsink;
        if ((dsink = createDataSink(p, outML)) == null)
        {
            throw new RuntimeException("Failed to create a DataSink for the given output MediaLocator: " + outML);
        }
        
        dsink.addDataSinkListener(this);
        fileDone = false;
        
        // OK, we can now start the actual transcoding.
        try
        {
            p.start();
            dsink.start();
        }
        catch (IOException e)
        {
            throw e;
        }
        
        // Wait for EndOfStream event.
        waitForFileDone();
        
        // Cleanup.
        try
        {
            dsink.close();
        }
        catch (Exception e)
        {}
        p.removeControllerListener(this);
        p.close();
        
    }
    
    /**Checks if two video format match*/
    private boolean videoMatch(VideoFormat vf1, VideoFormat vf2)
    {
        if(vf1 instanceof RGBFormat && vf2 instanceof RGBFormat)
        {
            RGBFormat rgbf1 = (RGBFormat)vf1;
            RGBFormat rgbf2 = (RGBFormat)vf2;
            return rgbf1.getBitsPerPixel() == rgbf2.getBitsPerPixel();
        }
        
        return vf1.getEncoding().equals(vf2.getEncoding());
    }
    
    /**
     * Create the DataSink.
     */
    DataSink createDataSink(Processor p, MediaLocator outML) throws NotRealizedError, NoDataSinkException, IOException
    {
        
        DataSource ds;
        
        if ((ds = p.getDataOutput()) == null)
        {
            throw new RuntimeException("Processor does not have an output DataSource");
        }
        
        DataSink dsink;
        
        dsink = Manager.createDataSink(ds, outML);
        dsink.open();
        
        return dsink;
    }
    
    
    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    
    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state)
    {
        synchronized (waitSync)
        {
            try
            {
                while (p.getState() < state && stateTransitionOK)
                    waitSync.wait();
            }
            catch (Exception e)
            {}
        }
        return stateTransitionOK;
    }
    
    
    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt)
    {
        
        if (evt instanceof ConfigureCompleteEvent ||
                evt instanceof RealizeCompleteEvent ||
                evt instanceof PrefetchCompleteEvent)
        {
            synchronized (waitSync)
            {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        }
        else if (evt instanceof ResourceUnavailableEvent)
        {
            synchronized (waitSync)
            {
                stateTransitionOK = false;
                waitSync.notifyAll();
            }
        }
        else if (evt instanceof EndOfMediaEvent)
        {
            evt.getSourceController().stop();
            evt.getSourceController().close();
        }
    }
    
    
    Object waitFileSync = new Object();
    boolean fileDone = false;
    boolean fileSuccess = true;
    
    /**
     * Block until file writing is done.
     */
    boolean waitForFileDone()
    {
        synchronized (waitFileSync)
        {
            try
            {
                while (!fileDone)
                    waitFileSync.wait();
            }
            catch (Exception e)
            {}
        }
        return fileSuccess;
    }
    
    
    /**
     * Event handler for the file writer.
     */
    public void dataSinkUpdate(DataSinkEvent evt)
    {
        
        if (evt instanceof EndOfStreamEvent)
        {
            synchronized (waitFileSync)
            {
                fileDone = true;
                waitFileSync.notifyAll();
            }
        }
        else if (evt instanceof DataSinkErrorEvent)
        {
            synchronized (waitFileSync)
            {
                fileDone = true;
                fileSuccess = false;
                waitFileSync.notifyAll();
            }
        }
    }            
}
