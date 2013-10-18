package nl.tudelft.rdfgears.rgl.datamodel.value.impl;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.ComparisonNotDefinedException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;


/**
 * Do NOT use. (Constructor made private to prevent usage). 
 * 
 * This was first much faster than creating a Jena literal with type RDFDataType.XSDDouble. 
 * However, it turns out that creating a Jena literal with a new Double(d) object is also 
 * fast, as opposed to a literal with Double type but a Double.toString(d) object. 
 * 
 * I compared runtime, not memory usage. But do verify this if you care.   
 * 
 * Nevertheless, this class is obsolete until it is shown that is is actually better than the 
 * Jena typed literal. 
 * 
 * To enable use of this class, instantiate it in your ValueFactoryIface#createLiteralDouble implementation. 
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class LiteralDoubleValue extends LiteralValue {
	private Literal node;
	
	private double d; // our value
	
	public LiteralDoubleValue(double value) {
		d = value;
	}
	
	/**
	 * Override getRDFNode. Do not create some Jena-resource with our value-id, but instead just return our Jena 
	 * equivalent Literal Node. 
	 * 
	 *  Get an RDFNode representation of this value. 
	 * Creating it is postponed until this function is called, because an RDFNode object is 
	 * a few times heavier than our RDFValue.
	 *  
	 */
	@Override
	public Literal getRDFNode() {
		if (node==null){
			// do create it after all :-) RDF note was requested for some reason. 
			node = Engine.getDefaultModel().createTypedLiteral(new Double(d), XSDDatatype.XSDdouble);
		}
		return node;
	}
	

	public double getValueDouble(){
		return d; 
	}
	
	/**
	 * return the Lexical Form of this literal. That is, the string-representation of the value, 
	 * without language or datatype.  
	 * 
	 */
	@Override
	public String getValueString() {
		return Double.toString(d);
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("\"");
		builder.append(d); 
		builder.append("\"");
		
		builder.append("^^<");
		builder.append(XSDDatatype.XSDdouble.getURI());
		builder.append(">");
		
		return builder.toString();
	}
	
	@Override
	public int compareTo(RGLValue v2){
		try {
			//System.out.println("Node is: "+ ((RDFValue) that).getRDFNode());
			double thatDouble = v2.asLiteral().getValueDouble();
			double thisDouble = getValueDouble();
			
			if (thisDouble==thatDouble) 
				return 0;
			else if (thisDouble<thatDouble) 
				return -1;
			else // (thisDouble>thatDouble)
				return 1;
		}
		catch (RuntimeException e){
			e.printStackTrace();
			throw new ComparisonNotDefinedException(this, v2);
		}
	}

	@Override
	public XSDDatatype getLiteralType() {
		return XSDDatatype.XSDdouble;
	}

	@Override
	public String getLanguageTag() {
		return null; // typed literal has no such tag
	}
}
