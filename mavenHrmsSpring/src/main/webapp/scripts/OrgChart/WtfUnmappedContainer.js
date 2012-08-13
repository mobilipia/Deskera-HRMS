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
Wtf.UnmappedContainer = function(config) {
    Wtf.apply(this, config);

    this.selectedUnMappedNode = "";

    Wtf.UnmappedContainer.superclass.constructor.call(this);
};

Wtf.extend(Wtf.UnmappedContainer, Wtf.Panel, {

    initComponent : function(){
        Wtf.UnmappedContainer.superclass.initComponent.call(this);
    },

    onRender : function(ct){
        Wtf.UnmappedContainer.superclass.onRender.call(this, ct);

        this.unmappedRec = Wtf.data.Record.create([
                {name: 'projid'},
                {name: 'userid'},
                {name: 'image'},
                {name: 'fname'},
                {name: 'lname'},
                {name: 'designation'},
                {name: 'role'}
        ]);

        this.unMappedNodeStore = new Wtf.data.Store({
            url: 'OrganizationChart/getUnmappedUsers.common',
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            }, this.unmappedRec)
        });

        this.unMappedNodeStore.on("load", this.renderUnmappedNodes, this);
        this.unMappedNodeStore.load({
            params: {
                action: 1
            }
        });
    },

    reloadUnmappedContainer: function() {
        this.body.dom.innerHTML = "";
        this.unMappedNodeStore.reload();
    },

    renderUnmappedNodes: function(obj, rec, opt) {

        var displayName = "";
        var displayRole = "";
        var totalStore = rec.length;

        if(totalStore > 0) {
            for(var cntStore = 0; cntStore < totalStore; cntStore++) {
                displayName = rec[cntStore].data.fname + " " + rec[cntStore].data.lname;
                var displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,20);
                if(Wtf.isIE6 || Wtf.isIE7){
                    displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,10);
                }
                displayRole = rec[cntStore].data.role != "" ? rec[cntStore].data.role : "-";
                var displayRoleEllipsis = Wtf.util.Format.ellipsis(displayRole,13);
                var unmappedNode = new Wtf.ChartNode({
                    autoscroll: true,
                    blankString: "",
                    blockId: "blockUnMapped_",
                    nodeid : rec[cntStore].data.userid,
                    layout: 'fit',
                    mainDivStyle: " blockUnMapped ",
                    nodeClass : "blocknodeUnmapped",
                    nodeName: displayNameEllipsis,
                    nodeNameTip: displayName,
                    nodeDesignation: displayRoleEllipsis,
                    nodeDesignationTip: displayRole,
                    projid: this.projid,
                    renderTo: this.body.id
                });

                unmappedNode.on("unMappedNodeDblClick", function(unmappedNode, e, flag) {
                    var MainPanel = Wtf.getCmp(this.projid + "unmappedContainer");
                    MainPanel.selectedUnMappedNode = unmappedNode.nodeid;
                    MainPanel.addTo(unmappedNode.nodeName);
                });

                unmappedNode.on("mapClicked", function(unmappedNode, e, flag) {
                    var MainPanel = Wtf.getCmp(this.projid + "unmappedContainer");
                    MainPanel.selectedUnMappedNode = unmappedNode.nodeid;
                    MainPanel.addTo(unmappedNode.nodeName);
                });

                this.makeUnmappedDraggable(rec[cntStore].data.userid);
            }
            this.doLayout();
        }
    },

    addTo: function(name) {
            var checkBoxSM = new Wtf.grid.CheckboxSelectionModel({
                singleSelect: true
            });

            this.cmodel = new Wtf.grid.ColumnModel([checkBoxSM, {
                header: "",
                width: 30,
                dataIndex:'image',
                renderer: this.pic
            },{
                header: WtfGlobal.getLocaleText("hrms.common.FirstName"),
                dataIndex: 'fname'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.LastName"),
                dataIndex: 'lname'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.roles"),
                width: 70,
                dataIndex: 'role'
            }]);

            var mappedStore = Wtf.getCmp(this.projid + "chartContainer").mappedNodeStore;

            this.UnmapGrid = new Wtf.grid.GridPanel({
                ds: mappedStore,
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
                loadMask: { msg: WtfGlobal.getLocaleText("hrms.Dashboard.Loading") }
            });

            this.addToWindow = new Wtf.Window({
                title: WtfGlobal.getLocaleText("hrms.common.map.node"),
                modal: true,
                layout: 'border',
                height: 500,
                resizable: false,
                width: 550,
                iconCls: 'pwndCommon iconwin',
                items: [
                    {
                        region:'north',
                        height:90,
                        border : false,
                        bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                        html: getTopHtml(WtfGlobal.getLocaleText({key:"hrms.common.map.node.params",params:[name]}), WtfGlobal.getLocaleText({key:"hrms.common.select.parent.node.1",params:[name]})+" <br>"+WtfGlobal.getLocaleText("hrms.common.click.map.node.2"))
                    },{
                        region:'center',
                        layout:'fit',
                        border:false,
                        bodyStyle: 'background:#f1f1f1;font-size:10px;',
                        items:[this.UnmapGrid]
                    }
               ],
                buttons: [{
                    text: WtfGlobal.getLocaleText("hrms.common.map.node"),
                    scope: this,
                    handler: this.addUserTo
                },{
                    text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                    scope: this,
                    handler: this.cancelAddTo
                }]
            });
        
        this.addToWindow.show();
    },

    pic: function(path) {
        if(path == '')
            path = '../../images/defaultuser.png';
        return '<img src= ' + path + ' height= "20px" width= "20px"></img>';
    },

    cancelAddTo: function() {
        if(this.addToWindow)
            this.addToWindow.close();
    },

    addUserTo: function() {

        if(this.UnmapGrid.selModel.selections.length < 1)
            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.select.node.add.unmapped.node"));

        else {
            var selectNode = this.UnmapGrid.selModel.selections.items[0].data;

            var level = parseFloat(selectNode.level) + 1;

            var selectedRec = this.unMappedNodeStore.query("userid", this.selectedUnMappedNode);

            this.insertNewNode(selectedRec.items[0].data.userid, selectNode.nodeid, level);
        }
    },

    insertNewNode: function(userid, parentid, level) {
        Wtf.Ajax.requestEx({
            method: 'GET',
            url: "OrganizationChart/insertNode.common",
            params: ({
                action: 2,
                userid: userid,
                fromId: parentid,
                level: level
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
                    ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occurred.inserting.node")]);
                }
                var node = Wtf.get("node_" + parentid);
                node.removeClass("chartDragClass");
            }
            else {
                ResponseAlert([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.node.inserted.successfully")]);
                this.reloadUnmappedContainer();
                var chartContainer = Wtf.getCmp(this.projid + "chartContainer");
                chartContainer.reloadChartContainer();
            }

            if(this.addToWindow)
                this.addToWindow.close();
        },
        function(result, req) {
            ResponseAlert([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occurred.connecting.server")]);
            if(this.addToWindow)
                this.addToWindow.close();
        });
    },
 
    makeUnmappedDraggable: function(userid) {
        var blockId = "blockUnMapped_" + userid;
        var node = Wtf.get(blockId);
        node.dd = new Wtf.ChartDDProxy(blockId, "group");
    }
});
