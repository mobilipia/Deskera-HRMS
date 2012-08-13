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

  Wtf.common.UpdateProfile = function(config){
     Wtf.apply(this,{
        title:WtfGlobal.getLocaleText("hrms.common.UpdateProfile"),
        id:'updateProfileWin',
        closable: true,
        modal: true,
        iconCls : 'deskeralogo',
        width: 470,
        height:560,
        resizable: false,
        layout: 'border',
        buttonAlign: 'right',
        renderTo: document.body,
        buttons: [{
            text: WtfGlobal.getLocaleText("hrms.common.Update"),
            scope: this,
            handler:this.saveForm.createDelegate(this),
            disabled:true
        }, {
            text:WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope: this,
            handler: function(){this.close();}
        }]
    },config);

   
    Wtf.common.UpdateProfile.superclass.constructor.call(this, config);
   
}

Wtf.extend( Wtf.common.UpdateProfile, Wtf.Window, {

    onRender: function(config){
        Wtf.common.UpdateProfile.superclass.onRender.call(this, config);
        this.createForm();       
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.UpdateProfile"),WtfGlobal.getLocaleText("hrms.common.UpdateProfile"))
        },{
            region: 'center',
            border: false,
            bodyStyle: 'background:#f1f1f1;font-size:10px;',
            autoScroll:true,
            items:this.userinfo
        });
    },
    createForm:function(){
       this.dfCmb= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Dateformat"),
            hiddenName:'formatid',
            store:Wtf.dfStore,
            width:220,
            valueField:'formatid',
            displayField:'name',
            mode: 'local',
            triggerAction: 'all',
            editable : false
        });

        this.tzCmb= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Timezone"),
            hiddenName:'tzid',
            store:Wtf.timezoneStore,
            valueField:'id',
            width:220,
            displayField:'name',
            mode: 'local',
            triggerAction: 'all',
            editable : false
        });

        if(!Wtf.StoreMgr.containsKey("timezone")){
            Wtf.timezoneStore.load();
            Wtf.StoreMgr.add("timezone",Wtf.timezoneStore)
        }

        this.userinfo= new Wtf.form.FormPanel({
                fileUpload:true,
                baseParams:{mode:12,formname:"account"},
//                url: Wtf.req.base+'UserManager.jsp',
                url:"Common/saveUser.common",
                region:'center',
                cls:"visibleDisabled",
                bodyStyle: "background: transparent;",
                border:false,
                style: "background: transparent;padding:20px;",
                defaultType:'textfield',
                labelWidth:125,
                items:[ {
                            name:'userid',
                            xtype:'hidden'
                        },{
                            fieldLabel:WtfGlobal.getLocaleText("hrms.common.username"),
                            name:'username',
                            id:'username',
                            readOnly:true,
                            cls:"readOnly",
                            width:220
                        },{
                            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employee.id"),
                            name:'employeeid',
                            readOnly:true,
                            cls:"readOnly",
                            width:220
                        },{ 
                            fieldLabel:WtfGlobal.getLocaleText("hrms.common.EMail")+'*',
                            name:'emailid',
                            allowBlank:false,
                            validator:WtfGlobal.noBlankCheck,
                            width:220,
                            maxLength:50,
                            vtype:'email'
                        },{
                            fieldLabel: WtfGlobal.getLocaleText("hrms.common.FirstName")+'*',
                            name: 'firstname',
                            id:'fname',
                            width:220,
                            maxLength:50,
                            validator:WtfGlobal.noBlankCheck,
                            allowBlank:false
                        },{
                            fieldLabel: WtfGlobal.getLocaleText("hrms.common.LastName")+'*',
                            name: 'lastname',
                            id:'lname',
                            maxLength:50,
                            width:220,
                            validator:WtfGlobal.noBlankCheck,
                            allowBlank:false
                        },{
                            fieldLabel: WtfGlobal.getLocaleText("hrms.common.userpicture"),
                            name:'userimage',
                            width:225,
                            inputType:'file',
                            id:'userimage',
                            hidden:isStandAlone?false:true,
                            hideLabel:isStandAlone?false:true
                        },{
                            fieldLabel: WtfGlobal.getLocaleText("hrms.common.contact.no")+'*',
                            name: 'contactnumber',
                            width:220,
                            allowDecimals:false,
                            validationDelay:0,
                            allowBlank:false,
                            maxLength:20,
                            id:'contactno',
                            xtype:'numberfield'
                        },{
                            fieldLabel: WtfGlobal.getLocaleText("hrms.common.address"),
                            name: 'address',
                            width:220,
                            id:'address',
                            maxLength:255,                         
                            xtype:'textarea'
                        },{
                            fieldLabel: WtfGlobal.getLocaleText("hrms.common.AboutMe"),
                            name: 'aboutuser',
                            width:220,
                            id:'aboutme',
                            maxLength:255,                           
                            xtype:'textarea'
                        },this.dfCmb,this.tzCmb
                ]
            });

            if(!Wtf.StoreMgr.containsKey("dfstore")){
                Wtf.dfStore.on('load',function(){
                    this.getRecord();
                },this);
                Wtf.dfStore.load();
                Wtf.StoreMgr.add("dfstore",Wtf.dfStore)
            }else{
                this.getRecord();
            }
        },        
        getRecord:function(){          
           Wtf.Ajax.requestEx({
//                url:Wtf.req.base+"UserManager.jsp",
                url:"Common/getparticularUserDetails.common",
                params:{
                   mode:24,
                   lid:loginid
                }
         },this,this.genSuccessResponse,this.genFailureResponse);

        },
        saveForm:function(){
            if(!this.userinfo.getForm().isValid()){
                calMsgBoxShow(5,0);
                return;
            }
            this.userinfo.getForm().submit({
                waitMsg:WtfGlobal.getLocaleText("hrms.common.Savinguserinformation"),
                waitTitle: WtfGlobal.getLocaleText("hrms.common.PleaseWait"),
                success:function(f,a){this.genSaveSuccessResponse(eval('('+a.response.responseText+')'))},
                failure:function(f,a){this.genSaveFailureResponse(eval('('+a.response.responseText+')'))},
                scope:this
            });            
        },

    genSuccessResponse:function(response){
            this.userinfo.getForm().setValues(response.data[0]);
            this.buttons[0].enable();
    },

    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.UpdateProfile"),msg);
    },

    genSaveSuccessResponse:function(response){
        if(response.success==true){
            updatePreferences();
        }
        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),response.msg],1);
        Wtf.getCmp('updateProfileWin').close();
    },

    genSaveFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"),msg],2);
        Wtf.getCmp('updateProfileWin').close();
    }
});



