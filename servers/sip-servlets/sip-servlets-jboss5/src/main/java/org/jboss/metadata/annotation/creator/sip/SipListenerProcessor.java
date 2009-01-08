package org.jboss.metadata.annotation.creator.sip;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.sip.annotation.SipListener;

import org.jboss.metadata.annotation.creator.AbstractFinderUser;
import org.jboss.metadata.annotation.creator.Processor;
import org.jboss.metadata.annotation.creator.ProcessorUtils;
import org.jboss.metadata.annotation.finder.AnnotationFinder;
import org.jboss.metadata.sip.spec.SipMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;

public class SipListenerProcessor extends AbstractFinderUser
	implements Processor<SipMetaData, Class<?>> {	

	public SipListenerProcessor(AnnotationFinder<AnnotatedElement> finder) {
		super(finder);
	}
	
	public void process(SipMetaData sipMetaData, Class<?> beanClass) {
		ListenerMetaData beanMetaData = create(beanClass);
		if(beanMetaData == null)
	         return; 
		List<ListenerMetaData> listenersMetaData = sipMetaData.getListeners(); 
		if(listenersMetaData == null) {
			listenersMetaData = new ArrayList<ListenerMetaData>();
			sipMetaData.setListeners(listenersMetaData);			
		}
		listenersMetaData.add(beanMetaData);
	}
	
	public ListenerMetaData create(Class<?> beanClass) {
		SipListener annotation = finder.getAnnotation(beanClass, SipListener.class);
		if (annotation == null)
			return null;

		ListenerMetaData listenerMetaData = new ListenerMetaData();
		listenerMetaData.setListenerClass(beanClass.getCanonicalName());
		
		return listenerMetaData;
	}	

	public Collection<Class<? extends Annotation>> getAnnotationTypes() {
		return ProcessorUtils.createAnnotationSet(SipListener.class);
	}


}