package Modeli;

public class CurrentParameters {

	public static String getCurrentCity() {
		return CurrentCity;
	}

	public static void setCurrentCity(String currentCity) {
		CurrentCity = currentCity;
	}

	public static String getCurrentCountry() {
		return CurrentCountry;
	}

	public static void setCurrentCountry(String currentCountry) {
		CurrentCountry = currentCountry;
	}

	static String CurrentCity;
	static String CurrentCountry;
	static String MestoID;

	public static String getMestoID() {
		return MestoID;
	}

	public static void setMestoID(String mestoID) {
		MestoID = mestoID;
	}

}
