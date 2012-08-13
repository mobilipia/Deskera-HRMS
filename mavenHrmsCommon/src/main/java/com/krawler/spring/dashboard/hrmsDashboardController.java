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
package com.krawler.spring.dashboard;



import com.krawler.common.admin.User;
import com.krawler.common.admin.widgetManagement;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.krawler.spring.dashboardWidget.widgetDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class hrmsDashboardController extends MultiActionController {
    private String successView;
    private widgetDAO widgetDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsDashboardDAO hrmsDashboardDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImplObj;

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public hrmsCommonDAO getHrmsCommonDAOObj() {
        return hrmsCommonDAOObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public hrmsDashboardDAO getHrmsDashboardDAOObj() {
        return hrmsDashboardDAOObj;
    }

    public void setHrmsDashboardDAO(hrmsDashboardDAO hrmsDashboardDAOObj) {
        this.hrmsDashboardDAOObj = hrmsDashboardDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    
    public widgetDAO getWidgetDAOObj() {
        return widgetDAOObj;
    }

    public void setWidgetDAO(widgetDAO widgetDAOObj) {
        this.widgetDAOObj = widgetDAOObj;
    }

    public String getSuccessView() {
		return successView;
	}

    public void setSuccessView(String successView) {
		this.successView = successView;
    }
    

   public ModelAndView getWidgetStatus(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException {
        JSONObject empty=new JSONObject();
        JSONObject jobj=new JSONObject();
        KwlReturnObject result = null;

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String userId=sessionHandlerImplObj.getUserid(request);
            User userObj =(User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", userId);
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(),filter_values = new ArrayList();
            filter_names.add("user");
            filter_values.add(userObj);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            result = widgetDAOObj.getwidgetManagement(requestParams);
            List lst = result.getEntityList();

            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                widgetManagement wmObj = (widgetManagement) ite.next();
                empty= new JSONObject(wmObj.getWidgetstate());
            }
            if(lst.size()==0) {
                empty=hrmsDashboardDAOObj.getDefaultWidgetState();
                requestParams.clear();
                requestParams.put("Widgetstate", empty.toString());
                requestParams.put("User", userId);
                requestParams.put("Modifiedon", new Date());
                result = widgetDAOObj.insertWidgetManagement(requestParams);
                widgetManagement wmObj = (widgetManagement) result.getEntityList().iterator().next();
                empty= new JSONObject(wmObj.getWidgetstate());

            }
            jobj.put("valid", true);
            jobj.put("data", empty);
            txnManager.commit(status);
        } catch(Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj.toString());
    }

//   public ModelAndView getUpdatesForWidgets(HttpServletRequest request,HttpServletResponse response){
//        JSONObject jobj = new JSONObject();
//        JSONArray jArr = new JSONArray();
//        JSONArray jarr = new JSONArray();
//        JSONArray temp = new JSONArray();
//        try {
//            String start = request.getParameter("start");
//            String limit = request.getParameter("limit");
//            int start1 = Integer.parseInt(start);
//            int limit1 = Integer.parseInt(limit);
//            int diff;
//            String userid = AuthHandler.getUserid(request);
//            StringBuffer usersList = new StringBuffer();
//            usersList.append("'" + userid + "'");
//            String goaldate = "";
//            HashMap<String,Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("checklink", "fullupdates");
//            requestParams.put("companyid", AuthHandler.getCompanyid(request));
//            if (hrmsCommonDAOObj.checkModule(requestParams)) {
//                Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
//                String query = "select distinct appm.appcycle from Appraisalmanagement appm where appm.manager.userID=? and appm.managerstatus=0 and ( appm.appcycle.submitstartdate<= ? and appm.appcycle.submitenddate>=? ) ";
//                List recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request), userdate, userdate});
//                Iterator itr = recordTotalCount.iterator();
//                String cmpsub = AuthHandler.getCmpSubscription(request);
//                User user = (User) session.load(User.class, AuthHandler.getUserid(request));
//
//                while (itr.hasNext()) {
//                    String updateDiv = "";
//                    JSONObject obj = new JSONObject();
//                    Appraisalcycle app = (Appraisalcycle) itr.next();
//                    diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
//                    updateDiv += "Submit the <a href='#' onclick='competencyedit()'> appraisal form </a> for appraisal cycle " + app.getCyclename() + " in <font color='green'> " + diff + "</font> day(s)";
//                    obj.put("update", getContentSpan(updateDiv));
//                    jArr.put(obj);
//                }
//                if (!hrmsManager.checkModule("reviewappraisal", session, request)) {
//                    query = "from Appraisalmanagement appm where appm.managerstatus=1 and appm.reviewstatus!=2 and appm.appcycle.submitenddate<?";
//                    recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{userdate});
//                    itr = recordTotalCount.iterator();
//                    while (itr.hasNext()) {
//                        Appraisalmanagement app = (Appraisalmanagement) itr.next();
//                        app.setReviewstatus(2);
//                        session.update(app);
//                    }
//                }
//                query = "from Appraisalmanagement appm where appm.employee.userID=? and appm.reviewstatus=2 group by appm.appcycle";
//                recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request)});
//                itr = recordTotalCount.iterator();
//                while (itr.hasNext()) {
//                    Appraisalmanagement app = (Appraisalmanagement) itr.next();
//                    if (app.getReviewersubmitdate() != null) {
//                        diff = (int) ((userdate.getTime() - app.getReviewersubmitdate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
//                        if (diff < 10) {
//                            String updateDiv = "";
//                            JSONObject obj = new JSONObject();
//                            updateDiv += "Your <a href='#' onclick='myfinalReport()'>appraisal</a> for appraisal cycle " + app.getAppcycle().getCyclename() + " has been approved";
//                            obj.put("update", getContentSpan(updateDiv));
//                            jArr.put(obj);
//                        }
//                    }
//                }
//
//                if (PermissionHandler.isSubscribed(hrms_Modules.appraisal, cmpsub) &&
//                        hrmsManager.checkModule("appraisal", session, request)) {
//                    query = "select distinct appm.appcycle from Appraisalmanagement appm where appm.employee.userID=? and appm.employeestatus=0 and ( appm.appcycle.submitstartdate<=? and appm.appcycle.submitenddate>=? )";
//                    Object[] object = {AuthHandler.getUserid(request), userdate, userdate};
//                    recordTotalCount = HibernateUtil.executeQuery(session, query, object);
//                    itr = recordTotalCount.iterator();
//
//                    while (itr.hasNext()) {
//                        String updateDiv = "";
//                        JSONObject obj = new JSONObject();
//                        Appraisalcycle app = (Appraisalcycle) itr.next();
//                        diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
//                        updateDiv += "Fill the <a href='#' onclick='myAppraisal()'> appraisal form </a>in <font color='green'> " + diff + "</font> day(s) for appraisal cycle " + app.getCyclename() + " initiated by " + user.getCompany().getCreator().getFirstName() + " " + user.getCompany().getCreator().getLastName();
//                        obj.put("update", getContentSpan(updateDiv));
//                        jArr.put(obj);
//                    }
//                    if (hrmsManager.checkModule("goal", session, request)) {
//                        query = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? order by createdon desc";
//                        recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request), AuthHandler.getCompanyid(request), false});
//                        itr = recordTotalCount.iterator();
//                        while (itr.hasNext()) {
//                            String updateDiv = "";
//                            JSONObject obj = new JSONObject();
//                            Finalgoalmanagement fgmt = (Finalgoalmanagement) itr.next();
//                            if (fgmt.getCreatedon() != null) {
//                                goaldate = AuthHandler.getUserDateFormatter(request, session).format(fgmt.getCreatedon());
//                            } else {
//                                goaldate = userdate.toString();
//                            }
//                            updateDiv += "<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a> goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + goaldate + "</font>";
//                            obj.put("update", getContentSpan(updateDiv));
//                            jArr.put(obj);
//                        }
//                    }
//                }
//
//                if (PermissionHandler.isSubscribed(hrms_Modules.recruitment, cmpsub) ){
//                    query = "select delflag from Recruiter where recruit.userID=? and delflag=0";
//                    recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request)});
//                    itr = recordTotalCount.iterator();
//                    if (itr.hasNext()) {
//                        String updateDiv = "";
//                        JSONObject obj = new JSONObject();
//                        updateDiv += "<p>You are selected on a interview panel.<br/>Please,click on following links for confirmation as a interviewer.</p>"
//                                +"<p><a href='#' onclick='interviewerPosition(1)'>Accept</a>&nbsp;&nbsp;<a href='#' onclick='interviewerPosition(0)'>Reject</a></p>";
//                        obj.put("update", getContentSpan(updateDiv));
//                        jArr.put(obj);
//                    }
//                }
//            } else {
//                JSONObject totalno = new JSONObject();
//                String updateDiv = "";
//                Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
//                String query = "select distinct appm.appcycle from Appraisalmanagement appm where (appm.manager.userID=? or appm.employee.userID=?) and appm.appcycle.submitenddate>=?";
//                List recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request), AuthHandler.getUserid(request),userdate});
//                Iterator itr = recordTotalCount.iterator();
//                while (itr.hasNext()) {
//                    JSONObject obj = new JSONObject();
//                    Appraisalcycle app = (Appraisalcycle) itr.next();
//                        Object[] row;
//                        String selfapp = "";
//                        updateDiv = "Appraisal Cycle: <font color='green'>" + app.getCyclename() + "</font><hr><li style='margin-left:10px;'>Deadline for submission: <font color='blue'>" + AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitenddate()) + "</font></li>";
//                        String hql = "select appm.employee.userID,appm.employeestatus,appm.reviewstatus from Appraisalmanagement appm where appm.manager.userID=? and " +
//                                "appm.appcycle.id=? group by appm.appcycle.id";
//                        Object[] object = {AuthHandler.getUserid(request), app.getId()};
//                        recordTotalCount = HibernateUtil.executeQuery(session, hql, object);
//                        if (recordTotalCount.size() > 0) {
//                            row = (Object[]) recordTotalCount.get(0);
//                            if (hrmsManager.isAppraiser(AuthHandler.getUserid(request), session, request)) {
//                                totalno = getAppraisalManagercount(session, AuthHandler.getUserid(request), app.getId(), totalno);
//                                int submitted = totalno.getInt("appraisalsubmitted");
//                                int remaIning = totalno.getInt("totalappraisal") - submitted;
//                                updateDiv += "<li style='margin-left:10px;'>No. of Appraisals Submitted: <font color='blue'>" + submitted + "</font><br>";
//                                updateDiv += "No. of Appraisals Remaining: <font color='blue'>" + remaIning + "</font></li>";
//                            }
//                        }
//                        hql = "from Appraisalmanagement appm where appm.employee.userID=? and appm.appcycle.id=? group by appm.appcycle.id";
//                        recordTotalCount = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request), app.getId()});
//                        if (!recordTotalCount.isEmpty()) {
//                            Appraisalmanagement log = (Appraisalmanagement) recordTotalCount.get(0);
//                            if ((Integer) log.getEmployeestatus() == 0) {
//                                selfapp = "Not Submitted";
//                            } else {
//                                selfapp = "Submitted";
//                            }
//                            updateDiv += "<li style='margin-left:10px;'>Self Appraisal: <font color='blue'>" + selfapp + "</font></li>";
//                        }
//                        obj.put("update", getContentSpan(updateDiv));
//                        jArr.put(obj);
//                }
//                if (!hrmsManager.checkModule("reviewappraisal", session, request)) {
//                    query = "from Appraisalmanagement where managerstatus=1 and reviewstatus!=2 and appcycle.submitenddate<? and appcycle.company.companyID=?";
//                    List rvlist = HibernateUtil.executeQuery(session, query,new Object[]{userdate,AuthHandler.getCompanyid(request)});
//                    Iterator ritr = rvlist.iterator();
//                    while (ritr.hasNext()) {
//                        Appraisalmanagement app = (Appraisalmanagement) ritr.next();
////                        app.setEmployeestatus(1);
//                        app.setReviewstatus(2);
//                        session.update(app);
//                    }
//                }
//            }
//            if (!hrmsManager.checkModule("reviewappraisal", session, request)) {
//                calculateModAvg(session, request, 1);
//            } else {
//                calculateModAvg(session, request, 0);
//            }
//            temp = jArr;
//            int ed = Math.min(temp.length(), start1 + limit1);
//            for (int i = start1; i < ed; i++) {
//                jarr.put(temp.getJSONObject(i));
//            }
//            jobj.put("data", jarr);
//            jobj.put("count", temp.length());
//        } catch (JSONException e) {
//            throw ServiceException.FAILURE("remoteApi.getUpdatesAudit:" + e.getMessage(), e);
//        } catch (ServiceException ex) {
//            throw ServiceException.FAILURE("crmManager.getUpdatesForWidgets", ex);
//        } catch (SessionExpiredException e) {
//            throw ServiceException.FAILURE(e.getMessage(), e);
//        }
//        return jobj.toString();
//    }
    
}
