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
<!DOCTYPE html>
<%@page import="nl.tudelft.wis.usem.service.ServicesUIHandler"%>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <!-- Le styles -->
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 50px;
        padding-bottom: 40px;
      }
	  #mydiv{
		position:absolute;
		top:41px;
		bottom:0px;
		width:100%;
	  }
    </style>
    <link href="assets/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    
    <script>
		function navigate(page)
		{			
			if(myframe.src){
			  myframe.src = page; }
			else if(myframe.location){
			  myframe.location.href = page; }
		}
	</script>

  </head>

  <body>

      <div class="container-fluid">
		<div class="row-fluid">
			<div class="span2 well">
				<h4>U-Sem Services</h4>
				<hr>
				<ul class="nav list">
				<%
					out.write(new ServicesUIHandler().getServicesHTML());
				%>
				</ul>
			</div>
			<div class="span10">
					<div id="mydiv">
						<iframe id="myframe" 
							height="100%" 
							src=""
							frameborder="0" 
							scrolling="no"
							width="100%">
						</iframe>
					</div>
			</div>
		</div>
	  </div>

  </body>
</html>
