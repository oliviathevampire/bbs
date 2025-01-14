package mchorse.bbs.camera.values;

import mchorse.bbs.camera.data.Point;
import mchorse.bbs.settings.values.base.BaseValueBasic;
import mchorse.bbs.data.types.BaseType;

public class ValuePoint extends BaseValueBasic<Point>
{
    public ValuePoint(String id, Point point)
    {
        super(id);

        this.value = point;
    }

    @Override
    public void reset()
    {
        this.value.set(0, 0, 0);
    }

    @Override
    public BaseType toData()
    {
        return this.value.toData();
    }

    @Override
    public void fromData(BaseType data)
    {
        this.value.fromData(data.asMap());
    }
}