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
<%@ page import="com.krawler.esp.database.DBCon"%>
<%@ page import="com.krawler.common.util.AuditAction"%>
<%@ page import="com.krawler.spring.sessionHandler.sessionHandlerImpl"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler"/>
<%
	String _sO = request.getParameter("type");
	String uri = URLUtil.getPageURL(request, Links.loginpageFull);
	String redirectUri = "";
	try {

        if (request.getSession().getAttribute("initialized") != null) {

		DBCon.insertAuditLog(AuditAction.LOGOUT_SUCCESS, "User "
				+ DBCon.getFullName(request)
				+ " has logged out"
				+ (!StringUtil.isNullOrEmpty(_sO) ? (" due to " + _sO)
						: ""), request);
        }
     
	
	String logoutUrl = this.getServletContext().getInitParameter(
			"casServerLogoutUrl");
	if (StringUtil.isNullOrEmpty(logoutUrl)) {
		redirectUri = uri + "login.html";
		if (!StringUtil.isNullOrEmpty(_sO)) {
			redirectUri += ("?" + _sO);
		}
	} else {
		String subdomain = URLUtil.getDomainName(request);
		redirectUri = logoutUrl
				+ String.format("?url=%s&subdomain=%s", URLEncoder
						.encode(uri, "UTF-8"), subdomain, _sO);
		if (!StringUtil.isNullOrEmpty(_sO)) {
			redirectUri += ("&type=" + _sO);
		}
	}
	sessionbean.destroyUserSession(request, response);
	response.sendRedirect(redirectUri);
    } catch (Exception exc) {
        out.println(exc);
        exc.printStackTrace();
	}
%>
