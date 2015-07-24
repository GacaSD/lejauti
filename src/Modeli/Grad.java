package Modeli;

import android.graphics.Bitmap;

public class Grad {
    	private String naziv;
		private int mestoID;
		private Bitmap slika;
		private int glavni;
		
		public int getGlavni() {
			return glavni;
		}

		public void setGlavni(int glavni) {
			this.glavni = glavni;
		}
		

		public Bitmap getSlika() {
			return slika;
		}

		public void setSlika(Bitmap slika) {
			this.slika = slika;
		}

		public String getNaziv() {
			return naziv;
		}

		public void setNaziv(String naziv) {
			this.naziv = naziv;
		}

		public int getMestoID() {
			return mestoID;
		}

		public void setMestoID(int mestoID) {
			this.mestoID = mestoID;
		}

	
	}
