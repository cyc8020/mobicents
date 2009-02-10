/**
 * Start time:17:07:31 2009-01-31<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.container.component.validator;

import javassist.CtClass;
import javassist.NotFoundException;

import org.mobicents.slee.container.component.SbbComponent;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.SbbDescriptorImpl;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.TCUtilityClass;
import org.mobicents.slee.container.component.validator.ClassUtils;
import org.mobicents.slee.container.component.validator.SbbComponentValidator;

/**
 * Start time:17:07:31 2009-01-31<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SbbComponentValidatorSbbConstraintsChildRelationsTest extends
		TCUtilityClass {

	public static final String _SBB_JAR_ONE_11_CONSTRAINTS_NO_CHILD_RELATION = "xml/validator/sbb/child/sbb-jar-one-SbbConstraintsOkNoChildRelation_1_1.xml";
	public static final String _SBB_JAR_ONE_11_CONSTRAINTS_2_CHILD_RELATION = "xml/validator/sbb/child/sbb-jar-one-SbbConstraintsOk2ChildRelation_1_1.xml";
	public static final String _SBB_JAR_ONE_11_CONSTRAINTS_2_CHILD_RELATION_THROW_EXCEPTION = "xml/validator/sbb/child/sbb-jar-one-SbbConstraintsOkChildRelationThrowException_1_1.xml";
	public static final String _SBB_JAR_ONE_11_CONSTRAINTS_2_CHILD_RELATION_WRONG_PREFIX = "xml/validator/sbb/child/sbb-jar-one-SbbConstraintsOk2ChildRelationWrongPrefix_1_1.xml";

	public void testSbbOne11Constraints2ChildRelation() throws Exception {
		final SbbDescriptorImpl descriptor = SbbDescriptorImpl
				.parseDocument(
						super
								.parseDocument(_SBB_JAR_ONE_11_CONSTRAINTS_2_CHILD_RELATION),
						null)[0];
		SbbComponent component = new SbbComponent();
		component.setAbstractSbbClass(Thread.currentThread()
				.getContextClassLoader().loadClass(
						descriptor.getSbbAbstractClass()
								.getSbbAbstractClassName()));

		SbbComponentValidator validator = new SbbComponentValidator();
		component.setDescriptor(descriptor);
		validator.setComponent(component);

		boolean b = validator.validateGetChildRelationMethods(ClassUtils
				.getAbstractMethodsFromClass(component.getAbstractSbbClass()),
				ClassUtils.getAbstractMethodsFromSuperClasses(component
						.getAbstractSbbClass()));

		assertTrue("Sbb class has not been validated, it should be", b);

	}

	public void testSbbOne11ConstraintsNoChildRelation() throws Exception {
		final SbbDescriptorImpl descriptor = SbbDescriptorImpl
				.parseDocument(
						super
								.parseDocument(_SBB_JAR_ONE_11_CONSTRAINTS_NO_CHILD_RELATION),
						null)[0];
		SbbComponent component = new SbbComponent();
		component.setAbstractSbbClass(Thread.currentThread()
				.getContextClassLoader().loadClass(
						descriptor.getSbbAbstractClass()
								.getSbbAbstractClassName()));

		SbbComponentValidator validator = new SbbComponentValidator();
		component.setDescriptor(descriptor);
		validator.setComponent(component);

		boolean b = validator.validateGetChildRelationMethods(ClassUtils
				.getAbstractMethodsFromClass(component.getAbstractSbbClass()),
				ClassUtils.getAbstractMethodsFromSuperClasses(component
						.getAbstractSbbClass()));

		assertFalse(
				"Sbb class has been validated, it should not be, it does not have child relateion method defined in descriptor",
				b);

	}

	public void testSbbOne11Constraints2ChildRelationWrongPrefix()
			throws Exception {
		final SbbDescriptorImpl descriptor = SbbDescriptorImpl
				.parseDocument(
						super
								.parseDocument(_SBB_JAR_ONE_11_CONSTRAINTS_2_CHILD_RELATION_WRONG_PREFIX),
						null)[0];
		SbbComponent component = new SbbComponent();
		component.setAbstractSbbClass(Thread.currentThread()
				.getContextClassLoader().loadClass(
						descriptor.getSbbAbstractClass()
								.getSbbAbstractClassName()));

		SbbComponentValidator validator = new SbbComponentValidator();
		component.setDescriptor(descriptor);
		validator.setComponent(component);

		boolean b = validator.validateGetChildRelationMethods(ClassUtils
				.getAbstractMethodsFromClass(component.getAbstractSbbClass()),
				ClassUtils.getAbstractMethodsFromSuperClasses(component
						.getAbstractSbbClass()));

		assertFalse(
				"Sbb class has been validated, it should not be, it does declare child relation method with wrong prefix",
				b);

	}

	public void testSbbOne11Constraints2ChildRelationThrowsException()
			throws Exception {
		final SbbDescriptorImpl descriptor = SbbDescriptorImpl
				.parseDocument(
						super
								.parseDocument(_SBB_JAR_ONE_11_CONSTRAINTS_2_CHILD_RELATION_THROW_EXCEPTION),
						null)[0];
		SbbComponent component = new SbbComponent();
		component.setAbstractSbbClass(Thread.currentThread()
				.getContextClassLoader().loadClass(
						descriptor.getSbbAbstractClass()
								.getSbbAbstractClassName()));

		SbbComponentValidator validator = new SbbComponentValidator();
		component.setDescriptor(descriptor);
		validator.setComponent(component);

		boolean b = validator.validateGetChildRelationMethods(ClassUtils
				.getAbstractMethodsFromClass(component.getAbstractSbbClass()),
				ClassUtils.getAbstractMethodsFromSuperClasses(component
						.getAbstractSbbClass()));

		assertFalse(
				"Sbb class has been validated, it should not be, it does declare child relation method with throws clause",
				b);

	}

}
