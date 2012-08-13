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
package com.krawler.spring.permissionHandler;

import com.krawler.common.admin.Rolelist;
import com.krawler.common.admin.UserLogin;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import javax.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class permissionHandlerController extends MultiActionController implements MessageSourceAware {

    private permissionHandlerDAO permissionHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private auditTrailDAO auditTrailDAOObj;
    private HibernateTransactionManager txnManager;
    private String successView;
    private MessageSource messageSource;

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public JSONObject getActivityJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("featureid", row[0]);
                obj.put("activityid", row[1]);
                obj.put("activityname", row[2]);
                obj.put("displayactivityname", row[3]);

                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getActivityList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            kmsg = permissionHandlerDAOObj.getActivityList();
            jobj = getActivityJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getRoleJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Rolelist rl = (Rolelist) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("roleid", rl.getRoleid());
                obj.put("rolename", rl.getRolename());
                obj.put("displayrolename", rl.getDisplayrolename());

                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getRoleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            kmsg = permissionHandlerDAOObj.getRoleList();
            jobj = getRoleJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getFeatureJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("featureid", row[0]);
                obj.put("featurename", row[1]);
                obj.put("displayfeaturename", row[2]);

                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getFeatureList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            kmsg = permissionHandlerDAOObj.getFeatureList();
            jobj = getFeatureJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getPermissions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            
            if (!permissionHandlerDAOObj.isSuperAdmin(userid, companyid)) {
                kmsg = permissionHandlerDAOObj.getActivityFeature();
                jobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("userid", userid);

                kmsg = permissionHandlerDAOObj.getUserPermission(requestParams);
                jobj = permissionHandler.getRolePermissionJson(kmsg.getEntityList(), jobj);
            } else {
                jobj.put("deskeraadmin", true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getRolePermissionJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("permission", row[1]);
                obj.put("featureid", row[2]);

                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getRolePermissions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String userid = request.getParameter("userid");
            String roleid = request.getParameter("roleid");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            requestParams.put("roleid", roleid);
            kmsg = permissionHandlerDAOObj.getUserPermission(requestParams);
            jobj = getRolePermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView setPermissions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = request.getParameter("userid");
            String currentUserId = sessionHandlerImplObj.getUserid(request);
            String[] features = request.getParameterValues("features");
            String[] permissions = request.getParameterValues("permissions");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);
            requestParams.put("roleid", request.getParameter("roleid"));

            kmsg = permissionHandlerDAOObj.setPermissions(requestParams, features, permissions);
            UserLogin userLogin = (UserLogin) kmsg.getEntityList().get(0);

//            auditTrailDAOObj.insertAuditLog(AuditAction.ADMIN_Permission,
//                    "Permissions updated for " + userLogin.getUser().getFirstName() + " " + userLogin.getUser().getLastName(),
//                    request, userid);

            if (userid.equals(currentUserId)) {
                kmsg = permissionHandlerDAOObj.getActivityFeature();
                jobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());

                requestParams = new HashMap<String, Object>();
                requestParams.put("userid", userid);

                kmsg = permissionHandlerDAOObj.getUserPermission(requestParams);
                HttpSession httpsession = request.getSession(true);
                Iterator ite2 = kmsg.getEntityList().iterator();
                while (ite2.hasNext()) {
                    Object[] roww = (Object[]) ite2.next();
                    httpsession.setAttribute(roww[0].toString(), roww[1]);
                }
                jobj = permissionHandler.getRolePermissionJson(kmsg.getEntityList(), jobj);
            }
            jobj.put("msg", messageSource.getMessage("hrms.commonlib.permissions.assigned.successfully", null, RequestContextUtils.getLocale(request)));
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveFeatureList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("featureid", request.getParameter("featureid"));
            requestParams.put("featurename", request.getParameter("featurename"));
            requestParams.put("displayfeaturename", request.getParameter("displayfeaturename"));

            kmsg = permissionHandlerDAOObj.saveFeatureList(requestParams);
            jobj = getFeatureJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveRoleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("roleid", request.getParameter("roleid"));
            requestParams.put("userid", request.getParameter("userid"));
            requestParams.put("rolename", request.getParameter("rolename"));
            requestParams.put("displayrolename", request.getParameter("displayrolename"));

            kmsg = permissionHandlerDAOObj.saveRoleList(requestParams);
            jobj = getFeatureJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj.put("msg", messageSource.getMessage("hrms.commonlib.role.saved.successfully", null, RequestContextUtils.getLocale(request)));
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveActivityList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("activityid", request.getParameter("activityid"));
            requestParams.put("activityname", request.getParameter("activityname"));
            requestParams.put("displayactivityname", request.getParameter("displayactivityname"));

            kmsg = permissionHandlerDAOObj.saveActivityList(requestParams);
            jobj = getFeatureJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteFeature(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("featureid", request.getParameter("featureid"));

            kmsg = permissionHandlerDAOObj.deleteFeature(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteRole(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String roleid = request.getParameter("roleid");
            String msg = "";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("roleid", roleid);
            
            Locale locale = RequestContextUtils.getLocale(request);
            requestParams.put("locale", locale);
            kmsg = permissionHandlerDAOObj.deleteRole(requestParams);
            msg = kmsg.getEntityList().get(0).toString();
            jobj.put("msg", msg);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("activityid", request.getParameter("activityid"));

            kmsg = permissionHandlerDAOObj.deleteActivity(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
