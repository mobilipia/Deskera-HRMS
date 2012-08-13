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

Wtf.ApproveReportsWin = function (config){
    Wtf.apply(this,config);
    this.save = true;
    Wtf.ApproveReportsWin.superclass.constructor.call(this,{
        buttons:[
                {
                    text:WtfGlobal.getLocaleText("hrms.common.Save"),
                    id:'btnsave',
                    handler:function (){
                        this.saveAuthorizeStatus();
                    },
                    scope:this
                },
                {
                    text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }
            ]
    });
}

Wtf.extend(Wtf.ApproveReportsWin,Wtf.Window,{
    initComponent:function (){
        Wtf.ApproveReportsWin.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetCenterPanel();
        this.GetSouthPanel();

        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[
                this.northPanel,
                this.reportGrid,
                this.AddEditForm
            ]
        });

        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){

        this.northPanel = new Wtf.Panel({
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(WtfGlobal.getLocaleText("hrms.common.authorize.statutory.information"), WtfGlobal.getLocaleText("hrms.common.authorize.statutory.information.selected.employee"),  "../../images/assign-manager.gif")
        });
    },
    GetCenterPanel:function (){

          var myData = [
                ["1",Wtf.Malaysian_StatutoryForm_AmanahSahamNasional],
                ["2",Wtf.Malaysian_StatutoryForm_CP39],
                ["3",Wtf.Malaysian_StatutoryForm_CP39A],
                ["4",Wtf.Malaysian_StatutoryForm_CP21],
                ["5",Wtf.Malaysian_StatutoryForm_HRDLevy],
                ["6",Wtf.Malaysian_StatutoryForm_PCB2],
                ["7",Wtf.Malaysian_StatutoryForm_TabungHaji],
                ["8",Wtf.Malaysian_StatutoryForm_TP1],
                ["9",Wtf.Malaysian_StatutoryForm_TP2],
                ["10",Wtf.Malaysian_StatutoryForm_TP3],
                ["11",Wtf.Malaysian_StatutoryForm_EA]
            ];

        var store = new Wtf.data.SimpleStore({
            fields: [

               {name: 'id'},
               {name: 'name'}
            ]
        });
        store.loadData(myData);

        this.sm2 = new Wtf.grid.CheckboxSelectionModel();


          this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.statutory.form"),
                dataIndex: 'name'
            }
         ]);
         this.reportGrid = new Wtf.grid.GridPanel({
            region:'center',
            id:this.id+'authorize_statutory_form_win',
            store: store,
            cm: this.cm,
            loadMask:true,
            displayInfo:true,
            sm: this.sm2,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            autoScroll:true,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            viewConfig: {
                forceFit: true
            }

        });

    },
    GetSouthPanel:function (){

         var myData = [
                ["0", WtfGlobal.getLocaleText("hrms.recruitment.pending")],
                ["1", WtfGlobal.getLocaleText("hrms.common.authorize")],
                ["2", WtfGlobal.getLocaleText("hrms.common.unauthorize")]
            ];

        var store = new Wtf.data.SimpleStore({
            fields: [

               {name: 'id'},
               {name: 'name'}
            ]
        });
        store.loadData(myData);


         this.actionStoreCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.action"),
            hiddenName: 'action',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:store,
            width:150,
            typeAhead:true,
            value:0
        });

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"south",
            height:55,
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:35px 55px 45px 145px",
            items:[
                this.actionStoreCmb
            ]
        });
    },
    saveAuthorizeStatus:function (){
        var empids=[];
        var formsSM = this.reportGrid.getSelectionModel();

        if(formsSM.getCount()<1){
           calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.select.form.perform.action")],0);
           return;
        }
        var forms = [];
        var formarr= formsSM.getSelections();

        for(var i=0; i<formarr.length;i++){
            forms.push(formarr[i].get('id'));
        }

        for(var i=0; i<this.emparr.length;i++){
            empids.push(this.emparr[i].get('userid'));
        }

        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.common.save.data.selected.employee"), function(btn){
            if(btn!="yes") return;
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.Savingdata"));

            Wtf.Ajax.requestEx({

            url: "Payroll/MalaysianStatutoryForm/authorizeStatutoryFormsData.py",
            params:{
                empids : empids,
                formarr:forms,
                month:this.month,
                year:this.year,
                action:this.actionStoreCmb.getValue()
            }},this,
            function(res,action){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow133")],0,0);
                this.close();
                this.grid.getStore().reload();
            },
            function() {
                calMsgBoxShow(27,1);
            }
        );

        },this);




    }
});

Wtf.authorizeStatusWin=function(config){
    Wtf.apply(this,config);
    Wtf.authorizeStatusWin.superclass.constructor.call(this,{
        buttonAlign : 'right',
        buttons :[{text : WtfGlobal.getLocaleText("hrms.common.Close"),
            scope : this,
            handler : function(){
                this.close();
            }
        }]
    });
    
}

Wtf.extend(Wtf.authorizeStatusWin, Wtf.Window, {
    group_id:"",
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
        Wtf.authorizeStatusWin.superclass.onRender.call(this,config);
        this.availablesm = new Wtf.grid.CheckboxSelectionModel({singleSelect:true});
        this.availablecm =  new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.availablesm,
            {
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname'
            }
         ]);
 
        this.availablegrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store:this.empGDS,
            cm: this.availablecm,
            border:false,
            id:this.id+'compavailablegrid',
            sm : this.availablesm,
            autoScroll:true,
            searchField:"code",
            serverSideSearch:true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.search.code"),
            viewConfig: {
                forceFit: true,
                autoFill:true
            }
        });

        this.selectedRec = new Wtf.data.Record.create([
        {
            "name":"formName"
        },
        {
            "name":"status"
        }
        ]);

        var URL= "Payroll/MalaysianStatutoryForm/getStatusForStatutoryForms.py";
        var readerJson=new Wtf.data.KwlJsonReader1({
            root:'data',
            totalProperty: 'count'
        },this.selectedRec);

        
        this.selectedds = new Wtf.data.Store({
            url: URL,
            baseParams: {
                month:this.month,
                year:this.year
            },
            reader: readerJson,
            autoLoad : false
        });
 
        this.selectedcm = new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer(),
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.statutory.form"),
            dataIndex: 'formName',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.status"),
            dataIndex: 'status',
            renderer:function(val){
                return Wtf.getStatutoryFormAuthorizeRenderer(val);
            }
        }
        ]);
        this.selectedgrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store: this.selectedds,
            cm: this.selectedcm,
            autoScroll:true,
            border:false,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg"))
            }
        });

        this.assignTeamPanel = new Wtf.Panel({
            layout : 'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                cls : 'formstyleClass',
                html :getTopHtml(WtfGlobal.getLocaleText("hrms.payroll.view.statutory.form.status"), WtfGlobal.getLocaleText("hrms.payroll.select.employee.view.statutory.form.status"), "../../images/assign-manager.gif")

            },{
                region : 'center',
                border :false,
                layout : 'fit',
                items : [{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : 'border',
                    items : [
                    {
                        region : 'west',
                        border : true,
                        width : 350,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title : WtfGlobal.getLocaleText("hrms.common.Employees"),
                            border : true,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.availablegrid
                        }]
                    },{
                        region : 'center',
                        layout : 'fit',
                        border : true,
                        items :[{
                            xtype : 'KWLListPanel',
                            title : WtfGlobal.getLocaleText("hrms.payroll.statutory.forms.status"),
                            border : true,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.selectedgrid
                        }]
                    }]
                }]
            }]
        });


        this.add(this.assignTeamPanel);

        this.availablesm.on("rowselect", function (sm, rowIndex, record){
            this.selectedds.load({
                params: {
                    userid:record.data.userid
                }
            });
        },this)

    }
   
});

