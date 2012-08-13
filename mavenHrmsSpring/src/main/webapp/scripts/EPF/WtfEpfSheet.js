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
Wtf.leavem.EpfSheet = function(config) {
    Wtf.apply(this, config);
    Wtf.leavem.EpfSheet.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.leavem.EpfSheet, Wtf.Window, {
    onRender: function(config) {
         Wtf.leavem.EpfSheet.superclass.onRender.call(this, config);
        var comboReader = new Wtf.data.Record.create([
            {name: 'id', type: 'string'},
            {name: 'name', type: 'string'}
        ]);
        
        this.taskpri = [["January","01"],
                           ["February","02"],
                           ["March","03"],
                           ["April","04"],
                           ["May","05"],
                           ["June","06"],
                           ["July","07"],
                           ["August","08"],
                           ["September","09"],
                           ["October","10"],
                           ["November","11"],
                           ["December","12"]];
        this.MonthStore=new Wtf.data.SimpleStore({
              fields:["value","id"],
              data:this.taskpri

          });
          var mdate=new Date().getMonth();
//           var tempval=this.rec.month
//            if(tempval!=""){
//            if(tempval<=9){
//                tempval="0"+this.rec.month;
//            }
//            }else{
             var tempval=mdate+1
          //  }
          this.monthCombo = new Wtf.form.ComboBox({
            hiddenName : 'monthfilter',
            fieldLabel:'Month of Contribution',
            store:this.MonthStore,
            width:200,
            forceSelection:true,
            value:tempval,
            editable: true,
            displayField:'value',
            valueField: 'id',
            mode: 'local',
            triggerAction:'all',
            emptyText:'select month'
        });
        
        this.PaymentForm = new Wtf.form.FormPanel({
            bodyStyle:'padding:7px 7px 7px 7px;background-color: #f1f1f1;font-size:10px;padding:10px 20px;',
                        url: Wtf.req.tmjsp+'travelManager.jsp?flag=73&companyid='+this.companyid,
                        waitMsgTarget: true,
                        method : 'POST',
                        border : false,
                        lableWidth :50,
                        layoutConfig: {
                            deferredRender: false
                        },
            items: [{xtype:'fieldset' ,
                title:"Company Information",
                height:180,
                scope:this,
                items:[
                              this.companyname = new Wtf.form.TextField({
                                    fieldLabel:"Name",
                                    value:this.myrec.companyname,
                                    allowBlank: false,
                                    name:'companyname',
                                    width:200,
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                }),
                                 this.Address = new Wtf.form.TextArea({
                                    fieldLabel:"Address",
                                    allowBlank: false,
                                    value:this.myrec.address,
                                    width:200,
                                    name:'address',
                                    maxLength:1024,
                                    anchor:'93%'
                                }),
                                this.companyno = new Wtf.form.TextField({
                                    fieldLabel:"Company No",
                                    allowBlank: false,
                                    name:'companyno',
                                    value:this.myrec.companyno,
                                    width:200,
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                }),
                                this.companyepfno = new Wtf.form.TextField({
                                    fieldLabel:"Company EPF No",
                                    allowBlank: false,
                                    value:this.myrec.Companyepfno,
                                    width:200,
                                    name:'companyepfno',
                                    maxLength:100,
                                    anchor:'93%',
                                    height: '18px'
                                }),
                               ]},{xtype:'fieldset' ,
                title:"EPF Contribution Information",
                height:100,
                scope:this,
            items:[this.monthCombo,new Wtf.form.TextField({
                                        fieldLabel:"Amount",
                                        name:'amount',
                                        readOnly:true,
                                        value:WtfGlobal.currencyRendererEPFOnly(this.amnt),
                                        anchor:'93%',
                                        emptyText:'',
                                        width:200
                                        })
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
                    html :getTopHtml("EPF Sheet","EPF Sheet","../../images/deskera/easy-epf-logo-app.jpg")
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
                text: 'Generate EPF File and Proceed to payment',
                id:'new-desi-btn',
                handler: this.generatepdfform,
                scope:this
            }
            ]
        });

        this.add(this.PaymentPanel);
    },
     generatepdfform:function(){
        this.close();
        loadEPFPayment(this.rec,this.amnt);
    }

});




