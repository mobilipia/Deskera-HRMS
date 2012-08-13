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
Wtf.newEmailTemplate = function (config){
    Wtf.apply(this, config);
    this.paramtypestore = new Wtf.data.SimpleStore({
        fields :['id', 'name', 'group'],
        data:[['mailsender',WtfGlobal.getLocaleText("hrms.common.SenderYou"), '1'],['mailrecipient',WtfGlobal.getLocaleText("hrms.common.Recipient"), '2'],['company',WtfGlobal.getLocaleText("hrms.common.Company"), '3'],['other',WtfGlobal.getLocaleText("hrms.common.Other"), '4']]
    });
    this.paramvaluestore = new Wtf.data.SimpleStore({
        fields :['id', 'name', 'group'],
        data:[['fname',WtfGlobal.getLocaleText("hrms.common.FirstName"), '1'],['lname',WtfGlobal.getLocaleText("hrms.common.LastName"), '1'],['phone',WtfGlobal.getLocaleText("hrms.common.PhoneNo"),'1'],['email',WtfGlobal.getLocaleText("hrms.common.Email"),'1'],
            ['currentyear',WtfGlobal.getLocaleText("hrms.common.Currentyear"), '4'],['cname',WtfGlobal.getLocaleText("hrms.common.SenderCompanyname"), '3'],['caddress',WtfGlobal.getLocaleText("hrms.common.SenderCompanyaddress"),'3'], ['cmail',WtfGlobal.getLocaleText("hrms.common.SenderCompanyemail"),'3'],['rname',WtfGlobal.getLocaleText("hrms.common.RecipientCompanyname"), '3']]
    });
  this.paramRecord = Wtf.data.Record.create([
    {
        name:'type'
    }]);

    this.paramStore = new Wtf.data.Store({
       url:"Common/Template/getParameterType.common" ,
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.paramRecord),
        baseParams:{
            type:'getTaxSlabs'
        }
    });
    this.paramStore.load();


    this.valueRecord = Wtf.data.Record.create([
    {
        name:'id'
    },
    {
        name:'ph'
    },
    {
        name:'type'
    },
    {
        name:'value'
    }]);

    this.valueStore = new Wtf.data.Store({
       url:"Common/Template/getParameterTypeValuePair.common" ,
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.valueRecord),
        baseParams:{
            type:'getTaxSlabs'
        }
    });
    this.valueStore.load();


    this.editorTiny = new Wtf.form.HtmlEditor({
        value: '<table class="headerTable" width="600" cellpadding="0" cellspacing="0"><tbody><tr><td style="border-top: 0px none rgb(0, 0, 0); border-bottom: 0px none rgb(255, 255, 255); padding: 0px; background-color: rgb(255, 255, 204); text-align: center;" class="headerTop" align="right"><div style="font-size: 10px; color: rgb(102, 51, 0); line-height: 200%; font-family: Verdana; text-decoration: none;" mc:edit="header" class="adminText"><span id="tpl-content-header" class="tpl-content">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Emailnotdisplayingcorrectly")+' <a class="adminText" href="*%7CARCHIVE%7C*">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Viewitinyourbrowser")+'</a></span></div></td></tr><tr><td style="border-top: 0px none rgb(51, 51, 51); border-bottom: 0px none rgb(255, 255, 255); padding: 0px; background-color: rgb(255, 255, 255);" class="headerBar" align="left" valign="middle"><div style="color: rgb(51, 51, 51); font-size: 15px; font-family: Verdana; font-weight: normal; text-align: center;" class="headerBarText"><div id="defaultimage-header_image" class="default-edit-image"><div style="width: 600px;" class="default-text"><span id="tpl-content-header-image" class="tpl-content">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Emailnotdisplayingcorrectly")+' <a class="adminText" href="*%7CARCHIVE%7C*">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Viewitinyourbrowser")+'</a></span></div></div></div></td></tr></tbody></table><table class="bodyTable" width="600" cellpadding="20" cellspacing="0"><tbody><tr><td style="padding: 20px; font-size: 12px; color: rgb(0, 0, 0); line-height: 150%; font-family: Verdana; width: 400px; background-color: rgb(255, 255, 255);" mc:edit="main" class="defaultText" align="left" valign="top"><span id="tpl-content-main" class="tpl-content"><span class="title">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.PrimaryHeading")+'</span><br><p>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Latin1111")+'</p><span class="subTitle">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Subheading")+'</span><br><p>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Clickheretoaddyoursidecolumncopyandimages")+'</p></span></td><td style="border-left: 1px dashed rgb(204, 204, 204); margin: 0px; padding: 20px; background-color: rgb(255, 255, 255); text-align: left; width: 200px;" class="sideColumn" align="left" valign="top"><div style="font-size: 11px; font-weight: normal; color: rgb(102, 102, 102); font-family: Arial; line-height: 150%;" mc:edit="sidecolumn" class="sideColumnText"><span id="tpl-content-sidecolumn" class="tpl-content"><span class="sideColumnTitle">'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Subheading")+'</span><br>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Clickheretoaddyoursidecolumncopyandimages")+'</span></div></td></tr><tr><td style="border-top: 0px none rgb(255, 255, 255); padding: 20px; background-color: rgb(204, 204, 204);" colspan="2" class="footerRow" align="left" valign="top"><div style="font-size: 10px; color: rgb(51, 51, 51); line-height: 100%; font-family: Verdana;" mc:edit="footer" class="footerText"><span id="tpl-content-footer" class="tpl-content"><br><br><br>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Ourmailingaddressis")+':<br>#company:cmail#<br>Copyright (C) #other:currentyear#  #company:cname# '+WtfGlobal.getLocaleText("hrms.CampaignDetail.Allrightsreserved")+'<br></span></div><div style="margin-top: 10px; text-align: right;"></div></td></tr></tbody></table>',
        plugins: [
            new Wtf.ux.form.HtmlEditor.insertImage({
                imageStoreURL: Wtf.req.base + "getFiles.jsp?action=1&type=img",
                imageUploadURL: Wtf.req.base + "getFiles.jsp?action=2&type=img"
            }),
            new Wtf.ux.form.HtmlEditor.HR({}),
            new Wtf.ux.form.HtmlEditor.SpecialCharacters({})
        ]
    });
    Wtf.newEmailTemplate.superclass.constructor.call(this,{
        id:this.addNewDashboardCall?"template_wiz_win_addnew_dash":(this.dashboardCall==true?"template_dash_win"+this.templateid:'template_wiz_win'+this.templateid),
        border:false,
        closable:true,
        layout : 'fit',
        iconCls: getTabIconCls(Wtf.etype.acc),
        bbar:['->',new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.SaveTemplate"),
            tooltip:{
                text:WtfGlobal.getLocaleText("hrms.CampaignDetail.SaveTemplate.tooltip")
            },
            scope:this,
            iconCls:"pwndCommon submitbuttonIcon",
            handler:this.saveTemplate
        })],
        items:  {
            border:false,
            autoWidth:true,
            autoScroll:true,
            layout:'border',
            bodyStyle:"background-color:#FFFFFF;margin:5px 50px 5px 50px;",
            items:[{
                region:'north',
                height:130,
                layout:'Column',
                border:false,
                items:[{
                    columnWidth:.5,
                    border:false,
                    items:{
                        xtype:"fieldset",
                        title:WtfGlobal.getLocaleText("hrms.CampaignDetail.MetaData"),
                        height:120,
                        defaults :{
                            xtype:"textfield",
                            width:280
                        },
                        items:[this.tempname= new Wtf.form.TextField({
                            fieldLabel:WtfGlobal.getLocaleText("hrms.CampaignDetail.TemplateName")+"*",
                            id:this.addNewDashboardCall?"template_name_txt_dash_addnew":(this.dashboardCall==true?'template_name_txt_dash'+this.templateid:'template_name_txt'+this.templateid),
                            maxLength:255,
                            allowBlank:false,
                            validator:WtfGlobal.noBlankCheck,
                            name:"template_name"
                        }),{
                            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.description"),
                            maxLength:1024,
                            id:this.addNewDashboardCall?"template_desc_dash_addnew":(this.dashboardCall==true?'template_desc_dash'+this.templateid:'template_desc'+this.templateid),
                            name:"template_desc"
                        },{
                            fieldLabel:WtfGlobal.getLocaleText("hrms.CampaignDetail.Subject")+"*",
                            id:this.addNewDashboardCall?"template_subject_dash_addnew":(this.dashboardCall==true?'template_subject_dash'+this.templateid:'template_subject'+this.templateid),
                            allowBlank:false,
                            validator:WtfGlobal.noBlankCheck,
                            maxLength:255,
                            name:"template_subject"
                        }]
                    }
                },{
                    columnWidth:.5,
                    border:false,
                    items:{
                        xtype:"fieldset",
                        title:WtfGlobal.getLocaleText("hrms.CampaignDetail.ParameterConfiguration"),
                        height:120,
                        items:[this.param1Combo = new Wtf.form.ComboBox({
                            fieldLabel:WtfGlobal.getLocaleText("hrms.CampaignDetail.ParameterType"),
                            store:this.paramStore,//this.paramtypestore,
                            name:"combovalue",
                            displayField:'type',//'name',
                            //valueField:'id',
                            hiddenName:"param_type",
                            mode:'local',
                            triggerAction:'all',
                            id:this.addNewDashboardCall?"param_type_combo_dash_addnew":(this.dashboardCall==true?'param_type_combo_dash'+this.templateid:'param_type_combo'+this.templateid)
                        }),this.param2Combo = new Wtf.form.ComboBox({
                            xtype:"combo",
                            fieldLabel:WtfGlobal.getLocaleText("hrms.CampaignDetail.ParameterValue"),
                            name:"param_value",
                            store:this.valueStore,//this.paramvaluestore,
                            mode:'local',
                            displayField:'value',//'name',
                            valueField:'ph',//'id',
                            id:this.addNewDashboardCall?"param_value_combo_dash_addnew":(this.dashboardCall==true?'param_value_combo_dash'+this.templateid:'param_value_combo'+this.templateid),
                            triggerAction:'all',
                            hiddenName:"param_value"
                        }),{
                            xtype:"button",
                            border:false,
                            minWidth:80,
                            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.Insert"),
                            scope:this,
                            handler:function(){
                                var paramtypeval="";
                                var paramvalueval="";
                                if(this.addNewDashboardCall){
                                    paramtypeval = Wtf.getCmp("param_type_combo_dash_addnew").getValue();
                                    paramvalueval = Wtf.getCmp("param_value_combo_dash_addnew").getValue();
                                } else {
                                    if(this.dashboardCall){
                                        paramtypeval = Wtf.getCmp("param_type_combo_dash"+this.templateid).getValue();
                                        paramvalueval = Wtf.getCmp("param_value_combo_dash"+this.templateid).getValue();
                                    } else{
                                        paramtypeval = Wtf.getCmp("param_type_combo"+this.templateid).getValue();
                                        paramvalueval = Wtf.getCmp("param_value_combo"+this.templateid).getValue();
                                    }
                                }

                                var strdata="";
                                this.isClosable=false;
                                if(paramtypeval.trim() == "" || paramvalueval.trim() == ""){
                                    this.isClosable=true;
                                    WtfComMsgBox(950,0);
                                    return true;
                                }
                                strdata = " #"+paramtypeval+":"+paramvalueval+"# ";
                                this.editorTiny.insertAtCursor(strdata);
                            }
                        }]
                    }
                }]
            },{
                region:'center',
                layout:'fit',
//                layout:'border',
                xtype:"fieldset",
                title:WtfGlobal.getLocaleText("hrms.common.Template"),
                bodyStyle:"background-color:#FFFFFF;margin-bottom:5px;",
                border:false,
                items:[this.editorTiny]
            }]
        }
    });
    this.param1Combo.on("select",function(){this.param2Combo.clearValue();},this)
    this.param2Combo.on("expand", function(obj, rec){
        var val = this.param1Combo.getValue();
        this.valueStore.filter("type",val);
  /*      if(val == "company") {
            this.paramvaluestore.filter("group", 3);
        } else if(val == "other") {
            this.paramvaluestore.filter("group", 4);
        }else if(val == "mailrecipient" || val == "mailsender") {
            this.paramvaluestore.filter("group", 1);
        } else
            this.paramvaluestore.filter("group", 0);
*/
    }, this);
     }

Wtf.extend(Wtf.newEmailTemplate,Wtf.ux.ClosableTabPanel,{
    isClosable:true,
    closeWindow:false,
    initComponent: function(config) {
        this.addEvents({
            'onsuccess': true
        });
        Wtf.newEmailTemplate.superclass.initComponent.call(this,config);
    },
    onRender: function(config){
        Wtf.newEmailTemplate.superclass.onRender.call(this,config);
        if(this.templateid) {
            if(this.dashboardCall){
                Wtf.getCmp("template_name_txt_dash"+this.templateid).setValue(this.tname);
                Wtf.getCmp("template_desc_dash"+this.templateid).setValue(this.tdesc);
                Wtf.getCmp("template_subject_dash"+this.templateid).setValue(this.tsubject);
            }else{
                Wtf.getCmp("template_name_txt"+this.templateid).setValue(this.tname);
                Wtf.getCmp("template_desc"+this.templateid).setValue(this.tdesc);
                Wtf.getCmp("template_subject"+this.templateid).setValue(this.tsubject);
            }
                this.getTemplateBody();
//            this.editorTiny.setValue(unescape(this.tbody));
        }
    },
    getTemplateBody: function(){
        Wtf.Ajax.requestEx({
            url: "Common/Template/getTemplateContent.common" ,//Wtf.req.base + "campaign.jsp",
            params: {
                flag: 27,
                templateid: this.templateid
            }
        }, this,
        function(action, response){
            if(action.success){
                this.editorTiny.setValue(unescape(action.data.html));
            }
        },
        function(action, response){

        });
    },
    saveTemplate:function(){
        this.isClosable=false;
        var tname="";
        var tsub="";
        var tdesc="";
        if(this.addNewDashboardCall){
           tname=Wtf.getCmp("template_name_txt_dash_addnew").getValue();
           tsub=Wtf.getCmp("template_subject_dash_addnew").getValue();
           tdesc=Wtf.getCmp("template_desc_dash_addnew").getValue();

        } else {
            if(this.dashboardCall){
                tname=Wtf.getCmp("template_name_txt_dash"+this.templateid).getValue();
                tsub=Wtf.getCmp("template_subject_dash"+this.templateid).getValue();
                tdesc=Wtf.getCmp("template_desc_dash"+this.templateid).getValue();
            } else {
                tname=Wtf.getCmp("template_name_txt"+this.templateid).getValue();
                tsub=Wtf.getCmp("template_subject"+this.templateid).getValue();
                tdesc=Wtf.getCmp("template_desc"+this.templateid).getValue();
            }
        }
        if(tname.trim() == "" || tsub.trim()=="" ){
        	if(this.dashboardCall){
        		if(tname.trim()==""){
        			Wtf.getCmp("template_name_txt_dash"+this.templateid).markInvalid(WtfGlobal.getLocaleText("hrms.common.Thisfieldismandatory"));
        		}
        		if(tsub.trim()==""){
        			Wtf.getCmp("template_subject_dash"+this.templateid).markInvalid(WtfGlobal.getLocaleText("hrms.common.Thisfieldismandatory"));
        		}
             }else{
            	 if(tname.trim()==""){
         			Wtf.getCmp("template_name_txt_dash_addnew").markInvalid(WtfGlobal.getLocaleText("hrms.common.Thisfieldismandatory"));
         		}
         		if(tsub.trim()==""){
         			Wtf.getCmp("template_subject_dash_addnew").markInvalid(WtfGlobal.getLocaleText("hrms.common.Thisfieldismandatory"));
         		} 
             }
        	return false;
        } else {
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.CampaignDetail.SavingTemplate"));
            Wtf.Ajax.requestEx({
                url: "Common/Template/saveTemplate.common" ,//Wtf.req.base + "campaign.jsp",
                params: {
                    tname:tname,
                    tsub:tsub,
                    tdesc:tdesc,
                    tbody:this.editorTiny.getValue(),
                    tplaintext:this.editorTiny.getValue(),//getPlainText(),
                    tid:this.templateid,
                    flag : 2,
                    mode : this.templateid ? 1 : 0
                }
            },
            this,
            function(res){
                if(res.value=='success') {
                //if(res.success) {
                Wtf.updateProgress();
                    if(this.templateid==undefined)
                        calMsgBoxShow(214,0,false,350);
                    else
                        calMsgBoxShow(215,0,false,350);
                    if(this.store)this.store.load();
                    this.isClosable=true;
                    this.closeWindow=true;
                    this.fireEvent("closeTemplate");
                    if(this.mailTemplate!=undefined){
                        this.mailTemplate.load();
                    }
                    
                }
            },
            function(){
                Wtf.updateProgress();
                if(this.templateid==undefined)
                    calMsgBoxShow(216,0,false,350);
                else
                    calMsgBoxShow(217,0,false,350);
                
            });
            return true;
        }
    }
});
