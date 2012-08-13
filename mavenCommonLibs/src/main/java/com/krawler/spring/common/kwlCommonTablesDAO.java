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
package com.krawler.spring.common;

import com.krawler.common.service.ServiceException;
import com.krawler.utils.json.base.JSONArray;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Karthik
 */
public interface kwlCommonTablesDAO {
    public Object getObject(String classpath, String id) throws ServiceException;
    
    public Object getClassObject(String classpath, String id) throws ServiceException;

    public KwlReturnObject getAllTimeZones() throws ServiceException;

    public KwlReturnObject getAllCurrencies() throws ServiceException;

    public KwlReturnObject getAllDateFormats() throws ServiceException;

    public KwlReturnObject getAllCountries() throws ServiceException;

    public DateFormat getUserDateFormatter(String dateFormatId, String userTimeFormatId, String timeZoneDiff) throws ServiceException;
    public String getSystemTimezone();
    public String getTimeZone(HttpServletRequest request, String uid);
    public Date toUserSystemTimezoneDate(HttpServletRequest request, String date, String userid);
    public JSONArray getDetailsJson(List ll, int index, String classstr);
}
