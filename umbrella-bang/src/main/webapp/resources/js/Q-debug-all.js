var Q = {
    // array util function
    each: function (array, fn, reverse) {
        if (array.constructor === Array || array instanceof Array) {
            var i, ln = array.length;
            if (reverse !== true) {
                for (i = 0; i < ln; i++) {
                    if (fn.call(array[i], array[i], i, array) === false) {
                        return i;
                    }
                }
            } else {
                for (i = ln - 1; i > -1; i--) {
                    if (fn.call(array[i], array[i], i, array) === false) {
                        return i;
                    }
                }
            }
            return true;
        } else if (typeof array === 'object') {
            for (var a in array) {
                if (fn.call(array[i], array[a], a, array) === false) {
                    return a;
                }
            }
        }
    },
    // message tips
    /**
     *
     * @param text
     * @param title
     */
    tip: function (text, title) {
        Q.tips(text, title);
    },
    tips: function (text, title) {

        var win = new Ext.Window({
            width: 150,
            height: 130,
            shadow: false,
            html: text,
            title: title || '提示'
        });

        win.on("show", function () {
            var el = this.getEl();

            el.slideIn('b', {
                easing: 'easeOut',
                callback: function () {
                    Ext.defer(this.close, 2000, this);
                },
                scope: this,
                duration: 1000
            });

            el.alignTo(Ext.getBody(), 'br-br');
        });

        win.on("beforeclose", function () {
            this.el.slideOut('b', {
                easing: 'easeOut',
                callback: function () {
                    this.close();
                },
                scope: this,
                duration: 1000
            });
            return false;
        });

        win.show();

        return win;
    },
    info: function (text, title) {
        Ext.Msg.show({
            title: title || "提示",
            msg: text,
            iconCls : '',
            buttons: Ext.Msg.OK
        });
    },
    warn: function (text, title) {

    },
    warning: function (text, title) {

    },
    error: function (text, title) {

    },
    // Dialog
    confirm: function (text, fn) {

    }
}

var $ = Q;