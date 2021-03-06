package core;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Date;
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
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.QRCodeReader;

import gui.SpaceXGUI;


public class ImgProc {
	private  BufferedImage imgIn;
	private BufferedImage imgOut;
	private Scalar greenScalar = new Scalar(0,200,0); //found a circle and QR code text
	private Scalar redScalar = new Scalar(200,0,0); // found a square
	private Scalar blueScalar = new Scalar(0,0,200); //square QR scan area
	private int minDiagonalLength = 140;
	private int maxRectHeight = 600;

	public static void main(String[] args) {
		ImgProc obj = new ImgProc();
		obj.findHulaHoops();

	}

	public ImgProc() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	}

	public void savePicture(String fileName, Boolean isFront) {
		imgIn = SpaceXGUI.getInstance().getVPanel().getImg(isFront);
		Mat originalMat = convertBufferedImageToMat(imgIn);
		Imgcodecs.imwrite("materials\\"+fileName+".png",originalMat);
	}

	public Object[] findQRCodes() {
		return analysePicture(-1,true);
	}

	public Object[] findHulaHoops() {
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
	/*	for(int index = 0;index<57;index++) {
			try {
				imgIn = convertMatToBufferedImage(Imgcodecs.imread("materials\\picture_"+index+".png"));
				res = picRunDown(assignment,isFront,imgIn);
				Imgcodecs.imwrite("materials\\front\\"+index+".png",convertBufferedImageToMat(imgOut));

			} catch (Exception ex) {
				System.out.println(index);
			}
		}*/


		//picture from drone
		imgIn = SpaceXGUI.getInstance().getVPanel().getImg(isFront);
		res = picRunDown(assignment,isFront,imgIn);

		return res;
	}

	private Object[] picRunDown(int assignment, boolean isFront, BufferedImage test) {
		//long time = new Date().getTime();
		Object[] res = new Object[3];
		Mat originalMat;
		Mat drawingMat;
		Mat grayImg = new Mat();
		//image from drone cam
		originalMat = convertBufferedImageToMat(test);
		drawingMat = originalMat.clone();
		Imgproc.cvtColor(originalMat, grayImg, Imgproc.COLOR_BGRA2GRAY); 
		Imgproc.GaussianBlur(grayImg, grayImg, new Size(5,5),2,2);
		Imgproc.Canny(grayImg, grayImg, 25, 50);
		List<Rect> rects;
		switch (assignment) {
		case 0:
			//hulahoops assignment

			rects = checkPossibleQrRectangle(grayImg, drawingMat);
			Object[] temp  = checkRectsForQrText(rects, originalMat, drawingMat);
			res[0] = temp;
			res[1] = checkImageForHulaHoops(grayImg, (ArrayList<Rect>)temp[1], originalMat, drawingMat);
			res[2] = rects;

			break;
		case 1: 
			//cube finding assignment
			rects = checkPossibleQrRectangle(grayImg, drawingMat);
			checkForCubes(originalMat, drawingMat, rects);
			break;
		case 2:
			//air fields assignment
			rects = checkPossibleQrRectangle(grayImg, drawingMat);
			List<String> qrTexts =  (List<String>)checkRectsForQrText(rects, originalMat, drawingMat)[0];
			if(qrTexts.size() > 0) {
				res[0] = qrTexts;
				break;
			}
			res[1] = checkForPosibleAirfield(originalMat, drawingMat, grayImg, rects);
			break;
		default:
			rects = checkPossibleQrRectangle(grayImg, drawingMat);
			res[0] = checkRectsForQrText(rects, originalMat, drawingMat);
			res[1] = rects;
			break;
		}

		imgOut = convertMatToBufferedImage(drawingMat);
		SpaceXGUI.getInstance().getVPanel().setImg(imgOut, isFront);
		//SpaceXGUI.getInstance().getVPanel().setImg(convertMatToBufferedImage1(grayImg), false);
		//SpaceXGUI.getInstance().appendToConsole("Picture rundown","time: " + (new Date().getTime() - time)  + " ms");
		return res;
	}

	private Object[] checkRectsForQrText(List<Rect> rects, Mat originalMat, Mat drawingMat) {
		List<String> qrCodes = new ArrayList<String>();
		List<Rect> outRects = new ArrayList<Rect>();
		Boolean qrExist;
		for(int i = 0;i<rects.size();i++) {
			//creating a submat of the mat to check for qr codes
			Mat possibleQrMat = originalMat.submat(rects.get(i));
			BufferedImage possibleQrBufImg = convertMatToBufferedImage(possibleQrMat);

			//Boolean tells if we use try harder hint
			String qrText = lookForQr(possibleQrBufImg, true);

			if(qrText != null){
				qrExist =false;
				Imgproc.putText(drawingMat, qrText, rects.get(i).tl(), Core.FONT_HERSHEY_PLAIN, 1, greenScalar,3);
				for(int z =0; z < qrCodes.size();z++){
					if(qrText.equals(qrCodes.get(z))){
						qrExist = true;	
					}		
				}
				if (qrExist == false){
					qrCodes.add(qrText);
					outRects.add(rects.get(i));
				}
			}
		}
		return new Object[] {qrCodes, outRects};
	}

	private  ArrayList<String[]> checkImageForHulaHoops(Mat grayImg, List<Rect> rects, Mat originalMat, Mat drawingMat) {
		double x,y, radius;
		ArrayList<String[]> hulahops = new ArrayList<String[]>();
		for(int i = 0 ; i< rects.size(); i++) {
			//creating a list with the rect to check for QR
			List<Rect> hulahopRect = new ArrayList<Rect>();
			hulahopRect.add(rects.get(i));
			List<String> qrTextList = (List<String>)checkRectsForQrText(hulahopRect, originalMat, drawingMat)[0];
			//see if we got a qr code
			String qrText = "";
			if(!qrTextList.isEmpty()) {
				qrText = qrTextList.get(0);
			}
			//see if qr code is hulahoop
			if(qrText.contains("P")) {
				//calculate x, y and radius esitmation of the hulahoop based on the rectangle with QR
				x = (rects.get(i).br().x - rects.get(i).tl().x)/2 + rects.get(i).tl().x;
				radius = (rects.get(i).width*1.3);
				y = rects.get(i).tl().y - (rects.get(i).width*1.4)*1.15; //width*1.4 for height, * 1.2 for hulahop center
				//draw center of cirle
				Imgproc.circle(drawingMat, new Point(x,y), 1, greenScalar, 6);
				//draw circle
				Imgproc.circle(drawingMat, new Point(x,y), (int)Math.round(radius), greenScalar,3);
				//change coordinates so 0,0 is in the center of the image instead of top left corner
				double[] displaced = coordinateDisplacement(x,y);				
				hulahops.add(new String[] {""+displaced[0],""+displaced[1], ""+radius, qrText});
			}
		}
		return hulahops;
	}

	public static double[] coordinateDisplacement(double x, double y) {
		int picWidth = 640; //half of real size
		int picHeight = 360; //half of real size
		if (x> picWidth && y< picHeight){
			//1. qaudrant
			x = x - picWidth;
			y = -y + picHeight;
		} else if(x > picWidth && y > picHeight) {
			//2. qaudrant
			x = x - picWidth;
			y = -y + picHeight;
		} else if (x < picWidth && y > picHeight) {
			//3. qaudrant
			x = x - picWidth;
			y = -y + picHeight;
		} else if(x < picWidth && y < picHeight) {
			//4. qaudrant
			x = x - picWidth;
			y = -y + picHeight;


		}
		return new double[]{x,y};
	}

	private List<Rect> checkPossibleQrRectangle(Mat grayImg, Mat drawingMat) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		int mode = Imgproc.RETR_LIST;
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(grayImg, contours, hierarchy, mode, method);
		List<Rect> rects = new ArrayList<Rect>();
		double factor = 1.5;
		MatOfPoint2f outArray;
		MatOfPoint mop;
		Rect rect;
		//check each contour for a rectangle
		for(int i = 0;i <contours.size();i++) {
			outArray = new  MatOfPoint2f();
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), outArray, 10, true);
			mop = new MatOfPoint(outArray.toArray());
			rect =  Imgproc.boundingRect(mop);
			
			//checks distance between topleft and bottomright
			if(rect.height < maxRectHeight && Point2D.distance(rect.tl().x, rect.tl().y, rect.br().x, rect.br().y) > minDiagonalLength) {
				//check that the width and height are within the factor of each other (rectangle where the 
				if(rect.width*factor > rect.height && rect.height*factor > rect.width) {
					rects.add(rect);
					Imgproc.rectangle(drawingMat, rect.tl(), rect.br(), redScalar,3);
				} 
			}
		}
		return rects;
	}

	private Object[] checkForPosibleAirfield(Mat originalMat, Mat drawingMat, Mat grayImg, List<Rect> rects) {
		Object[]  res = null;
		Mat circles = new Mat();
		int dp = 2, minDist = 150, minRadius = 35, maxRadius = 100, param1 = 50, param2 = 100;
		double[] circlePoints = new double[3];
		Point center = new Point();;
		int radius;

		for(int z = 0;z<rects.size();z ++) {
			Imgproc.HoughCircles(grayImg.submat(rects.get(z)), circles, Imgproc.CV_HOUGH_GRADIENT, dp
					, minDist, param1, param2, minRadius, maxRadius);
			if(circles.cols() > 0) {
				int rectCount = 0;
				for(int c = 0; c < rects.size();c++) {
					if(rects.get(z).tl().x < rects.get(c).tl().x && rects.get(z).tl().y < rects.get(c).tl().y && 
							rects.get(z).br().x > rects.get(c).br().x && rects.get(z).br().y > rects.get(c).br().y) {
						rectCount ++;
					}
				}
				if(rectCount > 5) {

					//used for testing
					if(circles.cols()> 1)

						Imgproc.rectangle(drawingMat, rects.get(z).tl(), rects.get(z).br(), redScalar,2);
					circlePoints = circles.get(0, 0);
					radius = (int)Math.round(circlePoints[2]);
					circlePoints[0] += rects.get(z).tl().x;
					circlePoints[1] += rects.get(z).tl().y;
					center.set(circlePoints);

					Imgproc.circle(drawingMat, center, radius, greenScalar,2);
					//return 1

				} else {
					//return 0
				}
			}
		}
		return res;
	}

	private Object[] checkForCubes(Mat orignalMat, Mat drawingMat, List<Rect> rects) {
		Object[] res = null;
		for(int z = 0; z < rects.size(); z ++) {
			if(rects.get(z).height > rects.get(z).width-5 && rects.get(z).height < rects.get(z).width+5) {
				Mat posibleCube = orignalMat.submat(rects.get(z));
				int redCount = 0;
				int greenCount = 0;
				int totalCount = posibleCube.cols()/5 + posibleCube.rows() / 5;
				for(int x = 0 ; x < posibleCube.cols();x+=5) {
					for (int c = 0; c < posibleCube.rows();c+=5) {
						double[] point = posibleCube.get(c, x);
						// point[] 2 = red, 1 = green, 1 = blue BGR
						if(point[2] > 100 && point[1] < 10 && point[0] <50)
							redCount ++;
						if(point[2] < 50 && point[1] > 50 && point[0] < 50) {
							greenCount ++;
						}

					}
				}
				String text = "is none";
				if(totalCount*0.69 < redCount) {
					text = "is red";
				} else if (totalCount*0.69 < greenCount) {
					text = "is green";
				} 
				Imgproc.putText(drawingMat, text, rects.get(z).tl(), Core.FONT_HERSHEY_PLAIN, 1, greenScalar,2);
			}
		}

		return res;
	}

	private String lookForQr(BufferedImage image, Boolean useHint) {
		String res = null;
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		// decode the barcode (if only QR codes are used, the QRCodeReader might be a better choice)
		//MultiFormatReader reader = new MultiFormatReader();
		QRCodeMultiReader reader = new QRCodeMultiReader();	
		try {
			Result scanResult;
			if(useHint) {
				Map<DecodeHintType,Object> tmpHintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
				//  tmpHintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
				tmpHintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
				scanResult = reader.decode(bitmap,tmpHintsMap);
			} else {
				scanResult = reader.decode(bitmap);
			}

			res = scanResult.getText();

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

	private Mat convertBufferedImageToMat(BufferedImage bufferedImg) {
		byte[] pixels = ((DataBufferByte) bufferedImg.getRaster().getDataBuffer()).getData();
		//create Mat from byte[]
		Mat mat = new Mat(bufferedImg.getHeight(),bufferedImg.getWidth(),CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2BGR);
		return mat;
	}
}


