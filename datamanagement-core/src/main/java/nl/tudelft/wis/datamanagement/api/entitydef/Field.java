package nl.tudelft.wis.datamanagement.api.entitydef;

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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import nl.tudelft.wis.datamanagement.api.entitydef.types.BagType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.BooleanType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.LiteralType;
import nl.tudelft.wis.datamanagement.api.entitydef.types.TupleType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Field {

    public Field() {
    }

    public Field(String name, FieldType value) {
	super();
	this.name = name;
	this.value = value;
    }

    @XmlAttribute
    private String name;

    @XmlElements({ @XmlElement(name = "bool", type = BooleanType.class),
	    @XmlElement(name = "literal", type = LiteralType.class),
	    @XmlElement(name = "bag", type = BagType.class),
	    @XmlElement(name = "tuple", type = TupleType.class) })
    private FieldType value;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public FieldType getValue() {
	return value;
    }

    public void setValue(FieldType value) {
	this.value = value;
    }

}
