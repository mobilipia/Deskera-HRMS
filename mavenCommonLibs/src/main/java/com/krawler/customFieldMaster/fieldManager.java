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

package com.krawler.customFieldMaster;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CustomColumnFormulae;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
//import com.krawler.crm.dbhandler.crmHandler;
//import com.krawler.crm.dbhandler.crmReports;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Transaction;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

/*
 *
 * @author krawler
 *
 *
 *
*/


public class fieldManager implements MessageSourceAware {
	private MessageSource messageSource;
	
    public JSONObject getCustomColumnJSON(HttpServletRequest request, JSONObject tmpObj, String modulName, String recId, String moduleId) throws ServiceException, JSONException, SessionExpiredException {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ArrayList list = this.getFieldData(session, modulName, recId);
            ArrayList list1 = this.getOnlyFieldName(session, request, moduleId);
            for (int j = 0; j < list1.size(); j++) {
                Object[] item1 = list1.toArray();
                for (int i = 0; i < list.size(); i++) {
                    Object[] item = (Object[]) list.get(i);
                    if (StringUtil.equal(item1[j].toString(), item[0].toString())) {
                        if ((StringUtil.equal(item[3].toString(), "3")) && !(StringUtil.equal(item[1].toString(), "undefined")) && !(StringUtil.isNullOrEmpty(item[1].toString()))) {
                            tmpObj.put(item[0].toString(), preferenceDate(session, request, new Date(item[1].toString()), 0));
                        } else if (!(StringUtil.isNullOrEmpty(request.getParameter("config"))) && (StringUtil.equal(item[3].toString(), "4")) && !(StringUtil.equal(item[1].toString(), "undefined")) && !(StringUtil.isNullOrEmpty(item[1].toString()))) {
                            tmpObj.put(item[0].toString(), this.customComboValue(session, item[1].toString()));
                        } else {
                            tmpObj.put(item[0].toString(), item[1].toString());
                        }
                        break;
                    } else {
                        tmpObj.put(item1[j].toString(), "");
                    }

                }
                if (list.size() < 1) {
                    tmpObj.put(item1[j].toString(), "");
                }
            }
        } catch(Exception e) {
            if (tx!=null) tx.rollback();
            throw ServiceException.FAILURE("crmManager.getCustomColumnJSON", e);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return tmpObj;
    }

    public ArrayList getFieldData(Session session,String module,String moduleRecId){
        ArrayList returnList = new ArrayList();
              String query1 = "show tables ";
                    SQLQuery sql1 = session.createSQLQuery(query1);
                    ArrayList l = new ArrayList();
                    l = (ArrayList) sql1.list();
                   if(!l.contains(module+"cstm")){
                        query1="CREATE TABLE  `"+module+"cstm`(`modulerecid` varchar(36) NOT NULL,`fieldparamid` int(11) NOT NULL, `fieldvalue` varchar(255) NOT NULL,   `fieldname` varchar(255) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                        sql1 = session.createSQLQuery(query1);
                        sql1.executeUpdate();
                    }
       //     String query = "SELECT `fieldname`,`fieldvalue`,`fieldtype` FROM " + module + "cstm  where modulerecid = ?";
            String query ="SELECT cm.fieldname,cm.fieldvalue,cm.fieldparamid, fp.fieldtype FROM  " + module + "cstm as cm inner join fieldparams as fp on cm.fieldparamid = fp.id where modulerecid = ?" ;
            SQLQuery sql = session.createSQLQuery(query);
            sql.setParameter(0, moduleRecId);
            returnList = (ArrayList) sql.list();


            return returnList;

    }

    public ArrayList getFieldName(Session session,String module,String moduleRecId){
        ArrayList returnList = new ArrayList();
        String query = "SELECT distinct `fieldname` FROM " + module + "cstm  ";
        SQLQuery sql = session.createSQLQuery(query);
        returnList = (ArrayList) sql.list();

        return returnList;

    }

    public ArrayList getOnlyFieldName(Session session, HttpServletRequest request, String moduleId) throws SessionExpiredException, ServiceException {
        ArrayList returnList = new ArrayList();
        try {
            String companyid = AuthHandler.getCompanyid(request);
            String query = "SELECT distinct `fieldname` FROM fieldparams where moduleid=? and companyid = ?";
            SQLQuery sql = session.createSQLQuery(query);
            sql.setParameter(0, moduleId);
            sql.setParameter(1, companyid);
            returnList = (ArrayList) sql.list();
        } catch (Exception e) {
            throw ServiceException.FAILURE("fieldManager.getOnlyFieldName", e);
        }
        return returnList;
    }

    public String customComboValue(Session session,String df) {
        String status = "";
        if (df != null) {
            ArrayList returnList = new ArrayList();
            String query = "SELECT  `name` FROM fieldComboData where id = ?";
            SQLQuery sql = session.createSQLQuery(query);
            sql.setParameter(0, df);
            returnList = (ArrayList) sql.list();
            Object[] item1 = returnList.toArray();
            status=item1[0].toString();
        }
        return status;
    }
    public JSONObject applyColumnFormulae(HttpServletRequest request, JSONObject tmpObj, String moduleid, String modName) throws ServiceException, JSONException {
        String fieldname = "";
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            String companyid = AuthHandler.getCompanyid(request);
            String operatorRegex = "[\\+\\-\\*\\/]"; // Regex to get find the operators in the formulae.
            String operandArr[] = null;
            String calStr = "";
            String Hql = "select formulae,fieldname from CustomColumnFormulae where companyid.companyID=? and moduleid=? ";
            List list = HibernateUtil.executeQuery(session, Hql, new Object[]{companyid, moduleid});
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                String formulae = (String) row[0];
                fieldname = (String) row[1];

                Object invoker = null;
                String primaryKey = "";
                java.lang.reflect.Method objMethod;
                calStr = formulae.replaceAll(operatorRegex, ",");
                operandArr = calStr.split(",");
                Class cl = Class.forName("com.krawler.crm.database.tables.Crm"+modName+"");
                if (modName.equals("Lead")) {
                    primaryKey = "leadid";
                } else if (modName.equals("Contact")) {
                    primaryKey = "contactid";
                } else if (modName.equals("Product")) {
                    primaryKey = "productid";
                } else if (modName.equals("Account")) {
                    primaryKey = "accountid";
                } else if (modName.equals("Opportunity")) {
                    primaryKey = "oppid";
                }
                invoker = session.get(cl, tmpObj.getString(primaryKey));
                if (invoker != null) {
                    for (int i = 0; i < operandArr.length; i++) {
                        String methodStr = operandArr[i].substring(0, 1).toUpperCase() + operandArr[i].substring(1).toLowerCase(); // Make first letter of operand capital.
                        objMethod = cl.getMethod("get" + methodStr + ""); // Gets the value of the operand
                        String operand = (String) objMethod.invoke(invoker);
                        if (StringUtil.isNullOrEmpty(operand)) {
                            operand = "0";
                        }
                        formulae = formulae.replaceAll(operandArr[i], operand); //Put the value in the formulae.
                    }
                }
                ScriptEngineManager mgr = new ScriptEngineManager();
                ScriptEngine engine = mgr.getEngineByName("js");
                double ans = (Double) engine.eval(formulae);
                String custom_ans = !String.valueOf(ans).equals("NaN") ? String.valueOf(ans) : "";
                tmpObj.put(fieldname, currencyRender(custom_ans, session, request));
            }
        } catch (IllegalAccessException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (JSONException ex) {
            tmpObj.put(fieldname, "");
        } catch (ScriptException ex) {
            tmpObj.put(fieldname, "");
        } catch (Exception ex) {
            tmpObj.put(fieldname, "");
        } finally {
            HibernateUtil.closeSession(session);
        }
        return tmpObj;
    }

    public boolean storeCustomFields(JSONArray jarray,String modulename,boolean  isNew,String modulerecid){
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
        for(int i =0;i<jarray.length();i++){
            try {
                JSONObject jobj = jarray.getJSONObject(i);
                Iterator ittr = jobj.keys();
                String fieldid = "";
                String fieldName = "";
                String fieldValue = "";
                while(ittr.hasNext()){
                    Object obj = ittr.next();
                    if(obj.toString().equals("filedid")){
                            fieldid = jobj.getString("filedid");
                    }else{
                           fieldName = obj.toString();

                    }
                }

                    String query1 = "show tables ";
                    SQLQuery sql1 = session.createSQLQuery(query1);
                    ArrayList l = new ArrayList();
                    l = (ArrayList) sql1.list();
                   if(!l.contains(modulename+"cstm")){
                        query1="CREATE TABLE  `"+modulename+"cstm`(`modulerecid` varchar(36) NOT NULL,`fieldparamid` int(11) NOT NULL, `fieldvalue` varchar(255) NOT NULL,   `fieldname` varchar(255) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                        sql1 = session.createSQLQuery(query1);
                        sql1.executeUpdate();
                    }
                   fieldValue = jobj.getString(fieldName);
                    String query =  "SELECT modulerecid FROM "+modulename+"cstm where modulerecid = ? and fieldparamid = ?";
                    SQLQuery sql = session.createSQLQuery(query);
                    sql.setParameter(0,modulerecid);
                    sql.setParameter(1, fieldid);
                    ArrayList list = (ArrayList)sql.list();
                    boolean recExists = false;
                    if(list.size() > 0){
                        recExists = true;
                    }

                   if((isNew || !recExists) && (!fieldValue.equals("undefined"))){
                        query = "insert into "+modulename+"cstm values(?,?,?,?)";
                   }else{
                       query = "update "+modulename+"cstm set fieldvalue=? where modulerecid=? and fieldparamid = ?";
                   }
                    sql = session.createSQLQuery(query);
                    if((isNew || !recExists) && (!fieldValue.equals("undefined"))){
                        sql.setParameter(0, modulerecid);
                        sql.setParameter(1, fieldid);
                        sql.setParameter(2, fieldValue);
                        sql.setParameter(3, fieldName);
                    }else{
                        sql.setParameter(0, fieldValue);
                        sql.setParameter(1, modulerecid);
                        sql.setParameter(2, fieldid);

                    }
                    sql.executeUpdate();


            } catch (Exception ex) {
                Logger.getLogger(fieldManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        } catch(Exception e) {
            if (tx!=null) tx.rollback();
            Logger.getLogger(fieldManager.class.getName()).log(Level.SEVERE, null, e);
        } finally {
                HibernateUtil.closeSession(session);
        }
    return true;
    }

    // for converted lead
    public boolean storeCustomFields(JSONArray jarray, String modulename, boolean isNew, String modulerecid, ArrayList ll) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
        for (int i = 0; i < jarray.length(); i++) {
            try {
                JSONObject jobj = jarray.getJSONObject(i);
                Iterator ittr = jobj.keys();
                String fieldid = "";
                String fieldName = "";
                String fieldValue = "";
                while (ittr.hasNext()) {
                    Object obj = ittr.next();
                    if (obj.toString().equals("filedid")) {
                        fieldid = jobj.getString("filedid");
                    } else {
                        fieldName = obj.toString();

                    }
                }

                String query1 = "show tables ";
                SQLQuery sql1 = session.createSQLQuery(query1);
                ArrayList l = new ArrayList();
                l = (ArrayList) sql1.list();
                if (!l.contains(modulename + "cstm")) {
                    query1 = "CREATE TABLE  `" + modulename + "cstm`(`modulerecid` varchar(36) NOT NULL,`fieldparamid` int(11) NOT NULL, `fieldvalue` varchar(255) NOT NULL,   `fieldname` varchar(255) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                    sql1 = session.createSQLQuery(query1);
                    sql1.executeUpdate();
                }
                fieldValue = jobj.getString(fieldName);
                String query = "SELECT modulerecid FROM " + modulename + "cstm where modulerecid = ? and fieldparamid = ?";
                SQLQuery sql = session.createSQLQuery(query);
                sql.setParameter(0, modulerecid);
                sql.setParameter(1, fieldid);
                ArrayList list = (ArrayList) sql.list();
                boolean recExists = false;
                if (list.size() > 0) {
                    recExists = true;
                }
                for (int j = 0; j < ll.size(); j++) {
                    Object[] temp = (Object[]) ll.get(j);
                    if ((isNew || !recExists) && (temp[0].toString().equals(fieldName))) {
                        query = "insert into " + modulename + "cstm values(?,?,?,?)";
                        sql = session.createSQLQuery(query);
                        sql.setParameter(0, modulerecid);
                        sql.setParameter(1, fieldid);
                        sql.setParameter(2, fieldValue);
                        sql.setParameter(3, fieldName);
                        sql.executeUpdate();
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(fieldManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        } catch(Exception e) {
            if (tx!=null) tx.rollback();
            Logger.getLogger(fieldManager.class.getName()).log(Level.SEVERE, null, e);
        } finally {
                HibernateUtil.closeSession(session);
        }
        return true;
    }
/*
CREATE TABLE `hqlcrm`.`fieldparams` (
  `id` INTEGER  NOT NULL,
  `maxlength` INTEGER  NOT NULL,
  `isessential` INTEGER  NOT NULL COMMENT '0 for not essential and 1 for essential',
  `fieldtype` INTEGER  NOT NULL,
  `validationtype` INTEGER  NOT NULL,
  `customregex` VARCHAR(1024)  NOT NULL,
  `fieldname` VARCHAR(255)  NOT NULL,
  `fieldlabel` VARCHAR(255)  NOT NULL,
  `companyid` VARCHAR(36)  NOT NULL,
  `moduleid` INTEGER  NOT NULL COMMENT 'will be used to create a column model for specific module' AFTER `companyid`,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;
*/
    public String createNewField(String fieldlabel,int isessential,int maxlength, int validationtype,int fieldtype,int moduleid,String customregex,String companyid,String combodata, String formulae, String iseditable, HttpServletRequest request){
        int result=0;
        String msg = "";
        Session session=null;
        try{
        String query = "insert into fieldparams(moduleid,fieldname,fieldlabel,isessential,maxlength,validationtype,fieldtype,customregex,companyid,iseditable) values(?,?,?,?,?,?,?,?,?,?)";
         session=HibernateUtil.getCurrentSession();
        Transaction tx = session.beginTransaction();
        SQLQuery sql = session.createSQLQuery(query);
        sql.setParameter(0,moduleid);
        sql.setParameter(1,fieldlabel.replaceAll(" ", "_"));
        sql.setParameter(2,fieldlabel);
        sql.setParameter(3,isessential);
        sql.setParameter(4,maxlength);
        sql.setParameter(5,validationtype);
        sql.setParameter(6,fieldtype);
        sql.setParameter(7,StringUtil.checkForNull(customregex));
        sql.setParameter(8,companyid);
        sql.setParameter(9,iseditable);

        result = sql.executeUpdate();
  tx.commit();
        if(!StringUtil.isNullOrEmpty(combodata)){

            String[] combovalues = combodata.split(";");
               query =  "SELECT max(id) as fieldid from fieldparams";
                    SQLQuery sql1 = session.createSQLQuery(query);
                    int fieldid = 0;
                    ArrayList list = (ArrayList)sql1.list();
                    if(list.size() > 0){

                        fieldid= Integer.parseInt(list.get(0).toString());
                    }
            for(int cnt=0;cnt < combovalues.length;cnt++){
                if(!StringUtil.isNullOrEmpty(combovalues[cnt])){
                tx = session.beginTransaction();
                query = "insert into fieldComboData(name,fieldid) values(?,?)";
                SQLQuery sql2 = session.createSQLQuery(query);

                sql2.setParameter(0,combovalues[cnt]);
                sql2.setParameter(1,fieldid);
                sql2.executeUpdate();
                tx.commit();
                }
            }

        }

        if(!StringUtil.isNullOrEmpty(formulae)) {
            tx = session.beginTransaction();
            setCustomColumnFormulae(session,request);
            tx.commit();
        }
        } catch(ConstraintViolationException e){
            msg = messageSource.getMessage("hrms.commonlib.new.custom.column.created", null, RequestContextUtils.getLocale(request));
        } catch(Exception e){
            msg = messageSource.getMessage("hrms.commonlib.new.custom.column.cannot.created", null, RequestContextUtils.getLocale(request));
        }finally{
            HibernateUtil.closeSession(session);
        }

        if(result == 1){
            msg = messageSource.getMessage("hrms.commonlib.new.custom.column.created", null, RequestContextUtils.getLocale(request));
            return msg;
        }else{
            if(StringUtil.isNullOrEmpty(msg)) {
                msg = messageSource.getMessage("hrms.commonlib.new.custom.column.cannot.created", null, RequestContextUtils.getLocale(request));
            }
            return msg;
        }

    }
public ArrayList getColumnModel(String moduleid,String companyid){
    ArrayList colmodel = new ArrayList();
    Session session = HibernateUtil.getCurrentSession();
    try{
        String query = "SELECT fieldname,fieldlabel,isessential,maxlength,validationtype,id,fieldtype,iseditable FROM fieldparams where moduleid = ? and companyid = ? ";
        SQLQuery sql = session.createSQLQuery(query);
        sql.setParameter(0, moduleid);
        sql.setParameter(1, companyid);
        colmodel = (ArrayList) sql.list();
    }catch(Exception e){

    }finally{
        HibernateUtil.closeSession (session);
    }

    return colmodel;
}

public String setCustomColumnFormulae(Session session, HttpServletRequest request) throws Exception {
    String result = "{success:false}";
    try {
        String companyid = AuthHandler.getCompanyid(request);
        String moduleid = request.getParameter("moduleid");
        String formulae = request.getParameter("rules");
        String fieldlabel = request.getParameter("fieldlabel");
        String formulaeid = java.util.UUID.randomUUID().toString();

        CustomColumnFormulae ccf = new CustomColumnFormulae();
        ccf.setCompanyid((Company) session.get(Company.class, companyid));
        ccf.setFieldname(fieldlabel);
        ccf.setFormulae(formulae);
        ccf.setFormulaeid(formulaeid);
        ccf.setModuleid(moduleid);

        session.save(ccf);
    } catch (Exception e) {
        throw ServiceException.FAILURE("crmHandler.setCustomColumnFormulae", e);
    }
    return result;
}
/*
CREATE TABLE `fieldComboData` (
  `id` INTEGER  NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255)  NOT NULL,
  `fieldid` INTEGER  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;*/
public ArrayList getComboData(String fieldid,String companyid){
    ArrayList colmodel = new ArrayList();
    Session session = HibernateUtil.getCurrentSession();
    try{
        String query = "SELECT `id`,`name` from fieldComboData  where fieldid = ?";
        SQLQuery sql = session.createSQLQuery(query);
        sql.setParameter(0, fieldid);
        colmodel = (ArrayList) sql.list();
        }catch(Exception e){

    }finally{
        HibernateUtil.closeSession (session);
    }

    return colmodel;

}

public String currencyRender(String currency, Session session, HttpServletRequest request) throws SessionExpiredException {
    if (!StringUtil.isNullOrEmpty(currency)) {
        KWLCurrency cur = (KWLCurrency) session.load(KWLCurrency.class, AuthHandler.getCurrencyID(request));
        String symbol = cur.getHtmlcode();
        char temp = (char) Integer.parseInt(symbol, 16);
        symbol = Character.toString(temp);
        float v = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (currency.equals("")) {
            return symbol;
        }
        v = Float.parseFloat(currency);
        String fmt = decimalFormat.format(v);
        fmt = symbol + " " + fmt;
        return fmt;
    } else {
        return "";
    }
}

public String preferenceDate(Session session, HttpServletRequest request, Date date, int timeflag) throws SessionExpiredException {
    KWLDateFormat dateFormat = (KWLDateFormat) session.load(KWLDateFormat.class, AuthHandler.getDateFormatID(request));
    String prefDate = "";
    if (timeflag == 0) {// 0 - No time only Date
        int spPoint = dateFormat.getJavaSeperatorPosition();
        prefDate = dateFormat.getJavaForm().substring(0, spPoint);
    } else // DateTime
    {
        prefDate = dateFormat.getJavaForm();
    }
    String result = "";
    if (date != null) {
        result = AuthHandler.getPrefDateFormatter(request, prefDate).format(date);
    } else {
        return result;
    }
    return result;
}



	@Override
	public void setMessageSource(MessageSource ms) {
	    messageSource = ms;
	}
}
