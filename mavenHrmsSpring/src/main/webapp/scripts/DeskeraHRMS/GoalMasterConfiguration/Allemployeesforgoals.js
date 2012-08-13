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
Wtf.allemployeegoals= function(config){
    Wtf.allemployeegoals.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.allemployeegoals, Wtf.Panel, {
    initComponent: function() {
        Wtf.allemployeegoals.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.allemployeegoals.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
        {
            name:'userid'
        },

        {
            name:'fname'
        },
        {
            name:'lname'
        },
        {
            name:'emailid'
        },
        {
            name:'designation'
        },
        {
            name:'contactno'
        },
        {
            name:'fullname'
        }
        ]);
        this.reader= new Wtf.data.KwlJsonReader1({
            root: 'data',
            totalProperty:'count'
        },
        this.record
        );

        this.ds = new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Common/getEmpForManagerFunction.common",
            baseParams: {
                flag: 141,
                paging:true
            },
            reader:this.reader

        });
        calMsgBoxShow(202,4,true);
        this.ds.load();
        this.ds.on("load",function(){
            WtfGlobal.closeProgressbar()
        },this);

        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true
        });
        this.cm = new Wtf.grid.ColumnModel([
            this.sm,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),//"Employee Name",
                dataIndex: 'fullname',
                sortable: true,
                renderer : function(val) {
            		return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.employee.assign.goal")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select Employee to assign goal(s)\" wtf:qtitle='Description'>"+val+"</div>";
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.employee.designation"),//"Employee Designation",
                dataIndex: 'designation',
                sortable: true,
                renderer : function(val) {
            	return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.employee.assign.goal")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select Employee to assign goal(s)\" wtf:qtitle='Description'>"+val+"</div>";
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.employee.email"),//"Employee Email",
                dataIndex: 'emailid',
                sortable: true,
                renderer: WtfGlobal.renderEmailTo
            },
            {
                header:WtfGlobal.getLocaleText("hrms.performance.employee.contact"),//'Employee Contact',
                dataIndex: 'contactno',
                sortable: true,
                renderer : function(val) {
            	return "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.performance.employee.assign.goal")+"\" wtf:qtitle="+WtfGlobal.getLocaleText("hrms.performance.description")+">"+val+"</div>";//return "<div wtf:qtip=\"Please select Employee to assign goal(s)\" wtf:qtitle='Description'>"+val+"</div>";
                }
            }

            ]);
     
        this.assignbutton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.assign.goals"),//'Assign Goals',
            tooltip:WtfGlobal.getLocaleText("hrms.performance.assign.goals.tooltip"),//"Select an employee and assign him goals which he is required to achieve.",
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            minWidth:82,
            disabled:true,
            handler:this.add1,
            scope:this
        });
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.ds.load({params:{start:0,limit:this.grid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.grid.id).setValue("");
        	}
     	});
        this.viewbutton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.view.goals"),//'View Goals',
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:75,
            disabled:true,
            handler:this.viewgoals,
            scope:this
        });
        var goalbtns=new Array();
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.assigngoals, Wtf.Perm.assigngoals.assign)){
        	goalbtns.push("-");
        	goalbtns.push(this.refreshBtn);
            goalbtns.push("-");
            goalbtns.push(this.assignbutton);
        //           goalbtns.push("-");
        //           goalbtns.push(this.viewbutton);
        }

        this.grid=new Wtf.KwlGridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            border:false,
            layout:'fit',
            viewConfig: {
                forceFit: true,
                emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg")+'</font></center>'//emptyText:'<center><font size="4">No records to show</font></center>'
            },
            stripeRows: true,
            loadMask:true,
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),//"Search by Employee Name",
            searchField:"fullname",
            serverSideSearch:true,
            tbar:goalbtns
        });

        this.sm.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(goalbtns, this.grid, [], [3]);
        },this);
        this.add(this.grid);        
    },
    add1:function(){
        this.arr=this.sm.getSelections();
        var empname=this.arr[0].get('fullname');
        var empid=this.arr[0].get('userid');
        var main=Wtf.getCmp("goalmanagementtabpanel");
        var demoTab=Wtf.getCmp(empid+"perticularemployeesforgoal");
        if(demoTab==null)
        {
            demoTab=new Wtf.perticularemployeegoals({
                id:empid+"perticularemployeesforgoal",
                title:WtfGlobal.getLocaleText("hrms.performance.assign.goals.to")+" "+empname,//"Assign Goals To "+empname,
                iconCls:getTabIconCls(Wtf.etype.hrmsmygoals),
                layout:'fit',
                border:false,
                closable:true,
                assign:true,
                empid:empid
            });
            main.add(demoTab);
        }
        main.setActiveTab(demoTab);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    viewgoals:function(){
        this.arr=this.sm.getSelections();
        var empname=this.arr[0].get('fullname');
        var empid=this.arr[0].get('userid');
        var main=Wtf.getCmp("goalmanagementtabpanel");
        var demoTab=Wtf.getCmp(empid+"viewemployeesgoals");
        if(demoTab==null)
        {
            demoTab=new Wtf.perticularemployeegoals({
                id:empid+"viewemployeesgoals",
                title:empname+WtfGlobal.getLocaleText("hrms.performance.s.goals"),//empname+"'s Goals ",
                iconCls:getTabIconCls(Wtf.etype.hrmsmygoals),
                layout:'fit',
                border:false,
                assign:false,
                closable:true,
                empid:empid
            });
            main.add(demoTab);
        }
        main.setActiveTab(demoTab);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    }
});
