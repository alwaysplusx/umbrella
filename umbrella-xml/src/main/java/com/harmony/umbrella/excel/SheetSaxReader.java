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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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

    private static final Set<String> READ_TAGS = new HashSet<String>();

    static {
        READ_TAGS.add("row");
        READ_TAGS.add("c");
        READ_TAGS.add("v");
    }

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
            // c 属性说明: r-坐标, t-cell类型
            stack.push(new E(qName, attributes));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            E e = stack.pop();
            System.out.println(e);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            E e = stack.peek();
            if (e.isCell()) {
                e.attributes.getType("v");
            }
            /*try {
                stack.peek().content = String.valueOf(getValue(Integer.valueOf(new String(ch, start, length))));
            } catch (NumberFormatException e) {
            } catch (Exception e) {
            }*/
        }

        public boolean isReadTag(String qname) {
            return READ_TAGS.contains(qname);
        }

    }

    protected class E {

        private String qname;
        private Attributes attributes;
        private String content;

        public E(String qname, Attributes attributes) {
            this.qname = qname;
            this.attributes = attributes;
        }

        public boolean isRow() {
            return "row".equals(qname);
        }

        public boolean isCell() {
            return "c".equals(qname);
        }

        public int getRowNum() {
            if (isRow()) {
                return Integer.parseInt(attributes.getValue("r"));
            } else if (isCell()) {
                String value = attributes.getValue("r");
                int i;
                for (i = 0; i < value.length(); i++) {
                    if (!Character.isAlphabetic(value.charAt(i))) {
                        break;
                    }
                }
                return Integer.parseInt(value.substring(i)) - 1;
            }
            return -1;
        }

        public int getColumnNum() {
            if (isCell()) {
                String value = attributes.getValue("r");
                int i;
                for (i = 0; i < value.length(); i++) {
                    if (!Character.isAlphabetic(value.charAt(i))) {
                        break;
                    }
                }
                return ExcelUtil.toColumnNumber(value.substring(0, i)) - 1;
            }
            return -1;
        }

        public int getCellType() {
            return 1;
        }

        @Override
        public String toString() {
            return "{qname:" + qname + ", content:" + content + "}";
        }

    }

}
