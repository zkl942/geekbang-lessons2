package org.geektimes.rest.client;

import org.geektimes.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class HttpPostInvocation implements Invocation {

    private final URI uri;

    private final URL url;

    private final MultivaluedMap<String, Object> headers;

    private final Entity entity;

    HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<?> entity) {
        this.uri = uri;
        this.headers = headers;
        this.entity = entity;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Invocation property(String name, Object value) {
        return this;
    }

    private String encodeValue(String value)  {
        String res = null;
        try {
            res = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Throwable throwable) {
            System.out.println(throwable.getCause());
        }
        return res;
    }

    @Override
    public Response invoke() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            connection.setDoOutput(true);
            setRequestHeaders(connection);

            String contentType = entity.getVariant().getMediaType().toString();
            connection.setRequestProperty("Content-Type", contentType);

            String payload = null;
            /**
             * form data
             * assemble payload data from form
             */
            if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
                Form form = (Form) entity.getEntity();
                MultivaluedMap<String, String> formData = form.asMap();

                // TODO: only processing one value now
                payload = formData.keySet().stream()
                        .map(key -> key + "=" + encodeValue(formData.get(key).get(0)))
                        .collect(Collectors.joining("&"));
            } else {
                // TODO: other content types
            }

            byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Length", String.valueOf(payloadBytes.length));
            try( OutputStream os = connection.getOutputStream()) {
                os.write(payloadBytes);
                os.flush();
            }

            // TODO Set the cookies
            int statusCode = connection.getResponseCode();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(statusCode);
            return response;
        } catch (IOException e) {
            System.out.println(e.getCause());
            // TODO Error handler
        }
        return null;
    }

    private void setRequestHeaders(HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (Object headerValue : entry.getValue()) {
                connection.setRequestProperty(headerName, headerValue.toString());
            }
        }
    }

    @Override
    public <T> T invoke(Class<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public <T> T invoke(GenericType<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public Future<Response> submit() {
        return null;
    }

    @Override
    public <T> Future<T> submit(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(GenericType<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(InvocationCallback<T> callback) {
        return null;
    }
}
