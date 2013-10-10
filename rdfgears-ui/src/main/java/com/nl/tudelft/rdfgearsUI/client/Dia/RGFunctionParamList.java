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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.nl.tudelft.rdfgearsUI.client.RGServiceAsync;
import com.nl.tudelft.rdfgearsUI.client.RGType;
import com.nl.tudelft.rdfgearsUI.client.RGTypeUtils;

public class RGFunctionParamList extends RGFunctionParam {
    RGServiceAsync RService = null;
    String value = null, source;
    Node owner;
    String formId;
    String groupId; // the entry holder id in node
    private boolean isLoadingItem = false;
    private Element loadingAnimation;

    private ArrayList<String> itemIds = new ArrayList<String>();
    private Map<String, RGFunctionParamListItem> itemMap = new HashMap<String, RGFunctionParamListItem>();

    private ArrayList<String> entryIds = new ArrayList<String>();

    public RGFunctionParamList(String id, final String val, String label,
	    com.google.gwt.xml.client.Element embeddedXmlSource, String gId,
	    Node n) {
	super(id, label);
	value = val;
	owner = n;
	formId = owner.canvas.createUniqueId();
	groupId = gId;

	Log.debug("RGFunctionParamList. ");
	if (embeddedXmlSource.hasAttribute("type")) {
	    String type = embeddedXmlSource.getAttribute("type");

	    isLoadingItem = true;

	    RService = owner.canvas.getRemoteService();
	    Log.debug("Requesting list: " + type);
	    RService.getListItems(type, new AsyncCallback<String>() {

		public void onSuccess(String result) {
		    Log.debug(result);
		    Document nodeDom = XMLParser.parse(result);

		    NodeList options = nodeDom.getElementsByTagName("option");
		    parseListItems(options);

		    if (val != null) {
			if (val.length() > 0) {
			    updateNodeBySelectedItem(val);
			}
		    }
		    
		    fireEventFromSource(new ComponentLoadedEvent(), RGFunctionParamList.this);
		}

		public void onFailure(Throwable caught) {
		    Log.error("RPC Failed");
		}
	    });

	} else {
	    NodeList options = embeddedXmlSource.getElementsByTagName("option");
	    parseListItems(options);

	    if (val != null) {
		if (val.length() > 0) {
		    updateNodeBySelectedItem(val);
		}
	    }
	}

    }

    void parseListItems(NodeList items) {
	com.google.gwt.xml.client.Element item, inputData, typeDef;
	RGType t;
	for (int i = 0; i < items.getLength(); i++) {
	    item = (com.google.gwt.xml.client.Element) items.item(i);

	    String itemValue = item.getAttribute("value");
	    String itemLabel = item.getAttribute("label");
	    RGFunctionParamListItem listItem = new RGFunctionParamListItem(i,
		    itemValue, itemLabel);
	    if (item.hasChildNodes()) {// input port <data ..> inside the item
		NodeList inputs = item.getElementsByTagName("data");
		for (int j = 0; j < inputs.getLength(); j++) {
		    inputData = (com.google.gwt.xml.client.Element) inputs
			    .item(j);
		    boolean iterate = (inputData.getAttribute("iterate")
			    .equalsIgnoreCase("false")) ? false : true;

		    if (inputData.hasChildNodes()) {
			typeDef = (com.google.gwt.xml.client.Element) inputData
				.getElementsByTagName("type").item(0);
			if (typeDef != null) {
			    t = new RGType(owner.canvas.getTypeChecker()
				    .rename(RGTypeUtils.unwrap(typeDef),
					    getId()));
			} else {
			    t = new RGType("<var name=\""
				    + owner.canvas.getTypeChecker()
					    .createUniqueTypeName() + "\"/>");
			}
		    } else {
			t = new RGType("<var name=\""
				+ owner.canvas.getTypeChecker()
					.createUniqueTypeName() + "\"/>");
		    }

		    listItem.addInputData(inputData.getAttribute("name"),
			    inputData.getAttribute("label"), t, iterate);
		}
	    }
	    itemMap.put(itemValue, listItem);
	    itemIds.add(itemValue);
	}
	if (isLoadingItem) {
	    isLoadingItem = false;
	    if (loadingAnimation != null && formContainer != null) {
		ListBox lb = buildListForm();
		loadingAnimation.removeFromParent();
		formContainer.appendChild(lb.getElement());
		assignHandler(formId);
	    }
	}
    }

    ListBox buildListForm() {
	ListBox lb = new ListBox();
	lb.getElement().setId(formId);
	RGFunctionParamListItem item;
	int i = 1;
	lb.addItem("Select...", "novalue");
	for (String itemId : itemIds) {
	    item = itemMap.get(itemId);
	    lb.addItem(item.label, item.value);
	    if (value != null) {
		if (item.value.equalsIgnoreCase(value)) {
		    lb.setSelectedIndex(i);
		}
	    }
	    i++;
	}
	if (value == null) {
	    lb.setSelectedIndex(0);
	}

	return lb;
    }

    @Override
    void display(Element container) {
	if (elementCache == null) {
	    initDisplayElement();

	    if (isLoadingItem) {
		loadingAnimation = DOM.createDiv();
		loadingAnimation.setAttribute("style",
			"width:100%; text-align:center;padding-top:4px;");
		loadingAnimation
			.setInnerHTML("<img src=\"images/loader.gif\">");
		formContainer.appendChild(loadingAnimation);
	    } else {

		ListBox lb = buildListForm();
		lb.getElement().setClassName("propertyParamList");

		formContainer.appendChild(lb.getElement());
		assignHandler(formId);
	    }

	    if (!desc.equals("")) {
		Element descContainer = DOM.createDiv();
		descContainer.setClassName("paramFormHelpText");
		descContainer.setInnerText(desc);
		elementCache.appendChild(descContainer);
	    }

	    container.appendChild(elementCache);
	} else {
	    container.appendChild(elementCache);
	}
    }

    void updateNodeBySelectedItem(String itemKey) {
	for (String entryId : entryIds) {
	    owner.removeEntry(entryId);
	}
	entryIds.clear();
	owner.changeGroupHeaderText(groupId, itemMap.get(itemKey).label);
	RGFunctionParamListItem selectedItem = itemMap.get(itemKey);
	for (int i = 0; i < selectedItem.getInputNum(); i++) {
	    RGFunctionParamListInputData inputData = selectedItem
		    .getInputDataByIdx(i);
	    String newEntryId = owner.canvas.createUniqueId();
	    entryIds.add(newEntryId);

	    owner.addInputEntry(newEntryId, inputData.name, inputData.type,
		    inputData.label, inputData.iterate, groupId);
	}
    }

    @Override
    void assignHandler(final String id) {

	$("#" + id).change(new Function() {
	    @Override
	    public void f() {
		value = $("#" + id).val();
		Log.debug("selected value: " + $("#" + id).val());
		updateNodeBySelectedItem(value);
	    }
	});
    }

    @Override
    void setValueFromString(String s) {

    }

    @Override
    com.google.gwt.xml.client.Element toXml(Document doc) {
	com.google.gwt.xml.client.Element var = doc.createElement("config");
	var.setAttribute("param", getId());
	if (value != null) {
	    if (value.length() > 0) {
		var.appendChild(doc.createTextNode(value));
	    }
	}
	return var;
    }
    
    public boolean isLoadingItem() {
	return isLoadingItem;
    }

}
