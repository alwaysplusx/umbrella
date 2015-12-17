Ext.define("Q.sys.view.Navigation", {
    extend: "Ext.panel.Panel",
    xtype: "sysnavigation",
    alias: "widget.sysnavigation",
    region: 'west',
    border: false,
    title: 'Navigation',
    width: 200,
    split: true,
    maxWidth: 500,
    collapsible: true,
    floatable: false,
    layout: "accordion",
    items: [{
        title: "System Settings",
        xtype: "treepanel",
        rootVisible: false,
        store: Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true,
                children: [
                    {text: "User Managerment", link: "user", leaf: true},
                    {text: "Module Management", link: "module", leaf: true}
                ]
            }
        })
    }, {
        xtype: "treepanel",
        title: "Application Settings",
        rootVisible: false
    }, {
        xtype: "treepanel",
        title: "Business"
    }, {
        xtype: "treepanel",
        title: "Test",
        rootVisible: false,
        store: Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true,
                children: [
                    {text: "Test Center", link: "test", leaf: true}
                ]
            }
        })
    }]
});