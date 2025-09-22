package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.util.ArrayList;
import com.opencsv.CSVReaderBuilder;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    

@SuppressWarnings("unchecked")
public static String csvToJson(String csvString) {
    
    String result = "{}";
    
    try {
    
        CSVReader reader = new CSVReaderBuilder(new StringReader(csvString)).build();
        List<String[]> csvData = reader.readAll();
        
        String[] headers = csvData.get(0);
        List<String[]> dataRows = csvData.subList(1, csvData.size());
        
        JsonObject json = new JsonObject();
        
        JsonArray colHeadings = new JsonArray();
        for (String header : headers) {
            colHeadings.add(header);
        }
        json.put("ColHeadings", colHeadings);
        
        JsonArray prodNums = new JsonArray();
        JsonArray data = new JsonArray();
        
        for (String[] row : dataRows) {
            // Add a check to ensure the row is not empty or just whitespace
            if (row.length > 0 && row[0] != null && !row[0].trim().isEmpty()) {
                prodNums.add(row[0]);
                JsonArray rowArray = new JsonArray();
                for (int i = 1; i < row.length; ++i) {
                    if (headers[i].equals("Season") || headers[i].equals("Episode")) {
                        rowArray.add(Integer.valueOf(row[i]));
                    } else {
                        rowArray.add(row[i]);
                    }
                }
                data.add(rowArray);
            }
        }
        json.put("ProdNums", prodNums);
        json.put("Data", data);
        
        result = Jsoner.serialize(json);
        
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    
    return result.trim();
    
}
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
         try {
        
        // 1. Parse the JSON string into a JsonObject
        JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);

        // 2. Extract the data from the JSON object
        JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
        JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
        JsonArray data = (JsonArray) jsonObject.get("Data");

        // 3. Prepare to write the CSV output to a string
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n");

        // 4. Write the header row
        String[] headers = colHeadings.toArray(new String[0]);
        csvWriter.writeNext(headers);

        // 5. Write the data rows
        for (int i = 0; i < prodNums.size(); i++) {
            List<String> rowList = new ArrayList<>();
            rowList.add(prodNums.get(i).toString());

            JsonArray rowData = (JsonArray) data.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                Object item = rowData.get(j);
                String header = headers[j + 1]; // +1 to account for ProdNum
                
                if (header.equals("Episode") && item instanceof Number && ((Number)item).intValue() < 10) {
                    rowList.add(String.format("%02d", ((Number)item).intValue()));
                } else {
                    rowList.add(item.toString());
                }
            }
            csvWriter.writeNext(rowList.toArray(new String[0]));
        }

        // 6. Close the writer and get the result
        csvWriter.close();
        result = stringWriter.toString().replace("\r\n", "\n");
        if (result.endsWith("\n")) {
            result = result.substring(0, result.length() - 1);
        }
        
    }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
