Ext.define("Ext.bang.window.MessageBox", {
    extend: "Ext.window.MessageBox",
    reconfigure: function (cfg) {
        this.callParent([cfg]);

        var me = this,
            textArea, textField;

        textArea = me.textArea;
        textField = me.textField;

        if (cfg.prompt || cfg.multiline) {
            me.multiline = cfg.multiline;
            if (cfg.multiline) {
                textArea.allowBlank = cfg.allowBlank !== false;
                textField.allowBlank = cfg.allowBlank !== false;
            } else {
                textArea.allowBlank = cfg.allowBlank !== false;
                textField.allowBlank = cfg.allowBlank !== false;
            }
        }
    },
    btnCallback: function (btn, event) {
        var me = this,
            value, field;
        // If this is caused by a keydown event (eg: SPACE on a Button), then the
        // hide will throw focus back to the previously focused element which will
        // then recieve an unexpected keyup event.
        // So defer callback handling until the upcoming keyup event.
        if (event && event.type === 'keydown' && !event.isSpecialKey()) {
            event.getTarget(null, null, true).on({
                keyup: function (e) {
                    me.btnCallback(btn, e);
                },
                single: true
            });
            return;
        }
        if (me.cfg.prompt || me.cfg.multiline) {
            if (me.cfg.multiline) {
                field = me.textArea;
            } else {
                field = me.textField;
            }
            // 点击确定当时没有输入数据
            if (btn.itemId === "yes" && !field.isValid()) {
                return;
            }
            value = field.getValue();
            field.reset();
        }
        // Component.onHide blurs the active element if the Component contains the active element
        me.hide();
        me.userCallback(btn.itemId, value, me.cfg);
    }
});