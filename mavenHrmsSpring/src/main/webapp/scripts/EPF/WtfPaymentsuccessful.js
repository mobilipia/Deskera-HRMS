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
Wtf.leavem.PaymentSucces = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.PaymentSucces.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.leavem.PaymentSucces, Wtf.Window, {
    onRender: function(config) {
        Wtf.leavem.PaymentSucces.superclass.onRender.call(this, config);
//        this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.empProfile));
        this.headingType="";
        this.confirmpanel= new Wtf.Panel({
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
                    html:getTopHtml("Step 3/3 : Congratulations..." ,"Congratulations...","../../images/deskera/easy-epf-logo-app.jpg")
                },{
                    border:false,
                    region:'center',
                    //bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    cls:'windowstyle1',
                    layout:"fit",
                    items: [
                    this.Confirmationform = new Wtf.form.FormPanel({
                        bodyStyle:'padding:7px 7px 7px 7px;background-color: #f1f1f1;font-size:10px;padding:10px 20px;',
                        url: Wtf.req.tmjsp+'travelManager.jsp?flag=73&companyid='+this.companyid,
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        lableWidth :120,
                        layoutConfig: {
                            deferredRender: false
                        },
                        items:[
                             {
                             xtype : 'panel',
                            id: "approverinfo1",
                            border: false,
                            cls:'infocss1',
                            bodyStyle: "color:gray",
                            html:"Your Payment is Successful.<br>You will receive an email confirmation shortly with the details."
                            },{
                            xtype:'fieldset' ,
                            title:"Payment Details",
                            scope:this,
                            height:100,
                            bodyStyle:"padding-left:15px;font-size:12px;",
                            html:"<div><table border='0px'><tr><td>Payment Successfully made to :</td><td> "+this.madeto+"</td></tr><tr><td>Amount(RM) :</td><td> "+this.amtval+"</td></tr></table></div>"
//                            items:[
//                                this.txtName1 = new Wtf.form.TextField({
//                                    fieldLabel:"Payment Successfully made to",
//                                    allowBlank: false,
//                                    disabled:true,
//                                    name:'payto',
//                                    maxLength:100,
//                                    value:this.madeto,
//                                    anchor:'93%',
//                                    height: '18px'
//                                }),
//                                this.txtName2 = new Wtf.form.TextField({
//                                    fieldLabel:"Amount(RM)",
//                                    disabled:true,
//                                    allowBlank: false,
//                                    name:'amt',
//                                    value:this.amtval,
//                                    maxLength:100,
//                                    anchor:'93%',
//                                    height: '18px'
//                                }),
//                            ]
                            }
                        ]
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text: 'Print',
                id:'prnt-btn',
                handler: this.Printinvoice,
                scope:this
            },{
                 anchor : '90%',
                text:  WtfGlobal.getLocaleText("hrms.common.Close"),
                id:'Close-btn',
                handler: this.Closebtn,
                scope:this
            }
            ]
        });
        this.add(this.confirmpanel);
    },

    Printinvoice: function(){

    },
    Closebtn:function(){
        this.close();
    }
});


Wtf.leavem.onlinebankPaySucces = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.onlinebankPaySucces.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.leavem.onlinebankPaySucces, Wtf.Window, {
    onRender: function(config) {
        Wtf.leavem.onlinebankPaySucces.superclass.onRender.call(this, config);
//        this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.empProfile));
        this.headingType="";
        if(this.confirmFlag == 1) {
            this.itemArr = [
                this.txtName2 = new Wtf.form.TextField({
                    fieldLabel:"TAC",
                    allowBlank: false,
                    name:'tac',
                    maxLength:100,
                    anchor:'93%'
                })
            ];
            this.bttnArr = [
                {
                    anchor : '90%',
                    text: 'Confirm',
                    id:'continue-btn1',
                    handler: this.confirmHandler,
                    scope:this
                },{
                    anchor : '90%',
                    text: 'Go back',
                    id:'back-btn',
                    handler: this.backHandler,
                    scope:this
                }
                ];
            this.contentFields = "<div><table border='0px'>\n\
                <tr><td>From Account :</td><td>164490150423 WSA</td></tr>\n\
                <tr><td>Corporation Name :</td><td>Kumpulan Wang Simpanan Pekerja</td></tr>\n\
                <tr><td>Account No. :</td><td>810085003</td></tr>\n\
                <tr><td>Mobile No. :</td><td>XM2U1315966842</td></tr>\n\
                <tr></tr>\n\
                <tr><td>Amount :</td><td>RM "+WtfGlobal.currencyRendererEPFOnly(this.amtval)+"</td></tr>\n\
                <tr><td>Effective Date :</td><td>Today</td></tr>\n\
                </table></div>";
            this.tacContents = "This transaction requires a TAC.(<a href='#'>What is a TAC?</a>)<br/><a href='#'>Request a TAC number</a>";
        } else if(this.confirmFlag == 2) { 
            this.itemArr = "";
            this.bttnArr = [
                {
                    anchor : '90%',
                    text: 'Continue',
                    id:'continue-btn2',
                    handler: this.continueHandler,
                    scope:this
                }
                ];
            this.contentFields = "<div><table border='0px'>\n\
                <tr><td>From Account :</td><td>164490150423 WSA</td></tr>\n\
                <tr><td>Corporation Name :</td><td>Kumpulan Wang Simpanan Pekerja</td></tr>\n\
                <tr><td>Account No. :</td><td>810085003</td></tr>\n\
                <tr><td>Mobile No. :</td><td>XM2U1315966842</td></tr>\n\
                <tr></tr>\n\
                <tr><td>Amount :</td><td>RM "+WtfGlobal.currencyRendererEPFOnly(this.amtval)+"</td></tr>\n\
                <tr><td>Effective Date :</td><td>Today</td></tr>\n\
                </table></div>";
            this.tacContents = "";
        } else if(this.confirmFlag == 3){
            this.itemArr = "";
            this.bttnArr = [
                {
                    anchor : '90%',
                    text: 'Print',
                    id:'prnt-btn',
                    handler: this.Printinvoice,
                    scope:this
                },{
                     anchor : '90%',
                    text:  WtfGlobal.getLocaleText("hrms.common.Close"),
                    id:'Close-btn',
                    handler: this.Closebtn,
                    scope:this
                }
                ];
            this.contentFields = "<div><table border='0px'>\n\
                <tr><td>Status :</td><td>Successful</td></tr>\n\
                <tr><td>Reference No. :</td><td>1544504594</td></tr>\n\
                <tr><td>Transaction Date :</td><td>20 Sept 2011</td></tr>\n\
                <tr><td>Transaction Time :</td><td>2:30:45</td></tr>\n\
                <tr></tr>\n\
                <tr><td>From Account :</td><td>164490150423 WSA</td></tr>\n\
                <tr><td>Corporation Name :</td><td>Kumpulan Wang Simpanan Pekerja</td></tr>\n\
                <tr><td>Account No. :</td><td>810085003</td></tr>\n\
                <tr><td>Mobile No. :</td><td>XM2U1315966842</td></tr>\n\
                <tr></tr>\n\
                <tr><td>Amount :</td><td>RM "+WtfGlobal.currencyRendererEPFOnly(this.amtval)+"</td></tr>\n\
                <tr><td>Effective Date :</td><td>Today</td></tr>\n\
                </table></div>";
            this.tacContents = "";
        }
            
        this.confirmpanel= new Wtf.Panel({
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
                    html:getTopHtml("EPF Payment" ,"","../../images/deskera/easy-epf-logo-app.jpg")
                },{
                    border:false,
                    region:'center',
                    //bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    cls:'windowstyle1',
                    layout:"fit",
                    items: [
                    this.Confirmationform = new Wtf.form.FormPanel({
                        bodyStyle:'padding:7px 7px 7px 7px;background-color: #f1f1f1;font-size:10px;padding:10px 20px;',
                        url: Wtf.req.tmjsp+'travelManager.jsp?flag=73&companyid='+this.companyid,
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        lableWidth :120,
                        layoutConfig: {
                            deferredRender: false
                        },
                        items:[
                             {
                             xtype : 'panel',
                            id: "approverinfo1"+this.confirmFlag,
                            border: false,
                            cls:'infocss1',
                            bodyStyle: "margin-bottom:15px;color:gray",
                            html:this.tacContents
                            },{
                            xtype:'fieldset' ,
                            title:"Payment Details",
                            scope:this,
                            height:225,                            
                            bodyStyle:"padding-left:15px;font-size:12px;",
                            html:this.contentFields,
                            items: this.itemArr                                
                            }
                        ]
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: this.bttnArr
        });
        this.add(this.confirmpanel);
    },

    Printinvoice: function(){

    },
    Closebtn:function(){
        this.close();
        Wtf.getCmp("as").remove(Wtf.getCmp("tabsunrisecalibar"))
    },
    continueHandler:function(){
        this.close();
         var getconfwindow=new Wtf.leavem.onlinebankPaySucces({
            width:500,
            madeto:this.madeto,
            amtval:this.amtval,
            modal:true,
            height:450,
//            resizable:false,
            listType:'New',
            iconCls : getTabIconCls(Wtf.etype.iconwin),
            title:"maybank2u.com",
            leaverec:null,
            leaveid:null,
            userid:"",
            layout:'fit',
            confirmFlag:3
         });
        getconfwindow.show();
    },
    confirmHandler:function(){
        this.close();
         var getconfwindow=new Wtf.leavem.onlinebankPaySucces({
            width:500,
            madeto:this.madeto,
            amtval:this.amtval,
            modal:true,
            height:450,
//            resizable:false,
            listType:'New',
            iconCls : getTabIconCls(Wtf.etype.iconwin),
            title:"maybank2u.com",
            leaverec:null,
            leaveid:null,
            userid:"",
            layout:'fit',
            confirmFlag:2
         });
        getconfwindow.show();
    },
    backHandler:function(){
        this.close();
        maybankloginwindow(this.amtval);
    }
});


