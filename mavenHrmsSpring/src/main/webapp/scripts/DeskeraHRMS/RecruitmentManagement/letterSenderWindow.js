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
Wtf.letterSenderWindow = function (config){
    Wtf.apply(this,config);
    config.resizable=false;

    Wtf.letterSenderWindow.superclass.constructor.call(this,{
        buttons:[
        {
            text: WtfGlobal.getLocaleText("hrms.common.Send"),
            scope:this,
            handler:function (){
                if(!this.AddEditForm.form.isValid()) {
                    return ;
                } else
                {
                    Wtf.MessageBox.confirm( WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.common.Doyouwanttosendthisletter"),function(btn){
                        if(btn!="yes") {
                            this.close();
                        }
                        else{
                            if(this.AddEdit=="Edit") {
                                var sm=this.generaltaxgrid. getSelectionModel();
                              //  var rec=sm.getSelected();
                             //   var row=this.userdsT.indexOf(rec);
                                sm.clearSelections();
                              //  WtfGlobal.highLightRow(this.generaltaxgrid ,"33CC33",5,row);
                            }
                            // var rec1=this.AddEditForm.getForm().getValues();
                           // calMsgBoxShow(200,4,true);
                            Wtf.Ajax.requestEx({
                                url: "Rec/Job/sendLetters.rec",//"Common/Template/sendLetters.common" ,
                                params: {
                                    save:true,
                                    templateid:this.letter.getValue(),
                                    userlist:this.userlist
                                }
                            },
                            this,
                            function(response){
                                if(response.msg == "null")
                                    Wtf.Msg.alert( WtfGlobal.getLocaleText("hrms.common.Message"), WtfGlobal.getLocaleText("hrms.common.Erroroccouredatserverside"));
                                else
                                    if(response.value == "failed")
                                        calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),""+response.msg.substr(response.msg.indexOf(":")+1,response.msg.length)+""],2);
                                    else
                                        calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.success"),""+response.msg+""],0);
                            //-    this.fireEvent('updategrid',this);
                                Wtf.getCmp('letterSenderListWindow_id').close();
                            },
                            function(){
                                Wtf.Msg.alert( WtfGlobal.getLocaleText("hrms.common.Message"),WtfGlobal.getLocaleText("hrms.common.Someerroroccoured"));
                            }
                            );
                        }
                    },this);
                }
            }
        },
        {
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler:function (){
                Wtf.getCmp('letterSenderListWindow_id').close();
            },
            scope:this
        }
        ]
    });
}
Wtf.extend(Wtf.letterSenderWindow,Wtf.Window,{
    initComponent:function (){
        Wtf.letterSenderWindow.superclass.initComponent.call(this);

        this.GetNorthPanel();
        this.GetAddEditForm();
        this.mainPanel = new Wtf.Panel({
            layout:"border",
            border:false,
            items:[
            this.northPanel,
            this.AddEditForm
            ]
        });
        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){
        var wintitle=this.AddEdit=="Edit"?WtfGlobal.getLocaleText("hrms.recruitment.EditLetterList"):WtfGlobal.getLocaleText("hrms.recruitment.SendLetter");
        var windetail=this.AddEdit=="Edit"?WtfGlobal.getLocaleText("hrms.recruitment.Fillupinformationeditletterlist"):WtfGlobal.getLocaleText("hrms.recruitment.Selectthelettertosend");
        var image=this.typeimage;
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:90,
            border:false,
            bodyStyle:"backgroubodyStylend-color:white;padding:8px;border-bottom:1px solid #bfbfbf;background-color: white",
            //html:getHeader(image,wintitle,windetail)
            html: getTopHtml(wintitle,windetail,image)
        });
    },
    GetAddEditForm:function (){
      this.templateListRecord = Wtf.data.Record.create([
        {
            name:'id'
        },       
        {
            name:'name'
        }
        ]);

        this.templateListStore = new Wtf.data.Store({
            url:"Common/Template/getBriefTemplateList.common" ,
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.templateListRecord),
            baseParams:{
                type:'gettemplateList'
            }
        });
        this.templateListStore.load();


        this.letter=new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.LetterType")+"*",
            editable:false,
            name:'selectletter',
            width:200,
            labelWidth:100,
            emptyText:WtfGlobal.getLocaleText("hrms.recruitment.SelectLetter"),
            allowBlank:false,
            store:this.templateListStore,
            displayField:'name',
            valueField:'id',
            typeAhead:true,
            mode: 'local',
            triggerAction: 'all'
        });

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            scope:this,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
            items:[
                this.letter//,
               // this.listdescr,
               // this.listtid
            ]
        });
/*
        if(this.AddEdit=="Edit") {
            var recData = this.generaltaxgrid.getSelectionModel().getSelected().data;
            this.listname.setValue(recData.listname);
            this.listdescr.setValue(recData.descr);
            this.listtid.setValue(recData.listid);
        }*/
    }
});
