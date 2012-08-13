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
<%@ page contentType="text/html"  pageEncoding="UTF-8" %>
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="com.krawler.esp.database.hrmsDbcon"%>
<%@ page import="com.krawler.utils.json.base.JSONException" %>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%@ page import="com.krawler.utils.json.base.*" %>

<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%

        String msg = "Session timed out. Please go back to login page";
	if (sessionbean.validateSession(request, response)) {
		try {
                        JSONObject jbj = new JSONObject();
                        boolean refresh = true;
                        msg = hrmsDbcon.getDashboard(request);
                      //  msg += "<link rel='alternate' type='application/rss+xml' title='RSS - Global RSS Feed' href=\""+com.krawler.common.util.URLUtil.getPageURL(request,"")+"feed.rss?m=global&u="+AuthHandler.getUserName(request)+"\">";
                        /*Request param must be sent from atleast one case*/
                        if(StringUtil.isNullOrEmpty(request.getParameter("refresh"))){
                           refresh = true;
                        } else {
                            refresh = Boolean.parseBoolean(request.getParameter("refresh"));
                        }
                        if(refresh) {
                                jbj.put("valid", "true");
                                jbj.put("data", msg);
                                msg = jbj.toString();
                           }
		} catch (Exception e) {
            	System.out.print(e.getMessage());
		} finally {
			out.println(msg);
		}
	} else {
		out.println(msg);
	}
%>
