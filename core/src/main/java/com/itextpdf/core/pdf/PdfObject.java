package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.IOException;

public class PdfObject {

    static public final byte Array = 1;
    static public final byte Boolean = 2;
    static public final byte Dictionary = 3;
    static public final byte IndirectReference = 4;
    static public final byte Name = 5;
    static public final byte Null = 6;
    static public final byte Number = 7;
    static public final byte Stream = 8;
    static public final byte String = 9;

    /**
     * Object type: Array, Boolean, Dictionary, ...
     */
    protected byte type = 0;

    /**
     * PdfDocument object belongs to. For direct objects it can be null.
     */
    protected PdfDocument pdfDocument = null;

    /**
     * If object is flushed the indirect reference is kept here.
     */
    protected PdfIndirectReference indirectReference = null;

    public PdfObject(byte type) {
        this(null, type);
    }

    public PdfObject(PdfDocument doc, byte type) {
        pdfDocument = doc;
        this.type = type;
    }

    /**
     * Gets object type.
     *
     * @return object type.
     */
    public byte getType() {
        return type;
    }

    /**
     * Flushes the object to the document.
     *
     * @throws IOException
     */
    final public void flush() throws IOException, PdfException {
        if (pdfDocument == null)
            return;
        PdfWriter writer = pdfDocument.getWriter();
        if (writer == null)
            return;
        writer.flushObject(this);
    }

    /**
     * Gets the pdf document object belongs to.
     *
     * @return pdf document associated object belongs to or null for direct objects.
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Gets the indirect reference associated with the object.
     * The indirect reference is used when flushing object to the document.
     * If no reference is associated - create a new one.
     *
     * @return indirect reference.
     */
    public PdfIndirectReference getIndirectReference() {
        if (indirectReference == null) {
            if (pdfDocument != null) {
                indirectReference = pdfDocument.getNextIndirectReference(this);
            }
        }
        return indirectReference;
    }

    public PdfObject setIndirectReference(PdfIndirectReference indirectReference) throws PdfException {
        if (this.indirectReference != null)
            throw new PdfException(PdfException.indirectReferenceAlreadyAssigned);
        this.indirectReference = indirectReference;
        return this;
    }

    /**
     * Indicates if the object can be placed to object stream.
     *
     * @return true if object can be placed to object stream, false otherwise.
     */
    public boolean canBeInObjStm() {
        return true;
    }

    public boolean isFlushed() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.getRefersTo() == null);
    }

    /**
     * Flushes the object to document.
     *
     * @param writer PdfWriter used to flush object.
     * @throws IOException
     * @throws PdfException
     */
    protected void flush(PdfWriter writer) throws IOException, PdfException {
        PdfIndirectReference indirectReference;
        if (isFlushed() || pdfDocument == null || (indirectReference = getIndirectReference()) == null)
            return;
        if (indirectReference == null)
            return;
        pdfDocument.add(indirectReference);
        if (writer.isFullCompression() && canBeInObjStm()) {
            PdfObjectStream objectStream = writer.getObjectStream();
            objectStream.addObject(this);
        } else {
            indirectReference.setOffset(writer.getCurrentPos());
            writer.writeToBody(this);
        }
        indirectReference.setRefersTo(null);
    }

}