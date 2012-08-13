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
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="com.krawler.esp.database.hrmsDbcon"%>
<%@ page import="com.krawler.hql.payroll.payrollManager"%>
<%@ page import="com.krawler.utils.json.base.JSONException" %>
<%@ page import="com.krawler.utils.json.base.JSONObject" %>
<%@ page import="com.krawler.utils.json.base.JSONArray" %>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@page import="com.krawler.esp.handlers.*" %>
<%@page import="java.util.*" %>
<%@ page import="com.krawler.hrms.*" %>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%

        String mode = request.getParameter("mode");
        String roleId = request.getParameter("roleid");
        String result = "";
        String flag = request.getParameter("flag");

       
            switch (Integer.parseInt(flag)) {
                case 1:
                try {
                    com.krawler.utils.json.base.JSONObject jobj = hrmsDbcon.getPerformanceData(13);
                    JSONArray jarr = jobj.getJSONArray("data");
                    jarr = jobj.getJSONArray("data");
                    result = "<pie>";
                    for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                        String Jsondata = hrmsDbcon.getChart(jarr.getJSONObject(j).get("name").toString(), request);
                        JSONObject obj = new JSONObject(Jsondata);
                        if (obj.getInt("Count") > 0) {
                            result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + obj.getString("Count") + "</slice>";
                        }
                    }
                    result += "</pie>";
                    break; 

               
            }
       catch (Exception e) {
            e.getMessage();
            }
                   case 2:
                try{
                   String year=null;
                       JSONObject jobj =  payrollManager.getAnnualExpense(year,request);
                        JSONArray jarr=jobj.getJSONArray("data");
                        jarr=jobj.getJSONArray("data");
                        int i;
                        int y;
                        JSONObject jobj1= new JSONObject();
                        result = "<chart><series>";
                        for(i=0;i<jarr.length()-1;i++){
                            jobj1=jarr.getJSONObject(i);
                         jobj1.getString("amount");
                         if(jobj1.getString("month")==null ||jobj1.getString("month")=="")
                             break;
                            result +="<value xid=\""+i+"\">"+jobj1.getString("month")+"</value>";
                        }
                        result += "</series><graphs>";
                        JSONObject jobj2= new JSONObject();
                      result +="<graph gid=\"1\">";
                         for(y=0;y<jarr.length()-1;y++){
                         jobj2=jarr.getJSONObject(y);
                         jobj2.getString("amount");
                         result +="<value xid=\""+y+"\">"+jobj2.getString("amount")+"</value>";
                       }
                         result += "</graph></graph></chart>";
                   }
                catch(Exception e){
                        e.getMessage();
                }
                break;
                case 3:
                    result=hrmsDbcon.getTimesheetChart(request);
                break;
                case 4:
                    result=hrmsDbcon.getTimesheetChart1(request);
                break;
        }
        out.print(result);

%>
