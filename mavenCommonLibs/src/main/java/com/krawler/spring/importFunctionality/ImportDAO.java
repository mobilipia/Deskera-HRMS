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
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ImportDAO {
    public List getModuleObject(String moduleName) throws ServiceException;
    public List getModuleColumnConfig(String moduleId, String companyid) throws ServiceException;
    public List getRefModuleData(HashMap<String, Object> requestParams, String module, String fetchColumn, String comboConfigid, ArrayList<String> filterNames, ArrayList<Object> filterValues) throws ServiceException, DataInvalidateException;
    public List getCustomComboID(String fetchColumn, ArrayList filterNames, ArrayList filterValues) throws ServiceException, DataInvalidateException;
    public Object saveRecord(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, Object csvReader, String modeName, String classPath, String primaryKey, Object extraObj, JSONArray customfield) throws DataInvalidateException;
	public Object saveImportLog(HashMap<String, Object> dataMap) throws ServiceException;
    public KwlReturnObject getImportLog(HashMap<String, Object> requestParams) throws ServiceException;
    public String getTableName(String fileName);
    public int createFileTable(String filename, int cols) throws ServiceException;
    public int removeFileTable(String tablename) throws ServiceException;
    public int removeAllFileTables() throws ServiceException;
    public int dumpFileRow(String filename, Object[] dataArray) throws ServiceException;
    public int makeUploadedFileEntry(String filename, String onlyfilename, String tablename, String companyid) throws ServiceException;
    public KwlReturnObject getFileData(String tablename, HashMap<String, Object>filterParams) throws ServiceException;
    public int markRecordValidation(String tablename, int id, int isvalid, String validateLog, String invalidColumns) throws ServiceException;
}
