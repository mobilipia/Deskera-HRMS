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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.common.util.StringUtil" %>
<%@ page import="com.krawler.esp.database.*" %>
<%@ page import="com.krawler.utils.json.base.JSONObject" %>
<%@ page import="com.krawler.utils.json.base.JSONArray" %>
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>

<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%
    Connection conn = null;

    try {
            PreparedStatement pstmt = null;
            ResultSet rs = null;


            String ip = request.getParameter("ip");
            String port = request.getParameter("port");
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            String fromDb = request.getParameter("fromdb");
            //String app = request.getParameter("app");
            String toDb = request.getParameter("todb");
            String companyid = request.getParameter("cid");
            int count=0;



            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" ,user,pass);
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();


            pstmt = conn.prepareStatement("insert into "+toDb+".company (select * from "+fromDb+".company where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in company");

            pstmt = conn.prepareStatement("insert into "+toDb+".CompanyPreferences (select * from "+fromDb+".CompanyPreferences where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in companypreferences");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_Modules (select * from "+fromDb+".hrms_Modules)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_modules");

            pstmt = conn.prepareStatement("insert into "+toDb+".audit_action (select * from "+fromDb+".audit_action)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in audit_action");

            pstmt = conn.prepareStatement("insert into "+toDb+".auditaction (select * from "+fromDb+".auditaction)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in auditaction");

            pstmt = conn.prepareStatement("insert into "+toDb+".auditgroup (select * from "+fromDb+".auditgroup)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in auditgroup");

            pstmt = conn.prepareStatement("insert into "+toDb+".country (select * from "+fromDb+".country)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in country");

            pstmt = conn.prepareStatement("insert into "+toDb+".currency (select * from "+fromDb+".currency)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in currency");

            pstmt = conn.prepareStatement("insert into "+toDb+".dateformat (select * from "+fromDb+".dateformat)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in dateformat");

            pstmt = conn.prepareStatement("insert into "+toDb+".DefaultMasterData (select * from "+fromDb+".DefaultMasterData)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in DefaultMasterData");

            pstmt = conn.prepareStatement("insert into "+toDb+".systemtimezone (select * from "+fromDb+".systemtimezone)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in systemtimezone");

            pstmt = conn.prepareStatement("insert into "+toDb+".timezone (select * from "+fromDb+".timezone)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in timezone");

            pstmt = conn.prepareStatement("insert into "+toDb+".master (select * from "+fromDb+".master)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in master");

            pstmt = conn.prepareStatement("insert into "+toDb+".userlogin (select * from "+fromDb+".userlogin where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in userlogin");

            pstmt = conn.prepareStatement("insert into "+toDb+".users (select * from "+fromDb+".users where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in users");

            pstmt = conn.prepareStatement("insert into "+toDb+".useraccount (select * from "+fromDb+".useraccount where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in useraccount");

            pstmt = conn.prepareStatement("insert into "+toDb+".featurelist (select * from "+fromDb+".featurelist)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in featurelist");

            pstmt = conn.prepareStatement("insert into "+toDb+".activitylist (select * from "+fromDb+".activitylist)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in activitylist");

            pstmt = conn.prepareStatement("insert into "+toDb+".placeholder (select * from "+fromDb+".placeholder)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in placeholder");

            pstmt = conn.prepareStatement("insert into "+toDb+".placeholder_lookup (select * from "+fromDb+".placeholder_lookup)");
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in placeholder_lookup");

            pstmt = conn.prepareStatement("insert into "+toDb+".role (select * from "+fromDb+".role where (company is null or company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in role");

            pstmt = conn.prepareStatement("insert into "+toDb+".userpermission (select * from "+fromDb+".userpermission where role in (select id from "+fromDb+".role where (company is null or company=?)))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in userpermission");

            pstmt = conn.prepareStatement("insert into "+toDb+".MasterData (select * from "+fromDb+".MasterData where (company is null or company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in masterdata");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".agency (select * from "+fromDb+".agency where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in agency");

            pstmt = conn.prepareStatement("insert into "+toDb+".allapplications (select * from "+fromDb+".allapplications where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in allapplications");

            pstmt = conn.prepareStatement("insert into "+toDb+".apiresponse (select * from "+fromDb+".apiresponse where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in apiresponse");

            pstmt = conn.prepareStatement("insert into "+toDb+".positionmain (select * from "+fromDb+".positionmain where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in positionmain");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".applyagency (select * from "+fromDb+".applyagency where agencyid in (select agencyid from "+fromDb+".agency where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in applyagency");

            pstmt = conn.prepareStatement("insert into "+toDb+".mastercmpt (select * from "+fromDb+".mastercmpt where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in mastercmpt");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".managecmpt (select * from "+fromDb+".managecmpt where cmptid in (select cmptid from "+fromDb+".mastercmpt where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in managecmpt");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".finalgoalmanagement (select * from "+fromDb+".finalgoalmanagement where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in finalgoalmanagement");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".appraisalcycle (select * from "+fromDb+".appraisalcycle where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in appraisalcycle");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".appraisalmanagement (select * from "+fromDb+".appraisalmanagement where employee in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in appraisalmanagement");
            
            pstmt = conn.prepareStatement("insert into "+toDb+".appraisal (select * from "+fromDb+".appraisal where appraisal in (select appraisalid from "+fromDb+".appraisalmanagement where employee in (select userid from "+fromDb+".users where company=?)))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in appraisal");

            pstmt = conn.prepareStatement("insert into "+toDb+".assignmanager (select * from "+fromDb+".assignmanager where empid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in assignmanger");

            pstmt = conn.prepareStatement("insert into "+toDb+".assignreviewer (select * from "+fromDb+".assignreviewer where employee in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in assignreviewer");

            pstmt = conn.prepareStatement("insert into "+toDb+".audit_trail (select * from "+fromDb+".audit_trail where user in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in audit_trail");

            pstmt = conn.prepareStatement("insert into "+toDb+".audittrail (select * from "+fromDb+".audittrail where user in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in audittrail");

            pstmt = conn.prepareStatement("insert into "+toDb+".audittrailpayroll (select * from "+fromDb+".audittrailpayroll where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in audittrailpayroll");

            pstmt = conn.prepareStatement("insert into "+toDb+".competencyAvg (select * from "+fromDb+".competencyAvg where employee in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in competencyAvg");

            pstmt = conn.prepareStatement("insert into "+toDb+".configData (select * from "+fromDb+".configData where referenceid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in configData");

            pstmt = conn.prepareStatement("insert into "+toDb+".configrecruitment (select * from "+fromDb+".configrecruitment where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in configrecruitment");

            pstmt = conn.prepareStatement("insert into "+toDb+".configrecruitmentdata (select * from "+fromDb+".configrecruitmentdata where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in configrecruitmentdata");

            pstmt = conn.prepareStatement("insert into "+toDb+".configrecruitmentmaster (select * from "+fromDb+".configrecruitmentmaster where configid in (select configid from "+fromDb+".configrecruitment where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in configrecuitmentmaster");

            pstmt = conn.prepareStatement("insert into "+toDb+".configType (select * from "+fromDb+".configType where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in configType");

            pstmt = conn.prepareStatement("insert into "+toDb+".crm_docs (select * from "+fromDb+".crm_docs where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in crm_docs");

            pstmt = conn.prepareStatement("insert into "+toDb+".crm_docsmap (select * from "+fromDb+".crm_docsmap where docid in (select docid from "+fromDb+".crm_docs where userid in (select userid from "+fromDb+".users where company=?)))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in crm_docsmap");

            pstmt = conn.prepareStatement("insert into "+toDb+".deductionmaster (select * from "+fromDb+".deductionmaster where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in deductionmaster");

            pstmt = conn.prepareStatement("insert into "+toDb+".emailtemplatefiles (select * from "+fromDb+".emailtemplatefiles where creator in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in emailtemplatefiles");

            //pstmt = conn.prepareStatement("insert into "+toDb+".externalapplicants (select * from "+fromDb+".externalapplicants where company=?)");
            //pstmt.setString(1, companyid);
            //pstmt.executeUpdate();
            //out.println("<br>Inserted in externalapplicants");

            pstmt = conn.prepareStatement("insert into "+toDb+".template (select * from "+fromDb+".template where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in template");

            pstmt = conn.prepareStatement("insert into "+toDb+".payhistory (select * from "+fromDb+".payhistory where userID in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in payhistory");

            pstmt = conn.prepareStatement("insert into "+toDb+".historydetail (select * from "+fromDb+".historydetail where historyid in (select historyid from "+fromDb+".payhistory where userID in (select userid from "+fromDb+".users where company=?)))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in historydetail");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_docs (select * from "+fromDb+".hrms_docs where referenceid in (select id from "+fromDb+".configrecruitmentdata where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_docs");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_docsmap (select * from "+fromDb+".hrms_docsmap where docid in (select docid from "+fromDb+".hrms_docs where referenceid in (select id from "+fromDb+".configrecruitmentdata where company=?)))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_docsmap");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_emailTemplates (select * from "+fromDb+".hrms_emailTemplates where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_emailTemplates");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_empexp (select * from "+fromDb+".hrms_empexp where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_empexp");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_Emphistory (select * from "+fromDb+".hrms_Emphistory where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_Emphistory");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_empprofile (select * from "+fromDb+".hrms_empprofile where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_empprofile");

            pstmt = conn.prepareStatement("insert into "+toDb+".hrms_goalcomments (select * from "+fromDb+".hrms_goalcomments where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in hrms_goalcomments");

            pstmt = conn.prepareStatement("insert into "+toDb+".htmltemplate (select * from "+fromDb+".htmltemplate where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in htmltemplate");

            pstmt = conn.prepareStatement("insert into "+toDb+".jobapplicant (select * from "+fromDb+".jobapplicant where company=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in jobapplicant");

            pstmt = conn.prepareStatement("insert into "+toDb+".jobprofile (select * from "+fromDb+".jobprofile where position in (select positionid from "+fromDb+".positionmain where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in jobprofile");

            pstmt = conn.prepareStatement("insert into "+toDb+".letter_history (select * from "+fromDb+".letter_history where template in (select id from "+fromDb+".htmltemplate where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in letter_history");

            pstmt = conn.prepareStatement("insert into "+toDb+".placeholder_mapping (select * from "+fromDb+".placeholder_mapping where template in (select id from "+fromDb+".htmltemplate where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in placeholder_mapping");

            pstmt = conn.prepareStatement("insert into "+toDb+".projreport_template (select * from "+fromDb+".projreport_template where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in projreport_template");

            pstmt = conn.prepareStatement("insert into "+toDb+".recruiter (select * from "+fromDb+".recruiter where recid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in recruiter");

            pstmt = conn.prepareStatement("insert into "+toDb+".taxmaster (select * from "+fromDb+".taxmaster where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in taxmaster");

            pstmt = conn.prepareStatement("insert into "+toDb+".templatemapdeduction (select * from "+fromDb+".templatemapdeduction where templateid in (select templateid from "+fromDb+".template where companyid=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in templatemapdeduction");

            pstmt = conn.prepareStatement("insert into "+toDb+".templatemaptax (select * from "+fromDb+".templatemaptax where templateid in (select templateid from "+fromDb+".template where companyid=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in templatemaptax");

            pstmt = conn.prepareStatement("insert into "+toDb+".wagemaster (select * from "+fromDb+".wagemaster where companyid=?)");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in wagemaster");

            pstmt = conn.prepareStatement("insert into "+toDb+".templatemapwage (select * from "+fromDb+".templatemapwage where templateid in (select templateid from "+fromDb+".template where companyid=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in templatemapwage");

            pstmt = conn.prepareStatement("insert into "+toDb+".timesheet (select * from "+fromDb+".timesheet where userid in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in timesheet");

            pstmt = conn.prepareStatement("insert into "+toDb+".usersearchstate (select * from "+fromDb+".usersearchstate where user in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in usersearchstate");

            pstmt = conn.prepareStatement("insert into "+toDb+".usertemplatemap (select * from "+fromDb+".usertemplatemap where userAccount in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in usertemplatemap");

            pstmt = conn.prepareStatement("insert into "+toDb+".widgetmanagement (select * from "+fromDb+".widgetmanagement where user in (select userid from "+fromDb+".users where company=?))");
            pstmt.setString(1, companyid);
            pstmt.executeUpdate();
            count++;
            out.println("<br>"+count+"> Inserted in widgetmanagement");

            

            conn.commit();
            pstmt.close();


    } catch(Exception e) {
        out.println("<br>Exception is "+e);
        conn.rollback();
    } finally {
        conn.close();
    }

    
%>
