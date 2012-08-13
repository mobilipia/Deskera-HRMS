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

package com.krawler.spring.hrms.payroll.bank.fileformat;

import java.util.Collection;

/**
 *
 * @author krawler
 */
public class TXTFile implements FileContentGenerator {

    private StringBuffer fileContent;
    private String delimiter;

    public TXTFile() {
        this.fileContent= new StringBuffer();
    }

    public TXTFile(String delimiter) {
        this.fileContent= new StringBuffer();
        this.delimiter = delimiter;
    }

    @Override
    public void addData(Object data) {

        if(data instanceof String){

            appendContent((String) data);

        } else if(data.getClass().isArray()){
            for(Object obj:(Object []) data){
                addData(obj);
            }
        } else if (data instanceof Collection){
            for(Object obj:(Collection) data){
                addData(obj);
            }
        } else {         
            appendContent(data==null?"Null":data.toString());
        }
    }

    private void appendContent (String str){

        if(this.delimiter!=null){
            this.fileContent.append(this.delimiter).append(str);
        }else{
            fileContent.append("\t").append(str);
        }
        
    }

    public String getFileContent(){
        return  this.fileContent.toString();
    }

    @Override
    public void insertBreak() {
        this.fileContent.append("\n");
    }

}
