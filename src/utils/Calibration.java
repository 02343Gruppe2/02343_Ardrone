package utils;

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
		
		
		for( int i = 0 ; i<20; i++) {
			Mat image = Imgcodecs.imread("materials\\cal\\chessboard"+i+".png");
			Mat grayImg = new Mat();
			Imgproc.cvtColor(image, grayImg, Imgproc.COLOR_BGRA2GRAY);
		/*	Mat corners = new Mat();
			for(int x =0;x<xsize;x++) {
				for(int y= 0;y<ysize;y++) {
					MatOfPoint3f points = new MatOfPoint3f(new Point3(x,y,0));
					corners.push_back(points);
				}
			}*/
			MatOfPoint2f corners = new MatOfPoint2f();
			Size size = new Size(xsize, ysize);
			boolean found = Calib3d.findChessboardCorners(image, size, corners);
			if(found) {
				Imgproc.cornerSubPix(grayImg, corners, new Size(11,11), new Size(-1,-1), new TermCriteria(TermCriteria.EPS+TermCriteria.MAX_ITER, 30, 0.1));
				
			}
			Calib3d.drawChessboardCorners(image, new Size(11,11), corners, found);
			Imgcodecs.imwrite("materials\\cal\\drawnimg"+i+".png", image);
		}
	}
}
