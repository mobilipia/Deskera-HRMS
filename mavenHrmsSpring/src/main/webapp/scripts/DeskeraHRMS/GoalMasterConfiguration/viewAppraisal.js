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
Wtf.viewAppraisal=function(config){
    Wtf.viewAppraisal.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.viewAppraisal,Wtf.Panel,{
    initComponent:function(config){
        Wtf.viewAppraisal.superclass.initComponent.call(this,config);
    },
    onRender: function(config) {
        Wtf.viewAppraisal.superclass.onRender.call(this, config);
        this.appraisalRecord=Wtf.data.Record.create([{
            name:"type"
        },{
            name:"sdate",
            type:'date'
        },{
            name:"edate",
            type:'date'
        },{
            name:"status"
        },{ 
            name:'manager'
        },{
            name:'cyclesdate',
            type:'date'
        },{
            name:'cycleedate',
            type:'date'
        }]);

        this.appraisalReader = new Wtf.data.KwlJsonReader1({
            root:"data",
            totalProperty:"count"
        },this.appraisalRecord);

        this.appraisalstore= new Wtf.data.Store({
            url: Wtf.req.base + "hrms.jsp",
            reader:this.appraisalReader,
            baseParams:{
                flag:162,
                employeeid:this.userid
            }
        });
        calMsgBoxShow(202,4,true);
        this.appraisalstore.load();
        this.appraisalstore.on("load",function(){WtfGlobal.closeProgressbar()},this);


        this.cm1=new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),{
                header:WtfGlobal.getLocaleText("hrms.performance.appraisal.cycle"),
                dataIndex:"type",
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.common.manager"),
                dataIndex:"manager",
                sortable: true
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.cycle.start.date"),
                dataIndex:"cyclesdate",
                sortable: true,
                renderer : WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.cycle.end.date"),
                dataIndex:"cycleedate",
                sortable: true,
                renderer : WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.submission.start.date"),
                dataIndex:"sdate",
                sortable: true,
                renderer : WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("hrms.performance.submission.end.date"),
                dataIndex:"edate",
                sortable: true,
                renderer : WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("hrms.common.status"),
                dataIndex:"status",
                sortable: true,
                renderer : function(val) {
                    if(val=='submitted')
                        return '<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.common.Updates.Submitted")+'</FONT>'
                    else if(val=='initiated')
                        return '<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.performance.initiated")+'</FONT>'
                    else
                        return '<FONT COLOR="red">'+val+'</FONT>'
                }
            }]);

        this.appraisalGrid = new Wtf.KwlGridPanel({
            store: this.appraisalstore,
            autoScroll :true,
            border:true,
            scope:this,
            loadMask:true,
            clicksToEdit :1,
            searchField:'status',
            searchEmptyText:WtfGlobal.getLocaleText("hrms.performance.search.status"),
            displayInfo:true,
            viewConfig: {
                forceFit: true,
                emptyText:Wtf.gridEmptytext
            },
            cm: this.cm1
        });
        this.add(this.appraisalGrid);
    }
});

