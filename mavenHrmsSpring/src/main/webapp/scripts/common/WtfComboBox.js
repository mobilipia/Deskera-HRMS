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
Wtf.form.FnComboBox=function(config){
    this.initial="REC";
    Wtf.form.FnComboBox.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.form.FnComboBox,Wtf.form.ComboBox,{
    addNewDisplay: WtfGlobal.getLocaleText("hrms.common.AddNew"),
    initComponent:function(config){
        Wtf.form.FnComboBox.superclass.initComponent.call(this, config);
        this.addNewID=this.initial+this.store.id;
        if(this.valueField&&this.valueField===this.displayField)
            this.addNewID=this.addNewDisplay;
        this.addLastEntry(this.store);
        this.store.on('load',this.addLastEntry,this);
        this.on('beforeselect',this.callFunction, this);
        if(this.hirarchical){
            this.tpl=new Wtf.XTemplate('<tpl for="."><div class="x-combo-list-item">{[this.getDots(values.level)]}{'+this.displayField+'}</div></tpl>',{
                getDots:function(val){
                    var str="";
                    for(var i=0;i<val;i++)
                        str+="....";
                    return str;
                }
            })
        }
    },

    addLastEntry:function(s){
        var recid=s.find(this.valueField||this.displayField,this.addNewID);
        if(recid==-1){
            var comboRec=Wtf.data.Record.create(s.fields);
            var rec=new comboRec({});
            s.insert(0,rec);
            rec.beginEdit();
            rec.set(this.valueField||this.displayField, this.addNewID);
            rec.set(this.displayField, this.addNewDisplay);
            rec.endEdit();
        }
    },

    callFunction:function(c,r){
        if(r.data[this.valueField]==this.addNewID){
            this.collapse();
            this.addNewFn();
            return false;
        }
    }
});
