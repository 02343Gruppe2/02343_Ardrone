package utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Calibration {

	public Calibration() {

		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	}
	
	public static void main(String[] args) {
		int xsize = 9, ysize = 6;
		
		List<Mat> imagePoint = new ArrayList<Mat>(), rvecs = new ArrayList<Mat>(), tvecs = new ArrayList<Mat>();
		for(int x =0;x<xsize;x++) {
			for(int y= 0;y<ysize;y++) {
				MatOfPoint3f points = new MatOfPoint3f(new Point3(x,y,0));
				imagePoint.add(points);
			}
		}
		
		List<Mat> cornerList = new ArrayList<Mat>();
		for( int i = 0 ; i<20; i++) {
			Mat image = Imgcodecs.imread("materials\\cal\\chessboard"+i+".png");
			Mat grayImg = new Mat();
			Imgproc.cvtColor(image, grayImg, Imgproc.COLOR_BGRA2GRAY);
			
			MatOfPoint2f corners = new MatOfPoint2f();
			Size size = new Size(xsize, ysize);
			boolean found = Calib3d.findChessboardCorners(image, size, corners);
			if(found) {
				Imgproc.cornerSubPix(grayImg, corners, new Size(11,11), new Size(-1,-1), new TermCriteria(TermCriteria.EPS+TermCriteria.MAX_ITER, 30, 0.1));
				cornerList.add(corners);
			}
			Calib3d.drawChessboardCorners(image, new Size(11,11), corners, found);
			Imgcodecs.imwrite("materials\\cal\\drawnimg"+i+".png", image);
		}
		Mat cameraMatrix = Mat.eye(new Size(xsize,ysize), CvType.CV_64F);
		Mat distCoeffs =  Mat.zeros(new Size(0,0), CvType.CV_64F);
		Calib3d.calibrateCamera(cornerList, imagePoint, new Size(xsize,ysize), cameraMatrix,distCoeffs, rvecs, tvecs);
		Mat imgIn = Imgcodecs.imread("materials\\picture_9.png");
		
		Imgproc.undistort(imgIn, imgIn, cameraMatrix, distCoeffs);
		Imgcodecs.imwrite("materials\\drawming.png",imgIn);
	}
}
