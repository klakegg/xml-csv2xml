package net.klakegg.xml.csv2xml;

import com.csvreader.CsvReader;
import lombok.Getter;
import org.kohsuke.args4j.Option;

import java.io.*;

/**
 * @author erlend
 */
@Getter
public class Arguments {

    @Option(name = "-comma", forbids = {"-semicolon"})
    private boolean commaDelimiter;

    @Option(name = "-semicolon", forbids = {"-comma"})
    private boolean semicolonDelimiter;

    @Option(name = "-backslash", forbids = {"-doubled"})
    private boolean backslashEscape;

    @Option(name = "-doubled", forbids = {"-backslash"})
    private boolean doubledEscape;

    @Option(name = "-source")
    private File sourceFile;

    @Option(name = "-target")
    private File targetFile;

    public InputStream getInputStream() throws IOException {
        if (sourceFile == null)
            return System.in;
        else
            return new FileInputStream(sourceFile);
    }

    public OutputStream getOutputStream() throws IOException {
        if (targetFile == null)
            return System.out;
        else
            return new FileOutputStream(targetFile);
    }

    public char getDelimiter() {
        if (commaDelimiter)
            return ',';
        else if (semicolonDelimiter)
            return ';';
        return ';';
    }

    public int getEscapeMode() {
        if (backslashEscape)
            return CsvReader.ESCAPE_MODE_BACKSLASH;
        else if (doubledEscape)
            return CsvReader.ESCAPE_MODE_DOUBLED;
        return CsvReader.ESCAPE_MODE_BACKSLASH;
    }
}
