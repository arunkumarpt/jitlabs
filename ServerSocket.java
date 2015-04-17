package jitLabsServer;

import java.io.*;
import java.net.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.*;

public class ServerSideSocket {

	public void run() throws SQLException, ClassNotFoundException {
		try {
			int serverPort = 9999;
			ServerSocket serverSocket = new ServerSocket(serverPort);
			// serverSocket.setSoTimeout(10000);
			while (true) {
				System.out.println("Waiting for client on port "
						+ serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to "
						+ server.getRemoteSocketAddress());
				PrintWriter toClient = new PrintWriter(
						server.getOutputStream(), true);
				BufferedReader fromClient = new BufferedReader(
						new InputStreamReader(server.getInputStream()));
				String line = fromClient.readLine();
				String arr[] = line.split(" ", 2);
				int firstWord = Integer.parseInt(arr[0]);
				System.out.println(firstWord);

				String userDetails = getUserData(firstWord);
				String baseLines = getBaseLine(firstWord);
			//	System.out.println("Server received: " + line + " "
			//			+ userDetails + " " + baseLines);
				toClient.println("Your Health vitals securly received by jitLabs cloud server  ");
		//		toClient.println("Server received: " + line + " " + userDetails
		//				+ " " + baseLines);
				String toLogstash = line + " " + userDetails + " " + baseLines;
				System.out.println(toLogstash);
				// + server.getLocalSocketAddress() + "\nGoodbye!");

				// adding here
				Socket sock = new Socket("localhost", 9998);
				// Socket sock = new Socket("localhost",4040);
				DataOutputStream out = new DataOutputStream(
						sock.getOutputStream());
				//out.writeUTF(toLogstash);
				// System.out.println(toLogstash);
				 out.writeBytes(toLogstash);
				// DataInputStream in = new
				// DataInputStream(sock.getInputStream());
				// System.out.println(in.readUTF(in));
				sock.close();

			}
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Connection con;

	private static String getUserData(int n) throws SQLException,
			ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost/jitlabs";
		con = DriverManager.getConnection(url, "root", "root");

		String query = "Select FirstName,Email,DoctorEmail,FamilyEmail from UserDetails where id="
				+ n;
		PreparedStatement ps = con.prepareStatement(query);
		// ps.setString(1, uname);
		// ps.setString(2, password);
		ResultSet result = ps.executeQuery();
		while (result.next()) { // retrieve data
			String FirstName = result.getString("FirstName");
			String Email = result.getString("Email");
			String DoctorEmail = result.getString("DoctorEmail");
			String FamilyEmail = result.getString("FamilyEmail");
			// System.out.println(FirstName + " " + Email + " " + DoctorEmail
			// + " " + FamilyEmail);
			return (FirstName + " " + Email + " " + DoctorEmail + " " + FamilyEmail);
		}

		return "Cannot update :(";

	}

	private static String getBaseLine(int n) throws SQLException,
			ClassNotFoundException {

		java.text.DecimalFormat nft = new java.text.DecimalFormat("000");
		nft.setDecimalSeparatorAlwaysShown(false);

		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost/jitlabs";
		con = DriverManager.getConnection(url, "root", "root");

		String query = "Select heartrate,respiration,bloodpressure_l,bloodpressure_h,bloodoxygen,bodytemp,ambiancetemp,humidity from BaseTable where id="
				+ n;
		PreparedStatement ps = con.prepareStatement(query);
		// ps.setString(1, uname);
		// ps.setString(2, password);
		ResultSet result = ps.executeQuery();
		while (result.next()) { // retrieve data
			String heartrate = nft.format(Integer.parseInt(result
					.getString("heartrate")));
			String respiration = nft.format(Integer.parseInt(result
					.getString("respiration")));
			String bloodpressure_l = nft.format(Integer.parseInt(result
					.getString("bloodpressure_l")));
			String bloodpressure_h = nft.format(Integer.parseInt(result
					.getString("bloodpressure_h")));

			String bloodoxygen = nft.format(Integer.parseInt(result
					.getString("bloodoxygen")));
			String bodytemp = nft.format(Integer.parseInt(result
					.getString("bodytemp")));
			String ambiancetemp = nft.format(Integer.parseInt(result
					.getString("ambiancetemp")));
			String humidity = nft.format(Integer.parseInt(result
					.getString("humidity")));

			return (heartrate + " " + respiration + " " + bloodpressure_l + " "
					+ bloodpressure_h + " " + bloodoxygen + " " + bodytemp
					+ " " + ambiancetemp + " " + humidity);
		}

		return "Cannot update :(";

	}

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException {
		ServerSideSocket srv = new ServerSideSocket();
		srv.run();
	}
}
