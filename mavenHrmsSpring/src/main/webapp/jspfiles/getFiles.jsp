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
<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<%@ page import="com.krawler.esp.handlers.AuthHandler" %>
<%@ page import="com.krawler.common.session.SessionExpiredException" %>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.io.File" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Date"%>
<%@ page import="com.krawler.common.util.URLUtil"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.krawler.esp.handlers.StorageHandler"%>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="org.apache.commons.fileupload.DiskFileUpload" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.FileUploadException" %>
<%@ page import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.utils.json.base.JSONException"%>
<%@ page import="com.krawler.spring.hrms.template.db.EmailTemplateFiles"%>
<%@ page import="com.krawler.spring.hrms.template.db.ThemeImages"%>
<%@ page import="org.hibernate.*"%>
<%@ page import="com.krawler.crm.database.tables.*"%>
<%@ page import="com.krawler.common.admin.User"%>

<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />

<%
    String fType = "";
    JSONObject ret = new JSONObject();
    String res = "";
    Session hiberSession = null;
    JSONObject r = new JSONObject();
    if(sessionbean.isValidSession(request, response)){
        try {
            fType = request.getParameter("type");
            int file_type = 1;
            if(fType != null && fType.compareTo("img") == 0) {
                file_type = 0;
            }
            String companyid = AuthHandler.getCompanyid(request);
            String userId = AuthHandler.getUserid(request);
            int action = Integer.parseInt(request.getParameter("action"));
            hiberSession = HibernateUtil.getCurrentSession();
            String subdomain = AuthHandler.getSubDomain(request);
            switch(action){
                case 1: //get the list of files
                    String domainURL = URLUtil.getDomainURL(subdomain,true);
                    String query = "FROM EmailTemplateFiles AS et WHERE et.type = ? AND et.creator.company.companyID = ?";
                    List ll = HibernateUtil.executeQuery(hiberSession, query, new Object[]{file_type,companyid});
                    Iterator ite = ll.iterator();
                    while (ite.hasNext()) {
                        JSONObject temp = new JSONObject();
                        EmailTemplateFiles obj = (EmailTemplateFiles) ite.next();
                        temp.put("id", obj.getId());
                        temp.put("imgname", obj.getName());
                        temp.put("description", obj.getExtn());
                        String url = domainURL + "video.jsp?c=" + companyid + "&f=" + obj.getId().concat(obj.getExtn()) + "&t=" + fType;
                        temp.put("url", url);
                        ret.append("data", temp);
                    }
                    r.put("data", ret);
                    r.put("valid", true);
                    break;
                case 2: //upload a new file
                    HashMap arrParam = new HashMap();
                    String fname = "";
                    List fileItems = null;
                    String file_id = "";
                    String file_extn = "";
                    String basePath = StorageHandler.GetDocStorePath() + companyid + "/" + fType ;
                    String filename = "";
                    DiskFileUpload fu = new DiskFileUpload();
                    if (fu.isMultipartContent(request)) {
                        fileItems = fu.parseRequest(request);
                        for (Iterator k = fileItems.iterator(); k.hasNext();) {
                            FileItem fi1 = (FileItem) k.next();
                            arrParam.put(fi1.getFieldName(), fi1.getString());
                            if (!fi1.isFormField()) {
                                try {
                                    fname = new String(fi1.getName().getBytes(), "UTF8");
                                } catch (UnsupportedEncodingException ex) {
                                    System.out.println(ex.getMessage());
                                }
                                file_id = java.util.UUID.randomUUID().toString();
                                file_extn = fname.substring(fname.lastIndexOf("."));
                                filename = file_id.concat(file_extn);
                                boolean isUploaded = false;
                                try {
                                    String contentType = fi1.getContentType();
                                    long size = fi1.getSize();
                                    fname = new String(fi1.getName().getBytes(), "UTF8");
                                    fname = fname.substring(fname.lastIndexOf("\\") + 1);
                                    if (fname.contains(".")) {
                                        file_extn = fname.substring(fname.lastIndexOf("."));
                                    }
                                    if (fi1.getSize() != 0) {
                                        File destDir = new File(basePath);
                                        if (!destDir.exists()) {
                                            destDir.mkdirs();
                                        }
                                        File uploadFile = new File(basePath+ "/"+ filename);
                                        fi1.write(uploadFile);
                                        isUploaded = true;
                                    }
                                    if(isUploaded){
                                        hiberSession = HibernateUtil.getCurrentSession();
                                        hiberSession.beginTransaction();
                                        EmailTemplateFiles fileEntry = new EmailTemplateFiles();
                                        fileEntry.setId(file_id);
                                        fileEntry.setCreatedon(new Date());
                                        fileEntry.setCreator((User)hiberSession.get(User.class,userId));
                                        fileEntry.setExtn(file_extn);
                                        fileEntry.setName(fname);
                                        fileEntry.setType(file_type);
                                        hiberSession.save(fileEntry);
                                        hiberSession.getTransaction().commit();
                                    }
                                } catch (Exception ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                        }
                    }
                    ret.put("valid", true);
                    ret.put("data", "{success: true}");
                    r.put("data", ret);
                    r.put("success", true);
                    break;
                case 3://get theme images
                    query = "FROM themeImages AS ti WHERE ti.deleted = 0";
                    ll = HibernateUtil.executeQuery(hiberSession, query);
                    ite = ll.iterator();
                    while (ite.hasNext()) {
                        JSONObject temp = new JSONObject();
                        ThemeImages obj = (ThemeImages) ite.next();
                        temp.put("id", obj.getId());
                        temp.put("name", obj.getImagename());
                        temp.put("url", obj.getUrl());
                        temp.put("height", obj.getHeight());
                        ret.append("data", temp);
                    }
                    r.put("data", ret);
                    r.put("valid", true);
                    break;
                case 4://delete files
                    query = "FROM EmailTemplateFiles AS et WHERE et.id = ? AND et.creator.company.companyID = ?";
                    ll = HibernateUtil.executeQuery(hiberSession, query, new Object[]{request.getParameter("tempid"), companyid});
                    ite = ll.iterator();
                    if (ll.size() > 0) {
                        EmailTemplateFiles obj = (EmailTemplateFiles) ite.next();
                        hiberSession.delete(obj);
                        ret.put("data", "{success: true}");
                    } else {
                        ret.put("data", "{success: false}");
                    }
                    r.put("data", ret);
                    r.put("valid", true);
                    break;
            }
            res = r.toString();
        } catch(SessionExpiredException e) {
            res = "{valid: false, data:" + e.getMessage() + "}";
        } catch(JSONException e){
            res = "{valid: false, data:" + e.getMessage() + "}";
        } finally {
            HibernateUtil.closeSession(hiberSession);
        }
    }
%>
<%=res%>
