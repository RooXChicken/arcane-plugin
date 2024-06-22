package com.rooxchicken.arcane.Data;

public class SkillData
{
    public String skillName;
    public int skillID;

    public String cooldownScore;

    public SkillData(String _skillName, int _skillID, String _cooldownScore)
    {
        skillName = _skillName;
        skillID = _skillID;

        cooldownScore = _cooldownScore;
    }
}
