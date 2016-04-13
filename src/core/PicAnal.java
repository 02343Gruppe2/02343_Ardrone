package core;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import gui.SpaceXGUI;

public class PicAnal {
	private static BufferedImage img;
	
	
	public static void main(String[] args) {
		PicAnal.findRecs();
	}
	
	public static void savePic(String fileName) {

		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		img = SpaceXGUI.getInstance().getVPanel().getImg();
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		
		//create Mat from byte[]
		Mat imageMat = new Mat(img.getHeight(),img.getWidth(),CvType.CV_8UC3);
		imageMat.put(0, 0, pixels);
		Imgcodecs.imwrite("materials\\"+fileName,imageMat);
	}
	
	public static  void analyse() {

		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		//get buffedimage from gui and convert to byte[]
		/*img = SpaceXGUI.getInstance().getVPanel().getImg();
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		
		//create Mat from byte[]
		Mat imageMat = new Mat(img.getHeight(),img.getWidth(),CvType.CV_8UC3);
		imageMat.put(0, 0, pixels);
		
		
		// save the picture
		Imgcodecs.imwrite("materials\\test.png",imageMat);*/
		for(int i = 0; i<11;i++) {
		Mat imageMat = Imgcodecs.imread("materials\\hulaHop"+i+".png");
		Mat circles = new Mat();
		Mat grayImg = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
		double[] vCircle = new double[3];
        int radius;
        Point pt = new Point();

		Scalar scalarColorB = new Scalar(0,0,0); // black scalar
		Scalar scalarColorT = new Scalar(0,255,0); //T scalar, initial color = green
        
		Imgproc.cvtColor(imageMat, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(grayImg, grayImg, new Size(3,3),0,0);
        //Imgproc.Canny(grayImg, circles, 5, 60);
        //Param1 = higher value of the two to canny, smaller = 50%
        //Param2 = accumulator threshold
        int dp = 2, minDist = 150, minRadius = 70, maxRadius = 270, param1 = 100, param2 = 100;
        Imgproc.HoughCircles(grayImg, circles, Imgproc.CV_HOUGH_GRADIENT, dp
        		, minDist, param1, param2, minRadius, maxRadius);
        for (int x = 0; x < circles.cols(); x++) {
        	vCircle = circles.get(0,x);
        	if (vCircle == null) {
        		//TODO return so that we know that there are no more balls
        		System.err.println("no circles found");
	            break;
	        }
    		pt.set(vCircle);
	        radius = (int)Math.round(vCircle[2]);
	        
	        // draw the found circle
	        Imgproc.circle(imageMat, pt, radius, scalarColorB, 2);
	        Imgproc.circle(imageMat, pt, 1, scalarColorT, 2);
	        
        }
		Imgcodecs.imwrite("materials\\test"+i+".png",imageMat);
		}
	}
	
	public static void findRecs() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		//image from drone cam
		/*img = SpaceXGUI.getInstance().getVPanel().getImg();
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		
		//create Mat from byte[]
		Mat imageMat = new Mat(img.getHeight(),img.getWidth(),CvType.CV_8UC3);
		imageMat.put(0, 0, pixels);*/

		//image from local file
		Mat imageMat = Imgcodecs.imread("materials\\06.png");
		//create grayscale and blur
		Mat grayImg = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
		Imgproc.cvtColor(imageMat, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(grayImg, grayImg, new Size(3,3),0,0);
		Imgproc.Canny(grayImg, grayImg, 10, 50);
		Imgproc.dilate(grayImg, grayImg, new Mat());
		
		//find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		int mode = Imgproc.RETR_LIST;
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgcodecs.imwrite("materials\\09.png",grayImg);
		Imgproc.findContours(grayImg, contours, hierarchy, mode, method);
		Scalar scalarColorB = new Scalar(0,255,0); // black scalar
		List<Rect> rects = new ArrayList<Rect>();
		//Imgproc.drawContours(imageMat, contours, -1, scalarColorB,3);
		System.out.println(contours.size());
		for(int i = 0;i <contours.size();i++) {
			MatOfPoint2f outArray = new  MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), outArray, 5, true);
			MatOfPoint mop = new MatOfPoint(outArray.toArray());
			Rect rect =  Imgproc.boundingRect(mop);
			rects.add(rect);
			if(Point2D.distance(rect.tl().x, rect.tl().y, rect.br().x, rect.br().y) > 100) {
				Imgproc.rectangle(imageMat, rect.tl(), rect.br(), scalarColorB, 2);
			}
			//check for qrcode in contour
			
		}
		Imgcodecs.imwrite("materials\\08.png",grayImg);
		Imgcodecs.imwrite("materials\\07.png",imageMat);
	}
	
	public static void findRecsv2() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		Scalar scalarColorB = new Scalar(0,255,0); // black scalar
		//image from drone cam
		/*img = SpaceXGUI.getInstance().getVPanel().getImg();
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		
		//create Mat from byte[]
		Mat imageMat = new Mat(img.getHeight(),img.getWidth(),CvType.CV_8UC3);
		imageMat.put(0, 0, pixels);*/

		//image from local file
		Mat imageMat = Imgcodecs.imread("materials\\06.png");
		//create grayscale and blur
		Mat grayImg = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
		Mat blurred = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
		Mat	shapes = new Mat();
		Imgproc.GaussianBlur(imageMat, blurred, new Size(3,3),0,0);
		Imgproc.cvtColor(blurred, grayImg, Imgproc.COLOR_BGRA2GRAY); 
        Imgproc.Canny(grayImg, shapes, 30, 360);
        //Imgproc.dilate(shapes, shapes, new Mat());
        Imgcodecs.imwrite("matertials\\09.png", shapes);
        double[] vec = new double[4];
        Imgproc.HoughLinesP(shapes, shapes, 1, Math.PI/180, 50, 100, 100 );
        List<Point> points = new ArrayList<Point>();
		for (int i = 0; i<shapes.rows();i++) {
			
			vec = shapes.get(i, 0);
			double x1 = vec[0];
			double y1 = vec[1];
			double x2 = vec[2];
			double y2 = vec[3];
			
			Line2D.Double line1 = new Line2D.Double(x1, y1, x2, y2);
			List<Line2D> lines = new ArrayList<Line2D>();
			for(int z = i+1; z < shapes.rows();z++) {
				
				vec = shapes.get(z, 0);
				double x3 = vec[0];
				double y3 = vec[1];
				double x4 = vec[2];
				double y4 = vec[3];
				Line2D.Double line2 = new Line2D.Double(x3, y3, x4, y4);
				double angle = angleBetween2Lines(line1, line2);
				if(Math.toDegrees(angle) < 95 && Math.toDegrees(angle) > 85 ) {
					lines.add(line2);
					if(lines.size() > 1) {
						
					}
					//if(Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
						//Imgproc.line(imageMat, new Point(vec[0],vec[1]), new Point(vec[2],vec[3]), scalarColorB,2);
					//}
					
				    double denom = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
				    Point intersect = new Point(((x1*y2 - y1*x2)*(x3 - x4) -
					                       (x1 - x2)*(x3*y4 - y3*x4)) / denom,
					                      ((x1*y2 - y1*x2)*(y3 - y4) -
					                       (y1 - y2)*(x3*y4 - y3*x4)) / denom); 
				    points.add(intersect);
				    Imgproc.circle(imageMat, intersect, 2, scalarColorB);
				/*	double a1 = (y2-y1)/(x2-x1);
					double b1 = y1 - a1*x1 ;
					double a2 = (y4-y3)/(x4-x3);
					double b2 = y3 - a2*x3;
					
					if(x1 == x2 || x3==x4 ){
						if(x1 != x3){
							//paralle linjer skal ignores
						}
						else if(x1 == x3){
							if(y1 == y2 || y3 == y4) {
								
							} else if ()
						
						}
					}*/
				}
			}
		}
		
		System.out.println(shapes.rows());
		System.out.println(points.size());
		Imgcodecs.imwrite("materials\\08.png",grayImg);
		Imgcodecs.imwrite("materials\\07.png",imageMat);
	}
	public static double angleBetween2Lines(Line2D line1, Line2D line2)
	{
	    double angle1 = Math.atan2(line1.getY1() - line1.getY2(),
	                               line1.getX1() - line1.getX2());
	    double angle2 = Math.atan2(line2.getY1() - line2.getY2(),
	                               line2.getX1() - line2.getX2());
	    return angle1-angle2;
	}
	
}


