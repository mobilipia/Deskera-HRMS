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
Wtf.CreateApplicant=function(config){
this.Form=new Wtf.form.FormPanel({
        border:false,
        region:'center',
        autoScroll:true,        
        style: "padding-Left:0px;padding-top:10px;padding-right: 40px;",
        layout:'form',
        labelWidth:105,
        defaults: {xtype:"textfield", width:200, allowBlank:false, maxLength:50},
        items:[{
            name:"fname",
            maxLength:50,
            validator:WtfGlobal.noBlankCheck,
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.profile.FirstName")+"*"
        },{
            name:"lname",
            maxLength:50,
            validator:WtfGlobal.noBlankCheck,
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.profile.LastName")+"*"
        },/*{
            xtype:"textarea",
            name:"addr",
            maxLength:255,
            fieldLabel:"Address*"
        },*/{
            name:"e",
            id: "applicantemailid",
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.Email")+"*",
            vtype:"email"
        },/*{
            name:"u",
            fieldLabel:"User Name*",
            maxLength:80
        },this.pass=new Wtf.form.TextField({
            name:"p",
            inputType:"password",
            fieldLabel:"Password*",
            id:'companyadminpass',
             vtype:"password"
        }),this.repass=new Wtf.form.TextField({
            name:"newpassword2",
            fieldLabel:"Retype Password*",
            inputType:"password",
            maxLength:50,
            vtype:"password",
           id:'recompanyadminpass',
           initialPassField: 'companyadminpass'
       }),*/this.contact=new Wtf.form.NumberField({
            name:"contact",
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.profile.ContactNo")+"*",
            maxLength:10,
             validator: function (value){
                var numtype=/^[0-9-]{3,25}$/;
                if (numtype.test(value))
                    return true;
                return false;
            }
        }),{
            xtype:"hidden",
            name:"compname",
           value:"demo"
        }]
    });
    var wintitle=WtfGlobal.getLocaleText("hrms.recruitment.create.applicant");
    var windetail='';
    var image='';
    windetail=WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails");
    image='../../images/createuser.png';
    this.CreatePanel=new Wtf.Panel({
        region:'center',
        border:false,
        autoScroll:true,
        layout:"border",
 
        bodyStyle: "padding-Left:10px;padding-top:10px;padding-right: 0px;",
        items:[{
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(wintitle,windetail,image)
        },{
            region :"center",
            border:false,
            bodyStyle: "padding-Left:20px;padding-top:20px;padding-right: 0px;background-color:#f1f1f1;",
           items:[this.Form]
        }]
    });
    Wtf.apply(this,{
        layout : "fit",
        items:[{
            border:false,
            layout:'border',
            items:[this.CreatePanel]
        }],
        buttons:[{
            text: WtfGlobal.getLocaleText("hrms.common.Create"),
            scope:this,
            handler:this.saveApplicant.createDelegate(this)
        },{
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope:this,
            handler:function(){this.close()}
        }]
    },config)
//    this.pass.on('change'hei,this.enablePwd,this);
//    this.repass.on('change',this.checkPwd,this);
    Wtf.CreateApplicant.superclass.constructor.call(this,config);
    this.addEvents({
        'updategrid':true
    });
}
Wtf.extend(Wtf.CreateApplicant,Wtf.Window,{
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
         Wtf.CreateApplicant.superclass.onRender.call(this,config);     
    },
    enablePwd:function(a,val){
         if(val.length<4)
              Wtf.Msg.show({
                title: WtfGlobal.getLocaleText("hrms.common.error"),
                msg:  WtfGlobal.getLocaleText("hrms.common.PasswordshouldbemorethanFourcharacter"),
                buttons: Wtf.Msg.OK,
                animEl: 'elId',
                icon: Wtf.MessageBox.ERROR
        });
    },
    checkPwd:function(a,val){
         if(val!=this.pass.getValue()){
            Wtf.Msg.show({
                    title: WtfGlobal.getLocaleText("hrms.common.error"),
                    msg: WtfGlobal.getLocaleText("hrms.common.EnterSamePassword"),
                    buttons: Wtf.Msg.OK,
                    animEl: 'elId',
                    icon: Wtf.MessageBox.ERROR
            });a.setValue("");
          }
    },
    saveApplicant:function(){
        var rec=this.Form.getForm().getValues();
        //if(this.pass.getValue().length>0)rec.p=hex_sha1(this.pass.getValue());
        if(!this.Form.getForm().isValid())
            return;
        else{
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.common.Areyousureyouwanttocreateanapplicant"),function(btn){
                if(btn!="yes") return;
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
//                    url: Wtf.req.base + 'hrms.jsp?flag=153',
                    url:"Rec/Job/createapplicantFunction.rec",
                    params: rec
                },
                    this,
                     function(response){
                         
                           var res=eval('('+response+')');                            
                            calMsgBoxShow([""+res.type+"",""+res.msg+""],0);
                            this.fireEvent('updategrid',this);
                            if(res.type != "Warning" || res.type !=WtfGlobal.getLocaleText("hrms.common.warning")){
                                this.Form.getForm().reset();
                                this.close();
                            } else{
                                Wtf.getCmp("applicantemailid").setValue("");
                            }
                    },
                     function(){
                        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.Message"),WtfGlobal.getLocaleText("hrms.CampaignDetail.Someerroroccoured"));
                    }
               )
             },this); 
        }
    }, 
    genSuccessResponse:function(res){
        var msg;
        res=eval('('+res+')');
        if (res.data =="msg:{succcess: true}") {
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.common.Applicantcreatedsuccessfully"));
            this.fireEvent('updategrid',this);
            this.Form.getForm().reset();
        }
        else {
            if (res.error =="msg:{companyname failure}")
                msg = WtfGlobal.getLocaleText("hrms.common.CompanyNamenotavailable");
            else if (res.error =="msg:{userid failure}")
                msg = WtfGlobal.getLocaleText("hrms.common.Useridnotavailable");
            else
                msg =WtfGlobal.getLocaleText("hrms.CampaignDetail.Someerroroccoured");
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.Message"),msg);
        }
    }, 
    genFailureResponse:function(res){
       var msg;
        res=eval('('+res+')');
        if (res =='{"data":"{"uri":"./applicantLogin.html","success":true}"}') {
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.common.Applicantcreatedsuccessfully"));
            this.fireEvent('updategrid',this);
            this.Form.getForm().reset();
        }
        else 
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.Message"),WtfGlobal.getLocaleText("hrms.CampaignDetail.Someerroroccoured"));
    }

});
