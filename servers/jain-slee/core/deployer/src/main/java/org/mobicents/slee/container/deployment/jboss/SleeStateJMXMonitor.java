package org.mobicents.slee.container.deployment.jboss;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.slee.management.SleeManagementMBean;
import javax.slee.management.SleeState;
import javax.slee.management.SleeStateChangeNotification;

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanServerLocator;

/**
 * JMX Client that, through JMX subscriptions, knows if the SLEE container is in running state.
 * 
 * @author martins
 *
 */
public class SleeStateJMXMonitor implements NotificationListener {

	private static final Logger logger = Logger.getLogger(SleeStateJMXMonitor.class);
	
	private final DeploymentManager deploymentManager;

  private SleeState sleeState = SleeState.STOPPED;

	/**
	 * @param deploymentManager
	 */
	public SleeStateJMXMonitor(DeploymentManager deploymentManager) {
		this.deploymentManager = deploymentManager;
	}
	
	private final static Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	private final static String[] EMPTY_STRING_ARRAY = new String[0];
	
	private boolean registredSleeManagementMBeanNotificationListener = false;
	
	/**
	 * 
	 * @return
	 */
	public SleeState getSleeState() {
		synchronized (this) {
			if(!registredSleeManagementMBeanNotificationListener) {
				try {
					// find mbean server
					final MBeanServer server = MBeanServerLocator.locateJBoss();
					// register for notifications when an mbean is registered on server
					server.addNotificationListener(ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate"), this, null, null);
					// check slee mngmnt mbean is registred
					final ObjectName sleeManagementMbeanObjectName = ObjectName.getInstance(SleeManagementMBean.OBJECT_NAME);
					if (server.isRegistered(sleeManagementMbeanObjectName) && !registredSleeManagementMBeanNotificationListener) {
						registerSleeManagementMBeanNotificationListener(server, sleeManagementMbeanObjectName);						
					}
					// else we wait for notification regarding its registration					
				} catch (Throwable e) {
					logger.error(e.getMessage(),e);
				}
			}
			return this.sleeState;
		}
	}
	
	private void registerSleeManagementMBeanNotificationListener(MBeanServer server, ObjectName sleeManagementMbeanObjectName) throws ListenerNotFoundException, InstanceNotFoundException, MalformedObjectNameException, NullPointerException, ReflectionException, MBeanException {
		if(!registredSleeManagementMBeanNotificationListener) {
			// ok, we don't need to know when an mbean registers in server anymore
			try {
				server.removeNotificationListener(ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate"), this);
			}
			catch (Throwable e) {
				logger.warn(e.getMessage(),e);
			}
			// but we need to learn when slee state changes
			server.addNotificationListener(sleeManagementMbeanObjectName, this, null, null);
			registredSleeManagementMBeanNotificationListener = true;
			// check if slee state is running
			SleeState sleeState = (SleeState) server.invoke(sleeManagementMbeanObjectName,"getState",EMPTY_OBJECT_ARRAY,EMPTY_STRING_ARRAY);
			setSleeState(sleeState);
		}
	}		

  private void setSleeState(SleeState sleeState) {
    if (logger.isDebugEnabled()) {
      logger.debug("setSleeState: value = "+sleeState.toString());
    }
    if (this.sleeState != sleeState) {
      this.sleeState = sleeState;
      if (sleeState == SleeState.RUNNING) {
        deploymentManager.sleeIsRunning();
      }       
    }
  }
	
	/* (non-Javadoc)
	 * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
	 */
	public void handleNotification(final Notification notification, Object handback) {
		
		// do in a new thread to avoid txs from mbean server
		Runnable runnable = new Runnable() {			
			public void run() {
				synchronized (SleeStateJMXMonitor.this) {
					if (notification instanceof SleeStateChangeNotification) {
						if (logger.isDebugEnabled()) {
							logger.debug("received slee state change jmx notification "+notification);
						}
						SleeStateChangeNotification sscn = (SleeStateChangeNotification) notification;
			      setSleeState(sscn.getNewState());
					}	
					else if (notification instanceof MBeanServerNotification) {
						MBeanServerNotification mbsn = (MBeanServerNotification) notification;
						try {
							final ObjectName sleeManagementMbeanObjectName = ObjectName.getInstance(SleeManagementMBean.OBJECT_NAME);
							if (mbsn.getType().equals(MBeanServerNotification.REGISTRATION_NOTIFICATION) && mbsn.getMBeanName().equals(sleeManagementMbeanObjectName)) {
								if (logger.isDebugEnabled()) {
									logger.debug("received slee management mbean registration jmx notification "+notification);
								}
								registerSleeManagementMBeanNotificationListener(MBeanServerLocator.locateJBoss(),sleeManagementMbeanObjectName);
							}
						}
						catch (Throwable e) {
							logger.error(e.getMessage(),e);
						}
					}				
				}
			}
		};
		new Thread(runnable).start();
	}

}
