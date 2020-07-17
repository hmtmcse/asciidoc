package com.hmtmcse.csv

import com.hmtmcse.fileutil.data.FileDirectoryListing
import com.hmtmcse.fileutil.data.TextFileData
import com.hmtmcse.fileutil.fd.FDUtil;
import com.hmtmcse.fileutil.fd.FileDirectory
import com.hmtmcse.fileutil.text.TextFile
import com.hmtmcse.parser4java.JsonProcessor
import com.hmtmcse.texttoweb.Config
import com.hmtmcse.texttoweb.common.ConfigLoader


public class CsvToJsonProcessor {

    public static String DATA_FILE_DIR = "data-file";
    private TextFile textFile
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private Config config;
    private FileDirectory fileDirectory
    private JsonProcessor jsonProcessor

    public CsvToJsonProcessor() {
        textFile = new TextFile()
        config = ConfigLoader.getConfig()
        jsonProcessor = new JsonProcessor()
        fileDirectory = new FileDirectory()
    }

    public String getSourcePath() {
        return FDUtil.concatPath(config.source, DATA_FILE_DIR)
    }

    public String getOutPath() {
        return FDUtil.concatPath(config.out, DATA_FILE_DIR)
    }

    public TextFileData getFile(String path) {
        try {
            String file = FDUtil.concatPath(getSourcePath(), path)
            if (fileDirectory.isExist(file)) {
                return textFile.fileToString(file)
            }
        } catch (Exception e) {
            println(e.getMessage())
        }
        return null
    }

    public void exportToOut() {
        String source = getSourcePath();
        String out = getOutPath();
        if (fileDirectory.isExist(source)) {
            String sourceFile
            String outFile
            String content
            List<CsvToJsonData> jsonData
            fileDirectory.createDirectoriesIfNotExist(out)
            fileDirectory.listDirRecursively(source).each { FileDirectoryListing file ->
                try {
                    sourceFile = file.fileDirectoryInfo.absolutePath - source
                    jsonData = getJsonDataFromCsvFile(sourceFile)
                    if (jsonData) {
                        content = jsonProcessor.klassToString(jsonData)
                        if (content) {
                            outFile = sourceFile.replace(".csv", ".json")
                            outFile = FDUtil.concatPath(out, outFile)
                            fileDirectory.removeAllIfExist(outFile)
                            textFile.stringToFile(outFile, content)
                        }
                    }
                } catch (Exception e) {
                    println(e.getMessage())
                }
            }
        }
    }

    public String trim(String text, String ch){
        if (text == null || text.isBlank() || text.isEmpty()){
            return ""
        }
        String data = text.substring(0,1)
        if (data.equals(ch)){
            text = text.substring(1,text.length())
        }
        data = text.substring(text.length() - 1, text.length())
        if (data.equals(ch)){
            text = text.substring(0, text.length() - 1)
        }
        return text
    }

    public String processText(String text){
        if (text == null || text.isBlank() || text.isEmpty()){
            return ""
        }
        text = trim(text, "\"")
        text = text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();
        return text
    }

    public List<CsvToJsonData> getJsonDataFromCsvFile(String path) {
        path = path?.replace(".json", ".csv")?.replace("/data-file/", "")
        TextFileData csvFile = getFile(path)
        List<CsvToJsonData> list = new ArrayList<>()
        if (csvFile) {
            CsvToJsonData csvToJsonData
            for (String eachLine : csvFile.lines) {
                List<String> line = parseLine(eachLine)
                if (line.size() < 5) {
                    continue;
                }
                csvToJsonData = new CsvToJsonData()
                csvToJsonData.english = processText(line[0])
                csvToJsonData.bangla = line[1]
                csvToJsonData.synonymous = line[2]
                csvToJsonData.englishMeaning = processText(line[3])
                csvToJsonData.example = processText(line[4])
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
