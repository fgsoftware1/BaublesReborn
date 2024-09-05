package io.babywolf.forge.api.bauble;

import java.util.Arrays;

public enum BaubleType {
    CANISTER(0, 1, 2),
    AMULET(3),
    RING(4, 5),
    BELT(6),
    HEAD(7),
    BODY(8),
    CHARM(9),
    TRINKET(3, 4, 5, 6, 7, 8, 9);

    int[] validSlots;

    BaubleType(int... validSlots) {
        this.validSlots = validSlots;
    }

    public static BaubleType getFromString(String type) {
        return switch (type) {
            case "canister" -> BaubleType.CANISTER;
            case "ring" -> BaubleType.RING;
            case "amulet" -> BaubleType.AMULET;
            case "belt" -> BaubleType.BELT;
            case "head" -> BaubleType.HEAD;
            case "body" -> BaubleType.BODY;
            case "charm" -> BaubleType.CHARM;
            default -> TRINKET;
        };
    }

    public boolean hasSlot(int slot) {
        return Arrays.stream(validSlots).anyMatch(s -> s == slot);
    }

    public int[] getValidSlots() {
        return validSlots;
    }
}