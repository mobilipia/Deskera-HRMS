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
Wtf.archivedGoals=function(config){

    Wtf.archivedGoals.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.archivedGoals,Wtf.Panel,{
    initComponent:function(config){
        Wtf.archivedGoals.superclass.initComponent.call(this,config);
    },
    onRender: function(config) {
        Wtf.archivedGoals.superclass.onRender.call(this, config);

        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        this.goalRecord=Wtf.data.Record.create([{
            name:'empname'
        },{
            name:'manname'
        },{
            name:'gname'
        },{
            name:'gid'
        },{
            name:'gdescription'
        },{
            name:'gwth'
        },{
            name:'gcontext'
        },{
            name:'gpriority'
        },{
            name:'gstartdate',
            type:'date'
        },{
            name:"genddate",
            type:'date'
        },{
            name:"gcomment"
        }
        ]);

        this.goalReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.goalRecord);

        this.goalstore= new Wtf.data.Store({
//            url: Wtf.req.base + "hrms.jsp",
            url:"Performance/Goal/archivedgoalsfunction.pf",
            reader:this.goalReader,
            baseParams:{
                flag:150
            }
        });
        calMsgBoxShow(202,4,true);
        this.goalstore.load();
        this.goalstore.on("load",function(){
        	this.unarchivebutton.setDisabled(true);
            if(msgFlag==1)
                WtfGlobal.closeProgressbar()
        },this);

        this.cm1=new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),this.sm,{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),//"Employee Name",
                sortable: true,
                dataIndex: 'empname'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.manager.name"),//"Manager Name",
                sortable: true,
                dataIndex: 'manname'
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.goal.name"),//"Goal Name",
                sortable: true,
                dataIndex: 'gname'
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.goal.description"),//"Goal Description",
                sortable: true,
                dataIndex: 'gdescription'
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.weightage"),//"Weightage",
                sortable: true,
                dataIndex: 'gwth'
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.context"),//"Context",
                sortable:true,
                dataIndex:'gcontext'
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.priority"),//"Priority ",
                sortable: true,
                dataIndex: 'gpriority'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),//"Start Date ",
                sortable: true,
                dataIndex: 'gstartdate',
                renderer:WtfGlobal.onlyDateRenderer
            },{
                header: WtfGlobal.getLocaleText("hrms.common.due.date"),//"Due Date ",
                sortable: true,
                dataIndex: 'genddate',
                renderer:WtfGlobal.onlyDateRenderer
            }
            ]);

        this.goalgrid = new Wtf.KwlGridPanel({
            store: this.goalstore,
            sm:this.sm,
            autoScroll :true,
            border:false,
            scope:this,
            id:'archivedgoalemp',
            clicksToEdit :1,
            searchField:'empname',
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),//"Search by Employee Name",
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            serverSideSearch:true,
            loadMask:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.no.goal.archived"))//WtfGlobal.emptyGridRenderer("No goal archived till now")
            },
            cm: this.cm1,
            tbar:['-',new Wtf.Toolbar.Button({
         		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
         		scope: this,
         		iconCls:'pwndRefresh',
         		handler:function(){
            		this.goalstore.load({params:{start:0,limit:this.goalgrid.pag.pageSize}});
            		Wtf.getCmp("Quick"+this.goalgrid.id).setValue("");
            	}
         	}),'-',this.unarchivebutton=new Wtf.Button({
                    text:WtfGlobal.getLocaleText("hrms.performance.unarchive.goals"),//'Unarchive Goals',
                    tooltip:WtfGlobal.getLocaleText("hrms.performance.remove.goals.from.archive.repository"),//"Remove the goals from archive repository, which you have to make active.",
                    iconCls:'pwndCommon archivebuttonIcon',
                    minWidth:87,
                    //disabled:true,
                    scope:this,
                    handler:this.unarchivegoal
                })]
        });
        this.add(this.goalgrid);
        
        this.sm.on("selectionchange",function(){
        	if(this.sm.getCount()>0)
        		this.unarchivebutton.setDisabled(false);
        	else
        		this.unarchivebutton.setDisabled(true);
        },this);
    },

    unarchivegoal:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0){
            calMsgBoxShow(42,1,false,225);
        }
        else{
            this.archiverec=this.goalgrid.getSelectionModel().getSelections();
            this.archivearr=[];
            this.archivearr1=[];
            for(var i=0;i<this.archiverec.length;i++){
                this.archivearr.push(this.archiverec[i].get('gid'));
                this.archivearr1.push(this.archiverec[i].get('gname'));
            }
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
                url:"Performance/Goal/insertGoal.pf",
                params:  {
                    flag:205,
                    archiveid:this.archivearr,
                    archive:"false",
                    gname:this.archivearr1
                }
            },
            this,
            function(){
                this.goalstore.load();
                var archivegoal=Wtf.getCmp('archivedgoalemp');
                if(archivegoal!=null){
                    archivegoal.getStore().load();
                }
                msgFlag=0;
                calMsgBoxShow(205,0);
            },
            function(){
                calMsgBoxShow(27,1);
            })
        }
    }
});

