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
Wtf.agencyWindow = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        width:410,
        height:400,
        buttons: [
        {
            text:  WtfGlobal.getLocaleText("hrms.common.Save"),
            handler: this.sendjobSaveRequest,
            scope:this
        },
        {
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.agencyWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.agencyWindow, Wtf.Window, {
    initComponent: function() {
        Wtf.agencyWindow.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        this.managerCmb = new Wtf.form.ComboBox({
            store:Wtf.managerStore,
            mode:'local',
            width:200,
            name:'manager',
            valueField: 'userid',
            displayField:'username',
            triggerAction: 'all',
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.approving.manager")+'*',
            typeAhead:true,
            allowBlank:false
        });

        if(!Wtf.StoreMgr.containsKey("manager")){
            if(!this.isEdit){
                Wtf.managerStore.on("load",this.setManager,this);
            }else{
                Wtf.managerStore.on("load",this.setManagerid,this);
            }
            Wtf.managerStore.load();
            Wtf.StoreMgr.add("manager",Wtf.managerStore)
        }else{
            if(!this.isEdit){
                this.setManager();
            }else{
                this.setManagerid();
            }
        }

        this.agname = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.agency.name")+'*',
            width:200,
            name: 'agname',
            maxLength:255,
            validator:WtfGlobal.noBlankCheck,
            allowBlank:false
        });

        this.weburl = new Wtf.form.TextField({
            allowBlank:false,
            width:200,
            maxLength:255,
            name: 'url',
            vtype:'url',
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.AgencyWebsite")+'*'
        });

        this.agencyid = new Wtf.form.Hidden();

        this.rcost = new Wtf.form.NumberField({
            allowBlank:false,
            width:200,
            maxLength:10,
            name: 'cost',
            allowDecimals:true,
            allowNegative:false,
            lableWidth :100,
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.recruitment.cost")+'(<span align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.getCurrencySymbol()+'</span>)*'
        });

        this.conper = new Wtf.form.TextField({
            allowBlank:false,
            width:200,
            maxLength:100,
            validator:WtfGlobal.validateNameFields,
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.contact.person")+'*'
        });

        this.contact = new Wtf.form.TextField({
            allowBlank:false,
            width:200,
            validator:WtfGlobal.validatePhoneNum,
            maxLength:25,
            name:'phoneno',
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.AgencyContact")+'*'
        });
        this.address = new Wtf.form.TextArea({
            allowBlank:false,
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.AgencyAddress")+'*',
            width:200,
            name: 'address',
            validator:WtfGlobal.noBlankCheck,
            maxLength:255
        });

        this.addAgencyForm=new Wtf.form.FormPanel({
            frame:false,
            border:false,
            bodyStyle : 'font-size:10px;padding:15px 20px;margin-left:3%;',
            labelWidth :125,
            items:[this.agencyid,this.agname,this.weburl,this.rcost,this.conper,this.managerCmb,this.contact,this.address] 
        }); 
        this.addAgencyPanel= new Wtf.Panel({
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 75,
                    border : false,
                    cls : 'panelstyleClass1',
                    html: getTopHtml(this.wintitle,WtfGlobal.getLocaleText("hrms.common.Fillupthefollowingform"),"../../images/addagency.jpg")
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [this.addAgencyForm]
                }]
            }]
        });
        this.add(this.addAgencyPanel);
        Wtf.agencyWindow.superclass.onRender.call(this, config);
        if(this.record!=null){
            this.addAgencyForm.on('render',function(){
                this.agname.setValue(this.record.data.agname);
                this.agencyid.setValue(this.record.data.agid);
                this.weburl.setValue(this.record.data.url),
                this.rcost.setValue(this.record.data.cost),
                this.conper.setValue(this.record.data.contactperson),
                this.contact.setValue(this.record.data.phoneno),
                this.address.setValue(this.record.data.address)
            },this);
        }
    }, 
    setManager:function(){
        if(Wtf.managerStore.getCount()>0){
            this.managerCmb.setValue(Wtf.managerStore.getAt(0).get('userid'));
        }
    },
    setManagerid:function(){
        if(Wtf.managerStore.getCount()>0){
            this.managerCmb.setValue(this.record.data.managerid);
        }
    },
    sendjobSaveRequest:function(){
        if(this.addAgencyForm.getForm().isValid())
        {
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp?flag=121",
                url: "Rec/Agency/addAgency.rec",
                scope:this,
                params:{
                    agencyid:this.agencyid.getValue(),
                    agencyname:this.agname.getValue(),
                    agencyweb:this.weburl.getValue(),
                    reccost:this.rcost.getValue(),
                    conperson:this.conper.getValue(),
                    apprman:this.managerCmb.getValue(),
                    agencyno:this.contact.getValue(),
                    agencyadd:this.address.getValue()
                }
            },this,
            function(a,req){
                req=eval('('+a+')');
                if(req.message=='exist'){
                    calMsgBoxShow(222,2);
                } else {
                    this.ds.load();
                    if(this.isEdit)
                        calMsgBoxShow(151,0);
                    else
                        calMsgBoxShow(68,0);
                    this.close();
                }
            },
            function(){
                calMsgBoxShow(27,1);
            }) 
        }
        else {
            calMsgBoxShow(28,2);
        }
    }
});
