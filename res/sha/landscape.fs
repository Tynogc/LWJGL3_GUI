#version 330

in vec2 v_texCo;
in vec3 v_pos;

in mat3 v_tbn;

out vec4 fragColor;

uniform sampler2D tex1;
uniform sampler2D tex2;

uniform vec3 u_cameraPos;

void main()
{
	vec3 toCamera = normalize(u_cameraPos-v_pos);
	
	vec3 normal;
	vec4 albedo;
	
	if(v_texCo.x>99){
	  normal = vec3(0, 0, 1);
	  albedo = vec4(1);
	}else{
	  albedo = texture(tex1, v_texCo);
	  if(albedo.x>0.99 && albedo.y == 0 && albedo.z >= 0.99) discard;
	  normal = texture(tex2, v_texCo).xyz*2-vec3(1);
	}
	normal = normalize(v_tbn*normal);
	
	fragColor = albedo*(dot(normal, toCamera)*0.8+0.2);
	//fragColor = vec4((normal+1)/2, 1);
	fragColor.w = 1;
}