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
package com.krawler.spring.hrms.template.db;

/**
 *
 * @author Abhishek Dubey <abhishek.dubey@krawlernetworks.com>
 */
public class PlaceHolderLookup {

    private String id;
    private String src_table;
    private String dest_table;
    private String src_field;

    public String getSrc_field() {
        return src_field;
    }

    public void setSrc_field(String src_field) {
        this.src_field = src_field;
    }
    
    public String getDest_table() {
        return dest_table;
    }

    public void setDest_table(String dest_table) {
        this.dest_table = dest_table;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc_table() {
        return src_table;
    }

    public void setSrc_table(String src_table) {
        this.src_table = src_table;
    }
}
