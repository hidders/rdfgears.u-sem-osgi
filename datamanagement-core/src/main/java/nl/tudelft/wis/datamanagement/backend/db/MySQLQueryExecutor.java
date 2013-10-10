package nl.tudelft.wis.datamanagement.backend.db;

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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import nl.tudelft.wis.datamanagement.backend.Config;

public class MySQLQueryExecutor {

    public void execute(Iterable<String> statements) throws Exception {
	//TODO connection pool
	
	System.out.println("Executing statements");
	// This will load the MySQL driver, each DB has its own driver
	Class.forName("com.mysql.jdbc.Driver");
	// Setup the connection with the DB
	Connection connection = DriverManager.getConnection(Config.getDbURL(),
		Config.getDbUsername(), Config.getDbPassword());

	connection.setAutoCommit(false);

	try {
	    Statement statement = connection.createStatement();

	    for (String s : statements) {
		System.out.println(s);
		statement.addBatch(s);
	    }

	    statement.executeBatch();

	    connection.commit();
	} catch (SQLException e) {
	    e.printStackTrace();
	    connection.rollback();
	}finally{
	    connection.close();
	}
    }
}
