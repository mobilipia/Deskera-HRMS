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
         import="com.krawler.common.util.*"  %>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%
        String company = URLUtil.getDomainName(request);
        String designation = request.getParameter("type");
        JSONObject jobj = new JSONObject();
        int flag = !StringUtil.isNullOrEmpty(request.getParameter("flag")) ? Integer.parseInt(request.getParameter("flag")) : 1;
        boolean status = false;
        String msg = "";
        String imageURL="http://apps.deskera.com";
        try {
            if(StringUtil.isStandAlone()){
                imageURL=URLUtil.getPageURL(request, "");
            }
            switch (flag) {
                case 1:
                    if (!StringUtil.isNullOrEmpty(company) && !StringUtil.isNullOrEmpty(designation)) {
                        jobj = hrmsDbcon.getDummyStatus(company, designation, request);
                        status = Boolean.parseBoolean(jobj.getString("success"));
                        msg = jobj.getString("msg");
                    } else {
                        status = false;
                        msg = "Invalid parameters. Please contact on an email support@deskera.com.";
                    }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <link rel="shortcut icon" href="../../images/deskera.png"/>
        <link href="http://signup2.deskera.com/style/style.css" rel="stylesheet" type="text/css" />
        <link href="../../style/form.css" rel="stylesheet" type="text/css" />
        <link href="../../style/style.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="../../lib/jquery-1.2.2.min.js"></script>
        <script type="text/javascript" src="../../scripts/core/42.js"></script>
        <script type="text/javascript" src="../../scripts/belongToUs.js?v=5"></script>
        <title>Deskera HRMS - Selecting Anonymous Id for Appraisal</title>
        <style type="text/css" media="screen">
            body{font-family:tahoma,arial,sans-serif;}
            .mainDiv{width:70px;margin-left:30%;margin-right:auto;padding:5px;/*background-color:#F1F1F1;*/}
            td div{margin-left : 17px}
        </style>
    </head>
    <body style="background-color:white;">
        <%if (status) {%>
        <div id="top-bg" style="background-image:none;">
            <div id="top">
                <div class="companylogo" style="float:left; margin-top:10px;">
                    <img src='<%=imageURL%>/b/<%=company%>/images/store?company=true&original=true'/>
                </div>
            </div>
        </div>
        <div id="middle-container" style="background-color:white;">
            <div id="odl-middle" style="background-color:white;">
                <div id="error-log" style="background-color:white;"></div>
                <div id="ajaxResult" style="background-color:white;"></div>
                <div class="sign-form-container" style="background-color:white;">
                    <%=msg%>
                </div>
            </div>
            <div class="footer" style="background-color:white;">
            </div>
        </div>
        <%} else {%>
        <div id="middle-container" style="background-color:white;">
            <div id="odl-middle" style="background-color:white;">
                <div id="error-log" style="background-color:white;"></div>
                <div id="ajaxResult" style="background-color:white;"></div>
                <div class="sign-form-container" style="padding-top:10px;">
                    <%=msg%>
                </div>
            </div>
            <div class="footer" style="background-color:white;">
            </div>
        </div>
        <%}%>
    </body>
</html>
<%                    break;
               case 2:
                    jobj = hrmsDbcon.setDummyStatus(request);
                    msg = jobj.getString("msg");
                    request.setAttribute("msg", msg);
                    request.setAttribute("success", jobj.getString("success"));
                    //String location = "http://apps.deskera.com/b/"+company+"/changepass.jsp?radio="+request.getParameter("radio")+"&pass="+request.getParameter("pass")+"&msg="+msg;
                    String location = "http://192.168.0.141:8080/deskeraplatform/b/"+company+"/changepass.jsp?radio="+request.getParameter("radio")+"&pass="+request.getParameter("pass")+"&msg="+msg;
                    jobj.put("location",location);
                    out.print(jobj);
                    break;
            }
        } catch (Exception e) {
            status = false;
            msg = "Problem occured while performing operation. Please contact on an email support@deskera.com.";
        }
%>
