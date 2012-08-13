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
Wtf.common.WtfAuditTrail = function(config){
    Wtf.common.WtfAuditTrail.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.common.WtfAuditTrail,Wtf.Panel,{
 onRender : function(config){
     Wtf.common.WtfAuditTrail.superclass.onRender.call(this,config);
        
        this.groupingView1 = new Wtf.grid.GroupingView({
            forceFit: true,
            showGroupName: false,
            enableGroupingMenu: true,
            hideGroupedColumn: true
        });
    
    this.auditRecord = Wtf.data.Record.create([
        { 
            name: 'username',
            type: 'string'
        },{
            name: 'auditid',
            type: 'string'
        },{
            name: 'details',
            type: 'string'
        },{
            name: 'timestamp',
            type: 'date'
        },{
            name: 'ipaddr',
            type: 'string'
        }
    ]);
    
    
    this.auditReader = new Wtf.data.KwlJsonReader({
        root: "data",
         totalProperty:"count"
    }, this.auditRecord);
    
    this.auditStore = new Wtf.data.Store({
            proxy: new Wtf.data.HttpProxy({
//            url: Wtf.req.base+"UserManager.jsp"
            url:"Common/AuditTrail/getAuditData.common"
        }),
        reader: this.auditReader
    });
    
    this.cmodel = new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer({
            width:26
    }),
            {
                header: WtfGlobal.getLocaleText("hrms.common.Details"),
                width: 150,
                dataIndex: 'details'               
            }, {
                header: WtfGlobal.getLocaleText("hrms.common.User"),
                width: 150,
                dataIndex: 'username'
            }, {
                header: WtfGlobal.getLocaleText("hrms.AuditTrail.PerformedOn"),
                width: 150,
                renderer:WtfGlobal.dateRenderer,
                dataIndex: 'timestamp',
                align:'center'
            }, {
                header: WtfGlobal.getLocaleText("hrms.AuditTrail.IPAddress"),
                width: 150,
                dataIndex: 'ipaddr',
                groupable:true
            }]);
            
   this.grid=new Wtf.grid.GridPanel({
        ds: this.auditStore,
        cm: this.cmodel,
        border: false,
        loadMask:true,
        view: this.groupingView,
        trackMouseOver: true,
        viewConfig: {
            forceFit: true,
            emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.AuditTrail.Norecordstodisplay"))
        }
    });
    
    this.cmodel.defaultSortable = true;  
    
        this.comboReader = new Wtf.data.Record.create([
            {name: 'id', type: 'string'},
            {name: 'name', type: 'string'}
        ]);

    this.groupRecord = Wtf.data.Record.create([
            {   name: 'groupname',
                type: 'string'
            },{
                name: 'groupid',
                type: 'string'
            }
        ]);

        this.groupReader = new Wtf.data.KwlJsonReader({
            root: "data"
        }, this.groupRecord);

        this.groupStore = new Wtf.data.Store({
                proxy: new Wtf.data.HttpProxy({
//                url: Wtf.req.base+"UserManager.jsp"
                url:"Common/AuditTrail/getAuditGroupData.common"
            }),
            reader: this.groupReader
        });
      
        this.resetBttn=new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.reset"),
                iconCls:'pwndCommon updatebuttonIcon',
                tooltip :WtfGlobal.getLocaleText("hrms.AuditTrail.ResetSearchResults"),
                id: 'btnRec' + this.id,
                scope: this,
                disabled :false
        });
        this.resetBttn.on('click',this.handleResetClick,this);   
                 
                               
      var innerPanel = new Wtf.Panel({
            border : false,
            layout : "border",
            id : this.id + "_innerPanel",
            bodyStyle : "background:transparent;",
            items:[{
                border:false,
                layout:'border',
                //hidden:true,
                region:'north',
                height:70,
                bodyStyle : "background:transparent;",
                items : [{
                        region:'center',
                        layout:"column",
                        bodyStyle:'padding-left: 16px;padding-top: 16px',
                        border:false,
                        items:[{
                                border : false,
                                layout : "column",
                                columnWidth : 1,
                                bodyStyle : "background: transparent; padding: 8px;",
                                id:"searchAlert"+this.id,
                                items:[{
                                    layout : "form",
                                    border : false,
                                    columnWidth: .4,
                                    labelWidth :'50',
                                    items:[this.groupCombo=new Wtf.form.ComboBox({
                                                    id:'group' + this.id,
                                                    store : this.groupStore,
                                                    displayField:'groupname',
                                                    typeAhead:true,
                                                    mode: 'local',
                                                    triggerAction: 'all',
                                                    emptyText : WtfGlobal.getLocaleText("hrms.AuditTrail.Selectatransaction"),
                                                    fieldLabel : WtfGlobal.getLocaleText("hrms.AuditTrail.Transaction"),
                                                    name : 'groupid',
                                                    valueField:'groupid'
                                                })
                                    ]
                                },{
                                    layout : "form",
                                    border : false,
                                    columnWidth: .4,
                                    anchor: '90%',
                                    labelWidth :'50',
                                    items:[this.fT = new Wtf.form.TextField({
                                            fieldLabel : WtfGlobal.getLocaleText("hrms.AuditTrail.Contains"),
                                            width : 200})
                                    ]},{
                                    layout : "form",
                                    border : false,
                                    columnWidth: .1,
                                    anchor: '90%',
                                    labelWidth :'10',
                                    items:[this.bttn = new Wtf.Button({
                                            text: WtfGlobal.getLocaleText("hrms.common.search"),
                                            scope: this,
                                            handler: this.searchHandler
                                        })
                                    ]},{
                                        layout : "form",
                                        border : false,
                                        columnWidth: .1,
                                        anchor: '90%',
                                        labelWidth :'10',
                                        items:[this.bttnReset = new Wtf.Button({
                                                text: WtfGlobal.getLocaleText("hrms.common.reset"),
                                                scope: this,
                                                handler: this.resetHandler
                                            })
                                        ]}
                        ]}]}]
             },{
             region:'center',
             layout:'fit',
             items:[new Wtf.Panel({
                                    layout:'fit',
                                    border:false,
                                    items:[this.grid]})],
                                    /*tbar: ['Quick Search: ', this.quickPanelSearch = new Wtf.KWLTagSearch({
                                        width: 200,
                                        emptyText:'Search by User',
                                        field: "username"
                                    }),this.resetBttn],*/
                                     bbar: new Wtf.PagingSearchToolbar({
                                        id: 'pgTbar' + this.id,
//                                        searchField: this.quickPanelSearch,
                                        pageSize: 30,
                                        store: this.auditStore,
                                        displayInfo: true,
                                        plugins: this.pP =new Wtf.common.pPageSize({})
                                    })
            }]
        });
         this.auditStore.on('datachanged', function() {
            var p = this.pP.combo.value;
//            this.quickPanelSearch.setPage(p);
         }, this);
         this.auditStore.on('load',this.auditStoreload,this);
          this.add(innerPanel);
          this.auditStore.baseParams = {
            mode:41,
            groupid:this.groupCombo.getValue(),
            search:this.fT.getValue()
          };
          calMsgBoxShow(202,4,true);
          this.auditStore.load({
            params: {
                start:0,
                limit:30
               } 
            });
            this.groupStore.load({
            params: {
                mode:42
               } 
            });
        },
        auditStoreload:function(store){
            WtfGlobal.closeProgressbar();
//            this.quickPanelSearch.StorageChanged(store);
        },
        handleResetClick:function(){
            this.groupCombo.reset();
            this.fT.reset();
             this.auditStore.baseParams = {
            mode:41
          };
          this.auditStore.load({
            params: {
                start:0,
                limit:this.pP.combo.value
               } 
            });
        },
        searchHandler: function() {
          this.auditStore.removeAll();
          this.auditStore.baseParams = {
            mode:41,
            groupid:this.groupCombo.getValue(),
            search:this.fT.getValue()
          };
          this.auditStore.load({
            params: {
                start:0,
                limit:this.pP.combo.value
               } 
            });
        },
        resetHandler: function() {
        	this.groupCombo.setValue('');
        	this.groupCombo.clearValue();
        	this.fT.setValue("");
        	this.auditStore.baseParams = {
        			mode:41,
                    groupid:this.groupCombo.getValue(),
                    search:this.fT.getValue()
            };
            this.auditStore.load({
              params: {
                  start:0,
                  limit:this.pP.combo.value
                 } 
              });
          }

});

//var usertab = userTabs[0];
//userTabs.remove(usertab);
//var profiletab =
//usertab.add(profiletab);
//usertab.doLayout();
