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
package com.krawler.spring.hrms.goal;

import com.krawler.common.admin.User;
import com.krawler.common.update.Updates;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
//import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.GoalComments;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class hrmsGoalDAOImpl implements hrmsGoalDAO,MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject Employeesgoalfinal(HashMap<String, Object> requestParams) {
        boolean success = false;
        ArrayList params = new ArrayList();
        List lst = null;
        int count = 0;
        Integer start, limit;
        try {
            String hql = "";
            String ss="";
            if(requestParams.get("ss")!=null){
                ss = requestParams.get("ss").toString();
            }
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
            params.add(requestParams.get("empid").toString());
            params.add(requestParams.get("Cid").toString());
            params.add(false);
            if (requestParams.get("user").toString().equals("user")) {
                hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? ";
            } else {
                hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? and manager.userID=?";
                params.add(requestParams.get("managerid").toString());
            }
            if (!StringUtil.isNullOrEmpty(ss)) {
                StringUtil.insertParamSearchString(params, ss, 1);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"goalname"});
                hql += searchQuery;
            }
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = lst.size();
            lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public KwlReturnObject getGoalCommentsWithID(HashMap<String, Object> requestParams){
        boolean success=false;
        List lst = null;
        int count=0;
        try{
            String gid = requestParams.get("goalid").toString();
            String hql = "from GoalComments where goalid.id=? order by createdon desc";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, gid);
            count = lst.size();
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public KwlReturnObject addCommentsfunction(HashMap<String, Object> requestParams) {
        boolean success=false;
        try {
            String goalids[] = (String[]) requestParams.get("goalid");
            Date dt=new Date();
            for (int i = 0; i < goalids.length; i++) {
            User user = (User) hibernateTemplate.get(User.class,requestParams.get("userid").toString());
                Finalgoalmanagement fgid = (Finalgoalmanagement) hibernateTemplate.get(Finalgoalmanagement.class,goalids[i]);
                GoalComments gcms = new GoalComments();
                gcms.setComment(requestParams.get("comment").toString());
                gcms.setGoalid(fgid);
                gcms.setCreatedon(dt);
            gcms.setUserid(user);
                hibernateTemplate.save(gcms);
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    public KwlReturnObject getFinalgoalmanagement(HashMap<String,Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from Finalgoalmanagement ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

public KwlReturnObject insertGoal(HashMap<String, Object> requestParams) {
        boolean success=false;
        DateFormat sdf;
        String gname="";
        List<Finalgoalmanagement> tabledata = new ArrayList<Finalgoalmanagement>();
        try {
            String apprmanager = "";
            Finalgoalmanagement fgmt = null;
            int logtext=0;
            if (StringUtil.isNullOrEmpty(requestParams.get("archive").toString())) {
                apprmanager = requestParams.get("apprmanager").toString();
                User usr = (User) hibernateTemplate.load(User.class, requestParams.get("empid").toString());
                String jsondata = requestParams.get("jsondata").toString();
                sdf=(DateFormat)requestParams.get("dateformatter");
                JSONArray jarr = new JSONArray(jsondata);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
                    String id = UUID.randomUUID().toString();
                    Date startdate = new Date(jobj.getLong("gstartdate"));
                     Date enddate = new Date(jobj.getLong("genddate"));
                    if (!jobj.has("gid") || jobj.getString("gid").equals("undefined")) {
                        fgmt = new Finalgoalmanagement();
                        fgmt.setId(id);
                        fgmt.setCreatedon(new Date());
                        fgmt.setInternal(true);
                        logtext=0;
                    } else {
                        fgmt = (Finalgoalmanagement) hibernateTemplate.load(Finalgoalmanagement.class, jobj.getString("gid"));
                        fgmt.setUpdatedon(new Date());
                        logtext=1;
                    }
                    User manager = (User) hibernateTemplate.load(User.class,requestParams.get("managerid").toString());
                    fgmt.setContext(jobj.getString("gcontext"));
                    fgmt.setAssignedby(apprmanager);
                    fgmt.setManager(manager);
                    fgmt.setGoaldesc(jobj.getString("gdescription"));
                    fgmt.setGoalwth(Integer.parseInt(jobj.getString("gwth")));
                    fgmt.setPercentcomplete(Integer.parseInt(jobj.getString("gcomplete")));
                    fgmt.setGoalname(jobj.getString("gname"));
                    fgmt.setStartdate(startdate);
                    fgmt.setEnddate(enddate);
                    fgmt.setPriority(jobj.getString("gpriority"));
                    fgmt.setcomment(jobj.getString("gcomment"));
                    fgmt.setUserID(usr);
                    fgmt.setUpdatedBy(manager);
                    fgmt.setCompleted(true);
                    fgmt.setArchivedflag(0);
                    hibernateTemplate.save(fgmt);
                    tabledata.add(fgmt);
                    gname+=jobj.getString("gname");
                    if(jarr.length()>i+1)
                    	gname+=" ,";
                    if(logtext==0) {
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has assigned new goal " + fgmt.getGoalname()+  " to " + AuthHandler.getFullName(fgmt.getUserID()),request);
                    } else {
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has updated goal " + fgmt.getGoalname()+  " for " + AuthHandler.getFullName(fgmt.getUserID()),request);
                    }
                    success=true;
                }	
            } else {
                String isArchive = requestParams.get("archive").toString();
                String archiveids[] = (String[]) requestParams.get("archiveid");
                for (int i = 0; i < archiveids.length; i++) {
                    String hql = "from Finalgoalmanagement where id=?";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, archiveids[i]);
                    if (!tabledata.isEmpty()) {
                        fgmt = (Finalgoalmanagement) tabledata.get(0);
                        if(isArchive.equals("true"))
                            fgmt.setArchivedflag(1);
                        else
                            fgmt.setArchivedflag(0);
                        hibernateTemplate.update(fgmt);
                        //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_ARCHIVED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has archived " + AuthHandler.getFullName(fgmt.getUserID()) + "'s goal " + fgmt.getGoalname(),request);
                    }
                }
                success=true;
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, gname, "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject changeMyGoalPercent(HashMap<String, Object> requestParams) {
        boolean success=false;
        try {
            Finalgoalmanagement fgmt = null;
            String jsondata = requestParams.get("jsondata").toString();
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                fgmt = (Finalgoalmanagement) hibernateTemplate.load(Finalgoalmanagement.class, jobj.getString("gid"));
                fgmt.setPercentcomplete(Integer.parseInt(jobj.getString("gcomplete")));
                fgmt.setUpdatedon(new Date());
                User a = (User) hibernateTemplate.load(User.class,requestParams.get("updatedBy").toString());
                fgmt.setUpdatedBy(a);
                hibernateTemplate.update(fgmt);
            }
            success=true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    public KwlReturnObject assignedgoalsdelete(HashMap<String, Object> requestParams) {
        boolean success=false;
        List <String[]> lst = new ArrayList<String[]>();
        try {
            String[] ids = (String[]) requestParams.get("ids");

            for (int i = 0; i < ids.length; i++) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) hibernateTemplate.load(Finalgoalmanagement.class, ids[i]);
                fgmt.setDeleted(true);
//                String logtext="User  " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted " + AuthHandler.getFullName(fgmt.getUserID()) + "'s goal " + fgmt.getGoalname();
                String[] temp={fgmt.getUserID().getUserID(),fgmt.getGoalname()};
                lst.add(temp);
                hibernateTemplate.saveOrUpdate(fgmt);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.GOAL_DELETED, logtext ,request);
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, lst.size());
        }

    }

    public KwlReturnObject archivedgoalsfunction(HashMap<String, Object> requestParams) {
        boolean success=false;
        ArrayList params = new ArrayList();
        Integer start, limit;
        int count=0;
        List lst=null;
        try {
            String ss="";
            if(requestParams.get("ss")!=null){
                ss = requestParams.get("ss").toString();
            }
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());

            String hql = "from Finalgoalmanagement where archivedflag=1 and userID.company.companyID=? and deleted=?";
            params.add(requestParams.get("cmp").toString());
            params.add(false);
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"userID.firstName","userID.lastName"});
                hql +=searchQuery;
            }
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql,params.toArray());
            count = lst.size();
            lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql,params.toArray(), new Integer[]{start, limit});
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }
   public KwlReturnObject getGoalsforAppraisal(HashMap<String, Object> requestParams){
        boolean success=false;
        List lst = null;
        int count=0;
        try{
            String empid = requestParams.get("empid").toString();
            String companyid = requestParams.get("companyid").toString();
            String managerid = requestParams.get("managerid").toString();
            Date sdate = (Date) requestParams.get("sdate");
            Date edate = (Date) requestParams.get("edate");
            String hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? and manager.userID=? and (startdate between ? and ?) and (enddate between ? and ?)";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{empid, companyid,false,managerid, sdate, edate, sdate, edate});
            count = lst.size();
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

     public KwlReturnObject getgoalassignedupdate(HashMap<String,Object> requestParams){
        Boolean success = false;
        DateFormat df = (DateFormat) requestParams.get("df");
        Date userdate = (Date) requestParams.get("userdate");
        String userid = requestParams.get("userid").toString();
        String companyid = requestParams.get("companyid").toString();
        List <Updates> list = new ArrayList<Updates>();
        try{
                         String query = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? order by createdon desc";
                         List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid,companyid, false});
//                        filter_names.add("userID.userID");
//                        filter_values.add(AuthHandler.getUserid(request));
//                        filter_names.add("userID.company.companyID");
//                        filter_values.add(AuthHandler.getCompanyid(request));
//                        filter_names.add("archivedflag");
//                        filter_values.add(0);
//                        filter_names.add("deleted");
//                        filter_values.add(false);
//
//                        ArrayList order_by = new ArrayList(),order_type = new ArrayList();
//                        order_by.add("createdon");
//                        order_type.add("desc");
//
//                        requestParams.put("filter_names", filter_names);
//                        requestParams.put("filter_values", filter_values);
//                        requestParams.put("order_by", order_by);
//                        requestParams.put("order_type", order_type);
//                        result = hrmsGoalDAOObj.getFinalgoalmanagement(requestParams);
//                        List recordTotalCount = result.getEntityList();
	                     Locale locale = null;
	                     if(requestParams.get("locale")!=null){
	                     	locale = (Locale) requestParams.get("locale");
	                     }
                        Iterator itr = recordTotalCount.iterator();
                        String goaldate = "";
                        while (itr.hasNext()) {
                            String updateDiv = "";
                            JSONObject obj = new JSONObject();
                            Finalgoalmanagement fgmt = (Finalgoalmanagement) itr.next();
                            if (fgmt.getCreatedon() != null) {
//                                goaldate = kwlCommonTablesDAOObj.getUserDateFormatter(sessionHandlerImplObj.getDateFormatID(request),sessionHandlerImplObj.getUserTimeFormat(request),sessionHandlerImplObj.getTimeZoneDifference(request)).format(fgmt.getCreatedon());
                                goaldate = df.format(fgmt.getCreatedon());
                            } else {
                                goaldate = userdate.toString();
                            }
                            updateDiv += messageSource.getMessage("hrms.common.Updates.getgoalassignedupdate",new Object[]{"<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a>","<font color='green'> " + StringUtil.getFullName(fgmt.getManager()) + "</font>","<font color='green'>" + goaldate + "</font>"}, "<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a> goal assigned by<font color='green'> " + StringUtil.getFullName(fgmt.getManager()) + "</font> on <font color='green'>" + goaldate + "</font>", locale);
                            list.add(new Updates(StringUtil.getContentSpan(updateDiv), fgmt.getEnddate()));
                        }
                        success = true;
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }
    
    public KwlReturnObject getGoalEditUpdates(HashMap<String,Object> requestParams){
    	Boolean success = false;
        Calendar calendar = Calendar.getInstance();
    	List <Updates> list = new ArrayList<Updates>();
        try{
        	String userid = requestParams.get("userid").toString();
        	Date startDate = (Date) requestParams.get("userdate");
            String companyid = requestParams.get("companyid").toString();
            DateFormat df = (DateFormat) requestParams.get("df");
            calendar.setTime(startDate);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        	calendar.add(Calendar.DAY_OF_MONTH, 7);//Up to 7 day's from current date
        	Date endDate = calendar.getTime();
        	Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            
        	String query = "from Finalgoalmanagement where (userID.userID=? or manager.userID=?) and userID.company.companyID=? and deleted=? and updatedon is not null and updatedon between ? and ? order by updatedon desc";
            List<Finalgoalmanagement> recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid,userid,companyid, false, startDate, endDate});
            Iterator<Finalgoalmanagement> itr = recordTotalCount.iterator();
            while (itr.hasNext()) {
            	String updateDiv = "";
            	Finalgoalmanagement fgmt = (Finalgoalmanagement) itr.next();
            	String updatedBy = fgmt.getUpdatedBy()!=null?(fgmt.getUpdatedBy().getFirstName()+" "+(fgmt.getUpdatedBy().getLastName()!=null?fgmt.getUpdatedBy().getLastName():"")):"";
            	if(userid.equals(fgmt.getUserID().getUserID())){
            		if(!fgmt.getUpdatedBy().getUserID().equals(fgmt.getUserID().getUserID())){
            			updateDiv +=messageSource.getMessage("hrms.common.Updates.getGoalEditUpdates1",new Object[]{"<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a>","<font color='green'> " + updatedBy +"</font>","<font color='green'>" + df.format(fgmt.getUpdatedon()) + "</font>"},"<a href='#' onclick='myGoals()'>" + fgmt.getGoalname() + "</a> goal updated by<font color='green'> " + updatedBy +"</font> on <font color='green'>" + df.format(fgmt.getUpdatedon()) + "</font>", locale);
                		list.add(new Updates(StringUtil.getContentSpan(updateDiv), fgmt.getUpdatedon()));
            		}
            	}else{
            		if(!fgmt.getUpdatedBy().getUserID().equals(fgmt.getManager().getUserID())){
            			updateDiv += messageSource.getMessage("hrms.common.Updates.getGoalEditUpdates1",new Object[]{"<a href='#' onclick='viewEmpGoals(\""+fgmt.getUserID().getUserID()+"\",\""+fgmt.getUserID().getFirstName()+" "+fgmt.getUserID().getLastName()+"\")'>" + fgmt.getGoalname() + "</a>","<font color='green'> " + updatedBy +"</font>","<font color='green'>" + df.format(fgmt.getUpdatedon()) + "</font>"},  "<a href='#' onclick='viewEmpGoals(\""+fgmt.getUserID().getUserID()+"\",\""+fgmt.getUserID().getFirstName()+" "+fgmt.getUserID().getLastName()+"\")'>" + fgmt.getGoalname() + "</a> goal updated by<font color='green'> " + updatedBy +"</font> on <font color='green'>" + df.format(fgmt.getUpdatedon()) + "</font>", locale);
                		list.add(new Updates(StringUtil.getContentSpan(updateDiv), fgmt.getUpdatedon()));
            		}
            	}
           }
           success = true;
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

  /* public KwlReturnObject updateappraisalGoalsFromCrm(HashMap<String, Object> requestParams) {
        
        HashMap GoalRate =null;
        List <HashMap> list = new ArrayList<HashMap>();
        Boolean success = false;
        try {

            JSONArray data = null;

//            String crmURL = this.getServletContext().getInitParameter("crmURL");

            String companyid = requestParams.get("companyid").toString();
//            if (StringUtil.isNullOrEmpty(request.getParameter("isemployee"))) {
//                uid = request.getParameter("empid");
//            } else {
//                uid = sessionHandlerImplObj.getUserid(request);
//            }

            String crmURL = requestParams.get("crmURL").toString();
            if (!StringUtil.isNullOrEmpty(crmURL)) {
                JSONObject userData = new JSONObject();
                userData.put("companyid", companyid);
                userData.put("userid", requestParams.get("userid").toString());
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
            list.add(GoalRate);
            success=true;
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
        }
//        return GoalRate;
        return new KwlReturnObject(success, "", "", list, list.size());
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
    }*/

    @Override
    @SuppressWarnings({ "unchecked", "finally" })
	public List<Finalgoalmanagement> getGoals(HashMap<String, Object> requestParams){
    	List<Finalgoalmanagement> finalgoalmanagements = null;
 		try{
 			Object userID = requestParams.get("userID");
 			Object companyid = requestParams.get("companyid");
 			Object archivedflag = requestParams.get("archivedflag");
 			Object managerid = requestParams.get("managerid");
 			Object sdate = requestParams.get("sdate");
 			Object edate = requestParams.get("edate");
 			String hql = "from Finalgoalmanagement where userID.userID=? and userID.company.companyID=? and archivedflag=0 and deleted=? and manager.userID=? and (startdate between ? and ?) and (enddate between ? and ?)";
 			finalgoalmanagements = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userID, companyid, archivedflag, managerid, sdate, edate, sdate, edate});
 		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return finalgoalmanagements;
		}
    }
     @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
