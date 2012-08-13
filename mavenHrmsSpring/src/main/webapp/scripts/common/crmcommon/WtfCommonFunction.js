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
Wtf.highLightGlobal = function(highLightId,EditorGrid,EditorStore,primaryKeyId) {
        if(highLightId!=undefined) {
             this.row=Wtf.highLightSearch(highLightId,EditorStore,primaryKeyId)
             if(this.row!=null) {
                if(primaryKeyId!="activityid") {
                    Wtf.highLightRow(EditorGrid,"FFFF00",2, this.row);
                }else {
                     Wtf.highLightText(EditorGrid,"FFFF00",2, this.row);
                }
            }
        }
}

Wtf.highLightSearch= function(highLightId,EditorStore,primaryKeyId) {
         var index =  EditorStore.findBy(function(record) {
            if(record.get(primaryKeyId)==highLightId)
                return true;
            else
                return false;
         });
        if(index == -1)
            return null;
        return index;
}
