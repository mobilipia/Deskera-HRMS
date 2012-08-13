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
package com.krawler.spring.hrms.payroll.tax;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import masterDB.Template;

/**
 *
 * @author shs
 */
public interface hrmsPayrollTaxDAO {

    public KwlReturnObject setTaxData(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject deleteMasterTax(HashMap<String, Object> requestParams);

    public KwlReturnObject GetTaxperCatgry(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject setNewIncometax(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject deleteincomeTax(HashMap<String, Object> requestParams);

    public KwlReturnObject getTaxTemplateCount(String id) throws ServiceException;

    public KwlReturnObject setTaxTemplateData(HashMap<String, Object> requestParams, Template template);

    public KwlReturnObject getTaxMaster(HashMap<String, Object> requestParams);

    public KwlReturnObject getDefualtTax(HashMap<String, Object> requestParams);
    public double getTaxRateInTemplate(HashMap<String, Object> requestParams);
    public KwlReturnObject getTaxTemplateDetails(HashMap<String, Object> requestParams);

    public KwlReturnObject getTempTaxTemplateDetails(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteTaxTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteTempTaxTemplateData(HashMap<String, Object> requestParams);
    public KwlReturnObject setTempTaxTemplateDetails(HashMap<String, Object> requestParams);
}
