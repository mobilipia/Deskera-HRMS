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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@ page import="com.krawler.esp.utils.ConfigReader" %>
<%@page import="com.krawler.common.util.StringUtil"%>

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
	Connection conn = null;
	String connectionURL = "jdbc:mysql://"+serverIP+":"+port+"/"+dbName+"?user="+userName+"&password="+password;
	String userid= null;
	
	try {
		Class.forName(driver);
		conn = DriverManager.getConnection(connectionURL);
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		pstmt = conn.prepareStatement("select userid from users where userid not in (select userid from hrms_empprofile)");
		rs = pstmt.executeQuery();
		int count = 0;
		while(rs.next()){
		
			userid = rs.getString("userid");
			pstmt1 = conn.prepareStatement("insert into hrms_empprofile (userid, gender, marriage, bloodgrp, fathername, mothername, bankname, bankbranch, panno, pfno, drvlicense, passportno, middlename, keyskills, updated_by, updated_on, status, terminateflag) values ('"+userid+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '"+userid+"', '2012-04-11', 'Approved', 'F')");
			pstmt1.executeUpdate();
			out.println(++count+" "+userid+"\n\n");
		
		}
		out.println("SUCCESS:"+count+" records inserted successfully.");
	} catch (Exception ex) {
		ex.printStackTrace();
		out.println(ex);
	} finally{
    	if(conn != null) {
        	conn.close();
    } 
}
%>
