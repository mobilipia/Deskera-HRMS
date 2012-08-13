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
	String filename = null;
	String userid= null;
	String url = "http://hrms.deskera.com/b/secutech/Performance/exportAppraisalReportPDF/appraisalReportExport.pf?";
	//String url = "http:ocalhost:8080/hrms/a/rana1/Performance/exportAppraisalReportPDF/appraisalReportExport.pf?";
	String urldata = null;
	String appraisalcycid ="c340667e33b1b4510133bdd17a2100bc";
	try {
		Class.forName(driver);
		conn = DriverManager.getConnection(connectionURL);
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		pstmt = conn.prepareStatement("select employee from appraisalmanagement where appcycle = ? group by employee");
		pstmt.setString(1, appraisalcycid);
		rs = pstmt.executeQuery();
		
		FileWriter fstream = new FileWriter("AppraisalReport.txt");
		BufferedWriter out1 = new BufferedWriter(fstream);
		while(rs.next()){
			
			
			userid = rs.getString("employee");
			pstmt1 = conn.prepareStatement("select fname,lname from users where userid = ?");
			pstmt1.setString(1, userid);
			rs1 = pstmt1.executeQuery();
			rs1.next();
			filename = rs1.getString("fname")+"_"+rs1.getString("fname");
			urldata = url;
			urldata += "filename="+filename+"&filetype=pdf&reviewappraisal="+false+"&appraisalcycid="+appraisalcycid+"&userid="+userid+"&employee="+false+"&self="+true+"&isPrint="+false+"&pdfEmail="+true;
			out1.write(urldata);
			out1.write("\n\n\n");
		}
		out1.close();
		out.print("SUCCESS");
	} catch (Exception ex) {
		ex.printStackTrace();
		out.println(ex);
	} finally{
    	if(conn != null) {
        	conn.close();
    } 
}
%>
