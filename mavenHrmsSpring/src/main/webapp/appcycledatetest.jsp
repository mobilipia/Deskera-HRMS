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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import=" org.hibernate.*" %>
<%@ page import=" com.krawler.esp.hibernate.impl.HibernateUtil" %>
<%@ page import=" com.krawler.hrms.performance.Appraisalcycle" %>
<%
            Connection conn = null;
            try {
                ResultSet rs = null;
                ResultSet rs1 = null;
                PreparedStatement pstmt = null;
                PreparedStatement pstmt1 = null;
                PreparedStatement pstmt2 = null;
                String query = "";
                String roleid = "";
                String ip = request.getParameter("ip");
                String port = request.getParameter("port");
                String user = request.getParameter("user");
                String pass = request.getParameter("pass");
                String Db = request.getParameter("Db");
                String app = request.getParameter("app");
                String toDb = request.getParameter("todb");

                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

                System.out.println("<br>" + ip);
                System.out.println("<br>" + port);
                System.out.println("<br>" + user);
                System.out.println("<br>" + pass);
                System.out.println("<br>" + Db);
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + Db + "?user=" + user + "&password=" + pass);

                Date startdate=(Date) fmt.parse(request.getParameter("startdate"));
                Date enddate=(Date) fmt.parse(request.getParameter("enddate"));
                //Date submitstartdate=(Date) fmt.parse(request.getParameter("submitsdate"));
                //Date submitenddate=(Date) fmt.parse(request.getParameter("submitedate"));
                String cyclename = request.getParameter("cyclename");
                String cycleid = request.getParameter("cycleid");

                Session session1 = HibernateUtil.getCurrentSession();

                //String hql="from Appraisalcycle where id =?";
                //List tabledata=HibernateUtil.executeQuery(session1, hql, new Object[]{cycleid});
                Appraisalcycle appcycle = (Appraisalcycle)session1.get(Appraisalcycle.class, cycleid);
                //pstmt = conn.prepareStatement("select startdate,enddate,submitstartdate,submitenddate from "
                  //      + Db + ".appraisalcycle where id = ?");
                
                //pstmt.setString(1, cycleid);
                //rs = pstmt.executeQuery();
                System.out.println("<br>Fetched appraisal cycle data");
                %><h1>Fetched appraisal cycle data</h1><%
                
                    System.out.println("<br>Inside loop");
                //if(!startdate.equals(fmt.parse(rs.getString(1))) || !enddate.equals(fmt.parse(rs.getString(2)))) {
                if(!startdate.equals(appcycle.getStartdate()) || !enddate.equals(appcycle.getEnddate())) {
                %><h1>Dates are not equal</h1>Request Start Date=<%=startdate%><br>Db start date=<%=appcycle.getStartdate()%>
                    <br>Request End Date=<%=enddate%><br>Db end date=<%=appcycle.getEnddate()%>
                    <%
                } else {
                    %><h1>Dates are equal</h1>Request Start Date=<%=startdate%><br>Db start date=<%=appcycle.getStartdate()%>
                    <br>Request End Date=<%=enddate%><br>Db end date=<%=appcycle.getEnddate()%><%
                }
                
                
                //pstmt.close();
                //rs.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                out.println("<br>" + ex.getMessage());
                //conn.rollback();
            } finally {

                conn.close();
            }

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
