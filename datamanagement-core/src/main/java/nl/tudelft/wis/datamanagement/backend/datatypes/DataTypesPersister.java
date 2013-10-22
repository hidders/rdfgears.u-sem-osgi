package nl.tudelft.wis.datamanagement.backend.datatypes;

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.api.entitydef.Field;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BagType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BooleanType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.LiteralType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.TupleType;
import nl.tudelft.wis.datamanagement.backend.Config;

public class DataTypesPersister {

	private Config myConf;

	public DataTypesPersister() {
		myConf = new Config();
	}

	public void remove(String name) {
		File file = fileForName(name);
		if (file.exists())
			file.delete();
	}

	public void store(EntityDescriptor entityDescriptor) throws Exception {
		if (entityDescriptor == null || entityDescriptor.getName() == null) {
			throw new IllegalArgumentException("Invalid entity descriptor.");
		}

		JAXBContext context = JAXBContext.newInstance(EntityDescriptor.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		File result = fileForName(entityDescriptor.getName());

		FileOutputStream os = new FileOutputStream(result);
		try {
			m.marshal(entityDescriptor, os);
		} finally {
			os.close();
		}

		if (entityDescriptor.getOriginalName() != null
				&& !entityDescriptor.getName().equalsIgnoreCase(
						entityDescriptor.getOriginalName())) {
			File oldFile = fileForName(entityDescriptor.getOriginalName());
			oldFile.delete();
		}

	}

	private File fileForName(String name) {
		File result = new File(myConf.getDataTypesDir(), name.toLowerCase()
				+ ".xml");
		return result;
	}

	public EntityDescriptor getByName(String name) throws Exception {
		File result = fileForName(name);

		if (!result.exists()) {
			return null;
		}

		JAXBContext jaxbContext = JAXBContext
				.newInstance(EntityDescriptor.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		EntityDescriptor ed = (EntityDescriptor) jaxbUnmarshaller
				.unmarshal(result);

		setTransientValues(ed);
		return ed;
	}

	public List<EntityDescriptor> getAll() throws Exception {
		List<EntityDescriptor> descs = new ArrayList<EntityDescriptor>();

		JAXBContext jaxbContext = JAXBContext
				.newInstance(EntityDescriptor.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		File folder = myConf.getDataTypesDir();
		for (String f : folder.list()) {
			if (f.endsWith(".xml")) {
				EntityDescriptor entityDesc = (EntityDescriptor) jaxbUnmarshaller
						.unmarshal(new File(folder, f));
				setTransientValues(entityDesc);
				descs.add(entityDesc);
			}
		}

		return descs;
	}

	public List<String> getAllNames() throws Exception {
		List<String> descs = new ArrayList<String>();

		File folder = myConf.getDataTypesDir();
		for (String f : folder.list()) {
			if (f.endsWith(".xml"))
				descs.add(f.substring(0, f.lastIndexOf(".xml")));
		}

		return descs;
	}

	private void setTransientValues(EntityDescriptor ed) {
		ed.setOriginalName(ed.getName());
	}

	public static void main(String[] args) throws Exception {
		TupleType desk = new TupleType();
		List<Field> properties = new ArrayList<Field>();
		properties.add(new Field("deskName", new LiteralType()));
		properties.add(new Field("number", new BooleanType()));
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

		new DataTypesPersister().store(desc);

		System.out.println(new DataTypesPersister().getByName("Employee"));
	}

}
