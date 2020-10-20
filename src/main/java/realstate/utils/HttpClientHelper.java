package realstate.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import lombok.Getter;

public class HttpClientHelper implements Closeable {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    private static final String ACCEPT_TEXT_HTML = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";

    @Getter
    private CloseableHttpClient httpClient;
    @Getter
    private HttpClientContext context;

    public HttpClientHelper() {
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        CookieStore cookieStore = new BasicCookieStore();
        context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        httpClient = HttpClients.custom().setUserAgent(USER_AGENT).setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
    }

    public HttpGet httpGet(String url) {
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Accept", ACCEPT_TEXT_HTML);
        return httpget;
    }

    public HttpPost httpPost(String url) {
        return httpPost(url, null);
    }

    public HttpPost httpPost(String url, List<NameValuePair> params) {
        HttpPost httpPost = new HttpPost(url);
        if (params != null && !params.isEmpty()) {
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return httpPost;
    }

    public String execute(HttpUriRequest request) throws Exception {
        return execute(request, false);
    }

    public String execute(HttpUriRequest request, boolean empty) throws Exception {
        return execute(request, empty ? emptyResponseHandler() : defaultResponseHandler());
    }

    public String execute(HttpUriRequest request, ResponseHandler<String> responseHandler) throws Exception {
        return httpClient.execute(request, responseHandler, context);
    }

    public ResponseHandler<String> defaultResponseHandler() {
        return new ResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    String responseValue = entity != null ? EntityUtils.toString(entity) : "";
                    return responseValue;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
    }

    public ResponseHandler<String> emptyResponseHandler() {
        return new ResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status == 302) {
                    return "";
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
    }

    public Map<String, String> getCookies() {
        Map<String, String> result = new LinkedHashMap<>();
        List<Cookie> cookies = context.getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            result.put(cookie.getName(), cookie.getValue());
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }

}
