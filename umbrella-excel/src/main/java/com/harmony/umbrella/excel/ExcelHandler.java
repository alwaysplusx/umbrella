package com.harmony.umbrella.excel;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.harmony.umbrella.util.DigitUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ExcelHandler extends DefaultHandler {

    private SharedStringsTable sharedStringsTable;
    private Stack stack = new Stack();
    private Stack<String> cellStack = new Stack<String>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("row".equals(qName)) {
            Map<String, String> attMap = toMap(attributes);
            // row 属性说明: r-行号, spans-起始列(起:结束), ht-行高(磅)
            int rowNum = Integer.parseInt(attributes.getValue("r")) - 1;
            String[] spans = attributes.getValue("spans").split(":");
            int startColumn = Integer.parseInt(spans[0]) - 1;
            int endColumn = Integer.parseInt(spans[1]) - 1;
            stack.push(new R(rowNum, startColumn, endColumn, attMap));
        } else if ("c".equals(qName)) {
            Map<String, String> attMap = toMap(attributes);
            // c 属性说明: r-坐标, t-cell类型
            String point = attributes.getValue("r");
            int splitIndex = getSplitIndex(point);
            // A12 数字部分
            int rowNum = Integer.parseInt(point.substring(splitIndex)) - 1;
            // A12 字母部分
            int columnNum = ExcelUtil.toColumnNumber(point.substring(0, splitIndex)) - 1;
            stack.push(new C(rowNum, columnNum, attMap));
        } else if (currentCell() != null) {
            cellStack.push(qName);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("row".equals(qName)) {
            handleRow((R) stack.pop());
        } else if ("c".equals(qName)) {
            C c = (C) stack.pop();
            ((R) stack.peek()).cells.add(c);
            handleCell(c);
        } else if (currentCell() != null) {
            cellStack.pop();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        C cell = currentCell();
        if (cell != null) {
            String currentQName = cellStack.peek();
            String content = new String(ch, start, length);
            if ("v".equals(currentQName)) {
                if (cell.isRefCell() && DigitUtils.isDigit(content)) {
                    cell.value = getSharedText(Integer.parseInt(content));
                } else {
                    cell.value = content;
                }
            } else if ("f".equals(currentQName)) {
                cell.formula = content;
            } else {

            }
        }
    }

    public C currentCell() {
        try {
            Object obj = stack.peek();
            return obj instanceof C ? (C) obj : null;
        } catch (EmptyStackException e) {
            return null;
        }
    }

    public R currentRow() {
        try {
            Object obj = stack.peek();
            return obj instanceof R ? (R) obj : null;
        } catch (EmptyStackException e) {
            return null;
        }
    }

    // excel handler

    public void handleRow(R r) {
        System.out.println("row: " + r.rowNum);
        for (C cell : r.getCells()) {
            System.out.println("\t" + cell.columnNum + ", " + cell.getStringValue());
        }
    }

    public SharedStringsTable getSharedStringsTable() {
        return sharedStringsTable;
    }

    public void setSharedStringsTable(SharedStringsTable sharedStringsTable) {
        this.sharedStringsTable = sharedStringsTable;
    }

    public void handleCell(C c) {
    }

    public void destroy() {
        stack.clear();
        cellStack.clear();
        stack = null;
        cellStack = null;
        sharedStringsTable = null;
    }

    private Map<String, String> toMap(Attributes atts) {
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < atts.getLength(); i++) {
            result.put(atts.getQName(i), atts.getValue(i));
        }
        return result;
    }

    private String getSharedText(int index) {
        CTRst ctRst = sharedStringsTable.getEntryAt(index);
        return ctRst.getT();
    }

    private int getSplitIndex(String text) {
        int i;
        for (i = 0; i < text.length(); i++) {
            if (!Character.isAlphabetic(text.charAt(i))) {
                break;
            }
        }
        return i;
    }

    public class R {

        public final int rowNum;
        public final int startColumn;
        public final int endColumn;
        public final Map<String, String> atts;
        final List<C> cells;

        public R(int rowNum, int startColumn, int endColumn, Map<String, String> atts) {
            this.rowNum = rowNum;
            this.startColumn = startColumn;
            this.endColumn = endColumn;
            this.atts = atts;
            this.cells = new ArrayList<C>(endColumn - startColumn + 1);
        }

        public C[] getCells() {
            return cells.toArray(new C[cells.size()]);
        }

        @Override
        public String toString() {
            return "R [r=" + rowNum + "]";
        }

    }

    public class C {

        public final int rowNum;
        public final int columnNum;
        public final Map<String, String> atts;
        public final String type;
        Object value;

        String formula;

        public C(int rowNum, int columnNum, Map<String, String> atts) {
            this.rowNum = rowNum;
            this.columnNum = columnNum;
            this.type = atts.get("t");
            this.atts = atts;
        }

        public String getStringValue() {
            return value != null ? value.toString() : null;
        }

        public String getFormula() {
            return formula;
        }

        public boolean isSimpleCell() {
            return atts.keySet().size() == 1 && "1".equals(atts.get("s"));
        }

        public boolean isNumberCell() {
            return "1".equals(atts.get("s")) && atts.get("t") == null;
        }

        public boolean isFormulaCell() {
            return formula != null;
        }

        public boolean isRefCell() {
            return atts.get("s") != null && "s".equals(atts.get("t"));
        }

        @Override
        public String toString() {
            return "C [r=" + rowNum + ", c=" + columnNum + ", v=" + value + "]";
        }

    }
}