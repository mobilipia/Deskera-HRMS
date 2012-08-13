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
package com.krawler.esp.database;

import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author krawler
 */
public class MBasicDataSource extends BasicDataSource {

    private static String baseURL = "";
    private HashMap dataSourceMap = new HashMap();

    protected synchronized DataSource createDataSource(String domain_key) throws SQLException {
        if (StringUtil.isNullOrEmpty(baseURL)) {
            baseURL = super.getUrl();
        }
        if (!StringUtil.isNullOrEmpty(domain_key)) {
            String newUrl = baseURL + "_" + domain_key;
            super.setUrl(newUrl);
        }
        super.dataSource = null;
        return super.createDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        ServletRequestAttributes ss = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String domain_key = "";
        try {
            domain_key = URLUtil.getDomainName(ss.getRequest());
        } catch (Exception ex) {
            System.out.print(ex);
        }
        if (!dataSourceMap.containsKey(domain_key)) {
            dataSourceMap.put(domain_key, createDataSource(domain_key));
        }
        super.dataSource = (DataSource) dataSourceMap.get(domain_key);
        return super.dataSource.getConnection();
    }
}
