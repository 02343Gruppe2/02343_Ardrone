package core;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import gui.SpaceXGUI;

public class PicAnal {
	private  BufferedImage imgIn;
	private  BufferedImage imgOut = null;
	
	public static void main(String[] args) {
		PicAnal obj = new PicAnal();
		obj.findRecs(false);
	}
	
	public PicAnal() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	}
	
	public void savePicture(String fileName, Boolean isFront) {
		Mat originalMat;
		imgIn = SpaceXGUI.getInstance().getVPanel().getImg(isFront);
		byte[] pixels = ((DataBufferByte) imgIn.getRaster().getDataBuffer()).getData();
		
		//create Mat from byte[]
		originalMat = new Mat(imgIn.getHeight(),imgIn.getWidth(),CvType.CV_8UC3);
		originalMat.put(0, 0, pixels); 
		Imgcodecs.imwrite("materials\\"+fileName+".png",originalMat);
	}

	public void picRunDown(int method) {
		Mat originalMat;
		Mat grayImg = new Mat();
		//image from drone cam
		imgIn = SpaceXGUI.getInstance().getVPanel().getImg(true);
		byte[] pixels = ((DataBufferByte) imgIn.getRaster().getDataBuffer()).getData();
		
		//create Mat from byte[]
		originalMat = new Mat(imgIn.getHeight(),imgIn.getWidth(),CvType.CV_8UC3);
		originalMat.put(0, 0, pixels);
		Imgproc.cvtColor(grayImg, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(originalMat, grayImg, new Size(3,3),0,0);
		Imgproc.Canny(grayImg, grayImg, 10, 50);
		Imgproc.dilate(grayImg, grayImg, new Mat());
		
		switch (method) {
		case 0: 
			findPosibleQr(grayImg);
			break;
		case 1: 
			findHulahops(grayImg,findPosibleQr(grayImg));
			break;
		default:
			
			break;
		}
	}
	
	public List<String> checkForQrText(List<Rect> rects, Mat originalMat) {
		List<String> qrCodes = new ArrayList<String>();
		Boolean qrExist;
		for(int i = 0;i<rects.size();i++) {
			Mat possibleQrMat = originalMat.submat(rects.get(i));
			
			BufferedImage possibleQrBufImg = new BufferedImage(possibleQrMat.width(),possibleQrMat.height(),BufferedImage.TYPE_3BYTE_BGR);
			byte[] data = new byte[possibleQrMat.cols()*possibleQrMat.rows()*(int)possibleQrMat.elemSize()];
			possibleQrMat.get(0, 0,data);
			possibleQrBufImg.getRaster().setDataElements(0, 0, possibleQrMat.cols(),possibleQrMat.rows(), data);
			//image.getRaster().setDataElements(0, 0, qr.get(0, 0));
			String qrText = lookForQr(possibleQrBufImg);
			
			if(!qrText.isEmpty()){
				qrExist =false;
				for(int z =0; z < qrCodes.size();z++){
					
					if(qrText.equals(qrCodes.get(z))){
						qrExist = true;	
					}		
				}
				if (qrExist == false){
					qrCodes.add(qrText);
					System.out.println(qrText);
				}
			}
		}
		return qrCodes;
	}
	
	public  ArrayList<double[]> findHulahops(Mat grayImg, List<Rect> rects) {
		double[] vCircle = new double[3];
        int radius;
        Point pt = new Point();
        Mat circles = new Mat();
		ArrayList<double[]> hulahops = new ArrayList<double[]>();
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
	      //checking if there is a rect below the circle, if there 
	        int distanceBetweenRectAndCircle = 30;
        	for(int c = 0;c<rects.size();c++) {
        		if((pt.y+radius) < rects.get(c).tl().y && rects.get(c).tl().y < (pt.y+radius+distanceBetweenRectAndCircle) && rects.get(c).br().x > pt.x &&  rects.get(c).tl().x < pt.x) {
        			
        			hulahops.add(vCircle);
    		        // draw the found circle
        		}
        	}
        }
		return hulahops;
	}
	
	public List<Rect> findPosibleQr(Mat grayImg) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		int mode = Imgproc.RETR_LIST;
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(grayImg, contours, hierarchy, mode, method);
		List<Rect> rects = new ArrayList<Rect>();
		for(int i = 0;i <contours.size();i++) {
			MatOfPoint2f outArray = new  MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), outArray, 5, true);
			MatOfPoint mop = new MatOfPoint(outArray.toArray());
			Rect rect =  Imgproc.boundingRect(mop);
			//checks distance between topleft and bottomright
			//QR codes are on A3, which height is sqrt(2) times bigger than width - 1.4, which is why we check for 1.25 and 1.55
			if(Point2D.distance(rect.tl().x, rect.tl().y, rect.br().x, rect.br().y) > 50 && rect.height < (rect.width*1.55) && rect.height > (rect.width*1.25)) {
				rects.add(rect);
			} 
		}
		return rects;
	}
	
	public Object[] findRecs(Boolean isFromDrone) {
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		ArrayList<String> qrCodes = new ArrayList<String>(); 
		boolean qrExist = false;
		
		
		String saveFile = "materials\\qrt4out.png";
		Mat originalMat;/*
		if(isFromDrone) {
			//image from drone cam
			imgIn = SpaceXGUI.getInstance().getVPanel().getImg(true);
			byte[] pixels = ((DataBufferByte) imgIn.getRaster().getDataBuffer()).getData();
			
			//create Mat from byte[]
			originalMat = new Mat(imgIn.getHeight(),imgIn.getWidth(),CvType.CV_8UC3);
			originalMat.put(0, 0, pixels);
		} else {
			String filename = "materials\\qrt4.png";
			originalMat = Imgcodecs.imread(filename);
		}
		*/
		
		for(int k = 0; k<160; k++){
			try {
			String filename = "materials\\picture_"+k+".png";
			originalMat = Imgcodecs.imread(filename);
		
		 
		Mat drawingMat = originalMat.clone();
		//create grayscale, blur, canny and dilate
		Mat grayImg = new Mat();

		Imgproc.cvtColor(grayImg, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(originalMat, grayImg, new Size(3,3),0,0);
		Imgproc.Canny(grayImg, grayImg, 10, 50);
		Imgproc.dilate(grayImg, grayImg, new Mat());
		
		//find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		int mode = Imgproc.RETR_LIST;
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgcodecs.imwrite("materials\\09.png",grayImg);
		Imgproc.findContours(grayImg, contours, hierarchy, mode, method);
		Scalar scalarColorGreen = new Scalar(0,255,0); // green scalar
		List<Rect> rects = new ArrayList<Rect>();
		//System.out.println(contours.size());
		for(int i = 0;i <contours.size();i++) {
			MatOfPoint2f outArray = new  MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), outArray, 5, true);
			MatOfPoint mop = new MatOfPoint(outArray.toArray());
			Rect rect =  Imgproc.boundingRect(mop);
			
			//checks distance between topleft and bottomright
			if(Point2D.distance(rect.tl().x, rect.tl().y, rect.br().x, rect.br().y) > 50 && rect.height < (rect.width*1.55) && rect.height > (rect.width*1.25)) {
				rects.add(rect);
				//drawing found rects
				Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), scalarColorGreen, 2);
				Mat possibleQrMat = originalMat.submat(rect);
				
				BufferedImage possibleQrBufImg = new BufferedImage(possibleQrMat.width(),possibleQrMat.height(),BufferedImage.TYPE_3BYTE_BGR);
				byte[] data = new byte[possibleQrMat.cols()*possibleQrMat.rows()*(int)possibleQrMat.elemSize()];
				possibleQrMat.get(0, 0,data);
				possibleQrBufImg.getRaster().setDataElements(0, 0, possibleQrMat.cols(),possibleQrMat.rows(), data);
				//image.getRaster().setDataElements(0, 0, qr.get(0, 0));
				String qrText = lookForQr(possibleQrBufImg);
				
				if(!qrText.isEmpty()){
					qrExist =false;
					for(int z =0; z < qrCodes.size();z++){
						
						if(qrText.equals(qrCodes.get(z))){
							qrExist = true;	
						}		
					}
					if (qrExist == false){
						qrCodes.add(qrText);
						System.out.println(qrText);
					}
				}
			} 
		}
		
		/*******************************Circles start*********************************************/
		double[] vCircle = new double[3];
        int radius;
        Point pt = new Point();
        Mat circles = new Mat();
		Scalar scalarColorBlue = new Scalar(255,0,0); //T scalar, initial color = red
		Scalar scalarColorRed = new Scalar(0,0,255); //T scalar, initial color = black
		ArrayList<double[]> hulahops = new ArrayList<double[]>();
		
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
	      //checking if there is a rect below the circle, if there 
	        int distanceBetweenRectAndCircle = 30;
        	for(int c = 0;c<rects.size();c++) {
        		if((pt.y+radius) < rects.get(c).tl().y && rects.get(c).tl().y < (pt.y+radius+distanceBetweenRectAndCircle) && rects.get(c).br().x > pt.x &&  rects.get(c).tl().x < pt.x) {
        			
        			hulahops.add(vCircle);
    		        // draw the found circle
    		        Imgproc.circle(drawingMat, pt, radius, scalarColorBlue, 2);
    		        Imgproc.circle(drawingMat, pt, 1, scalarColorRed, 2);
        		}
        	}
        }
		
        /*********************************Circles end******************************************/
		
		
		
		
		
		
		imgOut = new BufferedImage(drawingMat.width(),drawingMat.height(),BufferedImage.TYPE_3BYTE_BGR);
		byte[] data = new byte[drawingMat.cols()*drawingMat.rows()*(int)drawingMat.elemSize()];
		drawingMat.get(0, 0,data);
		imgOut.getRaster().setDataElements(0, 0, drawingMat.cols(),drawingMat.rows(), data);
		Imgcodecs.imwrite("materials\\fixedpicture_"+k+".png",drawingMat);
		//saving images
		/*Imgcodecs.imwrite("materials\\08.png",grayImg);
		Imgcodecs.imwrite(saveFile,drawingMat);*/
			} catch (Exception ex) {
				
			}
		}
		
		
		//Object[] result = { qrCodes, hulahops };
		Object[] result = null;
		return result;
	}
	
	public String lookForQr(BufferedImage image) {
		String res = "";
		
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		// decode the barcode (if only QR codes are used, the QRCodeReader might be a better choice)
		MultiFormatReader reader = new MultiFormatReader();
		try {
			//System.out.println("H: "+ bitmap.getHeight()+ " W: "+ bitmap.getWidth());
			Result scanResult = reader.decode(bitmap);
			res =scanResult.getText();
			//System.out.println(res);
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception ex) {
		}
		return res;
	}
}


