package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.utils.TimeUtils;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ByteType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.widgets.UIEnvelope;
import mchorse.bbs.ui.camera.utils.undo.CameraWorkUndo;
import mchorse.bbs.ui.camera.utils.undo.ValueChangeUndo;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.undo.IUndo;

public abstract class UIClip <T extends Clip> extends UIElement
{
    public static final IKey CATEGORY = UIKeys.CAMERA_PANELS_KEYS_TITLE;

    public T clip;
    public UICameraPanel editor;

    public UIToggle enabled;
    public UITextbox title;
    public UIColor color;
    public UITrackpad layer;
    public UITrackpad tick;
    public UITrackpad duration;
    public UIEnvelope envelope;

    protected UIScrollView left;
    protected UIScrollView right;

    public static CameraWorkUndo undo(UICameraPanel editor, BaseValue property, BaseType newValue)
    {
        return undo(editor, property, property.toData(), newValue);
    }

    public static CameraWorkUndo undo(UICameraPanel editor, BaseValue property, BaseType oldValue, BaseType newValue)
    {
        return new ValueChangeUndo(property.getPath(), oldValue, newValue).editor(editor);
    }

    public UIClip(T clip, UICameraPanel editor)
    {
        this.clip = clip;
        this.editor = editor;

        this.enabled = new UIToggle(UIKeys.CAMERA_PANELS_ENABLED, (b) -> this.editor.postUndo(this.undo(this.clip.enabled, new ByteType(b.getValue()))));
        this.title = new UITextbox(1000, (t) -> this.editor.postUndo(this.undo(this.clip.title, new StringType(t))));
        this.title.tooltip(UIKeys.CAMERA_PANELS_TITLE_TOOLTIP);
        this.color = new UIColor((c) -> this.editor.postUndo(this.undo(this.clip.color, new IntType(c))));
        this.color.tooltip(UIKeys.CAMERA_PANELS_COLOR_TOOLTIP);
        this.layer = new UITrackpad((v) -> this.editor.timeline.updateClipProperty(this.clip.layer, v.intValue()));
        this.layer.limit(0, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_LAYER);
        this.tick = new UITrackpad((v) -> this.editor.timeline.updateClipProperty(this.clip.tick, TimeUtils.fromTime(v)));
        this.tick.limit(0, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_TICK);
        this.duration = new UITrackpad((v) ->
        {
            this.editor.timeline.updateClipProperty(this.clip.duration, TimeUtils.fromTime(v));
            this.updateDuration(TimeUtils.fromTime(v));
        });
        this.duration.limit(1, Integer.MAX_VALUE, true).tooltip(UIKeys.CAMERA_PANELS_DURATION);
        this.envelope = new UIEnvelope(this);

        this.left = UI.scrollView(5, 10);
        this.left.scroll.opposite = true;
        this.left.relative(this).w(140).h(1F);
        this.right = UI.scrollView(5, 10);
        this.right.relative(this).x(1F, -140).w(140).h(1F);

        this.add(this.left, this.right);
        this.left.add(UI.label(UIKeys.CAMERA_PANELS_TITLE).background(), this.title);
        this.left.add(this.enabled.marginBottom(6));
        this.left.add(UI.label(UIKeys.CAMERA_PANELS_COLOR).background(), this.color);
        this.left.add(UI.label(UIKeys.CAMERA_PANELS_METRICS).background(), this.layer, this.tick, this.duration);
        this.left.add(UI.label(UIKeys.CAMERA_PANELS_ENVELOPES_TITLE).background().marginTop(12), this.envelope);
    }

    public void handleUndo(IUndo<CameraWork> undo, boolean redo)
    {
        this.fillData();
    }

    public IUndo<CameraWork> undo(BaseValue property, BaseType newValue)
    {
        return this.undo(property, property.toData(), newValue);
    }

    public IUndo<CameraWork> undo(BaseValue property, BaseType oldValue, BaseType newValue)
    {
        return undo(this.editor, property, oldValue, newValue);
    }

    protected void updateDuration(int duration)
    {
        this.envelope.updateDuration();
    }

    public void cameraEditorWasOpened()
    {}

    public void editClip(Position position)
    {
        this.fillData();
    }

    public void fillData()
    {
        TimeUtils.configure(this.tick, 0);
        TimeUtils.configure(this.duration, 1);

        this.enabled.setValue(this.clip.enabled.get());
        this.title.setText(this.clip.title.get());
        this.color.setColor(this.clip.color.get());
        this.layer.setValue(this.clip.layer.get());
        this.tick.setValue(TimeUtils.toTime(this.clip.tick.get()));
        this.duration.setValue(TimeUtils.toTime(this.clip.duration.get()));
        this.envelope.fillData();
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.gradientHBox(this.left.area.x, this.left.area.y, this.left.area.mx(), this.left.area.ey(), Colors.A25, 0);

        super.render(context);
    }
}