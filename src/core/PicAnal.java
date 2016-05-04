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
	private Scalar greenScalar = new Scalar(0,200,0);
	private Scalar redScalar = new Scalar(200,0,0);
	private Scalar blueScalar = new Scalar(0,0,200);
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

	public Object[] analysePicture(int assignment, boolean isFront) {
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
	
	public Object[] picRunDown(int assignment, boolean isFront, BufferedImage test) {
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
		//Imgproc.dilate(grayImg, grayImg, new Mat());
		
		switch (assignment) {
		case 0:
			//hulahops assignment
			if(isFront) {
				List<Rect> rects = findPosibleQr(grayImg, drawingMat);
				res[0] = checkForQrText(rects, originalMat, drawingMat);
				res[1] = findHulahops(grayImg, rects, originalMat, drawingMat);
				res[2] = rects;
			} else {
				
			}
			break;
		case 1: 
			//cube finding assignment
			
			break;
		case 2:
			//air fields assignment
			break;
		default:
			
			break;
		}
		imgOut = convertMatToBufferedImage(drawingMat);
		return res;
	}
	
	public List<String> checkForQrText(List<Rect> rects, Mat originalMat, Mat drawingMat) {
		List<String> qrCodes = new ArrayList<String>();
		Boolean qrExist;
		for(int i = 0;i<rects.size();i++) {
			Rect rect = new Rect(new Point(rects.get(i).tl().x-10,rects.get(i).tl().y-10), new Point(rects.get(i).br().x+10,rects.get(i).br().y+10));
			//Rect with 10 more px surrounding
			Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), blueScalar);
			Mat possibleQrMat = originalMat.submat(rect);
			BufferedImage possibleQrBufImg = convertMatToBufferedImage(possibleQrMat);
			String qrText = lookForQr(possibleQrBufImg);
			
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
	
	public  ArrayList<Object[]> findHulahops(Mat grayImg, List<Rect> rects, Mat originalMat, Mat drawingMat) {
		double[] vCircle = new double[3];
        int radius;
        Point pt = new Point();
        Mat circles = new Mat();
		ArrayList<Object[]> hulahops = new ArrayList<Object[]>();
		int dp = 2, minDist = 75, minRadius = 70, maxRadius = 270, param1 = 100, param2 = 100;
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
	        //Imgproc.circle(drawingMat, pt, radius, blueScalar,2);
	      //checking if there is a rect below the circle, if there 
	        int distanceBetweenRectAndCircle = 35;
        	for(int c = 0;c<rects.size();c++) {
        		if((pt.y+radius) < rects.get(c).tl().y && rects.get(c).tl().y < (pt.y+radius+distanceBetweenRectAndCircle) && rects.get(c).br().x > pt.x &&  rects.get(c).tl().x < pt.x) {
        			//In our picture we have 0,0 in the top left corner, but we want the 0,0 to be the center of the picture 
        			//which is why we do the following calculations depending on where it is
        			Imgproc.circle(drawingMat, pt, radius, greenScalar,2);
        			
        			 if (vCircle[0] > 320 && vCircle[1] < 240){
        				//1. qaudrant
        				vCircle[0] = vCircle[0] - 320;
        				vCircle[1] = -vCircle[1] + 240;
        			}else if(vCircle[0] > 320 && vCircle[1] > 240) {
        				//2. qaudrant
        				vCircle[0] = vCircle[0] - 320;
        				vCircle[1] = -vCircle[1] + 240;
        			} else if (vCircle[0] < 320 && vCircle[1] > 240) {
        				//3. qaudrant
        				vCircle[0] = vCircle[0] - 320;
        				vCircle[1] = -vCircle[1] + 240;
        			} else if(vCircle[0] < 320 && vCircle[1] < 240) {
        				//4. qaudrant
        				vCircle[0] = vCircle[0] - 320;
        				vCircle[1] = -vCircle[1] + 240;
        			}
        			List<Rect> hulahopRect = new ArrayList<Rect>();
        			hulahopRect.add(rects.get(c));
        			List<String> qrTextList = checkForQrText(hulahopRect, originalMat, drawingMat);
        			String qrText = null;
        			if(!qrTextList.isEmpty()) {
        				qrText = qrTextList.get(0);
        			}
        			hulahops.add(new Object[] {vCircle[0],vCircle[1], vCircle[2], qrText});
    		        // draw the found circle
        		}
        	}
        }
		return hulahops;
	}
	
	public List<Rect> findPosibleQr(Mat grayImg, Mat drawingMat) {
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
				Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), redScalar,2);
			} 
		}
		return rects;
	}
	
	public String lookForQr(BufferedImage image) {
		String res = null;
		
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
	
	public BufferedImage convertMatToBufferedImage(Mat mat) {
		BufferedImage bufferedImg = new BufferedImage(mat.width(),mat.height(),BufferedImage.TYPE_3BYTE_BGR);
		byte[] data = new byte[mat.cols()*mat.rows()*(int)mat.elemSize()];
		mat.get(0, 0,data);
		bufferedImg.getRaster().setDataElements(0, 0, mat.cols(),mat.rows(), data);
		return bufferedImg;
	}
	
	public Mat convertBufferedImageToMa(BufferedImage bufferedImg) {
		byte[] pixels = ((DataBufferByte) bufferedImg.getRaster().getDataBuffer()).getData();
		//create Mat from byte[]
		Mat mat = new Mat(bufferedImg.getHeight(),bufferedImg.getWidth(),CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		return mat;
	}
}


