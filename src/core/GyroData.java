package core;

public class GyroData {
	static GyroData ourIstance = new GyroData();
	public float[] physGyros;
	private GyroData() {
		physGyros = new float[]{0, 0, 0};
	}
	
}
