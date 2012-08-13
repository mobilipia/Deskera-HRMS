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
Wtf.timesheetemp=function(config){
    Wtf.timesheetemp.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.timesheetemp,Wtf.Panel,{
	autoScroll :false,
    initComponent:function(config){
        Wtf.timesheetemp.superclass.initComponent.call(this,config);
    },
    onRender: function(config) {
        Wtf.timesheetemp.superclass.onRender.call(this, config);
        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        Wtf.ux.grid.GridSummary.Calculations.totalTime = function(v, record, colName, data, rowIdx){
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
        { name:'jobtype'},{name:'col1'},{name:'col2'},{name:'col3'},{name:'col4'},{name:'col5'},
        { name:"col6"},{name:"col7"},{name:'colid1'},{name:'colid2'},{name:'colid3'},
        { name:'colid4'},{name:'colid5'},{name:"colid6"},{name:"colid7"},{name:"total"},{name:"isSubmitted"}
        ]);


        this.jobReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.jobRecord);
        
        this.jobstore= new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url: "Timesheet/EmployeesTimesheet.ts",
            reader:this.jobReader,
            pruneModifiedRecords:true,
            baseParams:{
                flag:26
            }
        });
 
        this.selectdate=new Wtf.form.DateField({
            name:'selctdate',
            format:'Y-m-d',
            allowBlank:false
        }) ;
        var text1=this.createTextField();
        var text2=this.createTextField();
        var text3=this.createTextField();
        var text4=this.createTextField();
        var text5=this.createTextField();
        var text6=this.createTextField();
        var text7=this.createTextField();
        
        this.fromdate=new Wtf.form.DateField({
            name:'from',
            width:150,
            format:'Y-m-d',
            maxValue:new Date(),
            disabled: (this.viewtimesheet)?true:false
        });
        this.todate=new Wtf.form.TextField({
            name:'to',
            width:150,
            readOnly:true,
            disabled:true
        });
        this.addbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.add"),//'Add',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.add.tooltip"),//"Enter the jobs performed by the employee according to the dates.",
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:50,
            scope:this,
            handler:this.insertjob
        });

        this.setbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.set.duration"),//'Set Duration',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.set.duration.tooltip"),//"Select a particular date range and click 'set' to find out the jobs in the timesheet for that date range.",
            iconCls:'pwndCommon setbuttonIcon',
            minWidth:50,
            scope:this,
            handler:this.setduration
        });
        var jobtypeflag = Wtf.cmpPref.timesheetjob;
        if(!jobtypeflag){
            
            checkForJobTypeStoreLoad();

            this.jobtext = new Wtf.form.ComboBox({
                store:Wtf.jobtypeStore,
                displayField:'name',
                valueField:'id',
                scope:this,
                mode:'local',
                selectOnFocus:true,
                typeAhead:false,
                allowBlank:false,
                editable:false,
                triggerAction :'all',
                forceSelection:true
            });            
        } else {
            this.jobtext=new Wtf.ux.TextField({
                name:'jobtypetextbox',
                width:200,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                maxLength:100
            });
        }
        this.deletejobs=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),//'Delete',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.delete.tooltip"),//"Select a row and delete the job you want to.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:50,
            disabled:true,
            scope:this,
            handler:function(){

                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                    msg:deleteMsgBox('job'),
                    icon:Wtf.MessageBox.QUESTION,
                    buttons:Wtf.MessageBox.YESNO,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            this.deletejob();
                        }
                    }
                });
            }
        });

        this.savejobs=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.save.as.draft"),//'Save As Draft',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.save.as.draft.tooltip"),//"Enter the jobs and save them as draft in order to revisit and submit at a later time.",
            iconCls:'pwndCommon draftbuttonIcon',
            minWidth:100,
            scope:this,
            handler:function(){
        		this.gridcalculation(false);
        	}
        });
        this.submitjobs=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.submit.timesheet"),//'Submit Timesheet',
            tooltip:WtfGlobal.getLocaleText("hrms.timesheet.submit.timesheet.tooltip"),//"Enter the jobs and submit them. Also send them for approval to the approving officer and view the status of your timesheet.",
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            minWidth:100,
            scope:this,
            handler:function(){
        		Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.timesheet.save.data"),WtfGlobal.getLocaleText("hrms.timesheet.submit"),function(btn){//"Save Data","Do you want to Submit the Timesheet?",function(btn){
        			if(btn=="yes") {
        				this.gridcalculation(true);
        			}
    			},this);
        	}
        });
        this.fromdate.on("render",function(){
            if(this.viewstdate)
            {
                this.fromdate.setValue(this.viewstdate);

            }else
            {
                this.fromdate.setValue(new Date().format('Y-m-d'));
                this.fromdate.enable();
            }

            this.changegridheader();
        },this);
        this.cm1=new Wtf.grid.ColumnModel([this.sm,
        {
            header: WtfGlobal.getLocaleText("hrms.timesheet.job"),//"Job",
            width:200,
            //fixed:true,
            sortable: true,
            dataIndex: 'jobtype',
            summaryRenderer:WtfGlobal.totalSummaryRenderer,
            renderer:(!jobtypeflag)?Wtf.comboBoxRenderer(this.jobtext):"",
            editor: (!this.viewtimesheet)?this.jobtext:"",
            align:'center'
        },
        {
            header: WtfGlobal.getLocaleText("hrms.timesheet.sunday"),//"Sunday",
            width: 200,
            sortable: true,
            editor:(!this.viewtimesheet)?text1:"",
            align:'center',
            summaryType:'totalTime',
            dataIndex: 'col1',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.monday"),//"Monday",
            width: 200,
            sortable: true,
            align:'center',
            editor:(!this.viewtimesheet)?text2:"",
            summaryType:'totalTime',
            dataIndex: 'col2',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.tuesday"),//"Tuesday",
            width:200,
            sortable:true,
            summaryType:'totalTime',
            editor:(!this.viewtimesheet)?text3:"",
            dataIndex:'col3',
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.wednesday"),//"Wednesday ",
            width: 200,
            sortable: true,
            dataIndex: 'col4',
            summaryType:'totalTime',
            editor:(!this.viewtimesheet)?text4:"",
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer

        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.thursday"),//"Thursday ",
            width: 200,
            sortable: true,
            summaryType:'totalTime',
            dataIndex: 'col5',
            editor:(!this.viewtimesheet)?text5:"",
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.friday"),//"Friday ",
            width: 200,
            sortable: true,
            summaryType:'totalTime',
            dataIndex: 'col6',
            editor:(!this.viewtimesheet)?text6:"",
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.saturday"),//"Saturday ",
            width: 200,
            sortable: true,
            summaryType:'totalTime',
            dataIndex: 'col7',
            editor:(!this.viewtimesheet)?text7:"",
            align:'center',
            renderer:WtfGlobal.timeSummaryRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.timesheet.total"),//"Total",
            width: 200,
            sortable: true,
            dataIndex: 'total',
            summaryType:'totalTime',
            align:'center',
            renderer:this.totaltime
        }]);

        this.jobgrid = new Wtf.grid.EditorGridPanel({
            store: this.jobstore,
            border:false,
            id:'timesheetGrid'+this.id,
            plugins:[this.summary],
            disabled:true,
            scope:this,
            stripeRows: true,
            loadMask:true,
            clicksToEdit :1,
            viewConfig: {
                forceFit: true
            },
            tbar:(this.viewtimesheet)?[(WtfGlobal.getLocaleText("hrms.timesheet.fromdate")+":"),//'From Date:',
            this.fromdate,'-',(WtfGlobal.getLocaleText("hrms.timesheet.todate")+":"),this.todate]:[(WtfGlobal.getLocaleText("hrms.timesheet.fromdate")+":"),//'To Date:',this.todate]:['From Date:',
            this.fromdate,'-',(WtfGlobal.getLocaleText("hrms.timesheet.todate")+":"),this.todate,this.setbutton,'-',this.addbutton,'-', this.savejobs,'-',this.submitjobs,'-',this.deletejobs//'To Date:',this.todate,this.setbutton,'-',this.addbutton,'-', this.savejobs,'-',this.submitjobs,'-',this.deletejobs
            ],
            cm: this.cm1,
            sm: this.sm
        });
        if(!jobtypeflag) {
            this.jobgrid.on("afteredit",this.defaultValueAssignFunction,this);
        }
        
        this.add(this.jobgrid);
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.jobgrid.on("validateedit",this.validEditFunction,this);
        this.sm.on("selectionchange",function (){
            if(this.sm.hasSelection() && !this.isSubmitted){
            	this.savejobs.enable();
                this.submitjobs.enable();
                this.deletejobs.enable();
            }
            if(this.sm.getCount()==0 || this.isSubmitted){
            	this.deletejobs.disable();
            }
        },this);
        this.jobstore.on('load',function(a,b,c){
        	 this.isSubmitted = false;
             if(b.length>0){
            	 if(b[0].data.isSubmitted){
            		 this.isSubmitted = true;
            		 this.addbutton.disable();
                     this.savejobs.disable();
                     this.submitjobs.disable();
                     for(var i=0; i<this.cm1.getColumnCount(); i++){
                    	 this.cm1.setEditable( i, false);
                     }
            	}else{
            		 this.addbutton.enable();
                     this.savejobs.enable();
                     this.submitjobs.enable();
                     for(var i=0; i<this.cm1.getColumnCount(); i++){
                    	 this.cm1.setEditable( i, true);
                     }
            	 }
              }else{
                  this.addbutton.enable();
                  this.savejobs.disable();
                  this.submitjobs.disable();
                  for(var i=0; i<this.cm1.getColumnCount(); i++){
                 	 this.cm1.setEditable( i, true);
                  }
              }
              this.deletejobs.disable();
        },this);
    },       
    defaultValueAssignFunction:function(e){
        if(e.field=='jobtype'){
            var hours = Wtf.jobtypeStore.find("id",e.record.get('jobtype')) != undefined ? Wtf.jobtypeStore.getAt(Wtf.jobtypeStore.find("id",e.record.get('jobtype'))).data.weightage : "";
            if(hours != undefined || hours != "") {                
//                this.jobstore.data.items[e.row].set('col1',hours);
//                this.jobstore.data.items[e.row].set('col2',hours);
//                this.jobstore.data.items[e.row].set('col3',hours);
//                this.jobstore.data.items[e.row].set('col4',hours);
//                this.jobstore.data.items[e.row].set('col5',hours);
//                this.jobstore.data.items[e.row].set('col6',hours);
//                this.jobstore.data.items[e.row].set('col7',hours);
                var aa=[];
                aa =hours.split(":");       
                var col1Total = [0,0,0,0,0,0,0];
                var col2Total = [0,0,0,0,0,0,0];
                if(aa[0]>24||aa[1]>=61)
                {
                    calMsgBoxShow(144,0);
                    return false;
                }
                for(var tmp1=0;tmp1<this.jobstore.getCount();tmp1++){
                    if(tmp1!=e.row)
                    {
                        aa=this.jobstore.getAt(tmp1).get("col1").split(":");
                        col1Total[0]=col1Total[0]+parseFloat(aa[0]);
                        col2Total[0]=col2Total[0]+parseFloat(aa[1]);

                        aa=this.jobstore.getAt(tmp1).get("col2").split(":");
                        col1Total[1]=col1Total[1]+parseFloat(aa[0]);
                        col2Total[1]=col2Total[1]+parseFloat(aa[1]);

                        aa=this.jobstore.getAt(tmp1).get("col3").split(":");
                        col1Total[2]=col1Total[2]+parseFloat(aa[0]);
                        col2Total[2]=col2Total[2]+parseFloat(aa[1]);

                        aa=this.jobstore.getAt(tmp1).get("col4").split(":");
                        col1Total[3]=col1Total[3]+parseFloat(aa[0]);
                        col2Total[3]=col2Total[3]+parseFloat(aa[1]);

                        aa=this.jobstore.getAt(tmp1).get("col5").split(":");
                        col1Total[4]=col1Total[4]+parseFloat(aa[0]);
                        col2Total[4]=col2Total[4]+parseFloat(aa[1]);

                        aa=this.jobstore.getAt(tmp1).get("col6").split(":");
                        col1Total[5]=col1Total[5]+parseFloat(aa[0]);
                        col2Total[5]=col2Total[5]+parseFloat(aa[1]);

                        aa=this.jobstore.getAt(tmp1).get("col7").split(":");
                        col1Total[6]=col1Total[6]+parseFloat(aa[0]);
                        col2Total[6]=col2Total[6]+parseFloat(aa[1]);
                    }
                }
                aa =hours.split(":");
                for(var cnt = 0; cnt <= col1Total.length; cnt++) {
                    var hours1 = hours;
                    var col1Total1=col1Total[cnt]+parseFloat(aa[0]);
                    var col2Total2=col2Total[cnt]+parseFloat(aa[1]);
                    if(col2Total2>60)
                    {
                        col1Total1=(col1Total1+1)+parseFloat((col2Total2-60)/60);
                    }
                    if(parseFloat(aa[1])==60){
                        aa[0]=parseFloat(aa[0])+1;
                        if(aa[0]<10){
                            hours1='0'+aa[0]+':00'
                        }else{
                            hours1=aa[0]+':00'
                        }
                    }
                    if(((col1Total1*60)+col2Total2)>1440){
                        hours1 = "00:00";
                    }
                    this.jobstore.data.items[e.row].set('col'+(cnt+1),hours1);
                }
            }
        }
    },
    insertjob:function(){
        this.p=new this.jobRecord({
            jobtype:'',
            col1:'00:00',
            col2:'00:00',
            col3:'00:00',
            col4:'00:00',
            col5:'00:00',
            col6:'00:00',
            col7:'00:00',
            total:'00:00'
        });
        this.jobgrid.stopEditing();
        this.c=this.jobstore.getCount();        
        this.jobstore .insert(this.c,this.p);

    },    
    validEditFunction:function(e){
        var aa=[];
        aa =e.value.split(":");       
        var col1Total = 0;
        var col2Total=0;
        if(e.column==2||e.column==3||e.column==4||e.column==5||e.column==6||e.column==7||e.column==8)
        {
            if(aa[0]>24||aa[1]>=61)
            {
                calMsgBoxShow(144,0);
                return false;
            }
        }
        if(e.column == 1){
            if(this.jobstore.find("jobtype", new RegExp('^'+e.value.trim()+'$'))!=-1){
                calMsgBoxShow(224,0);
                return false;
            }
        }
        for(var tmp1=0;tmp1<this.jobstore.getCount();tmp1++){
            if(tmp1!=e.row)
            {
                if(e.column==2){
                    aa=this.jobstore.getAt(tmp1).get("col1").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
                if(e.column==3){
                    aa=this.jobstore.getAt(tmp1).get("col2").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
                if(e.column==4){
                    aa=this.jobstore.getAt(tmp1).get("col3").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
                if(e.column==5){
                    aa=this.jobstore.getAt(tmp1).get("col4").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
                if(e.column==6){
                    aa=this.jobstore.getAt(tmp1).get("col5").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
                if(e.column==7){
                    aa=this.jobstore.getAt(tmp1).get("col6").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
                if(e.column==8){
                    aa=this.jobstore.getAt(tmp1).get("col7").split(":");
                    col1Total=col1Total+parseFloat(aa[0]);
                    col2Total=col2Total+parseFloat(aa[1]);
                }
            }

        }
        aa=(e.value).split(":");
        col1Total=col1Total+parseFloat(aa[0]);
        col2Total=col2Total+parseFloat(aa[1]);
        if(col2Total>60)
        {
            col1Total=(col1Total+1)+parseFloat((col2Total-60)/60);
        }
        if(parseFloat(aa[1])==60){
            aa[0]=parseFloat(aa[0])+1;
            if(aa[0]<10){
            e.value='0'+aa[0]+':00'
            }else{
                e.value=aa[0]+':00'
            }
        }
        if(((col1Total*60)+col2Total)>1440)
        {
            calMsgBoxShow(80,0);
            return false;
        }
        return true;

    },
    changegridheader: function(){
        this.jobgrid.enable();
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
        this.dateArray.push(myDate.format('Y-m-d'))

        this.cm1.setColumnHeader(2,myDate.format('D d M Y'))
        for(i=2;i<8;i++)
        {
            myDate.setDate(myDate.getDate()+1);
            this.dateArray.push(myDate.format('Y-m-d'))            
            this.cm1.setColumnHeader(i+1,myDate.format('D d M Y'))
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
        this.jobstore.on("load",function(){
            if(msgFlag==1){
                WtfGlobal.closeProgressbar();
            }
            },this);
    },
    gridcalculation:function(isSubmittedFlag){
        var jsondata = "";

        for(var i=0;i<this.jobstore.getCount();i++){

            jsondata += "{'jobtype':'" + WtfGlobal.onlySinglequoateRenderer(this.jobstore.getAt(i).get("jobtype")) + "',";
            jsondata += "'col1':'" + (this.jobstore.getAt(i).get("col1")==""?"00:00":this.jobstore.getAt(i).get("col1")) + "',";
            jsondata += "'colid1':'" + this.jobstore.getAt(i).get("colid1") + "',";
            jsondata += "'col2':'" + (this.jobstore.getAt(i).get("col2")==""?"00:00":this.jobstore.getAt(i).get("col2")) + "',";
            jsondata += "'colid2':'" + this.jobstore.getAt(i).get("colid2") + "',";
            jsondata += "'col3':'" + (this.jobstore.getAt(i).get("col3")==""?"00:00":this.jobstore.getAt(i).get("col3")) + "',";
            jsondata += "'colid3':'" + this.jobstore.getAt(i).get("colid3") + "',";
            jsondata += "'col4':'" + (this.jobstore.getAt(i).get("col4")==""?"00:00":this.jobstore.getAt(i).get("col4")) + "',";
            jsondata += "'colid4':'" + this.jobstore.getAt(i).get("colid4") + "',";
            jsondata += "'col5':'" + (this.jobstore.getAt(i).get("col5")==""?"00:00":this.jobstore.getAt(i).get("col5")) + "',";
            jsondata += "'colid5':'" + this.jobstore.getAt(i).get("colid5") + "',";
            jsondata += "'col6':'" + (this.jobstore.getAt(i).get("col6")==""?"00:00":this.jobstore.getAt(i).get("col6")) + "',";
            jsondata += "'colid6':'" + this.jobstore.getAt(i).get("colid6") + "',";
            jsondata += "'col7':'" + (this.jobstore.getAt(i).get("col7")==""?"00:00":this.jobstore.getAt(i).get("col7")) + "',";
            jsondata += "'colid7':'" + this.jobstore.getAt(i).get("colid7") + "'},";
        }
        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);

        var modifiedwork=this.jobstore.getModifiedRecords();
        this.vflag=0;
        for(i=0;i<this.jobstore.getCount();i++)
        // for(i=0;i<modifiedwork.length;i++)
        {
            if(this.jobstore.getAt(i).get('jobtype')=="")
            // if(modifiedwork[i].get('jobtype')=="")
            {
                this.vflag=0;
                calMsgBoxShow(81,0);
                break;
            }else{
                this.vflag=1;

            }
        }
        var param={};
        if(this.empid)
        {
            param={
                flag:25,
                colHeader:this.dateArray,
                jsondata:finalStr,
                empid:this.empid,
                isSubmitted:isSubmittedFlag
            }
        }
        else
        {
            param=  {
                flag:25,
                colHeader:this.dateArray,
                jsondata:finalStr,
                isSubmitted:isSubmittedFlag
            }
        }
        if(this.vflag==1)
        {
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp",
                url: "Timesheet/insertTimeSheet.ts",
                params: param
            }, this,
            function(response){
                var res=eval('('+response+')');
                calMsgBoxShow([res[0].titleMsg,res[1].msg],0);
                var stdate=this.fromdate.getRawValue();
                this.jobstore.load({
                    params:{
                        empid:this.empid,
                        startdate:stdate,
                        enddate:this.todate.getValue()
                    }
                });
            },
            function(response)
            {
                calMsgBoxShow(82,1);                
            })
        }
    },   
    setduration:function(){
        this.changegridheader();

    },

    deletejob:function(btn){
        //if(btn!="yes") return;
        var rs=this.jobgrid.getSelectionModel().getSelections();
        var ids=[];
        this.sm.clearSelections();
        var store=this.jobgrid.getStore();
        var k=0;
        for(var i=0;i<rs.length;i++){
             var rec=store.indexOf(rs[i]);
             WtfGlobal.highLightRow(this.jobgrid,"FF0000",5, rec)
            if(rs[i].get('colid1'))
            {   
                for(var j=0;j<7;j++)
                {
                    ids[k]=rs[i].get("colid"+(j+1));
                    k++;
                }
            }
            else{
                store.remove(rs[i]);
            }
            
        }
        if(ids.length>0)
        {
            calMsgBoxShow(201,4,true);
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp",
                url: "Timesheet/deletetimesheetjobs.ts",
                params:{
                    flag:47,
                    ids:ids
                }
            }, this,
            function(response){
                var res=eval('('+response+')');
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),res.msg],0);
                var stdate=this.fromdate.getRawValue();
               var params={
                        empid:this.empid,
                        startdate:stdate,
                        enddate:this.todate.getValue()
                    }
                WtfGlobal.delaytasks(this.jobstore,params);
            },
            function(response)
            {
                calMsgBoxShow(49,1);
            })
        }
    },
    totaltime:function(a,b,c){
        var sum="00:00";
        var aa=[]
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
        return  hr+":"+mn+" "+WtfGlobal.getLocaleText("hrms.timesheet.hrs");//" hrs";
    },
    createTextField:function(){
        var reg=/^(([0-1][0-9]|[2][0-3]):([0-5][0-9]))|(([2][4]:[0][0]))|(([0-1][0-9]|[2][0-3]):([0-6][0]))$/;
        return new Wtf.form.TextField({
            regex:reg,
            allowBlank:'false',
            maxLength:5
        });
    }
}); 



Wtf.StartTimer = function (config){
    Wtf.apply(this,config);
    Wtf.StartTimer.superclass.constructor.call(this,{
        buttons:[{
                    text:WtfGlobal.getLocaleText("hrms.common.start"),
                    disabled:this.res.started,
                    handler:function (){
                        this.startFunction();
                    },
                    scope:this
                },{
                    text:WtfGlobal.getLocaleText("hrms.common.stop"),
                    disabled:!this.res.started,
                    handler:function (){
                		this.stopFunction();
                    },
                    scope:this
                },{
                    text:WtfGlobal.getLocaleText("hrms.common.reset"),
                    disabled:!this.res.started,
                    handler:function (){
                        this.resetFunction();
                    },
                    scope:this
                },{
                    text:WtfGlobal.getLocaleText("hrms.common.Close"),
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }]
    });
};

Wtf.extend(Wtf.StartTimer,Wtf.Window,{
    initComponent:function (){
        Wtf.StartTimer.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetAddEditForm();
        
        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[this.northPanel, this.AddEditForm]
        });
        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){
        var image='';
        if(this.action=="Edit"){
            windetail=WtfGlobal.getLocaleText("hrms.common.Editthemasterfieldinformation");
            image='../../images/master.gif';
        } else {
            windetail=WtfGlobal.getLocaleText("hrms.common.Filluptheinformationtoaddmasterfield");
            image='../../images/master.gif';
        }
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:70,
            border:false,
            bodyStyle:"background-color:white;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(WtfGlobal.getLocaleText("hrms.timesheet.job.timer"),WtfGlobal.getLocaleText("hrms.timesheet.job.timer.detail"),image)
        });
    },
    GetAddEditForm:function (){
    	this.timeExceeds = false;
    	this.jobitem = null;
    	if(this.isFreeText){
    		this.jobitem = new Wtf.ux.TextField({
                fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.job.name"),
                width:200,
                maxLength:100,
                name:"id",
                anchor:'70%',
                allowBlank:false
            });
    	}else{
    		this.jobitem = new Wtf.form.ComboBox({
                fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.select.job"),
                store:Wtf.jobtypeStore,
                displayField:'name',
                hiddenName:'id',
                valueField:'id',
                name:'id',
                scope:this,
                mode:'local',
                anchor:'70%',
                selectOnFocus:true,
                typeAhead:false,
                allowBlank:false,
                editable:false,
                triggerAction :'all',
                forceSelection:true
            });
    	}
    	
    	this.time=new Wtf.form.TextField({
           regex:/^(([0-1][0-9]|[2][0-3]):([0-5][0-9]))|(([2][4]:[0][0]))|(([0-1][0-9]|[2][0-3]):([0-6][0]))$/,
           fieldLabel:WtfGlobal.getLocaleText("hrms.timesheet.hours")+"*"+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.timesheet.enterhours.format")),
           width:200,
           allowBlank:false,
           name:"time",
           maxLength:5,
           value:"00:00",
           disabled:true,
           anchor:'70%'
        });
    	
    	if(this.res.jobname!=undefined){
			this.jobitem.setValue(this.res.jobname);
			this.jobitem.setDisabled(true);
		}

        Wtf.jobtypeStore.on('load', function (a,b,c){
            this.jobitem.setValue(this.res.jobname);
        },this);

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
            url:"Timesheet/setTimer.ts",
            items:[this.jobitem, this.time]
        });
    },   
    
    getBlankJson: function(){
    	var jsondata = "";

        jsondata += "{'jobtype':' ',";
        jsondata += "'col1':'00:00',";
        jsondata += "'colid1':'" + undefined + "',";
        jsondata += "'col2':'00:00',";
        jsondata += "'colid2':'" + undefined + "',";
        jsondata += "'col3':'00:00',";
        jsondata += "'colid3':'" + undefined + "',";
        jsondata += "'col4':'00:00',";
        jsondata += "'colid4':'" + undefined + "',";
        jsondata += "'col5':'00:00',";
        jsondata += "'colid5':'" + undefined + "',";
        jsondata += "'col6':'00:00',";
        jsondata += "'colid6':'" + undefined + "',";
        jsondata += "'col7':'00:00',";
        jsondata += "'colid7':'" + undefined + "'},";
        return jsondata;
    },
    
    startFunction:function (){
        if(this.AddEditForm.form.isValid()){
            this.AddEditForm.form.submit({
                params:{
                    flag:202
                },
                success:function(){
                	reloadAlerts();
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.success"),
                        msg:WtfGlobal.getLocaleText("hrms.timesheet.timer.started.successfully"),
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                    this.close();
                },
                failure:function (){
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.error"),
                        msg:WtfGlobal.getLocaleText("hrms.timesheet.error.starting.timer"),
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });
                    this.close();
                },
                scope:this
            });
        }
    },
    
    stopFunction: function(){
    	Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.save.data"), WtfGlobal.getLocaleText("hrms.timesheet.want.stop.timer"), function(btn){
			if(btn=="yes") {
				Wtf.Ajax.requestEx({
	                url: "Timesheet/stopTimer.ts",
	                params:{
    					colHeader:this.colHeader,
    	                jsondata:this.getBlankJson(),
    	                isSubmitted:false,
    	                startdate:this.startdate,
    	                enddate:this.enddate,
    	                time:this.time.getValue(),
    	                timeExceeds:this.timeExceeds
					}
	            }, this,
	            function(response){
	            	reloadAlerts();
	            	var res=eval('('+response+')');
	            	if(res.success){
	            		Wtf.MessageBox.show({
	                        title:WtfGlobal.getLocaleText("hrms.common.success"),
	                        msg:WtfGlobal.getLocaleText("hrms.timesheet.timer.stopped.successfully"),
	                        icon:Wtf.MessageBox.INFO,
	                        buttons:Wtf.MessageBox.OK
	                    });
	            		this.close();
	            	}else{
	            		this.time.setDisabled(false);
	            		this.timeExceeds = true;
	            		Wtf.MessageBox.show({
	                        title:WtfGlobal.getLocaleText("hrms.common.warning"),
	                        msg:WtfGlobal.getLocaleText("hrms.timesheet.total.working.hours.day.exceed.24hrs")+"<br>"+WtfGlobal.getLocaleText("hrms.timesheet.enter.working.hours.manually"),
	                        icon:Wtf.MessageBox.WARNING,
	                        buttons:Wtf.MessageBox.OK
	                    });
	            	}
	            },
	            function(response){
	            	Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.error"),
                        msg:WtfGlobal.getLocaleText("hrms.timesheet.error.stopping.timer"),
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });                
	            });
			}
		},this);
    },
    
    resetFunction: function(){
    	Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.save.data"), WtfGlobal.getLocaleText("hrms.timesheet.want.cancel.timer"), function(btn){
			if(btn=="yes") {
				Wtf.Ajax.requestEx({
	                url: "Timesheet/cancelTimer.ts",
	                params:{
    					colHeader:this.colHeader,
    	                jsondata:this.getBlankJson(),
    	                isSubmitted:false,
    	                startdate:this.startdate,
    	                enddate:this.enddate
					}
	            }, this,
	            function(response){
	            	Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.success"),
                        msg:WtfGlobal.getLocaleText("hrms.timesheet.timer.cancelled.successfully"),
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                    this.close();
	            },
	            function(response){
	            	Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("hrms.common.error"),
                        msg:WtfGlobal.getLocaleText("hrms.timesheet.error.cancelling.timer"),
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });                
	            });
			}
		},this);
    }
});
