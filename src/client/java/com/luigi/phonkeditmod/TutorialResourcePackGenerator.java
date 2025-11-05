package com.luigi.phonkeditmod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Gera automaticamente um resource pack tutorial para o usuário.
 * 
 * Cria o pack em: .minecraft/resourcepacks/PhonkEdit-CustomSongs/
 * 
 * O pack contém:
 * - Estrutura de pastas correta
 * - pack.mcmeta
 * - sounds.json de exemplo
 * - HOW_TO.txt com instruções
 * 
 * E ATIVA AUTOMATICAMENTE o pack!
 */
public class TutorialResourcePackGenerator {
	
	private static final String PACK_NAME = "PhonkEdit-CustomSongs";
	
	/**
	 * Gera o tutorial pack se não existir
	 */
	public static void generateIfNeeded() {
		try {
			// Caminho para resourcepacks: .minecraft/resourcepacks/
			Path minecraftDir = Paths.get(System.getProperty("user.dir"));
			Path resourcepacksDir = minecraftDir.resolve("resourcepacks");
			Path tutorialPackDir = resourcepacksDir.resolve(PACK_NAME);
			
			// Se já existe, não faz nada
			if (Files.exists(tutorialPackDir)) {
				System.out.println("[Phonk Edit Mod] Tutorial pack já existe: " + tutorialPackDir);
				return;
			}
			
			System.out.println("[Phonk Edit Mod] Gerando tutorial pack em: " + tutorialPackDir);
			
			// Cria estrutura de pastas
			Path assetsDir = tutorialPackDir.resolve("assets/phonk-edit-mod/sounds/custom");
			Files.createDirectories(assetsDir);
			
			// Gera pack.mcmeta
			generatePackMcmeta(tutorialPackDir);
			
			// Gera sounds.json
			generateSoundsJson(tutorialPackDir);
			
			// Gera HOW_TO.txt
			generateHowTo(tutorialPackDir);
			
			// Gera PLACE_OGG_FILES_HERE.txt na pasta custom
			generatePlaceholder(assetsDir);
			
			// NOTA: Removemos a cópia do áudio de exemplo, pois o pack já está ativado automaticamente!
			// O usuário só precisa adicionar seus arquivos OGG e dar reload (F3+T)
			
			System.out.println("[Phonk Edit Mod] Tutorial pack gerado com sucesso!");
			System.out.println("[Phonk Edit Mod] Localização: " + tutorialPackDir);
			System.out.println("[Phonk Edit Mod] O pack será ativado automaticamente no primeiro tick do jogo!");
			System.out.println("[Phonk Edit Mod] Adicione seus arquivos OGG na pasta 'custom/' e pressione F3+T para recarregar!");
			
		} catch (IOException e) {
			System.err.println("[Phonk Edit Mod] Erro ao gerar tutorial pack: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Ativa automaticamente o resource pack nas opções do jogo.
	 * Isso elimina a necessidade do usuário ir em Options → Resource Packs → Enable.
	 */
	public static void enablePackAutomatically() {
		try {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client == null) {
				System.err.println("[Phonk Edit Mod] MinecraftClient não disponível ainda, não é possível ativar pack");
				return;
			}
			
			ResourcePackManager packManager = client.getResourcePackManager();
			
			// Nome do pack no formato que o Minecraft espera: "file/PhonkEdit-CustomSongs"
			String packProfileName = "file/" + PACK_NAME;
			
			// Pega a lista atual de packs habilitados
			List<String> enabledPacks = new ArrayList<>(packManager.getEnabledIds());
			
			// Se o pack já está habilitado, não faz nada
			if (enabledPacks.contains(packProfileName)) {
				System.out.println("[Phonk Edit Mod] Pack já está habilitado: " + packProfileName);
				return;
			}
			
			// Adiciona o pack à lista de habilitados
			enabledPacks.add(packProfileName);
			
			// Aplica a nova lista
			packManager.setEnabledProfiles(enabledPacks);
			
			System.out.println("[Phonk Edit Mod] Pack habilitado automaticamente: " + packProfileName);
			System.out.println("[Phonk Edit Mod] O pack estará ativo no próximo reload de recursos!");
			
		} catch (Exception e) {
			System.err.println("[Phonk Edit Mod] Erro ao ativar pack automaticamente: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void generatePackMcmeta(Path packDir) throws IOException {
		String content = "{\n" +
				"  \"pack\": {\n" +
				"    \"pack_format\": 34,\n" +
				"    \"description\": \"§6Tutorial: Custom Phonk Songs\\n§7Add your own phonk tracks here!\"\n" +
				"  }\n" +
				"}\n";
		
		Path file = packDir.resolve("pack.mcmeta");
		Files.writeString(file, content, StandardOpenOption.CREATE);
	}
	
	private static void generateSoundsJson(Path packDir) throws IOException {
		// Gera um JSON VAZIO para evitar tocar sons inexistentes
		// O usuário deve adicionar suas próprias entradas
		String content = "{\n}\n";
		
		Path file = packDir.resolve("assets/phonk-edit-mod/sounds.json");
		Files.writeString(file, content, StandardOpenOption.CREATE);
		
		// Cria um arquivo README ao lado do sounds.json com instruções
		String readmeContent = 
				"=============================================================================\n" +
				"  HOW TO ADD CUSTOM PHONK SONGS - sounds.json\n" +
				"=============================================================================\n" +
				"\n" +
				"1. Place your .ogg audio files in: sounds/custom/\n" +
				"   Example: sounds/custom/test_phonk.ogg\n" +
				"\n" +
				"2. Add entries to sounds.json following this format:\n" +
				"\n" +
				"   {\n" +
				"     \"custom/test_phonk\": {\n" +
				"       \"sounds\": [ \"phonk-edit-mod:custom/test_phonk\" ],\n" +
				"       \"subtitle\": \"Test Phonk\"\n" +
				"     },\n" +
				"     \"custom/another_song\": {\n" +
				"       \"sounds\": [ \"phonk-edit-mod:custom/another_song\" ]\n" +
				"     }\n" +
				"   }\n" +
				"\n" +
				"3. IMPORTANT:\n" +
				"   - Keys MUST start with \"custom/\"\n" +
				"   - File name must match (without .ogg extension)\n" +
				"   - Don't forget commas between entries!\n" +
				"   - Last entry should NOT have a comma\n" +
				"\n" +
				"4. This pack is ALREADY ENABLED!\n" +
				"   Just press F3+T to reload resources\n" +
				"\n" +
				"5. In-game: Press 'O' -> Set Audio Mode to 'Mix' or 'Custom Only'\n" +
				"\n" +
				"=============================================================================\n";
		
		Path readmeFile = packDir.resolve("assets/phonk-edit-mod/SOUNDS_JSON_GUIDE.txt");
		Files.writeString(readmeFile, readmeContent, StandardOpenOption.CREATE);
	}
	
	private static void generateHowTo(Path packDir) throws IOException {
		String content = 
				"==============================================================================\n" +
				"  PHONK EDIT MOD - CUSTOM SONGS TUTORIAL\n" +
				"==============================================================================\n" +
				"\n" +
				"Welcome! This resource pack lets you add your own phonk songs to the mod.\n" +
				"\n" +
				"--- QUICK START (5 minutes) ---\n" +
				"\n" +
				"1. CONVERT YOUR SONGS TO OGG\n" +
				"   - Use Audacity: File -> Export -> OGG Vorbis\n" +
				"   - Or online: https://convertio.co/mp3-ogg/\n" +
				"\n" +
				"2. ADD OGG FILES\n" +
				"   - Place your .ogg files in:\n" +
				"     assets/phonk-edit-mod/sounds/custom/\n" +
				"   - Example: custom/test_phonk.ogg\n" +
				"\n" +
				"3. REGISTER IN SOUNDS.JSON\n" +
				"   - Open: assets/phonk-edit-mod/sounds.json\n" +
				"   - Add entry:\n" +
				"     {\n" +
				"       \"custom/test_phonk\": {\n" +
				"         \"sounds\": [ \"phonk-edit-mod:custom/test_phonk\" ]\n" +
				"       }\n" +
				"     }\n" +
				"   - IMPORTANT: Key must start with \"custom/\"\n" +
				"   - IMPORTANT: Name must match file (without .ogg)\n" +
				"\n" +
				"4. RELOAD RESOURCES\n" +
				"   - This pack is ALREADY ENABLED automatically!\n" +
				"   - Just press F3+T to reload resources\n" +
				"   - Or press 'O' -> Click \"Reload Custom Files\"\n" +
				"\n" +
				"5. CONFIGURE MOD\n" +
				"   - Enter a world\n" +
				"   - Press 'O' key (opens config menu)\n" +
				"   - Set \"Audio Mode\" to \"Mix\" or \"Custom Only\"\n" +
				"\n" +
				"6. ENJOY!\n" +
				"   - Play normally and wait for the effect\n" +
				"   - Your custom phonk will play!\n" +
				"\n" +
				"--- AUDIO MODES ---\n" +
				"\n" +
				"- Mod Only: Uses only 9 original phonk tracks (default)\n" +
				"- Mix: Random between original and custom tracks (RECOMMENDED)\n" +
				"- Custom Only: Uses only your custom tracks\n" +
				"\n" +
				"--- FILE REQUIREMENTS ---\n" +
				"\n" +
				"Format: OGG Vorbis (NOT MP3, WAV, FLAC)\n" +
				"Sample Rate: 44100 Hz recommended\n" +
				"Bitrate: 128-192 kbps\n" +
				"Duration: Any (5-30 seconds recommended for phonk)\n" +
				"\n" +
				"--- TROUBLESHOOTING ---\n" +
				"\n" +
				"Q: \"No custom sounds detected\"\n" +
				"A: 1. Pack is auto-enabled, but verify in Options -> Resource Packs\n" +
				"   2. Verify sounds.json has entries starting with \"custom/\"\n" +
				"   3. Press F3+T or click \"Reload Custom Files\" (press O)\n" +
				"   4. Check logs: .minecraft/logs/latest.log\n" +
				"\n" +
				"Q: \"Sound doesn't play\"\n" +
				"A: 1. Verify .ogg file exists in custom/ folder\n" +
				"   2. Check filename matches sounds.json exactly\n" +
				"   3. Ensure file is valid OGG (test in VLC)\n" +
				"   4. Set Audio Mode to \"Custom Only\" to test\n" +
				"\n" +
				"Q: \"JSON syntax error\"\n" +
				"A: 1. Validate at: https://jsonlint.com/\n" +
				"   2. Check all quotes are \" (not ' or smart quotes)\n" +
				"   3. Verify commas between entries (not after last)\n" +
				"\n" +
				"--- EXAMPLE ---\n" +
				"\n" +
				"Let's add \"Shadowboxing - DVRST\":\n" +
				"\n" +
				"1. Download from YouTube -> Convert to OGG\n" +
				"2. Rename to: shadowboxing.ogg\n" +
				"3. Move to: assets/phonk-edit-mod/sounds/custom/\n" +
				"4. Edit sounds.json:\n" +
				"   {\n" +
				"     \"custom/shadowboxing\": {\n" +
				"       \"sounds\": [ \"phonk-edit-mod:custom/shadowboxing\" ]\n" +
				"     }\n" +
				"   }\n" +
				"5. Enable pack in game\n" +
				"6. O -> Reload Custom Files\n" +
				"7. Set Audio Mode: Mix\n" +
				"8. Play and enjoy!\n" +
				"\n" +
				"==============================================================================\n";
		
		Path file = packDir.resolve("HOW_TO.txt");
		Files.writeString(file, content, StandardOpenOption.CREATE);
	}
	
	private static void generatePlaceholder(Path customDir) throws IOException {
		String content = 
				"==============================================================================\n" +
				"  PLACE YOUR .OGG FILES HERE!\n" +
				"==============================================================================\n" +
				"\n" +
				"This folder is where you put your custom phonk songs.\n" +
				"\n" +
				"STEPS:\n" +
				"\n" +
				"1. Convert your songs to OGG Vorbis format\n" +
				"   - Use Audacity or online converters\n" +
				"\n" +
				"2. Place .ogg files in THIS folder\n" +
				"   - Example: test_phonk.ogg\n" +
				"\n" +
				"3. Register in ../sounds.json\n" +
				"   - Add entry: \"custom/test_phonk\": {...}\n" +
				"\n" +
				"4. Press F3+T to reload resources\n" +
				"   - Pack is already enabled automatically!\n" +
				"\n" +
				"5. In-game: Press O -> Set Audio Mode to Mix/Custom Only\n" +
				"\n" +
				"See HOW_TO.txt in the pack root for full instructions!\n" +
				"\n" +
				"==============================================================================\n";
		
		Path file = customDir.resolve("PLACE_OGG_FILES_HERE.txt");
		Files.writeString(file, content, StandardOpenOption.CREATE);
	}
}
