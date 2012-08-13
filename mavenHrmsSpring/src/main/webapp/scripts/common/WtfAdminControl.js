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
Wtf.common.MainAdmin = function(config){
    Wtf.common.MainAdmin.superclass.constructor.call(this,config);
     this.addEvents({
        'adminclicked':true,
        'featureclicked': true,
        "masterclicked":true
    });
    this.on("adminclicked",this.handleAdminClick,this);
    this.on("featureclicked",this.handleFeatureClick,this);
    this.on("masterclicked", this.handleMasterClick, this);
    this.actTab=null;
}

Wtf.extend(Wtf.common.MainAdmin,Wtf.Panel,{
    handleAdminClick:function(){
        this.actTab=0;
        if(this.tabpanel)
            this.tabpanel.setActiveTab(0);
    },

    handleFeatureClick:function(){
        this.actTab=1;
        if(this.tabpanel)
            this.tabpanel.setActiveTab(3);
    },

    handleMasterClick:function(){
        this.actTab=1;
        if(this.tabpanel)
            this.tabpanel.setActiveTab(1);
    },

    onRender : function(config){
        Wtf.common.MainAdmin.superclass.onRender.call(this,config);
        this.adminFeatures=new Wtf.common.Features({
            title:'feature Administration',
            border:false
        });

        this.masterconfig = new Wtf.MasterConfigurator({
            layout:"fit",
            title:WtfGlobal.getLocaleText("hrms.administration.master.configuration"),
            border:false,
            id:"masterConfigTab",
            iconCls:getTabIconCls(Wtf.etype.hrmsmaster)
        });

        this.tabpanel = this.add(new Wtf.TabPanel({
            title:'dfdf',
            id : 'subtabpanel'+this.id,
            border : false,
            activeItem : 0
        }));

        //this.tabpanel.add(this.adminUser);
        this.tabpanel.add(this.masterconfig);
    }   
});

