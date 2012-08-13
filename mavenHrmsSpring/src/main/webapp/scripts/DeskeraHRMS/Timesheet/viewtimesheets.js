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
Wtf.viewtimesheet=function(config){

    Wtf.viewtimesheet.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.viewtimesheet,Wtf.Panel,{
    initComponent:function(config){
        Wtf.viewtimesheet.superclass.initComponent.call(this,config);
    },
    onRender: function(config) {
       Wtf.viewtimesheet.superclass.onRender.call(this, config);
        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.viewtimerecord=Wtf.data.Record.create([
        {
            name:'empid'
        },

        {
            name:'empname'
        },

        {
            name:'startdate',type:'date'
        },

        {
            name:'enddate',type:'date'
        },

        {
            name:'hours'
        },

        {
            name:'work'
        },

        {
            name:"status"
        },

        {
            name:"approvedby"
        }
        ]);


        this.timereader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.viewtimerecord);

        this.timestore= new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url: "Timesheet/AllTimesheets.ts",
            reader:this.timereader,
            baseParams:{
                flag:27
            }
        });

        this.fromdate=new Wtf.form.DateField({
            name:'from',
            width:150,
            format:'Y-m-d'
        });
        this.todate=new Wtf.form.TextField({
            name:'to',
            width:150,
            readOnly:true,
            disabled:true
        });

        this.setbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.set.duration"),//'Set Duration',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.view.set.duration.tooltip"),//"Select a start date and set the time to view the timesheet for that time period.",
            iconCls:'pwndCommon setbuttonIcon',
            minWidth:50,
            scope:this,
            handler:this.setduration
        });

        this.approvebutton=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.timesheet.approve.timesheet"),//'Approve Timesheet',
                tooltip:WtfGlobal.getLocaleText("hrms.timesheet.approve.timesheet.tooltip"),//"Change the status of the timesheet by approving it if the jobs carried out by the employee are found to be at par with the standards set.",
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                minWidth:114,
                scope:this,
                disabled:true,
                handler:function(){
		        	Wtf.MessageBox.show({
		                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
		                msg:WtfGlobal.getLocaleText("hrms.timesheet.approve.timesheet.confirm.msg"),
		                icon:Wtf.MessageBox.QUESTION,
		                buttons:Wtf.MessageBox.YESNO,
		                scope:this,
		                fn:function(button){
		                    if(button=='yes'){
		                    	this.approvefunc("approve");
		                    }
		                }
		            });
        		}
        });
        this.rejectbutton=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.timesheet.reject.timesheet"),
                tooltip:WtfGlobal.getLocaleText("hrms.timesheet.change.status.timesheet.rejecting"),
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                minWidth:114,
                scope:this,
                disabled:true,
                handler:function(){
		        	Wtf.MessageBox.show({
		                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
		                msg:WtfGlobal.getLocaleText("hrms.timesheet.reject.timesheet.confirm.msg"),
		                icon:Wtf.MessageBox.QUESTION,
		                buttons:Wtf.MessageBox.YESNO,
		                scope:this,
		                fn:function(button){
		                    if(button=='yes'){
		                    	this.approvefunc("reject");
		                    }
		                }
		            });
        		}
        });
        this.viewbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.view.timesheet"),//'View Timesheet',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.view.timesheet.tooltip"),//"Click on a particular user and view his/her timesheet and view all the jobs done by that employee on a given date range.",
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:96,
            id:"viewButton",
            scope:this,
            handler:function(){
               this.cmpst= this.fromdate.getValue().format('Y-m-d');
                this.cmpend=this.todate.getValue();
                this.arr=this.sm.getSelections();
                var empname=this.arr[0].get('empname');
                var empid=this.arr[0].get('empid');

                if(this.arr.length==1)
                    {
                var main=Wtf.getCmp("timesheetmanage");
                var tdemoTab=Wtf.getCmp("timesheetemp"+empid);
                if(tdemoTab==null)
                {
                    tdemoTab=new Wtf.timesheetemp({
                        id:"timesheetemp"+empid,
                        title:WtfGlobal.getLocaleText({key:"hrms.timesheet.EmployeesTimesheet",params:[empname]}),
                        layout:'fit',
                        viewtimesheet:true,
                        border:false,
                        closable:true,
                        empid:this.arr[0].get('empid'),
                        viewstdate:this.cmpst,
                        viewenddate:this.cmpend,
                        iconCls:getTabIconCls(Wtf.etype.hrmsprofile)
                    });

                    main.add(tdemoTab);
                }
                main.setActiveTab(tdemoTab);
                main.doLayout();
            }else{
                 calMsgBoxShow(72,0);
            }
            }
        });

         this.reportbutton=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.timesheet.generate.report"),//'Generate Report',
                tooltip:WtfGlobal.getLocaleText("hrms.timesheet.generate.report.tooltip"),//"Generate a report for the timesheet and then export it to an external location and also view it in the form of charts for better understanding.",
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                minWidth:114,
                scope:this,
                disabled:true,
                handler:this.generateReport
        });
          var viewbtns=new Array();
          this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        	  	this.timestore.load({params:{start:0,limit:this.viewtimeGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.viewtimeGrid.id).setValue("");
        	}
          });
          viewbtns.push('-');
          viewbtns.push(this.refreshBtn);
          viewbtns.push('-');
          viewbtns.push(WtfGlobal.getLocaleText("hrms.timesheet.fromdate")+":");//'From Date:');
          viewbtns.push(this.fromdate);
          viewbtns.push('-');
          viewbtns.push(WtfGlobal.getLocaleText("hrms.timesheet.todate")+":");//'To Date:');
          viewbtns.push(this.todate);         
          viewbtns.push(this.setbutton);
          if(!WtfGlobal.EnableDisable(Wtf.UPerm.viewtimesheet, Wtf.Perm.viewtimesheet.viewtimesheet)){
               viewbtns.push('-');
               viewbtns.push(this.viewbutton);
          }
          if(!WtfGlobal.EnableDisable(Wtf.UPerm.viewtimesheet, Wtf.Perm.viewtimesheet.approve)){
               viewbtns.push('-');
               viewbtns.push(this.approvebutton);
               viewbtns.push('-');
               viewbtns.push(this.rejectbutton);
               viewbtns.push('-');
               viewbtns.push(this.reportbutton);
          }
        this.cm1=new Wtf.grid.ColumnModel([
            this.sm,
        {
            header: WtfGlobal.getLocaleText("hrms.common.employee.name"),//"Employee Name",
            width:200,
            align:'center',
            sortable: true,
            dataIndex: 'empname'
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),//"Start Date",
            width: 200,
            sortable: true,
            align:'center',
            renderer:WtfGlobal.onlyDateRenderer,
            dataIndex: 'startdate'
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),//"End Date",
            width: 200,
            sortable: true,
            align:'center',
            renderer:WtfGlobal.onlyDateRenderer,
            dataIndex: 'enddate'
        },
        {
            header: WtfGlobal.getLocaleText("hrms.timesheet.official.work.hours"),//"Official Work Hours",
            width: 200,
            align:'center',
            sortable: true,
            dataIndex: 'hours'
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.employee.work.hours"),//"Employee Work Hours",
            width:200,
            align:'center',
            sortable:true,
            dataIndex:'work'
        //	 editor:this.istaxcheckbox
        },

        {
            header: WtfGlobal.getLocaleText("hrms.common.status"),//"Status ",
            width: 200,
            sortable: true,
            align:'center',
            dataIndex: 'status',
            renderer:function(a,b,c){
                if(c.get('status')==0){
                    return WtfGlobal.getLocaleText("hrms.common.status.notapproved");
                }else if(c.get('status')==1){
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.status.approved")+'</span>';
                }else if(c.get('status')==2){
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.status.rejected")+'</span>';
                }else{
                    return "";
                }
            }
        },
        {
            header: WtfGlobal.getLocaleText("hrms.timesheet.manager.name"),//"Approved By ",
            width: 200,
            align:'center',
            sortable: true,
            dataIndex: 'approvedby'
        }
    ]);

        this.viewtimeGrid = new Wtf.KwlGridPanel({
            store: this.timestore,
            autoScroll :true,
            border:false,
            scope:this,
            stripeRows: true,
            loadMask:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.timesheet.grid.msg"))//"No timesheet for the selected duration")
            },
            searchField:'empname',
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),//"Search by Employee Name ",
            serverSideSearch:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            displayInfo:true,
            cm: this.cm1,
            sm:this.sm,
            tbar:viewbtns

        });

        enableDisableButton("viewButton",this.timestore,this.sm);

        this.sm.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(viewbtns, this.viewtimeGrid, [10,14,16], [12]);
        },this);

        this.add( this.viewtimeGrid);
        this.fromdate.on("render",function(){
        	if(this.startdate)
        		this.fromdate.setValue(new Date(this.startdate).format('Y-m-d'));
        	else
        		this.fromdate.setValue(new Date().format('Y-m-d'));
           this.weekhandler();
        },this);
       
    }, 
    weekhandler:function(){ 
        this.day=this.fromdate.getValue().format('D');        
        var myDate=new Date();
        var myDate1=new Date();

        myDate1=this.fromdate.getValue();
        myDate=this.fromdate.getValue();

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
        this.fromdate.setValue(myDate.format('Y-m-d'));
        this.todate.setValue(myDate1.format('Y-m-d'));
        this.appdate1=myDate;
        this.appdate2=myDate1;
        var stdate=this.fromdate.getRawValue();
        var enddate=this.todate.getValue();
        this.timestore.baseParams={
                                flag:27,
                               startdate:stdate,
                               enddate:enddate
                            }
        calMsgBoxShow(202,4,true);
        this.timestore.load({
            params:{
                 start:0,
                 limit:this.viewtimeGrid.pag.pageSize
            }
        });
        this.timestore.on("load",function(){
            if(msgFlag==1)
            WtfGlobal.closeProgressbar()
        },this);
    },
    approvefunc:function(action){
         var apprvstdate=this.fromdate.getRawValue();
        this.emparr=this.sm.getSelections();
        this.empids=[];
        for(var i=0;i<this.emparr.length;i++){
            if(this.emparr[i].get('status')==0)
                this.empids.push(this.emparr[i].get('empid'));
            else{
                calMsgBoxShow(209,2);
                return;
            }
        }
        calMsgBoxShow(200,4,true);
        Wtf.Ajax.requestEx({
//          url:Wtf.req.base + "hrms.jsp",
            url: "Timesheet/AlltimesheetsApproval.ts",
          params: {
                flag:28,
                startdate:apprvstdate,
                enddate:this.todate.getValue(),
                empids:this.empids,
                action:action
            }
        },this,
        function(){
            if(action=="approve") {
                calMsgBoxShow(83,0);
            } else {
                calMsgBoxShow(230,0);
            }
            var stdate=this.fromdate.getRawValue();
            this.timestore.load({
                params:{
                 flag:27,
                 start:0,
                 limit:this.viewtimeGrid.pag.pageSize,
                 startdate:WtfGlobal.convertToGenericDate( this.appdate1),
                 enddate:WtfGlobal.convertToGenericDate( this.appdate2)
                }
            });

        },
        function(){
             calMsgBoxShow(84,1);
        }
    )
    },
     setduration:function(){
        this.weekhandler();
    },
    generateReport:function(){
        var arr=this.sm.getSelections();
        var empid=arr[0].get('empid');
        var empname=arr[0].get('empname');
        var stdate=this.fromdate.getValue().format('Y-m-d');
        var main=Wtf.getCmp("timesheetmanage");
        var demoTab=Wtf.getCmp('timesheetReport'+empid);
        if(demoTab==null)
        {
            demoTab=new Wtf.TimesheetReport({
                id:'timesheetReport'+empid,
                title:WtfGlobal.getLocaleText({key:"hrms.timesheet.EmployeesTimesheet",params:[empname]}),
                iconCls:getTabIconCls(Wtf.etype.crm),
                autoScroll:true,
                layout:'fit',
                border:false,
                empid:empid,
                empname:empname,
                closable:true,
                viewstdate:stdate
            });
            main.add(demoTab);
        }
        main.setActiveTab(demoTab);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    }
});

