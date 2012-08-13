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

package com.krawler.spring.common;


import java.util.List;

/**
 *
 * @author Karthik
 */
public class KwlSuccessMsg {

    private boolean successFlag;
    private String msg;
    private String errorCode;
    private List ll;
    private int listCount;

    public KwlSuccessMsg(boolean successFlag, String msg, String errorCode, List ll, int listCount) {
        this.successFlag = successFlag;
        this.msg = msg;
        this.errorCode = errorCode;
        this.ll = ll;
        this.listCount = listCount;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getListCount() {
        return listCount;
    }

    public List getLl() {
        return ll;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSuccessFlag() {
        return successFlag;
    }
}

