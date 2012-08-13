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


Wtf.UnautorizeSalaryWin = function (config){
    Wtf.apply(this,config);
    this.save = true;
    Wtf.UnautorizeSalaryWin.superclass.constructor.call(this,{
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

Wtf.extend(Wtf.UnautorizeSalaryWin,Wtf.Window,{
    initComponent:function (){
        Wtf.UnautorizeSalaryWin.superclass.initComponent.call(this);
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
            html:getTopHtml(WtfGlobal.getLocaleText("hrms.payroll.unauthorize.payroll"), WtfGlobal.getLocaleText("hrms.payroll.unauthorize.payroll.selected.employee"), "../../images/assign-manager.gif")
        });
    },
    GetCenterPanel:function (){

        this.sm2 = new Wtf.grid.CheckboxSelectionModel();


          this.cm =   new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            this.sm2,
            {
                dataIndex: 'payhistoryid',
                hidden: true
            },{
                header:WtfGlobal.getLocaleText("hrms.payroll.resource"),
                dataIndex: 'employeeid'
            },{
                header:WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname'
            }
         ]);
         this.reportGrid = new Wtf.grid.GridPanel({
            region:'center',
            id:this.id+'authorize_statutory_form_win',
            store: this.empGDS,
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

         this.comment = new Wtf.ux.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.Comment"),
            scope:this,
            width:200,
            maxLength:255,
            name:"unauthorize_comment"
        });

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"south",
            height:100,
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:15px 55px 45px 155px",
            items:[
                this.comment
            ]
        });
    },
    saveAuthorizeStatus:function (){

            var historyid=[];

            if(!this.AddEditForm.form.isValid()){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.enter.valid.comment")],0);
                return
                
            };
            var sel = this.sm2.selections;
            if(sel.length==0){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.payroll.select.record.authorized.salary")],0);
                return;
            }

           var htmlString=WtfGlobal.getLocaleText("hrms.payroll.unauthorize.selected.employee.salary");

            for(var i=0;i<sel.length;i++){
                if(sel.items[i].get("status")==2||sel.items[i].get("status")==3){
                    historyid.push(sel.items[i].get("payhistoryid"));
                }
            }
            
            var comment= this.comment.getValue();

            comment = WtfGlobal.replaceAll(comment, "'", '&#39;');
            comment = WtfGlobal.replaceAll(comment, "\"", '&#34;');
            comment = WtfGlobal.replaceAll(comment, "\\\\", '\\\\');
            
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), htmlString,function(btn){
                if(btn!="yes") return;
                calMsgBoxShow(200,4,true);
                Wtf.Ajax.requestEx({
                    url:"Payroll/Date/updatePayrollHistory.py",
                    params:{
                        historyid:historyid,
                        statusid:4,
                        comment : comment
                    }
                },
                this,
                function (response){
                    var res=eval('('+response+')');
                    if(res.success==true){

                        Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.salary.unauthorized.successfully"));
                        this.grid.getStore().reload();
                        this.close();
                    }else {
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.some.error.while.unauthorizing")],2);
                    }
                },function (){
                    calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.some.error.while.unauthorizing")],2);
                });
            },this);

    }
});


