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
<%@page import="com.krawler.esp.database.*" %>
<%@page import="com.krawler.utils.json.base.*" %>
<%@page import="java.util.*" %>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@page import="com.krawler.esp.handlers.*" %>
<%@page import="com.krawler.common.locale.MessageSourceProxy" %>
<%@page import="com.krawler.common.util.LocaleUtil" %>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%

    JSONObject jobj=new JSONObject();
    JSONObject obj=new JSONObject();
   boolean isFormSubmit = false;
    if(sessionbean.validateSession(request, response)){
        try {
            int mode;
            int start;
            int limit;
            boolean success = true;
            HashMap hm=null;
            if(ServletFileUpload.isMultipartContent(request)){
                hm=new FileUploadHandler().getItems(request);
                mode=Integer.parseInt(hm.get("mode").toString());
            }else
                mode = Integer.parseInt(request.getParameter("mode"));

            boolean isLoggable = false;
            int actiontype=0;
            String actiontext="";
            switch (mode) {
               case 1 :
                     obj=DBCon.getFeatureList(request);
                break;
               case 2 :
                     obj=DBCon.getActivityList(request);
                break;
               case 3 :
                     isFormSubmit=true;
                     DBCon.saveFeature(request);
                     obj.put("msg", "Feature has been saved successfully.");
                break;
               case 4 :
                     isFormSubmit=true;
                     DBCon.saveActivity(request);
                     obj.put("msg", "Activity has been saved successfully.");
                break;
               case 5 :
                     DBCon.deleteFeature(request);
                     obj.put("msg", "Feature has been Deleted successfully.");
                break;
               case 6 :
                     DBCon.deleteActivity(request);
                     obj.put("msg", "Activity has been Deleted successfully.");

                break;
               case 7 :
                     obj=DBCon.getPermissionCode(request);
                break;
                case 8:
                     obj=DBCon.getRoles(request);
                break;
                case 9:
                     boolean isSaved = DBCon.saveRole(request);
                     if(isSaved){
                     	obj.put("msg", MessageSourceProxy.getMessage("hrms.jsp.msg.rolehasbeensavedsuccessfully" ,null,"Role has been saved successfully.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                     }else{
                    	 success = false;
                    	 obj.put("msg", MessageSourceProxy.getMessage("hrms.jsp.msg.role.already.exists" ,null, "Role name already exists, Please assign another role.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));	 
                     }
                break;
                case 10:
                	 DBCon.deleteRole(request);
                     obj.put("msg", MessageSourceProxy.getMessage("hrms.jsp.msg.rolehasbeendeletedsuccessfully" ,null,"Role has been deleted successfully.", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                     //obj.put("msg", "Role has been deleted successfully.");
                break;
               case 11 :
                   if(request.getParameter("start")==null)
                            {
                             start=0;
                             limit=15;
                        }else{
                              start=Integer.parseInt(request.getParameter("start"));
                             limit=Integer.parseInt(request.getParameter("limit"));
                        }
                     obj=DBCon.getAllUserDetails(request,start,limit);
                break;
               case 12 :
                     isFormSubmit=true;
                     if(hm==null)throw new Exception("Form does not support file upload.");
                     obj=DBCon.saveUser(request,hm);
                break;
               case 13 :
                     DBCon.deleteUser(request);
                     obj.put("msg", "User has been Deleted successfully.");
                break;
               case 14 :
                     DBCon.setPassword(request);
                     obj.put("msg", "Password has been saved successfully.");
                break;
               case 15 :
                     DBCon.setPermissions(request);
                     obj.put("msg", "Permissions have been assigned successfully.");
                break;
               case 16 :
                     obj=DBCon.getAllTimeZones(request);
                     break;
               case 17 :
                     obj=DBCon.getAllCurrencies(request);
                break;
               case 18 :
                     obj=DBCon.getCompanyInformation(request);
                break;
               case 19 :
                     obj=DBCon.getCompanyHolidays(request);
                break;
               case 20 :
                     obj=DBCon.getAllCountries(request);
                break;
               case 21 :
                     isFormSubmit=true;
                     if(hm==null)throw new Exception("Form does not support file upload.");
                     DBCon.updateCompany(request, hm);
                     obj.put("msg", "Company has been updated successfully.");
                break;
                case 22 :
                     obj=DBCon.getManagers(request);
                break;
                case 25 :
                     isFormSubmit=true;
                     DBCon.setEmpIdFormat(request);
                break;
				case 31:
                     obj.put("data", DBCon.getPreferences(request));
                     break;
               case 32:
                     obj=DBCon.getAllDateFormats(request);
                     break;
                case 23:
                    String platformURL = this.getServletContext().getInitParameter("platformURL");
                    obj=DBCon.changepassword(platformURL,request);
                    break;
                  case 24:
                     obj=DBCon.getparticularUserDetails(request);
                    break;
               case 41:
                     obj=DBCon.getAuditTrail(request);
                     break;
               case 42:
                     obj=DBCon.getAuditGroups(request);
                     break;
                  case 111 :
                      if(request.getParameter("start")==null)
                            {
                             start=0;
                             limit=15;
                        }else{
                              start=Integer.parseInt(request.getParameter("start"));
                             limit=Integer.parseInt(request.getParameter("limit"));
                        }
                     obj=DBCon.getAllCompanyDetails(request,start,limit);
                break;
                case 112 :
                    if(request.getParameter("start")==null)
                            {
                             start=0;
                             limit=15;
                        }else{
                              start=Integer.parseInt(request.getParameter("start"));
                             limit=Integer.parseInt(request.getParameter("limit"));
                        }
                     obj=DBCon.getUserofCompany(request,start,limit);
                break;
                case 113:
                    DBCon.deletecompany(request);
                    break;
                case 114 :
                   if(request.getParameter("start")==null)
                            {
                             start=0;
                             limit=15;
                        }else{
                             start=Integer.parseInt(request.getParameter("start"));
                             limit=Integer.parseInt(request.getParameter("limit"));
                        }
                     obj=DBCon.getAllUserDetails_profile(request,start,limit);
                break;                                
                case 115 :
                   if(request.getParameter("start")==null)
                            {
                             start=0;
                             limit=15;
                        }else{
                             start=Integer.parseInt(request.getParameter("start"));
                             limit=Integer.parseInt(request.getParameter("limit"));
                        }
                     obj=DBCon.getexEmployees(request,start,limit);
                break;
                case 116 :
                   if(request.getParameter("start")==null)
                            {
                             start=0;
                             limit=15;
                        }else{
                             start=Integer.parseInt(request.getParameter("start"));
                             limit=Integer.parseInt(request.getParameter("limit"));
                        }
                     obj=DBCon.getEmpHistory(request,start,limit);
                break;
            }
            obj.put("success",success);
         } catch (Exception e) {
            obj.put("success", false);
            obj.put("msg", e.getMessage());
        } finally {
            jobj.put("valid", true);
            if(!isFormSubmit){
                jobj.put("data", obj);
            }else{
                jobj=obj;
            }
        }
    } else {
        sessionbean.destroyUserSession(request, response);
         jobj.put("valid", false);
    }

    out.println(jobj);
%>
