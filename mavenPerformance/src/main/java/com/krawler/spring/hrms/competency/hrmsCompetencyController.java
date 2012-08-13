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
package com.krawler.spring.hrms.competency;

import com.krawler.common.admin.Useraccount;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.CompetencyDesMap;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.GoalComments;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.hrms.performance.Mastercmpt;
import com.krawler.hrms.performance.QuestionGroup;
import com.krawler.hrms.performance.ReviewerQuestionMap;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.spring.hrms.appraisal.hrmsAppraisalDAO;
import com.krawler.spring.hrms.appraisalcycle.bizservice.hrmsAppraisalcycleservice;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONObject;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
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

public class hrmsCompetencyController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsAppraisalDAO hrmsAppraisalDAOObj;
     private MessageSource messageSource;

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }
     public void setHrmsAppraisalcycleservice(hrmsAppraisalcycleservice hrmsAppraisalcycleserviceobj) {
        this.hrmsAppraisalcycleserviceobj = hrmsAppraisalcycleserviceobj;
    }

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

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public hrmsCompetencyDAO getHrmsCompetencyDAOObj() {
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

    public ModelAndView getCompAndDesig(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int cnt = 0;
        Integer start = 0;
        Integer limit = 15;
        try {
            String ss = request.getParameter("ss");
//            ArrayList params = new ArrayList();
//            params.add(AuthHandler.getCompanyid(request));
//            String searchString = "";
//            if(!StringUtil.isNullOrEmpty(ss)){
//                StringUtil.insertParamSearchString(params, ss, 2);
//                searchString = StringUtil.getSearchString(ss, "and", new String[]{"desig.value", "mastercmpt.cmptname"});
//            }
//            String hql = "from Managecmpt where mastercmpt.company.companyID=? and delflag=0 "+searchString+" order by desig.value";
//            tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
//            count = tabledata.size();
//            list = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
//
//            cnt = list.size();
            if (request.getParameter("start") != null) {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            } 

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(),filter_values = new ArrayList();
            ArrayList order_by = new ArrayList(),order_type = new ArrayList();
            filter_names.add("mastercmpt.company.companyID");
            filter_values.add(sessionHandlerImplObj.getCompanyid(request));
            filter_names.add("delflag");
            filter_values.add(0);
            order_by.add("desig.value");
            order_type.add("asc");
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("ss", ss);
            requestParams.put("searchcol", new String[]{"desig.value", "mastercmpt.cmptname"});
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("allflag", false);

            result= hrmsCompetencyDAOObj.getManagecmpt(requestParams);
            list = result.getEntityList();
            count = result.getRecordTotalCount();
            cnt = list.size();
            for (int i = 0; i < cnt; i++) {
                Managecmpt log = (Managecmpt) list.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
                tmpObj.put("cmptwt", log.getWeightage());
                tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                tmpObj.put("desname", log.getDesig().getValue());
                tmpObj.put("mcompid", log.getMid());
                tmpObj.put("designid", log.getDesig().getId());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getCompetencyAvailable(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        try {
            String desigid = request.getParameter("desig");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("desigid", desigid);
            requestParams.put("delflag", 0);
            result = hrmsCompetencyDAOObj.getCompetencyAvailable(requestParams);
            List lst = result.getEntityList();
            count = result.getRecordTotalCount();
            JSONArray jArr = new JSONArray();
            for(int ctr=0;ctr<lst.size();ctr++){
                JSONObject tmpObj = new JSONObject();
                Mastercmpt log = (Mastercmpt) lst.get(ctr);
                tmpObj.put("cmptid", log.getCmptid());
                tmpObj.put("cmptname", log.getCmptname());
                tmpObj.put("cmptwt", log.getCmptwt());
                tmpObj.put("cmptdesc", log.getCmptdesc());
                jArr.put(tmpObj);
            }
            jobj.put("data", jArr);
            jobj.put("success", true);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getCompetencyAssigned(HttpServletRequest request,HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        try {
            String desigid = request.getParameter("desig");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("desigid", desigid);
            requestParams.put("delflag", 0);
            result = hrmsCompetencyDAOObj.getCompetencyAssigned(requestParams);
            List lst = result.getEntityList();
            count = result.getRecordTotalCount();
            JSONArray jArr = new JSONArray();
            for(int ctr=0;ctr<lst.size();ctr++){
                JSONObject tmpObj = new JSONObject();
                Managecmpt log = (Managecmpt) lst.get(ctr);
                tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
                tmpObj.put("cmptwt", log.getWeightage());
                tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                tmpObj.put("desname", log.getDesig().getValue());
                jArr.put(tmpObj);
            }
            jobj.put("data", jArr);
            jobj.put("success", true);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView assignCompetency(HttpServletRequest request,HttpServletResponse response){
        List tabledata = null;
        String[] comp = request.getParameterValues("item_ids");
        String[] wth=request.getParameterValues("wth");
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("desig.id");
            filter_values.add(request.getParameter("desid"));
            filter_names.add("mastercmpt.company.companyID");
            filter_values.add(sessionHandlerImplObj.getCompanyid(request));
            filter_names.add("delflag");
            filter_values.add(0);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = hrmsCompetencyDAOObj.getManagecmpt(requestParams);
            tabledata = result.getEntityList();
            for (int i = 0; i < tabledata.size(); i++) {
                Managecmpt log = (Managecmpt) tabledata.get(i);
                requestParams.clear();
                requestParams.put("mid", log.getMid());
                requestParams.put("delflag", 1);
                result = hrmsCompetencyDAOObj.addManagecmpt(requestParams);
                
            }
            for (int i = 0; i < comp.length; i++) {
                requestParams.clear();
                requestParams.put("mastercmpt", comp[i]);
                requestParams.put("desig", request.getParameter("desid"));
                requestParams.put("delflag", 0);
                requestParams.put("weightage", (StringUtil.isNullOrEmpty(wth[i])==false?Integer.parseInt(wth[i]):0));
                result = hrmsCompetencyDAOObj.addManagecmpt(requestParams);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.ASSIGN_COMPETENCY, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has assigned competency " + li1.getCmptname() + " to designation " + li2.getValue(),request);
                Managecmpt li = (Managecmpt) result.getEntityList().iterator().next();
                auditTrailDAOObj.insertAuditLog(AuditAction.ASSIGN_COMPETENCY, "User  " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has assigned competency " + li.getMastercmpt().getCmptname() + " to designation " + li.getDesig().getValue(), request, "0");
            }
            jobj.put("valid", true);
            jobj.put("data", "");
            txnManager.commit(status);

        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        } finally {
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }

    public ModelAndView getCompetency(HttpServletRequest request,HttpServletResponse response) {
        KwlReturnObject result = null;
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int cnt = 0;
        try {
            String ss = request.getParameter("ss");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("company.companyID");
            filter_values.add(sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("ss", ss);
            requestParams.put("searchcol", new String[]{"cmptname"});
            StringUtil.checkpaging(requestParams, request);
            result = hrmsCompetencyDAOObj.getMastercmpt(requestParams);
            tabledata = result.getEntityList();
            cnt = result.getRecordTotalCount();
            JSONArray jArr = new JSONArray();
            for (int i = 0; i < tabledata.size(); i++) {
                Mastercmpt log = (Mastercmpt) tabledata.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getCmptid());
                tmpObj.put("cmptname", log.getCmptname());
                tmpObj.put("cmptwt", log.getCmptwt());
                tmpObj.put("cmptdesc", log.getCmptdesc());
               // jobj.append("data", tmpObj);
                jarr.put(tmpObj);
            }
            jobj.put("data", jArr);
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", cnt);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public boolean checkaddCompetency(HashMap<String,Object> requestParams)  {
        boolean result = false;
        KwlReturnObject result1 = null;
        List tabledata = null;
        HttpServletRequest request = (HttpServletRequest)requestParams.get("request");
        String competency = request.getParameter("cmptname");
        boolean success = false;
        try {
            if (request.getParameter("cmptid").equals("null")) {
//                String hql = "from Mastercmpt where cmptname=? and company.companyID=?";
//                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{competency,sessionHandlerImplObj.getCompanyid(request)});
                requestParams.clear();
                ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
                filter_names.add("cmptname");
                filter_values.add(competency);
                filter_names.add("company.companyID");
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result1 = hrmsCompetencyDAOObj.getMastercmpt(requestParams);
                tabledata = result1.getEntityList();
            } else {
//                String hql = "from Mastercmpt where cmptname=? and cmptid != ? and company.companyID=?";
//                tabledata = HibernateUtil.executeQuery(session, hql, new Object[] {competency, request.getParameter("cmptid"),sessionHandlerImplObj.getCompanyid(request)});
                requestParams.clear();
                ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
                filter_names.add("cmptname");
                filter_values.add(competency);
                filter_names.add("!cmptid");
                filter_values.add(request.getParameter("cmptid"));
                filter_names.add("company.companyID");
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result1 = hrmsCompetencyDAOObj.getMastercmpt(requestParams);
                tabledata = result1.getEntityList();
            }
            if (!tabledata.isEmpty()) {
                result = true;
            }
            success = true;
        } catch (Exception ex) {
            success = false;
            ex.printStackTrace();
        }
        return result;
    }

    public ModelAndView addCompetency(HttpServletRequest request, HttpServletResponse response){
        Mastercmpt contact;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean flag;
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            if (request.getParameter("cmptid").equals("null")) {
                requestParams.put("request", request);
                flag = checkaddCompetency(requestParams);
                if (!flag) {
                    requestParams.clear();
                    requestParams.put("cmptname", request.getParameter("cmptname"));
                    requestParams.put("cmptdesc", request.getParameter("cmptdesc"));
                    requestParams.put("cmptwt", 0);
                    requestParams.put("company", sessionHandlerImplObj.getCompanyid(request));
                    hrmsCompetencyDAOObj.addMastercmpt(requestParams);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.COMPETENCY_ADDED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new competency " + contact.getCmptname(),request);
                    auditTrailDAOObj.insertAuditLog(AuditAction.COMPETENCY_ADDED, "User  " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new competency " + request.getParameter("cmptname"), request, "0");
                    jobj.put("message",true);
                } else {
                    jobj.put("message",false);
                }
            } else {
                requestParams.clear();
                requestParams.put("request", request);
                flag = checkaddCompetency(requestParams);
                if (!flag) {
                    requestParams.clear();
                    requestParams.put("cmptname", request.getParameter("cmptname"));
                    requestParams.put("cmptdesc", request.getParameter("cmptdesc"));
                    requestParams.put("cmptwt", 0);
                    requestParams.put("cmptid", request.getParameter("cmptid"));
                    hrmsCompetencyDAOObj.addMastercmpt(requestParams);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.COMPETENCY_EDITED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited competency " + contact.getCmptname(),request);
                    auditTrailDAOObj.insertAuditLog(AuditAction.COMPETENCY_EDITED, "User  " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited competency " + request.getParameter("cmptname"), request, "0");
                    jobj.put("message",true);
                } else {
                    jobj.put("message",false);
                }
            }
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
        return  new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView deleteCompetency(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        List tabledata;
        KwlReturnObject result = null;
        int flag = 0;
        String msgdelcmp="";
        String msgcmp="";
        String msg;
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String cmptids[] = request.getParameterValues("cmptid");
            for (int i = 0; i < cmptids.length; i++) {
                requestParams.put("cmptid", cmptids[i]);
                result = hrmsCompetencyDAOObj.getAppraisal(requestParams);
                tabledata = result.getEntityList();
                String cmpname="";
                if (tabledata.isEmpty()) {
                    if(flag==1) {
                        flag = 3;
                    }
                    else {
                        flag = 2;
                    }
                    requestParams.clear();
                    requestParams.put("cmptid", cmptids[i]);
                    result = hrmsCompetencyDAOObj.deleteMastercmpt(requestParams);
                    cmpname=result.getMsg();
                    msgdelcmp = cmpname+", ";
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.COMPETENCY_DELETED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted competency " + cmpname,request);
                    auditTrailDAOObj.insertAuditLog(AuditAction.COMPETENCY_DELETED, "User  " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has deleted competency " + cmpname, request, "0");
                } else {
                    Mastercmpt mcmpt = (Mastercmpt) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Mastercmpt",cmptids[i]);
                    msgcmp=mcmpt.getCmptname()+", ";
                    if(flag==2) {
                        flag=3;
                    } else {
                        flag=1;
                    }
                }
            }
//            jobj.put("message", flag);
            if(flag==1) {
                jobj.put("message", messageSource.getMessage("hrms.performance.Competenciescannotbedeleted",null,"Competencies cannot be deleted.", RequestContextUtils.getLocale(request)));
                jobj.put("success",false);
            } else if(flag==2) {
                jobj.put("message", messageSource.getMessage("hrms.performance.Competenciesaredeletedsuccessfully",null,"Competencies deleted successfully.", RequestContextUtils.getLocale(request)));
                jobj.put("success",true);
            } else if(flag==3) {
                msgdelcmp=msgdelcmp.substring(0, msgdelcmp.length()-2);
                msgcmp=msgcmp.substring(0, msgcmp.length()-2);
                jobj.put("message",messageSource.getMessage("hrms.performance.FollowingCompetenciesaredeleted",null,"Following Competencies are deleted", RequestContextUtils.getLocale(request))+": "+msgdelcmp+"<br>"+messageSource.getMessage("hrms.performance.Followingcompetenciescannotbedeleted",null,"Following competencies cannot be deleted", RequestContextUtils.getLocale(request))+": "+msgcmp);
                jobj.put("success",true);
            }
            if(result.isSuccessFlag())
                jobj1.put("valid", true);
            else
                jobj1.put("valid", false);
            jobj1.put("data", jobj.toString());

            txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView insertQuestion(HttpServletRequest request,HttpServletResponse response){
         KwlReturnObject result;
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        String groupID = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("desigid", request.getParameter("desigid"));
            result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
            if(result.getEntityList().size() > 0){
                CompetencyDesMap log = (CompetencyDesMap) result.getEntityList().get(0);
                groupID = log.getGroupid();
            }
            requestParams.clear();
            requestParams.put("desigid", request.getParameter("desigid"));
            requestParams.put("groupID", groupID);
            requestParams.put("jsondata", request.getParameter("jsondata"));
            result=hrmsCompetencyDAOObj.insertQuestion(requestParams);

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

    public ModelAndView getCompetencyQuestion(HttpServletRequest request,HttpServletResponse response) {
        KwlReturnObject result = null;
        List tabledata = null;
        List tabledata2 = null;
        List tabledata3 = null;
        boolean isEmployee = false;
        List<HashMap<String, String>> list = null;
        boolean flg = false;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        String reviewerdesignationid = "";
        int cnt = 0,cnt1 = 0,cnt2 = 0,totalcount = 0;
        Appraisalmanagement appraisalmanagement = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            if(!StringUtil.isNullOrEmpty(request.getParameter("employee"))){
            	isEmployee = Boolean.parseBoolean(request.getParameter("employee"));
            	requestParams.clear();
                List<String> filter_names = new ArrayList<String>();
                List<Object> filter_values = new ArrayList<Object>();
                filter_names.add("appcycle.id");
                filter_values.add(request.getParameter("appcycle"));
                filter_names.add("employee.userID");
                if(isEmployee)
                	filter_values.add(sessionHandlerImplObj.getUserid(request));
                else
                	filter_values.add(request.getParameter("uid"));
                if(!isEmployee){
                    flg = true;
                    filter_names.add("manager.userID");
                    filter_values.add(request.getParameter("manid"));
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
            	requestParams.put("desigid", request.getParameter("desig"));
            result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
            tabledata = result.getEntityList();
            count = result.getRecordTotalCount();
            cnt = tabledata.size();
            for (int i = 0; i < cnt; i++) {
                CompetencyDesMap log = (CompetencyDesMap) tabledata.get(i);
                requestParams.clear();
                String groupid = log.getGroupid();
                boolean isEmp = flg;
                String designationid = reviewerdesignationid;
                String empdesignationid = appraisalmanagement!=null?appraisalmanagement.getEmpdesid().getId():request.getParameter("desig");
                List<CompetencyQuestion> tabledata1 = hrmsCompetencyDAOObj.getQuestionGroupMysql(groupid, isEmp, designationid, empdesignationid, appraisalmanagement);
                if(tabledata1!=null){
                for(CompetencyQuestion log1: tabledata1){
                    String revdesid = "";
                    int questionorder = 0;
                    totalcount++;
                    questionorder = log1.getQuesorder();
                    JSONObject tmpObj = new JSONObject();
                    String questionid = log1.getQuesid();
                    tmpObj.put("qdescription", questionid);
                    tmpObj.put("qdesc", log1. getQuesdesc());
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
                            jArr.put(questionorder-1,tmpObj);
                        } else {
                            if(revdesid.equals(reviewerdesignationid)){
                                tmpObj.put("qdes", revdesid);
                                jArr.put(questionorder-1,tmpObj);
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
                        jArr.put(questionorder-1,tmpObj);
                    }
                    tmpObj.put("isVisible", log1.isVisible());
                }
                }
            }
            JSONArray jArr1 =  new JSONArray();
            for(int i =0;i<jArr.length();i++) {
                if(!jArr.isNull(i)) {
                    jArr1.put(jArr.get(i));
                }
            }
            jobj.put("data", jArr1);
            jobj.put("success", true);
            jobj.put("count", totalcount);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getCompetencyQuestion1(HttpServletRequest request,HttpServletResponse response) {
        KwlReturnObject result = null;
        List tabledata = null;
        List tabledata1 = null;
        List tabledata2 = null;
        List tabledata3 = null;
        boolean isEmployee = false;
        List<HashMap<String, String>> list = null;
        boolean flg = false;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        int cnt = 0,cnt1 = 0,cnt2 = 0,totalcount = 0;
        try {
//            String ss = request.getParameter("ss");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            requestParams.put("desigid", request.getParameter("desig"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("employee"))){
                isEmployee = Boolean.parseBoolean(request.getParameter("employee"));
                if(!isEmployee){
                    flg = true;
                }
            }
//            requestParams.put("ss", ss);
//            requestParams.put("searchcol", new String[]{"cmptname"});
//            StringUtil.checkpaging(requestParams, request);
            result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
            tabledata = result.getEntityList();
            count = result.getRecordTotalCount();
            cnt = tabledata.size();
            for (int i = 0; i < cnt; i++) {
                CompetencyDesMap log = (CompetencyDesMap) tabledata.get(i);
                requestParams.clear();
                requestParams.put("grid", log.getGroupid());
                result = hrmsCompetencyDAOObj.getQuestionGroup(requestParams);
                tabledata1 = result.getEntityList();
                cnt1 = tabledata1.size();
                for (int j = 0; j < cnt1; j++) {
                    QuestionGroup log1 = (QuestionGroup) tabledata1.get(j);
                    

                    if (!StringUtil.isNullOrEmpty(request.getParameter("submitstatus"))) {
                        if (request.getParameter("submitstatus").equals("1")) {
                            requestParams.clear();

                            filter_names.clear();
                            ArrayList filter_params = new ArrayList();
                            String userID = null, reviewerid = null;
                            if (isEmployee) {
                                userID = sessionHandlerImplObj.getUserid(request);
                                reviewerid = userID;
                            } else {
                                userID = request.getParameter("uid");
                                reviewerid = request.getParameter("manid");
                            }

                            filter_names.add(hrmsAnonymousAppraisalConstants.appcycleId);
                            filter_params.add(request.getParameter("appcycle"));

                            filter_names.add(hrmsAnonymousAppraisalConstants.employeeUserID);
                            filter_params.add(userID);

                            if (!isEmployee) {
                                filter_names.add(hrmsAnonymousAppraisalConstants.managerUserID);
                                filter_params.add(reviewerid);
                            }


                            filter_names.add(hrmsAnonymousAppraisalConstants.cmptquestionQuesid);
                            filter_params.add(log1.getCmptquestion().getQuesid());

//                            filter_names.add(BuildCriteria.OPERATORORDER + hrmsAnonymousAppraisalConstants.cmptquestionQuesid);
//                            filter_params.add(BuildCriteria.OPERATORORDERASC);

                            requestParams.put(hrmsAnonymousAppraisalConstants.filter_names, filter_names);
                            requestParams.put(hrmsAnonymousAppraisalConstants.filter_params, filter_params);

                            result = hrmsAppraisalcycleserviceobj.getQuestionAnswerForm(requestParams);
                            list = result.getEntityList();
                            if (list.isEmpty()) {
                                continue;
                            }
                        }
                    }
                    requestParams.clear();
                    requestParams.put("qid", log1.getCmptquestion().getQuesid());

                    result = hrmsCompetencyDAOObj.getCmptQuestion(requestParams);
                    tabledata2 = result.getEntityList();
                    cnt2 = tabledata2.size();
                    totalcount += cnt2;
                    for (int k = 0; k < cnt2; k++) {
                        CompetencyQuestion log2 = (CompetencyQuestion) tabledata2.get(k);
                        requestParams.clear();
                        requestParams.put("qid", log2.getQuesid());
                        requestParams.put("desid", log.getDesig().getId());
                        result = hrmsCompetencyDAOObj.getReviewerQuestionMap(requestParams);
                        tabledata3 = result.getEntityList();
                        if(tabledata3.size() > 0){
                            ReviewerQuestionMap log3 = (ReviewerQuestionMap) tabledata3.get(0);
                            JSONObject tmpObj = new JSONObject();
                            if(flg){
                                if(log3.getRevdesid() == null){
                                    tmpObj.put("qdescription", log2.getQuesid());
                                    tmpObj.put("qdesc", log2.getQuesdesc());
                                    tmpObj.put("qans", log2.getNoofans());
                                    tmpObj.put("qorder", log2.getQuesorder());
                                    tmpObj.put("qtype", StringUtil.isNullOrEmpty(log2.getQuestype())?"100":log2.getQuestype());
                                    tmpObj.put("qdes", "");
                                    jArr.put(log2.getQuesorder()-1,tmpObj);
                                } else {
                                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", sessionHandlerImplObj.getUserid(request));
                                    if(log3.getRevdesid().getId().equals(ua.getDesignationid().getId())){
                                        tmpObj.put("qdescription", log2.getQuesid());
                                        tmpObj.put("qdesc", log2.getQuesdesc());
                                        tmpObj.put("qans", log2.getNoofans());
                                        tmpObj.put("qorder", log2.getQuesorder());
                                        tmpObj.put("qtype", StringUtil.isNullOrEmpty(log2.getQuestype())?"100":log2.getQuestype());
                                        tmpObj.put("qdes", log3.getRevdesid().getId());
                                        jArr.put(log2.getQuesorder()-1,tmpObj);
                                    } else {
                                        totalcount--;
                                    }
                                }
                            } else {
                                if(log3.getRevdesid() != null){
                                    tmpObj.put("qdescription", log2.getQuesid());
                                    tmpObj.put("qdesc", log2.getQuesdesc());
                                    tmpObj.put("qans", log2.getNoofans());
                                    tmpObj.put("qorder", log2.getQuesorder());
                                    tmpObj.put("qtype", StringUtil.isNullOrEmpty(log2.getQuestype())?"100":log2.getQuestype());
                                    tmpObj.put("qdes", log3.getRevdesid().getId());
                                } else {
                                    tmpObj.put("qdescription", log2.getQuesid());
                                    tmpObj.put("qdesc", URLDecoder.decode(log2.getQuesdesc()));
                                    tmpObj.put("qans", log2.getNoofans());
                                    tmpObj.put("qorder", log2.getQuesorder());
                                    tmpObj.put("qtype", StringUtil.isNullOrEmpty(log2.getQuestype())?"100":log2.getQuestype());
                                    tmpObj.put("qdes", "");
                                }
                                jArr.put(log2.getQuesorder()-1,tmpObj);
                            }
                            
                        }
                    }
                }
            }
            JSONArray jArr1 =  new JSONArray();
            for(int i =0;i<jArr.length();i++) {
                if(!jArr.isNull(i)) {
                    jArr1.put(jArr.get(i));
                }
            }
            jobj.put("data", jArr1);
            jobj.put("success", true);
            jobj.put("count", totalcount);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    public ModelAndView deleteCompetencyQuestion(HttpServletRequest request,HttpServletResponse response) {
    	KwlReturnObject result=null;
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("ids", request.getParameterValues("ids"));
            result=hrmsCompetencyDAOObj.deleteCompetencyQuestion(requestParams);
            if(result.isSuccessFlag())
            {
            	jobj.put("success", true);
            }else{
            	jobj.put("success", false);
            }
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getQuestionAnswerForm(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        List<HashMap<String, String>> list = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            Appraisalmanagement log = (Appraisalmanagement) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalmanagement", request.getParameter("appraisalid"));
            String appCycleID = request.getParameter(hrmsAnonymousAppraisalConstants.appraisalcycid);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            String userID = null,reviewerid=null;
            if (Boolean.parseBoolean(request.getParameter("employee"))) {
                userID = sessionHandlerImplObj.getUserid(request);
                reviewerid =  userID;
            } else {
                userID = log.getEmployee().getUserID();
                reviewerid = log.getManager().getUserID();
            }

            filter_names.add(hrmsAnonymousAppraisalConstants.appcycleId);
            filter_params.add(appCycleID);
            
            filter_names.add(hrmsAnonymousAppraisalConstants.employeeUserID);
            filter_params.add(userID);

            filter_names.add(hrmsAnonymousAppraisalConstants.managerUserID);
            filter_params.add(reviewerid);

            filter_names.add(BuildCriteria.OPERATORORDER+hrmsAnonymousAppraisalConstants.cmptquestionOrder);
            filter_params.add(BuildCriteria.OPERATORORDERASC);
            
            requestParams.put(hrmsAnonymousAppraisalConstants.filter_names, filter_names);
            requestParams.put(hrmsAnonymousAppraisalConstants.filter_params, filter_params);

            result = hrmsAppraisalcycleserviceobj.getQuestionAnswerForm(requestParams);
            list = result.getEntityList();
            JSONArray cmpObj = new JSONArray();
            if (!list.isEmpty()) {

                HashMap<String, String> jsonmap = list.get(0);
                HashMap<String, String> questionjsonmap = list.get(1);
                count = questionjsonmap.size();
                if (questionjsonmap.size() > 0) {
                    for (String key : questionjsonmap.keySet()) {
                        JSONObject appObj = new JSONObject();
                        Integer size = Integer.parseInt(questionjsonmap.get(key));
                        appObj.put(hrmsAnonymousAppraisalConstants.question, key);
                        for(int i=0;i<size;i++){
                            appObj.append(hrmsAnonymousAppraisalConstants.answer, jsonmap.get(key+i));
                        }
                        appObj.put("count",size);
                        cmpObj.put(appObj);
                    }
                }
            }

            jobj.put(hrmsAnonymousAppraisalConstants.success, true);
            jobj.put(hrmsAnonymousAppraisalConstants.data, cmpObj);
            jobj.put(hrmsAnonymousAppraisalConstants.totalCount, count);
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
