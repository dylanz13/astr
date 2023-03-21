import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class draw {

    public static void main(String[] args) {
        int sx = 673 - 3;
        int sy = 944 - 18;
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D;
        ArrayList<Double> RA = new ArrayList<>();
        ArrayList<Double> Vrad = new ArrayList<>();
        try {
            image = new BufferedImage(
                    1346, 944, BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(new File("./img.png"));
            System.out.println("Image reading successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fs = new FileInputStream("./GalaxyMeasures.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(fs);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp = 0;


            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for(int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if(row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if(tmp > cols) cols = tmp;
                }
            }

            for(int r = 2; r < rows; r++) {
                double ra = 0;
                row = sheet.getRow(r);
                if(row != null) {
                    for(int c = 0; c < cols; c++) {
                        cell = row.getCell((short)c);
                        if (c == 2) {
                            ra += cell.getNumericCellValue();
                        } else if (c == 3) {
                            ra += cell.getNumericCellValue()/60f;
                        } else if (c == 4) {
                            ra += cell.getNumericCellValue()/3600f;
                        } else if (c == 12) {
                            Vrad.add(cell.getNumericCellValue());
                            RA.add(ra);
                        }

                    }
                }
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
        try {
            float f = 6500/12000f;
            double radius = f * (sy - 45);
            graphics2D = image.createGraphics();
            graphics2D.setPaint ( Color.RED );
            drawCenteredCircle(graphics2D, sx - radius*Math.cos(0.75*Math.PI/8 + Math.PI/2),
                    sy - radius*Math.sin(0.75*Math.PI/8 + Math.PI/2), 10);
            graphics2D.setPaint ( Color.BLACK );
            int i = 0;
            for (Double aDouble : RA) {
                radius = (Vrad.get(i)/12000f) * (sy - 45);
                if (aDouble > 14) {
                    drawCenteredCircle(graphics2D, sx + radius * Math.cos((aDouble-14) * Math.PI/8 + Math.PI/2),
                            sy - radius * Math.sin((aDouble - 14) * Math.PI/8 + Math.PI/2), 10);
                } else {
                    drawCenteredCircle(graphics2D, sx - radius*Math.cos((14-aDouble)*Math.PI/8 + Math.PI/2),
                            sy - radius * Math.sin((14-aDouble)*Math.PI/8 + Math.PI/2), 10);
                }
                i++;
            }
            int j = 0;
            for (double d : Vrad) {
                if (d > 12000) {
                    System.out.println(d + " at: "+ j);
                }
                j++;
            }
            graphics2D.dispose ();
            System.out.println("Image drawing was successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(image, "png", new File("./ima.png"));
            System.out.println("Image save was successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawCenteredCircle(Graphics2D g, double x, double y, double r) {
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval((int) x,(int) y,(int) r,(int) r);
    }
}
