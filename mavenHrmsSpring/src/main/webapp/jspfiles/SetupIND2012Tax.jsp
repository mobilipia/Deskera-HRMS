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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@ page import="com.krawler.esp.utils.ConfigReader" %>
<%@page import="com.krawler.common.util.StringUtil"%>

<%!
	ConfigReader configReader = ConfigReader.getinstance();
	String driver = configReader.get("driver");
	String serverIP = configReader.get("serverIP");
	String port = configReader.get("port");
	String dbName = configReader.get("dbName");
	String userName = configReader.get("userName");
	String password = configReader.get("password");
	Connection conn = null;
	String connectionURL = "jdbc:mysql://"+serverIP+":"+port+"/"+dbName+"?user="+userName+"&password="+password+"&useUnicode=yes&characterEncoding=UTF-8";
	ResultSet rs = null;
	PreparedStatement pstmt = null;
	
	public String getCompanyId(String subdomain){
		String companyid = null;
		try{
			String query="select companyid from company where subdomain=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, subdomain);
			rs = pstmt.executeQuery();
			while(rs.next()){
				companyid = rs.getString("companyid"); 
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return companyid;
	}
	
	public String getCostCenter(String companyid, String name){
		String id = null;
		try{
			pstmt = conn.prepareStatement("select id  from costcenter where name=? and company=?");
			pstmt.setString(1, name);
			pstmt.setString(2, companyid);
			rs  = pstmt.executeQuery();
			while(rs.next()){
				id = rs.getString("id");
			}
			
			if(id==null && StringUtil.isNullOrEmpty(id)){
				id = UUID.randomUUID().toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String query="insert into costcenter (id, name, code, company, creationdate) values (?, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				pstmt.setString(2, name);
				pstmt.setString(3, name);
				pstmt.setString(4, companyid);
				pstmt.setString(5, sdf.format(new Date()));
				pstmt.executeUpdate();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}
	
	
	public String createComponentType(String companyid, String name){
		String id = null;
		try{
			pstmt = conn.prepareStatement("select id  from MasterData where value=? and masterid=? and company=?");
			pstmt.setString(1, name);
			pstmt.setInt(2, 21);
			pstmt.setString(3, companyid);
			rs  = pstmt.executeQuery();
			while(rs.next()){
				id = rs.getString("id");
			}
			
			if(id==null && StringUtil.isNullOrEmpty(id)){
				id = UUID.randomUUID().toString();
				String query="insert into MasterData (id, masterid, value, company, weightage, componenttype, worktime) values (?, ?, ?, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				pstmt.setInt(2, 21);
				pstmt.setString(3, name);
				pstmt.setString(4, companyid);
				pstmt.setInt(5, 0);
				pstmt.setInt(6, 5);
				pstmt.setString(7, null);
				pstmt.executeUpdate();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}
	
	public boolean isScriptExecuted(String companyid, String name){
		boolean flag = false;
		try{
			String query="select compid from componentmaster where code=? and companyid=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, name);
			pstmt.setString(2, companyid);
			rs = pstmt.executeQuery();
			while(rs.next()){
				flag = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	public String createComponentRules(String companyid, String code, String description, String subtype, String costcenter, String paymentterm, int frequency, Double[][] val){
		String id = null;
		
		id = createComponent(companyid, code, description, subtype, costcenter, paymentterm, frequency);
		
		createRules(id, val);
		return id;
	}
	
	public String createComponent(String companyid, String code, String description, String subtype, String costcenter, String paymentterm, int frequency){
		String id = null;
		try{
			id = UUID.randomUUID().toString();
			
			String query="insert into componentmaster (compid, companyid, code, sdate, edate, description, subtype, frequency, costcenter, "
			+"paymentterm, amount, isadjust, isdefault, isblock, method, computeon, istaxablecomponent)"
			+" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, companyid);
			pstmt.setString(3, code);
			pstmt.setDate(4, null);
			pstmt.setDate(5, null);
			pstmt.setString(6, description);
			pstmt.setString(7, subtype);
			pstmt.setInt(8, frequency);
			pstmt.setString(9, costcenter);
			pstmt.setString(10, paymentterm);
			pstmt.setInt(11, 0);
			pstmt.setString(12, "T");
			pstmt.setString(13, "F");
			pstmt.setString(14, "F");
			pstmt.setInt(15, 3);
			pstmt.setString(16, null);
			pstmt.setString(17, "F");
			pstmt.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}
	
	public String createRules(String component, Double[][] val){
		String id = null;
		try{
			String query="insert into component_rule (id, lowerlimit, upperlimit, coefficient, component) values (?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			
			for(int i=0; i< val.length; i++){
				id = UUID.randomUUID().toString();
				pstmt.setString(1, id);
				pstmt.setDouble(2, val[i][0]);
				pstmt.setDouble(3, val[i][1]);
				pstmt.setDouble(4, val[i][2]);
				pstmt.setString(5, component);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}
%>
<% 
	try {
		String companyid = request.getParameter("companyid");
		
		if(companyid!=null){
			Class.forName(driver);
			conn = DriverManager.getConnection(connectionURL);
			out.print("<br /><br /><br />");
			
			String componentCode = "TAX-001";
			boolean scriptExecuted = isScriptExecuted(companyid, componentCode);
			if(scriptExecuted){
				out.println("<br /><br />Script all-ready executed.");
			}else{
				/*Create default cost center*/
				String costcenter = getCostCenter(companyid, "Default");
				
				/*Create income-tax component*/
				String type = createComponentType(companyid, "Income Tax");
				
				/*Payment-type Monthly*/
				String paymentterm = "ff80808133ceee3a0133cf04fe800011";
				
				
				int frequency = 0;
				/*Add income-tax component for Men*/
				Double[][] men = {
						{      0.0,    200000.0, 0.0},
						{ 200001.0,    500000.0, 10.0},
						{ 500001.0,   1000000.0, 20.0},
						{1000001.0, 999999999.0, 30.0}
					 };
				createComponentRules(companyid, componentCode, "IND-2012 Income-Tax (Men)", type, costcenter, paymentterm, frequency, men);
				out.println("Component <b>IND-2012 Income-Tax (Men)</b> added successfully.<br /><br />");
				
				/*Add income-tax component for Women*/
				Double[][] women = {
						{      0.0,    200000.0, 0.0},
						{ 200001.0,    500000.0, 10.0},
						{ 500001.0,   1000000.0, 20.0},
						{1000001.0, 999999999.0, 30.0}
					 };
				createComponentRules(companyid, "TAX-002", "IND-2012 Income-Tax (Women)", type, costcenter, paymentterm, frequency, women);
				out.println("Component <b>IND-2012 Income-Tax (Women)</b> added successfully.<br /><br />");
				
				/*Add income-tax component for Senior Citizens*/
				Double[][] seniorCitizens = {
						{      0.0,    250000.0, 0.0},
						{ 250001.0,    500000.0, 10.0},
						{ 500001.0,   1000000.0, 20.0},
						{1000001.0, 999999999.0, 30.0}
					 };
				createComponentRules(companyid, "TAX-003", "IND-2012 Income-Tax (Senior Citizens)", type, costcenter, paymentterm, frequency, seniorCitizens);
				out.println("Component <b>IND-2012 Income-Tax (Senior Citizens)</b> added successfully.<br /><br />");
				
				/*Add income-tax component for Very Senior Citizens*/
				Double[][] verySeniorCitizens = {
						{      0.0,    500000.0, 0.0},
						{ 500001.0,   1000000.0, 20.0},
						{1000001.0, 999999999.0, 30.0}
					 };
				createComponentRules(companyid, "TAX-004", "IND-2012 Income-Tax (Very Senior Citizens)", type, costcenter, paymentterm, frequency, verySeniorCitizens);
				out.println("Component <b>IND-2012 Income-Tax (Very Senior Citizens)</b> added successfully.<br /><br />");
			}
		}else{
			out.println("<br /><br />Please pass companyid.");	
		}
	} catch (Exception ex) {
		ex.printStackTrace();
		out.println(ex);
	} finally{
    	if(conn != null) {
        	conn.close();
    	}
	}
%>
