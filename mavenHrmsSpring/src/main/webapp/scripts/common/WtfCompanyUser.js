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
Wtf.CompanyUser = function(config){
     Wtf.CompanyUser.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.CompanyUser, Wtf.Panel, {
   onRender:function(config){
       Wtf.CompanyUser.superclass.onRender.call(this,config);

        this.usersRec = new Wtf.data.Record.create([
                {name: 'userid'},
                {name: 'username'},
                {name: 'fname'},
                {name: 'lname'},
                {name: 'image'},
                {name: 'emailid'},
                {name: 'lastlogin',type: 'date'},
                {name: 'aboutuser'},
                {name: 'address'},
                {name:'contactno'}
            ]);

            this.userds = new Wtf.data.Store({
                reader: new Wtf.data.KwlJsonReader({
                    root: "data",
                    totalProperty:'count'
                },this.usersRec),
                url: Wtf.req.base+'UserManager.jsp',
                baseParams:{
                    mode:112,
                    companyid:this.companyid
                }
            });
            this.userds.load({
                params:{
                    start:0,
                    limit:15
                }
            });

  this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
  this.rowNo=new Wtf.grid.RowNumberer();
            this.gridcm= new Wtf.grid.ColumnModel([this.rowNo,{
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username',
                autoWidth : true,
                sortable: true,
                groupable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.name"),
                dataIndex: 'fullname',
                autoWidth : true,
                sortable: true,
                groupable: true,
                renderer : function(value,p,record){
                    return (record.data["fname"] + " " + record.data["lname"]);
                }
            },{
                header :WtfGlobal.getLocaleText("hrms.common.EmailAddress"),
                dataIndex: 'emailid',
                autoSize : true,
                sortable: true,
                renderer: WtfGlobal.renderEmailTo,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.last.login"),
                dataIndex: 'lastlogin',
                renderer:WtfGlobal.dateRenderer,
                autoSize : true,
                sortable: true,
                groupable: true
            },{
                header :WtfGlobal.getLocaleText("hrms.common.address"),
                dataIndex: 'address',
                autoSize : true,
                sortable: true,
                groupable: true
            }]);
             this.usergrid = new Wtf.KwlGridPanel({
                store: this.userds,
                cm: this.gridcm,
                border : false,
                displayInfo:true,
                trackMouseOver: true,
                stripeRows: true,
                searchLabel:WtfGlobal.getLocaleText("hrms.common.QuickSearch"),
                searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search.username"),
                searchField:"username",
                loadMask : true,
                viewConfig: {
                    forceFit:true
                }
           });
           this.add( this.usergrid);
	}
});
