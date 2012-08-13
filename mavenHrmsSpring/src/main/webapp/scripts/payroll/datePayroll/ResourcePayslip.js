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
Wtf.resourcePayslip=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    config.layout="fit";    
    config.closable=true;
    this.modifiedflag=0;
    Wtf.resourcePayslip.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.resourcePayslip,Wtf.Panel,{
    initComponent:function(config){
        Wtf.resourcePayslip.superclass.initComponent.call(this,config);
        this.jsondata = "";
        this.salgen=0;
        var symbol=WtfGlobal.getCurrencySymbol();
        this.empform=new Wtf.Panel({
            height:110,
            columnWidth:1,
            border:false,
            bodyStyle:'margin-left:33%;margin-top:1%',
            scope:this,
            items:[{
                    height:100,
                    width:400,
                    scope:this,
                    layout:'form',
                    bodyStyle:'padding-top:20px;padding-left:50px;',
                    items:[{
                            xtype:'textfield',
                            width:'70%',
                            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employee.name"),
                            value:this.ename,
                            readOnly :true
                        },{
                            xtype:'textfield',
                            width:'70%',
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.AccountNumber"),
                            readOnly :true,
                            value:this.accno
                        }]
                }]

        });
        
        this.storeURLEC= "Payroll/Date/getSalaryComponents.py?userid="+this.userid+"&enddate="+this.enddate+"&type="+0+"&frequency="+this.frequency+"&historyid="+this.historyid;
        this.storeURLE= "Payroll/Date/getSalaryComponents.py?userid="+this.userid+"&enddate="+this.enddate+"&type="+1+"&frequency="+this.frequency+"&historyid="+this.historyid+"&otherRemuneration="+4;
        this.storeURLD= "Payroll/Date/getSalaryComponents.py?userid="+this.userid+"&enddate="+this.enddate+"&type="+2+"&frequency="+this.frequency+"&historyid="+this.historyid;
        this.storeURLT= "Payroll/Date/getSalaryComponents.py?userid="+this.userid+"&enddate="+this.enddate+"&type="+3+"&frequency="+this.frequency+"&historyid="+this.historyid;
        this.earningPan=new Wtf.Panel({
            columnWidth:0.8,
            bodyStyle:'margin-top:1%',
            border:false,
            scope:this,
            items:[this.wages=new Wtf.resourcePayslipGrid({
                    type:WtfGlobal.getLocaleText("hrms.common.Earning"),
                    height:160,
                    scope:this,
                    id:'Earning'+this.id,
                    Data:'Wage',
                    storeURL:this.storeURLE,
                    startdate:this.startdate,
                    enddate:this.enddate
                })]
        });

        this.deductionPan=new Wtf.Panel({
            columnWidth:0.8,
            border:false,
            bodyStyle:'margin-top:1%',
            scope:this,
            items:[this.diduces=new Wtf.resourcePayslipGrid({
                    type:WtfGlobal.getLocaleText("hrms.common.Deductions"),
                    id:'Deduction'+this.id,
                    scope:this,
                    height:150,
                    flag:this.flag,
                    Data:'Deduc',
                    storeURL:this.storeURLD,
                    startdate:this.startdate,
                    enddate:this.enddate                                   
                })]
        });

        this.taxPan=new Wtf.Panel({
            columnWidth:0.8,
            bodyStyle:'margin-top:1%',
            border:false,
            scope:this,
            items:[this.taxes=new Wtf.resourcePayslipGrid({
                    type:WtfGlobal.getLocaleText("hrms.common.Tax"),
                    id:'Tax'+this.id,
                    height:150,
                    scope:this,
                    flag:this.flag,
                    Data:'Tax',
                    storeURL:this.storeURLT,
                    startdate:this.startdate,
                    enddate:this.enddate
                })]
        });
        
        this.empContributionPan=new Wtf.Panel({
            columnWidth:0.8,
            bodyStyle:'margin-top:1%',
            border:false,
            scope:this,
            items:[this.empcontrib=new Wtf.resourcePayslipGrid({
                    type:WtfGlobal.getLocaleText("hrms.payroll.EmployerContribution"),
                    id:'Newgrid'+this.id,
                    height:150,
                    scope:this,
                    flag:this.flag,
                    mappingid:this.mappingid,
                    Data:'EC',
                    storeURL:this.storeURLEC,
                    startdate:this.startdate,
                    enddate:this.enddate
                })]
        });

        this.totalAmountPan=new Wtf.Panel({
            columnWidth:0.8,
            layout:'form',
            border:false,
            labelWidth:65,
            bodyStyle:'margin-left:81%;margin-bottom:2%;margin-top:2%',
            scope:this,
            items:[this.tot=new Wtf.form.TextField({ 
                    border:true,
                    scope:this,
                    cls:'textfstyle',
                    readOnly:true,
                    fieldLabel:'<span style=\"padding-left:-3px;font-family:Lucida Sans Unicode;\"><b>'+WtfGlobal.getLocaleText("hrms.payroll.TOTAL")+'</b>('+symbol+')</span>',
                    allowDecimals:true,
                    labelSeparator:'',
                    value:0,
                    height:20,
                    width:120,
                    bodyStyle:'margin-left:10%;background:white;border-right:3px',
                    decimalPrecision:2
                })
            ]
        });

        this.wages.storetax.on("load",function(c,r,i){
            this.setTotalSal();                                       
        },this);
        this.diduces.storetax.on("load",function(c,r,i){
            this.setTotalSal();
        },this);
        this.taxes.storetax.on("load",function(c,r,i){
            this.setTotalSal();
        },this);
        this.fromdateemp=new Wtf.form.DateField({
            width:155,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            disabled:true,
            value:this.startdate
        });
        this.todateemp= new Wtf.form.DateField({
            width:155,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            disabled:true,
            format:'m/d/Y',
            value:this.enddate
        });
        this.dwnldpay=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
            iconCls:"pwnd downloadIcon",
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.click.download.payslip.pdf"),
            scope:this,
            handler:function(){
        		Wtf.get('downloadframe').dom.src = "Payroll/Salary/exportSalarySlip.py?"+"userid="+this.userid+"&startdate="+this.startdate+"&enddate="+this.enddate+"&historyid="+this.historyid;
            }
        });

        this.myprint=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip"),
            scope:this,
            iconCls:"pwnd printIcon",
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.print.payslip"),
            handler : function(){
        		window.open("Payroll/Salary/printSalarySlip.py?"+"userid="+this.userid+"&startdate="+this.startdate+"&enddate="+this.enddate+"&historyid="+this.historyid,"mywindow","menubar=1,resizable=1,scrollbars=1");
            }
        });

        var btns=[];
        btns.push(WtfGlobal.getLocaleText("hrms.common.start.date")+':',this.fromdateemp,WtfGlobal.getLocaleText("hrms.common.end.date")+':',this.todateemp);
        if(this.flag!="employee"){
            btns.push('-',this.dwnldpay,'-',this.myprint);
        }
        if(this.reviewPayrollFlag){
            btns=[];
            
            this.linkComponent=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.manage.amount"),
                scope:this,
                iconCls:"pwndHRMS manageAmountIcon",
                tooltip :WtfGlobal.getLocaleText("hrms.payroll.update.amount.components"),
                handler : function(){
                    var userid="";
                    var emparr=this.generatedSalaryListGrid.getSelectionModel().getSelections();
                    
                    userid=emparr[0].get('resource');
                    var absence=emparr[0].get('absence');
                    
                    this.linkComponentWin=new Wtf.linkComponentWin({
                        iconCls:getButtonIconCls(Wtf.btype.winicon),
                        layout:'fit',
                        closable:true,
                        width:760,
                        title:WtfGlobal.getLocaleText("hrms.payroll.link.payroll.component"),
                        height:500,
                        border:false,
                        empGDS:this.generatedSalaryStore,
                        modal:true,
                        userid:userid,
                        startdate:this.startdate,
                        enddate:this.enddate,
                        frequency:this.frequency,
                        scope:this,
                        plain:true,
                        allempGrid:this.generatedSalaryListGrid,
                        reviewPayrollFlag:this.reviewPayrollFlag,
                        absence:absence,
                        historyid:this.historyid
                    });
                    this.linkComponentWin.show();

                    this.linkComponentWin.on("reloadComponents", this.reloadComponents, this);
                }
            });

            if(this.payrollStatus==1 ||this.payrollStatus==2 ||this.payrollStatus==4){
                btns.push('-');
                btns.push(this.linkComponent);
            }
            
        }
        var bbarBtns = [];
        this.MainDataEntryPanel=new Wtf.Panel({
            id:this.id+'payslip',
            layout:'column',
            border:false,
            bodyStyle:'background:white',
            scope:this,
            autoScroll:true,
            items:[this.empform,this.earningPan,this.deductionPan,this.taxPan,this.empContributionPan,this.totalAmountPan],
            tbar:btns,
            buttonAlign:'right',
            bbar:bbarBtns
        });
        
        this.add(this.MainDataEntryPanel);
        this.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
    },
    reloadComponents:function(a){
        this.wages.storetax.reload();
        this.diduces.storetax.reload();
        this.taxes.storetax.reload();
        this.empcontrib.storetax.reload();
        
    },
    setTotalSal:function(){
        var a1=0;
        var a2=0;
        var a3=0;
        var a=0;
        a2=parseFloat((this.diduces.total.getValue()).replace(/,/g,''));
        a1=parseFloat((this.wages.total.getValue()).replace(/,/g,''));
        a3=parseFloat((this.taxes.total.getValue()).replace(/,/g,''));
        a=a1-a2-a3;
        this.tot.setValue(WtfGlobal.currencyRenderer2(parseFloat(a).toFixed(2)));
    }
});

/**
 * Component for Earning/Deduction/Tax/Employer Contribution Grid
 */
Wtf.resourcePayslipGrid=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    config.border=false;
    Wtf.resourcePayslipGrid.superclass.constructor.call(this,config);
},

Wtf.extend(Wtf.resourcePayslipGrid,Wtf.Panel,{
    initComponent:function(config){

        Wtf.resourcePayslipGrid.superclass.initComponent.call(this,config);

        this.rateEditor=new Wtf.form.TextField();
        this.amountEditor=new Wtf.form.TextField();
        this.cmtax=new Wtf.grid.ColumnModel([
            {
                header:this.type,
                dataIndex: 'type',
                sortable: true,
                autoWidth:true
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.percent.of"),
                dataIndex: 'computeon',
                align: 'center',
                sortable: true,
                hidden:(this.flag!="employee")?false:true,
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.payroll.Amount"),
                dataIndex:'amount',
                autoWidth:true,
                scope:this,
                sortable: true,
                align:'right',
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            }
            ]);

        this.fieldstax=[{
            name:'id'
        },{
            name:'type'
        },{
            name:'amount',
            type:'float'
        },{
            name:'computeon'
        }];


        this.fieldstaxx=Wtf.data.Record.create(this.fieldstax);
        this.readertax=new Wtf.data.KwlJsonReader({
            root:this.Data,
            totalProperty:'totalcount'
        },this.fieldstaxx);
        this.storetax= new Wtf.data.Store({
            scope:this,
            url : this.storeURL,
            method:'GET',
            reader: this.readertax
        });
        this.storetax.load({
            params:{
        		userid:this.userid,
                startdate:this.startdate,
                enddate:this.enddate,
                type:this.type
            }
        });

        var btns=[];
        btns.push('->',WtfGlobal.getLocaleText("hrms.payroll.TOTAL")+'<span align=\"right\" style="font-family:Lucida Sans Unicode;">('+WtfGlobal.getCurrencySymbol()+')</span>',
            this.total= new Wtf.form.TextField({
                border:false,
                scope:this,
                cls:'textfstyle',
                width:100,
                value:0,
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.TOTAL"),
                readOnly:true,
                id:this.id+'gridtotal',
                height:16,
                bodyStyle:'background:white'
            }));
        this.grid = new Wtf.grid.EditorGridPanel({
            scope:this,
            bodyStyle:'width:99.7%',
            cm:this.cmtax,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            store:this.storetax,
            autoScroll:true,
            viewConfig: {
                forceFit: true
            },
            height:this.height,
            clicksToEdit:1,
            stripeRows: true,
            bbar:btns
        });

        this.storetax.on("load",function(){
            this.amtot=0;
            for(i=0;i<this.storetax.getCount();i++)
                {
                    this.amtot=this.amtot+this.storetax.getAt(i).get('amount');
                }
            this.total.setValue(WtfGlobal.currencyRenderer2(this.amtot));
        },this);
        
        this.pan1=new Wtf.Panel({
            height:this.height,
            border:false,
            bodyStyle:'margin-left:25%',
            scope:this,
            items:[this.grid]
        });

        this.add(this.pan1);

        this.doLayout();
        this.pan1.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
    }

}); 
