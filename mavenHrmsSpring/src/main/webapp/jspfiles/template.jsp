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
<%@ page import="com.krawler.utils.json.base.*" %>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%
        boolean isFormSubmit = false;
        JSONObject jobj = new JSONObject();
        JSONObject obj = new JSONObject();
        if (sessionbean.validateSession(request, response)) {
            try {
                int action = Integer.parseInt(request.getParameter("action"));
                switch (action) {
                    case 0:
                        jobj = hrmsDbcon.saveReportTemplate(request);
                        break;
                    case 1:
                        jobj = hrmsDbcon.getAllReportTemplate(request);
                        break;
                    case 2:
                        jobj = hrmsDbcon.deleteReportTemplate(request);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                jobj.put("success", false);
                jobj.put("msg", e.getMessage());
            } finally {
                obj.put("valid", true);
                if (!isFormSubmit) {
                    obj.put("data", jobj);
                } else {
                    obj = jobj;
                }
            }
        } else {
            sessionbean.destroyUserSession(request, response);
            obj.put("valid", false);
        }
        out.println(obj);
%>

