package nl.tudelft.wis.datamanagement.backend.hbm;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.tudelft.wis.datamanagement.api.entitydef.BasicType;
import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.api.entitydef.Field;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BagType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BooleanType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.LiteralType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.TupleType;
import nl.tudelft.wis.datamanagement.backend.Config;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.sun.org.apache.xerces.internal.dom.DOMOutputImpl;

public class HBMPersister {

    private Element rootElement;
    private Document doc;
    
    public void remove(String name) {
	File file = fileForName(name);
	if(file.exists()){
	    file.delete();
	}
    }

    public void store(EntityDescriptor entityDescriptor) throws Exception {
	DocumentBuilderFactory docFactory = DocumentBuilderFactory
		.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

	doc = docBuilder.newDocument();

	DOMImplementation domImpl = doc.getImplementation();
	DocumentType doctype = domImpl.createDocumentType("hibernate-mapping",
		"-//Hibernate/Hibernate Mapping DTD 3.0//EN",
		"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd");
	doc.appendChild(doctype);

	rootElement = doc.createElement("hibernate-mapping");
	doc.appendChild(rootElement);

	storeEntity(entityDescriptor.getEntity(), entityDescriptor.getName());

	saveAsFile(entityDescriptor);
    }

    private void saveAsFile(EntityDescriptor entityDescriptor)
	    throws Exception {

	File result = fileForName(entityDescriptor.getName());

	LSSerializer lsSerializer = ((DOMImplementationLS) doc
		.getImplementation()).createLSSerializer();
	lsSerializer.getDomConfig().setParameter("format-pretty-print", true);
	
	LSOutput out = new DOMOutputImpl();
	FileOutputStream stream = new FileOutputStream(result);
	out.setByteStream(stream);
	try {
	    lsSerializer.write(doc, out);
	} finally {
	    stream.close();
	}

	if (entityDescriptor.getOriginalName() != null
		&& !entityDescriptor.getName().equalsIgnoreCase(
			entityDescriptor.getOriginalName())) {
	    File oldFile = fileForName(entityDescriptor.getOriginalName());
	    oldFile.delete();
	}
    }

    private File fileForName(String name) {
	File result = new File(Config.getHBMDir(), name.toLowerCase()
		+ ".hbm.xml");
	return result;
    }

    private void storeEntity(TupleType entity, String entityName) {
	Element classElement = doc.createElement("class");
	rootElement.appendChild(classElement);

	classElement.setAttribute("entity-name", entityName);
	classElement.setAttribute("table", entityName);

	Element idElement = doc.createElement("id");
	classElement.appendChild(idElement);

	idElement.setAttribute("name", "$id$");
	idElement.setAttribute("type", "long");
	idElement.setAttribute("column", "id");

	Element genElement = doc.createElement("generator");
	idElement.appendChild(genElement);

	genElement.setAttribute("class", "native");

	addProperties(entityName, entity, classElement);

    }

    private void addProperties(String entityName, TupleType entity,
	    Element classElement) {
	if (entity.getProperties() != null) {
	    for (Field prop : entity.getProperties()) {
		if (prop.getValue() instanceof BasicType) {
		    addAtomicProperty(prop, classElement);
		} else {
		    addEntityProperty(entityName, prop, classElement);
		}
	    }
	}
    }

    private void addEntityProperty(String ownerEntityName, Field prop,
	    Element classElement) {
	if (prop.getValue() instanceof BagType) {
	    Element bagElement = doc.createElement("bag");
	    classElement.appendChild(bagElement);

	    bagElement.setAttribute("name", prop.getName());
	    bagElement.setAttribute("cascade", "all-delete-orphan");
	    bagElement.setAttribute("lazy", "false");

	    Element key = doc.createElement("key");
	    bagElement.appendChild(key);

	    key.setAttribute("column", ownerEntityName + "_id");

	    Element oneToMany = doc.createElement("one-to-many");
	    bagElement.appendChild(oneToMany);

	    oneToMany.setAttribute("class",
		    ownerEntityName + "_" + prop.getName());

	    // Store the new entity
	    storeEntity(((BagType) prop.getValue()).getTuple(), ownerEntityName
		    + "_" + prop.getName());

	} else {

	    Element manyToOne = doc.createElement("many-to-one");
	    classElement.appendChild(manyToOne);

	    manyToOne.setAttribute("name", prop.getName());
	    manyToOne.setAttribute("lazy", "false");
	    manyToOne.setAttribute("class",
		    ownerEntityName + "_" + prop.getName());
	    manyToOne.setAttribute("column",
		    ownerEntityName + "_" + prop.getName() + "_id");
	    manyToOne.setAttribute("unique", "true");
	    manyToOne.setAttribute("cascade", "all");

	    // Store the new entity
	    storeEntity(((TupleType) prop.getValue()).getTuple(),
		    ownerEntityName + "_" + prop.getName());
	}

    }

    private void addAtomicProperty(Field prop, Element classElement) {
	Element propertyElement = doc.createElement("property");
	classElement.appendChild(propertyElement);

	propertyElement.setAttribute("name", prop.getName());
	propertyElement.setAttribute("column", prop.getName());
	propertyElement.setAttribute("type",
		((BasicType) prop.getValue()).getJavaType());

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

	// EntityDescriptiorManager edm = new EntityDescriptiorManager();
	//
	// edm.store(desc);
	//
	// System.out.println(edm.getByName("Employee").getEntity());
	new HBMPersister().store(desc);
	new HBMPersister().remove(desc.getName());

    }

}
