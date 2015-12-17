Ext.define("Ext.bang.app.Controller", {
    extend: "Ext.app.Controller",

    requires: ["Ext.bang.util.EventSource"],

    /**
     * 对扩展部分初始化
     */
    constructor: function (config) {
        var me = this;
        me.callParent([config]);

        me.on("beforechangestage", me.displayViewInterceptor);

    },

    // 配合displayViewInterceptor起作用
    monitor: {
        button: true,
        field: true,
        data: true
    },

    /**
     * 按钮点击时候触发, 如果button存在btn["action"]属性则激活对应的changeview事件
     *
     * @param btn
     */
    onActionButtonClick: function (btn, e, eOpts) {
        var me = this,
            action = btn.action;

        var actionStage = me.actionStage(action);

        if (action && actionStage) {

            var eventSource = me.packEventSource({
                source: btn,
                event: e,
                eOpts: eOpts
            });

            if (actionStage.isRedirect() || actionStage.isShow()) {
                // 页面有变换
                me.changeStageAndView(actionStage, eventSource);

            } else if (actionStage.isSubmit()) {

                // 改变阶段并提交, 或者不改变阶段直接提交当前阶段
                // e.g.:
                //      delete->page(修改当前的stage, 再提交)
                //      none->page(none表示不修改当前stage, 提交现在显示的view.
                //          原因有可能从page页面跳转到编辑页面提交按钮只有一个但是提交的为跳转过来的阶段)
                //              add->page
                //              edit->page

                var mv = me.getMainView(),
                    submitStage = actionStage,
                    next = actionStage.next();

                if (!actionStage.isNone()) {
                    // change stage
                    me.changeStageAndView(actionStage, eventSource);
                } else {
                    submitStage = mv.getCurrentStage();
                }

                // submit actionStage stage
                me.submitStage(submitStage, next, eventSource);

            }

        }

    },

    /**
     * 列表行点击事件
     */
    onItemClick: function (v, r, element, index, event, eOpts) {
        var me = this;

        me.changeStageAndView(new Q.Stage("page"), me.packEventSource({
            tabPanel: v,
            source: r,
            element: element,
            rowIndex: index,
            event: event,
            eOpts: eOpts
        }));

    },

    /**
     * 列表的阶段双击事件
     */
    onItemDoubleClick: function (v, r, element, index, event, eOpts) {
        if (Q.doubleClickDisabled()) {
            return;
        }

        var me = this,
            action = Q.getDoubleClickAction();

        var actionStage = me.actionStage(action);

        if (action && actionStage) {
            me.changeStageAndView(actionStage, me.packEventSource({
                tabPanel: v,
                source: r,
                element: element,
                rowIndex: index,
                event: event,
                eOpts: eOpts
            }));
        }

    },

    /**
     * 将事件源封装
     *
     * @param o
     *            事件源
     * @returns {Ext.bang.util.EventSource}
     */
    packEventSource: function (o) {
        return Ext.create("Ext.bang.util.EventSource", o);
    },

    /**
     * 解析对应的action->stage关系
     *
     * @param action
     *            动作名称
     * @returns {*} 动作对应的阶段
     */
    actionStage: function (action) {
        var actionStage = Q.Stage.valueOf(action);
        return Ext.isEmpty(actionStage) ? new Q.Stage(action) : actionStage;
    },

    /**
     * 更改主界面的阶段
     *
     * button 点击事件响应方法, 该方法匹配的一般为更改视图的按钮. 如: 返回按钮(由编辑页面返回到page界面, action=page),
     * 新建按钮(由page界面进入编辑新建页面),
     *
     * 注: 对应的stage名称解析成为Q.Stage
     *
     * @param stage
     *            更改的阶段
     * @param eventSource
     *            事件源
     */
    changeStageAndView: function (stage, eventSource) {
        var me = this,
            mv = me.getMainView();

        // check stage instance
        if (!(stage instanceof Q.Stage)) {
            stage = me.actionStage(stage);
            if (!(stage instanceof Q.Stage)) {
                throw new Error("stage not Q.Stage instance");
            }
        }

        // 当前视图的stage
        var currentStage = mv.getCurrentStage();
        // 当前显示的视图
        var currentView = mv.getCurrentView();

        // 将要显示的视图, none表示不变更当前页面
        var willDisplayView = mv.getStageView(stage);

        if (!willDisplayView) {
            throw new Error("can't get stage[" + stage.getName() + "] view");
        }

        // fire "beforechangestage" event
        if (me.fireEvent("beforechangestage", willDisplayView, currentView, stage, currentStage, eventSource) === true) {

            console.info("change stage: " + currentStage.getName() + "->" + stage.getName() + ", view:" + (currentView && currentView.id) + "->" + (willDisplayView && willDisplayView.id));

            // 主界面根据阶段跟换显示的页面
            mv.changeView(willDisplayView, currentView, stage, currentStage);

            try {
                // fire changestage event
                me.fireEvent("changestage", willDisplayView, currentView, stage, currentStage, eventSource);
            } catch (e) {
                Q.log(e);
            }

        }

    },

    /**
     * 提交当前阶段的列表或表单数据
     *
     * @param stage
     *            提交的阶段
     * @param next
     *            提交后的下一个阶段
     * @param es
     *            事件源
     */
    submitStage: function (stage, next, eventSource) {
        var me = this,
            mv = me.getMainView(),
            currentStage = mv.getCurrentStage();

        // 当前视图的stage
        if (!currentStage.isStageOf(stage)) {
            Q.warning("提交错误阶段数据");
        }

        // 当前显示的视图
        var currentView = mv.getCurrentView();

        console.info("submit stage: " + stage.getName() + ", next to " + next.getName());

        // fire "beforesubmitstage" event
        if (me.fireEvent("beforesubmitstage", stage, currentView, next, eventSource) === true) {

            var method = me.findSubmitMethod(stage, eventSource);
            if (!method) {
                throw new Error("can not find submit method of stage [" + currentStage.getName() + "]");
            }

            method.call(this, currentView, stage, next);
        }
    },

    /**
     * beforechangestage 监听事件扩展, 对将要显示的面板控制字段, 按钮, 数据注入等
     *
     * @param newView
     *            将显示的面板
     * @param oldView
     *            原显示的面板
     * @param newStage
     *            显示后的阶段
     * @param oldStage
     *            原阶段
     */
    displayViewInterceptor: function (newView, oldView, newStage, oldStage, scope) {
        // 需要显示的视图所需要用到的数据
        var me = this,
            monitor = me.monitor,
            data = {};

        // new stage 需要数据才加载对应的数据
        if (newStage.requireData()) {
            var from = {
                view: oldView,
                stage: oldStage
            };
            var to = {
                view: newView,
                stage: newStage
            };
            data = me.getSwitchValue(from, to);

            if (data === false) {
                Q.tip($("message.select.data"));
                return false;
            }

        }

        if (monitor) {
            if (monitor.button) {
                me.panelButtonControl(newView, newStage, data, scope);
            }

            if (monitor.field) {
                me.panelFieldControl(newView, newStage, data, scope)
            }

            if (monitor.data) {
                me.panelDataControl(newView, newStage, data, scope);
            }
        }
    },

    getSwitchValue: function (from, to) {
        var me = this,
            fromStage = from.stage,
            toStage = to.stage;
        var data = {};

        if (toStage.requireData()) {
            var method = me.findSwitchValueMethod(fromStage, toStage);

            if (!method) {
                throw new Error("no specification get switch value method find");
            }
        }

        data = method.call(this, from.view, to.view, to.stage);

        return data;
    },

    /**
     * 通过事件源来确定调用的方法
     *
     * @param stage
     *            页面当前处在的阶段
     *
     * @param eventSource
     *            事件源
     */
    findSubmitMethod: function (stage, eventSource) {
        var me = this;
        var source = eventSource.source,
            methodName = source.methodName;

        if (!methodName) {
            var mapper = me.submitMapper;

            Q.each(mapper, function (m, p) {
                Q.each(Q.split(m.stage, ",", true), function (s, p) {
                    if (stage.isStageOf(s)) {
                        methodName = m.methodName;
                        return false;
                    }
                });
                if (methodName) {
                    return false;
                }
            });
        }

        return me[methodName] ? me[methodName] : null;
    },

    /**
     * 通过配置获取对于的switchDataMapper配置信息获取对于的stwich获取值方法
     *
     * @param fromStage
     *            源阶段
     * @param toStage
     *            目标阶段
     * @returns {*} 获取转换值的方法
     */
    findSwitchValueMethod: function (fromStage, toStage) {
        var me = this,
            switchDataMapper = me.switchDataMapper;

        var methodName;

        if (switchDataMapper) {
            Q.each(switchDataMapper, function (sdm, p) {
                if (fromStage.isStageOf(sdm.from)) {
                    Q.each(Q.split(sdm.to, ",", true), function (to, p) {
                        if (toStage.isStageOf(to)) {
                            methodName = sdm.methodName;
                            return false;
                        }
                    });
                    if (methodName) {
                        return false;
                    }
                }
            });
        }
        // no switchDataMapper use default methodName = get + stageName + Data
        return methodName ? me[methodName] : me.findMethod(fromStage.getName(), "get", "Data");
    },

    /**
     * 页面的输入字段的控制, 可以通过一下方式配置
     *
     * 1.stageName属性, 通过指定的stageName. 数据可以是 'add', 'add|new|edit|view', ['add',
     * 'view', 'edit'] 当当前的stage名称存field定义的stageName名称内的时候, 显示对应的字段,
     * 否者readOnly(stageAction=readOnly) ps: stageAction - show, hide,
     * disabled[disable], enable, none, read, write
     *
     * 2.inStage方法，通过调用方法返回对应的值(stageAction, true, false) true = write, enable
     * false = readOnly, enable
     *
     * @param panel
     *            被控制的面板
     * @param stage
     *            控制的阶段
     * @param data
     *            控制所需要用到的数据
     * @param scope
     */
    panelFieldControl: function (panel, stage, data, scope) {
        var me = this,
            fields = panel.query("field");

        Ext.each(fields, function (field, p) {
            me.fieldControl(field, stage, data, scope);
        });

    },

    fieldControl: function (field, stage, data, scope) {
        var me = this;

        if (!(field instanceof Ext.form.Field)) {
            // support panel input
            return this.panelFieldControl(field, stage, data, scope);
        }

        var caller = field.inStage,
            names = field.supportStage;

        var stageResult;

        if (Ext.isFunction(caller)) {
            /*
             * inStage method return true = write(write, enable) false =
             * read(readOnly, enable)
             */
            stageResult = caller.call(field, stage.getName(), data, scope);

            if (typeof stageResult === "boolean") {
                stageResult = stageResult ? "write" : "read";
            }

        } else if (!Ext.isEmpty(names)) {

            if (names === "all") {

                stageResult = "write";

            } else {

                names = Q.split(names, ",", true);
                var targetName = stage.getName();

                var hasStageNameFlag = false;

                for (var i = 0, max = names.length; i < max; i++) {
                    if (targetName === names[i]) {
                        hasStageNameFlag = true;
                        break;
                    }
                }

                stageResult = hasStageNameFlag ? "write" : (field.stageAction || "read");
            }
        }

        // 根据结果控制按钮的显示状态
        if (stageResult === "none") {
            // 延续当前状态, 不做更改 do none

        } else if (!Ext.isEmpty(stageResult)) {

            stageResult = Q.split(stageResult, ",", true);

            for (var i = 0, max = stageResult.length; i < max; i++) {
                var result = stageResult[i];

                if (result === "write") {
                    // 可写
                    field.setDisabled(false);
                    field.setReadOnly(false);
                    return;
                } else if (result === "hide") {
                    // 隐藏
                    field.hide();
                    return;
                } else if (result === "read") {
                    // 只读
                    field.show();
                    field.setReadOnly(true);
                    field.setDisabled(false);
                    return;
                } else if (result === "show") {
                    // 显示
                    field.show();
                } else if (result === "disable" || result === "disabled") {
                    // 禁用
                    field.setDisabled(true);
                } else if (result === "enable") {
                    // 启用
                    field.setDisabled(false);
                }
            }
        } else {
            me.setFieldDefaultStage(field, stage);
        }
    },

    /**
     * 页面按钮各个阶段的控制, 按钮可以选择两种方式来实现被控制
     *
     * 1.stageName属性, 通过指定的stageName. 数据可以是 'add', 'add|new|edit|view', ['add',
     * 'view', 'edit'] 当当前的stage名称存button定义的stageName名称内时候则显示对应的按钮,
     * 否者disabled(stageAction=disabled) ps: stageAction - show, hide,
     * disabled[disable], enable, none
     *
     * 2.inStage方法, 通过指定的方法返回值, 返回值对应stageAction的值或者[true, false].
     * true对应的默认状态为enable, false对应disabled
     *
     * @param panel
     *            被控制的面板
     * @param stage
     *            控制到的阶段
     * @param data
     *            控制面板所需要的数据
     * @param scope
     *            scope data
     */
    panelButtonControl: function (panel, stage, data, scope) {
        var me = this, buttons = panel.query("button");

        Ext.each(buttons, function (button, p) {
            me.buttonControl(button, stage, data, scope);
        });

    },

    /**
     * 页面按钮各个阶段的控制, 按钮可以选择两种方式来实现被控制
     *
     * 1.stageName属性, 通过指定的stageName. 数据可以是 'add', 'add|new|edit|view', ['add',
     * 'view', 'edit'] 当当前的stage名称存button定义的stageName名称内时候则显示对应的按钮,
     * 否者disabled(stageAction=disabled) ps: stageAction - show, hide,
     * disabled[disable], enable, none
     *
     * 2.inStage方法, 通过指定的方法返回值, 返回值对应stageAction的值或者[true, false].
     * true对应的默认状态为enable, false对应disabled
     *
     * @param panel
     *            被控制的面板
     * @param stage
     *            控制到的阶段
     * @param data
     *            控制面板所需要的数据
     * @param scope
     */
    buttonControl: function (button, stage, data, scope) {
        var me = this;

        // panel input support
        if (!(button instanceof Ext.button.Button)) {
            return me.panelButtonControl(button, stage, data, scope);
        }

        var caller = button.inStage, names = button.supportStage;

        // 两个属性均没有配置
        if (Ext.isEmpty(caller) && Ext.isEmpty(names)) {
            // 没有配置属性但配置了action, 置为激活状态
            if (button.action) {
                button.setDisabled(false);
            }
            return;
        }

        // stage的判定结果
        var stageResult;

        if (Ext.isFunction(caller)) {

            stageResult = caller.call(button, stage.getName(), data, scope);

        } else if (!Ext.isEmpty(names)) {

            if (names === "all") {

                stageResult = "enable";

            } else {

                names = Q.split(names, ",", true);
                var targetName = stage.getName();

                var hasStageNameFlag = false;

                for (var i = 0, max = names.length; i < max; i++) {
                    if (targetName === names[i]) {
                        hasStageNameFlag = true;
                        break;
                    }
                }

                stageResult = hasStageNameFlag ? "enable" : (button.stageAction || "disabled");
            }

        }

        // 根据结果控制按钮的显示状态
        if (stageResult === "none") {
            // 延续当前状态, 不做更改 do none

        } else if (stageResult === false || stageResult === "disabled" || stageResult === "disable") {

            button.show();
            button.setDisabled(true);

        } else if (stageResult === true || stageResult === "enable") {

            button.show();
            button.setDisabled(false);

        } else if (stageResult === "show") {

            button.show();

        } else if (stageResult === "hide") {

            button.hide();

        } else {
            // 设置默认情况
            me.setButtonDefaultStage(btn, stage);
        }

    },

    /**
     * 给面板注入数据
     *
     * @param panel
     *            待注入的面板
     * @param stage
     *            当前阶段
     * @param data
     *            注入的所有数据
     * @param scope
     *            scope data
     */
    panelDataControl: function (panel, stage, data, scope) {
        var me = this;

        // reset first
        if (panel.reset) {
            panel.reset();
        }

        // if not reset view
        var inject = stage.requireData() && !Q.isEmpty(data, false);

        if (inject) {
            var fields = panel.query("field"), fieldPrefix = panel.fieldPrefix || "";

            Ext.each(fields, function (field, p) {
                me.injectData(field, stage, data, fieldPrefix, scope);
            });

        }

    },

    /**
     * 给字段注入数据
     *
     * @param field
     *            注入的数据
     * @param stage
     *            当前阶段
     * @param data
     *            注入的数据(maybe all)
     * @param scope
     *            scope data
     */
    injectData: function (field, stage, data, prefix, scope) {
        var me = this,
            value;

        if (field.getInjectData) {
            value = field.getInjectData(stage, data, scope);
        } else {
            var xtype = field.getXType(),
                name = (prefix || "") + field.getName();
            value = me.findFieldValue(name, data, xtype, field);
        }

        field.setValue(value);

    },

    /**
     * 根据字段名称过滤出对应的值
     *
     * @param name
     *            字段名称
     * @param data
     *            字段值
     */
    findFieldValue: function (name, data, xtype, opt) {
        var value = data[name];

        if (!(typeof value === "string") && Ext.isEmpty(data)) {
            value = data;
        }

        if (value) {
            if (xtype === "datefield" || xtype === "timefield") {

                if (value instanceof Date) {
                    return value;
                } else {
                    var format = (opt && opt.format) || "Y-m-d H:i:s";
                    return Ext.Date.parse(value, format) || value;
                }

            } else if (xtype === "textarea") {

                return value && value.replace(/<br\/>/g, "\n");

            } else if (xtype === "numberfield") {

                if (value) {
                    var precision = opt.decimalPrecision || opt.precision;
                    return precision ? Ext.Number.toFixed(value, precision) : value;
                } else {
                    return value;
                }

            } else if (xtype === "radiogroup") {

                if (Ext.isObject(value)) {
                    return value;
                } else if (Ext.isString(value)) {
                    var result = {};
                    return result[opt.name] = value;
                }

            } else if (xtype === "checkbox") {

                var result = {};

            }
        }

        if (typeof value === "object") {
            try {
                value = Ext.encode(value);
            } catch (e) {
            }
        }

        return value;
    },

    /**
     * 设置控件的默认阶段状态
     *
     * @param field
     * @param stage
     */
    setFieldDefaultStage: function (field, stage) {

        if (stage.isView()) {
            /**
             * 查看阶段的字段默认控制为enable, readOnly
             */
            field.setDisabled(false);
            field.setReadOnly(true);
        } else {
            /**
             * 如果以上条件均不满足设置默认选项 可以通过字段默认 defaultEnable defaultReadOnly
             * 两个默认值设置控件的状态
             */
            field.setDisabled(field.defaultEnable || false);
            field.setReadOnly(field.defaultReadOnly || false);
        }

    },

    /**
     * 设置按钮各个阶段的控制
     *
     * @param button
     * @param stage
     */
    setButtonDefaultStage: function (button, stage) {

        if (button.action === "back") {
            // 返回按钮不需要控制
            button.setDisabled(false);
            return;
        }

        if (stage.isView()) {
            /**
             * 查看阶段的字段默认控制为enable, readOnly
             */
            button.setDisabled(true);
        } else {
            /**
             * 如果以上条件均不满足设置默认选项 可以通过字段默认 defaultEnable defaultReadOnly
             * 两个默认值设置控件的状态
             */
            button.setDisabled(button.defaultEnable || false);
        }
    },

    /**
     * 提交列表数据工具方法
     *
     * @param gridPanel
     * @param stage
     * @returns {boolean}
     */
    submitGridData: function (gridPanel, stage, opts) {
        var me = this, mv = me.getMainView(), name = btn.name, submitMethod;

        if (gridPanel.isValid && !gridPanel.isValid()) {
            return false;
        }

        // 不存在指定的提交方法
        var params; // 组装提交的数据

        if (gridPanel.getSubmitData) {

            params = gridPanel.getSubmitData();

        } else {
            var selection = gridPanel.getSelection();

            if (selection.length === 0) {
                Q.tip($("message.select.data"));
                return false;
            }

            params = selection[0].getData();
        }

        var store = gridPanel.getStore(),
            url = gridPanel.getUrl(stage),
            method = (gridPanel.getMethod && gridPanel.getMethod()) || gridPanel.method || "POST",
            el = gridPanel.getEl();

        gridPanel.fireEvent("beforesubmit", params, stage, url, opts);

        el.mask(opts.mask || $("message.submit"));

        Q.ajax({
            method: method,
            url: url,
            params: params,
            success: function (result, o) {
                gridPanel.fireEvent("submit", true, result, stage, url);
                if (opts.success) {
                    opts.success(result, o);
                }
            },
            failure: function (result, o) {
                gridPanel.fireEvent("submit", false, result, stage, url);
                if (opts.failure) {
                    opts.failure(result, o);
                }
            },
            callback: function (result, o) {
                el.unmask();
                if (opts.callback) {
                    opts.callback(result, o);
                }

            }
        });

    },

    /**
     * 表单提交工具方法
     *
     * @param formPanel
     *            提交的表单
     * @param stage
     * @returns {boolean}
     */
    submitFormData: function (formPanel, stage, opts) {
        if (!formPanel.isValid()) {
            // 验证不通过
            return false;
        }

        var opts = opts || {};
        // 不存在指定的提交方法
        var form = formPanel.getForm(),
            url = formPanel.getUrl(stage.getName()),
            method = (formPanel.getMethod && view.getMethod()) || view.method || "POST",
            el = formPanel.getEl();

        // 表单相关联的数据
        var params = (view.getRefData && view.getRefData()) || {};

        // 触发主视图提交前事件, 参数为提交的视图, 以及提交的参数
        formPanel.fireEvent("beforesubmit", params, stage, url, opts);

        var mask = opts.mask || $("message.submit");

        var elMask = false;

        if (typeof mask === "string") {
            elMask = mask;
            el.mask(elMask);
        }
        form.submit({
            method: method,
            url: url,
            params: params,
            waitTitle: elMask ? "" : mask.title,
            waitMsg: elMask ? "" : (mask.message || mask.msg),
            success: function (form, action) {
                var result = action.result;
                formPanel.fireEvent("submit", true, result, stage, url, opts);
                if (opts.success) {
                    opts.success(form, action);
                }
                if (elMask) {
                    el.unmask();
                }
            },
            failure: function (form, action) {
                var result = action.result;
                formPanel.fireEvent("submit", true, result, stage, url, opts);
                if (opts.failure) {
                    opts.failure(form, action);
                }
                if (elMask) {
                    el.unmask();
                }
            }
        });
    },

    // util method

    /**
     * 查询this中是否有存在指定名称的方法
     *
     * @param name
     *            方法名称
     * @param prefix
     *            名称前缀
     * @param suffix
     *            名称后缀
     * @returns {*}
     */
    findMethod: function (name, prefix, suffix) {
        if (prefix) {
            name = Ext.String.capitalize(name);
        } else {
            prefix = "";
        }

        if (suffix) {
            suffix = Ext.String.capitalize(suffix);
        } else {
            suffix = "";
        }

        return this[prefix + name + suffix];
    },

    /**
     * application main view
     *
     * @returns {*}
     */
    getMainView: function () {
        var me = this;
        if (me.inApplication()) {
            return me.getApplication().getMainView();
        }
        return null;
    },

    /**
     * 判断当前controller是否处于app中
     *
     * @returns {boolean}
     */
    inApplication: function () {
        return !Ext.isEmpty(this.getApplication());
    }

});