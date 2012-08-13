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
Wtf.manageCompetency = function(config){
      Wtf.manageCompetency.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.manageCompetency, Wtf.Panel, {
    initComponent: function() {

        Wtf.manageCompetency.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.manageCompetency.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
        {
            "name":"cmptid"
        },
        {
            "name":"cmptname"
        },
        {
            "name":"cmptdesc"
        },

        {
            "name":"cmptwt"
        }

        ]);

        this.ds = new Wtf.data.Store({
            baseParams: {
                flag: 101
            },
//            url: Wtf.req.base + "hrms.jsp",
            url: "Performance/Competency/getCompetency.pf",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },
            this.record
            )
        });
        calMsgBoxShow(202,4,true);
        this.ds.load({
            params: {
                start:0,
                limit:15
            }});
        this.ds.on("load",function(){
            if(msgFlag==1) {
                WtfGlobal.closeProgressbar();
            }
        },this);

        this.sm= new Wtf.grid.CheckboxSelectionModel({});
        this.cm = new Wtf.grid.ColumnModel([
            this.sm,
            {
                dataIndex: 'cmptid',
                hidden: true,
                hideable:false
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.competency"),//"Competency",
                dataIndex: 'cmptname',
                sortable: true
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.description"),//"Description",
                dataIndex: 'cmptdesc',
                sortable: true,
                renderer:function(val){
                    if(Wtf.isIE6 || Wtf.isIE7)
                        return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                    return "<span style='white-space:pre-wrap;'>"+val+"</span>";
                }
            } 
            ]); 
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.ds.load({params:{start:0,limit:this.competencyGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.competencyGrid.id).setValue("");
        	}
     	});
        
        this.deleteButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.delete"),//'Delete',
            tooltip:WtfGlobal.getLocaleText("hrms.performance.competency.delete.tooltip"),//"Delete the competency which is not required anymore.",
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:53,
            disabled:true,
            hidden:true,
            scope:this,
            handler:this.deletecomptency
        });

        this.addButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.add"),//'Add',
            tooltip:WtfGlobal.getLocaleText("hrms.performance.competency.add.tooltip"),//"Add new competency for a job position with a brief description.",
            iconCls:getButtonIconCls(Wtf.btype.addbutton),
            minWidth:42,
            handler:this.add1,
            hidden:true,
            scope:this
        });

       this.editButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.common.edit"),//'Edit',
            tooltip:WtfGlobal.getLocaleText("hrms.performance.competency.edit.tooltip"),//"Edit the name or description of the competency.",
            iconCls:getButtonIconCls(Wtf.btype.editbutton),
            minWidth:42,
            disabled:true,
            hidden:true,
            handler:this.edit1,
            scope:this
       });

        this.searchText= new Wtf.form.TextField({
            emptyText:WtfGlobal.getLocaleText("hrms.common.search"),//'Search',
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.search"),//'Search',
            handler:this.search1,
            scope:this
        });
     var competencybtns=new Array();
     competencybtns.push('-');
     competencybtns.push(this.refreshBtn);
     competencybtns.push('-');
     competencybtns.push(this.addButton);
     competencybtns.push('-');
     competencybtns.push(this.editButton);
     competencybtns.push('-');
     competencybtns.push(this.deleteButton);


     if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymaster, Wtf.Perm.competencymaster.add)){
        this.addButton.show();
     }
     if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymaster, Wtf.Perm.competencymaster.edit)){
         this.editButton.show();
     }
     if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymaster, Wtf.Perm.competencymaster.deletecomp)){
         this.deleteButton.show();
     }
        this.competencyGrid=new Wtf.KwlGridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            cls:"gridWithUl",
            border:false,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addcompetency(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.performance.manage.competency.grid.msg")+"</a>")//WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addcompetency(\""+this.id+"\")'>Get started by adding a competency now...</a>")
            },
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.performance.manage.competency.grid.search.msg"),//"Search by Competency",
            loadMask:true,
            serverSideSearch:true,
            displayInfo:true,
            searchField:"cmptname",
            listeners:{
                scope:this,
                rowclick:function(competencyGrid,rowIndex,evObj)
                {
                    this.rowindex=rowIndex;
                }
            },
            tbar:competencybtns
        });

        //If you want to enable button ,if only one record selected ,otherwise disable
//        var arrId=new Array();
//        arrId.push("edit");
//        enableDisableButton(arrId,this.ds,sm);
        this.add(this.competencyGrid);
        this.sm.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(competencybtns, this.competencyGrid, [5], [7]);
        },this);
    },

    add1:function(){
        var addcomp=new Wtf.managecompetencyWindow({
                width:390,
                modal:true,
                height:265,
                title:WtfGlobal.getLocaleText("hrms.performance.competency"),//"Competency",
                resizable:false,
                layout:'fit',
                wintitle:WtfGlobal.getLocaleText("hrms.performance.add.competency"),//"Add Competency",
                editflag:false,
                datastore:this.ds,
                cleargrid:this.competencyGrid,
                action:"add"
        });
        addcomp.on('show',function(){
            addcomp.competency.focus(true,100);
        },this);
         addcomp.show();
    },
     edit1:function(){
        if(this.competencyGrid.getSelectionModel().getCount()==0||this.competencyGrid.getSelectionModel().getCount()>1){
            calMsgBoxShow(131,0);
        }
        else{
            this.editcomp=new Wtf.managecompetencyWindow({
                width:390,
                modal:true,
                height:265,
                title:WtfGlobal.getLocaleText("hrms.performance.competency"),//"Competency",
                resizable:false,
                layout:'fit',
                wintitle:WtfGlobal.getLocaleText("hrms.performance.edit.competency"),//"Edit Competency",
                editflag:true,
                compid:this.competencyGrid.getSelectionModel().getSelected().get('cmptid'),
                compname:this.competencyGrid.getSelectionModel().getSelected().get('cmptname'),
                compdesc:this.competencyGrid.getSelectionModel().getSelected().get('cmptdesc'),
                compwt:this.competencyGrid.getSelectionModel().getSelected().get('cmptwt'),
                datastore:this.ds,
                cleargrid:this.competencyGrid,
                action:"edit"
            })
            this.editcomp.on('show',function(){
                this.editcomp.competency.focus(true,100);
            },this);
            this.editcomp.show();
        }
     },
     deletecomptency:function(){
        this.delkey=this.sm.getSelections();
        this.ids=[];
        this.competencyGrid.getSelectionModel().clearSelections();
        for(var i=0;i<this.delkey.length;i++){
            this.ids.push(this.delkey[i].get('cmptid'));
            var rec=this.ds.indexOf(this.delkey[i]);
            WtfGlobal.highLightRow(this.competencyGrid,"FF0000",5, rec)
        }
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
            msg:deleteMsgBox('competency'),
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    calMsgBoxShow(201,4,true);
                    Wtf.Ajax.requestEx({
                        baseParams: {
                            flag:106
                        },
//                        url: Wtf.req.base + "hrms.jsp?flag=106",
                        url: "Performance/Competency/deleteCompetency.pf",
                        scope:this,
                        params:{
                            cmptid:this.ids
                        }
                    },
                    this,
                    function(response){
                        var params={
                            start:0,
                            limit:this.competencyGrid.pag.pageSize 
                        }
                        var res=eval('('+response+')');
                        if(res.success)
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"),res.message],0);
                        else
                            calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),res.message],2);
//                        if(res.message)
//                            calMsgBoxShow(161,2);
//                        else
//                            calMsgBoxShow(46,0);
                        WtfGlobal.delaytasks(this.ds,params);
                    },
                    function()
                    {
                        calMsgBoxShow(27,1);
                    }
                    );
                }
            }
        });
            }  
 });
        function addcompetency(Id){            
            Wtf.getCmp(Id).add1();
        }
