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
Wtf.appsList = function(config){
    Wtf.apply(this, config);
    Wtf.appsList.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.appsList, Wtf.Panel, {
    initComponent: function(){
        Wtf.appsList.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.appsList.superclass.onRender.call(this,config);
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
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.record = Wtf.data.Record.create([{
            name:'lid'
        },{
            name:'uname'
        },{
            name:'cname'
        },{
            name:'email'
        },{
            name:'address1'
        },{
            name:'contactno'
        },{
            name:'documents'
        }]);
        this.reader= new Wtf.data.KwlJsonReader1({
            root: 'data',
            totalProperty:"count"
        },
        this.record
        );

        this.allAppsGDS = new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getExternalApplicant.rec",
            baseParams: {
                flag:49
            },
            reader:this.reader

        });
        calMsgBoxShow(202,4,true);
        this.allAppsGDS.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.allAppsGDS.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar()
        },this);
        
        this.cm = new Wtf.grid.ColumnModel([
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.recruitment.applicant.name"),//"Applicant Name",
                dataIndex: 'cname',
                sortable: true,
                pdfwidth:50
            },{
                header: WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email ID",
                dataIndex: 'email',
                sortable: true,
                pdfwidth:50,
                renderer: WtfGlobal.renderEmailTo
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.contact"),//"Contact",
                dataIndex: 'contactno',
                sortable: true,
                pdfwidth:50
            }],{
                header: WtfGlobal.getLocaleText("hrms.common.documents"),//"Documents",
                dataIndex: 'documents',
                sortable: true,
                align:"center",
                renderer:function(a,b,c,d,e,f){
                    return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"../../fdownload.jsp?url=" + c.data.docid + "&mailattch=true&dtype=attachment\")'><img class='downloaddocs' src='../../images/document-download.gif' style='cursor:pointer' title='"+WtfGlobal.getLocaleText("hrms.common.Clicktodownloaddocument")+"' ></a></div>";
                }
            });
        
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.allAppsGDS.load({params:{start:0,limit:this.applGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.applGrid.id).setValue("");
        	}
     	});
        this.createapp=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.create.applicant"),//'Create Applicant',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.create.applicant.tooltip"),//"Fill in the basic details of the new entrant like name, address, email, contact number etc., to create a new entry.",
            iconCls:'pwndCommon profilebuttonIcon',
            scope:this,
            handler:this.createApplicant
        });
        this.viewprofile=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.view.edit.profile"),//'View/Edit Profile',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.view.edit.profile.tooltip"),//"View & edit the information entered in the profile like your personal & contact information, academic and work experience details.",
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            disabled:true,
            scope:this,
            handler:this.addprofile
        });
        this.jobsearch=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.job.search"),//'Job Search',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.job.search.tooltip"),//"Search for jobs and also apply for the jobs if your profile matches with that of the organization's requirements.",
            iconCls:'pwndCommon searchbuttonIcon',
            disabled:true,
            scope:this,
            handler:this.jobSearch
        });
        this.jobstatus=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.job.status"),//'Job Status',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.job.status.tooltip"),//"The details like job ID, date of applying, status of the application and the date of interview can be located.",
            iconCls:'pwndHRMS jobstatustabIcon',
            disabled:true,
            scope:this,
            handler:this.jobStatus
        });
        this.document=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.documents"),//'Documents',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.external.documents.tooltip"),//"Upload important documents and any one having access to these documents can download them whenever required.",
            iconCls:getButtonIconCls(Wtf.btype.docbutton),
            disabled:true,
            scope:this,
            handler:this.uploaddocuments
        });
        this.advanceSearchBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.common.advanced.search"),//"Advanced Search",
            id:'advanced3',// In use, Do not delete
            scope : this,
            tooltip:WtfGlobal.getLocaleText("hrms.common.advanced.search.tooltip"),//'Search for multiple terms in multiple fields',
            handler : this.configurAdvancedSearch,
            iconCls : 'pwnd searchtabpane'
        });
        this.exportApplications = new Wtf.Button({
            text: WtfGlobal.getLocaleText("hrms.common.ExportApplications"),
            iconCls: 'pwndExport export',
            minWidth:81,
            scope:this,
            id:this.id+'exportApplications',
            handler:this.exportApplications
        });
        this.searchparams=[{
        	name:"cname",
        	dbname:"col1",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.applicant.name"),//"Applicant Name",
        	xtype:'textfield'
        },{
        	name:"email",
        	dbname:"col3",
        	header:WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email ID",
        	xtype:'textfield'
        },{
        	name:"contact",
        	dbname:"col4",
        	header:WtfGlobal.getLocaleText("hrms.common.contact.no"),//"Contact No.",
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
            url: "Rec/Job/getExternalApplicantForExport.rec",
            filename:this.title
        });
        this.deleteapp= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.delete.applicant"),//'Delete Applicant',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.delete.applicant.tooltip"),//"Select a row to delete it from the list of applicants.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:109,
            disabled:true,
            scope:this,
            handler:this.deleteapps
        });
        var btnArr=[];
        btnArr.push('-',this.refreshBtn,'-',this.createapp,'-',this.viewprofile,'-',this.deleteapp,'-',this.jobsearch,'-',this.jobstatus,'-',this.document,'-',this.ExportInfoBtn,'-',this.advanceSearchBtn);
        this.applGrid = new Wtf.KwlGridPanel({
            border: false,
            id:'allappslist',
            store: this.allAppsGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            displayInfo:true,
            trackMouseOver: true,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.external.grid.search.msg"),//"Search by Applicant Name",
            searchField:"uname",
            serverSideSearch:true,
            tbar:btnArr,
            bbar:[this.exportApplications],
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:createApplicant(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.recruitment.external.grid.msg")+"</a>")//WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:createApplicant(\""+this.id+"\")'>Get started by adding a job applicant now...</a>")
            }
        });

        this.sm2.on("selectionchange",function (){
            WtfGlobal.enableDisableBtnArr(btnArr,this.applGrid,[5,9,11,13] ,[7]);
        },this);
    
    },  
    addprofile:function(){

        this.arr=this.sm2.getSelections();
        if(this.sm2.hasSelection()==false){
            calMsgBoxShow(42,0);
            return;
        }
        var p= this.arr[0].get('lid');
        var main=Wtf.getCmp("recruitmentmanage");
        var projectBudget1 = Wtf.getCmp(p+'Application');
        if(projectBudget1 == null){
            projectBudget1 = new Wtf.createapplicantForm({
                autoScroll:true,
                closable:true,
                al:true,
                title:this.arr[0].get('cname')+WtfGlobal.getLocaleText("hrms.common.profile"),//this.arr[0].get('cname')+"'s Profile",
                profId:p,
                iconCls:getTabIconCls(Wtf.etype.hrmsprofile),
                id:p+'Application'
            });
            main.add(projectBudget1);
        }
        main.setActiveTab(projectBudget1);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    jobSearch:function(){
        this.arr=this.sm2.getSelections();
        if(this.sm2.hasSelection()==false){
            calMsgBoxShow(42,0);
            return;
        }
        var p= this.arr[0].get('lid');
        var main=Wtf.getCmp("recruitmentmanage");
        var projectBudget2 = Wtf.getCmp(p+'jobsearch');
        if(projectBudget2 == null){
            projectBudget2 = new Wtf.recruitmentJobs({
                autoScroll:true,
                closable:true,
                layout:'fit',
                title:this.arr[0].get('cname')+WtfGlobal.getLocaleText("hrms.recruitment.s.jobs"),//this.arr[0].get('cname')+"'s Jobs",
                profId:p,
                arr:this.arr[0],
                id:p+'jobsearch',
                iconCls:getTabIconCls(Wtf.etype.hrmsexternaljob),
                border:false
            });
            main.add(projectBudget2);
        }
        main.setActiveTab(projectBudget2);
        main.doLayout();
    },

    jobStatus:function(){

        this.arr=this.sm2.getSelections();
        if(this.sm2.hasSelection()==false){
            calMsgBoxShow(42,0);
            return;
        }
        var p= this.arr[0].get('lid');
        var main=Wtf.getCmp("recruitmentmanage");
        var projectBudget3 = Wtf.getCmp(p+'jobstatus');
        if(projectBudget3 == null){
            projectBudget3 = new Wtf.recruitmentJobstatus({
                autoScroll:true,
                closable:true,
                border:false,
                layout:'fit',
                iconCls:'pwndHRMS jobstatustabIcon',
                title:this.arr[0].get('cname')+WtfGlobal.getLocaleText("hrms.recruitment.s.job.status"),//this.arr[0].get('cname')+"'s Job Status",
                profId:p,
                id:p+'jobstatus'
            });
            main.add(projectBudget3);
        }
        main.setActiveTab(projectBudget3);
        main.doLayout();
    },

    loadStore:function(){
        this.allAppsGDS.load({
            params:{
                start:0,
                limit:this.applGrid.pag.pageSize,
                ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
            }
        });
    },
    createApplicant:function(){
        var p = Wtf.getCmp("createApplicant");

        if(!p){
            p= new Wtf.CreateApplicant({
                title:WtfGlobal.getLocaleText("hrms.recruitment.create.applicant"),//'Create Applicant',
                id:'createApplicant',
                closable: true,
                modal: true,
                iconCls:getButtonIconCls(Wtf.btype.winicon),
                width: 410,
                height: 300,
                resizable: false,
                layout: 'fit',
                buttonAlign: 'right'
            //  renderTo: document.body
            });p.show();
            p.on('updateGrid',this.loadStore,this);
        }
    },
    uploaddocuments:function(){
        if(this.sm2.hasSelection()==false){
            calMsgBoxShow(42,0);
            return;
        }
        var rec=this.sm2.getSelections() ;
        var appname=(rec[0].get('cname'));
        var appid=rec[0].get('lid');
        var main=Wtf.getCmp("recruitmentmanage");
        var edsignupTab=Wtf.getCmp('appfilepanel'+appid);
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.document_panel({
                layout:'fit',
                border:false,
                title:appname+" "+WtfGlobal.getLocaleText("hrms.common.documents"),//appname+" Documents",
                lid:appid,
                id:'appfilepanel'+appid,
                manager:false,
                closable:true,
                app:"applicant",
                iconCls:getTabIconCls(Wtf.etype.hrmsdocuments)
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();      
    },
    deleteapps:function(){
        if(this.sm2.hasSelection()){//Delete Record
            this.delkey=this.sm2.getSelections();
            this.ids=[];
            this.sm2.clearSelections();
            for(var i=0;i<this.delkey.length;i++){
                this.ids.push(this.delkey[i].get('lid'));
                var rec=this.allAppsGDS.indexOf(this.delkey[i]);
                WtfGlobal.highLightRow(this.applGrid,"FF0000",5, rec)
            }
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:WtfGlobal.getLocaleText("hrms.recruitment.delete.selected.applicants"),//'Do you want to delete the selected applicants?',
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url: "Rec/Job/deleteJobapplicant.rec",
                            params:{
                                flag:56,
                                ids:this.ids
                            }
                        },this,
                        function(){
                            calMsgBoxShow(64,0);
                            var params={
                                start:0,
                                limit:this.applGrid.pag.pageSize,
                                ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
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
                if(column.config[i].title==undefined)
                    title=column.config[i].dataIndex;
                else
                    title=column.config[i].title;
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
    exportApplications: function(){
    	var url = "Rec/Job/exportAllApplications.rec?"+Wtf.urlEncode(Wtf.urlDecode("employeetype="+this.type+"&status="+this.status+"&visible="+true+"&filetype="+"csv"+"&name="+this.title+"&applicationflag="+1+"&allApplicationList="+true+"&searchJson="+this.searchJson));
    	Wtf.get('downloadframe').dom.src = url;
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.searchparams,
            searchid:this.searchid
        });
        this.objsearchComponent.searchFlag = 6;
    },
    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.searchStore.load(
        {params:{
            searchid:this.searchid,
            searchFlag:6
        }});
        this.advanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
    	this.allAppsGDS.baseParams = {
            mode:114,
            start:0,
            limit:this.applGrid.pag.pageSize
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
            start:0,
            limit:this.applGrid.pag.pageSize,
            ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
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
            start:0,
            limit:this.applGrid.pag.pageSize,
            ss: Wtf.getCmp("Quick"+this.applGrid.id).getValue()
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
                searchFlag:6
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
function createApplicant(Id){
    Wtf.getCmp(Id).createApplicant();
}
