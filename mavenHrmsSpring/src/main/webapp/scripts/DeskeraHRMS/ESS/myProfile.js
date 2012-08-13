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
Wtf.myProfileWindow = function(config) {  
    Wtf.myProfileWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.myProfileWindow, Wtf.Panel, {
    initComponent: function() {
        Wtf.myProfileWindow.superclass.initComponent.call(this);
    },
    onRender: function(config){
        Wtf.myProfileWindow.superclass.onRender.call(this, config);
        var hidepayroll=true;
        if(Wtf.cmpPref.payrollbase=="Date"==false){
	        if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll)){
	            if(!WtfGlobal.EnableDisable(Wtf.UPerm.payroll, Wtf.Perm.payroll.view)){
	                hidepayroll=false;
	            }
	        }
        }
        
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
        
        this.costcenter = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.costcenter"),
            store:this.CostCenterStore,
            mode:'local',
            typeAhead:true,
            editable:true,
            valueField: 'id',
            hiddenName :'costcenter',
            displayField:'name',
            width:200,
            triggerAction:'all',
            forceSelection:true,
            emptyText:WtfGlobal.getLocaleText("hrms.common.select.cost.center"),
            disabled:this.editperm
        });
        this.CostCenterStore.load();
        
       this.frequencyStoreCmb = new Wtf.form.ComboBox({
    	   triggerAction:"all",
    	   fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.salary.frequency"),
    	   hiddenName: 'frequency',
    	   mode:"local",
    	   valueField:'id',
    	   displayField:'name',
    	   store:Wtf.frequencyStore,
    	   width:200,
    	   typeAhead:true,
    	   forceSelection:true,
    	   disabled:true
       });

        var profrecord=new Wtf.data.Record.create([
        {
            name:"fname"
        },{
            name:"middlename"
        },{
            name:"lname"
        },{
            name:"fullname"
        },{
            name:"dob"
        },{
            name:"gender"
        },{
            name:"image"
        },{
            name:"aboutuser"
        },

        {
            name:"marriage"
        },{
            name:"bloodgrp"
        },{
            name:"bankacc"
        },{
            name:"bankname"
        },{
            name:"bankbranch"
        },

        {
            name:"panno"
        },{
            name:"pfno"
        },{
            name:"drvlicense"
        },{
            name:"passportno"
        },{
            name:"exppassport"
        },

        {
            name:"fathername"
        },{
            name:"fatherdob"
        },{
            name:"mothername"
        },{
            name:"motherdob"
        },

        {
            name:"spousename"
        },{
            name:"spousedob"
        },{
            name:"child1name"
        },{
            name:"child1dob"
        },

        {
            name:"child2name"
        },{
            name:"child2dob"
        },{
            name:"mobno"
        },{
            name:"workno"
        },

        {
            name:"landno"
        }, {
            name:"workmail"
        }, {
            name:"othermail"
        }, {
            name:"presentaddr"
        },

        {
            name:"presentcity"
        },{
            name:"presentstate"
        },{
            name:"precountry"
        },{
            name:"permaddr"
        },{
            name:"permcity"
        },

        {
            name:"permstate"
        },{
            name:"permcountry"
        },{
            name:"mailaddr"
        },{
            name:"emgname"
        },{
            name:"emgreln"
        },

        {
            name:"emghome"
        },{
            name:"emgwork"
        },{
            name:"emgmob"
        },{
            name:"emgaddr"
        },{
            name:"empid"
        },

        {
            name:"department"
        },{
            name:"designationid"
        },{
            name:"costcenter"
        },{
            name:"frequency"
        },{
            name:"managername"
        },{
            name:"emptype"
        },{
            name:"joindate"
        },{
            name:"confirmdate"
        },{
            name:"relievedate"
        },{
            name:"trainingmon"
        },{
            name:"trainingyr"
        },{
            name:"probationyr"
        },

        {
            name:"probationmon"
        },{
            name:"noticeyr"
        },{
            name:"noticemon"
        },{
            name:"commid"
        },{
            name:"branchcode"
        },{
            name:"branchcity"
        },

        {
            name:"branchaddr"
        },{
            name:"brachcountry"
        },{
            name:"keyskills"
        },{
            name:"wkstarttime"
        },{
            name:"wkendtime"
        },

        {
            name:"weekoff"
        }
        ]);

        var profreader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },profrecord);


        this.profstore=new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url:"Common/getEmpProfile.common",
            reader:profreader,
            autoLoad:true,
            baseParams:{
                flag:52,
                userid:this.lid
//                grouper:'viewprofile',
//                firequery:'1'
            }
        });

        this.depCmb = new Wtf.form.ComboBox({
            store:Wtf.depStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.department") ,
            mode:'local',
            hiddenName:'department',
            name:'department',
            width:200,
            disabled:this.editperm,
            forceSelection: true,
            valueField: 'id',
            displayField:'name',
            typeAhead:true,
            triggerAction: 'all'            

        });

        if(!Wtf.StoreMgr.containsKey("dep")){
            Wtf.depStore.load();
            Wtf.StoreMgr.add("dep",Wtf.depStore)
        }


        this.desigCmb = new Wtf.form.ComboBox({
            store:Wtf.desigStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.designation"),
            mode:'local',
            hiddenName:'designationid',   
            name:'designationid',
            disabled:this.editperm,
            forceSelection: true,
            width:200,
            valueField: 'id',
            displayField:'name',
            triggerAction: 'all',
            typeAhead:true,
                    listeners:{
            scope:this,
            Select:function(combo,record){
                this.templateCmb.clearValue();
                this.templateStore.removeAll();
                this.templateStore.baseParams={
                    type:'getTemplistperDesign'
                };
                this.templateStore.load({
                    params:{
                        desigid:record.get('id')

            }
                });
        }
        }

        });

        if(!Wtf.StoreMgr.containsKey("desig")){
            Wtf.desigStore.load();
            Wtf.StoreMgr.add("desig",Wtf.desigStore)
        }

        this.managerCmb = new Wtf.form.ComboBox({
            displayField:'username',
            store:Wtf.reportingToStore,
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.ReportingTo"),
            mode:'local',
            hiddenName :'reportto',
            name:'managername',
            disabled:true,
            forceSelection: true,
            valueField: 'userid',
            triggerAction: 'all',        
            width:200,
            typeAhead:true
        });

        if(!Wtf.StoreMgr.containsKey("reportingto")){
            Wtf.reportingToStore.load();
            Wtf.StoreMgr.add("reportingto",Wtf.reportingToStore);
        }

        this.countrycombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PresentCountry"),
            width:200,
            store:  Wtf.countryStore,
            typeAhead:true,
            valueField: 'id',
            name: 'presentcountry',
            hiddenName:'presentcountry',
            displayField: 'name',
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all'
        });
        this.countrycombo1 = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PermanentCountry"),
            width:200,
            store:  Wtf.countryStore,
            typeAhead:true,
            name: 'permcountry',
            hiddenName:'permcountry',
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            disabled:this.blockemployeestoedit
        });
        this.branchcountrycombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BranchCountry"),
            width:200,
            store:  Wtf.countryStore,
            typeAhead:true,
            name: 'branchcountry',
            hiddenName:'branchcountry',
            valueField: 'id',
            disabled:this.editperm,
            forceSelection: true,
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all'

        });

        if(!Wtf.StoreMgr.containsKey("country")){
            Wtf.countryStore.load();
            Wtf.StoreMgr.add("country",Wtf.countryStore)
        }

        this.maritstore = new Wtf.data.SimpleStore({
            fields:['id','marit'],
            data: [ 
            ['1',WtfGlobal.getLocaleText("hrms.common.Single")],
            ['2',WtfGlobal.getLocaleText("hrms.common.Married")]
            ]
        });
        this.genderstore = new Wtf.data.SimpleStore({
            fields:['id','gender'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.common.Male")],
            ['2',WtfGlobal.getLocaleText("hrms.common.Female")]
            ]
        });

        this.relationstore = new Wtf.data.SimpleStore({
            fields:['id','relation'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.common.Brother")],
            ['2',WtfGlobal.getLocaleText("hrms.common.Father")],
            ['3',WtfGlobal.getLocaleText("hrms.common.Friend")],
            ['4',WtfGlobal.getLocaleText("hrms.common.Mother")],
            ['5',WtfGlobal.getLocaleText("hrms.common.Sister")],
            ['6',WtfGlobal.getLocaleText("hrms.common.Other")]
            ]
        });
        this.daystore = new Wtf.data.SimpleStore({
            fields:['id','weekday'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.Sunday")],
            ['2',WtfGlobal.getLocaleText("hrms.Monday")],
            ['3',WtfGlobal.getLocaleText("hrms.Tuesday")],
            ['4',WtfGlobal.getLocaleText("hrms.Wednesday")],
            ['5',WtfGlobal.getLocaleText("hrms.Thursday")],
            ['6',WtfGlobal.getLocaleText("hrms.Friday")],
            ['7',WtfGlobal.getLocaleText("hrms.Saturday")]
            ]
        });
        this.prdstore = new Wtf.data.SimpleStore({
            fields:['id'],
            data: [
            ['0'],
            ['1'],
            ['2'],
            ['3'],
            ['4'],
            ['5'],
            ['6'],
            ['7'],
            ['8'],
            ['9'],
            ['10'],
            ['11'],
            ['12']
            ]
        });

        this.jobtypestore = new Wtf.data.SimpleStore({
            fields:['id','jobtype'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.common.FullTime")],
            ['2',WtfGlobal.getLocaleText("hrms.common.PartTime")],
            ['3',WtfGlobal.getLocaleText("hrms.common.Contract")]
            ]
        });

        this.emptypestore = new Wtf.data.SimpleStore({
            fields:['id','type'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.common.Trainee")],
            ['2',WtfGlobal.getLocaleText("hrms.common.Contractual")],
            ['3',WtfGlobal.getLocaleText("hrms.common.Permanent")]
            ]
        });

        this.timestore = new Wtf.data.SimpleStore({
            fields:['id','type'],
            data: [
            ['1',WtfGlobal.getLocaleText("hrms.common.am")],
            ['2',WtfGlobal.getLocaleText("hrms.common.pm")]
            ]
        });

        this.dateofjoining = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.DateofJoining")+"*",
            format:"Y-m-d",
            name:'joindate',
            disabled:this.editperm,          
            width:200,
            allowBlank:false
        });
        this.dateofconfirm = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.DateofConfirmation")+"*",
            format:"Y-m-d",
            name:'confirmdate',
            disabled:this.editperm,
            width:200,
            allowBlank:false
        });
        this.dateofrelieve = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.DateofRelieving"),
            format:"Y-m-d",
            name:'relievedate',
            disabled:true,
            width:200
        });
        this.dob = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.DateofBirth"),
            format:"Y-m-d",
            name:'DoB',
            width:200,
            maxValue:new Date().clearTime(true),
            disabled:this.blockemployeestoedit
        });

        this.marState=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.MaritalStatus"),
            hiddenName:'marital',
            store:this.maritstore,
            displayField: 'marit',
            valueField:'id',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead:true,
            forceSelection: true,
            mode: 'local',
            width:200
        });
        this.gender=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.Gender"),
            hiddenName:'gender',
            store:this.genderstore,
            displayField: 'gender',
            valueField:'gender',
            forceSelection: true,
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead:true,
            mode: 'local',
            width:200,
            disabled:this.blockemployeestoedit
        });
      
        this.homeTele=new Wtf.form.NumberField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.LandlineNo"),
            name:'landno',
            width:200,
            maxLength:15,
            disabled:this.blockemployeestoedit
        });

        this.workTele=new Wtf.form.NumberField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.WorkTelephone"),
            name:'workno',
            width:200,
            maxLength:15,
            disabled:this.blockemployeestoedit
        });
        this.mobileno=new Wtf.form.NumberField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.MobileNo"),
            name:'mobno',
            width:200,
            maxLength:15,
            disabled:this.blockemployeestoedit            
        });

        this.emergencyTele=new Wtf.form.NumberField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.LandlineNo"),
            name:'emghome',
            width:200,
            maxLength:15
        });
        this.emergencyTele2=new Wtf.form.NumberField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.WorkContactNo"),
            name:'emgwork',
            width:200,
            maxLength:15
        });
        this.emergencyTele3=new Wtf.form.NumberField({         
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.MobileNo"),
            name:'emgmob',
            width:200,
            maxLength:15
        });

        this.emergencyname=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.EmergencyContactPersonName"),
            name:'emgname',
            width:200,
            maxLength:255
        });
        this.emergencyaddr=new Wtf.form.TextArea({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.EmergencyContactPersonAddress"),
            name:'emgaddr',
            height:35,
            width:200,
            maxLength:255
        });
        this.workEmail=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.WorkEmail"),
            name:'workmail',
            vtype:'email',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });

        this.otherEmail=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.OtherEmail"),
            vtype:'email',
            name:'othermail',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });
 
        this.empid=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.employee.id"),
            name:'empid',
            disabled:this.editperm,
//            readOnly:true,
            regex:/^[a-zA-Z]{1,}-{1}[0-9]{1,}$|^[a-zA-Z]{1,}-{1}[0-9]{1,}-{1}[a-zA-Z]{1,}$|^[0-9]{0,}$/,
            width:200,
            maxLength:255
        });
        this.fullname=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.FullName"),
            name:'fullname',
            readOnly:true,
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });
        this.userpicture=new Wtf.Panel({
            border: false,
            width:350,
            layout: 'column',
            items: [
                {
                    id: this.id+"userpicturepanel",
                    border:false,
                    style: "float:left;",
                    columnWidth: .4,
                    html:"<img id='"+this.id+"userpicture' height='80' width='80' alt='User Image' src=images/defaultuser.png?v="+Math.random()+"&userImg=true'/>"
                },
//                this.aboutUser = Wtf.Panel(
                {
                    id: this.id+"aboutUser",
                   // style: "float:right;",
                    border: false,
                    columnWidth: .6
                }
//            )
            ]
        });
        this.fname=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.FirstName")+"*",
            name:'fname',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit,
            allowBlank:false
        });
        this.lname=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.LastName")+"*",
            name:'lname',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit,
            allowBlank:false
        });
        this.mname=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.MiddleName"),
            name:'mname',
            enableKeyEvents:true,
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });
        this.bloodgrp=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BloodGroup"),
            name:'bloodgrp',
            width:200,
            maxLength:10,
            disabled:this.blockemployeestoedit
        });
        this.fathername=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.FatherName"),
            name:'fathername',
            width:200,
            maxLength:250,
            disabled:this.blockemployeestoedit
        });
        this.fatherdob = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.FatherDOB"),
            format:"Y-m-d",
            name:'fatherDoB',
            width:200,
            maxValue:new Date().clearTime(true),
            disabled:this.blockemployeestoedit
        });
        this.mothername=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.MotherName"),
            name:'mothername',
            width:200,
            maxLength:250,
            disabled:this.blockemployeestoedit
        });
        this.motherdob = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.MotherDOB"),
            format:"Y-m-d",
            name:'motherDoB',
            width:200,
            maxValue:new Date().clearTime(true),
            disabled:this.blockemployeestoedit
        });
        this.spousename=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.SpouseName"),
            name:'spousename',
            disabled:true,
            width:200,
            maxLength:250
        });
        this.spousedob = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.SpouseDOB"),
            format:"Y-m-d",
            name:'spouseDoB',
            disabled:true,           
            width:200,
            maxValue:new Date().clearTime(true)
        });
        this.childname=new Wtf.form.TextField({     
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.FirstChildName"),
            disabled:true,
            name:'child1name',
            width:200,
            maxLength:250
        });
        this.childdob = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.FirstChildDOB"),
            format:"Y-m-d",
            disabled:true,
            name:'childDoB1',
            width:200,
            maxValue:new Date().clearTime(true)
        });
        this.childname1=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.SecondChildName"),
            disabled:true,
            name:'child2name',
            width:200,
            maxLength:250
        });
        this.childdob1 = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.SecondChildDOB"),
            format:"Y-m-d",
            disabled:true,
            name:'childDoB2',
            width:200,
            maxValue:new Date().clearTime(true)
        });

        this.bankacc=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BankAc"),
            name:'bankacc',
            width:200,
            maxLength:20,
            disabled:this.blockemployeestoedit
        });
        this.bankname=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BankName"),
            name:'bankname',
            width:200,
            maxLength:250,
            disabled:this.blockemployeestoedit
        });
        this.bankbranch=new Wtf.form.TextArea({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BankBranch"),
            name:'bankbranch',
            width:200,
            maxLength:250,
            disabled:this.blockemployeestoedit
        });
        this.panno=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PANNo"),
            name:'panno',
            width:200,
            maxLength:20,
            disabled:this.blockemployeestoedit
        });
        this.pfno=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.EPFNo"),
            name:'pfno',
            width:200,
            maxLength:20,
            disabled:this.blockemployeestoedit
        });
        this.drivingli=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.DrivingLicenseNo"),
            name:'drvlicense',
            width:200,
            maxLength:20,
            disabled:this.blockemployeestoedit
        });
        this.passport=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PassportNo"),
            name:'passportno',
            width:200,
            maxLength:20,
            disabled:this.blockemployeestoedit
        });
        this.exppassport=new Wtf.form.DateField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.ExpiryDateofPassport"),
            format:"Y-m-d",
            name:'exppassport',
            width:200,
            disabled:this.blockemployeestoedit
        });
        this.city1=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PresentCity"),
            name:'presentcity',
            emptyText:'',
            width:200,
            maxLength:255
        });
        this.state1=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PresentState"),
            name:'presentstate',
            emptyText:'',
            width:200,
            maxLength:255
        });
        this.city2=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PermanentCity"),
            name:'permcity',
            emptyText:'',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });
        this.state2=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PermanentState"),
            name:'permstate',
            emptyText:'',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });
        this.addr=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PresentAddress"),
            name:'presentaddr',
            height:35,            
            width:200,
            maxLength:255
        });
        this.permaddr=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.PermanentAddress"),
            name:'permaddr',
            height:35,
            emptyText:'',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });
        this.mailaddr=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.MailingAddress"),
            name:'mailaddr',
            height:35,
            emptyText:'',
            width:200,
            maxLength:255,
            disabled:this.blockemployeestoedit
        });

        this.commid=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.CommunicationID"),
            name:'commid',
            width:200,
            disabled:this.editperm,
            maxLength:100
        });
        this.workbranch=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.WorkingBranchCode"),
            name:'branchcode',
            width:200,
            disabled:this.editperm,
            maxLength:20
        });
        this.workbranchaddr=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BranchAddress"),
            name:'branchaddr',
            width:200,
            disabled:this.editperm,
            maxLength:255
        });
        this.branchcity=new Wtf.form.TextField({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.BranchCity"),
            name:'branchcity',
            width:200,
            disabled:this.editperm,
            maxLength:255
        });

        this.keyskills=new Wtf.form.TextArea({            
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.KeySkills"),
            name:'keyskills',
            width:200,
            maxLength:1000
        });

        this.trainmon=new Wtf.form.ComboBox({            
            hiddenName:'trainingmon',
            store:this.prdstore,
            displayField: 'id',
            valueField:'id',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Months"),
            selectOnFocus:true,
            disabled:this.editperm,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            labelWidth:150,
            width:100,
            listWidth:100
        });
        this.trainyr=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.TrainingPeriod"),
            hiddenName:'trainingyr',
            store:this.prdstore,
            displayField: 'id',
            valueField:'id',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Years"),
            selectOnFocus:true,
            disabled:this.editperm,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            width:100,
            listWidth:100
        });
        this.probationmonth=new Wtf.form.ComboBox({            
            hiddenName:'probationmon',
            store:this.prdstore,
            displayField: 'id',
            valueField:'id',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Months"),
            selectOnFocus:true,
            triggerAction: 'all',
            disabled:this.editperm,
            editable: false,
            mode: 'local',
            labelWidth:150,
            width:100,
            listWidth:100
        });
        this.probationyear=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.ProbationPeriod"),
            hiddenName:'probationyr',
            store:this.prdstore,
            displayField: 'id',
            valueField:'id',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Years"),
            selectOnFocus:true,
            disabled:this.editperm,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            width:100,
            listWidth:100
        });
        this.noticemonth=new Wtf.form.ComboBox({
            hiddenName:'noticemon',
            store:this.prdstore,
            displayField: 'id',
            valueField:'id',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Months"),
            selectOnFocus:true,
            triggerAction: 'all',
            disabled:this.editperm,
            editable: false,
            mode: 'local',
            labelWidth:150,
            width:100,
            listWidth:100
        });
        this.noticeyear=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.NoticePeriod"),
            hiddenName:'noticeyr',
            store:this.prdstore,
            displayField: 'id',
            valueField:'id',
            emptyText:WtfGlobal.getLocaleText("hrms.common.Years"),
            disabled:this.editperm,
            selectOnFocus:true,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            width:100,
            listWidth:100
        });
        this.relnship=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.RelationwithEmergencyContactPerson"),
            hiddenName:'emgreln',
            store:this.relationstore,
            displayField: 'relation',
            valueField:'relation',
            selectOnFocus:true,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            width:200,
            listWidth:200
        });
        this.emptype=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.TypeofEmployment"),
            name:'emptype',            
            hiddenName:'emptype',
            store:this.emptypestore,
            displayField: 'type',
            valueField:'type',
            disabled:this.editperm,   
            selectOnFocus:true,
            triggerAction: 'all',
            editable: false,
            mode: 'local',
            width:200
        });
        this.MSComboconfig = {
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.WeeklyOff"),
            hiddenName:'weekoff',
            store:this.daystore,
            displayField: 'weekday',
            valueField:'weekday',
            selectOnFocus:true,
            triggerAction: 'all',
            typeAhead:true,
            mode: 'local',
            width:200,
            disabled:this.blockemployeestoedit
        },
        this.daycmp = new Wtf.common.Select(Wtf.applyIf({
            multiSelect:true,
            labelSeparator:'',
            forceSelection:false
        },this.MSComboconfig));
//        this.daycmp=new Wtf.form.ComboBox({
//            fieldLabel: 'Weekly Off',
//            hiddenName:'weekday',
//            store:this.daystore,
//            displayField: 'weekday',
//            valueField:'weekday',
//            selectOnFocus:true,
//            triggerAction: 'all',
//            typeAhead:true,
//            mode: 'local',
//            width:200
//        });

        this.acadGridRowSM=new Wtf.grid. CheckboxSelectionModel({
            singleSelect:false
        });

        this.workexGridRowSM=new Wtf.grid. CheckboxSelectionModel({
            singleSelect:false
        });

        this.qual=new Wtf.form.TextField({
            allowBlank:false,
            maxLength:100
        });
        this.lnst=new Wtf.form.TextField({
            name:'Institute',
            allowBlank:false,
            maxLength:255
        });
        this.yearofgrad=new Wtf.form.DateField({
            name:'yeargrd',
            allowBlank:false,
            format:'Y-m-d'
        });
        this.yearofgradfrom=new Wtf.form.DateField({
            name:'yeargrdfrm',
            allowBlank:false,
            format:'Y-m-d'
        });
        this.marks=new Wtf.form.TextField({
            name:'marks',
            allowBlank:false,
            maxLength:15
        });

        this.org=new Wtf.form.TextField({
            name:'organisation',
            allowBlank:false,
            maxLength:255
        });

        this.pos=new Wtf.form.TextField({
            name:'position',
            allowBlank:false,
            maxLength:255

        });
        this.beyr=new Wtf.form.DateField({
            name:'beginyear',
            allowBlank:false,
            format:'Y-m-d'            
        });
        this.endyr=new Wtf.form.DateField({
            name:'endyear',
            allowBlank:false,
            format:'Y-m-d'
        });
        this.comment=new Wtf.form.TextField({
            name:'comment',
            maxLength:255

        });
        this.pecomment=new Wtf.form.TextField({
            name:'pecomment',
            maxLength:255

        });
        this.peorg=new Wtf.form.TextField({
            name:'peorg',
            maxLength:255

        });
        this.pedesig=new Wtf.form.TextField({
            name:'pedesig',
            maxLength:255

        });
        this.pestartyr=new Wtf.form.DateField({
            name:'pestart',
            format:'Y-m-d'

        });
        this.peendyr=new Wtf.form.DateField({
            name:'peend',
            format:'Y-m-d'
        });
        this.starttime=new Wtf.form.TimeField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.StartTime"),
            name:'starttime',
            width:200,
            forceSelection: true,
            minValue:new Date(new Date().format("M d, Y")+" 8:00:00 AM"),
            maxValue:new Date(new Date().add(Date.DAY, 1).format("M d, Y")+" 7:45:00 AM"),
            value:"8:00 AM",
            disabled:this.blockemployeestoedit        
        });
        this.endtime=new Wtf.form.TimeField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.EndTime"),
            name:'endtime',
            width:200,
            forceSelection: true,
            minValue:new Date(new Date().format("M d, Y")+" 8:00:00 AM"),
            maxValue:new Date(new Date().add(Date.DAY, 1).format("M d, Y")+" 7:45:00 AM"),
            value:"8:00 AM",
            disabled:this.blockemployeestoedit
        });


       
        this.quacombo = new Wtf.form.ComboBox({
            store:  Wtf.quaStore,
            editable: false,
            name: 'qualification',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            allowBlank:false
        });

        if(!Wtf.StoreMgr.containsKey("qua")){
            Wtf.quaStore.load();
            Wtf.StoreMgr.add("qua",Wtf.quaStore)
        }

        this.training=new Wtf.Panel({
            width:515,            
            frame:false,
            border:false,
            layout:'column',            
            defaults:{
                labelWidth:150
            },
            items:[
            {
                columnWidth:.5,
                frame:false,
                border:false,
                layout:'form',                
                items:[this.trainyr]
            },
            {
                columnWidth:.5,
                frame:false,
                border:false,           
                items:[this.trainmon]

            }
            ]
        });

        this.probation=new Wtf.Panel({
            width:515,            
            frame:false,
            border:false,
            layout:'column',            
            defaults:{
                labelWidth:150
            },
            items:[
            {
                columnWidth:.5,
                frame:false,
                border:false,
                layout:'form',            
                items:[ this.probationyear]

            },
            {
                columnWidth:.5,
                frame:false,
                border:false,                
                items:[this.probationmonth]

            }
            ]
        });
        this.notice=new Wtf.Panel({
            width:515,            
            frame:false,
            border:false,
            layout:'column',            
            defaults:{
                labelWidth:150
            },
            items:[
            {
                columnWidth:.5,
                frame:false,
                border:false,
                layout:'form',                
                items:[ this.noticeyear]

            },
            {
                columnWidth:.5,
                frame:false,
                border:false,                
                items:[this.noticemonth]

            }
            ]
        });

        var profilebtns=new Array();
        this.subButton1= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.Save"),
            iconCls:getButtonIconCls(Wtf.btype.submitbutton),
            scope:this,
            handler: function() {
                var joindate=this.dateofjoining.getValue();
                var confirmdate=this.dateofconfirm.getValue();
                if(confirmdate<joindate && userroleid==1){
                    calMsgBoxShow(165,0);
                }else{
                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.save.data"),WtfGlobal.getLocaleText("hrms.common.want.to.save.changes"),function(btn){
                        if(btn!="yes") {
                            return;
                        }
                        this.saveprofile();
                    },this);
                }
            }
        });

        this.CncBtn= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.cancel"),
            iconCls:getButtonIconCls(Wtf.btype.cancelbutton),
            scope:this,
            handler:function(){
                this.displayFormValue();
                this.acadds.load();
                this.workds.load();                
            }
        });

        this.persninfo=new Wtf.form.FormPanel({
            baseParams:{
                flag:50,
                formname:'Personal',
                userid:this.lid
            },
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/saveempprofile.common",
            layout:'column',
            bodyStyle: "background:white ",
            border:false,
            //autoScroll:true,
            //iconCls:getTabIconCls(Wtf.etype.hrmspersonaldata),
            items:[
            {
                columnWidth:0.5,
                style:'margin-top:1%;margin-left:3%',
                layout:'form',
                autoHeight:true,
                border:false,
                defaults:{
                    labelWidth:150
                },
                items:[
                {
                    xtype:'fieldset',
                    title:WtfGlobal.getLocaleText("hrms.common.PersonalDetails"),
                    width:'75%',
                    height:'30%',
                    style:'padding-left:50px;padding-bottom:20px;padding-top:10px',
                    items:[
                    this.userpicture,this.fname,this.mname,this.lname,this.fullname,this.dob,this.gender,this.marState,
                    this.bloodgrp
                    ]
                },
                {
                    xtype:'fieldset',
                    title:WtfGlobal.getLocaleText("hrms.common.OtherDetails"),
                    width:'75%',
                    height:'30%',
                    style:'padding-left:50px;padding-bottom:20px;padding-top:10px',
                    items:[
                    this.bankacc, this.bankname,this.bankbranch, this.panno,this.pfno,this.drivingli,this.passport,
                    this.exppassport
                    ]
                }
                ]
            },this.rightperpan= new Wtf.Panel(
            {
                columnWidth:0.45,
                border:false,
                style:'margin-top:1%',
                layout:'form',
                autoHeight:true,
                defaults:{
                    labelWidth:150
                },
                items:[
                {
                    xtype:'fieldset',
                    width:'75%',
                    height:'30%',
                    title:WtfGlobal.getLocaleText("hrms.common.DependentDetails"),
                    style:'padding-left:50px;padding-bottom:20px;padding-top:10px',
                    items:[
                    this.fathername,this.fatherdob,this.mothername,this.motherdob,this.spousename,this.spousedob,
                    this.childname, this.childdob,this.childname1,this.childdob1
                    ]

                },
                {
                    xtype:'fieldset',
                    width:'75%',
                    height:'25%',
                    title:WtfGlobal.getLocaleText("hrms.common.KeySkills"),
                    style:'padding-left:50px;padding-bottom:20px;padding-top:10px',
                    items:[
                    this.keyskills
                    ]
                }
                ]
            })]
        });
        
        this.attrPanel = new Wtf.attributeComponent({
            widthValue: 200,
            configType:"Personal",
            layout:'form',
            border: false,
            refid:this.lid,
            grouper:'viewprofile',
            chk:1
        });
//        if(this.attrPanel.items){
            this.rightperpan.add(this.attrCont=new Wtf.Panel({
                border:false,
                items:[{
                    xtype:'fieldset',
                    title:WtfGlobal.getLocaleText("hrms.common.ExtraDetails"),
                    border:false,
                    width:'75%',
                    height:'25%',
                    style:'padding-left:50px;padding-bottom:20px;padding-top:10px',
                    items:[
                    this.attrPanel
                    ]
                }]
            }));
//        }
        this.attrPanel.on('closeform',function(){
            if(this.attrPanel.items.length==0){
                this.rightperpan.remove(this.attrCont);
            }
            this.rightperpan.doLayout();
        },this)
        this.contactspnl=new Wtf.form.FormPanel({
            baseParams:{
                flag:50,
                formname:'Contact',
                userid:this.lid
            },
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/saveempprofile.common",
            layout:'column',
            bodyStyle: "background:white ",
            border:false,
            //autoScroll:true,
            //iconCls:getTabIconCls(Wtf.etype.hrmscontactinfo),
            items:[this.leftconpan= new Wtf.Panel(
            {
                columnWidth:0.5,
                width:'50%',
                autoHeight:true,
                style:'margin-top:1%;margin-left:3%',
                layout:'form',
                defaults:{
                    labelWidth:150
                },
                border:false,
                items:[{
                    xtype:'fieldset',
                    title:WtfGlobal.getLocaleText("hrms.common.ContactDetails"),
                    width:'75%',
                    height:500,
                    style:'padding-left:50px;padding-bottom:20px;padding-top:25px',
                    items:[
                    this.mobileno,this.workTele,this.homeTele,
                    this.workEmail,this.otherEmail,this.addr,this.city1, this.state1,this.countrycombo,
                    this.permaddr,this.city2,this.state2,
                    this.countrycombo1,this.mailaddr
                    ]
                }
                ]
            }),{
                columnWidth:0.45,
                border:false,
                autoHeight:true,
                style:'margin-top:1%',
                layout:'form',
                defaults:{
                    labelWidth:150
                },
                items:[{
                    xtype:'fieldset',
                    width:'75%',
                    height:300,
                    title:WtfGlobal.getLocaleText("hrms.common.EmergencyContactDetails"),
                    style:'padding-left:50px;padding-bottom:20px;padding-top:25px',
                    items:[
                    this.emergencyname,this.relnship,this.emergencyTele,this.emergencyTele2, this.emergencyTele3,
                    this.emergencyaddr
                    ]
                },
                {
                    xtype:'fieldset',
                    width:'75%',
                    height:180,
                    title:WtfGlobal.getLocaleText("hrms.common.WorkShiftDetails"),
                    style:'padding-left:50px;padding-top:25px',
                    items:[
                    this.starttime,this.endtime,this.daycmp
                    ]
                }]
            }
            ]
        });
        this.attrPanel1 = new Wtf.attributeComponent({
            widthValue: 200,
            configType:"Contact",
            layout:'form',
            border: false,
            refid:this.lid,
            chk:1
        });
        this.leftconpan.add(this.attrCont1=new Wtf.Panel({
            border:false,
            items:[{
                xtype:'fieldset',
                border:false,
                title:WtfGlobal.getLocaleText("hrms.common.ExtraDetails"),
                width:'75%',
                height:'25%',
                style:'padding-left:50px;padding-bottom:20px;padding-top:10px',
                items:[
                this.attrPanel1
                ]
            }]

        }));
        this.attrPanel1.on('closeform',function(){
            if(this.attrPanel1.items.length==0){
                this.leftconpan.remove(this.attrCont1);
            }
            this.leftconpan.doLayout();
        },this)
        this.empdelbtn=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            tooltip:{
                title: WtfGlobal.getLocaleText("hrms.common.DeleteRecord"),
                text: WtfGlobal.getLocaleText("hrms.common.Clicktodeleteselectedrecord")
            },
            disabled:true,
            handler: function () {
                this. empGridDel();
            },
            scope: this
        });
        var empgrid="";
        var empgridbuttons=new Array();
        empgrid =Wtf.grid.EditorGridPanel;
        empgridbuttons.push(this.empdelbtn);
            
        this.EmpGridRowSM=new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

         var fieldsrec =  Wtf.data.Record.create([
        {
            name:'templatename'
        },{
            name:'payinterval'
        },{
            name:'templateid'
        },{
            name:'salaryflag'
        },{
            name:'basic'
        },{
            name:'effectivedate',
            type:'date',
            dateFormat:'Y-m-d'
            
            
        }
        ])

        var templatedatareader=new Wtf.data.KwlJsonReader({
            root:'data'
        },fieldsrec);

        this.assignedTemplatestore = new Wtf.data.Store({
            url: "Payroll/Template/getAssignedTemplateForEmp.py",
            method:'GET',
            reader: templatedatareader

        });
        var stemplateCM = new Wtf.grid.ColumnModel([
        {
            header:WtfGlobal.getLocaleText("hrms.CampaignDetail.TemplateName"),
            dataIndex:'templatename'

        },{
            header:WtfGlobal.getLocaleText("hrms.common.EffectiveFrom"),
            dataIndex:'effectivedate',
            sortable:true,
            renderer:WtfGlobal.onlyDateRenderer
        },{
            header:WtfGlobal.getLocaleText({key:"hrms.common.BasicSalarysym",params:[WtfGlobal.getCurrencySymbol()]}),
            dataIndex:'basic',
            align: 'right'

        },{
            header:WtfGlobal.getLocaleText("hrms.payroll.payinterval"),
            dataIndex:'payinterval',
            renderer:function(val){
                    var result = " - ";
                    switch(val){
                        case 1:
                            result = WtfGlobal.getLocaleText("hrms.payroll.onceamonth");
                            break;
                        case 2:
                            result = WtfGlobal.getLocaleText("hrms.payroll.twiceamonth");
                            break;

                        case 3:
                            result =WtfGlobal.getLocaleText("hrms.payroll.onceaweek");
                            break;
                    }

                    return result;
                }

        },{
            header:WtfGlobal.getLocaleText("hrms.common.delete"),
            width: 40,
            renderer:this.deleteRenderer.createDelegate(this)
        }
        ]);
       this.salaryTemplateGrid = new Wtf.grid.GridPanel({
               store:this.assignedTemplatestore,
               cm:stemplateCM,
             //  width:'99%',
               border: false,
               height:500,
               viewConfig:{
                   forceFit:true
               }

           
       })
           this.templaterec=new Wtf.data.Record.create([
    {
        name:'name'
    },
    {
        name:'templateid'
    }
    ]);
    this.templateStore =  new Wtf.data.Store({
        url: Wtf.req.base + "PayrollHandler.jsp" ,
        baseParams:{
            type:'getTemplistperDesign'
        },
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        },this.templaterec),
        autoLoad : false
    });

    this.templateStore.on("load",function(){
        if(this.templateStore.getCount()==0){
            this.templateCmb.emptyText=WtfGlobal.getLocaleText("hrms.common.Notemplateassignedforselecteddesignation");
            this.templateCmb.reset();
        } else {
            var row = this.templateStore.find("id", this);
            if(row != -1) {
                this.templateCmb.setValue(config.record.data.templateid);
            } else {
                this.templateCmb.emptyText=WtfGlobal.getLocaleText("hrms.common.SelectTemplate");
                this.templateCmb.reset();
            }
        }
    },this);

    this.templateStore.on("loadexception",function(){
        //this.templateCmb.emptyText="No template assigned for selected designation";
        this.templateCmb.reset();
    },this);
    this.templateStore.load();
//    this.templateStore.load({
//        params:{
//            desigid:config.record.get('designationid')
//        }
//    });

    this.templateCmb = new Wtf.form.ComboBox({
        store:this.templateStore,
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.Salarytemplate"),
        mode:'local',
        hiddenName:'templateid',
        name:'templateid',
       // allowBlank: false,
        width:140,
        emptyText: WtfGlobal.getLocaleText("hrms.common.SelectTemplate"),
        valueField: 'templateid',
        displayField:'name',
        forceSelection: true,
        triggerAction: 'all',
        typeAhead:true
    });

    this.effectiveFromdtpicker = new Wtf.form.DateField({
        format:'m-d-Y',
       // allowBlank: false,
        emptyText: WtfGlobal.getLocaleText("hrms.common.EffectiveFrom")
    });
    this.basicSal = new Wtf.form.NumberField({
            width: 100,
            emptyText:WtfGlobal.getLocaleText("hrms.common.BasicSalary"),
            maxLength: 10,
            allowNegative:false
        });
    this.assignTemplateBtn = new Wtf.Button({
        text:WtfGlobal.getLocaleText("hrms.common.AssignTemplate"),
        id:'btnsavetemp',
        handler:function(){
//           var saveelement = Wtf.get('btnsavetemp');
//           saveelement.findParent('table').className = "x-btn-wrap x-btn x-item-disabled";
            if(this.effectiveFromdtpicker.getValue() != "" && this.templateCmb.getValue() != "" && this.basicSal.getValue() !="" && this.effectiveFromdtpicker.getValue()>=this.dateofjoining.getValue()){
                var recindex = this.assignedTemplatestore.find("effectivedate",this.effectiveFromdtpicker.getValue());
                if(recindex == -1 ){
                    this.assignTemplateBtn.disable();
                    Wtf.Ajax.requestEx({
                        url:'Payroll/Template/assignTemplateToUser.py',
                        method:'POST',
                        params:{
                            templateid:this.templateCmb.getValue(),
                            effectivedate:this.effectiveFromdtpicker.getValue().format("Y-m-d"),
                            basicsal: this.basicSal.getValue(),
                            userid:this.lid,
                            mode:"add"
                        }
                    },this, function(response){
                                if(!response.success) {
                                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),response.msg], 2);
                                } else {
                                    this.assignedTemplatestore.load({params:{userid:this.lid}});
                                    this.templateCmb.reset();
                                    this.effectiveFromdtpicker.reset();
                                    this.basicSal.setValue(0);
                                    this.assignTemplateBtn.enable();
                                }
                    }, function(response){
                        this.assignTemplateBtn.enable();
                    })
                }else{
                    Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.Dateselectionerror"),WtfGlobal.getLocaleText("hrms.common.Dateselectionerror.Message"));
                }
            } else {
            	if(this.effectiveFromdtpicker.getValue()<this.dateofjoining.getValue())
            	{
            		calMsgBoxShow(191, 2);
            	}else{
            		if(this.basicSal.getValue() ==""){
                		this.basicSal.markInvalid();
                	}
                    calMsgBoxShow(28, 2);
            	}
            }
            this.assignTemplateBtn.enable();
        },
        scope:this

    })
        this.orgpanel=new Wtf.form.FormPanel({
            baseParams:{
                flag:50,
                formname:'Organizational',
                userid:this.lid
            },
//            url: Wtf.req.base + "hrms.jsp",
            url:"Common/saveempprofile.common",
            layout:'column',
            bodyStyle: "background:white ",
            border:false,
            //autoScroll:true,
            //iconCls:getTabIconCls(Wtf.etype.hrmsorganization),
            items:[{
                columnWidth:0.49,
                style:'margin-top:1%;margin-left:3%',
                width:'50%',
                layout:'form',
                autoHeight:true,
                border:false,
                defaults:{
                    labelWidth:150
                },
                items:[
                {
                    xtype:'fieldset',
                    title:WtfGlobal.getLocaleText("hrms.common.OrganizationalDetails"),
                    width:'75%',
                    height:560,
                    style:'padding-left:50px;padding-bottom:20px;padding-top:25px',
                    items:[
                    this.empid, this.depCmb , this.desigCmb ,this.costcenter, this.frequencyStoreCmb, this.managerCmb,this.emptype,
                    this.commid,this.workbranch,this.workbranchaddr,this.branchcity,
                    this.branchcountrycombo, this.dateofjoining, this.dateofconfirm,
                    this.dateofrelieve,this.training,this.probation,this.notice
                    ]
                }]
            },this.rightorgpan= new Wtf.Panel(
                {
                    columnWidth:0.48,
                    border:false,
                    //hidden:this.editperm,
                    style:'margin-top:1%;',
                    layout:'form',
                    autoHeight:true,
                    defaults:{
                        labelWidth:150
                    },items:[{
                        xtype:'fieldset',
                        title:WtfGlobal.getLocaleText("hrms.common.SalaryTemplates"),
                        width:'90%',
                        hidden:hidepayroll,
                       // hidden:(this.exemp)?true:false,
                        height:500,
                        layout: 'border',
                        //style:'padding-left:50px;padding-right:50px;padding-bottom:20px;padding-top:25px',
                        style:'padding-bottom:20px;padding-top:25px',
                        items:[  {layout:'column',bodyStyle:'background-color:#FFFFFF;',region:'north',height:50,border:false,items:[{columnWidth:0.32,border:false,items:[this.templateCmb]},{columnWidth:0.23,border:false,items:[this.basicSal]},{columnWidth:0.24,border:false,items:[this.effectiveFromdtpicker]},{columnWidth:0.20,border:false,items:[this.assignTemplateBtn]}]},{region:'center',bodyStyle:'background-color:#FFFFFF;',layout:'fit',items:[this.salaryTemplateGrid]}]}]
                    })
            ]
        });

        this.assignedTemplatestore.load({params:{userid:this.lid,grouper:'viewprofile'}});
         this.salaryTemplateGrid.on("rowclick",function(grid,rowindex,event){
            if(event.getTarget("div[class='pwndCommon gridCancel']")){
            	Wtf.MessageBox.show({
                    title: WtfGlobal.getLocaleText("hrms.common.confirm"),
                    msg:deleteMsgBox('template'),
                    buttons:Wtf.MessageBox.YESNO,
                    icon:Wtf.MessageBox.QUESTION,
                    scope:this,
                    fn:function(button){
                        if(button=='yes'){
                        	calMsgBoxShow(201,4,true);
                var rec = grid.store.getAt(rowindex);
             
                 var currentdate = new Date();
                if(rec.get("salaryflag") == "0"){
                       
                  Wtf.Ajax.requestEx({
                url:'Payroll/Template/assignTemplateToUser.py',
                method:'POST',
                params:{
                    templateid:rec.get("templateid"),
                    effectivedate:rec.get("effectivedate").format("Y-m-d"),
                    userid:this.lid,
                    mode:"delete"
                }
            },this, function(response){
                if(response.msg == "invalid"){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.deletePrevGenSalforSamePayCycle")],2);
                } else {
                    grid.store.remove(rec);
                    this.assignedTemplatestore.load({params:{userid:this.lid}});
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.common.Templatedeletedsuccessfully")],0);
                }
                grid.store.remove(rec);
                this.assignedTemplatestore.load({params:{userid:this.lid}});
            }, function(response){

            })
                } else {
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.SalalreadyGenCantUnassignEmployee")], 2);
                }
               }
              }
            });
            }
        },this);
        
        this.attrPanel2 = new Wtf.attributeComponent({
            widthValue: 200,
            configType:"Organizational",
            layout:'form',
            border: false,
            refid:this.lid,
            chk:1
        });
        this.rightorgpan.add(this.attrCont2=new Wtf.Panel({
            border:false,
            items:[{
                xtype:'fieldset',
                border:false,
                title:WtfGlobal.getLocaleText("hrms.common.ExtraDetails"),
                width:'84%',
                height:'25%',
                style:'padding-left:50px;padding-bottom:20px;padding-top:25px',
                items:[
                this.attrPanel2
                ]
            }]

        }));
        this.attrPanel2.on('closeform',function(){
            if(this.attrPanel2.items.length==0){
                this.rightorgpan.remove(this.attrCont2);
            }
            this.rightorgpan.doLayout();
        },this)

        this.work = new Wtf.data.Record.create([
        {
            name: 'id'
        },

        {
            name: 'organisation'
        },

        {
            name: 'position'
        },

        {
            name: 'beginyear',
            type:'date'
        },

        {
            name: 'endyear',
            type:'date'
        },

        {
            name: 'comment'
        }
        ]);
        this.workds = new Wtf.data.Store({
            method: 'POST',
            pruneModifiedRecords:true,
            baseParams: {
                flag: 51,
                userid:this.lid,
                type:'work'
            },
            url: Wtf.req.base + "hrms.jsp",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data'
            }, this.work)
        });
        this.workcm = new Wtf.grid.ColumnModel([
            this.workexGridRowSM,
            {
                header:WtfGlobal.getLocaleText("hrms.common.Organization"),
                dataIndex: 'organisation',
                editor: this.org
            },{
                header:WtfGlobal.getLocaleText("hrms.common.Position"),
                dataIndex: 'position',
                editor:  this.pos
            },{
                header:WtfGlobal.getLocaleText("hrms.common.start.date"),
                dataIndex: 'beginyear',
                editor: this.beyr,
                renderer:WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("hrms.common.end.date"),
                dataIndex: 'endyear',
                editor: this.endyr,
                renderer:WtfGlobal.onlyDateRenderer
            },
            {
                header:WtfGlobal.getLocaleText("hrms.common.Comment"),
                dataIndex: 'comment',
                editor: this.comment
            }]);
        this.acad = new Wtf.data.Record.create([
        {
            name: 'id'
        },

        {
            name: 'qualification'
        },

        {
            name: 'institution'
        },

        {
            name: 'gradyear',
            type:'date'
        },

        {
            name: 'marks'
        },
        {
            name:'qualificationin'
        },
        {
            name:'yeargrdfrm',
            type:'date'
        }
        ]);
        this.acadds = new Wtf.data.Store({
            method: 'POST',
            pruneModifiedRecords:true,
            baseParams: {
                flag:51,
                userid:this.lid,
                type:'acad'
            },
            url: Wtf.req.base + 'hrms.jsp',
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data'
            }, this.acad)
        });
        this.acadcm = new Wtf.grid.ColumnModel([
            this.acadGridRowSM,
            {
                header: WtfGlobal.getLocaleText("hrms.common.Qualification"),
                dataIndex: 'qualification',
                editor:  this.quacombo
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.Specialization"),
                dataIndex:'qualificationin',
                editor:  this.qual
            },{
                header:WtfGlobal.getLocaleText("hrms.common.Institution"),
                dataIndex: 'institution',
                editor:  this.lnst
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),
                dataIndex: 'yeargrdfrm',
                editor:  this.yearofgradfrom,
                renderer:WtfGlobal.onlyDateRenderer
            },{
                header: WtfGlobal.getLocaleText("hrms.common.end.date"),
                dataIndex: 'gradyear',
                editor:  this.yearofgrad,
                renderer:WtfGlobal.onlyDateRenderer
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.Marks.Grade"),
                dataIndex: 'marks',
                editor: new Wtf.form.TextField({
                    name:'marks',
                    allowBlank:false
                })
            }
            ]);
        var btnsEQDetails=new Array(
        		this.addAcadBtn = new Wtf.Toolbar.Button({
        			text: WtfGlobal.getLocaleText("hrms.common.add"),
        			iconCls:getButtonIconCls(Wtf.btype.addbutton),
        			tooltip:{
        			title:  WtfGlobal.getLocaleText("hrms.common.AddRecord"),
        			text:  WtfGlobal.getLocaleText("hrms.common.Clicktoaddrecord")
        			},
        			handler: function () {
        				this.acadrec();
        			},
        			scope: this
        		}),'-',
        		this.delAcadBtn = new Wtf.Toolbar.Button({
        			text: WtfGlobal.getLocaleText("hrms.common.delete"),
        			iconCls:getButtonIconCls(Wtf.btype.deletebutton),
        			tooltip:{
        				title:  WtfGlobal.getLocaleText("hrms.common.DeleteRecord"),
        				text:  WtfGlobal.getLocaleText("hrms.common.Clicktodeleteselectedrecord")
        			},
        			id: 'BtnAcadDel' + this.id,
        			disabled:true,
        			handler: function () {
        				this.acadGridDel();
        			},
        			scope: this
        		})
        );
        var btnsEmpDetails=new Array(
        		this.addWorkexBtn = new Wtf.Toolbar.Button({
                    text: WtfGlobal.getLocaleText("hrms.common.add"),
                    iconCls:getButtonIconCls(Wtf.btype.addbutton),
                    tooltip:  WtfGlobal.getLocaleText("hrms.common.Clicktoaddrecord"),
                    handler: function () {
        				this.workrec();
                    },
                    scope: this
                }),'-',
        		this.delWorkexBtn = new Wtf.Toolbar.Button({
                    text: WtfGlobal.getLocaleText("hrms.common.delete"),
                    iconCls:getButtonIconCls(Wtf.btype.deletebutton),
                    tooltip:WtfGlobal.getLocaleText("hrms.common.Clicktodeleteselectedrecord"),
                    id: 'BtnWorkexDel' + this.id,
                    disabled:true,
                    handler: function () {
                        this.workexGridDel();
                    },
                    scope: this
                })
        );
        this.expPanel = new Wtf.Panel({
            //frame:true,
            border: false,
            title:WtfGlobal.getLocaleText("hrms.common.QualificationandEmploymentDetails"),
            layout:'border',
            bodyStyle:"background:white;",
            iconCls:getTabIconCls(Wtf.etype.hrmsqualification),
            items:[
            {
                region: 'west',
                split:true,
                width:'50%',
                frame:true,
                style:"padding:20px 0px;",
                title:WtfGlobal.getLocaleText("hrms.common.EmployeeQualification"),
                layout: 'fit',
                border: false,
                tbar: btnsEQDetails,
                items: [
                this.acadGrid = new Wtf.grid.EditorGridPanel({
                    border:false,
                    autoDestroy:true,
                    ds: this.acadds,
                    cm: this.acadcm,
                    sm: this.acadGridRowSM,
                    clicksToEdit :1,
                    viewConfig: {
                        forceFit: true
                    }
                })
                ]
            },
            {
                region: 'center',
                title:WtfGlobal.getLocaleText("hrms.common.EmploymentDetails"),
                border: false,
                frame:true,
                style:"padding:20px 0px;",
                layout:'fit',
                labelWidth:136,
                items:[
                this.workGrid = new Wtf.grid.EditorGridPanel({
                    border:false,
                    autoDestroy:true,
                    ds: this.workds,
                    cm: this.workcm,
                    sm: this.workexGridRowSM,
                    clicksToEdit :1,
                    viewConfig: {
                        forceFit: true
                    }
                })
                ],
                tbar:btnsEmpDetails
            }
            ]

        });


        profilebtns.push('->');
        profilebtns.push(this.subButton1);
        this.docpanel=new Wtf.document_panel({
            title:WtfGlobal.getLocaleText("hrms.common.documents"),
            layout:'fit',
            border:false,
            lid:this.lid,
            manager:this.manager,
            id:'filepanel'+this.id,
            iconCls:getTabIconCls(Wtf.etype.hrmsdocument)
        });
        var myfinalreport=1;
        if(this.report){ 
            myfinalreport=0;
        }
         var finalreport="";
         if(Wtf.cmpPref.annmng){
            finalreport=Wtf.AppraisalReport;
        }else{
            finalreport=Wtf.finalReport;
        }
        this.appraisalPanel=new finalreport({
            id:"myfinalreport",
            title:"<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.common.AppraisalReport.tooltip")+"\">"+WtfGlobal.getLocaleText("hrms.common.AppraisalReport")+"</div>",
            layout:'fit',
            border:false,
            reviewappraisal:false,
            myfinalReport:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsreport)
        });
        if(Wtf.cmpPref.payrollbase=="Date"){
        	this.salarypanel=new Wtf.MyPayslip({
                id:this.id+"myPayslipEmpProfile",
                layout: 'fit',
                title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.my.payslip.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.my.payslip")+"</div>",
                border: false,
                selectedUserID:this.lid,
                iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip)
            });
        }else{
        this.salarypanel=new Wtf.viewmypayslip({
            id:this.id+"viewmypayslip",
            layout: 'fit',
            title: WtfGlobal.getLocaleText("hrms.common.Payslips"),
            profile:true,
            userid:this.lid,
            border: false,            
            iconCls:getTabIconCls(Wtf.etype.hrmsreport)
        });
        }
        
        this.tabPanel=new Wtf.TabPanel({
            activeTab:0,
            border:false,
            items:[this.persninfopan=new Wtf.Panel({
                border:false,
                title:WtfGlobal.getLocaleText("hrms.common.PersonalDetails"),
                autoScroll:true,
                iconCls:getTabIconCls(Wtf.etype.hrmspersonaldata)
            }),this.contactspnlpan=new Wtf.Panel({
                border:false,
                title:WtfGlobal.getLocaleText("hrms.common.ContactandWorkShiftDetails"),
                autoScroll:true,
                iconCls:getTabIconCls(Wtf.etype.hrmscontactinfo)
            }),this.orgpanelpan=new Wtf.Panel({
                border:false,
                title:WtfGlobal.getLocaleText("hrms.common.OrganizationalDetails"),
                autoScroll:true,
                iconCls:getTabIconCls(Wtf.etype.hrmsorganization)
            }),this.expPanel
            ,this.docpanel
            ],
            bbar:profilebtns

        });
        if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.appraisal)){
        if(!this.manager) {
            this.tabPanel.add(this.appraisalPanel);
        }
        }
        if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll)){
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.payroll, Wtf.Perm.payroll.view)) {
                this.tabPanel.add(this.salarypanel);
            }
        }
        this.add(this.tabPanel);

        this.tabPanel.doLayout();
        this.tabPanel.on("beforetabchange",function(tabp,tab){
            if(tab.id=='filepanel'+this.id||tab.id=='myfinalreport'||tab.id==this.id+"viewmypayslip"||tab.id==this.id+"myPayslipEmpProfile"){
                  this.tabPanel.getBottomToolbar().setVisible(false);
            }
            else{
                this.tabPanel.getBottomToolbar().setVisible(true);
            }
             this.tabPanel.syncSize();

        },this);
        this.persninfopan.on('activate',function(){
            this.persninfopan.add(this.persninfo);
            this.subButton1.setDisabled(false);
            this.persninfopan.doLayout();
        },this);
        this.orgpanelpan.on('activate',function(){
            this.orgpanelpan.add(this.orgpanel);
            /*if(this.editperm){
                this.subButton1.setDisabled(true);
            }*/
//            this.orgpanelpan.doLayout();
//            this.tabPanel.doLayout();
            this.doLayout();
        },this);
        var storeflag=false;
        this.expPanel.on('activate',function(){
            this.subButton1.setDisabled(false);
            if(!storeflag){
                this.acadds.load();
                this.workds.load();
            }
            storeflag=true;
            this.doLayout();
        },this);
        this.contactspnlpan.on('activate',function(){
            this.subButton1.setDisabled(false);
            this.contactspnlpan.add(this.contactspnl);
            this.doLayout();
        },this);
        this.docpanel.on('activate',function(){
            this.doLayout();
        },this);
        this.appraisalPanel.on('activate',function(){
            this.doLayout();
        },this);
        this.salarypanel.on('activate',function(){
            this.doLayout();
        },this);                         
        this.marState.on('select',this.enableMarstatus,this);           
       // this.profstore.load();
        this.profstore.on("load",this.displayFormValue,this);       
        this.acadds.on("load",this.acadrec,this);
        this.workds.on("load",this.workrec,this);       
        this.mname.on('blur',this.setfullname,this);
        this.fname.on('blur',this.setfullname,this);
        this.lname.on('blur',this.setfullname,this);
        this.acadGrid.on("afteredit",this.fillacadGridValue,this);
        this.workGrid .on("afteredit",this.fillworkGridValue,this);        
        this.workGrid .on("validateedit",this.validate,this);
        this.acadGrid .on("validateedit",this.validateacad,this);           
        this.workexGridRowSM.on("selectionchange",function (){
            if(this.workexGridRowSM.hasSelection()){
                this.delWorkexBtn.enable();
            } else {
                this.delWorkexBtn.disable();
            }
        },this);
        this.acadGridRowSM.on("selectionchange",function (){
            if(this.acadGridRowSM.hasSelection()){
                this.delAcadBtn.enable();
            } else {
                this.delAcadBtn.disable();
            }
        },this);      
    },
    acadrec:function(){
        this.acadnewrec=new this.acad({
            id:'',
            qualification:'',
            qualificationin:'',
            institution:'',
            gradyear:'',
            marks:''
        });
        this.acadds.add(this.acadnewrec);
    },
    workrec:function(){
        this.worknewrec=new this.work({
            id:'',
            organisation:'',
            position:'',
            beginyear:'',
            endyear:'',
            comment:''
        });
        this.workds.add(this.worknewrec);
    },
    perec:function(){
        this.penewrec=new this.employer({
            id:'',
            organisation:'',
            position:'',
            beginyear:'',
            endyear:'',
            comment:''
        });
        this.empds.add(this.penewrec);
    },
    fillacadGridValue:function(e){
        if(e.row ==  this.acadds.getCount()-1){
            this.acadrec();
        }
        this.mainflag=1;
    }, 
    fillworkGridValue:function(e){
        if(e.row ==  this.workds.getCount()-1){
            this.workrec();
        }
        this.mainflag=1;
    },
    fillempGridValue:function(e){
        if(e.row ==  this.empds.getCount()-1){
            this.perec();
        }
        this.mainflag=1;
    },
    validate:function(e){
        if(e.column==4)
        {
            if(e.record.get('beginyear') > e.value)
            {
                return false;
            }
        }


    },
    validateacad:function(e){
        if(e.column==5)
        {
            if(e.record.get('yeargrdfrm') > e.value)
            {
                return false;
            }
        }


    },
    validateemployer:function(e){
        if(e.column==4)
        {
            if(e.record.get('beginyear') > e.value)
            {
                return false;
            }
        }


    },
    saveprofile:function(){
     calMsgBoxShow(200,4,true);
     this.errflag=0;
     this.errmsg="";
     this.formflag=1;
     if(this.persninfo.getForm().isValid()&&this.contactspnl.getForm().isValid()&&this.orgpanel.getForm().isValid()){
    	if(this.attrPanel.isValidate() && this.attrPanel1.isValidate() && this.attrPanel2.isValidate()){
	        if(this.lid==this.managerCmb.getValue() && this.roleid!=1 && userroleid==1) {
	            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.CannotAssignEmployeesasOwnManager")],2);
	        }
	        else {
	            calMsgBoxShow(200,4,true);
	            this.persnFormSubmit();
	        }
    	}else{
    		calMsgBoxShow(203,2);
    	}
      } else {
            calMsgBoxShow(5,2);
      }
    },
    persnFormSubmit:function(){
        this.persninfo.getForm().submit({
            success:function(f,a){
                this.contactsFormSubmit();
            },
            failure:function(f,a){
                this.errflag=this.errflag+1;
                this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorPersonalDetails",params:[this.errmsg]})+", "
                this.contactsFormSubmit();
            },
            scope:this
        });
    },
    contactsFormSubmit:function(){
        if(this.contactspnlpan.items){
            this.formflag=this.formflag+1;
            this.contactspnl.getForm().submit({
                success:function(f,a){
                    this.orgFormSubmit();
                },
                failure:function(f,a){
                    this.errflag=this.errflag+1;
                    this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorContactandWorkShiftDetails",params:[this.errmsg]})+", "
                    this.orgFormSubmit();
                },
                scope:this
            });
        } else {
            this.orgFormSubmit();
        }
    },
    orgFormSubmit:function(){
        if(this.orgpanelpan.items){
            this.formflag=this.formflag+1;
            this.orgpanel.getForm().submit({
                success:function(f,a){
                    this.qualNEmpSubmit();
                },
                failure:function(f,a){
                    this.errflag=this.errflag+1;
                    var res=eval('('+a.response.responseText+')');
                    if(!res.success) {
                        this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorOrganizationalDetailsMsg",params:[this.errmsg,res.msg]})+", "
                    } else {
                        this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorOrganizationalDetails",params:[this.errmsg]})+", "
                    }
                    this.qualNEmpSubmit();
                },
                scope:this
            });
        } else {
            this.qualNEmpSubmit();
        }
    },
    qualNEmpSubmit:function(){
        var jsondata = "";
        var jsondata1 = "";
        var jsondata2 = "";
        var vflag=0;
        var saveflag=1;
        var saveflag1=1;
        var saveflag2=1;
        if(this.workds.getCount()>0) {

            var modifiedwork=this.workds.getModifiedRecords();

            for(i=0;i<modifiedwork.length;i++)
            {
                if(modifiedwork[i].get('organisation')=="" || modifiedwork[i].get('position')==""||modifiedwork[i].get('beginyear')==""||modifiedwork[i].get('endyear')=="")
                {
                    vflag=0;
                    this.formflag=this.formflag+1;
                    this.errflag=this.errflag+1
                    this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorQualificationandEmploymentDetails",params:[this.errmsg]})+", "
                    calMsgBoxShow(28,0,false,250);
                    saveflag1=0;
                    break;
                }else{
                    vflag=1;
                    saveflag1=1;
                }
            }
            if(vflag==1){
                for(var i=0;i< this.workds.getCount()-1;i++){

                    jsondata += "{'id':'" + this.workds.getAt(i).get("id") + "',";
                    jsondata += "'organisation':'" + WtfGlobal.onlySinglequoateRenderer(this.workds.getAt(i).get("organisation")) + "',";
                    jsondata += "'position':'" + WtfGlobal.onlySinglequoateRenderer(this.workds.getAt(i).get("position")) + "',";
                    jsondata += "'beginyear':'" + WtfGlobal.convertToGenericDate(this.workds.getAt(i).get("beginyear")) + "',";
                    jsondata += "'endyear':'" + WtfGlobal.convertToGenericDate(this.workds.getAt(i).get("endyear")) + "',";
                    jsondata += "'type':'work',";
                    jsondata += "'comment':'" + WtfGlobal.onlySinglequoateRenderer(this.workds.getAt(i).get("comment")) + "'},";
                }
                var trmLen = jsondata.length - 1;
                var finalwork = jsondata.substr(0,trmLen);
            }
        }


        if(this.acadds.getCount()>0) {

            var modifiedacad= this.acadds.getModifiedRecords();
            for(i=0;i<modifiedacad.length;i++)
            {
                if( modifiedacad[i].get('qualification')=="" ||  modifiedacad[i].get('institution')==""|| modifiedacad[i].get('gradyear')==""|| modifiedacad[i].get('marks')==""
                    || modifiedacad[i].get('qualifiacationin')=="" ||  modifiedacad[i].get('yeargrdfrm')==""    )
                    {
                    vflag=0;
                    this.formflag=this.formflag+1;
                    this.errflag=this.errflag+1
                    this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorQualificationandEmploymentDetails",params:[this.errmsg]})+", "
                    calMsgBoxShow(28,0,false,250);
                    saveflag=0;
                    break;
                }else{
                    vflag=1;
                    saveflag=1;
                }
            }
            if(vflag==1){
                for(var j=0;j< this.acadds.getCount()-1;j++){

                    jsondata1 += "{'id':'" + this.acadds.getAt(j).get("id") + "',";
                    jsondata1 += "'qualification':'" + this.acadds.getAt(j).get("qualification") + "',";
                    jsondata1 += "'institution':'" + WtfGlobal.onlySinglequoateRenderer(this.acadds.getAt(j).get("institution")) + "',";
                    jsondata1 += "'gradyear':'" + WtfGlobal.convertToGenericDate(this.acadds.getAt(j).get("gradyear")) + "',";
                    jsondata1 += "'yeargrdfrm':'" + WtfGlobal.convertToGenericDate(this.acadds.getAt(j).get("yeargrdfrm")) + "',";
                    jsondata1 += "'qualificationin':'" + WtfGlobal.onlySinglequoateRenderer(this.acadds.getAt(j).get("qualificationin")) + "',";
                    jsondata1 += "'type':'acad',";
                    jsondata1 += "'marks':'" + WtfGlobal.onlySinglequoateRenderer(this.acadds.getAt(j).get("marks")) + "'},";
                }
                var trmLen1 = jsondata1.length - 1;
                var finalacad = jsondata1.substr(0,trmLen1);
            }
        }
        if(saveflag==1&&saveflag1==1&&saveflag2==1){
            this.formflag=this.formflag+1;
            Wtf.Ajax.requestEx({
//                url: Wtf.req.base + "hrms.jsp",
                url:"Common/saveempprofile.common",
                params: {
                    flag:50,
                    jsondatawk:finalwork,
                    jsondatacad:finalacad,
                    //jsondataprof:jsondata3,
                    userid:this.lid
                }
            }, this,
            function(response){
                var res=eval('('+response+')');
                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),res.msg],1);
                this.successOrFail();
                this.workds.load();
                this.acadds.load();
            },
            function(response)
            {
                this.errflag=this.errflag+1
                this.errmsg=WtfGlobal.getLocaleText({key:"hrms.common.errorQualificationandEmploymentDetails",params:[this.errmsg]})+", "
                calMsgBoxShow(27,1);
                this.successOrFail();
            });
        } else {
            this.successOrFail();
        }
    },
    successOrFail:function(){
        if(this.errflag==this.formflag){
            calMsgBoxShow(27,1);
        } else if(this.errflag==0){
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow122")],1);
        } else {
            this.errmsg=this.errmsg.substring(0, Math.max(0, this.errmsg.length-2));
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText({key:"hrms.common.ProfileUpdatedSuccessExceptError",params:["<br>"+this.errmsg]})],1);
        }
    },
    displayFormValue:function(){
        if(this.profstore.getCount()>0){
            var emprof=this.profstore.getAt(0);
            var src = emprof.data.image;
            var getMark = Wtf.get(this.id+"userpicture");
            //getMark.dom.src="images/store/temp_i1.jpeg";
            getMark.dom.src=src;
            Wtf.get(this.id+"aboutUser").dom.innerHTML="<div style=\"float:right;\">"+emprof.data.aboutuser+"</div>";
            this.fname.setValue(emprof.data.fname);
            this.mname.setValue(emprof.data.middlename);
            this.lname.setValue(emprof.data.lname);
            this.fullname.setValue(emprof.data.fullname);
            this.dob.setValue(emprof.data.dob);
            this.gender.setValue(emprof.data.gender);
            this.marState.setValue(emprof.data.marriage);
            this.bloodgrp.setValue(emprof.data.bloodgrp);
            this.bankacc.setValue(emprof.data.bankacc);
            this.bankname.setValue(emprof.data.bankname);
            this.bankbranch.setValue(unescape(emprof.data.bankbranch));
            this.panno.setValue(emprof.data.panno);
            this.pfno.setValue(emprof.data.pfno);
            this.drivingli.setValue(emprof.data.drvlicense);
            this.passport.setValue(emprof.data.passportno);
            this.exppassport.setValue(emprof.data.exppassport);
            this.fathername.setValue(emprof.data.fathername);
            this.fatherdob.setValue(emprof.data.fatherdob);
            this.mothername.setValue(emprof.data.mothername);
            this.motherdob.setValue(emprof.data.motherdob);
            this.spousename.setValue(emprof.data.spousename);
            this.spousedob.setValue(emprof.data.spousedob);
            this.childname.setValue(emprof.data.child1name);
            this.childdob.setValue(emprof.data.child1dob);
            this.childname1.setValue(emprof.data.child2name);
            this.childdob1.setValue(emprof.data.child2dob);
            if(emprof.data.mobno!=''){
                this.mobileno.setValue(emprof.data.mobno);
            }
            if(emprof.data.workno!=''){
                this.workTele.setValue(emprof.data.workno);
            }
            if(emprof.data.landno!=''){
                this.homeTele.setValue(emprof.data.landno);
            }
            this.workEmail.setValue(emprof.data.workmail);
            this.otherEmail.setValue(emprof.data.othermail);
            this.addr.setValue(unescape(emprof.data.presentaddr));
            this.city1.setValue(emprof.data.presentcity);
            this.state1.setValue(emprof.data.presentstate);
            this.countrycombo.setValue(emprof.data.precountry);
            this.permaddr.setValue(unescape(emprof.data.permaddr));
            this.city2.setValue(emprof.data.permcity);
            this.state2.setValue(emprof.data.permstate);
            this.countrycombo1.setValue(emprof.data.permcountry);
            this.mailaddr.setValue(unescape(emprof.data.mailaddr));
            this.emergencyname.setValue(emprof.data.emgname);
            this.relnship.setValue(emprof.data.emgreln);
            if(emprof.data.emghome!=''){
                this.emergencyTele.setValue(emprof.data.emghome);
            }
            if(emprof.data.emgwork!=''){
                this.emergencyTele2.setValue(unescape(emprof.data.emgwork));
            }
            if(emprof.data.emgmob!=''){
                this.emergencyTele3.setValue(emprof.data.emgmob);
            }
            this.emergencyaddr.setValue(unescape(emprof.data.emgaddr));
            this.empid.setValue(emprof.data.empid);
            this.depCmb.setValue(emprof.data.department);
            this.desigCmb.setValue(emprof.data.designationid);
            this.costcenter.setValue(emprof.data.costcenter);
            if(emprof.data.frequency==-1){
            	this.frequencyStoreCmb.setValue("");
            }else{
            	this.frequencyStoreCmb.setValue(emprof.data.frequency);
            }
                this.templateStore.baseParams={
                    type:'getTemplistperDesign'
                };
                this.templateStore.load({
                    params:{
                        desigid:emprof.data.designationid

                    }
                });
            this.managerCmb.setValue(emprof.data.managername);
            this.emptype.setValue(emprof.data.emptype);
            this.dateofjoining.setValue(emprof.data.joindate);
            this.dateofconfirm.setValue(emprof.data.confirmdate);
            this.dateofrelieve.setValue(emprof.data.relievedate);
            this.trainmon.setValue(emprof.data.trainingmon);
            this.trainyr.setValue(emprof.data.trainingyr);
            this.probationmonth.setValue(emprof.data.probationmon);
            this.probationyear.setValue(emprof.data.probationyr);
            this.noticemonth.setValue(emprof.data.noticemon);
            this.noticeyear.setValue(emprof.data.noticeyr);
            this.commid.setValue(emprof.data.commid);
            this.workbranch.setValue(emprof.data.branchcode);
            this.workbranchaddr.setValue(emprof.data.branchaddr);
            this.branchcity.setValue(emprof.data.branchcity);
            this.branchcountrycombo.setValue(emprof.data.brachcountry);
            this.keyskills.setValue(unescape(emprof.data.keyskills));
            if(emprof.data.wkstarttime!=''){
                this.starttime.setValue(emprof.data.wkstarttime);
            }
            if(emprof.data.wkendtime!=''){
                this.endtime.setValue(emprof.data.wkendtime);
            }
            if(emprof.data.weekoff!=''){
                this.daycmp.setValue(emprof.data.weekoff);
            }
        }

        this.enableMarstatus();
    },
    setfullname:function(){
        this.fullname.setValue(this.fname.getValue()+" "+this.mname.getValue()+" "+this.lname.getValue());
    },
    workexGridDel:function(){
        if(this.workexGridRowSM.hasSelection()){//Delete Record
            this.delkey1=this.workexGridRowSM.getSelections();
            this.ids1=[];
            for(var i=0;i<this.delkey1.length;i++){
                this.ids1.push(this.delkey1[i].get('id'));
            }
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:deleteMsgBox('employment detail'),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + 'hrms.jsp',
                            params:{
                                flag:53,
                                ids:this.ids1
                            }
                        },this,
                        function(){
                        	for(var i=0;i<this.delkey1.length;i++){
                        		this.workds.remove(this.delkey1[i]);	
                            }
                            calMsgBoxShow(120,0);
                        },
                        function(){
                            calMsgBoxShow(121,1);

                        }

                        )
                    }
                }
            });

        }
        else{//No selection
            calMsgBoxShow(42,0);
        }
    },
    acadGridDel:function(){
        if(this.acadGridRowSM.hasSelection()){//Delete Record
            this.delkey=this.acadGridRowSM.getSelections();
            this.ids=[];
            for(var i=0;i<this.delkey.length;i++){
                this.ids.push(this.delkey[i].get('id'));
            }
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:deleteMsgBox('qualification detail'),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + 'hrms.jsp',
                            params:{
                                flag:53,
                                ids:this.ids
                            }
                        },this,
                        function(){
                        	for(var i=0;i<this.delkey.length;i++){
                            	this.acadds.remove(this.delkey[i]);	
                            }
                            calMsgBoxShow(116,0);
                        },
                        function(){
                            calMsgBoxShow(117,0);

                        }

                        )
                    }
                }
            });

        }
        else{//No selection
            calMsgBoxShow(42,0);
        }
    },
    empGridDel:function(){
        if(this.EmpGridRowSM.hasSelection()){//Delete Record
            this.delkey2=this.EmpGridRowSM.getSelections();
            this.ids2=[];
            for(var i=0;i<this.delkey2.length;i++){
                this.ids2.push(this.delkey2[i].get('id'));
            }
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),
                msg:deleteMsgBox('employee detail'),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + 'hrms.jsp',
                            params:{
                                flag:53,
                                ids:this.ids2
                            }
                        },this,
                        function(){
                            calMsgBoxShow(142,0);
                            this.empds.load();
                        },
                        function(){
                            calMsgBoxShow(117,0);

                        }

                        )
                    }
                }
            });

        }
        else{//No selection
            calMsgBoxShow(42,0);
        }
    },
    enableMarstatus:function(){
         var status=this.marState.getValue();
            if(status==2){
                this.spousename.enable();
                this.spousedob.enable();
                this.childname.enable();
                this.childdob.enable();
                this.childname1.enable();
                this.childdob1.enable();
            }else{
                this.spousename.disable();
                this.spousedob.disable();
                this.childname.disable();
                this.childdob.disable();
                this.childname1.disable();
                this.childdob1.disable();
                this.spousename.setValue('');
                this.spousedob.setValue('');
                this.childname.setValue('');
                this.childdob.setValue('');
                this.childname1.setValue('');
                this.childdob1.setValue('');
            }
    },
   deleteRenderer:function(a,b,c,d){
        if(c.data.id !='-1')
            return "<div><div class='pwndCommon gridCancel' style='cursor:pointer' wtf:qtip="+WtfGlobal.getLocaleText("hrms.common.DeleteRecord")+"></div></div>";
    }


});
