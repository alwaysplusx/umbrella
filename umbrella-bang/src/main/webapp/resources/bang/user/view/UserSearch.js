Ext.define("Q.user.view.UserSearch", {
    extend: "Ext.Window",
    xtype: "usersearch",
    alias: "widget.usersearch",
    title: $("label.user.search"),
    layout: "border",
    height: 300,
    width: 500,
    border: false,
    closeAction: "hide",
    items: [{
        xtype: "textfield",
        fieldLabel: "登录用户",
        value: "wuxii"
    }],
    buttons: [{
        text: $("button.search"),
        action: "search->page"
    }, {
        text: $("button.back"),
        action: "page"
    }]
});