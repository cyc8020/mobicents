package org.csapi.cm;

/**
 *	Generated from IDL definition of exception "P_ILLEGAL_SITE_ID"
 *	@author JacORB IDL compiler 
 */

public final class P_ILLEGAL_SITE_IDHolder
	implements org.omg.CORBA.portable.Streamable
{
	public org.csapi.cm.P_ILLEGAL_SITE_ID value;

	public P_ILLEGAL_SITE_IDHolder ()
	{
	}
	public P_ILLEGAL_SITE_IDHolder(final org.csapi.cm.P_ILLEGAL_SITE_ID initial)
	{
		value = initial;
	}
	public org.omg.CORBA.TypeCode _type ()
	{
		return org.csapi.cm.P_ILLEGAL_SITE_IDHelper.type ();
	}
	public void _read(final org.omg.CORBA.portable.InputStream _in)
	{
		value = org.csapi.cm.P_ILLEGAL_SITE_IDHelper.read(_in);
	}
	public void _write(final org.omg.CORBA.portable.OutputStream _out)
	{
		org.csapi.cm.P_ILLEGAL_SITE_IDHelper.write(_out, value);
	}
}
