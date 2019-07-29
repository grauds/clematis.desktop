package jworkspace.weather

import com.google.common.base.Stopwatch
import groovy.transform.TupleConstructor
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.lang3.math.NumberUtils
import org.apache.log4j.Logger

@TupleConstructor
abstract class AbstractCsvReader {

    private static final Logger LOG = Logger.getLogger(AbstractCsvReader.class)

    boolean hasHeader

    String csvFileName

    abstract def mapToItems(CSVParser records)

    protected def read() {
        def stopwatch = Stopwatch.createStarted()
        LOG.info(String.format("Start reading {}", csvFileName))

        def records = readRecordsFromCsvFile()
        def items = mapToItems(records)

        LOG.info(String.format("Finish reading {}, number of records: {}, time: {}", csvFileName, records.recordNumber, stopwatch.stop()))
        items
    }

    protected CSVParser readRecordsFromCsvFile() {
        Reader fileReader = getReader()
        def records = getDefaultParser().parse(fileReader)

        records
    }

    protected Float safeParseFloat(String text) {
        return NumberUtils.isParsable(text) ? Float.parseFloat(text) : null
    }

    protected Float safeParseInteger(String text) {
        return NumberUtils.isParsable(text) ? Integer.parseInt(text) : null
    }

    protected InputStreamReader getReader() {
        new InputStreamReader(AbstractCsvReader.getResourceAsStream(csvFileName))
    }

    private CSVFormat getDefaultParser() {
        def parser = CSVFormat.DEFAULT
        parser = parser.withDelimiter((char)';')
        hasHeader ? parser.withFirstRecordAsHeader() : parser
    }
}