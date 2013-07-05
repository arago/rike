package de.arago.lucene.api;

import java.util.Properties;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class IndexConfig {

    private final String name;
    private Class<? extends Converter<?>> converterClass;
    private String path;
    private Analyzer analyzer = null;
    private final Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    public IndexConfig(String name) {
        this.name = name;
    }

    public void setConverterClass(Class<? extends Converter<?>> converterClass) {
        this.converterClass = converterClass;
    }

    public void setPath(String path) {
        if (path == null || path.isEmpty()) throw new IllegalArgumentException("path cannot be empty");
        this.path = path;
    }

    public Class<? extends Converter<?>> getConverterClass() {
        return converterClass;
    }

    public String getPath() {
        return path;
    }

    public void setAnalyzer(Analyzer a) {
        analyzer = a;
    }

    public Analyzer getAnalyzer() {
        if(analyzer==null)
            analyzer = new StandardAnalyzer(Version.LUCENE_43);
        return analyzer;
    }

    public String getName() {
        return name;
    }
}
