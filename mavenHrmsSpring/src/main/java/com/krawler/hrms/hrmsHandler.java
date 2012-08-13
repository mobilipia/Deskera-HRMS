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
package com.krawler.hrms;

import com.krawler.common.admin.AuditAction;
//import com.krawler.common.util.AuditAction;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.ConfigData;
import com.krawler.common.admin.ConfigType;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_EmailTemplates;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.common.docs.PDFReportTemplate;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.HrmsMsgs;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.LocaleUtil;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.handlers.fileUploader;
import com.krawler.utils.json.base.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.esp.web.resource.Links;
import com.krawler.hrms.ess.Empexperience;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.compennsation.Compensation;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.master.Master;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.GoalComments;
import com.krawler.hrms.recruitment.Positionmain;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.hrms.performance.Mastercmpt;
import com.krawler.hrms.recruitment.Agency;
import com.krawler.hrms.recruitment.Allapplications;
import com.krawler.hrms.recruitment.Applicants;
import com.krawler.hrms.recruitment.ConfigRecruitmentData;
import com.krawler.hrms.recruitment.Jobapplicant;
import com.krawler.hrms.recruitment.Jobprofile;
import com.krawler.hrms.recruitment.Recruiter;
import com.krawler.hrms.timesheet.Timesheet;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.common.locale.MessageSourceProxy;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.fileupload.FileItem;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author krawler
 */
public class hrmsHandler {
    private static SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void Insertdummyfunction(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        try {
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.dummyfunction", ex);
        }


    }

    public static JSONObject InternalJobpositions(Session session, HttpServletRequest request) throws ServiceException, ParseException, SessionExpiredException, JSONException {
        Date date;
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        boolean checkflag = true;
        JSONObject jobj = new JSONObject();
        try {
            Company cmp = (Company) session.load(Company.class, AuthHandler.getCompanyid(request));
            CompanyPreferences cmpPref = (CompanyPreferences) session.load(CompanyPreferences.class, cmp.getCompanyID());
            String jobidformat = (cmpPref.getJobidformat() == null ? "" : cmpPref.getJobidformat());
            if (StringUtil.isNullOrEmpty(request.getParameter("posid"))) {
                String jobid = (request.getParameter("jobid")).substring(jobidformat.length());
                checkflag = checkJobs(request.getParameter("position"), request.getParameter("department"), request.getParameter("jobtype"), session, request);
                if (checkflag) {
                    Positionmain posmain = new Positionmain();
                    MasterData md = (MasterData) session.load(MasterData.class, request.getParameter("position"));
                    MasterData dept = (MasterData) session.load(MasterData.class, request.getParameter("department"));
                    User man = (User) session.load(User.class, request.getParameter("manager"));
                    date = (Date) fmt.parse(request.getParameter("startdate"));
                    posmain.setStartdate(date);
                    date = (Date) fmt.parse(request.getParameter("enddate"));
                    posmain.setEnddate(date);
                    posmain.setPosition(md);
                    posmain.setDetails(request.getParameter("details"));
                    posmain.setJobtype(request.getParameter("jobtype"));
                    posmain.setJobidwthformat(Integer.parseInt(jobid));
                    posmain.setJobid(jobidformat + (Integer.parseInt(jobid)));
                    posmain.setDelflag(0);
                    posmain.setCompany(cmp);
                    posmain.setManager(man);
                    posmain.setDepartmentid(dept);
                    posmain.setNoofpos(Integer.parseInt(request.getParameter("nopos")));
                    posmain.setCreatedBy((User) session.get(User.class, AuthHandler.getUserid(request)));
                    posmain.setPositionsfilled(0);
                    session.save(posmain);
                    jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.jobpositionupdatedsuccessfully",null,"Job position added successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                } else {
                    jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.jobpositionalreadypresent",null,"Job position already present", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                }
            } else {
                Positionmain posmain = (Positionmain) session.load(Positionmain.class, request.getParameter("posid"));
                if (request.getParameter("details")!=null) {
                    posmain.setDetails(request.getParameter("details"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("manager"))) {
                    posmain.setManager((User) session.load(User.class, request.getParameter("manager")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                    posmain.setDepartmentid((MasterData) session.load(MasterData.class, request.getParameter("department")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("startdate"))) {
                    date = (Date) fmt.parse(request.getParameter("startdate"));
                    posmain.setStartdate(date);
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                    date = (Date) fmt.parse(request.getParameter("enddate"));
                    if(new Date(fmt.format(new Date())).after(date)){
                        posmain.setDelflag(2);
                    }else{
                        posmain.setDelflag(0);
                    }
                    posmain.setEnddate(date);
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("nopos"))) {
                    if(posmain.getPositionsfilled()==Integer.parseInt(request.getParameter("nopos"))){
                        posmain.setDelflag(3);
                    }
                    posmain.setNoofpos(Integer.parseInt(request.getParameter("nopos")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("jobshift"))) {
                    posmain.setJobshift(request.getParameter("jobshift"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("location"))) {
                    posmain.setLocation(request.getParameter("location"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("relocation"))) {
                    posmain.setRelocation(request.getParameter("relocation"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("expmonth"))) {
                    posmain.setExperiencemonth(Integer.parseInt(request.getParameter("expmonth")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("expyear"))) {
                    posmain.setExperienceyear(Integer.parseInt(request.getParameter("expyear")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("travel"))) {
                    posmain.setTravel(request.getParameter("travel"));
                }
                session.update(posmain);
                jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.jobpositionupdatedsuccessfully.",null,"Job position updated successfully.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }
            //@@ProfileHandler.insertAuditLog(session,
//                    ("Internal".equals(request.getParameter("jobtype")) ? AuditAction.INTERNAL_JOB_ADDED : AuditAction.EXTERNAL_JOB_ADDED),
//                    "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added " + request.getParameter("jobtype") + " job", request);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("InternalJobpositions", ex);
        } catch (ParseException ex) {
            throw ServiceException.FAILURE("InternalJobpositions", ex);
        } catch (SessionExpiredException ex) {
            throw ServiceException.FAILURE("InternalJobpositions", ex);
        } catch (JSONException ex) {
            throw new JSONException("InternalJobpositions");
        } finally {
        }
        return jobj;
    }

    public static boolean checkJobs(String desigid, String deptid, String type, Session session, HttpServletRequest request) throws ServiceException {
        List tabledata = null;
        boolean flag = true;
        try {
            String hql = "from Positionmain where position.id=? and departmentid.id=? and jobtype=? and delflag!=1";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{desigid, deptid, type});
            if (!tabledata.isEmpty()) {
                flag = false;
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("checkJobs", ex);
        }
        return flag;
    }

    public static JSONObject DeleteInternalJobs(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, JSONException {
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        String hql = null;
        boolean flag=true;
        try {
            String delids[] = request.getParameterValues("delid");
            for (int i = 0; i < delids.length; i++) {
                hql = "from Allapplications where position.positionid=? and company.companyID=? and delflag=0";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{delids[i], AuthHandler.getCompanyid(request)});
                if (tabledata.isEmpty()) {
                    hql = "from Positionmain where positionid=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, delids[i]);
                    if (!tabledata.isEmpty()) {
                        Positionmain log = (Positionmain) tabledata.get(0);
                        log.setDelflag(1);
                        session.update(log);
                    }
                }else{
                    flag=false;
                }
            }
            if (flag) {
                jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.jobpositionsselectedaresuccessfullydeleted",null,"Job positions selected are successfully deleted.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            } else {
                jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.somejobshaveassignedapplicantshencecannotbedeleted",null,"Some jobs have assigned applicants hence cannot be deleted", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("DeleteInternalJobs", ex);
        } catch (SessionExpiredException ex) {
            throw ServiceException.FAILURE("DeleteInternalJobs", ex);
        } catch (JSONException ex) {
            throw new JSONException("DeleteInternalJobs");
        } finally {
        }
        return jobj;
    }

    public static void addMasterField(Session session, HttpServletRequest request) throws ServiceException {
        int goalid = 0;
        try {
            if (request.getParameter("action").equals("Add")) {
                Master gmst = new Master();
                Company cmp=(Company) session.load(Company.class,AuthHandler.getCompanyid(request));
                gmst.setName(request.getParameter("name"));
                // gmst.setCompany(cmp);
                session.save(gmst);
            } else {
                goalid = Integer.parseInt(request.getParameter("id"));
                Master gmst = (Master) session.load(Master.class, goalid);
                gmst.setName(request.getParameter("name"));
                session.save(gmst);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.addMasterField", ex);
        } finally {
        }

    }

    public static String insertTimeSheet(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject msg = new JSONObject();
        int approved = 0;
        String apprmanager = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            msg.put("msg","No data to change");
            User usr = null;
            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                String empid = AuthHandler.getUserid(request);
                usr = (User) session.load(User.class, empid);
            } else {
                usr = (User) session.load(User.class, request.getParameter("empid"));
            }

            String jsondata = request.getParameter("jsondata");
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            String[] dateA = request.getParameterValues("colHeader");
            ArrayList timesheetIDs=new ArrayList();
            timesheetIDs.add(AuthHandler.getUserid(request));
            String marks="";

            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                for (int j = 1; j <= 7; j++) {
                    String [] time=jobj.getString("col" + j).split(":");
                    Date dateValue = fmt.parse(dateA[j - 1]);
                    String jobtype = jobj.getString("jobtype");
                     if(approved==0){
                        if (jobj.getString("colid" + j).equals("undefined")) {

                            String id = UUID.randomUUID().toString();
                            Timesheet tmsht = new Timesheet();
                            tmsht.setId(id);
                            tmsht.setDatevalue(dateValue);
                            tmsht.setUserID(usr);
                            tmsht.setJobtype(jobtype);
                            tmsht.setWorktime(Integer.parseInt(time[0]));
                            tmsht.setWorktimemin(Integer.parseInt(time[1]));
                            tmsht.setApproved(approved);
                            tmsht.setApprovedby(apprmanager);
                            session.save(tmsht);
                        marks+="?,";
                            timesheetIDs.add(tmsht.getId());
                         msg.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.timesheetsavedsuccessfully",null,"Timesheet has been saved successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        } else {
                            Timesheet tmsht = (Timesheet) session.load(Timesheet.class, jobj.getString("colid" + j));
                        approved=tmsht.getApproved();
                        if(approved==0)
                        {
                                tmsht.setDatevalue(dateValue);
                                tmsht.setUserID(usr);
                                tmsht.setJobtype(jobtype);
                                tmsht.setWorktime(Integer.parseInt(time[0]));
                                tmsht.setWorktimemin(Integer.parseInt(time[1]));
                                tmsht.setApproved(approved);
                                tmsht.setApprovedby(apprmanager);
                                session.save(tmsht);
                         msg.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.timesheetsavedsuccessfully",null,"Timesheet has been saved successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        }else{
                           msg.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.timesheetisalreadyapproved",null,"Timesheet is already approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                            }
                        marks+="?,";
                            timesheetIDs.add(tmsht.getId());
                        }
                    }

                }
            }
            //@@ProfileHandler.insertAuditLog(session, AuditAction.TIMESHEET_SUBMITTED, "Employee " + AuthHandler.getFullName(usr) + " has changed timesheet for the duration from " + dateA[0] +" to " +dateA[6],request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.TaskInsert", ex);
        } finally {
        }
        return msg.toString();
    }

    public static void insertGoal(Session session, HttpServletRequest request) throws ServiceException {
        int approved = 0;
        List tabledata = null;
        String apprmanager = "";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        int logtext=0;
        try {
            Finalgoalmanagement fgmt = null;
            if (StringUtil.isNullOrEmpty(request.getParameter("archive"))) {
                apprmanager = AuthHandler.getFullName(session,AuthHandler.getUserid(request));
                User usr = (User) session.load(User.class, request.getParameter("empid"));
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
                    String id = UUID.randomUUID().toString();
                    Date startdate = AuthHandler.getDateFormatter(request).parse(jobj.getString("gstartdate"));
                    Date enddate = AuthHandler.getDateFormatter(request).parse(jobj.getString("genddate"));
                    if (jobj.getString("gid").equals("undefined")) {
                        fgmt = new Finalgoalmanagement();
                        fgmt.setId(id);
                        fgmt.setCreatedon(new Date());
                        fgmt.setInternal(true);
                        logtext=0;
                    } else {
                        fgmt = (Finalgoalmanagement) session.load(Finalgoalmanagement.class, jobj.getString("gid"));
                        fgmt.setUpdatedon(new Date());
                        logtext=1;
                    }
                    fgmt.setContext(jobj.getString("gcontext"));
                    fgmt.setAssignedby(apprmanager);
                    fgmt.setManager((User) session.load(User.class,AuthHandler.getUserid(request)));
                    fgmt.setGoaldesc(jobj.getString("gdescription"));
                    fgmt.setGoalwth(Integer.parseInt(jobj.getString("gwth")));
                    fgmt.setGoalname(jobj.getString("gname"));
                    fgmt.setStartdate(startdate);
                    fgmt.setEnddate(enddate);
                    fgmt.setPriority(jobj.getString("gpriority"));
                    fgmt.setcomment(jobj.getString("gcomment"));
                    fgmt.setUserID(usr);
                    fgmt.setArchivedflag(0);
                    fgmt.setCompleted(true);
                    session.save(fgmt);
                    if(logtext==0) {
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has assigned new goal " + fgmt.getGoalname()+  " to " + AuthHandler.getFullName(fgmt.getUserID()),request);
                    } else {
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has updated goal " + fgmt.getGoalname()+  " for " + AuthHandler.getFullName(fgmt.getUserID()),request);
                    }
                }
            } else {
                String archiveids[] = request.getParameterValues("archiveid");
                for (int i = 0; i < archiveids.length; i++) {
                    String hql = "from Finalgoalmanagement where id=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, archiveids[i]);
                    if (!tabledata.isEmpty()) {
                        fgmt = (Finalgoalmanagement) tabledata.get(0);
                        fgmt.setArchivedflag(1);
                        session.update(fgmt);
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_ARCHIVED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has archived " + AuthHandler.getFullName(fgmt.getUserID()) + "'s goal " + fgmt.getGoalname(),request);
                    }
                }
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.GoalInsert", ex);
        } finally {
        }

    }

    public static void addMasterDataField(Session session, HttpServletRequest request) throws ServiceException {
        String goalid = "";
        String auditID = "";
        String auditMsg = "";
        String masterGroup = "";
        try {
            Master master = (Master) session.load(Master.class, Integer.parseInt(request.getParameter("configid")));
            MasterData gmst;
            User user = (User) session.load(User.class, AuthHandler.getUserid(request));
            String fullName = AuthHandler.getFullName(user);
            if (request.getParameter("action").equals("Add")) {
                gmst = new MasterData();
                gmst.setValue(request.getParameter("name"));
                gmst.setMasterid(master);
                gmst.setCompany((Company) session.get(Company.class,AuthHandler.getCompanyid(request)));
                auditID = com.krawler.common.util.AuditAction.MASTER_ADDED;
                auditMsg = "added ";
                session.save(gmst);
            } else {
                goalid = request.getParameter("id");
                gmst = (MasterData) session.load(MasterData.class, goalid);
                gmst.setValue(request.getParameter("name"));
                gmst.setMasterid(master);
                auditID = com.krawler.common.util.AuditAction.MASTER_EDITED;
                auditMsg = " updated ";
                session.save(gmst);
            }
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+"master item <b>"+gmst.getValue()+"</b> of master group <b>"+master.getName()+".</b>", request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.addMasterField", ex);
        } finally {
        }

    }

    public static JSONObject AppraisalAssign(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException, JSONException {
        JSONObject jobj = new JSONObject();
        try {
            Appraisalmanagement appm = null;
            boolean flag = true;
            String query = "";
            String manager = "";
            String hql = "";
            Iterator ite = null;
            List tabledata = null;
            List list = null;
            String ids[] = request.getParameterValues("empids");
            Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
            hql="select id from Appraisalcycle where id=? and submitenddate>=?";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("apptype"),userdate});
           if (tabledata.isEmpty()) {
                jobj.put("message", "Appraisal cycle submission has already ended");
            } else {
                for (int i = 0; i < ids.length; i++) {
                    hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? ";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{ids[i], request.getParameter("apptype")});
                    if (tabledata.isEmpty()) {//check for appraisal cycle already initiated
                        query = "select count(assignman.userID) from Assignmanager  where assignemp.userID=? and managerstatus=1";
                        list = HibernateUtil.executeQuery(session, query, ids[i]);
                        long man = (Long) list.get(0);
                        query = "select count(reviewer.userID) from Assignreviewer  where employee.userID=? and reviewerstatus=1";
                        list = HibernateUtil.executeQuery(session, query, ids[i]);
                        long rv = (Long) list.get(0);
                        if (man > 0 && rv > 0) { //at least 1 manager and reviewer are assigned
                            query = "from Assignmanager where assignemp.userID=? and managerstatus=1";
                            list = HibernateUtil.executeQuery(session, query, ids[i]);
                            ite = list.iterator();
                            while (ite.hasNext()) {
                                Assignmanager assignman = (Assignmanager) ite.next();
                                if (assignman.getAssignman() != null) {
                                    manager = assignman.getAssignman().getUserID();
                                    appm = new Appraisalmanagement();
                                    User usr = (User) session.load(User.class, ids[i]);
                                    appm.setAppcycle((Appraisalcycle) session.load(Appraisalcycle.class, request.getParameter("apptype")));
                                    appm.setEmployee(usr);
                                    appm.setManager((User) session.load(User.class, manager));
                                    appm.setEmployeestatus(0);
                                    appm.setManagerstatus(0);
                                    appm.setEmployeedraft(0);
                                    appm.setManagerdraft(0);
                                    appm.setAppraisalstatus(request.getParameter("status"));
                                    appm.setReviewstatus(0);
                                    session.saveOrUpdate(appm);
                                }
                            }
                        } else {
                            if (rv == 0 && man == 0) {
                                jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.noappraiserandreviewerassignedforselectedappraisalcycle",null,"No appraiser and reviewer assigned for selected appraisal cycle", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                                return jobj;
                            } else if (rv == 0) {
                                jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.noreviewerassignedforselectedappraisalcycle",null,"No reviewer assigned for selected appraisal cycle", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                                return jobj;
                            } else {
                                jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.noappraiserassignedforselectedappraisalcycle",null,"No appraiser assigned for selected appraisal cycle", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                                return jobj;
                            }
                        }
                    } else {
                        jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalcycleisalreadyinitiatedfortheselectedemployee",null,"Appraisal Cycle is already initiated for the selected employee", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        return jobj;
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.INITIATE_APPRAISAL, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has initiated appraisal of  " + AuthHandler.getFullName(appm.getEmployee()) + " for the appraisal cycle " + appm.getAppcycle().getCyclename(), request);
                    jobj.put("message", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalsuccessfullyintiatedforselectedemployee",null,"Appraisal successfully intiated for selected employee(s)", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                }
            }
        } catch (HibernateException ex) {
            throw new HibernateException("AppraisalAssign");
        } finally {
        }
        return jobj;
    }

    public static void AlltimesheetsApproval(Session session, HttpServletRequest request) throws ServiceException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        int approved = 1;
        try {
            startdate = (Date) fmt.parse(request.getParameter("startdate"));
            enddate = (Date) fmt.parse(request.getParameter("enddate"));
            Object[] obj = {startdate, enddate};
            String managername = AuthHandler.getFullName(session,AuthHandler.getUserid(request));
            String[] ids = request.getParameterValues("empids");
            String str = "";
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    str += ",";
                }
                str += "'" + ids[i] + "'";
            }

            String query = "from Timesheet where userID.userID in(" + str + ") and datevalue between ? and ?";

            List lst = HibernateUtil.executeQuery(session, query, obj);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Timesheet tmsht = (Timesheet) ite.next();
                tmsht.setApproved(approved);
                tmsht.setApprovedby(managername);
                session.save(tmsht);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.TIME_SHEET_APPROVED, "Timesheet of "+AuthHandler.getFullName(tmsht.getUserID())+" from "+startdate+" to "+enddate+" approved by "+AuthHandler.getFullName(session, AuthHandler.getUserid(request)), request);
            }


        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.AlltimesheetsApproval", ex);
        } finally {
        }

    }

    public static void createdates(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        Date d;
        try {
            GregorianCalendar g;

        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.dummyfunction", ex);
        }
    }


    public static String Externalapplicant(Session session, HttpServletRequest request, String subdomain) throws ServiceException, JSONException {
        String result = "";
        JSONObject jobj = new JSONObject();
        String id = "";
        String fname="";
        String lname="";
        String uname="";
        String pass="";
        String ipaddr="";
        try {
            Company cmp = null;
            fname=request.getParameter("fname");
            lname=request.getParameter("lname");
            uname=request.getParameter("u");
            pass=request.getParameter("p");
            if (!StringUtil.isNullOrEmpty(subdomain)) {
                String hql = "from Company where subDomain=?";
                List lst = HibernateUtil.executeQuery(hql, subdomain);
                if (!lst.isEmpty()) {
                    Company cmpt = (Company) lst.get(0);
                    cmp = (Company) session.load(Company.class, cmpt.getCompanyID());
                    String companyid = cmp.getCompanyID();
                    if (StringUtil.isNullOrEmpty(request.getParameter("userid"))) {
                        String SELECT_USER_INFO = "from Jobapplicant where username=? and company.companyID=?";
                        List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{request.getParameter("u"), companyid});
                        if (list.size() == 0) {
                            id = UUID.randomUUID().toString();
                            Jobapplicant jobapp = new Jobapplicant();
                            jobapp.setApplicantid(id);
                            jobapp.setFirstname(fname);
                            jobapp.setLastname(lname);
                            jobapp.setEmail(request.getParameter("e"));
                            jobapp.setAddress1(request.getParameter("c"));
                            jobapp.setContactno(request.getParameter("contact"));
                            jobapp.setUsername(uname);
                            jobapp.setPassword(pass);
                            jobapp.setCompany(cmp);
                            session.save(jobapp);
                            jobj.put("success", true);
                            jobj.put("uri", "./applicantLogin.html");
                            String uri = URLUtil.getPageURL(request, Links.loginpagewthFull, cmp.getSubDomain()) + "applicantLogin.html";
                            String pmsg = String.format(KWLErrorMsgs.msgMailInvite, fname, "Demo",fname,lname, uri, "Demo");
                            String htmlmsg = String.format(HrmsMsgs.msgMailInviteUsernamePassword,fname+" "+lname,cmp.getCreator().getFirstName()+" "+cmp.getCreator().getLastName() ,cmp.getCompanyName(),uname,
                                    pass, uri, uri, "");
                            try {                                                               
                                String email = request.getSession().getAttribute("sysemailid").toString();
                                SendMailHandler.postMail(new String[]{request.getParameter("e")}, "[Deskera] Welcome to Deskera HRMS", htmlmsg, pmsg, email);
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                             AuditAction action = (AuditAction) session.load(AuditAction.class, com.krawler.common.util.AuditAction.CREATE_APPLICANT);
                             String details="Job applicant "+fname+""+lname+" has been signed up through web site.";
                            if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                                ipaddr = request.getRemoteAddr();
                            } else {
                                ipaddr = request.getHeader("x-real-ip");
                            }
                            //@@ProfileHandler.insertAuditLog(session, action, details, ipaddr, cmp.getCreator());
                        } else {
                            jobj.put("failure", 0);
                        }
                        result = jobj.toString();
                    } else {
                        Jobapplicant jobapp = (Jobapplicant) session.load(Jobapplicant.class, request.getParameter("userid"));
                        jobapp.setFirstname(request.getParameter("fname"));
                        jobapp.setLastname(request.getParameter("lname"));
                        jobapp.setEmail(request.getParameter("email"));
                        jobapp.setAddress1(request.getParameter("addr"));
                        jobapp.setContactno(request.getParameter("mobileno"));
                        jobapp.setState(request.getParameter("state"));
                        jobapp.setCountry(request.getParameter("country"));
                        jobapp.setOtheremail(request.getParameter("alternateemail"));
                        jobapp.setMobileno(request.getParameter("phno"));
                        session.save(jobapp);
                        result = "{\"success\":\"true\"}";
                    }
                }
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("Externalapplicant", ex);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("Externalapplicant", ex);
        } finally {
        }
        return result;
    }

    public static void applyforjobexternal(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException, JSONException {
        String status = "Pending";
        Date date1;
        DateFormat formatter;
        String positions = "";
        Jobapplicant applicant = null;
        User user = null;
        String applicantid;
        try {
            formatter = new SimpleDateFormat("MM/dd/yyyy");
            date1 = (Date) formatter.parse(request.getParameter("applydt"));
            Allapplications allapl = null;
            if (StringUtil.isNullOrEmpty(request.getParameter("apcntid"))){
                applicantid=AuthHandler.getUserid(request);
            }else{
                applicantid=request.getParameter("apcntid");
            }
            if (Integer.parseInt(request.getParameter("employeetype")) == 1) {
                user = (User) session.load(User.class, applicantid);
            } else {
                applicant = (Jobapplicant) session.load(Jobapplicant.class, applicantid);
            }
            Company cmp = (Company) session.load(Company.class, AuthHandler.getCompanyid(request));
            CompanyPreferences cmpPref = (CompanyPreferences) session.load(CompanyPreferences.class, cmp.getCompanyID());
            String[] ids = request.getParameterValues("posid");
            for (int i = 0; i < ids.length; i++) {
                Positionmain position = (Positionmain) session.load(Positionmain.class, ids[i]);
                allapl = new Allapplications();
                allapl.setStatus(status);
                allapl.setApplydate(date1);
                if (Integer.parseInt(request.getParameter("employeetype")) == 1) {
                    allapl.setEmployee(user);
                    allapl.setEmployeetype(1);
                } else {
                    allapl.setJobapplicant(applicant);
                    allapl.setEmployeetype(0);
                }
                allapl.setPosition(position);
                allapl.setCompany(cmp);
                allapl.setDelflag(0);
                allapl.setApplicationflag(0);
                allapl.setRejectedbefore(0);
                session.save(allapl);
                positions += "" + position.getJobid() + ":" + position.getPosition().getValue() + ",";
                if (Integer.parseInt(request.getParameter("employeetype")) == 1) {
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.APPLY_FOR_JOB, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has applied for job position " + allapl.getPosition().getJobid(),request);
                } else {
                    if(request.getSession().getAttribute("userid")!=null){
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.APPLY_FOR_JOB, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has applied external applicant " + allapl.getJobapplicant().getFirstname() + " " + allapl.getJobapplicant().getLastname() + " for job position " + allapl.getPosition().getJobid(),request);
                    }
                }
            }
            if (!(Integer.parseInt(request.getParameter("employeetype")) == 1)) {
                String cmpname=cmp.getCompanyName();
                positions = positions.replace(positions.substring(positions.length() - 1), "");
                String pmsg = String.format(HrmsMsgs.jobPlnmsg, (applicant.getFirstname() + " " + applicant.getLastname()),cmpname,cmpname);
                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,allapl.getPosition().getPosition().getValue()+"["+(allapl.getPosition().getJobid()!=null?allapl.getPosition().getJobid():"")+"]", cmpname, cmpname);
                String subject=String.format(HrmsMsgs.jobSubject,(allapl.getPosition().getJobid()!=null?allapl.getPosition().getJobid():""),allapl.getPosition().getPosition().getValue());
                try {
                    SendMailHandler.postMail(new String[]{applicant.getEmail()}, subject, htmlmsg, pmsg, cmp.getEmailID());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException ex) {
            throw new JSONException("applyforjobexternal");
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public static void scheduleinterview(Session session, HttpServletRequest request) throws ServiceException, ParseException, SessionExpiredException {
        Date date1;
        DateFormat formatter;
        String interviewdate;
        boolean reflag=false;
        String htmlmsg="";
        String pmsg="";
        String interviewsub="";
        try {
            String interviewtime=request.getParameter("interviewtime");
            String location=request.getParameter("interviewplace");
            Company cmp=(Company) session.get(Company.class,AuthHandler.getCompanyid(request));
            formatter = new SimpleDateFormat("MM/dd/yyyy");
            date1 = (Date) formatter.parse(request.getParameter("interviewdt"));
            interviewdate=(AuthHandler.getUserDateFormatter(request,session).format(date1));
            Allapplications allapl = null;
            String[] ids = request.getParameterValues("ids");
            String[] emailids=new String[ids.length] ;
            String[] jobs=new String[ids.length] ;
            String[] applicant=new String[ids.length] ;
            for (int i = 0; i < ids.length; i++) {
                allapl = (Allapplications) session.load(Allapplications.class, ids[i]);
                if(allapl.getStatus().equalsIgnoreCase("In Process")){
                    reflag=true;
                }
                allapl.setStatus("In Process");
                allapl.setInterviewdate(date1);
                allapl.setInterviewtime(interviewtime);
                allapl.setInterviewplace(location);
                allapl.setContactperson((User) session.load(User.class, request.getParameter("contactperson")));
                allapl.setInterviewcomment(request.getParameter("interviewcomment"));
                allapl.setRecruiter(request.getParameter("rid"));
                if(Integer.parseInt(request.getParameter("employeetype"))==1){
                    emailids[i]=allapl.getEmployee().getEmailID();
                    applicant[i]=(allapl.getEmployee().getFirstName()+" "+allapl.getEmployee().getLastName());
                }else{
                    emailids[i]=allapl.getJobapplicant().getEmail();
                    applicant[i]=(allapl.getJobapplicant().getFirstname()+" "+allapl.getJobapplicant().getLastname());
                }
                jobs[i]=("["+allapl.getPosition().getJobid()+"] "+allapl.getPosition().getPosition().getValue()+"");
                session.update(allapl);
                String usrnm;
                if(allapl.getEmployee()!=null){
                    usrnm=AuthHandler.getFullName(allapl.getEmployee());
                }
                else{
                    usrnm=allapl.getJobapplicant().getFirstname()+" "+allapl.getJobapplicant().getLastname();
                }
                //@@ProfileHandler.insertAuditLog(session, AuditAction.INTERVIEW_SCHEDULED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has scheduled interview for " + usrnm + " on "+ interviewdate + " at " + allapl.getInterviewplace(),request);
            }
             User usr=(User) session.get(User.class,AuthHandler.getUserid(request));
             Useraccount ua = (Useraccount) session.get(Useraccount.class, usr.getUserID());
                if (!StringUtil.isNullOrEmpty(request.getParameter("mail"))) {
                for (int j = 0; j < emailids.length; j++) {
                    if (reflag) {
                        pmsg = String.format(HrmsMsgs.rescheduleinterviewPlnmsg, applicant[j], jobs[j], cmp.getCompanyName(), interviewdate, interviewtime,
                                location, usr.getContactNumber(), usr.getEmailID(), usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                        htmlmsg = String.format(HrmsMsgs.rescheduleinterviewHTMLmsg, applicant[j], jobs[j], cmp.getCompanyName(), interviewdate, interviewtime,
                                location, usr.getContactNumber(), usr.getEmailID(), usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                        interviewsub = String.format(HrmsMsgs.rescheduleinterviewSubject, allapl.getPosition().getJobid(), allapl.getPosition().getPosition().getValue(), cmp.getCompanyName());
                        try {
                            SendMailHandler.postMail(new String[]{emailids[j]}, interviewsub, htmlmsg, pmsg, usr.getEmailID());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        pmsg = String.format(HrmsMsgs.interviewPlnmsg, jobs[j], interviewdate, interviewtime, location);
                        htmlmsg = String.format(HrmsMsgs.interviewHTMLmsg, applicant[j], jobs[j], cmp.getCompanyName(), interviewdate, interviewtime,
                                location, usr.getContactNumber(), usr.getEmailID(), usr.getFirstName() + " " + usr.getLastName(), ua.getDesignationid() != null ? ua.getDesignationid().getValue() : " ", cmp.getCompanyName());
                        interviewsub = String.format(HrmsMsgs.interviewSubject, allapl.getPosition().getJobid(), allapl.getPosition().getPosition().getValue(), cmp.getCompanyName());
                        try {
                            SendMailHandler.postMail(new String[]{emailids[j]}, interviewsub, htmlmsg, pmsg, usr.getEmailID());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    String[] recruit=request.getParameter("rid").split(",");
                    for(int k=0;k<recruit.length;k++){
                        Recruiter r=(Recruiter)session.get(Recruiter.class,recruit[k]);
                        String interviewer=r.getRecruit().getFirstName()+" "+r.getRecruit().getLastName();
                        String intpmsg = String.format(HrmsMsgs.interviewinvitePlnmsg,interviewer, jobs[j], interviewdate, interviewtime, location);
                        String inthtmlmsg = String.format(HrmsMsgs.interviewinviteHTMLmsg,interviewer, jobs[j],interviewdate, interviewtime,
                            location,usr.getFirstName()+" "+usr.getLastName(),ua.getDesignationid()!=null?ua.getDesignationid().getValue():" ",cmp.getCompanyName());
                        String interviewinvitesub = String.format(HrmsMsgs.interviewinviteSubject, allapl.getPosition().getJobid(), allapl.getPosition().getPosition().getValue(), cmp.getCompanyName());
                        try {
                            SendMailHandler.postMail(new String[]{r.getRecruit().getEmailID()}, interviewinvitesub, inthtmlmsg, intpmsg, usr.getEmailID());
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (ParseException ex) {
            throw ServiceException.FAILURE("scheduleinterview", ex);
        }catch(NullPointerException ex){
         throw ServiceException.FAILURE("scheduleinterview", ex);
        }
        finally {
        }
    }

    public static void adminallapps(Session session, HttpServletRequest request) throws ServiceException {
        try {

            String jsondata = request.getParameter("jsondata");
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                Allapplications appl = (Allapplications) session.load(Allapplications.class, jobj.getString("id"));
                if (StringUtil.isNullOrEmpty(jobj.getString("callback")) == false) {
                appl.setCallback((MasterData) session.load(MasterData.class, jobj.getString("callback")));
            }
                if (StringUtil.isNullOrEmpty(jobj.getString("rank")) == false) {
                appl.setCallback((MasterData) session.load(MasterData.class, jobj.getString("rank")));
            }
                appl.setStatus(jobj.getString("status"));
                session.save(appl);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.GoalInsert", ex);
        } finally {
        }

    }

    public static void Allappsdelete(Session session, HttpServletRequest request) throws ServiceException {
        List tabledata = null;
        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                //Allapplications apps = (Allapplications) session.load(Allapplications.class, ids[i]);
                //session.delete(apps);
                String hql = "from Allapplications where id=?";
                tabledata = HibernateUtil.executeQuery(session, hql, ids[i]);
                if (!tabledata.isEmpty()) {
                    Allapplications log = (Allapplications) tabledata.get(0);
                    log.setDelflag(1);
                    if(log.getEmployee()==null){
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.DELETE_APPLICATION, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted application of " + log.getJobapplicant().getFirstname() + " "+log.getJobapplicant().getLastname(),request);
                    }
                    else{
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.DELETE_APPLICATION, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted application of " + AuthHandler.getFullName(log.getEmployee()),request);
                    }
                    session.update(log);
                }
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.deleteexperience", ex);
        }


    }

    public static void allappsformsave(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Date joiningdate = null;
        DateFormat formatter;
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
            String[] ids = request.getParameterValues("ids");
            String[] positionids = request.getParameterValues("positionids");
            for (int i = 0; i < ids.length; i++) {
                Allapplications appl = (Allapplications) session.load(Allapplications.class, ids[i]);
                Positionmain position = (Positionmain) session.load(Positionmain.class, positionids[i]);
                if (StringUtil.isNullOrEmpty(request.getParameter("callback"))==false) {
                    appl.setCallback((MasterData) session.load(MasterData.class, request.getParameter("callback")));
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("status"))) {
                } else {
                    appl.setStatus(request.getParameter("status"));
                    if (request.getParameter("status").equalsIgnoreCase("Rejected")) {
                        if(appl.getApplicationflag()==1 && position.getPositionsfilled()>0){
                            position.setPositionsfilled(position.getPositionsfilled()-1);
                            position.setDelflag(0);
                            session.update(position);
                        }
                        appl.setApplicationflag(2);
                        appl.setRejectedbefore(1);
                    } else if (request.getParameter("status").equalsIgnoreCase("Selected")) {
                        appl.setApplicationflag(1);
                        if (Boolean.parseBoolean(request.getParameter("changeselected"))) {
                            if(position.getNoofpos()>=position.getPositionsfilled()+1){
                                if ( position.getNoofpos() == position.getPositionsfilled()+1 ) {
                                    position.setDelflag(3);
                                }
                                position.setPositionsfilled(position.getPositionsfilled() + 1);
                            }
                            session.update(position);
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("joiningdate"))) {
                            joiningdate = (Date) formatter.parse(request.getParameter("joiningdate"));
                            appl.setJoiningdate(joiningdate);
                        }
                    } else {
                        if(appl.getApplicationflag()==1 && position.getPositionsfilled()>=0){
                            position.setPositionsfilled(position.getPositionsfilled()-1);
                            position.setDelflag(0);
                            session.update(position);
                        }
                        appl.setApplicationflag(0);
                    }
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("rank"))==false) {
                    appl.setRank((MasterData) session.load(MasterData.class, request.getParameter("rank")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("statuscomment"))) {
                    appl.setStatuscomment(request.getParameter("statuscomment"));
                }
                String usrnm;
                if(appl.getEmployee()!=null){
                    usrnm=AuthHandler.getFullName(appl.getEmployee());
                }
                else{
                    usrnm=appl.getJobapplicant().getFirstname()+" "+appl.getJobapplicant().getLastname();
                }
                session.update(appl);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PROSPECT_EDITED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited prospect of " + usrnm,request);
            }
        } catch (ParseException ex) {
            throw ServiceException.FAILURE("hrmsHandler.allappsformsave", ex);
		} finally {
        }
    }

    public static JSONObject transferappdataFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, UnsupportedEncodingException, NoSuchAlgorithmException, JSONException, JSONException {
        String pass = "1234";
        JSONObject jobj = new JSONObject();
        String hql = "";
        List tabledata = null;
        Integer codeid2 = null;
        User user;
        Useraccount ua;
        boolean flag1=false;
        boolean flag2=false;
        try {
            if (request.getParameter("employeetype").equalsIgnoreCase("External")) {
                if (StringUtil.isNullOrEmpty(request.getParameter("employeeid")) == false) {
                    String[] codeid = (request.getParameter("employeeid")).split("-");
                    for (int x = 0; x < codeid.length; x++) {
                        if (codeid[x].matches("[0-9]*") == true) {
                            codeid2 = Integer.parseInt(codeid[x]);
                        }
                    }
                }
                String hql1 = "from Useraccount where employeeid=? and user.company.companyID=?";
                if (HibernateUtil.executeQuery(session, hql1, new Object[]{codeid2,AuthHandler.getCompanyid(request)}).isEmpty() == true) {
                    flag1=true;
                }
                String hql2 = "from UserLogin where userName=?";
                if (HibernateUtil.executeQuery(session, hql2, request.getParameter("appusername")).isEmpty() == true) {
                    flag2=true;
                }
                //tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("appusername"));
                if (flag1&&flag2) {
                    hql = "from ConfigRecruitmentData where id=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("applicantid"));
                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        ConfigRecruitmentData jobapp = (ConfigRecruitmentData) ite.next();
                        user = new User();
                        ua = new Useraccount();
                        Allapplications app = (Allapplications) session.load(Allapplications.class, request.getParameter("applicationid"));
                        String userid = UUID.randomUUID().toString();
                        UserLogin userLogin = new UserLogin();
                        userLogin.setUserID(userid);
                        userLogin.setUserName(request.getParameter("appusername"));
                        userLogin.setPassword(AuthHandler.getSHA1(pass));
                        user.setFirstName(jobapp.getCol1());
                        user.setLastName(jobapp.getCol2());
                        user.setEmailID(jobapp.getCol3());
                        ua.setUserID(userid);
                        ua.setEmployeeid(codeid2);
                        ua.setSalary("0");
                        user.setCompany((Company) session.load(Company.class, AuthHandler.getCompanyid(request)));
                        ua.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designationid")));
                        ua.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("departmentid")));
                        ua.setRole((Role) session.load(Role.class, "3"));
                        User cre = user.getCompany().getCreator();
                        user.setTimeZone(cre.getTimeZone());
                        user.setDateFormat(cre.getDateFormat());
                        user.setUserLogin(userLogin);
                        jobapp.setDeleted(true);
                        deleteuserappFunction(0, false, request.getParameter("applicantid"), request.getParameter("applicantid"), session, request);
                        session.saveOrUpdate(userLogin);
                        session.saveOrUpdate(user);
                        session.saveOrUpdate(ua);
                        session.update(jobapp);
                        jobj.put("message", true);
                    }
                } else {
                    jobj.put("message", false);
                }
            } else{
                user=(User) session.load(User.class, request.getParameter("applicantid"));
                ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                if(user!=null){
                    deleteuserappFunction(1, false, request.getParameter("applicantid"), request.getParameter("applicantid"), session, request);
                    int histsave = 0;
                    Date saveDate = new Date();
                    SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd");
                    saveDate = new Date(fmt1.format(saveDate));
                    Emphistory ehst = new Emphistory();
                    User updatedby = (User) session.get(User.class, AuthHandler.getUserid(request));
                    if ((MasterData) session.load(MasterData.class, request.getParameter("designationid")) != ua.getDesignationid()) {
                        ehst.setDesignation(ua.getDesignationid());
                        histsave = 1;
                    }
                    ua.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designationid")));
                    if ((MasterData) session.load(MasterData.class, request.getParameter("departmentid")) != ua.getDepartment()) {
                        ehst.setDepartment(ua.getDepartment());
                        if (histsave == 0) {
                            ehst.setDesignation(ua.getDesignationid());
                        }
                        histsave = 2;
                    }
                    ua.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("departmentid")));
                    if (histsave == 1) {
                        ehst.setDepartment(ua.getDepartment());
                    }
                    if (histsave == 1 || histsave == 2) {
                        ehst.setUserid(user);
                        ehst.setEmpid(ua.getEmployeeid());
                        ehst.setUpdatedon(saveDate);
                        ehst.setUpdatedby(updatedby);
                        ehst.setCategory(Emphistory.Emp_Desg_change);
                        session.saveOrUpdate(ehst);
                    }
                    session.update(user);
                    jobj.put("message", true);
                }
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("addnewuserFunction", ex);
        } catch (UnsupportedEncodingException e) {
            throw ServiceException.FAILURE("Auth.getSHA1", e);
        } catch (NoSuchAlgorithmException e) {
            throw ServiceException.FAILURE("Auth.getSHA1", e);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.addnewuserFunction", e);
        }
        return jobj;
    }

    public static void deleteuserappFunction(int employeetype, boolean flag,String applicantid, String applicationid, Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String hql="";
        List tabledata=null;
        try {
            if (employeetype == 0) {
                if (flag) {
                    hql = "from Allapplications where configjobapplicant.id=? and id!=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{applicantid, applicationid});
                } else {
                    hql = "from Allapplications where configjobapplicant.id=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{applicantid});
                }
            } else {
                if (flag) {
                    hql = "from Allapplications where employee.userID=? and id!=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{applicantid, applicationid});
                } else{
                    hql = "from Allapplications where employee.userID=?";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{applicantid});
                }
            }
            Iterator ite = tabledata.iterator();
            while (ite.hasNext()) {
                Allapplications allapp = (Allapplications) ite.next();
                allapp.setDelflag(1);
                session.update(allapp);
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("deleteuserappFunction", ex);
        }
    }

    public static void assignedgoalsdelete(Session session, HttpServletRequest request) throws ServiceException {

        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) session.load(Finalgoalmanagement.class, ids[i]);
                fgmt.setDeleted(true);
                String logtext="User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted " + AuthHandler.getFullName(fgmt.getUserID()) + "'s goal " + fgmt.getGoalname();
                session.saveOrUpdate(fgmt);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_DELETED, logtext ,request);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.deleteGoals", ex);
        }


    }

    public static JSONObject deletetimesheetjobs(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                Timesheet tmst = (Timesheet) session.load(Timesheet.class, ids[i]);
                if (tmst.getApproved() == 1) {
                    jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.timesheetisapproved",null,"Timesheet has been approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                    break;
                } else {
                    session.delete(tmst);
                    jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.timesheetdeletedsuccessfully",null,"Timesheet has been deleted successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                }
            }

        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.deletetimesheetjobs", ex);
        }

        return jobj;
    }

    public static void extusrchngpass(Session session, HttpServletRequest request) throws ServiceException {

        try {
            if (StringUtil.isNullOrEmpty(request.getParameter("pass"))) {
            } else {
                Jobapplicant exapp = (Jobapplicant) session.load(Jobapplicant.class, request.getParameter("userid"));
                exapp.setPassword(request.getParameter("pass"));
                session.saveOrUpdate(exapp);
            }

        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.changepassword", ex);
        }


    }

    public static void editCompFunction(Session session, HttpServletRequest request) throws ServiceException {
        int cmptid = Integer.parseInt(request.getParameter("cmptid"));
        String cmptname = request.getParameter("cmptname");
        String cmptdesc = request.getParameter("cmptdesc");
        int cmptwt = Integer.parseInt(request.getParameter("cmptwt"));

        try {
            Mastercmpt contact = (Mastercmpt) session.load(Mastercmpt.class, cmptid);
            contact.setCmptname(cmptname);
            contact.setCmptdesc(cmptdesc);
            contact.setCmptwt(cmptwt);

            session.update(contact);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void assignCompetencyFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        List tabledata = null;
        String[] comp = request.getParameterValues("item_ids");
        String[] wth=request.getParameterValues("wth");
        try {
            String hql = "from Managecmpt where desig.id=? and mastercmpt.company.companyID=? and delflag=0";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("desid"), AuthHandler.getCompanyid(request)});
            for (int i = 0; i < tabledata.size(); i++) {
                Managecmpt log = (Managecmpt) tabledata.get(i);
                log.setDelflag(1);
                session.update(log);
            }
            for (int i = 0; i < comp.length; i++) {
                Managecmpt contact = new Managecmpt();
                Mastercmpt li1 = (Mastercmpt) session.get(Mastercmpt.class, comp[i]);
                MasterData li2 = (MasterData) session.get(MasterData.class, request.getParameter("desid"));
                contact.setDesig(li2);
                contact.setMastercmpt(li1);
                contact.setWeightage((StringUtil.isNullOrEmpty(wth[i])==false?Integer.parseInt(wth[i]):0));
                contact.setDelflag(0);
                session.save(contact);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.ASSIGN_COMPETENCY, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has assigned competency " + li1.getCmptname() + " to designation " + li2.getValue(),request);
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("assignCompetencyFunction", ex);
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("GetTaxperCatgry", se.COMPANYID_NULL);
        } finally {
        }
    }

    public static void delDesigCompFunction(Session session, HttpServletRequest request) throws ServiceException {
        List tabledata = null;
        try {
            String cmptids[] = request.getParameterValues("appid");
            for (int i = 0; i < cmptids.length; i++) {
                String hql = "from Managecmpt where mid=?";
                tabledata = HibernateUtil.executeQuery(session, hql, cmptids[i]);
                if (!tabledata.isEmpty()) {
                    Managecmpt log = (Managecmpt) tabledata.get(0);
                    log.setDelflag(1);
                    session.update(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject addCompetencyFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, JSONException {
        Mastercmpt contact;
        JSONObject jobj = new JSONObject();
        boolean flag;
        try {
            if (request.getParameter("cmptid").equals("null")) {
                flag = checkaddCompetencyFunction(request.getParameter("cmptname"), session, request);
                if (!flag) {
                    contact = new Mastercmpt();
                    contact.setCmptname(request.getParameter("cmptname"));
                    contact.setCmptdesc(request.getParameter("cmptdesc"));
                    contact.setCmptwt(0);
                    contact.setCompany((Company) session.load(Company.class, AuthHandler.getCompanyid(request)));
                    session.save(contact);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.COMPETENCY_ADDED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new competency " + contact.getCmptname(),request);
                    jobj.put("message",true);
                } else {
                    jobj.put("message",false);
                }
            } else {
                flag = checkaddCompetencyFunction(request.getParameter("cmptname"), session, request);
                if (!flag) {
                    contact = (Mastercmpt) session.load(Mastercmpt.class, request.getParameter("cmptid"));
                    contact.setCmptname(request.getParameter("cmptname"));
                    contact.setCmptdesc(request.getParameter("cmptdesc"));
                    contact.setCmptwt(0);
                    session.update(contact);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.COMPETENCY_EDITED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited competency " + contact.getCmptname(),request);
                    jobj.put("message",true);
                } else {
                    jobj.put("message",false);
                }
            }
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("addCompetencyFunction", se.COMPANYID_NULL);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("addCompetencyFunction", ex);
        }
        return  jobj;
    }

    public static boolean checkaddCompetencyFunction(String competency, Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        boolean result = false;
        List tabledata = null;
        try {
            if (request.getParameter("cmptid").equals("null")) {
                String hql = "from Mastercmpt where cmptname=? and company.companyID=?";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{competency,AuthHandler.getCompanyid(request)});
            } else {
                String hql = "from Mastercmpt where cmptname=? and cmptid != ? and company.companyID=?";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[] {competency, request.getParameter("cmptid"),AuthHandler.getCompanyid(request)});
            }
            if (!tabledata.isEmpty()) {
                result = true;
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("checkaddCompetencyFunction", ex);
        }
        return result;
    }

    public static JSONObject deleteCompFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, JSONException {
        JSONObject jobj = new JSONObject();
        boolean flag;
        try {
            String cmptids[] = request.getParameterValues("cmptid");
            flag = checkdelCompetencyFunction(session, request);
            if (!flag) {
                for (int i = 0; i < cmptids.length; i++) {
                    Mastercmpt item = (Mastercmpt) session.get(Mastercmpt.class, cmptids[i]);
                    String cmpname=item.getCmptname();
                    session.delete(item);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.COMPETENCY_DELETED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted competency " + cmpname,request);
                }
            }
            jobj.put("message", flag);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("deleteCompFunction", ex);
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("deleteCompFunction", se.COMPANYID_NULL);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("deleteCompFunction", ex);
        }
        return jobj;
    }

    public static boolean checkdelCompetencyFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        boolean result = false;
        List tabledata;
        try {
            String cmptids[] = request.getParameterValues("cmptid");
            for (int i = 0; i < cmptids.length; i++) {
                String hql = "from Appraisal where competency.mastercmpt.cmptid=?";
                tabledata = HibernateUtil.executeQuery(session, hql, cmptids[i]);
                if (!tabledata.isEmpty()) {
                    result = true;
                }
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("checkdelCompetencyFunction", ex);
        }
        return result;
    }

    public static void addAgencyFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        try {
            Agency contact = null;
            if (StringUtil.isNullOrEmpty(request.getParameter("agencyid"))) {
                String id = UUID.randomUUID().toString();
                contact = new Agency();
                contact.setAgencyid(id);
                contact.setCompany((Company) session.load(Company.class, AuthHandler.getCompanyid(request)));
                contact.setDelflag(0);
            } else {
                contact = (Agency) session.load(Agency.class, request.getParameter("agencyid"));
            }
            contact.setAgencyname(request.getParameter("agencyname"));
            contact.setAgencyweb(request.getParameter("agencyweb"));
            contact.setReccost(request.getParameter("reccost"));
            contact.setConperson(request.getParameter("conperson"));
            contact.setApprman((User) session.load(User.class, request.getParameter("apprman")));
            contact.setAgencyno(request.getParameter("agencyno"));
            contact.setAgencyadd(request.getParameter("agencyadd"));
            if (StringUtil.isNullOrEmpty(request.getParameter("agencyid"))) {
                session.save(contact);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.ADD_AGENCY, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new agency " + contact.getAgencyname(),request);
            }else{
                session.update(contact);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.EDIT_AGENCY, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited agency " + contact.getAgencyname(),request);
            }
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("addAgencyFunction", se.COMPANYID_NULL);
        } catch (HibernateException ex) {
            throw new HibernateException("addAgencyFunction");
        } finally {
        }
    }

    public static void delAgencyFunction(Session session, HttpServletRequest request) throws ServiceException {
        List tabledata = null;
        int count = 0;
        try {
            String agencyids[] = request.getParameterValues("agencyid");

            for (int i = 0; i < agencyids.length; i++) {
                String hql = "from Agency where agencyid=?";
                tabledata = HibernateUtil.executeQuery(session, hql, agencyids[i]);
                if (!tabledata.isEmpty()) {
                    Agency log = (Agency) tabledata.get(0);
                    log.setDelflag(1);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.DELETE_AGENCY, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted agency " + log.getAgencyname(),request);
                    session.update(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject deletemasterdata(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject msg = new JSONObject();
        String auditID = "";
        String auditMsg = "";
        try {
            User user = (User) session.load(User.class, AuthHandler.getUserid(request));
            String fullName = AuthHandler.getFullName(user);
            String ids[] = request.getParameterValues("ids");
            auditID = com.krawler.common.util.AuditAction.MASTER_DELETED;
            auditMsg = "deleted";
            String groupName="";
            for (int i = 0; i < ids.length; i++) {
                MasterData mdata=(MasterData) session.load(MasterData.class,ids[i] );
                groupName=mdata.getMasterid().getName();
                session.delete(mdata);
            }
            msg.put("data",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.masterdatafielddeletedsuccessfully",null,"Master data field deleted successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+" <b>"+ids.length+"</b> master item(s) of master group <b>"+groupName+"</b>", request);
        } catch (Exception e) {
            e.printStackTrace();
        }
         return  msg ;
    }

    public static void addCommentsfunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        try {
            String goalids[] = request.getParameterValues("goalid");
            Date dt=new Date();
            for (int i = 0; i < goalids.length; i++) {
            User user = (User) session.get(User.class,AuthHandler.getUserid(request));
            Finalgoalmanagement fgid = (Finalgoalmanagement) session.get(Finalgoalmanagement.class,goalids[i]);
            GoalComments gcms = new GoalComments();
            gcms.setComment(request.getParameter("comment"));
            gcms.setGoalid(fgid);
            gcms.setCreatedon(dt);
            gcms.setUserid(user);
            session.save(gcms);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.addCommentsfunction",e);
        }catch(SessionExpiredException e){
            throw ServiceException.FAILURE("hrmsHandler.addCommentsfunction",e);
        }
    }

    public static JSONObject saveempprofile(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException, ParseException {
        //int approved = 0;
        String id;
        JSONObject msg = new JSONObject();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        List tabledata = null;
        try {
            String currentuser = AuthHandler.getUserid(request);
            String userid = request.getParameter("userid");
            String jsondata = request.getParameter("jsondatawk");
            String jsondata1 = request.getParameter("jsondatacad");
            //String jsondata2 = request.getParameter("jsondataprof");
            //String jsondata3 = request.getParameter("jsondataemp");
            Empexperience experi = null;
            Empprofile prof = null;
            UserLogin eapp = (UserLogin) session.load(UserLogin.class, request.getParameter("userid"));
            if (!StringUtil.isNullOrEmpty(request.getParameter("formname")) && request.getParameter("formname").equals("Personal")) {
                try {
                    String query = "from Empprofile where userLogin.userID=?";
                    tabledata = HibernateUtil.executeQuery(session, query, userid);
                    if (tabledata.size() == 0) {
                        id = UUID.randomUUID().toString();
                        prof = new Empprofile();
                        prof.setUserLogin(eapp);
                    } else {
                        prof = (Empprofile) session.load(Empprofile.class, userid);
                    }
                    prof.setMiddlename(request.getParameter("mname"));
//                    prof.setBankacc(request.getParameter("bankacc"));
                    prof.setBankbranch(request.getParameter("bankbranch"));
                    prof.setBankname(request.getParameter("bankname"));
                    prof.setBloodgrp(request.getParameter("bloodgrp"));
                    if (StringUtil.isNullOrEmpty(request.getParameter("childDoB1")) == false) {
                        prof.setChild1DoB(fmt.parse(request.getParameter("childDoB1")));
                    }
                    prof.setChild1name(request.getParameter("child1name"));
                    if (StringUtil.isNullOrEmpty(request.getParameter("childDoB2")) == false) {
                        prof.setChild2DoB(fmt.parse(request.getParameter("childDoB2")));
                    }
                    prof.setChild2name(request.getParameter("child2name"));
                    if (StringUtil.isNullOrEmpty(request.getParameter("DoB")) == false) {
                        prof.setDoB(fmt.parse(request.getParameter("DoB")));
                    }
                    prof.setDrvlicense(request.getParameter("drvlicense"));
                    if (StringUtil.isNullOrEmpty(request.getParameter("exppassport")) == false) {
                        prof.setExppassport(fmt.parse(request.getParameter("exppassport")));
                    }
                    if (StringUtil.isNullOrEmpty(request.getParameter("fatherDoB")) == false) {
                        prof.setFatherDoB(fmt.parse(request.getParameter("fatherDoB")));
                    }
                    prof.setFathername(request.getParameter("fathername"));
                    prof.setGender(request.getParameter("gender"));
                    prof.setMarriage(request.getParameter("marital"));
                    if (StringUtil.isNullOrEmpty(request.getParameter("motherDoB")) == false) {
                        prof.setMotherDoB(fmt.parse(request.getParameter("motherDoB")));
                    }
                    prof.setMothername(request.getParameter("mothername"));
                    prof.setPanno(request.getParameter("panno"));
                    prof.setPassportno(request.getParameter("passportno"));
                    prof.setPfno(request.getParameter("pfno"));
                    if (StringUtil.isNullOrEmpty(request.getParameter("spouseDoB")) == false) {
                        prof.setSpouseDoB(fmt.parse(request.getParameter("spouseDoB")));
                    }
                    prof.setSpousename(request.getParameter("spousename"));
                    prof.setKeyskills(request.getParameter("keyskills"));

                    prof.setUpdated_by(currentuser);
                    prof.setUpdated_on(new Date());
                    if (StringUtil.equal(userid, currentuser)) {
                        prof.setStatus("Pending");
                    } else {
                        prof.setStatus("Approved");
                    }
                    session.save(prof);
                    User u = (User) session.load(User.class, userid);
                    Useraccount ua = (Useraccount) session.load(Useraccount.class, userid);
                    u.setFirstName(request.getParameter("fname"));
                    u.setLastName(request.getParameter("lname"));
                    ua.setSalary(request.getParameter("salarypermonth"));
                    ua.setAccno(request.getParameter("bankacc"));
                    insertConfigData(session, request, "Personal", userid);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PROFILE_EDITED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited " + AuthHandler.getFullName(u) + "'s profile", request);
                    session.saveOrUpdate(u);
                    msg.put("msg", "Profile updated successfully.");
                    msg.put("success", true);
                } catch (Exception e) {
                    System.out.println("Error is" + e);
                }
            } else if (!StringUtil.isNullOrEmpty(request.getParameter("formname")) && request.getParameter("formname").equals("Contact")) {
                String query = "from Empprofile where userLogin.userID=?";
                tabledata = HibernateUtil.executeQuery(session, query, userid);
                if (tabledata.size() == 0) {
                    id = UUID.randomUUID().toString();
                    prof = new Empprofile();
                    prof.setUserLogin(eapp);
                } else {
                    prof = (Empprofile) session.load(Empprofile.class, userid);
                }
                prof.setEmgaddr(request.getParameter("emgaddr"));
                prof.setEmghome(request.getParameter("emghome"));
                prof.setEmgmob(request.getParameter("emgmob"));
                prof.setEmgname(request.getParameter("emgname"));
                prof.setEmgreln(request.getParameter("emgreln"));
                prof.setEmgwork(request.getParameter("emgwork"));
                prof.setLandno(request.getParameter("landno"));
                prof.setMailaddr(request.getParameter("mailaddr"));
                prof.setMobno(request.getParameter("mobno"));
                prof.setPermaddr(request.getParameter("permaddr"));
                prof.setPermcity(request.getParameter("permcity"));
                if (StringUtil.isNullOrEmpty(request.getParameter("permcountry")) == false) {
                    prof.setPermcountry((MasterData) session.load(MasterData.class, request.getParameter("permcountry")));
                }
                prof.setPermstate(request.getParameter("permstate"));
                prof.setPresentaddr(request.getParameter("presentaddr"));
                prof.setPresentcity(request.getParameter("presentcity"));
                if (StringUtil.isNullOrEmpty(request.getParameter("presentcountry")) == false) {
                    prof.setPresentcountry((MasterData) session.load(MasterData.class, request.getParameter("presentcountry")));
                }
                prof.setPresentstate(request.getParameter("presentstate"));
                prof.setWorkno(request.getParameter("workno"));
                prof.setWorkmail(request.getParameter("workmail"));
                prof.setOthermail(request.getParameter("othermail"));
                prof.setWeekoff(request.getParameter("weekoff"));
                prof.setWkstarttime(request.getParameter("starttime"));
                prof.setWkendtime(request.getParameter("endtime"));
                session.save(prof);
                insertConfigData(session, request, "Contact", userid);
                msg.put("msg", "Profile updated successfully.");
                msg.put("success", true);

            } else if (!StringUtil.isNullOrEmpty(request.getParameter("formname")) && request.getParameter("formname").equals("Organizational")) {
                String query = "from Empprofile where userLogin.userID=?";
                tabledata = HibernateUtil.executeQuery(session, query, userid);
                if (tabledata.size() == 0) {
                    id = UUID.randomUUID().toString();
                    prof = new Empprofile();
                    prof.setUserLogin(eapp);
                } else {
                    prof = (Empprofile) session.load(Empprofile.class, userid);
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("reportto")) == false) {
                    prof.setReportto((User) session.load(User.class, request.getParameter("reportto")));
                }
                prof.setEmptype(request.getParameter("emptype"));
                prof.setCommid(request.getParameter("commid"));
                prof.setBranchcode(request.getParameter("branchcode"));
                prof.setBranchaddr(request.getParameter("branchaddr"));
                prof.setBranchcity(request.getParameter("branchcity"));
                if (StringUtil.isNullOrEmpty(request.getParameter("relievedate")) == false) {
                    prof.setRelievedate(fmt.parse(request.getParameter("relievedate")));
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("branchcountry")) == false) {
                    prof.setBranchcountry((MasterData) session.load(MasterData.class, request.getParameter("branchcountry")));
                }
                prof.setProbperiod(request.getParameter("probationmon") + "," + request.getParameter("probationyr"));
                prof.setTrainperiod(request.getParameter("trainingmon") + "," + request.getParameter("trainingyr"));
                prof.setNoticeperiod(request.getParameter("noticemon") + "," + request.getParameter("noticeyr"));
                if (StringUtil.isNullOrEmpty(request.getParameter("confirmdate")) == false) {
                    prof.setConfirmdate(fmt.parse(request.getParameter("confirmdate")));
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("joindate")) == false) {
                    prof.setJoindate(fmt.parse(request.getParameter("joindate")));
                }
                session.save(prof);
                User u = (User) session.load(User.class, userid);
                Useraccount ua = (Useraccount) session.load(Useraccount.class, userid);
                int histsave = 0;
                Date saveDate = new Date();
                SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd");
                saveDate = new Date(fmt1.format(saveDate));
                Emphistory ehst = new Emphistory();
                User updatedby = (User) session.get(User.class, AuthHandler.getUserid(request));
                if (StringUtil.isNullOrEmpty(request.getParameter("designationid")) == false) {
                    if ((MasterData) session.load(MasterData.class, (String) request.getParameter("designationid")) != ua.getDesignationid()) {
                        ehst.setDesignation(ua.getDesignationid());
                        histsave = 1;
                    }
                    ua.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designationid")));
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("department")) == false) {
                    if ((MasterData) session.load(MasterData.class, (String) request.getParameter("department")) != ua.getDepartment()) {
                        ehst.setDepartment(ua.getDepartment());
                        if (histsave == 0) {
                            ehst.setDesignation(ua.getDesignationid());
                        }
                        histsave = 2;
                    }
                    ua.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));

                }
                if (StringUtil.isNullOrEmpty(request.getParameter("empid")) == false) {
                    String[] codeid = (request.getParameter("empid")).split("-");
                    Integer codeid2 = null;
                    for (int x = 0; x < codeid.length; x++) {
                        if (codeid[x].matches("[0-9]*") == true) {
                            codeid2 = Integer.parseInt(codeid[x]);
                        }
                    }
                    String q2 = "from User where employeeid=? and company.companyID=?";
                    List emplst = HibernateUtil.executeQuery(session, q2, new Object[]{codeid2, AuthHandler.getCompanyid(request)});
                    if (emplst.size() > 0) {
                        int getid = ua.getEmployeeid();
                        if (getid != codeid2) {
                            msg.put("msg", "Employee ID is already assigned to another employee");
                        }
                    } else {
                        ua.setEmployeeid(codeid2);
                    }
                    if (histsave == 1) {
                        ehst.setDepartment(ua.getDepartment());
                    }
                    if (histsave == 1 || histsave == 2) {
                        ehst.setUserid(u);
                        ehst.setEmpid(ua.getEmployeeid());
                        ehst.setUpdatedon(saveDate);
                        ehst.setUpdatedby(updatedby);
                        ehst.setCategory(Emphistory.Emp_Desg_change);
                        session.saveOrUpdate(ehst);
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PROFILE_EDITED, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited " + AuthHandler.getFullName(u) + "'s profile", request);
                    session.saveOrUpdate(u);
                }
                insertConfigData(session, request, "Organizational", userid);
                msg.put("msg", "Profile updated successfully.");
                msg.put("success", true);

            } else {
                if (jsondata.length() > 0) {
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
                    if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                        id = UUID.randomUUID().toString();
                        experi = new Empexperience();
                        experi.setUserid(eapp);
                        experi.setType(jobj.getString("type"));
                    } else {
                        experi = (Empexperience) session.load(Empexperience.class, jobj.getString("id"));
                    }
                    experi.setOrganization(jobj.getString("organisation"));
                    experi.setPosition(jobj.getString("position"));
                    experi.setBeginyear(jobj.getString("beginyear"));
                    experi.setEndyear(jobj.getString("endyear"));
                    experi.setComment(jobj.getString("comment"));
                    session.save(experi);
                }
            }
                if (jsondata1.length() > 0) {
                JSONArray jarr1 = new JSONArray("[" + jsondata1 + "]");
                for (int j = 0; j < jarr1.length(); j++) {
                    JSONObject jobj = jarr1.getJSONObject(j);
                    if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                        id = UUID.randomUUID().toString();
                        experi = new Empexperience();
                        experi.setUserid(eapp);
                        experi.setType(jobj.getString("type"));
                    } else {
                        experi = (Empexperience) session.load(Empexperience.class, jobj.getString("id"));
                    }
                experi.setQualification(jobj.getString("qualification"));
                experi.setInstitution(jobj.getString("institution"));
                experi.setYearofgrad(jobj.getString("gradyear"));
                experi.setMarks(jobj.getString("marks"));
                experi.setFrmyear(jobj.getString("yeargrdfrm"));
                experi.setQaulin(jobj.getString("qualificationin"));
                session.save(experi);
                }
            }
                msg.put("msg", "Profile updated successfully.");
                msg.put("success", true);
                }

        } catch (JSONException ex) {
            throw ServiceException.FAILURE("hrmsHandler.EmpProfilesave", ex);
        } catch (SessionExpiredException ex) {
            throw ServiceException.FAILURE("hrmsHandler.EmpProfilesave", ex);
        } catch (ParseException ex) {
            throw ServiceException.FAILURE("hrmsHandler.EmpProfilesave", ex);
        }
        return msg;
    }

    public static void EmpExperiencedelete(Session session, HttpServletRequest request) throws ServiceException {

        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                Empexperience exps = (Empexperience) session.load(Empexperience.class, ids[i]);
                session.delete(exps);

            }

        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.deleteEmpexperience", ex);
        }


    }

    public static void addRecruitersFunction(Session session, HttpServletRequest request) throws ServiceException,SessionException,HibernateException {
       //Status 0=pending,1=accepted,2=rejected,3=Not sent
        List tabledata = null;
        String hql = null;
        Recruiter pos = null;
        try {
             String[] recids=null;
             Company cmpid=(Company)session.get(Company.class,AuthHandler.getCompanyid(request));
             User u=(User)session.get(User.class,AuthHandler.getUserid(request));
            if (StringUtil.isNullOrEmpty(request.getParameter("delrec"))) {
                recids = request.getParameterValues("jobids");
                for (int i = 0; i < recids.length; i++) {
                    hql = "from Recruiter where recruit.userID=? ";
                    tabledata = HibernateUtil.executeQuery(session, hql, recids[i]);
                    if (!tabledata.isEmpty()) {
                        pos = (Recruiter) tabledata.get(0);
                        pos.setDelflag(0);
                        session.update(pos);
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.SET_AS_INTERVIEWER, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has set " + AuthHandler.getFullName(pos.getRecruit()) + " as interviewer",request);
                    } else {
                        User md = (User) session.load(User.class, recids[i]);
                        String id = UUID.randomUUID().toString();
                        Recruiter contact = new Recruiter();
                        contact.setRid(id);
                        contact.setRecruit(md);
                        contact.setDelflag(0);
                        session.save(contact);
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.SET_AS_INTERVIEWER, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has set " + AuthHandler.getFullName(contact.getRecruit()) + " as interviewer",request);
                    }
                }
            } else {
                String[] delrecids = request.getParameterValues("appid");
                for (int i = 0; i < delrecids.length; i++) {
                    String query = "from Recruiter where recruit.userID=?";
                    tabledata = HibernateUtil.executeQuery(session, query, delrecids[i]);
                    if (!tabledata.isEmpty()) {
                        pos = (Recruiter) tabledata.get(0);
                        pos.setDelflag(3);
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.UNASSIGN_INTERVIEWER, "User  " + AuthHandler.getFullName(pos.getRecruit()) + " has been unassigned  as interviewer by " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)),request);
                        session.update(pos);
                    }
                }
            }
            if (StringUtil.isNullOrEmpty(request.getParameter("delrec"))) {
                for(int i=0;i<recids.length;i++){
                  User r=(User)session.get(User.class,recids[i]);
                  Useraccount ua = (Useraccount) session.get(Useraccount.class, r.getUserID());
                  String fullname=AuthHandler.getFullName(r);
                  String uri = URLUtil.getPageURL(request, Links.loginpagewthFull,cmpid.getSubDomain())+"jspfiles/Confirmation.jsp?c="+cmpid.getCompanyID()+"&u="+r.getUserID()+"&acpt=";
                  String pmsg = String.format(HrmsMsgs.interviewerSelectionpln, fullname);
                  String htmlmsg = String.format(HrmsMsgs.interviewerSelectionHTML,fullname,uri+"1",uri+"0",
                          AuthHandler.getFullName(u),ua.getDesignationid().getValue(),cmpid.getCompanyName());
                    try {
                        SendMailHandler.postMail(new String[]{r.getEmailID()}, HrmsMsgs.interviewerSubject, htmlmsg, pmsg,u.getEmailID());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
         catch (HibernateException ex) {
            throw ServiceException.FAILURE("Failure hrms.Handler",ex);
        }
    }

    public static void ApplyforInternalJobs(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        String uname = "";
        String uid = "";
        try {
            uid = AuthHandler.getUserid(request);
            String[] ids = request.getParameterValues("jobids");
            String status = request.getParameter("status");
            if (StringUtil.isNullOrEmpty(request.getParameter("appid"))) {
                for (int i = 0; i < ids.length; i++) {
                    Positionmain pos = (Positionmain) session.load(Positionmain.class, ids[i]);
                    User md = (User) session.load(User.class, uid);
                    Applicants appl = new Applicants();
                    appl.setApplempid(md);
                    appl.setPositionmain(pos);
                    appl.setStatus(status);
                    session.save(appl);
                }

            } else {
                String[] appids = request.getParameterValues("appid");
                for (int i = 0; i < appids.length; i++) {
                    Applicants app = (Applicants) session.load(Applicants.class, appids[i]);
                    if (app.getStatus().equals("checked")) {
                        app.setStatus("unchecked");
                    } else {
                        app.setStatus("checked");
                    }
                    session.update(app);
                }
            }
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("ApplyforInternalJobs", se.USERID_NULL);
        } finally {
        }
    }

    public static void addcompensationFunction(Session session, HttpServletRequest request) throws ServiceException {
        try {
            String userid = AuthHandler.getUserid(request);
            Compensation contact = new Compensation();
            String id = UUID.randomUUID().toString();
            MasterData perfo = (MasterData) session.load(MasterData.class, request.getParameter("prate"));
            MasterData promo = (MasterData) session.load(MasterData.class, request.getParameter("promotion"));
            User li = (User) session.load(User.class, request.getParameter("employeeid"));
            User li2 = (User) session.load(User.class, userid);
            contact.setComid(id);
            contact.setEmpcompen(li);
            contact.setMancompen(li2);
            contact.setComcsal(request.getParameter("currsal"));
            contact.setComnsal(request.getParameter("newsal"));
            contact.setPerformanceid(perfo);
            contact.setCompi(request.getParameter("pinc"));
            contact.setComdi(request.getParameter("dinc"));
            contact.setComdate(request.getParameter("date"));
            contact.setPromotionid(promo);
            session.save(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject createapplicantFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException, ParseException, SessionExpiredException, UnsupportedEncodingException, NoSuchAlgorithmException, MessagingException {
        JSONObject jobj = new JSONObject();
        List list = null;
        String SELECT_USER_INFO;
        Jobapplicant jobapp;
        JSONArray jarr;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
             String pwd=request.getParameter("p");
             String cmp=AuthHandler.getCompanyid(request);
            if (StringUtil.isNullOrEmpty(request.getParameter("update"))) {
                SELECT_USER_INFO = "from Jobapplicant where username=? and company.companyID=? ";
                list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{request.getParameter("u"),cmp});
                if (list.size() == 0) {
                    jobapp = new Jobapplicant();
                    String id = UUID.randomUUID().toString();
                    Company cmpid = (Company) session.load(Company.class, cmp);
                    jobapp.setApplicantid(id);
                    jobapp.setFirstname(request.getParameter("fname"));
                    jobapp.setLastname(request.getParameter("lname"));
                    jobapp.setEmail(request.getParameter("e"));
                    jobapp.setAddress1(request.getParameter("addr"));
                    jobapp.setContactno(request.getParameter("contact"));
                    jobapp.setUsername(request.getParameter("u"));
                    jobapp.setPassword(AuthHandler.getSHA1(pwd));
                    jobapp.setStatus(0);
                    jobapp.setCompany(cmpid);
                    session.save(jobapp);
                    String uri = URLUtil.getPageURL(request, Links.loginpagewthFull,cmpid.getSubDomain())+"applicantLogin.html";
                    String pmsg = String.format(KWLErrorMsgs.msgMailInvite, jobapp.getFirstname(), "Demo", jobapp.getUsername(), pwd, uri, "Demo");
                    String htmlmsg = String.format(HrmsMsgs.msgMailInviteUsernamePassword, jobapp.getFirstname(), AuthHandler.getFullName(session, AuthHandler.getUserid(request)), AuthHandler.getCompanyName(request), jobapp.getUsername(),
                            pwd, uri, uri, "");
                    try {
                        SendMailHandler.postMail(new String[]{jobapp.getEmail()}, "[Deskera] Welcome to Deskera HRMS", htmlmsg, pmsg, "admin.hrms@mailinator.com");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    jobj.put("msg", "Applicant created successfully");
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.CREATE_APPLICANT, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has created new applicant " + jobapp.getFirstname() + " " + jobapp.getLastname(),request);
                } else {
                    jobj.put("msg", "User name already exist");
                }
            } else {
                SELECT_USER_INFO = "from Jobapplicant where applicantid=?";
                list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{request.getParameter("profileid")});
                if (!list.isEmpty()) {
                    String jsondata1 = request.getParameter("jsondata1");
                    String jsondata2 = request.getParameter("jsondata2");
                    String jsondata3 = request.getParameter("jsondata3");
                    String jsondata4 = request.getParameter("jsondata4");
                    String jsondata5 = request.getParameter("jsondata5");
                    jobapp = (Jobapplicant) session.load(Jobapplicant.class, request.getParameter("profileid"));
                    if (jsondata1.length() > 0) {
                        jarr = new JSONArray("[" + jsondata1 + "]");
                        for (int i = 0; i < jarr.length(); i++) {
                            jobj = jarr.getJSONObject(i);
                            jobapp.setTitle(jobj.getString("title"));
                            jobapp.setFirstname(jobj.getString("firstname"));
                            jobapp.setLastname(jobj.getString("lastname"));
                            jobapp.setEmail(jobj.getString("email"));
                            jobapp.setOtheremail(jobj.getString("otheremail"));
                            jobapp.setBirthdate((Date) fmt.parse(jobj.getString("birthdate")));
                        }
                    }
                    if (jsondata2.length() > 0) {
                        jarr = new JSONArray("[" + jsondata2 + "]");
                        for (int i = 0; i < jarr.length(); i++) {
                            jobj = jarr.getJSONObject(i);
                            jobapp.setContactno(jobj.getString("contactno"));
                            jobapp.setMobileno(jobj.getString("mobileno"));
                            jobapp.setCity(jobj.getString("city"));
                            jobapp.setState(jobj.getString("state"));
                            jobapp.setCountryid((MasterData) session.load(MasterData.class, jobj.getString("country")));
                            jobapp.setAddress1(jobj.getString("address1"));
                            jobapp.setAddress2(jobj.getString("address2"));
                        }
                    }
                    if (jsondata3.length() > 0) {
                        jarr = new JSONArray("[" + jsondata3 + "]");
                        for (int i = 0; i < jarr.length(); i++) {
                            jobj = jarr.getJSONObject(i);
                            if (StringUtil.isNullOrEmpty(jobj.getString("graddegree")) == false) {
                                jobapp.setGraddegree(jobj.getString("graddegree"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradspecilization")) == false) {
                                jobapp.setGradspecialization(jobj.getString("gradspecilization"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradcollege")) == false) {
                                jobapp.setGradcollege(jobj.getString("gradcollege"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("graduniversity")) == false) {
                                jobapp.setGraduniversity(jobj.getString("graduniversity"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradepercent")) == false) {
                                jobapp.setGradpercent(jobj.getString("gradepercent"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("gradpassdate")) == false) {
                                jobapp.setGradpassdate((Date) fmt.parse(jobj.getString("gradpassdate")));
                            }


                            if (StringUtil.isNullOrEmpty(jobj.getString("pgqualification")) == false) {
                                jobapp.setPgqualification(jobj.getString("pgqualification"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgspecialization")) == false) {
                                jobapp.setPgspecialization(jobj.getString("pgspecialization"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgcollege")) == false) {
                                jobapp.setPgcollege(jobj.getString("pgcollege"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pguniversity")) == false) {
                                jobapp.setPguniversity(jobj.getString("pguniversity"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgpercent")) == false) {
                                jobapp.setPgpercent(jobj.getString("pgpercent"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("pgpassdate")) == false) {
                                jobapp.setPgpassdate((Date) fmt.parse(jobj.getString("pgpassdate")));
                            }

                            if (StringUtil.isNullOrEmpty(jobj.getString("othername")) == false) {
                                jobapp.setOthername(jobj.getString("othername"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherqualification")) == false) {
                                jobapp.setOtherqualification(jobj.getString("otherqualification"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherpercent")) == false) {
                                jobapp.setOtherpercent(jobj.getString("otherpercent"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherdetails")) == false) {
                                jobapp.setOtherdetails(jobj.getString("otherdetails"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("otherpassdate")) == false) {
                                jobapp.setOtherpassdate((Date) fmt.parse(jobj.getString("otherpassdate")));
                            }
                        }
                    }
                    if (jsondata4.length() > 0) {
                        jarr = new JSONArray("[" + jsondata4 + "]");
                        for (int i = 0; i < jarr.length(); i++) {
                            jobj = jarr.getJSONObject(i);
                            if (jobj.getString("experiencemonth").equals("")) {
                                jobapp.setExperiencemonth(0);
                            } else {
                                jobapp.setExperiencemonth(Integer.parseInt(jobj.getString("experiencemonth")));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("experienceyear")) == false) {
                                jobapp.setExperienceyear(Integer.parseInt(jobj.getString("experienceyear")));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("functionalexpertise")) == false) {
                                jobapp.setFunctionalexpertise(jobj.getString("functionalexpertise"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("currentindustry")) == false) {
                                jobapp.setCurrentindustry(jobj.getString("currentindustry"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("currentorganization")) == false) {
                                jobapp.setCurrentorganization(jobj.getString("currentorganization"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("currentdesignation")) == false) {
                                jobapp.setCurrentdesignation(jobj.getString("currentdesignation"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("grosssalary")) == false) {
                                jobapp.setGrosssalary(Integer.parseInt(jobj.getString("grosssalary")));
                            }
                            jobapp.setExpectedsalary(Integer.parseInt(jobj.getString("expectedsalary")));
                        }
                    }
                    if (jsondata5.length() > 0) {
                        jarr = new JSONArray("[" + jsondata5 + "]");
                        for (int i = 0; i < jarr.length(); i++) {
                            jobj = jarr.getJSONObject(i);
                            jobapp.setKeyskills(jobj.getString("keyskills"));
                            if (StringUtil.isNullOrEmpty(jobj.getString("category")) == false) {
                                jobapp.setCategory(jobj.getString("category"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("filepath")) == false) {
                                jobapp.setFilepath(jobj.getString("filepath"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("companyrelative")) == false) {
                                jobapp.setCompanyrelative(jobj.getString("companyrelative"));
                            }
                            if (StringUtil.isNullOrEmpty(jobj.getString("appearedbefore")) == false) {
                                jobapp.setAppearedbefore(jobj.getString("appearedbefore"));
                                if(jobj.getString("appearedbefore").equalsIgnoreCase("yes")){
                                    if (StringUtil.isNullOrEmpty(jobj.getString("interviewplace")) == false) {
                                        jobapp.setInterviewplace(jobj.getString("interviewplace"));
                                    }
                                    if (StringUtil.isNullOrEmpty(jobj.getString("interviewdate")) == false) {
                                        jobapp.setInterviewdate((Date) fmt.parse(jobj.getString("interviewdate")));
                                    }
                                    if (StringUtil.isNullOrEmpty(jobj.getString("interviewposition")) == false) {
                                        jobapp.setInterviewposition((MasterData) session.load(MasterData.class, jobj.getString("interviewposition")));
                                    }
                                }
                            }
                            jobapp.setInterviewlocation(jobj.getString("interviewlocation"));
                            session.update(jobapp);
                        }
                    }
                }
                jobj.put("msg", "Applicant updated successfully.");
            }

        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("createapplicantFunction", se.COMPANYID_NULL);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("createapplicantFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("createapplicantFunction");
//        } catch (NoSuchAlgorithmException ex) {
//            throw new JSONException("createapplicantFunction");
        } catch (ParseException ex) {
            throw new JSONException("createapplicantFunction");
//        } catch (UnsupportedEncodingException ex) {
//            throw new JSONException("createapplicantFunction");
        } finally {
        }
        return jobj;

    }

    public static void addjobprofileFunction(Session session, HttpServletRequest request) throws ServiceException {
        String id;
        try {
            String jsondata1 = request.getParameter("jsondataresp");
            String jsondata2= request.getParameter("jsondataskill");
            String jsondata3 = request.getParameter("jsondataqual");
            Jobprofile job = null;
            Positionmain pos = (Positionmain) session.load(Positionmain.class, request.getParameter("position"));
            JSONArray jarr = new JSONArray("[" + jsondata1 + "]");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                    id = UUID.randomUUID().toString();
                    job = new Jobprofile();
                    job.setId(id);
                } else {
                    job = (Jobprofile) session.load(Jobprofile.class, jobj.getString("id"));
                }
                job.setResponsibility(jobj.getString("responsibility"));
                job.setPosition(pos);
                job.setType(Integer.parseInt(jobj.getString("type")));
                session.save(job);
            }

            jarr = new JSONArray("[" + jsondata2 + "]");
            for (int j = 0; j < jarr.length(); j++) {
                JSONObject jobj = jarr.getJSONObject(j);
                if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                    id = UUID.randomUUID().toString();
                    id = UUID.randomUUID().toString();
                    job = new Jobprofile();
                    job.setId(id);
                } else {
                    job = (Jobprofile) session.load(Jobprofile.class, jobj.getString("id"));
                }
                job.setSkill(jobj.getString("skill"));
                job.setSkilldesc(jobj.getString("skilldesc"));
                job.setPosition(pos);
                job.setType(Integer.parseInt(jobj.getString("type")));
                session.save(job);
            }

            jarr = new JSONArray("[" + jsondata3 + "]");
            for (int j = 0; j < jarr.length(); j++) {
                JSONObject jobj = jarr.getJSONObject(j);
                if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                    id = UUID.randomUUID().toString();
                    id = UUID.randomUUID().toString();
                    job = new Jobprofile();
                    job.setId(id);
                } else {
                    job = (Jobprofile) session.load(Jobprofile.class, jobj.getString("id"));
                }
                job.setQualification(jobj.getString("qualification"));
                job.setQualificationdesc(jobj.getString("qualificationdesc"));
                job.setPosition(pos);
                job.setType(Integer.parseInt(jobj.getString("type")));
                session.save(job);
            }
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("addjobprofileFunction",ex);
        }finally {
        }
    }

     public static JSONObject saveReportTemplate(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = null;
        try {
            String jsondata = request.getParameter("data");
            String userid = request.getParameter("userid");
            String name = request.getParameter("name");
            String desc = request.getParameter("desc");

            jobj = new JSONObject();
            PDFReportTemplate proj_temp = new PDFReportTemplate();
            proj_temp.setName(name);
            proj_temp.setDescription(desc);
            proj_temp.setConfiguration(jsondata);
            proj_temp.setUser((User) session.get(User.class, userid));
            session.save(proj_temp);
            jobj.put("success",true);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.saveReportTemplate", e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.saveReportTemplate", e);
        }
        return jobj;
    }
      public static JSONObject getAllReportTemplate(Session session, HttpServletRequest request) throws ServiceException{
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String userid = AuthHandler.getUserid(request);
            String Hql = "select p from PDFReportTemplate p where p.user.userID=? ";

            List entityList = HibernateUtil.executeQuery(session, Hql, new Object[]{userid});
            Iterator ite = entityList.iterator();
            while (ite.hasNext()) {
                PDFReportTemplate obj = (PDFReportTemplate) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("tempid",obj.getID());
                jtemp.put("tempname",obj.getName());
                jtemp.put("description",obj.getDescription());
                jtemp.put("configstr",obj.getConfiguration());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("hrmsHandler.getAllReportTemplate", ex);
        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public static JSONObject deleteReportTemplate(Session session, HttpServletRequest request) throws ServiceException {
            JSONObject jobj = new JSONObject();
         try {
              String tempid = request.getParameter("deleteflag");
              PDFReportTemplate tmst = (PDFReportTemplate) session.load(PDFReportTemplate.class, tempid);
              session.delete(tmst);
              jobj.put("success",true);
              jobj.put("msg", "Template deleted successfully");
        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } 
        return jobj;
    }

      public static void deleteApplicants(Session session, HttpServletRequest request) throws ServiceException,HibernateException, SessionExpiredException {
        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                Jobapplicant exps = (Jobapplicant) session.load(Jobapplicant.class, ids[i]);
                exps.setUsername(exps.getUsername()+"_del");
                exps.setDeleted(true);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.DELETE_APPLICANT, "User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted applicant " + exps.getFirstname() + " " + exps.getLastname(),request);
                session.saveOrUpdate(exps);
              }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.deleteApplicants", e);
        }
    }

   public static JSONObject appraisalFunction(Session session, HttpServletRequest request) throws ServiceException, ParseException, JSONException, SessionExpiredException {
        List tabledata = null;
        JSONObject msgjobj=new JSONObject();
        User u = null;
        boolean flag=true;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Appraisalmanagement appmanage=null;
            Appraisal app=null;
            if (StringUtil.isNullOrEmpty(request.getParameter("rateperformance"))) {
                String jsondata1 = request.getParameter("jsoncompetency");
                String jsondata2 = request.getParameter("jsongoal");
                String [] appids=request.getParameter("appraisalid").split(",");
                for(int k=0;k<appids.length;k++){
                String hql = "from Appraisalmanagement where appraisalid=?";
                tabledata = HibernateUtil.executeQuery(session, hql, appids[k]);
                    if (tabledata.isEmpty()) {
                        appmanage = new Appraisalmanagement();
                        flag = true;
                    } else {
                        appmanage = (Appraisalmanagement) session.load(Appraisalmanagement.class, appids[k]);
                        String appcycleid = appmanage.getAppcycle().getId();
                        String employeeid = appmanage.getEmployee().getUserID();
                        String query = "from Appraisalmanagement where appcycle.id=? and employee.userID=? and reviewstatus=2 and (not date(now()) between appcycle.submitstartdate and appcycle.submitenddate)";
                        List list = HibernateUtil.executeQuery(session, query, new Object[]{appcycleid, employeeid});
                        if (!list.isEmpty()) {
                                flag = false;
                            }
                        }
                    if (flag) {
                        if (Boolean.parseBoolean(request.getParameter("employee"))) {
                            appmanage.setEmployeecomment(request.getParameter("empcomment"));
                            if (!Boolean.parseBoolean(request.getParameter("saveasDraft"))) {
                                appmanage.setEmployeestatus(1);
                                appmanage.setEmployeedraft(0);
                            } else {
                                appmanage.setEmployeedraft(1);
                            }
                            appmanage.setEmployeecompscore(Double.parseDouble(request.getParameter("competencyscore")));
                            appmanage.setEmployeegoalscore(Double.parseDouble(request.getParameter("goalscore")));
                            appmanage.setEmployeegapscore(Double.parseDouble(request.getParameter("compgapscore")));
                            appmanage.setEmployeesubmitdate((Date) fmt.parse(request.getParameter("submitdate")));
                        } else {
                            appmanage.setManagercomment(request.getParameter("mancomment"));
                            if (!Boolean.parseBoolean(request.getParameter("saveasDraft"))) {
                                appmanage.setManagerstatus(1);
                                appmanage.setManagerdraft(0);
                                appmanage.setAppraisalstatus("submitted");
                                //@@ProfileHandler.insertAuditLog(session,AuditAction.APPRAISAL_SUBMITTED,"Appraiser " + AuthHandler.getFullName(appmanage.getManager()) + " has submitted appraisal for " + AuthHandler.getFullName(appmanage.getEmployee()) + " for the appraisal cycle " + appmanage.getAppcycle().getCyclename(), request);
                            } else {
                                appmanage.setAppraisalstatus("pending");
                                appmanage.setManagerdraft(1);
                            }
                            appmanage.setReviewstatus(0);
                            if (!StringUtil.isNullOrEmpty(request.getParameter("performance"))) {
                                appmanage.setPerformance((MasterData) session.load(MasterData.class, request.getParameter("performance")));
                            }
                            appmanage.setManagercompscore(Double.parseDouble(request.getParameter("competencyscore")));
                            appmanage.setManagergoalscore(Double.parseDouble(request.getParameter("goalscore")));
                            appmanage.setManagergapscore(Double.parseDouble(request.getParameter("compgapscore")));
                            appmanage.setManagersubmitdate((Date) fmt.parse(request.getParameter("submitdate")));
                            if (Boolean.parseBoolean(request.getParameter("salarychange"))) {
                                appmanage.setSalaryrecommend(1);
                                if (!StringUtil.isNullOrEmpty(request.getParameter("newdesignation"))) {
                                    appmanage.setNewdesignation((MasterData) session.load(MasterData.class, request.getParameter("newdesignation")));
                                } else {
                                    appmanage.setNewdesignation(null);
                                }
                                if (!StringUtil.isNullOrEmpty(request.getParameter("newdepartment"))) {
                                    appmanage.setNewdepartment((MasterData) session.load(MasterData.class, request.getParameter("newdepartment")));
                                } else {
                                    appmanage.setNewdepartment(null);
                                }
                                if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                                    appmanage.setSalaryincrement(Float.parseFloat(request.getParameter("salaryincrement")));
                                } else {
                                    appmanage.setSalaryincrement(0);
                                }
                            } else {
                                appmanage.setSalaryrecommend(0);
                            }
                        }
                        if (appmanage.getEmployeestatus() == 1 && appmanage.getManagerstatus() == 1) {
                            appmanage.setAppraisalstatus("submitted");
                            appmanage.setReviewstatus(0);
                            appmanage.setTotalscore((appmanage.getEmployeegoalscore() + appmanage.getEmployeecompscore() + appmanage.getManagergoalscore() + appmanage.getManagercompscore()) / 4);
                        }
                        session.save(appmanage);
                        String cmp = "from Appraisal where appraisal.appraisalid=? and competency!=null";
                        List cmplst = HibernateUtil.executeQuery(session, cmp, appids[k]);
                        JSONArray jarr = new JSONArray("[" + jsondata1 + "]");
                        for (int i = 0; i < jarr.length(); i++) {
                            JSONObject jobj = jarr.getJSONObject(i);
                            if(cmplst.size()>0){
                                app = (Appraisal) cmplst.get(i);
                            }else{
                                app = new Appraisal();
                            }
                            if (Boolean.parseBoolean(request.getParameter("employee"))) {
                                if (!StringUtil.isNullOrEmpty(jobj.getString("compemprate"))) {
                                    app.setCompemprating(Double.parseDouble(jobj.getString("compemprate")));
                                } else {
                                    app.setCompemprating(0);
                                }
                                app.setCompempgap(Double.parseDouble(jobj.getString("compempgap")));
                                app.setCompempcomment(jobj.getString("compempcomment"));
                            } else {
                                if (!StringUtil.isNullOrEmpty(jobj.getString("compmanrate"))) {
                                    app.setCompmanrating(Double.parseDouble(jobj.getString("compmanrate")));
                                } else {
                                    app.setCompmanrating(0);
                                }
                                app.setCompmangap(Double.parseDouble(jobj.getString("compmangap")));
                                app.setCompmancomment(jobj.getString("compmancomment"));
                            }
                            app.setAppraisal((Appraisalmanagement) session.load(Appraisalmanagement.class, appids[k]));
                            app.setCompetency((Managecmpt) session.load(Managecmpt.class, jobj.getString("mid")));
                            session.save(app);
                        }

                        jarr = new JSONArray("[" + jsondata2 + "]");
                        for (int j = 0; j < jarr.length(); j++) {
                            JSONObject jobj = jarr.getJSONObject(j);
                            if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                                app = new Appraisal();
                            } else {
                                app = (Appraisal) session.load(Appraisal.class, jobj.getString("id"));
                            }
                            if (Boolean.parseBoolean(request.getParameter("employee"))) {
                                if (StringUtil.equal(appids[k], jobj.getString("goalapprid"))) {
                                    if (!StringUtil.isNullOrEmpty(jobj.getString("goalemprate"))) {
                                        app.setGoalemprating(Double.parseDouble(jobj.getString("goalemprate")));
                                    } else {
                                        app.setGoalemprating(0);
                                    }
                                    app.setGoalempcomment(jobj.getString("goalempcomment"));
                                    app.setAppraisal((Appraisalmanagement) session.load(Appraisalmanagement.class, appids[k]));
                                    app.setGoal((Finalgoalmanagement) session.load(Finalgoalmanagement.class, jobj.getString("goalid")));
                                    session.save(app);
                                }
                            } else {
                                if (!StringUtil.isNullOrEmpty(jobj.getString("goalmanrate"))) {
                                    app.setGoalmanrating(Double.parseDouble(jobj.getString("goalmanrate")));
                                } else {
                                    app.setGoalmanrating(0);
                                }
                                app.setGoalmancomment(jobj.getString("goalmancomment"));
                                app.setAppraisal((Appraisalmanagement) session.load(Appraisalmanagement.class, appids[k]));
                                app.setGoal((Finalgoalmanagement) session.load(Finalgoalmanagement.class, jobj.getString("goalid")));
                                session.save(app);
                            }
                        }
                    } else{
                        break;
                    }
                }
                if (Boolean.parseBoolean(request.getParameter("employee"))&&!Boolean.parseBoolean(request.getParameter("saveasDraft"))) {
                    //@@ProfileHandler.insertAuditLog(session,AuditAction.APPRAISAL_SUBMITTED,"Employee " + AuthHandler.getFullName(appmanage.getEmployee())+" has submitted appraisal for the appraisal cycle " + appmanage.getAppcycle().getCyclename(), request);
                }
            } else {
                String performance[] = request.getParameterValues("item_prate");
                String finalid[] = request.getParameterValues("item_fid");
                for (int i = 0; i < finalid.length; i++) {
                    String query = "from Appraisalmanagement where appraisalid=?";
                    tabledata = HibernateUtil.executeQuery(session, query, finalid[i]);
                    MasterData perfo = (MasterData) session.load(MasterData.class, performance[i]);
                    if (!tabledata.isEmpty()) {
                        appmanage = (Appraisalmanagement) tabledata.get(0);
                        appmanage.setPerformance(perfo);
                        appmanage.setAppraisalstatus("submitted");
                        appmanage.setReviewstatus(0);
                        u = appmanage.getEmployee();
                        session.update(appmanage);
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.APPERAISAL_DONE, "Appraisal of " + (u != null ? AuthHandler.getFullName(u) : "") + " done by " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)), request);
                    }
                }
            }
            msgjobj.put("message",flag);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("appraisalFunction", ex);
        } catch (ParseException ex) {
            throw new JSONException("appraisalFunction");
//        } catch (SessionExpiredException e) {
//            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (HibernateException ex) {
            throw new HibernateException("appraisalFunction");
        } finally {
        }
        return msgjobj;
    }
   public static JSONObject setConfirmation(Session session, HttpServletRequest request,String cmp,String user) throws ServiceException,HibernateException, JSONException {
       JSONObject jobj=new JSONObject();
       try {
           Integer accept=Integer.parseInt(request.getParameter("acpt"));
            String hql="from Recruiter where recruit.userID=? and recruit.company.companyID=? and allapplication is null";
            List lst=HibernateUtil.executeQuery(session, hql,new Object[]{user,cmp});
            if(lst.size()==1){
              Recruiter r=(Recruiter)lst.get(0);
              int statusFlag = r.getDelflag();
              if(statusFlag == 0) {
                  String ipaddr = "";
                  if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                      ipaddr = request.getRemoteAddr();
                  } else {
                      ipaddr = request.getHeader("x-real-ip");
                  }
                  User usr = (User) session.get(User.class, user);
                  if(accept==1){
                   r.setDelflag(1);
                   AuditAction action = (AuditAction) session.load(AuditAction.class, com.krawler.common.util.AuditAction.ACCEPT_AS_INTERVIEWER);
                   //@@ProfileHandler.insertAuditLog(session, action, "User  " + usr.getFirstName() + " " + usr.getLastName() + " has accepted interviewer position", ipaddr, usr);
                  }else{
                   r.setDelflag(2);
                   AuditAction action = (AuditAction) session.load(AuditAction.class, com.krawler.common.util.AuditAction.REJECT_AS_INTERVIEWER);
                   //@@ProfileHandler.insertAuditLog(session, action, "User  " + usr.getFirstName() + " " + usr.getLastName() + " has rejected interviewer position", ipaddr, usr);
                  }
                  session.save(r);
                  if(accept==1){
                      Company cmpid=(Company)session.get(Company.class,cmp);
                      String fullname=r.getRecruit().getFirstName()+" "+r.getRecruit().getLastName();
                      User u=cmpid.getCreator();
                      Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                      String pmsg = String.format(HrmsMsgs.Thankspln,fullname);
                      String htmlmsg = "";
                      if(ua.getDesignationid()==null)
                        htmlmsg = String.format(HrmsMsgs.ThanksHTML,fullname,u.getFirstName()+" "+u.getLastName(),cmpid.getCompanyName(),"");
                      else
                        htmlmsg = String.format(HrmsMsgs.ThanksHTML,fullname,u.getFirstName()+" "+u.getLastName(),ua.getDesignationid().getValue(),cmpid.getCompanyName());
                      try {
                          SendMailHandler.postMail(new String[]{r.getRecruit().getEmailID()}, HrmsMsgs.ThanksSubject, htmlmsg, pmsg,u.getEmailID());
                      } catch (MessagingException e) {
                          e.printStackTrace();
                      }
                  }
                  
                  jobj.put("success", true);
                  jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.operationhasbeendonesuccessfully",null,"Operation has been done successfully11.", LocaleUtil.getCompanyLocale(cmp,0)));
              } else {
                  jobj.put("success", false);
                  jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.alreadyacceptedrejectedinviation",null,"It seems you've already accepted/rejected the invitation. Please send an email to support@deskera.com if this message is incorrect.", LocaleUtil.getCompanyLocale(cmp,0)));
              }
            }else{
               jobj.put("success", false);
               jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.itseemsyouarenotrequestedasrecruiter",null,"It seems you are not requested as recruiter. Please send an email to support@deskera.com if this message is incorrect.", LocaleUtil.getCompanyLocale(cmp,0)));
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.setConfirmation", e);
        }catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.setConfirmation", e);
        }catch (Exception e) {
            throw ServiceException.FAILURE("hrmsHandler.setConfirmation", e);
        }
        return jobj;
    }

   public static boolean isReviewer(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        boolean result = false;
        List tabledata;
        String hql;
        try {
            hql = "from Assignreviewer where reviewer.userID=? and reviewerstatus=1";
            tabledata = HibernateUtil.executeQuery(session, hql, AuthHandler.getUserid(request));
            Iterator ite = tabledata.iterator();
            String users = "";
            while (ite.hasNext()) {
                Assignreviewer log = (Assignreviewer) ite.next();
                users += "'" + log.getEmployee().getUserID() + "',";
            }
            if (users.length() > 0) {//review link will be shown only after end date of submission
                users = users.substring(0, users.length() - 1);
                Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
                hql = "from Appraisalmanagement appm where appm.employee.userID in (" + users + ") and appm.appcycle.submitenddate<?";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{userdate});
                if (!tabledata.isEmpty()) {
                    result = true;
                }
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("isReviewer", ex);
        }
        return result;
    }

    public static JSONObject reviewappraisalFunction(Session session, HttpServletRequest request) throws ServiceException, HibernateException, JSONException {
       JSONObject jobj=new JSONObject();
        try {
            String[] appraisalids = request.getParameterValues("appraisalids");
            String[] employeeids = request.getParameterValues("employeeids");
            String[] appcycleids = request.getParameterValues("appcycleid");
            float salary;
            float salaryincrement = 0;
            String employeeid = employeeids[0];
            for (int i = 0; i < appraisalids.length; i++) {
                Appraisalmanagement app = (Appraisalmanagement) session.load(Appraisalmanagement.class, appraisalids[i]);
                if(app.getReviewstatus()!=2){
                    User user = (User) session.load(User.class, employeeid);
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                    app.setOriginaldesignation(ua.getDesignationid());
                    app.setOriginaldepartment(ua.getDepartment());
                    if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) {
                        app.setReviewstatus(2);
                        app.setAppraisalstatus("submitted");
                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                        app.setReviewercomment(request.getParameter("reviewercomment"));
                        }
                        app.setEmployeestatus(1);
                        app.setManagerstatus(1);
                        app.setReviewersubmitdate(new Date());
                        if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                            app.setReviewdepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
                            app.setReviewdesignation((MasterData) session.load(MasterData.class, request.getParameter("designation")));
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                            app.setReviewsalaryincrement(Float.parseFloat(request.getParameter("salaryincrement")));
                            }else{
                                  app.setReviewsalaryincrement(0);
                            }
                    } else {
                        app.setManagerstatus(0);
                        app.setReviewstatus(1);
                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                            app.setReviewercomment(request.getParameter("reviewercomment"));
                        }
                        app.setAppraisalstatus("pending");
                    }
                    session.update(app);
                }else{
                    jobj.put("success",true);
                    jobj.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalisalreadyapproved ",null,"Appraisal has been already approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));//"Appraisal is already approved ");
                    return jobj;
                }
        }
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) {
                User user = (User) session.load(User.class, employeeid);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                if (StringUtil.isNullOrEmpty(ua.getSalary())) {
                    salary = 0;
                } else {
                    salary = Float.parseFloat(ua.getSalary());
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                    salaryincrement = Float.parseFloat(request.getParameter("salaryincrement"));
                } else {
                    salaryincrement = 0;
                }
                salary = salary + (salary * salaryincrement) / 100;
                ua.setSalary("" + salary);
                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                    ua.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
                    ua.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designation")));
                }
                session.update(user);
            }
             jobj.put("success",true);
             jobj.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalstatuschangedsuccessfully",null,"Appraisal status changed successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
             return jobj;
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewappraisalFunction", e);
        }catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewappraisalFunction", e);
        }
    }

    public static String reviewanonymousAppraisal(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String employeeid = request.getParameter("employeeid");
            String appcycleid = request.getParameter("appraisalcycleid");
            float salary;
            float salaryincrement = 0;
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) { //To approve request
                String hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and managerstatus=1";
                List list = HibernateUtil.executeQuery(session, hql, new Object[]{employeeid, appcycleid});
                Iterator ite = list.iterator();
                while (ite.hasNext()) {//Change status of all submitted request
                    Appraisalmanagement app = (Appraisalmanagement) ite.next();
                    if (app.getReviewstatus() != 2) {
                        User user = (User) session.load(User.class, employeeid);
                        Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                        app.setOriginaldesignation(ua.getDesignationid());
                        app.setOriginaldepartment(ua.getDepartment());
                        app.setReviewstatus(2);
                        app.setAppraisalstatus("submitted");
                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                            app.setReviewercomment(request.getParameter("reviewercomment"));
                        }
                        app.setEmployeestatus(1);
                        app.setManagerstatus(1);
                        app.setReviewersubmitdate(new Date());
                        if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                            app.setReviewdepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
                            app.setReviewdesignation((MasterData) session.load(MasterData.class, request.getParameter("designation")));
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                            app.setReviewsalaryincrement(Float.parseFloat(request.getParameter("salaryincrement")));
                        } else {
                            app.setReviewsalaryincrement(0);
                        }
                        session.update(app);
                    } else {
                        jobj.put("success", true);
                        jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalisalreadyapproved ",null,"Appraisal is already approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        return jobj.toString();
                    }
                }
            } else { //To unapprove the request
                String[] appraisalids = request.getParameterValues("appraisalids");
                for (int i = 0; i < appraisalids.length; i++) {
                    Appraisalmanagement app = (Appraisalmanagement) session.load(Appraisalmanagement.class, appraisalids[i]);
                    if (app.getReviewstatus() != 2) {
                        app.setManagerstatus(0);
                        app.setReviewstatus(1);
                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                            app.setReviewercomment(request.getParameter("reviewercomment"));
                        }
                        app.setAppraisalstatus("pending");
                    } else {
                        jobj.put("success", true);
                        jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalisalreadyapproved ",null,"Appraisal is already approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        return jobj.toString();
                    }
                }
            }
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) { //Update user information
                User user = (User) session.load(User.class, employeeid);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                if (StringUtil.isNullOrEmpty(ua.getSalary())) {
                    salary = 0;
                } else {
                    salary = Float.parseFloat(ua.getSalary());
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                    salaryincrement = Float.parseFloat(request.getParameter("salaryincrement"));
                } else {
                    salaryincrement = 0;
                }
                salary = salary + (salary * salaryincrement) / 100;
//                user.setSalary("" + salary);
                
                int histsave=0;
                Date saveDate = new Date();
                SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd");
                saveDate = new Date(fmt1.format(saveDate));
                Emphistory ehst=new Emphistory();
//                User updatedby=(User)session.get(User.class,AuthHandler.getUserid(request));
                if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
//                    if((MasterData) session.load(MasterData.class, (String) request.getParameter("designation"))!=user.getDesignationid()){
//                        ehst.setDesignation(user.getDesignationid());
                        histsave=1;
//                    }
//                    user.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designation")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
//                    if((MasterData) session.load(MasterData.class, (String) request.getParameter("department"))!=user.getDepartment()){
//                        ehst.setDepartment(user.getDepartment());
//                        if(histsave==0) {
//                            ehst.setDesignation(user.getDesignationid());
//                        }
                        histsave=2;
//                    }
//                    user.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
                }
//                if(histsave==1){
//                    ehst.setDepartment(user.getDepartment());
//                }
//                if(histsave==1||histsave==2) {
//                    ehst.setUserid(user);
//                    ehst.setEmpid(user.getEmployeeid());
//                    ehst.setUpdatedon(saveDate);
//                    ehst.setUpdatedby(updatedby);
//                    ehst.setCategory(Emphistory.Emp_Desg_change);
//                    session.saveOrUpdate(ehst);
//                }
                session.update(user);
                session.update(ua);
            }
            jobj.put("success", true);
            jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalstatuschangedsuccessfully",null,"Appraisal status changed successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            return jobj.toString();
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewappraisalFunction", e);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewappraisalFunction", e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewappraisalFunction", e);
        }
    }

   public static void terminateEmp(Session session, HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException, ParseException {
       String hql="";
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
       try {
           String ids[] = request.getParameterValues("ids");
           String masterid=request.getParameter("tercause");
           String desc=((StringUtil.isNullOrEmpty(request.getParameter("terdesc")))?"":request.getParameter("terdesc"));
           String reldate=request.getParameter("relievedate");
           Date rvdate=fmt.parse(reldate);
           Empprofile emp=null;
           UserLogin usl=null;
           User usr=null;
           User updatedby=(User)session.get(User.class,AuthHandler.getUserid(request));
            for (int i = 0; i < ids.length; i++) {
                hql = "from Empprofile where userLogin.userID=?";
                List lst = HibernateUtil.executeQuery(session, hql, new Object[]{ids[i]});
                usl=(UserLogin)session.get(UserLogin.class,ids[i]);
                usr=(User)session.get(User.class,ids[i]);
                if(lst.isEmpty()){
                          emp=new Empprofile();
                          emp.setUserLogin(usl);
                }else{
                  emp=(Empprofile)session.get(Empprofile.class,ids[i]);
                }
                 Emphistory ehst=new Emphistory();
                 Useraccount ua = (Useraccount) session.get(Useraccount.class, ids[i]);
                 ehst.setUserid(usr);
                 ehst.setDepartment(ua.getDepartment());
                 ehst.setDesignation(ua.getDesignationid());
                 ehst.setSalary(ua.getSalary());
                 ehst.setEmpid(ua.getEmployeeid());
                 ehst.setUpdatedon(rvdate);
                 ehst.setJoindate(emp.getJoindate());
                 ehst.setEnddate(rvdate);
                 ehst.setUpdatedby(updatedby);
                 ehst.setCategory(Emphistory.Emp_Desg_change);
                 emp.setTerminatedby(updatedby);
                 emp.setTercause((MasterData)session.get(MasterData.class,masterid));
                 emp.setRelievedate(rvdate);
                 emp.setTerReason(desc);
                 emp.setTermnd(true);
                 usr.setDeleteflag(1);
                 session.saveOrUpdate(ehst);
                 session.saveOrUpdate(emp);
                 session.saveOrUpdate(usr);
                 //@@ProfileHandler.insertAuditLog(session, AuditAction.EMPLOYEE_TERMINATED, "Employee " + AuthHandler.getFullName(usr) + " terminated by " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)),request);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.terminateEmp", e);
        }catch(SessionExpiredException e){
          throw ServiceException.FAILURE("hrmsHandler.terminateEmp", e);
        }catch(ParseException e){
          throw ServiceException.FAILURE("hrmsHandler.terminateEmp", e);
        }
    }

   public static void rehireEmp(Session session, HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException, ParseException {
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
       try {
           String ids[] = request.getParameterValues("ids");
           String deptid=request.getParameter("dept");
           String desgid=request.getParameter("desg");
           String salary=(request.getParameter("salary"));
           String reldate=request.getParameter("joindate");
           String tempid=request.getParameter("templateid");
           Date rvdate=fmt.parse(reldate);
           MasterData desg=(MasterData)session.get(MasterData.class,desgid);
           MasterData dept=(MasterData)session.get(MasterData.class,deptid);
            for (int i = 0; i < ids.length; i++) {
                User u=(User)session.get(User.class,ids[i]);
                Empprofile emp=(Empprofile)session.get(Empprofile.class,ids[i]);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                ua.setDepartment((dept));
                ua.setDesignationid(desg);
                ua.setSalary(salary);
                u.setDeleteflag(0);
                if (StringUtil.isNullOrEmpty(tempid) == false) {
                     ua.setTemplateid(tempid);
                }
                emp.setJoindate(rvdate);
                emp.setTermnd(false);
                emp.setRelievedate(null);
                session.saveOrUpdate(emp);
                session.saveOrUpdate(u);
                session.saveOrUpdate(ua);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.REHIRE_EMPLOYEE, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has rehired employee " + AuthHandler.getFullName(u) ,request);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.terminateEmp", e);
        }catch(ParseException e){
          throw ServiceException.FAILURE("hrmsHandler.terminateEmp", e);
        }
    }

    public static JSONObject setappraisalcycleFunction(Session session, HttpServletRequest request) throws ServiceException,JSONException, ParseException, SessionExpiredException {
       JSONObject jobj=new JSONObject();
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
       boolean flag=false;
       boolean initiateFlag = false;
       try {
            Date startdate=(Date) fmt.parse(request.getParameter("startdate"));
            Date enddate=(Date) fmt.parse(request.getParameter("enddate"));
            Date submitstartdate=(Date) fmt.parse(request.getParameter("submitsdate"));
            Date submitenddate=(Date) fmt.parse(request.getParameter("submitedate"));
            String cyclename = request.getParameter("cyclename");
            Appraisalcycle appcycle = null;
            List tabledata = null;
            String hql="from Appraisalcycle where company.companyID=? and ((? between startdate and enddate) or (? between startdate and enddate))";
            if(request.getParameter("editflag").equals("1")) {
                String cycleid = request.getParameter("cycleid");
                appcycle = (Appraisalcycle)session.get(Appraisalcycle.class, cycleid);
                boolean dateflag = true;
                if(!startdate.equals(appcycle.getStartdate()) || !enddate.equals(appcycle.getEnddate())) {
                    dateflag = false;
                    //Check for cycle initiated
                    hql="from Appraisalmanagement where appcycle = ?";
                    tabledata=HibernateUtil.executeQuery(session, hql, new Object[]{appcycle});
                    if(tabledata.size() == 0) {
                        dateflag = true;
                    }
                }

                if(dateflag) {
                    hql="from Appraisalcycle where company.companyID=? and id != ? and ((? between startdate and enddate) or (? between startdate and enddate)) ";
                    tabledata=HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getCompanyid(request), cycleid, startdate, enddate});
                } else {
                    initiateFlag = true;
                    jobj.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.youcannoteditcyclestartandenddateasappraisalhasalreadybeeninitiatedfortheselectedappraisalcycle",null,"You can not edit cycle start and end date as appraisal has already been initiated for the selected appraisal cycle.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                }
            } else {
                tabledata=HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getCompanyid(request), startdate, enddate});
                appcycle=new Appraisalcycle();
            }

            if(!initiateFlag && tabledata.isEmpty()){
                appcycle.setCyclename(cyclename);
                appcycle.setStartdate(startdate);
                appcycle.setEnddate(enddate);
                appcycle.setCreatedby((User)session.get(User.class,AuthHandler.getUserid(request)));
                appcycle.setCompany((Company)session.get(Company.class,AuthHandler.getCompanyid(request)));
                appcycle.setSubmitstartdate(submitstartdate);
                if(request.getParameter("editflag").equals("1")) {
                    if(appcycle.getSubmitenddate().before(submitenddate)){
                        appcycle.setReviewed(false);
                        appcycle.setCycleapproval(false);
                    }
                } else {
                    appcycle.setReviewed(false);
                    appcycle.setCycleapproval(false);
                }

                appcycle.setSubmitenddate(submitenddate);

                session.save(appcycle);
                if(request.getParameter("editflag").equals("1")) {
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.CYCLE_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited appraisal cycle " + appcycle.getCyclename(),request);
                }
                else{
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.CYCLE_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new appraisal cycle " + appcycle.getCyclename() + " for the period from " + fmt.format(startdate) + " till " + fmt.format(enddate),request);
                }
                flag=true;
            } else if(!initiateFlag && !tabledata.isEmpty()) {
                jobj.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalcycleaddedisoverlappingotherappraisalcycle.",null,"Appraisal cycle added is overlapping other appraisal cycle.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }
            jobj.put("success",flag);
       } catch(ParseException e){
          throw ServiceException.FAILURE("hrmsHandler.setappraisalcycleFunction", e);
        } catch(SessionExpiredException e){
          throw ServiceException.FAILURE("hrmsHandler.setappraisalcycleFunction", e);
        }
       return jobj;
    }
     public static void deleteDocuments(Session session, HttpServletRequest request) throws ServiceException,HibernateException {
        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                HrmsDocs hdocs = (HrmsDocs) session.load(HrmsDocs.class, ids[i]);
                hdocs.setDeleted(true);
                session.saveOrUpdate(hdocs);
              }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.deleteDocuments", e);
        }
    }
      public static String reviewanonymousAppraisalReport(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String employeeid = request.getParameter("employeeid");
            String appcycleid = request.getParameter("appraisalcycleid");
            float salary;
            float salaryincrement = 0;
                String hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and managerstatus=1";
                List list = HibernateUtil.executeQuery(session, hql, new Object[]{employeeid, appcycleid});
                Iterator ite = list.iterator();
                while (ite.hasNext()) {//Change status of all submitted request
                    Appraisalmanagement app = (Appraisalmanagement) ite.next();
                    if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) {//To approve request
                    if (app.getReviewstatus() != 2) {
                        User user = (User) session.load(User.class, employeeid);
                        Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                        app.setOriginaldesignation(ua.getDesignationid());
                        app.setOriginaldepartment(ua.getDepartment());
                        app.setReviewstatus(2);
                        app.setAppraisalstatus("submitted");
                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                            app.setReviewercomment(request.getParameter("reviewercomment"));
                        }
                        app.setEmployeestatus(1);
                        app.setManagerstatus(1);
                        app.setReviewersubmitdate(new Date());
                        if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
                            app.setReviewdepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
                            app.setReviewdesignation((MasterData) session.load(MasterData.class, request.getParameter("designation")));
                        }
                        if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                            app.setReviewsalaryincrement(Float.parseFloat(request.getParameter("salaryincrement")));
                        } else {
                            app.setReviewsalaryincrement(0);
                        }
                        session.update(app);
                    } else {
                        jobj.put("success", true);
                        jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalisalreadyapproved ",null,"Appraisal is already approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        return jobj.toString();
                    }
                }else { //To unapprove the request
                    if (app.getReviewstatus() != 2) {
                        app.setManagerstatus(0);
                        app.setReviewstatus(1);
                        if (!StringUtil.isNullOrEmpty(request.getParameter("reviewercomment"))) {
                            app.setReviewercomment(request.getParameter("reviewercomment"));
                        }
                        app.setAppraisalstatus("pending");
                    } else {
                        jobj.put("success", true);
                        jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalisalreadyapproved ",null,"Appraisal has been already approved", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                        return jobj.toString();
                    }
            }
            }
            if (Boolean.parseBoolean(request.getParameter("reviewstatus"))) { //Update user information
                User user = (User) session.load(User.class, employeeid);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                if (StringUtil.isNullOrEmpty(ua.getSalary())) {
                    salary = 0;
                } else {
                    salary = Float.parseFloat(ua.getSalary());
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("salaryincrement"))) {
                    salaryincrement = Float.parseFloat(request.getParameter("salaryincrement"));
                } else {
                    salaryincrement = 0;
                }
                salary = salary + (salary * salaryincrement) / 100;
//                user.setSalary("" + salary);
                int histsave=0;
                Date saveDate = new Date();
                SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd");
                saveDate = new Date(fmt1.format(saveDate));
                Emphistory ehst=new Emphistory();
//                User updatedby=(User)session.get(User.class,AuthHandler.getUserid(request));
                if (!StringUtil.isNullOrEmpty(request.getParameter("designation"))) {
//                    if((MasterData) session.load(MasterData.class, (String) request.getParameter("designation"))!=user.getDesignationid()){
//                        ehst.setDesignation(user.getDesignationid());
//                        histsave=1;
//                    }
//                    user.setDesignationid((MasterData) session.load(MasterData.class, request.getParameter("designation")));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("department"))) {
//                    if((MasterData) session.load(MasterData.class, (String) request.getParameter("department"))!=user.getDepartment()){
//                        ehst.setDepartment(user.getDepartment());
//                        if(histsave==0) {
//                            ehst.setDesignation(user.getDesignationid());
//                        }
//                        histsave=2;
//                    }
//                    user.setDepartment((MasterData) session.load(MasterData.class, request.getParameter("department")));
                }
                if(histsave==1){
//                    ehst.setDepartment(user.getDepartment());
                }
                if(histsave==1||histsave==2) {
                    ehst.setUserid(user);
//                    ehst.setEmpid(user.getEmployeeid());
                    ehst.setUpdatedon(saveDate);
//                    ehst.setUpdatedby(updatedby);
                    ehst.setCategory(Emphistory.Emp_Desg_change);
                    session.saveOrUpdate(ehst);
                }
                session.update(user);
            }
            jobj.put("success", true);
            jobj.put("msg", MessageSourceProxy.getMessage("hrms.hrmshandler.msg.appraisalstatuschangedsuccessfully",null,"Appraisal status changed successfully", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            return jobj.toString();
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewanonymousAppraisalReport", e);
        }catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewanonymousAppraisalReport", e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.reviewanonymousAppraisalReport", e);
        }
    }
        public static void approveAppraisalCycle(Session session, HttpServletRequest request) throws ServiceException,HibernateException {
        try {
            String[] ids = request.getParameterValues("ids");
            for (int i = 0; i < ids.length; i++) {
                Appraisalcycle appcl = (Appraisalcycle) session.load(Appraisalcycle.class, ids[i]);
                appcl.setCycleapproval(Boolean.parseBoolean(request.getParameter("status")));
                session.saveOrUpdate(appcl);
              }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.approveAppraisalCycle", e);
        }
    }

      public static JSONObject getDummyStatus(Session session, HttpServletRequest request,String cmp,String desig) throws ServiceException,JSONException {
       JSONObject jobj=new JSONObject();
       JSONArray jArr=new JSONArray();
       int cnt=0;
       String htmlStr="<div id='formContainerDiv' style='margin-top:70px;'>" +
               "<ol style='text-align:left'><li>Pick only 1 ID.</li>" +
               "<li>Password should be minimum 4 characters.</li>" +
               "<li>Since the system is anonymous, you will not be able to retrieve the password, so make sure you can remember it or store it someplace safe!</li>" +
               "</ol><fieldset style='width:500px;'><legend>Step 1: Select ID of your choice</legend>" +
               "<FORM action='javascript:dummyvalidateLogin()' name='dummyform'><table>"+
               "<tr><td><p>Available IDs:</p></td><td></td></tr><tr><td></td><td align='left'>";
       try {
            String hql="select companyID from Company where subDomain=?";
            List lst=HibernateUtil.executeQuery(session, hql,new Object[]{cmp});
            if(lst.size()==1){
                hql="select u.user.userLogin.userName,u.userID from Useraccount u where u.dummystatus=1 and u.user.company.companyID=? and u.designationid.value=? order by u.user.userLogin.userName";
                List lst1=HibernateUtil.executeQuery(session, hql,new Object[]{lst.get(0),desig});
                cnt=lst1.size();
                if (cnt > 0) {
                    Iterator ite = lst1.iterator();
                    while (ite.hasNext()) {
                        Object[] row = (Object[]) ite.next();
                        JSONObject tempobj = new JSONObject();
                        tempobj.put("uname", row[0]);
                        tempobj.put("uid", row[1]);
                        jArr.put(tempobj);
                        htmlStr += "<INPUT TYPE=RADIO NAME='dummy' VALUE='" + row[1] + "' ID='" + row[1] + "' onchange='javascript:enablePasswordField()'>" + row[0] + "<br>";
                    }
                    jobj.put("success", true);
                    jobj.put("totalCount", cnt);
                    htmlStr += "</td></tr></table></fieldset><br><fieldset style='width:500px;'><legend>Step 2: Enter Password</legend><table><tr><td align='left'>Password:  </td><td> <INPUT TYPE=password id='newpassword' disabled=true /></td><td id='oldpasswd_td' style='font-size:10px;'></td></tr>";
                    htmlStr += "<tr><td align='left'>Confirm Password:</td><td>   <INPUT TYPE=password id='newpassword2' disabled=true  /></td><td id='passwd_td' style='font-size:10px;'></td></tr>";
                    htmlStr += "<tr><td></td><td><INPUT TYPE=SUBMIT class='order-sign-up' VALUE='Register'  id='submt' disabled=true/></td></tr></table></FORM></fieldset></div>";
                    jobj.put("msg", htmlStr);
                } else {
                    jobj.put("success",true);
                    jobj.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.alltheIDshavebeenassigned",null,"All the IDs have been assigned", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                }
            }else{
                 jobj.put("success",true);
                 jobj.put("msg",MessageSourceProxy.getMessage("hrms.hrmshandler.msg.invalidsubdomain",null,"Invalid Subdomain", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            }
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.getDummyStatus", e);
        }catch (Exception e) {
            throw ServiceException.FAILURE("hrmsHandler.getDummyStatus", e);
        }
        return jobj;
    }

        public static JSONObject setDummyStatus(Session session, HttpServletRequest request) throws ServiceException,HibernateException, JSONException {
             JSONObject jobj=new JSONObject();
            try {
            String ids = request.getParameter("radio");
            User u=(User) session.get(User.class, ids);
            UserLogin ul=(UserLogin) session.get(UserLogin.class, ids);
            Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
            if(ua.getDummystatus()==2){
                jobj.put("success",true);
                jobj.put("msg","Selected ID is already assigned.");
            }else {
                    if (StringUtil.equal(request.getParameter("pass"), request.getParameter("oldpass"))) {
                        ua.setDummystatus(2);
                        ul.setPassword(request.getParameter("pass"));
                        session.save(ul);
                        session.update(u);
                        jobj.put("success", true);
                        jobj.put("msg", "Selected ID is assigned successfully.");
                    } else {
                        jobj.put("success", true);
                        jobj.put("msg", "Passwords do not match.");
                    }
                }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.setDummyStatus", e);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.setDummyStatus", e);
        }
             return jobj;
    }


    public static JSONObject EmailCases(String temptype, Session session, HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj=new JSONObject();
        List tabledata = null;
        try {
            String GET_MAIL_MSG = "select body_plain,body_html,subject from hrms_EmailTemplates where templatetype=?";
            tabledata = HibernateUtil.executeQuery(session, GET_MAIL_MSG, new Object[]{temptype});
            Iterator itr = tabledata.iterator();
            while (itr.hasNext()) {
                 Object u[] = (Object[]) itr.next();
                 jobj.put("plainmessage",u[0].toString());
                 jobj.put("htmlmessage",u[1].toString());
                 jobj.put("title",u[2].toString());
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("EmailCases", ex);
        }
        return jobj;
    }

    public static JSONObject sendappraisalemailFunction(Session session, HttpServletRequest request) throws ServiceException, HibernateException, SessionExpiredException, JSONException {
        try {
            String cyclename = "";
            JSONObject jobj = new JSONObject();
            String cycledate = "";
            String mailcontent="";
            List recordTotalCount = null;
            String email= request.getSession().getAttribute("sysemailid").toString();
            boolean flag=true;
            String plainmsg="";
            String htmlmsg="";
            String title="";
//            User usr = (User) session.load(User.class, AuthHandler.getUserid(request));
//            if (usr.getEmailID() != null) {
//                 email = usr.getEmailID();
//            }
            Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
            Appraisalcycle app = (Appraisalcycle) session.load(Appraisalcycle.class, request.getParameter("appraisalcycleid"));
            String hql1="select id from Appraisalcycle where id=? and submitenddate>=?";
            List tabledata = HibernateUtil.executeQuery(session, hql1, new Object[]{request.getParameter("appraisalcycleid"),userdate});
            if (!tabledata.isEmpty()) {
                JSONObject eobj = EmailCases(hrms_EmailTemplates.appraisalRemainder, session, request);
                plainmsg = eobj.getString("plainmessage");
                htmlmsg = eobj.getString("htmlmessage");
                title = eobj.getString("title");
                String query = "select userID,emailID,firstName,lastName from User where company.companyID=? and deleted=?";
                List list = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getCompanyid(request), false});
                Iterator itr = list.iterator();
                while (itr.hasNext()) {
                    Object[] u = (Object[]) itr.next();
                    cyclename = app.getCyclename();
                    cycledate = AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitenddate());
                    mailcontent = "<p>Appraisal Cycle: " + cyclename + "<br>Deadline for submission: " + cycledate;
                    String hql = "select appm.employeestatus from Appraisalmanagement appm where appm.employee.userID=? and appm.appcycle.id=? group by appm.appcycle.id";
                    recordTotalCount = HibernateUtil.executeQuery(session, hql, new Object[]{u[0].toString(), app.getId()});
                    if (!recordTotalCount.isEmpty()) {
                        Object log = (Object) recordTotalCount.get(0);
                        if ((Integer) log == 0) {
                            mailcontent += "<br>Self Appraisal: " + "Not Submitted";
                        }
                    }
                    mailcontent+="</p>";
                    String intpmsg = String.format(plainmsg, u[2].toString()+" "+u[3].toString(), mailcontent);
                    String inthtmlmsg = String.format(htmlmsg, u[2].toString()+" "+u[3].toString(), mailcontent);
                    try {
                        SendMailHandler.postMail(new String[]{u[1].toString()}, title, inthtmlmsg, intpmsg, email);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                flag = false;
            }
            jobj.put("message", flag);
            return jobj;
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.sendappraisalemailFunction", e);
        }
    }

    public static JSONObject sendRevieweremailFunction(Session session, HttpServletRequest request) throws ServiceException, HibernateException, SessionExpiredException, JSONException {
        try {
            JSONObject jobj = new JSONObject();
            String mailcontent = "";
            String htmlmailcontent = "";
            String plnmailcontent = "";
            String email = request.getSession().getAttribute("sysemailid").toString();
            String plainmsg = "";
            String htmlmsg = "";
            String title = "";
            String url = "";
//            User usr = (User) session.load(User.class, AuthHandler.getUserid(request));
//            if (usr.getEmailID() != null) {
//                email = usr.getEmailID();
//            }
            Company cmpid = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            String appcycleid = request.getParameter("appraisalcycleid");
            url = URLUtil.getPageURL(request, Links.loginpagewthFull, cmpid.getSubDomain()) + "appraise.jsp?pdfEmail=true&reviewappraisal=false&appraisalcycid=" + appcycleid + "&userid=";
            JSONObject eobj = EmailCases(hrms_EmailTemplates.pdfMailtoReviewer, session, request);
            plainmsg = eobj.getString("plainmessage");
            htmlmsg = eobj.getString("htmlmessage");
            title = eobj.getString("title");
            Appraisalcycle app = (Appraisalcycle) session.load(Appraisalcycle.class, appcycleid);
            mailcontent = "Appraisal Cycle: " + app.getCyclename() + "\n\n";
            String query = "select distinct reviewer.userID,reviewer.firstName,reviewer.lastName,reviewer.emailID from Assignreviewer where reviewer.company.companyID=? and reviewer.deleted=? and reviewerstatus=1";
            List list = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getCompanyid(request), false});
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Object[] reviewer = (Object[]) itr.next();
                String hql = "select distinct employee.userID,employee.firstName,employee.lastName,employee.employeeid from Appraisalmanagement where appcycle.id=? and reviewstatus=2 and employee.userID in " +
                        "(select employee.userID from Assignreviewer where reviewer.userID=? and reviewerstatus=1)";
                List list1 = HibernateUtil.executeQuery(session, hql, new Object[]{appcycleid, reviewer[0].toString()});
                Iterator ite1 = list1.iterator();
                if (!list1.isEmpty()) {
                    while (ite1.hasNext()) {
                        Object[] row = (Object[]) ite1.next();
                        String target="_parent";
                        String empid=hrmsManager.getEmpidFormatEdit(session, request,(Integer)row[3]);
                        plnmailcontent += "" + row[1]+" "+row[2] + " - " + url + row[0] + "&d="+reviewer[0]+"\n";
                        htmlmailcontent+="<a target='"+target+"' href='"+ url+row[0]+"&d="+reviewer[0]+"'>"+row[1]+" "+row[2]+"</a>\n";
                    }
                    plnmailcontent=mailcontent+plnmailcontent;
                    htmlmailcontent=mailcontent+htmlmailcontent;
                    String intpmsg = String.format(plainmsg, reviewer[1].toString()+" "+reviewer[2].toString(), plnmailcontent);
                    String inthtmlmsg = String.format(htmlmsg, reviewer[1].toString()+" "+reviewer[2], htmlmailcontent.replaceAll("\n","<br>" ));
                    htmlmailcontent="";
                    plnmailcontent="";
                    try {
                        SendMailHandler.postMail(new String[]{reviewer[3].toString()}, title, inthtmlmsg, intpmsg, email);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jobj.put("message", mailcontent);
            return jobj;
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.sendRevieweremailFunction", e);
        }
    }

    public static JSONObject sendappraisalreportEmail(Session session, HttpServletRequest request) throws ServiceException, HibernateException, SessionExpiredException, JSONException {
        try {
            JSONObject jobj = new JSONObject();
            String mailcontent = "";
            String htmlmailcontent = "";
            String plnmailcontent = "";
            String email = request.getSession().getAttribute("sysemailid").toString();
            String plainmsg = "";
            String htmlmsg = "";
            String title = "";
            String url = "";
//            User usr = (User) session.load(User.class, AuthHandler.getUserid(request));
//            if (usr.getEmailID() != null) {
//                email = usr.getEmailID();
//            }
            Company cmpid = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            String appcycleid = request.getParameter("appraisalcycleid");
            url = URLUtil.getPageURL(request, Links.loginpagewthFull, cmpid.getSubDomain()) + "appraise.jsp?pdfEmail=true&reviewappraisal=false&appraisalcycid=" + appcycleid + "&userid=";
            JSONObject eobj = EmailCases(hrms_EmailTemplates.emailemployeeReport, session, request);
            plainmsg = eobj.getString("plainmessage");
            htmlmsg = eobj.getString("htmlmessage");
            title = eobj.getString("title");
            Appraisalcycle app = (Appraisalcycle) session.load(Appraisalcycle.class, appcycleid);
            mailcontent = "Appraisal Cycle: ";
            String hql = "select distinct employee.userID,employee.firstName,employee.lastName,employee.employeeid,employee.emailID from Appraisalmanagement where appcycle.id=? and reviewstatus=2";
            List list1 = HibernateUtil.executeQuery(session, hql, new Object[]{appcycleid});
            Iterator ite1 = list1.iterator();
            if (!list1.isEmpty()) {
                while (ite1.hasNext()) {
                    Object[] row = (Object[]) ite1.next();
                    String target = "_parent";
                    plnmailcontent += "" + app.getCyclename() + " (" + url + row[0] +"&d="+row[0]+")\n\n";
                    htmlmailcontent += "<a target='" + target + "' href='" + url + row[0] +"&d="+row[0]+"'>" +app.getCyclename()+ "</a>\n\n";

                    plnmailcontent = mailcontent + plnmailcontent;
                    htmlmailcontent = mailcontent + htmlmailcontent;
                    String intpmsg = String.format(plainmsg, row[1].toString() + " " + row[2].toString(), plnmailcontent);
                    String inthtmlmsg = String.format(htmlmsg, row[1].toString() + " " + row[2].toString(), htmlmailcontent.replaceAll("\n", "<br>"));
                    htmlmailcontent = "";
                    plnmailcontent = "";
                    try {
                        SendMailHandler.postMail(new String[]{row[4].toString()}, title, inthtmlmsg, intpmsg, email);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            jobj.put("message", mailcontent);
            return jobj;
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("hrmsHandler.sendappraisalreportEmail", e);
        }
    }

    public static JSONObject getjobsForjsp(Session session, HttpServletRequest request, String cmp, String desig) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        int cnt = 0;
        JSONArray jarr1=new JSONArray();
        JSONArray jarr2=new JSONArray();
        JSONArray jarr3=new JSONArray();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String hql="select companyID from Company where subDomain=?";
            List lst=HibernateUtil.executeQuery(session, hql,new Object[]{cmp});
            Date companydate = com.krawler.common.util.Timezone.toCompanySystemTimezoneDate(session, request, fmt.format(new Date()),String.valueOf(lst.get(0)));
            if(lst.size()==1){
                Company c=(Company)session.get(Company.class,String.valueOf(lst.get(0)));
                hql = "from Positionmain  where positionid=? and jobtype!='Internal' and company.companyID=? and ( startdate<=? and enddate>=? )";
                List lst1=HibernateUtil.executeQuery(session, hql,new Object[]{desig,c.getCompanyID(),companydate,companydate});
                cnt=lst1.size();
                if (cnt > 0) {
                    Iterator ite = lst1.iterator();
                    while (ite.hasNext()) {
                        Positionmain row = (Positionmain) ite.next();
                        jobj.put("jobid", row.getJobid()!=null?row.getJobid():"");
                        jobj.put("jobname", row.getPosition().getValue());	
                        jobj.put("jobdept", row.getDepartmentid().getValue());
                        if (!StringUtil.isNullOrEmpty(row.getLocation())) {
                            jobj.put("location", row.getLocation());
                        }
                        jobj.put("nopos", row.getNoofpos());
                        if (!StringUtil.isNullOrEmpty(row.getDetails().trim())) {
                            jobj.put("desc", row.getDetails());
                        }
                        if (!StringUtil.isNullOrEmpty(row.getJobshift())) {
                            jobj.put("shift", row.getJobshift());
                        }
                        if (!StringUtil.isNullOrEmpty(row.getRelocation())) {
                            jobj.put("relocation", row.getRelocation());
                        }
                        if (!StringUtil.isNullOrEmpty(row.getTravel())) {
                            jobj.put("travel", row.getTravel());
                        }
                        if (row.getExperienceyear() != null||row.getExperiencemonth() != null) {
                            String experience=" ";
                            if(row.getExperienceyear() != null){
                              experience= ((row.getExperienceyear() != 0) ? row.getExperienceyear()+ " years " :"") ;
                            }
                            if(row.getExperiencemonth() != null){
                              experience+=((row.getExperiencemonth() != 0) ? row.getExperiencemonth()+ " months " :"") ;
                            }
                            if(!StringUtil.isNullOrEmpty(experience)){
                            jobj.put("experience",experience);
                            }
                        }
                        jarr1=getjobprofileFunction(session, desig,1);
                        if(jarr1.length()>0){
                            jobj.put("responsibility",jarr1);
                        }
                        jarr2=getjobprofileFunction(session, desig,2);
                        if(jarr2.length()>0){
                            jobj.put("skills",jarr2);
                        }
                        jarr3=getjobprofileFunction(session, desig,3);
                        if(jarr3.length()>0){
                            jobj.put("qualification",jarr3);
                        }
                        jobj.put("companyname", c.getCompanyName());
                    }
                    jobj.put("success", true);
                    jobj.put("url",(URLUtil.getPageURL(request,Links.loginpagewthFull,cmp)+"applicant.jsp?jobid="+desig));
                }else{
                  jobj.put("success",false);
                  jobj.put("msg",MessageSourceProxy.getMessage("hrms.recruitment.Eitheroldlinkorjobdoesnotexist",null,"Either you are coming from an old link or bookmark, or this job does not exist.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                  jobj.put("companyname",c.getCompanyName());
                }
            }else{
                jobj.put("success",false);
                jobj.put("msg",MessageSourceProxy.getMessage("hrms.recruitment.InvalidSubdomain",null,"Invalid Subdomain", RequestContextUtils.getLocale(request)));
            }
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.getjobsForjsp", e);
        }catch (Exception e) {
            throw ServiceException.FAILURE("hrmsHandler.getjobsForjsp", e);
        }
        return jobj;
    }
   
    public static KwlReturnObject addConfigRecruitmentData(Session session, HashMap<String, Object> requestParams) {
        List<ConfigRecruitmentData> list = new ArrayList<ConfigRecruitmentData>();
        boolean success = false;
        try {
            ConfigRecruitmentData ConfigRecruitmentDataobj = (ConfigRecruitmentData) HibernateUtil.setterMethod(session, requestParams, "com.krawler.hrms.recruitment.ConfigRecruitmentData", "Id");
            list.add(ConfigRecruitmentDataobj);
        } catch (Exception e) {
            success = false;
            System.out.println("Error is " + e);
        } finally {
            return new KwlReturnObject(success, "Configmaster added successfully", "-1", list, list.size());
        }
    }

    public static JSONObject saveJobs(Session session, HttpServletRequest request, String cmp, String desig, String colnumbers) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        int cnt = 0;
        String pwd = "1234";
        String status = "Pending";
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        HashMap<String, String> arrParam = new HashMap<String, String>();
        HashMap<Integer,String> filemap = new HashMap<Integer,String >();
        boolean fileUpload = false;
        String filledcols ="";
        String datecols ="";
        Allapplications allapl = null;
        ArrayList<FileItem> hm = new ArrayList<FileItem>();
        String datenumbers = request.getParameter("datenumbers");
        String mandatorynumbers = request.getParameter("mandatorynumbers");
        String[] mandatoryfields = null;
        HashMap<String,String> mandatorymap = new HashMap<String,String >();
        if(!StringUtil.isNullOrEmpty(mandatorynumbers)){
            mandatoryfields = mandatorynumbers.split(",");
            for (int i = 0; i < mandatoryfields.length; i++) {
                mandatorymap.put(mandatoryfields[i], "true");
            }
        }
        try {
            Company company = (Company) session.get(Company.class, cmp);
            fileUploader.parseRequest(request, arrParam, hm, fileUpload,filemap);
            String[] cols = colnumbers.split(",");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean mandatory = false;
            for (int i = 0; i < cols.length; i++) {
                if (arrParam.get("Col" + cols[i])!=null && !arrParam.get("Col" + cols[i]).trim().equals("")) {
                    requestParams.put("Col" + cols[i], arrParam.get("Col" + cols[i]).trim());
                    jobj.put("Col" + cols[i], arrParam.get("Col" + cols[i]).trim());
                    filledcols += cols[i] +",";
                } else {
                        if(mandatorymap.containsKey(cols[i])){
                             mandatory = true;
                        }
                }
            }
            if(!StringUtil.isNullOrEmpty(datenumbers)){
                String[] dates = datenumbers.split(",");
                for (int i = 0; i < dates.length; i++) {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("Col" + dates[i] +"_dd"))
                           && !StringUtil.isNullOrEmpty((String) arrParam.get("Col" + dates[i] +"_mm"))
                           && !StringUtil.isNullOrEmpty((String) arrParam.get("Col" + dates[i] +"_yy"))) {
                        String date = arrParam.get("Col" + dates[i]+"_mm") + "/" +arrParam.get("Col" + dates[i]+"_dd") + "/" +arrParam.get("Col" + dates[i]+"_yy");
                        requestParams.put("Col" + dates[i] , date);
                        jobj.put("Col" + dates[i], date);
                        datecols += dates[i] +",";
                    } else {
                             if(mandatorymap.containsKey(dates[i])){
                                 mandatory = true;
                             }
                    }
                }
            }
            if (!mandatory) {
                requestParams.put("Company",cmp);
                requestParams.put("Referenceid", desig);
                KwlReturnObject result = addConfigRecruitmentData(session,requestParams);
                ConfigRecruitmentData ConfigRecruitmentDataobj = (ConfigRecruitmentData) result.getEntityList().get(0);
                arrParam.put("docname", "_Resume");
                arrParam.put("docdesc", "Resume");
                
                for(int j=0;j<filemap.size();j++){
                    HrmsDocs doc = fileUploader.uploadFile(session, hm.get(j), ConfigRecruitmentDataobj, arrParam, false);
                    HrmsDocmap docMap = new HrmsDocmap();
                    docMap.setDocid(doc);
                    docMap.setRecid(ConfigRecruitmentDataobj.getId());
                    session.save(docMap);
                    String classstr = "com.krawler.hrms.recruitment.ConfigRecruitmentData";
                    Class cl = Class.forName(classstr);
                    Method setter = cl.getMethod("set"+filemap.get(j), String.class);
                    setter.invoke(ConfigRecruitmentDataobj, doc.getDocid());

                }
                session.save(ConfigRecruitmentDataobj);

                Positionmain position = (Positionmain) session.load(Positionmain.class, desig);
                allapl = new Allapplications();
                allapl.setStatus(status);
                allapl.setApplydate(new Date());
                allapl.setConfigjobapplicant(ConfigRecruitmentDataobj);
                allapl.setEmployeetype(4);  //configjobapplicant=4 reset to 0
                allapl.setPosition(position);
                allapl.setCompany(company);
                allapl.setDelflag(0);
                allapl.setApplicationflag(0);
                allapl.setRejectedbefore(0);
                session.save(allapl);


                String companyname = company.getCompanyName();
                String subject=String.format(HrmsMsgs.jobSubject,(position.getJobid()!=null?position.getJobid():""),position.getPosition().getValue());
                String pmsg = String.format(HrmsMsgs.jobPlnmsg, (ConfigRecruitmentDataobj.getCol1() + " " + ConfigRecruitmentDataobj.getCol2()), companyname, companyname);
                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,position.getPosition().getValue()+" ["+(position.getJobid()!=null?position.getJobid():"")+"]", companyname, companyname);
                try {
                    String email = getRecruitmentEmailId(request);
                    SendMailHandler.postMail(new String[]{ConfigRecruitmentDataobj.getCol3()}, subject, htmlmsg, pmsg, email);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                jobj.put("msg", "<div style='text-align: left;'><p>Your application for " + position.getPosition().getValue() + " [" + (position.getJobid()!=null?position.getJobid():"") + "] at " + companyname + " has been submitted successfully.</p>" +
                        "<p>If your resume get shortlisted, we will get in touch with you.</p>" +
                        "With best wishes,<br>Recruitment Team at " + companyname + "</div>");
                jobj.put("success", true);
                jobj.put("fields", false);
            } else {
                jobj.put("msg", "Please fill all the fields marked with *");
                jobj.put("success", false);
                jobj.put("fields", true);
                if (!StringUtil.isNullOrEmpty(filledcols)){
                    filledcols = filledcols.substring(0,filledcols.length()-1);
                    jobj.put("filledcols", filledcols);
                }
                if (!StringUtil.isNullOrEmpty(datecols)){
                    datecols = datecols.substring(0,datecols.length()-1);
                    jobj.put("datecols", datecols);
                }
                

            }
        } catch (JSONException e) {
        	e.printStackTrace();
            throw ServiceException.FAILURE("hrmsHandler.savejobs", e);
        } catch (Exception e) {
        	e.printStackTrace();
            throw ServiceException.FAILURE("hrmsHandler.savejobs", e);
        }
        return jobj;
    }
    
    public static String getSysEmailIdByCompanyID(HttpServletRequest request){
    	String emailId = "admin@deskera.com";
    	try{
    		String jobid = request.getParameter("jobid");
    		String hql = "from Positionmain where positionid = ?";
    		List<Positionmain> list = HibernateUtil.executeQuery(hql, jobid);
    		Positionmain positionmain = list!=null?list.get(0):null;
    		if(positionmain!=null){
    			Company company = positionmain.getCompany();
        		if(company!=null){
        			emailId = company.getEmailID();
        			if(StringUtil.isNullOrEmpty(emailId)){
        				emailId = company.getCreator().getEmailID();                    
                    }
        		}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return emailId;
    	}
    }
    
    public static String getRecruitmentEmailId(HttpServletRequest request){
    	String emailId = "admin@deskera.com";
    	try{
    		String jobid = request.getParameter("jobid");
    		String hql = "from Positionmain where positionid = ?";
    		List<Positionmain> list = HibernateUtil.executeQuery(hql, jobid);
    		Positionmain positionmain = list!=null?list.get(0):null;
    		if(positionmain!=null){
    			Company company = positionmain.getCompany();
    			if(company!=null){
    				hql = "from CompanyPreferences where companyid = ?";
        			List<CompanyPreferences> listCompanyPreferences = HibernateUtil.executeQuery(hql, company.getCompanyID());
        			CompanyPreferences companyPreferences = listCompanyPreferences!=null?listCompanyPreferences.get(0):null;
        			if(companyPreferences!=null){
        				emailId = companyPreferences.getEmailNotification();
        			}
        			
        			if(StringUtil.isNullOrEmpty(emailId)){
        				emailId = company.getEmailID();
        				if(StringUtil.isNullOrEmpty(emailId)){
        					emailId = company.getCreator().getEmailID();                    
        				}
        			}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return emailId;
    	}
    }
        public static JSONObject saveJobs(Session session, HttpServletRequest request, String cmp, String desig) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        int cnt = 0;
        String pwd = "1234";
        String status = "Pending";
        Jobapplicant jobapp = null;
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        HashMap<String, String> arrParam = new HashMap<String, String>();
        boolean fileUpload = false;
        ArrayList<FileItem> hm = new ArrayList<FileItem>();
        Allapplications allapl = null;
        try {
            Company company = (Company) session.get(Company.class, cmp);
            fileUploader.parseRequest(request, arrParam, hm, fileUpload);
            if (!StringUtil.isNullOrEmpty((String) arrParam.get("fname")) && !StringUtil.isNullOrEmpty((String) arrParam.get("lname")) && !StringUtil.isNullOrEmpty((String) arrParam.get("e")) &&
                    !StringUtil.isNullOrEmpty((String) arrParam.get("contact")) && !StringUtil.isNullOrEmpty((String) arrParam.get("addr1")) && !hm.isEmpty() &&
                    !StringUtil.isNullOrEmpty((String) arrParam.get("preflocation"))) {
                jobapp = new Jobapplicant();
                jobapp.setFirstname((String) arrParam.get("fname"));
                jobapp.setLastname((String) arrParam.get("lname"));
                jobapp.setEmail((String) arrParam.get("e"));
                jobapp.setContactno((String) arrParam.get("contact"));
                jobapp.setUsername((String) arrParam.get("e"));
                jobapp.setAddress1((String) arrParam.get("addr1"));
                jobapp.setPassword(AuthHandler.getSHA1(pwd));
                jobapp.setStatus(0);
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("title"))) {
                    jobapp.setTitle((String) arrParam.get("title"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("otheremail"))) {
                    jobapp.setOtheremail((String) arrParam.get("otheremail"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("mobileno"))) {
                    jobapp.setMobileno((String) arrParam.get("mobileno"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("state"))) {
                    jobapp.setState((String) arrParam.get("state"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("city"))) {
                    jobapp.setCity((String) arrParam.get("city"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("country"))) {
                    jobapp.setCountryid((MasterData) session.get(MasterData.class, (String) arrParam.get("country")));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("graddegree"))) {
                    jobapp.setGraddegree((String) arrParam.get("graddegree"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("gradspecilization"))) {
                    jobapp.setGradspecialization((String) arrParam.get("gradspecilization"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("graduniversity"))) {
                    jobapp.setGraduniversity((String) arrParam.get("graduniversity"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("gradcollege"))) {
                    jobapp.setGradcollege((String) arrParam.get("gradcollege"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("gradepercent"))) {
                    jobapp.setGradpercent((String) arrParam.get("gradepercent"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("pgqualification"))) {
                    jobapp.setPgqualification((String) arrParam.get("pgqualification"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("pgspecialization"))) {
                    jobapp.setPgspecialization((String) arrParam.get("pgspecialization"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("pguniversity"))) {
                    jobapp.setPguniversity((String) arrParam.get("pguniversity"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("pgcollege"))) {
                    jobapp.setPgcollege((String) arrParam.get("pgcollege"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("pgpercent"))) {
                    jobapp.setPgpercent((String) arrParam.get("pgpercent"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("othername"))) {
                    jobapp.setOthername((String) arrParam.get("othername"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("otheruni"))) {
                    jobapp.setOtherdetails((String) arrParam.get("otheruni"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("otherpercent"))) {
                    jobapp.setOtherpercent((String) arrParam.get("otherpercent"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("currentcompanyname"))) {
                    jobapp.setCurrentorganization((String) arrParam.get("currentcompanyname"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("currentdesig"))) {
                    jobapp.setCurrentdesignation((String) arrParam.get("currentdesig"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("currentindustry"))) {
                    jobapp.setCurrentindustry((String) arrParam.get("currentindustry"));
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("experiencemonth"))) {
                        jobapp.setExperiencemonth(Integer.parseInt((String) arrParam.get("experiencemonth")));
                    }
                } catch (Exception e) {
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("experienceyear"))) {
                        jobapp.setExperienceyear(Integer.parseInt((String) arrParam.get("experienceyear")));
                    }
                } catch (Exception e) {
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("grossctc"))) {
                        jobapp.setGrosssalary(Integer.parseInt((String) arrParam.get("grossctc")));
                    }
                } catch (Exception e) {
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("expectedctc"))) {
                        jobapp.setExpectedsalary(Integer.parseInt((String) arrParam.get("expectedctc")));
                    }
                } catch (Exception e) {
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("functionalexpertise"))) {
                    jobapp.setFunctionalexpertise((String) arrParam.get("functionalexpertise"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("keyskills"))) {
                    jobapp.setKeyskills((String) arrParam.get("keyskills"));
                }
                if (!StringUtil.isNullOrEmpty((String) arrParam.get("preflocation"))) {
                    jobapp.setInterviewlocation((String) arrParam.get("preflocation"));
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("dobyy"))&&!StringUtil.isNullOrEmpty((String) arrParam.get("dobmm"))
                            &&!StringUtil.isNullOrEmpty((String) arrParam.get("dobdd"))) {
                        jobapp.setBirthdate(fmt.parse((String) arrParam.get("dobmm")+"/"+(String) arrParam.get("dobdd")+"/"+(String) arrParam.get("dobyy")));
                    }
                } catch (Exception e) {
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("pgpassdateyy"))&&!StringUtil.isNullOrEmpty((String) arrParam.get("pgpassdatemm"))
                            &&!StringUtil.isNullOrEmpty((String) arrParam.get("pgpassdatedd"))) {
                        jobapp.setPgpassdate(fmt.parse((String) arrParam.get("pgpassdatemm")+"/"+(String) arrParam.get("pgpassdatedd")+"/"+(String) arrParam.get("pgpassdateyy")));
                    }
                } catch (Exception e) {
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("gradpassdateyy"))&&!StringUtil.isNullOrEmpty((String) arrParam.get("gradpassdatemm"))
                            &&!StringUtil.isNullOrEmpty((String) arrParam.get("gradpassdatedd"))) {
                        jobapp.setGradpassdate(fmt.parse((String) arrParam.get("gradpassdatemm")+"/"+(String) arrParam.get("gradpassdatedd")+"/"+(String) arrParam.get("gradpassdateyy")));
                    }
                } catch (Exception e) {
                }
                try {
                    if (!StringUtil.isNullOrEmpty((String) arrParam.get("otherpassdateyy"))&&!StringUtil.isNullOrEmpty((String) arrParam.get("otherpassdatemm"))
                            &&!StringUtil.isNullOrEmpty((String) arrParam.get("otherpassdatedd"))) {
                        jobapp.setOtherpassdate(fmt.parse((String) arrParam.get("otherpassdatemm")+"/"+(String) arrParam.get("otherpassdatedd")+"/"+(String) arrParam.get("otherpassdateyy")));
                    }
                } catch (Exception e) {
                }
                jobapp.setCompany(company);
                session.save(jobapp);
                arrParam.put("docname", arrParam.get("fname") + " " + arrParam.get("lname") + "_Resume");
                HrmsDocs doc = fileUploader.uploadFile(session, hm.get(cnt), jobapp.getApplicantid(), arrParam, false);
                HrmsDocmap docMap = new HrmsDocmap();
                docMap.setDocid(doc);
                docMap.setRecid(jobapp.getApplicantid());
                session.save(docMap);
                Positionmain position = (Positionmain) session.load(Positionmain.class, desig);
                allapl = new Allapplications();
                allapl.setStatus(status);
                allapl.setApplydate(new Date());
                allapl.setJobapplicant(jobapp);
                allapl.setEmployeetype(0);
                allapl.setPosition(position);
                allapl.setCompany(company);
                allapl.setDelflag(0);
                allapl.setApplicationflag(0);
                allapl.setRejectedbefore(0);
                session.save(allapl);
                insertConfigDataExternal(session, request, "Recruitment", jobapp.getApplicantid(), arrParam, cmp);
                String companyname = company.getCompanyName();
                String subject=String.format(HrmsMsgs.jobSubject,(position.getJobid()!=null?position.getJobid():""),position.getPosition().getValue());
                String pmsg = String.format(HrmsMsgs.jobPlnmsg, (jobapp.getFirstname() + " " + jobapp.getLastname()), companyname, companyname);
                String htmlmsg = String.format(HrmsMsgs.jobHTMLmsg,position.getPosition().getValue()+"["+(position.getJobid()!=null?position.getJobid():"")+"]", companyname, companyname);
                try {
                    String email = request.getSession().getAttribute("sysemailid").toString();
                    SendMailHandler.postMail(new String[]{jobapp.getEmail()}, subject, htmlmsg, pmsg, email);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                jobj.put("msg", "<div style='text-align: left;'><p>Your application for " + position.getPosition().getValue()+" ["+(position.getJobid()!=null?position.getJobid():"")+"] at " + companyname + " has been submitted successfully.</p>"+
                        "<p>If your resume get shortlisted, we will get in touch with you.</p>"+
                        "With best wishes,<br>Recruitment Team at "+companyname+"</div>");
                jobj.put("success", true);
                jobj.put("fields", false);
            } else {
                jobj.put("msg", "Please fill all the fields marked with *");
                jobj.put("success", false);
                jobj.put("fields", true);
                jobj.put("firstname",(String) arrParam.get("fname"));
                jobj.put("lastname",(String) arrParam.get("lname"));
                jobj.put("email",(String) arrParam.get("e"));
                jobj.put("otheremail",(String) arrParam.get("otheremail"));
                jobj.put("contactno",(String) arrParam.get("contact"));
                jobj.put("altercontactno",(String) arrParam.get("mobileno"));
                jobj.put("state",(String) arrParam.get("state"));
                jobj.put("city",(String) arrParam.get("city"));
                jobj.put("address",(String) arrParam.get("addr1"));
                jobj.put("dobyy",(String) arrParam.get("dobyy"));
                jobj.put("dobmm",(String) arrParam.get("dobmm"));
                jobj.put("dobdd",(String) arrParam.get("dobdd"));
                jobj.put("graddegree",(String) arrParam.get("graddegree"));
                jobj.put("gradspecilization",(String) arrParam.get("gradspecilization"));
                jobj.put("graduniversity",(String) arrParam.get("graduniversity"));
                jobj.put("gradcollege",(String) arrParam.get("gradcollege"));
                jobj.put("gradepercent",(String) arrParam.get("gradepercent"));
                jobj.put("gradpassdateyy",(String) arrParam.get("gradpassdateyy"));
                jobj.put("gradpassdatemm",(String) arrParam.get("gradpassdatemm"));
                jobj.put("gradpassdatedd",(String) arrParam.get("gradpassdatedd"));
                jobj.put("pgqualification",(String) arrParam.get("pgqualification"));
                jobj.put("pgspecialization",(String) arrParam.get("pgspecialization"));
                jobj.put("pguniversity",(String) arrParam.get("pguniversity"));
                jobj.put("pgcollege",(String) arrParam.get("pgcollege"));
                jobj.put("pgpercent",(String) arrParam.get("pgpercent"));
                jobj.put("pgpassdateyy",(String) arrParam.get("pgpassdateyy"));
                jobj.put("pgpassdatemm",(String) arrParam.get("pgpassdatemm"));
                jobj.put("pgpassdatedd",(String) arrParam.get("pgpassdatedd"));
                jobj.put("othername",(String) arrParam.get("othername"));
                jobj.put("otheruni",(String) arrParam.get("otheruni"));
                jobj.put("otherpercent",(String) arrParam.get("otherpercent"));
                jobj.put("otherpassdateyy",(String) arrParam.get("otherpassdateyy"));
                jobj.put("otherpassdatemm",(String) arrParam.get("otherpassdatemm"));
                jobj.put("otherpassdatedd",(String) arrParam.get("otherpassdatedd"));
                jobj.put("currentcompanyname",(String) arrParam.get("currentcompanyname"));
                jobj.put("currentdesig",(String) arrParam.get("currentdesig"));
                jobj.put("currentindustry",(String) arrParam.get("currentindustry"));
                jobj.put("experiencemonth",(String) arrParam.get("experiencemonth"));
                jobj.put("experienceyear",(String) arrParam.get("experienceyear"));
                jobj.put("functionalexpertise",(String) arrParam.get("functionalexpertise"));
                jobj.put("grossctc",(String) arrParam.get("grossctc"));
                jobj.put("expectedctc",(String) arrParam.get("expectedctc"));
                jobj.put("keyskills",(String) arrParam.get("keyskills"));
                jobj.put("preflocation",(String) arrParam.get("preflocation"));
            }
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsHandler.savejobs", e);
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsHandler.savejobs", e);
        }
        return jobj;
    }

    public static JSONArray getjobprofileFunction(Session session, String position, Integer type) throws ServiceException, JSONException {
        JSONArray jarr = new JSONArray();
        try {
            String hql = "from Jobprofile where position.positionid=? and type=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{position, type});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Jobprofile job = (Jobprofile) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", job.getId());
                tmpObj.put("qualification", job.getQualification());
                tmpObj.put("qualificationdesc", job.getQualificationdesc());
                tmpObj.put("responsibility", job.getResponsibility());
                tmpObj.put("skill", job.getSkill());
                tmpObj.put("skilldesc", job.getSkilldesc());
                jarr.put(tmpObj);
            }

        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getjobprofileFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getjobprofileFunction");
        } finally {
        }
        return jarr;
    }

    public static void deletejobprofileData(Session session, HttpServletRequest request) throws ServiceException, JSONException {
        try {
            String delids[] = request.getParameterValues("delid");
            for (int i = 0; i < delids.length; i++) {
                Jobprofile jobprof = (Jobprofile) session.get(Jobprofile.class, delids[i]);
                if (jobprof != null) {
                    session.delete(jobprof);
                }
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("getjobprofileFunction", ex);
        }  finally {
        }
    }

    public static int insertConfigData(Session session, HttpServletRequest request, String formtype, String referenceid) {
        int successflag = 0;
        int delflag = 0;
        try {
            String query1 = "from ConfigType where formtype=? and company.companyID = ?";
            List lst = HibernateUtil.executeQuery(session, query1, new Object[]{formtype, AuthHandler.getCompanyid(request)});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                String query3 = "from ConfigData where referenceid=?";
                List lst1 = HibernateUtil.executeQuery(session, query3, new Object[]{referenceid});
                Iterator ite1 = lst1.iterator();
                ConfigData condata = null;
                if (ite1.hasNext()) {
                    condata = (ConfigData) ite1.next();
                    condata.setCol(contyp.getColnum(), request.getParameter(contyp.getName()));
                } else {
                    condata = new ConfigData();
                    condata.setReferenceid(referenceid);
                    condata.setCol(contyp.getColnum(), request.getParameter(contyp.getName()));
                }
                session.save(condata);
           }
        } catch (Exception e) {
            return 0;
        }
        return successflag;
    }

	public static int insertConfigDataExternal(Session session, HttpServletRequest request, String formtype, String referenceid, HashMap<String, String> arrParam, String companyid) {
        int successflag = 0;
        int delflag = 0;
        try {
            String query1 = "from ConfigType where formtype=? and company.companyID = ?";
            List lst = HibernateUtil.executeQuery(session, query1, new Object[]{formtype, companyid});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                String query3 = "from ConfigData where referenceid=?";
                List lst1 = HibernateUtil.executeQuery(session, query3, new Object[]{referenceid});
                Iterator ite1 = lst1.iterator();
                ConfigData condata = null;
                if (ite1.hasNext()) {
                    condata = (ConfigData) ite1.next();
                    condata.setCol(contyp.getColnum(), arrParam.get(contyp.getName()));
                } else {
                    condata = new ConfigData();
                    condata.setReferenceid(referenceid);
                    condata.setCol(contyp.getColnum(), arrParam.get(contyp.getName()));
}
                session.save(condata);
           }
        } catch (Exception e) {
            return 0;
        }
        return successflag;
    }
        public static String getLocalTextforJsp(int caseid,String companyid) throws ServiceException{
            String msg="";
            switch(caseid){
                case 1:
                    msg=MessageSourceProxy.getMessage("hrms.jspMsgs.Anerroroccurredwhileperformingoperationdeskra",null,"An error occurred while performing operation. Please contact support at support@deskera.com.", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 2:
                    msg=MessageSourceProxy.getMessage("hrms.jspMsgs.Eitheryouarecomingbookmarkorthisjobdoesnotexist",null,"Either you are coming from an old link or bookmark, or this job does not exist.", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 3:
                    msg=MessageSourceProxy.getMessage("hrms.jspMsgs.JobPage",null,"Job Page", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 4:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.profile.Experience",null,"Experience", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 5:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.JobLocation",null,"Job Location", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 6:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.JobShift",null,"JobShift", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 7:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.RelocationProvided",null,"Relocation Provided", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 8:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.TravelRequired",null,"Travel Required", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 9:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.ApplyOnline",null,"Apply Online", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 10:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.Responsibilities",null,"Responsibilities", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 11:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.Skills",null,"Skills", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 12:
                    msg=MessageSourceProxy.getMessage("hrms.common.Qualifications",null,"Qualifications", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 13:
                    msg=MessageSourceProxy.getMessage("hrms.common.ApplyFor",null,"Apply For ", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 14:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.JobApplicationForm",null,"Job Application Form", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 15:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.PersonalInformation",null,"Personal Information", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 16:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.ContactInformation",null,"Contact Information", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 17:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.AcademicInformation",null,"AcademicInformation", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 18:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.WorkExperience",null,"Work Experience", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 19:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.OtherInformation",null,"OtherInformation", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 20:
                    msg=MessageSourceProxy.getMessage("hrms.common.submit",null,"Submit", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                case 21:
                    msg=MessageSourceProxy.getMessage("hrms.recruitment.InvalidEmailID",null,"Invalid E-mail ID", LocaleUtil.getCompanyLocale(companyid,1));
                    break;
                default:
                    msg="";
                    break;
            }
            return msg;
        }
     
}
