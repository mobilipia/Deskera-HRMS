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

/**
 *
 * @author krawler
 */
public class CompetencyQuestion implements java.io.Serializable {
    private String quesid;
    private String quesdesc;
    private int noofans;
    private String questype;
    private int quesorder;
    private boolean visible;

    public int getNoofans() {
        return noofans;
    }

    public void setNoofans(int noofans) {
        this.noofans = noofans;
    }

    public String getQuesdesc() {
        return quesdesc;
    }

    public void setQuesdesc(String quesdesc) {
        this.quesdesc = quesdesc;
    }

    public String getQuesid() {
        return quesid;
    }

    public void setQuesid(String quesid) {
        this.quesid = quesid;
    }

    public String getQuestype() {
        return questype;
    }

    public void setQuestype(String questype) {
        this.questype = questype;
    }

    public int getQuesorder() {
        return quesorder;
    }

    public void setQuesorder(int quesorder) {
        this.quesorder = quesorder;
    }

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
    
}
