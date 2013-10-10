package nl.tudelft.wis.datamanagement.test;

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

import junit.framework.Assert;
import nl.tudelft.wis.datamanagement.backend.DataTypesFacade;
import nl.tudelft.wis.datamanagement.backend.persistance.EntityPersister;

import org.junit.Test;

public class TestInsertChildren {

    
    @Test
    public void test() throws Exception {
	DataTypesFacade facade = new DataTypesFacade();
	facade.storeDataType(TestUtils.getDescriptor());

	EntityPersister ep = new EntityPersister();
	ep.store("Employee", TestUtils.getObject());

	String query = "from Employee";

	Map<String, Object> setFields = new HashMap<String, Object>();
	setFields.put("departmentName", "newDep");
	
	//
	//
	ep.insertChild(query, null, "departments", setFields);

	
	List<Map> executeQuery = ep.executeQuery("from Employee", null);
	Map emp = executeQuery.get(0);

	List<Map<String, Object>> departments = (List) emp.get("departments");
	Assert.assertEquals(departments.size(), 3);
    }
}
