Ext.define("Q.user.view.Viewport", {
    extend: "Ext.bang.container.Viewport",

    refViews: [
        {ref: "pageView", selector: "userpage", stage: ["page", "delete"]},
        {ref: "editView", selector: "useredit", stage: ["add", "save", "edit", "view"]},
        {ref: "searchWin", selector: "usersearch", stage: "search"}
    ],

    requires: [
        "Q.user.view.UserPage",
        "Q.user.view.UserEdit",
        "Q.user.view.UserSearch"
    ],

    layout: "border",

    stage: "page",

    initComponent: function () {
        var me = this,
            page = me.getPageView();

        Ext.apply(me, {
            items: [page]
        });

        this.callParent(arguments);
    }

});