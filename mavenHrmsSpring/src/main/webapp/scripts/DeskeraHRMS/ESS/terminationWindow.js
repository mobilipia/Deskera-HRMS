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
Wtf.terService = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        width:430,
        height:300,
        buttons: [
        {
            text:WtfGlobal.getLocaleText("hrms.common.Save"),
            handler: this.SaveRequest,
            scope:this
        },
        {
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.terService.superclass.constructor.call(this, config);

};

Wtf.extend(Wtf.terService, Wtf.Window, {
    initComponent: function() {
        Wtf.terService.superclass.initComponent.call(this);
    },
    loadAllStores:function(){

    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.terService.superclass.onRender.call(this, config);
                  
        this.terCmb = new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.TerminationCause")+'*',
            store:Wtf.terStore,
            mode:'local',
            valueField: 'id',
            displayField:'name',
            hiddenName:'tercause',
            triggerAction: 'all',
            typeAhead:true,
            allowBlank:false,
            width:200,
            addNewFn:this.addtermination.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(15,Wtf.terStore,"Add");
                },
                scope: this
            })]
        });

        if(!Wtf.StoreMgr.containsKey("ter")){
            Wtf.terStore.on("load",this.setReason,this);
            Wtf.terStore.load();
            Wtf.StoreMgr.add("ter",Wtf.terStore)
        }
        else{
            this.setReason();
        }

        this.terdesc=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.description"),
            width:200,
            name:'terdesc',
            maxLength:255
        });

        this.terdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.TerminationDate")+'*',
            width : 200,
            name:'relievedate',
            value:new Date(),
            allowBlank:false,
            format:'Y-m-d'
        });
          
        this.IneternalForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
            style: "background: transparent;padding-left:20px;padding-top: 20px;padding-right: 0px;",
            autoScroll:false,
            labelWidth :119,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.terdate,this.terCmb,this.terdesc
            ]
        })

        this.terminationpanel= new Wtf.Panel({            
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region :'north',
                    height : 80,
                    border : false,
                    cls : 'panelstyleClass1',
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.TerminationofService"), WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"),"../../images/termination-of-ser-popup.jpg")
                },{
                    border:false,
                    region:'center',
                    cls : 'formstyleClass2',
                    layout:"fit",
                    items: [
                    this.IneternalForm
                    ]
                }]
            }]
        });

        this.add(this.terminationpanel);
    }, 
    SaveRequest:function(){
        if(! this.IneternalForm.form.isValid()){
            return ;
        }else{
            var ids=[];
            if(this.grids.getSelectionModel().getCount()>0)
            	this.arr=this.grids.getSelectionModel().getSelections();
            this.grids.getSelectionModel().clearSelections();
            for(var i=0;i<this.arr.length;i++){
                var rec=this.grids.getStore().indexOf(this.arr[i]);
                WtfGlobal.highLightRow(this.grids,"33CC33",5, rec);
                ids.push(this.arr[i].get('userid'));
            }
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.common.Doyouwanttoterminateservicesoftheselectedemployees"),function(btn){
                if(btn=="yes") {
                    calMsgBoxShow(200,4,true);
                    this.IneternalForm.form.submit({
//                        url: Wtf.req.base + 'hrms.jsp',
                        url:"Common/terminateEmp.common",
                        params:{
                            flag:57,
                            ids:ids
                        },
                        scope:this,
                        success:function(req, resp){
                            if(resp.result.msg=="invalidterminatedate"){
                                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.Relievingdateafterjoindate")],1);
                            } else {
                            	if(resp.result.msg=="adminerror"){
                            		calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.SuperAdmincannotbeDeletedTerminationService")],2);
                            	}else{
                            		var params={
                            				start:0,
                            				limit:this.grids.pag.pageSize,
                            				ss: Wtf.getCmp("Quick"+this.grids.id).getValue()
                            		}
                            		WtfGlobal.delaytasks(this.grids.getStore(),params);
                            		this.close();
                            		calMsgBoxShow(155,0);
                            		var exgrid= Wtf.getCmp('exempgridexempdgr');
                            		if(exgrid!=null){
                                          exgrid.getStore().load();
                            		}
                            	}
                            }
                        },
                        failure:function(){ 
                            calMsgBoxShow(156,0);
                        }
                    });
                }else{                    
                    this.close();
                    this.grids.getSelectionModel().clearSelections();
                }
            },this);
        }

    },  
    addtermination:function(){
        WtfGlobal.showmasterWindow(15,Wtf.terStore,"Add");
    } ,
    setReason:function(){
        if(Wtf.terStore.getCount()>0){
            this.terCmb.setValue(Wtf.terStore.getAt(Wtf.terStore.getCount()-1).get('id'));
        }
        if(Wtf.terStore.getCount()==1){
        	this.terCmb.setValue("");
        }
    }
});
Wtf.rehire = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        width:430,
        height:260,
        buttons: [
        {
            text: WtfGlobal.getLocaleText("hrms.common.Save"),
            handler: this.RehireRequest,
            scope:this
        },
        {
            text:WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.rehire.superclass.constructor.call(this, config);

};

Wtf.extend(Wtf.rehire, Wtf.Window, {
    initComponent: function() {
        Wtf.rehire.superclass.initComponent.call(this);
    },    
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.rehire.superclass.onRender.call(this, config);
        this.templaterec=new Wtf.data.Record.create([
        {
            name:'name'
        },
        {
            name:'templateid'
        }
        ]);
//        this.templateStore =  new Wtf.data.Store({
////            url: Wtf.req.base + "PayrollHandler.jsp" ,
//            url:"Payroll/Template/getTemplistperDesign.py",
//            reader: new Wtf.data.KwlJsonReader({
//                root:'data'
//            },this.templaterec),
//            autoLoad : false
//        });
        
//        this.templateStore.on("load",function(){
//            if(this.templateStore.getCount()==0){
//                this.templateCmb.emptyText="No template assigned for selected designation";
//                this.templateCmb.reset();
//            } else {
//                this.templateCmb.emptyText="Select a template";
//                this.templateCmb.reset();
//            }
//        },this);

        this.descmb = new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.designation")+'*',
            store:Wtf.desigStore,
            mode:'local',
            valueField: 'id',
            displayField:'name',
            hiddenName:'desg',
            triggerAction: 'all',
            typeAhead:true,
            allowBlank:false,
            width:200,
            addNewFn:this.addDesignation.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
                },
                scope: this
            })]//,
//            listeners:{
//                scope:this,
//                Select:function(combo,record,index){
//                    this.templateStore.removeAll();
//                    this.templateStore.baseParams={
//                        type:'getTemplistperDesign'
//                    };
//                    this.templateStore.load({
//                        params:{
//                            desigid:record.get('id')
//                        }
//                    });
//                }
//            }
        });

        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.on("load",this.setReason,this);
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }
        else{
            this.setReason();
        }

        this.deptCmb = new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.department")+'*',
            store:Wtf.depStore,
            mode:'local',
            valueField: 'id',
            hiddenName:'dept',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,
            allowBlank:false,
            width:200,
            addNewFn:this.addDepartment.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    WtfGlobal.showmasterWindow(7,Wtf.depStore,"Add");
                },
                scope: this
            })]
        });

        if(!Wtf.StoreMgr.containsKey("dep")){
            Wtf.depStore.on("load",this.setDepartment,this);
            Wtf.depStore.load();
            Wtf.StoreMgr.add("dep",Wtf.depStore)
        }
        else{
            this.setDepartment();
        }

        this.salary=new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Salary")+'*',
            width:200,
            hidden:true,
            hideLabel:true,
            name:'salary',
            allowNegative:false,
            value:0,
            maxLength:10
        });

        this.joindate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.DateofJoining")+'*',
            width : 200,
            name:'joindate',
            value:new Date(),
            allowBlank:false,
            format:'Y-m-d'
        });      
//        this.templateCmb = new Wtf.form.ComboBox({
//            store:this.templateStore,
//            fieldLabel:'Salary template',
//            mode:'local',
//            hiddenName:'templateid',
//            name:'templateid',
//            width:200,
//            valueField: 'templateid',
//            displayField:'name',
//            triggerAction: 'all',
//            typeAhead:true
//        });

        this.rehireform = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
            style: "background: transparent;padding-left:20px;padding-top: 20px;padding-right: 0px;",
            autoScroll:false,
            labelWidth :119,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.deptCmb,this.descmb,this.joindate,this.salary//,this.templateCmb
            ]
        })

        this.rehirepanel= new Wtf.Panel({
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region :'north',
                    height : 80,
                    border : false,
                    cls : 'panelstyleClass1',
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.RehiretheExEmployee"), WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"),"../../images/rehire-popup.jpg")
                },{
                    border:false,
                    region:'center',
                    cls : 'formstyleClass2',
                    layout:"fit",
                    items: [
                    this.rehireform
                    ]
                }]
            }]
        });

        this.add(this.rehirepanel);
    },
    RehireRequest:function(){
        if(! this.rehireform.form.isValid()){
            return ;
        }else{
            var ids=[]
            var arr=this.grids.getSelectionModel().getSelections();
            this.grids.getSelectionModel().clearSelections();
            for(var i=0;i<arr.length;i++){
                var rec=this.grids.getStore().indexOf(arr[i]);
                WtfGlobal.highLightRow(this.grids,"33CC33",5, rec);
                ids.push(arr[i].get('userid'));
            }
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.common.Doyourehireselectedemployees"),function(btn){
                if(btn=="yes") {
                    calMsgBoxShow(200,4,true);
                    this.rehireform.form.submit({
                        //url: Wtf.req.base + 'hrms.jsp',
                        url:"Common/rehireEmp.common",
                        params:{
                            flag:58,
                            ids:ids
                        },
                        scope:this,
                        success:function(req, resp){
                            if(resp.result.msg=="invalidjoindate"){
                                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.Currentjoiningdateafterterminationdate")],1);
                            } else {
                                var params={
                                    start:0,
                                    limit:this.grids.pag.pageSize,
                                    ss: Wtf.getCmp("Quick"+this.grids.id).getValue()
                                }
                                WtfGlobal.delaytasks(this.grids.getStore(),params);
                                this.close();
                                calMsgBoxShow(158,0);
                                var exgrid= Wtf.getCmp('empmntgridqualifiedgr');
                                if(exgrid!=null){
                                    exgrid.getStore().load();
                                }
                            }
                        },
                        failure:function(){
                            calMsgBoxShow(159,0);
                        }
                    });
                }else{                    
//                    this.close();
//                    this.grids.getSelectionModel().clearSelections();
                }
            },this);
        }

    },
    addDesignation:function(){
        WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    },
    addDepartment:function(){
        WtfGlobal.showmasterWindow(7,Wtf.depStore,"Add");
    },
    setReason:function(){
        if(Wtf.desigStore.getCount()>0){
            this.descmb.setValue(Wtf.desigStore.getAt(Wtf.desigStore.getCount()-1).get('id'));
//            this.templateStore.baseParams={
//                type:'getTemplistperDesign'
//            };
//            this.templateStore.load({
//                params:{
//                    desigid:this.descmb.getValue()
//                }
//            });
        }
    }, 
    setDepartment:function(){
        if(Wtf.depStore.getCount()>0){
            this.deptCmb.setValue(Wtf.depStore.getAt(Wtf.depStore.getCount()-1).get('id'));
        }
    }
});

