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
<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.esp.utils.ConfigReader"%>
<%@ page import="com.krawler.common.util.StringUtil" %>
<%@ page import="com.krawler.esp.database.*" %>
<%@ page import="com.krawler.utils.json.base.JSONObject" %>
<%@ page import="com.krawler.utils.json.base.JSONArray" %>
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>

<%
        Connection conn = null;

        try {
           PreparedStatement pstmt = null;
           
           ConfigReader configReader = ConfigReader.getinstance();
           String driver = configReader.get("driver");
           String serverIP = configReader.get("serverIP");
           String port = configReader.get("port");
           String dbName = configReader.get("dbName");
           String userName = configReader.get("userName");
           String password = configReader.get("password");
           
           String connectionURL = "jdbc:mysql://"+serverIP+":"+port+"/"+dbName+"?user="+userName+"&password="+password;
            String data = "";
            try {
                Class.forName(driver).newInstance();
                conn = DriverManager.getConnection(connectionURL);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            //rehire emp
            String companysql = " select joindate, userid from hrms_empprofile where joindate > relievedate and terminateflag = ? ";
            String updatesql = " update users set deleteflag = 0 where userid = ? ";
            String updatesql1 = " update hrms_empprofile set terminateflag = ? where userid = ? ";
            PreparedStatement pstmtcompany = conn.prepareStatement(companysql);
            pstmtcompany.setString(1, "T");
            ResultSet rs =pstmtcompany.executeQuery();
            java.util.Date now = new java.util.Date();
            java.util.Date jd = new java.util.Date();
            now = fmt.parse(fmt.format(now));
            while(rs.next()){
                String joinDate = rs.getString("joindate");
                jd = fmt.parse(joinDate);
                if(now.equals(jd)){
                    pstmt = conn.prepareStatement(updatesql);
                    pstmt.setString(1, rs.getString("userid"));
                    int y = pstmt.executeUpdate();
                    pstmt = conn.prepareStatement(updatesql1);
                    pstmt.setString(1, "F");
                    pstmt.setString(2, rs.getString("userid"));
                    int h = pstmt.executeUpdate();
                }
            }

            //terminate emp
            companysql = " select relievedate, userid from hrms_empprofile where joindate < relievedate and terminateflag = ? ";
            updatesql = " update users set deleteflag = 1 where userid = ? ";
            updatesql1 = " update hrms_empprofile set terminateflag = ? where userid = ? ";
            pstmtcompany = conn.prepareStatement(companysql);
            pstmtcompany.setString(1, "F");
            rs =pstmtcompany.executeQuery();
            now = new java.util.Date();
            jd = new java.util.Date();
            now = fmt.parse(fmt.format(now));
            while(rs.next()){
                String joinDate = rs.getString("relievedate");
                jd = fmt.parse(joinDate);
                if(now.equals(jd)){
                    pstmt = conn.prepareStatement(updatesql);
                    pstmt.setString(1, rs.getString("userid"));
                    pstmt.executeUpdate();
                    pstmt = conn.prepareStatement(updatesql1);
                    pstmt.setString(1, "T");
                    pstmt.setString(2, rs.getString("userid"));
                    pstmt.executeUpdate();
                }
            }
            conn.commit();
        }  catch (Exception ex) {
            out.println("<br>"+ex.getMessage());
            conn.rollback();
        }  finally {
            conn.close();
        }
%>
