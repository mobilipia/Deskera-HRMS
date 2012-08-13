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

Wtf.ChartDDProxy = function(id, sGroup, config) {

    if (id) {
        this.init(id, sGroup, config);
        this.initFrame();
    }
};

Wtf.extend(Wtf.ChartDDProxy, Wtf.dd.DDProxy, {

    startDrag: function(x, y) {
        var dragEl = Wtf.get(this.getDragEl());
        var el = Wtf.get(this.getEl());

        var isRoot = false;

        if(el.id.indexOf("block_") >= 0) {
            var ChartPanel = Wtf.getCmp(companyid + "chartContainer");
            var nodeData = ChartPanel.mappedNodeStore.query("nodeid", el.id.split("block_")[1]);
            if(nodeData.items[0].data.level == 0)
                isRoot = true;
        }

        if(!isRoot) {
            dragEl.applyStyles({
                border: '',
                'z-index': 2000,
                height: 17,
                backgroundColor: el.dom.style.backgroundColor,
                color: el.dom.style.color
            });

            dragEl.update(el.dom.innerHTML);
            dragEl.addClass(el.dom.className + ' dd-proxy');
        }
    },

    onDragOver: function(e, targetId) {

        var toAddClass = true;
        var target = Wtf.get(targetId);
        this.lastTarget = target;

        if(target.id.indexOf("block_") >= 0) {
            var node = Wtf.get("node_" + target.id.split("block_")[1]);

            if(this.id.indexOf("block_") >= 0) {
                var treeNode = Wtf.get("tree_" + this.id.split("block_")[1]);
                if(treeNode.query("div#" + target.id).length > 0)
                    toAddClass = false;
            }
            if(toAddClass)
                node.addClass("chartDragClass");
        }
    },

    onDragOut: function(e, targetId) {

        var toRemoveClass = true;
        var target = Wtf.get(targetId);
        this.lastTarget = null;

        if(target.id.indexOf("block_") >= 0) {
            var node = Wtf.get("node_" + target.id.split("block_")[1]);

            if(this.id.indexOf("block_") >= 0) {
                var treeNode = Wtf.get("tree_" + this.id.split("block_")[1]);
                if(treeNode.query("div#" + target.id).length > 0)
                    toRemoveClass = false;
            }
            if(toRemoveClass)
                node.removeClass("chartDragClass");
        }
    },

    endDrag: function(e) {

        var ChartPanel = Wtf.getCmp(companyid + "chartContainer");
        var dragEl = Wtf.get(this.getDragEl());
        var el = Wtf.get(this.getEl());
        var t = Wtf.get(this.lastTarget);

        var isRoot = false;

        if(el.id.indexOf("block_") >= 0) {
            var nodeData = ChartPanel.mappedNodeStore.query("nodeid", el.id.split("block_")[1]);
            if(nodeData.items[0].data.level == 0)
                isRoot = true;
        }

        if(t && t.id.indexOf("block_") >= 0 && !isRoot) {

            if ('function' === typeof this.config.fn)
                this.config.fn.apply(this.config.scope || window, [this, this.config.dragData]);

            if(this.id.indexOf("block_") >= 0) {
                var treeNode = Wtf.get("tree_" + this.id.split("block_")[1]);
                t = (t.id);

                if (this.lastTarget) {
                    var nodeTree = Wtf.get("node_" + this.lastTarget.id.split("block_")[1]);
                    nodeTree.removeClass("chartDragClass");
                }

                if(treeNode.query("div#" + t).length == 0 && nodeData.items[0].data.fromuid != t.split("block_")[1]) {

                    var parent = t.split("block_")[1];
                    var nodeid = this.id.split("block_")[1];
                    var parentData = ChartPanel.mappedNodeStore.query("nodeid", parent);
                    var level = parentData.items[0].data.level + 1;

                    ChartPanel.updateNode(nodeid, parent, level);
                }

                else {    // stop drag event if event not dropped in the proper day
                    Wtf.MessageBox.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.common.node.cannot.appended.parent.child.node"));
                    e.stopEvent();
                }
            }

            else {
                t = (t.id);

                var UnmappedPanel = Wtf.getCmp(companyid + "unmappedContainer");
                var parentTo = t.split("block_")[1];
                var userid = this.id.split("blockUnMapped_")[1];

                parentData = ChartPanel.mappedNodeStore.query("nodeid", parentTo);
                level = parentData.items[0].data.level + 1;

                UnmappedPanel.insertNewNode(userid, parentTo, level);
            }
        }
        else        // stop drag event if event not dropped in the proper day
            e.stopEvent();
    }
});

Wtf.reg("ChartDragPlugin", Wtf.ChartDDProxy);
