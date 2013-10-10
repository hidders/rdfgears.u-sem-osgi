package com.nl.tudelft.rdfgearsUI.client.Dia;

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


import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.xml.client.Document;

public class RGFunctionParamDescription extends RGFunctionParam {
	String descText;
	Element descContainer;
	public RGFunctionParamDescription(String id, String Label, String desc) {
		super(id, Label);
		descText = desc;
	}

	@Override
	void setValueFromString(String s) {
		descText = s;
		descContainer.setInnerText(descText);
	}

	@Override
	void display(Element container) {
		if(elementCache == null){
			initDisplayElement();
			descContainer = DOM.createDiv();
			descContainer.setInnerHTML(descText);
			formContainer.appendChild(descContainer);
			container.appendChild(elementCache);
		}else {
			container.appendChild(elementCache);
		}
	}

	@Override
	void assignHandler(String id) {
		
	}

	@Override
	com.google.gwt.xml.client.Element toXml(Document doc) {
		return null;
	}

}
