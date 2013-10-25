package nl.tudelft.wis.datamanagement.backend.persistance;

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

import nl.tudelft.wis.datamanagement.backend.Config;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {

	private static final Session sessionFactory = buildSessionFactory();

	private static Config myConf;
	private static boolean initDone = false;

	private static void init() {
		if (initDone) {
			return;
		}
		myConf = new Config();
		initDone = true;
	}

	private static Session buildSessionFactory() {
		init();
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration configuration = new Configuration();
			configuration.addDirectory(myConf.getHBMDir());
			configuration.setProperty("hibernate.connection.url", myConf.getDatabaseURL());
			configuration.setProperty("hibernate.connection.username", myConf.getDatabaseUser());
			configuration.setProperty("hibernate.connection.password", myConf.getDatabasePwd());
			
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.configure().getProperties()).buildServiceRegistry();        
			SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			
			Session openSession = sessionFactory.openSession();

			return openSession;
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() {
		return sessionFactory;
	}

}