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
Wtf.EmpPayslip=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    config.layout="fit";    
    config.closable=true;
    this.modifiedflag=0;
    Wtf.EmpPayslip.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.EmpPayslip,Wtf.Panel,{
    initComponent:function(config){
        Wtf.EmpPayslip.superclass.initComponent.call(this,config);
        this.addEvents({
        'gridload':true        
    });
        this.jsondata = "";
        var cursymbol=this.cursymbol;
        var a=0.000;
        var a1=0.000;
        var a2=0.000;
        var a3=0.000;
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
        if(this.flag=="employee")
        {
            this.storeURL1= Wtf.req.base + "PayrollHandler.jsp?type=HistWages&TempId="+this.TempId+"&histid="+this.histid;
            this.storeURL3= Wtf.req.base + "PayrollHandler.jsp?type=HistTaxes&TempId="+this.TempId+"&histid="+this.histid;
            this.storeURL2= Wtf.req.base + "PayrollHandler.jsp?type=HistDeduces&TempId="+this.TempId+"&histid="+this.histid;
            this.storeURL4= "Payroll/EmpContrib/getHistEmpContrib.py?histid="+this.histid;
        } else {
            this.storeURL1= "Payroll/Wage/getwagesPerTempid.py?TempId="+this.TempId+"&salary="+this.fixedsal+"&empid="+this.empid+"&grouper=b";
            this.storeURL2= "Payroll/Deduction/getDeducPerTempid.py?TempId="+this.TempId+"&salary="+this.fixedsal+"&empid="+this.empid+"&stdate="+this.stdate.format("m/d/Y")+"&empid="+this.empid+"&grouper=b";
            this.storeURL3= "Payroll/Tax/getTaxPerTempid.py?TempId="+this.TempId+"&salary="+this.fixedsal+"&empid="+this.empid+"&grouper=b&firequery=1&netSalary="+this.netSalary;
            this.storeURL4= "Payroll/EmpContrib/getEmpContribPerTempid.py?TempId="+this.TempId+"&salary="+this.fixedsal+"&empid="+this.empid+"&grouper=b&firequery=1";
        }
        this.pan1=new Wtf.Panel({
            columnWidth:0.8,
            bodyStyle:'margin-top:1%',
            border:false,
            scope:this,
            items:[this.wages=new Wtf.PayslipGrid({
                    type:'Earning',
                    Localetype:"hrms.common.Earning",
                    height:160,
                    scope:this,
                    id:'Earning'+this.id,
                    TempId:this.TempId,
                    empid:this.empid,
                    flag:this.flag,
                    stdate:this.stdate,
                    salary:this.salary,
                    mappingid:this.mappingid,
                    deduc:this.deduc,
                    fixedsal:this.fixedsal,
                    Data:'Wage',
                    total:this.salary,
                    storeURL:this.storeURL1,
                    cursymbol:cursymbol,
                    paycyclestart:this.paycyclestart,
                    paycycleend:this.paycycleend,
                    paycycleactualstart:this.stdate.format("m-d-Y"),
                    paycycleactualend:this.enddate.format("m-d-Y"),
                    ischanged:this.ischanged
                })]
        });

        this.wages.on("storeload",function(a, b, c){
             this.wagestatus=1;
             if(this.deducstatus==1&&this.wagestatus==1&&!(this.salaryGenerated =='true')){
                this.getdepcomp();
             }
        },this);

        this.wages.on("datamodified",function(){
            this.modifiedflag=1;
        },this);

        this.pan3=new Wtf.Panel({
            columnWidth:0.8,
            border:false,
            bodyStyle:'margin-top:1%',
            scope:this,
            items:[this.diduces=new Wtf.PayslipGrid({
                    type:'Deduction',
                    Localetype:"hrms.common.Deductions",
                    id:'Deduction'+this.id,
                    scope:this,
                    height:150,
                    TempId:this.TempId,
                    empid:this.empid,
                    flag:this.flag,
                    stdate:this.stdate,
                    salary:this.salary,
                    Data:'Deduc',
                    fixedsal:this.fixedsal,
                    total:this.deduc,
                    storeURL:this.storeURL2,
                    cursymbol:cursymbol,
                    paycycleactualstart:this.stdate.format("m-d-Y"),
                    paycycleactualend:this.enddate.format("m-d-Y"),
                    paycyclestart:this.paycyclestart,
                    paycycleend:this.paycycleend,
                    ischanged:this.ischanged
                })]
        });

        this.diduces.on("storeload",function(a, b, c){
             this.deducstatus=1;
             if(this.deducstatus==1&&this.wagestatus==1&&!(this.salaryGenerated =='true')){
                this.getdepcomp();
             }
        },this);

        this.diduces.on("datamodified",function(){
            this.modifiedflag=1;
        },this);

        this.pan2=new Wtf.Panel({
            columnWidth:0.8,
            bodyStyle:'margin-top:1%',
            border:false,
            scope:this,
            items:[this.taxes=new Wtf.PayslipGrid({
                    type:'Tax',
                    Localetype:"hrms.common.Tax",
                    id:'Tax'+this.id,
                    salary:this.salary,
                    empid:this.empid,
                    height:150,
                    scope:this,
                    flag:this.flag,
                    stdate:this.stdate,
                    total:this.tax,
                    TempId:this.TempId,
                    fixedsal:this.fixedsal,
                    dtotal:this.deduc,
                    Data:'Tax',
                    storeURL:this.storeURL3,
                    cursymbol:cursymbol,
                    paycycleactualstart:this.stdate.format("m-d-Y"),
                    paycycleactualend:this.enddate.format("m-d-Y"),
                    paycyclestart:this.paycyclestart,
                    paycycleend:this.paycycleend,
                    ischanged:this.ischanged
                })]
        });
        this.taxes.on("storeload",function(){
             this.taxstatus=1;
        },this);
        this.taxes.on("datamodified",function(){
            this.modifiedflag=1;
        },this);

        this.pan5=new Wtf.Panel({
            columnWidth:0.8,
            bodyStyle:'margin-top:1%',
            border:false,
            scope:this,
            items:[this.empcontrib=new Wtf.PayslipGrid({
                    type:'Employer Contribution',
                    Localetype:"hrms.payroll.EmployerContribution",
                    id:'Newgrid'+this.id,
                    salary:this.salary,
                    empid:this.empid,
                    height:150,
                    scope:this,
                    flag:this.flag,
                    stdate:this.stdate,
                    mappingid:this.mappingid,
                    total:this.tax,
                    TempId:this.TempId,
                    fixedsal:this.fixedsal,
                    dtotal:this.deduc,
                    Data:'EC',
                    storeURL:this.storeURL4,
                    cursymbol:cursymbol,
                    paycycleactualstart:this.stdate.format("m-d-Y"),
                    paycycleactualend:this.enddate.format("m-d-Y"),
                    paycyclestart:this.paycyclestart,
                    paycycleend:this.paycycleend,
                    ischanged:this.ischanged
                })]
        });
        this.empcontrib.on("storeload",function(){
            // this.taxstatus=1;
        },this);
        this.empcontrib.on("datamodified",function(){
          //  this.modifiedflag=1;
        },this);

        this.pan4=new Wtf.Panel({
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

        this.cnamedata = [
            [WtfGlobal.getLocaleText("hrms.payroll.Weekly"),'01'],
            [WtfGlobal.getLocaleText("hrms.payroll.Monthly"),'02'],
            [WtfGlobal.getLocaleText("hrms.payroll.Quarterly"),'03'],
            [WtfGlobal.getLocaleText("hrms.payroll.Yearly"),'04']
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
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.Selectasalaryfrequency"),
            selectOnFocus:true,
            width:150,
            value:'Monthly',
            height:200,
            triggerAction :'all',
            listeners :{
                scope:this,
                select:function(TaxName,rec,i){
                }
            }
        });
        this.fromdateemp=new Wtf.form.DateField({
            width:155,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            disabled:true,
            value:this.stdate
        });
        this.todateemp= new Wtf.form.DateField({
            width:155,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            disabled:true,
            format:'m/d/Y',
            value:this.enddate
        });
        this.xbt2=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.GenerateSalary"),
            scope:this,
            minWidth:110,
            id:this.id+'GenerateSalary',
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            handler:function(){
                if(this.fromdateemp.getRawValue()=="" || this.todateemp.getRawValue()=="" || this.fromdateemp.getRawValue()>this.todateemp.getRawValue())
                {
                    calMsgBoxShow(14,1);
                } else
                {
                    this.Wagejsondata = "";
                    for(var ix=0;ix<=this.wages.storetax.getCount()-1;ix++)
                    {
                        this.WStoreTax=this.wages.storetax.getAt(ix);
                        this.Wagejsondata += "{'type':'" + this.WStoreTax.get('Type')+ "',";
                        this.Wagejsondata += "'Id':'" +this.WStoreTax.get('Id')+ "',";
                        this.Wagejsondata += "'amount':'" +parseFloat(this.WStoreTax.get('amount')).toFixed(2)+ "',";
                        if(this.WStoreTax.get('ratetype')==0){
                            this.Wagejsondata += "'Rate':'0',";
                        } else{
                            this.Wagejsondata += "'Rate':'"+parseFloat(this.WStoreTax.get('Rate')).toFixed(2)+"',";
                        }
                        this.wagetot=this.wages.total.getValue().replace(/,/g,'');
                        this.Wagejsondata+= "'total':'" +this.wagetot+ "'},";
                    }
                    this.trmLen1 =this.Wagejsondata.length - 1;
                    this.WageJson = this.Wagejsondata.substr(0,this.trmLen1);
                    this.Deducjsondata = "";
                    for(var ix=0;ix<=this.diduces.storetax.getCount()-1;ix++)
                    {
                        this.DStoreTax=this.diduces.storetax.getAt(ix);
                        this.Deducjsondata += "{'type':'" + this.DStoreTax.get('Type')+ "',";
                        this.Deducjsondata += "'Id':'" +this.DStoreTax.get('Id')+ "',";
                        this.Deducjsondata += "'amount':'" +parseFloat(this.DStoreTax.get('amount')).toFixed(2)+ "',";
                        if(this.DStoreTax.get('ratetype')==0){
                            this.Deducjsondata += "'Rate':'0',";
                        } else{
                            this.Deducjsondata += "'Rate':'"+parseFloat(this.DStoreTax.get('Rate')).toFixed(2)+"',";
                        }
                        this.deductot=this.diduces.total.getValue().replace(/,/g,'');
                        this.Deducjsondata+= "'total':'" +this.deductot+ "'},";
                    }
                    this.trmLen2 =this.Deducjsondata.length - 1;
                    this.DeducJson = this.Deducjsondata.substr(0,this.trmLen2);
                    this.taxesjsondata = "";
                    for(var ix=0;ix<=this.taxes.storetax.getCount()-1;ix++)
                    {
                        this.TStoreTax=this.taxes.storetax.getAt(ix);
                        this.taxesjsondata += "{'type':'" + this.TStoreTax.get('Type')+ "',";
                        this.taxesjsondata += "'Id':'" +this.TStoreTax.get('Id')+ "',";
                        this.taxesjsondata += "'amount':'" +parseFloat(this.TStoreTax.get('amount')).toFixed(2)+ "',";
                        if(this.TStoreTax.get('ratetype')==0){
                            this.taxesjsondata += "'Rate':'0',";
                        } else{
                            this.taxesjsondata += "'Rate':'"+parseFloat(this.TStoreTax.get('Rate')).toFixed(2)+"',";
                        }
                        this.taxtot=this.taxes.total.getValue().replace(/,/g,'');
                        this.taxesjsondata+= "'total':'" +this.taxtot+ "'},";
                    }
                    this.trmLen3 =this.taxesjsondata.length - 1;
                    this.taxesJson = this.taxesjsondata.substr(0,this.trmLen3);
                    this.ecjsondata = "";
                    for(var ix=0;ix<=this.empcontrib.storetax.getCount()-1;ix++)
                    {
                        this.ECStoreTax=this.empcontrib.storetax.getAt(ix);
                        this.ecjsondata += "{'type':'" + this.ECStoreTax.get('Type')+ "',";
                        this.ecjsondata += "'amount':'" +parseFloat(this.ECStoreTax.get('amount')).toFixed(2)+ "',";
                        if(this.ECStoreTax.get('ratetype')==0){
                            this.ecjsondata += "'Rate':'0',";
                        } else{
                            this.ecjsondata += "'Rate':'"+parseFloat(this.ECStoreTax.get('Rate')).toFixed(2)+"',";
                        }
                        this.ectot=this.empcontrib.total.getValue().replace(/,/g,'');
                        this.ecjsondata+= "'total':'" +this.ectot+ "'},";
                    }
                    this.trmLen4 =this.ecjsondata.length - 1;
                    this.ecJson = this.ecjsondata.substr(0,this.trmLen4);
                    this.netsal=this.tot.getValue().replace(/,/g,'');
                    this.fixedsal=this.fixedsal.replace(/,/g,'');
                    Wtf.Ajax.requestEx({
                        //url: Wtf.req.base + "PayrollHandler.jsp" ,
                        url: "Emp/setPayrollforTemp.py" ,
                        method:'post',
                        params:{
                            save:'true',
                            saveType:'PayHistory',//Create Payslip Per Template
                            empid:this.empid,
                            empname:this.ename,
                            design:this.design,
                            gross:this.fixedsal,
                            net:this.netsal,
                            wagetot:this.wagetot,
                            taxtot:this.taxtot,
                            deductot:this.deductot,
                            ectot:this.ectot,
                            mappingid:this.mappingid,
                            tempid:this.TempId,
                            WageJson:this.WageJson,
                            DeducJson:this.DeducJson,
                            taxesJson:this.taxesJson,
                            ECJson:this.ecJson,
                            stdate:this.fromdateemp.getRawValue(),
                            enddate:this.todateemp.getRawValue()
                        }
                    },
                    this,
                    function(req,res){
                        if(req.value.toString()!="success"){
                            calMsgBoxShow(15,0);
                        }
                        if(req.value.toString()=="success"){
                            calMsgBoxShow(16,0);
                            this.taxes.editrate.disable();
                            this.wages.editrate.disable();
                            this.diduces.editrate.disable();
                            this.empcontrib.editrate.disable();
                            Wtf.getCmp(this.id+'updateslip').disable();
                            Wtf.getCmp(this.id+'GenerateSalary').disable();
                            this.fromdateemp.disable();
                            this.todateemp.disable();
                            this.salgen=1;
                        }
                    },
                    function(req,res){
                    }
                );
                }
            }
        });
        this.xbt1=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.UpdateAmount"),
            scope:this,
            id:this.id+'updateslip',
            iconCls:"pwndCommon updatebuttonIcon",
            minWidth:110,
            handler:function()
            {
                Wtf.Msg.show({
                    title:  WtfGlobal.getLocaleText("hrms.common.warning"),
                    scope: this,
                    animEl: 'elId',
                    icon: Wtf.MessageBox.QUESTION,
                    msg: WtfGlobal.getLocaleText("hrms.payroll.Wouldliketoupdatepayslip"),
                    buttons: Wtf.Msg.YESNO,
                    fn: function(btn,value){
                        if(btn=='yes'){
                            var salary;
                            var maxtot1=0;
                            this.clickflag=0;
                            salary=this.wages.total.getValue()-this.diduces.total.getValue();
                            for(var i=0; i <this.taxes.storetax.getCount(); i++){
                                this.taxes.storetax.getAt(i).set("amount",parseFloat((this.taxes.storetax.getAt(i).get("Rate")*salary)/100));
                            }
                            for(var i=0; i <this.taxes.storetax.getCount(); i++){
                                maxtot1+=parseFloat(this.taxes.storetax.getAt(i).get("amount"));
                            }
                            this.taxes.total.setValue(parseFloat(maxtot1).toFixed(2));
                            a1=parseFloat(this.wages.total.getValue());
                            a2=parseFloat(this.diduces.total.getValue());
                            a3=parseFloat(this.taxes.total.getValue());
                            a=a1-a2-a3;
                            this.tot.setValue(parseFloat(a).toFixed(2));
                            this.clickflag=1;
                            this.wages.storetax.commitChanges();
                            this.diduces.storetax.commitChanges();
                            this.taxes.storetax.commitChanges();
                        } else{
                            this.wages.storetax.load();
                            this.diduces.storetax.load();
                            this.taxes.storetax.load();
                        }
                    }
                });
            }
        });
        this.dwnldpay=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.DownloadPayslip"),
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            disabled:true,
            scope:this,
            handler:function(){
                var docname=this.ename+"_"+this.paycyclestart+"";
                this.exportReport(1, "pdf", docname,this.empid,companyName,this.fromdateemp.getRawValue(),this.todateemp.getRawValue());
            }
        });

        this.myprint=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip"),
            scope:this,
            disabled : true,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.PrintPayslip.tooltip"),
            handler : function(){
                var docname=this.ename+"_"+this.paycyclestart+"";
                this.exportReport(1, "print", docname,this.empid,companyName,this.fromdateemp.getRawValue(),this.todateemp.getRawValue());

            }
        });
        
        if(this.generated=='1'){
              this.dwnldpay.enable();
              this.myprint.enable();
        }else{
            this.dwnldpay.disable();
            this.myprint.disable();
        }
        var genSalaryText = WtfGlobal.getLocaleText("hrms.payroll.GenerateSalary");
        var genSalaryTextMsg = WtfGlobal.getLocaleText("hrms.payroll.want.to.generate.salary");
        if(Wtf.cmpPref.approvesalary!=undefined && Wtf.cmpPref.approvesalary===true){
            genSalaryText = WtfGlobal.getLocaleText("hrms.payroll.send.for.authorization");
            genSalaryTextMsg = WtfGlobal.getLocaleText("hrms.payroll.want.to.send.it.authorization");
        }
        var btns=[];
        btns.push(WtfGlobal.getLocaleText("hrms.common.start.date")+':',this.fromdateemp,WtfGlobal.getLocaleText("hrms.common.end.date"),this.todateemp);
        if(this.flag!="employee"){
            btns.push(
            '-',
            this.exppayslip=new Wtf.Button({
                text:genSalaryText,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                scope:this,
                handler:function(){
            		
	            	if(isStandAlone){
		        		this.getUnpaidleaves();
		        	}else{
		        		calMsgBoxShow(202,4,true);
			           	var json = new Array();
			            for(var i=0; i<this.arr.length; i++){
			            	var data = {};
			            	data['userid'] = this.arr[i].data.empid;
			            	data['fromDate'] = this.paycyclestart;
			            	data['toDate'] = this.paycycleend;
			            	json.push(data);
			            }
			            
			            Wtf.Ajax.requestEx({
			                url: "Emp/getLeavesFromEleaves.py" ,
			                scope:this,
			                method:'post',
			                params:{
			            		jsondata:Wtf.encode(json)
			                }
			            },this,
			            function(request){
			            	var req = eval('('+request+')');
			            	this.getUnpaidleaves(req);			            	
			            	WtfGlobal.closeProgressbar();
			            },
			            function(response){
			            	WtfGlobal.closeProgressbar();
			            });
		        	}
            	
	            	/*Wtf.MessageBox.show({
	                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),
	                    msg:genSalaryTextMsg,
	                    icon:Wtf.MessageBox.QUESTION,
	                    buttons:Wtf.MessageBox.YESNO,
	                    scope:this,
	                    fn:function(button){
	                        if(button=='yes'){
	                        	this.genpayslipchk(1);
	                        }
	                    }
	                });*/
                }//1 for generation
            }),'-',this.dwnldpay,'-',this.myprint
        );
        }
        var bbarBtns = [];
        if(this.flag!="employee"){
            bbarBtns.push('->',
            this.deletepayslip=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.common.delete"),
                iconCls:getButtonIconCls(Wtf.btype.deletebutton),
                scope:this,
                handler:function(){
                    this.genpayslipchk(0);
                    }//0 for delete
            })
            )
        }
        this.MainDataEntryPanel=new Wtf.Panel({
            id:this.id+'payslip',
            layout:'column',
            border:false,
            bodyStyle:'background:white',
            scope:this,
            autoScroll:true,
            items:[this.empform,this.pan1,this.pan3,this.pan2,this.pan5,this.pan4],
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
    myfun:function(finalStr) 
    {
        this.finalStr=finalStr;
        return finalStr;
    },
    exportReport: function(flag, exportType, docName,empid,cname,stdate,enddate){
        var selIds = "";
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
            "&stdate="+this.paycyclestart+
            "&flagpdf="+"datewise"+
            "&showborder="+this.showBorder+
            "&cdomain="+subDomain+
            "&enddate="+this.paycycleend;
        setDldUrl(url, exportType);
    },
    getdepcomp:function(){
        Wtf.Ajax.requestEx({
                        url: Wtf.req.base + "../Payroll/Wage/getDepwages.py" ,
                        method:'post',
                        params:{
                            TempId:this.TempId,
                            earnamount:this.wages.amtot,
                            deducamount:this.diduces.amtot,
                            paycyclestart:this.paycyclestart,
                            paycycleend:this.paycycleend,
                            paycycleactualstart:this.stdate.format("m-d-Y"),
                            paycycleactualend:this.enddate.format("m-d-Y"),
                            empid:this.empid,
                            ischanged:this.ischanged
                        }
                    },
                    this,
                    function(req,res){
                       if(req.Wage!=null&&req.Wage!=undefined){
                               var datarec = req.Wage;
                               var arr = [];
                               for(var ctr=0;ctr<datarec.length;ctr++){
                                   var newrec = new this.wages.fieldstaxx({
                                	   Id:datarec[ctr].Id,
                                       Type:datarec[ctr].Type,
                                       Rate:datarec[ctr].Rate,
                                       amount:datarec[ctr].amount,
                                       amtot:datarec[ctr].amtot,
                                       ratetype:datarec[ctr].ratetype,
                                       depwage:datarec[ctr].depwage,
                                       depwageid:datarec[ctr].depwageid,
                                       computeon:datarec[ctr].computeon
                                   })
                                   this.wages.amtot =this.wages.amtot+datarec[ctr].amount;
                                   arr.push(newrec);
                               }
                               this.wages.storetax.insert(this.wages.storetax.getTotalCount(),arr);
                               this.wages.total.setValue(WtfGlobal.currencyRenderer2(this.wages.amtot));
                               Wtf.getCmp(this.empid+'payslipTab'+this.stdate).setTotalSal();
                      }
                      if(req.Deduc!=null&&req.Deduc!=undefined){
                               var datarec = req.Deduc;
                               var arr = [];
                               for(var ctr=0;ctr<datarec.length;ctr++){
                                   var newrec = new this.diduces.fieldstaxx({
                                       Id:datarec[ctr].Id,
                                       Type:datarec[ctr].Type,
                                       Rate:datarec[ctr].Rate,
                                       amount:datarec[ctr].amount,
                                       amtot:datarec[ctr].amtot,
                                       ratetype:datarec[ctr].ratetype,
                                       depwage:datarec[ctr].depwage,
                                       depwageid:datarec[ctr].depwageid,
                                       computeon:datarec[ctr].computeon
                                   })
                                   this.diduces.amtot =this.diduces.amtot+datarec[ctr].amount;
                                   arr.push(newrec);
                               }
                               this.diduces.storetax.insert(this.diduces.storetax.getTotalCount(),arr);
                               this.diduces.total.setValue(WtfGlobal.currencyRenderer2(this.diduces.amtot));
                               Wtf.getCmp(this.empid+'payslipTab'+this.stdate).setTotalSal();
                      }
                      if(req.Tax!=null&&req.Tax!=undefined){
                               var datarec = req.Tax;
                               var arr = [];
                               for(var ctr=0;ctr<datarec.length;ctr++){
                                   var newrec = new this.taxes.fieldstaxx({
                                       Id:datarec[ctr].Id,
                                       Type:datarec[ctr].Type,
                                       Rate:datarec[ctr].Rate,
                                       amount:datarec[ctr].amount,
                                       amtot:datarec[ctr].amtot,
                                       ratetype:datarec[ctr].ratetype,
                                       depwage:datarec[ctr].depwage,
                                       depwageid:datarec[ctr].depwageid,
                                       computeon:datarec[ctr].computeon
                                   })
                                   this.taxes.amtot =this.taxes.amtot+datarec[ctr].amount;
                                   arr.push(newrec);
                               }
                               this.taxes.storetax.insert(this.taxes.storetax.getTotalCount(),arr);
                               this.taxes.total.setValue(WtfGlobal.currencyRenderer2(this.taxes.amtot));
                               Wtf.getCmp(this.empid+'payslipTab'+this.stdate).setTotalSal();
                      }
                      if(req.EC!=null&&req.EC!=undefined){
                               datarec = req.EC;
                               arr = [];
                               for(ctr=0;ctr<datarec.length;ctr++){
                                   newrec = new this.empcontrib.fieldstaxx({
                                       Type:datarec[ctr].Type,
                                       Rate:datarec[ctr].Rate,
                                       amount:datarec[ctr].amount,
                                       amtot:datarec[ctr].amtot,
                                       ratetype:datarec[ctr].ratetype,
                                       computeon:datarec[ctr].computeon
                                   })
                                   this.empcontrib.amtot =this.empcontrib.amtot+datarec[ctr].amount;
                                   arr.push(newrec);
                               }
                               this.empcontrib.storetax.insert(this.empcontrib.storetax.getTotalCount(),arr);
                               this.empcontrib.total.setValue(WtfGlobal.currencyRenderer2(this.empcontrib.amtot));
                      }
                    },
                    function(req,res){

                    }
                );
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
    },
    genpayslipchk:function(actionid){
        if (this.modifiedflag==1&&actionid!=0){
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms..Messages.calMsgBoxShow139"),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='no')
                    {
                        return;
                    } else {
                        this.genpayslip();
                    }
                }
            });
        } else if(actionid==0){
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
                        this.deletePayslip();
                    }
                }
            });
        } else {
            this.genpayslip();
        }
    },
    deletePayslip: function(){
    	var empid= new Array();
        empid.push(this.empid);
    	
        Wtf.Ajax.requestEx({
            url: "Emp/deletePayslipDetails.py" ,
            method:'post',
            params:{
                empid:Wtf.encode(empid),
                enddate:this.todateemp.getRawValue(),
                startdate:this.paycyclestart
            }
        },
        this,
        function(response){
            var res=eval('('+response+')');
            Wtf.Msg.show({
                title:WtfGlobal.getLocaleText("hrms.common.success"),
                msg: ""+res.msg+"",
                scope:this,
                width:300,
                buttons: Wtf.Msg.OK,
                fn: function(btn,value){
                    this.dwnldpay.disable();
                    this.myprint.disable();
                    this.fireEvent('gridload',response);
                },
                animEl: 'elId',
                icon:Wtf.MessageBox.INFO
            });
        },
        function(req,res){
            calMsgBoxShow(27,1);
        }
        );
    },
    genpayslip:function(){
        var wagedata = "";
        var wagerec;
        var wagest=this.wages.storetax;
        for(var i=0;i<wagest.getCount();i++){
            wagerec=wagest.getAt(i).data;
            wagedata += "{'ctg':'Wages',";
            wagedata += "'type':'" + wagerec.Type + "',";
            wagedata += "'amount':'" + wagerec.amount + "',";
            wagedata += "'Id':'" + wagerec.Id + "',";
            wagedata += "'Rate':'" + wagerec.Rate + "'},";
        }
        var trmLen = wagedata.length - 1;
        var finalwage = wagedata.substr(0,trmLen);

        var deddata = "";
        var dedrec;
        var dedcst=this.diduces.storetax;
        for( i=0;i<dedcst.getCount();i++){
            dedrec=dedcst.getAt(i).data;
            deddata += "{'ctg':'Deduction',";
            deddata += "'type':'" + dedrec.Type + "',";
            deddata += "'amount':'" + dedrec.amount + "',";
            deddata += "'Id':'" + dedrec.Id + "',";
            deddata += "'Rate':'" + dedrec.Rate + "'},";
        }
        trmLen = deddata.length - 1;
        var finalded = deddata.substr(0,trmLen);

        var taxdata = "";
        var taxrec;
        var taxst=this.taxes.storetax;
        for( i=0;i<taxst.getCount();i++){
            taxrec=taxst.getAt(i).data;
            taxdata += "{'ctg':'Taxes',";
            taxdata += "'type':'" + taxrec.Type + "',";
            taxdata += "'amount':'" + taxrec.amount + "',";
            taxdata += "'Id':'" + taxrec.Id + "',";
            taxdata += "'Rate':'" + taxrec.Rate + "'},";
        }
        trmLen = taxdata.length - 1;
        var finaltax = taxdata.substr(0,trmLen);

        var ecdata = "";
        var ecrec;
        var ecst=this.empcontrib.storetax;
        for( i=0;i<ecst.getCount();i++){
            ecrec=ecst.getAt(i).data;
            ecdata += "{'ctg':'Taxes',";
            ecdata += "'type':'" + ecrec.Type + "',";
            ecdata += "'amount':'" + ecrec.amount + "',";
            ecdata += "'Rate':'" + ecrec.Rate + "'},";
        }
        trmLen = ecdata.length - 1;
        var finalec = ecdata.substr(0,trmLen);
        this.taxtot=this.taxes.total.getValue().replace(/,/g,'');
        this.netsal=this.tot.getValue().replace(/,/g,'');
        this.wagetot=this.wages.total.getValue().replace(/,/g,'');
        this.deductot=this.diduces.total.getValue().replace(/,/g,'');
        this.ectot=this.empcontrib.total.getValue().replace(/,/g,'');

//        if(WtfGlobal.currencyRenderer2(this.wagetot)!=WtfGlobal.currencyRenderer2(this.fixedsal)){
//            calMsgBoxShow(4,1);
//            return;
//        }
        if(parseFloat(this.deductot)>parseFloat(this.wagetot)){
            calMsgBoxShow(157,1);
            return;
        }
        if(parseFloat(this.taxtot)>parseFloat(this.wagetot)){
            calMsgBoxShow(157,1);
            return;
        }
        if((parseFloat(this.taxtot)+parseFloat(this.deductot))>parseFloat(this.wagetot)){
            calMsgBoxShow(157,1);
            return;
        }
        Wtf.Ajax.requestEx({
            url: "Emp/setPayrollforTemp1.py" ,
            method:'post',
            params:{
                flag:59,
                empid:this.empid,
                gross:this.fixedsal,
                net:this.netsal,
                wagetot:this.wagetot,
                taxtot:this.taxtot,
                deductot:this.deductot,
                ectot:this.ectot,
                mappingid:this.mappingid,
                tempid:this.TempId,
                WageJson:finalwage,
                DeducJson:finalded,
                taxesJson:finaltax,
                ECJson:finalec,
                salaryStatus:Wtf.cmpPref.approvesalary?2:3,
                stdate:this.fromdateemp.getRawValue(),
                enddate:this.todateemp.getRawValue(),
                paycyclestart:this.paycyclestart,
                paycycleend:this.paycycleend
            }
        },
        this,
        function(response){
//            var res=eval('('+response+')');
            Wtf.Msg.show({
                title:WtfGlobal.getLocaleText("hrms.common.success"),
                msg: ""+response.msg.toString()+"",
                scope:this,
                width:300,
                buttons: Wtf.Msg.OK,
                fn: function(btn,value){
                    if(response.value.toString()=="success" || response.value.toString()==WtfGlobal.getLocaleText("hrms.common.success")){
                            this.dwnldpay.enable();
                            this.myprint.enable();
                        this.fireEvent('gridload',response);
                    }
                },
                animEl: 'elId',
                icon:Wtf.MessageBox.INFO
            });
        },
        function(req,res){
            calMsgBoxShow(27,1);
        }
    );
    },
    
    getUnpaidleaves: function(req){
    	for(var i=0 ; i<this.arr.length; i++){
        	if(req!=undefined && req.data!=undefined && req.data[i]!=undefined && req.data[i]!=""){
        		this.arr[i].data.unpaidleave=req.data[i].unpaidleave;
        	}else{
        		this.arr[i].data.unpaidleave=0;
        	}
        }
        
    	var adjustLeaveWin = new Wtf.leavem.LeaveAdjustWindow({
            title: WtfGlobal.getLocaleText("hrms.payroll.Unpaidleaves"),
            height: 400,
            width: 450,
            modal: true,
            resizable: false,
            paycycleend: this.paycycleend,
            emptempstore: this.emptempstore,
            empstore: this.empstore,
            paycyclestart: this.paycyclestart,
            TempId:this.TempId,
            fromdateempG: this.fromdateempG,
            todateempG: this.todateempG,
            layout: 'fit',
            iconCls : getTabIconCls(Wtf.etype.iconwin),
            bodyStyle: "background-color: #f1f1f1;",
            arr: this.arr,
            extraarr: []
        });
        adjustLeaveWin.show();
    }
});
