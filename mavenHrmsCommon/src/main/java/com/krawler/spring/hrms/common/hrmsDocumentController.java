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

import com.krawler.common.admin.Docmap;
import com.krawler.common.admin.Docs;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class hrmsDocumentController extends MultiActionController {

    private documentDAO documentDAOObj;
    private hrmsExtApplDocsDAO hrmsExtApplDocsDAOObj;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private storageHandlerImpl storageHandlerImplObj;
    private String successView;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setdocumentDAO(documentDAO documentDAOObj) {
        this.documentDAOObj = documentDAOObj;
    }

    public void sethrmsExtApplDocsDAO(hrmsExtApplDocsDAO hrmsExtApplDocsDAOObj) {
        this.hrmsExtApplDocsDAOObj = hrmsExtApplDocsDAOObj;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj) {
        this.storageHandlerImplObj = storageHandlerImplObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public ModelAndView addDocuments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject myjobj = new JSONObject();
        List fileItems = null;
        KwlReturnObject kmsg = null;
        String auditAction = "";
        boolean applicant = false;
        String id = java.util.UUID.randomUUID().toString();
        PrintWriter out = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            response.setContentType("text/html;charset=UTF-8");
            out = response.getWriter();
            String userid = sessionHandlerImplObj.getUserid(request);
            String map = request.getParameter("mapid");
            HashMap<String, String> arrParam = new HashMap<String, String>();
            boolean fileUpload = false;
            String docdesc;
            ArrayList<FileItem> fi = new ArrayList<FileItem>();
            if (request.getParameter("fileAdd") != null) {
                DiskFileUpload fu = new DiskFileUpload();
                fileItems = fu.parseRequest(request);
                documentDAOObj.parseRequest(fileItems, arrParam, fi, fileUpload);
                arrParam.put("IsIE", request.getParameter("IsIE"));
                if (arrParam.get("applicantid").equalsIgnoreCase("applicant")) {
                    applicant = true;
                    userid = arrParam.get("refid");
                }
                if (StringUtil.isNullOrEmpty((String) arrParam.get("docdesc")) == false) {
                    docdesc= (String) arrParam.get("docdesc");
                }
            }
            for (int cnt = 0; cnt < fi.size(); cnt++) {
                String docID;
                if (applicant) {
                    kmsg = hrmsExtApplDocsDAOObj.uploadFile(fi.get(cnt), userid, arrParam, profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)));
                    HrmsDocs doc = (HrmsDocs) kmsg.getEntityList().get(0);
                    docID = doc.getDocid();
                } else {
                    kmsg = documentDAOObj.uploadFile(fi.get(cnt), userid, arrParam);
                    Docs doc = (Docs) kmsg.getEntityList().get(0);
                    docID = doc.getDocid();
                }


                String companyID = sessionHandlerImplObj.getCompanyid(request);
                String userID = sessionHandlerImplObj.getUserid(request);
                String refid = arrParam.get("refid");
                jobj.put("userid", userID);
                jobj.put("docid", docID);
                jobj.put("companyid", companyID);
                jobj.put("id", id);
                jobj.put("map", map);
                jobj.put("refid", refid);
                if (arrParam.get("applicantid").equalsIgnoreCase("applicant")) {
                    hrmsExtApplDocsDAOObj.saveDocumentMapping(jobj);
                } else {
                    documentDAOObj.saveDocumentMapping(jobj);
                }
            }
            myjobj.put("ID", id);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        } finally {
            out.close();
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    public HashMap getDocInfo(KwlReturnObject kmsg,storageHandlerImpl storageHandlerObj) throws ServiceException {
        HashMap ht = null;
        Hashtable htable;
        if(kmsg.getEntityList().size()!=0){
            htable = getExtDocumentDownloadHash(kmsg.getEntityList());
            ht = new HashMap();
            String src = storageHandlerObj.GetDocStorePath();
            src = src + htable.get("svnname");
            try{
                File fp = new File(src);

                ht.put("attachment", src);
                ht.put("filename", htable.get("Name"));
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return ht;
    }
    public ModelAndView downloadDocuments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String details = "";
        String auditAction = "";
        try {
            String url = request.getParameter("url");
            url = StringUtil.checkForNull(url);
            String applicant = request.getParameter("applicant");
            applicant = StringUtil.checkForNull(applicant);
            Hashtable ht;
            if (applicant.equalsIgnoreCase("applicant")) {
                kmsg = hrmsExtApplDocsDAOObj.downloadDocument(url);
                ht = getExtDocumentDownloadHash(kmsg.getEntityList());
            } else {
                kmsg = documentDAOObj.downloadDocument(url);
                ht = getDocumentDownloadHash(kmsg.getEntityList());
            }

            String src = storageHandlerImplObj.GetDocStorePath();
//            String src = "/home/trainee/";
            if (request.getParameter("mailattch") != null) {
                src = src + ht.get("svnname");
            } else {
                src = src + ht.get("userid").toString() + "/" + ht.get("svnname");
            }

            File fp = new File(src);
            byte[] buff = new byte[(int) fp.length()];
            FileInputStream fis = new FileInputStream(fp);
            int read = fis.read(buff);
            javax.activation.FileTypeMap mmap = new javax.activation.MimetypesFileTypeMap();
            response.setContentType(mmap.getContentType(src));
            response.setContentLength((int) fp.length());
            response.setHeader("Content-Disposition", request.getParameter("dtype") + "; filename=\"" + ht.get("Name") + "\";");
            response.getOutputStream().write(buff);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            String map = ht.get("relatedto").toString();
            String refid = ht.get("recid").toString();

            myjobj.put("success", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getDocs(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        KwlReturnObject result = null;
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            JSONArray jarr = new JSONArray();
            String userid = "";
            int start, limit;

            userid = request.getParameter("userid");
            String currentuser = AuthHandler.getUserid(request);
            String ss = request.getParameter("ss");
            if (request.getParameter("start") == null) {
                start = 0;
                limit = 15;
            } else {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }

            if (request.getParameter("applicant").equalsIgnoreCase("applicant")) {

                filter_names.add("recid");
                filter_values.add(userid);

                filter_names.add("docid.deleted");
                filter_values.add(false);

                filter_names.add("docid.referenceid");
                filter_values.add(userid);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                requestParams.put("ss", ss);
                requestParams.put("searchcol", new String[]{"docid.docname","docid.docdesc"});
                requestParams.put("start", start);
                requestParams.put("limit", limit);
                requestParams.put("allflag", false);
                result = hrmsExtApplDocsDAOObj.getDocs(requestParams);

                Iterator ite = result.getEntityList().iterator();
                while (ite.hasNext()) {
                    HrmsDocmap docs = (HrmsDocmap) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("docid", docs.getDocid().getDocid());
                    tmpObj.put("docname", docs.getDocid().getDocname());
                    tmpObj.put("docdesc", docs.getDocid().getDocdesc());
                    tmpObj.put("uploadedby", docs.getDocid().getUploadedby());
                    Float dsize = Math.max(0, Float.parseFloat(docs.getDocid().getDocsize()) / 1024);
                    tmpObj.put("docsize", String.valueOf(dsize));
                    tmpObj.put("uploaddate", AuthHandler.getDateFormatter(request).format(docs.getDocid().getUploadedon()));
                    jarr.put(tmpObj);
                }

            } else {
                filter_names.add("recid");
                filter_values.add(userid);

                filter_names.add("docid.deleteflag");
                filter_values.add(0);

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                requestParams.put("ss", ss);
                requestParams.put("searchcol", new String[]{"docid.docname","docid.docdesc"});
                requestParams.put("start", start);
                requestParams.put("limit", limit);
                requestParams.put("allflag", false);

                result = documentDAOObj.getDocs(requestParams);

                Iterator ite = result.getEntityList().iterator();
                while (ite.hasNext()) {
                    Docmap docs = (Docmap) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("docid", docs.getDocid().getDocid());
                    tmpObj.put("docname", docs.getDocid().getDocname());
                    tmpObj.put("docdesc", docs.getDocid().getDocdesc());
                    tmpObj.put("uploadedby", docs.getDocid().getUserid().getFirstName() + " " + docs.getDocid().getUserid().getLastName());
                    Float dsize = Math.max(0, Float.parseFloat(docs.getDocid().getDocsize()) / 1024);
                    tmpObj.put("docsize", String.valueOf(dsize));
                    tmpObj.put("uploaddate", AuthHandler.getDateFormatter(request).format(docs.getDocid().getUploadedon()));
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public Hashtable getDocumentDownloadHash(List ll) {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                com.krawler.common.admin.Docmap cDocMap = (com.krawler.common.admin.Docmap) ite.next();
                ht.put("relatedto", cDocMap.getRelatedto());
                ht.put("recid", cDocMap.getRecid());

                com.krawler.common.admin.Docs t = cDocMap.getDocid();
                ht.put("docid", t.getDocid());
                ht.put("Name", t.getDocname());
                ht.put("Size", t.getDocsize());
                ht.put("Type", t.getDoctype());
                ht.put("svnname", t.getStorename());
                ht.put("storeindex", String.valueOf(t.getStorageindex()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ht;
    }

    public Hashtable getExtDocumentDownloadHash(List ll) {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                com.krawler.hrms.common.docs.HrmsDocmap docmap = (com.krawler.hrms.common.docs.HrmsDocmap) ite.next();
                com.krawler.hrms.common.docs.HrmsDocs t = docmap.getDocid();
                ht.put("docid", t.getDocid());
                ht.put("Name", t.getDocname());
                ht.put("Size", t.getDocsize());
                ht.put("Type", t.getDoctype());
                ht.put("svnname", t.getStorename());
                ht.put("storeindex", String.valueOf(t.getStorageindex()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ht;
    }

    public ModelAndView deleteDocuments(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("ids", request.getParameterValues("ids"));
            String applicant = request.getParameter("applicant");
            applicant = StringUtil.checkForNull(applicant);
            if (applicant.equalsIgnoreCase("applicant")) {
                result = hrmsExtApplDocsDAOObj.deleteDocuments(requestParams);
            } else {
                result = documentDAOObj.deleteDocuments(requestParams);
            }
            if (result.isSuccessFlag()) {
                jobj.put("success", true);
            } else {
                jobj.put("success", false);
            }
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }
}
