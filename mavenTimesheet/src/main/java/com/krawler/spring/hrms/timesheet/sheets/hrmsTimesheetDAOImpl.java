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
package com.krawler.spring.hrms.timesheet.sheets;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.update.Updates;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.timesheet.Timesheet;
import com.krawler.hrms.timesheet.TimesheetTimer;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.web.servlet.support.RequestContextUtils;
import javax.servlet.http.HttpServletRequest;

public class hrmsTimesheetDAOImpl implements hrmsTimesheetDAO,MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject AllTimesheets(HashMap<String, Object> requestParams ) throws ServiceException{
        boolean success = false;
        List lst = null;
        String ss = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        int count=0;
        Integer start,limit;
        try {
        	String userid = requestParams.get("userid").toString();
			Boolean isAdmin = (Boolean) requestParams.get("isAdmin");
            start=Integer.parseInt(requestParams.get("start").toString());
            limit=Integer.parseInt(requestParams.get("limit").toString());
            startdate= fmt.parse(requestParams.get("startdate").toString());
            enddate= fmt.parse(requestParams.get("enddate").toString());
            if(requestParams.get("ss")!=null){
                ss=requestParams.get("ss").toString();
            }
            ArrayList<Object> params = new ArrayList<Object>();
//            params.add(1);
            params.add(startdate);
            params.add(enddate);
            params.add(requestParams.get("Cid").toString());
            String hql =null;
            if(isAdmin)
            	hql = "select userID.userID,userID.firstName,approved,approvedby,userID.lastName,sum(worktime), sum(worktimemin) from Timesheet where (isSubmitted=1 or (isSubmitted=0 and approved=2)) and datevalue between ? and ? and  userID.company.companyID=?";
            else{
            	params.add(userid);
            	hql = "select userID.userID,userID.firstName,approved,approvedby,userID.lastName,sum(worktime), sum(worktimemin) from Timesheet where (isSubmitted=1 or (isSubmitted=0 and approved=2)) and datevalue between ? and ? and  userID.company.companyID=? and userID IN (select userID from Empprofile where reportto.userID = ?)";
            }
            if(!StringUtil.isNullOrEmpty(ss)){
                StringUtil.insertParamSearchString(params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"userID.firstName","userID.lastName"});
                hql +=searchQuery;
            }
            hql +=" group by userID.userID";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = lst.size();
            lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});
            success=true;
        } catch(Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public KwlReturnObject timesheetByUserID(HashMap<String, Object> requestParams) throws ServiceException {
        boolean success = false;
        List lst = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;

        try {
            startdate= fmt.parse(requestParams.get("startdate").toString());
            enddate= fmt.parse(requestParams.get("enddate").toString());
            ArrayList params = new ArrayList();
            params.add(startdate);
            params.add(enddate);
            params.add(requestParams.get("userid").toString());
            String hql = "from Timesheet where datevalue between ? and ? and userID.userID=? order by jobtype asc,datevalue asc ";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
        } catch(Exception e){
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, 0);
        }
    }

    public KwlReturnObject insertTimeSheet(HashMap<String, Object> requestParams) throws ServiceException {
        boolean success = false;
        List <Timesheet>lst =new ArrayList<Timesheet>();;
        int approved = 0;
        String apprmanager = "";
        String msg="";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
        	Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            String jsondata = requestParams.get("jsondata").toString();
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            String[] dateA = (String[]) requestParams.get("colHeader");
            Boolean isSubmitted = requestParams.get("isSubmitted").equals("true")?true:false;

            User usr = null;

            usr = (User) hibernateTemplate.load(User.class, requestParams.get("empid").toString());
            CompanyPreferences preferences = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, (Serializable) requestParams.get("companyid"));
            boolean isText = preferences.isTimesheetjob();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                for (int j = 1; j <= 7; j++) {
                    String[] time = jobj.getString("col" + j).split(":");
                    Date dateValue = fmt.parse(dateA[j - 1]);
                    String jobtype = jobj.getString("jobtype");
//                    if (approved == 0) {
                        if (jobj.getString("colid" + j).equals("undefined")) {

                            String id = UUID.randomUUID().toString();
                            Timesheet tmsht = new Timesheet();
                            tmsht.setId(id);
                            tmsht.setDatevalue(dateValue);
                            tmsht.setUserID(usr);
                            tmsht.setJobtype(jobtype);
                            tmsht.setWorktime(Integer.parseInt(time[0]));
                            tmsht.setWorktimemin(Integer.parseInt(time[1]));
                            tmsht.setApproved(0);
                            tmsht.setApprovedby(apprmanager);
                            tmsht.setIsSubmitted(isSubmitted?1:0);//1 to submit
                            tmsht.setText(isText);
                            hibernateTemplate.save(tmsht);
                            msg = (isSubmitted?messageSource.getMessage("hrms.timesheet.Timesheetsubmittedsuccessfully", null,"Timesheet submitted successfully.", locale):messageSource.getMessage("hrms.timesheet.Timesheetsavedsuccessfully", null,"Timesheet saved successfully.", locale));
                            lst.add(tmsht);
                            success = true;
                        } else {
                            Timesheet tmsht = (Timesheet) hibernateTemplate.load(Timesheet.class, jobj.getString("colid" + j));
                            approved = tmsht.getApproved();
                            if (tmsht.getApproved() == 0 || tmsht.getApproved() == 2) {//Not approved
                                tmsht.setDatevalue(dateValue);
                                tmsht.setUserID(usr);
                                tmsht.setJobtype(jobtype);
                                tmsht.setWorktime(Integer.parseInt(time[0]));
                                tmsht.setWorktimemin(Integer.parseInt(time[1]));
                                tmsht.setApproved(0);
                                tmsht.setApprovedby(apprmanager);
                                tmsht.setIsSubmitted(isSubmitted?1:0);//1 to submit
                                tmsht.setText(isText);
                                hibernateTemplate.save(tmsht);
                                msg = msg = (isSubmitted?messageSource.getMessage("hrms.timesheet.Timesheetsubmittedsuccessfully", null,"Timesheet submitted successfully.", locale):messageSource.getMessage("hrms.timesheet.Timesheetsavedsuccessfully", null,"Timesheet saved successfully.", locale));
                                lst.add(tmsht);
                            } else {
                                msg = messageSource.getMessage("hrms.timesheet.Timesheetisalreadyapproved", null,"Timesheet is already approved.", locale);
                            }
                            success = true;
                        }
//                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
            msg="Failed";
        } finally {
            return new KwlReturnObject(success, msg, "-1", lst, 0);
        }
    }

    public KwlReturnObject deletetimesheetjobs(HashMap<String, Object> requestParams) throws ServiceException {
        boolean success = false;
        String msg="";
        try {
            String[] ids = (String[]) requestParams.get("ids");
            for (int i = 0; i < ids.length; i++) {
                Timesheet tmst = (Timesheet) hibernateTemplate.load(Timesheet.class, ids[i]);
                if (tmst.getApproved() == 1) {
                    msg="Approved Timesheet can not be deleted.";
                    success=true;
                    break;
                } else {
                    hibernateTemplate.delete(tmst);
                    msg="Timesheet is successfully deleted.";
                    success=true;
                }
            }

        } catch (Exception e) {
            success=false;
            msg="Failed";
        } finally {
            return new KwlReturnObject(success, msg, "-1", null, 0);
        }
    }

    public KwlReturnObject AlltimesheetsApproval(HashMap<String, Object> requestParams) throws ServiceException {
        boolean success = false;
        String msg="";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        
        try {
            startdate = (Date) fmt.parse(requestParams.get("startdate").toString());
            enddate = (Date) fmt.parse(requestParams.get("enddate").toString());
            Object[] obj = {startdate, enddate};

            String[] ids = (String[]) requestParams.get("ids");

            String str = "";
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    str += ",";
                }
                str += "'" + ids[i] + "'";
            }

            String query = "from Timesheet where userID.userID in(" + str + ") and datevalue between ? and ?";

            List lst = HibernateUtil.executeQuery(hibernateTemplate, query, obj);
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Timesheet tmsht = (Timesheet) ite.next();
                if(requestParams.containsKey("submitstatus") && requestParams.get("submitstatus")!=null) {
                    tmsht.setIsSubmitted(Integer.parseInt(requestParams.get("submitstatus").toString()));
                }
                if(requestParams.containsKey("approvestatus") && requestParams.get("approvestatus")!=null) {
                    tmsht.setApproved(Integer.parseInt(requestParams.get("approvestatus").toString()));
                }
                tmsht.setApprovedby(requestParams.get("managername").toString());
                hibernateTemplate.save(tmsht);

//                ProfileHandler.insertAuditLog(session, AuditAction.TIME_SHEET_APPROVED, "Timesheet of "+AuthHandler.getFullName(tmsht.getUserID())+" from "+startdate+" to "+enddate+" approved by "+AuthHandler.getFullName(session, AuthHandler.getUserid(request)), request);
            }
            success=true;
            msg="true";
        } catch (Exception e){
            success=false;
            msg="false";
        } finally {
            return new KwlReturnObject(success, msg, "-1", null, 0);
        }
    }
    
    /**
     * Function to get approve or reject timesheet notification.
     * @param requestParams
     * @return 
     */
    public KwlReturnObject timesheetsApprovalUpdates(HashMap<String, Object> requestParams){
    	Boolean success = false;
    	Calendar calendar = Calendar.getInstance();
    	Date jobtypestartdate;
    	Date jobtypeenddate;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	List <Updates> list = new ArrayList<Updates>();
        try{
		        	Locale locale = null;
		            if(requestParams.get("locale")!=null){
		            	locale = (Locale) requestParams.get("locale");
		            }
        			DateFormat df = (DateFormat) requestParams.get("df");
                    String userid = requestParams.get("userid").toString();
                    Date userdate = (Date)(requestParams.get("userdate"));
                    String companyid = requestParams.get("companyid").toString();
                    int status = Integer.parseInt(requestParams.get("status").toString());
                    String query = "from Timesheet where userID.userID=? and userID.company.companyID=? and approved=? and datevalue<= ? order by datevalue desc";
                    List<Timesheet> recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid,companyid,status,userdate});
                    Iterator<Timesheet> itr = recordTotalCount.iterator();
                    if(itr.hasNext()) {
                        String updateDiv = "";
                        Timesheet timesheet = itr.next();
                        calendar.setTime(timesheet.getDatevalue());
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        calendar.add(calendar.DAY_OF_MONTH, -(calendar.get(calendar.DAY_OF_WEEK)-1)); 
                        jobtypestartdate = calendar.getTime();
                        calendar.add(Calendar.DAY_OF_MONTH, 6);
                        jobtypeenddate = calendar.getTime();
                        if(timesheet.getApproved() == 1) {//Approve
                            updateDiv += messageSource.getMessage("hrms.timesheet.timesheet.for.duration", null, locale)+" <a href='#' onclick='timesheet(\""+sdf.format(jobtypestartdate)+"\")'> "+df.format(jobtypestartdate) +"</a> "+messageSource.getMessage("hrms.common.to.small", null, locale)+" <a href='#' onclick='timesheet(\""+sdf.format(jobtypestartdate)+"\")'> "+df.format(jobtypeenddate)+"</a> "+messageSource.getMessage("hrms.common.is.approved.by", null, locale)+" <font color='green'>" + timesheet.getApprovedby()+"</font>.";
                        } else if(timesheet.getApproved() == 2) {//Reject
                            updateDiv += messageSource.getMessage("hrms.timesheet.timesheet.for.duration", null, locale)+" <a href='#' onclick='timesheet(\""+sdf.format(jobtypestartdate)+"\")'> "+df.format(jobtypestartdate) +"</a> "+messageSource.getMessage("hrms.common.to.small", null, locale)+" <a href='#' onclick='timesheet(\""+sdf.format(jobtypestartdate)+"\")'> "+df.format(jobtypeenddate)+"</a> "+messageSource.getMessage("hrms.common.is.rejected.by", null, locale)+" <font color='green'>" + timesheet.getApprovedby()+"</font>.";
                        }
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), jobtypestartdate));
                    }
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }
    
    public KwlReturnObject timesheetsSubmittedUpdates(HashMap<String, Object> requestParams){
    	Boolean success = false;
    	Calendar calendar = Calendar.getInstance();
    	Date jobtypestartdate;
    	Date jobtypeenddate;
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
    	List <Updates> list = new ArrayList<Updates>();
        try{
		        	Locale locale = null;
		            if(requestParams.get("locale")!=null){
		            	locale = (Locale) requestParams.get("locale");
		            }

        			String userid = requestParams.get("userid").toString();
        			Boolean isAdmin = (Boolean) requestParams.get("isAdmin");
        			Date userdate = (Date) requestParams.get("userdate");
        			DateFormat df = (DateFormat) requestParams.get("df");
                    String companyid = requestParams.get("companyid").toString();
                    calendar.setTime(userdate);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DAY_OF_WEEK)-1)); 
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    jobtypestartdate = calendar.getTime();
                    calendar.add(Calendar.DAY_OF_MONTH, 6);
                    jobtypeenddate = calendar.getTime();
                    String query="";
                    List<Timesheet> recordTotalCount;
                    if(isAdmin){
                    	query = "from Timesheet where userID.company.companyID=? and isSubmitted = ? and approved= ? and datevalue between ? and ? group by userID.userID";
                    	recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, 1, 0, jobtypestartdate, jobtypeenddate});
                    }
                    else{
                    	query = "from Timesheet where userID.company.companyID=? and userID IN (select userID from Empprofile where reportto.userID = ?) and isSubmitted = ? and approved = ? and datevalue between ? and ? group by userID.userID";
                    	recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, userid, 1, 0, jobtypestartdate, jobtypeenddate});
                    }
                    
                    Iterator<Timesheet> itr = recordTotalCount.iterator();
                    while(itr.hasNext()) {
                        String updateDiv = "";
                        Timesheet timesheet = itr.next();
                        updateDiv +=messageSource.getMessage("hrms.common.Alert.timesheetsSubmittedUpdates",new Object[]{"<a href='#' onclick='viewtimesheet(\""+sdf.format(jobtypestartdate)+"\")'> "+df.format(jobtypestartdate) +"</a>" ,"<a href='#' onclick='viewtimesheet(\""+sdf.format(jobtypestartdate)+"\")'>"+df.format(jobtypeenddate)+"</a> ","<font color='green'>" + timesheet.getUserID().getFirstName()+" "+timesheet.getUserID().getLastName()+"</font>"},"Timesheet for duration <a href='#' onclick='viewtimesheet(\""+sdf.format(jobtypestartdate)+"\")'> "+df.format(jobtypestartdate) +"</a> to <a href='#' onclick='viewtimesheet(\""+sdf.format(jobtypestartdate)+"\")'>"+df.format(jobtypeenddate)+"</a> is submmitted by <font color='green'>" + timesheet.getUserID().getFirstName()+" "+timesheet.getUserID().getLastName()+"</font>.", locale);
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), jobtypestartdate));
                    }
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }


	@Override
	public TimesheetTimer setTimer(HashMap<String, Object> requestParams) {
		TimesheetTimer timer = null;
		try {
			
			if(requestParams.get("userid")!=null){
				List<TimesheetTimer> list = HibernateUtil.executeQuery(hibernateTemplate, "from TimesheetTimer where user.userID = ? ", new Object[]{requestParams.get("userid").toString()});
				for(TimesheetTimer obj: list){
					timer = obj;
				}
			}
			
			if(timer==null){
				timer = new TimesheetTimer();
			}
			
			if(requestParams.get("userid")!=null){
				timer.setUser((User) hibernateTemplate.load(User.class, requestParams.get("userid").toString()));
			}
			
			if(requestParams.get("startdate")!=null){
				timer.setStartTime((Date) requestParams.get("startdate"));
			}
			
			if(requestParams.get("stopdate")!=null){
				timer.setStopTime((Date) requestParams.get("stopdate"));
			}else{
				timer.setStopTime(null);
			}
			
			if(requestParams.get("name")!=null){
				timer.setJobname(requestParams.get("name").toString());
			}
			hibernateTemplate.saveOrUpdate(timer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timer;
	}

	@Override
	public TimesheetTimer getTimer(String userid) {
		TimesheetTimer timer = null;
		try {
			List<TimesheetTimer> list = HibernateUtil.executeQuery(hibernateTemplate, "from TimesheetTimer where user.userID = ? ", new Object[]{userid});
			for(TimesheetTimer obj: list){
				timer = obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timer;
	}

	@Override
	public List<Timesheet> getTimesheet(String userid, Date startdate, Date enddate, String jobname) {
		List<Timesheet> list = null;
		try {
			list = HibernateUtil.executeQuery(hibernateTemplate, "from Timesheet where userID.userID = ? and datevalue >= ? and datevalue <= ? and jobtype = ?", new Object[]{userid, startdate, enddate, jobname});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List <Updates> timerUpdates(TimesheetTimer timer, SimpleDateFormat sdf, Locale locale){
    	List <Updates> list = new ArrayList<Updates>();
    	try{
        	if(timer!=null && timer.getStopTime()==null){
        		CompanyPreferences preferences = (CompanyPreferences) hibernateTemplate.load(CompanyPreferences.class, timer.getUser().getCompany().getCompanyID());
        		if(preferences!=null){
        			if(preferences.isTimesheetjob()){
        				list.add(new Updates(messageSource.getMessage("hrms.timesheet.timer.for.job", null, locale)+" <font color='green'>"+timer.getJobname()+"</font> "+messageSource.getMessage("hrms.timesheet.is.started.at", null, locale)+" "+sdf.format(timer.getStartTime())+".", timer.getStartTime()));
        			}else{
		        		MasterData md = (MasterData) hibernateTemplate.load(MasterData.class, timer.getJobname());
		        		if(md!=null){
		        			list.add(new Updates(messageSource.getMessage("hrms.timesheet.timer.for.job", null, locale)+" <font color='green'>"+md.getValue()+"</font> "+messageSource.getMessage("hrms.timesheet.is.started.at", null, locale)+" "+sdf.format(timer.getStartTime())+".", timer.getStartTime()));
		        		}
        			}
        		}
        	}
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }

	@Override
	public boolean saveTimesheet(Timesheet timesheet) {
		boolean success = true;
		try {
			hibernateTemplate.saveOrUpdate(timesheet);
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public boolean deleteTimesheet(TimesheetTimer timer) {
		boolean success = true;
		try {
			hibernateTemplate.delete(timer);
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
}
