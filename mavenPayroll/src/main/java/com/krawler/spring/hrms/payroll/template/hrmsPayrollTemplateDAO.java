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
package com.krawler.spring.hrms.payroll.template;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import masterDB.Historydetail;
import masterDB.Payhistory;

/**
 *
 * @author krawler
 */
public interface hrmsPayrollTemplateDAO {

    public KwlReturnObject getPayProcessData(HashMap<String, Object> requestParams);
    public KwlReturnObject setTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject getTemplateDetails(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteTempTemplate(HashMap<String, Object> requestParams);
    public KwlReturnObject editTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject checkTemplateUserMap(HashMap<String, Object> requestParams);
    public KwlReturnObject setTemplateStatus(HashMap<String, Object> requestParams);
    public KwlReturnObject setTempTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteTemplate(HashMap<String, Object> requestParams);
    public KwlReturnObject getTemplate(HashMap<String,Object> requestParams);
    public KwlReturnObject getUserSalaryTemplateMap(HashMap<String, Object> requestParams);
    public KwlReturnObject getTemplateForEmp(HashMap<String,Object> requestParams);
    public KwlReturnObject assignTemplateToUser(HashMap<String,Object> requestParams) throws ServiceException;
    public KwlReturnObject getTemplateForEmponDate(HashMap<String, Object> requestParams);
    public List<Payhistory> getPayhistory(HashMap<String, Object> requestParams);
    public List<Historydetail> getPayHistorydetail(HashMap<String, Object> requestParams);
    
}
