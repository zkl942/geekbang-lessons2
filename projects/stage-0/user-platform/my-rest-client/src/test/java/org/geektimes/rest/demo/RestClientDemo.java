package org.geektimes.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

/**
 * Java 只提供最原始的http组件，但是提供这些规范给别人实现
 * 有了这些规范的实现就易用了
 * 所以说:
 * JAX-RS is nothing more than a specification, a set of interfaces and annotations offered by Java EE.
 * 这里我们自己即是API的实现者，也是API的使用者
 */
public class RestClientDemo {

    public static void main(String[] args) {
        RestClientDemo restClientDemo = new RestClientDemo();
        restClientDemo.doGET();
        restClientDemo.doPOST();
    }

    private void doGET() {
        System.out.println("GET request......");
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response

        String content = response.readEntity(String.class);

//        System.out.println(content);
    }

    public void doPOST() {
        System.out.println("POST request......");
        Client client = ClientBuilder.newClient();

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.put("inputEmail", Collections.singletonList("laozhikun1994@gmail.com"));
        formData.put("inputPassword", Collections.singletonList("dsgfsgdferwe"));
        formData.put("inputPhone", Collections.singletonList("11111111111"));
        formData.put("inputName", Collections.singletonList("Zhikun Lao"));

        Entity<Form> formEntity = Entity.form(formData);

        Response response = client
                .target("http://127.0.0.1:8080/register/validate")      // WebTarget
                .request() // Invocation.Builder
                .post(formEntity);                              //  Response

        String content = response.readEntity(String.class);

        System.out.println(content);
    }
}
