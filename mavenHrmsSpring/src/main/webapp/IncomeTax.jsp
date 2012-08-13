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
<%@page import="java.net.URLDecoder"%><script>
function myUnescape(str)
{
	return unescape(str);	
}
</script>

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
		String parent = null;
		String id =null;
		Class.forName(driver);
		conn = DriverManager.getConnection(connectionURL);
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		
		//####### Strat Child
		pstmt = conn.prepareStatement("select id from malaysian_deductions where name=?");
		pstmt.setString(1,"Child");
		rs = pstmt.executeQuery();
		while(rs.next()){
			parent=rs.getString("id");
		}
		
		//1
		pstmt = conn.prepareStatement("select id from malaysian_deductions where name=?");
		pstmt.setString(1,"Child over the age of 18 years and receiving full-time instruction at diploma level onwards in an institution of higher education in Malaysia");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		//2
		pstmt = conn.prepareStatement("select id from malaysian_deductions where name=?");
		pstmt.setString(1,"Child over the age of 18 years and receiving full-time instruction at degree level onwards in an institution of higher education outside Malaysia");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//3
		pstmt = conn.prepareStatement("select id from malaysian_deductions where name=?");
		pstmt.setString(1,"Disabled child as certified by the Department of Social Welfare");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//4
		pstmt = conn.prepareStatement("select id from malaysian_deductions where name=?");
		pstmt.setString(1,"Disabled child receiving further instruction at diploma level onwards in an institution of higher education in Malaysia or at degree level onwards in an institution of higher education outside Malaysia");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//5
		pstmt = conn.prepareStatement("select id from malaysian_deductions where name=?");
		pstmt.setString(1,"Child under the age of 18 years");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		//#######End Child
		
		
		
		//####### Strat Allowances1
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Gift of fixed line telephone, mobile phone, pager or Personal Digital Assistant (PDA) registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Gift of fixed line telephone");
		rs = pstmt.executeQuery();
		while(rs.next()){
			parent=rs.getString("id");
		}
		
		//1
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Gift of fixed line telephone, mobile phone, pager or Personal Digital Assistant (PDA) registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Fixed line telephone");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		//2
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Gift of fixed line telephone, mobile phone, pager or Personal Digital Assistant (PDA) registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Mobile phone");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//3
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Gift of fixed line telephone, mobile phone, pager or Personal Digital Assistant (PDA) registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Pager or Personal Digital Assistant (PDA)");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		//#######End Allowances1
		
		
		
		//####### Strat Allowances2
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Monthly bills for subscription of broadband, fixed line telephone, mobile phone, pager and PDA registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Monthly bills for subscription of broadband");
		rs = pstmt.executeQuery();
		while(rs.next()){
			parent=rs.getString("id");
		}
		
		//1
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Monthly bills for subscription of broadband, fixed line telephone, mobile phone, pager and PDA registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Subscription of broadband");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		//2
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Monthly bills for subscription of broadband, fixed line telephone, mobile phone, pager and PDA registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Fixed line telephone");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//3
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Monthly bills for subscription of broadband, fixed line telephone, mobile phone, pager and PDA registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Mobile phone");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//4
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Monthly bills for subscription of broadband, fixed line telephone, mobile phone, pager and PDA registered in the name of the employee or employer including cost of registration and installation.");
		pstmt.setString(2,"Pager and Personal Digital Assistant (PDA)");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		//#######End Allowances2
		
		
		
		
		//####### Strat Allowances3
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Subsidised interest for housing, education or car loan");
		rs = pstmt.executeQuery();
		while(rs.next()){
			parent=rs.getString("id");
		}
		
		//1
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Subsidised interest for Housing");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		//2
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Employer contribution of subsidised interest for Housing");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//3
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Subsidised interest for Education");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//4
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Employer contribution of subsidised interest for Education");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		//5
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Subsidised interest for Car");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		
		
		
		//6
		pstmt = conn.prepareStatement("select id from malaysian_deductions where description=? and name=?");
		pstmt.setString(1,"Subsidised interest for housing, education or car loan is fully exempted from tax if the total amount of loan taken in aggregate does not exceed RM300,000. If the total amount of loan exceeds RM300,000, the amount of subsidized interest to be exempted from tax is limited in accordance with the following formula: Where; A x B C A = is the difference between the amount of interest to be borne by the employee and the amount of interest payable by the employee in the basis period for a year of assessment; B = is the aggregate of the balance of the principal amount of housing, education or car loan taken by the employee in the basis period for a year of assessment or RM300,000, whichever is lower; C = is the total aggregate of the principal amount of housing, education or car loan taken by the employee.");
		pstmt.setString(2,"Employer contribution of subsidised interest for Car");
		rs = pstmt.executeQuery();
		while(rs.next()){
			id=rs.getString("id");
		}
		pstmt2 = conn.prepareStatement("update malaysian_deductions set parent=? where id=?");
		pstmt2.setString(1,parent);
		pstmt2.setString(2,id);
		pstmt2.executeUpdate();
		//#######End Allowances3
		
		
    	out.println("SUCCESS");
	} catch (Exception ex) {
		ex.printStackTrace();
		out.println(ex);
	} finally{
    	if(conn != null) {
        	conn.close();
    } 
}
%>
