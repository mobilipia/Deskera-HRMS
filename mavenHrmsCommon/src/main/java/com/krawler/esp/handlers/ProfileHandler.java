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

import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.AuditGroup;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyHoliday;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.Country;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.UserPermission;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.LocaleUtil;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.esp.servlets.ProfileImageServlet;
import com.krawler.esp.web.resource.Links;
import com.krawler.hrms.performance.Assignmanager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.mail.MessagingException;
import org.apache.commons.fileupload.FileItem;
import com.krawler.utils.json.base.*;
import org.hibernate.Session;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.performance.Assignreviewer;
import java.util.UUID;
import masterDB.Payhistory;
import org.hibernate.HibernateException;
import com.krawler.esp.Search.SearchBean;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.springframework.web.servlet.support.RequestContextUtils;
import javax.servlet.http.HttpServletRequest;

public class ProfileHandler {

    public static String getUserFullName(Session session, String userid) throws ServiceException {
        String name = null;
        try {
            String SELECT_USER_INFO = "select u.firstName, u.lastName from User as u " +
                    "where u.userID = ?";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, userid);
            Iterator ite = list.iterator();
            if (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                name = (StringUtil.isNullOrEmpty((String) row[0]) ? "" : row[0]) + " " + (StringUtil.isNullOrEmpty((String) row[1]) ? "" : row[1]);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getUserFullName", e);
        }

        return name;
    }

    public static JSONObject getAllUserDetails(Session session, HttpServletRequest request, int start, int limit) throws ServiceException {
        JSONObject jobj = new JSONObject();
        int count;
        try {
            String companyid = AuthHandler.getCompanyid(request);
//			String SELECT_USER_INFO = "select userID, userLogin.userName, firstName, lastName, image, " +
//			"emailID,userLogin.lastActivityDate, aboutUser, address, contactNumber,designationid.value,salary,accno,department.id,designationid.id,role.ID from User where company.companyID=? and deleted=?";
            String SELECT_USER_INFO="from User where company.companyID=? and deleteflag=?";
			List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{AuthHandler.getCompanyid(request),0});
            count = list.size();
			List list1 = HibernateUtil.executeQueryPaging(session, SELECT_USER_INFO, new Object[]{AuthHandler.getCompanyid(request),false}, new Integer[]{start, limit});
            Iterator itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                User u=(User) itr.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                //Object[] row = (Object[]) itr.next();
                JSONObject obj = new JSONObject();
                // obj.put("department",(StringUtil.isNullOrEmpty(u.getDepartment().getValue())? "" : u.getDepartment().getValue()));
                obj.put("department",(ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                obj.put("departmentname",(ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                obj.put("role",(ua.getRole() == null ? "" :ua.getRole().getID()));
                String name="";
                if(ua.getRole()!=null&&ua.getRole().getCompany()!=null){
                	name = ua.getRole().getName();
                }else{
                	name = MessageSourceProxy.getMessage("hrms.common.role."+ua.getRole().getID() ,null,ua.getRole().getName(), LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0));
                }
                obj.put("rolename",(ua.getRole() == null ? "" :name));
				obj.put("userid",u.getUserID() );
				obj.put("username",u.getUserLogin().getUserName());
				obj.put("fname",u.getFirstName() );
				obj.put("lname",u.getLastName() );
                obj.put("image", u.getImage());
				obj.put("emailid",u.getEmailID());
                obj.put("lastlogin", (u.getUserLogin().getLastActivityDate() == null ? "" : AuthHandler.getDateFormatter(request).format(u.getUserLogin().getLastActivityDate())));
                obj.put("aboutuser", u.getAboutUser());
                obj.put("address", u.getAddress());
                obj.put("contactno", u.getContactNumber());
				obj.put("designation",ua.getDesignationid()== null?"":ua.getDesignationid().getValue() );
                obj.put("designationid",ua.getDesignationid()== null?"":ua.getDesignationid().getId());
                obj.put("salary",ua.getSalary());
                obj.put("accno", ua.getAccno());
               // obj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, u.getEmployeeid()));
//                 obj.put("department",(row[13] == null ? "" : row[13]));
//                obj.put("role",(row[15] == null ? "" : row[15]));
//				obj.put("userid", row[0]);
//				obj.put("username", row[1]);
//				obj.put("fname", row[2]);
//				obj.put("lname", row[3]);
//				obj.put("image", row[4]);
//				obj.put("emailid", row[5]);
//				obj.put("lastlogin", (row[6] == null ? "" : AuthHandler.getDateFormatter(request).format(row[6])));
//				obj.put("aboutuser", row[7]);
//				obj.put("address", row[8]);
//				obj.put("contactno", row[9]);
//				obj.put("designation", row[10]);
//                obj.put("designationid",row[14]);
//                obj.put("salary",row[11]);
//                obj.put("accno", row[12]);
//				if(!StringUtil.isNullOrEmpty(row[11].toString()))
//				{
//					Newuser nw=(Newuser) session.load(Newuser.class,row[11].toString());
//					obj.put("salary",nw.getSalary());
//				}

                List lst1 = HibernateUtil.executeQuery(session, "from  Assignmanager where assignemp.userID=? and managerstatus=1", u.getUserID());
                Iterator itr1 = lst1.iterator();
                if (itr1.hasNext()) {
                    while (itr1.hasNext()) {
                        Assignmanager asm = (Assignmanager) itr1.next();
                        obj.append("manager", asm.getAssignman().getFirstName() + " " + asm.getAssignman().getLastName());
                    }
                } else {
                    obj.put("manager", " ");
                }
                jArr.put(obj);
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }

        return jobj;
    }

    public static JSONObject getValidUserOptions(Session session, String userid) throws ServiceException {
        return new JSONObject();
    }

    public static JSONObject saveUser(Session session, HttpServletRequest request, HashMap hm) throws ServiceException {
        JSONObject obj = new JSONObject();
//        Integer codeid2 = null;
//        try {
//            String id = (String) hm.get("userid");
//             if (StringUtil.isNullOrEmpty((String) hm.get("employeeid")) == false) {
//            String[] codeid = ((String) hm.get("employeeid")).split("-");
//
//            for (int x = 0; x < codeid.length; x++) {
//                if (codeid[x].matches("[0-9]*") == true) {
//                    codeid2 = Integer.parseInt(codeid[x]);
//                }
//            }
//             }
//            UserLogin userLogin;
//            String auditMessage="";
//            String auditID="";
//            User user;
//            User creater= (User)session.load(User.class, AuthHandler.getUserid(request));
//            String fullnameCreator = AuthHandler.getFullName(creater);
//            String companyid=AuthHandler.getCompanyid(request);
//            String pwd = null;
////			if (id != null && id.length() > 0) {
////				user = (User) session.load(User.class, id);
////				userLogin = user.getUserLogin();
////                obj.put("msg", "User has been edited successfully");
////			}
//            if (StringUtil.isNullOrEmpty(id) == false) {
//                user = (User) session.load(User.class, id);
//                userLogin = user.getUserLogin();
//                String pass = (String) hm.get("password");
//                String oldpass = (String) hm.get("oldpassword");
//                if (StringUtil.isNullOrEmpty(oldpass) == false) {
//                    if (AuthHandler.getSHA1(oldpass).equals(userLogin.getPassword())) {
//                        if (StringUtil.isNullOrEmpty(pass) == false) {
//                            userLogin.setPassword(AuthHandler.getSHA1(pass));
//                        }
//                    } else {
//                        throw new Exception("Old Password does not match");
//                    }
//                }
//                 String q2 = "from User where employeeid=? and userID=? and company.companyID=?";
//                 String q3 = "from User where employeeid=? and company.companyID=?";
//                if (HibernateUtil.executeQuery(session, q2, new Object[]{codeid2,(String)hm.get("userid"),companyid}).isEmpty() == true) {
//                   if (HibernateUtil.executeQuery(session, q3, new Object[]{codeid2,companyid}).isEmpty() == false) {
//                    throw new Exception("Employee ID already present");
//                   }
//                }
//                if (user.getUserID().equals(AuthHandler.getUserid(request))) {
//                    auditMessage = "User " + fullnameCreator + " has modified his profile";
//                    auditID = AuditAction.PROFILE_CHANGED;
//                } else {
//                    auditMessage = "Profile of user " + AuthHandler.getFullName(user) + " has been modified by " + fullnameCreator;
//                    auditID = AuditAction.USER_MODIFIED;
//                }
//                  if (StringUtil.isNullOrEmpty((String) hm.get("templateid")) == false) {
//                     user.setTemplateid((String) hm.get("templateid"));
//                }
//                  else {
//                     user.setTemplateid(" ");
//                }
//                obj.put("msg", "User has been edited successfully");
//            } else {
//                String uuid=UUID.randomUUID().toString();
//                user = new User();
//                userLogin = new UserLogin();
//                userLogin.setUserID(uuid);
//                user.setUserLogin(userLogin);
//                userLogin.setUser(user);
//                String q = "from User where userLogin.userName=?";// and company.companyID=?";
//                if (HibernateUtil.executeQuery(session, q, new Object[]{hm.get("username")}).isEmpty() == false && hm.get("username").equals(userLogin.getUserName()) == false) {
//                    throw new Exception("User name not available");
//                }
//                String q2 = "from User where employeeid=? and company.companyID=?";
//                if (HibernateUtil.executeQuery(session, q2, new Object[]{codeid2,companyid}).isEmpty() == false) {
//                    throw new Exception("Employee ID already present");
//                }
//
//
//
//                userLogin.setUserName((String) hm.get("username"));
//                pwd = AuthHandler.generateNewPassword();
//                userLogin.setPassword(AuthHandler.getSHA1(pwd));
//                user.setCompany((Company) session.load(Company.class, AuthHandler.getCompanyid(request)));
//                obj.put("msg", "User has been saved successfully");
//            }
//
//            user.setFirstName((String) hm.get("fname"));
//            user.setLastName((String) hm.get("lname"));
//            user.setEmailID((String) hm.get("emailid"));
//            user.setAddress((String) hm.get("address"));
//            user.setContactNumber((String) hm.get("contactno"));
//            user.setEmployeeid(codeid2);
//            if (StringUtil.isNullOrEmpty((String) hm.get("roleid")) == false) {
//                user.setRole((Role) session.load(Role.class, (String) hm.get("roleid")));
//            }
//            if (StringUtil.isNullOrEmpty((String) hm.get("designationid")) == false) {
//                user.setDesignation("none");
//                user.setDesignationid((MasterData) session.load(MasterData.class, (String) hm.get("designationid")));
//            }
//            if (StringUtil.isNullOrEmpty((String) hm.get("department")) == false) {
//                user.setDepartment((MasterData) session.load(MasterData.class, (String) hm.get("department")));
//            }
//            if (StringUtil.isNullOrEmpty((String) hm.get("salary")) == false) {
//                if (((String) hm.get("salary")).length() > 0) {
//                    user.setSalary((String) hm.get("salary"));
//                } else {
//                    user.setSalary("0");
//                }
//            }
//
//            if (StringUtil.isNullOrEmpty((String) hm.get("accno")) == false) {
//                if (((String) hm.get("accno")).length() > 0) {
//                    user.setAccno((String) hm.get("accno"));
//                } else {
//                    user.setAccno("0");
//                }
//            }
//
//            if (StringUtil.isNullOrEmpty((String) hm.get("formatid")) == false) {
//                user.setDateFormat((KWLDateFormat) session.load(KWLDateFormat.class, (String) hm.get("formatid")));
//            }
//            String diff = null;
//            if (StringUtil.isNullOrEmpty((String) hm.get("tzid")) == false) {
//                KWLTimeZone timeZone = (KWLTimeZone) session.load(KWLTimeZone.class, (String) hm.get("tzid"));
//                diff = timeZone.getDifference();
//                user.setTimeZone(timeZone);
//            }
//            if (StringUtil.isNullOrEmpty((String) hm.get("aboutuser")) == false) {
//                user.setAboutUser((String) hm.get("aboutuser"));
//            }
//
//            String imageName = ((FileItem) (hm.get("userimage"))).getName();
//            if (StringUtil.isNullOrEmpty(imageName) == false) {
//                session.saveOrUpdate(user);
//                String fileName = user.getUserID() + FileUploadHandler.getImageExt();
//                user.setImage(ProfileImageServlet.ImgBasePath + fileName);
//                new FileUploadHandler().uploadImage((FileItem) hm.get("userimage"),
//                        fileName,
//                        StorageHandler.GetProfileImgStorePath(), 100, 100, false, false);
//            }
//            //  user.setCompany((Company)session.load(Company.class,AuthHandler.getCompanyid(request)));
//            session.saveOrUpdate(userLogin);
//            session.saveOrUpdate(user);
//            SessionHandler.updatePreferences(request, null, (StringUtil.isNullOrEmpty((String) hm.get("formatid")) ? null : (String) hm.get("formatid")), (StringUtil.isNullOrEmpty((String) hm.get("tzid")) ? null : (String) hm.get("tzid")), diff);
//            if (StringUtil.isNullOrEmpty(id)) {
////                String permission="from ProjectFeature where displayfeaturename=?";
////                List perm=HibernateUtil.executeQuery(session, permission,"Employee");
////                ProjectFeature perms=(ProjectFeature) perm.get(0);
////
////                ProjectFeature perms1=(ProjectFeature) session.load(ProjectFeature.class,perms.getFeatureID());
////                UserPermission uperm=new UserPermission();
////                uperm.setFeature(perms1);
////                uperm.setRole(userLogin);
////                uperm.setPermissionCode(1);
////                session.save(uperm);
//
//
//                String uri = URLUtil.getPageURL(request, Links.loginpageFull);
//                String pmsg = String.format(KWLErrorMsgs.msgMailInvite, user.getFirstName(), fullnameCreator, userLogin.getUserName(), pwd, uri, fullnameCreator);
//                String htmlmsg = String.format(KWLErrorMsgs.msgMailInviteUsernamePassword, user.getFirstName(), fullnameCreator, AuthHandler.getCompanyName(request), userLogin.getUserName(),
//                        pwd, uri, uri, fullnameCreator);
////				try {
////					// SendMailHandler.postMail(new String[] { user.getEmailID() },"[Deskera] Welcome to Deskera HRMS", htmlmsg, pmsg, creater.getEmailID());
////				} catch (ConfigurationException e) {
////					e.printStackTrace();
////				} catch (MessagingException e) {
////					e.printStackTrace();
////			}
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw ServiceException.FAILURE(e.getMessage(), e);
//        }
        return obj;
    }

    public static void deleteUser(Session session, HttpServletRequest request) throws ServiceException {
        try {
            JSONObject jobj = new JSONObject();
            JSONArray jArr = new JSONArray();

            String[] ids = request.getParameterValues("userids");
            for (int i = 0; i < ids.length; i++) {
                User u = (User) session.load(User.class, ids[i]);
                if (u.getRoleID() == 1) {
                    throw new Exception("Cannot delete Administrator");
                }
                //UserLogin userLogin = (UserLogin) session.load(UserLogin.class, ids[i]);
                u.setDeleteflag(1);
                session.saveOrUpdate(u);
            //session.delete(userLogin);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static void setPassword(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String password = request.getParameter("password");
            if (password == null || password.length() <= 0) {
                password = AuthHandler.generateNewPassword();
            }
            String newpass = AuthHandler.getSHA1(password);
            User user = (User) session.load(User.class, request.getParameter("userid"));
            UserLogin userLogin = user.getUserLogin();
            userLogin.setPassword(newpass);
            session.saveOrUpdate(userLogin);
            String uri = URLUtil.getPageURL(request, Links.loginpageFull);
            String fname = user.getFirstName();
            if (StringUtil.isNullOrEmpty(fname)) {
                fname = user.getUserLogin().getUserName();
            }
            String pmsg = String.format(
                    KWLErrorMsgs.msgTempPassword,
                    fname, password, uri);
            String htmlmsg = String.format(
                    KWLErrorMsgs.msgMailPassword,
                    fname, password, uri, uri);
            try {
                String adminEmailId = request.getSession().getAttribute("sysemailid").toString();
                SendMailHandler.postMail(new String[]{user.getEmailID()},
                        KWLErrorMsgs.msgMailSubjectPassword, htmlmsg,
                        pmsg, adminEmailId);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.setPassword", e);
        }
    }

    public static void setPermissions(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String id = request.getParameter("roleid");
            String[] features = request.getParameterValues("features");
            String[] permissions = request.getParameterValues("permissions");
            String sql = "delete from UserPermission where role.ID=?";
            HibernateUtil.executeUpdate(session, sql, id);
            Role role = (Role) session.load(Role.class, id);
            for (int i = 0; i < features.length; i++) {
                if (permissions[i].equals("0")) {
                    continue;
                }
                UserPermission permission = new UserPermission();
                permission.setRole(role);
                permission.setFeature((ProjectFeature) session.load(ProjectFeature.class, features[i]));
                permission.setPermissionCode(Long.parseLong(permissions[i]));
                session.save(permission);
            }
           // insertAuditLog(session, AuditAction.PERMISSIONS_MODIFIED, AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has changed the permissions of role " + role.getName(), request);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.setPermissions", e);
        }
    }

    public static void updateLastLogin(Session session, HttpServletRequest request) throws ServiceException {
        try {
            UserLogin userLogin = (UserLogin) session.load(UserLogin.class, AuthHandler.getUserid(request));
            userLogin.setLastActivityDate(new Date());
            session.update(userLogin);
         //   insertAuditLog(session, AuditAction.LOG_IN_SUCCESS, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has logged in", request);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.updateLastLogin", e);
        }
    }

    public static JSONObject getAllTimeZones(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query = "from KWLTimeZone";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                KWLTimeZone timeZone = (KWLTimeZone) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", timeZone.getTimeZoneID());
                obj.put("name", timeZone.getName());
                obj.put("difference", timeZone.getDifference());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllTimeZones", e);
        }

        return jobj;
    }

    public static JSONObject getAllCurrencies(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query = "from KWLCurrency";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                KWLCurrency currency = (KWLCurrency) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("currencyid", currency.getCurrencyID());
                obj.put("symbol", currency.getSymbol());
                obj.put("currencyname", currency.getName());
                obj.put("htmlcode", currency.getHtmlcode());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllCurrencies", e);
        }

        return jobj;
    }

    public static JSONObject getCompanyInformation(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONObject modobj=new JSONObject();
        try {
            String query = "select c,cpr from CompanyPreferences c right outer join c.company cpr where cpr.companyID=?";
            List list = HibernateUtil.executeQuery(session, query, AuthHandler.getCompanyid(request));
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                Company company =(Company) row[1];
                CompanyPreferences cmpr=null;
                JSONObject obj = new JSONObject();
                obj.put("phone", company.getPhoneNumber());
                obj.put("state", company.getState());
                obj.put("currency", (company.getCurrency() == null ? "1" : company.getCurrency().getCurrencyID()));
                obj.put("city", company.getCity());
                obj.put("emailid", company.getEmailID());
                obj.put("companyid", company.getCompanyID());
                obj.put("timezone", (company.getTimeZone() == null ? "1" : company.getTimeZone().getTimeZoneID()));
                obj.put("zip", company.getZipCode());
                obj.put("fax", company.getFaxNumber());
                obj.put("website", company.getWebsite());
                obj.put("image", company.getCompanyLogo());
                obj.put("modifiedon", (company.getModifiedOn() == null ? "" : AuthHandler.getDateFormatter(request).format(company.getModifiedOn())));
                obj.put("createdon", (company.getCreatedOn() == null ? "" : AuthHandler.getDateFormatter(request).format(company.getCreatedOn())));
                obj.put("companyname", company.getCompanyName());
                obj.put("country", (company.getCountry() == null ? "" : company.getCountry().getID()));
                obj.put("address", company.getAddress());
                obj.put("subdomain", company.getSubDomain());
                //obj.put("subscription",company.getSubscriptionCode());
                if(row[0]!=null){
                cmpr=(CompanyPreferences)row[0];
                obj.put("employeeidformat", cmpr.getEmpidformat());
                obj.put("jobidformat", cmpr.getJobidformat());
                obj.put("selfapp",cmpr.isSelfappraisal());
                obj.put("competency",cmpr.isCompetency());
                obj.put("goal",cmpr.isGoal());
                obj.put("annmng",cmpr.isAnnmanager());
                obj.put("approveappraisal",cmpr.isApproveappraisal());
                obj.put("promotionrec",cmpr.isPromotion());
                obj.put("weightage",cmpr.isWeightage());
                obj.put("reviewappraisal",cmpr.isReviewappraisal());
                obj.put("partial",cmpr.isPartial());
                obj.put("fullupdates",cmpr.isFullupdates());
                obj.put("modaverage",cmpr.isModaverage());
                }else{
                obj.put("selfapp",false);
                obj.put("competency",false);
                obj.put("goal",false);
                obj.put("annmng",false);
                obj.put("approveappraisal",false);
                obj.put("promotionrec",false);
                obj.put("weightage",false);
                obj.put("reviewappraisal",false);
                obj.put("partial",false);
                obj.put("fullupdates",false);
                obj.put("modaverage",false);
                }
                modobj=gethrmsModules(session);
                obj.put("modules",modobj);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getCompanyInformation", e);
        }

        return jobj;
    }

    public static JSONObject getCompanyHolidays(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query = "from CompanyHoliday where company.companyID=? order by holidayDate";
            List list = HibernateUtil.executeQuery(session, query, AuthHandler.getCompanyid(request));
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                CompanyHoliday holiday = (CompanyHoliday) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("holiday", AuthHandler.getDateFormatter(request).format(holiday.getHolidayDate()));
                obj.put("description", holiday.getDescription());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getCompanyHolidays", e);
        }

        return jobj;
    }

    public static JSONObject getAllCountries(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query = "from Country";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                Country country = (Country) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", country.getID());
                obj.put("name", country.getCountryName());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllCountries", e);
        }

        return jobj;
    }

    public static void updateCompany(Session session, HttpServletRequest request, HashMap hm) throws ServiceException {
        try {
            CompanyPreferences cp = (CompanyPreferences) session.load(CompanyPreferences.class, AuthHandler.getCompanyid(request));
            Company company = cp.getCompany();
            company.setCompanyName((String) hm.get("companyname"));
            company.setAddress((String) hm.get("address"));
            company.setCity((String) hm.get("city"));
            company.setState((String) hm.get("state"));
            company.setZipCode((String) hm.get("zip"));
            company.setPhoneNumber((String) hm.get("phone"));
            company.setFaxNumber((String) hm.get("fax"));
            company.setWebsite((String) hm.get("website"));
            company.setEmailID((String) hm.get("mail"));
            company.setSubDomain((String) hm.get("domainname"));
            cp.setEmpidformat((String) hm.get("employeeidformat"));
            company.setCountry((Country) session.load(Country.class, (String) hm.get("country")));
            company.setCurrency((KWLCurrency) session.load(KWLCurrency.class, (String) hm.get("currency")));
            KWLTimeZone timeZone = (KWLTimeZone) session.load(KWLTimeZone.class, (String) hm.get("timezone"));
            company.setTimeZone(timeZone);
            company.setModifiedOn(new Date());
            JSONArray jArr = new JSONArray((String) hm.get("holidays"));
            Set<CompanyHoliday> holidays = company.getHolidays();
            holidays.clear();
            DateFormat formatter = AuthHandler.getDateFormatter(request);
            for (int i = 0; i < jArr.length(); i++) {
                CompanyHoliday day = new CompanyHoliday();
                JSONObject obj = jArr.getJSONObject(i);
                day.setDescription(obj.getString("description"));
                day.setHolidayDate(formatter.parse(obj.getString("day")));
                day.setCompany(company);
                holidays.add(day);
            }
            String imageName = ((FileItem) (hm.get("logo"))).getName();
            if (StringUtil.isNullOrEmpty(imageName) == false) {
                String fileName = AuthHandler.getCompanyid(request) + FileUploadHandler.getCompanyImageExt();
                company.setCompanyLogo(ProfileImageServlet.ImgBasePath + fileName);
                new FileUploadHandler().uploadImage((FileItem) hm.get("logo"),
                        fileName,
                        StorageHandler.GetProfileImgStorePath(), 130, 25, true, false);
            }
            session.update(company);
            SessionHandler.updatePreferences(request, (String) hm.get("currency"), null, (String) hm.get("timezone"), timeZone.getDifference());
           // insertAuditLog(session, AuditAction.COMPANY_UPDATION, "User " + AuthHandler.getUserName(request) + " changed company details", request);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.updateCompany", e);
        }
    }

    public static JSONObject getManagers(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String SELECT_USER_INFO = "from User where (role.ID=? or role.ID=?) and company.companyID=? and deleteflag=? order by firstName";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{Role.COMPANY_MANAGER,Role.COMPANY_ADMIN, AuthHandler.getCompanyid(request),0});
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                User u = (User) itr.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + (u.getLastName()!=null?u.getLastName():""));
                obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("department", ua.getDepartment()!=null?ua.getDepartment().getValue():"");
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }

        return jobj;
    }

    public static JSONObject getAllCompanyDetails(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        JSONObject jobj = new JSONObject();
        int count;
        String admincmp = "ff808081227de9f701227de9fb410001";
        try {
            String SelectCompany = "from Company where companyID <> ? and deleted=?";
            List list = HibernateUtil.executeQuery(session, SelectCompany, new Object[]{admincmp, false});
            count = list.size();
            List list1 = HibernateUtil.executeQueryPaging(session, SelectCompany, new Object[]{admincmp, false}, new Integer[]{start, limit});
            Iterator itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                Company company = (Company) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("companyid", company.getCompanyID());
                obj.put("companyname", company.getCompanyName());
                //obj.put("currency",company.getCurrency());
                //obj.put("timezone",company.getTimeZone());
                //obj.put("subdomain",company.getSubDomain());
                obj.put("address", company.getAddress());
                obj.put("city", company.getCity());
                obj.put("phoneno", company.getPhoneNumber());
                String uq = "from User where role.ID=1 and company.companyID=?";
                List cmpls=HibernateUtil.executeQuery(session, uq, company.getCompanyID());
                try{
                User u = (User) cmpls.get(0);
                obj.put("admin_fname", u.getFirstName());
                obj.put("admin_lname", u.getLastName());
                obj.put("admin_uname", u.getUserLogin().getUserName());
                obj.put("emailid", company.getEmailID());
                //    obj.put("website",company.getWebsite());
                //obj.put("emailid", company.getEmailID());
                //	 obj.put("country",(company.getCountry()==null)?"":company.getCountry());//==null?
                }catch(Exception e){}
                jArr.put(obj);
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllCompanyDetails", e);
        }
        return jobj;
    }

    public static JSONObject getUserofCompany(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        JSONObject jobj = new JSONObject();
        int count;
        try {
            String SELECT_USER = "from User as u where u.company.companyID=?";
            String cid = request.getParameter("companyid");
            List list = HibernateUtil.executeQuery(session, SELECT_USER, cid);
            count = list.size();
            List list1 = HibernateUtil.executeQueryPaging(session, SELECT_USER, new Object[]{cid}, new Integer[]{start, limit});
            Iterator itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                User user = (User) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", user.getFirstName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", (user.getUserLogin().getLastActivityDate() == null ? "" : AuthHandler.getDateFormatter(request).format(user.getUserLogin().getLastActivityDate())));
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                jArr.put(obj);
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }
        return jobj;
    }

    public static void deletecompany(Session session, HttpServletRequest request) throws ServiceException {
        try {            
            String[] ids = request.getParameterValues("cmpid");
            for (int i = 0; i < ids.length; i++) {
                Company c = (Company) session.load(Company.class, ids[i]);
                c.setDeleted(1);
                session.saveOrUpdate(c);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.deletecompanies", e);
        }
    }

    public static JSONObject changePassword(String platformURL,Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String msg = "";
        try {
            String password = request.getParameter("currentpassword");
            String pwd = request.getParameter("changepassword").toString();
            String uid = AuthHandler.getUserid(request);
            String companyid = AuthHandler.getCompanyid(request);

            if (password == null || password.length() <= 0) {
                msg = MessageSourceProxy.getMessage("hrms.common.InvalidPassword", null,"Invalid Password.", RequestContextUtils.getLocale(request));
            } else {
                //String newpass=AuthHandler.getSHA1(password);
                if(StringUtil.isStandAlone()){
                    platformURL ="";
                }
                if (!StringUtil.isNullOrEmpty(platformURL)) {
                    JSONObject userData = new JSONObject();
                    userData.put("pwd", pwd);
                    userData.put("oldpwd", password);
                    userData.put("userid", uid);
                    userData.put("remoteapikey", StorageHandler.GetRemoteAPIKey());
                    String action = "3";
                    JSONObject resObj = APICallHandler.callApp(platformURL, userData, companyid, action);
                    if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                        User user = (User) session.load(User.class, uid);
                        UserLogin userLogin = user.getUserLogin();
                        userLogin.setPassword(pwd);
                        session.saveOrUpdate(userLogin);
                        msg = MessageSourceProxy.getMessage("hrms.common.PasswordChangedsuccessfully", null,"Invalid Password.", RequestContextUtils.getLocale(request));

                    } else {
                        if (!resObj.isNull("errorcode") && resObj.getString("errorcode").equals("e10")) {
                            msg =MessageSourceProxy.getMessage("hrms.common.OldpasswordisincorrectPleasetryagain", null,"Invalid Password.", RequestContextUtils.getLocale(request));
                        } else {
                            msg =MessageSourceProxy.getMessage("hrms.common.ErrorinchangingPassword", null,"Invalid Password.", RequestContextUtils.getLocale(request));
                        }
                    }
                } else {
                    User user = (User) session.load(User.class, uid);
                    UserLogin userLogin = user.getUserLogin();
                    String currentpass = userLogin.getPassword();
                    if (StringUtil.equal(password, currentpass)) {
                        userLogin.setPassword(pwd);
                        session.saveOrUpdate(userLogin);
                        msg = MessageSourceProxy.getMessage("hrms.common.PasswordChangedsuccessfully", null,"Invalid Password.", RequestContextUtils.getLocale(request));
                    } else {
                        msg =MessageSourceProxy.getMessage("hrms.common.OldpasswordisincorrectPleasetryagain", null,"Invalid Password.", RequestContextUtils.getLocale(request));
                    }
                }
            }
            jobj.put("msg", msg);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.setPassword", e);
        }
        return jobj;
    }

    public static void insertAuditLog(Session session, String actionid, String details, HttpServletRequest request, String recid) throws ServiceException {
        try {
            AuditAction action = (AuditAction) session.load(AuditAction.class, actionid);
            insertAuditLog(session, action, details, request, recid);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static void insertAuditLog(Session session, String actionid, String details, String ipAddress, String userid, String recid) throws ServiceException {
        try {
            AuditAction action = (AuditAction) session.load(AuditAction.class, actionid);
            User user = (User) session.load(User.class, userid);
            insertAuditLog(session, action, details, ipAddress, user, recid);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static void insertAuditLog(Session session, AuditAction action, String details, HttpServletRequest request, String recid) throws ServiceException {
        try {
            User user = (User) session.load(User.class, AuthHandler.getUserid(request));
            String ipaddr = null;
            if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                ipaddr = request.getRemoteAddr();
            } else {
                ipaddr = request.getHeader("x-real-ip");
            }

            insertAuditLog(session, action, details, ipaddr, user, recid);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static void insertAuditLog(Session session, AuditAction action, String details, String ipAddress, User user, String recid) throws ServiceException {
        try {
            String aid = UUID.randomUUID().toString();
            AuditTrail auditTrail = new AuditTrail();
            auditTrail.setID(aid);
            auditTrail.setAction(action);
            auditTrail.setAuditTime(new Date());
            auditTrail.setDetails(details);
            auditTrail.setIPAddress(ipAddress);
            auditTrail.setRecid(recid);
            auditTrail.setExtraid("0");
            auditTrail.setUser(user);
            session.save(auditTrail);
            String id=auditTrail.getID();

            ArrayList<Object> indexFieldDetails = new ArrayList<Object>();
              ArrayList<String> indexFieldName = new ArrayList<String>();
              indexFieldDetails.add(details);
               indexFieldName.add("details");
               indexFieldDetails.add(id);
               indexFieldName.add("transactionid");
               indexFieldDetails.add(action.getID());
               indexFieldName.add("actionid");
               indexFieldDetails.add(ipAddress);
               indexFieldName.add("ipaddr");
               String userName = user.getUserLogin().getUserName()+" "+user.getFirstName()+" "+user.getLastName();
               indexFieldDetails.add(userName);
               indexFieldName.add("username");
               indexFieldDetails.add(auditTrail.getAuditTime());
               indexFieldName.add("timestamp");
                String indexPath = com.krawler.esp.handlers.StorageHandler.GetAuditTrailIndexPath();
                com.krawler.esp.indexer.KrawlerIndexCreator kwlIndex = new com.krawler.esp.indexer.KrawlerIndexCreator();
                kwlIndex.setIndexPath(indexPath);
                com.krawler.esp.indexer.CreateIndex  cIndex = new com.krawler.esp.indexer.CreateIndex();
                cIndex.indexAlert(kwlIndex, indexFieldDetails, indexFieldName);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static JSONObject getAuditTrail(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String ss = request.getParameter("ss");
            String searchtext = request.getParameter("search");
            String gid = request.getParameter("groupid");
            String companyid = AuthHandler.getCompanyid(request);

            String auditID = "";
            if (searchtext.compareTo("") != 0) {
                String query2 = searchtext + "*";
                SearchBean bean = new SearchBean();
                String indexPath = StorageHandler.GetAuditTrailIndexPath();
                String[] searchWithIndex = {"details", "ipaddr", "username"};
                Hits hitResult = bean.skynetsearchMulti(query2, searchWithIndex, indexPath);
                if (hitResult != null) {
                    Iterator itrH = hitResult.iterator();
                    while (itrH.hasNext()) {
                        Hit hit1 = (Hit) itrH.next();
                        org.apache.lucene.document.Document doc = hit1.getDocument();
                        auditID += "'" + doc.get("transactionid") + "',";
                    }
                    if (auditID.length() > 0) {
                        auditID = auditID.substring(0, auditID.length() - 1);
                    }
                }
            }
            List recordTotalCount = null;
            List list=null;
            if (gid.compareTo("") != 0 && searchtext.compareTo("") != 0) {  /* query for both gid and search  */
                String query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ") and action.auditGroup.ID = ? order by auditTime desc";
                recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{companyid, gid});
                list = HibernateUtil.executeQueryPaging(session, query, new Object[]{companyid, gid}, new Integer[]{start, limit});
            } else if (gid.compareTo("") != 0 && searchtext.compareTo("") == 0) { /* query only for gid  */
                String query = "from AuditTrail where user.company.companyID=? and action.auditGroup.ID = ? order by auditTime desc";
                recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{companyid, gid});
                list = HibernateUtil.executeQueryPaging(session, query, new Object[]{companyid, gid}, new Integer[]{start, limit});
            } else if (gid.compareTo("") == 0 && searchtext.compareTo("") != 0) {  /* query only for search  */
                String query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ")  order by auditTime desc";
                recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{companyid});
                list = HibernateUtil.executeQueryPaging(session, query, new Object[]{companyid}, new Integer[]{start, limit});
            } else {        /* query for all  */
                String query = "from AuditTrail where user.company.companyID=?  order by auditTime desc";
                recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{companyid});
                list = HibernateUtil.executeQueryPaging(session, query, new Object[]{companyid}, new Integer[]{start, limit});
            }

            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                AuditTrail auditTrail = (AuditTrail) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", auditTrail.getID());
                obj.put("username", AuthHandler.getFullName(auditTrail.getUser()));
                obj.put("ipaddr", auditTrail.getIPAddress());
                obj.put("details", auditTrail.getDetails());
                obj.put("timestamp", AuthHandler.getDateFormatter(request).format(auditTrail.getAuditTime()));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", recordTotalCount.size());
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }

        return jobj;
    }

    public static JSONObject getAuditGroups(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            String query = "from AuditGroup";

            List list = list = HibernateUtil.executeQuery(session, query);
            int count = list.size();
            if (start != null && limit != null) {
                list = HibernateUtil.executeQueryPaging(session, query, new Integer[]{Integer.parseInt(start), Integer.parseInt(limit)});
            }

            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                AuditGroup auditGroup = (AuditGroup) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("groupid", auditGroup.getID());
                obj.put("groupname", auditGroup.getGroupName());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }

        return jobj;
    }

    private static String getFormattedDate(Date curDate, String javaForm) {
        SimpleDateFormat sdf = new SimpleDateFormat(javaForm);
        return sdf.format(curDate);
    }

    public static JSONObject getAllDateFormats(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String query = "from KWLDateFormat";
            List list = HibernateUtil.executeQuery(session, query);
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            Date curDate = new Date();
            while (itr.hasNext()) {
                KWLDateFormat dateFormat = (KWLDateFormat) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("formatid", dateFormat.getFormatID());
                obj.put("formalname", dateFormat.getName());
                obj.put("name", getFormattedDate(curDate, dateFormat.getJavaForm()));
                obj.put("javaform", dateFormat.getJavaForm());
                obj.put("scriptform", dateFormat.getScriptForm());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }

        return jobj;
    }

    public static JSONObject getparticularUserDetails(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String lid = request.getParameter("lid");
            Object[] params = new Object[]{AuthHandler.getCompanyid(request), lid};
            String SELECT_USER_INFO = "from User where company.companyID=? and userID=?";

            List list = list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, params);


            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                User user = (User) itr.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                UserLogin ul = user.getUserLogin();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", ul.getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", (ul.getLastActivityDate() == null ? "" : AuthHandler.getDateFormatter(request).format(ul.getLastActivityDate())));
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                obj.put("formatid", (user.getDateFormat() == null ? "4" : user.getDateFormat().getFormatID()));
                obj.put("tzid", (user.getTimeZone() == null ? "23" : user.getTimeZone().getTimeZoneID()));
                //obj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, user.getEmployeeid()));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }

        return jobj;
    }
    public static JSONObject getAllUserDetails_profile(Session session, HttpServletRequest request, int start, int limit) throws ServiceException,JSONException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        int count;
        try {
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();            
            String SELECT_USER_INFO = "select emp,u from Empprofile emp right outer join emp.userLogin.user u where u.company.companyID=? and u.deleteflag=?";
            params.add(AuthHandler.getCompanyid(request));
            params.add(0);
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"u.firstName","u.lastName"});
                SELECT_USER_INFO +=searchQuery;
            }
            //@@useraccount

//            if(!StringUtil.isNullOrEmpty(request.getParameter("combo"))){
//                SELECT_USER_INFO =SELECT_USER_INFO +" order by u.firstName asc";
//            }else{
//                SELECT_USER_INFO =SELECT_USER_INFO +" order by u.employeeid asc";
//            }
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, params.toArray());
            count = list.size();
            List list1 = HibernateUtil.executeQueryPaging(session, SELECT_USER_INFO, params.toArray(), new Integer[]{start,limit});
            Iterator itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                JSONObject obj = new JSONObject();
                Empprofile e = null;
                User u = (User) row[1];
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
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
                	name = MessageSourceProxy.getMessage("hrms.common.role."+ua.getRole().getID() ,null,ua.getRole().getName(), LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0));
                }
                obj.put("rolename", (ua.getRole() == null ? "" : name));
                obj.put("userid", u.getUserID());
                obj.put("username", u.getUserLogin().getUserName());
                obj.put("fname", u.getFirstName());
                obj.put("lname", u.getLastName());
                obj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));
                obj.put("image", u.getImage());
                obj.put("emailid", u.getEmailID());
                obj.put("lastlogin", (u.getUserLogin().getLastActivityDate() == null ? "" : AuthHandler.getDateFormatter(request).format(u.getUserLogin().getLastActivityDate())));
                obj.put("aboutuser", u.getAboutUser());
                obj.put("address", u.getAddress());
                obj.put("contactno", u.getContactNumber());
                obj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                obj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                obj.put("salary", ua.getSalary());
                obj.put("accno", ua.getAccno());
                obj.put("templateid", ua.getTemplateid()!=null?ua.getTemplateid():"");
                //obj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, u.getEmployeeid()));

                List lst1 = HibernateUtil.executeQuery(session, "from  Assignmanager where assignemp.userID=? and managerstatus=1", u.getUserID());
                Iterator itr1 = lst1.iterator();
                if (itr1.hasNext()) {
                    while (itr1.hasNext()) {
                        Assignmanager asm = (Assignmanager) itr1.next();
                        if (asm.getAssignman() != null) {
                            obj.append("managerid", asm.getAssignman().getUserID());
                            obj.append("manager", asm.getAssignman().getFirstName() + " " + asm.getAssignman().getLastName());
                        } 
                    }
                } else {
                    obj.put("manager", " ");
                    obj.put("managerid", " ");
                }
                jArr.put(obj);
                lst1 = HibernateUtil.executeQuery(session, "from  Assignreviewer where employee.userID=? and reviewerstatus=1", u.getUserID());
                itr1 = lst1.iterator();
                if (itr1.hasNext()) {
                    while (itr1.hasNext()) {
                        Assignreviewer rev = (Assignreviewer) itr1.next();
                        if (rev.getReviewer() != null) {
                            obj.append("reviewerid", rev.getReviewer().getUserID());
                            obj.append("reviewer", rev.getReviewer().getFirstName() + " " + rev.getReviewer().getLastName());
                        }
                    }
                } else {
                    obj.put("reviewer", " ");
                    obj.put("reviewerid", " ");
                }
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        } finally {
            if(!jobj.has("data")) {
                jobj.put("count", 0);
                jobj.put("data", "");
            }
            return jobj;
        }
    }

     public static void update_profile_status(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        try {
           String[] ids = request.getParameterValues("emp_ids");
            for (int i = 0; i < ids.length; i++)
            {
                Empprofile prof = (Empprofile) session.load(Empprofile.class, ids[i]);                
                prof.setStatus("Approved");
                prof.setUpdated_by(AuthHandler.getUserid(request));
                prof.setUpdated_on(new Date());
                session.saveOrUpdate(prof);
                //ProfileHandler.insertAuditLog(session, AuditAction.PROFILE_APPROVED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has approved " + AuthHandler.getFullName(session, prof.getUserID()),request);
            }                
        } catch (HibernateException ex) {
            throw ServiceException.FAILURE("ProfileHandler.EmpProfilestatus", ex);
        } catch (SessionExpiredException ex) {
            throw ServiceException.FAILURE("ProfileHandler.EmpProfilestatus", ex);
        }
    }
       public static void setEmpIdFormat(Session session, HttpServletRequest request) throws ServiceException,SessionExpiredException,NullPointerException {
        try {
            String cmpid=AuthHandler.getCompanyid(request);
            CompanyPreferences cmpPref=null;
            Company company = (Company) session.load(Company.class,cmpid );
            
           // company.setSubscriptionCode(Long.parseLong(request.getParameter("subcription")));
            session.saveOrUpdate(company);
            String query="from CompanyPreferences where company.companyID=?";
            List   tabledata = HibernateUtil.executeQuery(session, query,cmpid );
            if(tabledata.size()==0){
               cmpPref=new CompanyPreferences();
               cmpPref.setCompany(company);
            }else{
             cmpPref=(CompanyPreferences) session.get(CompanyPreferences.class,cmpid);
            }
            cmpPref.setEmpidformat(request.getParameter("employeeidformat"));
            cmpPref.setJobidformat(request.getParameter("jobidformat"));
              if(StringUtil.isNullOrEmpty(request.getParameter("selfappraisal"))){
                  cmpPref.setSelfappraisal(false);
              }else{
                     cmpPref.setSelfappraisal(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("competencies"))){
                  cmpPref.setCompetency(false);
              }else{
                     cmpPref.setCompetency(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("goals"))){
                  cmpPref.setGoal(false);
              }else{
                     cmpPref.setGoal(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("annmng"))){
                  cmpPref.setAnnmanager(false);
              }else{
                     cmpPref.setAnnmanager(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("approveappr"))){
                  cmpPref.setApproveappraisal(false);
              }else{
                     cmpPref.setApproveappraisal(true);
              }
             if(StringUtil.isNullOrEmpty(request.getParameter("promotionrec"))){
                  cmpPref.setPromotion(false);
              }else{
                     cmpPref.setPromotion(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("weightage"))){
                  cmpPref.setWeightage(false);
              }else{
                     cmpPref.setWeightage(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("reviewappraisal"))){
                  cmpPref.setReviewappraisal(false);
              }else{
                     cmpPref.setReviewappraisal(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("partial"))){
                  cmpPref.setPartial(false);
              }else{
                  cmpPref.setPartial(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("fullupdates"))){
                  cmpPref.setFullupdates(false);
              }else{
                  cmpPref.setFullupdates(true);
              }
              if(StringUtil.isNullOrEmpty(request.getParameter("modaverage"))){
                  cmpPref.setModaverage(false);
              }else{
                  cmpPref.setModaverage(true);
              }
            session.saveOrUpdate(cmpPref);

        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("ProfileHandler.setEmpIdFormat", e);
        }  catch (NullPointerException e) {
            throw ServiceException.FAILURE("ProfileHandler.setEmpIdFormat", e);
        }
    }
         public static JSONObject getexEmployees(Session session, HttpServletRequest request, int start, int limit) throws ServiceException,JSONException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        int count;
        try {
            ArrayList params = new ArrayList();
            String ss = request.getParameter("ss");
            String SELECT_USER_INFO = "select emp,u from Empprofile emp right outer join emp.userLogin.user u where u.company.companyID=? and u.deleteflag=? and emp.termnd=? ";
            params.add(AuthHandler.getCompanyid(request));
            params.add(1);
            params.add(true);
            if(!StringUtil.isNullOrEmpty(ss)){
                //StringUtil.insertParamSearchString(params, ss, 2);
                SELECT_USER_INFO += StringUtil.getSearchString(ss, " and", new String[]{"u.firstName","u.lastName"});
            }
            //@@useraccount

//            if(!StringUtil.isNullOrEmpty(request.getParameter("combo"))){
//                SELECT_USER_INFO =SELECT_USER_INFO +" order by u.firstName asc";
//            }else{
//                SELECT_USER_INFO =SELECT_USER_INFO +" order by u.employeeid asc";
//            }
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, params.toArray());
            count = list.size();
            List list1 = HibernateUtil.executeQueryPaging(session, SELECT_USER_INFO, params.toArray(), new Integer[]{start, limit});
            Iterator itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                JSONObject obj = new JSONObject();
                Empprofile e = null;
                User u = (User) row[1];
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                e = (Empprofile) row[0];
                obj.put("department", (ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                obj.put("departmentname", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                obj.put("userid", u.getUserID());                
                obj.put("fname", u.getFirstName());
                obj.put("lname", u.getLastName());
                obj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));                
                obj.put("emailid", u.getEmailID());                                                
                obj.put("contactno", u.getContactNumber());
                obj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                obj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                obj.put("salary", ua.getSalary());
                //obj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, u.getEmployeeid()));
                obj.put("termdate",AuthHandler.getDateFormatter(request).format(e.getRelievedate()));
                if(e.getTercause()!=null)
                    obj.put("termreason",e.getTercause().getValue());
                else
                    obj.put("termreason","");
                obj.put("termdesc",e.getTerReason());
                if(e.getTerminatedby()!=null)
                obj.put("termby",AuthHandler.getFullName(e.getTerminatedby()));
                jArr.put(obj);
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }

        return jobj;
    }
         public static JSONObject getEmpHistory(Session session, HttpServletRequest request, int start, int limit) throws ServiceException,JSONException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        int count=0;
        List list=null;
        List list1=null;
        Iterator itr;
        try {
            String userid=request.getParameter("userid");
            String cmpid=AuthHandler.getCompanyid(request);
            String SELECT_USER_INFO = "from Emphistory where userid.userID=? and userid.company.companyID=? ";
            list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{userid,cmpid});
            count = list.size();           
            list1 = HibernateUtil.executeQueryPaging(session, SELECT_USER_INFO, new Object[]{userid,cmpid}, new Integer[]{start, limit});
            itr = list1.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {                
                JSONObject obj = new JSONObject();
                Emphistory ehst = (Emphistory) itr.next();
                obj.put("designation",(ehst.getDesignation()!=null?ehst.getDesignation().getValue():""));
                obj.put("department",(ehst.getDepartment()!=null?ehst.getDepartment().getValue():""));
                obj.put("startdate",ehst.getJoindate()!=null?AuthHandler.getDateFormatter(request).format(ehst.getJoindate()):"");
                obj.put("enddate",AuthHandler.getDateFormatter(request).format(ehst.getEnddate()));
                obj.put("salary",ehst.getSalary());
                obj.put("category","Designations ");
                obj.put("hid",ehst.getHid());
                jArr.put(obj);
            }
            String payroll="from Payhistory  where  userID.userID=? ";
            list = HibernateUtil.executeQuery(session, payroll, new Object[]{userid});
            count+=list.size();
            list1 = HibernateUtil.executeQueryPaging(session, payroll, new Object[]{userid}, new Integer[]{start, limit});
            itr = list1.iterator();
            while (itr.hasNext()) {
                JSONObject jobjtemp = new JSONObject();
                Payhistory group = (Payhistory) itr.next();               
                jobjtemp.put("designation", group.getDesign());
                jobjtemp.put("department", group.getDepartment());
                jobjtemp.put("salary", group.getNet());
                jobjtemp.put("startdate", AuthHandler.getDateFormatter(request).format(group.getCreatedon()));
                jobjtemp.put("enddate", AuthHandler.getDateFormatter(request).format(group.getCreatedfor()));
                jobjtemp.put("category","Payroll");
                jobjtemp.put("hid", group.getHistoryid());
                jArr.put(jobjtemp);
            }
            jobj.put("count", count);
            jobj.put("data", jArr);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("ProfileHandler.getEmpHistory", e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("ProfileHandler.getEmpHistory", e);
        }

        return jobj;
    }
        public static JSONObject gethrmsModules(Session session) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String SELECT_USER_INFO = "from hrms_Modules";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO);
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                hrms_Modules u = (hrms_Modules) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("moduleid", u.getModuleID());
                obj.put("modulename",u.getModuleName() );
                obj.put("moduledispname",u.getDisplayModuleName());                
                jArr.put(obj);
            }
            jobj.put("data", jArr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }

        return jobj;
    }
}
