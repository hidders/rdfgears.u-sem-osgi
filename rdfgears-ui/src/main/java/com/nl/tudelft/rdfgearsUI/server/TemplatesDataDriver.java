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


import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.tudelft.rdfgears.plugin.WorkflowTemplate;
import nl.tudelft.wis.usem.plugin.access_management.PluginAccessManagerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TemplatesDataDriver {

	private ConfigurationDataDriver configurationDataDriver;

	public TemplatesDataDriver(ConfigurationDataDriver configurationDataDriver) {
		this.configurationDataDriver = configurationDataDriver;
	}

	public String getWorkflowFileAsNode(String wfId) {
		String fContent = getTemplateFile(wfId);
		if (fContent.startsWith("<error>")) {
			return "<error>Workflow's file cannot be found</error>";
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document wfDoc;
		Element proc, desc, inputs, func, param, output;
		StreamResult result = new StreamResult(new StringWriter());
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document wfXml = dBuilder.parse(new ByteArrayInputStream(fContent
					.getBytes()));
			// transform workflow to node
			Element metadata = (Element) wfXml.getElementsByTagName("metadata")
					.item(0);
			String id = metadata.getElementsByTagName("id").item(0)
					.getTextContent();
			String name = metadata.getElementsByTagName("name").item(0)
					.getTextContent();
			String des = metadata.getElementsByTagName("description").item(0)
					.getTextContent();
			wfDoc = dBuilder.newDocument();

			des = des.replace("&lt;", "<");
			des = des.replace("&gt;", ">");
			des = des.replace("&amp;", "&");

			proc = wfDoc.createElement("processor");
			proc.setAttribute("label", name);
			desc = wfDoc.createElement("description");
			if (des != null)
				desc.appendChild(wfDoc.createTextNode(des));

			proc.appendChild(desc);

			inputs = wfDoc.createElement("inputs");
			func = wfDoc.createElement("function");
			func.setAttribute("type", "custom-java");
			param = wfDoc.createElement("param");
			param.setAttribute("name", "implementation");
			param.setAttribute("value", "workflow:" + id);
			func.appendChild(param);
			inputs.appendChild(func);

			// parse the input port
			Element wfInputList = (Element) wfXml.getElementsByTagName(
					"workflowInputList").item(0);
			NodeList inputPorts = wfInputList
					.getElementsByTagName("workflowInputPort");
			for (int i = 0; i < inputPorts.getLength(); i++) {
				Element inputP = (Element) inputPorts.item(i);
				String inName = inputP.getAttribute("name");

				Element data = wfDoc.createElement("data");
				data.setAttribute("iterate", "false");
				data.setAttribute("name", inName);
				data.setAttribute("label", inName);
				if (inputP.getElementsByTagName("type").getLength() > 0) {
					Element t = (Element) wfDoc.importNode(inputP
							.getElementsByTagName("type").item(0), true);
					data.appendChild(t);
				}
				inputs.appendChild(data);
			}
			proc.appendChild(inputs);

			Element network = (Element) wfXml.getElementsByTagName("network")
					.item(0);

			if (network.hasAttribute("output")) {
				output = wfDoc.createElement("output");
				if (wfXml.getElementsByTagName("output-type").getLength() > 0) {
					Element wOutput = (Element) wfDoc.importNode(wfXml
							.getElementsByTagName("output-type").item(0), true);
					wfDoc.renameNode(wOutput, null, "type");
					output.appendChild(wOutput);
				}

				proc.appendChild(output);
			}

			wfDoc.appendChild(proc);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(wfDoc);
			t.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
			return "<error>Workflow's file has an invalid format</error>";
		}
		// System.out.println(result.getWriter().toString());
		return result.getWriter().toString();
	}

	public String getTemplateFile(String wfId) {
		wfId = wfId.trim();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		// System.out.println("Looking for wf with id:" + wfId);
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			for (WorkflowTemplate template : PluginAccessManagerFactory
					.getPluginManager().getServices(WorkflowTemplate.class)) {
				Document d = dBuilder.parse(template.asInputStream());
				d.getDocumentElement().normalize();

				Element meta = (Element) d.getElementsByTagName("metadata")
						.item(0);
				if (meta != null) {
					if (meta.hasChildNodes()) {
						if (meta.getElementsByTagName("id").getLength() > 0) {
							Element id = (Element) meta.getElementsByTagName(
									"id").item(0);
							if (id.hasChildNodes()) {
								String ids = id.getTextContent().trim();
								if (wfId.equals(ids)) {
									return DataDriverUtils.readFileToString(template.asInputStream());
								}
							}
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "<error>Workflow's file cannot be found</error>";
	}

	public String getTemplateDirContent() {
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
			root = doc.createElement("workflows");
			doc.appendChild(root);
			for (WorkflowTemplate template : PluginAccessManagerFactory
					.getPluginManager().getServices(WorkflowTemplate.class)) {
				Document d = dBuilder.parse(template.asInputStream());
				d.getDocumentElement().normalize();
				Element wf = (Element) d.getElementsByTagName("rdfgears").item(
						0);

				Element meta = (Element) wf.getElementsByTagName("metadata")
						.item(0);
				// System.out.println("id size:" +
				// meta.getElementsByTagName("id").getLength());
				// Element ide = (Element)
				// meta.getElementsByTagName("id").item(0);
				// System.out.println("id:" + ide.getTextContent());
				String id = meta.getElementsByTagName("id").item(0)
						.getTextContent();
				String name = meta.getElementsByTagName("name").item(0)
						.getTextContent();
				String desc = meta.getElementsByTagName("description").item(0)
						.getTextContent();

				Element newWf = doc.createElement("item");
				// newNode.setAttribute("id",
				// FilenameUtils.removeExtension(op.getName()));
				newWf.setAttribute("id", id);
				newWf.setAttribute("name", name);
				if (desc != null) {
					if (desc.length() > 0) {
						Element descEl = doc.createElement("description");
						descEl.appendChild(doc.createTextNode(desc));
						newWf.appendChild(descEl);
					}
				}
				if (meta.getElementsByTagName("category").getLength() > 0) {
					String cat = meta.getElementsByTagName("category").item(0)
							.getTextContent();
					if (cat.trim().length() > 0) {
						if (categoryNames.contains(cat)) {
							catName2Element.get(cat).appendChild(newWf);
						} else {
							categoryNames.add(cat);
							Element catElement = doc.createElement("category");
							catElement.setAttribute("name", cat);
							catElement.appendChild(newWf);
							catName2Element.put(cat, catElement);
						}
					} else {
						root.appendChild(newWf);
					}
				} else {
					root.appendChild(newWf);
				}
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

		// System.out.println(result.getWriter().toString());
		return result.getWriter().toString();
	}

}
