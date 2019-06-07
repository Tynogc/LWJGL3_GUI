#version 330

in vec4 v_color;
in vec2 v_texCo;
in vec3 v_pos;

out vec4 fragColor;

uniform sampler2D tex1;
uniform sampler2D tex2;
uniform sampler2D tex3;
uniform sampler2D tex4;

uniform int u_mode;
uniform vec3 u_modeAddon1;
uniform vec3 u_modeAddon2;
uniform vec3 u_modeAddon3;

vec3 getHoloColor(vec3 n, vec3 inp){
	float intens = n.z;
	n.z = 1;
	n *= 2;
	n -= 1;
	
	vec3 to = inp - v_pos;
	float att = max((length(to)-abs(inp.z))* 0.2, 1.3);
	to = normalize(to);
	float r = max(dot(-to, vec3(0,0,-1)), 0);
	
	to = normalize(reflect(to, n));
	
	float specularFactor = max(dot(vec3(0,0,-1), to), 0.0);
	specularFactor = pow(specularFactor, 2.2);
	//Color-Shift (hologram-effect)
	float qq = (1-dot(n, normalize(inp)))*10+n.x*2.2+n.y*3.5+inp.x*0.02+inp.y*0.03;
		
	vec3 q = vec3(sin(qq+2.094)*2+1, sin(qq)*2+1, sin(qq-2.094)*2+1);
		
	return ((q/2+q/2*v_color.xyz)*intens*specularFactor + vec3(pow(r, 5.9)*0.3))/att;
}

void main()
{
	if(u_mode == 0){//Standard render
		fragColor = v_color;
	
		if(v_texCo.x <= 99){
			fragColor *= texture(tex1, v_texCo);
		}
		return;
	}
	fragColor = v_color * texture(tex1, v_texCo);
	if(u_mode == 11 || u_mode == 10){
		vec3 m = texture(tex2, v_texCo).xyz;
		float w = fragColor.w;
		fragColor += fragColor*max(dot(vec3(m.xy, 0), u_modeAddon1), 0)*0.2*m.z;
		fragColor.w = w;
	}
	if(u_mode == 11 || u_mode == 12){//Hologramm
		fragColor += vec4(getHoloColor(texture(tex3, v_texCo).xyz, u_modeAddon2), 0)*v_color;
		fragColor += vec4(getHoloColor(texture(tex3, v_texCo).xyz, u_modeAddon3), 0)*v_color;
		fragColor += vec4(getHoloColor(texture(tex4, v_texCo).xyz, u_modeAddon3), 0)*v_color;
		fragColor += vec4(getHoloColor(texture(tex4, v_texCo).xyz, u_modeAddon2), 0)*v_color;
		fragColor = min(fragColor, 1);
	}
}