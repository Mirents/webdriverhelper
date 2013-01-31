package reporting;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVReportBuilder {
    private String pathToCsvFile;
    private List<String> columnHeaders = new ArrayList<String>();
    private List<List<String>> entries = new ArrayList<List<String>>();

    public CSVReportBuilder(String pathToCsvFile) {
        this.pathToCsvFile = pathToCsvFile;
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

    public void addColumnValues(Map<String, String> columnValues) {
        if(columnHeaders.size() == 0) {
            for(String headerValue : columnValues.keySet()) {
                columnHeaders.add(headerValue);
            }
        }
        List<String> columnValuesList = new ArrayList<String>();
        columnValuesList.addAll(columnValues.values());
        this.entries.add(columnValuesList);
    }
}
