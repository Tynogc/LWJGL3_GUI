#version 330

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec4 in_color;
layout (location = 2) in vec2 in_texCo;

out vec4 v_color;
out vec2 v_texCo;
out vec3 v_pos;

uniform mat4 u_transformation;

void main()
{
	gl_Position = u_transformation * vec4(in_position, 1.0);
	v_pos = in_position;
	v_color = in_color;
	v_texCo = in_texCo;
}