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

package com.krawler.common.admin;

import java.util.Date;

/**
 *
 * @author krawler
 */
public class UserSearchState {
    private String id;
    private String SearchState;
    private Date modifiedon;
    private User user;
    private int searchFlag;
    private String searchName;
    private boolean deleteflag;

    public String getSearchState() {
        return SearchState;
    }

    public String getId() {
        return id;
    }

    public Date getModifiedon() {
        return modifiedon;
    }

    public User getUser() {
        return user;
    }

    public void setSearchState(String SearchState) {
        this.SearchState = SearchState;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedon = modifiedon;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public int getSearchFlag() {
		return searchFlag;
	}

	public void setSearchFlag(int searchFlag) {
		this.searchFlag = searchFlag;
	}

    public boolean isDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(boolean deleteflag) {
        this.deleteflag = deleteflag;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
    
}
