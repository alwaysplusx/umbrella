package com.harmony.umbrella.log;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcTest {

    // h2
    //    private static final String url = "jdbc:h2:file:~/.h2/harmony/bang";
    //    private static final String user = "sa";
    //    private static final String password = "";

    private static final String url = "jdbc:oracle:thin:@192.168.1.156:1521/platform";
    private static final String user = "platform";
    private static final String password = "ptt715";

    private static Connection conn;

    @BeforeClass
    public static void beforeClass() throws Exception {
        conn = DriverManager.getConnection(url, user, password);
    }

    @Test
    public void testMetadata() throws Exception {
        DatabaseMetaData metaData = conn.getMetaData();
        /*ResultSet typeInfo = metaData.getTypeInfo();
        show(typeInfo);*/

        /*ResultSet catalogs = metaData.getCatalogs();
        show(catalogs);*/

        /*ResultSet clientInfoProperties = metaData.getClientInfoProperties();
        show(clientInfoProperties);*/

        /*ResultSet schemas = metaData.getSchemas();
        show(schemas);*/

        /*List<String> tableTypes = new ArrayList<String>();
        ResultSet types = metaData.getTableTypes();
        while (types.next()) {
            tableTypes.add(types.getString(1));
        }
        ResultSet tables = metaData.getTables(conn.getCatalog(), conn.getCatalog(), null, tableTypes.toArray(new String[tableTypes.size()]));*/

        /*while (tables.next()) {
            System.out.println(tables.getObject(1));
        }*/
    }

    public void show(ResultSet resultSet) throws Exception {
        while (resultSet.next()) {
            System.out.println(resultSet.getObject(1));
        }
    }
}
