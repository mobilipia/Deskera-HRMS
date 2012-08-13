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
Wtf.assignReviewer = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        width:600,
        height:600,
        buttons: [
        {
            text:  WtfGlobal.getLocaleText("hrms.common.Assign"),
            handler: this.saveassignReviewer,
            scope:this
        },
        {
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler: function(){
                this.close();
            },
            scope:this
        }]
    }, config);
    Wtf.assignReviewer.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.assignReviewer, Wtf.Window, {
    initComponent: function() {
        Wtf.assignReviewer.superclass.initComponent.call(this);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.assignReviewer.superclass.onRender.call(this, config);
        this.sm = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        this.cm2 = new Wtf.grid.ColumnModel(
            [this.sm,{
                header:  WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex: 'username',
                sortable: true

            },{
                header:  WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                sortable: true
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                sortable: true
            }]);

        this.recGrid=new Wtf.grid.GridPanel({
            cm:this.cm2,
            store:Wtf.employeeStore,
            sm:this.sm,
            viewConfig: {
                forceFit: true
            }
        });

        this.recPanel= new Wtf.Panel({
            frame:true,
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                items:[{
                    region : 'north',
                    height : 75,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #FFFFFF;',
                    html: getTopHtml( WtfGlobal.getLocaleText("hrms.common.AssignReviewer"), WtfGlobal.getLocaleText("hrms.common.Selectfromthefollowingmanager"),'../../images/assign-manager.gif')
                },{
                    border:false,
                    region:'center',
                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    layout:"fit",
                    items: [this.recGrid]
                }]
            }]
        });
        this.add(this.recPanel);

        this.on("show",function(){
            if(!Wtf.StoreMgr.containsKey("manager")){
                Wtf.employeeStore.on('load',this.loadReviewer,this);
                Wtf.employeeStore.load();
                Wtf.StoreMgr.add("manager",Wtf.employeeStore)
            }else{
                this.loadReviewer();
            }
        });
    },
    saveassignReviewer:function(){
        if(this.recGrid.getSelectionModel().getCount()==0)
            msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.pleaseSelectRecFirst")],1);
        else{
            this.user=this.allempGrid.getSelectionModel().getSelections();
            this.userids=[];
            this.reviewerids=[];
            this.assignflag=true;
            for(var i=0;i<this.user.length;i++){
                this.userids.push(this.user[i].get('userid'));
            }
            this.reviewer=this.recGrid.getSelectionModel().getSelections();
            for(var j=0;j<this.reviewer.length;j++){
                this.reviewerids.push(this.reviewer[j].get('userid'));
            }
            
            for(i=0;i<this.user.length;i++){
                for(j=0;j<this.reviewer.length;j++){
                    if(this.user[i].get('userid')==this.reviewer[j].get('userid')){
                        this.assignflag=false;
                        break;
                    }
                }
            }

            if(!this.assignflag){
                this.close(); 
                msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.PleaseSelValidRecord")],1);
            }
            else{
                var rec=this.sm2.getSelected();
                var row=this.empGDS.indexOf(rec);
                this.sm2.clearSelections();
                WtfGlobal.highLightRow(this.allempGrid ,"33CC33",5,row);
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
                    url:  Wtf.req.base + 'hrms.jsp',
                    params:  {
                        flag:137,
                        userid:this.userids,
                        reviewerid:this.reviewerids,
                        isManager:false
            }
                },
                this,
                function(){
                    this.close();
                    msgBoxShow([ WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.Reviewerassignedsuccessfully")],1,1);
                    var delayTask = new Wtf.util.DelayedTask(function(){
                        this.empGDS.load({
                            params:{
                                start:0,
                                limit:this.allempGrid.pag.pageSize,
                                ss: Wtf.getCmp("Quick"+this.allempGrid.id).getValue()
        }
                        });
                    },this);
                    delayTask.delay(1000);
    },
                function(){
                    Wtf.Msg.alert( WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.common.Errorinassigningreviewer"));
                })
            }
        }
    },
    loadReviewer:function(){
        var rec=this.sm2.getSelected();
        if(rec.data['reviewerid'].length==1){
            this.revid=rec.data['reviewerid'];
            var row=Wtf.employeeStore.findBy(this.findrevuser,this);
            this.sm.selectRow(row);
        }
        if(rec.data['reviewerid'].length>1){
            var rowarr=[];
            var index=[];
            rowarr=rec.data['reviewerid']
            for(var i=0;i<rowarr.length;i++){
                this.revid=rowarr[i];
                index[i]=Wtf.employeeStore.findBy(this.findrevuser,this);
            }
            this.sm.selectRows(index);
        }
    },
    findrevuser:function(record){
        if(record.get('userid')==this.revid){
            return true;
        } else{
            return false;
        }
    }
});

Wtf.assignManagerWin=function(config){
    Wtf.apply(this,config);
    Wtf.assignManagerWin.superclass.constructor.call(this,{
        buttonAlign : 'right',
        buttons :[this.savebtn=new Wtf.Button({
            text :  WtfGlobal.getLocaleText("hrms.common.Save"),
            scope: this,
            minWidth:75,
            disabled:true,
            handler:function() {
                this.createMemberList();
            }
        }),{
            text :  WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope : this,
            handler : function(){
                this.close();
            }
        }]
    });
    this.addEvents({
        "savedata": true
    });
}

Wtf.extend(Wtf.assignManagerWin, Wtf.Window, {
    group_id:"",
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
        Wtf.assignManagerWin.superclass.onRender.call(this,config);     
        this.availablesm = new Wtf.grid.CheckboxSelectionModel();
        this.availablecm = new Wtf.grid.ColumnModel([this.availablesm,
           {
                header: WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex: 'username',
                sortable: true
            },
            {
                header:  WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                sortable: true
            },
            {
                header:  WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                sortable: true
            }
        ]);
        this.quickSearchEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"username",
            emptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg")
        });
           this.availableds = new Wtf.data.Store({
//            url:  Wtf.req.base + 'hrms.jsp',
            url:"Common/getAvailableManagers.common",
            baseParams: {
                flag: 211,
                manager:this.managerF,
                userid:this.userid,
                salarymanager:this.salaryManagerF
            },
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },new Wtf.data.Record.create([{
                name:'userid'
            },{
                name:'username'
            },{
                name:'designation'
            },{
                name:'departmentname'
            }])
            ),
            autoLoad : false
        });
        //this.availablegrid = new Wtf.grid.EditorGridPanel({
        this.availablegrid = new Wtf.KwlEditorGridPanel({
            height:100,
            store:this.availableds,
            cm: this.availablecm,
            border:false,
            id:this.id+'empavailablegrid',
            sm : this.availablesm,
            autoScroll:true,
            searchField:"username",
            serverSideSearch:true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            viewConfig: {
                forceFit: true,
                autoFill:true                
            }
            //tbar : ['Quick Search: ',this.quickSearchEmp]
        });

        this.availableds.on("load",this.empSearch,this);

        this.selectedRec = new Wtf.data.Record.create([
        {
            name:'userid'
        },

        {
            name:'username'
        },
        {
            name:'designation'
        },
        {
            name:'departmentname'
        }
        ]);

        this.selectedds = new Wtf.data.Store({
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/getAssignedManager.common",
            baseParams:{
                manager:this.managerF,
                salarymanager:this.salaryManagerF,
                flag:61,
                userid:this.userid
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data',
                totalProperty: 'count'
            },this.selectedRec),
            autoLoad : false
        });
        this.selectedsm = new Wtf.grid.CheckboxSelectionModel();
        this.selectedcm = new Wtf.grid.ColumnModel([this.selectedsm,        
            {
                header:  WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex: 'username',
                sortable: true
            },
            {
                header:  WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                sortable: true
            },
            {
                header:  WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                sortable: true
            }
        ]);
        this.quickSearchAssgEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"username",
            emptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg")
        });
        this.selectedgrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store: this.selectedds,
            cm: this.selectedcm,
            sm : this.selectedsm,
            autoScroll:true,
            border:false,            
            viewConfig: {
                forceFit: true
            },
            tbar : [ WtfGlobal.getLocaleText("hrms.common.QuickSearch")+': ',this.quickSearchAssgEmp]
        });
        this.selectedds.load();
        this.selectedds.on("load",this.empAssgSearch,this);
        
        this.movetoright = document.createElement('img');
        this.movetoright.src = "../../images/arrowright.gif";
        this.movetoright.style.width = "24px";
        this.movetoright.style.height = "24px";
        this.movetoright.style.margin = "5px 0px 5px 0px";
        this.movetoright.onclick = this.movetorightclicked.createDelegate(this,[]);

        this.movetoleft = document.createElement('img');
        this.movetoleft.src = "../../images/arrowleft.gif";
        this.movetoleft.style.width = "24px";
        this.movetoleft.style.height = "24px";
        this.movetoleft.style.margin = "5px 0px 5px 0px";
        this.movetoleft.onclick = this.movetoleftclicked.createDelegate(this,[]);

        this.centerdiv = document.createElement("div");
        this.centerdiv.appendChild(this.movetoright);
        this.centerdiv.appendChild(this.movetoleft);
        this.centerdiv.style.padding = "135px 10px 135px 10px";
        var id="1";
        var msg1="";
        var head="";
        if(this.text1=='edit')
        {
            this.bttntext=WtfGlobal.getLocaleText("hrms.common.Update");
            var flag=9;
            id=this.group_id;
            msg1=WtfGlobal.getLocaleText("hrms.performance.group.updated.successfully");
            head="Edit Group";
            this.imgsrc = 'edit-group.gif';
        }
        else
        {
            this.bttntext=WtfGlobal.getLocaleText("hrms.common.Create");
            var flag=6;
            id=this.group_id;
            msg1="Group Added successfully.";
            head="Create New Group";
            this.imgsrc = 'add-group.gif';
        }

        var title="";
        var titlehtml="";
        var titlehtml1="";
        var userid="";
        if(this.managerF){
            title=WtfGlobal.getLocaleText("hrms.performance.appraiser");
            titlehtml=WtfGlobal.getLocaleText("hrms.common.AssignAppraiser");
            titlehtml1=WtfGlobal.getLocaleText("hrms.common.Selectemployeeforassigningappraiser");
        }else if(this.salaryManagerF){
            title=WtfGlobal.getLocaleText("hrms.administration.salary.authorization");
            titlehtml=WtfGlobal.getLocaleText("hrms.Administration.AssignSalaryAuthorization");
            titlehtml1=WtfGlobal.getLocaleText("hrms.common.Selectemployeeforassignsalaryauthorization");
        }else{
            title=WtfGlobal.getLocaleText("hrms.common.Reviewer");
            titlehtml=WtfGlobal.getLocaleText("hrms.common.AssignReviewer");
            titlehtml1=WtfGlobal.getLocaleText("hrms.common.Selectemployeeforassigningreviewer");
        }
        this.assignTeamPanel = new Wtf.Panel({
            layout : 'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                cls : 'formstyleClass',
                html :getTopHtml(titlehtml, " "+titlehtml1, "../../images/assign-manager.gif")
                   
            },{
                region : 'center',
                border : false,                
                layout : 'fit',
                items : [{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : 'border',
                    items : [
                    {
                        region : 'west',
                        border : false,
                        width : 350,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title :  WtfGlobal.getLocaleText("hrms.common.Employees"),
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.availablegrid
                        }]
                    },{
                        region : 'center',
                        border : false,
                        contentEl : this.centerdiv
                    },{
                        region : 'east',
                        border : false,
                        width : 350,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title : titlehtml,
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.selectedgrid
                        }]
                    }]
                }]
            }]
        });


        this.add(this.assignTeamPanel);

    },
    empSearch: function(store, rec, opt) {
        this.quickSearchEmp.StorageChanged(store);
    },
    empAssgSearch: function(store, rec, opt) {
        this.quickSearchAssgEmp.StorageChanged(store);
        if(this.selectedds.getCount()>0)
            this.savebtn.enable();
        else
            this.savebtn.disable();
        this.refreshAssignedparams();
        this.availableds.load();
        
    },
    refreshAssignedparams : function() {
        var managerids = [];
        for(var i=0;i<this.selectedds.getCount();i++){
            managerids.push(this.selectedds.getAt(i).get('userid'));
        }
        this.availableds.baseParams.managerids=managerids;
    },
    movetorightclicked : function() {
        var selected = this.availablesm.getSelections();
        if(selected.length>0){
            this.selectedds.add(selected);
            this.refreshAssignedparams();
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.availableds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            this.savebtn.enable();
        else
           this.savebtn.disable();
    },

    movetoleftclicked : function() {

        var selected = this.selectedsm.getSelections();
        if(selected.length>0){
            this.availableds.add(selected);
            this.refreshAssignedparams();
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.selectedds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            this.savebtn.enable();
        else
           this.savebtn.disable();
    },
    createMemberList:function(){
        var managerid=[];
        var availmanagerid=[];
        var userid=[];
        this.assignflag=true;
        
        if(this.selectedds.getCount()==0){
        	var msg="";
            if(this.managerF){
            	msg=WtfGlobal.getLocaleText("hrms.admin.assign.appraiser.msg");
            }else if(this.salaryManagerF){
            	msg=WtfGlobal.getLocaleText("hrms.admin.assign.salary.authorizer.msg");
            }else{
            	msg=WtfGlobal.getLocaleText("hrms.admin.assign.reviewer.msg");
            }
        	msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), msg], 0);
        	return false;	
        }
        
        for(var i=0;i<this.selectedds.getCount();i++){
            managerid.push(this.selectedds.getAt(i).get('userid'));
        }

        for(var i=0;i<this.availableds.getCount();i++){
            availmanagerid.push(this.availableds.getAt(i).get('userid'));
        }
        for(var j=0;j<managerid.length;j++){
            if(managerid[j]==this.userid){
                this.assignflag=false;
                break;
            }
        }
        if(!this.assignflag){
            this.close();
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.PleaseSelValidRecord")],1);
        }
        else{
            var rec=this.allempGrid.getSelectionModel().getSelected();
            this.allempGrid.getSelectionModel().clearSelections();

            if(rec!=undefined){
                var row=this.empGDS.indexOf(rec);
                WtfGlobal.highLightRow(this.allempGrid ,"33CC33",5,row);
            }

            userid.push(this.userid);
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
//                url:  Wtf.req.base + 'hrms.jsp',
                url:"Common/assignManager.common",
                params:  {
                    flag:137,
                    userid:userid,
                    managerid:managerid,
                    availmanagerid:availmanagerid,
                    isManager:this.managerF,
                    salaryManagerF:this.salaryManagerF
                }
            },
            this,
            function(){
            	var msg="";
                if(this.managerF){
                	msg=WtfGlobal.getLocaleText("hrms.common.Appraiserassignedsuccessfully");
                }else if(this.salaryManagerF){
                	msg=WtfGlobal.getLocaleText("hrms.common.authorizedassignedsuccessfully");
                }else{
                	msg=WtfGlobal.getLocaleText("hrms.common.Reviewerassignedsuccessfully");
                }            	
                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.success"), msg],0,0);
                var delayTask = new Wtf.util.DelayedTask(function(){
                    this.empGDS.load({
                        params:{
                            start:this.allempGrid.pag.cursor,
                            limit:this.allempGrid.pag.pageSize,
                            ss: Wtf.getCmp("Quick"+this.allempGrid.id).getValue()
                        }
                    });
                    this.close();
                },this);
                delayTask.delay(1000);
            },
            function(){
                Wtf.Msg.alert( WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.Errorinassigningmanager"));
            });
        }
    }
});


