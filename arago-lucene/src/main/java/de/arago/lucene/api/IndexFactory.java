package de.arago.lucene.api;

import de.arago.lucene.util.IndexCreator;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;

public final class IndexFactory {

    private static final ConcurrentHashMap<String, Index<?>> indices = new ConcurrentHashMap<String, Index<?>>();
    private static final String prefix = "index.";
    static final Logger logger = Logger.getLogger(IndexFactory.class.getName());
    private static final String defaultPath;

    static {
        defaultPath = System.getProperty("de.arago.lucene.defaultPath", "/tmp/");

        try {
            final File path = new File(defaultPath);

            if (!path.exists()) path.mkdirs();
            if (!path.exists() || !path.isDirectory() || !path.canWrite()) throw new IllegalStateException("cannot use index path " + defaultPath);
        } catch(Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static Properties getDefaultProperties() {
        Properties p = new Properties();
        p.put("index.marsValidierer.converterClass", "de.arago.lucene.xmlschema.MarsSchemaConverter");
        p.put("index.marsValidierer.creatorClass", "de.arago.lucene.xmlschema.MarsSchemaIndexCreator");
        p.put("index.rike-tasks.creatorClass", "de.arago.rike.commons.util.TaskIndexCreator");
        p.put("index.rike-tasks.converterClass", "de.arago.rike.commons.util.TaskIndexConverter");
        p.put("index.mars-schema.creatorClass", "de.arago.lucene.xmlschema.MarsSchemaIndexCreator");
        p.put("index.mars-schema.converterClass", "de.arago.lucene.xmlschema.MarsSchemaConverter");
        p.put("index.issue-condition.creatorClass", "de.arago.lucene.ki.IssueConditionIndexCreator");
        p.put("index.issue-condition.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.issue-statistics.creatorClass", "de.arago.lucene.issue.IssueStatisticIndexCreator");
        p.put("index.issue-statistics.converterClass", "de.arago.lucene.issue.IssueStatisticIndexConverter");
        p.put("index.tag-names.creatorClass", "de.arago.lucene.ki.TagsIndexCreator");
        p.put("index.tag-names.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.ki-wiki-relatives.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.ki-wiki-relatives.creatorClass", "de.arago.lucene.util.NoopIndexCreator");
        p.put("index.ki-index.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.ki-index.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.ki-index.creatorClass", "de.arago.lucene.util.NoopIndexCreator");
        p.put("index.ki-index.analyzerClass", "de.arago.lucene.util.MultiAnalyzerFactory");

        return p;
    }

    public static Index<?> getIndex(String name, Properties config) {
        synchronized(indices) {
            if (!indices.containsKey(name)) {
                Properties p = new Properties();

                p.putAll(getDefaultProperties());
                if (config != null) p.putAll(config);

                indices.put(name, createIndex(name, p));
            }

            return indices.get(name);
        }
    }

    public static Index<?> getIndex(String name) {
        return getIndex(name, null);
    }

    public static Index<?> getNewIndex(String name) {
        Properties config = null;

        if (indices.containsKey(name)) {
            Index<?> index = indices.remove(name);
            config = index.getConfig().getProperties();
            index.delete();
        }

        return getIndex(name, config);
    }

    public static Index<?> getNewIndex(String name, Properties config) {
        indices.remove(name);

        return getIndex(name, config);
    }

    @SuppressWarnings("rawtypes")
    private static Index<?> createIndex(String name, Properties settings) {
        IndexConfig config = new IndexConfig(name);

        String path = settings.getProperty(prefix + name + ".path");
        config.setPath(path == null ? defaultPath + prefix + name + ".index" : path);
        config.setProperties(settings);

        try {
            String klass = settings.getProperty(prefix + name + ".converterClass");
            Class<?> cl = Class.forName(klass);
            config.setConverterClass((Class<? extends Converter<?>>) cl);

            String aname = settings.getProperty(prefix + name + ".analyzerClass");
            if (aname != null) {
                Class<?> aclass = Class.forName(aname);

                if (AnalyzerFactory.class.isAssignableFrom(aclass)) {
                    config.setAnalyzer(((AnalyzerFactory) aclass.newInstance()).create(settings));
                } else {
                    config.setAnalyzer((Analyzer) aclass.newInstance());
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "could not create index " + name, e);
            throw new ExceptionInInitializerError(e);
        }

        Index<?> index = new Index(config);

        fillIndex(index);

        return index;
    }

    public static Iterator<Index<?>> getIndices() {
        return Collections.unmodifiableCollection(indices.values()).iterator();
    }

    private static <T> void fillIndex(Index<T> index) {

        String creatorKlass = index.getConfig().getProperties().getProperty(prefix + index.getName() + ".creatorClass");
        if (creatorKlass == null) {
            logger.log(Level.WARNING, "no creator class specified for index " + index.getName());
            return;
        }

        logger.info("filling index " + index.getName());

        try {
            IndexCreator<T> creator = (IndexCreator<T>) Class.forName(creatorKlass).newInstance();

            creator.fill(index);
        } catch (Exception e) {
            logger.log(Level.WARNING, "could not fill index " + index.getName(), e);
        }

        logger.info("index filled " + index.getName());
    }
}
