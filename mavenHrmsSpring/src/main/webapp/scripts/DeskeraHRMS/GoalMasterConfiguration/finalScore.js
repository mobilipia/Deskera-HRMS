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
Wtf.finalScore=function(config){
    Wtf.finalScore.superclass.constructor.call(this,config);
    Wtf.form.Field.prototype.msgTarget='side';
    this.appraisalView = 0;
};
Wtf.extend(Wtf.finalScore,Wtf.Panel,{
    initComponent:function(config){
        Wtf.finalScore.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.finalScore.superclass.onRender.call(this,config);
        if(!Wtf.StoreMgr.containsKey("prat")){
            Wtf.prat.load();
            Wtf.StoreMgr.add("prat",Wtf.prat)
        }

        this.record = Wtf.data.Record.create([{
            name:'fid'
        },{
            name:'eid'
        },{
            name:'ename'
        },{
            name:'desig'
        },{
            name:'desigid'
        },{
            name:'date',
            type:'date'
        },{
            name:'man'
        },{
            name:'apptype'
        },{
            name:'cscore'
        },{
            name:'gscore'
        },{
            name:'tscore'
        },{
            name:'empcom'
        },{
            name: 'performance'
        },{
            name:'mancom'
        },{
            name: 'prate'
        },{
            name: 'status'
        },{
            name: 'gapscore'
        },{
            name: 'reviewstatus'
        },{
            name: 'reviewcomment'
        },{
            name: 'salary'
        },{
            name: 'prevsalary'
        },{
            name: 'salaryrecommend'
        },{
            name: 'newdesignation'
        },{
            name: 'newdesignationname'
        },{
            name: 'newdepartment'
        },{
            name: 'newdepartmentname'
        },{
            name: 'salaryincrement'
        },{
            name: 'performance'
        },{
            name: 'appcyclesdate'
        },{
            name: 'appcycleedate'
        },{
            name: 'appcyclename'
        },{
            name: 'appcycleid'
        }]);

        var summary = new Wtf.grid.GroupSummary();
        this.ds = new Wtf.data.GroupingStore({
            baseParams: {
                flag: 136,
                finalreport:this.myfinalreport,
                userid:this.userid,
                reviewappraisal:this.reviewappraisal
            },
            url: Wtf.req.base + "hrms.jsp",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },
            this.record
            ),
            sortInfo:{
                field: 'apptype',
                direction: "ASC"
            },
            groupField:(this.myfinalreport=='1')?'apptype':'ename'
        });

        calMsgBoxShow(202,4,true);
        this.ds.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar()
        },this);
        this.ds.on('beforeload', function(){
            this.ds.baseParams={
                flag: 136,
                finalreport:this.myfinalreport,
                userid:this.userid,
                reviewappraisal:this.reviewappraisal,
                appraisalcycid:this.appTypeCombo.getValue(),
                view : this.appraisalView
            }
        }, this);
        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });


        this.apprec=new Wtf.data.Record.create([
        {
            name:'appcycleid'
        },
        {
            name:'appcycle'
        },
        {
            name:'startdate'
        },
        {
            name:'enddate'
        },
        {
            name:'currentFlag'
        }
        ]);
           this.appTypeStore =  new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url:"Performance/Appraisalcycle/getAppraisalcycleform.pf",
            baseParams: {
                flag: 168
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.apprec),
            autoLoad : false
        });
        this.appTypeStore.load();
        this.appTypeStore.on('load',function(){
            if(this.appTypeStore.getCount()>0){
                var row=this.appTypeStore.findBy(this.findrecord,this);
                if(row!=-1) {
                    this.appTypeCombo.setValue(this.appTypeStore.getAt(row).get('appcycleid'));
                }
                this.ds.baseParams={
                    flag: 136,
                    finalreport:this.myfinalreport,
                    userid:this.userid,
                    reviewappraisal:this.reviewappraisal,
                    appraisalcycid:this.appTypeCombo.getValue()
                }
                this.ds.load({
                    params : {
                        start:0,
                        limit:this.finalGrid.pag.pageSize
                    }
                });
            }
        },this);
        this.appTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            mode:"local",
            valueField:'appcycleid',
            displayField:'appcycle',
            store:this.appTypeStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle")+"*",
//            allowBlank:false,
            width:150,
            typeAhead:true
        });
        this.appTypeCombo.on('select',function(){
                    this.ds.baseParams={
                        flag: 136,
                        finalreport:this.myfinalreport,
                        userid:this.userid,
                        reviewappraisal:this.reviewappraisal,
                        appraisalcycid:this.appTypeCombo.getValue()
                    }
                this.ds.load({
                        params:{
                             start:0,
                             limit:this.finalGrid.pag.pageSize
                        }
                    });
        },this);
        this.arr=[];
        this.arr.push(this.sm);
        this.arr.push({
            header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
            hidden:(this.myfinalreport!='1')?false:true,
            dataIndex: 'ename'
        },{
            header: WtfGlobal.getLocaleText("hrms.common.OldDesignation"),
            dataIndex: 'desig',
            hideable: false,
            summaryRenderer: function(v, params, data){
                    return 'Total';
                }

        },{
            header: WtfGlobal.getLocaleText("hrms.performance.new.designation"),
            dataIndex: 'newdesignationname'
        },{
            header:"<div align=\"right\">Previous Salary("+WtfGlobal.getCurrencySymbol()+")</div>",
            dataIndex: 'prevsalary',
            renderer:function(val){
                return('<div align=\"right\">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
            }
        },{
            header:"<div align=\"right\">Current Salary("+WtfGlobal.getCurrencySymbol()+")</div>",
            dataIndex: 'salary',
            renderer:function(val){
                return('<div align=\"right\">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
            }
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.appraisal.date"),
            dataIndex: 'date',
            hidden:true,
            renderer:WtfGlobal.onlyDateRenderer,
            sortable:true
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.approving.appraiser"),
            hidden:true,
            dataIndex: 'man'
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle"),
            dataIndex: 'apptype'
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.competency.score"),
            dataIndex: 'cscore',
            summaryType: 'average',
            align:'right',
            scope:this,
            renderer:WtfGlobal.numericPrecisionRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.goal.score"),
            dataIndex: 'gscore',
            align:'right',
            summaryType: 'average',
            scope:this,
            renderer:WtfGlobal.numericPrecisionRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.total.score"),
            dataIndex: 'tscore',
            summaryType: 'average',
            align:'right',
            scope:this,
            hidden:true,
            renderer:WtfGlobal.numericPrecisionRenderer
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.performance.rating"),
            hidden:true,
            dataIndex: 'performance',
            scope:this
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.salary.increment")+"(%)",
            dataIndex: 'salaryincrement',
            align:'right',
            scope:this
        },{
            header: WtfGlobal.getLocaleText("hrms.common.Comment"),
            dataIndex: 'reviewcomment'
        },{
            header: WtfGlobal.getLocaleText("hrms.performance.review.status"),
            id : 'reviewStat',
            hidden : true,
            dataIndex: 'reviewstatus',
            renderer : function(val) {
                if(val=='0')
                    return '<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.recruitment.pending")+'</FONT>'
                else if(val=='1')
                    return '<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.common.Unapproved")+'</FONT>'
                else if(val=='2')
                    return '<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.common.Approved")+'</FONT>'
            }
        });

        this.cm = new Wtf.grid.ColumnModel(this.arr);

        this.viewAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.view.appraisal"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.select.employee.appraisal.details"),
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:95,
            //hidden : (userroleid == 1)? true : false,
            disabled:true,
            hidden:true,
            handler:this.viewappraisal,
            scope:this
        });

        this.viewSumApp = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.summary.appraisal.report"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.click.view.summary.appraisal"),
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:95,
            //hidden : (userroleid == 1)? true : false,
            id : "viewSum"+this.id,
            disabled:true,
            handler:function(){
                this.viewSumappraisal("viewDet"+this.id, "viewSum"+this.id, 0)
            },
            scope:this
        });
        this.viewDetApp = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.detail.appraisal.report"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.view.appraisal.detail"),
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:95,
            //hidden : (userroleid == 1)? true : false,
            id : "viewDet"+this.id,
            handler:function(){
                this.viewSumappraisal("viewSum"+this.id, "viewDet"+this.id, 1)
            },
            scope:this
        });

        this.subButton= new Wtf.Button({
            text: WtfGlobal.getLocaleText("hrms.common.Save"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.save.details.employee.appraisal"),
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            minWidth:47,
            id:"sub",
            disabled:true,
            scope:this,
            handler:this.performance
        });

        this.submitAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.generate.report"),
            iconCls:getButtonIconCls(Wtf.btype.reportbutton),
            minWidth:60,
            handler:this.filterFunction,
            scope:this
        });

        this.approveAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.overall.approve.appraisal"),
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            minWidth:90,
            disabled:true,
            hidden:true,
            scope:this,
            handler:function(){
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                    msg:WtfGlobal.getLocaleText("hrms.performance.want.to.approve.appraisal")+'<br><br><b>'+WtfGlobal.getLocaleText("hrms.common.data.cannot.changed.later.msg")+'</b></br></br>',
                    buttons:Wtf.MessageBox.YESNO,
                    icon:Wtf.MessageBox.QUESTION,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            this.reviewAppraisal(true,330,400)
                        }
                    }
                })
            }
        });

        this.unapproveAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.appraisal.unapprove.appraisal"),
            iconCls:getButtonIconCls(Wtf.btype.cancelbutton),
            minWidth:90,
            disabled:true,
            scope:this,
            hidden:true,
            handler:function(){
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                    msg:WtfGlobal.getLocaleText("hrms.appraisal.want.unapprove.appraisal")+'<br><br><b>'+WtfGlobal.getLocaleText("hrms.common.data.cannot.changed.later.msg")+'</b></br></br>',
                    buttons:Wtf.MessageBox.YESNO,
                    icon:Wtf.MessageBox.QUESTION,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            this.reviewAppraisal(false,250,400)
                        }
                    }
                })
            }
        });

        this.addComment = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.AddComment"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            disabled:true,
            minWidth:60,
            hidden:true,
            handler:this.addcomments,
            scope:this
        });

        this.startDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.start.date"),
            width : 125,
            name:'startdate',
            value:new Date(),
            allowBlank:false,
            readOnly:true,
            format:'m/d/Y'
        });

        this.endDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.end.date"),
            width : 125,
            name:'startdate',
            value:new Date(),
            allowBlank:false,
            readOnly:true,
            format:'m/d/Y'
        });

        var kwlGrid=Wtf.KwlGridPanel;
        var finlabtns=new Array();
        if(this.reviewer){
            finlabtns.push('-');
            finlabtns.push(this.viewAppraisal);
            finlabtns.push(this.approveAppraisal);
            finlabtns.push(this.unapproveAppraisal);
            finlabtns.push(this.addComment);
        }else{
            if(this.myfinalreport==0){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.finalscore, Wtf.Perm.finalscore.manage)){
                    finlabtns.push('-');
                    finlabtns.push(this.viewAppraisal);
                    kwlGrid=Wtf.KwlEditorGridPanel;
                }
            }
//            finlabtns.push('->');
//            finlabtns.push('From:');
//            finlabtns.push(this.startDate);
//            finlabtns.push('To:');
//            finlabtns.push(this.endDate);
//            finlabtns.push(this.submitAppraisal);
        }
        finlabtns.push(this.viewSumApp);
        finlabtns.push('-');
        finlabtns.push(this.viewDetApp);
        if(this.myfinalreport!=1){
        finlabtns.push('->');
        finlabtns.push(WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle")+':');
        finlabtns.push(this.appTypeCombo);
        }
         var grptext="";
        this.finalGrid=new kwlGrid({
            cm:this.cm,
            store:this.ds,
            border:false,
            sm:this.sm,
            height:600,
            loadMask:true,
            plugins:summary,
            view: new Wtf.grid.GroupingView({
                forceFit: true,
                showGroupName:false,
                enableGroupingMenu: false,
                enableNoGroups:false,
                hideGroupedColumn: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.no.appraisal.done.till")),
                groupTextTpl:((this.myfinalreport!=1 && this.reviewer==true)?'{text} (Select all Appraisal records to Approve)':'{text}')
            }),
            searchLabel:WtfGlobal.getLocaleText("hrms.common.QuickSearch"),
            searchEmptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg"),
            displayInfo:true,
            searchField:"ename",
            clicksToEdit:1,
            tbar:finlabtns
        });
        this.finalGrid.on("afteredit",this.fillGridValue,this);
        this.finalGrid.on('beforeedit',this.checkEditable,this);

        this.finalInfopanel = new Wtf.Panel({
            layout:'border',
            border:false,
            items:[
            {
                region:'center',
                border:false,
                frame:false,
                layout:'fit',
                items:[this.finalGrid]
            }
            ]
        });

        this.add(this.finalInfopanel);
        if(this.myfinalreport==1){
        //this.perrat.disable();
        //this.subButton.disable();
        }
        if(this.reviewer){
            this.sm.on("selectionchange",function() {
                WtfGlobal.enableDisableBtnArr(finlabtns, this.finalGrid, [1], [3,4]);
                if(this.sm.hasSelection()){
                    this.statusrec=this.sm.getSelections();
                    this.checkRec=this.statusrec[0].get('eid');
                    this.disableFlag=false;
                    for(var i=0;i<this.statusrec.length;i++){
                        if(this.checkRec!=this.statusrec[i].get('eid')){
                            this.disableFlag=true;
                        }
                    }
                    var recArr = this.finalGrid.store.queryBy(function(record) {
                        if(record.get("eid")==this.checkRec)
                            return true;
                        else
                            return false;
                     },this);
                    if(recArr.length!=this.statusrec.length)
                        this.disableFlag=true;
                    
                    if(this.disableFlag) {
                        this.approveAppraisal.disable();
                    } else{
                        this.approveAppraisal.enable();
                    }
                } else{
                    this.approveAppraisal.disable();
                }
            },this);  
        }else{
            if(this.myfinalreport==0){
                this.sm.on("selectionchange",function(){
                    if(this.sm.hasSelection()){
                    // this.subButton.enable();
                    }else{
                    //  this.subButton.disable();
                    }
                    WtfGlobal.enableDisableBtnArr(finlabtns, this.finalGrid, [1], []);
                },this);
            }
        }
    },//end of init component

    rateRenderer:function(val){
        if(val!="none"){
            var newval=Wtf.prat.getAt(Wtf.prat.find('id',val)).get('name');
            return newval;
        }
        else
            return '';
    },

    fillGridValue:function (e){
        for(i=0;i<Wtf.prat.getCount();i++){
            if(Wtf.prat.getAt(i).get("prat") == e.value){
                this.ds.getAt(e.row).set("prate",e.value);
            }
        }
    },
    viewappraisal:function(){
        if(this.finalGrid.getSelectionModel().getCount()==0||this.finalGrid.getSelectionModel().getCount()>1)
        {
            calMsgBoxShow(42,2);
        }
        else{
            var record=this.finalGrid.getSelectionModel().getSelected().data;
            var main=Wtf.getCmp("goalmanagementtabpanel");
            var demoTab=Wtf.getCmp(record.eid+"viewappraisal");
            if(demoTab==null)
            {
                demoTab=new Wtf.competencyEval({
                    id:record.eid+"viewappraisal",
                    title:WtfGlobal.getLocaleText({key:"hrms.performance.s.appraisal.form",params:[record.ename]}),
                    iconCls:getTabIconCls(Wtf.etype.hrmsmygoals),
                    autoScroll:true,
                    border:false,
                    closable:true,
                    viewappraisal:true,
                    employee:false,
                    read:true,
                    modify:true,
                    desid:record.desigid,
                    aid:record.fid,
                    empid:record.eid,
                    ename:record.ename,
                    designation:record.desig,
                    mname:record.man,
                    date:record.date,
                    empcom:record.empcom,
                    mancom:record.mancom,
                    compscore:record.cscore,
                    goalscore:record.gscore,
                    compgapscore:record.gapscore,
                    salaryrecommend:record.salaryrecommend,
                    salaryincrement:record.salaryincrement,
                    newdesignation:record.newdesignation,
                    newdepartment:record.newdepartment,
                    prating:record.prate,
                    apptype:record.apptype,
                    stdate:record.appcyclesdate,
                    eddate:record.appcycleedate
                });
                main.add(demoTab);
            }
            main.setActiveTab(demoTab);
            main.doLayout();
            Wtf.getCmp("as").doLayout();
        }
    },
    checkEditable:function(obj){
        if(obj.record.get('status')=="submitted"){
            obj.cancel=true;
        }else{
            obj.cancel=false;
        }
    },
    performance:function(){
        if(this.finalGrid.getSelectionModel().getSelections()==0)
        {
            calMsgBoxShow(42,2);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:WtfGlobal.getLocaleText("hrms.performance.want.provide.performance.rating")+'<br><br><b>'+WtfGlobal.getLocaleText("hrms.common.data.cannot.changed.later.msg")+'</b></br></br>',
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        var cnt=this.sm.getCount();
                        var rec=this.sm.getSelections();
                        this.status=true;
                        for(var i=0;i<cnt;i++)
                        {
                            if(rec[i].get('prate')=="none" || rec[i].get('status')=="submitted"){
                                this.status=false;
                            }
                        }
                        if(this.status){
                            var prate=[];
                            var fid=[];
                            for(i=0;i<cnt;i++)
                            {
                                prate.push(rec[i].get('prate'));
                                fid.push(rec[i].get('fid'));
                            }
                            calMsgBoxShow(200,4,true);
                            Wtf.Ajax.requestEx({
                                url:Wtf.req.base + "hrms.jsp?flag=159",
                                params: {
                                    item_fid:fid,
                                    item_prate:prate,
                                    rateperformance:"performance"
                                }
                            },
                            this,
                            function(){
                                calMsgBoxShow(29,0);
                                this.finalGrid.getSelectionModel().clearSelections() ;
                                this.ds.load();
                            },
                            function(){
                                calMsgBoxShow(27,2);
                                this.finalGrid.getSelectionModel().clearSelections() ;
                            })
                        }
                        else{
                            calMsgBoxShow(43,2);
                        }
                    }
                }
            })
        }
    },
    addPerformance:function(){
        this.finalGrid.stopEditing();
        WtfGlobal.showmasterWindow(13,Wtf.prat,"Add");
    },
    filterFunction:function(){
        if(this.startDate.getValue()!==""&&this.endDate.getValue()!=""){
            if(this.startDate.getValue()>this.endDate.getValue()){
                calMsgBoxShow(14,0);
            }
            else{
                this.finalGrid.getStore().baseParams={
                    flag: 136,
                    finalreport:this.myfinalreport,
                    userid:this.userid,
                    startdate:this.startDate.getValue().format("Y-m-d"),
                    enddate:this.endDate.getValue().format("Y-m-d")
                }
                this.finalGrid.getStore().load({
                    params:{
                        start:0,
                        limit:this.finalGrid.pag.pageSize,
                        appraisalcycid:this.appTypeCombo.getValue()
                    }
                });
            }
        }else{
            calMsgBoxShow(152,2);
        }

    },
    reviewAppraisal:function(reviewstatus,winheight,winwidth){
        var cnt=this.sm.getCount();
        var rec=this.sm.getSelections();
        this.appids=[];
        this.empids=[];
        this.appcycleids=[];
        for(var i=0;i<cnt;i++)
        {
            this.appids.push(rec[i].get('fid'));
            this.empids.push(rec[i].get('eid'));
            this.appcycleids.push(rec[i].get('appcycleid'));
        }
        this.salaryWin= new Wtf.approvalWindow({
            modal:true,
            title:WtfGlobal.getLocaleText("hrms.AuditGroup.Appraisal"),
            closable:true,
            resizable:false,
            layout:'fit',
            width:winwidth,
            height:winheight,
            finalGrid:this.finalGrid,
            reviewstatus:reviewstatus,
            appids:this.appids,
            empids:this.empids,
            appcycleids:this.appcycleids,
            ds:this.ds
        });
        this.salaryWin.show();
//        this.salaryInc= new Wtf.form.NumberField({
//            fieldLabel:'Salary Increment*',
//            width:200,
//            maxLength:2,
//            allowBlank:false
//        });
//
//        this.deptrec=new Wtf.data.Record.create([
//        {
//            name:'id'
//        },
//
//        {
//            name:'name'
//        }
//        ]);
//
//        this.deptStore =  new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
//            baseParams: {
//                configid:7,
//                flag:203
//            },
//            reader: new Wtf.data.KwlJsonReader1({
//                root:'data'
//            },this.deptrec),
//            autoLoad : false
//        });
//        this.deptStore.load();
//
//        this.positionrec=new Wtf.data.Record.create([
//        {
//            name:'id'
//        },
//
//        {
//            name:'name'
//        }
//        ]);
//
//        this.positionStore =  new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
//            baseParams: {
//                configid:1,
//                flag:203
//            },
//            reader: new Wtf.data.KwlJsonReader1({
//                root:'data'
//            },this.positionrec),
//            autoLoad : false
//        });
//
//        this.positionStore.load();
//
//        this.newDesignation = new Wtf.form.ComboBox({
//            fieldLabel:"New Designation",
//            store:this.positionStore,
//            anchor:'50.5%',
//            mode:'local',
//            hiddenName :'name',
//            valueField: 'id',
//            displayField:'name',
//            triggerAction: 'all',
//            typeAhead:true,
//            //listWidth:200,
//            width:200
//        });
//
//        this.newDepartment = new Wtf.form.ComboBox({
//            fieldLabel:'New Department',
//            store:this.deptStore,
//            mode:'local',
//            anchor:'50.5%',
//            valueField: 'id',
//            displayField:'name',
//            triggerAction: 'all',
//            typeAhead:true,
//            //listWidth:200,
//            width:200
//        });
//
//
//        this.salaryForm = new Wtf.form.FormPanel({
//            waitMsgTarget: true,
//            border : false,
//            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
//            autoScroll:false,
//            lableWidth :70,
//            layoutConfig: {
//                deferredRender: false
//            },
//            items:[this.salaryInc,this.newDepartment,this.newDesignation]
//        });
//
//        this.salaryPanel= new Wtf.Panel({
//            border: false,
//            layout:'fit',
//            autoScroll:false,
//            items:[{
//                border:false,
//                region:'center',
//                layout:"border",
//                items:[{
//                    region : 'north',
//                    height : 75,
//                    border : false,
//                    bodyStyle : 'background:white;border-bottom:1px solid #FFFFFF;',
//                    html: getTopHtml("Salary Increment", "Please enter following details to provide salary increment")
//                },{
//                    border:false,
//                    region:'center',
//                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
//                    layout:"fit",
//                    items: [this.salaryForm]
//                }]
//            }]
//        });
//
//        this.salaryWindow=new Wtf.Window({
//            iconCls:getButtonIconCls(Wtf.btype.winicon),
//            layout:'fit',
//            closable:true,
//            width:380,
//            title:'Salary Increment',
//            height:300,
//            border:false,
//            modal:true,
//            scope:this,
//            plain:true,
//            buttonAlign :'right',
//            buttons: [{
//                text:'Submit',
//                handler:function(){
//                    if(this.salaryForm.getForm().isValid()){
//                        this.reviewFunction(reviewstatus,this.salaryInc.getValue(),this.newDepartment.getValue(),this.newDesignation.getValue());
//                        this.salaryWindow.close();
//                    } else{
//                        return;
//                    }
//                },
//                scope:this
//            },{
//                text: 'Cancel',
//                scope:this,
//                handler:function(){
//                    this.salaryWindow.close();
//                }
//            }],
//            items: [this.salaryPanel]
//        });
//        if(reviewstatus){
//            this.salaryWindow.show();
//        } else{
//            this.reviewFunction(reviewstatus," "," "," ");
//        }
    },
    addcomments:function(){
        var cnt=this.sm.getCount();
        var rec=this.sm.getSelections();
        this.appids=[];
        for(var i=0;i<cnt;i++)
        {
            this.appids.push(rec[i].get('fid'));
        }
        this.addcom=new Wtf.goalComment({
            width:390,
            modal:true,
            height:250,
            title:WtfGlobal.getLocaleText("hrms.performance.review.comments"),
            resizable:false,
            layout:'fit',
            note:WtfGlobal.getLocaleText("hrms.common.Fillupthefollowingform"),
            read:false,
            blank:false,
            viewflag:true,
            applybutton:true,
            commentarr:this.appids,
            commentflag:false,
            ds:this.ds,
            cleargrid:this.finalGrid
        });
        this.addcom.show();
    },
    reviewFunction:function(reviewstatus,salaryinc,department,designation){
        var cnt=this.sm.getCount();
        var rec=this.sm.getSelections();
        this.appids=[];
        this.empids=[];
        this.appcycleids=[];
        for(var i=0;i<cnt;i++)
        {
            this.appids.push(rec[i].get('fid'));
            this.empids.push(rec[i].get('eid'));
            this.appcycleids.push(rec[i].get('appcycleid'));
        }
        Wtf.Ajax.requestEx({
            url:Wtf.req.base + "hrms.jsp?flag=165",
            params: {
                appraisalids:this.appids,
                employeeids:this.empids,
                reviewstatus:reviewstatus,
                addComment:false,
                salaryincrement:salaryinc,
                appcycleid:this.appcycleids,
                department:department,
                designation:designation
            }
        },
        this,
        function(){
            calMsgBoxShow(29,0);
            this.finalGrid.getSelectionModel().clearSelections() ;
            this.ds.load();
        },
        function(){
            calMsgBoxShow(27,2);
            this.finalGrid.getSelectionModel().clearSelections() ;
        })
    },
    findrecord:function(rec){
        if(rec.get('currentFlag')=="1"){
            return true;
        }else{
            return false;
        }
    },
    viewSumappraisal : function(id1,id2, flag){
        Wtf.getCmp(id1).enable();
        Wtf.getCmp(id2).disable();
        this.appraisalView = flag;
        if(flag == 1){
            this.viewAppraisal.show();
            this.approveAppraisal.show();
            this.unapproveAppraisal.show();
            this.addComment.show();
            this.cm.setHidden(3,true);
            this.cm.setHidden(4,true);
            this.cm.setHidden(6, false);
            this.cm.setHidden(7, false);
            this.cm.setHidden(12, false);
            this.cm.setHidden(13,true);
            if(this.myfinalreport!='1')
                this.cm.setHidden(15,false);
        }else {
            this.viewAppraisal.hide();
            this.approveAppraisal.hide();
            this.unapproveAppraisal.hide();
            this.addComment.hide();
            this.cm.setHidden(3,false);
            this.cm.setHidden(4,false);
            this.cm.setHidden(6, true);
            this.cm.setHidden(7, true);
            this.cm.setHidden(12, true);
            this.cm.setHidden(13, false);
            this.cm.setHidden(15,true);
        }
                
        this.ds.load({
            params:{
                start:0,
                limit:this.finalGrid.pag.pageSize,
                view : this.appraisalView
            }
        });
    }
});//end of extend
