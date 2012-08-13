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

Wtf.GeneratePayroll=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    config.autoScroll=true;
    config.layout="fit";
    config.closable=true;
    Wtf.GeneratePayroll.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.GeneratePayroll,Wtf.Panel,{
    initComponent:function(config){
        Wtf.GeneratePayroll.superclass.initComponent.call(this,config);
        this.fromdateempG=new Wtf.form.DateField({
            width:135,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y'
        });
        this.todateempG= new Wtf.form.DateField({
            width:135,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            format:'m/d/Y'
        });
        this.usersRec = new Wtf.data.Record.create([
        {
            name: 'empid'
        },
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
        }
        ]);

        this.userds = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.usersRec),
            url: Wtf.req.base + 'PayrollHandler.jsp'
        });
        this.userds.load({
            params:{
                start:0,
                limit:15,
                taxcash:this.taxcash,
                wagecash:this.wagecash,
                deduccash:this.deduccash,
                TempId:this.TempId,
                type:'EmpPerTemId',
                ttotal:this.ttotal,
                wtotal:this.wtotal,
                dtotal:this.dtotal
            }
        });
        this.userds.on("load",function(){
            if(this.userds.getCount()==0)
            {
                calMsgBoxShow(18,0);
                
            }
            
        },this);
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            scope:this,
            singleSelect:false
        });
        this.gridcm= new Wtf.grid.ColumnModel([this.selectionModel,
        {
            header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
            dataIndex: 'EName',
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Earning"),
            dataIndex: 'Wage',
            sortable: true,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return(WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2)));
                }
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.common.Taxes"),
            dataIndex: 'Tax',
            sortable: true,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return(WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2)));
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Deductions"),
            dataIndex: 'Deduc',
            sortable: true,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return(WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2)));
                }
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.payroll.netpay"),
            dataIndex: 'Salary',
            //autoWidth : true,
            scope:this,
            sortable: true,
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return(WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2)));
                }
            }
        }],this);
        this.sm2= new Wtf.grid.CheckboxSelectionModel({
                singleSelect:false,
                scope:this,
                listeners:{
                    scope:this,
                    rowselect :function(selectionmodal,index,record){
                         var recData = this.usergrid.getSelectionModel().getSelected().data;
                        this.ename=recData.EName;
                        this.accno=recData.AccNo;
                        this.salary=recData.Wage;
                        this.fixedsal=recData.FixedSal;
                        this.tax=recData.Tax;
                        this.deduc=recData.Deduc;
                        this.empid=recData.empid;
                        this.design=recData.design;
                     },
                     rowdeselect:function(selectionmodal,index,record){
                    }
                }
            });
        this.usergrid = new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            loadMask:true,
            searchLabel:WtfGlobal.getLocaleText("hrms.common.QuickSearch"),
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            searchField:"EName",
            viewConfig: {
                forceFit: true
            },
            store: this.userds,
            cm: this.gridcm,
            scope:this,
            width:400,
            id:'grouplistgridgenpay',
            border :false,
            sm:this.sm2,
            tbar:[
            '-', WtfGlobal.getLocaleText("hrms.common.start.date"), this.fromdateempG, WtfGlobal.getLocaleText("hrms.common.end.date"), this.todateempG,'-',
            this.gensalbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.GenerateSalary"),
                scope:this,
                minWidth:100,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                handler:function(){
                    if(this.fromdateempG.getRawValue()=="" || this.todateempG.getRawValue()=="" || this.fromdateempG.getRawValue()>this.todateempG.getRawValue())
                    {
                        calMsgBoxShow(14,0);
                    }else{
                        this.arr=this.usergrid.getSelections();
                        if(this.arr.length==0)
                        {
                            calMsgBoxShow(19,0);
                        }
                        else{
                            this.jsondata = "";
                            var vflag=0;
                            this.arr=this.usergrid.getSelections();
                            for(i=0;i<this.arr.length;i++)
                            {
                                this.jsondata += "{'EName':'" + this.arr[i].get('EName')+ "',";
                                this.jsondata += "'AccNo':'" +this.arr[i].get('AccNo')+ "',";
                                this.jsondata += "'Wage':'" +this.arr[i].get('Wage')+ "',";
                                this.jsondata += "'FixedSal':'" +this.arr[i].get('FixedSal')+ "',";
                                this.jsondata += "'Tax':'" +this.arr[i].get('Tax')+ "',";
                                this.jsondata += "'Deduc':'" +this.arr[i].get('Deduc')+ "',";
                                this.jsondata += "'empid':'" +this.arr[i].get('empid')+ "',";
                                this.jsondata+= "'design':'" +this.arr[i].get('design')+ "'},";
                                if(this.arr[i].get('Salary')<0){
                                   vflag=1;
                                   break;
                                }
                            }
                            this.trmLen1 =this.jsondata.length - 1;
                            this.jsondata = this.jsondata.substr(0,this.trmLen1);
                            if(vflag==0){
                            Wtf.Ajax.requestEx({
                                url: "Emp/setPayrollforTemp.py" ,
                                scope:this,
                                method:'post',
                                params:{
                                    save:'true',
                                    saveType:'PayHistoryforTemp',//Create Payslip Per Template
                                    jsondata:this.jsondata,
                                    TempId:this.TempId,
                                    stdate:this.fromdateempG.getRawValue(),
                                    enddate:this.todateempG.getRawValue()
                                }
                            },this,
                                function(req){
                                    var resp=req.value.toString();
                                    if(resp=="success"){
                                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),req.msg.toString()],0);
                                        this.empstore.load();
                                    }else if(resp=="failure"){
                                        calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),req.msg.toString()],0);
                                    }

                                },
                                function(req){

                                }
                            );
                        }else{
                            calMsgBoxShow(157,0);
                        }
                        }
                    }
                }
            }),
            {
                text:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails"),
                scope:this,
                minWidth:100,
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                handler:function(){
                    this.arr=this.usergrid.getSelections();
                    if(this.arr.length>1)
                    {
                         calMsgBoxShow(20,0);
                    }
                    else
                    {
                        if(this.usergrid.getSelectionModel().getSelected()!=null){
                            this.mainTabId = Wtf.getCmp("mainpayrolltab");
                            this.payslip = Wtf.getCmp("payslipTabgenpay");
                            if(this.payslip == null){
                                this.payslip = new Wtf.EmpPayslip({
                                    layout:"fit",
                                    //title:"Employee Payslip",
                                    closable:true,
                                    iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                                    border:false,
                                    id:"payslipTabgenpay",
                                    TempId:this.TempId,
                                    ename:this.ename,
                                    accno:this.accno,
                                    salary:this.salary,
                                    tax:this.tax,
                                    empid:this.empid,
                                    deduc:this.deduc,
                                    cursymbol:this.currency,
                                    fixedsal:this.fixedsal,
                                    design:this.design
                                });
                                this.mainTabId.add(this.payslip);
                            }
                            this.mainTabId.setActiveTab(this.payslip);
                            this.mainTabId.doLayout();
                        }
                        else
                        {
                             calMsgBoxShow(21,0);
                        }
                    }
                }
            },
            this.mypdf=new Wtf.Button({
				text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
				scope:this,
				iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
				tooltip :WtfGlobal.getLocaleText("hrms.payroll.view.past.payslips"),
				handler : function(){
                        var rec=this.sm2.getSelected();
					  this.exportReport(1, "pdf", WtfGlobal.getLocaleText("hrms.payroll.leave.card"), rec.data['empid'],companyName,this.fromdateempG.getRawValue(),this.todateempG.getRawValue());
						this.empid=null;
				}
			})
            ]
        });
        this.mypdf.disable();
        this.pan=new Wtf.Panel({
            layout:'fit',
            autoHeight:true,
            columnWidth:1,
            title:WtfGlobal.getLocaleText("hrms.payroll.employee.list"),
            border:false,
            scope:this,
            items:[this.usergrid]
        });

        this.MainDataEntryPanelG=new Wtf.Panel({
            layout:'fit',
            border:false,
            bodyStyle:'background:white',
            scope:this,
            items:[this.usergrid]
        });
        this.add(this.MainDataEntryPanelG);
        this.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
    },
     exportReport: function(flag, exportType, docName,empid,cname,stdate,enddate){
        var selIds = "";
        var colHeader = "{\"data\": []}";
        if (flag==1){
            colHeader = "{\"data\": [\"No\",\"From\", \"To\", \"Duration\", \"Reason\", "+
        "\"Type Of Leave\", \"Paid\", \"LPW\", \"Employee Signature\", \"Approver Signature\", \"Balance\"]}";
        }
        var url =  "Payroll/Date/Salary/exportPDF.py?" +"&flag=" + flag +
        "&colHeader=" + colHeader+
        "&userIDs="+ selIds +
        "&reportname="+ docName +
        "&exporttype=" + exportType+
        "&empid="+empid+
        "&cname="+cname+
        "&stdate="+stdate+
        "&cdomain="+subDomain+
        "&flagpdf="+"datewise"+
        "&enddate="+enddate;
        setDldUrl(url);
    }
});
