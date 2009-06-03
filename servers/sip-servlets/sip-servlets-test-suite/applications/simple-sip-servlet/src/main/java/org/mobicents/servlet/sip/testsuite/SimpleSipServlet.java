/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.testsuite;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.SipSession.State;

import org.apache.log4j.Logger;


public class SimpleSipServlet extends SipServlet implements SipErrorListener, TimerListener {
	
	private static final String TEST_REGISTER_C_SEQ = "testRegisterCSeq";
	private static final String TEST_SUBSCRIBER_URI = "testSubscriberUri";
	private static final String TEST_EXTERNAL_ROUTING = "testExternalRouting";
	private static final String TEST_EXTERNAL_ROUTING_NO_INFO = "testExternalRoutingNoInfo";
	private static final String TEST_NON_EXISTING_HEADER = "TestNonExistingHeader";
	private static final String TEST_ALLOW_HEADER = "TestAllowHeader";
	private static final String TEST_TO_TAG = "TestToTag";
	private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";
	private static final String CANCEL_RECEIVED = "cancelReceived";
	private static final String SUBSCRIBER_URI = "sip:testSubscriberUri@sip-servlets.com";	
	
	@Resource
	SipFactory sipFactory;
	@Resource
	TimerService timerService;
	
	@Override
	protected void doBranchResponse(SipServletResponse resp)
			throws ServletException, IOException {
		resp.getApplicationSession().setAttribute("doBranchResponse", "true");
		super.doBranchResponse(resp);
	}

	private static transient Logger logger = Logger.getLogger(SimpleSipServlet.class);
	private static String TEST_REINVITE_USERNAME = "reinvite";
	private static String TEST_CANCEL_USERNAME = "cancel";
	
	/** Creates a new instance of SimpleProxyServlet */
	public SimpleSipServlet() {
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		logger.info("the simple sip servlet has been started");
		super.init(servletConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doInvite(SipServletRequest request) throws ServletException,
			IOException {
		logger.info("from : " + request.getFrom());
		logger.info("Got request: "
				+ request.getMethod());
		
		request.createResponse(SipServletResponse.SC_TRYING).send();
		
		String fromString = request.getFrom().toString();
		if(fromString.contains(TEST_EXTERNAL_ROUTING_NO_INFO)) {			
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
			timerService.createTimer(request.getApplicationSession(), 1000, false, (Serializable)sipServletResponse);
			return;
		}
		
		if(fromString.contains(TEST_EXTERNAL_ROUTING)) {
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_RINGING);
			sipServletResponse.send();
			sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
			timerService.createTimer(request.getApplicationSession(), 1000, false, (Serializable)sipServletResponse);
			return;
		}
		
		if(fromString.contains(TEST_ALLOW_HEADER)) {
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_METHOD_NOT_ALLOWED);
			sipServletResponse.setHeader("Allow", "INVITE, ACK, CANCEL, OPTIONS, BYE");
			sipServletResponse.addHeader("Allow", "SUBSCRIBE, NOTIFY");
			sipServletResponse.addHeader("Allow", "REFER");
			sipServletResponse.send();
			return;
		}
		
		logger.info("Subscriber URI received : " + request.getSubscriberURI());
		if(fromString.contains(TEST_SUBSCRIBER_URI) && !SUBSCRIBER_URI.equalsIgnoreCase(request.getSubscriberURI().toString())) {			
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_SERVER_INTERNAL_ERROR);
			sipServletResponse.send();
			return;
		}	
		
		if(fromString.contains(TEST_TO_TAG)) {
			//checking if the to tag is not
			Address fromAddress = request.getFrom();
			Address toAddress   = sipFactory.createAddress(request.getRequestURI(), request.getTo().getDisplayName());
	        SipServletRequest newRequest = sipFactory.createRequest(request.getApplicationSession(), "INVITE", fromAddress, toAddress);
	        if(newRequest.getTo().getParameter("tag") != null) {
				logger.error("the ToTag should be empty, sending 500 response");
				SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_SERVER_INTERNAL_ERROR);
				sipServletResponse.send();
				return;
			}
		}
		
		request.getAddressHeader(TEST_NON_EXISTING_HEADER);
		request.getHeader(TEST_NON_EXISTING_HEADER);
		request.getHeaders(TEST_NON_EXISTING_HEADER);
		request.getParameterableHeader("Reply-To");
		request.getParameterableHeaders("Reply-To");
		// Test register cseq issue http://groups.google.com/group/mobicents-public/browse_thread/thread/70f472ca111baccf
		if(fromString.contains(TEST_REGISTER_C_SEQ)) {
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_RINGING);
			sipServletResponse.send();
			sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
			sipServletResponse.send();
			
			SipApplicationSession app = sipFactory.createApplicationSession();
			sipFactory.createRequest(app, "REGISTER", "sip:me@simple-servlet.com", "sip:you@localhost:5058").send();
			return;
		}
		if(!TEST_CANCEL_USERNAME.equalsIgnoreCase(((SipURI)request.getFrom().getURI()).getUser())) {
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_RINGING);
			sipServletResponse.send();
			sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
			sipServletResponse.send();
		} else {
			SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_RINGING);
			sipServletResponse.send();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
			sipServletResponse.send();
		}
	}
	
	@Override
	protected void doAck(SipServletRequest req) throws ServletException,
			IOException {
		if(req.getFrom().getURI() instanceof SipURI) {
			if(TEST_REINVITE_USERNAME.equalsIgnoreCase(((SipURI)req.getFrom().getURI()).getUser())) {
				SipServletRequest reInvite = req.getSession(false).createRequest("INVITE");
				if(req.getSession(false) == reInvite.getSession(false)) {
					reInvite.send();
				} else {
					logger.error("the newly created subsequent request doesn't have " +
							"the same session instance as the one it has been created from");
				}
			}
		}
	}
	
	@Override
	protected void doSuccessResponse(SipServletResponse resp)
			throws ServletException, IOException {
		// This is for the REGISTER CSeq test
		if(resp.getMethod().equalsIgnoreCase("REGISTER")) {
			int cseq = Integer.parseInt(resp.getRequest().getHeader("CSeq").substring(0,1));
			if(cseq < 4) {
				try {
					// Put some delay as per http://groups.google.com/group/mobicents-public/browse_thread/thread/70f472ca111baccf
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Check if the session remains in INITIAL state, if not, the test will fail for missing registers
				if(resp.getSession().getState().equals(State.INITIAL))
					resp.getSession().createRequest("REGISTER").send();
			}
			return;
		}
		if(!"BYE".equalsIgnoreCase(resp.getMethod())) {
			resp.createAck().send();
			resp.getSession(false).createRequest("BYE").send();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doBye(SipServletRequest request) throws ServletException,
			IOException {

		logger.info("Got BYE request: " + request);
		SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_OK);
		
		// Force fail by not sending OK if the doBranchResponse is called. In non-proxy app
		// this would be wrong.
		if(!"true".equals(request.getApplicationSession().getAttribute("doBranchResponse")))
			sipServletResponse.send();
	}	
	
	@Override
	protected void doCancel(SipServletRequest request) throws ServletException,
			IOException {
		
		logger.info("Got CANCEL request: " + request);
		SipFactory sipFactory = (SipFactory)getServletContext().getAttribute(SIP_FACTORY);
		try {
			SipServletRequest sipServletRequest = sipFactory.createRequest(
					sipFactory.createApplicationSession(), 
					"MESSAGE", 
					"sip:sender@sip-servlets.com", 
					"sip:receiver@sip-servlets.com");
			SipURI sipUri=sipFactory.createSipURI("receiver", "127.0.0.1:5080");
			sipServletRequest.setRequestURI(sipUri);
			sipServletRequest.setContentLength(CANCEL_RECEIVED.length());
			sipServletRequest.setContent(CANCEL_RECEIVED, CONTENT_TYPE);
			sipServletRequest.send();
		} catch (ServletParseException e) {
			logger.error("Exception occured while parsing the addresses",e);
		} catch (IOException e) {
			logger.error("Exception occured while sending the request",e);			
		}
	}

	@Override
	protected void doRegister(SipServletRequest req) throws ServletException,
			IOException {
		Address contact = req.getAddressHeader("Contact");
		contact.setExpires(3600);
		logger.info("REGISTER Contact Address.toString = " + contact.toString());
		int response = SipServletResponse.SC_OK;
		if(!"<sip:sender@127.0.0.1:5080;transport=udp;lr>;expires=3600".equals(contact.toString())) {
			response = SipServletResponse.SC_SERVER_INTERNAL_ERROR;
		}
		SipServletResponse resp = req.createResponse(response);
		resp.send();
	}
	
	@Override
	protected void doInfo(SipServletRequest req) throws ServletException,
			IOException {
		String content = (String) req.getContent();
		req.getSession().setAttribute("mutable", content);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int response = SipServletResponse.SC_OK;
		if(!content.equals(req.getSession().getAttribute("mutable")))
			response = SipServletResponse.SC_SERVER_INTERNAL_ERROR;
		
		SipServletResponse resp = req.createResponse(response);
		resp.send();
	}
	
	// SipErrorListener methods

	/**
	 * {@inheritDoc}
	 */
	public void noAckReceived(SipErrorEvent ee) {
		logger.error("noAckReceived.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void noPrackReceived(SipErrorEvent ee) {
		logger.error("noPrackReceived.");
	}

	public void timeout(ServletTimer timer) {
		SipServletResponse sipServletResponse = (SipServletResponse)timer.getInfo();
		try {
			sipServletResponse.send();
		} catch (IOException e) {
			logger.error("Unexpected exception while sending the OK", e);
		}
	}

}