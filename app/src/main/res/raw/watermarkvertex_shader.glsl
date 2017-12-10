uniform mat4 u_MVPMatrix;
attribute vec3 a_vPosition;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
void main()
{
       gl_Position = u_MVPMatrix * vec4(a_vPosition,1.0);
       v_texCoord = a_texCoord;
}