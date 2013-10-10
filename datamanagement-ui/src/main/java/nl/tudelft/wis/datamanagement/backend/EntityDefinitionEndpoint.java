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


import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.backend.datatypes.DataTypesPersister;
import nl.tudelft.wis.datamanagement.backend.db.DatabaseManager;
import nl.tudelft.wis.datamanagement.backend.hbm.HBMPersister;

public class EntityDefinitionEndpoint {

    public boolean save(JSONObject input) {
	try {
	    EntityDescriptor entityDescriptor = new EntityDescriptorJSONParser()
		    .parse(input);

	    new DataTypesFacade().storeDataType(entityDescriptor);

	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}

	return true;
    }

    public JSONObject read(String name) {
	try {
	    EntityDescriptor descriptor = new DataTypesPersister()
		    .getByName(name);

	    return new EntityDescriptorJSONBuilder().parse(descriptor);

	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public boolean delete(String name) {
	try {
	    new DataTypesFacade().removeDataType(name);
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}

	return true;
    }

    public static void main(String[] args) {
	String data = "{\"name\":\"test\",\"description\":\"test\",\"readAccess\":false,\"writeAccess\":true,\"entities\":[{\"title\":\"Propety\",\"key\":\"_2\",\"isFolder\":false,\"isLazy\":false,\"tooltip\":null,\"href\":null,\"icon\":null,\"addClass\":null,\"noLink\":false,\"activate\":false,\"focus\":false,\"expand\":false,\"select\":false,\"hideCheckbox\":false,\"unselectable\":false,\"name\":\"Property\",\"rglType\":\"double\"},{\"title\":\"PropetyMult\",\"key\":\"_3\",\"isFolder\":true,\"isLazy\":false,\"tooltip\":null,\"href\":null,\"icon\":null,\"addClass\":null,\"noLink\":false,\"activate\":false,\"focus\":false,\"expand\":false,\"select\":false,\"hideCheckbox\":false,\"unselectable\":false,\"name\":\"PropetyMult\",\"rglType\":\"double\",\"isMultiple\":true}]}";
	JSONObject jsonObj = (JSONObject) JSONSerializer.toJSON(data);

	new EntityDefinitionEndpoint().save(jsonObj);
    }
}
