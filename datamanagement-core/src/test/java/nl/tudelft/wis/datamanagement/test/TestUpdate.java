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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import nl.tudelft.wis.datamanagement.backend.DataTypesFacade;
import nl.tudelft.wis.datamanagement.backend.persistance.EntityPersister;

import org.junit.Test;

public class TestUpdate {

    @Test
    public void testUpdateRoot() throws Exception {
	DataTypesFacade facade = new DataTypesFacade();
	facade.storeDataType(TestUtils.getDescriptor());

	EntityPersister ep = new EntityPersister();
	ep.store("Employee", TestUtils.getObject());

	String query = "from Employee";
	Map<String, Object> setFields = new HashMap<String, Object>();
	setFields.put("name", "newName");
	//
	//
	ep.update(query, null, setFields);

	
	List<Map> executeQuery = ep.executeQuery("from Employee", null);
	Map emp = executeQuery.get(0);

	Assert.assertEquals(emp.get("name"), "newName");
    }
    
    @Test
    public void testOneToOne() throws Exception {
	DataTypesFacade facade = new DataTypesFacade();
	facade.storeDataType(TestUtils.getDescriptor());

	EntityPersister ep = new EntityPersister();
	ep.store("Employee", TestUtils.getObject());

	String query = "select e.desk from Employee e";
	Map<String, Object> setFields = new HashMap<String, Object>();
	setFields.put("deskName", "newName");
	//
	//
	ep.update(query, null, setFields);

	
	List<Map> executeQuery = ep.executeQuery("from Employee", null);
	Map emp = executeQuery.get(0);

	Assert.assertEquals(((Map) emp.get("desk")).get("deskName"), "newName");
    }
    
    @Test
    public void testOneToMany() throws Exception {
	DataTypesFacade facade = new DataTypesFacade();
	facade.storeDataType(TestUtils.getDescriptor());

	EntityPersister ep = new EntityPersister();
	ep.store("Employee", TestUtils.getObject());

	String query = "select dep from Employee e join e.departments dep where dep.departmentName like 'dep2'";
	Map<String, Object> setFields = new HashMap<String, Object>();
	setFields.put("departmentName", "newName");
	//
	//
	ep.update(query, null, setFields);

	
	List<Map> executeQuery = ep.executeQuery("from Employee", null);
	Map emp = executeQuery.get(0);

	List<Map<String, Object>> departments = (List) emp.get("departments");
	Assert.assertTrue(departments.get(0).get("departmentName").equals("newName") || departments.get(1).get("departmentName").equals("newName"));
    }
    
    @Test
    public void testReplaceOneToOne() throws Exception {
	DataTypesFacade facade = new DataTypesFacade();
	facade.storeDataType(TestUtils.getDescriptor());

	EntityPersister ep = new EntityPersister();
	ep.store("Employee", TestUtils.getObject());

	String query = "from Employee";
	Map<String, Object> setFields = new HashMap<String, Object>();
	
	Map desk = new HashMap();
	desk.put("deskName", "newDesk");
	desk.put("number", "5");
	
	setFields.put("desk", desk);
	//
	//
	ep.update(query, null, setFields);

	
	List<Map> executeQuery = ep.executeQuery("from Employee", null);
	Map emp = executeQuery.get(0);

	Map<String, Object> d = (Map) emp.get("desk");
	Assert.assertTrue(d.get("deskName").equals("newDesk"));
    }
    
    @Test
    public void testReplaceOneToMany() throws Exception {
	DataTypesFacade facade = new DataTypesFacade();
	facade.storeDataType(TestUtils.getDescriptor());

	EntityPersister ep = new EntityPersister();
	ep.store("Employee", TestUtils.getObject());

	String query = "from Employee";
	Map<String, Object> setFields = new HashMap<String, Object>();
	ArrayList list = new ArrayList();
	
	Map department1 = new HashMap();
	department1.put("departmentName", "newDep");
	list.add(department1);
	
	setFields.put("departments", list);
	//
	//
	ep.update(query, null, setFields);

	
	List<Map> executeQuery = ep.executeQuery("from Employee", null);
	Map emp = executeQuery.get(0);

	List<Map<String, Object>> departments = (List) emp.get("departments");
	Assert.assertEquals(departments.size(), 1);
	Assert.assertTrue(departments.get(0).get("departmentName").equals("newDep"));
    }
}
