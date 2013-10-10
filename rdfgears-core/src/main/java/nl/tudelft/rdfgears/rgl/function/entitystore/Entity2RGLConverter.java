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


import java.util.HashSet;
import java.util.List;
import java.util.Map;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.util.row.FieldIndexArrayMap;

public class Entity2RGLConverter {

    public RGLValue convert(Map<String, Object> m) {
	removeMetadata(m);
	FieldIndexArrayMap fiMap = new FieldIndexArrayMap(m.keySet());
	ModifiableRecord rec = new ModifiableRecord(fiMap);

	for (String key : m.keySet()) {
	    Object obj = m.get(key);

	    if (obj instanceof List<?>) {
		List<Object> bag = (List<Object>) obj;
		rec.put(key, convert(bag));

	    } else if (obj instanceof Map<?, ?>) {
		Map<String, Object> record = (Map<String, Object>) obj;
		rec.put(key, convert(record));
	    } else {
		rec.put(key, ValueFactory.createLiteralPlain(m.get(key)
			.toString(), null));
	    }

	}

	return rec;
    }

    private void removeMetadata(Map<String, Object> m) {
	for (String field : new HashSet<String>(m.keySet())) {
	    if (field.startsWith("$") && field.endsWith("$")) {
		m.remove(field);
	    }
	}

    }

    private RGLValue convert(List<Object> bag) {
	List<RGLValue> backingList = ValueFactory.createBagBackingList();

	for (Object o : bag) {
	    if (o instanceof Map<?, ?>) {
		Map<String, Object> record = (Map<String, Object>) o;
		backingList.add(convert(record));
	    }
	}

	return new ListBackedBagValue(backingList);
    }
}
