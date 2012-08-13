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
Wtf.WageEntryForm=function(config){    
    config.border=false;
    config.layout="fit";
    Wtf.WageEntryForm.superclass.constructor.call(this,config);
    Wtf.apply(this, config);
}
Wtf.extend(Wtf.WageEntryForm,Wtf.Panel,{
    initComponent:function(config){
        Wtf.WageEntryForm.superclass.initComponent.call(this,config);
        this.earncomp = 0;
        this.deduccomp = 0;
        this.netcomp = 0;
        this.fieldswage=[
        {
            name:'type'
        },{
            name:'cash'
        },{
            name:'code'
        },{
            name:'id'
        },{
            name:'assigned'
        },{
            name:'rate'
        },{
            name:'amount'
        },{
            name: 'depwage'
        },{
            name: 'depwageid'
        },{
            name: 'computeon'
        },{
            name: 'expr'
        },{
            name: 'comp'
        }
        ];
        this.expander = new Wtf.grid.RowExpander({
            tpl : new Wtf.XTemplate('<p>&nbsp;&nbsp;&nbsp;&nbsp;<b>'+WtfGlobal.getLocaleText("hrms.payroll.compute.on")+':</b> {[this.f(values)]}</p>',{f:function(record){
            var val = record.computeon;
            var obj = Wtf.getCmp(this.id);
            if(val=="0"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentdeductions");
            }else if(val=="1"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentearnings");
            }else if(val=="2"){
                return WtfGlobal.getLocaleText("hrms.payroll.netsalary");
            }else if(val=="3"){
                var returnstr = "";
                var expr = record.expr.split("(add)");
                obj.storewage.clearFilter(true);
                for(var ctr=0;ctr<expr.length;ctr++){
                    var subexpr = expr[ctr].split("(sub)");
                    for(var cnt=0;cnt<subexpr.length;cnt++){
                        var exprwthcoeff = subexpr[cnt].split("\*");
                        var ceff =1;
                        var wgid;
                        if(exprwthcoeff.length > 1) {
                            ceff = exprwthcoeff[0];
                            wgid = exprwthcoeff[1]
                        } else {
                            wgid = exprwthcoeff[0]
                        }
                         if(wgid==-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr += ceff + " * " + "<b>"+WtfGlobal.getLocaleText("hrms.payroll.template.basic")+"</b>"
                        }
                        var ind = obj.storewage.find('id',wgid);
                        if(ind>-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr += ceff + " * " + obj.storewage.getAt(ind).get("type");
                        }
                    }
                }
                return returnstr;
            }else{
                return "N/A";
            }
            }},this)
        });
        this.wcheckboxselection= new Wtf.grid.CheckboxSelectionModel({
            scope:this,
            singleSelect:false
        });
        this.fieldswage=Wtf.data.Record.create(this.fieldswage);
        this.readerwage=new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.fieldswage);
        this.storewage= new Wtf.data.Store({
            url: "Payroll/Wage/getWageMaster.py",
            method:'GET',
            reader: this.readerwage
        });
        this.storewageforgrid= new Wtf.data.Store({
            url: "Payroll/Wage/getDefualtWages.py",
            method:'GET',
            reader: this.readerwage
        });
        this.storewageforgrid.load({
            params:{
                type:'getDefualtWages',
                start:0,
                grouper:'addpayroll',
                limit:15
            }
        });
        this.storewageforgrid.on("load",function(){
            var totalcount = this.storewageforgrid.query("computeon", "1");
            this.earncomp = totalcount.length;
            this.addemptyrec();
        },this)
        this.ratenumberfield=new Wtf.form.NumberField({
            allowNegative:false,
            decimalPrecision:2
        });
        this.Wtypecombo= new Wtf.form.ComboBox({
            store:this.storewage,
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.WageTypeName"),
            displayField:'type',
            mode: 'local',
            valueField:'type',
            forceSelection:true,
            scope:this,
            width:180,
            height:200,
            triggerAction :'all',
            listeners:{
                scope:this,
                select:function(combo,rec,index){
                    this.insertrate=this.grid.getSelectionModel().getSelected();
                    this.insertrateRate=rec.get('cash');
                    this.insertrateCode=rec.get('code');
                    this.insertrateId=rec.get('id');
                    this.insertratetype=rec.get('type');
                    this.insertrateratetype=rec.get('rate');
                    this.insertratedepwage=rec.get('depwage');
                    this.insertratedepwageid=rec.get('depwageid');
                    this.insertratecomputeon=rec.get('computeon');
                    this.insertrateexpr=rec.get('expr');
                }
            }
        });
        this.Wtypecombo.on("expand", function(obj, rec){
            this.storewage.filter("comp",'wage');
        },this);
        this.cmwage=new Wtf.grid.ColumnModel([
            //new Wtf.grid.RowNumberer(),
//            new Wtf.grid.CheckboxSelectionModel({
//                scope:this,
//                singleSelect:false
//            }),
            this.expander,{
                header: WtfGlobal.getLocaleText("hrms.payroll.WageType"),
                dataIndex: 'type',
                editor:this.Wtypecombo,
                width:200
            },{
                header: WtfGlobal.getLocaleText("hrms.common.code"),
                dataIndex: 'code'
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.value"),
                dataIndex: 'cash',
                renderer:function(val, meta, record, rowIndex, colIndex, store){
                    if(record.data.rate=='1'){
                        return('<div align=\"right\">'+parseFloat(val).toFixed(2)+' %</div>');
                    }
                    else{
                        return('<div align=\"right\" style=\"font-family:Lucida Sans Unicode;\">'+WtfGlobal.payrollcurrencyRenderer(val)+'</div>');
                    }
                }
            },{
                header:  WtfGlobal.getLocaleText("hrms.payroll.percent.of"),
                dataIndex: 'computeon',
                align: 'center',
                sortable: true,
                renderer:this.rendererfun.createDelegate(this)
            },{
            	header:  WtfGlobal.getLocaleText("hrms.common.delete"),
                dataIndex: '',
                width:35,
                renderer:this.deleteRenderer.createDelegate(this)
            }
            ]);
        
        this.storewage.load({
            params:{
                allflag:'true',
                type:'Wages',
                cname:'aa',
                deduc:'true',
                empcont:'true',
                grouper:'addpayroll',
                //firequery:'1',
                start:0,
                limit:15
            }
        });

       
        this.sm=new Wtf.grid.CheckboxSelectionModel({
            scope:this
        });
        this.gridrowselection=function(){
        };
        this.grid = new Wtf.grid.EditorGridPanel({
            store:this.storewageforgrid,
            stripeRows: true,
            id:this.id+'addwagegrid',
            scope:this,
            sm:this.wcheckboxselection,
            height:440,
            clicksToEdit:1,
            title: WtfGlobal.getLocaleText("hrms.common.Earnings"),
            plugins:this.expander,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.Nowageassignedforcurrentsalarytemplate"))
            },
            cm:this.cmwage
        });
        this.grid.on("validateedit",function(e)
        {
            this.currentedit = e.record;
            if(this.grid.getStore().find('type',e.value)==-1){
                if(this.insertratecomputeon=="0"){//current deduction
                        var idsplit = this.id.split("addwage");
                        if(idsplit.length>0){
                            var deducobj = Wtf.getCmp(idsplit[0]+"adddeduc");
                            if(deducobj!=null){
                                if(deducobj.earncomp>0||deducobj.netcomp>0){
                                    Wtf.MessageBox.show({
                                            title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                            msg: WtfGlobal.getLocaleText("hrms.payroll.AsyouhaveincludedcomponentinDeductionWageyoucannotincludeDeductiondependentcomponentinWage"),
                                            buttons: Wtf.MessageBox.OK,
                                            animEl: 'mb9',
                                            scope:this,
                                            icon: Wtf.MessageBox.INFO
                                    });
                                    this.cancel=true;
                                    return false;
                                }else{
                        this.deduccomp++;
                        this.insertcomp(e.column);
                                }
                            }
                        }
                }else if(this.insertratecomputeon=="1"){//current earning
                    if(this.earncomp>0||this.netcomp>0){
                        Wtf.MessageBox.show({
                                title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                msg:WtfGlobal.getLocaleText("hrms.payroll.Youcannotincludetwocomponentswhichdependsoneachother"),
                                buttons: Wtf.MessageBox.OK,
                                animEl: 'mb9',
                                scope:this,
                                icon: Wtf.MessageBox.INFO
                        });
                        this.cancel=true;
                        return false;
                    }else{
                        this.earncomp++;
                        this.insertcomp(e.column);
                    }
                }else if(this.insertratecomputeon=="2"){//net salary
                    if(this.earncomp>0||this.netcomp>0){
                         Wtf.MessageBox.show({
                                title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                msg: WtfGlobal.getLocaleText("hrms.payroll.Youcannotincludetwocomponentswhichdependsoneachother"),
                                buttons: Wtf.MessageBox.OK,
                                animEl: 'mb9',
                                scope:this,
                                icon: Wtf.MessageBox.INFO
                        });
                        this.cancel=true;
                        return false;
                    }else{
                         var idsplit = this.id.split("addwage");
                        if(idsplit.length>0){
                            var deducobj = Wtf.getCmp(idsplit[0]+"adddeduc");
                            if(deducobj!=null){
                                if(deducobj.earncomp>0||deducobj.netcomp>0){
                                    Wtf.MessageBox.show({
                                            title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                            msg: WtfGlobal.getLocaleText("hrms.payroll.AsyouhaveincludedcomponentinDeductionWageyoucannotincludeDeductiondependentcomponentinWage"),
                                            buttons: Wtf.MessageBox.OK,
                                            animEl: 'mb9',
                                            scope:this,
                                            icon: Wtf.MessageBox.INFO
                                    });
                                    this.cancel=true;
                                    return false;
                                }else{
                        this.netcomp++;
                        this.insertcomp(e.column);
                    }
                            }
                        }
                    }
                }else if(this.insertratecomputeon=="3"){
                    Wtf.MessageBox.show({
                            title:  WtfGlobal.getLocaleText("hrms.common.warning"),
                            msg: WtfGlobal.getLocaleText("hrms.payroll.SomeDependenceSureYouWanttoContinue"),
                            buttons: Wtf.MessageBox.YESNO,
                            animEl: 'mb9',
                            scope:this,
                            icon: Wtf.MessageBox.INFO,
                            fn:function(btn,text){
                                if(btn=="yes"){
                                    this.insertcomp(e.column);
                                    this.storewage.clearFilter(true);
                                    this.getdep(this.insertrateexpr);
                                    this.storewage.filter("comp",'wage');
                                    
                                } else {
                                    this.grid.getStore().remove(e.record);
//                                    this.insertrate=e.record;
//                                    if(e.column==1){
//                                        this.insertrate.set('type','');
//                                    }
                                    this.cancel=true;
                                    return false;
                                }
                            }
                    });
                    this.grid.getView().refresh();
                }else{
                    this.insertcomp(e.column);
                }
               
            } else{
                this.cancel=true;
                return false;
            }
            if(e.record.data.rate=='1'){
                if(e.record.data.cash>100){
                    return false;
                }
            }
        },this);
        this.grid.on("afteredit",function(e){
            if(e.row==this.grid.getStore().getCount()-1){
                this.addemptyrec();
                this.grid.getSelectionModel().selectAll();
                this.grid.getSelectionModel().deselectRow(this.grid.getStore().getCount()-1);
            }
        },this);
        this.add(this.grid);
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.storewage.on('load',function(){
            if(this.edittemp=="yes"){
                for(this.i=0;this.i<this.grid.getStore().getCount()-1;this.i++){
                    this.rec=this.grid.getStore().getAt(this.i);
                    if(this.rec.get('assigned')=='1'){
                        this.wcheckboxselection.selectRow(this.i,true);
                    }
                }
            }
        },this);
        if(this.edittemp=="yes"){
            this.storewageforgrid.on('load',function(){
                var totalcount = this.storewageforgrid.query("computeon", "1");
                var totalcount1 = this.storewageforgrid.query("computeon", "2");
                var totalcount2 = this.storewageforgrid.query("computeon", "0");
                this.earncomp = totalcount.length;
                this.netcomp = totalcount1.length;
                this.deduccomp = totalcount2.length;
                this.grid.getSelectionModel().selectAll();
                this.grid.getSelectionModel().deselectRow(this.grid.getStore().getCount()-1);
            },this);
        }

        this.grid.on("click",function(e){
            if(e.target.className=='pwndCommon gridCancel'){
                var selected = this.grid.getSelectionModel().getSelected();
                if(this.grid.getSelectionModel().getSelections().length>0){
                    if(this.grid.getStore().indexOf(selected)!=this.grid.getStore().getCount()-1){
                        for(var n = 0 ; n < this.grid.getStore().data.length-1 ; n++){
                            var mainstring = this.grid.getStore().getAt(n).data.expr;
                            if(mainstring.indexOf(selected.data.id) != -1){
//                            if(this.grid.getStore().getAt(n).data.depwageid == selected.data.id){
                                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.CannotdeletedSomeothercomponentsaredependsonthisemployercontribution")],2);
                                return;
                            }
                        }
                        var taxstore = (this.edittemp=='yes')?Wtf.getCmp(this.parentId).taxentryform1.storetaxforgrid:Wtf.getCmp(this.parentId).taxentryform.storetaxforgrid;
                        var deducstore = (this.edittemp=='yes')?Wtf.getCmp(this.parentId).deductionentryform1.storededucforgrid:Wtf.getCmp(this.parentId).deductionentryform.storededucforgrid;
//                        for(n = 0 ; n < taxstore.data.length ; n++){
////                            if(taxstore.getAt(n).data.depwageid == selected.data.id){
//                            var mainstring = taxstore.getAt(n).data.expr;
//                            if(mainstring.indexOf(selected.data.id) != -1){
//                                calMsgBoxShow(["Warning","Can not deleted.Some other components are depends on this wage."],2);
//                                return;
//                            }
//                        }
                        for(n = 0 ; n < deducstore.data.length ; n++){
//                            if(deducstore.getAt(n).data.depwageid == selected.data.id){
                            var mainstring = deducstore.getAt(n).data.expr;
                            if(mainstring != undefined){
                                if(mainstring.indexOf(selected.data.id) != -1){
                                    calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),"Cannot deleted.Some other components are depends on this wage."],2);
                                    return;
                                }
                            }
                        }
                        this.grid.getStore().remove(this.grid.getSelectionModel().getSelected());
                        if(selected.get("computeon")==0)
                            this.deduccomp--;
                        if(selected.get("computeon")==1)
                            this.earncomp--;
                        if(selected.get("computeon")==2)
                            this.netcomp--;
                        Wtf.getCmp("editTemp").enable();
                    }
                }
            }
        },this);
        this.record=new Wtf.data.Record.create([
        {
            name:'type'
        },
        {
            name:'cash'
        },
        {
            name:'id'
        },
        {
            name:'code'
        }
        ]);
        if(this.paramstore!=1){

            this.addemptyrec();
        }
    },
    rendererfun: function(val,met,record,row,col,store,exp){
        if(record.get("rate")==0){
            return "-"
        }
            if(val=="0"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentdeductions");
            }else if(val=="1"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentearnings");
            }else if(val=="2"){
                return WtfGlobal.getLocaleText("hrms.payroll.netsalary");
            }else if(val=="3"){
                var returnstr = "";
                var expr = record.get("expr").split("(add)");
                this.storewage.clearFilter(true);
                for(var ctr=0;ctr<expr.length;ctr++){
                    var subexpr = expr[ctr].split("(sub)");
                    for(var cnt=0;cnt<subexpr.length;cnt++){
                        var exprwthcoeff = subexpr[cnt].split("\*");
                        var ceff =1;
                        var wgid;
                        if(exprwthcoeff.length > 1) {
                            ceff = exprwthcoeff[0];
                            wgid = exprwthcoeff[1]
                        } else {
                            wgid = exprwthcoeff[0]
                        }
                        if(wgid==-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr += ceff + " * " + "<b>"+WtfGlobal.getLocaleText("hrms.payroll.template.basic")+"</b>"
                        }
                        var ind = this.storewage.find('id',wgid);
                        if(ind>-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr +=  ceff + " * " + this.storewage.getAt(ind).get("type");
                        }
                    }
                }
                return returnstr;
            }
     },
    insertcomp: function(col){
        this.insertrate=this.currentedit;
        if(col==1){
            this.insertrate.beginEdit();
            this.insertrate.set('cash',this.insertrateRate);
            this.insertrate.set('code',this.insertrateCode);
            this.insertrate.set('id',this.insertrateId);
            this.insertrate.set('rate',this.insertrateratetype);
            this.insertrate.set('depwage',this.insertratedepwage);
            this.insertrate.set('type',this.insertratetype);
            this.insertrate.set('depwageid',this.insertratedepwageid);
            this.insertrate.set('computeon',this.insertratecomputeon);
            this.insertrate.set('expr',this.insertrateexpr);
            this.insertrate.endEdit();
        }
    },
    getdep: function(exprstr){
        var expr = exprstr.split("(add)");
        for(var ctr=0;ctr<expr.length;ctr++){
            var subexpr = expr[ctr].split("(sub)");
            for(var cnt=0;cnt<subexpr.length;cnt++){
                var exprwthcoeff = subexpr[cnt].split("\*");
                var wgid;
                if(exprwthcoeff.length > 1) {
                    wgid = exprwthcoeff[1]
                } else {
                    wgid = exprwthcoeff[0]
                }
                var index = this.grid.getStore().find('id',wgid);
                if(index==-1 && subexpr[cnt]!=""){
                     var ind = this.storewage.find('id',wgid);
                      if(ind>-1){
                          var idsplit;
                          var ind1;
                          var comborec = this.storewage.getAt(ind);
                          if(comborec.get("comp")=="wage")
                                this.grid.getStore().insert(0,comborec);
                          else if (comborec.get("comp")=="empcontrib"){
                                idsplit = this.id.split("addwage");
                                if(idsplit.length>0){
                                    var empContObj = Wtf.getCmp(idsplit[0]+"addempcontrib");
                                    if(empContObj!=null){
                                        ind1 = empContObj.storewageforgrid.find('id',wgid);
                                        if(ind1==-1){
                                            empContObj.addrecord(comborec);
                                        }
                                    }
                                }
                          } else {
                                idsplit = this.id.split("addwage");
                                if(idsplit.length>0){
                                    var deducobj = Wtf.getCmp(idsplit[0]+"adddeduc");
                                    if(deducobj!=null){
                                        ind1 = deducobj.storededucforgrid.find('id',wgid);
                                        if(ind1==-1){
                                            deducobj.addrecord(comborec);
                                        }
                                    }
                                }
                          }
                          if(comborec.get("computeon")=="3")
                            this.getdep(comborec.get("expr"));
                      }
                }
            }
        }
    },
    addemptyrec:function(){
        this.grid.getStore().add(new this.record({
            type:'',
            cash:'',
            id:'-1',
            code:''
        }));
    },
    deleteRenderer:function(a,b,c,d){
        if(c.data.id !='-1')
            return "<div><div class='pwndCommon gridCancel' style='cursor:pointer' wtf:qtip="+WtfGlobal.getLocaleText("hrms.common.DeleteRecord")+"></div></div>";
    },
    addrecord: function(newrec){
        this.grid.getStore().insert(0,newrec);
    }
});
