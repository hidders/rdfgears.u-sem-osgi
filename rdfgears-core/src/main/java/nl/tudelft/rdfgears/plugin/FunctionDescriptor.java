package nl.tudelft.rdfgears.plugin;

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


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import nl.tudelft.rdfgears.rgl.function.RGLFunction;

public class FunctionDescriptor {
	
	public String functionDescriptor;
	private Class<? extends RGLFunction> functionClass;
	
	public FunctionDescriptor(InputStream functionDescriptor, Class<? extends RGLFunction> functClass) {
		this.functionClass = functClass;
		this.functionDescriptor = convertStreamToString(functionDescriptor);
	}
	
	public FunctionDescriptor(String functionDescriptor, Class<? extends RGLFunction> functClass) {
		this.functionDescriptor = functionDescriptor;
		this.functionClass = functClass;
	}

	public InputStream asInputStream() {
		try {
			return new ByteArrayInputStream(functionDescriptor.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String asString() {
		return functionDescriptor;
	}
	
	public Class<? extends RGLFunction> getFunctionClass() {
		return functionClass;
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

}
