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
package com.krawler.spring.hrms.appraisal;

import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.competency.hrmsCompetencyDAO;
import com.krawler.spring.hrms.goal.hrmsGoalDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

public class hrmsAppraisalController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private hrmsGoalDAO hrmsGoalDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private MessageSource messageSource;


    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public hrmsAppraisalDAO getHrmsAppraisalDAO() {
        return hrmsAppraisalDAOObj;
    }

    public void setHrmsAppraisalDAO(hrmsAppraisalDAO hrmsAppraisalDAOObj) {
        this.hrmsAppraisalDAOObj = hrmsAppraisalDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public hrmsAppraisalcycleDAO getHrmsAppraisalcycleDAOObj() {
        return hrmsAppraisalcycleDAOObj;
    }

    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }

    public hrmsCompetencyDAO getHrmsCompetencyDAOObj() {
        return hrmsCompetencyDAOObj;
    }

    public void setHrmsCompetencyDAO(hrmsCompetencyDAO hrmsCompetencyDAOObj) {
        this.hrmsCompetencyDAOObj = hrmsCompetencyDAOObj;
    }

    public hrmsGoalDAO getHrmsGoalDAOObj() {
        return hrmsGoalDAOObj;
    }

    public void setHrmsGoalDAO(hrmsGoalDAO hrmsGoalDAOObj) {
        this.hrmsGoalDAOObj = hrmsGoalDAOObj;
    }

    public ModelAndView getappraisalFunction(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        String start, limit;
        Iterator ite;
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            if (request.getParameter("start") == null) {
                start = "0";
                limit = "15";
            } else {
                start = request.getParameter("start");
                limit = request.getParameter("limit");
            }
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("paging", request.getParameter("paging"));
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("dept", request.getParameter("dept"));
            if (!(hrmsCommonDAOObj.isEmployee(requestParams).isSuccessFlag()) && !(hrmsCommonDAOObj.isManager(requestParams).isSuccessFlag())) {
                result = hrmsCommonDAOObj.getUserList(requestParams);
                count = result.getRecordTotalCount();
                ite = result.getEntityList().iterator();
                Empprofile emp;
                while (ite.hasNext()) {
//                    User log = (User) ite.next();
//                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getUserID());
                    Useraccount ua = (Useraccount) ite.next();
                    User log = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", ua.getUserID());//use ua.user

                    requestParams.clear();
                    requestParams.put("empid", log.getUserLogin().getUserID());
                    result = hrmsCommonDAOObj.getEmpDetailsbyEmpid(requestParams);
                    if (result.getEntityList().iterator().hasNext()) {
                        emp = (Empprofile) result.getEntityList().iterator().next();
                    } else {
                        emp = null;
                    }

                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getUserID());
                    if (emp != null) {
                    	if(emp.getJoindate()!=null){
                    		tmpObj.put("joindate", sdf.format(emp.getJoindate()));
                    	}else{
                    		tmpObj.put("joindate", "");
                    	}
                    } else {
                        tmpObj.put("joindate", "");
                    }
                    requestParams.clear();
                    requestParams.put("empid", ua.getEmployeeid());
                    requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    if(ua.getEmployeeIdFormat()==null){
                    	tmpObj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                    }else{
                    	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                    	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    	tmpObj.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                    }
                    if (ua.getDepartment() != null) {
                        tmpObj.put("department", ua.getDepartment().getValue());
                    } else {
                        tmpObj.put("department", "");
                    }
                    if (ua.getDesignationid() != null) {
                        tmpObj.put("designation", ua.getDesignationid().getValue());
                    } else {
                        tmpObj.put("designation", "");
                    }
                    tmpObj.put("email", log.getEmailID());
                    tmpObj.put("fullname", log.getFirstName() + " " + (log.getLastName() == null ? "" : log.getLastName()));

                    tmpObj = getEmployeeAppraisals(request, log.getUserID(), tmpObj);
                    jArr.put(tmpObj);
                }
            } else {
                result = hrmsCommonDAOObj.getEmpForManagerFunction(requestParams);
                count = result.getRecordTotalCount();
                ite = result.getEntityList().iterator();
//                Empprofile ep;
                while (ite.hasNext()) {
                    Empprofile log = (Empprofile) ite.next();
                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getUserID());
                    requestParams.clear();
//                    requestParams.put("empid", log.getUserLogin().getUserID());
//                    result = hrmsCommonDAOObj.getEmpDetailsbyEmpid(requestParams);
//                    ep = (Empprofile) result.getEntityList().iterator().next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getUserID());
//                    if (ep != null) {
                    if(log.getJoindate()!=null){
                    	tmpObj.put("joindate", sdf.format(log.getJoindate()));
                    }else{
                    	tmpObj.put("joindate", "");
                    }
//                    }
                    requestParams.clear();
                    requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                    requestParams.put("empid", ua.getEmployeeid());
                    if(ua.getEmployeeIdFormat()==null){
                    	tmpObj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                    }else{
                    	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                    	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    	tmpObj.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                    }
                    if (ua.getDepartment() != null) {
                        tmpObj.put("department", ua.getDepartment().getValue());
                    } else {
                        tmpObj.put("department", "");
                    }
                    if (ua.getDesignationid() != null) {
                        tmpObj.put("designation", ua.getDesignationid().getValue());
                    } else {
                        tmpObj.put("designation", "");
                    }
                    tmpObj.put("email", log.getUserLogin().getUser().getEmailID());
                    tmpObj.put("fullname", log.getUserLogin().getUser().getFirstName() + " " + (log.getUserLogin().getUser().getLastName() == null ? "" : log.getUserLogin().getUser().getLastName()));

                    tmpObj = getEmployeeAppraisals(request, log.getUserID(), tmpObj);
                    jArr.put(tmpObj);
                }
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public JSONObject getEmployeeAppraisals(HttpServletRequest request, String employeeid, JSONObject tmpObj) {
        KwlReturnObject result = null;
        SimpleDateFormat fmt1 = new SimpleDateFormat("MMMM d, yyyy");
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("employeeid", employeeid);
            result = hrmsAppraisalDAOObj.getEmployeeAppraisals(requestParams);
            Iterator ite = result.getEntityList().iterator();
            while (ite.hasNext()) {
                Appraisalmanagement app = (Appraisalmanagement) ite.next();
                String cname = "";
                String sdate = "";
                String edate = "";
                String mname = "";
                if (app.getAppcycle() != null) {
                    sdate = fmt1.format(app.getAppcycle().getStartdate());
                    edate = fmt1.format(app.getAppcycle().getEnddate());
                    cname = app.getAppcycle().getCyclename();
                    mname = app.getManager().getFirstName() + " " + app.getManager().getLastName();

                    tmpObj.append("cyclename", cname);
                    tmpObj.append("startdate", sdate);
                    tmpObj.append("enddate", edate);
                    tmpObj.append("manager", mname);
                    if (app.getAppraisalstatus().equals("submitted")) {
                        tmpObj.append("status", "Completed");
                    } else {
                        tmpObj.append("status", app.getAppraisalstatus());
                    }
                    tmpObj.append("edate", fmt1.format(app.getAppcycle().getSubmitenddate()));
                    tmpObj.append("sdate", fmt1.format(app.getAppcycle().getSubmitstartdate()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpObj;
    }

    public ModelAndView AppraisalAssign(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            Appraisalmanagement appm = null;
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            boolean flag = true;
            String query = "";
            String manager = "";
            String hql = "";
            String auditappname="";
            String auditempid="";
            Iterator ite = null;
            List tabledata = null;
            List list = null;
            String ids[] = request.getParameterValues("empids");
            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, dateFmt.format(new Date()), sessionHandlerImplObj.getUserid(request));

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("id");
            filter_values.add(request.getParameter("apptype"));
            filter_names.add(">=submitenddate");
            filter_values.add(userdate);
            requestParams.clear();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
            tabledata = result.getEntityList();
            if (tabledata.isEmpty()) {
                jobj.put("message", messageSource.getMessage("hrms.performance.Appraisalcyclesubmissionhasalreadyended",null,"Appraisal cycle submission has already ended.", RequestContextUtils.getLocale(request)));
            } else {
                for (int i = 0; i < ids.length; i++) {
                    filter_names.clear();
                    filter_values.clear();
                    filter_names.add("employee.userID");
                    filter_values.add(ids[i]);
                    filter_names.add("appcycle.id");
                    filter_values.add(request.getParameter("apptype"));
                    requestParams.clear();
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    tabledata = result.getEntityList();
                    if (tabledata.isEmpty()) {//check for appraisal cycle already initiated
                        requestParams.clear();
                        requestParams.put("userid", ids[i]);
                        result = hrmsCommonDAOObj.getManagerCountforUserid(requestParams);
                        list = result.getEntityList();
                        long man = (Long) list.get(0);
                        result = hrmsCommonDAOObj.getReviewerCountforUserid(requestParams);
                        list = result.getEntityList();
                        long rv = (Long) list.get(0);
                        if (man > 0 && rv > 0) { //at least 1 manager and reviewer are assigned
                            filter_names.clear();
                            filter_values.clear();
                            filter_names.add("assignemp.userID");
                            filter_values.add(ids[i]);
                            filter_names.add("managerstatus");
                            filter_values.add(1);
                            requestParams.clear();
                            requestParams.put("filter_names", filter_names);
                            requestParams.put("filter_values", filter_values);
                            result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                            list = result.getEntityList();
                            ite = list.iterator();
                            requestParams.clear();
                            requestParams.put("empid", ids[i]);
                            result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                            User usr = null;
                            if (result.getEntityList() != null && result.getEntityList().size() > 0) {
                                usr = (User) result.getEntityList().get(0);
                            }
                            Useraccount userUseraccount = hrmsCommonDAOObj.getUseraccountByUserId(ids[i]);
                            while (ite.hasNext()) {
                                Assignmanager assignman = (Assignmanager) ite.next();
                                if (assignman.getAssignman() != null) {
                                    manager = assignman.getAssignman().getUserID();
                                    appm = new Appraisalmanagement();
                                    requestParams.clear();
                                    requestParams.put("primary", true);
                                    requestParams.put("id", request.getParameter("apptype"));
                                    result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
                                    Appraisalcycle appcyc = null;
                                    if (result.getEntityList() != null && result.getEntityList().size() > 0) {
                                        appcyc = (Appraisalcycle) result.getEntityList().get(0);
                                    }
                                    requestParams.clear();
                                    requestParams.put("empid", manager);
                                    result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                                    User mang = null;
                                    if (result.getEntityList() != null && result.getEntityList().size() > 0) {
                                        mang = (User) result.getEntityList().get(0);
                                    }
                                    Useraccount managerUseraccount = hrmsCommonDAOObj.getUseraccountByUserId(mang.getUserID());
                                    requestParams.clear();
                                    requestParams.put("appcycle", appcyc);
                                    requestParams.put("user", usr);
                                    requestParams.put("manager", mang);
                                    requestParams.put("empstatus", 0);
                                    requestParams.put("mgrstatus", 0);
                                    requestParams.put("empdraft", 0);
                                    requestParams.put("mgrdraft", 0);
                                    requestParams.put("apprstat", request.getParameter("status"));
                                    requestParams.put("revstat", 0);
                                    requestParams.put("empdesid", userUseraccount.getDesignationid());
                                    requestParams.put("managerdesid", managerUseraccount.getDesignationid());
                                    requestParams.put("competencyQuestions", (List<CompetencyQuestion>) hrmsAppraisalDAOObj.getCompetencyQuestions(requestParams));
                                    Appraisalmanagement appraisalmanagement = hrmsAppraisalDAOObj.AppraisalAssign(requestParams);
                                    auditappname=appcyc.getCyclename();
                                    auditempid=usr.getUserID();

                                }
                            }
                            auditTrailDAOObj.insertAuditLog(AuditAction.INITIATE_APPRAISAL, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has initiated appraisal of  " + profileHandlerDAOObj.getUserFullName(auditempid) + " for the appraisal cycle " + auditappname, request, "0");
                        } else {
                            if (rv == 0 && man == 0) {
                                jobj.put("message", messageSource.getMessage("hrms.performance.Noappraiserandreviewerassignedforselectedappraisalcycle",null,"No appraiser and reviewer assigned for selected appraisal cycle.", RequestContextUtils.getLocale(request)));
                            } else if (rv == 0) {
                                jobj.put("message", messageSource.getMessage("hrms.common.Noreviewerassignedforselectedappraisalcycle", null,"No reviewer assigned for selected appraisal cycle.", RequestContextUtils.getLocale(request)));
                            } else {
                                jobj.put("message", messageSource.getMessage("hrms.performance.Noappraiserassignedforselectedappraisalcycle",null,"No appraiser assigned for selected appraisal cycle.", RequestContextUtils.getLocale(request)));
                            }
                        }
                    } else {
                        jobj.put("message", messageSource.getMessage("hrms.performance.Appraisalalreadyinitiatedtheselectedemployeeforthechosenappraisalcycle",null,"Appraisal has been already initiated for the selected employee for the chosen appraisal cycle.", RequestContextUtils.getLocale(request)));
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.INITIATE_APPRAISAL, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has initiated appraisal of  " + AuthHandler.getFullName(appm.getEmployee()) + " for the appraisal cycle " + appm.getAppcycle().getCyclename(), request);
                    if(!jobj.has("message")) {
                        jobj.put("message", messageSource.getMessage("hrms.performance.Appraisalsuccessfullyinitiatedforselectedemployees",null,"Appraisal successfully initiated for selected employee(s).", RequestContextUtils.getLocale(request)));
                    }
                }
            }
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        } finally {
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

//    public ModelAndView getUserForReviewerperAppCyc(HttpServletRequest request, HttpServletResponse response) {
//        KwlReturnObject result = null;
//        JSONObject jobj = new JSONObject();
//        JSONObject jobj1 = new JSONObject();
//        JSONArray jarr = new JSONArray();
//        List lst = null;
//        String hql;
//        try {
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
//
//            String appcylid = request.getParameter("appcylid");
//            String users = "";
//            requestParams.put("id", appcylid);
//            requestParams.put("primary", true);
//            result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
//            Appraisalcycle cycleObj = null;
//            if (result.getEntityList() != null && result.getEntityList().size() > 0) {
//                cycleObj = (Appraisalcycle) result.getEntityList().get(0);
//            }
//
//            filter_names.clear();
//            filter_values.clear();
//            filter_names.add("reviewer.userID");
//            filter_values.add(sessionHandlerImplObj.getUserid(request));
//            filter_names.add("reviewerstatus");
//            filter_values.add(1);
//            requestParams.clear();
//            requestParams.put("filter_names", filter_names);
//            requestParams.put("filter_values", filter_values);
//
//            result = hrmsCommonDAOObj.getAssignreviewer(requestParams);
//            lst = result.getEntityList();
//            Iterator ite = lst.iterator();
//            while (ite.hasNext()) {
//                Assignreviewer log = (Assignreviewer) ite.next();
//                users += "'" + log.getEmployee().getUserID() + "',";
//            }
//
//            if (users.length() > 0) {
//                users = users.substring(0, users.length() - 1);
//            }
//            requestParams.clear();
//            requestParams.put("users", users);
//            requestParams.put("appcycle", cycleObj);
//            result = hrmsAppraisalcycleDAOObj.getUsersforAppCyc(requestParams);
//            lst = result.getEntityList();
//            ite = lst.iterator();
//            while (ite.hasNext()) {
//                User user = (User) ite.next();
//                JSONObject tmpObj = new JSONObject();
//                tmpObj.put("name", user.getFirstName() + " " + user.getLastName());
//                tmpObj.put("id", user.getUserID());
//                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
//                if(ua.getDesignationid()!=null){
//                    requestParams.clear();
//                    requestParams.put("desigid", ua.getDesignationid().getId());
//                    result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
//                    if(result.getEntityList().size() > 0){
//                        tmpObj.put("isquestionemp", "true");
//                    } else {
//                        tmpObj.put("isquestionemp", "false");
//                    }
//                } else {
//                    tmpObj.put("isquestionemp", "false");
//                }
//                jarr.put(tmpObj);
//            }
//            jobj.put("data", jarr);
//            jobj.put("count", lst.size());
//            jobj1.put("valid", true);
//            jobj1.put("data", jobj.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return new ModelAndView("jsonView", "model", jobj1.toString());
//    }

    public ModelAndView getUserForReviewerperAppCyc(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        List lst = null;
        int count = 0;
        String hql;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String appcylid = request.getParameter("appcylid");
            requestParams.put("reviewerId", sessionHandlerImplObj.getUserid(request));
            requestParams.put("appCycleId", appcylid);
            result = hrmsAppraisalcycleDAOObj.getUsersforAppCycleMYSQL(requestParams);
            lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Object[] userList = (Object[]) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("name",(String) userList[3] + " " + (String) userList[4]);
                tmpObj.put("id",(String) userList[0]);
                String designation = (String) userList[5];
                if(designation!=null){
                    requestParams.clear();
                    requestParams.put("desigid", designation);
                    count = hrmsCompetencyDAOObj.getCompetencyDesMapCount(requestParams);
                    if(count > 0){
                        tmpObj.put("isquestionemp", "true");
                    } else {
                        tmpObj.put("isquestionemp", "false");
                    }
                } else {
                    tmpObj.put("isquestionemp", "false");
                }
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", lst.size());
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getappraisalCompetencyFunction(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject tmpObj = new JSONObject();
        JSONArray jarr = new JSONArray();
        Iterator ite;
        double ans = 0;
        int appcnt = 0, cnt = 1;
        try {

            String appraisal = request.getParameter("appraisal");
            String companyid = sessionHandlerImplObj.getCompanyid(request);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            filter_names.add("appraisal.appraisalid");
            filter_values.add(appraisal);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            result = hrmsAppraisalDAOObj.getAppraisal(requestParams);
            tabledata = result.getEntityList();

            if (!tabledata.isEmpty()) {

                requestParams.clear();
                requestParams.put("appid", appraisal);

                result = hrmsAppraisalDAOObj.getAppraisalforAppCyc(requestParams);
                appcnt = result.getRecordTotalCount();

                requestParams.clear();

                requestParams.put("primary", true);
                requestParams.put("id", appraisal);

                result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                Appraisalmanagement appmanage = (Appraisalmanagement) result.getEntityList().iterator().next();

                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Appraisal app = (Appraisal) ite.next();
                    if (app.getCompetency() != null) {
                        tmpObj = new JSONObject();
                        tmpObj.put("cmptid", app.getCompetency().getMid());
                        tmpObj.put("mid", app.getCompetency().getMid());
                        tmpObj.put("compid", app.getAppid());
                        tmpObj.put("cmptname", app.getCompetency().getMastercmpt().getCmptname());
//                        tmpObj.put("cmptnametemp", "<span style='margin-left:30px;'>" + cnt + ". " + app.getCompetency().getMastercmpt().getCmptname() +"</span><br/>");
                        tmpObj.put("cmptnametemp", "<span style='margin-left:30px;font-size:1.1em;'>" + cnt + ". " + app.getCompetency().getMastercmpt().getCmptname() +"</span><br/>");
                        requestParams.clear();
                        requestParams.put("checklink", "weightage");
                        requestParams.put("companyid", companyid);
                        if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                            tmpObj.put("cmptwt", app.getCompetency().getWeightage());
                        } else {
                            ans = 100 / Double.parseDouble("" + appcnt);
                            tmpObj.put("cmptwt", ans);
                        }
                        tmpObj.put("cmptdesc", app.getCompetency().getMastercmpt().getCmptdesc());
                        if (appmanage.getManagerdraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("manrat", "");
                            tmpObj.put("mangap", "");
                            tmpObj.put("mancompcomment", "");
                        } else if (appmanage.getManagerdraft() == 0 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("manrat", app.getCompmanrating());
                            tmpObj.put("mangap", app.getCompmangap());
                            tmpObj.put("mancompcomment", app.getCompmancomment());
                        } else {
                            tmpObj.put("manrat", app.getCompmanrating());
                            tmpObj.put("mangap", app.getCompmangap());
                            tmpObj.put("mancompcomment", app.getCompmancomment());
                        }
                        if (appmanage.getEmployeedraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("emprat", app.getCompemprating());
                            tmpObj.put("empgap", app.getCompempgap());
                            tmpObj.put("empcompcomment", app.getCompempcomment());
                        } else if (appmanage.getEmployeedraft() == 0 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("emprat", app.getCompemprating());
                            tmpObj.put("empgap", app.getCompempgap());
                            tmpObj.put("empcompcomment", app.getCompempcomment());
                        } else {
                            tmpObj.put("emprat", "");
                            tmpObj.put("empgap", "");
                            tmpObj.put("empcompcomment", "");
                        }
                        jarr.put(tmpObj);
                    }
                    cnt++;
                }
            } else {
                requestParams.clear();
                filter_names.clear();
                filter_values.clear();

                filter_names.add("desig.id");
                filter_values.add(request.getParameter("desig"));

                filter_names.add("delflag");
                filter_values.add(0);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);

                result = hrmsCompetencyDAOObj.getManagecmpt(requestParams);
                appcnt = result.getRecordTotalCount();
                tabledata = result.getEntityList();


                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Managecmpt log = (Managecmpt) ite.next();
                    tmpObj = new JSONObject();
                    tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                    tmpObj.put("mid", log.getMid());
                    tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
//                    tmpObj.put("cmptnametemp", "<b><span style='margin-left:30px;font-family:tahoma,arial,helvetica,sans-serif;font-size:10px;'>" + cnt + ". " + log.getMastercmpt().getCmptname() +"</span></b><br/>");
                    tmpObj.put("cmptnametemp", "<b><span style='margin-left:30px;font-size:1.1em;'>" + cnt + ". " + log.getMastercmpt().getCmptname() +"</span></b><br/>");

                    requestParams.clear();
                    requestParams.put("checklink", "weightage");
                    requestParams.put("companyid", companyid);
                    if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                        tmpObj.put("cmptwt", log.getWeightage());
                    } else {
                        ans = 100 / Double.parseDouble("" + appcnt);
                        tmpObj.put("cmptwt", ans);
                    }
                    tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                    jarr.put(tmpObj);
                    cnt++;
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }


//    public HashMap updateappraisalGoalsFromCrm(HttpServletRequest request) {
//        JSONObject jobj = new JSONObject();
//        HashMap GoalRate =null;
//        String msg = "";
//        try {
//            String uid = null;
//            JSONArray data = null;
//
//            String crmURL = this.getServletContext().getInitParameter("crmURL");
//
//            String companyid = sessionHandlerImplObj.getCompanyid(request);
//            if (StringUtil.isNullOrEmpty(request.getParameter("isemployee"))) {
//                uid = request.getParameter("empid");
//            } else {
//                uid = sessionHandlerImplObj.getUserid(request);
//            }
//
//            if (!StringUtil.isNullOrEmpty(crmURL)) {
//                JSONObject userData = new JSONObject();
//                userData.put("companyid", companyid);
//                userData.put("userid", uid);
////                userData.put("remoteapikey", StorageHandler.GetRemoteAPIKey());
//                String action = "12";
//                JSONObject resObj = APICallHandler.callApp(crmURL, userData, companyid, action);
//                if (!resObj.isNull("success") && resObj.getBoolean("success")) {
//                    data = new JSONArray(resObj.getString("data"));
//                    GoalRate =new HashMap();
//                    ExtractGoalInfo(data,GoalRate);
//                } else {
//                }
//            }
//            jobj.put("msg", msg);
//            jobj.put("data", data);
//        } catch (Exception e) {
//            throw ServiceException.FAILURE("ProfileHandler.setPassword", e);
//        }
//        return GoalRate;
//    }


//    public static void ExtractGoalInfo(JSONArray jarr,HashMap GoalRate) throws ServiceException {
//        try {
//                for (int i = 0; i < jarr.length(); i++) {
//                    JSONObject jobj = jarr.getJSONObject(i);
//                    GoalRate.put(jobj.getString("gid")+"gname", jobj.getString("gname"));
//                    GoalRate.put(jobj.getString("gid"), jobj.getString("percentgoal"));
//                }
//        } catch (Exception ex) {
//            throw ServiceException.FAILURE("hrmsHandler.updateGoal", ex);
//        }
//    }


    public ModelAndView getappraisalGoalsFunction(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject tmpObj = new JSONObject();
        HashMap GoalRate;
        List tabledata = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
//
//            String crmURL = this.getServletContext().getInitParameter("crmURL");
            String userid = sessionHandlerImplObj.getUserid(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
//            String uid;
//
//            if (StringUtil.isNullOrEmpty(request.getParameter("isemployee"))) {
//                uid = request.getParameter("empid");
//            } else {
//                uid = userid;
//            }
//
//            requestParams.put("companyid",companyid);
//            requestParams.put("userid",uid);
//            requestParams.put("crmURL",crmURL);
//
//            result=hrmsGoalDAOObj.updateappraisalGoalsFromCrm(requestParams);
//
//            GoalRate = (HashMap) result.getEntityList().iterator().next();
//
            String appraisal = request.getParameter("appraisal");



            if (StringUtil.isNullOrEmpty(request.getParameter("isemployee"))) {


                filter_names.add("appraisal.appraisalid");
                filter_values.add(appraisal);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);

                result = hrmsAppraisalDAOObj.getAppraisal(requestParams);
                tabledata = result.getEntityList();

                if (!tabledata.isEmpty()) {

                    requestParams.clear();

                    requestParams.put("primary", true);
                    requestParams.put("id", appraisal);

                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                    Appraisalmanagement appmanage = (Appraisalmanagement) result.getEntityList().iterator().next();

                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Appraisal app = (Appraisal) ite.next();
                        if (app.getGoal() != null) {
                            tmpObj = new JSONObject();
                            tmpObj.put("gid", app.getGoal().getId());
                            tmpObj.put("goalid", app.getAppid());
                            tmpObj.put("gname", app.getGoal().getGoalname());
                            tmpObj.put("gwth", app.getGoal().getGoalwth());
                            tmpObj.put("assignedby", StringUtil.getFullName(app.getGoal().getManager()));
                            if (appmanage.getManagerdraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                tmpObj.put("gmanrat", "");
                                tmpObj.put("mangoalcomment", "");
                            } else if (appmanage.getManagerdraft() == 0 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                tmpObj.put("gmanrat", app.getGoalmanrating());
                                tmpObj.put("mangoalcomment", app.getGoalmancomment());
                            } else {
                                tmpObj.put("gmanrat", app.getGoalmanrating());
                                tmpObj.put("mangoalcomment", app.getGoalmancomment());
                            }
                            if (appmanage.getEmployeedraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                tmpObj.put("gemprat", app.getGoalemprating());
                                tmpObj.put("empgoalcomment", app.getGoalempcomment());
                            } else if (appmanage.getEmployeedraft() == 0 && !Boolean.parseBoolean(request.getParameter("employee"))) {
                                tmpObj.put("gemprat", app.getGoalemprating());
                                tmpObj.put("empgoalcomment", app.getGoalempcomment());
                            } else {
                                tmpObj.put("gemprat", "");
                                tmpObj.put("empgoalcomment", "");
                            }
                            jarr.put(tmpObj);
                        }
                    }
                } else {
                    String managerid;
                    Date sdate = null;
                    Date edate = null;
                    if (StringUtil.isNullOrEmpty("managerid")) {
                        managerid = userid;
                    } else {
                        managerid = request.getParameter("managerid");
                    }
                    if (!StringUtil.isNullOrEmpty("appraisal")) {
                        requestParams.clear();

                        requestParams.put("primary", true);
                        requestParams.put("id", appraisal);

                        result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                        Appraisalmanagement appr = (Appraisalmanagement) result.getEntityList().iterator().next();
                        if (appr.getAppcycle() != null) {
                            sdate = appr.getAppcycle().getStartdate();
                            edate = appr.getAppcycle().getEnddate();
                        }
                    }

                    requestParams.clear();

                    requestParams.put("empid", request.getParameter("empid"));
                    requestParams.put("companyid", companyid);
                    requestParams.put("managerid", managerid);
                    requestParams.put("sdate", sdate);
                    requestParams.put("edate", edate);

                    result = hrmsGoalDAOObj.getGoalsforAppraisal(requestParams);
                    tabledata = result.getEntityList();


                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                        tmpObj = new JSONObject();
                        tmpObj.put("gid", fgmt.getId());
                        tmpObj.put("gname", fgmt.getGoalname());
                        tmpObj.put("gwth", fgmt.getGoalwth());
                        jarr.put(tmpObj);
                    }
                }
            } else {
                String[] appids = request.getParameter("appraisal").split(",");
                for (int i = 0; i < appids.length; i++) {

                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("appraisal.appraisalid");
                    filter_values.add(appids[i]);

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);

                    result = hrmsAppraisalDAOObj.getAppraisal(requestParams);
                    tabledata = result.getEntityList();

                    Iterator ite = tabledata.iterator();
                    if (!tabledata.isEmpty()) {
                        while (ite.hasNext()) {
                            Appraisal app = (Appraisal) ite.next();
                            if (app.getGoal() != null) {
                                tmpObj = new JSONObject();
                                tmpObj.put("gid", app.getGoal().getId());
                                tmpObj.put("goalid", app.getAppid());
                                tmpObj.put("gname", app.getGoal().getGoalname());
                                tmpObj.put("gwth", app.getGoal().getGoalwth());
                                tmpObj.put("gemprat", app.getGoalemprating());
                                tmpObj.put("gmanrat", app.getGoalmanrating());
                                tmpObj.put("assignedby", StringUtil.getFullName(app.getGoal().getManager()));
                                tmpObj.put("mangoalcomment", app.getGoalmancomment());
                                tmpObj.put("empgoalcomment", app.getGoalempcomment());
                                tmpObj.put("goalapprid", appids[i]);
                                jarr.put(tmpObj);
                            }
                        }
                    } else {
                        String managerid;
                        Date sdate = null;
                        Date edate = null;
                        String empid = userid;
                        requestParams.clear();

                        requestParams.put("primary", true);
                        requestParams.put("id", appids[i]);

                        result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                        Appraisalmanagement appr = (Appraisalmanagement) result.getEntityList().iterator().next();
                        managerid = appr.getManager().getUserID();
                        if (appr.getAppcycle() != null) {
                            sdate = appr.getAppcycle().getStartdate();
                            edate = appr.getAppcycle().getEnddate();
                        }

                        requestParams.clear();

                        requestParams.put("empid", empid);
                        requestParams.put("companyid", companyid);
                        requestParams.put("managerid", managerid);
                        requestParams.put("sdate", sdate);
                        requestParams.put("edate", edate);

                        result = hrmsGoalDAOObj.getGoalsforAppraisal(requestParams);
                        tabledata = result.getEntityList();

                        ite = tabledata.iterator();
                        while (ite.hasNext()) {
                            Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                            tmpObj = new JSONObject();
                            tmpObj.put("gid", fgmt.getId());
                            tmpObj.put("gname", fgmt.getGoalname());
                            tmpObj.put("gwth", fgmt.getGoalwth());
                            tmpObj.put("assignedby", StringUtil.getFullName(fgmt.getManager()));
                            tmpObj.put("goalapprid", appids[i]);
                            jarr.put(tmpObj);
                        }

                    }
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView appraisalFunction(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List tabledata = null;
        JSONObject msgjobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        Iterator ite = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);


        boolean flag = true;

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();

            Appraisalmanagement appmanage = null;
            requestParams1.put("rateperformance", request.getParameter("rateperformance"));
            if (StringUtil.isNullOrEmpty(request.getParameter("rateperformance"))) {
                requestParams1.put("jsoncompetency", request.getParameter("jsoncompetency"));
                requestParams1.put("jsongoal", request.getParameter("jsongoal"));
                String[] appids = request.getParameter("appraisalid").split(",");

                for (int k = 0; k < appids.length; k++) {

                    requestParams.put("primary", true);
                    requestParams.put("id", appids[k]);

                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    tabledata = result.getEntityList();
                    ite = tabledata.iterator();

                    if (tabledata.isEmpty()) {
                        appmanage = new Appraisalmanagement();
                        flag = true;
                    } else {
                        appmanage = (Appraisalmanagement) tabledata.iterator().next();

                        requestParams.clear();
                        requestParams.put("appcycleid", appmanage.getAppcycle().getId());
                        requestParams.put("employeeid", appmanage.getEmployee().getUserID());

                        result = hrmsAppraisalcycleDAOObj.submitAppraisalCheck(requestParams);
                        List list = result.getEntityList();
                        if (!list.isEmpty()) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        requestParams1.put("appmanage", appmanage);
                        requestParams1.put("appid", appids[k]);
                        if (Boolean.parseBoolean(request.getParameter("employee"))) {

                            requestParams1.put("employee", request.getParameter("employee"));
                            requestParams1.put("empcomment", request.getParameter("empcomment"));
                            requestParams1.put("saveasDraft", request.getParameter("saveasDraft"));
                            requestParams1.put("competencyscore", request.getParameter("competencyscore"));
                            requestParams1.put("goalscore", request.getParameter("goalscore"));
                            requestParams1.put("compgapscore", request.getParameter("compgapscore"));
                            requestParams1.put("submitdate", request.getParameter("submitdate"));
                        } else {

                            requestParams1.put("employee", request.getParameter("employee"));

                            requestParams1.put("mancomment", request.getParameter("mancomment"));
                            requestParams1.put("saveasDraft", request.getParameter("saveasDraft"));
                            requestParams1.put("performance", request.getParameter("performance"));

                            requestParams1.put("competencyscore", request.getParameter("competencyscore"));
                            requestParams1.put("goalscore", request.getParameter("goalscore"));
                            requestParams1.put("compgapscore", request.getParameter("compgapscore"));
                            requestParams1.put("submitdate", request.getParameter("submitdate"));
                            requestParams1.put("salarychange", request.getParameter("salarychange"));
                            requestParams1.put("newdesignation", request.getParameter("newdesignation"));
                            requestParams1.put("newdepartment", request.getParameter("newdepartment"));
                            requestParams1.put("salaryincrement", request.getParameter("salaryincrement"));
                        }
                        requestParams.clear();
                        requestParams.put("appid", appids[k]);
                        result = hrmsAppraisalDAOObj.getAppraisalforAppCyc(requestParams);
                        List cmplst = result.getEntityList();

                        requestParams1.put("complist", cmplst);


                        hrmsAppraisalDAOObj.appraisalFunction(requestParams1);
                        if(request.getParameter("isquestion").equals("true"))
                        {
                        	ArrayList commentArray = null;	
                        	if (Boolean.parseBoolean(request.getParameter("employee"))){
                        		commentArray = new ArrayList();
                            	SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                            	while (ite.hasNext()) {
                                    Appraisalmanagement app = (Appraisalmanagement) ite.next();
                                    app.setEmployeesubmitdate((Date) fmt.parse(request.getParameter("submitdate")));
                                    commentArray.add(app.getAppraisalid());
//                                    app.setEmployeecomment(request.getParameter("ecomments"));
//                                    if (!Boolean.parseBoolean(request.getParameter("saveasDraft")))
//                                        app.setEmployeestatus(1);
                                }	
                        	}
                        	requestParams1.put("ite", ite);
                        	requestParams1.put("commentArray", commentArray);
                        	Appraisalmanagement log = (Appraisalmanagement) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalmanagement", request.getParameter("appraisalid"));
                        	if (Boolean.parseBoolean(request.getParameter("employee"))) {
                                requestParams1.put("manid", sessionHandlerImplObj.getUserid(request));
                                requestParams1.put("empid", sessionHandlerImplObj.getUserid(request));
                                requestParams1.put("isemployee", true);
                            } else {
                            	
                                requestParams1.put("manid", log.getManager().getUserID());
                                requestParams1.put("empid", log.getEmployee().getUserID());
                                requestParams1.put("isemployee", false);
                            }
                        	requestParams1.put("appcycle", request.getParameter("appcycle"));
                            if(!StringUtil.isNullOrEmpty(request.getParameter("appraisalid"))){
                                requestParams1.put("appraisalid", request.getParameter("appraisalid"));
                            }
                            requestParams1.put("jsondata", request.getParameter("jsonqustion"));
                            result=hrmsAppraisalDAOObj.saveAnswers(requestParams1);
                        }
                    } else {
                        break;
                    }
                }
                if (Boolean.parseBoolean(request.getParameter("employee")) && !Boolean.parseBoolean(request.getParameter("saveasDraft"))) {
                    //@@ProfileHandler.insertAuditLog(session,AuditAction.APPRAISAL_SUBMITTED,"Employee " + AuthHandler.getFullName(appmanage.getEmployee())+" has submitted appraisal for the appraisal cycle " + appmanage.getAppcycle().getCyclename(), request);
                    auditTrailDAOObj.insertAuditLog(AuditAction.APPRAISAL_SUBMITTED, "Employee " + StringUtil.getFullName(appmanage.getEmployee())+" has submitted appraisal for the appraisal cycle " + appmanage.getAppcycle().getCyclename(), request, "0");
                }
            } else {
                String performance[] = request.getParameterValues("item_prate");

                String finalid[] = request.getParameterValues("item_fid");


                for (int i = 0; i < finalid.length; i++) {
                    requestParams.put("primary", true);
                    requestParams.put("id", finalid[i]);

                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    tabledata = result.getEntityList();
                    if (!tabledata.isEmpty()) {
                        appmanage = (Appraisalmanagement) tabledata.get(0);
                        requestParams1.put("appmanage", appmanage);
                        requestParams1.put("perf", performance[i]);
//                        //@@ProfileHandler.insertAuditLog(session, AuditAction.APPERAISAL_DONE, "Appraisal of " + (u != null ? AuthHandler.getFullName(u) : "") + " done by " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)), request);
                        hrmsAppraisalDAOObj.appraisalFunction(requestParams1);
                    }

                }
            }
            msgjobj.put("message", flag);
            jobj1.put("valid", true);
            jobj1.put("data", msgjobj.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView saveAnswers(HttpServletRequest request, HttpServletResponse response) {
         KwlReturnObject result;
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        List tabledata = null;
        Iterator ite = null;
        ArrayList filter_names = new ArrayList(), filter_values = new ArrayList(), commentArray = new ArrayList();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            if (Boolean.parseBoolean(request.getParameter("employee"))) {
                filter_names.add("employee.userID");
                filter_values.add(sessionHandlerImplObj.getUserid(request));
                filter_names.add("appcycle.id");
                filter_values.add(request.getParameter("appcycle"));

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                tabledata = result.getEntityList();
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Appraisalmanagement app = (Appraisalmanagement) ite.next();
                    app.setEmployeesubmitdate((Date) fmt.parse(request.getParameter("submitdate")));
                    commentArray.add(app.getAppraisalid());
                    app.setEmployeecomment(request.getParameter("ecomments"));
                    if (!Boolean.parseBoolean(request.getParameter("saveasDraft")))
                        app.setEmployeestatus(1);
                }
            }
            Appraisalmanagement log = (Appraisalmanagement) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalmanagement", request.getParameter("appraisalid"));
            requestParams.clear();
            requestParams.put("performance", request.getParameter("performance"));
            requestParams.put("saveasDraft", request.getParameter("saveasDraft"));
            requestParams.put("mancomment", request.getParameter("mancomment"));
            requestParams.put("ite", ite);
            requestParams.put("commentArray", commentArray);
            requestParams.put("submitdate", request.getParameter("submitdate"));
            if (Boolean.parseBoolean(request.getParameter("employee"))) {
                requestParams.put("manid", sessionHandlerImplObj.getUserid(request));
                requestParams.put("empid", sessionHandlerImplObj.getUserid(request));
                requestParams.put("isemployee", true);
            } else {
                requestParams.put("manid", log.getManager().getUserID());
                requestParams.put("empid", log.getEmployee().getUserID());
                requestParams.put("isemployee", false);
            }
            requestParams.put("appcycle", request.getParameter("appcycle"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("appraisalid"))){
                requestParams.put("appraisalid", request.getParameter("appraisalid"));
            }
            requestParams.put("jsondata", request.getParameter("jsondata"));
            result=hrmsAppraisalDAOObj.saveAnswers(requestParams);

            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
     @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
