Ext.define("Q.user.view.UserPage", {
    extend: "Ext.bang.grid.PagePanel",
    xtype: "userpage",
    alias: "widget.userpage",
    region: "center",
    border: false,
    rowNumber: true,

    stage: "page",
    url: "user",
    method: "POST",
    submitMask: $("message.submit"),

    getSubmitData: function () {
        var me = this,
            stage = me.stage;
        if (stage === "delete") {
            var selections = me.getSelection();
            var ids = [];
            Q.each(selections, function (r, p) {
                ids.push(r.get("userId"));
            });
            return {ids: ids};
        }
    },

    tbar: [{
        text: $("button.add"),
        action: "add",
        iconCls: "icon-add"
    }, {
        text: $("button.edit"),
        action: "edit",
        iconCls: "icon-edit",
        inStage: function(){
            // FIXME changestage before load event
            var grid = this.up("grid");
            return grid.getSelection().length !== 0;
        },
        disabled: true
    }, {
        text: $("button.delete"),
        action: "delete->page",
        iconCls: "icon-delete",
        disabled: true
    }, {
        text: $("button.view"),
        action: "view",
        iconCls: "icon-view",
        disabled: true
    }, {
        text: $("button.search"),
        action: "search",
        iconCls: "icon-search"
    }, {
        text: $("button.search.clear"),
        hidden: true,
        action: "search.clear",
        iconCls: "icon-clear"
    }],
    store: "Q.user.store.UserStore"
});