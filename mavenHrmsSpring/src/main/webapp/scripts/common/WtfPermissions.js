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
Wtf.common.Permissions=function(config){
    this.PerRecord=new Wtf.data.Record.create(
        ['featureid','permission']
     );

     this.PerStore = new Wtf.data.Store({
//        url: Wtf.req.base+'UserManager.jsp',
        url:"Common/Permission/getPermissionCode.common",
        baseParams:{
            mode:7
        },
        reader: new Wtf.data.KwlJsonReader({
            root: 'data'
        },this.PerRecord)
     });
    this.PerStore.on('load',this.checkActivities,this);
    this.roleRecord=new Wtf.data.Record.create(
        ['roleid','rolename']
    );
    this.roleStore = new Wtf.data.Store({
//        url: Wtf.req.base+'UserManager.jsp',
        url:"Common/Permission/getRoles.common",
        baseParams:{
            mode:8
        },
        reader: new Wtf.data.KwlJsonReader({
            root: 'data'
        },this.roleRecord)
    });
    this.roleCmb= new Wtf.form.ComboBox({
        fieldLabel:WtfGlobal.getLocaleText("hrms.common.Role"),
        hiddenName:'roleid',
        store:this.roleStore,
        valueField:'roleid',
        displayField:'rolename',
        mode: 'local',
        anchor:'90%',
        triggerAction: 'all',
        editable : false
    });
    this.applyBtn=new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.Apply"),
            scope:this,
            handler:this.ApplyPermission,
            minWidth:75

    });
    Wtf.apply(this,{
        buttons:[this.applyBtn,{
            text:WtfGlobal.getLocaleText("hrms.common.Close"),
            scope:this,
            handler:this.cancel
        }]
    },config);
    Wtf.common.Permissions.superclass.constructor.call(this,config);
}

Wtf.extend( Wtf.common.Permissions,Wtf.Window,{
    title:WtfGlobal.getLocaleText("hrms.common.AssignPermission"),
    id:'AP',
    height:500,
    width:500,
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
        if(Wtf.isIE7 || Wtf.isSafari || Wtf.isIE6)
            this.width=550;
        this.featureRecord=new Wtf.data.Record.create(
            ['featureid','featurename','displayfeaturename']
        );

        this.featureStore = new Wtf.data.Store({
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/Permission/getFeatureList.common",
            baseParams:{mode:1},
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            },this.featureRecord)
        });
        this.featureStore.on('load',this.loadActivities,this);
        this.roleStore.on('load',function(){
            this.roleCmb.setValue(1);
            var length= this.roleStore.getCount();
            var rec=new this.roleRecord({
                roleid:1234,
                rolename:WtfGlobal.getLocaleText("hrms.payroll.addnew")
            });
            this.roleStore.insert(length,rec);
            this.loadPermissions();
        },this);
        this.roleCmb.on('select',this.loadPermissions,this);
        Wtf.common.Permissions.superclass.onRender.call(this,config);
        this.featureStore.load();
    },

    loadActivities:function(){
        this.ActRecord=new Wtf.data.Record.create(
            ['featureid','activityid','activityname','displayactivityname']
        );
        this.ActStore = new Wtf.data.Store({
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/Permission/getActivityList.common",
            baseParams:{mode:2},
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            },this.ActRecord)
        });
        this.ActStore.on('load',this.createWindow,this);
        this.ActStore.load();
    },

    openNewRoleWindow:function(){
        this.roleNameField = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.RoleName")+ ' *',
            anchor : '99%',
            allowBlank:false,
            maxLength:50
        });
        this.groupForm = new Wtf.FormPanel({
                labelWidth: 100,
                labelAlign : 'left',
                border:false,
                bodyStyle:'padding:5px 5px 0',
                layout : 'form',
                anchor : '100%',
                defaultType: 'textfield',
                buttonAlign :'right',
                items: [this.roleNameField]
            });
        this.win=new Wtf.Window({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            title:WtfGlobal.getLocaleText("hrms.common.Role"),
            height:100,
            width:300,
            id: 'roleWin',
            modal:true,
            resizable:false,
            scope:this,
            items:[this.groupForm],
            buttonAlign :'right',
            buttons: [
            {
                text: WtfGlobal.getLocaleText("hrms.common.Save"),
                scope:this,
                handler:function(){
                    if(!this.roleNameField.isValid()){
                        return;
                    }
                    Wtf.Ajax.requestEx({
                        url: Wtf.req.base+'UserManager.jsp',
                        params: {
                            mode:9,
                            rolename:this.roleNameField.getValue()
                        }
                    },this,this.genSuccessResponse,this.genFailureResponse);
                }
            },
            {
                text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                handler: function(){
                    Wtf.getCmp("roleWin").close();
                }
            }
            ]
        });
        this.win.show();
    },
    loadPermissions:function(c){
        if(c!=null&&c.getValue()==1234){
            c.setValue(this.roleStore.getAt(0).get('roleid'));
            this.openNewRoleWindow();
            return;
        }
        this.PerStore.load({
            params:{
                roleid:this.roleCmb.getValue()
            }
        });
    },

    createForm:function(){
        this.AssPerForm= new Wtf.FormPanel({
            region:'center',
            cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
            bodyStyle: "background: transparent;",
            border:false,          
            autoScroll:true,
            bodyBorder:false,
            style: "background: transparent;",
            width:'100%',
            height:'100%',
            id:'AssPerForm',
            items:[{
                name:'userid',
                xtype:'hidden'
            },{
                layout:'column',
                style:'padding:20px',
                border:false,
                items:[{
                    columnWidth:.73,
                    layout:'form',
                    border:false,
                    labelWidth:50,
                    items:this.roleCmb
                },{
                    columnWidth:.23,
                    layout:'form',
                    border:false,
                    labelWidth:50,
                    items:{
                        xtype:'button',
                        text:WtfGlobal.getLocaleText("hrms.common.DeleteRole"),
                        scope:this,
                        handler:this.deleteRole
                    }
                }]
            },{
                layout:'column',
                border:false,
                items:[{
                        columnWidth:.48,
                        layout:'form',
                        id:this.id+"-col0",
                        border:false
                },{
                    columnWidth:.48,
                    layout:'form',
                    id:this.id+"-col1",
                    border:false
                }]
            }]
        });
        for(var i=0;i<this.featureStore.getCount();i++)
            this.createFeatureSet(i,i%2);
    },

    createWindow:function(){
        this.createForm();
        this.MainWinPanel= new Wtf.Panel({
            border:false,
            height:430,
            layout:'border',
            items:[{
              region:'north',
              border:false,
              height:80,
              cls : 'panelstyleClass1',
              html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.SetUsersPermissions"),WtfGlobal.getLocaleText("hrms.common.Setpermissionfortheusers"), "../../images/user-permission.gif"),
              layout:'fit'
             },{
              region:'center',
              border:false,
              layout:'fit',
              cls : 'formstyleClass2',
              items:[this.AssPerForm]
             }]
        });
        this.add(this.MainWinPanel);
        this.doLayout();
        this.MainWinPanel.doLayout();
        this.roleStore.load();
    },

    createFeatureSet:function(index,col){
        var rec=this.featureStore.getAt(index);
        var feature=new Wtf.form.FieldSet({
            id:'feature'+rec.get('featureid'),
            xtype:"fieldset",
            style:'margin:10px',
            collapsible:true,
            collapsed:true,
            autoHeight:true,
            title:rec.get('displayfeaturename')
        });
        this.ActStore.filter('featureid',rec.get('featureid'));
        for(var i=0;i<this.ActStore.getCount();i++)
            this.createActivity(i,feature);
        this.ActStore.clearFilter();
        Wtf.getCmp(this.id+"-col"+col).add(feature);
    },

    createActivity:function(index,feature){
        var rec=this.ActStore.getAt(index);
        var activity=new Wtf.form.Checkbox({
            fieldLabel: rec.get('displayactivityname'),
            name: "act"+rec.get('activityid'),
            id:"activity"+rec.get('activityid')          
        });
        feature.add(activity);
    },

    isChecked:function(actRec,index){
        var permRec=this.PerStore.getAt(this.PerStore.find('featureid',actRec.get('featureid')));
        if(permRec==null)return false;
        var permCode=permRec.get('permission');
        while(index>0){
            permCode=Math.floor(permCode/2);
            index--;
        }
        return permCode%2==1;
    },

    checkActivities:function(){
        var comp;
        var featureids=this.ActStore.collect("featureid");
        var userRole=this.roleCmb.getValue();
        this.applyBtn.setDisabled(userRole=="1"||userRole=="2"||userRole=="3");//comment for commit
        for(var i=0;i<featureids.length;i++){
            this.ActStore.filter('featureid',featureids[i]);
            for(var x=0;x<this.ActStore.getCount();x++){
                var actRec=this.ActStore.getAt(x);
                comp=Wtf.getCmp("activity"+actRec.get('activityid'));
                comp.setValue(this.isChecked(actRec,x));
                comp.setDisabled(userRole=="1"||userRole=="2"||userRole=="3");//comment for commit
            }
            this.ActStore.clearFilter();
        }
    },

    ApplyPermission:function(){
        var permCode=[];
        var features=[];
        var formVal=this.AssPerForm.getForm().getValues();
        for(var i=0;i<this.featureStore.getCount();i++){
            var feature=this.featureStore.getAt(i).get('featureid');
            features.push(feature);
            permCode.push(this.getNewFeatureValue(feature,formVal));
        }
        calMsgBoxShow(200,4,true);
        Wtf.Ajax.requestEx({
//            url: Wtf.req.base+'UserManager.jsp',
            url:"Common/Permission/setPermissions.common",
            params: {
                mode:15,
                roleid:this.roleCmb.getValue(),
                features:features,
                permissions:permCode
            }
        },this,this.genSuccessResponse,this.genFailureResponse);

    },

    getNewFeatureValue:function(featureid,formVal){
        var code=0;
        var tmp=1;
        this.ActStore.filter('featureid',featureid);
        for(var i=0;i<this.ActStore.getCount();i++){
            var id=this.ActStore.getAt(i).get('activityid');
            if(eval('('+'formVal.act'+id+')'))code=code+tmp;
            tmp=2*tmp;
        }
        this.ActStore.clearFilter();
        return code;
    },
    cancel:function(){
        this.close();
    },

    genSuccessResponse:function(response){
       // Wtf.Msg.alert('User Permissions',response.msg);
       if(response.success){
    	   msgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), response.msg],1);
       }else{
    	   msgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), response.msg],0);
       }
        if(response.success==true)
            this.roleStore.reload();
        if(Wtf.getCmp("roleWin"))
            Wtf.getCmp("roleWin").close();
//        this.close();
//        this.destroy();
    },

    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.UserPermissions"),msg);
        if(Wtf.getCmp("roleWin"))
            Wtf.getCmp("roleWin").close();
    },

    deleteRole:function(){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), deleteMsgBox('role'),function(btn){
            if(btn!="yes") return;
            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp',
                params: {
                    mode:10,
                    roleid:this.roleCmb.getValue()
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
    }
});

