package nl.tudelft.rdfgears.tests;

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

import java.util.Collections;
import java.util.Iterator;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryLiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryURIValue;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.custom.MultiplicationFunction;
import nl.tudelft.rdfgears.rgl.function.sparql.SPARQLFunction;
import nl.tudelft.rdfgears.rgl.workflow.ConstantProcessor;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import static org.junit.Assert.*;

public class TestRGLFunctions {

	public static SPARQLFunction getConstructFunction(){
		/* create a constructFunction with typing definition */
    	SPARQLFunction function = new SPARQLFunction();
    	function.initialize(Collections.singletonMap("query", Data.movieConstructQuery));
    	function.requireInputType("graph1", GraphType.getInstance());
    	function.requireInputType("director", RDFType.getInstance());
    	return function;
	}

	public static SPARQLFunction getSelectFunction(){
		/* create a constructFunction with typing definition */
		SPARQLFunction function = new SPARQLFunction();
		function.initialize(Collections.singletonMap("query", Data.movieSelectQuery));
    	function.requireInputType("graph1", GraphType.getInstance());
    	return function;
	}

	public static SPARQLFunction getSelectNrOfStudentsFunction(){
		/* create a constructFunction with typing definition */
		String queryStr = "select ?univ ?nrOfStudents WHERE { GRAPH ?graph1 { ?univ <http://dbpedia.org/ontology/numberOfStudents> ?nrOfStudents } }";
		SPARQLFunction function = new SPARQLFunction();
		function.initialize(Collections.singletonMap("query", queryStr));
    	function.requireInputType("graph1", GraphType.getInstance());
    	return function;
	}


	@Test public void testStudentCount() {
		SPARQLFunction selectF = getSelectNrOfStudentsFunction();
		selectF.getOutputType();
		
		/* set input row */
		FieldMappedValueRow inputRow = new FieldMappedValueRow(selectF.getFieldIndexMap());
		inputRow.put("graph1", (new ConstantProcessor(Data.getGraphFromFile("./data/tu-delft-eindhoven.xml"))).getResultValue());
		
    	/* execute */
    	BagValue resultBag = (BagValue) selectF.execute(inputRow);
    	Iterator<RGLValue> resultIter = resultBag.iterator();
		System.out.println("TU records:");
    	
		int recordCount = 0;
    	while(resultIter.hasNext()){
    		RecordValue rec = (RecordValue) resultIter.next();
    		assertTrue(rec.get("univ")!=null);
    		assertTrue(rec.get("nrOfStudents")!=null);
    		System.out.println(rec);
    		recordCount++;
    	}
    	assertTrue("I should have 2 record, but I have "+recordCount, recordCount==2);
    	
		
	}
	
	
	@Test public void testMultiply() {
		RGLFunction multiply = new MultiplicationFunction();
		FieldMappedValueRow mulInputRow = new FieldMappedValueRow(multiply.getFieldIndexMap());
		mulInputRow.put(MultiplicationFunction.value1, Engine.getValueFactory().createLiteralTyped("12", XSDDatatype.XSDdecimal));
		mulInputRow.put(MultiplicationFunction.value2, Engine.getValueFactory().createLiteralTyped(new Double(0.5), XSDDatatype.XSDdouble));
		RGLValue result = multiply.execute(mulInputRow);
		assertTrue("Multiplication result be correct ", ((MemoryLiteralValue) result).getRDFNode().asLiteral().getDouble()==6.0);
	}
	
	
	@Test public void testSelectFunction() {
		/* create a constructFunction with typing definition */
		SPARQLFunction function = new SPARQLFunction();
		function.initialize(Collections.singletonMap("query", Data.dbpediaSelectQuery));
		
    	function.requireInputType("graph1", GraphType.getInstance());
    	
    	FieldMappedValueRow inputRow = new FieldMappedValueRow(function.getFieldIndexMap());
    	inputRow.put("graph1", Data.getGraphFromFile("./data/dbpedia.xml"));
    	
    	BagValue resultBag  = function.execute(inputRow).asBag();
    	Iterator<RGLValue> recordIter = resultBag.iterator();
    	int resultCount = 0;
    	while(recordIter.hasNext()){
    		RecordValue record = (RecordValue) recordIter.next();
    		assertTrue("Should contain 'director' field", record.get("director")!=null);
    		assertTrue("Should contain 'name' field", record.get("film")!=null);
    		assertTrue("Should contain 'label' field", record.get("label")!=null);
    		resultCount++;
    	}
    	assertTrue("Should have 100 results (query says LIMIT 100)", resultCount==100);
	}
	
	@Test public void testConstructFunction() {
		
		SPARQLFunction constructFunction = getConstructFunction();
		
		TypeRow inputTypeRow = new TypeRow();
    	inputTypeRow.put("graph1", GraphType.getInstance());
    	inputTypeRow.put("director", RDFType.getInstance());
    	
    	try {
			assertTrue("Construct processor must return a graph", constructFunction.getOutputType(inputTypeRow).isType(GraphType.getInstance()));
		} catch (WorkflowCheckingException e) {
			/* this is a problem */
			e.printStackTrace();
			assertTrue("Typing exception was thrown", false);
		}
		
		FieldMappedValueRow inputRow = new FieldMappedValueRow(constructFunction.getFieldIndexMap());
    	inputRow.put("graph1", Data.getGraphFromFile("./data/linkedmdb.xml"));
    	
    	MemoryURIValue directorURI = new MemoryURIValue("http://data.linkedmdb.org/resource/director/866");
    	inputRow.put("director", directorURI);
    	
    	GraphValue resultGraph = (GraphValue) constructFunction.execute(inputRow);
    	assertTrue("There should be 3 triples, not "+resultGraph.getModel().size(), resultGraph.getModel().size()==3);
    	
    }
}

