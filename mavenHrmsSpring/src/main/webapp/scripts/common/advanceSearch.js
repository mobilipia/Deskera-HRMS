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

Wtf.advancedSearchComponent = function(config){
    Wtf.apply(this, config);

    this.events = {
        "filterStore": true,
        "saveStore": true,
        "reloadgridStore": true,
        "clearStoreFilter": true
    };

    this.RecruiterRecord = Wtf.data.Record.create([{
        name:'fname'
    },
    {
        name:'rid'
    },
    {
        name:'lname'
    },
    {
        name:'name'
    }]);

    this.RecruiterReader = new Wtf.data.KwlJsonReader({
        root:"data",
        totalProperty:"count"
    },this.RecruiterRecord);

    this.RecruiterStore= new Wtf.data.Store({
        url: "Rec/Job/getRecruiter.rec",
        reader:this.RecruiterReader,
        baseParams:{
            flag:126,
            firequery: '1'
        }
    });
    this.RecruiterStore.load();
    
    this.combovalArr=[];

    var mainArray=new Array();

    for (i=0;i<this.cm.length;i++) {
        var tmpArray=new Array();
        if(this.cm[i].dbname && (this.cm[i].hidden==undefined || this.cm[i].hidden==false)) {
            var header=headerCheck(WtfGlobal.HTMLStripper(this.cm[i].header));
            tmpArray.push(header);
            tmpArray.push(this.cm[i].dbname);
            tmpArray.push(this.cm[i].xtype);
            tmpArray.push(this.cm[i].cname);
            tmpArray.push(this.cm[i].iscustom);
            mainArray.push(tmpArray)
        }
    }

    var myData = mainArray;

    this.combostore = new Wtf.data.SimpleStore({
        fields: [
        {
            name: 'header'
        },

        {
            name: 'name'
        },
        {
            name: 'xtype'
        },
        {
            name: 'cname'
        },
        {
            name : 'iscustom'
        }
        ]
    });
    this.combostore.loadData(myData);

    this.columnCombo = new Wtf.form.ComboBox({
        store : this.combostore,
        typeAhead : true,
        displayField:'header',
        valueField : 'name',
        triggerAction: 'all',
        emptyText : WtfGlobal.getLocaleText("hrms.common.Selectanoption"),
        mode:'local'
    })

    this.columnCombo.on("select",this.displayField,this);


    this.cm=new Wtf.grid.ColumnModel([{
        header: WtfGlobal.getLocaleText("hrms.common.Column"),
        dataIndex:'column'
    },{
        header: WtfGlobal.getLocaleText("hrms.common.Search1Text"),
        dataIndex:'searchText',
        hidden:true

    },
    {
        header: WtfGlobal.getLocaleText("hrms.common.SearchText"),
        dataIndex:'id'
    },{
        header: WtfGlobal.getLocaleText("hrms.common.delete"),
        renderer : function(val, cell, row, rowIndex, colIndex, ds) {
            return "<div class='pwndCommon deletecolIcon'>&nbsp;</div>";
        }
    }
    ]);

    this.searchRecord = Wtf.data.Record.create([{
        name: 'column'
    },{
        name: 'searchText'
    },{
        name: 'dbname'
    },
    {
        name: 'id'
    },{
        name: 'xtype'
    },{
        name: 'iscustom'
    },{
        name: 'stateid'
    }

    ]);

    this.GridJsonReader = new Wtf.data.KwlJsonReader({
        root: "data",
        totalProperty: 'count'
    }, this.searchRecord);

    this.searchStore = new Wtf.data.Store({
        reader: this.GridJsonReader,
        url:"Common/getSavedSearch.common"
    });

    if(this.searchid!=undefined){ // For Saved Searches
        this.searchStore.on("load",this.savedSearchStoreload,this);
    }
    
    this.on("cellclick", this.deleteFilter, this);

    Wtf.advancedSearchComponent.superclass.constructor.call(this, {

        region :'north',
        height:150,
        hidden:true,
        store: this.searchStore,
        cm:this.cm,
        stripeRows: true,
        autoScroll : true,
        border:false,
        clicksToEdit:1,
        viewConfig: {
            forceFit:true
        },

        tbar: [this.columnCombo,'-',this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("hrms.common.SearchText")+": "), this.searchText = new Wtf.ux.TextField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.NewMasterRecord"),
            anchor: '95%',
            maxLength: 100,
            width:125
        }),this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.activityList.add"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.Addatermtosearch")
            },
            handler: this.addSearchFilter,
            scope: this,
            iconCls : 'pwndExport addfilter'
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.common.search"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.add.terms.search")
            },
            handler: this.doSearch,
            scope:this,
            disabled:true,
            iconCls : 'pwnd searchtabpane'
        }),
        
        this.cancel = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.common.Close"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.Clearsearchtermsandcloseadvancedsearch")
            },
            handler: this.cancelSearch,
            scope:this,
            iconCls:'pwndCommon cancelbuttonIcon'
        }),this.save = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.common.remember.search"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.Savesearchterms")
            },
            handler: this.saveSearchHandler,
            scope:this,
            iconCls:'pwndCommon submitbuttonIcon'
        }),this.saveSearchName = new Wtf.ux.TextField({
            anchor: '95%',
            maxLength: 100,
            width:125
        })]
    });

}

Wtf.extend(Wtf.advancedSearchComponent, Wtf.grid.EditorGridPanel, {
    addSearchFilter:function(){
        var column =this.columnCombo.getValue();
        var do1=0;
        var searchText;
        if (column != "" &&  (this.searchText.getValue() !="" || (this.searchText.getXType()=="numberfield" && this.searchText.getValue()==0))){
            //            searchText=this.searchText.getValue();
            if(this.searchText.getXType()=="datefield") {
                searchText=this.searchText.getValue().format('m/d/Y')+"##"+this.searchtillText.getValue().format('m/d/Y');
                this.searchText1="between "+this.searchText.getRawValue()+" and "+this.searchtillText.getRawValue();
            } else{
                searchText=this.searchText.getValue();
                this.searchText1=this.searchText.getRawValue();
            }
            //            this.searchText1=this.searchText.getRawValue();
            this.combovalArr.push(this.searchText1);
            this.columnText="";
            this.iscustom = false;
            if(searchText != "" || (this.searchText.getXType()=="numberfield" && this.searchText.getValue()==0)) {
                for(var i=0;i<this.combostore.getCount();i++) {
                    if(this.combostore.getAt(i).get("name")== column) {
                        this.columnText=this.combostore.getAt(i).get("header");
                        this.iscustom = this.combostore.getAt(i).get("iscustom");
                        do1=1;
                    }
                }
                if(do1==1) {
                    this.search.enable();
                    this.search.setTooltip(WtfGlobal.getLocaleText("hrms.common.Searchonmultipleterms"));
                    var searchRecord = new this.searchRecord({
                        column: this.columnText,
                        searchText: searchText,
                        dbname:column,
                        id:this.searchText1,
                        xtype:this.searchText.getXType(),
                        iscustom : this.iscustom
                    });

                    var index=this.searchStore.find('column',this.columnText);
                    if (index == -1 ) {
                        this.searchStore.add(searchRecord);
                    } else {
                    	this.combovalArr.splice(index, 1);
                        this.searchStore.remove(this.searchStore.getAt(index ) );
                        this.searchStore.insert(index,searchRecord);
                    }
                }
            }
        } else {
            if(column == "") {
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.Selectacolumntosearch")],2);
            } else if(this.searchText.getValue() =="") {
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.PleasespecifyaSearchText")],2);
            }
        }
        this.searchText.setValue("");
        if(this.searchtillText!=null){
            this.searchtillText.setValue("");
        }
    },

    savedSearchStoreload:function(store,record,options){
        if ( this.searchStore.getCount() > 0 ){
            var filterJson=[];
             var i=0;

            this.searchStore.each(function(filterRecord){

                var recdata = filterRecord.data;
                var searchText=recdata.searchText;
                searchText = WtfGlobal.replaceAll(searchText, "\\\\" , "\\\\");
                var xtype=filterRecord.data.xtype;

                filterJson.push({
                    iscustom:recdata.iscustom,
                    column : recdata.dbname,
                    searchText:searchText,
                    columnheader:encodeURIComponent(recdata.column),
                    search:searchText,
                    combosearch:searchText,
                    xtype:xtype
                });

                i++;
            },this);

            filterJson = {
                root:filterJson
            }

            this.fireEvent("reloadgridStore", Wtf.encode(filterJson));
            this.search.enable();

        } else {
            this.search.disable();
        }
    },
    
    getJsonofStore : function(){
       var filterJson=[];
       var i=0;

       this.searchStore.each(function(filterRecord){

            var recdata = filterRecord.data;
            var searchText=recdata.searchText+"";
            var xtype=recdata.xtype;

            if (xtype == 'datefield' || xtype =='Date' ){
                 if(recdata.searchText && recdata.searchText.format)
                        searchText=WtfGlobal.convertToOnlyDate(recdata.searchText);
            }
            searchText = WtfGlobal.replaceAll(searchText, "\\\\" , "\\\\");
            if(this.combovalArr[i])
                this.combovalArr[i] = WtfGlobal.replaceAll(this.combovalArr[i], "\\\\" , "\\\\");

            var dbName = recdata.dbname;
            if(recdata.iscustom==true){
                dbName = "cfd.col"+recdata.dbname;
            }
            filterJson.push({
                column:encodeURIComponent(recdata.column),
                refdbname:recdata.refdbname,
                xfield:recdata.xfield,
                iscustom:recdata.iscustom,
                fieldtype:recdata.fieldtype,
                searchText:searchText,
                dbname : dbName,
                id : recdata.id,
                xtype:xtype,
                combosearch:this.combovalArr[i]

            });
            i++;
        },this);

        filterJson = {
            data:filterJson
        }

        return Wtf.encode(filterJson);
    },
    
    saveSearchHandler:function(){
        if ( this.searchStore.getCount() > 0 ){

            this.saveSearchName.setValue( WtfGlobal.HTMLStripper(this.saveSearchName.getValue()));

            if(this.saveSearchName.getValue()!="" && this.saveSearchName.validate()){

                var filterJson= this.getJsonofStore();
                var saveSearchName = this.saveSearchName.getValue();
                
                this.fireEvent("saveStore",filterJson, saveSearchName);
            } else {
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.please.enter.valid.search.add")],0);
            }

        }else{
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.Alert"), WtfGlobal.getLocaleText("hrms.common.Selectsearchtextandsavesearchresults")],0);

        }
    },

    doSearch:function(){
        if ( this.searchStore.getCount() > 0 ){
            var filterJson='{"root":[';
            var i=0;

            this.searchStore.each(function(filterRecord){
                var searchText=filterRecord.data.searchText;
                var value="";
                value=filterRecord.data.searchText;
                var xtype=filterRecord.data.xtype;
                //                if (xtype == 'combo' || xtype =='Combobox' || xtype =='userscombo' ){
                //                    searchText=filterRecord.data.comboid;
                //                }
                if(filterRecord.data.iscustom==undefined || filterRecord.data.iscustom==""){
                   filterRecord.data.iscustom = false;
                    filterJson+='{ "iscustom":'+filterRecord.data.iscustom+',"column":"'+filterRecord.data.dbname+'","searchText":"'+searchText+'","columnheader":"'+encodeURIComponent(filterRecord.data.column)+'","search":"'+value+'","combosearch":"'+this.combovalArr[i]+'","xtype":"'+xtype+'"},';
                }else{
                   var dbName = filterRecord.data.dbname;
                   
                   if(this.searchid ==undefined){
                        dbName = "cfd.col"+dbName
                   }
                   filterJson+='{ "iscustom":'+filterRecord.data.iscustom+',"column":"'+dbName+'","searchText":"'+searchText+'","columnheader":"'+encodeURIComponent(filterRecord.data.column)+'","search":"'+value+'","combosearch":"'+this.combovalArr[i]+'","xtype":"'+xtype+'"},';
                }

                i++;
            },this);

            filterJson=filterJson.substring(0,filterJson.length-1);
            filterJson+="]}";
            this.fireEvent("filterStore",filterJson);

        }else{
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.common.Selectsearchtextandaddgetsearchresults")],2);
            this.fireEvent("filterStore","");
        }
    },
    cancelSearch:function(){
        this.columnCombo.setValue("");
        var searchXtype = this.searchText.getXType();
        if(searchXtype=='combo')
            this.columnCombo.fireEvent("select",undefined,'');
        (this.toDate != undefined)?this.toDate.setValue(""):null;
        (this.fromDate != undefined)?this.fromDate.setValue(""):null;
        this.searchStore.removeAll();
        this.search.disable();
        this.combovalArr=[];
        this.fireEvent("clearStoreFilter");
    },

    deleteFilter:function(gd, ri, ci, e) {
        var event = e;
        if(ci==3) {
            this.searchStore.remove(this.searchStore.getAt(ri));
            if(this.searchStore.getCount()==0) {
                this.search.disable();
                this.search.setTooltip(WtfGlobal.getLocaleText("hrms.common.add.terms.search"));
            }
            this.combovalArr.splice(ri,1);
        }
    },

    displayField:function(combo,record){
        if(record == '')
            var recXtype = "textfield";
        else
            recXtype=record.get('xtype');
        if (recXtype == "None"){
            record.set('xtype','textfield');
        }

        if (this.text){
            this.text.destroy();
        }
        if (this.text1||this.searchtillText) {
            this.text1.destroy();
            this.searchtillText.destroy();
        }

        this.searchText.destroy();
        this.add.destroy();
        this.search.destroy();
        this.cancel.destroy();
        this.save.destroy();
        this.saveSearchName.destroy();
        this.doLayout();



        if (recXtype == "textfield" || recXtype == 'Text' || recXtype =='textarea'){
            this.searchText = new Wtf.ux.TextField({
                anchor: '95%',
                maxLength: 100,
                width:125
            });
        }

        if (recXtype == "numberfield" || recXtype == 'Number(Integer)' || recXtype == 'Number(Float)'){
            this.searchText = new Wtf.form.NumberField({
                anchor: '95%',
                maxLength: 100,
                width:125
            });
        }

        if(recXtype == "time" || recXtype == "timefield"){
            this.searchText=new Wtf.form.TimeField({
                width:125,
                minValue:new Date(new Date().format("M d, Y")+" 8:00:00 AM"),
                maxValue:new Date(new Date().add(Date.DAY, 1).format("M d, Y")+" 7:45:00 AM")
            });
        }

        if(recXtype == "date" || recXtype == "datefield"){
            this.searchText=new Wtf.form.DateField({
                width:125,
                format:'Y-m-d'
            });
            this.searchtillText=new Wtf.form.DateField({
                width:125,
                format:'Y-m-d'
            });
        }



        if (recXtype == "combo" || recXtype == "Combobox" ){


            //                 var comboReader = new Wtf.data.Record.create([
            //                {
            //                    name: 'id',
            //                    type: 'string'
            //                },
            //                {
            //                    name: 'name',
            //                    type: 'string'
            //                }
            //                ]);

            var comboname="";
            var flag="";
            var comboboxname=record.get('cname');
            var valuefield;

            if(comboboxname=='gender')
            {
                this.comboStore = new Wtf.data.SimpleStore({
                    fields:['id','name'],
                    data: [
                    ['1', WtfGlobal.getLocaleText("hrms.common.Male")],
                    ['2', WtfGlobal.getLocaleText("hrms.common.Female")]
                    ]
                });
                valuefield="name";
            } else if(comboboxname=='weeklyoff') {
                this.comboStore = new Wtf.data.SimpleStore({
                    fields:['id','name'],
                    data: [
                    ['1','sunday'],
                    ['2','monday'],
                    ['3','tuesday'],
                    ['4','wednesday'],
                    ['5','thursday'],
                    ['6','friday'],
                    ['7','saturday']
                    ]
                });
                valuefield="name";

            } else if(comboboxname=='marital') {
                this.comboStore = new Wtf.data.SimpleStore({
                    fields:['id','name'],
                    data: [
                    ['1', WtfGlobal.getLocaleText("hrms.common.Single")],
                    ['2', WtfGlobal.getLocaleText("hrms.common.Married")]
                    ]
                });
                valuefield="id";
            } else if(comboboxname=='country') {
                if(!Wtf.StoreMgr.containsKey("country")){
                    Wtf.countryStore.load();
                    Wtf.StoreMgr.add("country",Wtf.countryStore)
                }
                this.comboStore = Wtf.countryStore;
                valuefield="id";
            } else if(comboboxname=='qualification') {
                if(!Wtf.StoreMgr.containsKey("qua")){
                    Wtf.quaStore.load();
                    Wtf.StoreMgr.add("qua",Wtf.quaStore)
                }
                this.comboStore = Wtf.quaStore;
                valuefield="name";
            } else if(comboboxname=='department') {
                if(!Wtf.StoreMgr.containsKey("dep")){
                    Wtf.depStore.load();
                    Wtf.StoreMgr.add("dep",Wtf.depStore)
                }
                this.comboStore = Wtf.depStore;
                valuefield="id";
            } else if(comboboxname=='designation') {
                if(!Wtf.StoreMgr.containsKey("desig")){
                    Wtf.desigStore.load();
                    Wtf.StoreMgr.add("desig",Wtf.desigStore)
                }
                this.comboStore = Wtf.desigStore;
                valuefield="id";
            } else if(comboboxname=='manager') {
            	if(!Wtf.StoreMgr.containsKey("manager")){
            		Wtf.managerStore.load();
            		Wtf.StoreMgr.add("manager",Wtf.managerStore);
            	}
                this.comboStore = Wtf.managerStore;
                valuefield="userid";
            } else if(comboboxname=='recruiter') {
            	this.comboStore = this.RecruiterStore;
                valuefield="rid";
            } else if(comboboxname=='status') {
                this.comboStore = new Wtf.data.SimpleStore({
                    fields:['id','name'],
                    data: [
                    ['1', WtfGlobal.getLocaleText("hrms.recruitment.pending")],
                    ['2', WtfGlobal.getLocaleText("hrms.recruitment.shortlisted")],
                    ['3', WtfGlobal.getLocaleText("hrms.recruitment.in.process")],
                    ['4', WtfGlobal.getLocaleText("hrms.recruitment.on.hold")],
                    ['5', WtfGlobal.getLocaleText("hrms.common.All")]
                    ]
                });
                valuefield="id";
            } else if(comboboxname=='applicant') {
                this.comboStore = new Wtf.data.SimpleStore({
                    fields:['id','name'],
                    data: [
                    ['1', WtfGlobal.getLocaleText("hrms.common.Internal")],
                    ['2', WtfGlobal.getLocaleText("hrms.common.External")],
                    ['3', WtfGlobal.getLocaleText("hrms.common.All")]
                    ]
                });
                valuefield="id";
            } else if(comboboxname=='jobstatus') {
            	this.jobstatusdata=[['0','Open'],['2','Expired'],['3','Filled'],['4','All']];
                this.comboStore = new Wtf.data.SimpleStore({
                    fields: ['id','name'],
                    data :this.jobstatusdata
                });
                valuefield="id";
            } else if(comboboxname=='jobtype') {
            	this.jobtypedata=[['1', WtfGlobal.getLocaleText("hrms.common.Both")],['2', WtfGlobal.getLocaleText("hrms.common.External")],['3', WtfGlobal.getLocaleText("hrms.common.Internal")],['4', WtfGlobal.getLocaleText("hrms.common.All")]];
                this.comboStore = new Wtf.data.SimpleStore({
                    fields: ['id','name'],
                    data :this.jobtypedata
                });
                valuefield="id";
            }
                
                
            //                this.comboStore = new Wtf.data.Store({
            //                    reader: new Wtf.data.KwlJsonReader({
            //                        root:'data'
            //                    }, comboReader),
            //                    url: Wtf.req.base + 'crm.jsp?flag='+flag+'&comboname='+comboname
            //
            //                });

            this.displayField=combo.getValue();

            //            this.comboStore.load();

            this.searchText = new Wtf.form.ComboBox({
                valueField: valuefield,
                // displayField: this.displayField,
                displayField: 'name',
                store: this.comboStore,
                typeAhead:true,
                forceSelection :true,
                anchor: '95%',
                mode: 'local',
                triggerAction: 'all',
                selectOnFocus: true,
                emptyText: WtfGlobal.getLocaleText("hrms.common.Selectanoption"),
                width:125
            });
        }



        if (recXtype !="datefield" && recXtype !="Date"){
            this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("hrms.common.SearchText")+": ");
            this.getTopToolbar().add(this.text);
            this.getTopToolbar().add(this.searchText);
        } else {
            this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("hrms.common.SearchBetween")+": ");
            this.getTopToolbar().add(this.text);
            this.getTopToolbar().add(this.searchText);
            this.text1=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("hrms.common.and")+": ");
            this.getTopToolbar().add(this.text1);
            this.getTopToolbar().add(this.searchtillText);
        }
        this.getTopToolbar().addButton([this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.activityList.add"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.Addatermtosearch")
            },
            handler: this.addSearchFilter,
            scope: this,
            iconCls : 'pwndExport addfilter'
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.common.search"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.add.terms.search")
            },
            handler: this.doSearch,
            disabled:true,
            scope:this,
            iconCls : 'pwnd searchtabpane'
        }),
        this.cancel = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.common.Close"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.Clearsearchtermsandcloseadvancedsearch")
            },
            handler: this.cancelSearch,
            scope:this,
            iconCls:'pwndCommon cancelbuttonIcon'
        })]);

        this.save = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("hrms.common.remember.search"),
            tooltip: {
                text: WtfGlobal.getLocaleText("hrms.common.Savesearchterms")
            },
            handler: this.saveSearchHandler,
            scope:this,
            iconCls:'pwndCommon submitbuttonIcon'
        });
        this.saveSearchName = new Wtf.ux.TextField({
            anchor: '95%',
            maxLength: 100,
            allowBlank:false,
            width:125
        });

        this.getTopToolbar().add(this.save);
        this.getTopToolbar().add(this.saveSearchName);

        this.add.getEl().dom.style.paddingLeft="4px";
        this.doLayout();

        if ( this.searchStore.getCount() > 0 ){
            this.search.enable();
        } else {
            this.search.disable();
        }
    }

});

