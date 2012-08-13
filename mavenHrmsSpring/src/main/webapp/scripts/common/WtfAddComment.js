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
Wtf.AddComment = function(config) {
    Wtf.apply(this, config);

     Wtf.AddComment.superclass.constructor.call(this,{
            title : WtfGlobal.getLocaleText("hrms.performance.AddComment"),
            closable : true,
            modal : true,
            iconCls : 'pwnd favwinIcon',
            width : 500,
            height: 300,
            resizable :false,
            buttonAlign : 'right',
            buttons :[{
                text : WtfGlobal.getLocaleText("hrms.performance.AddComment"),
                scope : this,
                handler:function(){
                var jsondata = "";
                var str = encodeURIComponent(WtfGlobal.HTMLStripper(this.Comment.getValue()));
                  
                jsondata+="{'leadid':'" + this.recid + "',";
                jsondata+="'mapid':'" + this.mapid + "',";
                jsondata+="'comment':'" +str + "'}";

                Wtf.Ajax.requestEx({
                    url: Wtf.req.base + 'crm.jsp',
                    params:{
                        jsondata:jsondata,
                        flag:255        /////////////flag for case
                    }
                },this,
                function(res) {
                    this.close();
                    WtfComMsgBox(600,0);
                    this.store.reload();
                    getDocsAndCommentList(this.recid,"",1,this.idX);
                },
                function(res) {
                    WtfComMsgBox(601,1);
                });
            }
               
            },{
                text :  WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope : this,
                handler : function() {
                    this.close();
                }
            }],
            layout : 'border',
                        items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html : "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
                    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
                    +"<img src = images/createuser.png style = 'width:40px;height:52px;margin:5px 5px 5px 5px;'></img>"
                    +"</div>"
                    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
                    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+WtfGlobal.getLocaleText("hrms.common.Comment")+"</b></div>"
                    +"<div style='font-size:10px;float:left;margin:15px 0px 10px 10px;width:100%;position:relative;'>"+WtfGlobal.getLocaleText("hrms.performance.AddComment")+"</div>"
                    +"</div>"
                    +"</div>"
            },{
                region : 'center',
                border : false,
                bodyStyle : 'background:#f1f1f1;font-size : 10px;padding:20px 20px 20px 20px;',
                layout : 'fit',
                items : [{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : "fit",
                    items : [this.createCourseForm = new Wtf.form.FormPanel({
                        baseCls: 'x-plain',
                        border : false,
                        bodyStyle : 'font-size:10px;',
                        //defaults : {width: 400},
                        lableWidth : 150,
                        defaultType: 'textfield',
                        items : [
                                this.Comment = new Wtf.form.TextArea({
                                    fieldLabel: WtfGlobal.getLocaleText("hrms.common.Comment"),
                                    name : 'Comment',
                                    maxlength :1024,
                                    invalidText : WtfGlobal.getLocaleText("hrms.common.invalid.comment"),
                                    allowBlank:false,
                                    validator:WtfGlobal.noBlankCheck,
                                    width : 300,
                                    height : 100
                                })]
                       })/*,this.coursebar = new Wtf.ProgressBar({
                            text:'Adding Comment...',
                            hidden : true
                   })*/]
                }]
             }]
       });

}

Wtf.extend(Wtf.AddComment, Wtf.Window, {

});


