package com.luigi.phonkeditmod.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker; // <-- Importante: é o @Invoker

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    // "Invoker" nos permite "chamar" um método privado
    @Invoker("loadPostProcessor")
    void callLoadPostProcessor(Identifier id);

    // Também vamos pegar o método de desativar, por garantia
    // (Embora disablePostProcessor() seja público, é bom saber fazer)
    @Invoker("disablePostProcessor")
    void callDisablePostProcessor();
}