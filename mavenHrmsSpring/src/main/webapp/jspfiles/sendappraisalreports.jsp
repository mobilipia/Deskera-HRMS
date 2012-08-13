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
<%@ page import="com.krawler.common.util.*" %>
<%@ page import="com.krawler.esp.database.*" %>
<%@ page import="com.krawler.utils.json.base.JSONObject" %>
<%@ page import="com.krawler.utils.json.base.JSONArray" %>
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="com.krawler.esp.handlers.StorageHandler"%>
<%@ page import="com.krawler.esp.handlers.SendMailHandler" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%@ page import=" com.krawler.common.admin.*"%>
<%@ page import=" com.krawler.esp.hibernate.impl.HibernateUtil" %>
<%@ page import=" org.hibernate.*" %>
<%@page import="com.krawler.esp.web.resource.*" %>
<%@page import="com.krawler.hrms.hrmsHandler" %>
<%@page import="com.krawler.hrms.hrmsManager" %>
<%@page import="com.krawler.hrms.performance.*" %>




<!--jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" /-->
<%

    try {
            
            String companyid = request.getParameter("companyid");




            JSONObject jobj = new JSONObject();
            String mailcontent = "";
            String htmlmailcontent = "";
            String plnmailcontent = "";
            String email = "admin@deskera.com";
            String plainmsg = "";
            String htmlmsg = "";
            String title = "";
            String url = "";
//            User usr = (User) session.load(User.class, AuthHandler.getUserid(request));
//            if (usr.getEmailID() != null) {
//                email = usr.getEmailID();
//            }
            Session session1 = HibernateUtil.getCurrentSession();

            Company cmpid = (Company) session1.get(Company.class, companyid);
            String sendmailto = request.getParameter("sendto");
            String appcycleid = request.getParameter("appraisalcycleid");
            url = URLUtil.getPageURL(request, Links.loginpagewthFull, cmpid.getSubDomain()) + "Performance/exportAppraisalReportPDF/appraisalReportExport.pf?pdfEmail=true&reviewappraisal=false&appraisalcycid=" + appcycleid + "&userid=";
            JSONObject eobj = hrmsHandler.EmailCases(hrms_EmailTemplates.pdfMailtoReviewer, session1, request);
            plainmsg = eobj.getString("plainmessage");
            htmlmsg = eobj.getString("htmlmessage");
            title = eobj.getString("title");
            Appraisalcycle app = (Appraisalcycle) session1.load(Appraisalcycle.class, appcycleid);
            mailcontent = "Appraisal Cycle: " + app.getCyclename() + "\n\n";
            //String query = "select distinct reviewer.userID,reviewer.firstName,reviewer.lastName,reviewer.emailID from Assignreviewer where reviewer.company.companyID=? and reviewer.deleted=? and reviewerstatus=1";
            //List list = HibernateUtil.executeQuery(session1, query, new Object[]{companyid, false});
            //Iterator itr = list.iterator();
            //while (itr.hasNext()) {
                //Object[] reviewer = (Object[]) itr.next();
                String hql = "select distinct employee.userID,employee.firstName,employee.lastName from Appraisalmanagement where appcycle.id=? and reviewstatus=2";
                List list1 = HibernateUtil.executeQuery(session1, hql, new Object[]{appcycleid});
                Iterator ite1 = list1.iterator();
                if (!list1.isEmpty()) {
                    while (ite1.hasNext()) {
                        Object[] row = (Object[]) ite1.next();
                        String target="_parent";
                        //String empid=hrmsManager.getEmpidFormatEdit(session1, request,(Integer)row[3]);
                        plnmailcontent += "" + row[1]+" "+row[2] + " - " + url + row[0] +"\n";
                        htmlmailcontent+="<a target='"+target+"' href='"+ url+row[0]+"'>"+row[1]+" "+row[2]+"</a>\n";
                    }
                    plnmailcontent=mailcontent+plnmailcontent;
                    htmlmailcontent=mailcontent+htmlmailcontent;
                    String intpmsg = String.format(plainmsg, " "+" "+" ", plnmailcontent);
                    String inthtmlmsg = String.format(htmlmsg, " "+" "+" ", htmlmailcontent.replaceAll("\n","<br>" ));
                    htmlmailcontent="";
                    plnmailcontent="";
                    try {
                        SendMailHandler.postMail(new String[]{sendmailto}, title, inthtmlmsg, intpmsg, email);
                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            //}
            //jobj.put("message", mailcontent);
            //return jobj;
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.sendRevieweremailFunction", e);
        } catch(Exception e) {
        out.println("<br>Exception is "+e);
        } finally {
        
    }

    
%>
