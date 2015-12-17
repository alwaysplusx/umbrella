Ext.define('Q.bang.sys.LoginForm', {
    extend: 'Ext.form.Panel',
    xtype: 'formlogin',
    frame: true,
    title: $("label.login"),
    width: 320,
    bodyPadding: 10,
    defaultType: 'textfield',
    initComponent: function () {
        
        Ext.apply(this, {
            defaults: {
                anchor: '100%',
                labelWidth: 120
            }
        });

        this.callParent();
    },
    submit: function () {
        var formPanel = this;
        var form = this.form;
        if (!form.isValid()) {
            return false;
        }
        var el = formPanel.getEl();
        el.mask($("message.login"));

        this.form.submit({
            url: "login",
            method: "POST",
            success: function (form, action) {
                var json = action.result;
                if (json.success) {
                    document.location.href = "";
                } else {
                    Q.error(json.message);
                }
            },
            failure: function (form, action) {
                el.unmask();
                if (action.result) {
                    Q.error(action.result);
                } else {
                    Q.error(action.response.responseText)
                }
            }
        });
    },
    items: [{
        fieldLabel: $("label.username"),
        name: 'username',
        allowBlank: false,
        emptyText: $("text.username")
    }, {
        fieldLabel: $("label.password"),
        name: 'password',
        allowBlank: false,
        emptyText: $("text.password"),
        inputType: 'password'
    }, {
        xtype: 'checkbox',
        fieldLabel: $("label.remember"),
        name: 'remember'
    }],
    buttonAlign: "center",
    buttons: [{
        text: $("label.register"),
        handler: function (b, e) {
        }
    }, {
        text: $("label.login"),
        handler: function (b, e) {
            var loginForm = b.findParentByType("formlogin");
            loginForm.submit();
        }
    }]
});