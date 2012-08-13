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
Wtf.customReport=function(config){
    Wtf.apply(this,config);
    Wtf.form.Field.prototype.msgTarget = "under";
    var defConf = {
        ctCls: 'reportfieldContainer',
        labelStyle: 'font-size:11px; text-align:right;'
    };
    this.attachheight=130;
    this.hfheight=150;
    this.subtitle = new Wtf.Panel({
        bodyStyle : 'margin-bottom:3px;padding-left:105px;',
        border:false,
        html:"<a tabindex=3 id = 'subtitlelink"+this.id+"'class='attachmentlink' href=\"#\" onclick=\"Addsubtitle(\'"+this.id+"\')\">"+WtfGlobal.getLocaleText("hrms.export.AddSubtitle")+"</a>"
    });
    this.count=1;
    this.hfieldset=new Wtf.Panel({
        columnWidth: 0.59,
        border: false,
        height : this.attachheight,
        items:[{
            xtype:'fieldset',
            title: WtfGlobal.getLocaleText("hrms.export.HeaderFields"),
            cls: "customFieldSet",
            defaults : defConf,
            autoHeight : true,
            items:[
            {
                xtype : 'textfield',
                fieldLabel:WtfGlobal.getLocaleText("hrms.export.HeaderNote"),
                labelSeparator:'',
                tabIndex:1,
                maxLength:40,
                //validator:WtfGlobal.validateHTField,
                maxLengthText:WtfGlobal.getLocaleText("hrms.export.MaxLengthText40"),
                emptyText:WtfGlobal.getLocaleText("hrms.export.InsertNote")
            },{
                xtype : 'textfield',
                fieldLabel:WtfGlobal.getLocaleText("hrms.export.ReportTitle"),
                labelSeparator:'',
                maxLength:40,
                tabIndex:2,
                //validator:WtfGlobal.validateHTField,
                maxLengthText:WtfGlobal.getLocaleText("hrms.export.MaxLengthText40"),
                emptyText:WtfGlobal.getLocaleText("hrms.export.InsertTitle")
            }]
            },this.subtitle
        ]
    });
    var bgvalue="#FFFFFF";
    var tvalue="#000000";
    this.bclrPicker=new Wtf.Panel({
        border:false,
        html:' <div tabindex=24 id = "bimg_div'+this.id+'" style="cursor:pointer; height:12px; width:12px; margin:auto; padding:auto; border:thin solid; border-color:'+tvalue
                +'; background-color:'+bgvalue+';" onclick=\"showPaletteBg(\''+this.id+'\')\" onkeypress=\"javascript:if(event.keyCode==13)showPaletteBg(\''+this.id+'\');\"></div>'
    });

    this.tclrPicker=new Wtf.Panel({
        border:false,
        html:'<div tabindex=25 id = "timg_div'+this.id+'" style="cursor:pointer; height:12px; width:12px; margin:auto; padding:auto; border:thin solid; border-color:'+tvalue
                +'; background-color:'+tvalue+';" onclick=\"showPaletteTxt(\''+this.id+'\')\" onkeypress=\"javascript:if(event.keyCode==13)showPaletteTxt(\''+this.id+'\');\"></div>'
    });
    this.tcc=tvalue;
    this.bcc=bgvalue;

    this.fpager= new Wtf.form.Checkbox({
                    name:'pager',
                    boxLabel:WtfGlobal.getLocaleText("hrms.export.Paging"),
                    labelSeparator:'',
                    tabIndex:28,
                    listeners:{check:this.checkfPager, scope:this}
    });
    this.hpager= new Wtf.form.Checkbox({
                    name:'pager',
                    boxLabel:WtfGlobal.getLocaleText("hrms.export.Paging"),
                    tabIndex:15,
                    labelSeparator:'',
                    listeners:{check:this.checkhPager, scope:this}
    });
    this.hdater= new Wtf.form.Checkbox({
                    name:'dater',
                    boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Date"),
                    tabIndex:14,
                    labelSeparator:'',
                    listeners:{check:this.checkhDater, scope:this}
    });
    this.fdater=new Wtf.form.Checkbox({
                    name:'dater',
                    boxLabel:WtfGlobal.getLocaleText("hrms.payroll.Date"),
                    tabIndex:27,
                    labelSeparator:'',
                    listeners:{check:this.checkfDater, scope:this}
    });

    
    this.customForm=new Wtf.FormPanel({
        fileUpload: true,
        autoScroll: true,
        border: false,
        width:'100%',
        frame:false,
        method :'POST',
        scope: this,
        labelWidth: 40,
        items:[{
            border: false,
            html: '<center><div style="padding-top:10px;color:#154288;font-weight:bold"> '+WtfGlobal.getLocaleText("hrms.export.Customizereportselectingyourpreferences")+'</div><hr style = "width:95%;"></center>'
        },{
            layout:'column',
            border: false,
            items:[this.hfieldset,{
                columnWidth: 0.20,
                border: false,
                bodyStyle : 'margin-left:50%;margin-top:15%;',
                items:[this.hdater]
                },{
                columnWidth: 0.19,
                border: false,
                bodyStyle : 'margin-left:15%;margin-top:15%;',
                items:[this.hpager]
            }]
        },{ 
            border: false,
            html: '<center><hr style = "width:95%;"></center>'
        },{
            layout: 'column',
            border: false,
            items:[{
                columnWidth: 0.49,
                border: false,
                items:[{
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.PageBorder"),
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[{
                        xtype:'radio',
                        id:'pbordertrue'+this.id,
                        name:'pborder',
                        inputValue :'true',
                        tabIndex:16,
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.WithBorder"),
                        labelSeparator:'',
                        checked:true
                      },{
                        xtype:'radio',
                        name:'pborder',
                        inputValue :'false',
                        tabIndex:17,
                        labelSeparator:'',
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.NoBorder")
                    }
                    ]
                },{
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.DataandGridBorder"),
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[{
                        xtype:'radio',
                        id:'gridbordertrue'+this.id,
                        name:'dborder',
                        inputValue :'true',
                        tabIndex:20,
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.WithBorder"),
                        labelSeparator:'',
                        checked:true
                    },
                    {
                        xtype:'radio',
                        name:'dborder',
                        inputValue :'false',
                        labelSeparator:'',
                        tabIndex:21,
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.NoBorder")
                    }]
                },
                {
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.SelectBackgroundColor"),
                    cls: "customFieldSet",
                    id: this.id + 'bcolorPicker',
                    autoHeight : true,
                    items:[this.bclrPicker]
                }]
            },{
                columnWidth: 0.49,
                border: false,
                items:[{
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.PageView"),
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[{
                        xtype:'radio',
                        name:'pview',
                        inputValue :'false',
                        tabIndex:18,
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.Potrait"),
                        labelSeparator:''
                    },
                    {
                        xtype:'radio',
                        name:'pview',
                        id:'pageviewtrue'+this.id,
                        inputValue :'true',
                        tabIndex:19,
                        labelSeparator:'',
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.Landscape"),
                        checked:true
                    }]
                },{
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.CompanyLogo"),
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[{
                        xtype:'radio',
                        name:'complogo',
                        inputValue :'false',
                        tabIndex:22,
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.HideLogo"),
                        labelSeparator:'',
                        checked:true
                    },
                    {
                        xtype:'radio',
                        name:'complogo',
                        id:'companylogo'+this.id,
                        inputValue :'true',
                        tabIndex:23,
                        labelSeparator:'',
                        boxLabel:WtfGlobal.getLocaleText("hrms.export.ShowLogo")
                    }]
                },
                {
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.SelectTextColor"),
                    cls: "customFieldSet",
                    id:this.id+'tcolorPicker',
                    autoHeight : true,
                    items:[this.tclrPicker]
                }              
                ]
            }]
        },
        {
            border: false,
            html: '<center><hr style = "width:95%;"></center>'
        },{
            layout:'column',
            border: false,
            items:[{
                columnWidth: 0.59,
                border: false,
                items:[{
                    xtype:'fieldset',
                    title: WtfGlobal.getLocaleText("hrms.export.FooterFields"),
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[
                    {
                        xtype : 'textfield',
                        id:'footernote'+this.id,
                        fieldLabel:WtfGlobal.getLocaleText("hrms.export.FooterNote"),
                        tabIndex:26,
                        maxLength:40,
                        //validator:WtfGlobal.validateHTField,
                        maxLengthText:WtfGlobal.getLocaleText("hrms.export.MaxLengthText40"),
                        labelSeparator:'',
                        emptyText:WtfGlobal.getLocaleText("hrms.export.InsertNote")
                    }]
                }]
            },{
                columnWidth: 0.20,
                border: false,
                bodyStyle : 'margin-left:55%;margin-top:15%;',
                items:[this.fdater]
            },{
                columnWidth: 0.20,
                border: false,
                bodyStyle : 'margin-left:15%;margin-top:15%;',
                items:[this.fpager]
            }]
        },
        {
            border: false,
            html: '<center><hr style = "width:95%;"></center>'
        },
        {
            xtype:'button',
            text:'<b>'+WtfGlobal.getLocaleText("hrms.export.ExportPDFFile")+'<b>',
            cls:'exportpdfbut',
            tabIndex:29,
            scope:this,
            handler:function(){
                if (this.customForm.getForm().isValid())
                 Wtf.MessageBox.confirm( WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.export.Doyoutosavereportforfurtheruses"),function(btn,text){
                            if(btn =='yes'){
                                this.saveTemplate();
                            }
                            else{
                                this.exportPdf();
                               // Wtf.getCmp('as').getActiveTab().close();
                            }
                        },this);
                else
                Wtf.MessageBox.alert( WtfGlobal.getLocaleText("hrms.common.alert"), WtfGlobal.getLocaleText("hrms.export.Pleasevalidateentries"));
            }
        }
        ]
    });
    
    Wtf.customReport.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.customReport,Wtf.Panel,{
    onRender: function(conf){
        Wtf.customReport.superclass.onRender.call(this, conf);
        if(this.reportType==2)
            Wtf.getCmp(this.id + 'adjustColWidth').hide();
        this.add(this.customForm);
    },
    removesubtitle:function(){
        this.attachheight -=35;
        this.hfieldset.setHeight(this.attachheight);
        if(this.count>5)
            document.getElementById('subtitlelink'+this.id).style.display='block';
        this.count--;
        if(this.count==1)
            document.getElementById('subtitlelink'+this.id).innerHTML = WtfGlobal.getLocaleText("hrms.export.AddSubtitle");
        this.doLayout();
    },
    Addsubtitle:function(){
        var tabcount = this.count*2;
        var textfield = new Wtf.form.TextField({
            fieldLabel: '',
            labelSeparator:'',
            tabIndex:tabcount+1,
            emptyText:WtfGlobal.getLocaleText("hrms.export.AddSubtitle"),
            maxLength:40,
            name: 'subtitle'+(this.count++)
        });
        this.attachheight = this.attachheight+35;
        var pid = 'subtitle'+this.count+this.id;
        this.hfieldset.insert(this.count,new Wtf.Panel({
            id : pid,
            cls:'subtitleAddRemove',
            border: false,
            html:'<a tabindex='+(tabcount+2)+' href=\"#\" class ="attachmentlink" style ="margin-left:5px" onclick=\"removesubtitle(\''+pid+'\',\''+this.id+'\')\">'+ WtfGlobal.getLocaleText("hrms.common.Remove")+'</a>',
            items:textfield
            })
        );
        this.hfieldset.setHeight(this.attachheight);
        document.getElementById('subtitlelink'+this.id).innerHTML = WtfGlobal.getLocaleText("hrms.export.Addanothersubtitle");
        if(this.count>5)
            document.getElementById('subtitlelink'+this.id).style.display='none';

        this.doLayout();
    },
    checkhDater:function(cbox,checked){
        if(checked)
        this.fdater.reset();
    },
    checkfDater:function(cbox,checked){
        if(checked)
        this.hdater.reset();
    },
    checkhPager:function(cbox,checked){
        if(checked)
        this.fpager.reset();
    },
    checkfPager:function(cbox,checked){
        if(checked)
        this.hpager.reset();
    },
    saveTemplate:function(){
        var nameField = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.name")+' *',
            id:'repTemplateName',
            //validator: WtfGlobal.validateUserName,
            allowBlank: false,
            maxLength: 40,
            width:255
        });
        var descField = new Wtf.form.TextArea({
            id:'repDescField',
            height: 187,
            hideLabel:true,
            cls:'descArea',
            maxLeangth: 200,
            fieldClass : 'descLabel',
            width:356
        });
        
        var Template = new Wtf.Window({
                title: WtfGlobal.getLocaleText("hrms.export.NewReportTemplate"),
                width: 390,
                layout: 'border',
                iconCls : 'iconwin',
                modal: true,
                height: 330,
                frame: true,
                border:false,
                items:[{
                    region: 'north',
                    height: 45,
                    width: '95%',
                    id:'northRegion',
                    border:false,
                    items:[{
                        layout:'form',
                        border:false,
                        labelWidth:100,
                        frame:true,
                        items:[nameField]
                    }]
                },{
                    region: 'center',
                    width: '95%',
                    height:'100%',
                    id: 'centerRegion',
                    layout:'fit',
                    border:false,
                    items:[{
                        xtype:'fieldset',
                        title: WtfGlobal.getLocaleText("hrms.performance.description"),
                        cls: 'textAreaDiv',
                        labelWidth:0,
                        frame:false,
                        border:false,
                        items:[descField]
                    }]
                }],
                buttons:[{
                    text: WtfGlobal.getLocaleText("hrms.common.Save"),
                    handler: function() {
                        if(!nameField.isValid()) {
                            //msgBoxShow(["Warning", "Please fill mandatory fields."],1);
                            return;
                        }
                        this.saveReportTemplate(Template,nameField,descField);
                        this.exportPdf();
                        mainPanel.remove(mainPanel.getActiveTab());
                    },
                    scope: this
                },{
                    text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                    handler:function() {
                        Template.close();
                    }
                }]
            });
            Template.show();
    },
    saveReportTemplate:function(win, nameField, descField){
        var tname = WtfGlobal.HTMLStripper(nameField.getValue());
        var description = WtfGlobal.HTMLStripper(descField.getValue());
        if(tname == null && tname == "") {
            Wtf.MessageBox.alert( WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.export.ThereportTempnotbeensavedPleasecheckentriesandtryagain"));
            return;
        }
        if(descField.isValid()){
            Wtf.Ajax.requestEx({
    //            url: Wtf.req.base + 'template.jsp',
                url:  "Common/ExportPdfTemplate/saveReportTemplate.common",
                params: {
                    action: 0,
                    name: tname,
                    data: this.generateData(),
                    desc: description,
                    userid:loginid
                },
                method:'POST'
            },
            this,
            function(res,req) {
                if(res.success)
                    msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.export.Thetemplatehasbeensaved")],1);
            },
            function() {
                msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.export.CouldnotcreatetemplatePleasetryagain")],2);
            });
            win.close();
        }
    },
    exportPdf:function() {
        var data=this.generateData();
            if(this.get == 3){
                var expt =new Wtf.ExportInterface({
                                    type:this.type,
                                    mode:this.mode,
                                    get:this.get,
                                    url:this.url,
            //                        filename:Wtf.getCmp('as').getActiveTab().title,
                                    filename:this.filename,
                                    //parent:this.templateWindow,
                                    gridConfig:this.gridConfig,
                                    cd:1,
                                    json:this.json,
                                    pdfDs:this.obj.pdfStore,
                                    obj:this.obj,
                                    configstr:data
                                });
                                expt.show();
//            var url =this.url+"?"+this.mode+"&config="+data+"&name="+this.filename+"&filetype="+ this.type
//                                 +"&get="+this.get+"&gridconfig="+encodeURIComponent(this.gridconfig)+"";
        }else{
            var params=this.obj.grid.getStore().lastOptions;
            var startdate=params.params.startdate;
            var enddate=params.params.enddate;
            var empid=params.params.empid;
    //        var url ="../../export.jsp?"+this.mode+"&config="+data+"&filename="+this.filename+"&filetype="+ this.type
            var url =this.url+"?"+this.mode+"&config="+data+"&name="+this.filename+"&filetype="+ this.type
                                 +"&get="+this.get+"&gridconfig="+encodeURIComponent(this.gridconfig)+"&startdate="+startdate+"&enddate="+enddate+"&empid="+empid+"";
        }
        Wtf.get('downloadframe').dom.src = url;
    },
    generateData:function(){
         var subtitles="";
         var tboxes=this.hfieldset.findByType('textfield');
         var headNote=WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tboxes[0].getValue()));
         var title=WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tboxes[1].getValue()));
         var sep="";
         for(i=2; i<tboxes.length; i++){
            subtitles += sep + WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tboxes[i].getValue()));
            sep="~";
         }
         var headDate=this.hdater.getValue();
         var headPager=this.hpager.getValue();
         var footDate=this.fdater.getValue();
         var footPager=this.fpager.getValue();
         var footNote=WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(Wtf.getCmp('footernote'+this.id).getValue()));

         var pb=Wtf.getCmp('pbordertrue'+this.id). getGroupValue();
         var gb=(Wtf.getCmp('gridbordertrue'+this.id). getGroupValue());
         var pv=(Wtf.getCmp('pageviewtrue'+this.id). getGroupValue());
         var cl=(Wtf.getCmp('companylogo'+this.id). getGroupValue());
         var tColor = this.tcc.substring(1);
         var bColor = this.bcc.substring(1);
         var data = '{"landscape":"'+pv+'","pageBorder":"'+pb+'","gridBorder":"'+gb+'","title":"'+title +'","subtitles":"'+subtitles +'","headNote":"'+headNote+'","showLogo":"'+cl +'","headDate":"'+headDate+'","footDate":"'+footDate+'","footPager":"'+footPager+'","headPager":"'+headPager+'","footNote":"'+footNote+'","textColor":"'+tColor+'","bgColor":"'+bColor+'"}';
         return data;
    },

    showColorPanelBg: function(obj) {
        var colorPicker = new Wtf.menu.ColorItem({
            id: 'coloritem'
        });

        var contextMenu = new Wtf.menu.Menu({
            id: 'contextMenu',
            items: [ colorPicker ]
        });
        contextMenu.showAt(Wtf.get(this.id + 'bcolorPicker').getXY());
        colorPicker.on('select', function(palette, selColor){
                this.bcc= '#' + selColor;
                Wtf.get("bimg_div"+this.id).dom.style.backgroundColor = this.bcc;
        },this);
    },
    showColorPanelTxt: function(obj) {
        var colorPicker = new Wtf.menu.ColorItem({
            id: 'coloritem'
        });
        var contextMenu = new Wtf.menu.Menu({
            id: 'contextMenu',
            items: [ colorPicker ]
        });
        contextMenu.showAt(Wtf.get(this.id + 'tcolorPicker').getXY());
        colorPicker.on('select', function(palette, selColor){
                this.tcc= '#' + selColor;
                Wtf.get("timg_div"+this.id).dom.style.backgroundColor = this.tcc;
        },this);
    }
});

function Addsubtitle(objid){
    Wtf.getCmp(objid).Addsubtitle();
}

function removesubtitle(objid,thisid){
    Wtf.getCmp(objid).ownerCt.remove(Wtf.getCmp(objid),true);
    Wtf.getCmp(thisid).removesubtitle();
}
function showPaletteBg(cid){
        Wtf.getCmp(cid).showColorPanelBg(Wtf.get("bimg_div"+cid));
}
function showPaletteTxt(cid){
        Wtf.getCmp(cid).showColorPanelTxt(Wtf.get("timg_div"+cid));
}
