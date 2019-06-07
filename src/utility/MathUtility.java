package utility;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MathUtility {

	public static float cubeRadius(float edge){
		edge /= 2;
		float a = (float)Math.sqrt(edge*edge*2);
		return (float)Math.sqrt(a*a+edge*edge);
	}
	
	public static Vector3f decodeVector(String s){
		if(s.equalsIgnoreCase("x"))return new Vector3f(1,0,0);
		if(s.equalsIgnoreCase("-x"))return new Vector3f(-1,0,0);
		if(s.equalsIgnoreCase("y"))return new Vector3f(0,1,0);
		if(s.equalsIgnoreCase("-y"))return new Vector3f(0,-1,0);
		if(s.equalsIgnoreCase("z"))return new Vector3f(0,0,1);
		if(s.equalsIgnoreCase("-z"))return new Vector3f(0,0,-1);
		
		if(s.startsWith("("))s = s.substring(1, s.length()-2);
		String[] st = s.trim().split(" ");
		return new Vector3f(Float.parseFloat(st[0]), Float.parseFloat(st[1]), Float.parseFloat(st[2]));
	}
	/**
	 * @Deprecated Buged! don't use!
	 */
	@Deprecated
	public static Vector3f solve(Matrix3f m, Vector3f v){
		float q;
		
		q = m.m00/m.m01;
		m.m01 = 0;
		m.m11 += m.m10*q;
		m.m21 += m.m20*q;
		v.y += v.x*q;
		
		q = m.m00/m.m02;
		m.m02 = 0;
		m.m12 += m.m10*q;
		m.m22 += m.m20*q;
		v.z += v.x*q;
		
		q = m.m11/m.m12;
		m.m12 = 0;
		m.m22 += m.m21*q;
		v.z += v.y*q;
		
		q = m.m22/m.m21;
		m.m21 += m.m22*q;
		v.y += v.z*q;
		
		q = m.m22/m.m20;
		m.m20 += m.m22*q;
		v.x += v.z*q;
		
		q = m.m11/m.m10;
		m.m10 += m.m11*q;
		v.x += v.y*q;
		
		return v;
	}
	
	private static Vector3f vv = new Vector3f();
	public static void smoothVector(Vector3f vIs, Vector3f vTo, float dist){
		vv.set(vIs).sub(vTo);
		float l = vv.length();
		if(l<0.01)return;
		
		vIs.x = smooth(vIs.x, vTo.x, dist*Math.abs(vIs.x-vTo.x)/l);
		vIs.y = smooth(vIs.y, vTo.y, dist*Math.abs(vIs.y-vTo.y)/l);
		vIs.z = smooth(vIs.z, vTo.z, dist*Math.abs(vIs.z-vTo.z)/l);
	}
	
	public static float smooth(float is, float to, float dist){
		if(is<to){
			is+=dist;
			if(is>to) return to;
		}else{
			is-=dist;
			if(is<to) return to;
		}
		return is;
	}
	
	public static String convertMathStringToFloat(String s){
		double value = 0;
		
		byte operator = 1;
		
		int startMark = 0;
		
		String st = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == ' ')continue;
			if(c == ','){
				st+=".";
			}else{
				st+=c;
			}
		}
		s = st;
		
		for (int i = 0; i < s.length()+1; i++) {
			char c = '-';
			if(i != s.length())
				c = s.charAt(i);
			byte nop = 0;
			if(i == s.length());
			else if(c == '+')nop = 1;
			else if(c == '-')nop = 2;
			else if(c == '*')nop = 3;
			else if(c == '/')nop = 4;
			else if(c == '^')nop = 5;
			else continue;
			
			double d = 0;
			if(i>0)
				d = Double.parseDouble(st.substring(startMark, i));
			if(operator == 1){
				value += d;
			}else if(operator == 2){
				value -= d;
			}else if(operator == 3){
				value *= d;
			}else if(operator == 4){
				value /= d;
			}else if(operator == 5){
				value = Math.pow(value, d);
			}
			
			operator = nop;
			startMark = i+1;
		}
		
		return ""+value;
	}
	
	public static String convertMathStringToInt(String s){
		return ""+Math.round(Float.parseFloat(convertMathStringToFloat(s)));
	}
	
	public static void mvtProjection(Matrix4f m, float left, float right, float top, float bottom){
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);

		m.m00(x_orth);
		m.m10(0);
		m.m20(0);
		m.m30(tx);
		m.m01(0);
		m.m11(y_orth);
		m.m21(0);
		m.m31(ty);
		m.m02(0);
		m.m12(0);
		m.m22(1);
		m.m32(0);
		m.m03(0);
		m.m13(0);
		m.m23(0);
		m.m33(1);
	}
}
