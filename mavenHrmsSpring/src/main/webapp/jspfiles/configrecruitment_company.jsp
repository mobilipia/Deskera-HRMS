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
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>

<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />


<%
        Connection conn = null;

        try {
            PreparedStatement pstmt = null;
            
            ConfigReader configReader = ConfigReader.getinstance();
            String driver = configReader.get("driver");
            String serverIP = configReader.get("serverIP");
            String port = configReader.get("port");
            String fromDB = configReader.get("dbName");
            String userName = configReader.get("userName");
            String password = configReader.get("password");
            
            String connectionURL = "jdbc:mysql://"+serverIP+":"+port+"/"+fromDB+"?user="+userName+"&password="+password;
            
            Class.forName(driver).newInstance();;
            conn = DriverManager.getConnection(connectionURL);
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();


            String companysql = "select companyid,companyname from "+fromDB+".company";
            PreparedStatement pstmtcompany = conn.prepareStatement(companysql);
            ResultSet rs =pstmtcompany.executeQuery();

            while(rs.next()){
            
                String companyid = rs.getString("companyid");
                String companyname = rs.getString("companyname");
                String titleid= UUID.randomUUID().toString();
                String countryid= UUID.randomUUID().toString();
                String preferedid= UUID.randomUUID().toString();
                out.println("<br> Executing Delete Querys ");

                String deleteconfigsql = "delete from "+fromDB+".configrecruitment where company=?";

                pstmt = conn.prepareStatement(deleteconfigsql);
                pstmt.setString(1, companyid);
                int deleteconfigsqlcount = pstmt.executeUpdate();

                

                String configrecruitmentsql = "INSERT INTO "+fromDB+".`configrecruitment` (`configid`,`configtype`,`formtype`,`position`,`name`,`Colnum`,`deleteflag`,`issystemproperty`,`allownull`,`visible`,  `company`) VALUES ";
                String configrecruitmentsql1 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',0,'Personal',3,'Last Name',2,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Personal',2,'First Name',1,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',7,'Personal',5,'Email ID',3,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Contact',1,'Contact No',4,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',5,'other',3,'Resume',5,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',4,'Contact',6,'Address',6,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Personal',4,'Date of Birth',7,'F','T','T','T','"+companyid+"') ";

                String configrecruitmentsql2 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',2,'Academic',6,'Graduation Passing Date',10,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Personal',6,'Applied Date',9,'F','T','T','F','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',1,'Current Organization',11,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Academic',5,'Graduation Percentage',8,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',7,'Personal',7,'Other Email Id',13,'F','F','T','T','"+companyid+"') ";
                String configrecruitmentsql3 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',6,'Contact',2,'Alternative Contact No.',14,'F','T','T','T','"+companyid+"'),('"+countryid+"',3,'Contact',3,'Country',15,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Contact',4,'State',16,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Contact',5,'City',17,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',7,'Post Graduation Degree',12,'F','T','T','T','"+companyid+"') ";
                String configrecruitmentsql4 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',0,'Academic',1,'Graduation Degree',18,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',2,'Graduation Specialization',19,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',3,'Graduation University',20,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',4,'Graduation College',21,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',8,'Post Graduation Specialization',22,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',9,'Post Graduation University',23,'F','T','T','T','"+companyid+"')";
                String configrecruitmentsql5 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',0,'Academic',10,'Post Graduation College',24,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Academic',11,'Post Graduation Percentage',25,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Academic',12,'Post Graduation Passing Date',26,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',2,'Current Designation',27,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',3,'Current Industry Type',28,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',4,'Experience',29,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',4,'Work',5,'Functional Expertise',30,'F','T','T','T','"+companyid+"') ";
                String configrecruitmentsql6 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',6,'Work',6,'Gross CTC',31,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Work',7,'Expected CTC',32,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',4,'other',1,'Key Skills',33,'F','T','T','T','"+companyid+"'),('"+preferedid+"',3,'other',2,'Preferred Interview Location',34,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'other',4,'Mother\\'s Maiden Name',35,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',14,'Other Degree Details ',37,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',13,'Other Degree',36,'F','F','T','T','"+companyid+"') " ;
                String configrecruitmentsql7 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',6,'Academic',15,'Other Percentage ',38,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Academic',16,'Other Passing Date ',39,'F','F','T','T','"+companyid+"'),('"+titleid+"',3,'Personal',1,'Title',40,'F','T','F','T','"+companyid+"') ";

                String configrecruitmentmastersql = "INSERT INTO "+fromDB+".`configrecruitmentmaster` (masterid,masterdata,configid)  ";
                String Mrid = UUID.randomUUID().toString();
                String Mrsid = UUID.randomUUID().toString();
                String Missid = UUID.randomUUID().toString();

                String Countryid = UUID.randomUUID().toString();
                String Prefereid = UUID.randomUUID().toString();

                String configrecruitmentmastersql1 = configrecruitmentmastersql + " VALUES ('"+Mrid+"','Mr','"+titleid+"'),('"+Mrsid+"','Mrs','"+titleid+"'),('"+Missid+"','Ms','"+titleid+"') ";
                String configrecruitmentmastersql2 = configrecruitmentmastersql + " select '"+Countryid+"',value,'"+countryid+"' from "+fromDB+".MasterData where masterid=11 limit 1 " ;
                String configrecruitmentmastersql3 = configrecruitmentmastersql + " select '"+Prefereid+"',value,'"+preferedid+"' from "+fromDB+".MasterData where masterid=18 limit 1 " ;

                String configrecruitmentmastersql4 = configrecruitmentmastersql + " select uuid(),value,'"+countryid+"' from "+fromDB+".MasterData where masterid=11 limit 1,1000 " ;
                String configrecruitmentmastersql5 = configrecruitmentmastersql + " select uuid(),value,'"+preferedid+"' from "+fromDB+".MasterData where masterid=18  limit 1,1000 " ;


                String updateconfigrecruitmentdatasql1="update  "+fromDB+".configrecruitmentdata set col40='"+Mrid+"' where  company='"+companyid+"' ";

                // this sets default country and location as first record
                String updateconfigrecruitmentdatasql2="update  "+fromDB+".configrecruitmentdata set col15='"+Countryid+"' where company='"+companyid+"' ";
                String updateconfigrecruitmentdatasql3="update  "+fromDB+".configrecruitmentdata set col34='"+Prefereid+"' where  company='"+companyid+"' ";
                
                pstmt = conn.prepareStatement(configrecruitmentsql1);
                int createdefaultcompanyconfigsqlcount1 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentsql2);
                int createdefaultcompanyconfigsqlcount2 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentsql3);
                int createdefaultcompanyconfigsqlcount3 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentsql4);
                int createdefaultcompanyconfigsqlcount4 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentsql5);
                int createdefaultcompanyconfigsqlcount5 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentsql6);
                int createdefaultcompanyconfigsqlcount6 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentsql7);
                int createdefaultcompanyconfigsqlcount7 = pstmt.executeUpdate();

                pstmt = conn.prepareStatement(configrecruitmentmastersql1);
                int configrecruitmentmastersqlcount1 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentmastersql2);
                int configrecruitmentmastersqlcount2 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentmastersql3);
                int configrecruitmentmastersqlcount3 = pstmt.executeUpdate();

                pstmt = conn.prepareStatement(configrecruitmentmastersql4);
                int configrecruitmentmastersqlcount4 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(configrecruitmentmastersql5);
                int configrecruitmentmastersqlcount5 = pstmt.executeUpdate();

                pstmt = conn.prepareStatement(updateconfigrecruitmentdatasql1);
                int updateconfigrecruitmentdatasqlcount1 = pstmt.executeUpdate();
                
                pstmt = conn.prepareStatement(updateconfigrecruitmentdatasql2);
                int updateconfigrecruitmentdatasqlcount2 = pstmt.executeUpdate();
                pstmt = conn.prepareStatement(updateconfigrecruitmentdatasql3);
                int updateconfigrecruitmentdatasqlcount3 = pstmt.executeUpdate();

                out.println("<br> delete Querys executed"+companyname);
                out.println("<br>deleteconfig :"+deleteconfigsqlcount);
                
                out.println("<br> Count for executed Querys => "+companyname);
                out.println("<br>configrecruitment1 :"+createdefaultcompanyconfigsqlcount1);
                out.println("<br>configrecruitment2 :"+createdefaultcompanyconfigsqlcount2);
                out.println("<br>configrecruitment3 :"+createdefaultcompanyconfigsqlcount3);
                out.println("<br>configrecruitment4 :"+createdefaultcompanyconfigsqlcount4);
                out.println("<br>configrecruitment5 :"+createdefaultcompanyconfigsqlcount5);
                out.println("<br>configrecruitment6 :"+createdefaultcompanyconfigsqlcount6);
                out.println("<br>configrecruitment7:"+createdefaultcompanyconfigsqlcount7);

                out.println("<br>configrecruitmentmastersqlcount1 :"+configrecruitmentmastersqlcount1);
                out.println("<br>configrecruitmentmastersqlcount2 :"+configrecruitmentmastersqlcount2);
                out.println("<br>configrecruitmentmastersqlcount3"+configrecruitmentmastersqlcount3);

                out.println("<br>configrecruitmentmastersqlcount4 :"+configrecruitmentmastersqlcount4);
                out.println("<br>configrecruitmentmastersqlcount5"+configrecruitmentmastersqlcount5);

                out.println("<br>updateconfigrecruitmentdatasqlcount1 :"+updateconfigrecruitmentdatasqlcount1);
                out.println("<br>updateconfigrecruitmentdatasqlcount2 :"+updateconfigrecruitmentdatasqlcount2);
                out.println("<br>updateconfigrecruitmentdatasqlcount3"+updateconfigrecruitmentdatasqlcount3);
            }
            
            conn.commit();
        }  catch (Exception ex) {
            out.println("<br>"+ex.getMessage());
            conn.rollback();
        }  finally {
            
            conn.close();
        }
%>
