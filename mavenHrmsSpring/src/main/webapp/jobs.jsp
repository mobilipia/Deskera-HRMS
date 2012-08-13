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
<%@ page import="com.krawler.esp.database.hrmsDbcon"
         import="com.krawler.common.util.*"%>
<%@ page import="com.krawler.utils.json.base.*"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%
        String company = URLUtil.getDomainName(request);
        String designation = request.getParameter("jobid");
        String url = "";
        JSONObject jobj = new JSONObject();
        int flag = !StringUtil.isNullOrEmpty(request.getParameter("flag")) ? Integer.parseInt(request.getParameter("flag")) : 1;
        boolean status = false;
        String msg = hrmsDbcon.getLocalTextforJsp(1, company);/* "An error occurred while performing operation. Please contact support at support@deskera.com.";//*/
        String imageURL="http://apps.deskera.com";
        try {
            switch (flag) {
                case 1:
                    if (!StringUtil.isNullOrEmpty(company) && !StringUtil.isNullOrEmpty(designation)) {
                        jobj = hrmsDbcon.getJobsforJsp(company, designation, request);
                        status = Boolean.parseBoolean(jobj.getString("success"));
                        url = jobj.has("url")?jobj.getString("url"):"";
                    } else {
                        status = false;
                        msg = jobj.has("msg")?jobj.getString("msg"):hrmsDbcon.getLocalTextforJsp(2, company);/*"Either you are coming from an old link or bookmark, or this job does not exist.";//*/
                    }
                    }
            if(StringUtil.isStandAlone()){
                imageURL=URLUtil.getPageURL(request, "");
            }
        } catch (Exception e) {
            status = false;
            msg =hrmsDbcon.getLocalTextforJsp(1, company);/*  "An error occurred while performing operation. Please contact support at support@deskera.com.";//*/
        }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link rel="shortcut icon" href="../../images/deskera.png"/>
       <!-- <link href="http://signup2.deskera.com/style/style.css" rel="stylesheet" type="text/css" /-->
        <link href="../../style/view.css" rel="stylesheet" type="text/css" />
        <title>
            <%if (status ||!StringUtil.isNullOrEmpty(company)&&jobj.has("companyname")) {
            if(jobj.has("jobname")){
                out.print(jobj.getString("jobname")+" [" + jobj.getString("jobid") + "] " + " - " + jobj.getString("companyname") + "");
                }else{
                   out.print("" + jobj.getString("companyname") + " - "+hrmsDbcon.getLocalTextforJsp(3, company));
                }
            } else {
                out.print("Deskera HRMS-Job Page");
            }
            %>
        </title>
        <style type="text/css" media="screen">
            body{font-family:tahoma,arial,sans-serif; overflow:auto;background-color:white;text-align:left;font-size:14px;}               
        </style>
        <!--[if IE ]>
            <style type="text/css" media="screen">
                body{font-family:tahoma,arial,sans-serif; overflow:auto;background-color:white;text-align:center;font-size:14px;}
            </style>
        <![endif]-->
    </head>
    <body >
        <div class="container-main" >
            <div class="container">
                <div class="wrapper" >
                    <div id="top-bg" style="text-align:left;" >
                        <div class="companylogo" style="margin-top:22px">
                            <img src='<%=imageURL%>/b/<%=company%>/images/store?company=true&original=true'/>
                            <span class="jobheader" style="margin-left:25px"><%=jobj.getString("companyname")%></span>
                        </div>
                    </div>
                    <%if (status) {%>
                    
                    <div style="margin-top:5%;text-align:left;">
                         <p style="margin-top:15px"><INPUT TYPE=BUTTON  VALUE='Apply Online' ONCLICK="_redirect('<%=url%>')" class="applybutton" id="applybtn1"/></p>
                        <span class="jobheader"><%=jobj.getString("jobname") + " [" + jobj.getString("jobid") + "]"%></span>
                        <%if(jobj.has("desc")){%><p><span class="jobtext1"><%String newVal=jobj.getString("desc").replace("\n","<br/>");%><%=newVal%></span></p><%}%>
                        <%if(jobj.has("responsibility")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(10, company)%>:</span>&nbsp;<ul>
                        <%JSONArray jarr=new JSONArray(jobj.getString("responsibility"));
                           for(int i=0;i<jarr.length();i++){
                                JSONObject robj = jarr.getJSONObject(i);
                                if(!StringUtil.isNullOrEmpty(robj.getString("responsibility").trim())){
                               %>
                               <li><span class="jobtext1"><%=robj.getString("responsibility")%></span> </li>
                          <% }}
                        %>
                        </ul></p><%}%>
                        <%if(jobj.has("skills")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(11, company)%>:</span>&nbsp;<ul>
                        <%JSONArray jarr=new JSONArray(jobj.getString("skills"));
                           for(int i=0;i<jarr.length();i++){
                                JSONObject robj = jarr.getJSONObject(i);
                                if(!StringUtil.isNullOrEmpty(robj.getString("skilldesc").trim()) || !StringUtil.isNullOrEmpty(robj.getString("skill").trim())){
                               %>
                               <li><span class="jobtext1"><%=robj.getString("skill")%></span>
                               <span class="jobtext1"><% String str="";
                               if(!StringUtil.isNullOrEmpty(robj.getString("skilldesc").trim()) && !StringUtil.isNullOrEmpty(robj.getString("skill").trim())){
                                   str+=" - ";
                               }
                               str+=robj.getString("skilldesc");
                               out.print(str);
                               %> </span></li>
                          <% }}
                        %>
                        </ul></p><%}%>
                        <% try{if(jobj.has("qualification")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(12, company)%>:</span>&nbsp;<ul>
                        <%JSONArray jarr=new JSONArray(jobj.getString("qualification"));
                           for(int i=0;i<jarr.length();i++){
                                JSONObject robj = jarr.getJSONObject(i);
                                if(!StringUtil.isNullOrEmpty(robj.getString("qualification").trim()) || !StringUtil.isNullOrEmpty(robj.getString("qualificationdesc").trim())){
                               %>
                               <li><span class="jobtext1"><%=robj.getString("qualification")%></span>
                               <span class="jobtext1"><% String str="";
                               if(!StringUtil.isNullOrEmpty(robj.getString("qualification").trim()) && !StringUtil.isNullOrEmpty(robj.getString("qualificationdesc").trim())){
                                   str+=" - ";
                               }
                               str+=robj.getString("qualificationdesc");
                               out.print(str);
                               %></span> </li>
                          <% }}
                        %>
                        </ul></p><%}}catch(Exception e){
                          }%>
                        <%if(jobj.has("experience")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(4, company)%>:</span>&nbsp;<span class="jobtext1"><%=jobj.getString("experience")%></span></p><%}%>
                        <%if(jobj.has("location")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(5, company)%>:</span>&nbsp;<span class="jobtext1"><%=jobj.getString("location")%></span></p><%}%>
                        <%if(jobj.has("shift")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(6, company)%>:</span>&nbsp;<span class="jobtext1"><%=jobj.getString("shift")%></span></p><%}%>
                        <%if(jobj.has("relocation")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(7, company)%>:</span>&nbsp;<span class="jobtext1"><%=jobj.getString("relocation")%></span></p><%}%>
                        <%if(jobj.has("travel")){%><p><span class="jobheader1"><%=hrmsDbcon.getLocalTextforJsp(8, company)%>:</span>&nbsp;<span class="jobtext1"><%=jobj.getString("travel")%></span></p><%}%>
                        <p style="margin-top:15px"><INPUT TYPE=BUTTON  VALUE='Apply Online' ONCLICK="_redirect('<%=url%>')" class="applybutton" id="applybtn2"/></p>
                    </div>
                    </div>
                    </div>
                </div>
        <%} else {%>
        <div class="sign-form-container" style="border: 1px solid #ddd; padding: 20px; margin-top: 40px; font-size: 20px; text-align: center;">
            <%=jobj.has("msg")?jobj.getString("msg"):msg%>
        </div>
        <%}%>        
        <script type="text/javascript">
            if(document.getElementById("applybtn1")!=null)
            document.getElementById("applybtn1").value="<%=hrmsDbcon.getLocalTextforJsp(9, company)%>";
            if(document.getElementById("applybtn2")!=null)
            document.getElementById("applybtn2").value="<%=hrmsDbcon.getLocalTextforJsp(9, company)%>";
            function _redirect(url){
                window.top.location.href =url;
            }
        </script>
    </body>
</html>

