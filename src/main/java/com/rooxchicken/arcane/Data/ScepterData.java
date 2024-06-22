package com.rooxchicken.arcane.Data;

import java.util.HashMap;

public class ScepterData
{
    public int id;
    public String name;
    public String itemName;

    public HashMap<String, SkillData> skills;

    public ScepterData(int _id, String _name, String _itemName)
    {
        id = _id;
        name = _name;
        itemName = _itemName;
    }
}
