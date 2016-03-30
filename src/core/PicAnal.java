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
	public static BufferedImage horizontalImg;
	public static BufferedImage verticalImg;
	public static  void analyse(boolean isHorizontal) {

		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		if(isHorizontal) {
			horizontalImg = analyseByteArr("horizontal.png");
		} else {
			verticalImg = analyseByteArr("vertical.png");
		}
	}
	
	private static BufferedImage analyseByteArr(String fileName) {
		//get buffedimage from gui and convert to byte[]
		BufferedImage img = SpaceXGUI.getInstance().getVPanel().getImg();
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		//create Mat from byte[]
		Mat imageMat = new Mat(img.getHeight(),img.getWidth(),CvType.CV_8UC3);
		imageMat.put(0, 0, pixels);
		
		
		//Mat imageMat = Imgcodecs.imread("materials\\hulaHop.jpeg");
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
        img = new BufferedImage(imageMat.width(),imageMat.height(),BufferedImage.TYPE_3BYTE_BGR);
        int bufferSize = imageMat.channels()*imageMat.cols()*imageMat.rows();
        byte [] b = new byte[bufferSize];
        imageMat.get(0,0,b); // get all the pixels
        final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
		Imgcodecs.imwrite("materials\\"+fileName,imageMat);
		return img;
	}
}