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

package masterDB;

import com.krawler.common.admin.User;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class ComponentResourceMappingHistory {

    private String id;
    private Componentmaster component;
    private User user;
    private Date componentstartdate;
    private Date componentenddate;
    private double amount;
    private Date periodstartdate;
    private Date periodenddate;
    private int frequency;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Componentmaster getComponent() {
        return component;
    }

    public void setComponent(Componentmaster component) {
        this.component = component;
    }

    public Date getComponentenddate() {
        return componentenddate;
    }

    public void setComponentenddate(Date componentenddate) {
        this.componentenddate = componentenddate;
    }

    public Date getComponentstartdate() {
        return componentstartdate;
    }

    public void setComponentstartdate(Date componentstartdate) {
        this.componentstartdate = componentstartdate;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPeriodenddate() {
        return periodenddate;
    }

    public void setPeriodenddate(Date periodenddate) {
        this.periodenddate = periodenddate;
    }

    public Date getPeriodstartdate() {
        return periodstartdate;
    }

    public void setPeriodstartdate(Date periodstartdate) {
        this.periodstartdate = periodstartdate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
