package com.nl.tudelft.rdfgearsUI.server;

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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.tudelft.rdfgears.plugin.FunctionDescriptor;
import nl.tudelft.wis.usem.plugin.admin.PluginAdminFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FunctionsDataDriver {

	private ConfigurationDataDriver configurationDataDriver;

	public FunctionsDataDriver(ConfigurationDataDriver configurationDataDriver) {
		this.configurationDataDriver = configurationDataDriver;
	}

	public String getFunctionsDirContent() {
		File functionsDir = new File(configurationDataDriver.getFunctionsDir());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		Element root = null;
		StreamResult result = new StreamResult(new StringWriter());
		ArrayList<String> categoryNames = new ArrayList<String>();
		Map<String, Element> catName2Element = new HashMap<String, Element>();

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
			root = doc.createElement("operators");
			doc.appendChild(root);
			if (functionsDir.isDirectory()) {
				File operators[] = functionsDir.listFiles();
				for (File op : operators) {
					if (op.isDirectory()) {
						// root.appendChild(readAllOperatorXmlFiles(op, doc));
					} else {
						Document d = dBuilder.parse(op);
						processDocument(doc, root, categoryNames,
								catName2Element, d);
					}
				}
			}

			List<FunctionDescriptor> services = PluginAdminFactory
					.getPluginAdmin().getServices(FunctionDescriptor.class);

			for (FunctionDescriptor descriptor : services) {
				Document d = dBuilder.parse(descriptor.asInputStream());
				processDocument(doc, root, categoryNames, catName2Element, d);
			}

			for (String cname : categoryNames) {
				if (catName2Element.containsKey(cname)) {
					root.appendChild(catName2Element.get(cname));
				}
			}

			Transformer t = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(doc);
			t.transform(source, result);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return result.getWriter().toString();
	}

	private void processDocument(Document doc, Element root,
			ArrayList<String> categoryNames,
			Map<String, Element> catName2Element, Document d) {
		d.getDocumentElement().normalize();
		Element proc = (Element) d.getElementsByTagName("processor").item(0);
		Element newNode = doc.createElement("item");
		String fId = "";
		NodeList params = proc.getElementsByTagName("param");
		for (int i = 0; i < params.getLength(); i++) {
			Element prm = (Element) params.item(i);
			if (prm.hasAttribute("name")) {
				if (prm.getAttribute("name").equals("implementation")) {
					if (prm.hasAttribute("value")) {
						fId = prm.getAttribute("value").trim();
					}
				}
			}
		}
		newNode.setAttribute("id", "function:" + fId);
		newNode.setAttribute("name", proc.getAttribute("label"));
		if (proc.hasAttribute("category")) {
			String cat = proc.getAttribute("category");
			if (cat.trim().length() > 0) {
				if (categoryNames.contains(cat)) {
					catName2Element.get(cat).appendChild(newNode);
				} else {
					categoryNames.add(cat);
					Element catElement = doc.createElement("category");
					catElement.setAttribute("name", cat);
					catElement.appendChild(newNode);
					catName2Element.put(cat, catElement);
				}
			} else {
				root.appendChild(newNode);
			}
		} else {
			root.appendChild(newNode);
		}
	}

	public String getFunctionFile(String fId) {
		fId = fId.trim();

		File functionsDir = new File(configurationDataDriver.getFunctionsDir());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();

			if (functionsDir.isDirectory()) {
				File operators[] = functionsDir.listFiles();
				for (File op : operators) {
					if (op.isDirectory()) {
						// root.appendChild(readAllOperatorXmlFiles(op, doc));
					} else {
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();

						if (isMatchID(fId, d))
							return DataDriverUtils.readFileToString(op
									.getAbsolutePath());
					}
				}

			}

			List<FunctionDescriptor> services = PluginAdminFactory
					.getPluginAdmin().getServices(FunctionDescriptor.class);

			for (FunctionDescriptor descriptor : services) {
				Document d = dBuilder.parse(descriptor.asInputStream());

				if (isMatchID(fId, d))
					return DataDriverUtils.readFileToString(descriptor
							.asInputStream());
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return "<error> Function's definition file cannot be found !!</error>";
	}

	private boolean isMatchID(String fId, Document d) throws IOException {
		NodeList params = d.getElementsByTagName("param");
		for (int i = 0; i < params.getLength(); i++) {
			Element prm = (Element) params.item(i);
			if (prm.hasAttribute("name")) {
				if (prm.getAttribute("name").equals("implementation")) {
					if (prm.hasAttribute("value")) {
						String fId1 = prm.getAttribute("value").trim();
						if (fId1.equals(fId)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
