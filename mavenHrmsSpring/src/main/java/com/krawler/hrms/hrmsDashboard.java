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

package com.krawler.hrms;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.database.hrmsDbcon;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.esp.handlers.*;
import com.krawler.esp.handlers.PermissionHandler;
import com.krawler.hql.payroll.payrollManager;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.recruitment.Positionmain;
import com.krawler.hrms.timesheet.Timesheet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import com.krawler.utils.json.base.*;
import com.krawler.utils.json.base.JSONArray;
/**
 *
 * @author krawler
 */
public class hrmsDashboard {
    private static List tabledata;
    private static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getContentDiv(String typeStr) {
        String div = "<div class=\"statuspanelcontentitemouter\"><div class=\"statuspanelcontentiteminner\"><div class=\"statusitemimg " + typeStr + "\">&nbsp; &nbsp;</div><div class=\"statusitemcontent\">";
        return div;
    }

    public static void getSectionHeader(StringBuilder sb, String headerText) {
        sb.append("<div class=\"statuspanelheader\"><span class=\"statuspanelheadertext\">");
        sb.append(headerText);
        sb.append("</span></div>");
    }

    public static void getSectionHeaderGraph(StringBuilder sb, String headerText) {
        sb.append("<div class=\"statuspanelheadergraph\"><span class=\"statuspanelheadertext\">");
        sb.append(headerText);
        sb.append("</span></div>");
    }

    public static String getContentSpan(String textStr) {
        String className = "litetext";
        String span = "<span class=\"" + className + "\">" + textStr + "</span></div></div></div><div class=\"statusclr\"></div>";
        return span;
    }

    private static StringBuilder createHelpSection(String title, String message, String imgPath, String tipMsg) {
        StringBuilder data=new StringBuilder();
        data.append("<h2>"+title+"</h2>");
        data.append("<div>"+message+"</div>");
        data.append("<div class='centered'><img src='"+imgPath+"'></div>");
//        data.append("Tip:"+tipMsg);
        data.append("<div>&nbsp;</div>");
        return data;
    }

     public static String getLink(String message, String functionName) {
        return "<a href=# onclick='"+functionName+"' >"+message+"</a>";
    }

      public static StringBuilder getSuperAdminDashboardList(Session session, HttpServletRequest request) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newadmin = new StringBuilder();

        try {
            newadmin.append("<li>");
            newadmin.append("<a href=# onclick='callSystemAdmin()'>Company Administration</a>");
            newadmin.append("<ul class=\"companylist\">");
            newadmin.append("</ul>");
            newadmin.append("</li>");



        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getSuperAdminDashboardList", e);
        } finally {
            finalString.append(newadmin);
        }

        return finalString;
    }

     public static StringBuilder getSuperAdminDashboardUpdateList(Session session, HttpServletRequest request, StringBuilder sb) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newTask = new StringBuilder();

        try {
            getNewCompany(session, request, newTask);
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getDashboardList", e);
        } finally {
            finalString.append(newTask);
        }

        return finalString;
    }

       public static StringBuilder getNewCompany(Session session, HttpServletRequest request, StringBuilder appendB) throws ServiceException {
        List entityList = null;
        String result = "";

        try {
           String newleadQ = " from Company c where c.deleted=false order by createdOn desc";
            //String newleadQ = " from Company ";
            int dl = 0;
            entityList = HibernateUtil.executeQueryPaging(session, newleadQ, new Integer[]{0, 6});

            dl = entityList.size();
            Iterator ite = entityList.iterator();
            while (ite.hasNext()) {
                String newcomp = "";
                Company comp = (Company) ite.next();
                newcomp += getContentDiv("newcompany");
                newcomp += "<a href=# onclick='callSystemAdmin()' >" + comp.getCompanyName();
                if(comp.getCreator()!=null )
                {
                  newcomp += " - [ " + comp.getCreator().getFirstName() + " ( " + comp.getCreator().getUserLogin().getUserName() + " ) ] </a> ";
                  if(comp.getCreatedOn()!=null)
                  {
                  newcomp += "   created on " + AuthHandler.getDateFormatter(request).format(comp.getCreatedOn());
                  }
                 }
                else{
                  newcomp += " - [ "+ "admin "+ " ( " + "admin " + " ) ] </a> ";
                }



                result += getContentSpan(newcomp);
            }
//
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getNewCompany", e);
        } finally {
            appendB.append(result);
        }

        return appendB;
    }


    public static StringBuilder getDashboardUpdateList(Session session, HttpServletRequest request, StringBuilder sb) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newTask = new StringBuilder();
        try {
            String userid = AuthHandler.getUserid(request);            
              if ((PermissionHandler.isEmployee(session,request ))||(PermissionHandler.isManager(session,request ))) {
                getemployeeupdates(session, request, newTask);

            }else{
                getadminupdates(session, request, newTask);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getDashboardList", e);
        } finally {
            finalString.append(newTask);
        }
        return finalString;
    }

        public static StringBuilder getHRMSauditLinks(Session session, HttpServletRequest request) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();

        try {

                newLink.append("<li>");
                newLink.append("<a href=# onclick='auditTrail()'>Audit Trail</a>");
                newLink.append("<ul class=\"auditlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");


        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmsauditLinks", e);
        } finally {
            finalString = newLink;
        }

        return finalString;
    }

       public static StringBuilder getHRMSadministrationLinks(Session session, HttpServletRequest request,JSONObject perms) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();

        try {
                if(PermissionHandler.isPermitted(perms, "useradmin", "view"))
                {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='employeemnt()' wtf:qtip=\"Manage all the users and their details in the system and assign them managers, permissions & reviewers.\">User Administration</a>");
                newLink.append("<ul class=\"userlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
                }

                 if(PermissionHandler.isPermitted(perms, "masterconf", "view"))
                 {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='loadAdminPage(2)' wtf:qtip='Configure all master settings and sub-fields in the master fields, add new fields and sub-fields and also modify the company preferences.'>Master Configuration</a>");
                newLink.append("<ul class=\"masterlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
                 }

             if(PermissionHandler.isPermitted(perms, "setappcycle", "view"))
       {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='ConfigAppraisalCycleMaster()' wtf:qtip='Configure all appraisal cycles and its start date and end date.'>Set Appraisal Cycle</a>");
                newLink.append("<ul class=\"auditlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
                
//                 if(PermissionHandler.isPermitted(perms, "companyadmin", "view"))
//                 {
//                newLink.append("<li>");
//                newLink.append("<a href=# onclick='loadAdminPage(3)'>Company Administration</a>");
//                newLink.append("<ul class=\"companylist\">");
//                newLink.append("</ul>");
//                newLink.append("</li>");
//                 }
                 if(PermissionHandler.isPermitted(perms, "audittrail", "view"))
                 {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='auditTrail()' wtf:qtip='Helps you to keep a track of all user activities performed on the system at any point of time and can also locate their place of occurrence through the IP Address.'>Audit Trail</a>");
                newLink.append("<ul class=\"auditlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
                 }

        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmsadministrationLinks", e);
        } finally {
            finalString = newLink;
        }

        return finalString;
    }

   public static String getHRMSadministrationLinks(HttpServletRequest request,JSONObject perms) throws ServiceException {
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
            if(PermissionHandler.isPermitted(perms, "useradmin", "view")) {
                tempJObj.put("name", "User Administration");
                tempJObj.put("onclick", "employeemnt()");
                tempJObj.put("img","../../images/user-administration.png");
                tempJObj.put("qtip","Manage all the users and their details in the system and assign them managers, permissions & reviewers.");
                jArr.put(tempJObj);
            }

            if(PermissionHandler.isPermitted(perms, "masterconf", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Master Configuration");
                tempJObj.put("onclick", "loadAdminPage(2)");
                tempJObj.put("img","../../images/master-configuration.png");
                tempJObj.put("qtip","Configure all master settings and sub-fields in the master fields, add new fields and sub-fields and also modify the company preferences.");
                jArr.put(tempJObj);
            }

            if(PermissionHandler.isPermitted(perms, "setappcycle", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Set Appraisal Cycle");
                tempJObj.put("onclick", "ConfigAppraisalCycleMaster()");
                tempJObj.put("img","../../images/set-appraisal-cycle.png");
                tempJObj.put("qtip","Configure all appraisal cycles and its start date and end date.");
                jArr.put(tempJObj);
            }

            if(PermissionHandler.isPermitted(perms, "audittrail", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Audit Trail");
                tempJObj.put("onclick", "auditTrail()");
                tempJObj.put("img","../../images/audit-trail.png");
                tempJObj.put("qtip","Helps you to keep a track of all user activities performed on the system at any point of time and can also locate their place of occurrence through the IP Address.");
                jArr.put(tempJObj);
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            finalString = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getHRMSadministrationLinks", e);
        } finally {
        }

        return finalString;
    }

   public static String getHRMSrecruitmentLinks(HttpServletRequest request,JSONObject perms) throws ServiceException {
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
           if(PermissionHandler.isPermitted(perms, "externaljobs", "view")) {
                tempJObj.put("name", "Add Jobs");
                tempJObj.put("onclick", "AddJobs2()");
                tempJObj.put("img","../../images/Add-jobs.png");
                tempJObj.put("qtip","You can set up and access job positions, job descriptions, and approve authority for a particular job.");
                jArr.put(tempJObj);
            }
            if(PermissionHandler.isPermitted(perms, "agencies", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Recruitment Agencies");
                tempJObj.put("onclick", "Recruitagencies()");
                tempJObj.put("img","../../images/manage-recruitment-agencies.png");
                tempJObj.put("qtip","Enter all details of recruitment agencies including their contact details and manage their information efficiently. Assign jobs to the recruitment agency & view the jobs assigned.");
                jArr.put(tempJObj);
            }
            if(PermissionHandler.isPermitted(perms, "allapps", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "All Applications");
                tempJObj.put("onclick", "allapps()");
                tempJObj.put("img","../../images/all-application.png");
                tempJObj.put("qtip","View all applications of candidates from different fields & their details. View the status of applications and take actions accordingly.");
                jArr.put(tempJObj);

           }
            if(PermissionHandler.isPermitted(perms, "recruiters", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Assign Interviewer");
                tempJObj.put("onclick", "recruiters()");
                tempJObj.put("img","../../images/Assign-interviewer.png");
                tempJObj.put("qtip","Assign interviewers to screen candidates on the basis of their merit. Interviewers are assigned on the basis of their professional knowledge.");
                jArr.put(tempJObj);
            }
            if (PermissionHandler.isPermitted(perms, "internaljobboard", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Internal Job Board");
                tempJObj.put("onclick", "internaljobBoard1()");
                tempJObj.put("img","../../images/internal-job-board.png");
                tempJObj.put("qtip","Employees can apply to internal jobs if there is a vacancy.");
                jArr.put(tempJObj);
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            finalString = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getHRMSrecruitmentLinks", e);
        } finally {
        }
        return finalString;
    }

   public static String getHRMSpayrollLinks(HttpServletRequest request,JSONObject perms) throws ServiceException {
        String finalString = "";
        Session session = null;
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
            session = HibernateUtil.getCurrentSession();
            if (PermissionHandler.isPermitted(perms, "payroll", "view")) {
                tempJObj.put("name", "Payroll Components");
                tempJObj.put("onclick", "masterConfig()");
                tempJObj.put("img","../../images/component-setting.png");
                tempJObj.put("qtip","Settings related to payroll components - wage, tax & deduction can be done. Add new component, edit or delete them and also enter their rates.");
                jArr.put(tempJObj);

                tempJObj = new JSONObject();
                tempJObj.put("name", "Payroll Management");
                tempJObj.put("onclick", "PayrollManagement()");
                tempJObj.put("img","../../images/payroll-management.png");
                tempJObj.put("qtip","Define the salary structure of employees. Assign a combination of wages, taxes and deductions to payroll templates. Employees are linked to payroll templates based on designations and pay scales.");
                jArr.put(tempJObj);

                tempJObj = new JSONObject();
                tempJObj.put("name", "Salary Report");
                tempJObj.put("onclick", "SalaryReport()");
                tempJObj.put("img","../../images/salary-report.png");
                tempJObj.put("qtip","Generate salary reports of the employees and view all taxes and deductions and the final net amount which the employee receives.");
                jArr.put(tempJObj);
            }
            
            if (PermissionHandler.isPermitted(perms, "mypayslip", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "My Payslip");
                tempJObj.put("onclick", "viewmypayslip()");
                tempJObj.put("img","../../images/view-my-payslip.png");
                tempJObj.put("qtip","Gives you a comprehensive data on the employee work duration to ensure that appropriate salary is paid to the employee based on his/her performance.");
                jArr.put(tempJObj);
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            finalString = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getHRMSpayrollLinks", e);
        } finally {
             HibernateUtil.closeSession(session);
        }
        return finalString;
    }

    public static String getHRMStimesheetLinks(HttpServletRequest request,JSONObject perms) throws ServiceException {
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
             if(PermissionHandler.isPermitted(perms, "viewtimesheet", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Approve Timesheets");
                tempJObj.put("onclick", "viewtimesheet()");
                tempJObj.put("img","../../images/approve-timesheets.png");
                tempJObj.put("qtip","Allows the administrator/ manager to review and approve submitted time sheets of his employees.");
                jArr.put(tempJObj);
            }
            if(PermissionHandler.isPermitted(perms, "mytimesheet", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "My Timesheet");
                tempJObj.put("onclick", "timesheet()");
                tempJObj.put("img","../../images/my-timesheet.png");
                tempJObj.put("qtip","The employees enter the hours for tasks performed for a specific period of time.");
                jArr.put(tempJObj);
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            finalString = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmstimesheetLinks", e);
        } finally {
        }
        return finalString;
    }

    public static String getHRMSperformanceLinks(HttpServletRequest request, JSONObject perms) throws ServiceException {
        String finalString = "";
        JSONObject tempJObj = new JSONObject();
        JSONArray jArr = new JSONArray();
        Session session = null;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        try {
            session = HibernateUtil.getCurrentSession();
            if ((!PermissionHandler.isEmployee(session,request ))&&(!PermissionHandler.isManager(session,request ))) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Manage Competencies");
                tempJObj.put("onclick", "configCompetency()");
                tempJObj.put("img","../../images/manage-competencies.png");
                tempJObj.put("qtip","Define and link key competencies alongside employee's job architecture and one can also add, edit or delete the competencies in the competency master.");
                jArr.put(tempJObj);
            }
            if (hrmsManager.checkModule("goal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "assigngoals", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", "Goal Setting");
                    tempJObj.put("onclick", "allemployeegoals()");
                    tempJObj.put("img","../../images/goal-setting.png");
                    tempJObj.put("qtip","The manager can frame realistic goals which can be weighed and scored and pass them on to their employees depending on their role in the organization.");
                    jArr.put(tempJObj);
                }
            }
            if (hrmsManager.checkModule("goal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "mygoals", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", "My Goals");
                    tempJObj.put("onclick", "myGoals()");
                    tempJObj.put("img","../../images/my-goals.png");
                    tempJObj.put("qtip","The employees can enter their goals with their description, weightage, start & due date and the person assigning it.");
                    jArr.put(tempJObj);
                }
            }
            if (PermissionHandler.isPermitted(perms, "initiateappraisal", "view")) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Initiate Appraisal");
                tempJObj.put("onclick", "InitiateAppraisal()");
                tempJObj.put("img","../../images/initiate-appraisal.png");
                tempJObj.put("qtip","Helps you initiate performance appraisal process for your employees. Performance of employees is assessed by their respective managers.");
                jArr.put(tempJObj);
                
            }
            if (hrmsManager.showappraisalForm(false,session, request)) {
                tempJObj = new JSONObject();
                tempJObj.put("name", "Appraise Others");
                tempJObj.put("onclick", "competencyedit()");
                tempJObj.put("img","../../images/appraisal-form.png");
                tempJObj.put("qtip","Personalized form to evaluate employee performance based on their goals and competencies.");
                jArr.put(tempJObj);
            }
            if(hrmsHandler.isReviewer(session, request)&&hrmsManager.checkModule("reviewappraisal", session, request)){
                tempJObj = new JSONObject();
                tempJObj.put("name", "Review Appraisal");
                tempJObj.put("onclick", "reviewAppraisal()");
                tempJObj.put("img","../../images/review-appraisal.png");
                tempJObj.put("qtip","Review Appraisal");
                jArr.put(tempJObj);
            }

            if (hrmsManager.checkModule("appraisal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "myappraisalform", "view") && hrmsManager.showappraisalForm(true,session, request)) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", "My Appraisal Form");
                    tempJObj.put("onclick", "myAppraisal()");
                    tempJObj.put("img","../../images/appraisal-form.png");
                    tempJObj.put("qtip","Personalized form to evaluate employee performance based on their goals and competencies.");
                    jArr.put(tempJObj);
                }
            }
            
            if (hrmsHandler.isReviewer(session, request)) {
                if (PermissionHandler.isPermitted(perms, "finalscore", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", "Appraisal Report");
                    tempJObj.put("onclick", "finalReport()");
                    tempJObj.put("img","../../images/appraisal-report.png");
                    tempJObj.put("qtip","View consolidated anonymous appraisals for others");
                    jArr.put(tempJObj);
                }
            }
            if (hrmsManager.checkModule("appraisal", session, request)) {
                Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, fmt.format(new Date()),AuthHandler.getUserid(request));
                String qry = "from Appraisalmanagement where employee.userID=? and appcycle.submitenddate<? and appcycle.cycleapproval=?";
                List tabledata1 = HibernateUtil.executeQuery(session, qry, new Object[]{AuthHandler.getUserid(request), userdate,true});
                if (!tabledata1.isEmpty()) {
                    if (PermissionHandler.isPermitted(perms, "myfinalscore", "view")) {
                        tempJObj = new JSONObject();
                        tempJObj.put("name", "My Appraisal Report");
                        tempJObj.put("onclick", "myfinalReport()");
                        tempJObj.put("img", "../../images/appraisal-report.png");
                        tempJObj.put("qtip", "View consolidated anonymous appraisals by others for me");
                        jArr.put(tempJObj);
                    }
                }
            }
            if (hrmsManager.checkModule("goal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "archivegoals", "view")) {
                    tempJObj = new JSONObject();
                    tempJObj.put("name", "Archived Goals");
                    tempJObj.put("onclick", "archivedgoals()");
                    tempJObj.put("img","../../images/archived-goals.png");
                    tempJObj.put("qtip","Archive your goals which are completed/ used in the appraisal process/ no longer pertinent.");
                    jArr.put(tempJObj);
                }
            }
            JSONObject jobj = new JSONObject();
            jobj.put("data", jArr);
            finalString = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmsperformanceLinks", e);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return finalString;
    }
    
      public static StringBuilder getHRMStimesheetLinks(Session session, HttpServletRequest request,JSONObject perms) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();

        try {
             if(PermissionHandler.isPermitted(perms, "viewtimesheet", "view")){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='viewtimesheet()' wtf:qtip='Allows the administrator/ manager to review and approve submitted time sheets of his employees.'>Approve Timesheets</a>");
                newLink.append("<ul class=\"timeshhetlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
              if(PermissionHandler.isPermitted(perms, "mytimesheet", "view")){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='timesheet()' wtf:qtip='The employees enter the hours for tasks performed for a specific period of time.'>My Timesheet</a>");
                newLink.append("<ul class=\"timeshhetlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
              }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmstimesheetLinks", e);
        } finally {
            finalString = newLink;
        }

        return finalString;
    }

    public static StringBuilder getHRMSrecruitmentLinks(Session session, HttpServletRequest request,JSONObject perms) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();

        try {
           if(PermissionHandler.isPermitted(perms, "externaljobs", "view")){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='AddJobs2()' wtf:qtip='You can set up and access job positions, job descriptions, and approve authority for a particular job.'>Add Jobs</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
            if(PermissionHandler.isPermitted(perms, "agencies", "view")){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='Recruitagencies()' wtf:qtip='Enter all details of recruitment agencies including their contact details and manage their information efficiently. Assign jobs to the recruitment agency & view the jobs assigned.'>Manage Recruitment Agencies</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
            if(PermissionHandler.isPermitted(perms, "allapps", "view")){
                newLink.append("</li>");
                newLink.append("<li>");
                newLink.append("<a href=# onclick='allapps()' wtf:qtip='View all applications of candidates from different fields & their details. View the status of applications and take actions accordingly.'>All Applications</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
           }     
            /*if(PermissionHandler.isPermitted(perms, "designationpre", "view")){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='prerequisites()'>Designation Prerequisites</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }*/
            if(PermissionHandler.isPermitted(perms, "recruiters", "view")){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='recruiters()' wtf:qtip='Assign interviewers to screen candidates on the basis of their merit. Interviewers are assigned on the basis of their professional knowledge.'>Assign Interviewer</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
            if (PermissionHandler.isPermitted(perms, "internaljobboard", "view")) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='internaljobBoard1()' wtf:qtip='Employees can apply to internal jobs if there is a vacancy.'>Internal Job Board</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
            /*if (PermissionHandler.isPermitted(perms, "createapplicant", "view")) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='applicantlist()' wtf:qtip='Create new applicants, edit or delete the existing applicants and can also view the job status.'>Create Applicant</a>");
                newLink.append("<ul class=\"recruitmentlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }*/
             

        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmsRecruitmentLinks", e);
        } finally {
            finalString = newLink;
        }

        return finalString;
    }


    public static StringBuilder getHRMSperformanceLinks(Session session, HttpServletRequest request, JSONObject perms) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();
        try {
            if (PermissionHandler.isPermitted(perms, "competencymanagement", "view")) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='configCompetency()' wtf:qtip=\"Define and link key competencies alongside employee's job architecture and one can also add, edit or delete the competencies in the competency master.\">Manage Competencies</a>");
                newLink.append("<ul class=\"performancelist\">");
                newLink.append("</ul>");
            }
            if (hrmsManager.checkModule("goal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "assigngoals", "view")) {
                    newLink.append("<li>");
                    newLink.append("<a href=# onclick='allemployeegoals()' wtf:qtip='The manager can frame realistic goals which can be weighed and scored and pass them on to their employees depending on their role in the organization.'>Goal Setting</a>");
                    newLink.append("<ul class=\"performancelist\">");
                    newLink.append("</ul>");
                }
            }
            if (hrmsManager.checkModule("goal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "mygoals", "view")) {
                    newLink.append("<li>");
                    newLink.append("<a href=# onclick='myGoals()' wtf:qtip='The employees can enter their goals with their description, weightage, start & due date and the person assigning it.'>My Goals</a>");
                    newLink.append("<ul class=\"performancelist\">");
                    newLink.append("</ul>");
                    newLink.append("</li>");
                }
            }
            if (PermissionHandler.isPermitted(perms, "initiateappraisal", "view")) {
                newLink.append("</li>");
                newLink.append("<li>");
                newLink.append("<a href=# onclick='InitiateAppraisal()' wtf:qtip='Helps you initiate performance appraisal process for your employees. Performance of employees is assessed by their respective managers.'>Initiate Appraisal</a>");
                newLink.append("<ul class=\"performancelist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
            if (hrmsManager.isAppraiser(AuthHandler.getUserid(request), session, request)) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='competencyedit()' wtf:qtip='Personalized form to evaluate employee performance based on their goals and competencies. '>Appraisal Form</a>");
                newLink.append("<ul class=\"performancelist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }
            if(hrmsHandler.isReviewer(session, request)){
                newLink.append("<li>");
                newLink.append("<a href=# onclick='reviewAppraisal()'>Review Appraisal</a>");
                newLink.append("<ul class=\"performancelist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }

                if (hrmsManager.checkModule("appraisal", session, request)) {
                    if (PermissionHandler.isPermitted(perms, "myappraisalform", "view")) {
                        newLink.append("<li>");
                        newLink.append("<a href=# onclick='myAppraisal()' wtf:qtip='Personalized form to evaluate employee performance based on their goals and competencies.'>My Appraisal Form</a>");
                        newLink.append("<ul class=\"performancelist\">");
                        newLink.append("</ul>");
                        newLink.append("</li>");
                    }
                }
            if (hrmsHandler.isReviewer(session, request)) {
                if (PermissionHandler.isPermitted(perms, "finalscore", "view")) {
                    newLink.append("<li>");
                    newLink.append("<a href=# onclick='finalReport()' wtf:qtip=\"Generate an appraisal report listing performance details so that effective appraisal of the employee could be done.\">Appraisal Report</a>");
                    newLink.append("<ul class=\"performancelist\">");
                    newLink.append("</ul>");
                    newLink.append("</li>");
                }
            }
            if (hrmsManager.checkModule("appraisal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "myfinalscore", "view")) {
                    newLink.append("<li>");
                    newLink.append("<a href=# onclick='myfinalReport()' wtf:qtip=\"Generate an appraisal report listing performance details so that effective appraisal of the employee could be done.\">My Appraisal Report</a>");
                    newLink.append("<ul class=\"performancelist\">");
                    newLink.append("</ul>");
                    newLink.append("</li>");
                }
            }
            if (hrmsManager.checkModule("goal", session, request)) {
                if (PermissionHandler.isPermitted(perms, "archivegoals", "view")) {
                    newLink.append("<li>");
                    newLink.append("<a href=# onclick='archivedgoals()' wtf:qtip='Archive your goals which are completed/ used in the appraisal process/ no longer pertinent.'>Archived Goals</a>");
                    newLink.append("<ul class=\"performancelist\">");
                    newLink.append("</ul>");
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmsperformanceLinks", e);
        } finally {
            finalString = newLink;
        }
        return finalString;
    }

       public static StringBuilder getHRMScompensationLinks(Session session, HttpServletRequest request,JSONObject perms) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();

        try {

               if (PermissionHandler.isPermitted(perms, "compensationmanage", "view")) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='compensationFunction()'>Compensation Management</a>");
                newLink.append("<ul class=\"compensationlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
               }
              if (PermissionHandler.isPermitted(perms, "compensationrecord", "view")) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='compensationRecFunction()'>Compensation Record</a>");
                newLink.append("<ul class=\"compensationlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
              }

                 if (PermissionHandler.isPermitted(perms, "mycompensationrecord", "view")) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='mycompensationRecFunction()'>My Compensation Record</a>");
                newLink.append("<ul class=\"compensationlist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }

        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmscompensationLinks", e);
        } finally {
            finalString = newLink;
        }

        return finalString;
    }

      public static StringBuilder getHRMSpayrollLinks(Session session, HttpServletRequest request,JSONObject perms) throws ServiceException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newLink = new StringBuilder();

        try {
            if ((!PermissionHandler.isEmployee(session,request ))) {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='masterConfig()' wtf:qtip='Settings related to payroll components - wage, tax & deduction can be done. Add new component, edit or delete them and also enter their rates.'>Payroll Component Setting</a>");
                newLink.append("<ul class=\"payrolllist\">");
                newLink.append("</ul>");
                newLink.append("</li>");

                newLink.append("<li>");
                newLink.append("<a href=# onclick='PayrollManagement()' wtf:qtip='Define the salary structure of employees. Assign a combination of wages, taxes and deductions to payroll templates. Employees are linked to payroll templates based on designations and pay scales.'>Payroll Management</a>");
                newLink.append("<ul class=\"payrolllist\">");
                newLink.append("</ul>");
                newLink.append("</li>");

                newLink.append("<li>");
                newLink.append("<a href=# onclick='SalaryReport()' wtf:qtip='Generate salary reports of the employees and view all taxes and deductions and the final net amount which the employee receives.'>Salary Report</a>");
                newLink.append("<ul class=\"payrolllist\">");
                newLink.append("</ul>");
                newLink.append("</li>");              
            }
            else
            {
                newLink.append("<li>");
                newLink.append("<a href=# onclick='viewmypayslip()' wtf:qtip='Gives you a comprehensive data on the employee work duration to ensure that appropriate salary is paid to the employee based on his/her performance.'>View My Payslip</a>");
                newLink.append("<ul class=\"payrolllist\">");
                newLink.append("</ul>");
                newLink.append("</li>");
            }

        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.gethrmspayrollLinks", e);
        } finally {
            finalString = newLink;
        }

        return finalString;
    }

    public static StringBuilder getemployeeupdates(Session session, HttpServletRequest request, StringBuilder appendB) throws ServiceException {
        String result = "";
        String newleads = "";
        int goallimit = 5;
        List tabledata = null;
        int count = 0;
        String goaldate = "";
        int diff;
        String qry="";
        String hql="";
        Iterator ite=null;
        try {
            String empid = AuthHandler.getUserid(request);
            String companyid = AuthHandler.getCompanyid(request);
            String cmpsub = AuthHandler.getCmpSubscription(request);
            User user=(User) session.load(User.class, AuthHandler.getUserid(request));
            Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, fmt.format(new Date()),empid);
            //String qry = "from Appraisalmanagement where employee.userID=? and employeestatus=0 and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
            if (PermissionHandler.isSubscribed(hrms_Modules.timesheet, cmpsub)) {
                DateFormat formatter = new SimpleDateFormat(AuthHandler.getDateFormat(request));
                Calendar cal = Calendar.getInstance();
                int weekday = cal.get(Calendar.DAY_OF_WEEK);
                cal.add(Calendar.DATE, -weekday + 1);
                Date sdate = cal.getTime();
                cal.add(Calendar.DATE, 6);
                Date edate = cal.getTime();

                String timesheet1 = "from Timesheet where datevalue between ? and ? and  userID.company.companyID=? and userID.userID=? group by approved";
                List timesheet = HibernateUtil.executeQuery(session, timesheet1, new Object[]{sdate, edate, companyid, empid});
                Timesheet tmst = null;
                result = getContentDiv("pwndCommon hrmstimesheet");
                if (!timesheet.isEmpty()) {
                    tmst = (Timesheet) timesheet.get(0);

                    if (tmst.getApproved() == 0) {
                        result += "Your<a href='#' onclick='timesheet()'> Timesheet</a> from <font color='green'>" + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> is <font color='green'>Pending</font> ";
                    } else {
                        result += "Your Timesheet from <font color='green'>" + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> has been <font color='green'>Approved</font> ";
                    }
                } else {
                    result += "Please,Fill<a href='#' onclick='timesheet()'> Timesheet  </a> from<font color='green'> " + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font>";
                }
                newleads += getContentSpan(result);
            }
            if (hrmsManager.isAppraiser(AuthHandler.getUserid(request), session, request)) {
                diff = 0;
                qry = "select distinct appm.appcycle from Appraisalmanagement appm where appm.manager.userID=? and appm.managerstatus=0 and ( appm.appcycle.submitstartdate<= ? and appm.appcycle.submitenddate>=? ) ";
                tabledata = HibernateUtil.executeQuery(session, qry, new Object[]{AuthHandler.getUserid(request),userdate,userdate});
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Appraisalcycle app = (Appraisalcycle) ite.next();
                    diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                    result = getContentDiv("pwndHRMS hrmsappraisal");
                    result += "Submit the <a href='#' onclick='competencyedit()'> appraisal form </a> for appraisal cycle " + app.getCyclename() + " in <font color='green'> " + diff + "</font> day(s)";
                    newleads += getContentSpan(result);
                }
            }
            if (PermissionHandler.isSubscribed(hrms_Modules.appraisal, cmpsub) &&
                    hrmsManager.checkModule("appraisal", session, request)) {
                qry = "select distinct appm.appcycle from Appraisalmanagement appm where appm.employee.userID=? and appm.employeestatus=0 and ( appm.appcycle.submitstartdate<=? and appm.appcycle.submitenddate>=? )";
                Object[] obj = {empid,userdate,userdate};
                tabledata = HibernateUtil.executeQuery(session, qry, obj);
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Appraisalcycle app = (Appraisalcycle) ite.next();
                    diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                    result = getContentDiv("pwndHRMS hrmsappraisal");
                    result += "Fill the <a href='#' onclick='myAppraisal()'> appraisal form </a>in <font color='green'> " + diff + "</font> day(s) for appraisal cycle " + app.getCyclename() + " initiated by " + user.getCompany().getCreator().getFirstName() + " " + user.getCompany().getCreator().getLastName();
                    newleads += getContentSpan(result);
                }
                    qry = "select distinct appm.appcycle, appm from Appraisalmanagement appm where appm.employee.userID=? and appm.reviewstatus=2";
                    tabledata = HibernateUtil.executeQuery(session, qry, new Object[]{AuthHandler.getUserid(request)});
                    ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Object[] row = (Object[]) ite.next();
                        Appraisalmanagement app = (Appraisalmanagement) row[1];
                        if (app.getReviewersubmitdate() != null) {
                            diff = (int) ((userdate.getTime() - app.getReviewersubmitdate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                            if (diff < 10) {
                                result = getContentDiv("pwndHRMS hrmsappraisal");
                                result += "Your <a href='#' onclick='myfinalReport()'>appraisal</a> for appraisal cycle " + app.getAppcycle().getCyclename() + " has been approved";
                                newleads += getContentSpan(result);
                            }
                        }
                    }
//            if(hrmsHandler.isReviewer(session, request)){
//                    diff=0;
//                JSONObject commData=new JSONObject();
//                    qry = "from Assignreviewer where reviewer.userID=?";
//                    tabledata = HibernateUtil.executeQuery(session, qry, AuthHandler.getUserid(request));
//                    ite = tabledata.iterator();
//                    String users = "";
//                    while (ite.hasNext()) {
//                        Assignreviewer log = (Assignreviewer) ite.next();
//                            users += "'" + log.getEmployee().getUserID() + "',";
//                    }
//                    if (users.length() > 0) {
//                        users = users.substring(0, users.length() - 1);
//                    }
//                    qry = "from Appraisalmanagement appm where appm.employee.userID in (" + users + ") and appm.reviewstatus=0 and appm.managerstatus=1  and ( appm.appcycle.submitstartdate<=? and appm.appcycle.submitenddate>=? ) group by appm.appcycle,appm.employee.userID";
//                    tabledata = HibernateUtil.executeQuery(session, qry, new Object[]{userdate,userdate});
//                    ite = tabledata.iterator();
//                    while (ite.hasNext()) {
//                       //Object[] row = (Object[]) ite.next();
//                       Appraisalmanagement app = (Appraisalmanagement) ite.next();
//                       commData=hrmsManager.getAppraisalManagers(session,app.getEmployee().getUserID(),app.getAppcycle().getId(),commData);
//                        if (Integer.parseInt(commData.getString("totalappraisal"))==Integer.parseInt(commData.getString("appraisalsubmitted"))) {
//                            diff = (int) ((app.getAppcycle().getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
//                            result = getContentDiv("pwndHRMS hrmsappraisal");
//                            result += "Review the <a href='#' onclick='reviewAppraisal()'> appraisal </a> for appraisal cycle " + app.getAppcycle().getCyclename() + " in <font color='green'> " + diff + "</font> day(s) for "+app.getEmployee().getFirstName()+" "+app.getEmployee().getLastName();
//                        } else{
//                            result = getContentDiv("pwndHRMS hrmsappraisal");
//                            result += commData.getString("appraisalsubmitted")+" out of "+commData.getString("totalappraisal")+" appraiser(s) have submitted the appraisal form for appraisal cycle "+app.getAppcycle().getCyclename()+" for "+app.getEmployee().getFirstName()+" "+app.getEmployee().getLastName();
//
//                        }
//                        newleads += getContentSpan(result);
//                    }
//                }

                hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? order by createdon desc";
                List goallst = HibernateUtil.executeQuery(session, hql, new Object[]{empid, companyid, false});
                int goalcnt = goallst.size();
                Finalgoalmanagement fgmt = null;
                if (!goallst.isEmpty()) {
                    if (goalcnt < goallimit) {
                        for (int x = 0; x < goalcnt; x++) {
                            fgmt = (Finalgoalmanagement) goallst.get(x);
                            result = getContentDiv("pwndHRMS addgoal");
                            if (fgmt.getCreatedon() != null) {
                                goaldate = AuthHandler.getUserDateFormatter(request, session).format(fgmt.getCreatedon());
                            } else {
                                goaldate = userdate.toString();
                            }
                            result += "<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a> goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + goaldate + "</font>";
                            newleads += getContentSpan(result);
                        }
                    } else {
                        for (int x = 0; x < goallimit; x++) {
                            fgmt = (Finalgoalmanagement) goallst.get(x);
                            result = getContentDiv("pwndHRMS addgoal");
                            if (fgmt.getCreatedon() != null) {
                                goaldate = AuthHandler.getUserDateFormatter(request, session).format(fgmt.getCreatedon());
                            } else {
                                goaldate = AuthHandler.getUserDateFormatter(request, session).format(userdate);
                            }
                            result += "<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a> goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + goaldate + "</font>";
                            newleads += getContentSpan(result);
                        }
                    }

                }
            }
            
            if (PermissionHandler.isSubscribed(hrms_Modules.recruitment, cmpsub) &&
                    PermissionHandler.isManager(session, request)) {
                newleads += getDashboardjobUpdates(session, request);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getemployeeupdates", e);
        } finally {
            appendB.append(newleads);
        }

        return appendB;
    }


      public static StringBuilder getadminupdates(Session session, HttpServletRequest request, StringBuilder appendB) throws ServiceException {        
        String result = "";
        String newleads = "";
        int days;      
        try {
             String cmpsub = AuthHandler.getCmpSubscription(request);
              if (PermissionHandler.isSubscribed(hrms_Modules.payroll, cmpsub)) {
             Calendar cal = Calendar.getInstance();
                days = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DATE);
                result = getContentDiv("pwndHRMS iconWage");
                result += "Salary generation is <font color='green'>" + days + " days</font> due";
                newleads += getContentSpan(result);
              }
                if (PermissionHandler.isSubscribed(hrms_Modules.recruitment, cmpsub)) {
                newleads+=getDashboardjobUpdates(session, request);
                }
             }
         catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getadminupdates", e);
        } finally {
            appendB.append(newleads);
        }

        return appendB;
    }

      public static StringBuilder getGraphs(Session session, HttpServletRequest request,StringBuilder sb) throws ServiceException,SessionExpiredException, JSONException {
        StringBuilder newGraph = new StringBuilder();
        StringBuilder links=new StringBuilder() ;
        String imgpath="../../images/dashboardmsg/";
        try {
              String cmpsub = AuthHandler.getCmpSubscription(request);
              newGraph=checkGraphs(session, request);
              if(!(newGraph.length()>0)){
                   hrmsDashboard.getSectionHeader(sb, "<span style='float: left;margin-top:5px'>Welcome to Deskera HRMS</span><span style='float: right;font-weight:normal'></span>");
                   newGraph.append("<div>");                  
                   links=createHelpSection(""+getLink("Employee management","myProfile()")+"","You can maintain comprehensive information about your employees.",""+imgpath+"myprofile.gif","");
                   newGraph.append(links);
                   if (PermissionHandler.isSubscribed(hrms_Modules.recruitment, cmpsub)) {
                   links=createHelpSection(""+getLink("Add Jobs","AddJobs2()")+"", "You can add job positions, their job descriptions as well as see all added job vacancies in a single list.", ""+imgpath+"addjobs.gif", "");
                   newGraph.append(links);
                   links=createHelpSection(""+getLink("Create Applicant","applicantlist()")+"", "You can add job applicants and set up their detailed profiles.", ""+imgpath+"createapplicant.gif", "");
                   newGraph.append(links);
                   links=createHelpSection(""+getLink("All Applications","allapps()")+"", "You can schedule interviews and select/ reject/on hold job applicants.", ""+imgpath+"allapps.gif", "");
                   newGraph.append(links);
                   }
                    if (PermissionHandler.isSubscribed(hrms_Modules.payroll, cmpsub)) {
                   links=createHelpSection(""+getLink("Payroll","masterConfig()")+"", "You can set wages, deductions and taxes that are used for calculating net pay of an employee.", ""+imgpath+"payroll.gif", "");
                   newGraph.append(links);
                   links=createHelpSection(""+getLink("Add Payroll template","PayrollManagement()")+"", "You can define salary structures of employees by assigning a combination of wages, taxes and deductions to a template.", ""+imgpath+"template.gif", "");
                   newGraph.append(links);
                    }
                   if (PermissionHandler.isSubscribed(hrms_Modules.appraisal, cmpsub) &&
                           hrmsManager.checkModule("goal", session, request)) {
                      links = createHelpSection("" + getLink("Set up goals", "allemployeegoals()") + "", "You can assign personalized goals to your employees and track their progress.", "" + imgpath + "goals.gif", "");
                      newGraph.append(links);
                  }
                   if (PermissionHandler.isSubscribed(hrms_Modules.timesheet, cmpsub)) {
                   links=createHelpSection(""+getLink("Time sheets","timesheet()")+"", "You can track no. of hours spent by your employees on a particular job.", ""+imgpath+"timesheet.gif", "");
                   newGraph.append(links);
                   }
                   newGraph.append("</div>");                    
              }
        } catch (ServiceException e) {
            throw ServiceException.FAILURE("hrmsDashboard.getGraphs", e);
        }catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsDashboard.getGraphs", e);
        }
        finally {
            sb.append(newGraph);
        }
        return sb;
    }

//      public static StringBuilder getHRMSessLinks(Session session, HttpServletRequest request,JSONObject perms) throws ServiceException {
//        StringBuilder finalString = new StringBuilder();
//        StringBuilder newLink = new StringBuilder();
//
//        try {
//              if (PermissionHandler.isPermitted(perms, "myprofile", "view")) {
//                newLink.append("<li>");
//                newLink.append("<a href=# onclick='myProfile()' wtf:qtip='Make your own personalised profile and also update the details from time to time.'>My Profile</a>");
//                newLink.append("<ul class=\"esslist\">");
//                newLink.append("</ul>");
//                newLink.append("</li>");
//              }
////              if (PermissionHandler.isPermitted(perms, "employeemnt", "view")) {
////                newLink.append("<li>");
////                newLink.append("<a href=# onclick='employeemnt()'>Employee Management</a>");
////                newLink.append("<ul class=\"esslist\">");
////                newLink.append("</ul>");
////                newLink.append("</li>");
////              }
//
//        } catch (JSONException e) {
//            throw ServiceException.FAILURE("hrmsDashboard.gethrmsessLinks", e);
//        } finally {
//            finalString = newLink;
//        }
//
//        return finalString;
//    }
       public static StringBuilder checkGraphs(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, JSONException {
        StringBuilder finalString = new StringBuilder();
        StringBuilder newGraph = new StringBuilder();
        String result="";
        String year=null;
        try {       
                     String cmpsub = AuthHandler.getCmpSubscription(request);
                    if (PermissionHandler.isSubscribed(hrms_Modules.appraisal, cmpsub)) {
                        JSONObject jobj = hrmsDbcon.getPerformanceData(13);
                        JSONArray jarr = jobj.getJSONArray("data");
                        jarr = jobj.getJSONArray("data");
                        for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                            String Jsondata = hrmsManager.getChart(jarr.getJSONObject(j).get("name").toString(),session,request);
                            JSONObject obj = new JSONObject(Jsondata);
                            if (obj.getInt("Count") > 0) {
                                result += "true";
                            }
                        }
                        if(result.contains("true")) {
                             newGraph.append("<div id='SalaryPanelHader' style='margin-top:15px;'><font size='3px' align= 'center' color='black' ><b>Employee Appraisal Rating Distribution </b></font></div>");
                             newGraph.append("<div id='WorkOrderMonthlyPanel' class='graphposition'><script>createNewChart('../../scripts/graph/krwcolumn/krwpie/krwpie.swf','krwpie', '100%', '40%', '8', '#FFFFFF', '../../scripts/graph/krwcolumn/examples/LeadsBySource/LeadsBySource_settings.xml','../../jspfiles/chardata.jsp?flag=1', 'WorkOrderMonthlyPanel')</script></div>");
                        }
                    }

              if (PermissionHandler.isSubscribed(hrms_Modules.payroll, cmpsub)) {
                  JSONObject payobj =  payrollManager.getAnnualExpense(year,request);
                  JSONArray jarr1 = payobj.getJSONArray("success");
                  if(StringUtil.equal(jarr1.getString(0).toString(),"true")){
                    KWLCurrency currency=(KWLCurrency)session.load(KWLCurrency.class, AuthHandler.getCurrencyID(request));
                    newGraph.append("<div id='SalaryPanelHader' style='margin-top:15px;'><font size='3px' align= 'center' color='black' ><b>Monthly Payroll Expenditure ("+currency.getName()+") </b></font></div>");
                    newGraph.append("<div id='SalaryPanel' class='graphposition'><script>createNewChart('../../scripts/HRMSGraph/krwcolumn.swf','krwcolumn', '700px', '300px', '8', '#FFFFFF', '../../scripts/HRMSGraph/setting.xml','../../jspfiles/chardata.jsp?flag=2', 'SalaryPanel')</script></div>");
                  }else{
                        newGraph.append("");
                  }
              }

        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsDashboard.checkGraphs", e);
        } finally {
            finalString.append(newGraph);
        }
        return finalString;
    }
        public static String getDashboardjobUpdates(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        String newleads = "";
        int days;
        Date endate;
        try {
//              Date today= new Date();
              String userid = AuthHandler.getUserid(request);
              Date today = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, fmt.format(new Date()),userid);
              String cmp=AuthHandler.getCompanyid(request);
              String hql="from Positionmain where company.companyID=? and createdBy.userID=? and delflag=0";
              List lst = HibernateUtil.executeQuery(session, hql, new Object[]{cmp,userid});
              Calendar cal1 = Calendar.getInstance();
              Calendar cal2 = Calendar.getInstance();
              for(int i=0;i<lst.size();i++){
                  Positionmain p=(Positionmain) lst.get(i);
                  endate=p.getEnddate();
                  cal1.set(endate.getYear(),endate.getMonth(),endate.getDate());
                  cal2.set(today.getYear(),today.getMonth(),today.getDate());
                  days=(int)((cal1.getTimeInMillis()-cal2.getTimeInMillis())/(24.0 * 60 * 60 * 1000));
                  if(days>=0 && days<=2){
                     String alert="" ;
                    result = getContentDiv("pwndHRMS addjobsDashboardIcon");
                    alert= "Job <a href='#' onclick='AddJobs2()'>"+p.getJobid()+":"+p.getPosition().getValue()+"</a> created by you ";
                    if(days==0){
                      alert=alert+"is expiring <font color='green'>today</font>";
                    }else{
                      alert=alert+"is <font color='green'>" + days + " day(s)</font> from expiration";
                    }
                    result+=alert;
                    newleads += getContentSpan(result);
                  }
              }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsDashboard.getadminupdates", e);
        } 
        return newleads;
    }
}
