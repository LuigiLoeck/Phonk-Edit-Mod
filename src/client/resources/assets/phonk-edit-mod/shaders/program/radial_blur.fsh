#version 150

uniform sampler2D DiffuseSampler;
uniform float BlurIntensity;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 center = vec2(0.5, 0.5);
    vec2 direction = texCoord - center;
    
    vec4 color = vec4(0.0);
    float total = 0.0;
    
    // Número de samples baseado na intensidade
    int samples = int(mix(1.0, 20.0, BlurIntensity));
    
    for (int i = 0; i < samples; i++) {
        float scale = 1.0 - BlurIntensity * (float(i) / float(samples)) * 0.5;
        vec2 offset = center + direction * scale;
        color += texture(DiffuseSampler, offset);
        total += 1.0;
    }
    
    fragColor = color / total;
}
