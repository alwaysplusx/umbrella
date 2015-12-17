Ext.define("Q.sys.view.Header", {
    extend: "Ext.panel.Panel",
    xtype: "sysheader",
    alias: "widget.sysheader",
    region: "north",
    border: false,
    title: "Layout Window with title <em>after</em> tools",
    header: {
        titlePosition: 0,
        titleAlign: "center"
    },
    tools: [{
        type: "gear"
    }]
});