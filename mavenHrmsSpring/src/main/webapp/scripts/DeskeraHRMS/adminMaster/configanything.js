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

Wtf.configAnyGrid = function(config){
    Wtf.form.Field.prototype.msgTarget='qtip',
    Wtf.apply(this, config);
    this.save = true;
    this.typeStore = new Wtf.data.SimpleStore({
        fields :['id', 'name'],
        data:[[0,WtfGlobal.getLocaleText("hrms.masterconf.TextField")],[1,WtfGlobal.getLocaleText("hrms.masterconf.NumberField")],[2,WtfGlobal.getLocaleText("hrms.masterconf.CheckBox")],[3,WtfGlobal.getLocaleText("hrms.masterconf.DateField")],[4,WtfGlobal.getLocaleText("hrms.masterconf.Dropdown")],[5,WtfGlobal.getLocaleText("hrms.masterconf.RichTextBox")],[6,WtfGlobal.getLocaleText("hrms.masterconf.TextArea")],[7,WtfGlobal.getLocaleText("hrms.masterconf.MultiSelectCombobox")]/*,[8,'File Upload']*/]
    });
    this.typeStore1 = new Wtf.data.SimpleStore({
        fields :['id', 'name'],
        data:[['Personal',WtfGlobal.getLocaleText("hrms.recruitment.PersonalInformation")],['Contact', WtfGlobal.getLocaleText("hrms.common.ContactandWorkShiftDetails")],['Organizational', WtfGlobal.getLocaleText("hrms.common.OrganizationalDetails")]]
    });
    this.groupingView = new Wtf.grid.GroupingView({
        forceFit: true,
        showGroupName: false,
        hideGroupedColumn: true,
        enableGroupingMenu: false
    });

    this.reader = new Wtf.data.KwlJsonReader1({
        root: 'data',
        totalProperty:'count'
    },
    [{
        name: 'configid'
    },{
        name: 'configtype'
    },{
        name: 'formtype'
    },{
        name: 'fieldname'
    },{
        name: 'allownull'
    },{
        name: 'blockemployees'
    }]
    );
    this.ds = new Wtf.data.GroupingStore({
//        url: Wtf.req.base + "hrms.jsp",
        url:"CustomCol/getConfigType.do",
        reader: this.reader
    });

    this.ds.baseParams = {
        flag: 212
    };

    this.ds.on("load",function(store,rec,opt){
        },this);

    this.sm = new Wtf.grid.CheckboxSelectionModel();

    this.cm = new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer({}),this.sm, {
        header:WtfGlobal.getLocaleText("hrms.masterconf.ConfigType"),
        dataIndex: 'configtype',
        width: 150,
        renderer: function(val) {
            if(val==0){
                return WtfGlobal.getLocaleText("hrms.masterconf.TextField");
            } else if(val==1){
                return WtfGlobal.getLocaleText("hrms.masterconf.NumberField");
            } else if(val==2){
                return WtfGlobal.getLocaleText("hrms.masterconf.CheckBox");
            } else if(val==3){
                return WtfGlobal.getLocaleText("hrms.masterconf.DateField");
            } else if(val==4){
                return WtfGlobal.getLocaleText("hrms.masterconf.Dropdown");
            } else if(val==5){
                return WtfGlobal.getLocaleText("hrms.masterconf.RichTextBox");
            }else if(val==6){
                return WtfGlobal.getLocaleText("hrms.masterconf.TextArea");
            }else if(val==7){
                return WtfGlobal.getLocaleText("hrms.masterconf.MultiSelectCombobox");
            }/*else if(val==8){
                return "File Upload";
            }*/
        }
    }, {
        header: WtfGlobal.getLocaleText("hrms.masterconf.Fieldname"),
        dataIndex: 'fieldname',
        width: 150
    }, {
        header: WtfGlobal.getLocaleText("hrms.masterconf.FormType"),
        dataIndex: 'formtype',
        width: 150
    }]);
    this.cm.defaultSortable = true;

    this.cloneRecord = Wtf.data.Record.create([{
        name: 'name',
        type: 'string'
    }, {
        name: 'displayname',
        type: 'string'
    }, {
        name: 'configid',
        type: 'string'
    }
    ]);

    this.cloneReader = new Wtf.data.KwlJsonReader1({
        root: "data"
    }, this.cloneRecord);

    this.cloneStore = new Wtf.data.Store({
        proxy: new Wtf.data.HttpProxy({
            url: Wtf.req.base + "hrms.jsp"
        }),
        reader: this.cloneReader
    });
    /*this.cloneStore.load({
        params:{
            flag:213
        }
    });*/

    this.addC = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.AddConfig"),
        iconCls:getButtonIconCls(Wtf.btype.addbutton),
        scope: this,
        handler:function(){
            this.addConfig(true);
        },
        scope:this
    });

    this.editC = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.EditConfig"),
        iconCls:getButtonIconCls(Wtf.btype.editbutton),
        scope: this,
        disabled:true,
        handler:function(){
            this.addConfig(false);
        },
        scope:this
    });

    this.delC = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.DeleteConfig"),
        iconCls:getButtonIconCls(Wtf.btype.deletebutton),
        scope: this,
        disabled:true,
        handler:function(){
            this.delConfig(true);
        },
        scope:this
    });

    this.setM = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("hrms.masterconf.SetMaster"),
        scope:this,
        handler:this.masterwin
    });
    this.cloneBttn=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("hrms.masterconf.CloneMaster"),
        scope:this,
        handler:function(){
            this.addconfig1();
        }
    })

    Wtf.configAnyGrid.superclass.constructor.call(this, {
        layout: 'fit',
        items: [{
            layout: 'fit',
            border: false,
            items: [this.grid = new Wtf.grid.GridPanel({
                border: false,
                region: 'center',
                store: this.ds,
                sm: this.sm,
                cm: this.cm,
                viewConfig: {
                    autoFill: true,
                    forceFit:true
                },
                loadMask: {
                    msg: WtfGlobal.getLocaleText("hrms.Dashboard.Loading")
                },
                tbar:[WtfGlobal.getLocaleText("hrms.common.QuickSearch")+': ', this.quickPanelSearch1 = new Wtf.KWLTagSearch({
                    width: 200,
                    emptyText : WtfGlobal.getLocaleText("hrms.common.SearchbyFieldname"),
                    field: "fieldname"
                })],
                bbar: ["-",this.addC,'-',this.editC,'-',this.delC]

            })]

        }]
    });
    this.ds.load();
    this.sm.on("selectionchange",this.disableBttns,this);
    this.ds.on('load', function(store) {
        this.quickPanelSearch1.StorageChanged(store);
    }, this);
};

Wtf.extend(Wtf.configAnyGrid, Wtf.Panel, {
    addconfig1:function(){
        this.win1 = new Wtf.Window({
            title:WtfGlobal.getLocaleText("hrms.masterconf.Clone"),
            layout:'fit',
            iconCls: 'winicon',
            modal: true,
            height:355,
            width:450,
            scope: this,
            buttons: [{
                text:  WtfGlobal.getLocaleText("hrms.common.add"),
                handler: function() {
                    Wtf.Ajax.requestEx({
                        method: 'POST',
//                        url: Wtf.req.base + "hrms.jsp",
                        url:"CustomCol/addConfigType.do",
                        params: {
                            flag:214,
                            configid:'clone',
                            //                            configtype:this.qType.getValue(),
                            formtype: this.qType1.getValue(),
                            fieldname: this.quesField.getValue()
                        //                            visible:this.visible.checked,
                        //                            disable:this.disable.checked,
                        //                            nullchk:this.nullchk.checked
                        }
                    },this,
                    function() {
                        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.masterconf.Configoptionaddedsuccessfully")], Wtf.MessageBox.INFO);
                        this.win1.close();
                        this.ds.load();
                        this.cloneStore.load({
                            params: {
                                flag: 213
                            }
                        });
                    },function(){
                        msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27")], Wtf.MessageBox.ERROR);
                    }
                    )
                },
                scope: this
            },{
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope:this,
                handler:function(){
                    this.win1.close();
                }
            }],
            items:[
            this.pPanel = new Wtf.Panel({
                layout: 'fit',
                border: false,
                items: this.inP = new Wtf.Panel({
                    layout: 'border',
                    border: false,
                    items: [{
                        region: 'north',
                        border: false,
                        height: 90,
                        bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                        html:getHeader('images/createuser.gif',WtfGlobal.getLocaleText("hrms.masterconf.Config"),WtfGlobal.getLocaleText("hrms.masterconf.Selectaconfigtype"))
                    },{
                        region: 'center',
                        layout: 'fit',
                        bodyStyle:"background:#f1f1f1;",
                        border: false,
                        items: [
                        this.addForm = new Wtf.form.FormPanel({
                            url: "jspfiles/admin/feedback.jsp",
                            region: "center",
                            bodyStyle: "padding: 10px;",
                            border: false,
                            labelWidth: 160,
                            height: 100,
                            buttonAlign: 'right',
                            items: [
                            /*this.qType = new Wtf.form.ComboBox({
                                valueField: 'configid',
                                displayField: 'displayname',
                                store: this.cloneStore,
                                fieldLabel:'Clone',
                                editable: false,
                                //name: 'type',
                                allowBlank: false,
                                anchor: '95%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: 'Select clone type'
                            }),*/this.qType1 = new Wtf.form.ComboBox({
                                valueField: 'id',
                                displayField: 'name',
                                store: this.typeStore1,
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Applyto")+'*',
                                editable: false,
                                //name: 'type',
                                allowBlank: false,
                                anchor: '95%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: WtfGlobal.getLocaleText("hrms.masterconf.ApplyConfig")
                            }), this.quesField = new Wtf.form.TextField({
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Fieldname")+'*',
                                scope: this,
                                allowBlank: false,
                                name: 'question',
                                maxLength: 256
                            })/*,this.numField = new Wtf.form.NumberField({
                                fieldLabel: 'Max Length',
                                scope: this,
                                allowBlank: false,
                                name: 'question',
                                maxLength: 256,
                                allowNegative:false,
                                allowDecimal:false
                            }), this.disable = new Wtf.form.Checkbox({
                                fieldLabel: 'Disable'
                            }),this.visible = new Wtf.form.Checkbox({
                                fieldLabel: 'Visible'
                            }),this.nullchk = new Wtf.form.Checkbox({
                                fieldLabel: 'Allow Null'
                            })*/]
                        })
                        ]
                    }

                    ]
                })
            })
            ]
        })
        this.win1.show();
    },

    masterwin:function(){
        this.attributeRecord = Wtf.data.Record.create([{
            name: 'displayname',
            type: 'string'
        }, {
            name: 'configid',
            type: 'string'
        }
        ]);

        this.attributeReader = new Wtf.data.KwlJsonReader1({
            root: "data"
        }, this.attributeRecord);

        this.attributeStore = new Wtf.data.Store({
            proxy: new Wtf.data.HttpProxy({
                url: Wtf.req.base + "hrms.jsp"
            }),
            reader: this.attributeReader
        });
        this.attributeStore.load({
            params:{
                flag:213
            }
        });


        this.attributeCombo= new Wtf.form.ComboBox({
            triggerAction: 'all',
            store:this.attributeStore,
            mode:'local',
            width: 150,
            listWidth:'240',
            forceSelection : true,
            typeAhead: true,
            displayField:'displayname',
            fieldLabel : WtfGlobal.getLocaleText("hrms.masterconf.Attribute"),
            hiddenName  : 'configid',
            allowBlank:false,
            valueField:'configid',
           emptyText:WtfGlobal.getLocaleText("hrms.masterconf.SelectanAttribute")
        });

        this.mastersm = new Wtf.grid.CheckboxSelectionModel();
        this.mastercm = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),this.sm,
            {
                header: WtfGlobal.getLocaleText("hrms.common.MasterRecord"),
                dataIndex: 'masterdata',
                editor: new Wtf.form.TextField({
                    allowBlank: false,
                    maxLength: 100,
                    items:[{
                        text: WtfGlobal.getLocaleText("hrms.common.delete"),
                        tooltip: {
                            title: WtfGlobal.getLocaleText("hrms.common.delete"),
                            text:  WtfGlobal.getLocaleText("hrms.common.Clicktodeleterecord")
                        }
                    }]
                })
            },{
                header: WtfGlobal.getLocaleText("hrms.common.status"),
                dataIndex: 'status',
                renderer: function(val, css, record, row, column, store) {
                    if(val=='1'){
                        return WtfGlobal.getLocaleText("hrms.common.Active");
                    }else {
                        return WtfGlobal.getLocaleText("hrms.common.Inactive");
                    }
                },
                hidden:true
            }]);

        this.masterReader = new Wtf.data.Record.create([
        {
            name: 'masterid'
        },

        {
            name: 'masterdata'
        },

        {
            name: 'status'
        }
        ]);

        this.masterds = new Wtf.data.Store({
            url: Wtf.req.base + "hrms.jsp",
            reader: new Wtf.data.KwlJsonReader1({
                root:"data"
            }, this.masterReader)
        });

        this.masterWin = new Wtf.Window({
            id: 'master' + this.id,
            title: WtfGlobal.getLocaleText("hrms.common.MasterRecord"),
            layout:'fit',
            iconCls: 'winicon',
            modal: true,
            height:400,
            width:600,
            scope: this,
            items:[this.poppanel = new Wtf.Panel({
                id: 'masterpanel' + this.id,
                layout: 'fit',
                cls: 'backcolor',
                border: false,
                tbar: [this.attributeCombo,'-', WtfGlobal.getLocaleText("hrms.common.NewRecord")+': ', this.masterText = new Wtf.form.TextField({
                    fieldLabel:WtfGlobal.getLocaleText("hrms.common.NewMasterRecord"),
                    anchor: '95%',
                    maxLength: 60,
                    id: this.id + 'masterText'
                }),'-',{
                    text:WtfGlobal.getLocaleText("hrms.common.add"),
                    tooltip: {
                        title: WtfGlobal.getLocaleText("hrms.common.add"),
                        text: WtfGlobal.getLocaleText("hrms.common.Clicktoaddnewrecord")
                    },
                    handler: function() {
                        Wtf.Ajax.request({
                            url: Wtf.req.base + "hrms.jsp",
                            method: 'POST',
                            params: {
                                flag: 215,
                                masterid:"",
                                configid:this.attributeCombo.getValue(),
                                masterdata:this.masterText.getValue()
                            },
                            success: function(response, e){
                                this.masterText.setValue("");
                                this.masterds.reload();
                                this.cloneStore.load({
                                    params:{
                                        flag:213
                                    }
                                });
                            },
                            scope: this
                        })
                    },
                    scope: this
                },'-',
                this.delmaster = new Wtf.Toolbar.Button({
                    text:  WtfGlobal.getLocaleText("hrms.common.delete"),
                    disabled:true,
                    tooltip: {
                        title:  WtfGlobal.getLocaleText("hrms.common.delete"),
                        text:  WtfGlobal.getLocaleText("hrms.common.Clicktodeleterecord")
                    },
                    handler: function() {
                        this.delConfig(false);
                    },
                    scope:this
                })],
                items: [this.addmaster = new Wtf.Panel({
                    id: 'addmaster' + this.id,
                    layout: 'fit',
                    border: false,
                    items: [this.masterGrid = new Wtf.grid.EditorGridPanel({
                        id: 'mastergrid' + this.id,
                        store: this.masterds,
                        sm:this.mastersm,
                        cm: this.mastercm,
                        border: false,
                        clicksToEdit: 1,
                        viewConfig: {
                            forceFit: true
                        }
                    })]
                })]
            })]
        })
        this.mastersm.on("selectionchange",this.handleBttns,this);
        this.masterWin.show();
        this.attributeCombo.on('select',this.masterload,this);
        this.masterGrid.on('afteredit',this.roleAfterEdit,this);
        //        this.masterGrid.on('beforeedit',this.roleBeforeEdit,this);
        this.masterds.on('loadException',this.masteronload,this);
    },
    handleBttns: function(obj){
        this.delmaster.disable();
        if(obj.getCount() > 0){
            this.delmaster.enable();
        }else{
            this.delmaster.disable();
        }
    },

    addConfig: function(flag1){
        this.win1 = new Wtf.Window({
            title:WtfGlobal.getLocaleText("hrms.masterconf.Config"),
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
           // iconCls: 'winicon',
            modal: true,
            height:255,
            width:450,
            scope: this,
            buttons: [{
                text: flag1 ?  WtfGlobal.getLocaleText("hrms.common.add"):  WtfGlobal.getLocaleText("hrms.common.Update"),
                id:'btnsave',
                handler: function() {
                    if(this.qType1.isValid() && this.quesField.isValid() && this.save){
                        var saveelement = Wtf.get('btnsave');
                        saveelement.findParent('table').className = "x-btn-wrap x-btn x-item-disabled";
                        this.save = false;
                        Wtf.Ajax.requestEx({
                            method: 'POST',
    //                        url: Wtf.req.base + "hrms.jsp",
                            url:"CustomCol/addConfigType.do",
                            params: {
                                flag:214,
                                configid: flag1 ?  "config":this.sm.getSelected().get("configid"),
                                //                            configtype: this.qType.getValue(),
                                formtype: this.qType1.getValue(),
                                fieldname: this.quesField.getValue(),
                                blockemployees: this.blockemployees.getValue()
                            //                            visible:this.visible.checked,
                            //                            disable:this.disable.checked,
                            //                            nullchk:this.nullchk.checked
                            }
                        },this,
                        function(responseObj,option) {
    //                        var responseObj = eval('('+response+')');
                            if(responseObj.success=='msg'){
                                var title=responseObj.comboTitle;
                                var msg =responseObj.msg;
                                msgBoxShow([title, msg], Wtf.MessageBox.INFO);
                            }
                            else{
                               msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), (flag1? WtfGlobal.getLocaleText("hrms.masterconf.Configoptionaddedsuccessfully"):WtfGlobal.getLocaleText("hrms.masterconf.Configoptioneditedsuccessfully"))], Wtf.MessageBox.INFO);
                            }
                            this.win1.close();
                            this.save = true;
                            this.ds.load();
                        /*this.cloneStore.load({
                                params:{
                                    flag:213
                                }
                            });*/
                        },function(){
                             msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27")], Wtf.MessageBox.ERROR);
                            this.save = true;
                            saveelement.findParent('table').className = "x-btn-wrap x-btn";
                        }
                        )
                    } else {
                        calMsgBoxShow(5,0);
                    }
                },
                scope: this
            },{
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope:this,
                handler:function(){
                    this.win1.close();
                }
            }],
            items:[
            this.pPanel = new Wtf.Panel({
                layout: 'fit',
                border: false,
                items: this.inP = new Wtf.Panel({
                    layout: 'border',
                    border: false,
                    items: [{
                        region: 'north',
                        border: false,
                        height: 90,
                        bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                        html:getHeader(flag1 ?'../../images/add-config.jpg':'../../images/edit-config.jpg',WtfGlobal.getLocaleText("hrms.masterconf.Config"),WtfGlobal.getLocaleText("hrms.masterconf.Selectaconfigtype"))
                    },{
                        region: 'center',
                        layout: 'fit',
                        bodyStyle:"background:#f1f1f1;",
                        border: false,
                        items: [
                        this.addForm = new Wtf.form.FormPanel({
                            url: "jspfiles/admin/feedback.jsp",
                            region: "center",
                            bodyStyle: "padding: 10px;",
                            border: false,
                            labelWidth:175,
                            height: 100,
                            buttonAlign: 'right',
                            items: [/*this.qType = new Wtf.form.ComboBox({
                                valueField: 'id',
                                displayField: 'name',
                                store: this.typeStore,
                                fieldLabel: 'Config Type',
                                editable: false,
                                value:flag1 ? null:(this.sm.getSelected().get("configtype")),
                                //name: 'type',
                                allowBlank: false,
                                anchor: '95%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: 'Select config type...'
                            }),*/this.qType1 = new Wtf.form.ComboBox({
                                valueField: 'id',
                                displayField: 'name',
                                store: this.typeStore1,
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Applyto")+'*',
                                editable: false,
                                value:flag1 ? null:(this.sm.getSelected().get("formtype")),
                                //name: 'type',
                                allowBlank: false,
                                anchor: '95%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: WtfGlobal.getLocaleText("hrms.masterconf.ApplyConfig")
                            }), this.quesField = new Wtf.form.TextField({
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Fieldname")+'*',
                                scope: this,
                                anchor: '95%',
                                allowBlank: false,
                                name: 'question',
                                value:flag1 ? null:(this.sm.getSelected().get("fieldname")),
                                maxLength: 256
                            }),this.blockemployees=new Wtf.form.Checkbox({
                                fieldLabel:WtfGlobal.getLocaleText("hrms.admin.block.employees.edit.details")+'  ',
                                name:'blockemployees'
                            })/*, this.numField = new Wtf.form.NumberField({
                                fieldLabel: 'Max Length',
                                scope: this,
                                allowBlank: false,
                                name: 'question',
                                maxLength: 256,
                                allowNegative:false,
                                allowDecimal:false
                            }), this.disable = new Wtf.form.Checkbox({
                                fieldLabel: 'Disable',
                                checked: flag1 ? false : (this.sm.getSelected().get("disable") == 1? true : false)
                            }),this.visible = new Wtf.form.Checkbox({
                                fieldLabel: 'Visible',
                                checked: flag1 ? false : (this.sm.getSelected().get("visible") == 1 ? true : false)
                            }),this.nullchk = new Wtf.form.Checkbox({
                                fieldLabel: 'Allow Null',
                                checked: flag1 ? false : (this.sm.getSelected().get("allownull") == 1 ? true : false)
                            })*/]
                        })
                        ]
                    }

                    ]
                })
            })
            ]
        })
        this.win1.show();
        this.qType1.clearInvalid();
        this.quesField.clearInvalid();
        this.blockemployees.setValue(flag1 ? false:((this.sm.getSelected().get("blockemployees"))==1?true:false));
    },

    delConfig: function(p){
        Wtf.Msg.show({
            title:(p==true)? WtfGlobal.getLocaleText("hrms.common.confirm"):WtfGlobal.getLocaleText("hrms.masterconf.DeleteMaster"),
            msg: WtfGlobal.getLocaleText("hrms.common.SelecteddatawillbedeletedDoyouwanttocontinue"),
            buttons: Wtf.Msg.YESNO,
            fn:(p==true)?this.confirmDelete:this.deletemaster,
            scope:this,
            //animEl: 'elId',
            icon: Wtf.MessageBox.QUESTION
        });
    },
    deletemaster: function(obj){
        if(obj == 'yes'){
            var delid = [];
            var tempdel = this.mastersm.getSelections();
            for(var i = 0;i < this.mastersm.getSelections().length;i++){

                delid.push(this.masterGrid.getSelections()[i].data["masterid"]);

            }
            var delidArr = Wtf.encode(delid);
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp",
                url:"CustomCol/deleteConfig.do",
                params:{
                    delid:delidArr,
                    mode:'master',
                    flag:217
                },
                method:'POST'
            },
            this,
            function(response,options){
                var responseObj = eval('('+response+')');
                if(responseObj.success=='msg'){
                    var title=responseObj.title;
                    var msg =responseObj.msg;
                    msgBoxShow([title, msg], Wtf.MessageBox.INFO);
                }
                else{
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.masterconf.Masterdatadeletedsuccessfully")], Wtf.MessageBox.INFO);
                }
                this.masterds.reload();
            },function(){
                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27")], Wtf.MessageBox.ERROR);
            })
        }

    },
    confirmDelete:function(obj){
        if(obj == 'yes'){
            var delid = [];
            for(var i = 0;i < this.sm.getSelections().length;i++){
                delid.push(this.grid.getSelections()[i].data['configid']);
            }
            var delidArr = Wtf.encode(delid);
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp",
                url:"CustomCol/deleteConfig.do",
                params:{
                    delid:delidArr,
                    mode:'config',
                    flag:217
                },
                method:'POST'
            },
            this,
            function(response){
                var respobj = eval('('+response+')');
                if(respobj.success) {
                     msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.masterconf.Configoptiondeletedsuccessfully")], Wtf.MessageBox.INFO);
                    this.ds.reload();
                }
            
            },function(){
                calMsgBoxShow(27,1);
            })
        }
    },

    disableBttns: function(obj){
        if(obj.getCount() > 1){
            this.delC.enable();
            this.editC.disable();
        }
        else if(obj.getCount() == 1){
            this.editC.enable();
            this.delC.enable();
        }

        else{
            this.delC.disable();
            this.editC.disable();
        }
    },
    /*roleBeforeEdit: function(e) {
        if(this.mastersm.getSelected() && this.mastersm.getSelected().get('status') ==0 && this.attributeCombo.getValue() == "coursetrainmode"){
            e.cancel=true;
            Wtf.Msg.alert('Alert', 'Cannot be edited. Inactive training mode');
        }
    },*/

    roleAfterEdit: function(e) {
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "hrms.jsp",
            method: 'POST',
            params: {
                flag: 215,
                masterid:e.record.data.masterid,
                configid:this.attributeCombo.getValue(),
                masterdata:e.value
            }
        },
        this,
        function(response, e){
            var respobj = eval('('+response+')');
            if(respobj.success!=null){
                if(respobj.success=='msg'){
                    var title=respobj.title;
                    var msg=respobj.msg;
                    msgBoxShow([title,msg],Wtf.MessageBox.INFO);
                    this.masterds.reload();
                }
            }
        },function(){


            })
    },
    masteronload:function(){
        this.masterds.removeAll();
    },
    masterload:function(){
        if(this.attributeCombo.getValue() == "coursetrainmode"){
            this.mastercm.setHidden(3,false);
        }
        else{
            this.mastercm.setHidden(3,true);
        }
        this.masterds.load({
            params:{
                flag:216,
                configid:this.attributeCombo.getValue()
            }
        })
    },
    onRender: function(config) {
        Wtf.configAnyGrid.superclass.onRender.call(this, config);
    }
});


