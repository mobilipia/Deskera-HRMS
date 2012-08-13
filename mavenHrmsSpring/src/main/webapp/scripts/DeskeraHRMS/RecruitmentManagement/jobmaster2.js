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
Wtf.jobmaster2 = function(config){
    Wtf.apply(this, config);
    Wtf.jobmaster2.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.jobmaster2, Wtf.Panel, {
	id:'AddJobsRecruitmentJobmaster2',
    initComponent: function(){
        Wtf.jobmaster2.superclass.initComponent.call(this);
    },
    onRender:function(config) {
        Wtf.jobmaster2.superclass.onRender.call(this,config);
        this.vacancy = 0;
        this.jobForm();
        this.getAdvanceSearchComponent();
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
        this.objsearchComponent.on("saveStore",this.saveStore, this);
        this.objsearchComponent.on("reloadgridStore",this.reloadgridStore, this);
        var jbPanel=new Wtf.Panel({
            border:false,
            layout:'border',
            items: (!this.jobbuttons)?[this.jbGrid]:[this.jbInfoPanel,this.jbGrid]
        });
        this.pan= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[this.objsearchComponent,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[jbPanel]
            }]
        });
        this.add(this.pan);
        /*this.on("activate",function(){
            this.doLayout();
        });*/
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
    jobForm:function(){
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.interjobRecord=Wtf.data.Record.create([{
            name:'posid'
        },{
            name:'posname'
        },{
            name:'posmasterid'
        },{
            name:'details'
        },{
            name:'department'
        },{
            name:'manager'
        },{
            name:'startdate',
            type:'date'
        },{
            name:'enddate',
            type:'date'
        },{
            name:'jobtype'
        },{ 
            name:'jobid'
        },{
            name:'positionstatus'
        },{
            name:'nopos'
        },{
            name:'posfilled'
        },{
            name:'url'
        }]);
        this.interreader=new Wtf.data.KwlJsonReader1({
            root: "data",
            totalProperty:'count'

        },this.interjobRecord);
        var emptystr="";
        if(this.agency){
            this.jobmasterGDS= new Wtf.data.Store({
//                url:Wtf.req.base + 'hrms.jsp',
                url:"Rec/Agency/viewagencyJobs.rec",
                reader:this.interreader,
                baseParams:{
                    flag: 148,
                    agencyid:this.agencyid
                }
            });
            emptystr=WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.job.assigned"));//WtfGlobal.emptyGridRenderer("No job assigned to agency till now");
        }else{
            this.jobmasterGDS= new Wtf.data.Store({
                //url:Wtf.req.base + 'hrms.jsp',
                url:"Rec/Job/getInternalJobs.rec",
                reader:this.interreader,
                baseParams:{
                    flag:7,
                    jobtype:this.agencyid?"All":Wtf.cmpPref.defaultapps,
                    jobstatus:4
                }
            });
           emptystr=WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addjobs(\""+this.id+"\")'>Get started by adding a job now...</a>");
        }
        calMsgBoxShow(202,4,true);
        this.jobmasterGDS.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.jobmasterGDS.on("load",function(a,b,c){
        if(msgFlag==1)
            WtfGlobal.closeProgressbar();
        },this);
        this.jobmasterGDS.on("beforeload",function(a,options){
         if(options!=undefined && options.params!=undefined && options.params.ss != undefined){
             this.jbGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.common.NoRecordsarefound"));
         } else {
             this.jbGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(emptystr);
         }
        },this);
        this.jobmasterGDS.on("loadexception",function(){
        if(msgFlag==1)
            WtfGlobal.closeProgressbar();
        },this);
        this.cm = new Wtf.grid.ColumnModel(
            [this.sm2,{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.id"),//"Job Id",
                dataIndex: 'jobid',
              //  width:50,
                pdfwidth:60,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
                dataIndex: 'department',
                pdfwidth:60,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),//"Designation",
                dataIndex: 'posname',
             //   width:150,
                pdfwidth:60,
                sortable: true
            },{ 
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.details"),//"Job Details",
                dataIndex: 'details',
             //   width:250,
                pdfwidth:60,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.approving.manager"),//"Approving Manager",
                dataIndex: 'manager',
                pdfwidth:60,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),//"Start Date",
                dataIndex: 'startdate',
                pdfwidth:100,
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.common.end.date"),//"End Date",
                dataIndex: 'enddate',
                renderer:WtfGlobal.onlyDateRenderer,
                pdfwidth:100,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.status"),//"Status",
                dataIndex: 'positionstatus',
                sortable: true,
                pdfwidth:60,
              //  width:45,
                renderer : function(val) {
                    if(val=='0')
                        return '<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.recruitment.open")+'</FONT>';//'<FONT COLOR="blue">Open</FONT>'
                    else if(val=='2')
                        return '<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.recruitment.expired")+'</FONT>';//'<FONT COLOR="red">Expired</FONT>'
                    else if(val=='3')
                        return '<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.recruitment.filled")+'</FONT>';//'<FONT COLOR="green">Filled</FONT>'
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.no.of.vacancies"),//"No. of Vacancies",
                dataIndex: 'nopos',
                align: 'right',
               // width:80,
                pdfwidth:60,
                sortable: true,
                renderer:WtfGlobal.numericRenderer
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.positions.filled"),//"Positions Filled",
                dataIndex: 'posfilled',
                align: 'right',
            //    width:50,
                pdfwidth:60,
                sortable: true,
                renderer:WtfGlobal.numericRenderer
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.page.link"),//"Job Page Link",
                dataIndex: 'url',
             //   width:50,
                id: "pagelink",
                hidden:(this.jobbuttons)?false:true,
                sortable: true,
                renderer:function(value, meta, record, rowIndex, colIndex, store){
                        if(record.data.jobtype!='Internal')
                        return "<div class='mailTo'><a href="+value+" target=_blank Wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.ApplyOnline")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.ApplyOnline")+"</a></div>";
                        else
                        return "";
                }
            }]); 

        this.addjobPos= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.add.job"),//'Add Job',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.add.job.tooltip"),//"Fill and submit the details mentioned to add a new job position.",
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            handler:this.addjobpos,
            scope:this
        });
        this.deljobPos= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.delete.job"),//'Delete Job',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.delete.job.tooltip"),//"The job functions which are not functional anymore can be deleted from the list.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            disabled:true,
            handler:this.deletejobpositons,
            scope:this
        });
        this.advanceSearchBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.common.advanced.search"),//"Advanced Search",
            id:'advanced3',// In use, Do not delete
            scope : this,
            tooltip:WtfGlobal.getLocaleText("hrms.common.advanced.search.tooltip"),//'Search for multiple terms in multiple fields',
            handler : this.configurAdvancedSearch,
            iconCls : 'pwnd searchtabpane'
        });
        this.editjobPos= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.view.edit.job.profile"),//'View/Edit Job Profile',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.view.edit.job.profile.tooltip"),//"Carry out necessary changes in the job information, responsibilities, skills and qualifications, and more.",
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            minWidth:105,
            disabled:true,
            handler:this.editjobpos,
            scope:this
        });
//        this.viewPrereq= new Wtf.Button({
//            text:'View Prerequisite',
//            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
//            minWidth:105,
//            handler:this.viewprereq,
//            scope:this
//        });
        this.assignAgency= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.assign.to.agency"),//'Assign To Agency',
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            handler:this.assignagency,
            disabled:true,
            scope:this
        });
        this.unassign= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.unassign.to.agency"),//'Unassign To Agency',
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            handler:this.unassignagency,
            disabled:true,
            scope:this
        });

        var expBtn=new Wtf.exportButton({
            obj:this,
            menuItem:{
                csv:true,
                pdf:true,
                rowPdf:true
            },
            get:2,
//            url:"Common/Export/.common"
            url:"Rec/Job/jobsExport.rec",
            filename:this.title
        });

        this.viewvacancies= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.view.vacancies"),//'View Vacancies',
            handler:this.viewVacancies,
            iconCls:getTabIconCls(Wtf.etype.hrmsqualification),
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.view.vacancies.tooltip"),//"View the jobs with available positions.",
            disabled:false,
            scope:this
        });
        this.clearFilterBut= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.clear.filter"),//'Clear Filter',
            handler:this.clearFilter,
            iconCls : 'pwndExport addfilter',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.job.clear.filter.tooltip"),//'Clear job status and job type filters.',
            scope:this
        });
        
        this.searchparams=[{
        	name:"jobid",
        	dbname:"jobid",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.job.id"),//"Job Id",
        	xtype:'textfield'
        },{
        	name:"department",
        	dbname:"departmentid.id",
        	header:WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
        	xtype:'combo',
        	cname:"department"
        },{
        	name:"posname",
        	dbname:"position.id",
        	header:WtfGlobal.getLocaleText("hrms.common.designation"),//"Designation",
        	xtype:'combo',
        	cname:"designation"
        },{
        	name:"details",
        	dbname:"details",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.job.details"),//"Job Details",
        	xtype:'textfield'
        },{
        	name:"manager",
        	dbname:"manager.userID",
        	header:WtfGlobal.getLocaleText("hrms.common.approving.manager"),//"Approving Manager",
        	xtype:'combo',
        	cname:"manager"
        },{
        	name:"startdate",
        	dbname:"startdate",
        	header:WtfGlobal.getLocaleText("hrms.common.start.date"),//"Start Date",
        	xtype:'datefield'
        },{
        	name:"enddate",
        	dbname:"enddate",
        	header:WtfGlobal.getLocaleText("hrms.common.end.date"),//"End Date",
        	xtype:'datefield'
        },{
//        	name:"jobstatus",
//        	dbname:"jobstatus",
//        	header:"Select Job Status",
//        	xtype:'combo',
//        	cname:"jobstatus"
//        },{
//        	name:"jobtype",
//        	dbname:"jobtype",
//        	header:"Select Job Type",
//        	xtype:'combo',
//        	cname:"jobtype"
//        },{
        	name:"nopos",
        	dbname:"noofpos",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.no.of.vacancies"),//"No. of Vacancies",
        	xtype:'numberfield'
        },{
        	name:"posfilled",
        	dbname:"positionsfilled",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.positions.filled"),//"Positions Filled",
        	xtype:'numberfield'
        }];

        this.jobtypedata=[['1','Both', WtfGlobal.getLocaleText("hrms.common.Both")],['2','External', WtfGlobal.getLocaleText("hrms.common.External")],['3','Internal', WtfGlobal.getLocaleText("hrms.common.Internal")],['4','All', WtfGlobal.getLocaleText("hrms.common.All")]];

        this.jobtypeStore=new Wtf.data.SimpleStore({
            fields: ['id','jobtype','jobtypedisplay'],
            data :this.jobtypedata
        });

        this.jobstatusdata=[['0',WtfGlobal.getLocaleText("hrms.recruitment.open")],['2',WtfGlobal.getLocaleText("hrms.recruitment.expired")],['3',WtfGlobal.getLocaleText("hrms.recruitment.filled")],['4', WtfGlobal.getLocaleText("hrms.common.All")]];

        this.jobstatusStore=new Wtf.data.SimpleStore({
            fields: ['id','jobstatus'],
            data :this.jobstatusdata
        });

        this.jobType = new Wtf.form.ComboBox({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.recruitment.job.type")+"*"),//"Job Type*",
            store:this.jobtypeStore,
            mode:'local',
            hiddenName :'jobtype',
            valueField: 'jobtype',
            displayField:'jobtypedisplay',
            forceSelection:true,
            triggerAction: 'all',
            typeAhead:true,
            loadMask:true,
            value:Wtf.cmpPref.defaultapps,
            allowBlank:false,
            emptyText:WtfGlobal.getLocaleText("hrms.recruitment.select.jobtype"),//"Select jobtype",
            width:120
        });

        this.jobStatus = new Wtf.form.ComboBox({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.recruitment.job.status")+"*"),//"Job Status*",
            store:this.jobstatusStore,
            mode:'local',
            hiddenName :'id',
            valueField: 'id',
            displayField:'jobstatus',
            forceSelection:true,
            triggerAction: 'all',
            typeAhead:true,
            loadMask:true,
            value:'4',
            allowBlank:false,
            emptyText:WtfGlobal.getLocaleText("hrms.recruitment.select.jobstatus"),//"Select jobstatus",
            width:120
        });

        var extbtns=new Array();
        if(this.jobbuttons){
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.externaljobs, Wtf.Perm.externaljobs.create)){
                extbtns.push('-');
                extbtns.push(this.addjobPos);
            }
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.externaljobs, Wtf.Perm.externaljobs.edit)){
                extbtns.push('-');
                extbtns.push(this.editjobPos);
            }
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.externaljobs, Wtf.Perm.externaljobs.deleteext)){
                extbtns.push('-');
                extbtns.push(this.deljobPos);
            }
            extbtns.push('-');
            extbtns.push(this.advanceSearchBtn);
            extbtns.push('->');
            extbtns.push('-');
            extbtns.push(expBtn);
            extbtns.push('-');
            extbtns.push(this.clearFilterBut);
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.externaljobs, Wtf.Perm.externaljobs.create)){
                extbtns.push('->','-',(WtfGlobal.getLocaleText("hrms.recruitment.job.status")+":"));//'Job Status:');
                extbtns.push(this.jobStatus);
                //extbtns.push('-');

                extbtns.push('->','-',(WtfGlobal.getLocaleText("hrms.recruitment.job.type.label")+":"));//'Job Type:');
                extbtns.push(this.jobType);
                extbtns.push('-');
            }
            extbtns.push(this.viewvacancies);
        }
        if(this.agencybuttons){
            extbtns.push('-');
            extbtns.push(this.assignAgency);

            extbtns.push('->','-',(WtfGlobal.getLocaleText("hrms.recruitment.job.status")+":"));//'Job Status:');
            extbtns.push(this.jobStatus);
        }
        if(!this.agencybuttons && !this.jobbuttons){
            extbtns.push('-');
            extbtns.push(this.unassign);
        }
        this.jbInfoPanel=new Wtf.Panel({
            html:"<div style='padding:5px;'><div style='height:14px;width:14px;background-color:#F5A9A9;float:left'></div>&nbsp; : "+WtfGlobal.getLocaleText("hrms.recruitment.jobs.having.today.enddate"),//"<div style='padding:5px;'><div style='height:14px;width:14px;background-color:#F5A9A9;float:left'></div>&nbsp; : Jobs having today as end date</div>",
            region:'north',
            border:false
        });
        if(!this.jobbuttons){
            if(this.agency){
                extbtns.push('->');
            } else {
                extbtns.push('-');
            }
            extbtns.push({
                xtype:"panel",
                border:false,
                width:200,
                html:"<div style='height:14px;width:14px;background-color:#F5A9A9;float:left'></div>&nbsp; : "+WtfGlobal.getLocaleText("hrms.recruitment.jobs.having.today.enddate")//"<div style='height:14px;width:14px;background-color:#F5A9A9;float:left'></div>&nbsp; : Jobs having today as end date"
            });
        }
        this.jbGrid = new Wtf.KwlGridPanel({
            region:'center',
            border: false,
            store: this.jobmasterGDS,
            cm: this.cm,
            sm: this.sm2,
            enableColumnHide: false,
            trackMouseOver: true,
           // autoScroll:true,
            loadMask:true,
            stripeRows: true,
            displayInfo:true,
            serverSideSearch:true,
            //loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.job.grid.search.msg"),//"Search by Job Id, Job Detail, Department, Designation, Manager",
            searchField:"jobid",
            viewConfig:{
                forceFit:true,
               // emptyText:emptystr,
                getRowClass: function(record) {
                    if(record.data.enddate.format("Y-m-d")==new Date().format("Y-m-d"))
                        return 'red-row';
                },
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg"))//"No Records to show.")
            },
            tbar:extbtns
        });
        this.jbGrid.on("render",function(){
        	if(Wtf.cmpPref.defaultapps=="Internal")
        		this.cm.setHidden(this.cm.getIndexById("pagelink"),true);
        },this);
        this.grid=this.jbGrid;
        
        this.sm2.on("selectionchange",function(){
            if(this.jobbuttons){
                WtfGlobal.enableDisableBtnArr(extbtns, this.jbGrid, [3], [5]);
            }else if(this.agencybuttons){
                    WtfGlobal.enableDisableBtnArr(extbtns, this.jbGrid, [], [1]);
            }else if(!this.agencybuttons){
                    WtfGlobal.enableDisableBtnArr(extbtns, this.jbGrid, [], [1]);
            }
        },this);

        this.jobType.on('select',function(a,b,c){
            if(b.get("jobtype")=="Internal"){
                var f = this.cm.getIndexById("pagelink");
                this.cm.setHidden(f,true);
            } else {
                var f = this.cm.getIndexById("pagelink");
                this.cm.setHidden(f,false);
            }
            if(this.vacancy==1){
                this.jbGrid.getStore().baseParams={
                    jobtype:b.data.jobtype,
                    jobstatus:this.jobStatus.getValue(),
                    flag:7,
                    vacancy:'vacancy'
                }
            } else {
                this.jbGrid.getStore().baseParams={
                    jobtype:b.data.jobtype,
                    jobstatus:this.jobStatus.getValue(),
                    flag:7
                }
            }
            this.jbGrid.getStore().load({
                params:{
                    start:0,
                    limit:this.jbGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.jbGrid.id).getValue()
                }
            });
        },this); 
        
        this.jobStatus.on('select',function(a,b,c){
            if(this.agencybuttons){
                this.jbGrid.getStore().baseParams={
                        jobtype:"All",
                        jobstatus:b.data.id,
                        flag:7
                    }
            } else {
                if(this.vacancy==1){
                    this.jbGrid.getStore().baseParams={
                        jobtype:this.jobType.getValue(),
                        jobstatus:b.data.id,
                        flag:7,
                        vacancy:'vacancy'
                    }
                } else {
                    this.jbGrid.getStore().baseParams={
                        jobtype:this.jobType.getValue(),
                        jobstatus:b.data.id,
                        flag:7
                    }
                }
            }
            this.jbGrid.getStore().load({
                params:{
                    start:0,
                    limit:this.jbGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.jbGrid.id).getValue()
                }
            });
        },this);
    },
    clearFilter:function(){
        this.jbGrid.getStore().baseParams={
                jobtype:Wtf.cmpPref.defaultapps,
                jobstatus:4
        }
        this.jbGrid.getStore().load({
                params:{
                    start:0,
                    limit:this.jbGrid.getBottomToolbar().pageSize
                }
        });
        if(Wtf.cmpPref.defaultapps=="Internal"){
    		this.cm.setHidden(this.cm.getIndexById("pagelink"),true);
        }else{
        	this.cm.setHidden(this.cm.getIndexById("pagelink"),false);
        }
        this.jobType.setValue(Wtf.cmpPref.defaultapps);
        this.jobStatus.setValue('4');
        if(this.vacancy==1){
        	this.vacancy=0;
        	this.viewvacancies.setText(WtfGlobal.getLocaleText("hrms.recruitment.view.vacancies"));//"View Vacancies");
    	}
    },
    deletejobpositons:function(){ 
        if(this.jbGrid.getSelectionModel().getCount()==0){
            calMsgBoxShow(42,0);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:deleteMsgBox('job position'),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.delrec=this.jbGrid.getSelectionModel().getSelections();
                        this.delarr=[];
                        this.sm2.clearSelections();
                        for(var i=0;i<this.delrec.length;i++){
                            this.delarr.push(this.delrec[i].get('posid'));
                             var rec=this.jobmasterGDS.indexOf(this.delrec[i]);
                             WtfGlobal.highLightRow(this.jbGrid,"FF0000",5, rec)
                        }
                        calMsgBoxShow(201,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                        url: "Rec/Job/DeleteInternalJobs.rec",
                            params:  {
                                flag:9,
                                delid:this.delarr
                            }
                        },
                        this,
                        function(response){
                            var res=eval('('+response+')');
                            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),""+res.message+""],1);
                            msgFlag = 0;
                            var params={
                                start:0,
                                limit:this.jbGrid.pag.pageSize
                            }
                            WtfGlobal.delaytasks(this.jobmasterGDS, params);
                        },
                        function(){
                            calMsgBoxShow(27,1);
                        })
                    }
                }
            })
        }
    },
    addjobpos:function(){ 
        var jobwin= new Wtf.AddJobs({
        	id:'AddJobsRecruitment',
            modal:true,
            title:WtfGlobal.getLocaleText("hrms.recruitment.add.job"),//"Add Job",
            closable:true,
            resizable:false,            
            layout:'fit',
            typejob:'External',
            action:'add',
            grids:this.jbGrid,
            ds:this.jobmasterGDS
        });
        jobwin.on('show',function(){
            jobwin.nopos.focus(true,100);
        },this);
        jobwin.show();
    },
    editjobpos:function(){ 
        if(this.sm2.getCount()==0||this.sm2.getCount()>1)
        {
            calMsgBoxShow(42,0);
        }
        else
        {           
            var jobid=this.jbGrid.getSelectionModel().getSelected().get('posmasterid');
            var main=Wtf.getCmp('externalTab');
            var edsignupTab=Wtf.getCmp(jobid+'Profile');
            if(edsignupTab==null)
            {
                edsignupTab=new Wtf.jobProfile({
                    id:jobid+'Profile',
                    title:this.jbGrid.getSelectionModel().getSelected().get('posname')+WtfGlobal.getLocaleText("hrms.common.profile"),//"'s Profile",
                    layout:'fit',
                    border:false,
                    autoScroll:true,
                    closable:true,
                    store:this.jobmasterGDS,
                    viewOnlyType:false,
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
//    viewprereq:function(){
//        if(this.sm2.getCount()==0||this.sm2.getCount()>1)
//        {
//            calMsgBoxShow(42,0);
//        }
//        else
//        {
//            var uid=this.jbGrid.getSelectionModel().getSelected().get('posmasterid');
//            var main =Wtf.getCmp('externalTab');
//            var mainSuccessionTab=Wtf.getCmp(uid+'jobs');
//            if(mainSuccessionTab==null)
//            {
//                mainSuccessionTab=new Wtf.viewPrerequisite({
//                    title:this.jbGrid.getSelectionModel().getSelected().get('posname')+' Prerequisite',
//                    id:uid+'externaljobs',
//                    border:false,
//                    layout:'fit',
//                    closable:true,
//                    designationid:this.jbGrid.getSelectionModel().getSelected().get('posmasterid'),
//                    iconCls:getTabIconCls(Wtf.etype.hrmspreinternal)
//                });
//                main.add(mainSuccessionTab);
//            }
//            main.setActiveTab(mainSuccessionTab);
//            main.doLayout();
//        }
//    },
    jobtypeSelection:function(jobValue){
        this.jbGrid.getStore().baseParams={
            jobtype:jobValue,
            flag:7
        }
        this.jbGrid.getStore().load({
            params:{
                start:0,
                limit:this.jbGrid.pag.pageSize,
                ss: Wtf.getCmp("Quick"+this.jbGrid.id).getValue()
            }
        });
        
    },
    assignagency:function(){
        if(this.sm2.getCount()==0)
        {
            calMsgBoxShow(42,0);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:WtfGlobal.getLocaleText("hrms.recruitment.assign.job.agency.msg"),//'Are you sure you want to assign the selected job position(s) to agency?',
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.posid=this.sm2.getSelections();
                        this.posids=[];
                        for(var i=0;i<this.posid.length;i++){
                            if(this.posid[i].get('positionstatus')==0)
                                this.posids.push(this.posid[i].get('posid'));
                            else {
                                calMsgBoxShow(208,2,false,250);
                                return;
                            }
                        }
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Rec/Agency/addApplyAgency.rec",
                            params:  {
                                flag:128,
                                jobids:this.posids,
                                ageid:this.agencyid
                            }
                        },
                        this,
                        function(response){
                            var res=eval('('+response+')');
                            calMsgBoxShow([""+res.heading+"",""+res.message+""],0);
                            //calMsgBoxShow(128,0);
                            if(res.heading == "Success"){
                                this.jobmasterGDS.load({
                                    params:{
                                        start:0,
                                        limit:this.jbGrid.pag.pageSize,
                                        ss: Wtf.getCmp("Quick"+this.jbGrid.id).getValue()
                                    }
                                });
                            }
                        },
                        function(){
                            calMsgBoxShow(55,2);
                        })
                    }
                }
            })
        }
    },
    unassignagency:function(){
        if(this.sm2.getCount()==0)
        {
            calMsgBoxShow(42,0);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:WtfGlobal.getLocaleText("hrms.recruitment.unassign.job.agency.msg"),//'Are you sure you want to unassign the selected job position(s) to agency?',
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.posid=this.sm2.getSelections();
                        this.posids=[];
                        for(var i=0;i<this.posid.length;i++){
                             this.posids.push(this.posid[i].get('posid'));
                        }
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Rec/Agency/deleteApplyAgency.rec",
                            params:  {
                                flag:128,
                                jobids:this.posids,
                                ageid:this.agencyid
                            }
                        },
                        this,
                        function(response){
//                            var res=eval('('+response+')');
                            calMsgBoxShow(221,0);
                                this.jobmasterGDS.load({
                                    params:{
                                        start:0,
                                        limit:this.jbGrid.pag.pageSize,
                                        ss: Wtf.getCmp("Quick"+this.jbGrid.id).getValue()
                                    }
                                });
                        },
                        function(){
                            calMsgBoxShow(55,2);
                        })
                    }
                }
            })
        }
    },
    viewVacancies:function(){
        if(this.vacancy==0){
            this.jobmasterGDS.baseParams={
                flag:7,
                jobtype:this.jobType.getValue(),
                jobstatus:this.jobStatus.getValue(),
                vacancy:'vacancy'
            }
            this.vacancy=1;
            this.viewvacancies.setText(WtfGlobal.getLocaleText("hrms.recruitment.ViewAll"))
        } else {
            this.jobmasterGDS.baseParams={
                flag:7,
                jobtype:this.jobType.getValue(),
                jobstatus:this.jobStatus.getValue()
            }
            this.vacancy=0;
            this.viewvacancies.setText(WtfGlobal.getLocaleText("hrms.recruitment.view.vacancies"));//"View Vacancies")
        }
        this.jobmasterGDS.load({
            params:{
                start:0,
                limit:this.jbGrid.pag.pageSize,
                ss: Wtf.getCmp("Quick"+this.jbGrid.id).getValue()
            }
        });
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.searchparams,
            searchid:this.searchid
        });
        this.objsearchComponent.searchid = this.searchid;
    },
    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.searchStore.load(
        {params:{
            searchid:this.searchid,
            searchFlag:2
        }});
        this.advanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
    	this.jobmasterGDS.baseParams = {
            mode:114,
            flag:7,
            jobtype:this.jobType.getValue(),
            jobstatus:this.jobStatus.getValue()
        };
    	this.jobmasterGDS.load();
        this.searchJson="";
        this.searchid="";
        this.objsearchComponent.hide();
        this.advanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.jobmasterGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            flag:7,
            jobtype:this.jobType.getValue(),
            jobstatus:this.jobStatus.getValue()
        };
        this.jobmasterGDS.load();
    },
    reloadgridStore:function(json){

        this.searchJson="";
        if(this.searchid!=undefined){
            this.searchJson=json;
        }
        
        this.jobmasterGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            flag:7,
            jobtype:this.jobType.getValue(),
            jobstatus:this.jobStatus.getValue()
        };
        this.jobmasterGDS.load();
    },
    saveStore:function(json, saveSearchName){
        this.saveJson=json;
        Wtf.Ajax.requestEx({
            url:"Common/saveSearch.common",
            params:{
                mode:115,
                saveJson:this.saveJson,
                saveSearchName:saveSearchName,
                searchFlag:2
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
function addjobs(Id){
    var addjobs=Wtf.getCmp(Id);   
        addjobs.addjobpos();   
}
