package nl.rdfgears.tudelft.webservice.requests;

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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.rdfgears.tudelft.webservice.requests.WorkflowRequest.OutputFormat;
import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.Optimizer;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLNull;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.ValueXMLSerializer;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ImRealXMLSerializer;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ValueSerializer;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;
import nl.tudelft.rdfgears.util.row.HashValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;

public abstract class WorkflowExecutionRequest extends WorkflowRequest {

	public WorkflowExecutionRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			WorkflowLoadingException {
		super(request, response);

	}

	protected HashValueRow valueRow; /* workflow input values */
	protected TypeRow typeRow; /* workflow input types */
	protected RGLType returnType;

	/**
	 * set the typeRow and valueRow
	 */
	public abstract void configureWorkflowInputs();

	/**
	 * Typecheck, configure the returnType and return it.
	 * 
	 * @return
	 * @throws WorkflowCheckingException
	 */
	public RGLType typeCheck() throws WorkflowCheckingException {
		if (typeRow == null) {
			configureWorkflowInputs();
		}
		returnType = Engine.typeCheck(workflow, typeRow);
		workflow = (new Optimizer()).optimize(workflow, false);
		return returnType;
	}

	/**
	 * Get the result type, as determined by typechecking. Returns null if
	 * typechecking failed or was not yet performed.
	 * 
	 * @return
	 */
	public RGLType getResultType() {
		return returnType;
	}

	public RGLValue getResult() {
		assert (returnType != null); // was set by typechecking
		if (valueRow == null) {
			throw new IllegalStateException(
					"You must first typecheck the workflow before executing it");
		}
		try {
			workflow = (new Optimizer()).optimize(workflow, false);
		} catch (WorkflowCheckingException e) {
			// Hmm we already did typchecking, didn't we??
			assert (false);
		}
		return workflow.execute(valueRow);
	}

	public abstract void execute() throws IOException;

	public OutputFormat getOutputFormat() {
		String format = request.getParameter("-format");
		if (format == null || format.equals("rdf")) {
			return OutputFormat.RDF_XML;
		} else if (format.equals("rgl")) {
			return OutputFormat.RGL_XML;
		} else if (format.equals("sparql-result")) {
			return OutputFormat.SPARQL_RESULT;
		} else {
			return OutputFormat.RDF_XML;
		}
	}

	public ValueSerializer getSerializer(RGLType returnType, OutputStream stream) {
		OutputFormat format = getOutputFormat();
		if (format == OutputFormat.RDF_XML && returnType.isGraphType()) {
			return new ImRealXMLSerializer(returnType, stream); // will
																// autodetect,
																// so works as
																// the options
																// are mutually
																// exclusive
		} else if (format == OutputFormat.SPARQL_RESULT
				&& returnType.isBagType()) {
			// will autodetect, so works as the options are mutually exclusive
			// may still fall back to RGL/XML as we didn't check whether it's
			// bag of record of RDFVal.
			return new ImRealXMLSerializer(returnType, stream);
		} else {
			/* conditions not met, use normal RGL/XML serializer */
			return new ValueXMLSerializer(stream);
		}
	}

	//
	// public void execute() throws IOException{
	// OutputStream out = null;
	// if (out==null){
	// out = response.getOutputStream();
	// response.setContentType("application/xml;charset=utf-8");
	// response.setStatus(HttpServletResponse.SC_OK);
	//
	//
	// }
	//
	//
	// ValueSerializer serializer = new ImRealXMLSerializer(returnType, out);
	// serializer.serialize(workflow.execute(valueRow));
	//
	// }

}
