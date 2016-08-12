package net.jquant.tools.tableformat;

import java.util.ArrayList;
import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-2.
 */
public abstract class AbstractTableFormatter implements TableFormatter {
    protected class CellData {
        private List lines;
        private int align;
        private int valign;

        private CellData() {
            super();
        }

        public CellData(int align, int valign) {
            this();
            this.lines = new ArrayList();
            this.align = align;
            this.valign = valign;
        }

        public void addLine(String text) {
            lines.add(text);
        }

        public int getLineCount() {
            return lines.size();
        }

        public List getLines() {
            return new ArrayList(lines);
        }

        public int getAlign() {
            return align;
        }

        public int getVAlign() {
            return valign;
        }
    }

    ;

    private int columnCount = 0;

    private List maxColWidths = new ArrayList();
    private List maxRowHeights = new ArrayList();

    private List tableData = new ArrayList();

    /**
     * Constructor
     */
    protected AbstractTableFormatter() {
        super();
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#addLine()
     */
    public final TableFormatter addLine() {
        return addLine(null);
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#addLine(java.lang.String)
     */
    public final TableFormatter addLine(String text) {
        if (tableData.size() <= 0) {
            throw new IllegalStateException("tableData.size()");
        }

        // Get current (last) row
        List row = (List) tableData.get(tableData.size() - 1);

        if (row.size() <= 0) {
            throw new IllegalStateException("Cannot addLine() to empty row.  Use nextCell() first.");
        }

        CellData cell = (CellData) row.get(row.size() - 1);

        // Add line to cell
        cell.addLine(text);

        //
        // Update max column width
        //

        if (maxColWidths.size() != columnCount) {
            throw new IllegalStateException("maxColWidths.size()");
        }

        int currentCol = row.size() - 1;

        int maxColWidth = ((Integer) maxColWidths.get(currentCol)).intValue();

        if (text != null && text.length() > maxColWidth) {
//            Pattern pattern = Pattern.compile("^[\\u4E00-\\u9FFF]+$");
//            Matcher matcher = pattern.matcher(text);
//            if(matcher.matches()){
//
//            }else{
//                maxColWidths.set(currentCol, new Integer(text.length()*2));
//            }
            maxColWidths.set(currentCol, new Integer(text.length()));
        }

        //
        // Update max row height
        //

        if (maxRowHeights.size() != tableData.size()) {
            throw new IllegalStateException("maxRowHeights.size()");
        }

        int currentRow = tableData.size() - 1;

        int maxRowHeight = ((Integer) maxRowHeights.get(currentRow)).intValue();

        if (cell.getLineCount() > maxRowHeight) {
            maxRowHeights.set(currentRow, new Integer(cell.getLineCount()));
        }

        return this;
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#nextCell()
     */
    public final TableFormatter nextCell() {
        return nextCell(ALIGN_DEFAULT, VALIGN_DEFAULT);
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#nextCell(int, int)
     */
    public final TableFormatter nextCell(int align, int valign) {
        if (
                (align != ALIGN_DEFAULT)
                        && (align != ALIGN_LEFT)
                        && (align != ALIGN_CENTER)
                        && (align != ALIGN_RIGHT)
                ) {
            throw new IllegalArgumentException("align");
        }

        if (
                (valign != VALIGN_DEFAULT)
                        && (valign != VALIGN_TOP)
                        && (valign != VALIGN_CENTER)
                        && (valign != VALIGN_BOTTOM)
                ) {
            throw new IllegalArgumentException("valign");
        }

        if (tableData.size() <= 0) {
            throw new IllegalStateException("tableData.size()");
        }

        List row = (List) tableData.get(tableData.size() - 1);

        CellData cell = new CellData(align, valign);

        row.add(cell);

        // Update column count
        if (row.size() > columnCount) {
            // Should only be off by 1
            if ((row.size() - 1) != columnCount) {
                throw new IllegalStateException("columnCount");
            }

            columnCount = row.size();

            maxColWidths.add(new Integer(0));
        }

        return this;
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#nextRow()
     */
    public final TableFormatter nextRow() {
        tableData.add(new ArrayList(columnCount));

        maxRowHeights.add(new Integer(0));

        return this;
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getColumnCount()
     */
    public int getColumnCount() {
        return columnCount;
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getRowCount()
     */
    public final int getRowCount() {
        if (tableData.size() <= 0) {
            throw new IllegalStateException("tableData.size()");
        }

        return tableData.size();
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getColumnWidth(int)
     */
    public final int getColumnWidth(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= columnCount) {
            throw new IllegalArgumentException("columnIndex");
        }

        return ((Integer) maxColWidths.get(columnIndex)).intValue();
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getRowHeight(int)
     */
    public final int getRowHeight(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tableData.size()) {
            throw new IllegalArgumentException("rowIndex");
        }

        return ((Integer) maxRowHeights.get(rowIndex)).intValue();
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getFormattedCell(int, int)
     */
    public final String[] getFormattedCell(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= tableData.size()) {
            throw new IllegalArgumentException("rowIndex");
        }

        if (columnIndex < 0 || columnIndex >= columnCount) {
            throw new IllegalArgumentException("columnIndex");
        }

        List lines;
        int align;
        int valign;

        int cellWidth = getColumnWidth(columnIndex);
        int cellHeight = getRowHeight(rowIndex);

        List row = (List) tableData.get(rowIndex);

        // Is there a cell at the specified row/col?
        if (row.size() > columnIndex) {
            CellData cell = (CellData) row.get(columnIndex);

            lines = cell.getLines();
            align = cell.getAlign();
            valign = cell.getVAlign();
        } else {
            lines = new ArrayList();
            align = ALIGN_DEFAULT;
            valign = VALIGN_DEFAULT;
        }

        int vpadding = cellHeight - lines.size();

        int topPad;
        int bottomPad;

        switch (valign) {
            case VALIGN_CENTER: {
                int carry = vpadding % 2;

                vpadding = vpadding - carry;

                topPad = bottomPad = vpadding / 2;

                bottomPad += carry;

                break;
            }
            case VALIGN_BOTTOM: {
                topPad = vpadding;
                bottomPad = 0;
                break;
            }
            // Deafault - Top
            default: {
                topPad = 0;
                bottomPad = vpadding;
                break;
            }
        }

        List result = new ArrayList(cellHeight);

        for (int i = 0; i < topPad; ++i) {
            result.add(getFormattedLine("", cellWidth, align));
        }

        for (int i = 0; i < lines.size(); ++i) {
            result.add(getFormattedLine((String) lines.get(i), cellWidth, align));
        }

        for (int i = 0; i < bottomPad; ++i) {
            result.add(getFormattedLine("", cellWidth, align));
        }

        if (result.size() != cellHeight) {
            throw new IllegalStateException("result.size()");
        }

        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     * getFormattedLine
     */
    private final String getFormattedLine(String text, int lineLength, int align) {
        if (text == null) {
            text = "";
        }

        int padding = lineLength - text.length();

        int leftPad;
        int rightPad;

        switch (align) {
            case ALIGN_CENTER: {
                int carry = padding % 2;

                padding = padding - carry;

                leftPad = rightPad = padding / 2;

                rightPad += carry;

                break;
            }
            case ALIGN_RIGHT: {
                leftPad = padding;
                rightPad = 0;
                break;
            }
            // Deafault - Left
            default: {
                leftPad = 0;
                rightPad = padding;
                break;
            }
        }

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < leftPad; ++i) {
            result.append(' ');
        }

        result.append(text);

        for (int i = 0; i < rightPad; ++i) {
            result.append(' ');
        }

        return result.toString();
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getTableWidth()
     */
    public int getTableWidth() {
        if (maxColWidths.size() != getColumnCount()) {
            throw new IllegalStateException("maxColWidths.size()");
        }

        int width = 0;

        for (int i = 0; i < columnCount; ++i) {
            width += ((Integer) maxColWidths.get(i)).intValue();
        }

        return width;
    }

    /*
     *  (non-Javadoc)
     * @see com.inamik.utils.TableFormatter#getTableHeight()
     */
    public int getTableHeight() {
        if (maxRowHeights.size() != getRowCount()) {
            throw new IllegalStateException("maxRowHeights.size()");
        }

        int height = 0;

        for (int i = 0, size = maxRowHeights.size(); i < size; i++) {
            height += ((Integer) maxRowHeights.get(i)).intValue();
        }

        return height;
    }
}
