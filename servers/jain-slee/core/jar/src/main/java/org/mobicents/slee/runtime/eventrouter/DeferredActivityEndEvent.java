package org.mobicents.slee.runtime.eventrouter;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.runtime.activity.ActivityContext;

/**
 * Useful activity end event version of the {@link DeferredEvent}.
 * 
 * @author martins
 *
 */
public class DeferredActivityEndEvent extends DeferredEvent {

	public DeferredActivityEndEvent(ActivityContext ac, SleeContainer sleeContainer) {
		super(ActivityEndEventImpl.EVENT_TYPE_ID,ActivityEndEventImpl.SINGLETON,ac,null,sleeContainer);
	}
}
