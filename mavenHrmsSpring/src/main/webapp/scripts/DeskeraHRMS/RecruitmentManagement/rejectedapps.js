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
Wtf.rejectedApps = function(config){
    Wtf.apply(this, config);
    Wtf.rejectedApps.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.rejectedApps, Wtf.Panel, {
    initComponent: function(){
        Wtf.rejectedApps.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.rejectedApps.superclass.onRender.call(this,config);
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
                items:[this.rejectGrid]
            }]
        });
        this.add(this.pan);
        this.exportinfo();
        this.type = 0;
        this.status = "";
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
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

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
        var employeetype=Wtf.cmpPref.defaultapps=="Internal"?"1":"0";
        this.rejectGDS =new Wtf.ux.MultiGroupingStore({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getJobApplications.rec",
            baseParams: {
                flag:38,
                gridst:2,
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
        this.rejectGDS.load();
        this.rejectGDS.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);
        
        this.cm = new Wtf.grid.ColumnModel(
            [this.sm2,{
                header: WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
                dataIndex: WtfGlobal.getLocaleText("hrms.common.department"),
                exportDataIndex:'Department'
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
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.rejected.for"),//"Job Id Rejected For",
                dataIndex: WtfGlobal.getLocaleText("hrms.common.jobid"),
                exportDataIndex:'JobId',
                sortable: true,
                renderer:WtfGlobal.linkRenderer
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.applied.date"),//"Applied Date",
                dataIndex: 'applydt',
                exportDataIndex:'applydt',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true
            },
            {
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

        this.appdocs=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.documents"),//'Documents',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.rejected.documents.tooltip"),//"The documents uploaded by the applicants can be accessed here.",
            iconCls:getButtonIconCls(Wtf.btype.docbutton),
            id:this.id+'documentsrej',
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
        
        this.exportApplications = new Wtf.Button({
            text: WtfGlobal.getLocaleText("hrms.common.ExportApplications"),
            iconCls: 'pwndExport export',
            minWidth:81,
            scope:this,
            id:this.id+'exportApplications',
            handler:this.exportApplications,
            disabled:Wtf.cmpPref.defaultapps=="External"?false:true
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
        	name:"jobid",
        	dbname:"position.jobid",
        	header:WtfGlobal.getLocaleText("hrms.recruitment.job.applied.for"),//"Job Id Applied For",
        	xtype:'textfield'
        }];
        this.ExportInfoBtn = new Wtf.exportButton({
            obj:this,
            menuItem:{
                csv:true,
                rowPdf:true,
                xls:true
            },
            userinfo:true,
            get:3,
            url: "Rec/Job/getJobApplicationsExport.rec",
            filename:this.title
        });
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.rejectGDS.load({params:{start:0,limit:this.rejectGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.rejectGrid.id).setValue("");
        	}
     	});
        this.deleteapp= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.delete.application"),//'Delete Application',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.rejected.delete.application.tooltip"),//"The application(s) which does not meet the job profile are not included in the consideration set and hence are deleted.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:109,
            disabled:true,
            scope:this,
            handler:this.deleteapps
        });
        this.viewprofile= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.view.profile"),//'View Profile',
            tooltip:WtfGlobal.getLocaleText("hrms.recruitment.rejected.view.profile.tooltip"),//"View the profile of rejected applicants.",
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:81,
            disabled:true,
            id:this.id+'viewprofilerejected',
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

        var rejectbtns=[];
        rejectbtns.push('-',this.refreshBtn);
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.rejectedapps, Wtf.Perm.rejectedapps.manage)){
            rejectbtns.push('-',this.deleteapp,'-',this.viewprofile,'-',this.editprospect,'-',this.appdocs,'-',this.ExportInfoBtn,'-',this.advanceSearchBtn);
        }
        rejectbtns.push('->','-',(WtfGlobal.getLocaleText("hrms.recruitment.applicant.type")+":"));//'Applicant Type:');
        rejectbtns.push(this.applicantcmb);
        this.rejectGrid = new Wtf.KwlEditorGridPanel({
            border: false,
            id:this.id+'rejectedgr',
            store: this.rejectGDS,
            cm: this.cm,
            sm: this.sm2,        
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.allapplication.grid.search.msg"),//"Search by Department, Job ID, Candidate Name",
            searchField:"cname",
            serverSideSearch:true,
            tbar: rejectbtns,
            bbar:[this.exportApplications],
            view: new Wtf.ux.MultiGroupingView({
                forceFit: true,
                showGroupName:false,
                enableGroupingMenu: false,
                hideGroupedColumn: true,
                groupTextTpl:'{text}',
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.rejected")+" "+this.applicantcmb.getValue()+" "+WtfGlobal.getLocaleText("hrms.recruitment.application.till.now"))//WtfGlobal.emptyGridRenderer("No Rejected "+this.applicantcmb.getValue()+" application till now")
            })
        });

        this.applicantcmb.on('select',function(a,b,c){
            var type;
            if(b.data.applicanthidden=="Internal"){
                type=1;
                this.exportApplications.setDisabled(true);
            }else if(b.data.applicanthidden=="Both"){
                type=2;
                this.exportApplications.setDisabled(true);
            }else{
                type=0;
                this.exportApplications.setDisabled(false);
            }
            this.type = type;
            this.rejectGDS.baseParams={
                employeetype:type,
                flag:38,
                gridst:2
            }
            this.rejectGDS.load({
                params:{
                    start:0,
                    limit:this.rejectGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.rejectGrid.id).getValue()
                }
            });
            this.rejectGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.no.rejected")+" "+this.applicantcmb.getValue()+" "+WtfGlobal.getLocaleText("hrms.recruitment.application.till.now"));//WtfGlobal.emptyGridRenderer("No rejected "+this.applicantcmb.getValue()+" application till now");
            this.rejectGrid.getView().refresh();
        },this); 

        this.sm2.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(rejectbtns, this.rejectGrid, [5,9], [3]);
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
//        this.rejectGrid.on('cellclick',this.onCellClick, this);
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
                icon:Wtf.MessageBox.QUESTION,
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:deleteMsgBox('application'),
                buttons:Wtf.MessageBox.YESNO,
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
                            this.rejectGDS.load({
                                params:{
                                    start:0,
                                    limit:this.rejectGrid.pag.pageSize,
                                    ss: Wtf.getCmp("Quick"+this.rejectGrid.id).getValue()
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
    
    savedata:function(){
        var jsondata = "";
        var record;
        for(var i=0;i< this.rejectGDS.getCount();i++){
            record=this.rejectGDS.getAt(i).data;
            jsondata += "{'status':'" + record.status + "',";
            jsondata += "'id':'" + record.id + "',";
            jsondata += "'recruiter':'" + record.recruiter + "',";
            jsondata += "'interviewdt':'" + record.interviewdt + "',";
            jsondata += "'rank':'" + record.rank + "',";
            jsondata += "'callback':'" + record.callback + "',";
            jsondata += "'apcntid':'" + record.apcntid + "'},";
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
            calMsgBoxShow(60,0);
            this.rejectGDS.load({
                params:{
                    start:0,
                    limit:this.rejectGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.rejectGrid.id).getValue()
                }
            });
            var allgrid=Wtf.getCmp('allapplsallappsviewgr');
            if(allgrid!=null){
                allgrid.getStore().load();
            }
            var qualgrid=Wtf.getCmp('qualifiedqualifiedgr');
            if(qualgrid!=null){
                qualgrid.getStore().load();
            }
        },
        function(response)
        {
            calMsgBoxShow(65,1);
        })
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
                employeetype:type,
                rejected:true,
                resizable:false,
                layout:'fit',
                appid:this.id,
                editval:'Rejected'
            });
            this.editprospect.show();
            this.editprospect.on('editpload',this.gridloads,this);
        }else{
            calMsgBoxShow(42,0);
        }
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
    gridloads:function(status){
        if(status=='Selected'){
            var qualgrid=Wtf.getCmp('qualifiedqualifiedgr');
            if(qualgrid!=null){
                qualgrid.getStore().load();
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
        var column = this.rejectGrid.getColumnModel();
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
        this.grid = this.rejectGrid;
    },
    exportApplications: function(){
    	var url = "Rec/Job/exportAllApplications.rec?"+Wtf.urlEncode(Wtf.urlDecode("employeetype="+this.type+"&status="+this.status+"&visible="+true+"&filetype="+"csv"+"&name="+this.title+"&applicationflag="+2+"&searchJson="+this.searchJson));
    	Wtf.get('downloadframe').dom.src = url;
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.searchparams,
            searchid:this.searchid
        });
    //    this.objsearchComponent.searchid = this.searchid;
    },
    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.searchStore.load(
        {params:{
            searchid:this.searchid,
            searchFlag:4
        }});
        this.advanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
    	this.rejectGDS.baseParams = {
            mode:114,
            employeetype:this.type,
            flag:38,
            gridst:2
        };
    	this.rejectGDS.load();
        this.searchJson="";
        this.searchid="";
        this.objsearchComponent.hide();
        this.advanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.rejectGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            employeetype:this.type,
            flag:38,
            gridst:2
        };
        this.rejectGDS.load();
    },
    reloadgridStore:function(json){
        this.searchJson="";
        if(this.searchid!=undefined){
            this.searchJson=json;
        }
        this.rejectGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson,
            employeetype:this.type,
            flag:38,
            gridst:2
        };
        this.rejectGDS.load();
    },
    saveStore:function(json, saveSearchName){
        this.saveJson=json;
        Wtf.Ajax.requestEx({
            url:"Common/saveSearch.common",
            params:{
                mode:115,
                saveJson:this.saveJson,
                saveSearchName:saveSearchName,
                searchFlag:4
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

