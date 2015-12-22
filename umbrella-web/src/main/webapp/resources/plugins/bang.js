(function () {

    Array.prototype.contains = function (o) {
        for (var i = 0, max = this.length; i < max; i++) {
            if (this[i] === o) {
                return true;
            }
        }
        return false;
    };

    // 添加基础原型链方法
    Date.prototype.format = function (format) {
        if (Ext && Ext.Date && Ext.Date.format) {
            return Ext.Date.format(this, format);
        }
        return this.toLocaleDateString();
    };

    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/\{(\d+)\}/g, function (m, i) {
            return args[i];
        });
    };

    String.format = function () {
        var args = [].slice.apply(arguments);
        var text = arguments[0];
        var args = args.slice(1);
        return text.replace(/\{(\d+)\}/g, function (m, i) {
            return args[i];
        });
    };

}());

/**
 * 基础工具
 */
var Q = function () {
    var loc = locale;
    if (typeof loc === "undefined") {
        return name;
    }
    var args = [].slice.apply(arguments);

    var name = args[0],
        args = args.slice(1);

    var text = loc[name];

    if (typeof text === "string" && text.trim() !== "") {
        return String.format(text, args);
    }

    return name;
};

// for shortcut
var $ = Q;

(function () {

    var cp;
    Ext.require("Ext.bang.state.CookieProvider", function () {
        cp = Ext.create("Ext.bang.state.CookieProvider");
    });

    Q.each = function (array, fn, reverse) {
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
    };

    /**
     * 分割字符文本为数据
     *
     * @param text 字符文本
     * @param split 分隔符
     * @param trim 去除空格
     * @returns {*}
     */
    Q.split = function (text, split, trim) {
        if (Ext.isEmpty(text)) {
            return [];
        }
        if (Ext.isArray(text)) {
            return text;
        }
        var arr = text.split(split);
        if (trim) {
            Ext.each(arr, function (v, p) {
                arr[p] = v.trim();
            });
        }
        return arr;
    };

    /**
     * 合并数组为字符串
     *
     * @param array
     * @param split
     * @param suffix
     */
    Q.merge = function (array, split, suffix) {
        if (Ext.isEmpty(array)) {
            return "";
        }
        if (Ext.isString(array)) {
            return array;
        }

        var result = "",
            split = split || ",",
            suffix = suffix || "";

        Q.each(array, function (v, p) {
            result += v;

            if (p < array.length - 1) {
                result += split + suffix;
            }

        });

        return result.trim();
    };

    Q.isEmpty = function (value, allowEmptyObject, allowEmptyString) {
        if (Ext.isEmpty(value, allowEmptyString)) {
            return true;
        }
        if (!allowEmptyObject && typeof value === "object") {
            return Ext.isEmpty(Ext.encode(value), allowEmptyString);
        }
        return false;
    };

    Ext.require("Ext.bang.window.MessageBox", function () {

        var messageBox = Ext.create("Ext.bang.window.MessageBox");

        Q.confirm = function (config, ok, otherwise) {

            if (!config) {
                config = {};
            }

            if (Ext.isString(config)) {
                config = {
                    message: config
                };
            }

            var otherwise = config.otherwise || otherwise;
            ok = config.ok || config.yes || ok;

            Ext.apply(config, {
                title: config.title || $("label.info"),
                message: config.message,
                buttons: config.buttons || messageBox.YESNO,
                icon: config.buttons || messageBox.QUESTION,
                callback: function (btn, msg, cfg) {
                    if (btn === "yes" && ok) {
                        ok.call(this, msg, cfg);
                    } else {
                        otherwise.call(this, btn, msg, cfg);
                    }
                }
            });

            messageBox.confirm(config);

        };

        Q.info = function (message, fn) {
            messageBox.confirm({
                title: $("label.info"),
                message: message,
                buttons: messageBox.YES,
                icon: messageBox.INFO,
                callback: fn
            });
        };

        Q.warning = function (message, fn) {
            messageBox.confirm({
                title: $("label.warn"),
                message: message,
                buttons: messageBox.YES,
                icon: messageBox.WARNING,
                callback: fn
            });
        };

        Q.warn = Q.warning;

        Q.error = function (message, fn) {
            messageBox.confirm({
                title: $("label.error"),
                message: message,
                buttons: messageBox.YES,
                icon: messageBox.ERROR,
                callback: fn
            });
        };

    });

    Q.tip = function (text, title) {

        var win = new Ext.Window({
            width: 150,
            height: 130,
            shadow: false,
            // baseCls: "",
            html: text,
            title: title || $("label.tip")
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
    };

    Q.ajax = function (config) {
        var success = config.success,
            failure = config.failure,
            callback = config.callback;

        config = Ext.apply(config, {
            success: function (response, opt) {
                if (success) {
                    var result = Ext.decode(response.responseText);
                    opt.response = response;

                    if (result.success) {
                        success.call(result, result, opt);
                    } else {
                        failure.call(result, result, opt);
                    }
                }
            },
            failure: function (response, opt) {
                if (failure) {
                    var result = Ext.decode(response.responseText);
                    opt.response = response;

                    failure.call(result, result, opt);
                }
            },
            callback: function (response, opt) {
                if (callback) {
                    var result = Ext.decode(response.responseText);
                    opt.response = response;

                    callback.call(result, result, opt);
                }
            }
        });

        Ext.Ajax.request(config);
    };

    // private method

    var _GET_DEFAULT_CONFIG = function () {
        return {
            link: "http://cdn.bootcss.com/extjs/6.0.0/classic/theme-classic/resources/theme-classic-all-debug.css",
            autoLoad: true,
            doubleClickAction: "view"
        };
    };

    var set = function (key, value) {
        cp.setCookie(key, value);
    };

    // public method

    /**
     * 获取系统配置信息
     * @param key
     */
    Q.get = function (key, defaultValue) {
        return cp.get(key, defaultValue);
    };

    Q.getSystemConfiguration = function () {
        var config = Ext.decode(Q.get("BANG_CONFIG")) || {};
        // 如果某些属性未设置则用默认值替代
        return Ext.applyIf(config, _GET_DEFAULT_CONFIG())
    };

    Q.setSystemConfiguration = function (config) {
        set("BANG_CONFIG", Ext.encode(config));
        set("BANG_THEME", config.themeLink);
    };

    Q.getThemeUrl = function () {
        return Q.get("BANG_THEME");
    };

    Q.isAutoLoad = function () {
        var config = Q.getSystemConfiguration(),
            autoLoad = config.autoLoad || "autoLoad";

        return (typeof autoLoad === "boolean") ? autoLoad : autoLoad === "autoLoad" ? true : false;
    };

    Q.getDoubleClickAction = function () {
        var config = Q.getSystemConfiguration();
        return config.doubleClickAction;
    };

    Q.doubleClickDisabled = function () {
        return Q.getDoubleClickAction() === "disable";
    };

    /**
     * 定义stage类
     * @param config
     * @constructor
     */
    Q.Stage = function (config) {
        var me = this;
        if (typeof config === "string") {
            var index = config.indexOf("->");
            if (index != -1) {
                me.stageName = config.substring(0, index);
                me.nextStage = new Q.Stage({
                    preStage: this,
                    stageName: config.substring(index + 2)
                });
                me.type = "submit";
            } else {
                me.stageName = config;
            }
        } else {

            var nextStage = config.next || config.nextStage || config.nextStageName;

            if (nextStage && !(nextStage instanceof Q.Stage)) {
                nextStage = new Q.Stage(nextStage);
                nextStage.preStage = me;
            }

            Ext.apply(config, {
                stageName: config.stage || config.stageName,
                nextStage: nextStage
            });

            Ext.apply(me, config);
        }
    };

    /**
     * 添加原型方法
     * @type {{isNeedData: Function, isNew: Function, isEdit: Function, isView: Function, isStageOf: Function}}
     */
    Q.Stage.prototype = {
        stageName: "page",
        type: "redirect",
        preStage: null,
        nextStage: null,
        useData: false,
        resetData: true,
        requireData: function () {
            return this.useData === true ? true : false;
        },
        requireRest: function () {
            return true;
        },
        /**
         * 阶段切换为提交页面
         * @returns {boolean}
         */
        isSubmit: function () {
            return this.type === "submit";
        },
        /**
         * 阶段切换为页面跳转
         * @returns {boolean}
         */
        isRedirect: function () {
            return this.type === "redirect";
        },
        /**
         * 切换阶段目标的显示类型为直接调用show方法
         *
         * @returns {boolean}
         */
        isShow: function () {
            return this.type === "show";
        },
        isTypeOf: function (type) {
            return this.type === type;
        },
        /**
         * 检测当前的stageName是否相同
         *
         * @param name stage name or stage
         * @returns {boolean}
         */
        isStageOf: function (stage) {
            if (!stage) {
                return false;
            }
            if (this === stage) {
                return true;
            }
            if (stage instanceof Q.Stage) {
                return this.stageName = stage.stageName;
            }
            return this.stageName === stage;
        },
        getName: function () {
            return this.stageName;
        },
        hasNext: function () {
            return !Ext.isEmpty(this.nextStage);
        },
        hasPre: function () {
            return !Ext.isEmpty(this.preStage);
        },
        pre: function () {
            return this.preStage;
        },
        next: function () {
            return this.nextStage;
        }
    };

    var stages = {
        // just for one button use to submit currentView
        NONE: {
            stageName: "none"
        },

        // 跳转到新建页面
        ADD: {
            stageName: "add"
        },

        // 跳转到编辑页面
        EDIT: {
            stageName: "edit",
            useData: true
        },

        // 提交删除数据
        DELETE: {
            stageName: "delete",
            type: "submit",
            nextStageName: "page"
        },

        // 跳转到查看页面
        VIEW: {
            stageName: "view",
            useData: true
        },

        // 跳转到列表界面
        PAGE: {
            stageName: "page"
        },

        // 跳转到查询界面
        SEARCH: {
            stageName: "search",
            reset: true,
            type: "show"
        }
    };

    // execute immediately, 动态添加判断方法
    Q.each(stages, function (v, k) {
        var name = "is" + Ext.String.capitalize(k.toLowerCase());

        if (Q.Stage.prototype[name]) {
            console.warn("Q.Stage." + name + " function already exists!");
        }

        Q.Stage.prototype[name] = function () {
            return this.stageName === v.stageName;
        };

    });

    /**
     * 枚举方法, 在已经存在的Q.stage中找寻stageName相同的stage， 如果未找到返回null
     *
     * @param stageName
     * @returns {*}
     */
    Q.Stage.valueOf = function (config) {
        if (config instanceof Q.Stage) {
            return config;
        }
        // 传入配置属性, 直接new一个对象
        if (Ext.isObject(config)) {
            return new Q.Stage(config);
        }

        var result = null;

        Q.each(stages, function (v, k) {
            if (v.stageName === config) {
                result = new Q.Stage(v);
                return false;
            }
        });

        return result;
    };

    Q.getFieldValue = function (field) {
        var value = field.getValue(),
            fieldName = field.name;

        if (Ext.isEmpty(value)) {
            return value;
        }

        if (field instanceof Ext.form.CheckboxGroup) {
            value = value[fieldName];
            return Q.merge(value, ",");
        }

        if (field instanceof Ext.form.RadioGroup) {
            return value[fieldName];
        }

        return value;
    };

    Q.setFieldValue = function (field, value) {

        if (field instanceof Ext.panel.Panel) {
            var fields = field.query("field");
            Q.each(fields, function (f, p) {
                Q.setFieldValue(f, value[f.name]);
            });
            return;
        }

        if (Ext.isEmpty(value)) {
            field.setValue(value);
            return;
        }

        var xtype = field.getXType();

        if (field instanceof Ext.form.field.Date
            || field instanceof Ext.form.field.Time) {

            if (!(value instanceof Date)) {
                var format = opt.format || "Y-m-d H:i:s";
                value = Ext.Date.parse(value, format) || value;
            }

        } else if (field instanceof Ext.form.field.TextArea) {

            value = value && value.replace(/<br\/>/g, "\n");

        } else if (field instanceof Ext.form.field.Number) {

            if (value) {
                var precision = field.decimalPrecision || field.precision;
                value = precision ? Ext.Number.toFixed(value, precision) : value;
            }

        } else if (field instanceof Ext.form.RadioGroup) {

            if (Ext.isString(value)) {
                var result = {};
                result[field.name] = value;
                value = result;
            }

        } else if (field instanceof Ext.form.CheckboxGroup) {

            if (Ext.isString(value)) {
                value = Q.split(value, ",", true);
                var result = {};
                result[field.name] = value;
                value = result;
            }

        }

        field.setValue(value);

    };

    Q.log = function (text) {
        if (text instanceof Error) {
            if (console.error) {
                console.error(text);
            }
        } else if (console.log) {
            console.log(text);
        }
    };

}());