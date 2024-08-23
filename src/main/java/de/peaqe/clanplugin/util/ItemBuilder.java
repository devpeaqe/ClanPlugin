package de.peaqe.clanplugin.util;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * *
 *  © 2024 peaqe
 *  Any use not authorized by the copyright holder will be reported to the police without exception.
 * *
 * @author peaqe
 * @version 1.0
 * @since 02.01.2024 | 19:22 Uhr
 * *
 */
@SuppressWarnings(value = "unused")
public class ItemBuilder {
    
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {

        this.item  = new ItemStack(material);
        this.meta = this.item.getItemMeta();

    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, short subID) {

        this.item  = new ItemStack(material, subID);
        this.meta = this.item.getItemMeta();

    }

    public ItemBuilder(Material material, int amount) {

        this.item  = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();

    }

    /** @deprecated */
    @Deprecated
    public ItemBuilder(Material material, int amount, short subID) {

        this.item  = new ItemStack(material, amount, subID);
        this.meta = this.item.getItemMeta();

    }

    /**
     * Sets the custom model data of the item.
     *
     * @param customModelData The custom model data to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setCustomModelData(int customModelData) {
        this.meta.setCustomModelData(customModelData);
        return this;
    }

    /**
     * Adds an item attribute modifier to the item.
     *
     * @param attribute The attribute to modify.
     * @param modifier  The modifier value.
     * @param operation The operation to apply.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier, AttributeModifier.Operation operation) {
        this.meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Removes an item attribute modifier from the item.
     *
     * @param attribute The attribute to remove the modifier from.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder removeAttributeModifier(Attribute attribute) {
        this.meta.removeAttributeModifier(attribute);
        return this;
    }

    /**
     * Sets the custom color of the item (if applicable).
     *
     * @param color The color to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setColor(Color color) {
        if (this.meta instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(color);
        }
        return this;
    }

    /**
     * Sets the display name of the item.
     *
     * @param displayName The display name to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setDisplayName(String displayName) {

        displayName = displayName.replace('&', '§');

        this.meta.displayName(Component.text(displayName));
        return this;

    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment The enchantment to add.
     * @param level The level of the enchantment.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {

        this.meta.addEnchant(enchantment, level, false);
        return this;

    }

    /**
     * Sets the lore of the item.
     *
     * @param lore The lines of lore to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setLore(String... lore){

        List<Component> lores = new ArrayList<>();
        for (var lore1 : lore) lores.add(Component.text(lore1));

        this.meta.lore(lores);
        return this;

    }

    /**
     * Adds a line of lore to the item.
     *
     * @param loreLine The lore line to add.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder addLore(String... loreLine) {

        List<Component> currentLore = this.meta.lore();

        if (currentLore == null) {
            currentLore = new ArrayList<>();
        }

        List<Component> lores = new ArrayList<>();
        for (var lore1 : loreLine) lores.add(Component.text(lore1));

        currentLore.addAll(lores);
        this.meta.lore(currentLore);

        return this;
    }

    /**
     * Adds a line of lore to the item.
     *
     * @param value If the lore has to be set.
     * @param loreLine The lore line to add.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder addLoreWithCondition(boolean value, String... loreLine) {

        if (!value) return this;

        List<Component> currentLore = this.meta.lore();

        if (currentLore == null) {
            currentLore = new ArrayList<>();
        }

        List<Component> lores = new ArrayList<>();
        for (var lore1 : loreLine) lores.add(Component.text(lore1));

        currentLore.addAll(lores);
        this.meta.lore(currentLore);

        return this;
    }

    /**
     * Sets the unbreakable state of the item.
     *
     * @param unbreakable The unbreakable state to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setUnbreakable(boolean unbreakable){

        this.meta.setUnbreakable(unbreakable);
        return this;

    }

    /**
     * @ignored
     * Sets the item as a written book with the specified author and pages.
     *
     * @param author The author of the book.
     * @param pages  The pages of the book.
     * @return The ItemBuilder instance.
     */
    @SuppressWarnings(value = "all")
    public ItemBuilder setWrittenBook(String author, String... pages) {

        this.item.setType(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) this.meta;

        bookMeta.setAuthor(author);
        bookMeta.setTitle("");

        List<Component> pagesList = new ArrayList<>();

        for (var page : pages) pagesList.add(Component.text(page));
        bookMeta.pages(pagesList);

        return this;
    }

    /**
     * Sets the item as an enchanted book with the specified enchantments.
     *
     * @param enchantments The enchantments to add to the book.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setEnchantedBook(Map<Enchantment, Integer> enchantments) {
        this.item.setType(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) this.meta;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            storageMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
        }
        return this;
    }

    /**
     * Sets the item as a firework rocket with the specified firework effect.
     *
     * @param effect The firework effect to apply to the rocket.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setFireworkRocket(FireworkEffect effect) {
        this.item.setType(Material.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = (FireworkMeta) this.meta;
        fireworkMeta.addEffect(effect);
        return this;
    }

    /**
     * @ignored
     * Sets the item as a written book with a title and pages.
     *
     * @param title The title of the book.
     * @param pages The pages of the book.
     * @return The ItemBuilder instance.
     */
    @SuppressWarnings(value = "all")
    public ItemBuilder setWrittenBookWithTitle(String title, String... pages) {

        this.item.setType(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) this.meta;

        bookMeta.setTitle(title);

        List<Component> pagesList = new ArrayList<>();

        for (var page : pages) pagesList.add(Component.text(page));
        bookMeta.pages(pagesList);

        return this;
    }

    /**
     * Sets the item as a compass pointing to the specified location.
     *
     * @param location The location to point to.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setCompass(Location location) {
        this.item.setType(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) this.meta;
        compassMeta.setLodestone(location);
        compassMeta.setLodestoneTracked(true);
        return this;
    }

    /**
     * Sets the item as a crossbow loaded with arrows.
     *
     * @param amount The amount of arrows to load.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setLoadedCrossbow(int amount) {
        this.item.setType(Material.CROSSBOW);
        CrossbowMeta crossbowMeta = (CrossbowMeta) this.meta;
        for (int i = 0; i < amount; i++) {
            crossbowMeta.addChargedProjectile(new ItemStack(Material.ARROW));
        }
        return this;
    }

    /**
     * Sets the item as a tropical fish bucket with the specified fish pattern and body color.
     *
     * @param pattern    The pattern of the fish.
     * @param bodyColor  The body color of the fish.
     * @param patternColor The pattern color of the fish.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setTropicalFishBucket(TropicalFish.Pattern pattern, DyeColor bodyColor, DyeColor patternColor) {
        this.item.setType(Material.TROPICAL_FISH_BUCKET);
        TropicalFishBucketMeta bucketMeta = (TropicalFishBucketMeta) this.meta;
        bucketMeta.setPattern(pattern);
        bucketMeta.setBodyColor(bodyColor);
        bucketMeta.setPatternColor(patternColor);
        return this;
    }


    /**
     * Adds item flags to the item.
     *
     * @param flags The item flags to add.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder addItemFlags(ItemFlag... flags) {

        this.meta.addItemFlags(flags);
        return this;

    }

    /**
     * Sets the item as a suspicious stew with the specified effect and custom name.
     *
     * @param effect     The effect of the suspicious stew.
     * @param customName The custom name of the stew.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setSuspiciousStew(PotionEffectType effect, String customName) {
        this.item.setType(Material.SUSPICIOUS_STEW);
        SuspiciousStewMeta stewMeta = (SuspiciousStewMeta) this.meta;
        stewMeta.addCustomEffect(new PotionEffect(effect, 200, 1), true);
        stewMeta.displayName(Component.text(customName));
        return this;
    }

    /**
     * Sets the item as a firework star with the specified effect.
     *
     * @param effect The firework effect to apply to the star.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setFireworkStar(FireworkEffect effect) {
        this.item.setType(Material.FIREWORK_STAR);
        FireworkMeta fireworkMeta = (FireworkMeta) this.meta;
        fireworkMeta.addEffect(effect);
        return this;
    }

    /**
     * Applies a glow effect to the item.
     *
     * @return The ItemBuilder instance.
     */
    public ItemBuilder glow(){

        if (this.item.getType().equals(Material.BOW)) {

            this.meta.addEnchant(Enchantment.THORNS, 1, true);

        } else {

            this.meta.addEnchant(Enchantment.INFINITY, 1, true);

        }

        this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;

    }

    /**
     * Applies a glow effect to the item.
     *
     * @return The ItemBuilder instance.
     */
    public ItemBuilder glow(boolean glow){

        if (glow) {
            if (this.item.getType().equals(Material.BOW)) {

                this.meta.addEnchant(Enchantment.THORNS, 1, true);

            } else {

                this.meta.addEnchant(Enchantment.INFINITY, 1, true);

            }

            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return this;

    }

    /**
     * Sets the amount of the item.
     *
     * @param amount The amount to set.
     * @return The ItemBuilder instance.
     */
    public ItemBuilder setAmount(int amount) {

        this.item.setAmount(amount);
        return this;

    }

    /**
     * @deprecated
     * Sets the durability of the item.
     *
     * @param durability The durability to set.
     * @return The ItemBuilder instance.
     */
    @Deprecated
    public ItemBuilder setDurability(short durability) {

        this.item.setDurability(durability);
        return this;

    }

    /**
     * Clears the lore of the item.
     *
     * @return The ItemBuilder instance.
     */
    public ItemBuilder clearLore() {
        this.meta.lore(new ArrayList<>());
        return this;
    }

    /**
     * Builds the final ItemStack.
     *
     * @return The built ItemStack.
     */
    public ItemStack build(){

        this.item.setItemMeta(this.meta);
        return this.item;

    }

}
