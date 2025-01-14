package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.ShakeClip;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;

public class UIShakeClip extends UIClip<ShakeClip>
{
    public UITrackpad shake;
    public UITrackpad shakeAmount;
    public UIBitToggle active;

    public UIShakeClip(ShakeClip modifier, UICameraPanel editor)
    {
        super(modifier, editor);

        this.shake = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.shake, new FloatType(value.floatValue()))));
        this.shake.tooltip(UIKeys.CAMERA_PANELS_SHAKE, Direction.BOTTOM);

        this.shakeAmount = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.shakeAmount, new FloatType(value.floatValue()))));
        this.shakeAmount.tooltip(UIKeys.CAMERA_PANELS_SHAKE_AMOUNT, Direction.BOTTOM);

        this.active = new UIBitToggle((value) -> this.editor.postUndo(this.undo(this.clip.active, new IntType(value)))).all();

        this.right.add(UI.row(5, 0, 20, this.shake, this.shakeAmount), this.active);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.shake.setValue(this.clip.shake.get());
        this.shakeAmount.setValue(this.clip.shakeAmount.get());
        this.active.setValue(this.clip.active.get());
    }
}