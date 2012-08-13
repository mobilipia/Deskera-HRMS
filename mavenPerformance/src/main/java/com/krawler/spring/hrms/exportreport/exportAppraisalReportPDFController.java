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

package com.krawler.spring.hrms.exportreport;


import com.krawler.common.util.AuditAction;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.CompetencyDesMap;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.QuestionGroup;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalDAO;
import com.krawler.spring.hrms.appraisal.hrmsAppraisalDAO;
import com.krawler.spring.hrms.appraisalcycle.bizservice.hrmsAppraisalcycleservice;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.competency.hrmsCompetencyDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author krawler
 */
public class exportAppraisalReportPDFController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private exportAppraisalReportPDFDAOImpl exportAppraisalReportPDFDAOImplObj;
    private hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
    private hrmsAnonymousAppraisalDAO hrmsAnonymousAppraisalDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private MessageSource messageSource;

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setHrmsCompetencyDAO(hrmsCompetencyDAO hrmsCompetencyDAOObj) {
        this.hrmsCompetencyDAOObj = hrmsCompetencyDAOObj;
    }

    public void setHrmsAppraisalcycleservice(hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj) {
        this.hrmsAppraisalcycleserviceobj = hrmsAppraisalcycleserviceobj;
    }
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public void setExportAppraisalReportPDFDAOImpl(exportAppraisalReportPDFDAOImpl exportAppraisalReportPDFDAOImplObj) {
        this.exportAppraisalReportPDFDAOImplObj = exportAppraisalReportPDFDAOImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }


    public String getSuccessView() {
        return successView;
    }
    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    public void setHrmsAppraisalDAO(hrmsAppraisalDAO hrmsAppraisalDAOObj) {
        this.hrmsAppraisalDAOObj = hrmsAppraisalDAOObj;
    }
    public void setHrmsAnonymousAppraisalDAO(hrmsAnonymousAppraisalDAO hrmsAnonymousAppraisalDAOObj) {
        this.hrmsAnonymousAppraisalDAOObj = hrmsAnonymousAppraisalDAOObj;
    }
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
    public ModelAndView appraisalReportExport(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List list = null;
        List tmplist = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        int counter = 1;
        double globalAvg = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        boolean isPrint = false;
        if(request.getParameter("isPrint")!=null)
        	isPrint = request.getParameter("isPrint").equals("false")?false:true;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            ArrayList order_by = new ArrayList(), order_type = new ArrayList();
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = sessionHandlerImplObj.getUserid(request);
            }
            String companyid="";
            if (StringUtil.isNullOrEmpty(request.getParameter("pdfEmail")))
                companyid=sessionHandlerImplObj.getCompanyid(request);
            else {
                User u = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", userID);
                companyid = u.getCompany().getCompanyID();
            }
            
            boolean isQuantitative = false;

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
                
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                tmpObj.put("appcylename", log.getAppcycle().getCyclename());
                if (StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                    SimpleDateFormat fmt1 = new SimpleDateFormat("MMMM d, yyyy");
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
                        isQuantitative = false;
                } else {
                    isQuantitative = true;
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
                            requestParams.put("compid", apsl.getCompetency().getMid());
                            requestParams.put("emp", competencyMid);
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
                            requestParams1.put("companyid", companyid);
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
                JSONArray mancom = new JSONArray();
                for (int j = 0; j < list.size(); j++) {
                    Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                    if (!StringUtil.isNullOrEmpty(apmt.getManagercomment())) {
                       mancom.put(j," " + counter + ") " + apmt.getManagercomment());
//                        if (!StringUtil.isNullOrEmpty(request.getParameter("filetype")) || !StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
//                            manComments += ",";
//                        } else {
//                            manComments += "<br>";
//                        }
                        counter++;
                    }
                }
                if(empavgscore.trim().length()!=0)
                    empavgscore = empavgscore.substring(0, empavgscore.trim().length() - 1);
                requestParams.clear();
                requestParams.put("checklink", "competency");
                requestParams.put("companyid",companyid);
                if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()&&!isQuantitative) { //Check for competency permission
                    tmpObj.put("competencies", cmpObj);
                    tmpObj.put("manavgwght", decimalFormat.format(globalAvg/lst.size()));
                } else {
                    tmpObj.put("competencies", new JSONArray());
                }

                tmpObj.put("mancom", mancom.toString());
                tmpObj.put("compempavgwght", empavgscore);
                requestParams.clear();
                requestParams.put("empid", userID);
                requestParams.put("cycleid", appCycleID);
                result = hrmsAppraisalcycleDAOObj.getAppraisalManagers(requestParams);
                tmplist = result.getEntityList();
                tmpObj.put("totalappraisal", tmplist.get(0));
                tmpObj.put("appraisalsubmitted", tmplist.get(1));
                jarr.put(tmpObj);
            } else {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("competencies", new JSONArray());
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
            requestParams.clear();
            requestParams.put("checklink", "goal");
            requestParams.put("companyid",companyid);
            String empgoals="{data:[]}";
            if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) { //Check for goal permission
                empgoals = getAppraisalReportGoalsforGrid(request, response);
            }

            Appraisalmanagement app = null;
            JSONObject quesans = new JSONObject();
            if(isQuantitative) {
                String allMan = "";
                for (int j = 0; j < list.size(); j++) {
                    app = (Appraisalmanagement) list.get(j);
                    if(j==0&& app.getEmployeedraft() == 0) {
                        allMan += app.getEmployee().getUserID()+",";
                    }
                    allMan += app.getManager().getUserID()+",";
                }
                if(allMan.length() > 0){
                    allMan = allMan.substring(0, allMan.length()-1);
                }
                requestParams.clear();
            filter_names.clear();
            filter_values.clear();
            filter_names.add(hrmsAnonymousAppraisalConstants.appcycleId);
            filter_values.add(appCycleID);
            filter_names.add(hrmsAnonymousAppraisalConstants.employeeUserID);
            filter_values.add(userID);
            filter_names.add(BuildCriteria.OPERATORIN+hrmsAnonymousAppraisalConstants.managerUserID);
            filter_values.add(allMan);
            filter_names.add(BuildCriteria.OPERATORORDER+hrmsAnonymousAppraisalConstants.cmptquestionOrder);
            filter_values.add(BuildCriteria.OPERATORORDERASC);

            requestParams.put(hrmsAnonymousAppraisalConstants.filter_names, filter_names);
            requestParams.put(hrmsAnonymousAppraisalConstants.filter_params, filter_values);
                

                if (!StringUtil.isNullOrEmpty(request.getParameter("filetype")) || !StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                    requestParams.put("export", true);
                }
                result = hrmsAppraisalcycleserviceobj.getQuestionAnswerGrid(requestParams);   // fetch the questions
                List<HashMap<String, String>> queslist = null;
                queslist = result.getEntityList();
                StringBuffer s = new StringBuffer();
                JSONArray jArr1 =  new JSONArray();
                String groupID = "";
                JSONArray quesObj = new JSONArray();
                if (!queslist.isEmpty()) {

                    HashMap<String, String> jsonmap = queslist.get(0);
                    HashMap<String, String> questionjsonmap = queslist.get(1);
                    HashMap<String, String> employeejsonmap = queslist.get(2);
                    HashMap<String, String> questionorderjsonmap = queslist.get(3);
                    count = questionjsonmap.size();
                    if (questionjsonmap.size() > 0) {
                        for (String key : questionjsonmap.keySet()) {
                            s.append(key);
                            s.append(",");
                            JSONObject appObj = new JSONObject();
                            appObj.put(hrmsAnonymousAppraisalConstants.question, questionjsonmap.get(key));
                            appObj.put(hrmsAnonymousAppraisalConstants.answer, jsonmap.containsKey(key)?jsonmap.get(key):"");
                            appObj.put(hrmsAnonymousAppraisalConstants.employeeanswer, employeejsonmap.containsKey(key)?employeejsonmap.get(key):"");
//                            quesObj.put(appObj);
                            quesObj.put(Integer.parseInt(questionorderjsonmap.get(key))-1, appObj);
                        }
                        s.deleteCharAt(s.length()-1);
                    }

                    if(app!=null && app.getEmpdesid()!=null){
                        requestParams.clear();
                        requestParams.put("desigid", app.getEmpdesid().getId());
                        result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
                        if(result.getEntityList().size() > 0){
                            CompetencyDesMap log = (CompetencyDesMap) result.getEntityList().get(0);
                            groupID = log.getGroupid();
                        }
                    }
                    List<QuestionGroup> list1 = null;
                    HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                    ArrayList filter_names1 = new ArrayList();
                    ArrayList filter_params = new ArrayList();
                    filter_names1.add(BuildCriteria.OPERATORNOTIN+"cmptquestion.quesid");
                    filter_names1.add("groupid");

                    filter_params.add(s);
                    filter_params.add(groupID);
                    requestParams1.put("filter_names", filter_names1);
                    requestParams1.put("filter_params", filter_params);
                    result = hrmsAppraisalcycleDAOObj.getQuestions(requestParams1);
                    list1 = result.getEntityList();
                    Set<CompetencyQuestion> competencyQuestions = app.getCompetencyQuestions();
                	Iterator<CompetencyQuestion> iterator = competencyQuestions.iterator();
                	Set<String> questions = new HashSet<String>();
                	while (iterator.hasNext()) {
        				questions.add(iterator.next().getQuesid());	
        			}
                    if (!list1.isEmpty()) {
                        Iterator <QuestionGroup> ite1 = list1.iterator();
                        while (ite1.hasNext()) {
                        	QuestionGroup app1 =  ite1.next();
                        	if(questions.contains(app1.getCmptquestion().getQuesid())){
                        		JSONObject appObj = new JSONObject();
                        		appObj.put(hrmsAnonymousAppraisalConstants.question, app1.getCmptquestion().getQuesdesc());
                        		quesObj.put(app1.getCmptquestion().getQuesorder()-1,appObj);
                        	}
                        }
                    }
                    for(int i =0;i<quesObj.length();i++) {
                        if(!quesObj.isNull(i)) {
                            jArr1.put(quesObj.get(i));
                        }
                    }
                }
                quesans.put("quesans", jArr1);
            } else {
                quesans.put("quesans", new JSONArray());
            }
            
            
            if(!isPrint){
            	exportAppraisalReportPDFDAOImplObj.processRequest(request, response, jobj.toString(), empgoals, quesans.toString());
                if (!StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                    String ipaddr = "";
                     if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                          ipaddr = request.getRemoteAddr();
                      } else {
                          ipaddr = request.getHeader("x-real-ip");
                      }
                    String details = "";
                    if (!StringUtil.isNullOrEmpty(request.getParameter("d"))) {
                        User u = exportAppraisalReportPDFDAOImplObj.getUser(request.getParameter("d"));
                        User u1 = exportAppraisalReportPDFDAOImplObj.getUser(request.getParameter("userid"));
                        Appraisalcycle appcy = exportAppraisalReportPDFDAOImplObj.getAppraisalCycle(request.getParameter("appraisalcycid"));
                        if (StringUtil.equal(request.getParameter("d"), request.getParameter("userid"))) {
                            details = "User " + u.getFirstName() + " " + u.getLastName() + " has downloaded self"
                                    + " appraisal report for appraisal cycle " + appcy.getCyclename() + " through email link";
                        } else {
                            details = "Reviewer " + u.getFirstName() + " " + u.getLastName() + " has downloaded " + u1.getFirstName() + " " + u1.getLastName() + "'s"
                                    + " appraisal report for appraisal cycle " + appcy.getCyclename() + " through email link";
                        }
                        auditTrailDAOObj.insertAuditLog(AuditAction.Appraisal_Report_Download, details, ipaddr, u.getUserID(), "0");
                    }
                } else {
                    String details = "";
                    User u = null;
                    String aID = request.getParameter("appraisalcycid");
                    Appraisalcycle appcy = (Appraisalcycle) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalcycle", aID);
                    String uID = request.getParameter("userid");
                    if (StringUtil.isNullOrEmpty(uID)) {
                        uID = sessionHandlerImplObj.getUserid(request);
                        u = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", uID);
                        details = "User " + StringUtil.getFullName(u) + " has downloaded self"
                                + " appraisal report for appraisal cycle " + appcy.getCyclename() + " through Deskera HRMS";
                    } else {
                        u = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", uID);

                        details = "Reviewer " + StringUtil.getFullName((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", sessionHandlerImplObj.getUserid(request))) + " has downloaded " + StringUtil.getFullName(u) + "'s"
                                + " appraisal report for appraisal cycle " + appcy.getCyclename() + " through Deskera HRMS";
                    }
                    auditTrailDAOObj.insertAuditLog(AuditAction.Appraisal_Report_Download, details, request, "0");
                }
            }else{
            	exportAppraisalReportPDFDAOImplObj.printReport(request, response, jobj.toString(), empgoals, quesans.toString());
            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public String getAppraisalReportGoalsforGrid(HttpServletRequest request, HttpServletResponse response) {
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

//            String userID = request.getParameter("empid");
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = sessionHandlerImplObj.getUserid(request);
            }
//            String appCycleID = request.getParameter("appcycleid");
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
                                appObj.put("assignedby", StringUtil.getFullName(app1.getGoal().getManager()));
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
        return jobj.toString();
    }

    public ModelAndView appraisalReportExport1(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try{
            jobj.put("data", jarr);
            jobj.put("count", count);

//         /   exportAppraisalReportPDFDAOImplObj.processRequest(request, response, jobj.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
