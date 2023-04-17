package otus.tests;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.TestNGCitrusSupport;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pojo.UserDTO;
import pojo.UserOutDTO;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class UsersTests extends TestNGCitrusSupport {
    private TestContext context;

    @BeforeSuite
    void setupTest() throws MalformedURLException {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "chrome");
        capabilities.setCapability("browserVersion", "109.0");
        capabilities.setCapability("selenoid:options", selenoidOptions);
        RemoteWebDriver driver = new RemoteWebDriver(
                URI.create("http://127.0.0.1:4444/wd/hub").toURL(),
                capabilities
        );
    }

    @Test(description = "Create user")
    @CitrusTest
    public void createNewUser() {
        this.context = citrus.getCitrusContext().createTestContext();
        $(http()
                .client("restClientReqres")
                .send()
                .post("user")
                .message()
                .type("application/json")
                .body(new ObjectMappingPayloadBuilder(getUserDTO(), "objectMapper"))
        );

        $(http()
                .client("restClientReqres")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type("application/json")
                .body(new ObjectMappingPayloadBuilder(getUserOutDTO(), "objectMapper"))
        );

//     Получаем созданного юзера и проверяем тело, описанное в файлеGetUser.json
        run(http()
                .client("restClientReqres")
                .send()
                .get("user/" + context.getVariable("userName")));

        run(http()
                .client("restClientReqres")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type("application/json")
                .body(new ClassPathResource("GetUser.json")));
    }

    public UserDTO getUserDTO() {
        UserDTO user = new UserDTO();
        user.setEmail("user@otus.ru");
        user.setUsername("user1");
        user.setUserStatus(10L);
        user.setId(1L);
        user.setFirstName("first");
        user.setPhone("123123-123123");
        user.setLastName("last");
        user.setPassword("pass");
        return user;
    }

    public UserOutDTO getUserOutDTO() {
        UserOutDTO userOut = new UserOutDTO();
        userOut.setCode(200L);
        userOut.setType("unknown");
        userOut.setMessage("1");

        return userOut;
    }

}
