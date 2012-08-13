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
Wtf.processPayrollWin = function(config) {
    Wtf.apply(this, config);
    Wtf.processPayrollWin.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.processPayrollWin, Wtf.Window, {
    onRender: function(config) {
        Wtf.processPayrollWin.superclass.onRender.call(this, config);
        this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.empProfile));
        this.headingType="";
        this.type=[[WtfGlobal.getLocaleText("hrms.payroll.trial"),'5'],[WtfGlobal.getLocaleText("hrms.payroll.final"),'6']];
        this.typestore=new Wtf.data.SimpleStore({
            fields:[{
                name:'type'
            },
            {
                name:'code'
            }],
            data:this.type
        });
        this.typeCombo=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.process")+"*",
            store:this.typestore,
            displayField:'type',
            typeAhead: true,
            valueField:'code',
            allowBlank:false,
            width:200,
            labelWidth:100,
            scope:this,
            hiddenName:'processstatus',
            name:'processstatus',
            mode: 'local',
//            value:'0',
            triggerAction: 'all',
            emptyText:'Select Type',
            selectOnFocus:true
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
                    html:getTopHtml(WtfGlobal.getLocaleText("hrms.payroll.process.payroll"), WtfGlobal.getLocaleText("hrms.common.fill.following.fields"))
                },{
                    border:false,
                    region:'center',
                    cls:'windowstyle',
                    layout:"fit",
                    items: [
                    this.WtfGeneralForm = new Wtf.form.FormPanel({
                        url:'Payroll/Date/processPayrollHistory.py',
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        bodyStyle: "background-color: #f1f1f1; margin: 5px ;padding:20px;",
                        lableWidth :50,
                        layoutConfig: {
                            deferredRender: false
                        },
                        defaults:{
                            anchor:'93%',
                            width: 200,
                            msgTarget: 'side'
                        },
                        defaultType:'textfield',
                        items:[
                        this.typeCombo,
                        this.paymentDate = new Wtf.form.DateField({
                            fieldLabel : WtfGlobal.getLocaleText("hrms.payroll.payment.date"),
                            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.payment.date"),
                            width : 200,
                            format:'Y-m-d',//change because as in CRM input in yyyy-mm-dd format to avoid Error if user selects 'March,2001'date format
                            name : 'paydate'
                        }),
//                        this.printChkBx = new Wtf.form.Checkbox({
//                            id: 'print'+this.id,
//                            border: false,
//                            bodyStyle: "left:-113px; !important",
//                            cls:'chkEmailNotificationPerUser',
//                            scope:this,
//                            checked:false,
//                            fieldLabel:"Print",
//                            name:'print'
//                        }),
//                        this.emailPayslipChkBx = new Wtf.form.Checkbox({
//                            id: 'email'+this.id,
//                            border: false,
//                            cls:'chkEmailNotificationPerUser',
//                            scope:this,
//                            checked:false,
//                            //hideLabel:true,
//                            fieldLabel:"Email Payslip",
//                            name:'email'
//                        }),
                        this.specification = new Wtf.form.TextArea({
                            fieldLabel : WtfGlobal.getLocaleText("hrms.payroll.payment.specification"),
                            width : 200,
                            maxLength:1024,
                            emptyText:WtfGlobal.getLocaleText("hrms.payroll.brief.payment.specification"),
                            name : 'payspecification'
                        }),
                        this.slipTxt1 = new Wtf.form.TextField({
                            width:200,
                            maxLength:100,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.slip.text1"),
                            anchor:'90%',
                            name:'sliptxt1',
                            id:'txtId1' + this.id
                        }),
                        this.slipTxt2 = new Wtf.form.TextField({
                            width:200,
                            maxLength:100,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.slip.text2"),
                            anchor:'90%',
                            name:'sliptxt2',
                            id:'txtId2' + this.id
                        }),
                        this.slipTxt3 = new Wtf.form.TextField({
                            width:200,
                            maxLength:100,
                            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.slip.text3"),
                            anchor:'90%',
                            name:'sliptxt3',
                            id:'txtId3' + this.id
                        })
                        ]
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text: WtfGlobal.getLocaleText("hrms.common.ok.small"),
                id:'Item-submit-btn',
                handler: this.saveRequest,
                scope:this
            },
            {
                anchor : '90%',
                text: WtfGlobal.getLocaleText("hrms.common.Close"),
                handler: function(){
                    this.close();
                },
                scope:this
            }
            ]
        });
        this.add(this.WtfGeneralPanel);
   },

    saveRequest: function(){
        if(this.WtfGeneralForm.form.isValid()){
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.process.selected.employee.salary"), function(btn){
                if(btn!="yes"){
                	Wtf.getCmp("Item-submit-btn").setDisabled(false);
                	return;
                } else{
                	Wtf.getCmp("Item-submit-btn").setDisabled(true);
                }
                calMsgBoxShow(200,4,true);
                this.WtfGeneralForm.form.submit({
                    scope : this,
                    params:{
                            "action":(this.action),
                            "historyid":this.historyid,
                            "userids":this.userids,
                            "enddate":this.enddate,
                            "frequency":this.frequency
                         },
                    failure: function(frm, action){
                        msgBoxShow(100,1);
                        this.close();
                    },
                    success: function(frm, action){
                       var msg=action.result.success;
                       if(msg){
                            Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.salary.processed.successfully"));
                            if(this.grid){
                                this.grid.getStore().reload();
                            }
                       }else{
                           calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.occured")],1);
                       }
                       this.close();
                    }
                },this);
            },this);
        }else{
            Wtf.getCmp("Item-submit-btn").setDisabled(false);
        }
    }
});
