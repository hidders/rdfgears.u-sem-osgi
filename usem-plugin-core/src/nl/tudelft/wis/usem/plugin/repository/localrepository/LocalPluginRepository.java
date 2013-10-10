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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nl.tudelft.wis.usem.plugin.repository.PluginRepository;

import org.apache.tomcat.util.http.fileupload.IOUtils;

public class LocalPluginRepository implements PluginRepository {
	
	private static final String REPOSITORY_FOLDER = "../temp/pluginRepository";

	@Override
	public String listRepositoryContents(Map<String, Object> properties) {

		File repoFolder = new File(REPOSITORY_FOLDER);

		System.out.println(repoFolder.getAbsolutePath());

		if (!repoFolder.isDirectory()) {
			return "";
		}

		JSONArray arr = new JSONArray();

		browseFolder(repoFolder, repoFolder, arr);

		return arr.toString();
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
	public InputStream getPlugin(Map<String, Object> properties, String key) throws Exception {
		File plugin = new File(LocalPluginRepository.REPOSITORY_FOLDER + key);

		return new FileInputStream(plugin);
	}
	
	@Override
	public String getPluginName(Map<String, Object> properties, String key) throws Exception {
		return  new File(LocalPluginRepository.REPOSITORY_FOLDER + key).getName();
	}
	
	public boolean publishlPlugin(String folder, String pluginName, InputStream plugin) {
		try {
			File publishFolder = new File(REPOSITORY_FOLDER + File.separator
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
