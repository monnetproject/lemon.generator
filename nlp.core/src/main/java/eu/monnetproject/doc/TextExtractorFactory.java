/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.monnetproject.doc;

/**
 *
 * @author tobwun
 */
public interface TextExtractorFactory {
    TextExtractor getService(DocumentType docType);
}
