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
<%@ page import="com.krawler.common.util.URLUtil"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="com.krawler.esp.handlers.ProfileHandler"%>
<%@ page import="com.krawler.esp.handlers.PermissionHandler"%>
<%@ page import="com.krawler.esp.database.DBCon"%>
<%@ page import="com.krawler.utils.json.base.JSONException"%>
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
<%@ page import="org.hibernate.Session"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%
   	Session hibernateSession = null;
	try {
        JSONObject jret = new JSONObject();
		JSONObject jbj = new JSONObject();
        String username = request.getRemoteUser();
        String result = "";
		String subdomain = URLUtil.getDomainName(request);
        boolean isValidUser = false;
        if (!StringUtil.isNullOrEmpty(username)) {
            jbj = DBCon.AuthUser(username, subdomain);
			if (jbj.has("success")){
				sessionbean.createUserSession(request, jbj);
				isValidUser = true;
			} else {
				result = "noaccess";
			}
        } else {
			if (sessionbean.validateSession(request, response)) {
            	username =  AuthHandler.getUserName(request);
            	jbj.put("username", username);
            	jbj.put("companyid", AuthHandler.getCompanyid(request));
            	jbj.put("company", AuthHandler.getCompanyName(request));
            	isValidUser = true;
			} else {
				result = "timeout";
			}
		}
		if (isValidUser) {
            hibernateSession = HibernateUtil.getCurrentSession();
            String userid=AuthHandler.getUserid(request);
            String fullname = ProfileHandler.getUserFullName(hibernateSession, userid);
            jbj.put("fullname", (fullname == null || fullname == "") ? username : fullname );           
			jbj.put("lid", userid);
            jbj.put("roleid", 0);
            jbj.put("perm", PermissionHandler.getPermissions(hibernateSession, userid));
            jbj.put("preferences", AuthHandler.getPreferences(hibernateSession, request));
            jbj.put("companypreferences", AuthHandler.getCompanyPreferences(hibernateSession, request));
            jbj.put("validuseroptions", ProfileHandler.getValidUserOptions(hibernateSession, userid));           
            jbj.put("subscriptioncode",AuthHandler.getCmpSubscription(request));
            jbj.put("base_url", URLUtil.getPageURL(request,""));
			jret.put("data", jbj.toString());
            jret.put("valid", true);
		} else {
			JSONObject j = new JSONObject();
			j.put("reason", result);
			jret.put("valid", false);
			jret.put("data", j);
		}
%>
<%=jret.toString()%>
<%
	} catch (JSONException e) {
        e.printStackTrace();
        out.println("{\"valid\":false,\"data\":{}}");
    } catch(Exception sE){
        System.out.println(sE);
        out.println("{\"valid\":false,\"data\":{}}");
    }
    finally {
        HibernateUtil.closeSession(hibernateSession);
    }
%>
