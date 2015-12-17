Ext.define("Q.user.store.UserStore", {
    extend: "Ext.bang.data.ListStore",
    model: "Q.user.model.UserModel",
    autoLoad: Q.isAutoLoad(),
    pageSize: 20,
    url: "user/page"
});