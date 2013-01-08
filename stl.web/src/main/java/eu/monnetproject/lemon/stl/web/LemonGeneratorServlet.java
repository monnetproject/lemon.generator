/**
 * ********************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *******************************************************************************
 */
package eu.monnetproject.lemon.stl.web;

import eu.monnetproject.label.LabelExtractorFactory;
import eu.monnetproject.lang.Language;
import eu.monnetproject.lemon.LemonModel;
import eu.monnetproject.lemon.LemonSerializer;
import eu.monnetproject.lemon.generator.GeneratorActor;
import eu.monnetproject.lemon.generator.LemonGeneratorConfig;
import eu.monnetproject.lemon.generator.lela.LeLAManager;
import eu.monnetproject.ontology.Ontology;
import eu.monnetproject.ontology.OntologySerializer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.lexinfo.LexInfo;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author John McCrae
 */
public class LemonGeneratorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OntologySerializer ontoSerializer;
    private final Iterable<GeneratorActor> generatorActors;
    private final LabelExtractorFactory lef;
    private final LemonSerializer lemonSerializer = LemonSerializer.newInstance(new LexInfo());
    private final Map<String, LemonGenerationFuture> futures = new HashMap<String, LemonGenerationFuture>();

    public LemonGeneratorServlet(Iterable<GeneratorActor> generatorActors, LabelExtractorFactory lef, OntologySerializer ontoSerializer) {
        this.generatorActors = generatorActors;
        this.lef = lef;
        this.ontoSerializer = ontoSerializer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().length() < 2) {
            resp.setContentType("text/html");
            resp.getWriter().print("<html><head>Lemon Generator</head><body>\n"
                    + "<form action='generate' method='post' enctype='multipart/form-data'><br/>\n"
                    + "<label for='ontology'>Ontology</label><input type='file' name='ontology'><br/>\n"
                    + "<label for='customLabel'>Custom Label</label><input type='text' name='customLabel'><br/>\n"
                    + "<label for='inferLang'>Infer language</label><input type='checkbox' name='inferLang'><br/>\n"
                    + "<label for='unlanged'>Default language</label><input type='text' name='unlanged'><br/>\n"
                    + "<label for='useDefaultLEP'>Infer labels for URIs</label><input type='checkbox' name='useDefaultLEP'><br/>\n"
                    + "<label for='lexiconName'>Lexicon Name(s)</label><input type='text' name='lexiconName'><br/>\n"
                    + "<label for='lexiconNamingPattern'>Lexicon Naming Pattern</label><input type='text' name='lexiconNamingPattern'><br/>\n"
                    + "<label for='entryNamingPattern'>Entry Naming Pattern</label><input type='text' name='entryNamingPattern'><br/>\n"
                    + "<label for='otherNamingPattern'>Other Naming Pattern</label><input type='text' name='otherNamingPattern'><br/>\n"
                    + "<input type='submit'/>"
                    + "</form>"
                    + "</body>"
                    + "</html>");
        } else if (req.getPathInfo().equals("/poll")) {
            final String id = req.getParameter("future");
            if (id != null && futures.containsKey(id)) {
                final LemonGenerationFuture future = futures.get(id);
                resp.setContentType("text/plain");
                final PrintWriter out = resp.getWriter();
                out.println(future.lastMessage());
                out.println(future.lastProgress());
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No future with ID " + id);
            }
        } else if (req.getPathInfo().equals("/report")) {
            final String id = req.getParameter("future");
            if (id != null && futures.containsKey(id)) {
                final LemonGenerationFuture future = futures.get(id);
                resp.setContentType("text/plain");
                final PrintWriter out = resp.getWriter();
                out.println(ReportWriter.writeReport(future.report()));
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No future with ID " + id);
            }
        } else if (req.getPathInfo().equals("/data")) {
            final String id = req.getParameter("future");
            if (id != null && futures.containsKey(id)) {
                final LemonGenerationFuture future = futures.get(id);
                resp.setContentType("application/rdf+xml");
                resp.setStatus(HttpServletResponse.SC_OK);
                final PrintWriter out = resp.getWriter();
                lemonSerializer.write(future.getModel(), out);
                out.flush();
                futures.remove(id);
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No future with ID " + id);
            }

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            if (!isMultipart) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post must be multipart");
            } else {

                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                @SuppressWarnings("unchecked")
                List<FileItem> items = upload.parseRequest(req);
                final LemonGeneratorConfig config = new LemonGeneratorConfig();
                Ontology ontology = null;
                boolean future = false;
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        if (item.getFieldName().equalsIgnoreCase("customLabel") && item.getString().length() > 0) {
                            config.customLabel = URI.create(item.getString());
                        } else if (item.getFieldName().equalsIgnoreCase("inferLang") && item.getString().length() > 0) {
                            config.inferLang = Boolean.parseBoolean(item.getString());
                        } else if (item.getFieldName().equalsIgnoreCase("languages") && item.getString().length() > 0) {
                            final String[] langStrs = item.getString().split(",");
                            final Set<Language> langs = new HashSet<Language>();
                            for (String langStr : langStrs) {
                                langs.add(Language.get(langStr.trim()));
                            }
                            config.languages = langs;
                        } else if (item.getFieldName().equalsIgnoreCase("unlanged") && item.getString().length() > 0) {
                            config.unlanged = Language.get(item.getString());
                        } else if (item.getFieldName().equalsIgnoreCase("useDefaultLEP") && item.getString().length() > 0) {
                            config.useDefaultLEP = Boolean.parseBoolean(item.getString());
                        } else if (item.getFieldName().equalsIgnoreCase("lexiconName") && item.getString().length() > 0) {
                            config.lexiconName = item.getString();
                        } else if (item.getFieldName().equalsIgnoreCase("entryNamingPattern") && item.getString().length() > 0) {
                            config.entryNamingPattern = item.getString();
                        } else if (item.getFieldName().equalsIgnoreCase("lexiconNamingPattern") && item.getString().length() > 0) {
                            config.lexiconNamingPattern = item.getString();
                        } else if (item.getFieldName().equalsIgnoreCase("otherNamingPattern") && item.getString().length() > 0) {
                            config.otherNamingPattern = item.getString();
                        } else if (item.getFieldName().equalsIgnoreCase("future") && item.getString().length() > 0) {
                            future = Boolean.parseBoolean(item.getString());
                        } else if (item.getString().length() > 0) {
                            System.err.println("Unrecognised parameter " + item.getFieldName());
                        }
                    } else {
                        if (item.getFieldName().equalsIgnoreCase("ontology")) {
                            ontology = ontoSerializer.read(new InputStreamReader(item.getInputStream()));
                        } else {
                            System.err.println("Unrecognised parameter " + item.getFieldName());
                        }
                    }
                }
                if (ontology == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No ontology");
                } else {
                    if (future) {
                        final LemonGenerationFuture lgf = new LemonGenerationFuture(ontology, config, generatorActors, lef);
                        futures.put(lgf.id(), lgf);
                        new Thread(lgf).start();
                        resp.setContentType("text/plain");
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().print(lgf.id());
                    } else {
                        final LeLAManager generator = new LeLAManager(generatorActors, lef);
                        final LemonModel model = generator.doGeneration(ontology, config);
                        resp.setContentType("application/rdf+xml");
                        resp.setStatus(HttpServletResponse.SC_OK);
                        final PrintWriter out = resp.getWriter();
                        lemonSerializer.write(model, out);
                        out.flush();
                    }
                }
            }
        } catch (Exception x) {
            throw new ServletException(x);
        }
    }
}
