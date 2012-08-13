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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.krawler.esp.database.hrmsDbcon"
        import="com.krawler.utils.json.base.*"
        import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
        <%@ page import="com.krawler.common.session.SessionExpiredException"%>
  <%@ page import="com.krawler.common.service.ServiceException"%>
  <%@ page import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
  <%@ page import="com.krawler.hrms.hrmsHandler"
           import="com.krawler.hrms.hrmsManager"
           import="com.krawler.common.util.*"%>
  <%@ page import="org.hibernate.Transaction"%>
  <%@ page import="org.hibernate.Session"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler"/>
<%
    String msg="";
    String user = request.getParameter("u");
    String pass = request.getParameter("p");
    JSONObject jobj = new JSONObject();
    int  flag = Integer.parseInt(request.getParameter("flag").toString());
    String subdomain = URLUtil.getDomainName(request);
    Session hibernateSession = null;
    Transaction tx = null;
    try{
      hibernateSession = HibernateUtil.getCurrentSession();    
    switch(flag){
        case 1:
                 tx=hibernateSession.beginTransaction();
                 msg=hrmsHandler.Externalapplicant(hibernateSession, request,subdomain);
                 tx.commit();
                     break;
        case 2:
                     jobj= hrmsManager.externaluser(hibernateSession, user, pass,subdomain);
                     if(jobj.has("success") && (jobj.get("success").equals(true))){
                       sessionbean.createExternalUserSession(request, jobj);
                       jobj.put("valid",true );
     
	} else {
        HibernateUtil.closeSessionFactory();
		jobj = new JSONObject();
		jobj.put("success", false);
        jobj.put("valid", false);
		jobj.put("message", "Authentication failed");
    }
                   msg=jobj.toString();
                  break;
}
    
}catch(Exception ex){
     if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
}finally {
            HibernateUtil.closeSession(hibernateSession);
        }
%>
<%=msg.toString()%>
