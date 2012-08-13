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

Wtf.PayslipGrid=function(config){
    Wtf.form.Field.prototype.msgTarget='side',
    //config.autoScroll=true;
    //config.layout="fit";

    config.border=false;
    Wtf.apply(Wtf.form.VTypes,
    {
        'percentage': function(val1) {
            var regExp = /^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
            return regExp.test(val1);
        },
        'percentageMask': /[0-9.]/,
        'percentageText': 'This field must be a percentage between 0.00 and 99.99'
    }
    );
    Wtf.PayslipGrid.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.PayslipGrid,Wtf.Panel,{
    initComponent:function(config){

        this.addEvents({
            "storeload": true
        });

        Wtf.PayslipGrid.superclass.initComponent.call(this,config);
        var salary=this.salary;
        var data=this.Data;
        var dtotal=this.dtotal;
        var fixedsal=this.fixedsal;
        var cursymbol=this.cursymbol;

//        this.modifiedflag=0;
        this.addEvents({
            "datamodified": true
        });

        this.rateEditor=new Wtf.form.TextField();
        this.amountEditor=new Wtf.form.TextField();
        this.cmtax=new Wtf.grid.ColumnModel([
           // new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText(this.Localetype),
                dataIndex: 'Type',
                sortable: true,
                autoWidth:true
            },{
                header:WtfGlobal.getLocaleText("hrms.payroll.Rate"),
                dataIndex: 'Rate',
                align:'center',
                sortable: true,
                hidden:true,
                autoWidth:true,
                //editor:(this.flag!="employee")?this.rateEditor:"",
                renderer:function(val,a,b){
                   if(b.get('ratetype')==1){
                       return WtfGlobal.percentageRenderer(val);
                   }
                   else{
                       return "<div align='center'>-</div>";
                   }
                }
             },{
                header: WtfGlobal.getLocaleText("hrms.payroll.percent.of"),
                dataIndex: 'computeon',
                align: 'center',
                sortable: true,
                hidden:(this.flag!="employee")?false:true,
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.payroll.Amount"),
                dataIndex:'amount',
                autoWidth:true,
                scope:this,
                sortable: true,
               // editor:(this.flag!="employee")?this.amountEditor:"",
                //readOnly:(this.flag=="employee")?true:false,
                align:'right',
                renderer:function(val){
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.delete"),
                dataIndex:'',
                width:30,
                hidden:(this.flag!="employee")?false:true,
                renderer:function(val, meta, record){
                    if(record.data.DeducId !='-1')
                        return "<div><div class='pwndCommon gridCancel' style='cursor:pointer' wtf:qtip="+ WtfGlobal.getLocaleText("hrms.common.DeleteRecord")+"></div></div>";
                }
            }
            ]);

        this.fieldstax=[
        {
            name:'Type'
        },
        {
            name:'Rate'
        },
        {
            name:'amount',
            type:'float'
        },
        {
            name:'amtot'
        },
        {
            name:'ratetype'
        },
        {
            name:'Id'
        },
        {
            name:'computeon'
        }
        ];


        this.fieldstaxx=Wtf.data.Record.create(this.fieldstax);
        this.readertax=new Wtf.data.KwlJsonReader({
            root:this.Data,
            totalProperty:'totalCount'
        },this.fieldstaxx);
        this.storetax= new Wtf.data.Store({
            scope:this,
            url : this.storeURL,
            method:'GET',
            reader: this.readertax
        });
        this.storetax.on("datachanged", function(){
        	WtfGlobal.closeProgressbar();
        }, this);
        
        this.storetax.on("beforeload", function(){
        	calMsgBoxShow(202,4,true);
        }, this);
        
        this.addbtn=new Wtf.Toolbar.Button({ 
            text:WtfGlobal.getLocaleText({key:"hrms.payroll.Addpayrollgrid",params:[WtfGlobal.getLocaleText(this.Localetype)]}),
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            tooltip:WtfGlobal.getLocaleText({key:"hrms.payroll.Addnewpayrollgridincurrentsalary",params:[WtfGlobal.getLocaleText(this.Localetype)]}),
            scope:this,
            handler:this.addnew
        });
        this.storetax.load({
            params:{
                paycyclestart:this.paycyclestart,
                paycycleend:this.paycycleend,
                paycycleactualstart:this.paycycleactualstart,
                paycycleactualend:this.paycycleactualend,
                ischanged:this.ischanged,
                mappingid:this.mappingid
            }
        });

        var btns=[];
        if(this.flag!="employee"){
            btns.push(this.addbtn);
        } 
            btns.push('->',WtfGlobal.getLocaleText("hrms.payroll.TOTAL")+'<span align=\"right\" style="font-family:Lucida Sans Unicode;">('+WtfGlobal.getCurrencySymbol()+')</span>',
            this.total= new Wtf.form.TextField({
                border:false,
                scope:this,
                cls:'textfstyle',
                width:100,
                value:0,
                fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.TOTAL"),
                readOnly:true,
                id:this.id+'gridtotal',
                height:16,
                bodyStyle:'background:white'
            }));
        this.grid = new Wtf.grid.EditorGridPanel({
            scope:this,
            //width:110,
            bodyStyle:'width:99.7%',
            //frame:true,
            //layout:'form',
            cm:this.cmtax,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            store:this.storetax,
            autoScroll:true,
            viewConfig: {
                forceFit: true
            },
            height:this.height,
            clicksToEdit:1,
            stripeRows: true,
            bbar:btns
        });

        this.grid.on("click",function(e){
            if(e.target.className=='pwndCommon gridCancel'){
                if(this.grid.getSelectionModel().getSelections().length>0){
                    //alert(this.grid.getSelectionModel().getSelections()[this.grid.getSelectionModel().getSelections().length-1].get('WagesId'));
                    //                    if(this.grid.getStore().indexOf(this.grid.getSelectionModel().getSelected())!=this.grid.getStore().getCount()-1){
                    this.grid.getStore().remove(this.grid.getSelectionModel().getSelected());
                    //                    }
                    var amtot=0;
                    for(i=0;i<this.storetax.getCount();i++)
                    {
                        amtot=amtot+this.storetax.getAt(i).get('amount');
                    }
                    this.total.setValue(WtfGlobal.currencyRenderer2(amtot));
                    Wtf.getCmp(this.empid+'payslipTab'+this.stdate).setTotalSal();
//                    this.modifiedflag=1;
                    this.fireEvent('datamodified');
                }
            }
        },this);

        this.storetax.on("load",function(){
            this.amtot=0;
            for(i=0;i<this.storetax.getCount();i++)
                {
                    this.amtot=this.amtot+this.storetax.getAt(i).get('amount');
                }
            this.total.setValue(WtfGlobal.currencyRenderer2(this.amtot));
            this.fireEvent("storeload");
            
        },this);
        
        this.pan1=new Wtf.Panel({
          //layout:'fit',
            height:this.height,
            // columnWidth:1,
            //width:600,
            border:false,
            bodyStyle:'margin-left:25%',
            scope:this,
            items:[this.grid]
        //bbar:['->',{xtype:'textfield',text:'total',scope:this,value:this.storetax.getAt(1).get('amount')}],

        });

        this.add(this.pan1);

        this.doLayout();
        this.pan1.doLayout();
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.grid.on("afteredit",this.fillGridValue,this);
        this.grid.on("beforeedit",this.chkEditorValue,this);
    },
     
    chkEditorValue:function(e){
      var x=1;
      if(e.record.get("ratetype")==0&&e.column==2){
          e.cancel=true;
      }

    },
    fillGridValue:function(e){
        var maxtot=0;
        if(e.column==2){
            this.storetax.getAt(e.row).set("amount",(this.fixedsal*this.storetax.getAt(e.row).get("Rate"))/100);
            for(var i=0; i <this.storetax.getCount(); i++){
                maxtot+=parseFloat(this.storetax.getAt(i).get("amount"));
            }
        } else if(e.column==4){
            this.storetax.getAt(e.row).set("Rate",(100*this.storetax.getAt(e.row).get("amount"))/this.fixedsal);
            for(var i=0; i <this.storetax.getCount(); i++){
                maxtot+=parseFloat(this.storetax.getAt(i).get("amount"));
            }
        }
        this.total.setValue(WtfGlobal.currencyRenderer2(maxtot));
        Wtf.getCmp(this.empid+'payslipTab'+this.stdate).setTotalSal();
//        this.modifiedflag=1;
        this.fireEvent('datamodified');

    },
    addnew:function(){
        var grids=Wtf.getCmp(this.id);
        var grgstore=grids.grid.getStore();
        var initcount=grgstore.getCount();
         var paywin=new Wtf.addpayCmpWin({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            layout:'fit',
            type:this.type,
            closable:true,
            resizable:false,
            stdate:this.stdate,
            gridst:grgstore,
            gridrec:this.fieldstaxx,
            title:WtfGlobal.getLocaleText("hrms.payroll.add.component"),
            border:false,            
            id:this.id+'paywindow',
            modal:true,
            scope:this,
            empid:this.empid,
            grid:grids,
            salary:this.fixedsal,
            plain:true
        });
        paywin.show();

    }


}); 
