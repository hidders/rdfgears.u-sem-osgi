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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.DOM;
import com.google.gwt.xml.client.Document;
import com.nl.tudelft.rdfgearsUI.client.RGType;
import com.nl.tudelft.rdfgearsUI.client.RGTypeUtils;

public class RGFunctionParamDataEntity extends RGFunctionParam{
	private String vals = null;
	private ArrayList <String> fieldFormIds = new ArrayList <String>();
	private Map <String, String> id2EntryId = new HashMap <String, String>();
	
	private String inputFormId, groupId;
	Node owner;
	private int inputCounter = 0;
	
	public RGFunctionParamDataEntity(String id, String value, String label, String gId, Node n) {
		super(id, label);
		groupId = gId;
		owner = n;
		pType = RGFunctionParamType.INPUT_FIELDS;
		vals = value;
		
		if(vals != null)
			if(vals.contains(";")){
				String[] vs = vals.split(";");
				for(String v: vs){
					addPort(v);
				}
			}
	}

	@Override
	void display(Element container) {
		if(elementCache == null){
			initDisplayElement();

			Element addFieldButton = DOM.createDiv();
			//addFieldButton.setInnerText("+ add input");
			addFieldButton.setInnerHTML("<img src=\"images/add.png\" /> add input");
			String addFieldButtonId = owner.canvas.createUniqueId();
			addFieldButton.setId(addFieldButtonId);
			addFieldButton.setClassName("addFieldButton");
			
			elementCache.appendChild(addFieldButton);
			
			
			if(!desc.equals("")){
				Element descContainer = DOM.createDiv();
				descContainer.setClassName("paramFormHelpText");
				descContainer.setInnerText(desc);
				elementCache.appendChild(descContainer);
			}
			
			container.appendChild(elementCache);
			
			assignAddButtonHandler(addFieldButtonId);
			
			if(fieldFormIds.size() > 0){
				for(String fId: fieldFormIds){
					String holderId = owner.canvas.createUniqueId();
					String dbuttonId = owner.canvas.createUniqueId();
					String value = owner.getPortNameByEntryId(id2EntryId.get(fId));
					Element holder = createForm(holderId, fId, dbuttonId, value);
					formContainer.appendChild(holder);
					
					assignHandler(fId);
					assignDelButtonHandler(dbuttonId, holderId, fId);
				}
			}			
		}else{
			container.appendChild(elementCache);
		}
		
	}
	
	public void addPort(String value){
		String formCandidateId = owner.canvas.createUniqueId();
		String entryId = owner.canvas.createUniqueId();
		
		fieldFormIds.add(formCandidateId);
		owner.addInputEntry(entryId, value, new RGType(RGTypeUtils.getSimpleVarType(owner.canvas.getTypeChecker().createUniqueTypeName())), value, false, groupId);
		id2EntryId.put(formCandidateId, entryId);
		inputCounter += 1;
	}
	
	private Element createForm(String holderId, String formId, String delButtonId, String value){
		Element holder = DOM.createDiv();
		holder.setId(holderId);
		Element deleteButton = DOM.createDiv();
		//deleteButton.setInnerText("X");
		deleteButton.setInnerHTML("<img src=\"images/del-white.png\"/>");
		deleteButton.setId(delButtonId);
		deleteButton.setAttribute("style", "display:inline;margin-left:5px;");
		
		Element t = DOM.createInputText();
		t.setAttribute("value",value);
		t.setClassName("inputString");
		t.setId(formId);
		
		holder.appendChild(t);
		holder.appendChild(deleteButton);
		
		return holder;
	}
	
	String addInputField(String value){
		String id = owner.canvas.createUniqueId();
		String holderId = owner.canvas.createUniqueId();
		String dbuttonId = owner.canvas.createUniqueId();
		
		Element holder = createForm(holderId, id, dbuttonId, value);
		
		fieldFormIds.add(id);
		
		if(formContainer != null){
			formContainer.appendChild(holder);
			Element t = holder.getElementsByTagName("input").getItem(0);
			t.focus();
			String entryId = owner.canvas.createUniqueId();
			owner.addInputEntry(entryId, value, new RGType(RGTypeUtils.getSimpleVarType(owner.canvas.getTypeChecker().createUniqueTypeName())), value, false, groupId);
//			owner.redrawPaths();
			
			id2EntryId.put(id, entryId);
			
			assignHandler(id);
			assignDelButtonHandler(dbuttonId, holderId, id);
			
		}else{
			Log.debug("formContainer NULL !!");
		}
		
		return id;
	}
	
	void removeField(String id){
		
	}
	
	void assignDelButtonHandler(String buttonId, final String holderId, final String formId){
		$("#" + buttonId).click(new Function(){
			@Override
			public void f(){
				removeField(holderId);
				fieldFormIds.remove(formId);
				$("#" + holderId).remove();
				owner.removeEntry(id2EntryId.get(formId));
//				owner.redrawPaths();
				id2EntryId.remove(formId);
				collectFieldVals();
			}
		});
//		$("#" + buttonId).mouseover(new Function(){
//			@Override
//			public void f(){
//				$("#" + buttonId).html("<img src=\"images/del-red.png\"/>");
//			}
//		});
//		$("#" + buttonId).mouseout(new Function(){
//			@Override
//			public void f(){
//				$("#" + buttonId).html("<img src=\"images/del-white.png\"/>");
//			}
//		});
	}
	
	void collectFieldVals(){
		vals = "";
		
		for(int i = 0; i < fieldFormIds.size(); i++){
			String fId = fieldFormIds.get(i);
			vals = vals + $("#" + fId).val() + ";";
		}
	}
	
	void assignAddButtonHandler(String id){
		$("#" + id).click(new Function(){
			@Override
			public void f(){
				inputFormId = addInputField("input" + inputCounter);
				inputCounter += 1;
			}
		});
	}
	
	@Override
	void assignHandler(final String id) {
		$("#" + id).blur(new Function(){
			@Override
			public void f(){
				owner.updateEntryText(id2EntryId.get(id), $("#" + id).val());
				owner.updatePortNameByEntryId(id2EntryId.get(id), $("#" + id).val());
				collectFieldVals();
			}
		});
		
	}

	@Override
	void setValueFromString(String s) {
		
	}

	@Override
	com.google.gwt.xml.client.Element toXml(Document doc) {
		com.google.gwt.xml.client.Element var = doc.createElement("config");
		var.setAttribute("param", "bindVariables");
		if(vals != null){
			if(vals.length() > 0){
				var.appendChild(doc.createTextNode(vals));
			}
		}
		return var;
	}
}
