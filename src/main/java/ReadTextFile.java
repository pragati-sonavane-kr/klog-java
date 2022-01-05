import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReadTextFile {
    public static void main(String[] args) throws IOException, ParseException {
        // Date date = new Date();

        LocalDate date = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String strDate = date.format(formatter);
        FileUtils.forceMkdir(new File("C://Users//"+strDate));
        System.out.println("Date Format with MM/dd/yyyy : "+strDate);
        Scanner sc = new Scanner(System.in);    //System.in is a standard input stream
        System.out.print("Enter KLOg file");
        String klog = sc.next();
        List<KlogObj> klogObjs = readFileLineByLine(klog);
        Scanner scan = new Scanner(System.in);    //System.in is a standard input stream
        System.out.print("Enter tlog file");
        String tlog = scan.next();
        List<KlogObj> tlogObjs = readFileLineByLine(tlog);
        List<KlogObj> listCommon = new ArrayList<>();
        Collection<KlogObj> diffKlog = new HashSet<>();
        klogObjs.stream()
                .filter(emp1 -> tlogObjs.stream().noneMatch(emp2 -> emp2.getCorelationId().equals(emp1.getCorelationId())))
                .forEach(diffKlog::add);
        Collection<KlogObj> difftlog = new HashSet<>();

        tlogObjs.stream()
                .filter(emp2 -> klogObjs.stream().noneMatch(emp1 -> emp1.getCorelationId().equals(emp2.getCorelationId())))
                .forEach(difftlog::add);

        Collection<KlogObj> commonObj = new HashSet<>();
        klogObjs.stream()
                .filter(emp1 -> tlogObjs.stream().anyMatch(emp2 -> emp2.getCorelationId().equals(emp1.getCorelationId())))
                .forEach(commonObj::add);
        writeToExcel(commonObj, difftlog, diffKlog);
        if (!diffKlog.isEmpty()) {
            for (KlogObj obj : diffKlog) {
                if (!obj.getPayload().contains("LCws")) {
                    FileUtils.forceMkdir(new File("C://Users//"+strDate+"//"+strDate+"_diff_paylod_bin//"));

                    File file = new File("C://Users//"+strDate+"//"+strDate+"_diff_paylod_bin//" + "klog_"+strDate+"_"+obj.getCorelationId()+"_"+obj.getDivision()+"_"+obj.getStore() + ".bin");

                    createBinFile(obj, file);


                } else {
                    FileUtils.forceMkdir(new File("C://Users//"+strDate+"//"+strDate+"_lcws_error_bin//"));


                    File file = new File("C://Users//"+strDate+"//"+strDate+"_lcws_error_bin//" + "klog_"+strDate+"_"+obj.getCorelationId()+"_"+obj.getDivision()+"_"+obj.getStore() + ".bin");

                    createBinFile(obj, file);
                }
            }
        }
        if (!difftlog.isEmpty()) {
            for (KlogObj obj : difftlog) {
                if (!obj.getPayload().contains("LCws")) {
                    FileUtils.forceMkdir(new File("C://Users//"+strDate+"//"+strDate+"_diff_paylod_bin//"));

                    File file = new File("C://Users//"+strDate+"//"+strDate+"_diff_paylod_bin//" + "klog_"+strDate+"_"+obj.getCorelationId()+"_"+obj.getDivision()+"_"+obj.getStore() + ".bin");

                    createBinFile(obj, file);
                } else {
                    FileUtils.forceMkdir(new File("C://Users//"+strDate+"//"+strDate+"_lcws_error_bin//"));

                    File file = new File("C://Users//"+strDate+"//"+strDate+"_lcws_error_bin//" + "klog_"+strDate+"_"+obj.getCorelationId()+"_"+obj.getDivision()+"_"+obj.getStore() + ".bin");

                    createBinFile(obj, file);
                }
            }

        }
        if (!commonObj.isEmpty()) {
            for (KlogObj obj : commonObj) {
                if (!obj.getPayload().contains("LCws")) {
                    FileUtils.forceMkdir(new File("C://Users//"+strDate+"//"+strDate+"_common_regular_payload_bin//"));

                    File file = new File("C://Users//"+strDate+"//"+strDate+"_common_regular_payload_bin//" + "klog_"+strDate+"_"+obj.getCorelationId()+"_"+obj.getDivision()+"_"+obj.getStore() + ".bin");

//Create the file
                    createBinFile(obj, file);
                } else {
                    FileUtils.forceMkdir(new File("C://Users//"+strDate+"//"+strDate+"_lcws_error_bin//"));


                    File file = new File("C://Users//"+strDate+"//"+strDate+"_lcws_error_bin//" + "klog_"+strDate+"_"+obj.getCorelationId()+"_"+obj.getDivision()+"_"+obj.getStore() + ".bin");

                    createBinFile(obj, file);
                }

            }
        }
        System.out.println("Different" + "Tlog" + difftlog + "Klog  " + diffKlog);
    }

    private static void createBinFile(KlogObj obj, File file) throws IOException {
        if (file.createNewFile()) {
            Base64.Decoder decoder = Base64.getMimeDecoder();
            byte[] decodedBytes = decoder.decode(obj.getPayload());

            FileUtils.writeByteArrayToFile(file, decodedBytes);

        } else {
            System.out.println("File already exists.");

        }
    }

    private static void writeToExcel(Collection<KlogObj> commonObj, Collection<KlogObj> difftlog, Collection<KlogObj> diffKlogObjs) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFRow row;
        String[] columns = {"DATE", "CORRELATION ID", "DIVISION", "STORE", "PRESENT IN (TLOG/KLOG) ERROR XML", "STATUS", "COMMENT"};
        // Create a Sheet
        Sheet sheet = workbook.createSheet("Klog");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setWrapText(true);

        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerCellStyle.setBorderRight(BorderStyle.MEDIUM);
        headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            headerRow.setHeightInPoints(56);
            // headerRow.au(1000000);
            cell.setCellValue(columns[i]);
            cell.getSheet().autoSizeColumn(i);
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum = 1;

        insertIntoExcelForCommon(commonObj, diffKlogObjs, difftlog, sheet, rowNum, workbook, columns);

        Scanner sc = new Scanner(System.in);    //System.in is a standard input stream
        System.out.print("Enter excel path to write file");
        // This data needs to be written (Object[])
        FileOutputStream out = new FileOutputStream(
                new File(sc.next()));

        workbook.write(out);
        workbook.close();
        out.close();
    }


    private static void insertIntoExcelForCommon(Collection<KlogObj> klogOnj, Collection<KlogObj> diffKlog, Collection<KlogObj> diffTlog, Sheet sheet, int rowNum, Workbook workbook, String[] colStrings) {

        if (!klogOnj.isEmpty()) {

            for (KlogObj obj : klogOnj) {
                Row rows = sheet.createRow(rowNum++);
                rows.setHeightInPoints(56);

                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);

                cellStyle.setWrapText(true);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setBorderTop(BorderStyle.MEDIUM);

                sheet.setColumnWidth(0, 5000);
                sheet.setColumnWidth(1, 10000);
                sheet.setColumnWidth(6, 10000);

                rows.createCell(0)
                        .setCellValue(obj.getDateTime());
                rows.getCell(0).setCellStyle(cellStyle);
                rows.createCell(1)
                        .setCellValue(obj.getCorelationId());
                rows.getCell(1).setCellStyle(cellStyle);
                rows.createCell(2)
                        .setCellValue(obj.getDivision());
                rows.getCell(2).setCellStyle(cellStyle);
                rows.createCell(3)
                        .setCellValue(obj.getStore());
                rows.getCell(3).setCellStyle(cellStyle);
                rows.createCell(4)
                        .setCellValue("Both");
                rows.getCell(4).setCellStyle(cellStyle);
                rows.createCell(5)
                        .setCellValue("Invalid");
                rows.getCell(5).setCellStyle(cellStyle);
                if(!obj.getComment().contains("LCws")) {
                    rows.createCell(6)
                            .setCellValue(obj.getComment() + " But present in both files");
                    rows.getCell(6).setCellStyle(cellStyle);

                }else{
                    rows.createCell(6)
                            .setCellValue(obj.getComment());
                    rows.getCell(6).setCellStyle(cellStyle);
                }
            }
        }
        if (!diffKlog.isEmpty()) {
            for (KlogObj obj : diffKlog) {
                Row rows = sheet.createRow(rowNum++);
                rows.setHeightInPoints(56);

                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setWrapText(true);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setBorderTop(BorderStyle.MEDIUM);


                sheet.setColumnWidth(0, 5000);
                sheet.setColumnWidth(1, 10000);
                sheet.setColumnWidth(6, 10000);

                rows.createCell(0)
                        .setCellValue(obj.getDateTime());
                rows.getCell(0).setCellStyle(cellStyle);
                rows.createCell(1)
                        .setCellValue(obj.getCorelationId());
                rows.getCell(1).setCellStyle(cellStyle);
                rows.createCell(2)
                        .setCellValue(obj.getDivision());
                rows.getCell(2).setCellStyle(cellStyle);
                rows.createCell(3)
                        .setCellValue(obj.getStore());
                rows.getCell(3).setCellStyle(cellStyle);
                rows.createCell(4)
                        .setCellValue("Present In Klog");
                rows.getCell(4).setCellStyle(cellStyle);
                rows.createCell(5)
                        .setCellValue("Invalid");
                rows.getCell(5).setCellStyle(cellStyle);
                rows.createCell(6)
                        .setCellValue(obj.getComment() + "Present In Klog");
                rows.getCell(6).setCellStyle(cellStyle);


            }
        }
        if (!diffTlog.isEmpty()) {
            for (KlogObj obj : diffTlog) {
                Row rows = sheet.createRow(rowNum++);
                rows.setHeightInPoints(56);
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setWrapText(true);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setBorderTop(BorderStyle.MEDIUM);


                sheet.setColumnWidth(0, 5000);
                sheet.setColumnWidth(1, 10000);
                sheet.setColumnWidth(6, 10000);

                rows.createCell(0)
                        .setCellValue(obj.getDateTime());
                rows.getCell(0).setCellStyle(cellStyle);
                rows.createCell(1)
                        .setCellValue(obj.getCorelationId());
                rows.getCell(1).setCellStyle(cellStyle);
                rows.createCell(2)
                        .setCellValue(obj.getDivision());
                rows.getCell(2).setCellStyle(cellStyle);
                rows.createCell(3)
                        .setCellValue(obj.getStore());
                rows.getCell(3).setCellStyle(cellStyle);
                rows.createCell(4)
                        .setCellValue("Present In Tlog");
                rows.getCell(4).setCellStyle(cellStyle);
                rows.createCell(5)
                        .setCellValue("Invalid");
                rows.getCell(5).setCellStyle(cellStyle);
                rows.createCell(6)
                        .setCellValue(obj.getComment() + "Present In Tlog");
                rows.getCell(6).setCellStyle(cellStyle);


            }
        }
    }

    private static List<KlogObj> readFileLineByLine(String klog) {
        BufferedReader reader;
        List<KlogObj> objList = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(
                    klog));
            String line = reader.readLine();

            while (line != null) {
                String[] arrOfStr = line.split("StoreError");


                String[] arrOfStrs = arrOfStr[1].split(" ");
                KlogObj klogObj = new KlogObj();
                for (String a : arrOfStrs) {

                    if (a.contains("Division")) {
                        String[] str = a.split("=");
                        klogObj.setDivision(str[1].replace("\"", ""));
                    }
                    if (a.contains("><Component")) {
                        String[] str = a.split("=");
                        String[] spl = str[1].split(">");
                        klogObj.setStore(spl[0].replace("\"", ""));
                    }
                    if (a.contains("CorrelationId")) {
                        String[] str = a.split("=");
                        klogObj.setCorelationId(str[1].replace("\"", ""));
                    }
                    if (a.endsWith("</Payload></")) {
                        String[] str = a.split(">");
                        if (str[1].contains("LCws")) {
                            klogObj.setValid(false);
                            klogObj.setComment("Repeated “LCws” characters.");
                            klogObj.setStatus("Invalid");
                        } else {
                            klogObj.setValid(true);
                            klogObj.setComment("Looks like regular payload.");
                            klogObj.setStatus("Invalid");
                        }
                        String payld = str[1].replace("</Payload", "");
                        klogObj.setPayload(payld);
                    }
                    if (a.contains("DateTime")) {
                        String[] str = a.split("=");
                        String[] splitDate = str[1].split("T");
                        klogObj.setDateTime(splitDate[0].replace("\"", ""));
                    }

                }

                objList.add(klogObj);


                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objList;
    }

}
