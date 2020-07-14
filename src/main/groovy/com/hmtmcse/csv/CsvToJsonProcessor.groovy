package com.hmtmcse.csv

import com.hmtmcse.fileutil.data.TextFileData
import com.hmtmcse.fileutil.fd.FDUtil;
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.common.ConfigLoader


public class CsvToJsonProcessor {

    public static String DATA_FILE_DIR = "data-files";
    private TextFile textFile
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public CsvToJsonProcessor() {
        textFile = new TextFile()
    }

    public String getSourcePath() {
        Config config = ConfigLoader.getConfig()
        return FDUtil.concatPath(config.source, DATA_FILE_DIR)
    }

    public TextFileData getFile(String path) {
        try {
            String file = FDUtil.concatPath(getSourcePath(), path)
            if (FileDirectory.instance().isExist(file)) {
                return textFile.fileToString(file)
            }
        } catch (Exception e) {
            println(e.getMessage())
        }
        return null
    }

    public List<CsvToJsonData> getJsonDataFromCsvFile(String path) {
        path = path?.replace(".json", ".csv")?.replace("/data-file/", "")
        TextFileData csvFile = getFile(path)
        List<CsvToJsonData> list = new ArrayList<>()
        if (csvFile) {
            CsvToJsonData csvToJsonData
            for (String eachLine : csvFile.lines) {
                List<String> line = parseLine(eachLine)
                if (line.size() < 3) {
                    continue;
                }
                csvToJsonData = new CsvToJsonData()
                csvToJsonData.english = line[0]
                csvToJsonData.bangla = line[1]
                csvToJsonData.synonymous = line[2]
                csvToJsonData.example = line[3]
                list.add(csvToJsonData)
            }
        }
        return list
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    continue;
                } else if (ch == '\n') {
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }
        result.add(curVal.toString());
        return result;
    }

}
