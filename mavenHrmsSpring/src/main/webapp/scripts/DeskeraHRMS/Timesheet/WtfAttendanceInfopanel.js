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
    Wtf.ux.comboBoxRenderer = function(combo) {
        return function(value) {
            var idx = combo.store.find(combo.valueField, value);
            if(idx==-1)return "";
            var rec = combo.store.getAt(idx);
            return rec.get(combo.displayField);
        };
    }


Wtf.AttendanceInfopanel = function(config) {
    Wtf.apply(this, config);
    Wtf.AttendanceInfopanel.superclass.constructor.call(this,{
        border: false
    });
}

Wtf.extend(Wtf.AttendanceInfopanel, Wtf.Panel, {

    initComponent: function() {

        var action = this.act;
        this.presentCmbStore = new Wtf.data.SimpleStore({
            fields:['statusid','status'],
            data:[[1,'Present'],
                  [2,'Absent']
                 ]
        });

  this.typesRecord = Wtf.data.Record.create([
           {name: 'type_id'},
		   {name: 'type'}

       ]);


        this.typeCmbStore = new Wtf.data.SimpleStore({
            fields:['typeid','type'],
            data:[[0,'Part Time'],
                  [3,'Full Time'],
                  [2,'Executive'],
                  [1,'Contract']
                 ]
        });
		this.presentCmb =new Wtf.form.ComboBox({
			   mode : 'local',
			   editable: false,
			   store: this.presentCmbStore,
			   displayField: 'status',
			   valueField: 'statusid',
               allowBlank: false,
               typeAhead: true,
			   triggerAction: 'all'
        });

        this.Center = Wtf.data.Record.create([
            {name: 'id'},
            {name: 'empcodeid'},
            {name: 'name'},
            {name: 'company'},
            {name: 'phone'},
            {name: 'email'},
            {name: 'docid'},
            {name: 'type'},
            {name: 'docname'},
            {name: 'storename'},
            {name: 'rateperhour'},
            {name: 'overrateperhour'},
            {name: 'holirateperhour'},
		    {name: 'isabsent'},
            {name: 'designation'},
            {name: 'statusid'},
            {name: 'phone'},
            {name: 'nohr'}
        ]);
        if(this.act==0||this.act==1||this.act==2){
        this.dsCenter = new Wtf.data.Store({

            baseParams: {
                flag: 300,
                companyid: companyid,
                type: 'c',
                text: ''
            },
            groupField:'storename',
            sortInfo:{field: 'storename', direction: "ASC"},
            url: Wtf.req.base + 'hrms.jsp',
            //url: 'json/Employee.json',
            reader: new Wtf.data.KwlJsonReader1({
                root:"data",
                totalProperty: 'TotalCount',
                remoteGroup:true,
                remoteSort: true
            }, this.Center)
        });
        }        
      
         this.page = new Wtf.PagingToolbar ({
            id: 'paging' + this.id,
            pageSize: 15,
            store: this.dsCenter,
            displayInfo: true,
//            displayMsg: 'Displaying results {0} - {1} of {2}',
//            emptyMsg: "No results to display",
            plugins: new Wtf.common.pPageSize()
         });
         this.gridsm=new Wtf.grid.CheckboxSelectionModel();
        this.cmCenter = new Wtf.grid.ColumnModel([
            this.gridsm,
           // new Wtf.grid.RowNumberer(),
            {
                dataIndex: 'id',
                hidden: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'empcodeid',
                sortable:true,
                hidden:true
                //groupable:true
            }

            ,{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'name',
                scope:this,
                sortable:true,
                renderer : function( value,metadata,record,row,col,store) {
                    if(action==0){
                   return "<a href = '#' class='viewclass' style = 'color : #175C9E;'> "+value +" </a>";
                    }else
                        return value;
                }



            },{
                header: WtfGlobal.getLocaleText("hrms.common.store"),
                dataIndex: 'storename',
                sortable:true
                //groupable:true
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.profile.MobileNo"),
                dataIndex: 'phone',
                hidden:this.act==0?false:true,
                sortable:true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'designation',
                sortable:true                
            },{
                header: WtfGlobal.getLocaleText("hrms.common.type"),
                dataIndex: 'type',
                sortable:true                
            },{
                header: WtfGlobal.getLocaleText("hrms.timesheet.no.hours"),
                dataIndex: 'nohr',
                sortable:true,               
                editor:new Wtf.form.NumberField(),
                hidden:this.act==1||this.act==2?false:true
            },{
                header: "Rate/Hour",
                dataIndex: 'rateperhour',
                editor:new Wtf.form.NumberField(),
                sortable:true
            },{
                header: "Overtime Rate/Hour",
                dataIndex: 'overrateperhour',
                editor:new Wtf.form.NumberField(),
                sortable:true
            },{
                header: "DaysPay",             
                renderer: this.calculatedayspay,
                 hidden:this.act==1||this.act==2?false:true
            },{
                header: WtfGlobal.getLocaleText("hrms.timesheet.attendance"),
                dataIndex: 'statusid',
                hidden:this.act==1?false:true,
                editor: this.presentCmb,
                renderer: Wtf.ux.comboBoxRenderer(this.presentCmb),
                scope:this             
            }
        ]);

        //this.gridsm = new Wtf.grid.RowSelectionModel();
        this.grid = new Wtf.AccGridComp({
            id : "accGrid" + this.id,
            border: false,
            selModel: this.gridsm,
            ds : this.dsCenter,
            cm : this.cmCenter,
             viewConfig: {
                        forceFit: true,
                        emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg")+'</font></center>'
                    }
        });
        this.addEvents({
            "opennewform": true,
            "deleteform": true
        });
    }, 
    onRender : function(config) {
        Wtf.AttendanceInfopanel.superclass.onRender.call(this,config);

        this.updateBut = new Wtf.Button({
                    text:WtfGlobal.getLocaleText("hrms.common.Update"),
                    iconCls:getButtonIconCls(Wtf.btype.submitbutton)
                });
         this.err1 = new Array();
           this.err1.push(this.page);
         if(this.act==1||this.act==2){
               this.err1.push(this.updateBut);

         this.updateBut.on("click",this.updateHandler,this);
         }
      
      
        var err = new Array();
        err.push(WtfGlobal.getLocaleText("hrms.common.QuickSearch")+" : ");
        err.push(this.quickSearchTF = new Wtf.wtfQuickSearch({
                           width: 200,
                           field : "name",
                           emptyText:WtfGlobal.getLocaleText("hrms.common.grid.search.msg")
                           }));
       
        this.leavesRecord = Wtf.data.Record.create([
            {name:'id'},
            {name:'name'},
           {name: 'store_id'},
		   {name: 'store_name'},
           {name: 'loc'},
           {name: 'phno'},
           {name: 'ManagerId'}
       ]);


/****************************************************************************/
//         this.typesRecord = Wtf.data.Record.create([
//           {name: 'type_id'},
//		   {name: 'type'},
//
//       ]);


        this.from_type = new Wtf.data.Store({
           autoLoad:false,
           url:  Wtf.req.base + 'inventory/inventory.jsp?flag=49',
            reader: new Wtf.data.KwlJsonReader({
                     totalProperty: 'count',
                     root:'data'
            }, this.typesRecord )
       });

       this.typename = new Wtf.form.ComboBox({
                            hiddenName : 'fromType',
                            store : this.from_type,
                            readOnly : true,
                            editable: false,
                           // allowBlank:false,
                            displayField:'type',
                            valueField:'type',
                            mode: 'local',
                            width : 200,
                            triggerAction: 'all',
                            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.type"),
							id : 'tr_fromType_id'+this.act
                    });
/******************************************************************************************/
       this.from_Store = new Wtf.data.Store({
           autoLoad:true,
           baseParams:{
               type:3,
               configid:7
           },
            //url:  Wtf.req.base + 'inventory/inventory.jsp?flag=46',
            url: "Common/getMasterDataField.common?flag=203",
            reader: new Wtf.data.KwlJsonReader1({
                    // totalProperty: 'count',
                     root:'data'
            }, this.leavesRecord)
       });
       this.from_Store.on('load',function(){
            if(this.act==0||this.act==1){
            this.storename.setValue("Store10");
            this.dsCenter.load({
                    params:{
                        filtertype:"store",
                        filterval:"Store10",
                        start:0,
                        limit:15
                    }
                });
            }

       },this);
//       this.typeCmbStore = new Wtf.data.SimpleStore({
//            fields:['type'],
//            data:[['Part Time'],
//                  ['Full Time'],
//                  ['Executive'],
//                  ['Contract']
//                 ]
//        });
		this.typeCmb =new Wtf.form.ComboBox({
			   mode : 'local',
			   editable: false,
			   store: this.typeCmbStore,
			   displayField: 'type',
			   valueField: 'typeid',
                emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.type"),
			   allowBlank: false,
               typeAhead: true,
			   //,
               triggerAction: 'all'
        });
       this.from_Store11 = new Wtf.data.Store({
           autoLoad:true,
           baseParams:{
               type:1,
               configid:7
           },
            url: "Common/getMasterDataField.common?flag=203",
            reader: new Wtf.data.KwlJsonReader1({
                     totalProperty: 'count',
                     root:'data'
            }, this.leavesRecord)
       });
this.from_Store11.load();
       /************************************************************************/
        this.storename = new Wtf.form.ComboBox({
                            hiddenName : 'fromStore',
                            store : this.from_Store,
                            readOnly : true,
                            editable: false,
                           // allowBlank:false,
                            displayField:'name',
                            valueField:'id',
                            mode: 'local',
                            width : 200,
                            triggerAction: 'all',
                            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.type"),
							id : 'tr_fromStore_id'+this.act
                    });
        this.storename1 = new Wtf.form.ComboBox({
                            hiddenName : 'fromStore11',
                            store : this.from_Store11,
                            readOnly : true,
                            editable: false,
                            //allowBlank:false,
                            displayField:'name',
                            valueField:'id',
                            mode: 'local',
                            width : 200,
                            triggerAction: 'all',
                            emptyText:WtfGlobal.getLocaleText("hrms.payroll.select.type"),
							id : 'tr_fromStore_id11'
                    });

/******************************************************************************/
 this.grid.on('rowclick', function(grid,rowindex,e) {

//            alert(e.getTarget().className);
           if(e.getTarget().className == 'viewclass')
               {

              //  var accountTab = Wtf.getCmp("EmpTab"+this.id);
                var accountTab123 = Wtf.getCmp("as");

                    //var rec = this.grid.getStore().getAt(this.grid.arow);
                    var rec = this.gridsm.getSelected();
                    this.empid = rec.data['id'];
                    if(!Wtf.getCmp(this.empid + this.id)) {
                        this.EmpDetails = new Wtf.EmployeeDetailPanel({
                            id: this.empid + this.id,
                            title:WtfGlobal.getLocaleText({key:"hrms.timesheet.s.details",params:[rec.data['name']]}),
                            closable: true,
                            border: false,
                            layout: "fit" ,
                            empid: this.empid
                        });
                        accountTab123.add(Wtf.getCmp(this.empid + this.id));
                    }
                    accountTab123.setActiveTab(Wtf.getCmp(this.empid + this.id));
                    accountTab123.doLayout();
               }

        }, this);             
        if(this.act==1||this.act==0)
{     
        err.push("Type : ");
        err.push(this.typename);
         err.push("-");
        err.push(     this.newBtn = new Wtf.Button({
                anchor : '90%',
                text: 'ALL DATA',
                id: 'newType',
                handler: function(){
                     this.storename.setValue("");
                    this.typename.setValue("");
                 //   this.dsCenter.filter('id','');
                  this.dsCenter.load({
                    params:{
                        start:0,
                        limit:15
                    }
                });
                },
                scope:this
       })
       );
}

        err.push("-");
        err.push("Date : ");
        this.tempDate = new Wtf.form.DateField({
            value:new Date(),
            readOnly:true,
            format:'Y-m-d',
            width : 200
        });
        err.push("-");
        err.push(this.tempDate);
        this.tmsheetBtn = new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.Featurelist.viewtimesheet"),
            iconCls:getButtonIconCls(Wtf.btype.viewbutton),
            minWidth:96,
            scope:this,
            handler:this.openTimesheet
        });
        err.push("-");
        err.push(this.tmsheetBtn);
        this.storename.on("select",this.filterGrid,this);
        this.typename.on("select",this.filterGridtype,this);
        this.tempDate.on("change",function(field,newval,oldval){

            //alert(newval+oldval);

        },this);
  //  this.quickSearchTF.StorageChanged(this.dsCenter);

/**********************************************************************************************/
        this.innerpanel = new Wtf.Panel({
            id : "Innpan" + this.id,
            border: false,
            layout :'fit',
            tbar:err,
            bbar:this.err1,
            items: this.grid
        });
        this.add(this.innerpanel);
        this.dsCenter.load({
                    params:{
                        start:0,
                        limit:15
                    }
                });
        this.dsCenter.on('load',function(){
             this.quickSearchTF.StorageChanged(this.dsCenter);
         },this);

        
    },

    openTimesheet:function(){
        var rs=this.grid.getSelectionModel().getSelections();
        if(rs.length!=1){
          calMsgBoxShow(72,0);
            return;
        }
        //alert("timesheet open"+rs[0].get('id'));
        var main=Wtf.getCmp("timesheetmanage");
        var vtdemoTab=Wtf.getCmp("viewtimesheet");
        if(vtdemoTab==null)
        {
            vtdemoTab=new Wtf.viewtimesheet({
                id:"viewtimesheet",
                title:WtfGlobal.getLocaleText("hrms.timesheet.view.timesheets"),//"View TimeSheets",
                layout:'fit',
                border:false,
                //closable:true,
                iconCls:getTabIconCls(Wtf.etype.hrmsviewtimesheet)
            });
            main.add(vtdemoTab);
        }
        main.setActiveTab(vtdemoTab);
        main.doLayout();
        vtdemoTab.doLayout();
        Wtf.getCmp("as").doLayout();
    },

 /************************************************************************************************/

     calculatedayspay:function (a,b,c,d,e){
          if(c.get('statusid')==2){
               return "0";
          }

        return c.get("rateperhour")*c.get("nohr");
    },
        takemyChange:function (a,b,c,d,e){
       //alert(a);
       if(a=="Software Engineer")
           {
               return "Full Time";
           }
       else if(a=="Trainee Engineer")
           {
               return "Part time";
           }
        else{
            return "Executive";
        }
   },updateHandler:function (){

       if(this.act==2){
        var i=0;   
        if(this.gridsm.getSelections().length == 0){         
         calMsgBoxShow(42,0);
    }else{
             {
                 var arr = new Array();
                 for(i=0;i<this.gridsm.getSelections().length;i++){
                     
                     arr.push(this.gridsm.getSelections()[i].get('id'));
                }
        }
    }
  }
       if(this.act==1){

           calMsgBoxShow(79,0);

       }
     },
    filterGrid:function (){
       // this.dsCenter.filter("storename",this.storename.getValue(),true,true);
       this.dsCenter.load({
                    params:{
                        filtertype:"store",
                        filterval:this.storename.getValue(),
                        start:0,
                        limit:15
                    }
                });
      this.typename.setValue("");
    },
    filterGridtype:function (){
        //this.dsCenter.filter("type",this.typename.getValue(),true,true);
         this.dsCenter.load({
                    params:{
                        filtertype:"type",
                        filterval:this.typename.getValue(),
                        start:0,
                        limit:15
                    }
                });
        this.storename.setValue("");
    }
});
