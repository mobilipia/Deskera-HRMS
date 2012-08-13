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
Wtf.appraisalCycleMasterGrid = function(config){
    Wtf.appraisalCycleMasterGrid.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.appraisalCycleMasterGrid, Wtf.Panel, {
    initComponent: function() {
        Wtf.appraisalCycleMasterGrid.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.appraisalCycleMasterGrid.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
            {name:"cycleid"},
            {name:"cyclename"},
            {name:"startdate",type:'date'},
            {name:"enddate",type:'date'},
            {name:"submitstartdate",type:'date'},
            {name:"submitenddate",type:'date'},
            {name:"canapprove"},
            {name:"status"}
        ]);

        this.ds = new Wtf.data.Store({
            baseParams: {
                flag: 301
            },
//            url: 'jspfiles/hrms.jsp',
            url: "Performance/Appraisalcycle/getAppraisalCycle.pf",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },this.record)
        });
         calMsgBoxShow(202,4,true);
        this.ds.load({
            params:{
                start:0,
                limit:15
            }
        });
         this.ds.on('load',function(){
            if(msgFlag==1){
                WtfGlobal.closeProgressbar();
            }
            var a=Wtf.getCmp("DSBMyWorkspaces");
            if(a){
                a.doSearch(a.url,'');
            }
            a=Wtf.getCmp("dash_performance");
            if(a){
                a.doSearch(a.url,'');
            }
         },this);
         this.ds.on('loadexception',function(){
              if(msgFlag==1){
            WtfGlobal.closeProgressbar();
              }
         },this);

        this.sm = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true
        });
        this.cm = new Wtf.grid.ColumnModel([
            this.sm,
            {
                header: WtfGlobal.getLocaleText("hrms.appraisalCycle.CycleName"),
                sortable:true,
                dataIndex: 'cyclename'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),
                sortable:true,
                renderer:WtfGlobal.dateonlyRenderer,
                align:"left",
                dataIndex: 'startdate'
            },
            {
                header:WtfGlobal.getLocaleText("hrms.common.end.date"),
                sortable:true,
                renderer:WtfGlobal.dateonlyRenderer,
                dataIndex: 'enddate'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.submission.start.date"),
                sortable:true,
                renderer:WtfGlobal.dateonlyRenderer,
                dataIndex: 'submitstartdate'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.submission.end.date"),
                sortable:true,
                renderer:WtfGlobal.dateonlyRenderer,
                dataIndex: 'submitenddate'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.appraisalCycle.AppraisalCycleStatus"),
                sortable:true,
                renderer:function(val){
                    if(val=='1'){
                        return '<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.common.Approved")+'</FONT>'
                    }else{
                         return '<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.common.Unapproved")+'</FONT>'
                    }
                },
                dataIndex: 'status'
            }
        ]);

        var empbtns=new Array();
        this.refreshBtn = new Wtf.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.ds.load({params:{start:0,limit:this.grid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.grid.id).setValue("");
        	}
     	});
        this.deleteButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.delete.unwanted.appraisal.cycle"),
            handler:this.delete1,
            disabled:true,
            scope:this
        });
        this.addButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.add"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.Add.tooltip"),
            handler:this.add1,
            scope:this
        });
        this.editButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.edit"),
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.edit.tooltip"),
            handler:this.edit1,
            disabled:true,
            scope:this
        });
        this.sendmailButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.EmailNotification"),
            iconCls:getButtonIconCls(Wtf.btype.emailbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.EmailNotification.tooltip"),
            handler:this.sendemail,
            disabled:true,
            scope:this
        });
        this.sendpdfmail= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.appraisalCycle.NotifyReviewers"),
            iconCls:getButtonIconCls(Wtf.btype.emailbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.NotifyReviewers.tooltip"),
            handler:this.sendemailtoReviewer,
            disabled:true,
            scope:this
        });
        this.sendappraisalreport= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.appraisalCycle.EmailAppraisalCycleReport"),
            iconCls:getButtonIconCls(Wtf.btype.emailbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.EmailAppraisalCycleReport.tooltip"),
            handler:this.sendappraisalReport,
            disabled:true,
            scope:this
        });
        this.approveButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.appraisalCycle.ApproveAppraisalCycle"),
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.ApproveAppraisalCycle.tooltip"),
            handler:function(){
                this.approveapp(true);
            },
            disabled:true,
            scope:this
        });
        this.unapproveButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.appraisalCycle.UnapproveAppraisalCycle"),
            iconCls:getButtonIconCls(Wtf.btype.cancelbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.appraisalCycle.UnapproveAppraisalCycle.tooltip"),
            handler:function(){
                this.approveapp(false);
            },
            disabled:true,
            scope:this
        });
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.setappcycle, Wtf.Perm.setappcycle.manage)){
            empbtns.push('-',this.refreshBtn,'-',this.addButton,"-",this.editButton,"-",this.deleteButton,'-',this.approveButton,'-',this.unapproveButton,'-',this.sendmailButton,'-',this.sendpdfmail,'-',this.sendappraisalreport);
            this.sm.on("selectionchange",function(){
                WtfGlobal.enableDisableBtnArr(empbtns, this.grid, [5,13],[7]);
                if(this.sm.hasSelection()){
                    var rec=this.sm.getSelected();
                    if(rec.data.status==1){
                        this.unapproveButton.enable();
                        this.sendappraisalreport.enable();
                    }else{
                        if(rec.data.canapprove==1){
                            this.approveButton.enable();
                        }
                        this.unapproveButton.disable();
                    }
                    if(rec.data.canapprove==1){
                            this.sendpdfmail.enable();
                        }
                }else{
                    this.approveButton.disable();
                    this.unapproveButton.disable();
                    this.sendpdfmail.disable();
                    this.sendappraisalreport.disable();
                }
            },this);
        }  
        this.grid=new Wtf.KwlGridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            border:false,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.appraisalCycle.Noappraisalcycleadded"))
            },
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.appraisalCycle.SearchByCycleName"),
            searchField:"cyclename",
            serverSideSearch:true,
            displayInfo:true,
            tbar:empbtns

        });
        
        this.add(this.grid);        
    },
    add1:function(){
        this.addappraisalCycle(true);
    },
    edit1:function(){
        this.addappraisalCycle(false);
    },
    delete1:function (){
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:WtfGlobal.getLocaleText("hrms.appraisalCycle.ConfirmDeleteAppraisalCycle"),
            icon:Wtf.MessageBox.WARNING,
            buttons:Wtf.MessageBox.YESNO,
            fn:function (text){
                if(text == "yes"){
                    Wtf.Ajax.requestEx({
                        url:"Performance/Appraisalcycle/deleteAppraisalCycle.pf",
                        params:{
                            flag:43,
                            action:"delete",
                            id:this.sm.getSelected().get('cycleid')
                        }
                    },
                    this,
                    function(response){
                        var res=eval('('+response+')');
                        if(res.success){
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),res.msg],0);
                            this.ds.load({
                                params:{
                                    start:0,
                                    limit:this.grid.pag.pageSize
                                }
                            });
                        }else {
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),res.msg],0);
                        }
                    },
                    function(){
                        calMsgBoxShow(27,1);
                    })
                }
            },
            scope:this
        });
    },
    addappraisalCycle:function(flag){
        this.sDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.start.date")+" *",
            format:'m/d/Y',
            width:200,
            allowBlank:false            
        });
        this.eDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.end.date")+" *",
            format:'m/d/Y',
            width:200,
            allowBlank:false
        });
        this.cycleName= new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Cycle")+' *',
            width:200,
            maxLength:50,
            validator:WtfGlobal.noBlankCheck,
            allowBlank:false
        });

        this.submitSdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.submission.start.date")+" *",
            format:'m/d/Y',
            width:200,
            allowBlank:false
        });
        this.submitEdDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.submission.end.date")+" *",
            format:'m/d/Y',
            width:200,
            minValue:new Date().clearTime(true),
            allowBlank:false
        });

        this.appcycleForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            labelWidth :110,
            layoutConfig: {
                deferredRender: false
            },
            items:[this.cycleName,this.sDate,this.eDate,this.submitSdate,this.submitEdDate]
        });

        this.appcyclePanel= new Wtf.Panel({
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
                    bodyStyle : 'background:white;border-bottom:1px solid #FFFFFF;',
                    html: (flag)?getTopHtml(WtfGlobal.getLocaleText("hrms.appraisalCycle.CreateAppraisalcycle"), WtfGlobal.getLocaleText("hrms.appraisalCycle.CreateAppraisalcycle.subtitle"),'../../images/add-apprisal-cycle.jpg'):getTopHtml( WtfGlobal.getLocaleText("hrms.common.EditAppraisalcycle"),  WtfGlobal.getLocaleText("hrms.common.Pleaseenterfollowingdetailstoeditappraisalcycle"),'../../images/edit-apprisal-cycle.jpg')
                },{
                    border:false,
                    region:'center',
                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    layout:"fit",
                    items: [this.appcycleForm]
                }]
            }]
        });

        this.appcycleWindow=new Wtf.Window({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            closable:true,
            width:400,
            title:WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle"),
            height:330,
            border:false,
            id:this.id+"addeditwindow",
            modal:true,
            scope:this,
            plain:true,
            buttonAlign :'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("hrms.common.submit"),
                handler:function(){
                    this.setappraisalCycle(flag);
                },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope:this,
                handler:function(){
                    this.appcycleWindow.close();
                }
            }],
            items: [this.appcyclePanel]
        });
        this.appcycleWindow.on('show',function(){
            this.cycleName.focus(true,100);
        },this);
        this.appcycleWindow.show();
        this.setDateValues(flag);
    },
    setDateValues:function(flag){
       if(!flag){
           this.cycleName.setValue(this.sm.getSelected().get('cyclename'));
           this.sDate.setValue(this.sm.getSelected().get('startdate'));
           this.eDate.setValue(this.sm.getSelected().get('enddate'));
           this.submitSdate.setValue(this.sm.getSelected().get('submitstartdate'));
           this.submitEdDate.setValue(this.sm.getSelected().get('submitenddate'));
       }
    },
    setappraisalCycle:function(flag){
        if(new Date(this.sDate.getValue())>new Date(this.eDate.getValue())||new Date(this.submitSdate.getValue())>new Date(this.submitEdDate.getValue())){
            calMsgBoxShow(170,1);
            if(new Date(this.sDate.getValue())>new Date(this.eDate.getValue())&&new Date(this.submitSdate.getValue())>new Date(this.submitEdDate.getValue())){
                this.eDate.setValue("");
                this.submitEdDate.setValue("");
            } else if(new Date(this.sDate.getValue())>new Date(this.eDate.getValue())) {
                this.eDate.setValue("");
            } else {
                this.submitEdDate.setValue("");
            }
        }
        
        if(this.sDate!=undefined && this.submitSdate!=undefined && this.submitSdate.getValue().clearTime()<this.sDate.getValue().clearTime()) {
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow230")]);
            return;
        }
        
        if(this.appcycleForm.getForm().isValid()){
             calMsgBoxShow(202,4,true);
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp",
                url:"Performance/Appraisalcycle/setAppraisalcycle.pf",
                params: {
                    flag:167,
                    editflag:flag?0:1,
                    cyclename:this.cycleName.getValue(),
                    cycleid:flag?'':this.sm.getSelected().get('cycleid'),
                    startdate:this.sDate.getValue().format("Y-m-d"),
                    enddate:this.eDate.getValue().format("Y-m-d"),
                    submitsdate:this.submitSdate.getValue().format("Y-m-d"),
                    submitedate:this.submitEdDate.getValue().format("Y-m-d")
                }
            }, this,
            function(response){
                var res=eval('('+response+')');
                if(res.success){
                    if(flag==1){
                        calMsgBoxShow(168,0);
                    }else{
                        calMsgBoxShow(172,0);
                    }
                    this.ds.load({
                        params:{
                            start:this.grid.pag.cursor,
                            limit:this.grid.pag.pageSize
                        }
                    });
                    this.appcycleWindow.close();
                    var initiateTab = Wtf.getCmp("viewapp");
                    if(initiateTab !=null) {
                        initiateTab.appTypeStore.load();
                    }
                }else if(res.msg) {
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),res.msg],0);
                } else {
                    calMsgBoxShow(190,2);
                }
            },
            function(){
                calMsgBoxShow(65,1);
            })
        } else{
            return;
        }
    },
    approveapp:function(status){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.appraisalCycle.confirmchangeappraisalcycle"),function(btn){
            if(btn!="yes") {
                return;
            }else{
                calMsgBoxShow(202,4,true);
                var rec=this.sm.getSelected();
                Wtf.Ajax.requestEx({
//                    url:Wtf.req.base + "hrms.jsp",
                    url: "Performance/Appraisalcycle/approveAppraisalCycle.pf",
                    params: {
                        flag:172,
                        status:status,
                        ids:rec.get('cycleid')
                    }
                }, this,
                function(){
                        this.ds.load({
                            params:{
                                start:this.grid.pag.cursor,
                                limit:this.grid.pag.pageSize
                            }
                        });
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.appraisalCycle.Appraisalcyclesstatuschanged")],0);
                },
                function(){
                    calMsgBoxShow(65,1);
                })
            }
        },this);
    },
    sendemail:function(){
         Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.appraisalCycle.sendemailnotificationtoallappraisees"),function(btn){
            if(btn!="yes") {
                return;
            }else{
                calMsgBoxShow(202,4,true);
                var rec=this.sm.getSelected();
                Wtf.Ajax.requestEx({
//                    url:Wtf.req.base + "hrms.jsp",
                    url:"Performance/Appraisalcycle/sendappraisalemail.pf",
                    params: {
                        flag:173,
                        appraisalcycleid:rec.get('cycleid')
                    }
                }, this,
                function(response){
                    var res=eval('('+response+')');
                    if(res.message){
                        calMsgBoxShow(180,0);
                    } else{
                        calMsgBoxShow(181,2);
                    }
                },
                function(){ 
                    calMsgBoxShow(180,0);
                })
            }
        },this);
    },
    sendemailtoReviewer:function() {
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.appraisalCycle.sendemailnotificationtoreviewer"),function(btn){
            if(btn!="yes") {
                return;
            }else{
                calMsgBoxShow(202,4,true);
                var rec=this.sm.getSelected();
                Wtf.Ajax.requestEx({
//                    url:Wtf.req.base + "hrms.jsp",
                    url:"Performance/Appraisalcycle/sendRevieweremailFunction.pf",
                    params: {
                        flag:62,
                        appraisalcycleid:rec.get('cycleid')
                    }
                }, this,
                function(response){                                        
                        calMsgBoxShow(182,0);
                },
                function(){
                    calMsgBoxShow(182,0);
                })
            }
        },this);

    },
    sendappraisalReport:function() {
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.appraisalCycle.sendappraisalreportemail"),function(btn){
            if(btn!="yes") {
                return;
            }else{
                calMsgBoxShow(202,4,true);
                var rec=this.sm.getSelected();
                Wtf.Ajax.requestEx({
//                    url:Wtf.req.base + "hrms.jsp",
                    url:"Performance/Appraisalcycle/sendappraisalreportEmail.pf",
                    params: {
                        flag:174,
                        appraisalcycleid:rec.get('cycleid')
                    }
                }, this,
                function(){
                        calMsgBoxShow(183,0);
                },
                function(){
                    calMsgBoxShow(183,0);
                })
            }
        },this);

    }
}); 
