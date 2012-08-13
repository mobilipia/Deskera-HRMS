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

Wtf.leavem.PaymentConf = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.PaymentConf.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.leavem.PaymentConf, Wtf.Window, {
    onRender: function(config) {
        Wtf.leavem.PaymentConf.superclass.onRender.call(this, config);
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
                    html:getTopHtml(this.headingType + "Step 2/3 : Payment Confirmation" ,"Payment Confirmation","../../images/deskera/easy-epf-logo-app.jpg")
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
                        lableWidth :50,
                        layoutConfig: {
                            deferredRender: false
                        },
                        items:[
                             {
                             xtype : 'panel',
                            id: "approverinfo1",
                            border: false,
                            cls:'infocss1',
                            bodyStyle: "color:gray;",
                            html:"Your Payment has not yet been placed. Please verify the details and click the \"Confirm\" button to make the payment "
                            },{
                            xtype:'fieldset' ,
                            title:"Payment Details",
                            scope:this,
                            height:100,
                            bodyStyle:"padding-left:15px;",
                            items:[
                                this.txtName1 = new Wtf.form.TextField({
                                    fieldLabel:"Payment To",
                                    allowBlank: false,
                                    name:'payto',
                                    maxLength:100,
                                    width:250,
                                    anchor:'93%',
                                    value:"Kumpulan Wang Simpanan Pekerja",
                                    height: '18px'
                                }),
                                this.txtName2 = new Wtf.form.TextField({
                                    fieldLabel:"Amount(RM)",
                                    allowBlank: false,
                                    name:'amt',
                                    maxLength:100,
                                    width:250,
                                    value:WtfGlobal.currencyRendererEPFOnly(this.amount),
                                    anchor:'93%',
                                    readOnly:true,
                                    height: '18px'
                                }),
                            ]
                            },{
                             xtype : 'panel',
                            id: "approverinfo",
                            border: false,
                            cls:'infocss1',
                            bodyStyle: "color:gray",
                            html:"<div style='color:red;'>Note:</div> Click on confirm will popup login to your banking system"
                        }
                        ]
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text: 'Confirm',
                id:'new-desi-btn',
                handler: this.Confirmpayment,
                scope:this
            }
            ]
        });
        this.add(this.confirmpanel);
    },
 
    Confirmpayment: function(){
        var madeto=this.txtName1.getValue();
        var amt=this.txtName2.getValue();
       this.close();
       if(this.isMayBank){
            maybankloginwindow(this.amount);
       }else{
            confirmationsuccess(madeto,amt);
       }
    }
       
});

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Wtf.leavem.maybankloginwindow = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.maybankloginwindow.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.leavem.maybankloginwindow, Wtf.Window, {
    onRender: function(config) {
        Wtf.leavem.maybankloginwindow.superclass.onRender.call(this, config);
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
                    html:getTopHtml(this.headingType + "EPF Payment" ,"<b>Note:</b> You are in secured site","../../images/deskera/easy-epf-logo-app.jpg")
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
                        lableWidth :50,
                        layoutConfig: {
                            deferredRender: false
                        },
                        items:[
                             {
                             xtype : 'panel',
                            id: "approverinfo1",
                            border: false,
                            cls:'infocss1',
                            //bodyStyle: "color:gray;",
                            html:"<b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Welcome</b><br><br>"
                            },{
                            xtype:'fieldset' ,
                            title:"Log in to Maybank2u.com online banking",
                            scope:this,
                            height:70,
                            bodyStyle:"padding-left:15px;",
                            items:[
                                this.txtName1 = new Wtf.form.TextField({
                                    fieldLabel:"Username",
                                    allowBlank: false,
                                    name:'payto',
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                })
                            ]
                            },{
                             xtype : 'panel',
                            id: "securityinfo",
                            border: false,
                            cls:'infocss1',
                            html:"<div ><b>Security information :</b><br clear='all'><br><ul><li>1) Security Alert : <a href=#>Do not click on this phishing website 18/12/07</a></span></li><li>2) Never login via email links</li><li>3) Never reveal your PIN/ or Password to anyone.</li></ul><br><span style='color: rgb(51, 102, 255);'><a href=#>Click here to notify us of any Maybank2u.com 'phishing' website</a></span><br><br><span style='font-weight: bold;'>Forgot your Online Banking password?</span><br>Call our customer care hotline at 1-300-88-6688 or 603-7844-3696<br>if you're overseas ( 24 hours daily, including holidays).<br>"


                        }
                        ]
                    })]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text: 'Next',
                id:'new-desi-btn',
                handler: this.OnlineTicketing,
                scope:this
            }
            ]
        });
        this.add(this.confirmpanel);
    },

    OnlineTicketing: function(){
        
       this.close();
       confirmationsuccessOnlinePay("KWSP ",this.amount);
    
    }

});

