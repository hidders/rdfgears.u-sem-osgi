package nl.tudelft.rdfgears.engine;

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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tudelft.rdfgears.plugin.FunctionDescriptor;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.CircularWorkflowException;
import nl.tudelft.rdfgears.rgl.exception.FunctionConfigurationException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.core.BagCategorize;
import nl.tudelft.rdfgears.rgl.function.core.BagFlatten;
import nl.tudelft.rdfgears.rgl.function.core.BagGroup;
import nl.tudelft.rdfgears.rgl.function.core.BagSingleton;
import nl.tudelft.rdfgears.rgl.function.core.BagUnion;
import nl.tudelft.rdfgears.rgl.function.core.ComparatorFunction;
import nl.tudelft.rdfgears.rgl.function.core.FilterFunction;
import nl.tudelft.rdfgears.rgl.function.core.IfThenElseFunction;
import nl.tudelft.rdfgears.rgl.function.core.RecordCreate;
import nl.tudelft.rdfgears.rgl.function.core.RecordJoin;
import nl.tudelft.rdfgears.rgl.function.core.RecordProject;
import nl.tudelft.rdfgears.rgl.function.core.RecordUnion;
import nl.tudelft.rdfgears.rgl.function.entitystore.EntityDeleteFunction;
import nl.tudelft.rdfgears.rgl.function.entitystore.EntityInsertChildFunction;
import nl.tudelft.rdfgears.rgl.function.entitystore.EntityQueryFunction;
import nl.tudelft.rdfgears.rgl.function.entitystore.EntityStoreFunction;
import nl.tudelft.rdfgears.rgl.function.entitystore.EntityUpdateFunction;
import nl.tudelft.rdfgears.rgl.function.obsolete.BagTopFunction;
import nl.tudelft.rdfgears.rgl.function.sparql.SPARQLFunction;
import nl.tudelft.rdfgears.rgl.workflow.ConstantProcessor;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;
import nl.tudelft.rdfgears.rgl.workflow.InputPort;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.rgl.workflow.WorkflowNode;
import nl.tudelft.rdfgears.util.ValueParser;
import nl.tudelft.rdfgears.util.XMLUtil;
import nl.tudelft.wis.usem.plugin.access_management.PluginAccessManagerFactory;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WorkflowLoader {

	private static final WorkflowNode FunctionProcessor = null;

	public static Workflow loadWorkflow(String workflowId)
			throws WorkflowLoadingException {
		Engine.init((Config) null);
		if (workflowId == null || workflowId.equals("")) {
			throw new WorkflowLoadingException(
					"No workflow was specified to load.");
		}
		
		//Make sure any plugin changes are applied 
		PluginAccessManagerFactory.getPluginManager().refresh();
		
		WorkflowLoader wLoader = new WorkflowLoader(workflowId);
		return wLoader.getWorkflow();
	}

	/**
	 * CONTINUE HERE, RECONSIDER APPROACH
	 */
	private String workflowId;
	private String usedXMLFile = "<no file>";

	private Document xmlDoc;
	private Set<WorkflowNode> inputsConfigured = new HashSet<WorkflowNode>();

	/* map of node id's to nodes; these do not include WorkflowInputNodes */
	private Map<String, WorkflowNode> nodeMap = new HashMap<String, WorkflowNode>();

	/* map of workflow input id's to the Node input ports */
	private Map<String, InputPort> inputMap = new HashMap<String, InputPort>();
	private Workflow workflow; // the loaded, complete workflow element
	private Workflow buildingWorkflow; // the workflow that is being built

	/**
	 * workflowId should give sufficient info to find the file; filename is
	 * based on id.
	 * 
	 * @param workflowId
	 * @throws WorkflowLoadingException
	 */
	public WorkflowLoader(String workflowId) throws WorkflowLoadingException {
		this.workflowId = workflowId;

		try {

			WorkflowNode output = load();
			setWorkflow(output);
		} catch (WorkflowLoadingException e) {
			e.setWorkflowName(workflowId);
			throw (e);
			//
			// String origMsg = e.getClass().getName() + ( e.getMessage()!=null
			// ? " "+e.getMessage() : "");
			// RuntimeException ex = new
			// RuntimeException("Loading of the XML file "+usedXMLFile+" failed, does it comply to the DTD? Error is: \n"+origMsg);
			// //
			// // if (true){
			// // Engine.getLogger().error(ex.getMessage());
			// // Engine.getLogger().error("Exiting with error status.");
			// // System.exit(-1);
			// // }
			// ex.setStackTrace(e.getStackTrace());
			// throw ex;
		}

	}

	public Workflow getWorkflow() {
		return this.workflow;
	}

	protected WorkflowNode load() throws CircularWorkflowException,
			WorkflowLoadingException {
		loadWorkflowXMLDocument();
		buildingWorkflow = new Workflow();
		String outputNodeId = getNetworkElement().getAttribute("output");

		try {
			recursivelyLinkInputs(outputNodeId);
		} catch (java.lang.StackOverflowError e) {
			throw new CircularWorkflowException(
					"A circular reference is detected via node " + outputNodeId);
		}

		/**
		 * it is possible that there is an input specified in the XML, but it is
		 * not connected to the workflow output via processors. Add it anyway,
		 * as some typing properties require an input to be available
		 */
		NodeList nodeList = getWorkflowInputListElement().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			if (item instanceof Element) {
				Element e = (Element) item;
				String workflowInputName = e.getAttribute("name");
				if (!workflowInputName.equals(""))
					buildingWorkflow.requireInput(workflowInputName); // was
																		// probably
																		// already
																		// added
			}
		}
		
		return getNode(outputNodeId);
	}

	protected void setWorkflow(WorkflowNode outputProcessor)
		throws WorkflowLoadingException {
	    buildingWorkflow.setOutputProcessor(outputProcessor);
	    // AbstractProcessor outputProducer =
	    // createProcessorFromElement(outputProcElem);
	    // workflow.setOutputProducer(outputProducer);

	    /* we made it, no exceptions */
	    workflow = buildingWorkflow;

	    workflow.setID(this.workflowId);

	    workflow.setName(getWorkflowName());
	    workflow.setDescription(getWorkflowDescription());
	}

	/**
	 * Given the node id, set it's inputs
	 * 
	 * @param nodeId
	 * @throws WorkflowLoadingException
	 */
	protected void recursivelyLinkInputs(String nodeId)
			throws WorkflowLoadingException {
		WorkflowNode node = getNode(nodeId);
		if (inputsConfigured.contains(node)) {
			return; // already linked
		}

		Element nodeElem = getProcessorElement(nodeId);

		if (node instanceof FunctionProcessor) {
			FunctionProcessor fnode = (FunctionProcessor) node;
			for (Element inputElem : XMLUtil.getSubElementByName(nodeElem,
					"inputPort")) {
				Element sourceElem = XMLUtil.getFirstSubElementByName(
						inputElem, "source");
				String inputName = inputElem.getAttribute("name");
				if (sourceElem == null) {
					throw new RuntimeException(
							"There is no <source> section for input '"
									+ inputName + "' of processor " + nodeId);
				}

				String sourceNodeId = sourceElem.getAttribute("processor");
				String workflowInputName = sourceElem
						.getAttribute("workflowInputPort");
				InputPort destinationPort = fnode.getPort(inputName);

				if (destinationPort == null) {
					for (String nnn : fnode.getFunction()
							.getRequiredInputNames()) {
						System.out.println("Requires name " + nnn);
					}

					throw new WorkflowLoadingException(
							"The processor '"
									+ nodeId
									+ "' specifies an <inputPort name=\""
									+ inputName
									+ "\"> , but there is no such input required for the function "
									+ fnode.getFunction().getFullName());
				}
				if (!sourceNodeId.equals("")) {
					WorkflowNode sourceNode = getNode(sourceNodeId);
					destinationPort.setInputProcessor(sourceNode);

					/* recursively link inputs of this node */
					recursivelyLinkInputs(sourceNodeId);

				} else if (!workflowInputName.equals("")) {
					/* must read from workflowInputPort */
					buildingWorkflow.addInputReader(workflowInputName,
							destinationPort);
				} else {
					throw new RuntimeException(
							"There is no complete <source> tag for port '"
									+ inputName + "' of processor '" + nodeId
									+ "'.");
				}
			}

		} else {
			/* nothing to be done */
		}

		inputsConfigured.add(node);
	}

	/**
	 * get the node object with given id; id is valid in this workflow XML
	 * 
	 * @param id
	 * @return
	 * @throws WorkflowLoadingException
	 */
	protected WorkflowNode getNode(String id) throws WorkflowLoadingException {
		assert (id != null);
		WorkflowNode node = nodeMap.get(id);
		if (node == null) {
			try {
				node = loadNode(id);
			} catch (Exception e) {

				String origMsg = e.getClass().getName()
						+ (e.getMessage() != null ? " " + e.getMessage() : "");
				RuntimeException ex = new RuntimeException(
						"The section for node '" + id + "' is not correct: "
								+ origMsg);

				ex.setStackTrace(e.getStackTrace());
				throw ex;
			}
			nodeMap.put(id, node);
		}

		if (node == null)
			throw new WorkflowLoadingException("The processor with id '" + id
					+ "' is referenced, but not defined. ");
		return node;
	}

	private Element getNetworkElement() {
		Element workflowElem = XMLUtil.getFirstSubElementByName(
				xmlDoc.getDocumentElement(), "workflow");
		return XMLUtil.getFirstSubElementByName(workflowElem, "network");
	}

	private Element getWorkflowInputListElement() {
		Element workflowElem = XMLUtil.getFirstSubElementByName(
				xmlDoc.getDocumentElement(), "workflow");
		return XMLUtil.getFirstSubElementByName(workflowElem,
				"workflowInputList");
	}
	
	public Element getProcessorElement(String nodeId) {
		List<Element> procElemList = XMLUtil.getSubElementByName(
				getNetworkElement(), "processor");
		for (Element procElem : procElemList) {
			if (nodeId.equals(procElem.getAttribute("id")))
				return procElem;
		}
		return null;
	}
	
	private String getWorkflowDescription() {
		Element workflowElem = XMLUtil.getFirstSubElementByName(
				xmlDoc.getDocumentElement(), "metadata");
		Element descrElem =  XMLUtil.getFirstSubElementByName(workflowElem,
				"description");
		if(descrElem != null){
			return descrElem.getTextContent();
		}
		
		return null;
	}
	
	private String getWorkflowName() {
		Element workflowElem = XMLUtil.getFirstSubElementByName(
				xmlDoc.getDocumentElement(), "metadata");
		Element nameElem =  XMLUtil.getFirstSubElementByName(workflowElem,
				"name");
		if(nameElem != null){
			return nameElem.getTextContent();
		}
		
		return null;
	}

	/**
	 * create a processor element from the XML. Mark iteration ports and set
	 * Function; it doesn't connect input/output ports to other processors.
	 * 
	 * @param nodeId
	 * @return
	 * @throws WorkflowLoadingException
	 */
	private WorkflowNode loadNode(String nodeId)
			throws WorkflowLoadingException {
		assert (nodeId != null);

		Element procElem = getProcessorElement(nodeId);
		if (procElem == null)
			throw new RuntimeException("Processor with id '" + nodeId
					+ "' is referenced, but it is not defined in the workflow");

		Element funcElem = XMLUtil.getFirstSubElementByName(procElem,
				"function");
		String type = funcElem.getAttribute("type");

		Map<String, String> configMap = new HashMap<String, String>();

		List<Element> configList = XMLUtil.getSubElementByName(funcElem,
				"config");
		if (configList.size() > 0) {
			for (Element configElement : configList) {
				String configKey = configElement.getAttribute("param");
				String configValue = configElement.getTextContent();
				configMap.put(configKey, configValue);
			}
		}

		/**
		 * TODO: Reconsider this if/then/else/else/else/else statement. It's
		 * ugly.
		 */

		/* either processor or function will be set by if/then/else switch */
		WorkflowNode processor = null;
		RGLFunction function = null;

		if (type.equals("workflow")) {
			String workflowId = getXMLFunctionParameter(funcElem, "workflow-id");
			function = WorkflowLoader.loadWorkflow(workflowId);
		} else if (type.equals("constant")) {
			RGLValue value;
			try {
				value = ValueParser.parseSimpleRGLValue(configMap.get("value"));
			} catch (ParseException e) {
				throw new RuntimeException("Cannot parse your value: "
						+ configMap.get("value"));
			}

			processor = new ConstantProcessor(value, nodeId);
		} else if (type.equals("comparator")) {
			function = new ComparatorFunction();
		} else if (type.equals("filter")) {
			function = new FilterFunction();
		} else {
			String functionName;
			if (type.equals("custom-java")) {
				functionName = getXMLFunctionParameter(funcElem,
						"implementation");
			} else {
				functionName = type;
			}
			function = instantiateFunction(functionName);
		}

		if (processor == null) {
			/* make a FunctionProcessor */

			assert (function != null);
			try {
				function.initialize(configMap);
			} catch (FunctionConfigurationException e) {
				e.setProcessor(processor);
				throw (e);
			} catch (NullPointerException e) {
				System.out
						.println("The implementation of the initialize() function in "
								+ function.getClass().getCanonicalName()
								+ " threw a NullPointerException, which probably means it is not resilient to missing "
								+ "config parameters. Improve the function implementation and/or repair the XML file to specify the required parameter.");
				throw (e);
			}

			FunctionProcessor funcProc = new FunctionProcessor(function, nodeId);
			configureIteration(funcProc, procElem);
			processor = funcProc;
		} else {
			/* it is a ConstantProcessor */
		}

		return processor;
	}

	/**
	 * mark the input ports for iteration, as specified in the processor XML
	 * Element
	 * 
	 * @param fproc
	 * @param processorElement
	 */
	private void configureIteration(FunctionProcessor fproc,
			Element processorElement) {
		List<Element> inputElemList = XMLUtil.getSubElementByName(
				processorElement, "inputPort");
		for (Element inputElem : inputElemList) {
			if ("true".equals(inputElem.getAttribute("iterate"))) {
				String portName = inputElem.getAttribute("name");
				fproc.getPort(portName).markIteration();
			}
		}
	}

	/**
	 * Initialize the XML DOM tree by reading the XML file from disk.
	 * 
	 * @throws SAXException
	 * @throws WorkflowLoadingException
	 */
	public void loadWorkflowXMLDocument() throws WorkflowLoadingException {
		String path = Engine.getConfig().getWorkflowPath();
		String filePath = path + workflowId + ".xml";
		try {

			FileReader fileReader = new FileReader(filePath);
			InputSource input = new InputSource(fileReader);
			DOMParser parser = new DOMParser();
			parser.parse(input);
			xmlDoc = parser.getDocument();
			usedXMLFile = filePath; // well, used... at least we TRY to use it
		} catch (FileNotFoundException e) {
			throw new WorkflowLoadingException("Workflow XML file for '"
					+ workflowId + "' not found in " + filePath);
		} catch (SAXException e) {
			throw new WorkflowLoadingException("Cannot parse XML file '"
					+ usedXMLFile + "': " + e.getMessage());
			// e.printStackTrace();
			// System.exit(0);
		} catch (IOException e) {
			throw new WorkflowLoadingException("Cannot load XML file '"
					+ usedXMLFile + "': " + e.getMessage());
		}

	}

	/**
	 * classmap to contain classnames as keys, but also the pre-known keys (e.g.
	 * "nrc-record-project")
	 */
	private static Map<String, Class<?>> functionClassMap = loadFunctionClassMap();
	private static ClassLoader classLoader = ClassLoader.getSystemClassLoader();

	public void setClassLoader(ClassLoader loader) {
		classLoader = loader;
	}

	private static Map<String, Class<?>> loadFunctionClassMap() {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();

		/* record functions */
		map.put("record-project", RecordProject.class);
		map.put("record-create", RecordCreate.class);
		map.put("record-union", RecordUnion.class);
		map.put("record-join", RecordJoin.class);

		/* bag functions */
		map.put("bag-flatten", BagFlatten.class);
		map.put("bag-singleton", BagSingleton.class);
		map.put("bag-union", BagUnion.class);
		map.put("bag-categorize", BagCategorize.class);
		map.put("bag-groupby", BagGroup.class);

		/*
		 * should this really be here? Currently function initialize()'ability
		 * requires a custom GUI node, which requires it to be pre-known here.
		 * That not nice!
		 */
		map.put("select-top-scorer", BagTopFunction.class);
		
		/*
		 * Two different versions are not really needed, as they are both
		 * implemented in SPARQLFunction. But in the UI they are different.
		 * 
		 * SPARQLFunction is automatically querying an endpoint if the
		 * "endpointURI" parameter is configured. Only restriction is that in
		 * that case it doesn't accept any prebind variables yet...
		 */
		map.put("sparql", SPARQLFunction.class);
		map.put("sparql-endpoint", SPARQLFunction.class);
		map.put("storeEntity", EntityStoreFunction.class);
		map.put("entityQuery", EntityQueryFunction.class);
		map.put("entityDelete", EntityDeleteFunction.class);
		map.put("entityUpdate", EntityUpdateFunction.class);
		map.put("entityInsertChild", EntityInsertChildFunction.class);
		map.put("if", IfThenElseFunction.class);
		return map;
	}

	public static Class<?> loadFunctionClass(String funcName) {
		Class<?> theClass = functionClassMap.get(funcName);
		if (theClass == null) {
			/* not known as a default function, then it must be a classname */

			String className = funcName;
			try { // WorkflowĹoaders's classloader can access the jars in a
					// jetty webapp.
				theClass = WorkflowLoader.class.getClassLoader().loadClass(
						className);
			} catch (ClassNotFoundException e) {

				theClass = loadFunctionFromPlugins(className);

				if (theClass == null) {
					String errorMsg = "Cannot load function '"
							+ className
							+ "'. It is not a predefined function, and I cannot find such a class. You may be missing the necessary jar file. ";
					Engine.getLogger().error(errorMsg);
					throw new RuntimeException(errorMsg);
				}
			}
		}

		return theClass;
	}

	private static Class<?> loadFunctionFromPlugins(String className) {
		List<FunctionDescriptor> services = PluginAccessManagerFactory.getPluginManager()
				.getServices(FunctionDescriptor.class);

		for (FunctionDescriptor fd : services) {
			if (fd.getFunctionClass().getName().equals(className)) {
				return fd.getFunctionClass();
			}
		}

		return null;
	}
	
	public static RGLFunction instantiateFunction(String funcName)
			throws WorkflowLoadingException {
		if (funcName.startsWith("workflow:")) {
			String workflowId = funcName.substring("workflow:".length());
			return WorkflowLoader.loadWorkflow(workflowId);
		} else {
			// must be a predefined nrc function, or a class
			try {
				Class<?> rglClass = loadFunctionClass(funcName);
				RGLFunction instance = (RGLFunction) rglClass.newInstance();
				return instance;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot instantiate class  " + funcName
						+ ", did you define a no-argument constructor? ");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot load class  " + funcName);
				e.printStackTrace();

			}
		}

		return null;
	}

	public static String getXMLFunctionParameter(Element functionElem,
			String parameterName) {
		NodeList childNodes = functionElem.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item instanceof org.w3c.dom.Element) {
				Element cfgElem = (Element) item;
				if (parameterName.equals(cfgElem.getAttribute("param"))) {
					return cfgElem.getTextContent();
				}
			}
		}
		return null; // no parameter with that name!
	}

	public static List<String> getInputList(Element procElem) {
		List<String> inputNameList = new ArrayList<String>();
		List<Element> inputPortList = XMLUtil.getSubElementByName(procElem,
				"inputPort");

		for (Element inputPortElem : inputPortList) {
			inputNameList.add(inputPortElem.getAttribute("name"));
		}
		return inputNameList;
	}
	
	protected Document getXmlDoc() {
	    return xmlDoc;
	}
}
