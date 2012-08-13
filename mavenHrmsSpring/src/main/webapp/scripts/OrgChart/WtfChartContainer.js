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

Wtf.ChartContainer = function(config) {
    Wtf.apply(this, config);

    this.mappedNodeStore = null;
    this.selectedMappedNode = "";
    this.selectedMappedNodeParent = "";
    this.isSelected = false;
    
    Wtf.ChartContainer.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.ChartContainer, Wtf.Panel, {
    //    bodyStyle : "overflow: auto !important;",

    initComponent : function(config) {
        Wtf.ChartContainer.superclass.initComponent.call(this, config);
    },

    orgchartPublishHandler: function(msg) {
        this.mappedNodeStore.reload();
    },

    onRender : function(ct) {
        Wtf.ChartContainer.superclass.onRender.call(this, ct);

        this.mappedRec = Wtf.data.Record.create([
        {
            name: 'projid'
        },

        {
            name: 'userid'
        },

        {
            name: 'username'
        },

        {
            name: 'emailid'
        },

        {
            name: 'contactno'
        },

        {
            name: 'nodeid'
        },

        {
            name: 'level'
        },

        {
            name: 'fromuid'
        },

        {
            name: 'fname'
        },

        {
            name: 'lname'
        },

        {
            name: 'image'
        },

        {
            name: 'xpos'
        },

        {
            name: 'ypos'
        },

        {
            name: 'designation'
        },
        {
            name: 'role'
        },
        {
            name: 'designationid'
        },
        {
            name: 'roleid'
        },
        {
            name: 'accno'
        },
        {
            name: 'department'
        },
        {
            name: 'employeeid'
        },

        {
            name: 'address'
        },
        
        {
            name: 'isleaf'
        }
        ]);

        this.mappedNodeStore = new Wtf.data.Store({
            url: 'OrganizationChart/getMappedUsers.common',
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            }, this.mappedRec)
        });

        this.mappedNodeStore.on("load", this.renderChartNodes, this);

        calMsgBoxShow(202,4,true);
        
        this.mappedNodeStore.load({
            params: {
                action: 3,        // for fetching mapped nodes
                pid: this.projid
            }
        });


    },
    editUserC:function(rec){
        this.editUserWindow=new Wtf.common.CreateUser({
            isEdit:true,
            record:rec
        });
        this.editUserWindow.on('close',this.setSelectedFalse, this);
        this.editUserWindow.on('save',this.genSuccessResponse, this);
        this.editUserWindow.on('notsave',this.genFailureResponse, this);

    },
    setSelectedFalse:function(){
            var ChartPanel = Wtf.getCmp(this.projid + "chartContainer");
            ChartPanel.isSelected = false;
    },
    genSuccessResponse:function(response){
        
        if(response.roleflag!=undefined){
            WtfComMsgBox([WtfGlobal.getLocaleText("hrms.administration.organization.chart"),response.msg]);
        }else {
            ResponseAlert([WtfGlobal.getLocaleText("hrms.administration.organization.chart"),response.msg]);
        }
        if(response.success==true)this.reloadChartContainer();
    },
    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        ResponseAlert([WtfGlobal.getLocaleText("hrms.common.permission"), msg]);
    },
    reloadChartContainer: function() {
        calMsgBoxShow(202,4,true);
        this.body.dom.innerHTML = "";
        this.mappedNodeStore.reload();
    },
    renderChartNodes: function(obj, rec, opt) {

        Wtf.updateProgress();
        var totalStore = rec.length;
       //// Wtf.ownerStore.load();
        if(totalStore > 0) {

            var displayName = rec[0].data.fname + " " + rec[0].data.lname;
            var displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,20);
            if(Wtf.isIE6 || Wtf.isIE7){
                displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,10);
            }
            var displayRole = rec[0].data.role != "" ? rec[0].data.role : "-";
            var displayRoleEllipsis = Wtf.util.Format.ellipsis(displayRole,13);
            var rootNodeId = rec[0].data.nodeid;

            var chartNodes = new Wtf.ChartNode({
                editIcon: "",
                mapIcon: "",
                nodeid : rootNodeId,
                projid: this.projid,
                nodeName: displayNameEllipsis,
                nodeNameTip: displayName,
                nodeDesignation: displayRoleEllipsis,
                nodeDesignationTip: displayRole,
                autoscroll: true,
                layout: "fit",
                handleclass: "",
                renderTo: this.body.id,
                unmapIcon: ""
            });

            this.makeDraggable(rootNodeId);

            chartNodes.on("editUserClicked", function(mappedNode, e, flag) {

                var ChartPanel = Wtf.getCmp(this.projid + "chartContainer");

                if(!ChartPanel.isSelected) {
                    ChartPanel.selectedMappedNode = mappedNode.nodeid;
                    ChartPanel.selectedMappedNodeParent = mappedNode.parentid;
                    ChartPanel.editUserProfile();
                    ChartPanel.isSelected = true;
                }
            });

            for(var cntStore = 1; cntStore < totalStore; cntStore++) {
                displayName = rec[cntStore].data.fname + " " + rec[cntStore].data.lname;
                displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,20);
                if(Wtf.isIE6 || Wtf.isIE7){
                    displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,10);
                }
                displayRole = rec[cntStore].data.role != "" ? rec[cntStore].data.role : "-";
                var displayRoleEllipsis = Wtf.util.Format.ellipsis(displayRole,13);
                Wtf.each(rec[cntStore].data.fromuid,function(item, idx){
                    chartNodes = new Wtf.ChartNode({
                        autoscroll: true,
                        layout: "fit",
                        mainDivStyle:"block"+(idx>0?" dullnode ":""),
                        nodeid: rec[cntStore].data.nodeid,
                        idprefix: "tree_"+(idx>0?idx:""),
                        nodeName: displayNameEllipsis,
                        nodeNameTip: displayName,
                        nodeDesignation: displayRoleEllipsis,
                        nodeDesignationTip: displayRole,
                        parentid: rec[cntStore].data.fromuid,
                        projid: this.projid,
                        renderTo: this.body.id
                    });

                    var parentId = "tree_" + item;
                    Wtf.get(parentId).appendChild(chartNodes.elDom.id);

                    if(idx==0){
                        chartNodes.on("editUserClicked", function(mappedNode, e, flag) {

                            var ChartPanel = Wtf.getCmp(this.projid + "chartContainer");

                            if(!ChartPanel.isSelected) {
                                ChartPanel.selectedMappedNode = mappedNode.nodeid;
                                ChartPanel.selectedMappedNodeParent = mappedNode.parentid;
                                ChartPanel.editUserProfile();
                                ChartPanel.isSelected = true;
                            }
                        });

                        chartNodes.on("editClicked", function(mappedNode, e, flag) {

                            var ChartPanel = Wtf.getCmp(this.projid + "chartContainer");
                            var nodeData = ChartPanel.mappedNodeStore.query("nodeid", mappedNode.nodeid);

                            if(nodeData.items[0].data.level != 0) {
                                if(!ChartPanel.isSelected) {
                                    ChartPanel.selectedMappedNode = mappedNode.nodeid;
                                    ChartPanel.selectedMappedNodeParent = mappedNode.parentid;

                                    var fname = nodeData.items[0].data.fname;
                                    var lname = nodeData.items[0].data.lname;
                                    var name = fname+" "+lname;
                                    name = name.trim();
                                    ChartPanel.editTo(name);
                                    ChartPanel.isSelected = true;
                                }
                            } else
                                ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.not.sufficient.priviledges.edit.root.node")]);
                        });

                       chartNodes.on("unmapClicked", function(mappedNode, e, flag) {

                            var ChartPanel = Wtf.getCmp(this.projid + "chartContainer");
                            if(!ChartPanel.isSelected) {

                                ChartPanel.isSelected = true;
                                var nodeData = ChartPanel.mappedNodeStore.query("nodeid", mappedNode.nodeid);

                                if(nodeData.items[0].data.level != 0) {
                                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.common.unmap.node"), function(btn) {
                                        if(btn == "yes") {
                                            ChartPanel.isSelected = false;
                                            ChartPanel.unmapNode(mappedNode.nodeid);
                                        }
                                        else if(btn == "no") {
                                            ChartPanel.isSelected = false;
                                        }
                                    }, this);
                                } else {
                                    ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.not.sufficient.priviledges.unmap.root.node")]);
                                }
                            }
                       });
                   }
               }, this);
               this.makeDraggable(rec[cntStore].data.nodeid);
            }

            var rootNode = Wtf.get("tree_" + rootNodeId).dom;

            for(var cntItems = rootNode.childNodes.length - 1; cntItems > 0; cntItems--) {
                if(rootNode.childNodes[cntItems].id.indexOf("tree_") >= 0)
                    this.renderLinks(rootNode);
            }

            if(rootNode.firstChild.className.indexOf("child") >= 0)
                rootNode.firstChild.className = rootNode.firstChild.className.split("child")[0];

            var rootDetailsIcon = Wtf.get("desc_" + rootNodeId).dom.nextSibling;
               rootDetailsIcon.style.marginLeft = "40px";
            this.doLayout();
        }
    },
   
    renderLinks: function(node) {

        var place = " child";
        var bottomLink = "";

        node.className = "tree block" + this.childrenLength(node);

        for(var cntItems = node.childNodes.length - 1; cntItems > 0; cntItems--) {
            if(node.childNodes[cntItems].id.indexOf("tree_") >= 0)
                this.renderLinks(node.childNodes[cntItems]);
        }

        var firstItem = this.firstNode(node.parentNode);
        if(firstItem && firstItem.nextSibling) {
            if(firstItem.id == node.id)
                place = " sib_left";

            else if(this.lastNode(node.parentNode).id == node.id)
                place = " sib_right";

            else
                place = " sib_middle";
        }

        if(!this.firstNode(node))
            bottomLink = " no_bottomlink";

        node.firstChild.className = " blocknode" + place + bottomLink;
    },

    firstNode: function(node) {

        for(var cntNodes = 0; cntNodes < node.childNodes.length; cntNodes++) {
            if(node.childNodes[cntNodes].id.indexOf("tree_") >= 0)
                return node.childNodes[cntNodes];
        }

        return 0;
    },

    lastNode: function(node) {

        var lastNode = "";

        for(var cntItems = 0; cntItems < node.childNodes.length; cntItems++) {
            if(node.childNodes[cntItems].id.indexOf("tree_") >= 0)
                lastNode = node.childNodes[cntItems];
        }

        return lastNode;
    },

    childrenLength: function(node) {

        var numChildren = 0;

        for(var cntChild = 0; cntChild < node.childNodes.length; cntChild++) {
            if(node.childNodes[cntChild].id.indexOf("tree_") >= 0)
                numChildren += this.childrenLength(node.childNodes[cntChild]);
        }

        if(numChildren == 0)
            numChildren++;

        return numChildren;
    },

    editUserProfile : function() {
        var nodeData = this.mappedNodeStore.query("nodeid", this.selectedMappedNode).items[0].data;
        var usersRec = this.mappedRec;
        var userRecData = new usersRec({
            'image': nodeData["image"],
            'userid': nodeData["userid"],
            'employeeid': nodeData["employeeid"],
            'accno': nodeData["accno"],
            'username': nodeData["username"],
            'fname': nodeData["fname"],
            'lname': nodeData["lname"],
            'emailid': nodeData["emailid"],
            'contactno': nodeData["contactno"],
            'address': nodeData["address"],
            'designation': nodeData["designation"],
            'designationid': nodeData["designationid"],
            'department': nodeData["department"],
            'role': nodeData["role"],
            'roleid': nodeData["roleid"]
        });

        this.editUserC(userRecData);

    },

  editTo: function(name) {

       var checkBoxSM = new Wtf.grid.CheckboxSelectionModel({
            singleSelect: true
        });

        this.cmodel = new Wtf.grid.ColumnModel(
            [checkBoxSM,
            {
                header: "",
                width: 30,
                dataIndex:'image',
                hidden:true,
                renderer : function(value){
                    if(!value||value == ""){
                        value = Wtf.DEFAULT_USER_URL;
                    }
                    return String.format("<img src='{0}' style='height:18px;width:18px;vertical-align:text-top;'/>",value);
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.FirstName"),
                dataIndex: 'fname',
                renderer:function(val){
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.common.FirstName")+"'>"+val+"</div>";
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.LastName"),
                dataIndex: 'lname',
                renderer:function(val){
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.common.LastName")+"'>"+val+"</div>";
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.roles"),
                width: 70,
                dataIndex: 'role',
                renderer:function(val){
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.common.roles")+"'>"+val+"</div>";
                }
            }]);

        this.mappedGridStore = new Wtf.data.Store({
            url: 'OrganizationChart/getGridMappedUsers.common',
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            }, this.mappedRec)
        });

        this.mappedGridStore.load({
            params: {
                action : 5,        // for fetching mapped nodes for grid
                pid: this.projid,
                nodeid: this.selectedMappedNode,
                parentid: this.selectedMappedNodeParent
            }
        });

        this.MapGrid = new Wtf.grid.GridPanel({
            ds: this.mappedGridStore,
            cm: this.cmodel,
            sm: checkBoxSM,
            autoScroll: true,
            layout: 'fit',
            height: 300,
            viewConfig: {
                forceFit: true
            },
            border: false,
            autoWidth: true,
            loadMask: { 
                msg: WtfGlobal.getLocaleText("hrms.Dashboard.Loading")
            }
        });

        this.editToWindow = new Wtf.Window({
            resizable: false,
            scope: this,
            title: WtfGlobal.getLocaleText("hrms.common.edit.parent"),
            modal: true,
            layout: 'border',
            height: 500,
            width: 550,
            projid : this.projid,
            iconCls: 'pwndCommon iconwin',
            items: [
                {
                    region:'north',
                    height:90,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html: getTopHtml(WtfGlobal.getLocaleText({key:"hrms.common.edit.parent.for",params:[name]}), WtfGlobal.getLocaleText({key:"hrms.common.select.parent.node.1",params:[name]})+" <br>"+WtfGlobal.getLocaleText("hrms.common.click.edit.parent.button.2"))
                },{
                    region:'center',
                    layout:'fit',
                    border:false,
                    bodyStyle: 'background:#f1f1f1;font-size:10px;',
                    items:[this.MapGrid]
                }
           ],
            buttons: [{
                text: WtfGlobal.getLocaleText("hrms.common.edit.parent"),
                scope: this,
                handler: this.editUserTo
            },{
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope: this,
                handler: this.cancelEditTo
            }]
        });

        this.editToWindow.show();

        this.editToWindow.on("close", function() {
            var ChartPanel = Wtf.getCmp(this.projid + "chartContainer");
            ChartPanel.isSelected = false;
        });
    },

    pic: function(path) {
        if(path == '')
            path = '../../images/defaultuser.png';
        return '<img src= ' + path + ' height= "20px" width= "20px"></img>';
    },

    cancelEditTo: function() {
        this.isSelected = false;
        if(this.editToWindow)
            this.editToWindow.close();
    },

    editUserTo: function() {

        if(this.MapGrid.selModel.selections.length < 1)
           ResponseAlert([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.select.node.edit.parent")]);

        else {
            var selectNode = this.MapGrid.selModel.selections.items[0].data;

            var level = parseFloat(selectNode.level) + 1;

            this.updateNode(this.selectedMappedNode, selectNode.nodeid, level);
            this.isSelected = false;
        }
    },

    updateNode: function(nodeId, parentId, level) {
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.Savingdata"));
        Wtf.Ajax.requestEx({
            method: 'GET',
            url: "OrganizationChart/updateNode.common",
            params: ({
                action: 6,
                refid: this.projid,
                nodeid: nodeId,
                fromId: parentId,
                level: level
            })
        },
        this,
        function(result, req) {
            var data = eval( '(' + result.data + ')');
            this.reloadChartContainer();
            Wtf.updateProgress();
            if(!data.success){
                if(data.msg){
                    ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), data.msg]);
                }
                else{
                    ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occurred.editing.node")]);
                }
            }
            else {
                ResponseAlert([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.node.edited.successfully")]);
                
            }

            if(this.editToWindow)
                this.editToWindow.close();
        },
        function(result, req) {
            if(this.editToWindow)
                this.editToWindow.close();
            ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occurred.connecting.server")]);
        });
    },

    unmapNode: function(nodeId) {
        calMsgBoxShow(202,4,true);
        Wtf.Ajax.requestEx({
            method: "GET",
            url: "OrganizationChart/deleteNode.common",
            params: ({
                action: 7,
                nodeId: nodeId
            })
            },
        this,
        function(result, req) {
             var data = eval( '(' + result.data + ')');
            if(!data.success){
                if(data.msg){
                    ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), data.msg]);
                }
                else{
                    ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occurred.deleting.node")]);
                }
            }
            else {
                ResponseAlert([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.node.deleted.successfully")]);
                this.reloadChartContainer();

                var unmappedContainer = Wtf.getCmp(this.projid + "unmappedContainer");
                unmappedContainer.reloadUnmappedContainer();
            }
        },
        function(result, req) {
            ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occurred.connecting.server")]);
        });
    },

    makeDraggable: function(nodeId) {
        var blockId = "block_" + nodeId;
        var node = Wtf.get(blockId);
        node.dd = new Wtf.ChartDDProxy(blockId, "group");
    }
});
