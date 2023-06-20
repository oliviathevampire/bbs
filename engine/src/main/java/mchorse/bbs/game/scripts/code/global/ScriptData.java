package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.*;
import mchorse.bbs.game.scripts.user.global.IScriptData;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptData implements IScriptData {
    @Override
    public MapType map(String data) {
        MapType map = DataToString.mapFromString(data);

        return map == null ? new MapType() : map;
    }

    @Override
    public MapType mapFromJS(Object jsObject) {
        BaseType base = this.convertToData(jsObject);

        return base instanceof MapType ? (MapType) base : null;
    }

    @Override
    public ListType list(String string) {
        ListType list = DataToString.listFromString(string);

        return list == null ? new ListType() : list;
    }

    @Override
    public ListType listFromJS(Object jsObject) {
        BaseType base = this.convertToData(jsObject);

        return base instanceof ListType ? (ListType) base : null;
    }

    private BaseType convertToData(Object object) {
        if (object instanceof String) {
            return new StringType((String) object);
        } else if (object instanceof Double) {
            return new DoubleType((Double) object);
        } else if (object instanceof Integer) {
            return new IntType((Integer) object);
        } else if (object instanceof ScriptObjectMirror mirror) {

            if (mirror.isArray()) {
                ListType list = new ListType();

                for (int i = 0, c = mirror.size(); i < c; i++) {
                    BaseType base = this.convertToData(mirror.getSlot(i));

                    if (base != null) {
                        list.add(base);
                    }
                }

                return list;
            } else {
                MapType map = new MapType();

                for (String key : mirror.keySet()) {
                    BaseType base = this.convertToData(mirror.get(key));

                    if (base != null) {
                        map.put(key, base);
                    }
                }

                return map;
            }
        }

        return null;
    }
}