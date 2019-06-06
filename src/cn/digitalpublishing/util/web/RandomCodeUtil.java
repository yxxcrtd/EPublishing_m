package cn.digitalpublishing.util.web;

public class RandomCodeUtil {
	
	private static final String pool="123456789abcdefghijklmnopqrstuvwxzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static String generateRandomCode(int length) {
		// 定义验证码的字符表 
		String chars = pool;
		char[] rands = new char[length];
		for (int i = 0; i < length; i++) {
			int rand = (int) (Math.random() * chars.length());
			rands[i] = chars.charAt(rand);
		}
		return String.valueOf(rands);
	}
	
	public static void main(String[] args){
		System.out.println(RandomCodeUtil.generateRandomCode(8));
	}

}
