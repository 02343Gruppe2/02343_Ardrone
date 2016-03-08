package gui.utils;

import java.awt.Color;
import java.util.Random;

public class CreateRandomColor {
	public static Color getRandomColor() {
		Random ran = new Random();
		
		int red = ran.nextInt(256);
		int green = ran.nextInt(256);
		int blue = ran.nextInt(256);;
		
		return new Color(red,green,blue);
	}
}
