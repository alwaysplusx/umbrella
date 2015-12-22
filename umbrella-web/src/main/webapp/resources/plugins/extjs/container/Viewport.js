/**
 * 每个被关联的组件都有两个属性[supportStage, action]
 *   action: 用于表示控件下一步的动作
 *   supportStage: 表示控件当前所支持的阶段
 *
 * 两个值的范围为
 *   表示页面跳转的名称
 *      page, list      列表(首页)
 *      new, add        新建
 *      edit            编辑
 *      view            查看
 *      search          查询
 *      back            返回(如果未设置preAction默认跳转到首页, 即认为preAction=page)
 *
 *   表示数据操作的名称
 *      submit          数据提交
 *      delete          数据删除
 *      search          数据查询
 *
 *   其他
 *      all             所有(用于supportStage)
 *
 */
Ext.define("Ext.bang.container.Viewport", {
    extend: "Ext.container.Viewport",

    stage: "page",

    constructor: function (config) {
        var me = this,
            refViews = me.refViews;
        // 通过配置的refViews配置对于的getter方法
        me.initGetter(refViews);
        // 初始化生成stage
        me.stage = Q.Stage.valueOf(me.stage);
        me.callParent();
    },

    /**
     * 根据refViews动态创建getter方法
     */
    initGetter: function (refViews) {
        var me = this;

        Q.each(refViews, function (v, p) {
            var ref = v.ref;
            var fnName = 'get' + Ext.String.capitalize(ref);

            // 自定义不覆盖
            if (!me[fnName]) {
                // 动态function, 通过名称过度再调用getRefView
                me[fnName] = Ext.Function.pass(me.getRefView, [ref, v]);
            }

        });

    },

    getStage: function () {
        return this.stage;
    },

    /**
     * 主面板的状态, 并设置相关联的面板状态
     * @param stage
     */
    setStage: function (stage) {
        var me = this,
            refViews = me.refViews;

        // reset viewport stage
        me.stage = stage;

        if (refViews) {
            // 子面板只需要设置阶段的名称
            Q.each(refViews, function (refView, p) {
                var refView = me[refView.ref];
                // 引用到的view如果已经创建才设置
                if (refView) {

                    var oldStage = refView.stage;

                    if (refView.setStage) {
                        refView.setStage(stage);
                    } else {
                        refView.stage = stage;
                    }

                    // 触发子面板的change事件
                    refView.fireEvent("changestage", stage, oldStage);
                }
            });
        }

    },

    /**
     * 根据refViews中的ref的生成的getter名称来获得对应的view
     *
     * @param viewName ref的名称（getter名称）
     * @param opts refViews中的配置项
     * @returns {*}
     */
    getRefView: function (viewName, opts) {
        var me = this,
            view = me[viewName];

        // view未创建或者已经被destroy创建一个新的view
        if (Ext.isEmpty(view) || view.destroyed) {
            view = me[viewName] = Ext.widget(opts.selector);
        }

        return view;
    },

    /**
     * 根据不同阶段获取不同的视图对象
     * @param stage 阶段
     */
    getStageView: function (stage) {
        var me = this,
            refs = me.refViews,
            view;

        // v = {ref: "pageView", selector: "userpage", stage: "page"},
        Q.each(refs, function (v, p) {

            Q.each(Q.split(v.stage, ",", true), function (s, p) {
                if (stage.isStageOf(s)) {
                    view = me.getRefView(v.ref, v);
                    return false;
                }
            });

            if (view) {
                // break;
                return false;
            }

        });

        return view;
    },

    /**
     * 获取当前viewport处在的stage
     *
     * @returns {string}
     */
    getCurrentStage: function () {
        return this.stage;
    },

    /**
     * 获取当前stage显示的viewport, 如果未找到则返回当前viewport位置为center的view
     * @returns {*}
     */
    getCurrentView: function () {
        var me = this,
            stage = me.stage;
        var view = me.getStageView(stage);

        if (!view) {
            view = me.query("panel[region=center]")[0];
        }

        return view;
    },

    /**
     * 更改当前显示的视图,
     * 并设置切换后的视图状态
     *
     * @param newView 要切换到的视图
     * @param oldView 被替换的视图
     * @param newStage 新视图的阶段
     * @param oldStage 旧视图的阶段
     */
    changeView: function (newView, oldView, newStage, oldStage) {
        var me = this;

        if (newView !== oldView) {
            // 页面变换
            if (oldStage.isShow()) {
                // 如果原来的阶段为弹出窗, 先隐藏
                oldView.hide();
            } else if (!newStage.isShow()) {
                me.remove(oldView, false);
            }

            // 如果新阶段为弹出窗则不隐藏原来的显示面板
            if (newStage.isShow()) {
                newView.show();
            } else if (!oldStage.isShow()) {
                me.add(newView);
            }
        }

        // 阶段变换
        me.setStage(newStage);

    }

});