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
package com.krawler.esp.database;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.esp.handlers.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import com.krawler.utils.json.base.*;
public class DBCon {
    
    public static JSONObject AuthUser(String uname, String passwd, String subdomain) throws ServiceException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jobj = AuthHandler.verifyLogin(session, uname, passwd, subdomain);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jobj;
    }

    public static JSONObject AuthUser(String uname, String subdomain) throws ServiceException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = AuthHandler.verifyLogin(session, uname, subdomain);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jobj;
    }
    
    public static JSONArray getPreferences(HttpServletRequest request) throws ServiceException {
        JSONArray jArr = new JSONArray();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jArr = AuthHandler.getPreferences(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jArr;
    }

    public static JSONObject getValidUserOptions(String userid) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jObj = ProfileHandler.getValidUserOptions(session, userid);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getPermissions(String userid) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = PermissionHandler.getPermissions(session, userid);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static String getUserFullName(String userid) throws ServiceException {
        String str="";
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            str = ProfileHandler.getUserFullName(session, userid);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static JSONObject getFeatureList(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = PermissionHandler.getFeatureList(session,request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getActivityList(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = PermissionHandler.getActivityList(session,request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static void saveFeature(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            PermissionHandler.saveFeature(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static void deleteFeature(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            PermissionHandler.deleteFeature(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static void saveActivity(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            PermissionHandler.saveActivity(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static void deleteActivity(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            PermissionHandler.deleteActivity(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static JSONObject getAllUserDetails(HttpServletRequest request,int start,int limit) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getAllUserDetails(session,request,start,limit);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

     public static JSONObject getparticularUserDetails(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getparticularUserDetails(session,request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject saveUser(HttpServletRequest request, HashMap hm) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        JSONObject obj=new JSONObject();
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
           obj= ProfileHandler.saveUser(session,request, hm);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return obj;
    }

    public static void deleteUser(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.deleteUser(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static void setPassword(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.setPassword(session, request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static void setPermissions(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.setPermissions(session, request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static JSONObject getPermissionCode(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = PermissionHandler.getPermissionCode(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static void updateLastLogin(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.updateLastLogin(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static JSONObject getAllCurrencies(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getAllCurrencies(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getAllTimeZones(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getAllTimeZones(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getCompanyInformation(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getCompanyInformation(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getCompanyHolidays(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getCompanyHolidays(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getAllCountries(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getAllCountries(session, request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static void updateCompany(HttpServletRequest request, HashMap hm) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.updateCompany(session,request, hm);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static String getCompanyid(String domain){
        String companyID=null;
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            companyID = AuthHandler.getCompanyid(session, domain);
        } catch (Exception ex) {
        } finally {
                HibernateUtil.closeSession(session);
        }
        return companyID;
    }

        public static JSONObject getManagers(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getManagers(session,request);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getAllCompanyDetails(HttpServletRequest request,Integer start,Integer limit) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getAllCompanyDetails(session,request,start,limit);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }


   public static JSONObject getUserofCompany(HttpServletRequest request,Integer start,Integer limit) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getUserofCompany(session,request,start,limit);
        } catch (ServiceException ex) {
                throw ex;
        } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

   public static void deletecompany(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.deletecompany(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

     public static JSONObject changepassword(String platformURL,HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jObj = ProfileHandler.changePassword(platformURL,session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static void insertAuditLog(String actionid, String details, String ipAddress, String userid) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            //@@ProfileHandler.insertAuditLog(session, actionid, details, ipAddress, userid);
            tx.commit();
//        } catch (ServiceException ex) {
//                if (tx!=null) tx.rollback();
//                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static void insertAuditLog(String actionid, String details, HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            //@@ProfileHandler.insertAuditLog(session, actionid, details, request);
            tx.commit();
//        } catch (ServiceException ex) {
//                if (tx!=null) tx.rollback();
//                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

    public static JSONObject getAuditTrail(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jObj = ProfileHandler.getAuditTrail(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static JSONObject getAuditGroups(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jObj = ProfileHandler.getAuditGroups(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static String getFullName(HttpServletRequest request) throws ServiceException {
        String name="";
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            name = AuthHandler.getFullName(session,AuthHandler.getUserid(request));
            tx.commit();
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return name;
    }

     public static JSONObject getAllDateFormats(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jObj = ProfileHandler.getAllDateFormats(session, request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

       public static JSONObject getRoles(HttpServletRequest request) throws ServiceException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jObj = PermissionHandler.getRoles(session, request);
            tx.commit();
        } catch (ServiceException ex) {
        		ex.printStackTrace();
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
        	ex.printStackTrace();
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }

    public static boolean saveRole(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        boolean success = false;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            boolean roleExists = PermissionHandler.isRoleExistsInCompany(session, request.getParameter("rolename"), AuthHandler.getCompanyid(request));
            if(!roleExists){
            	PermissionHandler.saveRole(session,request);
            	success = true;
            }
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return success;
    }

    public static void deleteRole(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            PermissionHandler.deleteRole(session,request);
            tx.commit();
        } catch (ServiceException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (JDBCException jex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE("Can't delete role. Some users have this role assigned", jex);
        } catch (Exception ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }

     public static Hashtable getfileinfo(String name) throws ServiceException {
		Hashtable ht = new Hashtable();
        Session session = null;
		try {
			session = HibernateUtil.getCurrentSession();
			ht = FileHandler.getfileinfo(session, name);
        } catch (ServiceException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
		return ht;
	}

 public static JSONObject getAllUserDetails_profile(HttpServletRequest request,int start,int limit) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getAllUserDetails_profile(session,request,start,limit);
        } catch (JSONException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }
   public static void setEmpIdFormat(HttpServletRequest request) throws ServiceException,SessionExpiredException,NullPointerException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.setEmpIdFormat(session,request);
            tx.commit();
        } catch (NullPointerException ex) {
                if (tx!=null) tx.rollback();
                throw ex;
        } catch (SessionExpiredException ex) {
                if (tx!=null) tx.rollback();
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
                HibernateUtil.closeSession(session);
        }
    }
   public static JSONObject getexEmployees(HttpServletRequest request,int start,int limit) throws ServiceException, JSONException, SessionExpiredException, SQLException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getexEmployees(session,request,start,limit);
        } catch (JSONException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }
   public static JSONObject getEmpHistory(HttpServletRequest request,int start,int limit) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jObj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jObj = ProfileHandler.getEmpHistory(session,request,start,limit);
        } catch (JSONException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        finally {
                HibernateUtil.closeSession(session);
        }
        return jObj;
    }
}
