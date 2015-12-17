package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class DbXrefUrlVisitor implements Closeable, Flushable {

    private static final Logger LOGGER = Logger.getLogger(DbXrefUrlVisitor.class.getSimpleName());

    private final PrintWriter pw;
    private final Set<String> visitedLinkedURLs;
    private final Map<String, Set<String>> dbxrefNon200HttpStatusMap;

    public DbXrefUrlVisitor(String outName, String logName) throws IOException {

        Preconditions.checkNotNull(outName);
        Preconditions.checkNotNull(logName);

        pw = new PrintWriter(outName);

        FileHandler fileHandler = new FileHandler(logName);
        fileHandler.setFormatter(new SimpleFormatter());

        LOGGER.addHandler(fileHandler);

        visitedLinkedURLs = new HashSet<>();
        dbxrefNon200HttpStatusMap = new HashMap<>();

        pw.write("entry ac\tdb\txref ac\turl\thttp status\tresolved url\thttp status\n");
    }

    public void visit(String entryAc, List<DbXref> xrefs) throws IOException {

        for (DbXref xref : xrefs) {

            String resolvedUrl = xref.getResolvedUrl();

            String linkedURL = xref.getLinkUrl();

            if (!visitedLinkedURLs.contains(linkedURL)) {

                Response response = requestUrls(xref);

                int j=0;
                int tries = 3;
                while (response.getResolvedUrlHttpStatus().equals("TIMEOUT") && j<tries) {

                    response = requestUrls(xref);
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

                if (!response.isUrlOK()) addUrlStatus(response, db+" => "+url);
                if (!response.isResolvedUrlOK()) addUrlStatus(response, db+" => "+resolvedUrl);

                visitedLinkedURLs.add(linkedURL);
            }
        }
    }

    private void addUrlStatus(Response response, String url) {

        if (!dbxrefNon200HttpStatusMap.containsKey(response.getUrlHttpStatus())) {

            dbxrefNon200HttpStatusMap.put(response.getUrlHttpStatus(), new HashSet<String>());
        }
        dbxrefNon200HttpStatusMap.get(response.getUrlHttpStatus()).add(url);
    }

    @Override
    public void flush() {

        pw.flush();
    }

    @Override
    public void close() {

        if (!dbxrefNon200HttpStatusMap.isEmpty()) {

            StringBuilder sb = new StringBuilder("\n\nUnsuccessful requests\n---------------------\n");

            for (Map.Entry<String, Set<String>> entry : dbxrefNon200HttpStatusMap.entrySet()) {

                sb.append(entry.getKey()).append(":\n");
                for (String url : entry.getValue()) {
                    sb.append("\t").append(url).append("\n");
                }
            }

            LOGGER.info(sb.toString());
        }

        dbxrefNon200HttpStatusMap.clear();
        visitedLinkedURLs.clear();

        pw.close();
    }

    private Response requestUrls(DbXref xref) throws IOException {

        String url = xref.getUrl();
        String urlHttpStatus = getResponseCode(url);
        String resolvedUrlHttpStatus = getResponseCode(xref.getResolvedUrl());

        return new Response(urlHttpStatus, resolvedUrlHttpStatus);
    }

    private String getResponseCode(String url) throws IOException {

        String response;
        HttpURLConnection con = null;

        URL obj = new URL(url);

        try {
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("HEAD");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.connect();

            LOGGER.info("Http HEAD request "+url+"\n");
            response = String.valueOf(con.getResponseCode());

        } catch (SocketTimeoutException e) {
            response = "TIMEOUT";
            LOGGER.warning(response+"; "+e.getMessage()+"\n");
        } catch (ProtocolException e) {
            response = "PROTOCOL";
            LOGGER.warning(response+"; "+e.getMessage()+"\n");
        } catch (MalformedURLException e) {
            response = "MALFORMEDURL";
            LOGGER.warning(response+"; "+e.getMessage()+"\n");
        } catch (IOException e) {
            response = "IO";
            LOGGER.warning(response+"; "+e.getMessage()+"\n");
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

        String getUrlHttpStatus() {
            return urlHttpStatus;
        }

        String getResolvedUrlHttpStatus() {
            return resolvedUrlHttpStatus;
        }

        boolean isUrlOK() {
            return urlHttpStatus.equals("200");
        }

        boolean isResolvedUrlOK() {
            return resolvedUrlHttpStatus.equals("200");
        }
    }
}
