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
Wtf.approvalWindow = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        buttons: [
        {
            text:  WtfGlobal.getLocaleText("hrms.common.submit"),
            handler: this.sendapproveSaveRequest,
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
    Wtf.approvalWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.approvalWindow, Wtf.Window, {
    initComponent: function() {
        Wtf.approvalWindow.superclass.initComponent.call(this);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.approvalWindow.superclass.onRender.call(this, config);

        this.salaryInc= new Wtf.form.NumberField({
            fieldLabel:'Salary Increment',
            width:200,
            maxLength:2
        });

        this.deptrec=new Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'name'
        }]);

        this.deptStore =  new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            baseParams: {
                configid:7,
                flag:203
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.deptrec),
            autoLoad : false
        });
        this.deptStore.load();

        this.positionrec=new Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'name'
        }
        ]);

        this.positionStore =  new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.positionrec),
            autoLoad : false
        });


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

        this.oldDesig=new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.OldDesignation"),
            labelWidth:110,
            disabled:true,
            width:200
        });

        this.oldDepart=new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.OldDepartment"),
            labelWidth:110,
            disabled:true,
            width:200
        });

        this.oldDesig.setValue(this.desig);
        this.oldDepart.setValue(this.dept);

        this.newDesignation = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.new.designation"),
            store:this.positionStore,
            //anchor:'50.5%',
            mode:'local',
            hiddenName :'name',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,
            //listWidth:200,
            width:200
        });

        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.on('load',function(){
                this.tempDesigStore.loadData(Wtf.desigStore.reader.jsonData);
                this.tempDesigStore.filterBy(function(rec){
                if(rec.data.name == this.desig) {
                    return false;
                }
                return true;
            },this);
            var allRecords = this.tempDesigStore.getRange();
            this.positionStore.removeAll();
            this.positionStore.add(allRecords);
                WtfGlobal.closeProgressbar();
            },this);
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }else{
            this.tempDesigStore.loadData(Wtf.desigStore.reader.jsonData);
            this.tempDesigStore.filterBy(function(rec){
                if(rec.data.name == this.desig) {
                    return false;
                }
                return true;
            },this);
            var allRecords = this.tempDesigStore.getRange();
            this.positionStore.removeAll();
            this.positionStore.add(allRecords);
            WtfGlobal.closeProgressbar();
        }

        this.newDepartment = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.new.department"),
            store:this.deptStore,
            mode:'local',
            //anchor:'50.5%',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,
            //listWidth:200,
            width:200
        });

        this.comments=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.comments")+'*',
            width:200,
            height:55,
            validator:WtfGlobal.noBlankCheck,
            allowBlank:false
        });
        this.winArr=[];
        if(this.reviewstatus){
            if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll)){
                this.winArr.push(this.salaryInc);
            }
            this.winArr.push(this.oldDesig,this.oldDepart,this.newDesignation,this.newDepartment,this.comments);
        } else{
            this.winArr.push(this.comments);
        }


        this.salaryForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            labelWidth :100,
            layoutConfig: {
                deferredRender: false
            },
            items:this.winArr
        });

        this.salaryPanel= new Wtf.Panel({
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
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.performance.appraisals"), WtfGlobal.getLocaleText("hrms.appraisal.enter.details"))
                },{
                    border:false,
                    region:'center',
                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    layout:"fit",
                    items: [this.salaryForm]
                }]
            }]
        });
        this.add(this.salaryPanel);
    }, 
    sendapproveSaveRequest:function(){
        if(!this.salaryForm.getForm().isValid())
        {
            return;
        }
        else
        {
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp?flag="+this.flag,
                url:"Performance/NonAnonymousAppraisal/reviewNonAnonymousAppraisal.pf",
                params: {
                    appraisalids:this.appids,
                    reviewstatus:this.reviewstatus,
                    reviewercomment:this.comments.getValue(),
                    salaryincrement:this.salaryInc.getValue(),
                    department:this.newDepartment.getValue(),
                    designation:this.newDesignation.getValue(),
                    employeeid:this.employeeid,
                    appraisalcycleid:this.appcycleid
                }
            },
            this,
            function(res){               
                var resp=eval('('+res+')');                
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),""+resp.msg+""],0);
                this.close();
                this.finalGrid.getSelectionModel().clearSelections() ;
                this.ds.load();
            },
            function(){
                calMsgBoxShow(27,2);
                this.finalGrid.getSelectionModel().clearSelections() ;
            })
            
        }
    }
});


Wtf.appraisalAppWindow = function(config) {
    Wtf.apply(this,{
        buttonAlign :'right',
        buttons: [
        {
            text:  WtfGlobal.getLocaleText("hrms.common.submit"),
            handler: this.sendapproveSaveRequest,
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
    Wtf.appraisalAppWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.appraisalAppWindow, Wtf.Window, {
    initComponent: function() {
        Wtf.appraisalAppWindow.superclass.initComponent.call(this);
    },
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender: function(config) {
        Wtf.appraisalAppWindow.superclass.onRender.call(this, config);

        this.salaryInc= new Wtf.form.NumberField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.salary.increment")+'(%)',
            width:200,
            maxLength:3
        });

        this.deptrec=new Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'name'
        }]);

        this.deptStore =  new Wtf.data.Store({
            url: "Common/getMasterDataField.common",
            baseParams: {
                configid:7,
                flag:203
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.deptrec),
            autoLoad : false
        });
        this.deptStore.load();

        this.positionrec=new Wtf.data.Record.create([
        {
            name:'id'
        },{
            name:'name'
        }
        ]);

        this.positionStore =  new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader1({
                root:'data'
            },this.positionrec),
            autoLoad : false
        });

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
        
        this.oldDesig=new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.OldDesignation"),
            labelWidth:110,
            disabled:true,
            width:200
        });

        this.oldDepart=new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.OldDepartment"),
            labelWidth:110,
            disabled:true,
            width:200
        });

        this.oldDesig.setValue(this.desig);
        this.oldDepart.setValue(this.dept);

        this.newDesignation = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.new.designation"),
            store:this.positionStore,            
            mode:'local',
            hiddenName :'name',
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,            
            width:200
        });


        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.on('load',function(){
                this.tempDesigStore.loadData(Wtf.desigStore.reader.jsonData);
                this.tempDesigStore.filterBy(function(rec){
                if(rec.data.name == this.desig) {
                    return false;
                }
                return true;
            },this);
            var allRecords = this.tempDesigStore.getRange();
            this.positionStore.removeAll();
            this.positionStore.add(allRecords);
                WtfGlobal.closeProgressbar();
            },this);
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }else{
            this.tempDesigStore.loadData(Wtf.desigStore.reader.jsonData);
            this.tempDesigStore.filterBy(function(rec){
                if(rec.data.name == this.desig) {
                    return false;
                }
                return true;
            },this);
            var allRecords = this.tempDesigStore.getRange();
            this.positionStore.removeAll();
            this.positionStore.add(allRecords);
            WtfGlobal.closeProgressbar();
        }
        

        this.newDepartment = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.new.department"),
            store:this.deptStore,
            mode:'local',            
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,            
            width:200
        });

        this.comments=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.performance.comments")+'*',
            width:200,
            height:55,
            validator:WtfGlobal.noBlankCheck,
            allowBlank:false
        });
        this.winArr=[];
        if(this.reviewstatus){
            if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll)){
                this.winArr.push(this.salaryInc);
            }
            this.winArr.push(this.oldDesig,this.oldDepart,this.newDesignation,this.newDepartment,this.comments);
        } else{
            this.winArr.push(this.comments);
        }


        this.salaryForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            labelWidth :120,
            layoutConfig: {
                deferredRender: false
            },
            items:this.winArr
        });

        this.salaryPanel= new Wtf.Panel({
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
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.performance.appraisals"), WtfGlobal.getLocaleText("hrms.appraisal.enter.details"))
                },{
                    border:false,
                    region:'center',
                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    layout:"fit",
                    items: [this.salaryForm]
                }]
            }]
        });
        this.add(this.salaryPanel);
    },
    sendapproveSaveRequest:function(){
        if(!this.salaryForm.getForm().isValid())
        {
            return;
        }
        else
        {
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base + "hrms.jsp?flag="+this.flag,
                url:"Performance/AnonymousAppraisal/reviewanonymousAppraisalReport.pf",
                params: {
                    reviewstatus:this.reviewstatus,
                    reviewercomment:this.comments.getValue(),
                    salaryincrement:this.salaryInc.getValue(),
                    department:this.newDepartment.getValue(),
                    designation:this.newDesignation.getValue(),
                    employeeid:this.employeeid,
                    appraisalcycleid:this.appcycleid
                }
            },
            this,
            function(res){
                var resp=eval('('+res+')');
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),""+resp.msg+""],0);
                this.close();                
                this.ds.load();
            },
            function(){
                calMsgBoxShow(27,2);                
            })

        }
    }
});
