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

import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.ImportLog;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.Modules;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.CsvReader;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.handlers.ServerEventManager;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import java.io.File;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.krawler.utils.json.base.JSONException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
/**
 *
 * @author krawler
 */
public class ImportHandler implements Runnable {    
    private String df = "yyyy-MM-dd";
    private String df_full = "yyyy-MM-dd hh:mm:ss";
    private String df_customfield = "MMM dd, yyyy hh:mm:ss aaa";
    private String EmailRegEx = "^[\\w-]+([\\w!#$%&'*+/=?^`{|}~-]+)*(\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[\\w-]+)$";
    private String TimeRegEx = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private int importLimit = 1500;
    private static final DecimalFormat dfmt= new DecimalFormat("#.#####");
    
    private static String[] masterTables = {"MasterItem"};
    private HibernateTransactionManager txnManager;
    private ImportDAO importDao;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    
    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public void setimportDAO(ImportDAO importDao) {
        this.importDao = importDao;
    }
    public void setmasterTables(String[] masterModules) {
        masterTables = masterModules;
    }


    public static boolean isMasterTable(String module){
        boolean isMasterModule = false;
        for(int i=0; i<masterTables.length; i++){
            if(module.equalsIgnoreCase(masterTables[0])){
                isMasterModule = true;
                break;
            }
        }
        return isMasterModule;
    }

    boolean isWorking = false;
    ArrayList processQueue = new ArrayList();

    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public boolean isIsWorking() {
        return isWorking;
    }

    public void add(HashMap<String, Object> requestParams) {
        try {
            processQueue.add(requestParams);
        } catch (Exception ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try{
            while (!processQueue.isEmpty()) {
                HashMap<String, Object> requestParams = (HashMap<String, Object>) processQueue.get(0);
                try {
                    this.isWorking = true;
                    String modulename = requestParams.get("modName").toString();

                    JSONObject jobj = importFileData(requestParams);

                    User user = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", requestParams.get("userid").toString());
                    String htmltxt = "Report for data imported.<br/>";
                    htmltxt += "<br/>Module Name: "+modulename+"<br/>";
                    htmltxt += "<br/>File Name: "+jobj.get("filename")+"<br/>";
                    htmltxt += "Total Records: " + jobj.get("totalrecords")+"<br/>";
                    htmltxt += "Records Imported Successfully: " + jobj.get("successrecords");
                    htmltxt += "<br/>Failed Records: " + jobj.get("failedrecords");
                    htmltxt += "<br/><br/>Please check the import log in the system for more details.";
                    htmltxt += "<br/>For queries, email us at support@deskera.com<br/>";
                    htmltxt += "Deskera Team";

                    String plainMsg = "Report for data imported.\n";
                    plainMsg += "\nModule Name: "+modulename+"\n";
                    plainMsg += "\nFile Name:"+jobj.get("filename")+"\n";
                    plainMsg += "Total Records: " + jobj.get("totalrecords");
                    plainMsg += "\nRecords Imported Successfully: " + jobj.get("successrecords");
                    plainMsg += "\nFailed Records: " + jobj.get("failedrecords");
                    plainMsg += "\n\nPlease check the import log in the system for more details.";

                    plainMsg += "\nFor queries, email us at support@deskera.com\n";
                    plainMsg += "Deskera Team";

                    SendMailHandler.postMail(new String[]{user.getEmailID()}, "Deskera Accounting - Report for data imported", htmltxt, plainMsg, "Admin Deskera<admin@deskera.com>");
                } catch (Exception ex) {
                    Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    processQueue.remove(requestParams);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            this.isWorking = false;
        }
    }

    public JSONObject getMappingCSVHeader(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        CsvReader csvReader = null;
        JSONObject jtemp1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        JSONObject jsnobj = new JSONObject();
        String delimiterType=request.getParameter("delimiterType");
        String str = "";
        {
            FileInputStream fstream = null;
            try {
                if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {

                    String fileid = UUID.randomUUID().toString();
                    fileid = fileid.replaceAll("-", ""); // To append UUID without "-" [SK]
//                    String Module = request.getParameter("type")==null?"":"_"+request.getParameter("type");
                    String f1 = uploadDocument(request, fileid);

                    if (f1.length() != 0) {
                        String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
                        File csv = new File(destinationDirectory + "/" + f1);
                        fstream = new FileInputStream(csv);
                        csvReader = new CsvReader(new InputStreamReader(fstream),delimiterType);

                        csvReader.readHeaders();

                        int cols = csvReader.getHeaderCount();
                        for (int k = 0; k < csvReader.getHeaderCount(); k++) {
                            jtemp1 = new JSONObject();
                            if(!StringUtil.isNullOrEmpty(csvReader.getHeader(k).trim())) {
                                jtemp1.put("header", csvReader.getHeader(k));
                                jtemp1.put("index", k);
                                jobj.append("Header", jtemp1);
                            }
                        }

                        if (jobj.isNull("Header")) {
                            jsnobj.put("success", "true");

                            str = jsnobj.toString();
                        } else {
                            jobj.append("success", "true");
                            jobj.append("FileName", f1);
                            jobj.put("name", f1);
                            jobj.put("delimiterType", delimiterType);
                            jobj.put("cols", cols);
                            str = jobj.toString();
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                csvReader.close();
                fstream.close();
            }
        }
        return jobj;
    }

    public String cleanHTML(String strText) throws IOException {
        return strText!=null?StringUtil.serverHTMLStripper(strText):null;
    }

    public String uploadDocument(HttpServletRequest request, String fileid) throws ServiceException {
        String result = "";
        try {
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            org.apache.commons.fileupload.DiskFileUpload fu = new org.apache.commons.fileupload.DiskFileUpload();
            org.apache.commons.fileupload.FileItem fi = null;
            org.apache.commons.fileupload.FileItem docTmpFI = null;

            List fileItems = null;
            try {
                fileItems = fu.parseRequest(request);
            } catch (FileUploadException e) {
                KrawlerLog.op.warn("Problem While Uploading file :" + e.toString());
            }

            long size = 0;
            String Ext = "";
            String fileName = null;
            boolean fileupload = false;
            java.io.File destDir = new java.io.File(destinationDirectory);
            fu.setSizeMax(-1);
            fu.setSizeThreshold(4096);
            fu.setRepositoryPath(destinationDirectory);
            java.util.HashMap arrParam = new java.util.HashMap();
            for (java.util.Iterator k = fileItems.iterator(); k.hasNext();) {
                fi = (org.apache.commons.fileupload.FileItem) k.next();
                arrParam.put(fi.getFieldName(), fi.getString());
                if (!fi.isFormField()) {
                    size = fi.getSize();
                    fileName = new String(fi.getName().getBytes(), "UTF8");

                    docTmpFI = fi;
                    fileupload = true;
                }
            }

            if (fileupload) {

                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                if (fileName.contains(".")) {
                    Ext = fileName.substring(fileName.lastIndexOf("."));
                }
                if (size != 0) {
                    int startIndex= fileName.contains("\\")?(fileName.lastIndexOf("\\")+1):0;
                    fileName = fileName.substring(startIndex, fileName.lastIndexOf("."));
                    fileName = fileName.replaceAll(" ", "");
                    fileName = fileName.replaceAll("/", "");
                    result = fileName+"_"+fileid + Ext;

                    File uploadFile = new File(destinationDirectory + "/" + result);
                    docTmpFI.write(uploadFile);
//                    fildoc(fileid, fileName, fileid + Ext, AuthHandler.getUserid(request), size);

                }
            }

        }
//        catch (ConfigurationException ex) {
//            Logger.getLogger(ExportImportContacts.class.getName()).log(Level.SEVERE, null, ex);
//            throw ServiceException.FAILURE("ExportImportContacts.uploadDocument", ex);
//        }
        catch (Exception ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE("ExportImportContacts.uploadDocument", ex);
        }
        return result;
    }

    public JSONObject importCSVFile(HashMap<String, Object> requestParams, JSONObject extraParams, Object extraObj) throws IOException, ServiceException, DataInvalidateException {
        CsvReader csvReader = null;
        FileInputStream fstream = null;
        JSONObject jobj = new JSONObject();
        String msg = "";
        boolean issuccess = true;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean commitedEx = false;

        int total = 0, failed = 0;
        String csvFile = "";
        Modules module = null;
        String companyid = requestParams.get("companyid").toString();
        String userid = requestParams.get("userid").toString();
        try {
            String mode = requestParams.get("modName").toString();
            String delimiterType = requestParams.get("delimiterType").toString();
            csvFile = requestParams.get("filename").toString();
            StringBuilder failedRecords = new StringBuilder();
            String jsondata = requestParams.get("resjson").toString();
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            JSONObject rootcsvjobj = jarr.getJSONObject(0);

            String rootcsvjson = rootcsvjobj.getString("root");
            JSONArray mapping = new JSONArray(rootcsvjson);

            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            File csv = new File(destinationDirectory + "/" + csvFile);
            fstream = new FileInputStream(csv);

            csvReader = new CsvReader(new InputStreamReader(fstream), delimiterType);
            csvReader.readHeaders();
            failedRecords.append(csvReader.getRawRecord()+",\"Error Message\"");


            String classPath="", primaryKey="", uniqueKeyMethodName="", uniqueKeyHbmName="";
            try {
                List list = importDao.getModuleObject(mode);
                module = (Modules) list.get(0); //Will throw null pointer if no module entry found
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+mode);
            }

            try {
                classPath = module.getPojoClassPathFull().toString();
                primaryKey = module.getPrimaryKey_MethodName().toString();
            } catch (Exception ex) {
                throw new DataInvalidateException("Please set proper properties for module "+mode);
            }
            uniqueKeyMethodName = module.getUniqueKey_MethodName();
            uniqueKeyHbmName = module.getUniqueKey_HbmName();


            JSONArray columnConfig = getModuleColumnConfig(module.getId(), companyid);

            while (csvReader.readRecord()) {
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                HashMap<String,Object> columnHeaderMap = new HashMap<String,Object>();
                JSONArray customfield = new JSONArray();
                for (int k = 0; k < mapping.length(); k++) {
                    JSONObject mappingJson = mapping.getJSONObject(k);
                    String datakey = mappingJson.getString("columnname");
                    Object dataValue = cleanHTML(csvReader.get(mappingJson.getInt("csvindex")));
                    dataMap.put(datakey, dataValue);
                    columnHeaderMap.put(datakey, mappingJson.getString("csvheader"));
                }

                for (int j=0; j< extraParams.length(); j++) {
                    String datakey = (String) extraParams.names().get(j);
                    Object dataValue = extraParams.get(datakey);
                    dataMap.put(datakey, dataValue);
                }

                Object object = null;
                try {
                    CheckUniqueRecord(requestParams, dataMap, classPath, uniqueKeyMethodName, uniqueKeyHbmName);
                    validateDataMAP(requestParams, dataMap, columnConfig, customfield, columnHeaderMap);
                    object = importDao.saveRecord(requestParams, dataMap, csvReader, mode, classPath, primaryKey, extraObj, customfield);
                } catch(Exception ex) {
                    failed++;
                    failedRecords.append("\n"+csvReader.getRawRecord()+",\""+ex.getMessage()+"\"");
                    Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                total++;
            }

            if(failed > 0) {
                createFailureFiles(csvFile, failedRecords, null);
            }

            int success = total-failed;
            if(total == 0) {
                msg = "Empty file.";
            } else if(success == 0) {
//                issuccess = false;
                msg = "Failed to import all the records.";
            } else if(success == total) {
                msg = "All records are imported successfully.";
            } else {
                msg = "Imported "+success+" record"+(success>1?"s":"")+" successfully";
                msg += (failed==0?".":" and failed to import "+failed+" record"+(failed>1?"s":"")+".");
            }

            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
//        } catch (JSONException e) {
//            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
//            issuccess = false;
//            msg = ""+e.getMessage();
//        } catch (IOException e) {
//            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
//            issuccess = false;
//            msg = ""+e.getMessage();
        } catch (Exception e) {
            if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            issuccess = false;
            msg = ""+e.getMessage();
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            csvReader.close();
            fstream.close();

            DefaultTransactionDefinition ldef = new DefaultTransactionDefinition();
            ldef.setName("import_Tx");
            ldef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus lstatus = txnManager.getTransaction(ldef);
            try {
                //Insert Integration log
                HashMap<String, Object> logDataMap = new HashMap<String, Object>();
                logDataMap.put("FileName", ImportLog.getActualFileName(csvFile));
                logDataMap.put("StorageName", csvFile);
                logDataMap.put("Log", msg);
                logDataMap.put("Type", "csv");
                logDataMap.put("TotalRecs", total);
                logDataMap.put("Rejected", failed);
                logDataMap.put("Module", module.getId());
                logDataMap.put("ImportDate", new Date());
                logDataMap.put("User", userid);
                logDataMap.put("Company", companyid);
                importDao.saveImportLog(logDataMap);
                txnManager.commit(lstatus);
            } catch (Exception ex) {
                txnManager.rollback(lstatus);
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj.put("totalrecords", total);
                jobj.put("successrecords", total-failed);
                jobj.put("failedrecords", failed);
                jobj.put("filename", ImportLog.getActualFileName(csvFile));
            } catch (JSONException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jobj;
    }

    public JSONObject doXLSImport(HashMap<String, Object> requestParams, JSONObject extraParams, Object extraObj) throws IOException, JSONException {

        JSONObject parserResponse = new JSONObject();
        JSONObject jobj = new JSONObject();
        String xlsFile = requestParams.get("filepath").toString();
        String xlsFileName = requestParams.get("onlyfilename").toString();
        String mode = requestParams.get("moduleName").toString();
        int sheetindex =  Integer.parseInt(requestParams.get("sheetindex").toString());
        int startindex =  Integer.parseInt(requestParams.get("startindex").toString());
        String companyid = requestParams.get("companyid").toString();
        String userid = requestParams.get("userid").toString();

        String msg = "";
        boolean issuccess = true;
        FileInputStream fstream = null;
        boolean commitedEx = false;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);


        int total = 0, failed = 0;
        Modules module = null;

        try {
                parserResponse = parseXLS1(xlsFile, sheetindex,startindex);
                JSONArray xlsHeaders = new JSONArray(parserResponse.getString("Header"));
                String xlsjson = parserResponse.getString("data");
                JSONArray jarr2 = new JSONArray(xlsjson);

                String jsondata = requestParams.get("resjson").toString();
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                JSONObject rootcsvjobj = jarr.getJSONObject(0);

                String rootcsvjson = rootcsvjobj.getString("root");
                JSONArray jarr1 = new JSONArray(rootcsvjson);

                File xls = new File(xlsFile);
                fstream = new FileInputStream(xls);

                StringBuilder failedRecords = new StringBuilder();
                for(int j=0; j < xlsHeaders.length(); j++) {
                    JSONObject header = xlsHeaders.getJSONObject(j);
                    failedRecords.append("\""+header.getString("header")+"\",");
                }
                failedRecords.append("\"Error Message\"");

                String classPath="", primaryKey="", uniqueKeyMethodName="", uniqueKeyHbmName="";
                try {
                    List list = importDao.getModuleObject(mode);
                    module = (Modules) list.get(0); //Will throw null pointer if no module entry found
                } catch (Exception ex) {
                    throw new DataInvalidateException("Column config not available for module "+mode);
                }

                try {
                    classPath = module.getPojoClassPathFull().toString();
                    primaryKey = module.getPrimaryKey_MethodName().toString();
                } catch (Exception ex) {
                    throw new DataInvalidateException("Please set proper properties for module "+mode);
                }
                uniqueKeyMethodName = module.getUniqueKey_MethodName();
                uniqueKeyHbmName = module.getUniqueKey_HbmName();


                JSONArray columnConfig = getModuleColumnConfig(module.getId(), companyid);

                for(int a = 1 ; a <= (jarr2.length()-1) ; a++ ){
                    JSONObject xlsjrecobj = jarr2.getJSONObject(a);

                    HashMap<String,Object> dataMap = new HashMap<String,Object>();
                    HashMap<String,Object> culmnHeaderMap = new HashMap<String,Object>();
                    JSONArray customfield = new JSONArray();
                    for (int k = 0; k < jarr1.length(); k++) {
                        JSONObject xlsjobj = jarr1.getJSONObject(k);
                        String datakey = xlsjobj.getString("columnname");
                        String xlsHeader = xlsjobj.getString("csvheader");
                        String xlsValue ="";
                         if(xlsjrecobj.has(xlsHeader))
                          xlsValue =  cleanHTML(xlsjrecobj.getString(xlsHeader));
                        dataMap.put(datakey, xlsValue);
                        culmnHeaderMap.put(datakey, xlsHeader);
                    }

                    for (int j=0; j< extraParams.length(); j++) {
                        String datakey = (String) extraParams.names().get(j);
                        Object dataValue = extraParams.get(datakey);
                        dataMap.put(datakey, dataValue);
                    }

                    Object object = null;
                    try {
                        CheckUniqueRecord(requestParams, dataMap, classPath, uniqueKeyMethodName, uniqueKeyHbmName);
                        validateDataMAP(requestParams, dataMap, columnConfig, customfield, culmnHeaderMap);
                        object = importDao.saveRecord(requestParams, dataMap, xlsjrecobj, mode, classPath, primaryKey, extraObj, customfield);
                    } catch(Exception ex) {
                        failed++;
//                        failedRecords.append("\n"+csvReader.getRawRecord()+",\""+ex.getMessage()+"\"");
                        failedRecords.append("\n");
                        for(int j=0; j < xlsHeaders.length(); j++) {
                            JSONObject header = xlsHeaders.getJSONObject(j);
                            if(xlsjrecobj.opt(header.getString("header")) != null) {
                                failedRecords.append("\""+xlsjrecobj.opt(header.getString("header"))+"\",");
                            } else
                                failedRecords.append(",");
                        }
                        failedRecords.append("\""+ex.getMessage()+"\"");
//                        Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    total++;
                }

                if(failed > 0) {
                    createFailureFiles(xlsFileName, failedRecords, ".csv");
                }

                int success = total-failed;
                if(total == 0) {
                    msg = "Empty file.";
                } else if(success == 0) {
    //                issuccess = false;
                    msg = "Failed to import all the records.";
                } else if(success == total) {
                    msg = "All records imported successfully.";
                } else {
                    msg = "Imported "+success+" record"+(success>1?"s":"")+" successfully";
                    msg += (failed==0?".":" and failed to import "+failed+" record"+(failed>1?"s":"")+".");
                }

                try {
                    txnManager.commit(status);
                } catch(Exception ex) {
                    commitedEx = true;
                    throw ex;
                }

            } catch (Exception e) {
                if (!commitedEx) { //if exception occurs during commit then dont call rollback
                    txnManager.rollback(status);
                }
                issuccess = false;
                msg = ""+e.getMessage();
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                fstream.close();

                DefaultTransactionDefinition ldef = new DefaultTransactionDefinition();
                ldef.setName("import_Tx");
                ldef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                TransactionStatus lstatus = txnManager.getTransaction(ldef);
                boolean exCommit = false;
                try {
                    //Insert Integration log

                    HashMap<String, Object> logDataMap = new HashMap<String, Object>();
                    logDataMap.put("FileName", ImportLog.getActualFileName(xlsFileName));
                    logDataMap.put("StorageName", xlsFileName);
                    logDataMap.put("Log", msg);
                    logDataMap.put("Type", "xls");
                    logDataMap.put("TotalRecs", total);
                    logDataMap.put("Rejected", failed);
                    logDataMap.put("Module", module.getId());
                    logDataMap.put("ImportDate", new Date());
                    logDataMap.put("User", userid);
                    logDataMap.put("Company", companyid);
                    importDao.saveImportLog(logDataMap);
                    try {
                        txnManager.commit(lstatus);
                    }catch(Exception ex){
                        exCommit = true;
                        throw ex;
                    }
                } catch (Exception ex) {
                    if (!exCommit) { //if exception occurs during commit then dont call rollback
                        txnManager.rollback(lstatus);
                    }
                    Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    jobj.put("success", issuccess);
                    jobj.put("msg", msg);
                    jobj.put("totalrecords", total);
                    jobj.put("successrecords", total-failed);
                    jobj.put("failedrecords", failed);
                    jobj.put("filename", ImportLog.getActualFileName(xlsFileName));
                } catch (JSONException ex) {
                    Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        return jobj;
    }

    public void validateDataMAP(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, JSONArray columnConfigArray, JSONArray customfield, HashMap<String, Object> columnHeaderMap) throws DataInvalidateException {
        String errorMsg = "";
        for (int k = 0; k < columnConfigArray.length(); k++) {
            try {
                JSONObject columnConfig = columnConfigArray.getJSONObject(k);
                String column = columnConfig.getString("pojoName");

                if (dataMap.containsKey(column)) {
                    validateColumnData(requestParams, dataMap, columnConfig, column, customfield, columnHeaderMap, null);
                } else {
                    if (columnConfig.has("defaultValue")) {
                        dataMap.put(column, getDefaultValue(columnConfig));
                    }
                }
            } catch (Exception ex) {
                errorMsg += ex.getMessage()+",";
            }
        }
        if(errorMsg.length()>0) {
            errorMsg = errorMsg.substring(0, errorMsg.length()-1)+".";
            throw new DataInvalidateException(errorMsg);
        }
    }

    public void createFailureFiles(String filename, StringBuilder failedRecords, String ext) {
        String destinationDirectory;
        try {
            destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            if(StringUtil.isNullOrEmpty(ext)) {
                ext = filename.substring(filename.lastIndexOf("."));
            }
            filename = filename.substring(0,filename.lastIndexOf("."));

            java.io.FileOutputStream failurefileOut = new java.io.FileOutputStream(destinationDirectory + "/" + filename+ImportLog.failureTag+ext);
            failurefileOut.write(failedRecords.toString().getBytes());
            failurefileOut.flush();
            failurefileOut.close();
        } catch (Exception ex) {
            System.out.println("\nError file write [success/failed] " + ex);
        }
    }

    public JSONArray getModuleColumnConfig(String moduleId, String companyid) throws ServiceException {
        JSONArray jArr = new JSONArray();
        List list = importDao.getModuleColumnConfig(moduleId, companyid);
        try {
            Iterator itr = list.iterator();
            while(itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                DefaultHeader dh = (DefaultHeader) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.DefaultHeader", row[0].toString());
                JSONObject jtemp = new JSONObject();
                jtemp.put("id", dh.getId());
                jtemp.put("columnName", dh.getDefaultHeader());
                jtemp.put("pojoName", dh.getPojoMethodName());
                jtemp.put("isMandatory", dh.isMandatory());
                jtemp.put("isNotNull", dh.isHbmNotNull());
                jtemp.put("maxLength", dh.getMaxLength());
                jtemp.put("defaultValue", dh.getDefaultValue());
                jtemp.put("validatetype", dh.getValidateType());
                jtemp.put("refModule", dh.getRefModule_PojoClassName());
                jtemp.put("refFetchColumn", dh.getRefFetchColumn_HbmName());
                jtemp.put("refDataColumn", dh.getRefDataColumn_HbmName());
                jtemp.put("customflag", dh.isCustomflag());
                jtemp.put("pojoHeader", dh.getPojoheadername());
                jtemp.put("xtype", dh.getXtype());
                jtemp.put("configid", dh.getConfigid()==null?"":dh.getConfigid());

                jArr.put(jtemp);
            }
        } catch (JSONException ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jArr;
    }

    public void CheckUniqueRecord(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, String classPath, String uniqueKeyMethodName, String uniqueKeyHbmName) throws DataInvalidateException {
        if(!StringUtil.isNullOrEmpty(uniqueKeyMethodName) && !StringUtil.isNullOrEmpty(uniqueKeyHbmName)) {
            try {
                //@@@ What for CrmAccount table.
                List list = getRefData(requestParams, classPath, uniqueKeyHbmName, uniqueKeyHbmName, "", dataMap.get(uniqueKeyMethodName));
                if(!list.isEmpty()) {
                    throw new DataInvalidateException("Record for "+dataMap.get(uniqueKeyMethodName)+" already exists");
                }
            } catch(DataInvalidateException ex) {
                throw ex;
            } catch(ServiceException ex) {
                throw new DataInvalidateException("Incorrect reference mapping for unique key column "+ uniqueKeyMethodName);
            }
        }
    }

    public void validateColumnData(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, JSONObject columnConfig, String column, JSONArray customfield, HashMap<String, Object> columnHeaderMap, String dateFormat) throws JSONException, DataInvalidateException, ParseException {
        int maxLength = columnConfig.getInt("maxLength");
        String csvHeader = (String) columnHeaderMap.get(column);
        csvHeader = (csvHeader==null?csvHeader:csvHeader.replaceAll("\\."," "));//remove '.' from csv Header
        String columnHeader = columnConfig.getString("columnName");
        String data = dataMap.get(column)==null?null:dataMap.get(column).toString();
        Object vDataValue = data;
        
        String ContactNo = "Contact Number",AltContactNo = "Alternate Contact Number";	// Validation for phone numbers only Neeraj
    	if(columnHeader.equals(ContactNo) || columnHeader.equals(AltContactNo)){
        	if(data!=null && data.length() < 10){
        		throw new DataInvalidateException("Data length less than 10 for column "+csvHeader +".");
        	}else if(data!=null && data.length() > 25){
        		throw new DataInvalidateException("Data length more than 25 for column "+csvHeader +".");
        	}
        }
    	
        if(columnConfig.has("validatetype")) {
           String validatetype = columnConfig.getString("validatetype");
           boolean customflag = false;
           if(columnConfig.has("customflag")) {
                customflag = columnConfig.getBoolean("customflag");
           }
           if(validatetype.equalsIgnoreCase("integer")) {
                try {
                    if(!StringUtil.isNullOrEmpty(data)) { // Remove ","(comma) from number
                        data = data.replaceAll(",", "");
                    }
                    if(maxLength>0 && data!=null && data.length() > maxLength) { // Added null value check for data[Sandeep k]
                        throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                    }
                   vDataValue = StringUtil.isNullOrEmpty(data) ? 0 : Integer.parseInt(data);
                } catch(Exception ex) {
                    throw new DataInvalidateException("Incorrect numeric value for "+csvHeader+", Please ensure that value type of "+csvHeader+" matches with the "+ columnHeader+".");
                }
           } else if(validatetype.equalsIgnoreCase("double")) {
                try {
                    if(!StringUtil.isNullOrEmpty(data)) { // Remove ","(comma) from number
                        data = data.replaceAll(",", "");
                        if(columnHeader.equals("Opening Balance")){			//TODO: replace currency symbol or any alphabet from currency header
                        	data = data.replaceAll("[^.0-9]", "");
                        }
                    }
                    if(maxLength>0 && data!=null && data.length() > maxLength) {
                        throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                    }
                    vDataValue = StringUtil.isNullOrEmpty(data) ? 0.0 : Double.parseDouble(data);
                } catch(Exception ex) {
                    throw new DataInvalidateException("Incorrect numeric value for "+csvHeader+", Please ensure that value type of "+csvHeader+" matches with the "+ columnHeader+".");
                }
           } else if(validatetype.equalsIgnoreCase("date")) {
               if(!StringUtil.isNullOrEmpty(data)) {
                    String ldf = dateFormat!=null ? dateFormat : (data.length()>10 ? df_full : df);
                    try {
                        DateFormat sdf = new SimpleDateFormat(ldf);
                        vDataValue = StringUtil.isNullOrEmpty(data) ? null : sdf.parse(data);
                        if(customflag && vDataValue != null) {
                            vDataValue = new SimpleDateFormat(df_customfield).format(vDataValue);
                        }
                    } catch(Exception ex) {
                        throw new DataInvalidateException("Incorrect date format for "+csvHeader+", Please specify values in "+ldf+" format.");
                    }
               } else {
                   vDataValue = null;
               }
           } else if(validatetype.equalsIgnoreCase("time")) {
               if(!StringUtil.isNullOrEmpty(data)) {
                   //@@@ need to uncomment
//                    Pattern pattern = Pattern.compile(EmailRegEx);
//                    if(!pattern.matcher(data).matches()){
//                        throw new DataInvalidateException("Incorrect time format for "+columnConfig.getString("columnName")+" use HH:MM AM or PM");
//                    }
                    vDataValue = data;
               } else {
                    vDataValue = null;
               }
           } else if(validatetype.equalsIgnoreCase("ref")) {
                if(!StringUtil.isNullOrEmpty(data)) {
                    try {
                        String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
                        if(columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid")) {
                            List list = getRefData(requestParams, columnConfig.getString("refModule"), columnConfig.getString("refDataColumn"), columnConfig.getString("refFetchColumn"), columnConfig.getString("configid"), data);
                            if(list.size()==0){
                                if(pref.equalsIgnoreCase("0")){ //Skip Record
                                    throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
                                } else if(pref.equalsIgnoreCase("1")) {
                                    vDataValue = null;  // Put 'null' value to skip column data.
                                    if (columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
                                        throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown, can not set empty data for " + columnHeader +".");
                                    }
                                } else if(pref.equalsIgnoreCase("2")) {
                                    if(!isMasterTable(columnConfig.getString("refModule"))){ // Cant't create entry for ref. module
                                        throw new DataInvalidateException(csvHeader+" entry not present in "+ columnHeader +" list, Please create new "+ columnHeader +" entry for '"+(data.replaceAll("\\.", ""))+"' as it requires some other details.");
                                    }
                                }
                            } else {
                                vDataValue = list.get(0).toString();
                            }
                        } else {
                            throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                        }
                    } catch(ServiceException ex) {
                        throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                    } catch(DataInvalidateException ex) {
                        throw ex;
                    } catch(Exception ex) {
                        throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown.");
                    }
                } else {
                    vDataValue = null;
                }
           } else if(validatetype.equalsIgnoreCase("email")) {
               if(maxLength>0 && data!=null && data.length() > maxLength) {
                   throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
               }
               if(!StringUtil.isNullOrEmpty(data)) {
                    Pattern pattern = Pattern.compile(EmailRegEx);
                    if(!pattern.matcher(data).matches()){
                        throw new DataInvalidateException("Invalid email address for "+csvHeader+".");
                    }
                    vDataValue = data;
               } else {
                    vDataValue = null;
               }
           } else if(validatetype.equalsIgnoreCase("boolean")) {
               if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("1") || data.equalsIgnoreCase("T")) {
                   vDataValue = true;
               } else if(data.equalsIgnoreCase("false") || data.equalsIgnoreCase("0") || data.equalsIgnoreCase("F")) {
                   vDataValue = false;
               } else {
                   throw new DataInvalidateException("Incorrect boolean value for "+csvHeader+".");
               }
           }

           if(vDataValue==null && columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
                throw new DataInvalidateException("Empty data found in "+csvHeader+", Can not set empty data for "+columnHeader+".");
           } else {
               if(customflag) {
                   JSONObject jobj = new JSONObject();
                   if(columnConfig.getString("xtype").equals("4") || columnConfig.getString("xtype").equals("7")) {//Drop down & Multi Select Drop down
                        try {
                            if(vDataValue!=null) {
                                if(!StringUtil.isNullOrEmpty(vDataValue.toString())) {
                                    List list = getCustomComboID(vDataValue.toString(), columnConfig.getInt("pojoHeader"),"id");
                                    vDataValue = list.get(0).toString();
                                }
                            } else {
                                throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                            }
                        } catch(ServiceException ex) {
                            throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                        } catch(DataInvalidateException ex) {
                            throw ex;
                        } catch(Exception ex) {
                            throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown.");
                        }
                   } else if(columnConfig.getString("xtype").equals("8")) {//Reference Drop down & Multi Select Drop down
                        try {
                            if(vDataValue!=null) {
                                if(!StringUtil.isNullOrEmpty(vDataValue.toString())) {
                                    if(columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid")) {
                                        List list = getRefData(requestParams, columnConfig.getString("refModule"), columnConfig.getString("refDataColumn"), columnConfig.getString("refFetchColumn"), columnConfig.getString("configid"), vDataValue);
                                        vDataValue = list.get(0).toString();
                                    } else {
                                        throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                                    }
                                }
                            } else {
                                throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                            }
                        } catch(ServiceException ex) {
                            throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                        } catch(DataInvalidateException ex) {
                            throw ex;
                        } catch(Exception ex) {
                            throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown.");
                        }
                   } else {
                        if(maxLength>0 && data!=null && data.length() > maxLength) {
                            throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                   }
                   }
                   jobj.put(columnConfig.getString("pojoName"), vDataValue==null?"":vDataValue);
                   jobj.put("filedid", columnConfig.getString("pojoHeader"));
                   jobj.put("xtype", columnConfig.getString("xtype"));
                   customfield.put(jobj);
                   if(dataMap.containsKey(column)) {
                       dataMap.remove(column);
                   }
               } else {
                   if(validatetype.equalsIgnoreCase("string") && maxLength>0 && data!=null && data.length() > maxLength) {
                        throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                   }
                   dataMap.put(column, vDataValue);
               }
           }
        } else { // If no validation type then check allow null property[SK]
        	if (vDataValue == null && columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
                throw new DataInvalidateException("Empty data found in " + csvHeader + ". Can not set empty data for " + columnHeader + ".");
            }
        }
    }

    public Object getDefaultValue(JSONObject columnConfig) throws ParseException, JSONException, DataInvalidateException {
        Object defaultValue = columnConfig.get("defaultValue");
        if(columnConfig.has("validatetype")) {
            String validatetype = columnConfig.getString("validatetype");
            if(validatetype.equalsIgnoreCase("integer")) {
                defaultValue = StringUtil.isNullOrEmpty(defaultValue.toString())?0:Integer.parseInt(defaultValue.toString());
            } else if(validatetype.equalsIgnoreCase("double")) {
                defaultValue = StringUtil.isNullOrEmpty(defaultValue.toString())?0.0:Double.parseDouble(defaultValue.toString());
            } else if(validatetype.equalsIgnoreCase("date")) {
                String ddateStr = defaultValue.toString();
                DateFormat sdf = new SimpleDateFormat(ddateStr.length()>10 ? df_full : df);
                if(ddateStr.equals("now")) {
                    defaultValue = new Date();
                } else {
                    defaultValue = StringUtil.isNullOrEmpty(ddateStr)? null : sdf.parse(ddateStr);
                }
            } else if(validatetype.equalsIgnoreCase("boolean")) {
                String data = defaultValue.toString();
                if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("1") || data.equalsIgnoreCase("T")) {
                    defaultValue = true;
                } else if (data.equalsIgnoreCase("false") || data.equalsIgnoreCase("0") || data.equalsIgnoreCase("F")) {
                    defaultValue = false;
                } else {
                    throw new DataInvalidateException("Incorrect default boolean value for "+columnConfig.getString("columnName")+".");
                }
           }
        }
        if (defaultValue == null && columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
            throw new DataInvalidateException("Can not set default empty data for " + columnConfig.getString("columnName")+".");
        }
        return defaultValue;
    }

    public List getRefData(HashMap<String, Object> requestParams, String table, String dataColumn, String fetchColumn, String comboConfigid, Object token) throws ServiceException, DataInvalidateException {
        ArrayList<String> filterNames = new ArrayList<String>();
        ArrayList<Object> filterValues = new ArrayList<Object>();
        filterNames.add(dataColumn);
        filterValues.add(token);
        return importDao.getRefModuleData(requestParams, table, fetchColumn, comboConfigid, filterNames , filterValues);
    }

    public List getCustomComboID(String combovalue, int fieldid,String fetchColumn) throws ServiceException, DataInvalidateException {
        ArrayList filterNames = new ArrayList<String>();
        ArrayList filterValues = new ArrayList<Object>();
        filterNames.add("name");
        filterValues.add(combovalue);
        filterNames.add("fieldid");
        filterValues.add(fieldid);
        return importDao.getCustomComboID(fetchColumn, filterNames, filterValues);
    }

    public String chkNullorEmptywithDatatruncation(String cc ,int dataTruncation) {
        String ret = "";
        if (!StringUtil.isNullOrEmpty(cc)) {
            if(cc.length() > dataTruncation && dataTruncation !=0){
                    cc = cc.substring(0, dataTruncation);
            }
            ret = cc.trim();
        }
        return ret;
    }

    //Function used for XLS preview grid
    public JSONObject parseXLS(String filename, int sheetNo) throws FileNotFoundException, IOException, JSONException{
            JSONObject jobj=new JSONObject();
                    POIFSFileSystem fs      =
            new POIFSFileSystem(new FileInputStream(filename));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(wb);
            HSSFSheet sheet = wb.getSheetAt(sheetNo);

            int startRow=0;
            int maxRow=sheet.getLastRowNum();
            int maxCol=0;
            int noOfRowsDisplayforSample = 20;
            if(noOfRowsDisplayforSample > sheet.getLastRowNum()){
                noOfRowsDisplayforSample = sheet.getLastRowNum();
            }

            JSONArray jArr=new JSONArray();
            try {
                for(int i=0;i <= noOfRowsDisplayforSample;i++) {
                    HSSFRow row = sheet.getRow(i);
                    JSONObject obj=new JSONObject();
                    JSONObject jtemp1 = new JSONObject();
                    if(row==null){
                        continue;
                    }
                    if(i==0) {
                        maxCol=row.getLastCellNum();
                    }
                    for(int cellcount=0; cellcount<maxCol; cellcount++){
                        HSSFCell cell = row.getCell(cellcount);
                        CellReference cref = new CellReference(i, cellcount);
                        String colHeader=cref.getCellRefParts()[2];
                        String val=null;
                        if(cell!=null){
                            switch(cell.getCellType()){
                                case HSSFCell.CELL_TYPE_NUMERIC: 
                                     if(HSSFDateUtil.isCellDateFormatted(cell)){
                                        val=Double.toString(cell.getNumericCellValue());
                                        java.util.Date date1 = HSSFDateUtil.getJavaDate(Double.parseDouble(val));
                                        DateFormat sdf = new SimpleDateFormat(df);
                                        val = sdf.format(date1);
                                     }else{
                                        val=dfmt.format(cell.getNumericCellValue());
                                     }
                                     break;
                                case HSSFCell.CELL_TYPE_STRING: val=cleanHTML(cell.getRichStringCellValue().getString()); break;
                            }
                        }
                        
                        if(i==0){ // List of Headers (Consider first row as Headers)
                            if(val!=null){
                                jtemp1 = new JSONObject();
                                jtemp1.put("header", val==null?"":val);
                                jtemp1.put("index", cellcount);
                                jobj.append("Header", jtemp1);
                            }
                        }
                        obj.put(colHeader,val);
                    }
//                    if(obj.length()>0){ //Don't show blank row in preview grid[SK]
                        jArr.put(obj);
//                    }
                }
            } catch(Exception ex) {
               Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            jobj.put("startrow", startRow);
            jobj.put("maxrow", maxRow);
            jobj.put("maxcol", maxCol);
            jobj.put("index", sheetNo);
            jobj.put("data", jArr);
            jobj.put("filename", filename);

            jobj.put("msg", "XLS has been successfully uploaded");
            jobj.put("lsuccess", true);
            jobj.put("valid", true);
            return jobj;
    }

    public JSONObject parseXLS1(String filename, int sheetNo,int startindex) throws FileNotFoundException, IOException, JSONException{
            JSONObject jobj=new JSONObject();
                    POIFSFileSystem fs      =
            new POIFSFileSystem(new FileInputStream(filename));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(wb);
            HSSFSheet sheet = wb.getSheetAt(sheetNo);
            ArrayList<String> arr = new ArrayList<String>();
            int startRow=0;
            int maxRow=sheet.getLastRowNum();
            int maxCol=0;

            JSONArray jArr=new JSONArray();
            try {
                for(int i=startindex;i<=sheet.getLastRowNum();i++) {
                    HSSFRow row = sheet.getRow(i);
                    JSONObject obj=new JSONObject();
                    JSONObject jtemp1 = new JSONObject();
                    if(row==null){
                        continue;
                    }
                    if(i==startindex){
                        maxCol=row.getLastCellNum();
                    }
                    for(int j=0; j<maxCol; j++){
                        HSSFCell cell = row.getCell(j);
                        String val=null;
                        if(cell==null){
                               arr.add(val);
                               continue;
                        };
                        String colHeader=new CellReference(i, j).getCellRefParts()[2];
                        switch(cell.getCellType()){
                            case HSSFCell.CELL_TYPE_NUMERIC: 
                                 if(HSSFDateUtil.isCellDateFormatted(cell)){
                                    val=Double.toString(cell.getNumericCellValue());
                                    java.util.Date date1 = HSSFDateUtil.getJavaDate(Double.parseDouble(val));
                                    DateFormat sdf = new SimpleDateFormat(df);
                                    val = sdf.format(date1);
                                 }else{
                                    val=dfmt.format(cell.getNumericCellValue());
                                 }
                                 break;
                            case HSSFCell.CELL_TYPE_STRING: val=cleanHTML(cell.getRichStringCellValue().getString()); break;
                        }
                        if(i==startindex){ // List of Headers (consider startindex row as a headers)
                            if(val!=null){
                            jtemp1 = new JSONObject();
                            jtemp1.put("header", val);
                            jtemp1.put("index", j);
                            jobj.append("Header", jtemp1);
                            obj.put(colHeader,val);
                            }
                            arr.add(val);
                        } else {
                            if(arr.get(j)!=null)
                                obj.put(arr.get(j),val);
                        }

                    }
                    if(obj.length()>0){
                    jArr.put(obj);
                }

                }
            } catch(Exception ex) {
               Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            jobj.put("startrow", startRow);
            jobj.put("maxrow", maxRow);
            jobj.put("maxcol", maxCol);
            jobj.put("index", sheetNo);
            jobj.put("data", jArr);
            jobj.put("filename", filename);

            jobj.put("msg", "XLS has been successfully uploaded");
            jobj.put("lsuccess", true);
            jobj.put("valid", true);
            return jobj;
    }

    public JSONObject validateFileData(HashMap<String, Object> requestParams) {
        JSONObject jobj = new JSONObject();
        String msg = "";
        boolean issuccess = true;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean commitedEx = false;

        int total = 0, failed = 0, fileSize=0;
        String fileName = "", extn = "";
        Modules module = null;
        String exceededLimit = "no", channelName="";
        try {            
            String mode = (String) requestParams.get("modName");
            fileName = (String) requestParams.get("filename");
            extn = fileName.substring(fileName.lastIndexOf(".")+1);
            channelName = "/ValidateFile/"+fileName;

            Object extraObj = requestParams.get("extraObj");
            JSONObject extraParams = (JSONObject) requestParams.get("extraParams");

            String jsondata = (String) requestParams.get("resjson");
            JSONObject rootcsvjobj = new JSONObject(jsondata);
            JSONArray mapping = rootcsvjobj.getJSONArray("root");

            String dateFormat=null, dateFormatId = (String) requestParams.get("dateFormat");
            if(extn.equalsIgnoreCase("csv") && !StringUtil.isNullOrEmpty(dateFormatId)){
                KWLDateFormat kdf = (KWLDateFormat) KwlCommonTablesDAOObj.getClassObject(KWLDateFormat.class.getName(), dateFormatId);
                dateFormat = kdf!=null ? kdf.getJavaForm() : null;
            }

            String classPath="", primaryKey="", uniqueKeyMethodName="", uniqueKeyHbmName="";
            try {
                List list = importDao.getModuleObject(mode);
                module = (Modules) list.get(0); //Will throw null pointer if no module entry found
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+mode);
            }

            try {
                classPath = module.getPojoClassPathFull().toString();
                primaryKey = module.getPrimaryKey_MethodName().toString();
            } catch (Exception ex) {
                throw new DataInvalidateException("Please set proper properties for module "+mode);
            }
            uniqueKeyMethodName = module.getUniqueKey_MethodName();
            uniqueKeyHbmName = module.getUniqueKey_HbmName();

            JSONArray columnConfig = getModuleColumnConfig(module.getId(), (String) requestParams.get("companyid"));
            String tableName = importDao.getTableName(fileName);
            KwlReturnObject kresult = importDao.getFileData(tableName, new HashMap<String, Object>());
            List fileDataList = kresult.getEntityList();
            Iterator itr = fileDataList.iterator();

            importDao.markRecordValidation(tableName, -1, 1, "", ""); //reset all invalidation
            JSONArray recordJArr = new JSONArray(), columnsJArr = new JSONArray(), DataJArr = new JSONArray();
            if (itr.hasNext()) { //
                Object[] fileData = (Object[]) itr.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("header", "Row No.");
                jtemp.put("dataIndex", "col0");
                jtemp.put("width", 50);
                columnsJArr.put(jtemp);

                for (int i = 1; i < fileData.length - 3; i++) {    //Discard columns, id at index 0 and isvalid,validationlog at last 2.
                    jtemp = new JSONObject();
                    jtemp.put("header", fileData[i] == null ? "" : fileData[i].toString());
                    jtemp.put("dataIndex", "col" + i);
                    columnsJArr.put(jtemp);
                }

                jtemp = new JSONObject();
                jtemp.put("header", "Validation Log");
//                jtemp.put("hidden", true);
                jtemp.put("dataIndex", "validateLog");
                columnsJArr.put(jtemp);


                //Create record Obj for grid's store
                for (int i = 0; i < fileData.length-1; i++) {
                    jtemp = new JSONObject();
                    jtemp.put("name", "col"+i);
                    recordJArr.put(jtemp);
                }
                jtemp = new JSONObject();
                jtemp.put("name", "validateLog");
                recordJArr.put(jtemp);
            }

            try {
                jobj.put("record", recordJArr);
                jobj.put("columns", columnsJArr);
                jobj.put("data", DataJArr);
                jobj.put("count", failed);
                jobj.put("valid", 0);
                jobj.put("totalrecords", total);
                jobj.put("isHeader", true);
                jobj.put("finishedValidation", false);
                ServerEventManager.publish(channelName, jobj.toString(), (ServletContext) requestParams.get("servletContext"));
            } catch(Exception ex) {
                throw ex;
            }

            fileSize = fileDataList.size()-1;
            fileSize = fileSize>=importLimit?importLimit:fileSize; // fileSize used for showing progress bar[Client Side]
            
            jobj.put("isHeader", false);
            int recIndex = 0;
            while (itr.hasNext()) {
                Object[] fileData= (Object[])itr.next();
                recIndex= (Integer) fileData[0];
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                HashMap<String,Object> columnHeaderMap = new HashMap<String,Object>();
                HashMap<String,Object> columnCSVindexMap = new HashMap<String,Object>();
                JSONArray customfield = new JSONArray();
                for (int k = 0; k < mapping.length(); k++) {
                    JSONObject mappingJson = mapping.getJSONObject(k);
                    String datakey = mappingJson.getString("columnname");
                    Object dataValue = cleanHTML((String) fileData[mappingJson.getInt("csvindex")+1]); //+1 for id column at index-0
                    dataMap.put(datakey, dataValue);
                    columnHeaderMap.put(datakey, mappingJson.getString("csvheader"));
                    columnCSVindexMap.put(datakey, mappingJson.getInt("csvindex")+1);
                }

                for (int j=0; j< extraParams.length(); j++) {
                    String datakey = (String) extraParams.names().get(j);
                    Object dataValue = extraParams.get(datakey);
                    dataMap.put(datakey, dataValue);
                }

                try {
                    if(total>=importLimit){
                        exceededLimit = "yes";
                        break;
                    }
                    //Update processing status at client side
                    if(total>0 && total%10==0){
                        try {
                            ServerEventManager.publish(channelName, "{parsedCount:"+total+",invalidCount:"+failed+", fileSize:"+fileSize+", finishedValidation:false}", (ServletContext) requestParams.get("servletContext"));
                        } catch(Exception ex) {
                            throw ex;
                        }
                    }

                    CheckUniqueRecord(requestParams, dataMap, classPath, uniqueKeyMethodName, uniqueKeyHbmName);
                    validateDataMAP2(requestParams, dataMap, columnConfig, customfield, columnHeaderMap, columnCSVindexMap, dateFormat);
                } catch(Exception ex) {
                    failed++;
                    String errorMsg = ex.getMessage(), invalidColumns = "";
                     try {
                        JSONObject errorLog = new JSONObject(errorMsg);
                        errorMsg = errorLog.getString("errorMsg");
                        invalidColumns = errorLog.getString("invalidColumns");
                    } catch (JSONException jex) {
                    }

                    importDao.markRecordValidation(tableName, recIndex, 0, errorMsg, invalidColumns);
                    JSONObject jtemp = new JSONObject();
                    for (int i = 0; i < fileData.length-2; i++) {
                        jtemp.put("col"+i, fileData[i] == null ? "" : fileData[i].toString());
                    }
                    jtemp.put("invalidcolumns", invalidColumns);
                    jtemp.put("validateLog", errorMsg);
                    DataJArr.put(jtemp);

//                    try {
//                        jtemp.put("count", failed);
//                        jtemp.put("totalrecords", total+1);
//                        jtemp.put("fileSize", fileSize);
//                        jtemp.put("finishedValidation", false);
//                        ServerEventManager.publish(channelName, jtemp.toString(), (ServletContext) requestParams.get("servletContext"));
//                    } catch(Exception dex) {
//                        throw dex;
//                    }
                }
                total++;
            }

            int success = total-failed;
            if(total == 0) {
                msg = "Empty file.";
            } else if(success == 0) {
                msg = "All the records are invalid.";
            } else if(success == total) {
                msg = "All the records are valid.";
            } else {
                msg = ""+success+" valid record"+(success>1?"s":"")+"";
                msg += (failed==0?".":" and "+failed+" invalid record"+(failed>1?"s":"")+".");
            }

            jobj.put("record", recordJArr);
            jobj.put("columns", columnsJArr);
            jobj.put("data", DataJArr);
            jobj.put("count", failed);
            jobj.put("valid", success);
            jobj.put("totalrecords", total);

            try {
                ServerEventManager.publish(channelName, "{parsedCount:"+total+",invalidCount:"+failed+", fileSize:"+fileSize+", finishedValidation:true}", (ServletContext) requestParams.get("servletContext"));
            } catch(Exception ex) {
                throw ex;
            }

            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
        } catch (Exception e) {
            if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            issuccess = false;
            msg = ""+e.getMessage();
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj.put("exceededLimit", exceededLimit);
            } catch (JSONException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jobj;
    }

    public JSONObject importFileData(HashMap<String, Object> requestParams) {
        JSONObject jobj = new JSONObject();
        String msg = "";
        boolean issuccess = true;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean commitedEx = false;

        int total = 0, failed = 0;
        String fileName = "", tableName="", extn="";
        Modules module = null;

        try {
            String mode = (String) requestParams.get("modName");
            fileName = (String) requestParams.get("filename");
            extn = fileName.substring(fileName.lastIndexOf(".")+1);
            StringBuilder failedRecords = new StringBuilder();

            String dateFormat=null, dateFormatId = (String) requestParams.get("dateFormat");
            if(extn.equalsIgnoreCase("csv") && !StringUtil.isNullOrEmpty(dateFormatId)){
                KWLDateFormat kdf = (KWLDateFormat) KwlCommonTablesDAOObj.getClassObject(KWLDateFormat.class.getName(), dateFormatId);
                dateFormat = kdf!=null ? kdf.getJavaForm() : null;
            }

            Object extraObj = requestParams.get("extraObj");
            JSONObject extraParams = (JSONObject) requestParams.get("extraParams");
            
            String jsondata = (String) requestParams.get("resjson");
            JSONObject rootcsvjobj = new JSONObject(jsondata);
            JSONArray mapping = rootcsvjobj.getJSONArray("root");

            String classPath="", primaryKey="", uniqueKeyMethodName="", uniqueKeyHbmName="";
            try {
                List list = importDao.getModuleObject(mode);
                module = (Modules) list.get(0); //Will throw null pointer if no module entry found
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+mode);
            }

            try {
                classPath = module.getPojoClassPathFull().toString();
                primaryKey = module.getPrimaryKey_MethodName().toString();
            } catch (Exception ex) {
                throw new DataInvalidateException("Please set proper properties for module "+mode);
            }
            uniqueKeyMethodName = module.getUniqueKey_MethodName();
            uniqueKeyHbmName = module.getUniqueKey_HbmName();

            JSONArray columnConfig = getModuleColumnConfig(module.getId(), (String) requestParams.get("companyid"));
            tableName = importDao.getTableName(fileName);
            HashMap<String, Object> filterParams = new HashMap<String, Object>();
//            filterParams.put("isvalid", 1); //To fetch valid records
            KwlReturnObject kresult = importDao.getFileData(tableName, filterParams); //Fetch all valid records
            List fileDataList = kresult.getEntityList();
            Iterator itr = fileDataList.iterator();
            if(itr.hasNext()){
                Object[] fileData= (Object[])itr.next(); //Skip header row
                failedRecords.append(createCSVrecord(fileData)+"\"Error Message\"");//failedRecords.append("\"Row No.\","+createCSVrecord(fileData)+"\"Error Message\"");
            }
            int recIndex = 0;
            importDao.markRecordValidation(tableName, -1, 1, "", ""); //reset all invalidation
            while (itr.hasNext()) {
                total++;
                Object[] fileData= (Object[])itr.next();
                recIndex= (Integer) fileData[0];
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                HashMap<String,Object> columnHeaderMap = new HashMap<String,Object>();
                HashMap<String,Object> columnCSVindexMap = new HashMap<String,Object>();
                JSONArray customfield = new JSONArray();
                for (int k = 0; k < mapping.length(); k++) {
                    JSONObject mappingJson = mapping.getJSONObject(k);
                    String datakey = mappingJson.getString("columnname");
                    Object dataValue = cleanHTML((String) fileData[mappingJson.getInt("csvindex")+1]); //+1 for id column at index-0
                    dataMap.put(datakey, dataValue);
                    columnHeaderMap.put(datakey, mappingJson.getString("csvheader"));
                    columnCSVindexMap.put(datakey, mappingJson.getInt("csvindex")+1);
                }

                for (int j=0; j< extraParams.length(); j++) {
                    String datakey = (String) extraParams.names().get(j);
                    Object dataValue = extraParams.get(datakey);
                    dataMap.put(datakey, dataValue);
                }

                Object object = null;
                try {
                    CheckUniqueRecord(requestParams, dataMap, classPath, uniqueKeyMethodName, uniqueKeyHbmName);
                    validateDataMAP2(requestParams, dataMap, columnConfig, customfield, columnHeaderMap, columnCSVindexMap, dateFormat);
                    object = importDao.saveRecord(requestParams, dataMap, null, mode, classPath, primaryKey, extraObj, customfield);
                } catch(Exception ex) {
                    failed++;
                    String errorMsg = ex.getMessage(), invalidColumns = "";
                     try {
                        JSONObject errorLog = new JSONObject(errorMsg);
                        errorMsg = errorLog.getString("errorMsg");
                        invalidColumns = errorLog.getString("invalidColumns");
                    } catch (JSONException jex) {
                    }
                    failedRecords.append("\n"+createCSVrecord(fileData)+"\""+errorMsg+"\"");//failedRecords.append("\n"+(total)+","+createCSVrecord(fileData)+"\""+ex.getMessage()+"\"");
                    importDao.markRecordValidation(tableName, recIndex, 0, errorMsg, invalidColumns);
                }
            }

            if(failed > 0) {
                createFailureFiles(fileName, failedRecords, ".csv");
            }

            int success = total-failed;
            if(total == 0) {
                msg = "Empty file.";
            } else if(success == 0) {
                msg = "Failed to import all the records.";
            } else if(success == total) {
                msg = "All records are imported successfully.";
            } else {
                msg = "Imported "+success+" record"+(success>1?"s":"")+" successfully";
                msg += (failed==0?".":" and failed to import "+failed+" record"+(failed>1?"s":"")+".");
            }

            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
        } catch (Exception e) {
            if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            issuccess = false;
            msg = ""+e.getMessage();
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DefaultTransactionDefinition ldef = new DefaultTransactionDefinition();
            ldef.setName("import_Tx");
            ldef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus lstatus = txnManager.getTransaction(ldef);
            boolean exCommit = false;
            try {
                //Insert Integration log
                HashMap<String, Object> logDataMap = new HashMap<String, Object>();
                String logId = (String) requestParams.get("logId");
                if(!StringUtil.isNullOrEmpty(logId)){
                    logDataMap.put("Id", logId);
                }
                logDataMap.put("FileName", ImportLog.getActualFileName(fileName));
                logDataMap.put("StorageName", fileName);
                logDataMap.put("Log", msg);
                logDataMap.put("Type", fileName.substring(fileName.lastIndexOf(".")+1));
                logDataMap.put("TotalRecs", total);
                logDataMap.put("Rejected", issuccess?failed:total);// if fail then rejected = total
                logDataMap.put("Module", module.getId());
                logDataMap.put("ImportDate", new Date());
                logDataMap.put("User", (String) requestParams.get("userid"));
                logDataMap.put("Company", (String) requestParams.get("companyid"));
                importDao.saveImportLog(logDataMap);

                importDao.removeFileTable(tableName);//Remove table after importing all records
                try {
                    txnManager.commit(lstatus);
                }catch(Exception ex){
                    exCommit = true;
                    throw ex;
                }
            } catch (Exception ex) {
                if(!exCommit) { //if exception occurs during commit then dont call rollback
                    txnManager.rollback(lstatus);
                }
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj.put("totalrecords", total);
                jobj.put("successrecords", total-failed);
                jobj.put("failedrecords", failed);
                jobj.put("filename", ImportLog.getActualFileName(fileName));
            } catch (JSONException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jobj;
    }

    public void validateDataMAP2(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, JSONArray columnConfigArray, JSONArray customfield, HashMap<String, Object> columnHeaderMap,  HashMap<String, Object> columnCSVindexMap, String dateFormat) throws DataInvalidateException {
        String errorMsg = "", invalidColumns="";
        for (int k = 0; k < columnConfigArray.length(); k++) {
            JSONObject columnConfig = null;
            String column = "";
            try {
                columnConfig = columnConfigArray.getJSONObject(k);
                column = columnConfig.getString("pojoName");

                if (dataMap.containsKey(column)) {
                    validateColumnData(requestParams, dataMap, columnConfig, column, customfield, columnHeaderMap, dateFormat);
                } else {
                    if (columnConfig.has("defaultValue")) {
                        dataMap.put(column, getDefaultValue(columnConfig));
                    } else if(columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
                        throw new DataInvalidateException("Data required for field "+column);
                    }
                }
            } catch (Exception ex) {
                errorMsg += ex.getMessage();
                invalidColumns += ("col"+columnCSVindexMap.get(column)+",");
            }
        }
        if(errorMsg.length()>0) {
            try {
                JSONObject errorLog = new JSONObject();
                errorLog.put("errorMsg", errorMsg);
                errorLog.put("invalidColumns", invalidColumns);
                errorMsg = errorLog.toString();
            } catch (JSONException ex) {
            }
            throw new DataInvalidateException(errorMsg);
        }
    }

    public int dumpXLSFileData(String filename, int sheetNo, int startindex) throws ServiceException {
        int dumpedRows = 0;
        try {
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "xlsfiles";
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(destinationDirectory + "/" + filename));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(sheetNo);
            int maxRow = sheet.getLastRowNum();
            int maxCol = 0;
            String tableName = importDao.getTableName(filename);
            for (int i = startindex; i <= maxRow; i++) {
                HSSFRow row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if (i == startindex) {
                    maxCol = row.getLastCellNum();  //Column Count
                }
                ArrayList<String> dataArray = new ArrayList<String>();
                JSONObject dataObj=new JSONObject();
                for (int j = 0; j < maxCol; j++) {
                    HSSFCell cell = row.getCell(j);
                    String val = null;
                    if (cell == null) {
                        dataArray.add(val);
                        continue;
                    }
                    String colHeader = new CellReference(i, j).getCellRefParts()[2];
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:                            
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                val = Double.toString(cell.getNumericCellValue());
                                java.util.Date date1 = HSSFDateUtil.getJavaDate(Double.parseDouble(val));
                                DateFormat sdf = new SimpleDateFormat(df);//(df_full); //BUG:16085
                                val = sdf.format(date1);
                            }else{
                            	val = dfmt.format(cell.getNumericCellValue());
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            val = cleanHTML(cell.getRichStringCellValue().getString());
                            break;
                    }
                    dataObj.put(colHeader,val);
                    dataArray.add(val); //Collect row data
                }
                //Insert Query
                if(dataObj.length()>0){ // Empty row check (if lenght==0 then all columns are empty)
                    dumpedRows += importDao.dumpFileRow(tableName, dataArray.toArray());
                }
            }
        } catch (IOException ex) {
            throw ServiceException.FAILURE("dumpXLSFileData: "+ex.getMessage(), ex);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("dumpXLSFileData: "+ex.getMessage(), ex);
        }
        return dumpedRows;
    }

    public int dumpCSVFileData(String filename, String delimiterType, int startindex) throws DataInvalidateException {
        int dumpedRows = 0;
        try {
            CsvReader csvReader = null;
            FileInputStream fstream = null;
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            File csv = new File(destinationDirectory + "/" + filename);
            fstream = new FileInputStream(csv);
            csvReader = new CsvReader(new InputStreamReader(fstream), delimiterType);
//            csvReader.readHeaders();
            String tableName = importDao.getTableName(filename);
            while (csvReader.readRecord()) {
                ArrayList<String> dataArray = new ArrayList<String>();
                for (int i = 0; i < csvReader.getColumnCount(); i++) {
                    dataArray.add(cleanHTML(csvReader.get(i)));
                }
                dumpedRows += importDao.dumpFileRow(tableName, dataArray.toArray());
            }
        } catch (IOException ex) {
            throw new DataInvalidateException("Invalid file, unable to read record at line "+(dumpedRows+1), ex);
        } catch (Exception ex) {
            throw new DataInvalidateException("Invalid file, unable to parse record at line "+(dumpedRows+1), ex);
        }
        return dumpedRows;
    }

    public String createCSVrecord(Object[] listArray) {
        String rec = "";
        for(int i=1; i<listArray.length-3; i++){    //Discard columns id at index 0 and isvalid,invalidColumns, validationlog at last 3 indexes.
            rec += "\""+(listArray[i]==null?"":listArray[i].toString())+"\",";
        }
        return rec;
    }

    public String addPendingImportLog(HashMap<String, Object> requestParams){
        String logId = null;
        try {
            //Insert Integration log
            String fileName = (String) requestParams.get("filename");
            String Module = (String) requestParams.get("modName");
            try {
                List list = importDao.getModuleObject(Module);
                Modules module = (Modules) list.get(0); //Will throw null pointer if no module entry found
                Module = module.getId();
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+Module);
            }
            HashMap<String, Object> logDataMap = new HashMap<String, Object>();
            logDataMap.put("FileName", ImportLog.getActualFileName(fileName));
            logDataMap.put("StorageName", fileName);
            logDataMap.put("Log", "Pending");
            logDataMap.put("Type", fileName.substring(fileName.lastIndexOf(".")+1));
            logDataMap.put("Module", Module);
            logDataMap.put("ImportDate", new Date());
            logDataMap.put("User", (String) requestParams.get("userid"));
            logDataMap.put("Company", (String) requestParams.get("companyid"));
            ImportLog importlog = (ImportLog)importDao.saveImportLog(logDataMap);
            logId = importlog.getId();
        } catch (Exception ex) {
            logId = null;
        }
        return logId;
    }
    
    public HashMap<String, Object> getImportRequestParams(HttpServletRequest request) throws SessionExpiredException{
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("modName", request.getParameter("modName"));
        requestParams.put("moduleName", request.getParameter("moduleName"));
        requestParams.put("delimiterType", request.getParameter("delimiterType"));
        requestParams.put("filename", request.getParameter("filename"));
        requestParams.put("resjson", request.getParameter("resjson"));
        requestParams.put("sheetindex", request.getParameter("sheetindex"));
        requestParams.put("onlyfilename", request.getParameter("onlyfilename"));
        requestParams.put("dateFormat", request.getParameter("dateFormat"));
        requestParams.put("masterPreference", request.getParameter("masterPreference"));

        requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
        requestParams.put("userid", sessionHandlerImplObj.getUserid(request));
        requestParams.put("doAction", request.getParameter("do"));
        //requestParams.put("timezome", TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request)));
        return requestParams;
    }

    public DateFormat getGMTDateFormatter(String df, HashMap<String, Object> requestParams){
        SimpleDateFormat sdf=new SimpleDateFormat(df);
        sdf.setTimeZone((TimeZone) requestParams.get("timezome"));
        return sdf;
    }
}
