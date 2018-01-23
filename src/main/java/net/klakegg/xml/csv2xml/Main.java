package net.klakegg.xml.csv2xml;

import com.csvreader.CsvReader;
import net.klakegg.xml.csv2xml.jaxb.CsvFileType;
import net.klakegg.xml.csv2xml.jaxb.FieldType;
import net.klakegg.xml.csv2xml.jaxb.LineType;
import net.klakegg.xml.csv2xml.jaxb.ObjectFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author erlend
 */
public class Main {

    private static final JAXBContext JAXB_CONTEXT;

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(CsvFileType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void main(String... args) {
        Arguments arguments = new Arguments();

        CmdLineParser cmdLineParser = new CmdLineParser(arguments);
        cmdLineParser.getProperties().withUsageWidth(80);

        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            cmdLineParser.printUsage(System.err);
            System.err.println();
            return;
        }

        try (InputStream inputStream = arguments.getInputStream();
             OutputStream outputStream = arguments.getOutputStream()) {

            // Prepare XML output
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(outputStream);
            xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
            xmlStreamWriter.writeStartElement("", "CsvFile", "urn:fdc:klakegg.net:2018:xml:csv2xml:CsvFile-1");

            // Open CSV for reading
            CsvReader csvReader = new CsvReader(inputStream, StandardCharsets.UTF_8);
            csvReader.setDelimiter(arguments.getDelimiter());
            csvReader.setEscapeMode(arguments.getEscapeMode());

            // Read headers
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();

            // Prepare marshalling
            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

            // For each line
            while (csvReader.readRecord()) {
                String[] record = csvReader.getValues();

                // Construct XML-line
                LineType lineType = new LineType();
                for (int i = 0; i < headers.length; i++) {
                    FieldType fieldType = new FieldType();
                    fieldType.setKey(headers[i]);
                    fieldType.setValue(record[i]);
                    lineType.getField().add(fieldType);
                }

                // Marshal!
                marshaller.marshal(OBJECT_FACTORY.createLine(lineType), xmlStreamWriter);
            }

            // Complete XML output
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
        } catch (IOException | XMLStreamException | JAXBException e) {
            e.printStackTrace();
        }
    }
}
