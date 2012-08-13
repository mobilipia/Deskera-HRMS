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
Wtf.DetailPanel=function(config)
    {        
        config.id=config.id2+'HRMSupdownCompo';
        config.panCommTitle = "<br><div><span id='gotoComments"+config.modulename+"' class='dpTitleHead'> <img src='../../images/comment12.gif' class='imgMidVA'/>  "+WtfGlobal.getLocaleText("hrms.performance.comments")+" :  </span><br></div>";
               
        config.newCommContentWithPerm = WtfGlobal.getLocaleText({key:"hrms.performance.commentedOn",params:["<span style=\"color:#15428B;  !important;\"> &nbsp; &nbsp; &nbsp; {addedby}</span> <span style=\"color:gray !important;\">","<i>{postedon}</i> </span>"]})+": {comment}   <br><br>";
        config.noPerm = "<div style='margin:3px;color:#15428B;'> <div id='{msgDiv}' style='height:auto;display:block;overflow:auto; margin-left:10px;'>"+WtfGlobal.getLocaleText("hrms.performance.Insufficientpermissionstoview")+"</div></div><br><br>";
        config.selectValid = "<div style='margin:3px;height:90%;width:90%;'> <div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>"+WtfGlobal.getLocaleText("hrms.common.Pleaseselectarecordtoseedetails")+"</div></div>";
        config.Failed = "<div style='margin:3px;color:red;'> <div id='{msgDiv}' style='height:auto;display:block;overflow:auto; margin-left:10px;'>"+WtfGlobal.getLocaleText("hrms.performance.FailedtoloadComments")+"</div></div>";

 Wtf.DetailPanel.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.DetailPanel,Wtf.Panel,{
    layout: "fit",
    commentFailed : "<div style='margin:3px;height:20%;width:90%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>"+WtfGlobal.getLocaleText("hrms.performance.FailedtoloadComments")+"</div></div>",
    initialMsg : "<div style='margin:3px;height:90%;width:90%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;margin-top:20px'>"+WtfGlobal.getLocaleText("hrms.common.Pleaseselectarecordtoseedetails")+"</div></div>",
    noPerm_com: "<span style=\"color:#15428B; font-weight:bold; !important;\"> "+WtfGlobal.getLocaleText("hrms.performance.comments")+" :  </span><br><br><div style='margin:3px;height:20%;width:20%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>"+WtfGlobal.getLocaleText("hrms.performance.Insufficientpermissionstoview")+"</div></div>",
    onRender: function(config){
        Wtf.DetailPanel.superclass.onRender.call(this,config);
        this.toolItems = new Array();
        this.messagePanelContentTemplate = new Wtf.Template(this.initialMsg);

          this.toolItems.push(this.comment=new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.performance.AddComment"),
            id:"Comment"+this.id,
            pressed: false,
            disabled:true,
            scope : this,
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.performance.AddComments.tooltip")
            },
            iconCls:'pwndExport comment',
            handler : function() {
                this.addComment();
            }
          })
          );
        
        this.detailtoolbar=new Wtf.Toolbar({
            items:this.toolItems
        });

        this.north = {
            region:'north',
            border: false,
            height:35,
           // margins:'0 5 2 0',
            layout:'fit',
            html:this.panTitle
        };
        this.center = {
            region:'center',
            autoScroll:true,
            layout:'fit',
            border: false,
            id:this.id2+"dloadpanelcenter",
            margins:'0 5 0 15',
            html: this.messagePanelContentTemplate.applyTemplate({
                msgDiv: "msgDiv_"
            })
        };

        this.dloadpanel= new Wtf.Panel({
            id:this.id+"downloadpanel",
            closable: true,
            split: true,
            border: false,
            bbar:this.detailtoolbar,
            bodyStyle: "background:#FFFFFF;border: solid 4px #5b84ba;",
            layout: "border",
            items: [this.center]
        });

        this.add(this.dloadpanel);
        var sm=this.grid.getSelectionModel();
        sm.on("selectionchange",function(){
            var arr=this.grid.getSelectionModel().getSelections();
            if(arr.length==0){
                this.comment.disable();
            } else {
                for(var i=0;i<arr.length;i++){
                    if(arr[i].get('gid')==undefined){
                        this.comment.disable();
                    }else{
                        this.comment.enable();
                    }
                }
            }
    },this);
    },       
   addComment:function(){

          var goalrec=this.grid.getSelectionModel().getSelections();
          var goalarr=[];
            for(var i=0;i<goalrec.length;i++){
                goalarr.push(goalrec[i].get('gid'));
            }
            this.addcom=new Wtf.goalComment({
                width:390,
                modal:true,
                height:250,
                title:WtfGlobal.getLocaleText("hrms.performance.goal.comments"),
                resizable:false,
                layout:'fit',
                note:WtfGlobal.getLocaleText("hrms.common.FillupthefollowingDetails"),
                read:false,
                blank:false,
                idX:this.id2,
                viewflag:false,
                applybutton:true,
                goalarr:goalarr,
                ds:this.Store,
                cleargrid:this.grid
            });
            this.addcom.show();
        
    }
});
