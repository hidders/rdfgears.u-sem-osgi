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
<%@page import="nl.tudelft.wis.datamanagement.backend.EnitiesUIHandler"%>
<html lang="en">
<head>
<meta charset="utf-8">
<!-- Le styles -->
<link href="css/bootstrap.css" rel="stylesheet">

<link href="css/bootstrap-responsive.css" rel="stylesheet">

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

<script src="../jquery/jquery.js" type="text/javascript"></script>
<script src="../jquery/jquery-ui.custom.js" type="text/javascript"></script>
<script src="../jquery/jquery.cookie.js" type="text/javascript"></script>

<link href="../src/skin/ui.dynatree.css" rel="stylesheet"
	type="text/css">
<script src="../src/jquery.dynatree.js" type="text/javascript"></script>

<!-- (Irrelevant source removed.) -->

<script src="js/entityPanel.js" type="text/javascript"></script>
<script src="js/entityList.js" type="text/javascript"></script>

<script src="js/bootstrap.js"></script>
<script src="js/bootbox.js"></script>


<style type="text/css">
body {
	padding-top: 50px;
	padding-bottom: 40px;
}

ul.dynatree-container {
	height: 100%;
	width: 100%;
	background-color: transparent;
	border: 1px solid transparent;
}

ul.dynatree-container a {
	color: black;
	border: 1px solid transparent;
	background-color: transparent;
}

ul.dynatree-container a:hover {
	background-color: transparent;
}

ul.dynatree-container a:focus,span.dynatree-focused a:link {
	background-color: gray;
}

.top-buffer {
	margin-top: 20px;
}
</style>

<script type="text/javascript">
	$(function() {
		$('[rel=tooltip]').dropdown();
	});
</script>

</head>

<body>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2 well">
				<h4>Defined Entities</h4>
				<hr>
				<div class="btn-group">
					<a id="btnAddNew" class="btn btn-mini" rel="tooltip"
						title="Add new entity"><i class="icon-plus"></i></a> <a
						id="btnRemoveEntity" class="btn btn-mini" rel="tooltip"
						title="Remove entity"><i class=" icon-remove"></i></a>
				</div>
				<div id="entityListTree" class="top-buffer"></div>
			</div>
			<div class="span9">
				<div id="entityPanel" class="row-fluid well">
					<div class="row-fluid top-buffer span6">
						<div>
							Name: <input id="txtName" type="text" />
						</div>
						<div>
							<label class="checkbox inline"> <input type="checkbox"
								id="chkRead"> Read Shared
							</label> <label class="checkbox inline"><input type="checkbox"
								id="chkWrite"> Write Shared </label>
						</div>

						<div class="well top-buffer">
							<div class="btn-group row-fluid">
								<a id="btnAddAtomic" class="btn btn-mini" rel='tooltip'
									title="Add propery"><i class=" icon-plus"></i></a> <a
									id="btnRemove" class="btn btn-mini" rel='tooltip'
									title="Remove propery"><i class=" icon-remove"></i></a>
							</div>
							<div id="tree"></div>

						</div>

					</div>

					<div class="row-fluid  span6">
						<label>Description:</label>
						<textarea id="txtDescription" rows="5"></textarea>
					</div>
					<div class="row-fluid top-buffer btn-group">
						<button id="btnSave" class="btn btn-primary">Save</button>
						<button id="btnCancel" class="btn">Cancel</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div id="property" class="modal hide fade" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>Property details</h3>
		</div>
		<div class="modal-body">
			<label>Name:</label> <input id="propertyName" type="text" /> <label>Type:</label>
			<select id="sctIsComposite">
				<option value="false">Atomic</option>
				<option value="true">Composite</option>
			</select>

			<div id="atomicProp">
				<label>RGL Type:</label> <select id="sctType">
					<option value="boolean">Boolean</option>
					<option value="literal">Literal</option>
				</select>
			</div>

			<div id="entityProp">
				<label class="checkbox inline"> <input type="checkbox"
					id="chkMultiple">Multiple
				</label>
			</div>
		</div>
		<div class="modal-footer">
			<button id="btnSaveProperty" class="btn  btn-primary">Save</button>
			<button id="btnCancelProperty" class="btn" data-dismiss="modal"
				aria-hidden="true">Cancel</button>
		</div>
	</div>
</body>
</html>
