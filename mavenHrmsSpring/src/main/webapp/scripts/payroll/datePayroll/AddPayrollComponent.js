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
Wtf.AddPayrollComponent = function(config) {
    Wtf.apply(this, config);
    Wtf.AddPayrollComponent.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.AddPayrollComponent, Wtf.Window, {
    modal:true,
    onRender: function(config) {
		this.expression=undefined;
        Wtf.AddPayrollComponent.superclass.onRender.call(this, config);
        this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.empProfile));
        this.headingType="";

        
        this.CostCenterRec = new Wtf.data.Record.create([
        {
            name:"id"
        },

        {
            name:"name"
        },
        {
            name:"code"
        },

        {
            name:"creationDate"
        }
        ]);

        this.CostCenterReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.CostCenterRec);

        this.CostCenterStore = new Wtf.data.Store({
            url: "Common/getCostCenter.common",
            reader:this.CostCenterReader

        });
        this.CostCenterStore.load();
        
        this.computeonRec = Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"sdate"
        },
        {
            "name":"edate"
        },
        {
            "name":"desc"
        },
        {
            "name":"type"
        },
        {
            "name":"typename"
        }
        ]);
        
        this.computeonStore = new Wtf.data.Store({
            baseParams: {
                flag: 101
            },
            url: "Payroll/Date/getComputeOnComponents.py",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },
            this.computeonRec
            )
        });
        
        this.conContainer = document.createElement('div');
        this.conContainer.className = 'conContainer';
        this.conContainer.id = 'parentCon';
        this.specifiedFormula = new Wtf.form.FieldSet({
            region:'center',
            id:'subrule',
            hidden:true,
            bodyStyle: 'overflow-y:scroll;',
            title:WtfGlobal.getLocaleText("hrms.payroll.your.formula"),
            items:[
                new Wtf.Panel({
                    id:'addCon',
                    border:false,
                    contentEl:this.conContainer
                })
            ]
        });
        
        this.specifiedFormulaPanel=new Wtf.Panel({
        	border:false,
                items:[this.specifiedFormula]
        });
        
   
        this.getRulesGridPanel();
        
        this.TypeCmb = new Wtf.form.ComboBox({
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.type")+'*',
                store:Wtf.gtypeStore,
                mode:'local',
                typeAhead:true,
                editable:true,
                valueField: 'id',
                hiddenName :'itemtypeid',
                displayField:'name',
                allowBlank: false,
                width: 200,
                triggerAction: 'all',
                selectOnFocus:true,
                forceSelection:true,
                emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.type")//,
                //typeAhead:false
        });
   
        this.computeonCombo=new Wtf.form.ComboBox({
            store:this.computeonStore,
            displayField:'code',
            typeAhead: true,
            valueField:'compid',
            id:this.id+"computedOn_combofield",
            allowBlank:false,
            name:'computeon',
            hiddenName:'computeon',
            width:200,
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.selectcomponent"),
            selectOnFocus:true
        });
        
        this.percent=[[WtfGlobal.getLocaleText("hrms.payroll.Amount"),'0'],[WtfGlobal.getLocaleText("hrms.payroll.Percent"),'1'],[WtfGlobal.getLocaleText("hrms.payroll.specifiedformula"),'2'],[WtfGlobal.getLocaleText("hrms.payroll.add.rules"),'3']];
        this.percentstore=new Wtf.data.SimpleStore({
            fields:[{
                name:'type'
            },{
                name:'code'
            }],
            data:this.percent
        });
        this.methodCombo=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.method")+"*",
            store:this.percentstore,
            displayField:'type',
            typeAhead: true,
            valueField:'code',
            allowBlank:false,
            width:200,
            labelWidth:100,
            scope:this,
            hiddenName:'method',
            name:'method',
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.percent.amount.specified.formula"),
            selectOnFocus:true,
            minValue : 0,
            value:this.rec?this.rec.get("method"):"0",
            plugins: [
                new Wtf.SpcifiedFormulaIcon({
                	handler: function(){
                            if(this.methodCombo.getValue()!='3'){
                		this.methodCombo.setValue('2');
            			this.callSpecifiedFormula();
                            }    
                	},
                	scope: this
                })],
            listeners:
            {
                scope:this,
                Select:function(combo,record,index) {
                    if(record.get('code')=="1") {//Percent
                        this.removeChildNode();
                        this.Amount.show();
                        this.Amount.allowBlank=false;
                        
                        this.specifiedFormula.hide();
                        this.rulesGrid.hide();
                        this.doLayout();
                        
                        this.hideComputedOnCombo(100, false, true, WtfGlobal.getLocaleText("hrms.payroll.compute.on")+"*","");
                        
                        this.changeLabelText(WtfGlobal.getLocaleText("hrms.payroll.percent")+"* :");
                        
                        this.specifiedFormulaHidden.setValue("");
                        
                        
                        
                    } else if(record.get('code')=="0") {//Amount
                        this.removeChildNode();
                        this.Amount.show();
                        this.Amount.allowBlank=false;
                        
                        this.specifiedFormula.hide();
                        this.rulesGrid.hide();
                        this.doLayout();
                        
                        
                        this.hideComputedOnCombo(9999999999999999, true, false, "","");
                        this.changeLabelText(WtfGlobal.getLocaleText("hrms.payroll.Amount")+" ("+WtfGlobal.getCurrencySymbol()+")* :");
                        this.specifiedFormulaHidden.setValue("");
                        
                    } else if(record.get('code')=="2") {//Specified Formula

                        this.callSpecifiedFormula();
                    } else if(record.get('code')=="3") {//Add Rule
                        
                        var flag = false;
                        
                        var recArr = Wtf.gtypeStore.queryBy(function(record) {
                            if(record.get("id")==this.TypeCmb.getValue()){
                                if(record.get("weightage")==5){
                                    flag=true;
                                    return true;
                                }
                                
                            }else{
                                return false;
                            }
                                
                        },this);
                        
                        if(!flag){
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.add.rule.is.applicable.for.only.income.tax.type.component")]);
                            this.methodCombo.setValue("");
                            return;
                        }
                        
                        this.removeChildNode();
                        this.Amount.setValue("0");
                        this.Amount.hide();
                        this.Amount.allowBlank=true;
                        
                        this.changeLabelText("")
                        this.specifiedFormula.hide();
                        
                        this.rulesGrid.show();
                        
                        this.doLayout();
                        
                        
                        this.hideComputedOnCombo(9999999999999999, true, false, "","");
                        this.specifiedFormulaHidden.setValue("");
                        
                    }
                }
            }
        });

        Wtf.gtypeStore.load();
        Wtf.paymentStore.load();
        this.computeonStore.load({
            params: {
                frequency:this.frequency,
                componentid:this.rec==undefined?"":this.rec.data.compid
            }});

        var amountTxt =WtfGlobal.getLocaleText({key:"hrms.payroll.amount.params",params:[WtfGlobal.getCurrencySymbol()]})+" *";
        if(this.rec){
            if(this.methodCombo.getValue()=="1"||this.methodCombo.getValue()=="2"){//Percent
                amountTxt =WtfGlobal.getLocaleText("hrms.payroll.Percent")+'(%) *';
            }
        }
        this.Amount = new Wtf.form.NumberField({
            fieldLabel:amountTxt,
            anchor:'93%',
            allowBlank: false,
            name:'amount',
            minValue:0,
            width:200,
            id:this.id+"amount_component",
            msgTarget: 'qtip',
            listeners:
            {
                scope:this,
                focus:function(){
                    if(this.methodCombo.getValue()=="1"||this.methodCombo.getValue()=="2"){//Percent or Specified Formula
                        this.Amount.maxValue = 100;
                    }if(this.methodCombo.getValue()=="0"){//Amount
                        this.Amount.maxValue = 9999999999999999;
                    }
                }
            }
        });



        

         this.FrequencyCmb = new Wtf.form.ComboBox({
                    fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.frequency")+' *',
                    store:Wtf.frequencyStore,
                    mode:'local',
                    typeAhead:true,
                    editable:true,
                    valueField: 'id',
                    hiddenName :'frequency',
                    displayField:'name',
                    allowBlank: false,
                    width: 200,
                    triggerAction: 'all',
                    selectOnFocus:true,
                    forceSelection:true,
                    emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.frequency"),
                    value:this.frequency,
                    disabled:true
        });
        
         this.frequencyHidden=new Wtf.form.Hidden({
             name:'frequency',
             id:'frequencyHidden',
             value:this.frequency
         });
         
         this.specifiedFormulaHidden=new Wtf.form.Hidden({
             name:'expression',
             id:'expression',
             value:this.rec!=undefined?this.rec.data.expression:""
         });
         this.specifiedFormula.on("show",function(){
                if(this.rec!=undefined && this.rec.data.expression!=""){
                    this.loadSpecifiedFormula(this.rec.data.expression);
                }
         },this);

        this.PaymentCmb = new Wtf.form.ComboBox({
                    fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.payment.type")+'*',
                    store:Wtf.paymentStore,
                    mode:'local',
                    typeAhead:true,
                    editable:true,
                    valueField: 'id',
                    hiddenName :'payment',
                    displayField:'name',
                    allowBlank: false,
                    width: 200,
                    triggerAction: 'all',
                    selectOnFocus:true,
                    forceSelection:true,
                    emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.payment.type")//,
                    //typeAhead:false
        });
        
        this.Debit = new Wtf.form.ComboBox({
                    fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.debit")+'*',
                    store:this.CostCenterStore,
                    mode:'local',
                    typeAhead:true,
                    editable:true,
                    valueField: 'id',
                    hiddenName :'debit',
                    displayField:'name',
                    allowBlank: false,
                    width: 200,
                    triggerAction: 'all',
                    selectOnFocus:true,
                    forceSelection:true,
                    emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.debit.type")//,
                    //typeAhead:false
        });
                                
       
        this.WtfGeneralPanel= new Wtf.Panel({
            border: false,
            layout:'fit',
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 70,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html:this.isview?getTopHtml(WtfGlobal.getLocaleText({key:"hrms.payroll.payroll.component.params",params:[this.action]}),""):getTopHtml(WtfGlobal.getLocaleText({key:"hrms.payroll.payroll.component.params",params:[this.action]}), WtfGlobal.getLocaleText("hrms.common.fill.following.fields"))
                },{
                    border:false,
                    region:'center',
                    cls:'windowstyle',
                    layout:"fit",
                    items: [
                    this.WtfGeneralForm = new Wtf.form.FormPanel({
                        url:'Payroll/Date/addPayComponent_Date.py',
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        bodyStyle: "background-color: #f1f1f1; margin: 5px ;padding:20px;",
                        lableWidth :50,
                        autoScroll:true,
                        layoutConfig: {
                            deferredRender: false
                        },
//                        defaults:{
//                            anchor:'93%',
//                            width: 200,
//                            msgTarget: 'side'
//                        },
                        defaultType:'textfield',
                        items:[
                        this.txtCode = new Wtf.ux.TextField({
                            width:200,
                            maxLength:50,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.code")+'*',
                            anchor:'90%',
                            allowBlank: false,
                            name:'code',
                            id:'txtId' + this.id
                        }),
                        this.startDate = new Wtf.form.DateField({
                            fieldLabel : WtfGlobal.getLocaleText("hrms.common.start.date"),
                            width : 200,
                            format:'Y-m-d',//change because as in CRM input in yyyy-mm-dd format to avoid Error if user selects 'March,2001'date format
                            name : 'startdate'
                        }),this.endDate = new Wtf.form.DateField({
                            fieldLabel : WtfGlobal.getLocaleText("hrms.common.end.date"),
                            width : 200,
                            format:'Y-m-d',//change because as in CRM input in yyyy-mm-dd format to avoid Error if user selects 'March,2001'date format
                            name : 'enddate'
                        }),
                        this.description = new Wtf.ux.TextField({
                            fieldLabel : WtfGlobal.getLocaleText("hrms.performance.description")+"*",
                            allowBlank: false,
                            width : 200,
                            maxLength:100,
                            name : 'description'
                        }),
                        this.TypeCmb ,
                        this.AdjChkBx = new Wtf.form.Checkbox({
                            id: 'Adjchk'+this.id,
                            border: false,
                            bodyStyle: "left:-113px; !important",
                            cls:'chkEmailNotificationPerUser',
                            scope:this,
                            checked:false,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.adjustable"),
                            name:'Adjchk'
                        }),
                        this.lauto = new Wtf.form.Checkbox({
                            id: 'lauto'+this.id,
                            border: false,
                            cls:'chkEmailNotificationPerUser',
                            scope:this,
                            checked:false,
                            //hideLabel:true,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.link.automatically"),
                            name:'lauto'
                        }),
                        this.blocked = new Wtf.form.Checkbox({
                            id: 'blocked'+this.id,
                            border: false,
                            cls:'chkEmailNotificationPerUser',
                            scope:this,
                            checked:false,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.blocked"),
                            name:'blocked'
                        }),
                        this.taxableComponent = new Wtf.form.Checkbox({
                            id: 'taxablecomponent'+this.id,
                            border: false,
                            cls:'chkEmailNotificationPerUser',
                            scope:this,
                            checked:false,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.taxable"),
                            name:'taxablecomponent'
                        }),this.FrequencyCmb,this.frequencyHidden, this.methodCombo, this.computeonCombo, this.specifiedFormulaHidden, this.specifiedFormulaPanel, this.rulesGridPanel,this.Amount, this.Debit, this.PaymentCmb
                        ]
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text: this.isEdit?WtfGlobal.getLocaleText("hrms.common.Update"):WtfGlobal.getLocaleText("hrms.common.submit"),
                id:'Item-submit-btn',
                handler: this.saveWtfGeneralRequest,
                scope:this
            },
            {
                anchor : '90%',
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                handler: function(){
                    this.close();
                },
                scope:this
            }
            ]
        });

        Wtf.gtypeStore.on("load",function(){
            if(this.rec){
                this.TypeCmb.setValue(this.rec.get("type"));
            }
        },this);
        Wtf.frequencyStore.on("load",function(){
            if(this.rec){
                this.FrequencyCmb.setValue(this.rec.get("frequency"));
            }
        },this);
        Wtf.paymentStore.on("load",function(){
            if(this.rec){
                this.PaymentCmb.setValue(this.rec.get("paymentterm"));
            }
        },this);
        this.computeonStore.on('load',function(){
            if(this.rec){
                this.computeonCombo.setValue(this.rec.get("computeon"));
            }
        },this);

        this.computeonCombo.on('expand',function(){
            if(this.rec){
                this.computeonStore.filterBy(function(record){
                    if(record.data.compid == this.rec.get("compid")) {
                        return false;
                    }
                    return true;
                },this);
            }
        },this);

        if(this.rec){
            this.txtCode.setValue(this.rec.get("code"));
            this.startDate.setValue(this.rec.get("sdate"));
            this.endDate.setValue(this.rec.get("edate"));
            this.description.setValue(this.rec.get("desc"));            
            this.lauto.setValue(this.rec.get("isdefault"));
            this.AdjChkBx.setValue(this.rec.get("isadjust"));
            this.blocked.setValue(this.rec.get("isblocked"));
            this.taxableComponent.setValue(this.rec.get("istaxablecomponent"));
            this.TypeCmb.setValue(this.rec.get("type"));
            this.FrequencyCmb.setValue(this.rec.get("frequency"));
            this.PaymentCmb.setValue(this.rec.get("paymentterm"));
            
        } else {//Amount
            
            this.hideComputedOnCombo(9999999999999999, true, false, "","")
            this.specifiedFormulaHidden.setValue("");
            
            
        }
         this.CostCenterStore.on("load",function(){
             if(this.rec){
               this.Debit.setValue(this.rec.get("costcenter"));
             }
         },this);
         
         
         this.WtfGeneralPanel.on("afterlayout",function(a, b){
             
             this.hideComputedOnCombo(9999999999999999, true, false, "","")
             this.specifiedFormulaHidden.setValue("");
             
             if(this.rec){
                 
                 this.TypeCmb.disable();
                 
                 if(this.rec.get("method") == "1"){
                    this.hideComputedOnCombo(100, false, true, WtfGlobal.getLocaleText("hrms.payroll.compute.on")+"*",this.rec.get("computeon"))
                    this.Amount.setValue(this.rec.get("percent"));
                    

                 } else if(this.rec.get("method") == "2"){
                    
                    this.hideComputedOnCombo(100, true, false, "","")
                
                    this.specifiedFormula.show();
                    this.specifiedFormulaHidden.setValue(this.rec.data.expression);
                    this.Amount.setValue(this.rec.get("percent"));
                    
                 } else if(this.rec.get("method") == "0"){

                    this.hideComputedOnCombo(9999999999999999, true, false, "","")
                    this.Amount.setValue(this.rec.get("amount"));
                    
                 } else if(this.rec.get("method") == "3"){
                    
                    this.methodCombo.disable();
                    this.hideComputedOnCombo(9999999999999999, true, false, "","")
                    this.rulesGrid.show();
                    this.changeLabelText("");
                    this.Amount.setValue("0");
                  
                    this.Amount.hide();
                    this.Amount.allowBlank=true;
                    
                 }
                 
             }
                
             
         },this);
         
         this.add(this.WtfGeneralPanel);
   },
   
   getRulesGridPanel : function(){
       
         
         this.RuleForm = new Wtf.Panel({
            border : false,
            lableWidth :50,
            region:"north",
            height:80,
            layout:'form',
            autoScroll:true,
            layoutConfig: {
                deferredRender: false
            },
            defaultType:'textfield',
            items:[
            this.lowerLimit = new Wtf.form.NumberField({
                width:150,
                maxLength:50,
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.lowerlimit"),
                anchor:'90%',
                name:'lowerLimit',
                allowNegative:false,
                id:'lowerLimit' + this.id
            }),
            this.upperLimit = new Wtf.form.NumberField({
                width:150,
                maxLength:50,
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.upperlimit"),
                anchor:'90%',
                name:'upperLimit',
                allowNegative:false,
                id:'upperLimit' + this.id
            }),
            this.coefft = new Wtf.form.NumberField({
                width:150,
                maxLength:50,
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.coefficient"),
                anchor:'90%',
                name:'coeff',
                id:'coeff' + this.id
            })
            ],
            
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text: WtfGlobal.getLocaleText("hrms.payroll.add.rule"),
                handler: function(){
                    
                    if(this.lowerLimit.getValue()==="" || this.upperLimit.getValue()==="" || this.coefft.getValue()===""){
                        
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.please.specify.lowerlimit.upperlimit.coeff")],2);
                        
                    } else {
                        
                        if(this.validRule(this.rulestore, this.lowerLimit.getValue(), this.upperLimit.getValue())){
                            if(this.rec){
                                this.saveComponentRuleRequest();
                            }else {
                                this.addRuleRecord();
                            }
                        
                        } else {
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.not.valid.rule")],2);
                        }
                        
                    }
                },
                scope:this
            }
            ]
        });
         
         this.getRuleStore();
         
         this.cm=new Wtf.grid.ColumnModel([
           // new Wtf.grid.CheckboxSelectionModel(),
            new Wtf.grid.RowNumberer(),
            {
                    header: WtfGlobal.getLocaleText("hrms.payroll.lowerlimit"),
                    dataIndex:'lowerlimit'
            },{
                    header: WtfGlobal.getLocaleText("hrms.payroll.upperlimit"),
                    dataIndex:'upperlimit'
            },{
                    header: WtfGlobal.getLocaleText("hrms.payroll.coefficient"),
                    dataIndex:'coefficient'
            },{
                    header: WtfGlobal.getLocaleText("hrms.common.delete"),
                    dataIndex:'deleteicon',
                    align:"center",
                    renderer:function(a,b,c,d,e,f){
                        
                        return "<div class='pwndCommon deletebuttonIconGrid' style='cursor:pointer' title="+WtfGlobal.getLocaleText("hrms.common.delete")+" ></div>";
                    }
            }
        ]);
        
        this.rulesGrid = new Wtf.form.FieldSet({
            height:(Wtf.isIE7 || Wtf.isIE8)?250:290,
            layout:'border',
            hidden:true,
            bodyStyle: "background-color: #f1f1f1;",// margin: 5px ;padding:20px;",
            title:WtfGlobal.getLocaleText("hrms.payroll.add.rules"),
            items:[this.RuleForm,
            this.grid = new Wtf.grid.GridPanel({
                store:this.rulestore,
                region:"center",
                autoScroll:true,
                cm: this.cm,
                searchLabel:" ",
                searchLabelSeparator:" ",
                sm: this.sm,
                viewConfig:{
                    forceFit:true
                }
            })
            ]
        });
        
        this.grid.on("cellclick", this.deleteTarget, this);
        
        this.RuleForm.on("afterLayout", function(){
            this.RuleForm.setHeight(120);
        },this);
        
        this.rulesGridPanel = new Wtf.Panel({
            layout:'fit',
            border:false,
            items:[this.rulesGrid]
        });
        
       
   },
   
   deleteTarget:function(grid, ri, ci, e) {

        var event = e;
        if(event.target.className == "pwndCommon deletebuttonIconGrid") {
            Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:WtfGlobal.getLocaleText("hrms.payroll.add.rule.msg"),
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    var rec=grid.getStore().getAt(ri);
                    
                    if(this.isEdit){

                        this.deleteRule(rec.data.id);

                    } else {


                        grid.getStore().remove(rec);
                    }
              }
           }
        });
       }    
    },
    
    deleteRule:function(ruleid) {
                 
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.Deletingdata"));
        Wtf.Ajax.requestEx({
            url:"Payroll/Date/deleteComponentRule.py",
                params:{
                    ruleid:ruleid
                }
            },this,
            function(){
               
                this.rulestore.load();
                Wtf.updateProgress();
            },
            function(){
                Wtf.updateProgress();
               
            }
        )
                    
               
    },
   
   getRuleStore : function(){
        
        if(this.rec){
            
            this.ruleRec = Wtf.data.Record.create([{
                        "name":"id"
                },{
                        "name":"lowerlimit"
                },{
                        "name":"upperlimit"
                },{
                        "name":"coefficient"
                },{
                        "name":"deleteicon"
             }]);
            
            
            this.rulestore = new Wtf.data.Store({
                baseParams: {
                    componentid: this.rec.get("compid")
                },
                url: "Payroll/Date/getComponentRules.py",
                reader: new Wtf.data.KwlJsonReader1({
                    root: 'data',
                    totalProperty:'count'
                },
                this.ruleRec
                )
            });
            
            this.rulestore.load();
         
        } else{
            
            this.rulestore = new Wtf.data.SimpleStore({
                fields: [
                {name: 'lowerlimit'},
                {name: 'upperlimit'},
                {name: 'coefficient'},
                {name: 'deleteicon'}
                ]
            });
            
        }
   
    },
   
   addRuleRecord : function(){
       
        this.ruleRecord = new Wtf.data.Record({
            lowerlimit : Wtf.getCmp('lowerLimit' + this.id).getValue(),
            upperlimit : Wtf.getCmp('upperLimit' + this.id).getValue(),
            coefficient : Wtf.getCmp('coeff' + this.id).getValue(),
            deleteicon : ""
        });
        
        this.rulestore.insert(this.rulestore.getCount(), this.ruleRecord);
        
        
   },
   
   validRule : function(store, llimit, ulimit){
       var valid=true;
       
       for( var i=0; i< store.getCount(); i++){
           
           var rec = store.getAt(i);
           
           if((rec.data.lowerlimit <= llimit && llimit <= rec.data.upperlimit) || (rec.data.lowerlimit <= ulimit && ulimit <= rec.data.upperlimit) ){
               valid = false;
               break;
           }
        
   
       } 
   
       return valid
       
   },
   
   saveComponentRuleRequest: function(){
        
        if(Wtf.getCmp('lowerLimit' + this.id).isValid() && Wtf.getCmp('upperLimit' + this.id).isValid() && Wtf.getCmp('coeff' + this.id).isValid()){
        	Wtf.Ajax.requestEx({
        		url:'Payroll/Date/addComponentRule_Date.py',
                params:{
	        		lowerLimit : Wtf.getCmp('lowerLimit' + this.id).getValue(),
	                upperLimit : Wtf.getCmp('upperLimit' + this.id).getValue(),
	                coeff : Wtf.getCmp('coeff' + this.id).getValue(),
	                componentid:this.rec.data.compid   
                }
            }, this,
            function(response){
            	if(response.success){
             	   this.rulestore.load();
                }else{
                    
                }
            }, 
            function(){
            	msgBoxShow(100,1);
                this.close();
            });
        }
    },
   
   loadSpecifiedFormula: function(expression) {
	   
           this.removeChildNode();
	   
           this.coechild = [];//coefficient
	   this.aschild = [];//add or sub
	   this.cchild = [];//component
	   this.mainchild = [];
	   
	   var records = eval(expression);
		for(var i=0; i<records.length; i++){
			if(this.coeff==null||this.coeff==undefined){
				this.coeff = [];
				this.oper = [];
			}
			if(records[i]!=undefined){
				this.coeff[i]=records[i].coefficient;
				this.oper[i]=records[i].operator;
				this.onRowSelect(i, records[i].component, records[i].componentname);
			}
		}
   },
   
   onRowSelect:function(row,compid,componentname){
	   if(this.mainchild[row]==undefined){
		   this.mainchild[row] = document.createElement('div');
   		   this.mainchild[row].id = compid;
   	    
   		   this.cchild[row] = document.createElement('div');
   		   this.cchild[row].id = "cchild"+compid;
   		   this.cchild[row].val = compid;
   		   this.cchild[row].innerHTML = componentname;
	       this.cchild[row].className="x-form-item";
   			if(Wtf.isIE7 || Wtf.isIE8){
   				this.cchild[row].style.display = "inline";
   			}else{
   				this.cchild[row].style.cssFloat = "left";
   			}
   		
   			this.coechild[row] = document.createElement('div');
   			this.coechild[row].id = "coechild"+compid;
   			this.coechild[row].className="x-form-item";
   			if(Wtf.isIE7 || Wtf.isIE8){
   				this.coechild[row].style.display = "inline"; 
   			}else{
	        	this.coechild[row].style.cssFloat = "left";
   			}
	        if(this.coeff!=null&&this.coeff!=undefined&&this.coeff.length>0){
	        	this.coechild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+this.coeff[row]+")</a>&nbsp;";
	        	this.coechild[row].val = this.coeff[row];
	        } else {
	        	this.coechild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+1+")</a>&nbsp;";
	        	this.coechild[row].val = 1;
	        }
	        
	        this.aschild[row] = document.createElement('div');
	        this.aschild[row].id = "aschild"+compid;
	        this.aschild[row].className="x-form-item";
	        if(Wtf.isIE7 || Wtf.isIE8){
	        	this.aschild[row].style.display = "inline";
	        }else{
	        	this.aschild[row].style.cssFloat = "left";
	        }
	        if(this.oper!=null&&this.oper!=undefined&&this.oper.length>0){
	        	if(this.oper[row]=="+"){
	        		this.aschild[row].val = "+";
	        	}else if(this.oper[row]=="-"){
	        		this.aschild[row].val = "-";
	        	}else{
	        		this.aschild[row].val = "+";
	        		this.oper[row] = "+";
	            }
	        	this.aschild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+this.oper[row]+")</a>&nbsp;";
	        }else{
	        	this.aschild[row].val = '+';
	        	this.aschild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>(+)</a>&nbsp;";
	        }
	        
	        this.mainchild[row].appendChild(this.aschild[row]);//operator
	        this.mainchild[row].appendChild(this.coechild[row]);//coefficient
	        this.mainchild[row].appendChild(this.cchild[row]);//component
	        this.conContainer.appendChild(this.mainchild[row]);
   		}
   },

    genJsonForRule : function () {
        
        var finalJson=[];
        for(var i=0;i<this.rulestore.getCount();i++) {
            var recordData = this.rulestore.getAt(i).data;
            finalJson.push({

                    lowerlimit:recordData.lowerlimit,
                    upperlimit:recordData.upperlimit,
                    coefficient:recordData.coefficient,
                    deleteicon:recordData.deleteicon

            });
        }
        finalJson = {
            data:finalJson
        }

    return Wtf.encode(finalJson);
    },


    saveWtfGeneralRequest: function(){
        if(this.methodCombo.getValue()=="2"&&(this.specifiedFormulaHidden.getValue()==undefined||this.specifiedFormulaHidden.getValue()=="[]"||this.specifiedFormulaHidden.getValue()=="")){
        	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.enter.specified.formula")],2);
        	return;
        }
        var sdate = this.startDate.getValue();
        var edate = this.endDate.getValue();
        if(sdate!="" && edate!=""){
            var startdat=new Date(sdate);
            var enddat=new Date(edate);
            if(enddat<startdat) {
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow170")]);
                return;
            }
        }
        
        
        var flag=true;
        var recArr = Wtf.gtypeStore.queryBy(function(record) {
            if(record.get("id")==this.TypeCmb.getValue()){
                if((record.get("weightage")!=5 && this.methodCombo.getValue()==3) || (record.get("weightage")==5 && this.methodCombo.getValue()!=3)){
                    flag=false;
                    return true;
                }

            }else{
                return true;
            }

        },this);

        if(!flag){
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.add.rule.is.applicable.for.only.income.tax.type.component")]);
            this.methodCombo.setValue("");
            return;
        }
        
        Wtf.getCmp("Item-submit-btn").setDisabled(true);
        if(this.WtfGeneralForm.form.isValid()){
            this.WtfGeneralForm.form.submit({
                scope : this,
                params:{
                        "action":(this.action),
                        "id":(this.action == "Edit")?this.rec.get("compid"):0,
                        "computeon":this.methodCombo.getValue()==1?this.computeonCombo.getValue():"",
                        "rules":this.methodCombo.getValue()==3?(this.isEdit?"":this.genJsonForRule()):"",
                        "taxablecomponent":this.taxableComponent.getValue()
                        
                     },
                failure: function(frm, action){
                    msgBoxShow(100,1);
                    this.close();
                },
                success: function(frm, action){
                   var msg=action.result.success;
                   if(msg){
                	   if(!action.result.isMethodChanged){
                		   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.method.cannot.changed.depends.other.component")],2);
                		   Wtf.getCmp("Item-submit-btn").setDisabled(false);
                		   return;
                	   }
                	   
                	   if(action.result.isDuplicateCode){
                		   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.component.code.already.exist")],2);
                		   Wtf.getCmp("Item-submit-btn").setDisabled(false);
                	   }else{
                		   if(this.action == "Edit"){
                			   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow137")]);
                		   }else{
                			   calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow136")]);
                		   }
                		   var grd=Wtf.getCmp("ComponentGrid");
                           if(grd){
                               grd.getStore().reload();
                           }
                           this.close();
                	   }
                   }else{
                       calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.error.adding.payroll.component")],1);
                   }
                }
            },this);
        }else{
            Wtf.getCmp("Item-submit-btn").setDisabled(false);
        }
    },
    
    callSpecifiedFormula:function(){
                
                this.hideComputedOnCombo(100, true, false, "","");
		this.Amount.show();
                this.Amount.allowBlank=false;
                
                
                
                this.rulesGrid.hide();
                this.specifiedFormula.show();
                this.doLayout();
                
		this.changeLabelText(WtfGlobal.getLocaleText("hrms.payroll.percent")+"* :");
		
		var spPanel = new Wtf.AddSpecifiedFormula({
			title:WtfGlobal.getLocaleText("hrms.payroll.specifiedformula"),
        	layout:"fit",
        	width:400,
        	height:300,
        	frequency:this.frequency,
        	componentid:this.rec==undefined?"":this.rec.data.compid,
        	expression:this.expression==undefined?(this.rec==undefined?"":this.rec.data.expression):this.expression
		});
		spPanel.show();
		spPanel.on("specified_formula",function(expression, expression1){
			this.expression=expression1;
			this.loadSpecifiedFormula(expression1);
			this.specifiedFormulaHidden.setValue(expression);
		},this);
    },
    
    changeLabelText : function(labelText){
        
        var dd_textfield = Wtf.getCmp(this.id+"amount_component");
        var ct = dd_textfield.el.findParent('div.x-form-item', 3, true);
        var label = ct.first('label.x-form-item-label');
        ct.first('label.x-form-item-label').dom.innerHTML = labelText;
    },
    
    removeChildNode : function(){
        
       if(this.conContainer.hasChildNodes()){
            while(this.conContainer.childNodes.length >= 1){
                this.conContainer.removeChild( this.conContainer.firstChild );       
            } 
        }
    },
    
    hideComputedOnCombo : function(maxAmountVal, allowBlank, show, labelText, value){
        
        if(show){
            this.computeonCombo.show();
        } else {
            this.computeonCombo.hide();
        }
        
        this.computeonCombo.setValue(value);
        this.Amount.maxValue = maxAmountVal;    
        this.computeonCombo.allowBlank = allowBlank;
        var dd_textfield1 = Wtf.getCmp(this.id+"computedOn_combofield");
        
        if(dd_textfield1.el!=undefined){
            var ct = dd_textfield1.el.findParent('div.x-form-item', 4, true);
            var label = ct.first('label.x-form-item-label');
            ct.first('label.x-form-item-label').dom.innerHTML = labelText;
        }
        
        
    }
});













Wtf.AddSpecifiedFormula = function(config) {
    Wtf.apply(this, config);
    Wtf.AddSpecifiedFormula.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.AddSpecifiedFormula, Wtf.Window, {
	modal:true,
    initComponent: function(config) {
	this.addEvents({
        'specified_formula':true        
    });
	Wtf.AddSpecifiedFormula.superclass.initComponent.call(this, config);
	this.computeonRec = Wtf.data.Record.create([{
		"name":"compid"
	},{
		"name":"code"
	},{
		"name":"sdate"
	},{
		"name":"edate"
	},{
		"name":"desc"
	},{
		"name":"type"
	},{
		"name":"typename"
	},{
		"name":"componentname"
	}]);
	                                            
	this.computeonStore = new Wtf.data.Store({
		baseParams: {
			flag: 101
		},
		url: "Payroll/Date/getComputeOnComponents.py",
		reader: new Wtf.data.KwlJsonReader1({
			root: 'data',
			totalProperty:'count',
			id:'compid'
		},this.computeonRec)
	});
	
	this.computeonStore.on('beforeload',function(store,option){
        option.params= option.params||{};
        option.params.frequency=this.frequency;
        option.params.componentid=this.componentid;
    },this);
	
	this.computeonStore.load();
	
	this.computeonStore.on("load", function(){
		var records = eval(this.expression);
		if(records!=undefined){
		for(var i=0; i<records.length; i++){
			if(this.coeff==null||this.coeff==undefined){
				this.coeff = [];
				this.oper = [];
			}
			this.coeff[i]=records[i].coefficient;
			this.oper[i]=records[i].operator;
			this.sm.selectRow(this.computeonStore.find('compid',records[i].component), true);
			//this.sm.selectRecords(this.computeonStore.getById(records[i].component), true);
		}
		}
	}, this);
	
	this.coechild = [];//coefficient
    this.aschild = [];//add or sub
    this.cchild = [];//component
    this.componentname = [];
    this.mainchild = [];
    this.conContainer = document.createElement('div');
    this.conContainer.className = 'conContainer';
    this.conContainer.id = 'parentCon';
    
    this.cm=new Wtf.grid.ColumnModel([
        new Wtf.grid.CheckboxSelectionModel(),
        new Wtf.grid.RowNumberer(),
        {
        	header: WtfGlobal.getLocaleText("hrms.payroll.component"),
        	dataIndex:'code',
        	sortable: true
        },{
        	header: WtfGlobal.getLocaleText("hrms.common.type"),
        	dataIndex:'typename',
        	sortable: true
        }
    ]);
    
    this.sm = new Wtf.grid.CheckboxSelectionModel();
    this.sm.on("rowselect",this.onRowSelect,this);
    this.sm.on("rowdeselect",this.onRowDeselect,this);
    this.specifiedFormula = new Wtf.Panel({
    	region:'center',
        id:'feed',
        layout:'fit',
        border:false,
        items:[{
            layout:'border',
            cls:'spFormula',
            height:(Wtf.isIE7 || Wtf.isIE8)?240:230,
            bodyStyle: "background-color: #f1f1f1;",
            border:false,
            layoutConfig:{labelSeparator:''},
            items:[
                 new Wtf.form.FieldSet({
                    height:(Wtf.isIE7 || Wtf.isIE8)?120:160,
                    region:'north',
                    layout:'fit',
                    id:'rules',
                    title:WtfGlobal.getLocaleText("hrms.payroll.1.select.wage.components"),
                    items:[
                       this.grid = new Wtf.KwlGridPanel({
                            id:'rulegrid',
                            store:this.computeonStore,
                            serverSideSearch:true,
                            cm: this.cm,
                            searchLabel:" ",
                            searchLabelSeparator:" ",
                            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.component.grid.search.msg"),
                            searchField:"code",
                            paging: false,
                            sm: this.sm,
                            viewConfig:{
                                forceFit:true
                            }
                        })
                    ]
                }),
                this.formula = new Wtf.form.FieldSet({
                    region:'center',
                    id:'subrules',
                    bodyStyle: 'overflow-y:scroll;',
                    title:WtfGlobal.getLocaleText("hrms.payroll.2.your.formula"),
                    items:[
                        new Wtf.Panel({
                            id:'addCon',
                            border:false,
                            contentEl:this.conContainer
                        })
                    ]
                })
            ]
        }],
        buttonAlign :'right',
        buttons: [{
            anchor : '90%',
            text: WtfGlobal.getLocaleText("hrms.common.ok"),
            id:'Item-submit-btn1',
            handler: function(){
        		var expr = new Array();
        		var expr1 = new Array();
        		for ( var ctr=0; ctr < this.mainchild.length; ctr++){
        			if(this.mainchild[ctr]!=undefined){
        				var temp={
        				operator:this.aschild[ctr].val,
        				coefficient:this.coechild[ctr].val,
        				component:this.cchild[ctr].val,
        				componentname:this.componentname[ctr].val
        				};
        				expr[ctr]=Wtf.encode(temp);
        				expr1[ctr]=temp;
        			}
        		}
        		this.fireEvent('specified_formula',Wtf.encode(expr), expr1);
        		this.close();
        	},
            scope:this
        },{
            anchor : '90%',
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    });
    this.add(this.specifiedFormula);
	},
	
	onRowDeselect:function(sel,row,rec){
		this.mainchild[row].removeChild(this.coechild[row]);
		this.coechild[row]=undefined;
		this.mainchild[row].removeChild(this.aschild[row]);
		this.aschild[row]=undefined;
		this.mainchild[row].removeChild(this.cchild[row]);
		this.cchild[row]=undefined;
		this.mainchild[row].removeChild(this.componentname[row]);
		this.componentname[row]=undefined;
		this.conContainer.removeChild(this.mainchild[row]);
		this.mainchild[row]=undefined;
    },

    onRowSelect:function(sel,row,rec){
    	if(this.mainchild[row]==undefined){
    		this.componentname[row] = document.createElement('div');
    		this.componentname[row].id = "componentname"+rec.data.componentname;
    		this.componentname[row].val = rec.data.componentname;
    		
    		this.mainchild[row] = document.createElement('div');
    		this.mainchild[row].id = rec.data.compid;
    	    
    		this.cchild[row] = document.createElement('div');
    		this.cchild[row].id = "cchild"+rec.data.compid;
    		this.cchild[row].val = rec.get("compid");
    		this.cchild[row].innerHTML = rec.get("code");
	        this.cchild[row].className="x-form-item";
    		if(Wtf.isIE7 || Wtf.isIE8){
    			this.cchild[row].style.display = "inline";
    		}else{
    			this.cchild[row].style.cssFloat = "left";
    		}
    		
    		this.coechild[row] = document.createElement('div');
    		this.coechild[row].id = "coechild"+rec.data.compid;
    		this.coechild[row].className="x-form-item";
    		if(Wtf.isIE7 || Wtf.isIE8){
    			this.coechild[row].style.display = "inline"; 
    		} else{
	        	this.coechild[row].style.cssFloat = "left";
    		}
	        if(this.coeff!=null&&this.coeff!=undefined&&this.coeff.length>0&&this.coeff[row]!=undefined){
	        	this.coechild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+this.coeff[row]+")</a>&nbsp;";
	        	this.coechild[row].val = this.coeff[row];
	        } else {
	        	this.coechild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+1+")</a>&nbsp;";
	        	this.coechild[row].val = 1;
	        }
	        
	        this.aschild[row] = document.createElement('div');
    		this.aschild[row].id = "aschild"+rec.data.compid;
    		this.aschild[row].className="x-form-item";
	        if(Wtf.isIE7 || Wtf.isIE8)
	        	this.aschild[row].style.display = "inline";
	        else
	        	this.aschild[row].style.cssFloat = "left";
	        if(this.oper!=null&&this.oper!=undefined&&this.oper.length>0){
	        	if(this.oper[row]=="+")
	        		this.aschild[row].val = "+";
	        	else if(this.oper[row]=="-")
	        		this.aschild[row].val = "-";
	        	else{
	        		this.aschild[row].val = "+";
	        		this.oper[row] = "+";
	            }
	        	this.aschild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>("+this.oper[row]+")</a>&nbsp;";
	        }else{
	        	this.aschild[row].val = '+';
	        	this.aschild[row].innerHTML = "&nbsp;<a href=javascript:void(0)>(+)</a>&nbsp;";
	        }
	        
	        if(Wtf.isIE7 || Wtf.isIE8){
	        	this.aschild[row].onclick = this.showRuleWin.createDelegate(this, [this.aschild[row]], false);
	        	this.coechild[row].onclick = this.showCoeffWin.createDelegate(this, [this.coechild[row]], false);
	        }else{
	        	this.aschild[row].onclick = this.showRuleWin.createDelegate(this);
	        	this.coechild[row].onclick = this.showCoeffWin.createDelegate(this);
	        }
	            
	        this.mainchild[row].appendChild(this.aschild[row]);//operator
	        this.mainchild[row].appendChild(this.coechild[row]);//coefficient
	        this.mainchild[row].appendChild(this.cchild[row]);//component
	        this.mainchild[row].appendChild(this.componentname[row]);//component
	        this.conContainer.appendChild(this.mainchild[row]);
    	}
    },
    
    showCoeffWin:function(e){
        if(Wtf.isIE7 || Wtf.isIE8){
    		this.updateelement = e;
        }else{
        	this.updateelement = e.currentTarget;
        }
        var top = new Wtf.Panel({
            frame:true,
            items: [{
                layout:'form',
                items:[{
                    layout:'column',
                    items: [{
                        html:WtfGlobal.getLocaleText("hrms.payroll.coefficient")+':<br><br>'
                    }]
                },{
                layout: 'column',
                fieldWidth:0,
                items: [
                    new Wtf.form.NumberField({
                        name:'coeff',
                        id:'coefffield',
                        value : this.updateelement.val,
                        allowNegative : false,
                        minValue : 0,
                        maxValue : 100,
                        decimalPrecision : 4
                    })
                ]}]
            },{
                layout:'column',
                items:[{
                    layout:'form',
                    buttons:[{
                        text:WtfGlobal.getLocaleText("hrms.common.ok"),
                        scope:this,
                        handler:function(){
                             var val = Wtf.getCmp("coefffield").value;
                             if(!Wtf.getCmp("coefffield").validateValue(val)){
                            	 return ;
                             }
                             this.updateelement.innerHTML="&nbsp;<a href=javascript:void(0)>"+val+"</a>&nbsp;";
                             this.updateelement.val = val;
                             win.close();
                        }
                    },{
                        text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                        handler:function(){
                            win.close();
                        }
                    }]
                }]
            }]
        });
        var win = new Wtf.Window({
            title:WtfGlobal.getLocaleText("hrms.payroll.coefficient"),
            closable:true,
            width:200,
            iconCls:'winicon',
            resizable:false,
            autoDestroy:true,
            modal:true,
            border:false ,
            id:'coefficientWindow',
            items: [top]
        });
        win.show();
    },
        
    showRuleWin:function(e){
    	if(Wtf.isIE7 || Wtf.isIE8){
        		this.updateelement = e;
    	}else{
            this.updateelement = e.currentTarget;
    	}
    	var top = new Wtf.Panel({
    		frame:true,
    		items: [{
    			layout:'form',
    			items:[{
    				layout:'column',
    				items: [{
    					html:WtfGlobal.getLocaleText("hrms.payroll.ApplyRuleIf")+':<br><br>'
                    }]
    			},{
    				layout: 'column',
                    fieldWidth:0,
                    items: [
                        new Wtf.form.Radio({
                            name:'cond',
                            id:'add',
                            checked : (this.updateelement.val =='+')?true:false,
                            boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Addition")
                        })
                    ]
               },{
            	   layout:'column',
            	   fieldWidth:0,
            	   items: [
            	           new Wtf.form.Radio({
            	        	   name:'cond',
            	        	   id:'sub',
            	        	   checked : (this.updateelement.val =='-')?true:false,
            	        	   boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Subtraction")
            	           })]
                    	}]
                	},{
                		layout:'column',
                		items:[{
                			layout:'form',
                			buttons:[{
                				text:WtfGlobal.getLocaleText("hrms.common.ok"),
                				scope:this,
                				handler:function(){
                                	if(Wtf.getCmp("add").checked == true){
                                		this.rad = 0;
                                	}else if(Wtf.getCmp("sub").checked == true){
                                		this.rad = 1;
                                	}
                                	if(this.rad==1){
                                		this.updateelement.innerHTML="&nbsp;<a href=javascript:void(0)>(-)</a>&nbsp;";
                                		this.updateelement.val = '-';
                                	}else{
                                		this.updateelement.innerHTML="&nbsp;<a href=javascript:void(0)>(+)</a>&nbsp;";
                                		this.updateelement.val = '+';
                                	}
                                	win.close();
                            	}
                			},{
                				text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                				handler:function(){
                					win.close();
                            	}
                			}]
                		}]
                	}]
            	});
            	Wtf.getCmp("add").on("check",this.invRadiobttn,this);
            	Wtf.getCmp("sub").on("check",this.invRadiobttn,this);
                var win = new Wtf.Window({
                    title:WtfGlobal.getLocaleText("hrms.payroll.andor"),
                    closable:true,
                    width:200,
                    iconCls:'winicon',
                    resizable:false,
                    autoDestroy:true,
                    modal:true,
                    border:false ,
                    id:'conditionWindow',
                    items: [top]
                });
                win.show();
            },
       
    invRadiobttn:function(obj,chk){
        if(obj.id=="add"){
        	Wtf.getCmp("sub").checked =false;
        }
        if(obj.id=="sub"){
        	Wtf.getCmp("add").checked =false;
        }
    }
});




Wtf.SpcifiedFormulaIcon= function(conf){
    Wtf.apply(this, conf);
    Wtf.SpcifiedFormulaIcon.superclass.constructor.call(this, conf);
    this.addEvents({
        beforeFilter: true,
        afterFilter: true
    });
};

Wtf.extend(Wtf.SpcifiedFormulaIcon, Wtf.util.Observable, {
    init: function(combo){
        this.combo = combo;
        combo.on("render", function(conf){
            this.SpcifiedFormulaIcon();
        }, this);
    },

    SpcifiedFormulaIcon: function(){
        var _cS = this.combo.getSize();
        this.width = (this.combo.width !== undefined) ? this.combo.width : _cS.width;
        this.width -= 25;
        this.combo.setWidth(this.width);
        var _cD = this.combo.el.dom;
        var _pD = _cD.parentNode;
        _cD.style.width = (this.width - 25) + "px";
        this._fI = document.createElement("img");
        this._fI.src = "../../images/addCompo.gif";
        this._fI.title=WtfGlobal.getLocaleText("hrms.payroll.specifiedformula");
        this._fI.height = 20;
        this._fI.width = 20;
        this._fI.style.left = this.width + "px";
        this._fI.style.position = "absolute";
        this._fI.style.margin = "0px 0px 0px 5px";
        this._fI.style.cursor = "pointer";
        this._fI.onclick = this.handler.createDelegate(this.scope, []);
        _pD.appendChild(this._fI);
    }
});
