/***************************************************
 *                                                 *
 *  Mobicents: The Open Source JSLEE Platform      *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
/*
 * ConcreteActivityContextInterfaceGenerator.java
 * 
 * Created on Aug 17, 2004
 *
 */
package org.mobicents.slee.container.deployment;

import java.util.Iterator;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import javax.slee.management.DeploymentException;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.component.DeployableUnitIDImpl;
import org.mobicents.slee.container.component.MobicentsSbbDescriptor;
import org.mobicents.slee.container.component.deployment.ClassPool;
import org.mobicents.slee.container.deployment.interceptors.ActivityContextInterfaceInterceptor;
import org.mobicents.slee.container.deployment.interceptors.DefaultActivityContextInterfaceInterceptor;
import org.mobicents.slee.runtime.activity.ActivityContextInterfaceImpl;
import org.mobicents.slee.runtime.activity.SbbActivityContextInterface;

/**
 * Class generating the concrete activity context interface class from the
 * activity context interface provided by a sbb developer
 * 
 * @author DERUELLE Jean <a
 *         href="mailto:jean.deruelle@gmail.com">jean.deruelle@gmail.com </a>
 * @author F.Moggia fixed activitycontext
 */
public class ConcreteActivityContextInterfaceGenerator {

    private MobicentsSbbDescriptor descriptor;

    /**
     * Logger to logg information
     */
    private static Logger logger = null;

    /**
     * Pool to generate or read classes with javassist
     */
    private  ClassPool pool = null;

    /**
     * The interface from which to generate the concrete interface
     */
    protected CtClass activityContextInterface = null;

    /**
     * The the concrete activity interface
     */
    protected CtClass concreteActivityContextInterface = null;

    static {
        logger = Logger
                .getLogger(ConcreteActivityContextInterfaceGenerator.class);
    }

    /**
     *  
     */
    public ConcreteActivityContextInterfaceGenerator(
            MobicentsSbbDescriptor descriptor) {
        this.pool =  ((DeployableUnitIDImpl)descriptor.getDeployableUnit()).getDUDeployer().getClassPool();       
        this.descriptor = descriptor;
    }

    /**
     * Generate the Activity Context Interface Class
     * 
     * @param activityContextInterfaceName
     *            the name of the Activity Context Interface
     * @return the concrete Activity Context Interface class implementing the
     *         Activity Context Interface
     */
    public Class generateActivityContextInterfaceConcreteClass(
            String activityContextInterfaceName) throws DeploymentException {
           

    		String tmpClassName=ConcreteClassGeneratorUtils.CONCRETE_ACTIVITY_INTERFACE_CLASS_NAME_PREFIX
            + activityContextInterfaceName
            + ConcreteClassGeneratorUtils.CONCRETE_ACTIVITY_INTERFACE_CLASS_NAME_SUFFIX;
            
    		concreteActivityContextInterface = pool.makeClass(tmpClassName);
    		CtClass sbbActivityContextInterface = null;
    		try {
                activityContextInterface = pool
                        .get(activityContextInterfaceName);
                sbbActivityContextInterface = pool.get(SbbActivityContextInterface.class.getName());
            } catch (NotFoundException nfe) {
                throw new DeploymentException ("Could not find aci " + activityContextInterfaceName, nfe);
            }
            
            //Generates the extends link
            ConcreteClassGeneratorUtils.createInheritanceLink(concreteActivityContextInterface, sbbActivityContextInterface);
            
            //Generates the implements link
            ConcreteClassGeneratorUtils.createInterfaceLinks(
                    concreteActivityContextInterface, new CtClass[] {
                            activityContextInterface });
            
            //Creates the fields
            createFields();
            //Creates the default constructor
            //createDefaultConstructor();
            //Creates the constructor with parameters
            try {    			
                CtClass[] parameters = new CtClass[] {
                		pool
                        .get(ActivityContextInterfaceImpl.class.getName()),
                        pool
                                .get(MobicentsSbbDescriptor.class.getName()) };
                createConstructorWithParameter(parameters);
            } catch (NotFoundException nfe) {
                logger.error("Could not find class. Constructor With Parameter not created");
                throw new DeploymentException ("Could not find class. Constructor With Parameter not created",nfe);
            }
            
            
            //Generates the methods to implement from the interface
            Map interfaceMethods = ClassUtils
                    .getInterfaceMethodsFromInterface(activityContextInterface);
            generateConcreteMethods(interfaceMethods);
            //generates the class
            String sbbDeploymentPathStr = descriptor.getDeploymentPath();

            try {
            	concreteActivityContextInterface.writeFile(sbbDeploymentPathStr);           	
				if(logger.isDebugEnabled()) { 
                	logger
                        .debug("Concrete Class "
                                + tmpClassName
                                + " generated in the following path "
                                + sbbDeploymentPathStr);
                }
            } catch (Exception e) {

                logger.error("problem generating concrete class", e);
                throw new DeploymentException(
                        "problem generating concrete class! ", e);
            }

            //load the class
            Class clazz = null;
            try {                
                clazz = Thread.currentThread().getContextClassLoader().loadClass(tmpClassName);
            } catch (Exception e1) {            	
                logger.error ("problem loading generated class", e1);
                throw new DeploymentException(
                        "problem loading the generated class! ", e1);
            }

			this.concreteActivityContextInterface.defrost();
            
            return clazz;
        
    }

    private boolean isBaseInterfaceMethod(String methodName) {
        if ("getActivity".equals(methodName) || "attach".equals(methodName)
                || "detach".equals(methodName) || "isEnding".equals(methodName))
            return true;
        return false;
    }

    /**
     * Generates the concrete methods of the class It generates a specific
     * method implementation for the javax.slee.ActivityContextInterface methods
     * for the methods coming from the ActivityContextInterface developer the
     * call is routed to the activity context interface interceptor
     * 
     * @param interfaceMethods
     *            the methods to implement coming from the
     *            ActivityContextInterface developer
     */
    private void generateConcreteMethods(Map interfaceMethods) {
        if (interfaceMethods == null)
            return;

        Iterator it = interfaceMethods.values().iterator();
        while (it.hasNext()) {
            CtMethod interfaceMethod = (CtMethod) it.next();
            if (interfaceMethod != null
                    && isBaseInterfaceMethod(interfaceMethod.getName()))
                continue; //@todo: need to check args also

            ConcreteClassGeneratorUtils.addInterceptedMethod(
                    concreteActivityContextInterface, interfaceMethod,
                    "sbbActivityContextInterfaceInterceptor", false);
        }

    }

    /**
     * Create the Fields needed in the activity context interface concrete class
     */
    protected void createFields() {
    	CtField activityContextInterfaceInterceptor = null;
        try {
            activityContextInterfaceInterceptor = new CtField(pool
                    .get(ActivityContextInterfaceInterceptor.class.getName()),
                    "sbbActivityContextInterfaceInterceptor",
                    concreteActivityContextInterface);
            activityContextInterfaceInterceptor.setModifiers(Modifier.PRIVATE);
            concreteActivityContextInterface
                    .addField(activityContextInterfaceInterceptor);
        } catch (CannotCompileException cce) {
        	logger.error(cce);
        } catch (NotFoundException nfe) {
        	logger.error(nfe);
        }
    }

    /**
     * Creates a constructor with parameters <BR>
     * For every parameter a field of the same class is created in the concrete
     * class And each field is gonna be initialized with the corresponding
     * parameter
     * 
     * @param parameters
     *            the parameters of the constructor to add
     */
    protected void createConstructorWithParameter(CtClass[] parameters) {
        CtConstructor constructorWithParameter = new CtConstructor(parameters,
                concreteActivityContextInterface);
        String constructorBody = 
        	"{"
        		+ "super($1);"
        		+ "sbbActivityContextInterfaceInterceptor = new "+DefaultActivityContextInterfaceInterceptor.class.getName()+"($2);"
        		+ "sbbActivityContextInterfaceInterceptor.setActivityContextInterface($1);"               
        	+ "}";
        try {
            concreteActivityContextInterface
                    .addConstructor(constructorWithParameter);
            constructorWithParameter.setBody(constructorBody);
            if(logger.isDebugEnabled()) { 
            	logger.debug("ConstructorWithParameter created");
            }
        } catch (CannotCompileException e) {
        	logger.error(e);
        }
    }
    
   

}