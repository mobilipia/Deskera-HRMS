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

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krawler
 */

// TODO : Sandeep
//      remove validateType : Done
//      map xType values



public class DefaultHeader {
    private String id;
    private String defaultHeader;           // Display Name
    private String moduleName;
    private String pojoheadername;          // DB name[For filter]
    private String recordname;              // Data Index
    private String xtype;                   // 0:String, 1:Integer, 2:Boolean, 3:Date, 4:Ref/combo, 5,6,7,8:String, 9:Double, 10:email
    private int flag;
    private String configid;
    private Modules module;
    private String pojoMethodName;          // to call setter methods
    private String validateType;            // ref,double,integer,date,email [ref : for foreign key],[default date format is yyyy-MM-dd ]
    private int maxLength;
    private boolean mandatory;              // True: Force to map column
    private boolean hbmNotNull;             // not-null value from .Hbm.xml file {<column not-null="????"/>}
    private String defaultValue;            // [now : For new date]
    private String refModule_PojoClassName; // pojo class name
    private String refDataColumn_HbmName;   // .hbm.xml data column name value {<column name="????"/>}
    private String refFetchColumn_HbmName;  // .hbm.xml fetch column name value {<column name="????"/>}
    private boolean allowImport;            // TRUE to show column in mapping interface OR False to hide
    private boolean required;
    private boolean customflag;
    private String dbcolumnname;           // Entry of database column name (same as in DB Table)
    private Set headerinfo = new HashSet();
    private boolean allowMapping;
    public boolean isCustomflag() {
        return customflag;
    }

    public void setCustomflag(boolean customflag) {
        this.customflag = customflag;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isHbmNotNull() {
        return hbmNotNull;
    }

    public void setHbmNotNull(boolean hbmNotNull) {
        this.hbmNotNull = hbmNotNull;
    }

    public boolean isAllowImport() {
        return allowImport;
    }

    public void setAllowImport(boolean allowImport) {
        this.allowImport = allowImport;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public Modules getModule() {
        return module;
    }

    public void setModule(Modules module) {
        this.module = module;
    }

    public String getPojoMethodName() {
        return pojoMethodName;
    }

    public void setPojoMethodName(String pojoMethodName) {
        this.pojoMethodName = pojoMethodName;
    }

    public String getRefDataColumn_HbmName() {
        return refDataColumn_HbmName;
    }

    public void setRefDataColumn_HbmName(String refDataColumn_HbmName) {
        this.refDataColumn_HbmName = refDataColumn_HbmName;
    }

    public String getRefFetchColumn_HbmName() {
        return refFetchColumn_HbmName;
    }

    public void setRefFetchColumn_HbmName(String refFetchColumn_HbmName) {
        this.refFetchColumn_HbmName = refFetchColumn_HbmName;
    }

    public String getRefModule_PojoClassName() {
        return refModule_PojoClassName;
    }

    public void setRefModule_PojoClassName(String refModule_PojoClassName) {
        this.refModule_PojoClassName = refModule_PojoClassName;
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }


    public String getConfigid() {
        return configid;
    }

    public void setConfigid(String configid) {
        this.configid = configid;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRecordname() {
        return recordname;
    }

    public void setRecordname(String recordname) {
        this.recordname = recordname;
    }

    public String getDefaultHeader() {
        return defaultHeader;
    }

    public void setDefaultHeader(String defaultHeader) {
        this.defaultHeader = defaultHeader;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPojoheadername() {
        return pojoheadername;
    }

    public void setPojoheadername(String pojoheadername) {
        this.pojoheadername = pojoheadername;
    }

    public String getXtype() {
        return xtype;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }

    public String getDbcolumnname() {
        return dbcolumnname;
    }

    public void setDbcolumnname(String dbcolumnname) {
        this.dbcolumnname = dbcolumnname;
    }

    public Set getHeaderinfo() {
        return headerinfo;
    }

    public void setHeaderinfo(Set headerinfo) {
        this.headerinfo = headerinfo;
    }

    public boolean isAllowMapping() {
        return allowMapping;
    }

    public void setAllowMapping(boolean allowMapping) {
        this.allowMapping = allowMapping;
    }

}
