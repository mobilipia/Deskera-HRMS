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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
Wtf.leavem.CompanySheet = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.CompanySheet.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.leavem.CompanySheet, Wtf.Window, {
    onRender: function(config) {
         Wtf.leavem.CompanySheet.superclass.onRender.call(this, config);
       
        this.PaymentForm = new Wtf.form.FormPanel({
            bodyStyle:'padding:7px 7px 7px 7px;background-color: #f1f1f1;font-size:10px;padding:10px 20px;',
                       url:"Common/saveUser.common",
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        lableWidth :50,
                        layoutConfig: {
                            deferredRender: false
                        },
            items: [{xtype:'fieldset' ,
                title:WtfGlobal.getLocaleText("hrms.common.company.information"),
                height:180,
                scope:this,
                items:[
                              this.companyname = new Wtf.form.TextField({
                                    fieldLabel:WtfGlobal.getLocaleText("hrms.common.name"),
                                    value:this.myrec.companyname,
                                    allowBlank: false,
                                    name:'companyname',
                                    width:200,
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                }),
                                 this.Address = new Wtf.form.TextArea({
                                    fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.profile.Address"),
                                    value:this.myrec.address,
                                    allowBlank: false,
                                    width:200,
                                    name:'address',
                                    maxLength:1024,
                                    anchor:'93%'
                                }),
                                this.companyno = new Wtf.form.TextField({
                                    fieldLabel:WtfGlobal.getLocaleText("hrms.common.company.no"),
                                    allowBlank: false,
                                    name:'companyno',
                                    value:this.myrec.companyno,
                                    width:200,
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                }),
                                this.companyepfno = new Wtf.form.TextField({
                                    fieldLabel:WtfGlobal.getLocaleText("hrms.common.company.erp.no"),
                                    allowBlank: false,
                                    value:this.myrec.Companyepfno,
                                    width:200,
                                    name:'companyepfno',
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                }),
                               ]}]
        });
        this.PaymentPanel= new Wtf.Panel({
            border: false,
            layout:'fit',
            items:[{
                border:false,
                 region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 75,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html :getTopHtml("Company Details","","../../images/popup_icons/leave-application.gif")
                },{
                    border:false,
                    region:'center',
                    layout:'fit',
                    autoheight:true,
                    height: '400px',
                    items: [this.PaymentForm]
                }]
            }],
            buttonAlign :'right',
            buttons: [
            {
                anchor : '90%',
                text:  WtfGlobal.getLocaleText("hrms.common.submit"),
                id:'new-desi-btn',
                handler: this.generatepdfform,
                scope:this
            },{
                anchor : '90%',
                text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                id:'cancelcinfo-btn',
                handler: this.cancelform,
                scope:this
            }
            ]
        });

        this.add(this.PaymentPanel);
    },
     generatepdfform:function(){
        var companyname=this.companyname.getValue();
        var companyno=this.companyno.getValue();
        var comepfno=this.companyepfno.getValue();
        var addr=this.Address.getValue();
         Wtf.Ajax.requestEx({
            url: "Payroll/Wage/CompanyEPFDetailSave.py",
            params: {
                name:companyname,
                addr:addr,
                compno:companyno,
                epfno:comepfno
        }},this,
        function(res) {
            if(res.ID!=undefined){
              msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.success"), "company Information saved success"],1);

            }else{
                msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"),"Fail to update company information"],2);
            }

        },
        function(res) {
            msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"),"Fail to update company information"],2);
        });
        this.close();
    },
    cancelform:function(){
        this.close();
    }

});





