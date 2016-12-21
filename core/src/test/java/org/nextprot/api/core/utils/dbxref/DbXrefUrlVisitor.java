package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Check http statuses of urls and resolved urls in DbXrefs
 */
public class DbXrefUrlVisitor implements Closeable, Flushable {

    private static final Logger LOGGER = Logger.getLogger(DbXrefUrlVisitor.class.getSimpleName());

    private final PrintWriter pw;
    private final Set<String> visitedTemplateURLs;
    private final Map<String, Set<String>> dbxrefNon200HttpStatusMap;

    public DbXrefUrlVisitor(String outName, String logName) throws IOException {

        Preconditions.checkNotNull(outName);
        Preconditions.checkNotNull(logName);

        pw = new PrintWriter(outName);

        FileHandler fileHandler = new FileHandler(logName);
        fileHandler.setFormatter(new SimpleFormatter());

        LOGGER.addHandler(fileHandler);

        visitedTemplateURLs = new HashSet<>();
        dbxrefNon200HttpStatusMap = new TreeMap<>((status1, status2) -> {

            boolean isStatus1Integer = status1.matches("\\d+");
            boolean isStatus2Integer = status2.matches("\\d+");

            // integer comes first
            if (isStatus1Integer && isStatus2Integer) {
                return Integer.parseInt(status1) - (Integer.parseInt(status2));
            }
            else if (isStatus1Integer) {
                return -1;
            }
            else if (isStatus2Integer) {
                return 1;
            }

            return status1.compareTo(status2);
        });

        pw.write("entry ac\tdb\txref ac\turl\thttp status\tresolved url\thttp status\n");
    }

    public void visit(String entryAc, List<DbXref> xrefs) throws IOException {

        if (xrefs != null) {
            for (DbXref xref : xrefs) {

                String resolvedUrl = xref.getResolvedUrl();

                // url template
                String dbName = xref.getDatabaseName();
                String templateURL = dbName+"^"+xref.getLinkUrl();

                if (!visitedTemplateURLs.contains(templateURL)) {

                    Response response = requestUrls(xref);

                    int j = 0;
                    int tries = 3;
                    while (response.getResolvedUrlHttpStatus().equals("TIMEOUT") && j < tries) {

                        response = requestUrls(xref);
                        j++;
                    }

                    String xrefAcc = xref.getAccession();
                    String url = xref.getUrl();

                    pw.write(entryAc);
                    pw.write("\t");
                    pw.write(dbName);
                    pw.write("\t");
                    pw.write(xrefAcc);
                    pw.write("\t");
                    pw.write(url);
                    pw.write("\t");
                    pw.write(response.getUrlHttpStatus());
                    pw.write("\t");
                    pw.write(resolvedUrl);
                    pw.write("\t");
                    pw.write(response.getResolvedUrlHttpStatus());
                    pw.write("\n");

                    if (!response.isUrlOK())
                        addDbNameUrlStatus(response, dbName + " => " + url);

                    if (!response.isResolvedUrlOK())
                        addResolvedDbNameUrlStatus(response, dbName + " => " + resolvedUrl);

                    visitedTemplateURLs.add(templateURL);
                }
            }
        }
    }

    private void addDbNameUrlStatus(Response response, String url) {

        if (!dbxrefNon200HttpStatusMap.containsKey(response.getUrlHttpStatus())) {

            dbxrefNon200HttpStatusMap.put(response.getUrlHttpStatus(), new HashSet<>());
        }
        dbxrefNon200HttpStatusMap.get(response.getUrlHttpStatus()).add(url);
    }

    private void addResolvedDbNameUrlStatus(Response response, String url) {

        if (!dbxrefNon200HttpStatusMap.containsKey(response.getResolvedUrlHttpStatus())) {

            dbxrefNon200HttpStatusMap.put(response.getResolvedUrlHttpStatus(), new HashSet<>());
        }
        dbxrefNon200HttpStatusMap.get(response.getResolvedUrlHttpStatus()).add(url);
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
        visitedTemplateURLs.clear();

        pw.close();
    }

    private Response requestUrls(DbXref xref) throws IOException {

        String urlHttpStatus = getResponseCode(xref, xref.getUrl());
        String resolvedUrlHttpStatus = getResponseCode(xref, xref.getResolvedUrl());

        return new Response(urlHttpStatus, resolvedUrlHttpStatus);
    }

    private String getResponseCode(DbXref xref, String url) throws IOException {

        String response;
        HttpURLConnection con = null;
        String xrefAccAndDbname = xref.getAccession()+"@"+xref.getDatabaseName();

        if (url == null || url.equalsIgnoreCase("none") || url.isEmpty()) {

            LOGGER.info(xrefAccAndDbname+": No possible request for "+url+"\n");
            return "UNDEFINED URL";
        }

        try {
            URL obj = new URL(url);

            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("HEAD");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            LOGGER.info(xrefAccAndDbname+": Http HEAD request "+url+"\n");
            con.connect();
            response = String.valueOf(con.getResponseCode());

        } catch (SocketTimeoutException e) {
            response = "TIMEOUT";
            LOGGER.warning(xrefAccAndDbname+": "+response+"; "+e.getMessage()+"\n");
        } catch (ProtocolException e) {
            response = "PROTOCOL";
            LOGGER.warning(xrefAccAndDbname+": "+response+"; "+e.getMessage()+"\n");
        } catch (MalformedURLException e) {
            response = "MALFORMEDURL";
            LOGGER.warning(xrefAccAndDbname+": "+response+"; "+e.getMessage()+"\n");
        } catch (IOException e) {
            response = "IO";
            LOGGER.warning(xrefAccAndDbname+": "+response+"; "+e.getMessage()+"\n");
        } catch (IllegalArgumentException e) {
            response = "ILLEGAL";
            LOGGER.info(xrefAccAndDbname+": "+e.getLocalizedMessage());
        }

        if (con != null)
            con.disconnect();

        return response;
    }

    private static class Response {

        String urlHttpStatus;
        String resolvedUrlHttpStatus;

        private Response(String urlHttpStatus, String resolvedUrlHttpStatus) {
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
