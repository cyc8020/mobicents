package org.csapi.fw.fw_enterprise_operator.service_subscription;

import org.omg.PortableServer.POA;

/**
 *	Generated from IDL interface "IpServiceContractInfoQuery"
 *	@author JacORB IDL compiler V 2.1, 16-Feb-2004
 */

public class IpServiceContractInfoQueryPOATie
	extends IpServiceContractInfoQueryPOA
{
	private IpServiceContractInfoQueryOperations _delegate;

	private POA _poa;
	public IpServiceContractInfoQueryPOATie(IpServiceContractInfoQueryOperations delegate)
	{
		_delegate = delegate;
	}
	public IpServiceContractInfoQueryPOATie(IpServiceContractInfoQueryOperations delegate, POA poa)
	{
		_delegate = delegate;
		_poa = poa;
	}
	public org.csapi.fw.fw_enterprise_operator.service_subscription.IpServiceContractInfoQuery _this()
	{
		return org.csapi.fw.fw_enterprise_operator.service_subscription.IpServiceContractInfoQueryHelper.narrow(_this_object());
	}
	public org.csapi.fw.fw_enterprise_operator.service_subscription.IpServiceContractInfoQuery _this(org.omg.CORBA.ORB orb)
	{
		return org.csapi.fw.fw_enterprise_operator.service_subscription.IpServiceContractInfoQueryHelper.narrow(_this_object(orb));
	}
	public IpServiceContractInfoQueryOperations _delegate()
	{
		return _delegate;
	}
	public void _delegate(IpServiceContractInfoQueryOperations delegate)
	{
		_delegate = delegate;
	}
	public POA _default_POA()
	{
		if (_poa != null)
		{
			return _poa;
		}
		else
		{
			return super._default_POA();
		}
	}
	public java.lang.String[] listServiceContracts() throws org.csapi.TpCommonExceptions,org.csapi.fw.P_ACCESS_DENIED
	{
		return _delegate.listServiceContracts();
	}

	public java.lang.String[] listServiceProfiles(java.lang.String serviceContractID) throws org.csapi.TpCommonExceptions,org.csapi.fw.P_INVALID_SERVICE_CONTRACT_ID,org.csapi.fw.P_ACCESS_DENIED
	{
		return _delegate.listServiceProfiles(serviceContractID);
	}

	public org.csapi.fw.TpServiceContractDescription describeServiceContract(java.lang.String serviceContractID) throws org.csapi.TpCommonExceptions,org.csapi.fw.P_INVALID_SERVICE_CONTRACT_ID,org.csapi.fw.P_ACCESS_DENIED
	{
		return _delegate.describeServiceContract(serviceContractID);
	}

}
