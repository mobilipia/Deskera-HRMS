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

function ConfigMaster(){
    var mainTabId = Wtf.getCmp("as");
    var projectBudget = Wtf.getCmp("masterConfigTab");
    if(projectBudget == null){
        projectBudget = new Wtf.MasterConfigurator({
            layout:"fit",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.administration.master.configuration.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.administration.master.configuration")+"</div>",//"<div wtf:qtip=\"Configure all master settings and sub-fields in the master fields, add new fields and sub-fields and also modify the company preferences.\">Master Configuration</div>",
            closable:true,
            border:false,
            iconCls:getTabIconCls(Wtf.etype.hrmsmaster),
            id:"masterConfigTab"
        }); 
        mainTabId.add(projectBudget);
    }
    mainTabId.setActiveTab(projectBudget);
    mainTabId.doLayout();
}

function ConfigAppraisalCycleMaster(){
    var mainTabId = Wtf.getCmp("as");
    var projectBudget = Wtf.getCmp("AppraisalCycleConfigTab");
    if(projectBudget == null){
        projectBudget = new Wtf.appraisalCycleMasterGrid({
            layout:"fit",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.set.appraisal.cycle.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.set.appraisal.cycle")+"</div>",//"<div wtf:qtip=\"Configure all appraisal cycles and its start date and end date.\">Set Appraisal Cycle</div>",
            closable:true,
            border:false,
            iconCls:getTabIconCls(Wtf.etype.hrmsmaster),
            id:"AppraisalCycleConfigTab"
        });
        mainTabId.add(projectBudget);
    }
    mainTabId.setActiveTab(projectBudget);
    mainTabId.doLayout();
}


function auditTrail(){
    var panel = Wtf.getCmp("auditTrail");
    if(panel==null){
        panel = new Wtf.common.WtfAuditTrail({
            layout : "fit",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.administration.audit.trail.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.administration.audit.trail")+"</div>",//'<div wtf:qtip="Helps you to keep a track of all user activities performed on the system and also locate their place of occurrence through the IP Address.">Audit Trail</div>',
            border : false,
            id : "auditTrail",
            iconCls:getTabIconCls(Wtf.etype.hrmsaudit),
            closable: true
        });
        Wtf.getCmp('as').add(panel);
    }
    Wtf.getCmp('as').setActiveTab(panel);
    Wtf.getCmp('as').doLayout();
}

function payrollData(parentPanel, userID, empname){
    var usrid = loginid+"main";
    if(usrid!=undefined){
        usrid= userID;
    }
    var title = WtfGlobal.getLocaleText("hrms.payroll.my.tax.declaration");
    if(empname!=undefined){
        title = empname+': Tax Declaration';
    }
    var panel = Wtf.getCmp("payrollData"+usrid);
    this.year=new Date('2012/01/01').format('Y-m-d');
    if(panel==null){
    	calMsgBoxShow(202,4,true);
    	Wtf.Ajax.requestEx({
            url: "Payroll/MalaysianIncomeTax/getMalaysianDeductionComponents.py",
            params: {
                userID:userID!=undefined?userID:loginid,
                year:this.year
            }
        }, this,
        function(response){
        	var deductions = eval('('+response+')');
        	panel = new Wtf.MalaysianUserIncomeTaxForm({
        		layout : "fit",
        		title:'<div wtf:qtip="'+WtfGlobal.getLocaleText("hrms.payroll.manage.monthly.contributions.deductions.allowances.current.month")+'">'+title+'</div>',
        		border : false,
        		id : "payrollData"+usrid,
        		iconCls:"pwndHRMS incometaxformTabIcon",
        		closable: true,
                        usrid:usrid,
        		userid:userID!=undefined?userID:loginid,
        		year:this.year,
        		compulsoryDeductions:deductions.data.compulsoryDeductions,
        		optionalDeductions:deductions.data.optionalDeductions,
        		allowanceDeductions:deductions.data.allowanceDeductions,
        		userdata:deductions.data.userdata
        	});
            if(parentPanel!=undefined){

                parentPanel.add(panel);
                parentPanel.setActiveTab(panel);
                parentPanel.doLayout();
                Wtf.getCmp('as').doLayout();

            }else {

                Wtf.getCmp('as').add(panel);
                Wtf.getCmp('as').setActiveTab(panel);
                Wtf.getCmp('as').doLayout();
            }
            WtfGlobal.closeProgressbar();
        	
        },
        function(response){
        	
        });   
    }else {

        if(parentPanel!=undefined){

            parentPanel.setActiveTab(panel);
            parentPanel.doLayout();
            Wtf.getCmp('as').doLayout();

        }else {

            Wtf.getCmp('as').setActiveTab(panel);
            Wtf.getCmp('as').doLayout();
        }
    }
}

function payrollDeclarationForm(usrid){
    var userid = usrid!=undefined?usrid:loginid;
    
    var title = WtfGlobal.getLocaleText("hrms.payroll.my.tax.declaration");
    if(usrid!=undefined){
        title = WtfGlobal.getLocaleText("hrms.payroll.user.tax.declaration");
    }
    
    this.year=new Date('2012/01/01').format('Y-m-d');
    
    	calMsgBoxShow(202,4,true);
    	Wtf.Ajax.requestEx({
            url: "Payroll/Date/getTaxableComponents.py",
            params: {
                userID:userid,
                year:this.year
            }
        }, this,
        function(response){
        	this.components = eval('('+response+')');
        	this.panel = new Wtf.MyTaxDeclarationForm({
        		layout : "fit",
        		title:'<div wtf:qtip="'+WtfGlobal.getLocaleText("hrms.payroll.manage.monthly.contributions.deductions.allowances.current.month")+'">'+title+'</div>',
        		border : false,
        		iconCls:"pwndHRMS incometaxformTabIcon",
        		closable: true,
                        autoScroll:true,
                        height:500,
                        userid:userid,
                        width:400,
                        year:this.year,
                        modal:true,
                        components:this.components
        	});
            this.panel.show();
            WtfGlobal.closeProgressbar();
        	
        },
        function(response){
        	
        });   
   
}

function payrollUserData(parentPanel, userID, empname, statutoryForm){
    var userid = loginid+"mainuser";
    if(userid!=undefined){
        userid= userID;
    }
    var id = "payrollUserData"+userid;
    var title = WtfGlobal.getLocaleText("hrms.payroll.my.statutory.forms.details");
    if(empname!=undefined){
        title = WtfGlobal.getLocaleText({key:"hrms.payroll.my.statutory.forms.details.params",params:[empname]});
        id=id+"Admin";
    }
    var panel = Wtf.getCmp("payrollUserData"+userid);
    this.year=new Date('2012/01/01').format('Y-m-d');
    if(panel==null){
    	calMsgBoxShow(202,4,true);
    	Wtf.Ajax.requestEx({
            url: "Payroll/MalaysianStatutoryForm/getUserStatutoryFormInformation.py",
            params: {
                year:this.year,
                userID:userID!=undefined?userID:loginid,
                declarationMonth:statutoryForm!=undefined?statutoryForm.monthCmb.getValue():new Date().getMonth(),
                declarationYear:statutoryForm!=undefined?statutoryForm.yearCmb.getValue():new Date().getFullYear()
            }
        }, this,
        function(response){
        	var userdata = eval('('+response+')');
        	panel = new Wtf.MalaysianUserIncomeTax({
        		layout : "fit",
        		title:title,
        		border : false,
        		id : id,
        		iconCls:"pwndHRMS incometaxuserformTabIcon",
        		closable: true,
        		year:this.year,
                userid:userID!=undefined?userID:loginid,
        		userdata:userdata.data.userdata,
        		statutoryForm:statutoryForm
    	});

        if(parentPanel!=undefined){

                parentPanel.add(panel);
                parentPanel.setActiveTab(panel);
                parentPanel.doLayout();
                Wtf.getCmp('as').doLayout();

        } else {

            Wtf.getCmp('as').add(panel);
            Wtf.getCmp('as').setActiveTab(panel);
            Wtf.getCmp('as').doLayout();
        }
        WtfGlobal.closeProgressbar();
    	},
        function(response){

        });
    }else {

        if(parentPanel!=undefined){

            parentPanel.setActiveTab(panel);
            parentPanel.doLayout();
            Wtf.getCmp('as').doLayout();

        }else {

            Wtf.getCmp('as').setActiveTab(panel);
            Wtf.getCmp('as').doLayout();
        }
    }
}

function statutoryFormCompanyDetails(parentPanel, statutoryForm){

    var title = WtfGlobal.getLocaleText("hrms.payroll.company.statutory.forms.details");
    var panel = Wtf.getCmp("statutoryFormCompanyDetails"+companyid);
    
    if(panel==null){
    	calMsgBoxShow(202,4,true);
    	Wtf.Ajax.requestEx({
            url: "Payroll/MalaysianStatutoryForm/getCompanyFormInformation.py",
            params: {
                companyid:companyid,
                month:statutoryForm!=undefined?statutoryForm.monthCmb.getValue():new Date().getMonth(),
                year:statutoryForm!=undefined?statutoryForm.yearCmb.getValue():new Date().getFullYear()
            }
        }, this,
        function(response){
        	var userdata = eval('('+response+')');
        	panel = new Wtf.MalaysianCompanyStatutoryForm({
        		layout : "fit",
        		title:title,
        		border : false,
        		id : "statutoryFormCompanyDetails"+companyid,
        		iconCls:"pwndHRMS incometaxuserformTabIcon",
        		closable: true,
        		year:this.year,
                companyid:companyid,
        		userdata:userdata.data.userdata,
        		statutoryForm:statutoryForm
    	});

        if(parentPanel!=undefined){

                parentPanel.add(panel);
                parentPanel.setActiveTab(panel);
                parentPanel.doLayout();
                Wtf.getCmp('as').doLayout();

        }
        WtfGlobal.closeProgressbar();
    	},
        function(response){

        });
    }else {

        if(parentPanel!=undefined){

            parentPanel.setActiveTab(panel);
            parentPanel.doLayout();
            Wtf.getCmp('as').doLayout();

        }else {

            Wtf.getCmp('as').setActiveTab(panel);
            Wtf.getCmp('as').doLayout();
        }
    }
}

function payrollUserList(){

    var main =Wtf.getCmp("as");
    var mainSuccessionTab=Wtf.getCmp("payrollUserListtabpanel");
    if(mainSuccessionTab==null)
    {
        this.panel=new Wtf.MalaysianPayrollUser({
            id:"payrollUserList",
            title:WtfGlobal.getLocaleText("hrms.administration.user.tax.details"),
            layout:'fit',
            border:false,
            parentPanel:mainSuccessionTab,
            iconCls:getTabIconCls(Wtf.etype.hrmsgoals)
        });

        mainSuccessionTab=new Wtf.TabPanel({
            title:WtfGlobal.getLocaleText("hrms.administration.user.tax.details"),
            id:'payrollUserListtabpanel',
            activeTab:0,
            border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsgoals),
            items:[this.panel]
        });
        main.add(mainSuccessionTab);
    }
    main.setActiveTab(mainSuccessionTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
       
    
}
function checkForm(fid){ // To remove Html Tag & script tag from fields of form
    //Ankush Kale
    var ValidateForm=fid;
    for(i=0;i<ValidateForm.form.items.items.length;i++)
    {
        var item =ValidateForm.form.items.items[i];
        if(item.xtype=="textfield"||item.xtype=="textarea")
        {
            var tmpVal=item.getValue();
            item.setValue(WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tmpVal)));
        }
    }
}

function enableDisableButton(buttonid,GridStore,GridSm){
    //Ankush  Kale
    GridSm.on("selectionchange",function(){
        changeEnable(buttonid,GridSm);
    },this);


    GridStore.on("load",function(){
        changeEnable(buttonid,GridSm);
    },this)

}

function loadOrganizationPage(id){
    var ev = "adminclicked";
    switch(id) {
        case 1, "1":
            ev = "adminclicked";
            break;
        case 2, "2":
            ev = "projectclicked";
            break;
        case 3, "3":
            ev = "companyclicked";
            break;
    }
    mainPanel.loadTab("../../chart.html", "   myorganizationpanel", "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.common.effortlessly.create.organization.chart")+"'>"+WtfGlobal.getLocaleText("hrms.common.Organization")+"</div>", "navareadashboard", Wtf.etype.orgaTab,false,ev);
}

function changeEnable(buttonid,GridSm){
    //Ankush  Kale
    var selRec=GridSm.getSelections();
    var flag=0;
    if(selRec.length==1)
    {
        for (i=0;i<buttonid.length;i++)
        {
            if(Wtf.getCmp(buttonid[i]))
            {
                Wtf.getCmp(buttonid[i]).enable();
                flag=1;
            }
        }
        if(flag==0){
            if(Wtf.getCmp(buttonid))
                Wtf.getCmp(buttonid).enable();
        }
    }
    if(selRec.length<1||selRec.length>1)
    {
        for (i=0;i<buttonid.length;i++)
        {
            if(Wtf.getCmp(buttonid[i]))
            {
                Wtf.getCmp(buttonid[i]).disable();
                flag=1;
            }
        }
        if(flag==0)
            if(Wtf.getCmp(buttonid))
                Wtf.getCmp(buttonid).disable();
    }

}

function NewlineRemove(str){
    // str = Wtf.util.Format.stripScripts(str);
    if (str)
        return str.replace(/\n/g, ' ');
    else
        return str;
}

function demo()
{
    var main=Wtf.getCmp("as");
    var demoTab=Wtf.getCmp("demo");
    if(demoTab==null)
    {
        demoTab=new Wtf.Panel({
            id:"demo",
            title:"Demo Hrms",
            closable:true
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
}

function keypositions()
{
    var main =Wtf.getCmp("as");
    var mainSuccessionTab=Wtf.getCmp("successionmanage");
    if(mainSuccessionTab==null)
    {
        this.keypos=new Wtf.keyPositions({
            id:"empSuccessionTab",
            title : "Key Positions",
            layout : 'fit',
            closable: true,
            height:800,
            iconCls:getTabIconCls(Wtf.etype.hrmskey)
        //iconCls:'pwnd userTabIcon'
        });

        mainSuccessionTab=new Wtf.TabPanel({
            title:WtfGlobal.getLocaleText("hrms.common.succession.management"),
            id:'successionmanage',
            activeTab:0,
            border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmssuccession),
            items:[this.keypos]
        });
        main.add(mainSuccessionTab);
    }
    main.setActiveTab(mainSuccessionTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function internaljobs()
{
    var main =Wtf.getCmp("as");
    var mainSuccessionTab=Wtf.getCmp("recruitmentmanage");
    if(mainSuccessionTab==null)
    {      
        mainSuccessionTab=new Wtf.TabPanel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.management"),//'Recruitment Management',
            id:'recruitmentmanage',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsrecruitment)
        });
        main.add(mainSuccessionTab);
    }
    main.setActiveTab(mainSuccessionTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function internaljobBoard1()
{
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edjobTab=Wtf.getCmp("internaljobBoard");
    if(edjobTab==null)
    {
        this.internaljobBoard=new Wtf.InternalJob({
            id:"internaljobBoard",
            scope:this,
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.internal.job.board.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.internal.job.board")+"</div>",//'<div wtf:qtip="Employees can apply to internal jobs if there is a vacancy.">Internal Job Board</div>',
            layout:'fit',
            iconCls:getTabIconCls(Wtf.etype.hrmsinternaljobmanage)
        });
        main.add(this.internaljobBoard);
    }
    main.setActiveTab(this.internaljobBoard);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function compensation()
{
    var main =Wtf.getCmp("as");
    var mainCompensationTab=Wtf.getCmp("compensation");
    if(mainCompensationTab==null)
    {        
        mainCompensationTab=new Wtf.TabPanel({
            title:WtfGlobal.getLocaleText("hrms.Featurelist.compensationmanage"),
            id:'compensation',
            activeTab:0,
            border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmscompensation)
        //items:[this.compmanage]
        });
        main.add(mainCompensationTab);
    }
    main.setActiveTab(mainCompensationTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function timesheetmanagement()
{
    var main =Wtf.getCmp("as");
    var maintimesheetTab=Wtf.getCmp("timesheetmanage");
    if(maintimesheetTab==null)
    {


        maintimesheetTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.timesheet.management.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.timesheet.management")+"</div>",//'<div wtf:qtip="Enter the time duration and work duration of employees in a time sheet and also set approvals and generate reports.">Timesheet Management</div>',
            id:'timesheetmanage',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmstime)
        });
        main.add(maintimesheetTab);
    }
    main.setActiveTab(maintimesheetTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function GoalManagement()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var GoalTab=Wtf.getCmp("GoalManagement");
    if(GoalTab==null)
    {
        GoalTab=new Wtf.GoalManagement({
            id:"GoalManagement",
            title:WtfGlobal.getLocaleText("hrms.performance.goal.management"),//"Goal Management",
            layout:'fit',
            closable:true/*,
                iconCls:getTabIconCls(Wtf.etype.hrms)*/
        });
        main.add(GoalTab);
    }
    main.setActiveTab(GoalTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function myAppraisal(appid)
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var GoalTab=Wtf.getCmp("myappraisal");
    if(GoalTab==null)
    {
        GoalTab=new Wtf.competencyEval({
            id:"myappraisal",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.my.appraisal.form.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.my.appraisal.form")+"</div>",//"<div wtf:qtip='Personalized form to evaluate employee performance based on their goals and competencies. '>My Appraisal Form</div>",
            employee:true,
            read:false,
            modify:true,
            viewappraisal:false,
            apptype:appid,
            autoScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsappraisalform)
        });
        main.add(GoalTab);
    } else {
        GoalTab.apptype=appid;
        GoalTab.appTypeStore.load();
    }
    main.on("activate",function(){
            if(GoalTab.compEvalpanel!=null)
                GoalTab.compEvalpanel.doLayout();
            if(Wtf.getCmp('southgoalpanelmyappraisal') != undefined)
                Wtf.getCmp('southgoalpanelmyappraisal').setHeight(250);
            Wtf.getCmp('southpanelcontmyappraisal').doLayout();
    },this)
    main.setActiveTab(GoalTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function InitiateAppraisal()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var edTab=Wtf.getCmp("viewapp");
    if(edTab==null)
    {

        edTab=new Wtf.Appraisalsmanagement({
            id:"viewapp",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.initiate.appraisal.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.initiate.appraisal")+"</div>",//"<div wtf:qtip='Helps you initiate performance appraisal process for your employees. Performance of employees is assessed by their respective managers.'>Initiate Appraisal</div>",
            layout:'fit',
            iconCls:getTabIconCls(Wtf.etype.hrmsinitapp)
        });
        main.add(edTab);
    }
    main.on("activate",function(){
        Wtf.getCmp('npanel'+edTab.id).setHeight(120);
        if(edTab.appraisalPanel!=null)
            edTab.appraisalPanel.doLayout();
    },this)
    main.setActiveTab(edTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();

}

function AddJobs()
{
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edjobTab=Wtf.getCmp("addjobs");
    if(edjobTab==null)
    {
        this.edjobTab1=new Wtf.jobmaster({
            id:"addjobs",
            title:WtfGlobal.getLocaleText("hrms.recruitment.internal.job.management"),//"Internal Job Management",
            scope:this,
            height:800,
            layout:'fit',            
            iconCls:getTabIconCls(Wtf.etype.hrmsinternalmanage)
        });
        this.internalTab=new Wtf.TabPanel({
            title:WtfGlobal.getLocaleText("hrms.recruitment.internal.jobs"),//"Internal Jobs",
            id:'internalTab',
            height:800,            
            scope:this,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsinternaljob),
            items:[this.edjobTab1]
        });
        main.add(this.internalTab);
    }
    main.setActiveTab( this.internalTab);
    main.doLayout();
    this.internalTab.setActiveTab(this.edjobTab1);
    this.internalTab.doLayout();
    Wtf.getCmp("as").doLayout();
}

function AddJobs2(searchId)
{
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edjobTab2=Wtf.getCmp("addjobs2");
    if(edjobTab2==null)
    {
        this.edjobTab2=new Wtf.jobmaster2({
            id:"addjobs2",
            scope:this,
            height:800,
            title:WtfGlobal.getLocaleText("hrms.recruitment.job.management"),//'Job Management',
            layout:'fit',
            jobbuttons:true,
            searchid:searchId,
            agency:false,
            disableBut:false,
            iconCls:getTabIconCls(Wtf.etype.hrmsexternalmanage)
        });
        this.externalTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.add.jobs.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.add.jobs")+"</div>",//"<div wtf:qtip='You can set up and access job positions, job descriptions, and approve authority for a particular job.'>Add Jobs</div>",
            id:'externalTab',
            height:800,
            scope:this,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsexternaljob),
            items:[this.edjobTab2]
        });
        main.add(this.externalTab);
    }

    main.setActiveTab( this.externalTab);
    main.doLayout();
    this.externalTab.setActiveTab(this.edjobTab2);
    this.externalTab.doLayout();

    if(searchId!=undefined){ // For Saved Searches to show Advenced Search component
        this.edjobTab2.searchid = searchId;
        this.edjobTab2.configurAdvancedSearch();
    }

    Wtf.getCmp("as").doLayout();
}

function recruiters()
{
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var recruiterTab=Wtf.getCmp("recruiters");
    if(recruiterTab==null)
    {
        recruiterTab=new Wtf.recruiters({
            id:"recruiters",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.interviewers.list.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.interviewers.list")+"</div>",//"<div wtf:qtip='Assign interviewers to screen candidates on the basis of their merit. Interviewers are assigned on the basis of their professional knowledge.'>Interviewers List</div>",
            scope:this,
            height:800,
            layout:'fit',
            // closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsrecruiter)
        });
        main.add(recruiterTab);
    }
    main.setActiveTab(recruiterTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function internaljobs()
{
    var main =Wtf.getCmp("as");
    var mainSuccessionTab=Wtf.getCmp("recruitmentmanage");
    if(mainSuccessionTab==null)
    {
        mainSuccessionTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.management.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.management")+"</div>",//'<div wtf:qtip="Manage the recruitment process of the applicants effectively & save a lot of organization\'s time.">Recruitment Management</div>',
            id:'recruitmentmanage',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsrecruitment)
        });
        main.add(mainSuccessionTab);
    }
    main.setActiveTab(mainSuccessionTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function applicantlist()
{
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edjobTab=Wtf.getCmp("applicants");
    if(edjobTab==null)
    {
            
        this.applicantlistTab=new Wtf.appsList({
            id:"applicants",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.applicants.list.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.applicants.list")+"</div>",//"<div wtf:qtip='Create new applicants, edit or delete the existing applicants and can also view the job status.'>Applicants List</div>",
            scope:this,
            height:800,
            layout:'fit',
            //closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsapplicantlist)
        });
        main.add(this.applicantlistTab);
    }
    main.setActiveTab(this.applicantlistTab);
    main.doLayout();
    this.applicantlistTab.doLayout();
    Wtf.getCmp("as").doLayout();
}

function Recruitagencies(){
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edagencyTab=Wtf.getCmp("jobagencies");
    if(edagencyTab==null)
    {
        this.edagency=new Wtf.recruitAgencies({
            id:"jobagencies",
            title:WtfGlobal.getLocaleText("hrms.recruitment.manage.job.agencies"),//"Manage Job Agencies",
            layout:'fit',
            height:800,
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.hrmsmanageagency)
        });
        this.agencyTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.recruitment.agency.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.recruitment.agency")+"</div>",//"<div wtf:qtip='Enter all details of recruitment agencies including their contact details and manage their information efficiently. Assign jobs to the recruitment agency & view the jobs assigned.'>Recruitment Agency</div>",
            id:'agencyTab',
            height:800,
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.hrmsrecruit),
            items:[this.edagency]
        });
        main.add(this.agencyTab);
    }
    main.setActiveTab(this.agencyTab);
    main.doLayout();
    this.agencyTab.setActiveTab(this.edagency);
    this.agencyTab.doLayout();
    Wtf.getCmp("as").doLayout();
}

function competencyedit(appid){
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var edsdaagencyTab=Wtf.getCmp("cmpts");
    if(edsdaagencyTab==null)
    {
        edsdaagencyTab=new Wtf.competencyEval({
            id:"cmpts",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.appraisal.form.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.appraisal.form")+"</div>",//"<div wtf:qtip='Personalized form to evaluate employee performance based on their goals and competencies.'>Appraisal Form</div>",
            employee:false,
            read:true,
            modify:false,
            viewappraisal:false,
            autoScroll:true,
            apptype:appid,
            iconCls:getTabIconCls(Wtf.etype.hrmsappraisalform)
        });
        main.add(edsdaagencyTab);
    } else {
        edsdaagencyTab.apptype=appid;
        edsdaagencyTab.appTypeStore.load();
    }
    main.on("activate",function(){
            if(edsdaagencyTab.compEvalpanel!=null)
                edsdaagencyTab.compEvalpanel.doLayout();
            if(Wtf.getCmp('southgoalpanelcmpts')!=null)
                Wtf.getCmp('southgoalpanelcmpts').setHeight(250);
            Wtf.getCmp('southpanelcontcmpts').doLayout();
    },this)
    main.setActiveTab(edsdaagencyTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function competencymanage()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("manageComp");
    if(demoTab==null)
    {
        demoTab=new Wtf.manageCompetency({
            id:"manageComp",
            title:WtfGlobal.getLocaleText("hrms.performance.competency.master"),//"Competency Master",
            layout:'fit',
            //border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmscompetencymaster)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function myAppraisalForm()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("employeecompetency");
    if(demoTab==null)
    {
        demoTab=new Wtf.employeeCompetency({
            id:"employeecompetency",
            title:WtfGlobal.getLocaleText("hrms.common.employee.form"),
            layout:'fit',
            border:false,
            closable:true/*,
                iconCls:getTabIconCls(Wtf.etype.hrms)*/
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function configCompetency()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("configComp");
    if(demoTab==null)
    {
        demoTab=new Wtf.assignCompetency({
            id:"configComp",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.competency.management.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.competency.management")+"</div>",//"<div wtf:qtip=\"Define and link key competencies alongside employee's job architecture and one can also add, edit or delete the competencies in the competency master.\">Competency Management</div>",
            layout:'fit',
            border:false,
            //closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmanagecompensation)
        });
        var competencyMaster =new Wtf.manageCompetency({
            id:"manageComp",
            title:WtfGlobal.getLocaleText("hrms.performance.competency.master"),//"Competency Master",
            layout:'fit',
            border:false,
            //closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmscompetencymaster)
        });
        main.add(competencyMaster);
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function timesheet(startdate)
{
    timesheetmanagement();
    var main=Wtf.getCmp("timesheetmanage");
    var tdemoTab=Wtf.getCmp("timesheetemp");
    if(tdemoTab==null)
    {
        tdemoTab=new Wtf.timesheetemp({
            id:"timesheetemp",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.timesheet.timesheet.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.timesheet.timesheet")+"</div>",//"<div wtf:qtip='The employees enter the hours for tasks performed for a specific period of time.'>TimeSheet</div>",
            layout:'fit',
            viewtimesheet:false,
            border:false,
            viewstdate:startdate,
            //closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmstimesheet)
        });
        main.add(tdemoTab);
    }
    main.setActiveTab(tdemoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function viewtimesheet(startdate)
{
    timesheetmanagement();
    var main=Wtf.getCmp("timesheetmanage");
    var vtdemoTab=Wtf.getCmp("viewtimesheet");
    if(vtdemoTab==null)
    {
        vtdemoTab=new Wtf.viewtimesheet({
            id:"viewtimesheet",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.timesheet.view.timesheets.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.timesheet.view.timesheets")+"</div>",//"<div wtf:qtip='Allows the administrator/ manager to review and approve submitted time sheets of his employees.'>View TimeSheets</div>",
            layout:'fit',
            border:false,
            startdate:startdate,
            //closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsviewtimesheet)
        });
        main.add(vtdemoTab);
    }
    main.setActiveTab(vtdemoTab);
    main.doLayout();
    vtdemoTab.doLayout();
    Wtf.getCmp("as").doLayout();
}

function viewedittimesheet()
{
    timesheetmanagement();
    var main=Wtf.getCmp("timesheetmanage");
    var vtdemoTab=Wtf.getCmp("viewedittimesheet");
    if(vtdemoTab==null)
    {
        vtdemoTab=new Wtf.Panel({
            id:"viewedittimesheet",
            layout: 'fit',
            title: WtfGlobal.getLocaleText("hrms.common.employee.schedule.payroll"),
            iconCls:getTabIconCls(Wtf.etype.hrmsgoals),
            border: false,
            closable: false,
            items:[new Wtf.AttendanceInfopanel({
                id: "EmppSchedule" ,
                layout : 'fit',
                title:"EmployeeSchedule",
                tabname: "Employee",
                act:2,
                clicksToEdit: 1
            })]
        // bbar: this.err1,

        //iconCls:getTabIconCls(Wtf.etype.accemployee)
        });
        main.add(vtdemoTab);
    }
    main.setActiveTab(vtdemoTab);
    //main.doLayout();
    //vtdemoTab.doLayout();
    Wtf.getCmp("as").doLayout();
}
function viewmypayslip()
{
    var main=Wtf.getCmp("as");
    var viewmypayslipx=Wtf.getCmp("viewmypayslip");
    if(viewmypayslipx==null)
    {
        viewmypayslipx=new Wtf.viewmypayslip({
            id:"viewmypayslip",
            layout: 'fit',
            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.my.payslip.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.my.payslip")+"</div>",//"<div wtf:qtip='Gives you a comprehensive data on the employee work duration to ensure that appropriate salary is paid to the employee based on his/her performance.'>My Payslip</div>",
            border: false,
            closable: true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip)
        });
        main.add(viewmypayslipx);
    }
    main.setActiveTab(viewmypayslipx);
    //main.doLayout();
    //vtdemoTab.doLayout();
    Wtf.getCmp("as").doLayout();
}


function myPayslipDate()
{
    var main=Wtf.getCmp("as");
    var myPayslip=Wtf.getCmp("myPayslip");
    if(myPayslip==null)
    {
    	myPayslip=new Wtf.MyPayslip({
            id:"myPayslip",
            layout: 'fit',
            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.Dashboard.MyPayslip.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.Dashboard.MyPayslip")+"</div>",
            border: false,
            closable: true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip)
        });
        main.add(myPayslip);
    }
    main.setActiveTab(myPayslip);
    Wtf.getCmp("as").doLayout();
}

function generatePayrollProcess()
{
    var main=Wtf.getCmp("as");
    var generatedSalaryComponent=Wtf.getCmp("GeneratePayrollProcessGridID");
    if(generatedSalaryComponent==null)
    {
        generatedSalaryComponent=new Wtf.GeneratePayrollProcessGrid({
            id:"GeneratePayrollProcessGridID",
            layout: 'fit',
            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.generate.salary.process")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.generate.salary.process")+"</div>",
            border: false,
            closable: true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip)
        });
        main.add(generatedSalaryComponent);
    }
    main.setActiveTab(generatedSalaryComponent);
        
    Wtf.getCmp("as").doLayout();
}

function authorizePayrollProcess()
{
    var main=Wtf.getCmp("as");
    var authorizeSalaryComponent=Wtf.getCmp("authorizePayrollProcessGridID");
    if(authorizeSalaryComponent==null)
    {
        authorizeSalaryComponent=new Wtf.authorizePayrollProcessGrid({
            id:"authorizePayrollProcessGridID",
            layout: 'fit',
            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.Dashboard.AuthorizeSalary")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.authorize.salary.process")+"</div>",
            border: false,
            closable: true,
            authorize: true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip)
        });
        main.add(authorizeSalaryComponent);
    }
    main.setActiveTab(authorizeSalaryComponent);
        
    Wtf.getCmp("as").doLayout();
}

function processPayroll()
{
    var main=Wtf.getCmp("as");
    var processSalaryComponent=Wtf.getCmp("processPayrollGridID");
    if(processSalaryComponent==null)
    {
        processSalaryComponent=new Wtf.processPayrollGrid({
            id:"processPayrollGridID",
            layout: 'fit',
            title: "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.process.salary")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.process.salary")+"</div>",
            border: false,
            closable: true,
            authorize: true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmypayslip)
        });
        main.add(processSalaryComponent);
    }
    main.setActiveTab(processSalaryComponent);
        
    Wtf.getCmp("as").doLayout();
}

function finalReport()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("finalreport");
    if(demoTab==null)
    {   
        var finalreport="";
        if(Wtf.cmpPref.annmng){
            finalreport=Wtf.AppraisalReport;
        }else{
            finalreport=Wtf.finalReport;
        }
        demoTab=new finalreport({
            id:"finalreport",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.appraisal.report.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.appraisal.report")+"</div>",//"<div wtf:qtip=\"Generate an appraisal report listing performance details so that effective appraisal of the employee could be done.\">Appraisal Report</div>",
            layout:'fit',
            border:false,
            reviewappraisal:false,
            reviewer:false,
            myfinalReport:false,
            iconCls:getTabIconCls(Wtf.etype.hrmsappraisalreport)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function myfinalReport(appid)
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("myfinalreport");
    if(demoTab==null)
    {
          var finalreport="";
         if(Wtf.cmpPref.annmng){
            finalreport=Wtf.AppraisalReport;
        }else{
            finalreport=Wtf.finalReport;
        }
        demoTab=new finalreport({
            id:"myfinalreport",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.my.appraisal.report.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.my.appraisal.report")+"</div>",//"<div wtf:qtip=\"Generate an appraisal report listing performance details so that effective appraisal of the employee could be done.\">My Appraisal Report</div>",
            layout:'fit',
            border:false,
            reviewer:false,
            reviewappraisal:false,
            apptype:appid,
            myfinalReport:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmyappraisalreport)
        });
        main.add(demoTab);
    } else {
        demoTab.apptype=appid;
        demoTab.appTypeStore.load();
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function GoalManagementTabPanel()
{
    var main=Wtf.getCmp("as");
    var demoTab=Wtf.getCmp("goalmanagementtabpanel");
    if(demoTab==null)
    {
        demoTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.appraisal.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.appraisal")+"</div>",//'<div wtf:qtip="Assessing an employee\'s performance can be done in an easier way.List down and manage the competencies, assign goals or set realistic goals by yourself.">Performance Appraisal</div>',
            id:'goalmanagementtabpanel',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsperformance)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function compensationFunction()
{
    compensation();
    var main=Wtf.getCmp("compensation");
    var demoTab=Wtf.getCmp("compmanage");
    if(demoTab==null)
    {
        demoTab=new Wtf.compensationManage({
            id:"compmanage",
            title:WtfGlobal.getLocaleText("hrms.Featurelist.compensationmanage"),
            layout:'fit',
            border:false,
            autoScroll:true,
            //   closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmanagecompensation)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function compensationRecFunction()
{
    compensation();
    var main=Wtf.getCmp("compensation");
    var compTab=Wtf.getCmp("compenreport");
    if(compTab==null)
    {
        compTab=new Wtf.compensationRec({
            id:"compenreport",
            title:WtfGlobal.getLocaleText("hrms.featurelist.compensation.report"),
            layout:'fit',
            border:false,
            mycompensation:0,
            iconCls:getTabIconCls(Wtf.etype.hrmsreport)
        });
        main.add(compTab);
    }
    main.setActiveTab(compTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function mycompensationRecFunction()
{
    compensation();
    var main=Wtf.getCmp("compensation");
    var compTab=Wtf.getCmp("mycompenreport");
    if(compTab==null)
    {
        compTab=new Wtf.compensationRec({
            id:"mycompenreport",
            title:WtfGlobal.getLocaleText("hrms.common.my.compensation.report"),
            layout:'fit',
            border:false,
            mycompensation:1,
            iconCls:getTabIconCls(Wtf.etype.hrmsreport)
        });
        main.add(compTab);
    }
    main.setActiveTab(compTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function allemployeegoals()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("allemployeesforgoal");
    if(demoTab==null)
    {
        demoTab=new Wtf.allemployeegoals({
            id:"allemployeesforgoal",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.assign.goal.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.assign.goal")+"</div>",//"<div wtf:qtip='The manager can frame realistic goals which can be weighed and scored and pass them on to their employees depending on their role in the organization.'>Assign Goal</div>",
            layout:'fit',
            border:false,
            //  closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsgoals)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();


}

function archivedgoals()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("archivedgoals");
    if(demoTab==null)
    {
        demoTab=new Wtf.archivedGoals({
            id:"archivedgoals",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.archived.goals.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.archived.goals")+"</div>",//"<div wtf:qtip='Archive your goals which are completed/ used in the appraisal process/ no longer pertinent.'>Archived Goals</div>",
            layout:'fit',
            border:false,
            iconCls:getTabIconCls(Wtf.etype.hrmsarchive)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function GoalAssign()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("perticularemployeesforgoal");
    if(demoTab==null)
    {
        demoTab=new Wtf.perticularemployeegoals({
            id:"perticularemployeesforgoal",
            title:WtfGlobal.getLocaleText("hrms.performance.goal.management"),//"Goal Management",
            layout:'fit',
            border:false,
            closable:true/*,
                iconCls:getTabIconCls(Wtf.etype.hrms)*/
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();

}

function myGoals()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var mgdemoTab=Wtf.getCmp("mygoals");
    if(mgdemoTab==null)
    {
        mgdemoTab=new Wtf.myGoals({
            id:"mygoals",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.performance.my.goals.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.performance.my.goals")+"</div>",//"<div wtf:qtip='View the goals assigned to you by your manager.'>My Goals</div>",
            layout:'fit',
            border:false,
            // closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsmygoals)
        });
        main.add(mgdemoTab);
    }
    main.setActiveTab(mgdemoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function Rejectedapps(){
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edsignupTab=Wtf.getCmp("rejected");
    if(edsignupTab==null)
    {
        edsignupTab=new Wtf.rejectedApps({
            id:"rejected",
            title:WtfGlobal.getLocaleText("hrms.recruitment.rejected.applications"),//"Rejected Applications",
            layout:'fit',
            border:false,
            //  closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsrejectapps)
        });
        main.add(edsignupTab);
    }
    main.setActiveTab(edsignupTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function allapps(internal, searchId, moduleId){
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    
    var edsignupTab=Wtf.getCmp("allappls");
    var edsignupTab1=Wtf.getCmp("rejected");
    var edsignupTab2=Wtf.getCmp("qualified");
    var edsignupTab3=Wtf.getCmp("applicants");

    if(edsignupTab==null)
    {
        edsignupTab=new Wtf.allApps({
            id:"allappls",
            title:WtfGlobal.getLocaleText("hrms.recruitment.pending.applications"),//"Pending Applications",
            layout:'fit',
            border:false,
            isInternal:internal,
            scope:this,
            searchid:searchId,
            iconCls:getTabIconCls(Wtf.etype.hrmsaddapps)
        });
        edsignupTab1=new Wtf.rejectedApps({
            id:"rejected",
            title:WtfGlobal.getLocaleText("hrms.recruitment.rejected.applications"),//"Rejected Applications",
            layout:'fit',
            border:false,
            scope:this,
            searchid:searchId,
            iconCls:getTabIconCls(Wtf.etype.hrmsrejectapps)
        });

        edsignupTab2=new Wtf.qualifiedApps({
            id:"qualified",
            title:WtfGlobal.getLocaleText("hrms.recruitment.selected.applications"),//"Selected Applications",
            layout:'fit',
            border:false,
            searchid:searchId,
            iconCls:getTabIconCls(Wtf.etype.hrmsviewapps)
        });

        edsignupTab3=new Wtf.appsList({
            id:"applicants",
            title:WtfGlobal.getLocaleText("hrms.recruitment.external.applicants.list"),//"External Applicants List",
            scope:this,
            border:false,
            layout:'fit',
            searchid:searchId,
            iconCls:getTabIconCls(Wtf.etype.hrmsapplicantlist)
        });

        main.add(edsignupTab);
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.rejectedapps, Wtf.Perm.rejectedapps.view)){
            main.add(edsignupTab1);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.qualifiedapps, Wtf.Perm.qualifiedapps.view)){
            main.add(edsignupTab2);
            main.add(edsignupTab3);
        }
    }
    main.setActiveTab(edsignupTab);
    main.doLayout();

    

    if(searchId!=undefined){ // For Saved Searches to show Advenced Search component
        
        if(moduleId==3){
            edsignupTab.searchid = searchId;
            edsignupTab.configurAdvancedSearch();
        } else if(moduleId==4){
            edsignupTab1.searchid = searchId;
            main.setActiveTab(edsignupTab1);
            edsignupTab1.configurAdvancedSearch();

        } else if(moduleId==5){
            edsignupTab2.searchid = searchId;
            main.setActiveTab(edsignupTab2);
            edsignupTab2.configurAdvancedSearch();

        } else if(moduleId==6){
            edsignupTab3.searchid = searchId;
            main.setActiveTab(edsignupTab3);
            edsignupTab3.configurAdvancedSearch();

        }
            
    }
    
    Wtf.getCmp("as").doLayout();
}

function prerequisites(){
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edsignupTab=Wtf.getCmp("prerequisites");
    if(edsignupTab==null)
    {
        edsignupTab=new Wtf.jobProfile({
            id:"prerequisites",
            title:WtfGlobal.getLocaleText("hrms.common.DesignationPrerequisites"),
            layout:'fit',
            border:false,
            //closable:true,
            autoscroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsdesignation)
        });
        main.add(edsignupTab);
    }
    main.setActiveTab(edsignupTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function testing(what)
{
    //alert(what);
    var main=Wtf.getCmp("as");
    var mgdemoTab=Wtf.getCmp("mastertabpanel");
    if(mgdemoTab==null)
    {
        mgdemoTab=new Wtf.MasterTabPanel({
            id:"mastertabpanel",
            title:WtfGlobal.getLocaleText("hrms.performance.management"),//"Performance Management",
            layout:'fit',
            border:false,
            closable:true/*,
                iconCls:getTabIconCls(Wtf.etype.hrms)*/
        });
        main.add(mgdemoTab);

    }
    main.setActiveTab(mgdemoTab);
    Wtf.getCmp('goalmanagementtabpanel').setActiveTab(Wtf.getCmp(what));
    Wtf.getCmp('goalmanagementtabpanel').doLayout();
    main.doLayout();


}

function qualified(){
    internaljobs();
    var main=Wtf.getCmp("recruitmentmanage");
    var edsignupTab=Wtf.getCmp("qualified");
    if(edsignupTab==null)
    {
        edsignupTab=new Wtf.qualifiedApps({
            id:"qualified",
            title:WtfGlobal.getLocaleText("hrms.recruitment.qualified.applications"),//"Qualified Applications",
            layout:'fit',
            border:false,
            // closable:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsviewapps)
        });
        main.add(edsignupTab);
    }
    main.setActiveTab(edsignupTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}




function PayrollManagement(companyname,companyid)
{
    var mainTabId = Wtf.getCmp("as");
    var AuditTrail=Wtf.getCmp("payrollmanagementtab");
    if(AuditTrail==null){
        AuditTrail =new Wtf.TempEmpMaster({
            title:WtfGlobal.getLocaleText("hrms.payroll.management"),
            iconCls: getTabIconCls(Wtf.etype.payroll),
            companyid:companyid,
            id: "payrollmanagementtab",
            companyname:companyname,
            closable:true,
            border:false,
            layout:'fit',
            scope:this
        });
        mainTabId.add(AuditTrail);
    }
    mainTabId.setActiveTab(Wtf.getCmp("payrollmanagementtab"));
    mainPanel.doLayout();

}
function TemplateManagement(companyname,companyid)
{
    var mainTabId = Wtf.getCmp("as");
    var AuditTrail=Wtf.getCmp("templatemanagementtab");
    if(AuditTrail==null){
        AuditTrail =new Wtf.PayrollGroupTemplate({
            title:WtfGlobal.getLocaleText("hrms.payroll.templatemanagement"),
            iconCls: getTabIconCls(Wtf.etype.acc),  
            companyid:companyid,
            id: "templatemanagementtab",
            companyname:companyname,
            closable:true,
            border:false,
            layout:'fit',
            scope:this
        });
        mainTabId.add(AuditTrail);
    }
    mainTabId.setActiveTab(Wtf.getCmp("templatemanagementtab"));
    mainPanel.doLayout();

}

function masterConfig(){
    var mainTabId = Wtf.getCmp("as");
    var projectBudget = Wtf.getCmp("PayCompoSetting");
    if(projectBudget == null){
        projectBudget = new Wtf.PayCompoSetting({
            layout:"fit",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.components.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.components")+"</div>",//"<div wtf:qtip='Settings related to payroll components - wage, tax & deduction can be done. Add new component, edit or delete them and also enter their rates.'>Payroll Components</div>",
            closable:true,
            border:false,
            iconCls: getTabIconCls(Wtf.etype.master),
            id:"PayCompoSetting"
        });
        mainTabId.add(projectBudget);
    }
    mainTabId.setActiveTab(projectBudget);
    mainTabId.doLayout();
}

function datePayrollComponentList(){
    var mainTabId = Wtf.getCmp("as");
    var projectBudget = Wtf.getCmp("datePaycomponent");
    if(projectBudget == null){
        projectBudget = new Wtf.datePayCompoSetting({
            layout:"fit",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.components.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.components")+"</div>",
            closable:true,
            border:false,
            iconCls: getTabIconCls(Wtf.etype.master),
            id:"datePaycomponent"
        });
        mainTabId.add(projectBudget);
    }
    mainTabId.setActiveTab(projectBudget);
    mainTabId.doLayout();
}

function SalaryReport(){

    var adminTab = Wtf.getCmp("as");
    var AuditTrail=Wtf.getCmp("genSalaryReport");
    if(AuditTrail==null){
        AuditTrail = new  Wtf.GenSalaryReport({
            id: "genSalaryReport",            
            title : "<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.payroll.salary.report.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.payroll.salary.report")+"</div>",//"<div wtf:qtip='Generate salary reports of the employees and view all taxes and deductions and the final net amount which the employee receives.'>Salary Report</div>",
            layout : 'fit',
            border:false,
            closable: true,
            iconCls: getTabIconCls(Wtf.etype.hrmssalaryreport)
        });
        adminTab.add(AuditTrail);
    }
    adminTab.setActiveTab(AuditTrail);    
    mainPanel.doLayout();
}

function ApproveSalary(){

    var adminTab = Wtf.getCmp("as");
    var AuditTrail=Wtf.getCmp("ApproveSalaryList");
    if(AuditTrail==null){
        AuditTrail = new  Wtf.ApproveSalaryList({
            id: "ApproveSalaryList",
            title : "<div wtf:qtip="+WtfGlobal.getLocaleText("hrms.Dashboard.AuthorizeSalary.tooltip")+">"+WtfGlobal.getLocaleText("hrms.Dashboard.AuthorizeSalary")+"</div>",
            layout : 'fit',
            border:false,
            closable: true,
            iconCls: getTabIconCls(Wtf.etype.hrmssalaryreport)
        });
        adminTab.add(AuditTrail);
    }
    adminTab.setActiveTab(AuditTrail);
    mainPanel.doLayout();
}

function callSystemAdmin(){
    var panel = Wtf.getCmp("systemadmin");
    if(panel==null){
        panel = new Wtf.common.SystemAdmin({
            title : WtfGlobal.getLocaleText("hrms.common.CompanyAdministration") ,
            layout : 'fit',
            id:'systemadmin',
            iconCls:'systemadmin',
            border:false,
            closable:true
        });
        Wtf.getCmp('as').add(panel);
    }
    Wtf.getCmp('as').setActiveTab(panel);
    Wtf.getCmp('as').doLayout();
}

function callCreateCompany(){


    var p = Wtf.getCmp("createcompany");
    if(!p){
        new Wtf.common.CreateCompany({
            title: WtfGlobal.getLocaleText("hrms.common.CreateCompany"),
            id:'createcompany',
            closable: true,
            modal: true,
            iconCls:'systemadmin',
            width: 410,
            height: 370,
            resizable: false,
            layout: 'fit',
            buttonAlign: 'right'
        }).show();
    }

}

function ess()
{
    var main =Wtf.getCmp("as");
    var mainTab=Wtf.getCmp("empmanagement");
    if(mainTab==null)
    {

        mainTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.administration.user.administration.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.administration.user.administration")+"</div>",//"<div wtf:qtip='Manage all the users and their details in the system and assign them managers, permissions & reviewers.'>User Administration</div>",
            id:'empmanagement',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsrecruitment)
        });
        main.add(mainTab);
    }
    main.setActiveTab(mainTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function employeemnt(searchId){
    ess();
    var main=Wtf.getCmp("empmanagement");
    var edsignupTab=Wtf.getCmp("empmntgrid");
    if(edsignupTab==null)
    {
        edsignupTab=new Wtf.empmnt({
            id:"empmntgrid",
            title:WtfGlobal.getLocaleText("hrms.administration.user.list"),//"User List",
            layout:'fit',
            border:false,
            searchid:searchId,
            iconCls:getTabIconCls(Wtf.etype.hrmsgoals)
        });
        var exempTab=new Wtf.exemp({
            id:"exempgrid",
            title:WtfGlobal.getLocaleText("hrms.administration.ex.employees"),//"Ex-Employees",
            layout:'fit',
            border:false,
            iconCls:getTabIconCls(Wtf.etype.hrmsgoals)
        });
        main.add(edsignupTab,exempTab);
    }
    main.setActiveTab(edsignupTab);
    main.doLayout();

    if(searchId!=undefined){ // For Saved Searches to show Advenced Search component
        edsignupTab.searchid = searchId;
        edsignupTab.configurAdvancedSearch();
    }

    Wtf.getCmp("as").doLayout();
}

function myProfile()
{
    ess();
    var main=Wtf.getCmp("empmanagement");
    var edsignupTab=Wtf.getCmp("empmntprofile");
    if(edsignupTab==null)
    {  
        var flag=true;
        var blockemployeestoedit = Wtf.cmpPref.blockemployees;
        if(userroleid==1){
            flag=false;
            blockemployeestoedit = false;
        }
               
        edsignupTab=new Wtf.myProfileWindow({
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("hrms.administration.my.profile.tooltip")+"'>"+WtfGlobal.getLocaleText("hrms.administration.my.profile")+"</div>",//"<div wtf:qtip='Make your own personalised profile and also update the details from time to time.'>My Profile</div>",
            id:'empmntprofile',
            editperm:flag,
            blockemployeestoedit:blockemployeestoedit,
            manager:false,
            lid:loginid,
            layout:'fit',
            iconCls:'pwnd myProfileIcon'
        });
        main.add(edsignupTab);
    }
    main.setActiveTab(edsignupTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}

function reviewAppraisal(){
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("reviewAppraisal");
    if(demoTab==null)
    {
         var finalreport="";
         if(Wtf.cmpPref.annmng){
            finalreport=Wtf.AppraisalReport;
        }else{
            finalreport=Wtf.finalReport;
        }
        demoTab=new finalreport({
            id:"reviewAppraisal",
            autoScroll:true,
            title:WtfGlobal.getLocaleText("hrms.performance.review.appraisal"),//"Review Appraisal",
            layout:'fit',
            border:false,
            reviewer:true,
            reviewappraisal:true,
            iconCls:getTabIconCls(Wtf.etype.hrmsreview)
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}
function interviewerPosition(accept){
    Wtf.Ajax.requestEx({
            url:Wtf.req.base + "hrms.jsp",
            params: {
                flag:176,
                acpt:accept
            }
        }, this,
        function(response){
            var resp=eval('('+response+')');
            if(resp.success == true){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),resp.msg], 3);
            } else {
                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),resp.msg], 2, false, 600);
            }
            var a=Wtf.getCmp("DSBMyWorkspaces");
            if(a){
                a.doSearch(a.url,'');
            }
            
            var b=Wtf.getCmp("DSBAlerts");
            if(b){
                b.doSearch(b.url,'');
            }
        },
        function()
        {
        })
}

function addEmailTemplate(a){
    var mainPanel = Wtf.getCmp("as");
    var panel=Wtf.getCmp('emailTemplatedashboard');
    if(panel==null)
    {
        panel=new Wtf.emailTemplate({
            mainTab:this.mainTab,
            id:"emailTemplatedashboard"
        })
        mainPanel.add(panel);
    }else {
       mainPanel.setActiveTab(panel);
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addNewEmailTemplate(a){

    var mainPanel = Wtf.getCmp("as");
    var panel=Wtf.getCmp('template_wiz_win_addnew_dash');
    if(panel==null)
    {
        var tipTitle=WtfGlobal.getLocaleText("hrms.administration.new.template");//"New Template";
        var title = Wtf.util.Format.ellipsis(tipTitle,18);
        panel=new Wtf.newEmailTemplate({
            title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.administration.email.template.tooltip")+"'>"+title+"</div>",//"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Email Template'>"+title+"</div>",
            tipTitle:tipTitle,
            id:'template_wiz_win_addnew_dash',
            closable:true,
            addNewDashboardCall:true

        });
        mainPanel.add(panel);
    }else {
        mainPanel.setActiveTab(panel);
        Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function viewEmpGoals(empid,empname){
	allemployeegoals()
	var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp(empid+"perticularemployeesforgoal");
    if(demoTab==null)
    {
        demoTab=new Wtf.perticularemployeegoals({
            id:empid+"perticularemployeesforgoal",
            title:WtfGlobal.getLocaleText("hrms.performance.assign.goals.to")+" "+empname,//"Assign Goals To "+empname,
            iconCls:getTabIconCls(Wtf.etype.hrmsmygoals),
            layout:'fit',
            border:false,
            closable:true,
            assign:true,
            empid:empid
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}


function timer(){
	this.day=new Date().format('D');      
    var myDate=new Date();
    var myDate1=new Date();
    switch(this.day){
        case 'Sun':
            myDate.setDate(myDate.getDate());
            myDate1.setDate(myDate1.getDate()+6);
            break;
        case 'Mon':
            myDate.setDate(myDate.getDate()-1);
            myDate1.setDate(myDate1.getDate()+5);
            break;
        case 'Tue':
            myDate.setDate(myDate.getDate()-2);
            myDate1.setDate(myDate1.getDate()+4);
            break;
        case 'Wed':
            myDate.setDate(myDate.getDate()-3);
            myDate1.setDate(myDate1.getDate()+3);
            break;
        case 'Thu':
            myDate.setDate(myDate.getDate()-4);
            myDate1.setDate(myDate1.getDate()+2);
            break;
        case 'Fri':
            myDate.setDate(myDate.getDate()-5);
            myDate1.setDate(myDate1.getDate()+1);
            break;
        case 'Sat':
            myDate.setDate(myDate.getDate()-6);
            myDate1.setDate(myDate1.getDate());
            break;
    }
    this.dateArray= new Array();
    this.dateArray.push(myDate.format('Y-m-d'));

    var temp = myDate.clone();
    for(i=0;i<6;i++){
    	temp.setDate(temp.getDate()+1);
        this.dateArray.push(temp.format('Y-m-d'));
    }
    
    calMsgBoxShow(202,4,true);
    Wtf.Ajax.requestEx({
        url: "Timesheet/timerStatus.ts"
    }, this,
    function(response){
    	
        checkForJobTypeStoreLoad();

    	var res=eval('('+response+')');
    	this.StartTimer = new Wtf.StartTimer({
            layout:"fit",
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            title:WtfGlobal.getLocaleText("hrms.timesheet.job.timer"),
            modal:true,
            width:400,
            height:220,
            isFreeText:Wtf.cmpPref.timesheetjob,
            startdate:myDate.format('Y-m-d'),
            enddate:myDate1.format('Y-m-d'),
            colHeader:this.dateArray,
            res:res
        }).show();
    	WtfGlobal.closeProgressbar();
    },
    function(response){
        calMsgBoxShow(82,1);                
    });
}
