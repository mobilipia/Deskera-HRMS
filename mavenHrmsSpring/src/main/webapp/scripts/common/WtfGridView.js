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
Wtf.ux.KWLGridView = function(config){
    Wtf.apply(this, config);
    Wtf.ux.KWLGridView.superclass.constructor.call(this);
};
Wtf.extend(Wtf.ux.KWLGridView, Wtf.grid.GridView,{
    initTemplates : function(){
        var ts = this.templates || {};
        if(!ts.master){
            ts.master = new Wtf.Template(
                '<div class="x-grid3" hidefocus="true">',
                '<div class="x-grid3-viewport">',
                '<div class="x-grid3-header"><div class="x-grid3-header-inner"><div class="x-grid3-header-offset">{header}</div></div><div class="x-clear"></div></div>',
                '<div class="x-grid3-scroller"><div class="x-grid3-body">{body}</div><a href="#" class="x-grid3-focus" tabIndex="-1"></a></div>',
                "</div>",
                '<div class="x-grid3-resize-marker"> </div>',
                '<div class="x-grid3-resize-proxy"> </div>',
                "</div>"
                );
        }

        if(!ts.header){
            ts.header = new Wtf.Template(
                '<table border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<thead><tr class="x-grid3-hd-row">{cells}</tr></thead>',
                "</table>"
                );
        }

        if(!ts.hcell){
            ts.hcell = new Wtf.Template(
                '<td class="x-grid3-hd x-grid3-cell x-grid3-td-{id}" style="{style}"><div ' +
                'Wtf:qtip="{tip}" {attr} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">', this.grid.enableHdMenu ? '<a class="x-grid3-hd-btn" href="#"></a>' : '',
                '{value}<img class="x-grid3-sort-icon" src="', Wtf.BLANK_IMAGE_URL, '" />',
                "</div></td>"
                );
        }

        if(!ts.body){
            ts.body = new Wtf.Template('{rows}');
        }

        if(!ts.row){
            ts.row = new Wtf.Template(
                '<div class="x-grid3-row {alt}" style="{tstyle}"><table class="x-grid3-row-table" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<tbody><tr>{cells}</tr>',
                (this.enableRowBody ? '<tr class="x-grid3-row-body-tr" style="{bodyStyle}"><td colspan="{cols}" class="x-grid3-body-cell" tabIndex="0" hidefocus="on"><div class="x-grid3-row-body">{body}</div></td></tr>' : ''),
                '</tbody></table></div>'
                );
        }

        if(!ts.cell){
            ts.cell = new Wtf.Template(
                '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
                '<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}>{value}</div>',
                "</td>"
                );
        }

        for(var k in ts){
            var t = ts[k];
            if(t && typeof t.compile == 'function' && !t.compiled){
                t.disableFormats = true;
                t.compile();
            }
        }

        this.templates = ts;

        this.tdClass = 'x-grid3-cell';
        this.cellSelector = 'td.x-grid3-cell';
        this.hdCls = 'x-grid3-hd';
        this.rowSelector = 'div.x-grid3-row';
        this.colRe = new RegExp("x-grid3-td-([^\\s]+)", "");
    },
    // private
    renderHeaders : function(){
        var cm = this.cm, ts = this.templates;
        var ct = ts.hcell;

        var cb = [], sb = [], p = {};

        for(var i = 0, len = cm.getColumnCount(); i < len; i++){
            p.id = cm.getColumnId(i);
            p.value = cm.getColumnHeader(i) || "";
            p.style = this.getColumnStyle(i, true);
            p.tip = cm.config[i].tip;
            if(cm.config[i].align == 'right'){
                p.istyle = 'padding-right:16px';
            }
            cb[cb.length] = ct.apply(p);
        }
        return ts.header.apply({
            cells: cb.join(""),
            tstyle:'width:'+this.getTotalWidth()+';'
            });
    }
});
