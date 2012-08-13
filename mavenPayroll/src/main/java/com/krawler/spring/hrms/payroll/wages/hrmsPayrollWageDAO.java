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

package com.krawler.spring.hrms.payroll.wages;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import masterDB.Template;
import masterDB.Wagemaster;

/**
 *
 * @author shs
 */
public interface hrmsPayrollWageDAO {

    public KwlReturnObject setWagesData(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject deleteMasterWage(HashMap<String, Object> requestParams) ;
    public KwlReturnObject getWageTemplateCount(String id) throws ServiceException;
    public KwlReturnObject setWageTemplateData(HashMap<String, Object> requestParams, Template template);
    public KwlReturnObject getDefualtWages(HashMap<String, Object> requestParams);
    public KwlReturnObject getWageMaster(HashMap<String, Object> requestParams);
    public KwlReturnObject getWageMasterForComponent(HashMap<String, Object> requestParams);
    public KwlReturnObject getWageMasterChild(HashMap<String, Object> requestParams);
    public double getWageRateInTemplate(HashMap<String, Object> requestParams);
    public KwlReturnObject getWageTemplateDetails(HashMap<String, Object> requestParams);
    public KwlReturnObject getTempWageTemplateDetails(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteWageTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteTempWageTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject setTempWageTemplateDetails(HashMap<String, Object> requestParams);
    public Double calculatewagessalary(String exprstr,String tempid);
    public KwlReturnObject getPayRollSummary(HashMap<String, Object> requestParams);
    public KwlReturnObject deletePayrollSummary(String id[]) ;
    public KwlReturnObject payrollSummarySave(JSONObject jobj) ;
     public KwlReturnObject getCompanyDetail(HashMap<String, Object> requestParams,String companyid);
     public KwlReturnObject SaveCompanyEPFDetail(String companyid,String cname,String cno,String addr,String cepfno,JSONObject jobj);
}
