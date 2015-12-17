Ext.define('Q.user.model.UserModel', {
    extend: 'Ext.data.Model',
    fields: [{
        text: $("user.label.userId"),
        name: 'userId',
        type: 'int',
        cm: {
            ordinal: 0,
            hidden: true,
            width: 120,
            sortable: true
        }
    }, {
        text: $("user.label.username"),
        name: 'username',
        type: 'string',
        cm: {
            ordinal: 1,
            width: 120,
            sortable: true
        }
    }, {
        text: $("user.label.nickname"),
        name: 'nickname',
        type: 'string',
        cm: {
            ordinal: 2,
            width: 120,
            sortable: true
        }
    }, {
        text: $("user.label.password"),
        name: 'password',
        type: 'string',
        cm: {
            ordinal: 3,
            width: 120,
            sortable: true
        }
    }, {
        text: $("user.label.email"),
        name: 'email',
        type: 'string',
        cm: {
            flex: 1,
            sortable: true
        }
    }, {
        text: $("user.label.phoneNumber"),
        name: 'phoneNumber',
        type: 'string',
        cm: {
            ordinal: 5,
            width: 120,
            sortable: true
        }
    }]
});