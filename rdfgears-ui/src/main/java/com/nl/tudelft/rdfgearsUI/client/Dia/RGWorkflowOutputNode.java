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


import java.util.ArrayList;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.nl.tudelft.rdfgearsUI.client.RGType;
import com.nl.tudelft.rdfgearsUI.client.RGTypeUtils;

public class RGWorkflowOutputNode extends Node{
	Element elementCache = null;
	public RGWorkflowOutputNode(String id, RGWorkflow owner, boolean withHelper) {
		super(id, owner, withHelper);
		isPermanentNode = true;
		setHeaderText("Output");
	}

	@Override
	void draw(RGCanvas canvas) {
		super.setCanvas(canvas);
		Element c = canvas.getElement(); 
		header.setClassName("outputNode");
		c.appendChild(root);/* do this before adding another element and set the handler
							   element has to be added to the canvas then can be manipulated*/
		
		addNodeInputPort(new RGType(RGTypeUtils.getSimpleVarType(canvas.getTypeChecker().createUniqueTypeName())));
		canvas.updateNodeDrawingState(getId(), NodeDrawingState.DONE);
		setupRootEventHandler();
	}

	@Override
	void displayProperties(Element container) {
		if(elementCache == null){
			elementCache = DOM.createDiv();
			elementCache.setClassName("propertyContainer");
			Element labelContainer = DOM.createDiv();
			labelContainer.setClassName("propertyLabelContainer");
			labelContainer.setInnerText("Output");
			elementCache.appendChild(labelContainer);
			
			
			Element descContainer = DOM.createDiv();
			descContainer.setClassName("paramFormHelpText");
			descContainer.setInnerText("Workflow Output Node");
			elementCache.appendChild(descContainer);
			
			container.appendChild(elementCache);
			
		}else{
			container.appendChild(elementCache);
		}
		
		
	}

	@Override
	com.google.gwt.xml.client.Element toXml(Document doc) {
		com.google.gwt.xml.client.Element network =  doc.createElement("network");
		com.google.gwt.xml.client.Element outputType =  doc.createElement("output-type");
		if(inputPaths.size() > 0){
			Path p = inputPaths.get(0);
			String sourceId = p.getSourceId();
			
			if(sourceId.equalsIgnoreCase(canvas.WORKFLOW_INPUT_NODE_ID)){
				Node workflowInput = p.getStartNode();
				network.setAttribute("output", "workflowInputPort:" + workflowInput.getPortNameByPortId(p.getStartPortId()));
			}else{
				network.setAttribute("output", sourceId);
			}
			
			outputType.appendChild(p.getEndPort().getType().getElement());
			
			
//			ArrayList <String> pNames = getPortNames();
//			for(String pN: pNames){
//				NodePort port = getPortByPortName(pN);
//				outputType.appendChild(port.)
//				}
//			}
			network.appendChild(outputType);
		}
		network.setAttribute("x", "" + getX());
		network.setAttribute("y", "" + getY());
		
		return network;
	}

}
