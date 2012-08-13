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
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="com.krawler.common.admin.Useraccount" %>
<%@ page import="com.krawler.esp.hibernate.impl.HibernateUtil" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import=" org.hibernate.*" %>
<%@ page import="com.krawler.utils.json.base.JSONException" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="com.krawler.hrms.performance.Appraisal" %>
<%@ page import="com.krawler.hrms.performance.Appraisalmanagement" %>
<%@ page import="com.krawler.hrms.performance.Assignmanager" %>
<%@ page import="com.krawler.hrms.performance.Finalgoalmanagement" %>
<%@ page import="com.krawler.hrms.performance.GoalComments" %>
<%@ page import="com.krawler.hrms.performance.Managecmpt" %>
<%@ page import="com.krawler.hrms.performance.Mastercmpt" %>
<%@ page import="com.krawler.hrms.performance.competencyAvg" %>
<%@ page import="com.krawler.common.admin.User" %>

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
            
            String companyid = request.getParameter("companyid");
            try {
                Class.forName(driver).newInstance();
                conn = DriverManager.getConnection(connectionURL);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            conn.setAutoCommit(false);
            String filename = "exportAppraisal.csv";
            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();
            Session session1 = HibernateUtil.getCurrentSession();
            ByteArrayOutputStream baos = null;
            JSONObject res = getAppraisalReport(session1, request);
            baos = getCsvData(request, res);
            writeDataToFile(filename, baos, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
%>
<%!
        private ByteArrayOutputStream getCsvData(HttpServletRequest request, JSONObject jobj) throws ServiceException{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StringBuilder resSB = new StringBuilder();
                try{
                    JSONObject jo = new JSONObject();
                    JSONArray colHeader = new JSONArray();
                    
                    JSONArray colEmpArray = new JSONArray();
                    JSONArray colIndArray = new JSONArray();
                    colHeader = jobj.getJSONArray("data1").getJSONObject(0).getJSONArray("header");
                    colHeader.put("Overallavg");
                    colHeader.put("OverallManagerComment");
                    jo = jobj.getJSONArray("data1").getJSONObject(0).getJSONArray("data").getJSONObject(0);
                    colEmpArray = jo.getJSONArray("data");
                    colIndArray = jobj.getJSONArray("data1").getJSONObject(0).getJSONArray("indarr");
                    String header = "";
                    for (int i = 0; i < colHeader.length(); i++)
                            header += "\"" + colHeader.get(i).toString() + "\"~";
                    header = header.substring(0, (header.length() -1));
                    header += "\n";
                    resSB.append(header);
                    Map<String,String> dataMap = new HashMap<String, String>();
                    Map<String,String> dataAvgMap = new HashMap<String, String>();
                    Map<String,String> dataCommentMap = new HashMap<String, String>();
                    for(int s = 0; s < colEmpArray.length(); s++){
                        StringBuilder dstr = new StringBuilder(100);
                        JSONObject temp1 = colEmpArray.getJSONObject(s);
                        JSONArray colDataArray = new JSONArray();
                        colDataArray = colEmpArray.getJSONObject(s).getJSONArray("competencies");
                        dstr .append("\"").append(temp1.getString("empname")).append("\"~");
                        dataMap.clear();
                        dataAvgMap.clear();
                        dataCommentMap.clear();
                        for(int i = 0; i < colDataArray.length(); i++){
                                JSONObject temp = colDataArray.getJSONObject(i);
                                dataMap.put(temp.getString("comptename"), temp.getString("compmanwght"));
                                dataCommentMap.put(temp.getString("comptename"), temp.getString("comments").replaceAll("\n", "^").replaceAll("\r", "^"));
                                dataAvgMap.put(temp.getString("comptename"), temp.getString("nominalRat"));
                        }
                        for (int g = 1; g < colIndArray.length(); g++){
                            if(dataMap.containsKey(colIndArray.get(g).toString())){
                                dstr.append("\"").append(dataMap.get(colIndArray.get(g).toString())).append("\"~");
                                dstr.append("\"").append(dataAvgMap.get(colIndArray.get(g).toString())).append("\"~");
                                dstr.append("\"").append(dataCommentMap.get(colIndArray.get(g).toString())).append("\"~");
                            } else {
                                dstr.append("\"\"~\"\"~\"\"~");
                            }
                        }
                        dstr.append("\"").append(temp1.getString("manavgwght")).append("\"~");
                        dstr.append("\"").append(temp1.getString("mancom").replaceAll("\n", "^").replaceAll("\r", "^")).append("\"");
                        dstr.append("\n");
                        resSB.append(dstr);
                    }
                    baos.write(resSB.toString().getBytes());
                    System.out.println("\n End of getcsvdata");
        } catch (JSONException e) {
          //  Logger.getLogger(ExportCSVXLSReportServlet.class.getName()).log(Level.SEVERE, null, e);
            throw ServiceException.FAILURE("ExportCSVXLSReportServlet.getCsvData", e);
        } catch (IOException e) {
          //  Logger.getLogger(ExportCSVXLSReportServlet.class.getName()).log(Level.SEVERE, null, e);
            throw ServiceException.FAILURE("ExportCSVXLSReportServlet.getCsvData", e);
        }

        return baos;
    }


        public static JSONObject getAppraisalReport(Session session, HttpServletRequest request) throws ServiceException, JSONException {
        String result = "";
        List list = null,list1=null;
        JSONObject jobj = new JSONObject();
        JSONObject ans = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray mainArray = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        JSONArray headerArray = new JSONArray();
        JSONArray indexArray = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        String manComments = "";
        int counter = 1;
        try {
            String appCycleID = request.getParameter("appraisalcycleid");
            String companyID = request.getParameter("companyid");
            headerArray.put("Employee Name");
            indexArray.put("Employee Name");
            hql = "select a.employee from Appraisalmanagement a where a.appcycle.id=? and a.appcycle.company.companyID=? and a.reviewstatus=2 group by a.employee ";
            List<User> userList = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID, companyID});

            for (User u: userList) {
           
                hql = "from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2 order by rand()";
                list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID, u.getUserID()});
                count = list.size();
                if (list.size() > 0) {
                    Appraisalmanagement log = (Appraisalmanagement) list.get(0);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("empname", u.getFirstName() + " " + u.getLastName());
                    String empavgscore = "";
                    hql = "from Appraisal where appraisal.appraisalid = ? and competency is not null";
                    List lst = HibernateUtil.executeQuery(session, hql, new Object[]{log.getAppraisalid()});
                    JSONArray cmpObj = new JSONArray();
                    String qry = "select avg(manavg) from competencyAvg where appcycle.id=? and employee.userID=? ";
                    List list2 = HibernateUtil.executeQuery(session, qry, new Object[]{request.getParameter("appraisalcycleid"), u.getUserID()});
                    double avg = 0;
                    String globalAvg = "0.00";
                    if (!list2.isEmpty() && list2.get(0) != null) {
                        
                        avg = (Double) list2.get(0);
                        DecimalFormat df = new DecimalFormat("#.##");
                        globalAvg = df.format(avg);
                        if (!globalAvg.contains(".")) {
                            globalAvg += ".00";
                        }
                    }
                    for (int i = 0; i < lst.size(); i++) {
                        Appraisal apsl = (Appraisal) lst.get(i);
                        JSONArray comments = new JSONArray();
                        JSONObject appObj = new JSONObject();
                        if (apsl.getCompetency() != null) {
                            String empwght = "";
                            String manwght = "";
                            appObj.put("comptename", apsl.getCompetency().getMastercmpt().getCmptname());
                            boolean flag = true;
                            for(int n = 0; n < indexArray.length(); n++){
                                String jk = indexArray.getString(n);
                                if(jk.equals(apsl.getCompetency().getMastercmpt().getCmptname())){
                                   flag = false;
                                   break;
                                }
                            }
                            if(flag){
                                indexArray.put(apsl.getCompetency().getMastercmpt().getCmptname());
                                headerArray.put(apsl.getCompetency().getMastercmpt().getCmptname());
                                headerArray.put("avg");
                                headerArray.put("Comments");
                            }
                            
                            double avgRat = 0;
                            ArrayList initialcomments = new ArrayList();
                            ArrayList initialscores = new ArrayList();
                            JSONObject jempcomments = new JSONObject();
                            for (int j = 0; j < list.size(); j++) {
                                Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                                hql = "from competencyAvg where appcycle = ? and competency.mid = ? and employee = ?";
                                List alst = HibernateUtil.executeQuery(session, hql, new Object[]{apmt.getAppcycle(), apsl.getCompetency().getMid(), apmt.getEmployee()});
                                if (alst.size() > 0) {
                                    competencyAvg compavg = (competencyAvg) alst.get(0);
                                    avgRat = compavg.getManavg();
                                }
                                hql = "from Appraisal where appraisal.appraisalid = ? and competency.mid=?";
                                List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{apmt.getAppraisalid(), apsl.getCompetency().getMid()});
                                if (wlst.size() > 0) {
                                    Double wt1, wt2;
                                    Appraisal wapsl = (Appraisal) wlst.get(0);
                                    wt1 = wapsl.getCompmanrating();
                                    wt2 = wapsl.getCompemprating();
                                    empwght += wt2.intValue() + ", ";
                                    if(wt1.intValue()!=0){
                                        initialscores.add(wt1.intValue() + "");
                                    }
                                    if (!StringUtil.isNullOrEmpty(wapsl.getCompmancomment())) {
                                        initialcomments.add(wapsl.getCompmancomment());
                                    }
                                    if (i < 1) {
                                        empavgscore += apmt.getEmployeecompscore() + ", ";
                                    }
                                }
                                if (!StringUtil.isNullOrEmpty(apmt.getManagercomment()) && (i < 1)) {
                                    manComments += counter + ") " + apmt.getManagercomment();
                                    if (!StringUtil.isNullOrEmpty(request.getParameter("filetype")) || !StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                                        manComments += ",";
                                    } else {
                                        manComments += "";
                                    }
                                }
                                counter++;
                            }
                            if(empwght.length()>0){
                            empwght = empwght.substring(0, empwght.trim().length() - 1);
                            }
                            String scoreAvg = "0.00";
                            DecimalFormat decimalFormat = new DecimalFormat("#.##");
                            scoreAvg = decimalFormat.format(avgRat);
                            if (!scoreAvg.contains(".")) {
                                scoreAvg += ".00";
                            }
                            List<String> shufflist = initialcomments;
                            Collections.shuffle(shufflist);
                            int indx = 1;
                            for (String inicom : shufflist) {
                                jempcomments = new JSONObject();
                                jempcomments.put("managercomment", inicom);
                                comments.put(jempcomments);
                                indx++;
                            }
                            shufflist = initialscores;
                            Collections.shuffle(shufflist);
                            for (String inicom : shufflist) {
                                manwght += inicom + ", ";
                            }
                            if(manwght.length()>0){
                                manwght = manwght.substring(0, manwght.trim().length() - 1);
                            }
                            appObj.put("comments", comments);
                            appObj.put("compempwght", empwght);
                            appObj.put("compmanwght", manwght);
                            appObj.put("nominalRat", scoreAvg);
                        }
                        cmpObj.put(appObj);
                    }
                    
                    if(empavgscore.length()>0){
                        empavgscore = empavgscore.substring(0, empavgscore.trim().length() - 1);
                    }
                    tmpObj.put("competencies", cmpObj);
                    tmpObj.put("mancom", manComments);
                    tmpObj.put("compempavgwght", empavgscore);
                    tmpObj.put("manavgwght", globalAvg);
                    jarr.put(tmpObj);
                }
                
                JSONObject countObj = new JSONObject();
                countObj.put("data", jarr);
                jarr1.put(countObj);
                jobj.put("success", true);
                jobj.put("header", headerArray);
                jobj.put("indarr", indexArray);
                jobj.put("data", jarr1);
                mainArray.put(jobj);
            }
            ans.put("data1", mainArray);
            System.out.println("\n End of appraisal report");
        } catch (JSONException ex) {
            throw new JSONException("hrmsManager.getAppraisalReport");
        } finally {
        }
        return ans;
    }

  	private void writeDataToFile(String filename, ByteArrayOutputStream baos, HttpServletResponse response) throws  ServiceException
	{
            BufferedOutputStream bufferedOutput = null;
            ByteArrayInputStream byteArrayInputStream = null;
            ServletOutputStream out = null;

           try{
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                response.setContentType("application/octet-stream");
                response.setContentLength(baos.size());

                out = response.getOutputStream();
                byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
                bufferedOutput = new BufferedOutputStream(out);
                byte[] buff = new byte[(1024 * 1024) * 2];
                int bytesRead;

                while (-1 != (bytesRead = byteArrayInputStream.read(buff, 0, buff.length))) {
                        bufferedOutput.write(buff, 0, bytesRead);
                        bufferedOutput.flush();
                }
            bufferedOutput.close();
            out.flush();
            out.close();
          }catch (IOException e) {
                throw ServiceException.FAILURE("ExportCSVXLSReportServlet.writeDataToFile", e);
          }catch (Exception e) {
                throw ServiceException.FAILURE("ExportCSVXLSReportServlet.writeDataToFile", e);
            }finally {
             try {
            if (out != null)
                out.close();
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
            if (bufferedOutput != null) {
                bufferedOutput.close();
            }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            }
        }
%>
