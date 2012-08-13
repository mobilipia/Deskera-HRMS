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
            
            boolean deleteflag = Boolean.parseBoolean(request.getParameter("delete"));
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(connectionURL);
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();

            

            String configrecruitmentsql = "INSERT INTO "+fromDB+".`configrecruitment` (`configid`,`configtype`,`formtype`,`position`,`name`,`Colnum`,`deleteflag`,`issystemproperty`,`allownull`,`visible`,  `company`) VALUES ";
            String configrecruitmentsql1 = configrecruitmentsql + " ('ff80808129c643d00129c65a291e000c',0,'Personal',3,'Last Name',2,'F','T','F','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129c643d00129c659b433000b',0,'Personal',2,'First Name',1,'F','T','F','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129c6b7210129c6c775730006',7,'Personal',5,'Email ID',3,'F','T','F','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129c6b7210129c6c7be4e0007',6,'Contact',1,'Contact No',4,'F','T','F','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129c6b7210129c6c8b3f80008',5,'other',3,'Resume',5,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129c6b7210129c6c92fce0009',4,'Contact',6,'Address',6,'F','T','F','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129c6b7210129c6cfae2c000e',2,'Personal',4,'Date of Birth',7,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6') ";
            String configrecruitmentsql2 = configrecruitmentsql + " ('ff80808129ca60fa0129caa9d815000f',2,'Academic',6,'Graduation Passing Date',10,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca370b0129ca384b8c0002',2,'Personal',6,'Applied Date',9,'F','T','T','F','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caaf5f150016',0,'Work',1,'Current Organization',11,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caa95aba000e',6,'Academic',5,'Graduation Percentage',8,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129ca9d6ff40004',7,'Personal',7,'Other Email Id',13,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6') ";
            String configrecruitmentsql3 = configrecruitmentsql + " ('ff80808129ca60fa0129ca9e05fc0005',6,'Contact',2,'Alternative Contact No.',14,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129ca9ecc730006',3,'Contact',3,'Country',15,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129ca9f45b30007',0,'Contact',4,'State',16,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129ca9f88e60008',0,'Contact',5,'City',17,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caaa5a4c0010',0,'Academic',7,'Post Graduation Degree',12,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6') ";
            String configrecruitmentsql4 = configrecruitmentsql + " ('ff80808129ca60fa0129caa08a78000a',0,'Academic',1,'Graduation Degree',18,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caa2fb1b000b',0,'Academic',2,'Graduation Specialization',19,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caa3a1a9000c',0,'Academic',3,'Graduation University',20,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caa40c14000d',0,'Academic',4,'Graduation College',21,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caacbdaa0011',0,'Academic',8,'Post Graduation Specialization',22,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caadf4450012',0,'Academic',9,'Post Graduation University',23,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6')";
            String configrecruitmentsql5 = configrecruitmentsql + " ('ff80808129ca60fa0129caae41fc0013',0,'Academic',10,'Post Graduation College',24,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caae7eca0014',6,'Academic',11,'Post Graduation Percentage',25,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caaebc110015',2,'Academic',12,'Post Graduation Passing Date',26,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129caafac9b0017',0,'Work',2,'Current Designation',27,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab004250018',0,'Work',3,'Current Industry Type',28,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab0514d0019',0,'Work',4,'Experience',29,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab0a533001a',4,'Work',5,'Functional Expertise',30,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6') ";
            String configrecruitmentsql6 = configrecruitmentsql + " ('ff80808129ca60fa0129cab0f7a5001b',6,'Work',6,'Gross CTC',31,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab167d3001c',6,'Work',7,'Expected CTC',32,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab1c1ef001d',4,'other',1,'Key Skills',33,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab24888001e',3,'other',2,'Preferred Interview Location',34,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129ca60fa0129cab32d83001f',0,'other',4,'Mother\\'s Maiden Name',35,'F','T','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129fdba440129fdc6a7ad0003',0,'Academic',14,'Other Degree Details ',37,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129fdba440129fdc65fd70002',0,'Academic',13,'Other Degree',36,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6') " ;
            String configrecruitmentsql7 = configrecruitmentsql + " ('ff80808129fdba440129fdc6eb910004',6,'Academic',15,'Other Percentage ',38,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129fdba440129fdc739a10005',2,'Academic',16,'Other Passing Date ',39,'F','F','T','T','a4792363-b0e1-4b67-992b-2851234d5ea6'),('ff80808129fde1ad0129fde436b40002',3,'Personal',1,'Title',40,'F','T','F','T','a4792363-b0e1-4b67-992b-2851234d5ea6') ";
            
            String configrecruitmentmastersql = "INSERT INTO "+fromDB+".`configrecruitmentmaster` (masterid,masterdata,configid)  ";

            String configrecruitmentmastersql1 = configrecruitmentmastersql + " VALUES ('ff80808129fde1ad0129fde4ec910003','Mr','ff80808129fde1ad0129fde436b40002'),('ff80808129fde1ad0129fde4fcb30004','Mrs','ff80808129fde1ad0129fde436b40002'),('ff80808129fde1ad0129fde557bc0005','Ms','ff80808129fde1ad0129fde436b40002') ";
            String configrecruitmentmastersql2 = configrecruitmentmastersql + " select id,value,'ff80808129ca60fa0129ca9ecc730006' from "+fromDB+".MasterData where masterid=11 " ;
            String configrecruitmentmastersql3 = configrecruitmentmastersql + " select id,value,'ff80808129ca60fa0129cab24888001e' from "+fromDB+".MasterData where masterid=18 " ;

            String allapplicationssql = " update "+fromDB+".allapplications set configjobapplicant = jobapplicantid,employeetype=4 where employeetype=0 ";
            String hrms_docssql = " update "+fromDB+".hrms_docs set referenceid=applicantid ";


            String configrecruitmentdatasql = " insert into "+fromDB+".configrecruitmentdata(   "+
                            "company/*j.company*/,id,/*j.applicantid*/  "+
                            "/* title */Col40,  "+
                            "/* firstname */Col1,/* lastname */Col2,/*email  */Col3,/*otheremail  */Col13,/* birthdate */Col7,  "+
                            "/*address  */Col6,/* city */Col17,/* state */Col16,/*country  */Col15,/*contactno  */Col4,/* mobileno */Col14,  "+
                            "/* graddegree */Col18,  "+
                            "/* gradspecialization */Col19,/* gradCollege */Col21,/* graduniversity */Col20,/* gradpercent */Col8,/* gradpassdate */Col10,  "+
                            "/* pgqualification */Col12,/*pgspecialization  */Col22,/* pgCollege */Col24,/*pguniversity  */Col23,/*pgpercent  */Col25,  "+
                            "/* pgpassdate */Col26,/* otherqualification */Col36,/* otherdetails */Col37,/*otherpercent  */Col38,/* otherpassdate */Col39,  "+
                            "/* Yearmonth */Col29,/* functionalexpertise */Col30,/* currentindustry */Col28,/*currentorganization  */Col11,/* currentdesignation */Col27,  "+
                            "/* grosssalary */Col31,/* expectedsalary */Col32,/*keyskills  */Col33,/*interviewlocation  */Col34,/* docid */Col5,/*jobposid*/  "+
                            "referenceid,deleted)  "+

                            "select j.company,j.applicantid,  "+
                            "case j.title when 'Mr' then 'ff80808129fde1ad0129fde4ec910003' when 'Mrs' then 'ff80808129fde1ad0129fde4fcb30004' when 'Ms' then 'ff80808129fde1ad0129fde557bc0005' end as title1  "+
                            ",j.firstname,j.lastname,j.email,j.otheremail,j.birthdate,  "+
                            "j.address1,j.city,j.state,j.country,j.contactno,j.mobileno,  "+
                            "j.graddegree,j.gradspecialization,j.gradCollege,j.graduniversity,  "+
                            "j.gradpercent,j.gradpassdate,j.pgqualification,j.pgspecialization,  "+
                            "j.pgCollege,j.pguniversity,j.pgpercent,j.pgpassdate,j.otherqualification,  "+
                            "j.otherdetails,j.otherpercent,j.otherpassdate,  "+
                            "concat(ifnull(j.experienceyear,0) ,' Years ',ifnull(j.experiencemonth,0),' months') as Yearmonth,  "+
                            "j.functionalexpertise,j.currentindustry,j.currentorganization,j.currentdesignation,  "+
                            "j.grosssalary,j.expectedsalary,j.keyskills,j.interviewlocation,hd.docid,ap.jobposid,j.deleteflag  "+
                            "from   "+fromDB+".jobapplicant j  "+
                            "left join   "+fromDB+".hrms_docsmap hd on hd.recid=j.applicantid  "+
                            "left join   "+fromDB+".allapplications ap  on ap.jobapplicantid = j.applicantid  "+
                            "group by j.applicantid;";

            if(deleteflag){

                out.println("<br> Executing Delete Querys ");

                String deleteconfigsql = "delete from "+fromDB+".configrecruitment";
                String deleteconfigrecruitmentmaster = "delete from "+fromDB+".configrecruitmentmaster";
                String deleteconfigrecruitmentdata = "delete from "+fromDB+".configrecruitmentdata";
                String deleteallapplications = "delete from "+fromDB+".allapplications where employeetype=4";

                pstmt = conn.prepareStatement(deleteconfigsql);
                int deleteconfigsqlcount = pstmt.executeUpdate();

                pstmt = conn.prepareStatement(deleteconfigrecruitmentmaster);
                int deleteconfigrecruitmentmastercount = pstmt.executeUpdate();

                pstmt = conn.prepareStatement(deleteconfigrecruitmentdata);
                int deleteconfigrecruitmentdatacount = pstmt.executeUpdate();

                pstmt = conn.prepareStatement(deleteallapplications);
                int deleteallapplicationscount = pstmt.executeUpdate();

                out.println("<br> delete Querys executed");

                out.println("<br>deleteconfig :"+deleteconfigsqlcount);
                out.println("<br>deleteconfigrecruitmentmaster :"+deleteconfigrecruitmentmastercount);
                out.println("<br>deleteconfigrecruitmentdata :"+deleteconfigrecruitmentdatacount);
                out.println("<br>deleteallapplications :"+deleteallapplicationscount);
            }
            pstmt = conn.prepareStatement(configrecruitmentsql1);
            int configrecruitmentsql1count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentsql2);
            int configrecruitmentsql2count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentsql3);
            int configrecruitmentsql3count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentsql4);
            int configrecruitmentsql4count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentsql5);
            int configrecruitmentsql5count = pstmt.executeUpdate();


            pstmt = conn.prepareStatement(configrecruitmentsql6);
            int configrecruitmentsql6count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentsql7);
            int configrecruitmentsql7count = pstmt.executeUpdate();


            pstmt = conn.prepareStatement(configrecruitmentmastersql1);
            int configrecruitmentmastersql1count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentmastersql2);
            int configrecruitmentmastersql2count = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentmastersql3);
            int configrecruitmentmastersql3count =pstmt.executeUpdate();



            pstmt = conn.prepareStatement(allapplicationssql);
            int allapplicationssqlcount =pstmt.executeUpdate();

            pstmt = conn.prepareStatement(hrms_docssql);
            int hrms_docssqlcount =pstmt.executeUpdate();

            pstmt = conn.prepareStatement(configrecruitmentdatasql);
            int configrecruitmentdatasqlcount =pstmt.executeUpdate();

            out.println("<br> Count for executed Querys ");

           


            out.println("<br>configrecruitment1 :"+configrecruitmentsql1count);
            out.println("<br>configrecruitment2 :"+configrecruitmentsql2count);
            out.println("<br>configrecruitment3 :"+configrecruitmentsql3count);
            out.println("<br>configrecruitment4 :"+configrecruitmentsql4count);
            out.println("<br>configrecruitment5 :"+configrecruitmentsql5count);
            out.println("<br>configrecruitment6 :"+configrecruitmentsql6count);
            out.println("<br>configrecruitment7:"+configrecruitmentsql7count);
            out.println("<br>configrecruitmentmaster :"+configrecruitmentmastersql1count);
            out.println("<br>configrecruitmentmaster :"+configrecruitmentmastersql2count);
            out.println("<br>configrecruitmentmaster :"+configrecruitmentmastersql3count);
            out.println("<br>allapplication :"+allapplicationssqlcount);
            out.println("<br>hrms_docs :"+hrms_docssqlcount);
            out.println("<br>configrecruitmentdata :"+configrecruitmentdatasqlcount);

            conn.commit();
        }  catch (Exception ex) {
            out.println("<br>"+ex.getMessage());
            conn.rollback();
        }  finally {
            
            conn.close();
        }
%>
