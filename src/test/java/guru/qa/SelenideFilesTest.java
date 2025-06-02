package guru.qa;

import com.codeborne.selenide.SelenideElement;
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
    void dragAndDropUploadFile() throws URISyntaxException {
        open("https://the-internet.herokuapp.com/upload");

        // Получаем файл из ресурсов
        File file = new File(getClass().getClassLoader().getResource("files/hello.txt").toURI());

        // Находим элемент drop-зоны
        SelenideElement dropzone = $("#drag-drop-upload");

        // Создаём input[type="file"] с JS и добавляем его в DOM
        String jsCreateInput = "let input = document.createElement('input');" +
                "input.type = 'file';" +
                "input.style.display = 'none';" +
                "input.id = 'temp-upload';" +
                "document.body.appendChild(input);";
        executeJavaScript(jsCreateInput);

        // Загружаем файл в скрытый input
        $("#temp-upload").uploadFile(file);

        // Генерируем dragenter + drop событие
        String jsDnD = "var target = arguments[0];" +
                "var input = document.getElementById('temp-upload');" +
                "var rect = target.getBoundingClientRect();" +
                "var dataTransfer = new DataTransfer();" +
                "dataTransfer.items.add(input.files[0]);" +
                "['dragenter', 'drop'].forEach(function(eventType) {" +
                "  var event = new DragEvent(eventType, {" +
                "    dataTransfer: dataTransfer," +
                "    bubbles: true," +
                "    cancelable: true," +
                "    clientX: rect.left + 10," +
                "    clientY: rect.top + 10" +
                "  });" +
                "  target.dispatchEvent(event);" +
                "});";
        executeJavaScript(jsDnD, dropzone);

        $(".dz-filename").shouldHave(exactText("hello.txt"));
    }
}
