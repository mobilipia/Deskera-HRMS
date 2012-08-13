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

package com.krawler.esp.servlets;

//import com.krawler.common.admin.*;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.CostCenter;
import com.krawler.common.admin.Country;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_Modules;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.krawler.esp.hibernate.impl.*;
import org.hibernate.*;
import java.io.*;
import java.sql.SQLException;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.PermissionHandler;
import com.krawler.esp.handlers.ProfileHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.web.resource.Links;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.Master;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.timesheet.Timesheet;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.*;
import javax.mail.MessagingException;

public class remoteapi extends HttpServlet {
   private static int action;
    /**
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Session session = null;
        String result = "";
        String validkey = StorageHandler.GetRemoteAPIKey();
        String remoteapikey = "";
        boolean testParam = false;
        if (!StringUtil.isNullOrEmpty(request.getParameter("data"))) {
            try {
                session = HibernateUtil.getCurrentSession();
                session.beginTransaction();
                JSONObject jobj = new JSONObject(request.getParameter("data"));
                if(jobj.has("remoteapikey"))
                    remoteapikey = jobj.getString("remoteapikey");
                testParam = (jobj.has("iscommit") && jobj.getBoolean("iscommit"));
                action = Integer.parseInt(request.getParameter("action"));
                switch (action) {
                    case 0://is company exist
                        result = CompanyidExits(session, request);
                        break;
                    case 1://is user exist
                        result = UserExits(session, request);
//                    result = CompanyDelete(session, request, jobj);
                        break;
                    case 2://create user
                        result = isCompanyActivated(session, request)?createUser(session, request):getMessage(2,99);
                        break;
                    case 3://create company
                        result = createCompany(session, request);
                        break;
                    case 4://delete user
                        result = isCompanyActivated(session, request)?UserDelete(session, request):getMessage(2,99);
                        break;
                    case 5://assign Role
                        result = isCompanyActivated(session, request)?assignRole(session, request):getMessage(2,99);
                        break;
                    case 6://activate user
                        result = isCompanyActivated(session, request)?activateuser(session, request):getMessage(2,99);
                        break;
                    case 7://deactivate user
                        result = isCompanyActivated(session, request)?deactivateuser(session, request):getMessage(2,99);
                        break;
                    case 8://edit company
                        result = isCompanyActivated(session, request)?updateCompany(session, request):getMessage(2,99);
                        break;
                    case 9://generate updates
                        result = isCompanyActivated(session, request)?generateUpdates(session, request):getMessage(2,99);
                        break;
                    case 10://edit user
                        result = isCompanyActivated(session, request)?editUser(session,request):getMessage(2,99);
                        break;
                    case 11://Add Goals from another apps to user
                        result = isCompanyActivated(session, request)?insertGoal(session,request):getMessage(2,99);
                        break;
                    case 12:
                        result = isCompanyActivated(session, request)?DeleteGoal(session,request):getMessage(2,99);
                        break;
                        
                    case 15:// Delete Company
                        result = deleteCompany(session,request);
                        break;
                        
                    case 16:// Deactivate Company
                        result = deactivateCompany(session,request);
                        break;
                }
                if (testParam && validkey.equals(remoteapikey)) {
                    session.getTransaction().commit();
                } else {
                    result = result.substring(0, (result.length() - 1));
                    result += ",\"action\": " + Integer.toString(action) + "}";
//                    result = "{success: true, action:" + Integer.toString(action) + ",data:" + getMessage(2, 2) + "}";
                }
            } catch (JSONException e) {
                result = getMessage(2, 2);//"{\"success\": false, \"errorcode\": \"e02\"}";
                if (testParam) {
                    result += ",\"action\": " + Integer.toString(action) + "}";
//                    result = "{success: false, action:" + Integer.toString(action) + ",data:" + getMessage(2, 2) + "}";
                }
                session.getTransaction().rollback();
            } catch (ServiceException e) {
                result = getMessage(2, 2);//"{\"success\": false, \"errorcode\": \"e02\"}";
                if (testParam) {
                    result += ",\"action\": " + Integer.toString(action) + "}";
//                    result = "{success: false, action:" + Integer.toString(action) + ",data:" + getMessage(2, 2) + "}";
                }
                session.getTransaction().rollback();
            } catch (Exception e) {
                result = getMessage(2, 2);
                System.out.println(e.getMessage());
                session.getTransaction().rollback();
            } finally {
                HibernateUtil.closeSession(session);
                out.print(result);
            }
        } else {
            out.println(getMessage(2, 1));
        }
    }

    public static String DeleteGoal(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        try {
            String jsondata = request.getParameter("data");
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                String id = jobj.getString("gid");
                Finalgoalmanagement fgmt = (Finalgoalmanagement) session.load(Finalgoalmanagement.class, id);
                if (fgmt != null) {
                    fgmt.setDeleted(true);
                    User usr = (User) session.load(User.class, jobj.getString("userid"));
                    User empusr = (User) session.load(User.class, jobj.getString("empid"));
                    if (usr != null && empusr != null) {
                        String Fullname = null,empFullname =null;
                        try {
                            Fullname = getFullName(usr);
                            empFullname = getFullName(empusr);
                        } catch (Exception ex) {
                            result = getMessage(2, 4); // user not exist
                            break;
                        }
                        String logtext = "User  " + Fullname + " has deleted " + empFullname + "'s goal " + fgmt.getGoalname();
                        session.saveOrUpdate(fgmt);
                        insertAuditLog(session, AuditAction.GOAL_DELETED, logtext, request,usr);
                        result = getMessage(1, 13); // goal delete success
                    } else {
                        result = getMessage(2, 4); // user not exist
                    }
                } else {
                    result = getMessage(2, 14);  // Goal with id not exist
                }
            }
        } catch (Exception ex) {
            result = getMessage(2, 13);
        }
        return result;
    }
  	public static String getFullName(User user) {
        String fullname = user.getFirstName();
        if(fullname!=null && user.getLastName()!=null) fullname+=" "+user.getLastName();
        if (StringUtil.isNullOrEmpty(user.getFirstName()) && StringUtil.isNullOrEmpty(user.getLastName())) {
            fullname = user.getUserLogin().getUserName();
        }
        return fullname;
	}

    public static void insertAuditLog(Session session, String actionid, String details, HttpServletRequest request,User user) throws ServiceException {
        try {
//            AuditAction action = (AuditAction) session.load(AuditAction.class, actionid);
//            insertAuditLog(session, action, details, request,user);
            String ipaddr = null;
            if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                ipaddr = request.getRemoteAddr();
            } else {
                ipaddr = request.getHeader("x-real-ip");
            }
            ProfileHandler.insertAuditLog(session, actionid, details, ipaddr, user.getUserID(), "0");
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
    /*public static void insertAuditLog(Session session, AuditAction action, String details, HttpServletRequest request,User user) throws ServiceException {
        try {
            String ipaddr = null;
            if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                ipaddr = request.getRemoteAddr();
            } else {
                ipaddr = request.getHeader("x-real-ip");
            }

//            ProfileHandler.insertAuditLog(session, action, details, ipaddr, user, "0");
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }*/
    public static String getMasterDataField(Session session, String Companyid,Integer configid) throws ServiceException {
        String result = "";
        try {
            String hql = "from MasterData where masterid.id=? and ( company is null or company.companyID=? )order by value ";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{configid,Companyid});
            Iterator ite = lst.iterator();
            if (ite.hasNext()) {
                MasterData mst = (MasterData) ite.next();
                return mst.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String insertGoal(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        try {
                Finalgoalmanagement fgmt = null;
                int logtext=0;
                String jsondata = request.getParameter("data");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
                    String id = jobj.getString("gid");
                    String companyid = jobj.getString("companyid");
                    Date startdate = new Date(jobj.getString("gstartdate"));
                    Date enddate = new Date(jobj.getString("genddate"));
                    logtext=jobj.getInt("logtext");
                    if (logtext==0){
                        fgmt = new Finalgoalmanagement();
                        fgmt.setId(id);
                        fgmt.setCreatedon(new Date());
                        fgmt.setInternal(false);
                        fgmt.setArchivedflag(0);
                        fgmt.setContext(getMasterDataField(session,companyid,2));
                        fgmt.setPriority(getMasterDataField(session,companyid,3));
                        fgmt.setGoalwth(Integer.parseInt(getMasterDataField(session,companyid,4)));
                        fgmt.setCompleted(false);
                    }else{
                        fgmt = (Finalgoalmanagement) session.load(Finalgoalmanagement.class, jobj.getString("gid"));
                        fgmt.setUpdatedon(new Date());
                    }
                    User AppraisalUser = (User) session.get(User.class,jobj.getString("userid"));
                    User usr = (User) session.get(User.class, jobj.getString("empid"));
                    if(usr!=null && AppraisalUser!=null ){
                        String Fullname = null;
                        try {
                            Fullname = getFullName(AppraisalUser);
                        } catch (Exception ex) {
                            result = getMessage(2, 4);
                            break;
//                            throw ServiceException.FAILURE("remoteapi.insertGoal", ex);
                        }
                        fgmt.setAssignedby(Fullname);
                        fgmt.setManager(AppraisalUser);
                        fgmt.setGoaldesc(jobj.getString("gdescription"));
                        fgmt.setGoalname(jobj.getString("gname"));
                        fgmt.setStartdate(startdate);
                        fgmt.setEnddate(enddate);
                        fgmt.setUserID(usr);
                        session.save(fgmt);
                        if(logtext==0) {
                            insertAuditLog(session, AuditAction.GOAL_ADDED, "User " + Fullname+ " has assigned new goal " + fgmt.getGoalname()+  " to " + getFullName(fgmt.getUserID()),request,AppraisalUser);
                        }else{
                            insertAuditLog(session, AuditAction.GOAL_EDITED, "User " + Fullname + " has updated goal " + fgmt.getGoalname()+  " for " + getFullName(fgmt.getUserID()),request,AppraisalUser);
                        }
                        result = getMessage(1, 12);
                    }else{
                        result = getMessage(2, 4);
                    }
                }
        } catch (Exception ex) {
            result = getMessage(2, 12);
            ex.printStackTrace();
//            throw ServiceException.FAILURE("remoteapi.insertGoal", ex);
        }
        return result;
    }

    private static String assignRole (Session session, HttpServletRequest request) throws  SQLException,   ServiceException {
        String result ="";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.isNull("userid")?"":jobj.getString("userid");
            String role = jobj.isNull("role")?"":jobj.getString("role");

            boolean flag=false;


            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
            } else if(jobj.has("username")) {
                userid = jobj.getString("username");
                flag = true;
            }else{
               return getMessage(2,1);
            }

            if(StringUtil.isNullOrEmpty(role) || StringUtil.isNullOrEmpty(userid)) {
                return getMessage(2,1);
            }

            String query = "";
            String msgStr = "";

    if(flag) {
            String query1="from UserLogin where userName=?";
            List list1 = HibernateUtil.executeQuery(session, query1, userid);
            Iterator itr1 = list1.iterator();
            if(itr1.hasNext()) {
                UserLogin user=(UserLogin) itr1.next();
                userid=user.getUserID();
                if(StringUtil.isNullOrEmpty(userid)){
                    return getMessage(2,6);
                }
             }
            }
            query="from User u where u.userID=?";
            List list = HibernateUtil.executeQuery(session, query, userid);
            int count = list.size();
            if(count > 0) {
                User userLogin = (User) session.load(User.class,userid);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, userid);
                    if (StringUtil.equal(role, "h1")) {
                        role = "1";
                    } else if (StringUtil.equal(role, "h2")) {
                        role = "2";
                    } else if (StringUtil.equal(role, "h3")) {
                        role = "3";
                    } else if (StringUtil.equal(role, "h4")) {
                        role = "4";
                    } else if (StringUtil.equal(role, "h0")) {
                        replaceCompanyCreator(userLogin, userLogin.getCompany().getCompanyID(),  session);
                        role = "1";
                    }
//                String permission = "from Role where name=?";
//                String roleStr = role.equals("h1")?"Administration":(role.equals("h2")?"Manager":"Employee");
//                List perm=HibernateUtil.executeQuery(session, permission,roleStr);
                Role r=(Role) session.load(Role.class, role);
                 ua.setRole(r);
                 userLogin.setDeleteflag(0);
                 session.update(userLogin);
                result = getMessage(1,8);
            } else {
                result = getMessage(2,6);
            }
        } catch (Exception e) {
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("comapanyServlet.CompanyidExits:"+e.getMessage(), e);
        }
        return result;
    }

    private static String createUser (Session session, HttpServletRequest request) throws  SQLException,   ServiceException {
        String retStr = "";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String id = "";String pwdText = "";
            String companyid = jobj.isNull("companyid")?"" : jobj.getString("companyid");
            String username = jobj.isNull("username")?"":jobj.getString("username");
            String pwd = jobj.isNull("password")?"":jobj.getString("password");
            String fname = jobj.isNull("fname")?"":jobj.getString("fname");
            String lname = jobj.isNull("lname")?"":jobj.getString("lname");
            String emailid = jobj.isNull("emailid")?"":jobj.getString("emailid");
            String userid = jobj.isNull("userid")?"":jobj.getString("userid");
            String subdomain = jobj.isNull("subdomain")?"":jobj.getString("subdomain");

            if(StringUtil.isNullOrEmpty(companyid) || StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(fname) ||
                    StringUtil.isNullOrEmpty(lname) || StringUtil.isNullOrEmpty(emailid) ||StringUtil.isNullOrEmpty(userid)
                    ) {
                return getMessage(2,1);
            }

            String hql ="from Company where companyID=?";
            List ls = HibernateUtil.executeQuery(session, hql, new Object[]{companyid});
            if(ls.size()==0) {
                return getMessage(2,8);
            }
            UserLogin userLogin;
            User user;
            Useraccount ua;
            Empprofile empProfile = new Empprofile();

            if(StringUtil.isNullOrEmpty(id)==false) {
                user = (User)session.load(User.class, id);
                ua = (Useraccount) session.load(Useraccount.class, id);
                userLogin=user.getUserLogin();
                String pass=jobj.getString("password");
                String oldpass=jobj.getString("oldpassword");
                if(StringUtil.isNullOrEmpty(oldpass)==false){
                    if(AuthHandler.getSHA1(oldpass).equals(userLogin.getPassword())){
                        if(StringUtil.isNullOrEmpty(pass)==false)
                            userLogin.setPassword(AuthHandler.getSHA1(pass));
                    }else
                        throw new Exception("Old Password does not match");
                }

            } else {
                 hql ="from UserLogin u where u.userID=?";
              ls = null;
              ls = HibernateUtil.executeQuery(session, hql, new Object[]{userid});
              if(ls.size()>0) {
                return getMessage(2,7);
             }else{
                user = new User();
                ua = new Useraccount();
                userLogin=new UserLogin();
                
                userLogin.setUserID(userid);
                ua.setUserID(userid);
//                user.setUserID(userid);
                user.setUserLogin(userLogin);
                //userLogin.setUser(user);
                String q="from User where userLogin.userName=? and company.companyID=?";// and company.companyID=?";
                ls = null;
                ls = HibernateUtil.executeQuery(session, q, new Object[]{username,companyid});
                if(ls.size()>0&&username.equals(userLogin.getUserName())==false) {
                    return getMessage(2,3);
                }
                userLogin.setUserName(username);
                if(jobj.isNull("password")) {
                   pwdText = AuthHandler.generateNewPassword();
                   pwd = AuthHandler.getSHA1(pwdText);
                }
                userLogin.setPassword(pwd);
                user.setCompany((Company)session.load(Company.class,companyid));

                empProfile = setEmployeeProfileDefault(userid , empProfile, userLogin);
             }

            }
            user.setDateFormat((KWLDateFormat)session.load(KWLDateFormat.class,"18"));
            user.setFirstName(fname);
            user.setLastName(lname);
            user.setEmailID(emailid);
            ua.setUser(user);
            ua.setSalary("0");
            String role = jobj.isNull("role")?"h3":jobj.getString("role");
            if (StringUtil.equal(role, "h1") || StringUtil.equal(role, "h0")) {
                role = "1";
            } else if (StringUtil.equal(role, "h2")) {
                role = "2";
            } else if (StringUtil.equal(role, "h3")) {
                role = "3";
            }
            ua.setRole((Role)session.load(Role.class,role));
            if(jobj.has("accno") && StringUtil.isNullOrEmpty(jobj.getString("accno"))==false) {
                if((jobj.getString("accno")).length()>0){
                   ua.setAccno(jobj.getString("accno"));
                }else{
                    ua.setAccno("0");
                }
            }

            ua.setTemplateid("0");
            int empid=getMaxCountEmpid(session,companyid);
            ua.setEmployeeid(empid);
            if(jobj.has("formatid") && StringUtil.isNullOrEmpty(jobj.getString("formatid"))==false) {
                user.setDateFormat((KWLDateFormat)session.load(KWLDateFormat.class, jobj.getString("formatid")));
            }

            String diff=null;
            if(jobj.has("tzid") && StringUtil.isNullOrEmpty(jobj.getString("tzid"))==false) {
                KWLTimeZone timeZone=(KWLTimeZone)session.load(KWLTimeZone.class, jobj.getString("tzid"));
                diff=timeZone.getDifference();
                user.setTimeZone(timeZone);
            }

            if(jobj.has("aboutuser") && StringUtil.isNullOrEmpty(jobj.getString("aboutuser"))==false) {
                user.setAboutUser(jobj.getString("aboutuser"));
            }

            session.saveOrUpdate(userLogin);
            session.saveOrUpdate(user);
            session.save(ua);
            session.save(empProfile);

            updatePreferences(request, null, (jobj.has("formatid")?jobj.getString("formatid"):null), (jobj.has("tzid")?jobj.getString("tzid"):null),diff);                
            retStr = getMessage(1, 5);
        } catch (Exception e) {
            e.printStackTrace();
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return retStr;
    }
    
     private static void  replaceCompanyCreator(User user, String companyid, Session session) throws ServiceException {
        
        try {
            Company company = (Company)session.load(Company.class, companyid);
            company.setCreator(user);
            session.save(company);

        } catch (Exception e) {
            Logger.getLogger(remoteapi.class.getName()).log(Level.SEVERE,"replaceCompanyCreator : Exception while replaceing company creator",e);
        }
        
    }

    public static Empprofile setEmployeeProfileDefault(String userid, Empprofile empProfile, UserLogin userLogin) throws ServiceException {
        
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            empProfile.setUserID(userid);
            empProfile.setGender("");
            empProfile.setMarriage("");
            empProfile.setBloodgrp("");
            empProfile.setFathername("");
            empProfile.setMothername("");
            empProfile.setBankname("");
            empProfile.setBankbranch("");
            empProfile.setPanno("");
            empProfile.setPfno("");
            empProfile.setDrvlicense("");
            empProfile.setPassportno("");
            empProfile.setMiddlename("");
            empProfile.setKeyskills("");
            empProfile.setUpdated_by(userid);
            empProfile.setUpdated_on(fmt.parse(fmt.format(new Date())));
            empProfile.setStatus("Approved");
            empProfile.setTermnd(false);

            empProfile.setUserLogin(userLogin);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return empProfile;
    }

    static void updatePreferences(HttpServletRequest request, String currencyid, String dateformatid, String timezoneid,String tzdiff) {
        if(currencyid!=null)request.getSession().setAttribute("currencyid", currencyid);
        if(timezoneid!=null){
            request.getSession().setAttribute("timezoneid", timezoneid);
            request.getSession().setAttribute("tzdiff", tzdiff);
        }
        if(dateformatid!=null)request.getSession().setAttribute("dateformatid", dateformatid);
    }

    private static String createCompany (Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid2 = jobj.isNull("username")?"":jobj.getString("username");
            String emailid2 = jobj.isNull("emailid")?"":jobj.getString("emailid");
            String password = jobj.isNull("password")?"":jobj.getString("password");
            String companyname = jobj.isNull("companyname")?"":jobj.getString("companyname");
            String companyid = jobj.isNull("companyid")?"":jobj.getString("companyid");
            String creatorid = jobj.isNull("userid")?"":jobj.getString("userid");
            String subdomain = jobj.isNull("subdomain")?"":jobj.getString("subdomain");
            String fname = jobj.isNull("fname")?"":jobj.getString("fname");
            String lname = jobj.isNull("lname")?"":jobj.getString("lname");
            if(StringUtil.isNullOrEmpty(companyname) || StringUtil.isNullOrEmpty(userid2) || StringUtil.isNullOrEmpty(creatorid)||
                    StringUtil.isNullOrEmpty(fname) || StringUtil.isNullOrEmpty(emailid2)||StringUtil.isNullOrEmpty(companyid)||
                    StringUtil.isNullOrEmpty(subdomain)) {
                return getMessage(2,1);
            }
            String pwdtext = "";
            if(jobj.isNull("password")) {
               pwdtext = AuthHandler.generateNewPassword();
               password = AuthHandler.getSHA1(pwdtext);
            }
            if (!(StringUtil.isNullOrEmpty(userid2) || StringUtil.isNullOrEmpty(emailid2))) {
                emailid2 = emailid2.replace(" ", "+");
                result = signupCompany(session, request, userid2,password, emailid2, companyname,
                    fname,jobj,companyid,creatorid,subdomain,lname);
                if (jobj.has("sendmail") && jobj.getBoolean("sendmail") && result.equals("success")) {
                    try {
                        String passwordString="";
                        if(jobj.isNull("password")) {
                            passwordString= String.format("		<p>Username: <strong>%s</strong> </p>"
                                + "               <p>Password: <strong>%s</strong></p>",userid2,pwdtext);
                        }
                        String uri = URLUtil.getPageURL(request, Links.loginpageFull);//URLUtil.getDomainURL("");
                        String pmsg = String.format("Hi %s,\n\nWelcome to Deskera HRMS and thanks for signing up!\n\n\n" +
                            "Bookmark your login page - %s\n\nThis is the address where you'll log in to your account for now on\n\n - " +
                            "The Deskera HRMS Team\n", userid2, uri, userid2);
                        String htmlmsg = String.format("<html><head><title>Deskera HRMS  Your Deskera HRMS Account</title></head>" +
                            "<style type='text/css'>a:link, a:visited, a:active {color: #03C;}" +
                            "body {font-family: Arial, Helvetica, sans-serif;color: #000;font-size: 13px;}" +
                            "</style><body><div><p>Hi <strong>%s</strong>,</p>" +
                            "<p>Welcome to Deskera HRMS and thanks for signing up!</p>" +
                            "<p>Access your Deskera HRMS account at: <a href='%s'>%s</a>" +
                            passwordString+
                            "<p>Read more about deskera:&nbsp;&nbsp;&nbsp;<a href='http://blog.deskera.com/'>Click here</a></p><br/><br/>" +
                            "<p>See you on Deskera HRMS!</p><p> - The Deskera HRMS Team</p>" +
                            "</div></body></html>", userid2, uri, uri, userid2);
                        try {
                            SendMailHandler.postMail(new String[] { emailid2 },"[Deskera] Welcome to Deskera HRMS",htmlmsg, pmsg, "admin@deskera.com");
                        } catch (MessagingException mE) {
                            result = getMessage(2, 5);
                            Logger.getLogger(remoteapi.class.getName()).log(Level.SEVERE,"Message Exception While Email User Info",mE);
                        }
                    //    uri += (Links.loginpage+"?first");
                     //   result = "{\"success\":true,\"uri\":\"" + uri + "\"}";
                    } catch (Exception ex) {
                        System.out.print(ex.getMessage());
                        result = getMessage(2, 5);
                    }
                }
                if(result.equals("success")) {
                    result = getMessage(1, 6);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Create company with passed company name and company created user
     * @param session A current transaction session
     * @param request A HttpServletRequest
     * @param id Unique username
     * @param password User password
     * @param emailid User emailid
     * @param fname User's name
     * @return JSON with success/failure response.
     * @throws ServiceException
     */
    private static String signupCompany(Session session, HttpServletRequest request, String id, String password, String emailid, String companyname,
            String fname,JSONObject jobj,String companyid,String creatorid,String subdomain, String lname) throws ServiceException {
        String result = "failure";
        int modules=0;
        try {
            Company company=null;
              String query1="from Company c where c.subDomain= ?";
            List list1 = HibernateUtil.executeQuery(session, query1, subdomain);
            Iterator itr1 = list1.iterator();
            if(itr1.hasNext()) {
                // rename company's invalid subdomain
                Company oldcompany = (Company) itr1.next();
                oldcompany.setSubDomain("old_"+oldcompany.getSubDomain());
                session.saveOrUpdate(oldcompany);

//                return getMessage(2, 8);//result="e11";
            }
            else{
                query1="from UserLogin u where u.userID=?";
                list1 = HibernateUtil.executeQuery(session, query1, creatorid);
                itr1 = list1.iterator();
                  if(itr1.hasNext()) {
                    return getMessage(2,7);
                }
                 query1="from Company c where c.companyID= ?";
                 list1 = HibernateUtil.executeQuery(session, query1, companyid);
                 itr1 = list1.iterator();
                if(itr1.hasNext()) {
                    return getMessage(2, 8);//result="e11";
                }
                company=new Company();
                company.setCompanyID(companyid);
                company.setSubDomain(subdomain);
                company.setAddress("");
                company.setCompanyName(companyname);
                company.setCountry((Country)session.get(Country.class,"244"));
                company.setTimeZone((KWLTimeZone)session.get(KWLTimeZone.class,"23"));
                company.setEmailID(emailid);
                company.setCurrency((KWLCurrency)session.get(KWLCurrency.class,"1"));
                company.setDeleted(0);
                company.setCreatedOn(new Date());
                company.setModifiedOn(new Date());
                session.save(company);                

                UserLogin userLogin = new UserLogin();
                User user = new User();
                Useraccount ua = new Useraccount();
                Empprofile empProfile = new Empprofile();

                ua.setUserID(creatorid);
                userLogin.setUserID(creatorid);               
                user.setUserLogin(userLogin);                
                user.setDateFormat((KWLDateFormat)session.load(KWLDateFormat.class,"18"));
                userLogin.setUserName(id);
                userLogin.setPassword(password);
                user.setFirstName(fname);
                user.setLastName(lname);
                user.setEmailID(emailid);
                ua.setUser(user);
                ua.setEmployeeid(0001);
                user.setCompany(company);
                ua.setRole((Role) session.load(Role.class,Role.COMPANY_ADMIN));
                ua.setSalary("10000");
                ua.setAccno("");
                user.setDesignation("admin");
                ua.setTemplateid("0");

                empProfile = setEmployeeProfileDefault(creatorid , empProfile, userLogin);

                session.save(userLogin);
                session.save(user);
                session.save(ua);
                session.save(empProfile);

                company.setCreator(user);
                session.save(company);
                CompanyPreferences cmpPref=new CompanyPreferences();
                cmpPref.setCompany(company);
                cmpPref.setAnnmanager(true);
                cmpPref.setTimesheetjob(true);
                modules=getmodules(session);
                cmpPref.setSubscriptionCode(modules);
                cmpPref.setSelfappraisal(true);
                cmpPref.setCompetency(true);
                cmpPref.setGoal(true);
                cmpPref.setApproveappraisal(true);
                cmpPref.setReviewappraisal(true);
                cmpPref.setFullupdates(true);
                cmpPref.setWeightage(true);                
                session.save(cmpPref);
                createRecruitmentFormFields(session,request,company);
                createPayrollFields(session, company);
                
                result="success";
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("CompanyHandler.Signup Company", e);
        }
        return result;
    }
    private static int createRecruitmentFormFields(Session session, HttpServletRequest request,Company company) throws SQLException, ServiceException {
        String companyid = company.getCompanyID();
        String titleid= UUID.randomUUID().toString();
        String countryid= UUID.randomUUID().toString();
        String preferedid= UUID.randomUUID().toString();
        
        String configrecruitmentsql = "INSERT INTO `configrecruitment` (`configid`,`configtype`,`formtype`,`position`,`name`,`Colnum`,`deleteflag`,`issystemproperty`,`allownull`,`visible`,  `company`) VALUES ";
        String configrecruitmentsql1 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',0,'Personal',3,'Last Name',2,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Personal',2,'First Name',1,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',7,'Personal',5,'Email ID',3,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Contact',1,'Contact No',4,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',5,'other',3,'Resume',5,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',4,'Contact',6,'Address',6,'F','T','F','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Personal',4,'Date of Birth',7,'F','T','T','T','"+companyid+"') ";

        String configrecruitmentsql2 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',2,'Academic',6,'Graduation Passing Date',10,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Personal',6,'Applied Date',9,'F','T','T','F','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',1,'Current Organization',11,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Academic',5,'Graduation Percentage',8,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',7,'Personal',7,'Other Email Id',13,'F','F','T','T','"+companyid+"') ";
        String configrecruitmentsql3 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',6,'Contact',2,'Alternative Contact No.',14,'F','T','T','T','"+companyid+"'),('"+countryid+"',3,'Contact',3,'Country',15,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Contact',4,'State',16,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Contact',5,'City',17,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',7,'Post Graduation Degree',12,'F','T','T','T','"+companyid+"') ";
        String configrecruitmentsql4 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',0,'Academic',1,'Graduation Degree',18,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',2,'Graduation Specialization',19,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',3,'Graduation University',20,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',4,'Graduation College',21,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',8,'Post Graduation Specialization',22,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',9,'Post Graduation University',23,'F','T','T','T','"+companyid+"')";
        String configrecruitmentsql5 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',0,'Academic',10,'Post Graduation College',24,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Academic',11,'Post Graduation Percentage',25,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Academic',12,'Post Graduation Passing Date',26,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',2,'Current Designation',27,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',3,'Current Industry Type',28,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Work',4,'Experience',29,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',4,'Work',5,'Functional Expertise',30,'F','T','T','T','"+companyid+"') ";
        String configrecruitmentsql6 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',6,'Work',6,'Gross CTC',31,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',6,'Work',7,'Expected CTC',32,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',4,'other',1,'Key Skills',33,'F','T','T','T','"+companyid+"'),('"+preferedid+"',3,'other',2,'Preferred Interview Location',34,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'other',4,'Mother\\'s Maiden Name',35,'F','T','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',14,'Other Degree Details ',37,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',0,'Academic',13,'Other Degree',36,'F','F','T','T','"+companyid+"') " ;
        String configrecruitmentsql7 = configrecruitmentsql + " ('"+UUID.randomUUID().toString()+"',6,'Academic',15,'Other Percentage ',38,'F','F','T','T','"+companyid+"'),('"+UUID.randomUUID().toString()+"',2,'Academic',16,'Other Passing Date ',39,'F','F','T','T','"+companyid+"'),('"+titleid+"',3,'Personal',1,'Title',40,'F','T','F','T','"+companyid+"') ";
        
        SQLQuery sql1 = session.createSQLQuery(configrecruitmentsql1);
        SQLQuery sql2 = session.createSQLQuery(configrecruitmentsql2);
        SQLQuery sql3 = session.createSQLQuery(configrecruitmentsql3);
        SQLQuery sql4 = session.createSQLQuery(configrecruitmentsql4);
        SQLQuery sql5 = session.createSQLQuery(configrecruitmentsql5);
        SQLQuery sql6 = session.createSQLQuery(configrecruitmentsql6);
        SQLQuery sql7 = session.createSQLQuery(configrecruitmentsql7);
        
        int createdefaultcompanyconfigsqlcount1= sql1.executeUpdate();
        int createdefaultcompanyconfigsqlcount2= sql2.executeUpdate();
        int createdefaultcompanyconfigsqlcount3= sql3.executeUpdate();
        int createdefaultcompanyconfigsqlcount4= sql4.executeUpdate();
        int createdefaultcompanyconfigsqlcount5= sql5.executeUpdate();
        int createdefaultcompanyconfigsqlcount6= sql6.executeUpdate();
        int createdefaultcompanyconfigsqlcount7= sql7.executeUpdate();

        String configrecruitmentmastersql = "INSERT INTO `configrecruitmentmaster` (masterid,masterdata,configid)  ";

        String configrecruitmentmastersql1 = configrecruitmentmastersql + " VALUES ('"+UUID.randomUUID().toString()+"','Mr','"+titleid+"'),('"+UUID.randomUUID().toString()+"','Mrs','"+titleid+"'),('"+UUID.randomUUID().toString()+"','Ms','"+titleid+"') ";
        String configrecruitmentmastersql2 = configrecruitmentmastersql + " select uuid(),value,'"+countryid+"' from MasterData where masterid=11 " ;
//      String configrecruitmentmastersql3 = configrecruitmentmastersql + " select uuid(),value,'"+preferedid+"' from MasterData where masterid=18 " ;

        SQLQuery sql8 = session.createSQLQuery(configrecruitmentmastersql1);
        SQLQuery sql9 = session.createSQLQuery(configrecruitmentmastersql2);
//      SQLQuery sql10 = session.createSQLQuery(configrecruitmentmastersql3);

        int configrecruitmentmastersqlcount8= sql8.executeUpdate();
        int configrecruitmentmastersqlcount9= sql9.executeUpdate();
//      int configrecruitmentmastersqlcount10= sql10.executeUpdate();

        System.out.println("<br> Count for executed Querys ");
        System.out.println("configrecruitment1 :"+createdefaultcompanyconfigsqlcount1);
        System.out.println("configrecruitment2 :"+createdefaultcompanyconfigsqlcount2);
        System.out.println("configrecruitment3 :"+createdefaultcompanyconfigsqlcount3);
        System.out.println("configrecruitment4 :"+createdefaultcompanyconfigsqlcount4);
        System.out.println("configrecruitment5 :"+createdefaultcompanyconfigsqlcount5);
        System.out.println("configrecruitment6 :"+createdefaultcompanyconfigsqlcount6);
        System.out.println("configrecruitment7:"+createdefaultcompanyconfigsqlcount7);

        System.out.println("configrecruitmentmastersqlcount8 :"+configrecruitmentmastersqlcount8);
        System.out.println("configrecruitmentmastersqlcount9 :"+configrecruitmentmastersqlcount9);
//      System.out.println("configrecruitmentmastersqlcount10 :"+configrecruitmentmastersqlcount10);

        return createdefaultcompanyconfigsqlcount7;
    }
    
    private static boolean createPayrollFields(Session session, Company company) {
    	boolean sucess = false;
    	
        try{
        	/*Create Default Cost-Center component*/
            CostCenter costCenter = new CostCenter();
            costCenter.setName("Default");
            costCenter.setCode("Default");
            costCenter.setCompany(company);
            costCenter.setCreationDate(null);
            session.save(costCenter);
        	
            /*Create Default component sub-type components*/
            Master master = (Master) session.get(Master.class, 21);
            int [] componenttype = {0, 1, 2, 3, 4, 5};
            String [] value = {"Employer Contribution", "Earning", "Deduction", "Tax", "Additional Remuneration", "Income Tax"};
            for(int i=0; i< componenttype.length; i++){
            	MasterData data = new MasterData();
            	data.setMasterid(master);
            	data.setValue(value[i]);
            	data.setCompany(company);
            	data.setWeightage(i);
            	data.setComponenttype(componenttype[i]);
            	data.setWorktime(null);
            	session.save(data);
            }
            
            sucess = true;
        }catch(Exception e){
        	sucess = false;
        	e.printStackTrace();
        }
        
        return sucess;
    }
    
    private static String CompanyidExits(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String companyid = "";
            if(!jobj.isNull("companyid")) {
                companyid = jobj.getString("companyid");
            } else {
                return getMessage(2,1);
            }
            String query="from Company c where c.companyID= ?";
            List list = HibernateUtil.executeQuery(session, query, companyid);
            int count = list.size();
            if(count > 0) {
                result = getMessage(1,1);
            } else {
                result = getMessage(1,2);
            }
        } catch (Exception e) {
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("comapanyServlet.CompanyidExits:"+e.getMessage(), e);
        }
        return result;
    }

    private static String CompanyDelete(Session session, HttpServletRequest request, JSONObject jobj) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            String companyid = "";
            if(!jobj.isNull("companyid")) {
                companyid = jobj.getString("companyid");
            } else {
                return getMessage(2,1);
            }
            String query="from Company c where c.companyID= ? and deleted = ?";
            List list = HibernateUtil.executeQuery(session, query, new Object[]{companyid,false});
            int count = list.size();
            if(count > 0) {
                Company c = (Company) session.load(Company.class, companyid);
                c.setDeleted(1);
                result = "{\"success\":true, 'msg': 'Company deleted successfully.'}";
            } else {
                result = getMessage(2,4);
            }
        } catch (Exception e) {
            result = "{\"success\":false, 'errormsg': 'Following error occured while deleting company : '"+e.getMessage()+"}";
            throw ServiceException.FAILURE("comapanyServlet.CompanyDelete:"+e.getMessage(), e);
        }
        return result;
    }

    private static String UserExits(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            boolean flag = false;
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
            } else if(jobj.has("username")) {
                userid = jobj.getString("username");
                flag = true;
            }
            if(StringUtil.isNullOrEmpty(userid)){
                return getMessage(2,1);
            }
            String query = "";
            if(!flag) {
                query="from UserLogin u where u.userID=?";
            } else {
                query="from UserLogin u where u.userName=?";
            }
            List list = HibernateUtil.executeQuery(session, query, userid);
            int count = list.size();
            if(count > 0) {
                result = getMessage(1,3);
            } else {
                result = getMessage(1,4);
            }
        } catch (Exception e) {
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("comapanyServlet.UserExits", e);
        }
        return result;
    }

    private static String UserDelete(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            boolean flag = false;
           if (jobj.has("userid")) {
                userid = jobj.getString("userid");
                String[] ids = userid.split(",");
                for (int i = 0; i < ids.length; i++) {
                    User u = (User) session.get(User.class, ids[i]);
                    UserLogin ul=(UserLogin)session.get(UserLogin.class, ids[i]);
                    if (u != null) {
                        ul.setUserName(ul.getUserName()+"_del");
                        u.setDeleteflag(1);
                        session.saveOrUpdate(ul);
                        session.save(u);
                        result = getMessage(1, 7);
                    }

                    AssignParentToChildOfDeletedUser(ids[i], session);
                }

            } else{
                   return getMessage(2, 1);
            }
        } catch (Exception e) {
            result = "{\"success\":false, 'errormsg': 'Following error occured while deleting user : '"+e.getMessage()+"}";
            throw ServiceException.FAILURE("comapanyServlet.CompanyUserDelete:"+e.getMessage(), e);
        }
        return result;
    }

    private static boolean AssignParentToChildOfDeletedUser(String deletedUserId, Session session) throws SQLException, ServiceException {
        boolean success = false;
        try {

              User parentNode = null;
              String getEmpProfile = "from Empprofile where userID = ? ";

              List<Empprofile> parentList = HibernateUtil.executeQuery(session, getEmpProfile, new Object[]{deletedUserId});

              for(Empprofile empParent : parentList){
                  if(empParent !=null){
                      parentNode = empParent.getReportto();
                      empParent.setReportto(null);
                      session.save(empParent);
                  }
              }

              String getChild = "from Empprofile a where a.reportto.userID = ? ";
              List<Empprofile> childList = HibernateUtil.executeQuery(session, getChild, new Object[]{deletedUserId});

              for(int i=0; i< childList.size();i++){

                  Empprofile emp = (Empprofile)childList.get(i);
                  emp.setReportto(parentNode);
                  session.save(emp);
              }

              success =true;
            
        } catch (Exception e) {
            success = false;
            throw ServiceException.FAILURE("comapanyServlet.AssignParentToChildOfDeletedUser:"+e.getMessage(), e);
        }
        return success;
    }
    
    private static String deactivateuser(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            boolean flag = false;
             if (jobj.has("userid")) {
                userid = jobj.getString("userid");
                String[] ids = userid.split(",");
                for (int i = 0; i < ids.length; i++) {
                    User u = (User) session.get(User.class, ids[i]);
                    if (u != null) {
                        u.setDeleteflag(1);
                        session.save(u);
                        result = getMessage(1, 10);
                    }
                }

            } else{
                   return getMessage(2, 1);
            }
        } catch (Exception e) {
            result = "{\"success\":false, 'errormsg': 'Following error occured while deactivating user : '"+e.getMessage()+"}";
            throw ServiceException.FAILURE("comapanyServlet.CompanyDeactivateuser:"+e.getMessage(), e);
        }
        return result;
    }
    private static String activateuser(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            boolean flag = false;
            if (jobj.has("userid")) {
                userid = jobj.getString("userid");
                String[] ids = userid.split(",");
                for (int i = 0; i < ids.length; i++) {
                    User u = (User) session.get(User.class, ids[i]);
                    if (u != null) {
                        u.setDeleteflag(0);
                        session.save(u);
                        result = getMessage(1, 9);
                    }
                }

            } else{
                   return getMessage(2, 1);
            }

        } catch (Exception e) {
            result = "{\"success\":false, 'errormsg': 'Following error occured while activating user : '" + e.getMessage() + "}";
            throw ServiceException.FAILURE("comapanyServlet.CompanyActivateuser:" + e.getMessage(), e);
        }
        return result;
    }

        private static String updateCompany(Session session,HttpServletRequest request) throws ServiceException, JSONException,SessionExpiredException {
            String result = "";
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String address = jobj.isNull("address") ? "" : jobj.getString("address");
            String city = jobj.isNull("city") ? "" : jobj.getString("city");
            String state = jobj.isNull("state") ? "" : jobj.getString("state");
            String companyname = jobj.isNull("companyname") ? "" : jobj.getString("companyname");
            String companyid = jobj.isNull("companyid") ? "" : jobj.getString("companyid");
            String phone = jobj.isNull("phone") ? "" : jobj.getString("phone");
            String subdomain = jobj.isNull("subdomain") ? "" : jobj.getString("subdomain");
            String fax = jobj.isNull("fax") ? "" : jobj.getString("fax");
            String zip = jobj.isNull("zip") ? "" : jobj.getString("zip");
            String website = jobj.isNull("website") ? "" : jobj.getString("website");
            String emailid = jobj.isNull("emailid") ? "" : jobj.getString("emailid");
            String currency = jobj.isNull("currency") ? "" : jobj.getString("currency");
            String country = jobj.isNull("country") ? "" : jobj.getString("country");
            String timezone = jobj.isNull("timezone") ? "" : jobj.getString("timezone");
            String imgPath = jobj.isNull("image") ? "" : jobj.getString("image");
            if (StringUtil.isNullOrEmpty(companyid) || StringUtil.isNullOrEmpty(subdomain) || StringUtil.isNullOrEmpty(companyname) || StringUtil.isNullOrEmpty(currency) || StringUtil.isNullOrEmpty(country) || StringUtil.isNullOrEmpty(timezone)) {
                return getMessage(2, 1);
            }
            try {
                Company company = null;
                String query1 = "from Company c where c.companyID= ?";
                List list1 = HibernateUtil.executeQuery(session, query1, companyid);
                Iterator itr1 = list1.iterator();
                if (!(itr1.hasNext())) {
                    return getMessage(2, 4);
                } else {
                    query1 = "from Company c where c.subDomain= ? and c.companyID <> ?";
                    list1 = HibernateUtil.executeQuery(session, query1, new Object[]{ subdomain,companyid});
                    itr1 = list1.iterator();
                    if (itr1.hasNext()) {
                        return getMessage(2, 10);
                    }
                    company = (Company) session.load(Company.class, companyid);
                    company.setSubDomain(subdomain);
                    company.setCompanyName(companyname);
                    company.setAddress(address);
                    company.setCity(city);
                    company.setState(state);
                    company.setPhoneNumber(phone);
                    company.setFaxNumber(fax);
                    company.setZipCode(zip);
                    company.setWebsite(website);
                    company.setEmailID(emailid);
                    company.setCompanyLogo(imgPath);
                    company.setCountry((Country) session.load(Country.class, country));
                    company.setCurrency((KWLCurrency) session.load(KWLCurrency.class, currency));
                    KWLTimeZone timeZone = (KWLTimeZone) session.load(KWLTimeZone.class, timezone);
                    company.setTimeZone(timeZone);
                    company.setModifiedOn(new Date());
                    session.saveOrUpdate(company);
                    result = getMessage(1, 11);
                }
            } catch (NullPointerException e) {
                throw ServiceException.FAILURE("CompanyHandler.updateCompany", e);
            }
            return result;
    }
    public static String getContentSpan(String textStr) {
        String span = "<div style='padding:0 0 5px 0;border-bottom:solid 1px #EEEEEE;'>" + textStr + "<div style='clear:both;visibility:hidden;height:0;line-height:0;'></div></div>";
        return span;
    }
    private static int getmodules(Session session) throws ServiceException{
     int moduleSum=0;
     String hql="from hrms_Modules" ;
     List lst=HibernateUtil.executeQuery(session, hql);
     Iterator ite=lst.iterator();
     while(ite.hasNext()){
         hrms_Modules hrm=(hrms_Modules) ite.next();
         moduleSum += (int) Math.pow(2, Double.parseDouble(hrm.getModuleID()));
     }
     return moduleSum;
    }
     public static Integer getMaxCountEmpid(Session session,String cmpid ) throws ServiceException {
        Integer maxcount = 0;
        try {
            String SELECT_USER_INFO1 = "select max(employeeid) from Useraccount where user.company.companyID=?";
            List list1 = HibernateUtil.executeQuery(session, SELECT_USER_INFO1, cmpid);
            Iterator itr1 = list1.iterator();
            while (itr1.hasNext()) {
                maxcount = ((Integer) itr1.next()) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxcount;
    }
    private static String generateUpdates(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException, ParseException {
        String result = "";
        int goallimit = 5;
        List tabledata = null;
        int count = 0;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String goaldate = "";
        int days;
        int diff;
        JSONObject allUpdates = new JSONObject();
        JSONObject update = new JSONObject();
        JSONObject finalJson = new JSONObject();
        JSONObject jobj = new JSONObject(request.getParameter("data"));
        String empid = jobj.isNull("userid") ? "" : jobj.getString("userid");
        String companyid = jobj.isNull("companyid") ? "" : jobj.getString("companyid");
        String offset = jobj.isNull("offset") ? "" : jobj.getString("offset");
        String limit = jobj.isNull("limit") ? "" : jobj.getString("limit");
        // String role = jobj.isNull("role")?"":jobj.getString("role");
        if (StringUtil.isNullOrEmpty(companyid) || StringUtil.isNullOrEmpty(empid) || StringUtil.isNullOrEmpty(offset) || StringUtil.isNullOrEmpty(limit)) {
            return getMessage(2, 1);
        }
        update.put("head", "<div style='padding:10px 0 10px 0;font-size:13px;font-weight:bold;color:#10559a;border-bottom:solid 1px #EEEEEE;'>Updates</div>");
        finalJson.append("data", update);
        try {
            User u = (User) session.get(User.class, empid);
            Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
            String role = ua.getRole().getID();
            if (StringUtil.equal(role, Role.COMPANY_USER)) {
                String qry = "from Appraisalmanagement where employee.userID=? and employeestatus=0 and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
                Object[] obj = {empid};
                tabledata = HibernateUtil.executeQuery(session, qry, obj);
                if (!tabledata.isEmpty()) {
                    Appraisalmanagement app = (Appraisalmanagement) tabledata.get(0);
                    Date now = new Date();
                    if (app.getAppcycle().getSubmitenddate().getDate() >= now.getDate() && app.getAppcycle().getSubmitstartdate().getDate() <= now.getDate()) {
                        count = tabledata.size();
                        if (count > 0) {
                            diff = (int) ((app.getAppcycle().getSubmitenddate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                            result = "Fill the appraisal form in <font color='green'> " + diff + "</font> day(s)";
                            update = new JSONObject();
                            update.put("update", "" + getContentSpan(result) + "");
                            allUpdates.append("data", update);
                        }
                    }
                }
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Calendar cal = Calendar.getInstance();
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DATE, -weekday + 1);
            Date sdate = cal.getTime();
            cal.add(Calendar.DATE, 6);
            Date edate = cal.getTime();

            String timesheet1 = "from Timesheet where datevalue between ? and ? and  userID.company.companyID=? and userID.userID=? group by approved";
            List timesheet = HibernateUtil.executeQuery(session, timesheet1, new Object[]{sdate, edate, companyid, empid});
            Timesheet tmst = null;
            update=new JSONObject();
            if (!timesheet.isEmpty()) {
                tmst = (Timesheet) timesheet.get(0);
                if (tmst.getApproved() == 0) {
                    result = "Your Timesheet from <font color='green'>" + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> is <font color='green'>Pending</font> ";
                    update.put("update",""+getContentSpan(result)+"");
                } else {
                    result = "Your Timesheet from <font color='green'>" + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> has been <font color='green'>Approved</font> ";
                    update.put("update",""+getContentSpan(result)+"");
                }
            } else {
                result = "Please Fill Timesheet  from<font color='green'> " + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font>";
                update.put("update",""+getContentSpan(result)+"");
            }
              allUpdates.append("data",update);
            String hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? order by createdon desc";
            List goallst = HibernateUtil.executeQuery(session, hql, new Object[]{empid, companyid,false});
            int goalcnt = goallst.size();
            Finalgoalmanagement fgmt = null;
            if (!goallst.isEmpty()) {
                if (goalcnt < goallimit) {
                    for (int x = 0; x < goalcnt; x++) {
                        update=new JSONObject();
                        fgmt = (Finalgoalmanagement) goallst.get(x);
                        if(fgmt.getCreatedon()!=null)
                        {
                          goaldate=formatter.format(fgmt.getCreatedon());
                        }
                        else{
                         goaldate=new Date().toString();
                        }
                        result = "" + fgmt.getGoalname() + " goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + goaldate + "</font>";
                        update.put("update",""+getContentSpan(result)+"");
                        allUpdates.append("data",update);
                    }
                } else {
                    for (int x = 0; x < goallimit; x++) {
                         update=new JSONObject();
                        fgmt = (Finalgoalmanagement) goallst.get(x);
                          if(fgmt.getCreatedon()!=null)
                        {
                          goaldate=formatter.format(fgmt.getCreatedon());
                        }
                        else{
                         goaldate=new Date().toString();
                        }
                        result = "" + fgmt.getGoalname() + " goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + goaldate + "</font>";
                        update.put("update",""+getContentSpan(result)+"");
                        allUpdates.append("data",update);
                    }
                }

            }
                } else {
                Company cmp1 = (Company) session.get(Company.class, companyid);
                CompanyPreferences cmpPref = (CompanyPreferences) session.get(CompanyPreferences.class, companyid);
                if (PermissionHandler.isSubscribed(hrms_Modules.payroll, Long.toString(cmpPref.getSubscriptionCode()))) {
                    String hql = "from Template where companyid=?";
                    List lst = HibernateUtil.executeQuery(session, hql, new Object[]{companyid});
                    if (!lst.isEmpty()) {
                        update = new JSONObject();
                        Calendar cal = Calendar.getInstance();
                        days = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DATE);
                        result = "Salary generation is <font color='green'>" + days + " days</font> due";
                        update.put("update", "" + getContentSpan(result) + "");
                        allUpdates.append("data", update);
                    }
                }
            }
               JSONObject data=new JSONObject();
               JSONArray jarr=new JSONArray();
               jarr=allUpdates.getJSONArray("data");
               int stoffset=Integer.parseInt(offset);
               int stlimit=Integer.parseInt(limit);
               for(int cntData=stoffset;cntData<(stoffset+stlimit)&&cntData<jarr.length();cntData++){
                    JSONObject tmpObj=new JSONObject();
                    tmpObj.put("update",jarr.getJSONObject(cntData).get("update"));
                    finalJson.append("data",tmpObj );
               }
               finalJson.put("count",jarr.length());
               String updates = "{\"valid\": true, \"success\": true, \"data\":" + finalJson.toString() + "}";
               result=updates;
            } catch (NullPointerException e) {
                throw ServiceException.FAILURE("CompanyHandler.generateUpdate", e);
            }catch(HibernateException ex){
            throw ServiceException.FAILURE("CompanyHandler.generateUpdate", ex);
            }
            return result;
    }

    public static String getMessage(int type, int mode){
        String r = "";
        String temp = "";
        switch(type){
            case 1:     // success messages
                temp = "m" + String.format("%02d", mode);
                r = "{\"success\": true, \"infocode\": \"" + temp + "\", \"action\" : " + action + "}";
                break;
            case 2:     // error messages
                temp = "e" + String.format("%02d", mode);

                r = "{\"success\": false, \"errorcode\": \"" + temp + "\", \"action\" : " + action + "}";
                break;
        }
        return r;
    }

    public static String editUser(Session session, HttpServletRequest request) throws ServiceException {
        String r = getMessage(1, 11);//"{\"success\": true, \"infocode\": \"m07\"}";
        try {
            String userid = "";
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            boolean flag = false;
            if (jobj.has("userid")) {
                userid = StringUtil.serverHTMLStripper(jobj.get("userid").toString());
            } else {
                flag = true;
                r = getMessage(2, 1);//"{\"success\": false, \"errorcode\": \"e01\"}";
            }
            if (!flag) {
                String emailid = jobj.has("emailid")?jobj.getString("emailid").trim().replace(" ", "+"):"";
                String fname = jobj.has("fname")?StringUtil.serverHTMLStripper(jobj.get("fname").toString()):"";
                String lname = jobj.has("lname")?StringUtil.serverHTMLStripper(jobj.get("lname").toString()):"";
                emailid = StringUtil.serverHTMLStripper(emailid);
                String contactno = jobj.has("contactno")?StringUtil.serverHTMLStripper(jobj.get("contactno").toString()):"";
                String address = jobj.has("address")?StringUtil.serverHTMLStripper(jobj.get("address").toString()):"";
                String tzId = jobj.has("timezone")?StringUtil.serverHTMLStripper(jobj.get("timezone").toString()):"";

                User usr = (User)session.get(User.class, userid);
                if(usr!=null) {
                    usr.setFirstName(fname);
                    usr.setLastName(lname);
                    usr.setEmailID(emailid);
                    usr.setAddress(address);
                    usr.setContactNumber(contactno);
                    usr.setTimeZone(StringUtil.isNullOrEmpty(tzId)?null:(KWLTimeZone) session.load(KWLTimeZone.class, tzId));
                    session.save(usr);
                } else {
                    r = getMessage(2, 6);
                }
            }
        } catch (JSONException e) {
            // Error Connecting to Server
            r = getMessage(2, 2);//"{\"success\": false, \"errorcode\": \"e02\"}";
            Logger.getLogger(remoteapi.class.getName()).log(Level.SEVERE, "JSON Exception While Editing User", e);
            throw ServiceException.FAILURE(r, e);

        } catch (Exception e) {
            // Error Connecting to Server
            r = getMessage(2, 2);//"{\"success\": false, \"errorcode\": \"e02\"}";
            Logger.getLogger(remoteapi.class.getName()).log(Level.SEVERE, "Exception While Editing User", e);
            throw ServiceException.FAILURE(r, e);
        }
        return r;
    }
    
    private static String deactivateCompany(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String companyid = "";
            if (jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                String[] ids = companyid.split(",");
                for (int i = 0; i < ids.length; i++) {
                    Company c = (Company) session.get(Company.class, ids[i]);
                    if (c != null) {
                        c.setDeactivate(1);
                        session.save(c);
                        result = getMessage(1, 10);
                    }
                }

            } else{
                   return getMessage(2, 1);
            }
        } catch (Exception e) {
            result = "{\"success\":false, 'errormsg': 'Following error occured while deactivating company : '"+e.getMessage()+"}";
            throw ServiceException.FAILURE("remoteapi.deactivateCompany:"+e.getMessage(), e);
        }
        return result;
    }
    
   
    public static boolean isCompanyActivated(Session session, HttpServletRequest request) throws ServiceException{
       
        boolean result = false;
       
        try{
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String companyid = "";
            if(!jobj.isNull("companyid")) {
                companyid = jobj.getString("companyid");
                
                String query = "select deactivate from Company where companyID=? ";
                List list = HibernateUtil.executeQuery(session, query, companyid);
                Iterator itr = list.iterator();
                while (itr.hasNext()) {
                    int deactivated = ((Integer) itr.next());
                    if(deactivated==0){
                        result = true;
                    }
                }
            } 
            
        } catch(JSONException e){
            throw ServiceException.FAILURE("JSON exception in isCompanyActivated()", e);
        } catch(ServiceException e){
            throw ServiceException.FAILURE("Service exception in isCompanyActivated()", e);
        }
       
        return result;
    }
    
    
    private static String deleteCompany(Session session, HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try{
            
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String companyid = "";
            if(!jobj.isNull("companyid")) {
                companyid = jobj.getString("companyid");
                
                Query A1 = session.createSQLQuery(" delete FROM appraisalmanagementquestionanswers where employee in (select userid from users where company=?) ");
                A1.setString(0,companyid);  
                int z2= A1.executeUpdate();
                
                Query a2 = session.createSQLQuery(" delete FROM appraisalquestionanswers where appraisal in (SELECT appraisal FROM appraisal where appraisal in (SELECT appraisalid FROM appraisalmanagement where employee in (select userid from users where company=?))) ");
                a2.setString(0,companyid);  
                a2.executeUpdate();
                
                Query a3 = session.createSQLQuery(" delete FROM appraisalcyclecompetencyquestions where appraisalid in (SELECT appraisal FROM appraisal where appraisal in (SELECT appraisalid FROM appraisalmanagement where employee in (select userid from users where company=?))) ");
                a3.setString(0,companyid);  
                a3.executeUpdate();
               
                Query q1 = session.createSQLQuery(" delete FROM CompanyPreferences where companyid=? ");
                q1.setString(0,companyid);  
                int a= q1.executeUpdate();
                
                Query q2 = session.createSQLQuery(" delete from appraisal where appraisal in (select appraisalid FROM appraisalmanagement where empdesid in (select id FROM MasterData where company=?)) ");
                q2.setString(0,companyid);  
                q2.executeUpdate();
                
                Query q3 = session.createSQLQuery(" delete from appraisal where goal in (select id FROM finalgoalmanagement where userid in (select userid from users where company=?) ) ");
                q3.setString(0,companyid);  
                q3.executeUpdate();
                
                Query q4 = session.createSQLQuery(" delete from appraisal where appraisal in (select appraisalid FROM appraisalmanagement where manager in (select userid FROM users where company=?) ) ");
                q4.setString(0,companyid);  
                q4.executeUpdate();
                
                
                Query q5 = session.createSQLQuery(" delete FROM appraisalmanagement where empdesid in (select id FROM MasterData where company=?) ");
                q5.setString(0,companyid);  
                q5.executeUpdate();
                
                
                Query q6 = session.createSQLQuery(" delete FROM appraisalmanagement where manager in (select userid FROM users where company=?) ");
                q6.setString(0,companyid);  
                q6.executeUpdate();
                
                Query q7 = session.createSQLQuery(" delete FROM applyagency where agencyid in (select agencyid FROM agency where company=?) ");
                q7.setString(0,companyid);  
                q7.executeUpdate();
                
                Query q8 = session.createSQLQuery(" delete FROM applyagency where posid in (select positionid from positionmain where manager in (select userid from users where company=?)) ");
                q8.setString(0,companyid);  
                q8.executeUpdate();
                
                Query q9 = session.createSQLQuery(" delete FROM applicant where jobid in (select positionid FROM positionmain where manager in (select userid from users where company=?)) ");
                q9.setString(0,companyid);  
                q9.executeUpdate();
                
                Query q10 = session.createSQLQuery(" delete FROM agency where company=? ");
                q10.setString(0,companyid);  
                q10.executeUpdate();
                
                Query q11 = session.createSQLQuery(" delete FROM recruiter where allapplication in (select id FROM allapplications where company=?) ");
                q11.setString(0,companyid);  
                q11.executeUpdate();
                
                Query q12 = session.createSQLQuery(" delete FROM recruiter where allapplication in (select id FROM allapplications where jobposid in (select positionid FROM positionmain where manager in (select userid from users where company=?))) ");
                q12.setString(0,companyid);  
                q12.executeUpdate();
                
                Query q13 = session.createSQLQuery(" delete FROM allapplications where jobposid in (select positionid FROM positionmain where manager in (select userid from users where company=?)) ");
                q13.setString(0,companyid);  
                q13.executeUpdate();
                
                Query q14 = session.createSQLQuery(" delete FROM allapplications where company=? ");
                q14.setString(0,companyid);  
                q14.executeUpdate();
                
                Query q15 = session.createSQLQuery(" delete FROM apiresponse where companyid=? ");
                q15.setString(0,companyid);  
                q15.executeUpdate();
                
                Query q16 = session.createSQLQuery(" delete FROM competencyAvg where appcycle in (select id FROM appraisalcycle where company=? )");
                q16.setString(0,companyid);  
                q16.executeUpdate();
                
                Query q17 = session.createSQLQuery(" delete FROM competencyAvg where appcycle in (select id FROM appraisalcycle where createdby in (select userid from users where company=?)  )");
                q17.setString(0,companyid);  
                q17.executeUpdate();
                
                
                Query q18 = session.createSQLQuery(" delete FROM appraisalcycle where createdby in (select userid from users where company=?) ");
                q18.setString(0,companyid);  
                q18.executeUpdate();
                
                
                Query q19 = session.createSQLQuery(" delete FROM appraisalcycle where company=? ");
                q19.setString(0,companyid);  
                q19.executeUpdate();
                
                Query q20 = session.createSQLQuery(" delete FROM pdfreporttemplate where user in (select userid from users where company=?) ");
                q20.setString(0,companyid);  
                q20.executeUpdate();
                
                Query q21 = session.createSQLQuery(" delete FROM competencyeval where empid in (select userid from users where company=?) ");
                q21.setString(0,companyid);  
                q21.executeUpdate();
                
                Query q22 = session.createSQLQuery(" delete FROM audittrailpayroll where companyid=? ");
                q22.setString(0,companyid);  
                q22.executeUpdate();
                
                Query q23 = session.createSQLQuery(" delete FROM companydetails where companyid=? ");
                q23.setString(0,companyid);  
                q23.executeUpdate();
                
                Query q24 = session.createSQLQuery(" delete  FROM companyholiday where company=? ");
                q24.setString(0,companyid);  
                q24.executeUpdate();
                
                Query q25 = session.createSQLQuery(" delete FROM component_resource_mapping_history where component in (select compid FROM componentmaster where companyid=?) ");
                q25.setString(0,companyid);  
                q25.executeUpdate();
                
                Query q26 = session.createSQLQuery(" delete FROM component_resource_mapping where component in (select compid FROM componentmaster where companyid=?) ");
                q26.setString(0,companyid);  
                q26.executeUpdate();
                
                Query q27 = session.createSQLQuery(" Update componentmaster set computeon = null  where companyid=? ");
                q27.setString(0,companyid);  
                q27.executeUpdate();
                
                Query q28 = session.createSQLQuery(" delete FROM specifiedcomponents where component in (select compid FROM componentmaster where companyid=? )");
                q28.setString(0,companyid);  
                q28.executeUpdate();
                
                Query q29 = session.createSQLQuery(" delete FROM componentmaster where companyid=? ");
                q29.setString(0,companyid);  
                q29.executeUpdate();
                
                Query q30 = session.createSQLQuery(" delete FROM configType where company=? ");
                q30.setString(0,companyid);  
                q30.executeUpdate();
                
                Query q31 = session.createSQLQuery(" delete FROM configrecruitment where company=? ");
                q31.setString(0,companyid);  
                q31.executeUpdate();
                
                Query q32 = session.createSQLQuery(" delete FROM configrecruitmentdata where company= ? ");
                q32.setString(0,companyid);  
                q32.executeUpdate();
                
                Query q33 = session.createSQLQuery(" delete FROM empcomment where empid in (select userid from users where company=?) ");
                q33.setString(0,companyid);  
                q33.executeUpdate();
                
                Query q34 = session.createSQLQuery(" delete FROM crm_docs where companyid= ? ");
                q34.setString(0,companyid);  
                q34.executeUpdate();
                
                Query q35 = session.createSQLQuery(" delete FROM crm_docsmap where docid in (select docid FROM crm_docs where companyid=?) ");
                q35.setString(0,companyid);  
                q35.executeUpdate();
                
                Query q36 = session.createSQLQuery(" delete FROM templatemapdeduction where deductionid in ( select deductionid FROM deductionmaster where companyid=?) ");
                q36.setString(0,companyid);  
                q36.executeUpdate();
                
                Query q37 = session.createSQLQuery(" delete FROM historydetail where deductionmaster in ( select deductionid FROM deductionmaster where companyid=? )");
                q37.setString(0,companyid);  
                q37.executeUpdate();
                
                Query q38 = session.createSQLQuery(" delete  FROM deductionmaster where companyid=? ");
                q38.setString(0,companyid);  
                q38.executeUpdate();
                
                Query q39 = session.createSQLQuery(" delete FROM templatemapemployercontribution where empcontribid in ( select id  FROM employercontributionmaster where companyid=?) ");
                q39.setString(0,companyid);  
                q39.executeUpdate();
                
                Query q40 = session.createSQLQuery(" delete FROM employercontributionmaster where companyid=? ");
                q40.setString(0,companyid);  
                q40.executeUpdate();
                
                Query q41 = session.createSQLQuery(" delete FROM externalapplicants where company=? ");
                q41.setString(0,companyid);  
                q41.executeUpdate();
                
                
                Query q42 = session.createSQLQuery(" delete FROM goalrating where company= ? ");
                q42.setString(0,companyid);  
                q42.executeUpdate();
                
                Query q43 = session.createSQLQuery(" delete FROM goalappraisal where empid in (select userid from users where company=?) ");
                q43.setString(0,companyid);  
                q43.executeUpdate();

                Query q44 = session.createSQLQuery(" delete FROM goaleval where empid in (select userid from users where company=?) ");
                q44.setString(0,companyid);  
                q44.executeUpdate();
                
                Query q45 = session.createSQLQuery(" delete FROM competencyappraisal where empid in (select userid from users where company=?) ");
                q45.setString(0,companyid);  
                q45.executeUpdate();
                
                Query q46 = session.createSQLQuery(" delete FROM finalreport where empid in (select userid from users where company=?) ");
                q46.setString(0,companyid);  
                q46.executeUpdate();
                
                
                Query q47 = session.createSQLQuery(" delete FROM compensation where comeid in (select userid from users where company=?) ");
                q47.setString(0,companyid);  
                q47.executeUpdate();
                
                Query q48 = session.createSQLQuery(" delete FROM group1 where companyid=? ");
                q48.setString(0,companyid);  
                q48.executeUpdate();
                
                Query q49 = session.createSQLQuery(" delete FROM hrms_emailTemplates where company= ? ");
                q49.setString(0,companyid);  
                q49.executeUpdate();
                
                Query q50 = session.createSQLQuery(" delete FROM htmltemplate where company=? ");
                q50.setString(0,companyid);  
                q50.executeUpdate();
                
                Query q51 = session.createSQLQuery(" delete FROM importlog where company=? ");
                q51.setString(0,companyid);  
                q51.executeUpdate();
                
                Query q52 = session.createSQLQuery(" delete FROM recruiter where allapplication in ( select id from allapplications where company=? )");
                q52.setString(0,companyid);  
                q52.executeUpdate();
                
                Query q53 = session.createSQLQuery(" delete FROM allapplications where jobapplicantid in ( select applicantid FROM jobapplicant where company=?) ");
                q53.setString(0,companyid);  
                q53.executeUpdate();
                
                Query q54 = session.createSQLQuery(" delete FROM hrms_docsmap where docid in ( select docid FROM hrms_docs where applicantid in (select applicantid from jobapplicant where company=?)) ");
                q54.setString(0,companyid);  
                q54.executeUpdate();
                
                Query q55 = session.createSQLQuery(" delete FROM hrms_docsmap where docid in ( select docid FROM hrms_docs where userid in (select userid from users where company=?)) ");
                q55.setString(0,companyid);  
                q55.executeUpdate();
                
                Query q56 = session.createSQLQuery(" delete FROM hrms_docs where applicantid in (select applicantid from jobapplicant where company=?) ");
                q56.setString(0,companyid);  
                q56.executeUpdate();
                
                Query q57 = session.createSQLQuery(" delete FROM hrms_docs where userid in (select userid from users where company=?) ");
                q57.setString(0,companyid);  
                q57.executeUpdate();
                
                Query q58 = session.createSQLQuery(" delete FROM jobapplicant where company=? ");
                q58.setString(0,companyid);  
                q58.executeUpdate();
                
                
                Query q59 = session.createSQLQuery(" delete FROM malaysia_company_form where company= ? ");
                q59.setString(0,companyid);  
                q59.executeUpdate();
                
                Query q60 = session.createSQLQuery(" delete FROM malaysia_form_company where company= ? ");
                q60.setString(0,companyid);  
                q60.executeUpdate();
                
                Query q61 = session.createSQLQuery(" delete FROM malaysian_statutory_employee where user in ( select userid FROM users where company=? ) ");
                q61.setString(0,companyid);  
                q61.executeUpdate();
                
                Query q62 = session.createSQLQuery(" delete FROM malaysian_statutory_company where company=? ");
                q62.setString(0,companyid);  
                q62.executeUpdate();
                
                Query q63 = session.createSQLQuery(" delete FROM managecmpt where cmptid in ( select cmptid from mastercmpt where company= ?) ");
                q63.setString(0,companyid);  
                q63.executeUpdate();
                
                Query q64 = session.createSQLQuery(" delete FROM mastercmpt where company=? ");
                q64.setString(0,companyid);  
                q64.executeUpdate();
                
                Query q65 = session.createSQLQuery(" delete FROM oldaudittrail where company=? ");
                q65.setString(0,companyid);  
                q65.executeUpdate();
                
                Query q66 = session.createSQLQuery(" delete FROM jobprofile where position in (select positionid FROM positionmain where company=? )");
                q66.setString(0,companyid);  
                q66.executeUpdate();
                
                Query q67 = session.createSQLQuery(" delete FROM jobprofile where position in (select positionid FROM positionmain where company=? )");
                q67.setString(0,companyid);  
                q67.executeUpdate();
                
                Query q68 = session.createSQLQuery(" delete FROM positionmain where manager in (select userid from users where company=?)");
                q68.setString(0,companyid);  
                q68.executeUpdate();
                
                Query q69 = session.createSQLQuery(" delete FROM positionmain where company=? ");
                q69.setString(0,companyid);  
                q69.executeUpdate();
                
                Query q70 = session.createSQLQuery(" delete FROM userpermission where role in (select id FROM role where company=?) ");
                q70.setString(0,companyid);  
                q70.executeUpdate();
                
                Query q71 = session.createSQLQuery(" delete FROM role where company=? ");
                q71.setString(0,companyid);  
                q71.executeUpdate();
                
                Query q72 = session.createSQLQuery(" delete FROM templatemaptax where taxid in (select taxid FROM taxmaster where companyid=?) ");
                q72.setString(0,companyid);  
                q72.executeUpdate();
                
                
                Query q73 = session.createSQLQuery(" delete FROM historydetail where taxmaster in ( select taxid FROM taxmaster where companyid= ? ) ");
                q73.setString(0,companyid);  
                q73.executeUpdate();
                
                Query q74 = session.createSQLQuery(" delete FROM taxmaster where companyid=? ");
                q74.setString(0,companyid);  
                q74.executeUpdate();
                
                Query q75 = session.createSQLQuery(" delete FROM templatemapwage where templateid in (select templateid FROM template where companyid= ?) ");
                q75.setString(0,companyid);  
                q75.executeUpdate();
                
                Query q76 = session.createSQLQuery(" delete FROM historydetail where historyid in (select historyid FROM payhistory where templateid in (select templateid FROM template where companyid=? ) )");
                q76.setString(0,companyid);  
                q76.executeUpdate();
                
                Query q77 = session.createSQLQuery(" delete FROM payhistory where templateid in (select templateid FROM template where companyid=? ) ");
                q77.setString(0,companyid);  
                q77.executeUpdate();
                
                Query q78 = session.createSQLQuery(" delete FROM historydetail where historyid in (select historyid FROM payhistory where userID in (select userid FROM users where company=? ) )");
                q78.setString(0,companyid);  
                q78.executeUpdate();
                
                Query q79 = session.createSQLQuery(" delete FROM payhistory where userID in (select userid FROM users where company=? ) ");
                q79.setString(0,companyid);  
                q79.executeUpdate();
                
                Query q80 = session.createSQLQuery(" delete FROM template where companyid=? ");
                q80.setString(0,companyid);  
                q80.executeUpdate();
                
                Query q81 = session.createSQLQuery(" delete FROM temptemplate where companyid=? ");
                q81.setString(0,companyid);  
                q81.executeUpdate();
                
                Query q82 = session.createSQLQuery(" delete FROM wagemaster where companyid=?  ");
                q82.setString(0,companyid);  
                q82.executeUpdate();
                
                Query q83 = session.createSQLQuery(" delete FROM assignreviewer where reviewer in (SELECT userid FROM users where company =?)  ");
                q83.setString(0,companyid);  
                q83.executeUpdate();
                
                Query q84 = session.createSQLQuery(" delete FROM assignreviewer where employee in (SELECT userid FROM users where company =?)  ");
                q84.setString(0,companyid);  
                q84.executeUpdate();
                
                Query q85 = session.createSQLQuery(" delete FROM audit_trail where user in (SELECT userid FROM users where company=? ) ");
                q85.setString(0,companyid);  
                q85.executeUpdate();
                
                
                
                Query q86 = session.createSQLQuery(" delete FROM recruiter where recid in (select userid from users where company= ? )");
                q86.setString(0,companyid);  
                int a1 =q86.executeUpdate();
                
                Query q87 = session.createSQLQuery(" delete FROM assignmanager where manid in (select userid from users where company=? ) ");
                q87.setString(0,companyid);  
                q87.executeUpdate();
                
                Query q88 = session.createSQLQuery(" delete FROM assignmanager where empid in (select userid from users where company=? ) ");
                q88.setString(0,companyid);  
                q88.executeUpdate();
                
                Query q89 = session.createSQLQuery(" delete FROM widgetmanagement where user in (select userid from users where company= ?) ");
                q89.setString(0,companyid);  
                q89.executeUpdate();
                
                Query q90 = session.createSQLQuery(" delete FROM malaysian_user_incometax_info where user in (select userid from users where company=? ) ");
                q90.setString(0,companyid);  
                q90.executeUpdate();
                
                Query q91 = session.createSQLQuery(" delete FROM timesheet where userid in (select userid from users where company=? ) ");
                q91.setString(0,companyid);  
                q91.executeUpdate();
                
                Query q92 = session.createSQLQuery(" delete FROM hrms_goalcomments where goalid in (select id  FROM finalgoalmanagement where userid in (select userid from users where company=? )) ");
                q92.setString(0,companyid);  
                q92.executeUpdate();
                
                Query q93 = session.createSQLQuery(" delete FROM hrms_goalcomments where goalid in (select id  FROM finalgoalmanagement where updatedBy in (select userid from users where company=? )) ");
                q93.setString(0,companyid);  
                q93.executeUpdate();
                
                Query q94 = session.createSQLQuery(" delete FROM finalgoalmanagement where userid in (select userid from users where company=? )");
                q94.setString(0,companyid);  
                q94.executeUpdate();
                
                Query q95 = session.createSQLQuery(" delete FROM malaysia_form_cp21 where user in  (select userid from users where company=? )  ");
                q95.setString(0,companyid);  
                q95.executeUpdate();
                
                Query q96 = session.createSQLQuery(" delete FROM malaysia_form_amanah_saham_nasional where user in  (select userid from users where company =?)  ");
                q96.setString(0,companyid);  
                q96.executeUpdate();
                
                Query q97 = session.createSQLQuery(" delete FROM malaysia_form_tabung_haji where user in  (select userid from users where company = ? ) ");
                q97.setString(0,companyid);  
                q97.executeUpdate();
                
                
                Query q98 = session.createSQLQuery(" delete FROM malaysia_form_hrd_levy where user in  (select userid from users where company= ? )");
                q98.setString(0,companyid);  
                q98.executeUpdate();
                
                Query q99 = session.createSQLQuery(" delete FROM malaysian_user_taxbenefits where payrollhistory in (select historyid FROM payrollhistory where user in  (select userid from users where company=? ) ) ");
                q99.setString(0,companyid);  
                q99.executeUpdate();
                
                Query q100 = session.createSQLQuery(" delete FROM malaysian_user_taxcomponent_history where payrollhistory in (select historyid FROM payrollhistory where user in  (select userid from users where company= ?) ) ");
                q100.setString(0,companyid);  
                q100.executeUpdate();
                
                Query q101 = session.createSQLQuery(" delete FROM payrollhistory where user in  (select userid from users where company=? ) ");
                q101.setString(0,companyid);  
                q101.executeUpdate();
                
                Query q102 = session.createSQLQuery(" delete FROM malaysian_user_taxcomponents where user in  (select userid from users where company=? ) ");
                q102.setString(0,companyid);  
                q102.executeUpdate();
                
                Query q103 = session.createSQLQuery(" delete FROM hrms_Emphistory where userid in  (select userid from users where company=? ) ");
                q103.setString(0,companyid);  
                q103.executeUpdate();
                
                Query q104 = session.createSQLQuery(" delete FROM timesheettimer where user in  (select userid from users where company=? )");
                q104.setString(0,companyid);  
                q104.executeUpdate();
                
                Query q105 = session.createSQLQuery(" delete FROM hrms_empprofile where reportto in  (select userid from users where company=? )  ");
                q105.setString(0,companyid);  
                q105.executeUpdate();
                
                Query q106 = session.createSQLQuery(" delete FROM hrms_empprofile where terminatedby in  (select userid from users where company=? )  ");
                q106.setString(0,companyid);  
                q106.executeUpdate();
                
                Query q107 = session.createSQLQuery(" delete FROM malaysia_form_cp39 where useraccount in (select userid FROM users where company = ? ) ");
                q107.setString(0,companyid);  
                q107.executeUpdate();
                
                Query q108 = session.createSQLQuery(" delete FROM costcenter where company=? ");
                q108.setString(0,companyid);  
                q108.executeUpdate();
                
                
                Query q109 = session.createSQLQuery(" delete FROM finalgoalmanagement where updatedBy in (select userid from users where company=? )");
                q109.setString(0,companyid);  
                q109.executeUpdate();
                
                
                Query q110 = session.createSQLQuery(" delete FROM useraccount where userid in (select userid from users where company=?) ");
                q110.setString(0,companyid);  
                q110.executeUpdate();
                
                
                Query q111 = session.createSQLQuery(" delete FROM hrms_empprofile where userid in (select userid from users where company=?)  ");
                q111.setString(0,companyid);  
                q111.executeUpdate();
                
                Query q112 = session.createSQLQuery(" delete FROM usersearchstate where user in (select userid from users where company=?)  ");
                q112.setString(0,companyid);  
                q112.executeUpdate();
                
                Query q113 = session.createSQLQuery(" delete FROM usertemplatemap where userAccount in (select userid from users where company=?)  ");
                q113.setString(0,companyid);  
                q113.executeUpdate();
                
                Query q114 = session.createSQLQuery(" delete FROM malaysia_form_cp39a where useraccount in (select userid from users where company=?)  ");
                q114.setString(0,companyid);  
                q114.executeUpdate();
                
                Query q115 = session.createSQLQuery(" delete FROM malaysia_form_ea where useraccount in (select userid from users where company=?)  ");
                q115.setString(0,companyid);  
                q115.executeUpdate();
                
                Query q116 = session.createSQLQuery(" delete FROM malaysia_form_pcb2 where useraccount in (select userid from users where company=?)  ");
                q116.setString(0,companyid);  
                q116.executeUpdate();
                
                Query q117 = session.createSQLQuery(" delete FROM malaysia_form_tp1 where useraccount in (select userid from users where company=?)  ");
                q117.setString(0,companyid);  
                q117.executeUpdate();
                
                Query q118 = session.createSQLQuery(" delete FROM malaysia_form_tp2 where useraccount in (select userid from users where company=?)  ");
                q118.setString(0,companyid);  
                q118.executeUpdate();
                
                Query q119 = session.createSQLQuery(" delete FROM malaysia_form_tp3 where useraccount in (select userid from users where company=?)  ");
                q119.setString(0,companyid);  
                q119.executeUpdate();
                
                Query q120 = session.createSQLQuery(" delete FROM projreport_template where userid in (select userid from users where company=?)  ");
                q120.setString(0,companyid);  
                q120.executeUpdate();
                
                Query q121 = session.createSQLQuery(" delete FROM hrms_empexp where userid in (select userid from users where company=?)  ");
                q121.setString(0,companyid);  
                q121.executeUpdate();
                
                Query q122 = session.createSQLQuery(" delete FROM emailtemplatefiles where creator in (select userid from users where company=?)  ");
                q122.setString(0,companyid);  
                q122.executeUpdate();
                
                Query q123 = session.createSQLQuery(" delete FROM users where company =?  ");
                q123.setString(0,companyid);  
                q123.executeUpdate();
                
                Query q125 = session.createSQLQuery(" delete FROM MasterMap where masterdataid1 in (select id FROM MasterData where company=?)  ");
                q125.setString(0,companyid);  
                q125.executeUpdate();
                
                if(!StringUtil.equal(companyid, "a4792363-b0e1-4b67-992b-2851234d5ea6")){
                    
                    Query q126 = session.createSQLQuery(" delete FROM MasterData where company =?  ");
                    q126.setString(0,companyid);  
                    q126.executeUpdate();
                
                
                    Query q127 = session.createSQLQuery(" delete FROM company where companyid =?  ");
                    q127.setString(0,companyid);  
                    q127.executeUpdate();
                }
                
                result = "{\"success\":true}";
                
            
            } 
            
        } catch(JSONException e){
            throw ServiceException.FAILURE("JSON exception in RemoteAPI_DeleteCompany() ", e);
        } catch(HibernateException e){
            throw ServiceException.FAILURE("Hibernate exception in RemoteAPI_DeleteCompany()", e);
        }catch(Exception e){
            throw ServiceException.FAILURE("Hibernate exception in RemoteAPI_DeleteCompany()", e);
        }
       
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
