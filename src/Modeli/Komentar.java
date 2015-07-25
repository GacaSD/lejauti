package Modeli;

public class Komentar {
	private String user;
	private int komentarID;
	private int mestoID;
	private String datum;
	private String tekstKomentara;
	private double ocena;
	private String slika;
	private String drzava;
	private String grad;
	private String maps;

	public String getDrzava() {
		return drzava;
	}

	public void setDrzava(String drzava) {
		this.drzava = drzava;
	}

	public String getGrad() {
		return grad;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public String getSlika() {
		return slika;
	}

	public void setSlika(String slika) {
		this.slika = slika;
	}

	public int getKomentarID() {
		return komentarID;
	}

	public void setKomentarID(int komentarID) {
		this.komentarID = komentarID;
	}

	public int getMestoID() {
		return mestoID;
	}

	public void setMestoID(int mestoID) {
		this.mestoID = mestoID;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getTekstKomentara() {
		return tekstKomentara;
	}

	public void setTekstKomentara(String tekstKomentara) {
		this.tekstKomentara = tekstKomentara;
	}

	public double getOcena() {
		return ocena;
	}

	public void setOcena(double ocena) {
		this.ocena = ocena;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setMaps(String maps) {
		this.maps = maps;
	}

	public String getMaps() {
		return maps;
	}

}
