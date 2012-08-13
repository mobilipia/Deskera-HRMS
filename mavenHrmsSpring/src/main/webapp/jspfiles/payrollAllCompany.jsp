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
<%@ page import="com.krawler.esp.utils.ConfigReader"%>
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
            int a2=0,a3=0,a4=0,a5=0,a6=0;
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ResultSet rs1 = null;


            String usertemplatesql = " select templateid, salary from useraccount where userid = ? ";
            String companysql = " select userid from users ";
            String insertMapTablesql = " insert into usertemplatemap values(?,?,?,?,?) ";
            PreparedStatement pstmtcompany = conn.prepareStatement(companysql);
            ResultSet rs =pstmtcompany.executeQuery();

            out.println("changes in usertemplatemap");
            while(rs.next()){
                pstmtcompany = conn.prepareStatement(usertemplatesql);
                pstmtcompany.setString(1, rs.getString("userid"));
                rs1 = pstmtcompany.executeQuery();
                if(rs1.next()){
                    if(rs1.getObject("templateid")!=null && rs1.getObject("salary")!=null && !rs1.getString("templateid").trim().equals("") && !rs1.getString("salary").trim().equals("")){
                        pstmtcompany = conn.prepareStatement(insertMapTablesql);
                        double sa = Double.parseDouble(rs1.getString("salary").toString());
                        sa = sa / 2.5;
                        String tempuid = java.util.UUID.randomUUID().toString();
                        pstmtcompany.setString(1, tempuid);
                        pstmtcompany.setDate(2, new java.sql.Date(sdf.parse("2010-09-01").getTime()));
                        pstmtcompany.setString(3, String.valueOf(sa));
                        pstmtcompany.setString(4, rs1.getString("templateid"));
                        pstmtcompany.setString(5, rs.getString("userid"));
                        a2 += pstmtcompany.executeUpdate();
                    }
                }
            }
            out.println("\nchanges in usertemplatemap=="+a2);

            String wagesql = " select * from wagemaster where rate = ? and computeon is null ";
            String deductionsql = " select * from deductionmaster where rate = ? and computeon is null ";
            String updatewagesql = " update wagemaster set computeon = ?, expr = ? where wageid = ? ";
            String updatedeductionsql = " update deductionmaster set computeon = ?, expr = ? where deductionid = ? ";
            pstmtcompany = conn.prepareStatement(wagesql);
            pstmtcompany.setString(1, "1");
            rs1 = pstmtcompany.executeQuery();

            out.println("changes in wagemaster");
            while(rs1.next()){
                pstmtcompany = conn.prepareStatement(updatewagesql);
                pstmtcompany.setString(1, "3");
                pstmtcompany.setString(2, "(add)-1");
                pstmtcompany.setString(3, rs1.getString("wageid"));
                a3 += pstmtcompany.executeUpdate();
            }
            out.println("\nchanges in wagemaster=="+a3);

            pstmtcompany = conn.prepareStatement(deductionsql);
            pstmtcompany.setString(1, "1");
            rs1 = pstmtcompany.executeQuery();

            out.println("changes in deductionmaster");
            while(rs1.next()){
                pstmtcompany = conn.prepareStatement(updatedeductionsql);
                pstmtcompany.setString(1, "3");
                pstmtcompany.setString(2, "(add)-1");
                pstmtcompany.setString(3, rs1.getString("deductionid"));
                a4 += pstmtcompany.executeUpdate();
            }
            out.println("\nchanges in deductionmaster=="+a4);

            String payHistorysql = " select historyid,payhistory.createdon as createdon,createdfor from payhistory inner join users on users.userid = payhistory.userID ";
            String updatePayhistorysql = " update payhistory set paycyclestart = ?, paycycleend = ? where historyid = ? ";

            pstmtcompany = conn.prepareStatement(payHistorysql);
            rs1 = pstmtcompany.executeQuery();

            out.println("changes in payhistory");
            while(rs1.next()){
                pstmtcompany = conn.prepareStatement(updatePayhistorysql);
                pstmtcompany.setDate(1, rs1.getDate("createdon"));
                pstmtcompany.setDate(2, rs1.getDate("createdfor"));
                pstmtcompany.setString(3, rs1.getString("historyid"));
                a5 += pstmtcompany.executeUpdate();
            }
            out.println("\nchanges in payhistory=="+a5);

            String templatesql = " select templateid from template where payinterval is null and effdate is null ";
            String updatetemplatesql = " update template set payinterval = ?, effdate = ? where templateid = ? ";

            pstmtcompany = conn.prepareStatement(templatesql);
            rs1 = pstmtcompany.executeQuery();

            out.println("changes in template");
            while(rs1.next()){
                pstmtcompany = conn.prepareStatement(updatetemplatesql);
                pstmtcompany.setString(1, "1");
                pstmtcompany.setString(2, "1");
                pstmtcompany.setString(3, rs1.getString("templateid"));
                a6 += pstmtcompany.executeUpdate();
            }
            out.println("\nchanges in template=="+a6);
            out.println("THE END");

            conn.commit();
        }  catch (Exception ex) {
            out.println("<br>"+ex.getMessage());
            conn.rollback();
        }  finally {
            conn.close();
        }
%>
