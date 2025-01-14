#version 330

//@vertex
#import "assets:shaders/formats/vertex_2d.glsl"

out vec2 pass_uv;

void main()
{
    gl_Position = vec4(in_vertex, 0, 1);
    pass_uv = in_vertex;
}

//@fragment
in vec2 pass_uv;

out vec4 out_color;

uniform sampler2D u_texture;
uniform sampler2D u_position;
uniform sampler2D u_normal;
uniform sampler2D u_lighting;
uniform sampler2D u_lightmap;
uniform sampler2D u_depth;

uniform vec2 u_screen_size;

uniform int u_fog;
uniform vec3 u_shading;

#import "sandbox:shaders/sky.glsl"

void apply_lightmap(inout vec4 result, vec2 coords)
{
    result.rgb *= texture(u_lightmap, coords).rgb;
}

void main()
{
    vec2 uv = (pass_uv / 2) + 0.5;
    vec3 position = texture(u_position, uv).xyz;
    vec3 normal = texture(u_normal, uv).xyz;
    vec4 lmap = texture(u_lighting, uv);

    out_color = texture(u_texture, uv);

    if (normal.x != 0 || normal.y != 0 || normal.z != 0)
    {
        if (lmap.w == 1)
        {
            apply_lightmap(out_color, lmap.xy);
        }

        float NdotU = dot(normal, u_shading);
        float shadingFactor = clamp(0.7 + NdotU * 0.3, 0, 1);

        out_color.rgb *= shadingFactor;

        if (u_fog > 0)
        {
            out_color.rgb = mix_fog(out_color.rgb, u_fog, position);
        }
    }
}