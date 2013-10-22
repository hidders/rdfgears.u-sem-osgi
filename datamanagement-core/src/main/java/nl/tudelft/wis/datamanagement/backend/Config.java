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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * This configuration system reads the properties defined in
 * DEFAULT_CONFIG_FILE, which is included in the resources of this project
 * 
 * @author Stefano Bocconi
 * 
 */
public class Config {

	public static final String DEFAULT_CONFIG_FILE = "../datamanagement.config";

	private static Properties configMap = new Properties();

	/**
	 * reinitialize the configuration
	 */
	public Config() {

		try {
			configMap.load(getStream(DEFAULT_CONFIG_FILE));
		} catch (Exception e) {
			URL dir = this.getClass().getClassLoader().getResource(".");
			if (dir != null) {
				System.err.println("Cannot open configuration file '"
						+ DEFAULT_CONFIG_FILE + "' for reading in dir "
						+ dir.getPath() + ". Trying Current Working Dir.");
			} else {
				System.err
						.println("Cannot open configuration file '"
								+ DEFAULT_CONFIG_FILE
								+ "' as there is no ClassLoader directory accessible. Trying Current Working Dir.");
			}

			/*
			 * try loading it from CWD, if it is not the same location as where
			 * this class is stored. Necessary for the junit test :-(
			 */
			try {
				configMap.load(new FileInputStream(DEFAULT_CONFIG_FILE)); // may
																			// not
				// work in
				// .jar
				// files,
				// etc
				String dirName = System.getProperty("user.dir");
				System.out.println("Ok, loaded config file " + dirName + "/"
						+ DEFAULT_CONFIG_FILE);
			} catch (Exception e2) {
				System.err.println("Cannot open configuration file '"
						+ DEFAULT_CONFIG_FILE + "' for reading in dir "
						+ System.getProperty("user.dir"));
			}
		}
		initConfig();

	}

	/**
	 * Get a stream of the file, assuming it can be found next to the current
	 * class location. Useful when dealing with JAR files, where the current
	 * working directory has nothing to do with the place where the config is
	 * stored.
	 * 
	 * @param fileName
	 * @return
	 */
	private InputStream getStream(String fileName) {
		InputStream resourceAsStream = this.getClass().getClassLoader()
				.getResourceAsStream(fileName);

		// no exception

		return resourceAsStream;
	}

	private void initConfig() {
	}

	public String getDatabaseURL() {

		return configMap.getProperty("database.url");
	}

	public String getDatabaseUser() {

		return configMap.getProperty("dbuser");
	}

	public String getDatabasePwd() {

		return configMap.getProperty("dbpassword");
	}

	public File getHBMDir() {
		String dirName = configMap.getProperty("rdfgears.base.path")
				+ configMap.getProperty("hbm.path");

		File destdir = new File(dirName);

		if (!destdir.exists())
			destdir.mkdirs();

		return destdir;
	}

	public File getDataTypesDir() {

		String dirName = configMap.getProperty("rdfgears.base.path")
				+ configMap.getProperty("data.types.path");

		File destdir = new File(dirName);

		if (!destdir.exists())
			destdir.mkdir();

		return destdir;
	}

}
