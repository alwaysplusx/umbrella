var config = {
    title: "抬头",
    allowBlank: false,
    multiline: true,
    message: "提示消息",
    yes: function () {
        console.info("yes");
    },
    no: function () {
        console.info("no");
    },
    callback: function () {
        console.info("callback");
    }
};

Ext.define('Ext.window.MessageBox', {
    extend: 'Ext.window.Window',

    progress: function (title, message, progressText) {
        if (Ext.isString(title)) {
            title = {
                title: title,
                message: message,
                progress: true,
                progressText: progressText
            };
        }
        return this.show(title);
    }
}, function (MessageBox) {
    /**
     * @class Ext.MessageBox
     * @alternateClassName Ext.Msg
     * @extends Ext.window.MessageBox
     * @singleton
     * @inheritdoc Ext.window.MessageBox
     */
        // We want to defer creating Ext.MessageBox and Ext.Msg instances
        // until overrides have been applied.
    Ext.onInternalReady(function () {
        Ext.MessageBox = Ext.Msg = new MessageBox();
    });
});