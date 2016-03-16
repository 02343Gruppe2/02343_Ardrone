package core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import gui.SpaceXGUI;

public class PicAnal {
	private static BufferedImage img;
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
		Mat imageMat = Imgcodecs.imread("materials\\hulaHop.jpeg");
		Mat circles = new Mat();
		Mat grayImg = new Mat(imageMat.rows(),imageMat.cols(),imageMat.type());
		double[] vCircle = new double[3];
        int radius;
        Point pt = new Point();

		Scalar scalarColorB = new Scalar(0,0,0); // black scalar
		Scalar scalarColorT = new Scalar(0,255,0); //T scalar, initial color = green
        
		Imgproc.cvtColor(imageMat, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(grayImg, grayImg, new Size(3,3),0,0);
        Imgproc.Canny(grayImg, circles, 5, 60);
        Imgproc.HoughCircles(grayImg, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 15, 10, 20, 100, 200);
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
	        Imgproc.circle(imageMat, pt, 1, scalarColorT, 0);
	        
        }

		Imgcodecs.imwrite("materials\\test1.png",imageMat);
	}
}
