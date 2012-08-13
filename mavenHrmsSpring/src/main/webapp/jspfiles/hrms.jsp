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
<%@ page contentType="text/html" pageEncoding="UTF-8"%>  
<%@ page import="com.krawler.esp.database.hrmsDbcon"%>
<%@ page import="com.krawler.utils.json.base.JSONException"%>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.utils.json.base.JSONArray"%>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="com.krawler.esp.handlers.FileUploadHandler"%>
<%@ page import="com.krawler.esp.handlers.AuthHandler"%>
<%@ page import="java.util.HashMap" %>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%

        String message = "";
        boolean isFormSubmit = false;
        JSONObject result = new JSONObject();
        JSONObject jobj = new JSONObject();
        if (sessionbean.validateSession(request, response)) {

            try {
                int flag;
                Integer start = 0;
                Integer limit = 0;
                HashMap hm = null;
                if (ServletFileUpload.isMultipartContent(request)) {
                    hm = new FileUploadHandler().getItems(request);
                    flag = Integer.parseInt(hm.get("flag").toString());
                } else {
                    flag = Integer.parseInt(request.getParameter("flag"));
                }

                switch (flag) {
                    case 1:
                        message = hrmsDbcon.getDemo(request);
                        break;
                    case 7:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getInternalJobs(request, start, limit);
                        break;
                    case 8:
                        message = hrmsDbcon.insertInternalJobs(request);

                        break;
                    case 9:
                        message = hrmsDbcon.deleteinternaljobs(request);
                        break;
                    case 10:
                        message = hrmsDbcon.applyforinernalJobs(request);
                        break;
                    case 16:
                        message = hrmsDbcon.getAllEmployeeGoals(request);
                        break;
                    case 18:
                        message = hrmsDbcon.addempappraisal(request);

                        break;
                    /*case 19:
                    message = hrmsDbcon.getAppraisalforemployee(request);
                    break;*/
                    case 20:
                        message = hrmsDbcon.getallappraisals(request);
                        break;
                    case 25:
                        message = hrmsDbcon.insertTimeSheet(request);
                        break;
                    case 26:
                        message = hrmsDbcon.getTimesheet(request);
                        break;
                    case 27:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getAllTimesheets(request, start, limit);
                        break;
                    case 28:
                        message = hrmsDbcon.ApproveTimesheets(request);
                        break;
                    case 29:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.finalemployeegoals(request, start, limit);
                        break;

                    case 36:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getsearchjobs(request, start, limit);
                        break;
                    case 37:
                        message = hrmsDbcon.applyforexternaljob(request);
                        break;
                    case 38:

                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getAppliedJobsExt(request, start, limit);
                        break;
                    case 39:
                        message = hrmsDbcon.scheduleinterview(request);
                        isFormSubmit = true;
                        break;
                    case 40:
                        message = hrmsDbcon.adminallappssave(request);
                        break;
                    case 42:
                        message = hrmsDbcon.deleteallappsadmin(request);
                        break;
                    case 44:
                        message = hrmsDbcon.allappsformsave(request);
                        isFormSubmit = true;
                        break;
                    case 45:
                        message = hrmsDbcon.deleteassignedgoals(request);
                        break;
                    case 46:
                        message = hrmsDbcon.extusersetpass(request);
                        break;
                    case 47:
                        message = hrmsDbcon.deletejobstimesheet(request);
                        break;
                    case 48:
                        message = hrmsDbcon.deletemasterdata(request);
                        break;
                    case 50:
                        isFormSubmit = true;
                        message = hrmsDbcon.saveempprofile(request);
                        break;
                    case 51:
                        message = hrmsDbcon.getempexperience(request);
                        break;
                    case 52:
                        message = hrmsDbcon.getEmpData(request);
                        break;
                    case 53:
                        message = hrmsDbcon.deleteEmpexperience(request);
                        break;
                    case 54:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getEmpDocuments(request, start, limit);
                        break;
                    case 49:
                        message = hrmsDbcon.GetApplicant(request);
                        break;
                    case 55:
                        message = hrmsDbcon.getGroupheader(request);
                        break;
                    case 56:
                        message = hrmsDbcon.deleteApplicants(request);
                        break;
                    case 57:
                        message = hrmsDbcon.terminateEmp(request);
                        isFormSubmit = true;
                        break;
                    case 58:
                        message = hrmsDbcon.rehireEmp(request);
                        isFormSubmit = true;
                        break;
                    case 59:
                        message = hrmsDbcon.Emppaysliphst(request);
                        break;
                    case 60:
                        message = hrmsDbcon.deleteDocuments(request);
                        break;
                    case 61:
                        message = hrmsDbcon.getAssignedManagers(request);
                        break;
                    case 62:
                        message = hrmsDbcon.sendReviewerEmail(request);
                        break;
                    case 101:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getCompetency(request, start, limit);
                        break;

                    case 102:
                        message = hrmsDbcon.addCompetency(request);
                        break;

                    case 103:
                        message = hrmsDbcon.getCompetency2(request);
                        break;

                    case 104:
                        message = hrmsDbcon.assignCompetency(request);
                        break;

                    case 105:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getCompAndDesig(request, start, limit);
                        break;

                    case 106:
                        message = hrmsDbcon.deleteComp(request);
                        break;

                    case 107:
                        message = hrmsDbcon.editComp(request);
                        break;

                    case 108:
                        message = hrmsDbcon.showComp(request);
                        break;

                    case 113:
                        message = hrmsDbcon.addCompensation(request);
                        break;

                    case 114:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getCompensation(request, start, limit);
                        break;

                    case 115:
                        message = hrmsDbcon.getCompetency3(request);
                        break;

                    case 116:
                        message = hrmsDbcon.showCompGrid(request);
                        break;

                    case 117:
                        message = hrmsDbcon.delDesigComp(request);
                        break;

                    case 120:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.showInternalApplicants(request, start, limit);
                        break;

                    case 121:
                        message = hrmsDbcon.addAgency(request);
                        break;

                    case 122:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.showAgency(request, start, limit);
                        break;

                    case 123:
                        message = hrmsDbcon.delAgency(request);
                        break;

                    case 125:
                        message = hrmsDbcon.addRecruiters(request);
                        break;

                    case 126:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.viewRecruiters(request, start, limit);
                        break;

                    case 128:
                        message = hrmsDbcon.applyAgency(request);
                        break;

                    case 136:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getfinalReport(request, start, limit);
                        break;

                    case 137:
                        message = hrmsDbcon.assignManager(request);
                        break;

                    case 138:
                        message = hrmsDbcon.updateinernalJobs(request);
                        break;

                    case 139:
                        message = hrmsDbcon.getassignManager(request);
                        break;

                    case 140:
                        message = hrmsDbcon.getAlerts(request);
                        break;

                    case 141:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getEmpForManager(request, start, limit);
                        break;

                    case 142:
                        message = hrmsDbcon.getappraisaltype(request);
                        break;

                    case 148:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.viewagencyJobs(request, start, limit);
                        break;

                    case 150:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.archivedgoals(request, start, limit);
                        break;

                    case 151:
                        message = hrmsDbcon.addComments(request);
                        break;

                    case 152:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getRecruiters(request, start, limit);
                        break;

                    case 153:
                        message = hrmsDbcon.createapplicant(request);
                        break;

                    case 154:
                        message = hrmsDbcon.getapplicantdata(request);
                        break;

                    case 155:
                        message = hrmsDbcon.getjobProfile(request);
                        break;

                    case 156:
                        message = hrmsDbcon.addjobProfile(request);
                        break;

                    case 157:
                        message = hrmsDbcon.viewjobProfile(request);
                        break;

                    case 158:
                        message = hrmsDbcon.getappraisalList(request);
                        break;

                    case 159:
                        message = hrmsDbcon.appraisal(request);
                        break;

                    case 160:
                        message = hrmsDbcon.getappraisalCompetency(request);
                        break;

                    case 161:
                        String crmURL = this.getServletContext().getInitParameter("crmURL");
                       // String crmURL = "http://192.168.0.102:8084/HQLCrm/";
                        message = hrmsDbcon.getappraisalGoals(crmURL,request);
                        break;

                    /*case 162:
                    if (request.getParameter("start") == null) {
                    start = 0;
                    limit = 15;
                    } else {
                    start = Integer.parseInt(request.getParameter("start"));
                    limit = Integer.parseInt(request.getParameter("limit"));
                    }
                    message = hrmsDbcon.getappraisalEmployee(request, start, limit);
                    break;*/

                    case 163:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getappraisalFunction(request, start, limit);
                        break;

                    case 165:
                        message = hrmsDbcon.reviewAppraisal(request);
                        break;

                    case 166:
                        message = hrmsDbcon.transferappData(request);
                        break;
                    case 167:
                        message = hrmsDbcon.setappraisalCycle(request);
                        break;

                    case 168:
                        message = hrmsDbcon.getappraisalCycle(request);
                        break;

                    case 169:
                        message = hrmsDbcon.getratingData(request);
                        break;

                    case 170:
                        message = hrmsDbcon.reviewanonymousAppraisal(request);
                        break;
                    case 171:
                        message = hrmsDbcon.reviewAppraisalReport(request);
                        break;
                    case 172:
                        message = hrmsDbcon.approveAppraisalCycle(request);
                        break;
                    case 173:
                        message = hrmsDbcon.sendappraisalEmail(request);
                        break;
                    case 174:
                        message = hrmsDbcon.sendappraisalreportEmail(request);
                        break;
                     case 175:
                        message = hrmsDbcon.deletejobprofileData(request);
                        break;

                     case 176:
                        String company = AuthHandler.getCompanyid(request);
                        String user = AuthHandler.getUserid(request);
                        jobj = hrmsDbcon.setInterviewerConfirmation(company, user, request);
                        //message = jobj.getString("msg");
                        message = jobj.toString();
                        break;

                     case 201:
                        message = hrmsDbcon.getMasterField(request);
                        break;
                    case 202:
                        message = hrmsDbcon.addMasterField(request);
                        isFormSubmit = true;
                        break;
                    case 203:
                        message = hrmsDbcon.getMasterDataField(request);
                        break;
                    case 204:
                        message = hrmsDbcon.addMasterDataField(request);
                        isFormSubmit = true;
                        break;
                    case 205:
                        message = hrmsDbcon.insertGoal(request);
                        break;
                    case 206:
                        message = hrmsDbcon.getManager(request);
                        break;
                    case 207:
                        message = hrmsDbcon.getSomeUserData(request);
                        break;
                    case 208:
                        message = hrmsDbcon.getEmpidFormat(request);
                        break;
                    case 209:
                        message = hrmsDbcon.getJobidFormat(request);
                        break;
                    case 210:
                        message = hrmsDbcon.getComments(request);
                        break;
                    case 211:
                        message = hrmsDbcon.getallEmployee(request);
                        break;
                    case 218:
                        message = hrmsDbcon.getConfigData(request);
                        break;                    
                    case 300:
                        message = hrmsDbcon.getEmpSchedule(request);
                        break;
                    case 404:
                        message = hrmsDbcon.update_profile_status(request);
                        break;
                    case 301:
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getAppraisalCycles(request, start, limit);
                        break;
                    case 405:
                        isFormSubmit = true;
                        if (request.getParameter("start") == null) {
                            start = 0;
                            limit = 15;
                        } else {
                            start = Integer.parseInt(request.getParameter("start"));
                            limit = Integer.parseInt(request.getParameter("limit"));
                        }
                        message = hrmsDbcon.getfinalReportWithColumns(request, start, limit);
                        message = "{\"valid\":true,\"data\":" + message + "}";
                        break;
                    case 406:
                        message = hrmsDbcon.getUserList(request);
                        break;
                    case 407:
                        message = hrmsDbcon.getAppraisalReport(request);
                        break;
                    case 408:
                        message = hrmsDbcon.getAppraisalReportforGrid(request);
                        break;
                    default:
                        break;
                }
                if (!isFormSubmit) {
                    result.put("valid", true);
                    result.put("data", message);
                    message = result.toString();

                }
            } catch (Exception e) {
                result.put("valid", true);
                result.put("data", e.getMessage());
                message = result.toString();
            } finally {
            }
        } else {
            sessionbean.destroyUserSession(request, response);
            jobj.put("valid", false);
            message = jobj.toString();
        }
        out.println(message);
%>



