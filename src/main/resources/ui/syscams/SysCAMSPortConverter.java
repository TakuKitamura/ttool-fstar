package ui.syscams;

import ui.TDiagramPanel;
import ui.TGComponent;

public class SysCAMSPortConverter extends SysCAMSCompositePort {
	private int period;
	private int rate;
	private int delay;
	private String origin;
	private String convType;
	
	public SysCAMSPortConverter(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos,
			TGComponent _father, TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
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

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getConvType() {
		return convType;
	}

	public void setConvType(String convType) {
		this.convType = convType;
	}
}