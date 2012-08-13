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
Wtf.PayrollGroupTemplate=function(config){
    config.border=false;
    config.activeTab= 0;
   // config.id="mainpayrolltab";
    config.enableTabScroll=true;
    Wtf.PayrollGroupTemplate.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.PayrollGroupTemplate,Wtf.Panel,{
    initComponent:function(config){
        Wtf.PayrollGroupTemplate.superclass.initComponent.call(this,config);
        this.xg = Wtf.grid;
           this.sm=new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false,
            scope:this
        });
        this.gridfields=[
        {
            name:'GroupName'
        },
        {
            name:'NosDeduc'
        },
        {
            name:'NosTax'
        },
        {
            name:'NosWage'
        },
        {
            name:'TempName'
        },
        {
            name:'TempRange'
        },
        {
            name:'TempID'
        },
        {
            name:'GroupId'
        },
        {
            name:'showborder'
        },
        {
            name:'payinterval'
        },
        {
            name:'effdate'
        },
        {
            name:'basic'
        }
        ];
        this.cmGroupGrid=new Wtf.grid.ColumnModel([this.sm,
            new Wtf.grid.RowNumberer(),
            {
                id:'GroupName',
                header:WtfGlobal.getLocaleText("hrms.common.designation"),
              //  width: 10,
                sortable: true,
                hidden:true,
                dataIndex: 'GroupName'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.payrolltemplates"),
               // width: 10,
                sortable: true,
                hideable: false,
                dataIndex: 'TempName'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.noofassignedwages"),
              //  width: 10,
               // sortable: true,
                align:'right',
                dataIndex: 'NosWage',
                renderer:WtfGlobal.numericRenderer
            },

            {
                header: WtfGlobal.getLocaleText("hrms.payroll.noofassignedtaxes"),
              //  width: 10,
              //  sortable: true,
                align:'right',
                dataIndex: 'NosTax',
                renderer:WtfGlobal.numericRenderer
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.noofassigneddeductions"),
            //    width: 10,
               // sortable: true,
                align:'right',
                dataIndex: 'NosDeduc',
                renderer:WtfGlobal.numericRenderer
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.payinterval"),
                dataIndex: 'payinterval',
                renderer:function(val){
                    var result = " - ";
                    switch(val){
                        case 1:
                            result = WtfGlobal.getLocaleText("hrms.payroll.onceamonth");
                            break;
                        case 2:
                            result = WtfGlobal.getLocaleText("hrms.payroll.twiceamonth");
                            break;

                        case 3:
                            result = WtfGlobal.getLocaleText("hrms.payroll.onceaweek");
                            break;
                    }

                    return result;
                }

            }
//            ,
//            {
//                header:"Salary Range ",
//                width: 10,
//                sortable: true,
//                align:'right',
//                dataIndex: 'TempRange',
//                renderer:function(val){
//                    var symbol=WtfGlobal.getCurrencySymbol();
//                    var myScripts = new Array(2)
//                    myScripts=val.split("-");
//                    var range=symbol+" "+WtfGlobal.currencyRenderer2(myScripts[0])+" - "+symbol+" "+WtfGlobal.currencyRenderer2(myScripts[1]);
//                    if(val!=null){
//                        return('<div class="currency" style="font-family:Lucida Sans Unicode;">'+range+'</div>');
//                    }
//
//                }
//            }
            ]);
        this.reader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalcount'
        },this.gridfields);
        this.groupGridStore=new Wtf.data.GroupingStore({
            reader: this.reader,
//            url: Wtf.req.base + "PayrollHandler.jsp",
            url : "Payroll/Template/getPayProcessData.py",
            sortInfo:{
                field: 'TempName',
                direction: "DSC"
            },
            groupField:'GroupName'
        });       
        this.groupGridStore.baseParams={
            PayProc:'get'
        };       
        this.groupGridStore.load({params:{
                start:0,    
                limit:15
        }});     
            this.editbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.generatepayroll"),
                minWidth:110,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.generatepayroll.tooltip"),
                scope:this,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                disabled:true,
                handler:this.generatePayroll
            });
            this.assinEmp=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.assignemployee"),
                minWidth:110,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.assignemployee.tooltip"),
                scope:this,
                iconCls:'pwndCommon profile2buttonIcon',
                disabled:true,
                handler:this.assignEmp
            });
        var tempbtns=[];
        tempbtns.push('-',new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.groupGridStore.load({params:{start:0,limit:this.Payrollgrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.Payrollgrid.id).setValue("");
        		}
     		}),'-',this.addpaytemp=new Wtf.Button({
                text: WtfGlobal.getLocaleText("hrms.payroll.addpayrolltemplate"),
                scope:this,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.addpayrolltemplate.tooltip"),
                minWidth:125,
                iconCls:getButtonIconCls(Wtf.btype.addbutton),
                handler: this.addtemplate
            }),'-',
            this.editviewtempbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.payroll.Template.Details"),
                minWidth:110,
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.Template.Details.Tooltip"),
                scope:this,
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                disabled:true,
                handler:this.tempDetails
            }),'-',
            this.deleteTempbtn=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.common.delete"),
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.delete.tooltip1"),
                minWidth:60,
                scope:this,
                iconCls:getButtonIconCls(Wtf.btype.deletebutton),
                disabled:true,
                handler:this.deleteTemp
            }));
        this.Payrollgrid=new Wtf.KwlGridPanel({            
            border:false,
            cm:this.cmGroupGrid,
            store:this.groupGridStore,
            width: 700,
            height: 450,
            scope:this,
            loadMask:true,
            sm:this.sm,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.searchbydesignationtemplate"),
            searchField:"GroupName",
            serverSideSearch:true,
            displayInfo:true,
            view: new Wtf.grid.GroupingView({
                forceFit: true,
                showGroupName:false,
                groupTextTpl: '{text}'
            }),
            tbar:tempbtns
        });

//        this.groupPanel=new Wtf.Panel({
//           // title:'Template Management',
//            border:false,
//            layout:'fit',
//            iconCls: getTabIconCls(Wtf.etype.acc),
//            items:[this.Payrollgrid]
//        });
        this.add(this.Payrollgrid);
        this.doLayout();
       // this.setActiveTab(this.groupPanel);
//        this.on('activate', function(tp, tab){
//            this.doLayout();
//        });
        calMsgBoxShow(202,4,true);
        this.groupGridStore.on("load",function(){            
            if(this.groupGridStore.getCount()==0){
                this.Payrollgrid.getView().emptyText=WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addTemplate(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.payroll.addtemplategrid.default")+"</a>");
                this.Payrollgrid.getView().refresh();                              
             }
              if(msgFlag==1){
                  WtfGlobal.closeProgressbar();
                }
        },this);
        this.sm.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(tempbtns, this.Payrollgrid, [5],[7]);
        },this);
    },
    addtemplate:function(obj,eventObj,recData,edit)
    {
        if(edit==1)
        	this.addpay=Wtf.getCmp('EditPayTemplate'+recData.TempID);
        else
        	this.addpay=Wtf.getCmp('AddPayTemplate');
        if(!this.addpay)
        {
            this.addpay=new Wtf.AddPayrollTemplate({
                scope:this,
                title: (edit == 1) ? WtfGlobal.getLocaleText({key:"hrms.payroll.Edit.Template",params:[recData.TempName]}) : WtfGlobal.getLocaleText("hrms.payroll.Add.Template"),
                globname:'addtemp',
                iconCls:'pwndCommon templatetabIcon',
                id: (edit == 1) ? 'EditPayTemplate'+recData.TempID : 'AddPayTemplate',
                groupGridStore:this.groupGridStore,
                editMode:recData ? true : false,
                templateid:recData ? recData.TempID : "",
                templatename:recData ? recData.TempName : "",
                showBorder:recData ? recData.showborder : "",
                range:recData ?  recData.TempRange : "",
                group:recData ?  recData.GroupId : "",
                payinterval:recData ?  recData.payinterval : "",
                effdate:recData ?  recData.effdate : "",
                basicval:recData ?  recData.basic : "",
                edit:edit
            });
            mainPanel.add(this.addpay);
            this.addpay.on('render',function(){
                this.addpay.tempname.focus(true,100);
            },this);
            this.doLayout();
        }
        mainPanel.setActiveTab(this.addpay);
        mainPanel.doLayout();
    },
    generatePayroll:function()
    {
         var recData = this.Payrollgrid.getSelectionModel().getSelected().data;

        if(this.Payrollgrid.getSelectionModel().getSelected()!=null){
            Wtf.Ajax.requestEx({
                 url: Wtf.req.base + "PayrollHandler.jsp" ,
                 scope:this,
                params:{
                    type:'CPaytemp',//Create Payslip Per Template
                    TempId:recData.TempID
                }},this,
                function(req){
                    if(req.responseText=="fail"){
                    }
                    else{
                        this.resp=eval('('+req.responseText+')');
                        if(this.generatepay==null)
                        {
                            this.generatepay=new Wtf.GeneratePayroll({
                                scope:this,
                                border:false,
                                iconCls:'pwndCommon profile2buttonIcon',
                                TempId:recData.TempID,
                                ttotal:req.Tax.TaxTotal,
                                taxcash:req.Tax.cashtotal,
                                wtotal:req.Wages.WageTotal,
                                wagecash:req.Wages.cashtotal,
                                dtotal:req.Deduction.DeducTotal,
                                deduccash:req.Deduction.cashtotal,
                                tempName:recData.TempName,
                                nostax:recData.NosTax,
                                noswage:recData.NosWage,
                                nosdeduc:recData.NosDeduc,
                                currency:req.curval,
                                title:'Payroll of_'+recData.TempName,
                                groupGridStore:this.groupGridStore
                            });
                            this.add(this.generatepay);
                            this.doLayout();
                            this.generatepay.on('beforedestroy',function(comp){
                                this.generatepay=null;

                            },this);
                        }
                        this.editbtn.disable();
                        this.editviewtempbtn.disable();
                        this.deleteTempbtn.disable();                        
                        this.assinEmp.disable();
                        this.Payrollgrid.getSelectionModel().clearSelections();
                        this.setActiveTab(this.generatepay);
                        this.doLayout();
                    }
                },
               function(req){
                });
        }
        else{
            this.editbtn.disable();
            this.editviewtempbtn.disable();
            this.deleteTempbtn.disable();            
            this.assinEmp.disable();
            this.Payrollgrid.getSelectionModel().clearSelections();
        }
    },
    tempDetails:function(obj,eventObj)
    {
        
            var recData = this.Payrollgrid.getSelectionModel().getSelected().data;
            this.addtemplate(obj,eventObj,recData,1);
            /*

            var ID = this.id+'editviewtemplate'+recData.TempID;
            this.open1=Wtf.getCmp(ID);
            if(!this.open1)
            {
                this.editviewtemp=new Wtf.EditViewTemplate({
                    scope:this,
                    id: ID,
                    iconCls:getTabIconCls(Wtf.etype.hrmsreport),
                    title:'Details Of '+recData.TempName,
                    templateid:recData.TempID,
                    templatename:recData.TempName,
                    range:recData.TempRange,
                    group:recData.GroupId,
                    globname:'editView',
                    groupStore:this.groupGridStore
                });

                this.add(this.editviewtemp);
                this.editviewtemp.on('render',function(){
                    this.editviewtemp.tempname.focus(true,100);
                },this);
                this.doLayout();
            }
            this.Payrollgrid.getSelectionModel().clearSelections();
            this.editbtn.disable();
            this.editviewtempbtn.disable();
            this.deleteTempbtn.disable();
            this.assinEmp.disable();
            this.setActiveTab(this.editviewtemp);
            this.doLayout();*/
    },
    deleteTemp:function()
    {
        var recData = this.Payrollgrid.getSelectionModel().getSelected().data;
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:deleteMsgBox('record'),
            buttons:Wtf.MessageBox.YESNO,
            icon:Wtf.MessageBox.QUESTION,
            scope:this,
            buttons: Wtf.MessageBox.YESNO,
            icon:Wtf.MessageBox.QUESTION,
            animEl: 'elId',
            fn:function(btn){
                if(btn=='yes'){
                     var delrec=this.Payrollgrid.getSelectionModel().getSelections();
                     var delarr=[];
                     this.Payrollgrid.getSelectionModel().clearSelections();
                     for(var i=0;i<delrec.length;i++){
                             delarr.push(delrec[i].get('TempID'));
                             var rec=this.groupGridStore.indexOf(delrec[i]);
                             WtfGlobal.highLightRow(this.Payrollgrid,"FF0000",5, rec)
                        }                                                           
                    calMsgBoxShow(201,4,true);
                    Wtf.Ajax.requestEx({
//                        url: Wtf.req.base + "PayrollHandler.jsp" ,
                        url:"Payroll/Template/deleteTemplateData.py",
                        method:'post',
                        params:{
                            dele:true,
                            delType:'template',
                            tempid:delarr
                        }
                    },this,
                    function(req,res){
                        if(req.value.toString()=="success"){
                            calMsgBoxShow(22,0);
                            var params={
                                        start:0,
                                        limit:15
                                          }
                            WtfGlobal.delaytasks(this.groupGridStore,params);                           
                            this.editbtn.disable();
                            this.editviewtempbtn.disable();
                            this.deleteTempbtn.disable();                          
                            this.assinEmp.disable();
                            this.Payrollgrid.doLayout();
                        }else if(req.value.toString()=="assign"){
                            Wtf.Msg.show({
                                title: WtfGlobal.getLocaleText("hrms.common.warning"),
                                //msg: 'The template <b>('+req.templates.toString()+")</b> has some assigned employees ",
                                msg:WtfGlobal.getLocaleText({key:"hrms.payroll.delete.template.warning",params:[req.templates.toString()]}),
                                scope:this,
                                width:260,
                                buttons: Wtf.Msg.OK,
                                animEl: 'elId',
                                icon: Wtf.MessageBox.WARNING
                            });
                            this.editbtn.disable();
                            this.editviewtempbtn.disable();
                            this.deleteTempbtn.disable();                            
                            this.assinEmp.disable();
                        }
                    },
                    function(req,res){
                        this.groupGridStore.reload();
                        this.Payrollgrid.doLayout();
                    });
                }
            }
        });
    },
    assignEmp:function()
    {
            var recData = this.Payrollgrid.getSelectionModel().getSelected().data;

            if(this.assEmpTT==null){
                this.add(this.assEmpTT=new Wtf.AssignEmployeeFT({
                    title:WtfGlobal.getLocaleText({key:"hrms.payroll.assignemployeeabc",params:[recData.TempName]}),
                    scope:this,
                    iconCls:getTabIconCls(Wtf.etype.hrmsmygoals),
                    closable:true,
                    TempId:recData.TempID,
                    Gname:recData.GroupName,
                    Srange:recData.TempRange,
                    TempName:recData.TempName,
                    groupstore:this.groupGridStore
                }));
                this.assEmpTT.on('beforedestroy',function(){
                    this.assEmpTT=null;
                },this);
        }
            this.Payrollgrid.getSelectionModel().clearSelections();
            this.editbtn.disable();
            this.editviewtempbtn.disable();
            this.deleteTempbtn.disable();
            this.assinEmp.disable();
            this.setActiveTab(this.assEmpTT);
            this.doLayout();
        }
}); 
function addTemplate(Id){
    Wtf.getCmp(Id).addtemplate()
}
