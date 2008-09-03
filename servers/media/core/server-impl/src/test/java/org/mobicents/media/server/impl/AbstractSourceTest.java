/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class AbstractSourceTest {

    public AbstractSourceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of connect method, of class AbstractSource.
     */
    @Test
    public void testConnect() throws Exception {
        Format[] formats = new Format[] {
            new AudioFormat("F1")
        };
        
        TestSource stream = new TestSource(formats);
        TestSink sink = new TestSink(formats);
        
        try {
            stream.connect(sink);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        assertEquals(sink.mediaStream, stream);
        assertEquals(stream.sink, sink);
    }

    /**
     * Test of disconnect method, of class AbstractSource.
     */
    @Test
    public void testDisconnect() {
        Format[] formats = new Format[] {
            new AudioFormat("F1")
        };
        
        TestSource stream = new TestSource(formats);
        TestSink sink = new TestSink(formats);
        
        try {
            stream.connect(sink);
            stream.disconnect(sink);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        assertEquals(sink.mediaStream, null);
        assertEquals(stream.sink, null);
    }

    @Test
    public void testFormatNegotiation() throws Exception {
        Format[] formats1 = new Format[] {
            new AudioFormat("F1")
        };

        Format[] formats2 = new Format[] {
            new AudioFormat("F1"),
            new AudioFormat("F2"),
        };
        
        TestSource stream = new TestSource(formats1);
        TestSink sink = new TestSink(formats2);
        
        try {
            stream.connect(sink);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        assertEquals(sink.mediaStream, stream);
        assertEquals(stream.sink, sink);
    }

    @Test
    public void testCommonFormats() throws Exception {
        Format[] formats1 = new Format[] {
            new AudioFormat("F1")
        };

        Format[] formats2 = new Format[] {
            new AudioFormat("F1"),
            new AudioFormat("F2"),
        };
        
        TestSource stream = new TestSource(formats1);
        TestSink sink = new TestSink(formats2);
        
        try {
            stream.connect(sink);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        assertEquals(sink.mediaStream, stream);
        assertEquals(stream.sink, sink);
        
        assertEquals(1, stream.commonFormats.size());
        if (!((Format)stream.commonFormats.get(0)).matches(new AudioFormat("F1"))) {
            fail("Expected " + new AudioFormat("F1"));
        }
    }
    
    @Test
    public void testFormatNegotiationWithWildcardSource() throws Exception {
        Format[] formats1 = new Format[] {
            new AudioFormat("F1")
        };

        Format[] formats2 = new Format[] {
            new AudioFormat("F1"),
            new AudioFormat("F2"),
        };
        
        TestSource stream = new TestSource(null);
        TestSink sink = new TestSink(formats2);
        
        try {
            stream.connect(sink);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        assertEquals(sink.mediaStream, stream);
        assertEquals(stream.sink, sink);
    }

    @Test
    public void testFormatNegotiationWithWildcardSink() throws Exception {
        Format[] formats1 = new Format[] {
            new AudioFormat("F1")
        };

        Format[] formats2 = new Format[] {
            new AudioFormat("F1"),
            new AudioFormat("F2"),
        };
        
        TestSource stream = new TestSource(formats1);
        TestSink sink = new TestSink(null);
        
        try {
            stream.connect(sink);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        assertEquals(sink.mediaStream, stream);
        assertEquals(stream.sink, sink);
    }

    @Test
    public void testFormatNotNegotiated() throws Exception {
        Format[] formats1 = new Format[] {
            new AudioFormat("F1")
        };

        Format[] formats2 = new Format[] {
            new AudioFormat("F2"),
        };
        
        TestSource stream = new TestSource(formats1);
        TestSink sink = new TestSink(formats2);
        
        try {
            stream.connect(sink);
            fail("IOException expected");
        } catch (IOException e) {
        }
    }    
    
    private class TestSink extends AbstractSink {

        private Format[] formats;
                
        public TestSink(Format[] formats) {
            this.formats = formats;
        }
        
        public Format[] getFormats() {
            return formats;
        }

        public boolean isAcceptable(Format format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void receive(Buffer buffer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    private class TestSource extends AbstractSource {

        private Format[] formats;
        
        public TestSource(Format[] formats) {
            this.formats = formats;
        }
        
        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Format[] getFormats() {
            return formats;
        }
    }
}