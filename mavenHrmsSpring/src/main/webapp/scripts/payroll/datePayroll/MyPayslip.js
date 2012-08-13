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
Wtf.MyPayslip = function(config){
    Wtf.MyPayslip.superclass.constructor.call(this,config);
    config.title="MyPayslip2";
};
Wtf.extend(Wtf.MyPayslip,Wtf.Panel,{
    onRender : function(config){
        Wtf.MyPayslip.superclass.onRender.call(this,config);
        
        this.usersRecords = new Wtf.data.Record.create([{
            name: 'historyid'
        },{
            name: 'userid'
        },{
            name: 'accno'
        },{
            name:'startdate',
            type:'date'
        },{
            name:'enddate',
             type:'date'
        },{
            name:'frequency'
        },{
            name:'gross'
        },{
            name:'tax'
        },{
            name:'deduction'
        },{
            name:'net'
        },{
            name:'design'
        },{
            name:'name'
        },{
            name:'unpaidleavesAmount'
        },{
            name:'incomeTax'
        },{
            name:'absence'
        },{
            name:'month'
        }]);

        this.userStore = new Wtf.data.Store({
            reader:new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.usersRecords),
            url:"Payroll/Date/getPayslips.py",
            baseParams:{
        		userid:this.selectedUserID
        	}
        });
        
        calMsgBoxShow(202,4,true);        
        this.userStore.load({
            scope:this,
            params:{
                start:0,
                limit:15
            }
        });
        this.userStore.on("load",function(){
            WtfGlobal.closeProgressbar();
        },this);
        
        this.rowNo=new Wtf.grid.RowNumberer();
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();

        this.gridcmodel= new Wtf.grid.ColumnModel([this.selectionModel,this.rowNo,{
            header: WtfGlobal.getLocaleText("hrms.common.Month"),
            dataIndex: 'month',            
            sortable: true,
            groupable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'startdate',
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
            header: WtfGlobal.getLocaleText("hrms.common.Earnings"),
            dataIndex: 'gross',
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
            dataIndex: 'deduction',
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{ 
            header: WtfGlobal.getLocaleText("hrms.common.Taxes"),
            dataIndex: 'tax',
            sortable: true,
            align:'right',
            groupable: true,
            renderer:function(val){
                if(val!=null){
                    return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                }
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.payroll.netpay"),
            dataIndex: 'net',            
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
        		this.userStore.load({params:{start:0,limit:this.myPayslipGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.myPayslipGrid.id).setValue("");
        	}
     	}));
        
        this.details = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails"),
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.SalaryDetails.tooltip"),
            scope:this,
            minWidth:100,
            disabled:true,
            iconCls:getButtonIconCls(Wtf.btype.reportbutton),
            handler:this.salaryDetails
        });
        paybtns.push('-',this.details);
        
        this.myprint=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip"),
            scope:this,
            disabled:true,
            iconCls:"pwnd printIcon",
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip.tooltip"),
            handler:function(){
        		this.getData();
        		window.open("Payroll/Salary/printSalarySlip.py?"+"userid="+this.userid+"&startdate="+this.startdate.format("Y-m-d")+"&enddate="+this.enddate.format("Y-m-d")+"&historyid="+this.historyid,"mywindow","menubar=1,resizable=1,scrollbars=1");
        	}
        });
        
        this.download = new Wtf.Toolbar.Button({
			text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
			scope:this,
            disabled:true,
            iconCls:"pwnd downloadIcon",
            minWidth:110,
			tooltip:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip.tooltip"),
			handler:function(){
        		this.getData();
        		Wtf.get('downloadframe').dom.src = "Payroll/Salary/exportSalarySlip.py?"+"userid="+this.userid+"&startdate="+this.startdate.format("Y-m-d")+"&enddate="+this.enddate.format("Y-m-d")+"&historyid="+this.historyid;
			}
		});
        paybtns.push('-',this.download,'-',this.myprint);
        
        this.myPayslipGridSM = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true,
            scope:this
        });
        
        this.myPayslipGrid= new Wtf.KwlGridPanel({
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
            store: this.userStore,
            displayInfo:true,
            cm: this.gridcmodel,
            region:'west',
            scope:this,
            width:400,
            id:'myPayslipGrid'+this.selectedUserID,            
            sm:this.myPayslipGridSM,
            tbar:paybtns
        });        
        
        this.innerpanel = new Wtf.Panel({            
            layout : 'fit',
            cls : 'backcolor',
            border : false,
            items:[new Wtf.Panel({
                border:false,
                autoLoad:false,
                paging:false,
                layout:'fit',
                items:[this.myPayslipGrid]
            })]
        });

        this.add(this.innerpanel);
        this.myPayslipGridSM.on("selectionchange",function(){
        	if(this.myPayslipGridSM.getCount()==1){
        		this.details.setDisabled(false);
        		this.myprint.setDisabled(false);
        		this.download.setDisabled(false);
        	}else{
        		this.details.setDisabled(true);
        		this.myprint.setDisabled(true);
        		this.download.setDisabled(true);
        	}	
        },this);
    },
  
    salaryDetails:function(){
    	this.getData();
    	this.mainTabId = Wtf.getCmp("as");
        this.payslip = Wtf.getCmp(this.id+"payslipTab"+this.startdate);
        if(this.payslip == null){
            this.payslip = new Wtf.resourcePayslip({
                layout:"fit",
                scope:this,
                closable:true,
                iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                border:false,
                id:this.id+"payslipTab"+this.startdate,
                title:WtfGlobal.getLocaleText({key:"hrms.payroll.SalaryDetailsofmonth",params:[this.startdate.format('F')]}),
                userid:this.userid,
                historyid:this.historyid,
                startdate:this.startdate.format("Y-m-d"),
                enddate:this.enddate.format("Y-m-d"),
                ename:this.name,
                accno:this.accno,
                frequency:this.frequency,
                unpaidleavesAmount:this.unpaidleavesAmount,
                incomeTax:this.incomeTax,
                absence:this.absence
            });
            this.mainTabId.add(this.payslip);
        }
        this.mainTabId.setActiveTab(this.payslip);
        this.mainTabId.doLayout();
    },
    
    getData:function(){
    	var sm = this.myPayslipGridSM.getSelected();
    	this.historyid=sm.get('historyid');
    	this.userid=sm.get('userid');
    	this.accno=sm.get('accno');
        this.gross=sm.get('gross');
        this.tax=sm.get('tax');
        this.deduction=sm.get('deduction');
        this.design=sm.get('design');
        this.startdate=sm.get('startdate');
        this.enddate=sm.get('enddate');
        this.name=sm.get('name');
        this.unpaidleavesAmount=sm.get('unpaidleavesAmount');
        this.incomeTax=sm.get('incomeTax');
        this.absence=sm.get('absence');
        this.frequency=sm.get('frequency');
    }
});
