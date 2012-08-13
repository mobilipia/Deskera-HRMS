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

package com.krawler.hql.payroll.ExHibernate;

import java.sql.Types;
import org.hibernate.Hibernate;
import org.hibernate.dialect.function.NoArgSQLFunction;

public class ExMySQLInnoDBDialect extends
    org.hibernate.dialect.MySQLInnoDBDialect{

     public ExMySQLInnoDBDialect() {
        super();
        registerColumnType(Types.LONGNVARCHAR, 65535, "text");
        registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
        registerFunction( "rand", new NoArgSQLFunction("rand", Hibernate.DOUBLE) );
     }

     public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }


}
