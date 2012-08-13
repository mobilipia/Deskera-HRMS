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

import com.krawler.common.admin.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.DiskFileUpload;
import java.util.HashMap;
import java.util.Iterator;
import com.krawler.common.service.ServiceException;
import com.krawler.hrms.common.docs.*;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.recruitment.ConfigRecruitmentData;
import com.krawler.hrms.recruitment.Jobapplicant;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import java.util.List;

/**
 *
 * @author krawler
 */
public class fileUploader {

    public static void parseRequest(HttpServletRequest request, HashMap<String, String> arrParam, ArrayList<FileItem> fi, boolean fileUpload) throws ServiceException {
        DiskFileUpload fu = new DiskFileUpload();
        FileItem fi1 = null;
        List fileItems = null;
        try {
            fileItems = fu.parseRequest(request);
        } catch (FileUploadException e) {
            throw ServiceException.FAILURE("Admin.createUser", e);
        }
        for (Iterator k = fileItems.iterator(); k.hasNext();) {
            fi1 = (FileItem) k.next();
            if (fi1.isFormField()) {
                arrParam.put(fi1.getFieldName(), fi1.getString());
            } else {
                try {
                    String fileName = new String(fi1.getName().getBytes(), "UTF8");
                    if (fi1.getSize() != 0) {
                        fi.add(fi1);
                        fileUpload = true;
                    }
                } catch (UnsupportedEncodingException ex) {
                }
            }
        }
    }
    public static void parseRequest(HttpServletRequest request, HashMap<String, String> arrParam, ArrayList<FileItem> fi, boolean fileUpload,HashMap<Integer,String> filemap) throws ServiceException {
        DiskFileUpload fu = new DiskFileUpload();
        FileItem fi1 = null;
        List fileItems = null;
        int i=0;
        try {
            fileItems = fu.parseRequest(request);
        } catch (FileUploadException e) {
            throw ServiceException.FAILURE("Admin.createUser", e);
        }
        for (Iterator k = fileItems.iterator(); k.hasNext();) {
            fi1 = (FileItem) k.next();
            try {
            	if (fi1.isFormField()) {
            		arrParam.put(fi1.getFieldName(), fi1.getString("UTF-8"));
            	} else {
                
                    String fileName = new String(fi1.getName().getBytes(), "UTF8");
                    if (fi1.getSize() != 0) {
                        fi.add(fi1);
                        filemap.put(i,fi1.getFieldName());
                        i++;
                        fileUpload = true;
                    }
            	}
            } catch (UnsupportedEncodingException ex) {
            	ex.printStackTrace();
            }
        }
    }

    public static HrmsDocs uploadFile(Session session, FileItem fi, String userid, HashMap arrparam, boolean flag) throws ServiceException,SessionExpiredException, UnsupportedEncodingException {
        HrmsDocs docObj = new HrmsDocs();
        User userObj = null;
        Jobapplicant jobapp = null;
        try {
            String fileName = new String(fi.getName().getBytes(), "UTF8");
            String Ext = "";
            if (fileName.contains(".")) {
                Ext = fileName.substring(fileName.lastIndexOf("."));
            }
            if (flag) {
                userObj = (User) session.get(User.class, userid);
                docObj.setUserid(userObj);
            } else {
                jobapp = (Jobapplicant) session.get(Jobapplicant.class, userid);
                docObj.setApplicantid(jobapp);
            }
            docObj.setDocname(fileName);
            docObj.setStorename("");
            docObj.setDoctype("");
            docObj.setUploadedon(new Date());
            docObj.setStorageindex(1);
            docObj.setDocsize(fi.getSize() + "");
            docObj.setDeleted(false);
            if (StringUtil.isNullOrEmpty((String) arrparam.get("docname")) == false) {
                docObj.setDispdocname((String) arrparam.get("docname"));
            }
            if (StringUtil.isNullOrEmpty((String) arrparam.get("docdesc")) == false) {
                docObj.setDocdesc((String) arrparam.get("docdesc"));
            }
            session.save(docObj);
            String fileid = docObj.getDocid();
            if (Ext.length() > 0) {
                fileid = fileid + Ext;
            }
            docObj.setStorename(fileid);
            session.update(docObj);           
            String temp = StorageHandler.GetDocStorePath1();
            uploadFile(fi, temp, fileid);

        } catch (ServiceException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return docObj;
    }
    public static HrmsDocs uploadFile(Session session, FileItem fi,ConfigRecruitmentData ConfigRecruitmentDataobj,HashMap arrparam, boolean flag) throws ServiceException,SessionExpiredException, UnsupportedEncodingException {
        HrmsDocs docObj = new HrmsDocs();
        User userObj = null;
        Jobapplicant jobapp = null;
        try {
            String fileName = new String(fi.getName().getBytes(), "UTF8");
            String Ext = "";
            if (fileName.contains(".")) {
                Ext = fileName.substring(fileName.lastIndexOf("."));
            }
            docObj.setDocname(fileName);
            docObj.setReferenceid(ConfigRecruitmentDataobj.getId());
            docObj.setStorename("");
            docObj.setDoctype("");
            docObj.setUploadedon(new Date());
            docObj.setUploadedby(ConfigRecruitmentDataobj.getCol1() + " " + ConfigRecruitmentDataobj.getCol2());
            docObj.setStorageindex(1);
            docObj.setDocsize(fi.getSize() + "");
            docObj.setDeleted(false);
            if (!StringUtil.isNullOrEmpty((String) arrparam.get("docname"))) {
                docObj.setDispdocname((String) arrparam.get("docname"));
            }
            if (!StringUtil.isNullOrEmpty((String) arrparam.get("docdesc"))) {
                docObj.setDocdesc((String) arrparam.get("docdesc"));
            }
            session.save(docObj);
            String fileid = docObj.getDocid();
            if (Ext.length() > 0) {
                fileid = fileid + Ext;
            }
            docObj.setStorename(fileid);
            session.update(docObj);
            String temp = StorageHandler.GetDocStorePath1();
            uploadFile(fi, temp, fileid);

        } catch (ServiceException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return docObj;
    }
    public static void uploadFile(FileItem fi, String destinationDirectory, String fileName) throws ServiceException {
        try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File uploadFile = new File(destinationDirectory + "/" + fileName);
            fi.write(uploadFile);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("moduleBuilderMethods.uploadFile", ex);
        }

    }
}
