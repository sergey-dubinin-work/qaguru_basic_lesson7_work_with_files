package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.assertj.PdfSoftAssertions;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;


public class FileParsingTest {

    private final ClassLoader classLoader = getClass().getClassLoader();

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    void parsePdfTest() throws IOException {

        open("https://junit.org/junit5/docs/current/user-guide/");

        File pdfDownload = $(byText("PDF download")).download();

        PDF pdf = new PDF(pdfDownload);

        PdfSoftAssertions softly = new PdfSoftAssertions();
        softly.assertThat(pdf).containsText("JUnit 5 User Guide");
        softly.assertThat(pdf.numberOfPages).isEqualTo(233);
        softly.assertAll();
    }

    @Test
    void parseXlsTest() {

        open("https://filesamples.com/formats/xls");

        File file = $("div.output").$("a")
                .scrollTo()
                .shouldBe(visible, Duration.ofSeconds(8)).download();

        XLS xls = new XLS(file);

        assertThat(xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue()).isEqualTo("Months");
    }

    @Test
    void parseCSVTest() throws Exception{
        try (InputStream stream = classLoader.getResourceAsStream("files/example.csv")){
            CSVReader reader = new CSVReader(new InputStreamReader(stream));

            List<String[]> list = reader.readAll();

            assertThat(list)
                    .hasSize(3)
                    .contains(new String[] {"Jane Smith", " 25", " Los Angeles", " 89005005007"});
        }

    }

    @Test
    void readZipStreamTest() throws Exception{
        try (InputStream stream = classLoader.getResourceAsStream("files/archive.zip");
                ZipInputStream zipInputStream = new ZipInputStream(stream)
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                assertThat(zipEntry.getName()).isEqualTo("hello.txt");
            }

        }

    }


    @Test
    void readZipFileTest() throws Exception {
        ZipFile zipFile = new ZipFile(new File(classLoader.getResource("files/archive.zip").toURI()));

        ZipEntry zipEntry = zipFile.entries().nextElement();

        assertThat(zipEntry.getName()).isEqualTo("hello.txt");
    }
}
