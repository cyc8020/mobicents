/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/

package org.mobicents.slee.runtime.eventrouter;

//import java.io.Serializable;

import javax.slee.ActivityContextInterface;
import javax.slee.Address;
import javax.slee.EventTypeID;
import javax.slee.SLEEException;
import javax.transaction.SystemException;

import org.jboss.logging.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.runtime.activity.ActivityContextHandle;

/**
 * A differed event. When an SBB posts an event, it winds up as one of these. When the tx commits, it actually makes it
 * into the event queue.
 * 
 * @author M. Ranganathan
 * @author Ivelin Ivanov (refactoring)
 * @author eduardomartins
 *
 */
public class DeferredEvent {

	private static Logger log = Logger.getLogger(DeferredEvent.class);

	private final EventTypeID eventTypeId;
	private final ActivityContextHandle activityContextHandle;
	private final Object event;
	private final Address address;
	
	/**
	 * the aci loaded for event routing, to be used in event handling rollbacks
	 */
	private ActivityContextInterface loadedAci;
	
	public DeferredEvent(int eventId, Object event,
			ActivityContextHandle activityContextHandle, Address address)
			throws SystemException {
		this(SleeContainer.lookupFromJndi().getEventManagement()
				.getEventTypeID(eventId), event, activityContextHandle, address);
	}

	public DeferredEvent(EventTypeID eventTypeId, Object event,
			ActivityContextHandle activityContextHandle, Address address)
			throws SystemException {
		if (log.isDebugEnabled()) {
			log.debug("DeferredEvent() " + eventTypeId + "\n"
					+ "Activity Context:" + activityContextHandle);
		}

		this.eventTypeId = eventTypeId;
		this.event = event;
		this.activityContextHandle = activityContextHandle;
		this.address = address;

		SleeContainer sleeContainer = SleeContainer.lookupFromJndi();
		// put event as pending in ac event queue manager
		ActivityEventQueueManager aeqm = sleeContainer.getEventRouter()
				.getEventRouterActivity(activityContextHandle)
				.getEventQueueManager();
		if (aeqm != null) {
			aeqm.pending(this);
			// add tx actions to commit or rollback
			SleeContainer.getTransactionManager().addAfterCommitAction(
					new CommitDeferredEventAction(this, aeqm));
			SleeContainer.getTransactionManager()
					.addAfterRollbackAction(
							new RollbackDeferredEventAction(this,
									activityContextHandle));
		} else {
			throw new SLEEException("unable to find ACs event queue manager");
		}		
	}

	/**
	 * @return Returns the activity.
	 */
	public ActivityContextHandle getActivityContextHandle() {
		return activityContextHandle;
	}

	/**
	 * @return Returns the address.
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @return Returns the event.
	 */
	public Object getEvent() {
		return event;
	}

	/**
	 * @return Returns the eventTypeId.
	 */
	public EventTypeID getEventTypeId() {
		return eventTypeId;
	}

	public ActivityContextInterface getLoadedAci() {
		return loadedAci;
	}
	
	public void setLoadedAci(ActivityContextInterface aci) {
		this.loadedAci = aci;
	}
}
