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

Wtf.importMenuArray = function(obj,moduleName,store,contactmapid,targetlistPagingLimit) {
    var archArray = [];
    var importButton = new Wtf.Action({
        text: WtfGlobal.getLocaleText("hrms.Importlog.ImportCSVFile"),
        id:'importcsvfile'+obj.id,
        tooltip:{
            text:WtfGlobal.getLocaleText("hrms.Importlog.ClicktoimportCSVfile")
        },
        iconCls: "pwnd importTabIcon",
        scope: obj,
        handler:function(){
            if(moduleName=="Lead"){
                obj.ImportLeads('csv');
            } else if(moduleName=="Contact"){
                obj.ImportContacts('csv');
            } else if(moduleName=="Target"){
                obj.ImportTargets('csv');
            } else if(moduleName=="Target List"){
                obj.importCSVfile('TargetList');
            } else if(moduleName=="Account"){
                obj.ImportAccounts('csv');
            }
        }
    });
    archArray.push(importButton);

    var importXLS=new Wtf.Action({
        text: WtfGlobal.getLocaleText("hrms.Importlog.ImportXLSFile"),
        tooltip:{
            text:WtfGlobal.getLocaleText("hrms.Importlog.ClicktoimportXLSfile")
        },
        iconCls: "pwnd importTabIcon",
        scope: obj,
        handler:function(){
                this.form=new Wtf.form.FormPanel({
               // frame:true,
                url:'fileUploadXLS',
                fileUpload:true,
                autoHeight:true,
                border:false,
                bodyStyle:'background:#f1f1f1;font-size:10px;padding:11px 15px 0',
                autoWidth:true,
                defaultType: 'textfield',
                items:[{
                        fieldLabel:WtfGlobal.getLocaleText("hrms.common.file.to.upload"),
                        inputType:'file',
                        id:'browsexlsBttn'
                }]

            });
            this.win1=new Wtf.Window({
                resizable: false,
                scope: this,
                layout: 'border',
                modal:true,
                width: 420,
                height: 185,
                iconCls: 'pwnd favwinIcon',
                id: 'importwindow',
                title: WtfGlobal.getLocaleText("hrms.Importlog.ImportXLSFile"),
                items: [
                {
                    region:'north',
                    height:70,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html: getTopHtml(WtfGlobal.getLocaleText("hrms.Importlog.ImportXLSFile"), WtfGlobal.getLocaleText("hrms.Importlog.ImportXLSFile"), "../../images/import.png")
                },{
                    region:'center',
                    layout:'fit',
                    border:false,
                    bodyStyle: 'background:#f1f1f1;font-size:10px;',
                    items:[this.form]
                }
                ],
                buttons:[{
                    text:WtfGlobal.getLocaleText("hrms.common.Upload"),
                    handler:function(){
                        // this.disable();
                        var parsedObject = document.getElementById('browsexlsBttn').value;
                        var extension =parsedObject.substr(parsedObject.lastIndexOf(".")+1);
                        var patt1 = new RegExp("xls","i");
                        if(patt1.test(extension)) {
                            this.form.getForm().submit({
                                waitMsg:WtfGlobal.getLocaleText("hrms.Importlog.UploadingFile"),
                                scope:this,
                                success:function(f,a){
                                    this.win1.close();
                                    genUploadResponse(a.request,true,a.response,moduleName,store,contactmapid,targetlistPagingLimit,obj)
                                },
                                failure:function(f,a){
                                    this.win1.close();
                                    genUploadResponse(a.request,false,a.response,moduleName,store,contactmapid,targetlistPagingLimit,obj)
                                }
                            });
                        } else {
                            ResponseAlert(83);
                        }

                    },
                    scope:this
                }]
            });this.win1.show();
            }
    });
    archArray.push(importXLS);
    if(moduleName!="Target List" && moduleName!= "Account" ) {

        var importGoogleContacts=new Wtf.Action({
            text: WtfGlobal.getLocaleText({key:"hrms.common.import.from.google.account",params:[moduleName]}),
            tooltip:{
                text:WtfGlobal.getLocaleText({key:"hrms.common.click.import.from.google.account",params:[moduleName]})
            },
            iconCls: "pwnd importTabIcon",
            scope: obj,
            handler:function(){
                Wtf.commonAuthenticationWindow(obj,store,moduleName,contactmapid);
            }
        });
        archArray.push(importGoogleContacts);
    }
    return archArray;

    function genUploadResponse(req,succeed,res,moduleName,store,contactmapid,targetlistPagingLimit,obj){
      //  this.win1.destroy();
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        var response=eval('('+res.responseText+')');
        if(succeed){
            succeed=response.lsuccess;
            if(succeed){
                this.win=new Wtf.SheetViewer1({
                    title: 'Available Sheets',
                    iconCls: 'pwnd favwinIcon',
                    closable:true,
                    autoScroll:true,
                    width:600,
                    height:600,
                    plain:true,
                    modal:true,
                    id:'importxls',
                    data:response,
                    layout:'border',
                    moduleName:moduleName,
                    store:store,
                    contactmapid:contactmapid,
                    targetlistPagingLimit:targetlistPagingLimit,
                    obj:obj

                });
                this.win.show();
            }else{
                msg=response.msg;
                Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.masterconf.FileUpload"), msg);
            }
        }
    }
}

Wtf.importMenuButtonA = function(menuArray,obj,modName) {
    var tbarArchive=new Wtf.Toolbar.Button({
        iconCls: "pwnd importicon",
        tooltip: {
            text: WtfGlobal.getLocaleText({key:"hrms.common.click.to.import",params:[modName]})
        },
        scope: obj,
        text:WtfGlobal.getLocaleText("hrms.Featurelist.importf"),
        menu: menuArray
    });
    return tbarArchive;
}

Wtf.commonAuthenticationWindow =  function(obj,store,moduleName,mapid) {

    if(moduleName=="Lead") {
        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.email.id"),
                id:'authentication_username'+obj.id,
                width:200,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                msgTarget:'side',
                vtype:"email",
                xtype:'striptextfield'
            },
            password=new Wtf.form.TextField({
                fieldLabel: WtfGlobal.getLocaleText("hrms.AuditGroup.Password"),
                id:'authentication_password'+obj.id,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                msgTarget:'side',
                inputType:"password",
                width:200
            }),{
                fieldLabel: WtfGlobal.getLocaleText("hrms.common.Company"),
                id:'authentication_company'+obj.id,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                msgTarget:'side',
                maxLength:250,
                width:200,
                xtype:'striptextfield'
            }]
        });
    }else if(moduleName=="Contact") {

        this.accStore = new Wtf.data.Store({
            url: Wtf.req.base + 'crm.jsp?flag=5',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.ComboReader),
            autoLoad:true
        });

        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.email.id"),
                id:'authentication_username'+obj.id,
                width:200,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                msgTarget:'side',
                vtype:"email",
                xtype:'striptextfield'
            },
            password=new Wtf.form.TextField({
                fieldLabel: WtfGlobal.getLocaleText("hrms.AuditGroup.Password"),
                id:'authentication_password'+obj.id,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                msgTarget:'side',
                inputType:"password",
                width:200
            }),this.accCombo=new Wtf.form.ComboBox({
                fieldLabel: WtfGlobal.getLocaleText("hrms.Importlog.Account"),
                id:'authentication_account'+obj.id,
                selectOnFocus:true,
                triggerAction: 'all',
                mode: 'local',
                store: this.accStore,
                displayField: 'name',
                emptyText:"-- "+WtfGlobal.getLocaleText("hrms.common.please.select")+" --",
                typeAhead: true,
                allowBlank:false,
                valueField:'id',
                msgTarget:'side',
                anchor:'100%',
                width:200

            })]
        });
    } else {
        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("hrms.common.email.id"),
                id:'authentication_username'+obj.id,
                width:200,
                allowBlank:false,
                msgTarget:'side',
                vtype:"email",
                xtype:'striptextfield'
            },
            password=new Wtf.form.TextField({
                fieldLabel: WtfGlobal.getLocaleText("hrms.AuditGroup.Password"),
                id:'authentication_password'+obj.id,
                allowBlank:false,
                validator:WtfGlobal.noBlankCheck,
                msgTarget:'side',
                inputType:"password",
                width:200
            })]
        });
    }

    this.selectAllAuthentication= new Wtf.form.Checkbox({
        boxLabel:WtfGlobal.getLocaleText("hrms.common.import.all"),
        inputType:'radio',
        name:'rectype',
        checked:true,
        inputValue:'false',
        id:"selectAllAuthentication"+obj.id,
        width: 100
    })
    this.selectSomeAuthentication= new Wtf.form.Checkbox({
        boxLabel:WtfGlobal.getLocaleText("hrms.common.select.some"),
        width: 100,
        inputType:'radio',
        id:"selectSomeAuthentication"+obj.id,
        inputValue:'true',
        name:'rectype'
    })
    this.TypeFormAuthentication=new Wtf.form.FormPanel({
        autoScroll:true,
        border:false,
        labelWidth:100,
        style: "background: #f1f1f1;padding-left: 35px;padding-top: 0px;padding-right: 30px;",
        layout:'column',
        items:[{border:false,columnWidth:.5,items:this.selectAllAuthentication},{border:false,columnWidth:.5,items:this.selectSomeAuthentication}]
    })

    this.authwin=new Wtf.Window({
        height:moduleName=="Target"?260:290,
        width:400,
        id:'authentication_window'+obj.id,
        iconCls: "pwnd favwinIcon",
        title:"Authentication",
        modal:true,
        shadow:true,
        scope:this,
        resizable:false,
        buttonAlign:'right',
        buttons: [{
            text: WtfGlobal.getLocaleText("hrms.common.submit"),
            scope:this,
            handler:function() {
                var username=Wtf.getCmp('authentication_username'+obj.id).getValue();
                var password=Wtf.getCmp('authentication_password'+obj.id).getValue();
                var selectAllAuthentication=Wtf.getCmp("selectAllAuthentication"+obj.id).getValue();

                var company="";
                var account="";
                if(username==""){
                        WtfComMsgBox(1054,0);
                        return;
                }if(password==""){
                        WtfComMsgBox(1055,0);
                        return;
                }
                if(moduleName=="Lead"){
                    company=Wtf.getCmp('authentication_company'+obj.id).getValue();
                    if(company==""){
                        WtfComMsgBox(1052,0);
                        return;
                    }
                }else if(moduleName=="Contact"){
                    account=Wtf.getCmp('authentication_account'+obj.id).getValue();
                    if(account==""){
                        WtfComMsgBox(1053,0);
                        return;
                    }
                }

                var jsondata = "";
                Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.fetching.google.contacts"));
                jsondata+="{'username':'" + username + "',";
                jsondata+="'password':'"+password+"'},";

                var trmLen = jsondata.length - 1;
                var finalStr = jsondata.substr(0,trmLen);

                Wtf.Ajax.requestEx({
                    url:Wtf.req.base +'crm.jsp',
                    params:{
                        jsondata:finalStr,
                        importAll:selectAllAuthentication,
                        account : account,
                        company : company,
                        moduleName:moduleName,
                        flag:800,
                        mapid:mapid
                    }
                },this,
                function(res) {
                    Wtf.updateProgress();
                    if(res.importAll){ // when user select to import all Google contacts
                        this.authwin.close();
                        store.load();
                        ResponseAlert(353);
                    } else {
                        if(res.data!=undefined) {
                            this.authwin.close();

                            var storeData = res.data;
                            var recContact = Wtf.data.Record.create([
                                      {name: 'firstName', mapping: 0},
                                      {name: 'lastName', mapping: 1},
                                       {name: 'email', mapping: 2}
                            ]);

                            this.listds = new Wtf.ux.data.JsonPagingStore({
                                lastOptions: {params:{start: 0,limit: 20}},
                                totalRecords : 'records',
                                successProperty: WtfGlobal.getLocaleText("hrms.common.success"),
                                reader:new Wtf.data.ArrayReader({},recContact),
                                fields: [{
                                    name:"firstName"
                                },{
                                    name:"lastName"
                                },{
                                    name:"email"
                                }]
                            });

                            this.listds.loadData(res.data);
                            this.listds.load({params:{start:0,limit:20}});
                            var sm = new Wtf.grid.CheckboxSelectionModel({
                                width:25
                            });

                            this.googleCM = new Wtf.grid.ColumnModel([
                                new Wtf.grid.CheckboxSelectionModel,
                                {
                                    header: WtfGlobal.getLocaleText("hrms.common.FirstName"),
                                    width:50,
                                    dataIndex: 'firstName'
                                },{
                                    header: WtfGlobal.getLocaleText("hrms.common.LastName"),
                                    width:50,
                                    dataIndex: 'lastName'
                                },{
                                    header: WtfGlobal.getLocaleText("hrms.common.email.id"),
                                    dataIndex: 'email'
                                }]);

                            this.contactGrid= new Wtf.grid.GridPanel({
                                id:"google_contact_grid"+obj.id,
                                store:this.listds,
                                scope:this,
                                cm:this.googleCM,
                                sm : sm,
                                border : false,
                                viewConfig: {
                                    forceFit:true
                                },
                                bbar: new Wtf.PagingToolbar({
                                  pageSize: 20,
                                  store: this.listds,
                                  displayInfo: true
//                                  displayMsg: 'Displaying messages {0} - {1} of {2}',
//                                  emptyMsg: "No messages to display"
                                })

                            });

                            this.gContactWin = new Wtf.Window({
                                resizable: false,
                                scope: this,
                                layout: 'border',
                                modal:true,
                                width: 600,
                                height:500,
                                iconCls: 'pwnd favwinIcon',
                                id: 'import_window_gContacts'+obj.id,
                                title: WtfGlobal.getLocaleText("hrms.common.google.contacts"),
                                items:[{
                                    region : 'north',
                                    height : 70,
                                    border : false,
                                    id:'googleContacts_North_panel'+obj.id,
                                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                                    html : getTopHtml(WtfGlobal.getLocaleText("hrms.common.google.contacts"), WtfGlobal.getLocaleText({key:"hrms.common.google.contact.list",params:[username]}),'../../images/leads.gif')
                                },{
                                    region:'center',
                                    layout:'fit',
                                    items:[this.contactGrid]
                                }],
                                buttons: [
                                {
                                    text: WtfGlobal.getLocaleText("hrms.Importlog.Import"),
                                    id: 'importBttn_googleContact'+obj.id,
                                    type: 'submit',
                                    scope: this,
                                    handler: function(){
                                        var s=this.contactGrid.getSelectionModel().getSelections();
                                        if(s.length>0){
                                            Wtf.saveGoogleContact(s,store,this.gContactWin,moduleName,company,account,username,this.listds,mapid);
                                        } else {
                                            WtfComMsgBox(1051,0);
                                            return;
                                        }

                                    }
                                },{
                                    text: WtfGlobal.getLocaleText("hrms.common.cancel"),
                                    id:'canbttn_googleContact'+obj.id,
                                    scope:this,
                                    handler:function() {
                                        this.gContactWin.close();
                                    }
                                }]
                            });

                            this.gContactWin.show();
                        } else {
                            WtfComMsgBox(1050,0);
                            Wtf.getCmp('authentication_username'+obj.id).setValue("");
                            Wtf.getCmp('authentication_password'+obj.id).setValue("");
                        }
                    }
                },
                function(res){
                    Wtf.updateProgress();
                    ResponseAlert(352);
                })

            }
        },{
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope:this,
            handler:function(){
                this.authwin.close()
            }
        }],
        layout : 'border',
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(WtfGlobal.getLocaleText("hrms.common.authentication"), WtfGlobal.getLocaleText("hrms.common.please.provide.google.account.details"), "../../images/import.png")
        },{
            region : 'center',
            border : false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            layout : 'fit',
            items :[this.form1]
        },{
            region : 'south',
            height : 40,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            layout : 'fit',
            items :this.TypeFormAuthentication
        }]
    });

    this.authwin.show();

    this.selectSomeAuthentication.on("check",function(){
        this.selectAllAuthentication=false;
    },this)

    this.selectAllAuthentication.on("check",function(){
        this.selectSomeAuthentication=false;
    },this)
}

Wtf.saveGoogleContact =  function(s,store,gcontactWin,moduleName,company,account,username,gcontactStore,mapid){
    var googleContactJson=""
    for(var i = 0 ; i < s.length ; i++){
        googleContactJson+="{'firstName':'" + s[i].data.firstName + "',";
        googleContactJson+="'lastName':'" + s[i].data.lastName + "',";
        googleContactJson+="'email':'" + s[i].data.email + "'},";
    }
    for(var j=0 ;j < s.length ;j++){
        gcontactStore.remove(s[j]);
    }

    gcontactStore.load({params:{start:0,limit:20}});
    var finalJson = googleContactJson.substring(0, (googleContactJson.length -1))
    Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.importing.google.contacts"));
    Wtf.Ajax.requestEx({
        url: Wtf.req.base + 'crm.jsp',
        params:{
            jsondata:finalJson,
            flag:801,
            moduleName:moduleName,
            company:company,
            account:account,
            username:username,
            mapid:mapid
        }
    },
    this,
    function(res)
        {
            Wtf.updateProgress();
            if(res.success){
                ResponseAlert(351);
            } else {
               ResponseAlert(352);
            }
            Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("hrms.common.google.contacts"),
                msg: WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.nRecImportedSuccess",params:[s.length]}),
                buttons: Wtf.MessageBox.YESNO,
                animEl: 'mb9',
                scope:this,
                icon: Wtf.MessageBox.INFO,
                fn:function(btn,text){
                    if(btn=="yes"){
                        store.reload();
                    } else {
                        gcontactWin.close();
                        store.reload();
                    }
                }
            });
        },
    function()
    {
        Wtf.updateProgress();
        ResponseAlert(352);

    });

}

/// import XLS

Wtf.SheetViewer1=function(config){
    Wtf.SheetViewer1.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.SheetViewer1,Wtf.Window,{
    initComponent:function(config){
        Wtf.SheetViewer1.superclass.initComponent.call(this,config);
        this.addButton({text:WtfGlobal.getLocaleText("hrms.common.Next"),disabled:true,id:"nextButton"+this.id}, this.getCSVMappingInterface,this);
        this.addButton( WtfGlobal.getLocaleText("hrms.common.cancel"), function(){this.close();},this);
    },

    onRender:function(config){
        Wtf.SheetViewer1.superclass.onRender.call(this,config);
        this.xlsfilename=this.data.file;
        for(var x=0;x<this.data.data.length;x++){
            this.data.data[x].srow='1';
        }
        var rec=new Wtf.data.Record.create([
            {name:'name'},{name:'index'},{name:'srow'}
        ])
        var store=new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root: "data"
            },rec),
            data:this.data
        });
        this.shgrid=new Wtf.grid.GridPanel({
            viewConfig:{
                forceFit:true
            },
            columns:[{
                header:WtfGlobal.getLocaleText("hrms.Importlog.SheetName"),
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("hrms.Importlog.StartingRow"),
                dataIndex:'srow'
            }],
            store:store
        });


        this.shgrid.on('rowclick',this.showDetail,this);

        var shdrec=new Wtf.data.Record.create([
            {name:'name'}
        ])
        var shdstore=new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root: "fields"
            },shdrec)
        });
        this.shdgrid=new Wtf.grid.GridPanel({
            columns:[],
            store:shdstore
        });
        this.shdgrid.on('rowclick',this.updateStartRow,this);
        this.add({
            region:'north',
            height:80,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(WtfGlobal.getLocaleText("hrms.Importlog.AvailableSheets"), WtfGlobal.getLocaleText("hrms.Importlog.step1.sheet.see.sample")+"<br/><br/>"+WtfGlobal.getLocaleText("hrms.Importlog.step2.next.import.selected.sheet"), "../../images/import.png")
        });
        this.add({
            region:'center',
            layout:'fit',
            autoScroll:true,
            items:this.shgrid
        });
        this.add({
            region:'south',
            height:220,
            layout:'fit',
            autoScroll:true,
            items:this.shdgrid

        });
    },

    updateStartRow:function(g,i,e){
        var rec = this.shgrid.getSelectionModel().getSelected();
        rec.set('srow',i+1);
    },

    genUploadResponse12:function(req,succeed,res){
        var msg=WtfGlobal.getLocaleText("hrms.common.FailedconnectionServer");
        var response=eval('('+res.responseText+')');
        if(succeed){
            msg=response.msg;
            succeed=response.lsuccess;
            this.Header= response.Header;
            this.xlsParserResponse = response;
            if(succeed){
                this.cursheet=response.index;
                var cm=this.createColumnModel1(response.maxcol);
                var store=this.createStore1(response,cm);
                this.shdgrid.reconfigure(store,cm);
                var rowno=this.shgrid.getStore().getAt(this.shgrid.getStore().find('index',this.cursheet)).get('srow');
                if(rowno)
                    this.shdgrid.getSelectionModel().selectRow(rowno-1);
            }else{
                Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.Importlog.FileImport"),msg);
            }
        }
        this.shdgrid.enable();
    },

    createColumnModel1:function(cols){
        var fields=[new Wtf.grid.RowNumberer()];
        for(var i=1;i<=cols;i++){
            var temp=i;
            var colHeader="";
            while(temp>0){
                temp--;
                colHeader=String.fromCharCode(Math.floor(temp%26)+"A".charCodeAt(0))+colHeader;
                temp=Math.floor(temp/26);
            }
            fields.push({header:colHeader,dataIndex:colHeader});
        }
        return new Wtf.grid.ColumnModel(fields);
    },

    createStore1:function(obj,cm){
        var fields=[];
        for(var x=0;x<cm.getColumnCount();x++){
            fields.push({name:cm.getDataIndex(x)});
        }

        var rec=new Wtf.data.Record.create(fields);
        var store = new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root: "data"
            },rec),
            data:obj
        });

        return store;
    },


    showDetail:function(g,i,e){
        Wtf.getCmp("nextButton"+this.id).enable();
        var rec=this.shgrid.getStore().getAt(i);
        if(this.cursheet&&this.cursheet==rec.get('index'))return;
        this.shdgrid.disable();
        Wtf.Ajax.request({
            method: 'GET',
            url: 'XLSDataExtractor',
            params:{
                filename:this.xlsfilename,
                index:rec.get('index')
            },
            scope: this,
            success: function(res, req){
                this.genUploadResponse12(req, true, res);
            },
            failure: function(res, req){
                this.genUploadResponse12(req, false, res);
            }
        });
    },

    getCSVMappingInterface:function(g,i,e){

       var rec=this.shgrid.getSelectionModel().getSelected();
       Wtf.getCmp("importxls").hide();
       this.mapCSV=new Wtf.csvMappingInterface({
            csvheaders:this.Header,
            modName:this.moduleName+"s",
            typeXLSFile:true,
            impWin1:Wtf.getCmp("importxls"),
            delimiterType:"",
            index:rec.get('index'),
            moduleName:this.moduleName,
            store:this.store,
            contactmapid:this.contactmapid,
            targetlistPagingLimit:this.targetlistPagingLimit,
            scopeobj:this.obj,
            cm:this.obj.EditorColumnArray
        }).show();

        Wtf.getCmp("csvMappingInterface").on('importfn',this.importCSVfunc, this);
    },

    importCSVfunc:function(response,index,moduleName,store,contactmapid,targetlistPagingLimit,scopeobj)
    {
           Wtf.saveXLS(this,response,index,moduleName,store,contactmapid,targetlistPagingLimit,scopeobj)
           this.on('importTargetListRecs',this.insertIntoGrid, this);
    },

    insertIntoGrid:function(insertRecs,store,targetlistPagingLimit,scopeobj){

          if(insertRecs[0].TLID !== undefined)
                scopeobj.TLID = insertRecs[0].TLID;
          store.baseParams.listID = scopeobj.TLID;
          store.baseParams.start = 0;
          store.baseParams.limit = targetlistPagingLimit;
          store.load();

    }

});


Wtf.saveXLS = function(obj,response,sheetindex,moduleName,store,contactmapid,targetlistPagingLimit,scopeobj)
{

    var sustr1 = moduleName =='Target List'? ('&tlid='+scopeobj.TLID):"";
    var name = moduleName =='Target List'? WtfGlobal.getLocaleText("hrms.common.Target"):moduleName;

    Wtf.Ajax.timeout=900000;
    Wtf.Ajax.requestEx({
        url:'../../exportimportcontacts.jsp?type=submit&do=xlsImport&resjson='+response+'&mapid='+contactmapid+sustr1,
        waitMsg :WtfGlobal.getLocaleText("hrms.Importlog.importing"),
        scope:this,
        params: ({
            filepath:obj.xlsfilename,
            sheetindex:sheetindex,
            moduleName:moduleName

        })
    },
    this,
    function (action,res) {

            Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("hrms.common.success"),
                msg: WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.imported.successfully", params:[name]}),
                buttons: Wtf.MessageBox.OK,
                animEl: 'mb9',
                icon: Wtf.MessageBox.INFO
            });

            if(action.data.importedRecords.data!=undefined){  // for Target List
                obj.addEvents({
                    'importTargetListRecs':true
                });
               obj.fireEvent('importTargetListRecs',action.data.importedRecords.data,store,targetlistPagingLimit,scopeobj);
            }
            store.load();
            Wtf.Ajax.timeout=30000;
    },
    function ( action,res) {
            Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("hrms.common.error"),
                msg: WtfGlobal.getLocaleText({key:"hrms.CampaignDetail.couldnot.imported.please.try.again",params:[name]}),
                buttons: Wtf.MessageBox.OK,
                animEl: 'mb9',
                icon: Wtf.MessageBox.ERROR
            });
            Wtf.Ajax.timeout=30000;
    });
};
