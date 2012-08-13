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
package com.krawler.esp.handlers;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.ProjectActivity;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.UserPermission;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.LocaleUtil;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.*;
import org.hibernate.Session;

public class PermissionHandler {

public static JSONObject getFeatureList(Session session,HttpServletRequest request) throws ServiceException {
		JSONObject jobj = new JSONObject();
		try {
            int subscrpt=Integer.parseInt(AuthHandler.getCmpSubscription(request));
            String modules="";
            String hql="from hrms_Modules";
            List mlst=HibernateUtil.executeQuery(session,hql);
            Iterator mite=mlst.iterator();
            while(mite.hasNext()){
                hrms_Modules hmd=(hrms_Modules)mite.next();
                int module=Integer.parseInt(hmd.getModuleID());
                if (((int)Math.pow(2, module) & subscrpt) == (int)Math.pow(2, module)) {
                  modules=modules+hmd.getModuleID()+",";
               }
            }
            if(modules.length()>0){
              modules=modules.substring(0,modules.length()-1);
            }
            String query="select featureID, featureName, displayFeatureName from ProjectFeature where moduleid.moduleID in("+modules+") or moduleid is null";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Object[] row = (Object[])ite.next();
                JSONObject obj=new JSONObject();
                obj.put("featureid",row[0]);
                obj.put("featurename",row[1]);
                obj.put("displayfeaturename",row[2]);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.getFeatureList", e);
        }
        return jobj;
    }

    public static JSONObject getActivityList(Session session,HttpServletRequest request) throws ServiceException {
		JSONObject jobj = new JSONObject();
		try {
             int subscrpt=Integer.parseInt(AuthHandler.getCmpSubscription(request));
            String modules="";
            String hql="from hrms_Modules";
            List mlst=HibernateUtil.executeQuery(session,hql);
            Iterator mite=mlst.iterator();
            while(mite.hasNext()){
                hrms_Modules hmd=(hrms_Modules)mite.next();
                int module=Integer.parseInt(hmd.getModuleID());
                if (((int)Math.pow(2, module) & subscrpt) == (int)Math.pow(2, module)) {
                  modules=modules+hmd.getModuleID()+",";
               }
            }
            if(modules.length()>0){
              modules=modules.substring(0,modules.length()-1);
            }
            String query="select feature.featureID, activityID, activityName, displayActivityName from ProjectActivity where feature.moduleid.moduleID in("+modules+") or feature.moduleid is null";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Object[] row = (Object[])ite.next();
                JSONObject obj=new JSONObject();
                obj.put("featureid",row[0]);
                obj.put("activityid",row[1]);
                obj.put("activityname",row[2]);
                obj.put("displayactivityname",row[3]);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.getActivityList", e);
        }
        return jobj;
    }
   
    
    public static JSONObject getPermissionCode(Session session, HttpServletRequest request)
			throws ServiceException {
		JSONObject jobj = new JSONObject();
		try {
            String query="select up.feature.featureID, up.permissionCode from UserPermission up inner join up.role r where r.ID=?";
            List list = HibernateUtil.executeQuery(session, query, request.getParameter("roleid"));
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
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.getPermissionCode", e);
        }
        return jobj;
	}


    public static void saveActivity(Session session, HttpServletRequest request) throws ServiceException {
		try {
            String id=request.getParameter("activityid");
            ProjectActivity activity;

            if(StringUtil.isNullOrEmpty(id)==false){
                activity=(ProjectActivity)session.load(ProjectActivity.class, id);
            }else{
                activity=new ProjectActivity();
                ProjectFeature feature=(ProjectFeature)session.load(ProjectFeature.class, request.getParameter("featureid"));
                activity.setFeature(feature);
            }
            activity.setActivityName(request.getParameter("activityname"));
            activity.setDisplayActivityName(request.getParameter("displayactivityname"));
            session.saveOrUpdate(activity);
            if(StringUtil.isNullOrEmpty(id)==false){
                updatePermissionsForActivity(session, activity, activity.getFeature(), true);
            }
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.saveActivity", e);
        }
    }

    private static void updatePermissionsForActivity(Session session, ProjectActivity activity, ProjectFeature feature, boolean insert) throws ServiceException{
		try {
            int pos=0;
            String query="select activityID from ProjectActivity where feature.featureID=?  order by activityID";
            List l=HibernateUtil.executeQuery(session, query,feature.getFeatureID());
            Iterator itr = l.iterator();
            while(itr.hasNext()){
                String row=(String)itr.next();
                if(activity.getActivityID().equals(row)) break;
                pos++;
            }
            query="from UserPermission where feature.featureID=?";
            List<UserPermission> list=HibernateUtil.executeQuery(session, query,feature.getFeatureID());
            itr = list.iterator();
            while(itr.hasNext()){
                UserPermission permission= (UserPermission)itr.next();
                long code=permission.getPermissionCode();
                if(insert) code=((code&~pos)<<1)+(code&pos);
                else code=((code&~(2*pos+1))>>>1)+(code&pos);

                if(code==0)
                    session.delete(permission);
                else{
                    permission.setPermissionCode(code);
                    session.save(permission);
                }
            }
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.updatePermissionsForActivity", e);
        }
    }

    public static void saveFeature(Session session, HttpServletRequest request) throws ServiceException {
		try {
            String id=request.getParameter("featureid");
            ProjectFeature feature;
            if(StringUtil.isNullOrEmpty(id)==false){
                feature=(ProjectFeature)session.load(ProjectFeature.class, id);
            }else{
                feature=new ProjectFeature();
            }
            feature.setFeatureName(request.getParameter("featurename"));
            feature.setDisplayFeatureName(request.getParameter("displayfeaturename"));
            session.saveOrUpdate(feature);
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.saveFeature", e);
        }
    }

    public static void deleteFeature(Session session, HttpServletRequest request) throws ServiceException {
		try {
            String id=request.getParameter("featureid");
            ProjectFeature feature;
            feature=(ProjectFeature)session.load(ProjectFeature.class, id);
            session.delete(feature);
        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.deleteFeature", e);
        }
    }

    public static void deleteActivity(Session session, HttpServletRequest request) throws ServiceException {
		try {
            String id=request.getParameter("activityid");
            ProjectActivity activity;
            activity=(ProjectActivity)session.load(ProjectActivity.class, id);
            updatePermissionsForActivity(session, activity, activity.getFeature(), false);
            session.delete(activity);
        } catch (Exception e) {
            e.printStackTrace();
                throw ServiceException.FAILURE("PermissionHandler.deleteActivity", e);
        }
    }
  
     

      public static void saveRole(Session session, HttpServletRequest request) throws ServiceException {
		try {
            String id=request.getParameter("roleid");
            Role role;
            if(StringUtil.isNullOrEmpty(id)){
                role=new Role();
            }else{
                role=(Role)session.load(Role.class, id);
            }
            role.setCompany((Company)session.load(Company.class, AuthHandler.getCompanyid(request)));
            role.setName(request.getParameter("rolename"));
            session.saveOrUpdate(role);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
      
    
    public static boolean isRoleExistsInCompany(Session session, String name, String companyid) throws ServiceException {
          Boolean isExists = false;
          try {
              String query = "from Role where name=? and company.companyID=?";
              List<Role> list = HibernateUtil.executeQuery(session, query, new Object[]{name, companyid});
              if (list!=null && !list.isEmpty()) {
                  isExists = true;
              }else{
            	  isExists = false;
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
          return isExists;
      }

    public static void deleteRole(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String id = request.getParameter("roleid");
            if (id.equals(Role.SUPER_ADMIN) || id.equals(Role.COMPANY_ADMIN) || id.equals(Role.COMPANY_USER) || id.equals(Role.COMPANY_MANAGER)) {
                throw new Exception(MessageSourceProxy.getMessage("hrms.jsp.msg.cannotdeletepredefinedrole" ,null,"Can not delete Predefined Roles", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }
            if (checkUserExistanceInRole(session, id)) {
                throw new Exception(MessageSourceProxy.getMessage("hrms.jsp.msg.usersexistsforthisrolepleaseassignanotherroletotheseusers" ,null,"Users exists for this role, please assign another role to these users.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }
            Role role = (Role) session.load(Role.class, id);
            session.delete(role);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static boolean checkUserExistanceInRole(Session session, String roleid) throws ServiceException {
        Boolean isExists = false;
        try {
            String query = "from Useraccount where role.ID= ?";
            List list = HibernateUtil.executeQuery(session, query, roleid);
            Iterator ite = list.iterator();
            if (ite.hasNext()) {
                isExists = true;
            }
        } catch (Exception e) {
            isExists=true;//so that role does not get Deleted
            e.printStackTrace();
        }finally{
             return isExists;
        }
    }

    public static JSONObject getRoles(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query="from Role where company is null or company.companyID=?";
            List list = HibernateUtil.executeQuery(session, query,AuthHandler.getCompanyid(request));
            Iterator ite = list.iterator();
            JSONArray jArr=new JSONArray();
            while(ite.hasNext() ) {
                Role role = (Role)ite.next();
                JSONObject obj=new JSONObject();
                obj.put("roleid",role.getID());
                String name="";
                if(role!=null&&role.getCompany()!=null){
                	name = role.getName();
                }else{
                	name = MessageSourceProxy.getMessage("hrms.common.role."+role.getID() ,null,role.getName(), LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0));
                }
                obj.put("rolename",name);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
        	e.printStackTrace();
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public static boolean isPermitted(JSONObject perms, String featureName, String activityName)throws JSONException {
        int perm=perms.getJSONObject("Perm").getJSONObject(featureName).optInt(activityName);
        int uperm=perms.getJSONObject("UPerm").optInt(featureName);
        if((perm & uperm)==perm)
            return true;
        return false;
    }
   
     public static boolean isSubscribed(int module, String cmpsubscription)throws JSONException {
         int subscrpt = Integer.parseInt(cmpsubscription);
         if (((int)Math.pow(2, module) & subscrpt) == (int)Math.pow(2, module)) {
             return true;
         } else {
             return false;
         }
    }

     public static boolean isEmployee(Session session,HttpServletRequest request) throws ServiceException {
        boolean emp = false;
        try {
            String userid = AuthHandler.getUserid(request);
            String companyid = AuthHandler.getCompanyid(request);
            Useraccount u=(Useraccount) session.load(Useraccount.class,userid);
             if(StringUtil.equal( u.getRole().getID(),Role.COMPANY_USER)) {
//                 // request.getSession().getAttribute(keyName);
//                 String jsondata = (String) request.getSession().getAttribute("permission");
//                 JSONArray jarr = new JSONArray("[" + jsondata + "]");
//                 JSONObject jobj=jarr.getJSONObject(0);
//                 //JSONObject jobj1=jarr.getJSONObject(1);
//                if(jobj.getJSONObject("UPerm").optInt("user",-1)>0)
//                {
//                    emp=true;
//                }
                 emp=true;
             }

     } catch (Exception e) {
            e.printStackTrace();
            throw ServiceException.FAILURE("PermissionHandler.isEmployee", e);
        }
        return emp;
    }

     public static boolean isManager(Session session,HttpServletRequest request) throws ServiceException {
        boolean man = false;
        try {
            String userid = AuthHandler.getUserid(request);
            String companyid = AuthHandler.getCompanyid(request);
            Useraccount u=(Useraccount) session.load(Useraccount.class,userid);
             if(StringUtil.equal( u.getRole().getID(),Role.COMPANY_MANAGER)) {
                 man=true;
             }
        } catch (Exception e) {
            e.printStackTrace();
            throw ServiceException.FAILURE("PermissionHandler.isManager", e);
        }
        return man;
    }

     public static JSONObject getPermissions(Session session, String userid)
			throws ServiceException {
		JSONObject jobj = new JSONObject();
        JSONObject fjobj=new JSONObject();
        JSONObject ujobj=new JSONObject();
        JSONObject cmpsub=new JSONObject();
        KwlReturnObject result = null;
		try {
            String query="select pf, pa from ProjectActivity pa right outer join pa.feature pf order by activityID";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                String fName=((ProjectFeature)row[0]).getFeatureName();
                ProjectActivity activity=(ProjectActivity)row[1];
                if(!fjobj.has(fName))
                    fjobj.put(fName, new JSONObject());

                JSONObject temp=fjobj.getJSONObject(fName);
                if(activity!=null)
                    temp.put(activity.getActivityName(), (int)Math.pow(2, temp.length()));
            }

            query="select up.feature.featureName, up.permissionCode from UserPermission up inner join up.role r, User user where user.role.ID=r.ID and user.userID=?";
            list = HibernateUtil.executeQuery(session, query, userid);
            ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                ujobj.put(row[0].toString(), row[1]);
            }
            cmpsub=ProfileHandler.gethrmsModules(session);

            jobj.put("Perm", fjobj);
            jobj.put("UPerm", ujobj);
            jobj.put("hrms_modules",cmpsub);

        } catch (Exception e) {
                throw ServiceException.FAILURE("PermissionHandler.getPermissions", e);
        }
        return jobj;
	}
}
