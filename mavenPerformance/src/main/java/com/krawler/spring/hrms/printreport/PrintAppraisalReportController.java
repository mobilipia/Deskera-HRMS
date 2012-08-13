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
package com.krawler.spring.hrms.printreport;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.CompetencyDesMap;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.spring.hrms.appraisal.hrmsAppraisalDAO;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.spring.hrms.appraisalcycle.bizservice.hrmsAppraisalcycleservice;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.competency.hrmsCompetencyDAO;
import com.krawler.spring.hrms.goal.hrmsGoalDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

public class PrintAppraisalReportController extends MultiActionController{
	private String successView;
	private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private PrintAppraisalReportDAOImp printAppraisalReportDAOImp;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
    private hrmsGoalDAO hrmsGoalDAOObj;
    
    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }
    
    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
    
    public void setPrintAppraisalReportDAOImp(PrintAppraisalReportDAOImp printAppraisalReportDAOImp) {
        this.printAppraisalReportDAOImp = printAppraisalReportDAOImp;
    }
    
    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }
    
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
    
    public void setHrmsCompetencyDAO(hrmsCompetencyDAO hrmsCompetencyDAOObj) {
        this.hrmsCompetencyDAOObj = hrmsCompetencyDAOObj;
    }
    
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    
    public void setHrmsAppraisalcycleservice(hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj) {
        this.hrmsAppraisalcycleserviceobj = hrmsAppraisalcycleserviceobj;
    }
    
    public void setHrmsAppraisalDAO(hrmsAppraisalDAO hrmsAppraisalDAOObj) {
        this.hrmsAppraisalDAOObj = hrmsAppraisalDAOObj;
    }
    
    public hrmsGoalDAO getHrmsGoalDAO() {
        return hrmsGoalDAOObj;
    }

    public void setHrmsGoalDAO(hrmsGoalDAO hrmsGoalDAOObj) {
        this.hrmsGoalDAOObj = hrmsGoalDAOObj;
    }
    
    public ModelAndView appraisalFormExport(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jsonData = new JSONObject();
    	JSONObject jsonResp = new JSONObject();
    	JSONArray jsonArr = new JSONArray();
    	Boolean isMyAppraisal;
    	
    	ArrayList<String> filter_names = new ArrayList<String>();
    	ArrayList<Object> filter_values = new ArrayList<Object>();
    	KwlReturnObject result = null;
    	
    	Appraisalmanagement app;
    	HashMap<String, Object> requestParams = new HashMap<String, Object>();
    	HashMap<String, String> personalData = new HashMap<String, String>();
    	
    	double ans = 0;
    	try{
    		String companyid = sessionHandlerImplObj.getCompanyid(request);
    		boolean isPrint = request.getParameter("isPrint").equals("true")?true:false;	
    		filter_names.add("appcycle.id");
            filter_values.add(request.getParameter("appraisalcycid"));
            filter_names.add("employee.userID");
            if(request.getParameter("uid").equals("undefined")){
            	isMyAppraisal = true;
            	filter_values.add(sessionHandlerImplObj.getUserid(request));
            }else{
            	isMyAppraisal = false;
            	filter_values.add(request.getParameter("uid"));	
            }
            if(!isMyAppraisal){
            	filter_names.add("manager.userID");
            	filter_values.add(request.getParameter("managerid"));
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            List<Appraisalmanagement> list = result.getEntityList();
            if(!list.isEmpty()){
            	app =  list.get(0);
            	Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", app.getEmployee().getUserID());
            	personalData.put("empname", app.getEmployee().getFirstName() + " " + app.getEmployee().getLastName());
            	personalData.put("desgname", app.getEmpdesid().getValue());
            	personalData.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
            	personalData.put("appcycname", app.getAppcycle()!=null?app.getAppcycle().getCyclename():"");
            	personalData.put("startdate", app.getAppcycle().getStartdate().toString());
            	personalData.put("enddate", app.getAppcycle().getEnddate().toString());
            	personalData.put("newDept", app.getNewdepartment() != null ? app.getNewdepartment().getValue() : "");
            	personalData.put("newDesig", app.getNewdesignation() != null ? app.getNewdesignation().getValue() : "");
            	personalData.put("salInc", app.getSalaryincrement()+"");
            	personalData.put("performance", app.getPerformance()!=null?app.getPerformance().getValue():"");
            	personalData.put("empcomment", app.getManagercomment()!=null?app.getManagercomment():"");
            	personalData.put("isMyAppraisal", isMyAppraisal.toString());
            	personalData.put("selfcomment", app.getEmployeecomment()!=null?app.getEmployeecomment():"");
            }
            
            //Comp
            int cnt=1;
            JSONObject tmpObj = null;
            JSONArray jarrComp = new JSONArray();
            List<Appraisal> appraisalsList = new ArrayList<Appraisal>();
            Iterator<Appraisal> appraisalsItr;
            filter_names.clear();
            filter_values.clear();
            requestParams.clear();
            filter_names.add("appraisal.appraisalid");
            filter_values.add(request.getParameter("appraisalid"));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            
            appraisalsList = hrmsAppraisalDAOObj.getAppraisal(requestParams).getEntityList();
            if(!appraisalsList.isEmpty()){
                requestParams.clear();
                requestParams.put("appid", request.getParameter("appraisalid"));
                result = hrmsAppraisalDAOObj.getAppraisalforAppCyc(requestParams);
                int appcnt = result.getRecordTotalCount();

                requestParams.clear();
                requestParams.put("primary", true);
                requestParams.put("id", request.getParameter("appraisalid"));
                result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                Appraisalmanagement appmanage = (Appraisalmanagement) result.getEntityList().iterator().next();
                appraisalsItr = appraisalsList.iterator();
                while (appraisalsItr.hasNext()) {
                    Appraisal appraisal = (Appraisal) appraisalsItr.next();
                    if (appraisal.getCompetency() != null) {
                    	tmpObj = new JSONObject();
                        tmpObj.put("cmptid", appraisal.getCompetency().getMid());
                        tmpObj.put("mid", appraisal.getCompetency().getMid());
                        tmpObj.put("compid", appraisal.getAppid());
                        tmpObj.put("cmptname", appraisal.getCompetency().getMastercmpt().getCmptname());
                        tmpObj.put("cmptnametemp", "<span style='margin-left:30px;font-size:1.1em;'>" + cnt + ". " + appraisal.getCompetency().getMastercmpt().getCmptname() +"</span><br/>");
                        requestParams.clear();
                        requestParams.put("checklink", "weightage");
                        requestParams.put("companyid", companyid);
                        if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                            tmpObj.put("cmptwt", appraisal.getCompetency().getWeightage());
                        } else {
                            ans =  (100 / Double.parseDouble("" + appcnt));
                            tmpObj.put("cmptwt", ans);
                        }
                        tmpObj.put("cmptdesc", appraisal.getCompetency().getMastercmpt().getCmptdesc());
                        if (appmanage.getManagerdraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("manrat", "");
                            tmpObj.put("mangap", "");
                            tmpObj.put("mancompcomment", "");
                        } else if (appmanage.getManagerdraft() == 0 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("manrat", (int)appraisal.getCompmanrating());
                            tmpObj.put("mangap", appraisal.getCompmangap());
                            tmpObj.put("mancompcomment", appraisal.getCompmancomment());
                        } else {
                            tmpObj.put("manrat", (int)appraisal.getCompmanrating());
                            tmpObj.put("mangap", appraisal.getCompmangap());
                            tmpObj.put("mancompcomment", appraisal.getCompmancomment());
                        }
                        if (appmanage.getEmployeedraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("emprat", (int)appraisal.getCompemprating());
                            tmpObj.put("empgap", appraisal.getCompempgap());
                            tmpObj.put("empcompcomment", appraisal.getCompempcomment());
                        } else if (appmanage.getEmployeedraft() == 0 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("emprat", (int)appraisal.getCompemprating());
                            tmpObj.put("empgap", appraisal.getCompempgap());
                            tmpObj.put("empcompcomment", appraisal.getCompempcomment());
                        } else {
                            tmpObj.put("emprat", "");
                            tmpObj.put("empgap", "");
                            tmpObj.put("empcompcomment", "");
                        }
                        jarrComp.put(tmpObj);
                    }
                    cnt++;
                }
            }else {
            	List<Managecmpt> managecmptsList;
            	Iterator<Managecmpt> managecmptsItr;
                requestParams.clear();
                filter_names.clear();
                filter_values.clear();
                filter_names.add("desig.id");
                filter_values.add(request.getParameter("desigId"));
                filter_names.add("delflag");
                filter_values.add(0);
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);

                result = hrmsCompetencyDAOObj.getManagecmpt(requestParams);
                int appcnt = result.getRecordTotalCount();
                managecmptsList = result.getEntityList();

                managecmptsItr = managecmptsList.iterator();
                while (managecmptsItr.hasNext()) {
                    Managecmpt log = (Managecmpt) managecmptsItr.next();
                    tmpObj = new JSONObject();
                    tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                    tmpObj.put("mid", log.getMid());
                    tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
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
                    jarrComp.put(tmpObj);
                    cnt++;
                }
            }
            //Comp
            
            //Questions
            boolean isEmployee = false;
            boolean flg = false;
            String reviewerdesignationid = "";
            List<CompetencyDesMap> competencyDesMapsList = null;
            Iterator<CompetencyDesMap> competencyDesMapsItr = null;
            List<CompetencyQuestion> competencyQuestionsList = null;
            
            int totalcount = 0;
            JSONArray jarrQueTemp = new JSONArray();
            JSONArray jarrQue = new JSONArray();
            
            filter_names.clear();
            filter_values.clear();
            requestParams.clear();
            Appraisalmanagement appraisalmanagement = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("employee"))){
            	isEmployee = Boolean.parseBoolean(request.getParameter("employee"));
            	requestParams.clear();
                filter_names.add("appcycle.id");
                filter_values.add(request.getParameter("appraisalcycid"));
                filter_names.add("employee.userID");
                if(isEmployee)
                	filter_values.add(sessionHandlerImplObj.getUserid(request));
                else
                	filter_values.add(request.getParameter("uid"));
                if(!isEmployee){
                    flg = true;
                    filter_names.add("manager.userID");
                    filter_values.add(request.getParameter("managerid"));
                }
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                List<Appraisalmanagement> appraisalmanagements = hrmsAppraisalDAOObj.getAppraisalmanagements(requestParams);
                if(appraisalmanagements.size()>0)
                	appraisalmanagement = appraisalmanagements.get(0);
                if(appraisalmanagement!=null && !isEmployee)
                      if(appraisalmanagement.getManagerdesid()!=null){
                	reviewerdesignationid = appraisalmanagement.getManagerdesid().getId();
                      }  
                }
            requestParams.clear();
            if(appraisalmanagement!=null)
            	requestParams.put("desigid", appraisalmanagement.getEmpdesid().getId());
            else
            	requestParams.put("desigid", request.getParameter("desigId"));
            result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
            competencyDesMapsList = result.getEntityList();
            competencyDesMapsItr = competencyDesMapsList.iterator();
            while(competencyDesMapsItr.hasNext()){
                CompetencyDesMap log = competencyDesMapsItr.next();
                requestParams.clear();
                String groupid = log.getGroupid();
                boolean isEmp = flg;
                String designationid = reviewerdesignationid;
                String empdesignationid = appraisalmanagement!=null?appraisalmanagement.getEmpdesid().getId():request.getParameter("desigId");
                competencyQuestionsList = hrmsCompetencyDAOObj.getQuestionGroupMysql(groupid, isEmp, designationid, empdesignationid, appraisalmanagement);
                if(competencyQuestionsList!=null){
                for(CompetencyQuestion log1: competencyQuestionsList){

                    String revdesid = "";
                    int questionorder = 0;
                    totalcount++;
                    questionorder = log1.getQuesorder();
                    tmpObj = new JSONObject();
                    String questionid = log1.getQuesid();
                    tmpObj.put("qdescription", questionid);
                    tmpObj.put("qdesc", log1.getQuesdesc());
                    tmpObj.put("qans", log1.getNoofans());
                    tmpObj.put("qorder", log1.getQuesorder());
                    tmpObj.put("qtype", StringUtil.isNullOrEmpty(log1.getQuestype())?"100":log1.getQuestype());
                    requestParams.clear();
                    requestParams.put("qid", questionid);
                    requestParams.put("desid", empdesignationid);
                    revdesid = hrmsCompetencyDAOObj.getReviewerDesignation(requestParams);
                    if(flg){
                        if(revdesid==null){
                            tmpObj.put("qdes", "");
                            jarrQueTemp.put(questionorder-1,tmpObj);
                        } else {
                            if(revdesid.equals(reviewerdesignationid)){
                                tmpObj.put("qdes", revdesid);
                                jarrQueTemp.put(questionorder-1,tmpObj);
                            } else {
                                totalcount--;
                            }
                        }
                    } else {
                        if(revdesid!=null){
                            tmpObj.put("qdes", revdesid);
                        } else {
                            tmpObj.put("qdes", "");
                        }
                        jarrQueTemp.put(questionorder-1,tmpObj);
                    }
                }
                }
            }
            JSONArray jArr1 =  new JSONArray();
            for(int i =0;i<jarrQueTemp.length();i++) {
                if(!jarrQueTemp.isNull(i)) {
                	jarrQue.put(jarrQueTemp.get(i));
                }
            }
            //Questions
            
            //QueAns
            List<HashMap<String, String>> queAnsList = null;
            filter_names.clear();
            filter_values.clear();
            requestParams.clear();
            
            Appraisalmanagement log = (Appraisalmanagement) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalmanagement", request.getParameter("appraisalid"));
            String appCycleID = request.getParameter(hrmsAnonymousAppraisalConstants.appraisalcycid);
            String userID = null,reviewerid=null;
            if (Boolean.parseBoolean(request.getParameter("employee"))) {
                userID = sessionHandlerImplObj.getUserid(request);
                reviewerid =  userID;
            } else {
                userID = log.getEmployee().getUserID();
                reviewerid = log.getManager().getUserID();
            }

            filter_names.add(hrmsAnonymousAppraisalConstants.appcycleId);
            filter_values.add(appCycleID);
            filter_names.add(hrmsAnonymousAppraisalConstants.employeeUserID);
            filter_values.add(userID);
            filter_names.add(hrmsAnonymousAppraisalConstants.managerUserID);
            filter_values.add(reviewerid);
            filter_names.add(BuildCriteria.OPERATORORDER+hrmsAnonymousAppraisalConstants.cmptquestionOrder);
            filter_values.add(BuildCriteria.OPERATORORDERASC);
            requestParams.put(hrmsAnonymousAppraisalConstants.filter_names, filter_names);
            requestParams.put(hrmsAnonymousAppraisalConstants.filter_params, filter_values);

            result = hrmsAppraisalcycleserviceobj.getQuestionAnswerForm(requestParams);
            queAnsList = result.getEntityList();
            JSONArray jarrQueAns = new JSONArray();
            if (!queAnsList.isEmpty()) {

                HashMap<String, String> jsonmap = queAnsList.get(0);
                HashMap<String, String> questionjsonmap = queAnsList.get(1);
                if (questionjsonmap.size() > 0) {
                    for (String key : questionjsonmap.keySet()) {
                        JSONObject appObj = new JSONObject();
                        Integer size = Integer.parseInt(questionjsonmap.get(key));
                        appObj.put(hrmsAnonymousAppraisalConstants.question, key);
                        JSONArray ansArr = new JSONArray();
                        JSONObject map = null;
                        for(int i=0;i<size;i++){
                        	map = new JSONObject();
                        	map.put(i+"",jsonmap.get(key+i));
                        	ansArr.put(map);
                        }
                        appObj.put(hrmsAnonymousAppraisalConstants.answer, ansArr);
                        appObj.put("count",size);
                        jarrQueAns.put(appObj);
                    }
                }
            }
            //QueAns
            
            //Goals
            JSONArray jarrGoal = new JSONArray();
        	try{
        		if(StringUtil.isNullOrEmpty(request.getParameter("employee"))){
            		filter_names.clear();
                    filter_values.clear();
                    requestParams.clear();
                    filter_names.add("appraisal.appraisalid");
                    filter_values.add(request.getParameter("appraisalid"));	
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    appraisalsList = hrmsAppraisalDAOObj.getAppraisal(requestParams).getEntityList();
                    Iterator<Appraisal> ite = appraisalsList.iterator();
                    if (!appraisalsList.isEmpty()) {
                        while (ite.hasNext()) {
                            Appraisal appraisal = (Appraisal) ite.next();
                            if (appraisal.getGoal() != null) {
                                tmpObj = new JSONObject();
                                tmpObj.put("gid", appraisal.getGoal().getId());
                                tmpObj.put("goalid", appraisal.getAppid());
                                tmpObj.put("gname", appraisal.getGoal().getGoalname());
                                tmpObj.put("gwth", appraisal.getGoal().getGoalwth());
                                tmpObj.put("gcomplete", (appraisal.getGoal().getPercentcomplete())==null?0:appraisal.getGoal().getPercentcomplete());
                                tmpObj.put("assignedby", sessionHandlerImplObj.getUserFullName(appraisal.getGoal().getManager()));
                                tmpObj.put("gmanrat", (int)appraisal.getGoalmanrating());
                                tmpObj.put("mangoalcomment", appraisal.getGoalmancomment());                           
                                tmpObj.put("gemprat", (int)appraisal.getGoalemprating());
                                tmpObj.put("empgoalcomment", appraisal.getGoalempcomment());
                                jarrGoal.put(tmpObj);
                            }
                        }
                    }else {
                        String managerid;
                        Date sdate=null;
                        Date edate=null;
                        if(StringUtil.isNullOrEmpty("managerid")){
                            managerid=AuthHandler.getUserid(request);
                        } else{
                            managerid=request.getParameter("managerid");
                        }
                        Appraisalcycle appcycle = (Appraisalcycle) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalcycle", request.getParameter("appraisalcycid"));
                        if(!StringUtil.isNullOrEmpty("appraisalcycid")){
                            if (appcycle != null) {
                                sdate = appcycle.getSubmitstartdate();
                                edate = appcycle.getSubmitenddate();
                            }
                        }
                        requestParams.clear();
                        requestParams.put("userID", userID);
                        requestParams.put("companyid", companyid);
                        requestParams.put("archivedflag", false);
                        requestParams.put("managerid", managerid);
                        requestParams.put("sdate", sdate);
                        requestParams.put("edate", edate);
                        List<Finalgoalmanagement>tabledata = hrmsGoalDAOObj.getGoals(requestParams);
                        Iterator<Finalgoalmanagement> iteFg = tabledata.iterator();
                        while (iteFg.hasNext()) {
                            Finalgoalmanagement fgmt = (Finalgoalmanagement) iteFg.next();
                            tmpObj = new JSONObject();
                            tmpObj.put("gid", fgmt.getId());
                            tmpObj.put("gname", fgmt.getGoalname());
                            tmpObj.put("gwth", fgmt.getGoalwth());
                            tmpObj.put("gcomplete", (fgmt.getPercentcomplete()==null)?0:fgmt.getPercentcomplete());
                            tmpObj.put("internal", fgmt.isInternal());
                            jarrGoal.put(tmpObj);
                        }
                    }
            	}else{
            		String[] appids=request.getParameter("appraisalids").split(",");
                    for(int i=0;i<appids.length;i++){
                    	filter_names.clear();
                        filter_values.clear();
                        requestParams.clear();
                        filter_names.add("appraisal.appraisalid");
                        filter_values.add(appids[i]);	
                        requestParams.put("filter_names", filter_names);
                        requestParams.put("filter_values", filter_values);
                        appraisalsList = hrmsAppraisalDAOObj.getAppraisal(requestParams).getEntityList();
                        Iterator<Appraisal> ite = appraisalsList.iterator();
                        if(!appraisalsList.isEmpty()){
                        	while (ite.hasNext()) {
                        		Appraisal appraisal = (Appraisal) ite.next();
                        		if (appraisal.getGoal() != null) {
                        			tmpObj = new JSONObject();
                        			tmpObj.put("gid", appraisal.getGoal().getId());
                        			tmpObj.put("goalid", appraisal.getAppid());
                        			tmpObj.put("gname", appraisal.getGoal().getGoalname());
                        			tmpObj.put("gwth", appraisal.getGoal().getGoalwth());
                        			tmpObj.put("gcomplete", (appraisal.getGoal().getPercentcomplete()==null)?0:appraisal.getGoal().getPercentcomplete());
                        			tmpObj.put("gemprat", (int)appraisal.getGoalemprating());
                        			tmpObj.put("gmanrat", (int)appraisal.getGoalmanrating());
                        			tmpObj.put("assignedby", sessionHandlerImplObj.getUserFullName(appraisal.getGoal().getManager()));
                        			tmpObj.put("mangoalcomment", appraisal.getGoalmancomment());
                        			tmpObj.put("empgoalcomment", appraisal.getGoalempcomment());
                        			tmpObj.put("goalapprid", appids[i]);
                        			jarrGoal.put(tmpObj);
                        		}
                        	}
                        }else{
                              String managerid;
                              Date sdate=null;
                              Date edate=null;
                              Appraisalmanagement appr = (Appraisalmanagement) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalmanagement", appids[i]);
                              managerid=appr.getManager().getUserID();
                              Appraisalcycle appcycle = (Appraisalcycle) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalcycle", request.getParameter("appraisalcycid"));
                              if(!StringUtil.isNullOrEmpty("appraisalcycid")){
                            	  if (appcycle != null) {
                            		  sdate = appcycle.getSubmitstartdate();
                            		  edate = appcycle.getSubmitenddate();
                            	  }
                              }
                              requestParams.clear();
                              requestParams.put("userID", userID);
                              requestParams.put("companyid", companyid);
                              requestParams.put("archivedflag", false);
                              requestParams.put("managerid", managerid);
                              requestParams.put("sdate", sdate);
                              requestParams.put("edate", edate);
                              List<Finalgoalmanagement>tabledata = hrmsGoalDAOObj.getGoals(requestParams);
                              Iterator<Finalgoalmanagement> iteFg = tabledata.iterator();
                              while (iteFg.hasNext()) {
                            	  Finalgoalmanagement fgmt = (Finalgoalmanagement) iteFg.next();
                            	  tmpObj = new JSONObject();
                            	  tmpObj.put("gid", fgmt.getId());
                            	  tmpObj.put("gname", fgmt.getGoalname());
                            	  tmpObj.put("gwth", fgmt.getGoalwth());
                            	  tmpObj.put("gcomplete", (fgmt.getPercentcomplete()==null)?0:fgmt.getPercentcomplete());
                            	  tmpObj.put("assignedby", sessionHandlerImplObj.getUserFullName((User) fgmt.getManager()));
                            	  tmpObj.put("goalapprid", appids[i]);
                            	  jarrGoal.put(tmpObj);
                              }
                        }
                 	}
                }
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	//Goals
            
            jsonArr.put(jarrComp);
            jsonArr.put(jarrQue);
        	jsonArr.put(jarrQueAns);
            jsonArr.put(jarrGoal);
            if(isPrint)
            	printAppraisalReportDAOImp.createPrintPriviewFile(request, response, jsonArr,personalData);
            else
            	printAppraisalReportDAOImp.createPDFPriview(request, response, jsonArr,personalData);
            jsonResp.put("valid", true);
            jsonResp.put("data", jsonData.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jsonResp.toString());
    }
}
