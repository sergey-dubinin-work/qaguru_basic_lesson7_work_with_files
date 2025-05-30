package guru.qa;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTest {

    private final ClassLoader classLoader = getClass().getClassLoader();

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
    }

    @Test
    void downloadTest() throws Exception {

        open("https://github.com/junit-team/junit5/blob/main/LICENSE.md");
        File file = $("a[data-testid=raw-button]").download();

        try (InputStream inputStream = new FileInputStream(file)) {
            assertThat(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("Eclipse Public License");
        }
        
    }

    @Test
    void uploadFile() throws URISyntaxException {

        open("https://the-internet.herokuapp.com/upload");
        $("#file-upload").uploadFile(new File("src/test/resources/files/hello.txt"));   // 1
//        $("#file-upload").uploadFromClasspath("files/hello.txt");                       // 2
//        $("#file-upload").uploadFile(new File(classLoader.getResource("files/hello.txt").toURI()));                      // 3

        $("#file-submit").click();

        $("#content")
                .shouldBe(visible)
                .shouldHave(text("File Uploaded!"));

        $("#uploaded-files")
                .shouldHave(text("hello.txt"));

    }

    @Test
    void dragAndDropUploadFile() {
        open("https://the-internet.herokuapp.com/upload");

        // todo: Реализовать загрузку через drag'n'drop
    }
}
