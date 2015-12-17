/**
 * Ext 组件扩展
 */
Ext.define("Ext.bang.grid.PagePanel", {
    xtype: "pagegrid",
    extend: 'Ext.grid.Panel',

    stage: "page",

    initComponent: function () {
        var me = this,
            store;

        try {
            store = me.store = Ext.create(eval(me.store));
        } catch (e) {
            store = me.store = Ext.data.StoreManager.lookup(me.store || 'ext-empty-store');
        }

        var columns = me.columns = me.getListColumns(store);

        if (!me.bbar) {
            var bbar = me.bbar = [{
                xtype: 'pagingtoolbar',
                border: false,
                pageSize: me.pageSize || Q.get("pageSize"),
                store: me.store,
                displayInfo: true
            }];
        }

        me.stage = Q.Stage.valueOf(me.stage);

        this.callParent(arguments);
    },

    getStage: function () {
        return this.stage;
    },

    setStage: function (stage) {
        this.stage = stage;
    },

    getUrl: function (stage) {
        var me = this,
            url = me.url;

        if (!url) {
            throw new Error("form not set url property");
        }

        // IE 9.x not has string.endsWith method
        if (url.lastIndexOf("/") !== url.length) {
            url += "/";
        }

        return stage.isDelete() ? url + "delete" : url + "list";
    },

    isValid: function () {
        var me = this,
            stage = me.stage;

        if (stage.isDelete()) {
            var selection = me.getSelection();
            if (selection.length === 0) {
                Q.tip($("message.select.data"));
                return false;
            } else if (selection.length > 1) {
                Q.tip($("message.select.onlyOne"));
                return false;
            }
        }

        return true;
    },

    getSubmitData: function () {
        return this.getStageData(this.stage);
    },

    getStageData: function (stage) {
        var me = this,
            stage = stage || me.stage;

        if (!stage) {
            throw new Error("stage not set");
        }

        if (stage.isDelete()) {
            var sel = me.getSelection();
            return sel[0] ? sel[0].getData() : null;
        }

        return null;
    },

    getListColumns: function (store) {
        var me = this;
        store = store || Ext.data.StoreManager.lookup(me.store || 'ext-empty-store');

        var columns = [];
        var model = store.getModel();

        Ext.each(model.getFields(), function (v, p) {
            if (!v.generated) {

                var column = {
                    text: v.text,
                    dataIndex: v.name,
                    width: v.width,
                    type: v.type,
                    ordinal: v.cm || v.cm.ordinal || p
                };

                columns.push(Ext.apply(column, v.cm));
            }
        });
        // 排序
        columns.sort(function (a, b) {
            return a.ordinal - b.ordinal;
        });

        if (me.rowNumber) {
            columns.splice(0, 0, {xtype: 'rownumberer', width: 30});
        }

        return columns;
    },

    listeners: {
        /**
         * 列表刷新时候清除选择的行
         */
        afterrender: function () {
            var me = this,
                sm = me.getSelectionModel(),
                store = me.getStore();
            store.on("beforeload", function () {
                sm.clearSelections();
            });
        }
    }
});