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

Wtf.editorSearch = function(config){
    Wtf.editorSearch.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.editorSearch, Wtf.form.TextField, {
    Store: null,
    StorageArray: null,
    initComponent: function(){
        Wtf.editorSearch.superclass.initComponent.call(this);
        this.addEvents({
            'SearchComplete': true
        });
    },
    onRender: function(ct, position){
        Wtf.editorSearch.superclass.onRender.call(this, ct, position);
        this.el.dom.onkeyup = this.onKeyUp.createDelegate(this);
    },
    onKeyUp: function(e){

    if(this.Store)
        {
             this.Store.clearFilter(true);
             var paging= Wtf.getCmp("paggintoolbar"+this.parentId);
            if (this.getValue() != "") {
                this.Store.removeAll();
                var i = 0;
                while (i < this.StorageArray.length) {
                    var str=new RegExp("^"+this.getValue()+".*$|\\s"+this.getValue()+".*$","i");

                    if (str.test(this.StorageArray[i].get(this.field))) {
                        this.Store.add(this.StorageArray[i]);
                    }
                    i++;
                }
                paging.onDataChange1(this.Store);

            }
            else {
                this.Store.removeAll();
                for (i = 0; i < this.StorageArray.length; i++) {
                    this.Store.insert(i, this.StorageArray[i]);
                }
                 paging.doLoad(0);
            }
        }
        this.fireEvent('SearchComplete', this.Store);
    },
    StorageChanged: function(store){
        this.Store = store;
        this.StorageArray = this.Store.getRange();
    }
});
Wtf.reg('MyQuickSearch1', Wtf.editorSearch);



