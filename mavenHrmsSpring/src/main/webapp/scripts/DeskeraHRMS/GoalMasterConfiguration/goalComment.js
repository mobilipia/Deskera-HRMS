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
Wtf.goalComment = function(config) {
    if(config.applybutton){
        Wtf.apply(this,{
            buttonAlign :'right',
            buttons: [
            {
                text: WtfGlobal.getLocaleText("hrms.performance.AddComment"),
                disabled:this.isDisable,
                handler: this.savecomment,
                scope:this
            },
            {
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                handler: function(){
                    this.close();
                },
                scope:this
            }
            ]
        }, config);
    }
    else{
        Wtf.apply(this,{
            buttonAlign :'right',
            buttons: [
            {
                text: WtfGlobal.getLocaleText("hrms.common.Ok"),
                handler: function(){
                    this.close();
                },
                scope:this
            }
            ]
        }, config);
    }
    Wtf.goalComment.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.goalComment, Wtf.Window, {
    initComponent: function(config) {
        Wtf.goalComment.superclass.initComponent.call(this,config);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.goalComment.superclass.onRender.call(this, config);
        this.comment = new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.comments")+' *',
            width : 200,
            height:60,
            allowBlank:this.blank,
            readOnly:this.read
        });
        
        this.commentForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            lableWidth :70,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.comment]
        });

        this.commentPanel= new Wtf.Panel({            
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
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.performance.comments"), this.note, "../../images/comment.gif")
                },{
                    border:false,
                    region:'center',
                    cls : 'panelstyleClass2',
                    layout:"fit",
                    items: [
                    this.commentForm
                    ]
                }]
            }]
        });
        this.add(this.commentPanel);
        if(this.viewflag)
            this.comment.setValue(this.comnt);
    },
    savecomment:function(){
        if(!this.viewflag){
            if(!this.comment.isValid()){
               return;
            }
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + 'hrms.jsp',
                url:"Performance/Goal/addCommentsfunction.pf",
                params:  {
                    flag:151,
                    goalid:this.goalarr,
                    comment:this.comment.getValue()
                }
            },
            this,
            function(){
                this.close();
                this.ds.load();
                this.cleargrid.getSelectionModel().clearSelections();
                calMsgBoxShow(130,0)
                getDocsAndCommentList(this.cleargrid,this.goalarr,1,this.idX);
            },
            function(){
                calMsgBoxShow(27,1);
            })
        }
        else{
            Wtf.Ajax.requestEx({
                url:Wtf.req.base + "hrms.jsp?flag=165",
                params: {
                    appraisalids:this.commentarr,
                    reviewercomment:this.comment.getValue(),
                    addComment:true
                }
            },
            this,
            function(){
                this.close();
                this.ds.load();
                this.cleargrid.getSelectionModel().clearSelections();
                calMsgBoxShow(130,0)
            },
            function(){
                calMsgBoxShow(27,1);
            })
        }
    }
});
