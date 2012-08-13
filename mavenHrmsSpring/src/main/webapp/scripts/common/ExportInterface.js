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
Wtf.ExportInterface=function(config) {
    Wtf.ExportInterface.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.ExportInterface, Wtf.Window, {

    initComponent : function(config) {
        this.winHeight = 424;
        if(Wtf.isIE6)
            this.winHeight = 413;
        this.topTitle = WtfGlobal.getLocaleText({key:"hrms.export.exportfile",params:[WtfGlobal.getLocaleText("hrms.export."+this.type)]});
        this.opt = WtfGlobal.getLocaleText({key:"hrms.export.chosecolumnText",params:[WtfGlobal.getLocaleText("hrms.export."+this.type)]});
        this.colSM = new Wtf.grid.CheckboxSelectionModel({
            width: 25
        });
        this.colCM = new Wtf.grid.ColumnModel([ this.colSM,{
            header: WtfGlobal.getLocaleText("hrms.common.Column"),
            dataIndex: "title"
        },{
            header: WtfGlobal.getLocaleText("hrms.common.title"),
            dataIndex: "header",
            hidden:true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.index"),
            dataIndex: "index",
            hidden:true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.align"),
            dataIndex: "align",
            hidden:true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Width"),
            hidden:((this.type=="csv")?true:false),
            dataIndex: 'width',
            editor: new Wtf.form.NumberField({
                allowBlank: false,
                maxValue: 850,
                minValue: 50
            })
        }]);
        this.headerField = new Wtf.form.TextField({
            labelSeparator:'',
            width: 180,
            emptyText: mainPanel.getActiveTab().title
        });
        this.colG = new Wtf.grid.EditorGridPanel({
            store: this.pdfDs,
            border: false,
            layout: "fit",
            width : 328,
            height:270,
            viewConfig: {
                forceFit: true
            },
            cm: this.colCM,
            autoScroll: true,
            clicksToEdit: 1,
            sm: this.colSM
        });
        this.colG.on("render", function(obj){
            obj.getSelectionModel().selectAll();
        }, this);
        this.title=WtfGlobal.getLocaleText("hrms.export.Export");
        this.iconCls=getButtonIconCls(Wtf.btype.winicon);
        this.height= this.winHeight;
        this.width= 350;
        this.modal=true;
        this.layout="table";
        this.layoutConfig= {
            columns: 1
        };
        this.resizable=false;
        this.items= [{
            height: 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html : getTopHtml(this.topTitle ,this.opt,this.type=="csv"?'../../images/export-csv-popup.jpg':'../../images/export-pdf-popup.jpg')
        },{
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:5px 5px 0px 5px;',
            layout: 'fit',
            width : 338,
            items: [this.colG]
        }];
        this.buttons= [{
            text:WtfGlobal.getLocaleText("hrms.export.Previous"),
            scope:this,
            hidden:((this.type=="csv")?true:false),
            hidden:(!this.parent)?true:false,
            handler:function() {
                this.hide();
                this.parent.show();
            }
        },{
            text: WtfGlobal.getLocaleText("hrms.export.Export"),
            scope: this,
            handler: function() {
            var selCol = this.colSM.getSelections();
            if(selCol.length > 0){
                var header = [];
                var title = [];
                var width = [];
                var indx = [];
                var align=[];
                var k = 0;
                var flag=0;

                for(var i = 0; i < selCol.length; i++) {
                    var recData = selCol[i].data;
                    header.push(recData.header);
                    if(recData.title.indexOf('(')!=-1) {
                        recData.title=recData.title.substring(0,recData.title.indexOf('(')-1);
                    }
                    if(recData.title.indexOf('*')!=-1) {
                        recData.title=recData.title.substring(0,recData.title.length-1);
                        title.push(recData.title);
                    } else
                        title.push(recData.title);
                    width.push(recData.width);
                    indx.push(recData.index);
                    if(recData.align=='')
                        align.push('none');
                    else
                        align.push(recData.align);


                }
                k = indx.length;
                for(i = 0; i < k; i++) {   //sort based on index
                    for(var j = i+1; j < k; j++) {
                        if(indx[i] > indx[j]) {
                            var temp = header[i];
                            header[i] = header[j];
                            header[j] = temp;

                            temp = title[i];
                            title[i] = title[j];
                            title[j] = temp;

                            temp = width[i];
                            width[i] = width[j];
                            width[j] = temp;

                            temp = align[i];
                            align[i] = align[j];
                            align[j] = temp;
                        }
                    }
                }
                if(this.type == "pdf") {
                    var max = Math.floor(820/k);  //820 = total width of pdf page
                    if(k >= (this.pdfDs.getTotalCount()*0.75)) {
                        max = 150;
                    }
                    max=Math.round(max);
                    for(i = 0; i < selCol.length; i++) {
                        if(selCol[i].data["width"] > max) {
                        flag = 1;
                        }
                    }
                    if(flag == 1) {
                        flag = 1;
                      //  Wtf.MessageBox.alert("Alert","The maximum width for fields is "+max+".");
                      msgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText({key:"hrms.export.MaxWidthIs",params:[max]})], 1);
                    }
                }
                if(flag == 0) {
                    this.close();
                    if(this.get==3){
                        var url =this.url+"?"+this.mode+"&config="+this.configstr+"&name="+this.filename+"&filetype="+ this.type
                             +"&header="+header+"&title="+title+"&width="+width+"&align="+align+"&get="+this.get+"";
                    }else{
                        var params=this.obj.grid.getStore().lastOptions;
                        var startdate=(params.params!=undefined?params.params.startdate:undefined);
                        var enddate=(params.params!=undefined?params.params.enddate:undefined);
                        var empid=(params.params!=undefined?params.params.empid:undefined);
                        var url=this.url+"?"+this.mode+"&config="+this.configstr+"&name="+this.filename+"&filetype=" + this.type
                            +"&header="+header+"&title="+title+"&name="+this.headerField.getValue()+"&width="+width+"&get="+this.get+"&align="+align+"&startdate="+startdate+"&enddate="+enddate+"&empid="+empid+"&exportFile="+true;
                    }
                    Wtf.get('downloadframe').dom.src  = url;
                }
               } else {
                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.export.SelectAtLeastOneColumn")], 1);
            }
         }
    },{
        text: WtfGlobal.getLocaleText("hrms.common.cancel"),
        scope:this,
        handler: function(){
            this.close();
        }
    }];
    Wtf.ExportInterface.superclass.initComponent.call(this,config);
    }
 });
       
Wtf.exportButton=function(config){
    var mnuBtns=[];
    if(config.menuItem.csv==true){
        this.isPDF = false;
        mnuBtns.push(this.createButton("csv"));
    }
    if(config.menuItem.pdf==true){
        this.isPDF = true;
        mnuBtns.push(this.createButton("pdf"));
    }
    //if(config.menuItem.rowPdf==true)mnuBtns.push(this.createRowButton(config.get));
    Wtf.apply(this,{
        menu:mnuBtns
    },config);   
    Wtf.exportButton.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.exportButton,Wtf.Toolbar.Button,{
    text:WtfGlobal.getLocaleText("hrms.export.Export"),
    iconCls: 'pwndExport export',
    createButton:function(type){
        var btn=new Wtf.Action({
            text: WtfGlobal.getLocaleText({key:"hrms.export.ExportToFile",params:[WtfGlobal.getLocaleText("hrms.export."+type.toUpperCase())]}),
            iconCls:'pwndExport '+(this.isPDF?'exportpdf':'exportcsv'),
            scope: this,
            handler:function(){
                    this.exportWithTemplate(this.obj,type,this.get,this.url,this.filename)
            }
        });
        return btn;
    },
    createRowButton:function(get){
        var btn=new Wtf.Action({
            text: WtfGlobal.getLocaleText("hrms.export.ExportSingleRow"),
            iconCls: 'pwndExport exportpdf',
            scope: this,
            disabled:true,
            handler:function(){
                this.exportSingleRow(this.obj,this.get)
            }
        });
        return btn;
    },

    exportWithTemplate:function(obj,type,get,url,filename) {
        if(obj.pdfStore==undefined){
            obj.pdfStore =new Wtf.data.Store({});
            obj.pdfStore=this.filPdfStore(obj,obj.grid.getColumnModel());
        }
        var jsonGrid =this.genJsonForPdf(obj);
        if(type == "pdf") {
            new Wtf.selectTempWin({
                type:type,
                get:get,
                mode:Wtf.urlEncode(obj.grid.getStore().baseParams),
//                filename:Wtf.getCmp('as').getActiveTab().title,
                filename:filename,
                storeToload:obj.pdfStore,
                gridConfig : jsonGrid,
                grid:obj.EditorGrid,
                obj:obj,
                url:url,
                json:(obj.searchJson!=undefined)?obj.searchJson:""
            });
        } else {
            var expt =new Wtf.ExportInterface({
                type:type,
                get:get,
                mode:Wtf.urlEncode(obj.grid.getStore().baseParams),
//                filename:Wtf.getCmp('as').getActiveTab().title,
                filename:filename,
                pdfDs:obj.pdfStore,
                url:url,
                obj:obj
            });
            expt.show();
        }
},

filPdfStore:function(obj,column)
{
    var k=1;
    for(i=1 ; i<column.getColumnCount() ; i++) { // skip row numberer
      if(column.isHidden(i)!=undefined||column.getColumnHeader(i)==""||column.getDataIndex(i)==""){
                continue;
      }
      else{
        if( column.config[i].pdfwidth!=undefined) {
            var aligned=column.config[i].align;
            var title;
            if(aligned==undefined)
                aligned='center';
            if(column.config[i].title==undefined)
                title=column.config[i].dataIndex;
            else
                title=WtfGlobal.HTMLStripper(column.config[i].title);
            obj.newPdfRec = new Wtf.data.Record({
                header : title,
                title : WtfGlobal.HTMLStripper(column.config[i].header),
                width : column.config[i].pdfwidth,
                align : aligned,
                index : k
            });
            obj.pdfStore.insert(obj.pdfStore.getCount(), obj.newPdfRec);
            k++;
        }
      }
    }
   return obj.pdfStore;
},

genJsonForPdf:function (obj)
{
    var jsondata = "{ data:[";
    for(i=0;i<obj.pdfStore.getCount();i++) {
        var record = obj.pdfStore.getAt(i);
        jsondata+="{'header':'" + WtfGlobal.HTMLStripper(record.data.header) + "',";
        if(record.data.align=="right" && record.data.title.indexOf("(")!=-1) {
            record.data.title=record.data.title.substring(0,record.data.title.indexOf("(")-1);
        }
        jsondata+="'title':'" + record.data.title + "',";
        jsondata+="'width':'" + record.data.width + "',";
        jsondata+="'align':'" + record.data.align + "'},";
    }
    var trmLen = jsondata.length - 1;
    var finalStr = jsondata.substr(0,trmLen);
    finalStr+="]}";
    return finalStr;
},
getDateDiff:function(ldate,fdate){
       var date1= new Date(ldate);
       var time=date1.getElapsed( new Date(fdate));
       var days=time/ (3600000*24);
       return days;
 },
 exportSingleRow:function(obj,get){
      var terms=null;
      var mode=0;
      var selRec=null;
        var rec=obj.grid.getSelectionModel().getSelected();
           if(get==5||get==6){
              terms=this.getDateDiff(rec.data.duedate,rec.data.invoicedate);
              mode=obj.isCustomer?1:2;
              selRec = "&name="+rec.data.customername+"&terms="+terms+"&amount="+rec.data.amount+"&bills="+rec.data.invoiceid;
           }else{
              mode=obj.isCNReport?4:3;
              selRec = "&name="+rec.data.personname+"&amount="+rec.data.amount+"&id="+rec.data.noteid+"&type="+obj.transType;
           }
           Wtf.get('downloadframe').dom.src = "exportInv.jsp?mode="+mode+"&rec="+selRec+"&filename="+Wtf.getCmp('as').getActiveTab().title+"&filetype=pdf";
    }
 });




