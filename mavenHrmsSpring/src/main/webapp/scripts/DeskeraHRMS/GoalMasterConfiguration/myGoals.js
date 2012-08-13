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
Wtf.myGoals= function(config){
    Wtf.myGoals.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.myGoals, Wtf.Panel, {
    initComponent: function() {
        Wtf.myGoals.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.myGoals.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
       {
            name:'gname'
        },
        {
          name:'gid'
        },
        {
            name:'gdescription'
        },
        {
          name:'gassignedby'
        },

        {
            name:'gwth'
        },

        {
            name:'gcontext'
        },

        {
            name:'gpriority'
        },

        {
            name:'gstartdate',
            type:'date'
        },

        {
            name:"genddate",
            type:'date'
        },
        {
            name:'percentcomp'
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
           url:"Performance/Goal/Employeesgoalfinal.pf",
           baseParams: {
                flag: 29

            },
           reader:this.reader

        });
        calMsgBoxShow(202,4,true);
        this.ds.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.ds.on("load",function(){if(msgFlag==1) WtfGlobal.closeProgressbar()},this);

        this.sm= new Wtf.grid.RowSelectionModel({
            singleSelect:false
        });
        this.sm.on("selectionchange",function (){
                this.decideAction();
        },this);
        this.percentcombo= new Wtf.form.ComboBox({
            store:Wtf.completedStore,
            displayField:'name',
            scope:this,
            selectOnFocus:true,
            width:200,
            allowBlank:false,
            typeAhead:true,
            valueField:'name',
            mode:'local',
            height:200,
            triggerAction :'all'
        });
        if(!Wtf.StoreMgr.containsKey("comp")){
            Wtf.completedStore.load();
            Wtf.StoreMgr.add("comp",Wtf.completedStore);
        }
        
        this.savedata=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.save.goals"),//'Save Goals',
            tooltip:WtfGlobal.getLocaleText("hrms.performance.save.goals.tooltip"),//"Save the data entered by you.",
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            minWidth:75,
            disabled: true,
            scope:this,
            handler:this.saveData
        });
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.ds.load({params:{start:0,limit:this.myGoalsgrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.myGoalsgrid.id).setValue("");
        	}
     	});
        this.cm = new Wtf.grid.ColumnModel([            
            new Wtf.grid.RowNumberer(),{
            header: WtfGlobal.getLocaleText("hrms.performance.goal.name"),//"Goal Name",
            //width:200,
            sortable: true,
            dataIndex: 'gname',
            renderer : function(val) {
                return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
            }
        },
        {
            header: WtfGlobal.getLocaleText("hrms.performance.goal.description"),//"Goal Description",
           // width: 200,
            sortable: true,
            dataIndex: 'gdescription'
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.weightage"),//"Weightage",
           // width: 200,
            sortable: true,            
            dataIndex: 'gwth'
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.context"),//"Context",
           // width:200,
            sortable:true,            
            dataIndex:'gcontext'

        },
        {
            header: WtfGlobal.getLocaleText("hrms.performance.priority"),//"Priority ",
          //  width: 100,
            sortable: true,
            dataIndex: 'gpriority'

        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),//"Start Date ",
          //  width: 150,
            align:'center',
            sortable: true,
            dataIndex: 'gstartdate',
            renderer:WtfGlobal.onlyDateRenderer
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.due.date"),//"Due Date ",
          //  width: 150,
            align:'center',
            sortable: true,
            dataIndex: 'genddate',
            renderer:WtfGlobal.onlyDateRenderer
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.assigned.by"),//"Assigned By ",
          //  width: 200,
            sortable: true,
            dataIndex: 'gassignedby'
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.percent.completed"),//"Percent Completed",
            editor: this.percentcombo,
            sortable: true,
            dataIndex: 'percentcomp',
            align: 'right'
        }
      ]);
      
        var empgoalbtnstbar=new Array();
        empgoalbtnstbar.push('-');
        empgoalbtnstbar.push(this.refreshBtn);
        empgoalbtnstbar.push('-');
        empgoalbtnstbar.push(this.savedata);

        this.myGoalsgrid=new Wtf.KwlEditorGridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            border:false,
            autoScroll :true,
            //layout:'fit',
            scope:this,
            loadMask:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.my.goal.grid.msg"))//WtfGlobal.emptyGridRenderer("No goal assigned till now")
            },
             clicksToEdit :1,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.performance.my.goal.grid.search.msg"),//"Search by Goal Name ",
            searchField:"gname",
            serverSideSearch:true,
            displayInfo:true,
            tbar:empgoalbtnstbar
        });
        
//If you want to enable button ,if only one record selected ,otherwise disable
        this.getDetailPanel();
        this.goalpan= new Wtf.Panel({
            layout:'border',
            region:'center',
            border:false,
            id:this.id+'goalpan',
            items:[
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.myGoalsgrid]
            },
            {
                region:'south',
                height:230,
                title:WtfGlobal.getLocaleText("hrms.performance.comments"),//'Comments',
                border:false,
                collapsible:true,
                id: "southpanel1",
                split : true,
                layout: "fit",
                items:[this.detailPanel]
            }
            ]
        });
        this.add(this.goalpan);
        this.goalpan.doLayout();
        this.percentcombo.on("select", function(){
            this.savedata.enable();
        }, this)
        Wtf.getCmp("southpanel1").on("collapse", function(){
            var titleSpan = document.createElement("div");
            titleSpan.innerHTML = WtfGlobal.getLocaleText("hrms.performance.goal.comments"),//"Goal Comments";
            titleSpan.id="southdash";
            titleSpan.className = "collapsed-header-title";
            Wtf.getCmp("southpanel1").container.dom.lastChild.appendChild(titleSpan);
        }, this);
    },
    decideAction:function (){
        var rec=this.sm.getSelections();
        var gid="";
        if(rec.length>0){
            gid=rec[0].data["gid"];
            if(gid!==undefined && gid!=''){
                getDocsAndCommentList(this.myGoalsgrid,gid,1,this.id);
            }
        }
    },
    getDetailPanel:function(){
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.myGoalsgrid,
            Store:this.ds,
            modulename:'Goals',
            height:200,
            employee:true,
            mapid:0,
            id2:this.id
        });
    },
    saveData:function(){
        var jsondata = "";
        var record;
        var vflag=0;
        var records = this.ds.getModifiedRecords();
        for(var i=0;i<records.length ;i++){
            record=records[i].data;
            jsondata += "{'gid':'" + record.gid + "',";
            jsondata += "'gcomplete':'" + record.percentcomp + "'},";
        }
        var finalStr = jsondata.substr(0,jsondata.length - 1);
        calMsgBoxShow(200,4,true);
        Wtf.Ajax.requestEx({
            url:"Performance/Goal/changeMyGoalPercent.pf",
            params: {
                jsondata:finalStr,
                empid:this.empid
            }
        }, this,
        function(response){
            this.ds.commitChanges();
            this.ds.reload();
            calMsgBoxShow(89,0);
        },
        function(response)
        {
            calMsgBoxShow(101,1);
        })
    },
    add1:function(){

    }
});





