package com.harmony.umbrella.excel;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author wuxii@foxmail.com
 */
public class ExcelHandler extends DefaultHandler {

    /*private Stack<Attributes> attStack = new Stack<Attributes>();
    private Stack<String> qnameStack = new Stack<String>();*/

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        /*attStack.push(attributes);
        qnameStack.push(qName);*/
        for (int i = 0; i < attributes.getLength(); i++) {
            System.out.println("qname: " + qName + ", att: name=" + attributes.getLocalName(i) + ", value=" + attributes.getValue(i) + ", qname=");
        }
        System.out.println();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        /*Attributes att = attStack.pop();
        String startQName = qnameStack.pop();*/
    }

    /*public void handleRow(Attributes att) {
        for (int i = 0; i < att.getLength(); i++) {
            System.out.println("row: " + att.getValue(i));
        }
    }

    public void handleCell(Attributes att) {
        for (int i = 0; i < att.getLength(); i++) {
            System.out.println("cell: " + att.getValue(i));
        }
    }*/

    public static void main(String[] args) throws Exception {
        OPCPackage pkg = OPCPackage.open("src/test/resources/excel/sax.xlsx");

        XMLReader parser = XMLReaderFactory.createXMLReader();

        ExcelHandler excelHandler = new ExcelHandler();
        parser.setContentHandler(excelHandler);

        XSSFReader r = new XSSFReader(pkg);

        parser.parse(new InputSource(r.getSheet("rId1")));

        // System.out.println(excelHandler.attStack.isEmpty());

    }

}
