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
Wtf.allApps = function(config){
    Wtf.apply(this, config);
    Wtf.allApps.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.allApps, Wtf.Panel, {
    initComponent: function(){
        Wtf.allApps.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.allApps.superclass.onRender.call(this,config);
        
        if(!Wtf.StoreMgr.containsKey("status")){
            Wtf.statusStore.load();
            Wtf.StoreMgr.add("status",Wtf.statusStore)
        }
        
        if(!Wtf.StoreMgr.containsKey("callback")){
            Wtf.callbackStore.load();
            Wtf.StoreMgr.add("callback",Wtf.callbackStore)
        }
        
        this.allapplsGrid();
        this.getAdvanceSearchComponent();
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
        this.objsearchComponent.on("saveStore",this.saveStore, this);
        this.objsearchComponent.on("reloadgridStore",this.reloadgridStore, this);
        this.pan= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[this.objsearchComponent,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.applGrid]
            }]
        });
        this.add(this.pan);
        this.exportinfo();
        this.type = 0;
        this.status = "";
        this.statusid=-1;
    },
    loaddata : function(){
        var mainArray=new Array();
        this.objsearchComponent.cm = this.searchparams;
        for (i=0;i<this.objsearchComponent.cm.length;i++) {
            var tmpArray=new Array();
            if(this.objsearchComponent.cm[i].dbname && (this.objsearchComponent.cm[i].hidden==undefined || this.objsearchComponent.cm[i].hidden==false)) {
                var header=headerCheck(WtfGlobal.HTMLStripper(this.objsearchComponent.cm[i].header));
                tmpArray.push(header);
                tmpArray.push(this.objsearchComponent.cm[i].dbname);
                tmpArray.push(this.objsearchComponent.cm[i].xtype);
                tmpArray.push(this.objsearchComponent.cm[i].cname);
                tmpArray.push(this.objsearchComponent.cm[i].iscustom);
                mainArray.push(tmpArray);
            }
        }
        var myData = mainArray;
        this.objsearchComponent.combostore.removeAll();
        this.objsearchComponent.combostore.loadData(myData);

    },
    allapplsGrid:function(){
        this.statusstore = new Wtf.data.SimpleStore({
            fields:['id','status', 'statushidden'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.recruitment.pending"), "Pending"],
            ['2',WtfGlobal.getLocaleText("hrms.recruitment.shortlisted"), "Shortlisted"],
            ['3',WtfGlobal.getLocaleText("hrms.recruitment.in.process"), "In Process"],
            ['4',WtfGlobal.getLocaleText("hrms.recruitment.on.hold"), "On Hold"],
            ['5', WtfGlobal.getLocaleText("hrms.common.All"), "All"]
            ]
        });

        this.applicantstore = new Wtf.data.SimpleStore({
            fields:['id','applicant', 'applicanthidden'],
            data: [
            ['1', WtfGlobal.getLocaleText("hrms.common.Internal"),"Internal"],
            ['2', WtfGlobal.getLocaleText("hrms.common.External"),"External"],
            ['3', WtfGlobal.getLocaleText("hrms.common.All"),"All"]
            ]
        });

        this.applicantcmb=new Wtf.form.ComboBox({
            store:this.applicantstore,
            displayField: 'applicant',
            valueField:'applicanthidden',
            anchor:'100%',
            emptyText:WtfGlobal.getLocaleText("hrms.common.select.status"),//'Select Status',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead: true,
            value:Wtf.cmpPref.defaultapps,
            mode: 'local',
            width:80
        });
       
        this.applicantcmb.on("render",function(){
            if(this.isInternal)
            {
            	this.applicantcmb.setValue("Internal");
                this.allAppsGDS.baseParams={
                    employeetype:1,
                    status:status,
                    statusid:this.statusid,
                    flag:38,
                    gridst:0
                },
                this.allAppsGDS.load({
                    params:{
                        start:0,
                        limit:this.applGrid.pag.pageSize,
                        ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
                    }
                });
            }
        },this);

        this.statuscmb=new Wtf.form.ComboBox({
            store:this.statusstore,
            displayField: 'status',
            valueField:'statushidden',
            anchor:'100%',
            emptyText:WtfGlobal.getLocaleText("hrms.common.select.status"),//'Select Status',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead: true,
            value:'All',
            mode: 'local',
            width:80           
        });
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });


        this.record = Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'posid'
        },{
            name:'apcntid'
        },{
            name:'jobid'
        },{
            name:'cname'
        },{
            name:'email'
        },{
            name:'jname'
        },{
            name:'applydt',
            type:'date'
        },{
            name:'status'
        },{
            name:'addr'
        },{
            name:'contact'
        },{
            name:'callback'
        },{
            name:'file'
        },{
            name:'recruiter'
        },{
            name:'rank'
        },{
            name:'interviewdt',
            type:'date'
        },{
            name:'callback'
        },{
            name:'interviewplace'
        },{
            name:'interviewcomment'
        },{
            name:'file'
        },{
            name:'rejectedbefore'
        },{
            name:WtfGlobal.getLocaleText("hrms.common.jobid"),
            mapping:'jobpositionid'
        },{
            name:WtfGlobal.getLocaleText("hrms.common.department"),
            mapping:'department'
        },{
            name:'vacancy'
        },{
            name:'filled'
        },{
            name:'docid'
        },{
            name:'employeetype'
        }]);
        
        this.reader= new Wtf.data.KwlJsonReader1({
            root: 'data',
            totalProperty:"count"
        },
        this.record
        );
        var status="";
        var statusid=-1;
        var employeetype=Wtf.cmpPref.defaultapps=="Internal"?"1":"0";
        this.allAppsGDS = new Wtf.ux.MultiGroupingStore({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getJobApplications.rec",
            baseParams: {
                flag:38 ,
                gridst:0,
                status:status,
                statusid:statusid,
                employeetype:employeetype
            },
            reader:this.reader,
            sortInfo:{
                field: WtfGlobal.getLocaleText("hrms.common.department"),
                direction: "ASC"
            },
            groupField:[WtfGlobal.getLocaleText("hrms.common.department"),WtfGlobal.getLocaleText("hrms.common.jobid")]

        });
        calMsgBoxShow(202,4,true);
        this.allAppsGDS.load();
        this.allAppsGDS.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);

        this.interviewdate = new Wtf.form.DateField({
            name:'interviewdt',
            width:200,
            allowBlank:false,
            format:'m/d/Y'
        });

        this.addprospect=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.add.prospect"),//'Add Prospect',
            iconCls:'iconaddClass',
            minWidth:90,
            scope:this,
            handler:this.prospect
        });

        this.cm = new Wtf.grid.ColumnModel(
            [this.sm2,{
                header: WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
                dataIndex: WtfGlobal.getLocaleText("hrms.common.department"),
                pdfwidth:80,
                exportDataIndex:'Department'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.candidate.name"),//"Candidate Name",
                dataIndex: 'cname',
                sortable: true,
                pdfwidth:80,
                exportDataIndex:'cname'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email ID",
                dataIndex: 'email',
                sortable: true,
                renderer: WtfGlobal.renderEmailTo,
                pdfwidth:80,
                exportDataIndex:'email'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.contact.no"),//"Contact No.",
                dataIndex: 'contact',
                sortable: true,
                pdfwidth:80,
                exportDataIndex:'contact'
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.applied.for"),//"Job Id Applied For",
                dataIndex: WtfGlobal.getLocaleText("hrms.common.jobid"),
                sortable: true,
                pdfwidth:80,
                exportDataIndex:'JobId'
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.applied.date"),//"Applied Date",
                dataIndex: 'applydt',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true,
                pdfwidth:80,
                exportDataIndex:'applydt'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.status"),//"Status",
                dataIndex: 'status',
                align: 'center',
                sortable: true,
                exportDataIndex:'status',
                renderer : function(val) {
                    if(val=='Pending')
                        return ('<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.recruitment.pending")+'</FONT>');//'<FONT COLOR="blue">Pending</FONT>'
                    else if(val=='Shortlisted')
                        return ('<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.recruitment.shortlisted")+'</FONT>');//'<FONT COLOR="green">Shortlisted</FONT>'
                    else if(val=='In Process')
                        return ('<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.recruitment.in.process")+'</FONT>');//'<FONT COLOR="red">In Process</FONT>'
                    else
                        return ('<FONT COLOR="DarkGoldenRod">'+WtfGlobal.getLocaleText("hrms.recruitment.on.hold")+'</FONT>');//'<FONT COLOR="DarkGoldenRod">On Hold</FONT>'
                },
                pdfwidth:80
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.assigned.interviewer"),//"Assigned Interviewer",
                dataIndex: 'recruiter',
                sortable: true,
                pdfwidth:80,
                exportDataIndex:'recruiter'
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.interview.date"),//"Interview Date",
                dataIndex: 'interviewdt',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true,
                pdfwidth:80,
                exportDataIndex:'interviewdt'
            },{
                header:WtfGlobal.getLocaleText("hrms.recruitment.rank"),//"Rank",
                dataIndex:'rank',
                sortable:true,
                pdfwidth:80,
                exportDataIndex:'rank'
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.request.call.back"),//"Request Call Back",
                dataIndex: 'callback',
                exportDataIndex:'callback',
                renderer : function(val) {
                    if(val=="No")
                        return WtfGlobal.getLocaleText("hrms.recruitment.callback.No");
                    else
                        return WtfGlobal.getLocaleText("hrms.recruitment.callback.Yes")
                },
                align: 'center',
                sortable: true,
                pdfwidth:80
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.rejected.before"),//"Rejected Before",
                dataIndex: 'rejectedbefore',
                align: 'center',
                exportDataIndex:'rejectedbefore',
                sortable: true,
                renderer : function(val) {
                    if(val==0)
                        return ('<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.common.no")+'</FONT>');//'<FONT COLOR="green">No</FONT>'
                    else
                        return ('<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.common.yes")+'</FONT>');//'<FONT COLOR="red">Yes</FONT>'
                },
                pdfwidth:80
            },{
                header:WtfGlobal.getLocaleText("hrms.recruitment.resume"),//"Resume",
                dataIndex:"id",
                exportDataIndex:'id',
                renderer:function(a,b,c,d,e,f){
                    if(c.data.employeetype == 0 && c.data.docid.length > 0)
                    return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"Common/Document/downloadDocuments.common?url=" + c.data.docid + "&mailattch=true&dtype=attachment&applicant=applicant\")'><div class='pwndHRMS resumeIcon' style='cursor:pointer' title='"+WtfGlobal.getLocaleText("hrms.common.Clicktodownloaddocument")+"' ></div></a></div>";
                },
                pdfwidth:80
            }]);

        var allappsbtns=new Array();
        allappsbtns.push('-', new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.allAppsGDS.load({params:{start:0,limit:this.applGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.applGrid.id).setValue("");
        	}
     	}));
        this.scheduleinterview= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.schedule.interview"),//'Schedule Interview',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.schedule.interview.tooltip"),//"Create, edit and manage interview scheduling for the short listed applicants.",
            iconCls:'pwndCommon calendarbuttonIcon',
            minWidth:114,
            disabled:true,
            scope:this,
            handler:function(){
                var type=this.applicantcmb.getValue();
                if(type=="Internal"){
                    type=1;
                }else if(type=="All"){
                    type=2;
                }else{
                    type=0;
                }
                this.type = type;
                this.rec=this.sm2.getSelections();
                if(this.rec.length>0)
                {
                    this.interviewsch= new Wtf.interview({
                        modal:true,
                        title:WtfGlobal.getLocaleText("hrms.recruitment.schedule.interview"),//"Schedule Interview",
                        resizable:false,
                        layout:'fit',
                        reason:'forallapps',
                        appid:this.id,
                        apcntid:this.sm2.getSelected().get("apcntid"),
                        employeetype:type,
                        appgrid:this.applGrid
                    }).show();
                }
                else{
                    Wtf.MessageBox.show({
                        msg: WtfGlobal.getLocaleText("hrms.recruitment.please.select.application"),//'Please select an application.',
                        buttons: Wtf.MessageBox.OK
                    });
                }
            }
        });
        this.deleteapp= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.delete.application"),//'Delete Application',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.delete.application.tooltip"),//"Delete the application of the candidate which is not required anymore.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:109,
            disabled:true,
            scope:this,
            handler:this.deleteapps
        });
        this.viewprofile= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.view.profile"),//'View Profile',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.view.profile.tooltip"),//"Access detailed profiles for each selected applicant.",
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:81,
            disabled:true,
            scope:this,
            id:this.id+'viewprofile',
            handler:this.viewprofile
        });
        this.editprospect= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.edit.prospect"),//'Edit Prospect',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.edit.prospect.tooltip"),//"Edit the status of the prospect as and when required.",
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            minWidth:85,
            disabled:true,
            scope:this,
            handler:this.editprospectfun
        });
        this.advanceSearchBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.common.advanced.search"),//"Advanced Search",
            id:'advanced3',// In use, Do not delete
            scope : this,
            tooltip:WtfGlobal.getLocaleText("hrms.common.advanced.search.tooltip"),//'Search for multiple terms in multiple fields',
            handler : this.configurAdvancedSearch,
            iconCls : 'pwnd searchtabpane'
        });
        
        
        this.searchparams=[{
        	name:"department",
        	dbname:"position.departmentid.id",
        	header:WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
        	xtype:'combo',
        	cname:"department"
        },{
        	name:"cname",
        	dbname:"1",
        	header:WtfGlobal.getLocaleText("hrms.common.candidate.name"),//"Candidate Name",
        	xtype:'textfield'
        },{
        	name:"email",
        	dbname:"2",
        	header:WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email ID",
        	xtype:'textfield'
        },{
        	name:"contact",
        	dbname:"3",
        	header:WtfGlobal.getLocaleText("hrms.common.contact.no"),//"Contact No.",
        	xtype:'textfield'
        },{
        	name:"applydt",
        	dbname:"applydate",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.applied.date"),//"Applied Date",
        	xtype:'datefield'
        },{
        	name:"recruiter",
        	dbname:"recruiter",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.assigned.interviewer"),//"Assigned Interviewer",
        	xtype:'combo',
        	cname:"recruiter"
        },{
        	name:"interviewdt",
        	dbname:"interviewdate",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.interview.date"),//"Interview Date",
        	xtype:'datefield'
        },{
        	name:"jobid",
        	dbname:"position.jobid",
        	header:"Job Id Applied For",
        	xtype:'textfield'
        }];
        
        this.ExportInfoBtn = new Wtf.exportButton({
            obj:this,
            menuItem:{
                csv:true,
                rowPdf:true
            },
            userinfo:true,
            get:3,
            params:{
            	isExport:true
            },
            url: "Rec/Job/getJobApplicationsExport.rec",
            filename:this.title
        });
        this.appdocs=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.documents"),//'Documents',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.documents.tooltip"),//"Select a particular row to select a candidate and view the documents uploaded by them in their profiles.",
            iconCls:getButtonIconCls(Wtf.btype.docbutton),
            id:this.id+'documentsall',
            minWidth:80,
            disabled:true,
            scope:this,
            handler:this.uploaddocuments
        });
        this.infoSender= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.send.letter"),//'Send Letter',
          //  tooltip:"Access detailed profiles for each selected applicant.",
            iconCls:getButtonIconCls(Wtf.btype.emailbutton),
            minWidth:81,
            disabled:true,
            scope:this,
            id:this.id+'infoSender',
            handler:this.infoSenderFun
        });
        
        this.exportApplications = new Wtf.Button({
            text: WtfGlobal.getLocaleText("hrms.common.ExportApplications"),
            iconCls: 'pwndExport export',
            minWidth:81,
            scope:this,
            id:this.id+'exportApplications',
            handler:this.exportApplications,
            disabled:Wtf.cmpPref.defaultapps=="External"?false:true
        });

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.allapps, Wtf.Perm.allapps.manage)){
            allappsbtns.push('-',this.scheduleinterview,'-',this.deleteapp,'-',this.viewprofile,'-',this.editprospect,'-',this.ExportInfoBtn,'-',this.advanceSearchBtn);
        }
        allappsbtns.push('->',(WtfGlobal.getLocaleText("hrms.recruitment.applicant.type")+":"));//'Applicant Type:');
        allappsbtns.push(this.applicantcmb);
        allappsbtns.push('-');
        allappsbtns.push('->',(WtfGlobal.getLocaleText("hrms.recruitment.application.status")+":"));//'Application Status:');
        allappsbtns.push(this.statuscmb);
        allappsbtns.push('-');
        this.applGrid =new Wtf.KwlEditorGridPanel({
            border: false,
            id:this.id+'allappsviewgr',
            store: this.allAppsGDS,
            cm: this.cm,
            sm: this.sm2,
            view: new Wtf.ux.MultiGroupingView({
                hideGroupedColumn :true,
                forceFit: true,
                showGroupName:false,
                enableGroupingMenu: false,                
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.pending")+" "+this.applicantcmb.getValue()+" "+WtfGlobal.getLocaleText("hrms.recruitment.application.till.now")),//WtfGlobal.emptyGridRenderer("No Pending "+this.applicantcmb.getValue()+" application till now"),
                groupTextTpl:'{text}'
            }),
            enableColumnHide: false,
            displayInfo:true,
            loadMask:true,
            clicksToEdit:1,
            trackMouseOver: true,
            bbar:[this.appdocs,'-',this.infoSender,'-',this.exportApplications],
            stripeRows: true,
            serverSideSearch: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.allapplication.grid.search.msg"),//"Search by Department, Job ID, Candidate Name",
            searchField:"cname",
            tbar:allappsbtns
        });

        var arrId=new Array();
        arrId.push(this.id+'viewprofile');
        arrId.push(this.id+'documentsall');
        enableDisableButton(arrId, this.allAppsGDS,this.sm2);
        this.interviewflag=true;
        this.sm2.on("selectionchange",function(sm,index,r){
            if(this.sm2.hasSelection()){
                this.editprospect.enable();
                this.deleteapp.enable();
                this.infoSender.enable();
                this.interviewflag=true;
                this.statusrec=this.sm2.getSelections();
                this.checkRec=this.statusrec[0].get('jobid');
                this.disableFlag=false;
                for(var i=0;i<this.statusrec.length;i++)
                {   var tempstatusid=this.statuscmb.store.find("status",this.statusrec[i].get('status')),tempflag=0;
                    if(tempstatusid==2 ||tempstatusid==3){
                        tempflag=1;
                    }
                    if(this.statusrec[i].get('status')=="Shortlisted" || this.statusrec[i].get('status')=="In Process" || tempflag==1){
                        this.scheduleinterview.enable();
                    }
                    else{
                        this.interviewflag=false;
                    }
                    if(this.checkRec!=this.statusrec[i].get('jobid')){
                        this.disableFlag=true;
                    }
                }
                if(this.disableFlag){
                    this.editprospect.disable();
                } else{
                    this.editprospect.enable();
                }
                if(this.interviewflag){
                    this.scheduleinterview.enable();
                }
                else{
                    this.scheduleinterview.disable();
                }
                if(this.sm2.getCount()==1){
                    this.appdocs.enable();
                    this.viewprofile.enable();
                }
                else{
                    this.appdocs.disable();
                    this.viewprofile.disable();
                }
            }else{
                this.scheduleinterview.disable();
                this.deleteapp.disable();
                this.viewprofile.disable();
                this.editprospect.disable();
                this.appdocs.disable();
                this.infoSender.disable();
            }
        },this); 
        this.statuscmb.on('select',function(a,b,c){
            var type=this.applicantcmb.getValue();
            if(type=="Internal"){
                type=1;
            }else if(type=="All"){
                type=2;
            }else{
                type=0;
            }
            this.type = type;
            var status=b.data.statushidden;
            var statusid=b.data.id;
            if(status=="All"){
                status="";
            }
            this.allAppsGDS.baseParams={
                employeetype:type,
                status:status,
                statusid:statusid,
                flag:38,
                gridst:0
            }
            this.status = status;
            this.statusid=statusid;
            this.allAppsGDS.load({
                params:{                    
                    start:0,
                    limit:this.applGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
                }
            });
        },this); 
        this.applicantcmb.on('select',function(a,b,c){
            var type;
            var status=this.statuscmb.getValue();
            var statusid=this.statuscmb.store.getAt(this.statuscmb.store.find("status",this.statuscmb.getValue())).data.id;
            if(status=="All"){
                status="";
                
            }
            if(statusid==5){
                statusid=-1;
            }
            if(b.data.applicanthidden=="Internal"){
                type=1;
                this.exportApplications.setDisabled(true);
            }else if(b.data.applicanthidden=="All"){
                type=2;
                this.exportApplications.setDisabled(true);
            }else{
                type=0;
                this.exportApplications.setDisabled(false);
            }
            this.type = type;
            this.status = status;
            this.statusid=statusid;
            this.allAppsGDS.baseParams={
                employeetype:type,
                status:status,
                statusid:statusid,
                flag:38,
                gridst:0
            }
            this.allAppsGDS.load({
                params:{
                    start:0,
                    limit:this.applGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
                }
            });
            this.applGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.pending")+" "+this.applicantcmb.getValue()+" "+WtfGlobal.getLocaleText("hrms.recruitment.application.till.now"));//WtfGlobal.emptyGridRenderer("No pending "+this.applicantcmb.getValue()+" application till now");
            this.applGrid.getView().refresh();
        },this); 
//        this.applGrid.on('cellclick',this.onCellClick, this);
    },             
    viewprofile:function(){
        var main,edsignupTab;
        this.arr=this.sm2.getSelections();
        this.profid=this.arr[0].get('apcntid');
        var p= this.arr[0].get('apcntid');
        if(this.arr[0].get('employeetype')==0){
            main=Wtf.getCmp("recruitmentmanage");
            edsignupTab=Wtf.getCmp(p+"Application");
            if(Wtf.getCmp(p+"Application")==null)
            {
                edsignupTab=new Wtf.createapplicantForm({
                    autoScroll:true,
                    profId: p,
                    title: this.arr[0].get('cname')+WtfGlobal.getLocaleText("hrms.common.profile"),//this.arr[0].get('cname')+"'s Profile",
                    id: p+"Application",
                    closable:true,
                    hidesubmit:true,
                    iconCls:getTabIconCls(Wtf.etype.hrmsprofile)
                });
                main.add(edsignupTab);
            }
            main.setActiveTab(edsignupTab);
            //Wtf.getCmp(this.profid).doLayout()
            main.doLayout();
            Wtf.getCmp("as").doLayout();
        }else{
            var perm=false;
            main=Wtf.getCmp("recruitmentmanage");
            edsignupTab=Wtf.getCmp(p+"Application");
            if(edsignupTab==null)
            {
                edsignupTab=new Wtf.myProfileWindow({
                    title:this.arr[0].get('cname')+WtfGlobal.getLocaleText("hrms.common.profile"),//this.arr[0].get('cname')+"'s Profile",
                    closable:true,
                    id: p+"Application",
                    layout:'fit',
                    editperm:perm,
                    lid:p,
                    manager:true,
                    report:false,
                    border:false,
                    iconCls:getTabIconCls(Wtf.etype.hrmsprofile)
                });
                main.add(edsignupTab);
            }
            main.setActiveTab(edsignupTab);
            main.setVisible(true);
            main.doLayout();
            Wtf.getCmp("as").doLayout();
        }
    },
    
    deleteapps:function(){
        if(this.sm2.hasSelection()){//Delete Record
            this.delkey=this.sm2.getSelections();
            this.ids=[];
            this.sm2.clearSelections();
            for(var i=0;i<this.delkey.length;i++){
                var rec=this.allAppsGDS.indexOf(this.delkey[i]);
                WtfGlobal.highLightRow(this.applGrid,"FF0000",5, rec);
                this.ids.push(this.delkey[i].get('id'));
            }
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:deleteMsgBox('application'),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        calMsgBoxShow(201,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url: "Rec/Job/deleteAllappliations.rec",
                            params:{
                                flag:42,
                                ids:this.ids
                            }
                        },this,
                        function(){
                            calMsgBoxShow(64,0);
                            var params={
                                start:0,
                                limit:this.applGrid.pag.pageSize
                            }
                            WtfGlobal.delaytasks(this.allAppsGDS,params);
                        },
                        function(){
                            calMsgBoxShow(54,1);

                        }

                        )
                    }
                }
            });

        }
        else{//No selection
            calMsgBoxShow(42,0);
        }
    },
    editprospectfun:function(){
        var type=this.applicantcmb.getValue();
        if(type=="Internal"){
            type=1;
        }else{
            type=0;
        }
        if(this.sm2.hasSelection()){
            this.editprospectwin=new Wtf.editprospect({
                modal:true,
                title:WtfGlobal.getLocaleText("hrms.common.edit.prospect"),//"Edit Prospect",
                iconCls:getButtonIconCls(Wtf.btype.winicon),
                autoDestroy: true,
                resizable:false,
                employeetype:type,
                layout:'fit',
                appid:this.id,
                editval:'Shortlisted'
            });
            this.editprospectwin.show();
            this.editprospectwin.on('editpload',this.gridloads,this);
        }else{
            calMsgBoxShow(42,0);
        }
    },
    infoSenderFun:function(){
        var aa = this.applGrid.getSelectionModel().getSelections();
        var userlist = "[";
        var i=0;
        while(i<aa.length){
            userlist += "{'uid':"+aa[i].data.apcntid+",'uname':"+aa[i].data.cname+",'emailid':"+aa[i].data.email+"},";
            i++;
        }
        if(userlist.length>1){
            userlist = userlist.substring(0,userlist.length-1);
        }
        userlist+="]";
        var targetWin = new Wtf.letterSenderWindow({
            layout:"fit",
            modal:true,
            title:WtfGlobal.getLocaleText("hrms.recruitment.send.letter"),//"Send Letter",
            closable:true,
            id:"letterSenderListWindow_id",
            closeAction:'close',
            width:400,
            typeimage:'../../images/payroll.gif',
            height:280,
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            scope:this,
            userlist:userlist
          //  AddEdit:addEditV,
           // generaltaxgrid:gridV
        });
        targetWin.show();


    },
    exportApplications: function(){
    	var url = "Rec/Job/exportAllApplications.rec?"+Wtf.urlEncode(Wtf.urlDecode("employeetype="+this.type+"&status="+this.status+"&statusid="+this.statusid+"&visible="+true+"&filetype="+"csv"+"&name="+this.title+"&applicationflag="+0+"&isStatusApplicable="+(this.status==""?false:true)+"&searchJson="+this.searchJson));
    	Wtf.get('downloadframe').dom.src = url;
    },
    uploaddocuments:function(){
        var type=this.applicantcmb.getValue();
        if(type=="Internal"){
            type="employee";
        }else{
            type="applicant";
        }
        var rec=this.sm2.getSelections() ;
        var appname=(rec[0].get('cname'));
        var appid=rec[0].get('apcntid');
        var main=Wtf.getCmp("recruitmentmanage");
        var edsignupTab=Wtf.getCmp('appfilepanel'+appid);
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.document_panel({
                layout:'fit',
                border:false,
                title:appname+WtfGlobal.getLocaleText("hrms.common.s.documents"),//appname+"'s Documents",
                lid:appid,
                id:'appfilepanel'+appid,
                manager:true,
                closable:true,
                app:type,
                iconCls:getTabIconCls(Wtf.etype.hrmsdocuments)
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    } ,
    gridloads:function(status){        
         if(status=='Selected'){
            var qualgrid=Wtf.getCmp('qualifiedqualifiedgr');
            if(qualgrid!=null){
                qualgrid.getStore().load();
            }
        }
        if(status=='Rejected'){
            var rejectgrid=Wtf.getCmp('rejectedrejectedgr');
            if(rejectgrid!=null){
                rejectgrid.getStore().load();
            }
        }
    },
    exportinfo:function(){
        var i,k=1;
        var column = this.applGrid.getColumnModel();
        this.pdfStore =new Wtf.data.Store({});
        for(i=0 ; i<column.getColumnCount() ; i++) { 
          if(column.isHidden(i)!=undefined||column.getColumnHeader(i)==""||column.getDataIndex(i)==""){
                    continue;
          }
          else{
                var aligned=column.config[i].align;
                var title;
                if(aligned==undefined)
                    aligned='center';
                if(column.config[i].title==undefined){
                    title=column.config[i].exportDataIndex;
                }else{
                    title=column.config[i].title;
                }
                this.newPdfRec = new Wtf.data.Record({
                    header : title,
                    title : column.config[i].header,
                    width : column.config[i].pdfwidth,
                    align : aligned,
                    index : k
                });
                this.pdfStore.insert(this.pdfStore.getCount(), this.newPdfRec);
                k++;
          }
        }
        this.grid = this.applGrid;
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.searchparams,
            searchid:this.searchid
        });
  //      this.objsearchComponent.searchid = this.searchid;
    },
    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.searchStore.load(
        {params:{
            searchid:this.searchid,
            searchFlag:3
        }});
        this.advanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
    	this.allAppsGDS.baseParams = {
            mode:114,
            employeetype:this.type,
            status:this.status,
            statusid:this.statusid,
            flag:38,
            gridst:0
        };
    	this.allAppsGDS.load();
        this.searchJson="";
        this.searchid="";
        this.objsearchComponent.hide();
        this.advanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.allAppsGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            employeetype:this.type,
            status:this.status,
            statusid:this.statusid,
            flag:38,
            gridst:0
        };
        this.allAppsGDS.load();
    },
    reloadgridStore:function(json){
        this.searchJson="";
        if(this.searchid!=undefined){
            this.searchJson=json;
        }
        this.allAppsGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            employeetype:this.type,
            status:this.status,
            statusid:this.statusid,
            flag:38,
            gridst:0
        };
        this.allAppsGDS.load();
    },
    saveStore:function(json, saveSearchName){
        this.saveJson=json;
        Wtf.Ajax.requestEx({
            url:"Common/saveSearch.common",
            params:{
                mode:115,
                saveJson:this.saveJson,
                saveSearchName:saveSearchName,
                searchFlag:3
            }
        },
        this,
        function(response){
            var res=eval('('+response+')');
            if(res.isduplicate){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText({key:"hrms.administration.remember.already.exists.change.name",params:[saveSearchName]})],0, false, 450);
            }else {
                calMsgBoxShow(204,0,false,300);
                reloadSavedSeaches();
            }
        },
        function(response){
            calMsgBoxShow(27,1);
        });
    }
});
