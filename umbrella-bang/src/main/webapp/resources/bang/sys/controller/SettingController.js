Ext.define("Q.sys.controller.SettingController", {
    extend: "Ext.app.Controller",

    requires: [
        "Q.sys.view.util.SettingWindow"
    ],

    init: function () {
        var me = this;

        me.control({
            "settingwindow": {
                // 窗口显示时候初始化数据
                show: this.onSettingWinShow,

                // 隐藏时候去除遮罩
                hide: function () {
                    Ext.getBody().unmask();
                }
            },
            // 选择主题
            "settingwindow field[name=themeName]": {
                select: this.onSelectTheme
            },
            // 最小化
            "settingwindow tool[type=minus]": {
                click: this.minimizeWindow
            },
            // 应用系统设置
            "settingwindow button[action=apply]": {
                click: this.applySetting
            },
            // 返回
            "settingwindow button[action=back]": {
                click: this.minimizeWindow
            },
            // 注销
            "settingwindow button[action=logout]": {
                click: this.logout
            }
        });

    },

    onSettingWinShow: function (settingWin) {
        var formPanel = settingWin.getSettingForm();

        Q.setFieldValue(formPanel, Q.getSystemConfiguration());

        Ext.getBody().mask();
    },

    onSelectTheme: function (c, r, e) {
        var formPanel = c.up("form"),
            vm = formPanel.getViewModel();

        var link = r.get("link");

        vm.set({
            themeLink: link
        });

        Ext.util.CSS.swapStyleSheet("theme", link);
    },

    logout: function (btn) {
        var settingWin = btn.up("settingwindow"),
            el = settingWin.getEl();

        el.mask($("message.logout"));
        Q.ajax({
            url: "logout",
            success: function (res, opts) {
                document.location.href = "";
            },
            failure: function (res, opts) {
                Q.tip(res.responseText);
                el.unmask();
            }
        });

    },

    applySetting: function (btn) {
        var settingWin = btn.up("settingwindow"),
            formPanel = settingWin.getSettingForm(),
            form = formPanel.getForm();

        Q.setSystemConfiguration(form.getValues());
    },

    getSettingWin: function () {
        var me = this;
        if (!me.settingWindow) {
            var settingWindow = me.settingWindow = Ext.create("Q.sys.view.util.SettingWindow");
        }
        return me.settingWindow;
    },

    minimizeWindow: function (btn) {
        var settingWin = btn.up("settingwindow");
        settingWin.hide();
    }

});