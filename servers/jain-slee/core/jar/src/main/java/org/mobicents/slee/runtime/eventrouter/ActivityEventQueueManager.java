package org.mobicents.slee.runtime.eventrouter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.slee.resource.FailureReason;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;

/**
 * 
 * Manages the queuing of events for a specific activity.
 * 
 * @author Eduardo Martins
 * 
 */

public class ActivityEventQueueManager {

	private static final Logger logger = Logger
			.getLogger(ActivityEventQueueManager.class);

	/**
	 * a dummy value to use as map entry value
	 */
	private static final Object MAP_VALUE = new Object();

	/**
	 * stores the activity end event when set
	 */
	private DeferredEvent activityEndEvent;

	/**
	 * atomic flag to define if the activity end event was queued
	 */
	private AtomicBoolean activityEndEventQueued = new AtomicBoolean(false);

	/**
	 * the set of pending events, i.e., events not committed yet, for this
	 * activity
	 */
	private ConcurrentHashMap<DeferredEvent, Object> pendingEvents = new ConcurrentHashMap<DeferredEvent, Object>();

	/**
	 * the slee container
	 */
	private final SleeContainer sleeContainer;

	/**
	 * the activity context id that identifies this object
	 */
	private final String acId;

	public ActivityEventQueueManager(
			String acId,
			SleeContainer sleeContainer) {
		this.sleeContainer = sleeContainer;
		this.acId = acId;
	}

	/**
	 * Indicates if there are pending events to be routed for the related
	 * activity.
	 * 
	 * @return
	 */
	public boolean noPendingEvents() {
		return pendingEvents.isEmpty();
	}

	/**
	 * Defines that the specified event is now pending
	 * 
	 * @param dE
	 */
	public void pending(DeferredEvent dE) {

		if (logger.isDebugEnabled()) {
			logger.debug("pending event of type " + dE.getEventTypeId()
					+ " for activity context with id "
					+ dE.getActivityContextId());
		}

		if (activityEndEvent == null) {
			// activity end event not set, we accept pending events
			pendingEvents.put(dE, MAP_VALUE);

		} else {
			// signal event router that processing of the event failed
			sleeContainer.getEventRouter().processEventRoutingFailure(dE,
					FailureReason.REASON_OTHER_REASON);
		}
	}

	/**
	 * Signals the manager that the event was committed, and thus can be routed.
	 * 
	 * @param dE
	 */
	public void commit(DeferredEvent dE) {

		if (logger.isDebugEnabled()) {
			logger.debug("committing event of type " + dE.getEventTypeId()
					+ " for activity context with id "
					+ dE.getActivityContextId());
		}

		if (pendingEvents.remove(dE) != null) {
			// confirmed it was a pending event
			if (dE.getEventTypeId().equals(
					ActivityEndEventImpl.EVENT_TYPE_ID)) {
				// store it
				activityEndEvent = dE;
				// check we can route it
				if (pendingEvents.isEmpty()) {
					// no pending events
					if (activityEndEventQueued.compareAndSet(false, true)) {
						// between element removal and check if it's empty there
						// is no sync so we need to ensure only one thread
						// routes the activity end event
						sleeContainer.getEventRouter().routeEvent(dE);
					}
				}
			} else {
				// route the event
				sleeContainer.getEventRouter().routeEvent(dE);
				// perhaps we need to route a frozen activity end event too
				routeActivityEndEventIfNeeded();
			}
		} else {
			sleeContainer.getEventRouter().processEventRoutingFailure(dE,
					FailureReason.REASON_OTHER_REASON);
		}
	}

	private void routeActivityEndEventIfNeeded() {
		if (pendingEvents.isEmpty()) {
			// no pending events
			// now check if we have a frozen activity end event
			if (activityEndEvent != null) {
				// route the frozen activity end event too
				if (activityEndEventQueued.compareAndSet(false, true)) {
					// between pending events map element removal and check if
					// it's empty there is no sync so we need to ensure only one
					// thread routes the activity end event
					sleeContainer.getEventRouter().routeEvent(activityEndEvent);
				}
			}
		}
	}

	/**
	 * Signals that the java transaction who fired the specified event did not
	 * commit, and thus the event should be not routed.
	 * 
	 * @param dE
	 */
	public void rollback(DeferredEvent dE) {

		if (logger.isDebugEnabled()) {
			logger.debug("rolling back event of type " + dE.getEventTypeId()
					+ " for activity context with id "
					+ dE.getActivityContextId());
		}

		if (pendingEvents.remove(dE) != null) {
			// confirmed the event was pending
			routeActivityEndEventIfNeeded();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((ActivityEventQueueManager) obj).acId
					.equals(this.acId);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return acId.hashCode();
	}
}
