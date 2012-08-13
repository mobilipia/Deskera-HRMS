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

/**
 *
 * @author krawler
 */
public class Applyagency {

    private String applyid;
    private Agency applyagency;
    private Positionmain applypos;

    public Applyagency() {
    }

    public Applyagency(String applyid, Agency applyagency, Positionmain applypos) {
        this.applyid = applyid;
        this.applyagency = applyagency;
        this.applypos = applypos;
    }

    public Agency getApplyagency() {
        return applyagency;
    }

    public void setApplyagency(Agency applyagency) {
        this.applyagency = applyagency;
    }

    public String getApplyid() {
        return applyid;
    }

    public void setApplyid(String applyid) {
        this.applyid = applyid;
    }

    public Positionmain getApplypos() {
        return applypos;
    }

    public void setApplypos(Positionmain applypos) {
        this.applypos = applypos;
    }
    
}
