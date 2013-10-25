<%--
  #%L
  RDFGears
  %%
  Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
  %%
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
  #L%
  --%>
<%@page import="nl.tudelft.wis.usem.plugin.repository.localrepository.LocalRepository"%>
<%@page
	import="nl.tudelft.wis.usem.plugin.repository.PluginRepositoryFactory"%>
<%@page
	import="org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory"%>
<%@page
	import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.tomcat.util.http.fileupload.FileItem"%>
<%@page import="java.io.InputStream"%>
<%@ page import="java.util.*"%>
<%
	try {
		String inputFolder = null;

		InputStream plugin = null;
		String pluginName = null;

		List<FileItem> items = new ServletFileUpload(
				new DiskFileItemFactory()).parseRequest(request);
		for (FileItem item : items) {
			if (item.isFormField()) {
				String fieldname = item.getFieldName();

				if (fieldname.equals("folder")) {
					inputFolder = item.getString();
				}
 
			} else {
				if (item.getFieldName().equals("pluginfile")) {
					pluginName = item.getName();
					plugin = item.getInputStream();
				}
			}
		}

		if (new LocalRepository().publishlPlugin(
				inputFolder, pluginName, plugin))
			out.write("<div class=\"success\">Plug-in successfully published!</div>");
		else
			out.write("<div class=\"error\">Error publishing plug-in!</div>");
	} catch (Throwable t) {
		t.printStackTrace();
		out.write("<div class=\"error\">Error publishing plug-in!</div>");
	}
%>
