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
Wtf.perticularemployeegoals=function(config){   

    Wtf.perticularemployeegoals.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.perticularemployeegoals,Wtf.Panel,{
    initComponent:function(config){
        Wtf.perticularemployeegoals.superclass.initComponent.call(this,config);
//        this.on('activate',function(){
//            this.goalstore.load();
//        },this);
        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
       
        this.goalRecord=Wtf.data.Record.create([
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
            name:"gcomment"
        },
        {
            name:"internal"
        },
        {
            name:'percentcomp'
        }
        ]);


        this.goalReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.goalRecord);

        this.contextcombo= new Wtf.form.FnComboBox({
            store:Wtf.contextstore,
            displayField:'name',
            valueField:'name',
            scope:this,
            mode:'local',
            selectOnFocus:true,
            typeAhead:true,
            allowBlank:false,
            width:200,
            height:200,
            triggerAction :'all',
            addNewFn:this.addContext.createDelegate(this)
        });

        if(!Wtf.StoreMgr.containsKey("context")){
            Wtf.contextstore.load();
            Wtf.StoreMgr.add("context",Wtf.contextstore)
        }

        this.prioritycombo= new Wtf.form.FnComboBox({
            store:Wtf.priostore,
            displayField:'name',
            scope:this,
            selectOnFocus:true,
            valueField:'name',
            mode:'local',
            allowBlank:false,
            width:200,
            typeAhead:true,
            height:200,
            triggerAction :'all',
            addNewFn:this.addPriority.createDelegate(this)
        });

        if(!Wtf.StoreMgr.containsKey("prio")){
            Wtf.priostore.load();
            Wtf.StoreMgr.add("prio",Wtf.priostore)
        }

        this.wthcombo= new Wtf.form.FnComboBox({
            store:Wtf.wthstore,
            displayField:'name',
            scope:this,
            selectOnFocus:true,
            width:200,
            allowBlank:false,
            typeAhead:true,
            valueField:'name',
            mode:'local',
            height:200,
            triggerAction :'all',
            addNewFn:this.addWeightage.createDelegate(this)
        });

        if(!Wtf.StoreMgr.containsKey("wth")){
            Wtf.wthstore.load();
            Wtf.StoreMgr.add("wth",Wtf.wthstore)
        }

        this.goalstore= new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url:"Performance/Goal/Employeesgoalfinal.pf",
            pruneModifiedRecords:true,
            reader:this.goalReader,
            baseParams:{
                flag:29,
                empid:this.empid
            }
        });
        
        this.percentcombo= new Wtf.form.FnComboBox({
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
            triggerAction :'all',
            addNewFn:this.addCompleted.createDelegate(this)
        });

        if(!Wtf.StoreMgr.containsKey("comp")){
            Wtf.completedStore.load();
            Wtf.StoreMgr.add("comp",Wtf.completedStore)
        }
        calMsgBoxShow(202,4,true);
        this.goalstore.load();
        this.goalstore.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);

        this.text1=new Wtf.ux.TextField({
            name:'goalname',
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            maxLength:255
        });
        this.text2=new Wtf.ux.TextField({
            name:'goaldescription',
            allowBlank:false,
            validator:WtfGlobal.noBlankCheck,
            maxLength:255
        });
        this.text3=new Wtf.ux.TextField({
            name:'goalcomment',
            maxLength:255

        });

        this.fromdate=new Wtf.form.DateField({
            name:'from',
            width:200,
            allowBlank:false,
            format:'Y-m-d'
        });
        this.todate=new Wtf.form.DateField({
            name:'to',
            width:200,
            allowBlank:false,
            format:'Y-m-d'
        });
        this.addbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.AddGoals"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.AddGoals.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:70,
            scope:this,
            handler:this.insertgoal
        });

        this.delbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.DeleteGoals"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.DeleteGoals.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:82,
            scope:this,
            disabled:true,
            handler:this.deletegoal
        });

        this.archivebutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.ArchiveGoals"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.ArchiveGoals.tooltip"),
            iconCls:'pwndCommon archivebuttonIcon',
            minWidth:87,
            disabled:true,
            scope:this,
            handler:this.archivegoal
        });

        this.savedata=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.SaveGoals"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.SaveGoals.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            minWidth:75,
            scope:this,
            disabled:true,
            handler:this.saveData
        });
        var empgoalbtnstbar=new Array();
        
        if(this.assign){
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.assigngoals, Wtf.Perm.assigngoals.manage)){
                empgoalbtnstbar.push('-');
                empgoalbtnstbar.push(this.addbutton);
                empgoalbtnstbar.push('-');
                empgoalbtnstbar.push(this.savedata);
                empgoalbtnstbar.push('-');
                empgoalbtnstbar.push(this.delbutton);
                empgoalbtnstbar.push('-');
                empgoalbtnstbar.push(this.archivebutton);
            }
        }
        else{
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.assigngoals, Wtf.Perm.assigngoals.manage)){
                empgoalbtnstbar.push('-');
                empgoalbtnstbar.push(this.archivebutton);
            }
        }

        this.cm1=new Wtf.grid.ColumnModel([
            this.sm,
            {
                header: WtfGlobal.getLocaleText("hrms.performance.goal.name"),
                sortable: true,
                dataIndex: 'gname',
                editor:this.text1,
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.goal.description"),
                sortable: true,
                editor: this.text2,
                dataIndex: 'gdescription'
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.goal.weightage"),
                sortable: true,
                editor: this.wthcombo,
                dataIndex: 'gwth'
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.context"),
                sortable:true,
                editor:this.contextcombo,
                dataIndex:'gcontext'
            },{
                header: WtfGlobal.getLocaleText("hrms.performance.priority"),
                sortable: true,
                dataIndex: 'gpriority',
                editor:this.prioritycombo
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),
                sortable: true,
                dataIndex: 'gstartdate',
                editor: this.fromdate,
                renderer:WtfGlobal.onlyDateRenderer
            },{
                header: WtfGlobal.getLocaleText("hrms.common.due.date"),
                sortable: true,
                dataIndex: 'genddate',
                editor:this.todate,
                renderer:WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.percent.completed"),
                sortable: true,
                editor: this.percentcombo,
                dataIndex: 'percentcomp',
                align: 'right'
        }]);

        this.goalgrid = new Wtf.KwlPagingEditorGrid({
            store: this.goalstore,
            sm:this.sm,
            autoScroll :true,
            border:false,
            id:'goalGrid'+this.id,
            autoDestroy:true,
            scope:this,
            loadMask:true,
            clicksToEdit :1,
            searchField:'gname',
            searchEmptyText:WtfGlobal.getLocaleText("hrms.performance.my.goal.grid.search.msg"),
            searchLabel:" ",
            searchLabelSeparator:" ",
            serverSideSearch:true,
            displayInfo:true,
            viewConfig: {
                forceFit: true,
                emptyText:Wtf.gridEmptytext
            },
            tbar:empgoalbtnstbar,
            cm: this.cm1
        });
        this.goalgrid.on('cellclick',function(grid,row,col,e) {
            var record = grid.getStore().getAt(row);
            if(!record.get('internal') && (col < 3 || col > 5)){
                return false;
            }
            return true;
        },this);
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
                items:[this.goalgrid]
            },
            {
                region:'south',
                height:230,
                title:WtfGlobal.getLocaleText("hrms.performance.comments"),
                border:false,
                id: this.id+"southpanel",
                collapsible:true,
                split : true,
                layout: "fit",
                items:[this.detailPanel]
            }
            ]
        });
        
        this.sm.on("rowdeselect",function (){
        	if(this.sm.getCount()==0){
        		var tpl1= new Wtf.Template("<div class='commentTemplate'>", "<div id='{msgDiv}' class='commentTemplate1'>"+ WtfGlobal.getLocaleText("hrms.common.Pleaseselectarecordtoseedetails")+"</div></div>");
                tpl1.overwrite(Wtf.getCmp(this.id+"dloadpanelcenter").body,'');
        	}
        },this);
        
        this.add(this.goalpan);
        this.goalpan.doLayout();
        this.goalgrid.on("validateedit",this.validate,this);
        if(this.assign){
            this.sm.on("selectionchange",function (){
                WtfGlobal.enableDisableBtnArr(empgoalbtnstbar, this.goalgrid, [], [5,7]);
                var rec =  this.sm. getSelections();
                if(rec.length!=0){
                    for(var i=0;i<rec.length;i++){
                        if(!rec[i].get("internal")){
                            this.delbutton.setDisabled(true);
                            break;
                        }
                    }
                }
                this.decideAction();
            },this);
        }else{
            this.sm.on("selectionchange",function (){
                WtfGlobal.enableDisableBtnArr(empgoalbtnstbar, this.goalgrid, [], [1]);
                this.decideAction();
            },this);
        }
        this.goalgrid.on('rowclick', function(grid, rowIndex, e){
            if(e.target.className == "pwndCommon addComment" || e.target.className == "pwndHRMS viewComment") return;
            var xy = e.getXY();
        //this.contextMenu.showAt(xy);
        }, this);
        this.goalgrid.on('click', this.docuploadhandler, this);
        this.goalstore.on('load',function(){
            if(this.goalstore.getCount()==0){
                this.insertgoal();
            }
            this.savedata.disable();
        },this);
        this.goalstore.on("update", function(){
            this.savedata.enable();
        },this);
        Wtf.getCmp(this.id+"southpanel").on("collapse", function(){
            var titleSpan = document.createElement("div");
            titleSpan.innerHTML = WtfGlobal.getLocaleText("hrms.performance.goal.comments");
            titleSpan.id="southdash";
            titleSpan.className = "collapsed-header-title";
            Wtf.getCmp(this.id+"southpanel").container.dom.lastChild.appendChild(titleSpan);
        }, this);
    },
    onRender: function(config) {
        Wtf.perticularemployeegoals.superclass.onRender.call(this, config);      
    }, 
    validate:function(e){
        if(e.column==7)
        {  
            if(e.record.get('gstartdate') > e.value)
            {
                return false;
            }
        }
        if(e.column==6)
        {
            if(e.record.get('genddate')!="")

            {
                if(e.record.get('genddate') < e.value)
                {
                    return false;
                }
            }
        }
    },
    docuploadhandler:function(e, t){
        if(e.target.className == "pwndCommon addComment" )
        {

            if(this.goalgrid.getSelectionModel().getCount()==0){
                calMsgBoxShow(131,0);
            }
            else{
                this.goalrec=this.goalgrid.getSelectionModel().getSelections();
                this.goalarr=[];
                for(var i=0;i<this.goalrec.length;i++){
                    this.goalarr.push(this.goalrec[i].get('gid'));
                }
                this.addcom=new Wtf.goalComment({
                    width:390,
                    modal:true,
                    height:250,
                    title:WtfGlobal.getLocaleText("hrms.performance.goal.comments"),
                    resizable:false,
                    layout:'fit',
                    note: WtfGlobal.getLocaleText("hrms.common.Fillupthefollowingform"),
                    read:false,
                    blank:false,
                    viewflag:false,
                    applybutton:true,
                    commentflag:true,
                    goalarr:this.goalarr,
                    ds:this.goalstore,
                    cleargrid:this.goalgrid
                });
                this.addcom.show();
            }
        }else{
            if(e.target.className == "pwndHRMS viewComment")
                if(this.goalgrid.getSelectionModel().getCount()==0||this.goalgrid.getSelectionModel().getCount()>1){
                    calMsgBoxShow(131,0);
                }else{
                    this.viewcom=new Wtf.goalComment({
                        width:390,
                        modal:true,
                        height:250,
                        title:WtfGlobal.getLocaleText("hrms.performance.goal.comments"),
                        resizable:false,
                        layout:'fit',
                        note:WtfGlobal.getLocaleText("hrms.performance.Employeecommentis"),
                        read:true,
                        blank:true,
                        viewflag:true,
                        comnt:this.goalgrid.getSelectionModel().getSelected().get('gcomment'),
                        applybutton:false
                    });
                    this.viewcom.show();
                }
        
        }
    },
    insertgoal:function(){
       
        this.p=new this.goalRecord({
            gname:'',
            gdescription:'',
            gwth:'',
            gcontext:'',
            gpriority:'',
            gstartdate:'',
            genddate:'',
            gcomment:'',
            internal:true,
            percentcomp:0

        })
        this.goalgrid.stopEditing();

        this.c=this.goalstore .getCount();

        this.goalstore.insert(this.c,this.p);

    },
    checkRecord : function(record){
        var bFields="";
        var vflag=0;
        bFields+=record.gname==""?WtfGlobal.getLocaleText("hrms.performance.goal.name")+", ":"";
        if(record.internal)
            bFields+=record.gdescription==""?WtfGlobal.getLocaleText("hrms.performance.goal.description")+", ":"";
        bFields+=record.gwth==""?WtfGlobal.getLocaleText("hrms.performance.weightage")+", ":"";
        bFields+=record.gcontext==""?WtfGlobal.getLocaleText("hrms.performance.context")+", ":"";
        bFields+=record.gpriority==""?WtfGlobal.getLocaleText("hrms.performance.priority")+", ":"";
        bFields+=record.gstartdate==""? WtfGlobal.getLocaleText("hrms.common.start.date")+", ":"";
        bFields+=record.genddate==""? WtfGlobal.getLocaleText("hrms.common.due.date")+", ":"";
        bFields=bFields.substring(0, Math.max(0, bFields.length-2));
        if(bFields.length>0)
        {
            vflag=0;
            calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.performance.Followingfieldsareblank")+":<br>"+bFields],0,null,370);
        }else{
            vflag=1;

        }
        return vflag;
    },
    checkColumnRecord : function(record, k){
        var columnArr = ["gname", "gdescription", "gwth", "gcontext", "gpriority", "gstartdate", "genddate"];
        var bFields="";
        if(record.get(columnArr[k] == ""))
            bFields+=columnArr[k];
        var vflag=0;
//        bFields+=record.gname==""?"Goal Name, ":"";
//        if(record.internal)
//            bFields+=record.gdescription==""?"Goal Description, ":"";
//        bFields+=record.gwth==""?"Weightage, ":"";
//        bFields+=record.gcontext==""?"Context, ":"";
//        bFields+=record.gpriority==""?"Priority, ":"";
//        bFields+=record.gstartdate==""?"Start Date, ":"";
//        bFields+=record.genddate==""?"End Date, ":"";
//        bFields=bFields.substring(0, Math.max(0, bFields.length-2));
        if(bFields.length>0)
        {
            vflag=0;
            //calMsgBoxShow(["Warning","Following fields are blank:<br>"+bFields],0,null,370);
        }else{
            vflag=1;

        }
        return vflag;
    },
    saveData:function(){
        var jsondata = "";
        var record;
        var bFields="";
        var vflag=0;
        var columnArr = ["gname", "gdescription", "gwth", "gcontext", "gpriority", "gstartdate", "genddate"];
        var columnArrName = [WtfGlobal.getLocaleText("hrms.performance.goal.name"), WtfGlobal.getLocaleText("hrms.performance.goal.description"),WtfGlobal.getLocaleText("hrms.performance.weightage"), WtfGlobal.getLocaleText("hrms.performance.context"), WtfGlobal.getLocaleText("hrms.performance.priority"),WtfGlobal.getLocaleText("hrms.common.start.date"), WtfGlobal.getLocaleText("hrms.common.due.date")];
        var records = this.goalstore.getModifiedRecords();
        if(records.length==0){
          //  calMsgBoxShow(["Warning","Fill all the fields"],0);
        }else{
            for(var k=0; k<7; k++){
                for(var i=0;i<records.length ;i++){
                  record=records[i];
                  //  vflag=this.checkColumnRecord(record, k);
                  if(record.get(columnArr[k]) == ""){
                        bFields+=columnArrName[k]+", ";
                        break;
                  }
                }
            }
            bFields=bFields.substring(0, Math.max(0, bFields.length-2));
            if(bFields.length>0){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.performance.Followingfieldsareblank")+":<br>"+bFields],0,null,370);
            } else {
                vflag = 1;
            }
//            for(var i=0;i<records.length ;i++){
//                record=records[i].data;
//                vflag=this.checkRecord(record);
//                if(vflag==0){
//                    break;
//                }
//                jsondata += "{'gname':'" + record.gname + "',";
//                jsondata += "'gid':'" + record.gid + "',";
//                jsondata += "'gdescription':'" + record.gdescription + "',";
//                jsondata += "'gwth':'" + record.gwth + "',";
//                jsondata += "'gcontext':'" + record.gcontext + "',";
//                jsondata += "'gpriority':'" + record.gpriority + "',";
//                jsondata += "'gcomplete':'" + record.percentcomp + "',";
//                jsondata += "'gstartdate':'" + WtfGlobal.convertToGenericDate(record.gstartdate) + "',";
//                jsondata += "'genddate':'" + WtfGlobal.convertToGenericDate(record.genddate) + "',";
//                jsondata += "'gcomment':'" + record.gcomment + "'},";
//            }

            if(vflag==1){

                var finalJson=[];
                for(var i=0;i<records.length;i++) {
                    record=records[i].data;
                    finalJson.push({
                            gname:record.gname,
                            gid:record.gid,
                            gdescription:record.gdescription,
                            gwth:record.gwth,
                            gcontext:record.gcontext,
                            gpriority:record.gpriority,
                            gcomplete:record.percentcomp,
                            gstartdate:WtfGlobal.getLongForLocale(record.gstartdate),
                            genddate:WtfGlobal.getLongForLocale(record.genddate),
                            gcomment:record.gcomment
                    });
                }
                
                this.savedata.disable();
                this.addbutton.disable();
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
                    //                url:Wtf.req.base + "hrms.jsp",
                    url:"Performance/Goal/insertGoal.pf",
                    params: {
                        flag:205,
                        jsondata:Wtf.encode(finalJson),
                        empid:this.empid
                    }
                }, this,
                function(response){
                    this.goalstore.commitChanges();
                    this.goalstore.reload();
                    this.savedata.enable();
                    this.addbutton.enable();
                    calMsgBoxShow(100,0,false,250);
                    var a=Wtf.getCmp("DSBMyWorkspaces");
                	if(a){
                        a.doSearch(a.url,'');
                    }
                },
                function(response)
                {
                    calMsgBoxShow(101,1);
                    this.savedata.enable();
                    this.addbutton.enable();
                })
            }
        }
    },  
    deletegoal:function(){
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),
            msg:deleteMsgBox('goal'),
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    this.delkey=this.sm.getSelections();
                    this.ids=[];
                    this.sm.clearSelections();
                    var store=this.goalgrid.getStore();
                    if(this.goalstore.getModifiedRecords().length==this.delkey.length){
                    	this.savedata.disable();
                    }
                    for(var i=0;i<this.delkey.length;i++){
                        var rec=this.goalstore.indexOf(this.delkey[i]);
                        WtfGlobal.highLightRow(this.goalgrid,"FF0000",5, rec)
                        if(this.delkey[i].get('gid'))
                        {
                            this.ids.push(this.delkey[i].get('gid'));
                        }
                        else{
                        	this.goalstore.remove(this.delkey[i]);
                        }
                    }
                    if(this.ids.length>0)
                    {
                        calMsgBoxShow(201,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Performance/Goal/assignedgoalsdelete.pf",
                            params:{
                                flag:45,
                                ids:this.ids
                            }
                        },this,
                        function(){
                            calMsgBoxShow(44,0);
                            WtfGlobal.delaytasks(this.goalstore);
                        },
                        function(){
                            calMsgBoxShow(45,1);

                        }

                        )
                    }
                }
            }
        });
    },
    archivegoal:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0){
            calMsgBoxShow(42,1);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms.performance.Areyousurewantarchiveselectedgoal"),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.archiverec=this.goalgrid.getSelectionModel().getSelections();
                        this.archivearr=[];
                        this.archivearr1=[];
                        for(var i=0;i<this.archiverec.length;i++){
                            this.archivearr.push(this.archiverec[i].get('gid'));
                            this.archivearr1.push(this.archiverec[i].get('gname'));
                        }
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Performance/Goal/insertGoal.pf",
                            params:  {
                                flag:205,
                                archiveid:this.archivearr,
                                archive:"true",
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
                            calMsgBoxShow(102,0);
                        }, 
                        function(){
                            calMsgBoxShow(27,1);
                        })
                    }
                }
            })
        }
    },
    viewComment:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0||this.goalgrid.getSelectionModel().getCount()>1){
            calMsgBoxShow(131,0);
        }
        else{
            this.viewcom=new Wtf.goalComment({
                width:390,
                modal:true,
                height:250,
                title:WtfGlobal.getLocaleText("hrms.performance.goal.comments"),
                resizable:false,
                layout:'fit',
                note:WtfGlobal.getLocaleText("hrms.performance.Employeecommentis"),
                read:true,
                blank:true,
                viewflag:true,
                comnt:this.goalgrid.getSelectionModel().getSelected().get('gcomment'),
                applybutton:false
            });
            this.viewcom.show();
        }
    }, 
    addComment:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0){
            calMsgBoxShow(131,0);
        }
        else{
            this.goalrec=this.goalgrid.getSelectionModel().getSelections();
            this.goalarr=[];
            for(var i=0;i<this.goalrec.length;i++){
                this.goalarr.push(this.goalrec[i].get('gid'));
            }
            this.addcom=new Wtf.goalComment({
                width:390,
                modal:true,
                height:250,
                title:WtfGlobal.getLocaleText("hrms.performance.goal.comments"),
                resizable:false,
                layout:'fit',
                note: WtfGlobal.getLocaleText("hrms.common.Fillupthefollowingform"),
                read:false,
                blank:false,
                viewflag:false,
                applybutton:true,
                goalarr:this.goalarr,
                ds:this.goalstore,
                commentflag:true,
                cleargrid:this.goalgrid
            });
            this.addcom.show();
        }
    },
    getDetailPanel:function(){
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.goalgrid,
            Store:this.goalstore,
            modulename:'Goals',
            height:200,
            mapid:0,
            id2:this.id
        });
    },
    decideAction:function (){
        var rec=this.sm.getSelections();
        var gid="";
        if(rec.length>0){
            gid=rec[0].data["gid"];
            if(gid!==undefined && gid!=''){
                getDocsAndCommentList(this.goalgrid,gid,1,this.id);
            } else {
                this.archivebutton.setDisabled(true);
            }
        }        
    },
    addWeightage:function(){
        this.goalgrid.stopEditing();
        WtfGlobal.showmasterWindow(4,Wtf.wthstore,"Add");
    },
    addContext:function(){
        this.goalgrid.stopEditing();
        WtfGlobal.showmasterWindow(2,Wtf.contextstore,"Add");
    },
    addPriority:function(){
        this.goalgrid.stopEditing();
        WtfGlobal.showmasterWindow(3,Wtf.priostore,"Add");
    },
    addCompleted:function(){
        this.goalgrid.stopEditing();
        WtfGlobal.showmasterWindow(5,Wtf.completedStore,"Add");
    }
}); 
