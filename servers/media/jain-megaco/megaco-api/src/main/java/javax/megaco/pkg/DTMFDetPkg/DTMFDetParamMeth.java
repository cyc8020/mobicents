package javax.megaco.pkg.DTMFDetPkg;

import javax.megaco.message.DescriptorType;
import javax.megaco.pkg.ParamValueType;
import javax.megaco.pkg.PkgItemParam;

/**
 *The MEGACO parameter class for the Termination Method Parameter is associated
 * with Signal Completion event of DTMF Detect Package. This class defines all
 * the static information for this parameter.
 */
public class DTMFDetParamMeth extends PkgItemParam {

	/**
	 * Identifies Termination method parameter of the MEGACO DTMF Detection
	 * Package. Its value shall be set equal to 0x0003.
	 */
	public static final int DTMF_DET_PARAM_METH = 0x0003;
	private DTMFDetPkg itemsPackageId = new DTMFDetPkg();

	public DTMFDetParamMeth() {
		super();

		super.paramValueType = ParamValueType.M_ITEM_PARAM_VALUE_STRING;
		super.paramId = DTMF_DET_PARAM_METH;
		super.paramsDescriptorIds = new int[] { DescriptorType.M_OBSERVED_EVENT_DESC };
		//FIXME: is this ok?? should not it be  DTMF_DET_PARAM_METH?
		super.paramsItemIds = new int[] { DTMFDetCeEvt.DTMF_DET_CE_EVENT };
	}

	/**
	 * The method can be used to get the parameter identifier as defined in the
	 * MEGACO packages. The implementation of this method in this class returns
	 * Id of Digit String parameter.
	 * 
	 * @return paramId - Returns param id {@link DTMF_DET_PARAM_METH}.
	 */
	public int getParamId() {

		return super.paramId;
	}

	/**
	 * The method can be used to get the type of the parameter as defined in the
	 * MEGACO packages. These could be one of string or enumerated value or
	 * integer or double value or boolean.
	 * 
	 * @return It returns {@link _ITEM_PARAM_VALUE_STRING} indicating that the
	 *         parameter is a string.
	 */
	public int getParamValueType() {
		return super.paramValueType;
	}

	public DTMFDetPkg getItemsPackageId() {
		return itemsPackageId;
	}

	/**
	 * The method can be used to get the descriptor ids corresponding to the
	 * parameters to which the parameter can be set.
	 * 
	 * @return This parameter can be present in Event descriptor. It shall thus
	 *         return a value {@link M_OBSERVED_EVENT_DESC} as a part of integer
	 *         vector.
	 */
	public int[] getParamsDescriptorIds() {
		return super.paramsDescriptorIds;
	}

	/**
	 * The method can be used to get the item ids corresponding to the
	 * parameters to which the parameter can be set. This method specifies the
	 * valid item (event/signal) ids to which the parameter can belong to.
	 * 
	 * @return The integer value corresponding to Network Failure. Thus this
	 *         shall return {@link DTMF_DET_CE_EVENT}.
	 */
	public int[] getParamsItemIds() {
		return super.paramsItemIds;
	}

}
