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
<%@page import="nl.tudelft.wis.usem.plugin.admin.PluginDetails"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="nl.tudelft.wis.usem.plugin.admin.PluginAdminUIHandler"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>U-SEM Plug-in Repository</title>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />
<script src="http://code.jquery.com/jquery-1.8.2.js"></script>
<script src="http://code.jquery.com/ui/1.9.1/jquery-ui.js"></script>
<style>
body {
	font-size: 62.5%;
}

label,input {
	display: block;
}

input.text {
	margin-bottom: 12px;
	width: 95%;
	padding: .4em;
}

fieldset {
	padding: 0;
	border: 0;
	margin-top: 25px;
}

h1 {
	font-size: 1.2em;
	margin: .6em 0;
}

div#plugins-contain {
	width: 400px;
	margin: 20px 0;
}

div#plugins-contain table {
	margin: 1em 0;
	border-collapse: collapse;
	width: 100%;
}

div#plugins-contain table td,div#plugins-contain table th {
	border: 1px solid #eee;
	padding: .6em 10px;
	text-align: left;
}

.ui-dialog .ui-state-error {
	padding: .3em;
}

.validateTips {
	border: 1px solid transparent;
	padding: 0.3em;
}

.myfields label,.myfields input {
	display: inline-block;
}
</style>
<script>
	$(function() {
		$("#info-dialog").dialog({
			autoOpen : false,
			height : 140,
			modal : true,
			close : function() {
				$('#pluginfile').val("");
			}
		});

		$( "#publish-plugin" )
        .button()
        .click(function() {
        	
        	function updateTips( t ) {
        		$( ".validateTips" )
                    .addClass( "ui-state-highlight" );
                setTimeout(function() {
                	$( ".validateTips" ).removeClass( "ui-state-highlight", 1500 );
                }, 500 );
            }
        	
        	if($('#pluginfile').val().length == 0){
        		$('#pluginfile').addClass( "ui-state-error" );
            	updateTips();
            	return;
        	}
             
        	var data, xhr;

			data = new FormData();
			data.append('pluginfile', $('#pluginfile')[0].files[0]);
			data.append('folder', $('#folder').val());

			xhr = new XMLHttpRequest();

			xhr.open('POST', 'publishPlugin.jsp', true);
			xhr.onreadystatechange = function(response) {
				if (xhr.readyState == 4 && xhr.status == 200) {
					$('#pluginfile').removeClass( "ui-state-error" );
					$( ".validateTips" ).removeClass( "ui-state-highlight");
					$("#info-dialog").html(xhr.responseText);
					$("#info-dialog").dialog("open");
				}
			};
			xhr.send(data);
        });
	});
</script>
</head>
<body>

	<div id="plugins-contain" class="ui-widget">
		<h1>Publish new Plug-in:</h1>
		
		<p class="validateTips">A plug-in file has to be selected.</p>

		<form>
			<fieldset>
				<label for="folder">Folder</label> 
				<input type="text" name="folder" id="folder" class="text ui-widget-content ui-corner-all" /> 
					<label for="pluginfile">Plug-in</label> 
					<input type="file" name="pluginfile" id="pluginfile" class="text ui-widget-content ui-corner-all" />
			</fieldset>
		</form>

	</div>

	<div id="info-dialog" class="ui-widget" title="Info"></div>

	<button id="publish-plugin">Publish plug-in</button>

</body>
</html>