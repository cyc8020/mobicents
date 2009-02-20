package javax.megaco.pkg.CPToneDetPkg;

import javax.megaco.pkg.MegacoPkg;
import javax.megaco.pkg.PkgEventItem;

/**
 * The MEGACO Caller Waiting Tone event class extends the PkgEventItem class.
 * This is a final class. This class defines Caller Waiting Tone event of MEGACO
 * Call Progress Tone Detect package. The methods shall define that this event
 * item belongs to the Call Progress Tone Detect package.
 */
public final class CPToneDetCrEvt extends PkgEventItem {

	/**
	 * Identifies Caller Waiting Tone event of the MEGACO Call Progress Tone
	 * Detect Package. Its value shall be set equal to 0x0038.
	 */
	public static final int CP_TONE_DET_CR_EVENT = 0x0038;

	private CPToneDetPkg itemsPackageId = new CPToneDetPkg();

	/**
	 * Constructs a Jain MEGACO Object representing event Item of the MEGACO
	 * Package for event Caller Waiting Tone and Package as Call Progress Tone
	 * Detect.
	 */
	public CPToneDetCrEvt() {
		super();
		super.itemId = CP_TONE_DET_CR_EVENT;
		super.eventId = CP_TONE_DET_CR_EVENT;
	}

	/**
	 * This method is used to get the event identifier from an Event Item
	 * object. The implementations of this method in this class returns the id
	 * of the Caller Waiting event of Call Progress Tone Detect Package.
	 * 
	 * @return It shall return {@link CP_TONE_DET_CR_EVENT}.
	 */
	public int getEventId() {

		return super.eventId;
	}

	/**
	 * This method is used to get the item identifier from an Item object. The
	 * implementations of this method in this class returns the id of the Caller
	 * Waiting event of Call Progress Tone Detect Package.
	 * 
	 * @return It shall return {@link CP_TONE_DET_CR_EVENT}.
	 */
	public int getItemId() {

		return super.itemId;
	}

	/**
	 * This method gets the package to which the item belongs. Since the Caller
	 * Waiting Tone event is defined in the Call Progress Tone Detect Package of
	 * MEGACO protocol, this method returns the CPToneDetPkg class object.
	 * 
	 * @return
	 */
	public MegacoPkg getItemsPackageId() {
		return itemsPackageId;
	}

}
