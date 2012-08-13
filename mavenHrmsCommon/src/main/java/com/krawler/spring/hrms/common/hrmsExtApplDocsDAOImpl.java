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
package com.krawler.spring.hrms.common;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.hrms.recruitment.Jobapplicant;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class hrmsExtApplDocsDAOImpl implements hrmsExtApplDocsDAO {

    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    public void parseRequest(List fileItems, HashMap<String, String> arrParam, ArrayList<FileItem> fi, boolean fileUpload) throws ServiceException {

        FileItem fi1 = null;
        for (Iterator k = fileItems.iterator(); k.hasNext();) {
            fi1 = (FileItem) k.next();
            if (fi1.isFormField()) {
                arrParam.put(fi1.getFieldName(), fi1.getString());
            } else {
                if (fi1.getSize() != 0) {
                    fi.add(fi1);
                    fileUpload = true;
                }
            }
        }
    }

    public KwlReturnObject uploadFile(FileItem fi, String userid, HashMap arrparam, String uploadedby) throws ServiceException {
        HrmsDocs docObj = new HrmsDocs();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String fileName = new String(fi.getName().getBytes(), "UTF8");
            String Ext = "";
            String a = "";

            if (fileName.contains(".")) {
                Ext = fileName.substring(fileName.indexOf(".") + 1, fileName.length());
                a = Ext.toUpperCase();
            }
            if(arrparam.get("IsIE").equals("true")){
                int cnt=fileName.indexOf("\\");
                while(cnt!=-1) {
                       fileName = fileName.substring(cnt+1,fileName.length());
                       cnt=fileName.indexOf("\\");
                }
            }
//            Jobapplicant jobapp = (Jobapplicant) hibernateTemplate.get(Jobapplicant.class, userid);
//            docObj.setApplicantid(jobapp);
            docObj.setDocname(fileName);
//            docObj.setDocdesc(docdesc);
            if (StringUtil.isNullOrEmpty((String) arrparam.get("docdesc")) == false) {
                docObj.setDocdesc((String) arrparam.get("docdesc"));
            }   
            docObj.setStorename("");
            docObj.setDoctype(a + " " + "File");
            docObj.setUploadedon(new Date());
            docObj.setUploadedby(uploadedby);
            docObj.setStorageindex(1);
            docObj.setDocsize(fi.getSize() + "");
            docObj.setReferenceid(userid);

            hibernateTemplate.save(docObj);

            String fileid = docObj.getDocid();
            if (Ext.length() > 0) {
                fileid = fileid + Ext;
            }
            docObj.setStorename(fileid);

            hibernateTemplate.update(docObj);

            ll.add(docObj);

//            String temp = "/home/trainee";
            String temp = storageHandlerImplObj.GetDocStorePath();
            uploadFile(fi, temp, fileid);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public void uploadFile(FileItem fi, String destinationDirectory, String fileName) throws ServiceException {
        try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File uploadFile = new File(destinationDirectory + "/" + fileName);
            fi.write(uploadFile);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.uploadFile", ex);
        }

    }

    public void saveDocumentMapping(JSONObject jobj) throws ServiceException {
        try {
            HrmsDocmap docMap = new HrmsDocmap();

            if (jobj.has("docid") && !StringUtil.isNullOrEmpty(jobj.getString("docid"))) {
                HrmsDocs doc = (HrmsDocs) hibernateTemplate.get(HrmsDocs.class, jobj.getString("docid"));
                docMap.setDocid(doc);
            }
            if (jobj.has("refid")) {
                docMap.setRecid(jobj.getString("refid"));
            }
            hibernateTemplate.save(docMap);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.saveDocumentMapping", ex);
        }
    }

    public KwlReturnObject getDocs(HashMap<String, Object> requestParams) {
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
            if (requestParams.containsKey("primary") && (Boolean) requestParams.get("primary")) {
                hql = "from com.krawler.hrms.common.docs.HrmsDocmap where id=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from com.krawler.hrms.common.docs.HrmsDocmap";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = (ArrayList) requestParams.get("filter_names");
                value = (ArrayList) requestParams.get("filter_values");
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null) {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = (ArrayList) requestParams.get("order_by");
                ordertype = (ArrayList) requestParams.get("order_type");
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public KwlReturnObject downloadDocument(String id) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            ll = HibernateUtil.executeQuery(hibernateTemplate, "FROM " +
                    "com.krawler.hrms.common.docs.HrmsDocmap where docid.docid =?", new Object[]{id});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteDocuments(HashMap<String, Object> requestParams) {
        boolean success = false;
        try {
            String[] ids = (String[]) requestParams.get("ids");
            for (int i = 0; i < ids.length; i++) {
                HrmsDocs docs = (HrmsDocs) hibernateTemplate.load(HrmsDocs.class, ids[i]);
                docs.setDeleted(true);
                hibernateTemplate.saveOrUpdate(docs);
            }
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "", null, 0);
        }
    }
}
