package de.arago.lucene.util;

import de.arago.lucene.api.AnalyzerFactory;
import java.util.Properties;
import org.apache.lucene.analysis.Analyzer;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

public class MultiAnalyzerFactory implements AnalyzerFactory {
    @Override
    public Analyzer create(Properties p) {
        KeywordAnalyzer defaultAnalyzer = new KeywordAnalyzer();
        Map analyzers = new HashMap();

        analyzers.put("_name_prefix", new LowCaseAnalyzer());


        return new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzers);
    }
}
