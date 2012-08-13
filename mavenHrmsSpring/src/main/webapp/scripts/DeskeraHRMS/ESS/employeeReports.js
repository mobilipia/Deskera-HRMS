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
Wtf.NewJoineesReport = function(config){
    Wtf.NewJoineesReport.superclass.constructor.call(this,config);
    config.autoScroll=false;
};
Wtf.extend(Wtf.NewJoineesReport,Wtf.Panel,{
    onRender : function(config){
        Wtf.NewJoineesReport.superclass.onRender.call(this,config);

        this.fromdaterep=new Wtf.form.DateField({
            width:155,
            //            id:'fromdaterep',
            scope:this,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getFirstDateOfMonth()
        });
        this.todaterep= new Wtf.form.DateField({
            width:155,
            readOnly:true,
            //            id:'todaterep',
            scope:this,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getLastDateOfMonth()
        });


        var st=this.fromdaterep.getValue().format('m/d/Y');
        var end=this.todaterep.getValue().format('m/d/Y');

        this.record = Wtf.data.Record.create([
        {
            name: 'userid'
        },

        {
            name: 'username'
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
            name: 'fname'
        },

        {
            name: 'lname'
        },

        {
            name: 'image'
        },

        {
            name: 'emailid'
        },

        {
            name: 'lastlogin',
            type: 'date'
        },

        {
            name: 'aboutuser'
        },

        {
            name: 'address'
        },

        {
            name:'contactno'
        },

        {
            name:'manager'
        },
        {
            name:'managerid'
        },

        {
            name:'salary'
        },

        {
            name:'roleid',
            mapping:'role'
        },

        {
            name:'accno'
        },

        {
            name: 'rolename'
        },

        {
            name: 'employeeid'
        },

        {
            name:'status'
        },
        {
            name:'fullname'
        },
        {
            name:'reviewer'
        },
        {
            name:'reviewerid'
        },
        {
            name:'templateid'
        },
        {
            name:'joindate',
            type:'date'
        }
        ]);


        this.empGDS =new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.record),
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/getAllUserDetailsHrms.common",
            baseParams:{
                mode:114,
                stdate:st,
                enddate:end
            }
        });
        this.empGDS.load();
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'employeeid',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username',
                autoWidth : true,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.designation"),
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
                header :WtfGlobal.getLocaleText("hrms.common.ProfileStatus"),
                dataIndex: 'status',
                autoSize : true,
                sortable: true,
                groupable: true
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.AssignedAppraiser"),
                dataIndex: 'manager',
                autoSize : true,
                sortable: true,
                groupable: true
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.AssignedReviewer"),
                dataIndex: 'reviewer',
                autoSize : true,
                sortable: true,
                groupable: true
            },  {
                header :WtfGlobal.getLocaleText("hrms.recruitment.joining.date"),
                dataIndex: 'joindate',
                renderer:WtfGlobal.onlyDateRenderer,
                autoSize : true,
                sortable: true,
                groupable: true
            }
            ]);

        this.allempGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'qualifiedgr',
            store: this.empGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            searchField:"fullname",
            tbar:[WtfGlobal.getLocaleText("hrms.common.start.date")+':',this.fromdaterep,WtfGlobal.getLocaleText("hrms.common.end.date"),this.todaterep,
            '-',{
                text:WtfGlobal.getLocaleText("hrms.timesheet.generate.report"),
                minWidth:100,
                scope:this,
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                handler:function()
                {
                    var st=this.fromdaterep.getValue().format('m/d/Y');
                    var end=this.todaterep.getValue().format('m/d/Y');
                    this.empGDS.removeAll();
                    this.empGDS.baseParams={
                        mode:114,
                        stdate:st,
                        enddate:end
                    }
                    calMsgBoxShow(202,4,true);
                    this.empGDS.load({
                        scope:this,
                        params:{
                            start:0,
                            limit:this.allempGrid.pag.pageSize
                        }
                    });
                    this.empGDS.on("load",function(){
                        WtfGlobal.closeProgressbar()
                    },this);
                }
            }
            ],
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.common.Nonewemployeesforselectedduration"))
            }
        });

        this.UsergridPanel2  = new Wtf.Panel({
            border:false,
            autoLoad:false,
            paging:false,
            layout:'fit',
            items:[this.allempGrid]
        });
        this.innerpanel2 = new Wtf.Panel({
            layout : 'fit',
            cls : 'backcolor',
            border :false,
            items:[this.UsergridPanel2]
        });
        this.add(this.innerpanel2);
    }
});

Wtf.DesigChngReport = function(config){
    Wtf.DesigChngReport.superclass.constructor.call(this,config);
    config.autoScroll=false;
};
Wtf.extend(Wtf.DesigChngReport,Wtf.Panel,{
    onRender : function(config){
        Wtf.DesigChngReport.superclass.onRender.call(this,config);

        this.fromdaterep=new Wtf.form.DateField({
            width:155,
            //            id:'fromdaterep',
            scope:this,
            readOnly:true,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.fromdate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getFirstDateOfMonth()
        });
        this.todaterep= new Wtf.form.DateField({
            width:155,
            readOnly:true,
            //            id:'todaterep',
            scope:this,
            emptyText:WtfGlobal.getLocaleText("hrms.timesheet.todate"),
            format:'m/d/Y',
            value:new Date().add(Date.MONTH,+0).getLastDateOfMonth()
        });


        var st=this.fromdaterep.getValue().format('m/d/Y');
        var end=this.todaterep.getValue().format('m/d/Y');

        this.record = Wtf.data.Record.create([
        {
            name: 'userid'
        },

        {
            name: 'username'
        },

        {
            name: 'olddesignation'
        },

        {
            name: 'newdesignation'
        },

        {
            name: 'newdepartmentname'
        },

        {
            name: 'olddepartmentname'
        },
        {
            name:'fullname'
        },
        {
            name: 'employeeid'
        },
        {
            name:'updatedate',
            type:'date'
        }
        ]);


        this.empGDS =new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.record),
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/getPromotedEmp.common",
            baseParams:{
                mode:117,
                stdate:st,
                enddate:end
            }
        });
        this.empGDS.load();
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'employeeid',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username',
                autoWidth : true,
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.OldDepartment"),
                dataIndex: 'olddepartmentname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.OldDesignation"),
                dataIndex: 'olddesignation',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.CurrentDepartment"),
                dataIndex: 'newdepartmentname',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.CurrentDesignation"),
                dataIndex: 'newdesignation',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.Updatedon"),
                dataIndex: 'updatedate',
                renderer:WtfGlobal.onlyDateRenderer,
                autoSize : true,
                sortable: true,
                groupable: true
            }
            ]);

        this.allempGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'deschnggr',
            store: this.empGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            searchField:"fullname",
            tbar:[WtfGlobal.getLocaleText("hrms.common.start.date")+':',this.fromdaterep,WtfGlobal.getLocaleText("hrms.common.end.date"),this.todaterep,
            '-',{
                text:WtfGlobal.getLocaleText("hrms.timesheet.generate.report"),
                minWidth:100,
                scope:this,
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                handler:function()
                {
                    var st=this.fromdaterep.getValue().format('m/d/Y');
                    var end=this.todaterep.getValue().format('m/d/Y');
                    this.empGDS.removeAll();
                    this.empGDS.baseParams={
                        mode:117,
                        stdate:st,
                        enddate:end
                    }
                    calMsgBoxShow(202,4,true);
                    this.empGDS.load({
                        scope:this,
                        params:{
                            start:0,
                            limit:this.allempGrid.pag.pageSize
                        }
                    });
                    this.empGDS.on("load",function(){
                        WtfGlobal.closeProgressbar()
                    },this);
                }
            }
            ],
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.common.Norecordsforselectedduration"))
            }
        });

        this.UsergridPanel2  = new Wtf.Panel({
            border:false,
            autoLoad:false,
            paging:false,
            layout:'fit',
            items:[this.allempGrid]
        });
        this.innerpanel2 = new Wtf.Panel({
            layout : 'fit',
            cls : 'backcolor',
            border :false,
            items:[this.UsergridPanel2]
        });
        this.add(this.innerpanel2);
    }
});

