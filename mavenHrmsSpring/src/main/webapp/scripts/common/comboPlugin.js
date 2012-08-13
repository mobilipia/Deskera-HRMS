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
Wtf.common.comboAddNew= function(conf){
    Wtf.apply(this, conf);
    Wtf.common.comboAddNew.superclass.constructor.call(this, conf);
    this.addEvents({
        beforeFilter: true,
        afterFilter: true
    });
};

Wtf.extend(Wtf.common.comboAddNew, Wtf.util.Observable, {
    init: function(combo){
        this.combo = combo;
        combo.on("render", function(conf){
            this.addNewButton();
        }, this);
        combo.on("invalid", function(conf){
            this.moveErrorIcon();
        }, this);
    },

    addNewButton: function(){
        var _cS = this.combo.getSize();
        this.width = (this.combo.width !== undefined) ? this.combo.width : _cS.width;
        this.width -= 25;
        this.combo.setWidth(this.width);
        var _cD = this.combo.el.dom;
        var _pD = _cD.parentNode;
        _cD.style.width = (this.width - 25) + "px";
        this._fI = document.createElement("img");
        this._fI.src = "../../images/add.gif";
        this._fI.height = 20;
        this._fI.width = 20;
        this._fI.style.left = this.width + "px";
        this._fI.style.position = "absolute";
        this._fI.style.margin = "0px 0px 0px 5px";
        this._fI.style.cursor = "pointer";
        this._fI.onclick = this.handler.createDelegate(this.scope, []);
//        _pD.parentNode.className += " newPlugin";
        _pD.appendChild(this._fI);
    },
    moveErrorIcon: function(){
        var _pD = this.combo.el.dom.parentNode;
        var _icon = _pD.nextSibling;
        if(_icon !== null){
            var left = _icon.style.left;
            if(left !== undefined){
                if(typeof left == "string"){
                    left = left.substring(0, left.length - 2);
                    left = parseInt(left);
                }
                left = left + 25;
            }
            _icon.style.left = left + "px";
        }
    }
});
