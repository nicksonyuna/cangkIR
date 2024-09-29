package cangkIR;

public class Courier {
	private String courierID;
	private String courierName;
	private int courierPrice;

	public Courier(String courierID, String courierName, int courierPrice) {
		super();
		this.courierID = courierID;
		this.courierName = courierName;
		this.courierPrice = courierPrice;
	}

	public String getCourierID() {
		return courierID;
	}

	public void setCourierID(String courierID) {
		this.courierID = courierID;
	}

	public String getCourierName() {
		return courierName;
	}

	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}

	public int getCourierPrice() {
		return courierPrice;
	}

	public void setCourierPrice(int courierPrice) {
		this.courierPrice = courierPrice;
	}
	
	@Override
    public String toString() {
        return courierName;
    }

}
