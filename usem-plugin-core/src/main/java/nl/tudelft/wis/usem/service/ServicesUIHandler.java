package nl.tudelft.wis.usem.service;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.tudelft.wis.usem.plugin.repository.localrepository.LocalRepository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServicesUIHandler {

	private static final String REST_INPUT_URL = "/rdfgears-rest/user/input/";

	public String getServicesHTML() {
		StringBuilder sb = new StringBuilder();

		Map<String, List<WorkflowDesc>> workflowDirContent = getWorkflowDirContent();

		for (String cat : workflowDirContent.keySet()) {

			sb.append("<li class=\"nav-header\">");
			sb.append(cat);
			sb.append("</li>");

			for (WorkflowDesc w : workflowDirContent.get(cat)) {
				sb.append("<li>");
				sb.append("<a onclick=\"navigate('" + REST_INPUT_URL
						+ w.getId() + "')\">");
				sb.append(w.getName());
				sb.append("</a>");
				sb.append("</li>");
			}

		}

		return sb.toString();
	}

	private Map<String, List<WorkflowDesc>> getWorkflowDirContent() {
		File operatorDir = new File(new LocalRepository().getWorkflowsDir());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Map<String, List<WorkflowDesc>> categories = new HashMap<String, List<WorkflowDesc>>();

		try {
			if (operatorDir.isDirectory()) {
				File operators[] = operatorDir.listFiles();

				dBuilder = dbFactory.newDocumentBuilder();
				for (File op : operators) {
					if (op.isFile()) {
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						Element wf = (Element) d.getElementsByTagName(
								"rdfgears").item(0);

						Element meta = (Element) wf.getElementsByTagName(
								"metadata").item(0);
						String id = meta.getElementsByTagName("id").item(0)
								.getTextContent();
						String name = meta.getElementsByTagName("name").item(0)
								.getTextContent();

						if (meta.getElementsByTagName("category").getLength() > 0) {
							String cat = meta.getElementsByTagName("category")
									.item(0).getTextContent();
							if (cat.trim().length() > 0) {
								if (!categories.containsKey(cat)) {
									categories
											.put(cat,
													new ArrayList<ServicesUIHandler.WorkflowDesc>());
								}
								categories.get(cat).add(
										new WorkflowDesc(id, name));
							}
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return categories;
	}

	private class WorkflowDesc {
		private String id;
		private String name;

		public WorkflowDesc(String id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}
	}
}
