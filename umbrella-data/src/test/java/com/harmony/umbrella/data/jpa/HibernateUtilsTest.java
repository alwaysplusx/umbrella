package com.harmony.umbrella.data.jpa;

import java.util.HashMap;
import java.util.List;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

/**
 * @author wuxii@foxmail.com
 */
public class HibernateUtilsTest {

    public static void main(String[] args) {
        List<ParsedPersistenceXmlDescriptor> units = PersistenceXmlParser.locatePersistenceUnits(new HashMap<>());
        for (ParsedPersistenceXmlDescriptor unit : units) {
            System.out.println(unit);
        }
    }
}
