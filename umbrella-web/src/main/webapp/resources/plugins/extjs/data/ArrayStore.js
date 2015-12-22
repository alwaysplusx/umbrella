Ext.define("Ext.bang.data.ArrayStore", {
    extend: "Ext.data.ArrayStore",
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