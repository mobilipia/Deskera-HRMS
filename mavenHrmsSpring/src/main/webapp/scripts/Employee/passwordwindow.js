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
Wtf.passwin = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        width:430,
        height:450,
        buttons: [
        {
            text: WtfGlobal.getLocaleText("hrms.common.submit"),
            handler: this.sendpasswinSave,
            scope:this
        },
        {
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.passwin.superclass.constructor.call(this, config);

};

Wtf.extend(Wtf.passwin, Wtf.Window, {
    initComponent: function() {
        Wtf.passwin.superclass.initComponent.call(this);
    },
    loadAllStores:function(){

    },
    onRender: function(config) {
        Wtf.passwin.superclass.onRender.call(this, config);
        //  this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.empProfile));
        Wtf.QuickTips.init();
        Wtf.form.Field.prototype.msgTarget = 'side';

        this.currentpass = new Wtf.ux.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.CurrentPassword")+'*',
            name:'currentpass',
            inputType:'password',
            width:150,
            validator:WtfGlobal.noBlankCheck,
            allowBlank:false
        });
    
        this.newpass = new Wtf.ux.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.NewPassword")+'*',
            name:'newpass',
            inputType:'password',
            width:150,
            id:'extuserpass',
            minLength:4,
            maxLength:32
        });

        this.newpassret = new Wtf.ux.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.RetypeNewPassword")+'*',
            name:'pass',
            inputType:'password',
            width:150,
            vtype:'password',
            id:'cpwdextuser',
            initialPassField: 'extuserpass'
        });



        this.passwinform = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            id:'passwinform',
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            labelWidth :150,
            layoutConfig: {
                deferredRender: false
            },
            items:[
            this.currentpass, this.newpass, this.newpassret
            ]
        })

        this.headingType=WtfGlobal.getLocaleText("hrms.common.ChangePassword");
        this.passwinpanel= new Wtf.Panel({
            frame:true,
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 70,
                    border : false,
                    cls : 'panelstyleClass1',
                    html:this.isview?getTopHtml(this.headingType,""):getTopHtml(this.headingType,WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"))
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [
                    this.passwinform
                    ]
                }]
            }]
        });
        this.add(this.passwinpanel);
    },

    sendpasswinSave:function(){
       
        
        if(!this.passwinform.form.isValid())
        {
            return ;
        }else

        {
            var password =encodeURI(hex_sha1(this.newpassret.getValue()));
            var currpassword=encodeURI(hex_sha1(this.currentpass.getValue()));
//            var  param={
//                flag:46,
//                userid:this.profId,
//                pass:password
//            };

            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + 'hrms.jsp',
//                params:param
//            url: Wtf.req.base+'UserManager.jsp',
            url:"ProfileHandler/changePassword.do",
            params: {
                mode:23,
                changepassword:password,
                currentpassword:currpassword,
                userid:this.profId
            }
            },
            this,
            function(response){
                this.close();
                if("Error in changing Password."==response.msg || WtfGlobal.getLocaleText("hrms.common.ErrorinchangingPassword")==response.msg)
                	msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"),response.msg],2);
                else
                	msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),response.msg],1);
            },

            function(){
                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.ErrorPasswordChanging")],1,1);
                  

            }

            )
        //Wtf.getCmp('Report'+this.id).getSelectionModel().clearSelections();
        }
    }

});








