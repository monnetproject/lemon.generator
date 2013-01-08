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
 ********************************************************************************
 */
package eu.monnetproject.lemon.stl.web;

import eu.monnetproject.lemon.generator.ActorGenerationReport;
import eu.monnetproject.lemon.generator.EntryGenerationReport;
import eu.monnetproject.lemon.generator.GenerationReport;
import java.net.URI;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class ReportWriter {

    public static String writeReport(GenerationReport report) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<URI, EntryGenerationReport> entry : report.getEntries().entrySet()) {
            writeEntry(sb, entry.getKey(), entry.getValue());
        }
        return sb.toString();
    }

    private static void writeEntry(StringBuilder sb, URI entryURI, EntryGenerationReport entryReport) {
        for (ActorGenerationReport actorReport : entryReport.getActorReports()) {
            writeActor(sb, entryURI, entryReport.getEntryURI(), actorReport);
        }
    }

    private static void writeActor(StringBuilder sb, URI entryURI, URI entryURI0, ActorGenerationReport actorReport) {
        sb.append("\"").append(entryURI)
                .append("\",\"").append(entryURI0)
                .append("\",\"").append(escape(actorReport.getActorName()))
                .append("\",\"").append(escape(actorReport.getMessage()))
                .append("\",\"").append(escape(actorReport.getStatus().toString()))
                .append("\"\n");

    }

    private static String escape(String s) {
        return s.replaceAll("\"", "\\\\\"");
    }
}
