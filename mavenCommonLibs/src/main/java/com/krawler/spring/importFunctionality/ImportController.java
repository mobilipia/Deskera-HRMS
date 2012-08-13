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

package com.krawler.spring.importFunctionality;

import com.krawler.common.admin.ImportLog;
import com.krawler.common.admin.Modules;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class ImportController extends MultiActionController implements MessageSourceAware {
    private ImportDAO importDao;
    public ImportHandler importHandler;
    private sessionHandlerImpl sessionHandlerImplObj;
    private MessageSource messageSource;
    
    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
    
    public void setimportDAO(ImportDAO importDao) {
        this.importDao = importDao;
    }
    public void setimportHandler(ImportHandler importHandler) {
        this.importHandler = importHandler;
    }

    public ModelAndView getColumnConfig(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try{
            String moduleName = request.getParameter("module");

            String ModuleId = null;
            String companyId = sessionHandlerImplObj.getCompanyid(request);
            try {
                List list = importDao.getModuleObject(moduleName);
                Modules module = (Modules) list.get(0); //Will throw null pointer if no module entry found
                ModuleId = module.getId();
            } catch(Exception ex) {
                throw new DataInvalidateException("Column config entries are not available for "+moduleName+" module.");
            }

            JSONArray DataJArr = importHandler.getModuleColumnConfig(ModuleId, companyId);
            jobj.put("data", DataJArr);
            jobj.put("count", DataJArr.length());
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            issuccess = true;
        } catch (Exception ex) {
            msg = ""+ex.getMessage();
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
            } catch (JSONException ex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView importRecords(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        JSONObject jobj1=new JSONObject();
        String View = "jsonView";
        try {
            String doAction = request.getParameter("do");
            System.out.println("A(( "+doAction+" start : "+new Date());
            String companyId = sessionHandlerImplObj.getCompanyid(request);
            if (doAction.compareToIgnoreCase("import") == 0 || doAction.compareToIgnoreCase("validateData") == 0) {
                HashMap<String, Object> requestParams = importHandler.getImportRequestParams(request);
                String eParams = request.getParameter("extraParams");
                JSONObject extraParams = StringUtil.isNullOrEmpty(eParams)?new JSONObject():new JSONObject(eParams);
                requestParams.put("extraParams", extraParams);
                requestParams.put("extraObj", null);
                requestParams.put("servletContext", this.getServletContext());
                if (doAction.compareToIgnoreCase("import") == 0) {
                    System.out.println("A(( Import start : "+new Date());
                    String exceededLimit = request.getParameter("exceededLimit");
                    if(exceededLimit.equalsIgnoreCase("yes")){ //If file contains records more than 1500 then Import file in background using thread
                        String logId = importHandler.addPendingImportLog(requestParams);
                        requestParams.put("logId", logId);
                        importHandler.add(requestParams);
                        if (!importHandler.isIsWorking()) {
                            Thread t = new Thread(importHandler);
                            t.start();
                        }
                        jobj.put("success", true);
                    } else {
                        jobj = importHandler.importFileData(requestParams);
                    }
                    jobj.put("exceededLimit", exceededLimit);
                    System.out.println("A(( Import end : "+new Date());
                } else if (doAction.compareToIgnoreCase("validateData") == 0) {
                    jobj = importHandler.validateFileData(requestParams);
                    jobj.put("success", true);
                }
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
            }else if (doAction.compareToIgnoreCase("getMapCSV") == 0) {
                jobj = importHandler.getMappingCSVHeader(request);
                View = "jsonView_ex";
                // Dump csv data in DB
                String filename = jobj.getString("name");
//                String actualfilename = ImportLog.getActualFileName(filename);
                String tableName = importDao.getTableName(filename);
                if(tableName.length()>64){ // To fixed Mysql ERROR 1103 (42000): Incorrect table name
                    throw new DataInvalidateException("Filename is too long, use upto 28 characters.");
                }
                importDao.createFileTable(tableName, jobj.getInt("cols"));
                importHandler.dumpCSVFileData(filename, jobj.getString("delimiterType"), 0);
//                importDao.makeUploadedFileEntry(filename, actualfilename, tableName, companyId);
                jobj1 = jobj;                
            } else if (doAction.compareToIgnoreCase("getXLSData") == 0) {
                try{
                    String filename = request.getParameter("filename");
                    int sheetNo = Integer.parseInt(request.getParameter("index"));
                    jobj = importHandler.parseXLS(filename, sheetNo);
                } catch(Exception e) {
                    Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, e);
                    try{
                        jobj.put("msg", e.getMessage());
                        jobj.put("lsuccess", false);
                        jobj.put("valid", true);
                    }catch(JSONException ex){}
                }
                View = "jsonView_ex";
                jobj1 = jobj;
            } else if (doAction.compareToIgnoreCase("dumpXLS") == 0) {
                int sheetNo = Integer.parseInt(request.getParameter("index"));
                int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
                int columns = Integer.parseInt(request.getParameter("totalColumns"));
                String filename = request.getParameter("onlyfilename");
//                String actualfilename = ImportLog.getActualFileName(filename);
                String tableName = importDao.getTableName(filename);
                importDao.createFileTable(tableName, columns);
                importHandler.dumpXLSFileData(filename, sheetNo, rowIndex);
//                importDao.makeUploadedFileEntry(filename, actualfilename, tableName, companyId);
                jobj.put("success", true);
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
            }
            System.out.println("A(( "+doAction+" end : "+new Date());
        } catch (Exception ex) {
            try {
                jobj.put("success", false);
                jobj.put("msg", ""+ex.getMessage());
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
            } catch (JSONException jex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, jex);
            }
        }
        return new ModelAndView(View, "model", jobj1.toString());
    }

    public ModelAndView fileUploadXLS(HttpServletRequest request, HttpServletResponse response) {
        String View = "jsonView_ex";
        JSONObject jobj=new JSONObject();
//        JSONObject jobj1=new JSONObject();
        try {
                System.out.println("A(( Upload XLS start : "+new Date());
                jobj.put("success", true);
                FileItemFactory factory = new DiskFileItemFactory(4096,new File("/tmp"));
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(10000000);
                List fileItems = upload.parseRequest(request);
                Iterator i = fileItems.iterator();
                String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "xlsfiles";
                String fileName=null;
                String fileid = UUID.randomUUID().toString();
                fileid = fileid.replaceAll("-", ""); // To append UUID without "-" [SK]
                String Ext = "";
                while(i.hasNext()){
                    java.io.File destDir = new java.io.File(destinationDirectory);
                    if (!destDir.exists()) { //Create xls file's folder if not present
                        destDir.mkdirs();
                    }
                    
                    FileItem fi = (FileItem)i.next();
                    if(fi.isFormField())continue;
                    fileName = fi.getName();
                    if (fileName.contains(".")) {
                        Ext = fileName.substring(fileName.lastIndexOf("."));
                        int startIndex= fileName.contains("\\")?(fileName.lastIndexOf("\\")+1):0;
                        fileName = fileName.substring(startIndex, fileName.lastIndexOf("."));
                    }

                    if(fileName.length()>28){ // To fixed Mysql ERROR 1103 (42000): Incorrect table name
                        throw new DataInvalidateException("Filename is too long, use upto 28 characters.");
                    }
                    fi.write(new File(destinationDirectory, fileName+"_"+fileid+Ext));
                }

                POIFSFileSystem fs      =
                new POIFSFileSystem(new FileInputStream(destinationDirectory+"/"+ fileName+"_"+fileid+Ext));
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                int count=wb.getNumberOfSheets();
                JSONArray jArr=new JSONArray();
                for(int x=0;x<count;x++){
                    JSONObject obj=new JSONObject();
                    obj.put("name", wb.getSheetName(x));
                    obj.put("index", x);
                    jArr.put(obj);
                }
                jobj.put("file", destinationDirectory+"/"+ fileName+"_"+fileid+Ext);
                jobj.put("filename", fileName+"_"+fileid+Ext);
                jobj.put("data", jArr);
                jobj.put("msg", messageSource.getMessage("hrms.commonlib.image.successfully.uploaded", null, RequestContextUtils.getLocale(request)));
                jobj.put("lsuccess", true);
                jobj.put("valid", true);
//                jobj1 = new JSONObject();
//                jobj1.put("valid", true);
//                jobj1.put("data", jobj);
        }catch(Exception e){
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, e);
            try{
                jobj.put("msg", e.getMessage());
                jobj.put("lsuccess", false);
                jobj.put("valid", true);
//                jobj1.put("valid", true);
//                jobj1.put("data", jobj);
            }catch(Exception ex){}
        } finally {
            System.out.println("A(( Upload XLS end : "+new Date());
            return new ModelAndView(View, "model", jobj.toString());
        }
    }

    public ModelAndView getImportLog(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("startdate", sdf.parse(request.getParameter("startdate")));
            requestParams.put("enddate",  sdf.parse(request.getParameter("enddate")));
            requestParams.put("companyid",  sessionHandlerImplObj.getCompanyid(request));
            KwlReturnObject result = importDao.getImportLog(requestParams);
            List list = result.getEntityList();
            DateFormat df = AuthHandler.getDateFormatter(request);
            JSONArray jArr = new JSONArray();
            Iterator itr = list.iterator();
            while(itr.hasNext()) {
                ImportLog ilog = (ImportLog) itr.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("id", ilog.getId());
                jtemp.put("filename", ilog.getFileName());
                jtemp.put("storename", ilog.getStorageName());
                jtemp.put("failurename", ilog.getFailureFileName());
                jtemp.put("log", ilog.getLog());
                jtemp.put("imported", ilog.getImported());
                jtemp.put("total", ilog.getTotalRecs());
                jtemp.put("rejected", ilog.getRejected());
                jtemp.put("type", ilog.getType());
                jtemp.put("importon", df.format(ilog.getImportDate()));
                jtemp.put("module", ilog.getModule().getModuleName());
                jtemp.put("importedby", (ilog.getUser().getFirstName()==null?"":ilog.getUser().getFirstName())+" "+(ilog.getUser().getLastName()==null?"":ilog.getUser().getLastName()));
                jtemp.put("company", ilog.getCompany().getCompanyName());
                jArr.put(jtemp);
            }
            jobj.put("data", jArr);
            jobj.put("count", result.getRecordTotalCount());
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            issuccess = true;
        } catch (Exception ex){
            msg = ""+ ex.getMessage();
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj1.put("valid", true);
                jobj1.put("data", jobj);
            } catch (JSONException ex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView removeAllFileTables(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try{
            int cnt = importDao.removeAllFileTables();
            msg = "Deleted "+cnt+" tables successfully";
            issuccess = true;
        } catch (Exception ex){
            msg = ""+ ex.getMessage();
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
            } catch (JSONException ex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public void downloadFileData(HttpServletRequest request, HttpServletResponse response) {
        try {
            String filename = request.getParameter("filename");
            String storagename = request.getParameter("storagename");
            String filetype = request.getParameter("type");
            String destinationDirectory = storageHandlerImpl.GetDocStorePath();
            destinationDirectory += filetype.equalsIgnoreCase("csv")?"importplans":"xlsfiles";
            File intgfile = new File(destinationDirectory + "/" + storagename);
            byte[] buff = new byte[(int) intgfile.length()];

            try {
                FileInputStream fis = new FileInputStream(intgfile);
                int read = fis.read(buff);
            } catch (IOException ex) {
                filename = "file_not_found.txt";
            }

            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(buff.length);
            response.getOutputStream().write(buff);
            response.getOutputStream().flush();
        } catch (IOException ex) {
            KrawlerLog.op.warn("Unable To Download File :" + ex.toString());
        } catch (Exception ex) {
            KrawlerLog.op.warn("Unable To Download File :" + ex.toString());
        }

    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
