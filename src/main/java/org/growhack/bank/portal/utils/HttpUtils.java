package org.growhack.bank.portal.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class HttpUtils {

    public static Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    public static String proxyHost = null;
    public static String CHARSET = "utf-8";
    public static int proxyPort = 0;

    public static int CONNECTION_TIMEOUT = 10000;
    public static int SOCKET_TIMEOUT = 10000;

    public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android; Build/003) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/19.77.34.5 Mobile Safari/537.36";

    static {
        try {
            List<String> lines = FileUtils.readLines(new File("config/proxy.dat"), CHARSET);
            proxyHost = lines.get(0);
            proxyPort = Integer.parseInt(lines.get(1));

        } catch (IOException e) {
            LOG.info("Don't have config of proxy data ... ");
        }

    }

    public static String get(String url, String charset) throws IOException {
        return Request.Get(url).execute().returnContent().asString(Charset.forName(charset));
    }

    public static String get(String url) throws IOException {
        return Request.Get(url).execute().returnContent().asString(Charset.forName("utf-8"));
    }


    private static Request createRequest(String url, boolean useProxy) {
        Request request = null;
        if (proxyHost == null || !useProxy)
            request = Request.Get(url).
                    addHeader("User-Agent", MOBILE_USER_AGENT)
                    .connectTimeout(CONNECTION_TIMEOUT)
                    .socketTimeout(SOCKET_TIMEOUT);
        else
            request = Request.Get(url).
                    addHeader("User-Agent", MOBILE_USER_AGENT)
                    .connectTimeout(CONNECTION_TIMEOUT)
                    .viaProxy(new HttpHost(proxyHost, proxyPort))
                    .socketTimeout(SOCKET_TIMEOUT);

        return request;
    }

    public static String getHttp(String url) throws IOException {

        return getHttp(url, true);
    }

    public static String getHttp(String url, boolean withoutProxy) throws IOException {
        Response response = createRequest(url, withoutProxy)
                .execute();

        HttpEntity entity = response.returnResponse().getEntity();
        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();

        String charSetName = CHARSET;

        if (charset != null) {
            charSetName = charset.displayName();
            LOG.info("Charset of page is : " + charset.toString());
        }
        InputStream is = entity.getContent();
        String tmp = IOUtils.toString(is, charSetName);
        is.close();
        return tmp;
    }


    public static String getHttpWithMobileUserAgent(String url) throws IOException {
        Response response = createRequest(url, true)
                .execute();

        HttpEntity entity = response.returnResponse().getEntity();
        ContentType contentType = ContentType.getOrDefault(entity);
        Charset charset = contentType.getCharset();

        String charSetName = "utf-8";

        if (charset != null) {
            charSetName = charset.displayName();
        }
        LOG.debug("Charset of page is : " + charSetName);
        InputStream is = entity.getContent();
        String tmp = IOUtils.toString(is, charSetName);
        is.close();
        return tmp;
    }

    public static String post(String url, String body) throws IOException {
        return Request.Post(url).useExpectContinue().version(HttpVersion.HTTP_1_1)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute().returnContent().asString();
    }

    public static void main(String[] args) throws Exception {
        String demo = "https://krakow.wyborcza.pl/krakow/7,44425,26975049,wolne-terminy-na-szczepienia-w-szpitalu-uniwersyteckim-w-krakowie.html";
        String content = HttpUtils.getHttpWithMobileUserAgent(demo);
        FileUtils.writeStringToFile(new File("demo.html"), content, "utf-8");
        System.out.println(content);
    }

}
