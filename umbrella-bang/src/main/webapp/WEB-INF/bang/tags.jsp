    <%@ page import="java.net.URLDecoder" %>
    <%
        String theme = "http://cdn.bootcss.com/extjs/6.0.0/classic/theme-classic/resources/theme-classic-all-debug.css";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie ck : cookies) {
                if ("BANG_THEME".equals(ck.getName())) {
                    try {
                        theme = URLDecoder.decode(ck.getValue(), "UTF-8");
                    } catch (Exception e) {
                        System.err.println("theme url decode error, " + e.toString());
                    }
                    break;
                }
            }
        }
        pageContext.setAttribute("BANG_THEME", theme);
    %>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <link rel="icon" href="resources/images/favicon.ico" type="image/x-icon"/>
    <link rel="shortcut icon" href="resources/images/favicon.ico" type="image/x-icon"/>
    <link rel="stylesheet" href="${BANG_THEME}">
    <link rel="stylesheet" href="resources/icon.css">

    <script type="text/javascript" src="http://cdn.bootcss.com/extjs/6.0.0/ext-all-debug.js"></script>
    <script type="text/javascript" src="http://cdn.bootcss.com/extjs/6.0.0/classic/locale/locale-zh_CN.js"></script>

    <script type="text/javascript">
        Ext.Loader.setConfig({
            enabled: true,
            paths: {
                "Ext.bang": "resources/plugins/extjs",
                "Q.bang": "resources/bang",
                "Q.user": "resources/bang/user",
                "Q.sys": "resources/bang/sys"
            }
        });
    </script>

    <script type="text/javascript" src="locale-zh_CN.js"></script>
    <script type="text/javascript" src="resources/plugins/extjs/state/CookieProvider.js"></script>
    <script type="text/javascript" src="resources/plugins/bang.js"></script>