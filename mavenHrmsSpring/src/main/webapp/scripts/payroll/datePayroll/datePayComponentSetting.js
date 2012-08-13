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
Wtf.datePayCompoSetting = function(config){
      Wtf.datePayCompoSetting.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.datePayCompoSetting, Wtf.Panel, {
    initComponent: function() {

        Wtf.datePayCompoSetting.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.datePayCompoSetting.superclass.onRender.call(this, config);

        this.frequencyCombo= new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.masterconf.frequency"),
            hiddenName: 'frequency',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:Wtf.frequencyStore,
            width:150,
            typeAhead:true,
            value:0
        });
        
        this.record = Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"sdate"
        },
        {
            "name":"edate"
        },
        {
            "name":"desc"
        },
        {
            "name":"type"
        },
        {
            "name":"isadjust"
        },
        {
            "name":"isdefault"
        },
        {
            "name":"isblocked"
        },
        {
            "name":"istaxablecomponent"
        },
        {
            "name":"frequency"
        },
        {
            "name":"costcenter"
        },
        {
            "name":"paymentterm"
        },
        {
            "name":"amount"
        },
        {
            "name":"basevalue"
        },
        {
            "name":"method"
        },{
            "name":"percent"
        },
        {
            "name":"computeon"
        },{
            "name":"expression"
        }]);

        this.ds = new Wtf.data.Store({
            baseParams: {
                flag: 101
            },
            url: "Payroll/Date/getPayComponent_Date.py",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },
            this.record
            )
        });
        
        this.sm= new Wtf.grid.CheckboxSelectionModel({});
        this.rowNo=new Wtf.grid.RowNumberer();
        this.cm = new Wtf.grid.ColumnModel([
            this.sm,this.rowNo,
            {
                dataIndex: 'compid',
                hidden: true,
                fixed:true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.code"),
                dataIndex: 'code',
                sortable: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.description"),
                dataIndex: 'desc',
                sortable: true,
                renderer:function(val){
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                    return "<span style='white-space:pre-wrap;'>"+val+"</span>";
                }
            },
            {
                header:WtfGlobal.getLocaleText("hrms.payroll.Amount"),
                dataIndex:'amount',
                 sortable: true,
                renderer:function(val){
                    if(val=="" || val ==null)
                        return WtfGlobal.currencyRenderer(0);
                    else
                        return WtfGlobal.currencyRenderer(val);
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.start.date"),
                dataIndex: 'sdate',
                sortable: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.end.date"),
                dataIndex: 'edate',
                sortable: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.added.default"),
                dataIndex:'isdefault',
                sortable: true,
                align:'center',
                renderer:function(val,a,b){
                    if(val){
                        return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                    }else{
                        return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                    }
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.is.blocked"),
                dataIndex:'isblocked',
                sortable: true,
                align:'center',
                renderer:function(val,a,b){
                    if(val){
                        return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                    }else{
                        return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                    }
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.is.ajustable"),
                dataIndex:'isadjust',
                sortable: true,
                align:'center',
                renderer:function(val,a,b){
                    if(val){
                        return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                    }else{
                        return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                    }
                }
            },
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.is.taxable"),
                dataIndex:'istaxablecomponent',
                sortable: true,
                align:'center',
                renderer:function(val,a,b){
                    if(val){
                        return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                    }else{
                        return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                    }
                }
            }
            ]); 
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.ds.load({params:{start:0,limit:this.componentGrid.pag.pageSize,frequency:this.frequencyCombo.getValue()}});
        		Wtf.getCmp("Quick"+this.componentGrid.id).setValue("");
        	}
     	});
        
        this.deleteButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.delete.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:53,
            disabled:true,
            hidden:true,
            scope:this,
            handler:this.delete1
        });

        this.addButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.payroll.addnew"),
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.addnew.tooltip"),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:42,
            handler:this.add1,
            hidden:true,
            scope:this
        });

       this.editButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.edit"),
            tooltip:WtfGlobal.getLocaleText("hrms.payroll.carry.changes.payroll.component.settings"),
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            minWidth:42,
            disabled:true,
            hidden:true,
            handler:this.edit1,
            scope:this
       });
       
       this.searchText= new Wtf.form.TextField({
            emptyText:WtfGlobal.getLocaleText("hrms.common.search"),
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.search"),
            handler:this.search1,
            scope:this
        });
       
       this.frequency =this.frequencyCombo.getValue();
     var componentbtns=new Array();
     componentbtns.push('-');
     componentbtns.push(this.refreshBtn);
     componentbtns.push('-');
     componentbtns.push(this.addButton);
     componentbtns.push('-');
     componentbtns.push(this.editButton);
     componentbtns.push('-');
     componentbtns.push(this.deleteButton);
     componentbtns.push('-');
     componentbtns.push(WtfGlobal.getLocaleText("hrms.payroll.select.frequency")+': ');
     componentbtns.push(this.frequencyCombo);

     this.frequencyCombo.on("select", function(){
    	 this.ds.load({params:{start:0,limit:this.componentGrid.pag.pageSize,frequency:this.frequencyCombo.getValue()}});
     }, this);

//     if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymaster, Wtf.Perm.competencymaster.add)){
        this.addButton.show();
//     }
//     if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymaster, Wtf.Perm.competencymaster.edit)){
         this.editButton.show();
//     }
//     if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymaster, Wtf.Perm.competencymaster.deletecomp)){
         this.deleteButton.show();
//     }
        this.componentGrid=new Wtf.KwlGridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            cls:"gridWithUl",
            border:false,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:adddatepaycomp(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.payroll.started.adding.component")+"</a>")
            },
            searchLabel:" ",
            id:"ComponentGrid",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.search.code"),
            loadMask:true,
            serverSideSearch:true,
            displayInfo:true,
            searchField:"code",
            listeners:{
                scope:this,
                rowclick:function(componentGrid,rowIndex,evObj)
                {
                    this.rowindex=rowIndex;
                }
            },
            tbar:componentbtns
        });


        this.add(this.componentGrid);

        this.ds.on('beforeload',function(store,option){
            option.params= option.params||{};
            option.params.frequency=this.frequencyCombo.getValue();
            option.params.limit=this.componentGrid.pag.pageSize;
        },this);
        calMsgBoxShow(202,4,true);

        this.ds.load({params:{start:0}});
        
        this.ds.on("load",function(){
            if(msgFlag==1) {
                WtfGlobal.closeProgressbar();
            }
        },this);
        
        this.sm.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(componentbtns, this.componentGrid, [5], [7]);
        },this);
    },
    
    add1:function(){       
        generalconf('Add',null,this.frequencyCombo.getValue());
    },
    
    edit1:function(){
         var rec=this.sm.getSelected()
        generalconf('Edit',rec,this.frequencyCombo.getValue());
    },
    
    delete1:function(){
        var componentid=[];
        for(var i=0;i<this.sm.selections.length;i++){
            componentid.push(this.sm.selections.items[i].get("compid"));
        }
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.delete.components"), function(btn){
            if(btn!="yes") return;
            calMsgBoxShow(201,4,true);
            Wtf.Ajax.requestEx({
                url:"Payroll/Date/deletePayComponent_Date.py",
                params:{
                    componentid:componentid
                }
            },
            this,
            function (response){
                var res=eval('('+response+')');
                if(res.success==true){
                	calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.component.deleted.successfully")],0);
                    this.componentGrid.getStore().reload();
                }else{
                	if(res.somedeleted){
                		calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.component.cannot.deleted.linked.automatically")],2);
                		this.componentGrid.getStore().reload();
                	}else{
                		calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.this.component.cannot.deleted.linked.automatically")],2);
                	}
                }
            },function (){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.error.deleting.component")],2);
            });
        },this);
   


    }
    
 });
function adddatepaycomp(Id){
    Wtf.getCmp(Id).add1();
}
function generalconf(action,rec,frequency){
    var mainTabId = Wtf.getCmp("as");
    var projectBudget = Wtf.getCmp("PayCompoSetting");
    if(projectBudget == null){
        projectBudget = new Wtf.AddPayrollComponent({
            layout:"fit",
            width:400,
            height:600,
            isEdit:action=='Edit'?true:false,
            action:action,
            rec:rec,
            title:WtfGlobal.getLocaleText({key:"hrms.payroll.payroll.component.params",params:[action]}),
            closable:true,
            border:false,
            iconCls: getTabIconCls(Wtf.etype.master),
            id:"PayCompoSetting",
            frequency:frequency
        });
        projectBudget.show();
    }
}
