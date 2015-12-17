Ext.define("Q.user.controller.UserController", {
    extend: "Ext.bang.app.Controller",

    // use stores auto create dependency store
    // stores: ["UserStore"],
    models: ["UserModel"],

    init: function () {
        var me = this;

        me.control({
            "* button[action!=undefined]": {
                click: this.onActionButtonClick
            },
            // 双击列表
            "grid": {
                itemclick: this.onItemClick,
                itemdblclick: this.onItemDoubleClick
            }

        });

    },
    listeners: {
        submitstage: function (flag, next, result) {
            var me = this,
                mv = me.getMainView();
            if (next.isPage() && flag) {
                var pageView = mv.getStageView(next);
                pageView.getStore().reload();
            }
        }
    },

    switchDataMapper: [
        {from: "page", to: ["edit", "view"], methodName: "getPageData"}
    ],

    getPageData: function (view, toView, toStage) {
        var selection = view.getSelection();
        return selection[0].getData();
    },

    // 使用mapper或者直接在button上配置方法名称
    submitMapper: [
        {stage: "delete", methodName: "deleteRecord"},
        {stage: ["add", "edit"], methodName: "submitData"}
    ],

    submitData: function (view, stage, next) {
        var me = this;

        if (!view.isValid()) {
            return false;
        }

        var form = view.getForm();

        // FIXME confirm与ajax整合成一个方法
        Q.confirm({
            message: "确认提交",
            ok: function () {
                form.submit({
                    url: view.getUrl(),
                    method: "POST",
                    success: function (form, action) {
                        Q.tip("提交成功");
                        me.changeStageAndView(next);
                        me.fireEvent("submitstage", true, next, action.result);
                    },
                    failure: function (form, action) {
                        Q.tip("提交失败");
                        me.fireEvent("submitstage", false, next, action.result);
                    }
                });
            },
            otherwise: function () {
                me.changeStageAndView(next);
            }
        });

    },

    deleteRecord: function (view, stage, next) {
        var me = this;

        var selection = view.getSelection(),
            record = selection[0];

        if (record) {
            Q.confirm({
                message: "确认删除",
                ok: function () {
                    Q.ajax({
                        method: "GET",
                        url: "user/delete/" + record.get("userId"),
                        success: function (result, opts) {
                            Q.tip("删除成功");
                            me.changeStageAndView(next);
                            me.fireEvent("submitstage", true, next, result);
                        },
                        failure: function (result, opts) {
                            Q.tip("删除失败");
                            me.fireEvent("submitstage", false, next, result);
                        }
                    });
                },
                otherwise: function () {
                    me.changeStageAndView(next);
                }
            });
        }
    }

});