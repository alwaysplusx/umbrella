Ext.define("Q.sys.controller.SystemController", {
    extend: "Ext.app.Controller",

    requires: [
        "Q.sys.view.util.SettingWindow"
    ],

    stores: ["ThemeStore"],

    /**
     * auto create getter/setter method
     */
    refs: [
        {ref: "content", selector: "syscontent"},
        {ref: "header", selector: "sysheader"},
        {ref: "navigation", selector: "sysnavigation"}
    ],

    constructor: function () {
        console.info(this);
        this.callParent(arguments);
    },

    init: function () {
        var me = this;

        me.control({

            "sysheader tool[type=gear]": {
                click: this.showSystemSettingWin
            },

            "sysnavigation panel[xtype=treepanel]": {
                cellclick: this.navCellClick
            }
        })

    },

    showSystemSettingWin: function () {
        this.getSystemSettingWin().show();
    },

    getSystemSettingWin: function () {
        var me = this,
            settingWin = me.settingWin;

        if (!settingWin) {
            var settingController = Ext.create("Q.sys.controller.SettingController");
            settingController.init();

            settingWin = me.settingWin = settingController.getSettingWin();
        }

        return settingWin;
    },

    navCellClick: function (me, td, c, r, tr, row, e, opts) {
        if (!r.get("leaf")) {
            return;
        }

        var me = this,
            app = me.application,
            mv = app.getMainView(),
            content = mv.down("syscontent");

        var title = r.get("text");
        var tabs = content.queryBy(function (v) {
            if (v.getTitle && v.getTitle() == title) {
                return true;
            }
            return false;
        });

        if (tabs.length == 0) {
            var tab = content.add(Ext.create("Ext.Panel", {
                title: r.get("text"),
                fitToFrame: true,
                closable: true,
                html: "<iframe src='" + r.get("link") + "' frameborder='0' width='100%' height='100%'></iframe>"
            }));
            content.setActiveTab(tab);
        } else {
            content.setActiveTab(tabs[0]);
        }
    },
    test: function () {
        console.info("test");
    }

});