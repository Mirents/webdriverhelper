package reporting;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReportBuilder {
    private String pathToCsvFile;
    private List<String> columnHeaders = new ArrayList<String>();
    private List<List<String>> entries = new ArrayList<List<String>>();

    public CSVReportBuilder(String pathToCsvFile) {
        this.pathToCsvFile = pathToCsvFile;
    }

    public CSVReportBuilder(String pathToCsvFile, List<String> columnHeaders) {
        this.pathToCsvFile = pathToCsvFile;
        this.columnHeaders = columnHeaders;
    }

    public void build() {
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(pathToCsvFile));
            String[] columnHeadersToWrite = new String[columnHeaders.size()];
            csvWriter.writeNext(columnHeaders.toArray(columnHeadersToWrite));
            for(List<String> entryRow : entries) {
                String[] columnEntriesToWrite = new String[entryRow.size()];
                csvWriter.writeNext(entryRow.toArray(columnEntriesToWrite));
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addColumnValues(List<String> columnValues) {
        this.entries.add(columnValues);
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<String> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }
}
