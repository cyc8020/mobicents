package org.mobicents.servlet.sip.testsuite.simple;
import javax.sip.SipProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.servlet.sip.SipServletTestCase;
import org.mobicents.servlet.sip.testsuite.ProtocolObjects;
import org.mobicents.servlet.sip.testsuite.TestSipListener;

public class ShootistSipServletTest extends SipServletTestCase {
	private static Log logger = LogFactory.getLog(ShootistSipServletTest.class);		
	private static final String TRANSPORT = "udp";
	private static final boolean AUTODIALOG = true;
	private static final int TIMEOUT = 5000;	
//	private static final int TIMEOUT = 100000000;
	
	TestSipListener receiver;
	
	ProtocolObjects receiverProtocolObjects;
	
	@Override
	public void deployApplication() {
		assertTrue(tomcat.deployContext(
				projectHome + "/sip-servlets-test-suite/applications/shootist-sip-servlet/src/main/sipapp",
				"sip-test-context", "sip-test"));
	}

	@Override
	protected String getDarConfigurationFile() {
		return "file:///" + projectHome + "/sip-servlets-test-suite/testsuite/src/test/resources/" +
				"org/mobicents/servlet/sip/testsuite/simple/simple-sip-servlet-dar.properties";
	}
	
	@Override
	protected void setUp() {
		try {
			super.setUp();						
			
			receiverProtocolObjects =new ProtocolObjects(
					"sender", "gov.nist", TRANSPORT, AUTODIALOG);
						
			receiver = new TestSipListener(5080, 5070, receiverProtocolObjects);
			SipProvider senderProvider = receiver.createProvider();			
			
			senderProvider.addSipListener(receiver);
			
			receiverProtocolObjects.start();			
		} catch (Exception ex) {
			fail("unexpected exception ");
		}
	}
	
	public void testShootist() throws InterruptedException {
//		receiver.sendInvite();
		Thread.sleep(TIMEOUT);
		assertTrue(receiver.getByeReceived());		
	}

	@Override
	protected void tearDown() {					
		receiverProtocolObjects.destroy();			
		logger.info("Test completed");		
	}
}