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

Wtf.configRecruitment = function(config){
    Wtf.form.Field.prototype.msgTarget='qtip',
    Wtf.apply(this, config);
    this.typeStore = new Wtf.data.SimpleStore({
        fields :['id', 'name'],
        data:[[0,WtfGlobal.getLocaleText("hrms.masterconf.TextField")],/*[1,'Checkbox'],*/[2,WtfGlobal.getLocaleText("hrms.masterconf.DateField")],[3,WtfGlobal.getLocaleText("hrms.masterconf.Dropdown")],[4,WtfGlobal.getLocaleText("hrms.masterconf.TextArea")],[5,WtfGlobal.getLocaleText("hrms.masterconf.FileUpload")],[6,WtfGlobal.getLocaleText("hrms.masterconf.NumberField")],[7,WtfGlobal.getLocaleText("hrms.masterconf.EmailField")]]
    });
    this.typeStore1 = new Wtf.data.SimpleStore({
        fields :['id', 'name'],
        data:[['Personal',WtfGlobal.getLocaleText("hrms.recruitment.PersonalInformation")],['Contact',WtfGlobal.getLocaleText("hrms.recruitment.ContactInformation")],['Academic',WtfGlobal.getLocaleText("hrms.recruitment.AcademicInformation")],['Work',WtfGlobal.getLocaleText("hrms.recruitment.WorkExperience")],['other',WtfGlobal.getLocaleText("hrms.recruitment.OtherInformation")]]
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
        name: 'fieldname',
        type:'string'
    },{
        name: 'allownull'
    },{
        name: 'position'
    },{
        name: 'issystemproperty'
    },{
        name: 'visible'
    },{
        name: 'allownull'
    }]
    );
    this.ds = new Wtf.data.GroupingStore({
        url:"Rec/Job/getConfigRecruitment.rec",
        reader: this.reader
    });

    this.ds.baseParams = {
        flag: 212
    };

    this.sm = new Wtf.grid.CheckboxSelectionModel();

    this.cm = new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer({}),this.sm, {
        header: WtfGlobal.getLocaleText("hrms.masterconf.ConfigType"),
        dataIndex: 'configtype',
        width: 150,
        renderer: function(val) {
            if(val==0){
                return WtfGlobal.getLocaleText("hrms.masterconf.TextField");
            } else if(val==1){
                return WtfGlobal.getLocaleText("hrms.masterconf.CheckBox");
            } else if(val==2){
                return WtfGlobal.getLocaleText("hrms.masterconf.DateField");
            } else if(val==3){
                return WtfGlobal.getLocaleText("hrms.masterconf.Dropdown");


            }else if(val==4){
                return WtfGlobal.getLocaleText("hrms.masterconf.TextArea");
            }else if(val==5){
                return WtfGlobal.getLocaleText("hrms.masterconf.FileUpload");
            }else if(val==6){
                return WtfGlobal.getLocaleText("hrms.masterconf.NumberField");
            }else if(val==7){
                return WtfGlobal.getLocaleText("hrms.masterconf.EmailField")
            }
        }
    }, {
        header: WtfGlobal.getLocaleText("hrms.masterconf.Fieldname"),
        dataIndex: 'fieldname',
        sortable:true,
        width: 150
    }, {
        header: WtfGlobal.getLocaleText("hrms.masterconf.FormType"),
        dataIndex: 'formtype',
        width: 150,
        renderer: function(val){
            return WtfGlobal.getLocaleText("hrms.recruitment."+val)
        }
    }, {
        header:  WtfGlobal.getLocaleText("hrms.common.Position"),
        dataIndex: 'position',
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
        scope: this,
        iconCls:getButtonIconCls(Wtf.btype.addbutton),
        handler:function(){
            this.addConfig(true);
        },
        scope:this
    });

    this.editC = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.EditConfig"),
        scope: this,
        iconCls:getButtonIconCls(Wtf.btype.editbutton),
        disabled:true,
        handler:function(){
            this.addConfig(false);
        }
    });

    this.up = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.Up"),
        scope: this,
        disabled:true,
        iconCls:getButtonIconCls(Wtf.btype.upbutton),
        handler:function(){
            this.updown(true);
        }
    });

    this.down = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.Down"),
        scope: this,
        disabled:true,
        iconCls:getButtonIconCls(Wtf.btype.downbutton),
        handler:function(){
            this.updown(false);
        }
    });

    this.delC = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("hrms.masterconf.DeleteConfig"),
        scope: this,
        disabled:true,
        iconCls:getButtonIconCls(Wtf.btype.deletebutton),
        handler:function(){
            this.delConfig(true);
        },
        scope:this
    });

    this.setM = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("hrms.masterconf.SetMaster"),
        scope:this,
        iconCls:getButtonIconCls(Wtf.btype.setmasterbutton),
        handler:this.masterwin
    });
    this.cloneBttn=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("hrms.masterconf.CloneMaster"),
        scope:this,
        handler:function(){
            this.addconfig1();
        }
    })

    Wtf.configRecruitment.superclass.constructor.call(this, {
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
                    emptyText :  WtfGlobal.getLocaleText("hrms.common.SearchbyFieldname"),
                    field: "fieldname"
                })],
                bbar: ["-",this.addC,this.editC,this.delC,this.up,this.down,this.setM]

            })]

        }]
    });
    this.ds.load();
    this.sm.on("selectionchange",this.disableBttns,this);
    this.ds.on('load', function(store) {
        this.quickPanelSearch1.StorageChanged(store);
    }, this);
};

Wtf.extend(Wtf.configRecruitment, Wtf.Panel, {
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
                        url:"Rec/Job/addConfigRecruitmentType.rec",
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
                        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.masterconf.Configoptionaddedsuccessfully")], Wtf.MessageBox.INFO);
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
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Applyto"),
                                editable: false,
                                //name: 'type',
                                allowBlank: false,
                                anchor: '95%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: WtfGlobal.getLocaleText("hrms.masterconf.ApplyConfig")
                            }), this.quesField = new Wtf.form.TextField({
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Fieldname"),
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
                url:"Rec/Job/getConfigRecruitment.rec"
            }),
            reader: this.attributeReader
        });
        this.attributeStore.load({
            params:{
                configtype:"3,1"

            }
        });


        this.attributeCombo= new Wtf.form.ComboBox({
            triggerAction: 'all',
            store:this.attributeStore,
            mode:'local',
            width: 240,
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
            url:"Rec/Job/getConfigMaster.rec",
            reader: new Wtf.data.KwlJsonReader1({
                root:"data"
            }, this.masterReader)
        });
        this.addrec = new Wtf.Toolbar.Button({
                    text:WtfGlobal.getLocaleText("hrms.common.add"),
                    iconCls:getButtonIconCls(Wtf.btype.addbutton),
                    disabled:true,
                    tooltip: {
                        title: WtfGlobal.getLocaleText("hrms.common.add"),
                        text: WtfGlobal.getLocaleText("hrms.common.Clicktoaddnewrecord")
                    },
                    handler: function() {
                        if(this.masterText.getValue()!=""){
                        	var recFound=false;
                        	for(var i=0;i<this.masterds.getCount();i++){
                        		if(this.masterds.getAt(i).get('masterdata')== this.masterText.getValue()){
                        			recFound=true;
                        			break;
                        		}
                        	}
                            if(!recFound){
                            	Wtf.Ajax.request({
                                    url:"Rec/Job/addConfigMaster.rec",
                                    method: 'POST',
                                    params: {
                                        flag: 215,
                                        configid:this.attributeCombo.getValue(),
                                        masterdata:this.masterText.getValue()
                                    },
                                    success: function(response, e){
                                        this.masterText.setValue("");
                                        this.masterds.reload();
                                        calMsgBoxShow(219,0);
                                    },
                                    scope: this
                                })
                            }else{
                            	calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText({key:"hrms.masterconf.Masterrecordabcalreadypresent",params:[this.masterText.getValue()]})],2);
                            }
                        }else{
                            calMsgBoxShow(218,2);
                        }
                    },
                    scope: this
        });
        this.masterWin = new Wtf.Window({
            id: 'master' + this.id,
            title: WtfGlobal.getLocaleText("hrms.common.MasterRecord"),
            layout:'fit',
            iconCls: getButtonIconCls(Wtf.btype.winicon),
            modal: true,
            height:400,
            width:650,
            scope: this,
            items:[this.poppanel = new Wtf.Panel({
                id: 'masterpanel' + this.id,
                layout: 'fit',
                cls: 'backcolor',
                border: false,
                tbar: [this.attributeCombo,'-', WtfGlobal.getLocaleText("hrms.common.NewRecord")+': ', this.masterText = new Wtf.form.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("hrms.common.NewMasterRecord"),
                    anchor: '95%',
                    maxLength: 60,
                    id: this.id + 'masterText'
                }),'-',this.addrec,'-',
                this.delmaster = new Wtf.Toolbar.Button({
                    text:  WtfGlobal.getLocaleText("hrms.common.delete"),
                    disabled:true,
                    iconCls:getButtonIconCls(Wtf.btype.deletebutton),
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
         //   iconCls: 'winicon',
            modal: true,
            height:355,
            width:450,
            scope: this,
            buttons: [{
                text: flag1 ?  WtfGlobal.getLocaleText("hrms.common.add"):  WtfGlobal.getLocaleText("hrms.common.Update"),
                handler: function() {
            		if(this.quesField.getValue().trim()!=''){
                    Wtf.Ajax.requestEx({
                        method: 'POST',
//                        url: Wtf.req.base + "hrms.jsp",
                        url:"Rec/Job/addConfigRecruitmentType.rec",
                        params: {
                            flag:214,
                            configid: flag1 ?  "config":this.sm.getSelected().get("configid"),
                            //                            configtype: this.qType.getValue(),
                            formtype: this.qType1.getValue(),
                            configtype: this.qType.getValue(),
                            fieldname: this.quesField.getValue(),
                            visible:this.visible.checked,
                            issystemproperty:this.issystemproperty.checked,
                            allownull:this.nullchk.checked
                        }
                    },this,
                    function(responseObj,option) {
//                        var responseObj = eval('('+response+')');
                        if(responseObj.success=='msg'){
                            var title=responseObj.title;
                            var msg =responseObj.msg;
                            msgBoxShow([title, msg], Wtf.MessageBox.INFO);
                        }
                        else{
                            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), (flag1? WtfGlobal.getLocaleText("hrms.masterconf.Configoptionaddedsuccessfully"):WtfGlobal.getLocaleText("hrms.masterconf.Configoptioneditedsuccessfully"))], Wtf.MessageBox.INFO);
                        }
                        this.win1.close();
                        this.ds.load();
                    /*this.cloneStore.load({
                            params:{
                                flag:213
                            }
                        });*/
                    },function(){
                        msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27")], Wtf.MessageBox.ERROR);
                    }
                    )
            		}else{
            			this.quesField.setValue('');
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
                            labelWidth: 150,
                            height: 100,
                            buttonAlign: 'right',
                            items: [this.qType = new Wtf.form.ComboBox({
                                valueField: 'id',
                                displayField: 'name',
                                store: this.typeStore,
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.ConfigType"),
                                editable: false,
                                value:flag1 ? null:(this.sm.getSelected().get("configtype")),
                                //name: 'type',
                                allowBlank: false,
                                anchor: '80%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText:WtfGlobal.getLocaleText("hrms.masterconf.Selectconfigtype")
                            }),this.qType1 = new Wtf.form.ComboBox({
                                valueField: 'id',
                                displayField: 'name',
                                store: this.typeStore1,
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Applyto"),
                                editable: false,
                                value:flag1 ? null:(this.sm.getSelected().get("formtype")),
                                //name: 'type',
                                allowBlank: false,
                                anchor: '80%',
                                mode: 'local',
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: WtfGlobal.getLocaleText("hrms.masterconf.ApplyConfig")
                            }), this.quesField = new Wtf.form.TextField({
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Fieldname"),
                                scope: this,
                                allowBlank: false,
                                name: 'question',
                                anchor: '80%',
                                value:flag1 ? null:(this.sm.getSelected().get("fieldname")),
                                maxLength: 256
                            })/*, this.numField = new Wtf.form.NumberField({
                                fieldLabel: 'Max Length',
                                scope: this,
                                allowBlank: false,
                                name: 'question',
                                maxLength: 256,
                                allowNegative:false,
                                allowDecimal:false
                            })*/, this.issystemproperty = new Wtf.form.Checkbox({
                                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.Systemproperty"),
                                checked: flag1 ? false : (this.sm.getSelected().get("issystemproperty") == 1? true : false)
                            }),this.visible = new Wtf.form.Checkbox({
                                fieldLabel: WtfGlobal.getLocaleText("hrms.common.Visible"),
                                checked: flag1 ? false : (this.sm.getSelected().get("visible") == 1 ? true : false)
                            }),this.nullchk = new Wtf.form.Checkbox({
                                fieldLabel:  WtfGlobal.getLocaleText("hrms.common.AllowNull"),
                                checked: flag1 ? false : (this.sm.getSelected().get("allownull") == 1 ? true : false)
                            })]
                        })
                        ]
                    }

                    ]
                })
            })
            ]
        })
        this.win1.show();
        this.qType.clearInvalid();
        this.qType1.clearInvalid();
        this.quesField.clearInvalid();
    },

    delConfig: function(p){
        Wtf.Msg.show({
            title:(p==true)? WtfGlobal.getLocaleText("hrms.common.confirm"):WtfGlobal.getLocaleText("hrms.masterconf.DeleteMaster"),
            msg: WtfGlobal.getLocaleText("hrms.common.SelecteddatawillbedeletedDoyouwanttocontinue"),
            buttons: Wtf.Msg.YESNO,
            fn:(p==true)?this.confirmDelete:this.deletemaster,
            scope:this,
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
                url:"Rec/Job/deleteConfigMaster.rec",
                params:{
                    delid:delidArr
                },
                method:'POST'
            },
            this,
            function(responseObj,options){
                if(responseObj.success!=null){
                    if(responseObj.success=='msg'){
                        var title=responseObj.title;
                        var msg =responseObj.msg;
                        calMsgBoxShow([title, msg], Wtf.MessageBox.INFO);
                    }
                    else{
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),  WtfGlobal.getLocaleText("hrms.common.Recorddeletedsuccessfully")], Wtf.MessageBox.INFO);
                    }
                }
                else{
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),  WtfGlobal.getLocaleText("hrms.common.Recorddeletedsuccessfully")], Wtf.MessageBox.INFO);
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
                url:"Rec/Job/deleteConfigRecruitment.rec",
                params:{
                    delid:delidArr,
                    mode:'config',
                    flag:217
                },
                method:'POST'
            },
            this,
            function(respobj,options){
//                var respobj = eval('('+response+')');
                this.ds.reload();
                if(respobj.success!=null){
                    if(respobj.success=='msg'){
                        var title=respobj.title;
                        var msg=respobj.msg;
                        msgBoxShow([title,msg],Wtf.MessageBox.INFO);
                    } else {
                        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.masterconf.Configoptiondeletedsuccessfully")], Wtf.MessageBox.INFO);
                    }
                }
                else{
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.masterconf.Configoptiondeletedsuccessfully")], Wtf.MessageBox.INFO);
                }
                
            /*this.cloneStore.load({
                    params:{
                        flag:213
                    }
                });*/
            },function(){
                msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27")], Wtf.MessageBox.ERROR);
            })
        }
    },
    updown:function(isup){
            if(this.sm.getSelected().get("position") > 1 || (this.sm.getSelected().get("position") == 1 && !isup)){
                Wtf.Ajax.requestEx({
                    url:"Rec/Job/updownConfigRecruitment.rec",
                    params:{
                        configid:this.sm.getSelected().get("configid"),
                        position : this.sm.getSelected().get("position"),
                        formtype : this.sm.getSelected().get("formtype"),
                        positioninc:isup?-1:1
                    },
                    method:'POST'
                },
                this,
                function(respobj,options){
                    if(respobj.success!=null){
                        if(respobj.success=='msg'){
                            var title=respobj.title;
                            var msg=respobj.msg;
                            msgBoxShow([title,msg],Wtf.MessageBox.INFO);
                        }
                    }
                    else{
                        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.masterconf.Configoptionpositionchangedsuccessfully")], Wtf.MessageBox.INFO);
                    }
                    this.ds.reload();
                    this.up.disable();
                    this.down.disable();
                /*this.cloneStore.load({
                        params:{
                            flag:213
                        }
                    });*/
                },function(){
                    msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow27")], Wtf.MessageBox.ERROR);
                })
            }
    },
    disableBttns: function(obj){
        if(obj.getCount() > 1 ){
            this.delC.enable();
            this.editC.disable();
            this.up.disable();
            this.down.disable();
        }
        else if(obj.getCount() == 1){
            this.editC.enable();
            

            if(this.sm.getSelected().get("position") == 1){
                this.up.disable();
            }else{
                this.up.enable();
            }
            if(this.sm.getSelected().get("issystemproperty")){
                this.delC.disable();
            }else{
                this.delC.enable();
            }
            
            this.down.enable();
        }

        else{
            this.delC.disable();
            this.editC.disable();
            this.down.disable();
            this.up.disable();
        }

    },

    roleAfterEdit: function(e) {
    	var recFound=false;
    	for(var i=0;i<this.masterds.getCount();i++){
    		if(this.masterds.getAt(i).get('masterdata')== e.value && !this.mastersm.isSelected(i)){
    			recFound=true;
    			break;
    		}
    	}
        if(!recFound){
        	Wtf.Ajax.requestEx({
                url:"Rec/Job/addConfigMaster.rec",
                method: 'POST',
                params: {
                    flag: 215,
                    masterid:e.record.data.masterid,
                    configid:this.attributeCombo.getValue(),
                    masterdata:e.value
                }
            },
            this,
            function(respobj, e){
                if(respobj.success!=null){
                    if(respobj.success=='msg'){
                        var title=respobj.title;
                        var msg=respobj.msg;
//                        msgBoxShow([title,msg],Wtf.MessageBox.INFO);
                        this.masterds.commitChanges();
                        this.masterds.reload();
                    }
                }
            },function(){


                })
        }else{
        	this.masterds.reload();
        	calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText({key:"hrms.masterconf.Masterrecordabcalreadypresent",params:[e.value]})],2);
        	return ;
        }
    },
    masteronload:function(){
        this.masterds.removeAll();
    },
    masterload:function(){
        this.addrec.enable();
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
        Wtf.configRecruitment.superclass.onRender.call(this, config);
    }
});


