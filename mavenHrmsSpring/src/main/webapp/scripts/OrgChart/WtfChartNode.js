/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
Wtf.ChartNode = function(config){
    Wtf.ChartNode.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.ChartNode, Wtf.Component, {

    bodyStyle : "",
    blankString: String.fromCharCode(160),
    blockId: "block_",
    detailsIcon: "details_icon",
    editIcon: "edit_icon",
    handleclass: " handle",
    idprefix: "tree_",
    mainDivStyle : "block",
    mapIcon: "map_icon",
    nodeClass : "blocknode",
    nodeid : "",
    parentid: "",
    treeclass: "tree",
    unmapIcon: "unmap_icon",

    initComponent: function(){
        Wtf.ChartNode.superclass.initComponent.call(this);
        this.addEvents({
            "dblclick": true    // fired when double clicked for adding the unmapped node to the node in the chart [displays the mapped nodes grid]
        });
    },

    onRender: function(config){
        Wtf.ChartNode.superclass.onRender.call(this,config);
		//	cls keyword is used instead of class keyword to show chat window in IE
        this.elDom = Wtf.DomHelper.append(this.renderTo,{
            tag: "div", id: this.idprefix + this.nodeid, cls: this.treeclass, children: [{

				tag: "div", id: "node_" + this.nodeid, cls: this.nodeClass, children: [{

                    tag: "div", cls: "leftlink", html: this.blankString
                }, {
                    tag: "div", cls: "rightlink", html: this.blankString
                }, {
                    tag: 'div', id: this.blockId + this.nodeid, cls: this.mainDivStyle, children: [{

                        tag : "div", cls : "blocktop " + this.handleclass, children : [{

                            tag : "span", id: "name_" + this.handleclass, html : "<div wtf:qtip=\""+this.nodeNameTip+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.common.node.title")+"'>"+this.nodeName+"</div>"
                        }]
                    }, {
                        tag : "div", cls : "blockbottom", children : [{

                            tag : "span", id: "desc_" + this.nodeid, html : "<div wtf:qtip=\""+this.nodeDesignationTip+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.common.node.role.title")+"'>"+this.nodeDesignation+"</div>"
                        }, {
                            tag : "a" , title : WtfGlobal.getLocaleText("hrms.common.EditUser"), cls : this.detailsIcon, children : [{

                                tag : "span", html : ""
                            }]
                        }, {
                            tag : "a" , title : WtfGlobal.getLocaleText("hrms.common.map.node"), cls : this.mapIcon, children : [{

                                tag : "span", html : ""
                            }]
                        }, {
                            tag : "a" , title : WtfGlobal.getLocaleText("hrms.common.edit.parent"), cls : this.editIcon, children : [{

                                tag : "span", html : ""
                            }]
                        }, {
                            tag : "a" , title : WtfGlobal.getLocaleText("hrms.common.unmap.node"), cls : this.unmapIcon, children : [{

                                tag : "span", html : ""
                            }]
                        }]
                    }]
                }, {
                    tag: "div", cls: "bottomlink", html: this.blankString
                }]
            }]
        },true);
        this.elDom.on('dblclick',this.onDblClick,this);
        this.elDom.on('click',this.onClick,this);
    },

    onClick: function(e, el, opt) {
        if(e) {
            var className = e.target.className;

            switch(className) {
                case "details_icon" :
                    this.fireEvent("editUserClicked", this, e);
                    break;

                case "map_icon" :
                    this.fireEvent("mapClicked", this, e);
                    break;

                case "edit_icon" :
                    this.fireEvent("editClicked", this, e);
                    break;

                case "unmap_icon" :
                    this.fireEvent("unmapClicked", this, e);
                    break;
            }
        }
    },

    onDblClick : function(e) {
        if(e) {
            this.fireEvent("unMappedNodeDblClick", this, e, true);
        }
    }
});

Wtf.reg('ChartNode', Wtf.ChartNode);
