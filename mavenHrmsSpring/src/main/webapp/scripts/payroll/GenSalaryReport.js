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
Wtf.GenSalaryReport = function(config){
    Wtf.GenSalaryReport.superclass.constructor.call(this,config);
    config.autoScroll=false;
    this.addEvents({
        "announce": true
    });
};
Wtf.extend(Wtf.GenSalaryReport,Wtf.Panel,{
    onRender : function(config){
        Wtf.GenSalaryReport.superclass.onRender.call(this,config);
        this.maxUsers = 0;
        this.costPerUser = 0;
        this.count = 0;
        this.cnamedata = [
        [WtfGlobal.getLocaleText("hrms.January"),'1'],
        [WtfGlobal.getLocaleText("hrms.February"),'2'],
        [WtfGlobal.getLocaleText("hrms.March"),'3'],
        [WtfGlobal.getLocaleText("hrms.April"),'4'],
        [WtfGlobal.getLocaleText("hrms.May"),'5'],
        [WtfGlobal.getLocaleText("hrms.June"),'6'],
        [WtfGlobal.getLocaleText("hrms.July"),'7'],
        [WtfGlobal.getLocaleText("hrms.August"),'8'],
        [WtfGlobal.getLocaleText("hrms.September"),'9'],
        [WtfGlobal.getLocaleText("hrms.October"),'10'],
        [WtfGlobal.getLocaleText("hrms.November"),'11'],
        [WtfGlobal.getLocaleText("hrms.December"),'12']
        ];
        this.combostore=new Wtf.data.SimpleStore({
            fields:[{
                name:'cname'
            },
            {
                name:'mid'
            }]
        });

        this.combostore.loadData(this.cnamedata);
        this.TaxName= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.Customer.Name"),
            store:this.combostore,
            displayField:'cname',
            mode: 'local',
            scope:this,
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.CName.Select"),
            selectOnFocus:true,
            width:120,
            height:200,
            triggerAction :'all',
            listeners :{
                scope:this,
                select:function(TaxName,rec,i){
                    this.month=this.combostore.getAt(i).get('cname');
                }
            }
        });
        this.fromdaterep=new Wtf.form.DateField({
            width:155,
            id:'fromdaterep',
            scope:this,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,-1).getFirstDateOfMonth()
        });
        this.todaterep= new Wtf.form.DateField({
            width:155,
            readOnly:true,
            id:'todaterep',
            scope:this,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+1).getLastDateOfMonth()
        });
        
        this.usersRecS = new Wtf.data.Record.create([
        {
            name: 'EName'
        },
        {
            name: 'Wage'
        },
        {
            name: 'AccNo'
        },
        {
            name: 'month'
        },
        {
            name: 'Tax'
        },
        {
            name: 'Deduc'
        },
        {
            name: 'Salary'
        },
        {
            name: 'FixedSal'
        },
        {
            name:'empid'
        },
        {
            name:'design'
        },
        {
            name:'tempid'
        },
        {
            name:'tempname'
        },
        {
            name:'stdate',type:'date'
        },
        {
            name:'enddate',type:'date'
        }
        ]); 


        this.userstore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.usersRecS),
            url: Wtf.req.base + 'PayrollHandler.jsp'

        });
        var st=this.fromdaterep.getValue().format('m/d/Y');
        var end=this.todaterep.getValue().format('m/d/Y'); //created by bhosale
        calMsgBoxShow(202,4,true);
        this.userstore.baseParams={
            type:'ReportPerMonth',
            stdate:st,
            enddate:end
        }
        this.userstore.load({
            params:{               
                 start:0,
                 limit:15                 
            }
        });
        this.userstore.on("load",function(){
            if(msgFlag==1)
            WtfGlobal.closeProgressbar()
        },this);
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.rowNo=new Wtf.grid.RowNumberer();
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();

       // this.gridcmodel= new Wtf.grid.ColumnModel([this.selectionModel,this.rowNo,
       this.gridcmodel= new Wtf.grid.ColumnModel([this.rowNo,
        {
            header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
            dataIndex: 'EName',
            pdfwidth:60,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Month"),
            dataIndex: 'month',
            align:'center',
            pdfwidth:60,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'stdate',
            pdfwidth:60,
            align:'center',
            renderer:WtfGlobal.onlyDateRenderer,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),
            dataIndex: 'enddate',
            pdfwidth:60,
            align:'center',
            renderer:WtfGlobal.onlyDateRenderer,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Earning"),
            dataIndex: 'Wage',
            sortable: true,
            align:'right',
            pdfwidth:60,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Taxes"),
            dataIndex: 'Tax',
            sortable: true,
            pdfwidth:60,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Deductions"),
            dataIndex: 'Deduc',
            sortable: true,
            pdfwidth:65,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\"">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.netpay"),
            dataIndex: 'Salary',
            //autoWidth : true,
            scope:this,
            pdfwidth:60,
            align:'right',
            sortable: true,
            groupable: true,
            summaryType:'sum',
            summaryRenderer:WtfGlobal.currencySummaryRenderer,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\"">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Template"),
            dataIndex: 'tempname',
            pdfwidth:60,
            align: 'center',
            sortable: true,
            groupable: true
        }],this);

        this.summary = new Wtf.ux.grid.GridSummary();
        this.salusergrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            autoScroll : false,
            stripeRows: true,
            plugins:[this.summary],
            searchLabel:" ",
            searchLabelSeparator:" ",
            displayInfo:true,
            loadMask:true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            searchField:"EName",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.nosaltillnow"))
            },
            store: this.userstore,
            cm: this.gridcmodel,
            region:'west',
            scope:this,
            width:400,
            id:'salusergrid',            
            sm: new Wtf.grid.CheckboxSelectionModel({
                singleSelect:false,
                scope:this,
                listeners:{
                    scope:this,
                    rowselect :function(sm,index,record){
                        var recData = this.salusergrid.getSelectionModel().getSelected().data;
                        this.ename=recData.EName;
                        //this.accno=recData.AccNo;
                        this.salary=recData.Wage;
                        this.fixedsal=recData.FixedSal;
                        this.tax=recData.Tax;
                        this.deduc=recData.Deduc;
                        this.empid=recData.empid;
                        this.design=recData.design;
                        this.TempId=recData.tempid;
                    },
                    rowdeselect:function(selectionmodal,index,record){
                    }
                }
            }),
            tbar:['-',new Wtf.Toolbar.Button({
         		text:WtfGlobal.getLocaleText("hrms.common.reset"),
         		scope: this,
         		iconCls:'pwndRefresh',
         		handler:function(){
            		this.userstore.load({params:{start:0,limit:this.salusergrid.pag.pageSize}});
            		Wtf.getCmp("Quick"+this.salusergrid.id).setValue("");
            		}
         		}),'-',WtfGlobal.getLocaleText("hrms.common.start.date")+':',this.fromdaterep,'-',WtfGlobal.getLocaleText("hrms.common.end.date"),this.todaterep,
         		'-',{
                    text:WtfGlobal.getLocaleText("hrms.payroll.GenerateSalaryReport"),
                    tooltip:WtfGlobal.getLocaleText("hrms.payroll.GenerateSalaryReport.tooltip"),
                    minWidth:100,
                    scope:this,
                    iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                    handler:function()
                    {
                        var st=this.fromdaterep.getValue().format('m/d/Y');
                        var end=this.todaterep.getValue().format('m/d/Y');
                        this.userstore.removeAll();
                        this.userstore.baseParams={
                            type:'ReportPerMonth',
                            stdate:st,
                            enddate:end
                        }
                        calMsgBoxShow(202,4,true);
                        this.userstore.load({
                            scope:this,
                            params:{
                                start:0,
                                limit:this.salusergrid.pag.pageSize
                            }
                        });
                        this.userstore.on("load",function(){WtfGlobal.closeProgressbar()},this);
                    }
            },'-',
            this.expBtn=new Wtf.exportButton({
                obj:this,
                menuItem:{
                    csv:true,
                    pdf:true,
                    rowPdf:true
                },
                get:4,
    //            url:"Common/Export/.common"
                url :"../../export.jsp",
                filename:WtfGlobal.HTMLStripper(this.title)
            })
            ]
        });
        this.grid = this.salusergrid;
        this.UsergridPanel2  = new Wtf.Panel({
            border:false,
            autoLoad:false,
            paging:false,
            layout:'fit',
            items:[this.salusergrid]
        });
        this.innerpanel2 = new Wtf.Panel({
            layout : 'fit',
            cls : 'backcolor',
            border :false,
            items:[this.UsergridPanel2]
        });
        this.add(this.innerpanel2);
    }
});

Wtf.ApproveSalaryList = function(config){
    Wtf.ApproveSalaryList.superclass.constructor.call(this,config);
    config.autoScroll=false;
    this.addEvents({
        "announce": true
    });
};
Wtf.extend(Wtf.ApproveSalaryList,Wtf.Panel,{
    onRender : function(config){
        Wtf.ApproveSalaryList.superclass.onRender.call(this,config);
                        
        this.salaryType = new Wtf.data.SimpleStore({
            fields:['id','value'],
            data:[
                ['0', WtfGlobal.getLocaleText("hrms.common.All")],
                ['1', WtfGlobal.getLocaleText("hrms.payroll.Unauthorized")],
                ['2', WtfGlobal.getLocaleText("hrms.recruitment.pending")],
                ['3', WtfGlobal.getLocaleText("hrms.payroll.Authorized")]
            ]
        });

        this.salaryTypeCombo = new Wtf.form.ComboBox({
            mode: 'local',
            triggerAction: 'all',
            typeAhead: true,
            width:110,
            editable: false,
            store: this.salaryType,
            displayField: 'value',
            valueField:'id',
            allowBlank:false,
            msgTarget: 'side'
        });
        this.salaryTypeCombo.setValue('0');

        this.usersRecS = new Wtf.data.Record.create([
        {
            name: 'EName'
        },
        {
            name: 'Wage'
        },
        {
            name: 'AccNo'
        },
        {
            name: 'month'
        },
        {
            name: 'Tax'
        },
        {
            name: 'Deduc'
        },
        {
            name: 'Salary'
        },
        {
            name: 'FixedSal'
        },
        {
            name:'empid'
        },
        {
            name:'design'
        },
        {
            name:'tempid'
        },
        {
            name:'tempname'
        },{
            name: 'historyid'
        },
        {
            name:'salarystatus'
        },
        {
            name:'stdate',type:'date'
        },
        {
            name:'enddate',type:'date'
        }
        ]);

        

        this.fromdate=new Wtf.form.DateField({
            width:105,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getFirstDateOfMonth()
        });
        this.todate= new Wtf.form.DateField({
            width:105,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            disabled:true,
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getLastDateOfMonth()
        });
        this.fromdate.on('change',function(){
            var myDate=new Date();
            myDate=this.fromdate.getValue();
            this.todate.setValue(myDate.add(Date.MONTH,+0).getLastDateOfMonth());
        },this);

        
        this.userstore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.usersRecS),
            url: Wtf.req.base + 'PayrollHandler.jsp'

        });
        var st=this.fromdate.getValue().format('m/d/Y');
        var end=this.todate.getValue().format('m/d/Y'); //created by bhosale
        calMsgBoxShow(202,4,true);
        this.userstore.baseParams={
            type:'GenerateSalaryList',
            stdate:st,
            enddate:end,
            salaryStatus:0
        }
        this.userstore.load({
            params:{
                 start:0,
                 limit:15
            }
        });
        this.userstore.on("load",function(){
            if(msgFlag==1)
            WtfGlobal.closeProgressbar()
        },this);
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.rowNo=new Wtf.grid.RowNumberer();

       

        this.gridcmodel= new Wtf.grid.ColumnModel([this.rowNo,this.selectionModel,
     
        {
            header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
            dataIndex: 'EName',
            pdfwidth:60,
            sortable: true,
            groupable: true
        },{
            header:  WtfGlobal.getLocaleText("hrms.common.Month"),
            dataIndex: 'month',
            align:'center',
            pdfwidth:60,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'stdate',
            pdfwidth:60,
            align:'center',
            renderer:WtfGlobal.onlyDateRenderer,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),
            dataIndex: 'enddate',
            pdfwidth:60,
            align:'center',
            renderer:WtfGlobal.onlyDateRenderer,
            sortable: true,
            groupable: true
        },{
            header:  WtfGlobal.getLocaleText("hrms.common.Earning"),
            dataIndex: 'Wage',
            sortable: true,
            align:'right',
            pdfwidth:60,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header:  WtfGlobal.getLocaleText("hrms.common.Taxes"),
            dataIndex: 'Tax',
            sortable: true,
            pdfwidth:60,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header:  WtfGlobal.getLocaleText("hrms.common.Deductions"),
            dataIndex: 'Deduc',
            sortable: true,
            pdfwidth:65,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\"">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.payroll.netpay"),
            dataIndex: 'Salary',
            //autoWidth : true,
            scope:this,
            pdfwidth:60,
            align:'right',
            sortable: true,
            groupable: true,
            summaryType:'sum',
            summaryRenderer:WtfGlobal.currencySummaryRenderer,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\"">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header:  WtfGlobal.getLocaleText("hrms.common.Template"),
            dataIndex: 'tempname',
            pdfwidth:60,
            align: 'center',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.SalaryStatus"),
            dataIndex: 'salarystatus',
            sortable: true,
            pdfwidth:60,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    if(val==1){
                        return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.payroll.Unauthorized")+'</span>';
                    } else if(val==2){
                        return '<span style=\'color:blue !important;\'>'+WtfGlobal.getLocaleText("hrms.recruitment.pending")+'</span>';
                    } else if(val==3){
                        return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.payroll.Authorized")+'</span>';
                    }
                    
                }
            }
        }],this);
        
        this.expBtn=new Wtf.exportButton({
            obj:this,
            menuItem:{
                csv:true,
                pdf:true,
                rowPdf:true
            },
            get:5,
            //            url:"Common/Export/.common"
            url :"../../export.jsp",
            filename:WtfGlobal.HTMLStripper(this.title)
        })
        var btns=[];
        this.gensalbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.Dashboard.AuthorizeSalary"),
                tooltip:WtfGlobal.getLocaleText("hrms.Dashboard.AuthorizeSalary.tooltip"),
                scope:this,
                minWidth:100,
                disabled:true,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                handler:function(){this.generateSalary(3);}
            });
         btns.push('-');
         btns.push(this.gensalbtn);

         this.unauthsalbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.UnauthorizeSalary"),
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.UnauthorizeSalary.tooltip"),
                scope:this,
                minWidth:100,
                disabled:true,
                iconCls:"pwndCommon cancelbuttonIcon",
                handler:function(){this.generateSalary(1);}
            });
        btns.push('-');
        btns.push(this.unauthsalbtn);

        if(userroleid==1){
            this.confunauthsalbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.ConfirmUnauthorization"),
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.ConfirmUnauthorization.tooltip"),
                scope:this,
                minWidth:100,
                disabled:true,
                iconCls:"pwndCommon deletebuttonIcon",
                handler:function(){
                    var rec =this.salusergrid.getSelectionModel().getSelections();
                    this.deletePayslip(rec);
                }
            });
            btns.push('-');
            btns.push(this.confunauthsalbtn);
        }

        btns.push('-');
        btns.push( WtfGlobal.getLocaleText("hrms.common.select.status")+':');
        btns.push(this.salaryTypeCombo);

        btns.push('->');
        btns.push(WtfGlobal.getLocaleText("hrms.common.start.date")+':');
        btns.push(this.fromdate);

        btns.push('-');
        btns.push(WtfGlobal.getLocaleText("hrms.common.end.date")+':');
        btns.push(this.todate);

        this.filterBtn=new Wtf.Toolbar.Button({
                iconCls:"pwndExport filter",
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.Selectadateandgeneratealistofgeneratedsalariesinthatmonth"),
                disabled:false,
                scope:this,
                handler:this.getHistoryList
            });
        btns.push('-');
        btns.push(this.filterBtn);

        this.resetbtn=new Wtf.Toolbar.Button({
            scope: this,
            iconCls:'pwndRefresh',
            handler:function(){
                this.userstore.load({params:{start:0,limit:this.salusergrid.pag.pageSize}});
                Wtf.getCmp("Quick"+this.salusergrid.id).setValue("");
                }
            });
        btns.push('-');
        btns.push(this.resetbtn);

        this.salDetails=new Wtf.Toolbar.Button({

            text:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails"),
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails.tooltip"),
            scope:this,
            minWidth:100,
            disabled:true,
            iconCls:getButtonIconCls(Wtf.btype.reportbutton),
            handler:this.getSalaryDetails
        })
            

        this.summary = new Wtf.ux.grid.GridSummary();
        this.salusergrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            autoScroll : false,
            stripeRows: true,
            plugins:[this.summary],
            searchLabel:" ",
            searchLabelSeparator:" ",
            displayInfo:true,
            loadMask:true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            searchField:"EName",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.nosaltillnow"))
            },
            store: this.userstore,
            cm: this.gridcmodel,
            region:'west',
            scope:this,
            width:400,
            sm: new Wtf.grid.CheckboxSelectionModel({
                singleSelect:false,
                scope:this,
                listeners:{
                    scope:this,
                    rowselect :function(sm,index,record){
                        var recData = this.salusergrid.getSelectionModel().getSelected().data;
                        this.ename=recData.EName;
                        //this.accno=recData.AccNo;
                        this.salary=recData.Wage;
                        this.fixedsal=recData.FixedSal;
                        this.tax=recData.Tax;
                        this.deduc=recData.Deduc;
                        this.empid=recData.empid;
                        this.design=recData.design;
                        this.TempId=recData.tempid;

                        this.generateSalButtonEnableDisable();
                        

                    },
                    rowdeselect:function(selectionmodal,index,record){

                        this.generateSalButtonEnableDisable();
                    }
                }
            }),
            bbar:[this.salDetails,'-',this.expBtn],
            tbar:btns
        });
        this.grid = this.salusergrid;
        this.UsergridPanel2  = new Wtf.Panel({
            border:false,
            autoLoad:false,
            paging:false,
            layout:'fit',
            items:[this.salusergrid]
        });
        this.innerpanel2 = new Wtf.Panel({
            layout : 'fit',
            cls : 'backcolor',
            border :false,
            items:[this.UsergridPanel2]
        });
        this.add(this.innerpanel2);
        this.salaryTypeCombo.on("select",function(){
            this.getHistoryList();
        },this);
    },

    generateSalButtonEnableDisable:function(){

        var rec = this.salusergrid.getSelectionModel().getSelections();
        this.gensalbtn.enable();
        this.unauthsalbtn.enable();
        if(this.confunauthsalbtn!=undefined){
            this.confunauthsalbtn.enable();
        }
        
        for(var i=0; i<rec.length ;i++){
            var salStatus = rec[i].data.salarystatus;
            if(salStatus==1 || salStatus==3){
                this.gensalbtn.disable();
                this.unauthsalbtn.disable();
                break;
            }
        }

        for(var i=0; i<rec.length ;i++){
            var salStatus = rec[i].data.salarystatus;
            if(salStatus==2 || salStatus==3){
                if(this.confunauthsalbtn!=undefined){
                    this.confunauthsalbtn.disable();
                }
                break;
            }
        }
        
        if(rec.length==1){
            this.salDetails.enable();
        }else if(rec.length==0) {
            this.gensalbtn.disable();
            this.unauthsalbtn.disable();
            if(this.confunauthsalbtn!=undefined){
                this.confunauthsalbtn.disable();
            }
            this.salDetails.disable();
        }else {
            this.salDetails.disable();
        }
        
    },
    generateSalary:function(mode){

        var rec = this.salusergrid.getSelectionModel().getSelections();
        var historyids="";
        for(var i=0; i<rec.length ;i++){
            var historyid = rec[i].data.historyid;
            historyids+=historyid+",";
        }

        historyids = historyids.substring(0,(historyids.length-1));
        var text="";
        if(mode==3){
            text="authorize";
        }else if(mode==1){
            text="unauthorize";
        }
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:text=="authorize"?WtfGlobal.getLocaleText("hrms.payroll.Areyousureyouauthorizesalaryselectedrecord"):WtfGlobal.getLocaleText("hrms.payroll.AreyousureyouUnauthorizesalaryselectedrecord"),
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='no')
                {
                    return;
                } else {
                    Wtf.Ajax.requestEx({
                        url: "Emp/generateApprovedSalary.py",
                        scope:this,
                        method:'post',
                        params:{
                            historyids:historyids,
                            mode:mode // mode 1: Unauthorized  2: Authorized
                        }
                    },this,
                    function(req){
                        
                        Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"),text=="authorize"?WtfGlobal.getLocaleText("hrms.payroll.Salaryauthorizedsuccessfully"):WtfGlobal.getLocaleText("hrms.payroll.SalaryUnauthorizedsuccessfully"));
                        
                        this.getHistoryList();
                    },
                    function(req){

                    });
                }
            }
        });
        
    },

    getHistoryList :function(){
        var st=this.fromdate.getValue().format('m/d/Y');
        var end=this.todate.getValue().format('m/d/Y');
        this.userstore.removeAll();
        this.userstore.baseParams={
            type:'GenerateSalaryList',
            stdate:st,
            enddate:end,
            salaryStatus:this.salaryTypeCombo.getValue()
        }
        calMsgBoxShow(202,4,true);
        this.userstore.load({
            scope:this,
            params:{
                start:0,
                limit:this.salusergrid.pag.pageSize
            }
        });
        this.userstore.on("load",function(){
            WtfGlobal.closeProgressbar();
            this.generateSalButtonEnableDisable();
        },this);
        
    },

     getSalaryDetails :function(){
        var rec =this.salusergrid.getSelectionModel().getSelections();
        var month=rec[0].get('stdate').format('F');
        if(rec.length>0){
            this.mainTabId = Wtf.getCmp("as");
            this.payslip = Wtf.getCmp(this.id+"payslipTab"+rec[0].get('stdate'));
            if(this.payslip == null){
                this.payslip = new Wtf.EmpPayslip({
                    layout:"fit",
                    scope:this,
                    closable:true,
                    iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                    border:false,
                    id:this.id+"payslipTab"+rec[0].get('stdate'),
                    TempId:rec[0].get('tempid'),
                    title: WtfGlobal.getLocaleText({key:"hrms.payroll.SalaryDetailsofmonth",params:[month]}),
                    ename:rec[0].get('EName'),
                    accno:rec[0].get('AccNo'),
                    salary:rec[0].get('Salary'),
                    tax:rec[0].get('Tax'),
                    empid:rec[0].get('empid'),
                    deduc:rec[0].get('Deduc'),
                    cursymbol:this.currency,
                    fixedsal:rec[0].get('FixedSal'),
                    design:rec[0].get('design'),
                    flag:"employee",
                    histid:rec[0].get('historyid'),
                    stdate:rec[0].get('stdate'),
                    enddate:rec[0].get('enddate')
                });
                this.mainTabId.add(this.payslip);
            }
            this.mainTabId.setActiveTab(this.payslip);
            this.mainTabId.doLayout();
        }
        else{
            calMsgBoxShow(42,0);
        }

    },

    deletePayslip: function(rec){
            
            var st=rec[0].get('stdate').format('m/d/Y');
            var end=rec[0].get('enddate').format('m/d/Y');
            var empid= new Array();
            
            for(var i=0; i<rec.length; i++){
            	empid.push(rec[i].get('empid'));
            }
            
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms.payroll.Areyousureyouwanttodeletethesalarydetailsoftheemployee"),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='no')
                    {
                        return;
                    } else {
                        Wtf.Ajax.requestEx({
                            url: "Emp/deletePayslipDetails.py" ,
                            method:'post',
                            params:{
                                empid:Wtf.encode(empid),
                                enddate:end,
                                startdate:st
                            }
                        },
                        this,
                        function(response){
                            var res=eval('('+response+')');
                            var msg = res.msg;
                            this.getHistoryList();
                            Wtf.notify.msg("Success",msg);
                        },
                        function(req,res){
                            calMsgBoxShow(27,1);
                        }
                        );
                    }
                }
            });
       
    }
});

