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
Wtf.Payroll_Status_Renderer_NOT_STARTED=WtfGlobal.getLocaleText("hrms.payroll.not.started");
Wtf.Payroll_Status_Renderer_ENTERED=WtfGlobal.getLocaleText("hrms.payroll.entered");
Wtf.Payroll_Status_Renderer_CALCULATED=WtfGlobal.getLocaleText("hrms.payroll.calculated");
Wtf.Payroll_Status_Renderer_AUTHORIZED=WtfGlobal.getLocaleText("hrms.payroll.Authorized");
Wtf.Payroll_Status_Renderer_UNAUTHORIZED=WtfGlobal.getLocaleText("hrms.payroll.Unauthorized");
Wtf.Payroll_Status_Renderer_PROCESSED_TRIAL=WtfGlobal.getLocaleText("hrms.payroll.processed.trial");
Wtf.Payroll_Status_Renderer_PROCESSED_FINAL=WtfGlobal.getLocaleText("hrms.payroll.processed.final");

Wtf.payrollStatusRenderer = function(val,a,record){
    if(val!=null){
        if(val==0){
            return Wtf.Payroll_Status_Renderer_NOT_STARTED;
        }else if(val==1){
            return '<span style=\'color:DarkOrchid !important;\'>'+Wtf.Payroll_Status_Renderer_ENTERED+'</span>';
        } else if(val==2){
            return '<span style=\'color:blue !important;\'>'+Wtf.Payroll_Status_Renderer_CALCULATED+'</span>';
        } else if(val==3){
            return '<span style=\'color:brown !important;\'>'+Wtf.Payroll_Status_Renderer_AUTHORIZED+'</span>';
        }else if(val==4){
            var comment = record.data.comment;
            if(comment!=undefined && comment.trim()!=""){
                return '<div style=\'color:red !important;\'><div style="cursor:pointer" wtf:qtip=\"'+comment+'\">'+Wtf.Payroll_Status_Renderer_UNAUTHORIZED+" "+WtfGlobal.addCommentIcon(comment)+'</div></div>';
            } else {
                return '<span style=\'color:red !important;\'>'+Wtf.Payroll_Status_Renderer_UNAUTHORIZED+'</span>';
            }
            
        }else if(val==5){
            return '<span style=\'color:green !important;\'>'+Wtf.Payroll_Status_Renderer_PROCESSED_TRIAL+'</span>';
        }else if(val==6){
            return '<span style=\'color:green !important;\'>'+Wtf.Payroll_Status_Renderer_PROCESSED_FINAL+'</span>';
        }
    }
}

Wtf.GeneratePayrollProcessGrid = function(config){
    Wtf.GeneratePayrollProcessGrid.superclass.constructor.call(this,config);
    config.title=WtfGlobal.getLocaleText("hrms.payroll.generate.payroll.process");
};
Wtf.extend(Wtf.GeneratePayrollProcessGrid,Wtf.Panel,{
    onRender : function(config){
		Wtf.monthStore.loadData(Wtf.onceMonthRec);
        Wtf.GeneratePayrollProcessGrid.superclass.onRender.call(this,config);
        
        this.getAdvancedSearchComponent();

        this.generatedSalaryRecord = new Wtf.data.Record.create([{
        	name: 'id'
        },
        {
            name: 'accountno'
        },
        {
            name: 'resource'
        },
        {
            name: 'employeeid'
        },
        {
            name: 'fullname'
        },
        {
            name: 'costcenter'
        },
        {
            name: 'costcentername'
        },
        {
            name: 'jobtitle'
        },
        {
            name: 'jobtitlename'
        },
        {
            name: 'contract'
        },
        {
            name: 'absence'
        },
        {
            name: 'unpaidleavesAmount'
        },
        {
            name: 'incomeTax'
        },
        {
            name: 'actual'
        },
        {
            name:'employmentdate',
            type:'date'
        },
        {
            name:'contractenddate',
             type:'date'
        },
        {
            name:'difference'
        },
        {
            name:'status'
        },{
            name:'comment'
        }
        ]);

        var paybtns=this.getToolbarArray();
        var bbarbtns=this.getBottomToolbarButtons();
        
        this.generatedSalaryStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.generatedSalaryRecord),
            url : "Payroll/Date/getGeneratedPayrollList.py"
        });
        this.generatedSalaryStore.on('beforeload',function(store,option){
            option.params= option.params||{};
            option.params.sdate=this.startdate.getValue().format("Y-m-d");
            option.params.edate=this.enddate.getValue().format("Y-m-d");
            option.params.frequency=this.frequencyStoreCmb.getValue();
            option.params.status=this.statusComobBox.getValue();
        },this);


        calMsgBoxShow(202,4,true);
        this.generatedSalaryStore.load({
            scope:this,
            params:{
                start:0,
                limit:15
            }
        });
        this.generatedSalaryStore.on("load",function(){
            WtfGlobal.closeProgressbar();
        },this);
        
        this.rowNo=new Wtf.grid.RowNumberer();
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.gridcmodel= new Wtf.grid.ColumnModel([
            this.selectionModel,this.rowNo,
        {
            header:WtfGlobal.getLocaleText("hrms.payroll.resource"),
            dataIndex: 'resource',
            sortable: true,
            hidden:true,
            groupable: true
        },{
            header:WtfGlobal.getLocaleText("hrms.common.employee.id"),
            dataIndex: 'employeeid',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.FullName"),
            dataIndex: 'fullname',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.costcenter"),
            dataIndex: 'costcentername',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.designation"),
            dataIndex: 'jobtitlename',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Contract"),
            dataIndex: 'contract',
            hidden:true,
            sortable: true,
            align:'right',
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.absence"),
            dataIndex: 'absence',
            sortable: true,
            align:'right',
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.actual"),
            dataIndex: 'actual',
            hidden:true,
            scope:this,
            sortable: true,
            align:'right',
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.employment.date"),
            dataIndex: 'employmentdate',
            sortable: true,
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.contract.date"),
            dataIndex: 'contractenddate',
            hidden:true,
            scope:this,
            sortable: true,
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            groupable: true
        },{
            header: '<div wtf:qtip="'+WtfGlobal.getLocaleText("hrms.payroll.amount.difference.between.current.net.previous.salary")+'">'+WtfGlobal.getLocaleText("hrms.payroll.difference")+'</div>',
            dataIndex: 'difference',
            sortable: true,
            align:'center',
            renderer:function(val){
                if(val==1){
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                } else if(val==0){
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Details"),
            dataIndex: 'difference',
            sortable: true,
            align:'center',
            renderer:function(a,b,rec,d,e,f){
                var ac=a;
                if(rec.data.absence=="" ||rec.data.jobtitle==""){
                    if(rec.data.absence===0){
                        return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.recruitment.filled")+'</span>';
                    }else{
                        return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.recruitment.not.filled")+'</span>';
                    }

                }else{
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.recruitment.filled")+'</span>';
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.status"),
            dataIndex: 'status',
            scope:this,
            sortable: true,
            align:'center',
            groupable: true,
            renderer:Wtf.payrollStatusRenderer
        }],this);

        this.summary = new Wtf.ux.grid.GridSummary();
        this.generatedSalaryListGrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            border:false,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search.fullname"),
            searchField:"fullname",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer("No records to show")
            },
            store: this.generatedSalaryStore,
            displayInfo:true,
            cm: this.gridcmodel,
            scope:this,
            width:400,
            sm:this.selectionModel,
            tbar:paybtns,
            bbar:bbarbtns
        });

        this.generatedSalaryListGrid.getSelectionModel().on("selectionchange", function(){
        	var statusCombo = this.statusComobBox.getValue();
            var selCount = this.generatedSalaryListGrid.getSelectionModel().getCount();
            if(selCount==1){
                
        		this.userid = this.generatedSalaryListGrid.getSelectionModel().getSelected().data.resource;
        		this.historyid = this.generatedSalaryListGrid.getSelectionModel().getSelected().data.id;
        		
                if(statusCombo==1 ||statusCombo==2 ||statusCombo==3 ||statusCombo==4 ||statusCombo==5){
                    this.deleteEntry.setDisabled(false);
                }
                if(statusCombo!=0){
                    this.reviewingDetails.setDisabled(false);
                    this.payrolldata.setDisabled(false);
                }
                
                if(statusCombo==0 || statusCombo==1 || statusCombo==4){
                    this.newComponent.setDisabled(false);
                }
                if(statusCombo==1 || statusCombo==4){
                    this.calculate.setDisabled(false);
                    this.linkComponent.setDisabled(false);
                    this.zoom.setDisabled(false);
                }
                if(statusCombo==2){
                	this.linkComponent.setDisabled(false);
                }
                this.payrollComponentdata.setDisabled(false);
                
        	}else if(selCount>1){

                if(statusCombo==1 ||statusCombo==2 ||statusCombo==3 ||statusCombo==4 ||statusCombo==5){
                    this.deleteEntry.setDisabled(false);
                }
                this.payrolldata.setDisabled(true);
                this.reviewingDetails.setDisabled(true);
                this.newComponent.setDisabled(true);
                if(statusCombo==1){
                    this.calculate.setDisabled(false);
                }
                this.reviewingDetails.setDisabled(true);
                this.linkComponent.setDisabled(true);
                this.zoom.setDisabled(true);
                this.payrollComponentdata.setDisabled(true);
                
            }else{
                this.zoom.setDisabled(true);
                this.linkComponent.setDisabled(true);
                this.deleteEntry.setDisabled(true);
                this.payrolldata.setDisabled(true);
                this.reviewingDetails.setDisabled(true);
                this.newComponent.setDisabled(true);
                this.calculate.setDisabled(true);
                this.payrollComponentdata.setDisabled(true);
        	}
        }, this);
       
        this.pan= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[
            this.filterPanel
            ,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.generatedSalaryListGrid]
            }
            ]
        });
        this.add(this.pan);

        this.statusComobBox.on('select', function(combo,record,index){
            this.generatedSalaryStore.reload();
        },this);
    },

    getToolbarArray : function(){

        var btns=[];

        var menubtns=[];
        menubtns.push(
        new Wtf.Action({
            text:WtfGlobal.getLocaleText("hrms.payroll.previous.salary.components"),
            tooltip:{
                text:WtfGlobal.getLocaleText("hrms.payroll.click.assign.component.settings.allocated.previous.month")
            },
            iconCls:"pwndHRMS assignComponentIcon",
            scope:this,
            handler : function(){
                
                this.assignComponentToEmployee("PreviousSettings");
                
            }
        }));

        menubtns.push(
        new Wtf.Action({
            text:WtfGlobal.getLocaleText("hrms.payroll.default.components"),
            tooltip:{
                text:WtfGlobal.getLocaleText("hrms.payroll.click.assign.default.component.settings.user.administration")
            },
            iconCls:"pwndHRMS assignComponentIcon",
            scope:this,
            handler : function(){

                this.assignComponentToEmployee("DefaultSettings");

            }
        }));

        this.newComponent=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.assign.component"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS assignComponentIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.assign.components.to.selected.employee"),
            menu:menubtns
        });

        btns.push(this.newComponent);
        btns.push('-');

        this.deleteEntry=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            scope:this,
            disabled:true,
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.delete.generated.authorized.processed.payroll.entries"),
            handler : this.deleteGeneratePayroll
        });

        btns.push(this.deleteEntry);
        btns.push('-');

        this.calculate=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.calculate"),
            scope:this,
            disabled:true,
            iconCls:"pwndPrint calculateIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.calculate.payroll.selected.employee"),
            handler : this.calculatePayroll
        });

        btns.push(this.calculate);
        btns.push('-');

        this.linkComponent=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.manage.amount"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS manageAmountIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.update.amount.components"),
            handler : function(){
                var userid="";
                var emparr=this.generatedSalaryListGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.select.single.record.manage.amount")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.select.record(s).manage.amount")],0);
                    return;
                }
                if(emparr[0].get('status')>2 && emparr[0].get('status')!=4){
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.salary.already.authorized.selected.employee")],1);
                    return;
                }
                userid=emparr[0].get('resource');
                var absence=emparr[0].get('absence');
                var historyid = emparr[0].get('id');
                var status = emparr[0].get("status")==0?true:false;

                this.linkComponentWin=new Wtf.linkComponentWin({
                    iconCls:getButtonIconCls(Wtf.btype.winicon),
                    layout:'fit',
                    closable:true,
                    width:760,
                    title:WtfGlobal.getLocaleText("hrms.payroll.manage.amount"),
                    height:500,
                    border:false,
                    empGDS:this.generatedSalaryStore,
                    modal:true,
                    userid:userid,
                    startdate:this.startdate.getValue().format("Y-m-d"),
                    enddate:this.enddate.getValue().format("Y-m-d"),
                    frequency:this.frequencyStoreCmb.getValue(),
                    absence:absence,
                    historyid:historyid,
                    scope:this,
                    plain:true,
                    status:status,
                    allempGrid:this.generatedSalaryListGrid
                });
                this.linkComponentWin.show();
            }
        });

        btns.push(this.linkComponent);
        btns.push('-');


        this.zoom=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.manage.employee.details"),
            scope:this,
            disabled:true,
            iconCls:"pwndPrint empDetailsIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.manage.employee.details"),
            handler : function(){

                var emparr=this.generatedSalaryListGrid.getSelectionModel().getSelections();
                if(emparr.length>1){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.single.record.manage.employee.details")],0);
                    return;
                }
                if(emparr.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.manage.employee.details")],0);
                    return;
                }
//                if(this.rec.data.status==0){
//                   msgBoxShow(["Alert","Payroll for selected employee is not started yet. Please link component first for the selected employee."],1);
//                   return;
//                }
//                if(this.rec.data.status>1){
//                   msgBoxShow(["Alert","Salary is already calculated for the selected employee."],1);
//                   return;
//                }

                var cu=new Wtf.PayrollResourceGrid({
        			record: emparr[0],
                    sdate :this.startdate.getValue().format("Y-m-d"),
                    edate :this.enddate.getValue().format("Y-m-d"),
                    frequency:this.frequencyStoreCmb.getValue()
        		});
        		cu.on('save',function(){

        			this.generatedSalaryStore.load({
        	            scope:this,
        	            params:{
        	                start:0,
        	                limit:15
        	            }
        	        });
        		}, this);
        		cu.on('notsave',this.genFailureResponse, this);
            }
        });

        btns.push(this.zoom);
        btns.push('-');

        this.reviewingDetails=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.review.details"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS viewbuttonIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.review.salary.details.selected.employee"),
            handler : function(){
                var recData = this.generatedSalaryListGrid.getSelectionModel().getSelected().data

                var userID = recData.resource;
        		var historyID = recData.id;
                var empname = recData.fullname;
                var accountno = recData.accountno;
                var unpaidleavesAmount = recData.unpaidleavesAmount;
                var absence = recData.absence;

                this.mainTabId = Wtf.getCmp("as");
                var payslipmodule = Wtf.getCmp(userID+"reviewingdetail"+this.startdate.getValue().format("Y-m-d"));
                if(payslipmodule == null){
                    payslipmodule = new Wtf.resourcePayslip({
                        layout:"fit",
                        title:WtfGlobal.getLocaleText({key:"hrms.payroll.s.pay.details",params:[empname]}),
                        closable:true,
                        border:false,
                        id:userID+"reviewingdetail"+this.startdate.getValue().format("Y-m-d"),
                        iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                        userid:userID,
                        historyid:historyID,
                        startdate:this.startdate.getValue().format("Y-m-d"),
                        enddate:this.enddate.getValue().format("Y-m-d"),
                        ename:empname,
                        accno:accountno,
                        reviewPayrollFlag:true,
                        generatedSalaryListGrid:this.generatedSalaryListGrid,
                        generatedSalaryStore:this.generatedSalaryStore,
                        frequency:this.frequencyStoreCmb.getValue(),
                        payrollStatus:recData.status,
                        unpaidleavesAmount:unpaidleavesAmount,
                        incomeTax:recData.incomeTax,
                        absence:absence
                    });
                    this.mainTabId.add(payslipmodule);
                    payslipmodule.on('gridload',function(){
                        calMsgBoxShow(202,4,true);
                        this.empstore.load();
                    },this);
                }
                this.mainTabId.setActiveTab(payslipmodule);
                this.mainTabId.doLayout();
            }
        });

        btns.push(this.reviewingDetails);
        btns.push('-');
        
        this.paydetails=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.export.Export"),
            scope:this,
            iconCls:'pwndExport export',
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.click.export.salary.details.csv.format"),
            handler:this.ExportData
        });

        btns.push(this.paydetails);
        
       
        btns.push('->');
        btns.push(WtfGlobal.getLocaleText("hrms.common.select.status"));

        this.statusComobBox = Wtf.payrollStatusCombobox("Generate");
        
        btns.push(this.statusComobBox);

        return btns;
        
    },
    assignComponentToEmployee : function (type){

        var userid="";
        var emparr=this.generatedSalaryListGrid.getSelectionModel().getSelections();
        if(emparr.length>1){
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.select.single.record.assign.components")],0);
            return;
        }
        if(emparr.length==0){
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.select.record.assign.components")],0);
            return;
        }
        userid=emparr[0].get('resource');
        var rec = emparr[0];

        calMsgBoxShow(202,4,true);
        Wtf.Ajax.requestEx({
            url: "Payroll/Date/getCheckForFilledDeclarationForm.py",
            params: {
                userid:userid,
                enddate:this.enddate.getValue().format("Y-m-d"),
                frequency:this.frequencyStoreCmb.getValue()
            }
        }, this,
        function(response){

            Wtf.updateProgress();
            if(!response.checkForDeclarationForm){
                
                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.tax.declaration.form.not.filled.selected.employee"), function(btn){
                
                if(btn!="yes") return;

                    if(type=="PreviousSettings"){
                       
                        this.getPreviousSalaryComponent(userid, rec);
                        
                    } else if(type=="DefaultSettings"){

                        this.getDefaultSalaryComponent(userid, rec);
                    }
                

                },this);
            } else {

                if(type=="PreviousSettings"){

                   this.getPreviousSalaryComponent(userid, rec);
                    
                } else if(type=="DefaultSettings"){
                    
                    this.getDefaultSalaryComponent(userid, rec);
                }
            }
           

        },
        function(response)
        {
            calMsgBoxShow(27,1);

        });

    },
    getPreviousSalaryComponent : function(userid, rec){

        this.compWindow=new Wtf.assignComponentWin({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            closable:true,
            width:860,
            title:WtfGlobal.getLocaleText("hrms.payroll.previous.salary.components"),
            height:600,
            border:false,
            empGDS:this.generatedSalaryStore,
            modal:true,
            userid:userid,
            scope:this,
            plain:true,
            rec:rec,
            generatePayrollLink:true,
            previousSalaryFlag:true,
            allempGrid:this.generatedSalaryListGrid,
            startdate:this.startdate.getValue().format("Y-m-d"),
            enddate:this.enddate.getValue().format("Y-m-d"),
            frequency:this.frequencyStoreCmb.getValue()
        });
        this.compWindow.show();
        
    },

    getDefaultSalaryComponent : function(userid, rec){

        this.compWindow=new Wtf.assignComponentWin({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            closable:true,
            width:860,
            title:WtfGlobal.getLocaleText("hrms.payroll.default.components"),
            height:600,
            border:false,
            empGDS:this.generatedSalaryStore,
            modal:true,
            userid:userid,
            scope:this,
            plain:true,
            rec:rec,
            generatePayrollLink:true,
            allempGrid:this.generatedSalaryListGrid,
            startdate:this.startdate.getValue().format("Y-m-d"),
            enddate:this.enddate.getValue().format("Y-m-d"),
            frequency:this.frequencyStoreCmb.getValue()
        });
        this.compWindow.show();
        
    },
    filterHandler: function(){
    	if(this.generatedSalaryStore!=undefined){
    		this.generatedSalaryStore.load({
    			scope:this,
    			params:{
                	start:0,
                	limit:this.generatedSalaryListGrid.pag.pageSize
            	}
    		});
    	}
    },

    getBottomToolbarButtons :function(){

         var bbtns =[];
         this.payrollComponentdata=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.components"),
            scope:this,
            disabled:true,
            iconCls:"pwndCommon reportbuttonIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.view.payroll.components.selected.employee"),
            handler : function(){

                    var rec = this.generatedSalaryListGrid.getSelectionModel().getSelected();
                    var empid = rec.data.resource;
                    var main=Wtf.getCmp("as");
                    var employeePayrollDataComponent=Wtf.getCmp("Employee_Payroll_Data"+empid);
                    if(employeePayrollDataComponent==null)
                    {
                        employeePayrollDataComponent=new Wtf.PayrollComponentDataGrid({
                            id:"Employee_Payroll_Data"+empid,
                            layout: 'fit',
                            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.components")+"'>"+WtfGlobal.getLocaleText({key:"hrms.payroll.s.components",params:[rec.data.fullname]})+"</div>",
                            border: false,
                            closable: true,
                            startdate:this.startdate.getValue().format("Y-m-d"),
                            enddate:this.enddate.getValue().format("Y-m-d"),
                            frequency:this.frequencyStoreCmb.getValue(),
                            year:this.yearCmb.getValue(),
                            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip),
                            empid:empid
                        });
                        main.add(employeePayrollDataComponent);
                    }
                    main.setActiveTab(employeePayrollDataComponent);

                    Wtf.getCmp("as").doLayout();
            }
        });

        bbtns.push(this.payrollComponentdata);
        bbtns.push('-');

        this.payrolldata=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.payroll.history"),
            scope:this,
            disabled:true,
            iconCls:"pwndPrint historyIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.view.payroll.history.selected.employee"),
            handler : function(){

                    var rec = this.generatedSalaryListGrid.getSelectionModel().getSelected();
                    if(rec.data.status==0){
                        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.payroll.selected.employee.not.started.yet.link.component")],1);
                        return;
                    }
                    var empid = rec.data.resource;
                    var main=Wtf.getCmp("as");
                    var employeePayrollDataComponent=Wtf.getCmp("GeneratePayrollProcessGridID"+empid);
                    if(employeePayrollDataComponent==null)
                    {
                        employeePayrollDataComponent=new Wtf.EmployeePayrollDataGrid({
                            id:"GeneratePayrollProcessGridID"+empid,
                            layout: 'fit',
                            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.payroll.history")+"'>"+WtfGlobal.getLocaleText({key:"hrms.payroll.s.payroll.history",params:[rec.data.fullname]})+"'</div>",
                            border: false,
                            closable: true,
                            startdate:this.startdate.getValue().format("Y-m-d"),
                            enddate:this.enddate.getValue().format("Y-m-d"),
                            frequency:this.frequencyStoreCmb.getValue(),
                            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip),
                            empid:empid
                        });
                        main.add(employeePayrollDataComponent);
                    }
                    main.setActiveTab(employeePayrollDataComponent);

                    Wtf.getCmp("as").doLayout();
            }
        });

        bbtns.push(this.payrolldata);

          this.debbugger=new Wtf.Button({
            text:'Debug Data',
            scope:this,
            iconCls:'pwndExport export',
            tooltip:"Click to show debug data",
            handler:function(){

                    var sel = this.generatedSalaryListGrid.getSelectionModel().selections;

                    if(sel.length!=1){
                        alert("Please select single record");
                        return;
                    }
                    var recData = this.generatedSalaryListGrid.getSelectionModel().getSelected().data

                    var userID = recData.resource;

                    Wtf.Ajax.requestEx({
                        url:"Payroll/Date/getIncomeTaxDataForDebug.py",
                        params:{
                            frequency:this.frequencyStoreCmb.getValue(),
                            enddate:this.enddate.getValue().format("Y-m-d"),
                            userid:userID
                        }
                    },
                    this,
                    function (r){
                        
                        if(r.success==true){
                            alert(" Earning :"+r.earning+"\n  Y :"+r.Y+"\n  K :"+r.K+"\n Y1BeforeBIK :"+r.Y1BeforeBIK+"\n  Y1 :"+r.Y1+"\n  K1 :"+r.K1+"\n  Yt :"+r.Yt+"\n  Kt :"+r.Kt+"\n  Y2 :"+r.Y2+"\n  K2 :"+r.K2+" \n  P :"+r.P+"\n Additional Renumeration:"+r.additionalrenum+"\n Current BIK:"+r.currentbik+"\n Current Comp, Opt and Allowances:"+r.currentcompoptandallowanceDeduction+"\n Current MTD :"+r.currentmtd+"\n Current Zakat :"+r.currentzakat+"\n Total Deduction:"+r.deduction+"\n Paid Income Tax:"+r.paidincometax+"\n Paid Other Deduction:"+r.paidotherdeduction+"\n Paid Zakat:"+r.paidzakat+"\n <b>Net MTD</b> :"+r.netmtd)
                        }
                    },
                    function (){
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"),"Some error occured while unauthorizing."],2);
                    });

            }
        });

     //   bbtns.push(this.debbugger);
         
        return bbtns;
    },

    getAdvancedSearchComponent: function(){

        this.monthCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.period"),
            hiddenName: 'period',
            forceSelection:true,
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.monthStore,
            width:150,
            typeAhead:true,
            value:new Date().getMonth()+1
        });

        this.startdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.start.date"),
            format:"Y-m-d",
            name:'joindate',
            disabled:true,
            width:200
        });
        this.enddate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.end.date"),
            format:"Y-m-d",
            disabled:true,
            name:'confirmdate',
            width:200
        });

        this.yearCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Year"),
            hiddenName: 'year',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:Wtf.yearStore,
            width:150,
            typeAhead:true,
            value:new Date().getFullYear()
        });


        this.frequencyStoreCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.frequency"),
            hiddenName: 'frequency',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:Wtf.frequencyStore,
            width:150,
            typeAhead:true,
            value:0
        });

        this.etrydate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.entry.date"),
            format:"Y-m-d",
            name:'entrydate',
            width:200
        });

        this.applyFilter = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.filter"),
            scope:this,
            iconCls:"pwndExport filter",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.filter"),
            handler : this.filterHandler
        });
        
        this.frequencyStoreCmb.on("select", function(a, b, c){
        	var i = eval(b.data.id);
        	if(i == 0){
        		Wtf.monthStore.loadData(Wtf.onceMonthRec);
        		this.monthCmb.setValue("1");
        	}else if(i == 1){
        		Wtf.monthStore.loadData(Wtf.onceWeekRec);
        		this.monthCmb.setValue("1");
        	}else{
        		Wtf.monthStore.loadData(Wtf.twiceMonthRec);
        		this.monthCmb.setValue("1");
        	}
        	this.setDatevalue();
        }, this);

//        this.closeFilter = new Wtf.Button({
//            text:'Close',
//            scope:this,
//            iconCls:"pwndCommon cancelbuttonIcon",
//            tooltip :"Close",
//            handler : function(){
//               this.filterPanel.hide();
//               this.advancedSearch.enable();
//               this.pan.doLayout();
//            }
//        });

        this.filterPanel  = new Wtf.form.FormPanel({
            autoScroll:true,
            hidden:true,
            border:false,
            layout:'fit',
            region:'north',
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 25px;',
            height:170,
//            tbar:['->',this.applyFilter ],
            items :[{
                border: false,
                defaults: {
                    border: false,
                    xtype: "fieldset",
                    autoHeight: true
                },
                items: [{
                    title: WtfGlobal.getLocaleText("hrms.common.advanced.search"),
                    layout:'column',
                    items: [
                        {
                            columnWidth: '.50',
                            layout : 'form',
                            border : false,
                            labelWidth: 150,
                            defaults:{anchor:'80%'},
                            items : [this.monthCmb,this.startdate,this.enddate]
                        },
                        {
                             columnWidth: '.50',
                             layout : 'form',
                             defaults:{anchor:'80%'},
                             border : false,
                             labelWidth: 150,
                             items : [this.yearCmb,this.frequencyStoreCmb,this.etrydate]
                        }
                    ]

                }]
            }]
        });

        this.filterPanel.show();
        this.monthCmb.on("select",function(combo,b,index){
            this.setDatevalue();
        },this);
        this.yearCmb.on("select",function(combo,b,index){
            this.setDatevalue();
        },this);

        this.filterPanel.on("show",function(){
            this.setDatevalue();
        },this);
        this.setDatevalue();
    },
    
    setDatevalue:function(){
    	var obj = Wtf.PayrollSetDatevalue(this);
        this.startdate.setValue(obj.startdt);
        this.enddate.setValue(obj.enddt);
        this.filterHandler();
    },
    
    deleteGeneratePayroll:function(){

        var emparr=this.generatedSalaryListGrid.getSelectionModel().getSelections();
        if(emparr.length==0){
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.delete")],0);
            return;
        }
        var empids=[];
        for(var i=0;i<emparr.length;i++){
            if(emparr[i].get("status")<=5){
                empids.push(emparr[i].get("resource"));
            }
        }
//      var htmlString="Do you want to delete salary for selected employee(s)?\n\
//                      <br><br><form><input type='checkbox' name='deletecomponent' value='delete' /> Check to delete linked components of selected employee(s) for current period<br /></form> ";
        
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.want.delete.salary.selected.employee"), function(btn){
            if(btn!="yes") return;

            var deleteComponentResourceMapping = true;//document.getElementsByName('deletecomponent')[0].checked;
            
            calMsgBoxShow(201,4,true);
            Wtf.Ajax.requestEx({
                url: "Payroll/Date/deleteResourcePayrollData.py",
                params: {
                    empids:empids,
                    startdate :this.startdate.getValue().format("Y-m-d"),
                    enddate :this.enddate.getValue().format("Y-m-d"),
                    frequency:this.frequencyStoreCmb.getValue(),
                    deleteComponentResourceMapping:deleteComponentResourceMapping
                }
            }, this,
            function(response){
                this.generatedSalaryStore.reload();
                Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.salary.deleted.successfully"));
            },
            function(response)
            {
                calMsgBoxShow(229,1);
            });
        },this);
    },
    
    calculatePayroll:function(){
        
        var emparr=this.generatedSalaryListGrid.getSelectionModel().getSelections();
        if(emparr.length==0){
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.to.calculate")],0);
            return;
        }
        var validate = this.validateCalculate(emparr);
        if(!validate){
            return;
        }
        
        var empids=[];
        var jsondata = "";
        
        for(var i=0;i<emparr.length;i++){

            empids.push(emparr[i].get("resource"));

            jsondata += "{'fullname':'" + emparr[i].get("fullname") + "',";
            jsondata += "'historyid':'" + emparr[i].get("id") + "',";
            jsondata += "'jobtitle':'" + emparr[i].get("jobtitle") + "',";
            jsondata += "'userid':'" + emparr[i].get("resource") + "',";
            jsondata += "'absence':'" + emparr[i].get("absence") + "',";
            jsondata += "'paycyclestartdate':'" + this.startdate.getValue().format("Y-m-d") + "',";
            jsondata += "'paycycleenddate':'" + this.enddate.getValue().format("Y-m-d") + "',";
            jsondata += "'frequency':'" + this.frequencyStoreCmb.getValue() + "',";
            jsondata += "'salarystatus':'2',";
            jsondata += "'costcenter':'" + emparr[i].get("costcenter") + "',";
            jsondata += "'employeeid':'" + emparr[i].get("employeeid") + "'},";

        }

        var finalStr = jsondata.substr(0,jsondata.length - 1);
        finalStr = "["+finalStr+"]";
        
        var htmlMsg = WtfGlobal.getLocaleText("hrms.payroll.want.to.calculate.salary.selected.resources");

         Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), htmlMsg,function(btn){
            if(btn!="yes") return;
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
                url: "Payroll/Date/calculatePayroll.py",
                params: {
                    jsonarray:finalStr,
                    empids:empids
                }
            }, this,
            function(response){
            	if(response.validationJson.length==0){
            		this.generatedSalaryStore.reload();
                    Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.salary.calculated.successfully"));
            	}else{
            		var error = "<P align='left'><br/>"+WtfGlobal.getLocaleText("hrms.payroll.previous.salary.following.employee.pending")+"<br/ >";
            		for(var i=0; i<response.validationJson.length; i++){
            			error += ("<br/><b>"+(i+1)+"."+response.validationJson[i].username+"</b>");
            		}
            		error += "<br/><br/>"+WtfGlobal.getLocaleText("hrms.payroll.to.generate.current.salary.previous.salary.status.processed.final")+"</P>";
            		
            		Wtf.MessageBox.show({
                        title: WtfGlobal.getLocaleText("hrms.common.error"),
                        msg: error,
                        buttons: Wtf.MessageBox.OK,
                        animEl: 'upbtn',
                        icon: Wtf.MessageBox.ERROR,
                        scope:this
                   });
            	}
            },
            function(response)
            {
                calMsgBoxShow(229,1);
            })
        },this);
        
    },

    validateCalculate: function(emparr){
        var validFlag=false;
        var name="";
        var essentialDetails="";
        for(var i=0;i<emparr.length;i++){

             if(emparr[i].get("jobtitle")=="" || emparr[i].get("absence")==""){

                if(emparr[i].get("absence")===0){


                }else{
                    essentialDetails +="<br>"+emparr[i].get("fullname")+",";
                   
                }

            }

            if(emparr[i].get("employmentdate")==""){

                name +="<br>"+emparr[i].get("fullname")+",";
                
            }

        }
        
        if(name!=""){
            name = name.substr(0,(name.length-1));
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.enter.date.joining.employee")+"<br>"+name],1);
            validFlag=false;
        } else if(essentialDetails!=""){
            essentialDetails = essentialDetails.substr(0,(essentialDetails.length-1));
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"),WtfGlobal.getLocaleText("hrms.payroll.fill.essential.fields.employee.before.calculating.payroll")+"</br>"+essentialDetails],1);
            validFlag=false;
        }else {
            validFlag=true;
        }
        

        return validFlag;
        
    },
    
    ExportData:function(){
    	var url = "Payroll/Salary/exportPayDetails.py?"+Wtf.urlEncode(Wtf.urlDecode("startdate="+this.startdate.getValue().format("Y-m-d")+"&enddate="+this.enddate.getValue().format("Y-m-d")+"&frequency="+this.frequencyStoreCmb.getValue()+"&module=Generate"+"&status="+this.statusComobBox.getValue()));
    	Wtf.get('downloadframe').dom.src = url;
    }
});

/**
 * Component for - (Authorize - Employee details)
 */

Wtf.authorizePayrollProcessGrid = function(config){
    Wtf.authorizePayrollProcessGrid.superclass.constructor.call(this,config);
    config.title=WtfGlobal.getLocaleText("hrms.payroll.authorize.payroll.process");
};
Wtf.extend(Wtf.authorizePayrollProcessGrid,Wtf.Panel,{
    onRender : function(config){
		Wtf.monthStore.loadData(Wtf.onceMonthRec);
        Wtf.authorizePayrollProcessGrid.superclass.onRender.call(this,config);
        
        this.getAdvancedSearchComponent();
        var tbarbtns=this.getToolbarArray();
        var bbarbtns=this.getBottomToolbarButtons();

        this.generatedSalaryRecord = new Wtf.data.Record.create([
        {
            name: 'payhistoryid'
        },{
            name: 'resource'
        },{
            name: 'employeeid'
        },
        {
            name: 'fullname'
        },
        {
            name: 'costcenter'
        },
        {
            name: 'costcentername'
        },
        {
            name: 'jobtitle'
        },
        {
            name: 'jobtitlename'
        },
        {
            name: 'netsalary'
        },
        {
            name:'employmentdate',
            type:'date'
        },
        {
            name:'contractenddate',
             type:'date'
        },
        {
            name:'difference'
        },
        {
            name:'status'
        },
        {
            name:'absence'
        },
        {
            name:'accountno'
        },
        {
            name:'unpaidleavesAmount'
        },
        {
            name: 'incomeTax'
        },
        {
            name:'comment'
        }]);

       
        this.generatedSalaryStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.generatedSalaryRecord),
            baseParams:{
                type:Wtf.Payroll_Date_Authorize
            },
            url: "Payroll/Date/getPayrollHistory.py"
        });

        this.generatedSalaryStore.on('beforeload',function(store,option){
            option.params= option.params||{};
            option.params.sdate=this.startdate.getValue().format("Y-m-d");
            option.params.edate=this.enddate.getValue().format("Y-m-d");
            option.params.frequency=this.frequencyStoreCmb.getValue();
            option.params.status=this.statusComobBox.getValue();
        },this);

        calMsgBoxShow(202,4,true);
        this.generatedSalaryStore.load({
            scope:this,
            params:{
                start:0,
                limit:15
            }
        });
        this.generatedSalaryStore.on("load",function(){
            WtfGlobal.closeProgressbar();
        },this);
        
        this.rowNo=new Wtf.grid.RowNumberer();
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
     
        this.gridcmodel= new Wtf.grid.ColumnModel([
        this.selectionModel,this.rowNo,
        {
            dataIndex: 'payhistoryid',
            hidden: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.resource"),
            dataIndex: 'employeeid',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.FullName"),
            dataIndex: 'fullname',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.costcenter"),
            dataIndex: 'costcentername',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.designation"),
            dataIndex: 'jobtitlename',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.netsalary"),
            dataIndex: 'netsalary',
            scope:this,
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
            }
        },{
            header: '<div wtf:qtip="'+WtfGlobal.getLocaleText("hrms.payroll.amount.difference.between.current.net.previous.salary")+'">'+WtfGlobal.getLocaleText("hrms.payroll.difference")+'</div>',
            dataIndex: 'difference',
            sortable: true,
            align:'right',
            renderer:function(val){
                var value = parseFloat(val).toFixed(2);
                if(value<0){
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.currencyRenderer(value)+'</span>';
                } else {
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.currencyRenderer(value)+'</span>';
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.employment.date"),
            dataIndex: 'employmentdate',
            sortable: true,
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.contract.date"),
            dataIndex: 'contractenddate',
            scope:this,
            sortable: true,
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.status"),
            dataIndex: 'status',
            scope:this,
            sortable: true,
            align:'center',
            groupable: true,
            renderer:Wtf.payrollStatusRenderer
        }],this);

        this.summary = new Wtf.ux.grid.GridSummary();
        this.generatedSalaryListGrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            border:false,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search.fullname"),
            searchField:"fullname",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.AuditTrail.Norecordstodisplay"))
            },
            store: this.generatedSalaryStore,
            displayInfo:true,
            cm: this.gridcmodel,
            scope:this,
            width:400,
            sm: this.selectionModel,
            tbar:tbarbtns,
            bbar:bbarbtns
        });
       
        this.pan= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[
            this.filterPanel
            ,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.generatedSalaryListGrid]
            }
            ]
        });
        this.add(this.pan);

        this.generatedSalaryListGrid.getSelectionModel().on("selectionchange", function(){
        	var statusCombo = this.statusComobBox.getValue();
            var selCount = this.generatedSalaryListGrid.getSelectionModel().getCount();
            if(selCount==1){
        		this.userid = this.generatedSalaryListGrid.getSelectionModel().getSelected().data.resource;
        		this.historyid = this.generatedSalaryListGrid.getSelectionModel().getSelected().data.id;
                this.reviewingDetails.setDisabled(false);
                if(statusCombo==2){
                    this.authBttn.setDisabled(false);
                    this.unauthorizeButton.setDisabled(false);
                } else if(statusCombo==3){
                	this.unauthorizeButton.setDisabled(false);
        		}
           }else if(selCount>1){
                this.reviewingDetails.setDisabled(true);
                if(statusCombo==2){
                    this.authBttn.setDisabled(false);
                    this.unauthorizeButton.setDisabled(false);
                } else if(statusCombo==3){
                	this.unauthorizeButton.setDisabled(false);
        		}
           }else{
                this.reviewingDetails.setDisabled(true);
                this.authBttn.setDisabled(true);
                this.unauthorizeButton.setDisabled(true);
           }
        }, this);

        this.statusComobBox.on('select', function(combo,record,index){
            this.generatedSalaryStore.reload();
        },this);
    },
    
    getToolbarArray : function(){
        var btns=[];        

         this.authBttn=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.authorize"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS authorizeComponentIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.authorize.payroll.selected.employee"),
            handler : function(){
                var historyid=[];
                var sel = this.selectionModel.selections;
                if(sel.length==0){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.authorized.salary")],0);
                    return;
                }
                    
               var htmlString=WtfGlobal.getLocaleText("hrms.payroll.want.to.authorize.selected.employee.salary");
       
                for(var i=0;i<sel.length;i++){
                    if(sel.items[i].get("status")==2){
                        historyid.push(sel.items[i].get("payhistoryid"));
                    }
                }
                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), htmlString,function(btn){
                    if(btn!="yes") return;
                    calMsgBoxShow(200,4,true);
                    Wtf.Ajax.requestEx({
                        url:"Payroll/Date/updatePayrollHistory.py",
                        params:{
                            historyid:historyid,
                            statusid:3
                        }
                    },
                    this,
                    function (response){
                        var res=eval('('+response+')');
                        if(res.success==false){
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.dependency.exists.for.component")],2);
                        } else {
                            Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.salary.authorized.successfully"));
                            this.generatedSalaryStore.reload();
                        }
                    },function (){
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.some.error.authorizing")],2);
                    });
                },this);
            }
        });

        btns.push(this.authBttn);
        
        this.unauthorizeButton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.unauthorize"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS authorizeComponentIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.unauthorize.payroll.selected.employee"),
            handler : function(){

                var sm = this.generatedSalaryListGrid.getSelectionModel();
                var emparr=sm.getSelections();
                if(sm.getCount()<1){
                   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.employee.to.unauthorize.payroll")],0);
                   return;
                }

                this.empSelected =new Wtf.data.Store(this.generatedSalaryListGrid.getStore().initialConfig);
                this.empSelected.add(emparr);

                this.salStatusWindow=new Wtf.UnautorizeSalaryWin({
                    iconCls:getButtonIconCls(Wtf.btype.winicon),
                    layout:'fit',
                    closable:true,
                    width:700,
                    title:WtfGlobal.getLocaleText("hrms.payroll.unauthorize.payroll"),
                    height:500,
                    border:false,
                    empGDS:this.empSelected,
                    modal:true,
                    scope:this,
                    grid:this.generatedSalaryListGrid,
                    plain:true
                });
                this.salStatusWindow.show();
            }
        });

        btns.push(this.unauthorizeButton);
        btns.push('-');

        this.reviewingDetails=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.review.details"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS viewbuttonIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.review.salary.details.selected.employee"),
            handler : function(){
                var recData = this.generatedSalaryListGrid.getSelectionModel().getSelected().data

                var userID = recData.resource;
        		var historyID = recData.payhistoryid;
                var empname = recData.fullname;
                var accountno = recData.accountno;
                var unpaidleavesAmount = recData.unpaidleavesAmount;
                var absence = recData.absence;

                this.mainTabId = Wtf.getCmp("as");
                this.payslip = Wtf.getCmp(userID+"reviewingdetailforAuthorization"+this.startdate.getValue().format("Y-m-d"));
                if(this.payslip == null){
                    this.payslip = new Wtf.resourcePayslip({
                        layout:"fit",
                        title:WtfGlobal.getLocaleText({key:"hrms.payroll.s.pay.details",params:[empname]}),
                        closable:true,
                        id:userID+"reviewingdetailforAuthorization"+this.startdate.getValue().format("Y-m-d"),
                        border:false,
                        iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                        userid:userID,
                        historyid:historyID,
                        startdate:this.startdate.getValue().format("Y-m-d"),
                        enddate:this.enddate.getValue().format("Y-m-d"),
                        ename:empname,
                        accno:accountno,
                        reviewPayrollFlag:true,
                        generatedSalaryListGrid:this.generatedSalaryListGrid,
                        generatedSalaryStore:this.generatedSalaryStore,
                        frequency:this.frequencyStoreCmb.getValue(),
                        payrollStatus:recData.status,
                        unpaidleavesAmount:unpaidleavesAmount,
                        incomeTax:recData.incomeTax,
                        absence:absence
                    });
                    this.mainTabId.add(this.payslip);
                    this.payslip.on('gridload',function(){
                        calMsgBoxShow(202,4,true);
                        this.empstore.load();
                    },this);
                }
                this.mainTabId.setActiveTab(this.payslip);
                this.mainTabId.doLayout();
            }
        });

        btns.push(this.reviewingDetails);
        btns.push('-');

        this.paydetails=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.export.Export"),
            scope:this,
            iconCls:'pwndExport export',
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.click.export.salary.details.csv.format"),
            handler:this.ExportData
        });

        btns.push(this.paydetails);
        btns.push("->");
        btns.push(WtfGlobal.getLocaleText("hrms.common.select.status"));
        this.statusComobBox = Wtf.payrollStatusCombobox("Authorize");

        btns.push(this.statusComobBox);

       return btns;        
    },

    filterHandler: function(){
    	if(this.generatedSalaryStore!=undefined){
    		this.generatedSalaryStore.load({
    			scope:this,
    			params:{
                	start:0,
                	limit:this.generatedSalaryListGrid.pag.pageSize
            	}
    		});
    	}
    },
    
    getBottomToolbarButtons :function(){

        var bbtns =[];

        return bbtns;
    },
    ExportData:function(){
    	var url = "Payroll/Salary/exportPayDetails.py?"+Wtf.urlEncode(Wtf.urlDecode("startdate="+this.startdate.getValue().format("Y-m-d")+"&enddate="+this.enddate.getValue().format("Y-m-d")+"&frequency="+this.frequencyStoreCmb.getValue()+"&module="+Wtf.Payroll_Date_Authorize+"&status="+this.statusComobBox.getValue()));
    	Wtf.get('downloadframe').dom.src = url;
    },
    getAdvancedSearchComponent: function(){

        this.monthCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.period"),
            hiddenName: 'period',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.monthStore,
            width:150,
            typeAhead:true,
            value:new Date().getMonth()+1
        });

        this.startdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.start.date"),
            format:"Y-m-d",
            name:'joindate',
            disabled:true,
            width:200
        });
        this.enddate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.end.date"),
            format:"Y-m-d",
            disabled:true,
            name:'confirmdate',
            width:200
        });

        this.yearCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Year"),
            hiddenName: 'year',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.yearStore,
            width:150,
            typeAhead:true,
            value:new Date().getFullYear()
        });

        this.frequencyStoreCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.frequency"),
            hiddenName: 'frequency',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.frequencyStore,
            width:150,
            typeAhead:true,
            value:0
        });
        
        this.frequencyStoreCmb.on("select", function(a, b, c){
        	var i = eval(b.data.id);
        	if(i == 0){
        		Wtf.monthStore.loadData(Wtf.onceMonthRec);
        		this.monthCmb.setValue("1");
        	}else if(i == 1){
        		Wtf.monthStore.loadData(Wtf.onceWeekRec);
        		this.monthCmb.setValue("1");
        	}else{
        		Wtf.monthStore.loadData(Wtf.twiceMonthRec);
        		this.monthCmb.setValue("1");
        	}
        	this.setDatevalue();
        }, this);

        this.etrydate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.entry.date"),
            format:"Y-m-d",
            name:'entrydate',
            width:200
        });

        this.applyFilter = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.filter"),
            scope:this,
            iconCls:"pwndExport filter",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.filter"),
            handler :this.filterHandler
                });


        this.filterPanel  = new Wtf.form.FormPanel({
            autoScroll:true,
           // hidden:true,
            border:false,
            layout:'fit',
            region:'north',
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 25px;',
            height:170,
//            tbar:['->',this.applyFilter ],
            items :[{
                border: false,
                defaults: {
                    border: false,
                    xtype: "fieldset",
                    autoHeight: true
                },
                items: [{
                    title: WtfGlobal.getLocaleText("hrms.common.advanced.search"),
                    layout:'column',
                    items: [
                        {
                            columnWidth: '.50',
                            layout : 'form',
                            border : false,
                            labelWidth: 150,
                            defaults:{anchor:'80%'},
                            items : [this.monthCmb,this.startdate,this.enddate]
                        },
                        {
                             columnWidth: '.50',
                             layout : 'form',
                             defaults:{anchor:'80%'},
                             border : false,
                             labelWidth: 150,
                             items : [this.yearCmb,this.frequencyStoreCmb,this.etrydate]
                        }
                    ]
                }]
            }]
        });
        this.filterPanel.show();

        this.monthCmb.on("select",function(combo,b,index){
            this.setDatevalue();
        },this);
        this.yearCmb.on("select",function(combo,b,index){
            this.setDatevalue();
        },this);

        this.filterPanel.on("show",function(){
            this.setDatevalue();
        },this);
        this.setDatevalue();
    },
    
    setDatevalue:function(){
    	var obj = Wtf.PayrollSetDatevalue(this);
        this.startdate.setValue(obj.startdt);
        this.enddate.setValue(obj.enddt);
        this.filterHandler();
    }

});

/**
 * Component for - (Process - Employee details)
 */

Wtf.processPayrollGrid = function(config){
    Wtf.processPayrollGrid.superclass.constructor.call(this,config);
    config.title="Process Payroll";
};
Wtf.extend(Wtf.processPayrollGrid,Wtf.Panel,{
    onRender : function(config){
		Wtf.monthStore.loadData(Wtf.onceMonthRec);
        Wtf.processPayrollGrid.superclass.onRender.call(this,config);

        this.getAdvancedSearchComponent();

        var tbarbtns=this.getToolbarArray();
        var bbarbtns=this.getBottomToolbarButtons();
        
        this.generatedSalaryRecord = new Wtf.data.Record.create([
        {
            name: 'payhistoryid'
        },{
            name: 'id'
        },{
            name: 'employeeid'
        },{
            name: 'accountno'
        },{
            name: 'fullname'
        },
        {
            name: 'costcenter'
        },
        {
            name: 'costcentername'
        },
        {
            name: 'jobtitle'
        },
        {
            name: 'jobtitlename'
        },
        {
            name: 'netsalary'
        },
        {
            name:'employmentdate',
            type:'date'
        },
        {
            name:'contractenddate',
             type:'date'
        },
        {
            name:'difference'
        },
        {
            name:'status'
        },
        {
            name:'absence'
        },
        {
            name:'unpaidleavesAmount'
        },
        {
        	name:'incomeTax'
        },
        {
        	name:'resource'
        }]);


        this.generatedSalaryStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.generatedSalaryRecord),
            baseParams:{
                type:Wtf.Payroll_Date_Process
            },
            url: "Payroll/Date/getPayrollHistory.py"
        });
        this.generatedSalaryStore.on('beforeload',function(store,option){
            option.params= option.params||{};
            option.params.sdate=this.startdate.getValue().format("Y-m-d");
            option.params.edate=this.enddate.getValue().format("Y-m-d");
            option.params.frequency=this.frequencyStoreCmb.getValue();
            option.params.status=this.statusComobBox.getValue();
        },this);
        calMsgBoxShow(202,4,true);
        this.generatedSalaryStore.load({
            scope:this,
            params:{
                start:0,
                limit:15
            }
        });
        this.generatedSalaryStore.on("load",function(){
            WtfGlobal.closeProgressbar();
        },this);

        this.rowNo=new Wtf.grid.RowNumberer();
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.selectionModel.on("selectionchange", function(temp){
            var statusCombo = this.statusComobBox.getValue();
            this.payslipBttn.setDisabled(true);
            this.myprint.setDisabled(true);
            this.dwnldpay.setDisabled(true);
            if(temp.selections.items.length==1){
                this.rec = temp.selections.items[0];
                var status = this.rec.data.status;
                if(statusCombo!=6){
                    this.processBttn.setDisabled(false);
                }
                
                if(status == "3") {//Authorized
                	this.unauthorizeButton.setDisabled(false);
                } else if(status == "5" || status == "6") {//Trail or Final Processed
                	if(status == "5"){
                		this.unauthorizeButton.setDisabled(false);
                	}
                    this.payslipBttn.setDisabled(false);
                    this.myprint.setDisabled(false);
                    this.dwnldpay.setDisabled(false);
                }

                this.userid = this.selectionModel.getSelected().data.id;
                this.payhistoryid = this.selectionModel.getSelected().data.payhistoryid;
                this.ename = this.selectionModel.getSelected().data.fullname;
                this.accno = this.selectionModel.getSelected().data.accountno;
        		this.historyid = this.selectionModel.getSelected().data.payhistoryid;
        		this.unpaidleavesAmount = this.selectionModel.getSelected().data.unpaidleavesAmount;
        		this.incomeTax = this.selectionModel.getSelected().data.incomeTax;
        		this.absence = this.selectionModel.getSelected().data.absence;
            }else if(temp.selections.items.length>1){
                if(statusCombo!=6){
                    this.processBttn.setDisabled(false);
                }
            } else{
                this.processBttn.setDisabled(true);
                this.unauthorizeButton.setDisabled(true);
            }
        }, this);

        this.gridcmodel= new Wtf.grid.ColumnModel([
            this.selectionModel,this.rowNo,
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.resource"),
            dataIndex: 'employeeid',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.FullName"),
            dataIndex: 'fullname',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.costcenter"),
            dataIndex: 'costcentername',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.designation"),
            dataIndex: 'jobtitlename',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.netsalary"),
            dataIndex: 'netsalary',
            scope:this,
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
            }
        },{
            header: '<div wtf:qtip="'+WtfGlobal.getLocaleText("hrms.payroll.amount.difference.between.current.net.previous.salary")+'">'+WtfGlobal.getLocaleText("hrms.payroll.difference")+'</div>',
            dataIndex: 'difference',
            sortable: true,
            align:'right',
            renderer:function(val){
                var value = parseFloat(val).toFixed(2);
                if(value<0){
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.currencyRenderer(value)+'</span>';
                } else {
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.currencyRenderer(value)+'</span>';
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.employment.date"),
            dataIndex: 'employmentdate',
            sortable: true,
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.contract.date"),
            dataIndex: 'contractenddate',
            scope:this,
            sortable: true,
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.status"),
            dataIndex: 'status',
            scope:this,
            sortable: true,
            align:'center',
            groupable: true,
            renderer:Wtf.payrollStatusRenderer
        }],this);

        this.summary = new Wtf.ux.grid.GridSummary();
        this.generatedSalaryListGrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            border:false,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search.fullname"),
            searchField:"fullname",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg"))
            },
            store: this.generatedSalaryStore,
            displayInfo:true,
            cm: this.gridcmodel,
            scope:this,
            width:400,
            sm: this.selectionModel,
            tbar:tbarbtns,
            bbar:bbarbtns
        });

        this.pan= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[
            this.filterPanel
            ,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.generatedSalaryListGrid]
            }
            ]
        });
        this.add(this.pan);

        this.statusComobBox.on('select', function(combo,record,index){
            this.generatedSalaryStore.reload();
        },this);
    },

    getToolbarArray : function(){
        var btns=[];
        btns.push('-');
        
      
        this.processBttn=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.process"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS processComponentIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.to.process.payroll.selected.employee"),
            handler : function(){
                var historyid=[];
                var userids=[];
                var sel = this.selectionModel.selections;
                for(var i=0;i<sel.length;i++){
                    if(sel.items[i].get("status")==3 || sel.items[i].get("status")==5){
                        historyid.push(sel.items[i].get("payhistoryid"));
                        userids.push(sel.items[i].get("resource"));
                    }
                }
                var processWin = Wtf.getCmp("processPay"+this.id);
                if(processWin == null){
                    processWin = new Wtf.processPayrollWin({
                        layout:"fit",
                        width:400,
                        height:400,
                        title:WtfGlobal.getLocaleText("hrms.payroll.process.payroll"),
                        closable:true,
                        border:false,
                        modal:true,
                        historyid:historyid,
                        userids:userids,
                        frequency: this.frequencyStoreCmb.getValue(),
                        enddate:this.enddate.getValue().format("Y-m-d"),
                        grid:this.generatedSalaryListGrid,
                        iconCls: getTabIconCls(Wtf.etype.master),
                        id:"processPay"+this.id
                    });
                    processWin.show();
                }
            }
        });

        btns.push(this.processBttn);
        btns.push('-');
        
        this.unauthorizeButton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.unauthorize"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS authorizeComponentIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.unauthorize.payroll.selected.employee"),
            handler : function(){

                var sm = this.generatedSalaryListGrid.getSelectionModel();
                var emparr=sm.getSelections();
                if(sm.getCount()<1){
                   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.employee.to.unauthorize.payroll")],0);
                   return;
                }

                this.empSelected =new Wtf.data.Store(this.generatedSalaryListGrid.getStore().initialConfig);
                this.empSelected.add(emparr);

                this.salStatusWindow=new Wtf.UnautorizeSalaryWin({
                    iconCls:getButtonIconCls(Wtf.btype.winicon),
                    layout:'fit',
                    closable:true,
                    width:700,
                    title:WtfGlobal.getLocaleText("hrms.payroll.unauthorize.payroll"),
                    height:500,
                    border:false,
                    empGDS:this.empSelected,
                    modal:true,
                    scope:this,
                    grid:this.generatedSalaryListGrid,
                    plain:true
                });
                this.salStatusWindow.show();
            }
        });

        btns.push(this.unauthorizeButton);
        btns.push('-');

          this.payslipBttn=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails"),
            scope:this,
            disabled:true,
            iconCls:"pwndHRMS viewbuttonIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.view.salary.details.selected.employee"),
            handler : function(){
                this.mainTabId = Wtf.getCmp("as");
                var salDetail = Wtf.getCmp(this.userid+"payslipTab"+this.startdate.getValue().format("Y-m-d"));
                if(salDetail == null){
                    salDetail = new Wtf.resourcePayslip({
                        layout:"fit",
                        title:WtfGlobal.getLocaleText({key:"hrms.payroll.employeespayslip",params:[this.ename]}),
                        closable:true,
                        border:false,
                        id:this.userid+"payslipTab"+this.startdate.getValue().format("Y-m-d"),
                        iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                        userid:this.userid,
                        historyid:this.payhistoryid,
                        startdate:this.startdate.getValue().format("Y-m-d"),
                        enddate:this.enddate.getValue().format("Y-m-d"),
                        frequency:this.frequencyStoreCmb.getValue(),
                        ename:this.ename,
                        accno:this.accno,
                        unpaidleavesAmount:this.unpaidleavesAmount,
                        incomeTax:this.incomeTax,
                        absence:this.absence
                    });
                    this.mainTabId.add(salDetail);
                    salDetail.on('gridload',function(){
                        calMsgBoxShow(202,4,true);
                        this.empstore.load();
                    },this);
                }
                this.mainTabId.setActiveTab(salDetail);
                this.mainTabId.doLayout();
            }
        });
        btns.push(this.payslipBttn);
        btns.push('-');

        this.paydetails=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.export.Export"),
            scope:this,
            iconCls:'pwndExport export',
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.click.export.salary.details.csv.format"),
            handler:this.ExportData
        });

        btns.push(this.paydetails);
        btns.push('-');
        
        this.dwnldpay=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
            iconCls:"pwnd downloadIcon",
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.click.download.payslip.pdf"),
            scope:this,
            disabled:true,
            handler:function(){
        		Wtf.get('downloadframe').dom.src = "Payroll/Salary/exportSalarySlip.py?"+"userid="+this.userid+"&startdate="+this.startdate.getValue().format("Y-m-d")+"&enddate="+this.enddate.getValue().format("Y-m-d")+"&historyid="+this.payhistoryid;
            }
        });
        btns.push(this.dwnldpay);
        btns.push('-');

        this.myprint=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip"),
            scope:this,
            iconCls:"pwnd printIcon",
            disabled:true,
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.print.payslip"),
            handler : function(){
        		window.open("Payroll/Salary/printSalarySlip.py?"+"userid="+this.userid+"&startdate="+this.startdate.getValue().format("Y-m-d")+"&enddate="+this.enddate.getValue().format("Y-m-d")+"&historyid="+this.payhistoryid,"mywindow","menubar=1,resizable=1,scrollbars=1");
            }
        });
        btns.push(this.myprint);
        btns.push("->");
        btns.push(WtfGlobal.getLocaleText("hrms.common.select.status"));
        this.statusComobBox = Wtf.payrollStatusCombobox("Process");
        btns.push(this.statusComobBox);
        return btns;
    },

    filterHandler:function(){
    	if(this.generatedSalaryStore!=undefined){
    		this.generatedSalaryStore.load({
    			scope:this,
    			params:{
                	start:0,
                	limit:this.generatedSalaryListGrid.pag.pageSize
            	}
    		});
    	}
    },

    getBottomToolbarButtons :function(){
        var bbtns =[];

        return bbtns;
    },

    getAdvancedSearchComponent: function(){

        this.monthCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.period"),
            hiddenName: 'period',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.monthStore,
            width:150,
            typeAhead:true,
            value:new Date().getMonth()+1
        });

        this.startdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.start.date"),
            format:"Y-m-d",
            name:'joindate',
            disabled:true,
            width:200
        });
        this.enddate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.end.date"),
            format:"Y-m-d",
            disabled:true,
            name:'confirmdate',
            width:200
        });

        this.yearCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Year"),
            hiddenName: 'year',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.yearStore,
            width:150,
            typeAhead:true,
            value:new Date().getFullYear()
        });

        this.frequencyStoreCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.frequency"),
            hiddenName: 'frequency',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.frequencyStore,
            width:150,
            typeAhead:true,
            value:0
        });

        this.frequencyStoreCmb.on("select", function(a, b, c){
        	var i = eval(b.data.id);
        	if(i == 0){
        		Wtf.monthStore.loadData(Wtf.onceMonthRec);
        		this.monthCmb.setValue("1");
        	}else if(i == 1){
        		Wtf.monthStore.loadData(Wtf.onceWeekRec);
        		this.monthCmb.setValue("1");
        	}else{
        		Wtf.monthStore.loadData(Wtf.twiceMonthRec);
        		this.monthCmb.setValue("1");
        	}
        	this.setDatevalue();
        }, this);


        this.etrydate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.entry.date"),
            format:"Y-m-d",
            name:'entrydate',
            width:200
        });

        this.applyFilter = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.filter"),
            scope:this,
            iconCls:"pwndExport filter",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.filter"),
            handler :this.filterHandler
        });

//        this.closeFilter = new Wtf.Button({
//            text:'Close',
//            scope:this,
//            iconCls:"pwndCommon cancelbuttonIcon",
//            tooltip :"Close",
//            handler : function(){
//               this.filterPanel.hide();
//               this.advancedSearch.enable();
//               this.pan.doLayout();
//            }
//        });

        this.filterPanel  = new Wtf.form.FormPanel({
            autoScroll:true,
          //  hidden:true,
            border:false,
            layout:'fit',
            region:'north',
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 25px;',
            height:170,
//            tbar:['->',this.applyFilter ],
            items :[{
                border: false,
                defaults: {
                    border: false,
                    xtype: "fieldset",
                    autoHeight: true
                },
                items: [{
                    title: WtfGlobal.getLocaleText("hrms.common.advanced.search"),
                    layout:'column',
                    items: [
                        {
                            columnWidth: '.50',
                            layout : 'form',
                            border : false,
                            labelWidth: 150,
                            defaults:{anchor:'80%'},
                            items : [this.monthCmb,this.startdate,this.enddate]
                        },
                        {
                             columnWidth: '.50',
                             layout : 'form',
                             defaults:{anchor:'80%'},
                             border : false,
                             labelWidth: 150,
                             items : [this.yearCmb,this.frequencyStoreCmb,this.etrydate]
                        }
                    ]
                }]
            }]
        });
        this.filterPanel.show();
        this.monthCmb.on("select",function(combo,b,index){
            this.setDatevalue();
        },this);
        this.yearCmb.on("select",function(combo,b,index){
            this.setDatevalue();
        },this);

        this.filterPanel.on("show",function(){
            this.setDatevalue();
        },this);
        this.setDatevalue();
    },

    setDatevalue:function(){
    	var obj = Wtf.PayrollSetDatevalue(this);
        this.startdate.setValue(obj.startdt);
        this.enddate.setValue(obj.enddt);
        this.filterHandler();
    },

    ExportData:function(){
    	var url = "Payroll/Salary/exportPayDetails.py?"+Wtf.urlEncode(Wtf.urlDecode("startdate="+this.startdate.getValue().format("Y-m-d")+"&enddate="+this.enddate.getValue().format("Y-m-d")+"&frequency="+this.frequencyStoreCmb.getValue()+"&module="+Wtf.Payroll_Date_Process+"&status="+this.statusComobBox.getValue()));
    	Wtf.get('downloadframe').dom.src = url;
    }

});
