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
Wtf.empmnt = function(config){
    Wtf.apply(this, config);
    Wtf.empmnt.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.empmnt, Wtf.Panel, {
    initComponent: function(){
        Wtf.empmnt.superclass.initComponent.call(this);
    },

    onRender:function(config) {
        Wtf.empmnt.superclass.onRender.call(this,config);
        this.empGrid();
        this.getAdvanceSearchComponent();
        this.exportinfo();
        this.getConfigData();
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
        this.objsearchComponent.on("saveStore",this.saveStore, this);
        this.objsearchComponent.on("reloadgridStore",this.reloadgridStore, this);
//        this.add(this.allempGrid);
        this.MembergridPanel = new Wtf.common.KWLListPanel({
            id: "membergridpanel" + this.id,
            title: "<div style='height:14px;width:14px;background-color:#99CC99;float:left;margin-top:3px;'></div>&nbsp; : "+WtfGlobal.getLocaleText("hrms.admin.Profilependingforapproval"),
            autoLoad: false,
            autoScroll:true,
            paging: false,
            layout: 'fit',
            items: [this.allempGrid]
        });

        this.pan= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[
            this.objsearchComponent
            ,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.MembergridPanel]
            }
            ]
        });
        this.add(this.pan);

    },
    loaddata : function(){
        var mainArray=new Array();
        this.objsearchComponent.cm = this.searchparams;
        for (i=0;i<this.objsearchComponent.cm.length;i++) {
            var tmpArray=new Array();
            if(this.objsearchComponent.cm[i].dbname && (this.objsearchComponent.cm[i].hidden==undefined || this.objsearchComponent.cm[i].hidden==false)) {
                var header=headerCheck(WtfGlobal.HTMLStripper(this.objsearchComponent.cm[i].header));
                tmpArray.push(header);
                tmpArray.push(this.objsearchComponent.cm[i].dbname);
                tmpArray.push(this.objsearchComponent.cm[i].xtype);
                tmpArray.push(this.objsearchComponent.cm[i].cname);
                tmpArray.push(this.objsearchComponent.cm[i].iscustom);
                mainArray.push(tmpArray)
            }
        }
        var myData = mainArray;
        this.objsearchComponent.combostore.removeAll();
        this.objsearchComponent.combostore.loadData(myData);

    },
    getConfigData : function (){
      this.count = 0;
        Wtf.Ajax.requestEx({
            url: "CustomCol/getConfigData.do",
            method: 'POST',
            params: {
                configFlag : "true",
                configType : "Personal",
                flag:218,
                grouper:'usergrid'
               // refid : this.lid
            }
        },this,
        function(response) {
            var responseObj = eval('('+response+')');
            if(responseObj.data !='' && responseObj.data !=null){
                this.count = responseObj.data.length;
                for(var i = 0; i < responseObj.data.length; i++) {
                   // if(responseObj.data[i].configtype==0){
                        this.searchparams[this.searchparams.length]=
                        {
                            name:responseObj.data[i].fieldname,
                            dbname:responseObj.data[i].colnum,
                            header:responseObj.data[i].fieldname,
                            xtype:'textfield',
                            iscustom : true

                        };
             //       }
                }
            }

//            this.loaddata();
        }, function() {
           
        }
        );
             Wtf.Ajax.requestEx({
            url: "CustomCol/getConfigData.do",
            method: 'POST',
            params: {
                configFlag : "true",
                configType : "Contact",
                flag:218,
                grouper:'usergrid'
                //refid : this.lid
            }
        },this,
        function(response) {
            var responseObj = eval('('+response+')');
            if(responseObj.data !='' && responseObj.data !=null){
                this.count = responseObj.data.length;
                for(var i = 0; i < responseObj.data.length; i++) {
                   // if(responseObj.data[i].configtype==0){
                        this.searchparams[this.searchparams.length]=
                        {
                            name:responseObj.data[i].fieldname,
                            dbname:responseObj.data[i].colnum,
                            header:responseObj.data[i].fieldname,
                            xtype:'textfield',
                            iscustom : true

                        };
             //       }
                }
            }

//            this.loaddata();
        }, function() {

        }
        );
             Wtf.Ajax.requestEx({
            url: "CustomCol/getConfigData.do",
            method: 'POST',
            params: {
                configFlag : "true",
                configType : "Organizational",
                flag:218,
                grouper:'usergrid',
                firequery:'1'
                //refid : this.lid
            }
        },this,
        function(response) {
            var responseObj = eval('('+response+')');
            if(responseObj.data !='' && responseObj.data !=null){
                this.count = responseObj.data.length;
                for(var i = 0; i < responseObj.data.length; i++) {
                   // if(responseObj.data[i].configtype==0){
                        this.searchparams[this.searchparams.length]=
                        {
                            name:responseObj.data[i].fieldname,
                            dbname:responseObj.data[i].colnum,
                            header:responseObj.data[i].fieldname,
                            xtype:'textfield',
                            iscustom : true

                        };
             //       }
                }
            }

            this.loaddata();
        }, function() {

        }
        );
    },
    empGrid:function(){

        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.record = Wtf.data.Record.create([
        {
            name: 'userid'
        },

        {
            name: 'username'
        },

        {
            name: 'designation'
        },

        {
            name: 'designationid'
        },

        {
            name: 'department'
        },

        {
            name: 'departmentname'
        },

        {
            name: 'firstname'
        },

        {
            name: 'lastname'
        },

        {
            name: 'image'
        },

        {
            name: 'emailid'
        },

        {
            name: 'lastlogin',
            type: 'date'
        },

        {
            name: 'aboutuser'
        },

        {
            name: 'address'
        },

        {
            name:'contactnumber'
        },

        {
            name:'manager'
        },
        {
            name:'managerid'
        },

        {
            name:'salary'
        },

        {
            name:'roleid',
            mapping:'role'
        },

        {
            name:'accno'
        },

        {
            name: 'rolename'
        },

        {
            name: 'employeeid'
        },

        {
            name:'status'
        },
        {
            name:'fullname'
        },
        {
            name:'reviewer'
        },
        {
            name:'reviewerid'
        },
        {
            name:'templateid'
        },{
            name:'frequency'
        }]);

   
        this.empGDS =new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.record),
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/getAllUserDetailsHrms.common",
            baseParams:{
                mode:114
            }
        });
        calMsgBoxShow(202,4,true);
        this.empGDS.load({params:{grouper:'usergrid',start:0,limit:15}});
        this.empGDS.on("load",function(){
            if(msgFlag==1)
                WtfGlobal.closeProgressbar();
        },this);
        this.searchparams=[
        {
            name:"fname",
            dbname:"u.firstName",
            header:WtfGlobal.getLocaleText("hrms.common.FirstName"),
            xtype:'textfield'

        },{
            name:"mname",
            dbname:"emp.middlename",
            header:WtfGlobal.getLocaleText("hrms.common.MiddleName"),
            xtype:'textfield'
        },{
            name:"lname",
            dbname:"u.lastName",
            header:WtfGlobal.getLocaleText("hrms.common.LastName"),
            xtype:'textfield'
        },{
            name:"DoB",
            dbname:"emp.DoB",
            header:WtfGlobal.getLocaleText("hrms.common.DoB"),
            xtype:'datefield'
        },{
            name:"gender",
            dbname:"emp.gender",
            header:WtfGlobal.getLocaleText("hrms.common.Gender"),
            xtype:'combo',
            cname:"gender"
        },

        {
            name:"marital",
            dbname:"emp.marriage",
            header:WtfGlobal.getLocaleText("hrms.common.MaritalStatus"),
            xtype:'combo',
            cname:"marital"
        },{
            name:"bldgrp",
            dbname:"emp.bloodgrp",
            header:WtfGlobal.getLocaleText("hrms.common.BloodGroup"),
            xtype:'textfield'
        }/*,{
            name:"bankacc",
            dbname:"bankacc",
            header:"Bank Account",
            xtype:'textfield'
        }*/,{
            name:"bankname",
            dbname:"emp.bankname",
            header:WtfGlobal.getLocaleText("hrms.common.BankName"),
            xtype:'textfield'
        },{
            name:"bankbranch",
            dbname:"emp.bankbranch",
            header:WtfGlobal.getLocaleText("hrms.common.BankBranch"),
            xtype:'textfield'
        }/*,{
            name:"pan",
            dbname:"pan",
            header:"PAN",
            xtype:'textfield'
        },{
            name:"pf",
            dbname:"pf",
            header:"PF",
            xtype:'textfield'
        },{
            name:"drivingli",
            dbname:"drivingli",
            header:"Driving License",
            xtype:'textfield'
        },{
            name:"passport",
            dbname:"passport",
            header:"Passport",
            xtype:'textfield'
        },{
            name:"exppassport",
            dbname:"exppassport",
            header:"Passport Expiry",
            xtype:'textfield'
        },{
            name:"fathername"
        },{
            name:"fatherDoB"
        },{
            name:"mothername"
        },{
            name:"motherDoB"
        },{
            name:"spousename"
        },{
            name:"spouseDoB"
        },{
            name:"childname1"
        },{
            name:"childDoB1"
        },{
            name:"childname2"
        },{
            name:"childDoB2"
        },{
            name:"mobileno"
        },{
            name:"worktele"
        },{
            name:"hometele"
        },{
            name:"workemail"
        },{
            name:"otheremail"
        },{
            name:"preaddress"
        }*/,{
            name:"city1",
            dbname:"emp.presentcity",
            header:WtfGlobal.getLocaleText("hrms.common.PresentCity"),
            xtype:'textfield'
        },{
            name:"state1",
            dbname:"emp.presentstate",
            header:WtfGlobal.getLocaleText("hrms.common.PresentState"),
            xtype:'textfield'
        },{
            name:"precountry",
            dbname:"emp.presentcountry.id",
            header:WtfGlobal.getLocaleText("hrms.common.PresentCountry"),
            xtype:'combo',
            cname:"country"
        }/*,{
            name:"peraddress"
        }*/,{
            name:"city2",
            dbname:"emp.permcity",
            header:WtfGlobal.getLocaleText("hrms.common.PermanentCity"),
            xtype:'textfield'
        },{
            name:"state2",
            dbname:"emp.permstate",
            header:WtfGlobal.getLocaleText("hrms.common.PermanentState"),
            xtype:'textfield'
        },{
            name:"permcountry",
            dbname:"emp.permcountry.id",
            header:WtfGlobal.getLocaleText("hrms.common.PermanentCountry"),
            xtype:'combo',
            cname:"country"
        }/*,{
            name:"mailaddress"
        },{
            name:"emercntname"
        },{
            name:"relation"
        },{
            name:"emrcontact"
        },{
            name:"emrcontact2"
        },{
            name:"emrcontact3"
        },{
            name:"emercntaddr"
        },{
            name:"empid"
        }*/,{
            name:"department",
            dbname:"ua.department.id",
            header:WtfGlobal.getLocaleText("hrms.common.department"),
            xtype:'combo',
            cname:"department"
        },{
            name:"designationid",
            dbname:"ua.designationid.id",
            header:WtfGlobal.getLocaleText("hrms.common.designation"),
            xtype:'combo',
            cname:"designation"
        },{
            name:"managername",
            dbname:"concat(mgr.assignman.firstName,' ',mgr.assignman.lastName)",
            header:WtfGlobal.getLocaleText("hrms.performance.appraiser"),
            xtype:'textfield'
        },{
            name:"appraisername",
            dbname:"concat(rev.reviewer.firstName,' ',rev.reviewer.lastName)",
            header:WtfGlobal.getLocaleText("hrms.common.Reviewer"),
            xtype:'textfield'
        }/*,{
            name:"tyofemp"
        }*/,{
            name:"Dtojoin",
            dbname:"emp.joindate",
            header:WtfGlobal.getLocaleText("hrms.recruitment.joining.date"),
            xtype:'datefield'
        },{
            name:"Dtoconfirm",
            dbname:"emp.confirmdate",
            header:WtfGlobal.getLocaleText("hrms.common.ConfirmationDate"),
            xtype:'datefield'
        }/*,{
            name:"Dtofrelieve"
        },{
            name:"trainingmon"
        },{
            name:"trainingyr"
        },{
            name:"probationyr"
        },{
            name:"probationmon"
        },{
            name:"noticeyr"
        },{
            name:"noticemon"
        },{
            name:"commid"
        }*/,{
            name:"brachcode",
            dbname:"emp.branchcode",
            header:WtfGlobal.getLocaleText("hrms.common.BranchCode"),
            xtype:'textfield'
        },{
            name:"brachcity",
            dbname:"emp.branchcity",
            header:WtfGlobal.getLocaleText("hrms.common.BranchCity"),
            xtype:'textfield'
        }/*,{
            name:"brachaddr"
        }*/,{
            name:"brachcountry",
            dbname:"emp.branchcountry.id",
            header:WtfGlobal.getLocaleText("hrms.common.BranchCountry"),
            xtype:'combo',
            cname:"country"
        },{
            name:"keyskills",
            dbname:"emp.keyskills",
            header:WtfGlobal.getLocaleText("hrms.common.Keyskills"),
            xtype:'textfield'
        },{
            name:"wkstart",
            dbname:"emp.wkstarttime",
            header:WtfGlobal.getLocaleText("hrms.common.WorkShiftStartTime"),
            xtype:'timefield'
        },{
            name:"wkend",
            dbname:"emp.wkendtime",
            header:WtfGlobal.getLocaleText("hrms.common.WorkShiftEndTime"),
            xtype:'timefield'
        },{
            name:"weeklyoff",
            dbname:"emp.weekoff",
            header:WtfGlobal.getLocaleText("hrms.common.WeeklyOff"),
            xtype:'combo',
            cname:"weeklyoff"
        },{
            name: 'organisation',
            dbname:"exp.organization",
            header:WtfGlobal.getLocaleText("hrms.common.PreviousEmployer"),
            xtype:'textfield'
        },{
            name: 'position',
            dbname:"exp.position",
            header:WtfGlobal.getLocaleText("hrms.common.PreviousPosition"),
            xtype:'textfield'
        },{
            name: 'qualification',
            dbname:"exp.qualification",
            header:WtfGlobal.getLocaleText("hrms.common.Qualification"),
            xtype:'combo',
            cname:"qualification"
        },{
            name: 'institution',
            dbname:"exp.institution",
            header:WtfGlobal.getLocaleText("hrms.common.EducationalInstitute"),
            xtype:'textfield'
        },{
            name: 'marks',
            dbname:"exp.marks",
            header:WtfGlobal.getLocaleText("hrms.common.Marks.Grade"),
            xtype:'textfield'
        },{
            name:'qualificationin',
            dbname:"exp.qaulin",
            header:WtfGlobal.getLocaleText("hrms.common.Specialization"),
            xtype:'textfield'
        }/*,{
            name:'yeargrdfrm',
            dbname:"exp.frmyear",
            header:"Qualification Start Date",
            xtype:'datefield'
        },{
            name: 'gradyear',
            dbname:"exp.yearofgrad",
            header:"Qualification End Date",
            xtype:'datefield'
        }*/
        ];
        
        this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'employeeid',
                autoWidth : true,
                pdfwidth:50,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username',
                pdfwidth:60,
                autoWidth : true,
                sortable: true               
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname',
                autoWidth : true,
                pdfwidth:100,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.department"),
                dataIndex: 'departmentname',
                autoWidth : true,
                pdfwidth:60,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                autoWidth : true,
                pdfwidth:60,
                sortable: true,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.EmailAddress"),
                dataIndex: 'emailid',
                autoSize : true,
                sortable: true,
                pdfwidth:100,
                renderer: WtfGlobal.renderEmailTo,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.ProfileStatus"),
                dataIndex: 'status',
                autoSize : true,
                pdfwidth:60,
                align:'center',
                sortable: true,
                groupable: true,
                renderer : function(val) {
                    if(val=='Pending')
                        return ('<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.recruitment.pending")+'</FONT>');//'<FONT COLOR="blue">Pending</FONT>'
                    else if(val=='Approved')
                        return ('<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.common.Approved")+'</FONT>');//'<FONT COLOR="green">Shortlisted</FONT>'
                    else if(val=='In Process')
                        return ('<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.recruitment.in.process")+'</FONT>');//'<FONT COLOR="red">In Process</FONT>'
                    else if(val=='On Hold')
                        return ('<FONT COLOR="DarkGoldenRod">'+WtfGlobal.getLocaleText("hrms.recruitment.on.hold")+'</FONT>');//'<FONT COLOR="DarkGoldenRod">On Hold</FONT>'
                    else if(val=='Incomplete')
                        return ('<FONT COLOR="Brown">'+WtfGlobal.getLocaleText("hrms.recruitment.InComplete")+'</FONT>');//'<FONT COLOR="DarkGoldenRod">On Hold</FONT>'
                    else
                         return val;
                }
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.AssignedAppraiser"),
                dataIndex: 'manager',
                autoSize : true,
                sortable: true,
                pdfwidth:120,
                groupable: true,
                renderer : function(val) {
                        return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
                }
            },  {
                header :WtfGlobal.getLocaleText("hrms.common.AssignedReviewer"),
                dataIndex: 'reviewer',
                autoSize : true,
                sortable: true,
                pdfwidth:120,
                groupable: true,
                renderer : function(val) {
                        return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
                }
            }
            ]);
        this.approvebtn=new Wtf.Action({
            text:WtfGlobal.getLocaleText("hrms.common.ApproveProfile"),
            tooltip:WtfGlobal.getLocaleText("hrms.common.ApproveProfile.tooltip"),
            id:this.id+'approved',
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            disabled:true,
            scope:this,
            handler:this.approve_status
        });
        var empbtns=new Array();
        var bbarbtns=new Array();
        var menubtns=[];
        menubtns.push(
            this.viewEditProfile = new Wtf.Action({
                text:WtfGlobal.getLocaleText("hrms.common.ViewEditProfile"),
                iconCls:getButtonIconCls(Wtf.btype.viewbutton),
                id:this.id+'viewprofilerejected',
                tooltip:WtfGlobal.getLocaleText("hrms.common.ViewEditProfile.tooltip"),
                disabled:true,
                scope:this,
                handler:this.viewprofile
            }),
            this.approvebtn
            );                 
        menubtns.push(
            this.documents = new Wtf.Action({
                text:WtfGlobal.getLocaleText("hrms.common.documents"),
                tooltip:WtfGlobal.getLocaleText("hrms.common.documents.tooltip"),
                iconCls:getButtonIconCls(Wtf.btype.documentbutton),
                id:this.id+'empdocs',
                disabled:true,
                scope:this,
                handler:this.documents
            }));
        menubtns.push(
            this.terminationOfService = new Wtf.Action({
                text:WtfGlobal.getLocaleText("hrms.common.TerminationofService"),
                tooltip:WtfGlobal.getLocaleText("hrms.common.TerminationofService.tooltip"),
                iconCls:'pwnd terminationIcon',
                disabled:true,
                scope:this,
                handler:this.termination
            }));
        menubtns.push(
        		this.personnelHistory = new Wtf.Action({
                text:WtfGlobal.getLocaleText("hrms.common.PersonnelHistory"),
                tooltip:WtfGlobal.getLocaleText("hrms.common.PersonnelHistory.tooltip"),
                iconCls:'pwnd emphistoryIcon',
                disabled:true,
                scope:this,
                handler:this.emphistory
            }));
       
       if(!isMalaysianCompany){
            menubtns.push(
            this.employeeTaxDeclaration = new Wtf.Action({
                text:WtfGlobal.getLocaleText("hrms.payroll.user.tax.declaration"),
                tooltip:WtfGlobal.getLocaleText("hrms.payroll.user.tax.declaration"),
                iconCls:"pwndHRMS incometaxuserformIcon",
                disabled:true,
                scope:this,
                handler:function(){
                    
                    var rec=this.sm2.getSelections() ;
                    var empid=rec[0].get('userid');
                    if(empid!=undefined){
                        payrollDeclarationForm(empid);
                    }    
                }
            }));
            
        }    
       
        empbtns.push('-', this.reset = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.empGDS.load({params:{grouper:'usergrid',start:0,limit:this.allempGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.allempGrid.id).setValue("");
        	}
     	}));
        
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.manageuser)){
            empbtns.push('-',this.personnelActions = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.admin.PersonnelActions"),
                iconCls:'pwndCommon personalActionIcon',
                menu:new Wtf.menu.Menu({
                    items:menubtns
                })
            })
            );
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.assignperm))
            empbtns.push('-', this.edituser = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.EditUser"),
                tooltip:WtfGlobal.getLocaleText("hrms.admin.EditUser.tooltip"),
                id:this.id+'EditUser',
                disabled:true,
                scope:this,
                iconCls:getButtonIconCls(Wtf.btype.editbutton),
                handler:function(){
                    this.showUserForm(true)
                }
            })
            );
//        empbtns.push('-',this.assignPermBtn =  new Wtf.Toolbar.Button({
//            text : "Assign Permissions",
//            tooltip:"Define access rights to the users i.e. what a particular user can / cannot do with the system depending on their roles and status in the organization.",
//            iconCls:'pwndHRMS permissionbuttonIcon',
//            disabled:true,
//            allowDomMove:false,
//            id:this.id+'assignpermissions',
//            scope : this,
//            handler : this.requestPermissions
//        }));

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.assignmanager))
            bbarbtns.push(this.assignManBtn = new Wtf.Toolbar.Button({
                text : WtfGlobal.getLocaleText("hrms.common.AssignAppraiser"),
                tooltip:WtfGlobal.getLocaleText("hrms.admin.AssignAppraiser.tooltip"),
                iconCls:'pwndHRMS managerbuttonIcon',
                id:this.id+'assignmanagerbtn',
                allowDomMove:false,
                disabled : true,
                scope : this,
                handler : function(){
                    this.assignManager(true,false);
                }
            }));
        bbarbtns.push('-',this.assignRevBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.common.AssignReviewer"),
            iconCls:'pwndCommon profilebuttonIcon',
            tooltip:WtfGlobal.getLocaleText("hrms.admin.AssignReviewer.tooltip"),
            id:this.id+'assignreviewerbtn',
            allowDomMove:false,
            disabled : true,
            scope : this,
            handler :function(){
                this.assignManager(false,false);
            }
        }));
        
        this.assigncompbtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.payroll.assign.component"),
            iconCls:'pwndCommon profilebuttonIcon',
            tooltip:WtfGlobal.getLocaleText("hrms.common.assign.default.component.settings.selected.employee"),
            id:this.id+'assigncompbtn',
            allowDomMove:false,
            disabled : true,
            scope : this,
            hidden:Wtf.cmpPref.payrollbase=="Date"?false:true,
            handler :function(){
                this.assignComponent();
            }
        });
        
        this.assignfreqbtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.payroll.assign.frequency"),
            iconCls:'pwndCommon profilebuttonIcon',
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.assign.payroll.frequency.selected.employee"),
            id:this.id+'assignfreqbtn',
            allowDomMove:false,
            disabled : true,
            scope : this,
            hidden:Wtf.cmpPref.payrollbase=="Date"?false:true,
            handler :function(){
                this.assignFrequency();
            }
        });
        
        if(Wtf.cmpPref.payrollbase=="Date"){
        	bbarbtns.push('-', this.assigncompbtn);
        	bbarbtns.push('-', this.assignfreqbtn);
        }
        
        bbarbtns.push('-',this.assignsalbtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.Administration.AssignSalaryAuthorization"),
            iconCls:'pwndCommon profilebuttonIcon',
            tooltip:WtfGlobal.getLocaleText("hrms.Administration.AssignSalaryAuthorization.tooltip"),
            allowDomMove:false,
            scope : this,
            handler :function(){
                this.assignManager(false,true);
            }
        }));
        
        
        
        var menubtns1=[];
        menubtns1.push(
            this.newEmployeesReport = new Wtf.Action({
                text : WtfGlobal.getLocaleText("hrms.admin.NewEmployeesReport"),
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                id:this.id+'joineerepbtn',
                disabled : false,
                scope : this,
                handler :function(){
                    this.joineeReport();
                }
            })
            );
         menubtns1.push(
        		this.designationChangesReport = new Wtf.Action({
                text : WtfGlobal.getLocaleText("hrms.admin.DesignationChangesReport"),
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                id:this.id+'desigchngbtn',
                disabled : false,
                scope : this,
                handler :function(){
                    this.desigChngReport();
                }
            })
            );
         empbtns.push('-',this.reports = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("hrms.common.Reports"),
                iconCls:getButtonIconCls(Wtf.btype.reportbutton),
                tooltip:WtfGlobal.getLocaleText("hrms.admin.Reports.tooltip"),
                menu:new Wtf.menu.Menu({
                    items:menubtns1
                })
            })
            );
        /*empbtns.push('-',this.joineeRepBtn = new Wtf.Toolbar.Button({
            text : "New Employees Report",
            iconCls:getButtonIconCls(Wtf.btype.reportbutton),
            id:this.id+'joineerepbtn',
            allowDomMove:false,
            disabled : false,
            scope : this,
            handler :function(){
                this.joineeReport();
            }
        }));*/
        empbtns.push('-',this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("hrms.common.advanced.search"),
            id:'advanced3',// In use, Do not delete
            scope : this,
            tooltip:WtfGlobal.getLocaleText("hrms.admin.AdvanceSearch.tooltip"),
            handler : this.configurAdvancedSearch,
            iconCls : 'pwnd searchtabpane'
        }));
        empbtns.push('->','-',
        this.ExportUserInfoBtn = new Wtf.exportButton({
            obj:this,
            menuItem:{
                csv:true,
                pdf:true,
                rowPdf:true
            },
            userinfo:true,
            get:3,
            url:"Common/exportUserInfo.common",
            filename:this.title
        }));
        this.allempGrid = new Wtf.KwlGridPanel({
            border: false,
            id:this.id+'qualifiedgr',
            store: this.empGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.admin.grid.search"),
            searchField:"fullname",
            tbar:empbtns,
            bbar:bbarbtns,
            serverSideSearch:true,
            viewConfig: {
                forceFit: true,
                getRowClass: function(record) {
                    if(record.data.status=='Pending')
                        return 'green-row';
                }
            }
        });

        this.sm2.on("selectionchange",function(){
        	if(this.sm2.getCount()==0){
        		//menubtns Start
        		this.viewEditProfile.setDisabled(true);
        		this.documents.setDisabled(true);
        		this.terminationOfService.setDisabled(true);
        		if(!isMalaysianCompany){
                        this.employeeTaxDeclaration.setDisabled(true);
        		}
        		this.personnelHistory.setDisabled(true);
        		this.approvebtn.setDisabled(true);
        		//menubtns End
        		
        		//empbtns Start
        		this.edituser.setDisabled(true);
        		//empbtns End
        		
        		//bbarbtns Start
        		this.assignManBtn.setDisabled(true);
        		this.assignRevBtn.setDisabled(true);
        		this.assigncompbtn.setDisabled(true);
        		this.assignfreqbtn.setDisabled(true);
        		//bbarbtns End
        	}else if(this.sm2.getCount()==1){
        		//menubtns Start
        		this.viewEditProfile.setDisabled(false);
        		this.documents.setDisabled(false);
        		this.terminationOfService.setDisabled(false);
        		if(!isMalaysianCompany){
                        this.employeeTaxDeclaration.setDisabled(false);
        		}
        		this.personnelHistory.setDisabled(false);
        		this.approvebtn.setDisabled(false);
        		//menubtns End
        		
        		//empbtns Start
        		this.edituser.setDisabled(false);
        		//empbtns End
        		
        		//bbarbtns Start
        		this.assignManBtn.setDisabled(false);
        		this.assignRevBtn.setDisabled(false);
        		this.assigncompbtn.setDisabled(false);
        		this.assignfreqbtn.setDisabled(false);
        		//bbarbtns End
        	}else if(this.sm2.getCount()>1){
        		//menubtns Start
        		this.viewEditProfile.setDisabled(true);
        		this.documents.setDisabled(true);
        		this.terminationOfService.setDisabled(false);
        		if(!isMalaysianCompany){
                        this.employeeTaxDeclaration.setDisabled(true);
        		}
        		this.personnelHistory.setDisabled(true);
        		this.approvebtn.setDisabled(false);
        		//menubtns End
        		
        		//empbtns Start
        		this.edituser.setDisabled(true);
        		//empbtns End
        		
        		//bbarbtns Start
        		this.assignManBtn.setDisabled(true);
        		this.assignRevBtn.setDisabled(true);
        		this.assigncompbtn.setDisabled(true);
        		this.assignfreqbtn.setDisabled(false);
        		//bbarbtns End
        	}
        },this);
    },

    exportinfo:function(){
        var i,k=1;
        var column = this.allempGrid.getColumnModel();
        this.pdfStore =new Wtf.data.Store({});
        for(i=1 ; i<column.getColumnCount() ; i++) { // skip row numberer
          if(column.isHidden(i)!=undefined||column.getColumnHeader(i)==""||column.getDataIndex(i)==""){
                    continue;
          }
          else{
                var aligned=column.config[i].align;
                var title;
                if(aligned==undefined)
                    aligned='center';
                if(column.config[i].title==undefined)
                    title=column.config[i].dataIndex;
                else
                    title=column.config[i].title;
                this.newPdfRec = new Wtf.data.Record({
                    header : title,
                    title : column.config[i].header,
                    width : column.config[i].pdfwidth,
                    align : aligned,
                    index : k
                });
                this.pdfStore.insert(this.pdfStore.getCount(), this.newPdfRec);
                k++;
          }
        }
        var extraparams=["accno","lastname","contactnumber","rolename","alternatecontactnumber","aboutuser","salary","fax","updatedon","lastlogin"
            ,"firstname","createdon","pannumber","ssnnumber","address","dob","gender","bloodgrp","fathername","mothername","passportno","joindate",
            "confirmdate","middlename","keyskills","wkstarttime","wkendtime","weekoff"]
        var extraparamsTitle=[ WtfGlobal.getLocaleText("hrms.common.acc.no"),
                               WtfGlobal.getLocaleText("hrms.common.LastName"),
                               WtfGlobal.getLocaleText("hrms.common.contact.number"),
                               WtfGlobal.getLocaleText("hrms.common.RoleName"),
                               WtfGlobal.getLocaleText("hrms.common.alternate.contact.number"),
                               WtfGlobal.getLocaleText("hrms.common.about.user"),
                               WtfGlobal.getLocaleText("hrms.common.Salary"),
                               WtfGlobal.getLocaleText("hrms.common.fax"),
                               WtfGlobal.getLocaleText("hrms.common.Updatedon"),
                               WtfGlobal.getLocaleText("hrms.common.last.login"),
                               WtfGlobal.getLocaleText("hrms.common.FirstName"),
                               WtfGlobal.getLocaleText("hrms.common.CreatedOn"),
                               WtfGlobal.getLocaleText("hrms.common.pan.number"),
                               WtfGlobal.getLocaleText("hrms.common.ssn.number"),
                               WtfGlobal.getLocaleText("hrms.recruitment.profile.Address"),
                               WtfGlobal.getLocaleText("hrms.common.DoB"),
                               WtfGlobal.getLocaleText("hrms.common.Gender"),
                               WtfGlobal.getLocaleText("hrms.common.BloodGroup"),
                               WtfGlobal.getLocaleText("hrms.common.father.name"),
                               WtfGlobal.getLocaleText("hrms.common.mother.name"),
                               WtfGlobal.getLocaleText("hrms.common.PassportNo"),
                               WtfGlobal.getLocaleText("hrms.recruitment.joining.date"),
                               WtfGlobal.getLocaleText("hrms.common.ConfirmationDate"),
                               WtfGlobal.getLocaleText("hrms.common.MiddleName"),
                               WtfGlobal.getLocaleText("hrms.common.KeySkills"),
                               WtfGlobal.getLocaleText("hrms.common.week.starttime"),
                               WtfGlobal.getLocaleText("hrms.common.week.endtime"),
                               WtfGlobal.getLocaleText("hrms.common.week.off")];
        for(i=0 ; i<extraparams.length ; i++) { // skip row numberer
                this.newPdfRec = new Wtf.data.Record({
                    header : extraparams[i],
                    title : extraparamsTitle[i],
                    width : 60,
                    align : aligned,
                    index : k
                });
                this.pdfStore.insert(this.pdfStore.getCount(), this.newPdfRec);
                k++;
        };
        this.grid = this.allempGrid;
    },

    viewprofile:function(){
        var perm=false;
        var rec=this.sm2.getSelections() ;
        var empname=(rec[0].get('firstname') + " " + rec[0].get('lastname'));
        var empid=rec[0].get('userid');
        var roleid=rec[0].get('roleid');
        var main=Wtf.getCmp("empmanagement");
        var edsignupTab=Wtf.getCmp("empmntprofile"+empid);
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.myProfileWindow({
                title:WtfGlobal.getLocaleText({key:"hrms.admin.employeesProfile",params:[empname]}),
                closable:true,
                id:'empmntprofile'+empid,
                layout:'fit',
                editperm:perm,
                blockemployeestoedit:false,
                lid:empid,
                roleid:roleid,
                manager:true,
                report:false,
                Grid:this.allempGrid,
                showTemplate:Wtf.cmpPref.payrollbase=="Date"?false:true,
                border:false,
                iconCls:'pwnd myProfileIcon'
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    documents:function(){
        var rec=this.sm2.getSelections() ;
        var empname=(rec[0].get('firstname') + " " + rec[0].get('lastname'));
        var empid=rec[0].get('userid');
        var main=Wtf.getCmp("empmanagement");
        var edsignupTab=Wtf.getCmp('empfilepanel'+empid);
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.document_panel({
                layout:'fit',
                border:false,
                title:WtfGlobal.getLocaleText({key:"hrms.admin.employeesDocuments",params:[empname]}),
                lid:empid,
                id:'empfilepanel'+empid,
                manager:true,
                closable:true,
                iconCls:'pwndCommon documenttabIcon'
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();      
    },
    approve_status:function(){
        var profile_id=[];
        var arr=this.sm2.getSelections();
        var flag=true;
        for(var i= 0;i<arr.length;i++) {
            if(arr[i].data.status!="Pending"){
                flag=false;
            }
        }        
        if(flag)
        {
            var approve_profile=this.sm2.getSelections();
            for(i=0;i<approve_profile.length;i++)
            {
                profile_id.push(approve_profile[i].get('userid'));
            }
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
//                url:Wtf.req.base+"hrms.jsp",
                url:"Common/approveprofile.common",
                params:{
                    flag:404,
                    emp_ids:profile_id
                }
            },
            this,
            function(){
                calMsgBoxShow(70,0,false,250);
                this.empGDS.load({
                    params:{
                        start:0,
                        limit:this.allempGrid.pag.pageSize,
                        ss: Wtf.getCmp("Quick"+this.allempGrid.id).getValue()
                    }
                });
            },
            function(){
                calMsgBoxShow(27,1);
            }
            );
        }else{
            calMsgBoxShow(148,0);
            this.sm2.clearSelections();
        }
    }, 

    showUserForm:function(isEdit){
        var rec=null;
        if(isEdit){
            if(this.allempGrid.getSelectionModel().hasSelection()==false||this.allempGrid.getSelectionModel().getCount()>1){
                Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.EditUser"), WtfGlobal.getLocaleText("hrms.common.EditUser.msg"));
                return;
            }
            rec = this.allempGrid.getSelectionModel().getSelected();
        }
        var cu=new Wtf.common.CreateUser({
            isEdit:isEdit,
            record:rec
        });
        cu.on('save',this.genSuccessResponse, this);
        cu.on('notsave',this.genFailureResponse, this);
    },

//    requestPermissions:function(){
//        var permWindow=new Wtf.common.Permissions({
//            title:"User Permissions",
//            resizable: false,
//            modal:true,
//            roleid:this.sm2.getSelected().data['roleid']
//        });
//        permWindow.show();
//    },
    assignManager:function(managerF,salaryManagerF){
    
        var title="";
        var userid="";
        var emparr=this.allempGrid.getSelectionModel().getSelections();
        if(managerF){
            title=WtfGlobal.getLocaleText("hrms.common.AssignAppraiser");
            userid=emparr[0].get('userid');
        }else if(salaryManagerF){
            title=WtfGlobal.getLocaleText("hrms.Administration.AssignSalaryAuthorization");
        }else{
            title=WtfGlobal.getLocaleText("hrms.common.AssignReviewer");
            userid=emparr[0].get('userid');
        }
        
        this.recWindow=new Wtf.assignManagerWin({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            closable:true,
            width:760,
            title:title,
            height:600,
            border:false,
            empGDS:this.empGDS,
            modal:true,           
            userid:userid,
            scope:this,
            plain:true,
            allempGrid:this.allempGrid,
            managerF:managerF,
            salaryManagerF:salaryManagerF
        });
        this.recWindow.show();       
    },
    assignComponent:function(){
        var userid="";
        var emparr=this.allempGrid.getSelectionModel().getSelections();
        userid=emparr[0].get('userid');
        var frequency=emparr[0].get('frequency');
        if(frequency==-1){
        	msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.admin.assign.frequency.selected.resource")],0);
        }else {
        	this.compWindow=new Wtf.assignComponentWin({
        		iconCls:getButtonIconCls(Wtf.btype.winicon),
        		layout:'fit',
        		closable:true,
        		width:860,
        		title:WtfGlobal.getLocaleText("hrms.payroll.assign.payroll.component"),
        		height:600,
        		border:false,
        		empGDS:this.empGDS,
        		modal:true,           
        		userid:userid,
        		frequency:frequency,
        		scope:this,
        		plain:true,
        		allempGrid:this.allempGrid
        	});
        	this.compWindow.show();
        }
    },
    assignFrequency:function(){
        var userid="";
        var emparr=this.allempGrid.getSelectionModel().getSelections();

        this.empSelected =new Wtf.data.Store(this.allempGrid.getStore().initialConfig);
        this.empSelected.add(emparr);

        this.compWindow=new Wtf.AssignFrequency({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            closable:true,
            width:600,
            title:WtfGlobal.getLocaleText("hrms.payroll.assign.frequency"),
            height:500,
            border:false,
            modal:true,
            empGDS:this.empSelected,
            id:'assign_frequency_window',
            scope:this,
            emparr:emparr,
            grid:this.allempGrid
        });
        this.compWindow.show();
    },
    saveassignManager:function(){
        if(this.recGrid.getSelectionModel().getCount()==0)
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.pleaseSelectRecFirst")],1);
        else{
            this.user=this.allempGrid.getSelectionModel().getSelections();
            this.userids=[];
            this.managerid=[];
            this.assignflag=true;
            for(var i=0;i<this.user.length;i++){
                this.userids.push(this.user[i].get('userid'));
            }
            this.manager=this.recGrid.getSelectionModel().getSelections();
            for(var j=0;j<this.manager.length;j++){
                this.managerid.push(this.manager[j].get('userid'));
            }
            for(i=0;i<this.user.length;i++){
                for(j=0;j<this.manager.length;j++){
                    if(this.user[i].get('userid')==this.manager[j].get('userid')){
                        this.assignflag=false;
                        break;
                    }
                }
            }
            if(!this.assignflag){
                this.recWindow.close();
                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.common.PleaseSelValidRecord")],1);
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
                        managerid:this.managerid,
                        isManager:true
                    }
                },
                this,
                function(){
                    this.recWindow.close();
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.admin.Appraiserassignedsuccessfully")],1,1);
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
                    Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.admin.Errorinassigningmanager"));
                })
            }
        }
    },
    genSuccessResponse:function(response){
        var rec=this.sm2.getSelected();
        var row=this.empGDS.indexOf(rec);
        this.sm2.clearSelections();
        WtfGlobal.highLightRow(this.allempGrid ,"33CC33",5,row);
        if(response.success==true) {
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),response.msg],0);
            var delayTask = new Wtf.util.DelayedTask(function(){
                this.empGDS.load({
                    params:{
                        start:this.allempGrid.pag.cursor,
                        limit:this.allempGrid.pag.pageSize,
                        ss: Wtf.getCmp("Quick"+this.allempGrid.id).getValue()
                    }
                });
            },this);
            delayTask.delay(1000);
        }
        this.enable();
    },
    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        msgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"),msg],2);
        this.enable();
    },       
    assignReviewer:function(){
        this.assignReviewer= new Wtf.assignReviewer({
            modal:true,
            title:WtfGlobal.getLocaleText("hrms.common.AssignReviewer"),
            resizable:false,
            layout:'fit',
            empGDS:this.empGDS,
            allempGrid:this.allempGrid,
            sm2:this.sm2
        });
        this.assignReviewer.show();
    },
    termination:function(){
        this.terWindow=new Wtf.terService({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            closable:true,
            resizable:false,
            title:WtfGlobal.getLocaleText("hrms.common.TerminationofService"),
            border:false,
            grids:this.allempGrid,
            id:this.id+'termwindow',
            modal:true,
            scope:this,
            plain:true
        });
        this.terWindow.show()
    },
    emphistory:function(){
        var rec=this.sm2.getSelections() ;
        var empname=(rec[0].get('firstname') + " " + rec[0].get('lastname'));
        var empid=rec[0].get('userid');
        var main=Wtf.getCmp("empmanagement");
        var edsignupTab=Wtf.getCmp('historypanel'+empid);
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.emphistory({
                layout:'fit',
                border:false,
                title:WtfGlobal.getLocaleText({key:"hrms.admin.employeesHistory",params:[empname]}),
                lid:empid,
                id:'historypanel'+empid,                
                closable:true,
                iconCls:'pwnd emphistoryTabIcon'
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout(); 
    },
    joineeReport:function(){
        var main=Wtf.getCmp("empmanagement");
        var edsignupTab=Wtf.getCmp('joineerep');
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.NewJoineesReport({
                layout:'fit',
                border:false,
                title:WtfGlobal.getLocaleText("hrms.admin.NewEmployeesReport"),
                id:"joineerep",
                closable:true,
                iconCls:getTabIconCls(Wtf.etype.crm)
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    desigChngReport:function(){
        var main=Wtf.getCmp("empmanagement");
        var edsignupTab=Wtf.getCmp('desigchngrep');
        if(edsignupTab==null)
        {
            edsignupTab=new Wtf.DesigChngReport({
                layout:'fit',
                border:false,
                title:WtfGlobal.getLocaleText("hrms.admin.DesignationChangesReport"),
                id:"desigchngrep",
                closable:true,
                iconCls:getTabIconCls(Wtf.etype.crm)
            });
            main.add(edsignupTab);
        }
        main.setActiveTab(edsignupTab);
        main.setVisible(true);
        main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.searchparams,
            searchid:this.searchid
        });
//        this.objsearchComponent.searchFlag = 1;
    },
    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.searchStore.load(
        {params:{
            searchid:this.searchid,
            searchFlag:1
        }});
        this.AdvanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
        this.empGDS.baseParams = {
            mode:114
        }
        this.empGDS.load();
        this.searchJson="";
        this.searchid="";
        this.objsearchComponent.hide();
        this.AdvanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.empGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson
        }
        this.empGDS.load();
    },
    reloadgridStore:function(json){
        this.searchJson="";
        if(this.searchid!=undefined){
            this.searchJson=json;
        }
        this.empGDS.baseParams = {
            mode:114,
            searchJson:this.searchJson
        }
        this.empGDS.load();
    },
    saveStore:function(json, saveSearchName){
        this.saveJson=json;
        Wtf.Ajax.requestEx({
            url:"Common/saveSearch.common",
            params:{
                mode:115,
                saveJson:this.saveJson,
                saveSearchName:saveSearchName,
                searchFlag:1
            }
        },
        this,
        function(response){
            var res=eval('('+response+')');
            if(res.isduplicate){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText({key:"hrms.administration.remember.already.exists.change.name",params:[saveSearchName]})],0, false, 450);
            }else {
                calMsgBoxShow(204,0,false,300);
                reloadSavedSeaches();
            }
        },
        function(response){
            calMsgBoxShow(27,1);
        });
    }
}); 

