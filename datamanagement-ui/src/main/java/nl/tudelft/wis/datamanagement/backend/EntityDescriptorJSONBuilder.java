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


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nl.tudelft.wis.datamanagement.api.entitydef.BasicType;
import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.api.entitydef.Field;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BagType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.TupleType;

public class EntityDescriptorJSONBuilder {

    public JSONObject parse(EntityDescriptor obj) {
	JSONObject result = new JSONObject();

	result.accumulate("name", obj.getName());
	result.accumulate("title", obj.getName());
	result.accumulate("readAccess", obj.isReadAccess());
	result.accumulate("writeAccess", obj.isWriteAccess());
	result.accumulate("description", obj.getDescription());

	result.accumulate("children", getChildren(obj.getEntity()));

	return result;
    }

    private JSONArray getChildren(TupleType entity) {
	JSONArray children = new JSONArray();

	if (entity.getProperties() == null)
	    return children;

	for (Field obj : entity.getProperties()) {
	    JSONObject prop = new JSONObject();

	    prop.accumulate("name", obj.getName());
	    prop.accumulate("title", obj.getName());

	    if (obj.getValue() instanceof BasicType) {
		prop.accumulate("rglType",
			((BasicType) obj.getValue()).getRGLType());
	    } else if (obj.getValue() instanceof BagType) {
		BagType entprop = (BagType) obj.getValue();
		prop.accumulate("isFolder", true);
		prop.accumulate("isMultiple", true);
		prop.accumulate("children", getChildren(entprop.getTuple()));
	    } else if (obj.getValue() instanceof TupleType) {
		TupleType entprop = (TupleType) obj.getValue();
		prop.accumulate("isFolder", true);
		prop.accumulate("isMultiple", false);
		prop.accumulate("children", getChildren(entprop.getTuple()));
	    }

	    children.add(prop);
	}

	return children;
    }

}
