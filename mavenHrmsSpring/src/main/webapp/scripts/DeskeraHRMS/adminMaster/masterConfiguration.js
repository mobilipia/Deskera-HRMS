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

Wtf.MasterConfigurator = function (config){
    Wtf.apply(this,config);
    Wtf.MasterConfigurator.superclass.constructor.call(this);
}

Wtf.extend(Wtf.MasterConfigurator,Wtf.Panel,{
    initComponent:function (){
        this.weekcount = 0;
        Wtf.MasterConfigurator.superclass.initComponent.call(this);
        calMsgBoxShow(202,4,true);
        this.getMasterGrid();
        this.getSystemAdmin();
        this.mainPanel = new Wtf.Panel({
            layout:"border",
            border:false,
            items:[
            this.newP = new Wtf.Panel({
                layout : 'fit',
                region : 'center'
            }),
            this.masterGrid,
            this.systemPanel
            ]
        });
        this.msgPanel = new Wtf.Panel({
            html: '<div id="empty" class="emptyGridText">'+WtfGlobal.getLocaleText("hrms.masterconf.SelectMastertype")+'</div>',
            frame: false
            }),
            this.newP.add(this.msgPanel);
        this.newP.add(this.getMasterDataGrid());
        this.mainPanel.doLayout();
        this.masterSm.on("selectionchange",function(){
            if(this.newP.getComponent(this.msgPanel.getId())!=null){
                this.newP.remove(this.msgPanel);
                if(this.masterSm.getSelected().get("name")!='Recruitment' && this.masterSm.getSelected().get("name")!='Custom Fields'){
                    this.newP.remove(this.masterDataGrid);
                    this.newP.add(this.getMasterDataGrid());
                    this.mainPanel.doLayout();
                }
            }
            if(this.masterSm.getSelected()){
                if(this.masterDataGrid!=null)this.newP.remove(this.masterDataGrid);
                if(this.configRecruitment!=null)this.newP.remove(this.configRecruitment);
                if(this.CostCenterGrid!=null)this.newP.remove(this.CostCenterGrid);
                if(this.customFieldCompo!=null)this.newP.remove(this.customFieldCompo);

                if(this.masterSm.getSelected().get("id")==17){//Custom Fields
                    this.newP.add(this.getCustomFieldCompo());
                    this.mainPanel.doLayout();
                } else if(this.masterSm.getSelected().get("id")==19){//Recruitment
                    this.newP.add(this.getRecruitmentCompo());
                    this.mainPanel.doLayout();
                } else if(this.masterSm.getSelected().get("id")==20){//Cost Center
                    this.newP.add(this.getCostCenterGrid());
                    this.CostCenterStore.load();
                    this.mainPanel.doLayout();
                }else{
                    this.newP.add(this.getMasterDataGrid());
                    this.mainPanel.doLayout();
                    calMsgBoxShow(202,4,true);
                    this.masterDataAdd.enable();
                    this.masterEdit.enable();
                    this.masterDataStore.load({
                        params:{
                            configid:this.masterGrid.getSelectionModel().getSelected().get("id")
                        }
                    });
                }
                if(!Wtf.StoreMgr.containsKey("dep")){
                    Wtf.depStore.load();
                    Wtf.StoreMgr.add("dep",Wtf.depStore)
                }

                if(this.masterSm.getSelected().get("id")==24 ||this.masterSm.getSelected().get("id")==22){//frequency and payment type
                    this.masterDataAdd.disable();
                    this.masterEdit.disable();
                    this.masterDataDelete.disable();
                }
            } else {
                this.masterDataAdd.disable();
                this.masterEdit.disable();
            }

        },this);
        this.createLeavePanel();
        this.add(this.mainPanel);
    },
    getMasterGrid:function (){
        this.masterRec = new Wtf.data.Record.create([
        {
            name:"id"
        },

        {
            name:"name"
        },

        {
            name:"parentid"
        },
        {
            name:'mappingid'
        }
        ]);

        this.masterReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.masterRec);

        this.masterStore = new Wtf.data.Store({
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/Master/getMasterField.common",
            reader:this.masterReader,
            baseParams:{
                flag:201
            }
        });

        this.masterStore.load();

        this.masterColumn = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.masterconf.MasterType"),
                sortable:true,
                dataIndex:"name"
            }
            ]);

        var masterbtn=new Array();
        this.masterAdd = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.masterconf.AddMasterType"),
            handler:function (){
                this.AddMaster("Add");
            },
            scope:this
        });

        this.masterEdit = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.masterconf.EditMasterType"),
            handler:function (){
                this.AddMaster("Edit");
            },
            scope:this,
            disabled:true
        });
        //By Avinash 10/09/2009 will be used to add edit master data
        //         if(!WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.manage)){
        //               masterbtn.push(this.masterAdd);
        //               masterbtn.push('-');
        //               masterbtn.push(this.masterEdit);
        //         }
        this.masterGrid = new Wtf.grid.GridPanel({
            sm:this.masterSm = new Wtf.grid.RowSelectionModel(),
            region:"west",
            width:300,
            store:this.masterStore,
            sortable:true,
            cm:this.masterColumn,
            viewConfig:{
                forceFit:true
            }
        //            ,
        //            bbar:masterbtn
        });
    },
    getMasterDataGrid:function (){
        this.masterDataRec = new Wtf.data.Record.create([
        {
            name:"id"
        },

        {
            name:"name"
        },
        {
            name:"weightage"
        },

        {
            name:"parentid"
        },

        {
            name:"configid"
        },
        {
            name:'mappingid'
        }
        ]);

        this.masterDataReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.masterDataRec);

        this.masterDataStore = new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            reader:this.masterDataReader,
            baseParams:{
                flag:203
            }
        });
        this.masterDataStore.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);

        this.masterDataColumn = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.masterconf.SubFields"),
                sortable:true,
                dataIndex:"name"
            }
            ]);

        this.masterDataAdd = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.masterconf.AddSubFields"),
            tooltip:WtfGlobal.getLocaleText("hrms.masterconf.AddSubFields.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            handler:function (){
                this.AddMasterData("Add");
            },
            disabled:true,
            scope:this
        });

        this.masterDataEdit = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.masterconf.EditSubFields"),
            tooltip:WtfGlobal.getLocaleText("hrms.masterconf.EditSubFields.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            handler:function (){
                this.AddMasterData("Edit");
            },
            scope:this,
            disabled:true
        });

        this.masterDataDelete = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.masterconf.DeleteSubFields"),
            tooltip:WtfGlobal.getLocaleText("hrms.masterconf.DeleteSubFields.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            handler:function (){
                this.DeleteMasterData();
            },
            scope:this,
            disabled:true
        });
        this.companySave = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.masterconf.DeleteSubFields"),
            handler:function (){
                this.DeleteMasterData();
            },
            scope:this,
            disabled:true
        });

        var masterDatabtn=new Array();
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.manage)){
            masterDatabtn.push(this.masterDataAdd);
            masterDatabtn.push('-');
            masterDatabtn.push(this.masterDataEdit);
            masterDatabtn.push('-');
            masterDatabtn.push(this.masterDataDelete);
        }
        this.masterDataGrid = new Wtf.grid.GridPanel({
            sm:this.masterDataSm = new Wtf.grid.RowSelectionModel(),
            store:this.masterDataStore,
            //region:"center",
            cm:this.masterDataColumn,
            viewConfig:{
                forceFit:true
            },
            bbar:masterDatabtn,
            emptyText: WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.masterconf.SelectMastertype"))
        });
        this.masterDataSm.on("selectionchange",function (){
            if(this.masterDataSm.getSelected()){
                this.masterDataEdit.enable();
                this.masterDataDelete.enable();
            } else {
                this.masterDataEdit.disable();
                this.masterDataDelete.disable();
            }
        },this);
        return this.masterDataGrid;
    },
    getCostCenterGrid:function (){
        this.CostCenterRec = new Wtf.data.Record.create([
        {
            name:"id"
        },

        {
            name:"name"
        },
        {
            name:"code"
        },

        {
            name:"creationDate"
        }
        ]);

        this.CostCenterReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.CostCenterRec);

        this.CostCenterStore = new Wtf.data.Store({
            url: "Common/getCostCenter.common",
            reader:this.CostCenterReader

        });
        this.CostCenterStore.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);

        this.CostCenterColumn = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.common.cost.center.code"),
                sortable:true,
                dataIndex:"code"
            },{
                header:WtfGlobal.getLocaleText("hrms.common.cost.center.name"),
                sortable:true,
                dataIndex:"name"
            }
            
            ]);

        this.CostCenterAdd = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.MasterData.add.cost.center"),
            tooltip:WtfGlobal.getLocaleText("hrms.MasterData.list.cost.centers.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            handler:function (){
                this.AddCostCenter("Add");
            },
//            disabled:true,
            scope:this
        });

        this.CostCenterEdit = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.MasterData.edit.cost.center"),
            tooltip:WtfGlobal.getLocaleText("hrms.MasterData.cost.center.edit"),
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            handler:function (){
                this.AddCostCenter("Edit");
            },
            scope:this,
            disabled:true
        });

        this.CostCenterDelete = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.MasterData.delete.cost.center"),
            tooltip:WtfGlobal.getLocaleText("hrms.MasterData.remove.cost.center"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            handler:function (){
                this.DeleteCostCenter();
            },
            scope:this,
            disabled:true
        });
        

        var CostCenterbtn=new Array();
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.manage)){
            CostCenterbtn.push(this.CostCenterAdd);
            CostCenterbtn.push('-');
            CostCenterbtn.push(this.CostCenterEdit);
            CostCenterbtn.push('-');
            CostCenterbtn.push(this.CostCenterDelete);
        }
        this.CostCenterGrid = new Wtf.grid.GridPanel({
            sm:this.CostCenterSm = new Wtf.grid.RowSelectionModel(),
            store:this.CostCenterStore,
            //region:"center",
            cm:this.CostCenterColumn,
            viewConfig:{
                forceFit:true
            },
            bbar:CostCenterbtn,
            emptyText: WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.MasterData.select.cost.center.subfields"))
        });
        this.CostCenterSm.on("selectionchange",function (){
            if(this.CostCenterSm.getSelected()){
                this.CostCenterEdit.enable();
                this.CostCenterDelete.enable();
            } else {
                this.CostCenterEdit.disable();
                this.CostCenterDelete.disable();
            }
        },this);
        return this.CostCenterGrid;
    },
    AddMaster:function (action){
        new Wtf.AddEditMaster({
            title:WtfGlobal.getLocaleText({key:"hrms.common.actionMasterField",params:[action]}),
            layout:"fit",
            modal:true,
            width:400,
            height:230,
            action:action,
            rec:this.masterSm.getSelected(),
            store:this.masterStore
        }).show();
    },
    AddMasterData:function (action){
        var configid = this.masterGrid.getSelectionModel().getSelected().get("id");
        var parentid = this.masterGrid.getSelectionModel().getSelected().get("parentid");
            this.addeditmaster = new Wtf.AddEditMasterData({
            layout:"fit",
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            title:WtfGlobal.getLocaleText({key:"hrms.masterconf.actionSubField",params:[action]}),
            modal:true,
            configid:configid,
            parentid:parentid,
            width:400,
            height:230,
            rec:this.masterDataSm.getSelected(),
            action:action,
            store:this.masterDataStore
        })

        this.addeditmaster.on('show',function(){
            this.addeditmaster.name.focus(true,100);
        },this);
        this.addeditmaster.show();
    },
    AddCostCenter:function (action){
        if(action!='Add'){
        var configid = this.CostCenterGrid.getSelectionModel().getSelected().get("id");
        var parentid = this.CostCenterGrid.getSelectionModel().getSelected().get("parentid");
        }
        this.addcostcenter = new Wtf.AddCostCenter({
            layout:"fit",
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            title:WtfGlobal.getLocaleText({key:"hrms.common.cost.center.params",params:[action]}),
            modal:true,
            configid:configid,
            parentid:parentid,
            width:400,
            height:260,
            rec:this.CostCenterSm.getSelected(),
            action:action,
            store:this.CostCenterStore
        })

//        this.addcostcenter.on('show',function(){
//            this.addcostcenter.name.focus(true,100);
//        },this);
        this.addcostcenter.show();
        
    },
    DeleteMasterData:function (){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), deleteMsgBox('field'),function(btn){
            if(btn!="yes") return;
            var configid = this.masterGrid.getSelectionModel().getSelected().get("id");
            calMsgBoxShow(201,4,true);
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp",
                url:"Common/Master/deletemasterdata.common",
                params:{
                    flag:48,
                    ids:this.masterDataSm.getSelected().get("id")
                }
            },
            this,
            function (response){
                var res=eval('('+response+')');
                if(res.success==false){
                	if(res.masterid=="1"){//Designation
                		calMsgBoxShow(210,2);
                	}else if(res.masterid=="25"){//Timesheet Job
                		calMsgBoxShow(231,2);
                	}
                }else{
                	this.masterDataStore.load({
                		params:{
                        	configid:configid
                    	}
                	});
                	msgFlag=0;
                	this.loadMasterStores(configid);
                	msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),res.data],1);
                }
            },function (){
                var configid = this.masterGrid.getSelectionModel().getSelected().get("id");
                var flag =86;
                switch(configid){
                    case 1:
                        flag=210;
                        break;
                    case 7:
                        flag=211;
                        break;
                    case 13:
                        flag=193;
                        break;
                    case 15:
                        flag=192;
                        break;
                    case 21:
                        flag=232;
                        break;
                }
                calMsgBoxShow(flag,2);
            });
        },this);
    },
     DeleteCostCenter:function (){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.common.delete.selected.field")+"<br><br><b>"+WtfGlobal.getLocaleText("hrms.Messages.DateCannotbeRetrive"),function(btn){
            if(btn!="yes") return;
            calMsgBoxShow(201,4,true);
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp",
                url:"Common/Master/deletecostcenter.common",
                params:{
                    flag:48,
                    ids:this.CostCenterSm.getSelected().get("id")
                }
            },
            this,
            function (response){
                var res=eval('('+response+')');
                if(res.success==false){
                	 calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.cost.center.cannot.deleted")],2);
                }else{
                	this.CostCenterStore.load();
                	msgBoxShow(["Success",res.data],1);
                }
            },function (){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.cost.center.cannot.deleted")],2);
            });
        },this);
    },
    loadMasterStores : function(configid) {
        switch(configid) {
            case 1 :
                Wtf.desigStore.removeListener("load");
                Wtf.desigStore.load();
                Wtf.StoreMgr.add("desig",Wtf.desigStore);
                break;
            case 2 :
                Wtf.contextstore.removeListener("load");
                Wtf.contextstore.load();
                Wtf.StoreMgr.add("context",Wtf.contextstore);
                break;
            case 3 :
                Wtf.priostore.removeListener("load");
                Wtf.priostore.load();
                Wtf.StoreMgr.add("prio",Wtf.priostore);
                break;
            case 4 :
                Wtf.wthstore.removeListener("load");
                Wtf.wthstore.load();
                Wtf.StoreMgr.add("wth",Wtf.wthstore);
                break;
            case 5 :
            	Wtf.completedStore.removeListener("load");
            	Wtf.completedStore.load();
                Wtf.StoreMgr.add("comp",Wtf.completedStore);
                break;
            case 7 :
                Wtf.depStore.removeListener("load");
                Wtf.depStore.load();
                Wtf.StoreMgr.add("dep",Wtf.depStore);
                break;
            case 10 :
                Wtf.rankStore.removeListener("load");
                Wtf.rankStore.load();
                Wtf.StoreMgr.add("rank",Wtf.rankStore);
                break;
            case 12:
                Wtf.quaStore.removeListener("load");
                Wtf.quaStore.load();
                Wtf.StoreMgr.add("qua",Wtf.quaStore);
                break;
            case 14:
                Wtf.catgStore.removeListener("load");
                Wtf.catgStore.load();
                Wtf.StoreMgr.add("catg",Wtf.catgStore);
                break;
            case 15:
                Wtf.terStore.removeListener("load");
                Wtf.terStore.load();
                Wtf.StoreMgr.add("ter",Wtf.terStore);
                break;
        }
    },
    
    getSystemAdmin:function(){
        var btns=[];
        this.companySave = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.Save"),
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            handler:function (){
                this.saveEmpIDformat();
            },
            scope:this
        });

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.manage)){
            btns.push('->',this.companySave); 
        }
         this.check1=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Updates.SelfAppraisal"),
            name:'selfappraisal'
        });
         this.check2=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.competencies"),
            name:'competencies'
        });
         this.check3=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.goals"),
            name:'goals'
        });
         this.check4=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.AnonymousAppraiser"),
            name:'annmng'
        });
        this.check5=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.overall.approve.appraisal"),
            name:'approveappr'
        });
        this.check6=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.promotion.recommendation"),
            name:'promotionrec'
        });
        this.check7=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.weightage"),
            name:'weightage'
        });
        this.check8=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.PartialSubmission"),
            name:'partial'
        });
        this.check9=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.Dashboard.ReviewAppraisal"),
            name:'reviewappraisal'
        });
        this.check10=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.FullUpdates"),
            name:'fullupdates'
        });
        this.check11=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.ModAverage"),
            name:'modaverage'
        });
        this.check12=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.OverallComments"),
            name:'overallcomments'
        });
        this.infoPan=new Wtf.Panel({
            border:false,
            bodyStyle:"padding-bottom:3px;",
            html:WtfGlobal.getLocaleText("hrms.masterconf.DefaultviewJobsandApplications")+" :"
        });
        this.radio2=new Wtf.form.Radio({
            boxLabel:WtfGlobal.getLocaleText("hrms.common.Internal"),
            hideLabel:true,
            name:'defaultapps',
            inputValue:'Internal'
        });
        this.radio3=new Wtf.form.Radio({
            boxLabel:WtfGlobal.getLocaleText("hrms.common.External"),
            hideLabel:true,
            name:'defaultapps',
            inputValue:'External'
        });
        
        this.payInfoPan=new Wtf.Panel({
            border:false,
            bodyStyle:"padding-bottom:3px;",
            html:WtfGlobal.getLocaleText("hrms.masterconf.payroll.based.on")+" :"
        });
        this.payRadio2=new Wtf.form.Radio({
            boxLabel:WtfGlobal.getLocaleText("hrms.common.Template"),
            hideLabel:true,
            name:'payrollbase',
            inputValue:'Template'
        });
        this.payRadio3=new Wtf.form.Radio({
            boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Date"),
            hideLabel:true,
            name:'payrollbase',
            inputValue:'Date'
        });

        
        this.day0=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.sunday"),
            name:'sunday',
            id:"master_configuration_working_days_0"
        });
        this.day1=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.monday"),
            name:'monday',
            id:"master_configuration_working_days_1"
        });
        this.day2=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.tuesday"),
            name:'tuesday',
            id:"master_configuration_working_days_2"
        });
        this.day3=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.wednesday"),
            name:'wednesday',
            id:"master_configuration_working_days_3"
        });
        this.day4=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.thursday"),
            name:'thursday',
            id:"master_configuration_working_days_4"
        });
        this.day5=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.friday"),
            name:'friday',
            id:"master_configuration_working_days_5"
        });
        this.day6=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.saturday"),
            name:'saturday',
            id:"master_configuration_working_days_6"
        });
        
        this.dayPanel=new Wtf.Panel({
            html:'<font color="#15428B">'+WtfGlobal.getLocaleText("hrms.common.WeeklyOff")+'</font>',
            border:false

        });
        this.mon = [["0", WtfGlobal.getLocaleText("hrms.January")],
                    ["1", WtfGlobal.getLocaleText("hrms.February")],
                    ["2", WtfGlobal.getLocaleText("hrms.March")],
                    ["3", WtfGlobal.getLocaleText("hrms.April")],
                    ["4", WtfGlobal.getLocaleText("hrms.May")],
                    ["5", WtfGlobal.getLocaleText("hrms.June")],
                    ["6", WtfGlobal.getLocaleText("hrms.July")],
                    ["7", WtfGlobal.getLocaleText("hrms.August")],
                    ["8", WtfGlobal.getLocaleText("hrms.September")],
                    ["9", WtfGlobal.getLocaleText("hrms.October")],
                    ["10",WtfGlobal.getLocaleText("hrms.November")],
                    ["11",WtfGlobal.getLocaleText("hrms.December")]
                   ];
        this.monthStore = new Wtf.data.SimpleStore({
               id    : 'monStore',
               fields: ['id','name'],
               data: this.mon
        });
        this.systemPanel=new Wtf.form.FormPanel({
            title:WtfGlobal.getLocaleText("hrms.masterconf.CompanyPreferences"),
            region:'east',
            width:400,
            autoScroll:true,
            layout:'form',
//            url:Wtf.req.base+"UserManager.jsp",
            url:"Common/Master/setEmpIdFormat.common",
            baseParams:{
                mode:25
            },
            border:true,
            labelWidth:125,
            cls:'formstyleClass3',
            defaults:{
                labelWidth:150
            },
            bodyStyle:'padding-top:25px;padding-left:15px',
            items:[{
                xtype:'textfield',
                name: 'employeeidformat',
                id:'employeeidformat',
                width: 200,
                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.EmployeeIDFormat"),
                regex:/^[a-zA-Z]{1,}-{1}[0]{1,}$|^[a-zA-Z]{1,}-{1}[0]{1,}-{1}[a-zA-Z]{1,}$|^[0]{0,}$/
            },
            {
                border: false,
                cls:'compLogoinfo11',
                height:60,
                html:"eg.  0000 <br>eg.  ABC-0000<br>eg.  ABC-0000-XYZ"
            },
            {
                xtype:'textfield',
                name: 'jobidformat',
                id:'jobidformat',
                width: 200,
                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.JobIDFormat"),
                regex:/^[a-zA-Z]{1,}-{1}[0]{1,}$|^[a-zA-Z]{1,}-{1}[0]{1,}-{1}[a-zA-Z]{1,}$|^[0]{0,}$/
            },
            {
                border: false,
                cls:'compLogoinfo11',
                height:60,
                html:"eg.  0000 <br>eg.  JB-0000<br>eg.  JB-0000-XYZ"
            },
            {
                xtype:'textfield',
                name: 'emailNotification',
                id:'emailNotification',
                width: 200,
                fieldLabel: WtfGlobal.getLocaleText("hrms.masterconf.EmailNotificationforRecruitment"),
                regex:/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/
            },
            {
                border: false,
                cls:'compLogoinfo11',
                height:20,
                html:" "
            },
            this.empCmb = new Wtf.form.ComboBox({
                triggerAction:"all",
                id:'finmonth',
                fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.FinancialMonth")+' <font color="SeaGreen">* </font>',
                hiddenName: 'financialmonth',
                mode:"local",
                valueField:'id',
                displayField:'name',
                store:this.monthStore,
                width:150,
                typeAhead:true
            }),
            {
                border:false,
                bodyStyle:"margin-bottom:10px;",
                html:'<div style=\"color:SeaGreen;font-size:11px;padding-left:25px;\">* '+WtfGlobal.getLocaleText("hrms.common.Usewhilegeneratingsalary")+'</div>'
            }
            ,{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.masterconf.SubscribedModules"),
                autoHeight:true,
                id:this.id+'modulefieldset'
            }
            ,{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.hrmsModules.appraisal"),
                id:this.id+'perfappfieldset',
                hidden:true,
                autoHeight:true,
                items:[this.check1,this.check2,this.check3,this.check4,this.check5,
                    this.check7,this.check6,this.check8,this.check9,this.check10,this.check11,this.check12]
            }
            ,{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.Dashboard.RecruitmentManagement"),
                id:this.id+'recmgmtfieldset',
                hidden:true,
                autoHeight:true,
                items:[this.infoPan,this.radio2,this.radio3]
            },{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.payroll.management"),
                id:this.id+'paymgmtfieldset',
                hidden:true,
                autoHeight:true,
                items:[this.checkapprovesalary=new Wtf.form.Checkbox({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Checktosendsalaryforauthorization"),
                        name:'approvesalary'
                    }),this.payInfoPan,this.payRadio2,this.payRadio3,this.dayPanel,this.day0,this.day1,this.day2,this.day3,this.day4,this.day5,this.day6 ]
            },{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.timesheet.management"),
                id:this.id+'timemgmtfieldset',
                hidden:true,
                autoHeight:true,
                items:[this.checktimesheetjob=new Wtf.form.Checkbox({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.job.free.text"),
                        name:'timesheetjob'
                    })]
            },{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.Dashboard.Administration"),
                id:this.id+'administration',
                autoHeight:true,
                items:[this.checkadministration=new Wtf.form.Checkbox({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.admin.block.employees.edit.details"),
                        name:'blockemployees'
                    })]
            },{
                xtype:'fieldset',
                width:'75%',
                title:WtfGlobal.getLocaleText("hrms.masterconf.SetWeeklyHolidays"),
                id:this.id+'holiday',
                hidden:true,
                autoHeight:true
            }
            ],
            bbar:btns

        });
        this.allModule="";
        Wtf.Ajax.requestEx({
//            url:Wtf.req.base+"UserManager.jsp",
            url:"Common/Master/getCompanyInformation.common",
            params:{
                mode:18
            }
            },
        this,
        function(request, response){
            var res = request;
            if(res && res.data){
                this.doLayout();
                Wtf.getCmp("employeeidformat").setValue(res.data[0].employeeidformat);
                Wtf.getCmp("jobidformat").setValue(res.data[0].jobidformat);
                Wtf.getCmp("finmonth").setValue(res.data[0].finanacialmonth);
                Wtf.getCmp("emailNotification").setValue(res.data[0].emailNotification);
                this.weekcount = res.data[0].weeklyholiday;
                this.check1.setValue(res.data[0].selfapp);
                this.check2.setValue(res.data[0].competency);
                this.check3.setValue(res.data[0].goal);
                this.check4.setValue(res.data[0].annmng);
                this.check5.setValue(res.data[0].approveappraisal);
                this.check6.setValue(res.data[0].promotionrec);
                this.check7.setValue(res.data[0].weightage);
                this.check8.setValue(res.data[0].partial);
                this.check9.setValue(res.data[0].reviewappraisal);
                this.check10.setValue(res.data[0].fullupdates);
                this.check11.setValue(res.data[0].modaverage);
                this.check12.setValue(res.data[0].overallcomments);
                this.checkapprovesalary.setValue(res.data[0].approvesalary)
                this.checktimesheetjob.setValue(res.data[0].timesheetjob);
                this.checkadministration.setValue(res.data[0].blockemployees);
                if(res.data[0].defaultapps=="Internal"){
                    this.radio2.setValue(true);
                } else {
                    this.radio3.setValue(true);
                }
                if(res.data[0].payrollbase=="Template"){
                    this.payRadio2.setValue(true);
                } else {
                    this.payRadio3.setValue(true);
                }
                this.allModule=res.data[0].modules;
                this.genrateModules(res.data[0].modules,res.data[0].subscription);
                for(var i = 0 ; i < 7 ; i++){
                    if((Math.pow(2, i) & this.weekcount) == Math.pow(2, i)){
                        this.weekStore.getAt(i).data.isholiday=true;
                    }
                }
                Wtf.getCmp("tempgrid").getView().refresh();
                this.checkWorkingDays(res.data[0].holidaycode);
               
            }
            WtfGlobal.closeProgressbar();
        },
        function(){
            }
            );
    },
    getHolidayCode:function(modules){
        var code=0;

        for(var i=0; i<7; i++){

            if(Wtf.getCmp("master_configuration_working_days_"+i).getValue()){
                code += Math.pow(2,parseInt(i));
            }
        }

        return code;
    },
    checkWorkingDays:function(holidaycode){
       
        for(var i=0; i<7; i++){
            
            if(holidaycode%2==1){
                Wtf.getCmp("master_configuration_working_days_"+i).setValue(true)
            }
            holidaycode=Math.floor(holidaycode/2);
           
        }

    },
    saveEmpIDformat:function(){
        if(this.systemPanel.form.isValid()){
            var code=this.getSub(this.allModule);
            var holidaycode = this.getHolidayCode();
            
            this.systemPanel.form.submit({
                waitMsg:WtfGlobal.getLocaleText("hrms.masterconf.Savingcompanypreferences"),
                scope: this,
                params:{
                    subcription:code,
                    holidaycode:holidaycode,
                    weeklyholiday:this.weekcount,
                    approvesalary:this.checkapprovesalary.getValue()
                },
                success:function(req,res){
                    if(res.response.responseText!=undefined){
                        var resObject = eval('(' + res.response.responseText + ')');
                        Wtf.cmpPref.approvesalary=resObject.approvesalary;
                    }
                    
                    calMsgBoxShow(145,0);
                    var empgrid=Wtf.getCmp('empmntgridqualifiedgr');
                    var jobgrid=Wtf.getCmp('interjobgrid2');
                    if(empgrid!=null){
                        empgrid.getStore().load();
                    }
                    if(jobgrid!=null){
                        jobgrid.getStore().load();
                    }
                },
                failure:function(){
                    calMsgBoxShow(27,0);
                }
            });
        }
    },
    genrateModules:function(modules,subscription){
       var modulefld=Wtf.getCmp(this.id+'modulefieldset');
       for(var i=0;i<modules.data.length;i++){
          modulefld.add( new Wtf.form.Checkbox({
            fieldLabel:modules.data[i].moduledispname+" ",
            name:modules.data[i].modulename,
            id:modules.data[i].moduleid+this.id
        }) );
        var comp=Wtf.getCmp(modules.data[i].moduleid+this.id);
        comp.setValue(this.isChecked(subscription,modules.data[i].moduleid));
        if(modules.data[i].moduleid==4){//For performance appraisal module
            comp.on("check",function(ckbox,val){
                if(val==true){
                    Wtf.getCmp(this.id+'perfappfieldset').setVisible(true);
                }
                else{
                    Wtf.getCmp(this.id+'perfappfieldset').setVisible(false);
                }
            },this)
        }
        if(modules.data[i].moduleid==1){//For recruitment management module
            comp.on("check",function(ckbox,val){
                if(val==true){
                    Wtf.getCmp(this.id+'recmgmtfieldset').setVisible(true);
                }
                else{
                    Wtf.getCmp(this.id+'recmgmtfieldset').setVisible(false);
                }
            },this)
        }
        if(modules.data[i].moduleid==2){//For payroll module
            comp.on("check",function(ckbox,val){
                if(val==true){
                    Wtf.getCmp(this.id+'paymgmtfieldset').setVisible(true);
                }
                else{
                    Wtf.getCmp(this.id+'paymgmtfieldset').setVisible(false);
                }
            },this)
        }
        if(modules.data[i].moduleid==3){//For timesheet module
            comp.on("check",function(ckbox,val){
                if(val==true){
                    Wtf.getCmp(this.id+'timemgmtfieldset').setVisible(true);
                }
                else{
                    Wtf.getCmp(this.id+'timemgmtfieldset').setVisible(false);
                }
            },this)
        }

//        Wtf.getCmp(this.id+'approvesalaryfieldset').setVisible(true);
       }

        this.doLayout();
    },
    isChecked:function(actRec,index){
        if((Math.pow(2,parseInt(index))&actRec)==Math.pow(2,parseInt(index))){
            return true;
        }else{
            return false;
        }
    },
    getSub:function(modules){
        var code=0;
        for(var i=0;i<modules.data.length;i++){
            if(Wtf.getCmp(modules.data[i].moduleid+this.id).getValue()){
                code += Math.pow(2,parseInt(modules.data[i].moduleid));
            }
        }
        return code;
    },
    getCustomFieldCompo:function() {
        this.customFieldCompo = new  Wtf.configAnyGrid({
            border: false
        });
        return this.customFieldCompo;
    },
    getRecruitmentCompo:function() {
        this.configRecruitment = new  Wtf.configRecruitment({
            border: false
        });
        return this.configRecruitment;
    },
    createLeaveGrid: function(){
        this.data1 = {'data':[{day:'Sunday', isholiday:false},{day:'Monday', isholiday:false},{day:'Tuesday', isholiday:false},{day:'Wednesday', isholiday:false},{day:'Thursday', isholiday:false},{day:'Friday', isholiday:false},{day:'Saturday', isholiday:false}]};
        this.weekStore = new Wtf.data.Store({
//            url: "../../admin.jsp",
//            baseParams: {
//                action: 0,
//                mode: 14,
//                projid: this.featureid
//            },
            reader: new Wtf.data.JsonReader({
                root: 'data',
                fields:[{
                    name: 'day'
//                    ,
//                    type: 'int'
                },{
                    name: 'isholiday',
                    type: 'boolean'
                }]
            })
        });
        
        var colArr = [];
        colArr.push({
            header: WtfGlobal.getLocaleText("hrms.common.Day"),
            align: 'center',
            dataIndex: 'day',
            renderer: function(val){
                //return Wtf.Week[val];
                return val;
            }
        });

        var checkdays = new Wtf.grid.CheckColumn({
            header: WtfGlobal.getLocaleText("hrms.masterconf.Markasholiday"),
            dataIndex: 'isholiday',
            align: 'center',
            //width: 5,
            scope: this,
            sortable: false
        });
        colArr.push(checkdays);
        var colM = new Wtf.grid.ColumnModel(colArr);
        var projWorkGrid = new Wtf.grid.EditorGridPanel({
            id: 'tempgrid',
            cm: colM,
            layout: 'fit',
            ds: this.weekStore,
            border: false,
            autoScroll:false,
           // height: 210,
            clicksToEdit : 1,
            viewConfig: {
                forceFit: true
            }
        });
//        this.weekStore.on("load", function(obj, records){
//            this.innerpanel.doLayout();
//            for(var cnt=0; cnt<records.length; cnt++) {
//                if(records[cnt].data["isholiday"]) {
//                    projWorkGrid.getView().getRow(cnt).style.background = '#FF9F8F';
//                }
//            }
//        }, this);
//        this.weekStore.load();
        projWorkGrid.on("beforeedit", function(e){
            if(this.archived){
                e.cancel = true;
            }
        },this)
        this.weekStore.loadData(this.data1);
        projWorkGrid.on("afteredit", function(e){
            this.updateDay(e.record);
        },this)
        projWorkGrid.on("cellclick", function(grid, ri, ci, e){
            if(e.target.tagName == 'IMG'){
                var rec = this.weekStore.getAt(ri);
                var fieldName = grid.getColumnModel().getDataIndex(ci);
                var classname = e.target.className;
                var ele = Wtf.get(e.target.id);
                if(classname.indexOf('x-grid3-check-col-on') !== -1){
                    ele.replaceClass('x-grid3-check-col-on', 'x-grid3-check-col');
                    rec.data[fieldName] = false;
                    this.weekcount -= Math.pow(2, ri);
                } else {
                    this.weekcount +=  Math.pow(2, ri);
                    if(this.weekcount == 127){
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.masterconf.Alldaysnotholiday")],0);
                        this.weekcount -=  Math.pow(2, ri);
                        return;
                    }
                    ele.replaceClass('x-grid3-check-col', 'x-grid3-check-col-on');
                    rec.data[fieldName] = true;
                    //this.weekcount +=  Math.pow(2, ri);
                }
               // this.updateDay(rec);
            }
        }, this)
        return projWorkGrid;
    },

    createLeavePanel: function(){
        var weekGrid = this.createLeaveGrid();
        this.leavePanel = new Wtf.Panel({
            layout: 'fit',
            autoHeight: true,
            width: 270,
            items: [weekGrid]
        });
        Wtf.getCmp(this.id+'holiday').add(this.leavePanel);
    },

    updateDay: function(rec){
        var value = rec.get('isholiday');
        var tempday = rec.get('day');
        var outTime = new Date(new Date().toDateString() + ' ' + rec.data.intime).add(Date.HOUR, 8).format('H:i:s');
        Wtf.Ajax.requestEx({
            url: '../../admin.jsp',
            params: {
                action: 1,
                mode: 2,
                emode: 3,
                day: tempday,
                dayLabel: Wtf.Week[tempday],
                intime : rec.data.intime,
                outtime : outTime,
                isholiday : value ? "on" : "off",
                projid: this.featureid
            },
            method:'POST'
        },
        this,
        function() {
            msgBoxShow(28, 0);
            this.weekStore.load();
            bHasChanged=true;
            if(refreshDash.join().indexOf("all") == -1)
                refreshDash[refreshDash.length] = 'all';
        },
        function() {
            msgBoxShow(4, 1);
        });
    }

});  
 
Wtf.grid.CheckColumn = function(config){
    Wtf.apply(this, config);
    if(!this.id){
        this.id = Wtf.id();
    }
    this.renderer = this.renderer.createDelegate(this);
};

Wtf.grid.CheckColumn.prototype ={
    init : function(grid){
        this.grid = grid;
    },
    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td';
        return '<img src="'+Wtf.BLANK_IMAGE_URL+'" class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</img>';
    }
};
