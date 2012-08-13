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

Wtf.Appraisalsmanagement=function(config){
    Wtf.Appraisalsmanagement.superclass.constructor.call(this,config);
    Wtf.form.Field.prototype.msgTarget='side';

    this.rowExpander = new Wtf.grid.RowExpander({
        tpl : new Wtf.XTemplate(
            '<table cellspacing="5" cellpadding="0">',
                '<tr>',
                    '<td width=150>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr align="center" class="fixed">',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle.name")+'</b></th>',//'<th><b>Appraisal cycle Name</b></th>',
                            '</tr>',
                         '<tpl for="cyclename">',
                            '<tr align="left" class="fixed">',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                         '</tpl>',
                        '</table>',
                    '</td>',
                    '<td width=200>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr>',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.common.start.date")+'</b></th>',//'<th><b>Start Date</b></th>',
                            '</tr>',
                              '<tpl for="startdate">',
                            '<tr>',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                              '</tpl>',
                        '</table>',
                    '</td>',
                    '<td width=200>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr>',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.common.end.date")+'</b></th>',//'<th><b>End Date</b></th>',
                            '</tr>',
                             '<tpl for="enddate">',
                            '<tr>',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                             '</tpl>',
                        '</table>',
                    '</td>',
                    '<td width=150>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr>',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.performance.appraiser")+'</b></th>',//'<th><b>Appraiser</b></th>',
                            '</tr>',
                             '<tpl for="manager">',
                            '<tr>',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                             '</tpl>',
                        '</table>',
                    '</td>',
                    '<td width=200>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr>',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.performance.submission.start.date")+'</b></th>',//'<th><b>Submission Start Date</b></th>',
                            '</tr>',
                              '<tpl for="sdate">',
                            '<tr>',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                              '</tpl>',
                        '</table>',
                    '</td>',
                    '<td width=200>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr>',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.performance.submission.end.date")+'</b></th>',//'<th><b>Submission End Date</b></th>',
                            '</tr>',
                              '<tpl for="edate">',
                            '<tr>',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                              '</tpl>',
                        '</table>',
                    '</td>',
                    '<td width=150>',
                        '<table width="100%" cellspacing="0" cellpadding="0">',
                            '<tr>',
                            	'<th><b>'+WtfGlobal.getLocaleText("hrms.common.status")+'</b></th>',//'<th><b>Status</b></th>',
                            '</tr>',
                              '<tpl for="status">',
                            '<tr>',
                                '<td>',
                                    '{.}',
                                '</td>',
                            '</tr>',
                              '</tpl>',
                        '</table>',
                    '</td>',
                '</tr>',
            '</table>'
            )
    });
};
Wtf.extend(Wtf.Appraisalsmanagement,Wtf.Panel,{
    initComponent:function(config){
        Wtf.Appraisalsmanagement.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.Appraisalsmanagement.superclass.onRender.call(this,config);

        this.itemDataRec = new Wtf.data.Record.create([{
            name:"id"
        },{
            name:"name"
        }]);

        this.itemDataReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.itemDataRec);

        this.apprec=new Wtf.data.Record.create([
        {
            name:'appcycleid'
        },
        {
            name:'appcycle'
        },
        {
            name:'startdate'
        },
        {
            name:'enddate'
        },
        {
            name:'currentFlag'
        },
        {
            name:'submitstartdate'
        },
        {
            name:'submitenddate'
        }
        ]);
           this.appTypeStore =  new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url:"Performance/Appraisalcycle/getAppraisalcycleform.pf",
            baseParams: {
                flag: 168,
                grouper:'initiateapp'
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.apprec),
            autoLoad : false
        });
        this.appTypeStore.on("load",function() {
            var row=this.appTypeStore.findBy(this.findrecord,this);
            if(row!=-1) {
                this.appTypeCombo.setValue(this.appTypeStore.getAt(row).get('appcycleid'));
                this.appstdate.setValue(this.appTypeStore.getAt(row).get('startdate'));
                this.appsenddate.setValue(this.appTypeStore.getAt(row).get('enddate'));
                this.startdate.setValue(this.appTypeStore.getAt(row).get('submitstartdate'));
                this.enddate.setValue(this.appTypeStore.getAt(row).get('submitenddate'));
            }
        },this);
        this.appTypeStore.load();

        this.appTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            mode:"local",
            valueField:'appcycleid',
            displayField:'appcycle',
            store:this.appTypeStore,
            emptyText:WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle"),//'Select appraisal cycle',
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle")+" *"),//"Appraisal Cycle *",
//            allowBlank:false,
            width:200,
            typeAhead:true
        });
        this.appTypeCombo.on('select',function(a,b,c){
                  this.appstdate.setValue(b.data.startdate);
                  this.appsenddate.setValue(b.data.enddate);
                  this.startdate.setValue(b.data.submitstartdate);
                  this.enddate.setValue(b.data.submitenddate);
        },this);       
        this.deptrec=new Wtf.data.Record.create([
        {
            name:'id'
        },

        {
            name:'name'
        }
        ]);
           this.deptStore =  new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            baseParams: {
                configid:7,
                flag:203,
                common:1
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.deptrec),
            autoLoad : false
        });
        this.deptStore.load();
        this.deptStore.on('load',this.insertrec,this);
        this.deptCmb = new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.department")+'*',
            store:this.deptStore,
            mode:'local',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,
            allowBlank:false,
            width:150,
            addNewFn:this.addDepartment.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
                    this.depadded=true;                                 // flag check for dep addition
                    WtfGlobal.showmasterWindow(7,this.deptStore,"Add");//WtfGlobal.showmasterWindow(7,this.deptStore,"Add");
                },
                scope: this
            })]
        });
        this.deptCmb.on('select',function(){
            this.deptval=this.deptCmb.getValue();
            this.appraisalAppraisalsmanagementStore.load({
                        params:{
                            start:0,
                            limit:this.appraisalAppraisalsmanagementGrid.pag.pageSize,
                            ss:this.appraisalAppraisalsmanagementGrid.quickSearchTF.getValue()
                        }
                   });

        },this);
        this.empname = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employee.name"),//"Employee Name",
            name:"username",
            width:200,
            readOnly:true
        });
        this.startdate = new Wtf.form.DateField({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.submission.start.date")+" *"),//"Submission Start Date *",
            name:"startdate",
            format:'m/d/Y',
            width:150,
            allowBlank:false,
            disabled:true
        });
        this.appstdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.appraisal.start.date"),//"Appraisal Cycle Start Date",
            format:'m/d/Y',
            width:150,
            disabled:true
        });
        this.appsenddate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.appraisal.end.date"),//"Appraisal Cycle End Date",
            format:'m/d/Y',
            width:150,
            disabled:true
        });

        this.enddate = new Wtf.form.DateField({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.submission.end.date")+" *"),//"Submission End Date *",
            name:"enddate",
            format:'m/d/Y',
            width:150,
            allowBlank:false,
            disabled:true
        });

        this.initiatePanel=new Wtf.Panel({
            frame:false,
            border:false,
            layout:'column',
            items:[
            {
                columnWidth:.33,
                frame:false,
                border:false,
                layout:'form',
                items:[this.appTypeCombo]
            },
            {
                columnWidth:.33,
                frame:false,
                border:false,
                layout:'form',
                items:[this.appstdate,this.startdate ]
            },
            {
                columnWidth:.33,
                frame:false,
                border:false,
                layout:'form',
                items:[this.appsenddate,this.enddate]
            }
            ]
        });


        this.appraisalform = new Wtf.form.FormPanel({
            border:false,
            frame:false,
            id:'appraisalmanagementform',
            bodyStyle:"background-color:#FFFFFF;padding:20px 20px 20px 20px;",
            items:[
                this.initiatePanel
            ]});

        this.recipeAppraisalsmanagementRec = new Wtf.data.Record.create([{
            name:"userid"
        },{
            name:'appraisalid'
        },{
            name:"employeeid"
        },{
            name:"email"
        },{
            name:'fullname'
        },{
            name:'joindate',
            type:'date'
        },{
            name:'designation'
        },{
            name:'department'
        },{
            name:'cyclename'
        },{
            name:'startdate'
        },{
            name:'enddate'
        },{
            name:'manager'
        },{
            name:'sdate'
        },{
            name:'edate'
        },{
            name:'status'
        }]);

        this.appraisalAppraisalsmanagementReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.recipeAppraisalsmanagementRec);

        this.appraisalAppraisalsmanagementStore = new Wtf.data.Store({
//            url: Wtf.req.base + "hrms.jsp",
            url:"Performance/Appraisal/getappraisalFunction.pf",
            reader:this.appraisalAppraisalsmanagementReader
        });
        this.deptval="";
        this.appraisalAppraisalsmanagementStore.on("beforeload",function(){
            this.appraisalAppraisalsmanagementStore.baseParams= {
                flag: 163,
                paging:true,
                dept:this.deptval,
                grouper:'initiateapp',
                firequery:'1'
            }
        },this);
        calMsgBoxShow(202,4,true);
        this.appraisalAppraisalsmanagementStore.load({params:{start:0,limit:15}});
        this.appraisalAppraisalsmanagementStore.on("load",function(){
        if(msgFlag==1)
            WtfGlobal.closeProgressbar();
        },this);


        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.appraisalAppraisalsmanagementColumn = new Wtf.grid.ColumnModel([
            this.rowExpander, this.sm,{
                header:WtfGlobal.getLocaleText("hrms.common.employee.id"),//"Employee Id",
                dataIndex:"employeeid",
                sortable: true,
                renderer : function(val) {
            		return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.edit.initiated.appraisals")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select the row(s) to edit initiated appraisals\" wtf:qtitle='Description'>"+val+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.common.employee.name"),//"Employee Name ",
                dataIndex:"fullname",
                sortable: true,
                renderer : function(val) {
            		return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.edit.initiated.appraisals")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select the row(s) to edit initiated appraisals\" wtf:qtitle='Description'>"+val+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.common.designation"),//"Designation",
                dataIndex:"designation",
                sortable: true,
                renderer : function(val) {
            		return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.edit.initiated.appraisals")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select the row(s) to edit initiated appraisals\" wtf:qtitle='Description'>"+val+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
                dataIndex:"department",
                sortable: true,
                renderer : function(val) {
            		return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.edit.initiated.appraisals")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select the row(s) to edit initiated appraisals\" wtf:qtitle='Description'>"+val+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email Id",
                dataIndex:"email",
                sortable: true,
                renderer : function(val) {
            	return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.edit.initiated.appraisals")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select the row(s) to edit initiated appraisals\" wtf:qtitle='Description'>"+val+"</div>";
                }
            }]);

        this.initiateButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.initiate"),//'Initiate',
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            disabled:true,
            minWidth:50,
            tooltip :WtfGlobal.getLocaleText("hrms.performance.initiate.tooltip"),//'Select an employee from the list and enter the appraisal type, start & end date and initiate his/her appraisal.',
            handler:this.initiateapprsal,
            scope:this
        });
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        	this.appraisalAppraisalsmanagementStore.load({params:{start:0,limit:this.appraisalAppraisalsmanagementGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.appraisalAppraisalsmanagementGrid.id).setValue("");
        	}
     	});

//        this.viewButton= new Wtf.Button({
//            text:'View Appraisal Status',
//            tooltip:"View the previous appraisals done and the status of the same by selecting an employee from the list.",
//            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
//            minWidth:70,
//            disabled:true,
//            handler:this.viewappraisal,
//            scope:this
//        });
    var initiatebtns=new Array();
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.initiateappraisal, Wtf.Perm.initiateappraisal.manage)){
            initiatebtns.push('-');
            initiatebtns.push(this.refreshBtn);
            initiatebtns.push('-');
            initiatebtns.push(this.initiateButton);
//            initiatebtns.push('-');
//            initiatebtns.push(this.viewButton);
    }
    if(userroleid==1){
            initiatebtns.push('-',(WtfGlobal.getLocaleText("hrms.common.select.department")+":"),this.deptCmb);//'Select Department:',this.deptCmb);
        }
        this.appraisalAppraisalsmanagementGrid = new Wtf.KwlGridPanel({
            cm:this.appraisalAppraisalsmanagementColumn,
            sm:this.sm,
            border:false,
            frame:false,
            plugins : this.rowExpander,
            id:'appraisalmanagementgrid',
            store:this.appraisalAppraisalsmanagementStore,
            searchField:"fullname",
            serverSideSearch:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            displayInfo:true,
            loadMask:true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),//"Search by Employee Name ",
            viewConfig: {
                forceFit: true,
                emptyText:Wtf.gridEmptytext
            },
            tbar:initiatebtns

        });
        this.appraisalPanel = new Wtf.Panel({
            layout:'border',
            border:false,
            items:[{
                region:'north',
                height:120,
                cls : 'formstyleClass2',
                border:false,
                frame:false,
                layout:'fit',
                id:'npanel'+this.id,
                items:[this.appraisalform]
            },{
                        region:'center',
                        border:false,
                        frame:false,
                        layout:'fit',
                        items:[this.appraisalAppraisalsmanagementGrid]
                }]
        });
        this.add(this.appraisalPanel);
        this.sm.on("selectionchange",function (){
            WtfGlobal.enableDisableBtnArr(initiatebtns, this.appraisalAppraisalsmanagementGrid, [], [3]);
        },this);
        this.on("activate",function(){
            Wtf.getCmp('npanel'+this.id).setHeight(120);
            this.appraisalPanel.doLayout();
        },this)

    },

    findrecord:function(rec) {
        if(rec.get('currentFlag')=="1"){
            return true;
        }else{
            return false;
        }
    },

    initiateapprsal:function(){
        
        if(this.appTypeStore.getTotalCount() < 1){
            
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.performance.please.create.appraisal.cycle")],0);
            return ;
        }
        
        this.appform= Wtf.getCmp('appraisalmanagementform');
        if(!this.appraisalform.getForm().isValid()||new Date(this.startdate.getValue())>new Date(this.enddate.getValue())){            
        	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle")],0);//calMsgBoxShow(["Warning","Please select an appraisal cycle."],0);
            return ;
        }
        else{
            if(Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().getSelections()==0){
                calMsgBoxShow(42,0);
            }
            else{
                var cnt=Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().getCount();
                var arr=Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().getSelections();
                var errStringStart = WtfGlobal.getLocaleText("hrms.performance.employees.not.eligible.appraisal")+':<br><div style="margin-left:60px"><ul><b>';//'Following employees are not eligible for the selected appraisal cycle.Please check the joining date:<br><div style="margin-left:60px"><ul><b>';
                var errStringUsers = '';
                var errStringDesg = "";
                var errStringEnd = '</ul></b></div>';
                for(var i=0,j=1;i<cnt;i++){
                    if(arr[i].get('joindate')=="" || arr[i].get('joindate').clearTime()>this.appstdate.getValue()){
                        errStringUsers += "<li>"+j+". "+arr[i].get('fullname')+'</li>';
                        j+=1;
                    }
                    if(arr[i].get('designation')==""){
                        errStringDesg = WtfGlobal.getLocaleText("hrms.performance.set.designation.employee");//"Please set the designation of the employee.";
                    }
                }
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                    msg:WtfGlobal.getLocaleText("hrms.performance.initiate.appraisal.cycle.employee"),//'Are you sure you want to initiate the Appraisal Cycle for the selected employee(s)?',
                    buttons:Wtf.MessageBox.YESNO,
                    width:475,
                    icon:Wtf.MessageBox.QUESTION,
                    scope:this,
                    fn:function(button){
                        if(button=='yes'){
                            if(errStringDesg!=""){
                            	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),errStringDesg],2);//calMsgBoxShow(["Warning",errStringDesg],2);
                            } else {
                                if(errStringUsers == ''){
                                    this.empids=[];
                                    this.appids=[];
                                    this.appraisalflag=true;
                                    for(var i=0;i<cnt;i++) {
                                        this.empids.push(arr[i].get('userid'));
                                        if(arr[i].get('status')=="initiated"){
                                            this.appids.push(arr[i].get('appraisalid'));
                                        } else if(arr[i].get('status')=="submitted"){
                                            this.appids.push("");
                                        } else if(arr[i].get('status')=="pending" && arr[i].get('cyclename')[0] == this.appTypeCombo.getRawValue()){
                                            this.appraisalflag=false;
                                            break;
                                        } else{
                                            this.appids.push("");
                                        }
                                    }
                                    if(this.appraisalflag){
                                        calMsgBoxShow(200,4,true);
                                        Wtf.Ajax.requestEx({
    //                                        url: Wtf.req.base + "hrms.jsp",
                                            url: "Performance/Appraisal/AppraisalAssign.pf",
                                            params:{
                                                flag:18,
                                                empids:this.empids,
                                                appraisalids:this.appids,
                                                apptype:this.appTypeCombo.getValue(),
                                                startdate:this.startdate.getValue().format("Y-m-d"),
                                                enddate:this.enddate.getValue().format("Y-m-d"),
                                                status:'initiated'
                                            }
                                        },
                                        this,
                                        function(response){
                                            var res=eval('('+response+')');
                                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),res.message],0);//calMsgBoxShow(["Success",res.message],0);
                                            this.deptval=this.deptCmb.getValue();
                                            Wtf.getCmp('appraisalmanagementgrid').store.load({
                                                params:{
                                                    start:0,
                                                    limit:this.appraisalAppraisalsmanagementGrid.pag.pageSize,
                                                    ss:this.appraisalAppraisalsmanagementGrid.quickSearchTF.getValue()
                                                }
                                            });
                                            Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().clearSelections() ;
                                            var a=Wtf.getCmp("DSBMyWorkspaces");
                                            if(a){
                                                a.doSearch(a.url,'');
                                            }
                                            a=Wtf.getCmp("dash_performance");
                                            if(a){
                                                a.doSearch(a.url,'');
                                            }

                                        },
                                        function(){
                                            calMsgBoxShow(27,1);
                                        })
                                    }else{
                                        calMsgBoxShow(171,1);
                                        Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().clearSelections();
                                    }
                                }else{
                                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),errStringStart+errStringUsers+errStringEnd],2,false,600);//calMsgBoxShow(["Warning",errStringStart+errStringUsers+errStringEnd],2,false,600);
                                }
                            }
                        }else{
                            Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().clearSelections() ;                          
                        }
                    }
                })
            }
        }
    },
    cancelappraisal:function(){
        Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().clearSelections() ;        
    },
//    viewappraisal:function(){
//        var record=Wtf.getCmp('appraisalmanagementgrid').getSelectionModel().getSelected().data;
//        var main=Wtf.getCmp("goalmanagementtabpanel");
//        var demoTab=Wtf.getCmp(record.userid+"appraisalstatus");
//        if(demoTab==null)
//        {
//                demoTab=new Wtf.viewAppraisal({
//                    id:record.userid+"appraisalstatus",
//                    title:record.fullname+"'s"+" Appraisal Status",
//                    iconCls:getTabIconCls(Wtf.etype.hrmsmygoals),
//                    autoScroll:true,
//                    border:false,
//                    layout:'fit',
//                    userid:record.userid,
//                    closable:true
//                });
//                main.add(demoTab);
//            }
//            main.setActiveTab(demoTab);
//            main.doLayout();
//            Wtf.getCmp("as").doLayout();
//    },
    addDepartment:function(){
    	WtfGlobal.showmasterWindow(7,this.deptStore,"Add");//WtfGlobal.showmasterWindow(7,this.deptStore,"Add");
    },
    insertrec:function(){
            if(this.deptStore.getCount()>0){
                var allrec=new this.deptrec({
                    id:'0',
                    name:'All'
                });
                var c=this.deptStore.getCount();
                this.deptStore.insert(c,allrec);
                this.deptCmb.setValue(this.deptStore.getAt(c).get('id'));
     this.deptval=this.deptCmb.getValue();
            if(this.depadded) {
                this.appraisalAppraisalsmanagementStore.load({
                        params:{
                            start:0,
                            limit:15,
                            ss:this.appraisalAppraisalsmanagementGrid.quickSearchTF.getValue()
                        }
                   });
            }
        }
    }
//    addappraisalCycle:function(){
//        this.sDate = new Wtf.form.DateField({
//            fieldLabel:"Start Date *",
//            format:'m/d/Y',
//            width:200,
//            allowBlank:false
//        });
//        this.eDate = new Wtf.form.DateField({
//            fieldLabel:"End Date *",
//            format:'m/d/Y',
//            width:200,
//            allowBlank:false
//        });
//        this.cycleName= new Wtf.form.TextField({
//            fieldLabel:'Cycle *',
//            width:200,
//            maxLength:50,
//            allowBlank:false
//        });
//
//        this.appcycleForm = new Wtf.form.FormPanel({
//            waitMsgTarget: true,
//            border : false,
//            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
//            autoScroll:false,
//            lableWidth :70,
//            layoutConfig: {
//                deferredRender: false
//            },
//            items:[this.cycleName,this.sDate,this.eDate]
//        });
//
//        this.appcyclePanel= new Wtf.Panel({
//            border: false,
//            layout:'fit',
//            autoScroll:false,
//            items:[{
//                border:false,
//                region:'center',
//                layout:"border",
//                items:[{
//                    region : 'north',
//                    height : 75,
//                    border : false,
//                    bodyStyle : 'background:white;border-bottom:1px solid #FFFFFF;',
//                    html: getTopHtml("Create Appraisal cycle", "Please enter following details to create appraisal cycle")
//                },{
//                    border:false,
//                    region:'center',
//                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
//                    layout:"fit",
//                    items: [this.appcycleForm]
//                }]
//            }]
//        });
//
//        this.appcycleWindow=new Wtf.Window({
//            iconCls:getButtonIconCls(Wtf.btype.winicon),
//            layout:'fit',
//            closable:true,
//            width:380,
//            title:'Appraisal Cycle',
//            height:280,
//            border:false,
//            modal:true,
//            scope:this,
//            plain:true,
//            buttonAlign :'right',
//            buttons: [{
//                text:'Submit',
//                handler:function(){
//                    this.setappraisalCycle();
//                },
//                scope:this
//            },{
//                text: 'Cancel',
//                scope:this,
//                handler:function(){
//                    this.appcycleWindow.close();
//                }
//            }],
//            items: [this.appcyclePanel]
//        });
//        this.appcycleWindow.show();
//
//    },
//    setappraisalCycle:function(){
//        if(new Date(this.sDate.getValue())>new Date(this.eDate.getValue())){
//            calMsgBoxShow(170,1);
//            this.sDate.setValue("");
//            this.eDate.setValue("");
//        }
//        if(this.appcycleForm.getForm().isValid()){
//            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp",
//                params: {
//                    flag:167,
//                    cyclename:this.cycleName.getValue(),
//                    startdate:this.sDate.getValue().format("Y-m-d"),
//                    enddate:this.eDate.getValue().format("Y-m-d")
//                }
//            }, this,
//            function(response){
//                var res=eval('('+response+')');
//                if(res.message){
//                    calMsgBoxShow(168,2);
//                }else{
//                    this.appcycleWindow.close();
//                    calMsgBoxShow(169,2);
//                }
//                this.appTypeStore.load();
//                this.appcycleWindow.close();
//            },
//            function()
//            {
//                calMsgBoxShow(65,1);
//            })
//        } else{
//            return;
//        }
//    }
});
