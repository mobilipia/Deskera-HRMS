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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.esp.handlers.AuthHandler"
         import="com.krawler.esp.database.hrmsDbcon"
         import="com.krawler.esp.handlers.PermissionHandler" %>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.utils.json.base.JSONArray"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%
        String message = "{\"data\":[]}";
        boolean isFormSubmit = false;
        if (sessionbean.validateSession(request, response)) {
            try {

                int flag = 0;
                JSONArray jArr = new JSONArray();
                JSONObject tempJObj = new JSONObject();
                if(request.getParameter("flag")!=null)
                    flag = Integer.parseInt(request.getParameter("flag"));
                else
                    flag = Integer.parseInt(request.getParameter("mode"));

                switch (flag) {
                    case 1: // get widgetstates
                        message = hrmsDbcon.getWidgetStatus(request);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                    case 2:// remove widgetstates
                        message = hrmsDbcon.removeWidgetFromState(request);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                    case 3:// insert widgetstates
                        message = hrmsDbcon.insertWidgetIntoState(request);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                    case 4:// change widgetstates
                        message = hrmsDbcon.changeWidgetStateOnDrop(request);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                    case 5:// get project updates
                        //JSONObject jobj = new JSONObject();
                        //jobj.put("data", new JSONArray());
                        //jobj.put("count", 0);
                        //message = jobj.toString();
                        message = hrmsDbcon.getUpdatesForWidgets(request);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                    case 6:// get Administration Links
                        message = hrmsDbcon.getDashboardLinks(request,flag);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                    case 7:// get Recruitment Links
                        message = hrmsDbcon.getDashboardLinks(request,flag);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;

                    case 8:// get Payroll Links
                        message = hrmsDbcon.getDashboardLinks(request,flag);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;

                    case 9:// get Timesheet Links
                        message = hrmsDbcon.getDashboardLinks(request,flag);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                        
                    case 10:// get Performance Links
                        message = hrmsDbcon.getDashboardLinks(request,flag);
                        message = "{\"valid\": true, \"data\":"+message+"}";
                        break;
                }
            } catch (Exception e) {
                message = "{\"success\":false,\"msg\":" + e + "}";
            } finally {
                //if(!isFormSubmit)
                //    message = "{\"valid\": true, \"data\":"+message+"}";

            }
        } else {
            message = "{'valid': false}";
        }
        out.println(message);
%>

