Ext.define("Q.sys.store.ThemeStore", {
    extend: "Ext.bang.data.ArrayStore",
    fields: ["name", "code", "link"],
    data: [
        ["Classic主题", "classic", "//cdn.bootcss.com/extjs/6.0.0/classic/theme-classic/resources/theme-classic-all-debug.css"],
        ["Crisp主题", "crisp", "//cdn.bootcss.com/extjs/6.0.0/classic/theme-crisp/resources/theme-crisp-all-debug.css"],
        ["Gray主题", "gray", "//cdn.bootcss.com/extjs/6.0.0/classic/theme-gray/resources/theme-gray-all-debug.css"],
        ["Triton主题", "triton", "//cdn.bootcss.com/extjs/6.0.0/classic/theme-triton/resources/theme-triton-all-debug.css"],
        ["Neptune主题", "neptune", "//cdn.bootcss.com/extjs/6.0.0/classic/theme-neptune/resources/theme-neptune-all-debug.css"],
        ["Aria主题", "aria", "//cdn.bootcss.com/extjs/6.0.0/classic/theme-aria/resources/theme-aria-all-debug.css"],
    ]
});