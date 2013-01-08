/****************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package eu.monnetproject.tagger.stanford;

import eu.monnetproject.lang.Language;
import eu.monnetproject.tagger.Tagger;
import eu.monnetproject.tagger.TaggerFactory;
import eu.monnetproject.util.Logging;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import eu.monnetproject.util.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author John McCrae
 */
public class StanfordTaggerFactory implements TaggerFactory {

    private static final HashMap<TaggerInfo, String> files = new HashMap<TaggerInfo, String>();
    private static final HashMap<Language, TaggerInfo> langs = new HashMap<Language, TaggerInfo>();

    static {
        files.put(new TaggerInfo(Language.ARABIC, "ar"), "/ar.ar.tagger");
        files.put(new TaggerInfo(Language.GERMAN, "stts"), "/de.stts.tagger");
        files.put(new TaggerInfo(Language.ENGLISH, "penn"), "/en.penn.tagger");
        files.put(new TaggerInfo(Language.FRENCH, "fr"), "/fr.fr.tagger");
        files.put(new TaggerInfo(Language.CHINESE, "zh"), "/zh.zh.tagger");
        for (TaggerInfo info : files.keySet()) {
            langs.put(info.language, info);
        }
    }
    private final Logger log = Logging.getLogger(this);
    private final String fileRegex = "((...?)(-...?.?)?(-...?.?)?)\\.(.+)\\.ser(\\.gz)?";
    private final HashMap<TaggerInfo, Tagger> taggerCache = new HashMap<TaggerInfo, Tagger>();

    private Tagger cache(TaggerInfo ti, Tagger t) {
        taggerCache.put(ti, t);
        return t;
    }

    public Tagger makeTagger(Language lng) {
        final TaggerInfo taggerInfo = langs.get(lng);
        if (taggerCache.containsKey(taggerInfo)) {
            return taggerCache.get(taggerInfo);
        }
        try {
            File f = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "load");
            Pattern pattern = Pattern.compile(fileRegex);
            if (!f.exists()) {
                log.warning("No load directory, cannot load artifacts");
            } else {
                for (File f2 : f.listFiles()) {
                    final Matcher matcher = pattern.matcher(f2.getName());
                    if (matcher.matches()) {
                        if (Language.get(matcher.group(1)).equals(lng)) {
                            return cache(taggerInfo,new StanfordTagger(f2));
                        }
                    }
                }
            }
            if (files.containsKey(taggerInfo)) {
                final String entryPath = files.get(taggerInfo);
                final URL resource = getClass().getResource(entryPath);
                final InputStream stream = resource.openStream();
                final File file = File.createTempFile(entryPath, null);
                file.deleteOnExit();
                final OutputStream out = new FileOutputStream(file);
                byte buf[] = new byte[1024];
                int len;
                while ((len = stream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                stream.close();
                return cache(taggerInfo,new StanfordTagger(file, entryPath.substring(1)));
            }
        } catch (Exception x) {
            Logging.stackTrace(log, x);
        }
        return null;
    }

    private static class TaggerInfo {

        private final Language language;
        private final String tagSet;

        public TaggerInfo(Language language, String tagSet) {
            this.language = language;
            this.tagSet = tagSet;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TaggerInfo other = (TaggerInfo) obj;
            if (this.language != other.language && (this.language == null || !this.language.equals(other.language))) {
                return false;
            }
            if ((this.tagSet == null) ? (other.tagSet != null) : !this.tagSet.equals(other.tagSet)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + (this.language != null ? this.language.hashCode() : 0);
            hash = 37 * hash + (this.tagSet != null ? this.tagSet.hashCode() : 0);
            return hash;
        }
    }
}
