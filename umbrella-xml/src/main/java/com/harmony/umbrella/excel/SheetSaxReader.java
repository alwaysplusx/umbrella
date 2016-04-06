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

import java.io.InputStream;
import java.util.Iterator;
import java.util.Stack;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * SUPPORT XLSX
 * 
 * @author wuxii@foxmail.com
 */
public class SheetSaxReader {

    XSSFReader xssfReader;

    public void read() throws Exception {
        xssfReader = new XSSFReader(OPCPackage.open("src/test/resources/excel/sax.xlsx"));
        Iterator<InputStream> sheets = xssfReader.getSheetsData();
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(new ExcelHandler());

        while (sheets.hasNext()) {
            InputStream is = sheets.next();
            xmlReader.parse(new InputSource(is));
            is.close();
            break;
        }

    }

    public static void main(String[] args) throws Exception {
        new SheetSaxReader().read();
    }

    public Object getValue(int index) throws Exception {
        SharedStringsTable sst = xssfReader.getSharedStringsTable();
        return sst.getEntryAt(index);
    }

    protected class ExcelHandler extends DefaultHandler {

        private Stack<E> stack = new Stack<E>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // row 属性说明: r-行号, spans-起始行(起:结束), ht-行高(磅)
            // c  属性说明: r-坐标, t-cell类型

            for (int i = 0; i < attributes.getLength(); i++) {
                System.out.println("qname: " + qName + ", att: name=" + attributes.getLocalName(i) + ", value=" + attributes.getValue(i) + "");
            }
            System.out.println();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            /*Attributes att = attStack.pop();
            String startQName = qnameStack.pop();*/
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            try {
                stack.peek().content = String.valueOf(getValue(Integer.valueOf(new String(ch, start, length))));
            } catch (NumberFormatException e) {
            } catch (Exception e) {
            }
        }
    }

    protected class E {

        private String qname;
        private Attributes attributes;
        private String content;

        public boolean isRow() {
            return false;
        }

        public int getRowNum() {
            return 1;
        }

        public int getColumnNum() {
            return 1;
        }

        public boolean isCell() {
            return false;
        }

        public int getCellType() {
            return 1;
        }

    }

}
