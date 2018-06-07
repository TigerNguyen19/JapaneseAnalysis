package org.kysubrse.tigernguyen.japaneseanalysis.data;

public class OutputData {
	private int No;
	public OutputData() {
	}
	
	public OutputData(int no, String strWordJP, String kana, String strWordVN) {
		this.No = no;
        this.strWordJP = strWordJP;
        this.kana = kana;
        this.strWordVN = strWordVN;
	}
	public int getNo() {
		return No;
	}
	public void setNo(int no) {
		No = no;
	}
	public String getStrWordJP() {
		return strWordJP;
	}
	public void setStrWordJP(String strWordJP) {
		this.strWordJP = strWordJP;
	}
	public String getKana() {
		return kana;
	}
	public void setKana(String kana) {
		this.kana = kana;
	}
	public String getStrWordVN() {
		return strWordVN;
	}
	public void setStrWordVN(String strWordVN) {
		this.strWordVN = strWordVN;
	}
	private String strWordJP;
	private String kana;
	private String strWordVN;	
}
