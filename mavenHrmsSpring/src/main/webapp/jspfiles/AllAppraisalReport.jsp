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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>


<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>

<% 
	String domainurl = URLUtil.getDomainName(request);
	domainurl = URLUtil.getDomainURL(domainurl,false);

	ConfigReader configReader = ConfigReader.getinstance();
	String driver = configReader.get("driver");
	String serverIP = configReader.get("serverIP");
	String port = configReader.get("port");
	String dbName = configReader.get("dbName");
	String userName = configReader.get("userName");
	String password = configReader.get("password");
	Connection conn = null;
	String connectionURL = "jdbc:mysql://"+serverIP+":"+port+"/"+dbName+"?user="+userName+"&password="+password;
	try {
		String app_cycle=request.getParameter("appid");
		Boolean isMod = false;
		if(!StringUtil.isNullOrEmpty(request.getParameter("isMod"))){
			isMod = Boolean.parseBoolean(request.getParameter("isMod"));
		}
		Class.forName(driver);
		conn = DriverManager.getConnection(connectionURL);
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement("select appraisalid,employee,manager from appraisalmanagement where appcycle=? group by employee");
		pstmt.setString(1,app_cycle);
		ResultSet rs = pstmt.executeQuery();
		
		%>
		<br />
		<br />
		<br />
		<table border="1">
		<tr>
		<th>Employee Name</th>
		<th>Appraiser Name</th>
		<th>Employee Score</th>
		<th>Appraiser Score</th>
		<th>Overall Competency Score</th>
		</tr><%
		NumberFormat nf = new DecimalFormat("#.##");
		while(rs.next()){
			String emp_name ="";
			String mng_name ="";
			String emp_scroe="";
			String manager_scroe="";
			
			List<Double> score = new ArrayList<Double>();
			
			String emp_quer="select concat(e.fname,' ',e.lname) as employee_name, a.competency, a.compemprating,a.compmanrating from appraisal as a inner join appraisalmanagement as am inner join users as e on a.appraisal=am.appraisalid and am.employee=e.userid where am.appcycle=? and am.employee=? and am.employeestatus=1 group by a.competency";
			PreparedStatement pstmte = null;
			pstmte = conn.prepareStatement(emp_quer);
			pstmte.setString(1,app_cycle);
			pstmte.setString(2,rs.getString("employee"));
			ResultSet rse = pstmte.executeQuery();
			
			while(rse.next()){
				if(rse.isLast()){
					emp_scroe+=Integer.parseInt(rse.getString("compemprating"))+"";
				}else{
					emp_scroe+=Integer.parseInt(rse.getString("compemprating"))+", ";
				}
			}
			
			/*********/
			String emp="select fname, lname from users where userid=?";
			PreparedStatement pstmtemp = null;
			pstmtemp = conn.prepareStatement(emp);
			pstmtemp.setString(1,rs.getString("employee"));
			ResultSet rsemp = pstmtemp.executeQuery();
			while(rsemp.next()){
				emp_name=rsemp.getString("fname")+" "+rsemp.getString("lname");	
			}
			/*********/
			
			String comptency="select a.competency from appraisal as a inner join appraisalmanagement as am on a.appraisal=am.appraisalid where am.appcycle=? and am.employee=? and am.managerstatus=1 group by a.competency";
			PreparedStatement pstmtcomptency = null;
			pstmtcomptency = conn.prepareStatement(comptency);
			pstmtcomptency.setString(1,app_cycle);
			pstmtcomptency.setString(2,rs.getString("employee"));
			ResultSet rscomptency = pstmtcomptency.executeQuery();
			
			while(rscomptency.next()){
				String manager_quer="select concat(m.fname,' ',m.lname) as manager_name, a.competency,a.compemprating,a.compmanrating from appraisal as a inner join appraisalmanagement as am inner join users as m on a.appraisal=am.appraisalid and am.manager=m.userid where am.appcycle=? and am.employee=? and a.competency=? and am.managerstatus=1 order by a.competency,manager_name";
				PreparedStatement pstmtm = null;
				pstmtm = conn.prepareStatement(manager_quer);
				pstmtm.setString(1,app_cycle);
				pstmtm.setString(2,rs.getString("employee"));
				pstmtm.setString(3,rscomptency.getString("competency"));
				ResultSet rsm = pstmtm.executeQuery();
				
				List<Integer> list = new ArrayList<Integer>();
				while(rsm.next()){
					list.add(Integer.parseInt(rsm.getString("compmanrating")));
					if(rscomptency.isFirst()){
						if(rsm.isLast()){
							mng_name+=rsm.getString("manager_name");
						}else{
							mng_name+=rsm.getString("manager_name")+", ";
						}
					}
				}
				
				Collections.sort(list);
				Double total = 0.0;
				if(isMod){
					if (list.size() > 2) {
		                for (int c = 1; c < list.size() - 1; c++) {
		                	total += list.get(c);
		                }
		                total= Double.valueOf(total / (list.size() - 2));
		            } else {
		                for (int c = 0; c < list.size(); c++) {
		                	total += list.get(c);
		                }
		                if(list.size()>0){
		                	total = Double.valueOf(total / list.size());
		                }
		            }
				}else{
					for (int c = 0; c < list.size(); c++) {
	                	total += list.get(c);
	                }
	                if(list.size()>0){
	                	total = Double.valueOf(total / list.size());
	                }
				}
				
				score.add(total);
				manager_scroe+=(nf.format(total)+", ");
			}
			
			Double overall_score = 0.0;
			for(Double val : score){
				overall_score+=val;
			}
			
			if(score.size()>0){
				overall_score=Double.parseDouble(nf.format(overall_score/score.size()));
			}
			
			//out.println(emp_name+"\t"+mng_name+"\t"+rs.getString("employeecompscore")+"\t"+rs.getString("managercompscore")+"\n");
			%>
			<tr>
			<td><%=emp_name%></td>
			<td><%=mng_name%></td>
			<td><%=emp_scroe%></td>
			<td><%=manager_scroe%></td>
			<td><%=overall_score%></td>
			</tr>
			
			<%	
		
			//out.println();
		}
		%></table><%
    	
	} catch (Exception ex) {
		ex.printStackTrace();
		out.println(ex);
	} finally{
    	if(conn != null) {
        	conn.close();
    } 
}
%>
