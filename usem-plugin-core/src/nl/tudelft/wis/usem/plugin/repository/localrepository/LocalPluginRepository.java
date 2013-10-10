package nl.tudelft.wis.usem.plugin.repository.localrepository;

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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nl.tudelft.wis.usem.plugin.repository.PluginRepository;

import org.apache.tomcat.util.http.fileupload.IOUtils;

public class LocalPluginRepository implements PluginRepository {

	public static final String DEFAULT_CONFIG_FILE = "../rdfgears.config";
	private static String repositoryDir;
	private Properties configMap = new Properties();

	@Override
	public String listRepositoryContents(Map<String, Object> properties) {

		try {
			configMap.load(getStream(DEFAULT_CONFIG_FILE));
		} catch (Exception e) {
			
			System.out.println("Config file " + DEFAULT_CONFIG_FILE + " not found");
			return null;
		}

		repositoryDir = configMap.getProperty("repository.dir");
		
		File repoFolder = new File(repositoryDir);

		System.out.println(repoFolder.getAbsolutePath());

		if (!repoFolder.isDirectory()) {
			return "";
		}

		JSONArray arr = new JSONArray();

		browseFolder(repoFolder, repoFolder, arr);

		return arr.toString();
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

	private void browseFolder(File root, File folder, JSONArray arr) {

		for (File f : folder.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".jar")) {
				JSONObject obj = new JSONObject();
				obj.accumulate("title", f.getName());
				obj.accumulate("key",
						folder.getPath().replace(root.getPath(), "")
								+ File.separator + f.getName());
				arr.add(obj);
			} else if (f.isDirectory()) {
				JSONObject obj = new JSONObject();
				obj.accumulate("title", f.getName());
				obj.accumulate("isFolder", true);
				JSONArray children = new JSONArray();
				browseFolder(root, f, children);
				obj.accumulate("children", children);
				arr.add(obj);
			}
		}
	}

	@Override
	public InputStream getPlugin(Map<String, Object> properties, String key)
			throws Exception {
		File plugin = new File(LocalPluginRepository.repositoryDir + key);

		return new FileInputStream(plugin);
	}

	@Override
	public String getPluginName(Map<String, Object> properties, String key)
			throws Exception {
		return new File(LocalPluginRepository.repositoryDir + key)
				.getName();
	}

	public boolean publishlPlugin(String folder, String pluginName,
			InputStream plugin) {
		try {
			File publishFolder = new File(repositoryDir + File.separator
					+ folder.trim());

			if (!publishFolder.exists()) {
				publishFolder.mkdirs();
			}

			IOUtils.copy(plugin, new FileOutputStream(new File(publishFolder,
					pluginName)));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
