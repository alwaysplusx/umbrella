Ext.define("Q.sys.view.Viewport", {
    extend: "Ext.container.Viewport",
    layout: "border",
    requires: [
        "Q.sys.view.Content",
        "Q.sys.view.Header",
        "Q.sys.view.Navigation",
        "Q.sys.view.util.SettingWindow"
    ],
    initComponent: function () {
        var me = this;
        
        Ext.apply(me, {
            items: [{
                xtype: "syscontent"
            }, {
                xtype: "sysheader"
            }, {
                xtype: "sysnavigation"
            }]
        });

        this.callParent(arguments);
    }
});