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
Wtf.AppraisalReport=function(config){
    Wtf.AppraisalReport.superclass.constructor.call(this,config);
    this.myfinalReport = this.myfinalReport?true:false;
    this.reviewappraisal = this.reviewappraisal?true:false;
};
Wtf.extend(Wtf.AppraisalReport,Wtf.Panel,{
    initComponent:function(config){
        Wtf.AppraisalReport.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.AppraisalReport.superclass.onRender.call(this,config);
        this.callGrid();
    },
    callGrid:function() {
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
                grouper:'appraisalreport',
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
            emptyText:WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle"),//'Select appraisal cycle',
//            allowBlank:false,
            width:150,
            typeAhead:true
        });

        this.empRec = new Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'name'
        },{
            name:'isquestionemp'
        }
        ]);
        this.empStore = new Wtf.data.Store({
//            url:Wtf.req.base + 'hrms.jsp',
            url:"Performance/Appraisal/getUserForReviewerperAppCyc.pf",
            baseParams: {
                flag : 406,
                grouper:'appraisalreport',
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
            emptyText:WtfGlobal.getLocaleText("hrms.performance.select.employee"),//'Select employee',
            width:150,
            typeAhead:true,
            listeners:{
                scope:this,
                select:function(c,r,i)
                {
                    if(r.data.isquestionemp == "true"){
                        this.question=true;
                    } else {
                        this.question=false;
                    }
                }
            }
        });
        this.approveAppraisal = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.overall.approve.appraisal"),//'Approve Appraisal',
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            tooltip:WtfGlobal.getLocaleText("hrms.performance.approve.appraisals.tooltip"),//"Approve the appraisals submitted for the selected employee.",
            minWidth:90,
            disabled:true,
            hidden:this.reviewappraisal?false:true,
            scope:this,
            handler:function(){
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                    msg:WtfGlobal.getLocaleText("hrms.performance.want.to.approve.appraisal")+"<br><br><b>"+WtfGlobal.getLocaleText("hrms.common.data.cannot.changed.later.msg")+"</b></br></br>",//'Are you sure you want to approve the performance appraisal of selected employee(s)?<br><br><b>Note: This data cannot be changed later.</b></br></br>',
                    buttons:Wtf.MessageBox.YESNO,
                    icon:Wtf.MessageBox.QUESTION,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            this.reviewAppraisalFun(true,360,400);
                        }
                    }
                })
            }
        });
        var _tb = [];
        _tb.push(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle")+":");//'Appraisal Cycle : ');
        _tb.push(this.appTypeCombo);
        if(!this.myfinalReport){
            _tb.push(WtfGlobal.getLocaleText("hrms.performance.select.employee")+":");//'Select Employee : ');
            _tb.push(this.empCmb);
        }
        if(this.reviewappraisal){
            _tb.push('-');
            _tb.push(this.approveAppraisal);
        }

        _tb.push('-');
        this.exportPdf = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.export.pdf.format"),//'Export to Pdf Format',
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("hrms.performance.export.pdf.format.report.tooltip")},//'Export Appraisal Report to Pdf Format'},
            iconCls:'pwndExport exportpdf',
            handler:function() {
                this.Export2Pdf();
            }
        });
        this.printReport = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.common.print"),//'Print',
            scope:this,
            iconCls:'pwndPrint printData',
            tooltip:{text:WtfGlobal.getLocaleText("hrms.performance.print.appraisal.report")},//'Print Appraisal Report'},
            handler:function() {
                this.printRep();
            }
        });
        _tb.push(this.exportPdf,'-',this.printReport);


        this.dsRec = new Wtf.data.Record.create([
        {
            name:'totalappraisal'
        },{
            name:'appraisalsubmitted'
        },{
            name:'data'
        }
        ]);
        this.ds = new Wtf.data.Store({
//            url: Wtf.req.base+"hrms.jsp",
            url:"Performance/AnonymousAppraisal/getAppraisalReport.pf",
            baseParams: {
                flag:405,
                reviewappraisal:this.reviewappraisal
            },
            reader: new Wtf.data.KwlJsonReader1({
                totalProperty: 'totalCount',
                root: "data"
            },this.dsRec)
        });
        this.ds1Rec = new Wtf.data.Record.create([
        {
            name: 'selfcomment'
        },{
            name: 'selfcompscore'
        },{
            name: 'comptename'
        },{
            name: 'compmanwght'
        },{
            name: 'comptdesc'
        },{
            name: 'comments'
        },{
            name: 'nominalRat'
        }
        ]);
        this.ds1 = new Wtf.data.Store({
//            url: Wtf.req.base+"hrms.jsp",
            url:"Performance/AnonymousAppraisal/getAppraisalReportforGrid.pf",
            baseParams: {
                flag:408,
                reviewappraisal:this.reviewappraisal
            },
            reader: new Wtf.data.KwlJsonReader1({
                totalProperty: 'totalCount',
                root: "data"
            },this.ds1Rec)
        });
        this.goalArr = [{
            header:WtfGlobal.getLocaleText("hrms.performance.goals"),//"Goals",
            dataIndex:'gname',
            align:'center',
            sortable:true,
            renderer : function(val) {
                    return WtfGlobal.commentRenderer(val);
                }
            },{
                header:WtfGlobal.getLocaleText("hrms.common.assigned.by"),//"Assigned By",
                dataIndex:'assignedby',
                align:'center',
                sortable:true
            }
        ];

            this.goalArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.appraiser.rating"),//"Appraiser Rating",
                dataIndex:'gmanrat',
                align:'right',
                sortable:true,
                editor: this.mangoalRate,
                renderer:WtfGlobal.numericPrecisionRenderer
            },{
               header:WtfGlobal.getLocaleText("hrms.performance.appraiser.comment"),//"Appraiser Comment",
                dataIndex:'mangoalcomment',
                hidden:(!this.employee)?false:true,
                sortable:true,
                editor: this.mangoalComment,
                renderer : function(val) {
                    return WtfGlobal.commentRenderer(val);
                }
            });
            if(Wtf.cmpPref.selfapp){
            	this.goalArr.push({
            		header:WtfGlobal.getLocaleText("hrms.performance.self.rating"),//"Self Rating",
            		dataIndex:'gemprat',
            		sortable:true,
            		align:'right',
            		editor: this.empgoalRate,
            		renderer:WtfGlobal.numericPrecisionRenderer
            	},{
            		header:WtfGlobal.getLocaleText("hrms.performance.self.comments"),//"Self Comments",
            		dataIndex:'empgoalcomment',
            		sortable:true,
            		editor: this.empgoalComment,
            		renderer : function(val) {
                        return WtfGlobal.commentRenderer(val);
                }
            	});
            }
        this.goalcolCM=new Wtf.grid.ColumnModel(this.goalArr);
        this.GoalRecord = new Wtf.data.Record.create([{
            name: 'gid'
        },{
            name: 'goalid'
        },{
            name: 'gname'
        },{
            name: 'gwth'
        },{
            name:'gemprat'
        },{
            name:'gmanrat'
        },{
            name:'empgoalcomment'
        },{
            name:'mangoalcomment'
        },{
            name:'assignedby'
        },{
            name:'goalapprid'
        }]);
        this.dataReader = new Wtf.data.KwlJsonReader1({
            root: "data"
        },this.GoalRecord);

        this.goalstore= new Wtf.data.Store({
            url:"Performance/AnonymousAppraisal/getAppraisalReportGoalsforGrid.pf",
            baseParams: {
                flag:408,
                reviewappraisal:this.reviewappraisal
            },
            reader: this.dataReader
        });
        
        this.templatePanel=new Wtf.appreportTemplate({
            layout:'fit',
//            style: 'background:rgb(241,241,241);',
           // region:'north',
            height:205,
            border:false,
            id:this.id+'templatePanel'
        });


        this.competencyArr = [{
            header:WtfGlobal.getLocaleText("hrms.common.name"),//"Name",
            dataIndex:'comptename',
            sortable:true,
            renderer : function(val) {
                if(Wtf.isIE6 || Wtf.isIE7)
                    return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;'>"+val+"</pre>";
                return "<div style='white-space:pre-wrap;'>"+val+"</div>";
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.description"),//"Description",
            dataIndex:'comptdesc',
            sortable:true,
            renderer : function(val) {
                if(Wtf.isIE6 || Wtf.isIE7)
                    return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;'>"+val+"</pre>";
                return "<div style='white-space:pre-wrap;'>"+val+"</div>";
            }
        }];
        if(Wtf.cmpPref.selfapp){
        	this.competencyArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.self.appraisal.score"),//"Self Appraisal Score",
                dataIndex:'selfcompscore',
                align:'center',
                sortable:true
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.self.appraisal.comments"),//"Self Appraisal Comments",
                dataIndex:'selfcomment',
                sortable:true,
                renderer : function(val) {
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;'>"+val+"</pre>";
                    return "<div style='white-space:pre-wrap;'>"+val+"</div>";
                }
            });
        }

            this.competencyArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.appraiser.competency.score"),//"Appraiser Competency Score",
                dataIndex:'compmanwght',
                align:'center',
                renderer:function(val, metadata, record, rwIn, clIn, store){
                    if(Wtf.cmpPref.modaverage){
                    	return "<div>"+val+"</div><div style='font-weight:bold;'>["+ WtfGlobal.getLocaleText("hrms.performance.mod.avg")+": "+record.get('nominalRat')+"] </div>"//return "<div>"+val+"</div><div style='font-weight:bold;'>[ Mod Avg: "+record.get('nominalRat')+"] </div>"
                    } else {
                    	return "<div>"+val+"</div><div style='font-weight:bold;'>["+ WtfGlobal.getLocaleText("hrms.performance.avg") +": "+record.get('nominalRat')+"] </div>"//return "<div>"+val+"</div><div style='font-weight:bold;'>[ Avg: "+record.get('nominalRat')+"] </div>"
                    }
                }
                  
            },{ 
                header:WtfGlobal.getLocaleText("hrms.performance.overall.appraiser.comments"),//"Appraiser Comments",
                dataIndex:'comments',
                sortable:true,
                renderer : function(val) {
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;'>"+val+"</pre>";
                    return "<div style='white-space:pre-wrap;'>"+val+"</div>";
                }
            });



        this.competencycolCM=new Wtf.grid.ColumnModel(this.competencyArr);

        this.competencyGrid = new Wtf.grid.GridPanel({
            region:'center',
            store: this.ds1,
            title:WtfGlobal.getLocaleText("hrms.performance.competencies"),//'Competencies',
            cls:"gridWithUl",
            hidden:true,
            cm: this.competencycolCM,
            border:true,
            style: 'padding:20px;background:white;',
            bbar:Wtf.cmpPref.modaverage?["<b><span style='font-family:tahoma,arial,helvetica,sans-serif;font-size:10px;'>"+WtfGlobal.getLocaleText("hrms.performance.mod.avg")+" : "+WtfGlobal.getLocaleText("hrms.performance.mode.average.ratings")+"</span>"]:null,//Wtf.cmpPref.modaverage?["<b><span style='font-family:tahoma,arial,helvetica,sans-serif;font-size:10px;'>Mod Avg. : Average of ratings after excluding a minimum and a maximum rating. For e.g, mod average of 2, 3, 2, 4, 5, 3 is (2+3+4+3)/4</span>"]:null,
            frame:false,
            layout:'fit',
            autoHeight:true,
           // height:400,
            autoScroll:true,
            split: true,
            viewConfig:{
                forceFit:true
            }
        });
        this.QuestionAnswerGrid();
        this.goalGrid = new Wtf.grid.GridPanel({
         //   region:'south',
            store: this.goalstore,
            title:WtfGlobal.getLocaleText("hrms.performance.goals"),//'Goals',
           // cls:"abc",
            cm: this.goalcolCM,
            border:true,
            style: 'padding:20px;white;',
            //bbar:Wtf.cmpPref.modaverage?["<b><span style='font-family:tahoma,arial,helvetica,sans-serif;font-size:10px;'>Mod Avg. : Average of ratings after excluding a minimum and a maximum rating. For e.g, mod average of 2, 3, 2, 4, 5, 3 is (2+3+4+3)/4</span>"]:null,
            frame:false,
            layout:'fit',
           // autoHeight:true,
            height:400,
            autoScroll:true,
            //height:280,
            split: true,
            viewConfig:{
                forceFit:true
            }
        });

        this.reportArr=[];
        this.reportArr.push(this.templatePanel);
        if(Wtf.cmpPref.goal&&Wtf.cmpPref.competency){

            this.reportArr.push(this.QuestionAnswerGrid,this.competencyGrid,this.goalGrid);
        } else if(Wtf.cmpPref.goal && !Wtf.cmpPref.competency){
            this.reportArr.push(this.goalGrid);
        } else if(!Wtf.cmpPref.goal && Wtf.cmpPref.competency){
            this.reportArr.push(this.QuestionAnswerGrid,this.competencyGrid);
        }
        this.reportpanel=new Wtf.Panel({
            //layout:'border',
            border:false,
            tbar:_tb,
            autoScroll:true,
            bodyStyle:'background-color:white;',
            items:this.reportArr,
            bbar:["<b><span style='font-family:tahoma,arial,helvetica,sans-serif;font-size:10px;'>"+WtfGlobal.getLocaleText("hrms.performance.mod.avg.and.overall.competency.scores.appraisal.cycle")+"</span>"]//"<b><span style='font-family:tahoma,arial,helvetica,sans-serif;font-size:10px;'>Mod Avg. and Overall Competency Scores will be calculated only after the submission period of the Appraisal cycle is over.</span>"]
        });
        this.add(this.reportpanel);
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
                        recno=this.appTypeStore.findBy(function(b){
                            if(b.get('appcycleid')==this.appTypeCombo.getValue()){
                                return true;
                            }else{
                                return false;
                            }
                        },this);
                    }
                    if(recno!=-1){
                        var rec=this.appTypeStore.getAt(recno);
                        if(rec.data.status==1){
                         this.ds.baseParams={
                        flag: 407,
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.ds.load();
                    this.ds1.baseParams={
                        flag: 408,
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.ds1.load();
                    this.QuestionAnswerds.baseParams={
                        flag: 408,
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.QuestionAnswerds.load();
                        }else{
                            this.templatePanel.emptynotapprvTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                            this.competencyGrid.hide();
                            this.QuestionAnswerGrid.hide();
                            this.reportpanel.getBottomToolbar().setVisible(false);
                            this.goalGrid.hide();
                            this.exportPdf.setDisabled(true);
                            this.printReport.setDisabled(true);
                        }
                    }else{
                         this.templatePanel.emptyselctapprvTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                         this.competencyGrid.hide();
                         this.QuestionAnswerGrid.hide();
                         this.reportpanel.getBottomToolbar().setVisible(false);
                         this.goalGrid.hide();
                         this.exportPdf.setDisabled(true);
                         this.printReport.setDisabled(true);
                   }
                }
            }else{
                  this.appTypeCombo.emptyText=WtfGlobal.getLocaleText("hrms.performance.no.appraisal.initiated");//"No appraisal initiated  ";
                  this.appTypeCombo.reset();
                  this.templatePanel.emptynonappTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                  this.competencyGrid.hide();
                  this.QuestionAnswerGrid.hide();
                  this.reportpanel.getBottomToolbar().setVisible(false);
                  this.goalGrid.hide();
                  this.exportPdf.setDisabled(true);
                  this.printReport.setDisabled(true);
            }
            this.reportpanel.syncSize();
        },this);

        this.empStore.on('load',function(){
            if(this.empStore.getCount()>0){
                this.empName=this.empStore.getAt(0).get("name");
                this.empCmb.setValue(this.empStore.getAt(0).get('id'));
                if(this.empStore.getAt(0).get("isquestionemp") == "true"){
                    this.question=true;
                } else {
                    this.question=false;
                }
                this.ds.baseParams={
                    flag: 407,
                    userid:this.myfinalReport?'':this.empCmb.getValue(),
                    appraisalcycid:this.appTypeCombo.getValue(),
                    reviewappraisal:this.reviewappraisal
                }
                this.ds.load();
                this.ds1.baseParams={
                        flag: 408,
                        userid:this.myfinalReport?'':this.empCmb.getValue(),
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                }
                this.ds1.load();
                this.QuestionAnswerds.baseParams={
                    flag: 408,
                    userid:this.myfinalReport?'':this.empCmb.getValue(),
                    appraisalcycid:this.appTypeCombo.getValue(),
                    reviewappraisal:this.reviewappraisal
                }
                this.QuestionAnswerds.load();
                if(Wtf.cmpPref.overallcomments){
                    this.templatePanel.setHeight(305);
                } else {
                    this.templatePanel.setHeight(205);
                }
            }else{
                this.empCmb.emptyText=WtfGlobal.getLocaleText("hrms.performance.no.employee.current.appraisal"),//"No employee for current appraisal cycle  ";
                this.empCmb.reset();
                this.templatePanel.emptynonempTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                this.competencyGrid.hide();
                this.QuestionAnswerGrid.hide();
                this.reportpanel.getBottomToolbar().setVisible(false);
                this.goalGrid.hide();
                this.exportPdf.setDisabled(true);
                this.printReport.setDisabled(true);
            }
            this.reportpanel.syncSize();
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
                        flag: 407,
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.ds.load();
                    this.ds1.baseParams={
                            flag: 408,
                            appraisalcycid:this.appTypeCombo.getValue(),
                            reviewappraisal:this.reviewappraisal
                    }
                    this.ds1.load();
                    this.QuestionAnswerds.baseParams={
                            flag: 408,
                            appraisalcycid:this.appTypeCombo.getValue(),
                            reviewappraisal:this.reviewappraisal
                    }
                    this.QuestionAnswerds.load();
                }else{
                     this.templatePanel.emptynotapprvTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                     this.competencyGrid.hide();
                     this.QuestionAnswerGrid.hide();
                     this.reportpanel.getBottomToolbar().setVisible(false);
                     this.goalGrid.hide();
                     this.exportPdf.setDisabled(true);
                     this.printReport.setDisabled(true);
                }
            }
            this.reportpanel.syncSize();
        },this);
        this.empCmb.on('select',function(c,r,i){
            this.empName=r.get("name");
            this.ds.baseParams={
                flag: 407,
                userid:this.empCmb.getValue(),
                appraisalcycid:this.appTypeCombo.getValue(),
                reviewappraisal:this.reviewappraisal
            }
            this.ds.load();
            this.ds1.baseParams={
                        flag: 408,
                        userid:this.empCmb.getValue(),
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.ds1.load();
            this.QuestionAnswerds.baseParams={
                        flag: 408,
                        userid:this.empCmb.getValue(),
                        appraisalcycid:this.appTypeCombo.getValue(),
                        reviewappraisal:this.reviewappraisal
                    }
                    this.QuestionAnswerds.load();

        },this);
        this.ds.on('load',function(a,b,c){
            if(this.ds.getAt(0).data.data.length>0){
                  this.templatePanel.progTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild,this.ds.getAt(0).data.data);
                  if(b[0].data.data[0].isquestionemp=="true"){
                	  this.question = true;
                  }else{
                	  this.question = false;
                  }
                  if(this.question==undefined) {
                      if(this.ds.getAt(0).data.data[0].isquestionemp=="true") {
                          this.question = true;
                      } else {
                          this.question = false;
                      }
                  }
                  if(this.question) {
                      this.QuestionAnswerGrid.show();
                      this.competencyGrid.hide();
                      this.reportpanel.getBottomToolbar().setVisible(false);
                  } else {
                    this.competencyGrid.show();
                    this.reportpanel.getBottomToolbar().setVisible(true);
                    this.QuestionAnswerGrid.hide();
                  }
                  
                  this.goalGrid.show();
                  this.doLayout();
                  this.approveAppraisal.setDisabled(false);
                  this.exportPdf.setDisabled(false);
                  this.printReport.setDisabled(false);
                  this.goalstore.load({
                    params:{
                        empid:(this.myfinalReport)?loginid:this.empCmb.getValue(),
                        appcycleid:this.appTypeCombo.getValue(),
                        employee:(this.myfinalReport)?true:false
                    }
                });
            }else{
                if(this.reviewappraisal){
                if(this.ds.getAt(0).data.appraisalsubmitted==0||this.ds.getAt(0).data.appraisalsubmitted=="0"){
                    this.templatePanel.emptyTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                }else{
                    this.templatePanel.emptyreviewTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                }
                }else{
                     if(Wtf.cmpPref.reviewappraisal){
                        this.templatePanel.emptyreportTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                     }
                     else{
                        if(this.ds.getAt(0).data.appraisalsubmitted==0||this.ds.getAt(0).data.appraisalsubmitted=="0"){
                            this.templatePanel.emptynonapprTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                        } else {
                            this.templatePanel.emptynonapprreportTpl.overwrite(Wtf.getCmp(this.id+'templatePanel'+'appraisaldetails').el.dom.firstChild);
                        }
                     }
                }
                this.competencyGrid.hide();
                this.QuestionAnswerGrid.hide();
                this.reportpanel.getBottomToolbar().setVisible(false);
                this.goalGrid.hide();
                this.doLayout();
                this.approveAppraisal.setDisabled(true);
                this.exportPdf.setDisabled(true);
                this.printReport.setDisabled(true);
            }
            this.reportpanel.syncSize();
        },this);
        this.on("activate",function(){
            if(this.reportpanel!=null)
                this.reportpanel.doLayout();
        },this);
    },
    QuestionAnswerGrid : function(){
        this.QuestionAnswerRec = new Wtf.data.Record.create([
        {
            name: 'question'
        },{
            name: 'answer'
        },{
            name: 'employeeanswer'
        }
        ]);
        this.QuestionAnswerds = new Wtf.data.Store({
//            url: Wtf.req.base+"hrms.jsp",
            url:"Performance/AnonymousAppraisal/getQuestionAnswerGrid.pf",
            baseParams: {
                flag:408,
                reviewappraisal:this.reviewappraisal
            },
            reader: new Wtf.data.KwlJsonReader1({
                totalProperty: 'totalCount',
                root: "data"
            },this.QuestionAnswerRec)
        });
        this.QuestionAnswerArr = [{
            header:WtfGlobal.getLocaleText("hrms.performance.question"),//"Question",
            dataIndex:'question',
            sortable:true,
            renderer : function(val) {
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:14px arial, tahoma, helvetica, sans-serif;padding-left:15px'>"+unescape(val)+"</pre>";
                    return "<div style='white-space:pre-wrap;font-size:1.3em;margin-left:15px;'>"+unescape(val)+"</div>";
                }
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.appraiser.response"),//"Appraiser Response",
            dataIndex:'answer',
            sortable:true,
            renderer : function(val) {
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:14px arial, tahoma, helvetica, sans-serif;padding-left:15px'>"+unescape(val)+"</pre>";
                    return "<div style='white-space:pre-wrap;font-size:1.3em;margin-left:15px;'>"+unescape(val)+"</div>";
                }
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.self.response"),//"Self Response",
            dataIndex:'employeeanswer',
            sortable:true,
            hidden:!Wtf.cmpPref.selfapp,
            renderer : function(val) {
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:14px arial, tahoma, helvetica, sans-serif;padding-left:15px'>"+unescape(val)+"</pre>";
                    return "<div style='white-space:pre-wrap;font-size:1.3em;margin-left:15px;'>"+unescape(val)+"</div>";
                }
        }];
        this.QuestionAnswercolCM=new Wtf.grid.ColumnModel(this.QuestionAnswerArr);

        this.QuestionAnswerGrid = new Wtf.grid.GridPanel({
            region:'center',
            store: this.QuestionAnswerds,
            title:WtfGlobal.getLocaleText("hrms.performance.qualitative.appraisal"),//'Qualitative Appraisal',
            cls:"gridWithUl",
            cm: this.QuestionAnswercolCM,
            border:true,
            style: 'padding:20px;background:white;',
            frame:false,
            layout:'fit',
            autoHeight:true,
            autoScroll:true,
            split: true,
            viewConfig:{
                forceFit:true
            }
        });
      
    },
    Export2Pdf:function(){
        var url ="Performance/exportAppraisalReportPDF/appraisalReportExport.pf?filename=AppraisalReport&filetype=pdf&reviewappraisal="+this.reviewappraisal+"&appraisalcycid="+this.appTypeCombo.getValue()+"&userid="+this.empCmb.getValue()+"&employee=false&self="+Wtf.cmpPref.selfapp+"&isPrint="+false;
//        var url ="../../appraise.jsp?filename=AppraisalReport&filetype=pdf&reviewappraisal="+this.reviewappraisal+"&appraisalcycid="+this.appTypeCombo.getValue()+"&userid="+this.empCmb.getValue()+"";
        Wtf.get('downloadframe').dom.src = url;
    },
    printRep:function(){
        var url ="Performance/exportAppraisalReportPDF/appraisalReportExport.pf?filename=AppraisalReport&filetype=pdf&reviewappraisal="+this.reviewappraisal+"&appraisalcycid="+this.appTypeCombo.getValue()+"&userid="+this.empCmb.getValue()+"&employee=false&self="+Wtf.cmpPref.selfapp+"&isPrint="+true;
        window.open(url,"mywindow","menubar=1,resizable=1,scrollbars=1");
    },
    findrecord:function(rec){
        if(rec.get('currentFlag')=="1"){
            return true;
        }else{
            return false;
        }
    },
    reviewAppraisalFun:function(reviewstatus,winheight,winwidth){
        if(Wtf.cmpPref.approveappraisal){
            this.salaryWin= new Wtf.appraisalAppWindow({
                modal:true,
                title:WtfGlobal.getLocaleText("hrms.performance.overall.approve.appraisal"),//"Approve Appraisal",
                closable:true,
                resizable:false,
                layout:'fit',
                flag:171,
                width:winwidth,
                height:winheight,
                reviewstatus:reviewstatus,
                employeeid:this.empCmb.getValue(),
                appcycleid:this.appTypeCombo.getValue(),
                ds:this.ds,
                desig:this.ds.getAt(0).get("data")[0].designation,
                dept:this.ds.getAt(0).get("data")[0].dept
            });
            this.salaryWin.show();
        }else{
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp?flag=171",
                url:"Performance/AnonymousAppraisal/reviewanonymousAppraisalReport.pf",
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
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),""+resp.msg+""],0);//calMsgBoxShow(["Success",""+resp.msg+""],0);
                this.ds.load();
            },
            function(){
                calMsgBoxShow(27,2);
            });
        }
    }
});

Wtf.appreportTemplate = function(config){
    Wtf.appreportTemplate.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.appreportTemplate,Wtf.Panel,{
     progTpl : new Wtf.XTemplate(
    		'<tpl for=".">' +
    	    '<div  style ="padding:0px 8px;font-size:14px;padding-left:3%;" class="header" >'+WtfGlobal.getLocaleText("hrms.performance.appraisal.details")+'</div>'+
    	    '<div style = "font-size:12px;color:black;height:100%;overflow:auto;float:left;padding-right:70px;margin-top:1%;padding-left:3%;">'
    	       +'<table  class="templateTable">'
    	            +'<tr>'
    	                +'<td align = "left" class="appraisalTemplate" style="padding-top:1px;"><b>'+WtfGlobal.getLocaleText("hrms.common.employee.name")+'  </b></td><td>: </td><td class="appraisalTemplateData">{empname} </td>'
    	            +'</tr>'
    	            +'<tr>'
    	                +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle.name")+'  </b></td><td>: </td><td class="appraisalTemplateData">{appcylename} </td>'
    	            +'</tr>'
    	            +'<tr style ="border-bottom:1px solid white;">'
    	            +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.appraisal.start.date")+'  </b></td><td>: </td><td class="appraisalTemplateData">{appcylestdate} </td>'
    	            +'</tr>'
    	            +'<tr style ="border-bottom:1px solid white;">'
    	            +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.appraisal.end.date")+'  </b></td><td>: </td><td class="appraisalTemplateData">{appcylendate} </td>'
    	            +'</tr>'
    	            +'<tr>'
    	                +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.total.no.of.appraisers")+'  </b></td><td>: </td><td class="appraisalTemplateData">{totalappraisal} </td>'
    	            +'</tr>'
    	            +'<tr>'
    	                +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.no.of.appraisals.submitted")+'  </b></td><td>: </td><td class="appraisalTemplateData">{appraisalsubmitted} </td>'
    	            +'</tr>'
    	            +'<tpl if="isoverallcomment" >'
    	            +'<tpl if="isselfappraisal" ><tr>'
    	                +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.overall.self.comments")+' </b></td><td>:&nbsp;</td><td class="appraisalTemplateData">{empcomment}</td>'
    	            +'</tr></tpl>'
    	            +'<tr>'
    	                +'<td align = "left" class="appraisalTemplate" style ="width:170px"><b>'+WtfGlobal.getLocaleText("hrms.performance.overall.appraiser.comments")+' </b></td><td>:&nbsp;</td><td class="appraisalTemplateData"><div style="white-space-pre-wrap;">{mancom}</div></td>'
    	            +'</tr></tpl>'
    	            +'<tpl if="this.isQues(isquestionemp)"><tr>'
    	                +'<td align = "left" class="appraisalTemplate"><b>'+WtfGlobal.getLocaleText("hrms.performance.overall.competency.score")+'  </b></td><td>:&nbsp;</td><td class="appraisalTemplateData">{manavgwght} </td>'
    	            +'</tr></tpl>'
    	       +'</table>'
    	    +'</div>'+
    	    '</tpl>',
    /*'<tpl for=".">' +
     '<div  style ="padding:0px 8px;font-size:14px;padding-left:3%;" class="header" >Appraisal Details</div>'+
    '<div style = "font-size:12px;color:black;height:100%;overflow:auto;float:left;padding-right:70px;margin-top:1%;padding-left:3%;">'
       +'<table  class="templateTable">'
            +'<tr>'
                +'<td align = "left" class="appraisalTemplate" style="padding-top:1px;"><b>Employee Name  </b></td><td>: </td><td class="appraisalTemplateData">{empname} </td>'
            +'</tr>'
            +'<tr>'
                +'<td align = "left" class="appraisalTemplate"><b>Appraisal Cycle Name  </b></td><td>: </td><td class="appraisalTemplateData">{appcylename} </td>'
            +'</tr>'
            +'<tr style ="border-bottom:1px solid white;">'
            +'<td align = "left" class="appraisalTemplate"><b>Appraisal Cycle Start Date  </b></td><td>: </td><td class="appraisalTemplateData">{appcylestdate} </td>'
            +'</tr>'
            +'<tr style ="border-bottom:1px solid white;">'
            +'<td align = "left" class="appraisalTemplate"><b>Appraisal Cycle End Date  </b></td><td>: </td><td class="appraisalTemplateData">{appcylendate} </td>'
            +'</tr>'
            +'<tr>'
                +'<td align = "left" class="appraisalTemplate"><b>Total No. of Appraisers  </b></td><td>: </td><td class="appraisalTemplateData">{totalappraisal} </td>'
            +'</tr>'
            +'<tr>'
                +'<td align = "left" class="appraisalTemplate"><b>No. of Appraisals Submitted  </b></td><td>: </td><td class="appraisalTemplateData">{appraisalsubmitted} </td>'
            +'</tr>'
            +'<tpl if="isoverallcomment" >'
            +'<tpl if="isselfappraisal" ><tr>'
                +'<td align = "left" class="appraisalTemplate"><b>Overall Self Comment </b></td><td>:&nbsp;</td><td class="appraisalTemplateData">{empcomment}</td>'
            +'</tr></tpl>'
            +'<tr>'
                +'<td align = "left" class="appraisalTemplate" style ="width:170px"><b>Overall Appraiser Comments </b></td><td>:&nbsp;</td><td class="appraisalTemplateData"><div style="white-space-pre-wrap;">{mancom}</div></td>'
            +'</tr></tpl>'
            +'<tpl if="this.isQues(isquestionemp)"><tr>'
                +'<td align = "left" class="appraisalTemplate"><b>Overall Competency Score  </b></td><td>:&nbsp;</td><td class="appraisalTemplateData">{manavgwght} </td>'
            +'</tr></tpl>'
       +'</table>'
    +'</div>'+
    '</tpl>',*/
    {
         isQues: function(isquestionemp){
             return isquestionemp == "false";
         }
    }
    ),
    emptyTpl : new Wtf.Template(
    		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.none.appraisers.submitted.appraisal")+'</div>'//'<div class="grid-link-text">None of the appraisers have submitted the appraisal yet.</div>'
),
   emptyreviewTpl : new Wtf.Template(
		   '<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.selected.appraisal.approved")+'</div>'//'<div class="grid-link-text">Selected appraisal has been approved.</div>'
),emptyreportTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.appraisal.not.approved")+'</div>'//'<div class="grid-link-text">Selected appraisal has not been approved.</div>'
),emptynonapprreportTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.appraisal.process.ongoing")+'</div>'//'<div class="grid-link-text">Appraisal process is ongoing.</div>'
),emptynonapprTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.none.appraisers.submitted.appraisal")+'</div>'//'<div class="grid-link-text">None of the appraisers have submitted the appraisal.</div>'
),emptynonempTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.no.employee.appraisal.cycle")+'</div>'//'<div class="grid-link-text">No employee for this appraisal cycle.</div>'
),emptynonappTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.no.appraisal.initiated")+'</div>'//'<div class="grid-link-text">No appraisal initiated.</div>'
),emptynotapprvTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle.not.approved")+'</div>'//'<div class="grid-link-text">Appraisal cycle has not been approved.</div>'
),emptyselctapprvTpl : new Wtf.Template(
		'<div class="grid-link-text">'+WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle.from.list")+'</div>'//'<div class="grid-link-text">Select appraisal cycle from list.</div>'
),
    onRender : function(config){
        Wtf.appreportTemplate.superclass.onRender.call(this,config);
          var innerPanel = new Wtf.Panel({
            border : false,
            layout : "fit",
            id : this.id + "_innerPanel",
            bodyStyle : "background:transparent;",
            items:[{
                border:false,
                layout:'border',
                bodyStyle : "background:transparent;",
                items : [{
                        autoScroll:true,
                        region:'center',
                        layout:"column",
                        bodyStyle:'padding:8px 8px 8px 8px',
                        border:false,
                        items:[{
                                border : false,
                                layout : "column",
                                columnWidth : 1,
                                id : "mainMod2_"+this.id,
                                bodyStyle : "background:transparent;padding:8px;",
                                items : [this.myModule1 = new Wtf.Panel({
                                        border : false,
                                        width: '100%',
                                        id:this.id+'appraisaldetails',
                                        autoLoad : false,
                                        bodyStyle : "background:transparent;"
                                })]
                            }]
                    }]
            }]

        });

        this.add( innerPanel );
     }
});
