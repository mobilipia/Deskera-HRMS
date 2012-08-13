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

Wtf.MalaysianCompanyStatutoryForm = function (config){
    Wtf.apply(this,config);
    Wtf.MalaysianCompanyStatutoryForm.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.MalaysianCompanyStatutoryForm, Wtf.Panel,{
	layout:'fit',
	closable:false,

    initComponent:function (){
        Wtf.MalaysianCompanyStatutoryForm.superclass.initComponent.call(this);
    },

    onRender: function(config){

        var btns=this.getTopToolbar();
        var amanahSahamNasional = this.getAmanahSahamNasional();
        var tabungHaji = this.getTabungHaji();
        var cp21 = this.getCP21();
        var hrdLevy = this.getHRDLevy();
        var tp1 = this.getTP1();
        var tp2 = this.getTP2();
        var tp3 = this.getTP3();
        var pcb2 = this.getPCB2();
        var cp39 = this.getCP39();
        var cp39a = this.getCP39A();
        var company = this.getCompanyDetails();
        var ea = this.getEA();
       
    	var p = new Wtf.Panel({
    	    layout:'column',
    	    bodyBorder:false,
    	    border:false,
    	    items: [{
    	        columnWidth:.32,
    	        bodyBorder:false,
        	    border:false,
                layout:'form',
                style:"padding-top:10px; padding-rigth:1px; padding-bottom:1px; padding-left:10px;",
    	        items:[amanahSahamNasional,hrdLevy,tabungHaji,cp21]
    	    },{
    	        columnWidth:.32,
    	        layout:'form',
                style:"padding-top:10px; padding-rigth:10px; padding-bottom:10px; padding-left:10px;",
    	        bodyBorder:false,
        	    border:false,
                items : [pcb2,tp1,tp2,tp3]

    	    },{
    	        columnWidth:.36,
    	        bodyBorder:false,
                layout:'form',
                style:"padding-top:10px; padding-rigth:10px; padding-bottom:10px; padding-left:10px;",
        	    border:false,
                items:[cp39, cp39a,company, ea]

    	    }]
    	});

    	this.formPanel= new Wtf.form.FormPanel({
    		autoWidth:true,
			autoScroll:true,
			baseParams:{
				userid:this.userid
			},
			url:"Payroll/MalaysianStatutoryForm/saveCompanyFormInformation.py",
			border:true,
			bodyBorder:false,
			id:'formPanel',
			items:[p]
		});
    	this.mainPanel = new Wtf.Panel({
    		layout:"fit",
    		border:false,
    		scope:this,
            tbar:btns,
    		items:[this.formPanel],
    		bbar:['->',{
    	     	id: 'Submit',
    	     	iconCls:getButtonIconCls(Wtf.btype.submitbutton),
    	        text: WtfGlobal.getLocaleText("hrms.common.submit"),
    	        handler: this.submit.createDelegate(this)
    	    }]
    	});

    	this.add(this.mainPanel);

        if(this.paymentTypeCombo.getValue()==1){
            this.chqNo.disable();
            this.chqNo.setValue("");
            
        }else {
            this.chqNo.enable();
            this.chqNo.setValue(this.userdata!=undefined?this.userdata.asnchqno:"");
        }
        this.paymentTypeCombo.on("select", function(combo,rec,index){
            if(index==1){
                this.chqNo.enable();
                this.chqNo.setValue(this.userdata!=undefined?this.userdata.asnchqno:"");
                
            }else {
                this.chqNo.disable();
                this.chqNo.setValue("");
            }

        },this);

        if(this.tabunghajiPaymentTypeCombo.getValue()==1){
            this.tabunghajiChqNo.disable();
            this.tabunghajiChqNo.setValue("");

        }else {
            this.tabunghajiChqNo.enable();
            this.tabunghajiChqNo.setValue(this.userdata!=undefined?this.userdata.tabunghajichqno:"");
        }
        this.tabunghajiPaymentTypeCombo.on("select", function(combo,rec,index){
            if(index==1){
                this.tabunghajiChqNo.enable();
                this.tabunghajiChqNo.setValue(this.userdata!=undefined?this.userdata.tabunghajichqno:"");

            }else {
                this.tabunghajiChqNo.disable();
                this.tabunghajiChqNo.setValue("");
            }

        },this);

        if(this.hrdPaymentTypeCombo.getValue()==1){
            this.hrdChqNo.disable();
            this.hrdChqNo.setValue("");

        }else {
            this.hrdChqNo.enable();
            this.hrdChqNo.setValue(this.userdata!=undefined?this.userdata.hrdchqno:"");
        }
        this.hrdPaymentTypeCombo.on("select", function(combo,rec,index){
            if(index==1){
                this.hrdChqNo.enable();
                this.hrdChqNo.setValue(this.userdata!=undefined?this.userdata.hrdchqno:"");

            }else {
                this.hrdChqNo.disable();
                this.hrdChqNo.setValue("");
            }

        },this);

        if(!Wtf.StoreMgr.containsKey("adminstore")){
            Wtf.adminStore.load();
            Wtf.StoreMgr.add("adminstore",Wtf.adminStore)
        }

        Wtf.adminStore.on('load', function(){
            if(this.userdata!=undefined){

                this.adminCmb.setValue(this.userdata.asnpreparedby);
                this.tabunghajiAdminCmb.setValue(this.userdata.tabunghajipreparedby);
                this.adminCmbTP1.setValue(this.userdata.personinchargetp1);
                this.adminCmbTP2.setValue(this.userdata.personinchargetp2);
                this.adminCmbTP3.setValue(this.userdata.personinchargetp3);
                this.adminCmbCP39.setValue(this.userdata.personinchargecp39);
                this.adminCmbCP39A.setValue(this.userdata.personinchargecp39a);
                this.adminCmbPCB2.setValue(this.userdata.personinchargepcb2);
                this.adminCmbEA.setValue(this.userdata.adminCmbEA);
            } else {
                this.adminCmb.setValue("");
                this.tabunghajiAdminCmb.setValue("");
                this.adminCmbTP1.setValue("");
                this.adminCmbTP2.setvalue("");
                this.adminCmbTP3.setValue("");
                this.adminCmbCP39.setValue("");
                this.adminCmbCP39A.setValue("");
                this.adminCmbPCB2.setValue("");
                this.adminCmbEA.setValue("");
            }
        },this);

        this.decMonthCmb.on("select", function(a,b,index){
            
            this.reloadData();

        },this);

        this.decYearCmb.on("select", function(a,b,index){
            
            this.reloadData();

        },this);
        
    	Wtf.MalaysianCompanyStatutoryForm.superclass.onRender.call(this, config);
    },
    reloadData :function(){

           calMsgBoxShow(202,4,true);
            Wtf.Ajax.requestEx({
                url: "Payroll/MalaysianStatutoryForm/getCompanyFormInformation.py",
                params: {
                    companyid:companyid,
                    month:this.decMonthCmb.getValue(),
                    year:this.decYearCmb.getValue()
                }
            }, this,
            function(response){
                var userdata = eval('('+response+')');
                var data = userdata.data.userdata;
                if(data!=undefined){

                    this.setAmanahSahamNasional(data);
                    this.setTabungHaji(data);
                    this.setCP21(data);
                    this.setHRDLevy(data);
                    this.setPCB2(data);
                    this.setTP1(data);
                    this.setTP2(data);
                    this.setTP3(data);
                    this.setCP39(data);
                    this.setCP39A(data);
                    this.setEA(data);
                    this.setCompanyDetails(data);
                }


                WtfGlobal.closeProgressbar();
            },
            function(response){

            });
    },
    getTopToolbar : function(){

       var bbtns = [];
       this.decMonthCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Month"),
            hiddenName: 'month',
            forceSelection:true,
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.monthStore0,
            width:80,
            typeAhead:true,
            value:this.statutoryForm!=undefined?this.statutoryForm.monthCmb.getValue():new Date().getMonth()
        });

        bbtns.push('-');
        bbtns.push(WtfGlobal.getLocaleText("hrms.payroll.select.month")+': ');
        bbtns.push(this.decMonthCmb);

        this.decYearCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Year"),
            hiddenName: 'year',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:Wtf.yearStore,
            width:80,
            typeAhead:true,
            value:this.statutoryForm!=undefined?this.statutoryForm.yearCmb.getValue():new Date().getFullYear()
        });

        bbtns.push('-');
        bbtns.push(WtfGlobal.getLocaleText("hrms.payroll.select.year")+': ');
        bbtns.push(this.decYearCmb);

        return bbtns;

    },
    submit:function(){
    	if(this.formPanel.form.isValid()){
    		this.formPanel.getForm().submit({
                params:{
                    month : this.decMonthCmb.getValue(),
                    year : this.decYearCmb.getValue()
                },
                waitMsg:WtfGlobal.getLocaleText("hrms.common.saving.information"),
                success:function(){
    				Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.user.information.saved.successfully"));
    				this.ownerCt.remove(this);
    				if(this.statutoryForm!=undefined){
                    	this.statutoryForm.empGDS.reload();
                    }
                },
                failure:function(f,a){
                	Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.saving.user.information"));
                },
                scope:this
            });
    	}else{

    	}
    },
    setAmanahSahamNasional : function(data){

        this.companyid.setValue(data.companyid);
        this.paymentTypeCombo.setValue(data.asnpaymenttype);
        this.chqNo.setValue(data.asnchqno);
        this.adminCmb.setValue(data.asnpreparedby);

    },
    getAmanahSahamNasional : function(){

        var arr = [];

        this.companyid=new Wtf.form.Hidden({
            name:'companyid',
            id:'companyid',
            value:this.userdata!=undefined?this.userdata.companyid:""
        });
        arr.push(this.companyid);

        this.paymentType = new Wtf.data.SimpleStore({
    		id    : 'paymentType',
    		fields: ['id','name'],
    		data: [["1", WtfGlobal.getLocaleText("hrms.payroll.cash")],
    	           ["2", WtfGlobal.getLocaleText("hrms.payroll.cheque")]
    	          ]
    	});

        this.paymentTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.payment.type"),
            hiddenName: 'asnpaymenttype',
            name:'asnpaymenttype',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.paymentType,
            width:'85%',
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.asnpaymenttype:"",
            allowBlank:false
            
        });

    	arr.push(this.paymentTypeCombo);

        this.chqNo = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.cheque.no"),
            scope:this,
            name:"asnchqno",
            width:'75%',
            disabled:true,
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.asnchqno:""
        });
        arr.push(this.chqNo);
       
        this.adminCmb = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.prepared.by"),
            mode:'local',
            hiddenName :'asnpreparedby',
            name:'asnpreparedby',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.asnpreparedby:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmb);

        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_AmanahSahamNasional,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },

    setTabungHaji : function(data){

        this.tabunghajiPaymentTypeCombo.setValue(data.tabunghajipaymenttype);
        this.tabunghajiChqNo.setValue(data.tabunghajichqno);
        this.tabunghajiAdminCmb.setValue(data.tabunghajichqno);
        this.tabunghajiAdminCmb.setValue(data.tabunghajipreparedby);

    },
    getTabungHaji : function(){

        var arr = [];

        this.tabunghajiPaymentType = new Wtf.data.SimpleStore({
    		id    : 'paymentType',
    		fields: ['id','name'],
    		data: [["1", WtfGlobal.getLocaleText("hrms.payroll.cash")],
    	           ["2", WtfGlobal.getLocaleText("hrms.payroll.cheque")]
    	          ]
    	});

        this.tabunghajiPaymentTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.payment.type"),
            hiddenName: 'tabunghajipaymenttype',
            name:'tabunghajipaymenttype',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.tabunghajiPaymentType,
            width:'85%',
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.tabunghajipaymenttype:"",
            allowBlank:false
            
        });

    	arr.push(this.tabunghajiPaymentTypeCombo);

        this.tabunghajiChqNo = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.cheque.no"),
            scope:this,
            name:"tabunghajichqno",
            width:'75%',
            disabled:true,
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.tabunghajichqno:""
        });
        arr.push(this.tabunghajiChqNo);

        this.tabunghajiAdminCmb = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.prepared.by"),
            mode:'local',
            hiddenName :'tabunghajipreparedby',
            name:'tabunghajipreparedby',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.tabunghajipreparedby:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.tabunghajiAdminCmb);

        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_TabungHaji,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },

    setCP21 : function(data){

        this.empfilerefno.setValue(data.cp21employerfilerefno);
    
    },
    
    getCP21 : function(){
        var arr = [];

        this.empfilerefno = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employer.file.reference.no"),
            scope:this,
            width:200,
            name:"cp21employerfilerefno",
            value:this.userdata!=undefined?this.userdata.cp21employerfilerefno:""
        });
        arr.push(this.empfilerefno);

        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_CP21,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

        
    },

    setHRDLevy : function(data){

        this.hrdPaymentTypeCombo.setValue(data.hrdpaymenttype);
        this.hrdChqNo.setValue(data.hrdchqno);

    },

    getHRDLevy : function(){

        var arr = [];

        this.hrdPaymentType = new Wtf.data.SimpleStore({
    		id    : 'paymentType',
    		fields: ['id','name'],
    		data: [["1", WtfGlobal.getLocaleText("hrms.payroll.cash")],
    	           ["2", WtfGlobal.getLocaleText("hrms.payroll.cheque")]
    	          ]
    	});

        this.hrdPaymentTypeCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.payment.type"),
            hiddenName: 'hrdpaymenttype',
            name:'hrdpaymenttype',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.hrdPaymentType,
            width:'85%',
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.hrdpaymenttype:"",
            allowBlank:false

        });

    	arr.push(this.hrdPaymentTypeCombo);

        this.hrdChqNo = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.cheque.no"),
            scope:this,
            name:"hrdchqno",
            width:'75%',
            disabled:true,
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.hrdchqno:""
        });
        arr.push(this.hrdChqNo);

        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_HRDLevy,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },

    setPCB2 : function(data){

        this.branch.setValue(data.branch);
        this.employerno.setValue(data.employerno);
        this.adminCmbPCB2.setValue(data.personinchargepcb2);

    },
    
    getPCB2 : function(){

        var arr = [];
        
        this.branch = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.branch"),
            scope:this,
            name:"branch",
            width:'75%',
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.branch:""
        });
        arr.push(this.branch);

        this.employerno = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employer.number"),
            scope:this,
            name:"employerno",
            width:'75%',
            maxLength:10,
            value:this.userdata!=undefined?this.userdata.employerno:""
        });
        arr.push(this.employerno);
        
        this.adminCmbPCB2 = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_PCB2]}),
            mode:'local',
            hiddenName :'personinchargepcb2',
            name:'personinchargepcb2',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.personinchargepcb2:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbPCB2);
        

        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_PCB2,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },

    setTP1 : function(data){

        this.adminCmbTP1.setValue(data.personinchargetp1);
    },

    getTP1 : function(){

        var arr = [];

        this.adminCmbTP1 = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_TP1]}),
            mode:'local',
            hiddenName :'personinchargetp1',
            name:'personinchargetp1',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.personinchargetp1:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbTP1);


        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_TP1,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },
    setTP2 : function(data){

        this.adminCmbTP2.setValue(data.personinchargetp2);
    },
    getTP2 : function(){

        var arr = [];

        this.adminCmbTP2 = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_TP2]}),
            mode:'local',
            hiddenName :'personinchargetp2',
            name:'personinchargetp2',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.personinchargetp2:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbTP2);


        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_TP2,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },
    setTP3 : function(data){

        this.adminCmbTP3.setValue(data.personinchargetp3);
    },
    
    getTP3 : function(){

        var arr = [];

        this.adminCmbTP3 = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_TP3]}),
            mode:'local',
            hiddenName :'personinchargetp3',
            name:'personinchargetp3',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.personinchargetp3:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbTP3);


        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_TP3,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },
    setCP39 : function(data){

        this.adminCmbCP39.setValue(data.personinchargecp39);
    },
    
    getCP39 : function(){

        var arr = [];
        
        this.adminCmbCP39 = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_CP39]}),
            mode:'local',
            hiddenName :'personinchargecp39',
            name:'personinchargecp39',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.personinchargecp39:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbCP39);


        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_CP39,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },

    setCompanyDetails : function(data){

        this.companyOtherPhoneNumber.setValue(data.companyEmployerNumbeHeadQuarter);
    },

    getCompanyDetails : function(){

        var arr = [];
        
        this.companyOtherPhoneNumber = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employer.no.head.quarter"),
            scope:this,
            width:200,
            name:"companyEmployerNumbeHeadQuarter",
            value:this.userdata!=undefined?this.userdata.companyEmployerNumbeHeadQuarter:""
        });
        arr.push(this.companyOtherPhoneNumber);


        var comp = new Wtf.form.FieldSet({
            title:WtfGlobal.getLocaleText("hrms.common.company.details"),
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },
    
    setCP39A : function(data){

        this.adminCmbCP39A.setValue(data.personinchargecp39a);
    },
    getCP39A : function(){

        var arr = [];

        this.adminCmbCP39A = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_CP39]}),
            mode:'local',
            hiddenName :'personinchargecp39a',
            name:'personinchargecp39a',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.personinchargecp39a:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbCP39A);


        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_CP39A,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },
    setEA : function(data){

        this.adminCmbEA.setValue(data.adminCmbEA);
    },
    
    getEA : function(){

        var arr = [];

        this.adminCmbEA = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.adminStore,
            fieldLabel:WtfGlobal.getLocaleText({key:"hrms.common.person.in.charge",params:[Wtf.Malaysian_StatutoryForm_EA]}),
            mode:'local',
            hiddenName :'adminCmbEA',
            name:'adminCmbEA',
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',
            width:'85%',
            value:this.userdata!=undefined?this.userdata.adminCmbEA:"",
            typeAhead:true,
            allowBlank:false
        });

        arr.push(this.adminCmbEA);


        var comp = new Wtf.form.FieldSet({
            title:Wtf.Malaysian_StatutoryForm_EA,
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'85%',
			autoHeight:true,
            items:arr
        });

        return comp;
    }
});
