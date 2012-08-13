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

import com.krawler.common.admin.*;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.*;
import org.hibernate.Session;

public class PermissionHandlercrm {

    public static JSONObject getFeatureList(Session session) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query="select featureID, featureName, displayFeatureName from ProjectFeature";
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

    public static JSONObject getRoleList(Session session) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query = "select roleid, rolename, displayrolename from Rolelist order by roleid";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            JSONArray jArr = new JSONArray();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("roleid", row[0]);
                obj.put("rolename", row[1]);
                obj.put("displayrolename", row[2]);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.getRoleList", e);
        }
        return jobj;
    }

    public static JSONObject getActivityList(Session session) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query="select feature.featureID, activityID, activityName, displayActivityName from ProjectActivity";
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
            String query="select feature.featureID, permissionCode from UserPermission up where userLogin.userID=?";
            List list = HibernateUtil.executeQuery(session, query, request.getParameter("userid"));
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

    public static JSONObject getPermissions(Session session, String userid)
            throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONObject fjobj=new JSONObject();
        JSONObject ujobj=new JSONObject();
        try {            
            String query="select pf, pa from ProjectActivity pa right outer join pa.feature pf order by activityID";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                String fName=((ProjectFeature)row[0]).getFeatureName();
                ProjectActivity activity=(ProjectActivity)row[1];
                if (!fjobj.has(fName))
                    fjobj.put(fName, new JSONObject());

                JSONObject temp=fjobj.getJSONObject(fName);
                if(activity!=null)
                    temp.put(activity.getActivityName(), (int)Math.pow(2, temp.length()));
                }

            query="select feature.featureName, permissionCode from UserPermission up where userLogin.userID=?";
            list = HibernateUtil.executeQuery(session, query, userid);
            ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                ujobj.put(row[0].toString(), row[1]);
            }
            jobj.put("Perm", fjobj);
            jobj.put("UPerm", ujobj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.getPermissions", e);
        }
        return jobj;
    }

    public static JSONObject getPermissionsValidate(Session session, String userid, HttpServletRequest request)
            throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONObject fjobj = new JSONObject();
        JSONObject ujobj = new JSONObject();
        try {
            if (!isSuperAdmin(request)) {

                String query = "select pf, pa from ProjectActivity pa right outer join pa.feature pf order by activityID";
                List list = HibernateUtil.executeQuery(session, query);
                Iterator ite = list.iterator();
                while (ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    String fName = ((ProjectFeature) row[0]).getFeatureName();
                    ProjectActivity activity = (ProjectActivity) row[1];
                    if (!fjobj.has(fName)) {
                        fjobj.put(fName, new JSONObject());
                    }

                    JSONObject temp = fjobj.getJSONObject(fName);
                    if (activity != null) {
                        temp.put(activity.getActivityName(), (int) Math.pow(2, temp.length()));
                    }
                }

                query = "select feature.featureName, permissionCode from UserPermission up where userLogin.userID=?";
                list = HibernateUtil.executeQuery(session, query, userid);
                ite = list.iterator();
                while (ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    ujobj.put(row[0].toString(), row[1]);
                }
                jobj.put("Perm", fjobj);
                jobj.put("UPerm", ujobj);
              //  jobj.put("deskeraadmin", false);
            } else {
                JSONObject temp = new JSONObject();
                jobj.put("deskeraadmin", true);
              
            }

        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.getPermissionsValidate", e);
        }
        return jobj;
    }

    public static JSONObject getRoles(Session session, String userid)
            throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONObject rjobj = new JSONObject();
        JSONObject ujobj = new JSONObject();
        try {
            String query = "select rl from Rolelist rl order by roleid";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            int inc = 0;
            while (ite.hasNext()) {
                Object row = (Object) ite.next();
                String rname = ((Rolelist) row).getRolename();
                rjobj.put(rname, (int) Math.pow(2, inc));
                inc++;
            }
            User user = (User) session.get(User.class, userid);
            int roleid = user.getRoleID();
            ujobj.put("roleid", roleid);

            jobj.put("Role", rjobj);
            jobj.put("URole", ujobj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.getRoles", e);
        }
        return jobj;
    }

    public static JSONObject getUserRoles(Session session, String userid)
            throws ServiceException {
        JSONObject jobj = new JSONObject();
        int roleid = 0;
        try {
            boolean isadmin=false;
            JSONArray jArr = new JSONArray();
            User user = (User) session.get(User.class, userid);
            roleid = user.getRoleID();
            JSONObject obj = new JSONObject();
            obj.put("roleid", roleid);

            String adminid=user.getCompany().getCreator().getUserID();
            if(user.getUserID().equals(adminid)) {
                isadmin=true;
            }
            obj.put("isadmin", isadmin);

            jArr.put(obj);

            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.getUserRoles", e);
        }
        return jobj;
    }

    public static void saveActivity(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String id=request.getParameter("activityid");
            ProjectActivity activity;
            ProjectFeature feature=null;
            if(id!=null&&id.length()>0){
                activity=(ProjectActivity)session.load(ProjectActivity.class, id);
            }else{
                activity=new ProjectActivity();
                feature=(ProjectFeature)session.load(ProjectFeature.class, request.getParameter("featureid"));
                activity.setFeature(feature);
            }
            activity.setActivityName(request.getParameter("activityname"));
            activity.setDisplayActivityName(request.getParameter("displayactivityname"));
            session.saveOrUpdate(activity);
            if(id!=null&&id.length()>0){
                updatePermissionsForActivity(session, activity, feature, true);
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
            if(id!=null&&id.length()>0){
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

    public static void saveRole(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String id = request.getParameter("roleid");
            Rolelist role;
            if (id != null && id.length() > 0) {
                role = (Rolelist) session.load(Rolelist.class, id);
            } else {
                role = new Rolelist();
            }
            role.setRolename(request.getParameter("rolename"));
            role.setDisplayrolename(request.getParameter("displayrolename"));
            session.saveOrUpdate(role);
        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.saveRole", e);
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

    public static void deleteRole(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String id = request.getParameter("roleid");
            Rolelist role;
            role = (Rolelist) session.load(Rolelist.class, id);
            session.delete(role);
        } catch (Exception e) {
            throw ServiceException.FAILURE("PermissionHandler.deleteRole", e);
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

    public static boolean isSuperAdmin(HttpServletRequest request) throws ServiceException {
        boolean admin = false;
        try {
            String userid = AuthHandler.getUserid(request);
            String companyid = AuthHandler.getCompanyid(request);
            if (userid.equals("ff808081227d4f5801227d535ebb0009") && companyid.equals("ff808081227d4f5801227d535eba0008")) {
                admin = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw ServiceException.FAILURE("PermissionHandler.isSuperAdmin", e);
        }
        return admin;
    }

    public static boolean setDefaultPermissions(Session session, UserLogin ulog,int flag)
            throws ServiceException {
        boolean ret = false;
        try {
            //UserLogin ulog = (UserLogin) session.get(UserLogin.class, userid);
            String query = "from ProjectFeature ";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                ProjectFeature projectFeature = (ProjectFeature) ite.next();
                int actsize = projectFeature.getActivities().size();
                int permcode=0;
                if(flag==1){     // admin
                    permcode = (int) Math.pow(2, actsize) -1 ;
                }
                if(flag==2){     // manager
                    permcode = (int) Math.pow(2, actsize);
                }
                if(flag==4){     // employee
                    permcode = 1;
                }
                UserLogin ulog2=ulog;
                UserPermission uperm = new UserPermission();
                uperm.setFeature(projectFeature);
                uperm.setPermissionCode(permcode);
             //   uperm.setUserLogin(ulog2);
                session.save(uperm);
            }

        } catch (Exception e) {
            ret = false;
            throw ServiceException.FAILURE("PermissionHandler.setDefaultPermissions", e);
        }
        return ret;
    }

}
