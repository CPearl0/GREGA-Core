package io.github.cpearl0.grega.core;

import com.gregtechceu.gtceu.api.fluid.FluidBuilder;
import com.gregtechceu.gtceu.api.fluid.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.material.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.material.material.properties.DustProperty;
import com.gregtechceu.gtceu.api.material.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.material.material.properties.IngotProperty;
import com.gregtechceu.gtceu.api.material.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import java.util.List;

import static com.gregtechceu.gtceu.data.material.GTMaterials.*;

/** Complete GT's existing elemental materials before their fluids and items are generated. */
public final class PeriodicTableMaterials {
    private static boolean modified;

    private PeriodicTableMaterials() {}

    public static void modify(PostMaterialEvent event) {
        if (modified) return;
        modified = true;

        for (var material : List.of(
                Scandium, Zirconium, Technetium, Praseodymium, Promethium,
                Gadolinium, Terbium, Dysprosium, Holmium, Erbium, Thulium, Ytterbium,
                Hafnium, Rhenium, Thallium, Polonium, Actinium, Protactinium,
                Neptunium, Curium, Berkelium, Californium, Einsteinium, Fermium,
                Mendelevium, Nobelium, Lawrencium, Rutherfordium, Dubnium,
                Seaborgium, Bohrium, Hassium, Meitnerium, Roentgenium,
                Nihonium, Moscovium, Livermorium)) {
            if (!material.hasProperty(PropertyKey.INGOT) && !material.hasProperty(PropertyKey.DUST)) {
                material.setProperty(PropertyKey.INGOT, new IngotProperty());
            }
        }

        // Follow GT's powder forms for alkali/alkaline-earth metals and brittle nonmetals.
        // The bulk states of Cn, Fl and Og are predictions: Cn/Fl are liquid, Og is solid.
        for (var material : List.of(
                Germanium, Selenium, Rubidium, Strontium, Tellurium,
                Astatine, Francium, Radium, Tennessine, Oganesson)) {
            if (!material.hasProperty(PropertyKey.DUST)) {
                material.setProperty(PropertyKey.DUST, new DustProperty());
            }
        }

        for (var material : GTRegistries.MATERIALS) {
            var element = material.getElement();
            if (element == null || element.protons() < 1 || element.protons() > 118
                    || material.hasProperty(PropertyKey.FLUID)) continue;
            var builder = new FluidBuilder();
            if (material == Oganesson) builder.temperature(325);
            material.setProperty(PropertyKey.FLUID, new FluidProperty(FluidStorageKeys.LIQUID, builder));
        }
    }
}
