package com.rafsan.inventory.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rafsan.inventory.entity.Item;

import java.awt.print.*;
import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import com.rafsan.inventory.entity.Licence;
import com.rafsan.inventory.entity.Sale;
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
    private final Sale sale;
    private final String barcode;
    private LicenceModel licenceModel;
    DecimalFormat df =new DecimalFormat("0.00");
    private static Properties prop = null;

    public PrintInvoice(ObservableList<Item> items, String barcode, Sale sale) {
        licenceModel = new LicenceModel();
        this.items = FXCollections.observableArrayList(items);
        this.barcode = barcode;
        this.sale = sale;
    }

    public void generateReport(Sale sale, boolean typeCopy) {

        Licence licence = licenceModel.getLicence(1);
        try {
            Document document = new Document();
            document.setPageSize(PageSize.A7);
            document.setMargins(0,0,0,0);


            FileOutputStream fs = new FileOutputStream("Report.pdf");
            PdfWriter writer = PdfWriter.getInstance(document, fs);
            document.open();
/*
            PdfContentByte cb = writer.getDirectContent();
            BarcodeEAN codeEAN = new BarcodeEAN();
            codeEAN.setCodeType(codeEAN.EAN13);
            codeEAN.setCode(barcode);
            codeEAN.setTextAlignment(Element.ALIGN_CENTER);
            document.add(codeEAN.createImageWithBarcode(cb, BaseColor.BLACK, BaseColor.DARK_GRAY));
*/
            Font f=new Font(Font.FontFamily.TIMES_ROMAN,9.0f,Font.NORMAL,BaseColor.BLACK);
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

            if (sale.getChannel() != null){
                c1 = new PdfPCell(new Phrase(sale.getChannel(), f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(""));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);
            }

            if (sale.getRrn() != null){
                c1 = new PdfPCell(new Phrase("RRN: ", f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(sale.getRrn(), f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);
            }

            if (sale.getImei() != null){
                c1 = new PdfPCell(new Phrase("IMEI: ", f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(sale.getImei(), f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);
            }

            if (sale.getPan() != null){
                c1 = new PdfPCell(new Phrase("PAN: ", f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(sale.getPan(), f));
                c1.setBorder(Rectangle.NO_BORDER);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);
            }

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

            if (typeCopy)
                document.add(table);

            table = createTable(typeCopy);
            document.add(table);

            if (!sale.getChannel().equalsIgnoreCase("CASH")){
                paragraph = new Paragraph("Approved By Pin", f);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
            }

            if (typeCopy){
                paragraph = new Paragraph("Customer Copy", f);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
            }else{
                paragraph = new Paragraph("Merchant Copy", f);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
            }

            addEmptyLine(paragraph, 5);

            paragraph = new Paragraph("Thank you for shopping with us.", f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("Please come again.", f);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.close();
        } catch (DocumentException | FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private PdfPTable createTable(boolean typeCopy) {
        Font f=new Font(Font.FontFamily.TIMES_ROMAN,9.0f,Font.NORMAL,BaseColor.BLACK);

        double total = 0;
        PdfPTable table = new PdfPTable(4);
        PdfPCell c1 = null;


        for (Item i : items) {
            c1 = new PdfPCell(new Phrase(i.getItemName(), f));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setBorder(Rectangle.NO_BORDER);

            if (typeCopy)
                table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf(df.format(i.getUnitPrice())), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);

            if (typeCopy)
                table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf((int)i.getQuantity()), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);

            if (typeCopy)
                table.addCell(c1);

            c1 = new PdfPCell(new Phrase(String.valueOf(df.format(i.getTotal())), f));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setBorder(Rectangle.NO_BORDER);

            if (typeCopy)
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

        c1 = new PdfPCell(new Phrase("SUB", f));
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

        //c1 = new PdfPCell(new Phrase(df.format(total*0.855), f));
        c1 = new PdfPCell(new Phrase(df.format(total*1), f));
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("VAT", f));
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

        //c1 = new PdfPCell(new Phrase(df.format(total*0.145), f));
        c1 = new PdfPCell(new Phrase(df.format(total*0), f));
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
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
        PDDocument document = PDDocument.load(new File("Report.pdf"));

        //PrintService myPrintService = findPrintService(prop.getProperty(" printer.name"));
        PrintService myPrintService = findPrintService("EPSON TM-T20II Receipt");

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

    public static int loadConfigs() throws IOException {
        try (InputStream input = new FileInputStream("config.properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
            return -1;
        }
        return 0;
    }
}
