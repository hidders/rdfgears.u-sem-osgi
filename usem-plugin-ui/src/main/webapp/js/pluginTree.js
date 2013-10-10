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
var selectedPlugins = [];
function loadTree(){
    $("#plugin-tree").dynatree({
      checkbox: true,
      selectMode: 3,
      initAjax: {url: "listRepo.jsp"},
      onSelect: function(select, node) {
        // Get a list of all selected nodes, and convert to a key array:
    	  selectedPlugins = $.map(node.tree.getSelectedNodes(), function(node){
			if (node.data.isFolder != true)
				return node.data.key;
			return null;
        });
      },
      onDblClick: function(node, event) {
        node.toggleSelect();
      },
      onKeydown: function(node, event) {
        if( event.which == 32 ) {
          node.toggleSelect();
          return false;
        }
      },
      // The following options are only required, if we have more than one tree on one page:
//        initId: "treeData",
      cookieId: "dynatree-Cb3",
      idPrefix: "dynatree-Cb3-"
    });
  

    $("#btnToggleSelect").click(function(){
      $("#tree2").dynatree("getRoot").visit(function(node){
        node.toggleSelect();
      });
      return false;
    });
    $("#btnDeselectAll").click(function(){
      $("#tree2").dynatree("getRoot").visit(function(node){
        node.select(false);
      });
      return false;
    });
    $("#btnSelectAll").click(function(){
      $("#tree2").dynatree("getRoot").visit(function(node){
        node.select(true);
      });
      return false;
    });
  }