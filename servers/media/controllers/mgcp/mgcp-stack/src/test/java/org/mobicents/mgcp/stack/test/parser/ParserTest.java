package org.mobicents.mgcp.stack.test.parser;

import jain.protocol.ip.mgcp.message.parms.Bandwidth;
import jain.protocol.ip.mgcp.message.parms.BearerInformation;
import jain.protocol.ip.mgcp.message.parms.CapabilityValue;
import jain.protocol.ip.mgcp.message.parms.CompressionAlgorithm;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.EchoCancellation;
import jain.protocol.ip.mgcp.message.parms.EncryptionMethod;
import jain.protocol.ip.mgcp.message.parms.GainControl;
import jain.protocol.ip.mgcp.message.parms.InfoCode;
import jain.protocol.ip.mgcp.message.parms.LocalOptionValue;
import jain.protocol.ip.mgcp.message.parms.PacketizationPeriod;
import jain.protocol.ip.mgcp.message.parms.ResourceReservation;
import jain.protocol.ip.mgcp.message.parms.SilenceSuppression;
import jain.protocol.ip.mgcp.message.parms.TypeOfNetwork;
import jain.protocol.ip.mgcp.message.parms.TypeOfService;

import java.text.ParseException;

import org.mobicents.mgcp.stack.parser.Utils;
import org.mobicents.mgcp.stack.test.TestHarness;

public class ParserTest extends TestHarness {
	Utils parser = null;

	public ParserTest() {
		super("ParserTest");
	}

	@Override
	public void setUp() {
		parser = new Utils();

	}

	@Override
	public void tearDown() {

	}

	public void testDecodeEncodeCapabilityList() throws ParseException {
		String capability = "a:PCMU;G729,p:10-100,e:on,s:off,v:L;S,m:sendonly;recvonly;sendrecv;inactive;netwloop;netwtest";
		CapabilityValue[] capabilities = parser.decodeCapabilityList(capability);

		assertNotNull(capabilities);
		assertEquals(6, capabilities.length);

		String encodedCapability = parser.encodeCapabilityList(capabilities);

		assertEquals(capability, encodedCapability);
	}

	public void testDecodeEncodeInfoCodeList() throws ParseException {
		String requestedInfo = "R,D,S,X,N,I,T,O,ES";
		InfoCode[] infoCodeList = parser.decodeInfoCodeList(requestedInfo);

		assertNotNull(infoCodeList);
		assertEquals(9, infoCodeList.length);

		String encodedInfoCodeList = parser.encodeInfoCodeList(infoCodeList);
		assertEquals(requestedInfo, encodedInfoCodeList);
	}

	public void testDecodeConnectionMode() {
		String connectionMode = "conttest";

		ConnectionMode cMode = parser.decodeConnectionMode(connectionMode);
		assertNotNull(cMode);
		assertEquals(connectionMode, cMode.toString());
	}

	public void testDecodeEncodeBearerInformation() throws ParseException {
		String text = "e:mu";
		BearerInformation bearerInformation = parser.decodeBearerInformation(text);
		assertNotNull(bearerInformation);
		assertEquals("mu", bearerInformation.toString());

		String encodedText = parser.encodeBearerInformation(bearerInformation);

		assertEquals(text, encodedText);
	}

	public void testDecodeLocalOptionValueList() throws ParseException {
		String text = "p:10,a:PCMU";
		LocalOptionValue[] localOptionValueList = parser.decodeLocalOptionValueList(text);
		assertNotNull(localOptionValueList);
		assertEquals(2, localOptionValueList.length);

		String encodedText = parser.encodeLocalOptionValueList(localOptionValueList);
		assertEquals(text, encodedText);

		text = "a:G729;PCMU,p:30-90,e:on,s:on,customkey:customevalue";
		localOptionValueList = parser.decodeLocalOptionValueList(text);
		assertEquals(5, localOptionValueList.length);

		encodedText = parser.encodeLocalOptionValueList(localOptionValueList);
		assertEquals(text, encodedText);

	}

	public void testDecodeEncodeBandwidth() throws ParseException {
		String text = "64";
		Bandwidth bandwidth = parser.decodeBandwidth(text);
		assertNotNull(bandwidth);
		assertEquals(64, bandwidth.getBandwidthLowerBound());
		assertEquals(64, bandwidth.getBandwidthUpperBound());

		String encodedText = parser.encodeBandwidth(bandwidth);

		assertEquals(text, encodedText);

		text = "64-128";
		bandwidth = parser.decodeBandwidth(text);
		assertNotNull(bandwidth);
		assertEquals(64, bandwidth.getBandwidthLowerBound());
		assertEquals(128, bandwidth.getBandwidthUpperBound());

		encodedText = parser.encodeBandwidth(bandwidth);

		assertEquals(text, encodedText);

	}

	public void testDecodeEncodeCompressionAlgorithm() throws ParseException {
		String text = "PCMU;G726";
		CompressionAlgorithm compressionAlgorithm = parser.decodeCompressionAlgorithm(text);
		assertNotNull(compressionAlgorithm);
		assertEquals(2, compressionAlgorithm.getCompressionAlgorithmNames().length);

		String encodedText = parser.encodeCompressionAlgorithm(compressionAlgorithm);

		assertEquals(text, encodedText);

	}

	public void testDecodeEncodeEchoCancellation() throws ParseException {
		String text = "on";
		EchoCancellation echoCancellation = parser.decodeEchoCancellation(text);
		assertNotNull(echoCancellation);
		assertTrue(echoCancellation.getEchoCancellation());

		String encodedText = parser.encodeEchoCancellation(echoCancellation);

		assertEquals(text, encodedText);
	}

	public void testDecodeEncodeEncryptionMethod() throws ParseException {
		String text = "base64:somekey";
		EncryptionMethod encryptionMethod = parser.decodeEncryptionMethod(text);
		assertNotNull(encryptionMethod);
		assertEquals(2, encryptionMethod.getEncryptionMethod());
		assertEquals("somekey", encryptionMethod.getEncryptionKey());

		String encodedText = parser.encodeEncryptionMethod(encryptionMethod);

		assertEquals(text, encodedText);
	}

	public void testDecodeEncodeGainControl() throws ParseException {
		String text = "auto";
		GainControl gainControl = parser.decodeGainControl(text);
		assertNotNull(gainControl);
		assertTrue(gainControl.getGainControlAuto());

		String encodedText = parser.encodeGainControl(gainControl);

		assertEquals(text, encodedText);

		text = "101";
		gainControl = parser.decodeGainControl(text);
		assertEquals(101, gainControl.getGainControl());
		encodedText = parser.encodeGainControl(gainControl);
		assertEquals(text, encodedText);

	}

	public void testDecodeEncodePacketizationPeriod() throws ParseException {
		String text = "20";
		PacketizationPeriod packetizationPeriod = parser.decodePacketizationPeriod(text);
		assertNotNull(packetizationPeriod);
		assertEquals(20, packetizationPeriod.getPacketizationPeriodLowerBound());
		assertEquals(packetizationPeriod.getPacketizationPeriodLowerBound(), packetizationPeriod
				.getPacketizationPeriodUpperBound());

		String encodedText = parser.encodePacketizationPeriod(packetizationPeriod);
		assertEquals(text, encodedText);

		text = "20-25";
		packetizationPeriod = parser.decodePacketizationPeriod(text);
		assertEquals(20, packetizationPeriod.getPacketizationPeriodLowerBound());
		assertEquals(25, packetizationPeriod.getPacketizationPeriodUpperBound());
		encodedText = parser.encodePacketizationPeriod(packetizationPeriod);
		assertEquals(text, encodedText);

	}

	public void testDecodeEncodeResourceReservation() throws ParseException {
		String text = "be";
		ResourceReservation resourceReservation = parser.decodeResourceReservation(text);
		assertNotNull(resourceReservation);
		assertEquals(ResourceReservation.BEST_EFFORT, resourceReservation.getResourceReservation());

		String encodedText = parser.encodeResourceReservation(resourceReservation);

		assertEquals(text, encodedText);
	}

	public void testDecodeEncodeSilenceSuppression() throws ParseException {
		String text = "off";
		SilenceSuppression silenceSuppression = parser.decodeSilenceSuppression(text);
		assertNotNull(silenceSuppression);
		assertEquals(false, silenceSuppression.getSilenceSuppression());

		String encodedText = parser.encodeSilenceSuppression(silenceSuppression);
		assertEquals(text, encodedText);
	}

	public void testDecodeEncodeTypeOfNetwork() throws ParseException {
		String text = "atm";
		TypeOfNetwork typeOfNetwork = parser.decodeTypeOfNetwork(text);
		assertNotNull(typeOfNetwork);
		assertEquals(TypeOfNetwork.ATM, typeOfNetwork.getTypeOfNetwork());

		String encodedText = parser.encodeTypeOfNetwork(typeOfNetwork);

		assertEquals(text, encodedText);
	}

	public void testDecodeEncodeTypeOfService() throws ParseException {
		String text = "5";
		TypeOfService typeOfService = parser.decodeTypeOfService(text);
		assertNotNull(typeOfService);
		assertEquals(5, typeOfService.getTypeOfService());

		String encodedText = parser.encodeTypeOfService(typeOfService);
		assertEquals(text, encodedText);
	}
	
	

}
