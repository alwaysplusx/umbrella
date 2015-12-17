Ext.define("Ext.bang.state.CookieProvider", {
    extend: "Ext.state.CookieProvider",
    prefix: "",
    encodeValue: function (v) {
        return v;
    },
    decodeValue: function (v) {
        return v;
    },
    get: function (name, defaultValue) {
        var ret = this.readCookies()[name];
        return ret === undefined ? defaultValue : ret;
    }
});