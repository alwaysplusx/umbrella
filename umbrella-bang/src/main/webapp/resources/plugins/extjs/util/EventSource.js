Ext.define("Ext.bang.util.EventSource", {
    source: null,
    event: null,
    eOpts: null,
    constructor: function (config) {
        Ext.apply(this, config);
    },
    isButton: function () {
        return sources instanceof Ext.Button;
    },

    isPageItem: function () {
        return this.source instanceof Ext.data.Model;
    },

    getSource: function () {
        return this.source;
    },

    getEvent: function () {
        return this.event;
    },

    getEventOptions: function () {
        return this.eOpts;
    }

});