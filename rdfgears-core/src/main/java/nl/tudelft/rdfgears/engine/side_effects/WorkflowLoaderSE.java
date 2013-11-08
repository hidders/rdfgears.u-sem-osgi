package nl.tudelft.rdfgears.engine.side_effects;

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

import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import nl.tudelft.rdfgears.engine.Config;
import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.exception.CircularWorkflowException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.rgl.workflow.WorkflowNode;
import nl.tudelft.wis.usem.plugin.admin.PluginAdminFactory;

import org.w3c.dom.NodeList;

public class WorkflowLoaderSE extends WorkflowLoader {

    public static Workflow loadWorkflow(String workflowId)
	    throws WorkflowLoadingException {
	Engine.init((Config) null);
	if (workflowId == null || workflowId.equals("")) {
	    throw new WorkflowLoadingException(
		    "No workflow was specified to load.");
	}

	// Make sure any plugin changes are applied
	PluginAdminFactory.refresh(true);

	WorkflowLoaderSE wLoader = new WorkflowLoaderSE(workflowId);
	return wLoader.getWorkflow();
    }

    public WorkflowLoaderSE(String workflowId) throws WorkflowLoadingException {
	super(workflowId);
    }

    protected WorkflowNode load() throws CircularWorkflowException,
	    WorkflowLoadingException {
	WorkflowNode output = super.load();

	try {
	    Set<WorkflowNode> nodesWithSideEffects = getSideEffectProcessors();

	    return new WorkflowNodeSE(output, nodesWithSideEffects);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new WorkflowLoadingException(e.getMessage());
	}
    }

    private Set<WorkflowNode> getSideEffectProcessors() throws Exception {
	XPathFactory xPathfactory = XPathFactory.newInstance();
	XPath xpath = xPathfactory.newXPath();
	XPathExpression expr = xpath
		.compile("//processor[function/config[@param = 'sideEffects'] = 'true']/@id");

	NodeList nl = (NodeList) expr.evaluate(getXmlDoc(), XPathConstants.NODESET);

	Set<WorkflowNode> nodesWithSideEffects = new HashSet<WorkflowNode>();

	for (int i = 0; i < nl.getLength(); i++) {
	    String nodeId = nl.item(i).getNodeValue();
	    recursivelyLinkInputs(nodeId);
	    nodesWithSideEffects.add(getNode(nodeId));
	}

	return nodesWithSideEffects;
    }

}
