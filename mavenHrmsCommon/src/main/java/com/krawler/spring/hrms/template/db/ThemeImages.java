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

import java.io.Serializable;

public class ThemeImages implements Serializable{
    private String id;
    private String imageName;
    private String height;
    private String url;
    private int deleted;

    public String getId(){
        return this.id;
    }
    public String getImagename(){
        return this.imageName;
    }
    public String getHeight(){
        return this.height;
    }
    public String getUrl(){
        return this.url;
    }
    public int getDeleted(){
        return this.deleted;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setImagename(String imgName) {
        this.imageName = imgName;
    }
    public void setHeight(String ht) {
        this.height = ht;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setDeleted(int del) {
        this.deleted = del;
    }
}
