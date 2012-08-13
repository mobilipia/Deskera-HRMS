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
Wtf.selectTempWin=function(config){
    Wtf.apply(this,config);
    back =this;
    var templateRec = Wtf.data.Record.create([
        {
            name: 'tempid',
            mapping:'tempid'
        },{
            name: 'tempname',
            mapping:'tempname'
        },{
            name: 'description',
            mapping:'description'
        },{
            name: 'configstr',
            mapping:'configstr'
        }
        ]);

    var template_ds = new Wtf.data.Store({
//        url: Wtf.req.base + 'template.jsp?&action=1',
        url:  "Common/ExportPdfTemplate/getAllReportTemplate.common",
        method: 'GET',
        reader: new Wtf.data.KwlJsonReader({
            root: 'data'
        },templateRec)
    });
    var sm=new Wtf.grid.RowSelectionModel({
        singleSelect:true
    });
    var namePanel = new Wtf.grid.GridPanel({
        id:'templateName',
        autoScroll: true,
        enableColumnResize:false,
        border:false,
        viewConfig:{
            forceFit:true
        },
        cm: new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer, {
                header:WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex: 'tempname'
            }]),
        ds: template_ds,
        sm:sm,
        height:180
    });

    namePanel.on('cellclick',function(gridObj, ri, ci, e){
        var config = gridObj.getStore().getAt(ri).data['configstr'];
        this.templateid = gridObj.getStore().getAt(ri).data['tempid'];
        var configstr = eval('('+config+')');
        var title = configstr["title"];
        var subtitle =configstr["subtitles"];
        var starr = subtitle.split("~");
        var subtitles = "";
        for(var i=0;i< starr.length;i++)
            subtitles += "<div>"+starr[i]+"</div>";

        var textColor = "#"+configstr["textColor"];
        var bgColor ="#"+configstr["bgColor"];

        var headdate = configstr["headDate"]=="true"?"<small>2009/01/01</small>":"";
        var footdate = configstr["footDate"]=="true"?"<small>2009/01/01</small>":"";

        var headnote = configstr["headNote"];
        var footnote = configstr["footNote"];

        var headpager = configstr["headPager"]=="true"?"1":"";
        var footpager = configstr["footPager"]=="true"?"1":"";

        var pageborder = configstr["pageBorder"]=="true"?"border:thin solid #666;":"";
        var gridborder = configstr["gridBorder"]=="true"?"1":"0";
        var displaylogo = configstr["showLogo"]=="true"?"block":"none";

        var pagelayoutPR = "height:380px;width:270px;margin:auto;";
        var pagelayoutLS = "height:270px;width:380px;margin:57px auto;";
        var pagelayout = configstr["landscape"]=="true"?pagelayoutLS:pagelayoutPR;

        var reportPreview = "<div style=\""+pagelayout+"align:center;color:"+textColor+";font-family:arial;padding:5px;font-size:12px;background:"+bgColor+";border-right:4px solid #DDD;border-bottom:4px solid #888\">" +
        "<div style=\""+pageborder+"height:99%;width:99%;\">" +
        "<div style=\"border-bottom:thin solid #666;margin:0 2px;height:6%;width:98%;\">" +
        "<table border=0 width=100% style=\"font-size:12px\">" +
        "<tr><td align=\"left\" width=25%>"+headdate+"</td><td align=\"center\" >"+headnote+"</td><td align=\"right\" width=25%>"+headpager+"</td></tr>" +
        "</table>" +
        "</div>" +
        "<div style=\"margin:0 2px;height:86%;width:98%;text-align:center;overflow:hidden;\">" +
        "<div style=\"border-bottom:thin solid #666;\">" +
        "<div style=\"display:"+displaylogo+";position:absolute;font-size:16px;margin:1px 0 0 1px\"><b>Deskera</b></div>" +
        "<div style=\"display:"+displaylogo+";position:absolute;color:#8080FF;font-size:16px\"><b>Deskera</b><sup><small><small><small>TM</small></small></small></sup></div>" +
        "<br/><div style=\"font-size:13px\"><b>"+title+"</b></div>" +
        subtitles + "<br/>"+
        "</div>" +
        "<table border="+gridborder+" width=90% cellspacing=0 style=\"font-size:12px;margin:5px auto;\">" +
        "<tr><td align=\"center\" width=10%><b>No.</b></td><td align=\"center\" width=20%><b>Index</b></td><td align=\"center\" width=45%><b>Task Name</b></td><td align=\"right\" width=25%><b>Resources</b></td></tr>" +
        "<tr><td align=\"center\">1.</td><td align=\"center\">31</td><td align=\"center\">Gather info.</td><td align=\"right\" >Thomas</td></tr>" +
        "<tr><td align=\"center\">2.</td><td align=\"center\">56</td><td align=\"center\">Documentation</td><td align=\"right\" >Jane,Alice</td></tr>" +
        "<tr><td align=\"center\">3.</td><td align=\"center\">78</td><td align=\"center\">Planning</td><td align=\"right\" >Darin</td></tr>" +
        "<tr><td align=\"center\">4.</td><td align=\"center\">90</td><td align=\"center\">Coding</td><td align=\"right\" >John</td></tr>" +
        "<tr><td align=\"center\">5.</td><td align=\"center\">111</td><td align=\"center\">Implemention</td><td align=\"right\">John</td></tr>" +
        "<tr><td align=\"center\">6.</td><td align=\"center\">112</td><td align=\"center\">Submission</td><td align=\"right\">John</td></tr>" +
        "</table>" +
        "</div>" +
        "<div style=\"border-top:thin solid #666;margin:0 2px;height:6%;width:98%;\">" +
        "<table border=0 width=100% style=\"font-size:12px\">" +
        "<tr><td align=\"left\" width=25%>"+footdate+"</td><td align=\"center\" >"+footnote+"</td><td align=\"right\" width=25%>"+footpager+"</td></tr>" +
        "</table>" +
        "</div>" +
        "</div>" +
        "</div>";


        var reportTmp = new Wtf.Template(reportPreview);
        reportTmp.overwrite(Wtf.getCmp("layoutpreview").body);
        back.smTmp = namePanel.getSelectionModel();
        back.configstr=back.smTmp.getSelected().data['configstr'];
        
    },this);
       
    template_ds.load();
    
    var templatePanel = new Wtf.Panel({
        id:'templatePanel',
        layout:'border',
        border:false,
        width:500,
        items:[{
            region:'center',
            width:'50%',
            border:false,
            layout:'fit',
            height:'100%',
            items:[namePanel]
        },{
            region:'east',
            width:410,
            border:false,
            layout: 'fit',
            height:'100%',
            bodyStyle:"background:#EEEEEE",
            items:[{
                layout:'fit',
                xtype:'fieldset',
                cls: 'textAreaDiv',
                preventScrollbars:false,
                frame:true,
                border:false,
                id:'layoutpreview',
                html:"<div style='font-size:14px;margin-top:175px;text-align:center;'>"+WtfGlobal.getLocaleText("hrms.export.Selectatemplatetoviewitspreview")+"</div>"
            }]
        }]
    });

    //var configstr="";

    this.templateWindow = new Wtf.Window({
        title: WtfGlobal.getLocaleText("hrms.export.ExistingReportTemplates"),
        modal:true,
        iconCls: getButtonIconCls(Wtf.btype.winicon),
        layout:'fit',
        items:[templatePanel],
        resizable:true,
        autoDestroy:true,
        height:600,
        width:600,
        buttons:[{
            text:WtfGlobal.getLocaleText("hrms.export.SelectColumns"),
            scope:this,
            handler:function() {
                var smTmpcheck = namePanel.getSelectionModel();
                if(smTmpcheck.getCount()<1){
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.export.Pleaseselectonetemplate")], 1);
                    return;
                } else {
                    this.templateWindow.hide();
                    var expt =new Wtf.ExportInterface({
                        type:this.type,
                        mode:this.mode,
                        get:this.get,
                        url:this.url,
//                        filename:Wtf.getCmp('as').getActiveTab().title,
                        filename:this.filename,
                        parent:this.templateWindow,
                        gridConfig:this.gridConfig,
                        cd:1,
                        json:this.json,
                        fromdate:this.fromdate,
                        todate:this.todate,
                        pdfDs:this.storeToload,
                        obj:this.obj,
                        configstr:back.configstr
                    });
                    expt.show();
                }
            }
        },{
            text:WtfGlobal.getLocaleText("hrms.export.Export"),
            scope:this,
            handler:function() {
                var smTmp = namePanel.getSelectionModel();
                if(smTmp.getCount()<1){
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.export.Pleaseselectonetemplate")], 1);
                    return;
                } else {
                    if(this.get == 3){
                           var expt =new Wtf.ExportInterface({
                                type:this.type,
                                mode:this.mode,
                                get:this.get,
                                url:this.url,
        //                        filename:Wtf.getCmp('as').getActiveTab().title,
                                filename:this.filename,
                                parent:this.templateWindow,
                                gridConfig:this.gridConfig,
                                cd:1,
                                json:this.json,
                                fromdate:this.fromdate,
                                todate:this.todate,
                                pdfDs:this.storeToload,
                                obj:this.obj,
                                configstr:back.configstr
                            });
                            expt.show();
//                        configstr=smTmp.getSelected().data['configstr'];
//                        var url =this.url+"?"+this.mode+"&config="+configstr+"&name="+this.filename+"&filetype="+ this.type
//                             +"&get="+this.get+"&gridconfig="+encodeURIComponent(this.gridConfig)+"";
                    }else{
                        var params=this.obj.grid.getStore().lastOptions;
                        var startdate=(params.params!=undefined?params.params.startdate:undefined);
                        var enddate=(params.params!=undefined?params.params.enddate:undefined);
                        var empid=(params.params!=undefined?params.params.empid:undefined);
                        configstr=smTmp.getSelected().data['configstr'];
    //                    var url ="../../export.jsp?"+this.mode+"&config="+configstr+"&filename="+this.filename+"&filetype="+ this.type
                        var url =this.url+"?"+this.mode+"&config="+configstr+"&name="+this.filename+"&filetype="+ this.type
                                 +"&get="+this.get+"&gridconfig="+encodeURIComponent(this.gridConfig)+"&startdate="+startdate+"&enddate="+enddate+"&empid="+empid+"&exportFile="+true;
                        Wtf.get('downloadframe').dom.src = url;
                    }
                    this.templateWindow.hide();
                }
                this.templateWindow.hide();
            },
            scope: this
        },{
             text: WtfGlobal.getLocaleText("hrms.common.CreateNew"),
             handler:function() {
                var custForm=new Wtf.customReport({
                    autoScroll: true,
                    border: false,
                    width:'99%',
                    bodyStyle : 'background:white;',
                    id:'custForm'+this.id + this.tabtitle,
                    reportGrid:this.grid,
                    type:this.type,
                    mode:this.mode,
                    get:this.get,
                    filename:this.filename,
                    url:this.url,
                    gridconfig:this.gridConfig,
                    cd:this.cd,
                    obj:this.obj,
                    reportType:1
                });
                var eobj = Wtf.getCmp(this.id + "_buildReport"+ this.tabtitle);
                if(eobj === undefined){
                    eobj = new Wtf.reportBuilder.builderPanel({
                        title: WtfGlobal.getLocaleText("hrms.export.BuildReportLayout"),
                        iconCls:"pwndExport template_builder",
                        id: this.id + "_buildReport" + this.tabtitle,
                        closable: true,
                        autoScroll: true,
                        formCont: custForm
                    });
                    mainPanel.add(eobj);
                }
                this.templateWindow.close();
                mainPanel.setActiveTab(eobj);
                mainPanel.doLayout();
            },
            scope: this
        },{
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            handler:function(){
                var smTmp = namePanel.getSelectionModel();
                if(smTmp.getCount()<1){
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.export.Pleaseselecttemplate")],2);
                    return;
                } else {
                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.export.Areyousuretodeletetheselectedtemplate"),function(btn) {
                        if(btn =='yes') {
                            Wtf.Ajax.requestEx({
                                //url: Wtf.req.base + 'template.jsp',
                                url:  "Common/ExportPdfTemplate/deleteReportTemplate.common",
                                params: {
                                    action: 2,
                                    deleteflag:this.templateid
                                },
                                method:'POST'
                            },
                            this,
                            function(res) {
                               // if(res.success)
                                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.export.Thetemplatedeletedsuccessfully")],1);
                                    template_ds.reload();
                            },
                            function() {
                                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.export.CouldnotdeletetemplatePleasetryagain")],2);
                            });
                          //  this.templateWindow.close();
                        }
                    },this);
                }
            },
            scope:this
        },
        {
            text:WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler:function() {
                this.templateWindow.close();
            },
            scope: this
        }]
    });
    this.templateWindow.show();

    Wtf.selectTempWin.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.selectTempWin,Wtf.Window,{
    onRender: function(conf){
        Wtf.selectTempWin.superclass.onRender.call(this, conf);
        this.add(this.templateWindow);
    }
});
