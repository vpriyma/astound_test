package ua.astound.test.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HttpRequest {

    private static final Logger LOG = LoggerFactory.getLogger("HttpClient");
    private static final String DEFAULT_ENCODING = "UTF-8";
    private boolean withAuth;
    private String login;
    private String password;
    private HttpRequestBase rawRequest;
    private boolean fullLogEnable = false;
    private boolean logResponseBody = true;
    private String succeededLog;
    private String requestLog;
    private String responseLog;

    // ***************************************************************************************************************
    HttpRequest(final String url, final HttpMethod method) {
        fullLogEnable = System.getProperties().containsKey("ws.loggger.enable") && Boolean.parseBoolean(System.getProperty("ws.loggger.enable"));
        if (method.equals(HttpMethod.GET)) {
            rawRequest = new HttpGet(url);
        } else if (method.equals(HttpMethod.PUT)) {
            rawRequest = new HttpPut(url);
        } else if (method.equals(HttpMethod.POST)) {
            rawRequest = new HttpPost(url);
        } else if (method.equals(HttpMethod.DELETE)) {
            rawRequest = new HttpDelete(url);
        } else if (method.equals(HttpMethod.PATCH)) {
            rawRequest = new HttpPatch(url);
        } else if (method.equals(HttpMethod.DELETE_WITH_BODY)) {
            rawRequest = new HttpDeleteWithBody(url);
        } else if (method.equals(HttpMethod.OPTIONS)) {
            rawRequest = new HttpOptions(url);
        } else {
            throw new HttpRequestException(method.toString() + " is unsupported for now!");
        }
    }

    public static HttpRequest get(final String url) {
        return new HttpRequest(url, HttpMethod.GET);
    }

    public static HttpRequest get(final String url, final Object... urlArguments) {
        return get(String.format(url, urlArguments));
    }

    public static HttpRequest put(final String url) {
        return new HttpRequest(url, HttpMethod.PUT);
    }

    public static HttpRequest put(final String url, final Object... urlArguments) {
        return put(String.format(url, urlArguments));
    }

    public static HttpRequest post(final String url) {
        return new HttpRequest(url, HttpMethod.POST);
    }

    public static HttpRequest post(final String url, final Object... urlArguments) {
        return post(String.format(url, urlArguments));
    }

    public static HttpRequest delete(final String url) {
        return new HttpRequest(url, HttpMethod.DELETE);
    }

    public static HttpRequest delete(final String url, final Object... urlArguments) {
        return delete(String.format(url, urlArguments));
    }

    public static HttpRequest deleteWithBody(final String url) {
        return new HttpRequest(url, HttpMethod.DELETE_WITH_BODY);
    }

    public static HttpRequest deleteWithBody(final String url, final Object... urlArguments) {
        return deleteWithBody(String.format(url, urlArguments));
    }

    public static HttpRequest options(final String url) {
        return new HttpRequest(url, HttpMethod.OPTIONS);
    }

    public static HttpRequest options(final String url, final Object... urlArguments) {
        return options(String.format(url, urlArguments));
    }

    private static boolean isBodyApplicableTo(final HttpRequestBase request) {
        return (request.getClass().equals(HttpPut.class) || request.getClass().equals(HttpPatch.class)
                || request.getClass().equals(HttpPost.class) || request.getClass().equals(HttpDeleteWithBody.class));
    }

    public static HttpRequest patch(final String url) {
        return new HttpRequest(url, HttpMethod.PATCH);
    }

    public static HttpRequest patch(final String url, final Object... urlArguments) {
        return patch(String.format(url, urlArguments));
    }

    public HttpRequest addHeader(final String key, final String value) {
        rawRequest.addHeader(key, value);
        return this;
    }

    public HttpRequest addAccept(final String value) {
        return addHeader("Accept", value);
    }

    public HttpRequest addUserAgent(final String value) {
        return addHeader("User-Agent", value);
    }

    public HttpRequest addContentType(final String value) {
        return addHeader("Content-Type", value);
    }

    public HttpRequest addBasicAuth(final String login, final String password) {
        withAuth = true;
        this.login = login;
        this.password = password;
        String encodedAuthorization = "Basic " + Base64.encodeBase64String((login + ":" + password).getBytes());
        addHeader("Authorization", encodedAuthorization);
        return this;
    }

    public HttpRequest addBearerTokenAuth(final String accessToken) {
        addHeader("Authorization", String.format("Bearer %s", accessToken));
        return this;
    }

    public HttpRequest addCSRFToken() {
        Cookie cookie = DefaultSecureHttpClient.getCookies().stream().filter(co -> co.getName().equals("XSRF-TOKEN"))
                .findFirst().orElse(null);
        return Objects.nonNull(cookie) ? addHeader("X-XSRF-TOKEN", cookie.getValue()) : this;
    }

    public HttpRequest addBody(final String body) {
        if (isBodyApplicableTo(rawRequest)) {
            try {
                ((HttpEntityEnclosingRequestBase) rawRequest)
                        .setEntity(new ByteArrayEntity(body.getBytes(DEFAULT_ENCODING)));
            } catch (UnsupportedEncodingException e) {
                throw new HttpRequestException("Exception during assign body to request", e);
            }
        } else {
            LOG.error("Cannot assign body to this http method!");
        }
        return this;
    }

    public HttpRequest addBodyAsFormData(final Map<String, String> formData) {
        StringBuilder formBuider = new StringBuilder();
        for (Map.Entry<String, String> formEntry : formData.entrySet()) {
            try {
                formBuider.append(URLEncoder.encode(formEntry.getKey(), DEFAULT_ENCODING));
                formBuider.append("=");
                formBuider.append(URLEncoder.encode(formEntry.getValue(), DEFAULT_ENCODING));
                formBuider.append("&");
            } catch (UnsupportedEncodingException e) {
                throw new HttpRequestException("constructing groovy failed", e);
            }
        }
        formBuider.deleteCharAt(formBuider.length() - 1);
        return addBody(formBuider.toString());
    }

    public HttpRequest doWithLogs() {
        fullLogEnable = true;
        return this;
    }

    public HttpRequest doNotLogResponseBody() {
        logResponseBody = false;
        return this;
    }

    private void printLogs() {
        LOG.info(requestLog);
        LOG.info(responseLog);
    }

    private void processLogs() {
        if (fullLogEnable)
            printLogs();
        else
            LOG.info("Request: {}", succeededLog);
    }

    public HttpResponseWrapper sendAndGetResponse() {
        return sendAndGetResponse(-1);
    }

    public HttpResponseWrapper sendRequestWithSeleniumCookies(final int expectedResponseCode,
                                                              final Set<org.openqa.selenium.Cookie> seleniumCookies) {
        HttpContext seleniumCtx = getContextForSeleniumCookies(seleniumCookies);
        HttpResponseWrapper responseWrapper = sendAndGetResponse(expectedResponseCode, seleniumCtx);
        return responseWrapper;
    }

    public HttpResponseWrapper sendRequestWithSeleniumCookiesWithRetry(final int retryCount,
                                                                       final int expectedResponseCode, final Set<org.openqa.selenium.Cookie> seleniumCookies) {
        HttpContext seleniumCtx = getContextForSeleniumCookies(seleniumCookies);
        HttpResponseWrapper response = null;
        for (int i = 0; i < retryCount; i++) {
            response = sendAndGetResponse(-1, seleniumCtx);
            FrameworkUtils.sleepCurrentThread(4000);
            if (response.getStatusCode() == expectedResponseCode) {
                return response;
            }
        }
        return response;
    }

    private HttpContext getContextForSeleniumCookies(final Set<org.openqa.selenium.Cookie> seleniumCookies) {
        BasicCookieStore tmpCookieStore = new BasicCookieStore();
        final Set<Cookie> transformedCookies = transformSeleniumCookiesToApache(seleniumCookies);
        tmpCookieStore.addCookies(transformedCookies.toArray(new Cookie[transformedCookies.size()]));
        HttpContext ctx = new BasicHttpContext();
        ctx.setAttribute(HttpClientContext.COOKIE_STORE, tmpCookieStore);
        return ctx;
    }

    private Set<Cookie> transformSeleniumCookiesToApache(final Set<org.openqa.selenium.Cookie> seleniumCookies) {
        Set<Cookie> apacheCookies = new HashSet<>();
        for (org.openqa.selenium.Cookie cookie : seleniumCookies) {
            String newCookieName = cookie.getName();
            BasicClientCookie newCookie = new BasicClientCookie(newCookieName, cookie.getValue());
            newCookie.setDomain(cookie.getDomain());
            newCookie.setPath(cookie.getPath());
            newCookie.setExpiryDate(cookie.getExpiry());
            apacheCookies.add(newCookie);
        }
        return apacheCookies;
    }

    public HttpResponseWrapper sendAndGetResponse(final int expectedResponseCode) {
        return sendAndGetResponse(expectedResponseCode, DefaultSecureHttpClient.getLocalContext());
    }

    public HttpResponseWrapper sendAndGetResponse(final int expectedResponseCode, final HttpContext ctx) {
        HttpResponseWrapper response = null;
        try {
            // rawRequest.addHeader("User-Agent", "autotests");
            logRequest(rawRequest);
            HttpResponse httpResponse = getHttpClient().execute(rawRequest, ctx);
            response = new HttpResponseWrapper(httpResponse);
            logResponse(response);
            if (expectedResponseCode != -1) {
                int actualCode = response.getStatusCode();
                new HttpRequestException(expectedResponseCode, response.getStatusCode(),
                                         String.format("EXCEPTION DETAILS:\n%s\n%s", requestLog, responseLog))
                        .throwIf(actualCode != expectedResponseCode);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            printLogs();
            throw new HttpRequestException(e.getMessage());
        }
        processLogs();
        return response;
    }

    public HttpResponseWrapper sendAndGetResponseDownload(final String fileName, final int expectedResponseCode) {
        return sendAndGetResponseDownload(fileName, expectedResponseCode, DefaultSecureHttpClient.getLocalContext());
    }

    public String executeMultiPartRequest(File file) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        HttpResponseWrapper response = null;
        try {

            InputStream stream = new FileInputStream(file);
            multipartEntityBuilder.addBinaryBody("file", stream,
                                                 org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM, file.getName());
            HttpEntity entity = multipartEntityBuilder.build();
            ((HttpPost) rawRequest).setEntity(entity);
            HttpResponse httpResponse = getHttpClient().execute(rawRequest);

            response = new HttpResponseWrapper(httpResponse);
            logResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public HttpResponseWrapper sendAndGetResponseDownload(final String fileName, final int expectedResponseCode,
                                                          final HttpContext ctx) {
        HttpResponseWrapper response = null;
        try {
            logRequest(rawRequest);
            HttpResponse httpResponse = getHttpClient().execute(rawRequest, ctx);
            getDownloadFile(httpResponse, fileName);
            if (expectedResponseCode != -1) {
                int actualCode = httpResponse.getStatusLine().getStatusCode();
                new HttpRequestException(expectedResponseCode, httpResponse.getStatusLine().getStatusCode(), "")
                        .throwIf(actualCode != expectedResponseCode);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            printLogs();
            throw new HttpRequestException(e.getMessage());
        }
        processLogs();
        return response;
    }

    public List<String> getPdfFileAsList(final HttpContext ctx) {
        InputStream is = null;
        try {
            HttpResponse httpResponse = getHttpClient().execute(rawRequest, ctx);
            is = httpResponse.getEntity().getContent();
            PDDocument document = PDDocument.load(is);
            document.getClass();
            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                PDFTextStripper tStripper = new PDFTextStripper();
                String pdfFileInText = tStripper.getText(document);
                return Arrays.asList(pdfFileInText.split("\\r?\\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    private void getDownloadFile(final HttpResponse httpResponse, final String fileName) {
        String summaryOutputPath = "output/summary";
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = httpResponse.getEntity().getContent();
            Files.createDirectories(Paths.get(summaryOutputPath));
            fos = new FileOutputStream(new File(summaryOutputPath + "/" + fileName));
            int inByte;
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public HttpResponseWrapper sendRequestWithRetry(final int retryCount, final int statusCode) {
        HttpResponseWrapper response;
        for (int i = 0; i < retryCount; i++) {
            response = sendAndGetResponse(-1);
            FrameworkUtils.sleepCurrentThread(2000);
            if (response.getStatusCode() == statusCode) {
                return response;
            }
        }
        return sendAndGetResponse(statusCode);
    }

    public HttpResponseWrapper sendRequestWithRetry(final int retryCount, List<Integer> expectedStatusCodes) {
        HttpResponseWrapper response = null;
        int actualStatusCode;
        for (int i = 0; i < retryCount; i++) {
            response = sendAndGetResponse(-1);
            actualStatusCode = response.getStatusCode();
            if (expectedStatusCodes.contains(actualStatusCode))
                return response;
            FrameworkUtils.sleepCurrentThread(2000);
        }
        return response;
    }

    public HttpResponseWrapper sendAndGetResponseWithoutContext(final int expectedResponseCode) {
        HttpResponseWrapper response = null;
        try {
            logRequest(rawRequest);
            HttpResponse httpResponse = getHttpClient().execute(rawRequest);
            response = new HttpResponseWrapper(httpResponse);
            logResponse(response);

            if (expectedResponseCode != -1) {
                int actualCode = response.getStatusCode();
                new HttpRequestException(expectedResponseCode, response.getStatusCode(), response.getBody())
                        .throwIf(actualCode != expectedResponseCode);
            }

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            printLogs();
            throw new HttpRequestException(e.getMessage());
        }
        processLogs();
        return response;
    }

    public void sendWithoutResponse(final int expectedResponseCode) {
        try {
            logRequest(rawRequest);
            HttpResponse httpResponse = getHttpClient().execute(rawRequest, DefaultSecureHttpClient.getLocalContext());
            int actualCode = httpResponse.getStatusLine().getStatusCode();
            new HttpRequestException(expectedResponseCode, actualCode, httpResponse.getStatusLine().getReasonPhrase())
                    .throwIf(actualCode != expectedResponseCode);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            printLogs();
            throw new HttpRequestException(e.getMessage());
        }
        processLogs();
    }

    private void logRequest(final HttpRequestBase rawRequest) throws IOException {
        StringBuilder requestDescription = new StringBuilder("=== REQUEST ===\n");
        succeededLog = rawRequest.getRequestLine().toString();
        requestDescription.append(rawRequest.getRequestLine().toString()).append("\n");
        for (Header header : rawRequest.getAllHeaders()) {
            requestDescription.append(header).append("\n");
        }
        requestDescription.append("COOKIES:").append("\n");
        for (Cookie cookie : DefaultSecureHttpClient.getCookies()) {
            requestDescription.append(cookie).append("\n");
        }

        if (withAuth) {
            requestDescription.append("User/password: ").append(login).append("/").append(password).append("\n");
        }

        if (isBodyApplicableTo(rawRequest)) {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase) rawRequest).getEntity();
            if (entity != null) {
                requestDescription.append(EntityUtils.toString(entity));
            }
        }
        requestDescription.append("\n");
        requestLog = requestDescription.toString();
        // LOG.info(requestDescription.toString());
    }

    private void logResponse(final HttpResponseWrapper response) {
        StringBuilder responseDescription = new StringBuilder("=== RESPONSE ===\n");
        succeededLog = String.format("%s completed with code %s", succeededLog,
                                     response.getRawResponse().getStatusLine().toString());
        responseDescription.append(response.getRawResponse().getStatusLine().toString()).append("\n");
        for (Header header : response.getRawResponse().getAllHeaders()) {
            responseDescription.append(header).append("\n");
        }

        if (logResponseBody) {
            responseDescription.append(response.getBody()).append("\n");
        } else {
            responseDescription.append("-skip-body-\n");
        }

        List<Cookie> cookies = DefaultSecureHttpClient.getCookies();
        if (!cookies.isEmpty()) {
            responseDescription.append("Cookies:\n");
            for (int i = 0; i < cookies.size(); i++) {
                responseDescription.append("Cookie: " + cookies.get(i) + "\n");
            }
        }
        responseLog = responseDescription.toString();
        // LOG.info(responseDescription.toString());

    }

    public HttpResponse sendAndGetResponseWithoutContent(final int expectedResponseCode) {
        HttpResponse httpResponse;
        try {
            logRequest(rawRequest);
            httpResponse = getHttpClient().execute(rawRequest, DefaultSecureHttpClient.getLocalContext());
            int actualCode = httpResponse.getStatusLine().getStatusCode();
            new HttpRequestException(expectedResponseCode, actualCode, httpResponse.getStatusLine().getReasonPhrase())
                    .throwIf(actualCode != expectedResponseCode);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            printLogs();
            throw new HttpRequestException(e.getMessage());
        }
        processLogs();
        return httpResponse;
    }

    public HttpResponseWrapper sendAndGetResponseWithTimeout(final int expectedResponseCode, final int timeout) {
        DefaultSecureHttpClient.resetDefaultSecureHttpClient();
        DefaultSecureHttpClient.setTimeout(timeout);
        return sendAndGetResponse(expectedResponseCode);
    }

    private HttpClient getHttpClient() {
        return HttpClientFactory.getDefaultSecureClient();
    }

    public enum HttpMethod {
        GET, PUT, POST, DELETE, DELETE_WITH_BODY, OPTIONS, HEAD, CONNECT, TRACE, PATCH

    }

    public enum ContentType {
        APPLICATION_XML("application/xml"), //
        APPLICATION_JSON("application/json;charset=utf-8"), //
        APPLICATION_PDF("application/pdf;charset=UTF-8"), //
        APPLICATION_OCTET_STREAM("application/octet-stream"), FORM_DATA("application/x-www-form-urlencoded"), //
        FORM_DATA_WITH_CHARSET("application/x-www-form-urlencoded; charset=UTF-8"), //
        ANY("*/*"), //
        MOBILE_USER_AGENT("Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 "
                          + "(KHTML, like Gecko) Chrome/41.0.2272.96 Mobile Safari/537.36 "
                          + "(compatible; Googlebot/2.1; +http://www.google.com/bot.html)"), //
        PLAIN_TEXT("text/plain;charset=UTF-8"), //
        XML("text/xml;charset=UTF-8"), //
        TEXT_JAVASCRIPT("text/javascript"), //
        PKPASS("application/vnd.apple.pkpass"), //
        IMAGE_PNG("image/png"),
        TEXT_HTML("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

        private String value;

        ContentType(final String value) {

            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
