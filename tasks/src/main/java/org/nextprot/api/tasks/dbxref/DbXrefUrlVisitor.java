package org.nextprot.api.tasks.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Visit and check http statuses of urls and resolved urls in DbXrefs
 */
class DbXrefUrlVisitor implements Closeable, Flushable {

    private static final Logger LOGGER = Logger.getLogger(DbXrefUrlVisitor.class.getSimpleName());
    private static final int TIMEOUT = 5000;

    private final PrintWriter pw;
    private final Set<String> visitedTemplateURLs;
    private final Map<String, Set<String>> dbxrefNon200HttpStatusMap;

    DbXrefUrlVisitor(String outName, String logName) throws IOException {

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

        pw.write("accession\tdb\txref ac\turl\thttp status\tresolved url\thttp status\n");
    }

    /**
     * Visit all xrefs and report statuses into outName file
     * @param accession the accession
     * @param xrefs xrefs that belong to accession
     * @throws IOException
     */
    void visit(String accession, List<DbXref> xrefs) throws IOException {

        Preconditions.checkNotNull(xrefs);

        for (DbXref xref : xrefs) {

            String resolvedUrl = xref.getResolvedUrl(accession);

            // url template
            String dbName = xref.getDatabaseName();
            String templateURL = dbName+"^"+xref.getLinkUrl();

            if (!visitedTemplateURLs.contains(templateURL)) {

                int currentTimeOut = TIMEOUT;

                Response response = requestUrls(xref, accession, currentTimeOut);

                int j = 0;
                int tries = 3;

                while (response.getResolvedUrlHttpStatus().equals("TIMEOUT") && j < tries) {

                    currentTimeOut *= 2;
                    response = requestUrls(xref, accession, currentTimeOut);
                    j++;
                }

                String xrefAcc = xref.getAccession();
                String url = xref.getUrl();

                pw.write(accession);
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

    private Response requestUrls(DbXref xref, String accession, int timeOut) throws IOException {

        String urlHttpStatus = getResponseCode(xref, xref.getUrl(), timeOut);
        String resolvedUrlHttpStatus = getResponseCode(xref, xref.getResolvedUrl(accession), timeOut);

        return new Response(urlHttpStatus, resolvedUrlHttpStatus);
    }

    private String getResponseCode(DbXref xref, String url, int timeOut) {

        String status="-1";
        String response;
        HttpURLConnection con = null;
        String headerMessage = "xref="+xref.getAccession()+";db="+xref.getDatabaseName()+";url="+url;

        if (url == null || url.equalsIgnoreCase("none") || url.isEmpty()) {

            LOGGER.info(headerMessage+"; Cannot execute request\n");
            return "UNDEFINED URL";
        }

        try {
            URL obj = new URL(url);

            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("HEAD");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setConnectTimeout(timeOut);
            con.setReadTimeout(timeOut);
            con.connect();

            status = String.valueOf(con.getResponseCode());
            LOGGER.info(headerMessage+";status="+status);
            response = status;

        } catch (SocketTimeoutException e) {

            response = "SOCKET TIMEOUT EXCEPTION";
            LOGGER.warning(buildErrorMessage(e, status, response, headerMessage));
        } catch (ProtocolException e) {

            response = "PROTOCOL EXCEPTION";
            LOGGER.warning(buildErrorMessage(e, status, response, headerMessage));
        } catch (MalformedURLException e) {

            response = "MALFORMEDURL EXCEPTION";
            LOGGER.warning(buildErrorMessage(e, status, response, headerMessage));
        } catch (IOException e) {

            response = "IO EXCEPTION";
            LOGGER.warning(buildErrorMessage(e, status, response, headerMessage));

        } catch (IllegalArgumentException e) {

            response = "ILLEGAL ARGUMENT EXCEPTION";
            LOGGER.warning(buildErrorMessage(e, status, response, headerMessage));
        }

        if (con != null)
            con.disconnect();

        return response;
    }

    private String buildErrorMessage(Exception e, String status, String response, String headerMessage) {

        return headerMessage+";status="+status+";response="+response+";message="+e.getMessage();
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
