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

<%@page language = "java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.krawler.esp.utils.ConfigReader"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<style>
   
    .content {
        background-color:white;
        border:10px solid #CCCCCC;
        margin:auto;
        padding:20px;
        text-align:left;
        width:200px;

    }
    #content {
        color:black;
        left:0;
        position:absolute;
        top:25%;
        width:100%;
    }
    #wrapper {
        margin:0;
        padding:0;
        text-align:center;
    }

</style>

<%
        String domainurl = URLUtil.getDomainName(request);
        domainurl = URLUtil.getDomainURL(domainurl,false);
		
        ConfigReader configReader = ConfigReader.getinstance();
        String driver = configReader.get("driver");
        String serverIP = configReader.get("serverIP");
        String port = configReader.get("port");
        String dbName = configReader.get("dbName");
        String userName = configReader.get("userName");
        String password = configReader.get("password");
        
        String connectionURL = "jdbc:mysql://"+serverIP+":"+port+"/"+dbName+"?user="+userName+"&password="+password;
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(connectionURL);
        conn.setAutoCommit(false);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int flag = !StringUtil.isNullOrEmpty(request.getParameter("flag")) ? Integer.parseInt(request.getParameter("flag")) : 1;
        String res = "";
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String baseUrl = domainurl+"assignGoals.jsp";

        String mobilePagesPath = this.getServletContext().getInitParameter("crmURL");
        //String subdomain = "demo";//request.getParameter("subdomain");
        String subdomain = request.getParameter("subdomain");
        mobilePagesPath = mobilePagesPath + "a/" + subdomain + "/bbmobile/welcomepage.jsp";

        try {
            switch (flag) {
                case 1:
                    String submitURL = baseUrl+"?flag=2&subdomain="+subdomain;
                    
                    //String companyid = "a4792363-b0e1-4b67-992b-2851234d5ea6";//request.getParameter("companyid");
                    String companyid = request.getParameter("companyid");
                    //String userid = "f2ccc62a-aae3-4305-9134-64b7fd83164e";//request.getParameter("userid");
                    String userid = request.getParameter("userid");
                    
                    String role = "1";
                    

                    String startStr = "<div class=\"form_wrapper\">"
                            + "<h4>Assign Goals</h4>"
                            + "   <form method=\"post\" action='" + submitURL + "'>"
                            + "     <table style='text-align:left;font-size:13px;'>";
                    String percent = "";
                    String userComboStr = "";
                    String goalName = "<tr><td><label for='gName'>Goal Name *</label></td><td><input type='text' name='gname' maxlength='255'/></td></tr>";
                    String goalDesc = "<tr><td><label for='gDesc'>Goal Description *</label></td><td><input type='text' name='gdesc' maxlength='255'/></td></tr>";
                    String weightageComboStr = "";
                    String contextComboStr = "";
                    String priorityComboStr = "";
                    String currDate = sdf.format(new Date());
                    String stDate = "<tr><td><label for='startdate'>Start Date *</label></td><td><input type='text' name='startdate' value='"+currDate+"' maxlength='10'/></td></tr>"
                            + "<tr><td>&nbsp;</td><td><span style='font-size:11px'>(yyyy-mm-dd )</span></td></tr>";
                    String dueDate = "<tr><td><label for='todate'>Due Date *</label></td><td><input type='text' name='duedate'  value='"+currDate+"' maxlength='10' /></td></tr>"
                            + "<tr><td>&nbsp;</td><td><span style='font-size:11px'>(yyyy-mm-dd )</span></td></tr>";
                    String hiddenFields = "<tr><td><input type='hidden' name='userid' value='" + userid + "'/>"
                            + "<input type='hidden' name='companyid' value='" + companyid + "'/>"
                            + "<input type='hidden' name='returnurl' value=''/></td></tr>";
                    String endStr = "       <tr><td>&nbsp;</td><td><input type='submit' name='submit' value='Submit' style='width:80px;'/>"
                            + "&nbsp;<input type='button' onclick=javascript:history.back() value='Cancel' style='width:80px;'/></td></tr>"
                            + "     </table>"
                            + "	</form>"
                            + "</div>";
                    try {
                    	pstmt = conn.prepareStatement("select value from MasterData where masterid=? and ( company is null or company=? )order by weightage,value");
                        pstmt.setString(1, "5");
                        pstmt.setString(2, companyid);
                        rs = pstmt.executeQuery();
                        percent = "<tr><td><label for='percent'>Percent Completed </label></td><td><select name='percent'>";
                        while (rs.next()) {
                        	percent += "<option value='" + rs.getObject("value") + "'>" + rs.getObject("value") + "</option>";
                        }
                        percent += "</select></td></tr>";
                    	
                        pstmt = conn.prepareStatement("select role from useraccount where userid = ?");
                        pstmt.setString(1, userid);
                        rs = pstmt.executeQuery();
                        if(rs.next()) {
                            role = rs.getString(1);
                        }
                        String userListQuery = "";
                        if("1".equals(role)) {
                            userListQuery = "select userid, concat(fname, ' ', lname) as name from users where company = ? and deleteflag = 0 order by fname asc, lname desc";
                        } else {
                            userListQuery = "select userid, concat(fname, ' ', lname) as name from users where company = ? and deleteflag = 0 and userid in (select userid from hrms_empprofile where reportto = ? ) order by fname asc, lname desc";
                        }

                        pstmt = conn.prepareStatement(userListQuery);
                        pstmt.setString(1, companyid);
                        if(!"1".equals(role)) {
                            pstmt.setString(2, userid);
                        }
                        rs = pstmt.executeQuery();
                        userComboStr = "<tr><td><label for='employee'>Employee Name *</label></td><td><select name='employee'>";
                        while (rs.next()) {
                            userComboStr += "<option value='" + rs.getObject("userid") + "'>" + rs.getObject("name") + "</option>";
                        }
                        userComboStr += "</select></td></tr>";


                        pstmt = conn.prepareStatement("select value from MasterData where masterid=? and ( company is null or company=? )order by weightage,value");
                        pstmt.setString(1, "4");
                        pstmt.setString(2, companyid);
                        rs = pstmt.executeQuery();

                        weightageComboStr = "<tr><td><label for='weightage'>Weightage *</label></td><td><select name='weightage'>";
                        while (rs.next()) {
                            weightageComboStr += "<option value='" + rs.getObject("value") + "'>" + rs.getObject("value") + "</option>";
                        }
                        weightageComboStr += "</select></td></tr>";

                        pstmt = conn.prepareStatement("select value from MasterData where masterid=? and ( company is null or company=? )order by weightage,value");
                        pstmt.setString(1, "2");
                        pstmt.setString(2, companyid);
                        rs = pstmt.executeQuery();

                        contextComboStr = "<tr><td><label for='context'>Context *</label></td><td><select name='context'>";
                        while (rs.next()) {
                            contextComboStr += "<option value='" + rs.getObject("value") + "'>" + rs.getObject("value") + "</option>";
                        }
                        contextComboStr += "</select></td></tr>";

                        pstmt = conn.prepareStatement("select value from MasterData where masterid=? and ( company is null or company=? )order by weightage,value");
                        pstmt.setString(1, "3");
                        pstmt.setString(2, companyid);
                        rs = pstmt.executeQuery();

                        priorityComboStr = "<tr><td><label for='priority'>Priority *</label></td><td><select name='priority'>";
                        while (rs.next()) {
                            priorityComboStr += "<option value='" + rs.getObject("value") + "'>" + rs.getObject("value") + "</option>";
                        }
                        priorityComboStr += "</select></td></tr>";

                        //close();
                        //conn.close();


                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        conn.close();
                    }
                    res = startStr + userComboStr + goalName + goalDesc + weightageComboStr + contextComboStr + priorityComboStr + stDate + dueDate + percent + hiddenFields + endStr;
                    break;
                    case 2 :
                        String outStringStart = "<style>"+
                                            "body {"+
                                            "background-color:#EEEEEE;"+
                                            "font-family:'trebuchet MS',tahoma,verdana,arial,helvetica,sans-serif;"+
                                            "margin:0;"+
                                            "}</style><div id='wrapper'>"+
                                                "<div id='content'>"+
                                                "<div class='content'>"+
                                                "<div id='msg' class='success'>";
                        String outStringEnd = "</div></div></div></div>";

                        String gName = request.getParameter("gname");
                        String gDesc = request.getParameter("gdesc");
                        String priority = request.getParameter("priority");
                        String weightage = request.getParameter("weightage");
                        String context = request.getParameter("context");
                        String startdate = request.getParameter("startdate");
                        String duedate = request.getParameter("duedate");
                        String employee = request.getParameter("employee");
                        String percentage = request.getParameter("percent");


                        if(StringUtil.isNullOrEmpty(gName) || StringUtil.isNullOrEmpty(gDesc)
                                || StringUtil.isNullOrEmpty(priority)  || StringUtil.isNullOrEmpty(employee)
                                || StringUtil.isNullOrEmpty(weightage) || StringUtil.isNullOrEmpty(context)
                                || StringUtil.isNullOrEmpty(startdate) || StringUtil.isNullOrEmpty(duedate)) {

                                String errorMsg = outStringStart+"<div style = 'text-align : center'>" +
                                        "Please fill all fields<br/> Click " +
                                        "<a href = '"+mobilePagesPath+"'>here</a> to go back." +
                                        "</div>"+outStringEnd;
                                if(StringUtil.isStandAlone()){
                                    
                                    errorMsg = outStringStart+"<div style = 'text-align : center'>" +
                                        "Please fill all fields<br/>" +
                                        "</div>"+outStringEnd;
                                
                                }

                                out.print(errorMsg);
                        } else {
                            Date stDt = new Date();
                            Date enDt = new Date();
                            try {
                                stDt = sdf.parse(startdate);
                                enDt = sdf.parse(duedate);
                            } catch(Exception e) {
                                String errorMsg = outStringStart +"<div style = 'text-align : center'>" +
                                        "Please enter valid dates.<br/> Click " +
                                        "<a href = '"+mobilePagesPath+"'>here</a> to go back." +
                                        "</div>" + outStringEnd;
                                if(StringUtil.isStandAlone()){
                                    
                                    errorMsg = outStringStart +"<div style = 'text-align : center'>" +
                                        "Please enter valid dates.<br/>" +
                                        "</div>" + outStringEnd;
                                
                                }
                                out.print(errorMsg);
                                return;
                            }
                            String id = UUID.randomUUID().toString();
                            String usrId = request.getParameter("userid");
                            pstmt = conn.prepareStatement("select concat(fname,' ',lname) from users where userid = ? ");
                            pstmt.setString(1, usrId);
                            rs = pstmt.executeQuery();
                            String fullName = "";
                            if(rs.next()) {
                                fullName  = rs.getString(1);
                            }

                            pstmt = conn.prepareStatement("insert into finalgoalmanagement (id,userid,goalname,goaldesc,goalwth,priority," +
                                    "context,startdate,enddate,completed,assignedby,createdon,archivedflag,deleteflag,manager,internal," +
                                    "percentcomplete) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                            pstmt.setString(1, id);
                            pstmt.setString(2, employee);
                            pstmt.setString(3, gName);
                            pstmt.setString(4, gDesc);
                            pstmt.setString(5, weightage);
                            pstmt.setString(6, priority);
                            pstmt.setString(7, context);
                            pstmt.setObject(8, stDt);
                            pstmt.setObject(9, enDt);
                            pstmt.setString(10, "T");
                            pstmt.setString(11, fullName);
                            pstmt.setObject(12, new Date());
                            pstmt.setInt(13, 0);
                            pstmt.setString(14, "F");
                            pstmt.setString(15, usrId);
                            pstmt.setString(16, "T");
                            pstmt.setString(17, percentage);
                            pstmt.executeUpdate();
                            conn.commit();
                            //rs.close();
                            //mobilePagesPath = mobilePagesPath + subdomain + "/bbmobile/welcomepage.jsp";


                            String successMsg = outStringStart +
                                                "<div style = 'text-align : center'>" +
                                        "Goal assigned successfully.<br/> Click " +
                                        "<a href = '"+mobilePagesPath+"'>here</a> to go back." +
                                        "</div>"+outStringEnd;

                            if(StringUtil.isStandAlone()){
                                    
                                successMsg = outStringStart +
                                                "<div style = 'text-align : center'>" +
                                        "Goal assigned successfully.<br/> " +
                                        "</div>"+outStringEnd;
                            }


                            out.print(successMsg);
                        }

                        break;

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            //rs.close();
            conn.rollback();
        } finally {
            if(rs != null)
                rs.close();
            conn.close();
        }
%>
<%= res %>

