Ext.define("Ext.bang.data.ListStore", {
    extend: "Ext.data.JsonStore",
    constructor: function (config) {
        var me = this,
            url;

        if (me.url) {
            url = me.url;
        }

        config = Ext.applyIf({
            proxy: {
                type: "ajax",
                url: url,
                reader: {
                    type: "json",
                    totalProperty: "totalCount",
                    rootProperty: "records"
                }
            }
        }, config);
        
        this.callParent([config]);
    },
    getFields: function () {
        var me = this,
            fields = me.callParent();

        if (fields === null) {
            fields = [];
        }

        var model = this.getModel();
        if (model) {
            fields = fields.concat(model.getFields());
        }
        return fields;
    }
});