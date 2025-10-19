#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    
    // Fórmula de luminância (padrão ITU-R BT.709)
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    
    fragColor = vec4(vec3(gray), color.a);
}
