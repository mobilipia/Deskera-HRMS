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
Wtf.common.KWLListPanel = function(config){
    Wtf.common.KWLListPanel.superclass.constructor.call(this, config);
    
    this.addEvents({
        "loadcomplete": true
    });
}

Wtf.extend(Wtf.common.KWLListPanel, Wtf.Panel, {
    tempdstore: null,
    dstore: null,
    checkFlag: 0,
    checkForAnoReq: 0,
    prevPageSize: 0,
    prevPageNo: 0,
    pageSize: 0,
    ImgSrc: "",
    pageno: 0,
    jobj: null,
    cursor: 0,
    paging: true,
    itemsContainer: null,
    renderTo: null,
    loadtext: null,
    contentEl: null,
    headerAsText: true,
    autoLoad: true,
    total: 0,
    collapse: null,
    bttnArray: null,
    
    initComponent: function(){
        Wtf.common.KWLListPanel.superclass.initComponent.call(this);
        this.addEvents({
            'dataloaded': true,
            'contextmenu': true
        });
    },
    
    onRender: function(config){
        Wtf.common.KWLListPanel.superclass.onRender.call(this, config);
        this.headercontent = document.createElement('div');
        if (this.collapsible) {
            this.collapseButton = document.createElement('div');
            this.collapseButton.className = 'collapse';
            this.collapseButton.id = this.id + 'Collapse';
            this.headercontent.appendChild(this.collapseButton);
        }
        this.header.dom.className = "header x-unselectable";
        
        this.header.dom.removeChild(this.header.dom.firstChild);
        this.el.dom.firstChild.nextSibling.firstChild.className += ' x-panel-body-noborder-KWL';
        
        
        this.headercontent.className = 'headercontent';
        this.headercontent.id = this.id + '-headcont';
        
        this.label = document.createElement('span');
        this.label.className = 'label';
        this.label.id = this.id + '-label';
        
        this.headercontent.appendChild(this.label);
        
        this.labelext = document.createElement('span');
        this.labelext.innerHTML = "";
        this.labelext.className = 'label';
        this.labelext.style.margin = '0 0 0 4px';
        this.label.id = this.id + '-labelext';
        
        this.headercontent.appendChild(this.labelext);
        
        if (this.paging) {
            this.btndiv = document.createElement('div');
            this.btndiv.className = 'btndiv';
            this.btndiv.id = this.id + '-btndiv';
            
            this.nxtdiv = document.createElement('div');
            this.nxtdiv.id = this.id + '-next';
            
            this.prediv = document.createElement('div');
            this.prediv.id = this.id + '-prev';
            
            this.nxtdiv.className = 'nxtBtnDivNone';
            this.prediv.className = 'preBtnDivNone';
            
            
            this.startlbl = document.createElement('span');
            this.startlbl.id = this.id + '-start';
            this.endlbl = document.createElement('span');
            this.endlbl.id = this.id + '-end';
            this.totallbl = document.createElement('span');
            this.totallbl.id = this.id + '-total';
            
            this.startlbl.innerHTML = 0 + '-';
            this.endlbl.innerHTML = 0 + ' / ';
            this.totallbl.innerHTML = 0;
            
            this.pagging = document.createElement('span');
            this.pagging.appendChild(this.startlbl);
            this.pagging.appendChild(this.endlbl);
            this.pagging.appendChild(this.totallbl);
            this.pagging.className = 'pagging';
            this.pagging.id = this.id + '-pagging';
            this.btndiv.appendChild(this.prediv);
            this.btndiv.appendChild(this.nxtdiv);
            this.headercontent.appendChild(this.btndiv);
            this.headercontent.appendChild(this.pagging);
        }
        this.header.dom.appendChild(this.headercontent);
        this.bttnArray = [];
        this.calculatePageSize();
        if (this.collapsible) 
            Wtf.get(this.collapseButton.id).addListener('click', this.toggleCollapse, this);
        if (this.url) {
            if (this.autoLoad == true) 
                this.loadUrl();
        }
        else if (this.ds) {
                this.jobj = eval('(' + this.ds + ')');
                this.FillContainer();
        }
        this.loadMask = new Wtf.LoadMask(this.el.dom, Wtf.apply(this.loadMask));
        
        /*if (!this.contentType) {
            if (this.Href) 
                this.makeDashBttn(0, this.pageSize);
            else 
                this.makeDashAlert(0, this.pageSize);
        }*/
        
    },
    onHover: function(which){
        if (which == 'next') 
            this.nxtdiv.className = 'nxtBtnDivover';
        else 
            this.prediv.className = 'preBtnDivover';
    },
    onOut: function(which){
        if (which == 'next') 
            this.nxtdiv.className = 'nxtBtnDivout';
        else 
            this.prediv.className = 'preBtnDivout';
    },
    onClick: function(which){
        if(this.dstore){
            if (which == 'next') {
                this.pageno = this.pageno + 1;
                if ((this.dstore.getCount() >= ((this.pageno + 1) * this.pageSize)) || this.dstore.getCount() == this.total) {
                    this.fillwithstore();
                    return;
                }
            }
            else {
                this.pageno = this.pageno - 1;
                if (this.dstore.getCount() >= ((this.pageno + 1) * this.pageSize)) {
                    this.fillwithstore();
                    return;
                }
            }
        }
        if (this.jobj) {
            this.remDashBttn();
            this.FillContainer();
        }
        else {
            if (this.checkFlag == 1) 
                this.checkForAnoReq = 1;
            else 
                this.checkForReq();
        }
    },
    
    fillwithstore: function(stre){
        if (stre) 
            this.dstore = stre;
        var c = this.pageSize * this.pageno;
        if (this.prevPageSize != this.pageSize || this.prevPageNo != this.pageno) {
            if (this.prevPageSize < this.pageSize) {
                if (this.Href) 
                    this.makeDashBttn(this.bttnArray.length, this.pageSize);
                else 
                    this.makeDashAlert(this.bttnArray.length, this.pageSize);
            }
            else {
                this.remDashBttn();                
            }
        }
        var total = 0;
        for (var i = 0; i < this.pageSize; i++) {
            if (this.dstore.getAt(c) != undefined) {
                if (!this.dstore.getAt(i).data['img'] || this.dstore.getAt(c).data['img'].length == 0) {
                    if (this.contentType) 
                        this.bttnArray[i].addData(this.dstore.getAt(c));
                    else 
                        this.bttnArray[i].addData('../../images/' + this.ImgSrc, this.dstore.getAt(c).data['name'], this.dstore.getAt(c).data['id'], this.Href, this.TabType);
                }
                else {
                    if (this.contentType) 
                          this.bttnArray[i].addData(this.tempdstore.getAt(c));
                    else 
                        this.bttnArray[i].addData(this.dstore.getAt(c).data['img'], this.dstore.getAt(c).data['name'], this.dstore.getAt(c).data['id'], this.Href, this.TabType);
                }
                this.bttnArray[i].id = this.dstore.getAt(c).data['id'];
                c++;
                total++;
            }
            else 
                break;
        }
        for (var t = i; t < this.bttnArray.length; t++) {
            this.bttnArray[t].hideButton();
        }
        if(this.tempdstore && this.tempdstore.reader && this.tempdstore.reader.jsonData){
            var cnt = this.tempdstore.reader.jsonData.count;
            if (cnt != 0) {
                this.startlbl.innerHTML = (this.pageno * this.pageSize) == 0 ? (1 + '-') : (((this.pageno * this.pageSize) + 1) + '-');
                this.endlbl.innerHTML = (c) + '/ '
            }
            else {
                this.startlbl.innerHTML = '0-';
                this.endlbl.innerHTML = '0/';
            }
            this.totallbl.innerHTML = cnt;
            if (this.pageno == 0) 
                startflag = true;
            else 
                startflag = false;
            if (((this.pageno * this.pageSize) + total) == cnt) 
                endflag = true;
            else 
                endflag = false;
            this.ResetNavBtns(startflag, endflag);
        }
    },
    
    FillContainer: function(){
        var i = 0;
        var startflag = true;
        var endflag = true;
        if (this.jobj != null) {
            //alert(this.tempdstore.getCount());
            while (i < this.pageSize && this.jobj['name'][((this.pageno * this.pageSize) + i)] != null) {
                new Wtf.common.WtfButton({
                    caption: this.jobj['name'][((this.pageno * this.pageSize) + i)],
                    href: "#",
                    bodyStyle: 'position:relative;float:left;',
                    imgSrc: '../../images/defaultuser.png',
                    renderTo: this.el.dom.firstChild.nextSibling.firstChild.id
                
                });
                this.cursor = ((this.pageno * this.pageSize) + i) + 1;
                i = i + 1;
            }
            this.startlbl.innerHTML = (this.pageno * this.pageSize) == 0 ? (1 + '-') : (((this.pageno * this.pageSize) + 1) + '-');
            this.endlbl.innerHTML = this.cursor + ' / ';
            this.totallbl.innerHTML = this.jobj['name'].length;
            
            if (this.pageno * this.pageSize == 0) 
                startflag = true;
            else 
                startflag = false;
            if (this.cursor == this.jobj['name'].length) 
                endflag = true;
            else 
                endflag = false;
        }
        else {
            this.startlbl.innerHTML = '0-';
            this.endlbl.innerHTML = '0 / ';
            this.totallbl.innerHTML = '0';
        }
        this.ResetNavBtns(startflag, endflag);
    },
    
    
    Refresh: function(jstring){
        jstring = jstring.trim();
        this.remDashBttn();
        if (jstring == "{}") 
            this.jobj = null;
        else 
            this.jobj = eval('(' + jstring + ')');
        this.pageno = 0;
        this.FillContainer();
    },
    FillContainerUrl: function(){
        var cnt = 0;
        this.total = this.tempdstore.reader.jsonData.count;
        if (!this.dstore) 
            this.dstore = new Wtf.data.Store;
        else 
            cnt = this.dstore.getCount();
        
        for (var z = 0; z < this.tempdstore.getCount(); z++) {
            var t = this.dstore.find("id", this.tempdstore.getAt(z).data["id"], 0, false, true);
            if (t == -1) {
                this.dstore.insert(cnt, this.tempdstore.getAt(z));
                cnt++;
            }
        }
        if (this.checkForAnoReq == 1) {
            this.checkForReq();
            this.checkForAnoReq = 0;
        }
        var i = 0;
        var startflag = true;
        var endflag = true;
        if (this.total != 0) {
            while (i < this.pageSize && this.tempdstore.getAt(i) != null) {
                if(this.bttnArray[i]){
                    if (!this.tempdstore.getAt(i).data['img'] || this.tempdstore.getAt(i).data['img'].length == 0) {
                        if (this.contentType) 
                            this.bttnArray[i].addData(this.tempdstore.getAt(i));
                        else 
                            this.bttnArray[i].addData('../../images/' + this.ImgSrc, this.tempdstore.getAt(i).data['name'], this.tempdstore.getAt(i).data['id'], this.Href, this.TabType);
                    }
                    else {
                        Wtf.iStore.setImg(this.tempdstore.getAt(i).data['id'],this.tempdstore.getAt(i).data['img']);
                        if (this.contentType) 
                            this.bttnArray[i].addData(this.tempdstore.getAt(i));
                        else 
                            this.bttnArray[i].addData(this.tempdstore.getAt(i).data['img'], this.tempdstore.getAt(i).data['name'], this.tempdstore.getAt(i).data['id'], this.Href, this.TabType);
                    }
                    this.bttnArray[i].id = this.tempdstore.getAt(i).data['id'];
                    this.cursor = (i) + 1;
                }
                i = i + 1;
            }
        }
        for (var t = i; t < this.bttnArray.length; t++) {
            this.bttnArray[t].hideButton();
        }
        var cnt = this.total;
        if (this.total != 0) {
            this.startlbl.innerHTML = (this.pageno * this.pageSize) == 0 ? (1 + '-') : (((this.pageno * this.pageSize) + 1) + '-');
            this.endlbl.innerHTML = ((this.pageno * this.pageSize) + i) + '/ '
        }
        else {
            this.startlbl.innerHTML = '0-';
            this.endlbl.innerHTML = '0/';
        }
        this.totallbl.innerHTML = cnt;
        if (this.pageno == 0) 
            startflag = true;
        else 
            startflag = false;
        if (((this.pageno * this.pageSize) + i) == cnt) 
            endflag = true;
        else 
            endflag = false;
        this.ResetNavBtns(startflag, endflag);
        this.checkFlag = 0;
        this.loadMask.hide();
        this.fireEvent("loadcomplete");
    },
    
    setTitle: function(title){
        this.title = title;
        this.label.innerHTML = this.title;
    },
    ResetTitle: function(title){
        if (this.labelext) 
            this.labelext.innerHTML = "  '" + title + "'";
    },
    ResetNavBtns: function(startflag, endflag){
        if (startflag) {
            this.prediv.className = "preBtnDivNone";
            this.prediv.onmouseover = null;
            this.prediv.onmouseout = null;
            this.prediv.onclick = null;
        }
        else {
            this.prediv.className = "preBtnDivout";
            this.prediv.onmouseover = this.onHover.createDelegate(this, ['prev']);
            this.prediv.onmouseout = this.onOut.createDelegate(this, ['prev']);
            this.prediv.onclick = this.onClick.createDelegate(this, ['prev']);
        }
        if (endflag) {
            this.nxtdiv.className = "nxtBtnDivNone";
            this.nxtdiv.onmouseover = null;
            this.nxtdiv.onmouseout = null;
            this.nxtdiv.onclick = null;
        }
        else {
            this.nxtdiv.className = "nxtBtnDivout";
            this.nxtdiv.onmouseover = this.onHover.createDelegate(this, ['next']);
            this.nxtdiv.onmouseout = this.onOut.createDelegate(this, ['next']);
            this.nxtdiv.onclick = this.onClick.createDelegate(this, ['next']);
        }
    },
    loadUrl: function(){
        if (this.checkFlag == 1) 
            this.checkForAnoReq = 1;
        else {
            this.makeAjaxRequest();
            this.checkFlag = 1;
        }
    },
    makeAjaxRequest: function(){
        if(!this.reader){
            this.reader = new Wtf.data.JsonReader({
                root: 'data',
                fields: [{
                    name: 'name',
                    type: 'string'
                }, {
                    name: 'id',
                    type: 'string'
                }, {
                    name: 'img',
                    type: 'string'
                }]
            });
        }
            
        this.tempdstore = new Wtf.data.Store({
            method: 'POST',
            id: 'tempstore',
            url: this.url,
            baseParams: ({
                pageno: this.pageno,
                pageSize: this.pageSize
            }),
            reader: this.reader
        });
        this.tempdstore.on('load', function(){
            this.fireEvent('dataloaded', this.tempdstore);
            if (this.prevPageSize != this.pageSize || this.prevPageNo != this.pageno) {
            if (this.prevPageSize < this.pageSize) {
                if (this.Href) 
                    this.makeDashBttn(this.bttnArray.length, this.bttnArray.length+this.tempdstore.getCount());
                else 
                    this.makeDashAlert(this.bttnArray.length, this.bttnArray.length+this.tempdstore.getCount());
            }
            else {
                this.remDashBttn();
            }
            this.prevPageSize = this.pageSize;
            this.prevPageNo = this.pageno;
           }
            this.FillContainerUrl();
        }, this);
        this.tempdstore.on('loadexception', this.loadReload, this);
        this.loadMask.show();
        this.tempdstore.load();
    },
    loadReload: function(){
        this.loadMask.hide();
    },
       
    makeDashBttn: function(start, end){
        for (var i = start; i < end; i++) 
            this.bttnArray.push(new Wtf.common.WtfButton({
                renderTo: this.el.dom.firstChild.nextSibling.firstChild.id,
                tabtype: this.TabType,
                bodyStyle: 'position:relative;float:left;',
                id: i
            }));
    },
    makeDashAlert: function(start, end){
        for (var i = start; i < end; i++) 
            this.bttnArray.push(new Wtf.ux.WtfAlert({
                renderTo: this.el.dom.firstChild.nextSibling.firstChild.id,
                bodyStyle: 'position:relative;float:left;',
                id: i
            }));
    },
    
    remDashBttn: function(){
        if(this.bttnArray.length > this.pageSize ) {
            while (this.pageSize != this.bttnArray.length) {
                 this.bttnArray[this.bttnArray.length - 1].removeButton();
                 this.bttnArray.pop();
            }
        }
    },
    checkForReq: function(){
        /*if (this.prevPageSize != this.pageSize || this.prevPageNo != this.pageno) {
            if (this.prevPageSize < this.pageSize) {
                if (this.Href) 
                    this.makeDashBttn(this.bttnArray.length, this.pageSize);
                else 
                    this.makeDashAlert(this.bttnArray.length, this.pageSize);
            }
            else {
                if (this.Href) 
                    this.remDashBttn();
                else 
                    this.remDashAlert();
            }
            this.prevPageSize = this.pageSize;
            this.prevPageNo = this.pageno;*/
            this.loadMask.show();
            this.makeAjaxRequest();
            this.checkFlag = 1;
        //}
    },
    calculatePageSize: function(){
        this.prevPageSize = this.pageSize;
        var startNo = this.pageno * this.prevPageSize + 1;
        var adiv5 = Wtf.get(this.id);
        if (this.Href) {
            var width_dispcontrols = Math.floor(adiv5.getWidth() / 82);
            var height_dispcontrols = Math.floor((adiv5.getHeight() - 25) / 101);
            this.pageSize = width_dispcontrols * height_dispcontrols;
        }
        else {
            this.pageSize = Math.floor((adiv5.getHeight() - 25) / 30);
        }
        if (this.pageSize <= 0) {
            this.pageSize = 1;
        }
        this.pageno = Math.floor((startNo - 1) / this.pageSize);
    },
    setUrl: function(url){
        this.url = url;
        this.calculatePageSize();
        if (this.prevPageSize < this.pageSize) {
            if (this.Href) 
                this.makeDashBttn(this.bttnArray.length, this.pageSize);
            else 
                this.makeDashAlert(this.bttnArray.length, this.pageSize);
        }
        else {
            this.remDashBttn();
        }
        this.loadMask.show();
        this.makeAjaxRequest();
    }
});
Wtf.reg('KWLListPanel', Wtf.common.KWLListPanel);
