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

package com.krawler.spring.hrms.payroll.bank.tabularreport;

import com.krawler.spring.hrms.payroll.bank.*;
import com.krawler.spring.hrms.payroll.bank.fileformat.FileContentGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krawler
 */
public class TabularReportSection implements  BankPayrollReportSection{
    private boolean showHeaders;
    private boolean insertBreakBetweenSection;
    private List<Column> columns = new ArrayList();
    private FileContentGenerator fcg;
    private List<Record> records=new ArrayList();

    public TabularReportSection(FileContentGenerator fcg) {
        this.fcg = fcg;
    }

    public TabularReportSection(FileContentGenerator fcg,boolean showHeaders) {
        this.fcg = fcg;
        this.showHeaders = showHeaders;
    }

    public TabularReportSection(FileContentGenerator fcg,boolean showHeaders, boolean insertBreakBetweenSection) {
        this.fcg = fcg;
        this.showHeaders = showHeaders;
        this.insertBreakBetweenSection = insertBreakBetweenSection;
    }

    @Override
    public void generate(){
       if(showHeaders){
           for(Column column: columns){
               fcg.addData(column.getName());
           }
           fcg.insertBreak();
       }
       for(Record rec: records){
            for(Column column: columns){
                fcg.addData(rec.getData(column));
            }
            fcg.insertBreak();
        }
       if(insertBreakBetweenSection){
            fcg.insertBreak();
       }
        
    };
    
    public void addColumns(Column... cols) {

        if(cols==null){
            throw new IllegalArgumentException("TabularReportSection.addColumn : Column can not be null");
        }
        for(Column col:cols)
            columns.add(col);

    }

    public void setData(List list){
        if(list==null){
            throw new IllegalArgumentException("TabularReportSection.setData : Data can not be null");
        }
        records=list;

    }

    @Override
    public void setData(Object data) {
        setData((List)data);
    }

    public void addRecords(Record... rs) {
        if(rs==null){
            return;
        }
        for(Record rec:rs)
            records.add(rec);
    }
}
