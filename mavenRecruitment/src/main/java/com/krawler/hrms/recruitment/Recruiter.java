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

package com.krawler.hrms.recruitment;

import com.krawler.common.admin.User;

/**
 *
 * @author krawler
 */
public class Recruiter {
    private User recruit;
    private String rid;
    private Integer delflag;
    private Allapplications allapplication;
    public Recruiter() {
    }

    public Recruiter(User recruit, String rid, Integer delflag) {
        this.recruit = recruit;
        this.rid = rid;
        this.delflag = delflag;
    }

    public Allapplications getAllapplication() {
        return allapplication;
    }

    public void setAllapplication(Allapplications allapplication) {
        this.allapplication = allapplication;
    }

    public User getRecruit() {
        return recruit;
    }

    public void setRecruit(User recruit) {
        this.recruit = recruit;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Integer getDelflag() {
        return delflag;
    }

    public void setDelflag(Integer delflag) {
        this.delflag = delflag;
    }

    
}
