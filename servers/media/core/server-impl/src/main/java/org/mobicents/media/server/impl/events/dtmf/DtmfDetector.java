/*
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.events.dtmf.DtmfEvent;

/**
 * Implements common fatures for DTMF detector.
 * 
 * @author Oleg Kulikov
 */
public class DtmfDetector extends AbstractSink {
    
    public final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    
    private final static Format RFC2833_FORMATS[] = new Format[]{DTMF};
    private final static Format[] INBAND_FORMATS = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM};
    private final static Format[] ALL_FORMATS = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM, DTMF};
    
    private InbandDetector inband;
    private Rfc2833Detector rfc2833;
    
    protected DtmfBuffer digitBuffer;
    
    private transient Processor dsp;
    private MediaSource source;
    
    private DTMFMode mode = DTMFMode.AUTO;
    
    public DtmfDetector(String name) {
    	super(name);
        digitBuffer = new DtmfBuffer(this);
        inband = new InbandDetector(this);
        rfc2833 = new Rfc2833Detector(this);
        
        dsp = new Processor("");
        dsp.getOutput().connect(inband);
        dsp.configure(new Format[] {PCMA, SPEEX, PCMU, G729}, new Format[]{LINEAR_AUDIO});
    }

    
    public DTMFMode getMode() {
        return mode;
    }
    
    public void setMode(DTMFMode mode) {
        this.mode = mode;
        if (source != null) {
            MediaSource s = source;
            disconnect(source);
            connect(s);
        }
    }
    
    @Override
    public void connect(MediaSource source) {
        super.connect(source);
        this.source = source;
        if (mode == DTMFMode.RFC2833) {
            source.connect(this.rfc2833);
        } else if (mode == DTMFMode.INBAND) {
            source.connect(dsp.getInput());
        } else {
            source.connect(this.rfc2833);
            source.connect(dsp.getInput());
        }
    }

    @Override
    public void disconnect(MediaSource source) {
        dsp.getInput().disconnect(source);
        rfc2833.disconnect(source);
        this.source = null;
        super.disconnect(source);
    }
    
    public void setDtmfMask(String mask) {
        digitBuffer.setMask(mask);
    }

    protected void sendEvent(String seq) {
        DtmfEvent evt = new DtmfEvent(seq);
        super.sendEvent(evt);
    }

    private void stop() {
    }
    
    public Format[] getFormats() {
        switch (mode) {
            case RFC2833 : return RFC2833_FORMATS;
            case INBAND : return INBAND_FORMATS;
            default : return ALL_FORMATS;
        }
    }

    public boolean isAcceptable(Format format) {
        return format.equals(DTMF) || format.equals(LINEAR_AUDIO);
    }


    public void receive(Buffer buffer) {
    }

    public String getID() {
        return null;
    }

    public Object getParameter(String name) {
        return null;
    }

    public void setParameter(String name, Object value) {
    }
}
