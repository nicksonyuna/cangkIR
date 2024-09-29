package cangkIR;

import java.util.ArrayList;

public class Database {
	private static ArrayList<User> users = new ArrayList<>();

	public static ArrayList<User> fetchUsers() {
		return new ArrayList<>(users);
	}

	public static void addUser(User user) {
		if (user.getRole() != null && (user.getRole().equals("admin") || user.getRole().equals("user"))) {
			users.add(user);
		} else {
			System.out.println("Error: User has an invalid role.");
		}
	}

}
