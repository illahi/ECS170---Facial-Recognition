public class Image {
	public int type;
	public String name;
	public int[] imageArr;

	public Image(int type, String name, String data) {
		this.type = type;
		//System.out.println("FAce type: "+ type);
		this.name = name;
		this.imageArr = new int[128*120];

		String[] dataArr = data.split("[ \n\r]+");

		for (int i = 0; i < 128*120; i++) {
			imageArr[i] = Integer.valueOf(dataArr[i]);
		}
	}
}
