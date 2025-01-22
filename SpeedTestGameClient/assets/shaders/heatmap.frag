#version 330 core

out vec4 FragColor;

uniform vec2 resolution;
uniform sampler2D heatmapTexture;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;
    float value = texture(heatmapTexture, uv).r;

    // Interpolate color based on value
    vec3 color = mix(vec3(0.0, 0.0, 1.0), vec3(1.0, 0.0, 0.0), value); // Blue to Red
    FragColor = vec4(color, 1.0);
}
