package engine.rules;

import battlescript.model.Action;
import engine.context.Context;

public interface Rule { Action select(Context context); }
