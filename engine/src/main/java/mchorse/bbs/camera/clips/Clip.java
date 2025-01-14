package mchorse.bbs.camera.clips;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.camera.values.ValueEnvelope;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.utils.math.Interpolations;

public abstract class Clip extends StructureBase
{
    public final ValueBoolean enabled = new ValueBoolean("enabled", true);
    public final ValueString title = new ValueString("title", "");
    public final ValueInt color = new ValueInt("color", 0);
    public final ValueInt layer = new ValueInt("layer", 0, 0, Integer.MAX_VALUE);
    public final ValueInt tick = new ValueInt("tick", 0, 0, Integer.MAX_VALUE);
    public final ValueInt duration = new ValueInt("duration", 1, 1, Integer.MAX_VALUE);
    public final ValueEnvelope envelope = new ValueEnvelope("envelope");

    public Clip()
    {
        this.register(this.enabled);
        this.register(this.title);
        this.register(this.color);
        this.register(this.layer);
        this.register(this.tick);
        this.register(this.duration);
        this.register(this.envelope);
    }

    public boolean isGlobal()
    {
        return false;
    }

    public void shutdown(ClipContext context)
    {}

    public boolean isInside(int tick)
    {
        int offset = this.tick.get();

        return tick >= offset && tick < offset + this.duration.get();
    }

    public void fromCamera(Camera camera)
    {}

    public void applyLast(ClipContext context, Position position)
    {
        int duration = this.duration.get();

        context.ticks = this.tick.get() + duration;
        context.relativeTick = duration;
        context.transition = 0;
        context.currentLayer = 0;

        this.applyClip(context, position);
    }

    public void apply(ClipContext context, Position position)
    {
        if (!this.enabled.get())
        {
            return;
        }

        float factor = this.envelope.get().factorEnabled(this.duration.get(), context.relativeTick + context.transition);

        if (factor >= 1)
        {
            this.applyClip(context, position);
        }
        else
        {
            Position temporary = new Position();

            temporary.set(position);
            this.applyClip(context, temporary);

            position.point.x = Interpolations.lerp(position.point.x, temporary.point.x, factor);
            position.point.y = Interpolations.lerp(position.point.y, temporary.point.y, factor);
            position.point.z = Interpolations.lerp(position.point.z, temporary.point.z, factor);

            position.angle.yaw = Interpolations.lerp(position.angle.yaw, temporary.angle.yaw, factor);
            position.angle.pitch = Interpolations.lerp(position.angle.pitch, temporary.angle.pitch, factor);
            position.angle.roll = Interpolations.lerp(position.angle.roll, temporary.angle.roll, factor);
            position.angle.fov = Interpolations.lerp(position.angle.fov, temporary.angle.fov, factor);
        }
    }

    protected abstract void applyClip(ClipContext context, Position position);

    public Clip copy()
    {
        Clip clip = this.create();

        clip.copy(this);

        return clip;
    }

    protected abstract Clip create();

    /**
     * Breakdown this fixture into another piece starting at given offset
     */
    public Clip breakDown(int offset)
    {
        int duration = this.duration.get();

        if (offset <= 0 || offset >= duration)
        {
            return null;
        }

        Clip clip = this.copy();

        clip.duration.set(duration - offset);
        clip.breakDownClip(this, offset);

        return clip;
    }

    protected void breakDownClip(Clip original, int offset)
    {
        this.envelope.get().breakDown(original, offset);
    }
}