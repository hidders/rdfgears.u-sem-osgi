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
import java.util.Map;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BooleanType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;
import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.api.entitydef.Field;
import nl.tudelft.wis.datamanagement.backend.datatypes.DataTypesPersister;
import nl.tudelft.wis.datamanagement.backend.persistance.EntityPersister;

public class EntityStoreFunction extends SimplyTypedRGLFunction {

    private String entity;

    @Override
    public void initialize(Map<String, String> config) {
	super.initialize(config);

	this.entity = config.get("entity");

	DataTypesPersister entityDescriptiorManager = new DataTypesPersister();
	EntityDescriptor byName;
	try {
	    byName = entityDescriptiorManager.getByName(entity);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return;
	}

	for (Field p : byName.getEntity().getProperties()) {
	    requireInput(p.getName());
	}
    }

    @Override
    public RGLType getOutputType() {
	return BooleanType.getInstance();
    }

    @Override
    public RGLValue simpleExecute(ValueRow inputRow) {
	EntityPersister entityDescriptiorManager = new EntityPersister();

	Map<String, Object> object = new RGL2EntityConverter().convert(inputRow);
	try {
	    entityDescriptiorManager.store(entity, object);
	} catch (Exception e) {
	    e.printStackTrace();
	    return ValueFactory.createFalse();
	}

	return ValueFactory.createTrue();
    }

}
