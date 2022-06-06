package net.plazmix.inventory.impl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.inventory.addon.BaseInventoryUpdater;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.action.impl.ClickableButtonAction;
import net.plazmix.inventory.button.action.impl.DraggableButtonAction;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import net.plazmix.inventory.button.impl.DraggableInventoryButton;
import net.plazmix.inventory.button.impl.SimpleInventoryButton;
import net.plazmix.inventory.manager.BukkitInventoryManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class BaseSimpleInventory implements BaseInventory {

    protected String inventoryTitle;

    protected int inventoryRows;
    protected int inventorySize;

    protected BaseInventoryUpdater inventoryUpdater;
    protected Inventory bukkitInventory;

    protected final TIntObjectMap<BaseInventoryButton> buttons = new TIntObjectHashMap<>();

    /**
     * Инициализировать данные инвентаря
     *
     * @param inventoryTitle - название инвентаря
     * @param inventoryRows  - количество линий в разметке инвентаря
     */
    public BaseSimpleInventory(String inventoryTitle, int inventoryRows) {
        this.inventoryTitle = inventoryTitle;
        this.inventoryRows = inventoryRows;

        this.inventorySize = inventoryRows * 9;
    }

    @Override
    public void create(Player player, boolean inventoryInitialize) {
        if (inventoryInitialize) {
            this.bukkitInventory = Bukkit.createInventory(player, inventorySize, inventoryTitle);
        }

        if (player != null) {

            this.clearInventory(player);
            this.drawInventory(player);
        }

        buttons.forEachEntry((buttonSlot, inventoryButton) -> {

            bukkitInventory.setItem(buttonSlot - 1, inventoryButton.getItemStack());
            return true;
        });
    }

    @Override
    public void openInventory(@NonNull Player player) {
        this.create(player, true);

        BukkitInventoryManager.INSTANCE.addOpenInventoryToPlayer(player, this);
        player.openInventory(bukkitInventory);
    }

    @Override
    public void updateInventory(@NonNull Player player) {
        this.create(player, false);

        // player.openInventory(bukkitInventory);
    }

    @Override
    public void clearInventory(@NonNull Player player) {
        this.bukkitInventory.clear();
        this.buttons.clear();
    }

    @Override
    public void setInventoryTitle(@NonNull String inventoryTitle) {
        Validate.isTrue(inventoryTitle.length() < 32, "inventory title length cannot be > 32");

        this.inventoryTitle = inventoryTitle;
        this.create(null, true);
    }

    @Override
    public void setInventoryRows(int inventoryRows) {
        Validate.isTrue(inventoryRows <= 6, "inventory rows length cannot be > 6");

        this.inventoryRows = inventoryRows;
        this.inventorySize = inventoryRows * 9;
        this.create(null, true);
    }

    @Override
    public void setInventorySize(int inventorySize) {
        Validate.isTrue(inventoryRows % 9 == 0, "Inventory must have a size that is a multiple of 9!");

        this.inventorySize = inventorySize;
        this.inventoryRows = inventorySize / 9;
        this.create(null, true);
    }

    @Override
    public void setItem(int buttonSlot, @NonNull BaseInventoryButton inventoryButton) {
        buttons.put(buttonSlot, inventoryButton);
    }

    @Override
    public void setOriginalItem(int buttonSlot,
                             ItemStack itemStack) {

        BaseInventoryButton inventoryButton = new SimpleInventoryButton(itemStack);
        this.setItem(buttonSlot, inventoryButton);
    }

    @Override
    public void setClickItem(int buttonSlot,
                        ItemStack itemStack,
                        ClickableButtonAction buttonAction) {

        BaseInventoryButton inventoryButton = new ActionInventoryButton(itemStack, buttonAction);
        this.setItem(buttonSlot, inventoryButton);
    }

    @Override
    public void setDragItem(int buttonSlot,
                            ItemStack itemStack,
                            DraggableButtonAction buttonAction) {

        BaseInventoryButton inventoryButton = new DraggableInventoryButton(itemStack, buttonAction);
        this.setItem(buttonSlot, inventoryButton);
    }

    @Override
    public void setInventoryUpdater(long updateTicks, @NonNull BaseInventoryUpdater inventoryUpdater) {
        if (this.inventoryUpdater != null) {
            this.inventoryUpdater.cancelUpdater();
        }

        this.inventoryUpdater = inventoryUpdater;

        inventoryUpdater.setEnable(true);
        inventoryUpdater.startUpdater(updateTicks);
    }

    @Override
    public void onOpen(Player player) {
        // override me.
    }

    @Override
    public void onClose(Player player) {
        // override me.
    }

}
