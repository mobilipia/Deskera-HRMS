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

Wtf.InternalJob = function(config){
    Wtf.apply(this, config);
    Wtf.InternalJob.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.InternalJob, Wtf.Panel, {
    initComponent: function(){
        Wtf.InternalJob.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.InternalJob.superclass.onRender.call(this,config);
        this.jobForm();
        this.add(this.jbGrid);
        this.on("activate",function(){this.doLayout();});

    },
    jobForm:function(){
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.interjobRecord=Wtf.data.Record.create([
        {
           name :'applicationid'
        },
        {
            name:'posid'
        },

        {
            name:'posname'
        },

        {
            name:'details'
        },

        {
            name:'department'
        },

        {
            name:'manager'
        },

        {
            name:'startdate',type:'date'
        },

        {
            name:'enddate',type:'date'
        },

        {
            name:'jobtype'
        },
        {
            name:'status'
        },
        {
            name:'jobid'
        },
        {
            name:'selectionstatus'
        }
        ]);
        this.interreader=new Wtf.data.KwlJsonReader1({
            root: "data",
            totalProperty:'count'
        },this.interjobRecord);


        this.internaljobGDS= new Wtf.data.Store({
//            url:Wtf.req.base + 'hrms.jsp',
            url:"Rec/Job/getInternalJobs.rec",
            reader:this.interreader,
            baseParams:{
                flag:7,
                jobtype:'internal',
                employee:true,
                jobstatus:0
            }
        });
        calMsgBoxShow(202,4,true);
        this.internaljobGDS.load();
        this.internaljobGDS.on("load",function(){
            if(msgFlag==1){
            WtfGlobal.closeProgressbar();
            }
        },this);

        this.cm = new Wtf.grid.ColumnModel(
            [
            this.sm2,{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.id"),
                dataIndex: 'jobid',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'posname',
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.PositionDetails"),
                dataIndex: 'details',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'department',
                sortable: true
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.approving.manager"),
                dataIndex: 'manager',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),
                dataIndex: 'startdate',
                align:'center',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.end.date"),
                dataIndex: 'enddate',
                align:'center',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true
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

        this.viewjobPos= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.ViewJobProfile"),
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.ViewJobProfile.tooltip"),
            minWidth:105,
            disabled:true,
            handler:this.viewjobpos,
            scope:this
        });
        this.viewProfile= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.view.edit.profile"),
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.view.edit.profile.tooltip"),
            minWidth:105,
            disabled:true,
            handler:this.viewProfile,
            scope:this
        });
        this.applyjobButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.ApplyForJob"),
            disabled:true,
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            minWidth:90,
            tooltip: {
                 title:WtfGlobal.getLocaleText("hrms.recruitment.JobPosition"),
                 text:WtfGlobal.getLocaleText("hrms.recruitment.ApplyForJob.tooltip")
            },
            scope:this,
            disabled:true,
            handler:function(){
                this.rec=this.sm2.getSelections();
                if(this.rec.length>0)
                {
                    this.applyforjob();
                }
                else{
                    Wtf.MessageBox.show({
                        msg: WtfGlobal.getLocaleText("hrms.recruitment.Pleaseselectajobposition"),
                        buttons: Wtf.MessageBox.OK

                    });
                }
            }
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
                this.rec=this.sm2.getSelections();
                if(this.rec.length>0)
                {
                    this.canceljob();
                }
                else{
                    Wtf.MessageBox.show({
                        msg: WtfGlobal.getLocaleText("hrms.recruitment.Pleaseselectajobposition"),
                        buttons: Wtf.MessageBox.OK

                    });
                }
            }
        });
//        this.viewjobButton= new Wtf.Button({
//                 text:'View Job Applicants',
//                 id:'viewinternaljobs',
//                iconCls:getButtonIconCls(Wtf.btype.viewbutton),
//                minWidth:114,
//                disabled:true,
//                tooltip: {
//                    title:"Job Position",
//                    text:"View the list of applicants who have applied for internal jobs."
//                },
//                scope:this,
//                handler:function(){
//                    if(this.jbGrid.getSelectionModel().getCount()==0||this.jbGrid.getSelectionModel().getCount()>1)
//                       calMsgBoxShow(42,0);
//                    else
//                        this.viewjobApplicants();
//                }
//        });

        this.toolbarItems = new Array();
   if(!WtfGlobal.EnableDisable(Wtf.UPerm.internaljobboard, Wtf.Perm.internaljobboard.apply))
   {
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.viewjobPos);
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.applyjobButton);
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.canceljobButton);
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.viewProfile);
   }
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.internaljobboard, Wtf.Perm.internaljobboard.viewjobs))
//            {   this.toolbarItems.push('-');
//                this.toolbarItems.push(this.viewjobButton);
//
//            }

        this.jbGrid = new Wtf.KwlGridPanel({
            border: false,
            id:'interjobgrid',
            store: this.internaljobGDS,
            cm: this.cm,
            sm: this.sm2,
            enableColumnHide: false,
            trackMouseOver: true,
            loadMask:true,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.SearchbyJobId"),
            searchField:"jobid",
            viewConfig: {
                forceFit: true,
                emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg")+'</font></center>'
            },
            displayInfo:true,
            tbar:this.toolbarItems
        });
         this.sm2.on("selectionchange",function(){
//             WtfGlobal.enableDisableBtnArr(this.toolbarItems, this.jbGrid, [], [1]);
             this.applyjobButton.disable();
             this.canceljobButton.disable();
             this.viewjobPos.disable();
             this.viewProfile.disable();
             if(this.sm2.getCount() == 1){
                 this.viewjobPos.enable();
             }
             if(this.sm2.getCount() > 0) {
                 var currentstatus = "";
                 this.canceljobButton.enable();
                 this.applyjobButton.enable();
                 this.viewProfile.enable();
                 for(var i = 0; i < this.sm2.selections.length; i++) {
                     if(this.sm2.selections.items[i].data.selectionstatus!='Pending'){
                        this.canceljobButton.disable();
                     }
                     if(currentstatus != this.sm2.selections.items[i].data.status) {
                         this.applyjobButton.disable();
                         currentstatus = this.sm2.selections.items[i].data.status;
                         break;
                     }
                     currentstatus = this.sm2.selections.items[i].data.status;
                 }
             }
//            if(!WtfGlobal.EnableDisable(Wtf.UPerm.internaljobboard, Wtf.Perm.internaljobboard.viewjobs)){
//                WtfGlobal.enableDisableBtnArr(this.toolbarItems, this.jbGrid, [], [1]);
//            }
//            else{
//                WtfGlobal.enableDisableBtnArr(this.toolbarItems, this.jbGrid, [], [1]);
//            }
        },this);

//        this.sm2.on("rowselect",function(sm,index,r){
//                if(r.get('status')=="1")
//                {
//                    calMsgBoxShow(124,0);
//                    this.sm2.clearSelections();
//                }
//        },this);
//        this.jbGrid.on('cellclick',this.onCellClick, this);
    }, 
//    deletejobpositons:function(){
//        this.deleteflag=true;
//        this.delkey=this.sm2.getSelections();
//        this.jobids=[];
//        for(var i=0;i<this.delkey.length;i++){
//            if(this.delkey[i].get('status')=='0')
//                this.deleteflag=false;
//        }
//        if(this.deleteflag){
//            for(i=0;i<this.delkey.length;i++){
//                this.jobids.push(this.delkey[i].get('posid'));
//            }
//            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + 'hrms.jsp',
//                params:  {
//                    flag:9,
//                    jobids:this.jobids
//                }
//            },
//            this,
//            function(){
//                calMsgBoxShow(60,0);
//                this.internaljobGDS.load();
//            },
//            function(){
//                calMsgBoxShow(49,0);
//            })
//        }
//        else{
//            calMsgBoxShow(129,0);
//            this.jbGrid.getSelectionModel().clearSelections();
//        }
//    },
    applyforjob:function(){
        this.delkey=this.sm2.getSelections();
        this.jobids=[];
        this.applyflag=true;
        for(var i=0;i<this.delkey.length;i++){
            if(this.delkey[i].get('status')=='1')
                this.applyflag=false;
        }
        if(this.applyflag){
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms.recruitment.AreyousureApplyselectedjobposition"),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.poskey=this.sm2.getSelections();
                        this.ids=[];
                        for(var i=0;i<this.poskey.length;i++){
                            this.ids.push(this.poskey[i].get('posid'));
                        }
                        this.currentdate= new Date().format("m/d/Y")
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Rec/Job/applyforjobexternal.rec",
                            params:{
                                flag:37,
                                posid:this.ids,
                                applydt: this.currentdate,
                                employeetype:1
                            }
                        },this,
                        function(){
                            calMsgBoxShow(125,0);
                            this.sm2.clearSelections();
                            this.internaljobGDS.load();
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
    },canceljob:function(){
        this.delkey=this.sm2.getSelections();
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
                        this.poskey=this.sm2.getSelections();
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
                            this.sm2.clearSelections();
                            this.internaljobGDS.load();
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
    }, viewjobpos:function(){
        if(this.sm2.getCount()==0||this.sm2.getCount()>1)
        {
            calMsgBoxShow(42,0);
        }
        else
        {
            var jobid=this.jbGrid.getSelectionModel().getSelected().get('posid');
            var main=Wtf.getCmp('recruitmentmanage');
            var edsignupTab=Wtf.getCmp(this.id+jobid+'Profile');
            if(edsignupTab==null)
            {
                edsignupTab=new Wtf.jobProfile({
                    id:this.id+jobid+'Profile',
                    title:WtfGlobal.getLocaleText({key:"hrms.common.empsProfile",params:[this.jbGrid.getSelectionModel().getSelected().get('posname')]}),
                    layout:'fit',
                    border:false,
                    autoScroll:true,
                    closable:true,
                    store:this.jobmasterGDS,
                    viewOnlyType:true,
                    positionid:this.sm2.getSelected().get('posid'),
                    iconCls:'pwndHRMS jobprofiletabIcon'
                });
                main.add(edsignupTab);
            }
            main.setActiveTab(edsignupTab);
            main.doLayout();
            Wtf.getCmp("as").doLayout();
            edsignupTab.on('editjobprofile',function(){
                this.jobmasterGDS.load({
                    params:{
                        start:this.jbGrid.pag.cursor,
                        limit:this.jbGrid.pag.pageSize
                    }
                })
            },this);
        }
    },
    viewProfile:function(){
        var main=Wtf.getCmp("recruitmentmanage");
        var edsignupTab=Wtf.getCmp(this.id+"emprecprofile");
        if(edsignupTab==null)
        {
            var flag=true;
            if(userroleid==1){
                flag=false;
            }
            edsignupTab=new Wtf.myProfileWindow({
                title:"<div wtf:qtip="+WtfGlobal.getLocaleText("hrms.administration.my.profile.tooltip")+">"+WtfGlobal.getLocaleText("hrms.administration.my.profile")+"</div>",
                id:this.id+'emprecprofile',
                editperm:flag,
                manager:false,
                closable:true,
                lid:loginid,
                layout:'fit',
                iconCls:'pwnd myProfileIcon'
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    }

//    viewjobApplicants:function(){
//      var uid=this.jbGrid.getSelectionModel().getSelected().get('posid');
//      var main =Wtf.getCmp('jobboardTab');
//        var mainSuccessionTab=Wtf.getCmp(uid+'jobs');
//        if(mainSuccessionTab==null)
//        {
//            mainSuccessionTab=new Wtf.viewApplicants({
//                title:this.jbGrid.getSelectionModel().getSelected().get('posname')+"'s Applicants",
//                id:uid+'jobs',
//                border:false,
//                layout:'fit',
//                closable:true,
//                jid:this.jbGrid.getSelectionModel().getSelected().get('posid'),
//                iconCls:getTabIconCls(Wtf.etype.hrmsmygoals)
//            });
//            main.add(mainSuccessionTab);
//        }
//        main.setActiveTab(mainSuccessionTab);
//        main.doLayout();
//    },
//    onCellClick:function(g,i,j,e){
//        e.stopEvent();
//        var el=e.getTarget("a");
//        if(el==null)return;
//        var header=g.getColumnModel().getDataIndex(j);
//        var rec=this.sm2.getSelected().data;
//        if(header=="jobid"){
//            var main=Wtf.getCmp("recruitmentmanage");
//            var edjobTab=Wtf.getCmp(rec.jobid+'Jobs');
//            if(edjobTab==null)
//            {
//                edjobTab=new Wtf.jobProfile({
//                    id:rec.jobid+'Jobs',
//                    title:rec.jobid+"'s Job Profile",
//                    iconCls:'pwndHRMS jobprofiletabIcon',
//                    layout:'fit',
//                    closable:true,
//                    jobposid:rec.jobid,
//                    positionid:rec.posid,
//                    disableSubmit:true,
//                    border:false,
//                    autoScroll:true
//                });
//                main.add(edjobTab);
//            }
//            main.setActiveTab(edjobTab);
//            main.doLayout();
//            Wtf.getCmp("as").doLayout();
//        }
//    }
});





