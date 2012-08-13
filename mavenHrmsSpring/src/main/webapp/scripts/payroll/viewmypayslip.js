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

Wtf.viewmypayslip = function(config){
    Wtf.viewmypayslip.superclass.constructor.call(this,config);
    config.title="viewmypayslip2";
};
Wtf.extend(Wtf.viewmypayslip,Wtf.Panel,{
    onRender : function(config){
        Wtf.viewmypayslip.superclass.onRender.call(this,config);
        this.maxUsers = 0;
        this.costPerUser = 0;
        this.count = 0;

        this.usersRecMS = new Wtf.data.Record.create([
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
            name:'stdate',
            type:'date'
        },
        {
            name:'enddate',
             type:'date'
        },
        {
            name:'showborder'
        },
        {
            name:'histid'
        }
        ]);

        var empid="";
        if(this.profile){
          empid=this.userid
        }
        this.userstoreMS = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.usersRecMS),
            baseParams:{
                type:'viewmypayslip',
                empid:empid
            },
            url: Wtf.req.base + 'PayrollHandler.jsp?'
        });
        calMsgBoxShow(202,4,true);        
        this.userstoreMS.load({
            scope:this,
            params:{
                start:0,
                limit:15
            }
        });
        this.userstoreMS.on("load",function(){
            if(msgFlag==1)
            WtfGlobal.closeProgressbar();
        },this);
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.rowNo=new Wtf.grid.RowNumberer();
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();

        this.gridcmodel= new Wtf.grid.ColumnModel([this.selectionModel,this.rowNo,
       {
            header: WtfGlobal.getLocaleText("hrms.common.Month"),
            dataIndex: 'month',            
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'stdate',
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),
            dataIndex: 'enddate',
            align:'center',
            renderer:WtfGlobal.dateonlyRenderer,
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Earning"),
            dataIndex: 'Wage',
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Deductions"),
            dataIndex: 'Deduc',
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{ 
            header:WtfGlobal.getLocaleText("hrms.common.Taxes"),
            dataIndex: 'Tax',
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.payroll.netpay"),
            dataIndex: 'Salary',            
            scope:this,
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return '<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>';
                }
            }
        }],this);
        var paybtns=[];
        
        paybtns.push('-',new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.userstoreMS.load({params:{start:0,limit:this.salusergridMS.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.salusergridMS.id).setValue("");
        	}
     	}));
        
        if(!this.profile){
        paybtns.push('-',new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails"),
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails.tooltip"),
                scope:this,
                minWidth:100,
                disabled:true,
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                handler:function(){
                     var rec =this.salusergridMS.getSelectionModel().getSelections();
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
                                TempId:this.TempId,
                                title:WtfGlobal.getLocaleText({key:"hrms.payroll.SalaryDetailsofmonth",params:[month]}),
                                ename:this.ename,
                                accno:this.accno,
                                salary:this.salary,
                                tax:this.tax,
                                empid:this.empid,
                                deduc:this.deduc,
                                cursymbol:this.currency,
                                fixedsal:this.fixedsal,
                                design:this.design,
                                flag:"employee",
                                histid:this.histid,
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
                }
            }));
        }

        this.myprint=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip"),
            scope:this,
            disabled : true,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip.tooltip"),
            handler : function(){
                var rec=this.salusergridMS.getSelectionModel().getSelections();
                var stdate=rec[0].get('stdate').format('m/d/Y');
                var enddate=rec[0].get('enddate').format('m/d/Y');
                var docname=rec[0].get('EName')+"_"+rec[0].get('stdate').format('FY')+"";
                this.exportReport(1, "print", docname,rec[0].get('empid'),companyName,stdate,enddate,rec);
                this.empid=null;
                this.salusergridMS.getSelectionModel().clearSelections();

            }
        });
        
        paybtns.push('-',new Wtf.Toolbar.Button({
				text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
				scope:this,
                                disabled:true,
				iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
                                minWidth:110,
				tooltip :WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip.tooltip"),
				handler : function(){
                        var rec=this.salusergridMS.getSelectionModel().getSelections();
                        var stdate=rec[0].get('stdate').format('m/d/Y');
                        var enddate=rec[0].get('enddate').format('m/d/Y');
                        var docname=rec[0].get('EName')+"_"+rec[0].get('stdate').format('FY')+"";
                        this.exportReport(1, "pdf", docname,rec[0].get('empid'),companyName,stdate,enddate,rec);
						this.empid=null;
						this.salusergridMS.getSelectionModel().clearSelections();
				}
			}),'-',this.myprint
        );
        this.summary = new Wtf.ux.grid.GridSummary();
        this.salusergridMS= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            border:false,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.SearchbyMonth"),
            searchField:"month",
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.NoPaysliptodownload"))
            },
            store: this.userstoreMS,
            displayInfo:true,
            cm: this.gridcmodel,
            region:'west',
            scope:this,
            width:400,
            id:'salusergridMS',            
            sm: new Wtf.grid.CheckboxSelectionModel({
                singleSelect:true,
                scope:this,
                listeners:{
                    scope:this,
                    rowselect :function(sm,index,record){
                        this.ename=this.salusergridMS.getSelectionModel().getSelected().get('EName');
                        this.accno=this.salusergridMS.getSelectionModel().getSelected().get('AccNo');
                        this.salary=this.salusergridMS.getSelectionModel().getSelected().get('Wage');
                        this.fixedsal=this.salusergridMS.getSelectionModel().getSelected().get('FixedSal');
                        this.tax=this.salusergridMS.getSelectionModel().getSelected().get('Tax');
                        this.deduc=this.salusergridMS.getSelectionModel().getSelected().get('Deduc');
                        this.empid=this.salusergridMS.getSelectionModel().getSelected().get('empid');
                        this.design=this.salusergridMS.getSelectionModel().getSelected().get('design');
                        this.TempId=this.salusergridMS.getSelectionModel().getSelected().get('tempid');
                        this.fromdateemp=this.salusergridMS.getSelectionModel().getSelected().get('stdate');
                        this.todateemp=this.salusergridMS.getSelectionModel().getSelected().get('enddate');
                        this.histid=this.salusergridMS.getSelectionModel().getSelected().get('histid');                    

                    }
                }
            }),
            tbar:paybtns
        });        
        this.UsergridPanel2  = new Wtf.Panel({
            border:false,
            autoLoad:false,
            paging:false,
            layout:'fit',
            items:[this.salusergridMS]
        });
        this.innerpanel2 = new Wtf.Panel({            
            layout : 'fit',
            cls : 'backcolor',
            border : false,
            items:[this.UsergridPanel2]
        });

        this.add(this.innerpanel2);
         this.salusergridMS.getSelectionModel().on("selectionchange",function(){
             if(this.profile){
              WtfGlobal.enableDisableBtnArr(paybtns, this.salusergridMS, [3,5],[]);
             }else{
                 WtfGlobal.enableDisableBtnArr(paybtns, this.salusergridMS, [3,5,7],[]);
             }
        },this);
    },
  
  exportReport: function(flag, exportType, docName,empid,cname,stdate,enddate,rec){
        var selIds = "";
        var showBorder = rec[0].get("showborder");
        var colHeader = "{\"data\": []}";
        if (flag==1){ 
            colHeader = "{\"data\": [\"No\",\"From\", \"To\", \"Duration\", \"Reason\", "+
        "\"Type Of Leave\", \"Paid\", \"LPW\", \"Employee Signature\", \"Approver Signature\", \"Balance\"]}";
        }

        var controller = "";
        if(exportType=='print'){
        	controller = "Payroll/Date/Salary/printHTML.py?";
        }else{
        	controller = "Payroll/Date/Salary/exportPDF.py?";
        }
        
        var url =  controller +"&flag=" + flag +
        "&colHeader=" + colHeader+
        "&userIDs="+ selIds +
        "&reportname="+ docName +
        "&exporttype=" + exportType+
        "&empid="+empid+
        "&cname="+cname+
        "&stdate="+stdate+
        "&showborder="+showBorder+
        "&cdomain="+subDomain+
        "&flagpdf="+"datewise"+
        "&enddate="+enddate;

        setDldUrl(url, exportType);
    }

});
