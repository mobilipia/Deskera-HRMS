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
package com.krawler.spring.profileHandler;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.APICallHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Karthik
 */
public class profileHandlerDAOImpl implements profileHandlerDAO,MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public String getUserFullName(String userid) throws ServiceException {
        String name = "";
        List ll = new ArrayList();
        try {
            String SELECT_USER_INFO = "select u.firstName, u.lastName from User as u " +
                    "where u.userID = ?  and u.deleteflag=0 ";
            ll = HibernateUtil.executeQuery(hibernateTemplate, SELECT_USER_INFO, new Object[]{userid});
            name = profileHandler.getUserFullName(ll);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getUserFullName", e);
        }
        return name;
    }
    public KwlReturnObject getUseBookmarks(String userid) throws ServiceException {
        List ll = new ArrayList();
        try {
            String SELECT_USER_INFO = " select bookmarkid,linkname,linkurl from crm_bookmarks c where userid=?  ";
            ll = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), SELECT_USER_INFO,new Object[]{userid});
           
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getUseBookmarks", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, ll.size());
    }
    public KwlReturnObject getUserDetails(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        int start = 0;
        int limit = 0;
        String serverSearch = "";
        try {
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
            }
            if (requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if (requestParams.containsKey("ss") && !StringUtil.isNullOrEmpty(requestParams.get("ss").toString())) {
                serverSearch = requestParams.get("ss").toString();
            }
            String SELECT_USER_INFO = "from User u ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            SELECT_USER_INFO += filterQuery;

            if (!StringUtil.isNullOrEmpty(serverSearch)) {
                SELECT_USER_INFO += " and (u.firstName like '%" + serverSearch + "%'";
                SELECT_USER_INFO += " or u.lastName like '%" + serverSearch + "%')";
            }
            ll = HibernateUtil.executeQuery(hibernateTemplate, SELECT_USER_INFO, filter_params.toArray());
            dl = ll.size();
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                ll = HibernateUtil.executeQueryPaging(hibernateTemplate, SELECT_USER_INFO, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllUserDetails", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAllManagers(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String role = " and ( bitwise_and( roleID , 2 ) = 2 ) ";
            String SELECT_USER_INFO = "from User u where company.companyID=?  and deleteflag=0 " + role;
            ll = HibernateUtil.executeQuery(hibernateTemplate, SELECT_USER_INFO, new Object[]{companyid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllManagers", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public void saveUser(HashMap<String, Object> requestParams) throws ServiceException {
        String id = "";
        String dateid = "";
        User user = null;
        try {
            if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                id = requestParams.get("userid").toString();
                user = (User) hibernateTemplate.load(User.class, id);
            } else {
                user = new User();
            }
            if (requestParams.containsKey("dateformat") && requestParams.get("dateformat") != null) {
                dateid = requestParams.get("dateformat").toString();
                user.setDateFormat((KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, dateid));
            }
            if (requestParams.containsKey("userlogin") && requestParams.get("userlogin") != null) {
                String userLoginId = requestParams.get("userlogin").toString();
                user.setUserLogin((UserLogin) hibernateTemplate.load(UserLogin.class, userLoginId));
            }
            if (requestParams.containsKey("image") && requestParams.get("image") != null) {
                String image = requestParams.get("image").toString();
                user.setImage(image);
            }
            if (requestParams.containsKey("firstName") && requestParams.get("firstName") != null) {
                String firstName = requestParams.get("firstName").toString();
                user.setFirstName(firstName);
            }
            if (requestParams.containsKey("lastName") && requestParams.get("lastName") != null) {
                String lastName = requestParams.get("lastName").toString();
                user.setLastName(lastName);
            }
            if (requestParams.containsKey("role") && requestParams.get("role") != null) {
                int role = Integer.parseInt(requestParams.get("role").toString());
                user.setRoleID(role);
            }
            if (requestParams.containsKey("emailID") && requestParams.get("emailID") != null) {
                String emailID = requestParams.get("emailID").toString();
                user.setEmailID(emailID);
            }
            if (requestParams.containsKey("address") && requestParams.get("address") != null) {
                String address = requestParams.get("address").toString();
                user.setAddress(address);
            }
            if (requestParams.containsKey("designation") && requestParams.get("designation") != null) {
                String designation = requestParams.get("designation").toString();
                user.setDesignation(designation);
            }
            if (requestParams.containsKey("contactNumber") && requestParams.get("contactNumber") != null) {
                String contactNumber = requestParams.get("contactNumber").toString();
                user.setContactNumber(contactNumber);
            }
            if (requestParams.containsKey("aboutUser") && requestParams.get("aboutUser") != null) {
                String aboutUser = requestParams.get("aboutUser").toString();
                user.setAboutUser(aboutUser);
            }
            if (requestParams.containsKey("userStatus") && requestParams.get("userStatus") != null) {
                String userStatus = requestParams.get("userStatus").toString();
                user.setUserStatus(userStatus);
            }
            if (requestParams.containsKey("timeZone") && requestParams.get("timeZone") != null) {
                String timeZone = requestParams.get("timeZone").toString();
                user.setTimeZone((KWLTimeZone) hibernateTemplate.load(KWLTimeZone.class, timeZone));
            }
            if (requestParams.containsKey("company") && requestParams.get("company") != null) {
                String company = requestParams.get("company").toString();
                user.setCompany((Company) hibernateTemplate.load(Company.class, company));
            }
            if (requestParams.containsKey("fax") && requestParams.get("fax") != null) {
                String fax = requestParams.get("fax").toString();
                user.setFax(fax);
            }
            if (requestParams.containsKey("alternateContactNumber") && requestParams.get("alternateContactNumber") != null) {
                String alternateContactNumber = requestParams.get("alternateContactNumber").toString();
                user.setAlternateContactNumber(alternateContactNumber);
            }
            if (requestParams.containsKey("phpBBID") && requestParams.get("phpBBID") != null) {
                int phpBBID = Integer.parseInt(requestParams.get("phpBBID").toString());
                user.setPhpBBID(phpBBID);
            }
            if (requestParams.containsKey("panNumber") && requestParams.get("panNumber") != null) {
                String panNumber = requestParams.get("panNumber").toString();
                user.setPanNumber(panNumber);
            }
            if (requestParams.containsKey("ssnNumber") && requestParams.get("ssnNumber") != null) {
                String ssnNumber = requestParams.get("ssnNumber").toString();
                user.setSsnNumber(ssnNumber);
            }
            if (requestParams.containsKey("dateFormat") && requestParams.get("dateFormat") != null) {
                String dateFormat = requestParams.get("dateFormat").toString();
                user.setDateFormat((KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, dateFormat));
            }
            if (requestParams.containsKey("timeformat") && requestParams.get("timeformat") != null) {
                int timeformat = Integer.parseInt(requestParams.get("timeformat").toString());
                user.setTimeformat(timeformat);
            }
            if (requestParams.containsKey("createdon") && requestParams.get("createdon") != null) {
                Date created = (Date) requestParams.get("createdon");
                user.setCreatedon(created);
            }
            if (requestParams.containsKey("updatedon") && requestParams.get("updatedon") != null) {
                Date updatedon = (Date) requestParams.get("updatedon");
                user.setCreatedon(updatedon);
            }
            if (requestParams.containsKey("deleteflag") && requestParams.get("deleteflag") != null) {
                int deleteflag = Integer.parseInt(requestParams.get("deleteflag").toString());
                user.setDeleteflag(deleteflag);
            }
            if (requestParams.containsKey("callwith") && requestParams.get("callwith") != null) {
                int callwith = Integer.parseInt(requestParams.get("callwith").toString());
                user.setCallwith(callwith);
            }
            if (requestParams.containsKey("userhash") && requestParams.get("userhash") != null) {
                String user_hash = requestParams.get("userhash").toString();
                user.setUser_hash(user_hash);
            }
            hibernateTemplate.saveOrUpdate(user);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.saveUser", e);
        }
    }

    public void deleteUser(String id) throws ServiceException {
        try {
            User u = (User) hibernateTemplate.load(User.class, id);
            if ((u.getRoleID() & 1) == 1) {
                throw new Exception("Cannot delete Administrator.");
            }
            if (u.getUserID().equals(u.getCompany().getCreator().getUserID())) {
                throw new Exception("Cannot delete Company Administrator.");
            }
            UserLogin userLogin = (UserLogin) hibernateTemplate.load(UserLogin.class, id);
            hibernateTemplate.delete(userLogin);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.deleteUser", e);
        }
    }

    public void saveUserLogin(HashMap<String, Object> requestParams) throws ServiceException {
        String userLoginId = "";
        UserLogin userLogin = null;
        try {
            if (requestParams.containsKey("userloginid") && requestParams.get("userloginid") != null) {
                userLoginId = requestParams.get("userloginid").toString();
                userLogin = (UserLogin) hibernateTemplate.load(UserLogin.class, userLoginId);
            } else {
                userLogin = new UserLogin();
            }
            userLogin.setLastActivityDate(new Date());
            if (requestParams.containsKey("userName") && requestParams.get("userName") != null) {
                String userName = requestParams.get("userName").toString();
                userLogin.setUserName(userName);
            }
            if (requestParams.containsKey("password") && requestParams.get("password") != null) {
                String password = requestParams.get("password").toString();
                userLogin.setPassword(password);
            }
            hibernateTemplate.update(userLogin);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.saveUserLogin", e);
        }
    }

    public KwlReturnObject changePassword(HashMap<String, Object> requestParams) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String password = "";
        String userid = "";
        String companyid = "";
        String pwd = "";
        String msg="";
        String platformURL = "";
        List ll = new ArrayList();
        int dl = 0;
        try {
            if (requestParams.containsKey("currentpassword") && requestParams.get("currentpassword") != null) {
                password = requestParams.get("currentpassword").toString();
            }
            if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                userid = requestParams.get("userid").toString();
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            if (requestParams.containsKey("changepassword") && requestParams.get("changepassword") != null) {
                pwd = requestParams.get("changepassword").toString();
            }
            if (requestParams.containsKey("platformURL") && requestParams.get("platformURL") != null) {
                platformURL = requestParams.get("platformURL").toString();
            }
            
            Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            
            if (password == null || password.length() <= 0) {
                msg=messageSource.getMessage("hrms.common.InvalidPassword", null,"Invalid Password.", locale);
            } else {
                
                if(StringUtil.isStandAlone()){
                    platformURL="";
                }
                if (!StringUtil.isNullOrEmpty(platformURL)) {
                    JSONObject userData = new JSONObject();
                    userData.put("pwd", pwd);
                    userData.put("oldpwd", password);
                    userData.put("userid", userid);
                    userData.put("remoteapikey",storageHandlerImpl.GetRemoteAPIKey());
                    String action = "3";
                    JSONObject resObj = APICallHandler.callApp(platformURL, userData, companyid, action);
                    if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                        User user = (User) hibernateTemplate.load(User.class, userid);
                        UserLogin userLogin = user.getUserLogin();
                           userLogin.setPassword(pwd);
                            hibernateTemplate.saveOrUpdate(userLogin);
                            msg= messageSource.getMessage("hrms.common.PasswordChangedsuccessfully", null,"Invalid Password.", locale);

                    } else {
                        if (!resObj.isNull("errorcode") && resObj.getString("errorcode").equals("e10")) {
                            msg = messageSource.getMessage("hrms.common.OldpasswordisincorrectPleasetryagain", null,"Invalid Password.", locale);
                        } else {
                            msg =messageSource.getMessage("hrms.common.ErrorinchangingPassword", null,"Invalid Password.", locale);
                        }
//                        msg="Error in changing Password";
                    }
                } else {
                    User user = (User) hibernateTemplate.load(User.class, userid);
                    UserLogin userLogin = user.getUserLogin();
                    String currentpass = userLogin.getPassword();
                    if (StringUtil.equal(password, currentpass)) {
                        userLogin.setPassword(pwd);
                        hibernateTemplate.saveOrUpdate(userLogin);
                        msg =  messageSource.getMessage("hrms.common.PasswordChangedsuccessfully", null,"Invalid Password.", locale);
                    } else {
                        msg = messageSource.getMessage("hrms.common.OldpasswordisincorrectPleasetryagain", null,"Invalid Password.", locale);
                    }
                }
            }
            jobj.put("msg", msg);

            ll.add(jobj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.changePassword", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public String getUser_hash(String userid) throws ServiceException {
        String res = "";
        try {
            JSONObject resObj = new JSONObject();
            User user = (User) hibernateTemplate.load(User.class, userid);
            resObj.put("userhash", user.getUser_hash());
            resObj.put("subdomain", user.getCompany().getSubDomain());
            res = resObj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getUser_hash", e);
        }
        return res;
    }
     public KwlReturnObject getUser(HashMap<String,Object> requestParams) {
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
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                User user = (User)hibernateTemplate.get(User.class, requestParams.get("userid").toString());
                List <User> list = new ArrayList<User>();
                list.add(user);
                result = new KwlReturnObject(success, "success", "", list, list.size());
                
            }
            hql = "from User";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<String>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<String>)requestParams.get("order_type"));
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
        public KwlReturnObject getEmpidFormatEdit(HashMap<String,Object> requestParameters){
        String mainstr = "";
        String empids = "";
        boolean success = false;
        List <String> lst = new ArrayList<String>();
        try {
            String cmpnyid = requestParameters.get("companyid").toString();
            Integer empid = Integer.valueOf(requestParameters.get("empid").toString());
//            KwlReturnObject result =  getCompanyid(requestParameters);
//            List list = result.getEntityList();
//            for (int ctr=0;ctr<list.size();ctr++) {
            CompanyPreferences cp = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, cmpnyid);
            Company cmp = cp.getCompany();
            String empidformat = cp.getEmpidformat() == null ? "" : cp.getEmpidformat();
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
//            }
            lst.add(mainstr);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return new KwlReturnObject(success, "success", "-1", lst, lst.size());
    }
        
        @SuppressWarnings("finally")
		public String getJobIdFormatEdit(HashMap<String,Object> requestParameters){
            String mainstr = "";
            String empids = "";
            try {
            	String jobidformat = requestParameters.get("jobidformat").toString();
                String cmpnyid = requestParameters.get("companyid").toString();
                Integer jobid = Integer.valueOf(requestParameters.get("jobid").toString());
                CompanyPreferences cp = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, cmpnyid);
                String idformat = cp.getJobidformat() == null ? "" : cp.getJobidformat();
                String[] row = (idformat).split("-");
                if (row.length == 1) {
                	if ((row[0]).startsWith("0")) {
                		int len = row[0].length();
                		String regex = "%0" + len + "d";
                		empids = String.format(regex, jobid);
                		jobidformat += (jobidformat.equals("")?"":"-");
                		mainstr = (jobidformat+empids);
                	} else {
                		jobidformat += (jobidformat.equals("")?"":"-"); 
                		mainstr = (jobidformat+jobid.toString());
                	}
                } else {
                	for (int i = 0; i < row.length; i++) {
                		if ((row[i]).startsWith("0")) {
                			int len = row[i].length();
                			String regex = "%0" + len + "d";
                			empids = String.format(regex, jobid);
                		}
                		if (!row[i].equals("")) {
                			if (!(row[i]).startsWith("0")) {
                				if(i==0){
                					jobidformat += (jobidformat.equals("")?"":"-"); 
                					mainstr = mainstr + jobidformat;
                				}else{
                					mainstr = mainstr + row[i] + "-";
                				}
                            } else {
                            	mainstr = mainstr + empids + "-";
                            }
                		} else {
                			mainstr = mainstr + empids + "-";
                        }
                	}
                	mainstr = mainstr.substring(0, mainstr.length() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	return mainstr;
            }
        }

        @SuppressWarnings("finally")
		public String getNewEmployeeIdFormat(HashMap<String, Object> requestParams){
        	String employeeid = "";
        	try{
        		String standardEmpId = requestParams.get("standardEmpId").toString();
        		String employeeIdFormat = requestParams.get("employeeIdFormat").toString();
        		String[] idFormates = standardEmpId.split("-");
        		String[] idFormates1 = employeeIdFormat.split("-");
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
        			employeeid += (idFormates1.length!=1?(idFormates[2]+"-"):"");
            		employeeid += idFormates[1];
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}finally{
        		return employeeid;
        	}
        }
     
     public KwlReturnObject getCompanyid(HashMap<String,Object> requestParams) {
        String result;
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String hql = "from Company  where companyID=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }
     
     public Date getLastActivityDate() throws ServiceException {
    	 List<Date> a = null;
    	 Date date = null;
     	 try {
        	String hql = "select max(lastActivityDate) as lastActivityDate from UserLogin";
 			a = HibernateUtil.executeQuery(hibernateTemplate, hql); 
 			if(a!=null && !a.isEmpty()){
 				date = a.get(0);
 			}
         } catch (Exception e) {
             throw ServiceException.FAILURE("profileHandlerDAOImpl.getLastActivityDate", e);
         }
         return date;
     }
     @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
