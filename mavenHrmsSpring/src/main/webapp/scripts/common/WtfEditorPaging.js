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

Wtf.EditorPagingToolbar = Wtf.extend(Wtf.PagingToolbar, {
    searchField: null,
    paramNames : {start: 'start', limit: 'limit', ss: 'ss',searchArray:'searchArray'},
    doLoad: function(start) {
        var o = {}, pn = this.paramNames;
        this.myStart=start;
        o[pn.start] = start;
        o[pn.limit] = this.pageSize;
        if(this.searchField) {
            o[pn.ss] = this.searchField.getValue();
            //o[pn.searchArray] = this.searchField.GetSearchArray();
        } else {
            o[pn.ss] = "";
            o[pn.searchArray] = "";
        }
/*        var count=0;
        this.store.on('datachanged',this.onDataChange,this);
       
            this.store.clearFilter(true);
       

        this.store.filterBy(function(record,id){
            count++;
            if(count > start && count<= start+this.pageSize)
                return true;
            else
                return false;
        },this);*/


        this.store.load({params:o});
    },

    onDataChange : function(){
//alert("dataChange");
        if(!this.rendered){
           // this.dsLoaded = [store, r, o];
            return;
        }
       this.cursor = this.myStart;//o.params ? o.params[this.paramNames.start] : 0;
       var d = this.getPageData(), ap = d.activePage, ps = d.pages;

       this.afterTextEl.el.innerHTML = String.format(this.afterPageText, d.pages);
       this.field.dom.value = ap;
       this.first.setDisabled(ap == 1);
       this.prev.setDisabled(ap == 1);
       this.next.setDisabled(ap == ps);
       this.last.setDisabled(ap == ps);
       this.loading.enable();
       this.updateInfo();
    },
    onDataChange1 : function(store){

        if(!this.rendered){
           // this.dsLoaded = [store, r, o];
            return;
        }
       this.cursor = 0;
        var total = store.getCount();
    
          var  activePage = Math.ceil((this.cursor+this.pageSize)/this.pageSize);
           var pages =  0;
           if(total < this.pageSize )
              pages= 1
           else
              pages= Math.ceil(total/this.pageSize)
         var ap=activePage;
         var ps = pages
       this.afterTextEl.el.innerHTML = String.format(this.afterPageText, pages);
       this.field.dom.value = ap;
        this.first.setDisabled(ap == 1);
       this.prev.setDisabled(ap == 1);
       this.next.setDisabled(ap == ps);
       this.last.setDisabled(ap == ps);
       this.loading.enable();
       this.updateInfo1();
    },
    updateInfo1 : function(){
        if(this.displayEl){
            var count = this.store.getCount();
            var msg = count == 0 ?
                this.emptyMsg :
                String.format(
                    this.displayMsg,
                    this.cursor+1, this.cursor+count, this.store.getCount()
                );
            this.displayEl.update(msg);
        }
    }
});

