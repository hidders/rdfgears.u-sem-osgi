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

import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.api.entitydef.Field;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BagType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.LiteralType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.TupleType;

public class TestUtils {
    public static EntityDescriptor getDescriptor() {
	TupleType desk = new TupleType();
	List<Field> properties = new ArrayList<Field>();
	properties.add(new Field("deskName", new LiteralType()));
	properties.add(new Field("number", new LiteralType()));
	desk.setProperties(properties);

	TupleType department = new TupleType();
	properties = new ArrayList<Field>();
	properties.add(new Field("departmentName", new LiteralType()));
	department.setProperties(properties);

	TupleType employee = new TupleType();
	properties = new ArrayList<Field>();
	properties.add(new Field("name", new LiteralType()));
	properties.add(new Field("departments", new BagType(department)));
	properties.add(new Field("desk", desk));
	employee.setProperties(properties);

	EntityDescriptor desc = new EntityDescriptor();

	desc.setName("Employee");
	desc.setEntity(employee);

	return desc;
    }

    public static Map getObject() {
	Map desk = new HashMap();
	desk.put("deskName", "desk1");
	desk.put("number", "5");

	Map department1 = new HashMap();
	department1.put("departmentName", "dep1");

	Map department2 = new HashMap();
	department2.put("departmentName", "dep2");

	List departments = new ArrayList();
	departments.add(department1);
	departments.add(department2);

	Map employee = new HashMap();
	employee.put("name", "firstEmployee");
	employee.put("desk", desk);
	employee.put("departments", departments);

	return employee;
    }
}
