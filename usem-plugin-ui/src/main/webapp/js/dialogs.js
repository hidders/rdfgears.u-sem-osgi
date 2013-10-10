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
 $(function() {
        var pluginfile = $( "#pluginfile" ),
            allFields = $( [] ).add( pluginfile ),
            tips = $( ".validateTips" );
 
        function updateTips( t ) {
            tips
                .text( t )
                .addClass( "ui-state-highlight" );
            setTimeout(function() {
                tips.removeClass( "ui-state-highlight", 1500 );
            }, 500 );
        }
 
        function checkLength( o, n ) {
            if ( o.val().length == 0) {
                o.addClass( "ui-state-error" );
                updateTips( n + " must be provided" );
                return false;
            } else {
                return true;
            }
        }
 
        $( "#upload-dialog" ).dialog({
            autoOpen: false,
            height: 280,
            width: 350,
            modal: true,
            buttons: {
                "Install": function() {
                    var bValid = true;
                    allFields.removeClass( "ui-state-error" );
 
                    bValid = bValid && checkLength( pluginfile, "Plug-in file");
 
                    if ( bValid ) {
                        $( "#uploadPluginFrom" ).submit();
                    }
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            },
            close: function() {
                allFields.val( "" ).removeClass( "ui-state-error" );
            }
        });
    });
    
    $(function() {
        var tree = $( "#plugin-tree" ),
            allFields = $( [] ).add( tree ),
            tips = $( ".validateTips" );
 
        function updateTips( t ) {
            tips
                .text( t )
                .addClass( "ui-state-highlight" );
            setTimeout(function() {
                tips.removeClass( "ui-state-highlight", 1500 );
            }, 500 );
        }
 
        function checkSelection( o) {
        	var checked = selectedPlugins.length;
        	if (checked == 0) {
        		o.addClass( "ui-state-error" );
                updateTips("At least one plugin must be selected" );
                return false;
            } else {
                return true;
            }
        }

 
        $( "#repo-dialog-form" ).dialog({
            autoOpen: false,
            height: 400,
            width: 380,
            modal: true,
            buttons: {
                "Install": function() {
                    var bValid = true;
                    allFields.removeClass( "ui-state-error" );
 
                    bValid = bValid && checkSelection( tree);
 
                    if ( bValid ) {
                    	$.get("installFromRepo.jsp", { data: JSON.stringify(selectedPlugins) },function(response){
                        	$("#info-dialog").html(response);
                        	$( "#info-dialog" ).dialog( "open" );
                		});
                    }
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            },
            close: function() {
                allFields.val( "" ).removeClass( "ui-state-error" );
                tree.removeClass( "ui-state-error" );
            }
        });
    });