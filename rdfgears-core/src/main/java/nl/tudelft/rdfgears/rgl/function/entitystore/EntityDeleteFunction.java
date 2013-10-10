package nl.tudelft.rdfgears.rgl.function.entitystore;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.FieldIndexArrayMap;
import nl.tudelft.rdfgears.util.row.ValueRow;
import nl.tudelft.wis.datamanagement.backend.persistance.EntityPersister;

/**
 * A function that can execute SPARQL SELECT/CONSTRUCT queries.
 * 
 * The query is configured with initialize()
 * 
 * 
 * @author Eric Feliksik
 * 
 */
public class EntityDeleteFunction extends SimplyTypedRGLFunction {

    private String query;

    public void initialize(Map<String, String> config) {
	query = config.get("query");

	/*
	 * configure the required inputs based on the ';' separated list of
	 * prebound variable names
	 */
	String bindVarStr = config.get("bindVariables");
	if (bindVarStr != null) {
	    String[] split = bindVarStr.split(";");
	    for (int i = 0; i < split.length; i++) {
		if (split[i].length() > 0)
		    requireInputType(split[i], RDFType.getInstance());
	    }
	}
    }

    @Override
    public RGLType getOutputType() {
	return RDFType.getInstance();
    }

    /**
     * The values in the inputRow will be bound to the variables in the query.
     * 
     * This is also the way to pass models; you can use a section GRAPH $mygraph
     * { ... } .
     * 
     * Then make sure the inputRow contains a field 'mygraph' with a RGL Graph
     * value.
     * 
     */
    @Override
    public RGLValue simpleExecute(ValueRow inputRow) {
	try {
	    Map<String, String> params = new HashMap<String, String>();
	    for (String key : inputRow.getRange()) {
		params.put(key, inputRow.get(key).asLiteral().getValueString());
	    }

	    new EntityPersister().delete(query, params);

	    return ValueFactory.createTrue();

	} catch (Throwable t) {
	    t.printStackTrace();
	    return ValueFactory.createFalse();
	}
    }

}
