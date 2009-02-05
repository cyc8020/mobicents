/**
 * Start time:11:10:10 2009-01-29<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.container.component.deployment.jaxb.descriptors.profile.query;

import java.util.ArrayList;
import java.util.List;

import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.And;
import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.Compare;
import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.HasPrefix;
import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.LongestPrefixMatch;
import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.Not;
import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.Or;
import org.mobicents.slee.container.component.deployment.jaxb.slee11.profile.RangeMatch;

/**
 * This class is agregation of query expresion elements defined in: slee.1.1
 * specs chapter 10.20.2. Generaly it creates expression tree from passed
 * arguments to match one defined in xml descriptor. This is an abstraction from plain translation from xml structure <br>
 * Start time:11:10:10 2009-01-29<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MQueryExpression {

	
	
	private MQueryExpressionType type;
	private MQueryExpressionType parentType;
	//for not, or, and
	private ArrayList<MQueryExpression> childExpressions=new ArrayList<MQueryExpression>();
	
	//For others
	private MCompare compare=null;
	private MLongestPrefixMatch longestPrefixMatch=null;
	private MHasPrefix hasPrefix=null;
	private MRangeMatch rangeMatch=null;
	/**
	 * Constructs this query expression tree
	 * 
	 * @param o
	 */
	public MQueryExpression(Object o) {

		if(o instanceof Compare)
		{
			this.parentType=null;
			this.type=MQueryExpressionType.Compare;
			this.compare=new MCompare((Compare) o);
		}else if(o instanceof HasPrefix)
		{
			this.parentType=null;
			this.type=MQueryExpressionType.HasPrefix;
			this.hasPrefix=new MHasPrefix( (HasPrefix) o);
		}else if(o instanceof LongestPrefixMatch)
		{
			this.parentType=null;
			this.type=MQueryExpressionType.LongestPrefixMatch;
			this.longestPrefixMatch=new MLongestPrefixMatch( (LongestPrefixMatch) o);
		}else if(o instanceof RangeMatch)
		{
			this.parentType=null;
			this.type=MQueryExpressionType.RangeMatch;
			this.rangeMatch=new MRangeMatch( (RangeMatch) o);
		}else if(o instanceof Or)
		{
			//here we will have atleast two elements
			Or or=(Or)o;
			this.parentType=null;
			this.type=MQueryExpressionType.Or;

			for(Object childRaw:or.getCompareOrRangeMatchOrLongestPrefixMatchOrHasPrefixOrAndOrOrOrNot())
			{
				MQueryExpression child=new MQueryExpression(childRaw);
				child.parentType=this.type;
				this.childExpressions.add(child);
			}
			
		}else if(o instanceof And)
		{
			//here we will have atleast two elements
			And and=(And)o;
			this.parentType=null;
			this.type=MQueryExpressionType.And;

			for(Object childRaw:and.getCompareOrRangeMatchOrLongestPrefixMatchOrHasPrefixOrAndOrOrOrNot())
			{
				MQueryExpression child=new MQueryExpression(childRaw);
				child.parentType=this.type;
				this.childExpressions.add(child);
			}
		}else if(o instanceof Not)
		{
			//Not should have one, we will get one, this is xml validation part
			Not not=(Not)o;
			this.parentType=null;
			this.type=MQueryExpressionType.Not;

			for(Object childRaw:not.getCompareOrRangeMatchOrLongestPrefixMatchOrHasPrefixOrAndOrOrOrNot())
			{
				MQueryExpression child=new MQueryExpression(childRaw);
				child.parentType=this.type;
				this.childExpressions.add(child);
			}
		}else
			throw new IllegalArgumentException("Can not match query expression element to any known: "+o);
		
	}
	public MQueryExpressionType getType() {
		return type;
	}
	public MQueryExpressionType getParentType() {
		return parentType;
	}
	public MCompare getCompare() {
		return compare;
	}
	public MLongestPrefixMatch getLongestPrefixMatch() {
		return longestPrefixMatch;
	}
	public MHasPrefix getHasPrefix() {
		return hasPrefix;
	}
	public MRangeMatch getRangeMatch() {
		return rangeMatch;
	}
	
	public MQueryExpression getNot()
	{
		return this.childExpressions.get(0);
	}
	
	public List<MQueryExpression> getAnd()
	{
		return this.childExpressions;
	}
	
	public List<MQueryExpression> getOr()
	{
		return this.childExpressions;
	}

}