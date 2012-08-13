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
Wtf.TimesheetReport=function(config){
    config.autoScroll="true";
    Wtf.TimesheetReport.superclass.constructor.call(this,config);
};
Wtf.extend(Wtf.TimesheetReport,Wtf.Panel,{
    initComponent:function(config){
        Wtf.TimesheetReport.superclass.initComponent.call(this,config);
    },
    onRender: function(config) {
        Wtf.TimesheetReport.superclass.onRender.call(this, config);
        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        Wtf.ux.grid.GridSummary.Calculations.totalTime1 = function(v, record, colName, data, rowIdx){
            var aa=[];
            var bb=[];            
            bb=data[colName];
            if(bb!=0){
                bb=bb.split(':');
            }
            aa=v.split(':');            
            var hr=Wtf.getValidNumberOrDefault(bb[0],0)+Wtf.getValidNumberOrDefault(aa[0],0);
            var mn=Wtf.getValidNumberOrDefault(bb[1],0)+Wtf.getValidNumberOrDefault(aa[1],0);
            if(mn>=60){
                hr++;
                mn-=60;
            }
            hr=hr<10?"0"+hr:hr;
            mn=mn<10?"0"+mn:mn;
            return  hr+":"+mn;
        };

        this.summary = new Wtf.ux.grid.GridSummary();
        this.jobRecord=Wtf.data.Record.create([
        { name:'jobtype'},{ name:'jobtypename'},{name:'col1'},{name:'col2'},{name:'col3'},{name:'col4'},{name:'col5'},
        { name:"col6"},{name:"col7"},{name:'colid1'},{name:'colid2'},{name:'colid3'},
        { name:'colid4'},{name:'colid5'},{name:"colid6"},{name:"colid7"},{name:"total"}
        ]);


        this.jobReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.jobRecord);

        this.jobstore= new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url: "Timesheet/EmployeesTimesheet.ts",
            reader:this.jobReader,
            baseParams:{
                flag:26
            }
        });
     
        this.fromdate=new Wtf.form.DateField({
            name:'from',
            width:150,
            format:'Y-m-d',
            disabled:true
        });
        this.todate=new Wtf.form.TextField({
            name:'to',
            width:150,
            readOnly:true,
            disabled:true
        });
                 
        this.fromdate.on("render",function(){           
            this.fromdate.setValue(this.viewstdate);           
            this.changegridheader();
        },this);
      
        this.cm1=new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer(),
        {
            header: WtfGlobal.getLocaleText("hrms.timesheet.job"),
            width:200,
            pdfwidth:60,
            sortable: true,
            dataIndex: 'jobtypename',
            summaryRenderer:WtfGlobal.totalSummaryRenderer           
        },
        {
            header:WtfGlobal.getLocaleText("hrms.timesheet.sunday"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            align:'center',
            summaryType:'totalTime1',
            dataIndex: 'col1',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.monday"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            align:'center',            
            summaryType:'totalTime1',
            dataIndex: 'col2',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header:WtfGlobal.getLocaleText("hrms.timesheet.tuesday"),
            width:200,
            sortable:true,
            pdfwidth:60,
            summaryType:'totalTime1',
            dataIndex:'col3',
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },

        {
            header:WtfGlobal.getLocaleText("hrms.timesheet.wednesday"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            dataIndex: 'col4',
            summaryType:'totalTime1',
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer

        },
        {
            header:WtfGlobal.getLocaleText("hrms.timesheet.thursday"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            summaryType:'totalTime1',
            dataIndex: 'col5',            
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },
        {
            header:WtfGlobal.getLocaleText("hrms.timesheet.friday"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            summaryType:'totalTime1',
            dataIndex: 'col6',            
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },
        {
            header:WtfGlobal.getLocaleText("hrms.timesheet.saturday"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            summaryType:'totalTime1',
            dataIndex: 'col7',            
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },
        {
            header: WtfGlobal.getLocaleText("hrms.timesheet.total"),
            width: 200,
            sortable: true,
            pdfwidth:60,
            dataIndex: 'total',
            summaryType:'totalTime1',
            renderer:this.totaltime
        }]);

        this.grid = new Wtf.grid.GridPanel({
            store: this.jobstore,
            autoScroll :true,
            border:false,
            id:'timesheetReportGrid'+this.id,
            plugins:[this.summary],            
            scope:this,
            stripeRows: true,                        
            viewConfig: {
                forceFit: true
            },            
            cm: this.cm1,
            sm: this.sm
        });
        var tbar=[];
        var expBtn=new Wtf.exportButton({
            obj:this,
            menuItem:{
                csv:true,
                pdf:true,
                rowPdf:true
            },
            get:2,
//            url:"Common/Export/.common"
            url:"Timesheet/timesheetExport.ts",
            filename:this.title
        });
        var chart=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.Chart"),
            scope:this,
            iconCls:'pwnd chart1',
            handler:this.addChart
        });
        tbar.push(WtfGlobal.getLocaleText("hrms.timesheet.fromdate")+':',this.fromdate,'-',WtfGlobal.getLocaleText("hrms.timesheet.todate")+':',this.todate,'-',expBtn,'-',chart);
        var timepanel=new Wtf.Panel({
            tbar:tbar,
            border:false,
            autoScroll:true,
            id:'timepanel'+this.id,
            layout:'fit',
            items:[this.grid]
        });
        this.add(timepanel);
        this.on('activate', function(tp, tab){
            this.doLayout();

        });       
    },
    changegridheader: function(){        
        this.day=this.fromdate.getValue().format('D');
        var mychange=(this.day==WtfGlobal.getLocaleText("hrms.Sunday").substring(0,3)?0:(this.day==WtfGlobal.getLocaleText("hrms.Monday").substring(0,3)?1:(this.day==WtfGlobal.getLocaleText("hrms.Tuesday").substring(0,3)?2:(this.day==WtfGlobal.getLocaleText("hrms.Wednesday").substring(0,3)?3:(this.day==WtfGlobal.getLocaleText("hrms.Thursday").substring(0,3)?4:(this.day==WtfGlobal.getLocaleText("hrms.Friday").substring(0,3)?5:(this.day==WtfGlobal.getLocaleText("hrms.Saturday").substring(0,3)?6:7)))))));
        var myDate=new Date();
        var myDate1=new Date();
        myDate1=this.fromdate.getValue();
        myDate=this.fromdate.getValue();
        if(mychange==7){
            switch(this.day){
                case 'Sun':
                    myDate.setDate(myDate.getDate());
                    myDate1.setDate(myDate1.getDate()+6);
                    break;
                case 'Mon':
                    myDate.setDate(myDate.getDate()-1);
                    myDate1.setDate(myDate1.getDate()+5);
                    break;
                case 'Tue':
                    myDate.setDate(myDate.getDate()-2);
                    myDate1.setDate(myDate1.getDate()+4);
                    break;
                case 'Wed':
                    myDate.setDate(myDate.getDate()-3);
                    myDate1.setDate(myDate1.getDate()+3);
                    break;
                case 'Thu':
                    myDate.setDate(myDate.getDate()-4);
                    myDate1.setDate(myDate1.getDate()+2);
                    break;
                case 'Fri':
                    myDate.setDate(myDate.getDate()-5);
                    myDate1.setDate(myDate1.getDate()+1);
                    break;
                case 'Sat':
                    myDate.setDate(myDate.getDate()-6);
                    myDate1.setDate(myDate1.getDate());
                    break;
            }
        }else{
            myDate.setDate(myDate.getDate()-mychange);
            myDate1.setDate(myDate1.getDate()+(6-mychange));
        }
        this.fromdate.setValue(myDate.format('Y-m-d'));
        this.todate.setValue(myDate1.format('Y-m-d'));
        this.dateArray= new Array();
        this.dateArray.push(myDate.format('Y-m-d'));

        this.cm1.setColumnHeader(2,myDate.format('D d M Y'));
        for(i=2;i<8;i++)
        {
            myDate.setDate(myDate.getDate()+1);
            this.dateArray.push(myDate.format('Y-m-d'));
            this.cm1.setColumnHeader(i+1,myDate.format('D d M Y'));
        }

        var stdate=this.fromdate.getRawValue();
        calMsgBoxShow(202,4,true);
        this.jobstore.load({
            params:{
                empid:this.empid,
                startdate:stdate,
                enddate:this.todate.getValue()
            }
        });
        this.jobstore.on("load",function(){WtfGlobal.closeProgressbar();},this);
    },
    totaltime:function(a,b,c){
        var sum="00:00";
        var aa=[];
        var hr=0;
        var mn=0;
        for(var i=1;i<=7;i++){
            var s=c.data["col"+i+""];
            if(typeof s=="string" &&s.indexOf(":")>=0)sum=s;
            aa=sum.split(':');
            hr+=Wtf.getValidNumberOrDefault(aa[0],0);
            mn+=Wtf.getValidNumberOrDefault(aa[1],0);
            if(mn>=60){
                hr++;
                mn-=60;
            }
        }
        hr=hr<10?"0"+hr:hr;
        mn=mn<10?"0"+mn:mn;
        return  hr+":"+mn+" hrs";
    },
    addChart:function(){        
        var main=Wtf.getCmp("timesheetmanage");
        var demoTab=Wtf.getCmp('timesheetChart'+this.empid);
        if(demoTab==null)
        {
            demoTab=new Wtf.Panel({
                id:'timesheetChart'+this.empid,
                title:WtfGlobal.getLocaleText({key:"hrms.timesheet.usersTimesheetChart",params:[this.empname]}),
                iconCls:getTabIconCls(Wtf.etype.crm),
                autoScroll:true,               
                layout:'border',
                bodyStyle:'background:white',
                border:false,                
                closable:true,
                items:[{
                        region:'center',
                        layout:'fit',                        
                        cls:'panelstyleClass1',
                        html:'<div id="timesheetChartjobwise'+this.id+'" style="margin-top:150px;margin-left:20px" ></div>',
                        border:false
                },{
                    region:'west',
                    layout:'fit',
                    width:'50%',                    
                    cls:'panelstyleClass1',
                    html:'<div id="timesheetChartdaywise'+this.id+'" style="margin-top:150px;margin-left:20px"  ></div>',
                    border:false
                }]
            });
            main.add(demoTab);
        }
        main.setActiveTab(demoTab);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
        var swf='../../scripts/HRMSGraph/krwcolumn.swf';
        var setting='../../scripts/HRMSGraph/timesheet.xml';
        var data='../../jspfiles/chardata.jsp?flag=3&dates='+this.dateArray+'&empid='+this.empid+'';
        createNewChart(swf,'krwcolumn', '600px', '300px', '8', '#FFFFFF',setting,data,'timesheetChartdaywise'+this.id);
        var jArr=this.createJson();
        var data1='../../jspfiles/chardata.jsp?flag=4&dates='+this.dateArray+'&empid='+this.empid+'&jobs='+jArr[0]+'&jobnames='+jArr[1];
        var setting1='../../scripts/HRMSGraph/timesheet_1.xml';
        createNewChart(swf,'krwcolumn', '600px', '300px', '8', '#FFFFFF',setting1,data1,'timesheetChartjobwise'+this.id);
    }, 
    createJson:function(){
    	var jArr = [];
        var jsondataId = [];
        var jsondataName = [];
        for(var i=0;i<this.jobstore.getCount();i++){
            jsondataName.push(this.jobstore.getAt(i).get("jobtypename"));
            jsondataId.push(this.jobstore.getAt(i).get("jobtype"));
        }        
        jArr[0] = jsondataId;
        jArr[1] = jsondataName;
        return jArr;
    }
});




