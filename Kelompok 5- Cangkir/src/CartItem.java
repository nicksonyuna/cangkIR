package cangkIR;

public class CartItem {
	private String cupName;
	private double price;
	private int quantity;
	private double total;

	public CartItem(String cupName, double price, int quantity) {
		this.cupName = cupName;
		this.price = price;
		this.quantity = quantity;
		this.total = price * quantity;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	// Other getters and setters if needed
}