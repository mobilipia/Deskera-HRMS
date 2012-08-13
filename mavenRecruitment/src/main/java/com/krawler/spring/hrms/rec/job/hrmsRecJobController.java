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

package com.krawler.spring.hrms.rec.job;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.HrmsMsgs;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.web.resource.Links;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.MasterData;
import java.util.Arrays;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.hrms.recruitment.Allapplications;
import com.krawler.hrms.recruitment.ConfigRecruitment;
import com.krawler.hrms.recruitment.ConfigRecruitmentData;
import com.krawler.hrms.recruitment.ConfigRecruitmentMaster;
import com.krawler.hrms.recruitment.Jobapplicant;
import com.krawler.hrms.recruitment.Jobprofile;
import com.krawler.hrms.recruitment.Positionmain;
import com.krawler.hrms.recruitment.Recruiter;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.customcol.customcolDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.common.hrmsDocumentController;
import com.krawler.spring.hrms.common.hrmsExtApplDocsDAO;
import com.krawler.spring.hrms.rec.agency.hrmsRecAgencyDAO;
import com.krawler.spring.hrms.template.db.HtmlTemplate;
import com.krawler.spring.hrms.template.db.PlaceHolder;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.HibernateException;
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
public class hrmsRecJobController extends MultiActionController implements MessageSourceAware  {
    private String successView;
    private hrmsRecJobDAO hrmsRecJobDAOObj;
    private hrmsRecAgencyDAO hrmsRecAgencyDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private customcolDAO customcolDAOObj;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImplObj;
    private auditTrailDAO auditTrailDAOObj;
    private hrmsExtApplDocsDAO hrmsExtApplDocsDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private storageHandlerImpl storageHandlerImplObj;
     private MessageSource messageSource;
    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj) {
        this.storageHandlerImplObj = storageHandlerImplObj;
    }
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void sethrmsExtApplDocsDAO(hrmsExtApplDocsDAO hrmsExtApplDocsDAOObj) {
        this.hrmsExtApplDocsDAOObj = hrmsExtApplDocsDAOObj;
    }

    public void sethrmsRecAgencyDAO(hrmsRecAgencyDAO hrmsRecAgencyDAOObj) {
        this.hrmsRecAgencyDAOObj = hrmsRecAgencyDAOObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public hrmsRecJobDAO getHrmsRecJobDAOObj() {
        return hrmsRecJobDAOObj;
    }

    public void setCustomcolDAO(customcolDAO customcolDAOObj) {
        this.customcolDAOObj = customcolDAOObj;
    }

    public void setHrmsRecJobDAO(hrmsRecJobDAO hrmsRecJobDAOObj) {
        this.hrmsRecJobDAOObj = hrmsRecJobDAOObj;
    }

    public profileHandlerDAO getProfileHandlerDAOObj() {
        return profileHandlerDAOObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }


    public ModelAndView getInternalJobs(HttpServletRequest request, HttpServletResponse response) {
        //0->open, 1->deleted, 2->expired, 3->filled
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            jobj = getJobs(request, false);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception ex) {
        	ex.printStackTrace();
            throw new SessionExpiredException("getInternalJobs", ex.getMessage());
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public JSONObject getJobs (HttpServletRequest request, Boolean export) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String jobtype = request.getParameter("jobtype");
        int jobstatus = Integer.parseInt(request.getParameter("jobstatus"));
        List lst;
        int count = 0;
        String status = "";
        Positionmain psm;
        KwlReturnObject result = null;
        try {
        	String Searchjson = request.getParameter("searchJson");
            String cid = sessionHandlerImplObj.getCompanyid(request);
            String ss = request.getParameter("ss");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", cid);
            result = profileHandlerDAOObj.getCompanyid(requestParams);
            Company cmp = null;
            if(result.getEntityList()!=null&&result.getEntityList().size()>0){
                cmp = (Company) result.getEntityList().get(0);
            }
            ArrayList <String> name = new ArrayList <String>();
            ArrayList <Object> value = new ArrayList <Object>();

            if (StringUtil.isNullOrEmpty(request.getParameter("employee"))) {
                name.add("company.companyID");
                value.add(cid);
                
                name.add("delflag");
                value.add(0);
                
                name.add("<enddate");
                value.add(new Date());

                requestParams.clear();
                requestParams.put("filter_names", name);
                requestParams.put("filter_values", value);

                result = hrmsRecJobDAOObj.getPositionmain(requestParams);
                lst = result.getEntityList();
                count = result.getRecordTotalCount();
                for (Integer ctr =0; ctr < lst.size();ctr++) {
                    Positionmain psmain = (Positionmain) lst.get(ctr);
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("positionid", psmain.getPositionid());
                    requestParams.put("delflag", 2);
                    hrmsRecJobDAOObj.updatePositionmain(requestParams);
                }
                name = new ArrayList <String>();
                value = new ArrayList <Object>();
                name.add("company.companyID");
                value.add(cid);

                if(!StringUtil.isNullOrEmpty(request.getParameter("vacancy"))){
                    name.add("!(noofpos-positionsfilled)");
                    value.add(0);
                }
               if (jobtype.equalsIgnoreCase("All")) {
                    if (jobstatus == 4) {
                        name.add("!delflag");
                        value.add(1);
                    }else{
                        name.add("delflag");
                        value.add(jobstatus);
                    }
                }else{
                    if (jobstatus == 4) {
                        name.add("!delflag");
                        value.add(1);

                        name.add("jobtype");
                        value.add(jobtype);
                    }else{
                        name.add("delflag");
                        value.add(jobstatus);

                        name.add("jobtype");
                        value.add(jobtype);
                    }
                }
            } else {
                name = new ArrayList <String>();
                value = new ArrayList <Object>();

                name.add("company.companyID");
                value.add(cid);

                name.add("!jobtype");
                value.add("External");

                name.add("delflag");
                value.add(0);

                name.add("<=startdate");
                value.add(new Date());

                name.add(">=enddate");
                value.add(new Date());

            }
            requestParams.clear();
            requestParams.put("filter_names", name);
            requestParams.put("filter_values", value);
            requestParams.put("allflag",export);
            requestParams.put("ss",ss);
            requestParams.put("searchcol",new String[]{"jobid", "details","departmentid.value","position.value","manager.firstName","manager.lastName"});
            if (!StringUtil.isNullOrEmpty(Searchjson)) {
                getMyAdvanceSearchparams(Searchjson, name);
                insertParamAdvanceSearchString(value, Searchjson);
            }
                StringUtil.checkpaging(requestParams, request);
                result = hrmsRecJobDAOObj.getPositionmain(requestParams);
            lst = result.getEntityList();
            count = result.getRecordTotalCount();
            for(Integer ctr=0;ctr<lst.size();ctr++) {
                JSONObject tmpObj = new JSONObject();
                psm = (Positionmain) lst.get(ctr);
                tmpObj.put("posid", psm.getPositionid());
                requestParams = new HashMap<String, Object>();
                requestParams.put("positionid", psm.getPositionid());
                requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
                name = new ArrayList <String>();
                value = new ArrayList <Object>();

                name.add("position.positionid");
                value.add(psm.getPositionid());
                name.add("employee.userID");
                value.add(sessionHandlerImplObj.getUserid(request));
                name.add("delflag");
                value.add(0);
                result = hrmsRecJobDAOObj.getPositionstatus(name,value);
                List statusList = result.getEntityList();
                Allapplications appobj = null;
                if(statusList!=null && statusList.size()>0){
                    appobj = (Allapplications)statusList.get(0);
                    status = appobj.getStatus();
                }else{
                    status = "none";
                }
                
                if (status.equalsIgnoreCase("none")) {
                    tmpObj.put("status", 0);
                    tmpObj.put("selectionstatus", messageSource.getMessage("hrms.recruitment.not.applied",null, RequestContextUtils.getLocale(request)));
                } else {
                    tmpObj.put("status", 1);
                    tmpObj.put("applicationid", appobj.getId());
                    tmpObj.put("selectionstatus", status);
                }
                tmpObj.put("posmasterid", psm.getPosition().getId());
                tmpObj.put("jobid", psm.getJobid());
                tmpObj.put("posname", psm.getPosition().getValue());
                tmpObj.put("details", psm.getDetails());
                tmpObj.put("department", psm.getDepartmentid().getValue());
                tmpObj.put("manager", psm.getManager().getFirstName() + " " + psm.getManager().getLastName());
                tmpObj.put("startdate", sessionHandlerImplObj.getDateFormatter(request).format(psm.getStartdate()));
                tmpObj.put("enddate", sessionHandlerImplObj.getDateFormatter(request).format(psm.getEnddate()));
                tmpObj.put("jobtype", psm.getJobtype());
                tmpObj.put("positionstatus", psm.getDelflag());
                tmpObj.put("departmentid", psm.getDepartmentid().getId());
                tmpObj.put("managerid", psm.getManager().getUserID());
                tmpObj.put("nopos", psm.getNoofpos());
                tmpObj.put("posfilled", psm.getPositionsfilled());
                String url=URLUtil.getPageURL(request,Links.loginpagewthFull,cmp.getSubDomain())+"jobs.jsp?jobid="+psm.getPositionid()+"";
                tmpObj.put("url",url );
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobj;
    }

    public ModelAndView getJobidFormat(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject obj = new JSONObject();
        int count = 0;
        Integer maxcount = 0;
        KwlReturnObject result = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("companyid", companyid);
            result = hrmsRecJobDAOObj.getMaxCountJobid(requestParams);
            maxcount = (Integer) result.getEntityList().get(0);
            CompanyPreferences cmp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", companyid);
            obj.put("maxempid", maxcount.toString());
            obj.put("jobidformat", cmp.getJobidformat()==null?"":cmp.getJobidformat().replaceAll("[^a-zA-Z]", ""));
            jobj.append("data", obj);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView InternalJobpositions(HttpServletRequest request, HttpServletResponse response) {
        Date date = null;
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        boolean checkflag = true;
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
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Company company =(Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", companyid);
            if (StringUtil.isNullOrEmpty(request.getParameter("posid"))) {
                ArrayList <String> name = new ArrayList <String>();
                ArrayList <Object> value = new ArrayList <Object>();
                name.add("position.id");
                value.add(request.getParameter("position"));

                name.add("department.id");
                value.add(request.getParameter("department"));

                name.add("jobtype");
                value.add(request.getParameter("jobtype"));

                name.add("!delflag");
                value.add(1);
                requestParams.clear();
                requestParams.put("filter_names", name);
                requestParams.put("filter_values", value);
                result = hrmsRecJobDAOObj.getPositionmain(requestParams);
                if(!result.getEntityList().isEmpty())
                    checkflag = false;
                if (checkflag) {

                    requestParams = new HashMap<String, Object>();
                    requestParams.put("masterid", request.getParameter("position"));
                    requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    result = hrmsCommonDAOObj.getMasterData(requestParams);
                    MasterData md = null;
                    if(result.getEntityList()!=null&&result.getEntityList().size()>0)
                        md = (MasterData)result.getEntityList().get(0);

                    requestParams = new HashMap<String, Object>();
                    requestParams.put("masterid", request.getParameter("department"));
                    requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    result = hrmsCommonDAOObj.getMasterData(requestParams);
                    MasterData dept = null;
                    if(result.getEntityList()!=null&&result.getEntityList().size()>0)
                        dept = (MasterData)result.getEntityList().get(0);
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("empid", request.getParameter("manager"));
                    result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                    User man = null;
                    if(result.getEntityList()!=null&&result.getEntityList().size()>0)
                        man = (User)result.getEntityList().get(0);
                    date = (Date) fmt.parse(request.getParameter("startdate"));

                    requestParams = new HashMap<String, Object>();
                    requestParams.put("empid", sessionHandlerImplObj.getUserid(request));
                    result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                    User createdby = null;
                    if(result.getEntityList()!=null&&result.getEntityList().size()>0)
                        createdby = (User)result.getEntityList().get(0);

                    requestParams = new HashMap<String, Object>();
                    requestParams.put("startdate",request.getParameter("startdate"));
                    requestParams.put("enddate",request.getParameter("enddate"));
                    requestParams.put("position",md);
                    requestParams.put("details",request.getParameter("details"));
                    requestParams.put("jobtype",request.getParameter("jobtype"));
                    requestParams.put("jobidwthformat",Integer.parseInt(request.getParameter("jobid")));
                    HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                    requestParams1.put("companyid", sessionHandlerImplObj.getCompanyid(request));
                    requestParams1.put("jobid", request.getParameter("jobid"));
                    requestParams1.put("jobidformat", request.getParameter("jobidformat"));
                    requestParams.put("jobid", profileHandlerDAOObj.getJobIdFormatEdit(requestParams1));
                    requestParams.put("delflag",0);
                    requestParams.put("company",company);
                    requestParams.put("manager",man);
                    requestParams.put("departmentid",dept);
                    requestParams.put("noofpos",Integer.parseInt(request.getParameter("nopos")));
                    requestParams.put("createdby",createdby);
                    requestParams.put("positionsfilled",0);
                    hrmsRecJobDAOObj.setPositionmain(requestParams);

                    jobj.put("message", messageSource.getMessage("hrms.recruitment.Jobpositionaddedsuccessfully",null,"Job position added successfully.", RequestContextUtils.getLocale(request)));
                } else {
                    jobj.put("message", messageSource.getMessage("hrms.recruitment.Jobpositionalreadypresent",null,"Job position already present.", RequestContextUtils.getLocale(request)));
                }
            } else {
                //Positionmain posmain = (Positionmain) session.load(Positionmain.class, request.getParameter("posid"));
                requestParams = new HashMap<String, Object>();
                requestParams.put("empid", request.getParameter("manager"));
                result = hrmsCommonDAOObj.getUserDetailsbyUserid(requestParams);
                User man = null;
                if(result.getEntityList()!=null&&result.getEntityList().size()>0)
                    man = (User)result.getEntityList().get(0);


//                requestParams = new HashMap<String, Object>();
//                requestParams.put("positionid",request.getParameter("posid"));
                ArrayList <String> name = new ArrayList <String>();
                ArrayList <Object> value = new ArrayList <Object>();

                name.add("positionid");
                value.add(request.getParameter("posid"));
                requestParams.clear();
                requestParams.put("filter_names", name);
                requestParams.put("filter_values", value);
                result = hrmsRecJobDAOObj.getPositionmain(requestParams);
                Positionmain posmain = null;
                requestParams = new HashMap<String, Object>();
                requestParams.put("positionid",request.getParameter("posid"));
                if(result.getEntityList()!=null&&result.getEntityList().size()>0)
                    posmain = (Positionmain)result.getEntityList().get(0);



                if (request.getParameter("details")!=null) {
                    requestParams.put("details",request.getParameter("details"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("manager"))) {
                    requestParams.put("manager",man);
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                    requestParams.put("departmentid",request.getParameter("department"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("startdate"))) {
                    //date = (Date) fmt.parse(request.getParameter("startdate"));
                    requestParams.put("startdate",request.getParameter("startdate"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                    date = (Date) fmt.parse(request.getParameter("enddate"));
                    if(new Date(fmt.format(new Date())).after(date)){
                        requestParams.put("delflag",2);
                    }else{
                        requestParams.put("delflag",0);
                    }
                    requestParams.put("enddate",request.getParameter("enddate"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("nopos"))) {
                    if(posmain.getPositionsfilled()==Integer.parseInt(request.getParameter("nopos"))){
                        requestParams.put("delflag",3);
                    }
                    requestParams.put("noofpos",Integer.parseInt(request.getParameter("nopos")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("jobshift"))) {
                    requestParams.put("jobshift",request.getParameter("jobshift"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("location"))) {
                    requestParams.put("location",request.getParameter("location"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("relocation"))) {
                    requestParams.put("relocation",request.getParameter("relocation"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("expmonth"))) {
                    requestParams.put("experiencemonth",Integer.parseInt(request.getParameter("expmonth")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("expyear"))) {
                    requestParams.put("experienceyear",Integer.parseInt(request.getParameter("expyear")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("travel"))) {
                    requestParams.put("travel",request.getParameter("travel"));
                }
                result = hrmsRecJobDAOObj.updatePositionmain(requestParams);
                jobj.put("message", messageSource.getMessage("hrms.recruitment.Jobpositionupdatedsuccessfully",null,"Job position updated successfully.", RequestContextUtils.getLocale(request)));
            }
//            ProfileHandler.insertAuditLog(session,
//                    ("Internal".equals(request.getParameter("jobtype")) ? AuditAction.INTERNAL_JOB_ADDED : AuditAction.EXTERNAL_JOB_ADDED),
//                    "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added " + request.getParameter("jobtype") + " job", request);

            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);

        } catch (ParseException ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        } catch (SessionExpiredException ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        } catch (JSONException ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
        
    }

    public ModelAndView viewjobprofileFunction(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {

//            String hql = "from Jobprofile where position.positionid=? and type=?";
//            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("position"), Integer.parseInt(request.getParameter("type"))});
//            Iterator ite = lst.iterator();
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("positionid", request.getParameter("position"));
//            requestParams.put("type", request.getParameter("type"));
            ArrayList name = new ArrayList();
            ArrayList value = new ArrayList();
            name.add("position.positionid");
            value.add(request.getParameter("position"));
            name.add("type");
            value.add(Integer.parseInt(request.getParameter("type").toString()));
            result = hrmsRecJobDAOObj.getJobProfile(name,value);
            List lst = result.getEntityList();
            for (Integer ctr=0;ctr<lst.size();ctr++) {
                Jobprofile job = (Jobprofile) lst.get(ctr);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", job.getId());
                tmpObj.put("qualification", job.getQualification());
                tmpObj.put("qualificationdesc", job.getQualificationdesc());
                tmpObj.put("responsibility", job.getResponsibility());
                tmpObj.put("skill", job.getSkill());
                tmpObj.put("skilldesc", job.getSkilldesc());
                jarr.put(tmpObj);
            }
            jobj.put("count", lst.size());
            jobj.put((String) Jobprofile.jobmeta.get(Integer.parseInt(request.getParameter("type").toString())), jarr);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    public JSONObject getviewjobprofiledata(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try {


            ArrayList name = new ArrayList();
            ArrayList value = new ArrayList();
            name.add("position.positionid");
            value.add(request.getParameter("position"));
            result = hrmsRecJobDAOObj.getJobProfile(name,value);
            List lst = result.getEntityList();
            for (Integer ctr=0;ctr<lst.size();ctr++) {
                Jobprofile job = (Jobprofile) lst.get(ctr);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", job.getId());
                tmpObj.put("qualification", job.getQualification());
                tmpObj.put("qualificationdesc", job.getQualificationdesc());
                tmpObj.put("responsibility", job.getResponsibility());
                tmpObj.put("skill", job.getSkill());
                tmpObj.put("skilldesc", job.getSkilldesc());
                jobj.append((String) Jobprofile.jobmeta.get(job.getType()), tmpObj);
            }

            for(int i=1;i<=Jobprofile.jobmeta.size();i++){
                if(jobj.isNull((String) Jobprofile.jobmeta.get(i))){
                    jobj.put((String) Jobprofile.jobmeta.get(i), new JSONArray());
                }
            }
            jobj.put("count", lst.size());
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return jobj1;
        }
    }
    public ModelAndView getjobprofileFunction(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        String hql = "";
        int count = 0;
        List lst;
        try {
//            Company cmp=(Company)session.get(Company.class,AuthHandler.getCompanyid(request));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            CompanyPreferences cmp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", sessionHandlerImplObj.getCompanyid(request));
            String jobidformat=(cmp.getJobidformat()==null?"":cmp.getJobidformat());
//            hql = "from Positionmain where positionid=?";
//            lst = HibernateUtil.executeQuery(session, hql, request.getParameter("position"));
//            count = lst.size();
//            Iterator ite = lst.iterator();
//            requestParams = new HashMap<String, Object>();
//            requestParams.put("positionid", request.getParameter("position"));
//            result = hrmsRecJobDAOObj.getPositionmainbyposid(requestParams);
            ArrayList name = new ArrayList();
            ArrayList value = new ArrayList();
            name.add("positionid");
            value.add(request.getParameter("position"));
            requestParams.clear();
            requestParams.put("filter_names", name);
            requestParams.put("filter_values", value);
            result = hrmsRecJobDAOObj.getPositionmain(requestParams);
            lst = result.getEntityList();
            for (Integer ctr=0;ctr<lst.size();ctr++) {
                JSONObject tmpObj = new JSONObject();
                Positionmain psm = (Positionmain) lst.get(ctr);
                tmpObj.put("jobid", psm.getPositionid());
                tmpObj.put("posid", psm.getJobid());
                tmpObj.put("designation", psm.getPosition().getValue());
                tmpObj.put("details", psm.getDetails());
                tmpObj.put("department", psm.getDepartmentid().getValue());
                tmpObj.put("manager", psm.getManager().getUserID());
                tmpObj.put("startdate", psm.getStartdate());
                tmpObj.put("enddate", psm.getEnddate());
                tmpObj.put("expmonth", psm.getExperiencemonth());
                tmpObj.put("expyear", psm.getExperienceyear());
                tmpObj.put("relocation", psm.getRelocation());
                tmpObj.put("location", psm.getLocation());
                tmpObj.put("jobshift", psm.getJobshift());
                tmpObj.put("travel", psm.getTravel());
                tmpObj.put("nopos", psm.getNoofpos());
                tmpObj.put("jobmeta", getviewjobprofiledata(request,response));
                jobj.put("count", result.getRecordTotalCount());
                jobj.append("data",tmpObj);
            }
            
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView DeleteInternalJobs(HttpServletRequest request, HttpServletResponse response){
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        String hql = null;
        boolean flag=true;
        ArrayList <String> name = new ArrayList<String>();
        ArrayList <Object> value = new ArrayList<Object>();
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        String title="Success";
        try {
            String delids[] = request.getParameterValues("delid");
            for (int i = 0; i < delids.length; i++) {
                name.clear();
                value.clear();
//                hql = "from Allapplications where position.positionid=? and company.companyID=? and delflag=0";
//                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{delids[i], AuthHandler.getCompanyid(request)});
                name.add("position.positionid");
                value.add(delids[i]);
                name.add("company.companyID");
                value.add(sessionHandlerImplObj.getCompanyid(request));
                name.add("delflag");
                value.add(0);
                result = hrmsRecJobDAOObj.getPositionstatus(name,value);
                tabledata = result.getEntityList();
                if (tabledata.isEmpty()) {
                    HashMap<String,Object> requestParams = new HashMap<String, Object>();

                    requestParams.put("filter_names",Arrays.asList("applypos"));
                    requestParams.put("filter_values",Arrays.asList((Positionmain) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.recruitment.Positionmain", delids[i])));
                    result = hrmsRecAgencyDAOObj.getApplyagency(requestParams);
                    if(result.getEntityList().isEmpty()) {
                        name.clear();
                        value.clear();
                        name.add("positionid");
                        value.add(delids[i]);

    //                    hql = "from Positionmain where positionid=?";
    //                    tabledata = HibernateUtil.executeQuery(session, hql, delids[i]);
                        requestParams.clear();
                        requestParams.put("filter_names", name);
                        requestParams.put("filter_values", value);
                        result = hrmsRecJobDAOObj.getPositionmain(requestParams);
                        tabledata = result.getEntityList();
                        if (!tabledata.isEmpty()) {
                            requestParams = new HashMap<String, Object>();
                            requestParams.put("delflag", 1);
                            requestParams.put("positionid", delids[i]);
                            result = hrmsRecJobDAOObj.updatePositionmain(requestParams);
    //                        Positionmain log = (Positionmain) tabledata.get(0);
    //                        log.setDelflag(1);
    //                        session.update(log);

                        }
                    } else {
                        flag=false;
                    }
                }else{
                    flag=false;
                }
            }
            if (flag) {
                jobj.put("message", messageSource.getMessage("hrms.recruitment.SelectedJobPositionsuccessfullydeleted",null,"Selected Job Position(s) successfully deleted.", RequestContextUtils.getLocale(request)));
            } else {
                jobj.put("message", messageSource.getMessage("hrms.recruitment.SomejobscannotbedeletedBeacause",null,"Some jobs have assigned applicants or are assigned to agencies and hence cannot be deleted.", RequestContextUtils.getLocale(request)));
            }
            jobj1.put("valid", true);
            jobj.put("title", title);
            jobj1.put("data", jobj.toString());
            txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView addjobprofile(HttpServletRequest request,HttpServletResponse response) {
        String id = "";
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String jsondata1 = request.getParameter("jsondataresp");
            String jsondata2= request.getParameter("jsondataskill");
            String jsondata3 = request.getParameter("jsondataqual");
            ArrayList name = new ArrayList();
            ArrayList value = new ArrayList();
            name.add("positionid");
            value.add(request.getParameter("position"));
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", name);
            requestParams.put("filter_values", value);
            result = hrmsRecJobDAOObj.getPositionmain(requestParams);
            Positionmain pos = null;
            if(result.getEntityList()!=null && result.getEntityList().size()>0)
                pos = (Positionmain)result.getEntityList().get(0);

            requestParams = new HashMap<String, Object>();
            JSONArray jarr = new JSONArray("[" + jsondata1 + "]");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                if (!StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                    id  = jobj.getString("id");
                    requestParams.put("id", id);
                }
                requestParams.put("responsibility", jobj.getString("responsibility"));
                requestParams.put("position", pos);
                requestParams.put("type", jobj.getString("type"));
                hrmsRecJobDAOObj.addJobProfile(requestParams);
                requestParams.clear();
            }

            requestParams.clear();
            jarr = new JSONArray("[" + jsondata2 + "]");
            for (int j = 0; j < jarr.length(); j++) {
                JSONObject jobj = jarr.getJSONObject(j);
                if (!StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                    id  = jobj.getString("id");
                    requestParams.put("id", id);
                }
                requestParams.put("skill", jobj.getString("skill"));
                requestParams.put("skilldesc", jobj.getString("skilldesc"));
                requestParams.put("type", jobj.getString("type"));
                requestParams.put("position", pos);
                hrmsRecJobDAOObj.addJobProfile(requestParams);
                requestParams.clear();
            }

            requestParams.clear();
            jarr = new JSONArray("[" + jsondata3 + "]");
            for (int j = 0; j < jarr.length(); j++) {
                JSONObject jobj = jarr.getJSONObject(j);
                if (!StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                    id  = jobj.getString("id");
                    requestParams.put("id", id);
                }
                requestParams.put("qualification", jobj.getString("qualification"));
                requestParams.put("qualificationdesc", jobj.getString("qualificationdesc"));
                requestParams.put("type", jobj.getString("type"));
                requestParams.put("position", pos);
                hrmsRecJobDAOObj.addJobProfile(requestParams);
                requestParams.clear();
            }
            jobj1.put("valid", true);
            jobj1.put("data", "success");
            txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    public void getdocsbyuser(HttpServletRequest request,String userid,JSONObject tmpObj) throws JSONException, SessionExpiredException {
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        KwlReturnObject result = null;
        
                ArrayList filter_names = new ArrayList();
                ArrayList filter_values = new ArrayList();
                filter_names.add("recid");
                filter_values.add(userid);

                filter_names.add("docid.deleted");
                filter_values.add(false);

                filter_names.add("docid.referenceid");
                filter_values.add(userid);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                requestParams.put("start", 0);
                requestParams.put("limit", 2);
                requestParams.put("searchcol", new String[]{"docid.docname"});
                requestParams.put("allflag", false);
                result = hrmsExtApplDocsDAOObj.getDocs(requestParams);

                Iterator ite = result.getEntityList().iterator();
                if (ite.hasNext()) {
                    HrmsDocmap docs = (HrmsDocmap) ite.next();
                    tmpObj.put("docid", docs.getDocid().getDocid());
                }
    }
    public ModelAndView getJobApplications(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null,recruitresult = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr =  new JSONArray();
        String hql = "";
        int count = 0;
        boolean isadmin = false;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
    	int type = 0;
        try {
        	String Searchjson = request.getParameter("searchJson");
            List lst = null;
            List lst1 = null;
            String cmpid=sessionHandlerImplObj.getCompanyid(request);
            String userid=sessionHandlerImplObj.getUserid(request);
            requestParams.clear();
            requestParams.put("userid",userid);
            if(hrmsCommonDAOObj.isAdmin(userid)){
                isadmin = true;
            }
            ArrayList params = new ArrayList();
            ArrayList filter_name = new ArrayList();
            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();
            String ss = request.getParameter("ss");
            int statusid=StringUtil.isNullOrEmpty(request.getParameter("statusid"))?-1:Integer.parseInt(request.getParameter("statusid"));
            String tempstatus=statusid==1?"Pending":(statusid==2?"Shortlisted":(statusid==3?"In Process":(statusid==4?"On Hold":"")));
            if (StringUtil.isNullOrEmpty(request.getParameter("userid"))) {
                int emptype = Integer.parseInt(request.getParameter("employeetype"));
            	type = emptype;
                int gridst=Integer.parseInt(request.getParameter("gridst"));
                
                String[] searchArray = null;
                String[] searchArray1 = null;
                if(emptype == 1) {//Internal
                    searchArray = new String[]{"employee.firstName","employee.lastName","position.jobid","position.departmentid.value"};
                    params.add(0);
                    filter_name.add("employee.deleteflag");
                }else if(emptype == 2) { // else added to execute only one condition
                    emptype = 1;
                    searchArray = new String[]{"employee.firstName","employee.lastName","position.jobid","position.departmentid.value"};
                    searchArray1 = new String[]{"configjobapplicant.col1", "configjobapplicant.col2","position.jobid","position.departmentid.value"};
                    params.add(0);
                    filter_name.add("employee.deleteflag");
                    params.add(false);
                    filter_name.add("configjobapplicant.deleted");
                } else {//External
                    searchArray = new String[]{"configjobapplicant.col1", "configjobapplicant.col2","position.jobid","position.departmentid.value"};
                    params.add(false);
                    filter_name.add("configjobapplicant.deleted");
                }
                if(emptype==0){
                    emptype = 4; //configjobapplicant=4 reset to 0
                }
                if(StringUtil.isNullOrEmpty(request.getParameter("status"))){
                    params.add(gridst);
                    params.add(cmpid);
                    params.add(0);
                    params.add(emptype);
                    //params.add(false);
                    filter_name.add("applicationflag");
                    filter_name.add("company.companyID");
                    filter_name.add("delflag");
                    filter_name.add("employeetype");
                    //filter_name.add("configjobapplicant.deleted");
                } else {
                        if (!tempstatus.equals("")) {
                            params.add(gridst);
                            params.add(cmpid);
                            params.add(0);
                            params.add(tempstatus);
                            params.add(emptype);
                            filter_name.add("applicationflag");
                            filter_name.add("company.companyID");
                            filter_name.add("delflag");
                            filter_name.add("status");
                            filter_name.add("employeetype");
                        } else {
                            params.add(gridst);
                            params.add(cmpid);
                            params.add(0);
                            params.add(emptype);
                            filter_name.add("applicationflag");
                            filter_name.add("company.companyID");
                            filter_name.add("delflag");
                            filter_name.add("employeetype");
                        }
                }
                if(!isadmin){
                    params.add(userid);
                    filter_name.add("position.manager.userID");
                }
              order_by.add("position.departmentid.value");
              order_by.add("position.jobid");
              order_type.add("");
              order_type.add("asc");
              requestParams.put("filter_names", filter_name);
              requestParams.put("filter_values", params);

              ArrayList params1 = new ArrayList(params);
              ArrayList filter_name1 = new ArrayList(filter_name);
              if(filter_name1.contains("employee.deleteflag")){
            	  params1.remove(filter_name1.indexOf("employee.deleteflag"));
            	  filter_name1.remove("employee.deleteflag");
              }
              if(filter_name.contains("configjobapplicant.deleted") && !(emptype==4)){
            	  params.remove(filter_name.indexOf("configjobapplicant.deleted"));
            	  filter_name.remove("configjobapplicant.deleted");
              }
              requestParams.put("filter_names1", filter_name1);
              requestParams.put("filter_values1", params1);

              requestParams.put("order_by", order_by);
              requestParams.put("order_type", order_type);
              requestParams.put("ss", request.getParameter("ss"));
              requestParams.put("searchcol", searchArray);
              requestParams.put("searchcol1", searchArray1);
              requestParams.put("allflag", false);
              if (!StringUtil.isNullOrEmpty(Searchjson)) {
                  getMyAdvanceSearchparams1(Searchjson, filter_name, (type!=2?type:1));
                  insertParamAdvanceSearchString1(params, Searchjson, type);
                  getMyAdvanceSearchparams1(Searchjson, filter_name1, (type!=2?type:0));
                  insertParamAdvanceSearchString1(params1, Searchjson, type);
              }
              StringUtil.checkpaging(requestParams, request);
              result = hrmsRecJobDAOObj.getPositionstatus(requestParams);
            } else {
                  params.add(request.getParameter("userid"));
                  params.add(cmpid);
                  params.add(0);
                  params.add(false);
                  filter_name.add("configjobapplicant.id");
                  filter_name.add("company.companyID");
                  filter_name.add("delflag");
                  filter_name.add("configjobapplicant.deleted");
                  order_by.add("position.departmentid.value");
                  order_by.add("position.jobid");
                  order_type.add("");
                  order_type.add("asc");
                  requestParams.put("filter_names", filter_name);
                  requestParams.put("filter_values", params);
                  requestParams.put("order_by", order_by);
                  requestParams.put("order_type", order_type);
                  requestParams.put("ss", request.getParameter("ss"));
                  requestParams.put("searchcol", new String[]{"position.jobid","position.position.value"});
                  requestParams.put("allflag", false);
                  if (!StringUtil.isNullOrEmpty(Searchjson)) {
                      getMyAdvanceSearchparams1(Searchjson, filter_name, type);
                      insertParamAdvanceSearchString1(params, Searchjson, type);
                  }
                  StringUtil.checkpaging(requestParams, request);
                  result = hrmsRecJobDAOObj.getPositionstatus(requestParams);

            }
            Allapplications allapps = null;
            count = result.getRecordTotalCount();
            lst = result.getEntityList();
            for (int ctr=0;ctr<lst.size();ctr++) {
//                if(isadmin){
                    allapps=(Allapplications) lst.get(ctr);
//                }else{
//                    Recruiter rec=(Recruiter) lst.get(ctr);
//                    allapps = rec.getAllapplication();
//                }

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", allapps.getId());
                tmpObj.put("posid", allapps.getPosition().getPositionid());
                tmpObj.put("rejectedbefore", allapps.getRejectedbefore());
                tmpObj.put("designationid", allapps.getPosition().getPosition().getId());
                tmpObj.put("departmentid", allapps.getPosition().getDepartmentid().getId());
                tmpObj.put("department", allapps.getPosition().getDepartmentid().getValue());
                tmpObj.put("Department", allapps.getPosition().getDepartmentid().getValue());
                tmpObj.put("designation", allapps.getPosition().getPosition().getValue());
                tmpObj.put("jobpositionid", allapps.getPosition().getJobid());
                tmpObj.put("jobid", allapps.getPosition().getPositionid());
                tmpObj.put("JobId", allapps.getPosition().getPosition().getValue());
                tmpObj.put("vacancy", allapps.getPosition().getNoofpos());
                tmpObj.put("filled", allapps.getPosition().getPositionsfilled());
                tmpObj.put("jname", allapps.getPosition().getPosition().getValue());
                tmpObj.put("applydt", sessionHandlerImplObj.getDateFormatter(request).format(allapps.getApplydate()));
                tmpObj.put("interviewdt", (allapps.getInterviewdate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(allapps.getInterviewdate())));
                tmpObj.put("joiningdate", (allapps.getJoiningdate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(allapps.getJoiningdate())));
                tmpObj.put("status", allapps.getStatus());
                tmpObj.put("jobDetails", allapps.getPosition().getDetails());
                if (allapps.getConfigjobapplicant() != null) {
                    tmpObj.put("apcntid", allapps.getConfigjobapplicant().getId());
                    tmpObj.put("cname", allapps.getConfigjobapplicant().getCol1() + " " + allapps.getConfigjobapplicant().getCol2());
                    tmpObj.put("email", allapps.getConfigjobapplicant().getCol3());
                    tmpObj.put("fname", allapps.getConfigjobapplicant().getCol1());
                    tmpObj.put("lname", allapps.getConfigjobapplicant().getCol2());
                    tmpObj.put("contact", allapps.getConfigjobapplicant().getCol4()!=null?allapps.getConfigjobapplicant().getCol4():"");
                    tmpObj.put("addr", allapps.getConfigjobapplicant().getCol6()!=null?allapps.getConfigjobapplicant().getCol6():"");
                    tmpObj.put("employeetype", 0);
                    if(!StringUtil.isNullOrEmpty(allapps.getConfigjobapplicant().getCol5())){
                        tmpObj.put("docid", allapps.getConfigjobapplicant().getCol5());
                    }
//                    getdocsbyuser(request,allapps.getConfigjobapplicant().getId(),tmpObj);
                }else{
                    tmpObj.put("apcntid", allapps.getEmployee().getUserID());
                    tmpObj.put("cname", allapps.getEmployee().getFirstName() + " " + allapps.getEmployee().getLastName());
                    tmpObj.put("email", allapps.getEmployee().getEmailID());
                    tmpObj.put("fname", allapps.getEmployee().getFirstName());
                    tmpObj.put("lname", allapps.getEmployee().getLastName());
                    tmpObj.put("contact", allapps.getEmployee().getContactNumber());
                    tmpObj.put("addr", allapps.getEmployee().getAddress());
                    tmpObj.put("employeetype", 1);
                }

                    ArrayList recruiterparams = new ArrayList();
                    recruiterparams.add(allapps.getId());
                    ArrayList filter_names = new ArrayList();
                    filter_names.add("allapplication.id");
                    requestParams.clear();
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", recruiterparams);
                    recruitresult = hrmsRecJobDAOObj.getRecruiters(requestParams);
                    if (StringUtil.checkResultobjList(recruitresult)) {
                        List recruiterlist = recruitresult.getEntityList();
                        for (int k = 0; k < recruiterlist.size(); k++) {
                            Recruiter r = (Recruiter) recruiterlist.get(k);
                            tmpObj.append("recruiter", r.getRecruit().getFirstName() + " " + r.getRecruit().getLastName());
                        }
                    }
                
                    tmpObj.put("callback",(allapps.getCallback() == null ? "" :allapps.getCallback().getValue()));
                    tmpObj.put("interviewplace", allapps.getInterviewplace());
                    tmpObj.put("interviewcomment", allapps.getInterviewcomment());
                    tmpObj.put("rank",(allapps.getRank() == null ? "" :allapps.getRank().getValue()));
                    jarr.put(tmpObj);
                }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
 
    public ModelAndView getAllUserDetails(HttpServletRequest request, HttpServletResponse response) throws JSONException{
        JSONObject jobj = new JSONObject();
        int count;
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        KwlReturnObject result = null;
        try {

            ArrayList params = new ArrayList();
            ArrayList filter_names = new ArrayList();
            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();
            filter_names.add("ua.user.company.companyID");
            filter_names.add("ua.user.deleteflag");
            params.add(sessionHandlerImplObj.getCompanyid(request));
            params.add(0);

            // @@ useraccount

//            if(!StringUtil.isNullOrEmpty(request.getParameter("combo"))){
//
//                order_by.add("u.firstName");
//                order_type.add("asc");
//            }else{
//
//                order_by.add("u.employeeid");
//                order_type.add("asc");
//            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", params);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("searchcol", new String[]{"u.firstName","u.lastName"});
//            requestParams.put("start", Integer.valueOf(request.getParameter("start")));
//            requestParams.put("limit", Integer.valueOf(request.getParameter("limit")));
            result = hrmsCommonDAOObj.getEmpprofileuser(requestParams);
            List list = result.getEntityList();
            count = result.getRecordTotalCount();
            for (int ctr=0;ctr< count;ctr++) {
                Object[] row = (Object[]) list.get(ctr);
                JSONObject obj = new JSONObject();
                Empprofile e = null;
                Useraccount ua = (Useraccount) row[1];
                User u = ua.getUser();
                if (row[0] != null) {
                    e = (Empprofile) row[0];
                    if (!StringUtil.isNullOrEmpty(e.getStatus())) {
                        obj.put("status", e.getStatus());
                    } else {
                        obj.put("status", "Pending");
                    }
                } else {
                    obj.put("status", "Incomplete");
                }
                obj.put("department", (ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                obj.put("departmentname", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                obj.put("role", (ua.getRole() == null ? "" : ua.getRole().getID()));
                String name="";
                if(ua.getRole()!=null&&ua.getRole().getCompany()!=null){
                	name = ua.getRole().getName();
                }else{
                	name = messageSource.getMessage("hrms.common.role."+ua.getRole().getID(),null, ua.getRole().getName(), RequestContextUtils.getLocale(request));
                }
                obj.put("rolename", (ua.getRole() == null ? "" : name));
                obj.put("userid", u.getUserID());
                obj.put("username", u.getUserLogin().getUserName());
                obj.put("fname", u.getFirstName());
                obj.put("lname", u.getLastName());
                obj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));
                obj.put("image", u.getImage());
                obj.put("emailid", u.getEmailID());
                obj.put("lastlogin", (u.getUserLogin().getLastActivityDate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(u.getUserLogin().getLastActivityDate())));
                obj.put("aboutuser", u.getAboutUser());
                obj.put("address", u.getAddress());
                obj.put("contactno", u.getContactNumber());
                obj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                obj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                obj.put("salary", ua.getSalary());
                obj.put("accno", ua.getAccno());
                obj.put("templateid", ua.getTemplateid()!=null?ua.getTemplateid():"");
                requestParams.clear();
                requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("empid",ua.getEmployeeid());
//                result = profileHandlerDAOObj.getEmpidFormatEdit(requestParams);
                obj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));

//                List lst1 = HibernateUtil.executeQuery(session, "from  Assignmanager where assignemp.userID=? and managerstatus=1", u.getUserID());
//                Iterator itr1 = lst1.iterator();
                requestParams.clear();
//                requestParams.put("userid", u.getUserID());
//                requestParams.put("managerstatus", 1);
                filter_names.clear();
                params.clear();
                filter_names.add("assignemp.userID");
                params.add(u.getUserID());

                filter_names.add("managerstatus");
                params.add(1);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", params);

                result = hrmsCommonDAOObj.getAssignmanager(requestParams);
                List lst1 = result.getEntityList();
                for (int cnt=0;cnt<lst1.size();cnt++) {
                        Assignmanager asm = (Assignmanager) lst1.get(cnt);
                        if (asm.getAssignman() != null) {
                            obj.append("managerid", asm.getAssignman().getUserID());
                            obj.append("manager", asm.getAssignman().getFirstName() + " " + asm.getAssignman().getLastName());
                        }
                }
                if(lst1.size()==0){
                    obj.put("manager", " ");
                    obj.put("managerid", " ");
                }
                jobj.append("data", obj);
//                jArr.put(obj);
//                lst1 = HibernateUtil.executeQuery(session, "from  Assignreviewer where employee.userID=? and reviewerstatus=1", u.getUserID());
//                itr1 = lst1.iterator();
                requestParams.clear();
                requestParams.put("userid", u.getUserID());
                requestParams.put("reviewerstatus", 1);
                result = hrmsCommonDAOObj.getAssignreviewer(requestParams);
                lst1 = result.getEntityList();
                for(int cnt1=0;cnt1<lst1.size();cnt1++){
                        Assignreviewer rev = (Assignreviewer) lst1.get(cnt1);
                        if (rev.getReviewer() != null) {
                            obj.append("reviewerid", rev.getReviewer().getUserID());
                            obj.append("reviewer", rev.getReviewer().getFirstName() + " " + rev.getReviewer().getLastName());
                        }
                }
                if(lst1.size()==0){
                    obj.put("reviewer", " ");
                    obj.put("reviewerid", " ");
                }
                jobj.append("data", obj);
            }
            jobj.put("count", count);
            
        } catch (Exception e) {
            System.out.print(e);
            
        } finally {
            if(!jobj.has("data")) {
                jobj.put("count", 0);
                jobj.put("data", "");
            }
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getRecruiter(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException,JSONException {
        KwlReturnObject result = null;
        List tabledata = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        int cnt = 0;
        try {
            String cmpid=sessionHandlerImplObj.getCompanyid(request);
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("delflag");
            filter_names.add("recruit.deleteflag");
            filter_names.add("recruit.company.companyID");

            filter_values.add(1);
            filter_values.add(0);
            filter_values.add(cmpid);

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = hrmsRecJobDAOObj.getRecruiter(requestParams);
            tabledata = result.getEntityList();
            cnt = result.getRecordTotalCount();
            for (int i = 0; i < cnt; i++) {
                Recruiter log = (Recruiter) tabledata.get(i);
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", log.getRecruit().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("rid", ua.getUserID());
                tmpObj.put("fname", log.getRecruit().getFirstName());
                tmpObj.put("lname", log.getRecruit().getLastName());
                tmpObj.put("fullname", log.getRecruit().getFirstName() + " " + (log.getRecruit().getLastName() == null ? "" : log.getRecruit().getLastName()));
                tmpObj.put("name", log.getRecruit().getFirstName() + " " + (log.getRecruit().getLastName() == null ? "" : log.getRecruit().getLastName()));
                //tmpObj.put("desig", log.getRecruit().getDesignation());
                tmpObj.put("desig",(ua.getDesignationid()==null?" ":ua.getDesignationid().getValue()));
                jobj.append("data", tmpObj);
            }
            if(!jobj.has("data")) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj.put("count", cnt);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsManager.viewRecruitersFunction", e);
        }catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.viewRecruitersFunction", e);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView scheduleinterview(HttpServletRequest request, HttpServletResponse response) {
        Date date1;
        DateFormat formatter;
        JSONObject jobj = new JSONObject();
        String interviewdate = "";
        //boolean reflag=false;
        String htmlmsg="";
        String pmsg="";
        String interviewsub="";
        KwlReturnObject result = null,recruitresult = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            String interviewtime=request.getParameter("interviewtime");
            String location=request.getParameter("interviewplace");
            formatter = new SimpleDateFormat("MM/dd/yyyy");
//            Company cmp=(Company) session.get(Company.class,AuthHandler.getCompanyid(request));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            result = profileHandlerDAOObj.getCompanyid(requestParams);
            Company cmp = null;
            if(StringUtil.checkResultobjList(result)){
                cmp = (Company) result.getEntityList().get(0);
            }

            date1 = (Date) formatter.parse(request.getParameter("interviewdt"));
            //interviewdate=(AuthHandler.getUserDateFormatter(request,session).format(date1));
            requestParams.clear();
            requestParams.put("request", request);
            interviewdate=request.getParameter("interviewdt");
            
            Allapplications allapl = null;
            String[] ids = request.getParameterValues("ids");
            String[] emailids=new String[ids.length] ;
            String[] Resumeids=new String[ids.length] ;
            String[] jobs=new String[ids.length] ;
            String[] applicant=new String[ids.length] ;
            Boolean[] reflag=new Boolean[ids.length];
            for (int i = 0; i < ids.length; i++) {
                requestParams.clear();
                requestParams.put("id", ids[i]);
                requestParams.put("primary", true);
                result = hrmsRecJobDAOObj.getPositionstatus(requestParams);
                allapl = (Allapplications) result.getEntityList().get(0);
                if(allapl.getStatus().equalsIgnoreCase("In Process")){
                    reflag[i]=true;
                }else{
                     reflag[i]=false;
                }
                requestParams.clear();
                requestParams.put("id", ids[i]);
                requestParams.put("status", "In Process");
                requestParams.put("interviewdate", date1);
                requestParams.put("interviewtime", interviewtime);
                requestParams.put("interviewplace", location);
                requestParams.put("contactperson", request.getParameter("contactperson"));
                requestParams.put("interviewcomment", request.getParameter("interviewcomment"));
                //set rid to recruiter table and remove recruiter column from allapp
                requestParams.put("recruiter", request.getParameter("rid"));
                result = hrmsRecJobDAOObj.addAllapplications(requestParams);
                if(StringUtil.checkResultobjList(result)){
                    allapl = (Allapplications)result.getEntityList().get(0);
                    requestParams.clear();
                    String[] Recruiterids = request.getParameter("rid").split(",");
                    requestParams.put("Allapplication",allapl.getId());
                    boolean success = hrmsRecJobDAOObj.deleteRecruiters(requestParams);
                    requestParams.put("Delflag",0);
                    for(int j=0;j<Recruiterids.length && success;j++){
                        requestParams.put("Recruit",Recruiterids[j]);
                        recruitresult = hrmsRecJobDAOObj.setRecruiters(requestParams);
                    }
                }
                
                jobs[i]=(allapl.getPosition().getPosition().getValue()+" ["+allapl.getPosition().getJobid()+"] ");
                
                if(Integer.parseInt(request.getParameter("employeetype"))==1){
                    emailids[i]=allapl.getEmployee().getEmailID();
                    applicant[i]=(allapl.getEmployee().getFirstName()+" "+allapl.getEmployee().getLastName());
                }else{
                    emailids[i]=allapl.getConfigjobapplicant().getCol3();
                    applicant[i]=(allapl.getConfigjobapplicant().getCol1()+" "+allapl.getConfigjobapplicant().getCol2());
                    Resumeids[i] = allapl.getConfigjobapplicant().getCol5();
                }
                String usrnm;
                if(allapl.getEmployee()!=null){
                    usrnm=StringUtil.getFullName(allapl.getEmployee());
                }
                else{
                    usrnm=allapl.getConfigjobapplicant().getCol1()+" "+allapl.getConfigjobapplicant().getCol2();
                }
                //@@ProfileHandler.insertAuditLog(session, AuditAction.INTERVIEW_SCHEDULED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has scheduled interview for " + usrnm + " on "+ interviewdate + " at " + allapl.getInterviewplace(),request);
            }
//             User usr=(User) session.get(User.class,AuthHandler.getUserid(request));
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", sessionHandlerImplObj.getUserid(request));
                User usr = ua.getUser();
                if (!StringUtil.isNullOrEmpty(request.getParameter("mail"))) {
                for (int j = 0; j < emailids.length; j++) {
                    if (reflag[j]) {
                        pmsg = String.format(HrmsMsgs.rescheduleinterviewPlnmsg, applicant[j], jobs[j], cmp.getCompanyName(), interviewdate, interviewtime,
                                location, usr.getContactNumber(), usr.getEmailID(), usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                        htmlmsg = String.format(HrmsMsgs.rescheduleinterviewHTMLmsg, applicant[j], jobs[j], cmp.getCompanyName(), interviewdate, interviewtime,
                                location, usr.getContactNumber(), usr.getEmailID(), usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                        interviewsub = String.format(HrmsMsgs.rescheduleinterviewSubject, allapl.getPosition().getJobid(), allapl.getPosition().getPosition().getValue(), cmp.getCompanyName());
                        try {
                            SendMailHandler.postMail(new String[]{emailids[j]}, interviewsub, htmlmsg, pmsg, usr.getEmailID());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        pmsg = String.format(HrmsMsgs.interviewPlnmsg, jobs[j], interviewdate, interviewtime, location);
                        htmlmsg = String.format(HrmsMsgs.interviewHTMLmsg, applicant[j], jobs[j], cmp.getCompanyName(), interviewdate, interviewtime,
                                location, usr.getContactNumber(), usr.getEmailID(), usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                        interviewsub = String.format(HrmsMsgs.interviewSubject, allapl.getPosition().getJobid(), allapl.getPosition().getPosition().getValue(), cmp.getCompanyName());
                        try {
                            SendMailHandler.postMail(new String[]{emailids[j]}, interviewsub, htmlmsg, pmsg, usr.getEmailID());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    ArrayList params = new ArrayList();
                    params.add(allapl.getId());
                    ArrayList filter_names = new ArrayList();
                    filter_names.add("allapplication.id");
                    requestParams.clear();
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", params);
                    recruitresult = hrmsRecJobDAOObj.getRecruiters(requestParams);
                    if (StringUtil.checkResultobjList(recruitresult)) {
                        List recruiterlist = recruitresult.getEntityList();
                        for (int k = 0; k < recruiterlist.size(); k++) {
                            Recruiter r = (Recruiter) recruiterlist.get(k);
                            String interviewer = r.getRecruit().getFirstName() + " " + r.getRecruit().getLastName();
                            String intpmsg = String.format(HrmsMsgs.interviewinvitePlnmsg, interviewer, jobs[j], interviewdate, interviewtime, location);
                            String inthtmlmsg = String.format(HrmsMsgs.interviewinviteHTMLmsg, interviewer, jobs[j], interviewdate, interviewtime,
                                    location, usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                            String interviewinvitesub = String.format(HrmsMsgs.interviewinviteSubject, allapl.getPosition().getJobid(), allapl.getPosition().getPosition().getValue(), cmp.getCompanyName());

                            
                            if (StringUtil.isNullOrEmpty(Resumeids[j])) {
                                try {
                                    SendMailHandler.postMail(new String[]{r.getRecruit().getEmailID()}, interviewinvitesub, inthtmlmsg, intpmsg, usr.getEmailID());
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                    hrmsDocumentController hdc =new hrmsDocumentController();
                                    KwlReturnObject kmsg = hrmsExtApplDocsDAOObj.downloadDocument(Resumeids[j]);
                                    HashMap ht = hdc.getDocInfo(kmsg,storageHandlerImplObj);
                                try {
                                    if(ht!=null && ht.containsKey("filename")){
                                        SendMailHandler.postMail(new String[]{r.getRecruit().getEmailID()}, interviewinvitesub, inthtmlmsg, intpmsg, usr.getEmailID(),new String[]{(String)ht.get("attachment"),(String)ht.get("filename")});
                                    }else{
                                       SendMailHandler.postMail(new String[]{r.getRecruit().getEmailID()}, interviewinvitesub, inthtmlmsg, intpmsg, usr.getEmailID());
                                    }
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                }
                            }
                            
                            
                        }
                    }
                }
            }
            if(result.isSuccessFlag()){
            	String interviewersname="";
            	String delimiter = ",";
            	String[] temp = request.getParameter("rid").split(delimiter);
            	for(int i=0;i<temp.length;i++){
            		interviewersname+=profileHandlerDAOObj.getUserFullName(temp[i]);
            		if(temp.length>i+1)
            			interviewersname+=", ";
            	}
            	for(int i=0;i<ids.length;i++)
            		auditTrailDAOObj.insertAuditLog(AuditAction.INTERVIEW_SCHEDULED, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has scheduled interview for applicant "+request.getParameterValues("cname")[i]+". Interviewers are "+interviewersname, request, "0");
            }
            jobj.put("success", "true");
            txnManager.commit(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
        finally {
            return new ModelAndView("jsonView","model","");
        }
    }
    
    public ModelAndView deleteAllappliations(HttpServletRequest request, HttpServletResponse response){
        List tabledata = null;
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
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("id", ids[i]);
                hrmsRecJobDAOObj.deleteAllapplications(requestParams);
            }
            jobj.put("success", "true");
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
            txnManager.commit(status);

        } catch (Exception ex) {
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getapplicantdata(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        try {
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("applicantid", request.getParameter("applicantid"));
            requestParams.put("primary", true);
            result = hrmsRecJobDAOObj.getJobApplicant(requestParams);
            List list = result.getEntityList();
            for(int ctr=0;ctr<list.size();ctr++){
                Jobapplicant jobapp = (Jobapplicant) list.get(ctr);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("title", jobapp.getTitle());
                tmpObj.put("firstname", jobapp.getFirstname());
                tmpObj.put("lastname", jobapp.getLastname());
                tmpObj.put("email", jobapp.getEmail());
                tmpObj.put("otheremail", jobapp.getOtheremail());
                tmpObj.put("birthdate", jobapp.getBirthdate());
                tmpObj.put("address1", jobapp.getAddress1());
                tmpObj.put("address2", jobapp.getAddress2());
                tmpObj.put("city", jobapp.getCity());
                tmpObj.put("state", jobapp.getState());
                if (jobapp.getCountryid() != null) {
                    tmpObj.put("country", jobapp.getCountryid().getId());
                }
                tmpObj.put("contactno", jobapp.getContactno());
                tmpObj.put("mobileno", jobapp.getMobileno());
                tmpObj.put("graddegree", jobapp.getGraddegree());
                tmpObj.put("gradspecialization", jobapp.getGradspecialization());
                tmpObj.put("gradcollege", jobapp.getGradcollege());
                tmpObj.put("graduniversity", jobapp.getGraduniversity());
                tmpObj.put("gradpercent", jobapp.getGradpercent());
                tmpObj.put("gradpassdate", jobapp.getGradpassdate());
                tmpObj.put("pgqualification", jobapp.getPgqualification());
                tmpObj.put("pgspecialization", jobapp.getPgspecialization());
                tmpObj.put("pgcollege", jobapp.getPgcollege());
                tmpObj.put("pguniversity", jobapp.getPguniversity());
                tmpObj.put("pgpercent", jobapp.getPgpercent());
                tmpObj.put("pgpassdate", jobapp.getPgpassdate());
                tmpObj.put("otherqualification", jobapp.getOtherqualification());
                tmpObj.put("othername", jobapp.getOthername());
                tmpObj.put("otherdetails", jobapp.getOtherdetails());
                tmpObj.put("otherpassdate", jobapp.getOtherpassdate());
                tmpObj.put("otherpercent", jobapp.getOtherpercent());
                tmpObj.put("experiencemonth", jobapp.getExperiencemonth());
                tmpObj.put("experienceyear", jobapp.getExperienceyear());
                tmpObj.put("functionalexpertise", jobapp.getFunctionalexpertise());
                tmpObj.put("currentindustry", jobapp.getCurrentindustry());
                tmpObj.put("currentorganization", jobapp.getCurrentorganization());
                tmpObj.put("currentdesignation", jobapp.getCurrentdesignation());
                tmpObj.put("grosssalary", jobapp.getGrosssalary());
                tmpObj.put("expectedsalary", jobapp.getExpectedsalary());
                tmpObj.put("category", jobapp.getCategory());
                tmpObj.put("companyrelative", jobapp.getCompanyrelative());
                tmpObj.put("appearedbefore", jobapp.getAppearedbefore());
                tmpObj.put("interviewlocation", jobapp.getInterviewlocation());
                tmpObj.put("keyskills", jobapp.getKeyskills());
                if (jobapp.getInterviewposition() != null) {
                    tmpObj.put("interviewposition", jobapp.getInterviewposition().getId());
                }
                tmpObj.put("interviewplace", jobapp.getInterviewplace());
                tmpObj.put("interviewdate", jobapp.getInterviewdate());
                jobj.append("data", tmpObj);
            }
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception ex) {
        } finally {
            
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView editProspect(HttpServletRequest request, HttpServletResponse response) {
        Date joiningdate = null;
        DateFormat formatter;
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        String appnames = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String[] ids = request.getParameterValues("ids");
            String[] cnames = request.getParameterValues("cnames");
            String[] positionids = request.getParameterValues("positionids");
            String applicantStatus = request.getParameter("selected");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            HashMap<String,Object> appParams = new HashMap<String, Object>();
            for (int i = 0; i < ids.length; i++) {
                requestParams.clear();
                requestParams.put("positionid", positionids[i]);
                requestParams.put("primary", true);
                result = hrmsRecJobDAOObj.getPositionmain(requestParams);
                Positionmain position = null;
                if(StringUtil.checkResultobjList(result)){
                    position = (Positionmain) result.getEntityList().get(0);
                }

                requestParams.clear();
                requestParams.put("id", ids[i]);
                requestParams.put("primary", true);
                result = hrmsRecJobDAOObj.getPositionstatus(requestParams);
                appParams.put("id", ids[i]);
                Allapplications appl = null;
                if(StringUtil.checkResultobjList(result)){
                    appl = (Allapplications) result.getEntityList().get(0);
                }
//                Allapplications appl = (Allapplications) session.load(Allapplications.class, ids[i]);
//                Positionmain position = (Positionmain) session.load(Positionmain.class, positionids[i]);

                
                if (StringUtil.isNullOrEmpty(request.getParameter("callback"))==false) {
                    appParams.put("callback", request.getParameter("callback"));
//                    appl.setCallback((MasterData) session.load(MasterData.class, request.getParameter("callback")));
                }
                if (StringUtil.isNullOrEmpty(applicantStatus)) {
                } else {
//                    appl.setStatus(request.getParameter("status"));
                    appParams.put("status", applicantStatus);
                    if (applicantStatus.equalsIgnoreCase("Rejected")) {
                        if(appl.getApplicationflag()==1 && position.getPositionsfilled()>0){
                            requestParams.clear();
                            requestParams.put("positionid", positionids[i]);
                            requestParams.put("positionsfilled", position.getPositionsfilled()-1);
                            requestParams.put("delflag", 0);
                            hrmsRecJobDAOObj.updatePositionmain(requestParams);
//                            position.setPositionsfilled(position.getPositionsfilled()-1);
//                            position.setDelflag(0);
//                            session.update(position);
                        }
                        appParams.put("applicationflag", 2);
                        appParams.put("rejectedbefore", 1);
//                        appl.setApplicationflag(2);
//                        appl.setRejectedbefore(1);
                    } else if (applicantStatus.equalsIgnoreCase("Selected")) {
                    	joiningdate = (Date) formatter.parse(request.getParameter("joiningdate"));
                    	if(appl.getApplydate()!=null && joiningdate!=null && appl.getApplydate().compareTo(joiningdate)<0){
//                        appl.setApplicationflag(1);
                        appParams.put("applicationflag", 1);
                        if (Boolean.parseBoolean(request.getParameter("changeselected"))) {
                            requestParams.clear();
                            requestParams.put("positionid", positionids[i]);
                            if(position.getNoofpos()>=position.getPositionsfilled()+1){
                                if ( position.getNoofpos() == position.getPositionsfilled()+1 ) {
                                    requestParams.put("delflag", 3);
//                                    position.setDelflag(3);
                                }
//                                position.setPositionsfilled(position.getPositionsfilled() + 1);
                                requestParams.put("positionsfilled", position.getPositionsfilled() + 1);
                            }
//                            session.update(position);
                            hrmsRecJobDAOObj.updatePositionmain(requestParams);
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("joiningdate"))) {
                            joiningdate = (Date) formatter.parse(request.getParameter("joiningdate"));
//                            appl.setJoiningdate(joiningdate);
                            appParams.put("joiningdate", joiningdate);
                        }
                    	}
                    } else {
                        if(appl.getApplicationflag()==1 && position.getPositionsfilled()>=0){
                            requestParams.clear();
//                            position.setPositionsfilled(position.getPositionsfilled()-1);
//                            position.setDelflag(0);
//                            session.update(position);
                            requestParams.put("positionid", positionids[i]);
                            requestParams.put("positionsfilled", position.getPositionsfilled()-1);
                            requestParams.put("delflag", 0);
                            hrmsRecJobDAOObj.updatePositionmain(requestParams);

                        }
//                        appl.setApplicationflag(0);
                        appParams.put("applicationflag", 0);

                    }
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("rank"))==false) {
//                    appl.setRank((MasterData) session.load(MasterData.class, request.getParameter("rank")));
                    appParams.put("rank", request.getParameter("rank"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("statuscomment"))) {
//                    appl.setStatuscomment(request.getParameter("statuscomment"));
                      appParams.put("statuscomment", request.getParameter("statuscomment"));
                }
                String usrnm;
                if(appl.getEmployee()!=null){
                    usrnm=StringUtil.getFullName(appl.getEmployee());
                }
                else{
                    usrnm=appl.getConfigjobapplicant().getCol1()+" "+appl.getConfigjobapplicant().getCol2();
                }
//                session.update(appl);
                
                if(request.getParameter("selected").equals("Selected")){
                	if(appl.getApplydate()!=null && joiningdate!=null && appl.getApplydate().compareTo(joiningdate)<0){
                    	hrmsRecJobDAOObj.addAllapplications(appParams);		
                    }else{	
                    	appnames +=(cnames[i]+" ,");	
                    }
                }else{
                	hrmsRecJobDAOObj.addAllapplications(appParams);
                }
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PROSPECT_EDITED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited prospect of " + usrnm,request);
            }
            txnManager.commit(status);
            if(!appnames.equals("")){
            	appnames = appnames.substring(0, appnames.length()-1);
            }	
            jobj1.put("msg", appnames);
            jobj1.put("success", true);
            jobj1.put("valid", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
		} finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }


    public ModelAndView getExternalApplicant(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException, SessionExpiredException {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count = 0;
        try {
        	request.setAttribute("isExport", false);
        	jobj = getExternalApplicantJSON(request, response);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView deleteJobapplicant(HttpServletRequest request, HttpServletResponse response) throws ServiceException,HibernateException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String[] ids = request.getParameterValues("ids");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            for (int i = 0; i < ids.length; i++) {
                requestParams.clear();
                requestParams.put("id", ids[i]);
                hrmsRecJobDAOObj.deleteConfigJobapplicant(requestParams);
            }
            jobj.put("data", "");
            jobj.put("valid", true);
            txnManager.commit(status);
        } catch (HibernateException e) {
            txnManager.rollback(status);
            throw ServiceException.FAILURE("hrmsHandler.deleteApplicants", e);
        }finally{
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }

    public ModelAndView jobsearch(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        String jobtype = "Internal";
        int count = 0;
        String status="";
        String userid=request.getParameter("userid");
        String ss = request.getParameter("ss");
        int start = 0;
        int limit = 15;
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
        if (request.getParameter("start") != null) {
            start = Integer.parseInt(request.getParameter("start"));
            limit = Integer.parseInt(request.getParameter("limit"));
        }

        try {
            List lst = null;
            if (StringUtil.isNullOrEmpty(request.getParameter("position"))) {
                filter_names.add("!jobtype");
                filter_names.add("company.companyID");
                filter_names.add("delflag");
                filter_names.add("<=startdate");
                filter_names.add(">=enddate");

                filter_values.add(jobtype);
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                filter_values.add(0);
                filter_values.add(new Date());
                filter_values.add(new Date());

            } else {
                filter_names.add("position.id");
                filter_names.add("!jobtype");
                filter_names.add("company.companyID");
                filter_names.add("delflag");
                filter_names.add("<=startdate");
                filter_names.add(">=enddate");

                filter_values.add(request.getParameter("position"));
                filter_values.add(jobtype);
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                filter_values.add(0);
                filter_values.add(new Date());
                filter_values.add(new Date());
            }

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("searchcol", new String[]{"jobid"} );
            requestParams.put("ss", ss);
            requestParams.put("allflag", false);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            result = hrmsRecJobDAOObj.getPositionmain(requestParams);
            lst = result.getEntityList();
            count = result.getRecordTotalCount();
            for(int ctr=0;ctr<count;ctr++){
                Positionmain extmt = (Positionmain) lst.get(ctr);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("jid", extmt.getPositionid());
//                status = getappPositionstatus(userid,extmt.getPositionid(), session, request);
                filter_names.clear();
                filter_values.clear();
                filter_names.add("configjobapplicant.id");
                filter_names.add("position.positionid");
                filter_names.add("delflag");
                filter_values.add(userid);
                filter_values.add(extmt.getPositionid());
                filter_values.add(0);
                requestParams.clear();
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result = hrmsRecJobDAOObj.getPositionstatus(requestParams);
                Allapplications app =null;
                if(StringUtil.checkResultobjList(result)){
                    app = (Allapplications) result.getEntityList().get(0);
                    status = app.getStatus();
                } else {
                    status = "none";
                }

                if (status.equalsIgnoreCase("none")) {
                    tmpObj.put("status", 0);
                    tmpObj.put("selectionstatus", messageSource.getMessage("hrms.recruitment.not.applied",null, RequestContextUtils.getLocale(request)));
                } else {
                    tmpObj.put("status", 1);
                    tmpObj.put("applicationid", app.getId());
                    tmpObj.put("selectionstatus", status);
                }
                tmpObj.put("jobname", extmt.getPosition().getValue());
                tmpObj.put("jobpositionid", extmt.getJobid());
                tmpObj.put("jdescription", extmt.getDetails());
                requestParams.clear();
                requestParams.put("request", request);
                DateFormat df=kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request),sessionHandlerImplObj.getUserTimeFormat(request),sessionHandlerImplObj.getTimeZoneDifference(request));
                tmpObj.put("jstartdate", df.format(extmt.getStartdate()));
                tmpObj.put("jenddate", df.format(extmt.getEnddate()));
                
                tmpObj.put("jdepartment", extmt.getDepartmentid().getValue());
                tmpObj.put("posmasterid", extmt.getPosition().getId());
                jobj.append("data", tmpObj);
            }
            if(jobj.isNull("data")){
                jobj.put("data", new com.krawler.utils.json.JSONArray());
            }
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {

        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }


    public ModelAndView applyforjobexternal(HttpServletRequest request, HttpServletResponse response) {
        String statusstr = "Pending";
        Date date1;
        DateFormat formatter;
        String positions = "";
        ConfigRecruitmentData applicant = null;
        User user = null;
        String applicantid;
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

            HashMap<String,Object> requestParams = new HashMap<String, Object>();

            formatter = new SimpleDateFormat("MM/dd/yyyy");
            date1 = (Date) formatter.parse(request.getParameter("applydt"));
            Allapplications allapl = null;
            if (StringUtil.isNullOrEmpty(request.getParameter("apcntid"))){
                applicantid=sessionHandlerImplObj.getUserid(request);
            }else{
                applicantid=request.getParameter("apcntid");
            }
            if (Integer.parseInt(request.getParameter("employeetype")) == 1) {
                user = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", applicantid);
            } else {
                applicant = (ConfigRecruitmentData) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.recruitment.ConfigRecruitmentData", applicantid);
            }
            Company cmp = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            String[] ids = request.getParameterValues("posid");
            for (int i = 0; i < ids.length; i++) {
                Positionmain position = (Positionmain) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.recruitment.Positionmain", ids[i]);
                requestParams.put("status",statusstr);
                requestParams.put("applydate", date1);
                if (Integer.parseInt(request.getParameter("employeetype")) == 1) {
                    requestParams.put("employee", user);
                    requestParams.put("employeetype", 1);
                } else {
                    requestParams.put("jobapplicant", applicant);
                    requestParams.put("employeetype", 4); // 0 is changed to 4 for configured job application
                }
                requestParams.put("position",ids[i]);
                requestParams.put("company",cmp);
                requestParams.put("delflag", 0);
                requestParams.put("applicationflag",0);
                requestParams.put("rejectedbefore", 0);
                positions += "" + position.getJobid() + ":" + position.getPosition().getValue() + ",";
                result = hrmsRecJobDAOObj.addAllapplications(requestParams);
                if(result.isSuccessFlag()) {
                    if (Integer.parseInt(request.getParameter("employeetype")) == 1) {
                        auditTrailDAOObj.insertAuditLog(AuditAction.APPLY_FOR_JOB, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has applied for job position " + position.getJobid(), request, "0");
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.APPLY_FOR_JOB, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has applied for job position " + allapl.getPosition().getJobid(),request);
                    } else {
                        if(request.getSession().getAttribute("userid")!=null){
                            //@@ProfileHandler.insertAuditLog(session, AuditAction.APPLY_FOR_JOB, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has applied external applicant " + allapl.getJobapplicant().getFirstname() + " " + allapl.getJobapplicant().getLastname() + " for job position " + allapl.getPosition().getJobid(),request);
                            auditTrailDAOObj.insertAuditLog(AuditAction.APPLY_FOR_JOB, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has applied external applicant " + applicant.getCol1() + " " + applicant.getCol2() + " for job position " + position.getJobid(),request,"0");
                        }
                    }
                }
            }
            /*if (!(Integer.parseInt(request.getParameter("employeetype")) == 1)) {
                String cmpname=cmp.getCompanyName();
                CompanyPreferences cmppref = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cmpname);
                positions = positions.replace(positions.substring(positions.length() - 1), "");
                String pmsg = String.format(HrmsMsgs.jobPlnmsg, (applicant.getFirstname() + " " + applicant.getLastname()),cmpname,cmpname);
//                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,allapl.getPosition().getPosition().getValue()+"["+cmp.getJobidformat()+allapl.getPosition().getJobidwthformat()+"]", cmpname, cmpname);
                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,positions, cmpname, cmpname);
                String subject=String.format(HrmsMsgs.jobSubject,cmp.getJobidformat()+allapl.getPosition().getJobidwthformat(),allapl.getPosition().getPosition().getValue());
                try {
                    SendMailHandler.postMail(new String[]{applicant.getEmail()}, subject, htmlmsg, pmsg, cmp.getEmailID());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }*/
            jobj.put("success", true);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid",true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    public ModelAndView canceljobexternal(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            String[] ids = request.getParameterValues("applicationid");
            for (int i = 0; i < ids.length; i++) {

                ArrayList<String> name = new ArrayList <String>();
                ArrayList<Object> value = new ArrayList <Object>();

                name.add("id");
                value.add(ids[i]);
                hrmsRecJobDAOObj.cancelAllapplications(name,value);
            }
            /*if (!(Integer.parseInt(request.getParameter("employeetype")) == 1)) {
                String cmpname=cmp.getCompanyName();
                CompanyPreferences cmppref = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cmpname);
                positions = positions.replace(positions.substring(positions.length() - 1), "");
                String pmsg = String.format(HrmsMsgs.jobPlnmsg, (applicant.getFirstname() + " " + applicant.getLastname()),cmpname,cmpname);
//                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,allapl.getPosition().getPosition().getValue()+"["+cmp.getJobidformat()+allapl.getPosition().getJobidwthformat()+"]", cmpname, cmpname);
                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,positions, cmpname, cmpname);
                String subject=String.format(HrmsMsgs.jobSubject,cmp.getJobidformat()+allapl.getPosition().getJobidwthformat(),allapl.getPosition().getPosition().getValue());
                try {
                    SendMailHandler.postMail(new String[]{applicant.getEmail()}, subject, htmlmsg, pmsg, cmp.getEmailID());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }*/
            jobj.put("success", true);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid",true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView getRecruitersFunction(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject result = null;
        int count = 0;
        try {
            
            HashMap<String, Object> requestParams =  new HashMap<String, Object>();
            if(!StringUtil.isNullOrEmpty(request.getParameter("status"))){
                requestParams.put("delflag",Integer.parseInt(request.getParameter("status")));
            }
            requestParams.put("deleteflag",0);
            requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("ss",request.getParameter("ss"));
            requestParams.put("allflag",false);
            StringUtil.checkpaging(requestParams, request);

            result = hrmsRecJobDAOObj.getRecruitersList(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                User u = (User) row[0];
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", u.getUserID());
                Recruiter rec = (Recruiter) row[1];
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + u.getLastName());
                obj.put("emailid", u.getEmailID());
                obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("department", ua.getDepartment()!=null?ua.getDepartment().getValue():"");
                if (rec == null) {
                    obj.put("status", 3);
                } else {
                    obj.put("status", rec.getDelflag());
                }
                jarr.put(obj);
            }
            count = result.getRecordTotalCount();
            jobj.put("data", jarr);
            jobj.put("Count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }


    public ModelAndView addRecruitersFunction(HttpServletRequest request, HttpServletResponse response) {
       //Status 0=pending,1=accepted,2=rejected,3=Not sent
        List tabledata = null;
        String hql = null;
        Recruiter pos = null;
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

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            String[] recids=null;
            String auditmsg="";
             Company cmpid=(Company)kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company",sessionHandlerImplObj.getCompanyid(request));
             User u=(User)kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User",sessionHandlerImplObj.getUserid(request));
            if (StringUtil.isNullOrEmpty(request.getParameter("delrec"))) {
                recids = request.getParameterValues("jobids");
                for (int i = 0; i < recids.length; i++) {

                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("recruit.userID");
                    filter_values.add(recids[i]);

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);

                    result= hrmsRecJobDAOObj.getRecruiter(requestParams);
                    tabledata = result.getEntityList();
                    requestParams.clear();
                    if (!tabledata.isEmpty()) {
                        pos = (Recruiter) tabledata.get(0);
                        
                        requestParams.put("Rid",pos.getRid());
                        requestParams.put("Delflag",0);
                        

                        auditmsg="User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has set " + StringUtil.getFullName(pos.getRecruit()) + " as interviewer";
                    } else {
                        User md = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", recids[i]);
                        requestParams.put("Delflag",0);
                        //requestParams.put("Recruit",md);
                        requestParams.put("Recruit",recids[i]);

                        auditmsg = "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has set " + StringUtil.getFullName(md) + " as interviewer";
                    }

                    result = hrmsRecJobDAOObj.setRecruiters(requestParams);
                    if(result.isSuccessFlag()){
                        auditTrailDAOObj.insertAuditLog(AuditAction.SET_AS_INTERVIEWER, auditmsg, request, "0");
                    }
                }
            } else {
                String[] delrecids = request.getParameterValues("appid");
                for (int i = 0; i < delrecids.length; i++) {

                    requestParams.clear();
                    filter_names.clear();
                    filter_values.clear();

                    filter_names.add("recruit.userID");
                    filter_values.add(delrecids[i]);

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_values);

                    result= hrmsRecJobDAOObj.getRecruiter(requestParams);
                    tabledata = result.getEntityList();
                    requestParams.clear();
                    if (!tabledata.isEmpty()) {
                        pos = (Recruiter) tabledata.get(0);

                        requestParams.put("Rid",pos.getRid());
                        requestParams.put("Delflag",3);
                        

                        auditmsg="User  " + StringUtil.getFullName(pos.getRecruit()) + " has been unassigned  as interviewer by " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request));
                    }


                    result = hrmsRecJobDAOObj.setRecruiters(requestParams);
                    if(result.isSuccessFlag()){
                        auditTrailDAOObj.insertAuditLog(AuditAction.SET_AS_INTERVIEWER, auditmsg, request, "0");
                    }
                }
            }
            if (StringUtil.isNullOrEmpty(request.getParameter("delrec"))) {
                for(int i=0;i<recids.length;i++){
                    User r=(User)kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User",recids[i]);
                  Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", r.getUserID());
                  String fullname=StringUtil.getFullName(r);
                  String uri = URLUtil.getPageURL(request, Links.loginpagewthFull,cmpid.getSubDomain())+"jspfiles/Confirmation.jsp?c="+cmpid.getCompanyID()+"&u="+r.getUserID()+"&acpt=";
                  String pmsg = String.format(HrmsMsgs.interviewerSelectionpln, fullname);
                  String htmlmsg = String.format(HrmsMsgs.interviewerSelectionHTML,fullname,uri+"1",uri+"0",
                          StringUtil.getFullName(u),cmpid.getCompanyName());
                    try {
                        SendMailHandler.postMail(new String[]{r.getEmailID()}, HrmsMsgs.interviewerSubject, htmlmsg, pmsg,u.getEmailID());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
            jobj.put("success", true);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid",true);
             txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView saveConfigRecruitment(HttpServletRequest request, HttpServletResponse response) {
       //Status 0=pending,1=accepted,2=rejected,3=Not sent
        List tabledata = null;
        JSONObject msg = new JSONObject();
        String hql = null;
        Recruiter pos = null;
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

            HashMap<String, Object> arrParam = new HashMap<String, Object>();
            HashMap<String, Object> requestParam = new HashMap<String, Object>();
            ArrayList<FileItem> hm = new ArrayList<FileItem>();
            boolean fileUpload = false;
            HashMap<Integer,String> filemap = new HashMap<Integer,String >();
            hrmsRecJobDAOObj.parseRequest(request, arrParam, hm, fileUpload,filemap);
            String auditmsg="";
            result = hrmsRecJobDAOObj.addConfigRecruitmentData(arrParam);
//                    if(result.isSuccessFlag()){
//                        auditTrailDAOObj.insertAuditLog(AuditAction.SET_AS_INTERVIEWER, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has edited " + profileHandlerDAOObj.getUserFullName(userid) + "'s profile", request, "0");
//                    }
            ConfigRecruitmentData ConfigRecruitmentDataobj = (ConfigRecruitmentData) result.getEntityList().get(0);
                for(int j=0;j<filemap.size();j++){
                    HrmsDocs doc = hrmsRecJobDAOObj.uploadFile(hm.get(j), ConfigRecruitmentDataobj, arrParam, false);
                    arrParam.clear();
                    arrParam.put("Docid",doc.getDocid());
                    arrParam.put("Recid",ConfigRecruitmentDataobj.getId());
                    result = hrmsRecJobDAOObj.addHrmsDocmap(arrParam);
                    requestParam.put(filemap.get(j),doc.getDocid());
                    requestParam.put("Id",ConfigRecruitmentDataobj.getId());
                }
            result = hrmsRecJobDAOObj.addConfigRecruitmentData(requestParam);
            if(result.isSuccessFlag()){
            	auditTrailDAOObj.insertAuditLog(AuditAction.PROFILE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has Edited " +ConfigRecruitmentDataobj.getCol1()+" "+ConfigRecruitmentDataobj.getCol2()+"'s profile"  , request, "0");
            }
            msg.put("msg", messageSource.getMessage("hrms.Messages.calMsgBoxShow122", null, RequestContextUtils.getLocale(request)));
            msg.put("success", true);
             txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView","model",msg.toString());
    }

    public ModelAndView createapplicantFunction(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        List list = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();

            String pwd=request.getParameter("p");
            String username = request.getParameter("u");
            String fname = request.getParameter("fname");
            String lname = request.getParameter("lname");
             String cmpid=sessionHandlerImplObj.getCompanyid(request);
            if (StringUtil.isNullOrEmpty(request.getParameter("update"))) {

                filter_names.add("Col3");
                filter_values.add(request.getParameter("e"));
                filter_names.add("deleted");
                filter_values.add(false);
                filter_names.add("company.companyID");
                filter_values.add(cmpid);

                requestParams.put("filter_names",filter_names);
                requestParams.put("filter_values",filter_values);

                result = hrmsRecJobDAOObj.getConfigJobApplicant(requestParams);

                list = result.getEntityList();

                if (list.size() == 0) {
                    Company cmp = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", cmpid);
                    requestParams.clear();
                    requestParams.put("Col1", fname);
                    requestParams.put("Col2", lname);
                    requestParams.put("Col3", request.getParameter("e"));
                    //requestParams.put("Address1", request.getParameter("addr"));
                    requestParams.put("Col4", request.getParameter("contact"));
//                    requestParams.put("Username", username);
//                    requestParams.put("Password", authHandler.getSHA1(pwd));
//                    requestParams.put("Status", 0);
                    requestParams.put("Company", cmpid);
                    result = hrmsRecJobDAOObj.addConfigRecruitmentData(requestParams);
                    if(result.isSuccessFlag()){
                        /*String uri = URLUtil.getPageURL(request, Links.loginpagewthFull,cmp.getSubDomain())+"applicantLogin.html";
                        String pmsg = String.format(KWLErrorMsgs.msgMailInvite, fname, "Demo", username, pwd, uri, "Demo");
                        String htmlmsg = String.format(HrmsMsgs.msgMailInviteUsernamePassword, fname, profileHandlerDAOObj.getUserFullName( sessionHandlerImplObj.getUserid(request)), sessionHandlerImplObj.getCompanyName(request), username,
                                pwd, uri, uri, "");
                        try {
                            SendMailHandler.postMail(new String[]{request.getParameter("e")}, "["+messageSource.getMessage("hrms.common.deskera", null, RequestContextUtils.getLocale(request))+"] "+messageSource.getMessage("hrms.common.welcome.deskera.hrms", null, RequestContextUtils.getLocale(request)), htmlmsg, pmsg, "admin.hrms@mailinator.com");
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }*/
                        jobj.put("msg", messageSource.getMessage("hrms.common.Applicantcreatedsuccessfully", null, RequestContextUtils.getLocale(request)));
                        jobj.put("type", messageSource.getMessage("hrms.common.success", null, RequestContextUtils.getLocale(request)));
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.CREATE_APPLICANT, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has created new applicant " + jobapp.getFirstname() + " " + jobapp.getLastname(),request);
                        auditTrailDAOObj.insertAuditLog(AuditAction.CREATE_APPLICANT, "User  " +profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has created new applicant " + fname + " " + lname , request, "0");
                    } else {
                        jobj.put("msg", messageSource.getMessage("hrms.recruitment.error.occurred.creating.applicant", null, RequestContextUtils.getLocale(request)));
                        jobj.put("type", messageSource.getMessage("hrms.common.error", null, RequestContextUtils.getLocale(request)));
                    }
                } else {
                    jobj.put("msg", messageSource.getMessage("hrms.common.email.already.exists", null, RequestContextUtils.getLocale(request)));
                    jobj.put("type", messageSource.getMessage("hrms.common.warning", null, RequestContextUtils.getLocale(request)));
                }
            } else {
                requestParams.put("primary",true);
                requestParams.put("applicantid",request.getParameter("profileid"));
                
                result = hrmsRecJobDAOObj.getConfigJobApplicant(requestParams);
                list = result.getEntityList();
                 if (!list.isEmpty()) {
                    String jsondata1 = request.getParameter("jsondata1");
                    String jsondata2 = request.getParameter("jsondata2");
                    String jsondata3 = request.getParameter("jsondata3");
                    String jsondata4 = request.getParameter("jsondata4");
                    String jsondata5 = request.getParameter("jsondata5");

                    requestParams.clear();

                    requestParams.put("Applicantid", request.getParameter("profileid"));
                    if (jsondata1.length() > 0) {
                            jobj = new JSONObject(jsondata1);
                            requestParams.put("Title", jobj.getString("title"));
                            requestParams.put("Firstname", jobj.getString("firstname"));
                            requestParams.put("Lastname", jobj.getString("lastname"));
                            requestParams.put("Email", jobj.getString("email"));
                            requestParams.put("Otheremail", jobj.getString("otheremail"));
                            requestParams.put("Birthdate", (Date) fmt.parse(jobj.getString("birthdate")));
                    }
                    if (jsondata2.length() > 0) {
                            jobj = new JSONObject(jsondata2);
                            requestParams.put("Contactno", jobj.getString("contactno"));
                            requestParams.put("Mobileno", jobj.getString("mobileno"));
                            requestParams.put("City", jobj.getString("city"));
                            requestParams.put("State", jobj.getString("state"));
                            requestParams.put("Countryid", jobj.getString("country"));
                            requestParams.put("Address1", jobj.getString("address1"));
                            requestParams.put("Address2", jobj.getString("address2"));
                        
                    }
                    if (jsondata3.length() > 0) {
                            jobj = new JSONObject(jsondata3);
                            if (StringUtil.isNullOrEmpty(jobj.getString("graddegree")) == false) {
                                requestParams.put("Graddegree", jobj.getString("graddegree"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradspecilization")) == false) {
                                requestParams.put("Gradspecialization", jobj.getString("gradspecilization"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradcollege")) == false) {
                                requestParams.put("Gradcollege", jobj.getString("gradcollege"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("graduniversity")) == false) {
                                requestParams.put("Graduniversity", jobj.getString("graduniversity"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradepercent")) == false) {
                                requestParams.put("Gradpercent", jobj.getString("gradepercent"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradpassdate")) == false) {
                                requestParams.put("Gradpassdate", (Date) fmt.parse(jobj.getString("gradpassdate")));
                            }


                            if (StringUtil.isNullOrEmpty(jobj.getString("pgqualification")) == false) {
                                requestParams.put("Pgqualification", jobj.getString("pgqualification"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgspecialization")) == false) {
                                requestParams.put("Pgspecialization", jobj.getString("pgspecialization"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgcollege")) == false) {
                                requestParams.put("Pgcollege", jobj.getString("pgcollege"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pguniversity")) == false) {
                                requestParams.put("Pguniversity", jobj.getString("pguniversity"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgpercent")) == false) {
                                requestParams.put("Pgpercent", jobj.getString("pgpercent"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgpassdate")) == false) {
                                requestParams.put("Pgpassdate", (Date) fmt.parse(jobj.getString("pgpassdate")));
                            }

                            if (StringUtil.isNullOrEmpty(jobj.getString("othername")) == false) {
                                requestParams.put("Othername", jobj.getString("othername"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherqualification")) == false) {
                                requestParams.put("Otherqualification", jobj.getString("otherqualification"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherpercent")) == false) {
                                requestParams.put("Otherpercent", jobj.getString("otherpercent"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherdetails")) == false) {
                                requestParams.put("Otherdetails", jobj.getString("otherdetails"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherpassdate")) == false) {
                                requestParams.put("Otherpassdate", (Date) fmt.parse(jobj.getString("otherpassdate")));
                            }
                    }
                    if (jsondata4.length() > 0) {
                           jobj = new JSONObject(jsondata4);
                            if (jobj.getString("experiencemonth").equals("")) {
                                requestParams.put("Experiencemonth", 0);
                            } else {
                               requestParams.put("Experiencemonth", Integer.parseInt(jobj.getString("experiencemonth")));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("experienceyear")) == false) {
                                requestParams.put("Experienceyear", Integer.parseInt(jobj.getString("experienceyear")));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("functionalexpertise")) == false) {
                                requestParams.put("Functionalexpertise", jobj.getString("functionalexpertise"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("currentindustry")) == false) {
                                requestParams.put("Currentindustry", jobj.getString("currentindustry"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("currentorganization")) == false) {
                                requestParams.put("Currentorganization", jobj.getString("currentorganization"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("currentdesignation")) == false) {
                                requestParams.put("Currentdesignation", jobj.getString("currentdesignation"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("grosssalary")) == false) {
                                requestParams.put("Grosssalary", Integer.parseInt(jobj.getString("grosssalary")));
                            }
                           requestParams.put("Expectedsalary", Integer.parseInt(jobj.getString("expectedsalary")));
                    }
                    if (jsondata5.length() > 0) {
                        jobj =  new JSONObject(jsondata5);
                        requestParams.put("Keyskills", jobj.getString("keyskills"));
                            if (StringUtil.isNullOrEmpty(jobj.getString("category")) == false) {
                                requestParams.put("Category", jobj.getString("category"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("filepath")) == false) {
                                requestParams.put("Filepath", jobj.getString("filepath"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("companyrelative")) == false) {
                                requestParams.put("Companyrelative", jobj.getString("companyrelative"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("appearedbefore")) == false) {
                                requestParams.put("Appearedbefore", jobj.getString("appearedbefore"));
                                if(jobj.getString("appearedbefore").equalsIgnoreCase("yes")){
                                    if (StringUtil.isNullOrEmpty(jobj.getString("interviewplace")) == false) {
                                        requestParams.put("Interviewplace", jobj.getString("interviewplace"));
                                    }
                                    if (StringUtil.isNullOrEmpty(jobj.getString("interviewdate")) == false) {
                                        requestParams.put("Interviewdate", (Date) fmt.parse(jobj.getString("interviewdate")));
                                    }
                                    if (StringUtil.isNullOrEmpty(jobj.getString("interviewposition")) == false) {
                                        requestParams.put("Interviewposition", jobj.getString("interviewposition"));
                                    }
                                }
                            }
                        requestParams.put("Interviewlocation", jobj.getString("interviewlocation"));
                        result= hrmsRecJobDAOObj.setJobApplicant(requestParams);
                    }
                   // insertConfigData(request, "Recruitment", request.getParameter("profileid"),sessionHandlerImplObj.getCompanyid(request));
                    hrmsCommonDAOObj.insertConfigData(request, "Recruitment", request.getParameter("profileid"),sessionHandlerImplObj.getCompanyid(request));
                }
                if(result.isSuccessFlag()) {
                    jobj.put("msg", messageSource.getMessage("hrms.recruitment.applicant.updated.successfully", null, RequestContextUtils.getLocale(request)));
                    jobj.put("type", messageSource.getMessage("hrms.common.success", null, RequestContextUtils.getLocale(request)));
                } else {
                    jobj.put("msg", messageSource.getMessage("hrms.recruitment.error.occurred.updating.applicant", null, RequestContextUtils.getLocale(request)));
                    jobj.put("type", messageSource.getMessage("hrms.common.error", null, RequestContextUtils.getLocale(request)));
                }
            }
             jobj1.put("data", jobj.toString());
            jobj1.put("valid",true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        } finally {
        	return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getEmpidFormat(HttpServletRequest request, HttpServletResponse response ) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        int count = 0;
        String mainstr = "";
        Integer maxcount = null;
        try {

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList(),select = new ArrayList();


            String cmpnyid = sessionHandlerImplObj.getCompanyid(request);
            filter_names.add("user.company.companyID");
            filter_values.add(cmpnyid);
            select.add("max(employeeid)");

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("select", select);

            result = hrmsCommonDAOObj.getUseraccount(requestParams);
            maxcount = (Integer) result.getEntityList().iterator().next()+1;

            requestParams.clear();
            requestParams.put("empid", maxcount);
            requestParams.put("companyid", cmpnyid);

            result = hrmsCommonDAOObj.getEmpidFormatEdit(requestParams);
            mainstr = result.getEntityList().get(0).toString();

            obj.put("maxempid", mainstr);
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data",jobj.toString());
            jobj1.put("valid",true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return  new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    public ModelAndView getConfigRecruitment(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        List lstJobApplicant =null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        ConfigRecruitmentData ConfigRecruitmentDataref = null;
        Class configrecdata =null;
        int count = 0;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            if(request.getParameter("formtype")!=null && request.getParameter("visible")!=null){
                requestParams.put("primary", true);
                requestParams.put("applicantid", request.getParameter("refid"));
                KwlReturnObject resultJobApplicant = hrmsRecJobDAOObj.getConfigJobApplicant(requestParams);
                lstJobApplicant = resultJobApplicant.getEntityList();
                Iterator ite = lstJobApplicant.iterator();
                while (ite.hasNext()) {
                    ConfigRecruitmentDataref = (ConfigRecruitmentData) ite.next();
                }
                configrecdata = Class.forName("com.krawler.hrms.recruitment.ConfigRecruitmentData");
                requestParams.clear();

                if(request.getParameter("formtype").equals("All")){
                    requestParams.put("filter_names", Arrays.asList("company.companyID","visible"));
                    requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request),Boolean.parseBoolean(request.getParameter("visible"))));
                }else{
                    requestParams.put("filter_names", Arrays.asList("company.companyID","formtype","visible"));
                    requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request),request.getParameter("formtype"),Boolean.parseBoolean(request.getParameter("visible"))));
                }

                
            }
            else if(request.getParameter("configtype")!=null){
                requestParams.put("filter_names", Arrays.asList("company.companyID","INconfigtype"));
                requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request),request.getParameter("configtype")));
            }else if(request.getParameter("visible")!=null){
                requestParams.put("filter_names", Arrays.asList("company.companyID","visible"));
                requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request),Boolean.parseBoolean(request.getParameter("visible"))));
            }else{
                requestParams.put("filter_names", Arrays.asList("company.companyID"));
                requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request)));
            }
            
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("searchcol", new String[]{"name"});
            if(request.getParameter("mapping")!=null){
                requestParams.put("order_by", Arrays.asList("colnum"));
                requestParams.put("order_type", Arrays.asList("asc"));
            } else {
                requestParams.put("order_by", Arrays.asList("formtype","position"));
                requestParams.put("order_type", Arrays.asList("asc","asc"));
            }
            result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            int i=0;
            while (ite.hasNext()) {
                ConfigRecruitment contyp = (ConfigRecruitment) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("index", i);
                tmpObj.put("configid", contyp.getConfigid());
                tmpObj.put("configtype", contyp.getConfigtype());
                tmpObj.put("fieldname", messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(contyp.getName()), null, contyp.getName(), RequestContextUtils.getLocale(request)));
                tmpObj.put("formtype", contyp.getFormtype());
                tmpObj.put("position", contyp.getPosition());
                tmpObj.put("colnum", contyp.getColnum());
                tmpObj.put("issystemproperty", contyp.isIsSystemProperty());
                tmpObj.put("visible", contyp.isVisible());
                tmpObj.put("allownull", contyp.isAllownull());
                tmpObj.put("allowblank", contyp.isAllownull());
                tmpObj.put("displayname", messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(contyp.getName()), null, contyp.getName(), RequestContextUtils.getLocale(request)) + " (" + contyp.getFormtype() +")");
                if(request.getParameter("fetchmaster")!=null){
                    if(contyp.getConfigtype()==3 || contyp.getConfigtype()==1){
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("configid.configid"));
                        requestParams.put("filter_values", Arrays.asList(contyp.getConfigid()));
                        tmpObj.put("data",getConfigMasterdataWithLocalString(requestParams,request));
                    }
                }
                if(request.getParameter("refid")!=null && configrecdata!=null){
                        JSONArray cdata = new JSONArray();
                        Method getter = configrecdata.getMethod("getCol"+contyp.getColnum());
                        Object obj = getter.invoke(ConfigRecruitmentDataref);
                        cdata.put(obj);
                        tmpObj.put("configdata", cdata);
                }
                i++;
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }
    
    public ModelAndView getConfigRecruitmentApplyOnline(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        List lstJobApplicant =null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        ConfigRecruitmentData ConfigRecruitmentDataref = null;
        Class configrecdata =null;
        int count = 0;
        try {
        	String companyid = request.getParameter("companyid");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            if(request.getParameter("formtype")!=null && request.getParameter("visible")!=null){
                requestParams.put("primary", true);
                requestParams.put("applicantid", request.getParameter("refid"));
                KwlReturnObject resultJobApplicant = hrmsRecJobDAOObj.getConfigJobApplicant(requestParams);
                lstJobApplicant = resultJobApplicant.getEntityList();
                Iterator ite = lstJobApplicant.iterator();
                while (ite.hasNext()) {
                    ConfigRecruitmentDataref = (ConfigRecruitmentData) ite.next();
                }
                configrecdata = Class.forName("com.krawler.hrms.recruitment.ConfigRecruitmentData");
                requestParams.clear();

                if(request.getParameter("formtype").equals("All")){
                    requestParams.put("filter_names", Arrays.asList("company.companyID","visible"));
                    requestParams.put("filter_values", Arrays.asList(companyid, Boolean.parseBoolean(request.getParameter("visible"))));
                }else{
                    requestParams.put("filter_names", Arrays.asList("company.companyID","formtype","visible"));
                    requestParams.put("filter_values", Arrays.asList(companyid, request.getParameter("formtype"),Boolean.parseBoolean(request.getParameter("visible"))));
                }

                
            }
            else if(request.getParameter("configtype")!=null){
                requestParams.put("filter_names", Arrays.asList("company.companyID","INconfigtype"));
                requestParams.put("filter_values", Arrays.asList(companyid, request.getParameter("configtype")));
            }else if(request.getParameter("visible")!=null){
                requestParams.put("filter_names", Arrays.asList("company.companyID","visible"));
                requestParams.put("filter_values", Arrays.asList(companyid, Boolean.parseBoolean(request.getParameter("visible"))));
            }else{
                requestParams.put("filter_names", Arrays.asList("company.companyID"));
                requestParams.put("filter_values", Arrays.asList(companyid));
            }
            
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("searchcol", new String[]{"name"});
            if(request.getParameter("mapping")!=null){
                requestParams.put("order_by", Arrays.asList("colnum"));
                requestParams.put("order_type", Arrays.asList("asc"));
            } else {
                requestParams.put("order_by", Arrays.asList("formtype","position"));
                requestParams.put("order_type", Arrays.asList("asc","asc"));
            }
            result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            int i=0;
            while (ite.hasNext()) {
                ConfigRecruitment contyp = (ConfigRecruitment) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("index", i);
                tmpObj.put("configid", contyp.getConfigid());
                tmpObj.put("configtype", contyp.getConfigtype());
                tmpObj.put("fieldname", messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(contyp.getName()), null, contyp.getName(), RequestContextUtils.getLocale(request)));
                tmpObj.put("formtype", contyp.getFormtype());
                tmpObj.put("position", contyp.getPosition());
                tmpObj.put("colnum", contyp.getColnum());
                tmpObj.put("issystemproperty", contyp.isIsSystemProperty());
                tmpObj.put("visible", contyp.isVisible());
                tmpObj.put("allownull", contyp.isAllownull());
                tmpObj.put("allowblank", contyp.isAllownull());
                tmpObj.put("displayname", messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(contyp.getName()), null, contyp.getName(), RequestContextUtils.getLocale(request)) + " (" + contyp.getFormtype() +")");
                if(request.getParameter("fetchmaster")!=null){
                    if(contyp.getConfigtype()==3 || contyp.getConfigtype()==1){
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("configid.configid"));
                        requestParams.put("filter_values", Arrays.asList(contyp.getConfigid()));
                        tmpObj.put("data",getConfigMasterdata(requestParams,request));
                    }
                }
                if(request.getParameter("refid")!=null && configrecdata!=null){
                        JSONArray cdata = new JSONArray();
                        Method getter = configrecdata.getMethod("getCol"+contyp.getColnum());
                        Object obj = getter.invoke(ConfigRecruitmentDataref);
                        cdata.put(obj);
                        tmpObj.put("configdata", cdata);
                }
                i++;
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	return new ModelAndView(successView,"model",jobj1.toString());
        }
    }

    public ModelAndView addConfigRecruitmentType(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        String companyid = "";
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean max = false,isfieldnotexist=true, isEmailNullOrVisibleFlag = true;
        String title = messageSource.getMessage("hrms.common.success", null,"Success", RequestContextUtils.getLocale(request));
        try {
            companyid = sessionHandlerImplObj.getCompanyid(request);
            ConfigRecruitment contyp = null;
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            if (!request.getParameter("configid").equals("clone")) {
                int colcount = 0;
                int position = 1;
                if (request.getParameter("configid").equals("config")) {
                    requestParams.put("filter_names", Arrays.asList("company.companyID"));
                    requestParams.put("filter_values", Arrays.asList(companyid));
                    result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
                    List lst = result.getEntityList();
                    colcount = lst.size();
                    if(colcount==70){
                        jobj.put("success", "msg");
                        jobj.put("title", messageSource.getMessage("hrms.common.alert", null,"Alert", RequestContextUtils.getLocale(request)));
                        jobj.put("msg", messageSource.getMessage("hrms.common.CannotaddnewfieldMaximumcustomfieldlimitreached", null,"Cannot add new field. Maximum custom field limit reached.", RequestContextUtils.getLocale(request)));
                        max = true;
                    }
                    if (!max) {
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("formtype","name","company.companyID"));
                        requestParams.put("filter_values", Arrays.asList(request.getParameter("formtype"), request.getParameter("fieldname"), companyid));
                        result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
                        List lst1 = result.getEntityList();
                        Iterator ite1 = lst1.iterator();
                        if (ite1.hasNext()) {
                            isfieldnotexist = false;
                            title = messageSource.getMessage("hrms.common.warning", null, RequestContextUtils.getLocale(request));
                            jobj.put("success", "msg");
                            jobj.put("title", messageSource.getMessage("hrms.common.alert", null,"Alert", RequestContextUtils.getLocale(request)));
                            jobj.put("msg", messageSource.getMessage("hrms.common.FieldNameexistsPleaseprovideadifferentFieldName", null,"FieldName exists. Please provide a different FieldName.", RequestContextUtils.getLocale(request)));
                        } else {
                        Iterator ite =lst.iterator();
                        int[] countchk= new int[71];
                        while(ite.hasNext()){
                            ConfigRecruitment tmpcontyp= (ConfigRecruitment) ite.next();
                            countchk[tmpcontyp.getColnum()]=1;
                            }
                        for(int i=1;i<=70;i++){
                            if(countchk[i]==0){
                                colcount=i;
                                    break;
                                }
                            }
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("formtype","company.companyID"));
                        requestParams.put("filter_values", Arrays.asList(request.getParameter("formtype"), companyid));
                        result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
                        position = result.getEntityList().size() + 1;

                            jobj.put("msg", messageSource.getMessage("hrms.common.Configoptionisaddedsuccessfully", null,"Config option is added successfully.", RequestContextUtils.getLocale(request)));
                        }
                        requestParams.clear();
                        requestParams.put("Position", position);
                    }
                } else {
                	boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
                    boolean allownull = Boolean.parseBoolean(request.getParameter("allownull"));
                    requestParams.clear();
                    contyp = (ConfigRecruitment) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.recruitment.ConfigRecruitment", request.getParameter("configid"));
                    colcount=contyp.getColnum();
                    if(colcount==1 || colcount==2 || colcount==3 || colcount==4 || colcount==9){
                    	String message = "";
                    	isEmailNullOrVisibleFlag = false;
                    	if(colcount==1){
                    		message = "'"+messageSource.getMessage("hrms.common.FirstName", null, RequestContextUtils.getLocale(request))+"'";
                    	}else{
                    		if(colcount == 2){
                    	  		message = "'"+messageSource.getMessage("hrms.common.LastName", null, RequestContextUtils.getLocale(request))+"'";
                    		}else{
                    			if(colcount == 3){
                    				message = "'"+messageSource.getMessage("hrms.recruitment.profile.EmailID", null, RequestContextUtils.getLocale(request))+"'";
                    			}else{
                    				if(colcount == 4){
                    					message = "'"+messageSource.getMessage("hrms.recruitment.profile.ContactNo", null, RequestContextUtils.getLocale(request))+"'";
                    				}else{
                    					if(colcount == 9){
                    						message = "'"+messageSource.getMessage("hrms.recruitment.profile.AppliedDate1", null, "A",RequestContextUtils.getLocale(request))+"'";
                    					}
                    				}
                    			}
                    		}
                    	}
                    	title = messageSource.getMessage("hrms.common.warning", null, RequestContextUtils.getLocale(request));
                    	jobj.put("msg", messageSource.getMessage("hrms.recruitment.you.cannot.edit.default.config", null, RequestContextUtils.getLocale(request))+" "+message+".");
                    }else{
                    	jobj.put("msg", messageSource.getMessage("hrms.common.Configoptionisaddedsuccessfully", null,"Config option is added successfully.", RequestContextUtils.getLocale(request)));
                    }
                    requestParams.clear();
                    
                    if(!contyp.getFormtype().equals(request.getParameter("formtype"))){
                        requestParams.put("position", contyp.getPosition());
                        requestParams.put("formtype", contyp.getFormtype());
                        hrmsRecJobDAOObj.updateConfigRecruitment(requestParams);

                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("formtype","company.companyID"));
                        requestParams.put("filter_values", Arrays.asList(request.getParameter("formtype"), companyid));
                        result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
                        position = result.getEntityList().size() + 1;

                        requestParams.clear();
                        requestParams.put("Position", position);
                    }
                    requestParams.put("Configid", request.getParameter("configid"));
                }
                if(isfieldnotexist && isEmailNullOrVisibleFlag){
                    requestParams.put("Company", companyid);
                    requestParams.put("Formtype", request.getParameter("formtype"));
                    requestParams.put("Name", request.getParameter("fieldname"));
                    requestParams.put("Colnum", colcount);
                    requestParams.put("Configtype", Integer.parseInt(request.getParameter("configtype")));
                    requestParams.put("Visible", request.getParameter("visible").equals("true"));
                    requestParams.put("IsSystemProperty", request.getParameter("issystemproperty").equals("true"));
                    requestParams.put("Allownull", request.getParameter("allownull").equals("true"));
                    result = hrmsRecJobDAOObj.addConfigRecruitmentType(requestParams);
                }
            }
            jobj.put("success", "msg");
            jobj.put("title", title);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return  new ModelAndView(successView,"model",jobj1.toString());
        }
    }
     public ModelAndView deleteConfigRecruitment(HttpServletRequest request,HttpServletResponse response){
        String mode = request.getParameter("mode");
        String delid = request.getParameter("delid");
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            JSONArray jarr = new JSONArray(delid);
            for (int ctr = 0; ctr < jarr.length(); ctr++) {
                if (mode.equals("config")) {

                    ConfigRecruitment contyp = (ConfigRecruitment) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.recruitment.ConfigRecruitment", jarr.get(ctr).toString());
                    if(contyp.getColnum() > 5){
                        if(!contyp.isIsSystemProperty()){
                            requestParams.put("column", "Col" + contyp.getColnum());
                            requestParams.put("company", contyp.getCompany().getCompanyID());
                            hrmsRecJobDAOObj.updateConfigRecruitmentDatatoDefault(requestParams);
                            hrmsRecJobDAOObj.deleteConfigRecruitment(jarr.get(ctr).toString());
                            jobj.put("success", "true");
                            jobj1.put("data", jobj);
                        }else{
                            jobj.put("success", "msg");
                            jobj.put("title", messageSource.getMessage("hrms.common.warning", null, RequestContextUtils.getLocale(request)));
                            jobj.put("msg", messageSource.getMessage("hrms.recruitment.unable.delete.system.config", null, RequestContextUtils.getLocale(request)));
                            jobj1.put("data", jobj);
                        }
                    }else{
                            jobj.put("success", "msg");
                            jobj.put("title", messageSource.getMessage("hrms.common.warning", null, RequestContextUtils.getLocale(request)));
                            jobj.put("msg", messageSource.getMessage("hrms.recruitment.unable.delete.default.system.configtypes", null, RequestContextUtils.getLocale(request)));
                            jobj1.put("data", jobj);

                    }
                    jobj1.put("valid", true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }

     public ModelAndView transferappdata(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int dt = 0;
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        HashMap<String,Integer> datatransfer = new HashMap<String, Integer>();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        datatransfer.put("count", dt);
        try {
            String appid = request.getParameter("applicantid");
            String mappedHeaders = request.getParameter("mappedheader");
            
            requestParams.put("employeetype", request.getParameter("employeetype"));
            requestParams.put("employeeid", request.getParameter("employeeid"));
            requestParams.put("appusername", request.getParameter("appusername"));
            requestParams.put("applicantid", appid);
            requestParams.put("designationid", request.getParameter("designationid"));
            requestParams.put("empjoindate", request.getParameter("empjoindate"));
            requestParams.put("departmentid", request.getParameter("departmentid"));
            requestParams.put("employeerectype", request.getParameter("employeerectype"));

            if (request.getParameter("employeerectype").toString().equalsIgnoreCase("0")) {
                com.krawler.utils.json.base.JSONObject headers = new com.krawler.utils.json.base.JSONObject(mappedHeaders);
                requestParams.put("middlename", fetchField(headers, "Middle Name", appid, false, datatransfer));
                requestParams.put("dob", fetchField(headers, "Date Of Birth", appid, true, datatransfer));
                requestParams.put("gender", fetchField(headers, "Gender", appid, false, datatransfer));
                requestParams.put("maritalstatus", fetchField(headers, "Marital Status", appid, false, datatransfer));
                requestParams.put("bloodgroup", fetchField(headers, "Blood Group", appid, false, datatransfer));
                requestParams.put("fathername", fetchField(headers, "Father's Name", appid, false, datatransfer));
                requestParams.put("fatherdob", fetchField(headers, "Father's DOB", appid, true, datatransfer));
                requestParams.put("mothername", fetchField(headers, "Mother's Name", appid, false, datatransfer));
                requestParams.put("motherdob", fetchField(headers, "Mother's DOB", appid, true, datatransfer));
                requestParams.put("keyskill", fetchField(headers, "Key Skills", appid, false, datatransfer));
                requestParams.put("panno", fetchField(headers, "PAN No", appid, false, datatransfer));
                requestParams.put("epfno", fetchField(headers, "EPF No", appid, false, datatransfer));
                requestParams.put("drivingno", fetchField(headers, "Driving License No", appid, false, datatransfer));
                requestParams.put("passportno", fetchField(headers, "Passport No", appid, false, datatransfer));
                requestParams.put("exdateofpassport", fetchField(headers, "Expiry Date of Passport", appid, true, datatransfer));
                requestParams.put("mobileno", fetchField(headers, "Mobile no", appid, false, datatransfer));
                requestParams.put("landlineno", fetchField(headers, "Landline No", appid, false, datatransfer));
                requestParams.put("otheremail", fetchField(headers, "Other Email", appid, false, datatransfer));
                requestParams.put("permanentaddress", fetchField(headers, "Permanent Address", appid, false, datatransfer));
                requestParams.put("presentaddress", fetchField(headers, "Present Address", appid, false, datatransfer));
                requestParams.put("presentcity", fetchField(headers, "Present City", appid, false, datatransfer));
                requestParams.put("presentstate", fetchField(headers, "Present State", appid, false, datatransfer));
                requestParams.put("presentcountry", fetchField(headers, "Present Country", appid, false, datatransfer));
                requestParams.put("Permanentcity", fetchField(headers, "Permanent City", appid, false, datatransfer));
                requestParams.put("Permanentstate", fetchField(headers, "Permanent State", appid, false, datatransfer));
                requestParams.put("Permanentcountry", fetchField(headers, "Permanent Country", appid, false, datatransfer));
            }

            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            result = hrmsRecJobDAOObj.transferappdata(requestParams);
            User relatedTo = ((ArrayList<User>) result.getEntityList()).get(0); 
            
            if (request.getParameter("employeerectype").toString().equalsIgnoreCase("0")) {//For External Applicant Only(Uploaded Documents transfer to Employee data.)
            	KwlReturnObject kwlReturnObject1 = null;
            	KwlReturnObject kwlReturnObject2 = null;
            	KwlReturnObject kwlReturnObject3 = null;
            	String userid = request.getParameter("applicantid");
            	ArrayList<String> filter_names = new ArrayList<String>();
            	ArrayList<Object> filter_values = new ArrayList<Object>();
            	filter_names.add("recid");
            	filter_values.add(userid);
            	filter_names.add("docid.deleted");
            	filter_values.add(false);
                filter_names.add("docid.referenceid");
                filter_values.add(userid);
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                kwlReturnObject1 = hrmsExtApplDocsDAOObj.getDocs(requestParams);
                ArrayList<HrmsDocmap> hrmsDocmaps = (ArrayList<HrmsDocmap>) kwlReturnObject1.getEntityList(); 
                kwlReturnObject2 = hrmsRecJobDAOObj.transferExternalAppDocs(hrmsDocmaps, relatedTo);
                kwlReturnObject3 = hrmsRecJobDAOObj.transferExternalAppDocMaps(hrmsDocmaps, (ArrayList<Docs>) kwlReturnObject2.getEntityList(), relatedTo);
            }
            
            jobj.put("success", result.isSuccessFlag());
            if(result.isSuccessFlag())
            	auditTrailDAOObj.insertAuditLog(AuditAction.TRANSFER_APPLICANT, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has transfered "+request.getParameter("appusername")+"'s applicants data", request, "0");
            
            if(datatransfer.get("count") > 0 && result.getMsg().equals("Applicant data trasfered successfully.")){
                jobj.put("msg", messageSource.getMessage("hrms.recruitment.data.not.transfer.incorrect.mapping", null, RequestContextUtils.getLocale(request)));
            } else { 
                jobj.put("msg", result.getMsg());
            }
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
            if(result.isSuccessFlag()){
                txnManager.commit(status);
            }else{
                txnManager.rollback(status);
            }
        } catch (Exception ex) {
            txnManager.rollback(status);
            ex.printStackTrace();
        } 
        return new ModelAndView(successView,"model",jobj1.toString());
    }

     private String fetchField(JSONObject header, String FieldName, String id, boolean checkdate, HashMap<String,Integer> erequestParams){
        String result = "";
        Integer transferdata=erequestParams.get("count");
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-d");
            if(!header.isNull(FieldName)){
                int index = Integer.parseInt(header.get(FieldName).toString());
                HashMap<String,String> requestParams = new HashMap<String, String>();
                requestParams.put("id", id);
                result = hrmsRecJobDAOObj.fetchField(requestParams, index);
                if(checkdate){
                    Date checkDate = sdf.parse(result);
                }
                if(!StringUtil.isNullOrEmpty(result)){
                    ConfigRecruitmentMaster contyp = (ConfigRecruitmentMaster) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.recruitment.ConfigRecruitmentMaster", result);
                    if(contyp!=null){
                        result = contyp.getMasterdata();
                    }
                }
            }
        } catch (com.krawler.utils.json.base.JSONException ex) {
            result = "";
            transferdata++;
            erequestParams.put("count", transferdata);
        } catch (Exception ex) {
            result = "";
            transferdata++;
            erequestParams.put("count", transferdata);
        } finally {
            return result;
        }
    }
//     public ModelAndView transferappdata(HttpServletRequest request,HttpServletResponse response){
//        KwlReturnObject result = null;
//        JSONObject jobj = new JSONObject();
//        JSONObject jobj1 = new JSONObject();
//        HashMap<String,Object> requestParams = new HashMap<String, Object>();
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
//        try {
//
//            requestParams.put("employeetype", request.getParameter("employeetype"));
//            requestParams.put("employeeid", request.getParameter("employeeid"));
//            requestParams.put("appusername", request.getParameter("appusername"));
//            requestParams.put("applicantid", request.getParameter("applicantid"));
//            requestParams.put("designationid", request.getParameter("designationid"));
//            requestParams.put("departmentid", request.getParameter("departmentid"));
//            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
//            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
//            result = hrmsRecJobDAOObj.transferappdata(requestParams);
//            jobj.put("success", result.isSuccessFlag());
//            jobj.put("msg", result.getMsg());
//            jobj1.put("data", jobj);
//            jobj1.put("valid", true);
//            if(result.isSuccessFlag()){
//                txnManager.commit(status);
//            }else{
//                txnManager.rollback(status);
//            }
//
//        } catch (Exception ex) {
//            txnManager.rollback(status);
//            ex.printStackTrace();
//        }
//        return new ModelAndView(successView,"model",jobj1.toString());
//    }
     private JSONArray getConfigMasterdata(HashMap<String,Object> requestParams, HttpServletRequest request) throws JSONException{
            KwlReturnObject result = null;
            JSONObject jobj = new JSONObject();
            JSONArray jarr = new JSONArray();
            requestParams.put("order_by", Arrays.asList("masterdata"));
            requestParams.put("order_type", Arrays.asList("asc"));
            result = hrmsRecJobDAOObj.getConfigMaster(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigRecruitmentMaster configrecruitmentmaster = (ConfigRecruitmentMaster) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("masterid", configrecruitmentmaster.getMasterid());
                tmpObj.put("masterdata",getConfigMasterStringByLocale(configrecruitmentmaster.getMasterdata(),request));
                jarr.put(tmpObj);
            }
            return jarr;
     }
     private JSONArray getConfigMasterdataWithLocalString(HashMap<String,Object> requestParams, HttpServletRequest request) throws JSONException{
            KwlReturnObject result = null;
            JSONObject jobj = new JSONObject();
            JSONArray jarr = new JSONArray();
            requestParams.put("order_by", Arrays.asList("masterdata"));
            requestParams.put("order_type", Arrays.asList("asc"));
            result = hrmsRecJobDAOObj.getConfigMaster(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigRecruitmentMaster configrecruitmentmaster = (ConfigRecruitmentMaster) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("masterid", configrecruitmentmaster.getMasterid());
                tmpObj.put("masterdata",getConfigMasterStringByLocale(configrecruitmentmaster.getMasterdata(),request));
                jarr.put(tmpObj);
            }
            return jarr;
     }
    
     public String getConfigMasterStringByLocale(String str,HttpServletRequest request){
         String st2=StringUtil.replaceNametoLocalkey(str);
             str=messageSource.getMessage("hrms.common.MasterConfig."+st2, null,str, RequestContextUtils.getLocale(request));
         return str;
     }
     public ModelAndView getConfigMaster(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("configid.configid"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("configid")));
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("searchcol", new String[]{"name"});
            requestParams.put("order_by", Arrays.asList("masterdata"));
            requestParams.put("order_type", Arrays.asList("asc"));
            result = hrmsRecJobDAOObj.getConfigMaster(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigRecruitmentMaster configrecruitmentmaster = (ConfigRecruitmentMaster) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("masterid", configrecruitmentmaster.getMasterid());
                tmpObj.put("masterdata", configrecruitmentmaster.getMasterdata());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }
     public ModelAndView addConfigMaster(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        try {
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("Configid", request.getParameter("configid"));
                requestParams.put("Masterdata", request.getParameter("masterdata"));
                if(request.getParameter("masterid")!=null){
                    requestParams.put("Masterid", request.getParameter("masterid"));
                }
                
                result = hrmsRecJobDAOObj.addConfigMaster(requestParams);
                jobj.put("success", "msg");
                jobj.put("title", "Alert");
                jobj.put("msg", result.getMsg());
                jobj1.put("data", jobj);
                jobj1.put("valid", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return  new ModelAndView(successView,"model",jobj1.toString());
        }
    }
     public ModelAndView deleteConfigMaster(HttpServletRequest request,HttpServletResponse response){
        String delid = request.getParameter("delid");
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
            JSONArray jarr = new JSONArray(delid);
            for (int ctr = 0; ctr < jarr.length(); ctr++) {
                    result =hrmsRecJobDAOObj.deleteConfigMaster(jarr.get(ctr).toString());
                    jobj.put("success", "true");
                    jobj1.put("data", jobj);
                    jobj1.put("valid", true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }
     public ModelAndView updownConfigRecruitment(HttpServletRequest request,HttpServletResponse response){
        
        KwlReturnObject result = null;
        boolean success =false;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        HashMap<String,Object> requestParams = new HashMap<String, Object>();
        try {
                        int position = Integer.parseInt(request.getParameter("position"));
                        int positioninc = Integer.parseInt(request.getParameter("positioninc"));
                        String configid = request.getParameter("configid");
                        String formtype = request.getParameter("formtype");
                        requestParams.put("filter_names", Arrays.asList("formtype","position","company.companyID"));
                        requestParams.put("filter_values", Arrays.asList(formtype, position+positioninc, sessionHandlerImplObj.getCompanyid(request)));
                        result = hrmsRecJobDAOObj.getConfigRecruitment(requestParams);
                        List lst = result.getEntityList();
                        Iterator ite = lst.iterator();
                        if (ite.hasNext()) {
                            ConfigRecruitment contyp = (ConfigRecruitment) ite.next();
                            contyp.setPosition(position);
                            success = hrmsRecJobDAOObj.saveConfigRecruitment(contyp);
                        }

                        if(success){
                            hrmsRecJobDAOObj.updownConfigRecruitment(configid,positioninc);
                            jobj.put("success", "true");
                        }else{
                            jobj.put("success", "false");
                        }

                        jobj1.put("data", jobj);
                        jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView(successView,"model",jobj1.toString());
    }
     public ModelAndView jobsExport(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 =  new JSONObject();
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try{

            jobj = getJobs(request,true);
            exportDAOImplObj.processRequest(request, response, jobj);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView sendLetters(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            //code for history of emails to be sent
          //  String records = request.getParameter("records");
            JSONArray recordsArr = new JSONArray(request.getParameter("userlist"));
     //       HtmlTemplate ht = (HtmlTemplate)hibernateTemplate.get(HtmlTemplate.class, request.getParameter("templateid"));

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            Date curDate = new Date();
            for(int i=0;i<recordsArr.length();i++){
//           User usr = (User)hibernateTemplate.get(User.class, recordsArr.getJSONObject(i).getString("uid"));
//              Allapplications ap = (Allapplications)hibernateTemplate.get(Allapplications.class, recordsArr.getJSONObject(i).getString("uid"));

               // if(usr==null)continue;

          //      requestParams.put("user", usr);
                requestParams.put("appid", recordsArr.getJSONObject(i).getString("uid"));
                requestParams.put("emailid", recordsArr.getJSONObject(i).getString("emailid"));
                requestParams.put("templateid", request.getParameter("templateid"));
      //          requestParams.put("templatename",ht.getName());
                requestParams.put("savedate", curDate);
                Locale locale = RequestContextUtils.getLocale(request);
                requestParams.put("locale", locale);
                result = hrmsRecJobDAOObj.saveLetterHistory(requestParams);
                requestParams.clear();

            }


            //txnManager.commit(status);//transaction commits here



            //code for sending emails start here
            requestParams.put("userid",sessionHandlerImplObj.getUserid(request));
            List usrObj = hrmsRecJobDAOObj.getUser(requestParams).getEntityList();
            String fromName = "";
            String fromEmail = "";
            if(usrObj.size()>0){
                User senderU = (User)usrObj.get(0);
                fromName = senderU.getFirstName()+"  "+senderU.getLastName();
                fromEmail = senderU.getEmailID();
            }

            requestParams.clear();

            requestParams.put("templateid",request.getParameter("templateid"));
            List htmltemplateObj = hrmsRecJobDAOObj.getHtmlTemplate(requestParams).getEntityList();
            HtmlTemplate ht = (HtmlTemplate) htmltemplateObj.get(0);
            String mail_subject = ht.getSubject()==null?"":ht.getSubject();
            String htmlLetter = ht.getText();
            if(htmlLetter==null)htmlLetter = "";
            String textLetter = URLDecoder.decode(htmlLetter, "utf-8");
            htmlLetter = URLDecoder.decode(htmlLetter, "utf-8");
            ArrayList<String[]> al = getPlaceHolderPairs(textLetter);

            ArrayList<String[]> phPaths = getPlaceHolderPathLookup(al);

            String cid = sessionHandlerImplObj.getCompanyid(request);
            String toEmail = null;
            ArrayList<String[]> userBasedLetterValues = null;
            for(int i=0;i<recordsArr.length();i++){//user based code for iteration
             //applicant id =   recordsArr.getJSONObject(i).getString("uid");
                //code for getting user values remained



                userBasedLetterValues/*for single user*/= getUserLetterValues(recordsArr.getJSONObject(i).getString("uid"), cid, phPaths);
                StringBuffer textletter_sbf = new StringBuffer(textLetter);
                StringBuffer htmlletter_sbf = new StringBuffer(htmlLetter);

                getConvertedLetter(textletter_sbf, htmlletter_sbf, userBasedLetterValues);//converting letter

                toEmail = recordsArr.getJSONObject(i).getString("emailid");

                //String[] recipients,String subject,String htmlMsg,String plainMsg,String fromAddress,String fromName
                SendMailHandler.postMail(new String[]{toEmail}, mail_subject,htmlletter_sbf.toString(), textletter_sbf.toString(), fromEmail, fromName);
                if(result.isSuccessFlag())
                	auditTrailDAOObj.insertAuditLog(AuditAction.SEND_LETTERS, "User  " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has send letter to "+recordsArr.getJSONObject(i).getString("uname"), request, "0");
            }
            txnManager.commit(status);//transaction commits here
        } catch (Exception e) {
            e.printStackTrace();
            result = new KwlReturnObject(false, "{\"valid\":\"true\",\"success\":\"true\",data:{msg:\""+e.getMessage()+"\",value:\"failed\"}}", "", null, 0);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ArrayList<String[]> getPlaceHolderPairs(String /*Buffer*/ htmlmsg) {
        ArrayList<String[]> al = new ArrayList();//#first:second#
        String expr = "[#]{1}[a-zA-Z]+[:]{1}[a-zA-Z]+[#]{1}";
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(htmlmsg);
        while (m.find()) {
            String table = "";
            String woHash = "";
            table = m.group();
            woHash = table.substring(1, table.length() - 1);
            String[] sp = woHash.split(":");
            al.add(new String[]{sp[0],sp[1]});
//alternate            al.add(woHash.split(":"));
        }
        return al;
    }

    public ArrayList<String[]> getPlaceHolderPathLookup(ArrayList<String[]> placeholders){
        ArrayList<String[]> phValuePairs = new ArrayList<String[]>();
        int i=0;
        while (i<placeholders.size()) {
            String phlStr="";
            if(!placeholders.get(i)[1].equals("cmail") && !placeholders.get(i)[1].equals("cname") && !placeholders.get(i)[1].equals("currentyear")){
                phlStr = getPlaceHolderLookupString(placeholders.get(i)[0],placeholders.get(i)[1]);
                phlStr +=getPlaceHolderDestColumn(placeholders.get(i)[0],placeholders.get(i)[1]);
            } else {
                if(placeholders.get(i)[1].equals("cmail"))
                    phlStr = "cmail";
                else if(placeholders.get(i)[1].equals("cname"))
                    phlStr = "cname";
                else if(placeholders.get(i)[1].equals("currentyear"))
                    phlStr = "currentyear";
            }

            phValuePairs.add(new String[]{placeholders.get(i)[0],placeholders.get(i)[1],phlStr});
            i++;
        }
        return phValuePairs;
    }

    public String getPlaceHolderLookupString(String fst, String snd /*HttpServletRequest request, HttpServletResponse response*/) {
        String retStr = "";
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("dest_table", fst);
            retStr = hrmsRecJobDAOObj.getPlaceHolderLookupString(requestParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return retStr;
        }
    }

    public String getPlaceHolderDestColumn(String fst, String snd) {
        List lst = getPlaceHolderData(fst, snd);
        if(lst.size()>0){
            PlaceHolder ph = (PlaceHolder)lst.get(0);
            return ph.getDest_column();
        }
        return null;
    }

    public List getPlaceHolderData(String fst, String snd) {
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("phs1", fst);
            requestParams.put("phs2", snd);
            result = hrmsRecJobDAOObj.getPlaceHolderData(requestParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error is " + e);
        } finally {
            return result.getEntityList();
        }
    }

    public ArrayList<String[]> getUserLetterValues(String applicant_id, String company_id,ArrayList<String[]> placeholderNPaths) throws ServiceException {
        ArrayList<String[]> phValuePairs = new ArrayList<String[]>();
        //code for user based values
        int i=0;
        KwlReturnObject result = null;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        while (i<placeholderNPaths.size()) {
            requestParams.put("applicant_id", applicant_id);
            requestParams.put("getph", placeholderNPaths.get(i)[2]);
            requestParams.put("gettable", placeholderNPaths.get(i)[0]);
            requestParams.put("companyid", company_id);
            result = hrmsRecJobDAOObj.getPlaceHolderUserValue(requestParams);
            String phlStr = "";
            if(result.getEntityList().size()>0){
              phlStr = result.getEntityList().get(0)!=null?((String)result.getEntityList().get(0)):"";
            } else {
                throw ServiceException.FAILURE("This Email Template is created for "+placeholderNPaths.get(i)[0]+".", null);
            }
            phValuePairs.add(new String[]{placeholderNPaths.get(i)[0],placeholderNPaths.get(i)[1],phlStr});
            i++;
            requestParams.clear();
        }
        return phValuePairs;
    }

    public void getConvertedLetter(StringBuffer textLetter,StringBuffer htmlLetter,ArrayList<String[]> phValuePairs){
        String expr = "[#]{1}[a-zA-Z]+[:]{1}[a-zA-Z]+[#]{1}";
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(textLetter);
        int st_i = 0;
        int end_i = 0;
        boolean matched = false;
        while (m.find()) {
            String table = "";
          //  String woHash = "";
            table = m.group();
          //   woHash = table.substring(1, table.length() - 1);
            // String[] sp = woHash.split(":");
            for(int i=0;i<phValuePairs.size() ;i++){
                if(table.equals("#"+phValuePairs.get(i)[0]+":"+phValuePairs.get(i)[1]+"#")){
                    st_i = textLetter.indexOf(table);
                    end_i = st_i+table.length();
                    if(st_i>=0){
                        textLetter.replace(st_i,end_i, phValuePairs.get(i)[2]);
                        //moved in last m = p.matcher(textLetter);
                        //break;
                    }
                    st_i = htmlLetter.indexOf(table);
                    end_i = st_i+table.length();
                    if(st_i>=0){
                        htmlLetter.replace(st_i,end_i, phValuePairs.get(i)[2]);
                      //  break;
                    }
                    m = p.matcher(textLetter);
                    matched = true;
                    break;
                }
            }
            if(!matched){
                st_i = textLetter.indexOf(table);
                end_i = st_i+table.length();
                if(st_i>=0){
                    textLetter.replace(st_i,end_i, "@~"+table.substring(1, table.length()-1)+"@~");
                    //moved in last m = p.matcher(textLetter);
                    //break;
                }
                st_i = htmlLetter.indexOf(table);
                end_i = st_i+table.length();
                if(st_i>=0){
                    htmlLetter.replace(st_i,end_i, "@~"+table.substring(1, table.length()-1)+"@~");
                  //  break;
                }
                m = p.matcher(textLetter);

                matched = false;
            }
        }

    }
    
    public void getMyAdvanceSearchparams(String Searchjson, List filter_names) throws Exception{
        JSONObject jobj = new JSONObject(Searchjson);
        int count = jobj.getJSONArray("root").length();
        for (int i = 0; i < count; i++) {
            JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
            if(jobj1.getString("xtype").equals("datefield")){
                  filter_names.add(">="+jobj1.getString("column"));
                  filter_names.add("<="+jobj1.getString("column"));
            } else {
            	if(jobj1.getString("xtype").equals("numberfield")){
            		filter_names.add(jobj1.getString("column"));
            	}else{
            		filter_names.add("LIKE"+jobj1.getString("column"));
            	}
            }
        }
    }
    
    public void insertParamAdvanceSearchString(List al, String Searchjson) throws Exception {
    	JSONObject jobj = new JSONObject(Searchjson);
    	int count = jobj.getJSONArray("root").length();
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    	try {
    		for (int i = 0; i < count; i++) {
    			JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
    			String trimedStr = jobj1.getString("searchText").trim();
    			if(jobj1.getString("xtype").equals("datefield")){
    				String[] dates=trimedStr.split("##");
    				al.add(new Date(df.format(new Date(dates[0].toString()))));
    				al.add(new Date(df.format(new Date(dates[1].toString()))));
    			} else {
    				if(jobj1.getString("xtype").equals("numberfield")){
    					al.add(Integer.parseInt(trimedStr));
    				}else{
    					al.add(trimedStr + "%");
    				}
    			}
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
    public void getMyAdvanceSearchparams1(String Searchjson, List filter_names, int type) throws Exception{
        JSONObject jobj = new JSONObject(Searchjson);
        int count = jobj.getJSONArray("root").length();
        String params2[] ={"concat(employee.firstName,' ',employee.lastName)", "employee.emailID", "employee.contactNumber", "employee.address"};
		String params1[] ={"concat(configjobapplicant.col1,' ',configjobapplicant.col2)", "configjobapplicant.col3", "configjobapplicant.col4", "configjobapplicant.col6"};
        for (int i = 0; i < count; i++) {
            JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
            if(jobj1.getString("xtype").equals("datefield")){
            	filter_names.add(">="+jobj1.getString("column"));
                filter_names.add("<="+jobj1.getString("column"));
            } else {
            	if(jobj1.getString("xtype").equals("numberfield")){
            		filter_names.add(jobj1.getString("column"));
            	}else{
            		if(jobj1.getString("column").equals("1") && type == 0)
            			filter_names.add("LIKE"+params1[0]);
            		else if(jobj1.getString("column").equals("2") && type == 0)
            			filter_names.add("LIKE"+params1[1]);
            		else if(jobj1.getString("column").equals("3") && type == 0)
            			filter_names.add("LIKE"+params1[2]);
            		else if(jobj1.getString("column").equals("4") && type == 0)
            			filter_names.add("LIKE"+params1[3]);
            		else if(jobj1.getString("column").equals("1") && type == 1)
            			filter_names.add("LIKE"+params2[0]);
            		else if(jobj1.getString("column").equals("2") && type == 1)
            			filter_names.add("LIKE"+params2[1]);
            		else if(jobj1.getString("column").equals("3") && type == 1)
            			filter_names.add("LIKE"+params2[2]);
            		else if(jobj1.getString("column").equals("4") && type == 1)
            			filter_names.add("LIKE"+params2[3]);
            		else
            			filter_names.add("LIKE"+jobj1.getString("column"));
            	}
            }
        }
    }
    
    public void insertParamAdvanceSearchString1(List al, String Searchjson, int type) throws Exception {
    	JSONObject jobj = new JSONObject(Searchjson);
    	int count = jobj.getJSONArray("root").length();
    	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    	try {
    		for (int i = 0; i < count; i++) {
    			JSONObject jobj1 = jobj.getJSONArray("root").getJSONObject(i);
    			String trimedStr = jobj1.getString("searchText").trim();
    			if(jobj1.getString("xtype").equals("datefield")){
    				String[] dates=trimedStr.split("##");
    				al.add(new Date(df.format(new Date(dates[0].toString()))));
    				al.add(new Date(df.format(new Date(dates[1].toString()))));
    			} else {
    				if(jobj1.getString("xtype").equals("numberfield")){
    					al.add(Integer.parseInt(trimedStr));
    				}else{
    					al.add(trimedStr + "%");
    				}
    			}
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public ModelAndView getJobApplicationsExport(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jobj = null;
    	JSONObject jobj1 = new JSONObject();
        try {
        	jobj = getJobApplicationsJson(request, response);
        	exportDAOImplObj.processRequest(request, response, jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
 
    public JSONObject getJobApplicationsJson(HttpServletRequest request, HttpServletResponse response){
    	KwlReturnObject result = null,recruitresult = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr =  new JSONArray();
        String hql = "";
        int count = 0;
        boolean isadmin = false;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        int type =0;
        try{
        	DateFormat df = sessionHandlerImplObj.getDateFormatter(request);
        	String Searchjson = request.getParameter("searchJson");
        	List lst = null;
            List lst1 = null;
            String cmpid=sessionHandlerImplObj.getCompanyid(request);
            String userid=sessionHandlerImplObj.getUserid(request);
            requestParams.clear();
            requestParams.put("userid",userid);
            if(hrmsCommonDAOObj.isAdmin(userid)){
                isadmin = true;
            }
            ArrayList params = new ArrayList();
            ArrayList filter_name = new ArrayList();
            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();
            String ss = request.getParameter("ss");
            int statusid=StringUtil.isNullOrEmpty(request.getParameter("statusid"))?-1:Integer.parseInt(request.getParameter("statusid"));
            String tempstatus=statusid==1?"Pending":(statusid==2?"Shortlisted":(statusid==3?"In Process":(statusid==4?"On Hold":"")));
            if (StringUtil.isNullOrEmpty(request.getParameter("userid"))) {
                int emptype = Integer.parseInt(request.getParameter("employeetype"));
                type = emptype;
                int gridst=Integer.parseInt(request.getParameter("gridst"));
                
                String[] searchArray = null;
                String[] searchArray1 = null;
                if(emptype == 1) {//Internal
                    searchArray = new String[]{"employee.firstName","employee.lastName","position.jobid","position.departmentid.value"};
                    params.add(0);
                    filter_name.add("employee.deleteflag");
                }else if(emptype == 2) { // else added to execute only one condition
                    emptype = 1;
                    searchArray = new String[]{"employee.firstName","employee.lastName","position.jobid","position.departmentid.value"};
                    searchArray1 = new String[]{"configjobapplicant.col1", "configjobapplicant.col2","position.jobid","position.departmentid.value"};
                    params.add(0);
                    filter_name.add("employee.deleteflag");
                    params.add(false);
                    filter_name.add("configjobapplicant.deleted");
                } else {//External
                    searchArray = new String[]{"configjobapplicant.col1", "configjobapplicant.col2","position.jobid","position.departmentid.value"};
                    params.add(false);
                    filter_name.add("configjobapplicant.deleted");
                }
                if(emptype==0){
                    emptype = 4; //configjobapplicant=4 reset to 0
                }
                if(StringUtil.isNullOrEmpty(request.getParameter("status"))){
                    params.add(gridst);
                    params.add(cmpid);
                    params.add(0);
                    params.add(emptype);
                    //params.add(false);
                    filter_name.add("applicationflag");
                    filter_name.add("company.companyID");
                    filter_name.add("delflag");
                    filter_name.add("employeetype");
                    //filter_name.add("configjobapplicant.deleted");
                } else {
                    if (!tempstatus.equals("")) {
                            params.add(gridst);
                            params.add(cmpid);
                            params.add(0);
                            params.add(tempstatus);
                            params.add(emptype);
                            filter_name.add("applicationflag");
                            filter_name.add("company.companyID");
                            filter_name.add("delflag");
                            filter_name.add("status");
                            filter_name.add("employeetype");
                        } else {
                            params.add(gridst);
                            params.add(cmpid);
                            params.add(0);
                            params.add(emptype);
                            filter_name.add("applicationflag");
                            filter_name.add("company.companyID");
                            filter_name.add("delflag");
                            filter_name.add("employeetype");
                        }
                }
                if(!isadmin){
                    params.add(userid);
                    filter_name.add("position.manager.userID");
                }
              order_by.add("position.positionid");
              order_type.add("asc");
              requestParams.put("filter_names", filter_name);
              requestParams.put("filter_values", params);

              ArrayList params1 = new ArrayList(params);
              ArrayList filter_name1 = new ArrayList(filter_name);
              if(filter_name1.contains("employee.deleteflag")){
            	  params1.remove(filter_name1.indexOf("employee.deleteflag"));
            	  filter_name1.remove("employee.deleteflag");
              }
              if(filter_name.contains("configjobapplicant.deleted") && !(emptype==4)){
            	  params.remove(filter_name.indexOf("configjobapplicant.deleted"));
            	  filter_name.remove("configjobapplicant.deleted");
              }
              requestParams.put("filter_names1", filter_name1);
              requestParams.put("filter_values1", params1);

              requestParams.put("order_by", order_by);
              requestParams.put("order_type", order_type);
              requestParams.put("ss", request.getParameter("ss"));
              requestParams.put("searchcol", searchArray);
              requestParams.put("searchcol1", searchArray1);
              requestParams.put("allflag", true);
              if (!StringUtil.isNullOrEmpty(Searchjson)) {
            	  getMyAdvanceSearchparams1(Searchjson, filter_name, (type!=2?type:1));
                  insertParamAdvanceSearchString1(params, Searchjson, type);
                  getMyAdvanceSearchparams1(Searchjson, filter_name1, (type!=2?type:0));
                  insertParamAdvanceSearchString1(params1, Searchjson, type);
              }
              StringUtil.checkpaging(requestParams, request);
              result = hrmsRecJobDAOObj.getPositionstatus(requestParams);
            } else {
                  params.add(request.getParameter("userid"));
                  params.add(cmpid);
                  params.add(0);
                  params.add(false);
                  filter_name.add("configjobapplicant.id");
                  filter_name.add("company.companyID");
                  filter_name.add("delflag");
                  filter_name.add("configjobapplicant.deleted");
                  order_by.add("position.positionid");
                  order_type.add("asc");
                  requestParams.put("filter_names", filter_name);
                  requestParams.put("filter_values", params);
                  requestParams.put("order_by", order_by);
                  requestParams.put("order_type", order_type);
                  requestParams.put("ss", request.getParameter("ss"));
                  requestParams.put("searchcol", new String[]{"position.jobid","position.position.value"});
                  requestParams.put("allflag", true);
                  if (!StringUtil.isNullOrEmpty(Searchjson)) {
                      getMyAdvanceSearchparams1(Searchjson, filter_name, type);
                      insertParamAdvanceSearchString1(params, Searchjson, type);
                  }
                  StringUtil.checkpaging(requestParams, request);
                  result = hrmsRecJobDAOObj.getPositionstatus(requestParams);

            }
            Allapplications allapps = null;
            count = result.getRecordTotalCount();
            lst = result.getEntityList();
            for (int ctr=0;ctr<lst.size();ctr++) {
//                if(isadmin){
                    allapps=(Allapplications) lst.get(ctr);
//                }else{
//                    Recruiter rec=(Recruiter) lst.get(ctr);
//                    allapps = rec.getAllapplication();
//                }

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("rejectedbefore", (allapps.getRejectedbefore()==0)?"No":"Yes");
            	tmpObj.put("id", "");
                tmpObj.put("posid", allapps.getPosition().getPositionid());
                tmpObj.put("designationid", allapps.getPosition().getPosition().getId());
                tmpObj.put("departmentid", allapps.getPosition().getDepartmentid().getId());
                tmpObj.put("department", allapps.getPosition().getDepartmentid().getValue());
                tmpObj.put("Department", allapps.getPosition().getDepartmentid().getValue());
                tmpObj.put("designation", allapps.getPosition().getPosition().getValue());
                tmpObj.put("jobpositionid", allapps.getPosition().getJobid());
                tmpObj.put("jobid", allapps.getPosition().getJobid());
                tmpObj.put("JobId", allapps.getPosition().getJobid());
                tmpObj.put("vacancy", allapps.getPosition().getNoofpos());
                tmpObj.put("filled", allapps.getPosition().getPositionsfilled());
                tmpObj.put("jname", allapps.getPosition().getPosition().getValue());
                tmpObj.put("applydt", df.format(allapps.getApplydate()));
                tmpObj.put("interviewdt", (allapps.getInterviewdate() == null ? "" : df.format(allapps.getInterviewdate())));
                tmpObj.put("joiningdate", (allapps.getJoiningdate() == null ? "" : df.format(allapps.getJoiningdate())));
                tmpObj.put("status", allapps.getStatus());
                tmpObj.put("jobDetails", allapps.getPosition().getDetails());
                if (allapps.getConfigjobapplicant() != null) {
                    tmpObj.put("apcntid", allapps.getConfigjobapplicant().getId());
                    tmpObj.put("cname", allapps.getConfigjobapplicant().getCol1() + " " + allapps.getConfigjobapplicant().getCol2());
                    tmpObj.put("email", allapps.getConfigjobapplicant().getCol3());
                    tmpObj.put("fname", allapps.getConfigjobapplicant().getCol1());
                    tmpObj.put("lname", allapps.getConfigjobapplicant().getCol2());
                    tmpObj.put("contact", allapps.getConfigjobapplicant().getCol4()!=null?allapps.getConfigjobapplicant().getCol4():"");
                    tmpObj.put("addr", allapps.getConfigjobapplicant().getCol6()!=null?allapps.getConfigjobapplicant().getCol6():"");
                    tmpObj.put("employeetype", 0);
                    if(!StringUtil.isNullOrEmpty(allapps.getConfigjobapplicant().getCol5())){
                        tmpObj.put("docid", allapps.getConfigjobapplicant().getCol5());
                    }
//                    getdocsbyuser(request,allapps.getConfigjobapplicant().getId(),tmpObj);
                }else{
                    tmpObj.put("apcntid", allapps.getEmployee().getUserID());
                    tmpObj.put("cname", allapps.getEmployee().getFirstName() + " " + allapps.getEmployee().getLastName());
                    tmpObj.put("email", allapps.getEmployee().getEmailID());
                    tmpObj.put("fname", allapps.getEmployee().getFirstName());
                    tmpObj.put("lname", allapps.getEmployee().getLastName()!=null?allapps.getEmployee().getLastName():"");
                    tmpObj.put("contact", allapps.getEmployee().getContactNumber()!=null?allapps.getEmployee().getContactNumber():"");
                    tmpObj.put("addr", allapps.getEmployee().getAddress()!=null?allapps.getEmployee().getAddress():"");
                    tmpObj.put("employeetype", 1);
                }
                
                    ArrayList recruiterparams = new ArrayList();
                    recruiterparams.add(allapps.getId());
                    ArrayList filter_names = new ArrayList();
                    filter_names.add("allapplication.id");
                    requestParams.clear();
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", recruiterparams);
                    recruitresult = hrmsRecJobDAOObj.getRecruiters(requestParams);
                    String recruiter = "";
                    if (StringUtil.checkResultobjList(recruitresult)) {
                        List recruiterlist = recruitresult.getEntityList();
                        for (int k = 0; k < recruiterlist.size(); k++) {
                            Recruiter r = (Recruiter) recruiterlist.get(k);
                            recruiter += (r.getRecruit().getFirstName() + " " + r.getRecruit().getLastName());
                            if(recruiterlist.size()>k+1)
                            	recruiter += ", ";
                        }
                    }
                    tmpObj.put("recruiter", recruiter);
                    tmpObj.put("callback",(allapps.getCallback() == null ? "" :allapps.getCallback().getValue()));
                    tmpObj.put("interviewplace", allapps.getInterviewplace());
                    tmpObj.put("interviewcomment", allapps.getInterviewcomment());
                    tmpObj.put("rank",(allapps.getRank() == null ? "" :allapps.getRank().getValue()));
                    jarr.put(tmpObj);
                }
            jobj.put("data", jarr);
            jobj.put("count", count);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	return jobj;
        }
    }
    
    public ModelAndView getExternalApplicantForExport(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException, SessionExpiredException {
    	JSONObject jobj = null;
    	JSONObject jobj1 = new JSONObject();
    	try {
    		request.setAttribute("isExport", true);
    		jobj = getExternalApplicantJSON(request, response);
            exportDAOImplObj.processRequest(request, response, jobj);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    public JSONObject getExternalApplicantJSON(HttpServletRequest request, HttpServletResponse response){
    	KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        int count = 0;
        try{
        	String Searchjson = request.getParameter("searchJson");
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(cmpid);
            params.add(false);
            ArrayList filter_names = new ArrayList();
            filter_names.add("company.companyID");
            filter_names.add("deleted");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", params);
            requestParams.put("searchcol", new String[]{"col1", "col2", "col3"});
            if (!StringUtil.isNullOrEmpty(Searchjson)) {
                getMyAdvanceSearchparams(Searchjson, filter_names);
                insertParamAdvanceSearchString(params, Searchjson);
            }
            if(!Boolean.parseBoolean(request.getAttribute("isExport").toString())){
            	Integer start = Integer.parseInt(request.getParameter("start"));
            	Integer limit = Integer.parseInt(request.getParameter("limit"));
            	requestParams.put("start", start);
            	requestParams.put("limit", limit);
            	requestParams.put("allflag", false);
            }
            requestParams.put("ss", ss);
            result = hrmsRecJobDAOObj.getConfigJobApplicant(requestParams);
            List lst1 = result.getEntityList();
            count = result.getRecordTotalCount();
            JSONArray jArr = new JSONArray();
            for(int ctr=0;ctr<lst1.size();ctr++){
                ConfigRecruitmentData extmt = (ConfigRecruitmentData) lst1.get(ctr);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("lid", extmt.getId());
                tmpObj.put("cname", extmt.getCol1() + " " + extmt.getCol2());
                tmpObj.put("contactno", extmt.getCol4()!=null?extmt.getCol4():"");
                tmpObj.put("uname", extmt.getCol3()!=null?extmt.getCol3():"");
                tmpObj.put("email", extmt.getCol3()!=null?extmt.getCol3():"");
                jArr.put(tmpObj);
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	return jobj;
        }
    }
    
    public ModelAndView exportAllApplications(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jobj = null;
    	JSONObject jobj1 = new JSONObject();
        try {
        	String header = getExtApplicantDataHeader(request, response);
        	String align = "";
        	String width = "";
        	String[] headerArr = header.split(",");
        	for (int i = 0; i < headerArr.length; i++) {
				align += "center";
				width +="80";
				if(headerArr.length<(i+1)){
					align += ",";
					width +=",";
				}
			}
        	request.setAttribute("align", align);
        	request.setAttribute("width", width);
        	request.setAttribute("header", header);
        	request.setAttribute("title", header);
        	JSONArray jsonArray = getExtApplicantData(request, response);
        	jobj = new JSONObject();
        	jobj.put("data", jsonArray);
        	jobj.put("count", jsonArray.length());
        	exportDAOImplObj.exportConfigData(request, response, jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    } 
    
    public String getExtApplicantDataHeader(HttpServletRequest request, HttpServletResponse response){
    	HashMap<String, Object> requestParams = new HashMap<String, Object>();
    	List<ConfigRecruitment> configRecruitments = null;
    	Iterator<ConfigRecruitment> iterator = null;
    	String header = "";
    	try{
    		requestParams.put("filter_names", Arrays.asList("company.companyID","visible"));
    		requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request),Boolean.parseBoolean(request.getParameter("visible"))));
    		requestParams.put("order_by", Arrays.asList("colnum"));
            requestParams.put("order_type", Arrays.asList("asc"));
    		configRecruitments = hrmsRecJobDAOObj.getConfigRecruitment(requestParams).getEntityList();
    		iterator = configRecruitments.iterator();
    		String name="";
    		while(iterator.hasNext()){
    			name = iterator.next().getName();
    			header += messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(name), null, name, RequestContextUtils.getLocale(request));
    			if(iterator.hasNext())
    				header += ",";
    		}
    	} catch (Exception e) {
            e.printStackTrace();
        } finally {
            return header;
        }
    }
    
    public JSONArray getExtApplicantData(HttpServletRequest request, HttpServletResponse response){
    	HashMap<String, Object> requestParams = new HashMap<String, Object>();
    	List<ConfigRecruitment> configRecruitments = null;
    	Iterator<ConfigRecruitment> iterator1 = null;
    	JSONObject jsonObject = null;
    	JSONArray jsonArray = new JSONArray();
    	try{
    		String companyId = sessionHandlerImplObj.getCompanyid(request);
    		Class configrecdata = Class.forName("com.krawler.hrms.recruitment.ConfigRecruitmentData");
    		requestParams.put("filter_names", Arrays.asList("company.companyID","visible"));
    		requestParams.put("filter_values", Arrays.asList(companyId, Boolean.parseBoolean(request.getParameter("visible"))));
    		requestParams.put("order_by", Arrays.asList("colnum"));
            requestParams.put("order_type", Arrays.asList("asc"));
    		configRecruitments = hrmsRecJobDAOObj.getConfigRecruitment(requestParams).getEntityList();
    		requestParams.clear();
    		
    		ArrayList<Object> filter_names = new ArrayList<Object>();
    		ArrayList<Object> filter_values = new ArrayList<Object>();
    		String allApplicationList = request.getParameter("allApplicationList");
    		boolean isAllApplicationList = allApplicationList!=null?Boolean.parseBoolean(allApplicationList):false;
    		if(isAllApplicationList){
    			List<ConfigRecruitmentData> configRecruitmentDatas = null;
    			Iterator<ConfigRecruitmentData> iterator2 = null;
    			filter_names.add("company.companyID");
    			filter_names.add("deleted");
    			    		
    			filter_values.add(companyId);
    			filter_values.add(false);
    			requestParams.put("filter_names", filter_names);
    			requestParams.put("filter_values", filter_values);
    			
    			String Searchjson = request.getParameter("searchJson");
    			if(!StringUtil.isNullOrEmpty(Searchjson) && !Searchjson.equals("undefined")){
    				getMyAdvanceSearchparams1(Searchjson, filter_names, 0);
    				insertParamAdvanceSearchString1(filter_values, Searchjson, 0);
    			}
    			
    			configRecruitmentDatas = hrmsRecJobDAOObj.getConfigJobApplicant(requestParams).getEntityList();
    			iterator2 = configRecruitmentDatas.iterator();
    			while(iterator2.hasNext()){
    				ConfigRecruitmentData configRecruitmentData = iterator2.next();
    				jsonObject = new JSONObject();
    				iterator1 = configRecruitments.iterator();
    				while(iterator1.hasNext()){
    					ConfigRecruitment configRecruitment = iterator1.next();
    					if(configRecruitment.getConfigtype()==3){
    						Method getter = configrecdata.getMethod("getCol"+configRecruitment.getColnum());
    						Object object = getter.invoke(configRecruitmentData);
    						requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("masterid"));
                            requestParams.put("filter_values", Arrays.asList(object));
                            List<ConfigRecruitmentMaster> configRecruitmentMasters = hrmsRecJobDAOObj.getConfigMaster(requestParams).getEntityList();
                            if(!configRecruitmentMasters.isEmpty() && configRecruitmentMasters.size()>0 && configRecruitmentMasters.get(0)!=null){
                            	jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), configRecruitmentMasters.get(0).getMasterdata()!=null?configRecruitmentMasters.get(0).getMasterdata():"");
                            }else{
                            	jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), "");
                            }
    					}else{
    						Method getter = configrecdata.getMethod("getCol"+configRecruitment.getColnum());
							Object object = getter.invoke(configRecruitmentData);
    						if(configRecruitment.getConfigtype()==5){
    							jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), object!=null?(object.equals("")?"No":"Yes"):"No");
    						}else{
    							jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), object!=null?object:"");
    						}
    					}
    				}
    				jsonArray.put(jsonObject);
    			}
    		}else{
    			String isStatusApplicable = request.getParameter("isStatusApplicable");
        		boolean isStatus = isStatusApplicable!=null?Boolean.parseBoolean(isStatusApplicable):false;
    			List<Allapplications> allapplications = null;
    			Iterator<Allapplications> iterator2 = null;
    			filter_names.add("configjobapplicant.company.companyID");
    			filter_names.add("configjobapplicant.deleted");
    			filter_names.add("delflag");
    			filter_names.add("applicationflag");
    		
    			filter_values.add(companyId);
    			filter_values.add(false);
    			filter_values.add(0);
    			filter_values.add(Integer.parseInt(request.getParameter("applicationflag")));
    			
    			String Searchjson = request.getParameter("searchJson");
    			if(!StringUtil.isNullOrEmpty(Searchjson) && !Searchjson.equals("undefined")){
    				getMyAdvanceSearchparams1(Searchjson, filter_names, 0);
    				insertParamAdvanceSearchString1(filter_values, Searchjson, 0);
    			}
    			
    			if(isStatus){
    				filter_names.add("status");
    				filter_values.add(request.getParameter("status"));
    			}
    			requestParams.put("filter_names", filter_names);
    			requestParams.put("filter_values", filter_values);
    			allapplications = hrmsRecJobDAOObj.getPositionstatus(requestParams).getEntityList();
    			iterator2 = allapplications.iterator();
    			while(iterator2.hasNext()){
    				Allapplications allapplication = iterator2.next();
    				jsonObject = new JSONObject();
    				iterator1 = configRecruitments.iterator();
    				while(iterator1.hasNext()){
    					ConfigRecruitment configRecruitment = iterator1.next();
    					if(configRecruitment.getConfigtype()==3){
    						Method getter = configrecdata.getMethod("getCol"+configRecruitment.getColnum());
    						Object object = getter.invoke(allapplication.getConfigjobapplicant());
    						requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("masterid"));
                            requestParams.put("filter_values", Arrays.asList(object));
                            List<ConfigRecruitmentMaster> configRecruitmentMasters = hrmsRecJobDAOObj.getConfigMaster(requestParams).getEntityList();
                            if(!configRecruitmentMasters.isEmpty() && configRecruitmentMasters.size()>0 && configRecruitmentMasters.get(0)!=null){
                            	jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), configRecruitmentMasters.get(0).getMasterdata()!=null?configRecruitmentMasters.get(0).getMasterdata():"");
                            }else{
                            	jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), "");
                            }
    					}else{
    						Method getter = configrecdata.getMethod("getCol"+configRecruitment.getColnum());
    						Object object = getter.invoke(allapplication.getConfigjobapplicant());
    						if(configRecruitment.getConfigtype()==5){
    							jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), object!=null?(object.equals("")?"No":"Yes"):"No");
    						}else{
    							jsonObject.put(messageSource.getMessage("hrms.recruitment."+StringUtil.mergeWithDots(configRecruitment.getName()), null, configRecruitment.getName(), RequestContextUtils.getLocale(request)), object!=null?object:"");
    						}
    					}
    				}
    				jsonArray.put(jsonObject);
    			}
    		}
    	} catch (Exception e) {
            e.printStackTrace();
        } finally {
            return jsonArray;
        }
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}

