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
Wtf.managecompetencyWindow = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        buttons: [
        {
            text:  WtfGlobal.getLocaleText("hrms.common.Save"),
            handler: this.savecompetency,
            scope:this
        },
        {
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }
        ]
    }, config);
    Wtf.managecompetencyWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.managecompetencyWindow, Wtf.Window, {
    initComponent: function(config) {
        Wtf.managecompetencyWindow.superclass.initComponent.call(this,config);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.managecompetencyWindow.superclass.onRender.call(this, config);
        
        this.competency = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.competency")+'*',
            width : 200,
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            maxLength:50
        });
        this.description = new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.description"),
            width : 200,
            height:50
        });
        this.weightage = new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.weightage")+'*',
            width : 200,
            allowBlank:false,
            maxLength:3
        });

        this.managecompetencyForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            lableWidth :70,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.competency,this.description]
        })

        this.managecompetencyPanel= new Wtf.Panel({
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
                    html: getTopHtml(this.wintitle,  WtfGlobal.getLocaleText("hrms.common.Fillupthefollowingform"),"../../images/compentencyadd.jpg")
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [
                    this.managecompetencyForm
                    ]
                }]
            }]
        });

        this.add(this.managecompetencyPanel);
        if(this.editflag){
            this.competency.setValue(this.compname);
            this.description.setValue(this.compdesc);
            this.weightage.setValue(this.compwt);
        }
        else{
            this.compid="null";
        }
    },
    savecompetency:function(){
        if(this.managecompetencyForm.getForm().isValid())
        {
            if(this.action=="edit"){
                var rec=this.cleargrid.getSelectionModel().getSelected();
                var row=this.datastore.indexOf(rec);
                this.cleargrid.getSelectionModel().clearSelections();
                WtfGlobal.highLightRow(this.cleargrid ,"33CC33",5,row);
            }
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp?flag=102",
                url:"Performance/Competency/addCompetency.pf",
                scope:this,
                params:{
                    cmptid:this.compid,
                    cmptname:this.competency.getValue(),
                    cmptdesc:this.description.getValue()                    
                }
            },
            this,
            function(response){
                var params="";
                var res=eval('('+response+')');
                if(this.action=="edit"){
                    params={
                        start:0,
                        limit:this.cleargrid.pag.pageSize,
                        ss:Wtf.getCmp("Quick"+this.cleargrid.id).getValue()
                    }
                    WtfGlobal.delaytasks(this.datastore,params);
                }else{
                    params={
                        start:0,
                        limit:this.cleargrid.pag.pageSize,
                        ss:Wtf.getCmp("Quick"+this.cleargrid.id).getValue()
                    }
                    this.datastore.load({
                        params:params
                    });
                }
                this.cleargrid.getSelectionModel().clearSelections();
                if(this.desig){
                    this.cleargrid.getStore().load({
                        params:{
                            flag:115,
                            desig:this.desig
                        }
                    });
                }
                this.close();
                if(this.action=="add"){
                    if(res.message){
                        calMsgBoxShow(47,0);
                    }else{
                        calMsgBoxShow(160,2);
                    }
                }
                else{
                    if(res.message){
                        calMsgBoxShow(48,0);
                    }else{
                        calMsgBoxShow(160,2);
                    }
                }
            },
            function()
            {
                calMsgBoxShow(27,1);
            });
        }
        else{
            calMsgBoxShow(28,0);
        }
    }
   
});








