precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_AlphaFactor;
void main() {

      vec4 texture = texture2D(u_texture, v_texCoord );
      gl_FragColor = vec4(0,1,0,1);
      gl_FragColor.a *=u_AlphaFactor;

     }


