package com.itextpdf.forms;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.test.ExtendedITextTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfFormFieldTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void formFieldTest01() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "formFieldFile.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false);

        Map<String, PdfFormField> fields = form.getFormFields();
        PdfFormField field = fields.get("Text1");

        Assert.assertTrue(fields.size() == 6);
        Assert.assertTrue(field.getFieldName().toUnicodeString().equals("Text1"));
        Assert.assertTrue(field.getValue().toString().equals("TestField"));
    }

    @Test
    public void formFieldTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "formFieldTest02.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Rectangle rect = new Rectangle(210, 490, 150, 22);
        PdfTextFormField field = PdfFormField.createText(pdfDoc, rect, "some value", "fieldName");
        form.addField(field);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest02.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void formFieldTest03() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(sourceFolder + "formFieldFile.pdf");
        String filename = destinationFolder + "formFieldTest03.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfPage page = pdfDoc.getFirstPage();
        Rectangle rect = new Rectangle(210, 490, 150, 22);

        PdfTextFormField field = PdfFormField.createText(pdfDoc, rect, "some value", "TestField");

        form.addField(field, page);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_formFieldTest03.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void choiceFieldTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "choiceFieldTest01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(210, 490, 150, 20);

        String[] options = new String[]{"First Item", "Second Item", "Third Item", "Fourth Item"};
        PdfChoiceFormField choice = PdfFormField.createComboBox(pdfDoc, rect, options, "First Item", "TestField");

        form.addField(choice);

        Rectangle rect1 = new Rectangle(210, 250, 150, 90);

        PdfChoiceFormField choice1 = PdfFormField.createList(pdfDoc, rect1, options, "Second Item", "TestField1");
        choice1.setMultiSelect(true);
        form.addField(choice1);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_choiceFieldTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void buttonFieldTest01() throws IOException, InterruptedException {

        String filename = destinationFolder + "buttonFieldTest01.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));

        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(36, 700, 20, 20);
        Rectangle rect1 = new Rectangle(36, 680, 20, 20);

        PdfButtonFormField group = PdfFormField.createRadioGroup(pdfDoc, "1", "TestGroup");

        PdfFormField.createRadioButton(pdfDoc, rect, group, "1");
        PdfFormField.createRadioButton(pdfDoc, rect1, group, "2");

        form.addField(group);

        PdfButtonFormField pushButton = PdfFormField.createPushButton(pdfDoc, new Rectangle(36, 650, 40, 20), "push", "Capcha");
        PdfButtonFormField checkBox = PdfFormField.createCheckBox(pdfDoc, new Rectangle(36, 560, 20, 20), "1", "TestCheck");

        form.addField(pushButton);
        form.addField(checkBox);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_buttonFieldTest01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
