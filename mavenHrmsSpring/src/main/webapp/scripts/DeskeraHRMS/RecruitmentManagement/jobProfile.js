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
Wtf.jobProfile=function(config){
    Wtf.jobProfile.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.jobProfile,Wtf.Panel,{
    initComponent:function(config){
	isNewReq=1;
    	this.addEvents({
            'editjobprofile':true
        });
        this.jobrecord=new Wtf.data.Record.create([{
            name:"jobid"
        },{
            name:"designation"
        },{
            name:"details"
        },{
            name:"department"
        },

        {
            name:"manager"
        },{
            name:"startdate"
        },{
            name:"enddate"
        },{
            name:"jobtype"
        },{
            name:"travel"
        },{
            name:"expmonth"
        },

        {
            name:"expyear"
        },{
            name:"relocation"
        },{
            name:"location"
        },{
            name:"jobshift"
        },{
            name:'posid'
        },{
            name:'nopos'
        },{
            name:"jobmeta"
        }]);

        this.jobrecordreader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.jobrecord);


        this.jobstore=new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url:"Rec/Job/getjobprofileFunction.rec",
            reader:this.jobrecordreader,
            autoLoad:true,
            baseParams:{
                flag:155,
                position:this.positionid
            }
        });

        this.schd=[[ WtfGlobal.getLocaleText("hrms.common.FullTime"),'1'],[ WtfGlobal.getLocaleText("hrms.common.PartTime"),'2']];

        this.sch=new Wtf.data.SimpleStore({
            fields:['initial','id'],
            data:this.schd
        });
			
        this.jobrec=new Wtf.data.Record.create([{
            name:'id'
        },{
            name:'name'
        }]);
        this.jobStore =  new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            baseParams: {
                configid:1,
                flag:203
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.jobrec),
            autoLoad : false
        });
        //this.jobStore.load();
        this.jobName = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.JobName"),
            readOnly:true,
            width:200
        });

        this.jobType = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.JobShift"),
            store:this.sch,
            displayField:'initial',
            valueField:'id',
            typeAhead:true,
            triggerAction:'all',
            disabled:this.viewOnlyType,
            forceSelection: true,
            mode:'local',
            selectOnFocus:true,
            width:200
        });

        this.location = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.Location"),
            readOnly:this.viewOnlyType,
            width:200,
            maxLength:255
        });
        this.nopos= new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.no.of.vacancies")+'*',
            name:'nopos',
            readOnly:this.viewOnlyType,
            allowDecimals:false,
            width:200,
            allowBlank:false,
            minValue:1,
            //            value:1,
            maxLength:5
        });

        this.deptrec=new Wtf.data.Record.create([{
            name:'id'
        },{
            name:'name'
        }]);
        this.deptStore =  new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            baseParams: {
                configid:7,
                flag:203
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.deptrec),
            autoLoad : false
        });
        //this.deptStore.load();
        this.jobDepartment = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.department"),
            readOnly:true,
            width:200
        });

        this.apprManager = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.approving.manager")+'*',
            store:Wtf.managerStore,
            displayField:'username',
            valueField:'userid',
            disabled:this.viewOnlyType,
            forceSelection:true,
            allowBlank:false,
            typeAhead:true,
            triggerAction:'all',
            mode:'local',
            selectOnFocus:true,
            width:200
        });

        if(!Wtf.StoreMgr.containsKey("manager")){
            Wtf.managerStore.load();
            Wtf.managerStore.on("load",function(){
                Wtf.StoreMgr.add("manager",Wtf.managerStore)
            },this)
            
        }

        this.jobstartDate = new Wtf.form.DateField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.start.date")+'*',
            allowBlank:false,
            disabled:this.viewOnlyType,
            format:'m/d/Y',
            width:200
        });

        this.jobendDate = new Wtf.form.DateField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.end.date")+'*',
            allowBlank:false,
            disabled:this.viewOnlyType,
            format:'m/d/Y',
            width:200
        });
        this.jobstartDate.on('blur', function(field){
            if(field.getValue()>this.jobendDate.getValue())
                this.jobendDate.markInvalid( WtfGlobal.getLocaleText({key:"hrms.recruitment.Thedateinthisfieldbeequaltooraftermessage",params:[this.jobstartDate.getValue().format('m/d/Y')]}));
        }, this);
        this.jobendDate.on('blur', function(field){
            if(field.getValue()<this.jobstartDate.getValue())
                field.markInvalid( WtfGlobal.getLocaleText({key:"hrms.recruitment.Thedateinthisfieldbeequaltooraftermessage",params:[this.jobstartDate.getValue().format('m/d/Y')]}));
        }, this);
        
        this.yesNoStore = new Wtf.data.SimpleStore({
            fields:['id','name'],
            data: [
            ['Yes', WtfGlobal.getLocaleText("hrms.recruitment.callback.Yes")],
            ['No', WtfGlobal.getLocaleText("hrms.recruitment.callback.No")]
            ]
        });
        
        this.travel = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.TravelRequired"),
            store:this.yesNoStore,
            displayField:'name',
            valueField:'id',
            disabled:this.viewOnlyType,
            forceSelection: true,
            typeAhead:true,
            triggerAction:'all',
            selectOnFocus:true,
            mode:'local',
            width:200
        });

        this.relocation = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.RelocationProvided"),
            store:this.yesNoStore,
            displayField:'name',
            valueField:'id',
            disabled:this.viewOnlyType,
            forceSelection: true,
            typeAhead:true,
            triggerAction:'all',
            selectOnFocus:true,
            mode:'local',
            width:200
        });

        
        
        this.exp = [[0],[1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11]];

        this.expstore = new Wtf.data.SimpleStore({
            fields: ['exp'],
            data :this.exp
        });

        this.exp_year = new Wtf.form.ComboBox({
            store: this.expstore,
            displayField:'exp',
            valueField:'exp',
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.Experienceyearsmonths"),
            labelSeperator:"",
            disabled:this.viewOnlyType,
            forceSelection: true,
            name:"workyear",
            typeAhead: true,
            width:70,
            labelWidth:70,
            emptyText: WtfGlobal.getLocaleText("hrms.common.Year"),
            mode: 'local',
            triggerAction: 'all'
        });

        this.exp_month = new Wtf.form.ComboBox({
            store: this.expstore,
            displayField:'exp',
            valueField:'exp',
            labelSeperator:"",
            disabled:this.viewOnlyType,
            forceSelection: true,
            name:"workmonth",
            typeAhead: true,
            width:70,
            emptyText:WtfGlobal.getLocaleText("hrms.common.Months"),
            mode: 'local',
            triggerAction: 'all'
        });

        this.experiencepanel=new Wtf.Panel({
            width:600,
            frame:false,
            border:false,
            style:'margin-bottom:0px',
            layout:'column',
            items:[
            {
                width:265,
                frame:false,
                border:false,
                layout:'form',
                items:[this.exp_year]
            },
            {
                width:150,
                frame:false,
                border:false,
                layout:'form',
                labelWidth:10,
                labelSeparator:" ",
                items:[this.exp_month]
            }]
        });

        this.jobDescription = new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.JobDescription"),
            width:200,
            readOnly:this.viewOnlyType,
            height:75,
            maxLength:512
        });
        this.posid= new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.job.id"),
            name:'jobid',
            width:200,
            disabled:true,
            maxLength:255
        });
        this.descriptionPanel=new Wtf.Panel({
            autoHeight:true,
            border:false,
            labelWidth:120,
            cls:'formstyleClass5',
            layout:'form',
            columnWidth:.99,
            items:[
            this.jobDescription
            ]
        });

        this.form1=new Wtf.form.FormPanel({
            columnWidth:.48,
            labelWidth:120,
            autoHeight:true,
            border:false,
            frame:false,
            style:'margin-bottom:0px',
            bodyStyle:"padding:0px 0px 0px 200px;",
            items:[this.posid,this.jobName,this.jobDepartment,this.apprManager,this.jobType,this.location,this.jobDescription]
        });

        this.form2=new Wtf.form.FormPanel({
            columnWidth:.52,
            labelWidth:145,
            autoHeight:true,
            border:false,
            frame:false,
            style:'margin-bottom:0px',
            bodyStyle:"padding:0px 0px 0px 100px;",
            items:[this.nopos,this.jobstartDate,this.jobendDate,this.travel,this.relocation,this.experiencepanel]
        });

        this.jobprofileForm=new Wtf.Panel({
            frame:false,
            border:false,
            bodyStyle:"background-color:#FFFFFF;padding:20px 20px 20px 20px;",
            layout:'column',
            items:[this.form1,this.form2]
        });

        this.record1 = new Wtf.data.Record.create([{
            name: 'id'
        },{
            name: 'responsibility' 
        }]);
        this.reader1=new Wtf.data.KwlJsonReader1({
            root: "responsibility",
            totalProperty:'count'

        },this.record1);
        this.dataStore1= new Wtf.data.Store({
//            url:Wtf.req.base + 'hrms.jsp',
            url:"Rec/Job/viewjobprofileFunction.rec",
            reader:this.reader1,
            baseParams:{
                flag:157,
                position:this.positionid,
                type:1
            }
        });
//        this.dataStore1.load();

        this.addButton1= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.add"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:45,
            scope:this,
            handler:function()
            {
                this.addstore1rec();
            }
        });
        this.deleteButton1= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton), 
            minWidth:45,
            disabled:true,
            scope:this,
            handler:function()
            {
                this.deleterec(this.responsibility);
            }
        });

        this.sm1 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.column1=new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            this.sm1,
            {
                header: WtfGlobal.getLocaleText("hrms.recruitment.Responsibility"),
                dataIndex: 'responsibility',
                //width:screen.width*49/80,
                editor: this.viewOnlyType?null:new Wtf.form.TextField({maxLength:255})
            }
            ]);

        this.responsibility=new Wtf.grid.EditorGridPanel({
            store:this.dataStore1,
            autoScroll:true,
            cm:this.column1,
            sm: this.sm1,
            height:250,
            title:WtfGlobal.getLocaleText("hrms.recruitment.Responsibilities"),
            clicksToEdit:1,
            viewConfig:{
                forceFit:true
            },
            cls:'formstyleClass5',            
            border:true,
            tbar:this.viewOnlyType?null:[this.addButton1,'-',this.deleteButton1]
        });

        this.record2=Wtf.data.Record.create([{
            name: 'id'
        },{
            name: 'skill'
        },{
            name: 'skilldesc'
        }]);
        this.reader2=new Wtf.data.KwlJsonReader1({
            root: "skill",
            totalProperty:'count'
        },this.record2);
        this.dataStore2= new Wtf.data.Store({
//            url:Wtf.req.base + 'hrms.jsp',
            url:"Rec/Job/viewjobprofileFunction.rec",
            reader:this.reader2,
            baseParams:{
                flag:157,
                position:this.positionid,
                type:2
            }
        });
//        this.dataStore2.load();
        this.addButton2= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.add"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:45,
            scope:this,
            handler:this.addstore2rec
        });
        this.deleteButton2= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton), 
            minWidth:45,
            scope:this,
            disabled:true,
            handler:function()
            {
                this.deleterec(this.skillgrid);
            }
        });

        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.column2=new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.recruitment.Skill"),
                dataIndex: 'skill',
                editor: this.viewOnlyType?null:new Wtf.form.TextField({maxLength:255})
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.description"),
                dataIndex: 'skilldesc',
                editor: this.viewOnlyType?null:new Wtf.form.TextField({maxLength:255})
            }
            ]);
        this.skillgrid = new Wtf.grid.EditorGridPanel({
            store:this.dataStore2,
            autoScroll:true,
            cm:this.column2,
            sm: this.sm2,
            height:250,
            viewConfig:{
                forceFit:true
            },
            title:WtfGlobal.getLocaleText("hrms.recruitment.SkillsRequired"),
            clicksToEdit:1,
            cls:'formstyleClass5',      
            border:true,
            tbar:this.viewOnlyType?null:[this.addButton2,'-',this.deleteButton2]
        });

        this.record3=Wtf.data.Record.create([{
            name: 'id'
        },{
            name: 'qualification'
        },{
            name: 'qualificationdesc'
        }]); 
        this.reader3=new Wtf.data.KwlJsonReader1({
            root: "qualification",
            totalProperty:'count'
        },this.record3);
        this.dataStore3= new Wtf.data.Store({
//            url:Wtf.req.base + 'hrms.jsp',
            url:"Rec/Job/viewjobprofileFunction.rec",
            reader:this.reader3,
            baseParams:{
                flag:157,
                position:this.positionid,
                type:3
            }
        });
//        this.dataStore3.load();
        this.sm3 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.column3=new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            this.sm3,
            {
                header: WtfGlobal.getLocaleText("hrms.common.Qualification"),
                dataIndex: 'qualification',
                editor: this.viewOnlyType?null:new Wtf.form.TextField({maxLength:255})
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.description"),
                dataIndex: 'qualificationdesc',
                editor: this.viewOnlyType?null:new Wtf.form.TextField({maxLength:255})
            }
            ]);

        
        this.addButton3= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.add"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:45,
            scope:this,
            handler:this.addstore3rec
        });
        this.deleteButton3= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:45,
            disabled:true,
            scope:this,
            handler:function()
            {
                this.deleterec(this.qualgrid);
            }
        });
        this.qualgrid = new Wtf.grid.EditorGridPanel({
            store:this.dataStore3,
            autoScroll:true,
            cm:this.column3,
            sm: this.sm3,
            height:250,
            title:WtfGlobal.getLocaleText("hrms.recruitment.QualificationRequired"),
            clicksToEdit:1,
            viewConfig:{
                forceFit:true
            },
            cls:'formstyleClass5',            
            border:true,
            tbar:this.viewOnlyType?null:[this.addButton3,'-',this.deleteButton3]
        });

        this.submit=new Wtf.Button({
            text: WtfGlobal.getLocaleText("hrms.common.Save"),
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            scope:this,
            handler:this.saveFormValue
        });
        this.bbar=this.viewOnlyType?null:['->',this.submit],
        this.jobpanel=new Wtf.Panel({
            scrollable:true,
            autoScroll:true,
            autoWidth:true,
            autoHeight:true,
            border:false,
            items:[
            this.jobprofileForm,this.responsibility,this.skillgrid,this.qualgrid
            ]
        });
        if(this.disableSubmit){
            this.submit.disable();
        }
        Wtf.jobProfile.superclass.initComponent.call(this,config);
    }, 
    onRender:function(config){
        this.add(this.jobpanel);
        this.jobstore.on("load",this.displayFormValue,this);
        Wtf.jobProfile.superclass.onRender.call(this,config);
        this.sm1.on("selectionchange",function(){
            if(this.sm1.hasSelection()){
                this.deleteButton1.enable();
            } else{
                this.deleteButton1.disable();
            }
        },this);
        this.sm2.on("selectionchange",function(){
            if(this.sm2.hasSelection()){
                this.deleteButton2.enable();
            } else{
                this.deleteButton2.disable();
            }
        },this);
        this.sm3.on("selectionchange",function(){
            if(this.sm3.hasSelection()){
                this.deleteButton3.enable();
            } else{
                this.deleteButton3.disable();
            }
        },this);
    },
    saveFormValue:function(){ 
    	if(isNewReq==1){
            if(this.jobstartDate.getValue().format('Ymd')>this.jobendDate.getValue().format('Ymd') || !this.form2.getForm().isValid() || !this.form1.getForm().isValid()){
                if(this.jobstartDate.getValue().format('Ymd')>this.jobendDate.getValue().format('Ymd'))
                    this.jobendDate.markInvalid(WtfGlobal.getLocaleText({key:"hrms.recruitment.Thedateinthisfieldbeequaltooraftermessage",params:[this.jobstartDate.getValue().format('m/d/Y')]}));
                calMsgBoxShow(203,0);
                return;
            }
            else{
            	isNewReq = 0;
            	this.submit.setDisabled(true);
                Wtf.Ajax.requestEx({
                    url: "Rec/Job/InternalJobpositions.rec",
                    params:{
                        startdate:this.jobstartDate.getValue().format("m/d/Y"),
                        enddate:this.jobendDate.getValue().format("m/d/Y"),
                        details:(this.jobDescription.getRawValue().trim().replace(/^\n*/," ")).replace(/\n*$/," "),
                        manager:this.apprManager.getValue(),
                        jobshift:this.jobType.getValue(),
                        posid:this.positionid,
                        location:this.location.getValue(),
                        relocation:this.relocation.getValue(),
                        travel:this.travel.getValue(),
                        expyear:this.exp_year.getValue(),
                        expmonth:this.exp_month.getValue(),
                        nopos:this.nopos.getValue()
                    }
                },this,
                function(){
                	isNewReq = 1;
                    calMsgBoxShow(174,0);
                    this.submit.setDisabled(false);
                },
                function(){
                	isNewReq = 1;
                    calMsgBoxShow(52,1);
                    this.submit.setDisabled(false);
                })
                var data1 = "";
                var data2 = "";
                var data3 = "";
                var rec;
                for(var i=0;i< this.dataStore1.getCount();i++){
                    rec=this.dataStore1.getAt(i).data;
                    if(rec.responsibility.trim()!=""){
                        data1 += "{'id':'" + rec.id + "',";
                        data1 += "'type':'" + 1 + "',";
                        data1 += "'responsibility':'" + WtfGlobal.onlySinglequoateRenderer(rec.responsibility) + "'},";
                    }
                }
                var trmLen = data1.length - 1;
                var jsondata1 = data1.substr(0,trmLen);
                for(i=0;i< this.dataStore2.getCount();i++){
                    rec=this.dataStore2.getAt(i).data;
                    if(rec.skill.trim()!=""){
                        data2 += "{'id':'" + rec.id + "',";
                        data2 += "'type':'" + 2 + "',";
                        data2 += "'skill':'" + WtfGlobal.onlySinglequoateRenderer(rec.skill) + "',";
                        data2 += "'skilldesc':'" + WtfGlobal.onlySinglequoateRenderer(rec.skilldesc) + "'},";
                    }
                }
                trmLen = data2.length - 1;
                var jsondata2 = data2.substr(0,trmLen);
                for(i=0;i< this.dataStore3.getCount();i++){
                    rec=this.dataStore3.getAt(i).data;
                    if(rec.qualification.trim()!=""){
                        data3 += "{'id':'" + rec.id + "',";
                        data3 += "'type':'" + 3 + "',";
                        data3 += "'qualification':'" + WtfGlobal.onlySinglequoateRenderer(rec.qualification) + "',";
                        data3 += "'qualificationdesc':'" + WtfGlobal.onlySinglequoateRenderer(rec.qualificationdesc) + "'},";
                    }
                }
                trmLen = data3.length - 1;
                var jsondata3 = data3.substr(0,trmLen);
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
                    url:"Rec/Job/addjobprofile.rec",
                    params: {
                        flag:156,
                        jsondataresp:jsondata1,
                        jsondataskill:jsondata2,
                        jsondataqual:jsondata3,
                        position:this.positionid
                    }
                }, this,
                function(){
                	  //this.fireEvent('editjobprofile');
                	  this.submit.setDisabled(false);
                	  this.dataStore1.reload();
                	  this.dataStore2.reload();
                	  this.dataStore3.reload();
                },
                function()
                {
                    calMsgBoxShow(52,1);
                    this.submit.setDisabled(false);
                })
            }
    	}
    	
    },     
    displayFormValue:function(){
        
        if(this.jobstore.getCount()>0){
            var profiledata=this.jobstore.getAt(0);
            this.jobName.setValue(profiledata.data.designation);
            this.jobDescription.setValue(profiledata.data.details);
            this.jobDepartment.setValue(profiledata.data.department);
            this.apprManager.setValue(profiledata.data.manager);
            this.jobstartDate.setValue(profiledata.data.startdate);
            this.jobendDate.setValue(profiledata.data.enddate);
            this.travel.setValue(profiledata.data.travel);
            this.exp_month.setValue(profiledata.data.expmonth);
            this.exp_year.setValue(profiledata.data.expyear);
            this.relocation.setValue(profiledata.data.relocation);
            this.location.setValue(profiledata.data.location);
            this.jobType.setValue(profiledata.data.jobshift);
            this.posid.setValue(profiledata.data.posid);
            this.nopos.setValue(profiledata.data.nopos);
            var jobmeta = profiledata.data.jobmeta.data;
            this.dataStore1.loadData(jobmeta);
            this.dataStore2.loadData(jobmeta);
            this.dataStore3.loadData(jobmeta);
        }
    }, 
    addstore1rec:function(){
        this.responsibility.getStore().add(new this.record1({
            id:'',
            responsibility:''
        }));
    },
    addstore2rec:function(){
        this.skillgrid.getStore().add(new this.record2({
            id:'',
            skill:'',
            skilldesc:''
        }));
    }, 
    addstore3rec:function(){
        this.qualgrid.getStore().add(new this.record3({
            id:'',
            qualification:'',
            qualificationdesc:''
        }));
    },
    deleterec:function(grid){
        this.delgrid=grid;
        this.rec=this.delgrid.getSelectionModel().getSelections();
        this.delarr=[];
        this.delgrid.getSelectionModel().clearSelections();
        for(var i=0;i<this.rec.length;i++){
            if(this.rec[i].get('id')!=""){
                this.delarr.push(this.rec[i].get('id'));
            } else {
                this.delgrid.getStore().remove(this.rec[i]);
            }
        }
        if(this.delarr.length>0){
            Wtf.Ajax.requestEx({
                url: Wtf.req.base + 'hrms.jsp',
                params:  {
                    flag:175,
                    delid:this.delarr
                }
            },
            this,
            function(){
                this.delgrid.getStore().load();
                },
                function(){
                })
        }
    }
});  
