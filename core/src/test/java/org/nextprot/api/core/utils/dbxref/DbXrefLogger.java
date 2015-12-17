package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbXrefLogger implements Closeable, Flushable {

    private final PrintWriter pw;
    private final PrintWriter log;

    public DbXrefLogger(String outName, String logName) throws FileNotFoundException {

        Preconditions.checkNotNull(outName);
        Preconditions.checkNotNull(logName);

        pw = new PrintWriter(outName);
        log = new PrintWriter(logName);

        pw.write("entry ac\tdb\txref ac\turl\thttp status\tresolved url\thttp status\n");
    }

    public void append(String entryAc, List<DbXref> xrefs) throws IOException {

        Set<String> visitedLinkedURLs = new HashSet<>();

        for (DbXref xref : xrefs) {

            log.flush();

            String resolvedUrl = xref.getResolvedUrl();

            String linkedURL = xref.getLinkUrl();

            if (!visitedLinkedURLs.contains(linkedURL)) {

                Response response = requestUrls(xref, log);

                int j=0;
                int tries = 3;
                while (response.getResolvedUrlHttpStatus().equals("TIMEOUT") && j<tries) {

                    response = requestUrls(xref, log);
                    j++;
                }

                String db = xref.getDatabaseName();
                String xrefAc = xref.getAccession();
                String url = xref.getUrl();

                pw.write(entryAc);
                pw.write("\t");
                pw.write(db);
                pw.write("\t");
                pw.write(xrefAc);
                pw.write("\t");
                pw.write(url);
                pw.write("\t");
                pw.write(response.getUrlHttpStatus());
                pw.write("\t");
                pw.write(resolvedUrl);
                pw.write("\t");
                pw.write(response.getResolvedUrlHttpStatus());
                pw.write("\n");

                visitedLinkedURLs.add(linkedURL);
            }
        }
    }

    @Override
    public void flush() {

        pw.flush();
        log.flush();
    }

    @Override
    public void close() {

        pw.close();
        log.close();
    }

    private Response requestUrls(DbXref xref, Writer log) throws IOException {

        String url = xref.getUrl();
        String urlHttpStatus = getResponseCode(url, log);
        String resolvedUrlHttpStatus = getResponseCode(xref.getResolvedUrl(), log);

        return new Response(urlHttpStatus, resolvedUrlHttpStatus);
    }

    private String getResponseCode(String url, Writer log) throws IOException {

        String response;
        HttpURLConnection con = null;

        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("HEAD");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            log.write("Http HEAD request "+url+"\n");
            response = String.valueOf(con.getResponseCode());

        } catch (SocketTimeoutException e) {
            log.write(e.getMessage()+"\n");
            response = "TIMEOUT";
        } catch (ProtocolException e) {
            log.write(e.getMessage()+"\n");
            response = "PROTOCOL";
        } catch (MalformedURLException e) {
            log.write(e.getMessage()+"\n");
            response = "MALFORMEDURL";
        } catch (IOException e) {
            log.write(e.getMessage()+"\n");
            response = "IO";
        }

        if (con != null)
            con.disconnect();

        return response;
    }

    private static class Response {

        String urlHttpStatus;
        String resolvedUrlHttpStatus;

        public Response(String urlHttpStatus, String resolvedUrlHttpStatus) {
            this.urlHttpStatus = urlHttpStatus;
            this.resolvedUrlHttpStatus = resolvedUrlHttpStatus;
        }

        public String getUrlHttpStatus() {
            return urlHttpStatus;
        }

        public String getResolvedUrlHttpStatus() {
            return resolvedUrlHttpStatus;
        }
    }
}
