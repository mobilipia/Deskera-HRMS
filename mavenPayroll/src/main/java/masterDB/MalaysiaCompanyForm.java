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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;

/**
 *
 * @author krawler
 */
public class MalaysiaCompanyForm {
	private String id;
	private Integer amanahSahamNasionalPaymentType;
	private Integer hrdLevyPaymentType;
	private Integer tabungHajiPaymentType;
	private String amanahSahamNasionalChequeno;
	private String hrdLevyChequeno;
	private String tabungHajiChequeno;
	private String cp21EmployerFileRefno;
	private String branch;
	private String employerno;
    private String employernoHQ;
	private Integer month;
	private Integer year;
	private User amanahSahamNasionalUser;
	private User tabungHajiUser;
	private User tp1User;
	private User tp2User;
	private User tp3User;
	private User cp39User;
	private User cp39AUser;
	private User pcb2User;
	private User eaUser;
	private Company company;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getAmanahSahamNasionalPaymentType() {
		return amanahSahamNasionalPaymentType;
	}
	public void setAmanahSahamNasionalPaymentType(
			Integer amanahSahamNasionalPaymentType) {
		this.amanahSahamNasionalPaymentType = amanahSahamNasionalPaymentType;
	}
	public Integer getHrdLevyPaymentType() {
		return hrdLevyPaymentType;
	}
	public void setHrdLevyPaymentType(Integer hrdLevyPaymentType) {
		this.hrdLevyPaymentType = hrdLevyPaymentType;
	}
	public Integer getTabungHajiPaymentType() {
		return tabungHajiPaymentType;
	}
	public void setTabungHajiPaymentType(Integer tabungHajiPaymentType) {
		this.tabungHajiPaymentType = tabungHajiPaymentType;
	}
	public String getAmanahSahamNasionalChequeno() {
		return amanahSahamNasionalChequeno;
	}
	public void setAmanahSahamNasionalChequeno(String amanahSahamNasionalChequeno) {
		this.amanahSahamNasionalChequeno = amanahSahamNasionalChequeno;
	}
	public String getHrdLevyChequeno() {
		return hrdLevyChequeno;
	}
	public void setHrdLevyChequeno(String hrdLevyChequeno) {
		this.hrdLevyChequeno = hrdLevyChequeno;
	}
	public String getTabungHajiChequeno() {
		return tabungHajiChequeno;
	}
	public void setTabungHajiChequeno(String tabungHajiChequeno) {
		this.tabungHajiChequeno = tabungHajiChequeno;
	}
	public String getCp21EmployerFileRefno() {
		return cp21EmployerFileRefno;
	}
	public void setCp21EmployerFileRefno(String cp21EmployerFileRefno) {
		this.cp21EmployerFileRefno = cp21EmployerFileRefno;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getEmployerno() {
		return employerno;
	}
	public void setEmployerno(String employerno) {
		this.employerno = employerno;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public User getAmanahSahamNasionalUser() {
		return amanahSahamNasionalUser;
	}
	public void setAmanahSahamNasionalUser(User amanahSahamNasionalUser) {
		this.amanahSahamNasionalUser = amanahSahamNasionalUser;
	}
	public User getTabungHajiUser() {
		return tabungHajiUser;
	}
	public void setTabungHajiUser(User tabungHajiUser) {
		this.tabungHajiUser = tabungHajiUser;
	}
	public User getTp1User() {
		return tp1User;
	}
	public void setTp1User(User tp1User) {
		this.tp1User = tp1User;
	}
	public User getTp2User() {
		return tp2User;
	}
	public void setTp2User(User tp2User) {
		this.tp2User = tp2User;
	}
	public User getTp3User() {
		return tp3User;
	}
	public void setTp3User(User tp3User) {
		this.tp3User = tp3User;
	}
	public User getCp39User() {
		return cp39User;
	}
	public void setCp39User(User cp39User) {
		this.cp39User = cp39User;
	}
	public User getCp39AUser() {
		return cp39AUser;
	}
	public void setCp39AUser(User cp39aUser) {
		cp39AUser = cp39aUser;
	}
	public User getPcb2User() {
		return pcb2User;
	}
	public void setPcb2User(User pcb2User) {
		this.pcb2User = pcb2User;
	}
	public User getEaUser() {
		return eaUser;
	}
	public void setEaUser(User eaUser) {
		this.eaUser = eaUser;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

    public String getEmployernoHQ() {
        return employernoHQ;
    }

    public void setEmployernoHQ(String employernoHQ) {
        this.employernoHQ = employernoHQ;
    }
}
