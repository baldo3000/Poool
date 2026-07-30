package me.baldo3000.poool.controller;

import me.baldo3000.poool.model.utils.V2d;

public interface GameEventListener {
    public void notifyPlayerInput(V2d vel);

    public void notifyCpuInput(V2d vel);
}
