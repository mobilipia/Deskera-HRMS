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
   Wtf.common.CreateCompany=function(config){





this.Form=new Wtf.form.FormPanel({
        border:false,
        region:'center',
        style: "padding-Left:0px;padding-top:10px;padding-right: 40px;",
        layout:'form',
        items:[{
                        xtype:"textfield",
                        name:"fname",
                        width:200,
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        maxLength:50,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.administration.admin.name")+" *"
 

                    },{
                        xtype:"textarea",
                        name:"address",
                        width:200,
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        maxLength:255,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.address")+" *"


                    },{
                        xtype:"textfield",
                        name:"e",
                        width:200,
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        maxLength:50,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.Email")+" *",
                        vtype:"email"


                    },{
                        xtype:"textfield",
                        name:"u",
                        width:200,
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        maxLength:30,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.UserName")+" *"
                        
                    },this.pass=new Wtf.form.TextField({
                        xtype:"textfield",
                        name:"p",
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        inputType:"password",
                        width:200,
                        maxLength:60,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.AuditGroup.Password")+" *",
                        id:'companyadminpass',
                         vtype:"password"

                    }),this.repass=new Wtf.form.TextField({
                        xtype:"textfield",
                        name:"newpassword2",
                        width:200,
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.administration.retype.password"),
                        inputType:"password",
                       vtype:"password",
                       id:'recompanyadminpass',
                       initialPassField: 'companyadminpass'
                           

                    }),{
                        xtype:"textfield",
                        name:"c",
                        width:200,
                        allowBlank:false,
                        validator:WtfGlobal.noBlankCheck,
                        maxLength:50,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.company.name")+" *"


                    }
//                    ,
//                    {
//                        xtype:"textfield",
//                        name:"cdomain",
//                        width:200,
//                        allowBlank:false,
//                        fieldLabel:"Domain *"
//
//
//                    }
                ]
    });

                            var wintitle=WtfGlobal.getLocaleText("hrms.common.CreateCompany")+" ";
                            var windetail='';
                            var image='';
                            windetail=WtfGlobal.getLocaleText("hrms.administration.fill.information.create.company");
                            image='../../images/createuser.png';

    this.CreatePanel=new Wtf.Panel({
        region:'center',
        border:false,
        autoScroll:true,
        layout:"border",
        height:'100%',
        bodyStyle: "padding-Left:10px;padding-top:10px;padding-right: 0px;",
        items:[
                       {        region:"north",
                                height:75,
                                border:false,
                                bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
                                html:getTopHtml(wintitle,windetail,image)
             },
            {
            region :"center",
            border:false,
            bodyStyle: "padding-Left:20px;padding-top:20px;padding-right: 0px;background-color:#f1f1f1;",
            items:[this.Form]
               }]
       });
    Wtf.apply(this,{
        layout : "fit",      
      //  region:'center',
        items:[{
            border:false,
            layout:'border',
            items:[this.CreatePanel]//this.CreatePanel]
        }],
          buttons:[{
            text: WtfGlobal.getLocaleText("hrms.common.Create"),
            scope:this,
            handler:this.saveReceipt.createDelegate(this)
        },{
            text: WtfGlobal.getLocaleText("hrms.common.Close"),
            scope:this,
            handler:function(){this.close()}
        }]
    },config)
    this.pass.on('change',this.enablePwd,this);
    this.repass.on('change',this.checkPwd,this);
    Wtf.common.CreateCompany.superclass.constructor.call(this,config);
     
}
  Wtf.extend(Wtf.common.CreateCompany,Wtf.Window,{
  onRender:function(config){
         Wtf.common.CreateCompany.superclass.onRender.call(this,config);

  },
    enablePwd:function(a,val){
         if(val.length<4)
              Wtf.Msg.show({
                                                title: WtfGlobal.getLocaleText("hrms.common.error"),
                                                msg: WtfGlobal.getLocaleText("hrms.common.PasswordshouldbemorethanFourcharacter"),
                                                buttons: Wtf.Msg.OK,
                                                animEl: 'elId',
                                                icon: Wtf.MessageBox.ERROR
                                        });
             //else
            //this.repass.enable();

        
    },
    checkPwd:function(a,val){
         if(val!=this.pass.getValue()){
                Wtf.Msg.show({
                        title: WtfGlobal.getLocaleText("hrms.common.error"),
                        msg: WtfGlobal.getLocaleText("hrms.administration.enter.same.password"),
                        buttons: Wtf.Msg.OK,
                        animEl: 'elId',
                        icon: Wtf.MessageBox.ERROR
                });a.setValue("");
          }
               

    },
     ShowCheckDetails:function(combo,rec){
         if(rec.get('requiredetail')){
            this.ReceiptSouthForm.show() ;
            this.ReceiptSouthForm.doLayout();
        }
        else{
            this.ReceiptSouthForm.hide();
            this.bank.setValue("");
            this.CheckNo.setValue("");
            this.Description.setValue("");
            this.Account.setValue("");
        }
    },
    GetRecNo:function(){
        return(this.recInitial.getValue()+this.recNo.getValue())
    },
    setbalanceDue:function(){
        this.BalanceDue.setValue(this.Receivable.getValue()-this.Received.getValue());
    },
    saveReceipt:function(){ 
         var rec=this.Form.getForm().getValues();
        if(this.pass.getValue().length>0)rec.p=hex_sha1(this.pass.getValue());
       
        rec.mode=1;
           if(!this.Form.getForm().isValid())
          { return;}
          else
               {
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.CreateCompany"), WtfGlobal.getLocaleText("hrms.administration.create.company"), function(btn){
            if(btn!="yes") return;
          
            Wtf.Ajax.requestEx({
                url: 'signup.jsp',
                params: rec
         },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
               }
    }, 

    genSuccessResponse:function(response){

        
       Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.administration.company.created.successfully"));
        

       this.Form.getForm().reset();
       Wtf.getCmp('comapnylistgrid').getStore().load({params:{start:0,limit:15}});
       bHasChanged=true;
       Wtf.getCmp("createcompany").close();
       this.close();

    },

    genFailureResponse:function(response){
       var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
       if(response.msg)msg=response.msg;
       Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.CreateCompany"), msg);
       Wtf.getCmp("createcompany").close();
       this.close();
    }

});
