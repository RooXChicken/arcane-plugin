package com.rooxchicken.arcane.Tasks;

import com.rooxchicken.arcane.Arcane;

public abstract class Task
{
    private Arcane plugin;
    public int id;

    private int tick = 0;
    public int tickThreshold = 1;
    public boolean cancel = false;

    public Task(Arcane _plugin) { plugin = _plugin; }

    public void tick()
    {
        tick++;
        if(tick < tickThreshold-1)
            return;

        run();
        tick = 0;
    }

    public void run() {}
    public void onCancel() {}
}
