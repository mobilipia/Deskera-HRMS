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
Wtf.document_panel=function(config){

    Wtf.document_panel.superclass.constructor.call(this,config);
},

Wtf.extend(Wtf.document_panel,Wtf.Panel,
{

    initComponent:function(config)
    {
      
        Wtf.document_panel.superclass.initComponent.call(this,config);
    },
 
    onRender:function(config)
    {
        Wtf.document_panel.superclass.onRender.call(this,config);

        this.headingType=WtfGlobal.getLocaleText("hrms.common.UploadFiles");
     
        this.add_files=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.UploadDocuments"),
            tooltip:WtfGlobal.getLocaleText("hrms.common.UploadDocuments.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.documentbutton),
            scope:this,
            handler:this.show_upload_window
        });

        this.deletedoc=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.DeleteDocuments"),
            tooltip:WtfGlobal.getLocaleText("hrms.common.DeleteDocuments.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            scope:this,
            disabled:true,
            handler:this.deletedocumnets
        });

        this.documentRecord=Wtf.data.Record.create([
        {
            name:'docid'
        },
        {
            name:'docname'
        },
        {
            name:'docdesc'
        },
        {
            name:'uploadedby'
        },
        {
            name:'docsize',
            type:'float'
        },
        {
            name:'uploaddate',
            type:'date'
        }
        ]);

        this.docReader = new Wtf.data.KwlJsonReader1({
            root: 'data',
            totalProperty:'count'

        },this.documentRecord);

        this.profile_store=new Wtf.data.Store({
//            url:Wtf.req.base +"hrms.jsp",
            url: "Common/Document/getDocs.common",
            baseParams:{
                flag:54,
                userid:this.lid,
                manager:this.manager,
                applicant:this.app
            },
            reader:this.docReader
        });
        calMsgBoxShow(202,4,true);
        this.profile_store.load();
        this.profile_store.on("load",function(){
            if(msgFlag==1){
                WtfGlobal.closeProgressbar();
            }
        },this);

        
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        var cm1="";
        if(this.manager){
            cm1=new Wtf.grid.ColumnModel([
                this.sm2,
                new Wtf.grid.RowNumberer(),
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentName"),
                    dataIndex:"docname"
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentDescription"),
                    dataIndex:"docdesc"
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentUploadedBy"),
                    dataIndex:"uploadedby"
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentDate"),
                    dataIndex:"uploaddate",
                    renderer:WtfGlobal.onlyDateRenderer
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentSize"),
                    dataIndex:"docsize",
                    renderer:this.filesize
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.Download"),
                    dataIndex:"id",
                    renderer:function(a,b,c,d,e,f){
//                        return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"Common/Document/downloadDocuments.common?url=" + c.data.docid + "&mailattch=true&dtype=attachment\")'><div class='pwndCommon downloaddocs' style='cursor:pointer' title='Click to download document' ></div></a></div>";
                        return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"Common/Document/downloadDocuments.common?url=" + c.data.docid + "&mailattch=true&dtype=attachment&applicant=" +f.baseParams.applicant +"\")'><div class='pwndCommon downloaddocs' style='cursor:pointer' title="+WtfGlobal.getLocaleText("hrms.common.Clicktodownloaddocument")+" ></div></a></div>";
                    }
                }
                ]);
        }else{
            cm1=new Wtf.grid.ColumnModel([
                this.sm2,
                new Wtf.grid.RowNumberer(),
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentName"),
                    dataIndex:"docname"
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentDescription"),
                    dataIndex:"docdesc"
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentDate"),
                    dataIndex:"uploaddate",
                    renderer:WtfGlobal.onlyDateRenderer
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.DocumentSize"),
                    dataIndex:"docsize",
                    renderer:this.filesize
                },
                {
                    header:WtfGlobal.getLocaleText("hrms.common.Download"),
                    dataIndex:"id",
                    renderer:function(a,b,c,d,e,f){
//                        return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"../../fdownload.jsp?url=" + c.data.docid + "&mailattch=true&dtype=attachment\")'><div class='pwndCommon downloaddocs' style='cursor:pointer' title='Click to download document' ></div></a></div>";
                        return "<div><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"../../Common/Document/downloadDocuments.common?url=" + c.data.docid + "&mailattch=true&dtype=attachment&applicant=" + f.baseParams.applicant +"\")'><div class='pwndCommon downloaddocs' style='cursor:pointer' title="+WtfGlobal.getLocaleText("hrms.common.Clicktodownloaddocument")+" ></div></a></div>";
                    }
                }
                ]);
        }

        var btnarr=[];
        btnarr.push('-',this.add_files,'-',this.deletedoc);
        this.grid=new Wtf.KwlGridPanel({
            border:false,
            store:this.profile_store,
            cm:cm1,
            sm:this.sm2,
            StripeRows:true,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.SearchbyDocumentNameDescription"),
            searchField:"docname",
            serverSideSearch:true,
            viewConfig:{
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:docUpload(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.common.Getstartedbyuploadingdocuments")+"</a>")
            },
            displayInfo:true,
            tbar:btnarr
        });
        this.add(this.grid);
//        this.add(this.upload_window);
        this.sm2.on("selectionchange",function(){            
            WtfGlobal.enableDisableBtnArr(btnarr, this.grid, [], [3]);
        },this);
    },
 
    show_upload_window:function()
    {
        this.createuploadwindow();
        this.upload_window.show();
    },
    filesize:function(a,b,c){
        var size=parseFloat(c.data['docsize']);
        var quo;
        if(size>=1024){
            quo=(size/1024);
            return (quo.toFixed(2)+" MB");
        }else{
            return (parseFloat(size).toFixed(2)+" KB");
        }
    },
    createuploadwindow : function(){
      this.docdesc=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.description"),
            height:'50%',
            name:'docdesc',
            width:200,
            maxLength:255
        });
      this.UploadForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            fileUpload:true,
            labelWidth:110,
//            url: "../../file.jsp?fileAdd=true",
            url: "Common/Document/addDocuments.common?fileAdd=true&IsIE="+Wtf.isIE,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            autoHeight:true,
            defaultType:'textfield',
            layoutConfig: {
                deferredRender: false
            },
            items:[
            {
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.File")+"*",
                inputType:'file',
                width:235,
                id:'fileupload'+this.id
            },
//            this.docname,
            this.docdesc,
            {
                xtype: "hidden",
                name: "refid",
                value: this.lid
            },{
                xtype: "hidden",
                name: "applicantid",
                value: this.app
            }
            ]
        });
        var assmngrbtn=new Array();
        assmngrbtn.push({
            text:WtfGlobal.getLocaleText("hrms.common.Upload"),
            handler:this.uploadDoc ,
            scope:this
        });

        assmngrbtn.push({
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope:this,
            handler: function(){
                this.upload_window.close();
//                this.UploadForm.getForm().reset();
            }
        });
        this.upload_window=new Wtf.Window({
            title:WtfGlobal.getLocaleText("hrms.common.UploadWindow"),
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            autoHeight:true,
            width:450,
            modal:true,
            items:[{
                    border : false,
                    cls : 'panelstyleClass1',
                    html:this.isview?getTopHtml(this.headingType,"","../../images/upload-file.gif"):getTopHtml(this.headingType,WtfGlobal.getLocaleText("hrms.common.Browseyourcomputerselectfiletoupload"),"../../images/upload-file.gif")
                },{
                    border:false,
                    cls : 'panelstyleClass2',
                    items:[this.UploadForm]
            }],
            buttonAlign :'right',
            buttons: assmngrbtn
        });
    },
    uploadDoc:function(){
        var filename=Wtf.getCmp('fileupload'+this.id).getValue();
        if(filename==""){
            calMsgBoxShow(140,0);
        }else{
            if(this.UploadForm.form.isValid())
            {
                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.common.youtouploadthedocument"),function(btn){
                    if(btn!="yes") {
                        this.upload_window.close();
//                        this.UploadForm.getForm().reset();
                    }else{
                        calMsgBoxShow(202,4,true);
                        this.UploadForm.getForm().submit({
                            scope:this,
                            success:function(){
                                this.profile_store.load({
                                    params:{
                                        start:0,
                                        limit:this.grid.pag.pageSize,
                                        ss:this.grid.quickSearchTF.getValue()
                                    }
                                });
                                calMsgBoxShow(141,0);
                                this.upload_window.close();
                            },
                            failure:function(){
                                calMsgBoxShow(27,1);
                                this.upload_window.close();
                            }
                        });
                        
                    }
                },this);
            }
        }
    },
    deletedocumnets:function(){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),deleteMsgBox('document'),function(btn){
            if(btn=="yes") {
                var delrec=this.sm2.getSelections();
                var delarr=[];
                this.sm2.clearSelections();
                for(var i=0;i<delrec.length;i++){
                    delarr.push(delrec[i].get('docid'));
                    var rec=this.profile_store.indexOf(delrec[i]);
                    WtfGlobal.highLightRow(this.grid,"FF0000",5, rec)
                }
                calMsgBoxShow(201,4,true);
                Wtf.Ajax.requestEx({
//                    url: Wtf.req.base + 'hrms.jsp',
                    url:"Common/Document/deleteDocuments.common",
                    params:  {
                        flag:60,
                        ids:delarr,
                        applicant:this.app
                    }
                },
                this,
                function(){                    
                    var params={
                        start:0,
                        limit:this.grid.pag.pageSize,
                        ss:this.grid.quickSearchTF.getValue()
                    }
                    WtfGlobal.delaytasks(this.profile_store,params);
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.common.Selecteddocumentaredeletedsuccessfully")],0);
                },
                function(){
                    calMsgBoxShow(27,1);
                });
            }
        },this);
        
    }
});
function docUpload(Id){
    Wtf.getCmp(Id).show_upload_window();
}


