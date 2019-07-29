package jworkspace.weather

import org.apache.commons.csv.CSVParser
import org.apache.log4j.Logger

import java.text.DateFormat
import java.text.SimpleDateFormat

@GrabResolver(name = 'Maven Central', root = 'http://repo1.maven.org/')

@Grab(group = 'commons-io', module = 'commons-io', version = '2.6')
@Grab(group = 'org.apache.commons', module = 'commons-lang3', version = '3.5')
@Grab(group = 'log4j', module = 'log4j', version = '1.2.16')

@Grab('org.apache.poi:poi-ooxml:4.0.0')
@Grab('org.apache.commons:commons-csv:1.5')

@Grab('de.siegmar:fastcsv:1.0.3')

/**
 * # Weather station Moscow, Russia, WMO_ID=27612,selection from 01.02.2005 till 01.02.2006, all days
 * # Encoding: Unicode
 * # The data is provided by the website 'Reliable Prognosis', rp5.ru
 * # If you use the data, please indicate the name of the website.
 * # For meteorological parameters see the address http://rp5.ru/archive.php?wmo_id=27612&lang=en
 * #
 *
 * @param fileName
 * @return array of observations
 */
class CsvReader extends AbstractCsvReader {

    private static final Logger LOG = Logger.getLogger(CsvReader.class)

    static String DATE_FORMAT = "DD.MM.YYYY hh:mm"

    static DateFormat df = new SimpleDateFormat(DATE_FORMAT)

    CsvReader(boolean hasHeader, String csvFileName) {
        super(hasHeader, csvFileName)
    }

    protected InputStreamReader getReader() {
        new InputStreamReader(AbstractCsvReader.getResourceAsStream(csvFileName), "UTF-16BE")
    }

    @Override
    def mapToItems(CSVParser records) {
        int counter = 0;
        records.collect {
            // "T";"Po";"P";"Pa";"U";"DD";"Ff";"ff10";"ff3";"N";"WW";"W1";"W2";"Tn";"Tx";"Cl";"Nh";
            // "H";"Cm";"Ch";"VV";"Td";"RRR";"tR";"E";"Tg";"E'";"sss"
            int pos = 0
            try {
                return new Observation(
                        weatherStationId: 27612,
                        date: df.parse(it.get(pos++)),
                        t: safeParseFloat(it.get(pos++)),
                        pO: safeParseFloat(it.get(pos++)),
                        p: safeParseFloat(it.get(pos++)),
                        pA: safeParseFloat(it.get(pos++)),
                        u: safeParseFloat(it.get(pos++)),
                        dd: WindDirection.fromString(it.get(pos++)),
                        ff: it.get(pos++),
                        ff10: it.get(pos++),
                        ff3: it.get(pos++),
                        n: it.get(pos++),
                        ww: it.get(pos++),
                        w1: it.get(pos++),
                        w2: it.get(pos++),
                        tn: safeParseFloat(it.get(pos++)),
                        tx: safeParseFloat(it.get(pos++)),
                        cl: it.get(pos++),
                        nh: it.get(pos++),
                        h: it.get(pos++),
                        cm: it.get(pos++),
                        ch: it.get(pos++),
                        vv: safeParseFloat(it.get(pos++)),
                        td: safeParseFloat(it.get(pos++)),
                        rrr: safeParseFloat(it.get(pos++)),
                        tr: safeParseFloat(it.get(pos++)),
                        e: it.get(pos++),
                        tg: safeParseFloat(it.get(pos++)),
                        eApostrophe: it.get(pos++),
                        sss: safeParseFloat(it.get(pos))
                )
            } catch (IllegalArgumentException ignored) {
                // skip the record
                counter++
                String record = "Unparseable record " + it.values
                String message = String.format(" in %s column: %s" , pos, ignored.getMessage())
                LOG.error(record + message)
                println record + message
                return null
            }
        }
    }
}

List<Observation> result = new CsvReader(true, "27612.01.02.2005.01.02.2006.1.0.0.en.unic.00000000.csv").read()
result.addAll((List<Observation>)new CsvReader(true, "27612.01.02.2006.01.02.2010.1.0.0.en.unic.00000000.csv").read())
result.addAll((List<Observation>)new CsvReader(true, "27612.01.02.2010.01.02.2015.1.0.0.en.unic.00000000.csv").read())
result.addAll((List<Observation>)new CsvReader(true, "27612.01.02.2015.28.07.2019.1.0.0.en.unic.00000000.csv").read())
println "Observations parsed: " + result.size()