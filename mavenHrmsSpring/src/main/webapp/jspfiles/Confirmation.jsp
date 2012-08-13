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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>   
<%@ page import="com.krawler.esp.database.hrmsDbcon"%>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%
	String company = request.getParameter("c");
	String user = request.getParameter("u");
    String accept=request.getParameter("acpt");
	JSONObject jobj = new JSONObject();
    boolean status = false;
    String msg = "";
    try{
        if (!StringUtil.isNullOrEmpty(company)||!StringUtil.isNullOrEmpty(user)||!StringUtil.isNullOrEmpty(accept)) {
            jobj = hrmsDbcon.setInterviewerConfirmation(company,user,request);
            status = Boolean.parseBoolean(jobj.getString("success"));
            msg = jobj.getString("msg");
        } else {
            status = false;
            msg = "Invalid parameters. Please contact on an email support@deskera.com.";
        }
    } catch(Exception e) {
        status = false;
        msg = "Problem occured while performing operation. Please contact on an email support@deskera.com.";
    }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>Confirmation Status</title>
	</head>
	<body>
		<div id="header">
			&nbsp;
            <%if (!status) {%>
            <strong><span><%=msg%></span></strong>
            <%} else {%>
            <span><%=msg%></span>
            <%}%>
		</div>
	</body>
</html>
