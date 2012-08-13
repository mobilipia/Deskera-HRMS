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
Wtf.competencyEval=function(config){
    Wtf.competencyEval.superclass.constructor.call(this,config);
    Wtf.form.Field.prototype.msgTarget='side';
};
Wtf.extend(Wtf.competencyEval,Wtf.Panel,{
    layout:'fit',
    initComponent:function(config){
        var cmpevalbtns=this.getSaveButtons();
        this.tbar=cmpevalbtns;
        cmpevalbtns= this.getSaveButtonsBottom();
        this.bbar = cmpevalbtns;
        Wtf.competencyEval.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.competencyEval.superclass.onRender.call(this,config);
        this.totalCompGap=0;
        this.ctotalComp=0;
        this.gtotalComp=0;
        this.manratempty=true;
        this.userrec=new Wtf.data.Record.create([{
            name:'appraisalid'
        },{
            name:'username'
        },{
            name:'managername'
        },{
            name:'employeecomment'
        },{
            name:'managercomment'
        },{
            name:'userid'
        },{
            name:'designation'
        },{
            name:'designationid'
        },{
            name:'salaryrec'
        },{
            name:'newdesig'
        },{
            name:'newdept'
        },{
            name:'salaryinc'
        },{
            name:'performance'
        },{
            name:'managercompscore'
        },{
            name:'employeecompscore'
        },{
            name:'managergoalscore'
        },{
            name:'employeegoalscore'
        },{
            name:'managerid'
        },{
            name:'startdate'
        },{
            name:'enddate'
        },{
            name:'appcycleid'
        },{
            name:'managerstatus'
        },{
            name:'isquestionemp'
        },{
            name:'employeestatus'
        }]);
        this.empnameStore =  new Wtf.data.Store({
            url:"Performance/Appraisalcycle/getAppraisallist.pf",
            baseParams: {
                flag: 158,
                employee:this.employee
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.userrec),
            autoLoad : false
        });
        calMsgBoxShow(202,4,true);

        this.ratingData=[['1','1 - '+WtfGlobal.getLocaleText("hrms.performance.lowest")],['2',2],['3',3],['4',4],['5','5 - '+WtfGlobal.getLocaleText("hrms.performance.highest")]];

        this.ratingStore=new Wtf.data.SimpleStore({
            fields: ['id','rating'],
            data :this.ratingData
        });

        this.escore=new Wtf.form.NumberField
        ({
            labelWidth:110,
            readOnly:true,
            allowNegative: false,
            width:200
        });
        this.mscore=new Wtf.form.NumberField
        ({
            labelWidth:110,
            readOnly:true,
            allowNegative: false,
            width:200
        });
        this.gapscore=new Wtf.form.NumberField
        ({
            labelWidth:110,
            readOnly:true,
            allowNegative: false,
            width:200
        });
        this.PersonalattrPanel = new Wtf.Panel({
            title:WtfGlobal.getLocaleText("hrms.performance.qualitative.appraisal"),//"Qualitative Appraisal",
            frame:true,
            hidden:true,
            border :false,
            autoHeight:true,
            refid:this.profId,
            formtype : "Personal",
            id:this.id+"Personal",
            fetchmaster:true,
            chk:1
        });
        this.empcompRate=new Wtf.form.ComboBox({
            store:this.ratingStore,
            displayField:'rating',
            valueField:'id',
            scope:this,
            mode:'local',
            selectOnFocus:true,
            typeAhead:true,
            allowBlank:false,
            triggerAction :'all'
        });
        this.empcompComment=new Wtf.form.TextField();
        if(this.read){
            this.empcompRate="";
            this.empcompComment="";
        }
        this.mancompRate=new Wtf.form.ComboBox({
            store:this.ratingStore,
            displayField:'rating',
            valueField:'id',
            scope:this,
            mode:'local',
            selectOnFocus:true,
            typeAhead:true,
            allowBlank:false,
            triggerAction :'all'
        });
        this.mancompComment=new Wtf.form.TextField();
        if(this.modify){
            this.mancompComment="";
            this.mancompRate="";
        }

        this.competencyArr = [{
            header:WtfGlobal.getLocaleText("hrms.performance.competency"),//"Competency",
            dataIndex:'cmptname',
            width:150,
            sortable:true,
            renderer : function(val) {
                return WtfGlobal.commentRenderer(val);
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.competency.description"),//"Competency Description",
            dataIndex:'cmptdesc',
            width:250,
            sortable:true,
            renderer : function(val) {
                var valTip = WtfGlobal.replaceAll(val, '"', "&#34;");
                if(Wtf.isIE6 || Wtf.isIE7)
                    return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+valTip+"\">"+val+"</pre>";
                return '<div style="white-space:pre-wrap;" wtf:qtip=\"'+valTip+'\">'+val+'</div>';
            }
        }];   
    if(Wtf.cmpPref.weightage){
        this.competencyArr.push({
            header:WtfGlobal.getLocaleText("hrms.performance.weightage"),//"Weightage",
            dataIndex:'cmptwt',
            align:'right',
            sortable:true,
            renderer:WtfGlobal.numericPrecisionRenderer
        });
    }
     if(!this.employee){
       this.competencyArr.push({
            header:WtfGlobal.getLocaleText("hrms.performance.appraiser.rating"),//"Appraiser Rating",
            dataIndex:'manrat',
            width:60,
            sortable:true,
            editor: this.mancompRate,
            renderer:Wtf.comboBoxRenderer(this.mancompRate)
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.appraiser.comment"),//"Appraiser Comment",
            dataIndex:'mancompcomment',
            width:250,
            sortable:true,
            editor: this.mancompComment,
            renderer : function(val) {
                var valTip = WtfGlobal.replaceAll(val, '"', "&#34;");
                if(Wtf.isIE6 || Wtf.isIE7)
                    return '<pre style="word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;" wtf:qtip=\"'+valTip+'\">'+val+'</pre>';
                return '<div style="white-space:pre-wrap;" wtf:qtip=\"'+valTip+'\">'+val+'</div>';
            }
        });
     }
        if(this.employee){
            this.competencyArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.self.rating"),//"Self Rating",
                dataIndex:'emprat',
                width:60,
                sortable:true,
                editor: this.empcompRate,
                renderer:Wtf.comboBoxRenderer(this.empcompRate)
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.self.comments"),//"Self Comments",
                dataIndex:'empcompcomment',
                width:250,
                sortable:true,
                editor: this.empcompComment,
                renderer : function(val) {
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                    return "<div style='white-space:pre-wrap;' wtf:qtip=\""+val+"\">"+val+"</div>";
                }
            });
        }

        this.competencycolCM=new Wtf.grid.ColumnModel(this.competencyArr);
        this.compRecord = new Wtf.data.Record.create([{
            name: 'cmptid'
        },{
            name: 'compid'
        },{
            name: 'cmptname'
        },{
            name: 'cmptwt'
        },{
            name: 'cmptdesc'
        },{
            name:'emprat'
        },{
            name:'empgap'
        },{
            name:'manrat'
        },{
            name:'mangap'
        },{
            name:'mid'
        },{
            name:'empcompcomment'
        },{
            name:'mancompcomment'
        },{
            name:'cmptnametemp'
        }]);
        this.comptdataReader = new Wtf.data.KwlJsonReader1({
            root: "data"
        },this.compRecord);
        this.comptstore= new Wtf.data.Store({
            sortInfo: {
                field: 'cmptname',
                direction: "ASC"
            },
            url:"Performance/Appraisal/getappraisalCompetencyFunction.pf",
            reader:this.comptdataReader,
            method: 'POST'
        });
        this.comptstore.on('load',function(rec){
            if(msgFlag==1){
                WtfGlobal.closeProgressbar();
            }
            if(this.question && this.isSubmitted && Wtf.cmpPref.competency) {
                this.helpTemplate9.overwrite(this.helpTextPanel.body,{});
                for(var i=0; i<this.comptstore.data.length; i++){
                    this.helpTemplate8.append(this.helpTextPanel.body, {cmptnametemp:this.comptstore.getAt(i).data.cmptname,index:i+1});
                }
                this.helpTemplate10.append(this.helpTextPanel.body,{});
            }
        },this);
        this.comptstore.on('loadexception',function(){
            if(msgFlag==1){
            WtfGlobal.closeProgressbar();
            }
        },this);
        this.competencyGrid = new Wtf.grid.EditorGridPanel(
        {
            store: this.comptstore,
            title:WtfGlobal.getLocaleText("hrms.performance.competency.evaluation"),//'Competency Evaluation',
            cm: this.competencycolCM,
            cls:"gridWithUl",
            border:true,
            frame:false,
            hidden:true,
            height:320,
            layout:'fit',
            autoScroll:true,
            split: true,
            viewConfig:{
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.no.competency.assigned"))//WtfGlobal.emptyGridRenderer("No competency assigned till now")
            },
            clicksToEdit:1,
            bbar:[
            '->' ,WtfGlobal.getLocaleText("hrms.performance.average.rating"), this.escore//'->' ,'Average Rating',  this.escore
            ]
        });


        this.sm3= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true
        });
        this.empgoalRate=new Wtf.form.ComboBox({
            store:this.ratingStore,
            displayField:'rating',
            valueField:'id',
            scope:this,
            mode:'local',
            selectOnFocus:true,
            typeAhead:true,
            allowBlank:false,
            triggerAction :'all'
        });
        this.empgoalComment=new Wtf.form.TextField();
        if(this.read){
            this.empgoalRate="";
            this.empgoalComment="";
        }
        this.mangoalRate=new Wtf.form.ComboBox({
            store:this.ratingStore,
            displayField:'rating',
            valueField:'id',
            scope:this,
            mode:'local',
            selectOnFocus:true,
            typeAhead:true,
            allowBlank:false,
            triggerAction :'all'
        });
        this.mangoalComment=new Wtf.form.TextField();
        if(this.modify){
            this.mangoalRate="";
            this.mangoalComment="";
        }
        this.goalArr = [{
            header:WtfGlobal.getLocaleText("hrms.performance.goals"),//"Goals",
            dataIndex:'gname',
            sortable:true,
            align: 'left',
            renderer : function(val) {
                return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
            }
        },{
            header:WtfGlobal.getLocaleText("hrms.performance.percent.completed"),//"Percent Complete",
            dataIndex:'gcomplete',
            align: 'right'
        }];

        if(Wtf.cmpPref.weightage){
            this.goalArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.goal.weightage"),//"Goal Weightage",
                dataIndex:'gwth',
                align:'right',
                sortable:true,
                hidden:(Wtf.cmpPref.weightage)?false:true,
                renderer:WtfGlobal.numericPrecisionRenderer
            });
        }
        if(!this.employee){
            this.goalArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.appraiser.rating"),//"Appraiser Rating",
                dataIndex:'gmanrat',
                sortable:true,
                editor: this.mangoalRate,
                renderer:Wtf.comboBoxRenderer(this.mangoalRate)
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
        }
        if(this.employee){
            this.goalArr.push({
                header:WtfGlobal.getLocaleText("hrms.performance.self.rating"),//"Self Rating",
                dataIndex:'gemprat',
                sortable:true,
                editor: this.empgoalRate,
                renderer:Wtf.comboBoxRenderer(this.empgoalRate)
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.self.comments"),//"Self Comments",
                dataIndex:'empgoalcomment',
                sortable:true,
                editor: this.empgoalComment,
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
                }
            });
        this.goalArr.push({
             header:WtfGlobal.getLocaleText("hrms.common.assigned.by"),//"Assigned By",
            dataIndex:'assignedby',
            sortable:true
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
        },{
            name:'gcomplete'
        }]);
        this.dataReader = new Wtf.data.KwlJsonReader1({
            root: "data"
        },this.GoalRecord);

        this.goalstore= new Wtf.data.Store({
            url:Wtf.req.base + 'hrms.jsp',
            reader:this.dataReader,
            method: 'POST'
        });
        this.goalGrid = new Wtf.grid.EditorGridPanel(
        {
            width:'100%',
            store:  this.goalstore,
            title:WtfGlobal.getLocaleText("hrms.performance.goal.evaluation"),//'Goal Evaluation',
            cm:  this.goalcolCM,
            sm:this.sm3,
            border:true,
            frame:false,
            layout:'fit',
            hidden:true,
            height:320,
            autoScroll:true,
            split: true,
            viewConfig:{
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.performance.my.goal.grid.msg"))//WtfGlobal.emptyGridRenderer("No goal assigned till now")
            },
            clicksToEdit:1,
            bbar:[
                  '->' ,WtfGlobal.getLocaleText("hrms.performance.average.rating"),  this.mscore//'->' ,'Average Rating',  this.mscore
            ]
        });

        this.apprec=new Wtf.data.Record.create([{
                name:'appcycleid'
            },{
                name:'appcycle'
            },{
                name:'startdate'
            },{
                name:'enddate'
            },{
                name:'substart'
            },{
                name:'currentFlag'
        }]);

        this.appTypeStore =  new Wtf.data.Store({
            url:"Performance/Appraisalcycle/getAppraisalcycleform.pf",
            baseParams: {
                flag: 168,
                employee:this.employee
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.apprec),
            autoLoad : false
        });
        this.appTypeStore.load({params:{grouper:'appraiseothers'}});
        this.appTypeStore.on('load',function(){
            var row;
            this.competencyGrid.hide();
            if(this.apptype!==undefined && this.apptype!=""){
                    this.appTypeCombo.setValue(this.apptype);
                    row = this.appTypeStore.find('appcycleid',this.apptype);
            } else {
                row=this.appTypeStore.findBy(this.findrecord,this);
            }
            if(row!=-1) {
                this.appTypeCombo.setValue(this.appTypeStore.getAt(row).get('appcycleid'));
                this.startdate.setValue(this.appTypeStore.getAt(row).get('startdate'));
                this.enddate.setValue(this.appTypeStore.getAt(row).get('enddate'));
                this.nochange=row;
            }
            
            if(this.appTypeStore.getCount()==0){
                 this.appTypeCombo.emptyText=WtfGlobal.getLocaleText("hrms.performance.no.appraisal.initiated"),//"No appraisal initiated  ";
                 this.appTypeCombo.reset();
            }
           if(this.onsubmitflag==1||this.onsubmitflag==2){
               this.appTypeCombo.setValue(this.appTypeStore.getAt(this.appstoreind).get('appcycleid'));
               this.nochange=this.appstoreind;
            }
           this.empnameStore.load({
               params:{
                   appcylid:this.appTypeCombo.getValue()
               }
           });
           if(this.employee){
               this.loademployeestores();
           } 
           this.printbtn.setDisabled(true);
       	   this.pdfbtn.setDisabled(true);
        },this);
        this.appTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            mode:"local",
            valueField:'appcycleid',
            displayField:'appcycle',
            store:this.appTypeStore,
            emptyText:WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle"),//'Select appraisal cycle',
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle")+" *"),//"Appraisal Cycle *",
            width:200,
            typeAhead:true
        });
        this.appTypeCombo.on('select',function(a,b,c){
           if(this.nochange==c){
               return;
           }
           this.nochange=c;
           this.nochange2=null;
           this.empId.setValue('');
           this.startdate.setValue(b.data.startdate);
            this.enddate.setValue(b.data.enddate);
            this.managerName.setValue('');
            this.desig.setValue('');
            this.ecomments.setValue('');
            this.mcomments.setValue('');
            this.gapscore.setValue('');
            this.mscore.setValue(0);
            this.escore.setValue(0);
            this.newDesignation.setValue('');
            this.newDepartment.setValue('');
            this.salaryIncrement.setValue('');
            this.perrat.setValue();
            this.goalstore.removeAll();
            this.comptstore.removeAll();
            this.empnameStore.load({
               params:{
                   appcylid:this.appTypeCombo.getValue()
               }
           });
            this.enableDisableSaveButtons(false);
            this.competencyGrid.hide();
            this.PersonalattrPanel.hide();
            this.goalGrid.hide();
            if(this.employee){
                if(Wtf.cmpPref.goal){
                    this.goalGrid.show();
                }
            }
            if(b.json.substart == 2){
            	this.helpTemplate5.overwrite(this.helpTextPanel.body);
            	this.goalGrid.hide();
            }
            this.doLayout();
        },this);

        this.empId=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.employee.name"),//'Employee Name',
            labelWidth:110,
            mode:"local",
            store:  this.empnameStore,
            displayField: 'username',
            valueField:'appraisalid',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead:true,
            width:200,
            listeners:{
                scope:this,
                select:function(c,r,i)
                {
                    if(this.nochange2==i){
                        return;
                    }
                    if(r.data.isquestionemp == "true"){
                        this.question=true;
                    } else {
                        this.question=false;
                    }
                    this.nochange2=i;
                    this.employeeSelect(r);
                }
            }
        });

        this.managerName=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.performance.appraiser.name"),//'Appraiser Name',
            labelWidth:110,
            mode:"local",
            store:  this.empnameStore,
            displayField: 'managername',
            valueField:'appraisalid',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead:true,
            width:200,
            listeners:{
                scope:this,
                select:function(c,r)
                {
                    this.employeeSelect(r);
                }
            }
        });

        this.desig=new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.designation"),//'Designation',
            labelWidth:110,
            disabled:true,
            width:200
        });
        this.apprMan=new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.performance.appraiser.name"),//'Appraiser Name',
            labelWidth:110,
            disabled:true,
            width:200
        });

        this.startdate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.form"),//'From',
            labelSeparator:'',
            format:'m/d/Y',
            width:100,
           disabled:true
        });

        this.enddate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.to"),//'To',
            format:'m/d/Y',
            width:100,
            labelSeparator:'',
            disabled:true
        });

        this.datePanel=new Wtf.Panel({
            width:800,
            frame:false,
            border:false,
            layout:'column',
            items:[
            {
                width:350,
                frame:false,
                border:false,
                labelWidth:30,
                layout:'column',
                items:[{
                    frame:false,
                    border:false,
                    layout:'form',
                    items:[new Wtf.form.LabelField({
                        value:(WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle.period")+":"),//"Appraisal Cycle Period:",
                        width:206,
                        style:"font-size:14px"
                })]
            },{
                frame:false,
                border:false,
                layout:'form',
                items:[this.startdate]
            }]
            },
            {
                width:350,
                frame:false,
                border:false,
                layout:'form',
                labelWidth:15,
                labelSeparator:"",
                items:[this.enddate]
            }]
        });

        this.salaryY=new Wtf.form.Radio({
            inputValue:'yes',
            name:'salary',
            boxLabel:WtfGlobal.getLocaleText("hrms.common.yes"),//'Yes',
            checked:false,
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.promotion.recommendation"),//'Promotion Recommendation',
            labelSeparator:''
        });

        this.salaryN=new Wtf.form.Radio({
            inputValue:'no',
            name:'salary',
            checked:true,
            boxLabel:WtfGlobal.getLocaleText("hrms.common.no"),//'No',
            labelSeparator:''
        });
        this.salaryPanel=new Wtf.Panel({
            width:800,
            frame:false,
            border:false,
            layout:'column',
            items:[
            {
                width:300,
                frame:false,
                border:false,
                layout:'form',
                items:[this.salaryY]
            },
            {
                width:300,
                frame:false,
                border:false,
                layout:'form',
                labelWidth:10,
                labelSeparator:" ",
                items:[this.salaryN]
            }]
        });

        this.perrat=new Wtf.form.FnComboBox({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.performance.rating")+"*"),//"Performance Rating*",
            name:'prate',
            allowBlank:false,
            store: Wtf.prat,
            displayField: 'name',
            valueField:'id',
            selectOnFocus:true,
            width:200,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            addNewFn:this.addPerformanceRating.createDelegate(this),
            plugins: [new Wtf.common.comboAddNew({
                handler: function(){
            		WtfGlobal.showmasterWindow(13,Wtf.prat,"Add");//WtfGlobal.showmasterWindow(13,Wtf.prat,"Add");
                },
                scope: this
            })]
        });

        if(!Wtf.StoreMgr.containsKey("prat")){
            Wtf.prat.on('load',function(){
                if(this.prating!=null)
                    this.perrat.setValue(this.prating);
            },this);
            Wtf.prat.load();
            Wtf.StoreMgr.add("prat",Wtf.prat)
        }
        else{
            if(this.prating!=null)
                this.perrat.setValue(this.prating);
        }

        this.tempDesigStore =  new Wtf.data.Store({              // As the filter does not apply on typeahead we need 2 stores
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },new Wtf.data.Record.create([{
                name:'id'
            },{
                name:'name'
            }])
            ),
            autoLoad : false
        });
        this.newDesigStore =  new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },new Wtf.data.Record.create([{
                name:'id'
            },{
                name:'name'
            }])
            ),
            autoLoad : false
        });

        this.newDesignation = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.new.designation"),//"New Designation",
            store:this.newDesigStore,
            anchor:'50.5%',
            mode:'local',
            hiddenName :'name',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true
        });

        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.on('load',function(){
                this.tempDesigStore.loadData(Wtf.desigStore.reader.jsonData);
                WtfGlobal.closeProgressbar();
            },this);
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }else{
            this.tempDesigStore.loadData(Wtf.desigStore.reader.jsonData);
            WtfGlobal.closeProgressbar();
        }

        
        this.newDepartment = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.new.department"),//'New Department',
            store:Wtf.depStore,
            mode:'local',
            anchor:'50.5%',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true
        });

        if(!Wtf.StoreMgr.containsKey("dep")){
            Wtf.depStore.on('load',function(){
                this.newDepartment.setValue(this.newdepartment);
            },this);
            Wtf.depStore.load();
            Wtf.StoreMgr.add("dep",Wtf.depStore)
        }else{
            this.newDepartment.setValue(this.newdepartment);
        }

        this.salaryIncrement=new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.performance.salary.increment")+"(%)",//'Salary Increment(%)',
            labelWidth:110,
            width:200
        });

        this.salaryDetails=new Wtf.Panel({
            width:800,
            frame:false,
            border:false,
            layout:'form',
            hidden:true,
            items:[this.newDepartment,this.newDesignation]
        });
        if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll)){
        this.salaryDetails.add(this.salaryIncrement);
        }
        this.itemarray=[];
        if(this.employee){
            this.itemarray.push(this.appTypeCombo,this.datePanel);
        }else{
            this.itemarray.push(this.appTypeCombo,this.empId,this.datePanel,this.desig);
            if(Wtf.cmpPref.promotionrec){
                this.itemarray.push(this.perrat,this.salaryPanel,this.salaryDetails);
            }
        }

        this.compEvalForm1=new Wtf.form.FormPanel({
            frame:false,
            labelWidth:200,
            autoHeight:true,
            id:'competencyval',
            columnWidth:0.5,
            cls:"visibleDisabled",
            border:false,
            style:"padding-top:20px",
            defaultType:'textfield',
            items:this.itemarray
        });

        this.commentArr=[];

        this.ecomments=new Wtf.ux.TextArea
        ({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.overall.self.comments")+" *"),//'Overall Self Comments *',
            labelWidth:110,
            disabled:this.read,
            allowaBlank:false,
            maxLength:1024,
            width:230,
            height:150
        });

        this.mcomments=new Wtf.ux.TextArea
        ({
            fieldLabel:(WtfGlobal.getLocaleText("hrms.performance.overall.appraiser.comments")+" *"),//'Overall Appraiser Comments *',
            labelWidth:110,
            disabled:this.modify,
            maxLength:1024,
            allowaBlank:false,
            width:230,
            height:150
        });

        if(this.employee){
            this.commentArr.push(this.ecomments);
        }else{
            this.commentArr.push(this.mcomments);
        }
        this.compEvalForm2=new Wtf.form.FormPanel({
            frame:false,
            border:false,
            columnWidth:0.5,
            cls:"visibleDisabled",
            hidden:(Wtf.cmpPref.overallcomments)?false:true,
            autoHeight:true,
            labelWidth:200,
            style:"padding-top:20px",
            items:this.commentArr
        });


        this.appraisalArr=[];
        this.southht=true;
        if(Wtf.cmpPref.goal&&Wtf.cmpPref.competency){
            this.appraisalArr.push({
                layout:'fit',
                border:false,
                frame:false,
                style: 'padding:5px;background:rgb(241,241,241);',
                items:this.competencyGrid
            },{
                layout:'fit',
                border:false,
                frame:false,
                style: 'padding:5px;background:rgb(241,241,241);',
                items:this.PersonalattrPanel
            },{
                id:'southgoalpanel'+this.id,
                layout:'fit',
                border:false,
                frame:false,
                style: 'padding:5px;background:rgb(241,241,241);',
                items:this.goalGrid
            });
       }
        else if(Wtf.cmpPref.goal && !Wtf.cmpPref.competency){
            this.appraisalArr.push({
                    layout:'fit',
                    border:false,
                    frame:false,
                    style: 'padding:5px;background:rgb(241,241,241);',
                    items:this.goalGrid
                });
        }
        else if(!Wtf.cmpPref.goal && Wtf.cmpPref.competency){
            this.appraisalArr.push({
                layout:'fit',
                border:false,
                frame:false,
                style: 'padding:5px;background:rgb(241,241,241);',
                items:this.competencyGrid
            },{
                layout:'fit',
                border:false,
                frame:false,
                style: 'padding:5px;background:rgb(241,241,241);',
                items:this.PersonalattrPanel
            });
        } else{
            this.southht=false;
            this.layout='fit';
            this.appraisalArr.push({
                layout:'fit',
                border:false,
                style: 'padding:5px 50px 5px 50px;background:rgb(241,241,241);',
                frame:false
            });
        }

        this.helpTextPanel=new Wtf.Panel({
            columnWidth:0.99,
            border:false,
            frame:false
        });

        this.helpTemplateKrawler=new Wtf.Template(
        		"<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.instructions.to.fill.job.card")+":<br/>"+
        		"<ol style='list-style:decimal inside;padding-left:20px;'><li>"+WtfGlobal.getLocaleText("hrms.performance.fill.self.evaluation.card")+"</li>"+
        		"<li>"+WtfGlobal.getLocaleText("hrms.performance.see.list.attributes.need.keep.in.mind.rating.yourself")+":"+
        		"<ul style='list-style:circle inside;padding-left:20px;'><li>"+WtfGlobal.getLocaleText("hrms.performance.q.quality")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.tu.technical.understanding")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.ti.team.initiatives")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.ac.analytical.capability")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.c.communication")+"</li></ul></li>"+
        		"<li>"+WtfGlobal.getLocaleText("hrms.performance.each.element.rated.to.constructs.scale.one.five")+":"+
        		"<ul style='list-style:circle inside;padding-left:20px;'><li>"+WtfGlobal.getLocaleText("hrms.performance.strongly.disagree.1")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.disagree.2")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.undecided.3")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.agree.4")+"</li><li>"+WtfGlobal.getLocaleText("hrms.performance.strongly.agree.5")+"</li></ul></li>"+
        		"<li>"+WtfGlobal.getLocaleText("hrms.performance.specific.in.your.comments")+"</li>"+
        		"<li>"+WtfGlobal.getLocaleText("hrms.performance.results.of.your.evaluation")+"</li></ol></span></b>"
        		/*"<b><span style='font-size:1.1em;'>Instructions to fill the job card:<br/>"+
        		"<ol style='list-style:decimal inside;padding-left:20px;'><li>As a part of process, please fill the self evaluation card which will facilitate the appraiser to assess your competencies.</li>"+
        		"<li>Please see the following list of attributes you need to keep in mind while rating yourself. The attributes of competencies are classified as under:"+
        		"<ul style='list-style:circle inside;padding-left:20px;'><li>Q-Quality</li><li>TU-Technical Understanding</li><li>TI-Team Initiatives</li><li>AC-Analytical Capability</li><li>C-Communication</li></ul></li>"+
        		"<li>Each element is rated to the constructs on a scale of one to five, with the left constructs as '1' and the right constructs as '5'."+
        		" For example on a scale of 1 to 5, with '1' being strongly disagree and  '5'  being strongly agree. The ratings are specified as under:"+
        		"<ul style='list-style:circle inside;padding-left:20px;'><li>Strongly Disagree (1)</li><li>Disagree (2)</li><li>Undecided (3)</li><li>Agree (4)</li><li>Strongly Agree (5)</li></ul></li>"+
        		"<li>Please be specific in your comments.</li>"+
        		"<li>The results of your evaluation will help us in identifying in what competencies an individual needs additional development or training.</li></ol></span></b>"*/
        );

        this.helpTemplate1=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.instructions")+":<br/>"+WtfGlobal.getLocaleText("hrms.performance.appraisal.instructions.1")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+WtfGlobal.getLocaleText("hrms.performance.appraisal.instructions.2")+"</span></b>"
        );

        this.helpTemplate2=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.appraisal.already.submitted")+"</span></b>"
        );

        this.helpTemplate3=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.please.select.employee")+"</span></b>"
        );

        this.helpTemplate4=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.no.employee.select")+"</span></b>"
        );

        this.helpTemplate5=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.appraisal.submission.already.ended")+"</span></b>"
        );

        this.helpTemplate6=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.appraisal.submission.not.yet.begun")+"</span></b>"
        );
            
        this.helpTemplate7=new Wtf.Template(
            "<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.select.appraisal.cycle")+"</span></b>"
        );
        this.helpTemplate8=new Wtf.XTemplate(
        		"<b><span style='margin-left:30px;font-size:1.1em;'>{index}. {cmptnametemp}</span></b><br/>"
        );
        this.helpTemplate9=new Wtf.XTemplate(
        	"<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.instructions")+":<br/>"+WtfGlobal.getLocaleText("hrms.performance.appraiser.instructions.1")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>"+WtfGlobal.getLocaleText("hrms.performance.appraiser.instructions.2")+"<br/></span></b>"
        );
        this.helpTemplate10=new Wtf.XTemplate(
        	"<b><span style='font-size:1.1em;'>"+WtfGlobal.getLocaleText("hrms.performance.appraiser.instructions.3")+"<br/>"+WtfGlobal.getLocaleText("hrms.performance.appraiser.instructions.4")+"</span></b>"
        );
    this.northPanel=new Wtf.Panel({
            region:'north',
            autoHeight:true,
            bodyStyle:"margin:5px 50px 5px 50px;background:rgb(241,241,241);",
            border:false,
            frame:false,
            layout:'column',
            items:[{
                frame:false,
                columnWidth:0.5,
                autoHeight:true,
                border:false,
                items:[this.compEvalForm1]
            },
            {
                autoHeight:true,
                frame:false,
                columnWidth:0.5,
                border:false,
                style:"padding:0px;background:rgb(241,241,241);",
                items:[this.compEvalForm2]
            }, this.helpTextPanel
            ]
        }
        );
        this.compEvalpanel = new Wtf.Panel({
            autoHeight:true,
            border:false,
            bodyStyle:"background:rgb(241,241,241);",
            items:[this.northPanel,{
                id:'southpanelcont'+this.id,
                border:false,
                frame:false,
                style: 'padding:5px 50px 5px 50px;background:rgb(241,241,241);',
                items:this.appraisalArr
            }]
        });
        this.on("activate",function(){
            if(this.compEvalpanel!=null)
                this.compEvalpanel.doLayout();
        },this)
        

        this.empnameStore.on("load",function(a,b,c){
            this.perrat.disable();
            this.salaryY.disable();
            this.perrat.clearInvalid();
            if(this.employee){
                if(a.reader.jsonData.isquestionemp=="true"){
                    this.competencyGrid.hide();
                    this.question = true;
                    var mainPanel = Wtf.getCmp(this.id+"Personal");
                    this.removeItems(mainPanel);
                    this.PersonalattrPanel.show();
                    this.goalGrid.show();
                    this.getQuestionRequest(a.reader.jsonData.desigid, a.reader.jsonData, false);
                } else {
                	this.question = false;
                    this.competencyGrid.show();
                    this.goalGrid.show();
                }
            }
            if(this.empnameStore.getCount()==0){
                    this.enableDisableSaveButtons(false);
                    this.empId.emptyText=WtfGlobal.getLocaleText("hrms.performance.no.employee.select"),//"No employee to select ";
                    this.empId.reset();
                    if(this.nochange==null||this.nochange==undefined){
                        this.helpTemplate7.overwrite(this.helpTextPanel.body);
                    }
                    else {
                        if(this.appTypeStore.getAt(this.nochange).get('substart')==1){
                            this.helpTemplate6.overwrite(this.helpTextPanel.body);
                        } else if(this.appTypeStore.getAt(this.nochange).get('substart')==2){
                            this.helpTemplate5.overwrite(this.helpTextPanel.body);
                        } else {
                            this.helpTemplate4.overwrite(this.helpTextPanel.body);
                        }
                    }
                    this.doLayout();
             } else{
                 this.empId.emptyText=WtfGlobal.getLocaleText("hrms.performance.select.employee.list"),//"Select employee from list ";
                 this.empId.reset();
                 this.helpTemplate3.overwrite(this.helpTextPanel.body);
                 this.doLayout();
             }
             if((this.onsubmitflag==1||this.onsubmitflag==2)&&this.empnameStore.getAt(this.empstoreind)!=null){
               this.empId.setValue(this.empnameStore.getAt(this.empstoreind).get('appraisalid'));
               if(this.onsubmitflag==1){
                   this.changeEmptyText(0);
               } else{
                   this.changeEmptyText(1);
               }
               this.nochange2=this.empstoreind;
               this.onsubmitflag=0;
            }
        },this);
        if(this.employee){
            this.competencyGrid.on("afteredit",this.afterCompEditFunction,this)
            this.goalGrid.on("afteredit",this.afterGoalGridEditFunction,this)
            this.empnameStore.on("load",function(){
                if(this.employee){
                    this.loademployeestores();
                }
            },this);
        }
        else{
            this.competencyGrid.on("afteredit",this.afterEditFunction,this)
            this.goalGrid.on("afteredit",this.afterGoalGridEdit,this)
            this.salaryY.on("check",function(){
                this.salaryN.checked=false;
                this.salaryDetails.show();
                this.doLayout();
            },this);
            this.salaryN.on("check",function(){
                this.salaryY.checked=false;
                this.salaryDetails.hide();
                this.doLayout();
            },this);
        }
        this.add(this.compEvalpanel);
        this.escore.setValue(0);
        this.mscore.setValue(0);

        if(this.viewappraisal){
            this.appTypeCombo.setValue(this.apptype);
            this.empId.setValue(this.ename);
            this.desig.setValue(this.designation);
            this.apprMan.setValue(this.mname);
            this.ecomments.setValue(this.empcom);
            this.mcomments.setValue(this.mancom);
            this.escore.setValue(parseFloat(this.compscore).toFixed(2));
            this.mscore.setValue(parseFloat(this.goalscore).toFixed(2));
            this.gapscore.setValue(parseFloat(this.compgapscore).toFixed(2));
            this.startdate.setValue(this.stdate);
            this.enddate.setValue(this.eddate);
            this.empId.disable();
            this.appTypeCombo.disable();
            this.mcomments.readOnly=true;
            this.ecomments.readOnly=true;
            this.setBtnStatus = true;
            this.enableDisableSaveButtons(false);
            this.perrat.disable();
            if(this.salaryrecommend==('1'||1)){
                this.salaryY.checked=true;
                this.salaryN.checked=false;
                this.salaryDetails.show();
                this.salaryIncrement.setValue(this.salaryincrement);
                this.doLayout();
            } else{
                this.salaryY.checked=false;
                this.salaryN.checked=true;
            }
            this.salaryIncrement.disable();
            this.salaryY.disable();
            this.salaryN.disable();
            this.newDesignation.disable();
            this.newDepartment.disable();
            this.storeLoad(this.aid,this.empid,this.desid);

        }
    },
    submitFunction:function(savedraft){
        this.perfoFlag=false;
        this.partialFlag=false;
        this.confirmtext="";
        this.confirmtitle="";
        this.jsonCompetency=new Array();
        this.jsonQuestion=new Array();
        this.jsonGoal=new Array();	
        if(savedraft){
            this.confirmtext=WtfGlobal.getLocaleText("hrms.common.want.to.save.draft");//"Do you want to save this draft ?";
            this.confirmtitle=WtfGlobal.getLocaleText("hrms.common.save.as.draft");//"Save As Draft";
        } else{
            this.confirmtext=WtfGlobal.getLocaleText("hrms.common.want.to.submit.draft");//"Do you want to submit the form ?";
            this.confirmtitle=WtfGlobal.getLocaleText("hrms.common.save.data");//"Save Data";
        }
        if(this.employee){
            if(!Wtf.cmpPref.partial){
                if(this.ecomments.getValue()=="" && !savedraft){
                    this.partialFlag=false;
                } else{
                    this.partialFlag=true;
                }
            } else{
                this.partialFlag=true;
            }
            this.perfoFlag=true;
            var appraisalid="";
            for(var i=0;i<this.empnameStore.getCount();i++){
                appraisalid+=this.empnameStore.getAt(i).get('appraisalid')+",";
            }
            this.appraisalid=appraisalid.substr(0,appraisalid.length-1);
        } else{
            if(Wtf.cmpPref.promotionrec) {
                if(this.perrat.getValue()!="" || savedraft){
                    this.perfoFlag=true;
                }
            } else {
                this.perfoFlag=true;
            }
            if(!Wtf.cmpPref.partial){
                if(this.mcomments.getValue()=="" && !savedraft){
                    this.partialFlag=false;
                } else{
                    this.partialFlag=true;
                }
            } else{
                this.partialFlag=true;
            }
        }
        if(!this.partialFlag&&Wtf.cmpPref.overallcomments){
            calMsgBoxShow(179,2);
            return;
        }
        if(this.perfoFlag){
            var rec;
            if(Wtf.cmpPref.competency){
            	if(!this.question){
                	for( i=0;i< this.competencyGrid.store.getCount();i++){
                		var data1 = {};
                		rec=this.competencyGrid.store.getAt(i).data;
                		data1['competencyid'] = rec.cmptid;
                		data1['id'] = rec.compid;
                		data1['mid'] = rec.mid;
                		if(!this.employee){
                			if(rec.manrat=="" && !Wtf.cmpPref.partial && !savedraft){
                				calMsgBoxShow(178,2);
                				return;
                			}
                			data1['compmanrate'] = rec.manrat;
                			data1['compmancomment'] = rec.mancompcomment;
                			data1['compmangap'] = 0;
                		}else{
                			if(rec.emprat=="" && !Wtf.cmpPref.partial && !savedraft){
                				calMsgBoxShow(178,2);
                				return;
                			}
                			data1['compemprate'] = rec.emprat;
                			data1['compempcomment'] = rec.empcompcomment;
                			data1['compempgap'] = 0;
                		}
                		this.jsonCompetency.push(data1);
                	}
                }else{
                	for(var i=0;i<this.finalOutput.count ;i++){
                        var ansStr = new Array();
                        record=this.finalOutput.data[i];
                        for(var j=0;j<record.qans;j++){
                            if((Wtf.getCmp(record.qdescription+"ans"+j).getValue().trim())=="" && !Wtf.cmpPref.partial && !savedraft){
                                   calMsgBoxShow(184,2);
                                   return;
                            }
                            if(!Wtf.getCmp(record.qdescription+"ans"+j).isValid()){
                                   calMsgBoxShow(185,2);
                                   return;
                            }
                            var obj={};
                            obj["ans"+j]=Wtf.getCmp(record.qdescription+"ans"+j).getValue();
                            ansStr.push(obj);
                        }
                        this.jsonQuestion.push({quesid:record.qdescription,
                                               quesans:ansStr});
                    }
                }
            }
            for(i=0;i< this.goalGrid.store.getCount();i++){
            	var data2 = {};
                rec=this.goalGrid.store.getAt(i).data;
                data2['goalid'] = rec.gid;
                data2['id'] = rec.goalid;
                if(!this.employee){
                    if(Wtf.cmpPref.goal) {
                        if(rec.gmanrat=="" && !Wtf.cmpPref.partial && !savedraft){
                            calMsgBoxShow(178,2);
                            return;
                        }
                    }
                    data2['goalmancomment'] = rec.mangoalcomment;
                    data2['goalmanrate'] = rec.gmanrat;
                }
                else{
                    if(Wtf.cmpPref.goal) {
                        if(rec.gemprat=="" && !Wtf.cmpPref.partial && !savedraft){
                            calMsgBoxShow(178,2);
                            return;
                        }
                    }
                    data2['goalempcomment'] = rec.empgoalcomment;
                    data2['goalapprid'] = rec.goalapprid;
                    data2['goalemprate'] = rec.gemprat;
                }
                this.jsonGoal.push(data2);
            }
            if(this.salaryY.getValue()){
                this.salarychange=true;
                this.newdesig=this.newDesignation.getValue();
                this.newdept=this.newDepartment.getValue();
                this.salinc=this.salaryIncrement.getValue();
            } else{
                this.salarychange=false;
                this.newdesig="";
                this.newdept="";
                this.salinc="";
            }
            Wtf.MessageBox.confirm(this.confirmtitle,this.confirmtext,function(btn){
                if(btn!="yes") {
                    return;
                }else{
                    calMsgBoxShow(200,4,true);
                    Wtf.Ajax.requestEx({
                        url:"Performance/Appraisal/appraisalFunction.pf",
                        params: {
                            flag:159,
                            jsoncompetency:Wtf.encode(this.jsonCompetency),
                            jsonqustion:Wtf.encode(this.jsonQuestion),
                            jsongoal:Wtf.encode(this.jsonGoal),
                            isquestion:this.question,
                            appraisalid:this.appraisalid,
                            employee:this.employee,
                            submitdate:new Date().format("Y-m-d"),
                            empcomment:this.ecomments.getValue(),
                            mancomment:this.mcomments.getValue(),
                            competencyscore:this.escore.getValue(),
                            goalscore:this.mscore.getValue(),
                            compgapscore:0,
                            salarychange:this.salarychange,
                            newdesignation:this.newdesig,
                            newdepartment:this.newdept,
                            salaryincrement:this.salinc,
                            saveasDraft:savedraft,
                            performance:this.perrat.getValue(),
                            appcycle:this.appTypeCombo.getValue()
                        }
                    }, this,
                    function(response){
                        var res=eval('('+response+')');
                        if(res.message){
                            (savedraft)?calMsgBoxShow(176,0):calMsgBoxShow(175,0);
                        } else{
                            calMsgBoxShow(177,2);
                        }
                        this.appstoreind=this.nochange;
                        this.empstoreind=this.nochange2;
                        if(savedraft){
                            this.onsubmitflag=1;
                        } else {
                            this.onsubmitflag=2;
                        }
                        this.empnameStore.load({
                                params:{
                                    appcylid:this.appTypeCombo.getValue()
                                }
                            });
                        
                        var a=Wtf.getCmp("DSBMyWorkspaces");
                        if(a){
                            a.doSearch(a.url,'');
                        }
                    }, 
                    function()
                    {
                        })
                }
                this.goalstore.reload();
            }, this)
        }else{
            this.perrat.markInvalid();
            calMsgBoxShow(28,0);
        }
    },

    getQuestionRequest:function(designationid, rec, isMan){
        Wtf.Ajax.requestEx({
                        url:"Performance/Competency/getCompetencyQuestion.pf",
                        scope:this,
                        params: {
                            desig:designationid,
                            employee:this.employee,
                            submitstatus:(!isMan)?rec.empsubmitstatus:rec.data.managerstatus,
                            uid:(!isMan)?"":rec.data.userid,
                            manid:(!isMan)?"":rec.data.managerid,
                            appcycle:(!isMan)?this.appTypeCombo.getValue():rec.data.appcycleid
                        }
                    }, this,

                    function(response){
                        var res=eval('('+response+')');
                        this.data1 = res;
                        var noofq = res.count;
                        this.finalOutput = res;
                        var tempPan = new Wtf.Panel({
                            layout:'table',
                            autoHeight:true,
                            cls:'appraisal-table',
                            defaults: {
                                bodyStyle:'padding:20px;'
                            },
                            layoutConfig: {
                                columns: 2
                            }

                        });
                        for(var i=0;i<noofq;i++){
                            var qcolwid = res.data[i].qtype=="50"?1:2;
                           tempPan.add(this.qmainpanel = new Wtf.Panel({
                                colspan:qcolwid,
                                border :false,
                                id:this.id+"Personal"+i,
                                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px;'
                            }));
                            this.qmainpanel.add(this.quescontainer = new Wtf.Panel({
                                border :false,
                                id:"qcontainer"+i,
                                style : 'margin-right:5px;margin-bottom:5px;padding-right:5px;'
                            }));
                           this.quescontainer.add(new Wtf.Panel({
                                id:'ques'+i,
                                scope: this,
                                html: "<div style='font-size:1.3em;width:98%'>"+res.data[i].qdesc+"</div>"
                            }));
                            var noofans = res.data[i].qans;
                            for(var j=0;j<noofans;j++){
                                this.qmainpanel.add(new Wtf.form.FormPanel({items:[new Wtf.form.TextArea({
                                                id:res.data[i].qdescription+'ans'+j,
                                                scope: this,
                                                maxLength: 10000,
                                                hideLabel:noofans>0?false:true,
                                                fieldLabel: "<span style='font-size:14px;'>"+(j+1)+"</span>",
                                                width: qcolwid==1?"95%":"97.5%",
                                                style : 'padding:5px;font-size:14px'
                            })]}));

                        }
                        }
                        tempPan.doLayout();
                        this.PersonalattrPanel.add(tempPan);
                        this.PersonalattrPanel.doLayout();
                        this.doLayout();
                    },
                    function()
                    {
                        });
                      Wtf.Ajax.requestEx({
                        url:"Performance/Competency/getQuestionAnswerForm.pf",
                        scope:this,
                        params: {
                            appraisalid:this.empId.getValue(),
                            employee:this.employee,
                            appraisalcycid:this.appTypeCombo.getValue()
                        }
                    }, this,

                    function(response){
                        var res=eval('('+response+')');
                        this.data2 = res;
                        var ansdisable;
                        if(this.employee) {
                            ansdisable = this.ecomments.disabled;
                        } else {
                            ansdisable  = this.mcomments.disabled;
                        }
                        if(Wtf.get(this.id+"Personal")!=null)
                        {
                        	var ansfields = Wtf.get(this.id+"Personal").query(".x-form-textarea");
                        	for(var cnt = 0;cnt<ansfields.length;cnt++) {
                        		ansfields[cnt].disabled = ansdisable;
                        	}
                        }
                        for(var i =0 ;i <res.totalCount;i++){
                            for(var j=0;j<res.data[i].count;j++){

                                    if(Wtf.getCmp(res.data[i].question+'ans'+j) != undefined) {
                                        Wtf.getCmp(res.data[i].question+'ans'+j).setValue(res.data[i].answer[j]);
                                    }
                            }
                        }
                    },
                    function(){

                    });
    },

    afterEditFunction:function(e){
        var rec;
        var competencyW=this.comptstore.data.items[e.row].get("cmptwt");
        if(e.field=='manrat'){
            this.comptstore.data.items[e.row].set("manrat",e.record.get('manrat'));
            this.manratempty=true;
            if(this.competencyGrid.store.find('manrat','')>=0){
                this.manratempty=true;
            }
            for(var i=0;i<this.competencyGrid.store.getCount();i++){
                rec=this.competencyGrid.store.getAt(i).data;
                if(rec.manrat==NaN){
                    this.manratempty=false;
                }
            }
            var totalComp=0;
            if(this.manratempty){
                for(i=0;i<this.competencyGrid.store.getCount();i++){
                    rec=this.competencyGrid.store.getAt(i).data;
                    totalComp=((rec.cmptwt)*(rec.manrat))+totalComp;
                }
                this.ctotalComp=totalComp/100;
                this.escore.setValue(this.ctotalComp.toFixed(2));
            }
        }
    },
    afterGoalGridEdit:function(e){
        var rec;
        this.goalwt=0;
        for(var i=0;i<this.goalstore.getCount();i++){
            rec=this.goalstore.getAt(i).data;
            this.goalwt=this.goalwt+rec.gwth;
        }
        if(e.field=='gmanrat'){
            this.goalstore.data.items[e.row].set('gmanrat',e.record.get('gmanrat'));
            this.manratempty=true;
            var i;
            var totalComp=0;
            for(i=0;i<this.goalstore.getCount();i++){
                rec=this.goalstore.getAt(i).data;
                if(rec.gmanrat==NaN){
                    this.manratempty=false;
                }
            }
            if(this.manratempty){
                for(i=0;i<this.goalstore.getCount();i++){
                    rec=this.goalGrid.store.getAt(i).data;
                    totalComp=(rec.gwth*(rec.gmanrat))+totalComp;
                }
                this.gtotalComp=totalComp/this.goalwt;
                this.mscore.setValue(this.gtotalComp.toFixed(2));
            }
        }
    },
    validEditFunction:function(e){
        var competencyW=this.comptstore.data.items[e.row].get("cmptwt");
        if(e.field=='emprat')
        {
            if(e.value>competencyW||e.value<0)
            {
                calMsgBoxShow(40,1);
                return false;
            }
        }
        if(e.field=='manrat')
        {
            if(e.value>competencyW||e.value<0)
            {
                calMsgBoxShow(40,1);
                return false;
            }
        }
    },

    validEditFunction1:function(e){
        var goalW=this.goalstore.data.items[e.row].get("gwth");
        if(e.field=='gemprat')
        {
            if(e.value>goalW||e.value<0)
            {
                calMsgBoxShow(40,1);
                return false;
            }
        }
        if(e.field=='gmanrat'||e.value<0)
        {
            if(e.value>goalW||e.value<0)
            {
                calMsgBoxShow(40,1);
                return false;

            }
        }
    },
    resetForm:function(){
        this.appTypeCombo.setValue('');
        this.empId.setValue('');
        this.desig.setValue('');
        this.ecomments.setValue('');
        this.mcomments.setValue('');
        this.gapscore.setValue('');
        this.mscore.setValue(0);
        this.escore.setValue(0);
        this.startdate.setValue('');
        this.enddate.setValue('');
        this.newDesignation.setValue('');
        this.newDepartment.setValue('');
        this.salaryIncrement.setValue('');
        this.perrat.setValue();
        this.goalstore.removeAll();
        this.comptstore.removeAll();
    },
    afterCompEditFunction:function(e){
        var rec;
        var competencyW=this.comptstore.data.items[e.row].get("cmptwt");
        if(e.field=='emprat'){
            this.comptstore.data.items[e.row].set("emprat'",e.record.get('emprat'));
            this.manratempty=true;
            for(var i=0;i<this.competencyGrid.store.getCount();i++){
                rec=this.competencyGrid.store.getAt(i).data;
                if(rec.emprat==NaN)     {
                    this.manratempty=false;
                }
            }
            var totalComp=0;
            if(this.manratempty){
                for(i=0;i<this.competencyGrid.store.getCount();i++){
                    rec=this.competencyGrid.store.getAt(i).data;
                    totalComp=((rec.cmptwt)*(rec.emprat))+totalComp;
                }
                this.ctotalComp=totalComp/100;
                this.escore.setValue(this.ctotalComp.toFixed(2));
            }
        }
    },
    afterGoalGridEditFunction:function(e){
        var rec;
        this.goalwt=0;
        for(var i=0;i<this.goalstore.getCount();i++){
            rec=this.goalstore.getAt(i).data;
            this.goalwt=this.goalwt+rec.gwth;
        }
        if(e.field=='gemprat'){
            this.goalstore.data.items[e.row].set('gemprat',e.record.get('gemprat'));
            this.manratempty=true;
            var totalComp=0;
            for(i=0;i<this.goalstore.getCount();i++){
                rec=this.goalstore.getAt(i).data;
                if(rec.gemprat==NaN){
                    this.manratempty=false;
                }
            }
            if(this.manratempty){
                for(i=0;i<this.goalstore.getCount();i++){
                    rec=this.goalGrid.store.getAt(i).data;
                    totalComp=((rec.gwth)*(rec.gemprat))+totalComp;
                }
                this.gtotalComp=totalComp/this.goalwt;
                this.mscore.setValue(this.gtotalComp.toFixed(2));
            }
        }
    },
    validCompEditFunction:function(e){
        var competencyW=this.comptstore.data.items[e.row].get("cmptwt");
        if(e.field=='emprat'){
            if(e.value>competencyW||e.value<0){
                calMsgBoxShow(40,1);
                return false;
            }
        }
        if(e.field=='manrat'){
            if(e.value>competencyW||e.value<0){
                calMsgBoxShow(40,1);
                return false;
            }
        }
    },
    validGoalEditFunction:function(e){
        var goalW=this.goalstore.data.items[e.row].get("gwth");
        if(e.field=='gemprat'){
            if(e.value>goalW||e.value<0){
                calMsgBoxShow(40,1);
                return false;
            }
        }
        if(e.field=='gmanrat'){
            if(e.value>goalW||e.value<0){
                calMsgBoxShow(40,1);
                return false;
            }
        }
    },
    showFormElement:function(obj){
            obj.container.up('div.x-form-item').dom.style.display='block';
        },


    hideFormElement:function(obj){
        obj.container.up('div.x-form-item').dom.style.display='none';
    },
    
    removeItems:function(mainPanel){
        if(mainPanel.items != undefined){
            var complen = mainPanel.items.length;
            for(var i=0; i<complen; i++){
                mainPanel.remove(mainPanel.items.items[0])
            }
        }
    },

    employeeSelect:function(rec){
        if(Wtf.cmpPref.goal){
            this.goalGrid.show();
        } 
        var mainPanel = Wtf.getCmp(this.id+"Personal");
        if(Wtf.cmpPref.competency)
        	this.removeItems(mainPanel);
        if(this.question){
            this.competencyGrid.hide();
            if(this.PersonalattrPanel)
                this.PersonalattrPanel.show();
        }
        else {
            if(Wtf.cmpPref.competency){
                this.competencyGrid.show();
            }
            if(this.PersonalattrPanel)
                this.PersonalattrPanel.hide();
        }
        this.ecomments.setValue('');
        this.mcomments.setValue('');
        this.perrat.setValue('');
        this.appraisaltypeFlag="false";
        this.appraisalstatusFlag=0;
        if(this.employee){
            this.appraisalid=this.managerName.getValue();
        } else{
            if(rec.data.managerstatus==1) {
                this.changeEmptyText(1);
            } else {
                this.changeEmptyText(0);
            }
            this.appraisalid=this.empId.getValue();
        }

            var record=rec.data;
            if(record.appraisalid==this.appraisalid)
            {
                    this.desig.setValue(record.designation);
                    this.designationid=record.designationid;
                    this.tempDesigStore.filterBy(function(rec){
                        if(rec.data.id == this.designationid) {
                            return false;
                        }
                        return true;
                    },this);
                    var allRecords = this.tempDesigStore.getRange();
                    this.newDesigStore.removeAll();
                    this.newDesigStore.add(allRecords);
                    this.userid=record.userid;
                    this.managerid=record.managerid;
                    this.appcycleid=record.appcycleid;
                    this.startdate.setValue(record.startdate);
                    this.enddate.setValue(record.enddate);
                    this.apprMan.setValue(record.managername);
                    this.ecomments.setValue(record.employeecomment);
                    this.mcomments.setValue(record.managercomment);
                    if(record.performance!=""){
                        this.perrat.setValue(record.performance);
                    } else if(Wtf.cmpPref.promotionrec){
                        this.perrat.clearValue();
                    }
                    if(this.employee){
                        this.escore.setValue(record.employeecompscore);
                        this.mscore.setValue(record.employeegoalscore);
                    } else{
                        this.escore.setValue(record.managercompscore);
                        this.mscore.setValue(record.managergoalscore);
                    }
                    if(record.salaryrec=='1' || record.salaryrec==1){
                        this.salaryY.setValue(true);
                        this.salaryN.setValue(false);
                        this.salaryDetails.show();
                        this.newDesignation.setValue(record.newdesig);
                        this.newDepartment.setValue(record.newdept);
                        this.salaryIncrement.setValue(record.salaryinc);
                        this.doLayout();
                    } else{
                        this.salaryY.setValue(false);
                        this.salaryN.setValue(true);
                        this.newDesignation.setValue('');
                        this.newDepartment.setValue('');
                        this.salaryIncrement.setValue('');
                    }
                }
                if(record.salaryrec=='1' || record.salaryrec==1){
                    this.salaryY.setValue(true);
                    this.salaryN.setValue(false);
                    this.salaryDetails.show();
                    this.newDesignation.setValue(record.newdesig);
                    this.newDepartment.setValue(record.newdept);
                    this.salaryIncrement.setValue(record.salaryinc);
                    this.doLayout();
                } else{
                    this.salaryY.setValue(false);
                    this.salaryN.setValue(true);
                    this.newDesignation.setValue('');
                    this.newDepartment.setValue('');
                    this.salaryIncrement.setValue('');
                }
              if(this.isSubmitted){
                this.salaryY.enable();
                this.salaryN.enable();
                this.newDesignation.enable();
                this.newDepartment.enable();
                this.salaryIncrement.enable();
              }else{
            	  this.salaryY.disable();
                  this.salaryN.disable();
                  this.newDesignation.disable();
                  this.newDepartment.disable();
                  this.salaryIncrement.disable();
              }
        this.storeLoad(this.appraisalid,this.userid,this.designationid,this.managerid,this.appcycleid);
        if(rec.data.isquestionemp == "true"){
        	this.question = true;
            this.getQuestionRequest(rec.data.designationid, rec, true);
        }else{
        	this.question = false;
        }
    },
    storeLoad:function(appraisalid,employeeid,designationid,managerid,appcycleid){
    	this.appraisalids = appraisalid;
        if(Wtf.cmpPref.goal){
            this.goalstore.load({
                params:{
                    flag:161,
                    empid:employeeid,
                    appraisal:appraisalid,
                    managerid:managerid,
                    appcycleid:appcycleid,
                    employee:this.employee
                }
            });
        }
        calMsgBoxShow(202,4,true);

        this.comptstore.load({
            params:{
                flag:160,
                desig:designationid,
                appraisal:appraisalid,
                employee:this.employee
            }
        });
    },
    setemployeeDetails:function(){
        var record;
        if(this.empnameStore.getCount()>0){
            //this.empId.disable();
            record=this.empnameStore.getAt(0).data;
            this.appraisalid=record.appraisalid;
            this.empId.setValue(record.appraisalid);
            this.storeLoad(record.appraisalid,record.userid,record.designationid,record.appcycleid);
            this.ecomments.setValue(record.employeecomment);
            this.mcomments.setValue(record.managercomment);
            this.apprMan.setValue(record.managername);
            this.desig.setValue(record.designation);
        }
    },
    addDesignation:function(){
    	WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");//WtfGlobal.showmasterWindow(1,Wtf.desigStore,"Add");
    },
    addDepartment:function(){
    	WtfGlobal.showmasterWindow(7,Wtf.depStore,"Add");//WtfGlobal.showmasterWindow(7,Wtf.depStore,"Add");
    },
    validateSalary:function(){
        if(this.salaryIncrement.getValue()>99 || this.salaryIncrement.getValue()<-99){
            this.salaryIncrement.setValue('');
            calMsgBoxShow(164,2);
        }
    },
    findrecord:function(rec){
                if(rec.get('currentFlag')=="1"){
                    return true;
                }else{
                    return false;
                }
            },
    loademployeestores:function(){
                 if(this.empnameStore.getCount()>0){
                this.enableDisableSaveButtons(true);
                this.appraisalids="";
                this.appraisalid="";
                this.designationid="";
                var employeedraft=0;
                var managerdraft=0;
                for(var i=0;i<this.empnameStore.getCount();i++){
                	this.appraisalids+=this.empnameStore.getAt(i).get('appraisalid')+",";
                }
                if(this.empnameStore.getAt(0).get('employeestatus')==1){
                    this.changeEmptyText(1);
                } else {
                    this.changeEmptyText(0);
                }
                this.designationid=this.empnameStore.getAt(0).get('designationid');
                this.appraisalid=this.empnameStore.getAt(0).get('appraisalid');
                this.appraisalids=this.appraisalids.substr(0,this.appraisalids.length-1);
                employeedraft=this.empnameStore.getAt(0).get('employeedraft');
                managerdraft=this.empnameStore.getAt(0).get('managerdraft');
                this.ecomments.setValue(this.empnameStore.getAt(0).get('employeecomment'));
                this.escore.setValue(this.empnameStore.getAt(0).get('employeecompscore'));
                this.mscore.setValue(this.empnameStore.getAt(0).get('employeegoalscore'));
                this.goalstore.load({
                        params:{
                            flag:161,
                            isemployee:true,
                            appraisal:this.appraisalids
                        }
                    });
                    calMsgBoxShow(202,4,true);
                    this.comptstore.load({
                        params:{
                            flag:160,
                            desig:this.designationid,
                            appraisal:this.appraisalid,
                            employeedraft:employeedraft,
                            managerdraft:managerdraft,
                            employee:this.employee
                        }
                    });
            } else {
                if(!this.appTypeCombo.value){
                    this.changeEmptyText(3);
                }
                else if(this.appTypeStore.getAt(this.nochange).get('substart')==1){
                    this.changeEmptyText(4);
                } else if(this.appTypeStore.getAt(this.nochange).get('substart')==2){
                    this.changeEmptyText(2);
                }
                this.enableDisableSaveButtons(false);
            }
    },
    changeEmptyText:function(emptytextflag){
        var tempcnt=0;
        this.isSubmitted=true;
        if(Wtf.cmpPref.weightage)
        {
            tempcnt=1;
        }
        if(emptytextflag!=0) {
        	this.setBtnStatus = true;
            this.enableDisableSaveButtons(false);
            this.competencyGrid.getColumnModel().setEditable(2+tempcnt,false);
            this.competencyGrid.getColumnModel().setEditable(3+tempcnt,false);
            this.goalGrid.getColumnModel().setEditable(1+tempcnt,false);
            this.goalGrid.getColumnModel().setEditable(2+tempcnt,false);
            if(emptytextflag==1){
            	this.isSubmitted=false;
                this.perrat.clearInvalid();
                this.helpTemplate2.overwrite(this.helpTextPanel.body);
            } else if(emptytextflag==2){
                this.helpTemplate5.overwrite(this.helpTextPanel.body);
            } else if(emptytextflag==3){
                this.helpTemplate7.overwrite(this.helpTextPanel.body);
            } else if(emptytextflag==4){
                this.helpTemplate6.overwrite(this.helpTextPanel.body);
            }
            this.doLayout();
            if(this.employee){
                this.ecomments.disable();
            } else{
                this.mcomments.disable();
                this.perrat.disable();
                this.salaryY.disable();
            }
            if(this.question && Wtf.cmpPref.competency) {
                var ansfields = Wtf.get(this.id+"Personal").query(".x-form-textarea");
                for(var cnt = 0;cnt<ansfields.length;cnt++) {
                    ansfields[cnt].disabled = true;
                }
            }
        } else {
            this.enableDisableSaveButtons(true);
            this.competencyGrid.getColumnModel().setEditable(2+tempcnt,true);
            this.competencyGrid.getColumnModel().setEditable(3+tempcnt,true);
            this.goalGrid.getColumnModel().setEditable(1+tempcnt,true);
            this.goalGrid.getColumnModel().setEditable(2+tempcnt,true);
            if(!this.question || ! Wtf.cmpPref.competency){
                if(companyid=="963b6fdc-316a-4a5f-b430-6a05c5b34363") {
                    this.helpTemplateKrawler.overwrite(this.helpTextPanel.body);
                } else {
                    this.helpTemplate1.overwrite(this.helpTextPanel.body);
                }
            } 
            this.doLayout();
            if(this.employee){
                this.ecomments.enable();
            } else{
                this.mcomments.enable();
                this.perrat.enable();
                this.salaryY.enable();
            }
        }
    },

    getSaveButtons : function() {
        var submitbtn= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.submit"),//'Submit',
            disabled:true,
            tooltip:WtfGlobal.getLocaleText("hrms.performance.appraisal.form.submit.tooltip"),//"Submit your evaluation for the chosen appraisal.",
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            handler:function(){
            	this.submitFunction(false)
            },
            scope:this
        });
        var draftbtn= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.save.as.draft"),//'Save As Draft',
            disabled:true,
            tooltip:WtfGlobal.getLocaleText("hrms.performance.appraisal.form.save.tooltip"),//"Save your appraisal form as a draft in order to revisit and submit at a later time.",
            iconCls:'pwndCommon draftbuttonIcon',
            handler:function(){
            	this.submitFunction(true)
            },
            scope:this
        });
        
        this.printbtn = new Wtf.Button({
        text:WtfGlobal.getLocaleText("hrms.common.print"),//'Print',
        scope:this,
        iconCls:'pwndPrint printData',
        tooltip:{text:WtfGlobal.getLocaleText("hrms.performance.print.appraisal.form")},//'Print Appraisal Form'},
        handler:function() {
        		this.isPrint=true;
        		this.printAndpdf();
        	}
        });
        this.pdfbtn = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.export.pdf.format"),//'Export to Pdf Format',
            scope:this,
            iconCls:'pwndExport exportpdf',
            tooltip:{text:WtfGlobal.getLocaleText("hrms.performance.export.appraisal.form")},//'Export Appraisal Form'},
            handler:function() {
            	this.isPrint=false;
            	this.printAndpdf();
            }
        });

        var cmpevalbtns=new Array();
        cmpevalbtns.push(draftbtn,'-',submitbtn,'-',this.pdfbtn,'-',this.printbtn);
        return cmpevalbtns;
    },	
    getSaveButtonsBottom : function() {
        var submitbtn= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.submit"),//'Submit',
            disabled:true,
            tooltip:WtfGlobal.getLocaleText("hrms.performance.appraisal.form.submit.tooltip"),//"Submit your evaluation for the chosen appraisal.",
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            handler:function(){
            	this.submitFunction(false)
            },
            scope:this
        });
        var draftbtn= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.timesheet.save.as.draft"),//'Save As Draft',
            disabled:true,
            tooltip:WtfGlobal.getLocaleText("hrms.performance.appraisal.form.save.tooltip"),//"Save your appraisal form as a draft in order to revisit and submit at a later time.",
            iconCls:'pwndCommon draftbuttonIcon',
            handler:function(){
            	this.submitFunction(true)
            },
            scope:this
        });

        var cmpevalbtns=new Array();
        cmpevalbtns.push(draftbtn,'-',submitbtn);
        return cmpevalbtns;
    },
    printAndpdf:function(){
    	var url = "Performance/PrintAppraisalReportPDF/appraisalFormExport.pf?"+"promotion="+this.salaryY.getValue()+"&question="+this.question+"&uid="+this.userid+"&appraisalcycid="+this.appTypeCombo.getValue()+"&isPrint="+this.isPrint+"&desigId="+this.designationid+"&appraisalid="+this.appraisalid+"&employee="+this.employee+"&managerid="+this.managerid+"&appraisalids="+this.appraisalids;
    	if(this.isPrint)
    		window.open(url,"mywindow","menubar=1,resizable=1,scrollbars=1");
    	else
    		Wtf.get('downloadframe').dom.src = url;
    },

    enableDisableSaveButtons : function(enable) {
        if(enable) {
            this.getTopToolbar().enable();
            this.getBottomToolbar().enable();
        } else {
            this.getTopToolbar().disable();
            this.getBottomToolbar().disable();
            if(this.setBtnStatus){
            	this.printbtn.setDisabled(false);
            	this.pdfbtn.setDisabled(false);
            	this.setBtnStatus = false;
            }
        }
    },
    addPerformanceRating:function(){
        WtfGlobal.showmasterWindow(13,Wtf.prat,"Add");
    },
    replaceAll: function(str,oldStr,newStr){
    	while(str.search(oldStr)!=-1){
    		str = str.replace(oldStr,newStr);
    	}
    	return str;
    }
});
