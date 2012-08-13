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
Wtf.common.UserGrid=function(config){

    this.usersRec = new Wtf.data.Record.create([
        {name: 'userid'},
        {name: 'employeeid'},
        {name: 'username'},
        {name: 'designation'},
        {name: 'designationid'},
        {name: 'department'},
        {name: 'departmentname'},
        {name: 'fname'},
        {name: 'lname'},
        {name: 'image'},
        {name: 'emailid'},
        {name: 'lastlogin',type: 'date'},
        {name: 'aboutuser'},
        {name: 'address'},
        {name:'contactno'},
        {name:'manager'},
        {name:'salary'},
        {name:'roleid', mapping:'role'},
        {name:'accno'},
        {name: 'rolename'}
    ]);

    this.userds = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data",
            totalProperty:"count"
        },this.usersRec),
        url: Wtf.req.base+'UserManager.jsp',
        baseParams:{
            mode:11
        }
    });
    this.userds.load();

    this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        
    this.gridcm= new Wtf.grid.ColumnModel([this.selectionModel,{
        dataIndex: 'image',
        width : 30,
        renderer : function(value){
            if(!value||value == ""){
                value = Wtf.DEFAULT_USER_URL;
            }
            return String.format("<img src='{0}' style='height:18px;width:18px;vertical-align:text-top;'/>",value);
        }
    },{
        header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
        dataIndex: 'employeeid',
        autoWidth : true,
        sortable: true,
        groupable: true
    },{
        header: WtfGlobal.getLocaleText("hrms.common.UserName"),
        dataIndex: 'username',
        autoWidth : true,
        sortable: true,
        groupable: true
    },{
        header: WtfGlobal.getLocaleText("hrms.EmailTemplateCmb.Name"),
        dataIndex: 'fullname',
        autoWidth : true,
        sortable: true,
        groupable: true,
        renderer : function(value,p,record){
            return (record.data["fname"] + " " + record.data["lname"]);
        }
    },{
        header: WtfGlobal.getLocaleText("hrms.common.department"),
        dataIndex: 'departmentname',
        autoWidth : true,
        sortable: true,
        groupable: true
    },
    {
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
    }
]);
//	this.createMenuItems();
     var btnArr=['-','->'];
     
      if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.manageuser))

       if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.assignperm))
    btnArr.push('-', new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.common.EditUser"),
                scope:this,
                iconCls:'iconeditClass',
                handler:function(){
                    this.showUserForm(true)
                }
            })
);
    btnArr.push('-',this.assignPermBtn =  new Wtf.Toolbar.Button({
        text : WtfGlobal.getLocaleText("hrms.activityList.assignperm"),
        id : "permissions"+this.id,
        allowDomMove:false,
        iconCls : 'pwnd permicon',
        scope : this,
        handler : this.requestPermissions
   }));

   if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.assignmanager))
    btnArr.push('-',this.assignManBtn = new Wtf.Toolbar.Button({
                    text : WtfGlobal.getLocaleText("hrms.activityList.assignmanager"),
                    iconCls : 'pwnd managericon',
                    allowDomMove:false,
                    disabled : true,
                    scope : this,
                    handler : function(){
                        this.assignManager();
                    }
    }));


    this.usergrid = new Wtf.KwlGridPanel({
        layout:'fit',
        store: this.userds,
        cm: this.gridcm,
        loadMask:true,
        sm : this.selectionModel,
        searchField:"username",
        searchEmptyText:WtfGlobal.getLocaleText("hrms.common.search.username"),
        border : false,
        viewConfig: {
            forceFit:true
        },
        bbar:btnArr
    });

    this.UsergridPanel  = new Wtf.Panel({
        //id : "Usergridpanel"+this.id,
        title:WtfGlobal.getInstrMsg(WtfGlobal.getLocaleText("hrms.common.double.click.view.profile")),
        autoLoad : false,
        paging : false,
        layout : 'fit',
        items:[this.usergrid]
    });    

    this.innerpanel = new Wtf.Panel({
        layout : 'fit',
        cls : 'backcolor',
        border : false,
        items:[this.UsergridPanel ]  
    }); 

    Wtf.apply(this,{
        layout : "fit",
        defaults:{border:false,bodyStyle:"background: transparent;"},
        loasMask:true,
        autoScroll:true,
        items:[this.usergrid]
    });

    Wtf.common.UserGrid.superclass.constructor.call(this,config);
    this.usergrid.getSelectionModel().on('selectionchange',this.enableDisable,this);
}, 
 
Wtf.extend(Wtf.common.UserGrid,Wtf.Panel,{

enableDisable:function(sm) {
    if(sm.hasSelection()){
       this.assignManBtn.enable();
    } else {
       this.assignManBtn.disable();
    }
},

showUserForm:function(isEdit){
    var rec=null;
    if(isEdit){
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.EditUser"), WtfGlobal.getLocaleText("hrms.admin.EditUser.msg"));
            return;
        }

        rec = this.usergrid.getSelectionModel().getSelected();
    }
    var cu=new Wtf.common.CreateUser({isEdit:isEdit,record:rec});
    cu.on('save',this.genSuccessResponse, this);
    cu.on('notsave',this.genFailureResponse, this);
},

deleteUser:function(){ 
    if(this.usergrid.getSelectionModel().hasSelection()==false){
        Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.user.deletion"), WtfGlobal.getLocaleText("hrms.common.select.user.delete"));
        return;
    }
    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.delete.selected.user"), function(btn){
        if(btn!="yes") return;
        var rec = this.usergrid.getSelectionModel().getSelections();
        var userids=[];
        for(var i=0;i<rec.length;i++){
            userids.push(rec[i].get('userid'));
        }
        Wtf.Ajax.requestEx({
            url: Wtf.req.base+'UserManager.jsp',
            params: {
                mode:13,
                userids:userids
            }
        },this,this.genSuccessResponse,this.genFailureResponse);
    },this);
},

changePassword:function(isEdit){
    var password="";
    if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
        Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.ChangePassword"), WtfGlobal.getLocaleText("hrms.common.select.one.user"));
        return;
    }
    if(!isEdit){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.reset.password"), function(btn){
            if(btn!="yes") return;
            var rec = this.usergrid.getSelectionModel().getSelected();
            this.confirmChangePassword(rec,password);
        },this);
    }else{
        this.createPasswordWindow();
    }

},

confirmChangePassword:function(rec,password){
    //if(password.length>0)password=hex_sha1(password);
    Wtf.Ajax.requestEx({
        url: Wtf.req.base+'UserManager.jsp',
        params: {
            mode:14,
            password:password,
            userid:rec.get('userid')
        }
    },this,this.genSuccessResponse,this.genFailureResponse);
},

    genSuccessResponse:function(response){
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.UserManagement"),response.msg);
        if(response.success==true)this.userds.reload();
        this.enable();
    },

    genFailureResponse:function(response){ 
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('User Management',msg);
        this.enable();
    },

    requestPermissions:function(){
        var rec=null;
//        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
//            Wtf.MessageBox.alert("User Permissions", "Please select one user");
//            return;
//        }

        rec = this.usergrid.getSelectionModel().getSelected();
        var permWindow=new Wtf.common.Permissions({
            title:WtfGlobal.getLocaleText("hrms.common.UserPermissions"),
            resizable: false,
            modal:true
            //userid:rec.get('userid')

        });
        permWindow.show();
    },

    createPasswordWindow:function(rec){
        this.form=new Wtf.form.FormPanel({
//            frame:true,
            url: Wtf.req.base+'UserManager.jsp?mode=3',
            labelWidth: 150,
            region:'center',
            border:false,
            bodyStyle:'background:transparent;padding:15px',
            cls : 'formstyleClass2',
            defaults: {width: 175},
            defaultType: 'textfield',
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.NewPassword"),
                name:'password',
                inputType:'password',
                maxLength:60,
                id:'pwdadminuser'
            },{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.confirm.new.password"),
                inputType:'password',
                name:'cpassword',
                vtype:'password',
                id:'cpwdadminuser',
                initialPassField: 'pwdadminuser'
            }]
        });

        this.win=new Wtf.Window({
            title: WtfGlobal.getLocaleText("hrms.common.ChangePassword"),
            closable:true,
            width:400,
            height:220,
            modal:true,
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'border',
            items:[{
                height:65,
                border:false,
                cls : 'formstyleClass3',
                region:'north',
                html:getTopHtml(WtfGlobal.getLocaleText("hrms.common.ChangePassword"), WtfGlobal.getLocaleText("hrms.common.ChangePassword"))
            },this.form],
            buttons:[{
                text:WtfGlobal.getLocaleText("hrms.common.set"),
                handler:function(){
                    if(this.form.getForm().isValid()==false)return;
                    this.disable();
                    var rec = this.usergrid.getSelectionModel().getSelected();
                    this.confirmChangePassword(rec,this.form.getForm().getValues().password);
                    this.win.close();
                    this.win.destroy();
                },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                handler:function(){
                    this.win.close();
                    this.win.destroy();
                },
                scope:this
            }]
        });this.win.show();
    },
    assignManager:function(){
        if(this.usergrid.getSelectionModel().getCount()==0)
            Wtf.Msg.alert( WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.select.record.first"));

        else{

            this.sm = new Wtf.grid.CheckboxSelectionModel({
                singleSelect:false
            });

            this.cm2 = new Wtf.grid.ColumnModel(
                [
                this.sm,
                {
                    header: WtfGlobal.getLocaleText("hrms.EmailTemplateCmb.Name"),
                    dataIndex: 'username',
                    sortable: true

                },
                {
                    header: WtfGlobal.getLocaleText("hrms.common.designation"),
                    dataIndex: 'designation',
                    sortable: true
                }
                ]);

//            this.saveassignmanager= new Wtf.Button({
//                text:'Save',
//                handler:this.saveassignManager,
//                scope:this
//            });
//
//            this.cancelBtn=new Wtf.Button({
//                text: 'Cancel',
//                scope:this,
//                handler:function(){
//
//                    this.recWindow.close();
//                }
//            })

            this.recGrid=new Wtf.grid.GridPanel({
                cm:this.cm2,
                store:Wtf.managerStore,
                sm:this.sm,
                viewConfig: {
                    forceFit: true
                }/*,
                tbar:[
                this.saveassignmanager
                ]*/
            });

            if(!Wtf.StoreMgr.containsKey("manager")){
                Wtf.managerStore.load();
                Wtf.StoreMgr.add("manager",Wtf.managerStore)
            }

            //this.headingType="Assign Manager";
            this.recPanel= new Wtf.Panel({
                frame:true,
                border: false,
                layout:'fit',
                autoScroll:false,
                items:[{
                    border:false,
                    region:'center',
                    layout:"border",
                    items:[{
                        region : 'north',
                        height : 75,
                        border : false,
                        bodyStyle : 'background:white;border-bottom:1px solid #FFFFFF;',
                        //html:this.isview?getTopHtml(this.headingType,""):getTopHtml(this.headingType,"Select Manager")
                        //html:this.getTopHtml("Competency","Fill up the following form")
                        html: getTopHtml(WtfGlobal.getLocaleText("hrms.activityList.assignmanager"), WtfGlobal.getLocaleText("hrms.common.select.following.manager"))
                    },{
                        border:false,
                        region:'center',
                        cls : 'panelstyleClass2',
                        layout:"fit",
                        items: [this.recGrid]

                    }]
                }]
            });
             var assmngrbtn=new Array();
             if(!WtfGlobal.EnableDisable(Wtf.UPerm.assignmanager, Wtf.Perm.assignmanager.manage))
                 {
             assmngrbtn.push({
                        text: WtfGlobal.getLocaleText("hrms.common.Save"),
                        handler:this.saveassignManager,
                        scope:this
                    });
                 }
                 assmngrbtn.push({
                        text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                        scope:this,
                        handler:function(){
                        this.recWindow.close();
                        }
                    });

            this.recWindow=new Wtf.Window({
                iconCls:getButtonIconCls(Wtf.btype.winicon),
                layout:'fit',
                closable:true,
                width:600,
                title:WtfGlobal.getLocaleText("hrms.activityList.assignmanager"),
                height:600,
                border:false,
                modal:true,
                scope:this,
                plain:true,
                
            buttonAlign :'right',
            buttons: assmngrbtn ,
                items: [this.recPanel]
            });
            this.recWindow.show()
        }
    }, 
    saveassignManager:function(){
        if(this.recGrid.getSelectionModel().getCount()==0)
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.select.record.first"));
        else{
            this.user=this.usergrid.getSelectionModel().getSelections();
            this.userids=[];
            this.assignflag='true';
            for(var i=0;i<this.user.length;i++){
                this.userids.push(this.user[i].get('userid'));
            }
            this.man=this.recGrid.getSelectionModel().getSelections();
            this.managerids=[];
            for(var i=0;i<this.man.length;i++){
                this.managerids.push(this.man[i].get('userid'));
            }
            for(var i=0;i<this.user.length;i++){
                for(var j=0;j<this.man.length;j++){
                    if(this.user[i].get('userid')==this.man[j].get('userid')){
                        this.assignflag='false';
                        break;
                    }
                }
            }   
            if(this.assignflag=='false'){
                this.recWindow.close();
                msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.PleaseSelValidRecord")]);
            }
            else{
                Wtf.Ajax.requestEx({
                    url:  Wtf.req.base + 'hrms.jsp',
                    params:  {
                        flag:137,
                        userid:this.userids,
                        managerid:this.managerids
                    }
                },
                this,
                function(){
                    this.recWindow.close();
                    msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.common.manager.assigned.successfully")],1,1);
                    this.userds.load();
                },
                function(){
                    Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.admin.Errorinassigningmanager"));

                })
            }
        }
    }
}); 
