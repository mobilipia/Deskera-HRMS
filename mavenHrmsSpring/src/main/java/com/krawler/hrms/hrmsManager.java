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

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.sql.SQLException;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import java.util.*;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.common.admin.*;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.esp.handlers.PermissionHandler;
import com.krawler.hrms.compennsation.Compensation;
import com.krawler.hrms.ess.Empexperience;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.Master;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.GoalComments;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.hrms.performance.Mastercmpt;
import com.krawler.hrms.recruitment.Agency;
import com.krawler.hrms.recruitment.Allapplications;
import com.krawler.hrms.recruitment.Applicants;
import com.krawler.hrms.recruitment.Applyagency;
import com.krawler.hrms.recruitment.Jobapplicant;
import com.krawler.hrms.recruitment.Jobprofile;
import com.krawler.hrms.recruitment.Positionmain;
import com.krawler.hrms.recruitment.Recruiter;
import com.krawler.hrms.timesheet.Timesheet;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.APICallHandler;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.hrms.performance.Goalrating;
import com.krawler.utils.json.base.JSONException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.krawler.esp.handlers.ProfileHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.web.resource.Links;
import com.krawler.hrms.performance.competencyAvg;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.servlet.support.RequestContextUtils;

public class hrmsManager {
    private static SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getDummyFunction(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;

        try {

            String selQry = "SELECT Query here";

            {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", "df");
                tmpObj.put("itemdescription", "asd");

                jarr.put(tmpObj);
            }

            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
        }

        //Select Queries
        return result;
    }

    public static String getWidgetStatus(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject empty=new JSONObject();
        try {
            String userId=AuthHandler.getUserid(request);
            User userObj =(User) session.get(User.class,userId);
            String query="FROM widgetManagement as wm WHERE wm.user = ?";
            List lst = HibernateUtil.executeQuery(session, query, new Object[]{userObj});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                widgetManagement wmObj = (widgetManagement) ite.next();
                empty= new JSONObject(wmObj.getWidgetstate());
            }
            if(lst.size()==0) {
                empty=insertDefaultWidgetState(session,request);
                widgetManagement wmObj = new widgetManagement();
                wmObj.setWidgetstate(empty.toString());
                wmObj.setUser(userObj);
                wmObj.setModifiedon(new Date());
                session.save(wmObj);
            }
        } catch(SessionExpiredException ex) {
            throw ServiceException.FAILURE("hrmsManager.getWidgetStatus", ex);
        } catch(JSONException ex) {
            throw ServiceException.FAILURE("hrmsManager.getWidgetStatus", ex);
        }
        return empty.toString();
    }

    public static JSONObject insertDefaultWidgetState(Session session,HttpServletRequest request) throws JSONException, SessionExpiredException, ServiceException{
        String col1="";
        String col2="";
        String col3="";

        col1="{'id':'DSBMyWorkspaces'},{'id':'dash_recruit'}";
        col2="{'id':'dash_performance'},{'id':'dash_payroll'}";
        col3="{'id':'dash_adminwidget'},{'id':'dash_timesheet'}";

        JSONObject jobj=new JSONObject("{'col1':"+(!col1.equals("")?"["+col1+"]":"[]")+",'col2':"+(!col2.equals("")?"["+col2+"]":"[]")+",'col3':"+(!col3.equals("")?"["+col3+"]":"[]")+"}");
        return jobj;
    }

    public static String insertWidgetIntoState(Session session,HttpServletRequest request) throws SessionExpiredException, JSONException, ServiceException{
        String userId=AuthHandler.getUserid(request);
        String wid=request.getParameter("wid");
        String colno=request.getParameter("colno");
        String columnToUpdate="col"+colno;
        widgetManagement wmObj = null;
        String query="FROM widgetManagement wm WHERE wm.user.userID =?";
        List lst = HibernateUtil.executeQuery(session, query, new Object[]{userId});
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            wmObj = (widgetManagement) ite.next();
        }
        JSONObject jobj=new JSONObject(wmObj.getWidgetstate());
        JSONObject check=getColumnPositionInWidgetState(jobj,wid);
        if(!check.getBoolean("present")){
            JSONObject empty=new JSONObject();
            empty.put("id", wid);
            jobj.append(columnToUpdate, empty);
            wmObj.setWidgetstate(jobj.toString());
        }
        session.save(wmObj);
        return "{success:true}";
    }

    public static JSONObject getColumnPositionInWidgetState(JSONObject jobj,String wid) throws JSONException{
        JSONObject toReturn=new JSONObject();
        for(int i=1;i<=3;i++) {
            String column="col"+String.valueOf(i);
            com.krawler.utils.json.base.JSONArray jArr=jobj.getJSONArray(column);
            for(int j=0;j<jArr.length();j++) {
                JSONObject empty=jArr.getJSONObject(j);
                if(empty.get("id").toString().equals(wid)){
                    toReturn.put("column", column);
                    toReturn.put("position", j);
                    toReturn.put("present", true);
                    return toReturn;
                }
            }
        }
        toReturn.put("present", false);
        return toReturn;
    }

    public static String removeWidgetFromState(Session session,HttpServletRequest request) throws SessionExpiredException, ServiceException, JSONException{
        String wid=request.getParameter("wid");
        widgetManagement wmObj = new widgetManagement();
        JSONObject empty=new JSONObject();
        String userId=AuthHandler.getUserid(request);
        String query="FROM widgetManagement wm WHERE wm.user.userID =?";
        List lst = HibernateUtil.executeQuery(session, query, new Object[]{userId});
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            wmObj = (widgetManagement) ite.next();
            empty=new JSONObject(wmObj.getWidgetstate());
        }
        JSONObject _state=getColumnPositionInWidgetState(empty, wid);
        String column=_state.getString("column");
        JSONObject jobj=deleteFromWidgetStateJson(session,empty,"id",wid,column);

        // update WidgetManagement Table
        wmObj.setWidgetstate(jobj.toString());
        session.save(wmObj);
        return "{success:true}";
    }

    public static JSONObject deleteFromWidgetStateJson(Session session,JSONObject jobj,String key,String value,String column) throws JSONException{
        com.krawler.utils.json.base.JSONArray jobj_col=deleteFromWidgetStateJsonArray(session, jobj.getJSONArray(column), key, value);
        jobj.put(column, jobj_col);
        return jobj;
    }

    public static com.krawler.utils.json.base.JSONArray deleteFromWidgetStateJsonArray(Session session,com.krawler.utils.json.base.JSONArray jArr,String key,String value) throws JSONException{
        com.krawler.utils.json.base.JSONArray toReturn=new com.krawler.utils.json.base.JSONArray();
        for(int i=0;i<jArr.length();i++){
            JSONObject empty=jArr.getJSONObject(i);
            if(!empty.get("id").toString().equals(value)){
                toReturn.put(empty);
            }
        }
        return toReturn;
    }

    public static String changeWidgetStateOnDrop(Session session,HttpServletRequest request) throws SessionExpiredException, JSONException, ServiceException{
        String wid=request.getParameter("wid");
        String userId=AuthHandler.getUserid(request);
        String colno=request.getParameter("colno");
        String columnToEdit="col"+colno;
        int position=Integer.parseInt(request.getParameter("position"));

        widgetManagement wmObj = null;
        String query="FROM widgetManagement wm WHERE wm.user.userID =?";
        List lst = HibernateUtil.executeQuery(session, query, new Object[]{userId});
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            wmObj = (widgetManagement) ite.next();
        }

        JSONObject jobj=new JSONObject(wmObj.getWidgetstate());
        JSONObject previous_details=getColumnPositionInWidgetState(jobj,wid);

        String pre_column=previous_details.getString("column");
        int pre_position=previous_details.getInt("position");
        if(pre_position<position && columnToEdit.equals(pre_column)){
            position--;
        }
        jobj=deleteFromWidgetStateJson(session, jobj, "id", wid,pre_column);
        com.krawler.utils.json.base.JSONArray jobj_col=jobj.getJSONArray(columnToEdit);
        JSONObject empty=new JSONObject();
        empty.put("id", wid);
        jobj_col=insertIntoJsonArray(jobj_col, position, empty);
        jobj.put(columnToEdit, jobj_col);

        wmObj.setWidgetstate(jobj.toString());
        session.save(wmObj);
        return "{success:true}";
    }

    public static JSONArray insertIntoJsonArray(JSONArray jArr,int position,JSONObject newjObj) throws JSONException{
        JSONArray toReturn=new JSONArray();
        Boolean added=false;
        for(int i=0;i<jArr.length();i++){
            if(i==position){
                toReturn.put(newjObj);
                added=true;

            }
            JSONObject empty=jArr.getJSONObject(i);
            toReturn.put(empty);
        }
        if(!added){
            toReturn.put(newjObj);
        }
        return toReturn;
    }

    public static String getAppraisalCycles(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, SQLException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List lst = null;
        int count = 0;
        try {
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, fmt.format(new Date()),AuthHandler.getUserid(request));
            String hql = "from Appraisalcycle ac where company.companyID=? ";
            params.add(AuthHandler.getCompanyid(request));
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 1);
                hql += StringUtil.getSearchString(ss, " and", new String[]{"ac.cyclename"});
            }
            hql += "order by ac.startdate desc";
            lst = HibernateUtil.executeQuery(session, hql, params.toArray());
            count = lst.size();
            List lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            Iterator ite = lst1.iterator();
            while (ite.hasNext()) {
                Appraisalcycle app = (Appraisalcycle) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cycleid", app.getId());
                tmpObj.put("cyclename", app.getCyclename());
                tmpObj.put("startdate", AuthHandler.getDateFormatter(request).format(app.getStartdate()));
                tmpObj.put("enddate", AuthHandler.getDateFormatter(request).format(app.getEnddate()));
                if (app.getSubmitstartdate() != null) {
                    tmpObj.put("submitstartdate", AuthHandler.getDateFormatter(request).format(app.getSubmitstartdate()));
                }
                if (app.getSubmitenddate() != null) {
                    tmpObj.put("submitenddate", AuthHandler.getDateFormatter(request).format(app.getSubmitenddate()));
                    if(app.getSubmitenddate().before(userdate)){
                        tmpObj.put("canapprove", "1");
                    } else {
                        tmpObj.put("canapprove", "0");
                    }
                }
                if(app.isCycleapproval()){
                    tmpObj.put("status","1");
                }else{
                    tmpObj.put("status","0");
                }
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalcycleFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisalcycleFunction");
        } catch (SessionExpiredException ex) {
            throw new SessionExpiredException("getappraisalcycleFunction", ex.getMessage());
        }
        return result;
    }

    public static String getAllUserDetails(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String companyid = AuthHandler.getCompanyid(request);
            String SELECT_USER_INFO = " from User where company.companyID=?";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, companyid);
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                JSONObject obj = new JSONObject();
                User user = (User) itr.next();
                obj.put("userid", user.getUserID());
                obj.put("username", user.getUserLogin().getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", user.getUserLogin().getLastActivityDate());
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", 2);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getAllUserDetails", e);
        }

        return jobj.toString();
    }

    public static String getInternalJobs(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException, JSONException {
        //0->open, 1->deleted, 2->expired, 3->filled
        String result = "{'count':0, 'data':[]}";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String jobtype = request.getParameter("jobtype");
        int jobstatus = Integer.parseInt(request.getParameter("jobstatus"));
        String hql = "from Positionmain pm where company.companyID=?";
        List lst;
        List lst1;
        String status;
        Iterator ite;
        Positionmain psm;
        String ss = request.getParameter("ss");
        ArrayList params = new ArrayList();
        try {
            Company cmp = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            params.add(AuthHandler.getCompanyid(request));
            if (StringUtil.isNullOrEmpty(request.getParameter("employee"))) {
                hql += " and delflag=0 and enddate<date(now())";
                lst = HibernateUtil.executeQuery(session, hql, params.toArray());
                ite = lst.iterator();
                hql = "from Positionmain pm where company.companyID=?";
                while (ite.hasNext()) {
                    psm = (Positionmain) ite.next();
                    psm.setDelflag(2);
                    session.update(psm);
                }
                    if (jobstatus == 4) {
                        if (jobtype.equalsIgnoreCase("All")) {
                            hql += " and pm.delflag!=1";
                        } else{
                            hql += " and pm.jobtype=? and pm.delflag!=1";
                            params.add(jobtype);
                        }
                    }else{
                        if (jobtype.equalsIgnoreCase("All")) {
                            hql += " and  pm.delflag=?";
                            params.add(jobstatus);
                        } else{
                            hql += " and  pm.jobtype=? and pm.delflag=?";
                            params.add(jobtype);
                            params.add(jobstatus);
                        }
                    }
            } else {
                hql += " and pm.jobtype!=? and pm.delflag=0 and ( pm.startdate<=date(now()) and pm.enddate>=date(now()) )";
                params.add("External");
            }
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                hql += StringUtil.getSearchString(ss, "and", new String[]{"pm.jobid", "pm.details"});
            }
            lst = HibernateUtil.executeQuery(session, hql, params.toArray());
            lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            ite = lst1.iterator();
            while (ite.hasNext()) {
                JSONObject tmpObj = new JSONObject();
                psm = (Positionmain) ite.next();
                tmpObj.put("posid", psm.getPositionid());
                status = getPositionstatus(psm.getPositionid(), session, request);
                if (status.equalsIgnoreCase("none")) {
                    tmpObj.put("status", 0);
                    tmpObj.put("selectionstatus", MessageSourceProxy.getMessage("hrms.recruitment.not.applied",null,"Not applied yet", RequestContextUtils.getLocale(request)));
                } else {
                    tmpObj.put("status", 1);
                    tmpObj.put("selectionstatus", status);
                }
                tmpObj.put("posmasterid", psm.getPosition().getId());
                tmpObj.put("jobid", psm.getJobid());
                tmpObj.put("posname", psm.getPosition().getValue());
                tmpObj.put("details", psm.getDetails());
                tmpObj.put("department", psm.getDepartmentid().getValue());
                tmpObj.put("manager", psm.getManager().getFirstName() + " " + psm.getManager().getLastName());
                tmpObj.put("startdate", AuthHandler.getDateFormatter(request).format(psm.getStartdate()));
                tmpObj.put("enddate", AuthHandler.getDateFormatter(request).format(psm.getEnddate()));
                tmpObj.put("jobtype", psm.getJobtype());
                tmpObj.put("positionstatus", psm.getDelflag());
                tmpObj.put("departmentid", psm.getDepartmentid().getId());
                tmpObj.put("managerid", psm.getManager().getUserID());
                tmpObj.put("nopos", psm.getNoofpos());
                tmpObj.put("posfilled", psm.getPositionsfilled());
                String url=URLUtil.getPageURL(request,Links.loginpagewthFull,cmp.getSubDomain())+"jobs.jsp?jobid="+psm.getPositionid()+"";
                tmpObj.put("url",url );
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", lst.size());
            result = jobj.toString();
        } catch (SessionExpiredException ex) {
            throw new SessionExpiredException("getInternalJobs", ex.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getInternalJobs", ex);
        } catch (JSONException ex) {
            throw new JSONException("getInternalJobs");
        } finally {
            return result;
        }
    }

    public static String getPositionstatus(String positionid, Session session, HttpServletRequest request) throws ServiceException {
        String result;
        List tabledata;
        try {
            String hql = "from Allapplications where employee.userID=? and position.positionid=?";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request), positionid});
            if (!tabledata.isEmpty()) {
                Allapplications app = (Allapplications) tabledata.get(0);
                result = app.getStatus();
            } else {
                result = "none";
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.getStatus", ex);
        } finally {
        }
        return result;
    }

    public static String GoalandEmployees(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {

            String hql = "select empid.userID,empid.firstName,goalmaster.goalid,goalmaster.goalname,goalmaster.goaldescription,goalmaster.goalweightage,startdate,enddate from Goalmanagement   ";
            List lst = HibernateUtil.executeQuery(session, hql);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Object[] obj = (Object[]) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("empid", obj[0]);
                tmpObj.put("empname", obj[1]);
                tmpObj.put("goalid", obj[2]);
                tmpObj.put("gname", obj[3]);
                tmpObj.put("gdetails", obj[4]);
                tmpObj.put("gwth", obj[5]);
                tmpObj.put("startdate", obj[6]);
                tmpObj.put("enddate", obj[7]);
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getMasterField(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String hql = "from Master where deleted=? ";
            List lst = HibernateUtil.executeQuery(session, hql,false);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Master mst = (Master) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", mst.getName());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getManager(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String SELECT_USER_INFO = "select userID, userLogin.userName, firstName, lastName, image, " +
                    "emailID, userLogin.lastActivityDate, aboutUser, address, contactNumber,designation from User where designation in ('Manager','Project Manager') ";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO);
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("userid", row[0]);
                obj.put("username", row[1]);
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getSomeUserData(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String empid = AuthHandler.getUserid(request);
            User u = (User) session.get(User.class, empid);
            Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());

            JSONObject obj = new JSONObject();
            obj.put("userid", empid);
            obj.put("username", u.getUserLogin().getUserName());
            obj.put("designation", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue()));
            obj.put("designationid", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getId()));
            obj.put("fullname", u.getFirstName() + " " + (u.getLastName() == null ? "" : u.getLastName()));
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (JSONException e) {
            throw new JSONException("hrmsManager.getSomeUserData");
        }

        return result;
    }

    public static String getMasterDataField(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String hql = "from MasterData where masterid.id=? and ( company is null or company.companyID=? )order by value ";
            Integer i = Integer.parseInt(request.getParameter("configid"));
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{i,AuthHandler.getCompanyid(request)});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                MasterData mst = (MasterData) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", mst.getValue());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getCompetencyFunction(Session session, HttpServletRequest request, int start, int limit) throws ServiceException, SessionExpiredException, JSONException {
        String result = "{'count':0, 'data':[]}";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int cnt = 0;
        try {
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(AuthHandler.getCompanyid(request));
            String searchString = "";
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 1);
                searchString = StringUtil.getSearchString(ss, "and", new String[]{"cmptname"});
            }
            String hql = "from Mastercmpt where company.companyID=? "+ searchString;
            tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            count = tabledata.size();
            tabledata = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            cnt = tabledata.size();
            for (int i = 0; i < cnt; i++) {
                Mastercmpt log = (Mastercmpt) tabledata.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getCmptid());
                tmpObj.put("cmptname", log.getCmptname());
                tmpObj.put("cmptwt", log.getCmptwt());
                tmpObj.put("cmptdesc", log.getCmptdesc());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getCompetencyFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getCompetencyFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getCompetencyFunction");
        } finally {
            return result;
        }
    }

    public static String getCompetencyFunction2(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;

        try {
            //request.getParameter("rter");
            Query q = session.createQuery("from Mastercmpt");
            tabledata = (List) q.list();
            count = tabledata.size();
            for (int i = 0; i < count; i++) {
                Mastercmpt log = (Mastercmpt) tabledata.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getCmptid());
                tmpObj.put("cmptname", log.getCmptname());
                tmpObj.put("cmptwt", log.getCmptwt());
                tmpObj.put("cmptdesc", log.getCmptdesc());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
        }

        //Select Queries
        return result;
    }

    public static String getCompAndDesigFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException, JSONException {
        String result = "{'count':0, 'data':[]}";
        List tabledata = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int cnt = 0;
        try {
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(AuthHandler.getCompanyid(request));
            String searchString = "";
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                searchString = StringUtil.getSearchString(ss, "and", new String[]{"desig.value", "mastercmpt.cmptname"});
            }
            String hql = "from Managecmpt where mastercmpt.company.companyID=? and delflag=0 "+searchString+" order by desig.value";
            tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            count = tabledata.size();
            list = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});

            cnt = list.size();
            for (int i = 0; i < cnt; i++) {
                Managecmpt log = (Managecmpt) list.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
                tmpObj.put("cmptwt", log.getWeightage());
                tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                tmpObj.put("desname", log.getDesig().getValue());
                tmpObj.put("mcompid", log.getMid());
                tmpObj.put("designid", log.getDesig().getId());
                jarr.put(tmpObj);
            }
//            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getCompAndDesigFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getCompAndDesigFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getCompAndDesigFunction");
        } finally {
            return result;
        }
    }

    public static String showCompFunction(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String hql = "from Managecmpt where desig.id=? and delflag=0";
            tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("desig"));
            count = tabledata.size();
            for (int i = 0; i < count; i++) {
                Managecmpt log = (Managecmpt) tabledata.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
                tmpObj.put("cmptwt", log.getWeightage());
                tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                tmpObj.put("desname", log.getDesig().getValue());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
        }
        return result;
    }

    public static JSONObject Employeesappraisal(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {

            String userid = AuthHandler.getUserid(request);
            String hql = "from Appraisalmanagement where employee.userID=? and employeestatus=0 and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
            List lst = HibernateUtil.executeQuery(session, hql, userid);
            if (!lst.isEmpty()) {
                Appraisalmanagement appr = (Appraisalmanagement) lst.get(lst.size()-1);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("apprtype", appr.getAppraisaltype());
                tmpObj.put("startdate", appr.getAppcycle().getSubmitstartdate());
                tmpObj.put("enddate", appr.getAppcycle().getSubmitenddate());
                tmpObj.put("status", appr.getAppraisalstatus());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobj;
    }

    public static String AllEmployeesappraisal(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();

        int count = 0;
        try {


            String hql = "from Appraisalmanagement ";
            List lst = HibernateUtil.executeQuery(session, hql);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Appraisalmanagement appr = (Appraisalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("apprtype", appr.getAppraisaltype());
                tmpObj.put("startdate", appr.getStartdate());
                tmpObj.put("enddate", appr.getEnddate());
                tmpObj.put("apprid", appr.getAppraisalid());
                tmpObj.put("empname", appr.getEmployee().getFirstName());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String EmployeesTimesheet(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        int count = 0;
        String userid;
        String min;
        String hrs;
        try {
            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                userid = AuthHandler.getUserid(request);
            } else {
                userid = request.getParameter("empid");
            }
            startdate = (Date) fmt.parse(request.getParameter("startdate"));
            enddate = (Date) fmt.parse(request.getParameter("enddate"));
            Object[] obj = {startdate, enddate, userid};
            String hql = "from Timesheet where datevalue between ? and ? and userID.userID=? order by jobtype asc,datevalue asc ";
            List lst = HibernateUtil.executeQuery(session, hql, obj);

            Iterator ite = lst.iterator();
            JSONObject tmpObj = null;
            for (int i = 1; i <= 7; i++) {
                if (!ite.hasNext()) {
                    break;
                }
                Timesheet tmsht = (Timesheet) ite.next();
                if (i == 1) {
                    tmpObj = new JSONObject();
                    tmpObj.put("jobtype", tmsht.getJobtype());
                }
                hrs= tmsht.getWorktime().toString();
                hrs=(hrs.length()==1?"0"+hrs:hrs);
                min= tmsht.getWorktimemin().toString();
                min=(min.length()==1?"0"+min:min);
                tmpObj.put("col" + i, hrs+":"+min);
                tmpObj.put("colid" + i, tmsht.getId());
                if (i == 7) {
                    jarr.put(tmpObj);
                    i = 0;
                }

            }

            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Select Queries
        return result;
    }

    public static String AllTimesheets(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException,JSONException,SessionExpiredException, ParseException, SQLException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        String count = "";
        try {
            startdate = fmt.parse(request.getParameter("startdate"));
            enddate = fmt.parse(request.getParameter("enddate"));
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(startdate);
            params.add(enddate);
            params.add(AuthHandler.getCompanyid(request));

            String hql = "select userID.userID,userID.firstName,approved,approvedby,userID.lastName from Timesheet where datevalue between ? and ? and  userID.company.companyID=?";
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"userID.firstName","userID.lastName"});
                    hql +=searchQuery;
            }
            hql +=" group by userID.userID";
            List lst = HibernateUtil.executeQuery(session, hql, params.toArray());
            count = String.valueOf(lst.size());
            List lst2 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});

            Iterator ite = lst2.iterator();
            while (ite.hasNext()) {
                Object[] obj1 = (Object[]) ite.next();

                //Calculate Week Work for that employee
                Object[] innerobj = {startdate, enddate, obj1[0]};
                String hql1 = "from Timesheet where datevalue between ? and ? and userID.userID=? order by jobtype asc,datevalue asc ";
                List lst1 = HibernateUtil.executeQuery(session, hql1, innerobj);

                Iterator ite1 = lst1.iterator();
                int total = 0;
                int min=0;
                for (int i = 1; i <= 7; i++) {
                    if (!ite1.hasNext()) {
                        break;
                    }
                    Timesheet tmsht = (Timesheet) ite1.next();
                    total += tmsht.getWorktime();
                    min +=tmsht.getWorktimemin();
                    if(min>=60){
                     total++;
                     min -=60;
                    }
                    if (i == 7) {
                        i = 0;
                    }

                }
                if (obj1[4] == null) {
                    obj1[4] = "";
                }
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("empid", obj1[0]);
                tmpObj.put("empname", obj1[1] + " " + obj1[4]);
                tmpObj.put("status", obj1[2]);
                tmpObj.put("approvedby", obj1[3]);
                tmpObj.put("work", total+" hrs,"+min+" mins");
                tmpObj.put("hours", 48);
                tmpObj.put("startdate",AuthHandler.getDateFormatter(request).format(startdate));
                tmpObj.put("enddate", AuthHandler.getDateFormatter(request).format(enddate));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (JSONException e) {
           throw ServiceException.FAILURE("hrmsManager.AllTimesheets", e);
        }catch(SessionExpiredException e){
            throw ServiceException.FAILURE("hrmsManager.AllTimesheets", e);
        }catch(ParseException e){
            throw ServiceException.FAILURE("hrmsManager.AllTimesheets", e);
        }

        //Select Queries
        return result;
    }

    public static String getCompensationFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, ParseException {
        String result = "";
        List tabledata = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        String query = null;
        String hql = null;
        int cnt = 0;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String uid = AuthHandler.getUserid(request);
            int showcomp = Integer.parseInt(request.getParameter("compensation"));
            if (showcomp == 0) {
                query = "from Compensation where mancompen.userID=?";
                tabledata = HibernateUtil.executeQuery(session, query, uid);
                count = tabledata.size();
                hql = "from Compensation where mancompen.userID=?";
                list = HibernateUtil.executeQueryPaging(session, hql, new Object[]{uid}, new Integer[]{start, limit});
                cnt = list.size();
            } else {
                query = "from Compensation where empcompen.userID=?";
                tabledata = HibernateUtil.executeQuery(session, query, uid);
                count = tabledata.size();
                hql = "from Compensation where empcompen.userID=?";
                list = HibernateUtil.executeQueryPaging(session, hql, new Object[]{uid}, new Integer[]{start, limit});
                cnt = list.size();
            }
            for (int i = 0; i < cnt; i++) {
                Compensation log = (Compensation) tabledata.get(i);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getEmpcompen().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("comeid", log.getEmpcompen().getUserID());
                if (log.getEmpcompen().getDeleteflag()==1) {
                    tmpObj.put("comename", log.getEmpcompen().getFirstName() + " " + log.getEmpcompen().getLastName());
                    //tmpObj.put("comdes", log.getEmpcompen().getDesignation());
                    tmpObj.put("comdes", ua.getDesignationid().getValue());
                    tmpObj.put("comdate", AuthHandler.getDateFormatter(request).format(fmt.parse(log.getComdate())));
                    tmpObj.put("comcsal", log.getComcsal());
                    tmpObj.put("comnsal", log.getComnsal());
                    tmpObj.put("comprat", log.getPerformanceid().getValue());
                    tmpObj.put("compi", log.getCompi());
                    tmpObj.put("comdi", log.getComdi());
                    tmpObj.put("promotion", log.getPromotionid().getValue());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getCompensationFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getCompensationFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getCompensationFunction");
        } catch (ParseException ex) {
            throw new JSONException("getCompensationFunction");
        } finally {
        }
        return result;
    }

    public static String Employeesgoalfinal(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String userid;
        int count = 0;
        List lst1=null;
        List lst=null;
        String ss = request.getParameter("ss");
        ArrayList params = new ArrayList();
        try {

            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                userid = AuthHandler.getUserid(request);
             String hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? ";
             params.add(userid);
             params.add(AuthHandler.getCompanyid(request));
             params.add(false);
             if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 1);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"goalname"});
                hql +=searchQuery;
            }
             lst = HibernateUtil.executeQuery(session, hql, params.toArray());
             count = lst.size();
             lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            } else {
                userid = request.getParameter("empid");
             String hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? and manager.userID=?";
             params.add(userid);
             params.add(AuthHandler.getCompanyid(request));
             params.add(false);
             params.add(AuthHandler.getUserid(request));
             if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 1);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"goalname"});
                hql +=searchQuery;
            }
             lst = HibernateUtil.executeQuery(session, hql, params.toArray());
             count = lst.size();
             lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            }
            Iterator ite = lst1.iterator();
            while (ite.hasNext()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                String gid=fgmt.getId();
                String comment = "from GoalComments where goalid.id=? ";
                List tabledata = HibernateUtil.executeQuery(session, comment,gid);
                int cnt = tabledata.size();
                tmpObj.put("gid",gid);
                tmpObj.put("gname", fgmt.getGoalname());
                tmpObj.put("gdescription", fgmt.getGoaldesc());
                tmpObj.put("gwth", fgmt.getGoalwth());
                tmpObj.put("gcontext", fgmt.getContext());
                tmpObj.put("gpriority", fgmt.getPriority());
                tmpObj.put("gstartdate", AuthHandler.getDateFormatter(request).format(fgmt.getStartdate()));
                tmpObj.put("genddate", AuthHandler.getDateFormatter(request).format(fgmt.getEnddate()));
                tmpObj.put("gcomment", cnt);
                tmpObj.put("internal", fgmt.isInternal());
                //tmpObj.put("gassignedby", fgmt.getManager().getFirstName()+" "+fgmt.getManager().get);
                tmpObj.put("gassignedby", fgmt.getManager().getFirstName() + " " + (fgmt.getManager().getLastName() == null ? "" : fgmt.getManager().getLastName()));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Select Queries
        return result;
    }

    public static String getCompetencyFunction3(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, JSONException {
        String result = "";
        List tabledata = null;
        List checkdata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        boolean flag = false;

        try {
            String desigid = request.getParameter("desig");
            String hql = "from Mastercmpt where company.companyID=? and cmptid not in (select mastercmpt from Managecmpt where desig.id=? and delflag=0)";
            Object[] obj = {AuthHandler.getCompanyid(request),desigid};
            List lst = HibernateUtil.executeQuery(session, hql, obj);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                JSONObject tmpObj = new JSONObject();
                Mastercmpt log = (Mastercmpt) ite.next();
                tmpObj.put("cmptid", log.getCmptid());
                tmpObj.put("cmptname", log.getCmptname());
                tmpObj.put("cmptwt", log.getCmptwt());
                tmpObj.put("cmptdesc", log.getCmptdesc());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getCompetencyFunction3", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getCompetencyFunction3", ex);
        } catch (JSONException ex) {
            throw new JSONException("getCompetencyFunction3");
                } finally {
        }
        return result;
    }

    public static String showCompGridFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException, JSONException {
        String result = "";
        String desid = request.getParameter("desig");
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        //String  desid = "";

        try {
            String hql = "from Managecmpt where desig.id=? and mastercmpt.company.companyID=? and delflag=0";
            Object[] obj = {desid, AuthHandler.getCompanyid(request)};
            tabledata = HibernateUtil.executeQuery(session, hql, obj);
            count = tabledata.size();
            for (int i = 0; i < count; i++) {
                Managecmpt log = (Managecmpt) tabledata.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
                tmpObj.put("cmptwt", log.getWeightage());
                tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                tmpObj.put("desname", log.getDesig().getValue());
                jarr.put(tmpObj);
            }
            //jarr.put(tmpObj);

            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("showCompGridFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("showCompGridFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("showCompGridFunction");
        } finally {
        }
        return result;
    }

    public static JSONObject externaluser(Session session, String username,
        String passwd, String subdomain) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            //String subdomain = "hrms";
            //String cmpid=AuthHandler.getCompanyid(session, subdomain);
            String SELECT_USER_INFO = "from Jobapplicant where username=? and password=? and company.subDomain=? and deleted=false";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, new Object[]{username, passwd, subdomain});
            Iterator ite = list.iterator();
            if (ite.hasNext()) {
                Jobapplicant exapt = (Jobapplicant) ite.next();
                jobj.put("success", true);
                jobj.put("lid", exapt.getApplicantid());
                jobj.put("username", exapt.getUsername());
                jobj.put("companyid", exapt.getCompany().getCompanyID());
                jobj.put("company", exapt.getCompany().getCompanyName());
				KWLTimeZone	timeZone = (KWLTimeZone) session.load(KWLTimeZone.class, "1");
                jobj.put("timezoneid", timeZone.getTimeZoneID());
                jobj.put("tzdiff", timeZone.getDifference());
                jobj.put("tzid", timeZone.getTzID());
				KWLDateFormat dateFormat =(KWLDateFormat) session.load(KWLDateFormat.class, "4");
				jobj.put("dateformatid", dateFormat.getFormatID());
				jobj.put("dateformat", dateFormat.getJavaForm());
            } else {
                jobj.put("failure", true);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("ExternalUser.verifyLogin", e);
        }
        return jobj;
    }

    public static String showInternalApplicantsFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        String result = "";
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int cnt = 0;
        try {
            String hql = "from Applicants where jobid=?";
            list=HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("jobid")});
            count = list.size();
            list = HibernateUtil.executeQueryPaging(session, hql, new Object[]{request.getParameter("jobid")}, new Integer[]{start, limit});
            cnt = list.size();
            for (int i = 0; i < cnt; i++) {
                Applicants log = (Applicants) list.get(i);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getApplempid().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("empid", log.getApplempid().getUserID());
                tmpObj.put("appname", log.getApplempid().getFirstName()+" "+log.getApplempid().getLastName());
                tmpObj.put("designation", ua.getDesignationid().getValue());
                tmpObj.put("department", ua.getDepartment().getValue());
                tmpObj.put("email", log.getApplempid().getEmailID());
                tmpObj.put("contactno", log.getApplempid().getContactNumber());
                tmpObj.put("appid", log.getAppid());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("showInternalApplicantsFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("showInternalApplicantsFunction");
        } finally {
        }
        return result;
    }

    public static String showAgencyFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException, JSONException {
        String result = "{'count':0, 'data':[]}";
        String hql2 = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        List lst2 = null;

        try {
            String cmpid = AuthHandler.getCompanyid(request);
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(cmpid);
            params.add(0);
            String searchString = "";
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 1);
                searchString = StringUtil.getSearchString(ss, "and", new String[]{"agencyname"});
            }
            hql2 = "from Agency where company.companyID=? and delflag=? "+ searchString;
            lst2 = HibernateUtil.executeQuery(session, hql2, params.toArray());
            count = lst2.size();
            lst2 = HibernateUtil.executeQueryPaging(session, hql2, params.toArray(), new Integer[]{start, limit});
            for (int i = 0; i < lst2.size(); i++) {
                Agency log = (Agency) lst2.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("agid", log.getAgencyid());
                tmpObj.put("agname", log.getAgencyname());
                tmpObj.put("url", log.getAgencyweb());
                tmpObj.put("cost", log.getReccost());
                tmpObj.put("manager", log.getApprman().getFirstName() + " " + (log.getApprman().getLastName() == null ? "" : log.getApprman().getLastName()));
                tmpObj.put("managerid", log.getApprman().getUserID());
                tmpObj.put("contactperson", log.getConperson());
                tmpObj.put("address", log.getAgencyadd());
                tmpObj.put("phoneno", log.getAgencyno());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("showAgencyFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("showAgencyFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("showAgencyFunction");
        } finally {
            return result;
        }
    }

    public static String jobsearch(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException, JSONException, SQLException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String jobtype = "Internal";
        String hql = "";
        int count = 0;
        String status="";
        String userid=request.getParameter("userid");
        String ss = request.getParameter("ss");
        ArrayList params = new ArrayList();
        try {
            List lst = null;
            Company cmp=(Company)session.get(Company.class,AuthHandler.getCompanyid(request));
            if (StringUtil.isNullOrEmpty(request.getParameter("position"))) {
                hql = "from Positionmain where jobtype!=? and company.companyID=? and delflag=0 and ( startdate<=date(now()) and enddate>=date(now()) )";
                params.add(jobtype);
                params.add(AuthHandler.getCompanyid(request));
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"jobid"});
                    hql +=searchQuery;
                }
                lst = HibernateUtil.executeQuery(session, hql, params.toArray());
                count=lst.size();
                lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            } else {
                hql = "from Positionmain  where position.id=? and jobtype!=? and company.companyID=? and ( startdate<=date(now()) and enddate>=date(now()) )";
                params.add(request.getParameter("position"));
                params.add(jobtype);
                params.add(AuthHandler.getCompanyid(request));
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"jobid"});
                    hql +=searchQuery;
                }
                lst = HibernateUtil.executeQuery(session, hql, params.toArray());
                count=lst.size();
                lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            }
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Positionmain extmt = (Positionmain) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("jid", extmt.getPositionid());
                status = getappPositionstatus(userid,extmt.getPositionid(), session, request);
                if (status.equalsIgnoreCase("none")) {
                    tmpObj.put("status", 0);
                    tmpObj.put("selectionstatus", MessageSourceProxy.getMessage("hrms.recruitment.not.applied",null,"Not applied yet", RequestContextUtils.getLocale(request)));
                } else {
                    tmpObj.put("status", 1);
                    tmpObj.put("selectionstatus", status);
                }
                tmpObj.put("jobname", extmt.getPosition().getValue());
                tmpObj.put("jobpositionid", extmt.getJobid());
                tmpObj.put("jdescription", extmt.getDetails());
                tmpObj.put("jstartdate", AuthHandler.getUserDateFormatter(request, session).format(extmt.getStartdate()));
                tmpObj.put("jenddate", AuthHandler.getUserDateFormatter(request, session).format(extmt.getEnddate()));
                tmpObj.put("jdepartment", extmt.getDepartmentid().getValue());
                tmpObj.put("posmasterid", extmt.getPosition().getId());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("jobsearch", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("jobsearch", ex);
        } catch (JSONException ex) {
            throw new JSONException("jobsearch");
        } finally {
        }
        return result;
    }

    public static String getappPositionstatus(String appid, String positionid, Session session, HttpServletRequest request) throws ServiceException {
        String result;
        List tabledata;
        try {
            String hql = "from Allapplications where jobapplicant.applicantid=? and position.positionid=?";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{appid, positionid});
            if (!tabledata.isEmpty()) {
                Allapplications app = (Allapplications) tabledata.get(0);
                result = app.getStatus();
            } else {
                result = "none";
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.getStatus", ex);
        } finally {
        }
        return result;
    }

    public static String GetExtapplyjobs(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException {
        String result = "{'count':0, 'data':[]}";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String hql = "";
        String count = "0";
        try {
            List lst = null;
            List lst1 = null;
            String cmpid=AuthHandler.getCompanyid(request);
            ArrayList params = new ArrayList();
            String ss = request.getParameter("ss");
            String searchString = "";
            if (StringUtil.isNullOrEmpty(request.getParameter("userid"))) {
                int gridst=Integer.parseInt(request.getParameter("gridst"));
                int emptype = Integer.parseInt(request.getParameter("employeetype"));
                String[] searchArray = null;
                if(emptype == 1) {//Internal
                    searchArray = new String[]{"employee.firstName","employee.lastName","position.jobid","position.departmentid.value"};
                } else {//External
                    searchArray = new String[]{"jobapplicant.firstname", "jobapplicant.lastname","position.jobid","position.departmentid.value"};
                }
                if(StringUtil.isNullOrEmpty(request.getParameter("status"))){
                    params.add(gridst);
                    params.add(cmpid);
                    params.add(emptype);
                    if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(params, ss, 4);
                        searchString = StringUtil.getSearchString(ss, "and", searchArray);
                    }
                    hql = "from Allapplications where applicationflag = ? and company.companyID=? and delflag=0 and employeetype=? "+ searchString;
                } else {
                    params.add(gridst);
                    params.add(cmpid);
                    params.add(request.getParameter("status"));
                    params.add(Integer.parseInt(request.getParameter("employeetype")));

                    if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(params, ss, 4);
                        searchString = StringUtil.getSearchString(ss, " and", searchArray);
                    }
                    hql = "from Allapplications where applicationflag = ? and company.companyID=? and delflag=0 and status=? and employeetype=? " + searchString;
                }
                lst1 = HibernateUtil.executeQuery(session, hql,params.toArray());
                count = String.valueOf(lst1.size());
                lst = HibernateUtil.executeQueryPaging(session, hql,params.toArray(), new Integer[]{start, limit});

            } else {
                params.add(request.getParameter("userid"));
                params.add(cmpid);
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    searchString = StringUtil.getSearchString(ss, "and", new String[]{"position.jobid","position.position.value"});
                }
                hql = "from Allapplications where jobapplicant.applicantid=? and company.companyID=? and delflag=0 "+ searchString;
                lst1 = HibernateUtil.executeQuery(session, hql, params.toArray());
                count = String.valueOf(lst1.size());
                lst = HibernateUtil.executeQueryPaging(session, hql,params.toArray(), new Integer[]{start, limit});
            }
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Allapplications allapps = (Allapplications) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", allapps.getId());
                tmpObj.put("posid", allapps.getPosition().getPositionid());
                tmpObj.put("designationid", allapps.getPosition().getPosition().getId());
                tmpObj.put("departmentid", allapps.getPosition().getDepartmentid().getId());
                tmpObj.put("department", allapps.getPosition().getDepartmentid().getValue());
                tmpObj.put("designation", allapps.getPosition().getPosition().getValue());
                tmpObj.put("jobpositionid", allapps.getPosition().getJobid()+ " (" + allapps.getPosition().getPosition().getValue() + ")");
                tmpObj.put("jobid", allapps.getPosition().getPositionid());
                tmpObj.put("vacancy", allapps.getPosition().getNoofpos());
                tmpObj.put("filled", allapps.getPosition().getPositionsfilled());
                tmpObj.put("jname", allapps.getPosition().getPosition().getValue());
                tmpObj.put("applydt", AuthHandler.getDateFormatter(request).format(allapps.getApplydate()));
                tmpObj.put("interviewdt", (allapps.getInterviewdate() == null ? "" : AuthHandler.getDateFormatter(request).format(allapps.getInterviewdate())));
                tmpObj.put("joiningdate", (allapps.getJoiningdate() == null ? "" : AuthHandler.getDateFormatter(request).format(allapps.getJoiningdate())));
                tmpObj.put("status", allapps.getStatus());
                if (allapps.getJobapplicant() != null) {
                    tmpObj.put("apcntid", allapps.getJobapplicant().getApplicantid());
                    tmpObj.put("cname", allapps.getJobapplicant().getFirstname() + " " + allapps.getJobapplicant().getLastname());
                    tmpObj.put("email", allapps.getJobapplicant().getEmail());
                    tmpObj.put("fname", allapps.getJobapplicant().getFirstname());
                    tmpObj.put("lname", allapps.getJobapplicant().getLastname());
                    tmpObj.put("contact", allapps.getJobapplicant().getContactno());
                    tmpObj.put("addr", allapps.getJobapplicant().getAddress1());
                }else{
                    tmpObj.put("apcntid", allapps.getEmployee().getUserID());
                    tmpObj.put("cname", allapps.getEmployee().getFirstName() + " " + allapps.getEmployee().getLastName());
                    tmpObj.put("email", allapps.getEmployee().getEmailID());
                    tmpObj.put("fname", allapps.getEmployee().getFirstName());
                    tmpObj.put("lname", allapps.getEmployee().getLastName());
                    tmpObj.put("contact", allapps.getEmployee().getContactNumber());
                    tmpObj.put("addr", allapps.getEmployee().getAddress());
                }
                if (!StringUtil.isNullOrEmpty(allapps.getRecruiter())) {
                    String recruiter[]=allapps.getRecruiter().split(",");
                    for(int j=0;j<recruiter.length;j++){
                        Recruiter r=(Recruiter) session.get(Recruiter.class,recruiter[j]);
                        tmpObj.append("recruiter", r.getRecruit().getFirstName() + " " + r.getRecruit().getLastName());
                    }
                }
                tmpObj.put("callback",(allapps.getCallback() == null ? "" :allapps.getCallback().getValue()));
                tmpObj.put("interviewplace", allapps.getInterviewplace());
                tmpObj.put("interviewcomment", allapps.getInterviewcomment());
                tmpObj.put("rank",(allapps.getRank() == null ? "" :allapps.getRank().getValue()));
                tmpObj.put("rejectedbefore", allapps.getRejectedbefore());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("GetExtapplyjobs", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("GetExtapplyjobs", ex);
        } catch (JSONException ex) {
            throw new JSONException("GetExtapplyjobs");
        } finally {
            return result;
        }
    }

    public static String viewRecruitersFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException,JSONException {
        String result = "";
        List tabledata = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int cnt = 0;
        try {
            String cmpid=AuthHandler.getCompanyid(request);
            String q1 = "from Recruiter where delflag=? and recruit.deleted=? and recruit.company.companyID=?";
            tabledata = HibernateUtil.executeQuery(session, q1, new Object[]{1,false,cmpid});
            count = tabledata.size();
            String hql = "from Recruiter where delflag=? and recruit.deleted=? and recruit.company.companyID=?";
            list = HibernateUtil.executeQueryPaging(session, hql, new Object[]{1,false,cmpid}, new Integer[]{start, limit});
            cnt = list.size();
            for (int i = 0; i < cnt; i++) {
                Recruiter log = (Recruiter) tabledata.get(i);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getRecruit().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("rid", log.getRid());
                tmpObj.put("fname", log.getRecruit().getFirstName());
                tmpObj.put("lname", log.getRecruit().getLastName());
                tmpObj.put("fullname", log.getRecruit().getFirstName() + " " + (log.getRecruit().getLastName() == null ? "" : log.getRecruit().getLastName()));
                //tmpObj.put("desig", log.getRecruit().getDesignation());
                tmpObj.put("desig",(ua.getDesignationid()==null?" ":ua.getDesignationid().getValue()));
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsManager.viewRecruitersFunction", e);
        }catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.viewRecruitersFunction", e);
        }
        return result;
    }
 

    public static String applyAgencyFunction(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;

        try {
            String[] recids = request.getParameterValues("jobids");
            String agencyid = request.getParameter("ageid");
            for (int i = 0; i < recids.length; i++) {
                Object[] obj = {agencyid, recids[i]};
                String hql = "Delete from Applyagency where agencyid=? and posid=?";
                HibernateUtil.executeUpdate(session, hql, obj);
            }

            for (int i = 0; i < recids.length; i++) {
                Agency md2 = (Agency) session.load(Agency.class, agencyid);
                Positionmain md1 = (Positionmain) session.load(Positionmain.class, recids[i]);
                String id = UUID.randomUUID().toString();
                Applyagency contact = new Applyagency();
                contact.setApplyid(id);
                contact.setApplypos(md1);
                contact.setApplyagency(md2);
                session.save(contact);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.JOB_TO_AGENCY, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has assigned job position " + contact.getApplypos().getJobid() + " to agency " + contact.getApplyagency().getAgencyname(),request);
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static String getfinalReportFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, ParseException {
        String result = "";
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        boolean isSummary = (request.getParameter("view").equals("0"))?true:false;
        int showfinal;
        try{
            showfinal = Integer.parseInt(request.getParameter("finalreport"));
        }catch(NumberFormatException e){
            showfinal = 0;
        }
        int count = 0;
        Iterator ite;
        String qry = null;
        String uid = "";
        Date startdate = null;
        Date enddate = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String appcycle=request.getParameter("appraisalcycid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) {
                if(!isSummary){
                    if ((!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request)))) {
                        qry = "from Appraisalmanagement  where reviewstatus=0 and appraisalstatus=? and appcycle.id=?";
                        list = HibernateUtil.executeQuery(session, qry, new Object[]{"submitted",appcycle});
                        count = list.size();
                        list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{"submitted",appcycle}, new Integer[]{start, limit});
                    } else {
                        qry = "from Assignreviewer where reviewer.userID=? and reviewerstatus=1";
                        list = HibernateUtil.executeQuery(session, qry, AuthHandler.getUserid(request));
                        ite = list.iterator();
                        String users = "";
                        while (ite.hasNext()) {
                            Assignreviewer log = (Assignreviewer) ite.next();
                            users += "'" + log.getEmployee().getUserID() + "',";
                        }
                        if (users.length() > 0) {
                            users = users.substring(0, users.length() - 1);
                        }
                        qry = "from Appraisalmanagement where employee.userID in (" + users + ") and reviewstatus=0 and appraisalstatus=? and appcycle.id=? ";
                        list = HibernateUtil.executeQuery(session, qry, new Object[]{"submitted",appcycle});
                        count = list.size();
                        list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{"submitted",appcycle}, new Integer[]{start, limit});
                    }
                }
                else{
                    if ((!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request)))) {
                        qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where reviewstatus=0 and appraisalstatus=? and appcycle.id=? group by employee.userID";
                        list = HibernateUtil.executeQuery(session, qry, new Object[]{"submitted",appcycle});
                        count = list.size();
                        list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{"submitted",appcycle}, new Integer[]{start, limit});

                    } else {
                        qry = "from Assignreviewer where reviewer.userID=? and reviewerstatus=1";
                        list = HibernateUtil.executeQuery(session, qry, AuthHandler.getUserid(request));
                        ite = list.iterator();
                        String users = "";
                        while (ite.hasNext()) {
                            Assignreviewer log = (Assignreviewer) ite.next();
                            users += "'" + log.getEmployee().getUserID() + "',";
                        }
                        if (users.length() > 0) {
                            users = users.substring(0, users.length() - 1);
                        }
                        qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where employee.userID in (" + users + ") and reviewstatus=0 and appraisalstatus=? and appcycle.id=? group by employee.userID";
                        list = HibernateUtil.executeQuery(session, qry, new Object[]{"submitted",appcycle});
                        count = list.size();
                        list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{"submitted",appcycle}, new Integer[]{start, limit});
                    }
                }
            } else {
                if(!isSummary){
                    if (showfinal == 0) {
                        if (!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request))) {
                            if (StringUtil.isNullOrEmpty(request.getParameter("startdate")) && StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                                qry = "from Appraisalmanagement where reviewstatus=2 and employee.deleted=? and appcycle.id=?";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false,appcycle}, new Integer[]{start, limit});
                            } else {
                                startdate = df.parse(request.getParameter("startdate"));
                                enddate = df.parse(request.getParameter("enddate"));
                                qry = "from Appraisalmanagement where reviewstatus=2 and employee.deleted=? and managersubmitdate between ? and ? and appcycle.id=?";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false, startdate, enddate,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false, startdate, enddate,appcycle}, new Integer[]{start, limit});
                            }
                        } else {
                            qry = "from Assignmanager where assignman.userID=? and managerstatus=1";
                            list = HibernateUtil.executeQuery(session, qry, AuthHandler.getUserid(request));
                            ite = list.iterator();
                            String users = "";
                            while (ite.hasNext()) {
                                Assignmanager log = (Assignmanager) ite.next();
                                users += "'" + log.getAssignemp().getUserID() + "',";
                            }
                            if (users.length() > 0) {
                                users = users.substring(0, users.length() - 1);
                            }
                            if (StringUtil.isNullOrEmpty(request.getParameter("startdate")) && StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                                qry = "from Appraisalmanagement where employee.userID in (" + users + ") and reviewstatus=2 and employee.deleted=? and appcycle.id=?";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false,appcycle}, new Integer[]{start, limit});
                            } else {
                                startdate = df.parse(request.getParameter("startdate"));
                                enddate = df.parse(request.getParameter("enddate"));
                                qry = "from Appraisalmanagement where employee.userID in (" + users + ") and reviewstatus=2 and employee.deleted=? and managersubmitdate between ? and ? and appcycle.id=?";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false, startdate, enddate,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false, startdate, enddate,appcycle}, new Integer[]{start, limit});
                            }
                        }
                    } else {
                        if (StringUtil.isNullOrEmpty(request.getParameter("userid"))) {
                            uid = AuthHandler.getUserid(request);
                        } else {
                            uid = request.getParameter("userid");
                        }
                        if (StringUtil.isNullOrEmpty(request.getParameter("startdate")) && StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                            qry = "from Appraisalmanagement where employee.userID=? and reviewstatus=2";
                            list = HibernateUtil.executeQuery(session, qry, new Object[]{uid});
                            count = list.size();
                            list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{uid}, new Integer[]{start, limit});
                        } else {
                            startdate = df.parse(request.getParameter("startdate"));
                            enddate = df.parse(request.getParameter("enddate"));
                            qry = "from Appraisalmanagement where employee.userID=? and reviewstatus=2 and managersubmitdate between ? and ?";
                            list = HibernateUtil.executeQuery(session, qry, new Object[]{uid, startdate, enddate});
                            count = list.size();
                            list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{uid, startdate, enddate}, new Integer[]{start, limit});
                        }
                    }
                }else{
                    if (showfinal == 0) {
                        if (!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request))) {
                            if (StringUtil.isNullOrEmpty(request.getParameter("startdate")) && StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                                qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where reviewstatus=2 and employee.deleted=? and appcycle.id=? group by employee.userID";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false,appcycle}, new Integer[]{start, limit});
                            } else {
                                startdate = df.parse(request.getParameter("startdate"));
                                enddate = df.parse(request.getParameter("enddate"));
                                qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where reviewstatus=2 and employee.deleted=? and managersubmitdate between ? and ? and appcycle.id=? group by employee.userID";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false, startdate, enddate,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false, startdate, enddate,appcycle}, new Integer[]{start, limit});
                            }
                        } else {
                            qry = "from Assignmanager where assignman.userID=? and managerstatus=1";
                            list = HibernateUtil.executeQuery(session, qry, AuthHandler.getUserid(request));
                            ite = list.iterator();
                            String users = "";
                            while (ite.hasNext()) {
                                Assignmanager log = (Assignmanager) ite.next();
                                users += "'" + log.getAssignemp().getUserID() + "',";
                            }
                            if (users.length() > 0) {
                                users = users.substring(0, users.length() - 1);
                            }
                            if (StringUtil.isNullOrEmpty(request.getParameter("startdate")) && StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                                qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where employee.userID in (" + users + ") and reviewstatus=2 and employee.deleted=? and appcycle.id=? group by employee.userID";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false,appcycle}, new Integer[]{start, limit});
                            } else {
                                startdate = df.parse(request.getParameter("startdate"));
                                enddate = df.parse(request.getParameter("enddate"));
                                qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where employee.userID in (" + users + ") and reviewstatus=2 and employee.deleted=? and managersubmitdate between ? and ? and appcycle.id=? group by employee.userID";
                                list = HibernateUtil.executeQuery(session, qry, new Object[]{false, startdate, enddate,appcycle});
                                count = list.size();
                                list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{false, startdate, enddate,appcycle}, new Integer[]{start, limit});
                            }
                        }
                    } else {
                        if (StringUtil.isNullOrEmpty(request.getParameter("userid"))) {
                            uid = AuthHandler.getUserid(request);
                        } else {
                            uid = request.getParameter("userid");
                        }
                        if (StringUtil.isNullOrEmpty(request.getParameter("startdate")) && StringUtil.isNullOrEmpty(request.getParameter("enddate"))) {
                            qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where employee.userID=? and reviewstatus=2 group by appcycle.id";
                            list = HibernateUtil.executeQuery(session, qry, new Object[]{uid});
                            count = list.size();
                            list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{uid}, new Integer[]{start, limit});
                        } else {
                            startdate = df.parse(request.getParameter("startdate"));
                            enddate = df.parse(request.getParameter("enddate"));
                            qry = "select am,avg(managercompscore),avg(managergoalscore),avg(totalscore),avg(managergapscore) from Appraisalmanagement am where employee.userID=? and reviewstatus=2 and managersubmitdate between ? and ? group by appcycle.id";
                            list = HibernateUtil.executeQuery(session, qry, new Object[]{uid, startdate, enddate});
                            count = list.size();
                            list = HibernateUtil.executeQueryPaging(session, qry, new Object[]{uid, startdate, enddate}, new Integer[]{start, limit});
                        }
                    }
                }
            }
            Double prevSalary ;
            if(!isSummary){
                ite = list.iterator();
                while (ite.hasNext()) {
                    Appraisalmanagement log = (Appraisalmanagement) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getEmployee().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    if(log.getReviewsalaryincrement() != 0)
                        prevSalary = (Double.parseDouble(ua.getSalary())*100)/(log.getReviewsalaryincrement()+100);
                    else
                        prevSalary = Double.parseDouble(ua.getSalary());
                    tmpObj.put("eid", log.getEmployee().getUserID());
                    tmpObj.put("ename", log.getEmployee().getFirstName() + " " + log.getEmployee().getLastName());
                    tmpObj.put("salary", ua.getSalary());
                    tmpObj.put("prevsalary", prevSalary.toString());
                    tmpObj.put("man", log.getManager().getFirstName() + " " + log.getManager().getLastName());
                    tmpObj.put("desigid", (log.getOriginaldesignation() == null ? "" : log.getOriginaldesignation().getId()));
                    tmpObj.put("desig", (log.getOriginaldesignation() == null ? "" : log.getOriginaldesignation().getValue()));
                    tmpObj.put("date", log.getManagersubmitdate()!=null?AuthHandler.getDateFormatter(request).format(log.getManagersubmitdate()):"");
                    tmpObj.put("cscore", log.getManagercompscore());
                    tmpObj.put("gscore", log.getManagergoalscore());
                    tmpObj.put("tscore", log.getTotalscore());
                    tmpObj.put("gapscore", log.getManagergapscore());
                    tmpObj.put("fid", log.getAppraisalid());
                    tmpObj.put("empcom", log.getEmployeecomment());
                    tmpObj.put("mancom", log.getManagercomment());
                    tmpObj.put("performance", log.getPerformance());
                    if (log.getPerformance() != null) {
                        tmpObj.put("prate", log.getPerformance().getId());
                        tmpObj.put("performance", log.getPerformance().getValue());
                    } else {
                        tmpObj.put("prate", "none");
                        tmpObj.put("performance", "");
                    }
                    if (log.getAppcycle() != null) {
                        tmpObj.put("apptype", log.getAppcycle().getCyclename());
                    } else{
                        tmpObj.put("apptype", "");
                    }
                    tmpObj.put("status", log.getAppraisalstatus());
                    tmpObj.put("reviewstatus", log.getReviewstatus());
                    tmpObj.put("reviewcomment", log.getReviewercomment());
                    tmpObj.put("salaryrecommend", log.getSalaryrecommend());
                    if (log.getNewdesignation() != null) {
                        tmpObj.put("newdesignation", log.getNewdesignation().getId());
                    } else {
                        tmpObj.put("newdesignation", "");
                    }
                    if (log.getNewdepartment() != null) {
                        tmpObj.put("newdepartment", log.getNewdepartment().getId());
                    } else {
                        tmpObj.put("newdepartment", "");
                    }
                    tmpObj.put("salaryincrement", log.getReviewsalaryincrement());
                    if(log.getAppcycle()!=null){
                        tmpObj.put("appcycleid", log.getAppcycle().getId());
                        tmpObj.put("appcyclename", log.getAppcycle().getCyclename());
                        tmpObj.put("appcyclesdate", log.getAppcycle().getStartdate());
                        tmpObj.put("appcycleedate", log.getAppcycle().getEnddate());
                    }
                    jarr.put(tmpObj);
                }
            }else{
                ite = list.iterator();
                while(ite.hasNext()){
                    Object[] row = (Object[]) ite.next();
                    Appraisalmanagement log = (Appraisalmanagement) row[0];
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getEmployee().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    if(log.getReviewsalaryincrement() != 0)
                        prevSalary = (Double.parseDouble(ua.getSalary())*100)/(log.getReviewsalaryincrement()+100);
                    else
                        prevSalary = Double.parseDouble(ua.getSalary());
                    tmpObj.put("eid", log.getEmployee().getUserID());
                    tmpObj.put("ename", log.getEmployee().getFirstName() + " " + log.getEmployee().getLastName());
                    tmpObj.put("salary", ua.getSalary());
                    tmpObj.put("prevsalary", prevSalary.toString());
                    tmpObj.put("man", log.getManager().getFirstName() + " " + log.getManager().getLastName());
                    tmpObj.put("desigid", (log.getOriginaldesignation() == null ? "" : log.getOriginaldesignation().getId()));
                    tmpObj.put("desig", (log.getOriginaldesignation() == null ? "" : log.getOriginaldesignation().getValue()));
                    tmpObj.put("date", log.getManagersubmitdate()!=null?AuthHandler.getDateFormatter(request).format(log.getManagersubmitdate()):"");
                    tmpObj.put("cscore", row[1]);
                    tmpObj.put("gscore", row[2]);
                    tmpObj.put("tscore", row[3]);
                    tmpObj.put("gapscore", row[4]);
                    tmpObj.put("fid", log.getAppraisalid());
                    tmpObj.put("empcom", log.getEmployeecomment());
                    tmpObj.put("mancom", log.getManagercomment());
                    //tmpObj.put("performance", log.getPerformance());
                    if (log.getAppcycle() != null) {
                        tmpObj.put("apptype", log.getAppcycle().getCyclename());
                    } else{
                        tmpObj.put("apptype", "");
                    }
                    tmpObj.put("status", log.getAppraisalstatus());
                    tmpObj.put("reviewstatus", log.getReviewstatus());
                    tmpObj.put("reviewcomment", log.getReviewercomment());
                    tmpObj.put("salaryrecommend", log.getSalaryrecommend());
                    if (log.getReviewdesignation() != null) {
                        tmpObj.put("newdesignation", log.getReviewdesignation().getId());
                        tmpObj.put("newdesignationname", log.getReviewdesignation().getValue());
                    } else {
                        tmpObj.put("newdesignation", "");
                        tmpObj.put("newdesignationname", "");
                    }
                    if (log.getReviewdepartment() != null) {
                        tmpObj.put("newdepartment", log.getReviewdepartment().getId());
                        tmpObj.put("newdepartmentname", log.getReviewdepartment().getValue());
                    } else {
                        tmpObj.put("newdepartment", "");
                        tmpObj.put("newdepartmentname", "");
                    }
                    tmpObj.put("salaryincrement", log.getReviewsalaryincrement());
                    if(log.getAppcycle()!=null){
                        tmpObj.put("appcycleid", log.getAppcycle().getId());
                        tmpObj.put("appcyclename", log.getAppcycle().getCyclename());
                        tmpObj.put("appcyclesdate", log.getAppcycle().getStartdate());
                        tmpObj.put("appcycleedate", log.getAppcycle().getEnddate());
                    }
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getfinalReportFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getfinalReportFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getfinalReportFunction");
        } catch (ParseException e) {
            throw ServiceException.FAILURE("hrmsHandler.getAllReportTemplate", e);
        } finally {
        }
        return result;
    }

    public static String makeRandom(String src){
        String target = "";
        StringTokenizer st = new StringTokenizer(src,"#@#");
        while(st.hasMoreTokens()){
           String str = st.nextToken();
           if(Math.random()<.5)
               target +=str+", ";
           else
               target = str+", "+target;
        }
        return target;
    }

    public static String getfinalReportWithColumns(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, ParseException {
        String result = "";
        String[] compID = null,compStr = null,commentStr = null;
        double[] avg;
        String reviewStat = "";
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        double competencyAvg = 0;
        int i=0;
        String mcomment = "", cscore = "", performance= "", rcomment = "", gscore = "", tscore = "", compavg = "";
        JSONArray array = new JSONArray();
        JSONObject jobj = new JSONObject();
        JSONObject temp = new JSONObject();
        JSONObject temp2 = new JSONObject();
        JSONObject tempObj = new JSONObject();
            if (hrmsManager.checkModule("anonymous", session, request)) {
                JSONObject object = new JSONObject(getfinalReportNonAnonymous(session, request, start, limit));
                JSONArray jarr = object.getJSONArray("coldata");
                if((hrmsManager.checkModule("appraisal", session, request) && jarr.length()<=1) || (!hrmsManager.checkModule("appraisal", session, request) && jarr.length()<=0))
                    object.put("coldata", jarr);
                else{
                    if(hrmsManager.checkModule("appraisal", session, request)){
                        tempObj = jarr.getJSONObject(1);
                        i=1;
                    }else
                        tempObj = jarr.getJSONObject(0);
                    compID = tempObj.getString("records").split(",");
                    compStr = new String[compID.length];
                    commentStr = new String[compID.length];
                    avg = new double[compID.length];
                    for(int j = 0; j<compID.length;j++){
                        commentStr[j] = "";
                        compStr[j] = "";
                        avg[j] = 0;
                    }
                    for(; i<jarr.length(); i++){
                        tempObj = jarr.getJSONObject(i);
                        if(tempObj.has("mcomment") && !StringUtil.isNullOrEmpty(tempObj.getString("mcomment")))
                            mcomment += tempObj.getString("mcomment")+"#@#";
                        if(hrmsManager.checkModule("competency", session, request)){
                            for(int j = 0; j<compID.length;j++){
                                if(tempObj.has(compID[j]) && !StringUtil.isNullOrEmpty(tempObj.getString(compID[j]))){
                                    compStr[j] += tempObj.getInt(compID[j])+"#@#";
                                    avg[j] += tempObj.getInt(compID[j]);
                                }
                                if(tempObj.has(compID[j]+"comment") && !StringUtil.isNullOrEmpty(tempObj.getString(compID[j]+"comment")))
                                    commentStr[j] += tempObj.getString(compID[j]+"comment")+"#@#";
                            }
                        }
                        if(tempObj.has("cscore") && !StringUtil.isNullOrEmpty(tempObj.getString("cscore"))){
                            cscore += tempObj.getDouble("cscore")+"#@#";
                            competencyAvg += tempObj.getDouble("cscore");
                        }
                        reviewStat = tempObj.getString("reviewStat");
                        if(tempObj.has("performance") && !StringUtil.isNullOrEmpty(tempObj.getString("performance")))
                            performance += tempObj.getString("performance")+", ";
                        if(tempObj.has("rcomment") && !StringUtil.isNullOrEmpty(tempObj.getString("rcomment")))
                            rcomment += tempObj.getString("rcomment")+"#@#";
                        if(tempObj.has("compavg") && !StringUtil.isNullOrEmpty(tempObj.getString("compavg")))
                            compavg += tempObj.getString("compavg")+", ";
                        if(hrmsManager.checkModule("goal", session, request))
                            if(tempObj.has("goalscore") && !StringUtil.isNullOrEmpty(tempObj.getString("goalscore")))
                                gscore += tempObj.getString("goalscore")+", ";
                        if(tempObj.has("tscore") && !StringUtil.isNullOrEmpty(tempObj.getString("tscore")))
                            tscore += tempObj.getString("tscore")+", ";
                    }
                    for(int j = 0;j<compID.length;j++){
                        if(hrmsManager.checkModule("appraisal", session, request))
                            avg[j] = avg[j]/(jarr.length()-1);
                        else
                            avg[j] = avg[j]/jarr.length();
                        temp2.put(compID[j], df.format(avg[j]));
                    }
                    if(hrmsManager.checkModule("appraisal", session, request))
                        competencyAvg = competencyAvg/(jarr.length()-1);
                    else
                        competencyAvg = competencyAvg/jarr.length();
                    temp2.put("cscore", df.format(competencyAvg));
                    mcomment = makeRandom(mcomment.substring(0, Math.max(0, mcomment.length()-1)));
                    temp.put("mcomment", mcomment.substring(0, Math.max(0, mcomment.length()-2)));
                    jobj.put("appraiser", "Appraiser(s) Ratings");
                    jobj.put("reviewStat", reviewStat);
                    temp.put("appraiser", "Appraiser(s) Comments");
                    temp2.put("appraiser", "<b>Appraiser(s) Average Score</b>");
                    for(i = 0; i<compID.length;i++){
                        compStr[i] = makeRandom(compStr[i].substring(0, Math.max(0, compStr[i].length()-1)));
                        jobj.put(compID[i], compStr[i].substring(0, Math.max(0, compStr[i].length()-2)));
                        commentStr[i] = makeRandom(commentStr[i].substring(0, Math.max(0, commentStr[i].length()-1)));
                        temp.put(compID[i]+"comment", commentStr[i].substring(0, Math.max(0, commentStr[i].length()-2)));
                    }
                    cscore = makeRandom(cscore.substring(0, Math.max(0, cscore.length()-1)));
                    jobj.put("cscore", cscore.substring(0, Math.max(0, cscore.length()-2)));
                    jobj.put("performance", performance.substring(0, Math.max(0, performance.length()-2)));
                    rcomment = makeRandom(rcomment.substring(0, Math.max(0, rcomment.length()-1)));
                    temp.put("rcomment", rcomment.substring(0, Math.max(0, rcomment.length()-2)));
                    jobj.put("goalscore", gscore.substring(0, Math.max(0, gscore.length()-2)));
                    jobj.put("tscore", tscore.substring(0, Math.max(0, tscore.length()-2)));
                    JSONArray tempArr = new JSONArray();
                    if(hrmsManager.checkModule("appraisal", session, request))
                        tempArr.put(jarr.get(0));
                    tempArr.put(jobj);
                    tempArr.put(temp);
                    tempArr.put(temp2);
                    object.put("coldata", tempArr);
                }
                result = object.toString();

            }else{
               result = getfinalReportNonAnonymous(session, request, start, limit);
            }
       return result;
    }

    public static String getfinalReportNonAnonymous(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, ParseException {
        JSONObject jobjTemp = new JSONObject();
        JSONObject jobjData;
        JSONObject empData;
        boolean flag = true;
        String records = "";
        JSONArray jarr = new JSONArray();
        JSONArray jarrColumns = new JSONArray();
        List list;
        int totalAppDone=0;
        int totalManagers=0;
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta =new JSONObject();
        JSONObject commData = new JSONObject();
        String hql = "";
        Iterator iter = null;
        String str;
        try {
            jobjTemp.put("header", "Appraiser");
            jobjTemp.put("tip", "Appraiser");
            jobjTemp.put("dataIndex", "appraiser");
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appraiser");
            jarrRecords.put(jobjTemp);
            String userID = request.getParameter("userid");
            /*
             * If user is viewing his own report then no userid is sent as the user combo is not present.
             * Only appraisal cycle combo is there and so only appriasal cycle id is sent in the request.
             */
            if(StringUtil.isNullOrEmpty(userID)){
                userID = AuthHandler.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
            User user =(User) session.load(User.class, userID);
            Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
            boolean compFlag = false;
            if (hrmsManager.checkModule("competency", session, request)) { //Check for competency permission
                hql = "from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2";
                list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID,userID});
                iter = list.iterator();
                if(iter.hasNext()){
                    Appraisalmanagement am = (Appraisalmanagement) iter.next();
                    try {
                        str = am.getOriginaldesignation().getId();
                    } catch (NullPointerException e) {
                        str = ua.getDesignationid().getId();
                    }
                }else{
                    str = ua.getDesignationid().getId();
                }
                hql = "from Managecmpt where desig.id=? and delflag=0";
                list = HibernateUtil.executeQuery(session, hql, new Object[]{str});
                iter = list.iterator();
                while(iter.hasNext()){
                    compFlag = true;
                    Managecmpt managecmpt = (Managecmpt) iter.next();
                    jobjTemp = new JSONObject();
                    String tStr = managecmpt.getMastercmpt().getCmptname();
                    String compID = managecmpt.getMastercmpt().getCmptid();
                    jobjTemp.put("header", tStr);
                    jobjTemp.put("tip", tStr);
                    jobjTemp.put("dataIndex", compID);
                    jobjTemp.put("summaryType", "average");
                    jobjTemp.put("renderer", "function(val,meta,record){ return \" <div style='white-space:pre-wrap;' > <span class='valueSpan'><b>score: \" +  val + \"</b></span><br><br/>" +
                        "<span class='commentSpan' wtf:qtip='\" + record.data['"+compID+"comment'] + \"'>comment: \" + Wtf.util.Format.ellipsis(record.data['"+compID+"comment'], 15) + \"</span></div>\";}");
                    jobjTemp.put("summaryRenderer", "function(val,meta,record){ return \"<span style='text-align: right;border:1px solid #eeeeee; float: left'><b>\" + val + \"</b></span>\";}");
                    jarrColumns.put(jobjTemp);
                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", compID);
                    jarrRecords.put(jobjTemp);
                    jobjTemp = new JSONObject();
                    jobjTemp.put("header", tStr+" Comment");
                    jobjTemp.put("dataIndex", compID+"comment");
                    jobjTemp.put("hidden", true);
                    jarrColumns.put(jobjTemp);
                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", compID+"comment");
                    jarrRecords.put(jobjTemp);
                }
            }
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ? and reviewstatus=0";
            } else{
                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ?";
            }
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID,userID});
            iter = list.iterator();
            while(iter.hasNext()){
                totalManagers+=1;
                Appraisalmanagement appmgnt = (Appraisalmanagement) iter.next();
                ua = (Useraccount) session.get(Useraccount.class, appmgnt.getManager().getUserID());
                if(appmgnt.getManagerstatus()==1){
                    totalAppDone+=1;
                    jobjData = new JSONObject();
                    empData = new JSONObject();
                    empData.put("appraiser", "Self Appraisal");
                    jobjData.put("appraiser", appmgnt.getManager().getFirstName()+" "+appmgnt.getManager().getLastName());
                    if(appmgnt.getManagercomment() != null)
                        jobjData.put("mcomment", appmgnt.getManagercomment());
                    else
                        jobjData.put("mcomment", "");
                    empData.put("mcomment", appmgnt.getEmployeecomment());
                    if(appmgnt.getReviewercomment() != null)
                        jobjData.put("rcomment",appmgnt.getReviewercomment());
                    else
                        jobjData.put("rcomment","");
                    jobjData.put("reviewStat", appmgnt.getReviewstatus());
                    if (hrmsManager.checkModule("goal", session, request)) { //Check for goal permission
                        jobjData.put("goalscore", appmgnt.getManagergoalscore());
                        empData.put("goalscore", appmgnt.getEmployeegoalscore());
                    }
                    jobjData.put("salaryinc", appmgnt.getSalaryincrement());
                    jobjData.put("eid", appmgnt.getEmployee().getUserID());
                    jobjData.put("ename", appmgnt.getEmployee().getFirstName() + " " + appmgnt.getEmployee().getLastName());
                    jobjData.put("desigid", ua.getDesignationid().getId());
                    jobjData.put("desig", ua.getDesignationid().getValue());
					jobjData.put("department", ua.getDepartment().getValue());
                    jobjData.put("date", appmgnt.getManagersubmitdate()!=null?AuthHandler.getDateFormatter(request).format(appmgnt.getManagersubmitdate()):"");
                    jobjData.put("cscore", appmgnt.getManagercompscore());
                    jobjData.put("tscore", appmgnt.getTotalscore());
                    jobjData.put("gapscore", appmgnt.getManagergapscore());
                    empData.put("cscore", appmgnt.getEmployeecompscore());
                    empData.put("gapscore", appmgnt.getEmployeegapscore());
                    jobjData.put("fid", appmgnt.getAppraisalid());
                    jobjData.put("empcom", appmgnt.getEmployeecomment());
                    empData.put("performance", "<center>N/A</center>");
                    if (appmgnt.getPerformance() != null) {
                        jobjData.put("prate", appmgnt.getPerformance().getId());
                        jobjData.put("performance", appmgnt.getPerformance().getValue());
                    } else {
                        jobjData.put("prate", "None");
                        jobjData.put("performance", "");
                    }
                    if (appmgnt.getAppcycle() != null) {
                        jobjData.put("apptype", appmgnt.getAppcycle().getCyclename());
                    } else{
                        jobjData.put("apptype", "");
                    }
                    jobjData.put("status", appmgnt.getAppraisalstatus());
                    empData.put("reviewStat", appmgnt.getReviewstatus());
                    jobjData.put("salaryrecommend", appmgnt.getSalaryrecommend());
                    if (appmgnt.getReviewdesignation() != null) {
                        jobjData.put("newdesignation", appmgnt.getReviewdesignation().getId());
                        jobjData.put("newdesignationname", appmgnt.getReviewdesignation().getValue());
                    } else {
                        jobjData.put("newdesignation", "");
                        jobjData.put("newdesignationname", "");
                    }
                    if (appmgnt.getReviewdepartment() != null) {
                        jobjData.put("newdepartment", appmgnt.getReviewdepartment().getId());
                        jobjData.put("newdepartmentname", appmgnt.getReviewdepartment().getValue());
                    } else {
                        jobjData.put("newdepartment", "");
                        jobjData.put("newdepartmentname", "");
                    }
                    if(appmgnt.getAppcycle()!=null){
                        jobjData.put("appcycleid", appmgnt.getAppcycle().getId());
                        jobjData.put("appcyclename", appmgnt.getAppcycle().getCyclename());
                        jobjData.put("appcyclesdate", appmgnt.getAppcycle().getStartdate());
                        jobjData.put("appcycleedate", appmgnt.getAppcycle().getEnddate());
                    }
                    if(compFlag) {//Check if competency
                        Appraisalmanagement apprmgt = (Appraisalmanagement) session.load(Appraisalmanagement.class, appmgnt.getAppraisalid());
                        hql = "from Appraisal where appraisal.appraisalid = ?";
                        List lst = HibernateUtil.executeQuery(session, hql, new Object[]{appmgnt.getAppraisalid()});
                        Iterator ite = lst.iterator();
                        int totComp = 0;
                        while(ite.hasNext()){
                            totComp++;
                            Appraisal app = (Appraisal) ite.next();
                            if(app.getCompetency()!=null){
                                if(flag)
                                    records += app.getCompetency().getMastercmpt().getCmptid()+",";
                                jobjData.put(app.getCompetency().getMastercmpt().getCmptid(), app.getCompmanrating());
                                jobjData.put(app.getCompetency().getMastercmpt().getCmptid()+"comment", app.getCompmancomment());
                                empData.put(app.getCompetency().getMastercmpt().getCmptid(), app.getCompemprating());
                                empData.put(app.getCompetency().getMastercmpt().getCmptid()+"comment", app.getCompempcomment());
                            }
                        }
                        flag = false;
                        jobjData.put("compavg", df.format(apprmgt.getManagercompscore()));
                        empData.put("compavg", df.format(apprmgt.getEmployeecompscore()));
                    }
                    jobjData.put("totalAppDone", totalAppDone);
                    jobjData.put("totalManagers", totalManagers);
                    jobjData.put("records", records.substring(0, Math.max(0, records.length()-1)));
                    if(jarr.length()<=0 && hrmsManager.checkModule("appraisal", session, request)){
                        jarr.put(empData);
                    }
                    jarr.put(jobjData);
                }
            }

            if(compFlag) {//Check if competency
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Competency Score");
                jobjTemp.put("tip", "Competency Score");
                jobjTemp.put("dataIndex", "cscore");
                jobjTemp.put("summaryType", "average");
                jobjTemp.put("align", "right");
                jobjTemp.put("renderer", "function(val,meta,record){ return \"<b>\" + val + \"</b>\";}");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "cscore");
                jarrRecords.put(jobjTemp);
            }
            if (hrmsManager.checkModule("goal", session, request)) { //Check for goal permission
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Goal Score");
                jobjTemp.put("tip", "Goal Score");
                jobjTemp.put("dataIndex", "goalscore");
                jobjTemp.put("summaryType", "average");
                jobjTemp.put("align", "right");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "goalscore");
                jarrRecords.put(jobjTemp);
            }
            if(PermissionHandler.isSubscribed(hrms_Modules.payroll, AuthHandler.getCmpSubscription(request))){
                jobjData = new JSONObject();
                jobjTemp.put("header", "Salary Increment (%)");
                jobjTemp.put("tip", "Salary Increment (%)");
                jobjTemp.put("dataIndex", "salaryinc");
                jobjTemp.put("align", "right");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salaryinc");
                jarrRecords.put(jobjTemp);
            }
            
            if(hrmsManager.checkModule("promotion", session, request)){
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Performance Rating");
                jobjTemp.put("tip", "Performance Rating");
                jobjTemp.put("dataIndex", "performance");
                jobjTemp.put("renderer", "function(val){ return \"<div wtf:qtip='\"+val+\"'>\"+val+\"</div>\"}");
                jarrColumns.put(jobjTemp);
            }
            
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "performance");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("header", "Comment");
            jobjTemp.put("tip", "Appraiser Comment");
            jobjTemp.put("dataIndex", "mcomment");
            jobjTemp.put("renderer", "function(val){ return \"<div style='white-space:pre-wrap;' wtf:qtip='\"+val+\"'>\"+val+\"</div>\"}");
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "mcomment");
            jarrRecords.put(jobjTemp);
            if(hrmsManager.checkModule("approveAppWin", session, request)){
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Reviewer Comment");
                jobjTemp.put("tip", "Reviewer Comment");
                jobjTemp.put("dataIndex", "rcomment");
                jobjTemp.put("renderer", "function(val){ return \"<div wtf:qtip='\"+val+\"'>\"+val+\"</div>\"}");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "rcomment");
                jarrRecords.put(jobjTemp);
            }

            jobjTemp = new JSONObject();
            jobjTemp.put("header", "Review Status");
            jobjTemp.put("tip", "Review Status");
            jobjTemp.put("dataIndex", "reviewStat");
            jobjTemp.put("renderer", "function(val){"+
                                                    "if(val=='0')"+
                                                        "return '<FONT COLOR=\"blue\">Pending</FONT>';"+
                                                    "else if(val=='1')"+
                                                        "return '<FONT COLOR=\"red\">Unapproved</FONT>';"+
                                                    "else if(val=='2')"+
                                                        "return '<FONT COLOR=\"green\">Completed</FONT>';"+
                                                    "}"
                        );
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "reviewStat");
            jarrRecords.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("header", "totalManagers");
            jobjTemp.put("dataIndex", "totalManagers");
            jobjTemp.put("hidden", true);
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "totalManagers");
            jobjTemp.put("type", "int");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("header", "totalAppDone");
            jobjTemp.put("dataIndex", "totalAppDone");
            jobjTemp.put("hidden", true);
            jarrColumns.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "totalAppDone");
            jobjTemp.put("type", "int");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "desigid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "eid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "fid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "ename");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "desig");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "department");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "date");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "empcom");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "compavg");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "gapscore");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "salaryrecommend");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "newdesignation");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "newdepartment");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "prate");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "apptype");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appcyclesdate");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appcycleedate");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "appcycleid");
            jarrRecords.put(jobjTemp);

            commData.put("coldata", jarr);
            commData.put("columns", jarrColumns);
            jMeta.put("totalProperty", "totalCount");
            jMeta.put("root", "coldata");
            jMeta.put("fields", jarrRecords);
            commData.put("metaData", jMeta);
            commData=getAppraisalManagers(session,userID,appCycleID,commData);
            commData.put("success", true);

        } catch (JSONException ex) {
            throw new JSONException("getfinalReportFunction");
        } finally {
        }
            return commData.toString();
        }

    public static String assignManagerFunction(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        String result = "";
        List tabledata = null;
        String[] userids = request.getParameterValues("userid");
        String[] reviewerids = request.getParameterValues("reviewerid");
        String[] managerids = request.getParameterValues("managerid");
        String hql;
        Assignmanager assign;
        Assignreviewer reviewer;
        try {
            if (Boolean.parseBoolean(request.getParameter("isManager"))) {
                for (int i = 0; i < userids.length; i++) {
                    hql = "from Assignmanager where assignemp.userID=? and managerstatus=1";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{userids[i]});
                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Assignmanager log = (Assignmanager) ite.next();
                        log.setManagerstatus(0);
                        session.update(log);
                    }
                }
                for (int i = 0; i < userids.length; i++) {
                    for (int j = 0; j < managerids.length; j++) {
                        assign = new Assignmanager();
                        assign.setAssignemp((User) session.load(User.class, userids[i]));
                        assign.setAssignman((User) session.load(User.class, managerids[j]));
                        assign.setManagerstatus(1);
                        session.saveOrUpdate(assign);
                    }
                }
            }
            else {
                for (int i = 0; i < userids.length; i++) {
                    hql = "from Assignreviewer where employee.userID=? and reviewerstatus=1";
                    tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{userids[i]});
                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        Assignreviewer log = (Assignreviewer) ite.next();
                        log.setReviewerstatus(0);
                        session.update(log);
                    }
                }
                for (int i = 0; i < userids.length; i++) {
                    for (int j = 0; j < managerids.length; j++) {
                            reviewer = new Assignreviewer();
                            reviewer.setEmployee((User) session.load(User.class, userids[i]));
                            reviewer.setReviewer((User) session.load(User.class, managerids[j]));
                            reviewer.setReviewerstatus(1);
                            session.saveOrUpdate(reviewer);
                    }
                }
            }
        }catch (ServiceException ex) {
            throw ServiceException.FAILURE("assignManagerFunction", ex);
        }
        return result;
    }

    public static String getassignManagerFunction(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            Query qry = session.createQuery("from Assignmanager where empid=? and managerstatus=1");
            qry.setParameter(0, request.getParameter("userid"));
            tabledata = (List) qry.list();
            count = tabledata.size();
            for (int i = 0; i < count; i++) {
                Assignmanager log = (Assignmanager) tabledata.get(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("userid", log.getAssignman().getUserID());
                tmpObj.put("username", log.getAssignman().getFirstName());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
        }

        //Select Queries
        return result;
    }

    public static String updateinernalJobsFunction(Session session, HttpServletRequest request) throws ServiceException {
        Date date;
        Date date1;
        String result = "";
        //DateFormat formatter;
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");

        try {
            date = (Date) fmt.parse(request.getParameter("startdate"));
            date1 = (Date) fmt.parse(request.getParameter("enddate"));
            MasterData md = (MasterData) session.load(MasterData.class, request.getParameter("position"));
            String posid = request.getParameter("posid");
            Positionmain posmain = (Positionmain) session.load(Positionmain.class, posid);
            posmain.setPosition(md);
            posmain.setDetails(request.getParameter("details"));
            posmain.setDepartment(request.getParameter("department"));
            posmain.setApprvmangr(request.getParameter("manager"));
            posmain.setStartdate(date);
            posmain.setEnddate(date1);
            posmain.setJobtype(request.getParameter("jobtype"));


            //posmain.setPositionid(posid);
            session.update(posmain);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.InsertKeypositionfunction", ex);
        }

        return result;
    }

    public static String colorText(String value, String color) {
        value = "<span style='color:" + color + " !important;'>" + value + "</span>";
        return value;
    }

    public static String getAlertsFunction(Session session, HttpServletRequest request) throws ServiceException {
        String uid = "";
        String result = "";
        StringBuilder finalString = new StringBuilder();
        int goallimit = 5;
        List tabledata;
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date dbDate = null;
        int days;
        int diff;
        JSONObject tmpObj = new JSONObject();
        try {
            //dbDate = df.parse(results.toString());
            String empid = AuthHandler.getUserid(request);
            String companyid = AuthHandler.getCompanyid(request);


            String uperms = "from UserPermission where userLogin.userID=? ";
            List perms = HibernateUtil.executeQuery(session, uperms, empid);
            UserPermission userperms = (UserPermission) perms.get(0);
            if (userperms.getFeature().getDisplayFeatureName().equals("Employee")) {
                //Query qry = session.createQuery("from com.krawler.hrms.performance.Appraisalmanagement as a where a.com.krawler.common.admin.User.empid=?");
                Query qry = session.createQuery("from Appraisalmanagement  where empid.userID=?");
                qry.setParameter(0, empid);
                tabledata = (List) qry.list();
                count = tabledata.size();
                if (count > 0) {
                    com.krawler.hrms.performance.Appraisalmanagement log = (com.krawler.hrms.performance.Appraisalmanagement) tabledata.get(0);
                    dbDate = log.getEnddate();
                    diff = (int) ((dbDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                    result += "<li><div class='statusTextWrap tmItemsWrap'>Fill the appraisal form in  " + diff + " days </div></li>";
                }
                DateFormat formatter = new SimpleDateFormat(AuthHandler.getDateFormat(request));
                Calendar cal = Calendar.getInstance();
                int weekday = cal.get(Calendar.DAY_OF_WEEK);
                cal.add(Calendar.DATE, -weekday + 1);
                Date sdate = cal.getTime();
                cal.add(Calendar.DATE, 6);
                Date edate = cal.getTime();


//            int startadjust = day - Calendar.SUNDAY;
//            // c.add(Calendar.DAY,0-startadjust);


                String timesheet1 = "from Timesheet where datevalue between ? and ? and  userID.company.companyID=? and userID.userID=? group by approved";
                List timesheet = HibernateUtil.executeQuery(session, timesheet1, new Object[]{sdate, edate, companyid, empid});
                Timesheet tmst = null;
                if (!timesheet.isEmpty()) {
                    tmst = (Timesheet) timesheet.get(0);

                    if (tmst.getApproved() == 0) {
                        result += "<li><div class='statusTextWrap tmItemsWrap '>Your<a href='#' onclick='timesheet()'> Timesheet</a> from <font color='green'>" + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> is <font color='green'>Pending</font> </div></li>";
                    } else {
                        result += "<li><div class='statusTextWrap tmItemsWrap '>Your Timesheet from <font color='green'>" + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> has been <font color='green'>Approved</font> </div></li>";
                    }
                } else {
                    result += "<li><div class='statusTextWrap tmItemsWrap '>Please Fill<a href='#' onclick='timesheet()'> Timesheet  </a> from<font color='green'> " + formatter.format(sdate) + "</font> to <font color='green'>" + formatter.format(edate) + "</font> </div></li>";
                }

                String hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? order by createdon desc";
                List goallst = HibernateUtil.executeQuery(session, hql, new Object[]{empid, companyid});
                int goalcnt = goallst.size();
                Finalgoalmanagement fgmt = null;
                if (!goallst.isEmpty()) {
                    if (goalcnt < goallimit) {
                        for (int x = 0; x < goalcnt; x++) {
                            fgmt = (Finalgoalmanagement) goallst.get(x);
                            result += "<li><div class='statusTextWrap tmItemsWrap '><a href='#' onclick='myGoals()'><font color='green'>" + fgmt.getGoalname() + "</font></a> goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + fgmt.getCreatedon() + "</font></div></li>";

                        }
                    } else {
                        for (int x = 0; x < goallimit; x++) {
                            fgmt = (Finalgoalmanagement) goallst.get(x);
                            result += "<li><div class='statusTextWrap tmItemsWrap'><a href='#' onclick='myGoals()'><font color='green'>" + fgmt.getGoalname() + "</font></a> goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + fgmt.getCreatedon() + "</font></div></li>";

                        }
                    }

                }
            } else {
                Calendar cal = Calendar.getInstance();
                days = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DATE);

                result += "<li><div class='statusTextWrap tmItemsWrap'>Salary generation is " + days + " days due</div></li>";
            }
            tmpObj.put("message", result);
            jArr.put(tmpObj);
            jobj.put("data", jArr);
            jobj.put("count", 1);
            result = jobj.toString();


        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.getAlertsFunction", ex);
        } finally {
            //  finalString=result;
        }
        return result;

    }

    public static String getEmpForManagerFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, SQLException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        String hql;
        Iterator ite;
        String ss = request.getParameter("ss");
        ArrayList params = new ArrayList();
        try {
            if (!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request))) {
                hql = "from User where deleted=? and company.companyID=?";
                params.add(false);
                params.add(AuthHandler.getCompanyid(request));
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"firstName","lastName"});
                    hql +=searchQuery;
                }
                tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
                count = tabledata.size();
                if (!StringUtil.isNullOrEmpty(request.getParameter("paging"))) {
                    tabledata = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                }
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    User log = (User) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getUserID());
                    tmpObj.put("username", log.getFirstName());
                    tmpObj.put("designationid", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getId()));
                    tmpObj.put("designation", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue()));
                    tmpObj.put("emailid", log.getEmailID());
                    tmpObj.put("contactno", log.getContactNumber());
                    tmpObj.put("fullname", log.getFirstName() + " " + (log.getLastName() == null ? "" : log.getLastName()));
                    jArr.put(tmpObj);
                }
            } else {
                hql = "from Assignmanager where assignman.userID=? and assignemp.deleted=? and assignemp.company.companyID=? and managerstatus=1";
                params.add(AuthHandler.getUserid(request));
                params.add(false);
                params.add(AuthHandler.getCompanyid(request));
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"assignemp.firstName","assignemp.lastName"});
                    hql +=searchQuery;
                }
                tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
                count = tabledata.size();
                if (!StringUtil.isNullOrEmpty(request.getParameter("paging"))) {
                    tabledata = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                }
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Assignmanager log = (Assignmanager) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getAssignemp().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getAssignemp().getUserID());
                    tmpObj.put("username", log.getAssignemp().getFirstName());
                    tmpObj.put("designationid", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getId()));
                    tmpObj.put("designation", (ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue()));
                    tmpObj.put("emailid", log.getAssignemp().getEmailID());
                    tmpObj.put("contactno", log.getAssignemp().getContactNumber());
                    tmpObj.put("fullname", log.getAssignemp().getFirstName() + " " + (log.getAssignemp().getLastName() == null ? "" : log.getAssignemp().getLastName()));
                    jArr.put(tmpObj);
                }
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getEmpForManagerFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getEmpForManagerFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getEmpForManagerFunction");
        } finally {
        }
        return result;
    }

    public static String getappraisaltypeFunction(Session session, HttpServletRequest request) throws ServiceException {
        String uid = "";
        String result = "";
        List tabledata;
        JSONObject obj = new JSONObject();
        JSONArray jArr = new JSONArray();
        Appraisalmanagement appm;
        try {
            String hql = "from Appraisalmanagement where empid.userID=?";
            tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("userid"));
            JSONObject tmpObj = new JSONObject();
            if (!tabledata.isEmpty()) {
                appm = (Appraisalmanagement) tabledata.get(0);
                tmpObj.put("apptype", appm.getAppraisaltype());
                jArr.put(tmpObj);
            } else {
                tmpObj.put("apptype", "none");
                jArr.put(tmpObj);
            }
            obj.put("data", jArr);
            obj.put("count", 1);
            result = obj.toString();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsManager.getStatus", ex);
        } finally {
        }
        return result;
    }

  public static String viewagencyJobsFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        String result = "{'count':0,'data':[]}";
        List tabledata = null;
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            ArrayList params = new ArrayList();
            String ss = request.getParameter("ss");
            String agencyid = request.getParameter("agencyid");
            params.add(agencyid);
            String searchString = "";
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                searchString = StringUtil.getSearchString(ss, "and", new String[]{"applypos.jobid","applypos.details"});
            }
            String hql = "from Applyagency where applyagency.agencyid=? "+ searchString;
            tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            count = tabledata.size();
            list = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                Applyagency log = (Applyagency) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("jobid", log.getApplypos().getJobid());
                tmpObj.put("posname", log.getApplypos().getPosition().getValue());
                tmpObj.put("department", log.getApplypos().getDepartment());
                tmpObj.put("details", log.getApplypos().getDetails());
                tmpObj.put("manager", log.getApplypos().getApprvmangr());
                tmpObj.put("startdate", AuthHandler.getDateFormatter(request).format(log.getApplypos().getStartdate()));
                tmpObj.put("enddate", AuthHandler.getDateFormatter(request).format(log.getApplypos().getEnddate()));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsManager.viewagencyJobsFunction", e);
        } finally {
           return result;
        }
    }

    public static String getChart(String name, Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String uid = AuthHandler.getUserid(request);
            String qry = "from Appraisalmanagement where manager.userID=? and performance.value=? and employee.deleted=? and reviewstatus=2";
            Object[] obj = {uid, name, false};
            tabledata = HibernateUtil.executeQuery(session, qry, obj);
            count = tabledata.size();
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("Count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisalGoalsFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalGoalsFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisalGoalsFunction");
        } finally {
        }
        return result;
    }

    public static String getEmpSchedule(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            String SELECT_USER_INFO = "select aman.assignemp from Assignmanager aman where aman.assignman.userID=? and managerstatus=1";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, AuthHandler.getUserid(request));
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            int count = list.size();
            if (!(StringUtil.isNullOrEmpty(start) || StringUtil.isNullOrEmpty(limit))) {
                list = HibernateUtil.executeQueryPaging(session, SELECT_USER_INFO, new Object[]{AuthHandler.getUserid(request)}, new Integer[]{Integer.parseInt(start), Integer.parseInt(limit)});
            }
            Iterator itr = list.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                JSONObject obj = new JSONObject();
                User user = (User) itr.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, user.getUserID());
                obj.put("id", user.getUserID());
                obj.put("name", AuthHandler.getFullName(user));
                obj.put("designation",  ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("email", user.getEmailID());
                obj.put("company", user.getCompany().getCompanyName());
                obj.put("phone", user.getContactNumber());
                obj.put("storename", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                obj.put("type", "Contract");
                obj.put("statusid", 1);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("TotalCount", count);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.getEmpSchedule", e);
        }

        return jobj.toString();
    }

    public static JSONObject getPerformanceDataFunction(Session session, int id) throws ServiceException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String hql = "from MasterData where masterid.id=? ";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{id});
            count = tabledata.size();
            Iterator ite = tabledata.iterator();
            while (ite.hasNext()) {
                MasterData mst = (MasterData) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", mst.getValue());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", count);
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsManager.getPerformanceDataFunction", e);
        } finally {
        }
        return jobj;
    }

    public static String archivedgoalsfunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        String ss = request.getParameter("ss");
        ArrayList params = new ArrayList();
        try {
            String cmp=AuthHandler.getCompanyid(request);
            String hql = "from Finalgoalmanagement where archivedflag=1 and userID.company.companyID=? and deleted=?";
            params.add(cmp);
            params.add(false);
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"userID.firstName","userID.lastName"});
                hql +=searchQuery;
            }
            List lst = HibernateUtil.executeQuery(session, hql,params.toArray());
            count = lst.size();
            List lst1 = HibernateUtil.executeQueryPaging(session, hql,params.toArray(), new Integer[]{start, limit});
            Iterator ite = lst1.iterator();
            while (ite.hasNext()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                if (fgmt.getUserID().getDeleteflag()==1) {
                    tmpObj.put("empname", fgmt.getUserID().getFirstName() + " " +(fgmt.getUserID().getLastName()==null?"":fgmt.getUserID().getLastName()));
                    tmpObj.put("gid", fgmt.getId());
                    tmpObj.put("gname", fgmt.getGoalname());
                    tmpObj.put("manname", fgmt.getAssignedby());
                    tmpObj.put("gdescription", fgmt.getGoaldesc());
                    tmpObj.put("gwth", fgmt.getGoalwth());
                    tmpObj.put("gcontext", fgmt.getContext());
                    tmpObj.put("gpriority", fgmt.getPriority());
                    tmpObj.put("gstartdate", AuthHandler.getDateFormatter(request).format(fgmt.getStartdate()));
                    tmpObj.put("genddate", AuthHandler.getDateFormatter(request).format(fgmt.getEnddate()));
                    tmpObj.put("gcomment", fgmt.getcomment());
                    tmpObj.put("gassignedby", fgmt.getAssignedby());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String GetEmpExperience(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String userid;
        int count = 0;
        try {

            String hql = "from Empexperience where userid.userID=? and type=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("userid"), request.getParameter("type")});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Empexperience extmt = (Empexperience) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", extmt.getId());
                tmpObj.put("qualification", extmt.getQualification());
                tmpObj.put("qualificationin", extmt.getQaulin());
                tmpObj.put("yeargrdfrm", extmt.getFrmyear());
                tmpObj.put("institution", extmt.getInstitution());
                tmpObj.put("gradyear", extmt.getYearofgrad());
                tmpObj.put("marks", extmt.getMarks());
                tmpObj.put("organisation", extmt.getOrganization());
                tmpObj.put("position", extmt.getPosition());
                tmpObj.put("beginyear", extmt.getBeginyear());
                tmpObj.put("endyear", extmt.getEndyear());
                tmpObj.put("comment", extmt.getComment());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Select Queries
        return result;
    }

    public static String empProfile(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String userid;
        int count = 0;
        try {
            String cmpid = AuthHandler.getCompanyid(request);
            String hql = "from Empprofile where userID=?  ";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("userid")});
            Iterator ite = lst.iterator();
            if (ite.hasNext()) {
                while (ite.hasNext()) {
                    Empprofile extmt = (Empprofile) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, extmt.getUserLogin().getUser().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    String middlename=(extmt.getMiddlename()!=null?extmt.getMiddlename():"");
                    tmpObj.put("lid", extmt.getUserID());
                    tmpObj.put("fname", extmt.getUserLogin().getUser().getFirstName());
                    tmpObj.put("lname", extmt.getUserLogin().getUser().getLastName());
                    tmpObj.put("fullname", extmt.getUserLogin().getUser().getFirstName() + " " + middlename + " " + extmt.getUserLogin().getUser().getLastName());
                    tmpObj.put("mname",middlename );
                    tmpObj.put("DoB", (extmt.getDoB() == null ? "" : extmt.getDoB()));
                    tmpObj.put("gender", extmt.getGender());
                    tmpObj.put("marital", extmt.getMarriage());
                    tmpObj.put("bldgrp", extmt.getBloodgrp());
                    tmpObj.put("bankacc", ua.getAccno());
                    tmpObj.put("bankname", extmt.getBankname());
                    tmpObj.put("bankbranch", extmt.getBankbranch());
                    tmpObj.put("pan", extmt.getPanno());
                    tmpObj.put("pf", extmt.getPfno());
                    tmpObj.put("drivingli", extmt.getDrvlicense());
                    tmpObj.put("passport", extmt.getPassportno());
                    tmpObj.put("exppassport", (extmt.getExppassport() == null ? "" : extmt.getExppassport()));
                    tmpObj.put("fathername", extmt.getFathername());
                    tmpObj.put("fatherDoB", (extmt.getFatherDoB() == null ? "" : extmt.getFatherDoB()));
                    tmpObj.put("mothername", extmt.getMothername());
                    tmpObj.put("motherDoB", (extmt.getMotherDoB() == null ? "" : extmt.getMotherDoB()));
                    tmpObj.put("spousename", extmt.getSpousename());
                    tmpObj.put("spouseDoB", (extmt.getSpouseDoB() == null ? "" : extmt.getSpouseDoB()));
                    tmpObj.put("childname1", extmt.getChild1name());
                    tmpObj.put("childDoB1", (extmt.getChild1DoB() == null ? "" : extmt.getChild1DoB()));
                    tmpObj.put("childname2", extmt.getChild2name());
                    tmpObj.put("childDoB2", (extmt.getChild2DoB() == null ? "" : extmt.getChild2DoB()));
                    tmpObj.put("mobileno", extmt.getMobno());
                    tmpObj.put("worktele", extmt.getWorkno());
                    tmpObj.put("hometele", extmt.getLandno());
                    tmpObj.put("workemail", extmt.getWorkmail());
                    tmpObj.put("otheremail", extmt.getOthermail());
                    tmpObj.put("preaddress", extmt.getPresentaddr());
                    tmpObj.put("city1", extmt.getPresentcity());
                    tmpObj.put("state1", extmt.getPermstate());
                    tmpObj.put("precountry", (extmt.getPresentcountry() == null ? "" : extmt.getPresentcountry().getId()));
                    tmpObj.put("peraddress", extmt.getPermaddr());
                    tmpObj.put("city2", extmt.getPresentcity());
                    tmpObj.put("state2", extmt.getPermstate());
                    tmpObj.put("permcountry", (extmt.getPermcountry() == null ? "" : extmt.getPermcountry().getId()));
                    tmpObj.put("mailaddress", extmt.getMailaddr());
                    tmpObj.put("emercntname", extmt.getEmgname());
                    tmpObj.put("relation", extmt.getEmgreln());
                    tmpObj.put("emrcontact", extmt.getEmghome());
                    tmpObj.put("emrcontact2", extmt.getEmgwork());
                    tmpObj.put("emrcontact3", extmt.getEmgmob());
                    tmpObj.put("emercntaddr", extmt.getEmgaddr());
                    tmpObj.put("empid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
                    tmpObj.put("department", ua.getDepartment()== null ? "" :ua.getDepartment().getId());
                    tmpObj.put("designationid", ua.getDesignationid()==null?"": ua.getDesignationid().getId());
                    tmpObj.put("managername", (extmt.getReportto() == null ? "" : extmt.getReportto().getUserID()));
                    tmpObj.put("tyofemp", extmt.getEmptype());
                    tmpObj.put("Dtojoin", (extmt.getJoindate() == null ? "" : extmt.getJoindate()));
                    tmpObj.put("Dtoconfirm", (extmt.getConfirmdate() == null ? "" : extmt.getConfirmdate()));
                    tmpObj.put("Dtofrelieve", (extmt.getRelievedate() == null ? "" : extmt.getRelievedate()));
                    tmpObj.put("trainingmon", (extmt.getTrainperiod() == null ? "" : extmt.getTrainperiod().substring(0, Math.max(0, extmt.getTrainperiod().indexOf(",")))));
                    tmpObj.put("trainingyr", (extmt.getTrainperiod() == null ? "" : extmt.getTrainperiod().substring(Math.max(0, extmt.getTrainperiod().indexOf(",") + 1))));
                    tmpObj.put("probationmon", (extmt.getProbperiod() == null ? "" : extmt.getProbperiod().substring(0, Math.max(0, extmt.getProbperiod().indexOf(",")))));
                    tmpObj.put("probationyr", (extmt.getProbperiod() == null ? "" : extmt.getProbperiod().substring(Math.max(0, extmt.getProbperiod().indexOf(",") + 1))));
                    tmpObj.put("noticemon", (extmt.getNoticeperiod() == null ? "" : extmt.getNoticeperiod().substring(0, Math.max(0, extmt.getNoticeperiod().indexOf(",")))));
                    tmpObj.put("noticeyr", (extmt.getNoticeperiod() == null ? "" : extmt.getNoticeperiod().substring(Math.max(0, extmt.getNoticeperiod().indexOf(",") + 1))));
                    tmpObj.put("wkstart", (extmt.getWkstarttime() == null ? "" : extmt.getWkstarttime()));
                    tmpObj.put("wkend", (extmt.getWkendtime() == null ? "" : extmt.getWkendtime()));
                    tmpObj.put("weeklyoff", extmt.getWeekoff());
                    tmpObj.put("commid", extmt.getCommid());
                    tmpObj.put("brachcode", extmt.getBranchcode());
//                    tmpObj.put("salarypermonth", extmt.getUserLogin().getUser().getSalary());
                    tmpObj.put("brachcity", extmt.getBranchcity());
                    tmpObj.put("brachaddr", extmt.getBranchaddr());
                    tmpObj.put("brachcountry", (extmt.getBranchcountry() == null ? "" : extmt.getBranchcountry().getId()));
                    tmpObj.put("keyskills", extmt.getKeyskills());
                    jarr.put(tmpObj);
                }
            } else {
                JSONObject tmpObj = new JSONObject();
                User u = (User) session.load(User.class, request.getParameter("userid"));
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                tmpObj.put("empid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
                //tmpObj.put("empid",u.getEmployeeid() );
                tmpObj.put("department", ua.getDepartment() == null ? "" : ua.getDepartment().getId());
                tmpObj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                tmpObj.put("fname", u.getFirstName());
                tmpObj.put("lname", u.getLastName());
                tmpObj.put("fullname", u.getFirstName() + " " + (u.getLastName() == null ? "" : u.getLastName()));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsManager.Empprofile", e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.Empprofile", e);
        }
        return result;
    }

    public static String getEmpidFormatEdit(Session session, HttpServletRequest request, Integer empid) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        int count = 0;
        String mainstr = "";
        String empids = "";
        String maxcount = null;
        try {

            String cmpnyid = AuthHandler.getCompanyid(request);
            String SELECT_USER_INFO = "from Company where companyID=?";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, cmpnyid);
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Company cmp = (Company) itr.next();
                CompanyPreferences cmpPref = (CompanyPreferences) session.load(CompanyPreferences.class, cmp.getCompanyID());
                String empidformat = cmpPref.getEmpidformat() == null ? "" : cmpPref.getEmpidformat();
                String[] row = (empidformat).split("-");
                if (row.length == 1) {
                    if ((row[0]).startsWith("0")) {
                        int len = row[0].length();
                        String regex = "%0" + len + "d";
                        empids = String.format(regex, empid);
                        mainstr = empids;
                    } else {
                        mainstr = empid.toString();
                    }

                } else {
                    for (int i = 0; i < row.length; i++) {

                        if ((row[i]).startsWith("0")) {
                            int len = row[i].length();
                            String regex = "%0" + len + "d";
                            empids = String.format(regex, empid);

                        }
                        if (!row[i].equals("")) {
                            if (!(row[i]).startsWith("0")) {
                                mainstr = mainstr + row[i] + "-";
                            } else {
                                mainstr = mainstr + empids + "-";
                            }
                        } else {
                            mainstr = mainstr + empids + "-";

                        }
                    }
                    mainstr = mainstr.substring(0, mainstr.length() - 1);
                }
            }
            obj.put("maxempid", mainstr);
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mainstr;
    }

    public static String getNewEmployeeIdFormat(HashMap<String, Object> requestParams){
    	String employeeid = "";
    	try{
    		String standardEmpId = requestParams.get("standardEmpId").toString();
    		String employeeIdFormat = requestParams.get("employeeIdFormat").toString();
    		String[] idFormates = standardEmpId.split("-");
    		if(idFormates.length==1){
    			employeeid = employeeIdFormat+"-";
    		}
    		for (int i = 0; i < idFormates.length; i++) {
    			if (idFormates[i].matches("[0-9]*") == true) {
    				employeeid += idFormates[i]+"-";
    			}else{
    				employeeid += (employeeIdFormat.equals("")?employeeIdFormat:(employeeIdFormat+"-"));
    			}
    		}
    		employeeid = employeeid.substring(0, employeeid.length() - 1);
    		idFormates = employeeid.split("-");
    		if(idFormates.length>2){
    			employeeid = "";
    			employeeid += (idFormates[0]+"-");
    			employeeid += (idFormates[2]+"-");
    			employeeid += idFormates[1];
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return employeeid;
    	}
    }
    
    public static String getEmpidFormat(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        int count = 0;
        String mainstr = "";
        String empids = "";
        Integer maxcount = null;
        try {

            String cmpnyid = AuthHandler.getCompanyid(request);
            maxcount = getMaxCountEmpid(session, request);
            String SELECT_USER_INFO = "from Company where companyID=?";
            List list = HibernateUtil.executeQuery(session, SELECT_USER_INFO, cmpnyid);
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Company cmp = (Company) itr.next();
                CompanyPreferences cmpPref = (CompanyPreferences) session.load(CompanyPreferences.class, cmp.getCompanyID());
                String empidformat = cmpPref.getEmpidformat() == null ? "" : cmpPref.getEmpidformat();
                String[] row = (empidformat).split("-");
                if (row.length == 1) {
                    if ((row[0]).startsWith("0")) {
                        int len = row[0].length();
                        String regex = "%0" + len + "d";
                        empids = String.format(regex, maxcount);
                        mainstr = empids;
                    } else {
                        mainstr = maxcount.toString();
                    }

                } else {

                    for (int i = 0; i < row.length; i++) {
                        if ((row[i]).startsWith("0")) {
                            int len = row[i].length();
                            String regex = "%0" + len + "d";
                            empids = String.format(regex, maxcount);

                        }
                        if (!row[i].equals("")) {
                            if (!(row[i]).startsWith("0")) {
                                mainstr = mainstr + row[i] + "-";
                            } else {
                                mainstr = mainstr + empids + "-";
                            }
                        } else {
                            mainstr = mainstr + empids + "-";
                        }
                    }
                    mainstr = mainstr.substring(0, mainstr.length() - 1);
                }

            }
            obj.put("maxempid", mainstr);
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Integer getMaxCountEmpid(Session session, HttpServletRequest request) throws ServiceException {
        Integer maxcount = null;
        try {
            String cmpnyid = AuthHandler.getCompanyid(request);
            String SELECT_USER_INFO1 = "select max(employeeid) from Useraccount where user.company.companyID=?";
            List list1 = HibernateUtil.executeQuery(session, SELECT_USER_INFO1, cmpnyid);
            Iterator itr1 = list1.iterator();
            while (itr1.hasNext()) {
                maxcount = ((Integer) itr1.next()) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxcount;
    }

    public static String GetApplicant(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "{'count':0, 'data':[]}";
        Integer start = Integer.parseInt(request.getParameter("start"));
        Integer limit = Integer.parseInt(request.getParameter("limit"));
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String userid;
        int count = 0;
        try {
            String cmpid = AuthHandler.getCompanyid(request);
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(cmpid);
            params.add(false);
            String searchString = "";
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 3);
                searchString = StringUtil.getSearchString(ss, "and", new String[]{"username", "firstname", "lastname"});
            }
            String hql = "from Jobapplicant where company.companyID=? and deleted=? "+ searchString;
            List lst = HibernateUtil.executeQuery(session, hql, params.toArray());
            List lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            count = lst.size();
            Iterator ite = lst1.iterator();
            // Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Jobapplicant extmt = (Jobapplicant) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("lid", extmt.getApplicantid());
                tmpObj.put("cname", extmt.getFirstname() + " " + extmt.getLastname());
                tmpObj.put("state", extmt.getState());
                tmpObj.put("contactno", extmt.getContactno());
                tmpObj.put("uname", extmt.getUsername());
                tmpObj.put("email", extmt.getEmail());
                tmpObj.put("address1", extmt.getAddress1());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("GetTaxperCatgry", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("GetTaxperCatgry", ex);
        } catch (JSONException ex) {
            throw new JSONException("GetTaxperCatgry");
        } finally {
            return result;
        }
    }

    public static String getRecruitersFunction(Session session, HttpServletRequest request,Integer start, Integer limit) throws ServiceException {
        String result = "{'count':0, 'data':[]}";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        try {
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(0);
            params.add(AuthHandler.getCompanyid(request));
            String searchString = "";
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                searchString = StringUtil.getSearchString(ss, "and", new String[]{"u.firstName", "u.lastName"});
            }
            String qry = "select u,r from Recruiter r right join r.recruit u where u.deleteflag=? and u.company.companyID=? "+ searchString;
            tabledata = HibernateUtil.executeQuery(session, qry, params.toArray());
            count=tabledata.size();
            List tabledata1 = HibernateUtil.executeQueryPaging(session, qry, params.toArray(), new Integer[]{start,limit});
            Iterator itr = tabledata1.iterator();
            while (itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                User u = (User) row[0];
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                Recruiter rec = (Recruiter) row[1];
                JSONObject obj = new JSONObject();
                obj.put("userid", u.getUserID());
                obj.put("username", u.getFirstName() + " " + u.getLastName());
                obj.put("emailid", u.getEmailID());
                obj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                obj.put("department", ua.getDepartment()!=null?ua.getDepartment().getValue():"");
                if (rec == null) {
                    obj.put("status", 3);
                } else {
                    obj.put("status", rec.getDelflag());
                }
                jarr.put(obj);
            }
            count = tabledata.size();
            jobj.put("data", jarr);
            jobj.put("Count", count);
            result = jobj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsManager.getRecruitersFunction", e);
        } finally {
            return result;
        }
    }

    public static String GetEmpDocuments(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException, SQLException, SQLException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String userid = "";
        String hql = "";
        int count = 0;
        try {
            userid = request.getParameter("userid");
            List lst = null;
            List lst1 = null;
            String currentuser = AuthHandler.getUserid(request);
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            if (request.getParameter("applicant").equalsIgnoreCase("applicant")) {
                hql = "from com.krawler.hrms.common.docs.HrmsDocmap where recid=? and docid.deleted=? and docid.applicantid.applicantid=? ";
                params.add(userid);
                params.add(false);
                params.add(userid);
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"docid.dispdocname","docid.docdesc"});
                    hql +=searchQuery;
                }
                lst = HibernateUtil.executeQuery(session, hql, params.toArray());
                lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            } else {
                if (request.getParameter("manager").equals("true")) {
                    hql = "from com.krawler.hrms.common.docs.HrmsDocmap where recid=? and docid.deleted=? ";
                    params.add(userid);
                    params.add(false);
                    if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(params, ss, 2);
                        String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"docid.dispdocname","docid.docdesc"});
                        hql +=searchQuery;
                    }
                    lst = HibernateUtil.executeQuery(session, hql, params.toArray());
                    lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                } else {
                    hql = "from com.krawler.hrms.common.docs.HrmsDocmap where recid=? and docid.deleted=? and docid.userid.userID=? ";
                    params.add(userid);
                    params.add(false);
                    params.add(currentuser);
                    if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(params, ss, 2);
                        String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"docid.dispdocname","docid.docdesc"});
                        hql +=searchQuery;
                    }
                    lst = HibernateUtil.executeQuery(session, hql, params.toArray());
                    lst1 = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                }
            }
            count = lst.size();
            Iterator ite = lst1.iterator();
            while (ite.hasNext()) {
                HrmsDocmap docs = (HrmsDocmap) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("docid", docs.getDocid().getDocid());
                tmpObj.put("docname", docs.getDocid().getDispdocname());
                tmpObj.put("docdesc", docs.getDocid().getDocdesc());
                if (request.getParameter("applicant").equalsIgnoreCase("applicant")) {
                    tmpObj.put("uploadedby", docs.getDocid().getApplicantid().getFirstname() + " " + docs.getDocid().getApplicantid().getLastname());
                } else {
                    tmpObj.put("uploadedby", docs.getDocid().getUserid().getFirstName() + " " + docs.getDocid().getUserid().getLastName());
                }
                Float dsize = Math.max(0, Float.parseFloat(docs.getDocid().getDocsize()) / 1024);
                tmpObj.put("docsize", String.valueOf(dsize));
                tmpObj.put("uploaddate", AuthHandler.getDateFormatter(request).format(docs.getDocid().getUploadedon()));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsManager.getEmpDocumentsFunction", e);
        }
        catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.getEmpDocumentsFunction", e);
        }
        return result;
    }

    public static String getapplicantdataFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String userid;
        int count = 0;
        try {
            String cmpid = AuthHandler.getCompanyid(request);
            String hql = "from Jobapplicant where applicantid=? and company.companyID=? ";
            List list = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("applicantid"), cmpid});
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                Jobapplicant jobapp = (Jobapplicant) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("title", jobapp.getTitle());
                tmpObj.put("firstname", jobapp.getFirstname());
                tmpObj.put("lastname", jobapp.getLastname());
                tmpObj.put("email", jobapp.getEmail());
                tmpObj.put("otheremail", jobapp.getOtheremail());
                tmpObj.put("birthdate", jobapp.getBirthdate());
                tmpObj.put("address1", jobapp.getAddress1());
                tmpObj.put("address2", jobapp.getAddress2());
                tmpObj.put("city", jobapp.getCity());
                tmpObj.put("state", jobapp.getState());
                if (jobapp.getCountryid() != null) {
                    tmpObj.put("country", jobapp.getCountryid().getId());
                }
                tmpObj.put("contactno", jobapp.getContactno());
                tmpObj.put("mobileno", jobapp.getMobileno());
                tmpObj.put("graddegree", jobapp.getGraddegree());
                tmpObj.put("gradspecialization", jobapp.getGradspecialization());
                tmpObj.put("gradcollege", jobapp.getGradcollege());
                tmpObj.put("graduniversity", jobapp.getGraduniversity());
                tmpObj.put("gradpercent", jobapp.getGradpercent());
                tmpObj.put("gradpassdate", jobapp.getGradpassdate());
                tmpObj.put("pgqualification", jobapp.getPgqualification());
                tmpObj.put("pgspecialization", jobapp.getPgspecialization());
                tmpObj.put("pgcollege", jobapp.getPgcollege());
                tmpObj.put("pguniversity", jobapp.getPguniversity());
                tmpObj.put("pgpercent", jobapp.getPgpercent());
                tmpObj.put("pgpassdate", jobapp.getPgpassdate());
                tmpObj.put("otherqualification", jobapp.getOtherqualification());
                tmpObj.put("othername", jobapp.getOthername());
                tmpObj.put("otherdetails", jobapp.getOtherdetails());
                tmpObj.put("otherpassdate", jobapp.getOtherpassdate());
                tmpObj.put("otherpercent", jobapp.getOtherpercent());
                tmpObj.put("experiencemonth", jobapp.getExperiencemonth());
                tmpObj.put("experienceyear", jobapp.getExperienceyear());
                tmpObj.put("functionalexpertise", jobapp.getFunctionalexpertise());
                tmpObj.put("currentindustry", jobapp.getCurrentindustry());
                tmpObj.put("currentorganization", jobapp.getCurrentorganization());
                tmpObj.put("currentdesignation", jobapp.getCurrentdesignation());
                tmpObj.put("grosssalary", jobapp.getGrosssalary());
                tmpObj.put("expectedsalary", jobapp.getExpectedsalary());
                tmpObj.put("category", jobapp.getCategory());
                tmpObj.put("companyrelative", jobapp.getCompanyrelative());
                tmpObj.put("appearedbefore", jobapp.getAppearedbefore());
                tmpObj.put("interviewlocation", jobapp.getInterviewlocation());
                tmpObj.put("keyskills", jobapp.getKeyskills());
                if (jobapp.getInterviewposition() != null) {
                    tmpObj.put("interviewposition", jobapp.getInterviewposition().getId());
                }
                tmpObj.put("interviewplace", jobapp.getInterviewplace());
                tmpObj.put("interviewdate", jobapp.getInterviewdate());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getapplicantdataFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getapplicantdataFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getapplicantdataFunction");
        } finally {
        }
        return result;
    }

       public static String getGroupheader(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        String st="Start Time";
        String end="End Time";
        try {
            JSONObject colHeader = new JSONObject();
            JSONObject grHeader = new JSONObject();
            JSONObject jObj = new JSONObject();
            jObj.put("header","Job");
            jObj.put("dataIndex","jobtype");
            jObj.put("editor","this.jobtext");
            colHeader.append("columnheader", jObj);
            for(int i=1;i<=14;i++){
            jObj = new JSONObject();
            if(i%2==0){
            jObj.put("header",end);
            }else{
                jObj.put("header",st);
            }
            jObj.put("dataIndex","col"+i+"");
            jObj.put("align","center");
            jObj.put("editor","new Wtf.form.TextField({regex:/^([0-1][0-9]|[2][0-3]):([0-5][0-9])$/,allowBlank:'false',maxLength:5})");
            colHeader.append("columnheader", jObj);
            }
            jObj = new JSONObject();
            jObj.put("header","Total");
            jObj.put("dataIndex","total");
            jObj.put("align","center");
            colHeader.append("columnheader", jObj);

             JSONObject grObj = new JSONObject();
             jObj = new JSONObject();
             jObj.put("header"," " );
             jObj.put("colspan", 2);
             jObj.put("align","center" );
             grObj.append("0",jObj );
             String [] dates=request.getParameterValues("datearray");
            for(int cnt=0;cnt<dates.length;cnt++) {
                 jObj = new JSONObject();
                 jObj.put("header", dates[cnt]);
                 jObj.put("colspan","2" );
                 jObj.put("align","center" );
                 grObj.append("0", jObj);
             }
             jObj = new JSONObject();
             jObj.put("header"," " );
             jObj.put("colspan", 1);
             jObj.put("align","center" );
             grObj.append("0",jObj );
             grHeader.append("groupheader", grObj);
            jObj = new JSONObject();
            jobj.put("success", true);
            jobj.append("data", colHeader);
            jobj.append("data", grHeader);
            result = jobj.toString();
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsManager.getGroupheader", e);
        } finally {
        }
        return result;
    }

    public static String getjobprofileFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String hql = "";
        int count = 0;
        List lst;
        try {
            Company cmp=(Company)session.get(Company.class,AuthHandler.getCompanyid(request));
//            String jobidformat=(cmpPref.getJobidformat()==null?"":cmpPref.getJobidformat());
            hql = "from Positionmain where positionid=?";
            lst = HibernateUtil.executeQuery(session, hql, request.getParameter("position"));
            count = lst.size();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                JSONObject tmpObj = new JSONObject();
                Positionmain psm = (Positionmain) ite.next();
                tmpObj.put("jobid", psm.getPositionid());
                tmpObj.put("posid", psm.getJobid());
                tmpObj.put("designation", psm.getPosition().getValue());
                tmpObj.put("details", psm.getDetails());
                tmpObj.put("department", psm.getDepartmentid().getValue());
                tmpObj.put("manager", psm.getManager().getUserID());
                tmpObj.put("startdate", psm.getStartdate());
                tmpObj.put("enddate", psm.getEnddate());
                tmpObj.put("expmonth", psm.getExperiencemonth());
                tmpObj.put("expyear", psm.getExperienceyear());
                tmpObj.put("relocation", psm.getRelocation());
                tmpObj.put("location", psm.getLocation());
                tmpObj.put("jobshift", psm.getJobshift());
                tmpObj.put("travel", psm.getTravel());
                tmpObj.put("nopos", psm.getNoofpos());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getjobprofileFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getjobprofileFunction");
        } finally {
        }
        return result;
    }

    public static String viewjobprofileFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {

            String hql = "from Jobprofile where position.positionid=? and type=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("position"), Integer.parseInt(request.getParameter("type"))});
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
            jobj.put("data", jarr);
            jobj.put("count", lst.size());
            result = jobj.toString();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("viewjobprofileFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("viewjobprofileFunction");
        } finally {
        }
        return result;
    }

    public static Integer getMaxCountJobid(Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        Integer maxcount = 0;
        try {
            String cmpnyid = AuthHandler.getCompanyid(request);
            String jobque = "select max(jobidwthformat) from Positionmain where company.companyID=?";
            List list1 = HibernateUtil.executeQuery(session, jobque, cmpnyid);
            if (!list1.isEmpty()) {
                maxcount = list1.get(0)==null?1:(Integer)list1.get(0)+ 1;
            }
        } catch (NullPointerException e) {
            throw ServiceException.FAILURE("hrmsManager.getMaxCountJobid", e);
        }
        return maxcount;
    }


    public static String getJobidFormat(Session session, HttpServletRequest request) throws ServiceException,JSONException,SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject obj = new JSONObject();
        int count = 0;
        String mainstr = "";
        Integer maxcount = 0;
        try {
            String cmpnyid = AuthHandler.getCompanyid(request);
            maxcount = getMaxCountJobid(session, request);
//            Company cmp=(Company) session.get(Company.class,cmpnyid );
            CompanyPreferences cmp=(CompanyPreferences) session.get(CompanyPreferences.class,cmpnyid );
            mainstr=(cmp.getJobidformat()==null?"":cmp.getJobidformat())+(maxcount.toString());
            obj.put("maxempid", mainstr);
            jarr.put(obj);
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (JSONException e) {
                throw ServiceException.FAILURE("hrmsManager.getJobidFormat", e);
        }catch(HibernateException e){
            throw ServiceException.FAILURE("hrmsManager.getJobidFormat", e);
        }
        return result;
    }

    public static JSONObject EmployeesTimesheetReport(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        int count = 0;
        String userid;
        String min;
        String hrs;
        int hr=0;
        int mn=0;
        try {
            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                userid = AuthHandler.getUserid(request);
            } else {
                userid = request.getParameter("empid");
            }
            startdate = (Date) fmt.parse(request.getParameter("startdate"));
            enddate = (Date) fmt.parse(request.getParameter("enddate"));
            Object[] obj = {startdate, enddate, userid};
            String hql = "from Timesheet where datevalue between ? and ? and userID.userID=? order by jobtype asc,datevalue asc ";
            List lst = HibernateUtil.executeQuery(session, hql, obj);

            Iterator ite = lst.iterator();
            JSONObject tmpObj = null;
            for (int i = 1; i <= 7; i++) {
                if (!ite.hasNext()) {
                    break;
                }
                Timesheet tmsht = (Timesheet) ite.next();
                if (i == 1) {
                    tmpObj = new JSONObject();
                    tmpObj.put("jobtype", tmsht.getJobtype());
                }
                    hr += tmsht.getWorktime();
                    mn +=tmsht.getWorktimemin();
                    if(mn>=60){
                     hr++;
                     mn -=60;
                    }
                hrs= tmsht.getWorktime().toString();
                hrs=(hrs.length()==1?"0"+hrs:hrs);
                min= tmsht.getWorktimemin().toString();
                min=(min.length()==1?"0"+min:min);
                tmpObj.put("col" + i, hrs+":"+min+" hrs");
                tmpObj.put("colid" + i, tmsht.getId());
                if (i == 7) {
                    tmpObj.put("total",(hr<10?"0"+hr:hr)+":"+(mn<10?"0"+mn:mn)+" hrs");
                    jarr.put(tmpObj);
                    i = 0;
                    hr=0;
                    mn=0;
                }

            }

            jobj.put("data", jarr);
            jobj.put("count", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobj;
    }
    public static String getComments(Session session, HttpServletRequest request) throws ServiceException,JSONException,SessionException{
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject commObj=new JSONObject();
        int count = 0;
        try {
            String goalids[] = request.getParameterValues("recid");
            for(int i=0;i<goalids.length;i++){
            String hql = "from GoalComments where goalid.id=? order by createdon desc";
            tabledata = HibernateUtil.executeQuery(session, hql,goalids[i]);
            count = tabledata.size();
            for (int j = 0; j < count; j++) {
                GoalComments log = (GoalComments) tabledata.get(j);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("commentid", log.getCommentid());
                tmpObj.put("comment", log.getComment());
                tmpObj.put("addedby", AuthHandler.getFullName(log.getUserid()));
                tmpObj.put("postedon", AuthHandler.getDateFormatter(request).format(log.getCreatedon()));
                jarr.put(tmpObj);
            }
            commObj.put("commList",jarr);
            }
            jobj.put("success", true);
            jobj.put("commData", commObj);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.getComments", e);
        }catch(JSONException e){
            throw ServiceException.FAILURE("hrmsManager.getComments", e);
        }
        return result;
    }
     public static String getAllReportChart(Session session, HttpServletRequest request) throws ServiceException,SessionExpiredException, ParseException{
        String result=" ";
        int hr=0;
        int mn=0;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
                String empid=request.getParameter("empid");
                String alldates=request.getParameter("dates");
                String[] dates=alldates.split(",");
                int len=dates.length;
                int i;
                int y;
                result = "<chart><series>";
                for(i=0;i<len;i++){
                    Date d=fmt.parse(dates[i]);
                    result +="<value xid=\""+i+"\">"+AuthHandler.getUserDateFormatter(request,session).format(d)+"</value>";
                }
                result += "</series><graphs>";
              result +="<graph gid=\"1\">";
                 for(y=0;y<len;y++){
                  Date startdate = (Date)fmt.parse(dates[y]);
                  Object[] obj = {startdate,empid};
                  String hql = "select sum(worktime),sum(worktimemin) from Timesheet where datevalue=? and userID.userID=? group by datevalue";
                  List lst = HibernateUtil.executeQuery(session, hql, obj);
                  Iterator ite=lst.iterator();
                  if(ite.hasNext()){
                      Object[] row=(Object[])ite.next();
                      hr=Integer.parseInt(row[0].toString());
                      mn=Integer.parseInt(row[1].toString());

                  }
                  if(mn>=60){
                   hr+=(mn/60);
                   mn=(mn%60);
                  }
                 //result +="<value xid=\""+y+"\">"+Math.round((hr+(mn/60.0))*100)/100.0+"</value>";
                 result +="<value xid=\""+y+"\">"+hr+"</value>";
               }
                         result += "</graph></graph></chart>";
        } catch (ServiceException e) {
            throw ServiceException.FAILURE("hrmsManager.getAllReportTemplate", e);
        } catch(ParseException e){
            throw ServiceException.FAILURE("hrmsManager.getAllReportTemplate", e);
        }
        return result;
    }
     public static String getAllReportChart1(Session session, HttpServletRequest request) throws ServiceException,SessionExpiredException, ParseException{
        String result=" ";
        int hr=0;
        int mn=0;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
                String empid=request.getParameter("empid");
                String alldates=request.getParameter("dates");
                String jobnames=request.getParameter("jobnames");
                String jobs=request.getParameter("jobs");
                String[] alljobs=jobs.split(",");
                String[] alljobnames=jobnames.split(",");
                String[] dates=alldates.split(",");
                int len=dates.length;
                Date startdate=(Date)fmt.parse(dates[0]);
                Date enddate=(Date)fmt.parse(dates[len-1]);
                int i;
                int y;
                result = "<chart><series>";
                for(i=0;i<alljobnames.length;i++){
                    result +="<value xid=\""+i+"\">"+alljobnames[i]+"</value>";
                }
                result += "</series><graphs>";
              result +="<graph gid=\"1\">";
                 for(y=0;y<alljobs.length;y++){
                  Object[] obj = {startdate,enddate,empid,alljobs[y]};
                  String hql = "select sum(worktime),sum(worktimemin) from Timesheet where datevalue between ? and ? and userID.userID=? and jobtype=? group by jobtype";
                  List lst = HibernateUtil.executeQuery(session, hql, obj);
                  Iterator ite=lst.iterator();
                  if(ite.hasNext()){
                      Object[] row=(Object[])ite.next();
                      hr=Integer.parseInt(row[0].toString());
                      mn=Integer.parseInt(row[1].toString());

                      }
                  if(mn>=60){
                   hr+=(mn/60);
                   mn=(mn%60);
                  }
                 //result +="<value xid=\""+y+"\">"+Math.round((hr+(mn/60.0))*100)/100.0+"</value>";
                 result +="<value xid=\""+y+"\">"+hr+"</value>";
               }
                         result += "</graph></graph></chart>";
        } catch (ServiceException e) {
            throw ServiceException.FAILURE("hrmsManager.getAllReportTemplate", e);
        } catch(ParseException e){
            throw ServiceException.FAILURE("hrmsManager.getAllReportTemplate", e);
        }
        return result;
    }
	public static HashMap updateappraisalGoalsFromCrm(String crmURL,Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        HashMap GoalRate =null;
        String msg = "";
        try {
            String uid = null;
            JSONArray data = null;
            String companyid = AuthHandler.getCompanyid(request);
            if (StringUtil.isNullOrEmpty(request.getParameter("isemployee"))) {
                uid = request.getParameter("empid");
            } else {
                uid = AuthHandler.getUserid(request);
            }

            if (!StringUtil.isNullOrEmpty(crmURL)) {
                JSONObject userData = new JSONObject();
                userData.put("companyid", companyid);
                userData.put("userid", uid);
                userData.put("remoteapikey", StorageHandler.GetRemoteAPIKey());
                String action = "12";
                JSONObject resObj = APICallHandler.callApp(crmURL, userData, companyid, action);
                if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                    data = new JSONArray(resObj.getString("data"));
                    GoalRate =new HashMap();
                    ExtractGoalInfo(data,GoalRate);
                } else {
                }
            }
            jobj.put("msg", msg);
            jobj.put("data", data);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.setPassword", e);
        }
        return GoalRate;
    }
    public static void ExtractGoalInfo(JSONArray jarr,HashMap GoalRate) throws ServiceException {
        try {
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
                    GoalRate.put(jobj.getString("gid")+"gname", jobj.getString("gname"));
                    GoalRate.put(jobj.getString("gid"), jobj.getString("percentgoal"));
                }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("hrmsHandler.updateGoal", ex);
        }
    }
    public static String getappraisalGoalsFunction(Session session, HttpServletRequest request,HashMap GoalRate) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject tmpObj = new JSONObject();
        String hql = "";
        List tabledata=null;
        try {
            if(StringUtil.isNullOrEmpty(request.getParameter("isemployee"))){
            hql = "from Appraisal where appraisal.appraisalid=?";
            tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("appraisal"));
            if (!tabledata.isEmpty()) {
                Appraisalmanagement appmanage = (Appraisalmanagement) session.load(Appraisalmanagement.class, request.getParameter("appraisal"));
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Appraisal app = (Appraisal) ite.next();
                    if (app.getGoal() != null) {
                        tmpObj = new JSONObject();
                        tmpObj.put("gid", app.getGoal().getId());
                        tmpObj.put("goalid", app.getAppid());
                        tmpObj.put("gname", app.getGoal().getGoalname());
                        tmpObj.put("gwth", app.getGoal().getGoalwth());
                        tmpObj.put("gcomplete", (app.getGoal().getPercentcomplete())==null?0:app.getGoal().getPercentcomplete());
                        tmpObj.put("assignedby", AuthHandler.getFullName(app.getGoal().getManager()));
                        if (appmanage.getManagerdraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("gmanrat", "");
                            tmpObj.put("mangoalcomment", "");
                        } else if(appmanage.getManagerdraft()==0 && Boolean.parseBoolean(request.getParameter("employee"))){
                            tmpObj.put("gmanrat", app.getGoalmanrating());
                            tmpObj.put("mangoalcomment", app.getGoalmancomment());
                        }
                        else{
                            tmpObj.put("gmanrat", app.getGoalmanrating());
                            tmpObj.put("mangoalcomment", app.getGoalmancomment());
                        }
                        if (appmanage.getEmployeedraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("gemprat", app.getGoalemprating());
                            tmpObj.put("empgoalcomment", app.getGoalempcomment());
                        } else if (appmanage.getEmployeedraft()==0 && !Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("gemprat", app.getGoalemprating());
                            tmpObj.put("empgoalcomment", app.getGoalempcomment());
                        }

                        else{
                            tmpObj.put("gemprat", "");
                            tmpObj.put("empgoalcomment", "");
                        }
                        jarr.put(tmpObj);
                    }
                }
            } else {
                String managerid;
                Date sdate=null;
                Date edate=null;
                if(StringUtil.isNullOrEmpty("managerid")){
                    managerid=AuthHandler.getUserid(request);
                } else{
                    managerid=request.getParameter("managerid");
                }
                if(!StringUtil.isNullOrEmpty("appraisal")){
                    Appraisalmanagement appr=(Appraisalmanagement) session.load(Appraisalmanagement.class, request.getParameter("appraisal"));
                    if (appr.getAppcycle() != null) {
                        sdate = appr.getAppcycle().getStartdate();
                        edate = appr.getAppcycle().getEnddate();
                    }
                }
                hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? and manager.userID=? and (startdate between ? and ?) and (enddate between ? and ?)";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("empid"), AuthHandler.getCompanyid(request),false, managerid, sdate, edate, sdate, edate});
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                    tmpObj = new JSONObject();
                    tmpObj.put("gid", fgmt.getId());
                    tmpObj.put("gname", fgmt.getGoalname());
                    tmpObj.put("gwth", fgmt.getGoalwth());
                    tmpObj.put("gcomplete", (fgmt.getPercentcomplete()==null)?0:fgmt.getPercentcomplete());
                    tmpObj.put("internal", fgmt.isInternal());
                    if(GoalRate!=null && !fgmt.isInternal() && GoalRate.containsKey(fgmt.getId())){
                        Float goalrate = Float.parseFloat((String)GoalRate.get(fgmt.getId()));
                        Integer gemprat = Math.round((goalrate * Finalgoalmanagement.MaxGoalRate) / 100);
                        tmpObj.put("gemprat", gemprat);
                        tmpObj.put("gmanrat", gemprat);
                        tmpObj.put("gname", GoalRate.get(fgmt.getId()+"gname"));
                        fgmt.setGoalname((String)GoalRate.get(fgmt.getId()+"gname"));
                        session.update(fgmt);
                    }
                    jarr.put(tmpObj);
                }
            }
            }else{
                  String[] appids=request.getParameter("appraisal").split(",");
                  for(int i=0;i<appids.length;i++){
                  hql = "from Appraisal where appraisal.appraisalid=?";
                  tabledata = HibernateUtil.executeQuery(session, hql,appids[i]);
                  Iterator ite = tabledata.iterator();
                  if(!tabledata.isEmpty()){
                while (ite.hasNext()) {
                    Appraisal app = (Appraisal) ite.next();
                    if (app.getGoal() != null) {
                        tmpObj = new JSONObject();
                        tmpObj.put("gid", app.getGoal().getId());
                        tmpObj.put("goalid", app.getAppid());
                        tmpObj.put("gname", app.getGoal().getGoalname());
                        tmpObj.put("gwth", app.getGoal().getGoalwth());
                        tmpObj.put("gcomplete", (app.getGoal().getPercentcomplete()==null)?0:app.getGoal().getPercentcomplete());
                        tmpObj.put("gemprat", app.getGoalemprating());
                        tmpObj.put("gmanrat", app.getGoalmanrating());
                        tmpObj.put("assignedby", AuthHandler.getFullName(app.getGoal().getManager()));
                        tmpObj.put("mangoalcomment", app.getGoalmancomment());
                        tmpObj.put("empgoalcomment", app.getGoalempcomment());
                        tmpObj.put("goalapprid", appids[i]);
                        jarr.put(tmpObj);
                    }
                }
                  }else{
                      String managerid;
                      Date sdate=null;
                      Date edate=null;
                    String empid=AuthHandler.getUserid(request);
                    Appraisalmanagement appr=(Appraisalmanagement) session.load(Appraisalmanagement.class, appids[i]);
                    managerid=appr.getManager().getUserID();
                    if (appr.getAppcycle() != null) {
                        sdate = appr.getAppcycle().getStartdate();
                        edate = appr.getAppcycle().getEnddate();
                    }
                hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? and manager.userID=? and (startdate between ? and ?) and (enddate between ? and ?)";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{empid, AuthHandler.getCompanyid(request),false,managerid, sdate, edate, sdate, edate});
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                    tmpObj = new JSONObject();
                    tmpObj.put("gid", fgmt.getId());
                    tmpObj.put("gname", fgmt.getGoalname());
                    tmpObj.put("gwth", fgmt.getGoalwth());
                    tmpObj.put("gcomplete", (fgmt.getPercentcomplete()==null)?0:fgmt.getPercentcomplete());
                    tmpObj.put("assignedby", AuthHandler.getFullName(fgmt.getManager()));
                    tmpObj.put("goalapprid", appids[i]);
                    if(GoalRate!=null && !fgmt.isInternal() && GoalRate.containsKey(fgmt.getId())){
                        Float goalrate = Float.parseFloat((String)GoalRate.get(fgmt.getId()));
                        Integer gemprat = Math.round((goalrate * Finalgoalmanagement.MaxGoalRate) / 100);
                        tmpObj.put("gemprat", gemprat);
                        tmpObj.put("gmanrat", gemprat);
                        tmpObj.put("gname", GoalRate.get(fgmt.getId()+"gname"));
                        fgmt.setGoalname((String)GoalRate.get(fgmt.getId()+"gname"));
                        session.update(fgmt);
                    }
                    jarr.put(tmpObj);
                }

                  }
            }
        }
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            result = jobj.toString();
        }catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisalGoalsFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalGoalsFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisalGoalsFunction");
        } finally {
        }
        return result;
    }

    public static String getappraisalCompetencyFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject tmpObj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String hql = "";
        Iterator ite;
        double ans=0;
        String qry="";
        List lst=null;
        try {
            hql = "from Appraisal where appraisal.appraisalid=?";
            tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("appraisal"));
            if (!tabledata.isEmpty()) {
                qry="select count(*) from Appraisal where appraisal.appraisalid=? and competency!=null";
                lst=HibernateUtil.executeQuery(session, qry, request.getParameter("appraisal"));
                Appraisalmanagement appmanage = (Appraisalmanagement) session.load(Appraisalmanagement.class, request.getParameter("appraisal"));
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Appraisal app = (Appraisal) ite.next();
                    if (app.getCompetency() != null) {
                        tmpObj = new JSONObject();
                        tmpObj.put("cmptid", app.getCompetency().getMid());
                        tmpObj.put("mid", app.getCompetency().getMid());
                        tmpObj.put("compid", app.getAppid());
                        tmpObj.put("cmptname", app.getCompetency().getMastercmpt().getCmptname());
                        if(hrmsManager.checkModule("weightage", session, request)) {
                            tmpObj.put("cmptwt", app.getCompetency().getWeightage());
                        } else{
                            ans = 100 / Double.parseDouble(lst.get(0).toString());
                            tmpObj.put("cmptwt", ans);
                        }
                        tmpObj.put("cmptdesc", app.getCompetency().getMastercmpt().getCmptdesc());
                        if (appmanage.getManagerdraft() == 1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("manrat", "");
                            tmpObj.put("mangap", "");
                            tmpObj.put("mancompcomment", "");
                        } else if(appmanage.getManagerdraft() == 0 && Boolean.parseBoolean(request.getParameter("employee"))){
                            tmpObj.put("manrat", app.getCompmanrating());
                            tmpObj.put("mangap", app.getCompmangap());
                            tmpObj.put("mancompcomment", app.getCompmancomment());
                        }
                        else {
                            tmpObj.put("manrat", app.getCompmanrating());
                            tmpObj.put("mangap", app.getCompmangap());
                            tmpObj.put("mancompcomment", app.getCompmancomment());
                        }
                        if (appmanage.getEmployeedraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("emprat", app.getCompemprating());
                            tmpObj.put("empgap", app.getCompempgap());
                            tmpObj.put("empcompcomment", app.getCompempcomment());
                        } else if (appmanage.getEmployeedraft()==0 && Boolean.parseBoolean(request.getParameter("employee"))) {
                            tmpObj.put("emprat", app.getCompemprating());
                            tmpObj.put("empgap", app.getCompempgap());
                            tmpObj.put("empcompcomment", app.getCompempcomment());
                        }

                        else {
                            tmpObj.put("emprat", "");
                            tmpObj.put("empgap", "");
                            tmpObj.put("empcompcomment", "");
                        }
                        jarr.put(tmpObj);
                    }
                }
            } else {
                qry="select count(*) from Managecmpt where desig.id=? and delflag=0";
                lst=HibernateUtil.executeQuery(session, qry, request.getParameter("desig"));
                hql = "from Managecmpt where desig.id=? and delflag=0";
                tabledata = HibernateUtil.executeQuery(session, hql, request.getParameter("desig"));
                ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Managecmpt log = (Managecmpt) ite.next();
                    tmpObj = new JSONObject();
                    tmpObj.put("cmptid", log.getMastercmpt().getCmptid());
                    tmpObj.put("mid", log.getMid());
                    tmpObj.put("cmptname", log.getMastercmpt().getCmptname());
                    if (hrmsManager.checkModule("weightage", session, request)) {
                        tmpObj.put("cmptwt", log.getWeightage());
                    } else {
                        ans = 100 / Double.parseDouble(lst.get(0).toString());
                        tmpObj.put("cmptwt", ans);
                    }
                    tmpObj.put("cmptdesc", log.getMastercmpt().getCmptdesc());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            result = jobj.toString();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalCompetencyFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisalCompetencyFunction");
        } finally {
        }
        return result;
    }

    public static ArrayList getappraisalData(String empid, String appraisalid, Session session, HttpServletRequest request) throws ServiceException {
        ArrayList result = new ArrayList();
        List tabledata = null;
        try {
            String hql = "from Assignmanager where assignemp.userID=? and managerstatus=1";
            tabledata = HibernateUtil.executeQuery(session, hql, empid);
            if (!tabledata.isEmpty()) {
                Assignmanager log = (Assignmanager) tabledata.get(0);
                result.add(log.getAssignman().getFirstName() + " " + log.getAssignman().getLastName());
            } else {
                result.add("");
            }
            hql = "select employeecomment,managercomment from Appraisalmanagement where appraisalid=?";
            tabledata = HibernateUtil.executeQuery(session, hql, appraisalid);
            Object[] row=(Object[]) tabledata.get(0);
            //Appraisalmanagement log = (Appraisalmanagement) tabledata.get(0);
            result.add(row[0]);
            result.add(row[1]);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisallistFunction", ex);
        }
        return result;
    }

    public static String getappraisallistFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List lst = null;
        ArrayList data;
        String hql;
        try {
               String appcylid=request.getParameter("appcylid");
            if (!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request))) {
                if (Boolean.parseBoolean(request.getParameter("employee"))) {
                     hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
                    lst = HibernateUtil.executeQuery(session, hql,new Object[]{AuthHandler.getUserid(request),appcylid});
                }
                else{
                hql = "from Appraisalmanagement where manager.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by concat(employee.firstName, employee.lastName) asc";
                lst = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request),appcylid});
                }
            } else {
                if (Boolean.parseBoolean(request.getParameter("employee"))) {
                    hql = "from Appraisalmanagement where employee.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
                    lst = HibernateUtil.executeQuery(session, hql,new Object[]{AuthHandler.getUserid(request),appcylid});
                } else {
                    hql = "from Assignmanager where assignman.userID=?";
                    lst = HibernateUtil.executeQuery(session, hql, AuthHandler.getUserid(request));
                    Iterator ite = lst.iterator();
                    String users = "";
                    while (ite.hasNext()) {
                        Assignmanager log = (Assignmanager) ite.next();
                        users +="'"+log.getAssignemp().getUserID()+"',";
                    }
                    if(users.length() > 0){
                        users = users.substring(0, users.length()-1);
                    }
//                     if (hrmsManager.checkModule("appraisal", session, request)) {
//                         hql = "from Appraisalmanagement where employee.userID in (" + users + ") and manager.userID=? and managerstatus=0 and employeestatus=1 and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by appcycle.submitstartdate asc";
//                    } else {
                        hql = "from Appraisalmanagement where employee.userID in (" + users + ") and manager.userID=? and appcycle.id=? and (date(now()) between appcycle.submitstartdate and appcycle.submitenddate) order by concat(employee.firstName, employee.lastName) asc";
//                    }
                    //String qry = "select u, r from Assignmanager r right join r.recruit u where u.role.ID=? and u.company.companyID=? ";
                    lst = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request),appcylid});
                }
            }
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                data = new ArrayList();
                Appraisalmanagement app = (Appraisalmanagement) ite.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, app.getEmployee().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("appraisalid", app.getAppraisalid());
                tmpObj.put("username", app.getEmployee().getFirstName() + " " + app.getEmployee().getLastName());
                tmpObj.put("userid", app.getEmployee().getUserID());
                data = getappraisalData(app.getEmployee().getUserID(), app.getAppraisalid(), session, request);
                tmpObj.put("managername", app.getManager().getFirstName() + " " + app.getManager().getLastName());
                if(app.getEmployeedraft()==1 && !Boolean.parseBoolean(request.getParameter("employee"))){
                    tmpObj.put("employeecomment", "");
                } else {
                    tmpObj.put("employeecomment", data.get(1));
                }
                if(app.getManagerdraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))){
                    tmpObj.put("managercomment", "");
                } else {
                    tmpObj.put("managercomment", data.get(2));
                }
                tmpObj.put("managercompscore", app.getManagercompscore());
                tmpObj.put("managerstatus", app.getManagerstatus());
                tmpObj.put("employeestatus", app.getEmployeestatus());
                tmpObj.put("employeecompscore", app.getEmployeecompscore());
                tmpObj.put("managergoalscore", app.getManagergoalscore());
                tmpObj.put("employeegoalscore", app.getEmployeegoalscore());
                tmpObj.put("designationid", ua.getDesignationid()!=null?ua.getDesignationid().getId():"");
                tmpObj.put("designation", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                tmpObj.put("salaryrec", app.getSalaryrecommend());
                tmpObj.put("managerid", app.getManager().getUserID());
                if (app.getNewdesignation() != null) {
                    tmpObj.put("newdesig", app.getNewdesignation().getId());
                } else {
                    tmpObj.put("newdesig", "");
                }
                if (app.getNewdepartment() != null) {
                    tmpObj.put("newdept", app.getNewdepartment().getId());
                } else {
                    tmpObj.put("newdept", "");
                }
                if (app.getPerformance() != null) {
                    tmpObj.put("performance", app.getPerformance().getId());
                } else {
                    tmpObj.put("performance", "");
                }
                if(app.getAppcycle()!=null){
                    tmpObj.put("appcycle", app.getAppcycle().getCyclename());
                    tmpObj.put("startdate", app.getAppcycle().getStartdate());
                    tmpObj.put("enddate", app.getAppcycle().getEnddate());
                    tmpObj.put("appcycleid", app.getAppcycle().getId());
                }
                tmpObj.put("salaryinc", app.getSalaryincrement());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", lst.size());
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisallistFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisallistFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisallistFunction");
        } finally {
        }
        return result;
    }

/*        public static String getUserList(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List lst = null;
        ArrayList data;
        String hql;
        try {
           String appcylid=request.getParameter("appcylid");
           Appraisalcycle cycleObj = (Appraisalcycle) session.load(Appraisalcycle.class, appcylid);
            if (!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request))) {
//                if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) {
//                    hql = "select distinct employee from Appraisalmanagement a where appcycle=? and reviewstatus=0 order by a.employee.firstName";
//                } else {
                    hql = "select distinct employee from Appraisalmanagement a where appcycle=? order by a.employee.firstName";
//                }
                lst = HibernateUtil.executeQuery(session, hql, new Object[]{cycleObj});
            } else {
                String users = "";
                if (Boolean.parseBoolean(request.getParameter("reviewer"))) {
                    hql = "from Assignreviewer where reviewer.userID=?";
                    lst = HibernateUtil.executeQuery(session, hql, AuthHandler.getUserid(request));
                    Iterator ite = lst.iterator();
                    while (ite.hasNext()) {
                        Assignreviewer log = (Assignreviewer) ite.next();
                        users +="'"+log.getEmployee().getUserID()+"',";
                    }
                } else {
                    hql = "from Assignmanager where assignman.userID=?";
                    lst = HibernateUtil.executeQuery(session, hql, AuthHandler.getUserid(request));
                    Iterator ite = lst.iterator();
                    while (ite.hasNext()) {
                        Assignmanager log = (Assignmanager) ite.next();
                        users +="'"+log.getAssignemp().getUserID()+"',";
                    }
                }

                if(users.length() > 0){
                    users = users.substring(0, users.length()-1);
                }
//                if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) {
//                    hql = "select distinct employee from Appraisalmanagement a where a.employee.userID in (" + users + ") and appcycle=?  and reviewstatus=0 order by a.employee.firstName";
//                } else {
                    hql = "select distinct employee from Appraisalmanagement a where a.employee.userID in (" + users + ") and appcycle=? order by a.employee.firstName";
//                }
                lst = HibernateUtil.executeQuery(session, hql, new Object[]{cycleObj});
            }
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                data = new ArrayList();
                User user = (User) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("name", user.getFirstName()+ " " + user.getLastName());
                tmpObj.put("id", user.getUserID());
                tmpObj=getAppraisalManagers(session,user.getUserID(),appcylid,tmpObj);
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", lst.size());
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisallistFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisallistFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisallistFunction");
        } finally {
        }
        return result;
    }
*/
    public static String getUserList(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List lst = null;
        String hql;
        try {
           String appcylid=request.getParameter("appcylid");
           String users = "";
           Appraisalcycle cycleObj = (Appraisalcycle) session.load(Appraisalcycle.class, appcylid);
            hql = "from Assignreviewer where reviewer.userID=? and reviewerstatus=1";
            lst = HibernateUtil.executeQuery(session, hql, AuthHandler.getUserid(request));
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Assignreviewer log = (Assignreviewer) ite.next();
                users +="'"+log.getEmployee().getUserID()+"',";
            }

            if(users.length() > 0){
                users = users.substring(0, users.length()-1);
            }
            hql = "select distinct employee from Appraisalmanagement a where a.employee.userID in (" + users + ") and appcycle=? order by a.employee.firstName";
            lst = HibernateUtil.executeQuery(session, hql, new Object[]{cycleObj});
            ite = lst.iterator();
            while (ite.hasNext()) {
                User user = (User) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("name", user.getFirstName()+ " " + user.getLastName());
                tmpObj.put("id", user.getUserID());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", lst.size());
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisallistFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisallistFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisallistFunction");
        } finally {
        }
        return result;
    }

    public static JSONObject getEmployeeAppraisals(Session session, HttpServletRequest request, String employeeid, JSONObject tmpObj) throws ServiceException, JSONException, SessionExpiredException {
        List lst = null;
        try {
            String hql = "from Appraisalmanagement where employee.userID=?";
            lst = HibernateUtil.executeQuery(session, hql, employeeid);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Appraisalmanagement app = (Appraisalmanagement) ite.next();
                String cname = "";
                String sdate = "";
                String edate = "";
                String mname = "";
                if (app.getAppcycle() != null) {
                    sdate = AuthHandler.getDateFormatter(request).format(app.getAppcycle().getStartdate());
                    edate = AuthHandler.getDateFormatter(request).format(app.getAppcycle().getEnddate());
                    cname = app.getAppcycle().getCyclename();
                    mname =  app.getManager().getFirstName() + " " +app.getManager().getLastName() ;

                    tmpObj.append("cyclename", cname);
                    tmpObj.append("startdate", sdate);
                    tmpObj.append("enddate", edate);
                    tmpObj.append("manager", mname);
                    if(app.getAppraisalstatus().equals("submitted")) {
                        tmpObj.append("status", "Completed");
                    } else {
                        tmpObj.append("status", app.getAppraisalstatus());
                    }
                    tmpObj.append("edate", AuthHandler.getDateFormatter(request).format(app.getAppcycle().getSubmitenddate()));
                    tmpObj.append("sdate", AuthHandler.getDateFormatter(request).format(app.getAppcycle().getSubmitstartdate()));
                }
            }
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisalemployeeFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalemployeeFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisalemployeeFunction");
        } finally {
        }
        return tmpObj;
    }
    public static String getappraisalFunction(Session session, HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException, SessionExpiredException, SQLException {
        String uid = "";
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        int count = 0;
        String hql;
        Object[] obj=null;
        ArrayList data;
        ArrayList params = new ArrayList();
        Iterator ite;
        try {
            uid = AuthHandler.getUserid(request);
            String ss = request.getParameter("ss");
            if (!(PermissionHandler.isEmployee(session, request)) && !(PermissionHandler.isManager(session, request))) {
                hql = "from User where deleted=? and company.companyID=?";
                params.add(false);
                params.add(AuthHandler.getCompanyid(request));

                if(!StringUtil.isNullOrEmpty(request.getParameter("dept"))){
                    if(!StringUtil.equal(request.getParameter("dept"),"0")){
                        hql=hql+" and department.id=?";
                        params.add(request.getParameter("dept"));
                    }
                }
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"firstName","lastName"});
                    hql +=searchQuery;
                }
                 hql +=" order by firstName,lastName";
                tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
                count = tabledata.size();
                if (!StringUtil.isNullOrEmpty(request.getParameter("paging"))) {
                    tabledata = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                }
                ite = tabledata.iterator();
                Empprofile emp;
                while (ite.hasNext()) {
                    User log = (User) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getUserID());
                    emp = (Empprofile) session.get(Empprofile.class,log.getUserLogin().getUserID());
                    data = new ArrayList();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getUserID());
                    if(emp !=null)
                        tmpObj.put("joindate", emp.getJoindate());
                    else
                        tmpObj.put("joindate", "");
                    tmpObj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
                    if (ua.getDepartment() != null) {
                        tmpObj.put("department", ua.getDepartment().getValue());
                    }
                    else{
                        tmpObj.put("department", "");
                    }
                    if (ua.getDesignationid() != null) {
                        tmpObj.put("designation", ua.getDesignationid().getValue());
                    }
                    else{
                        tmpObj.put("designation", "");
                    }
                    tmpObj.put("email", log.getEmailID());
                    tmpObj.put("fullname", log.getFirstName() + " " + (log.getLastName() == null ? "" : log.getLastName()));
                    tmpObj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));

                    tmpObj = getEmployeeAppraisals(session, request, log.getUserID(), tmpObj);
//                    data = getappraisalempData(log.getUserID(), uid, session, request);
//                    tmpObj.put("appraisalid", data.get(0));
//                    tmpObj.put("type", data.get(1));
//                    tmpObj.put("sdate", data.get(2));
//                    tmpObj.put("edate", data.get(3));
//                    tmpObj.put("status", data.get(4));
                    jArr.put(tmpObj);
                }
            } else {
                hql = "from Assignmanager where assignman.userID=? and assignemp.deleted=? and assignemp.company.companyID=? and managerstatus=1";
                params.add(uid);
                params.add(false);
                params.add(AuthHandler.getCompanyid(request));
                if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"firstName","lastName"});
                    hql +=searchQuery;
                }
                tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
                count = tabledata.size();
                if (!StringUtil.isNullOrEmpty(request.getParameter("paging"))) {
                    tabledata = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                }
                ite = tabledata.iterator();
                Empprofile ep;
                while (ite.hasNext()) {
                    Assignmanager log = (Assignmanager) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getAssignemp().getUserID());
                    ep = (Empprofile) session.get(Empprofile.class, log.getAssignemp().getUserLogin().getUserID());
                    data = new ArrayList();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", log.getAssignemp().getUserID());
                    if(ep != null)
                        tmpObj.put("joindate", ep.getJoindate());
                    tmpObj.put("employeeid", ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
                    if (ua.getDepartment() != null) {
                        tmpObj.put("department", ua.getDepartment().getValue());
                    }
                    else{
                        tmpObj.put("department", "");
                    }
                    if (ua.getDesignationid() != null) {
                        tmpObj.put("designation", ua.getDesignationid().getValue());
                    }
                    else{
                        tmpObj.put("designation", "");
                    }
                    tmpObj.put("email", log.getAssignemp().getEmailID());
                    tmpObj.put("fullname", log.getAssignemp().getFirstName() + " " + (log.getAssignemp().getLastName() == null ? "" : log.getAssignemp().getLastName()));

                    tmpObj = getEmployeeAppraisals(session, request, log.getAssignemp().getUserID(), tmpObj);
//                    data = getappraisalempData(log.getAssignemp().getUserID(), uid, session, request);
//                    tmpObj.put("appraisalid", data.get(0));
//                    tmpObj.put("type", data.get(1));
//                    tmpObj.put("sdate", data.get(2));
//                    tmpObj.put("edate", data.get(3));
//                    tmpObj.put("status", data.get(4));
                    jArr.put(tmpObj);
                }
            }
            jobj.put("data", jArr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getEmpForManagerFunction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getEmpForManagerFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getEmpForManagerFunction");
        } finally {
        }
        return result;
    }

    public static ArrayList getappraisalempData(String empid, String managerid, Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        ArrayList result = new ArrayList();
        List tabledata = null;
        boolean flag=true;
        int m1,m2;
        SimpleDateFormat sdf;
        try {
            String hql = "from Appraisalmanagement where employee.userID=? and manager.userID=? order by startdate";
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{empid, managerid});
            if (!tabledata.isEmpty()) {
                Appraisalmanagement log = (Appraisalmanagement) tabledata.get(tabledata.size()-1);
                if(log.getAppraisaltype().equalsIgnoreCase("Monthly")){
                    sdf=new SimpleDateFormat("yyyyMM");
                    m1=Integer.parseInt(sdf.format(log.getEnddate()));
                    m2=Integer.parseInt(sdf.format(new Date()));
                    if(m1!=m2){
                        flag=false;
                    }
                }else{
                    sdf=new SimpleDateFormat("yyyy");
                    m1=Integer.parseInt(sdf.format(log.getEnddate()));
                    m2=Integer.parseInt(sdf.format(new Date()));
                    if(m1!=m2){
                        flag=false;
                    }
                }
                result.add(log.getAppraisalid());
                result.add(log.getAppraisaltype());
                result.add(AuthHandler.getDateFormatter(request).format(log.getStartdate()));
                result.add(AuthHandler.getDateFormatter(request).format(log.getEnddate()));
                if (flag) {
                    result.add(log.getAppraisalstatus());
                }else{
                    result.add("Kickstart my appraisal");
                }
            }else{
                result.add("");
                result.add("");
                result.add("");
                result.add("");
                result.add("Kickstart my appraisal");
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalempData", ex);
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getappraisalempData", se.getMessage());
        }
        return result;
    }

    public static String getappraisalcycleFunction(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List lst = null;
        String bol = request.getParameter("employee");
        boolean bolen = false;
        if (!StringUtil.isNullOrEmpty(bol)) {
            bolen = Boolean.parseBoolean(bol);
        }
        int count = 0;
        try {
            Date date1 = new Date();
            Appraisalcycle currentCycleObj = null;
            List lst1 = null;
            Iterator ite1;
            String hql1 = "";
            if (StringUtil.isNullOrEmpty(bol) || (bolen == false)) {
                hql1 = "from Appraisalcycle where company.companyID=? and (startdate <= ? and enddate >= ?) " +
                        " or enddate < ? order by enddate desc ";
                lst1 = HibernateUtil.executeQuery(session, hql1, new Object[]{AuthHandler.getCompanyid(request), date1, date1, date1});
                ite1 = lst1.iterator();
                if (ite1.hasNext()) {
                    currentCycleObj = (Appraisalcycle) ite1.next();
                }
                String hql = "from Appraisalcycle where company.companyID=?";
                lst = HibernateUtil.executeQuery(session, hql, AuthHandler.getCompanyid(request));
                count = lst.size();
                Iterator ite = lst.iterator();
                while (ite.hasNext()) {
                    Appraisalcycle app = (Appraisalcycle) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("appcycleid", app.getId());
                    tmpObj.put("appcycle", app.getCyclename());
                    tmpObj.put("startdate", app.getStartdate());
                    tmpObj.put("enddate", app.getEnddate());
                    tmpObj.put("submitstartdate", app.getSubmitstartdate());
                    tmpObj.put("submitenddate", app.getSubmitenddate());
                    if (app.isCycleapproval()) {
                            tmpObj.put("status", "1");
                        } else {
                            tmpObj.put("status", "0");
                        }
                    if (app.getId().equals(currentCycleObj.getId())) {
                        tmpObj.put("currentFlag", 1);
                    } else {
                        tmpObj.put("currentFlag", 0);
                    }
                    jarr.put(tmpObj);
                }
            } else {
                hql1 = "from Appraisalcycle where company.companyID=? and (startdate <= ? and enddate >= ?) " +
                        " or enddate < ? order by enddate desc ";
                lst1 = HibernateUtil.executeQuery(session, hql1, new Object[]{AuthHandler.getCompanyid(request), date1, date1, date1});
                ite1 = lst1.iterator();
                if (ite1.hasNext()) {
                    currentCycleObj = (Appraisalcycle) ite1.next();
                }
                /*String myreport = "and apm.employeestatus=0";
                if (!StringUtil.isNullOrEmpty(request.getParameter("myreport"))) {
                    myreport = "";
                }*/
   //             String hql = "select apm,appcyle from Appraisalmanagement apm right outer join apm.appcycle appcyle where apm.employee.userID=? "+
     //                   "and appcyle.company.companyID=? and (date(now()) between apm.appcycle.submitstartdate and apm.appcycle.submitenddate) group by apm.appcycle.id";
                 String hql = "select apm,appcyle from Appraisalmanagement apm right outer join apm.appcycle appcyle where apm.employee.userID=? "+
                            "and appcyle.company.companyID=? group by apm.appcycle.id";
                    lst = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request), AuthHandler.getCompanyid(request)});
                count = lst.size();
                Iterator ite = lst.iterator();
                if (ite.hasNext()) {
                    while (ite.hasNext()) {
                        Object[] row = (Object[]) ite.next();
                        Appraisalcycle app = (Appraisalcycle) row[1];
                        JSONObject tmpObj = new JSONObject();
                        tmpObj.put("appcycleid", app.getId());
                        tmpObj.put("appcycle", app.getCyclename());
                        tmpObj.put("startdate", app.getStartdate());
                        tmpObj.put("enddate", app.getEnddate());
                        tmpObj.put("submitstartdate",AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitstartdate()));
                        tmpObj.put("submitenddate", AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitenddate()));
                        if (app.isCycleapproval()) {
                            tmpObj.put("status", "1");
                        } else {
                            tmpObj.put("status", "0");
                        }
                        if (app.getId().equals(currentCycleObj.getId())) {
                            tmpObj.put("currentFlag", 1);
                        } else {
                            tmpObj.put("currentFlag", 0);
                        }
                        jarr.put(tmpObj);
                    }
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", count);
            result = jobj.toString();
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getappraisalcycleFunction", ex);
        } catch (JSONException ex) {
            throw new JSONException("getappraisalcycleFunction");
        }
        return result;
    }

    public static boolean checkModule(String checklink,Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        boolean result = false;
        String companyid="";
        try {
            companyid=AuthHandler.getCompanyid(request);
            result=checkModule(checklink,session,request,companyid);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("hrmsManager.checkModule", ex);
        }
        return result;
    }
    public static boolean checkModule(String checklink,Session session, HttpServletRequest request,String companyid) throws ServiceException, SessionExpiredException {
        boolean result = false;
        try {
            CompanyPreferences cmpPref = null;
            String query = "from CompanyPreferences where company.companyID=?";
            List lst = HibernateUtil.executeQuery(session, query, companyid);
            if (!lst.isEmpty()) {
                cmpPref = (CompanyPreferences) session.get(CompanyPreferences.class, companyid);
                if (checklink.equalsIgnoreCase("appraisal")) {
                    if (cmpPref.isSelfappraisal()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("competency")) {
                    if (cmpPref.isCompetency()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("goal")) {
                    if (cmpPref.isGoal()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("anonymous")) {
                    if (cmpPref.isAnnmanager()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("approveAppWin")) {
                    if (cmpPref.isApproveappraisal()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("weightage")) {
                    if (cmpPref.isWeightage()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("reviewappraisal")) {
                    if (cmpPref.isReviewappraisal()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("fullupdates")) {
                    if (cmpPref.isFullupdates()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("modaverage")) {
                    if (cmpPref.isModaverage()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("promotion")) {
                    if (cmpPref.isPromotion()) {
                        result=true;
                    }
                }
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("hrmsManager.checkModule", ex);
        }
        return result;
    }

    public static String getratingdataFunction(Session session, HttpServletRequest request) throws ServiceException,JSONException,SessionException{
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String hql="from Goalrating where company.companyID=?";
            tabledata = HibernateUtil.executeQuery(session, hql, AuthHandler.getCompanyid(request));
            if(!tabledata.isEmpty()){
                Goalrating goalrate = (Goalrating) tabledata.get(0);
                int minvalue=Integer.parseInt(goalrate.getMinvalue());
                int maxvalue=Integer.parseInt(goalrate.getMaxvalue());
                for(int i=minvalue;i<=maxvalue;i++){
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("id", i);
                    tmpObj.put("rating", i);
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            result = jobj.toString();
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.getratingdataFunction", e);
        }catch(JSONException e){
            throw ServiceException.FAILURE("hrmsManager.getratingdataFunction", e);
        }
        return result;
    }
    public static JSONObject getAppraisalManagers(Session session,String empid,String cycleid,JSONObject jobj ) throws ServiceException,HibernateException, JSONException {

        try {
             String hql="select count(*) from Appraisalmanagement where employee.userID=? and appcycle.id=? ";
             List lst=HibernateUtil.executeQuery(session,hql,new Object[]{empid,cycleid});
             jobj.put("totalappraisal", lst.get(0));
             hql="select count(*) from Appraisalmanagement where employee.userID=? and appcycle.id=? and managerstatus!=?";
             lst=HibernateUtil.executeQuery(session,hql,new Object[]{empid,cycleid,0});
             jobj.put("appraisalsubmitted", lst.get(0));
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsManager.getAppraisalManagers", e);
        }
        return jobj;
    }

    public static JSONObject getAppraisalManagercount(Session session,String empid,String cycleid,JSONObject jobj ) throws ServiceException,HibernateException, JSONException {

        try {
             String hql="select count(*) from Appraisalmanagement where manager.userID=? and appcycle.id=? ";
             List lst=HibernateUtil.executeQuery(session,hql,new Object[]{empid,cycleid});
             jobj.put("totalappraisal", lst.get(0));
             hql="select count(*) from Appraisalmanagement where manager.userID=? and appcycle.id=? and managerstatus!=?";
             lst=HibernateUtil.executeQuery(session,hql,new Object[]{empid,cycleid,0});
             jobj.put("appraisalsubmitted", lst.get(0));
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsManager.getAppraisalManagers", e);
        }
        return jobj;
    }

    public static boolean isAppraiser(String appraiserid,Session session, HttpServletRequest request) throws ServiceException{
        boolean result = false;
        try {
            String query = "from Assignmanager where assignman.userID=? and managerstatus=1";
            List lst = HibernateUtil.executeQuery(session, query, appraiserid);
            if (!lst.isEmpty()) {
                result=true;
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("isAppraiser", ex);
        }
        return result;
    }

    public static boolean showappraisalForm(boolean employee, Session session, HttpServletRequest request) throws ServiceException, SessionExpiredException {
        boolean result = false;
        List tabledata;
        String hql;
        try {
            Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
            if (employee) {
                hql = "from Appraisalmanagement where employee.userID=? and appcycle.submitenddate>=?";
            } else{
                hql = "from Appraisalmanagement where manager.userID=? and appcycle.submitenddate>=?";
            }
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request),userdate});
            if (!tabledata.isEmpty()) {
                result = true;
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("isReviewer", ex);
        }
        return result;
    }


    public static String getallemployeeFunction(Session session, HttpServletRequest request) throws ServiceException,JSONException,SessionException{
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String hql="";
        try {
             String manager = request.getParameter("manager");
             String userid=request.getParameter("userid");
            if (Boolean.parseBoolean(manager)) {
            hql = "from User where deleteflag=? and company.companyID=? and userID!=? and" +
                    " userID not in (select assignman.userID from Assignmanager where assignemp.userID=? and managerstatus=1 ) ";
            }else{
                  hql = "from User where deleteflag=? and company.companyID=? and userID!=? and" +
                    " userID not in (select reviewer.userID from Assignreviewer where employee.userID=? and reviewerstatus=1 ) ";
            }
            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{0,AuthHandler.getCompanyid(request),userid,userid});
            Iterator ite = tabledata.iterator();
            while (ite.hasNext()) {
                User u = (User) ite.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("userid", u.getUserID());
                tmpObj.put("username", u.getFirstName() + " " + (u.getLastName() == null ? "" : u.getLastName()));
                tmpObj.put("departmentname", ua.getDepartment()==null ? "" :ua.getDepartment().getValue());
                tmpObj.put("designation", ua.getDesignationid()==null ? "" :ua.getDesignationid().getValue());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            result = jobj.toString();
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("hrmsManager.getallemployeeFunction", e);
        }catch(JSONException e){
            throw ServiceException.FAILURE("hrmsManager.getallemployeeFunction", e);
        }
        return result;
    }

      public static String getAssignedManager(Session session, HttpServletRequest request) throws ServiceException,JSONException,SessionException{
        String result = "";
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String manager = request.getParameter("manager");
            if (Boolean.parseBoolean(manager)) {
                String hql = "from Assignmanager where assignemp.userID=? and managerstatus=1";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("userid")});
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Assignmanager u = (Assignmanager) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getAssignman().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", u.getAssignman().getUserID());
                    tmpObj.put("username", AuthHandler.getFullName(u.getAssignman()));
                    tmpObj.put("departmentname", ua.getDepartment() == null ? "" : ua.getDepartment().getValue());
                    tmpObj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                    jarr.put(tmpObj);
                }
            } else {
                String hql = "from Assignreviewer where employee.userID=? and reviewerstatus=1";
                tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("userid")});
                Iterator ite = tabledata.iterator();
                while (ite.hasNext()) {
                    Assignreviewer u = (Assignreviewer) ite.next();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, u.getReviewer().getUserID());
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", u.getReviewer().getUserID());
                    tmpObj.put("username", AuthHandler.getFullName(u.getReviewer()));
                    tmpObj.put("departmentname", ua.getDepartment() == null ? "" : ua.getDepartment().getValue());
                    tmpObj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                    jarr.put(tmpObj);
                }
            }

            jobj.put("data", jarr);
            jobj.put("count", tabledata.size());
            result = jobj.toString();
        } catch (NullPointerException e) {
            throw ServiceException.FAILURE("hrmsManager.getAssignedManager", e);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("hrmsManager.getAssignedManager", e);
        }
        return result;
    }

     public static String getAppraisalReport(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        String manComments = "";
        int counter = 1;
        try {
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = AuthHandler.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ? and reviewstatus=0 and managerstatus=1 order by rand()";
            } else {
                hql = "from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2 order by rand()";
            }
            list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID, userID});
            count = list.size();
            if (list.size() > 0) {
                Appraisalmanagement log = (Appraisalmanagement) list.get(0);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getEmployee().getUserID());
                JSONObject tmpObj = new JSONObject();
//                if (StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
//                    tmpObj.put("empid", hrmsManager.getEmpidFormatEdit(session, request, log.getEmployee().getEmployeeid()));
//                }else{
//                    tmpObj.put("empid",request.getParameter("empid"));
//                }
                tmpObj.put("empname", log.getEmployee().getFirstName() + " " + log.getEmployee().getLastName());
                if (log.getEmployeedraft() == 0) {
                   tmpObj.put("empcomment", (StringUtil.isNullOrEmpty(log.getEmployeecomment())?"None":log.getEmployeecomment()));
               } else {
                    tmpObj.put("empcomment", "None");
                }
                tmpObj.put("reviewercomment", log.getReviewercomment());
                tmpObj.put("designation", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                tmpObj.put("appcylename", log.getAppcycle().getCyclename());
                if (StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                    tmpObj.put("appcylestdate", AuthHandler.getUserDateFormatter(request, session).format(log.getAppcycle().getStartdate()));
                    tmpObj.put("appcylendate", AuthHandler.getUserDateFormatter(request, session).format(log.getAppcycle().getEnddate()));
                } else {
                    DateFormat df=new SimpleDateFormat("EEE, d MMM yyyy");
                    tmpObj.put("appcylestdate", df.format(log.getAppcycle().getStartdate()));
                    tmpObj.put("appcylendate", df.format(log.getAppcycle().getEnddate()));
                }
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                String empavgscore = "";
                hql = "from Appraisal where appraisal.appraisalid = ? and competency is not null";
                List lst = HibernateUtil.executeQuery(session, hql, new Object[]{log.getAppraisalid()});
                JSONArray cmpObj = new JSONArray();
                String qry = "select avg(manavg) from competencyAvg where appcycle.id=? and employee.userID=? ";
                List list2 = HibernateUtil.executeQuery(session, qry, new Object[]{request.getParameter("appraisalcycid"), userID});
                double avg = 0;
                String globalAvg = "0.00";
                if (!list2.isEmpty() && list2.get(0) != null) {
                    avg = (Double) list2.get(0);
                    DecimalFormat df = new DecimalFormat("#.##");
                    globalAvg = df.format(avg);
                    if (!globalAvg.contains(".")) {
                        globalAvg += ".00";
                    }
                }
                for (int i = 0; i < lst.size(); i++) {
                    Appraisal apsl = (Appraisal) lst.get(i);
                    JSONArray comments = new JSONArray();
                    JSONObject appObj = new JSONObject();
                    if (apsl.getCompetency() != null) {
                        String empwght = "";
                        String manwght = "";
                        appObj.put("comptename", apsl.getCompetency().getMastercmpt().getCmptname());
                        appObj.put("comptdesc", apsl.getCompetency().getMastercmpt().getCmptdesc());
                        if (log.getEmployeedraft() == 0) {
                            appObj.put("selfcomment", apsl.getCompempcomment());
                            appObj.put("selfcompscore", apsl.getCompemprating() == 0 ? "" : apsl.getCompemprating());
                        } else {
                            appObj.put("selfcomment", "");
                            appObj.put("selfcompscore", "");
                        }
                        double avgRat = 0;
                        ArrayList initialcomments = new ArrayList();
                        ArrayList initialscores = new ArrayList();
                        JSONObject jempcomments = new JSONObject();
                        for (int j = 0; j < list.size(); j++) {
                            Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                            hql = "from competencyAvg where appcycle = ? and competency.mid = ? and employee = ?";
                            List alst = HibernateUtil.executeQuery(session, hql, new Object[]{apmt.getAppcycle(), apsl.getCompetency().getMid(), apmt.getEmployee()});
                            if (alst.size() > 0) {
                                competencyAvg compavg = (competencyAvg) alst.get(0);
                                avgRat = compavg.getManavg();
                            }
                            hql = "from Appraisal where appraisal.appraisalid = ? and competency.mid=?";
                            List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{apmt.getAppraisalid(), apsl.getCompetency().getMid()});
                            if (wlst.size() > 0) {
                                Double wt1, wt2;
                                Appraisal wapsl = (Appraisal) wlst.get(0);
                                wt1 = wapsl.getCompmanrating();
                                wt2 = wapsl.getCompemprating();
                                empwght += wt2.intValue() + ", ";
                                if(wt1.intValue()!=0){
                                    initialscores.add(wt1.intValue() + "");
                                }
                                if (!StringUtil.isNullOrEmpty(wapsl.getCompmancomment())) {
                                    initialcomments.add(wapsl.getCompmancomment());
                                }
                                if (i < 1) {
                                    empavgscore += apmt.getEmployeecompscore() + ", ";
                                }
                            }
                            if (!StringUtil.isNullOrEmpty(apmt.getManagercomment()) && (i < 1)) {
                                manComments += counter + ") " + apmt.getManagercomment();
                                if (!StringUtil.isNullOrEmpty(request.getParameter("filetype")) || !StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                                    manComments += ",";
                                } else {
                                    manComments += "<br>";
                                }
                                counter++;
                            }
                        }
                        if(empwght.length()>0){
                        empwght = empwght.substring(0, empwght.trim().length() - 1);
                        }
                        String scoreAvg = "0.00";
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        scoreAvg = decimalFormat.format(avgRat);
                        if (!scoreAvg.contains(".")) {
                            scoreAvg += ".00";
                        }
                        List<String> shufflist = initialcomments;
                        Collections.shuffle(shufflist);
                        int indx = 1;
                        for (String inicom : shufflist) {
                            jempcomments = new JSONObject();
                            jempcomments.put("managercomment", inicom);
                            comments.put(jempcomments);
                            indx++;
                        }
                        shufflist = initialscores;
                        Collections.shuffle(shufflist);
                        for (String inicom : shufflist) {
                            manwght += inicom + ", ";
                        }
                        if(manwght.length()>0){
                            manwght = manwght.substring(0, manwght.trim().length() - 1);
                        }
                        appObj.put("comments", comments);
                        appObj.put("compempwght", empwght);
                        appObj.put("compmanwght", manwght);
                        appObj.put("nominalRat", scoreAvg);
                    }
                    cmpObj.put(appObj);
                }
                if(empavgscore.length()>0){
                    empavgscore = empavgscore.substring(0, empavgscore.trim().length() - 1);
                }
                if (hrmsManager.checkModule("competency", session, request,ua.getUser().getCompany().getCompanyID())) {
                    tmpObj.put("competencies", cmpObj);
                    tmpObj.put("manavgwght", globalAvg);
                } else {
                    tmpObj.put("competencies", new JSONArray());
                }
                tmpObj.put("mancom", manComments);
                tmpObj.put("compempavgwght", empavgscore);
                tmpObj = getAppraisalManagers(session, userID, appCycleID, tmpObj);
                jarr.put(tmpObj);
            }
            JSONObject countObj = new JSONObject();
            countObj = getAppraisalManagers(session, userID, appCycleID, countObj);
            countObj.put("data", jarr);
            jarr1.put(countObj);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", count);
            result = jobj.toString();
        } catch (JSONException ex) {
            throw new JSONException("hrmsManager.getAppraisalReport");
        } finally {
        }
        return result;
    }

    public static String getAppraisalReportGoalsforGrid(Session session, HttpServletRequest request) {
//        KwlReturnObject result = null,result1 = null;
        List list = null,list1 = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        
            String result = "";

        try {
//            Company cmp = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = AuthHandler.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ? and reviewstatus=0 and managerstatus=1 order by rand()";
            } else {
                hql = "from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2 order by rand()";
            }
            list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID, userID});
            count = list.size();
            JSONArray cmpObj = new JSONArray();
            String jempcomments = "";
            if (list.size() > 0) {
                Iterator ite = list.iterator();
                while (ite.hasNext()) {
                Appraisalmanagement log = (Appraisalmanagement) ite.next();
                Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getEmployee().getUserID());
                JSONObject tmpObj = new JSONObject();
                String manageravgscore = "";
                String empavgscore = "";
                hql = "from Appraisal where appraisal.appraisalid = ? ";
                List lst = HibernateUtil.executeQuery(session, hql, new Object[]{log.getAppraisalid()});
                for (int i = 0; i < lst.size(); i++) {
                     Appraisal app1 = (Appraisal) lst.get(i);
                            if (app1.getGoal() != null) {
                                JSONObject appObj = new JSONObject();
                                appObj.put("gid", app1.getGoal().getId());
                                appObj.put("goalid", app1.getAppid());//goalspcificid(primary id appraisal)
                                appObj.put("gname", app1.getGoal().getGoalname());
                                appObj.put("gwth", app1.getGoal().getGoalwth());//goal weightage
                                appObj.put("assignedby", AuthHandler.getFullName(app1.getGoal().getManager()));
                                if (log.getManagerdraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                    appObj.put("gmanrat", "");
                                    appObj.put("mangoalcomment", "");
                                } else if(log.getManagerdraft()==0 && Boolean.parseBoolean(request.getParameter("employee"))){
                                    appObj.put("gmanrat", app1.getGoalmanrating());
                                    appObj.put("mangoalcomment", app1.getGoalmancomment());
                                }
                                else{
                                    appObj.put("gmanrat", app1.getGoalmanrating());
                                    appObj.put("mangoalcomment", app1.getGoalmancomment());
                                }
                                if (log.getEmployeedraft()==1 && Boolean.parseBoolean(request.getParameter("employee"))) {
                                    appObj.put("gemprat", app1.getGoalemprating());
                                    appObj.put("empgoalcomment", app1.getGoalempcomment());
                                } else if (log.getEmployeedraft()==0 && !Boolean.parseBoolean(request.getParameter("employee"))) {
                                    appObj.put("gemprat", app1.getGoalemprating());
                                    appObj.put("empgoalcomment", app1.getGoalempcomment());
                                }

                                else{
                                    appObj.put("gemprat", "");
                                    appObj.put("empgoalcomment", "");
                                }
                                cmpObj.put(appObj);
                            }
                        }
                    }
            }
            
            jobj.put("success", true);
            jobj.put("data", cmpObj);
            jobj.put("totalCount", count);
            result = jobj.toString();
        } catch (JSONException ex) {
            throw new JSONException("hrmsManager.getAppraisalReport");
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }
    public static String getAppraisalReportforGrid(Session session, HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String result = "";
        List list = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONArray jarr1 = new JSONArray();
        int count = 0;
        int cnt = 0;
        String hql = "";
        try {
            Company cmp = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
            String userID = request.getParameter("userid");
            if (StringUtil.isNullOrEmpty(userID)) {
                userID = AuthHandler.getUserid(request);
            }
            String appCycleID = request.getParameter("appraisalcycid");
            if (Boolean.parseBoolean(request.getParameter("reviewappraisal"))) { //In review appraisal tab fetch only pending requests.
                hql = "from Appraisalmanagement where appcycle.id = ? and employee.userID = ? and reviewstatus=0 and managerstatus=1 order by rand()";
            } else {
                hql = "from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2 order by rand()";
            }
            list = HibernateUtil.executeQuery(session, hql, new Object[]{appCycleID, userID});
            count = list.size();
            JSONArray cmpObj = new JSONArray();
            String jempcomments = "";
            if (list.size() > 0) {
                Appraisalmanagement log = (Appraisalmanagement) list.get(0);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, log.getEmployee().getUserID());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("empid", hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
                tmpObj.put("empname", AuthHandler.getFullName(log.getEmployee()));
                tmpObj.put("reviewercomment", log.getReviewercomment());
                tmpObj.put("designation", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                tmpObj.put("appcylename", log.getAppcycle().getCyclename());
                tmpObj.put("appcylestdate", AuthHandler.getUserDateFormatter(request, session).format(log.getAppcycle().getStartdate()));
                tmpObj.put("appcylendate", AuthHandler.getUserDateFormatter(request, session).format(log.getAppcycle().getEnddate()));
                tmpObj.put("dept", ua.getDepartment() != null ? ua.getDepartment().getValue() : "");
                String manageravgscore = "";
                String empavgscore = "";
                hql = "from Appraisal where appraisal.appraisalid = ? and competency is not null";
                List lst = HibernateUtil.executeQuery(session, hql, new Object[]{log.getAppraisalid()});
                cmpObj = new JSONArray();
                for (int i = 0; i < lst.size(); i++) {
                    Appraisal apsl = (Appraisal) lst.get(i);
                    JSONObject appObj = new JSONObject();
                    if (apsl.getCompetency() != null) {
                        String empwght = "";
                        String manwght = "";
                        appObj.put("comptename", apsl.getCompetency().getMastercmpt().getCmptname());
                        appObj.put("comptdesc", apsl.getCompetency().getMastercmpt().getCmptdesc());
                        if(log.getEmployeedraft()==0){
                            appObj.put("selfcomment", apsl.getCompempcomment());
                            appObj.put("selfcompscore", apsl.getCompemprating()==0 ? "":apsl.getCompemprating());
                        } else {
                            appObj.put("selfcomment", "");
                            appObj.put("selfcompscore", "");
                        }
                        String qry="select avg(manavg) from competencyAvg where appcycle.id=? and competency.mastercmpt.cmptid=? ";
                        List list2=HibernateUtil.executeQuery(session,qry,new Object[]{request.getParameter("appraisalcycid"),apsl.getCompetency().getMastercmpt().getCmptid()});
                        double avg=0;
                        if(!list2.isEmpty() && list2.get(0)!=null) {
                            avg=(Double)list2.get(0);
                        }
                        appObj.put("global", avg);

                        int countComment=1;
                        jempcomments = "";
                        ArrayList initialcomments=new ArrayList();
                        ArrayList initialscores=new ArrayList();
                        double avgRat = 0;
                        for (int j = 0; j < list.size(); j++) {
                            Appraisalmanagement apmt = (Appraisalmanagement) list.get(j);
                            hql = "from competencyAvg where appcycle = ? and competency.mid = ? and employee = ?";
                            List alst = HibernateUtil.executeQuery(session, hql, new Object[]{apmt.getAppcycle(), apsl.getCompetency().getMid(), apmt.getEmployee()});
                            if(alst.size() > 0) {
                                competencyAvg compavg = (competencyAvg) alst.get(0);
                                  avgRat=compavg.getManavg();
                            }
                            hql = "from Appraisal where appraisal.appraisalid = ? and competency.mid=?";
                            List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{apmt.getAppraisalid(), apsl.getCompetency().getMid()});
                            if (wlst.size() > 0) {
                                Double wt1,wt2;
                                Appraisal wapsl = (Appraisal) wlst.get(0);
                                wt1=wapsl.getCompmanrating();
                                wt2=wapsl.getCompemprating();
                                empwght += wt2.intValue() + ", ";
                                if(wt1.intValue()!=0){
                                    initialscores.add(wt1.intValue()+"");
                                }
                                if (!StringUtil.isNullOrEmpty(wapsl.getCompmancomment())) {
                                    initialcomments.add(wapsl.getCompmancomment());
                                }
                                countComment++;
                                if (i < 1) {
                                    empavgscore += apmt.getEmployeecompscore() + ", ";
                                    manageravgscore += apmt.getManagercompscore() + ", ";
                                }
                            }
                        }
                        empwght = empwght.substring(0, empwght.trim().length() - 1);

                        String scoreAvg="0.00";
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        scoreAvg = decimalFormat.format(avgRat);
                        if(!scoreAvg.contains("."))
                            scoreAvg += ".00";

                        List<String> shufflist = initialcomments;
                        Collections.shuffle(shufflist);
                        int indx=1;
                        for (String inicom : shufflist) {
                            jempcomments += ""+(indx)+")  "+inicom + "<br>";
                            indx++;
                        }
                        shufflist=initialscores;
                        Collections.shuffle(shufflist);
                        for (String inicom : shufflist) {
                            manwght += inicom + ", ";
                        }
                        if(manwght.length()>0){
                            manwght = manwght.substring(0, manwght.trim().length() - 1);
                        }
                        appObj.put("comments", jempcomments);
                        appObj.put("compempwght", empwght);
                        appObj.put("compmanwght", manwght);
                        appObj.put("nominalRat", scoreAvg);
                    }
                    cmpObj.put(appObj);
                }
                empavgscore = empavgscore.substring(0, empavgscore.trim().length() - 1);
                manageravgscore = manageravgscore.substring(0, manageravgscore.trim().length() - 1);



                tmpObj.put("data", cmpObj);
                tmpObj.put("compempavgwght", empavgscore);
                tmpObj.put("manavgwght", manageravgscore);
                tmpObj = getAppraisalManagers(session, userID, appCycleID, tmpObj);
                jarr.put(tmpObj);
            }
            JSONObject countObj = new JSONObject();
            jobj.put("success", true);
            jobj.put("data", cmpObj);
            jobj.put("totalCount", count);
            result = jobj.toString();
        } catch (JSONException ex) {
            throw new JSONException("hrmsManager.getAppraisalReport");
        } finally {
        }
        return result;
    }

    public static String getUpdatesForWidgets(Session session, HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        JSONArray jarr = new JSONArray();
        JSONArray temp = new JSONArray();
        try {
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            int start1 = Integer.parseInt(start);
            int limit1 = Integer.parseInt(limit);
            int diff;
            String userid = AuthHandler.getUserid(request);
            StringBuffer usersList = new StringBuffer();
            usersList.append("'" + userid + "'");
            String goaldate = "";
            if (hrmsManager.checkModule("fullupdates", session, request)) {
                Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
                String query = "select distinct appm.appcycle from Appraisalmanagement appm where appm.manager.userID=? and appm.managerstatus=0 and ( appm.appcycle.submitstartdate<= ? and appm.appcycle.submitenddate>=? ) ";
                List recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request), userdate, userdate});
                Iterator itr = recordTotalCount.iterator();
                String cmpsub = AuthHandler.getCmpSubscription(request);
                User user = (User) session.load(User.class, AuthHandler.getUserid(request));

                while (itr.hasNext()) {
                    String updateDiv = "";
                    JSONObject obj = new JSONObject();
                    Appraisalcycle app = (Appraisalcycle) itr.next();
                    diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                    updateDiv += "Submit the <a href='#' onclick='competencyedit()'> appraisal form </a> for appraisal cycle " + app.getCyclename() + " in <font color='green'> " + diff + "</font> day(s)";
                    obj.put("update", getContentSpan(updateDiv));
                    jArr.put(obj);
                }
                if (!hrmsManager.checkModule("reviewappraisal", session, request)) {
                    query = "from Appraisalmanagement appm where appm.managerstatus=1 and appm.reviewstatus!=2 and appm.appcycle.submitenddate<?";
                    recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{userdate});
                    itr = recordTotalCount.iterator();
                    while (itr.hasNext()) {
                        Appraisalmanagement app = (Appraisalmanagement) itr.next();
                        app.setReviewstatus(2);
                        session.update(app);
                    }
                }
                query = "from Appraisalmanagement appm where appm.employee.userID=? and appm.reviewstatus=2 group by appm.appcycle";
                recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request)});
                itr = recordTotalCount.iterator();
                while (itr.hasNext()) {
                    Appraisalmanagement app = (Appraisalmanagement) itr.next();
                    if (app.getReviewersubmitdate() != null) {
                        diff = (int) ((userdate.getTime() - app.getReviewersubmitdate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                        if (diff < 10) {
                            String updateDiv = "";
                            JSONObject obj = new JSONObject();
                            updateDiv += "Your <a href='#' onclick='myfinalReport()'>appraisal</a> for appraisal cycle " + app.getAppcycle().getCyclename() + " has been approved";
                            obj.put("update", getContentSpan(updateDiv));
                            jArr.put(obj);
                        }
                    }
                }

                if (PermissionHandler.isSubscribed(hrms_Modules.appraisal, cmpsub) &&
                        hrmsManager.checkModule("appraisal", session, request)) {
                    query = "select distinct appm.appcycle from Appraisalmanagement appm where appm.employee.userID=? and appm.employeestatus=0 and ( appm.appcycle.submitstartdate<=? and appm.appcycle.submitenddate>=? )";
                    Object[] object = {AuthHandler.getUserid(request), userdate, userdate};
                    recordTotalCount = HibernateUtil.executeQuery(session, query, object);
                    itr = recordTotalCount.iterator();

                    while (itr.hasNext()) {
                        String updateDiv = "";
                        JSONObject obj = new JSONObject();
                        Appraisalcycle app = (Appraisalcycle) itr.next();
                        diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                        updateDiv += "Fill the <a href='#' onclick='myAppraisal()'> appraisal form </a>in <font color='green'> " + diff + "</font> day(s) for appraisal cycle " + app.getCyclename() + " initiated by " + user.getCompany().getCreator().getFirstName() + " " + user.getCompany().getCreator().getLastName();
                        obj.put("update", getContentSpan(updateDiv));
                        jArr.put(obj);
                    }
                    if (hrmsManager.checkModule("goal", session, request)) {
                        query = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? order by createdon desc";
                        recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request), AuthHandler.getCompanyid(request), false});
                        itr = recordTotalCount.iterator();
                        while (itr.hasNext()) {
                            String updateDiv = "";
                            JSONObject obj = new JSONObject();
                            Finalgoalmanagement fgmt = (Finalgoalmanagement) itr.next();
                            if (fgmt.getCreatedon() != null) {
                                goaldate = AuthHandler.getUserDateFormatter(request, session).format(fgmt.getCreatedon());
                            } else {
                                goaldate = userdate.toString();
                            }
                            updateDiv += "<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a> goal assigned by<font color='green'> " + fgmt.getAssignedby() + "</font> on <font color='green'>" + goaldate + "</font>";
                            obj.put("update", getContentSpan(updateDiv));
                            jArr.put(obj);
                        }
                    }
                }

                if (PermissionHandler.isSubscribed(hrms_Modules.recruitment, cmpsub) ){
                    query = "select delflag from Recruiter where recruit.userID=? and delflag=0";
                    recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request)});
                    itr = recordTotalCount.iterator();
                    if (itr.hasNext()) {
                        String updateDiv = "";
                        JSONObject obj = new JSONObject();
                        updateDiv += "<p>You are selected on a interview panel.<br/>Please,click on following links for confirmation as a interviewer.</p>"
                                +"<p><a href='#' onclick='interviewerPosition(1)'>Accept</a>&nbsp;&nbsp;<a href='#' onclick='interviewerPosition(0)'>Reject</a></p>";
                        obj.put("update", getContentSpan(updateDiv));
                        jArr.put(obj);
                    }
                }
            } else {
                JSONObject totalno = new JSONObject();
                String updateDiv = "";
                Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
                String query = "select distinct appm.appcycle from Appraisalmanagement appm where (appm.manager.userID=? or appm.employee.userID=?) and appm.appcycle.submitenddate>=?";
                List recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{AuthHandler.getUserid(request), AuthHandler.getUserid(request),userdate});               
                Iterator itr = recordTotalCount.iterator();
                while (itr.hasNext()) {
                    JSONObject obj = new JSONObject();
                    Appraisalcycle app = (Appraisalcycle) itr.next();             
                        Object[] row;
                        String selfapp = "";
                        updateDiv = "Appraisal Cycle: <font color='green'>" + app.getCyclename() + "</font><hr><li style='margin-left:10px;'>Deadline for submission: <font color='blue'>" + AuthHandler.getUserDateFormatter(request, session).format(app.getSubmitenddate()) + "</font></li>";
                        String hql = "select appm.employee.userID,appm.employeestatus,appm.reviewstatus from Appraisalmanagement appm where appm.manager.userID=? and " +
                                "appm.appcycle.id=? group by appm.appcycle.id";
                        Object[] object = {AuthHandler.getUserid(request), app.getId()};
                        recordTotalCount = HibernateUtil.executeQuery(session, hql, object);
                        if (recordTotalCount.size() > 0) {
                            row = (Object[]) recordTotalCount.get(0);
                            if (hrmsManager.isAppraiser(AuthHandler.getUserid(request), session, request)) {
                                totalno = getAppraisalManagercount(session, AuthHandler.getUserid(request), app.getId(), totalno);
                                int submitted = totalno.getInt("appraisalsubmitted");
                                int remaIning = totalno.getInt("totalappraisal") - submitted;
                                updateDiv += "<li style='margin-left:10px;'>No. of Appraisals Submitted: <font color='blue'>" + submitted + "</font><br>";
                                updateDiv += "No. of Appraisals Remaining: <font color='blue'>" + remaIning + "</font></li>";
                            }
                        }
                        hql = "from Appraisalmanagement appm where appm.employee.userID=? and appm.appcycle.id=? group by appm.appcycle.id";
                        recordTotalCount = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request), app.getId()});
                        if (!recordTotalCount.isEmpty()) {
                            Appraisalmanagement log = (Appraisalmanagement) recordTotalCount.get(0);
                            if ((Integer) log.getEmployeestatus() == 0) {
                                selfapp = "Not Submitted";
                            } else {
                                selfapp = "Submitted";
                            }
                            updateDiv += "<li style='margin-left:10px;'>Self Appraisal: <font color='blue'>" + selfapp + "</font></li>";
                        }
                        obj.put("update", getContentSpan(updateDiv));
                        jArr.put(obj);   
                }
                if (!hrmsManager.checkModule("reviewappraisal", session, request)) {
                    query = "from Appraisalmanagement where managerstatus=1 and reviewstatus!=2 and appcycle.submitenddate<? and appcycle.company.companyID=?";
                    List rvlist = HibernateUtil.executeQuery(session, query,new Object[]{userdate,AuthHandler.getCompanyid(request)});
                    Iterator ritr = rvlist.iterator();
                    while (ritr.hasNext()) {
                        Appraisalmanagement app = (Appraisalmanagement) ritr.next();
//                        app.setEmployeestatus(1);
                        app.setReviewstatus(2);
                        session.update(app);
                    }
                }
            }
            if (!hrmsManager.checkModule("reviewappraisal", session, request)) {
                calculateModAvg(session, request, 1);
            } else {
                calculateModAvg(session, request, 0);
            }
            temp = jArr;
            int ed = Math.min(temp.length(), start1 + limit1); 
            for (int i = start1; i < ed; i++) {
                jarr.put(temp.getJSONObject(i));
            }
            jobj.put("data", jarr);
            jobj.put("count", temp.length());
        } catch (JSONException e) {
            throw ServiceException.FAILURE("remoteApi.getUpdatesAudit:" + e.getMessage(), e);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("crmManager.getUpdatesForWidgets", ex);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj.toString();
    }

    public static String getContentSpan(String textStr) {
        String span = "<div>" + textStr + "<div style='clear:both;visibility:hidden;height:0;line-height:0;'></div></div>";
        return span;
    }

    public static String serverHTMLStripper(String stripTags)
            throws IllegalStateException, IndexOutOfBoundsException {
        Pattern p = Pattern.compile("<[^>]*>");
        Matcher m = p.matcher(stripTags);
        StringBuffer sb = new StringBuffer();
        if (!StringUtil.isNullOrEmpty(stripTags)) {
            while (m.find()) {
                m.appendReplacement(sb, "");
            }
            m.appendTail(sb);
            stripTags = sb.toString();
        }
        return stripTags.trim();
    }

    public static void calculateModAvg(Session session, HttpServletRequest request, int review) throws ServiceException {
        try {
            Date userdate = com.krawler.common.util.Timezone.toUserSystemTimezoneDate(session, request, dateFmt.format(new Date()), AuthHandler.getUserid(request));
            String query;
            if (review == 1) {
                query = "select distinct appm.appcycle.id from Appraisalmanagement appm where appm.reviewstatus=2 and appm.appcycle.reviewed=false and appm.appcycle.submitenddate<? and appm.appcycle.company.companyID=?";
            } else {
                query = "select distinct appm.appcycle.id from Appraisalmanagement appm where appm.appcycle.submitenddate<? and appm.appcycle.reviewed=false and appm.appcycle.company.companyID=?";
            }
            List recordTotalCount = HibernateUtil.executeQuery(session, query, new Object[]{userdate, AuthHandler.getCompanyid(request)});
            Iterator itr = recordTotalCount.iterator();
            while (itr.hasNext()) {
                Object app = (Object) itr.next();
                String query1;
                if (review == 1) {
                    query1 = "select distinct employee.userID from Appraisalmanagement appm where appm.reviewstatus=2 and appm.appcycle.reviewed=false and appm.appcycle.submitenddate<? and appm.appcycle.id=?";
                } else {
                    query1 = "select distinct employee.userID from Appraisalmanagement appm where appm.appcycle.submitenddate<? and appm.appcycle.reviewed=false and appm.appcycle.id=?";
                }
                List listCount1 = HibernateUtil.executeQuery(session, query1, new Object[]{userdate, app.toString()});
                Iterator itr1 = listCount1.iterator();
                while (itr1.hasNext()) {
                    Object usr = (Object) itr1.next();
                    String query2;
                    if (review == 1) {
                        query2 = "select appraisalid from Appraisalmanagement where appcycle.id=? and employee.userID = ? and reviewstatus=2 and appcycle.reviewed=false";
                    } else {
                        query2 = "select appraisalid from Appraisalmanagement where appcycle.id=? and employee.userID = ?";
                    }
                    List listCount2 = HibernateUtil.executeQuery(session, query2, new Object[]{app.toString(), usr.toString()});
                    if (listCount2.size() > 0) {
                        String hql = "select competency.mid from Appraisal where appraisal.appraisalid = ? and competency is not null";
                        List lst = HibernateUtil.executeQuery(session, hql, new Object[]{String.valueOf(listCount2.get(0))});
                        for (int i = 0; i < lst.size(); i++) {                        
                            ArrayList newArr=new ArrayList();
                            for (int j = 0; j < listCount2.size(); j++) {
                                hql = "select compmanrating from Appraisal where appraisal.appraisalid = ? and competency.mid=?";
                                List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{String.valueOf(listCount2.get(j)), String.valueOf(lst.get(i))});
                                if (wlst.size() > 0) {
                                    Double wt1;
                                    wt1 = (Double) wlst.get(0);
                                    if(wt1.intValue()!=0){
                                        newArr.add(wt1);
                                    }
                                }
                            }
                            double avgRat = 0;
                            if (hrmsManager.checkModule("modaverage", session, request)) {
                                Collections.sort(newArr);
                                if (newArr.size() > 2) {
                                    for (int c = 1; c < newArr.size() - 1; c++) {
                                        avgRat += (Double)newArr.get(c);
                                    }
                                    avgRat = avgRat / (newArr.size() - 2);
                                } else {
                                    for (int c = 0; c < newArr.size(); c++) {
                                        avgRat += (Double)newArr.get(c);
                                    }
                                    if(newArr.size()>0){
                                    avgRat = avgRat / newArr.size();
                                    }
                                }
                            } else {                                
                                for (int c = 0; c < newArr.size(); c++) {
                                    avgRat += (Double)newArr.get(c);
                                }
                                if(newArr.size()>0){
                                avgRat = avgRat / newArr.size();
                                }
                            }
                            hql = "select id from competencyAvg where appcycle.id = ? and employee.userID=? and competency.mid=?";
                            List wlst = HibernateUtil.executeQuery(session, hql, new Object[]{app.toString(), usr.toString(), String.valueOf(lst.get(i))});
                            if (wlst.isEmpty()) {
                                competencyAvg cmpavg = new competencyAvg();
                                cmpavg.setAppcycle((Appraisalcycle) session.load(Appraisalcycle.class, app.toString()));
                                cmpavg.setEmployee((User) session.load(User.class, usr.toString()));
                                cmpavg.setCompetency((Managecmpt) session.load(Managecmpt.class, String.valueOf(lst.get(i))));
                                cmpavg.setManavg(avgRat);
                                session.save(cmpavg);
                            } else if (wlst.size() > 0) {
                                competencyAvg cmpavg = (competencyAvg) session.get(competencyAvg.class, String.valueOf(wlst.get(0)));
                                cmpavg.setManavg(avgRat);
                                session.update(cmpavg);
                            }
                        }
                    }
                }
                Appraisalcycle appcycle = (Appraisalcycle) session.load(Appraisalcycle.class, app.toString());
                appcycle.setReviewed(true);
                session.update(appcycle);
            }
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("hrmsManager.calculateModAvg", ex);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
     public static JSONArray getCountry(Session session, HttpServletRequest request) throws ServiceException {
        JSONArray jarr = new JSONArray();       
        try {
            String hql = "from MasterData where masterid.id=? order by value ";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{11});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                MasterData mst = (MasterData) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", mst.getId());
                tmpObj.put("name", mst.getValue());
                jarr.put(tmpObj);
            }                                  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jarr;
    }
    public static String getConfigData(Session session, HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {

            String hql = "from ConfigType where formtype=? and company.companyID=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{request.getParameter("configType"), AuthHandler.getCompanyid(request)});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("fieldname", contyp.getName());
                tmpObj.put("configid", contyp.getConfigid());
                tmpObj.put("configtype", contyp.getConfigtype());
                String hql2 = "from ConfigData where referenceid=?";
                List lst2 = HibernateUtil.executeQuery(session, hql2, new Object[]{request.getParameter("refid")});
                Iterator ite2 = lst2.iterator();
                if (!ite2.hasNext()) {
                    tmpObj.put("configdata", "");
                } else {
                    JSONArray jarr2 = new JSONArray();
                    while (ite2.hasNext()) {
                        JSONObject jtemp = new JSONObject();
                        ConfigData condata = (ConfigData) ite2.next();
                        String configdata = condata.getCol(contyp.getColnum());
                        jarr2.put(configdata);
                    }
                    tmpObj.put("configdata", jarr2);
                }
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            result = jobj.toString();
    } catch (Exception e) {
            e.printStackTrace();
    } finally {
        
    }
    return result;
} 

}
