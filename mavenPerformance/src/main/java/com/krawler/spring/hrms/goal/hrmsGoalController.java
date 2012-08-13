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
package com.krawler.spring.hrms.goal;

import com.krawler.common.admin.User;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.GoalComments;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

public class hrmsGoalController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsGoalDAO hrmsGoalDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
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

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public hrmsGoalDAO getHrmsGoalDAO() {
        return hrmsGoalDAOObj;
    }

    public void setHrmsGoalDAO(hrmsGoalDAO hrmsGoalDAOObj) {
        this.hrmsGoalDAOObj = hrmsGoalDAOObj;
    }

    public profileHandlerDAO getProfileHandlerDAO() {
        return profileHandlerDAOObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public ModelAndView Employeesgoalfinal(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        List lst = null;
        int start, limit;
        DateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            
            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                requestParams.put("empid", sessionHandlerImplObj.getUserid(request));

                requestParams.put("user", "user");
            } else {
                requestParams.put("managerid", sessionHandlerImplObj.getUserid(request));
                requestParams.put("empid", request.getParameter("empid"));
                requestParams.put("user", "manager");
            }
            requestParams.put("Cid",sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("ss",request.getParameter("ss"));
            if (request.getParameter("start") == null) {
                start = 0;
                limit = 15;
            } else {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            result = hrmsGoalDAOObj.Employeesgoalfinal(requestParams);
           lst = result.getEntityList();
           count = result.getRecordTotalCount();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                String gid=fgmt.getId();
                HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                requestParams1.put("goalid",gid);
                result=hrmsGoalDAOObj.getGoalCommentsWithID(requestParams1);
                int cnt = result.getRecordTotalCount();
                tmpObj.put("gid",gid);
                tmpObj.put("gname", fgmt.getGoalname());
                tmpObj.put("gdescription", fgmt.getGoaldesc());
                tmpObj.put("gwth", fgmt.getGoalwth());
                tmpObj.put("gcontext", fgmt.getContext());
                tmpObj.put("percentcomp", (fgmt.getPercentcomplete()==null)?0:fgmt.getPercentcomplete());
                tmpObj.put("gpriority", fgmt.getPriority());
                tmpObj.put("gstartdate", sdf.format(fgmt.getStartdate()));
                tmpObj.put("genddate", sdf.format(fgmt.getEnddate()));
                tmpObj.put("gcomment", cnt);
                tmpObj.put("gassignedby", fgmt.getManager().getFirstName() + " " + (fgmt.getManager().getLastName() == null ? "" : fgmt.getManager().getLastName()));
                tmpObj.put("internal", fgmt.isInternal());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Select Queries
            return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getComments(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject commObj=new JSONObject();
        int count = 0;
        try {
            String goalids[] = request.getParameterValues("recid");
            HashMap<String, Object> requestParams =new HashMap<String, Object>();
            for(int i=0;i<goalids.length;i++){
                requestParams.clear();
                requestParams.put("goalid", goalids[i]);
                result = hrmsGoalDAOObj.getGoalCommentsWithID(requestParams);
            tabledata=result.getEntityList();
                count = result.getRecordTotalCount();
            for (int j = 0; j < count; j++) {
                GoalComments log = (GoalComments) tabledata.get(j);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("commentid", log.getCommentid());
                tmpObj.put("comment", log.getComment());
                tmpObj.put("addedby", StringUtil.getFullName(log.getUserid()));
                tmpObj.put("postedon", kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request),sessionHandlerImplObj.getUserTimeFormat(request),sessionHandlerImplObj.getTimeZoneDifference(request)).format(log.getCreatedon()));
                jarr.put(tmpObj);
            }
            commObj.put("commList",jarr);
            }
            jobj.put("success", true);
            jobj.put("commData", commObj);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView addCommentsfunction(HttpServletRequest request, HttpServletResponse response) {

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
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("goalid", request.getParameterValues("goalid"));
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
            requestParams.put("comment", request.getParameter("comment"));
            result = hrmsGoalDAOObj.addCommentsfunction(requestParams);
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

    public ModelAndView insertGoal(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result;
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
            if (StringUtil.isNullOrEmpty(request.getParameter("archive"))) {
                requestParams.put("archive", "");
            } else {
                requestParams.put("archive", request.getParameter("archive"));
                if(request.getParameter("archive").equals("true")){
            		for(int i=0;i<request.getParameterValues("archiveid").length;i++){
                        auditTrailDAOObj.insertAuditLog(AuditAction.GOAL_ARCHIVED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has archived goal "+ request.getParameterValues("gname")[i], request, "0");
                    }
            	}
                else{
                	for(int i=0;i<request.getParameterValues("archiveid").length;i++){
                		auditTrailDAOObj.insertAuditLog(AuditAction.GOAL_UNARCHIVED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has unarchived goal "+ request.getParameterValues("gname")[i], request, "0");
                	}
                }
            }
            requestParams.put("apprmanager", profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)));
            requestParams.put("empid", request.getParameter("empid"));
            requestParams.put("archiveid", request.getParameterValues("archiveid"));
            requestParams.put("jsondata", request.getParameter("jsondata"));
            requestParams.put("dateformatter",new SimpleDateFormat("MMMM d, yyyy"));
            requestParams.put("managerid",sessionHandlerImplObj.getUserid(request));
            result=hrmsGoalDAOObj.insertGoal(requestParams);
            if(result.isSuccessFlag()&&StringUtil.isNullOrEmpty(request.getParameter("archive"))){
            	List<Finalgoalmanagement> goalsList = result.getEntityList();
            	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                String emailFrom= sessionHandlerImplObj.getSysEmailIdByCompanyID(request);
                User user = null;
                boolean isNewGoals = false;
                String title = messageSource.getMessage("hrms.performance.goals.assigned.manager", null, RequestContextUtils.getLocale(request));
            	String inthtmlmsg = "<html><style type='text/css'>a:link, a:visited, a:active {color: #03C;}body {font-family: Arial, Helvetica, sans-serif; color: #000;font-size: 13px;}</style>";
            	inthtmlmsg+="<body>";
            	JSONArray jarr = new JSONArray(request.getParameter("jsondata"));
            	for (int i = 0; i < goalsList.size(); i++) {
            		Finalgoalmanagement fgmt = goalsList.get(i);
            		user = fgmt.getUserID();
                    JSONObject jobGoal = jarr.getJSONObject(i);
                    if(i==0){
                    	inthtmlmsg+= "<p>"+messageSource.getMessage("hrms.common.hi", null, RequestContextUtils.getLocale(request))+" <b>"+user.getFirstName()+" "+(user.getLastName()!=null?user.getLastName():"")+",</b></p>";
                    	inthtmlmsg+="<p><b>"+requestParams.get("apprmanager").toString()+"</b> "+messageSource.getMessage("hrms.performance.assigned.goals.you", null, RequestContextUtils.getLocale(request))+"</p>";		
                    }
                    if (!jobGoal.has("gid") || jobGoal.getString("gid").equals("undefined")) {
                    	isNewGoals = true;
                    	inthtmlmsg+="<p><b>"+(i+1)+") "+fgmt.getGoalname()+"</b></p>";
                    	inthtmlmsg+="<p>"+messageSource.getMessage("hrms.performance.priority", null, RequestContextUtils.getLocale(request))+": "+fgmt.getPriority()+"</p>";
                    	inthtmlmsg+="<p>"+messageSource.getMessage("hrms.performance.weightage", null, RequestContextUtils.getLocale(request))+": "+fgmt.getGoalwth()+"</p>";
                    	inthtmlmsg+="<p>"+messageSource.getMessage("hrms.common.duration", null, RequestContextUtils.getLocale(request))+": "+messageSource.getMessage("hrms.common.form", null, RequestContextUtils.getLocale(request))+" "+sdf.format(fgmt.getStartdate())+" "+messageSource.getMessage("hrms.common.to.small", null, RequestContextUtils.getLocale(request))+" "+sdf.format(fgmt.getEnddate())+"</p>";
                    	inthtmlmsg+="<p>"+fgmt.getGoaldesc()+"</p>";
                    }
            	}
            	inthtmlmsg+="<p>"+messageSource.getMessage("hrms.common.thanks.regards", null, RequestContextUtils.getLocale(request))+",<br>"+messageSource.getMessage("hrms.common.deskera.hrms.team", null, RequestContextUtils.getLocale(request))+"</p>";
            	inthtmlmsg+="<p>"+messageSource.getMessage("hrms.common.system.generated.message", null, RequestContextUtils.getLocale(request))+"</p>";
            	inthtmlmsg+="</body>";
            	inthtmlmsg+="</html>";
            	if(isNewGoals){
            		try {
            			SendMailHandler.postMail(new String[]{user.getEmailID()}, title, inthtmlmsg, "", emailFrom);
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}
            	
            	auditTrailDAOObj.insertAuditLog(AuditAction.GOAL_ASSIGNED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has assigned new goals "+result.getMsg()+" to " +profileHandlerDAOObj.getUserFullName(request.getParameter("empid")) , request, "0");
            }
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

     public ModelAndView changeMyGoalPercent(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result;
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
            requestParams.put("empid", request.getParameter("empid"));
            requestParams.put("updatedBy", sessionHandlerImplObj.getUserid(request));
            requestParams.put("jsondata", request.getParameter("jsondata"));
            result=hrmsGoalDAOObj.changeMyGoalPercent(requestParams);
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

    public ModelAndView assignedgoalsdelete(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result;
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
            result=hrmsGoalDAOObj.assignedgoalsdelete(requestParams);
            List lst=result.getEntityList();
            for(int i=0;i<result.getRecordTotalCount();i++){
                String[] temp=(String[]) lst.get(i);
//                "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted " + AuthHandler.getFullName(fgmt.getUserID()) + "'s goal " + fgmt.getGoalname();
                //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_DELETED, logtext ,request);
                auditTrailDAOObj.insertAuditLog(AuditAction.GOAL_DELETED, "User  " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has deleted " + profileHandlerDAOObj.getUserFullName(temp[0]) + "'s goal " + temp[1], request, "0");
            }
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

    public ModelAndView archivedgoalsfunction(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject result=null;
        int count = 0;
        String ss = request.getParameter("ss");
        int start, limit;
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("cmp", AuthHandler.getCompanyid(request));
            if (request.getParameter("start") == null) {
                start = 0;
                limit = 15;
            } else {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("ss", ss);

            result=hrmsGoalDAOObj.archivedgoalsfunction(requestParams);
            count=result.getRecordTotalCount();
            List lst=result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                if (fgmt.getUserID().getDeleteflag()==0) {
                    tmpObj.put("empname", fgmt.getUserID().getFirstName() + " " +(fgmt.getUserID().getLastName()==null?"":fgmt.getUserID().getLastName()));
                    tmpObj.put("gid", fgmt.getId());
                    tmpObj.put("gname", fgmt.getGoalname());
                    tmpObj.put("manname", fgmt.getAssignedby());
                    tmpObj.put("gdescription", fgmt.getGoaldesc());
                    tmpObj.put("gwth", fgmt.getGoalwth());
                    tmpObj.put("gcontext", fgmt.getContext());
                    tmpObj.put("gpriority", fgmt.getPriority());
                    tmpObj.put("gstartdate", AuthHandler.getDateFormatter(request).format(fgmt.getStartdate()));
                    tmpObj.put("genddate", AuthHandler.getDateFormatter(request).format(fgmt.getEnddate()));
                    tmpObj.put("gcomment", fgmt.getcomment());
                    tmpObj.put("gassignedby", fgmt.getAssignedby());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            jobj1.put("valid", true);
            jobj1.put("data", jobj.toString());
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
