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
Wtf.PayrollResourceGrid=function(config){
    Wtf.apply(this, config);
    
    this.id=new Wtf.form.TextField({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.employee.id")+'*',
        name:'id',
        allowBlank:false,
        width:'75%',
        id:'id',
        value:this.record.data.employeeid
    });
    this.historyid=new Wtf.form.Hidden({
        name:'historyid',
        id:'historyid',
        value:this.record.data.id
    });
    this.userid=new Wtf.form.Hidden({
        name:'userid',
        id:'userid',
        value:this.record.data.resource
    });
    this.sDate=new Wtf.form.Hidden({
        name:'paycyclestartdate',
        id:'paycyclestartdate',
        value:this.sdate
    });
    this.eDate=new Wtf.form.Hidden({
        name:'paycycleenddate',
        id:'paycycleenddate',
        value:this.edate
    });
    
    this.frequencyHidden=new Wtf.form.Hidden({
        name:'frequency',
        id:'frequency',
        value:this.frequency
    });

    this.name=new Wtf.form.TextField({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.FullName")+'*',
        name:'fullname',
        allowBlank:false,
        width:'75%',
        id:'fullname',
        value:this.record.data.fullname
    });
    
    this.CostCenterRec = new Wtf.data.Record.create([{
    	name:"id"
    },{
    	name:"name"
    },{
    	name:"code"
    },{
    	name:"creationDate"
    }]);

    this.CostCenterReader = new Wtf.data.KwlJsonReader1({
    	root:"data",
    	totalProperty:"count"
    },this.CostCenterRec);
    
    this.CostCenterStore = new Wtf.data.Store({
    	url: "Common/getCostCenter.common",
    	reader:this.CostCenterReader
    });
    this.CostCenterStore.load();
    
    this.costcenter = new Wtf.form.ComboBox({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.costcenter"),
        store:this.CostCenterStore,
        mode:'local',
        typeAhead:true,
        editable:true,
        valueField: 'id',
        hiddenName :'costcenter',
        displayField:'name',
        width:227,
        triggerAction:'all',
        forceSelection:true,
        emptyText:WtfGlobal.getLocaleText("hrms.common.select.cost.center")
    });

    this.CostCenterStore.on('load',function(){
        this.costcenter.setValue(this.record.data.costcenter);
    },this);

    this.jobtitle = new Wtf.form.ComboBox({
        store:Wtf.desigStore,
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.designation")+'*',
        mode:'local',
        hiddenName:'jobtitle',
        name:'designationid',
        width:227,
        allowBlank:false,
        valueField: 'id',
        displayField:'name',
        triggerAction: 'all',
        forceSelection:true,
        validator:WtfGlobal.validateDropDowns,
        typeAhead:true
    });

    if(!Wtf.StoreMgr.containsKey("desig")){
        Wtf.desigStore.on('load',function(){
            this.jobtitle.setValue(this.record.data.jobtitle);
        },this);
        Wtf.desigStore.load();
        Wtf.StoreMgr.add("desig",Wtf.desigStore);
    }else{
        this.jobtitle.setValue(this.record.data.jobtitle);
    }
    this.contract=new Wtf.form.TextField({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.Contract")+'*',
        name:'contract',
        allowBlank:false,
        width:'75%',
        id:'contract',
        hidden:true,
        value:this.record.data.contract
    });
    
    this.absence=new Wtf.form.TextField({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.absence")+'*',
        name:'absence',
        allowBlank:false,
        width:'75%',
        id:'absence',
        value:this.record.data.absence
    });
    
    this.actual=new Wtf.form.TextField({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.actual")+'*',
        name:'actual',
        allowBlank:false,
        width:'75%',
        id:'actual',
        value:this.record.data.actual
    });
    
    this.employementDate=new Wtf.form.DateField({
    	id:'employmentdate',
        fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.employment.date")+"*",
        format:'m/d/Y',
        width:227,
        allowBlank:false,
        name:'employmentdate',
        hidden:true,
        value:this.record.data.employmentdate
    });
    
    this.contractDate=new Wtf.form.DateField({
    	id:'contractenddate',
        fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.contract.date")+"*",
        format:'m/d/Y',
        width:227,
        allowBlank:false,
        name:'contractenddate',
        hidden:true,
        value:this.record.data.contractenddate
    });
    
    this.info= new Wtf.form.FormPanel({
        url:"Payroll/Date/editResorcePayrollData.py",
        region:'center',
        cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder visibleDisabled',
        bodyStyle: "background: transparent;",
        border:false,
        bodyBorder:false,
        style: "background: transparent;padding-left:20px;padding-top: 20px;padding-right: 0px;",
        width:'100%',
        height:'100%',
        id:'info',
        defaultType:'textfield',
        items:[this.id, this.name, this.costcenter, this.jobtitle, this.absence,this.historyid,this.userid,this.sDate,this.eDate,this.frequencyHidden]
    });

    this.MainWinPanel= new Wtf.Panel({
        border:false,
        autoScroll:true,
        layout:'border',
        items:[{
            region:'north',
            id:'north1',
            border:false,
            height:80,
            style:"background: #FFFFFF;",
            html:getTopHtml(WtfGlobal.getLocaleText("hrms.payroll.manage.employee.details"), WtfGlobal.getLocaleText("hrms.payroll.manage.employee.details"), '../../images/edit-user-popup.jpg'),//'<br><div><font size= 2>'+(config.record==null?'Create':'Edit')+' User</font></div><br><font size= 1>'+(config.record==null?'Enter':'Edit')+' user details</font>',
            layout:'fit'
        },{
            region:'center',
            id:'center1',
            border:false,
            layout:'fit',
            cls : 'formstyleClass2',
            items:[this.info]
        }]
    });

    this.win=new Wtf.Window({
        iconCls:getButtonIconCls(Wtf.btype.winicon),
        title:WtfGlobal.getLocaleText("hrms.payroll.manage.employee.details"),
        id:'editResourceDetails',
        height:350,
        width:430,
        modal:true,
        resizable:false,
        layout:'fit',
        scope:this,
        items:[this.MainWinPanel],
        buttonAlign :'right',
        buttons: [{
            text:WtfGlobal.getLocaleText("hrms.common.Save"),
            scope:this,
            handler:function(){
                if(!this.info.form.isValid()){
                    return
                }else{
                    calMsgBoxShow(200,4,true);
                    this.info.getForm().submit({
                        waitMsg:WtfGlobal.getLocaleText("hrms.common.Savinguserinformation"),
                        success:function(f,a){
                    		this.win.close();
                    		this.fireEvent('save',a);
                        },
                        failure:function(f,a){
                            this.win.close();
                        },
                        scope:this
                    });
                }
            }
        },{
            text:WtfGlobal.getLocaleText("hrms.common.cancel"),
            handler:this.cancel.createDelegate(this)
        }]
    });

    this.win.on("show",function(obj) {
        if(obj.tools)
            obj.tools.close.dom.style.display='none';
    },this);
    
    this.win.show();

    Wtf.PayrollResourceGrid.superclass.constructor.call(this,config);
    this.addEvents({
        'save':true,
        'notsave':true
    });
};

Wtf.extend( Wtf.PayrollResourceGrid,Wtf.Panel,{
    onRender:function(config){
        Wtf.PayrollResourceGrid.superclass.onRender.call(config);
    },
    cancel:function(){
        this.win.close();
        this.win.destroy();
    }
});
