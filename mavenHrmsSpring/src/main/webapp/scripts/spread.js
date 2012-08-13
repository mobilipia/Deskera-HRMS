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


// IE Bug Resolved - Cursor moves before first character in textfield cell editor

Wtf.override(Wtf.Element,{
    focus: function(defer, /* private */dom) {
        var me = this,dom = dom || me.dom;
        try {
            if (Number(defer)) {
                me.focus.defer(defer, null, [null, dom]);
            }else {
                dom.focus();
            }
        } catch (e) { }
        if (document.selection) {
            var range = document.selection.createRange();
            if (dom && dom.value) {
                range.move('character', dom.value.length);
                range.select();
            }
        }
        return me;
    }
});



Wtf.AlignPalette = function(config){
    Wtf.AlignPalette.superclass.constructor.call(this, config);
    this.addEvents(
        'select'
        );
    if(this.handler){
        this.on("select", this.handler, this.scope, true);
    }
};
Wtf.extend(Wtf.AlignPalette, Wtf.Component, {
    itemCls : "x-align-palette",
    value : null,
    clickEvent:'click',
    ctype: "Wtf.AlignPalette",
    aligns : [
    "left", "center", "right"//, "top", "mid", "bottom"
    ],

    onRender : function(container, position){
        var t = new Wtf.XTemplate(
            '<tpl for="."><a href="#" class="text-align:{.}" hidefocus="on">',
            '<em><span class="sheetBar align-palette-{.}-img" unselectable="on">&#160;</span></em></a></tpl>'
            );
        var el = document.createElement("div");
        el.className = this.itemCls;
        t.overwrite(el, this.aligns);
        container.dom.insertBefore(el, position);
        this.el = Wtf.get(el);
        this.el.on(this.clickEvent, this.handleClick,  this, {delegate: "a"});
        if(this.clickEvent != 'click'){
            this.el.on('click', Wtf.emptyFn,  this, {delegate: "a", preventDefault:true});
        }
    },
    afterRender : function(){
        Wtf.AlignPalette.superclass.afterRender.call(this);
        if(this.value){
            var s = this.value;
            this.value = null;
            this.select(s);
        }
    },
    handleClick : function(e, t){
        e.preventDefault();
        if(!this.disabled && t.className){
            this.select(t.className);
        }
    },
    select : function(align){
        this.value = align;
        this.fireEvent("select", this, align);
    }
});








Wtf.menu.AlignItem = function(config){
    Wtf.menu.AlignItem.superclass.constructor.call(this, new Wtf.AlignPalette(config), config);
    this.palette = this.component;
    this.relayEvents(this.palette, ["select"]);
    if(this.selectHandler){
        this.on('select', this.selectHandler, this.scope);
    }
};
Wtf.extend(Wtf.menu.AlignItem, Wtf.menu.Adapter);




Wtf.menu.AlignMenu = function(config){
    Wtf.menu.AlignMenu.superclass.constructor.call(this, config);
    this.plain = true;
    var ci = new Wtf.menu.AlignItem(config);
    this.add(ci);
    this.palette = ci.palette;
    this.relayEvents(ci, ["select"]);
};
Wtf.extend(Wtf.menu.AlignMenu, Wtf.menu.Menu);











Wtf.override(Wtf.EventObjectImpl, {
    F2 : 113
});





Wtf.override(Wtf.form.ComboBox, {
    beforeBlur : function(){
        if(this.store)this.store.clearFilter();
    }
});




// add keydown event for safari and chrome
Wtf.override(Wtf.form.Field, {
    initEvents : function(){
        this.el.on(Wtf.isIE || Wtf.isSafari || Wtf.isChrome ? "keydown" : "keypress", this.fireKey,  this);
        this.el.on("focus", this.onFocus,  this);
        this.el.on("blur", this.onBlur,  this);
        this.originalValue = this.getValue();
//        var o = this.inEditor && Wtf.isWindows && Wtf.isGecko ? {buffer:10} : null;
//        this.el.on("blur", this.onBlur,  this, {buffer:10});

    }
});




Wtf.SpreadSheet = {};


Wtf.SpreadSheet.GridEditor = function(field, config){
    Wtf.SpreadSheet.GridEditor.superclass.constructor.call(this, field, config);
    field.monitorTab = Wtf.isSafari;
};
Wtf.extend(Wtf.SpreadSheet.GridEditor, Wtf.Editor, {
    alignment: "tl-tl",
    autoSize: "width",
    hideEl : false,
    cls:"spreadSheet-editor x-grid-editor "+ ( Wtf.isIE?" ie-grid-editor ":"") + ( Wtf.isSafari?" safari-grid-editor ":""),
    shim:false,
    shadow:false,
    completeEdit : function(remainVisible){
        if(!this.editing){
            return;
        }
        var v = this.getValue();
        if(this.revertInvalid !== false && !this.field.isValid()){
            v = this.startValue;
            this.cancelEdit(true);
            if(this.field.regexText)
                WtfComMsgBox(["Alert",this.field.regexText]);
        }
        if(String(v) === String(this.startValue) && this.ignoreNoChange){
            this.editing = false;
            this.hide();
            return;
        }
        if(this.fireEvent("beforecomplete", this, v, this.startValue) !== false){
            this.editing = false;
            if(this.updateEl && this.boundEl){
                this.boundEl.update(v);
            }
            if(remainVisible !== true){
                this.hide();
            }
            this.fireEvent("complete", this, v, this.startValue, this.oldCellValue);
        }
    }
});











Wtf.SpreadSheet.ColumnModel = function(config){
    Wtf.SpreadSheet.ColumnModel.superclass.constructor.call(this,config);
};
Wtf.extend(Wtf.SpreadSheet.ColumnModel, Wtf.grid.ColumnModel, {

    setConfig : function(config, initial){
        if(!initial){
            delete this.totalWidth;
            for(var i1 = 0, len1 = this.config.length; i1 < len1; i1++){
                var c1 = this.config[i1];
                if(c1.editor){
                    c1.editor.destroy();
                }
            }
        }
        this.config = config;
        this.lookup = {};

        for(var i = 0, len = config.length; i < len; i++){
            var c = config[i];
            if(typeof c.renderer == "string"){
                c.renderer = Wtf.util.Format[c.renderer];
            }
            if(typeof c.id == "undefined"){
                c.id = i;
            }
            if(c.sheetEditor){
                var myEditor = this.getEditor(c.sheetEditor);

                c.editor = new Wtf.SpreadSheet.GridEditor(myEditor);
                this.config[i].editor = c.editor;
                if(c.sheetEditor.xtype == "combo"){
                    c.renderer = this.getComboRenderer(myEditor);
                    this.config[i].renderer = c.renderer;
                }else if(c.sheetEditor.xtype == "select" ){
                    c.renderer = this.getSelectComboRenderer(myEditor);
                    this.config[i].renderer = c.renderer;
                } else if(c.sheetEditor.xtype =="checkbox"){
                                    c.renderer  = function(v, p, record){


      p.css += ' x-grid3-check-col-td';
      return '<div class="x-grid3-check-col'+(v =="true" ?'-on':'')+' x-grid3-cc-'+this.id+'"> </div>';
   }

                }
            } else if(c.editor && c.editor.isFormField){
                c.editor = new Wtf.SpreadSheet.GridEditor(c.editor); //
            }

            if(typeof c.headerName == "undefined"){
                c.headerName = this.setConfigHeaderName(i);
                this.config[i].headerName = c.headerName;
            }
            if(typeof c.width == "undefined" && i>1){
                c.width = 180;
                this.config[i].width = c.width;
            }

            this.lookup[c.id] = c;
        }
        if(!initial){
            this.fireEvent('configchange', this);
        }
    },
    validateSelection : function(combo,record,index){
        return record.get('hasAccess' );
    },
    getEditor : function(eObj){
        var editor = null;
        if(eObj.xtype == "combo") {
            if(eObj.useDefault==true){
                eObj.selectOnFocus = true;
                eObj.triggerAction = 'all';
                eObj.mode = 'local';
                eObj.valueField = 'id';
                eObj.displayField = 'name';
                eObj.typeAhead = true;
                eObj.tpl= Wtf.comboTemplate;
            }
            editor = new Wtf.form.ComboBox(eObj);
            editor.on('beforeselect',this.validateSelection,this);
        }else if(eObj.xtype == "select") {
            if(eObj.useDefault==true){
                eObj.selectOnFocus = true;
                eObj.forceSelection = true;
                eObj.multiSelect = true;
                eObj.triggerAction = 'all';
                eObj.mode = 'local';
                eObj.valueField = 'id';
                eObj.displayField = 'name';
                eObj.typeAhead = true;
                eObj.tpl= Wtf.comboTemplate;
            }
            editor = new Wtf.common.Select(eObj);
            editor.on('beforeselect',this.validateSelection,this);
        } else if(eObj.xtype == "textfield") {
            editor = new Wtf.form.TextField(eObj);
        } else if(eObj.xtype == "numberfield") {
            editor = new Wtf.form.NumberField(eObj);
        } else if(eObj.xtype == "datefield") {
            editor = new Wtf.form.DateField(eObj);
        } else if(eObj.xtype == "timefield") {
            if(eObj.useDefault==true) {
                eObj.minValue = new Date(new Date().format("M d, Y")+" 8:00:00 AM");
                eObj.maxValue = new Date(new Date().add(Date.DAY, 1).format("M d, Y")+" 7:45:00 AM");
                eObj.value = "8:00 AM";
            }
            editor = new Wtf.form.TimeField(eObj);
        } else if(eObj.xtype == "textarea") {
            editor = new Wtf.form.TextArea(eObj);
        }else if(eObj.xtype == "checkbox"){
                   editor = new Wtf.form.Checkbox(eObj);
        }

        return editor;
    },

    getComboRenderer : function(combo){
        return function(value) {
            var idx = combo.store.find(combo.valueField, value);
            if(idx == -1)
                return "";
            var rec = combo.store.getAt(idx);
            return rec.get(combo.displayField);
        }
    },
    getSelectComboRenderer : function(combo){
        return function(value) {
            var idx;
            var rec;
            var valStr="";
            if (value != undefined && value != "") {
                var valArray = value.split(",");
                for (var i=0;i < valArray.length;i++ ){
                    idx = combo.store.find(combo.valueField, valArray[i]);
                    if(idx != -1){
                        rec = combo.store.getAt(idx);
                        valStr+=rec.get(combo.displayField)+", ";
                    }
                }
                if(valStr != ""){
                    valStr=valStr.substring(0, valStr.length -2);
                    valStr="<div wtf:qtip=\""+valStr+"\">"+Wtf.util.Format.ellipsis(valStr,27)+"</div>";
                }

            }
            return valStr;
        }
    },

    getColumnHeaderName : function(col){
        return this.config[col].headerName;
    },

    setConfigHeaderName : function(col){
        var headerConf = this.config[col].header;
        var headerName = WtfGlobal.HTMLStripper(headerConf);
        var indx=headerName.indexOf('(');
        if(indx!=-1) {
            indx = headerName.indexOf("&#");
            if(indx!=-1)
                headerName = headerName.substring(0,headerName.indexOf('('));
        }
        headerName = headerName.replace("*","");
        return headerName;
    }
});


















Wtf.SpreadSheet.SelectionModel = function(config){
    Wtf.apply(this, config);
    this.selections = new Wtf.util.MixedCollection(false, function(o){
        return o.id;
    });

    this.navigationType = 0; // 1 Edit, 2 Motion
    this.last = false;
    this.lastActive = false;
    this.selection = null;

    this.addEvents(
        "beforecellselect",
        "cellselect",
        "columnCellsSelect",
        "columnCellsDeSelect",
        "celldeselect",
        "selectionchange",
        "beforerowselect",
        "rowselect",
        "rowdeselect"
        );

    Wtf.SpreadSheet.SelectionModel.superclass.constructor.call(this);
};

Wtf.extend(Wtf.SpreadSheet.SelectionModel, Wtf.grid.CheckboxSelectionModel,  {
    selType : "None",

    initEvents : function(){
        this.selType = "None";

        /* Check Box */
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);
            Wtf.fly(view.innerHd).on('mousedown', this.onHdMouseDown, this);

        }, this);

        /* Row  */
        if(!this.grid.enableDragDrop && !this.grid.enableDrag){
            this.grid.on("rowmousedown", this.handleRowMouseDown, this);
        }else{
            this.grid.on("rowclick", function(grid, rowIndex, e) {
                if(e.button === 0 && !e.shiftKey && !e.ctrlKey) {
                    this.selectRow(rowIndex, false);
                    grid.view.focusRow(rowIndex);
                }
            }, this);
        }

        this.rowNav = new Wtf.KeyNav(this.grid.getGridEl(), {
            "up" : function(e){
                if(!e.shiftKey){
                    this.selectPrevious(e.shiftKey);
                }else if(this.last !== false && this.lastActive !== false){
                    var last = this.last;
                    this.selectRange(this.last,  this.lastActive-1);
                    this.grid.getView().focusRow(this.lastActive);
                    if(last !== false){
                        this.last = last;
                    }
                }else{
                    this.selectFirstRow();
                }
            },
            "down" : function(e){
                if(!e.shiftKey){
                    this.selectNext(e.shiftKey);
                }else if(this.last !== false && this.lastActive !== false){
                    var last = this.last;
                    this.selectRange(this.last,  this.lastActive+1);
                    this.grid.getView().focusRow(this.lastActive);
                    if(last !== false){
                        this.last = last;
                    }
                }else{
                    this.selectFirstRow();
                }
            },
            scope: this
        });

        var view = this.grid.view;
        view.on("refresh", this.onRefresh, this);
        //       view.on("rowupdated", this.onRowUpdated, this);
        view.on("rowremoved", this.onRemove, this);




        /* Cell */
        this.grid.on("cellmousedown", this.handleCellMouseDown, this);
        if(Wtf.isIE || Wtf.isSafari){
            this.grid.getGridEl().on("keydown" , this.handleKeyDown, this);
        } else{
            this.grid.getGridEl().on("keypress", this.handleKeyDown, this);
        }
        //        var view = this.grid.view;
        view.on("refresh", this.onViewChange, this);
        view.on("rowupdated", this.onRowUpdated, this);
        view.on("beforerowremoved", this.clearSelections, this);
        view.on("beforerowsinserted", this.clearSelections, this);
        if (this.grid.isEditor){
            this.grid.on("beforeedit", this.beforeEdit,  this);
        }



    },
    /*
 * Part below not YET required  might be required for future changes
 */
    beforeEdit : function(e){
        this.select(e.row, e.column, false, true, e.record);
    },
    onViewChange : function(){
        this.clearSelections(true);
    },
    getSelectedCell : function(){
        return this.selection ? this.selection.cell : null;
    },
    isSelectable : function(rowIndex, colIndex, cm){
        return !cm.isHidden(colIndex);
    },
    acceptsNav : function(row, col, cm){
        return !cm.isHidden(col) && cm.isCellEditable(col, row);
    },
    onRefresh : function(){
        var ds = this.grid.store, index;
        var s = this.getSelections();
        this.clearSelections(true);
        for(var i = 0, len = s.length; i < len; i++){
            var r = s[i];
            if((index = ds.indexOfId(r.id)) != -1){
                this.selectRow(index, true);
            }
        }
        if(s.length != this.selections.getCount()){
            this.fireEvent("selectionchange", this);
        }
    },
    onRemove : function(v, index, r){
        if(this.selections.remove(r) !== false){
            this.fireEvent('selectionchange', this);
        }
    },
    getCount : function(){
        return this.selections.length;
    },
    selectFirstRow : function(){
        this.selectRow(0);
    },
    selectLastRow : function(keepExisting){
        this.selectRow(this.grid.store.getCount() - 1, keepExisting);
    },
    selectNext : function(keepExisting){
        if(this.hasNext()){
            this.selectRow(this.last+1, keepExisting);
            this.grid.getView().focusRow(this.last);
        }
    },
    selectPrevious : function(keepExisting){
        if(this.hasPrevious()){
            this.selectRow(this.last-1, keepExisting);
            this.grid.getView().focusRow(this.last);
        }
    },
    hasNext : function(){
        return this.last !== false && (this.last+1) < this.grid.store.getCount();
    },
    hasPrevious : function(){
        return !!this.last;
    },
    getSelections : function(){
        return [].concat(this.selections.items);
    },
    getSelected : function(){
        return this.selections.itemAt(0);
    },
    each : function(fn, scope){
        var s = this.getSelections();
        for(var i = 0, len = s.length; i < len; i++){
            if(fn.call(scope || this, s[i], i) === false){
                return false;
            }
        }
        return true;
    },
    hasSelection : function(){
        return this.selections.length > 0;
    },

    isSelected : function(index){
        var r = typeof index == "number" ? this.grid.store.getAt(index) : index;
        return (r && this.selections.key(r.id) ? true : false);
    },
    isIdSelected : function(id){
        return (this.selections.key(id) ? true : false);
    },
    deselectRange : function(startRow, endRow, preventViewNotify){
        if(this.locked) return;
        for(var i = startRow; i <= endRow; i++){
            this.deselectRow(i, preventViewNotify);
        }
    },
    restoreLast : function(){
        if(this._last){
            this.last = this._last;
        }
    },


    //===================================   Changes from here onwards    ==================================================================//



    selectAll : function(){
        if(this.locked) return;
        this.selections.clear();
        this.clearSelections();
        for(var i = 0, len = this.grid.store.getCount(); i < len; i++){
            this.selectRow(i, true, undefined, true);
        }
        this.fireEvent("selectionchange", this);
    },
    handleCellMouseDown : function(g, row, cell, e){
        if(e.button !== 0 || this.isLocked()){
            return;
        }
        this.select(row, cell);
    },
    handleRowMouseDown : function(g, row, cell, e){

    },

    select : function(rowIndex, colIndex, preventViewNotify, preventFocus,  r){
        if(this.fireEvent("beforecellselect", this, rowIndex, colIndex) !== false){
            this.clearCellSelection();
            if(colIndex == 1){
                this.selType = "Row";
            } else {
                this.clearRowSelections();
                this.selType = "Cell";
            }
            r = r || this.grid.store.getAt(rowIndex);
            if(this.grid.colModel.isCellEditable(colIndex, rowIndex)){
                this.selection = {
                    record : r,
                    cell : [rowIndex, colIndex]
                };
            } else {
                if(colIndex != 1)this.selType = "None";
                this.fireEvent("celldeselect", this, rowIndex, colIndex);
                return null;
            }
            if(!preventViewNotify){
                var v = this.grid.getView();
                v.onCellSelect(rowIndex, colIndex);
                if(preventFocus !== true){
                    v.focusCell(rowIndex, colIndex);
                }
            }
            this.fireEvent("cellselect", this, rowIndex, colIndex);
            this.fireEvent("selectionchange", this, this.selection);
        }
    },

    selectColumnCells : function(colIndex){
        var rows = this.grid.store.getCount();
        this.clearSelections();
        this.colCellSelected = true;
        this.selType = "Column";
        this.colCellSelectedIndex = colIndex;
        var v = this.grid.getView();
        for(var row = 0; row < rows; row++){
            v.onColumnCellSelect(row, colIndex);
        }
        this.fireEvent("celldeselect", this, -1, colIndex);
        this.fireEvent("columnCellsSelect", this, colIndex);
        this.fireEvent("selectionchange", this, this.selection);
    },

    onRowUpdated : function(v, index, r){
        if(this.isSelected(r)){
            v.onRowSelect(index);
        }
        if(this.selection && this.selection.record == r){
            v.onCellSelect(index, this.selection.cell[1]);
        }
    },

    selectRecords : function(records, keepExisting){
        if(!keepExisting){
            this.clearRowSelections();
        }
        var ds = this.grid.store;
        for(var i = 0, len = records.length; i < len; i++){
            this.selectRow(ds.indexOf(records[i]), true);
        }
    },

    clearSelections : function(fast){
        if(this.selection){
            var s = this.selection;
            if(s){
                if(fast !== true){
                    this.grid.view.onCellDeselect(s.cell[0], s.cell[1]);
                }
                this.selection = null;
                this.fireEvent("selectionchange", this, null);
            }
        }
        if(this.selections){
            if(this.locked) return;
            if(fast !== true){
                var ds = this.grid.store;
                var s = this.selections;
                s.each(function(r){
                    this.deselectRow(ds.indexOfId(r.id));
                }, this);
                s.clear();
            }else{
                this.selections.clear();
            }
            this.last = false;
        }
        this.clearColumnCellSelections();
        this.selType = "None";
    },

    clearCellSelection : function(fast){
        this.clearColumnCellSelections();
        if(this.selection){
            var s = this.selection;
            if(s){
                if(fast !== true){
                    this.grid.view.onCellDeselect(s.cell[0], s.cell[1]);
                }
                this.selection = null;
                this.fireEvent("selectionchange", this, null);
            }
        }
        this.selType = "None";
    },
    clearRowSelections : function(fast){
        this.clearColumnCellSelections();
        if(this.selections){
            if(this.locked) return;
            if(fast !== true){
                var ds = this.grid.store;
                var s = this.selections;
                s.each(function(r){
                    this.deselectRow(ds.indexOfId(r.id));
                }, this);
                s.clear();
            }else{
                this.selections.clear();
            }
            this.last = false;
        }
        this.selType = "None";
    },

    clearColumnCellSelections : function(){
        if(this.colCellSelected){
            this.colCellSelected = false;
            var rows = this.grid.store.getCount();
            var colIndex = this.colCellSelectedIndex;
            var v = this.grid.getView();
            for(var row = 0; row < rows; row++){
                v.onColumnCellDeSelect(row, colIndex);
            }
            this.fireEvent("selectionchange", this, this.selection);
            this.fireEvent("columnCellsDeSelect", this, colIndex);
            this.colCellSelectedIndex = -1;
        }
        this.selType = "None";
    },

    selectRows : function(rows, keepExisting){
        if(!keepExisting){
            this.clearRowSelections();
        }
        for(var i = 0, len = rows.length; i < len; i++){
            this.selectRow(rows[i], true);
        }
    },

    selectRange : function(startRow, endRow, keepExisting){
        if(this.locked) return;
        if(!keepExisting){
            this.clearRowSelections();
        }
        if(startRow <= endRow){
            for(var i = startRow; i <= endRow; i++){
                this.selectRow(i, true);
            }
        }else{
            for(var i = startRow; i >= endRow; i--){
                this.selectRow(i, true);
            }
        }
    },

    selectRow : function(index, keepExisting, preventViewNotify, dontFireEvent){
        if(this.locked || (index < 0 || index >= this.grid.store.getCount())) return;
        var r = this.grid.store.getAt(index);
        if(r && this.fireEvent("beforerowselect", this, index, keepExisting, r) !== false){
            if(!keepExisting || this.singleSelect){
                this.clearRowSelections();
            }
            this.selections.add(r);
            this.selType = "Row";
            this.last = this.lastActive = index;
            if(!preventViewNotify){
                this.grid.getView().onRowSelect(index);
            }
            this.fireEvent("rowselect", this, index, r);
            if(dontFireEvent==true){}
            else this.fireEvent("selectionchange", this);
        }
    },

    deselectRow : function(index, preventViewNotify){
        if(this.locked) return;
        if(this.last == index){
            this.last = false;
        }
        if(this.lastActive == index){
            this.lastActive = false;
        }
        var r = this.grid.store.getAt(index);
        if(r){
            this.selections.remove(r);
            if(!preventViewNotify){
                this.grid.getView().onRowDeselect(index);
            }
            this.fireEvent("rowdeselect", this, index, r);
            this.fireEvent("selectionchange", this);
        }
        this.selType = "None";
    },

    isAlphaNumKey:function(e){
        var kc = e.getCharCode();
        if( (kc >= 65 && kc <= 90)  || (kc > 47 && kc < 58) || (kc >= 96 && kc <= 121 && !(e.charCode == 0 || e.charCode == undefined) ) )
            return true;
        else
            return false;
    },

    isF2Pressed:function(e){    // Change this Code depending on other browsers
        if(Wtf.isSafari){
            if(e.charCode==63237){
                return true;
            } else if(e.charCode == 0 && e.keyCode==113 ){   // chrome
                return true;
            } else {
                return false;
            }
        } else {
            if( ( e.charCode == 0 || e.charCode == undefined ) && e.keyCode==113 ) {
                return true;
            } else {
                return false;
            }
        }

    },

    handleKeyDown : function(e){
        var g = this.grid, s = this.selection;
        if(!s)return;
        e.stopEvent();
        if(!e.isNavKeyPress()){
            if( this.isF2Pressed(e) ) {
                if(g.isEditor && !g.editing) {
                    g.startEditing(s.cell[0], s.cell[1]);
                    this.navigationType = 1;
                }
            }
            else if(this.isAlphaNumKey(e)) {
                if(g.isEditor && !g.editing) {
                    var val = String.fromCharCode(e.getKey());
                    g.startEditing(s.cell[0], s.cell[1], true, val);
                    this.navigationType = 2;
                }
            }
            return;
        }
        if(!s){
            var cell = g.walkCells(0, 0, 1, this.isSelectable,  this);
            if(cell){
                this.select(cell[0], cell[1]);
            }
            return;
        }
        var sm = this;
        var walk = function(row, col, step){
            return g.walkCells(row, col, step, sm.isSelectable,  sm);
        };
        var k = e.getKey(), r = s.cell[0], c = s.cell[1];
        var newCell;
        switch(k){
            case e.TAB:
                if(e.shiftKey){
                    newCell = walk(r, c-1, -1);
                }else{
                    newCell = walk(r, c+1, 1);
                }
                break;
            case e.DOWN:
                newCell = walk(r+1, c, 1);
                break;
            case e.UP:
                newCell = walk(r-1, c, -1);
                break;
            case e.RIGHT:
                newCell = walk(r, c+1, 1);
                break;
            case e.LEFT:
                newCell = walk(r, c-1, -1);
                break;
            case e.ESC:
                newCell = walk(r, c, -1);
                break;
            case e.ENTER:
                if(e.shiftKey){
                    newCell = walk(r-1, c, -1);
                }else{
                    newCell = walk(r+1, c, 1);
                }
                break;
            default:
                newCell = walk(r, c, -1);
                break;
        }
        if(newCell){
            this.select(newCell[0], newCell[1]);
        }
    },

    onEditorKey : function(field, e){
        var k = e.getKey(), newCell, g = this.grid, ed = g.activeEditor;
        if(!e.isNavKeyPress() || e.getKey == undefined){

            return;
        }
        if(k == e.TAB){
            e.stopEvent();
            ed.completeEdit();
            if(e.shiftKey){
                newCell = g.walkCells(ed.row, ed.col-1, -1, this.acceptsNav, this);
            }else if(ed.row==0){
                newCell = g.walkCells(ed.row+1, ed.col+1, 1, this.acceptsNav, this);
            }else{
                newCell = g.walkCells(ed.row, ed.col+1, 1, this.acceptsNav, this);
            }
        }else if(k == e.ENTER){
            ed.completeEdit();
            if(e.shiftKey){
                newCell = g.walkCells(ed.row-1, ed.col, -1, this.acceptsNav, this);
            }else{
                newCell = g.walkCells(ed.row+1, ed.col, 1, this.acceptsNav, this);
            }
            e.stopEvent();
        }else if(k == e.ESC){
            e.stopEvent();
            ed.cancelEdit();
            return;
        }
        /*
      * TO DO Shashank
      * else if(this.navigationType==2){
            if(k == e.DOWN){
                e.stopEvent();
                ed.completeEdit();
                newCell = g.walkCells(ed.row+1, ed.col, 1, this.acceptsNav, this);
            }else if(k == e.UP){
                e.stopEvent();
                ed.completeEdit();
                newCell = g.walkCells(ed.row-1, ed.col, -1, this.acceptsNav, this);
            }else if(k == e.RIGHT){
                e.stopEvent();
                ed.completeEdit();
                newCell = g.walkCells(ed.row, ed.col+1, 1, this.acceptsNav, this);
            }else if(k == e.LEFT){
                e.stopEvent();
                ed.completeEdit();
                newCell = g.walkCells(ed.row, ed.col-1, -1, this.acceptsNav, this);
            }
        */
        if(k == undefined){
            e.stopEvent();
            newCell = g.walkCells(ed.row, ed.col, 1, this.acceptsNav, this);
        }
        if(newCell){
            this.select(newCell[0], newCell[1]);
        }
    }

});














Wtf.SpreadSheet.Look = function(config){
    Wtf.apply(this, config);
    Wtf.SpreadSheet.Look.superclass.constructor.call(this);
};
Wtf.extend(Wtf.SpreadSheet.Look, Wtf.grid.GridView, {
    initTemplates : function() {
        var ts = this.templates || {};
        if(!ts.master){
            ts.master = new Wtf.Template(
                '<div class="x-grid3" hidefocus="true">',
                '<div class="x-grid3-viewport">',
                '<div class="x-grid3-header"><div class="x-grid3-header-inner"><div class="x-grid3-header-offset">{header}</div></div><div class="x-clear"></div></div>',
                '<div class="x-grid3-scroller"><div class="x-grid3-body">{body}</div><a href="#" class="x-grid3-focus" tabIndex="-1"></a></div>',
                "</div>",
                '<div class="x-grid3-resize-marker"> </div>',
                '<div class="x-grid3-resize-proxy"> </div>',
                "</div>"
                );
        }

        if(!ts.header){
            ts.header = new Wtf.Template(
                '<table border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<thead><tr class="x-grid3-hd-row x-sheet-hd-row-ssg">{cells}</tr></thead>',
                "</table>"
                );
        }

        if(!ts.hcell){
            ts.hcell = new Wtf.Template(
                '<td class="x-grid3-hd x-grid3-cell x-grid3-td-{id}" style="{style}"><div ' +
                'Wtf:qtip="{tip}" {attr} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">', this.grid.enableHdMenu ? '<a class="x-grid3-hd-btn" href="#"></a>' : '',
                '{value}<img class="x-grid3-sort-icon" src="', Wtf.BLANK_IMAGE_URL, '" />',
                "</div></td>"
                );
        }

        if(!ts.body){
            ts.body = new Wtf.Template('{rows}');
        }

        if(!ts.row){
            ts.row = new Wtf.Template(
                '<div class="x-grid3-row x-sheet-row {alt}" style="{tstyle}"><table class="x-grid3-row-table x-sheet-row-height" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<tbody><tr>{cells}</tr>',
                (this.enableRowBody ? '<tr class="x-grid3-row-body-tr" style="{bodyStyle}"><td colspan="{cols}" class="x-grid3-body-cell" tabIndex="0" hidefocus="on"><div class="x-grid3-row-body">{body}</div></td></tr>' : ''),
                '</tbody></table></div>'
                );
        }

        if(!ts.cell){
            ts.cell = new Wtf.Template(
                '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css} x-sheet-col-ssg" style="{style}" tabIndex="0" {cellAttr}>',
                '<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}>{value}</div>',
                "</td>"
                );
        }

        for(var k in ts){
            var t = ts[k];
            if(t && typeof t.compile == 'function' && !t.compiled){
                t.disableFormats = true;
                t.compile();
            }
        }

        this.templates = ts;

        this.tdClass = 'x-grid3-cell';
        this.cellSelector = 'td.x-grid3-cell';
        this.hdCls = 'x-grid3-hd';
        this.rowSelector = 'div.x-grid3-row';
        this.colRe = new RegExp("x-grid3-td-([^\\s]+)", "");
        this.modName = this.modulename;
    },
    handleHdMenuClick : function(item){
        var index = this.hdCtxIndex;
        var cm = this.cm, ds = this.ds;
        var grid = this;
        switch(item.id){
            case "asc":
                ds.sort(cm.getDataIndex(index), "ASC");
                break;
            case "desc":
                ds.sort(cm.getDataIndex(index), "DESC");
                break;
            default:
                index = cm.getIndexById(item.id.substr(4));
                if(index != -1){
                    if(item.checked && cm.getColumnsBy(this.isHideableColumn, this).length <= 1){
                        this.onDenyColumnHide();
                        return false;
                    }
                    cm.setHidden(index, item.checked);
                }
        }
        return true;
    },

    onHeaderClick : function(g, index){
        if(this.headersDisabled || !this.cm.isSortable(index)){
            return;
        }
        g.stopEditing();
        g.getSelectionModel().selectColumnCells(index);
    },

    onRowSelect : function(row){
        this.addRowClass(row, "x-grid3-row-selected");
    },

    onRowDeselect : function(row){
        this.removeRowClass(row, "x-grid3-row-selected");
    },

    onCellSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).addClass("x-sheet-cell-selected");
        }
    },

    onCellDeselect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).removeClass("x-sheet-cell-selected");
        }
    },

    onColumnCellSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).addClass("x-sheet-col-cells-selected");
        }
    },

    onColumnCellDeSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).removeClass("x-sheet-col-cells-selected");
        }
    },

    doRender : function(cs, rs, ds, startRow, colCount, stripe){
        var ts = this.templates, ct = ts.cell, rt = ts.row, last = colCount-1;
        var cm = this.cm;
        var tstyle = 'width:'+this.getTotalWidth()+';';
        var buf = [], cb, c, p = {}, r;
        var search = "";
        for(var j = 0, len = rs.length; j < len; j++){
            var rp = {tstyle: tstyle};
            r = rs[j]; cb = [];
            var rowIndex = (j+startRow);

            var myCellStyle = r.data.cellStyle; // must be object
            //     var myCellCss = r.data.cellCss;      // must be object
            var reg=new RegExp("[*^+.()[\\]$?]", "gi");
            for(var i = 0; i < colCount; i++){
                c = cs[i];
                p.id = c.id;
                p.css = i == 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '');
                p.attr = p.cellAttr = "";
                p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
                p.style = c.style;

                if(r.dirty && typeof r.modified[c.name] !== 'undefined'){
                    p.css += ' x-grid3-dirty-cell';
                }

                if(myCellStyle) p.style += myCellStyle[c.id] ? myCellStyle[c.id] : '';
                    //   if(myCellCss) p.css += myCellCss[c.id] ? myCellCss[c.id] : '';

                if(this.rules && i>1){
                    if(p.value){
                    var s1 = p.value.toString();
                    s1 = Wtf.util.Format.htmlDecode(s1);
                    s1 = WtfGlobal.HTMLStripper(s1?s1:"");
                    if(s1.indexOf(WtfGlobal.getCurrencySymbol()) != -1){
                        s1=s1.replace(WtfGlobal.getCurrencySymbol(),"");
                    }
                    if(s1.indexOf("&#160;") != -1){
                        s1=s1.replace("&#160;","");
                    }
                    for(var rli=0; rli<this.rules.length; rli++){
                        var rl = this.rules[rli];
                        search =  rl.search;
                        if ( search.match(reg) ){
                            search = "\\"+search;
                        }else{
                            if (search == "\\"){
                                search ="\\\\"
                            }
                        }
                        var s2 = new RegExp(search, "gi");
                        var m = s1.match(s2);
                        var b = false;
                        if(rl.combo==0 && m){
                            b = true;
                        } else if(rl.combo==1 && !m){
                            b = true;
                        } else if(rl.combo==2 && s1==rl.search){
                            b = true;
                        } else if(rl.combo==3 || rl.combo==4){
                            try{
                                search = search.replace(",","");
                                search = search.replace(" %","");
                                s1= WtfGlobal.replaceAll(s1,",","");
                                s1= s1.replace(" %","");
                                if((!isNaN(search)) && (!isNaN(s1)) && search != "" && s1 != "" ){
                                    var f1 = parseFloat(search);
                                    var f2 = parseFloat(s1);
                                    if(rl.combo==3 && f2<f1){
                                        b = true;
                                    } else if(rl.combo==4 && f2>f1){
                                        b = true;
                                    }
                                }
                            }
                            catch(e){
                            }
                        }

                        if( rl.tCheck && b ) {
                            p.style += "color:#"+rl.txtPanel+";";
                        }
                        if( rl.bCheck && b ) {
                            p.style += "background-color:#"+rl.bgPanel+";";
                        }
                    }

                }
                }

                cb[cb.length] = ct.apply(p);
            }
            var alt = [];
            if(stripe && ((rowIndex+1) % 2 == 0)){
                alt[0] = "x-grid3-row-alt";
            }
            if(r.dirty){
                alt[1] = " x-grid3-dirty-row";
            }
            rp.cols = colCount;
            if(this.getRowClass){
                alt[2] = this.getRowClass(r, rowIndex, rp, ds);
            }
            rp.alt = alt.join(" ");
            rp.cells = cb.join("");
            buf[buf.length] =  rt.apply(rp);
        }
        return buf.join("");
    },

    renderUI : function(){

        var header = this.renderHeaders();
        var body = this.templates.body.apply({
            rows:''
        });

        var html = this.templates.master.apply({
            body: body,
            header: header
        });

        var g = this.grid;

        g.getGridEl().dom.innerHTML = html;
        this.initElements();

        this.mainBody.dom.innerHTML = this.renderRows();
        this.processRows(0, true);

        Wtf.fly(this.innerHd).on("click", this.handleHdDown, this);
        this.mainHd.on("mouseover", this.handleHdOver, this);
        this.mainHd.on("mouseout", this.handleHdOut, this);
        this.mainHd.on("mousemove", this.handleHdMove, this);

        this.scroller.on('scroll', this.syncScroll,  this);
        if(g.enableColumnResize !== false){
            this.splitone = new Wtf.grid.GridView.SplitDragZone(g, this.mainHd.dom);
        }

        if(g.enableColumnMove){
            this.columnDrag = new Wtf.SpreadSheet.ColumnDragZone(g, this.innerHd);      //
            this.columnDrop = new Wtf.SpreadSheet.HeaderDropZone(g, this.mainHd.dom);   //
        }

        if(g.enableHdMenu !== false){
            if(g.enableColumnHide !== false){
                this.colMenu = new Wtf.menu.Menu({
                    id:g.id + "-hcols-menu"
                });
                this.colMenu.on("beforeshow", this.beforeColMenuShow, this);
                this.colMenu.on("itemclick", this.handleHdMenuClick, this);
            }
            this.hmenu = new Wtf.menu.Menu({
                id: g.id + "-hctx"
            });
            this.hmenu.add(
            {
                id:"asc",
                text: this.sortAscText,
                scope:this,
                handler:this.updateSortStateAsc,
                cls: "xg-hmenu-sort-asc"
            },
            {
                id:"desc",
                text: this.sortDescText,
                scope:this,
                handler:this.updateSortStateDesc,
                cls: "xg-hmenu-sort-desc"
            }
            );
            if(g.enableColumnHide !== false){
                this.hmenu.add('-',
                {
                    id:"columns",
                    text: this.columnsText,
                    menu: this.colMenu,
                    iconCls: 'x-cols-icon'
                }
                );
            }
            this.hmenu.on("itemclick", this.handleHdMenuClick, this);
        }
        if(g.enableDragDrop || g.enableDrag){
            var dd = new Wtf.grid.GridDragZone(g, {
                ddGroup : g.ddGroup || 'GridDD'
            });
        }

    this.updateHeaderSortState();

    },

    updateSortStateAsc: function() {
        this.grid.ownerCt.SpreadSheetGrid.getStore().resumeEvents();
    },

    updateSortStateDesc: function() {
        var state = this.grid.ownerCt.SpreadSheetGrid.getStore().getSortState();
        if(!state){
            return;
        }
        if(state.direction == 'DESC' && this.sortState == state.field) {
            this.delayFunction=new Wtf.util.DelayedTask(function(){
                this.grid.ownerCt.SpreadSheetGrid.getStore().resumeEvents();
            },this);
            this.grid.ownerCt.SpreadSheetGrid.getStore().suspendEvents();
            this.delayFunction.delay(1400);
        } else {
            this.grid.ownerCt.SpreadSheetGrid.fireEvent('sortchange', this.grid.ownerCt.SpreadSheetGrid, state, true);
            this.sortState = state.field;
        }

    },
    renderHeaders : function(){
        var cm = this.cm, ts = this.templates;
        var ct = ts.hcell;
        var cb = [], sb = [], p = {};
        for(var i = 0, len = cm.getColumnCount(); i < len; i++){
            p.id = cm.getColumnId(i);
            p.value = cm.getColumnHeader(i) || "";
            p.style = this.getColumnStyle(i, true);
            p.tip = cm.config[i].tip;
            if(cm.config[i].align == 'right'){
                p.istyle = 'padding-right:16px';
            }
            cb[cb.length] = ct.apply(p);
        }
        return ts.header.apply({
            cells: cb.join(""),
            tstyle:'width:'+this.getTotalWidth()+';'
        });
    },

    getCellValue : function(row, col){
        var valData = this.getCell(row, col).innerHTML;
        valData = valData.replace("&nbsp;", " ");
        valData = Wtf.util.Format.htmlDecode(valData);
        var cellValue = WtfGlobal.HTMLStripper(valData?valData:"");
        return cellValue;
    }


});













Wtf.SpreadSheet.HeaderDragZone = function(grid, hd, hd2){
    this.grid = grid;
    this.view = grid.getView();
    this.ddGroup = "gridHeader" + this.grid.getGridEl().id;
    Wtf.SpreadSheet.HeaderDragZone.superclass.constructor.call(this, hd);
    if(hd2){
        this.setHandleElId(Wtf.id(hd));
        this.setOuterHandleElId(Wtf.id(hd2));
    }
    this.scroll = false;
};
Wtf.extend(Wtf.SpreadSheet.HeaderDragZone, Wtf.dd.DragZone, {
    maxDragWidth: 120,
    getDragData : function(e){
        var t = Wtf.lib.Event.getTarget(e);
        var h = this.view.findHeaderCell(t);
        if(h){
            return {ddel: h.firstChild, header:h};
        }
        return false;
    },

    onInitDrag : function(e){
        this.view.headersDisabled = true;
        var clone = this.dragData.ddel.cloneNode(true);
        clone.id = Wtf.id();
        clone.style.width = Math.min(this.dragData.header.offsetWidth,this.maxDragWidth) + "px";
        this.proxy.update(clone);
        return true;
    },

    afterValidDrop : function(){
        var v = this.view;
        setTimeout(function(){
            v.headersDisabled = false;
        }, 50);
    },

    afterInvalidDrop : function(){
        var v = this.view;
        setTimeout(function(){
            v.headersDisabled = false;
        }, 50);
    }
});









Wtf.SpreadSheet.HeaderDropZone = function(grid, hd, hd2){
    this.grid = grid;
    this.view = grid.getView();

    this.proxyTop = Wtf.DomHelper.append(document.body, {
        cls:"col-move-top", html:"&#160;"
    }, true);
    this.proxyBottom = Wtf.DomHelper.append(document.body, {
        cls:"col-move-bottom", html:"&#160;"
    }, true);
    this.proxyTop.hide = this.proxyBottom.hide = function(){
        this.setLeftTop(-100,-100);
        this.setStyle("visibility", "hidden");
    };
    this.ddGroup = "gridHeader" + this.grid.getGridEl().id;


    Wtf.SpreadSheet.HeaderDropZone.superclass.constructor.call(this, grid.getGridEl().dom);
};
Wtf.extend(Wtf.SpreadSheet.HeaderDropZone, Wtf.dd.DropZone, {
    proxyOffsets : [-4, -9],
    fly: Wtf.Element.fly,

    getTargetFromEvent : function(e){
        var t = Wtf.lib.Event.getTarget(e);
        var cindex = this.view.findCellIndex(t);
        if(cindex !== false){
            return this.view.getHeaderCell(cindex);
        }
    },

    nextVisible : function(h){
        var v = this.view, cm = this.grid.colModel;
        h = h.nextSibling;
        while(h){
            if(!cm.isHidden(v.getCellIndex(h))){
                return h;
            }
            h = h.nextSibling;
        }
        return null;
    },

    prevVisible : function(h){
        var v = this.view, cm = this.grid.colModel;
        h = h.prevSibling;
        while(h){
            if(!cm.isHidden(v.getCellIndex(h))){
                return h;
            }
            h = h.prevSibling;
        }
        return null;
    },

    positionIndicator : function(h, n, e){
        var x = Wtf.lib.Event.getPageX(e);
        var r = Wtf.lib.Dom.getRegion(n.firstChild);
        var px, pt, py = r.top + this.proxyOffsets[1];
        if((r.right - x) <= (r.right-r.left)/2){
            px = r.right+this.view.borderWidth;
            pt = "after";
        }else{
            px = r.left;
            pt = "before";
        }
        var oldIndex = this.view.getCellIndex(h);
        var newIndex = this.view.getCellIndex(n);

        if(this.grid.colModel.isFixed(newIndex)){
            return false;
        }

        var locked = this.grid.colModel.isLocked(newIndex);

        if(pt == "after"){
            newIndex++;
        }
        if(oldIndex < newIndex){
            newIndex--;
        }
        if(oldIndex == newIndex && (locked == this.grid.colModel.isLocked(oldIndex))){
            return false;
        }

        var sCol = this.grid.sCol ? this.grid.sCol : 2;
        if(oldIndex < sCol || newIndex < sCol){           //
            return false;
        }

        px +=  this.proxyOffsets[0];
        this.proxyTop.setLeftTop(px, py);
        this.proxyTop.show();
        if(!this.bottomOffset){
            this.bottomOffset = this.view.mainHd.getHeight();
        }
        this.proxyBottom.setLeftTop(px, py+this.proxyTop.dom.offsetHeight+this.bottomOffset);
        this.proxyBottom.show();
        return pt;
    },

    onNodeEnter : function(n, dd, e, data){
        if(data.header != n){
            this.positionIndicator(data.header, n, e);
        }
    },

    onNodeOver : function(n, dd, e, data){
        var result = false;
        if(data.header != n){
            result = this.positionIndicator(data.header, n, e);
        }
        if(!result){
            this.proxyTop.hide();
            this.proxyBottom.hide();
        }
        return result ? this.dropAllowed : this.dropNotAllowed;
    },

    onNodeOut : function(n, dd, e, data){
        this.proxyTop.hide();
        this.proxyBottom.hide();
    },

    onNodeDrop : function(n, dd, e, data){
        var h = data.header;
        if(h != n){
            var cm = this.grid.colModel;
            var x = Wtf.lib.Event.getPageX(e);
            var r = Wtf.lib.Dom.getRegion(n.firstChild);
            var pt = (r.right - x) <= ((r.right-r.left)/2) ? "after" : "before";
            var oldIndex = this.view.getCellIndex(h);
            var newIndex = this.view.getCellIndex(n);
            var locked = cm.isLocked(newIndex);
            if(pt == "after"){
                newIndex++;
            }
            if(oldIndex < newIndex){
                newIndex--;
            }
            if(oldIndex == newIndex && (locked == cm.isLocked(oldIndex))){
                return false;
            }

            var sCol = this.grid.sCol ? this.grid.sCol : 2;
            if(oldIndex < sCol || newIndex < sCol){           //
                return false;
            }

            cm.setLocked(oldIndex, locked, true);
            cm.moveColumn(oldIndex, newIndex);
            this.grid.fireEvent("columnmove", oldIndex, newIndex);
            return true;
        }
        return false;
    }
});



Wtf.SpreadSheet.ColumnDragZone = function(grid, hd){
    Wtf.SpreadSheet.ColumnDragZone.superclass.constructor.call(this, grid, hd, null);
    this.proxy.el.addClass('x-grid3-col-dd');
};

Wtf.extend(Wtf.SpreadSheet.ColumnDragZone, Wtf.SpreadSheet.HeaderDragZone, {
    handleMouseDown : function(e){

    },

    callHandleMouseDown : function(e){
        Wtf.SpreadSheet.ColumnDragZone.superclass.handleMouseDown.call(this, e);
    }
});

















Wtf.SpreadSheet.Grid = Wtf.extend(Wtf.grid.GridPanel, {
    clicksToEdit: 1,
    isEditor : true,
    detectEdit: false,
    trackMouseOver: false,
    loadMask:true,
    border:false,

    initComponent : function(){
        Wtf.SpreadSheet.Grid.superclass.initComponent.call(this);
        this.activeEditor = null;
        this.addEvents(
            "beforeedit",
            "afteredit",
            "validateedit",
            "columnCellsSelect",
            "columnCellsDeSelect",
            "cellselect",
            "celldeselect",
            "savemystate",
            "aftersave"
            );
    },

    initEvents : function(){
        Wtf.SpreadSheet.Grid.superclass.initEvents.call(this);
        this.on("bodyscroll", this.stopEditing, this);

        if(this.isEditor) {
            this.on("cellclick", this.onCellSngClick, this);
            this.on("cellmousedown", this.onCellSngClick, this);
            this.on("celldblclick", this.onCellDblClick, this);
        }

        this.on("columnmove", this.saveMyState, this);
        this.on("columnresize", this.saveMyState, this);
        this.on("sortchange", this.saveMyState, this);

        this.colModel.on("hiddenchange", this.saveMyState, this);
        this.colModel.on("widthchange", this.saveMyState, this);
        this.colModel.on("configchanged", this.saveMyState, this);

        //      this.getGridEl().addClass("xedit-grid");

        this.KeyNavigation = true;
    },

    saveMyState : function(){
        var state = this.getState();
        this.fireEvent("savemystate", this, state);
    },

    applyState : function(state){
        var cm = this.colModel;
        var cs = state.columns;
        var chechwidth=false
        var checholdIndex=""
        var checholdi=""
        if(cs){
            for(var i = 0, len = cs.length; i < len; i++){
                var s = cs[i];
                var c = cm.getColumnById(s.id);
                if(c){
                    c.hidden = s.hidden;
                    c.width = s.width;
                    var oldIndex = cm.getIndexById(s.id);
                    if(oldIndex != i){
                        cm.moveColumn(oldIndex, i);
                    }
                    if(s.id=="checker"){
                        chechwidth=true;
                        checholdIndex=oldIndex;
                        checholdi=i;
                }
            }
        }
        }
        if(state.sort){
            this.store[this.store.remoteSort ? 'setDefaultSort' : 'sort'](state.sort.field, state.sort.direction);
        }
        if(chechwidth && Wtf.isGecko){
            c = cm.getColumnById("checker");
            if(c){
                c.width = 18;
                cm.moveColumn(checholdIndex, checholdi);
            }
        }
    },

    applyCustomHeader: function(header){
        var cm = this.colModel;
        var cs = cm.config;
        for(var i = 0, len = cs.length; i < len; i++){
            var s = cs[i];
            var c = cm.getColumnById(s.id);
            var ismandotory = false;
            for(var j = 0 ; j< header.length ; j++){
                var oldHeader = header[j].oldheader.trim();
                var newHeader = header[j].newheader.trim();
                ismandotory = header[j].ismandotory;
                if(header[j].recordname.replace(" ","_") == c.dataIndex || header[j].recordname == c.validationId){
                    if(ismandotory==true)
                        c.mandatory = true;
                    else
                        c.mandatory = false;
                }
                var currency = c.header.trim().split("(");
                var currency1;
                if(currency.length>1)
                    currency1= currency[1].split(")");
                        if(oldHeader == c.headerName.trim()){
                            c.header = newHeader;
                            if(currency[1]!=null){
                               newHeader = newHeader+"("+currency1[0]+")";
                               c.header = newHeader;
                            }
                            if(ismandotory && oldHeader.substring(oldHeader.length-1,oldHeader.length).trim()!="*"){
                               c.header = newHeader+" *";
                            } else if(ismandotory !=true && oldHeader.substring(oldHeader.length-1,oldHeader.length).trim()=="*"){
                               c.header =newHeader.substring(0,newHeader.length-1).trim()
                            }
                        }
//                        else if(currency[0].trim()== oldHeader.substring(0,oldHeader.length-3)){
//                               if(newHeader.trim().substring(oldHeader.length-2,oldHeader.length-1)!="$" && newHeader.trim().substring(oldHeader.length-2,oldHeader.length-1)!="%"){
//                                    c.header = newHeader+"("+currency[1]+")";
//                               }
//
//                               if(ismandotory)
//                                   c.header=c.header+" *";
//                               else if(ismandotory !=true && oldHeader.substring(oldHeader.length-1,oldHeader.length)=="*"){
//                                    c.header =newHeader.substring(0,newHeader.length-1)
//                                }else{
//                                    c.header = newHeader;
//                                }
//
//                        }
                    }
                }
    },

    onCellDblClick : function(g, row, col){
        this.startEditing(row, col);
        this.KeyNavigation = false;
    },

    onCellSngClick : function(g, row, column){
        var e ={
            row:row,
            column:column,
            grid:g,
            record:g.store.getAt(row)
        }
        this.selModel.select(e.row, e.column, false, true, e.record);
    //    this.fireEvent('beforeedit',e);
    },

    onAutoEditClick : function(e, t){
        var row = this.view.findRowIndex(t);
        var col = this.view.findCellIndex(t);
        if(row !== false && col !== false){
            if(this.selModel.getSelectedCell){
                var sc = this.selModel.getSelectedCell();
                if(sc && sc.cell[0] === row && sc.cell[1] === col){
                    this.startEditing(row, col);
                }
            }else{
                if(this.selModel.isSelected(row)){
                    this.startEditing(row, col);
                }
            }
        }
    },

    onEditComplete : function(ed, value, startValue, oldCellValue){
        this.editing = false;
        this.activeEditor = null;
        ed.un("specialkey", this.selModel.onEditorKey, this.selModel);
        if(String(value) !== String(startValue)){
            var r = ed.record;
            var field = this.colModel.getDataIndex(ed.col);
            var header = this.colModel.getColumnHeaderName(ed.col);
            var e = {
                grid: this,
                record: r,
                field: field,
                originalValue: startValue,
                value: value,
                header:header,
                oldCellValue: oldCellValue,
                row: ed.row,
                column: ed.col,
                cancel:false
            };
            if(this.fireEvent("validateedit", e) !== false && !e.cancel){
                if(typeof e.value == "string"){
                    r.set(field, WtfGlobal.HTMLStripper(e.value));
                } else {
                    r.set(field, e.value);
                }
                delete e.cancel;
                this.fireEvent("afteredit", e);
            }
        }
        this.view.focusCell(ed.row, ed.col);
    },

    startEditing : function(row, col, setNull, val){
        this.stopEditing();
        if(this.colModel.isCellEditable(col, row)){
            this.view.ensureVisible(row, col, true);
            var r = this.store.getAt(row);
            var field = this.colModel.getDataIndex(col);
            var oldCellValue = this.view.getCellValue(row, col);
            var e = {
                grid: this,
                record: r,
                field: field,
                value: r.data[field],
                row: row,
                column: col,
                cancel:false
            };
            if(this.fireEvent("beforeedit", e) !== false && !e.cancel){
                this.editing = true;
                var ed = this.colModel.getCellEditor(col, row);
                if(!ed.rendered){
                    ed.render(this.view.getEditorParent(ed));
                }
                (function(){
                    ed.row = row;
                    ed.col = col;
                    ed.record = r;
                    ed.oldCellValue = oldCellValue;
                    ed.on("complete", this.onEditComplete, this, {
                        single: true
                    });
                    ed.on("specialkey", this.selModel.onEditorKey, this.selModel);
                    this.activeEditor = ed;
                    var v = r.data[field];
                    ed.startEdit(this.view.getCell(row, col), v);
                    if(setNull)ed.field.setValue(val);
                }).defer(50, this);
            }
        }
    },

    stopEditing : function(){
        if(this.activeEditor){
            this.activeEditor.completeEdit();
        }
        this.activeEditor = null;
    },
    walkCells : function(row, col, step, fn, scope){
        var sCol=this.sCol?this.sCol:0;
        var eCol=this.eCol?this.eCol:0;
        var cm = this.colModel, clen = cm.getColumnCount()-eCol;
        var ds = this.store, rlen = ds.getCount(), first = true;

        if(step < 0){
            if(col < sCol){
                row--;
                first = false;
            }
            while(row >= 0){
                if(!first){
                    col = clen-1;
                }
                first = false;
                while(col >= sCol){
                    if(fn.call(scope || this, row, col, cm) === true){
                        return [row, col];
                    }
                    col--;
                }
                row--;
            }
        } else {
            if(col >= clen){
                row++;
                first = false;
            }
            while(row < rlen){
                if(!first){
                    col = sCol;
                }
                first = false;
                while(col < clen){
                    if(fn.call(scope || this, row, col, cm) === true){
                        return [row, col];
                    }
                    col++;
                }
                row++;
            }
        }
        return null;
    }

});

Wtf.reg('spreadSheetGrid', Wtf.SpreadSheet.Grid);










function showRWPalette(cid, panelID, imgDivID, checkID){
    Wtf.getCmp(cid).showColorPanel(cid, panelID, imgDivID, checkID);
}
function removeRWFieldSet(fid, cid, no){
            Wtf.getCmp(cid).removeFieldSet(fid, no);
        }
function addMoreRWFieldSet(cid){
    Wtf.getCmp(cid).addMoreFieldSet(true);
}



Wtf.SpreadSheet.RuleWindow = function (config){
    Wtf.apply(this,{
        buttons:[{
            text:'Save and Apply',
            scope:this,
            handler:this.applyHandler
        },{
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope:this,
            handler:this.cancelHandler
        }]
    },config);

    Wtf.SpreadSheet.RuleWindow.superclass.constructor.call(this,config);
    this.addEvents(
        "ruleApply"
        );
}

Wtf.extend(Wtf.SpreadSheet.RuleWindow, Wtf.Window,{
    modal : true,
    id : 'RuleWindow',
    shadow : true,
    constrain : true,
    bufferResize : true,
    resizable : false,
    title : WtfGlobal.getLocaleText("hrms.common.ConditionalColorCoding"),
    iconCls : "pwnd favwinIcon",
    initComponent : function(config){
        Wtf.SpreadSheet.RuleWindow.superclass.initComponent.call(this,config);

        this.fsid = this.id+'fieldSet';
        this.tRule = 0;
        this.width = 680;
        this.cnt=0;
        this.height = 'auto';
        this.fsArray = [];
        this.ruleCount = 0;
        this.createWindow();
        this.on("show",function()
        {
            var restoreWSize = this.getSize();
            var restorePSize = this.MainWinPanel.getSize();
            if((restoreWSize.height)>300 && this.grid.view.rules.length==0){
                restoreWSize.height=299;
                restorePSize.height=230;
                this.setSize(restoreWSize.width, restoreWSize.height );
                this.MainWinPanel.setSize(restorePSize.width, restorePSize.height);
                this.doLayout();
            }
        },this);
    },

    createWindow : function(){
        this.getUserRules();
        this.MainWinPanel= new Wtf.Panel({
            border : false,
            height : 230,
            layout : 'border',
            items : [{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.ConditionalColorCoding"),  WtfGlobal.getLocaleText("hrms.common.Changecolorsbasedonrules"), "../../images/sheet/ruleWin.png"),
                layout:'fit'
            },
            this.rulesPanel,
            {
                region : 'south',
                height : 30,
                border : false,
                bodyStyle : "background:rgb(241, 241, 241);",
                html: "<span title="+ WtfGlobal.getLocaleText("hrms.common.Clicktoaddmoreruleset")+" id='southAddRulesID' class='shortcuts'><a href='#' style='padding-right:30px;float:right;' onclick=addMoreRWFieldSet('"+this.id+"')>"+ WtfGlobal.getLocaleText("hrms.common.Addanotherrule")+"</a></span>",
                layout:'fit'
            }]
        });
        this.add(this.MainWinPanel);
        this.MainWinPanel.doLayout();
    },

    getUserRules : function(){
        this.rulesPanel = new Wtf.Panel({
            style : "background:rgb(241, 241, 241);padding:0px 5px 0px 5px;",
            border : false,
            region : 'center',
            layout : "fit"
        });
        if(this.grid.view.rules.length>0){
            this.on('show', this.editRulePanel, this);
        }
        else {
            this.createRulePanel();
        }
    },

    createRulePanel : function(){
        this.tRule++;
        this.addMoreFieldSet(false);
    },

    editRulePanel : function(rw){
        var rules = rw.grid.view.rules;
        for(var i=0;i<rules.length; i++){
            this.rulesPanel.add(this.getRuleForm(rules[i]));
            if(i>0)this.adjustHeight(80, false);
            }
        this.doLayout();
    },

    addMoreFieldSet : function(doLayout){
        if(this.ruleCount>=4) {
            if(this.ruleCount == 4) {
                Wtf.get('southAddRulesID').dom.style.display="none";
            } else {
                return;
            }
        }
        this.rulesPanel.add(this.getRuleForm());
        if(doLayout){
            this.adjustHeight(80, true);
        }
    },

    removeFieldSet : function(fid, no){
        if(this.fsArray.length == 1 && !Wtf.getCmp(this.fsid + this.fsArray[0]+'search').isValid()) {
            ResponseAlert(511);
            return;
        }
        Wtf.MessageBox.confirm( WtfGlobal.getLocaleText("hrms.common.confirm"),  WtfGlobal.getLocaleText("hrms.common.Areyousureyouwanttodelete"), function(btn){
            if (btn == "yes") {
                Wtf.get('southAddRulesID').dom.style.display="block";
                if(this.ruleCount<2){
                    this.addMoreFieldSet(true);
                }
                this.rulesPanel.remove(Wtf.getCmp(fid));
                this.ruleCount--;
                this.fsArray.remove(no);
                this.adjustHeight(-80, true);

                var rules = this.generateConditionRule(true);
                this.fireEvent('ruleApply', this, rules, true);
            }
        },this);

    },

    adjustHeight : function(height, doLayout){
         if(this.ruleCount==5){
             Wtf.get('southAddRulesID').dom.style.display="none";
             }
        var restoreWSize = this.getSize();
        var restorePSize = this.MainWinPanel.getSize();
        var restorePos = this.getPosition(true);
        if(this.cnt==0&&(restoreWSize.height + height)>350){
            restoreWSize.height=259;
            restorePSize.height=200;
        }
        this.cnt++;
        this.setPosition(restorePos[0], restorePos[1] - (height/2));
        this.setSize(restoreWSize.width, restoreWSize.height + height);
        this.MainWinPanel.setSize(restorePSize.width, restorePSize.height + height);

        if(doLayout)this.doLayout();

    },

    getRuleForm : function(values){
        this.ruleCount++;
        var no = this.tRule++;
        var cid = this.fsid + no;
        this.fsArray.push(no);
        //type
        var ruleTypeStore = new Wtf.data.SimpleStore({
            fields : ['id','name'],
            data : [
            [0,WtfGlobal.getLocaleText("hrms.common.Textcontains")],
            [1,WtfGlobal.getLocaleText("hrms.common.Textdoesnotcontains")],
            [2,WtfGlobal.getLocaleText("hrms.common.Textexactlymatches")],
            [3,WtfGlobal.getLocaleText("hrms.common.Numberislessthan")],
            [4,WtfGlobal.getLocaleText("hrms.common.Numberisgreaterthan")]
            ]
        });
        var ruleTypeCombo = new Wtf.form.ComboBox({
            fieldLabel :  WtfGlobal.getLocaleText("hrms.common.Rule"),
            store : ruleTypeStore,
            value : 0,
            id : cid + 'combo',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
            anchor : '70%',
            width : 155
        });

        ruleTypeCombo.on('change', function(combo, newValue, oldValue){
            var ser = Wtf.getCmp(cid + 'search');
            var val = ser.getValue();
            if(newValue==3 || newValue==4){
                ser.regex = /^\d*$/;
            } else{
                ser.regex = /\w/;
            }
            ser.setValue(val);
        }

        );


        //string
        var searchText = new Wtf.form.TextField({
            fieldLabel :  WtfGlobal.getLocaleText("hrms.common.Text"),
            id : cid + 'search',
            allowBlank : false,
            //regex:/\w/,
            maxLength : 20,
            width:'95%'
        });
       var padding="";
        if(Wtf.isIE7){
            padding='padding-left:4px !important;';
        }
        //text check
        var textColorCheck = new Wtf.form.Checkbox({
            fieldLabel:  WtfGlobal.getLocaleText("hrms.common.TextColor"),
            id : cid + 'tCheck',
            width : 'auto',
            name: "ssRuleTextColor",
            style:(Wtf.isOpera)?"left: -2px !important; top: 0px !important":""
        });

        //bg check
        var bgColorCheck = new Wtf.form.Checkbox({
            fieldLabel:  WtfGlobal.getLocaleText("hrms.common.BackgroundColor"),
            id : cid + 'bCheck',
            width : 'auto',
            name: "ssRuleBgColor",
            style:(Wtf.isOpera)?"left: -2px !important; top: 0px !important":""
        });

        var items = [
        {
            border : false,
            columnWidth : .27,
            frame : false,
            style : 'padding:4px;',
            items : ruleTypeCombo
        },
        {
            border : false,
            columnWidth : .28,
            frame : false,
            height:35,
            style : 'padding:4px;',
            items : searchText
        },
        {
            border : false,
            columnWidth : .05,
            frame : false,
            style : 'padding:2px 0px 4px 12px;',
            items : textColorCheck
        },
        {
            border : false,
            columnWidth : .1,
            id : cid+'txtPanel',
            mColor : '000000',
            frame : false,
            style : 'padding:6px 2px 2px 0px;',
            html : '<div style="float:left;'+padding+'"> Text: </div> <div id = "'+cid+'txtImgDiv" class="s-color-div" wtf:qtip='+ WtfGlobal.getLocaleText("hrms.common.Clicktoaddcolor")+' \n\
                            style="background-color:#000000;" onclick=showRWPalette("'+this.id+'","'+cid+'txtPanel'+'","'+cid+'txtImgDiv","' + cid + 'tCheck")></div>'
        },
        {
            border : false,
            columnWidth : .05,
            frame : false,
            style : 'padding:2px 0px 4px 12px;',
            items : bgColorCheck
        },
        {
            border : false,
            columnWidth : .15,
            id : cid+'bgPanel',
            mColor : 'FFFFFF',
            frame : false,
            style : 'padding:6px 2px 2px 0px;',
            html : '<div style="float:left;'+padding+'"> Background: </div><div id = "'+cid+'bgImgDiv" class="s-color-div" wtf:qtip='+ WtfGlobal.getLocaleText("hrms.common.Clicktoaddcolor")+' \n\
                            style="background-color:#FFFFFF;" onclick=showRWPalette("'+this.id+'","'+cid+'bgPanel'+'","'+cid+'bgImgDiv","' + cid + 'bCheck")></div>'
        },
        {
            border : false,
            columnWidth : .08,
            id : 'remove'+cid,
            frame : false,
            style : 'padding:8px 0px 10px 4px;',
            //            html : '<span class="shortcuts" ><a href="#"  onclick=removeRWFieldSet("' + cid + '","' + this.id + '",'+ no +')>remove</a></span>'
            html : '<span title='+ WtfGlobal.getLocaleText("hrms.common.Clicktoremoverule")+' style="float:right;"><a href="#" onclick=removeRWFieldSet("' + cid + '","' + this.id + '",'+ no +')><img src="../../images/sheet/cancel.gif"/></a></span>'
        }
        ];

        // fieldset
        var fieldSet = new Wtf.form.FieldSet({
            style : 'padding:0px 10px 10px 10px;',
            layout : 'column',
            id : cid,
            autoHeight : true,
            title :  WtfGlobal.getLocaleText("hrms.common.SetRule"),
            items : items
        });

        if(values){
            this.setRuleValues(cid, values);
        }

        return fieldSet;
    },

    showColorPanel : function(cid, panelID, imgDivID, checkID) {
        Wtf.getCmp(checkID).setValue(true);
        var colorPicker = new Wtf.menu.ColorItem();
        var contextMenu = new Wtf.menu.Menu({
            items: [ colorPicker ]
        });
        contextMenu.showAt(Wtf.get(panelID).getXY());
        colorPicker.on('select', function(palette, selColor){
            Wtf.getCmp(panelID).mColor = selColor;
            Wtf.get(imgDivID).dom.style.backgroundColor = '#' + selColor;
        },this);
    },

    setRuleValues : function(fsids, values){
        Wtf.getCmp(fsids+'combo').setValue(values.combo);
        Wtf.getCmp(fsids+'search').setValue(values.search);
        if(values.combo == 3 || values.combo == 4){
            Wtf.getCmp(fsids+'search').regex= /^\d*$/;
        }

        Wtf.getCmp(fsids+'tCheck').setValue(values.tCheck);
        Wtf.getCmp(fsids+'txtPanel').mColor = values.txtPanel;

        Wtf.getCmp(fsids+'bCheck').setValue(values.bCheck);
        Wtf.getCmp(fsids+'bgPanel').mColor = values.bgPanel;

        Wtf.getCmp(fsids).on('afterlayout',function(c, d, e){
            Wtf.get(fsids+'txtImgDiv').dom.style.backgroundColor = '#' + values.txtPanel;
            Wtf.get(fsids+'bgImgDiv').dom.style.backgroundColor = '#' + values.bgPanel;
        });

    },

    generateConditionRule: function(delFlag) {
        /*List of ids
         x + combo
         x + search
         x + tCheck
         x + bCheck
         x + bgPanel
         x + txtPanel
        */

        var rules = [];
        for(var i=0; i<this.fsArray.length; i++) {
            var fsids = this.fsid + this.fsArray[i];
            if(Wtf.getCmp(fsids+'search').isValid()){
                var combo    = Wtf.getCmp(fsids+'combo').getValue();
                var search   = Wtf.getCmp(fsids+'search').getValue();
                var tCheck   = Wtf.getCmp(fsids+'tCheck').getValue();
                var txtPanel = Wtf.getCmp(fsids+'txtPanel').mColor;
                var bCheck   = Wtf.getCmp(fsids+'bCheck').getValue();
                var bgPanel  = Wtf.getCmp(fsids+'bgPanel').mColor;

                var obj = {};
                obj.combo = combo;
                obj.search = search;
                obj.tCheck = tCheck;
                obj.txtPanel = txtPanel;
                obj.bCheck = bCheck;
                obj.bgPanel = bgPanel;

                if(!(search.trim()=="")){
                    if(tCheck || bCheck){
                        rules[rules.length] = obj;
                    }
                }
            }else{
                if(!delFlag) {
                    WtfComMsgBox(61);
                    return false;
                } else {
                    rules = "";
                }
            }
        }
        return rules;
    },

    applyHandler : function(){
        var rules = this.generateConditionRule(false);

        if(rules.length<1){
            ResponseAlert(510);
        }
        this.fireEvent('ruleApply', this, rules, false);
        this.close();

    },

    cancelHandler : function(){
        this.close();
        this.destroy();
    }

});


Wtf.SpreadSheet.FormulaeWindow = function (config){
    Wtf.apply(this,{
        buttons:[{
            text:'Previous',
            scope:this,
            handler:this.previousHandler,
            hidden:!this.createFieldFlag
        },{
            text:'Save and Apply',
            scope:this,
            handler:this.applyHandler
        },{
            text: WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope:this,
            handler:this.cancelHandler
        }]
    },config);

    Wtf.SpreadSheet.FormulaeWindow.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.SpreadSheet.FormulaeWindow, Wtf.Window,{
    modal : true,
    id : 'FormulaeWindow',
    shadow : true,
    constrain : true,
    iconCls: 'pwnd favwinIcon',
    bufferResize : true,
    resizable : false,
    closable:false,
    title : 'Apply Formulas',
    initComponent : function(config){
        Wtf.SpreadSheet.FormulaeWindow.superclass.initComponent.call(this,config);

        this.fsid = this.id+'fieldSet';
        this.tRule = 0;
        this.width = 680;
        this.height = 'auto';
        this.fsArray = [];
        this.ruleCount = 0;
        this.createWindow();
        this.show();
    },

    createWindow : function(){
        this.getUserRules();
        this.MainWinPanel= new Wtf.Panel({
            border : false,
            height : 210,
            layout : 'border',
            items : [{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml("Apply Formulas", "Add formulas to the custom column"),
                layout:'fit'
            },
            this.rulesPanel
            ]
        });
        this.add(this.MainWinPanel);
        this.MainWinPanel.doLayout();
    },

    getUserRules : function(){
        this.rulesPanel = new Wtf.form.FormPanel({
            style : "background:rgb(241, 241, 241);padding:10px;",
            border : false,
            region : 'center',
            layout : "fit"
        });
        this.createRulePanel();
    },

    createRulePanel : function(){
        this.tRule++;
        this.addMoreFieldSet(false);
    },

    editRulePanel : function(rw){
        var rules = rw.grid.view.rules;
        for(var i=0;i<rules.length; i++){
            this.rulesPanel.add(this.getRuleForm(rules[i]));
            if(i>0)this.adjustHeight(80, false);
            }
        this.doLayout();
    },

    addMoreFieldSet : function(doLayout){
        if(this.ruleCount>=4) {
            if(this.ruleCount == 4) {
                Wtf.get('southAddRulesID').dom.style.display="none";
            } else {
                return;
            }
        }
        this.rulesPanel.add(this.getRuleForm());
        if(doLayout){
            this.adjustHeight(80, true);
        }
    },

    removeFieldSet : function(fid, no){
         Wtf.get('southAddRulesID').dom.style.display="block";
        if(this.ruleCount<2){
            this.addMoreFieldSet(true);
        }
        this.rulesPanel.remove(Wtf.getCmp(fid));
        this.ruleCount--;
        this.fsArray.remove(no);
        this.adjustHeight(-80, true);
    },

    adjustHeight : function(height, doLayout){
        var restoreWSize = this.getSize();
        var restorePSize = this.MainWinPanel.getSize();
        var restorePos = this.getPosition(true);

        this.setPosition(restorePos[0], restorePos[1] - (height/2));
        this.setSize(restoreWSize.width, restoreWSize.height + height);
        this.MainWinPanel.setSize(restorePSize.width, restorePSize.height + height);

        if(doLayout)this.doLayout();

    },

    getRuleForm : function(values){
        this.ruleCount++;
        var no = this.tRule++;
        var cid = this.fsid + no;
        this.fsArray.push(no);
        //type

         var mainArray=new Array();

        for (i=0;i<this.cm.config.length;i++) {
            var tmpArray=new Array();
            if((this.cm.config[i].xtype=="numberfield" || (this.cm.config[i].sheetEditor!=undefined && this.cm.config[i].sheetEditor.xtype=="numberfield" && this.cm.config[i].id.substr(0, 12) == "custom_field"))
                && (this.cm.config[i].hidden==undefined || this.cm.config[i].hidden==false)) {
                var header=headerCheck(WtfGlobal.HTMLStripper(this.cm.config[i].header));
                tmpArray.push(this.cm.config[i].dataIndex);
                tmpArray.push(header);
                mainArray.push(tmpArray)
            }
        }
        var myData = mainArray;

        this.combostore = new Wtf.data.SimpleStore({
            fields: [
            {
                name: 'id'
            },{
                name: 'name'
            }
            ]
        });
        this.combostore.loadData(myData);

        var operatorStore = new Wtf.data.SimpleStore({
            fields : ['id','name'],
            data : [
            ["+","+"],
            ["-","-"],
            ["*","*"],
            ["/","/"]
            ]
        });

        var opertorText1 = new Wtf.form.ComboBox({
            fieldLabel : 'Column',
            store : this.combostore,
            id : cid + 'combo1',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
           // anchor : '70%',
            width : 100
        });

        //string
        var operatorText = new Wtf.form.ComboBox({
            fieldLabel : 'Operator',
            store : operatorStore,
            id : cid + 'combo',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
           // anchor : '70%',
            width : 110
        });

        var operatorText2 = new Wtf.form.ComboBox({
            fieldLabel : 'Column',
            store : this.combostore,
            id : cid + 'combo2',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
            // anchor : '70%',
            width : 110
        });

        var items = [
       {
            border : false,
            columnWidth : .27,
            frame : false,
            layout:'form',
            hidden:this.ruleCount>1?true:false,
            style : 'padding:4px;',
            items : opertorText1
        },
        {
            border : false,
            columnWidth : .28,
            frame : false,
            layout:'form',
            style : 'padding:4px;',
            items : operatorText
        },
        {
            border : false,
            columnWidth : .28,
            layout:'form',
            frame : false,
            style : 'padding:4px;',
            items : operatorText2
        },
        {
            border : false,
            columnWidth : .08,
            id : 'add'+cid,
            frame : false,
            hidden:this.ruleCount>1?true:false,
            style : 'padding:8px 0px 10px 4px;',
            html : '<span title="Click to extend formulas" id="southAddRulesID" style="float:right;"><a href="#" onclick=addMoreRWFieldSet("'+this.id+'")><img src="../../images/add.gif"/></a></span>'
        },
        {
            border : false,
            columnWidth : .08,
            id : 'remove'+cid,
            frame : false,
            hidden:this.ruleCount>1?false:true,
            style : 'padding:8px 0px 10px 4px;',
            html : '<span title="Click to remove formulas" id="southRemoveRulesID" style="float:right;"><a href="#" onclick=removeRWFieldSet("' + cid + '","' + this.id + '",'+ no +')><img src="../../images/orgchart/Delete.gif"/></a></span>'
        }
        ];

        // fieldset
        var fieldSet = new Wtf.form.FieldSet({
            style : 'padding:10px;',
            layout : 'column',
            id : cid,
            autoHeight : true,
            title : 'Set Formulas',
            labelWidth:50,
            items : items
        });

        if(values){
            this.setRuleValues(cid, values);
        }

        return fieldSet;
    },

    applyHandler : function(){
        var formulae = "";
        for(var i=0; i<this.fsArray.length; i++) {
            var fsids = this.fsid + this.fsArray[i];

            var operation    = Wtf.getCmp(fsids+'combo').getValue();
            var operator1   = Wtf.getCmp(fsids+'combo1').getValue();
            var operator2   = Wtf.getCmp(fsids+'combo2').getValue();
            if(operator1 != undefined) {
                formulae += operator1+operation+operator2;
            } else {
                formulae += operation+operator2;
            }

        }
        if(this.fsArray.length.length<1){
            ResponseAlert(551);
        }
        if(formulae != "") {
            Wtf.Ajax.requestEx({
                url: Wtf.req.base + '/customColumn.jsp',
                params:{
                    mode:1,
                    fieldlabel:this.fieldlabel,
                    maxlength:this.maxlength,
                    validationType:this.validationType,
                    fieldType:this.fieldType,
                    modulename:this.modulename,
                    moduleid:this.moduleid,
                    isessential:this.isessential,
                    iseditable:false,
                    customregex:this.customRegex,
                    rules:formulae,
                    createFieldFlag : this.createFieldFlag
                }
            },
            this,
            function(res) {
                if(res.msg) {
                    Wtf.Msg.alert('Status', res.msg);
                } else {
                    Wtf.Msg.alert('Status', 'New column created successfully.<br/> Please close the tab and open again to use the new field');
                }
                if(this.createFieldFlag) {
                    loadCustomFieldColModel(this.moduleid);
                }
            },
            function(res) {
                Wtf.Msg.alert('Status', 'Failed to connect with the server.');
            }
            );
            this.close();
            if(this.createFieldFlag) {
                Wtf.getCmp("new_custom_field_window").destroy();
            }
        } else {
            Wtf.Msg.alert('Alert', 'No formulae generated to apply');
        }
    },

    cancelHandler : function(){
        if(this.createFieldFlag) {
            Wtf.getCmp("new_custom_field_window").destroy();
        }
        this.close();

    },

    previousHandler: function(){
        this.close();
        Wtf.getCmp("new_custom_field_window").show();
    }

});










Wtf.SpreadSheet.Panel = function (config){
    Wtf.SpreadSheet.Panel.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.SpreadSheet.Panel, Wtf.Panel,{
    layout:'fit',
    border:false,
    selColIndex : 0,
    selType : "None",
    sCol:3,
    eCol:0,
    initComponent: function(config){
        this.ssDBid = -1;
        this.rules = [];
        if(!this.isDetailPanel)
            this.getMyConfig();

        Wtf.SpreadSheet.Panel.superclass.initComponent.call(this,config);

        this.createSsTbar();
        this.createSelModel();
        this.createColModel();
        this.createSpreadSheetLook();

        this.createGrid();

        this.addEvents2Comp();

        this.add(this.SpreadSheetGrid);

    },

    addEvents2Comp:function(){
        this.SpreadSheetGrid.selModel.on('cellselect',this.setSelectedCellText,this);
        this.SpreadSheetGrid.selModel.on('celldeselect',this.setSelectedCellTextNull,this);
        this.SpreadSheetGrid.on('cellcontextmenu', this.cellcontextmenu, this);
        this.SpreadSheetGrid.on('rowcontextmenu', this.rowcontextmenu, this);

        this.SpreadSheetGrid.selModel.on('columnCellsSelect',this.columnCellsSelectHandler,this);
        this.SpreadSheetGrid.selModel.on('columnCellsDeSelect',this.columnCellsDeSelectHandler,this);
        this.SpreadSheetGrid.selModel.on('selectionchange',this.selectionChangeHandler,this);

        //        this.SpreadSheetGrid.on('render',function(){
        //                this.SpreadSheetGrid.getTopToolbar().getEl().child("table").wrap({tag:'center'});
        //        },this);
        this.SpreadSheetGrid.on('savemystate', this.saveMyStateHandler, this);

    },

    cellcontextmenu:function(grid, rowIndex, cellIndex, e){
        e.stopEvent();
    },
    rowcontextmenu:function(grid, rowIndex, e){
        e.stopEvent();
    },
    getCustomColumnData:function(rData){
        jsondata = ",customfield:[]";
        if(GlobalColumnModel[this.moduleid]){
            jsondata =',customfield:[{';
        for(var cnt = 0;cnt<GlobalColumnModel[this.moduleid].length;cnt++){

        if(cnt > 0){
            jsondata +="},{";
        }
        var recData = rData[GlobalColumnModel[this.moduleid][cnt].fieldname]
        if(GlobalColumnModel[this.moduleid][cnt].fieldtype=="3" && recData!=""){
            var daterec =recData;
            if(typeof(recData)=='string'){
                daterec = Date.parseDate(recData,WtfGlobal.getDateFormat());
            }
            jsondata +="\""+GlobalColumnModel[this.moduleid][cnt].fieldname+"\": \""+WtfGlobal.convertToGenericDate (daterec)+"\",\"filedid\":\""+GlobalColumnModel[this.moduleid][cnt].fieldid+"\",\"xtype\":\""+GlobalColumnModel[this.moduleid][cnt].fieldtype+"\"";
        }else
            jsondata +="\""+GlobalColumnModel[this.moduleid][cnt].fieldname+"\": \""+recData+"\",\"filedid\":\""+GlobalColumnModel[this.moduleid][cnt].fieldid+"\",\"xtype\":\""+GlobalColumnModel[this.moduleid][cnt].fieldtype+"\"";
        }
        jsondata +='}]';
        }
        return jsondata;
    },
    getCustomField:function(){
        var field = [];
        if(GlobalColumnModel[this.moduleid]) {
            for(var cnt = 0;cnt<GlobalColumnModel[this.moduleid].length;cnt++) {
                field.push(GlobalColumnModel[this.moduleid][cnt].fieldlabel);
            }
        }
        return field;
    },
    getCustomValues:function(rData){
        var values = [];
        if(GlobalColumnModel[this.moduleid]) {
            for(var cnt = 0;cnt<GlobalColumnModel[this.moduleid].length;cnt++) {
                if(GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid]) {
                    if(this.customColumnModel[cnt].fieldtype=="7"){ // for multiselet combo
                        var store = GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid];
                        var pushValue=searchValueFieldMultiSelect(store,rData[GlobalColumnModel[this.moduleid][cnt].fieldname],'id','name')

                        values.push(pushValue);
                    }
                    else {
                        var store = GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid];
                        var num = store.find("id", rData[GlobalColumnModel[this.moduleid][cnt].fieldname]);
                        if(num != -1) {
                            values.push(store.getAt(num).get('name'));
                        } else {
                            values.push("");
                        }
                    }
                } else {
                    values.push(rData[GlobalColumnModel[this.moduleid][cnt].fieldname]);
                }
            }
        }
        return values;
    },
    getEmptyCustomFields:function(gridRec){
       if(GlobalColumnModel[this.moduleid]) {
            for(var cnt = 0;cnt<GlobalColumnModel[this.moduleid].length;cnt++) {
                gridRec[GlobalColumnModel[this.moduleid][cnt].fieldname]='';
            }
        }
        return gridRec;
    },
    createGrid:function(){

             if(GlobalColumnModel[this.moduleid]){
        for(var cnt = 0;cnt < GlobalColumnModel[this.moduleid].length;cnt++){
         var typeCustom = 'auto'
         if(this.customColumnModel[cnt].fieldtype == 3){
             typeCustom = 'date'
         }else if(this.customColumnModel[cnt].fieldtype == 2){
             typeCustom = 'int'
         }
         this.store.fields.items.push({
              name:GlobalColumnModel[this.moduleid][cnt].fieldname,
             sortDir:'ASC',
             type:typeCustom
         })
         this.store.fields.keys.push(GlobalColumnModel[this.moduleid][cnt].fieldname);
        }
        }
        this.bbar1 = (this.pagingFlag ? new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                parentGridObj:this.parentGridObj,
                id: "pagingtoolbar" + this.id,
                store: this.store,
                displayInfo: true,
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id,
                    spreadSheet:true
                })
            }):'')
       this.store.reader = new Wtf.data.KwlJsonReader(this.store.reader.meta, this.store.fields.items);
        this.SpreadSheetGrid = new Wtf.SpreadSheet.Grid({
            store        :   this.store,
            isEditor     :   this.isEditor==false?false:true,
            cm           :   (this.cmArray?this.getColModel():this.cm),
            selModel     :   this.getSelModel(),
            view         :   this.getSpreadSheetLook(),
            tbar         :   this.getSsTbar(),
            bbar         :   this.bbar1,
            sCol         :   this.sCol,
            eCol         :   this.eCol,
            stripeRows   :   true,
            id           :   this.gid
        });
        this.SpreadSheetGrid.on("aftersave",function(id,iscustomHeader){
            if(!this.isDetailPanel)
            this.getMyConfig(iscustomHeader);
            //this.createColModel();
        },this)

        this.store.on('load',function(){
            if(this.pagingFlag) {
                this.quickSearchTF.StorageChanged(this.store);
                this.quickSearchTF.on('SearchComplete', function() {
                    this.SpreadSheetGrid.getView().refresh();
                }, this);
            }
        },this);
        this.store.on("datachanged",function(){
            if(this.pagingFlag) {
                if(this.pP.combo)
                    this.quickSearchTF.setPage(this.pP.combo.value);
            }
        },this);
    },

    createSsTbar:function(){
        this.createStyleButtons();

        this.clearFormatBut = new Wtf.Toolbar.Button({
            tooltip:"Clear formatting.",
            iconCls:'sheetBar clear-format-img',
            scope : this,
            handler : function(but, e){
                this.clearFormatHandler();
            }
        });
        this.clearFormatBut.setDisabled(true);

        this.boldBut = new Wtf.Toolbar.Button({
            tooltip:"Bold",
            iconCls:'sheetBar bold-img',
            scope : this,
            handler : function(bold, e){
                this.extraStyleHandler("font-weight", "bold");
            }
        });
        this.boldBut.setDisabled(true);

        this.strikeBut = new Wtf.Toolbar.Button({
            tooltip:"Strikethrough",
            iconCls:'sheetBar strike-img',
            scope : this,
            handler : function(strike, e){
                this.extraStyleHandler("text-decoration", "line-through");
            }
        });
        this.strikeBut.setDisabled(true);

        //        this.blinkBut = new Wtf.Toolbar.Button({
        //            text:" xyz ",
        //            tooltip:"Blink",
        //            style : 'text-decoration:blink;',
        //            scope : this,
        //            handler : function(blink, e){
        //                this.extraStyleHandler("text-decoration", "blink");
        //            }
        //        });
        //        this.blinkBut.setDisabled(true);

        this.alignBut = new Wtf.Toolbar.Button({
            tooltip:"Align",
            iconCls:'sheetBar align-palette-left-img',
            menu: this.alignMenu
        });
        this.alignBut.setDisabled(true);

        this.textColorBut = new Wtf.Toolbar.Button({
            tooltip:"Text color",
            iconCls:'sheetBar text-color-but-img',
            menu: this.textColorMenu
        });
        this.textColorBut.setDisabled(true);
        this.bgColorBut = new Wtf.Toolbar.Button({
            tooltip:"Background color",
            iconCls:'sheetBar bg-color-but-img',
            menu: this.bgColorMenu
        });
        this.bgColorBut.setDisabled(true);
        this.undoBut = new Wtf.Toolbar.Button({
            tooltip:"Undo",
            iconCls:'sheetBar undo-img',
            scope : this,
            handler :this.undoOperation
        });

        this.undoBut.setDisabled(true);

        this.redoBut = new Wtf.Toolbar.Button({
            tooltip:"Redo",
            iconCls:'sheetBar redo-img',
            scope : this,
            handler :this.redoOperation
        });

        this.redoBut.setDisabled(true);

        this.ruleWin = new Wtf.Toolbar.Button({
            text:"Conditional Color Coding",
            iconCls:"applyRulesIcon",
            tooltip:"Change color based on rules.",
            scope:this,
            handler: function(){
                var ruleWin = new Wtf.SpreadSheet.RuleWindow({
                    sSheet : this,
                    grid : this.getGrid()
                });
                ruleWin.show();
                ruleWin.on('ruleApply', this.saveMyRuleHandler, this);
            }
        });

        this.customizeHeader = new Wtf.Action({
            text:"Customize Header",
            iconCls:"pwnd customizeHeader",
            handler:function (){

                this.customizeHeader=new Wtf.customizeHeader({
                    scope:this,
                    modulename:this.SpreadSheetGrid.view.modulename
                });
                Wtf.getCmp("crm_customize_header").show();
            },
            scope:this
        });

        this.customFormulae = new Wtf.Action({
            text:"Add Custom Formulas",
            iconCls:"pwnd customizeHeader",
            handler:function (){
                if(this.selColIndex != -1) {
                    var fieldid = this.colModel.config[this.selColIndex].id;
                    if(fieldid.substr(0, 12) == "custom_field") {
                        if(this.colModel.config[this.selColIndex].sheetEditor != undefined && (this.colModel.config[this.selColIndex].sheetEditor.xtype == "combo"
                                || this.colModel.config[this.selColIndex].sheetEditor.xtype == "datefield" || this.colModel.config[this.selColIndex].sheetEditor.xtype == "timefield")) {
                             Wtf.Msg.alert('Status', 'You can not set formulas to the Combo, Date or Time fields');
                             return;
                        }
                        new Wtf.SpreadSheet.FormulaeWindow({
                            id:this.id+'addCustomFormulae',
                            cm:this.colModel,
                            fieldlabel:this.colModel.config[this.selColIndex].dataIndex,
                            modulename:this.moduleName,
                            moduleid:this.moduleid,
                            createFieldFlag : false
                        });
                    } else {
                        Wtf.Msg.alert('Status', 'You can add formulas to only custom columns. <br/> Please select the custom column.');
                    }
                } else {
                    Wtf.Msg.alert('Status', 'You can add formulas to only custom columns. <br/> Please select the custom column.');
                }
            },
            scope:this
        });

        this.delCustomColumn = new Wtf.Action({
            text:"Delete Custom Column",
            tooltip:{text:'Delete selected custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function (){
                Wtf.MessageBox.confirm('Confirm', 'Are you sure you want to delete?', function(btn){
                    if (btn == "yes") {
                        if(this.selColIndex != -1) {
                            var fieldid = this.colModel.config[this.selColIndex].id;
                            if(fieldid.substr(0, 12) == "custom_field") {
                                fieldid = fieldid.replace("custom_field", "");
                                Wtf.Ajax.requestEx({
                                    url: Wtf.req.base + '/customColumn.jsp',
                                    params:{
                                        mode:3,
                                        fieldlabel:this.colModel.config[this.selColIndex].dataIndex,
                                        modulename:this.moduleName,
                                        moduleid:this.moduleid,
                                        fieldid: fieldid
                                    }
                                },
                                this,
                                function(res) {
                                    if(res.msg) {
                                        Wtf.Msg.alert('Status', res.msg);
                                    }
                                    loadCustomFieldColModel(this.moduleid);
                                    Wtf.masterStore.load();
                                },
                                function(res) {
                                    Wtf.Msg.alert('Status', 'Failed to connect with the server.');
                                });
                            } else {
                                Wtf.Msg.alert('Status', 'You can delete only custom columns. <br/> Please select the custom column to delete.');
                            }
                        } else {
                            Wtf.Msg.alert('Status', 'You can delete only custom columns. <br/> Please select the custom column to delete.');
                        }
                    }
               },this);
            },
            scope:this
        });

        this.addCustomColumn = new Wtf.Action({
            text:"Add Custom Column",
            tooltip:{text:'Add new custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function (){
                addCustomColumn(this);
            },
            scope:this
        });
        var menuArray=[this.customizeHeader];
        if(this.moduleName != "Campaign"){
            menuArray.push(this.addCustomColumn);
            menuArray.push(this.delCustomColumn);
            menuArray.push(this.customFormulae);
        }
        this.customize=new Wtf.Toolbar.Button({
            iconCls: "pwnd customizeHeader",
            text:"Manage Columns",
            menu: menuArray
        });

        this.selectedCellText = new Wtf.form.TextField({
            maxLength:85,
            style : 'text-align:center;',
            width:200,
            readOnly:true
        });
        this.SSEditorText = new Wtf.form.TextField({
            maxLength:300,
            width:210,
            readOnly:true
        });
        this.refreshButton = new Wtf.Toolbar.Button({
            scope:this,
            iconCls:"pwndCRM refreshButton",
            tooltip:{text:'Click to refresh.'},
            handler:function(){this.store.reload();}
        });

        this.sstbar = [];
        this.sstbar.push(this.selectedCellText);
        this.sstbar.push("-");
        this.sstbar.push(this.SSEditorText);
        this.sstbar.push("-");
        this.sstbar.push(this.clearFormatBut);
        this.sstbar.push("-");
        this.sstbar.push(this.boldBut);
        this.sstbar.push(this.strikeBut);
        //        this.sstbar.push(this.blinkBut);
        this.sstbar.push("-");
        this.sstbar.push(this.alignBut);
        this.sstbar.push(this.textColorBut);
        this.sstbar.push(this.bgColorBut);
        this.sstbar.push("-");
        this.sstbar.push(this.undoBut);
        this.sstbar.push(this.redoBut);
        this.sstbar.push(this.ruleWin);
        this.sstbar.push("-");
        if(Wtf.URole.roleid == Wtf.AdminId){
            if(this.parentGridObj.subTab==undefined && this.parentGridObj.newFlag==2){
                this.sstbar.push(this.customize);
                this.sstbar.push("-");
            }
        }
          this.sstbar.push("->");
         this.sstbar.push(this.refreshButton);
    },
    enableDisableUndoButt : function(){
        if (this.parentGridObj.storeFormat.length > 0) {
            this.undoBut.setDisabled(false);
        }else{
            this.undoBut.setDisabled(true);
        }
    },
    enableDisableRedoButt : function(){
        if (this.parentGridObj.redoObject.length > 0) {
            this.redoBut.setDisabled(false);
        }else{
            this.redoBut.setDisabled(true);
        }
    },
    redoOperation : function(){
        if (this.parentGridObj.redoObject.length > 0) {
            var jsonObject=eval('('+this.parentGridObj.redoObject+')');
            this.parentGridObj.storeFormat.push(jsonObject);
            this.enableDisableUndoButt();
            var row;
            var recordid;
            var recordIndex;
            var record;
            var cellStyle;
            var colIndex;
            var recordname;
            var style;
            var lastStyle;
            var sel=[];
            var styleSplit;
            var styleValue;
            var isDeleted;
            for(var i=0; i < jsonObject.length;i++ ){
                row = jsonObject[i];
                recordid = row.id;
                recordIndex = this.store.find(this.keyid,recordid);
                record = this.store.getAt(recordIndex);
                cellStyle=row.cellStyle;
                isDeleted=row.isdeleted;
                for(var key in cellStyle){
                    recordname = key;
                    colIndex=this.colModel.getIndexById(this.colModel.lookup[recordname].id);
                    style = cellStyle[recordname];
                    lastStyle=style.trim();
                    if(style != ""){
                        if(lastStyle != ""){
                            if(isDeleted){
                                styleSplit=lastStyle.split(' ');
                                for(var j = 0; j < styleSplit.length;j++){
                                    style = styleSplit[j];
                                    this.removeSpecificStyleFromRecCell(record,recordname,style);
                                }
                            }else{
                                styleSplit=lastStyle.split(':');
                                styleValue=styleSplit[1].trim();
                                styleValue=styleValue.substring(0,styleValue.length-1);
                                this.addStyleToRecCell(styleSplit[0],styleValue.trim(),recordIndex,colIndex);
                            }
                        }

                    }
                }
                sel.push(record);

            }
            this.refreshMyView(false);
            Wtf.saveStyleInDB(sel,true,undefined,this.keyid,this.moduleName,this.parentGridObj);
            this.parentGridObj.redoObject =new Array() ;
            this.enableDisableRedoButt();
        }
    },
    undoOperation : function(){
        if (this.parentGridObj.storeFormat.length > 0) {
            var jsonObject=this.parentGridObj.storeFormat.pop();
            this.enableDisableUndoButt();

            var row;
            var recordid;
            var recordIndex;
            var record;
            var cellStyle;
            var colIndex;
            var recordname;
            var style;
            var lastStyle;
            var sel=[];
            var isDeleted;
            var tempJsonObject = Wtf.encode(jsonObject);
            for(var i=0; i < jsonObject.length;i++ ){
                row = jsonObject[i];
                recordid = row.id;
                recordIndex = this.store.find(this.keyid,recordid);
                if(recordIndex != -1){
                    record = this.store.getAt(recordIndex);
                    cellStyle=row.cellStyle;
                    isDeleted=row.isdeleted;
                    for(var key in cellStyle){
                        recordname = key;
                        colIndex=this.colModel.getIndexById(this.colModel.lookup[recordname].id);
                        style = cellStyle[recordname];
                        lastStyle = style;
                        lastStyle=lastStyle.trim();
                        if(lastStyle != ""){
                            if(isDeleted){
                                  this.addStylesToRecCell(lastStyle,recordIndex,colIndex);
                            }else{
                                this.removeSpecificStyleFromRecCell(record,recordname,lastStyle);
                            }

                        }
                    }
                    sel.push(record);
                }else{
                    tempJsonObject=eval('('+tempJsonObject+')');
                    tempJsonObject.splice(i,1);
                    tempJsonObject = Wtf.encode(tempJsonObject);
                }
            }
            tempJsonObject=eval('('+tempJsonObject+')');
            if(tempJsonObject.length > 0){
                this.refreshMyView(false);
                Wtf.saveStyleInDB(sel,true,undefined,this.keyid,this.moduleName,this.parentGridObj);
                this.parentGridObj.redoObject = Wtf.encode(tempJsonObject);// for redo operation
                this.enableDisableRedoButt();
            }else{
                this.undoOperation();
            }

        }
    },
    createColModel:function(){
        this.colArr = [];
        this.colArr.push(new Wtf.grid.RowNumberer({width:30}));
        this.colArr.push(this.getSelModel());
        if(this.cmArray)
            for(var i =0; i<this.cmArray.length; i++){
                this.colArr.push(this.cmArray[i]);
            }
           this.customColumnModel = GlobalColumnModel[this.moduleid];
           if(this.customColumnModel){
            for(var cnt=0;cnt<this.customColumnModel.length;cnt++){
                var tempObj=null;
                var editorObj = {
                    xtype:WtfGlobal.getXType(this.customColumnModel[cnt].fieldtype),
                    maxLength:this.customColumnModel[cnt].maxlength,
                    required:this.customColumnModel[cnt].isessential,
                    store:null,
                    useDefault:true
                }
                 if(this.customColumnModel[cnt].fieldtype == 3){
                     editorObj['format']=WtfGlobal.getOnlyDateFormat();
                 }
                if(this.customColumnModel[cnt].fieldtype==4 || this.customColumnModel[cnt].fieldtype==7){
                    editorObj = {
                        xtype:WtfGlobal.getXType(this.customColumnModel[cnt].fieldtype),
                        required:this.customColumnModel[cnt].isessential,
                        store:null,
                        useDefault:true
                    }
                    if(GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid] == null){
                            GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid] = new Wtf.data.Store({
                                url:'jspfiles/customColumn.jsp?mode=2&fieldid='+this.customColumnModel[cnt].fieldid+"_dc="+Math.random(),
                                 reader: new Wtf.data.JsonReader({
                                         root:'data'
                                         }, GlobalComboReader),
                                autoLoad:true
                            });
                      var tempScopeObj = this;
//                      GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid].on("load",function(){
//                          tempScopeObj.SpreadSheetGrid.view.refresh();
//                      })
                    } else {
                        GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid].reload();
                    }
                    editorObj.store=GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid];
                }
                if(this.customColumnModel[cnt].fieldtype==8){// Ref Module Combo
                    editorObj = {
                        xtype:WtfGlobal.getXType(this.customColumnModel[cnt].fieldtype),
                        required:this.customColumnModel[cnt].isessential,
                        store:null,
                        useDefault:true
                    }
                    var store;
                    if(GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid] == null){
                        if(this.customColumnModel[cnt].moduleflag == 1) {
                            var comboid = this.customColumnModel[cnt].comboid;
                            if(comboid == Wtf.common.productModuleID) {
                                store = Wtf.productStore;
                            } else if(comboid == Wtf.common.leadModuleID) {
                                store = Wtf.leadStore;
                            } else if(comboid == Wtf.common.contactModuleID) {
                                store = Wtf.contactStore;
                            } else if(comboid == Wtf.common.caseModuleID) {
                                store = Wtf.caseStore;
                            } else if(comboid == Wtf.common.oppModuleID) {
                                store = Wtf.opportunityStore;
                            }
                        } else {
                            store = new Wtf.data.Store({
                                url: 'Common/CRMManager/getComboData.do',
                                baseParams:{
                                    comboname:this.customColumnModel[cnt].comboname
                                },
                                reader: new Wtf.data.KwlJsonReader({
                                    root:'data'
                                },  Wtf.ComboReader),
                                autoLoad:false
                            });
                        }
                        GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid] = store;
                    } else {
                        store = GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid];
                    }
                    store.load();
                    editorObj.store=GlobalComboStore["cstore"+this.customColumnModel[cnt].fieldid];
                }

                var suffix = this.customColumnModel[cnt].isessential ? " *":"";
                tempObj = {
                    header:this.customColumnModel[cnt].fieldlabel+suffix,
                    id:'custom_field'+this.customColumnModel[cnt].fieldid,
                    sheetEditor: this.customColumnModel[cnt].iseditable=="true" ? editorObj: undefined,
                    dataIndex:this.customColumnModel[cnt].fieldname,
                    pdfwidth:60,
                    sortable:true,
                    xtype:WtfGlobal.getXType(this.customColumnModel[cnt].fieldtype)
                }
                if(this.customColumnModel[cnt].fieldtype == 3){
                     tempObj['renderer']=WtfGlobal.onlyDateRenderer;
                }

               this.colArr.push(tempObj);
            }
          }
        this.colModel = new Wtf.SpreadSheet.ColumnModel(this.colArr);
    },

    createSelModel:function(){
        this.selModel = new Wtf.SpreadSheet.SelectionModel();
    },

    createSpreadSheetLook:function(){
        this.spreadSheetLook = new Wtf.SpreadSheet.Look({
            forceFit : false,
            moduleid:this.moduleid,
            id:this.id,
            gridObjScope: this,
            modulename:this.moduleName
        });
    },

    getSsTbar:function(){
        return this.sstbar;
    },

    getGrid:function(){
        return this.SpreadSheetGrid;
    },

    getColModel:function(){
        return this.colModel;
    },

    getSelModel:function(){
        return this.selModel;
    },

    getSpreadSheetLook:function(){
        return this.spreadSheetLook;
    },

    changeSSEditorText:function(){
        this.SSEditorText = null;
    },

    setSelectedCellText : function(selModel, rowIndex, colIndex){
        this.setSelType(false);
        var header = this.getColModel().getColumnHeaderName(colIndex);
        this.selectedCellText.setValue(header+" X "+(rowIndex+1) );

        var valData = this.getGrid().getView().getCell(rowIndex,colIndex).innerHTML;
        valData = valData.replace("&nbsp;", " ");
        valData = Wtf.util.Format.htmlDecode(valData);
        var val = WtfGlobal.HTMLStripper(valData?valData:"");
        this.SSEditorText.setValue(val);
    },

    setSelectedCellTextNull : function(selModel, rowIndex, colIndex){
        this.setSelType();
        this.selectedCellText.setValue("");
        this.SSEditorText.setValue("");
    },

    columnCellsDeSelectHandler : function(obj, index){
        this.setSelType();
        this.selColIndex = -1;
    },

    columnCellsSelectHandler : function(obj, index){
        this.setSelType();
        this.selColIndex = index;
    },

    selectionChangeHandler : function(sm){
        this.setSelType();
    },

    setSelType : function() {
        var disable = false;
        var sm = this.getSelModel();
        if(sm.selType == "None" || (sm.getSelections().length==0&&sm.selType != "Cell"))disable=true;

        if(sm.selType == 'Cell'){
            if(sm.selection && sm.selection.record && sm.selection.record.get(this.keyid) == "0"){
                disable=true;
            }
        }
        if(!disable && (sm.selType == "Row" ) ){
            var selections=sm.getSelections();
            var record;
            for(var i=0;i< selections.length;i++){
                record=selections[i];
                if(record.get(this.keyid) == "0" ){
                    disable=true;
                    break;
                }
            }
        }
        this.alignBut.setDisabled(disable);
        this.textColorBut.setDisabled(disable);
        this.bgColorBut.setDisabled(disable);
        this.boldBut.setDisabled(disable);
        this.strikeBut.setDisabled(disable);
        //     this.blinkBut.setDisabled(disable);
        this.clearFormatBut.setDisabled(disable);
    },

    createStyleButtons : function(){
        this.alignMenu = new Wtf.menu.AlignMenu();
        this.alignMenu.on('select',function(cm, style){
            var s = style.split(":");
            this.extraStyleHandler(s[0], s[1]);
        },this);


        this.textColorMenu = new Wtf.menu.ColorMenu({
            allowReselect : true
        });
        this.textColorMenu.on('select',function(cm, color){
            this.extraStyleHandler('color', '#'+color);
        },this);


        this.bgColorMenu = new Wtf.menu.ColorMenu({
            allowReselect : true
        });
        this.bgColorMenu.on('select',function(cm, color){
            this.extraStyleHandler('background-color', '#'+color);
        },this);

    },

    //==============================================================================================================================//

    addStyleToRecCell : function(property, value, row, col){
        var hID = this.getColModel().getColumnId(col);
        var rec = this.store.getAt(row);
        if(!rec.data.cellStyle)rec.data.cellStyle={};
        if(!rec.data.cellStyle[hID])rec.data.cellStyle[hID]='';
        rec.data.cellStyle[hID] += " "+property+":"+value+";";

        if(!rec.data.tempCellStyle)rec.data.tempCellStyle={};
        if(!rec.data.tempCellStyle[hID])rec.data.tempCellStyle[hID]='';
        rec.data.tempCellStyle[hID] += " "+property+":"+value+";";

    },
    addStylesToRecCell : function(styles, row, col){
        var hID = this.getColModel().getColumnId(col);
        var rec = this.store.getAt(row);
        if(!rec.data.cellStyle)rec.data.cellStyle={};
        if(!rec.data.cellStyle[hID])rec.data.cellStyle[hID]='';
        rec.data.cellStyle[hID] += " "+styles;
    },

    removeStyleFromRecCell : function(rec, hID, property) {
        if(property && rec.data.cellStyle[hID]) {
            var re = new RegExp(' ' + property + ':[\\w\\s-,#]*;', "g");
            rec.data.cellStyle[hID] = rec.data.cellStyle[hID].replace(re, "","g");
        }
    },

    removeSpecificStyleFromRecCell : function(rec, hID, style) {
        if(rec.data.cellStyle[hID]) {
            style = " "+style;
            var re = new RegExp(style+'(?!.*'+style+')', "g");
            rec.data.cellStyle[hID] = rec.data.cellStyle[hID].replace(re, "");
        }
    },

    clearRecTempCellStyle : function(rec) {
            rec.data.tempCellStyle={};
    },

    clearStyleFromRecCell : function(row, col) {
        var hID = this.getColModel().getColumnId(col);
        var rec = this.store.getAt(row);
        if(rec.data.cellStyle && rec.data.cellStyle[hID]) {
            rec.data.tempCellStyle[hID]=rec.data.cellStyle[hID];
            delete rec.data.cellStyle[hID];
        }
    },

//==============================================================================================================================//

    clearFormatHandler : function(){
      //  ResponseAlert(500);
        var selType = this.getSelModel().selType;
        if(selType == "None") {

        } else if(selType == "Row") {
            this.clearStyleRowHandler();
        } else if(selType == "Column") {
            this.clearStyleColumnHandler();
        } else if(selType == "Cell") {
            this.clearStyleCellHandler();
        }
        this.refreshMyView(false);

    },

    clearStyleCellHandler : function(property, value){
        var sel = [];
        var cell = this.getSelModel().getSelectedCell();
        if(cell){
            var record = this.getGrid().getStore().getAt(cell[0]);
            this.clearRecTempCellStyle(record);
            sel.push(record);
            this.clearStyleFromRecCell(cell[0], cell[1]);
        }
        Wtf.saveStyleInDB(sel,undefined,1,this.keyid,this.moduleName,this.parentGridObj);
    },

    clearStyleColumnHandler : function(property, value){
        var col = this.selColIndex;
        var sel = [];
        var rows = this.getGrid().getStore().getCount();
        for(var row=0; row<rows; row++){
            sel.push(this.getGrid().getStore().getAt(row));
            this.clearStyleFromRecCell(row, col);
        }
        this.fireEvent('addStyle', this, sel);
    },

    clearStyleRowHandler : function(property, value){
        var sel = this.getSelModel().getSelections();
        var cCount = this.getColModel().getColumnCount();
        for(var i=0;i<sel.length;i++){
            this.clearRecTempCellStyle(sel[i]);
            var row = this.getGrid().getStore().indexOf(sel[i]);
            for(var col=this.sCol; col < cCount-this.eCol; col++){
                this.clearStyleFromRecCell(row, col);
            }
        }
        Wtf.saveStyleInDB(sel,undefined,1,this.keyid,this.moduleName,this.parentGridObj);
        this.getSelModel().clearSelections();
    },



//==============================================================================================================================//

    extraStyleHandler : function(propery, value){
      //  ResponseAlert(500);
        var selType = this.getSelModel().selType;
        if(selType == "None") {

        } else if(selType == "Row") {
            this.extraStyleRowHandler(propery, value);
        } else if(selType == "Column") {
            this.extraStyleColumnHandler(propery, value);
        } else if(selType == "Cell") {
            this.extraStyleCellHandler(propery, value);
        }
        this.refreshMyView(false);
    },

    extraStyleCellHandler : function(property, value){
        var sel = [];
        var cell = this.getSelModel().getSelectedCell();
        if(cell){
            var record = this.getGrid().getStore().getAt(cell[0]);
            this.clearRecTempCellStyle(record);
            sel.push(record);
            this.addStyleToRecCell(property, value, cell[0], cell[1]);
        }
       Wtf.saveStyleInDB(sel,undefined,undefined,this.keyid,this.moduleName,this.parentGridObj);
    },

    extraStyleColumnHandler : function(property, value){
        var col = this.selColIndex;
        var sel = [];
        var rows = this.getGrid().getStore().getCount();
        for(var row=0; row<rows; row++){
            sel.push(this.getGrid().getStore().getAt(row));
            this.addStyleToRecCell(property, value, row, col);
        }
        Wtf.saveStyleInDB(sel,undefined,undefined,this.keyid,this.moduleName,this.parentGridObj);
    },

    extraStyleRowHandler : function(property, value){
        var sel = this.getSelModel().getSelections();
        var cCount = this.getColModel().getColumnCount();
        for(var i=0;i<sel.length;i++){
            this.clearRecTempCellStyle(sel[i]);
            var row = this.getGrid().getStore().indexOf(sel[i]);
            for(var col=this.sCol; col < cCount-this.eCol; col++){
                this.addStyleToRecCell(property, value, row, col);
            }
        }
        Wtf.saveStyleInDB(sel,undefined,undefined,this.keyid,this.moduleName,this.parentGridObj);
        this.getSelModel().clearSelections();
    },


    //==============================================================================================================================//

    getReportMenu : function(reports){
        var report;
        var reportArr = [];
        var temp;
        for(var i=0;i<reports.length;i++){
            report=reports[i];
            temp = new Wtf.Action({
                text: report.link,
                tooltip:{
                    text:report.tooltip
                    },
                iconCls:getTabIconCls(Wtf.etype.reportsMenuIcon),
                handler: function(e) {
                    this.openReportTab(parseInt(e.initialConfig.reportid));
                },
                scope: this,
                initialConfig : {
                  reportid : report.id
                }
            });
            reportArr.push(temp);
        }
        if(reportArr.length != 0){
            var tbar=this.getTopToolbar();
            var btnPos = tbar.items.length;
            if(tbar.items.items[tbar.items.length-1].iconCls=="helpButton")
                btnPos = tbar.items.length -1;
            else {
                tbar.add('->');
                btnPos++;
            }
            tbar.insertButton(btnPos,new Wtf.Toolbar.Button({
                iconCls:getTabIconCls(Wtf.etype.reports),
                tooltip: {text: "Click to view reports related to "+this.moduleName+"."},
                scope: this,
                text:"Reports",
                menu: reportArr
            }));
            tbar.insertButton(tbar.items.length-1,new Wtf.Toolbar.Separator());
        }
    },

    openReportTab :  function(name){
        var mainTab="";
        if(this.parentGridObj.subTab && this.parentGridObj.submainTab != undefined){
            mainTab=this.parentGridObj.submainTab;
        }else{
            mainTab=this.parentGridObj.mainTab;
        }
        var val=Object;
        val=getPaneldetails(name,mainTab.id,this.parentGridObj.initialConfig.Rrelatedto,this.parentGridObj.initialConfig.relatedtonameid);
        var title = Wtf.util.Format.ellipsis(val.title,18);
        var panel=Wtf.getCmp(val.id);


        if(panel==null) {
            panel= new Wtf.Panel({
                title:"<span wtf:qtip=\'"+val.tooltip+"\'>"+title+"</span>",
                id:val.id,
                layout:'fit',
                border:false,
                closable:true,
                iconCls:getTabIconCls(Wtf.etype.reports),
                items:new Wtf.AllReportTab({
                    scope:this,
                    head:val.head,
                    layout:'fit',
                    id:val.InnerID,
                    border:false,
                    stageflag:val.stageflag,
                    sourceflag:val.sourceflag,
                    details:val

                })
            });
            mainTab.add(panel);
        }
        mainTab.setActiveTab(panel);
        mainTab.doLayout();
    },
    getMyConfig : function(iscustomHeader){
        ResponseAlert(500);
        var module = this.moduleName;

        if(this.archivedParentName!=undefined){
            module=this.archivedParentName
        }else{
            if(module.endsWith("Contact")){
                module="Contact";
            }
        }

        Wtf.Ajax.requestEx({
//            url: Wtf.req.base + 'spreadSheet/spreadsheet.jsp',
            url: "Common/Spreadsheet/getSpreadsheetConfig.do",
            params:{
                action : 5,
                module : module
            }
        },
        this,
        function(res) {
            var data = res.data[0];
            var header = res.Header;
            this.setMyConfig(data, true, true, true,header);
            if(res.data[1] && !iscustomHeader)
                this.getReportMenu(res.data[1]);
            this.moduleHeader = header;
        },
        function(res) {

        });

    },

    setMyConfig : function(data, applyRule, applyState, refresh,header){
        this.ssDBid = data.cid;
        if(applyRule){
            this.rules = data.rules.rules;
            this.getGrid().view.rules = this.rules;
        }
        if(applyState){
            if(data.state){
                if(data.state.columns != false){
                    this.getGrid().applyState(data.state);
                }
                if(header != undefined){
                   this.getGrid().applyCustomHeader(header);
                }
            }
        }
        if(refresh){
            this.refreshMyView(applyState);
        }
    },

    saveMyStateHandler : function(grid, state){

        for(var i = 0; i < state.columns.length; i++){
            if(state.columns[i].id=='checker'){
                state.columns[i].width=18;
            }
        }

        var module = this.moduleName;
        Wtf.Ajax.requestEx({
            url: "Common/Spreadsheet/saveSpreadsheetConfig.do",
            params:{
                action : 7,
                cid : this.ssDBid,
                module : module,
                state : Wtf.encode(state)
            }
        },
        this,
        function(res) {
            var data;
            if(res.data!=undefined){
                data = res.data[0];
                this.setMyConfig(data, false, false, false,"");
            }
        },
        function(res) {
        });

    },

    saveMyRuleHandler : function(ruleWin, rules, delFlag){
//        if(rules.length>0){
            ResponseAlert(500);
//        }
        var ruleo = {
            rules:rules
        };
        var module = this.moduleName;
        Wtf.Ajax.requestEx({
            url: "Common/Spreadsheet/saveSpreadsheetConfig.do",
            params:{
                action : 6,
                cid : this.ssDBid,
                module : module,
                rules : (rules==""?rules:Wtf.encode(ruleo))
            }
        },
        this,
        function(res) {
            var data = res.data[0];
            this.setMyConfig(data, true, false, true,"");
        },
        function(res) {

        });

    },


    //==============================================================================================================================//

    refreshMyView : function(headers){
  //      ResponseAlert(501);
        this.getSelModel().selType="None";
        this.setSelType();
        var view = this.getGrid().view;
        view.refresh(headers);
    }


});


