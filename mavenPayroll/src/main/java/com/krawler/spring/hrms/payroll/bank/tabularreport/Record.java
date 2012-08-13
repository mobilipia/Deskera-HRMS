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

package com.krawler.spring.hrms.payroll.bank.tabularreport;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author krawler
 */
public class Record {
    private Map<Column,Object> rec= new HashMap<Column,Object>();

    public <E> Record setData(Column<E> col, E value){
        if(!col.isValidData(value)){
            throw new IllegalArgumentException("Data "+ value+" is longer than "+col.getLength()+" characters/ digits for Column "+col.getName());
        }
        rec.put(col, value);
        return this;
    }

    public Object getData(Column col){
        return rec.get(col);
    }
}
