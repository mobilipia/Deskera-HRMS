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
Wtf.qualifiedApps = function(config){
    Wtf.apply(this, config);
    Wtf.qualifiedApps.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.qualifiedApps, Wtf.Panel, {
    initComponent: function(){
        Wtf.qualifiedApps.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.qualifiedApps.superclass.onRender.call(this,config);
        this.rejectedGrid();
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
                items:[this.qualifiedGrid]
            }]
        });
        this.add(this.pan);
        this.exportinfo();
        this.type = 0; 
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
    rejectedGrid:function(){
        this.changeflag = 0;
        this.applicantstore = new Wtf.data.SimpleStore({
            fields:['id','applicant', 'applicanthidden'],
            data: [
            ['1', WtfGlobal.getLocaleText("hrms.common.Internal"), "Internal"],
            ['2', WtfGlobal.getLocaleText("hrms.common.External"), "External"],
            ['3', WtfGlobal.getLocaleText("hrms.common.Both"), "All"]
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
            width:100
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
            name:'joiningdate',
            type:'date'
        },{
            name:'callback'
        },{
            name:'interviewplace'
        },{
            name:'interviewcomment'
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
            name:'fname'
        },{
            name:'lname'
        },{
            name:'designationid'
        },{
            name:'departmentid'
        },{
            name:'designation'
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
        var employeetype=Wtf.cmpPref.defaultapps=="Internal"?"1":"0";
        this.qualifiedGDS =new Wtf.ux.MultiGroupingStore({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getJobApplications.rec",
            baseParams: {
                flag:38,
                gridst:1,
                employeetype:employeetype
            },
            reader:this.reader,
            sortInfo:{
                field: 'department',
                direction: "ASC"
            },
            groupField:[WtfGlobal.getLocaleText("hrms.common.department"),WtfGlobal.getLocaleText("hrms.common.jobid")]

        });
        calMsgBoxShow(202,4,true);
        this.qualifiedGDS.load();
        this.qualifiedGDS.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);
        
        this.cm = new Wtf.grid.ColumnModel(
            [this.sm2,{
                header: WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
                dataIndex:WtfGlobal.getLocaleText("hrms.common.department"),
                exportDataIndex:'Department',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.candidate.name"),//"Candidate Name",
                dataIndex: 'cname',
                exportDataIndex:'cname',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email ID",
                dataIndex: 'email',
                exportDataIndex:'email',
                sortable: true,
                renderer: WtfGlobal.renderEmailTo
            },{
                header: WtfGlobal.getLocaleText("hrms.common.address"),//" Address",
                dataIndex: 'addr',
                exportDataIndex:'addr',
                sortable: true,
                renderer : function(val) {
                    return unescape(val);
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.contact.no"),//"Contact No.",
                dataIndex: 'contact',
                exportDataIndex:'contact',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.selected.for"),//"Job Id Selected For",
                dataIndex: WtfGlobal.getLocaleText("hrms.common.jobid"),
                exportDataIndex:'JobId',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.applied.date"),//"Applied Date",
                dataIndex: 'applydt',
                exportDataIndex:'applydt',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.joining.date"),//"Joining Date",
                dataIndex: 'joiningdate',
                exportDataIndex:'joiningdate',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.recruitment.resume"),//"Resume",
                dataIndex:"id",
                exportDataIndex:'id',
                renderer:function(a,b,c,d,e,f){
//                        return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"Common/Document/downloadDocuments.common?url=" + c.data.docid + "&mailattch=true&dtype=attachment\")'><div class='pwndCommon downloaddocs' style='cursor:pointer' title='Click to download document' ></div></a></div>";
                    if(c.data.employeetype == 0 && c.data.docid.length > 0)
                    return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"Common/Document/downloadDocuments.common?url=" + c.data.docid + "&mailattch=true&dtype=attachment&applicant=applicant\")'><div class='pwndHRMS resumeIcon' style='cursor:pointer' title='"+WtfGlobal.getLocaleText("hrms.common.Clicktodownloaddocument")+"' ></div></a></div>";
                }
            }]);
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.qualifiedGDS.load({params:{start:0,limit:this.qualifiedGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.qualifiedGrid.id).setValue("");
        	}
     	});
        this.appdocs=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.documents"),//'Documents',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.rejected.documents.tooltip"),//"The documents uploaded by the applicants can be accessed here.",
            iconCls:getButtonIconCls(Wtf.btype.docbutton),
            id:this.id+'documentsqua',
            minWidth:80,
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
        	name:"addr",
        	dbname:"4",
        	header:WtfGlobal.getLocaleText("hrms.common.address"),//"Address",
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
        	name:"joiningdate",
        	dbname:"joiningdate",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.joining.date"),//"Joining Date",
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
            url: "Rec/Job/getJobApplicationsExport.rec",
            filename:this.title
        });
        this.deleteapp= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.delete.application"),//'Delete Application',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.qualified.delete.application.tooltip"),//"Delete the application not required anymore.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            disabled:true,
            scope:this,
            handler:this.deleteapps
        });
        this.viewprofile= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.view.profile"),//'View Profile',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.qualified.view.profile.tooltip"),//"View the profile of the selected candidates and their academic qualifications, skill sets etc.",
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            id:this.id+'viewprofilerejected',
            disabled:true,
            scope:this,
            handler:this.viewprofile
        });
        this.editprospect= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.edit.prospect"),//'Edit Prospect',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.edit.prospect.tooltip"),//"Edit the status of the prospect as and when required.",
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            minWidth:85,
            disabled:true,
            scope:this,
            handler:this.editprospect
        });
        this.transferdata= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.transfer.applicant.data"),//'Transfer Applicant Data',
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.transfer.applicant.data.tooltip"),//"Select an applicant and transfer all its details to another location to put them on company's payroll.",
            minWidth:85,
            disabled:true,
            scope:this,
            handler:this.transferData
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
        var qualifiedbtns=[];
        qualifiedbtns.push('-',this.refreshBtn);
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.qualifiedapps, Wtf.Perm.qualifiedapps.manage)){
            qualifiedbtns.push('-',this.deleteapp,'-',this.viewprofile,'-',this.editprospect,'-',this.appdocs,'-',this.ExportInfoBtn,'-',this.advanceSearchBtn);
        }
        qualifiedbtns.push('->','-',(WtfGlobal.getLocaleText("hrms.recruitment.applicant.type")+":"));//'Applicant Type:');
        qualifiedbtns.push(this.applicantcmb);
        this.qualifiedGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'qualifiedgr',
            store: this.qualifiedGDS,
            cm: this.cm,
            sm: this.sm2,
            view: new Wtf.ux.MultiGroupingView({
                hideGroupedColumn :true,
                forceFit: true,
                showGroupName:false,
                enableGroupingMenu: false,
                enableGroupingX: false,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.selected")+" "+this.applicantcmb.getValue()+" "+WtfGlobal.getLocaleText("hrms.recruitment.application.till.now")),//WtfGlobal.emptyGridRenderer("No Selected "+this.applicantcmb.getValue()+" application till now"),
                groupTextTpl:'{text}'
            }),
            displayInfo:true,
            loadMask:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            serverSideSearch:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.allapplication.grid.search.msg"),//"Search by Department, Job ID, Candidate Name",
            searchField:"cname",
            tbar:qualifiedbtns,
            bbar:[this.exportApplications]
        });

        this.applicantcmb.on('select',function(a,b,c){
            var type;
            if(b.data.applicanthidden=="Internal"){
                type=1;
                this.exportApplications.setDisabled(true);
            }else if(b.data.applicanthidden=="External"){
                type=0;
                this.exportApplications.setDisabled(false);
            }else{
                type=2;
                this.exportApplications.setDisabled(true);
            }
            this.qualifiedGDS.baseParams={
                employeetype:type,
                flag:38,
                gridst:1
            }
            this.type = type;
            this.qualifiedGDS.load({
                params:{
                    start:0,
                    limit:this.qualifiedGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.qualifiedGrid.id).getValue()
                }
            });
            this.qualifiedGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.selected")+" "+this.applicantcmb.getValue()+" "+WtfGlobal.getLocaleText("hrms.recruitment.application.till.now"));//WtfGlobal.emptyGridRenderer("No selected "+this.applicantcmb.getValue()+" application till now");
            this.qualifiedGrid.getView().refresh();
        },this); 

        this.sm2.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(qualifiedbtns, this.qualifiedGrid, [5,9,11], [3]);
            if(this.sm2.hasSelection()){
                this.statusrec=this.sm2.getSelections();
                this.checkRec=this.statusrec[0].get('jobid');
                this.disableFlag=false;
                for(var i=0;i<this.statusrec.length;i++)
                {
                    if(this.checkRec!=this.statusrec[i].get('jobid')){
                        this.disableFlag=true;
                    }
                }
                if(this.disableFlag){
                    this.editprospect.disable();
                } else{
                    this.editprospect.enable();
                }
            }else{
                this.deleteapp.disable();
                this.viewprofile.disable();
                this.editprospect.disable();
            }
        },this);
        this.qualifiedGrid.on('cellclick',this.onCellClick, this);
    },
    DownloadLink:function(a, b, c, d, e, f){     
        var msg="";
        if(c.data['file'])
            msg='<img src="./images/document12.gif "  id=\''+c.data['id']+'\'/>';
        else
            msg="";
        return msg;


    },
    deleteapps:function(){
        if(this.sm2.hasSelection()){//Delete Record
            this.delkey=this.sm2.getSelections();
            this.ids=[];
            for(var i=0;i<this.delkey.length;i++){
                this.ids.push(this.delkey[i].get('id'));
            }
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:deleteMsgBox('application'),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        calMsgBoxShow(201,4,true);
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + 'hrms.jsp',
                            params:{
                                flag:42,
                                ids:this.ids
                            }
                        },this,
                        function(){
                            calMsgBoxShow(64,0);
                            this.qualifiedGDS.load({
                                params:{
                                    start:0,
                                    limit:this.qualifiedGrid.pag.pageSize,
                                    ss: Wtf.getCmp("Quick"+this.qualifiedGrid.id).getValue()
                                }
                            });
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
                    report:false,
                    manager:true,
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

    savedata:function(){
        var jsondata = "";

        for(var i=0;i< this.qualifiedGDS.getCount();i++){

            jsondata += "{'status':'" + this.qualifiedGDS.getAt(i).get("status") + "',";
            jsondata += "'id':'" + this.qualifiedGDS.getAt(i).get("id") + "',";
            jsondata += "'recruiter':'" + this.qualifiedGDS.getAt(i).get("recruiter") + "',";
            jsondata += "'interviewdt':'" + this.qualifiedGDS.getAt(i).get("interviewdt") + "',";
            jsondata += "'rank':'" + this.qualifiedGDS.getAt(i).get("rank") + "',";
            jsondata += "'callback':'" + this.qualifiedGDS.getAt(i).get("callback") + "',";
            jsondata += "'apcntid':'" + this.qualifiedGDS.getAt(i).get("apcntid") + "'},";
        }
        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);

        Wtf.Ajax.requestEx({
            url:Wtf.req.base + "hrms.jsp",
            params: {
                flag:40,
                jsondata:finalStr
            }
        }, this,
        function(response){
            var res=eval('('+response+')');
            calMsgBoxShow(29,0);
            this.qualifiedGDS.load({
                params:{
                    start:0,
                    limit:this.qualifiedGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.qualifiedGrid.id).getValue()
                }
            });
        },
        function(response)
        {
            calMsgBoxShow(65,1);
        })
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
    },
    editprospect:function(){
        var type=this.applicantcmb.getValue();
        if(type=="Internal"){
            type=1;
        }else{
            type=0;
        }
        if(this.sm2.hasSelection()){
            this.editprospect=new Wtf.editprospect({
                modal:true,
                title:WtfGlobal.getLocaleText("hrms.common.edit.prospect"),//"Edit Prospect",
                iconCls:getButtonIconCls(Wtf.btype.winicon),
                autoDestroy: true,
                selected:true,
                resizable:false,
                employeetype:type,
                layout:'fit',
                appid:this.id,
                editval:'Selected'
            });
            this.editprospect.show();
            this.editprospect.on('editpload',this.gridloads,this);
        }else{
            calMsgBoxShow(42,0);
        }
    },
    onCellClick:function(g,i,j,e){
        e.stopEvent();
        var el=e.getTarget("a");
        if(el==null)return;
        var header=g.getColumnModel().getDataIndex(j);
        var rec=this.sm2.getSelected().data;
        if(header=="jobpositionid"){
            var main=Wtf.getCmp("recruitmentmanage");
            var edjobTab=Wtf.getCmp(rec.jobpositionid+'Jobs');
            if(edjobTab==null)
            {
                edjobTab=new Wtf.jobProfile({
                    id:rec.jobpositionid+'Jobs',
                    title:rec.jobpositionid+WtfGlobal.getLocaleText("hrms.recruitment.s.job.profile"),//rec.jobpositionid+"'s Job Profile",
                    iconCls:'pwndHRMS jobprofiletabIcon',
                    layout:'fit',
                    closable:true,
                    jobposid:rec.jobpositionid,
                    positionid:rec.posid,
                    border:false,
                    disableSubmit:true,
                    autoScroll:true
                });
                main.add(edjobTab);
            }
            main.setActiveTab(edjobTab);
            main.doLayout();
            Wtf.getCmp("as").doLayout();
        }
    },
    transferData:function(){
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
            msg:WtfGlobal.getLocaleText("hrms.recruitment.transfer.applicant.data.msg")+'<br><br><b>'+WtfGlobal.getLocaleText("hrms.common.data.cannot.changed.later.msg")+'</b>',//'Are you sure you want to transfer the applicant data?<br><br><b>Note: This data cannot be changed later.</b>',
            buttons:Wtf.MessageBox.YESNO,
            icon:Wtf.MessageBox.QUESTION,
            scope:this,
            fn:function(button){
                if(button=='yes'){
                    this.showWindow();
                }
            }
        })
    },
    exportApplications: function(){
    	var url = "Rec/Job/exportAllApplications.rec?"+Wtf.urlEncode(Wtf.urlDecode("employeetype="+this.type+"&status="+this.status+"&visible="+true+"&filetype="+"csv"+"&name="+this.title+"&applicationflag="+1+"&searchJson="+this.searchJson));
    	Wtf.get('downloadframe').dom.src = url;
    },
    showWindow :function(){
        var rec=this.sm2.getSelected().data;
                    var dept=rec.Department;
                    var desig=rec.designation;
                    this.employeeID = new Wtf.form.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.employee.id"),//'Employee ID',
                        width : 200,
                        disabled:true,
                        allowBlank:false
                    });
                    this.userName = new Wtf.form.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.username")+"*",//'Username*',
                        width : 200,
                        validator:WtfGlobal.noBlankCheck,
                        allowBlank:false
                    });
                    this.newDesignation = new Wtf.form.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.designation"),//'Designation',
                        width : 200,
                        disabled:true,
                        allowBlank:false
                    });
                    this.newDepartment = new Wtf.form.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.department"),//'Department',
                        width : 200,
                        disabled:true,
                        allowBlank:false
                    });
                    this.emailId = new Wtf.form.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.email.id")+"*",//'Email Id*',
                        width : 200,
                        vtype:"email",
                        allowBlank:false
                    });
                    if(this.applicantcmb.getValue()=="External" || this.applicantcmb.getValue()=="Both"){
                        if(rec.employeetype==0){
                            var uname=rec.fname+'.'+rec.lname;
                            this.usernameForm = new Wtf.form.FormPanel({
                                waitMsgTarget: true,
                                border : false,
                                bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
                                autoScroll:false,
                                lableWidth :70,
                                layoutConfig: {
                                    deferredRender: false
                                },
                                items:[this.employeeID,this.userName,this.newDepartment,this.newDesignation]
                            });

                            this.usernamePanel= new Wtf.Panel({
                                border: false,
                                layout:'fit',
                                autoScroll:false,
                                items:[{
                                    border:false,
                                    region:'center',
                                    layout:"border",
                                    items:[{
                                        region : 'north',
                                        height : 75,
                                        border : false,
                                        bodyStyle : 'background:white;border-bottom:1px solid #FFFFFF;',
                                        html: getTopHtml(WtfGlobal.getLocaleText("hrms.recruitment.assign.username"), WtfGlobal.getLocaleText("hrms.recruitment.username.for.selected.applicant"))//getTopHtml("Assign Username", "Please enter a username for selected applicant")
                                    },{
                                        border:false,
                                        region:'center',
                                        bodyStyle : 'background:#f1f1f1;font-size:10px;',
                                        layout:"fit",
                                        items: [this.usernameForm]
                                    }]
                                }]
                            });

                            this.userWindow=new Wtf.Window({
                                iconCls:getButtonIconCls(Wtf.btype.winicon),
                                id:"applicantWindow",
                                layout:'fit',
                                closable:true,
                                width:400,
                                title:WtfGlobal.getLocaleText("hrms.recruitment.transfer.applicant.data"),//'Transfer Applicant Data',
                                height:330,
                                border:false,
                                modal:true,
                                scope:this,
                                plain:true,
                                buttonAlign :'right',
                                buttons: [{
                                    text:WtfGlobal.getLocaleText("hrms.common.submit"),//'Submit',
                                    handler:function(){
                                        //this.saveuserName(rec);
                                        if(this.usernameForm.getForm().isValid()){
                                            this.uname = this.userName.getValue();
                                            this.getFormFields();
                                        }
                                    },
                                    scope:this
                                },{
                                        text: WtfGlobal.getLocaleText("hrms.common.cancel"),//'Cancel',
                                        scope:this,
                                        handler:function(){
                                            this.userWindow.close();
                                        }
                                    }],
                                    items: [this.usernamePanel]
                            });
                            this.userWindow.show();
                            Wtf.Ajax.requestEx({
    //                            url: Wtf.req.base + 'hrms.jsp',
                                url:"Rec/Job/getEmpidFormat.rec",
                                params: {
                                    flag:208
                                }
                            },this,
                            function(req){
                                this.resp=eval('('+req+')');
                                this.values=this.resp.data[0].maxempid;
                                this.employeeID.setValue(this.values);
                            },
                            function(){
                            });
                            this.userName.setValue(uname);
                            this.newDepartment.setValue(dept);
                            this.newDesignation.setValue(desig);
                            this.emailId.setValue(rec.email);
                        } else {
                            this.saveuserName(rec);
                        }
                    } else{
                        this.saveuserName(rec);
                    }
    },

    getFormFields :function(){
        this.userWindow.close();
        var arr=this.sm2.getSelections();
        var profid=arr[0].get('apcntid');
        Wtf.Ajax.requestEx({
            url: "Rec/Job/getConfigRecruitment.rec",
            method: 'POST',
            params: {
               // visible: "True",
                fetchmaster: true,
                refid : profid,
                formtype : "All",
                mapping: true
            }
            },this,
        function(response) {
            var responseObj = eval('('+response+')');
            this.configData = eval('('+response+')');
            if(responseObj.data !='' && responseObj.data !=null){
                for(var n = 0 ; n < responseObj.data.length ; n++){
                    if(responseObj.data[n].colnum > 4){
                        responseObj.data[n].colnum = 31;
                    }
                }
                this.showHeaderMappingWindow(responseObj, profid);
            }
        }, function() {
        })
    },

    showHeaderMappingWindow :function(res, profid){
        this.changeflag = 0;
        var headerlist = [
            [ 1 , 'First Name' ],
            [ 2 , 'Last Name' ],
            [ 3 , 'Email ID' ],
            [ 4 , 'Contact No' ],
            [ 5 , 'Permanent Address' ],
            [ 6 , 'Middle Name' ],
            [ 7 , 'Date Of Birth' ],
            [ 8 , 'Gender' ],
            [ 9 , 'Marital Status' ],
            [ 10 , 'Blood Group' ],
            [ 11 , 'Father\'s Name' ],
            [ 12 , 'Father\'s DOB' ],
            [ 13 , 'Mother\'s Name' ],
            [ 14 , 'Mother\'s DOB' ],
            [ 15 , 'Key Skills' ],
            [ 16 , 'PAN No' ],
            [ 17 , 'EPF No' ],
            [ 18 , 'Driving License No' ],
            [ 19 , 'Passport No' ],
            [ 20 , 'Expiry Date of Passport' ],
            [ 21 , 'Mobile no' ],
            [ 22 , 'Landline No' ],
            [ 23 , 'Other Email' ],
            [ 24 , 'Present Address' ],
            [ 25 , 'Present City' ],
            [ 26 , 'Present State' ],
            [ 27 , 'Present Country' ],
            [ 28 , 'Permanent City' ],
            [ 29 , 'Permanent State' ],
            [ 30 , 'Permanent Country' ],
            [ 31 , '-' ]  //for extra any unmapped column
            ];
        var headerds = new Wtf.data.SimpleStore({
            fields: [
                { name:"index" },
                { name:"headername" }
            ]
        });
        Wtf.ux.comboBoxRenderer = function(combo) {
            return function(value) {
                var idx = combo.store.find(combo.valueField, value);
                if(idx == -1 ){
                    return "-";//false;//"";
                }
                if(this.changeflag == 0){
                    if( idx!=0 && idx!=1 && idx!=2 && idx!=3){
                        return "-";
                    }
                }
                var rec = combo.store.getAt(idx);
                return rec.get(combo.displayField);
            };
        };
        headerds.loadData(headerlist);
        var headerCombo = new Wtf.form.ComboBox({
            store: headerds,
            displayField: 'headername',
            emptyText: WtfGlobal.getLocaleText("hrms.common.select.columnl"),//"<Select a column>",
            valueField: 'index',
            mode: 'local',
            forceSelection: true,
            editable: true,
            typeAhead:true,
            triggerAction: 'all',
            selectOnFocus: true
        });
        headerCombo.on("select", function(combo, rec){
            if(rec.get("index")==1 || rec.get("index")==2 || rec.get("index")==3 || rec.get("index")==4){
                combo.setValue(31);
            }
        }, this);
        var listds = new Wtf.data.JsonStore({
            fields: [{
                name:"fieldname"
            },{
                name:"index"
            },{
                name:"colnum"
            }]
        });
        listds.loadData(res.data);
        var listcm = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText("hrms.recruitment.external.form.attributes"),//"External Form Attributes",
            dataIndex: 'fieldname'
        },{
            header: WtfGlobal.getLocaleText("hrms.common.system.attributes"),//"System Attributes",
            dataIndex: 'colnum',
            editor: headerCombo,
            renderer: Wtf.ux.comboBoxRenderer(headerCombo).createDelegate(this)
        }]);
        var haderMapgrid= new Wtf.grid.EditorGridPanel({
            region:'center',
            id:'headerlist' + this.id,
            clicksToEdit : 1,
            store: listds,
            cm: listcm,
            border : false,
            width: 434,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
        this.headerMapWin = new Wtf.Window({
            resizable: false,
            scope: this,
            layout: 'border',
            modal:true,
            width: 400,
            height: 415,
            iconCls:'WinIcon',
            id: 'importcsvwindow',
            title: WtfGlobal.getLocaleText("hrms.recruitment.map.applicant.information"),//'Map Applicant Information',
            items:[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html : getTopHtml(WtfGlobal.getLocaleText("hrms.recruitment.map.applicant.information"),WtfGlobal.getLocaleText("hrms.recruitment.map.applicant.information.with.present.attribute"),"../../images/exportcsv40_52.gif")//getTopHtml("Map Applicant Information","Map applicant information with present attribute","../../images/exportcsv40_52.gif")
            }, haderMapgrid],
            buttons: [{
                text:WtfGlobal.getLocaleText("hrms.common.back"),//'Back',
                scope:this,
                handler:function() {
                    this.headerMapWin.close();
                    this.showWindow();
                }
            },{
                text: WtfGlobal.getLocaleText("hrms.common.continue"),//'Continue',
                type:  WtfGlobal.getLocaleText("hrms.common.submit"),
                scope: this,
                handler: function(){
                    var mappedHeaders = '';
                    var headerArray = new Array();
                    var comboCount = headerds.getCount()-1; //mapping-combo records count escape last record for unmapped column
                    for(j=0;j<listds.getCount();j++)
                        headerArray[j] = 0;
                    for(var j=0;j<listds.getCount();j++){
                        var index = listds.getAt(j).get("colnum");
                        index--;
                        if(index < comboCount){ //consider only mapping-combo records, skip other
                            headerArray[index]=headerArray[index]+1;
                            var rec = headerCombo.store.getAt(index);
                            if(rec != undefined )
                                mappedHeaders += "\""+rec.get(headerCombo.displayField)+"\":"+this.configData.data[j].colnum+",";
                        }
                    }
                    mappedHeaders = mappedHeaders.substr(0, mappedHeaders.length-1);
                    mappedHeaders = "{"+mappedHeaders+"}";
                    var mismatch = 0;
                    for(j=0;j<comboCount;j++){  //mapping-combo record count
                        if(headerArray[j]>1){   //for one to one mapping use " != 1"
                            mismatch = 1;
                            break;
                        }
                    }
                    if(mismatch == 1){
                    	msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.recruitment.headers.mappings")], 1);//msgBoxShow(["Error", "Please Check Headers Mappings."], 1);
                        return;
                    }
                    rec=this.sm2.getSelected().data;
                    var uname = rec.fname+'.'+rec.lname;
                    Wtf.Ajax.requestEx({
                            url:"Rec/Job/getEmpidFormat.rec",
                            params: {
                                flag:208
                            }
                        },this,
                        function(req){
                            this.resp=eval('('+req+')');
                            this.values=this.resp.data[0].maxempid;
                            Wtf.Ajax.requestEx({
                                url:"Rec/Job/transferappdata.rec",
                                params: {
                                    flag:166,
                                    mappedheader : mappedHeaders,
                                    applicantid: profid,
                                    employeetype: this.applicantcmb.getValue(),
                                    employeerectype: rec.employeetype,
                                    employeeid: this.values,
                                    empjoindate: rec.joiningdate.format("Y-m-d"),
                //                    applicationid:rec.id,
                                    designationid:rec.designationid,
                                    departmentid:rec.departmentid,
                                    appusername: this.uname
                                }
                            }, this,
                            function(response){
                                calMsgBoxShow(200,4,true);
                                if(response.success){
                                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), response.msg],0);
                                    if(rec.employeetype==0){
                                        this.headerMapWin.close();
                                    }
                                    this.qualifiedGDS.load({
                                        params:{
                                            start:0,
                                            limit:this.qualifiedGrid.pag.pageSize,
                                            ss: Wtf.getCmp("Quick"+this.qualifiedGrid.id).getValue()
                                        }
                                    });
                                }else{
                                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), response.msg],2);//calMsgBoxShow(['Warning', response.msg],2);
                                }
                            },
                            function()
                            {
                                calMsgBoxShow(65,1);
                            })
                        },
                        function(){
                        });
                }
            },{
                text:WtfGlobal.getLocaleText("hrms.common.cancel"),//'Cancel',
                scope:this,
                handler:function() {
                    this.headerMapWin.close();
                }
            }]
        }),
        this.headerMapWin.show();
        this.changeflag = 1;
        haderMapgrid.on("beforeedit",function(object){
            if(object.value==1||object.value==2||object.value==3||object.value==4){
                object.cancel=true;
            }
        }, this);
        
    },
    saveuserName:function(rec){
        this.userflag=false;
        if(this.applicantcmb.getValue()=="Internal" || rec.employeetype==1){
            this.userflag=true;
        }else{
            if(this.usernameForm.getForm().isValid()){
                this.userflag=true;
            }
        }
        if(this.userflag){
            Wtf.Ajax.requestEx({
                url:"Rec/Job/transferappdata.rec",
                params: {
                    flag:166,
                    employeetype:this.applicantcmb.getValue(),
                    employeerectype: rec.employeetype,
                    employeeid:this.employeeID.getValue(),
                    applicantid:rec.apcntid,
                    empjoindate: rec.joiningdate.format("Y-m-d"),
                    applicationid:rec.id,
                    designationid:rec.designationid,
                    departmentid:rec.departmentid,
                    appusername:this.userName.getValue(),
                    emailid:this.emailId.getValue()
                }
            }, this,
            function(response){
                calMsgBoxShow(200,4,true);
                if(response.success){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), response.msg],0);//calMsgBoxShow(['Success', response.msg],0);
                    if(this.applicantcmb.getValue()=="External"){
                        this.userWindow.close();
                    }
                    this.qualifiedGDS.load({
                        params:{
                            start:0,
                            limit:this.qualifiedGrid.pag.pageSize,
                            ss: Wtf.getCmp("Quick"+this.qualifiedGrid.id).getValue()
                        }
                    });
                }else{
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), response.msg],2);//calMsgBoxShow(['Success', response.msg],2);
                }
            },
            function()
            {
            	calMsgBoxShow(65,1);
            })
        }else{
            return;
        }
    },
      gridloads:function(status){       
        if(status=='Rejected'){
            var rejectgrid=Wtf.getCmp('rejectedrejectedgr');
            if(rejectgrid!=null){
                rejectgrid.getStore().load();
            }
        }
        if(status!='Rejected'&&status!='Selected'){
            var allappsgrid=Wtf.getCmp('allapplsallappsviewgr');
            if(allappsgrid!=null){
                allappsgrid.getStore().load();
            }
        }
    },
    exportinfo:function(){
        var i,k=1;
        var column = this.qualifiedGrid.getColumnModel();
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
        this.grid = this.qualifiedGrid;
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.searchparams,
            searchid:this.searchid
        });
     //   this.objsearchComponent.searchFlag = 5;
    },
    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.searchStore.load(
        {params:{
            searchid:this.searchid,
            searchFlag:5
        }});
        this.advanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
    	this.qualifiedGDS.baseParams = {
            mode:114,
            employeetype:this.type,
            flag:38,
            gridst:1
        };
    	this.qualifiedGDS.load();
        this.searchJson="";
        this.searchid="";
        this.objsearchComponent.hide();
        this.advanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.qualifiedGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            employeetype:this.type,
            flag:38,
            gridst:1
        };
        this.qualifiedGDS.load();
    },
    reloadgridStore:function(json){
        this.searchJson="";
        if(this.searchid!=undefined){
            this.searchJson=json;
        }
        this.qualifiedGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            employeetype:this.type,
            flag:38,
            gridst:1
        };
        this.qualifiedGDS.load();
    },
    saveStore:function(json, saveSearchName){
        this.saveJson=json;
        Wtf.Ajax.requestEx({
            url:"Common/saveSearch.common",
            params:{
                mode:115,
                saveJson:this.saveJson,
                saveSearchName:saveSearchName,
                searchFlag:5
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
