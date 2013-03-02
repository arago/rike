package de.arago.lucene.api;

import de.arago.lucene.util.IndexCreator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;

public final class IndexFactory {

    private static final ConcurrentHashMap<String, Index<?>> indices = new ConcurrentHashMap<String, Index<?>>();
    private static final String prefix = "index.";
    static final Logger logger = LogManager.getLogger(IndexFactory.class.getName());
    private static Properties settings = new Properties();

    private static Properties getDefaultProperties() {
        Properties p = new Properties();
        p.put("index.marsValidierer.converterClass", "de.arago.lucene.xmlschema.MarsSchemaConverter");
        p.put("index.marsValidierer.creatorClass", "de.arago.lucene.xmlschema.MarsSchemaIndexCreator");
        p.put("index.kiValidierer.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.kiValidierer.creatorClass", "de.arago.lucene.ki.IssueConditionIndexCreator");
        p.put("index.orga.creatorClass", "de.arago.wisdome.rike.task.TaskIndexCreator");
        p.put("index.orga.converterClass", "de.arago.wisdome.rike.task.TaskIndexConverter");
        p.put("index.mars-schema.creatorClass", "de.arago.lucene.xmlschema.MarsSchemaIndexCreator");
        p.put("index.mars-schema.converterClass", "de.arago.lucene.xmlschema.MarsSchemaConverter");
        p.put("index.issue-condition.creatorClass", "de.arago.lucene.ki.IssueConditionIndexCreator");
        p.put("index.issue-condition.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.issue-statistics.creatorClass", "de.arago.lucene.issue.IssueStatisticIndexCreator");
        p.put("index.issue-statistics.converterClass", "de.arago.lucene.issue.IssueStatisticIndexConverter");
        p.put("index.tag-names.creatorClass", "de.arago.lucene.rule.TagsIndexCreator");
        p.put("index.tag-names.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.ki-wiki-relatives.converterClass", "de.arago.lucene.util.StringMapConverter");
        p.put("index.ki-wiki-relatives.creatorClass", "de.arago.lucene.util.NoopIndexCreator");
        return p;
    }

    public static Index<?> getIndex(String name, Properties p) {
        // set defaults
        settings.putAll(getDefaultProperties());
        settings.putAll(p);
        return getIndex(name);
    }

    public static Index<?> getIndex(String name) {
        if (!indices.containsKey(name)) {
            indices.put(name, createIndex(name));
        }
        return indices.get(name);
    }

    public static Index<?> getNewIndex(String name) {
        if (indices.containsKey(name)) {
            indices.get(name).delete();
            indices.remove(name);
        }
        return getIndex(name);
    }

    @SuppressWarnings("rawtypes")
    private static Index<?> createIndex(String name) {
        IndexConfig config = new IndexConfig(name);

        String path = settings.getProperty(prefix + name + ".path");
        config.setPath(path == null ? "/tmp/" + prefix + name + ".index" : path);
        config.setProperties(settings);

        try {
            String klass = settings.getProperty(prefix + name + ".converterClass");
            Class<?> cl = Class.forName(klass);
            config.setConverterClass((Class<? extends Converter<?>>) cl);

            String aname = settings.getProperty(prefix + name + ".analyzerClass");
            Class<?> aclass = Class.forName(aname);
            config.setAnalyzer((Analyzer) aclass.newInstance());
        } catch (Exception e) {
            System.err.println("error while creating index " + name);

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
        String creatorKlass = settings.getProperty(prefix + index.getName() + ".creatorClass");
        if (creatorKlass == null) {
            creatorKlass = getDefaultProperties().getProperty(prefix + index.getName() + ".creatorClass");
        }
        if (creatorKlass == null) {
            return;
        }

        logger.info("filling index " + index.getName());

        try {
            IndexCreator<T> creator = (IndexCreator<T>) Class.forName(creatorKlass).newInstance();

            creator.fill(index);
            index.ready();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            //throw new RuntimeException(e);
        }

        logger.info("index filled " + index.getName());
    }
}
