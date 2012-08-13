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
package com.krawler.common.update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

public class Updates implements Comparable<Object>{
	private String updateDiv;
	private Date date;
	
	public String getUpdateDiv() {
		return updateDiv;
	}
	
	public Date getDate() {
		return date;
	}

	public Updates(String updateDiv, Date date) {
		super();
		this.updateDiv = updateDiv;
		this.date = date;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if(o!=null){
			Updates updates = (Updates) o;
			if(this.date!=null && updates.date!=null){
				return this.date.compareTo(updates.date);
			}else{	
				return -1;
			}	
		}else{
			return -1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((updateDiv == null) ? 0 : updateDiv.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Updates other = (Updates) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (updateDiv == null) {
			if (other.updateDiv != null)
				return false;
		} else if (!updateDiv.equals(other.updateDiv))
			return false;
		return true;
	}
	
	public static List<Updates> setUpdatesOrder(List<Updates> updates){
		List<Updates> list = new ArrayList<Updates>();
		List<Updates> list1 = new ArrayList<Updates>();
		Iterator<Updates> iterator = updates.iterator();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date today = null;
		try {
			today = fmt.parse(fmt.format(new Date()));
		} catch (ParseException e) {	
			e.printStackTrace();
		}
		if(today!=null){
			while (iterator.hasNext()) {
				Updates temp = iterator.next();
				if(temp.date!=null && today.compareTo(temp.date)<=0)	
					list.add(temp);
				else
					list1.add(temp);
			}
			Collections.reverse(list1);
			list.addAll(list1);
		}
		return list;
	}
}
