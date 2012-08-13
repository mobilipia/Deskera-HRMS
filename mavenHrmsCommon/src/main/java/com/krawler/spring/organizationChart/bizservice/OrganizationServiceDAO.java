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

package com.krawler.spring.organizationChart.bizservice;

import com.krawler.common.service.ServiceException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Locale;



/**
 *
 * @author Kuldeep Singh
 */
public interface OrganizationServiceDAO {

    public JSONObject getUnmappedUsers (String companyid, Locale locale) throws ServiceException;

    public HashMap<String, Object> insertNode (String parentid, String childid) throws ServiceException;

    public JSONObject getMappedUsers (String userid, String companyid, Locale locale) throws ServiceException;

    public JSONObject getGridMappedUsers (String parentid, String childid, String userid, String companyid, Locale locale) throws ServiceException;

    public HashMap<String, Object> updateNode (String parentid, String childid) throws ServiceException;

    public HashMap<String, Object> deleteNode (String nodeid) throws ServiceException;
    

}
