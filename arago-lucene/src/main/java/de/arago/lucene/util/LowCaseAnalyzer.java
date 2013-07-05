package de.arago.lucene.util;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
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
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source;
        TokenStream stream;

        if("_name_prefix".equals(fieldName)) {
            source = new NGramTokenizer(reader,mingram,maxgram);
            stream = new WordDelimiterFilter(new LowerCaseFilter(Version.LUCENE_43,source), WordDelimiterFilter.ALPHANUM, CharArraySet.EMPTY_SET);
        } else if(fieldName.startsWith("_ngram_")) {
            source = new NGramTokenizer(reader,3,4);
            stream = null;
        } else {
            source = new WhitespaceTokenizer(Version.LUCENE_43,reader);
            stream = new WordDelimiterFilter(new LowerCaseFilter(Version.LUCENE_43,source), WordDelimiterFilter.ALPHANUM, CharArraySet.EMPTY_SET);
        }

        return new TokenStreamComponents(source, stream);
    }

    /*@Override
    public TokenStream tokenStream(String string, Reader reader) {
        if("_name_prefix".equals(string)) {
            NGramTokenizer ngram = new NGramTokenizer(reader,mingram,maxgram);
            TokenStream stream = new LowerCaseFilter(Version.LUCENE_43,ngram);
            return stream;
        } else if(string.startsWith("_ngram_")) {
            NGramTokenizer ngram = new NGramTokenizer(reader,3,4);
            return ngram;
        } else {
            TokenStream stream = new WhitespaceTokenizer(Version.LUCENE_43,reader);
            stream = new LowerCaseFilter(Version.LUCENE_43,stream);
            return stream;
        }
    }*/

}