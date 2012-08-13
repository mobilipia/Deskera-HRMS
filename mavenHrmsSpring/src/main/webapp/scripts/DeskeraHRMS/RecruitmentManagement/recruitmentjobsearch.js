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
Wtf.recruitmentJobs= function(config){
    Wtf.recruitmentJobs.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.recruitmentJobs, Wtf.Panel, {
    initComponent: function() {
        Wtf.recruitmentJobs.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.recruitmentJobs.superclass.onRender.call(this, config);
//        this.record1 = Wtf.data.Record.create([
//        {
//            name:'id'
//        },
//        {
//            name:'jobid'
//        },
//        {
//            name:'jname'
//        },
//        {
//            name:'applydt'
//        },
//        {
//            name:'interviewdt'
//        },
//        {
//            name:'status'
//        }
//
//        ]);
//        this.reader1= new Wtf.data.KwlJsonReader1({
//            root: 'data'
//        },
//        this.record1
//        );
//
//        this.ds1 = new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
//            baseParams: {
//                flag:38 ,
//                userid:this.profId
//
//            },
//            reader:this.reader1
//
//        });
//        this.ds1.load();

        this.record = Wtf.data.Record.create([
            {
                name :'applicationid'
            },{
                name:'jobname'
            },{
                name:'jid'
            },{
                name:'jdescription'
            },{
                name:'jstartdate'
            },{
                name:"jenddate"
            },{
                name:"jdepartment"
            },{
                name:'posmasterid'
            },{
                name:'jobpositionid'
            },{
                name:'selectionstatus'
            },{
                name:'status'
            }
        ]);

        this.jobRecord = Wtf.data.Record.create([
        {
            name:'name'
        },
        {
            name:'id'
        }

        ]);
        this.reader= new Wtf.data.KwlJsonReader1({
            root: 'data',
            totalProperty:"count"
        },
        this.record
        );

        this.position="";
        this.ds = new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/jobsearch.rec",
            reader:this.reader

        });

        this.ds.on("beforeload",function(){
            this.ds.baseParams={
                flag: 36,
                userid:this.profId,
                start:0,
                limit:this.myJobsgrid.pag.pageSize
            }
        },this)

        this.com1Reader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.jobRecord);

        this.com1store= new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            reader:this.com1Reader,
            baseParams:{
                flag:203,
                configid:1
            }
        });
        this.com1store.load();
        this.com1store.on("load",function(){
            this.allrec=new this.jobRecord({
                id:'All',
                name:WtfGlobal.getLocaleText("hrms.recruitment.all")
            });
            this.com1store.add(this.allrec);
        },this); 
        this.com1combo= new Wtf.form.ComboBox({
            store:this.com1store,
            valueField:'id',
            displayField:'name',
            scope:this,
            hiddenName:'position',
            selectOnFocus:true,
            emptyText: WtfGlobal.getLocaleText("hrms.recruitment.all"),
            mode:'local',
            width:150,
            editable:true,
            typeAhead :true,
            height:200,
            value:'All',
            triggerAction :'all'
        });



        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.cm = new Wtf.grid.ColumnModel([
            this.sm,{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.id"),
                sortable: true,
                dataIndex: 'jobpositionid'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                sortable: true,
                dataIndex: 'jobname'
            },
            {
                header:WtfGlobal.getLocaleText("hrms.common.department"),
                sortable: true,
                dataIndex: 'jdepartment'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.details"),
                sortable: true,
                dataIndex: 'jdescription'
            },
            {
                header:  WtfGlobal.getLocaleText("hrms.common.start.date"),
                sortable: true,
                align:'center',
                dataIndex:'jstartdate'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.due.date"),
                sortable: true,
                align:'center',
                dataIndex:'jenddate'
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.status"),
                dataIndex: 'selectionstatus',
                sortable: true,
                renderer : function(val) {
                    if(val=='Pending')
                        return '<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.recruitment.pending")+'</FONT>'
                    else if(val=='Shortlisted')
                        return '<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.recruitment.shortlisted")+'</FONT>'
                    else if(val=='In Process')
                        return '<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.recruitment.in.process")+'</FONT>'
                    else if(val=='On Hold')
                        return '<FONT COLOR="DarkGoldenRod">'+WtfGlobal.getLocaleText("hrms.recruitment.on.hold")+'</FONT>'
                    else if(val=='Rejected')
                        return '<FONT COLOR="Indigo">'+WtfGlobal.getLocaleText("hrms.recruitment.rejected")+'</FONT>'
                    else if(val=='Selected')
                        return '<FONT COLOR="Fuchsia">'+WtfGlobal.getLocaleText("hrms.recruitment.Selected")+'</FONT>'
                    else
                        return '<FONT COLOR="Brown">'+val+'</FONT>'
                }
            }
            ]);

        this.applybutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.ApplyForJob"),
            iconCls:'pwndHRMS assignbuttonIcon',
            scope:this,
            id:'recapplyforjob',
            disabled:true,
            handler:this.applyjob
        });
        this.canceljobButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.CancelJob"),
            disabled:true,
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            minWidth:90,
            tooltip: {
                 title:WtfGlobal.getLocaleText("hrms.recruitment.JobPosition"),
                 text:WtfGlobal.getLocaleText("hrms.recruitment.CancelJob.tooltip")
            },
            scope:this,
            handler:function(){
                this.rec=this.sm.getSelections();
                if(this.rec.length>0)
                {
                    this.canceljob();
                }
                else{
                    Wtf.MessageBox.show({
                        title:  WtfGlobal.getLocaleText("hrms.common.warning"),
                        msg: WtfGlobal.getLocaleText("hrms.recruitment.Pleaseselectajobposition"),
                        buttons: Wtf.MessageBox.OK

                    });
                }
            }
        });
        this.myJobsgrid=new Wtf.KwlGridPanel({ 
            cm:this.cm,
            store:this.ds,
            sm:this.sm,     
            layout:'fit',
            border:false,
            displayInfo:true,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.SearchbyJobId"),
            searchField:"jobpositionid",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.Nojobstodisplay")+'</font></center>'
            },
            tbar:[
            '-',this.applybutton,this.canceljobButton,'->',WtfGlobal.getLocaleText("hrms.recruitment.selectby.designation")+':', this.com1combo
            ]
        });
        
        var arrId=new Array();           
        arrId.push("recviewprerequisites");    
        enableDisableButton(arrId,this.ds,this.sm);
         this.sm.on("selectionchange",function(){
             this.applybutton.disable();
             this.canceljobButton.disable();
             if(this.sm.getCount() > 0) {
                 var currentstatus = "";
                 this.canceljobButton.enable();
                 this.applybutton.enable();
                 for(var i = 0; i < this.sm.selections.length; i++) {
                     if(this.sm.selections.items[i].data.selectionstatus!='Pending'){
                        this.canceljobButton.disable();
                     }
                     if(currentstatus != this.sm.selections.items[i].data.status) {
                         this.applybutton.disable();
                         currentstatus = this.sm.selections.items[i].data.status;
                         break;
                     }
                     currentstatus = this.sm.selections.items[i].data.status;
                 }
             }
        },this);

        this.com1combo.on("render",function(){
            this.com1combo.setValue("All");
            this.search();
        },this);
        this.add(this.myJobsgrid);
        this.com1combo.on('select',this.search,this);
       
    },     
//    prerequisite:function(){
//        this.arr1=this.sm.getSelections();
//        this.pasid=this.arr1[0].get('posmasterid');
//        this.viewPrereqWindow=new Wtf.myprequisites({
//            layout:'fit',
//            iconCls:getButtonIconCls(Wtf.btype.winicon),
//            title: 'Prerequites For This Job',
//            closable:true,
//            id:'prerequisites'+this.profId,
//            width:600,
//            height:400,
//            border:false,
//            scope:this,
//            posid:this.pasid,
//            buttons:[
//            {
//                text:'Close',
//                scope:this,
//                handler:function(){
//                    Wtf.getCmp('prerequisites'+this.profId).close();
//                }
//            }
//            ]
//        }).show();
//    },
//    viewprofile:function(){
//        
//        var mainTabId = Wtf.getCmp("maintab");
//        var projectBudget = Wtf.getCmp(this.profId+'profile');
//        if(projectBudget == null){
//            projectBudget = new Wtf.ApplicationForm({
//                autoScroll:true,
//                profId:this.profId,
//                id:this.profId+'profile'
//            });
//            mainTabId.add(projectBudget);
//        }
//        mainTabId.setActiveTab(projectBudget);
//        mainTabId.doLayout();
//    },
    search:function(){
        calMsgBoxShow(202,4,true);
        this.myJobsgrid.quickSearchTF.setValue("");
        if(this.com1combo.getValue()=="All") {
            this.position="";
            this.ds.load({
                params:{
                    userid:this.profId,
                    start:0,
                    limit:this.myJobsgrid.pag.pageSize
                }
            });
        } else {
            this.position=this.com1combo.getValue();
            this.ds.load({
                params:{
                    userid:this.profId,
                    position:this.com1combo.getValue(),
                    start:0,
                    limit:this.myJobsgrid.pag.pageSize
                }
            });
        }
        this.ds.on("load",function(){
            if(msgFlag==1){
                Wtf.MessageBox.hide();
            }
        },this);

    },
    applyjob:function(){
        if(this.sm.hasSelection()) {
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms.recruitment.AreyousureApplyselectedjobposition"),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes') {
                        this.poskey=this.sm.getSelections();
                        this.ids=[];
                        for(var i=0;i<this.poskey.length;i++){
                            this.ids.push(this.poskey[i].get('jid'));
                        }
                        this.currentdate= new Date().format("m/d/Y")
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Rec/Job/applyforjobexternal.rec",
                            params:{
                                flag:37,
                                apcntid:this.profId,
                                posid:this.ids,
                                applydt: this.currentdate,
                                employeetype:0
                            }
                        },this,
                        function(){
                            calMsgBoxShow(125,0);
                            this.sm.clearSelections();
                            if(this.com1combo.getValue()=="All") {
                                this.ds.load({
                                    params:{
                                        flag: 36,
                                        userid:this.profId,
                                        start:0,
                                        limit:this.myJobsgrid.pag.pageSize
                                    }
                                });
                            } else {
                                this.ds.load({
                                    params:{
                                        flag: 36,
                                        position:this.com1combo.getValue(),
                                        userid:this.profId,
                                        start:0,
                                        limit:this.myJobsgrid.pag.pageSize
                                    }
                                });
                            }
                            if(Wtf.getCmp('recjobstatusgrid'+this.profId) != null)
                                Wtf.getCmp('recjobstatusgrid'+this.profId).getStore().load();
                        },
                        function(){
                            calMsgBoxShow(126,1);
                        })
                    }
                }
            })
        }else{
            calMsgBoxShow(42,0);
        }
    },
    canceljob:function(){
        this.delkey=this.sm.getSelections();
        this.jobids=[];
        this.applyflag=false;
        for(var i=0;i<this.delkey.length;i++){
            if(this.delkey[i].get('selectionstatus')=='Pending')
                this.applyflag=true;
        }
        if(this.applyflag){
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms.recruitment.Areyousurecancelselectedjobposition"),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.poskey=this.sm.getSelections();
                        this.ids=[];
                        for(var i=0;i<this.poskey.length;i++){
                            this.ids.push(this.poskey[i].get('applicationid'));
                        }
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Rec/Job/canceljobexternal.rec",
                            params:{
                                flag:37,
                                applicationid:this.ids,
                                employeetype:1
                            }
                        },this,
                        function(){
                            calMsgBoxShow(212,0);
                            this.sm.clearSelections();
                            this.ds.load({
                                params:{
                                    flag: 36,
                                    userid:this.profId,
                                    start:0,
                                    limit:this.myJobsgrid.pag.pageSize
                                }
                            });
                            if(Wtf.getCmp('recjobstatusgrid'+this.profId) != null)
                               Wtf.getCmp('recjobstatusgrid'+this.profId).getStore().load();
                        },
                        function(){
                            calMsgBoxShow(126,1);
                        })
                    }
                }
            })
        }
        else{
            calMsgBoxShow(129,0);
            this.jbGrid.getSelectionModel().clearSelections();
        }
    },
    dateRenderer: function(v) { 
        if(!v) return v;
        return v.format("l, F d, Y");
    }
});
