Ext.define("Q.sys.view.Content", {
    extend: "Ext.tab.Panel",
    region: 'center',
    alias: "widget.syscontent",
    border: false,
    defaults: {
        border: false
    },
    items: [{
        rtl: false,
        title: 'Bogus Tab',
        html: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.'
    }]
});