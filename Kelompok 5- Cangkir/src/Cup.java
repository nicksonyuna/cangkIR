package cangkIR;

public class Cup {
	private String cupID;
	private String cupName;
	private double price;

	public Cup(String cupID, String cupName, double price) {
		super();
		this.cupID = cupID;
		this.cupName = cupName;
		this.price = price;
	}

	public String getCupID() {
		return cupID;
	}

	public void setCupID(String cupID) {
		this.cupID = cupID;
	}

	public String getCupName() {
		return cupName;
	}

	public void setCupName(String cupName) {
		this.cupName = cupName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
