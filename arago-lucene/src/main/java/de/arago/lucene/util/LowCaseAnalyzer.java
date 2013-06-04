package de.arago.lucene.util;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.util.Version;

/**
*
* @author vvoss
*/
final public class LowCaseAnalyzer extends Analyzer {
    private final int mingram;
    private final int maxgram;

    public LowCaseAnalyzer() {
        this(2, 4);
    }

    public LowCaseAnalyzer(int mingram, int maxgram) {
        this.mingram = mingram;
        this.maxgram = maxgram;
    }

    @Override
    public TokenStream tokenStream(String string, Reader reader) {
        if("_name_prefix".equals(string)) {
            NGramTokenizer ngram = new NGramTokenizer(reader,mingram,maxgram);
            TokenStream stream = new LowerCaseFilter(Version.LUCENE_36,ngram);
            return stream;
        } else if(string.startsWith("_ngram_")) {
            NGramTokenizer ngram = new NGramTokenizer(reader,3,4);
            return ngram;
        } else {
            TokenStream stream = new WhitespaceTokenizer(Version.LUCENE_36,reader);
            stream = new LowerCaseFilter(Version.LUCENE_36,stream);
            return stream;
        }
    }

}