import java.awt.Color;


public class Crypter {	
	private Picture copy;
	private Picture key;
	private int bytes;
	private int countc; //count chars at text, when crypting

	public Crypter(String key){
		this.key = new Picture(key);
		copy = new Picture(key);
		bytes = this.key.width() * this.key.height()-2;
		
	}
	
	public String deCrypt(String file){
		if(file.length() < 1)
			return null;
		copy = new Picture(file);
		int allowed = deCalcAllowed(1, 1);
		int count=0;
		String text = "";
		
		for(int i = 0; i<key.width(); i++)
			for(int j = 0; j<key.height(); j++){
				if(!(i == 1 && j == 1)){
					count++;
					if(count==allowed){
						text += deCryptChar(i, j);
						count=0;
					}				
				}
			}
		
		return text;
	}

	public boolean encrypt(String text, String file){
		copy = new Picture(key.toString());		
		countc = 0;		
		int count=0;
		
		if(text.length() == 0) return false; // Nothing to encrypt
		if(text.length() > bytes) return false; // The string is too long
		
		int allowed = (int) Math.floor(bytes/text.length()); // bytes between characters. includes char.				
		allowed(allowed, 1, 1);		
		
		//int badData = (int)Math.floor(bytes/allowed)-text.length(); 
		
		try{
			for(int i = 0; i<key.width(); i++)
				for(int j = 0; j<key.height(); j++){
					if(!(i == 1 && j == 1)){
						count++;
						if(count==allowed){
							cryptChar(i, j, next(text));
							count=0;
						}
						else{
							cryptChar(i, j, 0);
						}				
					}
				}
			
			copy.save(file);
		}
		catch(Exception e){
			System.out.println("I'm sorry, something went wrong, probably invalid character, try using characters present in 8-bit ASCII");
			return false;
		}
		
		return true;		
	}
	
	private char deCryptChar(int x, int y){
		Color d = difference(x, y);
		int a = d.getRed() + d.getGreen() + d.getBlue();
		return (char) a;
	}
	
	private void cryptChar(int x, int y, int a){
		Color pix = key.get(x,y);
		int blue = pix.getBlue();
		int red = pix.getRed();
		int green = pix.getGreen();
		
		if(a == 0){
			int intensity=30; // close to 40 is most secure
			int r = (int)(Math.random()*intensity);
			if(blue >= 128) blue -= r;
			else  blue += r;
			r = (int)(Math.random()*intensity);
			if(green >= 128) green -= r;
			else  green += r;
			r = (int)(Math.random()*intensity);
			if(red >= 128) red -= r;
			else  red += r;
		}
		else{
			Coord c = split3(a);
			int r = c.x;
			if(blue >= 128) blue -= r;
			else  blue += r;
			r = c.y;
			if(green >= 128) green -= r;
			else  green += r;
			r = c.z;
			if(red >= 128) red -= r;
			else  red += r;
		}		
		pix = new Color(red, green, blue);
		copy.set(x, y, pix);
	
	}
	
	private int next(String text){
		char a = 0;
		if(text.length() > countc){
		a=text.charAt(countc);		
		countc++;
		}		
		return a;		
	}
	
	private void allowed(int allowed, int x, int y){
		Color pix = key.get(x,y);
		int blue = pix.getBlue();
		int red = pix.getRed();
		int green = pix.getGreen();
		
		int count = 0;
		while(allowed > 127){
			count++;
			allowed-=127;
		}
		if(count > 0){
			Coord e = split2(count);
			if(red < 128){
				red += e.x;
			}
			else{
				red -= e.x;
			}
			if(green < 128){
				green += e.y;
			}
			else{
				green -= e.y;
			}
		}
		if(allowed <= 127){			
			if(blue < 128){				
				blue += allowed;
			}
			else if(blue >= 128){				
				blue -= allowed;
			}			
			
		}
		
		pix = new Color(red, green, blue);
		copy.set(x, y, pix);
		
		
	}
	
	private int deCalcAllowed(int x, int y){
		Color d = difference(x, y);
		return (d.getRed() * 127 + d.getGreen() * 127) + d.getBlue();
	}
	
	private Color difference(int x, int y){
		Color pix = key.get(x,y);
		int blue = pix.getBlue();
		int red = pix.getRed();
		int green = pix.getGreen();
		Color c = copy.get(x,y);
		int Cblue = c.getBlue();
		int Cred = c.getRed();
		int Cgreen = c.getGreen();
		return new Color(Math.abs(red-Cred), Math.abs(green-Cgreen), Math.abs(blue-Cblue)); 
	}
	
	private Coord split2(int a){
		int r = (int)(Math.random() * a);
		a-=r;
		Coord c = new Coord(a, r);
		return c;
	}
	
	private Coord split3(int a){
		int z = 0;
		int r = (int)(Math.random() * a);
		a-=r;
		if(a > r){
			z = (int)(Math.random() * a);
			a-=z;			
		}
		else{
			z = (int)(Math.random() * r);
			r-=z;			
		}
		
		Coord c = new Coord(a, r, z);
		return c;
	}
	
	
}

class Coord {
	public int x;
	public int y;
	public int z;
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Coord(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

}

