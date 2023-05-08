package com.example.demo.functionals;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.UserEntity;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.catalina.LifecycleException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.app.v1.config.AuthenticationFilter.TEST_AUTHENTICATION_HEADER;
import static com.example.demo.app.v1.config.AuthenticationFilter.TEST_AUTHENTICATION_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Component
public class MyStepdefs extends CucumberTestBase {

    private String url;
    private HttpResponse response;
    private List<NameValuePair> urlParameters;

    @Autowired
    private UserRepository userRepository;

    public MyStepdefs() throws LifecycleException, IOException {
    }

    @Given("I have a UserCreateServlet")
    public void iHaveAUserCreateServlet() throws LifecycleException {
        url = String.format("http://localhost:%s/create-user", this.port);
        startTomcat();
    }

    @When("I create a new user with name {string} and email {string} and password {string}")
    public void iCreateANewUserWithNameAndEmailAndPassword(String name, String email, String password) throws LifecycleException {

        try {

            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);

            urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("name", name));
            urlParameters.add(new BasicNameValuePair("email", email));
            urlParameters.add(new BasicNameValuePair("password", password));

            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            post.setHeader(TEST_AUTHENTICATION_HEADER, TEST_AUTHENTICATION_TOKEN);

            response = client.execute(post);

        } catch (IOException e) {
            stopTomcat();
            throw new RuntimeException(e);
        }
    }

    @Then("the new user should be saved in the database")
    public void theNewUserShouldBeSavedInTheDatabase() throws LifecycleException {
        //userRepository = getBean(UserRepositoryImpl.class);
        try {
            assertEquals(HttpServletResponse.SC_FOUND, response.getStatusLine().getStatusCode()); // 302 because servlet redirection to /list-user
            UserEntity userEntity = userRepository.getUserEntityByEmail("test@example.com");

            for (NameValuePair nameValuePair : urlParameters) {
                if ("name".equals(nameValuePair.getName())) {
                    assertEquals(userEntity.getUsername(), nameValuePair.getValue());
                }
                if ("email".equals(nameValuePair.getName())) {
                    assertEquals(userEntity.getEmail(), nameValuePair.getValue());
                }
                if ("password".equals(nameValuePair.getName())) {
                    assertNotEquals(userEntity.getPassword(), nameValuePair.getValue());
                }
            }
        } finally {
            stopTomcat();
        }
    }

}
