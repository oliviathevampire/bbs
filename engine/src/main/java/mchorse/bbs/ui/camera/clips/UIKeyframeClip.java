package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.utils.UICameraDopeSheetEditor;
import mchorse.bbs.ui.camera.utils.UICameraGraphEditor;
import mchorse.bbs.ui.camera.utils.UICameraKeyframesEditor;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.undo.CompoundUndo;
import mchorse.bbs.utils.undo.IUndo;

import java.util.List;

public class UIKeyframeClip extends UIClip<KeyframeClip>
{
    public UIButton all;
    public UIButton x;
    public UIButton y;
    public UIButton z;
    public UIButton yaw;
    public UIButton pitch;
    public UIButton roll;
    public UIButton fov;

    public UICameraGraphEditor graph;
    public UICameraDopeSheetEditor dope;

    public IKey[] titles = new IKey[8];
    public int[] colors = {Colors.RED, Colors.GREEN, Colors.BLUE, Colors.CYAN, Colors.MAGENTA, Colors.YELLOW, Colors.LIGHTEST_GRAY};

    private IKey title = IKey.EMPTY;
    private UICameraKeyframesEditor current;

    public UIKeyframeClip(KeyframeClip clip, UICameraPanel editor)
    {
        super(clip, editor);

        this.graph = new UICameraGraphEditor(editor);
        this.dope = new UICameraDopeSheetEditor(editor);

        this.all = new UIButton(UIKeys.CAMERA_PANELS_ALL, (b) -> this.selectChannel(null, 0));
        this.x = new UIButton(UIKeys.X, (b) -> this.selectChannel(this.clip.x, 1));
        this.y = new UIButton(UIKeys.Y, (b) -> this.selectChannel(this.clip.y, 2));
        this.z = new UIButton(UIKeys.Z, (b) -> this.selectChannel(this.clip.z, 3));
        this.yaw = new UIButton(UIKeys.CAMERA_PANELS_YAW, (b) -> this.selectChannel(this.clip.yaw, 4));
        this.pitch = new UIButton(UIKeys.CAMERA_PANELS_PITCH, (b) -> this.selectChannel(this.clip.pitch, 5));
        this.roll = new UIButton(UIKeys.CAMERA_PANELS_ROLL, (b) -> this.selectChannel(this.clip.roll, 6));
        this.fov = new UIButton(UIKeys.CAMERA_PANELS_FOV, (b) -> this.selectChannel(this.clip.fov, 7));

        this.right.add(UI.label(UIKeys.CAMERA_PANELS_KEYFRAMES).background());
        this.right.add(UI.row(this.all));
        this.right.add(UI.row(this.x, this.y, this.z));
        this.right.add(UI.row(this.yaw, this.pitch));
        this.right.add(UI.row(this.roll, this.fov));

        List<UIButton> buttons = this.right.getChildren(UIButton.class);

        for (int i = 0; i < this.titles.length; i++)
        {
            this.titles[i] = buttons.get(buttons.size() - this.titles.length + i).label;
        }
    }

    @Override
    public void updateDuration(int duration)
    {
        this.dope.updateConverter();
        this.dope.keyframes.setDuration(duration);

        this.graph.updateConverter();
        this.graph.keyframes.setDuration(duration);
    }

    @Override
    public void editClip(Position position)
    {
        long tick = this.editor.timeline.tick - this.clip.tick.get();

        CompoundUndo<CameraWork> undo = new CompoundUndo<CameraWork>(
            this.undoKeyframes(this.clip.x, tick, position.point.x),
            this.undoKeyframes(this.clip.y, tick, position.point.y),
            this.undoKeyframes(this.clip.z, tick, position.point.z),
            this.undoKeyframes(this.clip.yaw, tick, position.angle.yaw),
            this.undoKeyframes(this.clip.pitch, tick, position.angle.pitch),
            this.undoKeyframes(this.clip.roll, tick, position.angle.roll),
            this.undoKeyframes(this.clip.fov, tick, position.angle.fov)
        );

        this.editor.postUndo(undo, false);
    }

    private IUndo<CameraWork> undoKeyframes(ValueKeyframeChannel channel, long tick, double value)
    {
        KeyframeChannel c = new KeyframeChannel();

        c.copy(channel.get());
        c.insert(tick, value);

        IUndo<CameraWork> undo = this.undo(channel, c.toData());

        channel.get().insert(tick, value);

        return undo;
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.graph.updateConverter();
        this.graph.keyframes.setDuration(this.clip.duration.get());
        this.dope.updateConverter();
        this.dope.keyframes.setDuration(this.clip.duration.get());

        this.dope.setClip(this.clip);
    }

    public void selectChannel(ValueKeyframeChannel channel, int id)
    {
        this.title = this.titles[id];
        this.dope.setVisible(id == 0);
        this.graph.setVisible(id != 0);

        this.current = id == 0 ? this.dope : this.graph;

        if (channel != null)
        {
            this.graph.setChannel(channel, this.colors[id - 1]);
        }

        this.editor.timeline.embedView(this.current);
        this.current.resetView();
    }

    @Override
    public void render(UIContext context)
    {
        /* Draw title of the channel */
        int x = this.area.ex() - context.font.getWidth(this.title.get()) - 10;
        int y = this.graph.area.y - context.font.getHeight() - 5;

        context.batcher.textShadow(this.title.get(), x, y);

        super.render(context);
    }
}