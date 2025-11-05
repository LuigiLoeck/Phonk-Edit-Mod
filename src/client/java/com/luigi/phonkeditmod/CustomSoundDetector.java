package com.luigi.phonkeditmod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Detecta sons customizados em resource packs ativos.
 * 
 * Funciona escaneando todos os resource packs carregados procurando por:
 * - assets/phonk-edit-mod/sounds.json
 * - Chaves que começam com "custom/"
 * 
 * Exemplo de sounds.json customizado:
 * {
 *   "custom/meu_phonk": {
 *     "sounds": [ "phonk-edit-mod:custom/meu_phonk" ]
 *   }
 * }
 */
public class CustomSoundDetector {
	
	private static final List<SoundEvent> customSounds = new ArrayList<>();
	private static final List<String> customSoundNames = new ArrayList<>();
	private static int previousSoundCount = 0; // Para detectar novos sons
	
	/**
	 * Escaneia resource packs ativos procurando por sons customizados
	 * @return true se encontrou novos sons, false caso contrário
	 */
	public static boolean detectCustomSounds() {
		int oldCount = previousSoundCount; // Usa o contador anterior (entre chamadas)
		customSounds.clear();
		customSoundNames.clear();
		
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) return false;
		
		ResourceManager resourceManager = client.getResourceManager();
		
		try {
			// Tenta encontrar sounds.json do nosso mod em todos os resource packs
			Identifier soundsJsonId = Identifier.of("phonk-edit-mod", "sounds.json");
			Optional<Resource> resourceOpt = resourceManager.getResource(soundsJsonId);
			
			if (resourceOpt.isEmpty()) {
				System.out.println("[Phonk Edit Mod] Nenhum sounds.json encontrado");
				previousSoundCount = 0;
				return false;
			}
			
			Resource resource = resourceOpt.get();
			
			// Parse do JSON
			JsonObject soundsJson = JsonParser.parseReader(
				new InputStreamReader(resource.getInputStream())
			).getAsJsonObject();
			
			// Procura por chaves que começam com "custom/"
			for (String key : soundsJson.keySet()) {
				if (key.startsWith("custom/")) {
					// Encontrou um som customizado!
					Identifier soundId = Identifier.of("phonk-edit-mod", key);
					SoundEvent soundEvent = SoundEvent.of(soundId);
					
					customSounds.add(soundEvent);
					customSoundNames.add(key);
					
					System.out.println("[Phonk Edit Mod] Detectado som customizado via resource pack: " + key);
				}
			}
			
			if (!customSounds.isEmpty()) {
				System.out.println("[Phonk Edit Mod] Total de sons customizados detectados: " + customSounds.size());
			} else {
				System.out.println("[Phonk Edit Mod] Nenhum som customizado encontrado (procure por chaves 'custom/*' no sounds.json)");
			}
			
			// Verifica se há novos sons (mais do que antes)
			boolean foundNewSounds = customSounds.size() > oldCount && oldCount >= 0;
			System.out.println("[Phonk Edit Mod] DEBUG - oldCount: " + oldCount + ", newCount: " + customSounds.size() + ", foundNewSounds: " + foundNewSounds);
			previousSoundCount = customSounds.size();
			return foundNewSounds;
			
		} catch (Exception e) {
			System.err.println("[Phonk Edit Mod] Erro ao detectar sons customizados: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Retorna lista de SoundEvents customizados detectados
	 */
	public static List<SoundEvent> getCustomSounds() {
		return new ArrayList<>(customSounds);
	}
	
	/**
	 * Retorna lista de nomes dos sons customizados
	 */
	public static List<String> getCustomSoundNames() {
		return new ArrayList<>(customSoundNames);
	}
	
	/**
	 * Verifica se há sons customizados disponíveis
	 */
	public static boolean hasCustomSounds() {
		return !customSounds.isEmpty();
	}
	
	/**
	 * Retorna quantidade de sons customizados
	 */
	public static int getCustomSoundCount() {
		return customSounds.size();
	}
}
