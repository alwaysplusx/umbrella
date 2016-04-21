/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.excel;

import java.io.File;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * SUPPORT XLSX
 * 
 * @author wuxii@foxmail.com
 */
public class SheetSaxReader {

    private XSSFReader xssfReader;

    public SheetSaxReader(String pathname) throws Exception {
        xssfReader = new XSSFReader(OPCPackage.open(pathname));
    }

    public SheetSaxReader(File file) throws Exception {
        xssfReader = new XSSFReader(OPCPackage.open(file));
    }

    public void read(XMLReader xmlReader, int sheetIndex) throws Exception {
        InputStream sheet = xssfReader.getSheet(toRelationId(sheetIndex));
        xmlReader.parse(new InputSource(sheet));
        sheet.close();
    }

    public void read(ExcelHandler handler, int sheetIndex) throws Exception {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
        SharedStringsTable oldSST = handler.getSharedStringsTable();
        try {
            handler.setSharedStringsTable(getSharedStringsTable());
            InputStream sheet = xssfReader.getSheet(toRelationId(sheetIndex));
            xmlReader.parse(new InputSource(sheet));
            sheet.close();
        } finally {
            handler.setSharedStringsTable(oldSST);
        }
    }

    public SharedStringsTable getSharedStringsTable() throws Exception {
        return xssfReader.getSharedStringsTable();
    }

    public String toRelationId(int index) {
        return "rId" + ++index;
    }
}
