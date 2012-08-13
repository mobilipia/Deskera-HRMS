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
Wtf.AssignEmployeeFT=function(config){

    config.border=false;
    config.layout="border";
    config.monitorResize=true;
    config.coun=0;
    config.frame=false;
    config.autoScroll=true;
            config.tbar=[{
                text:WtfGlobal.getLocaleText("hrms.common.AssignTemplate"),
                scope:this,
                minWidth:110,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                handler:function(){
                    Wtf.Msg.show({
                        title:  WtfGlobal.getLocaleText("hrms.common.warning"),
                        msg: WtfGlobal.getLocaleText("hrms.payroll.save.changes"),
                        buttons: Wtf.Msg.YESNO,
                        fn: function(btn,value){
                            if(btn=='yes'){
                                this.saveEdited();
                            }
                        },
                        scope: this,
                        animEl: 'elId',
                        icon: Wtf.MessageBox.QUESTION
                    });
                }
            }];
    Wtf.AssignEmployeeFT.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.AssignEmployeeFT,Wtf.Panel,{
    initComponent:function(config){
        Wtf.AssignEmployeeFT.superclass.initComponent.call(this,config);



        this.emplistfields=[
        {
            name:'uname'
        },

        {
            name:'design'
        },

        {
            name:'salary'
        }
        ];
        this.EmployeeListIndex=0;
        this.unassSelMod=new Wtf.grid.CheckboxSelectionModel({
            scope:this,
            singleSelect:false
        });
        this.unassColMod=new Wtf.grid.ColumnModel(

            [
            this.unassSelMod,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                sortable: true,
                dataIndex: 'empname',
                renderer:function(val,a,b){
                    if(val!=null){
                       if(b.get('status')=='2'){
                            return('<div style="color:blue;" align=\"left\">'+val+'</div>');
                        }else{
                            return('<div align=\"left\">'+val+'</div>');
                        }
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                sortable: true,
                dataIndex: 'design',
                renderer:function(val,a,b){
                    if(val!=null){
                        if(b.get('status')=='2'){
                            return('<div style="color:blue;" align=\"left\">'+val+'</div>');
                        }else{
                            return('<div align=\"left\">'+val+'</div>');
                        }
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.Salary"),
                sortable: true,
                dataIndex:'salary',
                renderer:function(val,a,b){
                    if(val!=null){
                        if(b.get('status')=='2'){
                            return('<div style="color:blue;" align=\"left\">'+WtfGlobal.currencyRenderer(val)+'</div>');
                        }else{
                            return('<div align=\"left\">'+WtfGlobal.currencyRenderer(val)+'</div>');
                        }
                    }
                }
            }
            ]);

        this.EmployeeListGrid = new Wtf.grid.GridPanel({
            autoScroll:true,
            frame:false,
            scope:this,
            //iconCls:'icon-grid',
            border:true,
            store:this.gridStore=new Wtf.data.Store({
                url: Wtf.req.base + "PayrollHandler.jsp",
                method:'GET',
                scope:this,
                id:this.id+'gridStore',
                reader:new Wtf.data.KwlJsonReader({
                    root:'data',
                    totalProperty:'totalCount'
                },[{
                    name:'empname'
                },{
                    name:'design'
                },{
                    name:'salary'
                },{
                    name:'empid'
                },{
                    name:'index'
                },{
                    name:'status'
                },{
                    name:'lname'
                }
                ]
                )
            }),
            forceFit: true,
            viewConfig:{
                forceFit:true
            },
            cm:this.unassColMod,
            sm:this.unassSelMod
        });
        this.EmployeeListGrid.on('beforerender',function(grid){
            this.i=0;
            while(this.i<grid.getStore().getCount()){
                this.recor=grid.getStore().getAt(i);
                this.recor.beginEdit();
                this.recor.set('index',this.EmployeeListIndex);
                this.recor.endEdit();
                this.recor.commit();
                grid.commitChanges();
                this.EmployeeListIndex++;
                this.i++;
            }
            grid.getStore().sort("index","ASC");
        });
        this.assSelModel=new Wtf.grid.CheckboxSelectionModel({
            scope:this,
            singleSelect:false,
            listeners:
            {
                scope:this,
                rowselect:function(sm,row,rec){
                    this.a1=row;
                    this.arr=this.EmployeeListGrid.getSelections();
                    this.arr2=new Array();
                    for(i=0;i<this.arr.length;i++)
                    {
                        this.arr2.push(this.arr[i].get('empid'));
                    }
                },
                rowdeselect :function(sm,row,rec){
                    if(this.EmployeeListGrid.getSelections().length<1){
                        this.arr2=null;
                    }
                }
            }
        });
        this.assColModel=new Wtf.grid.ColumnModel(
            [
            this.assSelModel,
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                sortable: true,
                dataIndex: 'empname',
                renderer:function(val,a,b){
                    if(val!=null){
                            return('<div style="color:green;" align=\"left\">'+val+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                sortable: true,
                dataIndex: 'design',
                renderer:function(val,a,b){
                    if(val!=null){
                            return('<div style="color:green;" align=\"left\">'+val+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.Salary"),
                sortable: true,
                dataIndex: 'salary',
                renderer:function(val,a,b){
                    if(val!=null){
                            return('<div style="color:green;" align=\"left\">'+WtfGlobal.currencyRenderer(val)+'</div>');
                    }
                }
            }
            ]
            );

        
        this.AssignEmployeeListGrid = new Wtf.grid.GridPanel({
            autoScroll:true,
            frame:false,
            id:'AssignEmployeeListGrid',
            border:true,
            scope:this,
            store:this.gridStore1=new Wtf.data.Store({
                url: Wtf.req.base + "PayrollHandler.jsp",
                method:'GET',
                scope:this,
                reader:new Wtf.data.KwlJsonReader({
                    root:'data',
                    totalProperty:'totalCount'
                },[{
                    name:'empname'
                },{
                    name:'design'
                },{
                    name:'salary'
                },{
                    name:'empid'
                },{
                    name:'index'
                },{
                    name:'status'
                }
                ]
                )
            }),

            forceFit: true,
            viewConfig:{
                forceFit:true
            },
            cm:this.assColModel,
            sm:this.assSelModel
        });

        this.movetoright = document.createElement('img');
        this.movetoright.src = "../../images/arrowright.gif";
        this.movetoright.style.width = "24px";
        this.movetoright.style.height = "24px";
        this.movetoright.style.margin = "5px 0px 5px 0px";
        this.movetoright.onclick = this.insert_Item.createDelegate(this,[this.EmployeeListGrid,this.AssignEmployeeListGrid,1]);

        this.movetoleft = document.createElement('img');
        this.movetoleft.src = "../../images/arrowleft.gif";
        this.movetoleft.style.width = "24px";
        this.movetoleft.style.height = "24px";
        this.movetoleft.style.margin = "5px 0px 5px 0px";
        this.movetoleft.onclick = this.insert_Item.createDelegate(this,[this.AssignEmployeeListGrid,this.EmployeeListGrid,0]);

        this.centerdiv = document.createElement("div");
        this.centerdiv.appendChild(this.movetoright);
        this.centerdiv.appendChild(this.movetoleft);
        this.centerdiv.style.padding = "165px 10px 165px 10px";

        this.transferbtnPanel=new Wtf.Panel({
            region:'center',
            border:false,
            frame:false,
             bodyStyle:'background:white',
            contentEl : this.centerdiv
        });

        this.EmployeeListIndex=0;
        this.AssignEmployeeListGrid.on('beforerender',function(grid){
            this.i=0;
            while(this.i<grid.getStore().getCount()){
                this.recor=grid.getStore().getAt(i);
                this.recor.beginEdit();
                this.recor.set('index',this.EmployeeListIndex);
                this.recor.endEdit();
                this.recor.commit();
                grid.commitChanges();
                this.EmployeeListIndex++;
                this.i++;

            }
            grid.getStore().sort("index","ASC");
        });
        this.SelectSubGridContainerPanel=new Wtf.Panel({
            layout:'fit'
        });
        this.SelectSubData = [
        ['Newly Joined Trainee','12000-14000',5,3,2],
        ['Trainee Level 1','15000-17000',5,2,2],
        ['Trainee Level 2','17000-19000',5,1,3],
        ['Experinced Trainee','20000-22000',6,5,5],
        ['Half Time Trainee','2000-7000',3,2,9]
        ];
        this.SelectSubStore = new Wtf.data.SimpleStore({
            fields: [{
                name: 'PAGName'
            },{
                name: 'GrossEarning'
            },{
                name: 'TFTArea'
            },{
                name: 'DFTArea'
            },{
                name: 'NetEarning'
            }
            ]
        });

        this.SelectSubStore.loadData(this.SelectSubData);
        this.sm=new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true,
            listeners:
            {
                scope:this,
                selectRow:function(sm,row,rec){

                }
            }
        });
       
        this.firstgridpanel=new Wtf.Panel({

            layout:'fit',
            style:'background:#FFFFFF',
            border:false,
            scope:this,
            region:'west',
            width:'48%',
            items:[this.EmployeeListGrid]
        });

        this.secondgridpanel=new Wtf.Panel({
            layout:'fit',
            style:'background:#FFFFFF',
            border:false,
            scope:this,
            region:'east',
            width:'48%',
            items:[this.AssignEmployeeListGrid]
        });

        this.colorcodepan=new Wtf.Panel({
            border:false,
            region:'north',
            width:200,
            layout:'column',
            height:30,
            bodyStyle:'background-color:white',
            items:[{
                    xtype:'panel',
                    border:false,
                    columnWidth:0.5,
                    bodyStyle:'margin-left:20%;margin-top:1.5%',
                    html:"<div style='height:14px;width:14px;background-color:blue;float:left'></div>&nbsp; :"+WtfGlobal.getLocaleText("hrms.payroll.employee.assigned.to.template")
                   },{
                    xtype:'panel',
                    border:false,
                    bodyStyle:'margin-left:20%;margin-top:1.5%',
                    columnWidth:0.5,
                    html:"<div style='height:14px;width:14px;background-color:green;float:left'></div>&nbsp; :"+WtfGlobal.getLocaleText("hrms.payroll.employee.assigned.to.current.template")
            }]
        });
        
        this.AssignmainPanel=new Wtf.Panel({
            layout:'border',
            style:'background:#FFFFFF',
            border:false,
            frame:false,
            region:'center',
            //autoScroll:true,
            id:'mainPanelAssigned',
            scope:this,
            items:[this.firstgridpanel,this.transferbtnPanel,this.secondgridpanel]
        });
        
        this.add(this.colorcodepan,this.AssignmainPanel);
        
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        
        this.gridStore.removeAll();
        this.gridStore.load({
            scope:this,
            params:'type=Users&cname=aa&Gname='+this.Gname+'&tid='+this.TempId
        });
        this.gridStore1.removeAll();
        this.gridStore1.load({
            scope:this,
            params:'type=AUsers&cname=aa&Tid='+this.TempId
        });
    },
    
   insert_Item:function(obj1,obj2,fl){

            this.recSize=obj1.getSelectionModel().getSelections().length;
            for(i=0;i<this.recSize;i++){
                if(obj1.getStore().indexOf(Rowselected=obj1.getSelectionModel().getSelected())!=-1)
                {
                    if(obj2.getStore().find('empid',Rowselected.get('empid'))==-1)
                    {
                        this.EmpSal=parseFloat(Rowselected.get('salary'));
                        this.Temprange=this.Srange;
                        this.TSRange=0.0;
                        this.TSRange=parseFloat(this.Srange.substring(0, this.Srange.indexOf('-')));
                        this.TERange=0.0;
                        this.TERange=parseFloat(this.Srange.substring(this.Srange.indexOf('-')+1,this.Srange.length));
                        if(this.EmpSal>=this.TSRange && this.EmpSal<this.TERange){
                            if(Rowselected.get('status')=='2' && fl==1){
                                Wtf.Msg.show({
                                    title: WtfGlobal.getLocaleText("hrms.common.warning"),
                                    msg: WtfGlobal.getLocaleText("hrms.payroll.employee.assigned.to.some.other.group"),
                                    scope:this,
                                    icon:Wtf.MessageBox.QUESTION,
                                    buttons: Wtf.Msg.YESNO,
                                    animEl: 'elId',
                                    fn:function(btn){
                                        if(btn=='yes'){
                                            obj2.getStore().add(Rowselected);
                                            obj2.getStore().commitChanges();
                                            obj1.getStore().remove(Rowselected);
                                            obj1.getStore().commitChanges();
                                        }
                                    }
                                });
                            }else {
                                obj2.getStore().add(Rowselected);
                                obj2.getStore().commitChanges();
                                obj1.getStore().remove(Rowselected);
                                obj1.getStore().commitChanges();
                            }
                        }else{
                            calMsgBoxShow(12,0);
                            obj1.getSelectionModel().deselectRow(obj1.getStore().indexOf(obj1.getSelectionModel().getSelected()));
                            this.recSize=obj1.getSelectionModel().getSelections().length;
                            i=0;
                        }
                    }else{
                        obj1.getStore().remove(Rowselected);
                        obj1.getStore().commitChanges();
                    }
                }
            }
        },
    saveEdited:function(){
        if(this.gridStore1.getCount()>0){
            this.arr2=null;
            this.arr2=new Array();
            for(i=0;i<this.gridStore1.getCount();i++)
            {
                this.arr2.push(this.gridStore1.getAt(i).get('empid'));
            }
            calMsgBoxShow(200,4,true);
            Wtf.Ajax.requestEx({
                url: Wtf.req.base + "PayrollHandler.jsp" ,
                method:'post',
                params:{
                    empidarr:this.arr2,
                    save:'true',
                    saveType:'AssignTemp',
                    tid:this.TempId,
                    TempName:this.TempName
                }
            },
            this,
            function(req,res){
                this.gridStore.removeAll();
                this.gridStore.reload();
                calMsgBoxShow(11,0);
                this.gridStore1.removeAll();
                this.gridStore1.reload();
                this.groupstore.reload();
            },
            function(req,res){
                }
                );
        }
        else{
            Wtf.Msg.show({
                msg: WtfGlobal.getLocaleText("hrms.payroll.unassign.all.employees"),
                width:260,
                scope:this,
                buttons: Wtf.Msg.YESNO,
                animEl: 'elId',
                fn:function(btn){
                    if(btn=='yes'){
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + "PayrollHandler.jsp" ,
                            method:'post',
                            params:{
                                save:'true',
                                saveType:'AssignTemp',
                                tid:this.TempId
                            }
                        },
                        this,
                        function(req,res){
                            calMsgBoxShow(11,0);
                            this.gridStore.removeAll();
                            this.gridStore.reload();
                            this.gridStore1.removeAll();
                            this.gridStore1.reload();
                        },
                        function(req,res){
                            }
                            );
                    }
                }
            });
        }
    }
});
