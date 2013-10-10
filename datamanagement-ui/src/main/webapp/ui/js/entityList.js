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
function resetEntityList() {
	$.get("listEntities.jsp", {}, function(response) {
		var result = jQuery.parseJSON(response);
		$("#entityListTree").dynatree("getRoot").removeChildren();
		$("#entityListTree").dynatree("getRoot").addChild(result);
	});
}

function deleteEntity(name){
	$.get("deleteEntity.jsp", {
		data : name
	}, function(response) {
		resetEntityList();
	});
}

$(function() {
	// Initialize the tree inside the <div>element.
	// The tree structure is read from the contained <ul> tag.
	$("#entityListTree").dynatree({
		title : "List of Entities",
		minExpandLevel : 2,
		onDblClick : function(node) {
			setEntity(node.data.title);
		},
	});

	$("#btnAddNew").click(function() {
		var node = $("#entityListTree").dynatree("getActiveNode");
		if (node) {
			node.deactivate();
		}
		setEntity();
	});

	$("#btnRemoveEntity")
			.click(
					function() {
						var node = $("#entityListTree").dynatree(
								"getActiveNode");
						if (node) {
							bootbox
									.confirm(
											"Are you sure you want to remove the selected entity?",
											function(result) {
												if (result) {
													deleteEntity(node.data.title);
												}
											});
						}
					});

	resetEntityList();
});