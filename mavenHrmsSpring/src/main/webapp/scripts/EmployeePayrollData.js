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

Wtf.EmployeePayrollDataGrid = function(config){
    Wtf.EmployeePayrollDataGrid.superclass.constructor.call(this,config);
    config.title=WtfGlobal.getLocaleText("hrms.payroll.data");
};
Wtf.extend(Wtf.EmployeePayrollDataGrid,Wtf.Panel,{
    onRender : function(config){
        Wtf.EmployeePayrollDataGrid.superclass.onRender.call(this,config);
        
        
        this.generatedSalaryRecord = new Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"sdate"
        },
        {
            "name":"edate"
        },
        {
            "name":"desc"
        },
        {
            "name":"type"
        },
        {
            "name":"isadjust"
        },
        {
            "name":"isdefault"
        },
        {
            "name":"isblock"
        },
        {
            "name":"istaxablecomponent"
        },
        {
            "name":"frequency"
        },
        {
            "name":"costcenter"
        },
        {
            "name":"paymentterm"
        },
        {
            "name":"amount"
        },
        {
            "name":"basevalue"
        }
        ]);

        this.groupingView = new Wtf.grid.GroupingView({
            forceFit: true,
            showGroupName: true,
            enableGroupingMenu: true,
            groupTextTpl: WtfGlobal.getLocaleText("hrms.payroll.PayCycle")+' ({gvalue} '+WtfGlobal.getLocaleText("hrms.common.to.small")+' {[values.rs[0].data.edate]}) ({[values.rs.length]} {[values.rs.length > 1 ? "'+WtfGlobal.getLocaleText("hrms.payroll.components")+'" : "'+WtfGlobal.getLocaleText("hrms.payroll.component")+'"]})',
            hideGroupedColumn: false
        });
        this.generatedSalaryStore = new Wtf.data.GroupingStore({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.generatedSalaryRecord),
            baseParams: {
                flag: 101
            },
            url : "Payroll/Date/getSalaryComponentsForEmployee.py",
            sortInfo: {
                field: 'sdate',
                direction: "DESC"
            }
        });
        calMsgBoxShow(202,4,true);
        this.generatedSalaryStore.load({
            scope:this,
            params:{
                start:0,
                limit:15,
                showAll:false,
                startdate:this.startdate,
                enddate:this.enddate,
                userid:this.empid
            }
        });
        
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        this.gridcmodel= new Wtf.grid.ColumnModel([
            this.selectionModel,
        {
                dataIndex: 'compid',
                hidden: true,
                groupable: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.code"),
                dataIndex: 'code'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.description"),
                dataIndex: 'desc',
                groupRenderer: WtfGlobal.nameRenderer,
                renderer:function(val){
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                    return "<span style='white-space:pre-wrap;'>"+val+"</span>";
                }
            },
            {
                header:WtfGlobal.getLocaleText("hrms.payroll.Amount"),
                dataIndex:'amount',
                renderer:function(val){
                    if(val=="" || val ==null)
                        return WtfGlobal.currencyRenderer(0);
                    else
                        return WtfGlobal.currencyRenderer(val);
                }
            },
            {
                header:WtfGlobal.getLocaleText("hrms.payroll.paycycle.start.date"),
                dataIndex: 'sdate',
                sortable: true,
                groupable: true
            },
            {
                header:WtfGlobal.getLocaleText("hrms.payroll.paycycle.end.date"),
                dataIndex: 'edate'
            }],this);

        var paybtns=this.getToolbarArray();

        this.summary = new Wtf.ux.grid.GridSummary();
        this.generatedSalaryListGrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            border:false,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.search.component.code"),
            searchField:"code",
            serverSideSearch:true,
            view: this.groupingView,
            layout:'fit',
            store: this.generatedSalaryStore,
            displayInfo:true,
            cm: this.gridcmodel,
            scope:this,
            width:400,
            sm: new Wtf.grid.CheckboxSelectionModel({
                scope:this,
                listeners:{
                    scope:this,
                    rowselect :function(sm,index,record){
                    }
                }
            }),
            tbar:paybtns
        });

        
        this.add(this.generatedSalaryListGrid);
        toggleBttn.on('toggle', this.onGroupBttnClick, this);
        
        this.generatedSalaryStore.on("load",function(){
            WtfGlobal.closeProgressbar();
            if(toggleBttn.pressed){
                this.generatedSalaryListGrid.getStore().groupBy(this.generatedSalaryListGrid.getStore().getSortState().field);
            }

        },this);
        
    },
    onGroupBttnClick: function(bttnobj, isPressed){
        if (isPressed){
            this.generatedSalaryStore.load({
            scope:this,
            params:{
                    start:0,
                    limit:15,
                    showAll:true,
                    startdate:this.startdate,
                    enddate:this.enddate,
                    userid:this.empid
                }
            });
            
        }else{
            this.generatedSalaryListGrid.store.clearGrouping();
            this.generatedSalaryStore.load({
            scope:this,
            params:{
                    start:0,
                    limit:15,
                    showAll:false,
                    startdate:this.startdate,
                    enddate:this.enddate,
                    userid:this.empid
                }
            });
           
        }
            
    },
    getToolbarArray : function(){

        var btns=[];
        btns.push('-');
        toggleBttn = new Wtf.Button ({
            text: WtfGlobal.getLocaleText("hrms.payroll.show.all"),
            iconCls:'pwndCommon reportbuttonIcon',
            tooltip: {
                text:WtfGlobal.getLocaleText("hrms.payroll.click.all.salaries.selected.employee")
            },
            enableToggle: true
        })
        btns.push(toggleBttn);

        return btns;
        
    },

    getBottomToolbarButtons :function(){

        var bbtns =[];

        this.zoom=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.zoom"),
            scope:this,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :WtfGlobal.getLocaleText("hrms.payroll.click.display.list.details.related.selected.entries")
        });

        bbtns.push(this.zoom);
        bbtns.push('-');

        this.resource=new Wtf.Button({
            text:'Resource',
            scope:this,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :"Click to display the selected resource card.",
            handler : function(){
                
            }
        });

        bbtns.push(this.resource);
        bbtns.push('-');

        this.schedule=new Wtf.Button({
            text:'Schedule',
            scope:this,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :"Click to display the work schedule of the selected resource.",
            handler : function(){
                
            }
        });

        bbtns.push(this.schedule);
        bbtns.push('-');

        this.hours=new Wtf.Button({
            text:'Hours',
            scope:this,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :"Click to display an hour entry application.",
            handler : function(){
                
            }
        });

        bbtns.push(this.hours);
        bbtns.push('-');

        
        this.reviewingDetails=new Wtf.Button({
            text:'Reviewing Details',
            scope:this,
            iconCls:getButtonIconCls(Wtf.btype.downloadbutton),
            tooltip :"Click to delete the generated, authorized or processed payroll entries. ",
            handler : function(){
                
            }
        });

        bbtns.push(this.reviewingDetails);

        return bbtns;
    }

});



Wtf.PayrollComponentDataGrid = function(config){
    Wtf.PayrollComponentDataGrid.superclass.constructor.call(this,config);
};
Wtf.extend(Wtf.PayrollComponentDataGrid,Wtf.Panel,{
    onRender : function(config){
        Wtf.PayrollComponentDataGrid.superclass.onRender.call(this,config);

        if(this.frequency==0){
            this.GetMonthlyRecord();
            this.GetMonthlyGridColumnModel()
        } else if(this.frequency==1){
            this.GetWeeklyRecord();
            this.GetWeeklyGridColumnModel()
        } else if(this.frequency==2){
            this.GetTwiceInMonthRecord();
            this.GetTwiceInMonthGridColumnModel()
        }
        
        this.generatedSalaryStore = new Wtf.data.GroupingStore({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalcount"
            },this.generatedSalaryRecord),
            baseParams: {
                flag: 101
            },
            url : "Payroll/Date/getYearlySalaryComponentsForEmployee.py"
        });

       
        this.generatedSalaryListGrid= new Wtf.KwlGridPanel({
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            border:false,
            loadMask:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.search.component.code"),
            searchField:"code",
            serverSideSearch:true,
            layout:'fit',
            store: this.generatedSalaryStore,
            displayInfo:true,
            cm: this.gridcmodel,
            scope:this,
            width:800,
            autoScroll:true,
            viewConfig: {
                forceFit: false
            }

        });


        this.add(this.generatedSalaryListGrid);
         this.generatedSalaryStore.on('beforeload',function(store,option){
            option.params= option.params||{};
           // option.params.start=0;
            option.params.limit=this.generatedSalaryListGrid.pag.pageSize;
            option.params.startdate=this.startdate;
            option.params.enddate=this.enddate;
            option.params.userid=this.empid;
            option.params.frequency=this.frequency;
            option.params.year=this.year;
        },this);

        calMsgBoxShow(202,4,true);
        this.generatedSalaryStore.load({
            params:{
                	start:0

            },
            scope:this
        });

        this.generatedSalaryStore.on("load",function(){
            WtfGlobal.closeProgressbar();
            
        },this);

    },

    GetMonthlyRecord : function(){

        this.generatedSalaryRecord = new Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"jan"
        },
        {
            "name":"feb"
        },
        {
            "name":"mar"
        },
        {
            "name":"apr"
        },
        {
            "name":"may"
        },
        {
            "name":"jun"
        },
        {
            "name":"jul"
        },
        {
            "name":"aug"
        },
        {
            "name":"sep"
        },
        {
            "name":"oct"
        },
        {
            "name":"nov"
        },
        {
            "name":"dec"
        }
        ]);

        return this.generatedSalaryRecord;
        
    },

    GetMonthlyGridColumnModel : function(){

        this.rowNo=new Wtf.grid.RowNumberer();
        this.gridcmodel= new Wtf.grid.ColumnModel([
           this.rowNo,
        {
                dataIndex: 'compid',
                hidden: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.code"),
                dataIndex: 'code'
            },
            {
                header: WtfGlobal.getLocaleText("hrms.January"),
                dataIndex: 'jan',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: WtfGlobal.getLocaleText("hrms.February"),
                dataIndex: 'feb',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: WtfGlobal.getLocaleText("hrms.March"),
                dataIndex: 'mar',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: WtfGlobal.getLocaleText("hrms.April"),
                dataIndex: 'apr',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: WtfGlobal.getLocaleText("hrms.May"),
                dataIndex: 'may',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: WtfGlobal.getLocaleText("hrms.June"),
                dataIndex: 'jun',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: WtfGlobal.getLocaleText("hrms.July"),
                dataIndex: 'jul',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.August"),
                dataIndex: 'aug',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.September"),
                dataIndex: 'sep',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.October"),
                dataIndex: 'oct',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.November"),
                dataIndex: 'nov',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header:WtfGlobal.getLocaleText("hrms.December"),
                dataIndex:'dec',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            }],this);

    }
    ,

    GetTwiceInMonthRecord : function(){

        this.generatedSalaryRecord = new Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"p1"
        },
        {
            "name":"p2"
        },
        {
            "name":"p3"
        },
        {
            "name":"p4"
        },
        {
            "name":"p5"
        },
        {
            "name":"p6"
        },
        {
            "name":"p7"
        },
        {
            "name":"p8"
        },
        {
            "name":"p9"
        },
        {
            "name":"p10"
        },
        {
            "name":"p11"
        },
        {
            "name":"p12"
        },
        {
            "name":"p13"
        },
        {
            "name":"p14"
        },
        {
            "name":"p15"
        },
        {
            "name":"p16"
        },
        {
            "name":"p17"
        },
        {
            "name":"p18"
        },
        {
            "name":"p19"
        },
        {
            "name":"p20"
        },
        {
            "name":"p21"
        },
        {
            "name":"p22"
        },
        {
            "name":"p23"
        },
        {
            "name":"p24"
        }
        ]);

        return this.generatedSalaryRecord;

    },

    GetTwiceInMonthGridColumnModel : function(){

        this.rowNo=new Wtf.grid.RowNumberer();
        this.gridcmodel= new Wtf.grid.ColumnModel([
           this.rowNo,
        {
                dataIndex: 'compid',
                hidden: true
            },
            {
                header: "Code",
                dataIndex: 'code'
            },
            {
                header: "Period 1",
                dataIndex: 'p1',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 2",
                dataIndex: 'p2',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
               header: "Period 3",
                dataIndex: 'p3',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 4",
                dataIndex: 'p4',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 5",
                dataIndex: 'p5',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 6",
                dataIndex: 'p6',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 7",
                dataIndex: 'p7',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 8",
                dataIndex: 'p8',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 9",
                dataIndex: 'p9',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 10",
                dataIndex: 'p10',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 11",
                dataIndex: 'p11',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 12",
                dataIndex:'p12',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 13",
                dataIndex: 'p13',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 14",
                dataIndex: 'p14',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
               header: "Period 15",
                dataIndex: 'p15',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 16",
                dataIndex: 'p16',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 17",
                dataIndex: 'p17',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 18",
                dataIndex: 'p18',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 19",
                dataIndex: 'p19',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 20",
                dataIndex: 'p20',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 21",
                dataIndex: 'p21',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 22",
                dataIndex: 'p22',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 23",
                dataIndex: 'p23',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 24",
                dataIndex:'p24',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            }],this);

    },
    
    GetWeeklyRecord : function(){


        this.generatedSalaryRecord = new Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"p1"
        },
        {
            "name":"p2"
        },
        {
            "name":"p3"
        },
        {
            "name":"p4"
        },
        {
            "name":"p5"
        },
        {
            "name":"p6"
        },
        {
            "name":"p7"
        },
        {
            "name":"p8"
        },
        {
            "name":"p9"
        },
        {
            "name":"p10"
        },
        {
            "name":"p11"
        },
        {
            "name":"p12"
        },
        {
            "name":"p13"
        },
        {
            "name":"p14"
        },
        {
            "name":"p15"
        },
        {
            "name":"p16"
        },
        {
            "name":"p17"
        },
        {
            "name":"p18"
        },
        {
            "name":"p19"
        },
        {
            "name":"p20"
        },
        {
            "name":"p21"
        },
        {
            "name":"p22"
        },
        {
            "name":"p23"
        },
        {
            "name":"p24"
        },
        {
            "name":"p25"
        },
        {
            "name":"p26"
        },
        {
            "name":"p27"
        },
        {
            "name":"p28"
        },
        {
            "name":"p29"
        },
        {
            "name":"p30"
        },
        {
            "name":"p31"
        },
        {
            "name":"p32"
        },
        {
            "name":"p33"
        },
        {
            "name":"p34"
        },
        {
            "name":"p35"
        },
        {
            "name":"p36"
        },
        {
            "name":"p37"
        },
        {
            "name":"p38"
        },
        {
            "name":"p39"
        },
        {
            "name":"p40"
        },
        {
            "name":"p41"
        },
        {
            "name":"p42"
        },
        {
            "name":"p43"
        },
        {
            "name":"p44"
        },
        {
            "name":"p45"
        },
        {
            "name":"p46"
        },
        {
            "name":"p47"
        },
        {
            "name":"p48"
        },
        {
            "name":"p49"
        },
        {
            "name":"p50"
        },
        {
            "name":"p51"
        },
        {
            "name":"p52"
        }
        ]);

        return this.generatedSalaryRecord;

    },

    GetWeeklyGridColumnModel : function(){

        this.rowNo=new Wtf.grid.RowNumberer();
        this.gridcmodel= new Wtf.grid.ColumnModel([
           this.rowNo,
        {
                dataIndex: 'compid',
                hidden: true
            },
            {
                header: "Code",
                dataIndex: 'code'
            },
            {
                header: "Period 1",
                dataIndex: 'p1',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 2",
                dataIndex: 'p2',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
               header: "Period 3",
                dataIndex: 'p3',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 4",
                dataIndex: 'p4',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 5",
                dataIndex: 'p5',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 6",
                dataIndex: 'p6',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 7",
                dataIndex: 'p7',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 8",
                dataIndex: 'p8',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 9",
                dataIndex: 'p9',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 10",
                dataIndex: 'p10',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 11",
                dataIndex: 'p11',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 12",
                dataIndex:'p12',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 13",
                dataIndex: 'p13',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 14",
                dataIndex: 'p14',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
               header: "Period 15",
                dataIndex: 'p15',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 16",
                dataIndex: 'p16',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 17",
                dataIndex: 'p17',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 18",
                dataIndex: 'p18',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 19",
                dataIndex: 'p19',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 20",
                dataIndex: 'p20',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 21",
                dataIndex: 'p21',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 22",
                dataIndex: 'p22',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 23",
                dataIndex: 'p23',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 24",
                dataIndex:'p24',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },{
                header: "Period 25",
                dataIndex: 'p25',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 26",
                dataIndex: 'p26',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
               header: "Period 27",
                dataIndex: 'p27',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 28",
                dataIndex: 'p28',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 29",
                dataIndex: 'p29',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 30",
                dataIndex: 'p30',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 31",
                dataIndex: 'p31',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 32",
                dataIndex: 'p32',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 33",
                dataIndex: 'p33',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 34",
                dataIndex: 'p34',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 35",
                dataIndex: 'p35',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 36",
                dataIndex:'p36',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 37",
                dataIndex: 'p37',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 38",
                dataIndex: 'p38',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
               header: "Period 39",
                dataIndex: 'p39',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 40",
                dataIndex: 'p40',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 41",
                dataIndex: 'p41',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 42",
                dataIndex: 'p42',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },

            {
                header: "Period 43",
                dataIndex: 'p43',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 44",
                dataIndex: 'p44',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 45",
                dataIndex: 'p45',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 46",
                dataIndex: 'p46',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 47",
                dataIndex: 'p47',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 48",
                dataIndex:'p48',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },{
                header: "Period 49",
                dataIndex: 'p49',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 50",
                dataIndex: 'p50',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 51",
                dataIndex: 'p51',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            },
            {
                header: "Period 52",
                dataIndex: 'p52',
                renderer:function(val){
                    return WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2));
                }
            }],this);

    }

});
