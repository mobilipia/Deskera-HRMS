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
Wtf.common.Features=function(config){
    Wtf.common.Features.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.common.Features, Wtf.Panel, {
    layout:'border',
    defaults:{split:true,border:false},
    initComponent:function(config){
        Wtf.apply(this,{
            bbar:[{
                text:WtfGlobal.getLocaleText("hrms.common.add.feature"),
                handler:function(){this.showFeatureForm(false)},
                scope:this
            },{
                text:WtfGlobal.getLocaleText("hrms.common.edit.feature"),
                handler:function(){this.showFeatureForm(true)},
                scope:this
            },{
                text:WtfGlobal.getLocaleText("hrms.common.delete.feature"),
                handler:this.deleteFeature,
                scope:this
            },'->',{
                text:WtfGlobal.getLocaleText("hrms.common.add.activity"),
                handler:function(){this.showActivityForm(false)},
                scope:this
            },{
                text:WtfGlobal.getLocaleText("hrms.common.edit.activity"),
                handler:function(){this.showActivityForm(true)},
                scope:this
            },{
                text:WtfGlobal.getLocaleText("hrms.common.delete.activity"),
                handler:this.deleteActivity,
                scope:this
            }]
        });
        Wtf.common.Features.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.common.Features.superclass.onRender.call(this,config);
        this.featureRecord=new Wtf.data.Record.create(['featureid','featurename','displayfeaturename']);
        this.featureStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:1
            },
            reader: new Wtf.data.KwlJsonReader({
                        root: 'data'
                    },this.featureRecord)
        });
        this.featureGrid=new Wtf.grid.GridPanel({
            region:'west',
            width:'40%',
            store:this.featureStore,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            viewConfig:{forceFit:true},
            layout:'fit',
            columns:[
                {header:'Feature Name',dataIndex:'featurename'},
                {header:'Feature Display Name',dataIndex:'displayfeaturename'}
            ]
        });

        this.activityRecord=new Wtf.data.Record.create(['activityid','featureid','activityname','displayactivityname']);
        this.activityStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:2
            },
            reader: new Wtf.data.KwlJsonReader({
                        root: 'data'
                    },this.activityRecord)
        });
        this.activityGrid=new Wtf.grid.GridPanel({
            region:'center',
            store:this.activityStore,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            layout:'fit',
            viewConfig:{forceFit:true},
            columns:[
                {header:WtfGlobal.getLocaleText("hrms.common.activity.name"),dataIndex:'activityname'},
                {header:WtfGlobal.getLocaleText("hrms.common.activity.display.name"),dataIndex:'displayactivityname'}
            ]
        });

        this.add(this.featureGrid);
        this.add(this.activityGrid);
        this.featureGrid.on('rowclick',this.filterActivities, this);

        this.featureStore.on('load',this.loadActivities, this);

        this.featureStore.load();
    },

    loadActivities:function(){
        this.activityStore.on('load',function(){
            this.activityStore.filter('featureid',/^[\0]*$/);
        }, this);
        this.activityStore.load();
    },

    filterActivities:function(){
        var featureid= this.featureGrid.getSelectionModel().getSelected().get('featureid');
        this.activityStore.filter('featureid',featureid);
    },

    showFeatureForm:function(isEdit){
        var rec=null;
        if(isEdit){
            if(this.featureGrid.getSelectionModel().hasSelection()==false){
                Wtf.MessageBox.alert("Edit Feature", "Please select a feature to edit.");
                return;
            }

            rec = this.featureGrid.getSelectionModel().getSelected();
        }

        this.createFeatureWindow(rec,isEdit);
    },

    createFeatureWindow:function(rec,isEdit){
        this.form=new Wtf.form.FormPanel({
            frame:true,
            url: Wtf.req.base+'UserManager.jsp?mode=3',
            labelWidth: 125,
            autoHeight:true,
            bodyStyle:'padding:5px 5px 0',
            autoWidth:true,
            defaults: {width: 175},
            defaultType: 'textfield',
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.feature.name"),
                name:'featurename',
                maskRe:/[A-Za-z_]+/,
                validator:WtfGlobal.noBlankCheck,
                allowBlank:false
            },{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.feature.display.name"),
                name:'displayfeaturename',
                validator:WtfGlobal.noBlankCheck,
                allowBlank:false
            }],
            buttons:[{
                text: WtfGlobal.getLocaleText("hrms.common.Save"),
                handler:function(){
                    this.form.getForm().submit({
                        waitMsg:WtfGlobal.getLocaleText("hrms.common.saving.feature"),
                        scope:this,
                        success:function(f,a){this.win.close();this.genSuccessResponse(eval('('+a.response.responseText+')'))},
                        failure:function(f,a){this.win.close();this.genFailureResponse(eval('('+a.response.responseText+')'))}
                    });

                },
                scope:this
            }]
        });
        this.form.add({xtype:'hidden', name:'featureid'});

        this.win=new Wtf.Window({
            title: (isEdit?WtfGlobal.getLocaleText("hrms.common.edit"):WtfGlobal.getLocaleText("hrms.activityList.add"))+' '+WtfGlobal.getLocaleText("hrms.common.feature"),
            closable:true,
            autoWidth:true,
            autoHeight:true,
            plain:true,
            modal:true,
            items:this.form
        });this.win.show();
        if(isEdit)this.form.getForm().loadRecord(rec);
    },

    genSuccessResponse:function(response){
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.project.feature"),response.msg);
        if(response.success==true)this.featureStore.reload();
    },

    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.project.feature"),msg);
    },

    deleteFeature:function(){
        if(this.featureGrid.getSelectionModel().hasSelection()==false){
            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.select.feature.delete"));
            return;
        }
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.delete.selected.feature"), function(btn){
            if(btn!="yes") return;
            var rec = this.featureGrid.getSelectionModel().getSelected();
            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp',
                params: {
                    mode:5,
                    featureid:rec.get('featureid')
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);

    },

    showActivityForm:function(isEdit){
        var rec=null;
        if(isEdit){
            if(this.activityGrid.getSelectionModel().hasSelection()==false){
                Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.edit.activity"), WtfGlobal.getLocaleText("hrms.common.select.activity.edit"));
                return;
            }

            rec = this.activityGrid.getSelectionModel().getSelected();
        }

        this.createActivityWindow(rec,isEdit);
    },

    createActivityWindow:function(rec,isEdit){
        this.aform=new Wtf.form.FormPanel({
            frame:true,
            url: Wtf.req.base+'UserManager.jsp?mode=4',
            labelWidth: 125,
            autoHeight:true,
            bodyStyle:'padding:5px 5px 0',
            autoWidth:true,
            defaults: {width: 175},
            defaultType: 'textfield',
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.activity.name"),
                name:'activityname',
                maskRe:/[A-Za-z_]+/,
                validator:WtfGlobal.noBlankCheck,
                allowBlank:false
            },{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.activity.display.name"),
                name:'displayactivityname',
                validator:WtfGlobal.noBlankCheck,
                allowBlank:false
            },new Wtf.form.ComboBox({
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.feature"),
                hiddenName:'featureid',
                store:this.featureStore,
                mode:'local',
                valueField:'featureid',
                displayField:'displayfeaturename',
                disableKeyFilter:true,
                triggerAction:'all',
                forceSelection:true,
                readOnly:isEdit,
                allowBlank:false
            })],
            buttons:[{
                text: WtfGlobal.getLocaleText("hrms.common.Save"),
                handler:function(){
                    this.aform.getForm().submit({
                        waitMsg:WtfGlobal.getLocaleText("hrms.common.saving.activity"),
                        scope:this,
                        success:function(f,a){this.awin.close();this.genSuccessResponse(eval('('+a.response.responseText+')'))},
                        failure:function(f,a){this.awin.close();this.genFailureResponse(eval('('+a.response.responseText+')'))}
                    });

                },
                scope:this
            }]
        });
        this.aform.add({xtype:'hidden', name:'activityid'})

        this.awin=new Wtf.Window({
            title: (isEdit?WtfGlobal.getLocaleText("hrms.activityList.edit"):WtfGlobal.getLocaleText("hrms.activityList.add"))+' '+WtfGlobal.getLocaleText("hrms.common.activity"),
            closable:true,
            autoWidth:true,
            autoHeight:true,
            plain:true,
            modal:true,
            items:this.aform
        });this.awin.show();
        if(isEdit)this.aform.getForm().loadRecord(rec);
    },

    deleteActivity:function(){
        if(this.activityGrid.getSelectionModel().hasSelection()==false){
            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.select.activity.delete"));
            return;
        }
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.delete.selected.activity"), function(btn){
            if(btn!="yes") return;
            var rec = this.activityGrid.getSelectionModel().getSelected();
            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp',
                params: {
                    mode:6,
                    activityid:rec.get('activityid')
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
    }
});
