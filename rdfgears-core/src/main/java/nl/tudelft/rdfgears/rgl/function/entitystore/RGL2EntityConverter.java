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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.util.row.ValueRow;

public class RGL2EntityConverter {
    public Map<String, Object> convert(ValueRow inputRow) {
	Map<String, Object> object = new HashMap<String, Object>();

	for (String p : inputRow.getRange()) {
	    RGLValue rglValue = inputRow.get(p);
	    if (rglValue.isBag()) {
		object.put(p, parse(rglValue.asBag()));
	    } else if (rglValue.isRecord()) {
		object.put(p, convert(rglValue.asRecord()));
	    } else if (rglValue.isBoolean()) {
		object.put(p, parse(rglValue.asBoolean()));
	    } else if (rglValue.isGraph()) {
		object.put(p, parse(rglValue.asGraph()));
	    } else if (rglValue.isLiteral()) {
		object.put(p, parse(rglValue.asLiteral()));
	    }
	}

	return object;
    }

    private Object parse(LiteralValue asLiteral) {
	return asLiteral.getValueString();
    }

    private Object parse(GraphValue asGraph) {
	return asGraph.getURI().toString();
    }

    private Object parse(BooleanValue asBoolean) {
	return asBoolean.isTrue();
    }

    private List parse(BagValue asBag) {
	List<Object> result = new ArrayList<Object>();

	for (RGLValue object : asBag) {
	    if (object.isRecord()) {
		result.add(convert(object.asRecord()));
	    } else {
		throw new IllegalArgumentException();
	    }
	}
	return result;
    }

    public Object convertRGLValue(RGLValue rglValue) {
	if (rglValue.isBag()) {
	    return parse(rglValue.asBag());
	} else if (rglValue.isRecord()) {
	    return convert(rglValue.asRecord());
	} else if (rglValue.isBoolean()) {
	    return parse(rglValue.asBoolean());
	} else if (rglValue.isGraph()) {
	    return parse(rglValue.asGraph());
	} else if (rglValue.isLiteral()) {
	    return parse(rglValue.asLiteral());
	}
	
	return null;
    }
}
