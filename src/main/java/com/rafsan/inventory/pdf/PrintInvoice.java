package com.rafsan.inventory.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rafsan.inventory.entity.Item;

import java.awt.print.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.rafsan.inventory.entity.Licence;
import com.rafsan.inventory.model.LicenceModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrintInvoice {

    private final ObservableList<Item> items;
    private final String barcode;
    private LicenceModel licenceModel;
    DecimalFormat df =new DecimalFormat("0.00");

    public PrintInvoice(ObservableList<Item> items, String barcode) {
        licenceModel = new LicenceModel();
        this.items = FXCollections.observableArrayList(items);
        this.barcode = barcode;
    }

    public void generateReport() {
        Licence licence = licenceModel.getLicence(1);
        try {
            Document document = new Document();
            document.setPageSize(PageSize.A7);
            document.setMargins(0,0,0,0);


            FileOutputStream fs = new FileOutputStream("Report.pdf");
            PdfWriter writer = PdfWriter.getInstance(document, fs);
            document.open();

            PdfContentByte cb = writer.getDirectContent();
            BarcodeEAN codeEAN = new BarcodeEAN();
            codeEAN.setCodeType(codeEAN.EAN13);
            codeEAN.setCode(barcode);
            codeEAN.setTextAlignment(Element.ALIGN_CENTER);
            document.add(codeEAN.createImageWithBarcode(cb, BaseColor.BLACK, BaseColor.DARK_GRAY));

            Font f=new Font(Font.FontFamily.TIMES_ROMAN,9.0f,Font.BOLD,BaseColor.BLACK);
            Paragraph paragraph = new Paragraph(licence.getCompany(), f);
            paragraph.setAlignment(Element.ALIGN_CENTER);

            document.add(paragraph);
            addEmptyLine(paragraph, 5);

            paragraph = new Paragraph(licence.getAddress1(), f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph(licence.getAddress2(), f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph(licence.getAddress3(), f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(paragraph, 1);

            document.add(paragraph);

            PdfPTable table = new PdfPTable(2);
            PdfPCell c1 = new PdfPCell(new Phrase("Date: ", f));
            c1.setBorder(Rectangle.NO_BORDER);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            c1 = new PdfPCell(new Phrase(dateFormat.format(cal.getTime()), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Ref: ", f));
            c1.setBorder(Rectangle.NO_BORDER);
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(barcode, f));
            c1.setBorder(Rectangle.NO_BORDER);
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(c1);

            document.add(table);

            paragraph = new Paragraph(" ");
            paragraph.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(paragraph, 1);

            document.add(paragraph);

            table = new PdfPTable(4);
            c1 = new PdfPCell(new Phrase("Item", f));
            c1.setBorder(Rectangle.NO_BORDER);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Price", f));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Qty", f));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Total", f));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);
            table.setHeaderRows(1);
            document.add(table);

            table = createTable();
            document.add(table);

            addEmptyLine(paragraph, 5);

            paragraph = new Paragraph("Thank you for shopping with us!", f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("_");
            document.add(paragraph);

            paragraph = new Paragraph("_");
            document.add(paragraph);

            paragraph = new Paragraph("_");
            document.add(paragraph);

            paragraph = new Paragraph("_");
            document.add(paragraph);
            paragraph = new Paragraph("_");
            document.add(paragraph);

            document.close();
        } catch (DocumentException | FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private PdfPTable createTable() {
        Font f=new Font(Font.FontFamily.TIMES_ROMAN,9.0f,Font.NORMAL,BaseColor.BLACK);

        double total = 0;
        PdfPTable table = new PdfPTable(4);
        PdfPCell c1 = null;

        for (Item i : items) {
            c1 = new PdfPCell(new Phrase(i.getItemName(), f));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf(df.format(i.getUnitPrice())), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf((int)i.getQuantity()), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf(df.format(i.getTotal())), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);
            table.addCell(c1);

            total = total + i.getTotal();
        }

        c1 = new PdfPCell(new Phrase(" "));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(" "));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(" "));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(" "));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("TOTAL", f));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(df.format(total), f));
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        return table;
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    // todo: print using fiscal receipt
    public void printReceipt() throws PrinterException, IOException {
        PDDocument document = PDDocument.load(new File("C:/Users/Administrator/Documents/IdeaProjects/JavaFX-Point-of-Sales/Report.pdf"));

        PrintService myPrintService = findPrintService("POS-80");

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(myPrintService);

        // todo: uncomment to print
        job.print();
    }

    private PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
}
