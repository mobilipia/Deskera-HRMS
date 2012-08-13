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
package com.krawler.spring.hrms.anonymousappraisal;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.AppraisalQuestionAnswers;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.hrms.performance.CompetencyDesMap;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.competencyAvg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.appraisal.hrmsAppraisalDAO;
import com.krawler.spring.hrms.appraisalcycle.bizservice.hrmsAppraisalcycleservice;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.competency.hrmsCompetencyDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

public class hrmsAnonymousAppraisalController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
    private hrmsAnonymousAppraisalDAO hrmsAnonymousAppraisalDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private MessageSource messageSource;

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }


    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public hrmsAppraisalcycleDAO getHrmsAppraisalcycleDAOObj() {
        return hrmsAppraisalcycleDAOObj;
    }

    public void setHrmsAppraisalcycleservice(hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj) {
        this.hrmsAppraisalcycleserviceobj = hrmsAppraisalcycleserviceobj;
    }

    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public hrmsAppraisalDAO getHrmsAppraisalDAO() {
        return hrmsAppraisalDAOObj;
    }

    public void setHrmsAppraisalDAO(hrmsAppraisalDAO hrmsAppraisalDAOObj) {
        this.hrmsAppraisalDAOObj = hrmsAppraisalDAOObj;
    }

    public hrmsAnonymousAppraisalDAO getHrmsAnonymousAppraisalDAO() {
        return hrmsAnonymousAppraisalDAOObj;
    }

    public void setHrmsAnonymousAppraisalDAO(hrmsAnonymousAppraisalDAO hrmsAnonymousAppraisalDAOObj) {
        this.hrmsAnonymousAppraisalDAOObj = hrmsAnonymousAppraisalDAOObj;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public void setHrmsCompetencyDAO(hrmsCompetencyDAO hrmsCompetencyDAOObj) {
        this.hrmsCompetencyDAOObj = hrmsCompetencyDAOObj;
    }

    public ModelAndView getAppraisalReport(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List list = null;
        List tmplist = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        SimpleDateFormat fmt1 = new SimpleDateFormat("MMMM d, yyyy");
        int count = 0;
        int cnt = 0;
        String hql = "";
        String manComments = "";
        int counter = 1;
        double globalAvg = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            ArrayList order_by = new ArrayList(), order_type = new ArrayList();
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = AuthHandler.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);

                filter_names.add("employee.userID");
                filter_values.add(userID);

                filter_names.add("reviewstatus");
                filter_values.add(0);

                filter_names.add("managerstatus");
                filter_values.add(1);

                order_by.add("rand()");
                order_type.add("asc");

            } else {
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);

                filter_names.add("employee.userID");
                filter_values.add(userID);

                filter_names.add("reviewstatus");
                filter_values.add(2);

                order_by.add("rand()");
                order_type.add("asc");
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            list = result.getEntityList();
            count = result.getRecordTotalCount();
            if (list.size() > 0) {
                Appraisalmanagement log = (Appraisalmanagement) list.get(0);
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getEmployee().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("empname", log.getEmployee().getFirstName() + " " + log.getEmployee().getLastName());
                tmpObj.put("appraisalid", log.getAppraisalid());
                tmpObj.put("mgr", log.getManager().getUserID());
                if (log.getEmployeedraft() == 0) {
                    tmpObj.put("empcomment", log.getEmployeecomment());
                } else {
                    tmpObj.put("empcomment", "");
                }
                tmpObj.put("reviewercomment", log.getReviewercomment());
                tmpObj.put("designation", log.getEmpdesid().getValue());
                CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", sessionHandlerImplObj.getCompanyid(request));
                if(cp.isOverallcomments()){
                    tmpObj.put("isoverallcomment", true);
                } else {
                    tmpObj.put("isoverallcomment", false);
                }
                if(cp.isSelfappraisal()) {
                    tmpObj.put("isselfappraisal", true);
                } else {
                    tmpObj.put("isselfappraisal", false);
                }
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                tmpObj.put("appcylename", log.getAppcycle().getCyclename());
                if (StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                    tmpObj.put("appcylestdate", fmt1.format(log.getAppcycle().getStartdate()));
                    tmpObj.put("appcylendate", fmt1.format(log.getAppcycle().getEnddate()));

                } else {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
                    tmpObj.put("appcylestdate", df.format(log.getAppcycle().getStartdate()));
                    tmpObj.put("appcylendate", df.format(log.getAppcycle().getEnddate()));
                }
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                String empavgscore = "";
                requestParams.clear();
                requestParams.put("appid", log.getAppraisalid());
                result = hrmsAppraisalDAOObj.getAppraisalforAppCyc(requestParams);
                List lst = result.getEntityList();
                JSONArray cmpObj = new JSONArray();
                requestParams.clear();
                requestParams.put("appid", request.getParameter("appraisalcycid"));
                requestParams.put("userid", userID);
                requestParams.put("globalavg", true);

//                result = hrmsAppraisalDAOObj.getCompAvgScore(requestParams);
//                List list2 = result.getEntityList();
//                double avg = 0;
//                String globalAvg = "0.00";
//                if (!list2.isEmpty() && list2.get(0) != null) {
//                    avg = (Double) list2.get(0);
//                    DecimalFormat df = new DecimalFormat("#.##");
//                    globalAvg = df.format(avg);
//                    if (!globalAvg.contains(".")) {
//                        globalAvg += ".00";
//                    }
//                }
                
                if(lst!=null && lst.size()>0){
                    tmpObj.put("isquestionemp", "false");
                } else {
                	tmpObj.put("isquestionemp", "true");
                }
                
                for (int i = 0; i < lst.size(); i++) {
                    Appraisal apsl = (Appraisal) lst.get(i);
                    JSONArray comments = new JSONArray();
                    JSONObject appObj = new JSONObject();
                    if (apsl.getCompetency() != null) {
                        String empwght = "";
                        String manwght = "";
                        appObj.put("comptename", apsl.getCompetency().getMastercmpt().getCmptname());
                        appObj.put("comptdesc", apsl.getCompetency().getMastercmpt().getCmptdesc());
                        if (log.getEmployeedraft() == 0) {
                            appObj.put("selfcomment", apsl.getCompempcomment());
                            appObj.put("selfcompscore", apsl.getCompemprating() == 0 ? "" : apsl.getCompemprating());
                        } else {
                            appObj.put("selfcomment", "");
                            appObj.put("selfcompscore", "");
                        }
                        double avgRat = 0;
                        String competencyMid = apsl.getCompetency().getMid();
                        ArrayList initialcomments = new ArrayList();
                        ArrayList initialscores = new ArrayList();
                        JSONObject jempcomments = new JSONObject();
                        for (int j = 0; j < list.size(); j++) {
                            Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                            requestParams.clear();
                            requestParams.put("appcyc", apmt.getAppcycle());
                            requestParams.put("compid", competencyMid);
                            requestParams.put("emp", apmt.getEmployee());
                            requestParams.put("avg", true);
                            result = hrmsAppraisalDAOObj.getCompAvgScore(requestParams);
                            List alst = result.getEntityList();

//                            if (alst.size() > 0) {
//                                competencyAvg compavg = (competencyAvg) alst.get(0);
//                                avgRat = compavg.getManavg();
//                            }

                            requestParams.clear();
                            filter_names.clear();
                            filter_values.clear();

                            filter_names.add("appraisal.appraisalid");
                            filter_values.add(apmt.getAppraisalid());
                            filter_names.add("competency.mid");
                            filter_values.add(competencyMid);

                            requestParams.put("filter_names", filter_names);
                            requestParams.put("filter_values", filter_values);
                            result = hrmsAppraisalDAOObj.getAppraisal(requestParams);
                            List wlst = result.getEntityList();
                            if (wlst.size() > 0) {
                                Double wt1, wt2;
                                Appraisal wapsl = (Appraisal) wlst.get(0);
                                wt1 = wapsl.getCompmanrating();
                                wt2 = wapsl.getCompemprating();
                                empwght += wt2.intValue() + ", ";
                                if (wt1.intValue() != 0) {
                                    initialscores.add(wt1.intValue() + "");
                                }
                                if (!StringUtil.isNullOrEmpty(wapsl.getCompmancomment())) {
                                    initialcomments.add(wapsl.getCompmancomment());
                                }
                                if (i < 1) {
                                    empavgscore += apmt.getEmployeecompscore() + ", ";
                                }
                            }
                        }
                        
                        if(initialscores!=null){
                        	HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                            requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                            requestParams1.put("checklink", "modaverage");
                            ArrayList newArr = (ArrayList) initialscores.clone();
                            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                            	Collections.sort(newArr);
                                if (newArr.size() > 2) {
                                    for (int c = 1; c < newArr.size() - 1; c++) {
                                        avgRat += Double.parseDouble(newArr.get(c).toString());
                                    }
                                    avgRat = avgRat / (newArr.size() - 2);
                                } else {
                                    for (int c = 0; c < newArr.size(); c++) {
                                        avgRat += Double.parseDouble(newArr.get(c).toString());
                                    }
                                    if(newArr.size()>0){
                                    avgRat = avgRat / newArr.size();
                                    }
                                }
                            } else {
                                for (int c = 0; c < newArr.size(); c++) {
                                    avgRat += Double.parseDouble(newArr.get(c).toString());
                                }
                                if(newArr.size()>0){
                                avgRat = avgRat / newArr.size();
                                }
                            }
                        }
                        globalAvg+=avgRat;
                        
                        empwght = empwght.substring(0, empwght.trim().length() - 1);
                        String scoreAvg = "0.00";
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
                        if (manwght.length() > 0) {
                            manwght = manwght.substring(0, manwght.trim().length() - 1);
                        }
                        appObj.put("comments", comments);
                        appObj.put("compempwght", empwght);
                        appObj.put("compmanwght", manwght);
                        appObj.put("nominalRat", scoreAvg);
                    }
                    cmpObj.put(appObj);
                }
                for (int j = 0; j < list.size(); j++) {
                Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                if (!StringUtil.isNullOrEmpty(apmt.getManagercomment())) {
                    manComments += "<b> " + counter + ")</b> " + apmt.getManagercomment();
                    if (!StringUtil.isNullOrEmpty(request.getParameter("filetype")) || !StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                        manComments += ",";
                    } else {
                        manComments += "<br>";
                    }
                    counter++;
                }
                }
                if(empavgscore.trim().length()!=0)
                    empavgscore = empavgscore.substring(0, empavgscore.trim().length() - 1);
                tmpObj.put("competencies", cmpObj);
                tmpObj.put("mancom", manComments);
                tmpObj.put("compempavgwght", empavgscore);
                tmpObj.put("manavgwght", decimalFormat.format(globalAvg/lst.size()));
                requestParams.clear();
                requestParams.put("empid", userID);
                requestParams.put("cycleid", appCycleID);
                result = hrmsAppraisalcycleDAOObj.getAppraisalManagers(requestParams);
                tmplist = result.getEntityList();
                tmpObj.put("totalappraisal", tmplist.get(0));
                tmpObj.put("appraisalsubmitted", tmplist.get(1));
                jarr.put(tmpObj);
            }

            JSONObject countObj = new JSONObject();
            requestParams.clear();
            requestParams.put("empid", userID);
            requestParams.put("cycleid", appCycleID);
            result = hrmsAppraisalcycleDAOObj.getAppraisalManagers(requestParams);
            tmplist = result.getEntityList();
            countObj.put("totalappraisal", tmplist.get(0));
            countObj.put("appraisalsubmitted", tmplist.get(1));
            countObj.put("data", jarr);
            jarr1.put(countObj);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getAppraisalReportforGrid(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            ArrayList order_by = new ArrayList(), order_type = new ArrayList();

//            requestParams.put("companyid", AuthHandler.getCompanyid(request));
//            Company cmp;
//            result = hrmsCommonDAOObj.getCompanyid(requestParams);
//            Company cmp = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
//            list = result.getEntityList();
//            if (list.iterator().hasNext()) {
//                cmp = (Company) list.iterator().next();
//            } else {
//                cmp = null;
//            }
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = AuthHandler.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);

                filter_names.add("employee.userID");
                filter_values.add(userID);

                filter_names.add("reviewstatus");
                filter_values.add(0);

                filter_names.add("managerstatus");
                filter_values.add(1);

                order_by.add("rand()");
                order_type.add("asc");
            } else {
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);

                filter_names.add("employee.userID");
                filter_values.add(userID);

                filter_names.add("reviewstatus");
                filter_values.add(2);

                order_by.add("rand()");
                order_type.add("asc");
            }

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            list = result.getEntityList();
            count = result.getRecordTotalCount();

            JSONArray cmpObj = new JSONArray();
            String jempcomments = "";
            if (list.size() > 0) {
                Appraisalmanagement log = (Appraisalmanagement) list.get(0);
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getEmployee().getUserID());
                JSONObject tmpObj = new JSONObject();
                requestParams.clear();
                requestParams.put("empid", ua.getEmployeeid());
                requestParams.put("companyid", AuthHandler.getCompanyid(request));
                tmpObj.put("empid", ua.getEmployeeid() == null ? "" : hrmsCommonDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                tmpObj.put("empname", AuthHandler.getFullName(log.getEmployee()));
                tmpObj.put("reviewercomment", log.getReviewercomment());
                tmpObj.put("designation", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                tmpObj.put("appcylename", log.getAppcycle().getCyclename());
                tmpObj.put("appcylestdate", kwlCommonTablesDAOObj.getUserDateFormatter(AuthHandler.getDateFormatID(request), AuthHandler.getUserTimeFormat(request), AuthHandler.getTimeZoneDifference(request)).format(log.getAppcycle().getStartdate()));
                tmpObj.put("appcylendate", kwlCommonTablesDAOObj.getUserDateFormatter(AuthHandler.getDateFormatID(request), AuthHandler.getUserTimeFormat(request), AuthHandler.getTimeZoneDifference(request)).format(log.getAppcycle().getEnddate()));
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                Double manageravgscore = 0.0;
                String empavgscore = "";
                requestParams.clear();
                requestParams.put("appid", log.getAppraisalid());
                result = hrmsAppraisalDAOObj.getAppraisalforAppCyc(requestParams);
                List lst = result.getEntityList();
                cmpObj = new JSONArray();
                for (int i = 0; i < lst.size(); i++) {
                    Appraisal apsl = (Appraisal) lst.get(i);
                    JSONObject appObj = new JSONObject();
                    if (apsl.getCompetency() != null) {
                        String empwght = "";
                        String manwght = "";
                        appObj.put("comptename", apsl.getCompetency().getMastercmpt().getCmptname());
                        appObj.put("comptdesc", apsl.getCompetency().getMastercmpt().getCmptdesc());
                        if (log.getEmployeedraft() == 0) {
                            appObj.put("selfcomment", apsl.getCompempcomment());
                            appObj.put("selfcompscore", apsl.getCompemprating() == 0 ? "" : apsl.getCompemprating());
                        } else {
                            appObj.put("selfcomment", "");
                            appObj.put("selfcompscore", "");
                        }
                        requestParams.clear();
                        requestParams.put("appid", request.getParameter("appraisalcycid"));
                        requestParams.put("compid", apsl.getCompetency().getMastercmpt().getCmptid());
                        requestParams.put("compavg", true);
                        result = hrmsAppraisalDAOObj.getCompAvgScore(requestParams);
                        List list2 = result.getEntityList();
                        double avg = 0;
                        if (!list2.isEmpty() && list2.get(0) != null) {
                            avg = (Double) list2.get(0);
                        }
                        appObj.put("global", avg);

                        int countComment = 1;
                        jempcomments = "";
                        ArrayList initialcomments = new ArrayList();
                        ArrayList initialscores = new ArrayList();
                        double avgRat = 0;
                        for (int j = 0; j < list.size(); j++) {
                            Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                            requestParams.clear();
                            requestParams.put("appcyc", apmt.getAppcycle());
                            requestParams.put("emp", apmt.getEmployee());
                            requestParams.put("avg", true);
                            requestParams.put("compid", apsl.getCompetency().getMid());
                            result = hrmsAppraisalDAOObj.getCompAvgScore(requestParams);
                            List alst = result.getEntityList();
//                            if (alst.size() > 0) {
//                                competencyAvg compavg = (competencyAvg) alst.get(0);
//                                avgRat = compavg.getManavg();
//                            }
                            requestParams.clear();
                            filter_names.clear();
                            filter_values.clear();

                            filter_names.add("appraisal.appraisalid");
                            filter_values.add(apmt.getAppraisalid());
                            filter_names.add("competency.mid");
                            filter_values.add(apsl.getCompetency().getMid());

                            requestParams.put("filter_names", filter_names);
                            requestParams.put("filter_values", filter_values);
                            result = hrmsAppraisalDAOObj.getAppraisal(requestParams);
                            List wlst = result.getEntityList();
                            if (wlst.size() > 0) {
                                Double wt1, wt2;
                                Appraisal wapsl = (Appraisal) wlst.get(0);
                                wt1 = wapsl.getCompmanrating();
                                wt2 = wapsl.getCompemprating();
                                empwght += wt2.intValue() + ", ";
                                if (wt1.intValue() != 0) {
                                    initialscores.add(wt1.intValue() + "");
                                }
                                if (!StringUtil.isNullOrEmpty(wapsl.getCompmancomment())) {
                                    initialcomments.add(wapsl.getCompmancomment());
                                }
                                countComment++;
                                if (i < 1) {
                                    empavgscore += apmt.getEmployeecompscore() + ", ";
                                    //manageravgscore += apmt.getManagercompscore() + ", ";
                                }
                            }
                        }
                        if(empwght.trim().length()!=0)
                            empwght = empwght.substring(0, empwght.trim().length() - 1);

                        if(initialscores!=null){
                        	HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                            requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                            requestParams1.put("checklink", "modaverage");
                            ArrayList newArr = (ArrayList) initialscores.clone();
                            if (hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()) {
                            	Collections.sort(newArr);
                                if (newArr.size() > 2) {
                                    for (int c = 1; c < newArr.size() - 1; c++) {
                                        avgRat += Double.parseDouble(newArr.get(c).toString());
                                    }
                                    avgRat = avgRat / (newArr.size() - 2);
                                } else {
                                    for (int c = 0; c < newArr.size(); c++) {
                                        avgRat += Double.parseDouble(newArr.get(c).toString());
                                    }
                                    if(newArr.size()>0){
                                    avgRat = avgRat / newArr.size();
                                    }
                                }
                            } else {
                                for (int c = 0; c < newArr.size(); c++) {
                                    avgRat += Double.parseDouble(newArr.get(c).toString());
                                }
                                if(newArr.size()>0){
                                avgRat = avgRat / newArr.size();
                                }
                            }
                        }
                        manageravgscore+=avgRat;
                        
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
                            jempcomments += "" + (indx) + ")  " + inicom + "<br>";
                            indx++;
                        }
                        shufflist = initialscores;
                        Collections.shuffle(shufflist);
                        for (String inicom : shufflist) {
                            manwght += inicom + ", ";
                        }
                        if (manwght.length() > 0) {
                            manwght = manwght.substring(0, manwght.trim().length() - 1);
                        }
                        appObj.put("comments", jempcomments);
                        appObj.put("compempwght", empwght);
                        appObj.put("compmanwght", manwght);
                        appObj.put("nominalRat", scoreAvg);
                    }
                    cmpObj.put(appObj);
                }
                if(empavgscore.trim().length()!=0)
                    empavgscore = empavgscore.substring(0, empavgscore.trim().length() - 1);
//                if(manageravgscore.trim().length()!=0)
//                manageravgscore = manageravgscore.substring(0, manageravgscore.trim().length() - 1);



                tmpObj.put("data", cmpObj);
                tmpObj.put("compempavgwght", empavgscore);
                tmpObj.put("manavgwght", manageravgscore/list.size());
//                tmpObj = getAppraisalManagers(session, userID, appCycleID, tmpObj);
                requestParams.clear();
                requestParams.put("empid", userID);
                requestParams.put("cycleid", appCycleID);
                result = hrmsAppraisalcycleDAOObj.getAppraisalManagers(requestParams);
                List tmplist = result.getEntityList();
                tmpObj.put("totalappraisal", tmplist.get(0));
                tmpObj.put("appraisalsubmitted", tmplist.get(1));
                jarr.put(tmpObj);
            }

            jobj.put("success", true);
            jobj.put("data", cmpObj);
            jobj.put("totalCount", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
//            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView reviewanonymousAppraisalReport(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String employeeid = request.getParameter("employeeid");
            String appcycleid = request.getParameter("appraisalcycleid");
            MasterData revdept = null, revdesig = null;
            float salary;
            float salaryincrement = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            filter_names.add("employee.userID");
            filter_values.add(employeeid);

            filter_names.add("appcycle.id");
            filter_values.add(appcycleid);

            filter_names.add("managerstatus");
            filter_values.add(1);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            List list = result.getEntityList();
            Iterator ite = list.iterator();
            while (ite.hasNext()) {//Change status of all submitted request
                Appraisalmanagement app = (Appraisalmanagement) ite.next();
                if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) {//To approve request
                    requestParams.clear();
                    requestParams.put("empid", employeeid);
                    result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                    User user = (User) result.getEntityList().iterator().next();
                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
                    if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                        requestParams.clear();
                        requestParams.put("masterid", request.getParameter("department"));
                        result = hrmsCommonDAOObj.getMasterData(requestParams);
                        revdept = (MasterData) result.getEntityList().iterator().next();
                    }

                    if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
                        requestParams.clear();
                        requestParams.put("masterid", request.getParameter("designation"));
                        result = hrmsCommonDAOObj.getMasterData(requestParams);
                        revdesig = (MasterData) result.getEntityList().iterator().next();
                    }
                    requestParams.clear();
                    requestParams.put("appmgmt", app);
                    requestParams.put("approve", request.getParameter("reviewstatus"));
                    requestParams.put("oridesig", ua.getDesignationid());
                    requestParams.put("oridept", ua.getDepartment());
                    requestParams.put("revdept", revdept);
                    if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                        requestParams.put("revcomment", request.getParameter("reviewercomment"));
                    }
                    requestParams.put("revdesig", revdesig);
                    requestParams.put("revstat", 2);
                    requestParams.put("apprstat", "submitted");
                    requestParams.put("empstat", 1);
                    requestParams.put("mgrstat", 1);
                    requestParams.put("revsubmitdate", new Date());

                    if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                        requestParams.put("revsalinc", Float.parseFloat(request.getParameter("salaryincrement")));
                    } else {
                        requestParams.put("revsalinc", Float.parseFloat("0"));
                    }
                } else { //To unapprove the request
                    requestParams.clear();
                    requestParams.put("appmgmt", app);
                    requestParams.put("approve", request.getParameter("reviewstatus"));
                    requestParams.put("mgrstat", 0);
                    requestParams.put("revstat", 1);
                    if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                        requestParams.put("revcomment", request.getParameter("reviewercomment"));
                    }
                    requestParams.put("apprstat", "pending");
                }
                result = hrmsAnonymousAppraisalDAOObj.reviewanonymousAppraisalReport(requestParams);
            }
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) { //Update user information
                int histsave = 0;
                String histdept="";
                String histdesig="";
                String histsal="";
                Date saveDate = new Date();
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
                saveDate = new Date(fmt.format(saveDate));
                String updatedby = sessionHandlerImplObj.getUserid(request);

                revdesig = null;
                revdept = null;
                requestParams.clear();
                requestParams.put("empid", employeeid);
                result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                User user = (User) result.getEntityList().iterator().next();
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
                requestParams.clear();
                if (StringUtil.isNullOrEmpty(ua.getSalary())) {
                    salary = 0;
                } else {
                    salary = Float.parseFloat(ua.getSalary());
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                    salaryincrement = Float.parseFloat(request.getParameter("salaryincrement"));
                    histsal=ua.getSalary();
                } else {
                    salaryincrement = 0;
                }
                salary = salary + (salary * salaryincrement) / 100;

                if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
//                    requestParams.clear();
//                    requestParams.put("masterid", request.getParameter("designation"));
//                    result = hrmsCommonDAOObj.getMasterData(requestParams);
//                    revdesig = (MasterData) result.getEntityList().iterator().next();
                    revdesig = (MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", request.getParameter("designation"));
                    if (revdesig != ua.getDesignationid()) {
                        histdesig = ua.getDesignationid().getId();
                        histsave = 1;
                    }
                }

                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
//                    requestParams.clear();
//                    requestParams.put("masterid", request.getParameter("department"));
//                    result = hrmsCommonDAOObj.getMasterData(requestParams);
//                    revdept = (MasterData) result.getEntityList().iterator().next();
                    revdept = (MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", request.getParameter("department"));
                    if (revdept != ua.getDepartment()) {
                        histdept = ua.getDepartment().getId();
                        if (histsave == 0) {
                            histdesig = ua.getDesignationid().getId();
                        }
                        histsave = 2;
                    }
                }
                
                requestParams.clear();
                requestParams.put("user", user);
                requestParams.put("salary", "" + salary);
                requestParams.put("revdesig", revdesig);
                requestParams.put("revdept", revdept);
                result = hrmsAnonymousAppraisalDAOObj.changeSalaryonReview(requestParams);
            
                requestParams.clear();
                if (histsave == 1) {
                    histdept = ua.getDepartment().getId();
                }
                if (histsave == 1 || histsave == 2) {
                    requestParams.put("Department", histdept);
                    requestParams.put("Designation", histdesig);
                    requestParams.put("Userid", user.getUserID());
                    requestParams.put("Empid", ua.getEmployeeid());
                    requestParams.put("Updatedon", saveDate);
                    requestParams.put("Updatedby", updatedby);
                    requestParams.put("Category", Emphistory.Emp_Desg_change);
                    result = hrmsCommonDAOObj.addEmphistory(requestParams);
                }
                if (!histsal.equals("")) {
                    requestParams.clear();
                    requestParams.put("Userid", user.getUserID());
                    requestParams.put("Salary", histsal);
                    requestParams.put("Updatedon", saveDate);
                    requestParams.put("Updatedby", updatedby);
                    requestParams.put("Category", Emphistory.Emp_Salary);
                    result = hrmsCommonDAOObj.addEmphistory(requestParams);
                }
            }
            jobj.put("success", result.isSuccessFlag());
            jobj.put("msg", messageSource.getMessage("hrms.performance.Appraisalisapprovedandemployeehistoryaddedsuccessfully",null,"Appraisal is approved and employee history added successfully.", RequestContextUtils.getLocale(request)));
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getAppraisalReportGoalsforGrid(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null,result1 = null;
        List list = null,list1 = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            ArrayList order_by = new ArrayList(), order_type = new ArrayList();

            String userID = request.getParameter("empid");
            String appCycleID = request.getParameter("appcycleid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);

                filter_names.add("employee.userID");
                filter_values.add(userID);

                filter_names.add("reviewstatus");
                filter_values.add(0);

                filter_names.add("managerstatus");
                filter_values.add(1);
            } else {
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);

                filter_names.add("employee.userID");
                filter_values.add(userID);

                filter_names.add("reviewstatus");
                filter_values.add(2);
            }

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            list = result.getEntityList();
            count = result.getRecordTotalCount();
            JSONArray cmpObj = new JSONArray();
            if (!list.isEmpty()) {
                //Appraisalmanagement appmanage = (Appraisalmanagement) session.load(Appraisalmanagement.class, request.getParameter("appraisal"));
                Iterator ite = list.iterator();
                while (ite.hasNext()) {
                    Appraisalmanagement app = (Appraisalmanagement) ite.next();
                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("appraisal.appraisalid");
                    filter_values.add(app.getAppraisalid());
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    result1 = hrmsAppraisalDAOObj.getAppraisal(requestParams);
                    list1 = result1.getEntityList();
                    if (!list1.isEmpty()) {
                        Iterator ite1 = list1.iterator();
                        while (ite1.hasNext()) {
                            Appraisal app1 = (Appraisal) ite1.next();
                            if (app1.getGoal() != null) {
                                JSONObject appObj = new JSONObject();
                                appObj.put("gid", app1.getGoal().getId());
                                appObj.put("goalid", app1.getAppid());//goalspcificid(primary id appraisal)
                                appObj.put("gname", app1.getGoal().getGoalname());
                                appObj.put("gwth", app1.getGoal().getGoalwth());//goal weightage
                                appObj.put("assignedby", AuthHandler.getFullName(app1.getGoal().getManager()));
                                if (app.getManagerdraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                    appObj.put("gmanrat", "");
                                    appObj.put("mangoalcomment", "");
                                } else if(app.getManagerdraft()==0 && Boolean.parseBoolean(request.getParameter("employee"))){
                                    appObj.put("gmanrat", app1.getGoalmanrating());
                                    appObj.put("mangoalcomment", app1.getGoalmancomment());
                                }
                                else{
                                    appObj.put("gmanrat", app1.getGoalmanrating());
                                    appObj.put("mangoalcomment", app1.getGoalmancomment());
                                }
                                if (app.getEmployeedraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                    appObj.put("gemprat", app1.getGoalemprating());
                                    appObj.put("empgoalcomment", app1.getGoalempcomment());
                                } else if (app.getEmployeedraft()==0) {
                                    appObj.put("gemprat", app1.getGoalemprating());
                                    appObj.put("empgoalcomment", app1.getGoalempcomment());
                                }
                                else{
                                    appObj.put("gemprat", "");
                                    appObj.put("empgoalcomment", "");
                                }
                                cmpObj.put(appObj);
                            }
                        }
                    }
                }
            }
            jobj.put("success", true);
            jobj.put("data", cmpObj);
            jobj.put("totalCount", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
    public ModelAndView getQuestionAnswerGrid(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List<HashMap<String, String>> list = null;
        String groupID = "";
        String allMan = "";
        JSONObject jobj1 = new JSONObject();
        
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String userID = request.getParameter(hrmsAnonymousAppraisalConstants.userid);
            String appCycleID = request.getParameter(hrmsAnonymousAppraisalConstants.appraisalcycid);
            if(StringUtil.isNullOrEmpty(userID)){
               userID = sessionHandlerImplObj.getUserid(request);
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList filter_values = new ArrayList();
            filter_names.add("appcycle.id");
            filter_values.add(appCycleID);

            filter_names.add("employee.userID");
            filter_values.add(userID);

            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) {
                filter_names.add("managerstatus");
                filter_values.add(1);

                filter_names.add("reviewstatus");
                filter_values.add(0);
            } else {
                filter_names.add("reviewstatus");
                filter_values.add(2);
            }

            Appraisalmanagement app = null;
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            list = result.getEntityList();
            if (!list.isEmpty()) {
                Iterator ite = list.iterator();
                while (ite.hasNext()) {
                    app = (Appraisalmanagement) ite.next();
                    if(allMan.equals("") && app.getEmployeedraft() == 0) {
                        allMan += app.getEmployee().getUserID()+",";
                    }
                    allMan += app.getManager().getUserID()+",";
                }
                if(allMan.length() > 0){
                    allMan = allMan.substring(0, allMan.length()-1);
                }
            }
            
            filter_names.clear();
            filter_params.clear();
            filter_names.add(hrmsAnonymousAppraisalConstants.appcycleId);
            filter_params.add(appCycleID);
            filter_names.add(hrmsAnonymousAppraisalConstants.employeeUserID);
            filter_params.add(userID);
            filter_names.add(BuildCriteria.OPERATORIN+hrmsAnonymousAppraisalConstants.managerUserID);
            filter_params.add(allMan);
            filter_names.add(BuildCriteria.OPERATORORDER+hrmsAnonymousAppraisalConstants.cmptquestionOrder);
            filter_params.add(BuildCriteria.OPERATORORDERASC);

            requestParams.put(hrmsAnonymousAppraisalConstants.filter_names, filter_names);
            requestParams.put(hrmsAnonymousAppraisalConstants.filter_params, filter_params);

            result = hrmsAppraisalcycleserviceobj.getQuestionAnswerGrid(requestParams);
            list = result.getEntityList();
            
            if(app!=null && app.getEmpdesid()!=null){
                requestParams.clear();
                requestParams.put("desigid", app.getEmpdesid().getId());
                result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
                if(result.getEntityList().size() > 0){
                    CompetencyDesMap log = (CompetencyDesMap) result.getEntityList().get(0);
                    groupID = log.getGroupid();
                }
            }
            
            JSONObject jobj = hrmsAppraisalcycleserviceobj.getQuestionAnswerGridJson(list, groupID, app);
            jobj.put(hrmsAnonymousAppraisalConstants.success, true);
            jobj1.put(hrmsAnonymousAppraisalConstants.valid, true);
            jobj1.put(hrmsAnonymousAppraisalConstants.data, jobj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
    
}
