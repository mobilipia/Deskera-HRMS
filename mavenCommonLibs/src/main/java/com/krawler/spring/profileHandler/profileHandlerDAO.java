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
package com.krawler.spring.profileHandler;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Karthik
 */
public interface profileHandlerDAO {

    public String getUserFullName(String userid) throws ServiceException;
    
    public KwlReturnObject getUseBookmarks(String userid) throws ServiceException;

    public KwlReturnObject getUserDetails(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException;

    public KwlReturnObject getAllManagers(HashMap<String, Object> requestParams) throws ServiceException;

    public void saveUser(HashMap<String, Object> requestParams) throws ServiceException;

    public void deleteUser(String id) throws ServiceException;

    public void saveUserLogin(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject changePassword(HashMap<String, Object> requestParams) throws ServiceException;

    public String getUser_hash(String userid) throws ServiceException;
	public KwlReturnObject getUser(HashMap<String,Object> requestParams);

    public KwlReturnObject getCompanyid(HashMap<String,Object> requestParams);
    public KwlReturnObject getEmpidFormatEdit(HashMap<String,Object> requestParameters);
    public Date getLastActivityDate() throws ServiceException;
    public String getJobIdFormatEdit(HashMap<String,Object> requestParameters);
    public String getNewEmployeeIdFormat(HashMap<String, Object> requestParams);
}
