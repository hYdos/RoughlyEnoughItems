/*
 * This file is licensed under the MIT License, part of Roughly Enough Items.
 * Copyright (c) 2018, 2019, 2020 shedaniel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.shedaniel.rei.plugin.fuel;

import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.QueuedTooltip;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.ScreenHelper;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class DefaultFuelCategory implements RecipeCategory<DefaultFuelDisplay> {
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    
    @Override
    public Identifier getIdentifier() {
        return DefaultPlugin.FUEL;
    }
    
    @Override
    public String getCategoryName() {
        return I18n.translate("category.rei.fuel");
    }
    
    @Override
    public int getDisplayHeight() {
        return 49;
    }
    
    @Override
    public EntryStack getLogo() {
        return EntryStack.create(Items.COAL);
    }
    
    @Override
    public List<Widget> setupDisplay(Supplier<DefaultFuelDisplay> recipeDisplaySupplier, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 17);
        String burnItems = DECIMAL_FORMAT.format(recipeDisplaySupplier.get().getFuelTime() / 200d);
        List<Widget> widgets = new LinkedList<>(Collections.singletonList(new RecipeBaseWidget(bounds) {
            @Override
            public void render(int mouseX, int mouseY, float delta) {
                super.render(mouseX, mouseY, delta);
                MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultPlugin.getDisplayTexture());
                blit(bounds.x + 5, startPoint.y, 0, 73, 18, 34);
                int height = MathHelper.ceil(System.currentTimeMillis() / 250d % 14d);
                blit(bounds.x + 7, startPoint.y + 12 + (3 - height), 82, 77 + (14 - height), 14, height);
                minecraft.textRenderer.draw(I18n.translate("category.rei.fuel.time.items", burnItems), bounds.x + 26, bounds.getMaxY() - 15, ScreenHelper.isDarkModeEnabled() ? 0xFFBBBBBB : 0xFF404040);
            }
        }));
        widgets.add(EntryWidget.create(bounds.x + 6, startPoint.y + 18).entries(recipeDisplaySupplier.get().getInputEntries().get(0)).markIsInput());
        return widgets;
    }
    
    @Override
    public RecipeEntry getSimpleRenderer(DefaultFuelDisplay recipe) {
        EntryWidget widget = EntryWidget.create(0, 0).entries(recipe.getInputEntries().get(0)).noBackground().noHighlight();
        String burnItems = DECIMAL_FORMAT.format(recipe.getFuelTime() / 200d);
        return new RecipeEntry() {
            @Override
            public int getHeight() {
                return 22;
            }
            
            @Nullable
            @Override
            public QueuedTooltip getTooltip(int mouseX, int mouseY) {
                if (widget.containsMouse(mouseX, mouseY))
                    return widget.getCurrentTooltip(mouseX, mouseY);
                return null;
            }
            
            @Override
            public void render(Rectangle bounds, int mouseX, int mouseY, float delta) {
                widget.setZ(getZ() + 50);
                widget.getBounds().setLocation(bounds.x + 4, bounds.y + 2);
                widget.render(mouseX, mouseY, delta);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(I18n.translate("category.rei.fuel.time_short.items", burnItems), bounds.x + 25, bounds.y + 8, -1);
            }
        };
    }
}
