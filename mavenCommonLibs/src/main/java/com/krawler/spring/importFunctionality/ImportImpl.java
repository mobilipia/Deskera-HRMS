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

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author krawler
 */
public class ImportImpl implements ImportDAO {
    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public Object saveRecord(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, Object csvReader, String modeName, String classPath, String primaryKey, Object extraObj, JSONArray customfield) throws DataInvalidateException {
        return HibernateUtil.objectSetterMethod(hibernateTemplate, dataMap, classPath, primaryKey);
    }

    @Override
    public Object saveImportLog(HashMap<String, Object> dataMap) throws ServiceException {
        return HibernateUtil.setterMethod(hibernateTemplate, dataMap, "com.krawler.common.admin.ImportLog", "Id");
    }

    @Override
    public KwlReturnObject getImportLog(HashMap<String, Object> requestParams) throws ServiceException {
        ArrayList params = new ArrayList();
        params.add(requestParams.get("startdate"));
        params.add(requestParams.get("enddate"));
        params.add(requestParams.get("companyid"));
        String query = "from ImportLog where (importDate>=? and importDate<=?) and company.companyID = ? order by importDate desc";
        List list = HibernateUtil.executeQuery(hibernateTemplate, query, params.toArray());
        int count = list.size();
        int start = Integer.parseInt(requestParams.get("start").toString());
        int limit = Integer.parseInt(requestParams.get("limit").toString());
        list = HibernateUtil.executeQueryPaging(hibernateTemplate, query, params.toArray(), new Integer[]{start,limit});
        return new KwlReturnObject(true, null, null, list, count);
    }
    
    @Override
    public List getRefModuleData(HashMap<String, Object> requestParams, String module, String fetchColumn, String comboConfigid, ArrayList<String> filterNames, ArrayList<Object> filterValues) throws ServiceException, DataInvalidateException {
        if(filterNames.size()!=filterValues.size()) {
            throw new DataInvalidateException("Count of Filternames and Filterparams are not same for module "+module);
        }
        String query = "select "+fetchColumn+" from "+module;
        try{
            String filter = StringUtil.filterQuery(filterNames, "where");
            return HibernateUtil.executeQuery(hibernateTemplate, query+filter, filterValues.toArray());
        } catch(Exception e) {
            throw ServiceException.FAILURE("ImportImpl. : getRefModuleData" + e.getMessage(), e);
        }
    }

    @Override
    public List getCustomComboID(String fetchColumn, ArrayList filterNames, ArrayList filterValues) throws ServiceException, DataInvalidateException {
        try{
//            "SELECT id FROM fieldComboData where name = ? and fieldid = ?"
            String query =  "SELECT "+fetchColumn+" FROM fieldComboData ";
            String filter = StringUtil.filterQuery(filterNames, "where");
            query+=filter;
            SQLQuery sql = hibernateTemplate.getSessionFactory().getCurrentSession().createSQLQuery(query);
            if (filterValues != null) {
				for (int i = 0; i < filterValues.size(); i++) {
					sql.setParameter(i, filterValues.get(i));
				}
			}
            return sql.list();
        } catch(Exception e) {
            throw ServiceException.FAILURE("ImportImpl. : getCustomComboID" + e.getMessage(), e);
        }
    }

    @Override
    public List getModuleColumnConfig(String moduleId, String companyid) throws ServiceException {
        String query = "select * from ( select dh.id, dh.defaultHeader from default_header dh " +
                    "inner join modules mo on mo.id = dh.module " +
//                    "inner join fieldparams fp on fp.id = dh.pojoheadername " +
                    "where dh.module=? "+// and fp.companyid = ? " +
                    "and (dh.allowimport = ? or dh.allowimport = ?)  " +
                    " union  " +
                    "select dh.id, dh.defaultHeader from default_header dh " +
                    "inner join modules mo on mo.id = dh.module " +
                    "where dh.module=? and (dh.allowimport = ? or dh.allowimport = ?) " +
                    "and (dh.customflag = ? or dh.customflag = ?) ) " +
                    "as temp order by defaultHeader asc";
        return HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), query,
//                new Object[]{moduleId, companyid, "T", "1", moduleId, "T", "1", "F", "0"});
                new Object[]{moduleId, "T", "1", moduleId, "T", "1", "F", "0"});
    }

    @Override
    public List getModuleObject(String moduleName) throws ServiceException {
        String query = "from Modules where modulename=?";
        return HibernateUtil.executeQuery(hibernateTemplate, query, moduleName);
    }

    public String getTableName(String fileName) {
        fileName = fileName.trim();
        int startIndex= fileName.contains("/")?(fileName.lastIndexOf("/")+1):0;
        int endIndex= fileName.contains(".")?fileName.lastIndexOf("."):fileName.length();
        String tablename = fileName.substring(startIndex, endIndex);
        tablename = tablename.replaceAll("\\.", "_");
        tablename = "IL_"+tablename;
        return tablename;
    }
    @Override
    public int createFileTable(String tablename, int cols) throws ServiceException{
        if(cols==0){
            return 0;
        }
        try {
            String query= "", columns = "";
            query = "DROP TABLE IF EXISTS  `"+tablename+"`";
            HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query);

            for(int i=0; i<cols; i++){
                columns += "`col"+i+"` TEXT DEFAULT NULL,";
            }
            query = "create table `"+ tablename +"` ("+
                "id INT NOT NULL AUTO_INCREMENT,"+
                columns +
                "isvalid INT(1) DEFAULT 1,"+
                "invalidcolumns VARCHAR(255) DEFAULT NULL,"+
                "validatelog VARCHAR(1000) DEFAULT NULL,"+
                "PRIMARY KEY (id)"+
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8";

            return HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query);
        } catch(Exception ex){
            throw ServiceException.FAILURE("createFileTable:"+ex.getMessage(), ex);
        }
    }

    public int removeFileTable(String tablename) throws ServiceException{
        try {
            String query = "DROP TABLE `"+tablename+"`";
            return HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query);
        } catch(Exception ex){
            throw ServiceException.FAILURE("removeFileTable:"+ex.getMessage(), ex);
        }
    }

    public int removeAllFileTables() throws ServiceException {
        int cnt=0;
        try {
            String getQuery = "show tables like 'IL_%'";
            List list = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), getQuery);

            Iterator itr= list.iterator();
            while(itr.hasNext()){
                String tablename = (String) itr.next();
                String query = "DROP TABLE `"+tablename+"`";
                HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query);
                cnt ++;
            }
        } catch(Exception ex){
            throw ServiceException.FAILURE("removeAllFileTables:"+ex.getMessage(), ex);
        }
        return cnt;
    }

    @Override
    public int dumpFileRow(String tablename, Object[] dataArray) throws ServiceException {
        if(dataArray.length==0){
            return 0;
        }
        try {
            String columns = ") values (";
            for(int i=dataArray.length-1; i>=0; i--){
                columns = ",col"+i+columns+"?,";
            }
            String query = "insert into `"+ tablename +"` ("+ (columns.substring(1, columns.length()-1)) +")";
            return HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query, dataArray);
        } catch(Exception ex){
            throw ServiceException.FAILURE("dumpFileRow:"+ex.getMessage(), ex);
        }
    }

    public int makeUploadedFileEntry(String filename, String onlyfilename, String tablename, String companyid) throws ServiceException {
        String query = "insert into uploadedfiles (id,filename,filepathname,tablename,company) values (UUID(), ?,?,?,?)";
        return HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query, new Object[]{onlyfilename, filename, tablename, companyid});
    }

    public int markRecordValidation(String tablename, int id, int isvalid, String validateLog, String invalidColumns) throws ServiceException {
        int affectedRows = 0;
        try {
            ArrayList params= new ArrayList();
            params.add(validateLog);
            params.add(invalidColumns);
            params.add(isvalid);

            String condition = "";
            if(id != -1){ // if id==-1 then update all else update respective record
                condition = " where id=?";
                params.add(id);
            }

            String query = "update `"+ tablename +"` set validatelog=?, invalidcolumns=?, isvalid=? "+condition;
            affectedRows = HibernateUtil.executeSQLUpdate(hibernateTemplate.getSessionFactory().getCurrentSession(), query, params.toArray());
        } catch(Exception ex){
            throw ServiceException.FAILURE("markRecordValidation:"+ex.getMessage(), ex);
        }
        return affectedRows;
    }

    public KwlReturnObject getFileData(String tablename, HashMap<String, Object>filterParams) throws ServiceException {
        List list = null;
        int count = 0;
        try {
            String condition = "";
            ArrayList params= new ArrayList();
            if(filterParams.containsKey("isvalid")){
                condition = (condition.length()==0?" where ":" and ")+" isvalid=? ";
                params.add(filterParams.get("isvalid"));
            }

            String query = "select * from `"+ tablename +"` "+condition ;
            list = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), query, params.toArray());
            count = list.size();

            if(filterParams.containsKey("start") && filterParams.containsKey("limit")){
                condition = " limit ?,?";
                int start = Integer.parseInt(filterParams.get("start").toString());
                int limit = Integer.parseInt(filterParams.get("limit").toString());
                params.add(start);
                params.add(limit);
                list = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), query, params.toArray());
            }
        } catch(Exception ex){
            throw ServiceException.FAILURE("getFileData:"+ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, null, null, list, count);
    }


}
