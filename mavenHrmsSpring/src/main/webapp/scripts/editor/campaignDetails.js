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
Wtf.campaignDetails = function (config){
    Wtf.campaignDetails.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.campaignDetails,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:'pwndnewCRM emailmarketingTabicon',
    initComponent: function(config) {
        Wtf.campaignDetails.superclass.initComponent.call(this,config);

        var help=getHelpButton(this,35);

        this.addEmailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.AddEmailMarketing"),
            scope:this,
            id:'addemailmarketing35',//In use,do not delete
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.AddEmailMarketing.tooltip")},
            iconCls:"pwnd addEmailMarketing",
            handler:function() {this.emailMarketing(0)}
        });

        this.editEmailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.EditEmailMarketing"),
            scope:this,
            disabled:true,
            iconCls:"pwnd editEmailMarketing",
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.EditEmailMarketing.tooltip")},
            handler:function() {
                if(this.Grid.getSelectionModel().getSelections().length==1) {
                    this.emailMarketing(1);
                } else {
                    ResponseAlert(72);
                }
            }
        });

        this.scheduleEmailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.ScheduleEmailMarketing"),
            scope:this,
            disabled:true,
            iconCls:"pwnd scheduleEmailMarketing",
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.ScheduleEmailMarketing.tooltip")},
            handler:function() {
                var sel = this.Grid.getSelectionModel().getSelections();
                if(sel.length==1) {
                    this.showScheduleWindow(sel[0]);
                } else {
                    ResponseAlert(102)
                }
            }
        });

        this.emailTempBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailTemplates"),
            scope:this,
            id:'emailtemplate35',//In use, do not delete.
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailTemplates.tooltip")},
            iconCls:"pwndCRM templateEmailMarketing",
            handler:function(){
                var panel = Wtf.getCmp('emailTemplate');
                if(panel==null) {
                    panel=new Wtf.emailTemplate({
                        mainTab:this.mainTab
                    })
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        });
        this.TargetListBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetList"),
            scope:this,
            id:'targetlist35', //In use, do not delete.
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetList.tooltip")},
            iconCls:"pwnd targetListEmailMarketing",
            handler:this.targetListHandler
        });

        var Rec = new Wtf.data.Record.create([
            {name:'id'},
            {name:'name'},
            {name:'templatename'},
            {name:'templateid'},
            {name:'fromname'},
            {name:'fromaddress'},
            {name:'replymail'},
            {name:'unsub'},
            {name:'fwdfriend'},
            {name:'archive'},
            {name:'updatelink'},
            {name:'targetcount', type:'int'},
            {name:'createdon'},
            {name:'campaignlog'}
        ]);
            var expander = new Wtf.grid.RowExpander({
                tpl: new Wtf.XTemplate(
                    '<div style="display:block;width:100%;" />',
                    '<tpl for=".">{[this.f(values)]}</tpl></div>', {
                    f: function(val){
                        var obj = val.campaignlog ;
                        var ret = "<table style='padding-left:10px;margin-top:7px;width:60%;border:1px solid grey'>  <caption style='margin-top:5px;'>"+WtfGlobal.getLocaleText("hrms.CampaignDetail.ActivityHistory")+"</caption><thead><tr>" +
                            "<td style='color:#15428B;border-bottom:1px solid black'>Date</td><td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("hrms.common.Sent")+"</td><td style='color:#15428B;border-bottom:1px solid black'>Viewed</td><td style='color:#15428B;border-bottom:1px solid black'>Failed</td></tr></thead><tbody>";
                        if(obj.campaignLogData!=undefined){
                            for(var cnt = 0; cnt < obj.campaignLogData.length; cnt++) {
                                ret += "<tr><td>" + new Date(obj.campaignLogData[cnt].activitydate).format(WtfGlobal.getDateFormat());+ "</td>"
                                ret += "<td>" + obj.campaignLogData[cnt].totalsent + "</td>"
                                ret += "<td>" + obj.campaignLogData[cnt].viewed + "</td>"
                                ret += "<td>" + obj.campaignLogData[cnt].failed + "</td></tr>"
                            }
                        }
                        ret += "</tbody></table>";
                        return ret;
                    }
                    
                })
        });
        
        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams:{
                flag:9,
                campid : this.campaignid
            },
            reader:EditorReader
        });
        this.EditorStore.load();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ expander,new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex:'name',
                renderer : function(val) {
                    return "<a href = '#' class='campAddMarketing'> "+val+"</a>";
                }

            },{
                header:WtfGlobal.getLocaleText("hrms.administration.email.template.tooltip"),
                dataIndex:'templatename'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.CreatedOn"),
                dataIndex:'createdon'
            },{
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.SendTestEmail"),
                dataIndex:'targetcount',
                renderer : function(a){
                    var ret = "";
                    if(a>0){
                        ret = "<a href = '#' class='sendTestMail' wtf:qtip="+WtfGlobal.getLocaleText("hrms.CampaignDetail.SendTestEmail.tooltip")+"> "+WtfGlobal.getLocaleText("hrms.CampaignDetail.SendTestEmail")+" </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.SendEmail"),
                dataIndex:'targetcount',
                renderer : function(a){
                    var ret = "";
                    if(a>0){
                        ret = "<a href = '#' class='campdetails' wtf:qtip="+WtfGlobal.getLocaleText("hrms.CampaignDetail.SendEmail.tooltip")+"> "+WtfGlobal.getLocaleText("hrms.CampaignDetail.SendEmail")+" </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.ViewStatus"),
                dataIndex:'targetcount',
                renderer : function(a){
                    var ret = "";
                    if(a>0){
                        ret = "<a href = '#' class='campchart' wtf:qtip="+WtfGlobal.getLocaleText("hrms.CampaignDetail.ViewStatus.tooltip")+"> "+WtfGlobal.getLocaleText("hrms.CampaignDetail.ViewStatus")+" </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.BounceReport"),
                dataIndex:'id',
                renderer : function(val){
                    var ret = "";
                    if(val != ""){
                        ret = "<a href = '#' class='bouncereport' wtf:qtip="+WtfGlobal.getLocaleText("hrms.CampaignDetail.BounceReport.tooltip")+" style='color:#15428B;text-decoration:none;' >"+WtfGlobal.getLocaleText("hrms.CampaignDetail.BounceReport")+"</a>";
                    }
                    return ret;
                }
            }
        ]);

        this.deleteCon= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:this.campDelete
        });

        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            layout:'fit',
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            },
            searchEmptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.SearchbyName"),
            plugins: expander,
            searchField:"name",
            tbar : ['-',this.addEmailMarketing,this.editEmailMarketing,this.emailTempBtn,this.scheduleEmailMarketing,'->',help]
        });
        this.Grid.on('cellclick', this.afterGridCellClick, this);
        this.campaignpan= new Wtf.Panel({
            layout:'fit',
            border:false,
            items:this.Grid
        })
        this.add(this.campaignpan);
    },
   loadBounceReport: function(emailmarketingid){
       
       this.createBounceReportGrid(emailmarketingid);
       
        
          
          var panelGrid = new Wtf.Panel({
              layout:'fit',
              id:'bounceReportPanel',
              title:WtfGlobal.getLocaleText("hrms.CampaignDetail.BounceReport"),
              closable:true,
              items:[this.bounceReportGrid]
          });
          this.mainTab.add(panelGrid);
          
          this.mainTab.activate(Wtf.getCmp('bounceReportPanel'));
          this.bounceReportStore.load({params:{start:0,limit:30}});
          
          


   },
    createBounceReportGrid:function(emailmarketingid){
        var bounceRec =new Wtf.data.Record.create([
            {name:'email'},
            {name:'fname'},
            {name:'lname'},
            {name:'status'},
            {name:'description'},
            {name:'targetid'}
            
        ]);
  this.bounceReportColumn = new Wtf.grid.ColumnModel(
            [new Wtf.grid.CheckboxSelectionModel({}),new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.common.EmailAddress"),
                dataIndex:'email'

            },{
                header:WtfGlobal.getLocaleText("hrms.common.FirstName"),
                dataIndex:'fname'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.LastName"),
                dataIndex:'lname'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.Reason"),
                dataIndex:'status',
                renderer : function(val,meta,record){
                    var ret = "";
                    if(val !=""){
                        ret = "<span wtf:qtip='"+record.get("description")+"'>"+val+"</span><img src=\"images/information.png\" style='vertical-align:middle;margin-left:5px;'wtf:qtip='"+record.get("description")+"'>";
                    }
                    return ret;
                }
            }
            
        ]);
        this.bounceReportStore = new Wtf.data.Store({
            url: Wtf.req.base + "crm.jsp",
            
            baseParams:{
                flag:827,
                emailmarketingid:emailmarketingid
            },
            reader:new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
            },bounceRec)
        });

        this.bounceReportGrid = new Wtf.KwlGridPanel({
            
            store: this.bounceReportStore,
            cm: this.bounceReportColumn,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.bounceReportGrid.EmptyText")
            },
            tbar:[new Wtf.Button({
                    text:WtfGlobal.getLocaleText("hrms.CampaignDetail.RemoveFromTargetList"),
                    handler:function(){
                        selModel = this.bounceReportGrid.getSelectionModel();

                        var recarray = selModel.getSelections();
                        
                        var targets = "";
                        for(var i=0; i< recarray.length;i++){
                            if(i>0){
                            targets +=",";
                            }
                            targets +=recarray[i].get("targetid");
                        }
                        if(targets !=""){
                         Wtf.Ajax.requestEx({
                            url: "../../jspfiles/crm.jsp",
                            params: {
                                flag: 828,
                                targets: targets
                                
                            }
                        }, this, function(action, response){
                            Wtf.MessageBox.show({
                                title:WtfGlobal.getLocaleText("hrms.common.success"),
                                msg:WtfGlobal.getLocaleText("hrms.CampaignDetail.Selectedtargetdeleted"),
                                icon:Wtf.MessageBox.INFO,
                                buttons:Wtf.MessageBox.OK
                            });
                              this.bounceReportStore.load({params:{start:0,limit:25}});
                        }, function(action, response){
                            Wtf.MessageBox.show({
                                title:WtfGlobal.getLocaleText("hrms.common.error"),
                                msg:WtfGlobal.getLocaleText("hrms.CampaignDetail.ErrorSelectedtargetdeleted"),
                                icon:Wtf.MessageBox.ERROR,
                                buttons:Wtf.MessageBox.OK
                            });
                        });
                        }else{
                            Wtf.MessageBox.show({
                                title:WtfGlobal.getLocaleText("hrms.common.error"),
                                msg:WtfGlobal.getLocaleText("hrms.CampaignDetail.Pleaseselectatleastonetargetfromthelist"),
                                icon:Wtf.MessageBox.ERROR,
                                buttons:Wtf.MessageBox.OK
                            });
                        }
                    },scope:this})
            ],
            bbar: new Wtf.PagingSearchToolbar({
                pageSize: 30,
                displayInfo: true,
                store: this.bounceReportStore,
                plugins:this.pP = new Wtf.common.pPageSize({
                })
            })
        });


    },
    showScheduleWindow: function(sel){
        var hrStore = new Wtf.data.SimpleStore({
            fields: ["id", "value"],
            data: [["0", "00:00"], ["1", "00:30"], ["2", "01:00"], ["3", "01:30"], ["4", "02:00"], ["5", "02:30"], ["6", "03:00"],
                ["7", "03:30"], ["8", "04:00"], ["9", "04:30"], ["10", "05:00"], ["11", "05:30"], ["12", "06:00"], ["13", "06:30"],
                ["14", "07:00"], ["15", "07:30"], ["16", "08:00"], ["17", "08:30"], ["18", "09:00"], ["19", "09:30"], ["20", "10:00"],
                ["21", "10:30"], ["22", "11:00"], ["23", "11:30"], ["24", "12:00"], ["25", "12:30"], ["26", "13:00"], ["27", "13:30"],
                ["28", "14:00"], ["29", "14:30"], ["30", "15:00"], ["31", "15:30"], ["32", "16:00"], ["33", "16:30"], ["34", "17:00"],
                ["35", "17:30"], ["36", "18:00"], ["37", "18:30"], ["38", "19:00"], ["39", "19:30"], ["40", "20:00"], ["41", "20:30"],
                ["42", "21:00"], ["43", "21:30"], ["44", "22:00"], ["45", "22:30"], ["46", "23:00"], ["47", "23:30"]]
        });
        var hrCombo = new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.CampaignDetail.ScheduleTime"),
            store: hrStore,
            displayField: "value",
            valueField: "id",
            width: 200,
            allowBlank: false,
            mode: "local",
            triggerAction: "all"
        });
        var dt = new Date();
        dt.setDate(dt.getDate() + 1);
        var scheduleDate = new Wtf.form.DateField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.CampaignDetail.DeliveryDate"),
            border: false,
            minValue: dt,
            width: 200,
            allowBlank: false
        });
        var schedularForm = new Wtf.form.FormPanel({
            region: "center",
            border: false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px 10px 10px 30px;',
            items: [scheduleDate, hrCombo/*, {
                xtype: "panel",
                border: false,
                html: "<center><span style='color: green;font-weight:bold'>As per CST</span></center>"
            }*/]
        });
        var win = new Wtf.Window({
            title: WtfGlobal.getLocaleText("hrms.CampaignDetail.Scheduledelivery"),
            modal: true,
            height: 210,
            iconCls:"pwnd favwinIcon",
            width: 380,
            resizable: false,
            layout: "border",
            items: [{
                region: "north",
                bodyStyle: "background-color: white",
                border:false,
                height: 65,
                html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.Schedule"), WtfGlobal.getLocaleText("hrms.CampaignDetail.ScheduleForm.Title"),"../../images/activity1.gif")
            }, schedularForm],
            buttons: [{
                text: WtfGlobal.getLocaleText("hrms.common.Schedule"),
                scope: this,
                handler: function(){
                    if(schedularForm.form.isValid()){
                        var sTime = hrCombo.getValue();
                        var index = hrCombo.store.find("id", sTime);
                        if(index != -1)
                            sTime = hrCombo.store.getAt(index).data["value"];
                        else
                            sTime = "00:00";
                        var sDt = scheduleDate.getValue().format("Y-m-d");
                        Wtf.Ajax.requestEx({
                            url: "../../jspfiles/campaign.jsp",
                            params: {
                                flag: 28,
                                emailmarketingid: sel.data["id"],
                                scheduledate: sDt,
                                scheduletime: sTime
                            }
                        }, this, function(action, response){
                            win.close();
                        }, function(action, response){
                            win.close();
                        });
                    }
                    else
                        ResponseAlert(103);
                }
            },{
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope: this,
                handler: function(){
                    win.close();
                }
            }]
        });
        win.show();
    },
    afterGridCellClick : function(Grid,rowIndex,columnIndex, e ) {
        var event = e ;
        if(event.getTarget("a[class='campdetails']")) {
            Wtf.Ajax.timeout=1200000;
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.CampaignDetail.Sendingmail"));
            Wtf.Ajax.request({
                url : Wtf.req.base + "campaign.jsp",
                params:{
                    emailmarkid : Grid.store.getAt(rowIndex).data.id,
                    campid : this.campaignid,
                    flag : 11
                },
                scope : this,
                success:function(res,req){
                    var obj = eval('('+res.responseText.trim()+')');
                    Wtf.updateProgress();
                    Wtf.Ajax.timeout=30000;
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.CampaignDetail.SendingEmailsforyourMarketingCampaign"),
                        msg:obj.data.msg,
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                 },
                failure : function() {
                    Wtf.updateProgress();
                    Wtf.Ajax.timeout=30000;
            }});
        }
        if(event.getTarget("a[class='campchart']")) {
            var chartid = "CampaignMailStatus"+Grid.store.getAt(rowIndex).data.id;
            var id = this.id+"graph";
            var swf = "../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
            var dataflag = "22&campID="+this.campaignid+"&mailMarID="+Grid.store.getAt(rowIndex).data.id+"";
            var mainID = this.mainTab.id;
            var xmlpath='../../scripts/graph/krwcolumn/examples/CampaignMailStatus/CampaignMailStatus_settings.xml';
            var param = "mailMarID="+Grid.store.getAt(rowIndex).data.id+"&campID="+this.campaignid;
            var tipTitle =Grid.store.getAt(rowIndex).data.name;
            var maintitle = Wtf.util.Format.ellipsis(tipTitle,20);
            var title = "<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Chart View'>"+maintitle+"</div>";
            var showHtml = 'false';
            globalChart(chartid,id,swf,dataflag,mainID,xmlpath,Wtf.id(),showHtml,"","",tipTitle);
        }
        var panel = Wtf.getCmp('comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId);
        if(panel!=null){
            this.editEmailMarketing.disable();
            this.scheduleEmailMarketing.disable();
            this.addEmailMarketing.disable();
        }else{
            this.editEmailMarketing.enable();
            this.scheduleEmailMarketing.enable();
        }
        if(event.getTarget("a[class='campAddMarketing']")) {
            this.emailMarketing(1);
        }
        if(event.getTarget("a[class='sendTestMail']")) {
            
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.CampaignDetail.Sendingtestmail"));
            Wtf.Ajax.requestEx({
                url : Wtf.req.base + "campaign.jsp",
                params:{
                    emailmarkid : Grid.store.getAt(rowIndex).data.id,
                    campid : this.campaignid,
                    flag : 12
                }},this,
                function(){
                    Wtf.updateProgress();
                    ResponseAlert(400);
                 },
                function() {
                    Wtf.updateProgress();
                    ResponseAlert(401);
                }
            );
        }if(event.getTarget("a[class='bouncereport']")) {
            var emailmarketingid =Grid.store.getAt(rowIndex).data.id;
            this.loadBounceReport(emailmarketingid);
        }
    },

    emailMarketing : function(mode) {
        this.mode = mode;
        var tipTitle=WtfGlobal.getLocaleText("hrms.CampaignDetail.AddEmailMarketing");
        var IconCls = "pwnd addEmailMarketingTab";
        this.emailMarkId = "0";
        if(mode==1) {
            this.recData = this.Grid.getSelectionModel().getSelected().data;
            tipTitle=WtfGlobal.getLocaleText("hrms.CampaignDetail.EditEmailMarketing")+" : "+this.recData.name;
            IconCls ="pwnd editEmailMarketingTab";
            this.emailMarkId = this.recData.id;
        }
        var panel = Wtf.getCmp('comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId);//+this.recDataId);

        var title = Wtf.util.Format.ellipsis(tipTitle,19);
        if(panel==null) {

            this.addEmailMarketing.disable();
            this.editEmailMarketing.disable();
            this.scheduleEmailMarketing.disable();
            this.setupRec = Wtf.data.Record.create([{
                name: "id"
            },{
                name: "title"
            },{
                name: "description"
            },{
                name: "licls"
            }]);
            this.campaignSetup = new Wtf.data.SimpleStore({
                fields: ["id", "title", "description", "licls"]
            });
            this.addEmailMarketFun();
            this.card1 = new Wtf.ux.Wiz.Card({
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.SetEmailDetails"),
                layout: "fit",
                border: false,
                items: this.addEmailMarketCmp
            });
            this.card2 = new Wtf.ux.Wiz.Card({
                border: false,
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.ChooseanEmailTemplate"),
                layout: "fit"
            });
            this.card2.on("show", this.showTemplateSelector, this);
            this.card3 = new Wtf.ux.Wiz.Card({
                border: false,
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.EditEmailTemplate"),
                layout: "fit"
            });
            this.card3.on("show", this.showTemplateEditor, this);
            this.card4 = new Wtf.ux.Wiz.Card({
                border: false,
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.EnteryourPlainTextMessage"),
                layout: "fit"
            });
            this.card4.on("show", this.showPlainMessageEditor, this);
            this.card5 = new Wtf.ux.Wiz.Card({
                layout: "fit",
                border: false,
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.FinalCampaignSetup")
            });
            this.card5.on("show", this.showFinalSetup, this);
            panel=new Wtf.ux.Wiz({
                closable: true,
                iconCls:IconCls,
                id : 'comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId,
                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle="+WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailMarketing")+">"+title+"</div>",
                headerConfig: {
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle="+WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailMarketing")+">"+tipTitle+"</div>"
                },
                cards:[this.card1, this.card2, this.card3, this.card4, this.card5]
            });
            panel.on("beforeNextcard", this.beforeNext, this);
            panel.on("beforefinish", this.beforeFinish, this);
            this.mainTab.add(panel);
        }
        this.mainTab.setActiveTab(panel);
        this.mainTab.doLayout();
        this.mainTab.on('remove',function(tp,panel){
            if(panel.id=='comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId) {
                this.addEmailMarketing.enable();
                this.Grid.getSelectionModel().clearSelections();
            }
        },this)
    },
    showFinalSetup: function(){
        var finalSetup = Wtf.getCmp("final_setup_card");
        if(!finalSetup) {
            this.finalSetupView = new Wtf.DataView({
                store: this.campaignSetup,
                itemSelector: "final_setup_card",
                tpl: new Wtf.XTemplate('<ul class="finalList"><tpl for=".">{[this.f(values)]}</tpl></div>', {
                    f: function(val){
                        return "<li class='" + val.licls + "'><label>" + val.title + "</label>" + val.description + "</li>";
                    },
                    scope: this
                })
            });
            finalSetup = new Wtf.Panel({
                id: "final_setup_card",
                bodyStyle: "background-color: white",
                autoScroll: true,
                html:"<span >"+WtfGlobal.getLocaleText("hrms.CampaignDetail.sdfsdf")+" </span>",
                items: this.finalSetupView
            });
            this.card5.add(finalSetup);
        }
        this.card5.doLayout();
    },
    showTemplateEditor: function(){
        var templateEditor = Wtf.getCmp("wizardTemplateEditor_" + this.id);
        if(templateEditor == null) {
            var selTemp = this.selEmailTempCmp.selectedTemplate;
            if(selTemp) {
                templateEditor = new Wtf.campaignMailEditor({
                    mode: this.mode,
                    marketRec: this.recData,
                    id: "wizardTemplateEditor_" + this.id,
                    templateRec: selTemp.tempRec,
                    templateid: selTemp.tempRec.data["templateid"]
                });
                this.card3.add(templateEditor);
                this.card3.doLayout();
            }
        }
    },
    showPlainMessageEditor: function(){
        var plainMessage = Wtf.getCmp("Plaintext_textarea_form");
        if(!plainMessage) {
            var val = Wtf.getCmp("wizardTemplateEditor_" + this.id).getPlainMessage();
            plainMessage = new Wtf.form.FormPanel({
                cls: "plainTextForm",
                id: "Plaintext_textarea_form",
                defaults: {
                    labelStyle: "width: 100%; margin-bottom: 7px;",
                    ctCls: "newTicketField"
                },
                items: [{
                    value: val.trim(),
                    id: "mail_plaintext_textfield",
                    fieldLabel: WtfGlobal.getLocaleText("hrms.CampaignDetail.PlainMessageEditorText"),
                    xtype: "textarea",
                    height: "95%",
                    width: "98%"
                }]
            });
            this.card4.add(plainMessage);
        }
    },
    addEmailMarketFun: function() {
        var recDataIdEmail="";
        if(this.mode==1){
            recDataIdEmail = this.recData.id;
        }
        var addEmailMarketCmp = Wtf.getCmp("addEmailMarketCmp_" + this.id+this.mode+recDataIdEmail);
        if(!addEmailMarketCmp) {
            this.addEmailMarketCmp = new Wtf.addEmailMarketCmp({
                id: "addEmailMarketCmp_" + this.id+this.mode+recDataIdEmail,
                emailmarkid: this.mode==1 ? this.recData.id :'',
                campaignid: this.campaignid,
                templateid: this.mode==1 ? this.recData.templateid :'',
                recData: this.recData,
                mode: this.mode,
                campaignname:this.campaignname,
                mainTab:this.mainTab
            });
        }
    },
    showTemplateSelector: function() {
        var selEmailTempCmp = Wtf.getCmp("selEmailTempCmp_"+this.templateid);
        if(selEmailTempCmp == null) {
            this.selEmailTempCmp =new Wtf.campaignMailTemplate({
                id : "selEmailTempCmp_"+this.templateid,
                selectedTemplate: (this.mode == 1) ? this.recData.templateid : null,
                border: false,
                mainTab:this.mainTab
            });
            this.card2.add(this.selEmailTempCmp);
            this.card2.doLayout();
        }
    },
    storeSetupInformation: function(obj, activeCard){
        switch(activeCard) {
            case 0:
                this.campaignSetup.add(this.getInitialSetup());
                break;
            case 1:
                this.campaignSetup.add(this.getTemplateSetup());
                break;
//            case 2:
//                break;
            case 3:
                this.campaignSetup.add(this.getPlainTextSetup());
                break;
        }
    },
    getInitialSetup: function(){
        var _aEM = this.addEmailMarketCmp;
        var temp = [];
        var list = _aEM.getList();
        var desc = WtfGlobal.getLocaleText("hrms.CampaignDetail.DeskeraHRMSwilldeliverthistothe");
        var _lN = "";
        for(var cnt = 0; cnt < list.length; cnt++)
            _lN += list[cnt].data["listname"] + ",";
        var liCls;
        if(_lN != ""){
            desc += _lN.substring(0, (_lN.length - 1));
            liCls =WtfGlobal.getLocaleText("hrms.common.success");
        } else {
            desc = WtfGlobal.getLocaleText("hrms.CampaignDetail.Nolistselectedtosendthiscampaign")
            liCls =WtfGlobal.getLocaleText("hrms.common.error");
        }
        this.removeSetupRec("list");
        temp[temp.length] = new this.setupRec({
            id: "list",
            title: WtfGlobal.getLocaleText("hrms.common.List"),
            licls: liCls,
            description: desc
        });
        desc = _aEM.getReplyMail();
        this.removeSetupRec("reply");
        temp[temp.length] = new this.setupRec({
            id: "reply",
            title: WtfGlobal.getLocaleText("hrms.CampaignDetail.Replies"),
            licls: (desc != "") ? WtfGlobal.getLocaleText("hrms.common.success") :WtfGlobal.getLocaleText("hrms.common.error"),
            description: (desc != "") ? desc : "No reply email specifed."
        });
        desc = _aEM.getSenderMail();
        this.removeSetupRec("sender");
        temp[temp.length] = new this.setupRec({
            id: "sender",
            title: WtfGlobal.getLocaleText("hrms.CampaignDetail.Senderemail"),
            licls: (desc != "") ? WtfGlobal.getLocaleText("hrms.common.success"): WtfGlobal.getLocaleText("hrms.common.error"),
            description: (desc != "") ? desc : "No sender email specified."
        });
//        desc = "All the tags have be specified.";
//        if(_aEM.getUnsubscribeLink() == "" || _aEM.getForwardLink() == "" || _aEM.getUpdateLink() == "" || _aEM.getArchiveLink() == "") {
//            desc = "";
//        }
//        this.removeSetupRec("tags");
//        temp[temp.length] = new this.setupRec({
//            id: "tags",
//            title: "Tag setup",
//            licls: (desc != "") ? "success" : "error",
//            description: (desc != "") ? desc : "Some of the tags are not specified."
//        });
        return temp;
    },
    getTemplateSetup: function(){
        var temp = [];
        var selTemp = this.selEmailTempCmp.getSelectedTemplate();
        if(selTemp) {
            selTemp = selTemp.tempRec;
            this.removeSetupRec("subject");
            temp[temp.length] = new this.setupRec({
                id: "subject",
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.Subjectline"),
                licls: WtfGlobal.getLocaleText("hrms.common.success"),
                description: selTemp.data["subject"]
            });
            this.removeSetupRec("html");
            temp[temp.length] = new this.setupRec({
                id: "html",
                title: WtfGlobal.getLocaleText("hrms.CampaignDetail.HTMLemail"),
                licls: WtfGlobal.getLocaleText("hrms.common.success"),
                description: WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.YouaresendingHTMLemail",params:["<span class='boldtext'>" + selTemp.data["templatename"] + "</span>"]})
            });
        }
        return temp;
    },
    getPlainTextSetup: function(){
        var temp = [];
        var desc = Wtf.getCmp("mail_plaintext_textfield").getValue().trim();
        var licls = WtfGlobal.getLocaleText("hrms.common.error");
        if(desc != ""){
            desc = WtfGlobal.getLocaleText("hrms.CampaignDetail.Youincludedplaintextversion");
            licls = WtfGlobal.getLocaleText("hrms.common.success");
        } else
            desc = WtfGlobal.getLocaleText("hrms.CampaignDetail.Youincludedplaintextversion");
        this.removeSetupRec("plainmsg");
        temp[temp.length] = new this.setupRec({
            id: "plainmsg",
            title: WtfGlobal.getLocaleText("hrms.CampaignDetail.Plaintextemail"),
            licls: licls,
            description: desc
        });
        return temp;
    },
    removeSetupRec: function(id){
        if(id != "") {
            var _cR = this.campaignSetup.query("id", id, false, true);
            _cR = _cR.items;
            if(_cR.length > 0)
                for(var cnt = 0; cnt < _cR.length; cnt++)
                    this.campaignSetup.remove(_cR[cnt]);
        }
    },
    beforeNext: function(obj, activeCard) {
        var flg = true;
        if(activeCard == 0) {
            flg = this.addEmailMarketCmp.activityform.form.isValid();
            if(flg){
                var list = this.addEmailMarketCmp.getList();
                if(list.length > 0){
                    flg = true;
                } else {
                    flg = false;
                    WtfComMsgBox(956,0);
                }
            }
        } else if(activeCard == 1) {
            var selTemp = this.selEmailTempCmp.getSelectedTemplate();
            if(selTemp !== null){
                var _tE = Wtf.getCmp("wizardTemplateEditor_" + this.id);
                if(_tE){
                    _tE.changeTemplate(selTemp.tempRec);
                }
                flg = true;
                this.tempID = selTemp.tempRec.data["templateid"];
            } else{
                ResponseAlert(600);
                flg = false;
            }
        }
        if(flg)
            this.storeSetupInformation(obj, activeCard);
        return flg;
    },
    beforeFinish: function(wizObj){
        var _aEM = this.addEmailMarketCmp;
        var _tE = Wtf.getCmp("wizardTemplateEditor_" + this.id);
        var senderId = _aEM.getSenderMail();
        var _tD = "[";
        var _tL = _aEM.getList();
        for(var cnt = 0; cnt < _tL.length; cnt++) {
            _tD += '{"listid" : "'+_tL[cnt].data.listid+'"},';
        }
        _tD = _tD.substring(0, (_tD.length - 1)) + "]";
        Wtf.Ajax.requestEx({
            url : Wtf.req.base + "campaign.jsp",
            params:{
                name: _aEM.getName(),
//                unsub: _aEM.getUnsubscribeLink(),
//                fwdfriend: _aEM.getForwardLink(),
//                archive: _aEM.getArchiveLink(),
//                updatelink: _aEM.getUpdateLink(),
                unsub: "",
                fwdfriend: "",
                archive: "",
                updatelink: "",
                fromaddress: senderId,
                replyaddress: _aEM.getReplyMail(),
                fromname: _aEM.getFromName(),
                inboundemail: senderId,
                templateid: this.tempID,
                campid: this.campaignid,
                targetlist: _tD,
                colortheme: _tE.getColorTheme(),
                htmlcont: _tE.getTemplateHtml(),
                plaincont: Wtf.getCmp("mail_plaintext_textfield").getValue().trim(),
                emailmarkid: this.mode ==1 ? this.recData.id: '',
                mode: this.mode,
                flag: 10
            }
        },this,
        function(){
            WtfComMsgBox(953,0);
            if(this.mode==0){
                this.EditorStore.load();
            }
            Wtf.getCmp('comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId).closePanel();
            this.addEmailMarketing.enable();
            this.Grid.getSelectionModel().clearSelections();
        },
        function() {
            WtfComMsgBox(954,1);
        });
    },

    targetListHandler : function() {
        var tlId = 'targetlistgrid';
        var targetComp = Wtf.getCmp(tlId );
        if(targetComp==null) {
            targetComp=new Wtf.targetListDetails({
                title:WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetLists"),
                id:tlId,
                mainTab:this.mainTab
            })
            this.mainTab.add(targetComp);
        }
        this.mainTab.setActiveTab(targetComp);
        this.mainTab.doLayout();
    }
});



Wtf.emailTemplate = function (config){
    Wtf.emailTemplate.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.emailTemplate,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    title : WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailTemplates"),
    id : this.id!="emailTemplatedashboard"?'emailTemplate':this.id,
    border:false,
    iconCls:getTabIconCls(Wtf.etype.acc),
    initComponent: function(config) {
        Wtf.campaignDetails.superclass.initComponent.call(this,config);
        this.getEditorGrid();
    },

    getEditorGrid : function () {
        var Rec = new Wtf.data.Record.create([
            {name:'templateid'},
            {name:'templatename'},
            {name:'description'},
            {name:'subject'},
            {name:'bodyhtml'},
            {name:'createdon'},
            {name:'modifiedon'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalcount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: "Common/Template/getTemplates.common" ,//Wtf.req.base + "campaign.jsp",
            pruneModifiedRecords:true,
            baseParams:{
                flag:1
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.TemplateName"),
                dataIndex:'templatename',
                sortable: true,
                renderer : function(val) {
                    return "<a href = '#' class='campdetails'> "+val+"</a>";
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.description"),
                dataIndex:'description',
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.common.CreatedOn"),
                dataIndex:'createdon',
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.common.ModifiedOn"),
                dataIndex:'modifiedon',
                sortable: true
            }]);

        this.deleteCon= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:this.campDelete
        });

        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            layout:'fit',
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            border : false,
            height:400,
            loadMask : true,
            displayInfo:true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addemltem(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.CampaignDetail.TemplateEditerGrid.EmptyText")+"</a>")
            },
            searchEmptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.SearchbyTemplateName"),
            searchField:"templatename",
            searchLabel:" ",
            searchLabelSeparator:" ",
            serverSideSearch:true,
            tbar:['-',new Wtf.Toolbar.Button({
         		text:WtfGlobal.getLocaleText("hrms.common.reset"),
         		scope: this,
         		iconCls:'pwndRefresh',
         		handler:function(){
            		this.EditorStore.load({params:{start:0,limit:this.Grid.pag.pageSize}});
            		Wtf.getCmp("Quick"+this.Grid.id).setValue("");
            		}
         		}),'-',{
                text:WtfGlobal.getLocaleText("hrms.administration.new.template"),
                scope : this,
                tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.NewTemplate.Tooltip")},
                iconCls:"pwndCommon addbuttonIcon",
                handler:this.addemltem
            },'-',{
                text:WtfGlobal.getLocaleText("hrms.CampaignDetail.DeleteTemplate"),
                scope : this,
                tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.DeleteTemplate.tooltip")},
                iconCls:"pwndCommon deletebuttonIcon",
                handler:this.delTemplate
                    /*function(){
                    Wtf.deleteGlobal(this.Grid,this.EditorStore,'EmailTemplate',"templateid","templateid",'Email Template',55,56,57);
                }*/
            }]
        });
    //    this.add(this.Grid);
        if(this.id=="emailTemplatedashboard"){
            this.templatePanel= new Wtf.Panel({
                title:WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailTemplates"),
                iconCls:getTabIconCls(Wtf.etype.acc),
                border:false,
                id:this.id+'emailtemplatepan',
                layout:'fit',
                items:[{
                    layout:'fit',
                    border:false,
                    items:[this.Grid]
                }
                ]
            });
            this.mainTab=new Wtf.TabPanel({
               id:this.id+"emailtemplatetabPanel",
               scope:this,
               border:false,
               resizeTabs: true,
               minTabWidth: 155,
               enableTabScroll: true,
               items:[this.templatePanel]
            });
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.templatePanel);
         } else{
             this.add(this.Grid);
         }
            this.doLayout();
        
        this.Grid.on("cellclick",this.gridCellClick,this);
    },

    addemltem:function(){
        var tipTitle=WtfGlobal.getLocaleText("hrms.administration.new.template");
                    var title = Wtf.util.Format.ellipsis(tipTitle,18);
                    var panel="";
                    if(this.id=="emailTemplatedashboard"){
                        panel = Wtf.getCmp('template_dash_win'+this.templateid);
                    } else {
                        panel = Wtf.getCmp('template_wiz_win'+this.templateid);
                    }
                    if(panel==null) {
                        panel=new Wtf.newEmailTemplate({
                            store: this.EditorStore,
                            title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle="+WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailTemplates")+">"+title+"</div>",
                            tipTitle:tipTitle,
                            mailTemplate:this.mailTemplate,
                            dashboardCall:this.id=="emailTemplatedashboard"?true:false
                        });
                        panel.on('render',function(){
                            panel.tempname.focus(true,100);
                        },this);
                        this.mainTab.add(panel);
                    }
                    this.mainTab.setActiveTab(panel);
                    this.mainTab.doLayout();
    },

    gridCellClick:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.getTarget("a[class='campdetails']")) {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var panel = Wtf.getCmp('template_wiz_win'+recdata.templateid);
            var tipTitle=recdata.templatename+" : "+ WtfGlobal.getLocaleText("hrms.CampaignDetail.EditTemplate");
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
                if(panel==null) {
                    panel=new Wtf.newEmailTemplate({
                        templateid : recdata.templateid,
                        tname : recdata.templatename,
                        tdesc : recdata.description,
                        tsubject : recdata.subject,
                        tbody : recdata.bodyhtml,
                        store: this.EditorStore,
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle="+WtfGlobal.getLocaleText("hrms.CampaignDetail.EmailTemplates")+">"+title+"</div>",
                        tipTitle:tipTitle
                    });
                    panel.on('render',function(){
                            panel.tempname.focus(true,100);
                    },this);
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();

        }
        },
    delTemplate:function(){
        if(this.Grid.getSelectionModel().getCount()>0){
                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"),WtfGlobal.getLocaleText("hrms.CampaignDetail.Doyouwanttodeletethistemplate"),function(btn){
                        if(btn!="yes") {
                            this.close();
                        }else{
                            var recData = this.Grid.getSelectionModel().getSelected().data;
                            Wtf.Ajax.requestEx({
                                url: "Common/Template/delTemplate.common"  ,
                                params: {
                                //    dele:true,
                               //     delType:'TaxSlab',
                                 //   slabid:recData.slabid
                                        tempid:recData.templateid
                                }
                            },
                            this,
                            function(response){
                                 calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.CampaignDetail.Templatedeletedsuccessfully")],0);
//                                calMsgBoxShow(["Success",""+response.msg+""],0);
                               //- this.fireEvent('updategrid',this);
                                this.EditorStore.reload();
                                //  this.Form.getForm().reset();
                            },
                            function(){
                                Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.Message"),WtfGlobal.getLocaleText("hrms.CampaignDetail.Someerroroccoured"));
                            }
                            );

                        }
               },this);
        } else {
            calMsgBoxShow(42,1);
        }
    }
});
function addemltem(Id){
 Wtf.getCmp(Id).addemltem();
}
Wtf.targetListDetails = function (config){
    Wtf.targetListDetails.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.targetListDetails,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:"pwndnewCRM targetlistTabicon",
    initComponent: function(config) {
        Wtf.targetListDetails.superclass.initComponent.call(this,config);
       
        var Rec = new Wtf.data.Record.create([
            {name:'listid'},
            {name:'listname'},
            {name:'description'},
            {name:'createdon'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams:{
                flag:4
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [new Wtf.grid.RowNumberer(),
             new Wtf.grid.CheckboxSelectionModel(),
            {
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetList"),
                dataIndex:'listname',
                renderer : function(val) {
                    return "<a href = '#' class='campTargetList'> "+val+"</a>";
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.description"),
                dataIndex:'description'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.CreatedOn"),
                dataIndex:'createdon'
        }]);
       var dashboardCall=false;
       if(this.id=="campaigntargetdetail"){
           dashboardCall=true;
       }
       this.newTargetsBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.New"),
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.AddNewTargetList")},
            scope:this,
            iconCls:"pwnd newTargetListEmailMarketing",
            handler:function(){this.targetsHandler(0,dashboardCall)}
        });

        this.editTargetsBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.edit"),
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.EditTargetList")},
            scope:this,
            iconCls:"pwnd editTargetListEmailMarketing",
            handler:function() {
                if(this.Grid.getSelectionModel().getSelections().length==1) {
                    this.targetsHandler(1,dashboardCall);
                } else {
                    ResponseAlert(67);
                }
            }
        });

        this.deleteTargetsBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.DeletetheselectedTargetLists")},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:function() {
                    Wtf.deleteGlobal(this.Grid,this.EditorStore,'Target List',"listid","listid",'TargetList',64,65,66);
                 }
        });
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetListGrid.emptytext")
            },
            tbar : ['-',this.newTargetsBtn,this.editTargetsBtn,this.deleteTargetsBtn],
            searchEmptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.SearchbyTargetList"),
            displayInfo : true,
            searchField:"listname"
        });

        this.targetList= new Wtf.Panel({
            layout:'fit',
            border:false,
            height:400,
            items:this.Grid
        })

       if(this.id=="campaigntargetdetail"){
            this.targetPanel= new Wtf.Panel({
                title:WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetLists"),
                iconCls:"pwndnewCRM targetlistTabicon",
                border:false,
                layout:'fit',
                id:this.id+'targetpan',
                items:[{
                    layout:'fit',
                    border:false,
                    items:[this.targetList]
                }
                ]
            });
            this.mainTab=new Wtf.TabPanel({
               id:this.id+"targetTabPanel",
               scope:this,
               border:false,
               resizeTabs: true,
               minTabWidth: 155,
               enableTabScroll: true,
               items:[this.targetPanel]
            });
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.targetPanel);
        } else {
            this.add(this.targetList);
        }
        this.doLayout();
        this.Grid.on("cellclick",this.targetGridCellClick,this);
    },
    targetGridCellClick:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.getTarget("a[class='campTargetList']")) {

            var mode=1;//for Edit
            var record = this.Grid.getSelectionModel().getSelected();
            var listID = record.get('listid');
            var listName = record.get('listname')+" ";
            
            var tlId = 'targetListTabnewedit_dash'+mode+listID;
            
            var targetListTab = Wtf.getCmp(tlId );
            if(targetListTab == null) {
                targetListTab = new Wtf.targetListWin({
                    mode : mode,
                    record : record,
                    id : tlId,
                    listID : listID,
                    TLID : listID,
                    store:this.EditorStore,
                    listname : listName,
                    iconCls: "pwnd editTargetListEmailMarketingWin",
                    mainTab:this.mainTab
                })
                this.mainTab.add(targetListTab);
            }
            this.mainTab.setActiveTab(targetListTab);
            this.mainTab.doLayout();

        }
    },
    targetsHandler : function(mode,dashboardCall) {
        var record;
        var listID = '';
        var listName ="New ";
        if(mode == 1) {
             record = this.Grid.getSelectionModel().getSelected();
             listID = record.get('listid');
             listName = record.get('listname')+" ";
        }
        var tlId = 'targetListTabnewedit'+mode+listID;
        if(dashboardCall){
            tlId = 'targetListTabnewedit_dash'+mode+listID;
        }
        var targetListTab = Wtf.getCmp(tlId );
        if(targetListTab == null) {
            targetListTab = new Wtf.targetListWin({
                mode : mode,
                record : record,
                id : tlId,
                listID : listID,
                TLID : listID,
                store:this.EditorStore,
                listname : listName,
                iconCls: (mode==0?"pwnd newTargetListEmailMarketingWin":"pwnd editTargetListEmailMarketingWin"),
                mainTab:this.mainTab
            })
            this.mainTab.add(targetListTab);
        }
        this.mainTab.setActiveTab(targetListTab);
        this.mainTab.doLayout();
    }
});

Wtf.targetListWin = function (config){
    config.title = config.listname+"Target List";
    Wtf.apply(this, config);
    Wtf.targetListWin.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.targetListWin,Wtf.ux.ClosableTabPanel,{
    iconCls: "pwnd favwinIcon",
    closable:true,
    layout:'fit',
    onRender: function(config){
        this.toolItems = [];
/* save button */
           var saveButton = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.Save"),
                tooltip:{text:WtfGlobal.getLocaleText("hrms.CampaignDetail.Savethetargetlist")},
                scope:this,
                iconCls:"pwnd saveBtn",
                handler:this.saveTargetList_Targets
            });
            this.toolItems.push(saveButton);
/* import menu button*/
        var importArray = [];
        var addNewTargets = new Wtf.Action({
            text: WtfGlobal.getLocaleText("hrms.CampaignDetail.OpenTargets"),
            scope: this,
            flag:0,
            listid : this.listID,
            handler:this.addNewTargetHandler
        });
        var importLeads = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.Leads")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Leads")+'</span>',
            scope: this,
            flag:1,
            iconCls:"pwndCRM leadSearch",
            listid : this.listID,
            handler:this.importHandler
        });
        var importContacts = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.Contacts.tooltip")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Contacts")+'</span>',
            scope: this,
            flag:2,
            iconCls:"pwndCRM contactsTabIconSearch",
            listid : this.listID,
            handler:this.importHandler
        });
        var importUsers = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.Users.tooltip")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Users.tooltip")+'</span>',
            scope: this,
            flag:3,
            iconCls:"pwndCRM author",
            listid : this.listID,
            handler:this.importHandler
        });
        var importNewTargets = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetLists.tooltip")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetLists")+'</span>',
            scope: this,
            flag:4,
            iconCls:"pwndCRM targetlistButtonicon",
            listid : this.listID,
            handler:this.importHandler
        });
        
//        toolItems.push(addNewTargets);
        
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.exportt)) {
            importArray.push(importLeads);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.exportt)) {
            importArray.push(importContacts);
        }
        importArray.push(importUsers);
        importArray.push(importNewTargets);

        var importEmails = new Wtf.Toolbar.Button({
            tooltip: {text: WtfGlobal.getLocaleText("hrms.CampaignDetail.ImportEmailBtn")},
            scope: this,
            text:WtfGlobal.getLocaleText("hrms.common.Import"),
            iconCls:"pwnd importicon",
            menu: importArray
        });
        this.toolItems.push(importEmails);
/*end import buttons*/

 var createNewArray = [];

        var createNewLeads = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.AddanewLead")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Leads")+'</span>',
            scope: this,
            iconCls:"pwndCRM leadSearch",
            handler:function(){this.createNewTarget(1)}
        });

        var createNewContacts = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.AddanewContact")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Contacts")+'</span>',
            scope: this,
            iconCls:"pwndCRM contactsTabIconSearch",
            handler:function(){this.createNewTarget(2)}
        });
        var createNewTargets = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("hrms.CampaignDetail.AddanewTarget")+'>'+WtfGlobal.getLocaleText("hrms.CampaignDetail.Targets")+'</span>',
            scope: this,
            iconCls:"pwndCRM targetlistButtonicon",
            handler:function(){this.createNewTarget(4)}
        });

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.exportt)) {
            createNewArray.push(createNewLeads);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.exportt)) {
            createNewArray.push(createNewContacts);
        }
        createNewArray.push(createNewTargets);

        var createNewEmails = new Wtf.Toolbar.Button({
            tooltip: {text: WtfGlobal.getLocaleText("hrms.CampaignDetail.AddLeadsContactsTargetseasily")},
            scope: this,
            text:WtfGlobal.getLocaleText("hrms.common.add"),
            iconCls:"pwnd addIcon",
            menu: createNewArray
        });
        this.toolItems.push(createNewEmails);

        Wtf.targetListWin.superclass.onRender.call(this,config);
        this.activityform=new Wtf.form.FormPanel({
                autoScroll:true,
                border:false,
                height:100,
                items :{
                    layout: 'column',
                    border: false,
                    defaults: {border: false},
                    items: [{
                        columnWidth: 1,
                        items: [{
                            layout: 'form',
                            border:false,
                            defaultType: 'textfield',
                            labelWidth:150,
                            defaults: {
                                width: 250
                            },
                            items: [
                                this.name = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("hrms.common.name")+'*',
                                    allowBlank : false,
                                    maxLength:255,
                                    value : this.mode == 1 ? this.record.data.listname : ''
                                }),
                                this.desc = new Wtf.form.TextArea({
                                    fieldLabel: WtfGlobal.getLocaleText("hrms.performance.description"),
                                    maxLength:1024,
                                    value : this.mode == 1 ? this.record.data.description : ''
                                })
                            ]
                        }]
                    }]
                }
        });

        this.targetRecord = new Wtf.data.Record.create([
                {name:'id'},
                {name:'name'},
                {name:'emailid'},
                {name:'relatedto'},
                {name:'relatedid'},
                {name:'company'},
                {name:'targetscount'},
                {name:'targetlistDescription'}
        ]);
        
        this.targetReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.targetRecord);
        
        this.campTargetStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams:{
                listID:this.TLID,
                flag:7
            },
            method:'post',
            reader:this.targetReader
        });
        this.targetColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.Email"),
                dataIndex:'emailid'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.Remove"),
                dataIndex:'remove',
                renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                               return "<div class='pwnd deleteButton' > </div>";
                }
            }

            ]);
        this.campTargetStore.load({
            params:{
                start:0,
                limit:50
            }
            });
        this.targetGrid = new Wtf.grid.GridPanel({
            store: this.campTargetStore,
            cm: this.targetColumn,
            tbar: [],
            clicksToEdit:1,
            border : false,
            loadMask : true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.SearchbyName"),
            searchField:"name",
            view: new Wtf.ux.grid.BufferView({
                    scrollDelay: false,
                    autoFill: true,
                    forceFit:true,
                    emptyText:WtfGlobal.getLocaleText("hrms.CampaignDetail.CampaignTargetGrid.emptyText")
            }),
            bbar: new Wtf.PagingSearchToolbar({
                pageSize: 50,
                displayInfo: true,
                store: this.campTargetStore,
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                })
            })
        });
        
        this.targetGrid.on("cellclick", this.deleteTarget, this);
        this.targetGrid.on("render", this.gridAfterRender, this);
        this.mainPanel = new Wtf.Panel({
            layout :'border',
            border : false,
            items :[{
                layout :'fit',
                region : 'north',
                height : 150,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px 10px 10px 30px;',
                items : this.activityform
            },{
                layout :'fit',
                region : 'center',
                items : this.targetGrid
            }]
        });
        this.add(this.mainPanel);
    },

    gridAfterRender: function(){
        this.importTargetListA =Wtf.importMenuArray(this,WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetList"),this.campTargetStore,"undefined",this.pP.combo.value);
        this.importTargetList = Wtf.importMenuButtonA(this.importTargetListA,this,WtfGlobal.getLocaleText("hrms.common.Target"));

        this.toolItems.push(this.importTargetList);
        this.targetGrid.getTopToolbar().addButton(this.toolItems)
    },

    deleteTarget:function(grid, ri, ci, e) {
        var event = e;
        if(event.target.className == "pwnd deleteButton") {
             this.isClosable = false;
              Wtf.Ajax.requestEx({
                    url : Wtf.req.base + "campaign.jsp",
                    params:{
                        listid : this.TLID,
                        relatedid : grid.selModel.selections.items[0].data.relatedid,
                        flag : 35
                    }},this,
                    function(res,action){
                        ResponseAlert(52);
                        grid.store.load({params:{start:0, limit:this.pP.combo.value}});
                     },
                    function() {
                        ResponseAlert(53);
                });
        }
    }, 
    importHandler:function(butObj, event){
        var importID = "ImportEmails";
        var win = Wtf.getCmp(importID);
        if(win == null){
            win = new Wtf.importTargetWindow({
                id:importID,
                TLID: this.TLID,
                scope:this,
                butObj:butObj
            });
        }
        win.show();
    },
////////////////// Import CSV ////////////////////////////////////////////
    importCSVfile:function(type){
//
//  TODO Kuldeep Singh
//
//      this.TargetsArry = ["cuserid","cusername","cemailid","caddress","ccontactno"];
//        this.records =  Wtf.data.Record.create(this.TargetsArry);
//        this.jreader = new Wtf.data.KwlJsonReader({
//            root: 'data'
//        },this.records);
//        this.userds = new Wtf.data.Store({
//            url: Wtf.req.base + "contact.jsp",
//            root:'data',
//            reader:this.jreader,
//            baseParams : {
//                type: "alltargets",
//                userid: loginid
//            }
//        });
//        this.userdsFlag = true;
//        this.userds.on("load",function(){
//            var newresentry = new this.records({
//                cuserid: '-1',
//                cusername: 'Create New',
//                cemailid:""
//            });
//            this.userds.insert(0, newresentry);
//            this.userdsFlag = false;
//        },this);
//        this.userds.load();
//        this.userCombo = new Wtf.form.ComboBox({
//            store: this.userds,
//            displayField: 'cusername',
//            valueField: 'cusername',
//            typeAhead: true,
//            mode: 'local',
//            forceSelection: true,
//            emptyText: "Click to select",
//            editable: true,
//            triggerAction: 'all',
//            selectOnFocus: true
//        });
//        this.userCombo.on("select",function(combo,record,index){
//            if(record.data.cuserid == '-1')
//                this.CreateNewTarget();
//        },this);
//        this.userCombo.on("blur",function(comboBox){
//            var val = comboBox.lastQuery;
//            if(comboBox.store.find("cusername",comboBox.getValue())==-1||comboBox.getValue()=='Create New'){
//                comboBox.clearValue();
//                comboBox.setValue("");
//            }
//        });
//        this.listds = new Wtf.data.SimpleStore({
//            fields: [{
//                name:"userid"
//            },{
//                name:"username"
//            },{
//                name:"emailid"
//            },{
//                name:"address"
//            },{
//                name:"contactno"
//            },{
//                name:"cusername"
//            }]
//        });
//        this.listcm = new Wtf.grid.ColumnModel([{
//            header: "Imported Target User Name",
//            dataIndex: 'username',
//            renderer: this.displayName
//        },{
//            header: "Map With Crm User",
//            dataIndex: 'cusername',
//            editor: this.userCombo,
//            renderer : Wtf.comboBoxRenderer(this.userCombo)
//        }]);
//        this.grid= Wtf.commonConflictWindowGrid('list'+this.id,this.listds,this.listcm)
//
//        this.listcm1 = new Wtf.grid.ColumnModel([{
//            header: "Imported Target User Name",
//            dataIndex: 'username',
//            width:430,
//            renderer: this.displayName
//        }]);
//        this.grid1= Wtf.commonConflictWindowGrid('list1'+this.id,this.listds,this.listcm1)

        this.impWin1 = Wtf.commonUploadWindow(this,type);
        this.impWin1.show();
    },
    displayName:function(value,gridcell,record,d,e){
        var uname=(record.json.username).trim();
        return uname;
    },
    CreateNewTarget : function() {
        var rec = this.grid.getSelectionModel().getSelected();
        this.addExtTargetfunction(0,rec,1);
    },
    mappingCSV:function(Header,res,impWin1,delimiterType)
    {
        this.filename=res.FileName;

        this.mapCSV=new Wtf.csvMappingInterface({
            csvheaders:Header,
            modName:WtfGlobal.getLocaleText("hrms.common.Targets"),
            impWin1:impWin1,
            delimiterType:delimiterType
        }).show();
        Wtf.getCmp("csvMappingInterface").on('importfn',this.importCSVfunc, this);

    },
    mapImportedRes : function(conflictWin,repOrig,resConf) {
        var nrecords=0;
        var temp_h="";
        var nrows=this.listds.getCount();
        var jsonData = "{userdata:[";
        for(var cnt =0 ;cnt< nrows;cnt++) {
            var urec = this.listds.getAt(cnt);
            var ind=this.userds.find("cusername",urec.data.cusername,0,true);

            if(repOrig==true){
                var uName=urec.json.username.trim();
                ind=this.userds.find("cusername",uName,0,true);
            }

            if(ind!=-1){
                nrecords++;
                var crec=this.userds.getAt(ind);
                jsonData +=  temp_h+"{user:\""+crec.data.cusername+"\",email:\""+crec.json.cemailid+"\",targetModuleid:\""+crec.json.cuserid+"\",userid:\""+urec.json.userid+"\",username:\""+urec.json.username+"\",emailid:\""+urec.json.emailid+"\",address:\""+urec.json.address+"\",contactno:\""+urec.json.contactno+"\"}";
                temp_h=",";
            }
        }
       jsonData +="]}";
        if(nrecords>0){
            Wtf.Ajax.requestEx({
                url: Wtf.req.base + 'contact.jsp',
                params: ({
                    type:"repTarget",
                    val: jsonData
                }),
                method: 'POST'
            },
            this,
            function(result, req){
                if(result!=null && result != "")
                for(var i =0 ; i< result.rec.length ; i++){
                var newrec = new this.targetRecord({
                    id:"",
                    name:result.rec[i].username,
                    emailid:result.rec[i].emailid,
                    relatedto:"4",
                    relatedid:""

                });
                this.campTargetStore.add(newrec);
              }
                WtfComMsgBox(458, 0);
                conflictWin.close();

            },function(){
               conflictWin.close();
            });
        }
        else{
            WtfComMsgBox(470, 0);
            conflictWin.close();
        }
    },
    importCSVfunc:function(response,delimiterType)
    {
          Wtf.commonConflictWindow(this,response,WtfGlobal.getLocaleText("hrms.common.Target"),this.filename,this.EditorStore,this.listds,this.grid,469,451,'../../images/leads.gif',"Null","Null",this.grid1,470,delimiterType,this.TLID);
          this.on('importrecs',this.insertIntoGrid, this);
    },
    insertIntoGrid:function(insertRecs,data){
            if(insertRecs[0].TLID !== undefined)
                this.TLID = insertRecs[0].TLID;
          this.campTargetStore.baseParams.listID = this.TLID;
          this.campTargetStore.load({params:{start:0, limit:this.pP.combo.value}});
    },
    addExtTargetfunction:function(action,record,flag){
        var windowHeading = action==0?WtfGlobal.getLocaleText("hrms.CampaignDetail.AddTarget"):WtfGlobal.getLocaleText("hrms.CampaignDetail.EditTarget");
        var windowMsg = action==0?WtfGlobal.getLocaleText("hrms.CampaignDetail.EnternewTargetdetails"):WtfGlobal.getLocaleText("hrms.CampaignDetail.EditexistingTargetdetails");
        this.addExtTargetWindow = new Wtf.Window({
            title : action==0?WtfGlobal.getLocaleText("hrms.CampaignDetail.AddTarget"):WtfGlobal.getLocaleText("hrms.CampaignDetail.EditTarget"),
            closable : true,
            modal : true,
            iconCls : 'pwnd favwinIcon',
            width : 430,
            height: 370,
            resizable :false,
            buttons :[{
                text : action==0?WtfGlobal.getLocaleText("hrms.common.add"):WtfGlobal.getLocaleText("hrms.common.edit"),
                id: "createUserButton",
                scope : this,
                handler:function(){
                    if(this.createuserForm.form.isValid()){
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + "contact.jsp",
                            params: ({
                                type:"newTargetAddress",
                                userid:Wtf.getCmp('tempContIdField').getValue(),
                                username:Wtf.getCmp('tempNameField').getValue(),
                                emailid:Wtf.getCmp('tempEmailField').getValue(),
                                address: Wtf.getCmp('tempAddField').getValue(),
                                contactno:Wtf.getCmp('tempPhoneField').getValue()
                            }),
                            method: 'POST'
                        },
                        this,
                        function(result, req){
                            if(result!=null && result != ""){
                                WtfComMsgBox(453, 0);
                            }
                            this.listds.remove(record);
                            var newrec = new this.targetRecord({
                                id:"",
                                name:Wtf.getCmp('tempNameField').getValue(),
                                emailid:Wtf.getCmp('tempEmailField').getValue(),
                                relatedto:"4",
                                relatedid:""

                            });
                            this.campTargetStore.add(newrec);
                            this.addExtTargetWindow.close();
                         },function(){
                            this.addExtTargetWindow.close();
                        });
                    }
                }
            },{
                text : WtfGlobal.getLocaleText("hrms.common.cancel"),
                id:'cancelCreateUserButton',
                scope : this,
                handler : function(){
                    this.addExtTargetWindow.close();
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                id: "userwinnorth",
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html :  getTopHtml(windowHeading,windowMsg)
            },{
                region : 'center',
                border : false,
                id : 'userwincenter',
                bodyStyle : 'background:#f1f1f1;font-size:10px;',
                layout : 'fit',
                items :[this.createuserForm = new Wtf.form.FormPanel({
                    border : false,
                    labelWidth: 120,
                    bodyStyle : 'margin-top:20px;margin-left:35px;font-size:10px;',
                    defaults: {
                        width: 200
                    },
                    defaultType: 'textfield',
                    items: [{
                        fieldLabel: WtfGlobal.getLocaleText("hrms.common.name")+'*',
                        id:'tempNameField',
                        name:'name',
                        validator:WtfGlobal.validateUserName,
                        allowBlank:false
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("hrms.common.email.id")+'*',
                        id:'tempEmailField',
                        name: 'emailid',
                        validator: WtfGlobal.validateEmail,
                        allowBlank:false,
                        renderer: WtfGlobal.renderEmailTo
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("hrms.common.Phone")+'*',
                        allowBlank:false,
                        id: "tempPhoneField",
                        name: 'phone'
                    },{
                        xtype:"textarea",
                        fieldLabel:  WtfGlobal.getLocaleText("hrms.common.address"),
                        id: "tempAddField",
                        name: 'address'
                    },{
                        xtype:"hidden",
                        id: "tempContIdField",
                        name: 'id'
                    }]
                })]
            }]
        });
        Wtf.getCmp('tempPhoneField').on("change", function(){
            Wtf.getCmp('tempPhoneField').setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('tempPhoneField').getValue()));
        }, this);
        Wtf.getCmp('tempAddField').on("change", function(){
            Wtf.getCmp('tempAddField').setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('tempAddField').getValue()));
        }, this);
        this.addExtTargetWindow.show();
        if(record!=null){
            Wtf.getCmp('tempNameField').setValue(record.json.username);
            Wtf.getCmp('tempEmailField').setValue(record.json.emailid);
            Wtf.getCmp('tempPhoneField').setValue(record.json.contactno);
            Wtf.getCmp('tempAddField').setValue(record.json.address);
            Wtf.getCmp('tempContIdField').setValue(record.json.userid);
        }
    },
    // TODO - create new targets 
    addNewTargetHandler:function(){
        addTargetModuleTab();
    },

    saveTargetList_Targets : function() {
        var targetdata = '';
        if(this.campTargetStore.getCount()<1){
           ResponseAlert(73);
           return ;
        }
        if(this.name.getValue()==""){
            ResponseAlert(63);
            return ;
        }
        Wtf.Ajax.requestEx({
            url : Wtf.req.base + "campaign.jsp",
            params:{
                targets : targetdata,
                listid : this.TLID,
                mode : this.mode,
                name : this.name.getValue(),
                desc : this.desc.getValue(),
                flag : 8
            }},this,
            function(){
               this.isClosable = true;
               this.fireEvent("close");
               WtfComMsgBox([WtfGlobal.getLocaleText("hrms.common.Target"),WtfGlobal.getLocaleText("hrms.CampaignDetail.Targetlistsavedsuccessfully")],0);
            },
            function() {
               WtfComMsgBox([WtfGlobal.getLocaleText("hrms.common.Target"),WtfGlobal.getLocaleText("hrms.CampaignDetail.FailedtosaveTargetlist")],1);
        })
 
        var targetListPanel=Wtf.getCmp(this.id);
        if(this.addNewDashboardCall){
            mainPanel.remove(targetListPanel);
            mainPanel.doLayout();
        } else {
            this.mainTab.remove(targetListPanel);
            this.mainTab.doLayout();
        }
        if(this.store)this.store.load();
        },
    createNewTarget:function(flag){

        this.relatedToMod ="";
        var subTitle ="";
        var title="";
        var imgPath="";
        if(flag==1){
            this.relatedToMod ="1";
            title=WtfGlobal.getLocaleText("hrms.CampaignDetail.AddanewLead");
            subTitle=WtfGlobal.getLocaleText("hrms.CampaignDetail.ProvideinformationtoaddLead");
            imgPath="../../images/leads.gif";
        } else if(flag==2){
            this.relatedToMod ="2";
            title=WtfGlobal.getLocaleText("hrms.CampaignDetail.AddanewContact");
            subTitle=WtfGlobal.getLocaleText("hrms.CampaignDetail.ProvideinformationtoaddContact");
            imgPath="../../images/contacts3.gif";
        } else if(flag==4){
            this.relatedToMod ="4";
            title=WtfGlobal.getLocaleText("hrms.CampaignDetail.AddanewTarget");
            subTitle=WtfGlobal.getLocaleText("hrms.CampaignDetail.ProvideinformationtoaddTarget");
            imgPath="../../images/createuser.png";
        }

        if(flag==2){
            this.form1=new Wtf.form.FormPanel({
                border:false,
                items:[
                new Wtf.form.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("hrms.common.name"),
                    id:'target_name'+this.id,
                    allowBlank:false,
                    msgTarget:'side',
                    width:200
                }),{
                    fieldLabel:WtfGlobal.getLocaleText("hrms.common.email.id"),
                    id:'target_email_id'+this.id,
                    width:200,
                    allowBlank:false,
                    msgTarget:'side',
                    vtype:"email",
                    xtype:'striptextfield'
                }]
            });
        } else {
            this.form1=new Wtf.form.FormPanel({
                border:false,
                items:[
                new Wtf.form.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("hrms.common.name"),
                    id:'target_name'+this.id,
                    allowBlank:false,
                    msgTarget:'side',
                    width:200
                }),{
                    fieldLabel:WtfGlobal.getLocaleText("hrms.common.email.id"),
                    id:'target_email_id'+this.id,
                    width:200,
                    allowBlank:false,
                    msgTarget:'side',
                    vtype:"email",
                    xtype:'striptextfield'
                },new Wtf.form.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("hrms.common.Company"),
                    id:'target_company'+this.id,
                    allowBlank:false,
                    msgTarget:'side',
                    width:200
                })]
            });
        }
        this.impWin1 = new Wtf.Window({
            resizable: false,
            scope: this,
            layout: 'border',
            modal:true,
            width: 380,
            height: flag==2?250:280,
            iconCls: 'pwnd favwinIcon',
            id: 'create_new_target_window',
            title: WtfGlobal.getLocaleText("hrms.CampaignDetail.NewTarget"),
            items: [
            {
                region:'north',
                height:80,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(""+title+"", ""+subTitle+"",""+imgPath+"")
            },{
                region:'center',
                layout:'fit',
                border:false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                items:this.form1
            }
            ],
            buttons: [{
                text: WtfGlobal.getLocaleText("hrms.common.submit"),
                type: 'submit',
                scope: this,
                handler: function(){
                    var targetName=Wtf.getCmp('target_name'+this.id).getValue();
                    var targetEmail=Wtf.getCmp('target_email_id'+this.id).getValue();
                    var company="";
                    if(flag==2){
                        if(targetEmail.trim()==""|| targetName.trim()==""){
                            ResponseAlert(152)
                            return;
                        }
                        this.saveContact(targetName,targetEmail);
                    } else{
                        company =Wtf.getCmp('target_company'+this.id).getValue();
                        if(targetEmail.trim()==""|| targetName.trim()==""||company.trim()==""){
                            ResponseAlert(152)
                            return;
                        }
                        if(flag==1){
                            this.saveLead(targetName,targetEmail,company);
                        }else {
                            this.saveTarget(targetName,targetEmail,company);
                        }
                    }
                     
                    this.impWin1.close();
                }
            },{
                text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope:this,
                handler:function() {
                    this.impWin1.close();
                }
            }]
        });
        this.impWin1.show();

    },saveLead:function(name,email,comp){
        var splitName = name.split(" ");
        var lname="";
        var fname="";
        if(splitName.length==1){
            lname=name;
        }else{
            fname = splitName[0];
            lname=splitName[1]
        }
        var leadid='0';
        var leadownerid=loginid;
        var jsondata = "";
        var validFlag=1;
        var title="";
        var leadstatusid="";
        var phone="";
        var ratingid="";
        var leadsourceid="";
        var industryid="";
        var street="";

        jsondata+="{'leadid':'" + leadid + "',";
        jsondata+="'leadownerid':'" +leadownerid + "',";
        jsondata+="'firstname':'" +fname + "',";
        jsondata+="'lastname':'" +lname+ "',";
        jsondata+="'validflag':'" +validFlag+ "',";
        jsondata+="'title':'" +title + "',";
        jsondata+="'phone':'" + phone+ "',";
        jsondata+="'leadstatusid':'" +leadstatusid + "',";
        jsondata+="'email':'" + email + "',";
        jsondata+="'street':'" +street + "',";
        jsondata+="'ratingid':'" +ratingid + "',";
        jsondata+="'industryid':'" +industryid+ "',";
        jsondata+="'leadsourceid':'" + leadsourceid + "',";
        jsondata+="'activities':'',";
        jsondata+="'productid': '', ";
        jsondata+="'price': '', ";
        jsondata+="'revenue': '', ";
        jsondata+="'moredetails': '', ";
        jsondata+="'company':'" +comp +"'},";
        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);
        this.saveRecordReq(finalStr,{flag:20,auditEntry:1},1);

           
       
    },saveContact:function(name,email) {
        var splitName = name.split(" ");
        var lname="";
        var fname="";
        if(splitName.length==1){
            lname=name;
        }else{
            fname = splitName[0];
            lname=splitName[1]
        }
        var contactid='0';
        var contactownerid=loginid;
        var jsondata = "";
        var validFlag=1;
        
        jsondata+="{'contactid':'" + contactid + "',";
        jsondata+="'contactownerid':'" +contactownerid+ "',";
        jsondata+="'firstname':'" +fname+ "',";
        jsondata+="'lastname':'" +lname+ "',";

        jsondata+="'accountid':'',";
        jsondata+="'phone':'',";
        jsondata+="'mobile':'',";
        jsondata+="'email':'"+email+"',";
        jsondata+="'industryid':'',";
        jsondata+="'leadsourceid':'',";
        jsondata+="'title':'',";
        jsondata+="'street':'',";
        jsondata+="'createdon':'',";
        jsondata+="'validflag':'" +validFlag+ "',";
        jsondata+="'activities':'',";
        jsondata+="'description':''},";

        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);
        this.saveRecordReq(finalStr,{flag:22,auditEntry:1},2);
       
    },saveTarget:function(name,email,company){
        var splitName = name.split(" ");
        var lname="";
        var fname="";
        if(splitName.length==1){
            lname=name;
        }else{
            fname = splitName[0];
            lname=splitName[1]
        }
        var targetid='0';
        var targetownerid=loginid;
        var jsondata = "";
        var validFlag=1;
  
        jsondata+='{"targetModuleid":"' + targetid + '",';
        jsondata+='"targetModuleownerid":"' +targetownerid+ '",';
        jsondata+='"firstname":"' +fname+ '",';
        jsondata+='"lastname":"' +lname+ '",';
        jsondata+='"company":"' +company+ '",';
        jsondata+='"auditstr":"",';
        jsondata+='"phone":"",';
        jsondata+='"mobile":"",';
        jsondata+='"email":"'+email+'",';
        jsondata+='"address":"",';
        jsondata+='"validflag":"' +validFlag+ '",';
        jsondata+='"description":""},';

        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);
        this.saveRecordReq(finalStr,{flag:301,auditEntry:1},3);

    },
    saveRecordReq : function (jsondata,paramObj,actionCode) {
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.Savingdata"));
        var jsonData= eval( '(' + jsondata+ ')');
        paramObj['jsondata'] = jsondata;
        paramObj['type'] = 1;
        paramObj['TLID'] = this.TLID;
        var recID="";
        Wtf.Ajax.requestEx({
            url:Wtf.req.base +'crm.jsp',    
            params:paramObj
        },this,
        function(res) {
            if(res.TLID)
                this.TLID = res.TLID;
            Wtf.updateProgress();
            this.campTargetStore.baseParams.listID = this.TLID;
            this.campTargetStore.load({params:{start:0, limit:this.pP.combo.value}});
        },
        function(res){
                    WtfComMsgBox(152,1);
        })
    }
});

Wtf.importTargetWindow = function (config){
    config.title = WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.Importaction",params:[config.butObj.text]});
    Wtf.apply(this, config);
    Wtf.importTargetWindow.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.importTargetWindow,Wtf.Window,{
    iconCls : "pwnd favwinIcon",
    layout : 'fit',
    modal:true,
    resizable:false,
    height : 500,
    width : 600,
    onRender: function(config){
        Wtf.importTargetWindow.superclass.onRender.call(this,config);
        this.importTargetStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams:{
                importID:this.butObj.flag,
                tlid : this.TLID,
                flag:20
            },
            method:'post',
            reader:this.scope.targetReader
        });
        var checkBoxSM =  new Wtf.grid.CheckboxSelectionModel({
            singleSelect: this.butObj.flag=="4"?true:false
        });
        this.colModel = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
                checkBoxSM,
            {
                header:WtfGlobal.getLocaleText("hrms.common.name"),
                sortable:true,
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.Email"),
                sortable:true,
                hidden:this.butObj.flag=="4"?true:false,
                dataIndex:'emailid'
            },{
                header:WtfGlobal.getLocaleText("hrms.CampaignDetail.NoofTargets"),
                sortable:true,
                width:50,
                hidden:this.butObj.flag=="4"?false:true,
                dataIndex:'targetscount'
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.description"),
                sortable:true,
                hidden:this.butObj.flag=="4"?false:true,
                dataIndex:'targetlistDescription',
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";
                }
        }]);

        this.quickPanelSearch  = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: WtfGlobal.getLocaleText("hrms.CampaignDetail.SearchbyName"),
            Store:this.importTargetStore
        });
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            searchField: this.quickPanelSearch,
            store: this.importTargetStore,
            displayInfo: true,
            plugins: this.pP = new Wtf.common.pPageSize()
        });

        this.importGrid = new Wtf.grid.GridPanel({
            store: this.importTargetStore,
            cm: this.colModel,
            sm : checkBoxSM,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            },
            tbar:['-',this.quickPanelSearch,'-'],
            bbar:this.pg
        });
        this.importTargetStore.load({
            params:{
                start:0,
                limit:this.importGrid.getBottomToolbar().pageSize
            }
        });

        this.importTargetStore.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
        }, this);
        this.importTargetStore.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);
        
        this.mainPanel = new Wtf.Panel({
            layout :'border',
            border : false,
            buttons: [{
                text:WtfGlobal.getLocaleText("hrms.common.submit"),
                scope:this,
                handler:this.addEmailsToGrid
            },{
                text: WtfGlobal.getLocaleText("hrms.common.Close"),
                scope:this,
                handler:function() {
                    this.close()
                }
            }],
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.Importaction",params:[this.butObj.text]}), WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.SelectactionandClicksubmitbtn",params:[this.butObj.text]}), "../../images/import.png")
            },{
                layout :'fit',
                region : 'center',
                items : this.importGrid
            }]
        });

        this.add(this.mainPanel);
    },
    addEmailsToGrid:function(){
        if(this.butObj.flag=="4") { // for target list
            var rec = this.importGrid.getSelectionModel().getSelected().data;
            Wtf.Ajax.requestEx({
                    url : Wtf.req.base + "campaign.jsp",
                    params:{
                        importtl : rec.relatedid,
                        listid : this.scope.TLID,
                        flag : 30
                    }},this,
                    function(res,action){
                        if(res.TLID)
                            this.scope.TLID = res.TLID;
                        Wtf.updateProgress();
                        this.scope.isClosable = false;
                        var targetStore = this.scope.targetGrid.getStore();
                        targetStore.baseParams.listID = this.scope.TLID;
                        targetStore.load({params:{start:0, limit:this.scope.pP.combo.value}});
                        Wtf.MessageBox.show({
                            title: WtfGlobal.getLocaleText("hrms.common.Import"),
                            msg: WtfGlobal.getLocaleText("hrms.CampaignDetail.TargetListImportedSuccess"),
                            buttons: Wtf.MessageBox.YESNO,
                            animEl: 'mb9',
                            scope:this,
                            icon: Wtf.MessageBox.INFO,
                            fn:function(btn,text){
                                if(btn=="yes"){

                                } else {
                                    this.close();
                                }
                            }
                        });
                     },
                    function() {
                });
        } else {
            if(this.importGrid.getSelectionModel().hasSelection()) {
                var jdata = "[";
                var rto = "";
                var sel = this.importGrid.getSelectionModel().getSelections();
                for(var i =0 ; i < sel.length ;i++ ){
                     var ID = sel[i].get("relatedid");
                     jdata+="{\"rid\":\""+ID+"\"},";
                     if(i==0)
                         rto = sel[i].get("relatedto");
                }
                jdata = jdata.substr(0, jdata.length-1);
                jdata += "]";
                Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.CampaignDetail.ImportingData"));
                Wtf.Ajax.requestEx({
                    url : Wtf.req.base + "campaign.jsp",
                    params:{
                        data : jdata,
                        listid : this.scope.TLID,
                        relatedto : rto,
                        flag : 29
                    }},this,
                    function(res,action){
                        if(res.TLID)
                            this.scope.TLID = res.TLID;
                        Wtf.updateProgress();
                        this.scope.isClosable = false;
                        var targetStore = this.scope.targetGrid.getStore();
                        targetStore.baseParams.listID = this.scope.TLID;
                        targetStore.load({params:{start:0, limit:this.scope.pP.combo.value}});
                        Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("hrms.common.Import"),
                            msg: WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.nRecImportedSuccess",params:[sel.length]}),
                            buttons: Wtf.MessageBox.YESNO,
                            animEl: 'mb9',
                            scope:this,
                            icon: Wtf.MessageBox.INFO,
                            fn:function(btn,text){
                                if(btn=="yes"){
                                  
                                } else {
                                    this.close();
                                }
                            }
                        });
                     },
                    function() {
                });
            }
        }

    }
});
