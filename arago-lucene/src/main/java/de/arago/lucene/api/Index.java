package de.arago.lucene.api;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

public class Index<T> implements Closeable {

    private final IndexConfig config;
    private volatile IndexWriter writer;
    private volatile IndexSearcher searcher;
    private volatile long indexModificationTime = 0;

    public Index(IndexConfig config) {
        this.config = config;
    }

    public String getName() {
        return config.getName();
    }

    protected IndexSearcher getSearcher() {
        File dir = new File(config.getPath());
        long modTime = dir.lastModified();
        if (indexModificationTime!=modTime)
            closeSearcher();
        if(searcher == null) {
            try {
                FSDirectory directory = NIOFSDirectory.open(new File(config.getPath()));
                //searcher = new IndexSearcher(directory);
                IndexReader reader = IndexReader.open(directory);
                searcher = new IndexSearcher(reader);
                indexModificationTime = modTime;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return searcher;
    }

    protected synchronized IndexWriter getWriter() {
        if (writer == null) {
            try {
                FSDirectory directory = NIOFSDirectory.open(new File(config.getPath()));

                try {
                    if (IndexWriter.isLocked(directory)) {
                        IndexWriter.unlock(directory);
                    }
                } catch(OverlappingFileLockException ex) {
                    IndexWriter.unlock(directory);
                }

                writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_43,config.getAnalyzer()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return writer;
    }

    private Converter<T> createConverter() {
        try {
            return (Converter<T>) config.getConverterClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public synchronized void optimize() {
        IndexWriter w = getWriter();

        try {
            w.forceMerge(1);
        } catch (Exception e) {
            try {
                w.rollback();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            throw new RuntimeException(e);
        } finally {
            closeWriter();
        }

    }

    private synchronized void closeWriter() {
        try {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    // blank
                } finally {
                    try {
                        if (IndexWriter.isLocked(writer.getDirectory())) {
                            IndexWriter.unlock(writer.getDirectory());
                        }
                    } catch (IOException ex) {
                        // blank
                    }
                }

                try {
                    writer.getDirectory().close();
                } catch (IOException ex) {
                    // blank
                }
            }
        } catch (AlreadyClosedException ex) {
            // blank
        } finally {
            writer = null;
        }
    }

    private synchronized void closeSearcher() {
        searcher = null;
    }

    @Override
    public void close() {
        closeSearcher();
        closeWriter();
    }

    public synchronized void replace(T o) {
        closeSearcher();
        remove(o);
        update(o);
        close();
    }

    public synchronized void update(T o) {
        Term id = createConverter().toLuceneID(o);
        Document document = createConverter().toLuceneDocument(o);

        IndexWriter w = null;

        try {
            w = getWriter();

            w.updateDocument(id, document);
//			w.commit();
        } catch (Exception e) {
            try {
                if (w != null) w.rollback();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            // TODO do stuff with exceptions
            e.printStackTrace(System.err);
        }

    }

    public synchronized void commit() {
        try {
            if (writer != null) writer.commit();
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * remember to commit at the end of softRemove block if you want read changes from index
     *
     * @param o
     */
    public synchronized void softRemove(T o) {
        Term remove = createConverter().toLuceneID(o);

        try {
            IndexWriter w = getWriter();

            if (remove != null) {
                w.deleteDocuments(remove);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public synchronized void remove(T o) {
        Term remove = createConverter().toLuceneID(o);

        try {
            IndexWriter w = getWriter();

            if (remove != null) {
                w.deleteDocuments(remove);
                w.commit();
                closeSearcher();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public Query parse(final String q) {
        try {
            return new QueryParser(Version.LUCENE_43,Converter.FIELD_CONTENT, config.getAnalyzer()).parse(q);
        } catch(ParseException ex) {
            throw new RuntimeException("could not parse query " + q, ex);
        }
    }

    public Converter<T> query(String q, int maxResults) {
        return query(parse(q), maxResults);
    }

    public Converter<T> query(String q, TopDocsCollector collector, int maxResults) {
        return query(parse(q), collector, maxResults);
    }

    public Converter<T> query(Query q, int maxResults) {

        return query(q, TopScoreDocCollector.create(maxResults,true), maxResults);
    }

    public long count(Query q) {
        TotalHitCountCollector collector = new TotalHitCountCollector();

        try {
            IndexSearcher s = getSearcher();
            s.search(q, collector);

            return collector.getTotalHits();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Converter<T> query(Query q, TopDocsCollector collector, int maxResults) {

        Converter<T> converter = createConverter();

        try {
            IndexSearcher s = getSearcher();
            s.search(q, collector);
            converter.setResult(collector.topDocs().scoreDocs, s);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return converter;
    }

    public synchronized void delete() {
        try {
            File directory = ((FSDirectory) getWriter().getDirectory()).getDirectory();

            close();
            FileUtils.deleteDirectory(directory);
        } catch (Exception ignored) {
        }
    }

    public boolean exists() {
        return new File(config.getPath()).exists();
    }

    public IndexConfig getConfig() {
        return config;
    }

    public static String escape(String in) {
        return QueryParser.escape(in);
    }
}
