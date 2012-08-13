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
Wtf.exemp = function(config){
    Wtf.apply(this, config);
    Wtf.exemp.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.exemp, Wtf.Panel, {
    initComponent: function(){
        Wtf.exemp.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.exemp.superclass.onRender.call(this,config);
        this.exempGrid();
        this.add(this.allexempGrid);

    },
    exempGrid:function(){

        this.sm = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.record = Wtf.data.Record.create([
        {
            name: 'userid'
        },       
        {
            name: 'designation'
        },

        {
            name: 'designationid'
        },

        {
            name: 'department'
        },

        {
            name: 'departmentname'
        },       
        {
            name: 'emailid'
        },       
        {
            name:'contactno'
        },      
        {
            name: 'employeeid'
        },      
        {
            name:'fullname'
        },
        {
            name:'termdate',
            type:'date'
        },
        {
            name:'termreason'
        },
        {
            name:'termdesc'
        },
        {
            name:'termby'
        }
        ]);

        this.fromdate=new Wtf.form.DateField({
                width:135,
                readOnly:true,
                emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
                format:'m/d/Y',
                value:new Date().add(Date.MONTH,+0).getFirstDateOfMonth()
            });
            this.todate= new Wtf.form.DateField({
                width:135,
                readOnly:true,
                emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
                disabled:true,
                format:'m/d/Y',
                value:new Date().add(Date.MONTH,+0).getLastDateOfMonth()
            });
            this.fromdate.on('change',function(){
                var myDate=new Date();
                myDate=this.fromdate.getValue();
                this.todate.setValue(myDate.add(Date.MONTH,+0).getLastDateOfMonth());
            },this);

        this.exempGDS =new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.record),
            url:"Common/getexEmployees.common",
            baseParams:{
                mode:115
            }
        });
        this.exempGDS.load();
        this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'employeeid',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.EmailAddress"),
                dataIndex: 'emailid',
                autoSize : true,
                sortable: true,
                renderer: WtfGlobal.renderEmailTo,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.DateofLeaving"),
                dataIndex: 'termdate',
                renderer:WtfGlobal.onlyDateRenderer,
                sortable: true                
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.ReasonforLeaving"),
                dataIndex: 'termreason',                
                sortable: true
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.ReasonDescription"),
                dataIndex:'termdesc',
                sortable: true
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.TerminatedBy"),
                dataIndex: 'termby',
                sortable: true
            }
            ]);

        var empbtns=new Array();
        
            empbtns.push('-',new Wtf.Toolbar.Button({
         		text:WtfGlobal.getLocaleText("hrms.common.reset"),
         		scope: this,
         		iconCls:'pwndRefresh',
         		handler:function(){
            		this.exempGDS.load({params:{grouper:'usergrid',start:0,limit:this.allexempGrid.pag.pageSize}});
            		Wtf.getCmp("Quick"+this.allexempGrid.id).setValue("");
            	}
         	}),'-',
            new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.view.edit.profile"),
                iconCls:getButtonIconCls(Wtf.btype.viewbutton),
                tooltip:WtfGlobal.getLocaleText("hrms.common.ViewEditProfile.tooltip2"),
                id:this.id+'viewprofilerejected',
                disabled:true,
                scope:this,
                handler:this.viewprofile
            }),'-',
            new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.Rehire"),
                iconCls:'pwnd rehireIcon',
                tooltip:WtfGlobal.getLocaleText("hrms.common.Rehire.tooltip"),
                disabled:true,
                scope:this,
                handler:this.rehirefun
            }),'-',
            this.fromdate,'-',
            this.todate,'-',
            new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.GenerateMonthlyReport"),
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                tooltip:WtfGlobal.getLocaleText("hrms.common.GenerateMonthlyReport.tooltip"),
                disabled:false,
                scope:this,
                handler:this.monthlyreport
            })
            );
        

        this.allexempGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'exempdgr',
            store: this.exempGDS,
            cm: this.cm,
            sm: this.sm,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            serverSideSearch:true,
            searchField:"fullname",
            tbar:empbtns,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.common.Noexemployeerecordtoshow"))
            }
        });     
        this.sm.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(empbtns, this.allexempGrid, [3],[5]);
        },this);

    },

    viewprofile:function(){
        var perm=false;
        var rec=this.sm.getSelections() ;
        var empname=(rec[0].get('fullname'));
        var empid=rec[0].get('userid');
        var main=Wtf.getCmp("empmanagement");
        var edsignupTab=Wtf.getCmp("empmntprofile"+empid);
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.myProfileWindow({
                title: WtfGlobal.getLocaleText({key:"hrms.common.empsProfile",params:[empname]}),
                closable:true,
                id:'empmntprofile'+empid,
                layout:'fit',
                editperm:perm,
                exemp:true,
                lid:empid,
                manager:true,
                report:false,
                border:false,
                iconCls:'pwnd myProfileIcon'
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    rehirefun:function(){
        this.rehire=new Wtf.rehire({
                iconCls:getButtonIconCls(Wtf.btype.winicon),
                layout:'fit',
                closable:true,
                resizable:false,
                title:WtfGlobal.getLocaleText("hrms.common.Rehire"),
                border:false,
                grids:this.allexempGrid,
                id:this.id+'rehirewindow',
                modal:true,
                scope:this,
                plain:true
            });
        this.rehire.show()
    },
    monthlyreport:function(){
        var myDate=new Date();
        var myDate1=new Date();

        myDate1=this.todate.getValue();
        myDate=this.fromdate.getValue();
        this.fromdate.setValue(myDate.format('Y-m-d'));
        this.todate.setValue(myDate1.format('Y-m-d'));
        var stdate=this.fromdate.getRawValue();
        var enddate=this.todate.getRawValue();

        this.exempGDS.baseParams={
                mode:115,
                startdate:stdate,
                enddate:enddate
        }
        this.exempGDS.load();
    }
});


Wtf.emphistory = function(config){
    Wtf.apply(this, config);
    Wtf.emphistory.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.emphistory, Wtf.Panel, {
    initComponent: function(){
        Wtf.emphistory.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.emphistory.superclass.onRender.call(this,config);
        this.emphstGrid();
        this.add(this.emphistGrid);

    },
    emphstGrid:function(){

        this.sm2 = new Wtf.grid.RowSelectionModel({
            singleSelect:false
        });
        this.hstrec = Wtf.data.Record.create([
        {
            name: 'hid'
        },
        {
            name: 'designation'
        },

        {
            name: 'department'
        },

        {
            name: 'startdate',
            type:'date'
        },

        {
            name: 'enddate',
            type:'date'
        },
        {
            name: 'salary'
        },
        {
            name:'category'
        }
        ]);


        this.hstGDS =new Wtf.data.GroupingStore({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.hstrec),
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/getEmpHistory.common",
            baseParams:{
                mode:116,
                userid:this.lid
            },
            sortInfo:{
                field: 'category',
                direction: "ASC"
            },
            groupField:'category'
        });
        this.hstGDS.load();
        this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),         
           {
                header: WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'department',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.Salary"),
                dataIndex: 'salary',
                autoSize : true,
               // sortable: true,
                renderer:WtfGlobal.currencyRenderer,
                groupable: true
            },  {
                header : WtfGlobal.getLocaleText("hrms.common.start.date"),
                dataIndex: 'startdate',
                renderer:WtfGlobal.onlyDateRenderer
               // sortable: true
            },  {
                header : WtfGlobal.getLocaleText("hrms.common.end.date"),
                dataIndex: 'enddate',
                renderer:WtfGlobal.onlyDateRenderer
               // sortable: true
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.category"),
                dataIndex: 'category'               
               // sortable: true
            }
            ]);

        this.emphistGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'emphstgr',
            store: this.hstGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            noSearch:true,      
            view:new Wtf.grid.GroupingView({
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.common.Nohistoryrecordtoshow")),
                hideGroupedColumn :true,
                groupTextTpl:'{text} ({[values.rs.length]} {[values.rs.length > 1 ? "'+WtfGlobal.getLocaleText("hrms.common.records")+'" : "'+WtfGlobal.getLocaleText("hrms.common.record")+'"]})'
            })
        });

    }
}); 


