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

package com.krawler.hrms.performance;

import com.krawler.hrms.master.MasterData;

/**
 *
 * @author krawler
 */
public class CompetencyDesMap implements java.io.Serializable {
    private String mid;
    private MasterData desig;
    private String groupid;
    private String comptype;

    public String getComptype() {
        return comptype;
    }

    public void setComptype(String comptype) {
        this.comptype = comptype;
    }

    public MasterData getDesig() {
        return desig;
    }

    public void setDesig(MasterData desig) {
        this.desig = desig;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }
    
}
