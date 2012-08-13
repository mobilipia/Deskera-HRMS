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
Wtf.AccGridComp = function(config) {
    Wtf.apply(this, config);
    
    Wtf.AccGridComp.superclass.constructor.call(this, {
        clicksToEdit: 1,
        border: false,  
        //loadMask: config.loadMask,
        viewConfig: {forceFit: true, autoFill: true}
    });
    this.addEvents({
        'onAccGridEdit': true,
        'onChkboxClick': true,
        'onAccCelldblclick': true,
        'onimgdivclick': true
    });
    
    //this.colModel.defaultSortable = true,
    this.on('afteredit', this.AfterGridEdit, this);    
    this.on('cellclick', this.OnCellClick, this);
    this.on('celldblclick', this.OnCellDblClick, this);
    this.getSelectionModel().on('getGridColNo', this.ReturnColumnNo, this);
};

Wtf.extend(Wtf.AccGridComp, Wtf.grid.EditorGridPanel, {
    arow: 0,
    acol: 0,
    
    AfterGridEdit: function(e) {
        this.fireEvent('onAccGridEdit', e.row, e.originalValue, e.value, e.field, e.record);
    },
    
    OnCellClick: function(gd, r, c, e) {
        this.arow = r;
        this.acol = c;
        if(c == 0)
            this.fireEvent('onChkboxClick', r);
        if(e.target.className == "minus" || e.target.className == "plus")
            this.fireEvent('onimgdivclick', r, e.target);
    },
    
    OnCellDblClick: function(gd, r, c, e) {
        this.fireEvent('onAccCelldblclick', r);
    },
    
    ReturnColumnNo: function() {
        this.getSelectionModel().gridCol = this.acol;
    }
});
