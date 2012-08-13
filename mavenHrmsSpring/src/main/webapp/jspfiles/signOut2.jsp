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
<%@ page import="com.krawler.common.util.StringUtil" %>
<%@ page import="com.krawler.esp.web.resource.Links"%>
<%@ page import="com.krawler.common.util.URLUtil"%>
<%@ page import="java.net.URLEncoder"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler"/>
<%
	String subdomain = null;
	String uri = null;
	String redirectUri = null;
	
	String newDomain = request.getParameter("n");

	if(!StringUtil.isNullOrEmpty(newDomain)){
        uri = URLUtil.getPageURL(request, Links.loginpageFull, newDomain);
		subdomain = newDomain;
	}
	else {
		uri = URLUtil.getPageURL(request, Links.loginpageFull);
		subdomain = URLUtil.getDomainName(request);
	}
	
	String logoutUrl = this.getServletContext().getInitParameter("casServerLogoutUrl");
	if( StringUtil.isNullOrEmpty(logoutUrl)){
		redirectUri = uri + "login.html";
	}
	else{
		redirectUri = logoutUrl + String.format("?url=%s&subdomain=%s",URLEncoder.encode(uri, "UTF-8"), subdomain);
	}
	
	sessionbean.destroyUserSession(request, response);
	response.sendRedirect(redirectUri);
%>
