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

import com.krawler.common.admin.ProjectActivity;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.RoleUserMapping;
import com.krawler.common.admin.Rolelist;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.UserPermission;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Karthik
 */
public class permissionHandlerDAOImpl implements permissionHandlerDAO, MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject getFeatureList() throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String Hql = "select featureID, featureName, displayFeatureName from ProjectFeature";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getFeatureList", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getFeatureList(String modules) {
        int dl = 0;
        List ll = null;
        try {
            String Hql="select featureID, featureName, displayFeatureName from ProjectFeature where moduleid.moduleID in("+modules+") or moduleid is null";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql);
            dl = ll.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getRoleList() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "from Rolelist order by roleid";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getRoleList", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getRoleofUser(String userid) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select roleId.roleid, roleId.displayrolename from RoleUserMapping where userId.userID=?";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, userid);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getRoleofUser", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }
    
    public KwlReturnObject getActivityList() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select feature.featureID, activityID, activityName, displayActivityName from ProjectActivity";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getActivityList", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject saveFeatureList(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String id = requestParams.containsKey("featureid") && requestParams.get("featureid") != null ? requestParams.get("featureid").toString() : "";
            ProjectFeature feature;
            if (!StringUtil.isNullOrEmpty(id)) {
                feature = (ProjectFeature) hibernateTemplate.load(ProjectFeature.class, id);
            } else {
                feature = new ProjectFeature();
            }
            if (requestParams.containsKey("featurename") && requestParams.get("featurename") != null) {
                feature.setFeatureName(requestParams.get("featurename").toString());
            }
            if (requestParams.containsKey("displayfeaturename") && requestParams.get("displayfeaturename") != null) {
                feature.setDisplayFeatureName(requestParams.get("displayfeaturename").toString());
            }
            hibernateTemplate.saveOrUpdate(feature);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveFeatureList", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject saveRoleList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String id = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";
            String userid = requestParams.containsKey("userid") && requestParams.get("userid") != null ? requestParams.get("userid").toString() : "";
            Rolelist role;
            if (!StringUtil.isNullOrEmpty(id)) {
                role = (Rolelist) hibernateTemplate.load(Rolelist.class, id);
            } else {
                role = new Rolelist();
            }
            if (requestParams.containsKey("rolename") && requestParams.get("rolename") != null) {
                role.setRolename(requestParams.get("rolename").toString());
            }
            if (requestParams.containsKey("displayrolename") && requestParams.get("displayrolename") != null) {
                role.setDisplayrolename(requestParams.get("displayrolename").toString());
            }
            hibernateTemplate.saveOrUpdate(role);

            RoleUserMapping rum = new RoleUserMapping();
            rum.setRoleId(role);
            if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                rum.setUserId((User)hibernateTemplate.load(User.class, requestParams.get("userid").toString()));
            }
            hibernateTemplate.saveOrUpdate(rum);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveRoleList", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject saveActivityList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String id = requestParams.containsKey("activityid") && requestParams.get("activityid") != null ? requestParams.get("activityid").toString() : "";
            ProjectActivity activity;
            ProjectFeature feature = null;
            if (!StringUtil.isNullOrEmpty(id)) {
                activity = (ProjectActivity) hibernateTemplate.load(ProjectActivity.class, id);
            } else {
                activity = new ProjectActivity();
                feature = (ProjectFeature) hibernateTemplate.load(ProjectFeature.class, requestParams.get("featureid").toString());
                activity.setFeature(feature);
            }
            if (requestParams.containsKey("activityname") && requestParams.get("activityname") != null) {
                activity.setActivityName(requestParams.get("activityname").toString());
            }
            if (requestParams.containsKey("displayactivityname") && requestParams.get("displayactivityname") != null) {
                activity.setDisplayActivityName(requestParams.get("displayactivityname").toString());
            }
            hibernateTemplate.saveOrUpdate(activity);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveActivityList", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteFeature(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String id = requestParams.containsKey("featureid") && requestParams.get("featureid") != null ? requestParams.get("featureid").toString() : "";
            ProjectFeature feature;
            if (!StringUtil.isNullOrEmpty(id)) {
                feature = (ProjectFeature) hibernateTemplate.load(ProjectFeature.class, id);
                hibernateTemplate.delete(feature);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteFeature", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteRole(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        String Hql = "";
        Rolelist role = null;
        String msg = "";
        try {
            String id = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";
            if (!StringUtil.isNullOrEmpty(id)) {
            	Locale locale = null;
                if(requestParams.get("locale")!=null){
                	locale = (Locale) requestParams.get("locale");
                }
                
                role = (Rolelist) hibernateTemplate.load(Rolelist.class, id);
                Hql = "from RoleUserMapping where roleId=?";
                ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, role);
                if (ll.size() > 0) {
                    msg = messageSource.getMessage("hrms.commonlib.role.cannot.deleted", null, locale);
                } else {
                    hibernateTemplate.delete(role);
                    msg = messageSource.getMessage("hrms.commonlib.role.deleted.successfully", null, locale);
                }
            }
            ll = new ArrayList();
            ll.add(msg);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteRole", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteActivity(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String id = requestParams.containsKey("activityid") && requestParams.get("activityid") != null ? requestParams.get("activityid").toString() : "";
            ProjectActivity activity;
            if (StringUtil.isNullOrEmpty(id)) {
                activity = (ProjectActivity) hibernateTemplate.load(ProjectActivity.class, id);
                hibernateTemplate.delete(activity);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteActivity", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getActivityFeature() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select pf, pa from ProjectActivity pa right outer join pa.feature pf order by activityID";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getActivityFeature", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getActivityFeature(String modules){
        List ll = null;
        int dl = 0;
        try {
            String Hql="select feature.featureID, activityID, activityName, displayActivityName from ProjectActivity where feature.moduleid.moduleID in("+modules+") or feature.moduleid is null";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql);
            dl = ll.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getUserPermission(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        Object[] params = null;
        try {
            String userid = requestParams.containsKey("userid") && requestParams.get("userid") != null ? requestParams.get("userid").toString() : "";
            String roleid = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";
            String Hql = " select feature.featureName, permissionCode, feature.featureID from UserPermission up where roleId.userId.userID=? ";
            params = new Object[]{userid};

            if(!StringUtil.isNullOrEmpty(roleid)) {
                 Hql += " and roleId.roleId.roleid=? ";
                 params = new Object[]{userid, roleid};
            }
            
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, params);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getUserPermission", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public boolean isSuperAdmin(String userid, String companyid) throws ServiceException {
        boolean admin = false;
        try {
            // Hardcoded id of admin user and admin company.
            if (userid.equals("ff808081227d4f5801227d535ebb0009") && companyid.equals("ff808081227d4f5801227d535eba0008")) {
                admin = true;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.isSuperAdmin", e);
        }
        return admin;
    }

    public KwlReturnObject setPermissions(HashMap<String, Object> requestParams, String[] features, String[] permissions) throws ServiceException {
        List ll = null;
        int dl = 0;
        String rid = "";
        try {
            String id = requestParams.containsKey("userid") && requestParams.get("userid") != null ? requestParams.get("userid").toString() : "";
            String roleId = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";

            String Hql = "select id from RoleUserMapping where userId.userID=? ";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, id);

            rid = ll.get(0).toString();
            RoleUserMapping rum = (RoleUserMapping) hibernateTemplate.load(RoleUserMapping.class, rid);
            rum.setRoleId((Rolelist)hibernateTemplate.load(Rolelist.class, roleId));
            hibernateTemplate.save(rum);


            // @@ why do we save role in Users - Ajay
//            User user = (User) hibernateTemplate.load(User.class, id);
//            user.setRoleID(roleId);
//            hibernateTemplate.save(user);
            
            Hql = "delete from UserPermission where roleId=?";
            HibernateUtil.executeUpdate(hibernateTemplate, Hql, new Object[]{rum});

            for (int i = 0; i < features.length; i++) {
                if (permissions[i].equals("0")) {
                    continue;
                }
                UserPermission permission = new UserPermission();
//                permission.setRoleId(rum);
                permission.setFeature((ProjectFeature) hibernateTemplate.load(ProjectFeature.class, features[i]));
                permission.setPermissionCode(Long.parseLong(permissions[i]));
                hibernateTemplate.save(permission);
            }
            UserLogin userLogin = (UserLogin)hibernateTemplate.load(UserLogin.class, id);
            ll = new ArrayList();
            ll.add(userLogin);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.setPermissions", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteUserPermission(HashMap<String,Object> requestParams){
        Boolean success = false;
		try {
            String roleid = requestParams.get("roleid").toString();
            String query="delete from UserPermission where role.ID=?";
            int num = HibernateUtil.executeUpdate(hibernateTemplate, query,new Object[]{roleid});
            if(num>0)
                success = true;
        }catch(Exception e){
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", null, 0);
        }
     }

    public KwlReturnObject getProjectActivity(HashMap<String,Object> requestParams){
        JSONObject fjobj=new JSONObject();
        Boolean success = false;
        List list = null;
		try {
            String query="select pf, pa from ProjectActivity pa right outer join pa.feature pf order by activityID";
            list = HibernateUtil.executeQuery(hibernateTemplate, query);
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
            list.clear();
            list.add(fjobj);
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
     }

    public KwlReturnObject getPermission(HashMap<String,Object> requestParams){
        JSONObject ujobj=new JSONObject();
        Boolean success = false;
        String userid = requestParams.get("userid").toString();
        List list = null;
		try {
            String query="select up.feature.featureName, up.permissionCode from UserPermission up inner join up.role r, Useraccount ua where ua.role.ID=r.ID and ua.userID=?";
            list = HibernateUtil.executeQuery(hibernateTemplate, query, userid);
            Iterator ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                ujobj.put(row[0].toString(), row[1]);
            }
            
//            jobj.put("Perm", fjobj);
//            jobj.put("UPerm", ujobj);
            success = true;
            list.clear();
            list.add(ujobj);
        } catch (Exception e) {
                e.printStackTrace();
                success = false;
        }
        return new KwlReturnObject(success, "", "", list, list.size());
	}

    public KwlReturnObject getPermissionCode(HashMap<String,Object> requestParams){
		Boolean success = false;
        List list = null;
		try {
            String roleid = requestParams.get("roleid").toString();
            String query="select up.feature.featureID, up.permissionCode from UserPermission up inner join up.role r where r.ID=?";
            list = HibernateUtil.executeQuery(hibernateTemplate, query, roleid);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
	}
    
    public KwlReturnObject getRoles(HashMap<String,Object> requestParams) {
		Boolean success = false;
        List list = null;
		try {
            String companyid = requestParams.get("companyid").toString();
            String query="from Role where company is null or company.companyID=?";
            list = HibernateUtil.executeQuery(hibernateTemplate, query,companyid);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }
   
    public KwlReturnObject addUserPermission(HashMap<String, Object> requestParams) {
        List <UserPermission> list = new ArrayList<UserPermission>();
        boolean success = false;
        try {
                
               UserPermission up = new UserPermission();
               up.setRole((Role)hibernateTemplate.get(Role.class, requestParams.get("role").toString()));
               up.setFeature((ProjectFeature)hibernateTemplate.get(ProjectFeature.class, requestParams.get("feature").toString()));
               up.setPermissionCode(Long.valueOf(requestParams.get("permissioncode").toString()));
               hibernateTemplate.save(up);
               list.add(up);
               success = true;
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "UserPermission added successfully.", "-1", list, list.size());
        }
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
