/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.arago.lucene.api;

import java.util.Properties;
import org.apache.lucene.analysis.Analyzer;

/**
 *
 */
public interface AnalyzerFactory {
    public Analyzer create(Properties p);
}
