Ext.define("Ext.bang.form.Panel", {
    extend: "Ext.form.Panel",
    /**
     * 表单相关联需要提交的数据
     */
    stage: "add",

    initComponent: function () {
        this.stage = Q.Stage.valueOf(this.stage);
        this.callParent(arguments);
    },

    getStage: function () {
        return this.stage;
    },

    setStage: function (stage) {
        this.stage = stage;
    },

    getUrl: function (stage) {
        var me = this,
            url = me.url,
            stage = stage || this.stage;

        if (!url) {
            throw new Error("form not set url property");
        }

        // IE 9.x not has string.endsWith method
        if (url.lastIndexOf("/") !== url.length) {
            url += "/";
        }

        return stage.isAdd() ? url + "save" : url + "update";
    },

    /**
     * 给表单加载数据, 数据来源可以是store.record, 或直接是数据的json对象
     * @param r
     */
    loadRecord: function (r) {
        var me = this,
            form = me.getForm();
        form.setValues(r.data);
        return r.data;
    },

    submit: function (opt) {
        var callback = opt.callback,
            success = opt.success,
            failure = opt.failure;

        var config = Ext.applyIf({
            success: function (form, action) {
                var result = action.result;

                if (success) {
                    success(result, Ext.apply({form: form}, action));
                }

                if (callback) {
                    callback(result, Ext.apply({form: form}, action));
                }

            },
            failure: function (form, action) {
                var result = action.result;

                if (failure) {
                    failure(result, Ext.apply({form: form}, action));
                }

                if (callback) {
                    callback(result, Ext.apply({form: form}, action));
                }

            }
        }, opt);

        this.form.submit(opt);
    },

    /**
     * 重置表单
     */
    reset: function () {
        var me = this,
            form = me.getForm();
        form.reset();
    },

    getRefData: function () {
        return {};
    }

});