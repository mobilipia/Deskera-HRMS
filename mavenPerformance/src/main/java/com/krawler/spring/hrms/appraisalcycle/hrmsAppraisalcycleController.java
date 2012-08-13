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
package com.krawler.spring.hrms.appraisalcycle;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_EmailTemplates;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.web.resource.Links;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.competency.hrmsCompetencyDAO;
import com.krawler.spring.hrms.nonanonymousappraisal.hrmsNonAnonymousAppraisalDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

public class hrmsAppraisalcycleController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;
    private hrmsCompetencyDAO hrmsCompetencyDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsNonAnonymousAppraisalDAO hrmsNonAnonymousAppraisalDAO;
    private MessageSource messageSource;

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
    
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public hrmsCompetencyDAO getHrmsCompetencyDAOObj() {
        return hrmsCompetencyDAOObj;
    }

    public void setHrmsCompetencyDAO(hrmsCompetencyDAO hrmsCompetencyDAOObj) {
        this.hrmsCompetencyDAOObj = hrmsCompetencyDAOObj;
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

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public profileHandlerDAO getProfileHandlerDAOObj() {
        return profileHandlerDAOObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setHrmsNonAnonymousAppraisalDAO(hrmsNonAnonymousAppraisalDAO hrmsNonAnonymousAppraisalDAO) {
        this.hrmsNonAnonymousAppraisalDAO = hrmsNonAnonymousAppraisalDAO;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }





    public ModelAndView getAppraisalCycle(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fmt1 = new SimpleDateFormat("MMMM d, yyyy");
        List lst = null;
        int count = 0;
        try {
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, fmt.format(new Date()),sessionHandlerImplObj.getUserid(request));
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("company.companyID");
            filter_values.add(sessionHandlerImplObj.getCompanyid(request));

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("ss", ss);
            requestParams.put("searchcol", new String[]{"ac.cyclename"});
            StringUtil.checkpaging(requestParams, request);
            result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
            List lst1 = result.getEntityList();
            count = result.getRecordTotalCount();
                for(int ctr=0;ctr<lst1.size();ctr++){
                    Appraisalcycle app = (Appraisalcycle) lst1.get(ctr);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("cycleid", app.getId());
                    tmpObj.put("cyclename", app.getCyclename());
                    tmpObj.put("startdate", fmt1.format(app.getStartdate()));
                    tmpObj.put("enddate", fmt1.format(app.getEnddate()));
                    if (app.getSubmitstartdate() != null) {
                        tmpObj.put("submitstartdate", fmt1.format(app.getSubmitstartdate()));
                    }
                    if (app.getSubmitenddate() != null) {
                        tmpObj.put("submitenddate", fmt1.format(app.getSubmitenddate()));
                        if(fmt1.parse(fmt1.format(app.getSubmitenddate())).before(fmt1.parse(fmt1.format(userdate)))){
                            tmpObj.put("canapprove", "1");
                        } else {
                            tmpObj.put("canapprove", "0");
                        }
                    }
                    if(app.isCycleapproval()){
                        tmpObj.put("status","1");
                    }else{
                        tmpObj.put("status","0");
                    }
                    jobj.append("data", tmpObj);
                }
            if (lst1.size() == 0) {
                    jobj.put("data", "");
                }
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getAppraisalcycleform(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr =  new JSONArray();
        List lst = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
        String bol = request.getParameter("employee");
        boolean bolen = false;
        if (!StringUtil.isNullOrEmpty(bol)) {
            bolen = Boolean.parseBoolean(bol);
        }
        int count = 0;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            Date date1 = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, fmt.format(new Date()),sessionHandlerImplObj.getUserid(request));
            Appraisalcycle currentCycleObj = null;
            List lst1 = null;
            Iterator ite1;
            String hql1 = "";
            if (StringUtil.isNullOrEmpty(bol) || (bolen == false)) {
//                hql1 = "from Appraisalcycle where company.companyID=? and (startdate <= ? and enddate >= ?) " +
//                        " or enddate < ? order by enddate desc ";
//                lst1 = HibernateUtil.executeQuery(session, hql1, new Object[]{AuthHandler.getCompanyid(request), date1, date1, date1});
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("date", date1);
                result = hrmsAppraisalcycleDAOObj.getAppraisalcycleform(requestParams);
                lst1 = result.getEntityList();
                        
                
                for(int ctr=0;ctr<result.getRecordTotalCount();ctr++){
                    currentCycleObj = (Appraisalcycle) lst1.get(ctr);
                }
//                String hql = "from Appraisalcycle where company.companyID=?";
//                lst = HibernateUtil.executeQuery(session, hql, AuthHandler.getCompanyid(request));
                requestParams.clear();
                ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
                filter_names.add("company.companyID");
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
                lst = result.getEntityList();
                count = result.getRecordTotalCount();
                Iterator ite = lst.iterator();
                while (ite.hasNext()) {
                    Appraisalcycle app = (Appraisalcycle) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("appcycleid", app.getId());
                    tmpObj.put("appcycle", app.getCyclename());
                    tmpObj.put("startdate", app.getStartdate());
                    tmpObj.put("enddate", app.getEnddate());
                    tmpObj.put("submitstartdate", app.getSubmitstartdate());
                    tmpObj.put("submitenddate", app.getSubmitenddate());
                    if (app.isCycleapproval()) {
                            tmpObj.put("status", "1");
                        } else {
                            tmpObj.put("status", "0");
                        }
                    if (currentCycleObj!=null && app.getId().equals(currentCycleObj.getId())) {
                        tmpObj.put("currentFlag", 1);
                    } else {
                        tmpObj.put("currentFlag", 0);
                    }
                    if (app.getSubmitstartdate().after(fmt1.parse(fmt1.format(date1)))) {
                        tmpObj.put("substart", 1);
                    } else if (app.getSubmitenddate().before(fmt1.parse(fmt1.format(date1)))) {
                        tmpObj.put("substart", 2);
                    } else {
                        tmpObj.put("substart", 0);
                    }
                    jobj.append("data", tmpObj);
                    jarr.put(tmpObj);
                }
            } else {
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("date", date1);
                result = hrmsAppraisalcycleDAOObj.getAppraisalcycleform(requestParams);
                lst1 = result.getEntityList();


                for(int ctr=0;ctr<result.getRecordTotalCount();ctr++){
                    currentCycleObj = (Appraisalcycle) lst1.get(ctr);
                }

//                 String hql = "select apm,appcyle from Appraisalmanagement apm right outer join apm.appcycle appcyle where apm.employee.userID=? "+
//                            "and appcyle.company.companyID=? group by apm.appcycle.id";
//                    lst = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request), AuthHandler.getCompanyid(request)});
                requestParams.clear();
//                requestParams.put("apm.employee.userID", AuthHandler.getUserid(request));
//                requestParams.put("appcyle.company.companyID", AuthHandler.getCompanyid(request));
                requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                result = hrmsAppraisalcycleDAOObj.getAppraisalcycleemployee(requestParams);
                lst = result.getEntityList();
                count = result.getRecordTotalCount();
                    for(int ctr=0;ctr<count;ctr++){
                        Object[] row = (Object[]) lst.get(ctr);
                        Appraisalcycle app = (Appraisalcycle) row[1];
                        JSONObject tmpObj = new JSONObject();
                        tmpObj.put("appcycleid", app.getId());
                        tmpObj.put("appcycle", app.getCyclename());
                        tmpObj.put("startdate", app.getStartdate());
                        tmpObj.put("enddate", app.getEnddate());
//                        tmpObj.put("submitstartdate",AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitstartdate()));
//                        tmpObj.put("submitenddate", AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitenddate()));
                        tmpObj.put("submitstartdate",kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request),sessionHandlerImplObj.getUserTimeFormat(request),sessionHandlerImplObj.getTimeZoneDifference(request)).format(app.getSubmitstartdate()));
                        tmpObj.put("submitenddate",kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request),sessionHandlerImplObj.getUserTimeFormat(request),sessionHandlerImplObj.getTimeZoneDifference(request)).format(app.getSubmitenddate()));
                        if (app.isCycleapproval()) {
                            tmpObj.put("status", "1");
                        } else {
                            tmpObj.put("status", "0");
                        }
                        if (currentCycleObj!=null && app.getId().equals(currentCycleObj.getId())) {
                            tmpObj.put("currentFlag", 1);
                        } else {
                            tmpObj.put("currentFlag", 0);
                        }
                        if (app.getSubmitstartdate().after(date1)) {
                            tmpObj.put("substart", 1);
                        } else {
                        	if (app.getSubmitenddate().before(date1)) {
                        		tmpObj.put("substart", 2);
                        	} else {
                            	tmpObj.put("substart", 0);
                            }
                        }
//                        jobj.append("data", tmpObj);
                        jarr.put(tmpObj);
                    }
                
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }


    public ModelAndView setAppraisalcycle(HttpServletRequest request, HttpServletResponse response){
       JSONObject jobj=new JSONObject();
       JSONObject jobj1=new JSONObject();
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
       boolean flag=false;
       boolean initiateFlag = false;
       KwlReturnObject result = null;
       //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

       try {
            HashMap<String,Object>requestParams = new HashMap<String, Object>();
            Date startdate=(Date) fmt.parse(request.getParameter("startdate"));
            Date enddate=(Date) fmt.parse(request.getParameter("enddate"));
            Date submitstartdate=(Date) fmt.parse(request.getParameter("submitsdate"));
            Date submitenddate=(Date) fmt.parse(request.getParameter("submitedate"));
            String cyclename = request.getParameter("cyclename");
            Appraisalcycle appcycle = null;
            List tabledata = null;
            String hql="";
            if(request.getParameter("editflag").equals("1")) {
                String cycleid = request.getParameter("cycleid");
//                appcycle = (Appraisalcycle)session.get(Appraisalcycle.class, cycleid);
                requestParams.put("primary", true);
                requestParams.put("id", cycleid);
                result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
                if(StringUtil.checkResultobjList(result)){
                    appcycle = (Appraisalcycle) result.getEntityList().get(0);
                }
                boolean dateflag = true;
                if(!startdate.equals(appcycle.getStartdate()) || !enddate.equals(appcycle.getEnddate())) {
                    dateflag = false;
                    //Check for cycle initiated
                    requestParams.clear();
                    ArrayList filter_names = new ArrayList(),filter_values = new ArrayList();
                    filter_names.add("appcycle");
                    filter_values.add(appcycle);
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    tabledata = result.getEntityList();

//                    hql="from Appraisalmanagement where appcycle = ?";
//                    tabledata=HibernateUtil.executeQuery(session, hql, new Object[]{appcycle});
                    if(tabledata.size() == 0) {
                        dateflag = true;
                    }
                }

                if(dateflag) {
//                    hql="from Appraisalcycle where company.companyID=? and id != ? and ((? between startdate and enddate) or (? between startdate and enddate)) ";
//                    tabledata=HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getCompanyid(request), cycleid, startdate, enddate});
                    requestParams.clear();
                    requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    requestParams.put("id", cycleid);
                    requestParams.put("date1", startdate);
                    requestParams.put("date2", enddate);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalcycle2(requestParams);
                    tabledata = result.getEntityList();

                } else {
                    initiateFlag = true;
                    jobj.put("msg",messageSource.getMessage("hrms.performance.cannot.edit.cycle.appraisal.initiated", null, RequestContextUtils.getLocale(request)));
                }
                requestParams.put("id", cycleid);
            } else {
//                hql="from Appraisalcycle where company.companyID=? and ((? between startdate and enddate) or (? between startdate and enddate))";
//                tabledata=HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getCompanyid(request), startdate, enddate});
                requestParams.clear();
                requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("date1", startdate);
                requestParams.put("date2", enddate);
                result = hrmsAppraisalcycleDAOObj.getAppraisalcycle1(requestParams);
                tabledata = result.getEntityList();
//                appcycle=new Appraisalcycle();
            }
            
            
            

           // if(!initiateFlag && tabledata.isEmpty()){
            if(!initiateFlag){
//                appcycle.setCyclename(cyclename);
//                appcycle.setStartdate(startdate);
//                appcycle.setEnddate(enddate);
//                appcycle.setCreatedby((User)session.get(User.class,AuthHandler.getUserid(request)));
//                appcycle.setCompany((Company)session.get(Company.class,AuthHandler.getCompanyid(request)));
//                appcycle.setSubmitstartdate(submitstartdate);
                requestParams.put("cyclename", cyclename);
                requestParams.put("startdate", startdate);
                requestParams.put("enddate", enddate);
                requestParams.put("createdby", sessionHandlerImplObj.getUserid(request));
                requestParams.put("company", sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("submitstartdate", submitstartdate);
                if(request.getParameter("editflag").equals("1")) {
                    if(appcycle.getSubmitenddate().before(submitenddate)){
//                        appcycle.setReviewed(false);
//                        appcycle.setCycleapproval(false);
                        requestParams.put("reviewed", false);
                        requestParams.put("cycleapproval", false);
                    }
                } else {
//                    appcycle.setReviewed(false);
//                    appcycle.setCycleapproval(false);
                    requestParams.put("reviewed", false);
                    requestParams.put("cycleapproval", false);
                }

//                appcycle.setSubmitenddate(submitenddate);
                requestParams.put("submitenddate", submitenddate);

//                session.save(appcycle);
                result = hrmsAppraisalcycleDAOObj.addAppraisalcycle(requestParams);
                if(result.isSuccessFlag()){
                	if(request.getParameter("editflag").equals("1")) {
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.CYCLE_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited appraisal cycle " + appcycle.getCyclename(),request);
                        auditTrailDAOObj.insertAuditLog(AuditAction.CYCLE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited appraisal cycle " + cyclename, request, "0");
                    }
                    else{
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.CYCLE_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new appraisal cycle " + appcycle.getCyclename() + " for the period from " + fmt.format(startdate) + " till " + fmt.format(enddate),request);
                        auditTrailDAOObj.insertAuditLog(AuditAction.CYCLE_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new appraisal cycle " + cyclename + " for the period from " + startdate + " till " + enddate, request, "0");
                    }
                }
                flag=true;
//            } else if(!initiateFlag && !tabledata.isEmpty()) {
//                jobj.put("msg","Appraisal cycle added is overlapping other appraisal cycle.");
            }
            jobj.put("success",result.isSuccessFlag()&&flag);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
            txnManager.commit(status);

        } catch(Exception e){
            e.printStackTrace();
            txnManager.rollback(status);
        }
       return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView approveAppraisalCycle(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
//                Appraisalcycle appcl = (Appraisalcycle) session.load(Appraisalcycle.class, ids[i]);
//                appcl.setCycleapproval(Boolean.parseBoolean(request.getParameter("status")));
//                session.saveOrUpdate(appcl);
               HashMap<String, Object> requestParams = new HashMap<String, Object>();
               requestParams.put("id", ids[i]);
               requestParams.put("cycleapproval", Boolean.parseBoolean(request.getParameter("status")));
               hrmsAppraisalcycleDAOObj.addAppraisalcycle(requestParams);
               jobj.put("success", "true");
               jobj1.put("data", jobj.toString());
               jobj1.put("valid", true);

           }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }


    // @@ Test if this is working (working@shs)
    public ModelAndView sendappraisalemail(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            String cyclename = "";
            String cycledate = "";
            String mailcontent="";
            List recordTotalCount = null;            
            String email= sessionHandlerImplObj.getSysEmailIdByCompanyID(request);
            boolean flag=true;
            String plainmsg="";
            String htmlmsg="";
            String title="";
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat fmt1 = new SimpleDateFormat("MMMM d, yyyy");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            KwlReturnObject result = null;
            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, fmt.format(new Date()),sessionHandlerImplObj.getUserid(request));
            requestParams.put("primary", true);
            requestParams.put("id", request.getParameter("appraisalcycleid"));
            result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
            Appraisalcycle app = null;
            if(StringUtil.checkResultobjList(result))
                app = (Appraisalcycle) result.getEntityList().get(0);
            
            requestParams.clear();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("id");
            filter_values.add(request.getParameter("appraisalcycleid"));
            filter_names.add(">=submitenddate");
            filter_values.add(userdate);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = hrmsAppraisalcycleDAOObj.getAppraisalCycle(requestParams);
            List tabledata = result.getEntityList();
            if (!tabledata.isEmpty()) {
                JSONObject eobj = EmailCases(hrms_EmailTemplates.appraisalRemainder);
                plainmsg = eobj.getString("plainmessage");
                htmlmsg = eobj.getString("htmlmessage");
                title = eobj.getString("title");
//                String query = "select userID,emailID,firstName,lastName from User where company.companyID=? and deleted=?";
//                List list = HibernateUtil.executeQuery(session, query, new Object[]{sessionHandlerImplObj.getCompanyid(request), false});
                requestParams.clear();
                filter_names.clear();
                filter_values.clear();

                filter_names.add("company.companyID");
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                filter_names.add("deleteflag");
                filter_values.add(0);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result = profileHandlerDAOObj.getUser(requestParams);
                int totcount = result.getRecordTotalCount();
                List list = result.getEntityList();
                for(int i=0;i<totcount;i++){
//                    Object[] u = (Object[]) list.get(i);
                    User u = (User) list.get(i);
                    cyclename = app.getCyclename();
//                    cycledate = kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request),sessionHandlerImplObj.getUserTimeFormat(request),sessionHandlerImplObj.getTimeZoneDifference(request)).format(app.getSubmitenddate());
                    cycledate = fmt1.format(app.getSubmitenddate());
                    mailcontent = "<p>"+messageSource.getMessage("hrms.performance.appraisal.cycle", null, RequestContextUtils.getLocale(request))+": " + cyclename + "<br>"+messageSource.getMessage("hrms.performance.deadline.submission", null, RequestContextUtils.getLocale(request))+": " + cycledate;
//                    String hql = "select appm.employeestatus from Appraisalmanagement appm where appm.employee.userID=? and appm.appcycle.id=? group by appm.appcycle.id";
//                    recordTotalCount = HibernateUtil.executeQuery(session, hql, new Object[]{u[0].toString(), app.getId()});
                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();
                    filter_names.add("employee.userID");
//                    filter_values.add(u[0].toString());
                    filter_values.add(u.getUserID());
                    filter_names.add("appcycle.id");
                    filter_values.add(app.getId());
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);


//                    if (!recordTotalCount.isEmpty()) {
                    if (!result.getEntityList().isEmpty()) {
                        
                        if (result.getRecordTotalCount() == 0) {
                            mailcontent += "<br>"+messageSource.getMessage("hrms.masterconf.SelfAppraisal", null, RequestContextUtils.getLocale(request))+": " + messageSource.getMessage("hrms.common.Updates.NotSubmitted", null, RequestContextUtils.getLocale(request));
                        }
                    }
                    mailcontent+="</p>";
//                    String intpmsg = String.format(plainmsg, u[2].toString()+" "+u[3].toString(), mailcontent);
//                    String inthtmlmsg = String.format(htmlmsg, u[2].toString()+" "+u[3].toString(), mailcontent);
                    String intpmsg = String.format(plainmsg, u.getFirstName()+" "+u.getLastName(), mailcontent);
                    String inthtmlmsg = String.format(htmlmsg, u.getFirstName()+" "+u.getLastName(), mailcontent);
                    try {
                        SendMailHandler.postMail(new String[]{u.getEmailID()}, title, inthtmlmsg, intpmsg, email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                flag = false;
            }
            jobj.put("message", flag);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public JSONObject EmailCases(String temptype) {
        JSONObject jobj=new JSONObject();
        List tabledata = null;
        KwlReturnObject result = null;
        try {
//            String GET_MAIL_MSG = "select body_plain,body_html,subject from hrms_EmailTemplates where templatetype=?";
//            tabledata = HibernateUtil.executeQuery(session, GET_MAIL_MSG, new Object[]{temptype});
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("temptype", temptype);
            result = hrmsCommonDAOObj.gethrms_EmailTemplates(requestParams);
            tabledata = result.getEntityList();
            for(int ctr=0;ctr<tabledata.size();ctr++){
                 Object u[] = (Object[]) tabledata.get(ctr);
                 jobj.put("plainmessage",u[0].toString());
                 jobj.put("htmlmessage",u[1].toString());
                 jobj.put("title",u[2].toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jobj;
    }


    // @@ Test if this is working 
    public ModelAndView getAppraisallist(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr= new JSONArray();
        List lst = null;
        List tabledata = null;
        ArrayList data;
        String hql;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String appcylid=request.getParameter("appcylid");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(),filter_values = new ArrayList();
            ArrayList order_by = new ArrayList(),order_type = new ArrayList();

            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, fmt.format(new Date()),sessionHandlerImplObj.getUserid(request));

            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            if (!(hrmsCommonDAOObj.isEmployee(requestParams).isSuccessFlag()) && !(hrmsCommonDAOObj.isManager(requestParams).isSuccessFlag())) {
                if (Boolean.parseBoolean(request.getParameter("employee"))) {
//                     hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
//                    lst = HibernateUtil.executeQuery(session, hql,new Object[]{AuthHandler.getUserid(request),appcylid});
                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("employee.userID");
                    filter_values.add(sessionHandlerImplObj.getUserid(request));

                    filter_names.add("appcycle.id");
                    filter_values.add(appcylid);

                    filter_names.add("<=appcycle.submitstartdate");
                    filter_values.add(fmt1.parse(fmt1.format(userdate)));

                    filter_names.add(">=appcycle.submitenddate");
                    filter_values.add(fmt1.parse(fmt1.format(userdate)));

                    order_by.add("appcycle.submitstartdate");
                    order_type.add("asc");

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    requestParams.put("order_by", order_by);
                    requestParams.put("order_type", order_type);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    lst = result.getEntityList();
                    if(lst!=null && !lst.isEmpty()){
                    Appraisalmanagement log = (Appraisalmanagement) lst.get(0);
                    if(log!=null && log.getEmpdesid()!=null){
                        requestParams.clear();
                        requestParams.put("desigid", log.getEmpdesid().getId());
                        result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
                        tabledata = result.getEntityList();
                        if(tabledata.size() > 0){
                            jobj.put("desigid", log.getEmpdesid().getId());
                            jobj.put("empsubmitstatus", log.getEmployeestatus());
                        } else {
                            jobj.put("empsubmitstatus", log.getEmployeestatus());
                        }
                    } else {
                        jobj.put("empsubmitstatus", log.getEmployeestatus());
                    }

                    if(log!=null){
                		List<String> list1 = hrmsNonAnonymousAppraisalDAO.getQuestions(log.getAppraisalid());
                		if(list1!=null && !list1.isEmpty()){
                			jobj.put("isquestionemp", "true");
                		}else{
                			jobj.put("isquestionemp", "false");
                		}
                	}
                    }
                }else{
//                    hql = "from Appraisalmanagement where manager.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by concat(employee.firstName, employee.lastName) asc";
//                    lst = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request),appcylid});
                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("manager.userID");
                    filter_values.add(sessionHandlerImplObj.getUserid(request));
                    
                    filter_names.add("employee.deleteflag");
                    filter_values.add(0);

                    filter_names.add("appcycle.id");
                    filter_values.add(appcylid);

                    filter_names.add("<=appcycle.submitstartdate");
                    filter_values.add(fmt1.parse(fmt1.format(userdate)));

                    filter_names.add(">=appcycle.submitenddate");
                    filter_values.add(fmt1.parse(fmt1.format(userdate)));

                    order_by.add("concat(employee.firstName, employee.lastName)");
                    order_type.add("asc");

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    requestParams.put("order_by", order_by);
                    requestParams.put("order_type", order_type);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    lst = result.getEntityList();
                }
            } else {
                if (Boolean.parseBoolean(request.getParameter("employee"))) {
//                    hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
//                    lst = HibernateUtil.executeQuery(session, hql,new Object[]{sessionHandlerImplObj.getUserid(request),appcylid});
                     requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();
                     filter_names.add("employee.userID");
                    filter_values.add(sessionHandlerImplObj.getUserid(request));

                    filter_names.add("appcycle.id");
                    filter_values.add(appcylid);

                    filter_names.add("<=appcycle.submitstartdate");
                    filter_values.add(fmt1.parse(fmt1.format(userdate)));

                    filter_names.add(">=appcycle.submitenddate");
                    filter_values.add(fmt1.parse(fmt1.format(userdate)));

                    order_by.add("appcycle.submitstartdate");
                    order_type.add("asc");

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);
                    requestParams.put("order_by", order_by);
                    requestParams.put("order_type", order_type);
                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
                    lst = result.getEntityList();
                    if(lst!=null && !lst.isEmpty()){
                    Appraisalmanagement log = (Appraisalmanagement) lst.get(0);
                    JSONObject tmpObj = new JSONObject();
                    if(log!=null && log.getEmpdesid()!=null){
                        requestParams.clear();
                        requestParams.put("desigid", log.getEmpdesid().getId());
                        result = hrmsCompetencyDAOObj.getCompetencyDesMap(requestParams);
                        tabledata = result.getEntityList();
                        if(tabledata.size() > 0){
                            jobj.put("desigid", log.getEmpdesid().getId());
                            jobj.put("empsubmitstatus", log.getEmployeestatus());
                        } else {
                            jobj.put("empsubmitstatus", log.getEmployeestatus());
                        }
                    } else {
                        jobj.put("empsubmitstatus", log.getEmployeestatus());
                    }
                    
                    if(log!=null){
                		List<String> list1 = hrmsNonAnonymousAppraisalDAO.getQuestions(log.getAppraisalid());
                		if(list1!=null && !list1.isEmpty()){
                			jobj.put("isquestionemp", "true");
                		}else{
                			jobj.put("isquestionemp", "false");
                		}
                	}
                    }
                } else {
//                    hql = "from Assignmanager where assignman.userID=?";
//                    lst = HibernateUtil.executeQuery(session, hql, sessionHandlerImplObj.getUserid(request));
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("assignman.userID");
                    filter_values.add(sessionHandlerImplObj.getUserid(request));

//                    requestParams.put("assignman.userID", sessionHandlerImplObj.getUserid(request));
                    requestParams.put("filter_names",filter_names);
                    requestParams.put("filter_values",filter_values);
                    result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                    lst = result.getEntityList();
                    Iterator ite = lst.iterator();
                    String users = "";
                    while (ite.hasNext()) {
                        Assignmanager log = (Assignmanager) ite.next();
                        users +="'"+log.getAssignemp().getUserID()+"',";
                    }
                    if(users.length() > 0){
                        users = users.substring(0, users.length()-1);
                    }

//                    hql = "from Appraisalmanagement where employee.userID in (" + users + ") and manager.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by concat(employee.firstName, employee.lastName) asc";
//                    lst = HibernateUtil.executeQuery(session, hql, new Object[]{sessionHandlerImplObj.getUserid(request),appcylid});
                    requestParams.clear();
                    requestParams.put("users", users);
                    requestParams.put("managerid", sessionHandlerImplObj.getUserid(request));
                    requestParams.put("appcycle", appcylid);
                    requestParams.put("date", userdate);
                    result = hrmsAppraisalcycleDAOObj.getAppraisallistbyManager(requestParams);
                    lst = result.getEntityList();

                }
            }
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                data = new ArrayList();
                Appraisalmanagement app = (Appraisalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                if(app!=null){
            		List<String> list1 = hrmsNonAnonymousAppraisalDAO.getQuestions(app.getAppraisalid());
            		if(list1!=null && !list1.isEmpty()){
            			tmpObj.put("isquestionemp", "true");
            		}else{
            			tmpObj.put("isquestionemp", "false");
            		}
            	}
                
                tmpObj.put("appraisalid", app.getAppraisalid());
                tmpObj.put("username", app.getEmployee().getFirstName() + " " + app.getEmployee().getLastName());
                tmpObj.put("userid", app.getEmployee().getUserID());
                requestParams.clear();
                requestParams.put("empid", app.getEmployee().getUserID());
                requestParams.put("appraisalid", app.getAppraisalid());
                data = getappraisalData(requestParams);
                tmpObj.put("managername", app.getManager().getFirstName() + " " + app.getManager().getLastName());
                if(app.getEmployeedraft()==1 && !Boolean.parseBoolean(request.getParameter("employee"))){
                    tmpObj.put("employeecomment", "");
                } else {
                    tmpObj.put("employeecomment", data.get(1));
                }
                if(app.getManagerdraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))){
                    tmpObj.put("managercomment", "");
                } else {
                    tmpObj.put("managercomment", data.get(2));
                }
                tmpObj.put("managercompscore", app.getManagercompscore());
                tmpObj.put("managerstatus", app.getManagerstatus());
                tmpObj.put("employeestatus", app.getEmployeestatus());
                tmpObj.put("employeecompscore", app.getEmployeecompscore());
                tmpObj.put("managergoalscore", app.getManagergoalscore());
                tmpObj.put("employeegoalscore", app.getEmployeegoalscore());
                tmpObj.put("designationid", app.getEmpdesid().getId());
                tmpObj.put("designation", app.getEmpdesid().getValue());
                tmpObj.put("salaryrec", app.getSalaryrecommend());
                tmpObj.put("managerid", app.getManager().getUserID());
                if (app.getNewdesignation() != null) {
                    tmpObj.put("newdesig", app.getNewdesignation().getId());
                } else {
                    tmpObj.put("newdesig", "");
                }
                if (app.getNewdepartment() != null) {
                    tmpObj.put("newdept", app.getNewdepartment().getId());
                } else {
                    tmpObj.put("newdept", "");
                }
                if (app.getPerformance() != null) {
                    tmpObj.put("performance", app.getPerformance().getId());
                } else {
                    tmpObj.put("performance", "");
                }
                if(app.getAppcycle()!=null){
                    tmpObj.put("appcycle", app.getAppcycle().getCyclename());
                    tmpObj.put("startdate", app.getAppcycle().getStartdate());
                    tmpObj.put("enddate", app.getAppcycle().getEnddate());
                    tmpObj.put("appcycleid", app.getAppcycle().getId());
                }
                tmpObj.put("salaryinc", app.getSalaryincrement());
                jarr.put(tmpObj);
            }
            jobj.put("data",jarr);
            jobj.put("count", lst.size());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ArrayList<String> getappraisalData(HashMap<String,Object> requestParams){
        ArrayList<String> result = new ArrayList<String>();
        KwlReturnObject result1 = null;
        List tabledata = null;
        try {
            String appraisalid = requestParams.get("appraisalid").toString();

            requestParams.clear();
            requestParams.put("primary", true);
            requestParams.put("id", appraisalid);
            result1 = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            tabledata = result1.getEntityList();
            
            if(tabledata!=null && !tabledata.isEmpty()){
            	Appraisalmanagement log = (Appraisalmanagement) tabledata.get(0);
            	result.add(StringUtil.getFullName(log.getManager()));
            	result.add(log.getEmployeecomment());
            	result.add(log.getManagercomment());
            }else{
            	result.add(null);
            	result.add(null);
            	result.add(null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public ModelAndView sendappraisalreportEmail(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try {
            JSONObject jobj = new JSONObject();

            String mailcontent = "";
            String htmlmailcontent = "";
            String plnmailcontent = "";
            String email = sessionHandlerImplObj.getSysEmailIdByCompanyID(request);
            String plainmsg = "";
            String htmlmsg = "";
            String title = "";
            String url = "";

            HashMap<String, Object> requestParams =  new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

//            Company cmpid = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            Company cmpid = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            String appcycleid = request.getParameter("appraisalcycleid");
            url = URLUtil.getPageURL(request, Links.loginpagewthFull, cmpid.getSubDomain()) + "Performance/exportAppraisalReportPDF/appraisalReportExport.pf?pdfEmail=true&reviewappraisal=false&appraisalcycid=" + appcycleid + "&userid=";
            JSONObject eobj = EmailCases(hrms_EmailTemplates.emailemployeeReport);
            plainmsg = eobj.getString("plainmessage");
            htmlmsg = eobj.getString("htmlmessage");
            title = eobj.getString("title");
//            Appraisalcycle app = (Appraisalcycle) session.load(Appraisalcycle.class, appcycleid);
            Appraisalcycle app =(Appraisalcycle) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalcycle", appcycleid);
            mailcontent = "Appraisal Cycle: ";
//            String hql = "select distinct employee.userID,employee.firstName,employee.lastName,employee.employeeid,employee.emailID from Appraisalmanagement where appcycle.id=? and reviewstatus=2";
//            List list1 = HibernateUtil.executeQuery(session, hql, new Object[]{appcycleid});

            filter_names.add("appcycle.id");
            filter_values.add(appcycleid);

            filter_names.add("reviewstatus");
            filter_values.add(2);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("distinct","employee.userID");

            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            List list1 =  result.getEntityList();

            Iterator ite1 = list1.iterator();
            if (!list1.isEmpty()) {
                while (ite1.hasNext()) {
//                    Object[] row = (Object[]) ite1.next();
//                    Appraisalmanagement appmgmt = (Appraisalmanagement) ite1.next();
                    
//                    User row = appmgmt.getEmployee();
                    User row = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", ite1.next().toString());

                    String target = "_parent";
//                    plnmailcontent += "" + app.getCyclename() + " (" + url + row[0] +"&d="+row[0]+")\n\n";
                    plnmailcontent += "" + app.getCyclename() + " (" + url + row.getUserID() +"&d="+row.getUserID()+")\n\n";
//                    htmlmailcontent += "<a target='" + target + "' href='" + url + row[0] +"&d="+row[0]+"'>" +app.getCyclename()+ "</a>\n\n";
                    htmlmailcontent += "<a target='" + target + "' href='" + url + row.getUserID() +"&d="+row.getUserID()+"'>" +app.getCyclename()+ "</a>\n\n";

                    plnmailcontent = mailcontent + plnmailcontent;
                    htmlmailcontent = mailcontent + htmlmailcontent;
//                    String intpmsg = String.format(plainmsg, row[1].toString() + " " + row[2].toString(), plnmailcontent);
                    String intpmsg = String.format(plainmsg, row.getFirstName() + " " + row.getLastName(), plnmailcontent);
//                    String inthtmlmsg = String.format(htmlmsg, row[1].toString() + " " + row[2].toString(), htmlmailcontent.replaceAll("\n", "<br>"));
                    String inthtmlmsg = String.format(htmlmsg, row.getFirstName() + " " + row.getLastName(), htmlmailcontent.replaceAll("\n", "<br>"));
                    htmlmailcontent = "";
                    plnmailcontent = "";
                    try {
//                        SendMailHandler.postMail(new String[]{row[4].toString()}, title, inthtmlmsg, intpmsg, email);
                        SendMailHandler.postMail(new String[]{row.getEmailID()}, title, inthtmlmsg, intpmsg, email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jobj.put("message", mailcontent);
//            return jobj;
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView sendRevieweremailFunction(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try {
            JSONObject jobj = new JSONObject();
            String mailcontent = "";
            String htmlmailcontent = "";
            String plnmailcontent = "";
            String email = sessionHandlerImplObj.getSysEmailIdByCompanyID(request);
            String plainmsg = "";
            String htmlmsg = "";
            String title = "";
            String url = "";

//            Company cmpid = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            Company cmp = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", cmpid);
            String appcycleid = request.getParameter("appraisalcycleid");
            url = URLUtil.getPageURL(request, Links.loginpagewthFull, cmp.getSubDomain()) + "Performance/exportAppraisalReportPDF/appraisalReportExport.pf?pdfEmail=true&reviewappraisal=false&appraisalcycid=" + appcycleid + "&userid=";
            JSONObject eobj = EmailCases(hrms_EmailTemplates.pdfMailtoReviewer);
            plainmsg = eobj.getString("plainmessage");
            htmlmsg = eobj.getString("htmlmessage");
            title = eobj.getString("title");
//            Appraisalcycle app = (Appraisalcycle) session.load(Appraisalcycle.class, appcycleid);

            Appraisalcycle app = (Appraisalcycle) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.performance.Appraisalcycle", appcycleid);
            mailcontent = messageSource.getMessage("hrms.performance.appraisal.cycle", null, RequestContextUtils.getLocale(request))+": " + app.getCyclename() + "\n\n";
//            String query = "select distinct reviewer.userID,reviewer.firstName,reviewer.lastName,reviewer.emailID from Assignreviewer where reviewer.company.companyID=? and reviewer.deleted=? and reviewerstatus=1";
//            List list = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getCompanyid(request), false});

            HashMap<String, Object> requestParams =  new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            filter_names.add("reviewer.company.companyID");
            filter_values.add(cmpid);

            filter_names.add("reviewer.deleteflag");
            filter_values.add(0);

            filter_names.add("reviewerstatus");
            filter_values.add(1);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("distinct","reviewer.userID");

            result = hrmsCommonDAOObj.getAssignreviewer(requestParams);

            List list = result.getEntityList();
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
//                Object[] reviewer = (Object[]) itr.next();
//                Assignreviewer reviewer = (Assignreviewer) itr.next();
                User reviewer = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", itr.next().toString());
//                String hql = "select distinct employee.userID,employee.firstName,employee.lastName,employee.employeeid from Appraisalmanagement where appcycle.id=? and reviewstatus=2 and employee.userID in " +
//                        "(select employee.userID from Assignreviewer where reviewer.userID=? and reviewerstatus=1)";
//                List list1 = HibernateUtil.executeQuery(session, hql, new Object[]{appcycleid, reviewer[0].toString()});

                filter_names.clear();
                filter_values.clear();
                requestParams.clear();

                filter_names.add("reviewer.userID");
                filter_values.add(reviewer.getUserID());

                filter_names.add("reviewerstatus");
                filter_values.add(1);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                

                result = hrmsCommonDAOObj.getAssignreviewer(requestParams);

                List revlist = result.getEntityList();
                int revcnt = result.getRecordTotalCount();
                String revids="";
                for(int i=0;i<revcnt;i++) {
                    Assignreviewer temprev = (Assignreviewer) revlist.get(i);
                    if(i==0) {
                        revids =revids+" ( ";
                    }
                    revids=revids+"'"+temprev.getEmployee().getUserID()+"',";
                }
                if(revcnt!=0){
                    revids = revids.substring(0, revids.length()-1);
                    revids = revids+" )";
                }

                filter_names.clear();
                filter_values.clear();
                requestParams.clear();

                filter_names.add("appcycle.id");
                filter_values.add(appcycleid);

                filter_names.add("reviewstatus");
                filter_values.add(2);

//                filter_names.add("INemployee.userID");
//                filter_values.add(revids);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                requestParams.put("distinct", "employee.userID");
                requestParams.put("append"," and employee.userID in " +revids+" ");

                result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);

                List list1 = result.getEntityList();

                Iterator ite1 = list1.iterator();
                if (!list1.isEmpty()) {
                    while (ite1.hasNext()) {
//                        Object[] row = (Object[]) ite1.next();
//                        Appraisalmanagement row = (Appraisalmanagement) ite1.next();
                        User emp = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", ite1.next().toString());
                        String target="_parent";
                        requestParams.clear();
                        Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", emp.getUserID());
                        requestParams.put("empid", ua.getEmployeeid());
                        requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
//                        String empid=hrmsManager.getEmpidFormatEdit(session, request,(Integer)row[3]);
//                       String empid = hrmsCommonDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString();
//                        plnmailcontent += "" + row[1]+" "+row[2] + " - " + url + row[0] + "&d="+reviewer[0]+"\n";
                       plnmailcontent += "" + emp.getFirstName()+" "+emp.getLastName() + " - " + url + emp.getUserID() + "&d="+reviewer.getUserID()+"\n";
//                        htmlmailcontent+="<a target='"+target+"' href='"+ url+row[0]+"&d="+reviewer[0]+"'>"+row[1]+" "+row[2]+"</a>\n";
                       htmlmailcontent+="<a target='"+target+"' href='"+ url+emp.getUserID()+"&d="+reviewer.getUserID()+"'>"+emp.getFirstName()+" "+emp.getLastName()+"</a>\n";
                    }
                    plnmailcontent=mailcontent+plnmailcontent;
                    htmlmailcontent=mailcontent+htmlmailcontent;
//                    String intpmsg = String.format(plainmsg, reviewer[1].toString()+" "+reviewer[2].toString(), plnmailcontent);
                    String intpmsg = String.format(plainmsg, reviewer.getFirstName()+" "+reviewer.getLastName(), plnmailcontent);
//                    String inthtmlmsg = String.format(htmlmsg, reviewer[1].toString()+" "+reviewer[2], htmlmailcontent.replaceAll("\n","<br>" ));
                    String inthtmlmsg = String.format(htmlmsg, reviewer.getFirstName()+" "+reviewer.getLastName(), htmlmailcontent.replaceAll("\n","<br>" ));
                    htmlmailcontent="";
                    plnmailcontent="";
                    try {
//                        SendMailHandler.postMail(new String[]{reviewer[3].toString()}, title, inthtmlmsg, intpmsg, email);
                        SendMailHandler.postMail(new String[]{reviewer.getEmailID().toString()}, title, inthtmlmsg, intpmsg, email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jobj.put("message", mailcontent);
//            return jobj;
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    public ModelAndView checkAppraiserReviewer(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try{
            JSONObject jobj = new JSONObject();
            SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date userdate = kwlCommonTablesDAOObj.toUserSystemTimezoneDate(request, dateFmt.format(new Date()), sessionHandlerImplObj.getUserid(request));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();

            jobj.put("isappraiser", false);
            jobj.put("isreviewer", false);
            requestParams.put("userdate", userdate);
            requestParams.put("employee", false);
            if(hrmsAppraisalcycleDAOObj.showappraisalForm(requestParams).isSuccessFlag()){
                jobj.put("isappraiser", true);
            }
            requestParams1.put("checklink", "reviewappraisal");
            if(hrmsAppraisalcycleDAOObj.isReviewer(requestParams).isSuccessFlag()&&hrmsCommonDAOObj.checkModule(requestParams1).isSuccessFlag()){
                jobj.put("isreviewer", true);
            }
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView deleteAppraisalCycle(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try{
            HashMap<String,Object>requestParams = new HashMap<String, Object>();

            requestParams.put("filter_names", Arrays.asList("appcycle.id"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("id").toString()));

            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
            if(result.getRecordTotalCount()>0){
                jobj.put("msg", messageSource.getMessage("hrms.performance.appraisal.cycle.cannot.deleted", null, RequestContextUtils.getLocale(request)));
                jobj.put("success",false);
            } else {
                requestParams.clear();
                requestParams.put("id", request.getParameter("id").toString());
                result = hrmsAppraisalcycleDAOObj.deleteAppraisalcycle(requestParams);
                if(result.isSuccessFlag()){
                    jobj.put("msg",messageSource.getMessage("hrms.performance.appraisal.cycle.deleted.successfully", null, RequestContextUtils.getLocale(request)));
                    jobj.put("success",true);
                } else {
                    jobj.put("msg",messageSource.getMessage("hrms.performance.errror.deleting.appraisal.cycle", null, RequestContextUtils.getLocale(request)));
                    jobj.put("success",false);
                }
            }
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
            txnManager.commit(status);

        } catch(Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
