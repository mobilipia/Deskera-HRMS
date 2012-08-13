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
Wtf.common.SystemAdmin=function(config){   
    Wtf.common.SystemAdmin.superclass.constructor.call(this,config);
},

Wtf.extend(Wtf.common.SystemAdmin,Wtf.Panel,{


  onRender:function(config){
         Wtf.common.SystemAdmin.superclass.onRender.call(this,config);

          this.usersRec = new Wtf.data.Record.create([
        {name: 'companyid'},
        {name: 'companyname'},
        {name: 'address'},
        {name: 'city'},
        {name: 'country'},
        {name: 'phoneno'},
        {name: 'admin_fname'},
        {name: 'admin_lname'},
        {name: 'admin_uname'},
        {name: 'emailid'}


    ]);

    this.userds = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data",
            totalProperty:'count'
        },this.usersRec),
        url: Wtf.req.base+'UserManager.jsp',
        baseParams:{
            mode:111
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
    this.gridcm= new Wtf.grid.ColumnModel([this.rowNo,this.selectionModel,{
        header: WtfGlobal.getLocaleText("hrms.common.company.name"),
        dataIndex: 'companyname',
        autoWidth : true,
        sortable: true,
        groupable: true

    },
    {
        header: WtfGlobal.getLocaleText("hrms.common.administrator"),
        dataIndex: 'admin_uname',
        renderer:function(v,m,r){
            return r.get('admin_fname')+" "+r.get('admin_lname')+" ("+v+")";
        }
    },
    {
        header: WtfGlobal.getLocaleText("hrms.EmailTemplateCmb.Email"),
        dataIndex: 'emailid',
        renderer:WtfGlobal.renderEmailTo
    },
    {
        header: WtfGlobal.getLocaleText("hrms.common.address"),
        dataIndex: 'address',
        autoWidth : true,
        sortable: true,
        groupable: true

    },{
        header :WtfGlobal.getLocaleText("hrms.recruitment.profile.City"),
        dataIndex: 'city',
	    autoSize : true,
        sortable: true,
        groupable: true
    },
    {
        header :WtfGlobal.getLocaleText("hrms.common.contact.no"),
        dataIndex: 'phoneno',
        autoSize : true,
        sortable: true,
        groupable: true
    }
    
 ]);

    this.usergrid = new Wtf.KwlGridPanel({
        layout:'fit',
        store: this.userds,
        cm: this.gridcm,
        sm : this.selectionModel,
        border : false,
        id:'comapnylistgrid',
        displayInfo:true,
        trackMouseOver: true,
        stripeRows: true,
        searchLabel:WtfGlobal.getLocaleText("hrms.common.QuickSearch"),
        searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search.company.name"),
        searchField:"companyname",
        viewConfig: {
            forceFit:true
        },
        bbar:['-',
            {
                text:WtfGlobal.getLocaleText("hrms.common.view.company.users"),
                iconCls:"list",
                handler:this.viewUser.createDelegate(this)
                },'-',
                {
                text:WtfGlobal.getLocaleText("hrms.common.CreateCompany"),
                iconCls:"createcompany",
                scope:this,
                handler:function(){
                    this.CreateCompany();
                }
        },'-',
    {
        text:WtfGlobal.getLocaleText("hrms.common.delete.company"),
        scope:this,     
        iconCls:"delete",
        handler:function(){             
                    if(this.selectionModel.hasSelection())
                    {
                        Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("hrms.common.Alert"),
                            msg:WtfGlobal.getLocaleText("hrms.common.want.delete.company"),
                            buttons:Wtf.MessageBox.YESNO,
                            scope:this,
                            fn:function(button){
                                if(button=='yes')
                                {
                                    this.deletecompanies();
                                }
                            }
                        });
                    }else{
                        Wtf.MessageBox.show({
                            // title: 'Not Selected',
                            msg: WtfGlobal.getLocaleText("hrms.common.select.single.company"),
                            buttons: Wtf.MessageBox.OK

                        });
                    }
        }
    }]
    });

       this.add( this.usergrid);
    },

 showUserForm:function(isEdit){
    var rec=null;
    if(isEdit){
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.EditUser"), WtfGlobal.getLocaleText("hrms.common.select.one.record.edit"));
            return;
        }

        rec = this.usergrid.getSelectionModel().getSelected();
    }
    var cu=new Wtf.common.CreateUser({isEdit:isEdit,record:rec});
    cu.on('save',this.genSuccessResponse, this);
    cu.on('notsave',this.genFailureResponse, this);
},

    genSuccessResponse:function(response){
         Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.company.management"), response.msg);
        if(response.success==true)this.userds.reload();
    },

    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.company.management"), msg);
    },
    CreateCompany:function(){
         var p = Wtf.getCmp("createcompany");
    if(!p){
       var comwin= new Wtf.common.CreateCompany({
            title:WtfGlobal.getLocaleText("hrms.common.CreateCompany"),
            id:'createcompany',
            closable: true,
            modal: true,
            iconCls : 'deskeralogo',
            width: 400,
            height: 450,
            resizable: false,
            layout: 'fit',
            buttonAlign: 'right'

        });
        comwin.show();
        this.userds.load({
        params:{
               start:0,
               limit:15
            }
        });
    }

    },

       deletecompanies:function(){
        this.delkey=this.selectionModel.getSelections();
            this.companyids=[];
            for(var i=0;i<this.delkey.length;i++){
                this.companyids.push(this.delkey[i].get('companyid'));
            }

        Wtf.Ajax.requestEx({
            url: Wtf.req.base + 'UserManager.jsp',
            params:  {
                mode:113,
                cmpid:this.companyids
            }
        },
        this,
        function(){
            // this.st.load();
            this.userds.load({
                params:{start:0,limit:15}
            });
            msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.companies.deleted.successfully")],1,1);
         

        },

        function(){
            Wtf.Msg.alert( WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.error.deleting.companies"));

        });
    },
    viewUser:function(){
             var rec = this.usergrid.getSelectionModel().getSelected();
              if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
                        Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.view.user"), WtfGlobal.getLocaleText("hrms.common.select.one.company"));
                        return;
                    }
                var panel = Wtf.getCmp('companyuser'+rec.get('companyid'));
                if(!panel){
                             panel = new  Wtf.CompanyUser({
                                    title:WtfGlobal.getLocaleText({key:"hrms.common.users.of", params:[rec.get('companyname')]}),
                                    layout : 'fit',
                                     id : 'companyuser'+rec.get('companyid'),
                                     iconCls:'usermanage',
                                     closable:true,
                                     companyid:rec.get('companyid'),
                                     companyname:rec.get('companyname'),
                                     border:false
                            });

                             Wtf.getCmp('as').add(panel);
                             Wtf.getCmp('as').setActiveTab(panel);
                }else{
                       Wtf.getCmp('as').setActiveTab(new  Wtf.CompanyUser({
                                    title:WtfGlobal.getLocaleText({key:"hrms.common.users.of", params:[rec.get('companyname')]}),
                                    layout : 'fit',
                                     id : 'companyuser'+rec.get('companyid'),
                                     iconCls:'usermanage',
                                     closable:true,
                                     companyid:rec.get('companyid'),
                                     companyname:rec.get('companyname'),
                                     border:false
                       }));
                       Wtf.getCmp('as').setActiveTab(panel);
                 }
                Wtf.getCmp('as').doLayout();
    }
});
