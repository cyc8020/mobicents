package org.csapi.cm;

/**
 *	Generated from IDL definition of exception "P_UNKNOWN_ENTERPRISE_NETWORK"
 *	@author JacORB IDL compiler 
 */

public final class P_UNKNOWN_ENTERPRISE_NETWORK
	extends org.omg.CORBA.UserException
{
	public P_UNKNOWN_ENTERPRISE_NETWORK()
	{
		super(org.csapi.cm.P_UNKNOWN_ENTERPRISE_NETWORKHelper.id());
	}

	public java.lang.String ExtraInformation;
	public P_UNKNOWN_ENTERPRISE_NETWORK(java.lang.String _reason,java.lang.String ExtraInformation)
	{
		super(org.csapi.cm.P_UNKNOWN_ENTERPRISE_NETWORKHelper.id()+""+_reason);
		this.ExtraInformation = ExtraInformation;
	}
	public P_UNKNOWN_ENTERPRISE_NETWORK(java.lang.String ExtraInformation)
	{
		this.ExtraInformation = ExtraInformation;
	}
}
