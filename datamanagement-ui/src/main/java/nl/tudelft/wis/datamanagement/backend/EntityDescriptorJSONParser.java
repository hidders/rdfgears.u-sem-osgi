package nl.tudelft.wis.datamanagement.backend;

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
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.api.entitydef.Field;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BagType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BasicTypeFacory;
import nl.tudelft.wis.datamanagement.api.entitydef.types.TupleType;

public class EntityDescriptorJSONParser {

    public EntityDescriptor parse(JSONObject obj) {
	EntityDescriptor entityDescriptor = new EntityDescriptor();

	entityDescriptor.setName(obj.getString("name"));
	entityDescriptor.setReadAccess("true".equals(obj
		.getString("readAccess")));
	entityDescriptor.setWriteAccess("true".equals(obj
		.getString("writeAccess")));

	entityDescriptor.setDescription(obj.getString("description"));

	entityDescriptor.setEntity(parseEntity(obj.getJSONArray("children")));

	return entityDescriptor;
    }

    private TupleType parseEntity(JSONArray jsonArray) {
	TupleType entity = new TupleType();
	List<Field> props = new ArrayList<Field>();
	entity.setProperties(props);

	if (jsonArray == null)
	    return entity;

	for (Object obj : jsonArray) {
	    JSONObject prop = (JSONObject) obj;

	    if (prop.containsKey("isFolder") && prop.getBoolean("isFolder")) {
		Field property = new Field();
		props.add(property);

		property.setName(prop.getString("name"));

		JSONArray children = null;
		if (prop.containsKey("children")) {
		    children = prop.getJSONArray("children");
		}

		if ("true".equals(prop.getString("isMultiple")))
		    property.setValue(new BagType(parseEntity(children)));
		else {
		    property.setValue(parseEntity(children));
		}

	    } else {
		Field property = new Field();
		props.add(property);

		property.setName(prop.getString("name"));
		property.setValue(BasicTypeFacory.typeForName(prop
			.getString("rglType")));
	    }

	}
	return entity;
    }

}
