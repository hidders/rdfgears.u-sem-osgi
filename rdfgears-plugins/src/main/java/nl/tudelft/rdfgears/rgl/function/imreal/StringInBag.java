package nl.tudelft.rdfgears.rgl.function.imreal;

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

import java.util.Map;


import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 *  Determines if a particular string literal occurs in a bag
 *  @author Claudia
 *
 */
public class StringInBag extends SimplyTypedRGLFunction  {
	public static String bag = "bag";
	public static String searchString = "searchString";
	
	public StringInBag(){
		requireInputType(bag, BagType.getInstance(new SuperTypePattern())); // any bag will do
		requireInputType(searchString, RDFType.getInstance());
	}
	
	@Override
	public void initialize(Map<String, String> config) {
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		
		RGLValue rdfValue = inputRow.get(searchString);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());
		
		String toSearchFor = rdfValue.asLiteral().toString();
		
		BagValue bv = inputRow.get(bag).asBag();
		for(RGLValue val : bv)
		{
			if(val.toString().equals(toSearchFor))
				return ValueFactory.createTrue();			
		}
		return ValueFactory.createFalse(); 
	}
	

	@Override
	public RGLType getOutputType() {
		return RDFType.getInstance();
	}

}
