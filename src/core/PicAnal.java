package core;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import gui.SpaceXGUI;

public class PicAnal {
	private  BufferedImage imgIn;
	private Scalar greenScalar = new Scalar(0,200,0); //found a circle and QR code text
	private Scalar redScalar = new Scalar(200,0,0); // found a square
	private Scalar blueScalar = new Scalar(0,0,200); //square QR scan area
	public static void main(String[] args) {
		PicAnal obj = new PicAnal();
		obj.analysePicture(0,true);
	}
	
	public PicAnal() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	}
	
	public void savePicture(String fileName, Boolean isFront) {
		imgIn = SpaceXGUI.getInstance().getVPanel().getImg(isFront);
		Mat originalMat = convertBufferedImageToMa(imgIn);
		Imgcodecs.imwrite("materials\\"+fileName+".png",originalMat);
	}

	public Object[] findQRCodes() {
		return analysePicture(-1,true);
	}
	
	public Object[] findHulahops() {
		return analysePicture(0, true);
	}
	
	public Object[] findAirfield() {
		return analysePicture(2, false);
	}
	
	public Object[] findCube() {
		return analysePicture(1, false);
	}
	
	private Object[] analysePicture(int assignment, boolean isFront) {
		Object[] res = null;
		
		//picture from file
		/*for(int index = 0;index<400;index++) {
			try {
				
				imgIn = convertMatToBufferedImage(Imgcodecs.imread("materials\\front\\picture_"+index+".png"));
				res = picRunDown(assignment,isFront,imgIn);
				Imgcodecs.imwrite("materials\\front\\tested\\"+index+".png",convertBufferedImageToMa(imgOut));
				
			} catch (Exception ex) {
			}
		}*/
		
		
		//picture from drone
		imgIn = SpaceXGUI.getInstance().getVPanel().getImg(isFront);
		res = picRunDown(assignment,isFront,imgIn);
		
		return res;
	}
	
	private Object[] picRunDown(int assignment, boolean isFront, BufferedImage test) {
		Object[] res = new Object[3];
		Mat originalMat;
		Mat drawingMat;
		Mat grayImg = new Mat();
		//image from drone cam
		originalMat = convertBufferedImageToMa(test);
		drawingMat = originalMat.clone();
		Imgproc.cvtColor(originalMat, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(grayImg, grayImg, new Size(3,3),0,0);
		Imgproc.Canny(grayImg, grayImg, 10, 50);
		Imgproc.dilate(grayImg, grayImg, new Mat());
		//Imgproc.erode(grayImg, grayImg, new Mat());
		
		switch (assignment) {
		case 0:
			//hulahops assignment
			if(isFront) {
				List<Rect> rects = findPosibleQrPortrait(grayImg, drawingMat);
				res[0] = checkRectsForQrText(rects, originalMat, drawingMat);
				res[1] = checkImageForHulahops(grayImg, rects, originalMat, drawingMat);
				res[2] = rects;
			} else {
				
			}
			break;
		case 1: 
			//cube finding assignment
			
			break;
		case 2:
			//air fields assignment
			List<Rect> rects = findPosibleQrBoth(grayImg, drawingMat);
			res[0] = checkRectsForQrText(rects, originalMat, drawingMat);
			break;
		default:
			List<Rect> rectss = findPosibleQrPortrait(grayImg, drawingMat);
			res[0] = checkRectsForQrText(rectss, originalMat, drawingMat);
			res[1] = rectss;
			break;
		}
		SpaceXGUI.getInstance().getVPanel().setImg(convertMatToBufferedImage(drawingMat), isFront);
		return res;
	}
	
	private List<String> checkRectsForQrText(List<Rect> rects, Mat originalMat, Mat drawingMat) {
		List<String> qrCodes = new ArrayList<String>();
		Boolean qrExist;
		for(int i = 0;i<rects.size();i++) {
			double tlx = (rects.get(i).tl().x-10 < 0) ? rects.get(i).tl().x : rects.get(i).tl().x-10;
			double tly = (rects.get(i).tl().y-10 < 0) ? rects.get(i).tl().y : rects.get(i).tl().y-10;
			double brx = (rects.get(i).br().x+10 > 640) ? rects.get(i).br().x : rects.get(i).br().x+10;
			double bry = (rects.get(i).br().y+10 > 360) ? rects.get(i).br().y : rects.get(i).br().y+10;
			Rect rect = new Rect(new Point(tlx,tly), new Point(brx,bry));
			//Rect with 10 more px surrounding
			Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), blueScalar);
			Mat possibleQrMat = originalMat.submat(rect);
			BufferedImage possibleQrBufImg = convertMatToBufferedImage(possibleQrMat);
			
			//Boolean tells if we use try harder hint
			String qrText = lookForQr(possibleQrBufImg, true);
			
			if(qrText != null){
				qrExist =false;
				Imgproc.putText(drawingMat, qrText, rects.get(i).tl(), Core.FONT_HERSHEY_PLAIN, 1, greenScalar,2);
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
	
	private  ArrayList<Object[]> checkImageForHulahops(Mat grayImg, List<Rect> rects, Mat originalMat, Mat drawingMat) {
		double[] circlePoints = new double[3];
        int radius;
        Point center = new Point();
        Mat circles = findCircles(grayImg);
		ArrayList<Object[]> hulahops = new ArrayList<Object[]>();
        for (int x = 0; x < circles.cols(); x++) {
        	circlePoints = circles.get(0,x);
        	if (circlePoints == null) {
        		//TODO return so that we know that there are no more balls
        		System.err.println("no circles found");
	            break;
	        }
    		center.set(circlePoints);
	        radius = (int)Math.round(circlePoints[2]);
	        //Imgproc.circle(drawingMat, pt, radius, blueScalar,2);
	      //checking if there is a rect below the circle, if there 
	        int distanceBetweenRectAndCircle = 35;
        	for(int c = 0;c<rects.size();c++) {
        		if((center.y+radius) < rects.get(c).tl().y && rects.get(c).tl().y < (center.y+radius+distanceBetweenRectAndCircle) && rects.get(c).br().x > center.x &&  rects.get(c).tl().x < center.x) {
        			//In our picture we have 0,0 in the top left corner, but we want the 0,0 to be the center of the picture 
        			//which is why we do the following calculations depending on where it is
        			Imgproc.circle(drawingMat, center, radius, greenScalar,2);
        			
        			 if (circlePoints[0] > 320 && circlePoints[1] < 240){
        				//1. qaudrant
        				circlePoints[0] = circlePoints[0] - 320;
        				circlePoints[1] = -circlePoints[1] + 240;
        			}else if(circlePoints[0] > 320 && circlePoints[1] > 240) {
        				//2. qaudrant
        				circlePoints[0] = circlePoints[0] - 320;
        				circlePoints[1] = -circlePoints[1] + 240;
        			} else if (circlePoints[0] < 320 && circlePoints[1] > 240) {
        				//3. qaudrant
        				circlePoints[0] = circlePoints[0] - 320;
        				circlePoints[1] = -circlePoints[1] + 240;
        			} else if(circlePoints[0] < 320 && circlePoints[1] < 240) {
        				//4. qaudrant
        				circlePoints[0] = circlePoints[0] - 320;
        				circlePoints[1] = -circlePoints[1] + 240;
        			}
        			List<Rect> hulahopRect = new ArrayList<Rect>();
        			hulahopRect.add(rects.get(c));
        			List<String> qrTextList = checkRectsForQrText(hulahopRect, originalMat, drawingMat);
        			String qrText = null;
        			if(!qrTextList.isEmpty()) {
        				qrText = qrTextList.get(0);
        			}
        			hulahops.add(new Object[] {circlePoints[0],circlePoints[1], circlePoints[2], qrText});
    		        // draw the found circle
        		}
        	}
        }
		return hulahops;
	}
	
	private List<Rect> findPosibleQrPortrait(Mat grayImg, Mat drawingMat) {
		List<MatOfPoint> contours = findContours(grayImg);
		List<Rect> rects = new ArrayList<Rect>();
		for(int i = 0;i <contours.size();i++) {
			Rect rect =  findRectangle(contours.get(i));
			//checks distance between topleft and bottomright
			//QR codes are on A3, which height is sqrt(2) times bigger than width - 1.4, which is why we check for 1.25 and 1.55
			if(Point2D.distance(rect.tl().x, rect.tl().y, rect.br().x, rect.br().y) > 50 && rect.height < (rect.width*1.55) && rect.height > (rect.width*1.25)) {
				rects.add(rect);
				Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), redScalar,2);
			} 
		}
		
		return rects;
	}
	
	private List<Rect> findPosibleQrBoth(Mat grayImg, Mat drawingMat) {
		List<MatOfPoint> contours = findContours(grayImg);
		List<Rect> rects = new ArrayList<Rect>();
		for(int i = 0;i <contours.size();i++) {
			Rect rect =  findRectangle(contours.get(i));
			//checks distance between topleft and bottomright
			if(Point2D.distance(rect.tl().x, rect.tl().y, rect.br().x, rect.br().y) > 50) {
				rects.add(rect);
				Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), redScalar,2);
			} 
		}
		return rects;
	}
	
	private Rect findRectangle (MatOfPoint points) {
		MatOfPoint2f outArray = new  MatOfPoint2f();
		Imgproc.approxPolyDP(new MatOfPoint2f(points.toArray()), outArray, 5, true);
		MatOfPoint mop = new MatOfPoint(outArray.toArray());
		return Imgproc.boundingRect(mop);
	}
	
	private void findAirfield(Mat grayImg) {
		Mat circles = findCircles(grayImg);
		double[] circlePoints = new double[3];
		Point center = new Point();;
		int radius;
		for(int i = 0 ; i<circles.cols();i++) {
			circlePoints = circles.get(0, i);
			radius = (int)Math.round(circlePoints[2]);
			center.set(circlePoints);
			
		}
	}
	
	private Mat findCircles(Mat image) {
		//The circles mat got circle center and radius at 0,column for each circle in a double[] with x,y,radius
		Mat res = new Mat();
		int dp = 2, minDist = 75, minRadius = 70, maxRadius = 270, param1 = 100, param2 = 100;
        Imgproc.HoughCircles(image, res, Imgproc.CV_HOUGH_GRADIENT, dp
        		, minDist, param1, param2, minRadius, maxRadius);
		return res;
	}
	
	private List<MatOfPoint> findContours(Mat image) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		int mode = Imgproc.RETR_LIST;
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(image, contours, hierarchy, mode, method);
		return contours;
	}
	
	private String lookForQr(BufferedImage image, Boolean useHint) {
		String res = null;
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		// decode the barcode (if only QR codes are used, the QRCodeReader might be a better choice)
		MultiFormatReader reader = new MultiFormatReader();
		//QRCodeReader qrreader = new QRCodeReader();
		try {
			Result scanResult;
			if(useHint) {
				Map<DecodeHintType,Object> tmpHintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
	            tmpHintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	            tmpHintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
	            scanResult = reader.decode(bitmap,tmpHintsMap);
			} else {
				scanResult = reader.decode(bitmap);
			}
			
			res = scanResult.getText();
			//System.out.println(res);
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception ex) {
		}
		return res;
	}
	
	private BufferedImage convertMatToBufferedImage(Mat mat) {
		BufferedImage bufferedImg = new BufferedImage(mat.width(),mat.height(),BufferedImage.TYPE_3BYTE_BGR);
		byte[] data = new byte[mat.cols()*mat.rows()*(int)mat.elemSize()];
		mat.get(0, 0,data);
		bufferedImg.getRaster().setDataElements(0, 0, mat.cols(),mat.rows(), data);
		return bufferedImg;
	}
	
	private Mat convertBufferedImageToMa(BufferedImage bufferedImg) {
		byte[] pixels = ((DataBufferByte) bufferedImg.getRaster().getDataBuffer()).getData();
		//create Mat from byte[]
		Mat mat = new Mat(bufferedImg.getHeight(),bufferedImg.getWidth(),CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		return mat;
	}
}


