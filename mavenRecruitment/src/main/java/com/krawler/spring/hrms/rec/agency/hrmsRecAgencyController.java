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
package com.krawler.spring.hrms.rec.agency;


import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.recruitment.Agency;
import com.krawler.hrms.recruitment.Applyagency;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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

/**
 *
 * @author shs
 */
public class hrmsRecAgencyController extends MultiActionController implements MessageSourceAware  {
    private String successView;
    private hrmsRecAgencyDAO hrmsRecAgencyDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private MessageSource messageSource;

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public hrmsRecAgencyDAO getHrmsRecAgencyDAOObj() {
        return hrmsRecAgencyDAOObj;
    }

    public void setHrmsRecAgencyDAO(hrmsRecAgencyDAO hrmsRecAgencyDAOObj) {
        this.hrmsRecAgencyDAOObj = hrmsRecAgencyDAOObj;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
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

    public ModelAndView showAgency(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException, JSONException {
        String hql2 = "";
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        List lst2 = null;
        KwlReturnObject result = null;
        try {
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            ArrayList params = new ArrayList();
            params.add(cmpid);
            params.add(0);

            ArrayList filter_names = new ArrayList();
            filter_names.add("company.companyID");
            filter_names.add("delflag");

            String[] searchcol = new String[] {"agencyname"};

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", params);
            requestParams.put("searchcol", searchcol);
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("allflag", false);
            StringUtil.checkpaging(requestParams, request);
            result = hrmsRecAgencyDAOObj.getAgency(requestParams);
            lst2 = result.getEntityList();
            count = result.getRecordTotalCount();
            JSONArray jArr = new JSONArray();
            for (int i = 0; i < lst2.size(); i++) {
                Agency log = (Agency) lst2.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("agid", log.getAgencyid());
                tmpObj.put("agname", log.getAgencyname());
                tmpObj.put("url", log.getAgencyweb());
                tmpObj.put("cost", log.getReccost());
                tmpObj.put("manager", log.getApprman().getFirstName() + " " + (log.getApprman().getLastName() == null ? "" : log.getApprman().getLastName()));
                tmpObj.put("managerid", log.getApprman().getUserID());
                tmpObj.put("contactperson", log.getConperson());
                tmpObj.put("address", log.getAgencyadd());
                tmpObj.put("phoneno", log.getAgencyno());
                jArr.put(tmpObj);
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception ex) {
            System.out.print(ex);

        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView addAgency(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result=null;
        int count = 0;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            if (StringUtil.isNullOrEmpty(request.getParameter("agencyid"))) {
                requestParams.put("delflag", 0);
            }else{
                requestParams.put("agencyid", request.getParameter("agencyid"));
            }
            requestParams.put("company", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("agencyname", request.getParameter("agencyname"));
            requestParams.put("agencyweb", request.getParameter("agencyweb"));
            requestParams.put("reccost", request.getParameter("reccost"));
            requestParams.put("conperson", request.getParameter("conperson"));
            requestParams.put("apprman", request.getParameter("apprman"));
            requestParams.put("agencyno", request.getParameter("agencyno"));
            requestParams.put("agencyadd", request.getParameter("agencyadd"));
            result = hrmsRecAgencyDAOObj.addagency(requestParams);
            count = result.getRecordTotalCount();
            if(count == 0){
                jobj1.put("message", result.getMsg());
            }
            jobj.put("valid", true);
            jobj.put("data",  jobj1.toString());
           // jobj.put("data", "success");
            txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        } finally {
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }

    public ModelAndView deleteAgency(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String agencyids[] = request.getParameterValues("agencyid");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            for (int i = 0; i < agencyids.length; i++) {
                requestParams.put("agencyid", agencyids[i]);
                hrmsRecAgencyDAOObj.deleteAgency(requestParams);
            }
            jobj.put("valid", true);
            jobj.put("data", "success");
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }

    public ModelAndView addApplyAgency(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        //Create transaction
        
        TransactionStatus status=null;
        int count = 0;
        KwlReturnObject result = null;
        ArrayList params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        try {
            String[] recids = request.getParameterValues("jobids");
            String agencyid = request.getParameter("ageid");
            //requestParams.put("agencyid", request.getParameter("ageid"));
            for (int i = 0; i < recids.length; i++) {
                filter_names.clear();
                params.clear();
                params.add(request.getParameter("ageid"));
                params.add(recids[i]);

                filter_names.add("applyagency.agencyid");
                filter_names.add("posid");

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", params);
                result = hrmsRecAgencyDAOObj.getApplyagency(requestParams);
                count = result.getRecordTotalCount();
                if(count > 0){
                    jobj1.put("message", messageSource.getMessage("hrms.recruitment.Pleasedontassignalreadyassignedjobs",null,"Please don't assign already assigned jobs.", RequestContextUtils.getLocale(request)));
                    jobj1.put("heading", messageSource.getMessage("hrms.common.warning", null,"Warning", RequestContextUtils.getLocale(request)));
                    jobj.put("valid", true);
                    jobj.put("data",  jobj1.toString());
                    return new ModelAndView("jsonView","model",jobj.toString());
                }
              //  hrmsRecAgencyDAOObj.deleteApplyAgency(requestParams);
            }
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("JE_Tx");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            status = txnManager.getTransaction(def);
            for (int i = 0; i < recids.length; i++) {
                requestParams.clear();
                requestParams.put("applyagency", agencyid);
                requestParams.put("applypos", recids[i]);
                hrmsRecAgencyDAOObj.addApplyAgency(requestParams);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.JOB_TO_AGENCY, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has assigned job position " + contact.getApplypos().getJobid() + " to agency " + contact.getApplyagency().getAgencyname(),request);
            }
            jobj1.put("message", messageSource.getMessage("hrms.Messages.calMsgBoxShow128", null,"Job position assigned successfully.", RequestContextUtils.getLocale(request)));
            jobj1.put("heading", messageSource.getMessage("hrms.common.success", null,"Success", RequestContextUtils.getLocale(request)));
            jobj.put("valid", true);
            jobj.put("data", jobj1.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj.toString());
    }

    public ModelAndView deleteApplyAgency(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] recids = request.getParameterValues("jobids");
            requestParams.put("agencyid", request.getParameter("ageid"));
            for (int i = 0; i < recids.length; i++) {
                requestParams.put("posid", recids[i]);
                hrmsRecAgencyDAOObj.deleteApplyAgency(requestParams);
            }
            
            jobj.put("valid", true);
            jobj.put("data", "success");
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj.toString());
    }

    public ModelAndView viewagencyJobs(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        KwlReturnObject result = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        try {
            ArrayList params = new ArrayList();
            String ss = request.getParameter("ss");
            String agencyid = request.getParameter("agencyid");
            params.add(agencyid);
//            String searchString = "";
//            if(!StringUtil.isNullOrEmpty(ss)){
//                StringUtil.insertParamSearchString(params, ss, 2);
//                searchString = StringUtil.getSearchString(ss, "and", new String[]{"applypos.jobid","applypos.details"});
//            }


//            String hql = "from Applyagency where applyagency.agencyid=? "+ searchString;
//            tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
//            count = tabledata.size();
//            list = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});


            ArrayList filter_names = new ArrayList();
            filter_names.add("applyagency.agencyid");

            String[] searchcol = new String[] {"applypos.jobid","applypos.details"};

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", params);
            requestParams.put("start", Integer.valueOf(request.getParameter("start")));
            requestParams.put("limit", Integer.valueOf(request.getParameter("limit")));
            requestParams.put("allflag", false);
            requestParams.put("searchcol", searchcol);
            requestParams.put("ss", request.getParameter("ss"));
            result = hrmsRecAgencyDAOObj.getApplyagency(requestParams);
            list = result.getEntityList();
            JSONArray jArr = new JSONArray();
            for(int i=0;i<list.size();i++){
                Applyagency log = (Applyagency) list.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("jobid", log.getApplypos().getJobid());
                tmpObj.put("posid", log.getApplypos().getPositionid());
                tmpObj.put("posname", log.getApplypos().getPosition().getValue());
                tmpObj.put("department", log.getApplypos().getDepartmentid().getValue());
                tmpObj.put("details", log.getApplypos().getDetails());
                tmpObj.put("manager", log.getApplypos().getManager().getFirstName() + " " + log.getApplypos().getManager().getLastName());
                tmpObj.put("positionstatus", log.getApplypos().getDelflag());
                tmpObj.put("nopos", log.getApplypos().getNoofpos());
                tmpObj.put("posfilled", log.getApplypos().getPositionsfilled());
                tmpObj.put("startdate", sessionHandlerImplObj.getDateFormatter(request).format(log.getApplypos().getStartdate()));
                tmpObj.put("enddate", sessionHandlerImplObj.getDateFormatter(request).format(log.getApplypos().getEnddate()));
                jobj.append("data", tmpObj);
                jArr.put(tmpObj);
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsManager.viewagencyJobsFunction", e);
        } finally {
           return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
