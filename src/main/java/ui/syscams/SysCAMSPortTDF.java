package ui.syscams;

import java.awt.Graphics;

import ui.TDiagramPanel;
import ui.TGComponent;

public class SysCAMSPortTDF extends SysCAMSPrimitivePort {
	private int period;
	private int rate;
	private int delay;
	private String origin;
	private String TDFType;
	
	public SysCAMSPortTDF(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
			TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
	}
	
	public String getTDFType() {
		return TDFType;
	}

	public void setTDFType(String tDFType) {
		TDFType = tDFType;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void drawParticularity(Graphics g) {
	}
}