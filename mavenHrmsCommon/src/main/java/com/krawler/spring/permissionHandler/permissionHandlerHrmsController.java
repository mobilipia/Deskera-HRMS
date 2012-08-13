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

import com.krawler.common.admin.Role;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
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
 * @author Karthik
 */
public class permissionHandlerHrmsController extends MultiActionController implements MessageSourceAware {

    private permissionHandlerDAO permissionHandlerDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private auditTrailDAO auditTrailDAOObj;
    private HibernateTransactionManager txnManager;
    private String successView;
    private MessageSource messageSource;

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

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

    public ModelAndView getFeatureList(HttpServletRequest request,HttpServletResponse response){
		JSONObject jobj = null;
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = null;
        KwlReturnObject result = null;
		try {
            int subscrpt=Integer.parseInt(sessionHandlerImplObj.getCmpSubscription(request));
            String modules="";
//            String hql="from hrms_Modules";
//            List mlst=HibernateUtil.executeQuery(session,hql);
            result = hrmsCommonDAOObj.getHrmsmodule(RequestContextUtils.getLocale(request));
            if(StringUtil.checkResultobjList(result)){
                jobj = (JSONObject) result.getEntityList().get(0);
            }
            jarr = jobj.getJSONArray("data");
            for(int ctr=0;ctr<jarr.length();ctr++){
                JSONObject hmd = jarr.getJSONObject(ctr);
                int module=Integer.parseInt(hmd.getString("moduleid"));
                if (((int)Math.pow(2, module) & subscrpt) == (int)Math.pow(2, module)) {
                  modules=modules+String.valueOf(module)+",";
               }
            }
            if(modules.length()>0){
              modules=modules.substring(0,modules.length()-1);
            }
//            String query="select featureID, featureName, displayFeatureName from ProjectFeature where moduleid.moduleID in("+modules+") or moduleid is null";
//            List list = HibernateUtil.executeQuery(session, query);
            result = permissionHandlerDAOObj.getFeatureList(modules);
            List list = result.getEntityList();
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Object[] row = (Object[])ite.next();
                JSONObject obj=new JSONObject();
                obj.put("featureid",row[0]);
                obj.put("featurename",row[1]);
                obj.put("displayfeaturename",messageSource.getMessage("hrms.Featurelist."+row[1], null,row[2].toString(), RequestContextUtils.getLocale(request)));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
                e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView getActivityList(HttpServletRequest request, HttpServletResponse response){
		JSONObject jobj = null;
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = null;
        KwlReturnObject result = null;
		try {
            int subscrpt=Integer.parseInt(sessionHandlerImplObj.getCmpSubscription(request));
            String modules="";
//            String hql="from hrms_Modules";
//            List mlst=HibernateUtil.executeQuery(session,hql);
            result = hrmsCommonDAOObj.getHrmsmodule(RequestContextUtils.getLocale(request));
            if(StringUtil.checkResultobjList(result)){
                jobj = (JSONObject) result.getEntityList().get(0);
            }
            jarr = jobj.getJSONArray("data");
            for(int ctr=0;ctr<jarr.length();ctr++){
                JSONObject hmd = jarr.getJSONObject(ctr);
                int module=Integer.parseInt(hmd.getString("moduleid"));
                if (((int)Math.pow(2, module) & subscrpt) == (int)Math.pow(2, module)) {
                  modules=modules+String.valueOf(module)+",";
               }
            }
            if(modules.length()>0){
              modules=modules.substring(0,modules.length()-1);
            }
//            String query="select feature.featureID, activityID, activityName, displayActivityName from ProjectActivity where feature.moduleid.moduleID in("+modules+") or feature.moduleid is null";
//            List list = HibernateUtil.executeQuery(session, query);
            result = permissionHandlerDAOObj.getActivityFeature(modules);
            List list = result.getEntityList();
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Object[] row = (Object[])ite.next();
                JSONObject obj=new JSONObject();
                obj.put("featureid",row[0]);
                obj.put("activityid",row[1]);
                obj.put("activityname",row[2]);
                obj.put("displayactivityname",messageSource.getMessage("hrms.activityList."+row[2], null,row[3].toString(), RequestContextUtils.getLocale(request)));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
                e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    public ModelAndView getPermissionCode(HttpServletRequest request, HttpServletResponse response){
		JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
		try {
//            String query="select up.feature.featureID, up.permissionCode from UserPermission up inner join up.role r where r.ID=?";
//            List list = HibernateUtil.executeQuery(session, query, request.getParameter("roleid"));
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("roleid", request.getParameter("roleid"));
            result = permissionHandlerDAOObj.getPermissionCode(requestParams);
            List list = result.getEntityList();
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Object[] row = (Object[])ite.next();
                JSONObject obj=new JSONObject();
                obj.put("featureid",row[0]);
                obj.put("permission",row[1]);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
                e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
	}

    public ModelAndView getRoles(HttpServletRequest request,HttpServletResponse response){
		KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
		try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            result= permissionHandlerDAOObj.getRoles(requestParams);
            List list = result.getEntityList();
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Role role = (Role)ite.next();
                JSONObject obj=new JSONObject();
                obj.put("roleid",role.getID());
                obj.put("rolename",messageSource.getMessage("hrms.Roles."+StringUtil.replaceNametoLocalkey(role.getName()), null,role.getName(), RequestContextUtils.getLocale(request)));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj1.put("data", jobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView setPermissions(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String id = request.getParameter("roleid");
            if(id.equals("1") || id.equals("2") || id.equals("3")) {
                jobj.put("success", false);
                jobj.put("msg", messageSource.getMessage("hrms.common.Youcantchangepermissionsfortheselectedrole", null,"You can't change permissions for the selected role.", RequestContextUtils.getLocale(request)));
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
                txnManager.rollback(status);
            } else {
                String[] features = request.getParameterValues("features");
                String[] permissions = request.getParameterValues("permissions");
                HashMap<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("roleid", id);
                KwlReturnObject result = permissionHandlerDAOObj.deleteUserPermission(requestParams);
                for (int i = 0; i < features.length; i++) {
                    if (permissions[i].equals("0")) {
                        continue;
                    }
                    requestParams.clear();
                    requestParams.put("role", id);
                    requestParams.put("feature", features[i]);
                    requestParams.put("permissioncode", permissions[i]);
                    permissionHandlerDAOObj.addUserPermission(requestParams);
                }
                jobj.put("success", true);
                jobj.put("msg", messageSource.getMessage("hrms.common.Permissionshavebeenassignedsuccessfully", null,"Permissions have been assigned successfully.", RequestContextUtils.getLocale(request)));
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
                txnManager.commit(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jobj.put("success", false);
            jobj.put("msg", messageSource.getMessage("hrms.common.Unexpectederroroccurredonserver", null,"Unexpected error occurred on server.", RequestContextUtils.getLocale(request)));
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
