package org.csapi.fw.fw_service.integrity;

/**
 *	Generated from IDL interface "IpSvcHeartBeatMgmt"
 *	@author JacORB IDL compiler V 2.1, 16-Feb-2004
 */


public abstract class IpSvcHeartBeatMgmtPOA
	extends org.omg.PortableServer.Servant
	implements org.omg.CORBA.portable.InvokeHandler, org.csapi.fw.fw_service.integrity.IpSvcHeartBeatMgmtOperations
{
	static private final java.util.Hashtable m_opsHash = new java.util.Hashtable();
	static
	{
		m_opsHash.put ( "disableSvcHeartBeat", new java.lang.Integer(0));
		m_opsHash.put ( "enableSvcHeartBeat", new java.lang.Integer(1));
		m_opsHash.put ( "changeInterval", new java.lang.Integer(2));
	}
	private String[] ids = {"IDL:org/csapi/fw/fw_service/integrity/IpSvcHeartBeatMgmt:1.0","IDL:org/csapi/IpInterface:1.0"};
	public org.csapi.fw.fw_service.integrity.IpSvcHeartBeatMgmt _this()
	{
		return org.csapi.fw.fw_service.integrity.IpSvcHeartBeatMgmtHelper.narrow(_this_object());
	}
	public org.csapi.fw.fw_service.integrity.IpSvcHeartBeatMgmt _this(org.omg.CORBA.ORB orb)
	{
		return org.csapi.fw.fw_service.integrity.IpSvcHeartBeatMgmtHelper.narrow(_this_object(orb));
	}
	public org.omg.CORBA.portable.OutputStream _invoke(String method, org.omg.CORBA.portable.InputStream _input, org.omg.CORBA.portable.ResponseHandler handler)
		throws org.omg.CORBA.SystemException
	{
		org.omg.CORBA.portable.OutputStream _out = null;
		// do something
		// quick lookup of operation
		java.lang.Integer opsIndex = (java.lang.Integer)m_opsHash.get ( method );
		if ( null == opsIndex )
			throw new org.omg.CORBA.BAD_OPERATION(method + " not found");
		switch ( opsIndex.intValue() )
		{
			case 0: // disableSvcHeartBeat
			{
			try
			{
				_out = handler.createReply();
				disableSvcHeartBeat();
			}
			catch(org.csapi.TpCommonExceptions _ex0)
			{
				_out = handler.createExceptionReply();
				org.csapi.TpCommonExceptionsHelper.write(_out, _ex0);
			}
				break;
			}
			case 1: // enableSvcHeartBeat
			{
			try
			{
				int _arg0=_input.read_long();
				org.csapi.fw.fw_service.integrity.IpFwHeartBeat _arg1=org.csapi.fw.fw_service.integrity.IpFwHeartBeatHelper.read(_input);
				_out = handler.createReply();
				enableSvcHeartBeat(_arg0,_arg1);
			}
			catch(org.csapi.P_INVALID_INTERFACE_TYPE _ex0)
			{
				_out = handler.createExceptionReply();
				org.csapi.P_INVALID_INTERFACE_TYPEHelper.write(_out, _ex0);
			}
			catch(org.csapi.TpCommonExceptions _ex1)
			{
				_out = handler.createExceptionReply();
				org.csapi.TpCommonExceptionsHelper.write(_out, _ex1);
			}
				break;
			}
			case 2: // changeInterval
			{
			try
			{
				int _arg0=_input.read_long();
				_out = handler.createReply();
				changeInterval(_arg0);
			}
			catch(org.csapi.TpCommonExceptions _ex0)
			{
				_out = handler.createExceptionReply();
				org.csapi.TpCommonExceptionsHelper.write(_out, _ex0);
			}
				break;
			}
		}
		return _out;
	}

	public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] obj_id)
	{
		return ids;
	}
}
