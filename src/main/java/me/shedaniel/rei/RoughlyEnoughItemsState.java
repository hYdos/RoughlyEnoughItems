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

package me.shedaniel.rei;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ApiStatus.Internal
public class RoughlyEnoughItemsState {
    private RoughlyEnoughItemsState() {}
    
    public static final Logger LOGGER = LogManager.getFormatterLogger("REI");
    
    private static List<Pair<String, String>> errors = new ArrayList<>();
    private static List<Pair<String, String>> warnings = new ArrayList<>();
    private static Set<String> errorSet = new LinkedHashSet<>();
    private static Set<String> warningSet = new LinkedHashSet<>();
    private static List<Runnable> continueCallbacks = new ArrayList<>();
    
    public static void error(String reason) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || FabricLoader.getInstance().isDevelopmentEnvironment())
            throw new RuntimeException(reason);
        if (RoughlyEnoughItemsState.errorSet.add(reason + " " + null)) {
            RoughlyEnoughItemsState.errors.add(new Pair<>(reason, null));
            LOGGER.error(reason);
        }
    }
    
    public static void error(String reason, String link) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || FabricLoader.getInstance().isDevelopmentEnvironment())
            throw new RuntimeException(reason + " " + link);
        if (RoughlyEnoughItemsState.errorSet.add(reason + " " + link)) {
            RoughlyEnoughItemsState.errors.add(new Pair<>(reason, link));
            LOGGER.error(reason + " " + link);
        }
    }
    
    public static void warn(String reason) {
        if (RoughlyEnoughItemsState.warningSet.add(reason + " " + null)) {
            RoughlyEnoughItemsState.warnings.add(new Pair<>(reason, null));
            LOGGER.warn(reason);
        }
    }
    
    public static void warn(String reason, String link) {
        if (RoughlyEnoughItemsState.warningSet.add(reason + " " + link)) {
            RoughlyEnoughItemsState.warnings.add(new Pair<>(reason, link));
            LOGGER.warn(reason + " " + link);
        }
    }
    
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static void onContinue(Runnable runnable) {
        continueCallbacks.add(runnable);
    }
    
    public static List<Pair<String, String>> getErrors() {
        return errors;
    }
    
    public static List<Pair<String, String>> getWarnings() {
        return warnings;
    }
    
    public static void clear() {
        errors.clear();
        errorSet.clear();
        warnings.clear();
        warningSet.clear();
    }
    
    public static void continues() {
        for (Runnable callback : continueCallbacks) {
            try {
                callback.run();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        continueCallbacks.clear();
    }
}
