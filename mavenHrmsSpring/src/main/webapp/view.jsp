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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title id="Deskerahrmstitle">HRMS</title>
		<script type="text/javascript">
		/*<![CDATA[*/
			function _r(url){ window.top.location.href = url;}
		/*]]>*/
		</script>
<!-- css -->
		<link rel="stylesheet" type="text/css" href="../../lib/resources/css/wtf-all.css"/>
		<link rel="stylesheet" type="text/css" href="../../style/view.css?v=3"/>
                <link rel="stylesheet" type="text/css" href="../../style/ImportWizard.css?v=3"/>
		<link rel="stylesheet" type="text/css" href="../../style/portal.css?v=3"/>
        <link rel = "stylesheet" type = "text/css" href = "../../style/orgChart.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/chart.css"></link>
        <link rel="stylesheet" type="text/css" href="../../style/dashboardstyles.css?v=3"/>
	<!--[if lte IE 6]>
		<link rel="stylesheet" type="text/css" href="../../style/ielte6hax.css" />
	<![endif]-->
	<!--[if IE 7]>
		<link rel="stylesheet" type="text/css" href="../../style/ie7hax.css" />
	<![endif]-->
	<!--[if IE 8]>
		<link rel="stylesheet" type="text/css" href="../../style/ie8hax.css" />
	<![endif]-->
<!-- /css -->
		<link rel="shortcut icon" href="../../images/deskera.png"/>
	</head>
	<body>
		<div id="loading-mask" style="width:100%;height:100%;background:#c3daf9;position:absolute;z-index:20000;left:0;top:0;">&#160;</div>
		<div id="loading">
			<div class="loading-indicator"><img src="../../images/loading.gif" style="width:16px;height:16px; vertical-align:middle" alt="Loading" />&#160;Loading...</div>
		</div>
<!-- js -->
		<script type="text/javascript" src="../../lib/adapter/wtf/wtf-base.js"></script>
		<script type="text/javascript" src="../../lib/wtf-all-debug.js"></script>
		<script type="text/javascript" src="../../props/wtf-lang-locale.js"></script>
        <script type="text/javascript" src="../../props/msgs/messages.js"></script>

		<script type="text/javascript" src="../../scripts/common/WtfKWLJsonReader.js"></script>
                
		<script type="text/javascript" src="../../scripts/WtfGlobal.js"></script>
		<script type="text/javascript" src="../../scripts/WtfSettings.js"></script>
		<script type="text/javascript" src="../../scripts/common/WtfListPanel.js"></script>
		<script type="text/javascript" src="../../scripts/WtfChannel.js"></script>
		<script type="text/javascript" src="../../scripts/WtfMain-ex.js"></script>
        <script type="text/javascript" src="../../scripts/core/WtfBindBase.js"></script>
        <script type="text/javascript" src="../../scripts/core/WtfBind.js"></script>
        <script type="text/javascript" src="../../scripts/WtfCustomPanel.js"></script>
        <script type="text/javascript" src="../../scripts/common/KwlEditorGrid.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/QuickSearch.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/Select.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/KwlGridPanel.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/KWLTagSearch.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfPaging.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/common/WtfPagingMemProxy.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfPagingPlugin.js"></script>
		<script type="text/javascript" src="../../scripts/common/KwlPagingEditorGrid.js"></script>
		<script type="text/javascript" src="../../scripts/common/WtfEditorPaging.js"></script>
		<script type="text/javascript" src="../../scripts/common/WtfEditorPagingPlugin.js"></script>
		<script type="text/javascript" src="../../scripts/common/editorSearch.js"></script>		
		<script type="text/javascript" src="../../scripts/common/WtfGridSummary.js"></script>
                <script type="text/javascript" src="../../scripts/common/WtfLibOverride.js"></script>
                <script type="text/javascript" src="../../scripts/common/WtfGroupSummary.js"></script>
		<script type="text/javascript" src="../../scripts/common/WtfUpdateProfile.js"></script>
		<script type="text/javascript" src="../../scripts/common/WtfCalSettings.js"></script>	
		<script type="text/javascript" src="../../scripts/common/ExportInterface.js"></script>
		<script type="text/javascript" src="../../scripts/common/WtfProjectFeatures.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfCreateUser.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfPermissions.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfUserGrid.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfAdminControl.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfAuditTrail.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/common/WtfGridView.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/DetailPanel.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfAddComment.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/common/WtfComboBox.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/common/comboPlugin.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/WtfGetDocsAndCommentList.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/common/MultiGrouping.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/common/WtfCommonFunction.js"></script>
                <script type="text/javascript" src="../../scripts/common/importInterface.js"></script>
                <script type="text/javascript" src="../../scripts/common/crmcommon/WtfTextField.js"></script>
                <script type="text/javascript" src="../../scripts/common/crmcommon/BufferView.js"></script>
                <script type="text/javascript" src="../../scripts/common/crmcommon/gContact.js"></script>
                <script type="text/javascript" src="../../scripts/common/crmcommon/WtfCrmCommonFunction.js"></script>
                <script type="text/javascript" src="../../scripts/common/crmcommon/WtfClosableTab.js"></script>
                <script type="text/javascript" src="../../scripts/common/crmcommon/WtfNotify.js"></script>
                <script type="text/javascript" src="../../scripts/common/advanceSearch.js"></script>
                <script type="text/javascript" src="../../scripts/common/attributeComponent.js"></script>

		<script type="text/javascript" src="../../scripts/DeskeraHRMS/adminMaster/masterConfiguration.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/adminMaster/AddEditMaster.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/adminMaster/appraisalCycleMaster.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/adminMaster/configanything.js?v=3"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/adminMaster/AddCostCenter.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/editor/newEmailTemplate.js"></script>
		<script type="text/javascript" src="../../scripts/HrmsMain.js"></script>
        <script type="text/javascript" src="../../scripts/GeneratePayrollProcess.js"></script>
        <script type="text/javascript" src="../../scripts/PayrollResourceGrid.js"></script>
        <script type="text/javascript" src="../../scripts/EmployeePayrollData.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/competencyEval.js"></script>		
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/AppraisalManagement.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/assignCompetency.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/CompetencyWindow.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/managecompetencyWindow.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/manageCompetency.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/Allemployeesforgoals.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/finalScore.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/FinalReport.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/goalComment.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/goalforemployee.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/myGoals.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/amchart.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/archivedGoals.js"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/approvalWindow.js"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/AppraisalReport.js"></script>
                <script type="text/javascript" src="../../scripts/editor/campaignDetails.js"></script>
        <!--script type="text/javascript" src="../../scripts/DeskeraHRMS/GoalMasterConfiguration/viewAppraisal.js"></script-->
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/Timesheet/timesheet.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/Timesheet/viewtimesheets.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/Timesheet/AccGridComp.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/Timesheet/WtfAttendanceInfopanel.js"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/Timesheet/TimesheetReport.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/recruitmentjobsearch.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/recruitmentjobstatus.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/CreateApplicant.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/createapplicantForm.js"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/jobProfile.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/ApplicationsList.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/InternalJobBoard.js"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/agencyWindow.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/recruitmentAgencies.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/rejectedapps.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/jobmaster2.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/editprospect.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/allApplications.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/addjobs.js"></script>		
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/interviewwindow.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/editprospect.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/qualifiedapps.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/recruiters.js"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/viewApplicants.js"></script>
        <script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/configRecruitment.js"></script>
        <script type="text/javascript" src="../../scripts/reportBuilder/builder.js"></script>
        <script type="text/javascript" src="../../scripts/reportBuilder/reportForm.js"></script>
        <script type="text/javascript" src="../../scripts/reportBuilder/selectTemplateWin.js"></script>	
        
        <!-- date based payroll js -->
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/TaxDeclarationForm.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/datePayComponentSetting.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/AddPayrollComponent.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/assignComponentWin.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/ResourcePayslip.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/ProcessPayrollWin.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/datePayroll/MyPayslip.js"></script>
        <!-- Organization scripts -->
        <script src="../../scripts/OrgChart/WtfChartNode.js" type="text/javascript"></script>
        <script src="../../scripts/OrgChart/WtfUnmappedContainer.js" type="text/javascript"></script>
        <script src="../../scripts/OrgChart/WtfChartContainer.js" type="text/javascript"></script>
        <script src="../../scripts/OrgChart/WtfChartDragPlugin.js" type="text/javascript"></script>
        <!-- template based payroll js -->
        <script type="text/javascript" src="../../scripts/payroll/AddIncometaxWin.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/PayCompoSetting.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/viewmypayslip.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/GenSalaryReport.js"></script>
		<script type="text/javascript" src="../../scripts/default/WtfDefaultManager.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/AddPayrollTemplate.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/WageEntryForm.js"></script>
                <script type="text/javascript" src="../../scripts/payroll/EmployerContributionForm.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/PayrollGroupTemplate.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/DeducEntryForm.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/UnauthorizePayrollWin.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/MalaysianStatutoryFormApprovalWin.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/MalaysianUserIncomeTaxForm.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/MalaysianUserIncomeTax.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/MalaysianCompanyStatutoryFromDetail.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/TaxEntryForm.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/GeneratePayroll.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/EmpPayslip.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/AddWTDWindow.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/PayslipGrid.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/EditViewTemplate.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/AssignEmployeeFromTemplate.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/TempEmpMaster.js"></script>
		<script type="text/javascript" src="../../scripts/payroll/CPFSetting.js"></script>
                <script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/myProfile.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/employeemanagement.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/assignReviewer.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/fileupload.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/terminationWindow.js?v=3"></script>
		<script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/ex-employee.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/DeskeraHRMS/ESS/employeeReports.js"></script>
        <script type="text/javascript" src="../../scripts/Employee/passwordwindow.js"></script>
		<script type="text/javascript" src="../../scripts/alerts/WtfComAlert.js"></script>
        <script type="text/javascript" src="../../scripts/alerts/ResponseAlert.js"></script>
        <script type="text/javascript" src="../../scripts/editor/compaignEmailTemplate.js"></script>
        <script type="text/javascript" src="../../scripts/payroll/userPayCycleGrid.js"></script>

        <script type="text/javascript" src="../../scripts/DeskeraHRMS/RecruitmentManagement/letterSenderWindow.js"></script>		
        <script type="text/javascript" src="../../scripts/payroll/payrollReports/componentList.js"></script>
		<script type="text/javascript">
		/*<![CDATA[*/
			PostProcessLoad = function(){
				setTimeout(function(){Wtf.get('loading').remove(); Wtf.get('loading-mask').fadeOut({remove: true});}, 250);
				Wtf.EventManager.un(window, "load", PostProcessLoad);
			}
			Wtf.EventManager.on(window, "load", PostProcessLoad);
		/*]]>*/
		</script>
<!-- /js -->
<!-- html -->
		<div id="header" style="position: relative;">
			  <img id="companyLogo" src="http://apps.deskera.com/b/<%=com.krawler.common.util.URLUtil.getDomainName(request)%>/images/store/?company=true" alt="logo"/>
                        <img src="../../images/hrms-right-logo.gif" alt="hrms" style="float:left;margin-left:4px;margin-top:1px;"/>

			<div id="userinfo" class="userinfo">
				<span id="whoami"></span><br /><a id="whoami_sign_out" href="#" onclick="signOut('signout');"></a>&nbsp;&nbsp;<a id="whoami_show_person_profile" href="#" onclick="showPersnProfile();"></a>&nbsp;&nbsp;<a id ="whoami_change_password" href="#" onclick="changepass();"></a>&nbsp;&nbsp;<a id="myProfile" href=# onclick='myProfile()'></a>&nbsp;&nbsp;<a  href="#"  id="organisationlink" onclick="loadOrganizationPage();"></a>
			</div>
			<div id="serchForIco"></div>
			<div id="searchBar"></div>
			<div id="shortcuts" class="shortcuts">
                            <div id="shortcutmenu1"style="float:left !important;position: relative;"></div>
			</div>
		</div>
		<div id='centerdiv'></div>
		<div style="display:none;">
			<iframe id="downloadframe"></iframe>
		</div>
        <input id="cursor_bin" type="text" style="display:none;"/>
<!-- /html -->
	</body>
</html>
