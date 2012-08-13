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
Wtf.finalReport=function(config){
    Wtf.finalReport.superclass.constructor.call(this,config);
    this.myfinalReport = this.myfinalReport?true:false;
    this.reviewappraisal = this.reviewappraisal?true:false;
};
Wtf.extend(Wtf.finalReport,Wtf.Panel,{
    initComponent:function(config){
        Wtf.finalReport.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.finalReport.superclass.onRender.call(this,config);
        this.callGrid();
        this.add(this.finalGrid);
    },
    callGrid:function() {
        this.quickPanelSearch = new Wtf.KWLTagSearch({
            emptyText:WtfGlobal.getLocaleText("hrms.performance.search.manager.name"),
            width: 200,
            field: 'manager'
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
        },
        {
            name:'status'
        }
        ]);
        var employee=false;
        if(this.myfinalReport){
            employee=true;
        }
        this.appTypeStore =  new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url:"Performance/Appraisalcycle/getAppraisalcycleform.pf",
            baseParams: {
                flag: 168,
                employee:employee,
                myreport:true
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.apprec)
        });
        this.appTypeStore.load();
        this.appTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            mode:"local",
            valueField:'appcycleid',
            displayField:'appcycle',
            store:this.appTypeStore,
            emptyText:WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle"),
//            allowBlank:false,
            width:150,
            typeAhead:true
        });

        this.empRec = new Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'name'
        }
        ]);
        this.empStore = new Wtf.data.Store({
//            url:Wtf.req.base + 'hrms.jsp',
            url:"Performance/Appraisal/getUserForReviewerperAppCyc.pf",
            baseParams: {
                flag : 406,
                reviewappraisal:this.reviewappraisal
            },
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data'
            },this.empRec)
        });
        this.empCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:this.empStore,
            emptyText:WtfGlobal.getLocaleText("hrms.performance.please.select.employee"),
//            allowBlank:false,
            width:150,
            typeAhead:true
        });

        this.viewAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.view.appraisal"),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.select.employee.appraisal.details"),
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:95,
            disabled:true,
            hidden:this.myfinalReport?true:false,
            handler:this.viewappraisal,
            scope:this
        });
        this.approveAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.overall.approve.appraisal"),
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            minWidth:90,
            disabled:true,
            hidden:this.reviewappraisal?false:true,
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
                            this.reviewAppraisal(true,360,400);
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
            hidden:this.reviewappraisal?false:true,
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
                            this.reviewAppraisal(false,250,400);
                        }
                    }
                })
            }
        });
        this.totalinitiated=new Wtf.form.NumberField({
            readOnly:true,
            disabled:true,
            value:0,
            width:50
        });
        this.totalsubmitted=new Wtf.form.NumberField({
            readOnly:true,
            disabled:true,
            value:0,
            width:50
        });
        var _tb = [];
        _tb.push(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle")+':');
        _tb.push(this.appTypeCombo);
        if(!this.myfinalReport){
            _tb.push(WtfGlobal.getLocaleText("hrms.performance.select.employee")+':');
            _tb.push(this.empCmb);            
        }
        if(this.reviewappraisal){
            _tb.push('-');
        }
        //        _tb.push(this.viewAppraisal);
        _tb.push(this.approveAppraisal);
        if(!Wtf.cmpPref.annmng){
            if(this.reviewappraisal){
                _tb.push('-');
            }
            _tb.push(this.unapproveAppraisal);
        }
        //        if(!this.myfinalReport){
        _tb.push('->');
        _tb.push(WtfGlobal.getLocaleText("hrms.performance.total.no.appraisals")+':');
        _tb.push(this.totalinitiated);
        _tb.push(WtfGlobal.getLocaleText("hrms.performance.no.of.appraisals.submitted")+':');
        _tb.push(this.totalsubmitted);
        //        }
        this.ds = new Wtf.data.Store({
//            url: Wtf.req.base+"hrms.jsp",
            url:"Performance/NonAnonymousAppraisal/getfinalReportNonAnonymous.pf",
            baseParams: {
                flag:405,
                reviewappraisal:this.reviewappraisal
            },
            reader: new Wtf.data.KwlJsonReader({
                totalProperty: 'totalCount',
                root: "data"
            })
        });
        var summary = new Wtf.ux.grid.GridSummary();
        this.sm = new Wtf.grid.CheckboxSelectionModel({});
        this.finalGrid=new Wtf.grid.GridPanel({
            scope:this,
            //            baseCls:'expandedGrid',
            store:this.ds,
            sm:this.sm,
//            plugins:Wtf.cmpPref.annmng?'':summary,
            loadMask:true,
            view: new Wtf.ux.KWLGridView({
                forceFit:true
            }),
            enableColumnHide: false,
            columns:[],
            border:false,
            tbar:_tb
        });
        this.sm.on('selectionchange', function(){
            if(this.myfinalReport){
                //                WtfGlobal.enableDisableBtnArr(_tb, this.finalGrid, [3,6], [4]);
                    WtfGlobal.enableDisableBtnArr(_tb, this.finalGrid, [], []);
                }
            else if(!Wtf.cmpPref.annmng){
                //                WtfGlobal.enableDisableBtnArr(_tb, this.finalGrid, [5,8], [6]);
                    WtfGlobal.enableDisableBtnArr(_tb, this.finalGrid, [], [7]);
            }
            if(this.sm.hasSelection()){
                if(!Wtf.cmpPref.annmng){
                    this.statusrec=this.sm.getSelections();
                    this.checkRec='0';
                    this.disableFlag=false;
                    for(var i=0;i<this.statusrec.length;i++){
                        if(this.checkRec!=this.statusrec[i].get('reviewStat')){
                            this.disableFlag=true;
                        }
                    }
                    var recArr = this.finalGrid.store.queryBy(function(record) {
                        if(record.get("reviewStat")==this.checkRec)
                            return true;
                        else
                            return false;
                    },this);
                    if(recArr.length!=this.statusrec.length)
                        this.disableFlag=true;

                    if(this.disableFlag) {
                        this.unapproveAppraisal.disable();
                        this.approveAppraisal.disable();
                    } else{
                        this.unapproveAppraisal.enable();
                        this.approveAppraisal.enable();
                    }
                }
            } 
        }, this);
        this.ds.on("load",function(){
                var columns = [];
                columns.push(this.sm);
                if(this.ds.reader.jsonData.columns.length<1){
                    this.finalGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.appraisal.not.approved"));
                }
                else if(this.ds.reader.jsonData.totalappraisal != "0"  && this.ds.reader.jsonData.appraisalsubmitted != 0) {
                    this.finalGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.selected.appraisal.approved"));
                } else if (this.ds.reader.jsonData.totalappraisal != "0" && this.ds.reader.jsonData.appraisalsubmitted == 0){
                    this.finalGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.none.appraisers.submitted.appraisal.yet"));
                } else {
                    this.finalGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.common.no.record.display"));
                }
                this.totalinitiated.setValue(this.ds.reader.jsonData.totalappraisal);
                this.totalsubmitted.setValue(this.ds.reader.jsonData.appraisalsubmitted);
                Wtf.each(this.ds.reader.jsonData.columns, function(column){
                    if(column.renderer) {
                        if(!Wtf.isIE)
                            column.renderer = eval ("(" + column.renderer + ")");
                        else {
                            column.renderer=function(val,meta,record){
                                    var Str="<span class='valueSpan'><b>"+val+"</b></span>";
                                    if(record.data[''+column.dataIndex+'comment'] !=undefined)
                                        Str+="<span class='commentSpan' wtf:qtip='" + record.data[''+column.dataIndex+'comment'] + "'> "+ Wtf.util.Format.ellipsis(record.data[''+column.dataIndex+'comment'], 15) + "</span>"
                                    return Str;
                                };
                        }
                    }
                    if(column.summaryRenderer)
                        column.summaryRenderer = eval("(" + column.summaryRenderer + ")");
                    columns.push(column);
                }); 
                this.finalGrid.getColumnModel().setConfig(columns);
                this.storeloaded(this.ds);
                this.finalGrid.getView().refresh();
//                if(this.ds.getCount()>0){
//                    this.approveAppraisal.enable();
//                } else{
//                    this.approveAppraisal.disable();
//                }
        },this); 
        //        this.ds.on('datachanged', function() {
        //            var p = this.pP.combo.value;
        //            this.quickPanelSearch.setPage(p);
        //        }, this);

        this.appTypeStore.on('load',function(){
            if(this.appTypeStore.getCount()>0){
                var row=this.appTypeStore.findBy(this.findrecord,this);
                if(row!=-1) {
                    this.appTypeCombo.setValue(this.appTypeStore.getAt(row).get('appcycleid'));
                }
                if(!this.myfinalReport){
                    this.empStore.baseParams={
                        flag:406,
                        reviewer:false,
                        appcylid:this.appTypeCombo.getValue()
                    }
                    this.empStore.load();
                }
                else{
                    var recno;
                    if(this.apptype!==undefined && this.apptype!=""){
                        this.appTypeCombo.setValue(this.apptype);
                        recno = this.appTypeStore.find('appcycleid',this.apptype);
                    } else {
                        recno=this.appTypeStore.find("appcycleid",this.appTypeCombo.getValue());
                    }
                    if(recno!=-1){
                        var rec=this.appTypeStore.getAt(recno);
                        if(rec.data.status==1){
                            this.ds.baseParams={
                                flag: 405,
                                appraisalcycid:this.appTypeCombo.getValue(),
                                reviewappraisal:this.reviewappraisal
                            }
                            this.ds.load();
                        } else {
                            this.finalGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle.not.approved"));
                            this.ds.removeAll();
                            this.finalGrid.getView().refresh();
                        }
                    }
                }
            }else{
                  this.appTypeCombo.emptyText=WtfGlobal.getLocaleText("hrms.performance.no.appraisal.initiated");
                 this.appTypeCombo.reset();
            }
        },this);

        this.empStore.on('load',function(){
            if(this.empStore.getCount()>0){
                this.empCmb.setValue(this.empStore.getAt(0).get('id'));
                this.ds.baseParams={
                    flag: 405,
                    userid:this.myfinalReport?'':this.empCmb.getValue(),
                    appraisalcycid:this.appTypeCombo.getValue(),
                    reviewappraisal:this.reviewappraisal
                }
                this.ds.load();
            }else{
                this.empCmb.emptyText=WtfGlobal.getLocaleText("hrms.performance.no.employee.current.appraisal");
                this.empCmb.reset();
            }
        },this);

        this.appTypeCombo.on('select',function(a,b,c){
            if(!this.myfinalReport){
                this.empCmb.clearValue();
                this.empStore.baseParams={
                    flag:406,
                    reviewer:false,
                    appcylid:this.appTypeCombo.getValue()
                }
                this.empStore.load();
            }else{
                if(b.data.status==1){
                    this.ds.baseParams={
                        flag: 405,
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.ds.load();
                }else{
                     this.finalGrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle.not.approved"));
                     this.ds.removeAll();
                     this.finalGrid.getView().refresh();
                }
            }
        },this);
        this.empCmb.on('select',function(c,r,i){
            this.ds.baseParams={
                flag: 405,
                userid:this.empCmb.getValue(),
                appraisalcycid:this.appTypeCombo.getValue(),
                reviewappraisal:this.reviewappraisal
            }
            this.ds.load();
        },this);
    },  
    findrecord:function(rec){ 
        if(rec.get('currentFlag')=="1"){
            return true;
        }else{
            return false;
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
                    title:WtfGlobal.getLocaleText({key:"hrms.performance.s.appraisal.form", params:[record.ename]}),
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
                    mname:record.appraiser,
                    date:record.date,
                    empcom:record.empcom,
                    mancom:record.mcomment,
                    compscore:record.cscore,
                    goalscore:record.goalscore,
                    compgapscore:record.gapscore,
                    salaryrecommend:record.salaryrecommend,
                    salaryincrement:record.salaryinc,
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
    storeloaded:function(store){
        this.quickPanelSearch.StorageChanged(store);
    },
    reviewAppraisal:function(reviewstatus,winheight,winwidth){
        this.appids=[];
        var desig,dept;
        if(!reviewstatus){
            var rec=this.sm.getSelections();
            for(var i=0;i<this.sm.getCount();i++)
            {
                if(rec[i].get('fid') != "") {
                    this.appids.push(rec[i].get('fid'));
                }
            }
        } else {
            for(var i=0;i<this.ds.getCount();i++)
            {
                if(this.ds.getAt(i).get('desig') != "") {
                    desig=this.ds.getAt(i).get('desig');
                    dept=this.ds.getAt(i).get('department');
                    break;
                }
            }
        }
        if(Wtf.cmpPref.approveappraisal){
            this.salaryWin= new Wtf.approvalWindow({
                modal:true,
                title:WtfGlobal.getLocaleText("hrms.AuditGroup.Appraisal"),
                closable:true,
                resizable:false,
                layout:'fit',
                flag:170,
                width:winwidth,
                height:winheight,
                finalGrid:this.finalGrid,
                reviewstatus:reviewstatus,
                appids:this.appids,
                employeeid:this.empCmb.getValue(),
                appcycleid:this.appTypeCombo.getValue(),
                ds:this.ds,
                desig:desig,
                dept:dept
            });
            this.salaryWin.show();
        }else{
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp?flag=170",
                url:"Performance/NonAnonymousAppraisal/reviewNonAnonymousAppraisal.pf",
                params: {
                    employeeid:this.empCmb.getValue(),
                    appraisalcycleid:this.appTypeCombo.getValue(),
                    reviewstatus:reviewstatus,
                    appraisalids:this.appids
                }
            },
            this,
            function(res){
                var resp=eval('('+res+')');
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),""+resp.msg+""],0);
                this.finalGrid.getSelectionModel().clearSelections() ;
                this.ds.load();
            },
            function(){
                calMsgBoxShow(27,2);
                this.finalGrid.getSelectionModel().clearSelections() ;
            });
        }
    }
}); 
