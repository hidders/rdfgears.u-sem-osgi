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


import nl.tudelft.wis.datamanagement.api.entitydef.EntityDescriptor;
import nl.tudelft.wis.datamanagement.backend.datatypes.DataTypesPersister;
import nl.tudelft.wis.datamanagement.backend.db.DatabaseManager;
import nl.tudelft.wis.datamanagement.backend.hbm.HBMPersister;

public class DataTypesFacade {
    
    public void storeDataType(EntityDescriptor ed) throws Exception {
	DataTypesPersister dataTypesPersister = new DataTypesPersister();
	DatabaseManager databaseManager = new DatabaseManager();
	HBMPersister hbmPersister = new HBMPersister();

	EntityDescriptor oldEntity = dataTypesPersister.getByName(ed.getName());

	databaseManager.updateDatabaseScheme(ed, oldEntity);
	hbmPersister.store(ed);
	dataTypesPersister.store(ed);
    }
    
    public void removeDataType(String name) throws Exception{
	DataTypesPersister dataTypesPersister = new DataTypesPersister();
	DatabaseManager databaseManager = new DatabaseManager();
	HBMPersister hbmPersister = new HBMPersister();

	EntityDescriptor entity = dataTypesPersister.getByName(name);

	databaseManager.removeDatabaseScheme(entity);
	hbmPersister.remove(name);
	dataTypesPersister.remove(name);
    }

}
