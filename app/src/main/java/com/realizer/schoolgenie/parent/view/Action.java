package com.realizer.schoolgenie.parent.view;

/**
 * Created by Bhagyashri on 9/14/2016.
 */
public enum Action {
    LR, // Left to right
    RL, // Right to left
    TB, // Top to bottom
    BT, // Bottom to top
    None ;// Action not found

    public static Action getSwipeType(String type)
    {
        switch (type) {

            case "LR":
                return Action.LR;
            case "RL":
                return Action.RL;
            case "TB":
                return Action.TB;
            case "BT":
                return Action.BT;
            default:
                return Action.None;
        }
    }
}
