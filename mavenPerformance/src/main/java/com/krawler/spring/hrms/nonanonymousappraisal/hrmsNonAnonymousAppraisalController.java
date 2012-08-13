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
package com.krawler.spring.hrms.nonanonymousappraisal;

import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.AppraisalQuestionAnswers;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.appraisal.hrmsAppraisalDAO;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.competency.hrmsCompetencyDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import org.springframework.web.servlet.support.RequestContextUtils;

public class hrmsNonAnonymousAppraisalController extends MultiActionController implements MessageSourceAware{

    private String successView;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
    private hrmsNonAnonymousAppraisalDAO hrmsNonAnonymousAppraisalDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImplObj;
    private MessageSource messageSource;

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
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

    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }

    public hrmsCompetencyDAO gethrmsCompetencyDAOObj() {
        return hrmsCompetencyDAOObj;
    }

    public void setHrmsCompetencyDAO(hrmsCompetencyDAO hrmsCompetencyDAOObj) {
        this.hrmsCompetencyDAOObj = hrmsCompetencyDAOObj;
    }

    public hrmsAppraisalDAO getHrmsAppraisalDAO() {
        return hrmsAppraisalDAOObj;
    }

    public void setHrmsAppraisalDAO(hrmsAppraisalDAO hrmsAppraisalDAOObj) {
        this.hrmsAppraisalDAOObj = hrmsAppraisalDAOObj;
    }

    public hrmsNonAnonymousAppraisalDAO getHrmsNonAnonymousAppraisalDAO() {
        return hrmsNonAnonymousAppraisalDAOObj;
    }

    public void setHrmsNonAnonymousAppraisalDAO(hrmsNonAnonymousAppraisalDAO hrmsNonAnonymousAppraisalDAOObj) {
        this.hrmsNonAnonymousAppraisalDAOObj = hrmsNonAnonymousAppraisalDAOObj;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
    
    
    public ModelAndView getfinalReportNonAnonymous(HttpServletRequest request, HttpServletResponse response){

        KwlReturnObject result = null;

        JSONObject jobjTemp = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        JSONObject jobjData;
        JSONObject empData;
        boolean flag = true;
        String records = "";
        JSONArray jarr = new JSONArray();
        JSONArray jarrColumns = new JSONArray();
        List list;
        int totalAppDone=0;
        int totalManagers=0;
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta =new JSONObject();
        JSONObject commData = new JSONObject();
        String hql = "";
        Iterator iter = null;
        String str;
        try {

            String companyid=sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            jobjTemp.put("header", messageSource.getMessage("hrms.performance.appraiser", null, RequestContextUtils.getLocale(request)));
            jobjTemp.put("tip", messageSource.getMessage("hrms.performance.appraiser", null, RequestContextUtils.getLocale(request)));
            jobjTemp.put("dataIndex", "appraiser");
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appraiser");
            jarrRecords.put(jobjTemp);
            String userID = request.getParameter("userid");
            /*
             * If user is viewing his own report then no userid is sent as the user combo is not present.
             * Only appraisal cycle combo is there and so only appriasal cycle id is sent in the request.
             */
            if(StringUtil.isNullOrEmpty(userID)){
                           userID = sessionHandlerImplObj.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
//            User user =(User) session.load(User.class, userID);
            requestParams.put("empid", userID);
            result=hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
            User user=(User) result.getEntityList().iterator().next();
            Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
            boolean compFlag = false;
//            if (hrmsManager.checkModule("competency", session, request)) { //Check for competency permission
                requestParams.clear();
                filter_names.add("appcycle.id");
                filter_values.add(appCycleID);
                filter_names.add("employee.userID");
                filter_values.add(userID);
                filter_names.add("reviewstatus");
                filter_values.add(2);
                
                requestParams.put("filter_names",filter_names);
                requestParams.put("filter_values",filter_values);

                result=hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                list=result.getEntityList();
                if(list.size()>0||Boolean.parseBoolean(request.getParameter("reviewappraisal"))) {
//                hql = "from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2";
//                list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID,userID});
                iter = list.iterator();
                if(iter.hasNext()){
                    Appraisalmanagement am = (Appraisalmanagement) iter.next();
                    try {
                        str = am.getOriginaldesignation().getId();
                    } catch (NullPointerException e) {
                        str = ua.getDesignationid().getId();
                    }
                }else{
                    str = (ua.getDesignationid() == null)?"":ua.getDesignationid().getId();
                }

        	List<AppraisalmanagementQuestionAnswers> list1 = hrmsNonAnonymousAppraisalDAOObj.getQuestions(appCycleID, userID);
        	if(list1!=null && list1.size()>0){
	        	for(AppraisalmanagementQuestionAnswers aqa: list1){
	                compFlag = true;
	                jobjTemp = new JSONObject();
	                String tStr = URLDecoder.decode(aqa.getCmptquestion().getQuesdesc(), "utf-8");
	                String compID = aqa.getCmptquestion().getQuesid();
	                jobjTemp.put("header", tStr);
	                jobjTemp.put("tip", tStr);
	                jobjTemp.put("dataIndex", compID);
	                jobjTemp.put("summaryType", "average");
	                jobjTemp.put("renderer", "function(val,meta,record){ return \" <div style='white-space:pre-wrap;' > "+
	                    "<span class='commentSpan' wtf:qtip='\" + record.data['"+compID+"'] + \"'> <font size='2'>\" + record.data['"+compID+"'] + \"</font> </span><br/></div>\";}");
	                jobjTemp.put("summaryRenderer", "function(val,meta,record){ return \"<span style='text-align: right;border:1px solid #eeeeee; float: left'><b>\" + val + \"</b></span>\";}");
	                jarrColumns.put(jobjTemp);
	                jobjTemp = new JSONObject();
	                jobjTemp.put("name", compID);
	                jarrRecords.put(jobjTemp);
	            }
        	}else{
            requestParams.clear();
            requestParams.put("checklink", "competency");
            requestParams.put("companyid",companyid);
            if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) { //Check for competency permission
                

                 requestParams.clear();
                 filter_names.clear();
                 filter_values.clear();
                filter_names.add("desig.id");
                filter_values.add(str);
                filter_names.add("delflag");
                filter_values.add(0);

                requestParams.put("filter_names",filter_names);
                requestParams.put("filter_values",filter_values);

                result=hrmsCompetencyDAOObj.getManagecmpt(requestParams);
                list=result.getEntityList();
//                hql = "from Managecmpt where desig.id=? and delflag=0";
//                list = HibernateUtil.executeQuery(session, hql, new Object[]{str});
                iter = list.iterator();
                while(iter.hasNext()){
                    compFlag = true;
                    Managecmpt managecmpt = (Managecmpt) iter.next();
                    jobjTemp = new JSONObject();
                    String tStr = managecmpt.getMastercmpt().getCmptname();
                    String compID = managecmpt.getMastercmpt().getCmptid();
                    jobjTemp.put("header", tStr);
                    jobjTemp.put("tip", tStr);
                    jobjTemp.put("dataIndex", compID);
                    jobjTemp.put("summaryType", "average");
//                    jobjTemp.put("renderer", "function(val,meta,record){ return \"<span class='valueSpan'><b>\" + val + \"</b></span>" +
//                        "<span class='commentSpan' wtf:qtip='\" + record.data['"+compID+"comment'] + \"'>\" + Wtf.util.Format.ellipsis(record.data['"+compID+"comment'], 15) + \"</span>\";}");
                    jobjTemp.put("renderer", "function(val,meta,record){ return \" <div style='white-space:pre-wrap;' > <span class='valueSpan'><b>"+messageSource.getMessage("hrms.performance.score", null, "Score", RequestContextUtils.getLocale(request))+": \" +  val + \"</b></span><br><br/>" +
                        "<span class='commentSpan' wtf:qtip='\" + record.data['"+compID+"comment'] + \"'>"+messageSource.getMessage("hrms.common.Comment", null, "Comment", RequestContextUtils.getLocale(request))+": \" + Wtf.util.Format.ellipsis(record.data['"+compID+"comment'], 15) + \"</span></div>\";}");
                    jobjTemp.put("summaryRenderer", "function(val,meta,record){ return \"<span style='text-align: right;border:1px solid #eeeeee; float: left'><b>\" + val + \"</b></span>\";}");
                    jarrColumns.put(jobjTemp);
                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", compID);
                    jarrRecords.put(jobjTemp);
                    jobjTemp = new JSONObject();
                    jobjTemp.put("header", tStr+" "+messageSource.getMessage("hrms.common.Comment", null, RequestContextUtils.getLocale(request)));
                    jobjTemp.put("dataIndex", compID+"comment");
                    jobjTemp.put("hidden", true);
                    jarrColumns.put(jobjTemp);
                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", compID+"comment");
                    jarrRecords.put(jobjTemp);
                }
            }

            }
            
            
            requestParams.clear();
            filter_names.clear();
            filter_values.clear();
            filter_names.add("appcycle.id");
            filter_values.add(appCycleID);
            filter_names.add("employee.userID");
            filter_values.add(userID);






            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
//                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ? and reviewstatus=0";
                filter_names.add("reviewstatus");
                filter_values.add(0);
            } //else{
//                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ?";
//            }
            requestParams.put("filter_names",filter_names);
            requestParams.put("filter_values",filter_values);

            result=hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            list=result.getEntityList();

            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
//            list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID,userID});
            iter = list.iterator();
            while(iter.hasNext()){
                totalManagers+=1;
                Appraisalmanagement appmgnt = (Appraisalmanagement) iter.next();
                ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", appmgnt.getEmployee().getUserID());
                if(appmgnt.getManagerstatus()==1){
                    totalAppDone+=1;
                    jobjData = new JSONObject();
                    empData = new JSONObject();
                    empData.put("appraiser", messageSource.getMessage("hrms.common.Updates.SelfAppraisal", null, RequestContextUtils.getLocale(request)));
                    jobjData.put("appraiser", appmgnt.getManager().getFirstName()+" "+appmgnt.getManager().getLastName());
                    if(appmgnt.getManagercomment() != null)
                        jobjData.put("mcomment", appmgnt.getManagercomment());
                    else
                        jobjData.put("mcomment", "");
                    empData.put("mcomment", appmgnt.getEmployeecomment());
                    if(appmgnt.getReviewercomment() != null)
                        jobjData.put("rcomment",appmgnt.getReviewercomment());
                    else
                        jobjData.put("rcomment","");
                    empData.put("rcomment", "<center>"+messageSource.getMessage("hrms.common.n.a", null, RequestContextUtils.getLocale(request))+"</center>");
                    jobjData.put("reviewStat", appmgnt.getReviewstatus());
//                    if (hrmsManager.checkModule("goal", session, request)) { //Check for goal permission
                    requestParams.put("checklink", "goal");
                    requestParams.put("companyid",companyid);
                    if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) { //Check for goal permission
                        jobjData.put("goalscore", appmgnt.getManagergoalscore());
                        empData.put("goalscore", appmgnt.getEmployeegoalscore());
                    }
                    jobjData.put("salaryinc", appmgnt.getSalaryincrement());
                    empData.put("salaryinc", "<center>"+messageSource.getMessage("hrms.common.n.a", null, RequestContextUtils.getLocale(request))+"</center>");
                    jobjData.put("eid", appmgnt.getEmployee().getUserID());
                    jobjData.put("ename", appmgnt.getEmployee().getFirstName() + " " + appmgnt.getEmployee().getLastName());
                    jobjData.put("desigid", (ua.getDesignationid() == null)?"":ua.getDesignationid().getId());
                    jobjData.put("desig", (ua.getDesignationid() == null)?"":ua.getDesignationid().getValue());
                    jobjData.put("department", (ua.getDepartment() == null)?"":ua.getDepartment().getValue());
                    jobjData.put("date", appmgnt.getManagersubmitdate()!=null?AuthHandler.getDateFormatter(request).format(appmgnt.getManagersubmitdate()):"");
                    jobjData.put("cscore", appmgnt.getManagercompscore());
                    jobjData.put("tscore", appmgnt.getTotalscore());
                    jobjData.put("gapscore", appmgnt.getManagergapscore());
                    empData.put("cscore", appmgnt.getEmployeecompscore());
                    empData.put("gapscore", appmgnt.getEmployeegapscore());
                    jobjData.put("fid", appmgnt.getAppraisalid());
                    jobjData.put("empcom", appmgnt.getEmployeecomment());
                    empData.put("performance", "<center>"+messageSource.getMessage("hrms.common.n.a", null, RequestContextUtils.getLocale(request))+"</center>");
                    empData.put("salaryinc", "<center>"+messageSource.getMessage("hrms.common.n.a", null, RequestContextUtils.getLocale(request))+"</center>");
                    if (appmgnt.getPerformance() != null) {
                        jobjData.put("prate", appmgnt.getPerformance().getId());
                        jobjData.put("performance", appmgnt.getPerformance().getValue());
                    } else {
                        jobjData.put("prate", messageSource.getMessage("hrms.common.none", null, RequestContextUtils.getLocale(request)));
                        jobjData.put("performance", "");
                    }
                    if (appmgnt.getAppcycle() != null) {
                        jobjData.put("apptype", appmgnt.getAppcycle().getCyclename());
                    } else{
                        jobjData.put("apptype", "");
                    }
                    jobjData.put("status", appmgnt.getAppraisalstatus());
                    empData.put("reviewStat", appmgnt.getReviewstatus());
                    jobjData.put("salaryrecommend", appmgnt.getSalaryrecommend());
                    if (appmgnt.getReviewdesignation() != null) {
                        jobjData.put("newdesignation", appmgnt.getReviewdesignation().getId());
                        jobjData.put("newdesignationname", appmgnt.getReviewdesignation().getValue());
                    } else {
                        jobjData.put("newdesignation", "");
                        jobjData.put("newdesignationname", "");
                    }
                    if (appmgnt.getReviewdepartment() != null) {
                        jobjData.put("newdepartment", appmgnt.getReviewdepartment().getId());
                        jobjData.put("newdepartmentname", appmgnt.getReviewdepartment().getValue());
                    } else {
                        jobjData.put("newdepartment", "");
                        jobjData.put("newdepartmentname", "");
                    }
                    if(appmgnt.getAppcycle()!=null){
                        jobjData.put("appcycleid", appmgnt.getAppcycle().getId());
                        jobjData.put("appcyclename", appmgnt.getAppcycle().getCyclename());
                        jobjData.put("appcyclesdate", appmgnt.getAppcycle().getStartdate());
                        jobjData.put("appcycleedate", appmgnt.getAppcycle().getEnddate());
                    }
                    
                    if(list1!=null && list1.size()>0){
                    	List<AppraisalmanagementQuestionAnswers> list2 = hrmsNonAnonymousAppraisalDAOObj.getQuestionsAnswers(appCycleID, userID);
                    	for(AppraisalmanagementQuestionAnswers aqa : list2){
                    		String answer = "";
                    		if(appmgnt.getManager().getUserID().equals(aqa.getManager().getUserID())){
                    			int count=0;
                    			for(AppraisalQuestionAnswers obj: aqa.getAppraisalquestionanswers()){
                    				answer+=(++count)+"."+obj.getAnswer()+"<br>";
                    			}
                        		jobjData.put(aqa.getCmptquestion().getQuesid(), URLDecoder.decode(answer, "utf-8"));
                    		}else{
                    			if(appmgnt.getEmployee().getUserID().equals(aqa.getManager().getUserID())){
                    				int count=0;
                    				for(AppraisalQuestionAnswers obj: aqa.getAppraisalquestionanswers()){
	                        			answer+=(++count)+"."+obj.getAnswer()+"<br>";
	                    			}
	                                empData.put(aqa.getCmptquestion().getQuesid(),  URLDecoder.decode(answer, "utf-8"));
                    			}
                    		}
                        }
                    }else{
                    	
                    if(compFlag) {//Check if competency
//                        Appraisalmanagement apprmgt = (Appraisalmanagement) session.load(Appraisalmanagement.class, appmgnt.getAppraisalid());

                        requestParams.clear();
                        requestParams.put("primary",true);
                        requestParams.put("id",appmgnt.getAppraisalid());

                        result=hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                        Appraisalmanagement apprmgt=(Appraisalmanagement) result.getEntityList().iterator().next();

                        requestParams.clear();
                        filter_names.clear();
                        filter_values.clear();
                        
                        filter_names.add("appraisal.appraisalid");
                        filter_values.add(appmgnt.getAppraisalid());
                        
                        requestParams.put("filter_names",filter_names);
                        requestParams.put("filter_values",filter_values);
                        
                        result=hrmsAppraisalDAOObj.getAppraisal(requestParams);
                        List lst=result.getEntityList();

//                        hql = "from Appraisal where appraisal.appraisalid = ?";
//                        List lst = HibernateUtil.executeQuery(session, hql, new Object[]{appmgnt.getAppraisalid()});
                        Iterator ite = lst.iterator();
                        int totComp = 0;
                        while(ite.hasNext()){
                            totComp++;
                            Appraisal app = (Appraisal) ite.next();
                            if(app.getCompetency()!=null){
                                if(flag)
                                    records += app.getCompetency().getMastercmpt().getCmptid()+",";
                                jobjData.put(app.getCompetency().getMastercmpt().getCmptid(), app.getCompmanrating());
                                jobjData.put(app.getCompetency().getMastercmpt().getCmptid()+"comment", app.getCompmancomment());
                                empData.put(app.getCompetency().getMastercmpt().getCmptid(), app.getCompemprating());
                                empData.put(app.getCompetency().getMastercmpt().getCmptid()+"comment", app.getCompempcomment());
                            }
                        }
                        flag = false;
                        jobjData.put("compavg", df.format(apprmgt.getManagercompscore()));
                        empData.put("compavg", df.format(apprmgt.getEmployeecompscore()));
                    }
                    
                    if(compFlag) {//Check if competency
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", messageSource.getMessage("hrms.performance.competency.score", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip", messageSource.getMessage("hrms.performance.competency.score", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("dataIndex", "cscore");
                        jobjTemp.put("summaryType", "average");
                        jobjTemp.put("align", "right");
                        jobjTemp.put("renderer", "function(val,meta,record){ return \"<b>\" + val + \"</b>\";}");
                        jarrColumns.put(jobjTemp);
                        jobjTemp = new JSONObject();
                        jobjTemp.put("name", "cscore");
                        jarrRecords.put(jobjTemp);
                    }
                    }
                    jobjData.put("totalAppDone", totalAppDone);
                    jobjData.put("totalManagers", totalManagers);
                    jobjData.put("records", records.substring(0, Math.max(0, records.length()-1)));
//                    if(jarr.length()<=0 && hrmsManager.checkModule("appraisal", session, request)){
                    requestParams.clear();
                    requestParams.put("checklink", "appraisal");
                    requestParams.put("companyid",companyid);
                    if(jarr.length()<=0 && hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
                        jarr.put(empData);
                    }
                    jarr.put(jobjData);
                }
            }

            
//            if (hrmsManager.checkModule("goal", session, request)) { //Check for goal permission
            /*requestParams.clear();
            requestParams.put("checklink", "appraisal");
            requestParams.put("companyid",companyid);
            if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) { *///Check for goal permission
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("hrms.performance.goal.score", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("hrms.performance.goal.score", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("dataIndex", "goalscore");
                jobjTemp.put("summaryType", "average");
                jobjTemp.put("align", "right");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "goalscore");
                jarrRecords.put(jobjTemp);
            /*}*/
            requestParams.clear();
            requestParams.put("cmpsubscription", AuthHandler.getCmpSubscription(request));
            requestParams.put("module",hrms_Modules.payroll);
//            if(PermissionHandler.isSubscribed(hrms_Modules.payroll, AuthHandler.getCmpSubscription(request))){
            if(hrmsCommonDAOObj.isSubscribed(requestParams).isSuccessFlag()){
                jobjData = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("hrms.performance.salary.increment", null, RequestContextUtils.getLocale(request))+" (%)");
                jobjTemp.put("tip", messageSource.getMessage("hrms.performance.salary.increment", null, RequestContextUtils.getLocale(request))+" (%)");
                jobjTemp.put("dataIndex", "salaryinc");
                jobjTemp.put("align", "right");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salaryinc");
                jarrRecords.put(jobjTemp);
            }

            requestParams.clear();
            requestParams.put("checklink", "promotion");
            requestParams.put("companyid",companyid);
            if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("hrms.performance.performance.rating", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("hrms.performance.performance.rating", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("dataIndex", "performance");
                jobjTemp.put("renderer", "function(val){ return \"<div wtf:qtip='\"+val+\"'>\"+val+\"</div>\"}");
                jarrColumns.put(jobjTemp);
            }

            jobjTemp = new JSONObject();
            jobjTemp.put("name", "performance");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("header", messageSource.getMessage("hrms.masterconf.OverallComment", null, RequestContextUtils.getLocale(request)));
            jobjTemp.put("tip", messageSource.getMessage("hrms.masterconf.OverallComment", null, RequestContextUtils.getLocale(request)));
            jobjTemp.put("dataIndex", "mcomment");
            jobjTemp.put("renderer", "function(val){ return \"<div style='white-space:pre-wrap;' wtf:qtip='\"+val+\"'>\"+val+\"</div>\"}");
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "mcomment");
            jarrRecords.put(jobjTemp);
            requestParams.clear();
            requestParams.put("checklink", "approveAppWin");
            requestParams.put("companyid",companyid);
            if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()&&!Boolean.parseBoolean(request.getParameter("reviewappraisal"))){
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("hrms.performance.reviewer.comment", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("hrms.performance.reviewer.comment", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("dataIndex", "rcomment");
                jobjTemp.put("renderer", "function(val){ return \"<div wtf:qtip='\"+val+\"'>\"+val+\"</div>\"}");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "rcomment");
                jarrRecords.put(jobjTemp);
            }

            jobjTemp = new JSONObject();
            jobjTemp.put("header", messageSource.getMessage("hrms.performance.review.status", null, RequestContextUtils.getLocale(request)));
            jobjTemp.put("tip", messageSource.getMessage("hrms.performance.review.status", null, RequestContextUtils.getLocale(request)));
            jobjTemp.put("dataIndex", "reviewStat");
            jobjTemp.put("renderer", "function(val){"+
                                                    "if(val=='0')"+
                                                        "return '<FONT COLOR=\"blue\">"+messageSource.getMessage("hrms.recruitment.pending", null, RequestContextUtils.getLocale(request))+"</FONT>';"+
                                                    "else if(val=='1')"+
                                                        "return '<FONT COLOR=\"red\">"+messageSource.getMessage("hrms.common.Unapproved", null, RequestContextUtils.getLocale(request))+"</FONT>';"+
                                                    "else if(val=='2')"+
                                                        "return '<FONT COLOR=\"green\">"+messageSource.getMessage("hrms.common.Completed", null, RequestContextUtils.getLocale(request))+"</FONT>';"+
                                                    "}"
                        );
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "reviewStat");
            jarrRecords.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("header", "totalManagers");
            jobjTemp.put("dataIndex", "totalManagers");
            jobjTemp.put("hidden", true);
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "totalManagers");
            jobjTemp.put("type", "int");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("header", "totalAppDone");
            jobjTemp.put("dataIndex", "totalAppDone");
            jobjTemp.put("hidden", true);
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "totalAppDone");
            jobjTemp.put("type", "int");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "desigid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "eid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "fid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "ename");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "desig");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "department");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "date");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "empcom");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "compavg");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "gapscore");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "salaryrecommend");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "newdesignation");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "newdepartment");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "prate");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "apptype");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appcyclesdate");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appcycleedate");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appcycleid");
            jarrRecords.put(jobjTemp);
        
            commData.put("coldata", jarr);
            commData.put("columns", jarrColumns);
            jMeta.put("totalProperty", "totalCount");
            jMeta.put("root", "coldata");
            jMeta.put("fields", jarrRecords);
            commData.put("metaData", jMeta);
            requestParams.clear();
            requestParams.put("empid", userID);
            requestParams.put("cycleid", appCycleID);
            result = hrmsAppraisalcycleDAOObj.getAppraisalManagers(requestParams);

            List tmplist = result.getEntityList();
            commData.put("totalappraisal", tmplist.get(0));
            commData.put("appraisalsubmitted", tmplist.get(1));
//            commData=getAppraisalManagers(session,userID,appCycleID,commData);
            commData.put("success", true);
            } else {
                jarr = new JSONArray();
                jarrColumns = new JSONArray();
                commData = new JSONObject();
                commData.put("coldata", jarr);
                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                commData.put("metaData", jMeta);
            }

//            jobj.put("valid", true);
//            jobj.put("data", commData.toString());
            jobj1.put("valid", true);
            jobj1.put("data", commData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView reviewNonAnonymousAppraisal(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result=null;

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
            
            Locale locale = RequestContextUtils.getLocale(request);
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) { //To approve request
//                String hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and managerstatus=1";
//                List list = HibernateUtil.executeQuery(session, hql, new Object[]{employeeid, appcycleid});




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
//                    if (app.getReviewstatus() != 2) {
//                        User user = (User) session.load(User.class, employeeid);
                        requestParams.clear();
                        requestParams.put("empid", employeeid);
                        result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                        User user = (User) result.getEntityList().iterator().next();
                        Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
//                        if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
//                            app.setReviewdepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
//                        }

                        if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                            requestParams.clear();
                            requestParams.put("masterid", request.getParameter("department"));
                            result = hrmsCommonDAOObj.getMasterData(requestParams);
                            revdept = (MasterData) result.getEntityList().iterator().next();
                        }

//                        if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
//                            app.setReviewdesignation((MasterData) session.load(MasterData.class, request.getParameter("designation")));
//                        }

                        if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
                            requestParams.clear();
                            requestParams.put("masterid", request.getParameter("designation"));
                            result = hrmsCommonDAOObj.getMasterData(requestParams);
                            revdesig = (MasterData) result.getEntityList().iterator().next();
                        }
//                        if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
//                            app.setReviewsalaryincrement(Float.parseFloat(request.getParameter("salaryincrement")));
//                        } else {
//                            app.setReviewsalaryincrement(0);
//                        }

//                        app.setOriginaldesignation(user.getDesignationid());
//                        app.setOriginaldepartment(user.getDepartment());
//                        app.setReviewstatus(2);
//                        app.setAppraisalstatus("submitted");
//                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
//                            app.setReviewercomment(request.getParameter("reviewercomment"));
//                        }
//                        app.setEmployeestatus(1);
//                        app.setManagerstatus(1);
//                        app.setReviewersubmitdate(new Date());

//                        session.update(app);
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

//                    } else {
//                        jobj.put("success", true);
//                        jobj.put("msg", "Appraisal is already approved ");
//                        return jobj.toString();
//                    }
                    requestParams.put("locale", locale);
                    result = hrmsNonAnonymousAppraisalDAOObj.reviewNonAnonymousAppraisalReport(requestParams);
                }
            } else { //To unapprove the request
                String[] appraisalids = request.getParameterValues("appraisalids");
                for (int i = 0; i < appraisalids.length; i++) {
//                    Appraisalmanagement app = (Appraisalmanagement) session.load(Appraisalmanagement.class, appraisalids[i]);
//                    if (app.getReviewstatus() != 2) {
//                        app.setManagerstatus(0);
//                        app.setReviewstatus(1);
//                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
//                            app.setReviewercomment(request.getParameter("reviewercomment"));
//                        }
//                        app.setAppraisalstatus("pending");
//                    } else {
//                        jobj.put("success", true);
//                        jobj.put("msg", "Appraisal has been already approved ");
//                        return jobj.toString();
//                    }
                    requestParams.clear();
                    requestParams.put("primary", true);
                    requestParams.put("id", appraisalids[i]);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                    Appraisalmanagement app = (Appraisalmanagement) result.getEntityList().iterator().next();
                    requestParams.clear();
                    requestParams.put("appmgmt", app);
                    requestParams.put("approve", request.getParameter("reviewstatus"));
                    requestParams.put("mgrstat", 0);
                    requestParams.put("revstat", 1);
                    if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                        requestParams.put("revcomment", request.getParameter("reviewercomment"));
                    }
                    requestParams.put("apprstat", "pending");
                    requestParams.put("locale", locale);
                    result = hrmsNonAnonymousAppraisalDAOObj.reviewNonAnonymousAppraisalReport(requestParams);

                }
            }
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) { //Update user information
//                User user = (User) session.load(User.class, employeeid);
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
//                user.setSalary("" + salary);
//                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
//                    user.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
//                }
//                if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
//                    user.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designation")));
//                }
//                session.update(user);

                if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
//                    requestParams.clear();
//                    requestParams.put("masterid", request.getParameter("designation"));
//                    result = hrmsCommonDAOObj.getMasterData(requestParams);
//                    revdesig = (MasterData) result.getEntityList().iterator().next();
                    revdesig = (MasterData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.master.MasterData", request.getParameter("designation"));
                    if (revdesig != ua.getDesignationid()) {
                        histdesig = (ua.getDesignationid() == null)?"":ua.getDesignationid().getId();
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
                        histdept = (ua.getDepartment() == null)?"":ua.getDepartment().getId();
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
                
                requestParams.put("locale", locale);
                result = hrmsNonAnonymousAppraisalDAOObj.changeSalaryonReview(requestParams);
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
//            jobj.put("success", true);
//            jobj.put("msg", "Appraisal status changed successfully");
//            return jobj.toString();
            jobj.put("success", result.isSuccessFlag());
            jobj.put("msg", messageSource.getMessage("hrms.performance.Appraisalisapprovedandemployeehistoryaddedsuccessfully", null, RequestContextUtils.getLocale(request)));
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
