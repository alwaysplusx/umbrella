Ext.define("Q.user.view.UserEdit", {
    extend: "Ext.bang.form.Panel",
    xtype: "useredit",
    alias: "widget.useredit",
    region: "center",
    border: false,

    url: "user",
    method: "POST",
    submitMask: $("message.submit"),

    tbar: [{
        text: $("button.save"),
        iconCls: "icon-save",
        action: {
            stageName: "none",
            nextStageName: "page",
            type: "submit"
        }, // "none->page"
        supportStage: "add,edit"
    }, {
        text: $("button.back"),
        iconCls: "icon-back",
        action: "page",
        supportStage: "all"
    }],

    items: [{
        layout: "column",
        border: false,
        padding: 10,
        defaults: {
            xtype: "fieldset",
            layout: "anchor",
            margin: 5,
            border: false,
            defaults: {
                xtype: "textfield",
                anchor: "60%"
            }
        },
        items: [{
            columnWidth: .33,
            items: [{
                fieldLabel: $("user.label.username"),
                name: "username"
            }, {
                fieldLabel: $("user.label.nickname"),
                name: "nickname"
            }, {
                fieldLabel: $("user.label.password"),
                name: "password"
            }]
        }, {
            columnWidth: .33,
            items: [{
                fieldLabel: $("user.label.email"),
                name: "email"
            }]
        }, {
            columnWidth: .33,
            items: [{
                fieldLabel: $("user.label.phoneNumber"),
                name: "phoneNumber"
            }]
        }]
    }, {
        border: false,
        defaults: {
            xtype: "hidden",
            border: false
        },
        items: [{
            name: "userId"
        }]
    }]

});