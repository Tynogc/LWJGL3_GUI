#version 330

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_normal;
layout (location = 2) in vec2 in_texCo;
layout (location = 3) in vec3 in_tangent;
layout (location = 4) in vec3 in_biTangent;

out mat3 v_tbn;
out vec2 v_texCo;
out vec3 v_pos;

uniform mat4 u_projection;
uniform mat4 u_transformation1;
uniform mat4 u_transformation2;

void main()
{
	vec4 p = u_transformation1 * u_transformation2 * vec4(in_position, 1.0);
	gl_Position = u_projection * p;
	v_pos = p.xyz;
	v_texCo = in_texCo;
	
	vec3 normal = normalize(u_transformation1 * u_transformation2 * vec4(in_normal, 0.0)).xyz;
	vec3 t = normalize(u_transformation1 * u_transformation2 * vec4(in_tangent, 0.0)).xyz;
	vec3 b = normalize(u_transformation1 * u_transformation2 * vec4(in_biTangent, 0.0)).xyz;
	
	v_tbn = mat3(t, b, normal);
}