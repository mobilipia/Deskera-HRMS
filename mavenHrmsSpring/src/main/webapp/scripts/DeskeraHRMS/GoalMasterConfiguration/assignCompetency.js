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
Wtf.assignCompetency = function(config){
    Wtf.assignCompetency.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.assignCompetency, Wtf.Panel, {
    initComponent: function() {
        Wtf.assignCompetency.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.assignCompetency.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
        {
            "name":"mcompid"
        },
        {
            "name":"cmptid"
        },
        {
            "name":"desname"
        },
        {
            "name":"cmptname"
        },
        {
            "name":"cmptdesc"
        },
        {
            "name":"cmptwt"
        },
        {
            "name":"designid"
        }

        ]);

        this.ds = new Wtf.data.GroupingStore({
            baseParams: {
                flag: 105

            },
//            url: Wtf.req.base + "hrms.jsp",
            url: "Performance/Competency/getCompAndDesig.pf",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },
            this.record
            ),
            sortInfo:{
                field: 'desname',
                direction: "ASC"
            },
            groupField:'desname'
        });
        calMsgBoxShow(202,4,true);
        this.ds.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.ds.on("load",function(){
        if(msgFlag==1)
            WtfGlobal.closeProgressbar();
        },this);

         this.sm= new Wtf.grid.CheckboxSelectionModel({
             singleSelect:true
            });
        this.cm = new Wtf.grid.ColumnModel([
            {
                dataIndex: 'cmptid',
                hidden: true,
                hideable:false
            },
            {
                header: WtfGlobal.getLocaleText("hrms.common.designation"),//"Designation",
                dataIndex:"desname"
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.all.competencies"),//"All Competencies",
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
            },
            {
                header: WtfGlobal.getLocaleText("hrms.performance.weightage"),//"Weightage",
                dataIndex: 'cmptwt',
                sortable: true,
                renderer:WtfGlobal.numericRenderer
            }

            ]);
        
        this.refreshBtn = new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.ds.load({params:{start:0,limit:this.assigncompetencyGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.assigncompetencyGrid.id).setValue("");
        	}
     	});

        this.competencyButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.assign.competency"),//'Assign Competency',
            tooltip:WtfGlobal.getLocaleText("hrms.performance.assign.competency.tooltip"),//"Select a designation from the list and assign competencies required for that job position.",
            iconCls:getButtonIconCls(Wtf.btype.assignbutton),
            minWidth:115,
            id:"asscomp",
            handler:this.add1,            
            scope:this
        });

        this.delcompetencyButton= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.performance.delete.competency"),//'Delete Competency',
            iconCls:getButtonIconCls(Wtf.btype.deletebutton),
            minWidth:115,
            id:"delcomp",
            handler:this.del1,
            scope:this
        });
  var compmntbtns=new Array();
  if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymanagement, Wtf.Perm.competencymanagement.assign)){
	  compmntbtns.push('-');
  	  compmntbtns.push(this.refreshBtn);
      compmntbtns.push('-');
      compmntbtns.push(this.competencyButton);
  }
//  if(!WtfGlobal.EnableDisable(Wtf.UPerm.competencymanagement, Wtf.Perm.competencymanagement.deletemnt)){
//      compmntbtns.push('-');
//      compmntbtns.push(this.delcompetencyButton);
//  }

        this.assigncompetencyGrid=new Wtf.KwlGridPanel({
            id:'CompetencyManagementgrid',
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            cls:"gridWithUl",
            border:false,
            view: new Wtf.grid.GroupingView({
                forceFit: true,
                showGroupName:false,
                enableGroupingMenu: false,
                hideGroupedColumn: true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:assigncompetency(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.performance.started.assigning.competency.msg")+"</a>")//WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:assigncompetency(\""+this.id+"\")'>Get started by assigning a competency now...</a>")

            }),
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.performance.competency.grid.msg"),//"Search by Designation, Competency ",
            displayInfo:true,
            loadMask:true,
            serverSideSearch:true,
            searchField:"desname",
            animCollapse: false,
            tbar:compmntbtns
        });
       
        var arrId=new Array();
        arrId.push("asscomp");      
        this.add(this.assigncompetencyGrid);

    },
  
    add1:function(){
        var designame="";
        var desigid="";
        if(this.sm.hasSelection()){
          designame = this.sm.getSelected().get("desname");
          desigid = this.sm.getSelected().get("designid");
        }
        this.windowpos= new Wtf.competencyWindow1({
            width:700,
            modal:true,
            height:600,
            title:WtfGlobal.getLocaleText("hrms.performance.assign.competency"),//"Assign Competency",
            resizable:false,
            layout:'fit',          
            desname:designame,
            desigid:desigid
        });
        this.windowpos.on('savedata', function(){
            this.ds.load({
                params:{
                    start:this.assigncompetencyGrid.pag.cursor,
                    limit:this.assigncompetencyGrid.pag.pageSize,
                    ss:Wtf.getCmp("Quick"+this.assigncompetencyGrid.id).getValue()
                }
            });
        }, this);
        this.windowpos.show();
    }, 
    del1:function(){
        if(this.assigncompetencyGrid.getSelectionModel().getCount()==0){
          calMsgBoxShow(42,0);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:deleteMsgBox('competency'),
                icon:Wtf.MessageBox.QUESTION,
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.statrec=this.assigncompetencyGrid.getSelectionModel().getSelections();
                        this.apparr=[];
                        for(var i=0;i<this.statrec.length;i++){
                            this.apparr.push(this.statrec[i].get('mcompid'));
                        }
                        calMsgBoxShow(201,4,true);
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.base + 'hrms.jsp',
                            params:  {
                                flag:117,
                                appid:this.apparr
                            }
                        },
                        this,
                        function(){
                           calMsgBoxShow(62,0);
                            this.ds.load({
                                params:{
                                    start:0,
                                    limit:this.assigncompetencyGrid.pag.pageSize,
                                    ss:Wtf.getCmp("Quick"+this.assigncompetencyGrid.id).getValue()
                                }
                            });
                        },
                        function(){
                           calMsgBoxShow(27,0);
                        })
                    }
                }
            })
        }
    }
});
function assigncompetency(Id){
    Wtf.getCmp(Id).add1();
}
