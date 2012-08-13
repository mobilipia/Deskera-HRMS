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


Wtf.WtfCustomPanel = function(config){
    Wtf.WtfCustomPanel.superclass.constructor.call(this, config);
    this.oldinnerHtml  ="";
    this.o1  ="";
    this.o2  ="";
    this.o3  ="";
    this.cmbcnt=1;
    this.config1.emptyText='No result found';
    this.config1.tableClassName='datagridonDB';
    this.config1.tableHeader='';
}
Wtf.extend(Wtf.WtfCustomPanel, Wtf.Panel,{
    onRender: function(config){
       Wtf.WtfCustomPanel.superclass.onRender.call(this, config);
       this.header.replaceClass('x-panel-header','portlet-panel-header');
       for(var count = 0;count<this.config1.length;count++){
               this.count = count;
               this.newObj = this.config1[count];
               this.callRequest();
       }
    },

    writeTemplateToBody:function(innerHTML,lib,ss,pgsize,pager){
       var temp1 = innerHTML ;
       var temp3 ='';
    
       if(this.config1.length == 4 ) { // for Knowledge Compass, template changes
           
           temp3 = this.o1 +this.o2 + this.o3 + temp1;
           temp3 = ''
                            +'<div style="background-color:#DFE8F6;padding:7px;float:left;width:99%">'
                            //+ this.o1 +this.o2 + this.o3 +"<div class='search_div' style='margin-right:43px !important;margin-top:-43px !important;padding:2px !important;' onclick='showResults()'>Search</div>"+temp1
                            + this.o1 +this.o2 + this.o3 +"</td><td><div class='search_div' onclick='showResults(\""+this.id+"\")'>Search</div></td></tr></tbody></table>"+temp1
                                +'</div>';
            if(!this.o1) {    // saves the previous templates of combos
               this.o1=lib;
            }else if(!this.o2){
               this.o2=lib;
            } else if(!this.o3){
              this.o3=lib;
            }
       } else
       if(this.config1.length >1)
           {
               var temp2 = this.oldinnerHtml;
               temp3 = '<div style="background-color:#DFE8F6;padding:7px;float:left;width:97%">'
                            +((this.newObj.isSearch)?this.addSearchBar1():"")
                            +'<div style="background-color:#ffffff;padding:1%;float:left;width:95.5%">'
                            + temp2 + lib
                            +((this.newObj.isPaging)?this.changePagingBar(pager):"")
                            +'</div>'
                            +'</div>';
       }else{
           temp3 = temp1;
       }
       var temp=new Wtf.Template(temp3);
       this.oldinnerHtml += lib;
       temp.overwrite(this.body);
    },

    addSearchBar1:function(searchss){
        var valueStr="";
        if(searchss){
            valueStr = "value = "+searchss;
        }
        var a1 =this.id;
        return('<div style="height:24px;width:97.5%;background-image:url(images/search-field-bg.gif);margin-bottom:6px;">'
           +'<div id="searchdiv\"'+a1+'\" class="search_div" onclick="btnpressed(\''+a1+'\')">Search</div>'
           +'<div class="searchspacer">&nbsp;</div>'
           +'<div style="width: 85%;overflow:hidden;">'
           +'<input '+valueStr+' onkeypress=\"javascript:if(event.keyCode==13)btnpressed(\''+a1+'\');\" style="background-color: transparent;float:left;border:none; padding-top:3px; height:21px; border-left:solid 1px #a0bcda;width:100%;" type="text" id="search'+a1+'" />'
           +'</div>'
           +'</div>');
    },

    paging:function(numpages,searchss){
        var a =this.id;
        if(!searchss) {
             searchss="";
        }
        var to = this.panelcount;
        if(this.panelcount>this.totalCount)
            to = this.totalCount;
        var pagininfo = "<span id='"+a+"pagetext0' style='float:left;padding-top:3px;'> 1 - "+to+" of "+this.totalCount+"</span>";
        var pager = '<div id="pageinfobar'+a+'" class="portlet-paging">'+pagininfo;
        if(numpages>1) {
            pager +='<span wtf:qtip="Older" id="'+a+'nextpage0" class="pagination-div next-pagination" onclick="pagingRedirect(\''+a+'\',1,'+this.count+',\''+searchss+'\','+this.panelcount+');">1</span>';
            pager +='<span wtf:qtip="Oldest" id="'+a+'lastpage0" class="pagination-div last-pagination" onclick="pagingRedirect(\''+a+'\','+(numpages-1)+','+this.count+',\''+searchss+'\','+this.panelcount+');">1</span>';
        }
        pager=pager+'</div>';
        return(pager);
    },

    changePagingBar : function(currentPage){
        var a = this.id;
        var numpages = Math.ceil(this.totalCount/this.panelcount);
        var from = (currentPage * this.panelcount)+1;
        var to = from + this.panelcount-1;
        if(to>this.totalCount)
            to = this.totalCount;
        var pagininfo = "<span id='"+a+"pagetext"+currentPage+"' style='float:left;padding:3px 0px 0px 6px;'> "+from+" - "+to+" of "+this.totalCount+"</span>";
        var pager = '<div id="pageinfobar'+a+'" style="float:left;padding-right:7px;background-color:#f1f1f1;width:98%;">';
        if(numpages>1 && currentPage==0) {//first page
            pager +=pagininfo;
            pager +='<span wtf:qtip="Older" id="'+a+'nextpage'+currentPage+'" class="pagination-div next-pagination" onclick="pagingRedirect(\''+a+'\','+(currentPage+1)+','+this.count+',\'\','+this.panelcount+');">1</span>';
            pager +='<span wtf:qtip="Oldest" id="'+a+'lastpage'+currentPage+'" class="pagination-div last-pagination" onclick="pagingRedirect(\''+a+'\','+(numpages-1)+','+this.count+',\'\','+this.panelcount+');">1</span>';
        } else if(currentPage==(numpages-1)){//last page
            pager += '<span wtf:qtip="Newest" id="'+a+'firstpage'+currentPage+'" class="pagination-div first-pagination" onclick="pagingRedirect(\''+a+'\','+(0)+','+this.count+',\'\','+this.panelcount+');">1</span>';
            pager += '<span wtf:qtip="Newer" id="'+a+'prevpage'+currentPage+'" class="pagination-div prev-pagination" onclick="pagingRedirect(\''+a+'\','+(currentPage-1)+','+this.count+',\'\','+this.panelcount+');">1</span>';
            pager += pagininfo;
        } else {
            pager += '<span wtf:qtip="Newest" id="'+a+'firstpage'+currentPage+'" class="pagination-div first-pagination" onclick="pagingRedirect(\''+a+'\','+(0)+','+this.count+',\'\','+this.panelcount+');">1</span>';
            pager += '<span wtf:qtip="Newer" id="'+a+'prevpage'+currentPage+'" class="pagination-div prev-pagination" onclick="pagingRedirect(\''+a+'\','+(currentPage-1)+','+this.count+',\'\','+this.panelcount+');">1</span>';
            pager += pagininfo;
            pager += '<span wtf:qtip="Older" id="'+a+'nextpage'+currentPage+'" class="pagination-div next-pagination" onclick="pagingRedirect(\''+a+'\','+(currentPage+1)+','+this.count+',\'\','+this.panelcount+');">1</span>';
            pager += '<span wtf:qtip="Oldest" id="'+a+'lastpage'+currentPage+'" class="pagination-div last-pagination" onclick="pagingRedirect(\''+a+'\','+(numpages-1)+','+this.count+',\'\','+this.panelcount+');">1</span>';

//            Wtf.get('pageinfobar'+a).createChild('<span id="'+a+'firstpage'+currentPage+'" class="pagination-div first-pagination" onclick="pagingRedirect(\''+a+'\','+(0)+','+this.count+',\'\','+this.panelcount+');">1</span>');
//            Wtf.get('pageinfobar'+a).createChild('<span id="'+a+'prevpage'+currentPage+'" class="pagination-div prev-pagination" onclick="pagingRedirect(\''+a+'\','+(currentPage-1)+','+this.count+',\'\','+this.panelcount+');">1</span>');
//            Wtf.get('pageinfobar'+a).createChild(pagininfo);
//            Wtf.get('pageinfobar'+a).createChild('<span id="'+a+'nextpage'+currentPage+'" class="pagination-div next-pagination" onclick="pagingRedirect(\''+a+'\','+(currentPage+1)+','+this.count+',\'\','+this.panelcount+');">1</span>');
//            Wtf.get('pageinfobar'+a).createChild('<span id="'+a+'lastpage'+currentPage+'" class="pagination-div last-pagination" onclick="pagingRedirect(\''+a+'\','+(numpages-1)+','+this.count+',\'\','+this.panelcount+');">1</span>');
        }
//        for(var cnt=0;cnt<childArrLength;cnt++) {
//            var childnode = document.getElementById(document.getElementById('pageinfobar'+a).childNodes[0].id);
//            document.getElementById('pageinfobar'+a).removeChild(childnode);
//        }

        pager=pager+'</div>';
        return(pager);
    },

    callRequest:function(url,searchss,pager){
        this.panelcount = (this.newObj.numRecs)?this.newObj.numRecs:this.panelcount;
        var headerHtml = this.newObj.headerHtml;
        var mytestpl=this.newObj.template;
        var xtooltip = this.newObj.xtooltip;
        var formatField = this.newObj.formatField;
        var prefixImage = this.newObj.prefixImage;
        var imageField = this.newObj.imageField;
        var formatFileSize = this.newObj.formatFileSize;
        var quoteFormatField = this.newObj.quoteFormatField;
        var tpl_tool_tip = "";
        var autoHide = "";
        var closable = "";
        var height = "";
        var emptyText = "";
        var shwCombo=this.newObj.isCombo;
        if(this.newObj.emptyText != null){
            emptyText = this.emptyText;
        }
        
        if(this.newObj.tool_tip != null){
            tpl_tool_tip = this.newObj.tool_tip.tpl_tool_tip;
            autoHide = this.newObj.tool_tip.autoHide;
            closable = this.newObj.tool_tip.closable;
            height = this.newObj.tool_tip.height;
        }

         Wtf.Ajax.requestEx({
            url: (url)?url:this.newObj.url+"?limit="+this.panelcount+"&start=0&searchString=",
			params: this.newObj.paramsObj
         },
         this,
         function(result, req) {
                var innerHTML="";
                var obj = result; //eval('('+result.responseText+')');
                if(xtooltip && formatField){
                    this.newObj.formatField = formatField;
                    this.formatSpecifiedField(obj);
                }
                
                if(xtooltip && quoteFormatField){
                    this.newObj.quoteFormatField = quoteFormatField;
                    this.formatquoteField(obj);
                }

                if(prefixImage && imageField){
                    this.newObj.imageField = imageField;
                    this.formatFileName(obj);
                }

                if(formatFileSize){
                    this.formatFileSize(obj);
                }
                if(this.id == 'DSBMyWorkspaces' || this.id == 'DSBAlerts' || this.id == 'DSBBookmarks' || this.id == 'DSBSavedSearches') {
                    var lib=this.getDataString(obj,mytestpl,headerHtml,tpl_tool_tip,autoHide,closable,height,emptyText,shwCombo);
                    this.totalCount = obj.count;
                    innerHTML=this.getPagingString(lib,obj,searchss,pager);
                    var pgsize=Math.ceil(obj.count/this.panelcount);
                    this.writeTemplateToBody(innerHTML,lib,searchss,pgsize,pager);
                    mytestpl = "";
                    this.togglePageCss(this.id,pager);
                    if(this.cmb3)
                    document.getElementById("cmb3").selectedIndex=this.cmb3;
                    if(url && this.isSearch){
                        document.getElementById("search"+this.id).value=searchss;
                    }
                    if(this.storeflag){
                            this.storefunction.call();
                    }
                    if(this.id == 'DSBMyWorkspaces'){
                        document.getElementById("refreshiconupdates").className="refreshWidgets";
                        document.getElementById("refreshiconupdates").title=WtfGlobal.getLocaleText("hrms.common.refresh.icon");
                    }
                    if(this.id == 'DSBSavedSearches'){
                        document.getElementById("refreshiconsavesearch").className="refreshWidgets";
                        document.getElementById("refreshiconsavesearch").title=WtfGlobal.getLocaleText("hrms.common.refresh.icon");
                    }
                    if(this.id == 'DSBAlerts'){
                        document.getElementById("refreshiconalerts").className="refreshWidgets";
                        document.getElementById("refreshiconalerts").title=WtfGlobal.getLocaleText("hrms.common.refresh.icon");
                    }
                } else {
                    this.body.dom.innerHTML = '<div class="portlet-body">'
                            +'<div class ="hrmsDashboardThumbnailPortlet" style="background-color:#ffffff;padding:1%;float:left;width:95.5%" id="hrmsDashboardThumbnailPortlet'+this.id+'">'
                            +'</div>'
                            +'</div>';
                    for(var cnt=0;cnt<obj.data.length;cnt++) {
                        new Wtf.emailTemplateThumbnail({
                            tName: obj.data[cnt].name,
                            thumbnail: obj.data[cnt].img,
                            tqtip: obj.data[cnt].qtip,
                            tfun1 : obj.data[cnt].onclick,
                            tempRec:cnt,
                            scope: this,
                            listeners: {
                                "templateSelected": this.selectTemplate
                            },
                            renderTo: "hrmsDashboardThumbnailPortlet"+this.id
                        });
                    }
                }
            },
            function(result, req){
                mytestpl = "";
            }
         );
    },

    selectTemplate :function(obj) {
        eval(obj.tfun1);
    },
    getDataString:function(obj, tpl, headerHtml,tpl_tool_tip,autoHide,closable,height,emptyText,shwCombo) {
        var lib= "";
        
        if(this.newObj.isTable == true)
            {

                lib = lib +"<table class='"+this.newObj.tableClassName+"' border='0' cellspacing=0 width='100%' style='float:left;margin:0px;'>";
                if(this.newObj.tableHeader != null)
                    {

                        lib = lib +this.newObj.tableHeader;
                    }
            }else{
                lib = lib +"<div class='content-wrapper'>";
            }
                lib = lib +headerHtml;

            if(shwCombo) {
                    lib = lib  +'<select id="cmb'+this.cmbcnt+'" style="width:200px;margin-right:3px;margin-left:5px;" onchange="filterBrands('+this.cmbcnt+')"><option  value="">Select Value --<option>';
                    this.cmbcnt++;
            }
            
            for(var i=0;i<obj.data.length;i++){
                if(this.newObj.isToolTip == true){
                    var target = "KCUser"+obj.data[i].userid;
                    createtooltip1(target,tpl_tool_tip,autoHide,closable,height);
                }
                if(obj.data.length==0)
                    {
                        lib = emptyText;
                    }
                if(this.pagingflag){
                    if(obj.count == -1){
                        this.config1[0].isPaging = false;
                        this.config1[0].WorkspaceLinks = '';
                        if(this.timeid){
                            clearTimeout(this.timeid);
                        }
                    }
                    else{
                        this.config1[0].isPaging = true;
                        this.config1[0].WorkspaceLinks = signoutLinks;
                        if(this.timeid){
                            clearTimeout(this.timeid);
                        }
                        var  time=parseInt(Wtf.GS.td,10);
                        var  timedelay = time * 60 * 1000;
                        this.timeid = this.doSearch.defer(timedelay,this,[this.url,'']);
                    }
                }
                lib = lib  + tpl.applyTemplate(obj.data[i])
            }
            
            if(shwCombo)
                    lib = lib  +"</select></div>";
            else {

            if(this.newObj.isTable == true)
                {
                        lib = lib +"</table><br style='clear:both'/>";
                }else{
                        lib = lib +"</div><br style='clear:both'/>";
                }
            }
            return lib;
    },

getPagingString:function(lib,obj,searchss,pager) {
        this.panelcount = (this.newObj.numRecs)?this.newObj.numRecs:this.panelcount;
        
        var tmpHTML = "";
        if(obj.count!=0){

         var links = " ";
        if(this.newObj.WorkspaceLinks != null){
            links = this.newObj.WorkspaceLinks;
        }
            var pgsize=Math.ceil(obj.count/this.panelcount);
        tmpHTML = '<div class="portlet-body">'
                            +((this.newObj.isSearch)?this.addSearchBar1(searchss):"")
                            +'<div style="background-color:#ffffff;padding:1%;float:left;width:95.5%">'
                            +lib
                            +((this.newObj.isPaging)? (pager===undefined ? this.paging(pgsize,searchss) : this.changePagingBar(pager)):"")
                            +'</div>'+links
                            +'</div>';
        }else{
            if(this.newObj.emptyText!=null)
                {
                    links = " ";
                    if(this.newObj.WorkspaceLinks != null){
                        links = this.newObj.WorkspaceLinks;
                    }
                    tmpHTML = '<div class="portlet-body">'
                            +((this.newObj.isSearch)?this.addSearchBar1():"")
                            +'<div style="background-color:#ffffff;padding:1%;float:left;width:95.5%">'
                            +this.newObj.emptyText
                            +'</div>'+ links
                            +'</div>';

                }
        }
         return tmpHTML;
    },


    doPaging:function(url,offset,searchstr,pager,subPan){
        url+="?limit="+this.panelcount+"&start="+offset+"&searchString="+searchstr;
        this.callRequest(url,searchstr,pager);
    },

    togglePageCss: function(panelid,pager){
        var clickedDiv = document.getElementById(panelid+pager);
        var prevDiv;
        for(var x=0;x<pager;x++){
          prevDiv = document.getElementById(panelid+(x));
          if(prevDiv)
             prevDiv.className="pagination-div deactive-pagination";
        }
        if(clickedDiv)
            clickedDiv.className="pagination-div active-pagination";

    },

    doSearch: function(url,searchstr){
        var str = this.newObj.url;
        var myArr = str.split('?');
        var newUrl=myArr[0]+"?limit="+this.panelcount+"&start=0&searchString="+searchstr;
        this.callRequest(newUrl,searchstr);
    },

    formatSpecifiedField : function(obj){
        for(i=0;i<obj.data.length;i++){
            for(j=0;j<this.newObj.formatField.length;j++){
                obj.data[i][this.newObj.formatField[j]] = getFormattedDate(obj.data[i][this.newObj.formatField[j]]);
            }
        }
    },

    formatquoteField : function(obj){
        for(i=0;i<obj.data.length;i++){
            for(j=0;j<this.newObj.quoteFormatField.length;j++){
                obj.data[i][this.newObj.quoteFormatField[j]] = obj.data[i][this.newObj.quoteFormatField[j]].adjustQuotes();
            }
        }
    },
    
    formatFileName : function(obj){
        for(var i = 0 ; i < obj.data.length; i++){
            if(obj.data[i].docName != undefined && obj.data[i].docName != null && obj.data[i].docName != ""){
                var imageClass = getimage(obj.data[i].docName);
                obj.data[i].imageClass = imageClass;
            }
        }
    },

    formatFileSize : function(obj){
        obj.data[0].totalSize = getFileSize(obj.data[0].totalSize);
    }
});

function showResults(widgetid) {
    
    var cmbstr="";
        var widget = Wtf.getCmp(widgetid);
        widget.cmb3 = document.getElementById('cmb3').selectedIndex;
        if((document.getElementById('cmb1')) && (document.getElementById('cmb1').value !='')) {
           cmbstr=cmbstr+document.getElementById('cmb1').value+",";
        }
        if((document.getElementById('cmb2')) && (document.getElementById('cmb2').value !='')) {
            cmbstr=cmbstr+document.getElementById('cmb2').value+",";
        }
        if((document.getElementById('cmb3')) && (document.getElementById('cmb3').value !='')) {
            cmbstr=cmbstr+document.getElementById('cmb3').value+",";
        }
    
    Wtf.getCmp('DSBKnowledgeCampus').doSearch("jspfiles/knowledgeUni/workspace.jsp",cmbstr);
}

function filterBrands(val) {

             if( (val==1) &&  (document.getElementById('cmb1')) ) {
                 
                    Wtf.Ajax.requestEx({
                            url:'jspfiles/knowledgeUni/CenterManagement.jsp',
                            params:{
                                flag:119,
                                segmentname:document.getElementById('cmb1').value
                            }
                        },
                        this,
                        function(result,resp){
                            if(result.success){
                                var cmb2Select = document.getElementById('cmb2');
                                cmb2Select.innerHTML = "";
                                var optSelectEl = getOptionElement("Select Value --" , "");
                                Wtf.isIE ? cmb2Select.add(optSelectEl) : cmb2Select.appendChild(optSelectEl);

                                optSelectEl =getOptionElement("" , "");
                                Wtf.isIE ? cmb2Select.add(optSelectEl) : cmb2Select.appendChild(optSelectEl);

                                for(var i=0;i<result.data.length;i++){
                                    var text = result.data[i].name;
                                    var opt = getOptionElement(text , text);
                                    Wtf.isIE ? cmb2Select.add(opt) : cmb2Select.appendChild(opt);
                                }
                            }else{
                                msgBoxShow(6,1);
                            }

                        }, function(){
                                msgBoxShow(6,1);
                    });

             }

}

function getOptionElement(text,val){
    var opt = document.createElement("option");
    opt.value = val;
    opt.text = text;
    return opt;
}


Wtf.emailTemplateThumbnail = function(conf){
    Wtf.apply(this, conf);
    this.addEvents({
        "templateSelected": true
    });
    Wtf.emailTemplateThumbnail.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.emailTemplateThumbnail, Wtf.Component, {
    onRender: function(conf){
        Wtf.emailTemplateThumbnail.superclass.onRender.call(this, conf);
        this.elDom = Wtf.get(this.renderTo).createChild({
            tag: "div",
            cls: "templateThumbCont templateThumbContainer"
        });
        this.templateImg = document.createElement("img");
        this.templateImg.width = "70";
        this.templateImg.height = "63";
//        this.templateImg.alt = this.tqtip;
        this.templateImg.src = this.thumbnail;
        this.templateImg.setAttribute("wtf:qtip",this.tqtip);
        var nameDiv = document.createElement("div");
        var centerTag = document.createElement("center");
        this.nameSpan = document.createElement("span");
        nameDiv.appendChild(this.nameSpan);
        this.nameSpan.className = "templateThumbSpan";
        this.nameSpan.setAttribute("wtf:qtip",this.tqtip);
        nameDiv.className = "templateNameDiv";
        this.templateImg.className = "templateThumbImg";
        this.nameSpan.innerHTML = this.tName;
        this.elDom.addListener("click", this.fireSelect, this);
        centerTag.appendChild(this.templateImg);
        this.elDom.appendChild(centerTag);
        this.elDom.appendChild(nameDiv);
    },
    setName: function(templatename){
        this.nameSpan.innerHTML = templatename;
    },
    setImage: function(src) {
        this.templateImg.src = src;
    },
    fireSelect: function(){
        this.fireEvent("templateSelected", this);
    },
    selectTemplate: function(){
        this.elDom.addClass("selectedTemplate");
        this.elDom.removeClass("templateThumbContainer");
    },
    deselectTemplate: function(){
        this.elDom.removeClass("selectedTemplate");
        this.elDom.addClass("templateThumbContainer");
    }
});
